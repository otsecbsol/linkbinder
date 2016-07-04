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
 * selectボックスに表示された内容をインクリメンタル検索する.
 *
 * == 使い方
 *   new SelectionIncSearch('incSearch', 'selectbox');
 *
 * @input インクリメンタル検索の入力フィールドID
 * @select 検索対象のセレクトボックスID
 *
 */
function SelectionIncSearch() {
    this.initialize.apply(this, arguments);
}
SelectionIncSearch.prototype = {
/*
 * 入力フィールドを監視する間隔(ms).
 */
interval: 500,

/*
 * オブジェクトを初期化し、
 * 入力フィールドの監視を開始する.
 *
 * @inputId インクリメンタル検索の入力フィールド
 * @selectId 検索対象のセレクトボックスID
 * @actionNameHiddenId 発生元のアクションID
 * @toCcGroup To(Group)、またはCc(Group)ID
 */
initialize:
    function(inputId, selectId, actionNameHiddenId, toCcGroupId) {
        this.input  = document.getElementById(inputId);
        if (this.input && this.input.value) {
            this.input.value = '';
        }
        this.select = document.getElementById(selectId);

        if (actionNameHiddenId) {
            this.actionNameHidden = document.getElementById(actionNameHiddenId);
            this.actionNameHiddenOld = null;
        }

        if (toCcGroupId) {
            this.toCcGroup = document.getElementById(toCcGroupId);
            this.toCcGroupOld = null;
        }

        this.checkLoopTimer = null;
        this.old = null;
        this.matchList = null;

        this.ignoreCase = true;

        this.checkLoop();
    },
/*
 * 入力フィールドを監視し、
 * 値が変更されていればインクリメンタル検索を開始する.
 */
checkLoop:
    function() {
        var exec = false;
        var values = this.parseInput();
        if (this.isValueChanged(values)) {
            this.old = values;
            exec = true;
        }

        if (this.actionNameHidden) {
            var actionNameHiddenValue = this.actionNameHidden.value;
            if (actionNameHiddenValue != this.actionNameHiddenOld) {
                this.actionNameHiddenOld = actionNameHiddenValue;
                exec = true;
            }
        }

        if (this.toCcGroup) {
            var selectValue = this.toCcGroup.value;
            if (selectValue != this.toCcGroupOld) {
                this.toCcGroupOld = selectValue;
                exec = true;
            }
        }
        if (exec) {
          this.startSearch(values);
        }

        if (!this.checkLoopTimer) {
            clearTimeout(this.checkLoopTimer);
        }
        this.checkLoopTimer = setTimeout(this._bind(this.checkLoop), this.interval);
    },
/*
 * 入力フィールドの値を解析し
 * 検索キーワードの配列を生成して返す.
 *
 * 例えば入力フィールドに
 *   Sample Name Test
 * と入力されている場合は次の配列が返される.
 *   ['Sample', 'Name', Test]
 *
 * @return キーワード配列.
 */
parseInput:
    function() {
        var value = this.input.value;
        if (!value) {
            return [];
        }
        var valueList = value.split(' ');
        var values = [];
        for (var i = 0, len = valueList.length; i < len; i++) {
            if (valueList[i]) values.push(valueList[i]);
        }
        return values;
    },
/*
 * valueが前回保存した値から変更があればtrueを返す.
 *
 * @value 比較対象の値
 * @return 変更があればtrue
 */
isValueChanged:
    function(value) {
        return !this.old
            || (value.join(' ') != this.old.join(' '));
    },
/*
 * valueが前回保存した値から変更があればtrueを返す.
 *
 * @value 比較対象の値
 * @return 変更があればtrue
 */
isSelectionChanged:
    function(value, old) {
        return !old
            || (value.join(' ') != old.join(' '));
    },
/*
 * インクリメンタル検索を開始する.
 *
 * @values 検索キーワード
 */
startSearch:
    function(values) {
        this.clearSelected();
        this.search(values);
        this.applySelect();
    },
/*
 * 検索対象のセレクトボックスの選択状態を全てクリアする.
 */
clearSelected:
    function() {
        for (var i = 0, len = this.select.options.length; i < len; i++) {
            this.select.options[i].selected = false;
        }
    },
/*
 * 検索を行い、
 * 検索キーワードにマッチした要素を選択状態にする.
 *
 * @values 検索キーワード
 */
search:
    function(values) {
        this.collectMatchList(values);
        this.selectOptions();
    },
collectMatchList:
    function(values) {
        this.matchList = [];
        for (var i = 0, len = this.select.options.length; i < len; i++) {
            if (this.match(this.select.options[i], values)) {
                this.matchList.push(this.select.options[i]);
            }
        }
    },
selectOptions:
    function() {
        // 選択箇所にスクロールするための対応
        var limit = 10;
        var list = this.matchList;
        var len = list.length;

        if (len > limit) {
            this.select.style.display = 'none';
        }
        for (var i = 0; i < len - limit; i++) {
            list[i].selected = true;
        }
        if (len > limit) {
            this.select.style.display = 'inline';
        }

        for (; i < len; i++) {
            list[i].selected = true;
        }

        //for (var i = 0, len = list.length; i < len; i++) {
        //    list[i].selected = true;
        //}
    },
/*
 * optionのラベル値が、valuesにマッチした場合はtrueを返す.
 *
 * @param option セレクトボックスのoption要素
 * @param values 検索キーワード
 * @return マッチした場合はtrue
 */
match:
    function(option, values) {
        if (values.length == 0 || values[0] == "") return false;

        var val = this.unescape(option.innerHTML);
        for (var i = 0, len = values.length; i < len; i++) {
            if (this.matchIndex(val, values[i]) == -1) {
                return false;
            }
        }
        return true;
    },
/*
 * patternが文字列中に含まれていればその位置を返す.
 *
 * @value 検索対象文字列
 * @pattern 検索キーワード
 * @return 最初に一致した位置
 */
matchIndex:
    function(value, pattern) {
        if (this.ignoreCase) {
            return value.toLowerCase().indexOf(pattern.toLowerCase());
        } else {
            return value.indexOf(pattern);
        }
    },
/*
 * valueに含まれる実体参照文字を元の文字に変換して返す.
 *
 * @value 対象の文字列
 * @return 変換後の文字列
 */
unescape:
    function(value) {
        var result = value;
        result = result.replace(/&lt;/g, "<");
        result = result.replace(/&gt;/g, ">");
        result = result.replace(/&quot;/g, "\"");
        result = result.replace(/&amp;/g, "&");

        return result;
    },
/*
 * マッチした要素に対して何か処理を行う.
 * 現状は何もしない.
 */
applySelect:
    function() {
        //TODO 選択されたオプションだけ上に移動するなど、必要であればここで行う
    },
// Util
_bind:
    function(func) {
        var self = this;
        var args = Array.prototype.slice.call(arguments, 1);
        return function(){ func.apply(self, args); };
    }
};
