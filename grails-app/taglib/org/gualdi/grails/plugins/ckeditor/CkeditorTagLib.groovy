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

import grails.util.Environment

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
