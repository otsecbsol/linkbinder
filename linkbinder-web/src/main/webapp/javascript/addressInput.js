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
var ID_DIALOG          = "editAddress";             /* Dialogとして表示するdivエリア */
var ID_GROUP           = "editAddressGroup";        /* 活動単位セレクトボックス */
var ID_INCSEARCH       = "editAddressIncSearch";    /* インクリメンタルサーチテキストボックス */
var ID_CANDIDATE       = "editAddressCandidate";    /* 選択候補ユーザーのセレクトボックス */
var ID_SELECTED        = "editAddressSelected";     /* 選択候補ユーザーのセレクトボックス */
var ID_OK              = "editAddressOk";           /* OKアクション */
var ID_CANCEL          = "editAddressCancel";       /* キャンセルアクション */
var ID_ADD             = "editAddressAdd";          /* ADDアクション */
var ID_REMOVE          = "editAddressRemove";       /* REMOVEアクション */
var ID_LOADER          = "ajax-loader-";            /* 非同期処理中を表す画像 */
var ID_USER_INI        = "editAddressIni";          /* 頭文字のリンク(User) */
var ID_ATTENTION_INI   = "editAddressAttentionIni"; /* 頭文字のリンク(Attention) */

var LABEL_TO                 = "グループ:";
var LABEL_CC                 = "グループ:";
var LABEL_USER               = "ユーザー:";
var LABEL_SELECTED_ATTENTION = "To:";
var LABEL_SELECTED_CC        = "Cc:";
var INITIAL_SPACER           = "  ";

/* 編集モード */
var NEW    = "NEW";
var UPDATE = "UPDATE";
var DELETE = "DELETE";
var NONE   = "NONE";

/* 定数 */
var DISPLAY_LABEL_KEY   = "labelWithRole";  /* 画面表示用ラベルを取得するためのキー */
var EMP_NO_KEY          = "empNo";          /* ユーザーを識別する社員番号を取得するためのキー */
var WIDTH_ELEMENT       = '420px';          /* 主要要素の幅 */
var SIZE_SELECT         = 15;               /* 選択候補・選択済リストの高さ */
var SIZE_FONT           = '12px';           /* フォントサイズ */
var INITAL_ALL          = "*";              /* 「すべて」を表すイニシャル */
var MODE_USER_INI       = "userIni";        /* Userのイニシャルを押した時を表す */
var MODE_ATTENTION_INI  = "attentionIni";   /* Attentionのイニシャルを押した時を表す */
var JOIN_NO_GROUP       = "-1";             /* グループに所属していないことを表す */
var USER_KEY_DELIMITER  = "-";              /* ユーザーIDとグループIDをつなぐ区切り文字 */

/* イニシャルサーチが発生するアクションの定義 */
var INI_GROUP_CHANGE           = "groupChange";
var INI_USER_INI_CHANGE        = "userIniChange";
var INI_ATTENTION_INI_CHANGE   = "attentionIniChange";
var INI_USER_GROUP_CHANGE      = "userGroupChange";
var INI_ATTENTION_GROUP_CHANGE = "attentionGroupChange";

/**
 * コンストラクタ。
 */
function AddressInput() {
    this.initialize.apply(this, arguments);
}

/**
 * 宛先編集に必要なデータを設定する。
 * ここで設定された値が、各要素のvalue、 labelとして使われる。
 *
 * @param groups 活動単位
 * @param groupUserMappings 活動単位と、それに所属するユーザーの従業員番号
 * @param users Person in Chargeとして設定可能なユーザー
 * @param updateModes 編集モード定義
 * @param isReply 返信文書の場合はtrue
 */
AddressInput.setupDataSource = function(groups,
                                        groupUserMappings,
                                        users,
                                        updateModes,
                                        isReply) {

    this.dsGroups            = groups;
    this.dsGroupUserMappings = groupUserMappings;
    this.dsUsers             = users;
    this.updateModes         = updateModes;
    this.isReply             = isReply;
};

/**
 * 宛先編集ポップアップダイアログを表示する。
 *
 * @param options 設定パラメーター
 *       【必須】
 *       ---------------------------------------------------------------------------
 *       action          : ポップアップ起動アクションが発生した要素
 *       tableId         : 設定済宛先情報を表示するtableタグのID
 *       key             : ポップアップの各要素を識別するためのキー値。
 *                         画面上では複数のPerson in Charge選択ポップアップダイアログが
 *                         表示される。各ダイアログを一意に識別するために必要
 *       to              : To/Ccのいずれの宛先を編集するか。trueの場合はTo、falseの場合はCc
 *       okButtonId      : OKボタンクリックした際に裏でクリックする隠しボタンのID
 *       selectedGroupId : ダイアログで選択したグループIDを保持する要素
 *       selectedUserId  : ダイアログで選択したユーザーIDを保持する要素
 *       ---------------------------------------------------------------------------
 *       【任意】
 *       selectedGroupElement  : 宛先に既に設定済の活動単位IDを格納した要素。
 *                               empNo1,empNo2,empNo3...の形式
 *                               'Edit'の場合は必須
 *       selectedValuesElement : 宛先に既に設定済のユーザーの従業員番号を格納した要素。
 *                               empNo1,empNo2,empNo3...の形式
 *                               'Edit'の場合は必須
 */
AddressInput.showDialog = function(options) {
    var inputDialog = options['action'].dialog;
    if (!inputDialog) {
        inputDialog = AddressInput.createAddressInput(options);
    }
    inputDialog.setup();
    inputDialog.show();
};

/**
 * 新しい宛先編集ポップアップダイアログを生成して返す。
 *
 * @param options 設定パラメーター。AddressInput.showDialogを参照のこと。
 *
 * @return 新しく生成されたAddressInputのインスタンス
 */
AddressInput.createAddressInput = function(options) {
    var d = new AddressInput(options);
    d.action.dialog = d;

    return d;
};

AddressInput.prototype = {
/**
 * ポップアップダイアログを初期化する。
 *
 * @param options 設定パラメーター。AddressInput.showDialogを参照のこと。
 */
initialize:
    function(options) {
        this.action               = options['action'];
        this.tableId              = options['tableId'];
        this.key                  = options['key'];
        this.to                   = options['to'];
        this.okButtonId           = options['okButtonId'];
        this.selectedGroupId      = options['selectedGroupId'];
        this.selectedUserId       = options['selectedUserId'];
        this.selectedValuesElement= options['selectedValuesElement'];
        this.selectedGroupElement = options['selectedGroupElement'];
        this.isAdd                = options['add'];

        this.isNew                 = true;
        this.selectedValuesElement = null;
        if (options['selectedValuesElement']) {
            this.selectedValuesElement = options['selectedValuesElement'];
            this.isNew = false;
        }

        this.startIncSearch = false;
        this.div = this.createDiv(this.action, this.tableId, this.key);
        var parent = document.getElementById('content');
        parent.appendChild(this.div);

        this.groupAll = AddressInput.dsGroups[0]['id'];
        this.initSelectUsers();
    },
/**
 * 選択済ユーザーマップ情報を初期化する。
 */
initSelectUsers:
    function() {
        this.dsSelectUsers = new Array();
        for (var i = 0; i < AddressInput.dsGroups.length; i++) {
            this.dsSelectUsers[AddressInput.dsGroups[i]['id']] = new Array();
        }
    },
/**
 * ポップアップダイアログを表すdiv要素を生成する。
 *
 * @param action ポップアップ起動アクションが発生した要素
 * @param tableId このダイアログを起動したテーブルID
 * @param key ポップアップの各要素を識別するためのキー値。
 *            画面上では複数のPerson in Charge選択ポップアップダイアログが
 *            表示される。各ダイアログを一意に識別するために必要
 *
 * @return 新しく生成したdiv要素
 */
createDiv:
    function(action, tableId, key) {
        var id = ID_DIALOG + tableId + key;
        var div = document.getElementById(id);
        if (!div) {
            div = document.createElement('div');
            div.id = ID_DIALOG + tableId + key;
            div.className = 'dialog';
            div.style.position = "absolute";
        } else {
            // 作りなおすため既存のテーブルを削除しておく
            div.removeChild(div.firstChild);
        }

        this.createContents(div, key);
        return div;
    },
/**
 * ポップアップダイアログに表示する要素を生成する。
 *
 * @param div ポップアップダイアログを表すdiv要素
 * @param key ポップアップの各要素を識別するためのキー値。
 *            画面上では複数の宛先編集ポップアップダイアログが
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

        this.createSelectAddressGroup(tbody, key);
        this.createLabel(tbody, key);
        this.createSelectGroup(tbody, key);
        this.createIncSearch(tbody, key);
        this.createSelectUsers(tbody, key);
        this.createActions(tbody, key);

        table.appendChild(tbody);
        div.appendChild(table);
    },
/**
 * ポップアップダイアログの宛先選択を促すメッセージを生成する。
 *
 * @param tbody 親要素となるtbody
 */
createSelectAddressGroupMessage:
    function(tbody) {
        var row = document.createElement('tr');
        var cell = document.createElement('td');
        cell.setAttribute("colSpan", "3");

        var span = document.createElement('span');
        span.className = 'info';
        // span.style.fontSize = SIZE_FONT;

        var name = this.to ? LABEL_TO : LABEL_CC;
        name = name.replace(/:\s*$/, "");

        var msg = "プルダウンから" + name + "を選択してください。";
        var text = document.createTextNode(msg);
        span.appendChild(text);

        if (this.isAdd) {
            var br = document.createElement('br');
            span.appendChild(br);

            var caution = document.createElement('span');
            caution.className = 'error';
            span.appendChild(caution);

            msg = "";
            text = document.createTextNode(msg);
            caution.appendChild(text);

            br = document.createElement('br');
            caution.appendChild(br);
            msg = "";
            text = document.createTextNode(msg);
            caution.appendChild(text);
        }

        var ul = document.createElement('ul');
        var li = document.createElement('li');
        li.appendChild(span);
        ul.appendChild(li);
        cell.appendChild(ul);

        row.appendChild(cell);
        tbody.appendChild(row);
    },
/**
 * ポップアップダイアログの宛先-活動-単位セレクトボックスを生成する。
 *
 * @param tbody 親要素となるtbody
 * @param key ポップアップの各要素を識別するためのキー値。
 *            画面上では複数の宛先編集ポップアップダイアログが
 *            表示される。各ダイアログを一意に識別するために必要
 */
createSelectAddressGroup:
    function (tbody, key) {
        this.createSelectAddressGroupMessage(tbody);

        var row = document.createElement('tr');
        var cell = document.createElement('td');
        cell.setAttribute("colSpan", "3");
        cell.className = 'label';
        // cell.style.fontSize = SIZE_FONT;

        var label = document.createElement('span');
        label.className = 'label';
        label.appendChild(document.createTextNode(this.to ? LABEL_TO : LABEL_CC));
        cell.appendChild(label);

        if (!this.isAdd) {
            var required = document.createElement('span');
            required.className = 'necessary';
            required.appendChild(document.createTextNode('*'));
            cell.appendChild(required);
        }

        this.selectGroup = document.createElement('select');
        this.selectGroup.id = ID_GROUP + key;
        this.selectGroup.style.width = WIDTH_ELEMENT;
        this.selectGroup.style.marginLeft = '5px';
        cell.appendChild(this.selectGroup);

        row.appendChild(cell);
        tbody.appendChild(row);

        var listener = function(me, sel) {
            return function() { me.groupSelectionChanged(sel); };
        }(this, this.selectGroup);
        observe(this.selectGroup, 'change', listener);

        row  = document.createElement('tr');
        cell = document.createElement('td');
        cell.setAttribute("colSpan", "4");
        var hr = document.createElement('hr');
        cell.appendChild(hr);

        row.appendChild(cell);
        tbody.appendChild(row);
    },
/**
 * ポップアップダイアログの入力欄ラベルを生成する。
 *
 * @param tbody 親要素となるtbody
 * @param key ポップアップの各要素を識別するためのキー値。
 *            画面上では複数の宛先編集ポップアップダイアログが
 *            表示される。各ダイアログを一意に識別するために必要
 */
createLabel:
    function (tbody, key) {
        var row, cell, text, action, listener, div, td, tr, tbl, tblBody, label;

        row = document.createElement('tr');

        cell = document.createElement('td');
        // cell.className = "label";
        // cell.style.fontSize = SIZE_FONT;

        tbl = document.createElement('table');
        tbl.setAttribute("width", "100%");
        tbl.setAttribute("border", "0");
        tblBody = document.createElement('tbody');;
        tr = document.createElement('tr');
        td = document.createElement('td');
        td.setAttribute("vAlign", "top");

        label = document.createElement('span');
        label.className = 'label';
        label.appendChild(document.createTextNode(LABEL_USER));
        td.appendChild(label);
        tr.appendChild(td);

        td = document.createElement('td');

        var actionNameHidden = document.createElement("input");
        actionNameHidden.setAttribute("type", "hidden");
        actionNameHidden.setAttribute("id", "actionNameHidden" + key);
        td.appendChild(actionNameHidden);
        this.actionNameHidden = actionNameHidden;

        this.createInitialUserLabel(INITAL_ALL, key, td);
        this.createInitialUserLabel('A', key, td);
        this.createInitialUserLabel('B', key, td);
        this.createInitialUserLabel('C', key, td);
        this.createInitialUserLabel('D', key, td);
        this.createInitialUserLabel('E', key, td);
        this.createInitialUserLabel('F', key, td);
        this.createInitialUserLabel('G', key, td);
        this.createInitialUserLabel('H', key, td);
        this.createInitialUserLabel('I', key, td);
        this.createInitialUserLabel('J', key, td);
        this.createInitialUserLabel('K', key, td);
        this.createInitialUserLabel('L', key, td);
        this.createInitialUserLabel('M', key, td);
        this.createInitialUserLabel('N', key, td);

        var br = document.createElement('br');
        td.appendChild(br);

        var spacer1 = document.createElement("span");
        spacer1.appendChild(document.createTextNode('　'));
        td.appendChild(spacer1);

        this.createInitialUserLabel('O', key, td);
        this.createInitialUserLabel('P', key, td);
        this.createInitialUserLabel('Q', key, td);
        this.createInitialUserLabel('R', key, td);
        this.createInitialUserLabel('S', key, td);
        this.createInitialUserLabel('T', key, td);
        this.createInitialUserLabel('U', key, td);
        this.createInitialUserLabel('V', key, td);
        this.createInitialUserLabel('W', key, td);
        this.createInitialUserLabel('X', key, td);
        this.createInitialUserLabel('Y', key, td);
        this.createInitialUserLabel('Z', key, td);

        tr.appendChild(td);
        tblBody.appendChild(tr);
        tbl.appendChild(tblBody);
        cell.appendChild(tbl);

        div = document.createElement('div');
        div.style.textAlign = 'right';
        div.style.styleFloat = 'right';

        action = document.createElement('a');
        action.id = "addAll" + key;
        action.href = "javascript:void(0);";
        text = document.createTextNode('すべて追加');
        action.appendChild(text);

        listener = function(me) {
            return function() { me.addAll(); };
        }(this);
        action.onclick = listener;

        div.appendChild(action);
        cell.appendChild(div);

        row.appendChild(cell);

        cell = document.createElement('td');
        row.appendChild(cell);

        cell = document.createElement('td');
        // cell.className = "label";
        // cell.style.fontSize = SIZE_FONT;

        tbl = document.createElement('table');
        tbl.setAttribute("width", "100%");
        tbl.setAttribute("border", "0");
        tblBody = document.createElement('tbody');;
        tr = document.createElement('tr');
        td = document.createElement('td');
        td.setAttribute("vAlign", "top");

        label = document.createElement('span');
        label.className = 'label';
        text = document.createTextNode(
                this.to ? LABEL_SELECTED_ATTENTION : LABEL_SELECTED_CC);
        label.appendChild(text);
        td.appendChild(label);
        tr.appendChild(td);

        td = document.createElement('td');

        this.createInitialAttentionLabel(INITAL_ALL, key, td);
        this.createInitialAttentionLabel('A', key, td);
        this.createInitialAttentionLabel('B', key, td);
        this.createInitialAttentionLabel('C', key, td);
        this.createInitialAttentionLabel('D', key, td);
        this.createInitialAttentionLabel('E', key, td);
        this.createInitialAttentionLabel('F', key, td);
        this.createInitialAttentionLabel('G', key, td);
        this.createInitialAttentionLabel('H', key, td);
        this.createInitialAttentionLabel('I', key, td);
        this.createInitialAttentionLabel('J', key, td);
        this.createInitialAttentionLabel('K', key, td);
        this.createInitialAttentionLabel('L', key, td);
        this.createInitialAttentionLabel('M', key, td);
        this.createInitialAttentionLabel('N', key, td);

        var br = document.createElement('br');
        td.appendChild(br);

        var spacer2 = document.createElement("span");
        spacer2.appendChild(document.createTextNode('　'));
        td.appendChild(spacer2);

        this.createInitialAttentionLabel('O', key, td);
        this.createInitialAttentionLabel('P', key, td);
        this.createInitialAttentionLabel('Q', key, td);
        this.createInitialAttentionLabel('R', key, td);
        this.createInitialAttentionLabel('S', key, td);
        this.createInitialAttentionLabel('T', key, td);
        this.createInitialAttentionLabel('U', key, td);
        this.createInitialAttentionLabel('V', key, td);
        this.createInitialAttentionLabel('W', key, td);
        this.createInitialAttentionLabel('X', key, td);
        this.createInitialAttentionLabel('Y', key, td);
        this.createInitialAttentionLabel('Z', key, td);

        tr.appendChild(td);
        tblBody.appendChild(tr);
        tbl.appendChild(tblBody);
        cell.appendChild(tbl);

        div = document.createElement('div');
        div.style.textAlign = 'right';
        div.style.styleFloat = 'right';

        action = document.createElement('a');
        action.id = "removeAll" + key;
        action.href = "javascript:void(0);";
        text = document.createTextNode('すべて削除');
        action.appendChild(text);

        listener = function(me) {
            return function() { me.removeAll(); };
        }(this);
        action.onclick = listener;

        div.appendChild(action);
        cell.appendChild(div);

        row.appendChild(cell);

        tbody.appendChild(row);
    },
/**
 * ポップアップダイアログのユーザー絞り込みのための
 * イニシャルリンクを生成する。
 *
 * @param initial 生成するイニシャルの文字
 * @param key ポップアップの各要素を識別するためのキー値。
 *            画面上では複数の宛先編集ポップアップダイアログが
 *            表示される。各ダイアログを一意に識別するために必要
 * @param cell 親要素となるtd
 */
createInitialUserLabel:
    function (initial, key, cell) {
        var selectInitial = document.createElement('a');
        selectInitial.id = ID_USER_INI + initial + key;
        selectInitial.href = "javascript:void(0);";
        var text = document.createTextNode(initial);
        selectInitial.appendChild(text);

        var listener = function(me, ini, ky) {
            return function() { me.initialUserSearch(ini, ky); };
        }(this, initial, key);
        observe(selectInitial, 'click', listener);

        var spacer = document.createElement("span");
        spacer.appendChild(document.createTextNode(INITIAL_SPACER));
        spacer.id = "spacer";
        cell.appendChild(spacer);
        cell.appendChild(selectInitial);
    },
/**
 * ポップアップダイアログのユーザー絞り込みのための
 * イニシャルリンクを生成する。
 *
 * @param initial 生成するイニシャルの文字
 * @param key ポップアップの各要素を識別するためのキー値。
 *            画面上では複数の宛先編集ポップアップダイアログが
 *            表示される。各ダイアログを一意に識別するために必要
 * @param cell 親要素となるtd
 */
createInitialAttentionLabel:
    function (initial, key, cell) {
        var selectInitial = document.createElement('a');
        selectInitial.id = ID_ATTENTION_INI + initial + key;
        selectInitial.href = "javascript:void(0);";
        var text = document.createTextNode(initial);
        selectInitial.appendChild(text);

        var listener = function(me, ini, ky) {
            return function() { me.initialAttentionSearch(ini, ky); };
        }(this, initial, key);
        observe(selectInitial, 'click', listener);

        var spacer = document.createElement("span");
        spacer.appendChild(document.createTextNode(INITIAL_SPACER));
        spacer.id = initial + "spacer";
        cell.appendChild(spacer);
        cell.appendChild(selectInitial);
    },
/**
 * ポップアップダイアログのユーザー絞り込みのための
 * 活動単位セレクトボックスを生成する。
 *
 * @param tbody 親要素となるtbody
 * @param key ポップアップの各要素を識別するためのキー値。
 *            画面上では複数の宛先編集ポップアップダイアログが
 *            表示される。各ダイアログを一意に識別するために必要
 */
createSelectGroup:
    function (tbody, key) {
        var row, cell;
        row  = document.createElement('tr');
        cell = document.createElement('td');

        this.selectCandidateGroup = document.createElement('select');
        this.selectCandidateGroup.id = ID_GROUP + "candidate" + key;
        this.selectCandidateGroup.style.width = WIDTH_ELEMENT;
        cell.appendChild(this.selectCandidateGroup);

        row.appendChild(cell);

        var listener = function(me, sel) {
            return function() { me.canditateGroupSelectionChanged(sel); };
        }(this, this.selectCandidateGroup);
        observe(this.selectCandidateGroup, 'change', listener);

        cell = document.createElement('td');
        row.appendChild(cell);

        cell = document.createElement('td');

        this.selectSelectedGroup = document.createElement('select');
        this.selectSelectedGroup.id = ID_GROUP + "selected" + key;
        this.selectSelectedGroup.style.width = WIDTH_ELEMENT;
        cell.appendChild(this.selectSelectedGroup);

        row.appendChild(cell);

        listener = function(me, sel) {
            return function() { me.selectedGroupSelectionChanged(sel); };
        }(this, this.selectSelectedGroup);
        observe(this.selectSelectedGroup, 'change', listener);

        tbody.appendChild(row);
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
        var row, cell;
        row = document.createElement('tr');

        cell = document.createElement('td');

        this.incSearchCandidate = document.createElement('input');
        this.incSearchCandidate.id = ID_INCSEARCH + "candidate" + key;
        this.incSearchCandidate.setAttribute('type', 'text');
        this.incSearchCandidate.style.width = WIDTH_ELEMENT;
        cell.appendChild(this.incSearchCandidate);
        row.appendChild(cell);

        cell = document.createElement('td');
        row.appendChild(cell);

        cell = document.createElement('td');

        this.incSearchSelected = document.createElement('input');
        this.incSearchSelected.id = ID_INCSEARCH + "selected" + key;
        this.incSearchSelected.setAttribute('type', 'text');
        this.incSearchSelected.style.width = WIDTH_ELEMENT;
        cell.appendChild(this.incSearchSelected);
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
        var row, cell, text, action, listener;
        row = document.createElement('tr');

        cell = document.createElement('td');
        // cell.setAttribute("style", "font-size: 10px;");

        this.candidate = document.createElement('select');
        this.candidate.id = ID_CANDIDATE + key;
        this.candidate.setAttribute('multiple', 'multiple');
        this.candidate.setAttribute('size', SIZE_SELECT);
        this.candidate.style.width = WIDTH_ELEMENT;
        this.candidate.setAttribute('class', 'user-list');
        cell.appendChild(this.candidate);

        row.appendChild(cell);

        cell = document.createElement('td');
        cell.setAttribute("align", "center");
        cell.setAttribute("valign", "top");
        // cell.setAttribute("style", "font-size: 10px;");

        action = document.createElement('a');
        action.id = ID_ADD + key;
        action.href = "javascript:void(0);";
        action.style.display = 'block';
        text = document.createTextNode(">>");
        action.appendChild(text);

        listener = function(me) {
            return function() { me.add(); };
        }(this);
        action.onclick = listener;
        cell.appendChild(action);

        action = document.createElement('a');
        action.id = ID_REMOVE + key;
        action.href = "javascript:void(0);";
        action.style.display = 'block';
        text = document.createTextNode("<<");
        action.appendChild(text);

        listener = function(me) {
            return function() { me.remove(); };
        }(this);
        action.onclick = listener;
        cell.appendChild(action);

        row.appendChild(cell);

        cell = document.createElement('td');
        // cell.setAttribute("style", "font-size: 10px;");

        this.selected = document.createElement('select');
        this.selected.id = ID_SELECTED + key;
        this.selected.setAttribute('multiple', 'multiple');
        this.selected.setAttribute('size', SIZE_SELECT);
        this.selected.style.width = WIDTH_ELEMENT;
        this.selected.setAttribute('class', 'user-list');
        cell.appendChild(this.selected);

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
        var row, cell;
        row = document.createElement('tr');
        cell = document.createElement('td');
        cell.setAttribute("colSpan", "2");
        row.appendChild(cell);

        cell = document.createElement('td');
        cell.setAttribute("align", "right");

        this.ok = document.createElement('a');
        this.ok.id = ID_OK + key;
        this.ok.href = "javascript:void(0)";
        this.ok.style.marginRight = '15px';
        this.ok.appendChild(document.createTextNode("OK"));
        var okListener = function(me) {
            return function() { me.performOk(); };
        }(this);
        this.ok.onclick = okListener;
        cell.appendChild(this.ok);

        var loader = document.createElement('img');
        loader.id = ID_LOADER + key;
        loader.src = "../images/ajax-loader.gif";
        loader.setAttribute("style", "display: none; position: absolute;");
        loader.style.display = "none";
        loader.style.position = "absolute";
        cell.appendChild(loader);

        this.cancel = document.createElement('a');
        this.cancel.id = ID_CANCEL + key;
        this.cancel.href = "javascript:void(0)";
        this.cancel.style.marginRight = '15px';
        this.cancel.appendChild(document.createTextNode("閉じる"));
        var cancelListener = function(me) {
            return function() { me.performCancel(); };
        }(this);
        this.cancel.onclick = cancelListener;
        cell.appendChild(this.cancel);

        row.appendChild(cell);
        tbody.appendChild(row);
    },
/**
 * ポップアップダイアログ表示設定を行う。
 */
setup:
    function() {
        this.setupSelectUsers();
        //  宛先-活動単位セレクトボックスのoption要素を再生成
        this.clearSelectFields(this.selectGroup);
        this.fillSelectGroup(this.selectGroup);
        //  先頭は「ALL」なので削除
        // 「Add」リンクから起動したときは残す
        if (!this.isAdd) {
            if (this.selectGroup.options.length >= 1) {
                this.selectGroup.removeChild(this.selectGroup.options[0]);
            }
        }
        if (this.selectedGroupElement) {
            for (var i = 0; i < this.selectGroup.options.length; i++) {
                if (this.selectGroup.options[i].value == this.selectedGroupElement.value) {
                    this.selectGroup.selectedIndex = i;
                    break;
                }
            }
            //  返信文書かつToの場合、宛先を変更することはできない
            if (AddressInput.isReply && this.to) {
                this.clearSelectFields(this.selectGroup);

                var dsGroups = AddressInput.dsGroups;
                for (var i = 0; i < dsGroups.length; i++) {
                    var data = dsGroups[i];
                    if (!data) continue;

                    if (this.selectedGroupElement.value == data['id']) {
                        var option = document.createElement('option');
                        option.value     = data['id'];
                        option.innerHTML = data['name'];
                        this.selectGroup.appendChild(option);
                        break;
                    }
                }
            }
        }

        //  ユーザー絞り込み用活動単位セレクトボックスの
        //  option要素を再生成
        this.clearSelectFields(this.selectCandidateGroup);
        this.fillSelectGroup(this.selectCandidateGroup);
        this.clearSelectFields(this.selectSelectedGroup);
        this.fillSelectGroup(this.selectSelectedGroup);

        // イニシャルの選択状態を初期化
        this.userInitial = null;
        this.attentionInitial = null;
        this.clearUserInitializeSearch(this.key);
        this.clearAttentionInitializeSearch(this.key);

        //  ユーザー選択リストを初期状態に設定
        this.groupSelectionChanged(this.selectGroup);
        this.selectedGroupSelectionChanged(this.selectSelectedGroup);

        //  ユーザー選択リストをインクリメンタル検索するための設定
        this.incSearchCandidate.value = '';
        this.incSearchSelected.value = '';
        if (!this.startIncSearch) {
          new SelectionIncSearch(this.incSearchCandidate.id, this.candidate.id, this.actionNameHidden.id, this.selectGroup.id);
          new SelectionIncSearch(this.incSearchSelected.id,  this.selected.id, this.actionNameHidden.id);
          this.startIncSearch = true;
        }
    },
/**
 * 選択済ユーザーマップ情報に選択済ユーザーを設定する。
 */
setupSelectUsers:
    function() {
        if (this.selectedValuesElement) {
            this.dsSelectUsers[this.groupAll] = this.selectedValuesElement.value.split(",");
        }
    },
/*
 * このダイアログを表示する。
 */
show:
    function() {
        Dialog.show(this.div.id);

        /*
        var pos = DialogUtil.computePosition(this.action, this.div);
        this.div.style.top  = pos.y + "px";
        this.div.style.left = pos.x + "px";
        */
    },
/**
 * AddressInput.dsGroupsに予め設定済の
 * 活動単位リストから、活動単位セレクトボックスに
 * option要素を設定する。
 *
 * @param select 対象のセレクトボックス
 */
fillSelectGroup:
    function (select) {
        var dsGroups = AddressInput.dsGroups;
        for (var i = 0; i < dsGroups.length; i++) {
            var data = dsGroups[i];
            if (!data) continue;

            var option = document.createElement('option');
            option.value = data['id'];
            option.text  = data['name'];

            if (document.all) {
                select.add(option);
            } else {
                select.appendChild(option);
            }
        }
    },
/**
 * valuesに指定された従業員番号リストから、
 * AddressInput.dsUsersに予め設定済の
 * ユーザー情報を取得しoption要素を設定する。
 *
 * @param sel 対象のセレクトボックス
 * @param values セレクトボックスに設定する従業員番号の配列
 * @param initial 対象のイニシャル
 * @param key ポップアップの各要素を識別するためのキー値。
 *             画面上では複数のPerson in Charge選択ポップアップダイアログが
 *             表示される。各ダイアログを一意に識別するために必要
 * @param mode User側か、Attention側のどちらのイニシャルサーチかを識別する
 * @param isSelected selectedGroupSelectionChangedファンクションから呼び出した時のみtrue
 */
fillSelectUserFields:
    function(sel, values, initial, key, mode, isSelected) {
        var dsUsers = AddressInput.dsUsers;
        for (var i = 0; i < values.length; i++) {
            var value = values[i];
            var option = document.createElement('option');
            if (mode == MODE_USER_INI && this.exists(this.candidate, option)) {
                // Userイニシャルサーチの時、候補リストにユーザーがいたら追加しない

                continue;
            }

            var data = new Array();
            if (!this.isAdd || isSelected) {
                var found = false;
                for (var key in dsUsers) {
                    if (typeof key != 'undefined') {
                        if (value == key) {
                            data = dsUsers[key];
                            found = true;
                            break;
                        }
                    }
                }
                if (!found) {
                    var empNo = value.substring(0, value.indexOf(USER_KEY_DELIMITER));
                    for (var key in dsUsers) {
                        if (typeof key != 'undefined') {
                            var tmpNo = key.substring(0, key.indexOf(USER_KEY_DELIMITER));
                            if (empNo == tmpNo) {
                                data = dsUsers[key];
                                break;
                            }
                        }
                    }
                }
            } else {
                data = dsUsers[value];
            }
            if (data == null || data.length == 0) continue;

            option.value = data[EMP_NO_KEY];
            var text = data[DISPLAY_LABEL_KEY];
            if (text.indexOf(" [") != -1) {
                if (this.selectedValuesElement && sel == this.selected) {
                    var selectedValues = this.selectedValuesElement.value.split(',');
                    for (var j = 0, len = selectedValues.length; j < len; j++) {
                        if (value == selectedValues[j]) {
                            text = text.substring(0, text.indexOf(" ["));
                        }
                    }
                }
            }
            option.text  = text;
            if (null == initial ||
                INITAL_ALL == initial) {
                // イニシャルを未選択か「*」を選択した場合はイニシャルサーチしない
                if (document.all) {
                    sel.add(option);
                } else {
                    sel.appendChild(option);
                }
            } else {
                if (this.initialExists(initial, text)) {
                    // イニシャルサーチ
                    if (document.all) {
                        sel.add(option);
                    } else {
                        sel.appendChild(option);
                    }
                }
            }
        }
    },
/**
 * イニシャルの背景色を初期化する。
 * イニシャルサーチをする毎に呼び出す。
 *
 * @param id 対象のイニシャルID
 * @param initial 対象のイニシャル
 * @param key ポップアップの各要素を識別するためのキー値。
 *             画面上では複数のPerson in Charge選択ポップアップダイアログが
 *             表示される。各ダイアログを一意に識別するために必要
 */
initializeSearchedColor:
    function(id, initial, key) {
      var initialLink = document.getElementById(id + initial + key);
      initialLink.className = "";
    },
/**
 * 宛先活動単位セレクトボックスの値が変更された時の処理。
 * 連動してユーザー選択候補を絞り込むための活動単位セレクトボックスの
 * 値を変更する。
 *
 * @param sel 対象のセレクトボックス
 */
groupSelectionChanged:
    function(sel) {
        var selectedValue = this.selectGroup.value;
        this.actionNameHidden.value = INI_GROUP_CHANGE + (+new Date());
        this.selectCandidateGroup.value = selectedValue;
        //  値を設定しただけだとonchangeイベントが発生しないので
        //  イベントリスナを直接呼び出す
        this.canditateGroupSelectionChanged(this.selectCandidateGroup);
    },
/**
 * 活動単位セレクトボックスの値が変更された時の処理。
 * ユーザー選択セレクトボックスに表示するoption要素を
 * 再生成する。
 *
 * @param sel 対象のセレクトボックス
 */
canditateGroupSelectionChanged:
    function(sel) {
        var selectedValue = this.selectCandidateGroup.value;
        this.actionNameHidden.value = INI_USER_GROUP_CHANGE + (+new Date());
        var select = this.candidate;
        var initial = this.userInitial;
        if (typeof initial == "undefined") {
            initial = null;
        }
        this.clearSelectFields(select);

        var users = AddressInput.dsGroupUserMappings[selectedValue];

        if (users) {
            this.fillSelectUserFields(select, users, initial);
        }
    },
/**
 * 活動単位セレクトボックスの値が変更された時の処理。
 * ユーザー選択セレクトボックスに表示するoption要素を
 * 再生成する。
 *
 * @param sel 対象のセレクトボックス
 */
selectedGroupSelectionChanged:
    function(sel) {
        var selectedValue = this.selectSelectedGroup.value;
        this.actionNameHidden.value = INI_ATTENTION_GROUP_CHANGE + (+new Date());
        var select = this.selected;
        var initial = this.attentionInitial;
        if (typeof initial == "undefined") {
            initial = null;
        }
        this.clearSelectFields(select);

        var users = this.dsSelectUsers[selectedValue];

        if (users) {
            this.fillSelectUserFields(select, users, initial, null, null, true);
        }
    },
/**
 * User側のイニシャル選択状態を初期化する。
 *
 * @param key ポップアップの各要素を識別するためのキー値。
 *             画面上では複数のPerson in Charge選択ポップアップダイアログが
 *             表示される。各ダイアログを一意に識別するために必要
 */
clearUserInitializeSearch:
    function(key) {
        this.initializeSearchedColor(ID_USER_INI, INITAL_ALL, key);
        this.initializeSearchedColor(ID_USER_INI, "A", key);
        this.initializeSearchedColor(ID_USER_INI, "B", key);
        this.initializeSearchedColor(ID_USER_INI, "C", key);
        this.initializeSearchedColor(ID_USER_INI, "D", key);
        this.initializeSearchedColor(ID_USER_INI, "E", key);
        this.initializeSearchedColor(ID_USER_INI, "F", key);
        this.initializeSearchedColor(ID_USER_INI, "G", key);
        this.initializeSearchedColor(ID_USER_INI, "H", key);
        this.initializeSearchedColor(ID_USER_INI, "I", key);
        this.initializeSearchedColor(ID_USER_INI, "J", key);
        this.initializeSearchedColor(ID_USER_INI, "K", key);
        this.initializeSearchedColor(ID_USER_INI, "L", key);
        this.initializeSearchedColor(ID_USER_INI, "M", key);
        this.initializeSearchedColor(ID_USER_INI, "N", key);
        this.initializeSearchedColor(ID_USER_INI, "O", key);
        this.initializeSearchedColor(ID_USER_INI, "P", key);
        this.initializeSearchedColor(ID_USER_INI, "Q", key);
        this.initializeSearchedColor(ID_USER_INI, "R", key);
        this.initializeSearchedColor(ID_USER_INI, "S", key);
        this.initializeSearchedColor(ID_USER_INI, "T", key);
        this.initializeSearchedColor(ID_USER_INI, "U", key);
        this.initializeSearchedColor(ID_USER_INI, "V", key);
        this.initializeSearchedColor(ID_USER_INI, "W", key);
        this.initializeSearchedColor(ID_USER_INI, "X", key);
        this.initializeSearchedColor(ID_USER_INI, "Y", key);
        this.initializeSearchedColor(ID_USER_INI, "Z", key);
    },
/**
 * Attention側のイニシャル選択状態を初期化する。
 *
 * @param key ポップアップの各要素を識別するためのキー値。
 *             画面上では複数のPerson in Charge選択ポップアップダイアログが
 *             表示される。各ダイアログを一意に識別するために必要
 */
clearAttentionInitializeSearch:
    function(key) {
        this.initializeSearchedColor(ID_ATTENTION_INI, INITAL_ALL, key);
        this.initializeSearchedColor(ID_ATTENTION_INI, "A", key);
        this.initializeSearchedColor(ID_ATTENTION_INI, "B", key);
        this.initializeSearchedColor(ID_ATTENTION_INI, "C", key);
        this.initializeSearchedColor(ID_ATTENTION_INI, "D", key);
        this.initializeSearchedColor(ID_ATTENTION_INI, "E", key);
        this.initializeSearchedColor(ID_ATTENTION_INI, "F", key);
        this.initializeSearchedColor(ID_ATTENTION_INI, "G", key);
        this.initializeSearchedColor(ID_ATTENTION_INI, "H", key);
        this.initializeSearchedColor(ID_ATTENTION_INI, "I", key);
        this.initializeSearchedColor(ID_ATTENTION_INI, "J", key);
        this.initializeSearchedColor(ID_ATTENTION_INI, "K", key);
        this.initializeSearchedColor(ID_ATTENTION_INI, "L", key);
        this.initializeSearchedColor(ID_ATTENTION_INI, "M", key);
        this.initializeSearchedColor(ID_ATTENTION_INI, "N", key);
        this.initializeSearchedColor(ID_ATTENTION_INI, "O", key);
        this.initializeSearchedColor(ID_ATTENTION_INI, "P", key);
        this.initializeSearchedColor(ID_ATTENTION_INI, "Q", key);
        this.initializeSearchedColor(ID_ATTENTION_INI, "R", key);
        this.initializeSearchedColor(ID_ATTENTION_INI, "S", key);
        this.initializeSearchedColor(ID_ATTENTION_INI, "T", key);
        this.initializeSearchedColor(ID_ATTENTION_INI, "U", key);
        this.initializeSearchedColor(ID_ATTENTION_INI, "V", key);
        this.initializeSearchedColor(ID_ATTENTION_INI, "W", key);
        this.initializeSearchedColor(ID_ATTENTION_INI, "X", key);
        this.initializeSearchedColor(ID_ATTENTION_INI, "Y", key);
        this.initializeSearchedColor(ID_ATTENTION_INI, "Z", key);
    },
/**
 * イニシャルサーチリンクが押された時の処理。
 * ユーザー選択セレクトボックスに表示するoption要素を
 * 再生成する。
 *
 * @param sel 対象のセレクトボックス
 * @param key ポップアップの各要素を識別するためのキー値。
 *             画面上では複数のPerson in Charge選択ポップアップダイアログが
 *             表示される。各ダイアログを一意に識別するために必要
 */
initialUserSearch:
    function(initial, key) {
        this.userInitial = initial;
        var selectedValue = this.selectCandidateGroup.value;
        var select = this.candidate;
        this.clearSelectFields(select);
        this.actionNameHidden.value = INI_USER_INI_CHANGE + (+new Date());
        this.clearUserInitializeSearch(key);
        var initialLink = document.getElementById(ID_USER_INI + initial + key);
        initialLink.className = "selectedInitial";

        var users = AddressInput.dsGroupUserMappings[selectedValue];

        if (users) {
            this.fillSelectUserFields(select, users, initial, key, MODE_USER_INI);
        }
    },
/**
 * イニシャルサーチリンクが押された時の処理。
 * ユーザー選択セレクトボックスに表示するoption要素を
 * 再生成する。
 *
 * @param sel 対象のセレクトボックス
 * @param key ポップアップの各要素を識別するためのキー値。
 *             画面上では複数のPerson in Charge選択ポップアップダイアログが
 *             表示される。各ダイアログを一意に識別するために必要
 */
initialAttentionSearch:
    function(initial, key) {
        this.attentionInitial = initial;
        var selectedValue = this.selectSelectedGroup.value;
        var select = this.selected;
        this.clearSelectFields(select);
        this.actionNameHidden.value = INI_ATTENTION_INI_CHANGE + (+new Date());
        this.clearAttentionInitializeSearch(key);
        var initialLink = document.getElementById(ID_ATTENTION_INI + initial + key);
        initialLink.className = "selectedInitial";

        var users = this.dsSelectUsers[selectedValue];

        if (users) {
            this.fillSelectUserFields(select, users, initial, key, MODE_ATTENTION_INI);
        }
    },
/*
 * OK
 */
performOk:
    function(e) {
        var g = this.getSelectedGroup();
        var au = new Array();
        var users = this.getSelectedUsers();
        for (var i = 0, len = users.length; i < len; i++) {
            var val = users[i][EMP_NO_KEY];
            var group = val.substring(val.indexOf(USER_KEY_DELIMITER) + 1);
            if (group == JOIN_NO_GROUP && (!g['id'] || g['id'] == 0)) {
                return;
            }
            au.push(val);
        }
        this.selectedGroupId.value = g['id'];
        this.selectedUserId.value = au.join(',');

        document.getElementById(this.okButtonId).click();
        this.initSelectUsers();
    },
/*
 * キャンセル
 */
performCancel:
    function(e) {
        this.initSelectUsers();
        Dialog.close(this.div.id);
    },
/**
 * 選択候補で現在選択中の値を選択済リストに移動する.
 * @param e
 */
add:
    function(e) {
        var users = this.dsSelectUsers[this.selectCandidateGroup.value];
        var allUsers = this.dsSelectUsers[this.groupAll];
        var attentionInitial = this.attentionInitial;
        var dsUsers = AddressInput.dsUsers;

        for (var i = 0; i < this.candidate.options.length; i++) {
            if (this.candidate.options[i].selected) {
                var opt = this.candidate.removeChild(this.candidate.options[i]);
                opt.selected = false;
                if (!this.exists(this.selected, opt)) {
                    if (typeof attentionInitial != "undefined" && attentionInitial != null) { /* イニシャルサーチ */
                        var data = dsUsers[opt.value];
                        var text = data[DISPLAY_LABEL_KEY];
                        if (this.initialExists(attentionInitial, text)) {
                            this.selected.appendChild(opt);
                        }
                    } else {
                        this.selected.appendChild(opt);
                    }
                }
                var optValue = opt.value;
                if (!this.existsArray(allUsers, optValue)) { /* 「*」に入っていたら追加しない */
                    users[users.length] = optValue;
                }
                if (!this.existsArray(allUsers, optValue)) {
                    allUsers[allUsers.length] = optValue;
                }
                i--;
            }
        }
        AddressInput.dsUsers[this.selectCandidateGroup.value] = users;
        AddressInput.dsUsers[this.groupAll] = allUsers;
    },
/**
 * 選択済リストで現在選択中の値を選択候補に移動する.
 * @param e
 */
remove:
    function(e) {
        var userInitial = this.userInitial;
        var dsUsers = AddressInput.dsUsers;
        for (var i = 0; i < this.selected.options.length; i++) {
            if (this.selected.options[i].selected) {
                var opt = this.selected.removeChild(this.selected.options[i]);
                opt.selected = false;
                if (!this.exists(this.candidate, opt)) {
                    if (typeof userInitial != "undefined" && userInitial != null) { /* イニシャルサーチ */
                        var data = dsUsers[opt.value];
                        var text = data[DISPLAY_LABEL_KEY];
                        if (this.initialExists(userInitial, text)) {
                            this.candidate.appendChild(opt);
                        }
                    } else {
                        this.candidate.appendChild(opt);
                    }
                }
                this.removeSelectUser(opt.value);
                i--;
            }
        }
    },
/**
 * 選択した値をマップから削除する
 */
removeSelectUser:
    function(value) {
        var empNo = value.substring(0, value.indexOf(USER_KEY_DELIMITER));
        for (var i = 0; i < AddressInput.dsGroups.length; i++) {
            var users = this.dsSelectUsers[AddressInput.dsGroups[i]['id']];
            for (var j = 0; j < users.length; j++) {
                var selectedEmpNo = users[j].substring(0, users[j].indexOf(USER_KEY_DELIMITER));
                if (selectedEmpNo == empNo) {
                    users[j].remove = null;
                    this.dsSelectUsers[AddressInput.dsGroups[i]['id']]
                            = users.slice(0, j).concat(
                                    users.slice(j + 1, users.length));
                    break;
                }
            }
        }
    },
/**
 * 選択候補の値を全て選択済リストに追加する.
 * @param e
 */
addAll:
    function(e) {
        var users = this.dsSelectUsers[this.selectCandidateGroup.value];
        var allUsers = this.dsSelectUsers[this.groupAll];
        var dsUsers = AddressInput.dsUsers;
        var attentionInitial = this.attentionInitial;
        while (this.candidate.options.length > 0) {
            var opt = this.candidate.removeChild(this.candidate.options[0]);
            var optValue = opt.value;
            if (!this.existsArray(allUsers, optValue)) { /* 「*」に入っていたら追加しない */
                users[users.length] = optValue;
            }
            if (!this.existsArray(allUsers, optValue)) {
                allUsers[allUsers.length] = optValue;
            }

            if (this.exists(this.selected, opt)) {
                continue;
            }

            opt.selected = false;
            if (typeof attentionInitial != "undefined" && attentionInitial != null) { /* イニシャルサーチ */
                var data = dsUsers[opt.value];
                var text = data[DISPLAY_LABEL_KEY];
                if (this.initialExists(attentionInitial, text)) {
                    this.selected.appendChild(opt);
                }
            } else {
                this.selected.appendChild(opt);
            }
        }
        AddressInput.dsUsers[this.selectCandidateGroup.value] = users;
        AddressInput.dsUsers[this.groupAll] = allUsers;
    },
/**
 * valueがinitialで始まるかを返す.
 * 始まる場合：true、始まらない：false.
 * initialが「すべて」を表す「*」だった場合は、
 * 無条件でtrueを返す.
 *
 * @param initial イニシャル
 * @param value 精査する値
 */
initialExists:
    function(initial, value) {
        if (initial == INITAL_ALL) {
            return true;
        }
        if (value.lastIndexOf(initial, 0) != -1) {
            return true;
        }
    },
/**
 * 選択済リストの値を全て選択候補に戻す.
 * @param e
 * @return
 */
removeAll:
    function(e) {
        var userInitial = this.userInitial;
        var dsUsers = AddressInput.dsUsers;
        while (this.selected.options.length > 0) {
            var opt = this.selected.removeChild(this.selected.options[0]);
            this.removeSelectUser(opt.value);

            if (this.exists(this.candidate, opt)) {
                continue;
            }
            opt.selected = false;
            if (typeof userInitial != "undefined" && userInitial != null) { /* イニシャルサーチ */
                var data = dsUsers[opt.value];
                var text = data[DISPLAY_LABEL_KEY];
                if (this.initialExists(userInitial, text)) {
                    this.candidate.appendChild(opt);
                }
            } else {
                this.candidate.appendChild(opt);
            }
        }
    },
/*
 * selectに既にoptionが表す値が存在する場合はtrue
 *
 * @param select セレクトボックス
 * @param option 比較対象の値が格納されたoptionオブジェクト
 */
exists:
    function(select, option) {
        for (var i = 0; i < select.options.length; i++) {
            if (select.options[i].value == option.value) {
                return true;
            }
        }
        return false;
    },
/**
 * 配列に既に指定した要素がいる場合はtrue
 *
 * @param array 配列
 * @param value 要素
 */
existsArray:
    function(array, value) {
        for (var i = 0; i < array.length; i++) {
            if (array[i] == value) {
                return true;
            }
        }
        return false;
    },
/**
 * セレクトボックスのoption要素を全て削除する。
 *
 * @param select 対象となるセレクトボックス
 */
clearSelectFields:
    function(select) {
        while (select.options.length > 0) {
            select.removeChild(select.options[0]);
        }
    },
/*
 * 選択された活動単位の名前を返す。
 *
 * @return 活動単位名
 */
getSelectedGroup:
    function() {
        var ds = AddressInput.dsGroups;
        var id = this.selectGroup.value;
        for (var i = 0; i < ds.length; i++) {
            if (ds[i]['id'] == id) {
                return ds[i];
            }
        }
        return null;
    },
/*
 * 選択された全てのユーザーを返す。
 * Add：ユーザーID+グループは、当該ユーザーが所属するグループIDと紐付いている
 * Edit:ユーザーID+グループは、画面に表示されているユーザーID+グループIDになり、
 *      当該ユーザーが所属しないグループIDになる可能性がある。
 *      Editの場合のみ、ユーザーID部分を切り出して判定する。
 *      宛先として使用するグループIDは、ダイアログで選択したTo(Group)(Cc(Group))になるので、
 *      ユーザーIDのみ整合性がとれていれば問題なし。
 *
 * @return 選択された全てのユーザー
 */
getSelectedUsers:
    function() {
        var ds = AddressInput.dsUsers;
        var allUsers = this.dsSelectUsers[this.groupAll];
        var users = new Array();
        for (var i = 0; i < allUsers.length; i++) {
            var allUser = allUsers[i];
            if (this.isAdd) {
                var u = ds[allUser];
                if (u) users[users.length] = u;
            } else {
                for (var d in ds) {
                    if (d.indexOf(USER_KEY_DELIMITER) == -1) {
                        continue;
                    }
                    var dsEmpNo = d.substring(0, d.indexOf(USER_KEY_DELIMITER));
                    var allUserTmp = allUser.substring(0, allUser.indexOf(USER_KEY_DELIMITER));
                    if (dsEmpNo == allUserTmp) {
                        var u = ds[d];
                        users[users.length] = u;
                        break;
                    }
                }
            }
        }
        return users;
    }
};
