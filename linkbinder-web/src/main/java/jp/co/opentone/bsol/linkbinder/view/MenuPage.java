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

import java.util.Map;

import javax.annotation.ManagedBean;
import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;

import jp.co.opentone.bsol.linkbinder.dto.Project;
import jp.co.opentone.bsol.linkbinder.dto.ProjectUser;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.service.UserPermissionHelper;
import jp.co.opentone.bsol.linkbinder.view.util.UserPermissionHelperWrapper;

/**
 * メニューヘッダの情報を表す.
 * @author opentone
 */
@ManagedBean
@Scope("view")
public class MenuPage extends AbstractPage {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 6633973828942219642L;

    /**
     * ユーザー権限の判定を行うオブジェクト.
     */
    @Resource
    private UserPermissionHelper permissionHelper;

    private UserPermissionHelperWrapper getHelperWrapper() {
        UserPermissionHelperWrapper result =
            new UserPermissionHelperWrapper(permissionHelper, getCurrentUser());
        return result;
    }

    /**
     * システム管理者メニューにアクセス可能な場合はtrue.
     * @return 判定結果
     */
    public boolean isSystemAdminAccessible() {
        User u = getCurrentUser();
        return u != null && u.isSystemAdmin();
    }

    /**
     * プロジェクト管理者メニューにアクセス可能な場合はtrue.
     * @return 判定結果
     */
    public boolean isProjectAdminAccessible() {
        User u = getCurrentUser();
        Project p = getCurrentProject();
        if (p == null) {
            //  プロジェクト未選択の場合はアクセス不可
            return false;
        }

        ProjectUser pu = getCurrentProjectUser();

        return getHelperWrapper().isAnyGroupAdmin(u, p.getProjectId())
            || (pu != null && pu.isProjectAdmin())
            || (u != null && u.isSystemAdmin());
    }

    /**
     * 現在のリクエストがコレポン新規登録中の操作の場合はtrue.
     * @return 判定結果
     */
    public boolean isNewCorresponEdit() {
        Map<String, String> params = viewHelper.getExternalContext().getRequestParameterMap();
        return "1".equals(params.get("newEdit")) || "1".equals(params.get("form:newEdit"));
    }
}
