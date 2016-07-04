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

var actId;
var errorActId;

/**
 * ファイルアップロード処理.
 */
function fileUpdateProcess(aId, eId, source, url) {
  actId = aId;
  errorActId = eId;
  if (source && url) {
    uploadFile(source, url, callBackUpload);
  }
}

/**
 * ファイルアップロードコールバック.
 */
function callBackUpload(res) {
  var json = JSON.parse(res);
  if (json['result'] == 'NG') {
    document.getElementById("form:" + errorActId).click();
    return;
  }

  for (var i = 0 ; i < json['fieldNames'].length ; i++) {
    var fieldName = json['fieldNames'][i];
    var no = fieldName.substring(fieldName.length-1);
    // importFile か attachment1～5のいずれかで呼ばれる
    if (isNaN(no)) {
      // 最後の1文字が数字でない
      no = "";
    } else {
      fieldName = fieldName.substring(0, fieldName.length-1);
    }
    document.getElementById("form:" + fieldName + "Key" + no).value = json['keys'][i];
    document.getElementById("form:" + fieldName + "Name" + no).value = json['names'][i];
    document.getElementById("form:" + fieldName + "Size" + no).value = json['fileSize'][i];
  }
  document.getElementById("form:" + actId).click();
}

var upload_no = 0;

/**
 *
 * @param source
 * @param url
 * @param callback
 */
function uploadFile(source, url, callback) {
  upload_no++;
  var iframe = $('<iframe name="upload_iframe_' + upload_no + '"/>');
  var div =$('<div style="position:absolute; top:0px; height:0px; width:0px; visibility:hidden;"></div>');
  var form = null;
  var inputs = null;
  source = $(source);
  if (source.is(":button")) {
    form = source.parents('form');
    inputs = form.find(':file');
  } else {
    inputs = source.filter(':file');
  }

  var newform = $('<form method="post" enctype="multipart/form-data" target="upload_iframe_' + upload_no + '" />');
  newform.attr("action", url);

  inputs.each(function() {
    $(this).after($(this).clone());
  });

  $('body').append(div);
  newform.append(inputs).appendTo(div);
  div.append(iframe);
  newform.submit(function() {
    iframe.load(function() {
      var res = iframe.contents().find('body').text();
      setTimeout(function() {
        if (callback) {
          callback(res);
        }
        div.remove();
      }, 0);
    });
  }).submit();
}

