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
package jp.co.opentone.bsol.linkbinder.dto;

import jp.co.opentone.bsol.framework.core.auth.UserHolder;
import jp.co.opentone.bsol.framework.core.dao.LegacyEntity;

/**
 * テーブル [v_user] の1レコードを表すDto.
 * テーブル [v_correspon_group] のレコードも付随している.
 *
 * @author opentone
 *
 */
public class UserWithGroup extends User implements LegacyEntity, UserHolder {

    /**
     *
     */
    private static final long serialVersionUID = 6230437952352450983L;

    /**
     * 活動単位ID.
     * 「すべて」を表す0を持たない.
     */
    private Long realCorresponGroupId;

    /**
     * @return realCorresponGroupId
     */
    public Long getRealCorresponGroupId() {
        return realCorresponGroupId;
    }

    /**
     * @param realCorresponGroupId セットする realCorresponGroupId
     */
    public void setRealCorresponGroupId(Long realCorresponGroupId) {
        this.realCorresponGroupId = realCorresponGroupId;
    }
}
