class CkeditorUrlMappings {
    static mappings = {
        // Legacy file manager
    	"/ckconnector" ( controller: "ckeditor", action: "connector" )
    	"/ckuploader" ( controller: "ckeditor", action: "uploader" )

        // Open file manager
        "/ofm/filemanager" (controller: "openFileManagerConnector", action: "fileManager")
        "/ofm/filetree" (controller: "openFileManagerConnector", action: "fileTree")
    }
}
