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


import jp.co.opentone.bsol.framework.core.dao.GenericDao;
import jp.co.opentone.bsol.linkbinder.dto.ProjectCompany;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCompanyCondition;

/**
 * project_company を操作するDao.
 *
 * @author opentone
 *
 */
public interface ProjectCompanyDao extends GenericDao<ProjectCompany> {

    /**
     * 指定した条件でプロジェクト会社情報の件数を取得する（エラーチェック用）.
     * @param condition 検索条件
     * @return プロジェクト会社件数
     */
    int countCheck(SearchCompanyCondition condition);
}
