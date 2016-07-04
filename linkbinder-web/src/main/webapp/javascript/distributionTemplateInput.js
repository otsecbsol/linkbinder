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
var ID_DIALOG          = "editAddress";           /* Dialogとして表示するdivエリア */
var ID_GROUP           = "editAddressGroup";      /* 活動単位セレクトボックス */
var ID_INCSEARCH       = "editAddressIncSearch";  /* インクリメンタルサーチテキストボックス */
var ID_CANDIDATE       = "editAddressCandidate";  /* 選択候補ユーザーのセレクトボックス */
var ID_SELECTED        = "editAddressSelected";   /* 選択候補ユーザーのセレクトボックス */
var ID_OK              = "editAddressOk";         /* OKアクション */
var ID_CANCEL          = "editAddressCancel";     /* キャンセルアクション */
var ID_USER_INI        = "editAddressIni";          /* 頭文字のリンク(User) */
var ID_ATTENTION_INI   = "editAddressAttentionIni"; /* 頭文字のリンク(Attention) */

var LABEL_TO                 = "To(グループ):";
var LABEL_CC                 = "Cc(グループ):";
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
var WIDTH_ELEMENT       = '420px';    /* 主要要素の幅 */
var SIZE_SELECT         = 15;         /* 選択候補・選択済リストの高さ */
var SIZE_FONT           = '12px';     /* フォントサイズ */
var INITAL_ALL          = "*";              /* 「すべて」を表すイニシャル */
var MODE_USER_INI       = "userIni";        /* Userのイニシャルを押した時を表す */
var MODE_ATTENTION_INI  = "attentionIni";   /* Attentionのイニシャルを押した時を表す */
var JOIN_NO_GROUP       = "-1";             /* グループに所属していないことを表す */
var USER_KEY_DELIMITER  = "-";              /* ユーザーIDとグループIDをつなぐ区切り文字 */

/* イニシャルサーチを発生するアクションの定義 */
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
 *       action         : ポップアップ起動アクションが発生した要素
 *       tableId        : 設定済宛先情報を表示するtableタグのID
 *       key            : ポップアップの各要素を識別するためのキー値。
 *                        画面上では複数のPerson in Charge選択ポップアップダイアログが
 *                        表示される。各ダイアログを一意に識別するために必要
 *       to             : To/Ccのいずれの宛先を編集するか。trueの場合はTo、falseの場合はCc
 *       ---------------------------------------------------------------------------
 *       【任意】
 *       rowId                 : このダイアログを起動した行ID。
 *                               'Edit'の場合は必須
 *       selectedGroupElement  : 宛先に既に設定済の活動単位IDを格納した要素。
 *                               empNo1,empNo2,empNo3...の形式
 *                               'Edit'の場合は必須
 *       selectedValuesElement : 宛先に既に設定済のユーザーの従業員番号を格納した要素。
 *                               empNo1,empNo2,empNo3...の形式
 *                               'Edit'の場合は必須
 */
AddressInput.showDialog = function(options) {
    var inputDialog = AddressInput.createAddressInput(options);
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
        this.rowId                = options['rowId'];
        this.selectedGroupElement = options['selectedGroupElement'];

        this.isNew                 = true;
        this.selectedValuesElement = null;
        if (options['selectedValuesElement']) {
            this.selectedValuesElement = options['selectedValuesElement'];
            this.isNew = false;
        }
        // 初回に表示された画面の位置が、次から異なる
        // ダイアログ表示の際に作成されたdivタグを一旦削除し、再作成する
        var parent = document.getElementById('content');
        var removeDiv = document.getElementById(ID_DIALOG + this.tableId + this.key);
        if (removeDiv != null) {
            parent.removeChild(removeDiv);
        }
        this.div = this.createDiv(this.action, this.tableId, this.key);
        parent.appendChild(this.div);
        this.initSelectUsers();
    },
/**
 * 選択済ユーザーマップ情報を初期化する。
 */
initSelectUsers:
    function() {
        this.dsSelectUsers = new Array();
        this.dsSelectAllUsers = new Array();
        this.addedUsers = new Array();
        for (var i = 0; i < AddressInput.dsGroups.length; i++) {
            this.dsSelectUsers[AddressInput.dsGroups[i]['id']] = new Array();
            this.addedUsers[AddressInput.dsGroups[i]['id']] = {};
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
        var div = document.createElement('div');
        div.id = ID_DIALOG + tableId + key;
        div.className = 'dialog';
        div.style.position = "absolute";

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
        span.style.fontSize = SIZE_FONT;

        var name = this.to ? LABEL_TO : LABEL_CC;
        name = name.replace(/:\s*$/, "");

        var msg = "プルダウンから " + name + " を選択してください。";
        var text = document.createTextNode(msg);
        span.appendChild(text);

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
        cell.style.fontSize = SIZE_FONT;

        var label = document.createElement('span');
        label.className = 'label';
        label.appendChild(document.createTextNode(this.to ? LABEL_TO : LABEL_CC));
        cell.appendChild(label);

        var required = document.createElement('span');
        required.className = 'necessary';
        required.appendChild(document.createTextNode('*'));
        cell.appendChild(required);

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
        var row, cell, text, action, listener, div;

        row = document.createElement('tr');

        cell = document.createElement('td');
        cell.className = "label";
        cell.style.fontSize = SIZE_FONT;

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
        div.style.textAlign = 'left';
        div.style.styleFloat = 'left';
        cell.appendChild(div);

        div = document.createElement('div');
        div.style.textAlign = 'right';
        div.style.styleFloat = 'right';

        action = document.createElement('a');
        action.id = "addAll";
        action.href = "javascript:void(0);";
        text = document.createTextNode('すべて追加');
        action.appendChild(text);

        div.appendChild(action);
        cell.appendChild(div);

        row.appendChild(cell);

        listener = function(me) {
            return function() { me.addAll(); };
        }(this);
        observe(action, 'click', listener);

        cell = document.createElement('td');
        row.appendChild(cell);

        cell = document.createElement('td');
        cell.className = "label";
        cell.style.fontSize = SIZE_FONT;

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
        action.id = "removeAll";
        action.href = "javascript:void(0);";
        text = document.createTextNode('すべて削除');
        action.appendChild(text);

        div.appendChild(action);
        cell.appendChild(div);

        row.appendChild(cell);

        listener = function(me) {
            return function() { me.removeAll(); };
        }(this);
        observe(action, 'click', listener);

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
        cell.setAttribute("style", "font-size: 10px;");

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
        cell.setAttribute("style", "font-size: 10px;");

        action = document.createElement('a');
        action.href = "javascript:void(0);";
        action.style.display = 'block';
        text = document.createTextNode(">>");
        action.appendChild(text);
        cell.appendChild(action);

        listener = function(me) {
            return function() { me.add(); };
        }(this);
        observe(action, 'click', listener);

        action = document.createElement('a');
        action.href = "javascript:void(0);";
        action.style.display = 'block';
        text = document.createTextNode("<<");
        action.appendChild(text);
        cell.appendChild(action);

        listener = function(me) {
            return function() { me.remove(); };
        }(this);
        observe(action, 'click', listener);

        row.appendChild(cell);

        cell = document.createElement('td');
        cell.setAttribute("style", "font-size: 10px;");

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
        this.setupSelectUsers();
        //  宛先-活動単位セレクトボックスのoption要素を再生成
        this.clearSelectFields(this.selectGroup);
        this.fillSelectGroup(this.selectGroup);
/*
        //  先頭は「ALL」なので削除
        if (this.selectGroup.options.length >= 1) {
            this.selectGroup.removeChild(this.selectGroup.options[0]);
        }
*/

        if (this.selectedGroupElement) {
            for (var i = 0; i < this.selectGroup.options.length; i++) {
                if (this.selectGroup.options[i].value == this.selectedGroupElement.value) {
                    this.selectGroup.selectedIndex = i;
                    break;
                }
            }
        }

        //  ユーザー絞り込み用活動単位セレクトボックスの
        //  option要素を再生成
        this.clearSelectFields(this.selectCandidateGroup);
        this.fillSelectGroup(this.selectCandidateGroup);
        this.clearSelectFields(this.selectSelectedGroup);
        this.fillSelectGroup(this.selectSelectedGroup);

        //  ユーザー選択リストを初期状態に設定
        this.groupSelectionChanged(this.selectGroup);
        this.selectedGroupSelectionChanged(this.selectSelectedGroup);

        //  ユーザー選択リストをインクリメンタル検索するための設定
        new SelectionIncSearch(this.incSearchCandidate.id, this.candidate.id, this.actionNameHidden.id, this.selectGroup.id);
        new SelectionIncSearch(this.incSearchSelected.id,  this.selected.id, this.actionNameHidden.id);
    },
/**
 * 選択済ユーザーマップ情報に選択済ユーザーを設定する。
 */
setupSelectUsers:
    function() {
        if (this.selectedValuesElement) {
            this.dsSelectAllUsers = this.selectedValuesElement.value.split(",");
            if (this.selectedGroupElement) {
                this.dsSelectUsers[this.selectedGroupElement.value] = this.dsSelectAllUsers;
            }
        }
    },
/*
 * このダイアログを表示する。
 */
show:
    function() {
		/*
        var pos = DialogUtil.computePosition(this.action, this.div);
        this.div.style.top  = pos.y + "px";
        this.div.style.left = pos.x + "px";
        */

        Dialog.show(this.div.id);
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
 * @param groupId グループID
 */
fillSelectUserFields:
    function(sel, values, initial, key, mode, isSelected, groupId) {
        var dsUsers = AddressInput.dsUsers;

        var groupName = "";
        if (groupId != null) {
          var ds = AddressInput.dsGroups;
          for (var idx = 0; idx < ds.length; idx++) {
              if (ds[idx]['id'] == groupId) {
                groupName = " [" + ds[idx]['name'] + "]";
                break;
              }
          }
        }

        for (var i = 0; i < values.length; i++) {
            var value = values[i];
            var option = document.createElement('option');
            if (MODE_USER_INI == mode && this.exists(this.candidate, option)) {
                // Userイニシャルサーチの時、候補リストにユーザーがいたら追加しない
                continue;
            }

            var data = dsUsers[value];
            if (data == null) { continue; }

            option.value = data[EMP_NO_KEY];
            var text = data[DISPLAY_LABEL_KEY];
            if (this.candidate == sel) {
                text = text + groupName;
            } else if (this.addedUsers[groupId].hasOwnProperty(value)) {
                text = text + groupName;
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
 * ユーザー選択候補を絞り込むための活動単位セレクトボックスの
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
        this.selectSelectedGroup.value = selectedValue;
        this.selectedGroupSelectionChanged(this.selectSelectedGroup);
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
            this.fillSelectUserFields(
                select, users, initial, null, null, false, selectedValue);
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
            this.fillSelectUserFields(
                select, users, initial, null, null, true, selectedValue);
        }
    },
/**
 * イニシャルサーチリンクが押された時の処理。
 * ユーザー選択セレクトボックスに表示するoption要素を
 * 再生成する。
 *
 * @param sel 対象のセレクトボックス
 */
initialUserSearch:
    function(initial, key) {
        this.userInitial = initial;
        var selectedValue = this.selectCandidateGroup.value;
        var select = this.candidate;
        this.clearSelectFields(select);
        this.actionNameHidden.value = INI_USER_INI_CHANGE + (+new Date());

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
        var initialLink = document.getElementById(ID_USER_INI + initial + key);
        initialLink.className = "selectedInitial";

        var users = AddressInput.dsGroupUserMappings[selectedValue];

        if (users) {
            this.fillSelectUserFields(
              select, users, initial, key, MODE_USER_INI, false, selectedValue);
        }
    },
/**
 * イニシャルサーチリンクが押された時の処理。
 * ユーザー選択セレクトボックスに表示するoption要素を
 * 再生成する。
 *
 * @param sel 対象のセレクトボックス
 */
initialAttentionSearch:
    function(initial, key) {
        this.attentionInitial = initial;
        var selectedValue = this.selectSelectedGroup.value;
        var select = this.selected;
        this.clearSelectFields(select);
        this.actionNameHidden.value = INI_ATTENTION_INI_CHANGE + (+new Date());

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
        var initialLink = document.getElementById(ID_ATTENTION_INI + initial + key);
        initialLink.className = "selectedInitial";

        var users = this.dsSelectUsers[selectedValue];

        if (users) {
            this.fillSelectUserFields(
              select, users, initial, key, MODE_ATTENTION_INI, false, selectedValue);
        }
    },
/*
 * OK
 */
performOk:
    function(e) {
        //  テーブルに行を追加
        //  または既存の行を更新
        var table = AddressTable.getTable(this.tableId, this.to);

        var g = this.getSelectedGroup();
        //  活動単位が未選択であれば処理を終了
        if (!g['id'] || g['id'] == 0) {
            return;
        }

        var au = new Array();
        var users = this.getSelectedUsers();
        for (var i = 0; i < users.length; i++) {
            var u = {};
            u.user = users[i];
            au.push(u);
        }

        if (this.isNew) {
            var ag = {};
            ag.mode           = AddressInput.updateModes[NEW];
            ag.corresponGroup = this.getSelectedGroup();
            ag.users          = au;

            var rowId = null;
            var existUsers = null;
            for (var rowIndex in table.ROWS) {
                var row = table.ROWS[rowIndex];
                if (row.group.corresponGroup.id == ag.corresponGroup.id) {
                    rowId = row.rowId;
                    existUsers = row.users;
                    for (var i = 0; i < au.length; i++) {
                        var empNo = au[i]['user']['empNo'];
                        var find = false;
                        for (var j = 0; j < row.users.length; j++) {
                            if (empNo == row.users[j]['user']['empNo']) {
                                find = true;
                                break;
                            }
                        }
                        if (!find) { existUsers.push(au[i]); }
                    }
                }
            }
            if (rowId != null) {
                table.updateRow(rowId, this.getSelectedGroup(), existUsers);
            } else {
                table.addNewRow(new AddressRow(ag, ag.users));
            }
        } else {
            table.updateRow(this.rowId, this.getSelectedGroup(), au);
        }
        this.initSelectUsers();
        Dialog.close(this.div.id);
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
        var allUsers = this.dsSelectAllUsers;
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
                    this.addedUsers[this.selectCandidateGroup.value][opt.value] = true;
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
    },
/**
 * 選択済リストで現在選択中の値を選択候補に移動する.
 * @param e
 */
remove:
    function(e) {
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
        for (var i = 0; i < AddressInput.dsGroups.length; i++) {
            var users = this.dsSelectUsers[AddressInput.dsGroups[i]['id']];
            for (var j = 0; j < users.length; j++) {
                if (users[j] == value) {
                    users[j].remove = null;
                    this.dsSelectUsers[AddressInput.dsGroups[i]['id']]
                            = users.slice(0, j).concat(
                                    users.slice(j + 1, users.length));
                    break;
                }
            }
        }

        var tmp = new Array();
        for (var k = 0; k < this.dsSelectAllUsers.length; k++) {
            var user = this.dsSelectAllUsers[k];
            if (user == value) { continue; }
            tmp.push(user);
        }
        this.dsSelectAllUsers = tmp;
    },
/**
 * 選択候補の値を全て選択済リストに追加する.
 * @param e
 */
addAll:
    function(e) {
        var users = this.dsSelectUsers[this.selectCandidateGroup.value];
        var allUsers = this.dsSelectAllUsers;
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
            this.addedUsers[this.selectCandidateGroup.value][opt.value] = true;
        }
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
 *
 * @return 選択された全てのユーザー
 */
getSelectedUsers:
    function() {
        var ds = AddressInput.dsUsers;
        var allUsers = this.dsSelectAllUsers;
        var users = new Array();
        for (var i = 0; i < allUsers.length; i++) {
            var u = ds[allUsers[i]];
            if (u) users[users.length] = u;
        }
        return users;
    }
};

function AddressTableBuilder() {
}
AddressTableBuilder.build = function(tableId, to, addresses, readOnly, cellClasses) {
    var dsGroups = AddressInput.dsGroups;
    var dsUsers  = AddressInput.dsUsers;

    var table = AddressTable.getTable(tableId, to);
    for (var i = 0; i < addresses.length; i++) {
        table.addNewRow(new AddressRow(addresses[i], addresses[i]['users'], readOnly, cellClasses));
    }
};

/*
 * 宛先を表示するテーブルを表す。
 */
function AddressTable() {
    this.initialize.apply(this, arguments);
}

/* 宛先を表すテーブルオブジェクト格納コンテナ */
AddressTable.TABLES = {};

/*
 * 指定されたidのテーブルオブジェクトを取得する。
 *
 * @param id テーブルID
 * @param to To/Cc判定フラグ
 * @return テーブルオブジェクト
 */
AddressTable.getTable = function(id, to) {
    if (AddressTable.TABLES[id]) {
        return AddressTable.TABLES[id];
    }
    var t = new AddressTable(id, to);
    AddressTable.TABLES[id] = t;
    return t;
};

/*
 * テーブルの指定された行を削除する。
 *
 * @param tableId 削除対象の行が所属するテーブル
 * @param row 削除する行を表すAddressRowオブジェクト
 */
AddressTable.removeRow = function(tableId, row) {
    var table = AddressTable.getTable(tableId);
    //  削除リストに追加
    if (row.group.mode != AddressInput.updateModes[NEW]) {
        row.group.mode = AddressInput.updateModes[DELETE];
        table.removed.push(row.toAddressObject());
    }

    for (var i = 0; i < table.tbody.childNodes.length; i++) {
        if (table.tbody.childNodes[i] == row.row) {
            table.tbody.removeChild(row.row);
            delete table.ROWS[row.rowId];
            break;
        }
    }
    for (var i = 0; i < table.tbody.childNodes.length; i++) {
        table.tbody.childNodes[i].className =
            ((i + 1) % 2) == 0 ? 'even' : 'odd';
    }
};

/*
 * テーブルの全ての行を削除する。
 *
 * @param tableId 削除対象の行が所属するテーブル
 */
AddressTable.removeAllRows = function(tableId) {
    var table = AddressTable.getTable(tableId);
    //  削除リストに追加
    for (var key in table.ROWS) {
        AddressTable.removeRow(tableId, table.ROWS[key]);
    }

    while (table.tbody.childNodes.length > 0) {
        table.tbody.removeChild(table.tbody.childNodes[0]);
    }
    table.ROWS = {};
};

AddressTable.prototype = {
/* このオブジェクトが保持する行オブジェクト */
ROWS: {},
/* 行ID採番変数 */
rowId: 0,
/* 削除済宛先のリスト */
removed: new Array(),
/*
 * テーブルのIDを指定して初期化する。
 *
 * @param id テーブルのID
 * @param To/Cc判定フラグ
 */
initialize:
    function(id, to) {
        this.ROWS = {};
        this.rowId = 0;
        this.removed = new Array();

        this.id = id;
        this.to = to;
        this.table = document.getElementById(this.id);

        this.tbody = null;
        for (var i = 0; i < this.table.childNodes.length; i++) {
            var tagName = this.table.childNodes[i].tagName;
            if (tagName && tagName.toLowerCase() == 'tbody') {
                this.tbody = this.table.childNodes[i];
                break;
            }
        }
        if (!this.tbody) {
            this.tbody = document.createElement('tbody');
            this.table.appendChild(this.tbody);
        }
    },
/*
 * テーブルに新しい行を追加する。
 *
 * @param row 追加するAddressRowオブジェクト
 */
addNewRow:
    function(row) {
        var rowId = this.createRowId();
        row.setup(this.id,
                  this.to,
                  rowId);

        this.setClassName(row);
        this.tbody.appendChild(row.row);

        this.ROWS[rowId] = row;
    },
/*
 * テーブルの行を更新する。
 *
 * @param rowId 更新対象の行ID
 * @param group 選択された活動単位
 * @param users 選択されたユーザーの配列
 */
updateRow:
    function(rowId, group, users) {
        var row = this.ROWS[rowId];
        if (row) {
            row.update(group, users);
        }
    },
/*
 * 行IDを生成する。
 *
 * @return 新しい行ID
 */
createRowId:
    function() {
        return this.id + "" + (this.rowId++);
    },
/*
 * 新しく追加する行のstyle classを設定する。
 *
 * @param newRow 新しく追加するAddressRowオブジェクト
 */
setClassName:
    function(newRow) {
        newRow.row.className =
            ((this.tbody.childNodes.length + 1) % 2 == 0) ? 'even' : 'odd';
    },
/*
 * このテーブルに設定された宛先を、
 * サーバーに引き渡す形式のオブジェクトに変換する。
 *
 * @return 宛先オブジェクト
 */
toAddressObject:
    function() {
        var result = new Array();
        for (var i = 0; i < this.tbody.childNodes.length; i++) {
            var r = this.tbody.childNodes[i];
            var row = this.ROWS[r.id];
            result.push(row.toAddressObject());
        }
        return result;
    },
toRemovedAddressObject:
    function() {
        return this.removed;
    }
};

/*
 * 1件の宛先を表す。
 */
function AddressRow() {
    this.initialize.apply(this, arguments);
}

AddressRow.prototype = {
/* 活動単位の列インデックス。 */
CELL_GROUP:   0,
/* ユーザーの列インデックス。 */
CELL_USERS:   1,
/* アクションの列インデックス。 */
CELL_ACTIONS: 2,
/*
 * 活動単位とユーザーを指定して、
 * 1件の宛先を表す新しい行を生成する。
 *
 * @param group 活動単位
 * @param users 活動単位に設定されたユーザー
 * @param readOnly この行を編集不可にする場合はtrue (optilnal)
 * @param cellClasses この行の各セルに設定するstylesheetクラス名. (optional)
 *                    長さ3の配列を想定
 */
initialize:
    function(group, users, readOnly, cellClasses) {
        this.group    = group;
        this.users    = users;
        this.readOnly = readOnly;
        this.cellClasses = cellClasses;
    },
/*
 * この行を初期化する。
 *
 * @param tableId 親となるテーブルID
 * @param to To/Cc判定フラグ
 * @param rowId この行のID
 */
setup:
    function(tableId, to, rowId) {
        this.tableId = tableId;
        this.to      = to;
        this.rowId   = rowId;

        this.row = this.createRow();
        this.row.id = this.rowId;

        //  現在のコレポン文書に対する返信文書があるかチェック
/*
        var replied = false;
        var count = this.group['replyCount'];
        if (count && parseInt(count) > 0) {
            replied = true;
        }

        //  Person in Chargeが設定済であるかチェック
        var hasPic = false;
        for (var i = 0; i < this.users.length; i++) {
            var count = this.users[i]['personInCharges']
                          ? this.users[i]['personInCharges'].length
                          : 0;

            if (count && count > 0) {
                hasPic = true;
                break;
            }
        }
        //  返信情報、PIC情報を宛先オブジェクトに保存
        if (replied) {
            this.group.replyCorresponExists = replied;
        }
        if (hasPic) {
            this.group.personInChargeExists = hasPic;
        }
*/
        if (!this.readOnly) {
            this.createActions(this.row);
        }

        this.cellGroup   = this.row.childNodes[this.CELL_GROUP];
        this.cellUsers   = this.row.childNodes[this.CELL_USERS];
        this.cellActions = this.row.childNodes[this.CELL_ACTIONS];

        this.setupGroup(this.group);
        this.setupUsers(this.users);
    },
/*
 * この行が表す1件の宛先の内容を更新する。
 *
 * @param group 活動単位
 * @param users 活動単位に設定されたユーザー
 */
update:
    function(group, users) {
        if (this.group.mode != AddressInput.updateModes[NEW]) {
            this.group.mode = AddressInput.updateModes[UPDATE];
        }
        this.group.corresponGroup = group;

        // address_user_idを引き継ぐ(PICを残したままにするために必要)
        for (var i = 0 ; i < this.users.length; i++) {
            for (var j = 0 ; j < users.length ; j++) {
                if (this.users[i]['id']
                    && this.users[i]['user']['empNo'] == users[j]['user']['empNo']) {
                    users[j]['id'] = this.users[i]['id'];
                    break;
                }
            }
        }

        this.users = users;

        this.setupGroup(this.group);
        this.setupUsers(this.users);
    },
/*
 * 新しい行を生成する。
 *
 * @return テーブルの行(tr)
 */
createRow:
    function() {
        var row = document.createElement('tr');
        for (var i = 0; i < 3; i++) {
            var cell = document.createElement('td');

            if (this.cellClasses
                && this.cellClasses.length > i
                && this.cellClasses[i] != '') {

                cell.className = this.cellClasses[i];
            }
            row.appendChild(cell);
        }

        return row;
    },
/*
 * この行を編集するアクションを生成する。
 *
 * @param row テーブルの行(tr)
 */
createActions:
    function(row) {
        var listener;
        var cell = row.childNodes[this.CELL_ACTIONS];

        var tableId           = this.tableId;
        var rowId             = this.rowId;
        var to                = this.to;
        var userValuesElement = this.getUserValuesElement();
        var groupValueElement = this.getGroupValueElement();

        //  編集
        var edit = this.createActionLink('編集');
        listener = function() {
            AddressInput.showDialog({
                action:                this,
                tableId:               tableId,
                key:                   rowId,
                to:                    to,
                selectedGroupElement:  groupValueElement,
                selectedValuesElement: userValuesElement,
                rowId:                 rowId});
        };
        observe(edit, 'click', listener);
        cell.appendChild(edit);

        //  削除
            var row = this;
            var del = this.createActionLink('削除');
            listener = function() {
                AddressTable.removeRow(tableId, row);
            };
            observe(del, 'click', listener);
            cell.appendChild(del);
    },
/*
 * この行を編集するためのアンカータグを生成する。
 *
 * @param label リンクに表示する文字列
 * @return アンカータグ(a)
 */
createActionLink:
    function(label) {
        var action = document.createElement('a');
        action.href = 'javascript:void(0);';
        action.className = 'margin';
        action.appendChild(document.createTextNode(label));

        return action;
    },
/*
 * この行が保持するユーザー情報を格納する要素のIDを返す。
 *
 * @return ID
 */
getUserValuesId:
    function() {
        return "users-" + this.rowId;
    },
/*
 * この行が保持するユーザー情報を格納する要素を返す。
 *
 * @return ユーザー情報を格納する要素
 */
getUserValuesElement:
    function() {
        if (!this.userValuesElement) {
            this.userValuesElement = this.createUserValuesElement();
        }
        return this.userValuesElement;
    },
/*
 * この行が保持するユーザー情報を格納する要素を生成する。
 *
 * @return ユーザー情報を格納する要素
 */
createUserValuesElement:
    function() {
        var id = this.getUserValuesId();
        elem = document.createElement('input');
        elem.type = 'hidden';
        elem.id = id;
        this.row.appendChild(elem);

        return elem;
    },
/*
 * この行が保持する活動単位を格納する要素のIDを返す。
 *
 * @return ID
 */
getGroupValueId:
    function() {
        return "group-" + this.rowId;
    },
/*
 * この行が保持する活動単位を格納する要素を返す。
 *
 * @return 活動単位を格納する要素
 */
getGroupValueElement:
    function() {
        if (!this.groupValueElement) {
            this.groupValueElement = this.createGroupValueElement();
        }
        return this.groupValueElement;
    },
/*
 * この行が保持する活動単位を格納する要素を生成する。
 *
 * @return 活動単位を格納する要素
 */
createGroupValueElement:
    function() {
        var id = this.getGroupValueId();
        elem = document.createElement('input');
        elem.type = 'hidden';
        elem.id = id;
        this.row.appendChild(elem);

        return elem;
    },
/*
 * この行に活動単位を保存し、表示のための設定を行う。
 *
 * @param group 活動単位
 */
setupGroup:
    function(group) {
        while (this.cellGroup.childNodes.length > 0) {
            this.cellGroup.removeChild(this.cellGroup.childNodes[0]);
        }
        this.cellGroup.appendChild(document.createTextNode(group['corresponGroup']['name']));
        this.getGroupValueElement().value = group['corresponGroup']['id'];
    },
/*
 * この行にユーザーを保存し、表示のための設定を行う。
 *
 * @param users ユーザー
 */
setupUsers:
    function(users) {
        while (this.cellUsers.childNodes.length > 0) {
            this.cellUsers.removeChild(this.cellUsers.childNodes[0]);
        }

        var empNos = new Array();
        for (var i = 0; i < users.length; i++) {
            if (!users[i]) continue;

            var span = document.createElement('span');
            span.className = 'edit-address-user';

            var userName = users[i]['user']['labelWithRole'];
            if (userName == null || typeof userName == "undefined") {
                userName = users[i]['user']['labelWithRole'];
            }

            span.appendChild(document.createTextNode(userName));
            //this.cellUsers.appendChild(span);
            this.cellUsers.appendChild(document.createTextNode(userName));
            var br = document.createElement('br');
            this.cellUsers.appendChild(br);

            empNos.push(users[i]['user']['empNo']);
        }
        this.getUserValuesElement().value = empNos.join(',');
    },
/*
 * この行に設定された宛先を、
 * サーバーに引き渡す形式のオブジェクトに変換する。
 *
 * @return 宛先オブジェクト
 */
toAddressObject:
    function() {
        var users = new Array();
        for (var i = 0,
                 values = this.getUserValuesElement().value.split(',');
             i < values.length; i++) {

            if (!AddressInput.dsUsers[values[i]]) {
                continue;
            }

            var u = {};
            u.user = {};
            u.user.empNo = values[i];
            u.user.nameE = AddressInput.dsUsers[values[i]]['nameE'];
            u.user.labelWithRole = AddressInput.dsUsers[values[i]]['labelWithRole'];
            u.user.role = AddressInput.dsUsers[values[i]]['role'];

            // address_user_idを引き継ぐ(PICを残したままにするために必要)
            for (var j = 0 ; j < this.users.length; j++) {
                if (this.users[j]['id']
                    && this.users[j]['user']['empNo'] == values[i]) {
                    u.id = this.users[j]['id'];
                    break;
                }
            }

            users.push(u);
        }

        var result = this.group;
        result.corresponGroup.id = this.getGroupValueElement().value;
        result.users = users;

        return result;
    }
};

/** distribution TemplateのTo Address一時保存 */
var distributionTemplateToAddressTableIniData;
/** distribution TemplateのCc Address一時保存 */
var distributionTemplateCcAddressTableIniData;

/**
 * distribution Template変更チェック
 * @return 変更あり = true
 */
function checkDistributionTemplate(){
    var flg = false;

    // To (Group)のチェック
    if(distributionTemplateToAddressTableIniData != null
            && distributionTemplateToAddressTableIniData != undefined) {
        var nowData = getTdTextData('toAddressTable');
        if(distributionTemplateToAddressTableIniData != nowData) {
            flg = true;
        }
    }
    // Cc (Group)のチェック
    if(distributionTemplateCcAddressTableIniData != null
            && distributionTemplateCcAddressTableIniData != undefined) {
        var nowData = getTdTextData('ccAddressTable');
        if(distributionTemplateCcAddressTableIniData != nowData) {
            flg = true;
        }
    }
    return flg;
};

function getTdTextData(id) {
    var list = searchNodes(document.getElementById(id));
    var nowData;
    for(j = 0 ; j < list.length; j++ ) {
        if(list[j].tagName == 'TD') {
            nowData += list[j].innerHTML;
        }
    }
    return nowData;
};

/**
 * コピーリンク押下処理
 * 入力内容変更チェックを行い、Dialogを表示する
 * @return
 */
function clickCopyLink() {
    if (!confirm('編集中で保存していない内容は失われます。続けてもよろしいですか？')) {
        return false;
    }
};

/**
 * 全ノード探索を行う
 * @param root 探索元
 * @return 探索結果List
 */
function searchNodes(root) {
    var list = [];
    var search = function (node) {
        while (node != null) {
            list.push(node);
            search(node.firstChild);
            node = node.nextSibling;
        }
    };
    search(root.firstChild);
    return list;
};

