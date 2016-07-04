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
package jp.co.opentone.bsol.linkbinder.util.view.correspon;

import java.io.Serializable;

import jp.co.opentone.bsol.linkbinder.dto.CorresponResponseHistory;

/**
 * 応答履歴の1レコードを表す表示用クラス.
 * @author opentone
 */
public class CorresponResponseHistoryModel implements Serializable {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 2331236861512741373L;

    /**
     * コレポン文書.
     */
    private CorresponResponseHistory corresponResponseHistory;

    /**
     * @param corresponResponseHistory
     *            the corresponResponseHistory to set
     */
    public void setCorresponResponseHistory(CorresponResponseHistory corresponResponseHistory) {
        this.corresponResponseHistory = corresponResponseHistory;
    }

    /**
     * @return the corresponResponseHistory
     */
    public CorresponResponseHistory getCorresponResponseHistory() {
        return corresponResponseHistory;
    }
}
