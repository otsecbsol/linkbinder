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
import jp.co.opentone.bsol.framework.core.dao.KeyDuplicateException;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroupUser;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroupUserMapping;
import jp.co.opentone.bsol.linkbinder.dto.User;

/**
 * correspon_group_user を操作するDao.
 *
 * @author opentone
 *
 */
public interface CorresponGroupUserDao extends GenericDao<CorresponGroupUser> {

    /**
     * 活動単位IDで、活動単位-ユーザーを検索する.
     * @param corresponGroupId
     *            活動単位ID
     * @return 活動単位に所属するユーザー
     */
    List<CorresponGroupUser> findByCorresponGroupId(Long corresponGroupId);

    /**
     * プロジェクトIDで、活動単位-ユーザーを検索する.
     * @param projectId プロジェクトID
     * @return 活動単位に所属するユーザー
     */
    List<CorresponGroupUser> findByProjectId(String projectId);

    /**
     * プロジェクトIDで、活動単位-ユーザーを検索する.
     * v_project_userを主とし、紐付くv_correspon_group_userを検索する.
     * @param projectId プロジェクトID
     * @return 活動単位に所属するユーザー
     */
    List<CorresponGroupUser> findProjectUserWithGroupByProjectId(String projectId);

    /**
     * 指定されたプロジェクト内の活動単位と、各活動単位に所属するユーザーのマッピング情報を返す.
     * @param projectId プロジェクトID
     * @return 活動単位とユーザーのマッピング情報
     */
    List<CorresponGroupUserMapping> findCorresponGroupUserMapping(
            String projectId);

    /**
     * 指定されたプロジェクト内の活動単位と、各活動単位に所属するユーザーのマッピング情報を返す.
     * グループID＝0を取得する際に、当該グループのグループIDも返す.
     * コレポン文書の宛先選択ダイアログ用.
     * @param projectId プロジェクトID
     * @return 活動単位とユーザーのマッピング情報
     */
    List<CorresponGroupUserMapping> findCorresponGroupIdUserMapping(
            String projectId);

    /**
     * 活動単位IDと従業員番号で、活動単位-ユーザーを検索する.
     * @param corresponGroupId
     *            活動単位ID
     * @param empNo
     *            従業員番号
     * @return 活動単位に所属するユーザー
     */
    CorresponGroupUser findByEmpNo(Long corresponGroupId, String empNo);

    /**
     * 活動単位IDで、活動単位ユーザーを削除する.
     * @param corresponGroupId
     *            活動単位ID
     * @param updateUser
     *            更新者
     * @return 該当件数
     * @throws KeyDuplicateException
     *             キーが重複した場合
     */
    Integer deleteByCorresponGroupId(Long corresponGroupId, User updateUser)
        throws KeyDuplicateException;
}
