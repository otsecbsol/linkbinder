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
package jp.co.opentone.bsol.framework.web.view.util;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * URLに付与するクエリ文字列を生成するためのヘルパクラス.
 * <p>
 * @author opentone
 */
public class QueryParameters implements Serializable {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -5370966790280962186L;

    /**
     * URLパラメーター格納コンテナ.
     */
    private Map<String, Object> parameters = new LinkedHashMap<String, Object>();

    /**
     * 空のインスンタンスを生成する.
     */
    public QueryParameters() {
    }

    /**
     * 最初のパラメーターを指定してインスタンス化する.
     * @param key キー
     * @param value 値
     */
    public QueryParameters(String key, Object value) {
        append(key, value);
    }

    public QueryParameters append(String key, Object value) {
        parameters.put(key, value);
        return this;
    }

    public QueryParameters append(QueryParameters params) {
        for (Map.Entry<String, Object> e : params.parameters.entrySet()) {
            append(e.getKey(), e.getValue());
        }
        return this;
    }

    /**
     * このオブジェクトをクエリ文字列に変換した結果を返す.
     * @return クエリ文字列
     */
    public String toQueryString() {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, Object> e : parameters.entrySet()) {
            if (!first) {
                result.append('&');
            }
            result.append(e.getKey()).append('=').append(e.getValue());
            first = false;
        }
        return result.toString();
    }
}
