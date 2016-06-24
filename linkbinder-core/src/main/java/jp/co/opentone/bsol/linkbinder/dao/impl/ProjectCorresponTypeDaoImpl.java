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
import jp.co.opentone.bsol.linkbinder.dao.ProjectCorresponTypeDao;
import jp.co.opentone.bsol.linkbinder.dto.ProjectCorresponType;

/**
 * project_correspon_type を操作するDao.
 *
 * @author opentone
 *
 */
@Repository
public class ProjectCorresponTypeDaoImpl extends AbstractDao<ProjectCorresponType> implements
    ProjectCorresponTypeDao {
    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -8737897486918546334L;
    /**
     * このクラスが利用するsqlMap.xmlのnamespace.
     */
    private static final String NAMESPACE = "projectCorresponType";

    /**
     * SQLID:コレポン文書種別IDとプロジェクトIDを指定して件数を取得.
     */
    private static final String SQL_COUNT_BY_CORRESPON_TYPE_ID_PROJECT_ID =
            "countByCorresponTypeIdProjectId";

    /**
     * 空のインスタンスを生成する.
     */
    public ProjectCorresponTypeDaoImpl() {
        super(NAMESPACE);
    }

    /*
     * (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.dao.ProjectCorresponTypeDao#countByCorresponTypeIdProjectId
     * (java.lang.Long, java.lang.String)
     */
    public int countByCorresponTypeIdProjectId(Long corresponTypeId, String projectId) {
        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("corresponTypeId", corresponTypeId);
        condition.put("projectId", projectId);

        return Integer.parseInt(getSqlMapClientTemplate()
            .queryForObject(getSqlId(SQL_COUNT_BY_CORRESPON_TYPE_ID_PROJECT_ID), condition)
            .toString());
    }
}
