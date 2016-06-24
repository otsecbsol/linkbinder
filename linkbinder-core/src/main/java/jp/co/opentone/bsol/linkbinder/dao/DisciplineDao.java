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
import jp.co.opentone.bsol.linkbinder.dto.Discipline;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchDisciplineCondition;

/**
 * discipline を操作するDao.
 *
 * @author opentone
 *
 */
public interface DisciplineDao extends GenericDao<Discipline> {

    /**
     * 部門情報の件数を取得する.
     * @param condition 検索条件
     * @return int 検索件数
     */
    int count(SearchDisciplineCondition condition);

    /**
     * 検索条件に該当する部門情報を検索する.
     * @param condition 検索条件
     * @return 部門情報
     */
    List<Discipline> find(SearchDisciplineCondition condition);

    /**
     * 指定した拠点に登録されていない部門情報を検索する.
     * @param projectId プロジェクトId
     * @param siteId 拠点Id
     * @return 部門情報
     */
    List<Discipline> findNotExistCorresponGroup(String projectId, Long siteId);

    /**
     * 条件を指定して部門情報件数を取得する(エラーチェック用).
     * @param condition 検索条件
     * @return 部門情報件数
     */
    int countCheck(SearchDisciplineCondition condition);

}
