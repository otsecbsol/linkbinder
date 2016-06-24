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
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroup;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroupUser;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponGroupCondition;

/**
 * correspon_group を操作するDao.
 *
 * @author opentone
 *
 */
public interface CorresponGroupDao extends GenericDao<CorresponGroup> {

    /**
     * 活動単位を検索する.
     * @param condition 検索条件
     * @return 活動単位
     */
    List<CorresponGroup> find(SearchCorresponGroupCondition condition);

    /**
     * 従業員番号を指定して活動単位ユーザーを取得する.
     * @param projectId プロジェクトID
     * @param empNo 従業員番号
     * @return 活動単位
     */
    List<CorresponGroupUser> findByEmpNo(String projectId, String empNo);

    /**
     * 従業員番号を指定して活動単位ユーザーを取得する(ソート列指定付き).
     * @param projectId プロジェクトID
     * @param empNo 従業員番号
     * @param sortColumn ソート対象列(SQLConvertUtil#encode(String)で整形済みであること)
     * @return 活動単位
     */
    List<CorresponGroupUser> findByEmpNo(String projectId, String empNo, String sortColumn);

    /**
     * 部門情報IDを指定して活動単位を取得する.
     * @param disciplineId 部門情報ID
     * @return 活動単位
     */
    List<CorresponGroup> findByDisciplineId(Long disciplineId);

    /**
     * 活動単位の件数を取得する.
     * @param condition 検索条件
     * @return 活動単位件数
     */
    int countCorresponGroup(SearchCorresponGroupCondition condition);
}
