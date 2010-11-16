/*
 * Copyright 2010 Stefano Gualdi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gualdi.grails.plugins.ckeditor.utils

class MimeUtils {

    static final MIME_TYPES = [
            html: 'text/html',
            htm: 'text/html',
            xml: 'text/xml',
            txt: 'text/plain',
            js: 'text/javascript',
            rss: 'application/rss+xml',
            atom: 'application/atom+xml',
            css: 'text/css',
            csv: 'text/csv',
            json: 'application/json',
            pdf: 'application/pdf',
            doc: 'application/msword',
            png: 'image/png',
            gif: 'image/gif',
            jpg: 'image/jpeg',
            jpeg: 'image/jpeg',
            swf: 'application/x-shockwave-flash',
            mov: 'video/quicktime',
            qt: 'video/quicktime',
            avi: 'video/x-msvideo',
            asf: 'video/x-ms-asf',
            asr: 'video/x-ms-asf',
            asx: 'video/x-ms-asf',
            mpa: 'video/mpeg',
            mpg: 'video/mpeg',
            mp2: 'video/mpeg',
            rtf: 'application/rtf',
            exe: 'application/octet-stream',
            xls: 'application/vnd.ms-excel',
            xlt: 'application/vnd.ms-excel',
            xlc: 'application/vnd.ms-excel',
            xlw: 'application/vnd.ms-excel',
            xla: 'application/vnd.ms-excel',
            xlm: 'application/vnd.ms-excel',
            ppt: 'application/vnd.ms-powerpoint',
            pps: 'application/vnd.ms-powerpoint',
            tgz: 'application/x-compressed',
            gz: 'application/x-gzip',
            zip: 'application/zip',
            mp3: 'audio/mpeg',
            mid: 'audio/mid',
            ico: 'image/x-icon'
    ]

    static getDefaultMimeType(String fileName) {
        def dotpos = fileName.lastIndexOf('.')
        if (dotpos >= 0) {
            def ext = fileName[dotpos + 1..-1]
            return MIME_TYPES[ext.toLowerCase()] ?: 'application/octet-stream'
        }
        else {
            return "text/plain"
        }
    }

    static getMimeTypeByExt(String ext) {
        def result = "text/plain"
        if(ext) {
            result = MIME_TYPES[ext.toLowerCase()] ?: 'application/octet-stream'
        }
        
        return result
    }
}