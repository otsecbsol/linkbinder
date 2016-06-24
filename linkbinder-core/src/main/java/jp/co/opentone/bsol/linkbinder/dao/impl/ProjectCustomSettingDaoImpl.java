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

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

import jp.co.opentone.bsol.linkbinder.dao.AbstractDao;
import jp.co.opentone.bsol.linkbinder.dao.ProjectCustomSettingDao;
import jp.co.opentone.bsol.linkbinder.dto.ProjectCustomSetting;

/**
 * project_custom_setting を操作するDao.
 *
 * @author opentone
 *
 */
@Repository
public class ProjectCustomSettingDaoImpl extends AbstractDao<ProjectCustomSetting>
        implements ProjectCustomSettingDao {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -9071205673153441236L;

    /**
     * このクラスが利用するsqlMap.xmlのnamespace.
     */
    private static final String NAMESPACE = "projectCustomSetting";

    /**
     * SQLID: を取得.
     */
    private static final String SQL_FIND_BY_PROJECT_ID = "findByProjectId";

    /**
     * 空のインスタンスを生成する.
     */
    public ProjectCustomSettingDaoImpl() {
        super(NAMESPACE);
    }

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.dao.ProjectCustomSettingDao#findByProjectId(java.lang.String)
     */
    public ProjectCustomSetting findByProjectId(String projectId) {
        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("projectId", projectId);
        ProjectCustomSetting record =
                (ProjectCustomSetting) getSqlMapClientTemplate().queryForObject(
                    getSqlId(SQL_FIND_BY_PROJECT_ID), condition);
        return record;
    }
}
