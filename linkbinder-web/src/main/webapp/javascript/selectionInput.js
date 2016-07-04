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
 * 選択候補のリストから対象を選択する入力フィールド
 *
 * = パラメーターの概要
 * -- 初期化時に次のオプションを指定してインスタンス化する。
 * ---------------------------------------------------------------------------------
 *  = 必須パラメーター
 *     - candidate        選択候補のselectタグのID
 *     - selected         選択済リストのselectタグのID
 *     - candidateValues  選択候補に設定する値。
 *                        optionタグのvalue値がカンマ区切りで格納されている前提
 *     - selectedValues   選択候補に設定する値。
 *                        optionタグのvalue値がカンマ区切りで格納されている前提
 *     - dataSource       selectタグに表示する内容を格納したオブジェクト。
 *                        optionタグのvalue値をキーとして対象のオブジェクトが取得できる形式であること
 *     - valueProperty    optionタグのvalue値に設定する値が格納されたプロパティ名
 *     - labelProperty    optionタグのtext値に設定する値が格納されたプロパティ名
 *     - addAction        選択候補から選択済リストへ値を移動するアクションを起動するタグのID
 *     - removeAction     選択済リストから選択候補へ値を移動するアクションを起動するタグのID
 *
 *  = オプションパラメーター
 *     - dialog           ポップアップダイアログで表示する場合はそのDialogのID
 *     - saveAction       選択内容を保存するアクションを起動するタグのID
 *                        ポップアップダイアログの場合は同時にダイアログが閉じられる
 *     - cancelAction     選択内容をキャンセルするアクションを起動するタグのID
 *                        ポップアップダイアログの場合は同時にダイアログが閉じられる
 * ---------------------------------------------------------------------------------
 *
 * = 使い方
 *   input = new SelectionInput({
 *                  candidate       : 'candidate',
 *                  selected        : 'selected',
 *                  candidateValues : 'candidateValueList',
 *                  selectedValues  : 'selectedValueList',
 *                  addAction       : 'add',
 *                  removeAction    : 'remove',
 *                  valueProperty   : 'id',
 *                  labelProperty   : 'name',
 *                  dataSource      : JSON.parse('{"1":{"id":1, "name":"foo"},"2":{"id":2, "name":"bar"}')''
 *                  });
 *
 *   input.setup();
 *
 */
function SelectionInput() {
    this.initialize.apply(this, arguments);
}

SelectionInput.prototype = {
initialize:
    function(options) {
        this.options = options;
    },
/**
 * 初期化処理.
 * オプションの解析、イベントハンドラの登録を行う.
 */
setup:
    function() {
        this.dataSource      = this.options['dataSource'];

        this.candidateValues = document.getElementById(this.options['candidateValues']);
        this.selectedValues  = document.getElementById(this.options['selectedValues']);
        this.candidate       = document.getElementById(this.options['candidate']);
        this.selected        = document.getElementById(this.options['selected']);
        this.valueProperty   = this.options['valueProperty'];
        this.labelProperty   = this.options['labelProperty'];

        this.unremovableAdmin = this.options['unremovableAdmin'];
        this.clearSelectFields(this.candidate);
        this.clearSelectFields(this.selected);

        this.fillSelectFields(this.candidate, this.candidateValues.value);
        this.fillSelectFields(this.selected, this.selectedValues.value);

        this.addSaveListener();
        this.addCancelListener();
        this.addAddListener();
        this.addRemoveListener();
        this.addAddAllListener();
        this.addRemoveAllListener();
    },
/**
 * 保存時に実行する関数を登録する.
 *
 * @param listener 関数
 */
addSaveEventListener:
    function(listener) {
        if (!this.saveListener) {
            this.saveListener = new Array();
        }
        this.saveListener[this.saveListener.length] = listener;
    },
/**
 * キャンセル時に実行する関数を登録する.
 *
 * @param listener 関数
 */
addCancelEventListener:
    function(listener) {
        if (!this.cancelListener) {
            this.cancelListener = new Array();
        }
        this.cancelListener[this.cancelListener.length] = listener;
    },
/**
 * 選択内容を保存するイベントリスナを登録する.
 */
addSaveListener:
    function() {
        if (!this.options['saveAction']) return;

        this.saveAction = document.getElementById(this.options['saveAction']);
        this.dialog     = this.options['dialog'];

        var save = function(me) {
            return function() { me.save(); };
        }(this);
        observe(this.saveAction, 'click', save);
    },
/**
 * 選択内容をキャンセルするイベントリストを登録する.
 */
addCancelListener:
    function() {
        if (!this.options['cancelAction']) return;

        this.cancelAction = document.getElementById(this.options['cancelAction']);
        var cancel = function(me) {
            return function() { me.cancel(); };
        }(this);
        observe(this.cancelAction, 'click', cancel);
    },
/**
 * 選択候補から選択済リストに追加するイベントリスナを登録する.
 */
addAddListener:
    function() {
        this.addAction = document.getElementById(this.options['addAction']);
        var add = function(me) {
            return function() { me.add(); };
        }(this);
        observe(this.addAction, 'click', add);
    },
/**
 * 選択済リストから選択候補に戻すイベントハンドラを追加する.
 */
addRemoveListener:
    function() {
        this.removeAction = document.getElementById(this.options['removeAction']);
        var remove = function(me) {
            return function() { me.remove(); };
        }(this);
        observe(this.removeAction, 'click', remove);
    },
/**
 * 選択候補の内容を全て選択済リストに追加するイベントリスナを登録する.
 */
addAddAllListener:
    function() {
        if (!this.options['addAllAction']) {
            return;
        }
        this.addAllAction = document.getElementById(this.options['addAllAction']);
        var addAll = function(me) {
            return function() { me.addAll(); };
        }(this);
        observe(this.addAllAction, 'click', addAll);
    },
/**
 * 選択済リストを全て選択候補に戻すイベントハンドラを追加する.
 */
addRemoveAllListener:
    function() {
        if (!this.options['removeAllAction']) {
            return;
        }
        this.removeAllAction = document.getElementById(this.options['removeAllAction']);
        var removeAll = function(me) {
            return function() { me.removeAll(); };
        }(this);
        observe(this.removeAllAction, 'click', removeAll);
    },
/**
 * 選択内容を保存する.
 * ポップアップダイアログの場合はダイアログを閉じる.
 * @param e
 */
save:
    function(e) {
        this.saveValues(this.candidate, this.candidateValues);
        this.saveValues(this.selected, this.selectedValues);

        this.closeDialog();

        if (this.saveListener) {
            for (var i = 0; i < this.saveListener.length; i++) {
                this.saveListener[i]();
            }
        }
    },
/**
 * select.optionタグの全ての値を連結してelementに格納する.
 * @param select 対象のselectタグ
 * @param element 値を格納する要素
 */
saveValues:
    function(select, element) {
        var values = "";
        for (var i = 0; i < select.options.length; i++) {
            values += select.options[i].value + ',';
        }
        element.value = values.substring(0, values.length - 1);
    },
/**
 * ポップアップダイアログを閉じる.
 * @return
 */
closeDialog:
    function() {
        if (this.dialog) {
            Dialog.close(this.dialog);
        }
    },
/**
 * 選択内容をキャンセルする.
 * @param e
 */
cancel:
    function(e) {
        this.clearSelectFields(this.candidate);
        this.clearSelectFields(this.selected);

        this.fillSelectFields(this.candidate, this.candidateValues.value);
        this.fillSelectFields(this.selected, this.selectedValues.value);

        this.closeDialog();

        if (this.cancelListener) {
            for (var i = 0; i < this.cancelListener.length; i++) {
                this.cancelListener[i]();
            }
        }
    },
/**
 * 選択候補で現在選択中の値を選択済リストに移動する.
 * @param e
 */
add:
    function(e) {
        this.move(this.candidate, this.selected);
    },
/**
 * 選択済リストで現在選択中の値を選択候補に移動する.
 * @param e
 */
remove:
    function(e) {
        this.move(this.selected, this.candidate, this.unremovableAdmin);
    },
/**
 * fromで選択済の値をtoに移動する
 * @param from 移動元
 * @param to 移動先
 */
move:
    function(from, to, unremovable) {
        for (var i = 0; i < from.options.length; i++) {
            if (from.options[i].selected
                && from.options[i].value != unremovable) {
                var opt = from.removeChild(from.options[i]);
                opt.selected = false;
                to.appendChild(opt);
                i--;
            } else {
                from.options[i].selected = false;
            }
        }
    },
/**
 * 選択候補の値を全て選択済リストに追加する.
 * @param e
 */
addAll:
    function(e) {
        this.moveAll(this.candidate, this.selected);
    },
/**
 * 選択済リストの値を全て選択候補に戻す.
 * @param e
 * @return
 */
removeAll:
    function(e) {
        this.moveAll(this.selected, this.candidate, this.unremovableAdmin);
    },
/**
 * fromの全ての値をtoに移動する
 * @param from 移動元
 * @param to 移動先
 */
moveAll:
    function(from, to, unremovable) {
        var unremovableOption = null;
        while (from.options.length > 0) {
            var opt = from.removeChild(from.options[0]);
            opt.selected = false;
            if (opt.value == unremovable) {
                unremovableOption = opt;
            } else {
                to.appendChild(opt);
            }
        }
        if (unremovableOption) {
            from.appendChild(unremovableOption);
        }
    },
/**
 * selectの子要素(option)を全て削除する.
 * @param select selectタグ
 */
clearSelectFields:
    function(select) {
        while (select.options.length > 0) {
            select.removeChild(select.options[0]);
        }
    },
/**
 * selectにvaluesの値を持つ子要素(option)を追加する.
 * valuesはカンマ区切りの値が格納されている前提.
 * @param select selectタグ
 * @param values 設定対象の値. ex. 1,2,3
 */
fillSelectFields:
    function(select, values) {
        var v = values.split(",");
        for (var i = 0; i < v.length; i++) {
            var data = this.findByKey(v[i]);
            if (!data) continue;

            var option = this.createOption(data);
            select.appendChild(option);
        }
    },
/**
 * keyに該当するオブジェクトをdataSourceから取得する.
 * @param key オブジェクト取得キー
 * @return 該当するオブジェクト
 */
findByKey:
    function(key) {
        return this.dataSource[key];
    },
/**
 * dataからoptionタグを生成して返す.
 * @param data optionに設定する値を持つオブジェクト.
 * @return optionタグ
 */
createOption:
    function(data) {
        var option = document.createElement('option');
        option.value = data[this.valueProperty];
        option.innerHTML = this.filter(data[this.labelProperty]);
        return option;
    },

/**
 * dataからoptionタグを生成して返す.
 * @param data optionに設定する値を持つオブジェクト.
 * @return optionタグ
 */
filter:
    function(txt) {
        txt = txt.replace('&', '&amp;');
        txt = txt.replace('<', '&lt;');
        txt = txt.replace('>', '&gt;');
        return txt;
    }
};
