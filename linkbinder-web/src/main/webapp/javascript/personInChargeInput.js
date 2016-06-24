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
 * 各要素のIDを表す定数
 */
var ID_DIALOG          = "editPic";           /* Dialogとして表示するdivエリア */
var ID_GROUP           = "editPicGroup";      /* 活動単位セレクトボックス */
var ID_INCSEARCH       = "editPicIncSearch";  /* インクリメンタルサーチテキストボックス */
var ID_CANDIDATE       = "editPicCandidate";  /* 選択候補ユーザーのセレクトボックス */
var ID_OK              = "editPicOk";         /* OKアクション */
var ID_CANCEL          = "editPicCancel";     /* キャンセルアクション */
var ID_DELETE          = "deletePic";         /* 削除アクション */
var ID_PIC_VALUES      = "editPicValues";     /* 選択値設定フィールド */
var ID_PIC_LABEL       = "picName";           /* 選択ユーザー設定フィールド */
var ID_PIC_LABEL_AREA  = "picNames";          /* 選択ユーザー設定フィールド表示領域 */

var WIDTH_ELEMENT = '360px'; /* 主要要素の幅 */
var SIZE_SELECT   = 15;      /* 選択候補の高さ */
var SIZE_FONT     = '12px';  /* フォントサイズ */
/**
 * コンストラクタ。
 */
function PersonInChargeInput() {
    this.initialize.apply(this, arguments);
}

/**
 * Person in Charge選択に必要なデータを設定する。
 * ここで設定された値が、各要素のvalue、 labelとして使われる。
 *
 * @param groups 活動単位
 * @param groupUserMappings 活動単位と、それに所属するユーザーの従業員番号
 * @param users Person in Chargeとして設定可能なユーザー
 */
PersonInChargeInput.setupDataSource = function(groups, groupUserMappings, users) {
    this.dsGroups            = groups;
    this.dsGroupUserMappings = groupUserMappings;
    this.dsUsers             = users;
};

/**
 * Person in Charge選択ポップアップダイアログを表示する。
 *
 * @param action ポップアップ起動アクションが発生した要素
 * @param key ポップアップの各要素を識別するためのキー値。
 *            画面上では複数のPerson in Charge選択ポップアップダイアログが
 *            表示される。各ダイアログを一意に識別するために必要
 */
PersonInChargeInput.showDialog = function(action, key) {
    var inputDialog = action.dialog;
    if (!inputDialog) {
        inputDialog = PersonInChargeInput.createPersonInChargeInput(action, key);
    }
    inputDialog.setup();
    inputDialog.show();
};

/**
 * 新しいPerson in Charge選択ポップアップダイアログを生成して返す。
 *
 * @param action ポップアップ起動アクションが発生した要素
 * @param key ポップアップの各要素を識別するためのキー値。
 *            画面上では複数のPerson in Charge選択ポップアップダイアログが
 *            表示される。各ダイアログを一意に識別するために必要
 *
 * @return 新しく生成されたPersonInChargeInputのインスタンス
 */
PersonInChargeInput.createPersonInChargeInput = function(action, key) {
    var d = new PersonInChargeInput(action, key);
    action.dialog = d;

    return d;
};

PersonInChargeInput.collectPicNameElements = function(key) {
    var result = new Array();
    var area = document.getElementById(ID_PIC_LABEL_AREA + key);
    if (!area) {
        return result;
    }

    if (area.childNodes.length == 0) {
        return result;
    }

    var func = function(children) {
        for (var i = 0; i < children.length; i++) {
            if (children[i].id && children[i].id.endsWith(ID_PIC_LABEL)) {
                result[result.length] = children[i];
            }
            if (children[i].hasChildNodes()) {
                func(children[i].childNodes);
            }
        }
    };
    func(area.childNodes);

    return result;
};

/**
 * 設定済のPerson in Chargeを削除する。
 *
 * @param action 削除アクションが発生した要素
 */
PersonInChargeInput.deletePic = function(action, key) {
    var baseId = action.id.substring(
                    0,
                    action.id.lastIndexOf(":") + 1)

    var values = document.getElementById(baseId + ID_PIC_VALUES);
    values.value = '';

    var labels  = PersonInChargeInput.collectPicNameElements();
    if (labels.length > 0) {
        for (var i = 0; i < labels.length; i++) {
            labels[i].innerHTML = ' ';
        }
    } else {
        document.getElementById(ID_PIC_LABEL_AREA + key).innerHTML = ' ';
    }
};

PersonInChargeInput.prototype = {
/**
 * ポップアップダイアログを初期化する。
 *
 * @param arguments[0] ポップアップ起動アクションが発生した要素
 * @param arguments[1] ポップアップの各要素を識別するためのキー値。
 *                     画面上では複数のPerson in Charge選択ポップアップダイアログが
 *                     表示される。各ダイアログを一意に識別するために必要
 */
initialize:
    function() {
        this.action = arguments[0];
        this.key    = arguments[1];

        this.div = this.createDiv(this.action, this.key);
        var baseId = this.action.id.substring(
                        0,
                        this.action.id.lastIndexOf(":") + 1)

        this.values = document.getElementById(baseId + ID_PIC_VALUES);
        this.labels  = PersonInChargeInput.collectPicNameElements(this.key);

        var parent = document.getElementById('content');
        parent.appendChild(this.div);
    },
/**
 * ポップアップダイアログを表すdiv要素を生成する。
 *
 * @param action ポップアップ起動アクションが発生した要素
 * @param key ポップアップの各要素を識別するためのキー値。
 *            画面上では複数のPerson in Charge選択ポップアップダイアログが
 *            表示される。各ダイアログを一意に識別するために必要
 *
 * @return 新しく生成したdiv要素
 */
createDiv:
    function(action, key) {
        var div = document.createElement('div');
        div.id = ID_DIALOG + key;
        div.className = 'dialog';
        div.style.position = "absolute";

        var pos = this.getPosition(action);
        div.style.top  = pos.y + "px";
        div.style.left = pos.x + "px";

        this.createContents(div, key);

        return div;
    },
/**
 * elementの絶対座標を返す。
 *
 * <pre>
 * var pos = getPosition(element);
 * alert(pos.x);
 * alert(pos.y);
 * </pre>
 * @param element 対象の要素
 * @return 絶対座標を表すオブジェクト。
 */
getPosition:
    function (element) {
        var e = element;
        var pos = new function(){ this.x = 0; this.y = 0; };
        while (e) {
            pos.x += e.offsetLeft;
            pos.y += e.offsetTop;
            e = e.offsetParent;
        }
        return pos;
    },
/**
 * ポップアップダイアログに表示する要素を生成する。
 *
 * @param div ポップアップダイアログを表すdiv要素
 * @param key ポップアップの各要素を識別するためのキー値。
 *            画面上では複数のPerson in Charge選択ポップアップダイアログが
 *            表示される。各ダイアログを一意に識別するために必要
 */
createContents:
    function(div, key) {
        var table = document.createElement('table');
        table.className = 'diaTbl';
        table.setAttribute('border', '0');
        table.setAttribute('cellpadding', '5');
        table.setAttribute('cellspacing', '3');

        var tbody = document.createElement('tbody');

        this.createLabel(tbody);
        this.createSelectGroup(tbody, key);
        this.createIncSearch(tbody, key);
        this.createSelectUsers(tbody, key);
        this.createActions(tbody, key);

        table.appendChild(tbody);
        div.appendChild(table);
    },
/**
 * ポップアップダイアログのタイトル(ラベル)を生成する。
 *
 * @param tbody ラベルの親要素となるtbody
 */
createLabel:
    function (tbody) {
        var row = document.createElement('tr');
        var cell = document.createElement('td');
        cell.className = 'label';
        // cell.style.fontSize = SIZE_FONT;

        var text = document.createTextNode('ユーザー:');
        cell.appendChild(text);

        row.appendChild(cell);
        tbody.appendChild(row);
    },
/**
 * ポップアップダイアログの活動単位セレクトボックスを生成する。
 *
 * @param tbody 親要素となるtbody
 * @param key ポップアップの各要素を識別するためのキー値。
 *            画面上では複数のPerson in Charge選択ポップアップダイアログが
 *            表示される。各ダイアログを一意に識別するために必要
 */
createSelectGroup:
    function (tbody, key) {
        var row = document.createElement('tr');
        var cell = document.createElement('td');

        this.selectGroup = document.createElement('select');
        this.selectGroup.id = ID_GROUP + key;
        this.selectGroup.style.width = WIDTH_ELEMENT;

        cell.appendChild(this.selectGroup);

        row.appendChild(cell);
        tbody.appendChild(row);

        var listener = function(me) {
            return function() { me.groupSelectionChanged(); };
        }(this);
        observe(this.selectGroup, 'change', listener);
    },
/**
 * ポップアップダイアログの
 * インクリメンタルサーチテキストボックスを生成する。
 *
 * @param tbody 親要素となるtbody
 * @param key ポップアップの各要素を識別するためのキー値。
 *            画面上では複数のPerson in Charge選択ポップアップダイアログが
 *            表示される。各ダイアログを一意に識別するために必要
 */
createIncSearch:
    function (tbody, key) {
        var row = document.createElement('tr');
        var cell = document.createElement('td');

        this.incSearch = document.createElement('input');
        this.incSearch.id = ID_INCSEARCH + key;
        this.incSearch.setAttribute('type', 'text');
        this.incSearch.style.width = WIDTH_ELEMENT;
        cell.appendChild(this.incSearch);

        row.appendChild(cell);
        tbody.appendChild(row);
    },
/**
 * ポップアップダイアログの
 * ユーザー選択リストボックスを生成する。
 *
 * @param tbody 親要素となるtbody
 * @param key ポップアップの各要素を識別するためのキー値。
 *            画面上では複数のPerson in Charge選択ポップアップダイアログが
 *            表示される。各ダイアログを一意に識別するために必要
 */
createSelectUsers:
    function (tbody, key) {
        var row = document.createElement('tr');
        var cell = document.createElement('td');

        this.candidate = document.createElement('select');
        this.candidate.id = ID_CANDIDATE + key;
        this.candidate.setAttribute('multiple', 'multiple');
        this.candidate.setAttribute('size', SIZE_SELECT);
        this.candidate.style.width = WIDTH_ELEMENT;
        this.candidate.setAttribute('class', 'user-list');
        cell.appendChild(this.candidate);

        row.appendChild(cell);
        tbody.appendChild(row);
    },
/**
 * ポップアップダイアログの
 * アクション(OK/Cancel)リンクを生成する。
 *
 * @param tbody 親要素となるtbody
 * @param key ポップアップの各要素を識別するためのキー値。
 *            画面上では複数のPerson in Charge選択ポップアップダイアログが
 *            表示される。各ダイアログを一意に識別するために必要
 */
createActions:
    function (tbody, key) {
        var row = document.createElement('tr');
        var cell = document.createElement('td');
        cell.setAttribute("align", "right");

        this.ok = document.createElement('a');
        this.ok.id = ID_OK + key;
        this.ok.href = "javascript:void(0)";
        this.ok.style.marginRight = '15px';
        this.ok.appendChild(document.createTextNode("OK"));
        var okListener = function(me) {
            return function() { me.performOk(); };
        }(this);
        observe(this.ok, 'click', okListener);
        cell.appendChild(this.ok);

        this.cancel = document.createElement('a');
        this.cancel.id = ID_CANCEL + key;
        this.cancel.href = "javascript:void(0)";
        this.cancel.style.marginRight = '15px';
        this.cancel.appendChild(document.createTextNode("閉じる"));
        var cancelListener = function(me) {
            return function() { me.performCancel(); };
        }(this);
        observe(this.cancel, 'click', cancelListener);
        cell.appendChild(this.cancel);

        row.appendChild(cell);
        tbody.appendChild(row);
    },
/**
 * ポップアップダイアログ表示設定を行う。
 */
setup:
    function() {
        //  活動単位セレクトボックスのoption要素を再生成
        this.clearSelectFields(this.selectGroup);
        this.fillSelectGroup();

        this.groupSelectionChanged();

        new SelectionIncSearch(this.incSearch.id, this.candidate.id);
    },
show:
    function() {
		/*
        var pos = DialogUtil.computePosition(this.action, this.div);
        //var pos = this.computePosition(this.action, this.div);
        this.div.style.top  = pos.y + "px";
        this.div.style.left = pos.x + "px";
        */

        Dialog.show(this.div.id);
    },
/**
 * PersonInChargeInput.dsGroupsに予め設定済の
 * 活動単位リストから、活動単位セレクトボックスに
 * option要素を設定する。
 */
fillSelectGroup:
    function () {
        var dsGroups = PersonInChargeInput.dsGroups;
        for (var i = 0; i < dsGroups.length; i++) {
            var data = dsGroups[i];
            if (!data) continue;

            var option = document.createElement('option');
            option.value = data['id'];
            option.text  = data['name'];

            if (document.all) {
                this.selectGroup.add(option);
            } else {
                this.selectGroup.appendChild(option);
            }
        }
    },
/**
 * valuesに指定された従業員番号リストから、
 * PersonInChargeInput.dsUsersに予め設定済の
 * ユーザー情報を取得しoption要素を設定する。
 *
 * @param values セレクトボックスに設定する従業員番号の配列
 */
fillSelectUserFields:
    function(values) {
        var dsUsers = PersonInChargeInput.dsUsers;
        for (var i = 0; i < values.length; i++) {
            var data = dsUsers[values[i]];
            if (!data) continue;

            var option = document.createElement('option');
            option.value = data['empNo'];
            option.text  = data['labelWithRole'];

            if (document.all) {
                this.candidate.add(option);
            } else {
                this.candidate.appendChild(option);
            }
        }
    },
/**
 * 活動単位セレクトボックスの値が変更された時の処理。
 * ユーザー選択セレクトボックスに表示するoption要素を
 * 再生成する。
 */
groupSelectionChanged:
    function(e) {
        var selected = this.selectGroup.value;
        this.clearSelectFields(this.candidate);

        var users = PersonInChargeInput.dsGroupUserMappings[selected];
        if (users) {
            this.fillSelectUserFields(users);
        }
    },
performOk:
    function(e) {
        var selected = new Array();
        for (var i = 0; i < this.candidate.options.length; i++) {
            if (this.candidate.options[i].selected) {
                selected[selected.length] = this.candidate.options[i].value;
            }
        }
        //  Person in Chargeに設定できるのは一人だけ
        if (selected.length != 1) {
            return;
        }

        var empNo = selected[0];
        var label = PersonInChargeInput.dsUsers[empNo]['label'];

        this.values.value     = empNo;
        if (this.labels.length > 0) {
            this.labels[0].innerHTML = label;
        } else {
            document.getElementById(ID_PIC_LABEL_AREA + this.key).innerHTML = label;
        }

        Dialog.close(this.div.id);
    },
performCancel:
    function(e) {
        Dialog.close(this.div.id);
    },
/**
 * セレクトボックスのoption要素を全て削除する。
 *
 * @param 対象となるセレクトボックス
 */
clearSelectFields:
    function(select) {
        while (select.options.length > 0) {
            select.removeChild(select.options[0]);
        }
    }
};

