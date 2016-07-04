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

import java.util.List;

import org.springframework.stereotype.Repository;

import jp.co.opentone.bsol.linkbinder.dao.AbstractDao;
import jp.co.opentone.bsol.linkbinder.dao.ProjectUserProfileDao;
import jp.co.opentone.bsol.linkbinder.dto.ProjectUserProfile;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchProjectUserProfileCondition;

/**
 * project_user_profile を操作するDao.
 *
 * @author opentone
 *
 */
@Repository
public class ProjectUserProfileDaoImpl extends AbstractDao<ProjectUserProfile> implements
        ProjectUserProfileDao {
    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -8737897486918546334L;
    /**
     * このクラスが利用するsqlMap.xmlのnamespace.
     */
    private static final String NAMESPACE = "projectUserProfile";

    /**
     * SQLID: 従業員番号を指定してユーザーを取得する.
     */
    private static final String SQL_FIND = "find";

    /**
     * 空のインスタンスを生成する.
     */
    public ProjectUserProfileDaoImpl() {
        super(NAMESPACE);
    }

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.dao.ProjectUserProfileDao
     *     #find(jp.co.opentone.bsol.linkbinder.dto.condition.SearchProjectUserProfileCondition)
     */
    public ProjectUserProfile find(SearchProjectUserProfileCondition condition) {
        return (ProjectUserProfile) getSqlMapClientTemplate()
            .queryForObject(getSqlId(SQL_FIND), condition);
    }

    /*
     * (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.dao.ProjectUserProfileDao
     *     #findList(jp.co.opentone.bsol.linkbinder.dto.condition.SearchProjectUserProfileCondition)
     */
    @SuppressWarnings("unchecked")
    public List<ProjectUserProfile> findList(SearchProjectUserProfileCondition condition) {
        return (List<ProjectUserProfile>) getSqlMapClientTemplate()
            .queryForList(getSqlId(SQL_FIND), condition);
    }
}
