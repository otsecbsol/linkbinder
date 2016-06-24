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
 * Distribution宛先種別を表すEnum.
 *
 * @author opentone
 *
 */
public enum DistributionType implements Code {

    /**
     * TO.
     */
    TO(1, "TO"),

    /**
     * CC.
     */
    CC(2, "CC");

    /**
     * このコードの値.
     */
    private Integer value;

    /**
     * このコードのラベル.
     */
    private String label;

    /**
     * 値とラベルを指定してインスタンス化する.
     * @param value 値
     */
    private DistributionType(Integer value, String label) {
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
