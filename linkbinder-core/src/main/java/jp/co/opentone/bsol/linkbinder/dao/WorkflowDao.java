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
package jp.co.opentone.bsol.linkbinder.dao;

import java.util.List;

import jp.co.opentone.bsol.framework.core.dao.GenericDao;
import jp.co.opentone.bsol.framework.core.dao.KeyDuplicateException;
import jp.co.opentone.bsol.framework.core.dao.StaleRecordException;
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
public interface WorkflowDao extends GenericDao<Workflow> {

    /**
     * 指定されたコレポン文書に設定された全ての承認フローを返す.
     * @param corresponId
     *            コレポン文書ID
     * @return 承認フロー
     */
    List<Workflow> findByCorresponId(Long corresponId);

    /**
     * 指定された承認フロー検索条件に設定された全ての承認フローを返す.
     * @param condition
     *            承認フロー検索条件
     * @return 承認フロー
     */
    List<Workflow> findSendApplyUser(SearchWorkflowCondition condition);

    /**
     * コレポン文書IDで、ワークフローを更新する.
     * @param workflow
     *            ワークフローオブジェクト
     * @return 更新した件数
     * @throws KeyDuplicateException
     *             キー重複による例外
     * @throws StaleRecordException
     *             排他チェックによる例外
     */
    Integer updateByCorresponId(Workflow workflow) throws KeyDuplicateException,
        StaleRecordException;

    /**
     * コレポン文書IDで、ワークフローを削除する.
     * @param corresponId
     *            コレポン文書ID
     * @param updateUser
     *            更新者
     * @return 削除した件数
     * @throws KeyDuplicateException
     *             キー重複による例外
     * @throws StaleRecordException
     *             排他チェックによる例外
     */
    Integer deleteByCorresponId(Long corresponId, User updateUser)
    throws KeyDuplicateException, StaleRecordException;

    /**
     * コレポン文書IDで、ワークフローを削除する.
     * @param corresponId
     *            コレポン文書ID
     * @param workflowNo
     *            ワークフロー№
     * @param updateUser
     *            更新者
     * @return 削除した件数
     * @throws KeyDuplicateException
     *             キー重複による例外
     * @throws StaleRecordException
     *             排他チェックによる例外
     */
    Integer deleteByCorresponIdWorkflowNo(Long corresponId, Long workflowNo, User updateUser)
    throws KeyDuplicateException, StaleRecordException;

    /**
     * workflowに設定された条件で、複数のワークフローレコードを更新する.
     * @param workflow 承認フロー
     * @param currentStatus 更新前の承認作業状態. この承認作業状態に一致するレコードのみ更新される.
     * @return 更新件数
     * @throws KeyDuplicateException キー重複による例外
     * @throws StaleRecordException 排他チェックによる例外
     */
    Integer updateWorkflowProcessStatusesByCorresponIdAndWorkflowType(
        Workflow workflow, WorkflowProcessStatus currentStatus)
            throws KeyDuplicateException, StaleRecordException;

    /**
     * workflowに設定された条件で、ワークフローレコードを1件更新する.
     * @param workflow 承認フロー
     * @param currentStatus 更新前の承認作業状態. この承認作業状態に一致するレコードのみ更新される.
     * @return 更新件数
     * @throws KeyDuplicateException キー重複による例外
     * @throws StaleRecordException 排他チェックによる例外
     */
    Integer updateWorkflowProcessStatusById(Workflow workflow, WorkflowProcessStatus currentStatus)
        throws KeyDuplicateException, StaleRecordException;
}
