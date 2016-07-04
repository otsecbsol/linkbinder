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

import jp.co.opentone.bsol.framework.core.dao.KeyDuplicateException;
import jp.co.opentone.bsol.framework.core.dao.StaleRecordException;
import jp.co.opentone.bsol.linkbinder.dao.AbstractDao;
import jp.co.opentone.bsol.linkbinder.dao.WorkflowTemplateDao;
import jp.co.opentone.bsol.linkbinder.dto.WorkflowTemplate;

/**
 * workflow_templateを操作するDao.
 *
 * @author opentone
 *
 */
@Repository
public class WorkflowTemplateDaoImpl extends AbstractDao<WorkflowTemplate> implements
    WorkflowTemplateDao {

    /**
     * serialVersionUID.
     */
    private static final long serialVersionUID = 2984586622137377269L;

    /**
     * このクラスが利用するsqlMap.xmlのnamespace.
     */
    private static final String NAMESPACE = "workflowTemplate";

    /**
     * SQLID: 承認フローテンプレートユーザーIDを指定して、承認フローテンプレートを取得する.
     */
    private static final String SQL_FIND_BY_WORKFLOW_TEMPLATE_USER_ID =
            "findByWorkflowTemplateUserId";

    /**
     * SQLID: 承認フローテンプレートユーザーIDを指定してテンプレートを削除する.
     */
    private static final String SQL_DELETE_BY_WORKFLOW_TEMPLATE_USER_ID =
            "deleteByWorkflowTemplateUserId";

    /**
     * このクラスが利用するsqlMap.xmlのnamespace.
     */
    public WorkflowTemplateDaoImpl() {
        super(NAMESPACE);
    }

    /*
     * (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.dao.WorkflowTemplateDao#findByWorkflowTemplateUserId
     * (java.lang.Long)
     */
    @SuppressWarnings("unchecked")
    public List<WorkflowTemplate> findByWorkflowTemplateUserId(Long workflowTemplateUserId) {
        return getSqlMapClientTemplate()
            .queryForList(getSqlId(SQL_FIND_BY_WORKFLOW_TEMPLATE_USER_ID), workflowTemplateUserId);
    }

    /*
     * (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.dao.WorkflowTemplateDao#deleteByWorkflowTemplateUserId
     * (java.lang.Long, jp.co.opentone.bsol.linkbinder.dto.User)
     */
    public Integer deleteByWorkflowTemplateUserId(WorkflowTemplate workflowTemplate)
            throws KeyDuplicateException, StaleRecordException {
        return getSqlMapClientTemplate().update(getSqlId(SQL_DELETE_BY_WORKFLOW_TEMPLATE_USER_ID),
            workflowTemplate);

    }

}
