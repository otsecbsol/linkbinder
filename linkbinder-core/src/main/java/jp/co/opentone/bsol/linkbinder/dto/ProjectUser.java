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


import org.apache.commons.lang.StringUtils;

import jp.co.opentone.bsol.framework.core.config.SystemConfig;
import jp.co.opentone.bsol.framework.core.exception.ApplicationFatalRuntimeException;
import jp.co.opentone.bsol.linkbinder.Constants;

/**
 * テーブル [v_project_user] の1レコードを表すDto.
 *
 * @author opentone
 *
 */
public class ProjectUser extends AbstractDto {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 4048434694752724815L;

    /**
     * Project.
     * <p>
     * [v_project_user.project_id]
     * </p>
     */
    private String projectId;

    /**
     * User.
     */
    private User user;

    /**
     * Security level.
     * <p>
     * [v_project_user.security_level]
     * </p>
     */
    private String securityLevel;

    /**
     * Project company.
     */
    private Company projectCompany;

    /**
     * Project user profile.
     */
    private ProjectUserProfile projectUserProfile;

    /**
     * Default correspon group.
     */
    private CorresponGroup defaultCorresponGroup;

    /**
     * プロジェクト管理者フラグ.
     */
    private String projectAdminFlg;

    /**
     * 空のインスタンスを生成する.
     */
    public ProjectUser() {
    }

    /**
     * このユーザーがProject Adminの権限を持つ場合はtrue.
     * @return Project Adminの権限を持つ場合はtrue、それ以外はfalse
     */
    public boolean isProjectAdmin() {

        String projectAdmin = SystemConfig.getValue(Constants.KEY_SECURITY_FLG_PROJECT_ADMIN);

        if (StringUtils.isEmpty(projectAdmin)) {
            throw new ApplicationFatalRuntimeException(
                                           "Security flg (Project Admin) not defined.");
        }
        return projectAdmin.equals(getProjectAdminFlg());
    }

    /**
     * projectId の値を返す.
     * <p>
     * [v_project_user.project_id]
     * </p>
     * @return projectId
     */
    public String getProjectId() {
        return projectId;
    }

    /**
     * projectId の値を設定する.
     * <p>
     * [v_project_user.project_id]
     * </p>
     * @param projectId
     *            projectId
     */
    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    /**
     * securityLevel の値を返す.
     * <p>
     * [v_project_user.security_level]
     * </p>
     * @return securityLevel
     */
    public String getSecurityLevel() {
        return securityLevel;
    }

    /**
     * securityLevel の値を設定する.
     * <p>
     * [v_project_user.security_level]
     * </p>
     * @param securityLevel
     *            securityLevel
     */
    public void setSecurityLevel(String securityLevel) {
        this.securityLevel = securityLevel;
    }

    /**
     * @param user
     *            the user to set
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * @return the user
     */
    public User getUser() {
        return user;
    }

    /**
     * @param projectCompany
     *            the projectCompany to set
     */
    public void setProjectCompany(Company projectCompany) {
        this.projectCompany = projectCompany;
    }

    /**
     * @return the projectCompany
     */
    public Company getProjectCompany() {
        return projectCompany;
    }

    /**
     * @param projectUserProfile
     *            the projectUserProfile to set
     */
    public void setProjectUserProfile(ProjectUserProfile projectUserProfile) {
        this.projectUserProfile = projectUserProfile;
    }

    /**
     * @return the projectUserProfile
     */
    public ProjectUserProfile getProjectUserProfile() {
        return projectUserProfile;
    }

    /**
     * @param defaultCorresponGroup
     *            the defaultCorresponGroup to set
     */
    public void setDefaultCorresponGroup(CorresponGroup defaultCorresponGroup) {
        this.defaultCorresponGroup = defaultCorresponGroup;
    }

    /**
     * @return the defaultCorresponGroup
     */
    public CorresponGroup getDefaultCorresponGroup() {
        return defaultCorresponGroup;
    }

    /**
     * projectAdminFlgを取得します.
     * @return the projectAdminFlg
     */
    public String getProjectAdminFlg() {
        return projectAdminFlg;
    }

    /**
     * projectAdminFlgを設定します.
     * @param projectAdminFlg the projectAdminFlg to set
     */
    public void setProjectAdminFlg(String projectAdminFlg) {
        this.projectAdminFlg = projectAdminFlg;
    }
}
