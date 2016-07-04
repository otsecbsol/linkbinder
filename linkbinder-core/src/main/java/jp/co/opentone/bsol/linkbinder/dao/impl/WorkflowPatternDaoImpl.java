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
import jp.co.opentone.bsol.linkbinder.dao.WorkflowPatternDao;
import jp.co.opentone.bsol.linkbinder.dto.WorkflowPattern;

/**
 * workflow_pattern を操作するDao.
 *
 * @author opentone
 *
 */
@Repository
public class WorkflowPatternDaoImpl extends AbstractDao<WorkflowPattern> implements
    WorkflowPatternDao {
    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -8737897486918546334L;
    /**
     * このクラスが利用するsqlMap.xmlのnamespace.
     */
    private static final String NAMESPACE = "workflowPattern";

    /**
     * SQLID:全ての承認フローパターンを検索する.
     */
    private static final String SQL_FIND_ALL = "findAll";

    /**
     * 空のインスタンスを生成する.
     */
    public WorkflowPatternDaoImpl() {
        super(NAMESPACE);
    }

    /*
     * (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.dao.WorkflowPatternDao#findAll()
     */
    @SuppressWarnings("unchecked")
    public List<WorkflowPattern> findAll() {
        return (List<WorkflowPattern>) getSqlMapClientTemplate()
            .queryForList(getSqlId(SQL_FIND_ALL));

    }

}
