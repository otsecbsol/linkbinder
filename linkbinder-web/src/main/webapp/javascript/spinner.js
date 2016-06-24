/*
 * Copyright 2016 OPEN TONE Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * 画面中央に表示される巨大なスピナ.
 */
var Spinner = {};
Spinner.onStatusChange = function(data) {
  //  イベントの発生元
  var s = $(data.source);

  var spinner = $('#spinner');
  var win= $(window);

  var left = win.scrollLeft() + ((win.width()  - spinner.width()) / 2);
  var top  = win.scrollTop()  + ((win.height() - spinner.height()) / 2);
  if (left < 0) left = 0;
  if (top < 0)  top  = 0;

  var zIndex = '200'; // ダイアログより前面
  spinner.css({'top': top, 'left': left, 'z-index': zIndex});
  var status = data.status;
  if (status == 'begin') {
    spinner.show();
  } else {
    spinner.hide();
  }
};

/**
 * posで指定した要素の右側にスピナを表示する.
 * アップロードなど処理の開始が非同期でない場合に使用する.
 */
Spinner.show = function(pos) {
  // spinnerの表示位置を決定
  var s = $(pos);
  var spinner = Spinner.getSpinner(s);
  if (spinner) {
      spinner.show();
  }
};

/**
 * スピナを消す.
 */
Spinner.hide = function(pos) {
  $('#spinner').hide();
};

/**
 * 非同期処理が終了した時点でスピナを消す.
 * showとセットで使用することを前提にしている.
 */
Spinner.hideOnCompletion = function(data) {
  if (data.status != 'begin') {
    $('#spinner').hide();
  }
};

Spinner.getSpinner = function(source) {
    // spinnerの表示位置を決定
    var offset = source.offset();
    if (offset == null) {
        return null;
    } else {
        var top = offset.top + "px";
        var left = (offset.left + source.outerWidth() + 5) + "px";
        var zIndex = "200"; // ダイアログより前面

        //  Ajaxイベントの状態に応じてspinnerの表示/非表示を切り替え
        return $('#spinner').css({top: top, left: left, "z-index": zIndex});
    }
};

/**
 * イベントの発生元の隣に表示する小さいスピナ.
 */
var SmallSpinner = {};
SmallSpinner.onStatusChange = function(data) {
  //  イベントの発生元
  var s = $(data.source);

  // spinnerの表示位置を決定
  var offset = s.offset();
  if (offset == null) {
    return;
  } else {
    var top = offset.top + "px";
    var left = (offset.left + s.outerWidth() + 5) + "px";
    var zIndex = '200'; // ダイアログより前面

    //  Ajaxイベントの状態に応じてspinnerの表示/非表示を切り替え
    var spinner = $('#spinner-small').css({top: top, left: left, "z-index": zIndex});
    var status = data.status;
    if (status == 'begin') {
      spinner.show();
    } else {
      spinner.hide();
    }
  }
};

/**
 * posで指定した要素の右側にスピナを表示する.
 * アップロードなど処理の開始が非同期でない場合に使用する.
 */
SmallSpinner.show = function(pos) {
  // spinnerの表示位置を決定
  var s = $(pos);
  var spinner = SmallSpinner.getSpinner(s);
  if (spinner) {
      spinner.show();
  }
};

/**
 * スピナを消す.
 */
SmallSpinner.hide = function(pos) {
  $('#spinner-small').hide();
};

SmallSpinner.getSpinner = function(source) {
  // spinnerの表示位置を決定
  var offset = source.offset();
  if (offset == null) {
    return;
  } else {
    var top = offset.top + "px";
    var left = (offset.left + source.outerWidth() + 5) + "px";
    var zIndex = '200'; // ダイアログより前面

    return $('#spinner-small').css({top: top, left: left, "z-index": zIndex});
  }
};
