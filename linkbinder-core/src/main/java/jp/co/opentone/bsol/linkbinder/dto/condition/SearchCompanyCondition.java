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
package jp.co.opentone.bsol.linkbinder.dto.condition;

import jp.co.opentone.bsol.linkbinder.dto.User;

/**
 * テーブル [v_project_company],[company] の検索条件を表すDto.
 *
 * @author opentone
 *
 */
public class SearchCompanyCondition extends AbstractCondition {

    /**
     * serialVersionUID.
     */
    private static final long serialVersionUID = -1025524967405486225L;

    /**
     * ID.
     */
    private Long id;

    /**
     * プロジェクトID.
     */
    private String projectId;

    /**
     * 会社コード.
     */
    private String companyCd;

    /**
     * 会社名.
     */
    private String name;

    /**
     * 役割.
     */
    private String role;

    /**
     * 削除ナンバー.
     */
    private String deleteNo;

    /**
     * ユーザー情報.
     */
    private User user;

    /**
     * 会社ID.
     */
    private Long companyId;

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the projectId
     */
    public String getProjectId() {
        return projectId;
    }

    /**
     * @param projectId
     *            the projectId to set
     */
    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    /**
     * @return the companyCd
     */
    public String getCompanyCd() {
        return companyCd;
    }

    /**
     * @param companyCd
     *            the companyCd to set
     */
    public void setCompanyCd(String companyCd) {
        this.companyCd = companyCd;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the role
     */
    public String getRole() {
        return role;
    }

    /**
     * @param role
     *            the role to set
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * @return the deleteNo
     */
    public String getDeleteNo() {
        return deleteNo;
    }

    /**
     * @param deleteNo
     *            the deleteNo to set
     */
    public void setDeleteNo(String deleteNo) {
        this.deleteNo = deleteNo;
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

    /**
     * @return the companyId
     */
    public Long getCompanyId() {
        return companyId;
    }

    /**
     * @param companyId the companyId to set
     */
    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }
}
