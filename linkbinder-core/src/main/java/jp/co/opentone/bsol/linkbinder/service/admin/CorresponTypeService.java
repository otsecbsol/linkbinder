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
package jp.co.opentone.bsol.linkbinder.service.admin;

import java.util.List;

import jp.co.opentone.bsol.framework.core.service.IService;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.linkbinder.dto.CorresponType;
import jp.co.opentone.bsol.linkbinder.dto.SearchCorresponTypeResult;
import jp.co.opentone.bsol.linkbinder.dto.WorkflowPattern;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponTypeCondition;

/**
 * このサービスではコレポン文書種別に関する処理を提供する.
 * @author opentone
 */
public interface CorresponTypeService extends IService {

    /**
     * 指定された条件に該当するコレポン文書種別を検索し返す.
     * @param condition 検索条件
     * @return コレポン文書種別
     * @throws ServiceAbortException 取得失敗
     */
    List<CorresponType> search(SearchCorresponTypeCondition condition) throws ServiceAbortException;

    /**
     * 指定されたコレポン文書種別を返す.
     * @param id ID
     * @return コレポン文書種別
     * @throws ServiceAbortException 検索失敗
     */
    CorresponType find(Long id) throws ServiceAbortException;

    /**
     * 指定されたコレポン文書種別一覧をExcel形式に変換して返す.
     * @param corresponTypes コレポン文書種別
     * @return Excel変換データ
     * @throws ServiceAbortException 変換失敗
     */
    byte[] generateExcel(List<CorresponType> corresponTypes) throws ServiceAbortException;

    /**
     * 指定された条件に該当するコレポン文書種別を検索し、指定ページのレコードだけを返す.
     * @param condition 検索条件
     * @return コレポン文書種別リスト
     * @throws ServiceAbortException 検索失敗
     */
    SearchCorresponTypeResult searchPagingList(SearchCorresponTypeCondition condition)
        throws ServiceAbortException;

    /**
     * 指定されたコレポン文書種別を返す.
     * @param projectCorresponTypeId プロジェクト - コレポン文書ID
     * @return コレポン文書種別
     * @throws ServiceAbortException コレポン文書種別取得失敗
     */
    CorresponType findByProjectCorresponTypeId(Long projectCorresponTypeId)
        throws ServiceAbortException;

    /**
     * 指定されたコレポン文書種別をプロジェクトで利用可能にする.
     * @param corresponType コレポン文書種別
     * @return 利用可能にしたコレポン文書種別ID
     * @throws ServiceAbortException 登録失敗
     */
    Long assignTo(CorresponType corresponType) throws ServiceAbortException;

    /**
     * 入力値を検証する.
     * @param corresponType コレポン文書種別
     * @return 検証正常true / 検証異常false
     * @throws ServiceAbortException 検証異常
     */
    boolean validate(CorresponType corresponType) throws ServiceAbortException;

    /**
     * プロジェクトに登録されていないコレポン文書種別を返す.
     * @return コレポン文書種別
     */
    List<CorresponType> searchNotAssigned();

    /**
     * 承認フローパターンを返す.
     * @return 承認フローパターンリスト
     */
    List<WorkflowPattern> searchWorkflowPattern();

    /**
     * 指定したコレポン文書種別を保存する.
     * @param corresponType コレポン文書種別
     * @return 保存したコレポン文書種別ID
     * @throws ServiceAbortException 保存失敗
     */
    Long save(CorresponType corresponType) throws ServiceAbortException;

    /**
     * 指定したコレポン文書種別を削除する.
     * @param corresponType コレポン文書種別
     * @throws ServiceAbortException 削除失敗
     */
    void delete(CorresponType corresponType) throws ServiceAbortException;

}
