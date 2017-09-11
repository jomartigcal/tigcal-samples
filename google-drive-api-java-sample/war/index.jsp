<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
  <head>
    <link rel="stylesheet" href="/static/bootstrap.min.css"></link>
    <link rel="stylesheet" href="/static/dredit.css"></link>
    <script src="https://www.google.com/jsapi" charset="utf-8"></script>
    <script src="/static/jquery.min.js" type="text/javascript" charset="utf-8"></script>
    <script src="/static/bootstrap.min.js" type="text/javascript" charset="utf-8"></script>
    <script src="/static/ace/ace.js" type="text/javascript" charset="utf-8"></script>
    <script src="/static/ace/mode-html.js" type="text/javascript" charset="utf-8"></script>
    <script src="/static/dredit.js" type="text/javascript" charset="utf-8"></script>
  </head>
  <body>
    <div id="main">
      <div id="nav-pane"></div>
      <div id="action-pane">
        <button type="submit" class="btn" id="create"><i
            class="icon-file icon"></i> New</button>
        <button type="submit" class="btn" id="open"><i
            class="icon-download icon"></i> Open</button>
        <button type="submit" class="btn" id="edit"><i
            class="icon-edit icon"></i> Edit</button>
        <button type="submit" class="btn" id="save"><i
            class="icon-upload icon"></i> Save</button>
        <div id="saving" class="pull-right alert">Talking to Drive</div>
        </div>
      <div id="editor-pane"></div>
    </div>
  </body>
  <script>
    var dredit = dredit || {};
    dredit.FILE_IDS = <%= request.getAttribute("ids") %>;
    dredit.CLIENT_ID = <%= request.getAttribute("client_id") %>;
    google.load('picker', '1');
  </script>
</html>
