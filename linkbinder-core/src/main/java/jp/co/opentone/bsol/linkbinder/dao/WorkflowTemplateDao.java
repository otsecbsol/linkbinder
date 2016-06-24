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
import jp.co.opentone.bsol.linkbinder.dto.WorkflowTemplate;

/**
 * workflowTemplateを操作するDao.
 *
 * @author opentone
 *
 */
public interface WorkflowTemplateDao extends GenericDao<WorkflowTemplate> {

    /**
     * 承認フローテンプレートユーザーIDを指定して承認フローテンプレートを取得する.
     * @param workflowTemplateUserId 承認フローテンプレートユーザーID
     * @return 承認フローテンプレート
     */
    List<WorkflowTemplate> findByWorkflowTemplateUserId(Long workflowTemplateUserId);

    /**
     * 承認フローテンプレートユーザーIDを指定して承認フローテンプレートを削除する.
     * @param workflowTemplate 承認フローテンプレート
     * @return 削除した件数
     * @throws KeyDuplicateException 削除失敗
     * @throws StaleRecordException 削除失敗
     */
    Integer deleteByWorkflowTemplateUserId(WorkflowTemplate workflowTemplate)
            throws KeyDuplicateException, StaleRecordException;

}
