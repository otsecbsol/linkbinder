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
 * メール通知受信要否.
 *
 * @author opentone
 *
 */
public enum EmailNoticeReceivable implements Code {
    /**
     * Yes.
     */
    YES(1, "Yes", true),

    /**
     * No.
     */
    NO(0, "No", false);

    /**
     * このコードの値.
     */
    private Integer value;
    /**
     * このコードの名前を表すラベル.
     */
    private String label;
    /**
     * このコードのboolean値.
     */
    private boolean flag;

    /**
     * 値とラベルを指定してインスタンス化する.
     * @param value
     *            値
     * @param label
     *            ラベル
     */
    private EmailNoticeReceivable(Integer value, String label, boolean flag) {
        this.value = value;
        this.label = label;
        this.flag = flag;
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

    /**
     * @return the flag
     */
    public boolean getFlag() {
        return flag;
    }
}
