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

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.Map;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;

/**
 * @author opentone
 */
public class ElasticsearchClientTest {

    private ElasticsearchConfiguration config;

    @Before
    public void setup() {
        config = ElasticsearchConfiguration.builder()
                .address("192.168.99.100", 9300)
                .index("9-1234-5")
//                .index("es-search-client-test")
                .build();
    }

    @Test
    public void testSearch() throws Exception {
        try (ElasticsearchClient client = new ElasticsearchClient(config)) {
            // 検索条件等、設定
            ElasticsearchSearchOption option = new ElasticsearchSearchOption();
            option.setSearchTypeName("document");     // 検索対象タイプ (インデックス生成時に設定した名前)
            option.setKeyword("文書");                // 検索キーワード
            // 検索対象とするフィールド
            // この場合、 document.titleやdocument.attachments.name が検索対象となる
            option.addSearchFields("title", "body", "attachments.name", "attachments.content");
            // 検索キーワードにマッチした箇所をハイライトするフィールド
            option.addHighlightFields("title", "body", "attachments.name", "attachments.content");

            client.search(option, response -> {
                assertThat(response, notNullValue());

                response.records().forEach(rec -> {
                    // レコードの検証
                    assertThat(rec.getId(), notNullValue());
                    assertThat(rec.getValueAsString("title"), notNullValue());

                    System.out.println(rec.getId());
                    System.out.println(rec.getValueAsString("title"));
                    System.out.println(rec.getValueAsString("body"));

                    // ネストしたフィールド
                    assertThat(rec.getValue("attachments"), notNullValue());
                    Stream<Map<String, Object>> s = rec.getValueAsStream("attachments");
                    s.forEach(a -> {
                        assertThat(a.get("name"), notNullValue());
                        assertThat(a.get("content"), notNullValue());
                        System.out.println(a.get("name"));
                        System.out.println(a.get("content"));
                    });

                    // ハイライトの検証
                    rec.getHighlightedFragments("title").forEach(h -> {
                        assertThat(h, notNullValue());
                        System.out.println(h);
                    });
                });
            });
        }
    }
}
