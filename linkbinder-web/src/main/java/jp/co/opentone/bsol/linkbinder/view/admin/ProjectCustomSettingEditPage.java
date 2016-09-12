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
package jp.co.opentone.bsol.linkbinder.view.admin;

import jp.co.opentone.bsol.framework.core.dto.Code;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.web.extension.jsf.annotation.Initialize;
import jp.co.opentone.bsol.framework.web.extension.jsf.annotation.Transfer;
import jp.co.opentone.bsol.linkbinder.action.AbstractAction;
import jp.co.opentone.bsol.linkbinder.dto.ProjectCustomSetting;
import jp.co.opentone.bsol.linkbinder.dto.code.CorresponStatus;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.service.admin.ProjectCustomSettingService;
import jp.co.opentone.bsol.linkbinder.view.AbstractPage;
import org.springframework.context.annotation.Scope;

import javax.annotation.ManagedBean;
import javax.annotation.Resource;
import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.List;

/**
 * プロジェクトカスタム設定画面.
 * @author opentone
 *
 */
@ManagedBean
@Scope("view")
public class ProjectCustomSettingEditPage extends AbstractPage {

    /**
     * serialVersionUID.
     */
    private static final long serialVersionUID = 3102546830220069904L;

    /**
     * 自分の情報設定時の表題.
     */
    private static final String TITLE_MINE = "プロジェクト設定";

    /**
     * プロジェクトカスタム設定情報.
     */
    @Transfer
    private ProjectCustomSetting projectCustomSetting;

    /**
     * DefaultStatus設定値.
     */
    @Transfer
    private Integer settingDefaultStatus;

    /**
     * UsePersonInCharge設定値.
     */
    @Transfer
    private boolean settingUsePersonInCharge;

    /**
     * UseAccessControl設定値.
     */
    @Transfer
    private boolean settingUseAccessControl;

    /**
     * UseLearning設定値.
     */
    @Transfer
    private boolean settingUseLearning;

    /**
     * プロジェクトカスタム設定画面サービス.
     */
    @Resource
    private ProjectCustomSettingService service;

    /**
     * 表題.
     */
    @Transfer
    private String title;

    /**
     * 空のインスタンスを生成する.
     */
    public ProjectCustomSettingEditPage() {
    }

    /**
     * selectDefaultStatusを取得します.
     * @return the selectDefaultStatus
     */
    public List<SelectItem> getSelectDefaultStatus() {
        List<Code> l = new ArrayList<Code>();
        for (CorresponStatus c : CorresponStatus.values()) {
            if (CorresponStatus.CANCELED != c) {
                l.add(c);
            }
        }
        return viewHelper.createSelectItem(l);
    }

    /**
     * ProjectCustomSettingを取得します.
     * @return the projectId
     */
    public ProjectCustomSetting getProjectCustomSetting() {
     return projectCustomSetting;
    }

     /**
      * ProjectCustomSettingを設定します.
      * @param projectCustomSetting プロジェクトカスタム設定情報
      */
     public void setProjectCustomSetting(ProjectCustomSetting projectCustomSetting) {
         this.projectCustomSetting = projectCustomSetting;
     }


    /**
     * titleを取得します.
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * titleを設定します.
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }


    /**
     * @param settingDefaultStatus the settingDefaultStatus to set
     */
    public void setSettingDefaultStatus(Integer settingDefaultStatus) {
        this.settingDefaultStatus = settingDefaultStatus;
    }

    /**
     * @return the settingDefaultStatus
     */
    public Integer getSettingDefaultStatus() {
        return settingDefaultStatus;
    }

    private CorresponStatus getCorresponStatus() {
        for (CorresponStatus c : CorresponStatus.values()) {
            if (c.getValue().equals(settingDefaultStatus)) {
                return c;
            }
        }
        return ProjectCustomSetting.DEFAULT_CORRESPON_STATUS;
    }

    /**
     * settingUsePersonInChargeを取得します.
     * @return UsePersonInCharge設定値
     */
    public boolean getSettingUsePersonInCharge() {
        return settingUsePersonInCharge;
    }

    /**
     * @param settingUsePersonInCharge the settingUsePersonInCharge to set
     */
    public void setSettingUsePersonInCharge(boolean settingUsePersonInCharge) {
        this.settingUsePersonInCharge = settingUsePersonInCharge;
    }

    /**
     * @return the settingUsePersonInCharge
     */
    public boolean isSettingUsePersonInCharge() {
        return settingUsePersonInCharge;
    }

    @Initialize
    public void initialize() {
        handler.handleAction(new InitializeAction(this));
    }

    /**
     * ユーザー情報を保存する.
     */
    public void save() {
        handler.handleAction(new SaveAction(this));
    }

    /**
     * @return the settingUseAccessControl
     */
    public boolean isSettingUseAccessControl() {
        return settingUseAccessControl;
    }

    /**
     * @param settingUseAccessControl the settingUseAccessControl to set
     */
    public void setSettingUseAccessControl(boolean settingUseAccessControl) {
        this.settingUseAccessControl = settingUseAccessControl;
    }

    public boolean isSettingUseLearning() {
        return settingUseLearning;
    }

    public void setSettingUseLearning(boolean settingUseLearning) {
        this.settingUseLearning = settingUseLearning;
    }

    static class InitializeAction extends AbstractAction {
        /**
         * serialVersionUID.
         */
        private static final long serialVersionUID = -1508852462175469710L;
        /** アクション発生元ページ. */
        private ProjectCustomSettingEditPage page;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page ページ
         */
        public InitializeAction(ProjectCustomSettingEditPage page) {
            super(page);
            this.page = page;
        }

        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            page.title = TITLE_MINE;

            // 権限チェック
            if (!page.isSystemAdmin() && !page.isProjectAdmin(page.getCurrentProjectId())) {
                throw new ServiceAbortException(
                    ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT);
            }

            ProjectCustomSetting pcs = page.service.find(page.getCurrentProjectId());

            page.projectCustomSetting = pcs;
            page.settingDefaultStatus = pcs.getDefaultStatus().getValue();
            page.settingUsePersonInCharge = pcs.isUsePersonInCharge();
            page.settingUseAccessControl = pcs.isUseCorresponAccessControl();
            page.settingUseLearning = pcs.isUseLearning();

            page.setCurrentProjectInfo(page.getCurrentProject());
        }
    }

    static class SaveAction extends AbstractAction {
        /**
         * serialVersionUID.
         */
        private static final long serialVersionUID = 8057180815175823187L;
        /** アクション発生元ページ. */
        private ProjectCustomSettingEditPage page;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page ページ
         */
        public SaveAction(ProjectCustomSettingEditPage page) {
            super(page);
            this.page = page;
        }

        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {

            ProjectCustomSetting pcs = page.projectCustomSetting;
            pcs.setDefaultStatus(page.getCorresponStatus());
            pcs.setUsePersonInCharge(page.getSettingUsePersonInCharge());
            pcs.setUseCorresponAccessControl(page.isSettingUseAccessControl());
            pcs.setUseLearning(page.isSettingUseLearning());

            page.service.save(pcs);

            page.projectCustomSetting = page.service.find(page.getCurrentProjectId());
            page.setCurrentProjectInfo(page.getCurrentProject());
            page.setPageMessage(ApplicationMessageCode.PROJECT_CUSTOM_SETTING_SAVED);
        }
    }
}
