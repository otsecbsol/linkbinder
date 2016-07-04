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
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroup;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroupUser;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroupUserMapping;
import jp.co.opentone.bsol.linkbinder.dto.Discipline;
import jp.co.opentone.bsol.linkbinder.dto.SearchCorresponGroupResult;
import jp.co.opentone.bsol.linkbinder.dto.Site;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponGroupCondition;

/**
 * このサービスではグループ情報に関する処理を提供する.
 * @author opentone
 */
public interface CorresponGroupService extends IService {

    /**
     * 指定された条件に該当する活動単位を返す.
     * @param condition
     *            検索条件
     * @return 活動単位
     * @throws ServiceAbortException
     *             検索に失敗
     */
    List<CorresponGroup> search(SearchCorresponGroupCondition condition)
        throws ServiceAbortException;

    /**
     * 指定された活動単位に所属するユーザーを返す.
     * @param id
     *            活動単位ID
     * @return ユーザー
     */
    List<CorresponGroupUser> findMembers(Long id);

    /**
     * プロジェクト内の活動単位とユーザーのマッピング情報を返す.
     * @return 活動単位とユーザーのマッピング情報
     */
    List<CorresponGroupUserMapping> findCorresponGroupUserMappings();

    /**
     * プロジェクト内の活動単位とユーザーのマッピング情報を返す.
     * グループID＝0を取得する際に、当該グループのグループIDも返す.
     * コレポン文書の宛先選択ダイアログ用.
     * @return 活動単位とユーザーのマッピング情報
     */
    List<CorresponGroupUserMapping> findCorresponGroupIdUserMappings();

    /**
     * 指定された活動単位を返す.
     * @param id
     *            活動単位ID
     * @return 活動単位
     * @throws ServiceAbortException
     *             検索に失敗
     * @throws ServiceAbortException
     *             検索エラー
     */
    CorresponGroup find(Long id) throws ServiceAbortException;

    /**
     * 指定された条件に該当する活動単位を検索し、指定ページのレコードだけを返す.
     * @param condition
     *            検索条件
     * @return 活動単位リスト
     * @throws ServiceAbortException
     *             検索エラー
     */
    SearchCorresponGroupResult searchPagingList(SearchCorresponGroupCondition condition)
        throws ServiceAbortException;

    /**
     * 指定された活動単位一覧をExcel形式に変換して返す.
     * @param corresponGroups
     *            活動単位一覧
     * @return Excel形式データ
     * @throws ServiceAbortException
     *             変換エラー
     */
    byte[] generateExcel(List<CorresponGroup> corresponGroups) throws ServiceAbortException;

    /**
     * 指定された活動単位を削除する.
     * @param corresponGroup
     *            活動単位
     * @throws ServiceAbortException
     *             削除エラー
     */
    void delete(CorresponGroup corresponGroup) throws ServiceAbortException;

    /**
     * 指定された拠点で活動する部門を追加する.
     * @param site
     *            拠点
     * @param discipline
     *            部門
     * @return 活動単位ID
     * @throws ServiceAbortException
     *             削除エラー
     */
    Long add(Site site, Discipline discipline) throws ServiceAbortException;

    /**
     * 指定された活動単位を保存する.
     * @param corresponGroup
     *            活動単位
     * @param users
     *            ユーザー
     * @throws ServiceAbortException
     *             保存処理失敗
     */
    void save(CorresponGroup corresponGroup, List<User> users) throws ServiceAbortException;

    /**
     * 追加する部門情報を取得する.
     * @param siteId 拠点コード
     * @return 追加する部門情報
     */
    List<Discipline> searchNotAdd(Long siteId);

}
