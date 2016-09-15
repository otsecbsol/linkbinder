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
package jp.co.opentone.bsol.linkbinder.service.correspon.impl;

import jp.co.opentone.bsol.framework.core.config.SystemConfig;
import jp.co.opentone.bsol.framework.core.elasticsearch.ElasticsearchClient;
import jp.co.opentone.bsol.framework.core.elasticsearch.ElasticsearchConfiguration;
import jp.co.opentone.bsol.framework.core.elasticsearch.ElasticsearchException;
import jp.co.opentone.bsol.framework.core.elasticsearch.ElasticsearchSearchOption;
import jp.co.opentone.bsol.framework.core.elasticsearch.response.ElasticsearchSearchResponse;
import jp.co.opentone.bsol.framework.core.elasticsearch.response.ElasticsearchSearchResultRecord;
import jp.co.opentone.bsol.framework.core.exception.ApplicationFatalRuntimeException;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.linkbinder.Constants;
import jp.co.opentone.bsol.linkbinder.dto.Attachment;
import jp.co.opentone.bsol.linkbinder.dto.Correspon;
import jp.co.opentone.bsol.linkbinder.dto.FullTextSearchCorresponsResult;
import jp.co.opentone.bsol.linkbinder.dto.FullTextSearchSummaryData;
import jp.co.opentone.bsol.linkbinder.dto.code.ForLearning;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchFullTextSearchCorresponCondition;
import jp.co.opentone.bsol.linkbinder.service.AbstractService;
import jp.co.opentone.bsol.linkbinder.service.correspon.CorresponFullTextSearchService;
import jp.co.opentone.bsol.linkbinder.util.ResourceUtil;
import jp.co.opentone.bsol.linkbinder.util.elasticsearch.CorresponDocumentConverter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.http.impl.cookie.DateParseException;
import org.apache.http.impl.cookie.DateUtils;
import org.elasticsearch.index.IndexNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * このサービスではコレポン文書全文検索に関する処理を提供する.
 * @author opentone
 */
@Service
@Transactional(rollbackFor = { ServiceAbortException.class })
public class CorresponFullTextSearchServiceImpl extends AbstractService implements
        CorresponFullTextSearchService {
    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 393317024963869994L;

    /**
     * logger.
     */
    private static Logger log = LoggerFactory.getLogger(CorresponFullTextSearchServiceImpl.class);

    /* (非 Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.service.correspon
     *      .CorresponFullTextSearchService#search(
     *      jp.co.opentone.bsol.linkbinder.dto.condition.SearchFullTextSearchCorresponCondition)
     */
    @Override
    @Transactional(readOnly = true)
    public List<FullTextSearchCorresponsResult> search(
            SearchFullTextSearchCorresponCondition condition) throws ServiceAbortException {
        List<FullTextSearchCorresponsResult> result = new ArrayList<>();
        doSearch(condition, false, result::add);

        return result;
    }

    /* (非 Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.service.correspon
     *      .CorresponFullTextSearchService#searchNoLimit(
     *      jp.co.opentone.bsol.linkbinder.dto.condition.SearchFullTextSearchCorresponCondition)
     */
    @Override
    @Transactional(readOnly = true)
    public List<FullTextSearchCorresponsResult> searchNoLimit(
            SearchFullTextSearchCorresponCondition condition) throws ServiceAbortException {
        List<FullTextSearchCorresponsResult> result = new ArrayList<>();
        doSearch(condition, true, result::add);

        return result;
    }

    /* (非 Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.service.correspon
     *      .CorresponFullTextSearchService#searchId(
     *      jp.co.opentone.bsol.linkbinder.dto.condition.SearchFullTextSearchCorresponCondition)
     */
    @Override
    @Transactional(readOnly = true)
    public List<Long> searchId(SearchFullTextSearchCorresponCondition condition)
            throws ServiceAbortException {
        List<Long> result = new ArrayList<>();
        doSearch(condition, false, r -> result.add(r.getId()));

        return result;
    }

    /*
     * (非 Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.service.correspon
     *      .CorresponFullTextSearchService#createIndex()
     */
    @Override
    @Transactional(readOnly = true)
    public void createIndex(String projectId) {
        try (ElasticsearchClient client = new ElasticsearchClient(setupConfiguration(projectId))) {
            createIndex(client);
        } catch (Exception e) {
            throw new ApplicationFatalRuntimeException(e);
        }
    }

    private void createIndex(ElasticsearchClient client) {
        String settingFile = SystemConfig.getValue(Constants.KEY_ELASTICSEARCH_INDEX_SETTING);
        String mappingFile = SystemConfig.getValue(Constants.KEY_ELASTICSEARCH_TYPE_MAPPING);

        try {
            String settingJson = FileUtils.readFileToString(ResourceUtil.getResource(settingFile), "UTF-8");
            String mappingJson = FileUtils.readFileToString(ResourceUtil.getResource(mappingFile), "UTF-8");
            Map<String, String> typeMappings = new HashMap<>();
            typeMappings.put(SystemConfig.getValue(Constants.KEY_ELASTICSEARCH_TYPE_NAME), mappingJson);

            client.createIndexIfNotExists(client.getConfiguration().getIndex(),
                    settingJson,
                    typeMappings);
        } catch (IOException e) {
            throw new ElasticsearchException(e);
        }
    }

    private void doSearch(SearchFullTextSearchCorresponCondition condition,
                boolean unlimited,
                Consumer<FullTextSearchCorresponsResult> c) {
        try (ElasticsearchClient client = new ElasticsearchClient(setupConfiguration(getCurrentProjectId()))) {
            ElasticsearchSearchOption option = setupSearchOption(condition);
            client.search(option, response -> handleResponse(response, condition, c));
        } catch (IndexNotFoundException e) {
            //retry
            createIndex(e.getIndex());
            doSearch(condition, unlimited, c);
        } catch (Exception e) {
            throw new ApplicationFatalRuntimeException(e);
        }
    }

    private ElasticsearchConfiguration setupConfiguration(String projectId) {
        return ElasticsearchConfiguration.builder()
                .address(
                        SystemConfig.getValue(Constants.KEY_ELASTICSEARCH_HOST),
                        NumberUtils.toInt(SystemConfig.getValue(Constants.KEY_ELASTICSEARCH_PORT)))
                .index(projectId)
                .build();
    }

    private ElasticsearchSearchOption setupSearchOption(SearchFullTextSearchCorresponCondition condition) {
        ElasticsearchSearchOption option = new ElasticsearchSearchOption();
        // 検索対象タイプ (インデックス生成時に設定した名前)
        option.setSearchTypeName(SystemConfig.getValue(Constants.KEY_ELASTICSEARCH_TYPE_NAME));
        option.setKeyword(condition.getKeyword());
        if (condition.getOperator() != null) {
            option.setOperator((condition.getOperator().toElasticsearchOperator()));
        }
        // 検索対象とするフィールド
        // この場合、 document.titleやdocument.attachments.name が検索対象となる
        switch (condition.getFullTextSearchMode()) {
        case ALL:
            if (isAcceptableDateString(condition.getKeyword())) {
                option.addSearchFields("title", "body", "lastModified");
                option.addHighlightFields("title", "body", "lastModified");
            } else {
                option.addSearchFields("title", "body");
                option.addHighlightFields("title", "body");
            }
            if (condition.isIncludeNonImage()) {
                option.addSearchFields("attachments.name", "attachments.content.content");
                option.addHighlightFields("attachments.name", "attachments.content.content");
            }
            if (condition.isIncludeImage()) {
                option.addSearchFields("attachments.extractedText");
                option.addHighlightFields("attachments.extractedText");
            }
            break;
        case SUBJECT:
            option.addSearchFields("title");
            option.addHighlightFields("title");
            break;
        case SUBJECT_AND_BODY:
            option.addSearchFields("title", "body");
            option.addHighlightFields("title", "body");
            break;
        case ATTACHED_FILE:
            option.addSearchFields("attachments.name", "attachments.content.content");
            option.addHighlightFields("attachments.name", "attachments.content.content");
            if (condition.isIncludeImage()) {
                option.addSearchFields("attachments.extractedText");
                option.addHighlightFields("attachments.extractedText");
            }
            break;
        }

        if (condition.isOnlyLearningCorrespon()) {
            option.addOptionalSearchCondition("forLearning",
                    String.valueOf(ForLearning.LEARNING.getValue()));
        }

        return option;
    }

    protected boolean isAcceptableDateString(String keyword) {
        final String[] acceptableFormats = {
                "yyyy-MM-dd",
                "yyyyMMdd"
        };

        return Stream.of(StringUtils.split(keyword, ' '))
                .allMatch(s -> {
                    String str = StringUtils.trim(s);
                    try {
                        DateUtils.parseDate(str, acceptableFormats);
                        return true;
                    } catch (DateParseException e) {
                        return false;
                    }
                });
    }

    private Long idToLong(String id) {
        String str;
        if (StringUtils.contains(id, '@')) {
            str = StringUtils.split(id, '@')[1];
        } else {
            str = id;
        }
        return NumberUtils.toLong(str);
    }

    private void handleResponse(ElasticsearchSearchResponse response,
                    SearchFullTextSearchCorresponCondition condition,
                    Consumer<FullTextSearchCorresponsResult> consumer) {
        response.records().forEach(rec -> {
            FullTextSearchCorresponsResult r = new FullTextSearchCorresponsResult();

            r.setId(idToLong(rec.getId()));
            if (rec.getHighlightedFragments("title").count() > 0) {
                r.setTitle(rec.getHighlightedFragments("title").collect(Collectors.joining()));
            } else {
                r.setTitle(rec.getValueAsString("title"));
            }
            r.setMdate(rec.getValueAsString("lastModified"));
            r.setWorkflowStatus(rec.getValueAsString("workflowStatus"));
            Stream<Map<String, Object>> s = rec.getValueAsStream("attachments");
            if (s != null) {
                s.forEach(a -> {
                    r.setTitle(ObjectUtils.toString(a.get("name")));
                    r.setAttachmentId(ObjectUtils.toString(a.get("id")));
                });
            }

            setSummaryData(condition, rec, r);

            consumer.accept(r);
        });
    }

    private void setSummaryData(SearchFullTextSearchCorresponCondition condition,
                    ElasticsearchSearchResultRecord rec,
                    FullTextSearchCorresponsResult r) {
        Stream<String> highlights = null;
        switch (condition.getFullTextSearchMode()) {
        case ALL:
            highlights = Stream.of(
                    rec.getHighlightedFragments("body"),
                    rec.getHighlightedFragments("attachments.name"),
                    rec.getHighlightedFragments("attachments.content.content"))
                .flatMap(Function.identity());
            break;
        case SUBJECT:
            highlights = rec.getHighlightedFragments("title");
            break;
        case SUBJECT_AND_BODY:
            highlights = rec.getHighlightedFragments("body");
            break;
        case ATTACHED_FILE:
            highlights = Stream.of(
                    rec.getHighlightedFragments("attachments.name"),
                    rec.getHighlightedFragments("attachments.content.content"))
                .flatMap(Function.identity());
            break;
        default:
        }

        List<FullTextSearchSummaryData> summaryDataList = highlights.collect(
                () -> new ArrayList<FullTextSearchSummaryData>(),
                (t, v) -> t.add(new FullTextSearchSummaryData(v, false)),
                (t, u) -> t.addAll(u));

        if (condition.isIncludeImage()) {
            summaryDataList.addAll(
                    rec.getHighlightedFragments("attachments.extractedText")
                            .collect(() -> new ArrayList<FullTextSearchSummaryData>(),
                                    (t, v) -> t.add(new FullTextSearchSummaryData(v, false)),
                                    (t, u) -> t.addAll(u)));
        }
        r.setSummaryList(summaryDataList);
    }

    /* (非 Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.service.correspon
     *      .CorresponFullTextSearchService#addToIndex(
     *      jp.co.opentone.bsol.linkbinder.dto.Correspon)
     */
    @Override
    @Transactional(readOnly = true)
    public void addToIndex(Correspon correspon, List<Attachment> attachments) throws ServiceAbortException {
        try (ElasticsearchClient client = new ElasticsearchClient(setupConfiguration(getCurrentProjectId()))) {
            CorresponDocumentConverter
                    .convert(correspon, attachments)
                    .forEach(client::addToIndex);
        } catch (Exception e) {
            throw new ApplicationFatalRuntimeException(e);
        }
    }

    /* (非 Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.service.correspon
     *      .CorresponFullTextSearchService#deleteFromIndex(
     *      jp.co.opentone.bsol.linkbinder.dto.Correspon)
     */
    @Override
    @Transactional(readOnly = true)
    public void deleteFromIndex(Correspon correspon, List<Attachment> attachments) throws ServiceAbortException {
        try (ElasticsearchClient client = new ElasticsearchClient(setupConfiguration(getCurrentProjectId()))) {
            CorresponDocumentConverter
                    .convert(correspon, attachments)
                    .forEach(client::deleteFromIndex);
        } catch (Exception e) {
            throw new ApplicationFatalRuntimeException(e);
        }
    }
}
