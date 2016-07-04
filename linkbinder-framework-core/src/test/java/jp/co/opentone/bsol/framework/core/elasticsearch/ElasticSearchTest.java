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

import static org.elasticsearch.common.xcontent.XContentFactory.*;
import static org.elasticsearch.index.query.QueryBuilders.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.highlight.HighlightField;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * ElasticSearchクライアントの動作検証.
 * @author opentone
 */
public class ElasticSearchTest {

    static Client client;

    private String index;

    @BeforeClass
    public static void classSetup() throws Exception {
        // REST APIのポートは9200だが、Java APIからの接続ポートは9300
        InetAddress address = InetAddress.getByName("192.168.99.100");
        client = TransportClient.builder().build()
                .addTransportAddress(new InetSocketTransportAddress(address, 9300));
    }

    @AfterClass
    public static void classTeardown() {
        if (client != null) {
            client.close();
        }
    }

    @Before
    public void setup() throws Exception {
        if (StringUtils.isNotEmpty(index)) {
            createIndex(index);
        }
    }

    @Test
    public void teardown() {
        if (StringUtils.isNotEmpty(index)) {
            try {
                deleteIndex(index);
            } catch (Exception ignore) {
                ignore.printStackTrace();
            }
        }
    }

    private CreateIndexResponse createIndex(String index) throws Exception {
        String settings = jsonBuilder()
                        .startObject()
                            .startObject("analysis")
                                .startObject("filter")
                                    .startObject("greek_lowercase_filter")
                                        .field("type", "lowercase")
                                        .field("language", "greek")
                                    .endObject()
                                .endObject()
                                .startObject("analyzer")
                                    .startObject("kuromoji_analyzer")
                                        .field("type", "custom")
                                        .field("tokenizer", "kuromoji_tokenizer")
                                        .field("filter", new String[] { "kuromoji_baseform", "greek_lowercase_filter", "cjk_width"})
                                    .endObject()
                                .endObject()
                            .endObject()
                        .endObject().string();

        String mapping = jsonBuilder()
                .startObject()
                .startObject("document")
                    .startObject("properties")
                        .startObject("title")
                            .field("type", "string")
                            .field("store", "yes")
                            .field("analyzer", "kuromoji_analyzer")
                        .endObject()
                        .startObject("body")
                            .field("type", "string")
                            .field("store", "yes")
                            .field("analyzer", "kuromoji_analyzer")
                        .endObject()
                        .startObject("attachments")
                            .startObject("properties")
                                .startObject("name")
                                    .field("type", "string")
                                    .field("store", "yes")
                                    .field("analyzer", "kuromoji_analyzer")
                                .endObject()
                                .startObject("content")
                                    .field("type", "string")
                                    .field("store", "yes")
                                    .field("analyzer", "kuromoji_analyzer")
                                .endObject()
                            .endObject()
                        .endObject()
                    .endObject()
                .endObject()
            .endObject().string();

        System.out.println(settings);
        System.out.println("---------------------------");
        System.out.println(mapping);

        IndicesAdminClient indicesClient = client.admin().indices();
        return indicesClient.prepareCreate(index)
                .setSettings(Settings.settingsBuilder().loadFromSource(settings))
                .addMapping("document", mapping)
                .get();
        /*
         *
         *     curl -XPOST http://192.168.99.100:9200/0-1234-5 -d ' {
     "index": {
      "analysis":  {
         "filter": {
           "greek_lowercase_filter": {"type": "lowercase", "language": "greek"}
        },
        "analyzer": {
          "kuromoji_analyzer": {
            "type": "custom",
            "tokenizer": "kuromoji_tokenizer",
            "filter": ["kuromoji_baseform", "greek_lowercase_filter", "cjk_width"]}
        }
        }
      }
    }'

         */
    }

    private DeleteIndexResponse deleteIndex(String index) {
        IndicesAdminClient indicesClient = client.admin().indices();
        return indicesClient.prepareDelete(index).get();
    }

    /**
     * index作成.
     */
    @Test
    public void testCreateIndex() throws Exception {
        // index名は英数小文字のみ。記号はハイフンであれば許容されるよう
        String index = "9-1234-5";
//        String index = "01test-create-index";

        try {
            CreateIndexResponse response = createIndex(index);
            assertThat(response.isAcknowledged(), is(true));
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            // 後始末
            DeleteIndexResponse deleteIndexResponse = deleteIndex(index);
            assertThat(deleteIndexResponse.isAcknowledged(), is(true));
        }
    }

    private void addDocuments(String index) throws Exception {
        Document doc = new Document();
        doc.id = 1L;
        doc.projectId = index;
        doc.title = "最初の文書";
        doc.body = "これは最初に登録した文書です。" + "<p>HTMLタグも含まれています。</p>";

        doc.attachments = new ArrayList<>();
        Attachment a1 = new Attachment();
        a1.name = "参考資料.xls";
        a1.content = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa 参考資料です。";
        doc.attachments.add(a1);

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(doc);

        System.out.println(json);

        IndexResponse response = client
                .prepareIndex(index, "document", String.valueOf(doc.id))
                .setSource(json)
                .get();
        System.out.println(response.toString());

        doc = new Document();
        doc.id = 2L;
        doc.projectId = index;
        doc.title = "2番目の文書";
        doc.body = "次に登録した文書です。" + "<p>HTMLタグも含まれています。</p>";

        doc.attachments = new ArrayList<>();
        a1 = new Attachment();
        a1.name = "参考資料Ver2.xls";
        a1.content = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa 参考資料です。";
        doc.attachments.add(a1);
        a1 = new Attachment();
        a1.name = "参考資料Ver3.xls";
        a1.content = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa 参考資料です。";
        doc.attachments.add(a1);

        mapper = new ObjectMapper();
        json = mapper.writeValueAsString(doc);

        System.out.println(json);

        response = client
                .prepareIndex(index, "document", String.valueOf(doc.id))
                .setSource(json)
                .get();

        System.out.println(response.toString());

    }
    @Test
    public void testAddDocument() throws Exception {
        String index = "9-1234-5";
        try {
            createIndex(index);
            addDocuments(index);
        } finally {
            deleteIndex(index);
        }
    }

    @Test
    public void testSearch() throws Exception {
        String index = "9-1234-5";
        try {
//            createIndex(index);
//            addDocuments(index);

            SearchResponse response = client.prepareSearch(index)
                .setTypes("document")
                .setSearchType(SearchType.QUERY_THEN_FETCH)
//                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                //  multiMatchQuery
                //    1番目: 検索キーワード
                //    2番目以降: 検索対象フィールド名
                //      ネストしたオブジェクトのフィールド名指定方法に注意
                .setQuery(multiMatchQuery("文書 HTML 参考", "title", "body", "attachments.name", "attachments.content"))
                //  addHighlightedField
                //    検索キーワードにマッチした部分文字列を返すための設定
                .addHighlightedField("title")
                .addHighlightedField("body")
                .addHighlightedField("attachments.name")
                .addHighlightedField("attachments.content")
//                //  matchQuery
//                //    1番目: 検索対象フィールド名
//                //    2番目: 検索ワード
//                .setQuery(matchQuery("title", "文書"))
//                .setPostFilter(postFilter)
                .setFrom(0).setSize(10)
//                .setHighlighterFilter(true)
                .execute()
                .actionGet();

            assertThat(response.getHits().hits().length, is(not(0)));

            for (SearchHit hit : response.getHits().hits()) {
                System.out.println("***** " + hit.getSourceAsString());
                for (Entry<String, HighlightField> e : hit.getHighlightFields().entrySet()) {
                    System.out.println("  " + e.getKey() + "=" + e.getValue());
                }
            }
        } finally {
//            deleteIndex(index);
        }
    }

    public static class Document {
        public Long id;
        @JsonIgnore
        public String projectId;
        public String title;
        public String body;

        public List<Attachment> attachments;
    }

    public static class Attachment {
        public String name;
        public String content;
    }
}
