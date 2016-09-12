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
package jp.co.opentone.bsol.linkbinder.dao;

import java.util.List;

import jp.co.opentone.bsol.framework.core.dao.GenericDao;
import jp.co.opentone.bsol.framework.core.dao.RecordNotFoundException;
import jp.co.opentone.bsol.linkbinder.dto.Correspon;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroup;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroupSummary;
import jp.co.opentone.bsol.linkbinder.dto.CorresponResponseHistory;
import jp.co.opentone.bsol.linkbinder.dto.CorresponUserSummary;
import jp.co.opentone.bsol.linkbinder.dto.RSSCorrespon;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponCondition;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponUserSummaryCondition;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchRSSCorresponCondition;

/**
 * コレポン文書を操作するDao.
 *
 * @author opentone
 *
 */
public interface CorresponDao extends GenericDao<Correspon> {

    /**
     * 検索条件に該当するコレポン文書を検索する. 指定されたページに表示するコレポン文書だけを取得する.
     * <p>
     * v_corresponを検索し、関連するテーブルの情報も合わせて取得する.
     * </p>
     * @param condition 検索条件
     * @return コレポン文書のリスト
     */
    List<Correspon> find(SearchCorresponCondition condition);

    /**
     * 検索条件に該当するコレポン文書の数を検索する.
     * <p>
     * v_corresponを検索し、関連するテーブルの情報も合わせて取得する.
     * </p>
     * @param condition 検索条件
     * @return コレポン文書のリスト
     */
    int count(SearchCorresponCondition condition);

    /**
     * 指定された返信コレポン文書の、最上位の親コレポン文書を検索する.
     * <p>
     * v_corresponを検索し、関連するテーブルの情報も合わせて取得する.
     * </p>
     * @param id ID
     * @return コレポン文書
     * @throws RecordNotFoundException レコードが見つからない
     */
    Correspon findTopParent(Long id) throws RecordNotFoundException;

    /**
     * 指定されたプロジェクトのコレポン文書のコレポン文書種別と活動単位でのサマリ情報を検索する.
     * @param projectId プロジェクトID
     * @param userGroups 活動単位
     * @return コレポン文書のコレポン文書種別と活動単位でのサマリ情報
     */
    List<CorresponGroupSummary> findCorresponGroupSummary(String projectId,
                                                          CorresponGroup[] userGroups);

    /**
     * コレポン文書のユーザー情報でのサマリ情報を検索する.
     * @param condition 検索条件
     * @return コレポン文書のユーザー情報でのサマリ情報
     */
    CorresponUserSummary findCorresponUserSummary(SearchCorresponUserSummaryCondition condition);

    /**
     * 指定したカスタムフィールドが紐付いているコレポン文書の数を検索する.
     * @param projectCustomFieldId プロジェクトカスタムフィールドID
     * @return コレポン文書の数
     */
    int countCorresponByCustomField(Long projectCustomFieldId);

    /**
     * 指定された宛先-ユーザーが返信したコレポン文書を検索する.
     * <p>
     * 返されるCorresponオブジェクトには、IDとコレポン文書番号のみが格納されている.
     * </p>
     * @param addressUserId 宛先-ユーザーID
     * @return 返信コレポン文書. 但し各オブジェクトに格納されているはIDとコレポン文書番号のみ
     */
    List<Correspon> findReplyCorresponByAddressUserId(Long addressUserId);

    /**
     * 指定された宛先-グループが返信したコレポン文書を検索する.
     * <p>
     * 返されるCorresponオブジェクトには、IDとコレポン文書番号のみが格納されている.
     * </p>
     * @param corresponid 親コレポン文書番号
     * @param groupId 宛先-グループID
     * @return 返信コレポン文書. 但し各オブジェクトに格納されているはIDとコレポン文書番号のみ
     */
    List<Correspon> findReplyCorresponByGroupId(Long corresponid, Long groupId);

    /**
     * 指定したコレポン文書種別が紐付いているコレポン文書の件数を検索する.
     * @param projectCorresponTypeId プロジェクトコレポン文書種別ID
     * @return コレポン文書件数
     */
    int countCorresponByCorresponType(Long projectCorresponTypeId);

    /**
     * 指定されたユーザ、期間に対応するRSS配信対象コレポン文書を取得する.
     * @param condition 検索条件
     * @return RSS配信対象コレポン文書
     */
    List<RSSCorrespon> findRSSCorrespon(SearchRSSCorresponCondition condition);

    /**
     * 大本のコレポン文書IDを検索する.
     * @param corresponId コレポン文書ID
     * @return 大本のコレポン文書ID
     */
    Long findRootCorresponId(Long corresponId);

    /**
     * 指定のコレポン文書IDから応答履歴を検索する.
     * @param corresponId コレポン文書ID
     * @param currentCorresponId 表示中のコレポン文書ID
     * @return 応答履歴
     */
    List<CorresponResponseHistory> findCorresponResponseHistory(
        Long corresponId, Long currentCorresponId);

    /**
     * 検索条件に該当するコレポン文書を検索する. 条件に該当するコレポン文書IDを返す.
     * @param condition 検索条件
     * @return コレポン文書IDのリスト
     */
    List<Long> findId(SearchCorresponCondition condition);

    /**
     * 検索条件に該当するコレポン文書を検索する. 指定されたページに表示するコレポン文書IDだけを取得する.
     * @param condition 検索条件
     * @return コレポン文書IDのリスト
     * @author opentone
     */
    List<Long> findIdInPage(SearchCorresponCondition condition);

    /**
     * 学習用文書を学習用プロジェクトへ登録する。
     */
    Long insertLearningCorrespon(Correspon correspon);
}
