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
 *
 */
function isProceedDelete() {
    var result = confirm("選択した要素は削除されます。続けてもよろしいですか？");
    if (result) {
        clearEditingFlag();
    }
    return result;
}
/*
 *
 */
function isProceedCancel() {
    var result = confirm("選択した要素は取り消されます。続けてもよろしいですか？");
    if (result) {
        clearEditingFlag();
    }
    return result;
}


var isEditing = false;

/*
 * 画面遷移を行うかの確認.
 */
function isScreenTransition() {
    var result = true;
    if (isEditing) {
        result = window.confirm("編集中で保存していない内容は失われます。\n続けてもよろしいですか？");
    }

    if (result) {
        clearEditingFlag();
    }
    return result;
}

/**
 * 編集状態を初期化(false)する.
 * @return
 */
function clearEditingFlag() {
    isEditing = false;
    //alert("Clear Editing Flag!");
    return true;
}

/**
 * 編集開始を設定.
 * @return
 */
function openEditingFlag() {
    isEditing = true;
    //alert("Start Editing Flag!");
    return true;
}

/**
 * 編集中フラグを初期化します.
 * ※New MER/All Updateの場合にはManagedBeenのフラグを取得する.
 * @return
 */
function initEditingFlag() {
    isEditing = getEditingElementValue();
    //alert("Initial Editing Flag![" + isEditing + "]");
}
/**
 * 編集中フラグを設定するHiddenタグがある場合、編集中フラグを設定.
 * <p>
 * merReportUpdateでは、Save後に再描画する為、
 * 編集中フラグが初期化される事に対する対応.
 * </p>
 * @return
 */
function setEditingElementValue() {
    var editingElem = document.getElementById("editing");
    if (editingElem != null) {
        editingElem.value = isEditing;
        //alert("Element Set!! [" + isEditing + "]");
    }
    return true;
}

/**
 * 編集中フラグを設定するHiddenタグがある場合、編集中フラグを取得.
 * ※編集中フラグを設定するHiddenタグがある場合、falseを返す.
 * @return 編集中フラグ
 */
function getEditingElementValue() {
    var editingElem = document.getElementById("editing");
    if (editingElem != null) {
        return editingElem.value == "false" ? false : true;
    }
    return false;
}
