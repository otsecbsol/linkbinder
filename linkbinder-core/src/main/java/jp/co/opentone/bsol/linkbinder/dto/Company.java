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

import java.util.Date;

import jp.co.opentone.bsol.framework.core.dao.VersioningEntity;
import jp.co.opentone.bsol.framework.core.util.CloneUtil;
import jp.co.opentone.bsol.linkbinder.util.ValueFormatter;

/**
 * テーブル [v_project_company] の1レコードを表すDto.
 *
 * @author opentone
 *
 */
public class Company extends AbstractDto implements VersioningEntity {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 8065607100465226069L;

    /**
     * Id.
     * <p>
     * [v_project_company.id]
     * </p>
     */
    private Long id;

    /**
     * Project company.
     * <p>
     * [v_project_company.project_company_id]
     * </p>
     */
    private Long projectCompanyId;

    /**
     * Project.
     * <p>
     * [v_project_company.project_id]
     * </p>
     */
    private String projectId;

    /**
     * Project name e.
     * <p>
     * [v_project_company.project_name_e]
     * </p>
     */
    private String projectNameE;

    /**
     * Company cd.
     * <p>
     * [v_project_company.company_cd]
     * </p>
     */
    private String companyCd;

    /**
     * Name.
     * <p>
     * [v_project_company.name]
     * </p>
     */
    private String name;

    /**
     * Role.
     * <p>
     * [v_project_company.role]
     * </p>
     */
    private String role;

    /**
     * Created by.
     * <p>
     * </p>
     */
    private User createdBy;

    /**
     * Created at.
     * <p>
     * [v_project_company.created_at]
     * </p>
     */
    private Date createdAt;

    /**
     * Updated by.
     * <p>
     * </p>
     */
    private User updatedBy;

    /**
     * Updated at.
     * <p>
     * [v_project_company.updated_at]
     * </p>
     */
    private Date updatedAt;

    /**
     * Version no.
     * <p>
     * [v_project_company.version_no]
     * </p>
     */
    private Long versionNo;

    /**
     * Delete no.
     * <p>
     * [v_project_company.delete_no]
     * </p>
     */
    private Long deleteNo;

    /**
     * 空のインスタンスを生成する.
     */
    public Company() {
    }

    /**
     * id の値を返す.
     * <p>
     * [v_project_company.id]
     * </p>
     * @return id
     */
    public Long getId() {
        return id;
    }

    /**
     * id の値を設定する.
     * <p>
     * [v_project_company.id]
     * </p>
     * @param id
     *            id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * projectCompanyId の値を返す.
     * <p>
     * [v_project_company.project_company_id]
     * </p>
     * @return projectCompanyId
     */
    public Long getProjectCompanyId() {
        return projectCompanyId;
    }

    /**
     * projectCompanyId の値を設定する.
     * <p>
     * [v_project_company.project_company_id]
     * </p>
     * @param projectCompanyId
     *            projectCompanyId
     */
    public void setProjectCompanyId(Long projectCompanyId) {
        this.projectCompanyId = projectCompanyId;
    }

    /**
     * projectId の値を返す.
     * <p>
     * [v_project_company.project_id]
     * </p>
     * @return projectId
     */
    public String getProjectId() {
        return projectId;
    }

    /**
     * projectId の値を設定する.
     * <p>
     * [v_project_company.project_id]
     * </p>
     * @param projectId
     *            projectId
     */
    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    /**
     * projectNameE の値を返す.
     * <p>
     * [v_project_company.project_name_e]
     * </p>
     * @return projectNameE
     */
    public String getProjectNameE() {
        return projectNameE;
    }

    /**
     * projectNameE の値を設定する.
     * <p>
     * [v_project_company.project_name_e]
     * </p>
     * @param projectNameE
     *            projectNameE
     */
    public void setProjectNameE(String projectNameE) {
        this.projectNameE = projectNameE;
    }

    /**
     * companyCd の値を返す.
     * <p>
     * [v_project_company.company_cd]
     * </p>
     * @return companyCd
     */
    public String getCompanyCd() {
        return companyCd;
    }

    /**
     * companyCd の値を設定する.
     * <p>
     * [v_project_company.company_cd]
     * </p>
     * @param companyCd
     *            companyCd
     */
    public void setCompanyCd(String companyCd) {
        this.companyCd = companyCd;
    }

    /**
     * name の値を返す.
     * <p>
     * [v_project_company.name]
     * </p>
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * name の値を設定する.
     * <p>
     * [v_project_company.name]
     * </p>
     * @param name
     *            name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * role の値を返す.
     * <p>
     * [v_project_company.role]
     * </p>
     * @return role
     */
    public String getRole() {
        return role;
    }

    /**
     * role の値を設定する.
     * <p>
     * [v_project_company.role]
     * </p>
     * @param role
     *            role
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * 作成者を返す.
     * <p>
     * </p>
     * @return createdBy
     */
    public User getCreatedBy() {
        return createdBy;
    }

    /**
     * 作成者を設定する.
     * <p>
     * </p>
     * @param createdBy
     *            作成者
     */
    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * createdAt の値を返す.
     * <p>
     * [v_project_company.created_at]
     * </p>
     * @return createdAt
     */
    public Date getCreatedAt() {
        return CloneUtil.cloneDate(createdAt);
    }

    /**
     * createdAt の値を設定する.
     * <p>
     * [v_project_company.created_at]
     * </p>
     * @param createdAt
     *            createdAt
     */
    public void setCreatedAt(Date createdAt) {
        this.createdAt = CloneUtil.cloneDate(createdAt);
    }

    /**
     * 更新者を返す.
     * <p>
     * </p>
     * @return updatedBy
     */
    public User getUpdatedBy() {
        return updatedBy;
    }

    /**
     * 更新者を設定する.
     * <p>
     * </p>
     * @param updatedBy
     *            更新者
     */
    public void setUpdatedBy(User updatedBy) {
        this.updatedBy = updatedBy;
    }

    /**
     * updatedAt の値を返す.
     * <p>
     * [v_project_company.updated_at]
     * </p>
     * @return updatedAt
     */
    public Date getUpdatedAt() {
        return CloneUtil.cloneDate(updatedAt);
    }

    /**
     * updatedAt の値を設定する.
     * <p>
     * [v_project_company.updated_at]
     * </p>
     * @param updatedAt
     *            updatedAt
     */
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = CloneUtil.cloneDate(updatedAt);
    }

    /**
     * versionNo の値を返す.
     * <p>
     * [v_project_company.version_no]
     * </p>
     * @return versionNo
     */
    public Long getVersionNo() {
        return versionNo;
    }

    /**
     * versionNo の値を設定する.
     * <p>
     * [v_project_company.version_no]
     * </p>
     * @param versionNo
     *            versionNo
     */
    public void setVersionNo(Long versionNo) {
        this.versionNo = versionNo;
    }

    /**
     * deleteNo の値を返す.
     * <p>
     * [v_project_company.delete_no]
     * </p>
     * @return deleteNo
     */
    public Long getDeleteNo() {
        return deleteNo;
    }

    /**
     * deleteNo の値を設定する.
     * <p>
     * [v_project_company.delete_no]
     * </p>
     * @param deleteNo
     *            deleteNo
     */
    public void setDeleteNo(Long deleteNo) {
        this.deleteNo = deleteNo;
    }


    /**
     * 表示用のCodeとNameを返却する.
     * @return 表示用のCodeとName
     */
    public String getCodeAndName() {
        return ValueFormatter.formatCodeAndName(companyCd, name);
    }

    /**
     * 新規登録・更新の判定.
     * @return boolean  登録true / 更新false
     */
    public boolean isNew() {
        return getId() == null || getId() == 0;
    }
}
