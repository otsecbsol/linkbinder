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
 * テーブル [custom_field_value] の1レコードを表すDto.
 *
 * @author opentone
 *
 */
public class CustomFieldValue extends AbstractDto implements Entity {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 6079582976320232190L;

    /**
     * Id.
     * <p>
     * [custom_field_value.id]
     * </p>
     */
    private Long id;

    /**
     * Custom field.
     * <p>
     * [custom_field_value.custom_field_id]
     * </p>
     */
    private Long customFieldId;

    /**
     * Value.
     * <p>
     * [custom_field_value.value]
     * </p>
     */
    private String value;

    /**
     * Order no.
     * <p>
     * [custom_field_value.order_no]
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
     * [custom_field_value.created_at]
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
     * [custom_field_value.updated_at]
     * </p>
     */
    private Date updatedAt;

    /**
     * Delete no.
     * <p>
     * [custom_field_value.delete_no]
     * </p>
     */
    private Long deleteNo;

    /**
     * 空のインスタンスを生成する.
     */
    public CustomFieldValue() {
    }

    /**
     * id の値を返す.
     * <p>
     * [custom_field_value.id]
     * </p>
     * @return id
     */
    public Long getId() {
        return id;
    }

    /**
     * id の値を設定する.
     * <p>
     * [custom_field_value.id]
     * </p>
     * @param id
     *            id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * customFieldId の値を返す.
     * <p>
     * [custom_field_value.custom_field_id]
     * </p>
     * @return customFieldId
     */
    public Long getCustomFieldId() {
        return customFieldId;
    }

    /**
     * customFieldId の値を設定する.
     * <p>
     * [custom_field_value.custom_field_id]
     * </p>
     * @param customFieldId
     *            customFieldId
     */
    public void setCustomFieldId(Long customFieldId) {
        this.customFieldId = customFieldId;
    }

    /**
     * value の値を返す.
     * <p>
     * [custom_field_value.value]
     * </p>
     * @return value
     */
    public String getValue() {
        return value;
    }

    /**
     * value の値を設定する.
     * <p>
     * [custom_field_value.value]
     * </p>
     * @param value
     *            value
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * orderNo の値を返す.
     * <p>
     * [custom_field_value.order_no]
     * </p>
     * @return orderNo
     */
    public Long getOrderNo() {
        return orderNo;
    }

    /**
     * orderNo の値を設定する.
     * <p>
     * [custom_field_value.order_no]
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
     * [custom_field_value.created_at]
     * </p>
     * @return createdAt
     */
    public Date getCreatedAt() {
        return CloneUtil.cloneDate(createdAt);
    }

    /**
     * createdAt の値を設定する.
     * <p>
     * [custom_field_value.created_at]
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
     * [custom_field_value.updated_at]
     * </p>
     * @return updatedAt
     */
    public Date getUpdatedAt() {
        return CloneUtil.cloneDate(updatedAt);
    }

    /**
     * updatedAt の値を設定する.
     * <p>
     * [custom_field_value.updated_at]
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
     * [custom_field_value.delete_no]
     * </p>
     * @return deleteNo
     */
    public Long getDeleteNo() {
        return deleteNo;
    }

    /**
     * deleteNo の値を設定する.
     * <p>
     * [custom_field_value.delete_no]
     * </p>
     * @param deleteNo
     *            deleteNo
     */
    public void setDeleteNo(Long deleteNo) {
        this.deleteNo = deleteNo;
    }
}
