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

/**
 * テーブル [correspon_hierarchy] の1レコードを表すDto.
 *
 * @author opentone
 *
 */
public class CorresponHierarchy extends AbstractDto implements Entity {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 4862011429732192028L;

    /**
     * Id.
     * <p>
     * [correspon_hierarchy.id]
     * </p>
     */
    private Long id;

    /**
     * Parent correspon.
     * <p>
     * [correspon_hierarchy.parent_correspon_id]
     * </p>
     */
    private Long parentCorresponId;

    /**
     * Child correspon.
     * <p>
     * [correspon_hierarchy.child_correspon_id]
     * </p>
     */
    private Long childCorresponId;

    /**
     * Reply address user id.
     * <p>
     * [correspon_hierarchy.reply_address_user_id]
     * </p>
     */
    private Long replyAddressUserId;

    /**
     * Created by.
     * <p>
     * </p>
     */
    private User createdBy;

    /**
     * Created at.
     * <p>
     * [correspon_hierarchy.created_at]
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
     * [correspon_hierarchy.updated_at]
     * </p>
     */
    private Date updatedAt;

    /**
     * Delete no.
     * <p>
     * [correspon_hierarchy.delete_no]
     * </p>
     */
    private Long deleteNo;

    /**
     * 空のインスタンスを生成する.
     */
    public CorresponHierarchy() {
    }

    /**
     * id の値を返す.
     * <p>
     * [correspon_hierarchy.id]
     * </p>
     * @return id
     */
    public Long getId() {
        return id;
    }

    /**
     * id の値を設定する.
     * <p>
     * [correspon_hierarchy.id]
     * </p>
     * @param id
     *            id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * parentCorresponId の値を返す.
     * <p>
     * [correspon_hierarchy.parent_correspon_id]
     * </p>
     * @return parentCorresponId
     */
    public Long getParentCorresponId() {
        return parentCorresponId;
    }

    /**
     * parentCorresponId の値を設定する.
     * <p>
     * [correspon_hierarchy.parent_correspon_id]
     * </p>
     * @param parentCorresponId
     *            parentCorresponId
     */
    public void setParentCorresponId(Long parentCorresponId) {
        this.parentCorresponId = parentCorresponId;
    }

    /**
     * childCorresponId の値を返す.
     * <p>
     * [correspon_hierarchy.child_correspon_id]
     * </p>
     * @return childCorresponId
     */
    public Long getChildCorresponId() {
        return childCorresponId;
    }

    /**
     * childCorresponId の値を設定する.
     * <p>
     * [correspon_hierarchy.child_correspon_id]
     * </p>
     * @param childCorresponId
     *            childCorresponId
     */
    public void setChildCorresponId(Long childCorresponId) {
        this.childCorresponId = childCorresponId;
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
     * [correspon_hierarchy.created_at]
     * </p>
     * @return createdAt
     */
    public Date getCreatedAt() {
        return createdAt;
    }

    /**
     * createdAt の値を設定する.
     * <p>
     * [correspon_hierarchy.created_at]
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
     * [correspon_hierarchy.updated_at]
     * </p>
     * @return updatedAt
     */
    public Date getUpdatedAt() {
        return updatedAt;
    }

    /**
     * updatedAt の値を設定する.
     * <p>
     * [correspon_hierarchy.updated_at]
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
     * [correspon_hierarchy.delete_no]
     * </p>
     * @return deleteNo
     */
    public Long getDeleteNo() {
        return deleteNo;
    }

    /**
     * deleteNo の値を設定する.
     * <p>
     * [correspon_hierarchy.delete_no]
     * </p>
     * @param deleteNo
     *            deleteNo
     */
    public void setDeleteNo(Long deleteNo) {
        this.deleteNo = deleteNo;
    }

    /**
     * @return replyAddressUserId の値を返す.
     */
    public Long getReplyAddressUserId() {
        return replyAddressUserId;
    }

    /**
     * @param replyAddressUserId の値を設定する.
     */
    public void setReplyAddressUserId(Long replyAddressUserId) {
        this.replyAddressUserId = replyAddressUserId;
    }
}
