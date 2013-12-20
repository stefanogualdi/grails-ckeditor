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
        	outb << """<textarea id="${this.config.instanceId}" name="${this.config.instanceName}">${this.initialValue?.encodeAsHTML()}</textarea>\n"""
		}
        outb << """<script type="text/javascript">\n"""

        if (this.config.removeInstance) {
            outb << """if (CKEDITOR.instances['${this.config.instanceId}']){CKEDITOR.remove(CKEDITOR.instances['${this.config.instanceId}']);}\n"""
        }

        outb << """CKEDITOR."""
		if (this.config.append) {
			outb << """appendTo"""
		}
		else {
			outb << """replace"""
		}
		outb << """('${this.config.instanceId}'"""
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
		return this.config.getBrowseUrl(this.config.type, this.config.userSpace, this.config.fileBrowser, this.config.showThumbs, this.config.viewMode)
    }
}
