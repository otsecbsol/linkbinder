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

import org.hibernate.validator.constraints.Length;

import jp.co.opentone.bsol.framework.core.dao.LegacyEntity;
import jp.co.opentone.bsol.framework.core.validation.constraints.Alphanumeric;
import jp.co.opentone.bsol.framework.core.validation.constraints.Required;
import jp.co.opentone.bsol.linkbinder.dto.code.MasterDataImportResultStatus;


/**
 * テーブル [v_project] の1レコードを表すDto.
 *
 * @author opentone
 *
 */
public class Project extends AbstractDto implements LegacyEntity {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -9070841956393952458L;

    /**
     * Project.
     * <p>
     * [v_project.project_id]
     * </p>
     */
    @Required
    @Length(max=11)
    @Alphanumeric
    private String projectId;

    /**
     * Client name e.
     * <p>
     * [v_project.client_name_e]
     * </p>
     */
    @Length(max = 30)
    @Alphanumeric
    private String clientNameE;

    /**
     * Client name j.
     * <p>
     * [v_project.client_name_j]
     * </p>
     */
    @Length(max = 30)
    private String clientNameJ;

    /**
     * Name e.
     * <p>
     * [v_project.name_e]
     * </p>
     */
    @Length(max = 40)
    @Alphanumeric
    private String nameE;

    /**
     * Name j.
     * <p>
     * [v_project.name_j]
     * </p>
     */
    @Length(max = 40)
    private String nameJ;

    /**
     * Use approved flg.
     * <p>
     * [v_project.use_approved_flg]
     * </p>
     */
    @Length(max = 1)
    @Alphanumeric
    private String useApprovedFlg;

    /**
     * Project Custom Setting.
     * <p>
     * [project_custom_setting]
     * </p>
     */
    private ProjectCustomSetting projectCustomSetting;

    /**
     * 取込結果.
     */
    private MasterDataImportResultStatus importResultStatus = MasterDataImportResultStatus.NONE;

    /**
     * 空のインスタンスを生成する.
     */
    public Project() {
    }

    /**
     * projectId の値を返す.
     * <p>
     * [v_project.project_id]
     * </p>
     * @return projectId
     */
    public String getProjectId() {
        return projectId;
    }

    /**
     * projectId の値を設定する.
     * <p>
     * [v_project.project_id]
     * </p>
     * @param projectId
     *            projectId
     */
    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    /**
     * clientNameE の値を返す.
     * <p>
     * [v_project.client_name_e]
     * </p>
     * @return clientNameE
     */
    public String getClientNameE() {
        return clientNameE;
    }

    /**
     * clientNameE の値を設定する.
     * <p>
     * [v_project.client_name_e]
     * </p>
     * @param clientNameE
     *            clientNameE
     */
    public void setClientNameE(String clientNameE) {
        this.clientNameE = clientNameE;
    }

    /**
     * clientNameJ の値を返す.
     * <p>
     * [v_project.client_name_j]
     * </p>
     * @return clientNameJ
     */
    public String getClientNameJ() {
        return clientNameJ;
    }

    /**
     * clientNameJ の値を設定する.
     * <p>
     * [v_project.client_name_j]
     * </p>
     * @param clientNameJ
     *            clientNameJ
     */
    public void setClientNameJ(String clientNameJ) {
        this.clientNameJ = clientNameJ;
    }

    /**
     * nameE の値を返す.
     * <p>
     * [v_project.name_e]
     * </p>
     * @return nameE
     */
    public String getNameE() {
        return nameE;
    }

    /**
     * nameE の値を設定する.
     * <p>
     * [v_project.name_e]
     * </p>
     * @param nameE
     *            nameE
     */
    public void setNameE(String nameE) {
        this.nameE = nameE;
    }

    /**
     * nameJ の値を返す.
     * <p>
     * [v_project.name_j]
     * </p>
     * @return nameJ
     */
    public String getNameJ() {
        return nameJ;
    }

    /**
     * nameJ の値を設定する.
     * <p>
     * [v_project.name_j]
     * </p>
     * @param nameJ
     *            nameJ
     */
    public void setNameJ(String nameJ) {
        this.nameJ = nameJ;
    }


    /**
     * useApprovedFlg の値を返す.
     * <p>
     * [v_project.use_approved_flg]
     * </p>
     * @return useApprovedFlg
     */
    public String getUseApprovedFlg() {
        return useApprovedFlg;
    }

    /**
     * useApprovedFlg の値を設定する.
     * <p>
     * [v_project.use_approved_flg]
     * </p>
     * @param useApprovedFlg
     *            useApprovedFlg
     */
    public void setUseApprovedFlg(String useApprovedFlg) {
        this.useApprovedFlg = useApprovedFlg;
    }

    /**
     * @param projectCustomSetting the projectCustomSetting to set
     */
    public void setProjectCustomSetting(ProjectCustomSetting projectCustomSetting) {
        this.projectCustomSetting = projectCustomSetting;
    }

    /**
     * @return the projectCustomSetting
     */
    public ProjectCustomSetting getProjectCustomSetting() {
        return projectCustomSetting;
    }

    public MasterDataImportResultStatus getImportResultStatus() {
        return importResultStatus;
    }

    public void setImportResultStatus(MasterDataImportResultStatus importResultStatus) {
        this.importResultStatus = importResultStatus;
    }
}
