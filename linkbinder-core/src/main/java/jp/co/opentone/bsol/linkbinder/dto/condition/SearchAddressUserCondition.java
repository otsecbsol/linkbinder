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

import jp.co.opentone.bsol.linkbinder.dto.AbstractDto;
import jp.co.opentone.bsol.linkbinder.dto.AddressCorresponGroup;
import jp.co.opentone.bsol.linkbinder.dto.AddressUser;
import jp.co.opentone.bsol.linkbinder.dto.code.AddressUserType;
import jp.co.opentone.bsol.linkbinder.dto.code.EmailNoticeReceivable;
import jp.co.opentone.bsol.linkbinder.dto.notice.EmailNoticeRecvSetting;

/**
 * 宛先 - ユーザーの検索条件.
 *
 * @author opentone
 *
 */
public class SearchAddressUserCondition extends AbstractDto {
    /**
     *
     */
    private static final long serialVersionUID = -4274972584330687743L;

    /**
     * v_address_correspon_group のDTO.
     */
    private AddressCorresponGroup addressCorresponGroup;

    /**
     * v_address_user のDTO.
     */
    private AddressUser addressUser;

    /**
     * v_email_notice_recv_setting のDTO.
     */
    private EmailNoticeRecvSetting emailNoticeRecvSetting;

    /**
     * @return the addressCorresponGroup
     */
    public AddressCorresponGroup getAddressCorresponGroup() {
        return addressCorresponGroup;
    }

    /**
     * @param addressCorresponGroup the addressCorresponGroup to set
     */
    public void setAddressCorresponGroup(AddressCorresponGroup addressCorresponGroup) {
        this.addressCorresponGroup = addressCorresponGroup;
    }

    /**
     * @return the addressUser
     */
    public AddressUser getAddressUser() {
        return addressUser;
    }

    /**
     * @param addressUser the addressUser to set
     */
    public void setAddressUser(AddressUser addressUser) {
        this.addressUser = addressUser;
    }

    /**
     * @return the emailNoticeRecvSetting
     */
    public EmailNoticeRecvSetting getEmailNoticeRecvSetting() {
        return emailNoticeRecvSetting;
    }

    /**
     * @param emailNoticeRecvSetting the emailNoticeRecvSetting to set
     */
    public void setEmailNoticeRecvSetting(EmailNoticeRecvSetting emailNoticeRecvSetting) {
        this.emailNoticeRecvSetting = emailNoticeRecvSetting;
    }

    /**
     * 宛先ユーザー種別：Normal Userを取得します.
     * @return 宛先ユーザー種別：検証依頼中
     */
    public AddressUserType getAddressUserTypeNormalUser() {
        return AddressUserType.NORMAL_USER;
    }

    /**
     * 宛先ユーザー種別：Attentionを取得します.
     * @return 宛先ユーザー種別：Attention
     */
    public AddressUserType getAddressUserTypeAttention() {
        return AddressUserType.ATTENTION;
    }

    /**
     * メール通知受信要否：Noを取得します.
     * @return メール通知受信要否：No
     */
    public EmailNoticeReceivable getEmailNoticeReceivableNo() {
        return EmailNoticeReceivable.NO;
    }
}
