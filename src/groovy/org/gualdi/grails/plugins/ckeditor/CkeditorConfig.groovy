/*
 * Copyright 2010 Stefano Gualdi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.gualdi.grails.plugins.ckeditor

import org.apache.log4j.Logger
import org.gualdi.grails.plugins.ckeditor.utils.PluginUtils
import org.gualdi.grails.plugins.ckeditor.utils.PathUtils
import org.gualdi.grails.plugins.ckeditor.exceptions.UnknownOptionException
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.apache.commons.lang.WordUtils

/**
 * @author Stefano Gualdi <stefano.gualdi@gmail.com>
 */

class CkeditorConfig {
	
	private final Logger log = Logger.getLogger(getClass())

    static final REQUEST_CONFIG = "ckeditor.plugin.config"

	static final PLUGIN_NAME = "ckeditor"

    static final DEFAULT_CONNECTORS_PREFIX = "ck"

	static final DEFAULT_BASEDIR = "/uploads/"

	static final DEFAULT_USERSPACE = ""
    
	static final DEFAULT_INSTANCENAME = "editor"

	static final DEFAULT_FILEBROWSER = "standard" // standard | ofm

    static final DEFAULT_SHOWTHUMBS = false

	static final RESOURCE_TYPES = ['link', 'image', 'flash']

    static final OFM_IMAGE_EXTS = ['jpg', 'jpeg', 'gif', 'png']

    static final OFM_LOCALES = ['ca', 'cs', 'da', 'de', 'en', 'es', 'fr', 'it', 'nl', 'pl', 'zh-cn']

    def contextPath
    def basePath

    def connectorsPrefix

    def skipAllowedItemsCheck

    def instanceName
    def userSpace
	def append

    def fileBrowser
    def defaultFileBrowser
    def showThumbs

	def type
	def target
	
	def config
	def localConfig

    CkeditorConfig(request, attrs = null) {
        def cfg = ConfigurationHolder.config

		this.contextPath = request.contextPath
		this.basePath = PluginUtils.getPluginResourcePath(this.contextPath, this.PLUGIN_NAME)

        this.connectorsPrefix = getConnectorsPrefix()

        this.defaultFileBrowser = cfg.ckeditor?.defaultFileBrowser ?: this.DEFAULT_FILEBROWSER

        this.skipAllowedItemsCheck = cfg.ckeditor?.skipAllowedItemsCheck ?: false

		this.localConfig = [:]
		
		createOrRetrieveConfig(request)
		
		if (attrs) {
	        this.instanceName = attrs.remove("name") ?: this.DEFAULT_INSTANCENAME
			this.userSpace = attrs.remove("userSpace") ?: this.DEFAULT_USERSPACE  
			this.append = (attrs.remove("append") == "true")

            this.fileBrowser = attrs.remove("fileBrowser") ?: this.defaultFileBrowser
            this.showThumbs = (attrs.remove("showThumbs") == "true")

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
            if (this.skipAllowedItemsCheck || key in ALLOWED_CONFIG_ITEMS || key.startsWith('filebrowser')) {
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
        if (this.skipAllowedItemsCheck || var in ALLOWED_CONFIG_ITEMS || var.startsWith('toolbar_')) {
			this.config[var] = value
		}
		else {
		    throw new UnknownOptionException("Unknown option: ${var}. Option names are case sensitive! Check the spelling.")
        }
    }

	def getBrowseUrl(type, userSpace, fileBrowser, showThumbs) {
        def browserUrl
        def prefix = getConnectorsPrefix()
        if (fileBrowser == 'ofm') {
            browserUrl = "${this.contextPath}/${prefix}/ofm?fileConnector=${this.contextPath}/${prefix}/ofm/filemanager&treeConnector=${this.contextPath}/${prefix}/ofm/filetree&type=${type}${userSpace ? '&space='+ userSpace : ''}${showThumbs ? '&showThumbs='+ showThumbs : ''}"
        }
        else {
            browserUrl = "${this.basePath}/js/filebrowser/browser.html?Connector=${this.contextPath}/${prefix}/standard/filemanager?Type=${type}${userSpace ? '&userSpace='+ userSpace : ''}"
        }

        return browserUrl
	}
	
	def getUploadUrl(type, userSpace) {
		return "${this.contextPath}/${getConnectorsPrefix()}/standard/uploader?Type=${type}${userSpace ? '&userSpace='+ userSpace : ''}" 
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
            def type = WordUtils.capitalize(t)
			def typeForConnector = "${type == 'Link' ? 'File' : type}"
			
            if (ckconfig?.upload?."${t}"?.browser) {
				this.config["filebrowser${type}BrowseUrl"] = "'${getBrowseUrl(typeForConnector, this.userSpace, this.fileBrowser, this.showThumbs)}'" 
            }
            if (ckconfig?.upload?."${t}"?.upload) {
				this.config["filebrowser${type}UploadUrl"] = "'${getUploadUrl(typeForConnector, this.userSpace)}'" 
            }
        }

		// Config options
		def configs = []
		this.config.each {k, v ->
			if (!localConfig[k]) {
				configs << "${k}: ${v}"
			}
		}
		this.localConfig.each {k, v ->
			configs << "${k}: ${v}"
		}

		StringBuffer configuration = new StringBuffer()
        if (configs.size()) {
            configuration << """, {\n"""
           	configuration << configs.join(",\n")
            configuration << """}\n"""
        }
	
        return configuration
    }

    static getResourceTypes() {
        return RESOURCE_TYPES
    }

    static getConnectorsPrefix() {
        def prefix = ConfigurationHolder.config.ckeditor?.connectors?.prefix ?: DEFAULT_CONNECTORS_PREFIX
        return PathUtils.checkSlashes(prefix, "L- R-", true)
    }

    static getUploadPrefix() {
        def ckconfig = ConfigurationHolder.config.ckeditor
        def prefix = null
        if (ckconfig?.upload?.baseurl && ckconfig?.upload?.enableContentController) {
            prefix = PathUtils.checkSlashes(ckconfig?.upload?.baseurl, "L+ R-", true)
        }
        return prefix
    }

    // See: http://docs.cksource.com/ckeditor_api/symbols/CKEDITOR.config.html
    static final ALLOWED_CONFIG_ITEMS = [
        // Items not listed in main config file
        'uiColor',
            
		// Main config
		'customConfig',
		'autoUpdateElement',
		'baseFloatZIndex',
		'baseHref',
		'contentsCss',
		'contentsLangDirection',
		'contentsLanguage',
		'language',
		'defaultLanguage',
		'enterMode',
		'forceEnterMode',
		'shiftEnterMode',
		'corePlugins',
		'docType',
		'bodyId',
		'bodyClass',
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

        // plugins/autogrow/plugin.js
        'autoGrow_minHeight',
        'autoGrow_maxHeight',

        // plugins/basicstyles/plugin.js
        'coreStyles_bold',
        'coreStyles_italic',
        'coreStyles_underline',
        'coreStyles_strike',
        'coreStyles_subscript',
        'coreStyles_superscript',

        // plugins/colorbutton/plugin.js
        'colorButton_enableMore',
        'colorButton_colors',
        'colorButton_foreStyle',
        'colorButton_backStyle',

        // plugins/contextmenu/plugin.js
        'browserContextMenuOnCtrl',

        // plugins/dialog/plugin.js
        'dialog_backgroundCoverColor',
        'dialog_backgroundCoverOpacity',
        'dialog_startupFocusTab',
        'dialog_magnetDistance',

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

        // plugins/filebrowser/plugin.js
        'filebrowserBrowseUrl',
        'filebrowserUploadUrl',
        'filebrowserImageBrowseUrl',
        'filebrowserFlashBrowseUrl',
        'filebrowserImageUploadUrl',
        'filebrowserFlashUploadUrl',
        'filebrowserImageBrowseLinkUrl',
        'filebrowserWindowFeatures',    

        // plugins/find/plugin.js
        'find_highlight',

        // plugins/font/plugin.js
        'font_names',
        'font_defaultLabel',
        'font_style',
        'fontSize_sizes',
        'fontSize_defaultLabel',
        'fontSize_style',

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

        // plugins/htmldataprocessor/plugin.js
        'forceSimpleAmpersand',

        // plugins/image/plugin.js
        'image_removeLinkByEmptyURL',
        'image_previewText',

        // plugins/indent/plugin.js
        'indentOffset',
        'indentUnit',
        'indentClasses',

        // plugins/keystrokes/plugin.js
        'blockedKeystrokes',
        'keystrokes',

        // plugins/menu/plugin.js
        'menu_subMenuDelay',
        'menu_groups',

        // plugins/newpage/plugin.js
        'newpage_html',

        // plugins/pastefromword/plugin.js
        'pasteFromWordPromptCleanup',
        'pasteFromWordCleanupFile',

        // plugins/pastetext/plugin.js
        'forcePasteAsPlainText',

        // plugins/removeformat/plugin.js
        'removeFormatTags',
        'removeFormatAttributes',

        // plugins/resize/plugin.js
        'resize_minWidth',
        'resize_minHeight',
        'resize_maxWidth',
        'resize_maxHeight',
        'resize_enabled',
        'resize_dir',

        // plugins/scayt/plugin.js
        'scayt_autoStartup',
        'scayt_maxSuggestions',
        'scayt_customerid',
        'scayt_moreSuggestions',
        'scayt_contextCommands',
        'scayt_sLang',
        'scayt_uiTabs',
        'scayt_srcUrl',
        'scayt_customDictionaryIds',
        'scayt_userDictionaryName',
        'scayt_contextMenuOntop',
        'scayt_contextMenuItemsOrder',

        // plugins/showblocks/plugin.js
        'startupOutlineBlocks',

        // plugins/showborders/plugin.js
        'startupShowBorders',

        // plugins/smiley/plugin.js
        'smiley_path',
        'smiley_images',
        'smiley_descriptions',
        'smiley_columns',

        // plugins/styles/plugin.js
        'stylesSet',

        // plugins/tab/plugin.js
        'tabSpaces',
        'enableTabKeyTools',

        // plugins/templates/plugin.js
        'templates',
        'templates_files',
        'templates_replaceContent',

        // plugins/toolbar/plugin.js
        'toolbarLocation',
        'toolbar',
        'toolbar_Basic',
        'toolbar_Full',
        'toolbarCanCollapse',
        'toolbarStartupExpanded',

        // plugins/undo/plugin.js
        'undoStackSize',

        // plugins/wsc/plugin.js
        'wsc_customerId',
        'wsc_customLoaderScript',

        // plugins/wysiwygarea/plugin.js
        'disableObjectResizing',
        'disableNativeTableHandles',
        'disableNativeSpellChecker',
        'ignoreEmptyParagraph'
    ]
}