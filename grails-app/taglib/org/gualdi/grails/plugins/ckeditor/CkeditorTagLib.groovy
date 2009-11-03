package org.gualdi.grails.plugins.ckeditor

import org.codehaus.groovy.grails.plugins.PluginManagerHolder

/**
 * @author Stefano Gualdi <stefano.gualdi@gmail.com>
 */

class CkeditorTagLib {

    static namespace = "ckeditor"

    def resources = { attrs ->
        def editor = new Ckeditor(request, attrs)
        out << editor.renderResources()
    }

	def config = { attrs, body ->
        def cfg = new CkeditorConfig(request)
		
		def var = attrs.remove('var');
        try {
			if (var) {
				def value = body()
	            cfg.addComplexConfigItem(var, value)
			}
			else {
	            cfg.addConfigItem(attrs)
			}
        }
        catch (Exception e) {
            throwTagError(e.message)
        }
    }

    def editor = { attrs, body ->
        def editor = new Ckeditor(request, attrs, body())
        out << editor.renderEditor()
    }

	def fileBrowser = { attrs, body ->
		def editor = new Ckeditor(request, attrs, body())
		out << editor.renderFileBrowser()
    }

    def fileBrowserLink = { attrs ->
		def editor = new Ckeditor(request, attrs)
		out << editor.renderFileBrowserLink()
    }
}
