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
 * 承認状態.
 * @author opentone
 */
public enum WorkflowStatus implements Code {

    /**
     * Preparerがコレポン文書を作成中.
     */
    DRAFT(0, "作成中"),
    /**
     * 承認フローに設定されたCheckerに対して、検証依頼済.
     */
    REQUEST_FOR_CHECK(1, "検証依頼"),
    /**
     * 承認フローに設定されたChecker/Approverが、当該コレポン文書を更新済.
     */
    UNDER_CONSIDERATION(2, "更新中"),
    /**
     * 承認フローに設定されたApproverに対して、承認依頼済.
     */
    REQUEST_FOR_APPROVAL(3, "承認依頼"),
    /**
     * 承認フローに設定されたChecker/Approverが、当該コレポン文書を否認済.
     */
    DENIED(4, "否認"),
    /**
     * コレポン文書が発行済.
     */
    ISSUED(5, "発行");

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
    private WorkflowStatus(Integer value, String label) {
        this.value = value;
        this.label = label;
    }

    /**
     * 検証・承認依頼中を表す全ての承認状態を返す.
     * @return 承認状態
     */
    public static WorkflowStatus[] getRequesting() {
        return new WorkflowStatus[] {
            WorkflowStatus.REQUEST_FOR_CHECK,
            WorkflowStatus.UNDER_CONSIDERATION,
            WorkflowStatus.REQUEST_FOR_APPROVAL
        };
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
