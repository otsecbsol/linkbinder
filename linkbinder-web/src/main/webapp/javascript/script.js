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
/*
 * Firefoxでもwindow.eventでイベントオブジェクトが参照できるようにする
 */
var ie = document.all;
if (!ie) {
    (function() {
       for (var p in Event.prototype) {
         if (p.match(/MOUSE|CLICK|KEYUP/)) {
           window.addEventListener(p.toLowerCase(), function(e) {
             window.event = e;
           }, true);
         }
       }
     }());
}

/*
 * documentオブジェクトに現在のスクロール位置を返す関数を追加.
 *
 * = 使い方
 *   var scroll = document.scroll();
 *   scroll.x;
 *   scroll.y;
 *
 * @return 現在位置 x, y を持つオブジェクト.
 */
document.scroll = function() {
    return {
        x: this.body.scrollLeft || this.documentElement.scrollLeft,
        y: this.body.scrollTop  || this.documentElement.scrollTop
    };
};

/*
 * Arrayオブジェクトにメソッドを追加.
 *   include(v)  : 配列に v が含まれている場合はtrue
 */
Array.prototype.include = function(v) {
    for (var i in this) {
        if (this[i] == v) return true;
    }
    return false;
};

/*
 * Stringオブジェクトにメソッド追加.
 *   endsWith(suffix) : 文字列の末尾がsuffixであればtrue
 */
String.prototype.endsWith = function(suffix) {
    if (suffix.length > this.length) return false;
    return this.lastIndexOf(suffix) == (this.length - suffix.length);
};

/**
 * targetにイベントリスナを登録する
 *
 * @param target イベント登録対象のDOMオブジェクト
 * @param eventType 'click', 'keydown'などのイベント名。
 *                   'onclick'のように'on'を付けてはいけない
 * @param listener  イベントハンドラとして登録する関数
 */
function observe(target, eventType, listener) {
    if (target.attachEvent) {
        target.attachEvent('on' + eventType,
                function() { listener.call(target, window.event); });
    } else {
        target.addEventListener(eventType, listener, false);
    }
}

/*
 * イベントが発生した要素を返す.
 *
 * @return イベントが発生した要素
 */
function getTarget(e) {
    if (e) return e.target;

    if (window.event) {
        return (window.event.srcElement || window.event.target);
    }
    if (isFireFox()){
        return document.activeElement;
    }
    return null;
}

/*
 * サーバーで実行するアクションの名前を設定する.
 *
 * @param e イベントオブジェクト.
 *          IEの場合はundefinedなので、代わりに当関数内でwindow.eventを使用する
 */
function setActionName(e) {
    var target = getTarget(e);
    var form = _findParentForm(target);
    if (!form) {
        return false;
    }
    var action = _findAction(form);
    if (!action) {
        return false;
    }
    var action = _findAction(form);
    action.value = _getActionName(target);
    return true;
}

/*
 * ページング処理用：指定ページをセットする.
 */
function setPageNo(val) {
    document.getElementById('form:pageNo').value = val;
}

/*
 * 確認ダイアログを表示する.
 */
function confirmButton(msg) {
    return confirm(msg || '処理を続けます。よろしいですか？');
}

// TODO 不要？
 /*
  * 一覧用：ハイライト表示.
  */
 var currentRowClass = [];
 function highlight(rowNum, rowObject) {
     currentRowClass[rowNum] = rowObject.className;
     rowObject.className = 'highlight';
 }

// TODO 不要？
 /*
  * 一覧用：ハイライトから戻す.
  */
 function returnColor(rowNum, rowObject) {
    rowObject.className = currentRowClass[rowNum];
 }

 /*
  * 指定IDのコンポーネントをクリックする
  */
function clickComponent(actId){
  var obj = document.getElementById("form:" + actId);
  if(obj.tagName == 'INPUT') {
    obj.click();
  } else if(obj.tagName == 'A') {
    obj.onclick();
  }
}
/*
 * UserAgentに指定された文字列が含まれているかを判別する.
 */
function isFireFox() {
    var ua = window.navigator.userAgent.toLowerCase();
    if (ua.indexOf('firefox') != -1) {
        return true;
    }
    return false;
}
/*
 * アクション名をセットする.
 */
function setFixedActionName(name) {
    var action = document.getElementById('form:actionName');
    if (action) {
        action.value = name;
    }
}

/*
 * パスワード忘れリンク
 */
function openForgetPassWindow(){
    var strWinName = "ForgotPassword";
    strWinName += Math.round(Math.random()*100000000);
    sList = window.open("http://hostname/path/to/forgetPass.jsp",
    		            strWinName,
    		            "width=530,height=230,left=300,top=260,hotkeys=no");
    sList.focus();
}

/*
 * -------------------------------------------------------
 * 以下、private関数
 * 外部から呼び出された場合の動作は保証しません。
 * -------------------------------------------------------
 */
function _findParentForm(srcElement) {
    for (var e = srcElement.parentNode; e != null; e = e.parentNode) {
        if (e.id && e.id.toLowerCase() == 'form') {
            return e;
        }
    }
    return null;
}

function _findAction(form) {
    var formId = form.id;
    return document.getElementById(formId + ':actionName');
}

function _getActionName(srcElement) {
    var tagName = srcElement.tagName.toLowerCase();
    if (tagName == 'input') {
        return srcElement.value;
    }
    if (tagName == 'a') {
        return srcElement.innerHTML;
    }
    return null;
}

/*
 * 指定したエレメント内のすべてのリンクを無効にする
 * 二度押し防止用
 */
function deleteAllLink(id){
  var node = document.getElementById(id);
  var search = function (node)
  {
      while (node != null)
      {
          // 自分を処理
          if(node.tagName === void(0)){
            // ignore
          } else if (node.tagName == 'A') {
            node.removeAttribute('href');
            node.setAttribute('onclick', new Function(""));
          }
          // 子供を再帰
          search(node.firstChild);
          // 次のノードを探査
          node = node.nextSibling;
      }
  }
  search(node.firstChild);
}

/*
 * 画面上のLink,Buttonの
 * Disable制御処理
 * Submitの際、各Button、LinkをDisableにします。
 */
//var disableFlag = false;
//var skipDisable = false;
//var ajaxRequest = false;
function disableSubmit() {
    return true;
//    if (skipDisable) {
//        skipDisable = false;
//        return true;
//    }
//
//    if (ajaxRequest) {
//        ajaxRequest = false;
//        return disableActionForAjax();
//    } else {
//        return disableAction();
//    }
}

//  未使用
function disableAction() {
  return true;
//    var target = getTarget();
//    if (target) {
//        var actionName = _getActionName(target);
//        if (actionName != null) {
//            var name = actionName.toLowerCase();
//
//            if (name == 'print' || name == 'csv' || name == 'excel' || name == 'zip') {
//                return true;
//            }
//        }
//    }
//
//    if (disableFlag) {
//        return false;
//    }
//
//    var obj = document.getElementsByTagName('A');
//    for(var i = 0; obj.length>i; i++ ) {
//        obj[i].removeAttribute('href');
//        obj[i].onclick = null;
//    }
//    return checkButton();
}

// 属性キャッシュ用配列
var actions = new Array();
function disableActionForAjax() {
    if (disableFlag) {
        return false;
    }

    var header = document.getElementById('header');
    var headLinks = header.getElementsByTagName('A');
    var obj = document.getElementsByTagName('A');
    for(var i = 0; obj.length>i; i++ ) {
        var find = false;
        for (var j = 0; headLinks.length>j; j++) {
            if (obj[i].id == headLinks[j].id) {
                find = true;
                break;
            }
        }

        if (!find) {
            // 属性をキャッシュしておく
            actions[obj[i].id] = new Object();
            actions[obj[i].id].href = obj[i].getAttribute('href');
            actions[obj[i].id].onclick = obj[i].onclick;
            obj[i].removeAttribute('href');
            obj[i].onclick = null;
        }
    }
    return checkButton();
}

function enableAction() {
    var obj = document.getElementsByTagName('A');
    for(var i = 0; obj.length>i; i++ ) {
        // hrefがセットされている要素はrerenderされた可能性があるためスキップする
        if (actions[obj[i].id] && !obj[i].getAttribute('href')) {
            obj[i].setAttribute('href', actions[obj[i].id].href);
            obj[i].onclick = actions[obj[i].id].onclick;
            actions[obj[i].id] = null;
        }
    }
    // 復元したら初期化する(1回のみ使用)
    actions = new Array();
    disableFlag = false;
}

function setSkipDisable(val) {
    skipDisable = val;
}
function setAjaxRequest(val) {
    ajaxRequest = val;
}
function disableLink(url) {
    disableSubmit();
    if (url) {
      location.href = url;
    }
}
function checkButton() {
    if (!disableFlag) {
        disableFlag = true;
        return true;
    } else {
        return false;
    }
}

/*
 * 入力値を大文字に変換する。
 * @param e keyupイベントオブジェクト
 */
function convertToUpperCase(e) {
    var ctrl;
    var keycode;

    if (e != null) {
        keycode = e.keyCode;
        ctrl = e.ctrlKey;
    }

    if (ctrl) return;
    if (keycode >= 65 && keycode <= 90) {
        var text = getTarget();
        toUpper(text);
    }
}

/*
 * 値を大文字に変換する。
 * @param text テキストボックスオブジェクト
 */
function toUpper(text) {
   if (text.value != "") {
        text.value = text.value.toUpperCase();
    }
}

/**
 * ログアウト確認メッセージを出力しログアウト可否を取得する.
 * @return true:(ログアウト)
 */
function isLogout() {
    var message = 'ログアウトしますか？';
    return confirm(message);
}

/**
 * 一覧表示のホバー時の背景色を変更する.
 * @param targetId
 */
$.fn.highlightRows = function(highlight, normal) {
	var selector = $(this).selector;

	$(this).delegate('tr', 'mouseover', highlight ? highlight : function() { selectRow(this); });
	$(this).delegate('tr', 'mouseout',  normal  ? normal  : function() { leaveRow(this); });

	if (typeof jsf == 'undefined' || !jsf || !jsf.ajax) return;
	//  JSFの非同期処理終了時に再バインド
	jsf.ajax.addOnEvent(function(event) {
		if (!event || !event.source) {
			return;
		}
		switch (event.status) {
		case 'success' :
			$(selector).undelegate('tr', 'mouseover');
			$(selector).undelegate('tr', 'mouseout');
			$(selector).delegate('tr', 'mouseover', highlight ? highlight : function() { selectRow(this); });
			$(selector).delegate('tr', 'mouseout',  normal  ? normal  : function() { leaveRow(this); });
			break;
		}
	});
}

/**
* DataTable選択行の背景色を変更する。
 * @param row
 * @returns
 */
function selectRow(row) {
	row.classNameOrg = row.classNameOrg || row.className;
	if (row.classNameOrg != 'undefined' || row.classNameOrg) {
		row.className = 'highlight';
	}
}

/**
 * DataTable選択行の背景色を元に戻す。
 * @param row
 * @returns
 */
function leaveRow(row) {
	if (row.classNameOrg) {
		row.className = row.classNameOrg;
	}
}


/**
 * jQuery.datepickerの第n週判定関数の改良版。
 * @param a
 */
function iso8601Week_mod(a) {
  a = new Date(a.getTime());
  //  日曜日は1として計算
  a.setDate(a.getDate() + 4 - ((a.getDay() || a.getDay() + 1) || 7));
  var b = a.getTime();
  a.setMonth(0);
  a.setDate(1);
  return Math.floor(Math.round((b - a) / 864e5) / 7) + 1;
}




// 以下、二重submit防止関連の処理
/**
 * 画面上の全てのsubmitボタンを無効化する.
 */
function disableSubmitButtons() {
  $('input[type=submit]').each(function() {
    if ($(this).attr('disabled')) {
      $(this).attr('disabledbackup', $(this).attr('disabled'));
    }
    $(this).attr('disabled', 'disabled');
  });
}

/**
 * 画面上の全てのsubmitボタンを{@link #disableSubmitButtons()}
 * 呼び出し前の状態に戻す.
 */
function enableSubmitButtons() {
  $('input[type=submit]').each(function() {
    if ($(this).attr('disabledbackup')) {
      $(this).attr('disabled', $(this).attr('disabledbackup'));
    } else {
      $(this).removeAttr('disabled');
    }
  });
}

function initSubmitButtons() {
  $('input[type=submit]').each(function() {
    //  以前の設定があれば適用
    //  Firefoxでの[戻る]対応
    if ($(this).attr('disabledbackup')) {
      $(this).attr('disabled', $(this).attr('disabledbackup'));
    }
  });
}
/**
 * 画面上の全ての<a>タグを無効にする.
 *
 * FIXME: 現在はhref属性を取り除いているだけなので
 * 見た目上は無効化されているように見えるが、onclickイベント
 * が有効なリンクの場合は無効化できていない.
 */
$.fn.disableLinks = function() {
  $('a', $(this)).each(function() {
    $(this).attr('hrefbackup', $(this).attr('href'));
    $(this).removeAttr('href');
  });
};
/**
 * 画面上の全ての<a>タグを{@link disableLinks()}呼び出し前の状態に戻す.
 */
$.fn.enableLinks = function() {
  $('a', $(this)).each(function() {
    $(this).attr('href', $(this).attr('hrefbackup'));
    $(this).removeAttr('hrefbackup');
  });
};

/**
 * 画面上の全てのinput[type=button]を無効化する.
 */
function disableTypeButtons() {
  $('input[type=button]').each(function() {
    if ($(this).attr('disabled')) {
      $(this).attr('disabledbackup', $(this).attr('disabled'));
    }
    $(this).attr('disabled', 'disabled');
  });
}
/**
 * 画面上の全てのinput[type=button]を{@link disableTypeButtons()}呼び出し前の状態に戻す.
 */
function enableTypeButtons() {
  $('input[type=button]').each(function() {
    if ($(this).attr('disabledbackup')) {
      $(this).attr('disabled', $(this).attr('disabledbackup'));
    } else {
      $(this).removeAttr('disabled');
    }
  });
}

/**
 * JSF-AjaxのSubmitイベント発生時にボタン・リンクを無効にする.
 * JQuery、JSF2(Mojarra)に依存.
 *
 * https://javaserverfaces.dev.java.net/nonav/docs/2.0/jsdocs/symbols/jsf.ajax.html#.addOnEvent
 */
$(document).ready(function() {
  if (typeof jsf == 'undefined' || !jsf || !jsf.ajax) return;
  jsf.ajax.addOnEvent(function(event) {
    if (!event || !event.source) {
      return;
    }
    switch (event.status) {
    case 'begin' :
      //  連打防止
      if ($('#form').attr('submitting') == event.source.id) {
        return;
      }
      $('#form').attr('submittiing', event.source.id);

      disableSubmitButtons();
      disableTypeButtons();
      $('#content').disableLinks();
      break;
    case 'complete' :
      $('#content').enableLinks();
      enableSubmitButtons();
      enableTypeButtons();
      $('#form').removeAttr('submitting');
      break;
    }
  });
});

function removeSubmitHidden(form) {
  $('input[type=submit]', form).each(function() {
    var $button = $(this);
    if ($button.attr('name')) {
      $('input[type=hidden][name=' + $button.attr('name') + ']').remove();
    }
  });
}
/**
 * formのsubmitイベント発生時にボタン・リンクを無効にする.
 * JQueryに依存.
 */
$.fn.disableOnSubmit = function() {
  var $form = $(this);
  $form.removeAttr('submitting');
  initSubmitButtons();

  $form.submit(function(e) {
    if ($(this).attr('submitting')) {
      return false;
    } else {
      $(this).attr('submitting', true);
      // すこし遅れてボタン・リンクを無効にする
      // (アクションが発生した "name = value" をサーバーに送信するため)
      setTimeout(function() {
        disableSubmitButtons();
        disableTypeButtons();
        $('#content').disableLinks();
      }, 10);
    }
  });
};
$(document).ready(function() {
  $('#form').disableOnSubmit();
});
$(window).unload(function() {
  $('#form').removeAttr('submitting');
  $('#content').enableLinks();
  enableSubmitButtons();
  enableTypeButtons();
});


function getJsonValues(id) {
    var json = $(id).html();
    return $.parseJSON(json);
}

$(document).ready(function() {
    //input 要素 内 でのEnter押下時 送信の抑止
    $("body").on("keypress", "input:not(.allow_submit)", function(event) {
        return event.which !== 13;
    });
});

// メニューの開閉
$(document).ready(function() {
    // 設定
    $("#toggle-config").click(function(){
      $("#config").slideToggle(100);
      return false;
    });
    $(window).resize(function(){
      var win = $(window).width();
      var p = 480;
      if(win > p){
        $("#config").show();
      } else {
        $("#config").hide();
      }
    });
    // メインメニュー
    $("#toggle-menu").click(function(){
      $("#main-menu").slideToggle(100);
      return false;
    });
    $(window).resize(function(){
      var win = $(window).width();
      var p = 480;
      if(win > p){
        $("#main-menu").show();
      } else {
        $("#main-menu").hide();
      }
    });
});
