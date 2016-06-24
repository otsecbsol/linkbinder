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
package jp.co.opentone.bsol.linkbinder.view;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.ManagedBean;

import org.springframework.context.annotation.Scope;

import jp.co.opentone.bsol.framework.core.util.ArgumentValidator;
import jp.co.opentone.bsol.linkbinder.Constants;
import jp.co.opentone.bsol.linkbinder.dto.LoginUserInfo;
import jp.co.opentone.bsol.linkbinder.dto.Project;
import jp.co.opentone.bsol.linkbinder.dto.ProjectUser;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponCondition;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchFullTextSearchCorresponCondition;

/**
 * ログイン情報を保持するManagedBean.
 * @author opentone
 */
@ManagedBean
@Scope("session")
public class LoginUserInfoHolder implements Serializable {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 647215058722452868L;
    /**
     * ログインユーザー.
     */
    private User loginUser;

    /**
     * ログインユーザー情報.
     */
    private LoginUserInfo loginUserInfo;

    /**
     * 最後にログインしたProject/ユーザー情報.
     */
    private LoginUserInfo lastLoginProjectUserInfo;

    /**
     * ログイン済みProject情報.
     * <p>キーは、ProjectId</p>
     */
    private Map<String, LoginUserInfo> loginedProjectUserInfoMap
        = new HashMap<String, LoginUserInfo>();

    /**
     * ログインユーザーを取得します.
     * @return ログインユーザー情報.
     */
    public User getLoginUser() {
        return loginUser;
    }

    /**
     * ログインユーザーを設定します.
     * @param loginUser ログインユーザー
     */
    public void setLoginUser(User loginUser) {
        this.loginUser = loginUser;
        if (this.loginUser != null) {
            this.loginUserInfo = new LoginUserInfo();
            this.loginUserInfo.setLoginUser(this.loginUser);
        } else {
            this.loginUserInfo = null;
        }
    }

    /* (非 Javadoc)
     * @see jp.co.opentone.bsol.mer.common.auth.UserHolder#getUserId()
     */
    public String getUserId() {
        return (null != loginUser) ? loginUser.getUserId() : null;
    }

    /**
     * ログイン済みProject/ユーザー情報を1件追加します.
     * @param info Project/ユーザー情報
     * @throws IllegalUserLoginException セッション内の情報と異なるユーザーでログインの場合
     */
    public void addLoginProject(LoginUserInfo info) throws IllegalUserLoginException {
        ArgumentValidator.validateNotNull(info, "info");
        ArgumentValidator.validateNotNull(info.getLoginProject(), "loginProject");
        ArgumentValidator.validateNotNull(info.getLoginUser(), "loginUser");
        if (null != loginUser) {
            // ユーザー情報の一致を確認
            if (!loginUser.getUserId().equals(info.getLoginUser().getUserId())) {
                throw new IllegalUserLoginException();
            }
        } else {
            // 初ログイン時にユーザー情報を保存
            loginUser = info.getLoginUser();
        }
        lastLoginProjectUserInfo = info;

        // ログイン済みProject情報に保持されている検索条件を取り出し、設定する
        info.setValue(Constants.KEY_SEARCH_CORRESPON_CONDITION,
                getSearchCorresponConditionByProjectId(info.getLoginProject().getProjectId()));
        info.setValue(Constants.KEY_SEARCH_FULL_TEXT_SEARCH_CORRESPON_CONDTION,
                getSearchFullTextCorresponConditionByProjectId(
                        info.getLoginProject().getProjectId()));

        loginedProjectUserInfoMap.put(info.getLoginProject().getProjectId(), info);
    }

    /**
     * ログイン済みProject情報から、projectIdの検索条件（一覧）を取得.
     * @param projectId プロジェクトID
     * @return 検索条件
     */
    private SearchCorresponCondition getSearchCorresponConditionByProjectId(String projectId) {
        LoginUserInfo ui = loginedProjectUserInfoMap.get(projectId);
        if (ui != null) {
            Object o = ui.getValue(Constants.KEY_SEARCH_CORRESPON_CONDITION);
            if (o != null && o.getClass() == SearchCorresponCondition.class) {
                return (SearchCorresponCondition) o;
            }
        }
        return null;
    }

    /**
     * ログイン済みProject情報から、projectIdの検索条件（全文）を取得.
     * @param projectId プロジェクトID
     * @return 検索条件
     */
    private SearchFullTextSearchCorresponCondition getSearchFullTextCorresponConditionByProjectId(
            String projectId) {
        LoginUserInfo ui = loginedProjectUserInfoMap.get(projectId);
        if (ui != null) {
            Object o = ui.getValue(Constants.KEY_SEARCH_FULL_TEXT_SEARCH_CORRESPON_CONDTION);
            if (o != null && o.getClass() == SearchFullTextSearchCorresponCondition.class) {
                return (SearchFullTextSearchCorresponCondition) o;
            }
        }
        return null;
    }

    /**
     * 最後にログインしたProject/ユーザー情報を取得します.
     * @return プロジェクトユーザー情報.
     */
    public LoginUserInfo getLastLoginProjectUser() {
        return lastLoginProjectUserInfo;
    }

    /**
     * 指定したProjectIdに対応するログイン済みProject/ユーザー情報を返します.
     * @param projectId ProjectId
     * @return ログイン済みProject/ユーザー情報. そのProjectでログインされていない場合はnull
     */
    public LoginUserInfo getLoginProjectUserInfo(String projectId) {
        ArgumentValidator.validateNotNull(projectId, "projectId");
        return loginedProjectUserInfoMap.get(projectId);
    }

    /**
     * 指定したProjectIdに対応するログイン済みProject情報を返します.
     * @param projectId ProjectId
     * @return ログイン済みProject/ユーザー情報. そのProjectでログインされていない場合はnull
     */
    public Project getLoginProjectInfo(String projectId) {
        LoginUserInfo lpuInfo = getLoginProjectUserInfo(projectId);
        return (null != lpuInfo) ? lpuInfo.getLoginProject() : null;
    }

    /**
     * 指定したProjectIdに対応するログイン済みProject/ユーザー情報を返します.
     * @param projectId ProjectId
     * @return ログイン済みProject/ユーザー情報. そのProjectでログインされていない場合はnull
     */
    public ProjectUser getLoginUserInfo(String projectId) {
        LoginUserInfo lpuInfo = getLoginProjectUserInfo(projectId);
        return (null != lpuInfo) ? lpuInfo.getProjectUser() : null;
    }

    /**
     * ログイン済みProject/ユーザー情報をクリアします.
     */
    public void clearAllLoginProject() {
        loginedProjectUserInfoMap.clear();
    }

    /**
     * @return the loginUserInfo
     */
    public LoginUserInfo getLoginUserInfo() {
        return loginUserInfo;
    }
}
