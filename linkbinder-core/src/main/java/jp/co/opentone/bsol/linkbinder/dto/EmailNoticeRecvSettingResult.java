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

import jp.co.opentone.bsol.linkbinder.dto.notice.EmailNoticeRecvSetting;

/**
 * メール通知受信設定画面を表示するクラスです.
 *
 * @author opentone
 *
 */
public class EmailNoticeRecvSettingResult {

    /**
     * テーブル [user_profile] の1レコードを表すDto.
     */
    private UserProfile userProfile;

    /**
     * テーブル [v_email_notice_recv_setting] の1レコードを表すDtoのList.
     */
    private List<EmailNoticeRecvSetting> emailNoticeRecvSettingList;

    /**
     * @return the userProfile
     */
    public UserProfile getUserProfile() {
        return userProfile;
    }

    /**
     * @param userProfile the userProfile to set
     */
    public void setUserProfile(UserProfile userProfile) {
        this.userProfile = userProfile;
    }

    /**
     * @return the emailNoticeRecvSettingList
     */
    public List<EmailNoticeRecvSetting> getEmailNoticeRecvSettingList() {
        return emailNoticeRecvSettingList;
    }

    /**
     * @param emailNoticeRecvSettingList the emailNoticeRecvSettingList to set
     */
    public void setEmailNoticeRecvSettingList(
            List<EmailNoticeRecvSetting> emailNoticeRecvSettingList) {
        this.emailNoticeRecvSettingList = emailNoticeRecvSettingList;
    }
}
