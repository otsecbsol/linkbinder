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
import jp.co.opentone.bsol.linkbinder.dao.WorkflowTemplateUserDao;
import jp.co.opentone.bsol.linkbinder.dto.WorkflowTemplateUser;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchWorkflowTemplateUserCondition;

/**
 * workflow_template_userを操作するDao.
 *
 * @author opentone
 *
 */
@Repository
public class WorkflowTemplateUserDaoImpl extends AbstractDao<WorkflowTemplateUser> implements
    WorkflowTemplateUserDao {

    /**
     * serialVersionUID.
     */
    private static final long serialVersionUID = 1550217715456661773L;

    /**
     * このクラスが利用するsqlMap.xmlのnamespace.
     */
    private static final String NAMESPACE = "workflowTemplateUser";

    /**
     * SQLID: 従業員番号、プロジェクトIDを指定して承認フローユーザーリストを取得する.
     */
    private static final String SQL_FIND = "find";

    /**
     * SQLID: 条件を指定して承認フローテンプレートユーザーの件数を取得する（エラーチェック用）.
     */
    private static final String SQL_COUNT_TEMPLATE_USER_CHECK = "countTemplateUserCheck";

    /**
     * 空のインスタンスを生成する.
     */
    public WorkflowTemplateUserDaoImpl() {
        super(NAMESPACE);
    }

    /*
     * (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.dao.WorkflowTemplateUserDao#findByEmpNoProjectId(java
     * .lang.String, java.lang.String)
     */
    @SuppressWarnings("unchecked")
    public List<WorkflowTemplateUser> find(SearchWorkflowTemplateUserCondition condition) {
        return getSqlMapClientTemplate().queryForList(getSqlId(SQL_FIND),
            condition);
    }

    /*
     * (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.dao.WorkflowTemplateUserDao#countTemplateUserCheck(
     *      jp.co.opentone.bsol.linkbinder.dto.condition.SearchWorkflowTemplateUserCondition)
     *
     */
    public int countTemplateUserCheck(SearchWorkflowTemplateUserCondition condition) {
        return Integer.parseInt(getSqlMapClientTemplate()
            .queryForObject(getSqlId(SQL_COUNT_TEMPLATE_USER_CHECK), condition).toString());
    }

}
