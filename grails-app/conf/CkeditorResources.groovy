/*
 * Copyright 2012 Stefano Gualdi
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
 *
 */


/**
 * @author Raanan Weber <raananw@gmail.com>
 */

def dev = grails.util.GrailsUtil.isDevelopmentEnv()

modules = {
    'ckeditor' {
        resource url:[plugin: 'ckeditor', dir: 'js/ckeditor', file: 'ckeditor'+(dev ? '' : '_source')+'.js'], disposition: 'head', exclude:'minify'
    }
}