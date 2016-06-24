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

import java.util.Date;

import jp.co.opentone.bsol.framework.core.util.CloneUtil;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.code.UseWhole;

/**
 * カスタムフィールドの検索条件を表すDto.
 *
 * @author opentone
 *
 */
public class SearchCustomFieldCondition extends AbstractCondition {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 9028173800950773648L;

    /**
     * Id.
     * <p>
     * [v_project_custom_field.id]
     * </p>
     */
    private Long id;

    /**
     * No.
     * <p>
     * [v_project_custom_field.no]
     * </p>
     */
    private Long no;

    /**
     * Project custom field.
     * <p>
     * [v_project_custom_field.project_custom_field_id]
     * </p>
     */
    private Long projectCustomFieldId;

    /**
     * Project.
     * <p>
     * [v_project_custom_field.project_id]
     * </p>
     */
    private String projectId;

    /**
     * Project name e.
     * <p>
     * [v_project_custom_field.project_name_e]
     * </p>
     */
    private String projectNameE;

    /**
     * Label.
     * <p>
     * [v_project_custom_field.label]
     * </p>
     */
    private String label;

    /**
     * Order no.
     * <p>
     * [v_project_custom_field.order_no]
     * </p>
     */
    private Long orderNo;

    /**
     * Use whole.
     * <p>
     * [v_project_custom_field.use_whole]
     * </p>
     */
    private UseWhole useWhole;

    /**
     * Created by.
     * <p>
     * </p>
     */
    private User createdBy;

    /**
     * Created at.
     * <p>
     * [v_project_custom_field.created_at]
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
     * [v_project_custom_field.updated_at]
     * </p>
     */
    private Date updatedAt;

    /**
     * Version no.
     * <p>
     * [v_project_custom_field.version_no]
     * </p>
     */
    private Long versionNo;

    /**
     * Delete no.
     * <p>
     * [v_project_custom_field.delete_no]
     * </p>
     */
    private Long deleteNo;

    /**
     * マスタ管理フラグ.
     */
    private boolean adminHome;

    /**
     * 空のインスタンスを生成する.
     */
    public SearchCustomFieldCondition() {
    }

    /**
     * id の値を返す.
     * <p>
     * [v_project_custom_field.id]
     * </p>
     * @return id
     */
    public Long getId() {
        return id;
    }

    /**
     * id の値を設定する.
     * <p>
     * [v_project_custom_field.id]
     * </p>
     * @param id
     *            id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * no の値を返す.
     * <p>
     * [v_project_custom_field.no]
     * </p>
     * @return no
     */
    public Long getNo() {
        return no;
    }

    /**
     * no の値を設定する.
     * <p>
     * [v_project_custom_field.no]
     * </p>
     * @param no
     *            no
     */
    public void setNo(Long no) {
        this.no = no;
    }

    /**
     * projectCustomFieldId の値を返す.
     * <p>
     * [v_project_custom_field.project_custom_field_id]
     * </p>
     * @return projectCustomFieldId
     */
    public Long getProjectCustomFieldId() {
        return projectCustomFieldId;
    }

    /**
     * projectCustomFieldId の値を設定する.
     * <p>
     * [v_project_custom_field.project_custom_field_id]
     * </p>
     * @param projectCustomFieldId
     *            projectCustomFieldId
     */
    public void setProjectCustomFieldId(Long projectCustomFieldId) {
        this.projectCustomFieldId = projectCustomFieldId;
    }

    /**
     * projectId の値を返す.
     * <p>
     * [v_project_custom_field.project_id]
     * </p>
     * @return projectId
     */
    public String getProjectId() {
        return projectId;
    }

    /**
     * projectId の値を設定する.
     * <p>
     * [v_project_custom_field.project_id]
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
     * [v_project_custom_field.project_name_e]
     * </p>
     * @return projectNameE
     */
    public String getProjectNameE() {
        return projectNameE;
    }

    /**
     * projectNameE の値を設定する.
     * <p>
     * [v_project_custom_field.project_name_e]
     * </p>
     * @param projectNameE
     *            projectNameE
     */
    public void setProjectNameE(String projectNameE) {
        this.projectNameE = projectNameE;
    }

    /**
     * label の値を返す.
     * <p>
     * [v_project_custom_field.label]
     * </p>
     * @return label
     */
    public String getLabel() {
        return label;
    }

    /**
     * label の値を設定する.
     * <p>
     * [v_project_custom_field.label]
     * </p>
     * @param label
     *            label
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * orderNo の値を返す.
     * <p>
     * [v_project_custom_field.order_no]
     * </p>
     * @return orderNo
     */
    public Long getOrderNo() {
        return orderNo;
    }

    /**
     * orderNo の値を設定する.
     * <p>
     * [v_project_custom_field.order_no]
     * </p>
     * @param orderNo
     *            orderNo
     */
    public void setOrderNo(Long orderNo) {
        this.orderNo = orderNo;
    }

    /**
     * useWhole の値を返す.
     * <p>
     * [v_project_custom_field.use_whole]
     * </p>
     * @return useWhole
     */
    public UseWhole getUseWhole() {
        return useWhole;
    }

    /**
     * useWhole の値を設定する.
     * <p>
     * [v_project_custom_field.use_whole]
     * </p>
     * @param useWhole
     *            useWhole
     */
    public void setUseWhole(UseWhole useWhole) {
        this.useWhole = useWhole;
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
     * [v_project_custom_field.created_at]
     * </p>
     * @return createdAt
     */
    public Date getCreatedAt() {
        return CloneUtil.cloneDate(createdAt);
    }

    /**
     * createdAt の値を設定する.
     * <p>
     * [v_project_custom_field.created_at]
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
     * [v_project_custom_field.updated_at]
     * </p>
     * @return updatedAt
     */
    public Date getUpdatedAt() {
        return CloneUtil.cloneDate(updatedAt);
    }

    /**
     * updatedAt の値を設定する.
     * <p>
     * [v_project_custom_field.updated_at]
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
     * [v_project_custom_field.version_no]
     * </p>
     * @return versionNo
     */
    public Long getVersionNo() {
        return versionNo;
    }

    /**
     * versionNo の値を設定する.
     * <p>
     * [v_project_custom_field.version_no]
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
     * [v_project_custom_field.delete_no]
     * </p>
     * @return deleteNo
     */
    public Long getDeleteNo() {
        return deleteNo;
    }

    /**
     * deleteNo の値を設定する.
     * <p>
     * [v_project_custom_field.delete_no]
     * </p>
     * @param deleteNo
     *            deleteNo
     */
    public void setDeleteNo(Long deleteNo) {
        this.deleteNo = deleteNo;
    }

    /**
     * マスタ管理フラグを返す.
     * @return マスタ管理true / マスタ管理ではないfalse
     */
    public boolean getAdminHome() {
        return adminHome;
    }

    /**
     * マスタ管理フラグを設定する.
     * @param adminHome マスタ管理フラグ
     */
    public void setAdminHome(boolean adminHome) {
        this.adminHome = adminHome;
    }

}
