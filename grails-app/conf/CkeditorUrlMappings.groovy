import org.gualdi.grails.plugins.ckeditor.CkeditorConfig

class CkeditorUrlMappings {

    static mappings = {

        def prefix = "/${CkeditorConfig.getConnectorsPrefix()}"
        def uploadPrefix = CkeditorConfig.getUploadPrefix()

        // Legacy file manager
        delegate.(prefix + "/standard/filemanager") (controller: "standardFileManagerConnector", action: "connector")

        // Open File Manager
        delegate.(prefix + "/ofm") (controller: "openFileManagerConnector", action: "index")
        delegate.(prefix + "/ofm/filemanager") (controller: "openFileManagerConnector", action: "fileManager")
        delegate.(prefix + "/ofm/filetree") (controller: "openFileManagerConnector", action: "fileTree")

        // Images outside the web-app dir
        if (uploadPrefix) {
            delegate.(uploadPrefix + "/$filepath**") (controller: "openFileManagerConnector", action: "show")
        }

        // File uploader
        delegate.(prefix + "/standard/uploader") (controller: "standardFileManagerConnector", action: "uploader")
    }
}
