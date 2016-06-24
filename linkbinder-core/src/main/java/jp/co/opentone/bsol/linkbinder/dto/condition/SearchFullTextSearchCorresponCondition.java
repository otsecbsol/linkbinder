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

import jp.co.opentone.bsol.linkbinder.dto.code.FullTextSearchMode;

/**
 * 全文検索の検索条件を表すDto.
 *
 * @author opentone
 *
 */
public class SearchFullTextSearchCorresponCondition extends AbstractCondition {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 217469472228960805L;

    /**
     * 検索キーワード.
     */
    private String keyword;

    /**
     * 検索対象.
     */
    private FullTextSearchMode fullTextSearchMode;

    /**
     * 空のインスタンスを生成する.
     */
    public SearchFullTextSearchCorresponCondition() {
    }

    /**
     * 検索キーワードを返却する.
     * @return keyword
     */
    public String getKeyword() {
        return keyword;
    }

    /**
     * 検索キーワードの値を設定する.
     * @param keyword 検索キーワード
     */
    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    /**
     * 検索対象を返却する.
     * @return fullTextSearchMode
     */
    public FullTextSearchMode getFullTextSearchMode() {
        return fullTextSearchMode;
    }

    /**
     * 検索対象を設定する.
     * @param fullTextSearchMode 検索対象
     */
    public void setFullTextSearchMode(FullTextSearchMode fullTextSearchMode) {
        this.fullTextSearchMode = fullTextSearchMode;
    }
}