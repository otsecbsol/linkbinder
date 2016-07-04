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
package jp.co.opentone.bsol.linkbinder.dao.mock;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import jp.co.opentone.bsol.framework.core.dao.KeyDuplicateException;
import jp.co.opentone.bsol.framework.core.dao.StaleRecordException;
import jp.co.opentone.bsol.linkbinder.dao.AbstractDao;
import jp.co.opentone.bsol.linkbinder.dao.WorkflowDao;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.Workflow;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowProcessStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowType;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchWorkflowCondition;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

@Repository
public class WorkflowDaoMock extends AbstractDao<Workflow> implements WorkflowDao {

    public WorkflowDaoMock() {
        super("mock");
    }

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public List<Workflow> findByCorresponId(Long corresponId) {
        Workflow w;
        User u;
        List<Workflow> wfs = new ArrayList<Workflow>();

        w = new Workflow();
        w.setId(new Long(10));
        w.setWorkflowNo(new Long(1));

        u = new User();
        u.setEmpNo("80001");
        u.setNameE("Checker1 User");
        w.setUser(u);
        w.setCreatedBy(u);
        w.setUpdatedBy(u);

        w.setWorkflowType(WorkflowType.CHECKER);
        w.setWorkflowProcessStatus(WorkflowProcessStatus.CHECKED);
        wfs.add(w);

        w = new Workflow();
        w.setId(new Long(20));
        w.setWorkflowNo(new Long(2));

        u = new User();
        u.setEmpNo("80002");
        u.setNameE("Checker2 User");
        w.setUser(u);
        w.setCreatedBy(u);
        w.setUpdatedBy(u);

        w.setWorkflowType(WorkflowType.CHECKER);
        w.setWorkflowProcessStatus(WorkflowProcessStatus.CHECKED);

        u = new User();
        u.setEmpNo("80002");
        u.setNameE("Checker2 User");
        w.setFinishedBy(u);

        w.setFinishedAt(new GregorianCalendar(2009, 3, 1, 12, 34, 56).getTime());
        wfs.add(w);

        w = new Workflow();
        w.setId(new Long(30));
        w.setWorkflowNo(new Long(3));

        u = new User();
        u.setEmpNo("80003");
        u.setNameE("Approver User");
        w.setUser(u);
        w.setCreatedBy(u);
        w.setUpdatedBy(u);

        w.setWorkflowType(WorkflowType.APPROVER);
        w.setWorkflowProcessStatus(WorkflowProcessStatus.NONE);
        wfs.add(w);

        return wfs;
    }

    public List<Workflow> findSendApplyUser(SearchWorkflowCondition condition) {
        return new ArrayList<Workflow>();
    }

    public Integer deleteByCorresponId(Long corresponId, User updateUser) throws KeyDuplicateException,
        StaleRecordException {
        System.out.println("削除処理完了：" + corresponId);
        return 1;
    }

    public Integer deleteByCorresponIdWorkflowNo(Long corresponId, Long workflowNo, User updateUser) throws KeyDuplicateException,
    StaleRecordException {
    System.out.println("削除処理完了：" + corresponId + "," + workflowNo);
    return 1;
}

    /**
     * ワークフローを登録する
     */
    @Override
    public Long create(Workflow entity) throws KeyDuplicateException {

        System.out.println("ID:" + entity.getId());
        System.out.println("コレポンID:" + entity.getCorresponId());
        System.out.println("EmpNo:" + entity.getUser().getEmpNo());
        System.out.println("検証者種別:" + entity.getWorkflowType());
        System.out.println("ワークフロー番号：" + entity.getWorkflowNo());
        System.out.println("承認作業状態：" + entity.getWorkflowProcessStatus());
        System.out.println("バージョンナンバー：" + entity.getVersionNo());

        return (long) 12;
    }

    @Override
    public Integer update(Workflow entity) throws KeyDuplicateException, StaleRecordException {

        // デバッグ
        System.out.println("承認フローアップデート処理開始");

        return update(entity, SQL_ID_UPDATE);
    }

    private Integer update(Workflow dto, String sqlId) throws KeyDuplicateException,
        StaleRecordException {

        System.out.println("承認フロー承認状態：" + dto.getWorkflowProcessStatus().getLabel());
        System.out.println("---------承認フローアップデート完了---------");

        // checkVersionNo(dto);
        try {
            // return getSqlMapClientTemplate().update(getSqlId(sqlId), dto);
            return 1;
        } catch (DataIntegrityViolationException e) {
            // KeyDuplicateException ex =
            // new KeyDuplicateException(
            // "primary key or foreign key duplicate.", dto);
            // ex.initCause(e);
            // throw ex;
            throw e;
        }
    }

    public Integer updateByCorresponId(Workflow workflow) {
        return 0;
    }

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.dao.WorkflowDao#updateWorkflowProcessStatus(jp.co.opentone.bsol.linkbinder.dto.Workflow, jp.co.opentone.bsol.linkbinder.dto.code.WorkflowProcessStatus)
     */
    public Integer updateWorkflowProcessStatusById(Workflow workflow,
        WorkflowProcessStatus currentStatus) throws KeyDuplicateException, StaleRecordException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.dao.WorkflowDao#updateWorkflowProcessStatuses(jp.co.opentone.bsol.linkbinder.dto.Workflow, jp.co.opentone.bsol.linkbinder.dto.code.WorkflowProcessStatus)
     */
    public Integer updateWorkflowProcessStatusesByCorresponIdAndWorkflowType(Workflow workflow,
        WorkflowProcessStatus currentStatus) throws KeyDuplicateException, StaleRecordException {
        // TODO Auto-generated method stub
        return null;
    }
}
