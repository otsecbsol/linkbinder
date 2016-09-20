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

import jp.co.opentone.bsol.framework.core.dao.VersioningEntity;
import jp.co.opentone.bsol.linkbinder.dto.code.CorresponStatus;

import java.util.Date;

/**
 * テーブル [project_custom_setting] の1レコードを表すDto.
 *
 * @author opentone
 *
 */
public class ProjectCustomSetting extends AbstractDto implements VersioningEntity {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -448180103152528872L;

    /**
     * Default Statusの初期値.
     */
    public static final CorresponStatus DEFAULT_CORRESPON_STATUS = CorresponStatus.OPEN;

    /**
     * Use Person In Chargeの初期値.
     */
    public static final boolean DEFAULT_USE_PERSON_IN_CHARGE = true;

    /**
     * ID.
     * <p>
     * [project_custom_setting.id]
     * </p>
     */
    private Long id;

    /**
     * Project Id.
     * <p>
     * [project_custom_setting.project_id]
     * </p>
     */
    private String projectId;

    /**
     * Default Status.
     * <p>
     * [project_custom_setting.default_status]
     * </p>
     */
    private CorresponStatus defaultStatus;

    /**
     * Use Person in Charge.
     * <p>
     * [project_custom_setting.use_person_in_charge]
     * </p>
     */
    private boolean usePersonInCharge;

    /**
     * useCorresponAccessControl.
     * <p>
     * [project_custom_setting.use_correspon_access_control]
     * </p>
     */
    private boolean useCorresponAccessControl;

    private boolean useLearning;

    /**
     * Created by.
     * <p>
     * </p>
     */
    private User createdBy;

    /**
     * Created at.
     * <p>
     * [project_custom_setting.created_at]
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
     * [project_custom_setting.updated_at]
     * </p>
     */
    private Date updatedAt;

    /**
     * Version no.
     * <p>
     * [project_custom_setting.version_no]
     * </p>
     */
    private Long versionNo;

    /**
     * Delete no.
     * <p>
     * [project_custom_setting.delete_no]
     * </p>
     */
    private Long deleteNo;

    /**
     * 空のインスタンスを生成する.
     */
    public ProjectCustomSetting() {
        this.defaultStatus = DEFAULT_CORRESPON_STATUS;
        this.usePersonInCharge = DEFAULT_USE_PERSON_IN_CHARGE;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param projectId the projectId to set
     */
    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    /**
     * @return the projectId
     */
    public String getProjectId() {
        return projectId;
    }

    /**
     * @param defaultStatus the defaultStatus to set
     */
    public void setDefaultStatus(CorresponStatus defaultStatus) {
        this.defaultStatus = defaultStatus;
    }

    /**
     * @return the defaultStatus
     */
    public CorresponStatus getDefaultStatus() {
        return defaultStatus;
    }

    /**
     * @param usePersonInCharge the usePersonInCharge to set
     */
    public void setUsePersonInCharge(boolean usePersonInCharge) {
        this.usePersonInCharge = usePersonInCharge;
    }

    /**
     * @return the usePersonInCharge
     */
    public boolean isUsePersonInCharge() {
        return usePersonInCharge;
    }

    /**
     * @param createdBy the createdBy to set
     */
    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * @return the createdBy
     */
    public User getCreatedBy() {
        return createdBy;
    }

    /**
     * @param createdAt the createdAt to set
     */
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * @return the createdAt
     */
    public Date getCreatedAt() {
        return createdAt;
    }

    /**
     * @param updatedBy the updatedBy to set
     */
    public void setUpdatedBy(User updatedBy) {
        this.updatedBy = updatedBy;
    }

    /**
     * @return the updatedBy
     */
    public User getUpdatedBy() {
        return updatedBy;
    }

    /**
     * @param updatedAt the updatedAt to set
     */
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * @return the updatedAt
     */
    public Date getUpdatedAt() {
        return updatedAt;
    }

    /**
     * @param versionNo the versionNo to set
     */
    public void setVersionNo(Long versionNo) {
        this.versionNo = versionNo;
    }

    /**
     * @return the versionNo
     */
    public Long getVersionNo() {
        return versionNo;
    }

    /**
     * @param deleteNo the deleteNo to set
     */
    public void setDeleteNo(Long deleteNo) {
        this.deleteNo = deleteNo;
    }

    /**
     * @return the deleteNo
     */
    public Long getDeleteNo() {
        return deleteNo;
    }

    /**
     * @return useCorresponAccessControl
     */
    public boolean isUseCorresponAccessControl() {
        return useCorresponAccessControl;
    }

    /**
     * @param useAccessControl the useCorresponAccessControl to set
     */
    public void setUseCorresponAccessControl(boolean useAccessControl) {
        this.useCorresponAccessControl = useAccessControl;
    }

    /**
     * 新規登録か更新か判定する.
     * @return 登録ならtrue / 更新ならfalse
     */
    public boolean isNew() {
        return getId() == null || getId() == 0;
    }

    public boolean isUseLearning() {
        return useLearning;
    }

    public void setUseLearning(boolean useLearning) {
        this.useLearning = useLearning;
    }
}
