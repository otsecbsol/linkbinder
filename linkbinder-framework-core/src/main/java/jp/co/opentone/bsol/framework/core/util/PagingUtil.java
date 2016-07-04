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
package jp.co.opentone.bsol.framework.core.util;

import java.io.Serializable;

/**
 * ページ遷移に関する処理ユーティリティ.
 * <p>
 * @author opentone
 */
public class PagingUtil implements Serializable {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 6473860104859373026L;

    /**
     * ページリンク文字列生成数の基準となる値.
     */
    private static final float CENTER = 0.5f;
    /**
     * ページリンク文字列生成時に加算する値.
     */
    private static final int ADD = 1;

    /**
     * 空のインスタンスを生成する.
     * 外部からのインスタンス化禁止.
     */
    private PagingUtil() {
    }

    /**
     * 画面表示件数の文字列を取得する. 形式は「start-end」.
     * @param pageNo
     *            現在のページ
     * @param pageRowNum
     *            1ページあたりの表示件数
     * @param dataCount
     *            総レコード数
     * @return 画面表示件数
     */
    public static String getPageDisplayNo(int pageNo, int pageRowNum, int dataCount) {
        int start = (pageNo - 1) * pageRowNum + 1;
        int end = pageNo * pageRowNum;
        if (dataCount < end) {
            end = dataCount;
        }
        if (end == 0) {
            start = 0;
        }

        return ConvertUtil.formatNumber(start) + "-" + ConvertUtil.formatNumber(end);
    }

    /**
     * ページリンクの文字列を取得する.
     * @param pageIndex
     *            リンクの表示数
     * @param pageNo
     *            現在のページ
     * @param dataCount
     *            総レコード数
     * @param pageRowNum
     *            1ページあたりのレコード表示件数
     * @return ページリンク用配列
     */
    public static String[] getPagingNo(int pageIndex, int pageNo, int dataCount, int pageRowNum) {
        float pageIndexNof = pageIndex;
        float pageNof = pageNo;

        int allPage = getAllPage(dataCount, pageRowNum);
        int startPage = 1;
        float pagePoint = pageNof / pageIndexNof;
        if ((pagePoint) > CENTER) {
            startPage = pageNo - Math.round(CENTER * pageIndexNof) + ADD;
        }
        int endPage = startPage + pageIndex - 1;
        if (endPage > allPage) {
            endPage = allPage;
            startPage = allPage - pageIndex + 1;
            if (startPage < 1) {
                startPage = 1;
            }
        }

        String pagingNo = "";
        for (int i = startPage; i <= endPage; i++) {
            pagingNo += i + ",";
        }
        if (pagingNo.length() > 0) {
            pagingNo = pagingNo.substring(0, pagingNo.length() - 1);
        }
        return pagingNo.split(",");
    }

    /**
     * 総ページ数を取得する.
     * @param dataCount
     *            総レコード数
     * @param pageRowNum
     *            1ページあたりのレコード表示件数
     * @return 総ページ数
     */
    public static int getAllPage(int dataCount, int pageRowNum) {
        if (pageRowNum == 0) {
            return 0;
        }

        int allPage = dataCount / pageRowNum;
        if (dataCount % pageRowNum != 0) {
            allPage += 1;
        }

        return allPage;
    }
}
