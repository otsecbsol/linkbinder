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
package jp.co.opentone.bsol.linkbinder.dao.impl;

import org.springframework.stereotype.Repository;

import jp.co.opentone.bsol.linkbinder.dao.AbstractDao;
import jp.co.opentone.bsol.linkbinder.dao.ProjectCompanyDao;
import jp.co.opentone.bsol.linkbinder.dto.ProjectCompany;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCompanyCondition;

/**
 * project_company を操作するDao.
 *
 * @author opentone
 *
 */
@Repository
public class ProjectCompanyDaoImpl extends AbstractDao<ProjectCompany>
       implements ProjectCompanyDao {
    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -8737897486918546334L;
    /**
     * このクラスが利用するsqlMap.xmlのnamespace.
     */
    private static final String NAMESPACE = "projectCompany";

    /**
     * 条件を指定してプロジェクト会社件数を取得する（エラーチェック用）.
     */
    private static final String SQL_ID_COUNT_CHECK = "countCheck";

    /**
     * 空のインスタンスを生成する.
     */
    public ProjectCompanyDaoImpl() {
        super(NAMESPACE);
    }

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.dao.ProjectCompanyDao#countCheck
     * (jp.co.opentone.bsol.linkbinder.dto.condition.SearchCompanyCondition)
     */
    public int countCheck(SearchCompanyCondition condition) {
        return Integer.parseInt(getSqlMapClientTemplate()
            .queryForObject(getSqlId(SQL_ID_COUNT_CHECK), condition).toString());
    }
}
