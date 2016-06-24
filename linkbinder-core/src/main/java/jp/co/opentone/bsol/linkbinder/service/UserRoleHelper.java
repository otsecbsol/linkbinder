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
package jp.co.opentone.bsol.linkbinder.service;

import java.io.Serializable;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.co.opentone.bsol.linkbinder.dto.Correspon;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.Workflow;

/**
 * このクラスではユーザの承認権限チェック処理に関する処理を提供する.
 * @author opentone
 */
@Service
@Transactional(readOnly = true)
public class UserRoleHelper implements Serializable {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 1975832431824192712L;

    /**
     * 指定のユーザーがPreparerか判定する.
     * @param correspon
     *            コレポン文書
     * @param user
     *            ユーザー
     * @return boolean Preparerならtrue / Preparerではなかったらfalse
     */
    public boolean isWorkflowPreparer(Correspon correspon, User user) {
        User createdBy = correspon.getCreatedBy();
        if (createdBy == null) {
            return false;
        }

        return createdBy.getEmpNo().equals(user.getEmpNo());
    }

    /**
     * 指定のユーザーがCheckerか判定する.
     * @param correspon
     *            コレポン文書
     * @param user
     *            ユーザー
     * @return boolean Checkerならtrue / Checkerではなかったらfalse
     */
    public boolean isWorkflowChecker(Correspon correspon, User user) {
        List<Workflow> workflow = correspon.getWorkflows();
        return workflow == null ? false : isWorkflowChecker(workflow, user);
    }

    /**
     * 指定のユーザーがApproverか判定する.
     * @param correspon
     *            コレポン文書
     * @param user
     *            ユーザー
     * @return boolean Checkerならtrue / Checkerではなかったらfalse
     */
    public boolean isWorkflowApprover(Correspon correspon, User user) {
        List<Workflow> workflow = correspon.getWorkflows();
        return workflow == null ? false : isWorkflowApprover(workflow, user);
    }

    /**
     * 指定のユーザーがCheckerか判定する.
     * @param workflow 承認フロー
     * @param user ユーザー
     * @return Checkerならtrue / Checkerではないfalse
     */
    public boolean isWorkflowChecker(List<Workflow> workflow, User user) {
        for (Workflow wf : workflow) {
            if (wf.isChecker()) {
                if (wf.getUser().getEmpNo().equals(user.getEmpNo())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 指定のユーザーがApproverか判定する.
     * @param workflow
     *            承認フロー
     * @param user
     *            ユーザー
     * @return boolean Checkerならtrue / Checkerではなかったらfalse
     */
    public boolean isWorkflowApprover(List<Workflow> workflow, User user) {
        for (Workflow wf : workflow) {
            if (wf.isApprover()) {
                if (wf.getUser().getEmpNo().equals(user.getEmpNo())) {
                    return true;
                }
            }
        }
        return false;
    }
}
