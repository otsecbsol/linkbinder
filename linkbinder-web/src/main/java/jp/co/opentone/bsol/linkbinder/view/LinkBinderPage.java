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

import jp.co.opentone.bsol.framework.web.view.Page;
import jp.co.opentone.bsol.linkbinder.dto.Correspon;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.service.UserRoleHelper;

/**
 * 1画面を構成するコンポーネント.
 * @author opentone
 */
public interface LinkBinderPage extends Page {

    /**
     * System Adminの場合はtrue.
     * @return System Adminの場合はtrue
     */
    boolean isSystemAdmin();

    /**
     * 現在選択中のプロジェクトIDを返す.
     * @return プロジェクトID
     */
    String getCurrentProjectId();

    /**
     * ログインユーザーが指定されたプロジェクトのProject Adminである場合はtrue.
     * @param currentProjectId プロジェクトID
     * @return Project Adminの場合はtrue
     */
    boolean isProjectAdmin(String currentProjectId);

    /**
     * ログインユーザーが指定されたコレポン文書のGroup Adminである場合はtrue.
     * @param correspon コレポン文書
     * @return Group Adminの場合はtrue
     */
    boolean isAnyGroupAdmin(Correspon correspon);

    /**
     * ログインユーザーを返す.
     * @return ログインユーザー
     */
    User getCurrentUser();

    /**
     * ユーザー権限に関する処理のヘルパーを返す.
     * @return ヘルパーオブジェクト
     */
    UserRoleHelper getUserRoleHelper();

}
