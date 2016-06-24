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

import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.code.ReadStatus;

/**
 * テーブル [correspon_read_status] の1レコードを表すDto.
 * <p>
 *
 * @author opentone
 *
 */
public class SearchCorresponReadStatusCondition extends AbstractCondition {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 3495834974465067255L;

    /**
     * Id.
     * <p>
     * [correspon_read_status.id]
     * </p>
     */
    private Long id;

    /**
     * Emp no.
     * <p>
     * [correspon_read_status.emp_no]
     * </p>
     */
    private String empNo;

    /**
     * Correspon.
     * <p>
     * [correspon_read_status.correspon_id]
     * </p>
     */
    private Long corresponId;

    /**
     * Read status.
     * <p>
     * [correspon_read_status.read_status]
     * </p>
     */
    private ReadStatus readStatus;

    /**
     * Created by.
     * <p>
     * </p>
     */
    private User createdBy;

    /**
     * Created at.
     * <p>
     * [correspon_read_status.created_at]
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
     * [correspon_read_status.updated_at]
     * </p>
     */
    private Date updatedAt;

    /**
     * Version no.
     * <p>
     * [correspon_read_status.version_no]
     * </p>
     */
    private Long versionNo;

    /**
     * Delete no.
     * <p>
     * [correspon_read_status.delete_no]
     * </p>
     */
    private Long deleteNo;

    /**
     * 空のインスタンスを生成する.
     */
    public SearchCorresponReadStatusCondition() {
    }

    /**
     * id の値を返す.
     * <p>
     * [correspon_read_status.id]
     * </p>
     * @return id
     */
    public Long getId() {
        return id;
    }

    /**
     * id の値を設定する.
     * <p>
     * [correspon_read_status.id]
     * </p>
     * @param id
     *            id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * empNo の値を返す.
     * <p>
     * [correspon_read_status.emp_no]
     * </p>
     * @return empNo
     */
    public String getEmpNo() {
        return empNo;
    }

    /**
     * empNo の値を設定する.
     * <p>
     * [correspon_read_status.emp_no]
     * </p>
     * @param empNo
     *            empNo
     */
    public void setEmpNo(String empNo) {
        this.empNo = empNo;
    }

    /**
     * corresponId の値を返す.
     * <p>
     * [correspon_read_status.correspon_id]
     * </p>
     * @return corresponId
     */
    public Long getCorresponId() {
        return corresponId;
    }

    /**
     * corresponId の値を設定する.
     * <p>
     * [correspon_read_status.correspon_id]
     * </p>
     * @param corresponId
     *            corresponId
     */
    public void setCorresponId(Long corresponId) {
        this.corresponId = corresponId;
    }

    /**
     * readStatus の値を返す.
     * <p>
     * [correspon_read_status.read_status]
     * </p>
     * @return readStatus
     */
    public ReadStatus getReadStatus() {
        return readStatus;
    }

    /**
     * readStatus の値を設定する.
     * <p>
     * [correspon_read_status.read_status]
     * </p>
     * @param readStatus
     *            readStatus
     */
    public void setReadStatus(ReadStatus readStatus) {
        this.readStatus = readStatus;
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
     * [correspon_read_status.created_at]
     * </p>
     * @return createdAt
     */
    public Date getCreatedAt() {
        return createdAt;
    }

    /**
     * createdAt の値を設定する.
     * <p>
     * [correspon_read_status.created_at]
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
     * [correspon_read_status.updated_at]
     * </p>
     * @return updatedAt
     */
    public Date getUpdatedAt() {
        return updatedAt;
    }

    /**
     * updatedAt の値を設定する.
     * <p>
     * [correspon_read_status.updated_at]
     * </p>
     * @param updatedAt
     *            updatedAt
     */
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * versionNo の値を返す.
     * <p>
     * [correspon_read_status.version_no]
     * </p>
     * @return versionNo
     */
    public Long getVersionNo() {
        return versionNo;
    }

    /**
     * versionNo の値を設定する.
     * <p>
     * [correspon_read_status.version_no]
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
     * [correspon_read_status.delete_no]
     * </p>
     * @return deleteNo
     */
    public Long getDeleteNo() {
        return deleteNo;
    }

    /**
     * deleteNo の値を設定する.
     * <p>
     * [correspon_read_status.delete_no]
     * </p>
     * @param deleteNo
     *            deleteNo
     */
    public void setDeleteNo(Long deleteNo) {
        this.deleteNo = deleteNo;
    }
}
