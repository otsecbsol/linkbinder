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

import java.util.List;

/**
 * 活動単位とそれに所属するユーザーの情報.
 *
 * @author opentone
 *
 */
public class CorresponGroupUserMapping extends AbstractDto {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 6415419377568314651L;

    /**
     * 活動単位ID.
     */
    private Long corresponGroupId;

    /**
     * 活動単位に所属するユーザー.
     */
    private List<UserWithGroup> users;

    /**
     * 空のインスタンスを生成する.
     */
    public CorresponGroupUserMapping() {
    }

    /**
     * @param corresponGroupId the corresponGroupId to set
     */
    public void setCorresponGroupId(Long corresponGroupId) {
        this.corresponGroupId = corresponGroupId;
    }

    /**
     * @return the corresponGroupId
     */
    public Long getCorresponGroupId() {
        return corresponGroupId;
    }

    /**
     * @param users the empNos to set
     */
    public void setUsers(List<UserWithGroup> users) {
        this.users = users;
    }

    /**
     * @return the users
     */
    public List<UserWithGroup> getUsers() {
        return users;
    }
}
