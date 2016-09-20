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
package jp.co.opentone.bsol.linkbinder.service.correspon;

import jp.co.opentone.bsol.framework.core.service.IService;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.linkbinder.dto.Correspon;
import jp.co.opentone.bsol.linkbinder.dto.IssueToLearningProjectsResult;
import jp.co.opentone.bsol.linkbinder.dto.Workflow;
import jp.co.opentone.bsol.linkbinder.dto.WorkflowTemplate;
import jp.co.opentone.bsol.linkbinder.dto.WorkflowTemplateUser;

import java.util.List;

/**
 * このサービスでは承認フローに関する処理を提供する.
 * @author opentone
 */
public interface CorresponWorkflowService extends IService {

    /**
     * 設定した承認フローを保存する.
     * @param correspon コレポン文書
     * @param workflows ワークフロー
     * @throws ServiceAbortException 保存に失敗したときエラー
     */
    void save(Correspon correspon, List<Workflow> workflows) throws ServiceAbortException;

    /**
     * ログインユーザーが、指定されたコレポン文書を検証済にする.
     * @param correspon コレポン文書
     * @param workflow ワークフロー
     * @throws ServiceAbortException 検証に失敗したときエラー
     */
    void check(Correspon correspon, Workflow workflow) throws ServiceAbortException;

    /**
     * ログインユーザーが、指定されたコレポン文書を承認する.
     * @param correspon コレポン文書
     * @param workflow ワークフロー
     * @result 結果
     * @throws ServiceAbortException 承認に失敗したときエラー
     */
    IssueToLearningProjectsResult approve(Correspon correspon, Workflow workflow) throws ServiceAbortException;

    /**
     * ログインユーザーが、指定されたコレポン文書を否認する.
     * @param correspon コレポン文書
     * @param workflow ワークフロー
     * @throws ServiceAbortException 否認に失敗したときエラー
     */
    void deny(Correspon correspon, Workflow workflow) throws ServiceAbortException;

    /**
     * 承認フローテンプレートユーザーを取得する.
     * @return 承認フローテンプレートユーザー
     */
    List<WorkflowTemplateUser> searchWorkflowTemplateUser();

    /**
     * 承認フローテンプレート情報を設定する.
     * @param workflowTemplateUserId  承認フローテンプレートユーザーID
     * @return 承認フローテンプレートユーザー
     * @throws ServiceAbortException 設定失敗
     */
    List<WorkflowTemplate> apply(Long workflowTemplateUserId) throws ServiceAbortException;

    /**
     * 承認フローテンプレート情報を削除する.
     * @param workflowTemplateUserId 承認フローテンプレートユーザーID
     * @throws ServiceAbortException 削除失敗
     */
    void deleteTemplate(Long workflowTemplateUserId) throws ServiceAbortException;

    /**
     * 承認フローをテンプレートとして保存する.
     * @param name 承認フローテンプレート名
     * @param workflow 承認フロー
     * @param correspon コレポン文書
     * @throws ServiceAbortException 保存失敗
     */
    void saveTemplate(String name, List<Workflow> workflow, Correspon correspon)
            throws ServiceAbortException;

}
