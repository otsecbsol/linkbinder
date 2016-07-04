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
 * RSSコレポンCategory種別.
 * @author opentone
 */
public enum RSSCategory implements Code {

    /**
     * 承認状態がIssuedで、ユーザーがAttentionに設定されている.
     */
    ISSUE_NOTICE_ATTENTION(1, "Issue Notice(Attention)"),
    /**
     * 承認状態がIssuedで、ユーザーがCcに設定されている.
     */
    ISSUE_NOTICE_CC(2, "Issue Notice(Cc)"),
    /**
     * 承認状態がRequest for Checkで、ユーザーがCheckerに設定されている.
     */
    REQUEST_FOR_CHECK(3, "Request for Check"),
    /**
     * 承認状態がRequest for Approvalで、ユーザーがApproverに設定されている.
     */
    REQUEST_FOR_APPROVAL(4, "Request for Approval"),
    /**
     * 承認状態がIssuedで、ユーザーがPerson in Chargeに設定されている.
     */
    PERSON_IN_CHARGE(5, "Person in Charge"),
    /**
     * 承認状態がDeniedで、ユーザーが作成した.
     */
    DENIED(6, "Denied");

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
    private RSSCategory(Integer value, String label) {
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
