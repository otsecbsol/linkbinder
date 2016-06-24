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
package jp.co.opentone.bsol.linkbinder.service.admin;

import jp.co.opentone.bsol.framework.core.service.IService;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.linkbinder.dto.ProjectCustomSetting;

/**
 * このサービスではプロジェクトカスタム設定情報に関する処理を提供する.
 * @author opentone
 */
public interface ProjectCustomSettingService extends IService {

    /**
     * プロジェクトIDで指定されたプロジェクトカスタム情報を返す.
     * <p>
     * CurrentProjectIdと等しいかの検証は実施する
     * </p>
     * @param projectId プロジェクトID
     * @return プロジェクトカスタム情報
     * @throws ServiceAbortException 検索エラー
     */
    ProjectCustomSetting find(String projectId) throws ServiceAbortException;

    /**
     * プロジェクトIDで指定されたプロジェクトカスタム情報を返す.
     * @param projectId プロジェクトID
     * @param isProjectIdValidate CurrentProjectIdと等しいかの検証
     * @return プロジェクトカスタム情報
     * @throws ServiceAbortException 検索エラー
     */
    ProjectCustomSetting find(String projectId, boolean isProjectIdValidate)
            throws ServiceAbortException;

    /**
     * 入力値を検証する.
     * @param projectCustomSetting プロジェクトカスタム情報
     * @throws ServiceAbortException 検証NG
     */
    void validate(ProjectCustomSetting projectCustomSetting) throws ServiceAbortException;

    /**
     * 指定されたプロジェクトカスタム情報を保存する.
     * @param projectCustomSetting プロジェクトカスタム情報
     * @return プロジェクトカスタム情報のID
     * @throws ServiceAbortException 保存処理失敗
     */
    Long save(ProjectCustomSetting projectCustomSetting) throws ServiceAbortException;
}
