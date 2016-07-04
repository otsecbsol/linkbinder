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

import jp.co.opentone.bsol.framework.core.dto.Code;

/**
 * メール通知イベントコード.
 *
 * @author opentone
 *
 */
public enum EmailNoticeEventCd implements Code {
    /**
     * Requested for Approval.
     */
    REQUESTED_FOR_APPROVAL(1, "Requested for Approval"),

    /**
     * Checked.
     */
    CHECKED(2, "検証"),

    /**
     * Approved.
     */
    APPROVED(3, "承認"),

    /**
     * Denied.
     */
    DENIED(4, "否認"),

    /**
     * Issued.
     */
    ISSUED(5, "発行"),

    /**
     * Attention/Cc Added.
     */
    ATTENTION_CC_ADDED(6, "宛先追加"),

    /**
     * Assign to.
     */
    ASSIGN_TO(7, "担当者設定");

    /**
     * このコードの値.
     */
    private Integer value;
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
    private EmailNoticeEventCd(Integer value, String label) {
        this.value = value;
        this.label = label;
    }

    /*
     * (non-Javadoc)
     * @see jp.co.opentone.bsol.framework.extension.ibatis.EnumValue#getValue()
     */
    public Integer getValue() {
        return value;
    }

    /*
     * (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.dto.code.Code#getLabel()
     */
    public String getLabel() {
        return label;
    }
}
