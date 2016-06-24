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
package jp.co.opentone.bsol.linkbinder.view.common;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.ManagedBean;
import javax.annotation.Resource;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.constraints.Length;
import org.springframework.context.annotation.Scope;

import jp.co.opentone.bsol.framework.core.config.SystemConfig;
import jp.co.opentone.bsol.framework.core.message.MessageCode;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.core.validation.constraints.Alphanumeric;
import jp.co.opentone.bsol.framework.core.validation.constraints.MailAddress;
import jp.co.opentone.bsol.framework.core.validation.constraints.Required;
import jp.co.opentone.bsol.framework.web.extension.jsf.annotation.Initialize;
import jp.co.opentone.bsol.framework.web.extension.jsf.annotation.Prerender;
import jp.co.opentone.bsol.framework.web.extension.jsf.annotation.Transfer;
import jp.co.opentone.bsol.linkbinder.Constants;
import jp.co.opentone.bsol.linkbinder.action.AbstractAction;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroupUser;
import jp.co.opentone.bsol.linkbinder.dto.Project;
import jp.co.opentone.bsol.linkbinder.dto.ProjectUserSetting;
import jp.co.opentone.bsol.linkbinder.dto.UserSettings;
import jp.co.opentone.bsol.linkbinder.dto.condition.AbstractCondition;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchProjectCondition;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchUserCondition;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.service.admin.ProjectService;
import jp.co.opentone.bsol.linkbinder.service.admin.UserService;
import jp.co.opentone.bsol.linkbinder.validation.groups.ValidationGroupBuilder;
import jp.co.opentone.bsol.linkbinder.view.AbstractPage;

/**
 * ユーザー情報表示・変更画面.
 * @author opentone
 */
@ManagedBean
@Scope("view")
public class UserSettingsPage extends AbstractPage {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -2115973212077621495L;

    /**
     * 自分の情報設定時の表題.
     */
    private static final String TITLE_MINE = "個人設定";

    /**
     * 他ユーザーの情報設定時の表題.
     */
    private static final String TITLE_USER = "ユーザー設定";

    /**
     * プロジェクト管理者の値を取得するKEY.
     */
    private static final String KEY_GROUP_ADMIN = "securityLevel.groupAdmin";

    /**
     * プロジェクト管理者の値を取得するKEY.
     */
    private static final String KEY_NORMAL_USER = "securityLevel.normalUser";

    /**
     * デフォルト活動単位のプロジェクト単位の区切り文字.
     */
    public static final String DELIM_PROJECT = ",";

    /**
     * デフォルト活動単位のデータの区切り文字.
     */
    public static final String DELIM_GROUP = "/";

    /**
     * 表示用：コードと名前の区切り文字.
     */
    public static final String DELIM_CODE_AND_NAME = " : ";

    /**
     * 部門管理者を表す値.
     */
    private String groupAdmin;

    /**
     * 一般ユーザーを表す値.
     */
    private String normalUser;

    /**
     * ユーザーサービス.
     */
    @Resource
    private UserService userService;

    /**
     * ユーザーサービス.
     */
    @Resource
    private ProjectService projectService;

    /**
     * ユーザーID.
     * 起動元画面から自動的にセットされる.
     */
    @Transfer
    private String id;

    /**
     * 遷移元ページ.
     * 起動元画面から自動的にセットされる.
     */
    @Transfer
    private String backPage;

    /**
     * ユーザー一覧検索条件.
     * 起動元画面がユーザー一覧の場合、セットされる.
     */
    @Transfer
    private AbstractCondition condition;

    /**
     * 表題.
     */
    @Transfer
    private String title;

    /**
     * ユーザー設定情報.
     */
    @Transfer
    private UserSettings userSettings;

    /**
     * デフォルトプロジェクトを設定できる.
     */
    @Transfer
    private boolean editingProject;

    /**
     * すべてのプロジェクトの情報を表示できる.
     */
    @Transfer
    private boolean viewAllProject;

    /**
     * デフォルト活動単位を設定できる.
     */
    @Transfer
    private boolean editingProjectUserProfile;

    /**
     * デフォルトプロジェクトID.
     */
    @Required
    @Transfer
    private String defaultProjectId;

    /**
     * デフォルトプロジェクトの選択肢.
     */
    @Transfer
    private List<SelectItem> projectSelectItems;

    /**
     * デフォルトメールアドレス.
     */
    @Transfer
    @Required
    @Length(max = 60)
    @MailAddress
    private String defaultEmailAddress;

    /**
     *
     */
    @Length(max = 31)
    @Alphanumeric
    private String password;

    @Length(max = 31)
    @Alphanumeric
    private String passwordConf;


    /**
     * RSS表示フラグ.
     */
    @Transfer
    private boolean enableRSSView;

    /**
     * 空のインスタンスを生成する.
     */
    public UserSettingsPage() {
    }

    /**
     * 入力検証グループ名を返す.
     * @return 入力検証グループ名
     */
    public String getValidationGroups() {
        if (isActionInvoked("form:save")) {
            return new ValidationGroupBuilder().defaultGroup().toString();
        } else {
            return new ValidationGroupBuilder().skipValidationGroup().toString();
        }
    }

    /**
     * メールアドレスの入力検証グループ名を返す.
     * @return メールアドレスの入力検証グループ名
     */
    public String getEmailAddressValidationGroups() {
        if (isActionInvoked("form:save")) {
            return new ValidationGroupBuilder().defaultGroup().toString();
        } else {
            return new ValidationGroupBuilder().skipValidationGroup().toString();
        }
    }

    /**
     * groupAdminを取得します.
     * @return the groupAdmin
     */
    public String getGroupAdmin() {
        return groupAdmin;
    }

    /**
     * groupAdminを設定します.
     * @param groupAdmin the groupAdmin to set
     */
    public void setGroupAdmin(String groupAdmin) {
        this.groupAdmin = groupAdmin;
    }

    /**
     * normalUserを取得します.
     * @return the normalUser
     */
    public String getNormalUser() {
        return normalUser;
    }

    /**
     * normalUserを設定します.
     * @param normalUser the normalUser to set
     */
    public void setNormalUser(String normalUser) {
        this.normalUser = normalUser;
    }

    /**
     * idを取得します.
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * idを設定します.
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * backPageを取得します.
     * @return the backPage
     */
    public String getBackPage() {
        return backPage;
    }

    /**
     * backPageを設定します.
     * @param backPage the backPage to set
     */
    public void setBackPage(String backPage) {
        this.backPage = backPage;
    }

    /**
     * conditionを取得します.
     * @return the condition
     */
    public AbstractCondition getCondition() {
        return condition;
    }

    /**
     * conditionを設定します.
     * @param condition the condition to set
     */
    public void setCondition(AbstractCondition condition) {
        this.condition = condition;
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
     * userSettingsを取得します.
     * @return the userSettings
     */
    public UserSettings getUserSettings() {
        return userSettings;
    }

    /**
     * userSettingsを設定します.
     * @param userSettings the userSettings to set
     */
    public void setUserSettings(UserSettings userSettings) {
        this.userSettings = userSettings;
    }

    /**
     * editingProjectを取得します.
     * @return the editingProject
     */
    public boolean isEditingProject() {
        return editingProject;
    }

    /**
     * editingProjectを設定します.
     * @param editingProject the editingProject to set
     */
    public void setEditingProject(boolean editingProject) {
        this.editingProject = editingProject;
    }

    /**
     * viewAllProjectを取得します.
     * @return the viewAllProject
     */
    public boolean isViewAllProject() {
        return viewAllProject;
    }

    /**
     * viewAllProjectを設定します.
     * @param viewAllProject the viewAllProject to set
     */
    public void setViewAllProject(boolean viewAllProject) {
        this.viewAllProject = viewAllProject;
    }

    /**
     * editingProjectUserProfileを取得します.
     * @return the editingProjectUserProfile
     */
    public boolean isEditingProjectUserProfile() {
        return editingProjectUserProfile;
    }

    /**
     * editingProjectUserProfileを設定します.
     * @param editingProjectUserProfile the editingProjectUserProfile to set
     */
    public void setEditingProjectUserProfile(boolean editingProjectUserProfile) {
        this.editingProjectUserProfile = editingProjectUserProfile;
    }

    /**
     * defaultProjectIdを取得します.
     * @return the defaultProjectId
     */
    public String getDefaultProjectId() {
        return defaultProjectId;
    }

    /**
     * defaultProjectIdを設定します.
     * @param defaultProjectId the defaultProjectId to set
     */
    public void setDefaultProjectId(String defaultProjectId) {
        this.defaultProjectId = defaultProjectId;
    }

    /**
     * defaultEmailAddressを取得します.
     * @return the defaultEmailAddress
     */
    public String getDefaultEmailAddress() {
        return defaultEmailAddress;
    }

    /**
     * defaultEmailAddressを設定します.
     * @param defaultEmailAddress the defaultEmailAddress to set
     */
    public void setDefaultEmailAddress(String defaultEmailAddress) {
        this.defaultEmailAddress = defaultEmailAddress;
    }

    /**
     * passwordを取得します.
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * passwordを設定します.
     * @param defaultEmailAddress the defaultEmailAddress to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * passwordConfを取得します.
     * @return the password
     */
    public String getPasswordConf() {
        return passwordConf;
    }

    /**
     * passwordConfを設定します.
     * @param defaultEmailAddress the defaultEmailAddress to set
     */
    public void setPasswordConf(String passwordConf) {
        this.passwordConf = passwordConf;
    }


    /**
     * projectSelectItemsを取得します.
     * @return the projectSelectItems
     */
    public List<SelectItem> getProjectSelectItems() {
        return projectSelectItems;
    }

    /**
     * projectSelectItemsを設定します.
     * @param projectSelectItems the projectSelectItems to set
     */
    public void setProjectSelectItems(List<SelectItem> projectSelectItems) {
        this.projectSelectItems = projectSelectItems;
    }

    /**
     * @param enableRSSView the enableRSSView to set
     */
    public void setEnableRSSView(boolean enableRSSView) {
        this.enableRSSView = enableRSSView;
    }

    /**
     * @return the enableRSSView
     */
    public boolean isEnableRSSView() {
        return enableRSSView;
    }

    /**
     * 画面を初期化する.
     * <p>
     * 他画面から遷移した時に起動される. ページの初期化に必要な情報を準備する.
     * </p>
     */
    @Initialize
    public void initialize() {
        handler.handleAction(new InitializeAction(this));
    }

    /**
     * ページ描画前の処理.
     * <p>
     * ページ描画直前に必ず起動される.
     * </p>
     */
    @Prerender
    public void prerender() {
        if (enableRSSView
                && StringUtils.isNotEmpty(id)
                && userSettings != null
                && StringUtils.isNotEmpty(userSettings.getRssFeedKey())) {
            viewHelper.setRequestValue(Constants.REQUEST_SCOPE_KEY_RSSUSERID, id);
            viewHelper.setRequestValue(
                Constants.REQUEST_SCOPE_KEY_RSSFEEDKEY, userSettings.getRssFeedKey());
        }
    }

    /**
     * メール通知受信設定画面に遷移する.
     * @return メール通知受信設定画面
     */
    public String goEmailNotificationSetting() {
        if (StringUtils.isNotEmpty(getBackPage())) {
            StringBuilder param = new StringBuilder("emailNotificationSetting?id=%s&backPage=");
            param.append(getBackPage());
            setNextSearchCondition(condition);
            return toUrl(String.format(param.toString(), getUserSettings().getUser().getEmpNo()));
        } else {
            return toUrl(String.format("emailNotificationSetting?id=%s", getUserSettings()
                    .getUser()
                    .getEmpNo()), super.isProjectSelected());
        }
    }

    /**
     * ユーザー情報を保存する.
     * @return null
     */
    public String save() {
        handler.handleAction(new SaveAction(this));
        return null;
    }
//
//    /**
//     * メールアドレスを保存する.
//     * @return null
//     */
//    public String saveEmailAddress() {
//        handler.handleAction(new SaveEmailAddressAction(this));
//        return null;
//    }

    /**
     * 遷移元画面に遷移する.
     * @return 遷移元画面
     */
    public String back() {
        setNextSearchCondition(condition);
        return toUrl(backPage);
    }

    /**
     * 現在のリクエストがsaveアクションによって発生した場合はtrue.
     * @return saveアクションの場合true
     */
    public boolean isSaveAction() {
        return isActionInvoked("form:save");
    }

    /**
     * 画面初期化アクション.
     * @author opentone
     */
    static class InitializeAction extends AbstractAction {
        /**
         * serialVersionUID.
         */
        private static final long serialVersionUID = -8005754745228245490L;

        /** アクション発生元ページ. */
        private UserSettingsPage page;

        /** 現在のプロジェクトのユーザー設定. */
        private ProjectUserSetting currentProjectUserSetting;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page ページ
         */
        public InitializeAction(UserSettingsPage page) {
            super(page);
            this.page = page;
        }

        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        @Override
        public void execute() throws ServiceAbortException {
            clear();

            page.condition =
                    page.getPreviousSearchCondition(SearchUserCondition.class);
            // 起動元からの必須パラメーター
            if (StringUtils.isEmpty(page.id)) {
                throw new ServiceAbortException("ID is not specified.",
                                                MessageCode.E_INVALID_PARAMETER);
            }
            page.userSettings = page.userService.findUserSettings(page.id);
            validate();
            // 現在のプロジェクトのユーザー設定を抜き出し
            setCurrentProjectUserSetting();

            // デフォルト値を設定.
            setDefaultValues();
            // システム設定からセキュリティレベルの値を取得
            setSecurityLevel();
            // 対象ユーザーが自身か他ユーザーかで表題を設定
            setTitle();
            // 編集・閲覧の許可を設定
            setEditingAndViewFlg();
            // 権限によって表示するプロジェクトを制限
            if (!page.viewAllProject) {
                changeProjectUserSettingList();
            }

            // デフォルトプロジェクトの選択肢を設定
            setProjectSelectItems();
            // RSSの表示を設定
            setRSSViewFlg();
        }

        private void clear() {
            page.userSettings = null;
            //  ユーザーのデフォルトプロジェクト
            page.defaultProjectId = null;
            //  ユーザーのデフォルトメールアドレス
            page.defaultEmailAddress = null;
            //  画面タイトル
            page.title = null;

            //  編集・閲覧権限フラグ
            page.editingProject = false;
            page.viewAllProject = false;
            page.editingProjectUserProfile = false;

            //入力値
            page.password = "";
            page.passwordConf = "";
            page.defaultEmailAddress="";


            //  プロジェクト選択リスト・RSSリンク表示フラグ
            page.projectSelectItems = new ArrayList<SelectItem>();
            page.enableRSSView = false;
        }
        /**
         * RSSの表示を設定する.
         */
        private void setRSSViewFlg() {
            if (StringUtils.isNotEmpty(page.userSettings.getRssFeedKey())) {
                page.setEnableRSSView(true);
            } else {
                page.setEnableRSSView(false);
            }
        }

        private void validate() throws ServiceAbortException {
            String login = page.getCurrentUser().getEmpNo();
            String projectId = page.getCurrentProjectId();
            //  他人のプロジェクト情報を参照する場合は
            //  プロジェクト選択が必須
            if (StringUtils.isEmpty(projectId) && !login.equals(page.id)) {
                throw new ServiceAbortException(
                    ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT);
            }

            //  現在のプロジェクトに所属していないユーザー情報を参照することはできない
            if (!login.equals(page.id)) {
                boolean correct = false;
                for (ProjectUserSetting setting : page.userSettings.getProjectUserSettingList()) {
                    if (setting.getProject().getProjectId().equals(projectId)) {
                        correct = true;
                        break;
                    }
                }
                if (!correct) {
                    throw new ServiceAbortException(
                        ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT);
                }
            }
        }

        private void setCurrentProjectUserSetting() throws ServiceAbortException {
            for (ProjectUserSetting setting : page.userSettings.getProjectUserSettingList()) {
                if (setting.getProject().getProjectId().equals(page.getCurrentProjectId())) {
                    currentProjectUserSetting = setting;
                    break;
                }
            }
        }

        private void setDefaultValues() throws ServiceAbortException {
            page.defaultProjectId = page.userSettings.getUser().getDefaultProjectId();
            for (ProjectUserSetting setting : page.userSettings.getProjectUserSettingList()) {
                if (setting.getProjectUser().getDefaultCorresponGroup() != null) {
                    setting.setDefaultCorresponGroupId(
                        setting.getProjectUser().getDefaultCorresponGroup().getId());
                }
                setting.setRole(setting.getProjectUser().getUser().getRole());
            }
            page.defaultEmailAddress =
                page.userService.findEMailAddress(page.userSettings.getUser().getEmpNo());

        }



        private void setSecurityLevel() {
            page.groupAdmin = SystemConfig.getValue(KEY_GROUP_ADMIN);
            page.normalUser = SystemConfig.getValue(KEY_NORMAL_USER);
        }

        private void setTitle() {
            if (page.id.equals(page.getCurrentUser().getEmpNo())) {
                page.title = TITLE_MINE;
            } else {
                page.title = TITLE_USER;
            }
        }

        /**
         * ログイン者が選択されているユーザー情報を変更できるか
         */
        private void setEditingAndViewFlg() {
            if (page.id.equals(page.getCurrentUser().getEmpNo())
                    || page.isSystemAdmin()) {
                page.editingProject = true;
                page.viewAllProject = true;
                page.editingProjectUserProfile = true;
            } else if (page.isProjectAdmin(page.getCurrentProjectId())
                    || isAnyGroupAdmin()) {
                page.editingProject = false;
                page.viewAllProject = false;
                page.editingProjectUserProfile = true;
            }
        }

        private void changeProjectUserSettingList() {
            List<ProjectUserSetting> projectUserSettingList = new ArrayList<ProjectUserSetting>();
            projectUserSettingList.add(currentProjectUserSetting);
            page.userSettings.setProjectUserSettingList(projectUserSettingList);
        }

        private boolean isAnyGroupAdmin() {
            if (currentProjectUserSetting == null) {
                return false;
            }

            for (CorresponGroupUser groupUser
                    : currentProjectUserSetting.getCorresponGroupUserList()) {
                if (page.isGroupAdmin(groupUser.getCorresponGroup().getId())) {
                    return true;
                }
            }
            return false;
        }

        private void setProjectSelectItems() throws ServiceAbortException {
            page.projectSelectItems = new ArrayList<SelectItem>();

            if (page.userSettings.isSystemAdmin()) {
                //  設定・表示対象のユーザーがSystem Adminの場合は
                //  所属していないプロジェクトも選択可能でなければならない
                List<Project> all = loadAllProjects();
                for (Project p : all) {
                    SelectItem item = new SelectItem(p.getProjectId(),
                                                     p.getProjectId()
                                                         + DELIM_CODE_AND_NAME
                                                         + p.getNameE());
                    page.projectSelectItems.add(item);
                }
            } else {
                for (ProjectUserSetting setting : page.userSettings.getProjectUserSettingList()) {
                    Project p = setting.getProject();
                    String value = p.getProjectId();
                    String label = p.getProjectId()
                                       + DELIM_CODE_AND_NAME
                                       + p.getNameE();
                    SelectItem item = new SelectItem(value, label);
                    page.projectSelectItems.add(item);
                }
            }
        }

        private List<Project> loadAllProjects() throws ServiceAbortException {
            try {
                //  全てのプロジェクトを取得して返す
                SearchProjectCondition condition = new SearchProjectCondition();
                return page.projectService.search(condition);
            } catch (ServiceAbortException e) {
                if (ApplicationMessageCode.NO_DATA_FOUND.equals(e.getMessageCode())) {
                    return new ArrayList<Project>();
                } else {
                    throw e;
                }
            }
        }
    }

    /**
     * 保存アクション.
     * @author opentone
     */
    static class SaveAction extends AbstractAction {
        /**
         * serialVersionUID.
         */
        private static final long serialVersionUID = 458418676878975254L;
        /** アクション発生元ページ. */
        private UserSettingsPage page;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page ページ
         */
        public SaveAction(UserSettingsPage page) {
            super(page);
            this.page = page;
        }

        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        @Override
        public void execute() throws ServiceAbortException {
            String password = page.getPassword();
            String passwordConf = page.getPasswordConf();

            validatePassword(password, passwordConf);

            if (page.editingProject) {
                page.userSettings.setDefaultProjectId(page.defaultProjectId);
            }

            page.userSettings.setDefaultEmailAddress(page.getDefaultEmailAddress());
            page.userSettings.setPassword(page.getPassword());

            page.userService.save(page.userSettings);

            // 更新後の情報を最新に(バージョンNoのみ)
            UserSettings current = page.userService.findUserSettings(page.id);
            page.userSettings.setUserProfileVersionNo(current
                    .getUserProfileVersionNo());

            page.setNextSearchCondition(page.condition);
            page.setPageMessage(ApplicationMessageCode.SAVE_SUCCESSFUL);
        }

        /**
         * 入力された値をチェックする.
         */
        public void validatePassword(String password, String passwordConf) throws ServiceAbortException {
            if (!ObjectUtils.equals(password, passwordConf)) {
                throw new ServiceAbortException(ApplicationMessageCode.PASSWORD_UNMATCH);
            }
        }
    }
}
