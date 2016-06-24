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
package jp.co.opentone.bsol.linkbinder.dto;

import java.util.HashMap;
import java.util.Map;

/**
 * 現在処理中のログインユーザー情報を保持するDto.
 *
 * @author opentone
 *
 */
public class LoginUserInfo extends AbstractDto {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 2291533279595370724L;

    /** ログインユーザー. */
    private User loginUser;

    /** 選択中プロジェクト. */
    private Project loginProject;

    /** 選択中プロジェクトユーザー. */
    private ProjectUser projectUser;

    /** プロジェクト毎の情報格納コンテナ. */
    private Map<String, Object> values = new HashMap<String, Object>();

    /**
     * @param loginUser the loginUser to set
     */
    public void setLoginUser(User loginUser) {
        this.loginUser = loginUser;
    }

    /**
     * @return the loginUser
     */
    public User getLoginUser() {
        return loginUser;
    }

    /**
     * @param loginProject the loginProject to set
     */
    public void setLoginProject(Project loginProject) {
        this.loginProject = loginProject;
    }

    /**
     * @return the loginProject
     */
    public Project getLoginProject() {
        return loginProject;
    }

    /**
     * @param projectUser the projectUser to set
     */
    public void setProjectUser(ProjectUser projectUser) {
        this.projectUser = projectUser;
    }

    /**
     * @return the projectUser
     */
    public ProjectUser getProjectUser() {
        return projectUser;
    }

    /**
     * プロジェクト毎の情報を格納する.
     * @param key キー
     * @param value 格納する値
     */
    public void setValue(String key, Object value) {
        this.values.put(key, value);
    }

    /**
     * 当オブジェクトに設定済のプロジェクト毎の情報を取得する.
     * @param <T> 戻り値の型
     * @param key キー
     * @return 格納済の値
     */
    @SuppressWarnings("unchecked")
    public <T> T getValue(String key) {
        return (T) this.values.get(key);
    }
}
