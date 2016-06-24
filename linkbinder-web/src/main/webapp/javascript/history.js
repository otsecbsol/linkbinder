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

var MIN_WIDTH = 930 ;
var H_DIV_ID;
var B_DIV_ID;
var H_TABLE_ID;
var B_TABLE_ID;
var OVER_10_FLG;

/*
 * 検索結果テーブルをスクロールさせる.
 */
function tableScroller(tableId, tableHeight, rowHeight) {
    if(!document.createElement) return;
    if(navigator.userAgent.match('Opera')) return;
    var table = document.getElementById(tableId);
    if (!table) return;
    if (table.tBodies[0].rows.length <= 1) return;

    // テーブルの複製を作成　idを元のid+'_H'とし、tbodyの中身を削除
    var table1 = table.cloneNode(true);
    table1.id += '_H';
    table1.style.position = 'relative';
    table1.style.left = '0';
    table1.style.top = '0';
    H_TABLE_ID = table1.id;

    // 新規DIV - ヘッダ部用 を作成
    var newDiv1 = document.createElement('div');
    newDiv1.id = 'D_' + table1.id;
    newDiv1.style.height = '24px';
    newDiv1.style.overflow = 'hidden';
    newDiv1.style.position = 'relative';
    newDiv1.style.margin = '0px';
    newDiv1.appendChild(table1);
    table.parentNode.insertBefore(newDiv1, table);
    H_DIV_ID = newDiv1.id;

    // テーブルの複製を作成　ヘッダ部を削除
    var table2 = table.cloneNode(true);
    table2.id += '_B';
    table2.deleteTHead();
    B_TABLE_ID = table2.id;

    // 10行を超える場合
    if (table2.tBodies[0].rows.length > 10) {
        OVER_10_FLG = true;
    }
    // 新規DIV - ボディ部用 を作成
    var newDiv2 = document.createElement('div');
    newDiv2.id = 'D_' + table2.id;
    if (OVER_10_FLG) {
        newDiv2.style.overflowX = 'hidden';
        newDiv2.style.overflowY = 'auto';
    } else {
        newDiv2.style.overflow = 'hidden';
    }
    newDiv2.style.height = Math.min(tableHeight, (table2.tBodies[0].rows.length * rowHeight) + 2) + 'px';
    newDiv2.style.padding = '0px';
    newDiv2.style.margin = '0px';

    newDiv2.appendChild(table2);
    B_DIV_ID = newDiv2.id;

    table1.tHead.rows[0].cells[0].className = 'history-corresponNo';
    table1.tHead.rows[0].cells[1].className = 'history-issuedOn';
    table1.tHead.rows[0].cells[2].className = 'history-from';
    table1.tHead.rows[0].cells[3].className = 'history-to';
    table1.tHead.rows[0].cells[4].className = 'history-subject';

    table2.tBodies[0].rows[0].cells[0].className = 'history-corresponNo';
    table2.tBodies[0].rows[0].cells[1].className = 'history-issuedOn';
    table2.tBodies[0].rows[0].cells[2].className = 'history-from';
    table2.tBodies[0].rows[0].cells[3].className = 'history-to';
    table2.tBodies[0].rows[0].cells[4].className = 'history-subject';

    table1.style.tableLayout = 'fixed';
    table2.style.tableLayout = 'fixed';

    table.parentNode.insertBefore(newDiv2, table);

    // 元テーブルを削除
    table.parentNode.removeChild(table);

    initializeHistory();
}

/**
 * 応答履歴テーブルのスクロール設定を初期化する.
 */
function initializeHistory() {
    resizeTable();
    window.onresize = resizeTable;

    var headDiv = document.getElementById(H_DIV_ID);
    var bodyDiv = document.getElementById(B_DIV_ID);

    // 横スクロールバーの同期
    addListener(bodyDiv, 'scroll', function(e) {headDiv.scrollLeft = bodyDiv.scrollLeft;}, false);
}

/**
 * テーブルとその外枠の幅をウィンドウサイズに合わせて再設定する.
 * 10行を超える場合とそうでない場合で設定を変える.
 */
function resizeTable() {
    var winWidth = getWindowSize() ;
    var tmp = winWidth / 10000; // 誤差を小さくするため桁を大き目に設定
    winWidth = Math.floor(tmp * 9800); // contentが98%のため

    winWidth = winWidth - 58; // content,main,detailのmargin、padding、borderの合計
    if (OVER_10_FLG) {
        document.getElementById(B_DIV_ID).style.width = winWidth + 'px';
        winWidth = winWidth -17 ;
        document.getElementById(H_DIV_ID).style.width = winWidth + 'px';
    } else {
        document.getElementById(H_DIV_ID).style.width = winWidth + 'px';
        document.getElementById(B_DIV_ID).style.width = winWidth + 'px';
    }

    if (winWidth > MIN_WIDTH) {
        document.getElementById(H_TABLE_ID).style.width = winWidth+ 'px';
        document.getElementById(B_TABLE_ID).style.width = winWidth + 'px';
    }
}

function getWindowSize() {
    var nIE = navigator.userAgent.indexOf("MSIE");
    var bIE6std = nIE >= 0 && navigator.userAgent.substr(nIE+5, 1) == "6"
                && document.compatMode && document.compatMode=="CSS1Compat";
    var nWidth;
    if (bIE6std) {         /* IE6スタンダードモードの時 */
        nWidth = document.documentElement.clientWidth;
    } else if (nIE >= 0) { /* IEの時 */
        nWidth = document.body.clientWidth;
    } else {               /* IE以外のとき */
        nWidth = window.innerWidth;
    }
    return nWidth;
}

/**
 * オブジェクトにイベントをセットする。
 *
 * @param elem オブジェクト
 * @param eventTyle トリガーになるイベント
 * @param func イベント発生時に実行されるアクション
 * @param cap
 */
function addListener(elem, eventType, func, cap) {
    if (elem.addEventListener) {
        elem.addEventListener(eventType, func, cap) ;
    }
    else if (elem.attachEvent) {
        elem.attachEvent('on' + eventType, func) ;
    }
    else {
        return false ;
    }
} // addListener()
