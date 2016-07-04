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
 * ポップアップダイアログを表示するクラス.
 *
 * = 使い方
 * -- id:dialogの要素をポップアップ表示する
 *      Dialog.show('dialog');
 *
 * -- id:dialogの要素を閉じる
 *      Dialog.close('dialog');
 *
 * @author opentone
 */
var Dialog = {
    /*
     * ポップアップダイアログを表示する.
     *
     * = 使い方
     *   Dialog.show('dialog');
     *
     * @param id 表示対象要素のID
     * @return 表示した要素
     */
    show: function(id, action, hideCallback) {
        var jqId = this._convertId(id);

        $(jqId).css('position', 'absolute');
        $(jqId).css('z-index', '5000');

        var option = {}
        if (hideCallback) {
            option.onHide = function(hash) {
                var result = hideCallback(hash);
                if (result) {
                    hash.o && hash.o.remove();
                }
                return result;
            }
        }
        Dialog._setPosition($(jqId));
        $(jqId).jqm(option);
        $(jqId).jqmShow();
    },
    /*
     * ポップアップダイアログを閉じる.
     *
     * = 使い方
     *   Dialog.close('dialog');
     *
     * @param id 表示済の要素
     */
    close: function (id) {
        var jqId = this._convertId(id);
        $(jqId).jqmHide();
    },

    _setPosition: function(dialog) {
        var left = Math.floor(($(window).width() - dialog.width()) / 2 + $(window).scrollLeft());
        var top = Math.floor(10 + $(window).scrollTop());
        // 調整
        left -= parseInt($('#content').css('margin-left'));
        top -= $('#content').position().top;
        dialog.css({
            top: top,
            left: left
        });
    },
    _convertId: function(id) {
        return '#' + $.escape(id);
    }
};

/*
 * ポップアップダイアログに関する共通ユーティリティ
 *
 * @author opentone
 */
var DialogUtil = {
/*
 * ポップアップダイアログの表示位置を計算して返す。
 *
 * @param action アクション
 * @param div 表示するダイアログ
 * @return 表示位置
 */
computePosition:
    function(action, div) {
        var size    = this.getElementSize(div);
        var pos     = this.getPosition(action);
        var winSize = this.getWindowSize();

        var x = pos.x;
        if (((x - document.scroll().x) + size.width) > winSize.width) {
            x = winSize.width - size.width + document.scroll().x;
        }
        var y = pos.y;
        if (((y - document.scroll().y) + size.height) > winSize.height) {
            y = winSize.height - size.height + document.scroll().y;
        }

        return new function() { this.x = x; this.y = y; };
    },
/*
 * 指定された要素の位置を返す。
 * @param elem 要素
 * @return サイズ
 */
getElementSize:
    function(elem) {
        var size = new function(){ this.width = 0; this.height = 0; };

        size.width  = elem.clientWidth;
        size.height = elem.clientHeight;

        return size;
    },
/*
 * ウインドウの表示サイズを返す。
 *
 * @return ウインドウのサイズ
 */
getWindowSize:
    function() {
        var size = new function(){ this.width = 0; this.height = 0; };
        if (window.innerWidth) {
            size.width  = window.innerWidth;
            size.height = window.innerHeight;
        } else if (document.documentElement && document.documentElement.clientWidth != 0) {
            size.width  = document.documentElement.clientWidth;
            size.height = document.documentElement.clientHeight;
        } else if (document.body) {
            size.width  = document.body.clientWidth;
            size.height = document.body.clientHeight;
        }
        return size;
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
    }
};

