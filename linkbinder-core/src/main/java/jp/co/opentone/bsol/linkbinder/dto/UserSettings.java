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

import java.util.List;

/**
 * ユーザー設定情報のDto.
 *
 * @author opentone
 *
 */
public class UserSettings extends AbstractDto {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 1550224556968365080L;

    /**
     * ユーザー.
     */
    private User user;

    /**
     * プロジェクトのユーザー設定情報リスト.
     */
    private List<ProjectUserSetting> projectUserSettingList;

    /**
     * 設定するデフォルトプロジェクトID.
     */
    private String defaultProjectId;

    /**
     * 設定するデフォルトメールアドレス.
     */
    private String defaultEmailAddress;

    /**
     * パスワード.
     */
    private String password;

    /**
     * RSS用feedKey.
     */
    private String rssFeedKey;

    /**
     * ユーザー設定情報の現在のバージョンNo.
     */
    private Long userProfileVersionNo;

    /**
     * 空のインスタンスを生成する.
     */
    public UserSettings() {
    }

    /**
     * userを取得します.
     * @return the user
     */
    public User getUser() {
        return user;
    }

    /**
     * userを設定します.
     * @param user the user to set
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * projectUserSettingListを取得します.
     * @return the projectUserSettingList
     */
    public List<ProjectUserSetting> getProjectUserSettingList() {
        //  このオブジェクトが保持するユーザー情報を子オブジェクトにも適用
        if (projectUserSettingList != null) {
            for (ProjectUserSetting s : projectUserSettingList) {
                s.setUser(user);
            }
        }
        return projectUserSettingList;
    }

    /**
     * projectUserSettingListを設定します.
     * @param projectUserSettingList the projectUserSettingList to set
     */
    public void setProjectUserSettingList(List<ProjectUserSetting> projectUserSettingList) {
        this.projectUserSettingList = projectUserSettingList;
    }

    /**
     * defaultProjectIdを取得します.
     * @return the defaultProjectId
     */
    public String getDefaultProjectId() {
        return defaultProjectId;
    }

    /**
     * defaultProjectIdを設定します.
     * @param defaultProjectId the defaultProjectId to set
     */
    public void setDefaultProjectId(String defaultProjectId) {
        this.defaultProjectId = defaultProjectId;
    }

    /**
     * defaultEmailAddressを取得します.
     * @return the defaultEmailAddress
     */
    public String getDefaultEmailAddress() {
        return defaultEmailAddress;
    }

    /**
     * defaultEmailAddressを設定します.
     * @param defaultEmailAddress the defaultEmailAddress to set
     */
    public void setDefaultEmailAddress(String defaultEmailAddress) {
        this.defaultEmailAddress = defaultEmailAddress;
    }

    /**
     * System Adminの場合はtrue.
     * @return System Adminの場合true
     */
    public boolean isSystemAdmin() {
        return user != null && user.isSystemAdmin();
    }

    /**
     * @param rssFeedKey the rssFeedKey to set
     */
    public void setRssFeedKey(String rssFeedKey) {
        this.rssFeedKey = rssFeedKey;
    }

    /**
     * @return the rssFeedKey
     */
    public String getRssFeedKey() {
        return rssFeedKey;
    }

    /**
     * @param userProfileVersionNo the userProfileVersionNo to set
     */
    public void setUserProfileVersionNo(Long userProfileVersionNo) {
        this.userProfileVersionNo = userProfileVersionNo;
    }

    /**
     * @return the userProfileVersionNo
     */
    public Long getUserProfileVersionNo() {
        return userProfileVersionNo;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }
}
