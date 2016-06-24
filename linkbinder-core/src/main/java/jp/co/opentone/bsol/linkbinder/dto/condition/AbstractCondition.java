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
package jp.co.opentone.bsol.linkbinder.dto.condition;

import jp.co.opentone.bsol.linkbinder.dto.AbstractDto;

/**
 * 検索条件を表すDto.
 *
 * @author opentone
 *
 */
public class AbstractCondition extends AbstractDto {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 5693980451966001867L;

    /**
     * ページ数.
     */
    private int pageNo = 1;

    /**
     * 画面表示件数.
     */
    private int pageRowNum = Integer.MAX_VALUE;

    /**
     * ページ数を取得する.
     * @return ページ数
     */
    public int getPageNo() {
        return pageNo;
    }

    /**
     * ページ数を設定する.
     * @param pageNo ページ数
     */
    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    /**
     * 画面表示件数を取得する.
     * @return 画面表示件数
     */
    public int getPageRowNum() {
        return pageRowNum;
    }

    /**
     * 画面表示件数を設定する.
     * @param pageRowNum 画面表示件数
     */
    public void setPageRowNum(int pageRowNum) {
        this.pageRowNum = pageRowNum;
    }

    /**
     * 取得するレコードのうち、除外する行数を取得する.
     * 戻り値の次の行からレコードを取得する.
     * （例：SkipNumが10→11行目から取得する）
     * @return 取得するレコードのうち、除外する行数
     */
    public int getSkipNum() {
        return (pageNo - 1) * pageRowNum;
    }
}
