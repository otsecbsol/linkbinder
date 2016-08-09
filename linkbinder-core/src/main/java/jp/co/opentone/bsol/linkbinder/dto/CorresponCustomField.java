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

import jp.co.opentone.bsol.framework.core.dao.Entity;

import java.util.Date;

/**
 * テーブル [correspon_custom_field] の1レコードを表すDto.
 *
 * @author opentone
 *
 */
public class CorresponCustomField extends AbstractDto implements Entity {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -6550419538482518754L;

    /**
     * Id.
     * <p>
     * [correspon_custom_field.id]
     * </p>
     */
    private Long id;

    /**
     * ROW_NUMBER().
     */
    private Long no;

    /**
     * Correspon.
     * <p>
     * [correspon_custom_field.correspon_id]
     * </p>
     */
    private Long corresponId;

    /**
     * Project custom field.
     * <p>
     * [correspon_custom_field.project_custom_field_id]
     * </p>
     */
    private Long projectCustomFieldId;

    private String label;

    /**
     * Value.
     * <p>
     * [correspon_custom_field.value]
     * </p>
     */
    private String value;

    /**
     * Created by.
     * <p>
     * </p>
     */
    private User createdBy;

    /**
     * Created at.
     * <p>
     * [correspon_custom_field.created_at]
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
     * [correspon_custom_field.updated_at]
     * </p>
     */
    private Date updatedAt;

    /**
     * Delete no.
     * <p>
     * [correspon_custom_field.delete_no]
     * </p>
     */
    private Long deleteNo;

    /**
     * 空のインスタンスを生成する.
     */
    public CorresponCustomField() {
    }

    /**
     * id の値を返す.
     * <p>
     * [correspon_custom_field.id]
     * </p>
     * @return id
     */
    public Long getId() {
        return id;
    }

    /**
     * id の値を設定する.
     * <p>
     * [correspon_custom_field.id]
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
     * [ROW_NUMBER()]
     * </p>
     * @return no
     */
    public Long getNo() {
        return no;
    }

    /**
     * no の値を設定する.
     * <p>
     * [ROW_NUMBER()]
     * </p>
     * @param no
     *            no
     */
    public void setNo(Long no) {
        this.no = no;
    }

    /**
     * corresponId の値を返す.
     * <p>
     * [correspon_custom_field.correspon_id]
     * </p>
     * @return corresponId
     */
    public Long getCorresponId() {
        return corresponId;
    }

    /**
     * corresponId の値を設定する.
     * <p>
     * [correspon_custom_field.correspon_id]
     * </p>
     * @param corresponId
     *            corresponId
     */
    public void setCorresponId(Long corresponId) {
        this.corresponId = corresponId;
    }

    /**
     * projectCustomFieldId の値を返す.
     * <p>
     * [correspon_custom_field.project_custom_field_id]
     * </p>
     * @return projectCustomFieldId
     */
    public Long getProjectCustomFieldId() {
        return projectCustomFieldId;
    }

    /**
     * projectCustomFieldId の値を設定する.
     * <p>
     * [correspon_custom_field.project_custom_field_id]
     * </p>
     * @param projectCustomFieldId
     *            projectCustomFieldId
     */
    public void setProjectCustomFieldId(Long projectCustomFieldId) {
        this.projectCustomFieldId = projectCustomFieldId;
    }

    /**
     * value の値を返す.
     * <p>
     * [correspon_custom_field.value]
     * </p>
     * @return value
     */
    public String getValue() {
        return value;
    }

    /**
     * value の値を設定する.
     * <p>
     * [correspon_custom_field.value]
     * </p>
     * @param value
     *            value
     */
    public void setValue(String value) {
        this.value = value;
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
     * [correspon_custom_field.created_at]
     * </p>
     * @return createdAt
     */
    public Date getCreatedAt() {
        return createdAt;
    }

    /**
     * createdAt の値を設定する.
     * <p>
     * [correspon_custom_field.created_at]
     * </p>
     * @param createdAt
     *            createdAt
     */
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
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
     * [correspon_custom_field.updated_at]
     * </p>
     * @return updatedAt
     */
    public Date getUpdatedAt() {
        return updatedAt;
    }

    /**
     * updatedAt の値を設定する.
     * <p>
     * [correspon_custom_field.updated_at]
     * </p>
     * @param updatedAt
     *            updatedAt
     */
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * deleteNo の値を返す.
     * <p>
     * [correspon_custom_field.delete_no]
     * </p>
     * @return deleteNo
     */
    public Long getDeleteNo() {
        return deleteNo;
    }

    /**
     * deleteNo の値を設定する.
     * <p>
     * [correspon_custom_field.delete_no]
     * </p>
     * @param deleteNo
     *            deleteNo
     */
    public void setDeleteNo(Long deleteNo) {
        this.deleteNo = deleteNo;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
