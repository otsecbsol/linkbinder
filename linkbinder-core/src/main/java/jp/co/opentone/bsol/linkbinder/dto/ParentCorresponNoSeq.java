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



/**
 * テーブル [parent_correspon_no_seq] の1レコードを表すDto.
 *
 * @author opentone
 *
 */
public class ParentCorresponNoSeq extends AbstractDto implements VersioningEntity {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -4079643787818934480L;

    /**
     * Id.
     * <p>
     * [parent_correspon_no_seq.id]
     * </p>
     */
    private Long id;

    /**
     * Site Id.
     * <p>
     * [parent_correspon_no_seq.site_id]
     * </p>
     */
    private Long siteId;

    /**
     * Discipline Id.
     * <p>
     * [parent_correspon_no_seq.discipline_id]
     * </p>
     */
    private Long disciplineId;

    /**
     * No.
     * <p>
     * [parent_correspon_no_seq.no]
     * </p>
     */
    private Long no;

    /**
     * Created by.
     * <p>
     * </p>
     */
    private User createdBy;

    /**
     * Created at.
     * <p>
     * [parent_correspon_no_seq.created_at]
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
     * [parent_correspon_no_seq.updated_at]
     * </p>
     */
    private Date updatedAt;

    /**
     * Version no.
     * <p>
     * [parent_correspon_no_seq.version_no]
     * </p>
     */
    private Long versionNo;

    /**
     * Delete no.
     * <p>
     * [parent_correspon_no_seq.delete_no]
     * </p>
     */
    private Long deleteNo;

    /**
     * 空のインスタンスを生成する.
     */
    public ParentCorresponNoSeq() {
    }

    /**
     * id の値を返す.
     * <p>
     * [v_correspon.id]
     * </p>
     * @return id
     */
    public Long getId() {
        return id;
    }

    /**
     * id の値を設定する.
     * <p>
     * [v_correspon.id]
     * </p>
     * @param id
     *            id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * siteIdを取得します.
     * @return the siteId
     */
    public Long getSiteId() {
        return siteId;
    }

    /**
     * siteIdを設定します.
     * @param siteId the siteId to set
     */
    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    /**
     * disciplineIdを取得します.
     * @return the disciplineId
     */
    public Long getDisciplineId() {
        return disciplineId;
    }

    /**
     * disciplineIdを設定します.
     * @param disciplineId the disciplineId to set
     */
    public void setDisciplineId(Long disciplineId) {
        this.disciplineId = disciplineId;
    }

    /**
     * noを取得します.
     * @return the no
     */
    public Long getNo() {
        return no;
    }

    /**
     * noを設定します.
     * @param no the no to set
     */
    public void setNo(Long no) {
        this.no = no;
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
     * [v_correspon.created_at]
     * </p>
     * @return createdAt
     */
    public Date getCreatedAt() {
        return createdAt;
    }

    /**
     * createdAt の値を設定する.
     * <p>
     * [v_correspon.created_at]
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
     * [v_correspon.updated_at]
     * </p>
     * @return updatedAt
     */
    public Date getUpdatedAt() {
        return updatedAt;
    }

    /**
     * updatedAt の値を設定する.
     * <p>
     * [v_correspon.updated_at]
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
     * [v_correspon.version_no]
     * </p>
     * @return versionNo
     */
    public Long getVersionNo() {
        return versionNo;
    }

    /**
     * versionNo の値を設定する.
     * <p>
     * [v_correspon.version_no]
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
     * [v_correspon.delete_no]
     * </p>
     * @return deleteNo
     */
    public Long getDeleteNo() {
        return deleteNo;
    }

    /**
     * deleteNo の値を設定する.
     * <p>
     * [v_correspon.delete_no]
     * </p>
     * @param deleteNo
     *            deleteNo
     */
    public void setDeleteNo(Long deleteNo) {
        this.deleteNo = deleteNo;
    }
}
