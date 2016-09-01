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
package jp.co.opentone.bsol.linkbinder.util.elasticsearch;

import com.google.common.collect.Lists;
import jp.co.opentone.bsol.framework.core.config.SystemConfig;
import jp.co.opentone.bsol.framework.core.elasticsearch.ElasticsearchDocument;
import jp.co.opentone.bsol.framework.core.util.ConvertUtil;
import jp.co.opentone.bsol.framework.core.util.DateUtil;
import jp.co.opentone.bsol.linkbinder.Constants;
import jp.co.opentone.bsol.linkbinder.dto.Attachment;
import jp.co.opentone.bsol.linkbinder.dto.Correspon;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;

import java.util.ArrayList;
import java.util.List;

/**
 * 文書を全文検索格納形式のオブジェクトに変換するクラス.
 * @author opentone
 */
public class CorresponDocumentConverter {

    /**
     * 変換する.
     * 添付ファイルを変換対象に含める場合は {@link #convert(Correspon, List)} を使用すること.
     * @param correspon 文書
     * @return 変換結果
     */
    public static List<ElasticsearchDocument> convert(Correspon correspon) {
        return convert(correspon, new ArrayList<>());
    }

    /**
     * 変換する.
     * @param correspon 文書
     * @param attachments 添付ファイル一覧
     * @return 変換結果
     */
    public static List<ElasticsearchDocument> convert(Correspon correspon, List<Attachment> attachments) {
        List<ElasticsearchDocument> result = Lists.newArrayList();
        CorresponElasticsearchDocument doc = new CorresponElasticsearchDocument();
        doc.id = String.valueOf(correspon.getId());
        doc.type = SystemConfig.getValue(Constants.KEY_ELASTICSEARCH_TYPE_NAME);
        doc.title = correspon.getSubject();
        doc.body = Jsoup.parse(correspon.getBody()).text();
        doc.lastModified = DateUtil.convertDateToString(correspon.getUpdatedAt());
        doc.workflowStatus = String.valueOf(correspon.getWorkflowStatus().getValue());
        doc.forLearning = String.valueOf(correspon.getForLearning().getValue());

        result.add(doc);

        attachments.forEach(a -> {
            CorresponElasticsearchDocument attachmentDoc = new CorresponElasticsearchDocument();
            attachmentDoc.id = ObjectUtils.toString(a.getId()) + "@" + correspon.getId();
            attachmentDoc.type = doc.type;
            attachmentDoc.title = StringUtils.EMPTY;
            attachmentDoc.body = StringUtils.EMPTY;
            attachmentDoc.lastModified = doc.lastModified;
            attachmentDoc.workflowStatus = doc.workflowStatus;
            attachmentDoc.workflowStatus = doc.forLearning;

            attachmentDoc.attachments = Lists.newArrayList(
                    new CorresponElasticsearchDocument.Attachment(
                            ObjectUtils.toString(a.getId()),
                            a.getFileName(),
                            ConvertUtil.toBase64String(a.getContent()),
                            a.getExtractedText())
            );

            result.add(attachmentDoc);
        });

        return result;
    }
}
