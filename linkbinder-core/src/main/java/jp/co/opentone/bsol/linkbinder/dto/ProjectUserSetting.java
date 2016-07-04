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

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import org.hibernate.validator.constraints.Length;

import jp.co.opentone.bsol.framework.core.validation.constraints.Required;

/**
 * ユーザー設定情報（プロジェクト単位）のDto.
 *
 * @author opentone
 *
 */
public class ProjectUserSetting extends AbstractDto {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -8067408822614808709L;

    /**
     * 活動単位選択リストのラベル形式.
     */
    public static final String LABEL_GROUP_SELECT_ITEM = "%s %s";
    /**
     * 活動単位選択リスト：Group Admin.
     */
    public static final String LABEL_GROUP_ADMIN = "(Group Admin)";
    /**
     * 活動単位選択リスト：Normal User.
     */
    public static final String LABEL_NORMAL_USER = "(Normal User)";

    /**
     * ユーザー情報.
     */
    private User user;

    /**
     * プロジェクト.
     */
    private Project project;

    /**
     * プロジェクト - ユーザー情報.
     */
    private ProjectUser projectUser;

    /**
     * 活動単位 - ユーザー一覧.
     */
    private List<CorresponGroupUser> corresponGroupUserList = new ArrayList<CorresponGroupUser>();

    /**
     * 選択肢：活動単位.
     */
    private List<SelectItem> groupSelectItems = new ArrayList<SelectItem>();

    /**
     * 設定するデフォルト活動単位ID.
     */
//    @Transfer
//    @SkipValidation("#{!userSettingsPage.saveAction || row.groupListEmpty}")
    @Required
    private Long defaultCorresponGroupId;

    /**
     * 設定するRole.
     */
//    @Transfer
//    @SkipValidation("#{!userSettingsPage.saveAction}")
    //CHECKSTYLE:OFF
    @Length(max = 50)
    //CHECKSTYLE:ON
    private String role;

    /**
     * インスタンスを生成する.
     */
    public ProjectUserSetting() {
    }

    /**
     * projectを取得します.
     * @return the project
     */
    public Project getProject() {
        return project;
    }

    /**
     * projectを設定します.
     * @param project the project to set
     */
    public void setProject(Project project) {
        this.project = project;
    }

    /**
     * projectUserを取得します.
     * @return the projectUser
     */
    public ProjectUser getProjectUser() {
        return projectUser;
    }

    /**
     * corresponGroupUserListを取得します.
     * @return the corresponGroupUserList
     */
    public List<CorresponGroupUser> getCorresponGroupUserList() {
        return corresponGroupUserList;
    }

    /**
     * projectUserを設定します.
     * @param projectUser the projectUser to set
     */
    public void setProjectUser(ProjectUser projectUser) {
        this.projectUser = projectUser;
    }

    /**
     * corresponGroupUserListを設定します.
     * @param corresponGroupUserList the corresponGroupUserList to set
     */
    public void setCorresponGroupUserList(List<CorresponGroupUser> corresponGroupUserList) {
        this.corresponGroupUserList = corresponGroupUserList;
    }

    /**
     * defaultCorresponGroupIdを取得します.
     * @return the defaultCorresponGroupId
     */
    public Long getDefaultCorresponGroupId() {
        return defaultCorresponGroupId;
    }

    /**
     * defaultCorresponGroupIdを設定します.
     * @param defaultCorresponGroupId the defaultCorresponGroupId to set
     */
    public void setDefaultCorresponGroupId(Long defaultCorresponGroupId) {
        this.defaultCorresponGroupId = defaultCorresponGroupId;
    }

    /**
     * roleを取得します.
     * @return the role
     */
    public String getRole() {
        return role;
    }

    /**
     * roleを設定します.
     * @param role the role to set
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * 画面表示判定用：corresponGroupUserListか空かどうか判定します.
     * @return true = リストが空
     */
    public boolean isGroupListEmpty() {
        return corresponGroupUserList.isEmpty();
    }

    /**
     * groupSelectItemsを設定します.
     * @param groupSelectItems the groupSelectItems to set
     */
    public void setGroupSelectItems(List<SelectItem> groupSelectItems) {
        this.groupSelectItems = groupSelectItems;
    }

    /**
     * groupSelectItemsを取得します.
     * @return the groupSelectItems
     */
    public List<SelectItem> getGroupSelectItems() {
        if (!isGroupListEmpty()) {
            groupSelectItems = new ArrayList<SelectItem>();

            for (CorresponGroupUser gUser : corresponGroupUserList) {
                Long value = gUser.getCorresponGroup().getId();
                String name = gUser.getCorresponGroup().getName();
                String label = String.format(LABEL_GROUP_SELECT_ITEM,
                                             name,
                                             getUserPermissionLabel(gUser));

                SelectItem item = new SelectItem(value, label);
                groupSelectItems.add(item);
            }
        }
        return groupSelectItems;
    }

    private String getUserPermissionLabel(CorresponGroupUser gu) {
        String result;
        if (isGroupAdmin(gu)) {
            result = LABEL_GROUP_ADMIN;
        } else if (isNormalUser()) {
            result = LABEL_NORMAL_USER;
        } else {
            //  その他の場合
            //  ・System Admin
            //  ・Project Admin
            result = "";
        }

        return result;
    }

    /**
     * このユーザーがProject Adminの権限を持つ場合はtrueを返す.
     * @return Project Adminの場合はtrue
     */
    public boolean isProjectAdmin() {
        ProjectUser pu = getProjectUser();
        return pu != null && pu.isProjectAdmin();
    }

    /**
     * 指定されたユーザーがGroup Adminの権限を持つ場合はtrueを返す.
     * @param gu 活動単位ユーザー情報
     * @return Gruop Adminの場合はtrue
     */
    public boolean isGroupAdmin(CorresponGroupUser gu) {
        return gu != null && gu.isGroupAdmin();
    }

    /**
     * このユーザーがNormal Userである場合はtrueを返す.
     * <p>
     * Normal Userとは、次の状態のこと.
     * <ul>
     * <li>System Adminでない</li>
     * <li>このプロジェクトでProject Adminではない</li>
     * <li>このプロジェクト内でユーザーが所属する全ての活動単位についてGroup Adminではない</li>
     * </ul>
     * @return Normal Userの場合はtrue
     */
    public boolean isNormalUser() {
        User u = getUser();
        if (u != null && u.isSystemAdmin()) {
            return false;
        }
        ProjectUser pu = getProjectUser();
        if (pu != null && pu.isProjectAdmin()) {
            return false;
        }
        if (getCorresponGroupUserList() != null) {
            for (CorresponGroupUser gu : getCorresponGroupUserList()) {
                if (isGroupAdmin(gu)) {
                    return false;
                }
            }
        }
        return pu != null;
    }

    /**
     * @param user the user to set
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * @return the user
     */
    public User getUser() {
        return user;
    }
}
