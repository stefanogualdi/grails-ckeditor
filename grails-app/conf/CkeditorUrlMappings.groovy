import org.gualdi.grails.plugins.ckeditor.CkeditorConfig

class CkeditorUrlMappings {

    static mappings = {

        def prefix = "/${CkeditorConfig.getConnectorsPrefix()}"

        // Legacy file manager
        delegate.(prefix + "/standard/filemanager") (controller: "standardFileManagerConnector", action: "connector")

        // File uploader
        delegate.(prefix + "/standard/uploader") (controller: "standardFileManagerConnector", action: "uploader")

        // Open file manager
        delegate.(prefix + "/ofm/filemanager") (controller: "openFileManagerConnector", action: "fileManager")
        delegate.(prefix + "/ofm/filetree") (controller: "openFileManagerConnector", action: "fileTree")
    }
}
