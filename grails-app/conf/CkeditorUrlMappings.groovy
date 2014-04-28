import org.gualdi.grails.plugins.ckeditor.CkeditorConfig

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
