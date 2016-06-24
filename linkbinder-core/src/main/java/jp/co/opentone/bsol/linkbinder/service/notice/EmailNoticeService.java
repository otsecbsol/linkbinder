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
package jp.co.opentone.bsol.linkbinder.service.notice;

import java.util.List;

import jp.co.opentone.bsol.framework.core.service.IService;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.linkbinder.dto.AddressCorresponGroup;
import jp.co.opentone.bsol.linkbinder.dto.Correspon;
import jp.co.opentone.bsol.linkbinder.dto.EmailNoticeRecvSettingResult;
import jp.co.opentone.bsol.linkbinder.dto.PersonInCharge;
import jp.co.opentone.bsol.linkbinder.dto.UserProfile;
import jp.co.opentone.bsol.linkbinder.dto.code.EmailNoticeEventCd;
import jp.co.opentone.bsol.linkbinder.dto.notice.EmailNoticeRecvSetting;

/**
 * E-mail通知に関するサービスを提供する.
 * @author opentone
 */
public interface EmailNoticeService extends IService {

    /**
     * コレポン文書発行通知(メール通知機能)の情報登録を行う.
     *
     * @param correspon ワークフロー更新の発生したコレポン文書
     * @param emailNoticeEventCd メール通知イベントコード
     * @throws ServiceAbortException 通知情報の登録に失敗した場合
     */
    void sendWorkflowNotice(Correspon correspon, EmailNoticeEventCd emailNoticeEventCd)
            throws ServiceAbortException;

    /**
     * コレポン文書発行通知(メール通知機能)の情報登録を行う.
     *
     * @param correspon 発行通知の発生したコレポン文書
     * @param emailNoticeEventCd メール通知イベントコード
     * @param additionalToUserIdArray 追加したアドレス
     * @throws ServiceAbortException 通知情報の登録に失敗した場合
     */
    void sendIssuedNotice(Correspon correspon, EmailNoticeEventCd emailNoticeEventCd,
            String[] additionalToUserIdArray) throws ServiceAbortException;

    /**
     * 宛先追加分のユーザに対してコレポン文書発行通知(メール通知機能)の情報登録を行う.
     *
     * @param correspon 発行通知の発生したコレポン文書
     * @param emailNoticeEventCd メール通知イベントコード
     * @param oldAddressCorresponGroupList 更新前の宛先・活動単位リスト
     * @throws ServiceAbortException 通知情報の登録に失敗した場合
     */
    void sendIssuedNoticeToAddtionalAddressUser(Correspon correspon,
            EmailNoticeEventCd emailNoticeEventCd,
            List<AddressCorresponGroup> oldAddressCorresponGroupList) throws ServiceAbortException;

    /**
     * 追加設定分のPICに対して担当者決定通知(メール通知機能)の情報登録を行う.
     *
     * @param correspon 担当者決定通知の発生したコレポン文書
     * @param emailNoticeEventCd メール通知イベントコード
     * @param oldPics 更新前のPICリスト
     * @param newPics 新規設定PICリスト
     * @throws ServiceAbortException 通知情報の登録に失敗した場合
     */
    void sendPICAssignedEmailNoticeToAdditionalPIC(Correspon correspon,
            EmailNoticeEventCd emailNoticeEventCd, List<PersonInCharge> oldPics,
            List<PersonInCharge> newPics) throws ServiceAbortException;

    /**
     * 指定された条件に該当するメール通知受信設定を検索し返す.
     *
     * @param empNo 社員番号
     * @return EmailNoticeRecvSettingResult メール通知受信設定
     * @throws ServiceAbortException 検索失敗した場合
     */
     EmailNoticeRecvSettingResult findEmailNoticeRecvSetting(String empNo)
             throws ServiceAbortException;

    /**
     * メール通知受信設定情報の情報登録を行う.
     *
     * @param recvSetting メール通知受信設定
     * @param userProfile ユーザー情報
     * @throws ServiceAbortException 保存に失敗した場合
     */
    void save(List<EmailNoticeRecvSetting> recvSetting, UserProfile userProfile)
            throws ServiceAbortException;
}
