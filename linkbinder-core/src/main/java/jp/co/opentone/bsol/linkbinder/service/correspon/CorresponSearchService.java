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
import jp.co.opentone.bsol.linkbinder.dto.Correspon;
import jp.co.opentone.bsol.linkbinder.dto.CorresponIndexHeader;
import jp.co.opentone.bsol.linkbinder.dto.SearchCorresponResult;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponCondition;

import java.util.List;

/**
 * このサービスではコレポン文書一覧に関する処理を提供する.
 * @author opentone
 */
public interface CorresponSearchService extends IService {

    /**
     * 指定された条件に該当するコレポン文書一覧情報を取得する. 取得に成功した際は、取得した一覧情報を返す. ①一覧情報を条件に合わせてSortし返す.
     * ②指定ページの該当する一覧情報を取得して返す(ページング機能).
     * @param condition
     *            指定された条件情報
     * @return コレポン文書一覧情報
     * @throws ServiceAbortException
     *             対象のコレポン文書の取得に失敗した場合
     */
    SearchCorresponResult search(SearchCorresponCondition condition) throws ServiceAbortException;

    /**
     * 指定されたコレポン文書情報をCSV形式のデータに変換する. CSV形式に変換して返す.
     * @param correspons
     *            指定されたコレポン文書情報
     * @return CSV形式データ
     * @throws ServiceAbortException
     *             CSV形式変換に失敗した場合
     */
    byte[] generateCsv(List<Correspon> correspons) throws ServiceAbortException;

    /**
     * 指定されたコレポン文書情報をExcelワークブックに変換する. Excel形式に変換して返す.
     * @param correspons
     *            指定されたコレポン文書情報
     * @return Excel形式データ
     * @throws ServiceAbortException
     *             Excel形式変換に失敗した場合
     */
    byte[] generateExcel(List<Correspon> correspons) throws ServiceAbortException;

    /**
     * 指定されたコレポン文書情報をHTML形式に変換する. HTML形式に変換して返す.
     * @param correspons
     *            指定されたコレポン文書情報
     * @param header
     *            列の表示／非表示
     * @return HTML形式データ
     * @throws ServiceAbortException
     *             HTML形式変換に失敗した場合
     */
    byte[] generateHTML(List<Correspon> correspons, CorresponIndexHeader header)
        throws ServiceAbortException;

    /**
     * 指定されたコレポン文書一覧をZIP形式に変換して返す.
     * @param correspons コレポン文書情報
     * @return ZIP形式データ
     * @throws ServiceAbortException ZIP形式変換に失敗した場合
     */
    byte[] generateZip(List<Correspon> correspons) throws ServiceAbortException;

    /**
     * 指定されたコレポン文書一覧をZIP形式に変換して返す.
     * @param correspons コレポン文書情報
     * @param usePersonInCharge PersonInChargeの利用可否
     * @return ZIP形式データ
     * @throws ServiceAbortException ZIP形式変換に失敗した場合
     */
    byte[] generateZip(List<Correspon> correspons, boolean usePersonInCharge)
            throws ServiceAbortException;

    /**
     * 指定されたコレポン文書の文書状態を変更する.
     * @param correspons コレポン文書リスト
     * @throws ServiceAbortException 文書状態の更新に失敗
     */
    void updateCorresponsStatus(List<Correspon> correspons) throws ServiceAbortException;

    /**
     * 指定されたコレポン文書の既読/未読状態を変更する.
     * @param correspons コレポン文書リスト
     * @throws ServiceAbortException 既読/未読状態の更新に失敗
     */
    void updateCorresponsReadStatus(List<Correspon> correspons) throws ServiceAbortException;

    /**
     * 指定されたコレポン文書を削除する.
     * @param correspons コレポン文書リスト
     * @throws ServiceAbortException 削除に失敗
     */
    void deleteCorrespons(List<Correspon> correspons, boolean learning) throws ServiceAbortException;

    /**
     * 指定された条件に該当するコレポン文書ID一覧を取得する. ①一覧情報を条件に合わせてSortし返す.
     * @param condition
     *            指定された条件情報
     * @return コレポン文書ID一覧
     * @throws ServiceAbortException
     *             対象のコレポン文書の取得に失敗した場合
     */
     List<Long> searchId(SearchCorresponCondition condition) throws ServiceAbortException;

    /**
     * 指定された条件に該当するコレポン文書ID一覧を取得する. 取得に成功した際は、取得したIDを返す. ①一覧情報を条件に合わせてSortし返す.
     * ②指定ページの該当する一覧情報を取得して返す(ページング機能).
     * @param condition 指定された条件情報
     * @return コレポン文書ID一覧
     * @throws ServiceAbortException 対象のコレポン文書の取得に失敗した場合
     */
     List<Long> searchIdInPage(SearchCorresponCondition condition) throws ServiceAbortException;
}
