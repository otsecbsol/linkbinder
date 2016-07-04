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

import jp.co.opentone.bsol.framework.core.dao.RecordNotFoundException;
import jp.co.opentone.bsol.linkbinder.dao.AbstractDao;
import jp.co.opentone.bsol.linkbinder.dao.ProjectCustomFieldDao;
import jp.co.opentone.bsol.linkbinder.dto.ProjectCustomField;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCustomFieldCondition;

/**
 * project_custom_field を操作するDao.
 *
 * @author opentone
 *
 */
@Repository
public class ProjectCustomFieldDaoImpl extends AbstractDao<ProjectCustomField> implements
    ProjectCustomFieldDao {
    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -8737897486918546334L;
    /**
     * このクラスが利用するsqlMap.xmlのnamespace.
     */
    private static final String NAMESPACE = "projectCustomField";

    /**
     * SQLID: 条件を指定してプロジェクトカスタムフィールド件数を取得する.
     */
    private static final String SQL_COUNT_CHECK = "countCheck";
    /**
     * SQLID: 条件を指定してIDを取得する.
     */
    private static final String SQL_FIND_ID_BY_PROJECT_ID_NO = "findIdByProjectIdNo";
    /**
     * SQLID: 条件を指定してPROJECT_CUSTOM_FIELD_IDを取得する.
     */
    private static final String SQL_FIND_PROJECT_CUSTOM_FIELD_ID_BY_PROJECT_ID_NO
        = "findProjectCustomFieldIdByProjectIdNo";

    /**
     * 空のインスタンスを生成する.
     */
    public ProjectCustomFieldDaoImpl() {
        super(NAMESPACE);
    }

    /*
     * (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.dao.ProjectCustomFieldDao#countCheck(jp.co.opentone.bsol.linkbinder
     * .dto.condition.SearchCustomFieldCondition)
     */
    public int countCheck(SearchCustomFieldCondition condition) {
        return Integer.parseInt(getSqlMapClientTemplate().queryForObject(getSqlId(SQL_COUNT_CHECK),
            condition).toString());
    }

    /* (non-Javadoc)
     */
    public Long findIdByProjectIdNo(SearchCustomFieldCondition condition)
        throws RecordNotFoundException {
        Object obj =
            getSqlMapClientTemplate().queryForObject(
                getSqlId(SQL_FIND_ID_BY_PROJECT_ID_NO), condition);

        if (obj == null || Long.parseLong(obj.toString()) <= 0) {
            throw new RecordNotFoundException(condition.getProjectId() + " " + condition.getNo());
        }
        return Long.parseLong(obj.toString());
    }

    /* (non-Javadoc)
     */
    public Long findProjectCustomFieldIdByProjectIdNo(SearchCustomFieldCondition condition)
        throws RecordNotFoundException {
        Object obj =
            getSqlMapClientTemplate().queryForObject(
                getSqlId(SQL_FIND_PROJECT_CUSTOM_FIELD_ID_BY_PROJECT_ID_NO), condition);

        if (obj == null || Long.parseLong(obj.toString()) <= 0) {
            throw new RecordNotFoundException(condition.getProjectId() + " " + condition.getNo());
        }
        return Long.parseLong(obj.toString());
    }
}
