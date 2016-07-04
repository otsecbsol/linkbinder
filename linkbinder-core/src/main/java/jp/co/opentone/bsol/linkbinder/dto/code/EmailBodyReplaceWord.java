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
package jp.co.opentone.bsol.linkbinder.dto.code;

/**
 * メール本文の置換文字.
 *
 * @author opentone
 *
 */
public enum EmailBodyReplaceWord {
    /**
     * corresponId.
     */
    CORRESPON_ID("\\$\\{corresponId\\}"),
    /**
     * corresponNo.
     */
    CORRESPON_NO("\\$\\{corresponNo\\}"),
    /**
     * fromGroup.
     */
    FROM_GROUP("\\$\\{fromGroup\\}"),
    /**
     * from.
     */
    FROM("\\$\\{from\\}"),
    /**
     * toGroup.
     */
    TO_GROUP("\\$\\{toGroup\\}"),
    /**
     * ccGroup.
     */
    CC_GROUP("\\$\\{ccGroup\\}"),
    /**
     * corresponType.
     */
    CORRESPON_TYPE("\\$\\{corresponType\\}"),
    /**
     * subject.
     */
    SUBJECT("\\$\\{subject\\}"),
    /**
     * workflowStatus.
     */
    WORKFLOW_STATUS("\\$\\{workflowStatus\\}"),
    /**
     * createdOn.
     */
    CREATED_ON("\\$\\{createdOn\\}"),
    /**
     * issuedOn.
     */
    ISSUED_ON("\\$\\{issuedOn\\}"),
    /**
     * deadlineForReply.
     */
    DEADLINE_FOR_REPLY("\\$\\{deadlineForReply\\}"),
    /**
     * createdBy.
     */
    CREATED_BY("\\$\\{createdBy\\}"),
    /**
     * issuedBy.
     */
    ISSUED_BY("\\$\\{issuedBy\\}"),
    /**
     * replyRequired.
     */
    REPLY_REQUIRED("\\$\\{replyRequired\\}"),
    /**
     * corresponUrl.
     */
    CORRESPON_URL("\\$\\{corresponUrl\\}");

    /**
     * このコードの名前を表すラベル.
     */
    private String label;

    /**
     * 値とラベルを指定してインスタンス化する.
     *
     * @param value 値
     * @param label ラベル
     */
    private EmailBodyReplaceWord(String label) {
        this.label = label;
    }

    /**
     * @return label
     */
    public String getLabel() {
        return label;
    }
}
