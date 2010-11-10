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

import org.gualdi.grails.plugins.ckeditor.utils.PathUtils

class OpenFileManagerTagLib {

    static namespace = "ofm"

    def baseUrl = { attrs ->
        def bUrl = PathUtils.getBaseUrl([space: attrs.space, type: attrs.type])

        def config = grailsApplication.config.ckeditor
        if (config?.upload?.baseurl) {
            bUrl = PathUtils.checkSlashes(config?.upload?.baseurl, "L- R-") + PathUtils.checkSlashes(bUrl, "R-")
        }
        else {
            bUrl = PathUtils.checkSlashes(bUrl, "R-")
        }

        out << "${request.contextPath}/${bUrl}"
    }

    def currentLocale = { attrs ->
        def locale = request.getLocale().toString()[0..1]
        if (!(locale in CkeditorConfig.OFM_LOCALES)) {
            locale = 'en'
        }
        out << locale
    }
}
