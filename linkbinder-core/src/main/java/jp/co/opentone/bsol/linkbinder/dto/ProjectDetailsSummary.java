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

import jp.co.opentone.bsol.framework.core.util.CloneUtil;




/**
 * プロジェクトホームの情報を保持したDto.
 *
 * @author opentone
 *
 */
public class ProjectDetailsSummary extends AbstractDto {

    /**
     * serialVersionUID.
     */
    private static final long serialVersionUID = 4295459640917749583L;

    /**
     * コレポン文書種別と活動単位でのサマリ情報.
     */
    private List<CorresponGroupSummary> corresponGroupSummary;

    /**
     * ユーザーが所属する活動単位.
     * SearchCorresponConditionに渡すために配列として定義.
     */
    private CorresponGroup[] userRelatedCorresponGroups;

    /**
     * ユーザー情報でのサマリ情報.
     */
    private CorresponUserSummary corresponUserSummary;

    /**
     * 会社情報.
     */
    private List<Company> companies;

    /**
     * 活動単位.
     */
    private List<CorresponGroup> corresponGroups;

    /**
     * プロジェクト管理者.
     */
    private List<ProjectUser> projectAdmins;

    /**
     * 部門管理者.
     */
    private List<ProjectUser> groupAdmins;

    /**
     * 一般ユーザー.
     */
    private List<ProjectUser> normalUsers;


    /**
     * 空のインスタンスを生成する.
     */
    public ProjectDetailsSummary() {
    }


    /**
     * corresponGroupSummaryを取得します.
     * @return the corresponGroupSummary
     */
    public List<CorresponGroupSummary> getCorresponGroupSummary() {
        return corresponGroupSummary;
    }


    /**
     * corresponGroupSummaryを設定します.
     * @param corresponGroupSummary the corresponGroupSummary to set
     */
    public void setCorresponGroupSummary(List<CorresponGroupSummary> corresponGroupSummary) {
        this.corresponGroupSummary = corresponGroupSummary;
    }


    /**
     * userRelatedCorresponGroupsを取得します.
     * @return the userRelatedCorresponGroups
     */
    public CorresponGroup[] getUserRelatedCorresponGroups() {
        return CloneUtil.cloneArray(CorresponGroup.class, userRelatedCorresponGroups);
    }


    /**
     * userRelatedCorresponGroupsを設定します.
     * @param userRelatedCorresponGroups the userRelatedCorresponGroups to set
     */
    public void setUserRelatedCorresponGroups(CorresponGroup[] userRelatedCorresponGroups) {
        this.userRelatedCorresponGroups
            = CloneUtil.cloneArray(CorresponGroup.class, userRelatedCorresponGroups);
    }


    /**
     * corresponUserSummaryを取得します.
     * @return the corresponUserSummary
     */
    public CorresponUserSummary getCorresponUserSummary() {
        return corresponUserSummary;
    }


    /**
     * corresponUserSummaryを設定します.
     * @param corresponUserSummary the corresponUserSummary to set
     */
    public void setCorresponUserSummary(CorresponUserSummary corresponUserSummary) {
        this.corresponUserSummary = corresponUserSummary;
    }


    /**
     * companiesを取得します.
     * @return the companies
     */
    public List<Company> getCompanies() {
        return companies;
    }


    /**
     * companiesを設定します.
     * @param companies the companies to set
     */
    public void setCompanies(List<Company> companies) {
        this.companies = companies;
    }


    /**
     * corresponGroupsを取得します.
     * @return the corresponGroups
     */
    public List<CorresponGroup> getCorresponGroups() {
        return corresponGroups;
    }


    /**
     * corresponGroupsを設定します.
     * @param corresponGroups the corresponGroups to set
     */
    public void setCorresponGroups(List<CorresponGroup> corresponGroups) {
        this.corresponGroups = corresponGroups;
    }


    /**
     * projectAdminsを取得します.
     * @return the projectAdmins
     */
    public List<ProjectUser> getProjectAdmins() {
        return projectAdmins;
    }


    /**
     * projectAdminsを設定します.
     * @param projectAdmins the projectAdmins to set
     */
    public void setProjectAdmins(List<ProjectUser> projectAdmins) {
        this.projectAdmins = projectAdmins;
    }


    /**
     * groupAdminsを取得します.
     * @return the groupAdmins
     */
    public List<ProjectUser> getGroupAdmins() {
        return groupAdmins;
    }


    /**
     * groupAdminsを設定します.
     * @param groupAdmins the groupAdmins to set
     */
    public void setGroupAdmins(List<ProjectUser> groupAdmins) {
        this.groupAdmins = groupAdmins;
    }


    /**
     * normalUsersを取得します.
     * @return the normalUsers
     */
    public List<ProjectUser> getNormalUsers() {
        return normalUsers;
    }


    /**
     * normalUsersを設定します.
     * @param normalUsers the normalUsers to set
     */
    public void setNormalUsers(List<ProjectUser> normalUsers) {
        this.normalUsers = normalUsers;
    }

}
