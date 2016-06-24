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
package jp.co.opentone.bsol.framework.core.elasticsearch;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 検索時に指定する各種オプション.
 * @author opentone
 */
public class ElasticsearchSearchOption implements Serializable {

    /**
     * 検索対象オブジェクトの名前.
     * インデックスに登録した type のこと.
     */
    private String searchTypeName;
    /** 検索キーワード. */
    private String keyword;
    /** 検索対象フィールド. */
    private List<String> searchFields = new ArrayList<>();
    /** 検索結果をハイライトするフィールド. */
    private List<String> highlightFields = new ArrayList<>();

    /** 検索位置. */
    private int from = Integer.MIN_VALUE;
    /** 検索件数. */
    private int size;


    public String getKeyword() {
        return keyword;
    }
    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
    public List<String> getSearchFields() {
        return searchFields;
    }
    public void setSearchFields(List<String> searchFields) {
        this.searchFields = searchFields;
    }
    public List<String> getHighlightFields() {
        return highlightFields;
    }
    public void setHighlightFields(List<String> highlightFields) {
        this.highlightFields = highlightFields;
    }
    public int getFrom() {
        return from;
    }
    public void setFrom(int from) {
        this.from = from;
    }
    public int getSize() {
        return size;
    }
    public void setSize(int size) {
        this.size = size;
    }

    public void addHighlightFields(String... fields) {
        Arrays.stream(fields).forEach(highlightFields::add);
    }

    public void addSearchFields(String... fields) {
        Arrays.stream(fields).forEach(searchFields::add);
    }
    public String getSearchTypeName() {
        return searchTypeName;
    }
    public void setSearchTypeName(String searchTypeName) {
        this.searchTypeName = searchTypeName;
    }
}
