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

import jp.co.opentone.bsol.framework.core.elasticsearch.ElasticsearchDocument;

import java.io.Serializable;
import java.util.List;

/**
 * @author opentone
 */
public class CorresponElasticsearchDocument extends ElasticsearchDocument implements Serializable {

    public String title;
    public String body;
    public String lastModified;
    public String workflowStatus;
    public String forLearning;

    public List<Attachment> attachments;

    public static class Attachment {
        public String id;
        public String name;
        public String content;
        public String extractedText;

        public Attachment(String id, String name, String content, String extractedText) {
            this.id = id;
            this.name = name;
            this.content = content;
            this.extractedText = extractedText;
        }
    }
}
