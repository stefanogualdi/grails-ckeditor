package org.gualdi.grails.plugins.ckeditor

import org.apache.log4j.Logger
import org.gualdi.grails.plugins.ckeditor.utils.PluginUtils
import org.gualdi.grails.plugins.ckeditor.utils.PathUtils
import org.gualdi.grails.plugins.ckeditor.exceptions.UnknownOptionException
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.apache.commons.lang.StringUtils

/**
 * @author Stefano Gualdi <stefano.gualdi@gmail.com>
 */

class CkeditorConfig {
	
	private final Logger log = Logger.getLogger(getClass())

    static final REQUEST_CONFIG = "ckeditor.plugin.config"
	
	static final PLUGIN_NAME = "ckeditor"

	static final DEFAULT_BASEDIR = "/uploads/"

	static final DEFAULT_USERSPACE = ""
	static final DEFAULT_INSTANCENAME = "editor"

	static final RESOURCE_TYPES = ['link', 'image', 'flash']

    def contextPath
    def basePath

    def instanceName
    def userSpace
	def append

	def type
	def target
	
	def config
	def localConfig

    CkeditorConfig(request, attrs = null) {
		this.contextPath = request.contextPath
		this.basePath = PluginUtils.getPluginResourcePath(this.contextPath, this.PLUGIN_NAME)

		this.localConfig = [:]
		
		createOrRetrieveConfig(request)
		
		if (attrs) {
	        this.instanceName = attrs.remove("name") ?: this.DEFAULT_INSTANCENAME
			this.userSpace = attrs.remove("userSpace") ?: this.DEFAULT_USERSPACE  
			this.append = (attrs.remove("append") == "true")

			this.type = attrs.remove("type")
			this.target = attrs.remove("target")

			addConfigItem(attrs, true)			
		}
    }

	private createOrRetrieveConfig(request) {
        if (!request[REQUEST_CONFIG]) {
            request[REQUEST_CONFIG] = [:]
        }
        this.config = request[REQUEST_CONFIG]
	}

    def addConfigItem(attrs, local = false) {
        attrs?.each { key, value ->
            if (key in ALLOWED_CONFIG_ITEMS || key.startsWith('filebrowser')) {
				def tmp = value?.trim()
				if (!tmp?.isNumber() && 
					!tmp?.equalsIgnoreCase('true') && 
					!tmp?.equalsIgnoreCase('false') && 
					!tmp?.startsWith('CKEDITOR.')) {
					tmp = "'${tmp}'"
				}
				if (local) {
					this.localConfig[key] = tmp
				}
				else {
                	this.config[key] = tmp
				}
            }
            else {
                throw new UnknownOptionException("Unknown option: ${key}. Option names are case sensitive! Check the spelling.")
            }
        }
    }

	def addComplexConfigItem(var, value) {
        if (var in ALLOWED_CONFIG_ITEMS || var.startsWith('toolbar_')) {
			this.config[var] = value
		}
		else {
		    throw new UnknownOptionException("Unknown option: ${var}. Option names are case sensitive! Check the spelling.")
        }
    }

	def getBrowseUrl(type, userSpace) {
		return "${this.basePath}/js/filebrowser/browser.html?Connector=${this.contextPath}/ckconnector?Type=${type}${userSpace ? '&userSpace='+ userSpace : ''}"	
	}
	
	def getUploadUrl(type, userSpace) {
		return "${this.contextPath}/ckuploader?Type=${type}${userSpace ? '&userSpace='+ userSpace : ''}" 
	}

    def getConfiguration() {
		def ckconfig = ConfigurationHolder.config.ckeditor

		def customConfig = ckconfig?.config 
		if (customConfig && !this.config["customConfig"]) {
			customConfig = PathUtils.checkSlashes(customConfig, "L- R-", true)
			this.config["customConfig"] = "'${this.contextPath}/${customConfig}'"	
		}

		// Collect browser settings per media type
        this.resourceTypes.each { t ->
            def type = StringUtils.capitalize(t)
			def typeForConnector = "${type == 'Link' ? 'File' : type}"
			
            if (ckconfig?.upload?."${t}"?.browser) {
				this.config["filebrowser${type}BrowseUrl"] = "'${getBrowseUrl(typeForConnector, this.userSpace)}'" 
            }
            if (ckconfig?.upload?."${t}"?.upload) {
				this.config["filebrowser${type}UploadUrl"] = "'${getUploadUrl(typeForConnector, this.userSpace)}'" 
            }
        }

		// Config options
		StringBuffer configs = new StringBuffer()
		this.config.each {k, v ->
			if (!localConfig[k]) {
				configs << "${k}: ${v},\n"
			}
		}
		this.localConfig.each {k, v ->
			configs << "${k}: ${v},\n"
		}

		StringBuffer configuration = new StringBuffer()
        if (configs.size()) {
            configuration << """, {\n"""
           	configuration << configs
            configuration << """}\n"""
        }
	
        return configuration
    }

    static getResourceTypes() {
        return RESOURCE_TYPES
    }

    // See: http://docs.cksource.com/ckeditor_api/symbols/CKEDITOR.config.html
    static final ALLOWED_CONFIG_ITEMS = [
		// Main config
		'customConfig',
		'autoUpdateElement',
		'baseHref',
		'contentsCss',
		'contentsLangDirection',
		'language',
		'defaultLanguage',
		'enterMode',
		'shiftEnterMode',
		'corePlugins',
		'docType',
		'fullPage',
		'height',
		'plugins',
		'extraPlugins',
		'removePlugins',
		'protectedSource',
		'tabIndex',
		'theme',
		'skin',
		'width',
		'baseFloatZIndex',

		// plugins/colorbutton/plugin.js
		'colorButton_enableMore',
		'colorButton_colors',
		'colorButton_foreStyle',
		'colorButton_backStyle',

		// plugins/keystrokes/plugin.js
		'blockedKeystrokes',
		'keystrokes',

		// plugins/dialog/plugin.js
		'dialog_backgroundCoverColor',
		'dialog_backgroundCoverOpacity',
		'dialog_magnetDistance',

		// plugins/wysiwygarea/plugin.js
		'disableObjectResizing',
		'disableNativeTableHandles',
		'disableNativeSpellChecker',
		'ignoreEmptyParagraph',

		// plugins/editingblock/plugin.js
		'startupMode',
		'startupFocus',
		'editingBlock',

		// plugins/entities/plugin.js
		'entities',
		'entities_latin',
		'entities_greek',
		'entities_processNumerical',
		'entities_additional',

		// plugins/find/plugin.js 
		'find_highlight',

		// plugins/font/plugin.js
		'font_names',
		'font_defaultLabel',
		'font_style',
		'fontSize_sizes',
		'fontSize_defaultLabel',
		'fontSize_style',

		// plugins/pastetext/plugin.js
		'forcePasteAsPlainText',

		// plugins/htmldataprocessor/plugin.js
		'forceSimpleAmpersand',

		// plugins/format/plugin.js
		'format_tags',
		'format_p',
		'format_div',
		'format_pre',
		'format_address',
		'format_h1',
		'format_h2',
		'format_h3',
		'format_h4',
		'format_h5',
		'format_h6',

		// plugins/image/plugin.js
		'image_removeLinkByEmptyURL',

		// plugins/menu/plugin.js
		'menu_subMenuDelay',
		'menu_groups',

		// plugins/newpage/plugin.js 
		'newpage_html',

		// plugins/pastefromword/plugin.js
		'pasteFromWordIgnoreFontFace',
		'pasteFromWordRemoveStyle',
		'pasteFromWordKeepsStructure',

		// plugins/removeformat/plugin.js 
		'removeFormatTags',
		'removeFormatAttributes',

		// plugins/resize/plugin.js
		'resize_minWidth',
		'resize_minHeight',
		'resize_maxWidth',
		'resize_maxHeight',
		'resize_enabled',

		// plugins/smiley/plugin.js
		'smiley_path',
		'smiley_images',
		'smiley_descriptions',

		// plugins/showblocks/plugin.js
		'startupOutlineBlocks',

		// plugins/stylescombo/plugin.js
		'stylesCombo_stylesSet',

		// plugins/tab/plugin.js
		'tabSpaces',

		//  plugins/templates/plugin.js
		'templates',
		'templates_files',
		'templates_replaceContent',

		// plugins/toolbar/plugin.js
		'toolbarLocation',
		'toolbar_Basic',
		'toolbar_Full',
		'toolbar',
		'toolbarCanCollapse',
		'toolbarStartupExpanded',

		// plugins/undo/plugin.js
		'undoStackSize'
    ]
}