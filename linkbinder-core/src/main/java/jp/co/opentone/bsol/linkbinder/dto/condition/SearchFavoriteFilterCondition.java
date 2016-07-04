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
package jp.co.opentone.bsol.linkbinder.dto.condition;

import jp.co.opentone.bsol.linkbinder.dto.Project;
import jp.co.opentone.bsol.linkbinder.dto.User;

/**
 * テーブル [favorite filter] の検索条件 を表すDto.
 *
 * @author opentone
 *
 */
public class SearchFavoriteFilterCondition extends AbstractCondition {

    /**
     * serialVersionUID.
     */
    private static final long serialVersionUID = 6446874882045130146L;

    /**
     * ユーザー情報.
     */
    private User user;
    /**
     * プロジェクト情報.
     */
    private Project project;

    /**
     * インスタンス生成.
     * @param project プロジェクト
     * @param user ユーザー
     */
    public SearchFavoriteFilterCondition(Project project, User user) {
        this.project = project;
        this.user = user;
    }

    /**
     * @return the user
     */
    public User getUser() {
        return user;
    }
    /**
     * @param user the user to set
     */
    public void setUser(User user) {
        this.user = user;
    }
    /**
     * @return the project
     */
    public Project getProject() {
        return project;
    }
    /**
     * @param project the project to set
     */
    public void setProject(Project project) {
        this.project = project;
    }

    public String getProjectId() {
        return (project == null) ? null : project.getProjectId();
    }

    public String getEmpNo() {
        return (user == null) ? null : user.getEmpNo();
    }

}
