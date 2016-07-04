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
 * メール通知種別.
 *
 * @author opentone
 *
 */
public enum EmailNoticeType {
    /**
     * Request for Check Notice.
     */
    REQUEST_FOR_CHECK_NOTICE("検証依頼"),

    /**
     * Request for Approval Notice.
     */
    REQUEST_FOR_APPROVAL_NOTICE("承認依頼"),

    /**
     * Deny Notice.
     */
    DENY_NOTICE("否認"),

    /**
     * Approval Notice.
     */
    APPROVAL_NOTICE("承認"),

    /**
     * Issue Notice (Attention).
     */
    ISSUE_NOTICE_ATTENTION("発行(To)"),

    /**
     * Issue Notice (Cc).
     */
    ISSUE_NOTICE_CC("発行(Cc)"),

    /**
     * Assignment Notice.
     */
    ASSIGNMENT_NOTICE("担当者設定");

    /**
     * このコードの名前を表すラベル.
     */
    private String label;

    /**
     * 値とラベルを指定してインスタンス化する.
     * @param value
     *            値
     * @param label
     *            ラベル
     */
    private EmailNoticeType(String label) {
        this.label = label;
    }

    /**
     * @return label
     */
    public String getLabel() {
        return label;
    }
}
