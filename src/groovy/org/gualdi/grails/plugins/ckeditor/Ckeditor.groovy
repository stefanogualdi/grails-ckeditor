package org.gualdi.grails.plugins.ckeditor

import org.apache.log4j.Logger
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.apache.commons.lang.StringUtils
import org.gualdi.grails.plugins.ckeditor.utils.PluginUtils

/**
 * @author Stefano Gualdi <stefano.gualdi@gmail.com>
 */

class Ckeditor {

    private final Logger log = Logger.getLogger(getClass())

    def config
	def initialValue
	
    Ckeditor(request, attrs, value = "") {
		this.config = new CkeditorConfig(request, attrs)
		this.initialValue = value
    }

    def renderResources() {
        StringBuffer outb = new StringBuffer()

        outb << """<script type="text/javascript" src="${this.config.basePath}/js/ckeditor/ckeditor.js"></script>"""

        return outb.toString()
    }
                                                
    def renderEditor() {
        StringBuffer outb = new StringBuffer()

		if (!this.config.append) {
        	outb << """<textarea name="${this.config.instanceName}">${this.initialValue?.encodeAsHTML()}</textarea>\n"""
		}
        outb << """<script type="text/javascript">\n"""
        outb << """CKEDITOR."""
		if (this.config.append) {
			outb << """appendTo"""
		}
		else {
			outb << """replace"""
		}
		outb << """('${this.config.instanceName}'"""
		outb << this.config.configuration
        outb << """);\n"""
        outb << """</script>\n"""

        return outb.toString()
    }

    def renderFileBrowser() {
        StringBuffer outb = new StringBuffer()

        outb << "<a href="
        outb << "\""
        outb << renderFileBrowserLink()
        outb << "\""
        outb << " "
        if (this.config.target) {
            outb << "target="
            outb << "\""
            outb << this.config.target
            outb << "\""
            outb << " "
        }
        outb << ">"
        outb << this.initialValue.encodeAsHTML()
        outb << "</a>"

		return outb.toString()
    }

    def renderFileBrowserLink() {
		return this.config.getBrowseUrl(this.config.type, this.config.userSpace)
    }

}
