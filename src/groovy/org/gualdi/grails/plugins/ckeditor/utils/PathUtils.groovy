package org.gualdi.grails.plugins.ckeditor.utils

/**
 * @author Stefano Gualdi <stefano.gualdi@gmail.com>
 */

class PathUtils {
    static splitFilename(fileName) {
    	def idx = fileName.lastIndexOf(".")
    	return  [name: fileName[0..idx - 1], ext: fileName[idx + 1..-1]]
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
}