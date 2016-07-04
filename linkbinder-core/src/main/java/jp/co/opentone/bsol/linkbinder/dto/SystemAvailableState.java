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



/**
 * 他システムの利用可能状態を保持する.
 *
 * @author opentone
 *
 */
public class SystemAvailableState extends AbstractDto {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -5610369171093105495L;

    /**
     * システムコード.
     */
    private String systemCode;

    /**
     * プロジェクトID.
     */
    private String projectId;

    /**
     * システム利用権限有無.
     */
    private boolean available;

    /**
     * 指定のパラメータを初期値にしてインスタンス生成.
     * @param systemCode システムコード
     * @param projectId プロジェクトID
     * @param available システム利用権限有無
     * @return システム利用可能状態オブジェクト
     */
    public static SystemAvailableState createInstance(
                String systemCode, String projectId, boolean available) {
        SystemAvailableState state = new SystemAvailableState();
        state.setSystemCode(systemCode);
        state.setProjectId(projectId);
        state.setAvailable(available);
        return state;
    }

    /**
     * @return the systemCode
     */
    public String getSystemCode() {
        return systemCode;
    }

    /**
     * @param systemCode the systemCode to set
     */
    public void setSystemCode(String systemCode) {
        this.systemCode = systemCode;
    }

    /**
     * @return the projectId
     */
    public String getProjectId() {
        return projectId;
    }

    /**
     * @param projectId the projectId to set
     */
    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    /**
     * @return the available
     */
    public boolean isAvailable() {
        return available;
    }

    /**
     * @param available the available to set
     */
    public void setAvailable(boolean available) {
        this.available = available;
    }

}
