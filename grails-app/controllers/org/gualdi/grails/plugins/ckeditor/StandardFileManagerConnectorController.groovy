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
 *
 */

package org.gualdi.grails.plugins.ckeditor

import org.gualdi.grails.plugins.ckeditor.utils.PathUtils
import org.gualdi.grails.plugins.ckeditor.utils.FileUtils

class StandardFileManagerConnectorController {

	// Generic errors
	static final ERROR_NOERROR = 0
	static final ERROR_CUSTOM = 1

	// Connector errors
	static final ERROR_FOLDER_EXISTS = 101
	static final ERROR_INVALID_FOLDER_NAME = 102
	static final ERROR_NO_CREATE_PERMISSIONS = 103
	static final ERROR_INVALID_FILE_NAME = 104
	static final ERROR_CANNOT_DELETE = 105
	static final ERROR_UNKNOWN = 110

	// Uploader errors
	static final ERROR_FILE_RENAMED = 201
	static final ERROR_INVALID_FILE_TYPE = 202
	static final ERROR_NO_UPLOAD_PERMISSIONS = 203

    def connector = {
		execute(params.Command, params.CurrentFolder, params.userSpace)
	}

	def uploader = {
		execute('FileUpload', '/', params.userSpace, true)
	}

    private execute(command, currentFolder, userSpace, uploadOnly = false) {
		def config = grailsApplication.config.ckeditor

    	def baseDir = config?.upload?.basedir ?: CkeditorConfig.DEFAULT_BASEDIR
        baseDir = PathUtils.checkSlashes(baseDir, "L+ R+", true)

        def spaceDir = PathUtils.sanitizePath(userSpace)
        spaceDir = PathUtils.checkSlashes(spaceDir, "L- R+", true)
        
		def type = params.Type
		def currentPath = "${baseDir}${spaceDir}${type}${currentFolder}"
		def currentUrl
		def realPath

        // Use a directory outside of the application space?
        if (config?.upload?.baseurl) {
            def baseUrl = PathUtils.checkSlashes(config.upload.baseurl, "R+", true)
            currentUrl = "${baseUrl}${spaceDir}${type}${currentFolder}"
            realPath = currentPath;
        }
        else {
            currentUrl = "${request.contextPath}${currentPath}"
            realPath = servletContext.getRealPath(currentPath)
        }

    	def finalDir = new File(realPath)
    	if (!finalDir.exists()) {
			finalDir.mkdirs()
    	}

        log.debug("userSpace = ${userSpace}")
    	log.debug("Command = ${command}")
    	log.debug("CurrentFolder = ${currentFolder}")
    	log.debug("Type = ${type}")
    	log.debug("finalDir = ${finalDir}")

    	def errorNo
    	def errorMsg

    	switch( command ) {
			case 'GetFolders':
				response.setHeader("Cache-Control", "no-cache")
				render(contentType: "text/xml", encoding: "UTF-8") {
					Connector(command: command, resourceType: type) {
						CurrentFolder(path: currentFolder, url: currentUrl)
						Folders {
							finalDir.eachDir {
								Folder(name: it.name)
							}
						}
					}
				}
				break
			case 'GetFoldersAndFiles':
				response.setHeader("Cache-Control", "no-cache")
				render(contentType: "text/xml", encoding: "UTF-8") {
					Connector(command: command, resourceType: type) {
						CurrentFolder(path: currentFolder, url: currentUrl)
						Folders {
							finalDir.eachDir {
								Folder(name: it.name)
							}
						}
						Files {
							finalDir.listFiles().sort().each {
								if (!it.directory) {
									'File'(name: it.name, size: it.length() / 1024)
								}
							}
						}
					}
				}
				break
			case 'CreateFolder':
				def newFolderName = params.NewFolderName
				def newFinalDir = new File(finalDir, newFolderName)
				errorNo = this.ERROR_NOERROR

				if (newFinalDir.exists()) {
					errorNo = this.ERROR_FOLDER_EXISTS
				}
				else {
					try {
						if (newFinalDir.mkdir()) {
							errorNo = this.ERROR_NOERROR
						}
						else {
							errorNo = this.ERROR_INVALID_FOLDER_NAME
						}
					}
					catch (SecurityException se) {
						errorNo = this.ERROR_NO_CREATE_PERMISSIONS
					}
				}

				response.setHeader("Cache-Control", "no-cache")
				render(contentType: "text/xml", encoding: "UTF-8") {
					Connector(command: command, resourceType: type) {
						CurrentFolder(path: currentFolder, url: currentUrl)
						'Error'(number: errorNo)
					}
				}
				break
			case 'DeleteFile':
                def fileName = params.FileName
				def fileFinalName = new File(finalDir, fileName)

                errorNo = this.ERROR_NOERROR
                if (fileFinalName.exists()) {
                    try {
						if (fileFinalName.delete()) {
							errorNo = this.ERROR_NOERROR
						}
						else {
							errorNo = this.ERROR_INVALID_FILE_NAME
						}
					}
					catch (SecurityException se) {
						errorNo = this.ERROR_NO_CREATE_PERMISSIONS
					}
                }
                else {
                    errorNo = this.ERROR_INVALID_FILE_NAME
                }

                response.setHeader("Cache-Control", "no-cache")
				render(contentType: "text/xml", encoding: "UTF-8") {
					Connector(command: command, resourceType: type) {
						CurrentFolder(path: currentFolder, url: currentUrl)
						'Error'(number: errorNo)
					}
				}
			    break
            case 'DeleteFolder':
                def folderName = params.FolderName
                def folderFinalName = new File(finalDir, folderName)

                errorNo = this.ERROR_NOERROR
                if (folderFinalName.exists() && folderFinalName.isDirectory()) {
                    try {
                        def deleteClosure
                        deleteClosure = {
                           log.debug "Dir ${it.canonicalPath}"
                           it.eachDir(deleteClosure)
                           it.eachFile {
                               log.debug "Deleting file ${it.canonicalPath}"
                               it.delete()
                           }
                        }
                        deleteClosure folderFinalName
                        folderFinalName.delete()

                        errorNo = this.ERROR_NOERROR
                    }
                    catch(SecurityException se) {
                        errorNo = this.ERROR_NO_CREATE_PERMISSIONS
                    }
                }
                else {
                    errorNo = this.ERROR_INVALID_FOLDER_NAME
                }

                response.setHeader("Cache-Control", "no-cache")
                render(contentType: "text/xml", encoding: "UTF-8") {
                    Connector(command: command, resourceType: type) {
                        CurrentFolder(path: currentFolder, url: currentUrl)
                        'Error'(number: errorNo)
                    }
                }
                break
            case 'RenameFile':
                def oldName = params.FileName
                def newName = params.NewName
				def oldFinalName = new File(finalDir, oldName)
				def newFinalName = new File(finalDir, newName)

                errorNo = this.ERROR_NOERROR
                if (!newFinalName.exists() && FileUtils.isFileAllowed(newName, type)) {
                    try {
						if( oldFinalName.renameTo( newFinalName ) ) {
							errorNo = this.ERROR_NOERROR
						}
						else {
							errorNo = this.ERROR_INVALID_FILE_NAME
						}
					}
					catch (SecurityException se) {
						errorNo = this.ERROR_NO_CREATE_PERMISSIONS
					}
                }
                else {
                    errorNo = this.ERROR_INVALID_FILE_NAME
                }

                response.setHeader("Cache-Control", "no-cache")
                render(contentType: "text/xml", encoding: "UTF-8") {
                    Connector(command: command, resourceType: type) {
                        CurrentFolder(path: currentFolder, url: currentUrl)
                        'Error'(number: errorNo)
                    }
                }
                break
            case 'RenameFolder':
                def oldName = params.FolderName
                def newName = params.NewName
                def oldFinalName = new File(finalDir, oldName)
                def newFinalName = new File(finalDir, newName)

                errorNo = this.ERROR_NOERROR
                if (!newFinalName.exists()) {
                    try {
                        if (oldFinalName.renameTo(newFinalName)) {
                            errorNo = this.ERROR_NOERROR
                        }
                        else {
                            errorNo = this.ERROR_INVALID_FOLDER_NAME
                        }
                    }
                    catch (SecurityException se) {
                        errorNo = this.ERROR_NO_CREATE_PERMISSIONS
                    }
                }
                else {
                    errorNo = this.ERROR_INVALID_FOLDER_NAME
                }

                response.setHeader("Cache-Control", "no-cache")
                render(contentType: "text/xml", encoding: "UTF-8") {
                    Connector(command: command, resourceType: type) {
                        CurrentFolder(path: currentFolder, url: currentUrl)
                        'Error'(number: errorNo)
                    }
                }
                break
			case 'FileUpload':
				errorNo = this.ERROR_NOERROR
				errorMsg = "" 
                def uploadFieldName = uploadOnly ? 'upload' : 'NewFile'
				def newName = ""
				def overwrite = config.upload.overwrite ?: false

				if (isUploadEnabled(type)) {
					if (request.method != "POST" ) {
						errorNo = this.ERROR_CUSTOM
						errorMsg = "INVALID CALL"
					}
					else {
						def file = request.getFile(uploadFieldName)
						if (!file) {
							errorNo = this.ERROR_CUSTOM
							errorMsg = "INVALID FILE"
						}
						else {
							errorNo = this.ERROR_NOERROR
							newName = file.originalFilename

                            def f = PathUtils.splitFilename(newName)
							if (FileUtils.isAllowed(f.ext, type)) {
                                def fileToSave = new File(finalDir, newName)
								if ( !overwrite ) {
									def idx = 1
									while (fileToSave.exists()) {
										errorNo = this.ERROR_FILE_RENAMED
										newName = "${f.name}(${idx}).${f.ext}"
										fileToSave = new File(finalDir, newName)
										idx++
									}
								}
								file.transferTo(fileToSave)
							}
							else {
								errorNo = this.ERROR_INVALID_FILE_TYPE
								errorMsg = "INVALID FILE TYPE"
							}
						}
					}
    			}
				else {
					errorNo = this.ERROR_CUSTOM
					errorMsg = "UPLOADS ARE DISABLED!"
				}

				response.setHeader("Cache-Control", "no-cache")
				render(contentType: "text/html", encoding: "UTF-8") {
					if (uploadOnly) {
						if (config?.upload?.baseurl) {
							currentUrl = "${request.contextPath}${currentUrl}"
						}
                        def fname = errorMsg ? "" : "${currentUrl}${newName}"
                        script(type: "text/javascript", "window.parent.CKEDITOR.tools.callFunction(${params.CKEditorFuncNum}, '${fname}', '${errorMsg}');")
					}
					else {
						script(type: "text/javascript", "window.parent.frames['frmUpload'].OnUploadCompleted(${errorNo}, '${newName}');")
					}
				}
				break
    	}
        log.debug "errorNo = ${errorNo}"
        log.debug "errorMsg = ${errorMsg}"
    }

    // Utility methods
    private isUploadEnabled(type) {
    	def config = grailsApplication.config.ckeditor

    	def resType = type?.toLowerCase()
    	if (resType == 'file') {
    		resType = 'link'
    	}

    	return config.upload."${resType}".upload
    }
}
