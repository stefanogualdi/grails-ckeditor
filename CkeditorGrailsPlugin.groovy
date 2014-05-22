/*
*
* Copyright 2009-2014 Stefano Gualdi
*
*   Licensed under the Apache License, Version 2.0 (the "License");
*   you may not use this file except in compliance with the License.
*   You may obtain a copy of the License at
*
*       http://www.apache.org/licenses/LICENSE-2.0
*
*   Unless required by applicable law or agreed to in writing, software
*   distributed under the License is distributed on an "AS IS" BASIS,
*   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*   See the License for the specific language governing permissions and
*   limitations under the License.
*
*/

class CkeditorGrailsPlugin {
    def version = "4.4.1.0"
    def grailsVersion = "2.0 > *"
    def dependsOn = [:]

    def pluginExcludes = [
            "grails-app/views/error.gsp"
    ]

    def author = "Stefano Gualdi"
    def authorEmail = "stefano.gualdi@gmail.com"
    def title = "CKeditor plugin"
    def description = """\
CKeditor web WYSIWYG editor integration plugin.
"""

    def license = "APACHE"
    def organization = [name: "Stefano Gualdi", url: "http://gualdi.org/"]
    def developers = []
    def issueManagement = [system: "JIRA", url: "http://jira.grails.org/browse/GPCKEDITOR"]
    def scm = [url: "http://github.com/stefanogualdi/grails-ckeditor/"]

    def documentation = "http://stefanogualdi.github.com/grails-ckeditor/"
}
