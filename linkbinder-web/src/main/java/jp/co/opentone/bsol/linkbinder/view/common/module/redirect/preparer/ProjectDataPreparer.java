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
package jp.co.opentone.bsol.linkbinder.view.common.module.redirect.preparer;

import java.util.Map;

import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.linkbinder.dto.Project;
import jp.co.opentone.bsol.linkbinder.view.common.module.redirect.RedirectProcessException;
import jp.co.opentone.bsol.linkbinder.view.common.module.redirect.RedirectProcessParameterKey;

/**
 * リダイレクト先の画面用にProject情報を準備するクラス.
 * <p>
 * RedirectProcessParameterKey.PROJECT_IDでパラメータ格納される
 * プロジェクトIDで対応するProject情報を検索し、
 * Constants.KEY_PROJECTをキーにして検索結果のProject情報を
 * セッションに格納する
 *
 * @author opentone
 */
public class ProjectDataPreparer extends RedirectDataPreparerBase {
    /**
     * パラメータに格納されるプロジェクトIDで Project情報を検索する.
     * @param parameterMap 必要データを取得するためのキー情報格納パラメータマップ
     * @return 検索結果のProject情報
     * @throws RedirectProcessException 検索結果が該当なしの場合
     */
    @Override
    protected Object find(Map<String, String> parameterMap) throws RedirectProcessException {
        try {
            String projectId = parameterMap.get(RedirectProcessParameterKey.PROJECT_ID);
            Project project = module.getProjectService().find(projectId);
            if (project == null) {
                throw new RedirectProcessException(
                    RedirectProcessException.ErrorCode.SPECIFIED_PROJECT_NOT_FOUND);
            }
            return project;
        } catch (ServiceAbortException sae) {
            throw new RedirectProcessException(
                RedirectProcessException.ErrorCode.OTHER_REASON, sae);
        }
    }

    /**
     * 検索オブジェクトを選択中のプロジェクトとしてセッションに格納する.
     * @param foundObject 格納オブジェクト
     * @throws RedirectProcessException 格納に失敗した場合
     */
    @Override
    protected void store(Object foundObject) throws RedirectProcessException {
        module.setCurrentProjectInfo((Project) foundObject);
    }
}
