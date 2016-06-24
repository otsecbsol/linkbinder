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
package jp.co.opentone.bsol.linkbinder.view.util;

import jp.co.opentone.bsol.framework.core.ProcessContext;
import jp.co.opentone.bsol.framework.core.config.SystemConfig;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.service.UserPermissionHelper;

/**
 * JSPページ内で{@link UserPermissionHelper}を使うためのラッパクラス.
 * @author opentone
 */
public class UserPermissionHelperWrapper {

    /**
     * 委譲先オブジェクト.
     */
    private UserPermissionHelper helper;
    /**
     * ログインユーザー.
     */
    private User login;
    /**
     * 実行コンテキスト初期化フラグ.
     */
    private volatile boolean contextInitialized;

    /**
     * 委譲先のオブジェクトを指定してインスタンス化する.
     * @param helper 委譲先オブジェクト
     * @param login ログインユーザー
     */
    public UserPermissionHelperWrapper(UserPermissionHelper helper, User login) {
        this.helper = helper;
        this.login = login;
    }

    /**
     * プロジェクトの活動単位の中に、指定ユーザーがDisicpline Adminの権限を持つものが含まれる場合はtrueを返す.
     * @see UserPermissionHelper#isAnyGroupAdmin(User, String)
     * @param user ユーザー
     * @param projectId
     *            プロジェクトID
     * @return ログインユーザーがGroup Adminの権限を持つ活動単位が含まれる場合はtrue
     */
    public boolean isAnyGroupAdmin(User user, String projectId) {
        if (!contextInitialized) {
            initContext();
        }
        return helper.isAnyGroupAdmin(user, projectId);
    }

    public void initContext() {
        ProcessContext pc = ProcessContext.getCurrentContext();
        // ユーザーID: DataSourceの選択に必要
        pc.setValue(SystemConfig.KEY_USER_ID, login.getUserId());

        contextInitialized = true;
    }
}
