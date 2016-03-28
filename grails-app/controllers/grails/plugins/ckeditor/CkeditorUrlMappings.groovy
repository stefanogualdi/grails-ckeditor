/*
 * Copyright 2016 Stefano Gualdi
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

package grails.plugins.ckeditor

class CkeditorUrlMappings {

    static mappings = {

        def prefix = "/${CkeditorConfig.getConnectorsPrefix()}"
        def uploadPrefix = CkeditorConfig.getUploadPrefix()

        // Open File Manager
        delegate.(prefix + "/ofm") (controller: "openFileManagerConnector", action: "index")
        delegate.(prefix + "/ofm/config") (controller: "openFileManagerConnector", action: "config")
        delegate.(prefix + "/ofm/filemanager") (controller: "openFileManagerConnector", action: "fileManager")

        // Images outside the web-app dir
        if (uploadPrefix) {
            delegate.(uploadPrefix + "/$filepath**") (controller: "openFileManagerConnector", action: "show")
        }

        // File uploader
        delegate.(prefix + "/uploader") (controller: "openFileManagerConnector", action: "uploader")
    }
}
