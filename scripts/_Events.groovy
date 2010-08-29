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

import grails.util.Environment
import org.codehaus.groovy.grails.plugins.PluginManagerHolder

eventCreateWarStart = { name, stagingDir ->
    if (Environment.current == Environment.current) {
        // Remove ckeditor _source folder source         
        println "\n[CKEDITOR PLUGIN] Removing _source folder ...\n"

        def version = PluginManagerHolder?.pluginManager?.getGrailsPlugin("ckeditor")?.version
        if (version) {
            def path = "${stagingDir}/plugins/ckeditor-${version}/js/ckeditor"

            ant.delete(dir: "${path}/_source")
            ant.delete(file: "${path}/ckeditor_basic_source.js")
            ant.delete(file: "${path}/ckeditor_source.js")
        }
        else {
            println "\n[CKEDITOR PLUGIN] Cannot remove _source folder!\n"
        }

        println "\n[CKEDITOR PLUGIN]  _source folder removed.\n"
    }
}
