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
package jp.co.opentone.bsol.linkbinder.service.correspon;

import jp.co.opentone.bsol.framework.core.service.IService;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.linkbinder.dto.AddressUser;
import jp.co.opentone.bsol.linkbinder.dto.Attachment;
import jp.co.opentone.bsol.linkbinder.dto.Correspon;
import jp.co.opentone.bsol.linkbinder.dto.CorresponResponseHistory;
import jp.co.opentone.bsol.linkbinder.dto.PersonInCharge;
import jp.co.opentone.bsol.linkbinder.dto.code.CorresponStatus;
import jp.co.opentone.bsol.linkbinder.util.view.correspon.CorresponResponseHistoryModel;

import java.util.List;

/**
 * このサービスではコレポン文書に関する処理を提供する.
 * @author opentone
 */
public interface CorresponService extends IService {

    /**
     * 指定されたIDでコレポン文書を取得する.
     * @param id ID
     * @return コレポン文書
     * @throws ServiceAbortException コレポン文書取得に失敗
     */
    Correspon find(Long id) throws ServiceAbortException;

    /**
     * 指定されたコレポン文書のカスタムフィールドを現在のカスタムフィールドマスタに合わせて
     * 調整する.
     * @param correspon コレポン文書
     * @throws ServiceAbortException 調整に失敗
     */
    void adjustCustomFields(Correspon correspon) throws ServiceAbortException;

    /**
     * 指定されたコレポン文書に添付されたファイル情報を取得する.
     * @param corresponId コレポン文書ID
     * @param attachmentId ファイルID
     * @return 添付ファイル情報
     * @throws ServiceAbortException 添付ファイル情報取得に失敗
     */
    Attachment findAttachment(Long corresponId, Long attachmentId) throws ServiceAbortException;

    /**
     * 指定されたコレポン文書に添付されたファイル情報を取得する.
     * @param corresponId コレポン文書ID
     * @return 添付ファイル情報
     * @throws ServiceAbortException 添付ファイル情報取得に失敗
     */
    List<Attachment> findAttachments(Long corresponId) throws ServiceAbortException;

    /**
     * 指定されたコレポン文書を検証依頼状態にする.
     * @param correspon コレポン文書
     * @throws ServiceAbortException コレポン文書の検証依頼に失敗
     */
    void requestForApproval(Correspon correspon) throws ServiceAbortException;

    /**
     * 指定されたコレポン文書をHTML形式に変換して返す.
     * @param correspon コレポン文書
     * @param corresponResponseHistory コレポン文書履歴情報
     * @return HTML形式
     * @throws ServiceAbortException 変換に失敗
     */
    byte[] generateHTML(Correspon correspon,
        List<CorresponResponseHistoryModel> corresponResponseHistory) throws ServiceAbortException;

    /**
     * 指定されたコレポン文書をHTML形式に変換して返す.
     * @param correspon コレポン文書
     * @param corresponResponseHistory コレポン文書履歴情報
     * @param usePersonInCharge PersonInChargeの利用可否
     * @return HTML形式
     * @throws ServiceAbortException 変換に失敗
     */
    byte[] generateHTML(Correspon correspon,
        List<CorresponResponseHistoryModel> corresponResponseHistory, boolean usePersonInCharge)
            throws ServiceAbortException;

    /**
     * 指定されたコレポン文書を削除する.
     * @param correspon コレポン文書
     * @throws ServiceAbortException コレポン文書の削除に失敗
     */
    void delete(Correspon correspon) throws ServiceAbortException;

    /**
     * 指定されたコレポン文書を発行する.
     * @param correspon コレポン文書
     * @throws ServiceAbortException コレポン文書の発行に失敗
     */
    void issue(Correspon correspon) throws ServiceAbortException;

    /**
     * 宛先-ユーザー及び任命したPerson in Chargeが返信したコレポン文書を取得する.
     * @param parentCorrespon 依頼コレポン文書
     * @param addressUserId 宛先-ユーザーID
     * @return 返信したコレポン文書
     * @throws ServiceAbortException 取得失敗
     */
    List<Correspon> findReplyCorrespons(Correspon parentCorrespon, Long addressUserId)
            throws ServiceAbortException;

    /**
     * 指定のコレポン文書の応答履歴を取得する.
     * @param correspon コレポン文書
     * @return 応答履歴
     * @throws ServiceAbortException 取得失敗
     */
    List<CorresponResponseHistory> findCorresponResponseHistory(Correspon correspon)
            throws ServiceAbortException;

    /**
     * 宛先-ユーザーに対するPerson in Chargeを任命する.
     * @param correspon コレポン文書
     * @param addressUser 宛先-ユーザー
     * @param pics Person in Chargeのリスト
     * @throws ServiceAbortException Person in Charge設定失敗
     */
    void assignPersonInCharge(Correspon correspon, AddressUser addressUser,
        List<PersonInCharge> pics) throws ServiceAbortException;

    /**
     * 文書状態を更新する.
     * @param correspon コレポン文書
     * @param status コレポン文書の文書状態(これに更新する)
     * @throws ServiceAbortException 文書状態更新で例外発生
     */
    void updateCorresponStatus(Correspon correspon, CorresponStatus status)
    throws ServiceAbortException;

    /**
     * 返信要否、期限を更新する.
     * @param correspon コレポン文書
     * @throws ServiceAbortException 更新失敗
     */
    void savePartial(Correspon correspon) throws ServiceAbortException;

    /**
     * コレポン文書と全ての添付ファイルをZIP形式にして返す.
     * @param correspons コレポン文書情報
     * @return ZIP形式データ
     * @throws ServiceAbortException ZIP形式変換に失敗した場合
     */
    byte[] generateZip(Correspon correspons) throws ServiceAbortException;

    /**
     * コレポン文書と全ての添付ファイルをZIP形式にして返す.
     * @param correspons コレポン文書情報
     * @param usePersonInCharge PersonInChargeの利用可否
     * @return ZIP形式データ
     * @throws ServiceAbortException ZIP形式変換に失敗した場合
     */
    byte[] generateZip(Correspon correspons, boolean usePersonInCharge)
            throws ServiceAbortException;

    /**
     * 指定された文書を学習用プロジェクトへ公開する.
     * @param id 文書ID
     * @return 作成した文書IDリスト
     * @throws ServiceAbortException 保存に失敗
     */
    List<Long> issueToLearningProjects(Long id) throws ServiceAbortException;
}
