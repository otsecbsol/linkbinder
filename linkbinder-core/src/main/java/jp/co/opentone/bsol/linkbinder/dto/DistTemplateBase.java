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

import java.util.Calendar;
import java.util.Date;

/**
 * DistributionテンプレートDtoのベースクラス.
 *
 * @author opentone
 *
 */
public abstract class DistTemplateBase extends AbstractDto implements DistTemplateCommon {

    /**
     * serialVersionUID.
     */
    private static final long serialVersionUID = 3022477013578170567L;

    /**
     * 作成情報.
     */
    private DistTemplateOperation createOperation = new DistTemplateOperation();

    /**
     * 更新情報.
     */
    private DistTemplateOperation updateOperation = new DistTemplateOperation();

    /**
     * バージョンNo.
     */
    private Long versionNo;

    /**
     * 削除No.
     */
    private Long deleteNo;

    /**
     * 作成者従業員番号を取得します.
     * @return 作成者従業員番号
     */
    public String getCreatedBy() {
        return createOperation.getOperateBy().getEmpNo();
    }

    /**
     * 作成者従業員番号を設定します.
     * @param createdBy 作成者従業員番号
     */
    public void setCreatedBy(String createdBy) {
        createOperation.getOperateBy().setEmpNo(createdBy);
    }

    /**
     * 作成日時を取得します.
     * @return 作成日時
     */
    public Date getCreatedAt() {
        return createOperation.getOperateAt();
    }

    /**
     * 作成日時を設定します.
     * @param createdAt 作成日時
     */
    public void setCreatedAt(Date createdAt) {
        createOperation.setOperateAt(createdAt);
    }

    /**
     * 現在日時を作成日時に設定します.
     * @return 設定した作成日時
     */
    public Date setCreatedAtNow() {
        Date now = getNowTime();
        setCreatedAt(now);
        return now;
    }

    /**
     * 更新者従業員番号を取得します.
     * @return 更新者従業員番号
     */
    public String getUpdatedBy() {
        return updateOperation.getOperateBy().getEmpNo();
    }

    /**
     * 更新者従業員番号を設定します.
     * @param createdBy 更新者従業員番号
     */
    public void setUpdatedBy(String createdBy) {
        updateOperation.getOperateBy().setEmpNo(createdBy);
    }

    /**
     * 更新日時を取得します.
     * @return 更新日時
     */
    public Date getUpdatedAt() {
        return updateOperation.getOperateAt();
    }

    /**
     * 更新日時を設定します.
     * @param updatedAt 更新日時
     */
    public void setUpdatedAt(Date updatedAt) {
        updateOperation.setOperateAt(updatedAt);
    }

    /**
     * 現在日時を更新日時に設定します.
     * @return 設定した更新日時
     */
    public Date setUpdatedAtNow() {
        Date now = getNowTime();
        setUpdatedAt(now);
        return now;
    }

    /**
     * バージョンNo.を取得します.
     * @return バージョンNo.
     */
    public Long getVersionNo() {
        return versionNo;
    }

    /**
     * バージョンNo.を設定します.
     * @param versionNo バージョンNo.
     */
    public void setVersionNo(Long versionNo) {
        this.versionNo = versionNo;
    }

    /**
     * 削除No.を取得します.
     * @return 削除No.
     */
    public Long getDeleteNo() {
        return deleteNo;
    }

    /**
     * 削除No.を設定します.
     * @param deleteNo 削除No.
     */
    public void setDeleteNo(Long deleteNo) {
        this.deleteNo = deleteNo;
    }

    /**
     * 作成者名(姓)を取得します.
     * @return 作成者名(姓)
     */
    public String getCreatedByLastNm() {
        return createOperation.getOperateBy().getLastName();
    }

    /**
     * 作成者名(姓)を設定します.
     * @param createdByLastNm 作成者名(姓)
     */
    public void setCreatedByLastNm(String createdByLastNm) {
        createOperation.getOperateBy().setLastName(createdByLastNm);
    }

    /**
     * 作成者名(名)を取得します.
     * @return 作成者名(名)
     */
    public String getCreatedByFrstNm() {
        return createOperation.getOperateBy().getFirstName();
    }

    /**
     * 作成者名(名)を設定します.
     * @param createdByFrstNm 作成者名(名)
     */
    public void setCreatedByFrstNm(String createdByFrstNm) {
        createOperation.getOperateBy().setFirstName(createdByFrstNm);
    }

    /**
     * 作成者名(英語)を取得します.
     * @return 作成者名(英語)
     */
    public String getCreatedByNmE() {
        return createOperation.getOperateBy().getNameE();
    }

    /**
     * 作成者名(英語)を設定します.
     * @param createdByNmE 作成者名(英語)
     */
    public void setCreatedByNmE(String createdByNmE) {
        createOperation.getOperateBy().setNameE(createdByNmE);
    }

    /**
     * 作成者名(日本語)を取得します.
     * @return 作成者名(日本語)
     */
    public String getCreatedByNmJ() {
        return createOperation.getOperateBy().getNameJ();
    }

    /**
     * 作成者名(日本語)を設定します.
     * @param createdByNmJ 作成者名(日本語)
     */
    public void setCreatedByNmJ(String createdByNmJ) {
        createOperation.getOperateBy().setNameJ(createdByNmJ);
    }

    /**
     * 更新者名(姓)を取得します.
     * @return 更新者名(姓)
     */
    public String getUpdatedByLastNm() {
        return updateOperation.getOperateBy().getLastName();
    }

    /**
     * 更新者名(姓)を設定します.
     * @param updatedByLastNm 更新者名(姓)
     */
    public void setUpdatedByLastNm(String updatedByLastNm) {
        updateOperation.getOperateBy().setLastName(updatedByLastNm);
    }

    /**
     * 更新者名(名)を取得します.
     * @return 更新者名(名)
     */
    public String getUpdatedByFrstNm() {
        return updateOperation.getOperateBy().getFirstName();
    }

    /**
     * 更新者名(名)を設定します.
     * @param updatedByFrstNm 更新者名(名)
     */
    public void setUpdatedByFrstNm(String updatedByFrstNm) {
        updateOperation.getOperateBy().setFirstName(updatedByFrstNm);
    }

    /**
     * 更新者名(英語)を取得します.
     * @return 更新者名(英語)
     */
    public String getUpdatedByNmE() {
        return updateOperation.getOperateBy().getNameE();
    }

    /**
     * 更新者名(英語)を設定します.
     * @param updatedByNmE 更新者名(英語)
     */
    public void setUpdatedByNmE(String updatedByNmE) {
        updateOperation.getOperateBy().setNameE(updatedByNmE);
    }

    /**
     * 更新者名(日本語)を取得します.
     * @return 更新者名(日本語)
     */
    public String getUpdatedByNmJ() {
        return updateOperation.getOperateBy().getNameJ();
    }

    /**
     * 更新者名(日本語)を設定します.
     * @param updatedByNmJ 更新者名(日本語)
     */
    public void setUpdatedByNmJ(String updatedByNmJ) {
        updateOperation.getOperateBy().setNameJ(updatedByNmJ);
    }

    /**
     * 現在日時を取得します.
     * @return 現在日時
     */
    public Date getNowTime() {
        Calendar cal = Calendar.getInstance();
        return cal.getTime();
    }

    /* (非 Javadoc)
     * @see java.lang.Object#clone()
     */
    @Override
    protected Object clone() throws AssertionError {
        DistTemplateBase obj = null;
        try {
            obj = (DistTemplateBase) super.clone();
            obj.createOperation = (DistTemplateOperation) this.createOperation.clone();
            obj.updateOperation = (DistTemplateOperation) this.updateOperation.clone();
            obj.versionNo = cloneField(this.versionNo);
            obj.deleteNo = cloneField(this.deleteNo);
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
        return obj;
    }
}
