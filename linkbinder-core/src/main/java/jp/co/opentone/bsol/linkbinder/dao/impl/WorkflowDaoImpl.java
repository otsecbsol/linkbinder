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
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import jp.co.opentone.bsol.framework.core.dao.KeyDuplicateException;
import jp.co.opentone.bsol.framework.core.dao.StaleRecordException;
import jp.co.opentone.bsol.linkbinder.dao.AbstractDao;
import jp.co.opentone.bsol.linkbinder.dao.WorkflowDao;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.Workflow;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowProcessStatus;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchWorkflowCondition;

/**
 * workflow を操作するDao.
 *
 * @author opentone
 *
 */
@Repository
public class WorkflowDaoImpl extends AbstractDao<Workflow> implements WorkflowDao {
    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -8737897486918546334L;
    /**
     * このクラスが利用するsqlMap.xmlのnamespace.
     */
    private static final String NAMESPACE = "workflow";

    /**
     * SQLID: コレポン文書を指定して担当者を取得.
     */
    private static final String SQL_FIND_BY_CORRESPON_ID = "findByCorresponId";

    /**
     * SQLID: コレポン文書情報を指定してメール受信を許可している担当者を取得.
     */
    private static final String SQL_FIND_SEND_APPLY_USER = "findSendApplyUser";

    /**
     * SQLID: コレポン文書を指定して削除.
     */
    private static final String SQL_DELETE_BY_CORRESPON_ID = "deleteByCorresponId";

    /**
     * SQLID: コレポン文書IDとワークフローNoを指定して削除.
     */
    private static final String SQL_DELETE_BY_CORRESPON_ID_WORKFLOW_NO =
                                                  "deleteByCorresponIdWorkflowNo";

    /**
     * SQLID: コレポン文書を指定して更新.
     */
    private static final String SQL_UPDATE_BY_CORRESPON_ID = "updateByCorresponId";

    /**
     * SQLID: 複数のワークフローの承認作業状態を更新.
     */
    private static final String
        SQL_UPDATE_WORKFLOW_PROCESS_STATUSES_BY_CORRESPON_ID_AND_WORKFLOW_TYPE =
            "updateWorkflowProcessStatusesByCorresponIdAndWorkflowType";

    /**
     * SQLID: ワークフローの承認作業状態を更新.
     */
    private static final String SQL_UPDATE_WORKFLOW_PROCESS_STATUS_BY_ID =
            "updateWorkflowProcessStatusById";

    /**
     * 空のインスタンスを生成する.
     */
    public WorkflowDaoImpl() {
        super(NAMESPACE);
    }

    /*
     * (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.dao.WorkflowDao#findByCorresponId(java.lang.Long)
     */
    @SuppressWarnings("unchecked")
    public List<Workflow> findByCorresponId(Long corresponId) {
        return getSqlMapClientTemplate().queryForList(getSqlId(SQL_FIND_BY_CORRESPON_ID),
                                                      corresponId);
    }

    /*
     * (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.dao.WorkflowDao#findSendApplyUser(jp.co.opentone.bsol.linkbinder.dto.Correspon)
     */
    @SuppressWarnings("unchecked")
    public List<Workflow> findSendApplyUser(SearchWorkflowCondition condition) {
        return getSqlMapClientTemplate()
                .queryForList(getSqlId(SQL_FIND_SEND_APPLY_USER), condition);
    }

    /*
     * (非 Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.dao.WorkflowDao#updateByCorresponId(jp.co.opentone.bsol.linkbinder
     * .dto.Workflow)
     */
    public Integer updateByCorresponId(Workflow workflow) throws KeyDuplicateException,
        StaleRecordException {
        return getSqlMapClientTemplate().update(getSqlId(SQL_UPDATE_BY_CORRESPON_ID), workflow);
    }

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.dao.WorkflowDao#deleteByCorresponId(
     * java.lang.Long, jp.co.opentone.bsol.linkbinder.dto.User)
     */
    public Integer deleteByCorresponId(Long corresponId, User updateUser)
        throws KeyDuplicateException, StaleRecordException {
        Workflow workflow = new Workflow();
        workflow.setCorresponId(corresponId);
        workflow.setUpdatedBy(updateUser);
        return getSqlMapClientTemplate().update(getSqlId(SQL_DELETE_BY_CORRESPON_ID), workflow);
    }

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.dao.WorkflowDao#deleteByCorresponIdWorkflowNo(
     * java.lang.Long, java.lang.Long, jp.co.opentone.bsol.linkbinder.dto.User)
     */
    public Integer deleteByCorresponIdWorkflowNo(Long corresponId, Long workflowNo, User updateUser)
        throws KeyDuplicateException, StaleRecordException {
        Workflow workflow = new Workflow();
        workflow.setCorresponId(corresponId);
        workflow.setUpdatedBy(updateUser);
        workflow.setWorkflowNo(workflowNo);
        return getSqlMapClientTemplate().update(
                   getSqlId(SQL_DELETE_BY_CORRESPON_ID_WORKFLOW_NO), workflow);
    }

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.dao.WorkflowDao
     *     #updateWorkflowProcessStatus(jp.co.opentone.bsol.linkbinder.dto.Workflow,
     *                                 jp.co.opentone.bsol.linkbinder.dto.code.WorkflowProcessStatus)
     */
    public Integer updateWorkflowProcessStatusById(
        Workflow workflow, WorkflowProcessStatus currentStatus)
            throws KeyDuplicateException, StaleRecordException {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("workflowProcessStatus", workflow.getWorkflowProcessStatus());
        param.put("updatedBy", workflow.getUpdatedBy());
        param.put("versionNo", workflow.getVersionNo());
        param.put("id", workflow.getId());
        param.put("currentWorkflowProcessStatus", currentStatus);

        return getSqlMapClientTemplate()
                    .update(getSqlId(SQL_UPDATE_WORKFLOW_PROCESS_STATUS_BY_ID), param);
    }

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.dao.WorkflowDao
     *     #updateWorkflowProcessStatuses(jp.co.opentone.bsol.linkbinder.dto.Workflow,
     *                                     jp.co.opentone.bsol.linkbinder.dto.code.WorkflowProcessStatus)
     */
    public Integer updateWorkflowProcessStatusesByCorresponIdAndWorkflowType(
        Workflow workflow, WorkflowProcessStatus currentStatus)
            throws KeyDuplicateException, StaleRecordException {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("workflowProcessStatus", workflow.getWorkflowProcessStatus());
        param.put("updatedBy", workflow.getUpdatedBy());
        param.put("versionNo", workflow.getVersionNo());
        param.put("corresponId", workflow.getCorresponId());
        param.put("workflowType", workflow.getWorkflowType());
        param.put("currentWorkflowProcessStatus", currentStatus);

        return getSqlMapClientTemplate().update(
                getSqlId(SQL_UPDATE_WORKFLOW_PROCESS_STATUSES_BY_CORRESPON_ID_AND_WORKFLOW_TYPE),
                param);
    }
}
