/*
 * Copyright (c) 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */



/**
 * Namespace for DrEdit user interface.
 * @type {object}
 */
var dredit = dredit || {};
dredit.main_view = null;
dredit.new_file_counter = 0;


/**
 * User interface controller class.
 * @constructor
 */
dredit.View = function(config) {
  this.config = config || {};
  this.current_page = null;
  this.editor_id = 'editor';
  this.pages = {};
};


/**
 * Creates the user interface of the view.
 */
dredit.View.prototype.CreateUi = function() {

  this.CreateNavs();
  this.CreateEditor();
  this.saver = new dredit.SaveNotification();

  $('#edit').click($.proxy(this.Edit, this));
  $('#save').click($.proxy(this.Save, this));
  $('#create').click($.proxy(this.Create, this));
  $('#open').click($.proxy(this.CreatePicker, this));

}

/**
 * Creates the list that will hold the file navigation elements.
 */
dredit.View.prototype.CreateNavs = function() {
  $('#nav-pane').append(
      $('<ul></ul>')
          .attr('id', 'editor-navs')
          .attr('class', 'nav nav-pills'));
}


/**
 * Creates the element that will hold the text editor.
 */
dredit.View.prototype.CreateEditor = function() {
  $('#editor-pane').append(
      $('<div></div>')
      .attr('id', 'editor-holder')
      .append(
          $('<div></div>')
          .attr('class', 'editor')
          .attr('id', this.editor_id)));
  this.editor = ace.edit(this.editor_id);
  var mode = require('ace/mode/html').Mode;
  this.editor.getSession().setMode(new mode());
}


/**
 * Creates a page for a file ID.
 * @param {string} File's ID or an empty string for a new file.
 */
dredit.View.prototype.CreatePage = function(file_id) {
  var page = new dredit.Page(this);
  page.Load(file_id);
  this.pages[page.tab_id] = page;
}


dredit.View.prototype.PickerSelected = function(data) {
  if (data.action == 'picked') {
    for (i in data.docs) {
      this.CreatePage(data.docs[i].id);
    }
  }
}

dredit.View.prototype.CreatePicker = function() {
  var view = new google.picker.View(google.picker.ViewId.DOCS);
  view.setMimeTypes('text/plain,text/html');
  picker = new google.picker.PickerBuilder().
    setAppId(dredit.CLIENT_ID).
    addView(view).
    setCallback($.proxy(this.PickerSelected, this)).
    build();
  picker.setVisible(true);
}


/**
 * Sets a tab id as the active page.
 * @param {string} tab_id Tab's ID.
 */
dredit.View.prototype.SetTab = function(tab_id) {
  this.SetPage(this.pages[tab_id]);
}


/**
 * Sets a page as the active page.
 * @param {dredit.Page} page Page to activate.
 */
dredit.View.prototype.SetPage = function(page) {
  this.current_page = page;
  $('#editor-navs > li').removeClass('active');
  page.label_holder.addClass('active');
  this.editor.setSession(page.session);
}



/**
 * Performs an appropriate XHR to save the current file.
 */
dredit.View.prototype.Save = function() {
  if (!dredit.blocked) {
    this.current_page.Save();
  }
  else {
    // console.log('Blocked on another operation.');
  }
};


dredit.View.prototype.Edit = function() {
  this.current_page.title_changer.Pop();
};


dredit.View.prototype.Create = function() {
  this.CreatePage();
};


/**
 * Model for an individual file.
 * @constructor
 */
dredit.Page = function(view) {
  this.file_id = null;
  this.title = null;
  this.description = null;
  this.content = null;
  this.position = null;
  this.view = view;
}


/**
 * Create the navigation label for this page.
 */
dredit.Page.prototype.CreateLabel = function() {
  this.label_holder = $('<li></li>');
  var tab_id = this.tab_id;
  var view = this.view;
  var _OnFileClicked = function() {
    view.SetTab(tab_id);
  }
  this.label_text = $('<a>Loading</a>')
        .attr('href', '#' + this.tab_id)
        .click(_OnFileClicked);
  this.label_holder.append(this.label_text);
  $('#editor-navs').append(this.label_holder);
}


/**
 * Load a file from the server.
 * @param {string} file ID to load or an empty string for a new file.
 */
dredit.Page.prototype.Load = function(file_id) {
  this.title_changer = new dredit.TitleChanger(this);
  if (!file_id) {
    this.tab_id = dredit.new_file_counter++ + "";
    this.title_changer.Pop();
    this.CreateLabel();
    this.Empty();
  }
  else {
    this.file_id = file_id;
    this.tab_id = file_id;
    this.CreateLabel();
    this.Get();
  }
}

dredit.Page.prototype.Empty = function(file_id) {
  this.GetSuccess({
    'title': 'untitled.html',
    'content': '',
    'mimeType': 'text/html',
    'description': '',
  });
}

dredit.Page.prototype.GetSuccess = function(data, result, xhr) {
  if (data.redirect) {
    window.location.href = data.redirect;
  }
  else {
  this.title = data['title'];
  this.UpdateTitle(this.title);
  this.content = data['content'];
  this.mimetype = data['mimeType'];
  this.description = data['description'];
  var EditSession = require('ace/edit_session').EditSession;
  this.session = new EditSession(this.content);
  this.view.SetPage(this);
  }
}


dredit.Page.prototype.ServiceError = function(xhr, text_status, error) {
  console.log(error);
}

dredit.Page.prototype.Get = function() {
  $.ajax({
      url: '/svc?file_id=' + this.file_id,
      success: this.GetSuccess,
      error: this.ServiceError,
      context: this
  });
}

dredit.Page.prototype.SaveSuccess = function(data, result, xhr) {
  if (data.redirect) {
    window.location.href = data.redirect;
  }
  else {
    this.file_id = data;
  }
}

dredit.Page.prototype.Save = function() {
  this.content = this.session.getValue();
  $.ajax({
      url: '/svc',
      type: 'PUT',
      type: this.IsNew() ? 'POST' : 'PUT',
      success: this.SaveSuccess,
      error: this.ServiceError,
      data: JSON.stringify(this.Read()),
      dataType: 'json',
      contentType: 'application/json',
      context: this
  });
}

dredit.Page.prototype.Read = function() {
  return {
      'content': this.content,
      'title': this.title,
      'description': this.description,
      'mimeType': this.mimetype,
      'resource_id': this.file_id
    };
}

dredit.Page.prototype.IsNew = function() {
  return (this.file_id == null);
}

dredit.Page.prototype.UpdateTitle = function(title) {
  this.title = title;
  this.label_text.text(title);
}



dredit.Editor = function(editor_id) {
  this.id = editor_id;
  this.editor = ace.edit(this.id);
}


dredit.Editor.prototype.SetMode = function(filetype) {
  var mode = require('ace/mode/' + filetype).Mode;
  this.editor.getSession().setMode(new mode());
}


dredit.SaveNotification = function() {
  $('#saving')
    .hide()  // hide it initially
    .ajaxStart(function() {
        $(this).show();
        //$('#save').hide();
        dredit.blocked = true;
    })
    .ajaxStop(function() {
        $(this).hide();
        dredit.blocked = false;
    });
}


dredit.TitleChanger = function(page) {
  this.page = page;
  this.title = null;
  this.CreateUi(page.title);
}


dredit.TitleChanger.prototype.Pop = function() {
  this.title.val(this.page.title || 'untitled.html');
  this.description.val(this.page.description);
  this.modal.modal('show');
  this.title.focus().select();
}


dredit.TitleChanger.prototype.Save = function() {
  this.page.UpdateTitle(this.title.val());
  this.page.description = this.description.val();
  this.modal.modal('hide');
  this.page.Save();
}


dredit.TitleChanger.prototype.CreateField = function(field, label) {
  return $('<div class="control-group"></div>')
      .append($('<label class="control-label"></label').text(label))
      .append($('<div class="controls"></div>')
        .append(field));
}


dredit.TitleChanger.prototype.CreateUi = function() {
  this.title = $('<input type="text" placeholder="Enter filename" name="title">');
  this.description = $('<textarea placeholder="Enter description"></textarea>');
  this.saver = $('<a href="#" class="btn btn-primary">Done</a>');
  this.modal = $('<div class="modal"></div>')
    .append(
      $('<div class="modal-header"></div>')
        .append($('<a class="close" data-dismiss="modal">close</a>'))
        .append($('<h3>Edit details</h3>')))
    .append(
      $('<div class="modal-body"></div>')
        .append($('<form class="form-horizontal">')
          .append(this.CreateField(this.title, 'Title'))
          .append(this.CreateField(this.description, 'Description'))))
    .append(
      $('<div class="modal-footer"></div>')
        .append(this.saver))

  this.saver.click($.proxy(this.Save, this));
}


function DrEdit() {
  this.view = new dredit.View();
  this.view.CreateUi();
  for (i in dredit.FILE_IDS) {
    this.view.CreatePage(dredit.FILE_IDS[i]);
  }
}


$(document).ready(function() {
  dredit.main = new DrEdit();
});
