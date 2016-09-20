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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jp.co.opentone.bsol.framework.core.elasticsearch.response.ElasticsearchSearchResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;

import java.io.Serializable;
import java.util.Map;
import java.util.function.Consumer;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Elasticsearch Clientのラッパークラス.
 * @author opentone
 */
public class ElasticsearchClient implements Serializable, AutoCloseable {

    /** 接続クライアント. */
    private Client client;
    /** 接続情報. */
    private ElasticsearchConfiguration config;

    /**
     * 設定情報を指定してインスタンス化する.
     * @param config 設定情報
     */
    public ElasticsearchClient(ElasticsearchConfiguration config) {
        this.config = config;

        TransportClient c = TransportClient.builder().build();
        config.getAddresses().stream().forEach(c::addTransportAddress);
        this.client = c;
    }

    public ElasticsearchConfiguration getConfiguration() {
        return config;
    }

    /**
     * 文書をインデックスに追加する.
     * @param doc 文書
     * @return 結果
     */
    public boolean addToIndex(ElasticsearchDocument doc) {
        ObjectMapper mapper = new ObjectMapper();
        String json;
        try {
            json = mapper.writeValueAsString(doc);
        } catch (JsonProcessingException e) {
            throw new ElasticsearchException("JSONへの変換に失敗しました", e);
        }

        String i = formatIndex(config.getIndex());
        IndexRequest createRequest = new IndexRequest(i, doc.type, doc.id).source(json);
        UpdateRequest updateRequest =
                new UpdateRequest(i, doc.type, doc.id).doc(json).upsert(createRequest);

        UpdateResponse response = client.update(updateRequest).actionGet();
        return response.isCreated();
    }

    public boolean deleteFromIndex(ElasticsearchDocument doc) {
        DeleteResponse response =
                client.prepareDelete(formatIndex(config.getIndex()), doc.type, doc.id).get();
        return response.isFound();
    }

    /**
     * 検索する.
     * @param option 検索オプション
     * @param c 検索結果を処理するオブジェクト
     */
    public void search(ElasticsearchSearchOption option, Consumer<ElasticsearchSearchResponse> c) {
        SearchRequestBuilder request = client.prepareSearch(formatIndex(config.getIndex()))
                .setTypes(option.getSearchTypeName())
                .setSearchType(SearchType.QUERY_THEN_FETCH)
                .setQuery(buildQueryBuilder(option));
        option.getHighlightFields().stream().forEach(request::addHighlightedField);

        if (option.getFrom() >= 0) {
            request.setFrom(option.getFrom()).setSize(option.getSize());
        }
        //TODO scroll

        c.accept(new ElasticsearchSearchResponse(request.execute().actionGet()));
    }

    private QueryBuilder buildQueryBuilder(ElasticsearchSearchOption option) {
        ElasticsearchSearchOption.Operator op = option.getOperator();
        MultiMatchQueryBuilder query = multiMatchQuery(
                option.getKeyword(),
                option.getSearchFields().stream().toArray(String[]::new));
        if (op != null && ElasticsearchSearchOption.Operator.AND == op) {
            query = query.operator(MatchQueryBuilder.Operator.AND);
        }
        if (option.getOptionalSearchConditions().isEmpty()) {
            return query;
        } else {
            BoolQueryBuilder q = boolQuery().must(query);
            option.getOptionalSearchConditions().forEach((k, v) -> {
                q.must(matchQuery(k, v));
            });
            return q;
        }
    }

    public void createIndexIfNotExists(String index, String settingJson, Map<String, String> mappingJson) {
        String formattedIndex = formatIndex(index);
        IndicesAdminClient indicesClient = client.admin().indices();

        IndicesExistsResponse existsResponse = indicesClient.prepareExists(formattedIndex).get();
        if (existsResponse.isExists()) {
            return;
        }

        CreateIndexRequestBuilder builder = indicesClient.prepareCreate(formattedIndex)
                .setSettings(Settings.settingsBuilder().loadFromSource(settingJson));
        mappingJson.forEach((k, v) -> {
            builder.addMapping(k, v);
        });

        CreateIndexResponse indexResponse = builder.get();
        if (!indexResponse.isAcknowledged()) {
            throw new ElasticsearchException(String.format("index %s の作成に失敗しました", index));
        }
    }

    /* (非 Javadoc)
     * @see java.lang.AutoCloseable#close()
     */
    @Override
    public void close() throws Exception {
        if (client != null) {
            client.close();
        }
    }

    public String formatIndex(String index) {
        return index.toLowerCase();
    }
}
