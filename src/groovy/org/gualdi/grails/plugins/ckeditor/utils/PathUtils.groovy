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

package org.gualdi.grails.plugins.ckeditor.utils

import grails.util.Holders
import org.gualdi.grails.plugins.ckeditor.CkeditorConfig
import org.apache.commons.lang.WordUtils

/**
 * @author Stefano Gualdi <stefano.gualdi@gmail.com>
 */
class PathUtils {

    static getBaseUrl(params) {
        def config = Holders.config.ckeditor

        def baseUrl
        if (config?.upload?.baseurl) {
            baseUrl = ""
        }
        else {
            baseUrl = config?.upload?.basedir ?: CkeditorConfig.DEFAULT_BASEDIR
        }

        baseUrl = PathUtils.checkSlashes(baseUrl, "L- R-", true)

        def spaceDir = PathUtils.sanitizePath(params.space)
        if (spaceDir) {
            baseUrl += "/" + spaceDir
        }

        def typeName = PathUtils.sanitizePath(params.type?.toLowerCase())
        if (typeName) {
            typeName = WordUtils.capitalize(typeName)
            baseUrl += "/" + typeName
        }

        return baseUrl
    }

    static splitFilename(fileName) {
    	def idx = fileName.lastIndexOf(".")
        def name = fileName
        def ext = ""
        if (idx > 0) {
            name = fileName[0..idx - 1]
            if(fileName.length() > idx + 1) { 
        		ext = fileName[idx + 1..-1]
	        }
        }
        return [name: name, ext: ext]
    }

    static getFilePath(fileName) {
    	def idx = fileName.lastIndexOf(File.separator)
        def path = fileName[0..idx]

        return path
    }

    static sanitizePath(path) {
        def result = ""
        if (path) {
	        // remove: . \ / | : ? * " ' ` ~ < > {space}
            result = path.replaceAll(/\.|\/|\\|\||:|\?|\*|"|'|~|`|<|>| /, "")
        }
        return result
    }

    /**
     * Remove or add slashes as indicated in rules
     *
     * rules: space separated list of rules
     *      R- = remove slash on right
     *      R+ = add slash on right
     *      L- = remove slash on left
     *      L+ = add slash on left
     */
    static checkSlashes(path, rules, isUrl = false) {
        def result = path?.trim()
        if (result) {
            def rls = rules.split(' ')
            def separator = isUrl ? '/' : File.separator
            rls.each { r ->
                def isAdd = (r[1] == '+')

                if (isAdd) {
                    if (r[0].toUpperCase() == 'L') {
                        // Add separator on left
                        if (!result.startsWith('/') && !result.startsWith('\\')  ) {
                            result = separator + result
                        }
                    }
                    else {
                        // Add separator on right
                        if (!result.endsWith('/') && !result.endsWith('\\')  ) {
                            result = result + separator
                        }
                    }
                }
                else {
                    if (r[0].toUpperCase() == 'L') {
                        // Remove separator on left
                        if (result.startsWith('/') || result.startsWith('\\')  ) {
                            result = result.substring(1)
                        }
                    }
                    else {
                        // Remove separator on right
                        if (result.endsWith('/') || result.endsWith('\\')  ) {
                            result = result[0..-2]
                        }
                    }
                }
            }
        }
        return result
    }

    static normalizePath(path) {
        def el = path.tokenize(File.separator)
        def p = []
        for(e in el) {
            if (e == ".") {
                // skip
            }
            else if (e == "..") {
                p.pop()
            }
            else {
                p << e
            }
        }

        def result = "" << ""
        if (path.startsWith(File.separator)) {
            result << File.separator
        }

        result << p.join(File.separator)

        if (path.endsWith(File.separator)) {
            result << File.separator
        }

        return result.toString()
    }

    static isSafePath(baseDir, file) {
        def p = normalizePath(file.absolutePath)
        return p.startsWith(baseDir)
    }
}