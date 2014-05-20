<%@page scriptletCodec="none" %>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="content-type" content="text/html; charset=utf-8"/>
        <title>File Manager</title>
        <link rel="stylesheet" type="text/css" href="${resource(dir: 'js/ofm/styles', file: 'reset.css', plugin: 'ckeditor')}"/>
        <link rel="stylesheet" type="text/css" href="${resource(dir: 'js/ofm/scripts/jquery.filetree', file: 'jqueryFileTree.css', plugin: 'ckeditor')}"/>
        <link rel="stylesheet" type="text/css" href="${resource(dir: 'js/ofm/scripts/jquery.contextmenu', file: 'jquery.contextMenu-1.01.css', plugin: 'ckeditor')}"/>
        <link rel="stylesheet" type="text/css" href="${resource(dir: 'js/ofm/styles', file: 'filemanager.css', plugin: 'ckeditor')}"/>
        <!--[if IE 9]>
        <link rel="stylesheet" type="text/css" href="${resource(dir: 'js/ofm/styles', file: 'ie9.css', plugin: 'ckeditor')}" />
        <![endif]-->
        <!--[if lte IE 8]>
        <link rel="stylesheet" type="text/css" href="${resource(dir: 'js/ofm/styles', file: 'ie8.css', plugin: 'ckeditor')}" />
        <![endif]-->

        <script type="text/javascript">
            // Base config
            var configUrl = "<%= configUrl %>";
        </script>
    </head>

    <body>
        <div>
            <form id="uploader" method="post">
                <button id="home" name="home" type="button" value="Home">&nbsp;</button>

                <h1></h1>

                <div id="uploadresponse"></div>
                <input id="mode" name="mode" type="hidden" value="add"/>
                <input id="currentpath" name="currentpath" type="hidden"/>

                <div id="file-input-container">
                    <div id="alt-fileinput">
                        <input id="filepath" name="filepath" type="text"/><button id="browse" name="browse" type="button" value="Browse"></button>
                    </div>
                    <input id="newfile" name="newfile" type="file"/>
                </div>
                <button id="upload" name="upload" type="submit" value="Upload"></button>
                <button id="newfolder" name="newfolder" type="button" value="New Folder"></button>
                <button id="grid" class="ON" type="button">&nbsp;</button>
                <button id="list" type="button">&nbsp;</button>
            </form>

            <div id="splitter">
                <div id="filetree"></div>

                <div id="fileinfo">
                    <h1></h1>
                </div>
            </div>

            <form name="search" id="search" method="get">
                <div>
                    <input type="text" value="" name="q" id="q"/>
                    <a id="reset" href="#" class="q-reset"></a>
                    <span class="q-inactive"></span>
                </div>
            </form>

            <ul id="itemOptions" class="contextMenu">
                <li class="select"><a href="#select"></a></li>
                <li class="download"><a href="#download"></a></li>
                <li class="rename"><a href="#rename"></a></li>
                <li class="move"><a href="#move"></a></li>
                <li class="replace"><a href="#replace"></a></li>
                <li class="delete separator"><a href="#delete"></a></li>
            </ul>

            <script type="text/javascript" src="${resource(dir: 'js/ofm/scripts', file: 'jquery-1.8.3.min.js', plugin: 'ckeditor')}"></script>
            <script type="text/javascript" src="${resource(dir: 'js/ofm/scripts', file: 'jquery.form-3.24.js', plugin: 'ckeditor')}"></script>
            <script type="text/javascript" src="${resource(dir: 'js/ofm/scripts/jquery.splitter', file: 'jquery.splitter-1.5.1.js', plugin: 'ckeditor')}"></script>
            <script type="text/javascript" src="${resource(dir: 'js/ofm/scripts/jquery.filetree', file: 'jqueryFileTree.js', plugin: 'ckeditor')}"></script>
            <script type="text/javascript" src="${resource(dir: 'js/ofm/scripts/jquery.contextmenu', file: 'jquery.contextMenu-1.01.js', plugin: 'ckeditor')}"></script>
            <script type="text/javascript" src="${resource(dir: 'js/ofm/scripts', file: 'jquery.impromptu-3.2.min.js', plugin: 'ckeditor')}"></script>
            <script type="text/javascript" src="${resource(dir: 'js/ofm/scripts', file: 'jquery.tablesorter-2.7.2.min.js', plugin: 'ckeditor')}"></script>
            <script type="text/javascript" src="${resource(dir: 'js/ofm/scripts', file: 'filemanager.min.js', plugin: 'ckeditor')}"></script>
        </div>
    </body>
</html>
