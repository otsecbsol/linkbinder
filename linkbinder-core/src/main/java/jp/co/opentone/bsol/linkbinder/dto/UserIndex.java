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

import jp.co.opentone.bsol.linkbinder.util.ValueFormatter;

/**
 * ユーザー一覧表示用のDto.
 *
 * @author opentone
 *
 */
public class UserIndex extends AbstractDto {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -3952980704743641724L;

    /**
     * プロジェクトユーザー.
     */
    private ProjectUser projectUser;

    /**
     * 代表の活動単位.
     * デフォルト活動単位がセットされる.
     * デフォルト活動単位がない場合、ユーザーが所属するIDの一番古い活動単位がセットされる.
     */
    private CorresponGroup corresponGroup;

    /**
     * SystemAdminフラグ.
     */
    private boolean systemAdmin;

    /**
     * ProjectAdminフラグ.
     */
    private boolean projectAdmin;

    /**
     * GroupAdminフラグ.
     */
    private boolean groupAdmin;

    /**
     * ユーザー設定情報の更新可能フラグ.
     */
    private boolean permitUpdate;

    /**
     * userを取得します.
     * @return the user
     */
    public ProjectUser getProjectUser() {
        return projectUser;
    }

    /**
     * userを設定します.
     * @param user the user to set
     */
    public void setProjectUser(ProjectUser user) {
        this.projectUser = user;
    }

    /**
     * corresponGroupを取得します.
     * @return the corresponGroup
     */
    public CorresponGroup getCorresponGroup() {
        return corresponGroup;
    }

    /**
     * corresponGroupを設定します.
     * @param corresponGroup the corresponGroup to set
     */
    public void setCorresponGroup(CorresponGroup corresponGroup) {
        this.corresponGroup = corresponGroup;
    }

    /**
     * systemAdminを取得します.
     * @return the systemAdmin
     */
    public boolean isSystemAdmin() {
        return systemAdmin;
    }

    /**
     * systemAdminを設定します.
     * @param systemAdmin the systemAdmin to set
     */
    public void setSystemAdmin(boolean systemAdmin) {
        this.systemAdmin = systemAdmin;
    }

    /**
     * projectAdminを取得します.
     * @return the projectAdmin
     */
    public boolean isProjectAdmin() {
        return projectAdmin;
    }

    /**
     * projectAdminを設定します.
     * @param projectAdmin the projectAdmin to set
     */
    public void setProjectAdmin(boolean projectAdmin) {
        this.projectAdmin = projectAdmin;
    }

    /**
     * groupAdminを取得します.
     * @return the groupAdmin
     */
    public boolean isGroupAdmin() {
        return groupAdmin;
    }

    /**
     * groupAdminを設定します.
     * @param groupAdmin the groupAdmin to set
     */
    public void setGroupAdmin(boolean groupAdmin) {
        this.groupAdmin = groupAdmin;
    }

    /**
     * permitUpdateを取得します.
     * @return the permitUpdate
     */
    public boolean isPermitUpdate() {
        return permitUpdate;
    }

    /**
     * permitUpdateを設定します.
     * @param permitUpdate the permitUpdate to set
     */
    public void setPermitUpdate(boolean permitUpdate) {
        this.permitUpdate = permitUpdate;
    }

    /**
     * 表示用：SystemAdminの表示用文字列を取得します.
     * @return SystemAdminの表示用文字列
     */
    public String getSystemAdminLabel() {
        return ValueFormatter.formatLabel(isSystemAdmin(), true);
    }

    /**
     * 表示用：ProjectAdminの表示用文字列を取得します.
     * @return ProjectAdminの表示用文字列
     */
    public String getProjectAdminLabel() {
        return ValueFormatter.formatLabel(isProjectAdmin(), true);
    }

    /**
     * 表示用：GroupAdminの表示用文字列を取得します.
     * @return GroupAdminの表示用文字列
     */
    public String getGroupAdminLabel() {
        return ValueFormatter.formatLabel(isGroupAdmin(), true);
    }
}
