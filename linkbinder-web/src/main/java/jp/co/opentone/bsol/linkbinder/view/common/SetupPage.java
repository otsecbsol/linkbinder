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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.ManagedBean;
import javax.annotation.Resource;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.constraints.Length;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import jp.co.opentone.bsol.framework.core.ProcessContext;
import jp.co.opentone.bsol.framework.core.config.SystemConfig;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.core.util.DateUtil;
import jp.co.opentone.bsol.framework.core.validation.constraints.Alphanumeric;
import jp.co.opentone.bsol.framework.core.validation.constraints.MailAddress;
import jp.co.opentone.bsol.framework.core.validation.constraints.Required;
import jp.co.opentone.bsol.framework.web.extension.jsf.annotation.Initialize;
import jp.co.opentone.bsol.framework.web.extension.jsf.annotation.Prerender;
import jp.co.opentone.bsol.framework.web.extension.jsf.annotation.Transfer;
import jp.co.opentone.bsol.linkbinder.Constants;
import jp.co.opentone.bsol.linkbinder.action.AbstractAction;
import jp.co.opentone.bsol.linkbinder.attachment.AttachmentInfo;
import jp.co.opentone.bsol.linkbinder.attachment.UploadedFile;
import jp.co.opentone.bsol.linkbinder.dto.Project;
import jp.co.opentone.bsol.linkbinder.dto.SysUsers;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.UserSettings;
import jp.co.opentone.bsol.linkbinder.dto.code.MasterDataImportProcessType;
import jp.co.opentone.bsol.linkbinder.dto.code.MasterDataImportType;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchProjectCondition;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.service.admin.ProjectService;
import jp.co.opentone.bsol.linkbinder.service.admin.UserService;
import jp.co.opentone.bsol.linkbinder.service.common.LoginService;
import jp.co.opentone.bsol.linkbinder.validation.groups.ValidationGroupBuilder;
import jp.co.opentone.bsol.linkbinder.view.AbstractPage;
import jp.co.opentone.bsol.linkbinder.view.admin.attachment.MasterSettingFileUploadAction;
import jp.co.opentone.bsol.linkbinder.view.admin.module.dataimport.MasterDataImportModule;
import jp.co.opentone.bsol.linkbinder.view.admin.module.dataimport.MasterDataImportModuleBuilder;
import jp.co.opentone.bsol.linkbinder.view.admin.module.dataimport.MasterDataImportablePage;
import jp.co.opentone.bsol.linkbinder.view.common.module.redirect.RedirectProcessParameterKey;
import jp.co.opentone.bsol.linkbinder.view.validator.AttachmentValidator;


/**
 * 当システムの初期設定画面.
 * @author opentone
 */
@Component
@ManagedBean
@Scope("view")
public class SetupPage extends AbstractPage implements MasterDataImportablePage {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 401285411829698123L;
    /**
     * ログ出力オブジェクト.
     */
    private static final Logger LOG = LoggerFactory.getLogger(SetupPage.class);

    //----- CSV取込・出力処理 -----
    /**
     * 利用可能フラグCSV取り込み時
     */
    public static final String APRY_FLG_CSV_STRING = "○";
    /**
     * 利用可能フラグDB登録時
     */
    public static final String APRY_FLG_DB_STRING = "X";
    /**
     * データを囲む文字.
     */
    public static final String QUOTE = "\"";
    //----- タイトル -----
    public static final String TITLE1 ="セットアップ";
    public static final String TITLE2 ="セットアップ - 1. 管理者ユーザー作成";
    public static final String TITLE3 ="セットアップ - 2. プロジェクト・利用ユーザー作成";
    public static final String TITLE4 ="セットアップ - 3. 準備完了";

    //----- 仮USER情報 -----
    public static final String SETUP_USER_ID = "setup";
    public static final String SETUP_PJ_ID = "setup";

    /**
     * ログインページ
     */
    public static final String LOGIN_PATH = "./login.jsf";

    /**
     * 取込ファイル.
     */
    private UploadedFile importFile;
    /**
     * 取込ファイル情報.
     */
    @Transfer
    private AttachmentInfo importFileInfo;

    /**
     * 取込エラーメッセージリスト.
     */
    private List<String> errorMessageList;

    /**
     * 入力項目：登録データ.
     */
    private List<SelectItem> selectImportDataType;

    @Autowired
    private MasterDataImportModuleBuilder builder;

    /**
     * ログインに関する処理を提供するサービス.
     */
    @Resource
    private LoginService loginService;
    /**
     * ユーザーに関する処理を提供するサービス.
     */
    @Resource
    private UserService userService;
    /**
     * プロジェクトに関する処理を提供するサービス.
     */
    @Resource
    private ProjectService projectService;


    //----- 各パネル表示 -----
    public boolean visibleStep1;
    public boolean visibleStep2;
    public boolean visibleStep3;
    public boolean visibleStep4;

    /**
     * 管理者ユーザー1名以上登録されているか.
     */
    private boolean existsSysUser;
    /**
     * プロジェクトが1件以上登録されているか.
     */
    public boolean existsProject;
    /**
     * 全ユーザーは1名以上登録されているか.
     */
    public boolean existsUsers;
    /**
     * ページタイトル.
     */
    private String title;
    /**
     * 戻り先パネル
     */
    private int backPage;
    /**
     * 処理条件-Code.
     */
    @Transfer
    @Required
    private Integer selectDataID;
    /**
     * 処理内容-Code.
     */
    @Transfer
    @Required
    private String processID;
    /**
     *  ユーザー設定情報.
     */
   @Transfer
   private UserSettings userSettings;
   /**
    * ユーザーID
    */
   @Transfer
   @Required
   @Length(max = 5)
   private String userId;
   /**
    * デフォルトメールアドレス.
    */
   @Transfer
   @Length(max = 60)
   @MailAddress
   private String defaultEmailAddress;
   /**
    * パスワード
    */
   @Length(max = 31)
   @Alphanumeric
   private String password;
   /**
    * パスワード確認
    */
   @Length(max = 31)
   @Alphanumeric
   private String passwordConf;
   /**
    * 姓
    */
   @Required
   @Length(max = 15)
   private String lastName;
   /**
    * 名
    */
   @Length(max = 16)
   private String firstName;

    /**
     * 空のインスタンスを生成する.
     */
    public SetupPage() {
    }

    /**
     * ページ描画前の処理.
     * <p>
     * ページ描画直前に必ず起動される.
     * </p>
     */
    @Prerender
    public void prerender() {

    }

    /**
     * 添付ファイルを検証する.
     * <p>
     * このメソッドはJSPのValidationフェーズで呼び出される.
     * </p>
     * @param context FacesContext
     * @param component 入力コンポーネント
     * @param value 入力値
     */
    public void validateAttachment(FacesContext context, UIComponent component, Object value) {
        if (!isActionInvoked("form:create")) {
            return;
        }

        if (StringUtils.isNotEmpty((String) value)) {
            AttachmentValidator validator = new AttachmentValidator();
            validator.validate(context, component, value);
        }
    }
    /**
     * アップロードエラー処理.
     * @return null
     */
    public String uploadingException() {
        handler.handleAction(new UploadingExceptionAction(this));
        return null;
    }
    /**
     * 入力検証グループ名を返す.
     * @return 入力検証グループ名
     */
    public String getValidationGroups() {
        if (isActionInvoked("formStep3:Insert")) {
            return new ValidationGroupBuilder().defaultGroup().toString();
        } else {
            return new ValidationGroupBuilder().skipValidationGroup().toString();
        }
    }

    /**
     * ユーザー登録の入力検証グループ名を返す.
     * @return メールアドレスの入力検証グループ名
     */
    public String getuserValidationGroups() {
        if (isActionInvoked("formStep2:insertUser")) {
            return new ValidationGroupBuilder().defaultGroup().toString();
        } else {
            return new ValidationGroupBuilder().skipValidationGroup().toString();
        }
    }

    // -----取得 & 設定------
    /**
     * @param existsSysUser
     *            to set
     */
    public void setExistsSysUser(boolean existsSysUser) {
        this.existsSysUser = existsSysUser;
    }

    /**
     * @return the existsSysUser
     */
    public boolean getExistsSysUser() {
        return existsSysUser;
    }

    /**
     * @param scName
     *            the existsProject to set
     */
    public void setExistsProject(boolean existsProject) {
        this.existsProject = existsProject;
    }

    /**
     * @return the existsProject
     */
    public boolean getExistsProject() {
        return existsProject;
    }

    /**
     * @param existsUsers
     *            to set
     */
    public void setExistsUsers(boolean existsUsers) {
        this.existsUsers = existsUsers;
    }

    /**
     * @return the existsUsers
     */
    public boolean getExistsUsers() {
        return existsUsers;
    }

    /**
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param backPage
     */
    public void setBackPage(int backPage) {
        this.backPage = backPage;
    }

    /**
     * @return the backPage
     */
    public int getBackPage() {
        return backPage;
    }

    /**
     * userSettingsを設定します.
     *
     * @param userSettings
     *            the userSettings to set
     */
    public void setUserSettings(UserSettings userSettings) {
        this.userSettings = userSettings;
    }

    /**
     * userSettingsを取得します.
     *
     * @return the userSettings
     */
    public UserSettings getUserSettings() {
        return userSettings;
    }

    /**
     * userIdを取得します.
     *
     * @return the userId
     */
    public String getUserId() {
        return userId;
    }

    /**
     * userIdを設定します.
     *
     * @param userId
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * defaultEmailAddressを取得します.
     *
     * @return the defaultEmailAddress
     */
    public String getDefaultEmailAddress() {
        return defaultEmailAddress;
    }

    /**
     * defaultEmailAddressを設定します.
     *
     * @param defaultEmailAddress
     *            the defaultEmailAddress to set
     */
    public void setDefaultEmailAddress(String defaultEmailAddress) {
        this.defaultEmailAddress = defaultEmailAddress;
    }

    /**
     * passwordを取得します.
     *
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * passwordを設定します.
     *
     * @param defaultEmailAddress
     *            the defaultEmailAddress to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * passwordConfを取得します.
     *
     * @return the password
     */
    public String getPasswordConf() {
        return passwordConf;
    }

    /**
     * passwordConfを設定します.
     *
     * @param defaultEmailAddress
     *            the defaultEmailAddress to set
     */
    public void setPasswordConf(String passwordConf) {
        this.passwordConf = passwordConf;
    }

    /**
     * passwordConfを取得します.
     *
     * @return the password
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * passwordConfを設定します.
     *
     * @param defaultEmailAddress
     *            the defaultEmailAddress to set
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * passwordConfを取得します.
     *
     * @return the password
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * passwordConfを設定します.
     *
     * @param defaultEmailAddress
     *            the defaultEmailAddress to set
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * @return the selectDataID
     */
    public Integer getSelectDataID() {
        return selectDataID;
    }

    /**
     * @param scName
     *            the selectDataID to set
     */
    public void setSelectDataID(Integer selectDataID) {
        this.selectDataID = selectDataID;
    }

    /**
     * @return the processID
     */
    public String getProcessID() {
        return processID;
    }

    /**
     * @param scName
     *            the processID to set
     */
    public void setProcessID(String processID) {
        this.processID = processID;

    }

    /**
     * @return the errorMessageList
     */
    public List<String> getErrorMessageList() {
        return errorMessageList;
    }

    /**
     * @param errorMessageList
     *            the errorMessageList to set
     */
    public void setErrorMessageList(List<String> errorMessageList) {
        this.errorMessageList = errorMessageList;
    }

    /**
     * @return visibleStep1
     */
    public Boolean getVisibleStep1() {
        return visibleStep1;
    }

    /**
     * @param scName
     *            the scName to set
     */
    public void setVisibleStep1(Boolean visibleStep1) {
        this.visibleStep1 = visibleStep1;
    }

    /**
     * @return visibleStep1
     */
    public Boolean getVisibleStep2() {
        return visibleStep2;
    }

    /**
     * @param scName
     *            the scName to set
     */
    public void setVisibleStep2(Boolean visibleStep2) {
        this.visibleStep2 = visibleStep2;
    }

    /**
     * @return visibleStep3
     */
    public Boolean getVisibleStep3() {
        return visibleStep3;
    }

    /**
     * @param scName
     *            the scName to set
     */
    public void setVisibleStep3(Boolean visibleStep3) {
        this.visibleStep3 = visibleStep3;
    }

    /**
     * @return visibleStep4
     */
    public Boolean getVisibleStep4() {
        return visibleStep4;
    }

    /**
     * @param scName
     *            the scName to set
     */
    public void setVisibleStep4(Boolean visibleStep4) {
        this.visibleStep4 = visibleStep4;
    }

    /**
     * ページの初期化を行う.
     */
    @Initialize
    public void initialize() {
        handler.handleAction(new InitializeAction(this));
    }

    /**
     * ページを初期化する.
     * @author opentone
     */
    static class InitializeAction extends AbstractAction {
        /**
         * SerialVersionUID.
         */
        private static final long serialVersionUID = -91538137494122937L;
        /** 初期設定画面. */
        private SetupPage page;
        /**
         * ページを指定してインスタンス化する.
         * @param page ログイン画面
         */
        public InitializeAction(SetupPage page) {
            super(page);
            this.page = page;
        }

        /* (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        @Override
        public void execute() throws ServiceAbortException {
            // セッション切れを起こさないために、ダミーログインを行なう
            dummyLogin();

            // 選択候補を設定
            page.selectImportDataType =
                    page.viewHelper.createSelectItem(MasterDataImportType.values());

            page.setVisibled(1);
            page.setPageTitle(1);
            if (page.userService.getSysUserCount() > 0 ) {
                page.existsSysUser = true;
            } else {
                page.existsSysUser = false;
            }
            page.existsProject = anyProjectsExist();

            List<User> listUser = page.userService.findAll();
            if( listUser.isEmpty() ){
                page.existsUsers = false;
            } else {
                page.existsUsers = true;
            }
        }

        private boolean anyProjectsExist() throws ServiceAbortException {
            SearchProjectCondition empty = new SearchProjectCondition();
            return !page.projectService.search(empty).isEmpty();
        }

        private void dummyLogin() throws ServiceAbortException {
            try {
                page.loginService.dummyLogin();
            } catch (ServiceAbortException e) {
                throw new ServiceAbortException(
                        "Dummy Login failed.", e, ApplicationMessageCode.E_GENERATION_FAILED);
            }
        }
    }

    public void step0() {
        handler.handleAction(new Step0Action(this));
    }
    public void step1() {
        handler.handleAction(new Step1Action(this));
    }
    public void step2() {
        handler.handleAction(new Step2Action(this));
    }
    public void step3() {
        handler.handleAction(new Step3Action(this));
    }
    public void step4() {
        handler.handleAction(new Step4Action(this));
    }
    public void back() {
        handler.handleAction(new BackAction(this));
    }
    public void insert() {
        // ファイルアップロード処理
        if (!handler.handleAction(new MasterSettingFileUploadAction(this))) {
            return;
        }

        handler.handleAction(new InsertAction(this));
    }

    /**
     * CSV出力を行う.
     * @return null
     */
    public String exportCsv() {
        handler.handleAction(new CSVDownloadAction(this));
        return null;
    }

    /**
     * 初期設定開始画面を表示する.
     * @author opentone
     *
     */
    static class Step0Action extends AbstractAction {
        /**
         * SerialVersionUID.
         */
        private static final long serialVersionUID = -169728429913939341L;

        /** アクション発生元ページ. */
        private SetupPage page;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page ページ
         */
        public Step0Action(SetupPage page) {
            super(page);
            this.page = page;

        }
        @Override
        public void execute() throws ServiceAbortException {
            page.clearErrorMessageList();
            page.setVisibled(1);
            page.setPageTitle(1);
            page.initialize();
        }
    }

    /**
     * 初期設定画面1設定開始ボタン押下時の処理
     * @author opentone
     *
     */
    static class Step1Action extends AbstractAction {
        /**
         * SerialVersionUID.
         */
        private static final long serialVersionUID = 1471473961471536905L;

        /** アクション発生元ページ. */
        private SetupPage page;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page ページ
         */
        public Step1Action(SetupPage page) {
            super(page);
            this.page = page;

        }
        @Override
        public void execute() throws ServiceAbortException {
            page.clearErrorMessageList();
            if (page.existsSysUser) {
                page.setVisibled(3);
                page.setPageTitle(3);
            } else {
                page.setVisibled(2);
                page.setPageTitle(2);
            }
            page.setBackPage(1);
        }
    }

    /**
     * 初期設定画面2登録ボタン押下時の処理.
     * @author opentone
     *
     */
    static class Step2Action extends AbstractAction {
        /**
         * SerialVersionUID.
         */
        private static final long serialVersionUID = 4682130173216817834L;

        /** アクション発生元ページ. */
        private SetupPage page;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page ページ
         */
        public Step2Action(SetupPage page) {
            super(page);
            this.page = page;
        }
        @Override
        public void execute() throws ServiceAbortException {
            page.clearErrorMessageList();
            String password = page.getPassword();
            String passwordConf = page.getPasswordConf();

            if (!ObjectUtils.equals(password, passwordConf)) {
                throw new ServiceAbortException(ApplicationMessageCode.PASSWORD_UNMATCH);
            } else {
                page.setContextUser(SETUP_USER_ID);

                List<SysUsers> userList = new ArrayList<>();
                userList.add(setupUserDto());
                page.userService.save(userList);

                page.setVisibled(3);
                page.setPageTitle(3);
                page.setBackPage(2);
            }
        }

        private SysUsers setupUserDto() {
            SysUsers u = new SysUsers();
            u.setEmpNo(page.userId);
            u.setLastName(page.lastName);
            u.setNameE(page.lastName + ' ' + page.firstName);
            u.setPassword(page.password);
            u.setMailAddress(page.defaultEmailAddress);
            u.setSysAdmFlg(APRY_FLG_DB_STRING);
            u.setUserRegistAprvFlg(APRY_FLG_DB_STRING);
            u.setUserIdAt(DateUtil.convertDateToStringForView(DateUtil.getNow()));

            return u;
        }
    }


    /**
     * 初期設定画面3でのボタン押下時の処理（初期設定画面4へ遷移する）.
     * @author opentone
     *
     */
    static class Step3Action extends AbstractAction {
        /**
         * SerialVersionUID.
         */
        private static final long serialVersionUID = -6560848009827657992L;

        /** アクション発生元ページ. */
        private SetupPage page;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page ページ
         */
        public Step3Action(SetupPage page) {
            super(page);
            this.page = page;

        }
        @Override
        public void execute() throws ServiceAbortException {
            page.clearErrorMessageList();
            page.setVisibled(4);
            page.setPageTitle(4);
            page.setBackPage(3);
        }
    }

    /**
     * 初期設定画面4でのボタン押下時の処理.
     * @author opentone
     *
     */
    static class Step4Action extends AbstractAction {
        /**
         * SerialVersionUID.
         */
        private static final long serialVersionUID = -3720736204127291898L;

        /** アクション発生元ページ. */
        private SetupPage page;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page ページ
         */
        public Step4Action(SetupPage page) {
            super(page);
            this.page = page;
        }
        @Override
        public void execute() throws ServiceAbortException {
            page.redirect();
        }

    }

    static class BackAction extends AbstractAction {
        /**
         * SerialVersionUID.
         */
        private static final long serialVersionUID = -2447339535442645099L;

        /** アクション発生元ページ. */
        private SetupPage page;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page ページ
         */
        public BackAction(SetupPage page) {
            super(page);
            this.page = page;
        }

        @Override
        public void execute() throws ServiceAbortException {
            page.clearErrorMessageList();
            page.setVisibled(page.getBackPage());
            page.setPageTitle(page.getBackPage());
        }
    }

    static class InsertAction extends AbstractAction {
        /**
         * SerialVersionUID.
         */
        private static final long serialVersionUID = 298954001082559028L;

        /** アクション発生元ページ. */
        private SetupPage page;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page ページ
         */
        public InsertAction(SetupPage page) {
            super(page);
            this.page = page;

        }
        @Override
        public void execute() throws ServiceAbortException {
            page.clearErrorMessageList();
            MasterDataImportModule<?> module = page.builder
                    .build(MasterDataImportType.valueOf(page.selectDataID));

            page.setContextUser(SETUP_USER_ID);

            module.execute(page);
        }
    }

    static class  CSVDownloadAction extends AbstractAction {
        /**
         * SerialVersionUID.
         */
        private static final long serialVersionUID = -1189572790020637382L;

        /** マスタ登録画面. */
        private SetupPage page;

        public CSVDownloadAction(SetupPage page) {
            super(page);
            this.page = page;

        }

        @Override
        public void execute() throws ServiceAbortException {
            page.clearErrorMessageList();
            try {
                String fileName = page.createFileName();
                byte[] data = null;

                page.setContextUser(SETUP_USER_ID);

                switch (MasterDataImportType.valueOf(page.selectDataID)) {
                    case PROJECT:
                        //プロジェクト情報
                        fileName +=  "_Project.csv";

                        List<Project> list = page.projectService.findAll();
                        data = page.projectService.generateCSV(list);
                        break;
                    case USER:
                        //ユーザー情報
                        fileName +=  "_USER.csv";
                        List<User> listUser = page.userService.findAll();
                        data = page.userService.generateCSV(listUser);
                        break;
                }
                page.viewHelper.download(fileName, data);
            } catch (IOException e) {
                throw new ServiceAbortException(
                    "Excel Download failed.", e, ApplicationMessageCode.E_DOWNLOAD_FAILED);
            }
        }
    }

    static class UploadingExceptionAction extends AbstractAction {
        /**
         * serialVersionUID.
         */
        private static final long serialVersionUID = -5299883299634618948L;

        /**
         * ページを指定してインスタンスを生成する.
         * @param page このアクションを起動したページ
         */
        public UploadingExceptionAction(SetupPage page) {
            super(page);
        }
        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        @Override
        public void execute() throws ServiceAbortException {
            throw new ServiceAbortException(ApplicationMessageCode.ERROR_UPLOADING_FILE);
        }
    }


    /**
     * ステップ毎の表示設定
     * @param stage
     */
    private void setVisibled(int stage) {

        switch (stage) {
        case 1:
            visibleStep1 = true;
            visibleStep2 = false;
            visibleStep3 = false;
            visibleStep4 = false;
            break;
        case 2:
            visibleStep1 = false;
            visibleStep2 = true;
            visibleStep3 = false;
            visibleStep4 = false;
            break;
        case 3:
            visibleStep1 = false;
            visibleStep2 = false;
            visibleStep3 = true;
            visibleStep4 = false;
            break;
        case 4:
            visibleStep1 = false;
            visibleStep2 = false;
            visibleStep3 = false;
            visibleStep4 = true;
            break;
        }

    }
    public void clearErrorMessageList() {
        if (getErrorMessageList() != null) {
            getErrorMessageList().clear();
        }
    }

    private void setPageTitle(int stage) {
        switch (stage) {
        case 1:
            title = TITLE1;
            break;
        case 2:
            title = TITLE2;
            break;
        case 3:
            title = TITLE3;
            break;
        case 4:
            title = TITLE4;
            break;
        }
    }

    private void setContextUser(String userId) {
        //  仮のユーザーを指定されたユーザーIDに設定
        ProcessContext pc = ProcessContext.getCurrentContext();
        pc.setValue(SystemConfig.KEY_USER_ID, userId);
    }

    private void clearRedirectValue() {
        // セッションにセットされた情報を消す
        viewHelper.removeSessionValue(Constants.KEY_REDIRECT_SCREEN_ID);
        viewHelper.removeSessionValue(RedirectProcessParameterKey.ID);
        viewHelper.removeSessionValue(RedirectProcessParameterKey.PROJECT_ID);
    }

    private void redirect() {
        HttpServletResponse response = (HttpServletResponse) viewHelper.getExternalContext().getResponse();
        try {
            response.sendRedirect(LOGIN_PATH);
        } catch (IOException e) {
            // ログイン状態を無効にする
            viewHelper.removeSessionValue(Constants.KEY_PROJECT);
            viewHelper.removeSessionValue(Constants.KEY_CURRENT_USER);
            // リダイレクトできないため、情報をクリアしてリダイレクトをキャンセル
            clearRedirectValue();
        }
    }

    public List<SelectItem> getSelectImportDataType() {
        return selectImportDataType;
    }

    public void setSelectImportDataType(List<SelectItem> selectImportDataType) {
        this.selectImportDataType = selectImportDataType;
    }

    public UploadedFile getImportFile() {
        if (importFile == null) {
            importFile = new UploadedFile();
        }
        return importFile;
    }

    public void setImportFile(UploadedFile importFile) {
        this.importFile = importFile;
    }
    @Override
    public AttachmentInfo getImportFileInfo() {
        return importFileInfo;
    }

    @Override
    public void setImportFileInfo(AttachmentInfo importFileInfo) {
        this.importFileInfo = importFileInfo;
    }

    /* (非 Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.view.admin.module.dataimport.MasterDataImportablePage#getImportType()
     */
    @Override
    public MasterDataImportType getImportType() {
        return MasterDataImportType.valueOf(getSelectDataID());
    }

    /* (非 Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.view.admin.module.dataimport.MasterDataImportablePage#getImportProcessType()
     */
    @Override
    public MasterDataImportProcessType getImportProcessType() {
        return MasterDataImportProcessType.CREATE_OR_UPDATE;
    }

    /* (非 Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.view.admin.module.dataimport.MasterDataImportablePage#importSucceeded()
     */
    @Override
    public void importSucceeded() {
        setPageMessage(ApplicationMessageCode.FINISHED);
    }

    /* (非 Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.view.admin.module.dataimport.MasterDataImportablePage#importParseFailed(java.util.List)
     */
    @Override
    public void importParseFailed(List<String> messages) {
        setErrorMessageList(messages);
    }
}
