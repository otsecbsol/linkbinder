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
package jp.co.opentone.bsol.linkbinder.dto.condition;

import jp.co.opentone.bsol.linkbinder.dto.AbstractDto;
import jp.co.opentone.bsol.linkbinder.dto.Correspon;
import jp.co.opentone.bsol.linkbinder.dto.code.EmailNoticeReceivable;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowProcessStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowType;

/**
 * ワークフローの検索条件.
 *
 * @author opentone
 *
 */
public class SearchWorkflowCondition extends AbstractDto {

    /**
     *
     */
    private static final long serialVersionUID = -8251365444175532452L;

    /**
     * v_correspon のDTO.
     */
    private Correspon correspon;

    /**
     * 検証者種別.
     */
    private WorkflowType workflowType;

    /**
     * @return the correspon
     */
    public Correspon getCorrespon() {
        return correspon;
    }

    /**
     * @param correspon the correspon to set
     */
    public void setCorrespon(Correspon correspon) {
        this.correspon = correspon;
    }

    /**
     * @return the workflowType
     */
    public WorkflowType getWorkflowType() {
        return workflowType;
    }

    /**
     * @param workflowType the workflowType to set
     */
    public void setWorkflowType(WorkflowType workflowType) {
        this.workflowType = workflowType;
    }

    /**
     * 承認作業状態：検証依頼中を取得します.
     * @return 承認作業状態：検証依頼中
     */
    public WorkflowProcessStatus getRequestForCheck() {
        return WorkflowProcessStatus.REQUEST_FOR_CHECK;
    }

    /**
     * 承認作業状態：承認依頼中を取得します.
     * @return 承認作業状態：承認依頼中
     */
    public WorkflowProcessStatus getRequestForApproval() {
        return WorkflowProcessStatus.REQUEST_FOR_APPROVAL;
    }

    /**
     * 承認作業状態：コレポン文書更新済を取得します.
     * @return 承認作業状態：コレポン文書更新済
     */
    public WorkflowProcessStatus getUnderConsideration() {
        return WorkflowProcessStatus.UNDER_CONSIDERATION;
    }

    /**
     * メール通知受信要否：Noを取得します.
     * @return メール通知受信要否：No
     */
    public EmailNoticeReceivable getEmailNoticeReceivableNo() {
        return EmailNoticeReceivable.NO;
    }
}
