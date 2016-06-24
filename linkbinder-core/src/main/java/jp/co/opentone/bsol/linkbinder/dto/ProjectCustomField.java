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

import jp.co.opentone.bsol.framework.core.dao.Entity;
import jp.co.opentone.bsol.framework.core.util.CloneUtil;


/**
 * テーブル [project_custom_field] の1レコードを表すDto.
 *
 * @author opentone
 *
 */
public class ProjectCustomField extends AbstractDto implements Entity {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 4639975218608091956L;

    /**
     * Id.
     * <p>
     * [project_custom_field.id]
     * </p>
     */
    private Long id;

    /**
     * Project.
     * <p>
     * [project_custom_field.project_id]
     * </p>
     */
    private String projectId;

    /**
     * Custom field.
     * <p>
     * [project_custom_field.custom_field_id]
     * </p>
     */
    private Long customFieldId;

    /**
     * Label.
     * <p>
     * [project_custom_field.label]
     * </p>
     */
    private String label;

    /**
     * Order no.
     * <p>
     * [project_custom_field.order_no]
     * </p>
     */
    private Long orderNo;

    /**
     * Created by.
     * <p>
     * </p>
     */
    private User createdBy;

    /**
     * Created at.
     * <p>
     * [project_custom_field.created_at]
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
     * [project_custom_field.updated_at]
     * </p>
     */
    private Date updatedAt;

    /**
     * Delete no.
     * <p>
     * [project_custom_field.delete_no]
     * </p>
     */
    private Long deleteNo;

    /**
     * 空のインスタンスを生成する.
     */
    public ProjectCustomField() {
    }

    /**
     * id の値を返す.
     * <p>
     * [project_custom_field.id]
     * </p>
     * @return id
     */
    public Long getId() {
        return id;
    }

    /**
     * id の値を設定する.
     * <p>
     * [project_custom_field.id]
     * </p>
     * @param id
     *            id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * projectId の値を返す.
     * <p>
     * [project_custom_field.project_id]
     * </p>
     * @return projectId
     */
    public String getProjectId() {
        return projectId;
    }

    /**
     * projectId の値を設定する.
     * <p>
     * [project_custom_field.project_id]
     * </p>
     * @param projectId
     *            projectId
     */
    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    /**
     * customFieldId の値を返す.
     * <p>
     * [project_custom_field.custom_field_id]
     * </p>
     * @return customFieldId
     */
    public Long getCustomFieldId() {
        return customFieldId;
    }

    /**
     * customFieldId の値を設定する.
     * <p>
     * [project_custom_field.custom_field_id]
     * </p>
     * @param customFieldId
     *            customFieldId
     */
    public void setCustomFieldId(Long customFieldId) {
        this.customFieldId = customFieldId;
    }

    /**
     * label の値を返す.
     * <p>
     * [project_custom_field.label]
     * </p>
     * @return label
     */
    public String getLabel() {
        return label;
    }

    /**
     * label の値を設定する.
     * <p>
     * [project_custom_field.label]
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
     * [project_custom_field.order_no]
     * </p>
     * @return orderNo
     */
    public Long getOrderNo() {
        return orderNo;
    }

    /**
     * orderNo の値を設定する.
     * <p>
     * [project_custom_field.order_no]
     * </p>
     * @param orderNo
     *            orderNo
     */
    public void setOrderNo(Long orderNo) {
        this.orderNo = orderNo;
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
     * [project_custom_field.created_at]
     * </p>
     * @return createdAt
     */
    public Date getCreatedAt() {
        return CloneUtil.cloneDate(createdAt);
    }

    /**
     * createdAt の値を設定する.
     * <p>
     * [project_custom_field.created_at]
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
     * [project_custom_field.updated_at]
     * </p>
     * @return updatedAt
     */
    public Date getUpdatedAt() {
        return CloneUtil.cloneDate(updatedAt);
    }

    /**
     * updatedAt の値を設定する.
     * <p>
     * [project_custom_field.updated_at]
     * </p>
     * @param updatedAt
     *            updatedAt
     */
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = CloneUtil.cloneDate(updatedAt);
    }

    /**
     * deleteNo の値を返す.
     * <p>
     * [project_custom_field.delete_no]
     * </p>
     * @return deleteNo
     */
    public Long getDeleteNo() {
        return deleteNo;
    }

    /**
     * deleteNo の値を設定する.
     * <p>
     * [project_custom_field.delete_no]
     * </p>
     * @param deleteNo
     *            deleteNo
     */
    public void setDeleteNo(Long deleteNo) {
        this.deleteNo = deleteNo;
    }
}
