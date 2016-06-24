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
package jp.co.opentone.bsol.linkbinder.service.admin.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.co.opentone.bsol.framework.core.dao.KeyDuplicateException;
import jp.co.opentone.bsol.framework.core.dao.StaleRecordException;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.core.util.ArgumentValidator;
import jp.co.opentone.bsol.linkbinder.dao.ProjectCustomSettingDao;
import jp.co.opentone.bsol.linkbinder.dto.ProjectCustomSetting;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.service.AbstractService;
import jp.co.opentone.bsol.linkbinder.service.admin.ProjectCustomSettingService;

/**
 * このサービスではプロジェクトカスタム設定情報に関する処理を提供する.
 * @author opentone
 */
@Service
@Transactional(rollbackFor = { ServiceAbortException.class })
public class ProjectCustomSettingServiceImpl extends AbstractService
        implements ProjectCustomSettingService {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 8860291169551812371L;

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.service.admin.ProjectCustomSettingService#find(java.lang.String)
     */
    @Transactional(readOnly = true)
    public ProjectCustomSetting find(String projectId) throws ServiceAbortException {
        return find(projectId, true);
    }

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.service.admin.ProjectCustomSettingService
     * #find(java.lang.String, boolean)
     */
    @Transactional(readOnly = true)
    public ProjectCustomSetting find(String projectId, boolean isProjectIdValidate)
        throws ServiceAbortException {
        ArgumentValidator.validateNotNull(projectId);
        if (isProjectIdValidate) {
            validateProjectId(projectId);
        }
        ProjectCustomSetting result = findByProjectId(projectId);
        return result;
    }

    /**
     * プロジェクトIDを指定してプロジェクトカスタム設定情報を取得する.
     * @param projectId プロジェクトID
     * @return プロジェクトカスタム設定情報
     */
    private ProjectCustomSetting findByProjectId(String projectId) throws ServiceAbortException {
        ProjectCustomSettingDao dao = getDao(ProjectCustomSettingDao.class);
        ProjectCustomSetting result = dao.findByProjectId(projectId);
        if (result == null) {
            return new ProjectCustomSetting();
        }
        return result;
    }

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.service.admin.ProjectCustomSettingService
     * #save(jp.co.opentone.bsol.linkbinder.dto.ProjectCustomSetting)
     */
    public Long save(ProjectCustomSetting projectCustomSetting) throws ServiceAbortException {
        validate(projectCustomSetting);
        Long id = null;
        if (projectCustomSetting.isNew()) {
            ProjectCustomSetting insertprojectCustomSetting =
                    createInsertProjectCustomSetting(projectCustomSetting);
            id = insertProjectCustomSetting(insertprojectCustomSetting);
        } else {
            // 拠点情報のプロジェクトが現在選択中のプロジェクト以外はエラー
            validateProjectId(projectCustomSetting.getProjectId());
            ProjectCustomSetting updateprojectCustomSetting =
                    createUpdateProjectCustomSetting(projectCustomSetting);
            id = updateProjectCustomSetting(updateprojectCustomSetting);
        }
        return id;
    }

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.service.admin.ProjectCustomSettingService
     * #validate(jp.co.opentone.bsol.linkbinder.dto.ProjectCustomSetting)
     */
    @Transactional(readOnly = true)
    public void validate(ProjectCustomSetting projectCustomSetting) throws ServiceAbortException {
        ArgumentValidator.validateNotNull(projectCustomSetting);
        validatePermission();
    }

    /**
     * 権限チェックを行う.
     * @return 権限がある場合はtrue / ない場合はfalse
     */
    private boolean isValidPermission() {
        User loginUser = getCurrentUser();
        String loginProjectId = getCurrentProjectId();
        return isSystemAdmin(loginUser) || isProjectAdmin(loginUser, loginProjectId);
    }

    /**
     * 権限がない場合はエラー.
     * @throws ServiceAbortException 権限がない
     */
    private void validatePermission() throws ServiceAbortException {
        if (!isValidPermission()) {
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT);
        }
    }

    /**
     * 登録用のプロジェクトカスタム設定情報オブジェクトを作成する.
     * @param projectCustomSetting プロジェクトカスタム設定情報
     * @return 登録用プロジェクトカスタム設定情報
     */
    private ProjectCustomSetting createInsertProjectCustomSetting(
        ProjectCustomSetting projectCustomSetting) {
        ProjectCustomSetting result = new ProjectCustomSetting();
        result.setProjectId(getCurrentProjectId());
        result.setDefaultStatus(projectCustomSetting.getDefaultStatus());
        result.setUsePersonInCharge(projectCustomSetting.isUsePersonInCharge());
        result.setCreatedBy(getCurrentUser());
        result.setUpdatedBy(getCurrentUser());
        return result;
    }

    /**
     * プロジェクトカスタム設定情報を登録する.
     * @param projectCustomSetting プロジェクトカスタム設定情報
     * @return 新規に登録したプロジェクトカスタム設定情報のID
     * @throws ServiceAbortException 登録失敗
     */
    private Long insertProjectCustomSetting(ProjectCustomSetting projectCustomSetting)
        throws ServiceAbortException {
        ProjectCustomSettingDao dao = getDao(ProjectCustomSettingDao.class);
        try {
            return dao.create(projectCustomSetting);
        } catch (KeyDuplicateException kde) {
            throw new ServiceAbortException(kde);
        }
    }

    /**
     * 更新用のプロジェクトカスタム設定情報オブジェクトを作成する.
     * @param projectCustomSetting プロジェクトカスタム設定情報
     * @return 更新用プロジェクトカスタム設定情報
     */
    private ProjectCustomSetting createUpdateProjectCustomSetting(
        ProjectCustomSetting projectCustomSetting) {
        ProjectCustomSetting result = new ProjectCustomSetting();
        result.setId(projectCustomSetting.getId());
        result.setProjectId(projectCustomSetting.getProjectId());
        result.setDefaultStatus(projectCustomSetting.getDefaultStatus());
        result.setUsePersonInCharge(projectCustomSetting.isUsePersonInCharge());
        result.setUseCorresponAccessControl(projectCustomSetting.isUseCorresponAccessControl());
        result.setUpdatedBy(getCurrentUser());
        result.setVersionNo(projectCustomSetting.getVersionNo());
        return result;
    }

    /**
     * プロジェクトカスタム設定情報を更新する.
     * @return projectCustomSetting プロジェクトカスタム設定情報
     * @throws ServiceAbortException 更新失敗
     */
    private Long updateProjectCustomSetting(ProjectCustomSetting projectCustomSetting)
        throws ServiceAbortException {
        ProjectCustomSettingDao dao = getDao(ProjectCustomSettingDao.class);
        try {
            dao.update(projectCustomSetting);
        } catch (KeyDuplicateException kde) {
            throw new ServiceAbortException(kde);
        } catch (StaleRecordException sre) {
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_PROJECT_CUSTOM_SETTING_NOT_EXIST);
        }
        return projectCustomSetting.getId();
    }
}
