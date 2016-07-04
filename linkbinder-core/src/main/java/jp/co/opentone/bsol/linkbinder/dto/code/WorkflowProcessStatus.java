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
 * 承認作業状態.
 * @author opentone
 */
public enum WorkflowProcessStatus implements Code {

    /**
     * 検証・承認作業前.
     */
    NONE(0, "作業前"),
    /**
     * Checkerに対して検証依頼中.
     */
    REQUEST_FOR_CHECK(1, "検証依頼中"),
    /**
     * Checkerが検証済.
     */
    CHECKED(2, "検証済"),
    /**
     * Approverに対して承認依頼中.
     */
    REQUEST_FOR_APPROVAL(3, "承認依頼中"),
    /**
     * Checker/Approverがコレポン文書を更新.
     */
    UNDER_CONSIDERATION(4, "更新中"),
    /**
     * Checker/Approverがコレポン文書を否認.
     */
    DENIED(5, "否認"),
    /**
     * Approverが承認済.
     */
    APPROVED(6, "承認済");

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
    private WorkflowProcessStatus(Integer value, String label) {
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
