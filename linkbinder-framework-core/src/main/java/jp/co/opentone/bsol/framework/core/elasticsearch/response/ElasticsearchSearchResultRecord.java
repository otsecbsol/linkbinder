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
package jp.co.opentone.bsol.framework.core.elasticsearch.response;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.lang.math.NumberUtils;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.highlight.HighlightField;

/**
 * 検索結果1件を表すクラス.
 * @author opentone
 */
public class ElasticsearchSearchResultRecord {

    /** 検索結果の実体. */
    private SearchHit hit;

    /**
     * 対象の検索結果を指定してインスタンス化する.
     * @param hit 検索結果
     */
    ElasticsearchSearchResultRecord(SearchHit hit) {
        this.hit = hit;
    }

    /**
     * IDを返す.
     * @return ID
     */
    public String getId() {
        return hit.getId();
    }

    /**
     * 指定されたフィールドの値を返す.
     * @param fieldName フィールド名
     * @return 値
     */
    public Object getValue(String fieldName) {
        return hit.getSource().get(fieldName);
    }

    /**
     * 指定されたフィールドの値を返す.
     * @param fieldName フィールド名
     * @return 値
     */
    public String getValueAsString(String fieldName) {
        Object val = getValue(fieldName);
        String result = null;
        if (val != null) {
            result = String.valueOf(val);
        }

        return result;
    }

    /**
     * 指定されたフィールドの値を返す.
     * @param fieldName フィールド名
     * @return 値
     */
    public Integer getValueAsInteger(String fieldName) {
        String val = getValueAsString(fieldName);
        Integer result = null;
        if (val != null) {
            result = NumberUtils.toInt(val);
        }

        return result;
    }

    /**
     * 指定されたフィールドへのStreamを返す.
     * リスト的に複数の値が格納されているフィールドではこのメソッドを使用すること.
     *
     * @see Stream
     * @param fieldName フィールド名
     * @return {@link Stream}
     */
    public <T> Stream<T> getValueAsStream(String fieldName) {
        Object val = getValue(fieldName);
        if (val != null && val instanceof List<?>) {
            @SuppressWarnings("unchecked")
            List<T> list = (List<T>) val;
            return list.stream();
        }

        return null;
    }

    /**
     * 検索キーワードにマッチした箇所をハイライトした値へのStreamを返す.
     * @see Stream
     * @param fieldName フィールド名
     * @return {@link Stream}
     */
    public Stream<String> getHighlightedFragments(String fieldName) {
        HighlightField field = hit.getHighlightFields().get(fieldName);
        if (field != null) {
            Stream<Text> s = Arrays.stream(field.getFragments());
            return s.map(t -> t.string());
        }

        return new ArrayList<String>().stream();
    }
}
