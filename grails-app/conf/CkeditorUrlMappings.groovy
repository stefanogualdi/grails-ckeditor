class CkeditorUrlMappings {
    static mappings = {
        // Legacy file manager
    	"/ck/standard/filemanager" (controller: "standardFileManagerConnector", action: "connector")

        // File uploader
    	"/ck/standard/uploader" (controller: "standardFileManagerConnector", action: "uploader")

        // Open file manager
        "/ck/ofm/filemanager" (controller: "openFileManagerConnector", action: "fileManager")
        "/ck/ofm/filetree" (controller: "openFileManagerConnector", action: "fileTree")
    }
}
