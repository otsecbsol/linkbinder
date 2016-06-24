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

import java.util.List;
import java.util.Map;

import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.linkbinder.dto.LoginUserInfo;
import jp.co.opentone.bsol.linkbinder.dto.Project;
import jp.co.opentone.bsol.linkbinder.dto.ProjectUser;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchUserCondition;
import jp.co.opentone.bsol.linkbinder.view.IllegalUserLoginException;
import jp.co.opentone.bsol.linkbinder.view.common.module.redirect.RedirectProcessException;
import jp.co.opentone.bsol.linkbinder.view.common.module.redirect.RedirectProcessException.ErrorCode;

/**
 * リダイレクト先の画面用にProjectUser情報を準備するクラス.
 * <p>
 * セッションに格納されるユーザID,プロジェクトIDで対応するProjectUser情報を検索し、
 * Constants.KEY_PROJECTをキーにして検索結果のProjectUser情報をセッションに格納する。
 * @author opentone
 */
public class ProjectUserPreparer extends RedirectDataPreparerBase {
    /**
     * カレントプロジェクト情報のプロジェクトIDと、カレントユーザの従業員番号で ProjectUserを検索する.
     * @param parameterMap 必要データを取得するためのキー情報格納マップ
     * @return 検索結果のProjectUser
     * @throws RedirectProcessException 検索結果が0件の場合
     */
    @Override
    protected Object find(Map<String, String> parameterMap) throws RedirectProcessException {
        try {
            Project curProject = getCurrentProject();
            User curUser = getCurrentUser();

            SearchUserCondition condition = new SearchUserCondition();
            condition.setProjectId(curProject.getProjectId());
            condition.setEmpNo(curUser.getEmpNo());
            List<ProjectUser> list = module.getUserService().search(condition);
            ProjectUser pu = null;
            if (list.size() > 0) {
                pu = list.get(0);
            } else if (!curUser.isSystemAdmin()) {
                throw new RedirectProcessException(
                    RedirectProcessException.ErrorCode.SPECIFIED_PROJECT_USER_NOT_FOUND);
            }
            LoginUserInfo result = new LoginUserInfo();
            result.setLoginUser(curUser);
            result.setLoginProject(curProject);
            result.setProjectUser(pu);
            return result;
        } catch (ServiceAbortException sae) {
            throw new RedirectProcessException(
                RedirectProcessException.ErrorCode.OTHER_REASON, sae);
        }
    }

    /**
     * 検索オブジェクトをキー:Constants.KEY_PROJECT_USERでセッションに格納する.
     * @param foundObject 格納オブジェクト
     * @throws RedirectProcessException 格納に失敗した場合
     */
    @Override
    protected void store(Object foundObject) throws RedirectProcessException {
        if (foundObject != null) {
            try {
                LoginUserInfo info = (LoginUserInfo) foundObject;
                module.getBasePage().getLoginUserInfoHolder().addLoginProject(info);
            } catch (IllegalUserLoginException e) {
                throw new RedirectProcessException(ErrorCode.OTHER_REASON);
            }
        }
    }

}
