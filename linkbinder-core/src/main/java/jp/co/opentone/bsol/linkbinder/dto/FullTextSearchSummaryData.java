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
package jp.co.opentone.bsol.linkbinder.dto;

import java.io.Serializable;

/**
 * コレポン文書全文検索結果情報の要約.
 * <p>
 * 要約文言の太字制御のプロパティを定義する.
 *
 * @author opentone
 *
 */
public class FullTextSearchSummaryData implements Serializable {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -519106072053943199L;
    /**
     * 要約内容.
     */
    private String value;
    /**
     * JSFのescapeプロパティにリンク.
     */
    private boolean escape;

    /**
     * コントラクタ.
     *
     * @param value
     *           要約内容
     * @param escape
     *           escapeプロパティ
     */
    public FullTextSearchSummaryData(String value, boolean escape) {
        this.value = value;
        this.escape = escape;
    }

    /**
     * 要約内容を取得する.
     *
     * @return  value
     */
    public String getValue() {
        return value;
    }

    /**
     * escapeプロパティを取得する.
     *
     * @return  escape
     */
    public boolean getEscape() {
        return escape;
    }


}
