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



/**
 * project_user_profileの検索条件を表すDto.
 *
 * @author opentone
 *
 */
public class SearchProjectUserProfileCondition extends AbstractCondition {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -4145820367862645285L;

    /**
     * ユーザID.
     */
    private String empNo;


    /**
     * プロジェクトID.
     */
    private String projectId;

    /**
     * Role.
     */
    private String role;

    /**
     * デフォルト活動単位.
     */
    private Long defaultCorresponGroupId;

    /**
     * empNoを取得します.
     * @return the empNo
     */
    public String getEmpNo() {
        return empNo;
    }


    /**
     * empNoを設定します.
     * @param empNo the empNo to set
     */
    public void setEmpNo(String empNo) {
        this.empNo = empNo;
    }


    /**
     * projectIdを取得します.
     * @return the projectId
     */
    public String getProjectId() {
        return projectId;
    }


    /**
     * projectIdを設定します.
     * @param projectId the projectId to set
     */
    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }


    /**
     * roleを取得します.
     * @return the role
     */
    public String getRole() {
        return role;
    }


    /**
     * roleを設定します.
     * @param role the role to set
     */
    public void setRole(String role) {
        this.role = role;
    }


    /**
     * defaultCorresponGroupIdを設定します.
     * @param defaultCorresponGroupId the defaultCorresponGroupId to set
     */
    public void setDefaultCorresponGroupId(Long defaultCorresponGroupId) {
        this.defaultCorresponGroupId = defaultCorresponGroupId;
    }


    /**
     * defaultCorresponGroupIdを取得します.
     * @return the defaultCorresponGroupId
     */
    public Long getDefaultCorresponGroupId() {
        return defaultCorresponGroupId;
    }
}
