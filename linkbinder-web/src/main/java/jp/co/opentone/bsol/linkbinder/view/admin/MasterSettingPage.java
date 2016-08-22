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

import java.io.IOException;
import java.util.List;

import javax.annotation.ManagedBean;
import javax.annotation.Resource;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.core.validation.constraints.Required;
import jp.co.opentone.bsol.framework.web.extension.jsf.annotation.Initialize;
import jp.co.opentone.bsol.framework.web.extension.jsf.annotation.Transfer;
import jp.co.opentone.bsol.linkbinder.action.AbstractAction;
import jp.co.opentone.bsol.linkbinder.attachment.AttachmentInfo;
import jp.co.opentone.bsol.linkbinder.attachment.UploadedFile;
import jp.co.opentone.bsol.linkbinder.dto.Project;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.code.MasterDataImportProcessType;
import jp.co.opentone.bsol.linkbinder.dto.code.MasterDataImportType;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchProjectCondition;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.service.admin.ProjectService;
import jp.co.opentone.bsol.linkbinder.service.admin.UserService;
import jp.co.opentone.bsol.linkbinder.service.correspon.CorresponFullTextSearchService;
import jp.co.opentone.bsol.linkbinder.validation.groups.ValidationGroupBuilder;
import jp.co.opentone.bsol.linkbinder.view.AbstractPage;
import jp.co.opentone.bsol.linkbinder.view.admin.attachment.MasterSettingFileUploadAction;
import jp.co.opentone.bsol.linkbinder.view.admin.module.dataimport.MasterDataImportModule;
import jp.co.opentone.bsol.linkbinder.view.admin.module.dataimport.MasterDataImportModuleBuilder;
import jp.co.opentone.bsol.linkbinder.view.admin.module.dataimport.MasterDataImportablePage;
import jp.co.opentone.bsol.linkbinder.view.validator.AttachmentValidator;

/**
 * マスタデータ取り込み画面.
 * @author opentone
 */
@ManagedBean
@Scope("view")
public class MasterSettingPage extends AbstractPage implements MasterDataImportablePage {
    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 8186671266016330700L;
    /**
     * 初期画面表示判定値.
     */
    private boolean initialDisplaySuccess = false;

    //サービス
    /**
     * プロジェクト情報サービス.
     */
    @Resource
    private ProjectService pjService;

    /**
     * ユーザー情報サービス.
     */
    @Resource
    private UserService userService;

    /**
     * 入力項目：登録データ.
     */
    private List<SelectItem> selectImportDataType;

    /**
     * 入力項目：処理種別.
     */
    private List<SelectItem> selectProcessType;

    /** 全文検索サービス. */
    @Autowired
    private CorresponFullTextSearchService fullTextSearchService;

    @Autowired
    private MasterDataImportModuleBuilder builder;

    /**
     * 検索条件.
     */
    @Transfer
    private SearchProjectCondition condition = null;

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
    private Integer processID;

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
     * 画面を初期化する.
     */
    @Initialize
    public void initialize() {
        handler.handleAction(new InitializeAction(this));
    }

    /**
     * CSV取り込み処理を行なう.
     * @return
     */
    public String create() {
        // ファイルアップロード処理
        if (!handler.handleAction(new MasterSettingFileUploadAction(this))) {
            return null;
        }

        handler.handleAction(new SaveAction(this));
        return null;
    }

    /**
     * CSV出力を行う.
     * @return null
     */
    public String export() {
        handler.handleAction(new CsvDownloadAction(this));
        return null;
    }

    /**
     * アップロードエラー処理.
     * @return null
     */
    public String uploadingException() {
        handler.handleAction(new UploadingExceptionAction(this));
        return null;
    }
    //-----取得 & 設定------
    /**
     * 検索条件を設定する.
     * @param condition
     *            検索条件
     */
    public void setCondition(SearchProjectCondition condition) {
        this.condition = condition;
    }

    /**
     * 検索条件を取得する.
     * @return 検索条件
     */
    public SearchProjectCondition getCondition() {
        return condition;
    }
    /**
     * 初期画面表示判定を取得する.
     * @return 初期画面表示判定
     */
    public boolean getInitialDisplaySuccess() {
        return initialDisplaySuccess;
    }

    /**
     * 初期画面表示判定を設定する.
     * @param initialDisplaySuccess 初期画面表示判定
     */
    public void setInitialDisplaySuccess(boolean initialDisplaySuccess) {
        this.initialDisplaySuccess = initialDisplaySuccess;
    }
    /**
     * @return the selectDataID
     */
    public Integer getSelectDataID() {
        return selectDataID;
    }
    /**
     * @param scName the selectDataID to set
     */
    public void setSelectDataID(Integer selectDataID) {
        this.selectDataID = selectDataID;
    }
    /**
     * @return the processID
     */
    public Integer getProcessID() {
        return processID;
    }
    /**
     * @param scName the processID to set
     */
    public void setProcessID(Integer processID) {
        this.processID = processID;
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

    public List<String> getErrorMessageList() {
        return errorMessageList;
    }

    public void setErrorMessageList(List<String> errorMessageList) {
        this.errorMessageList = errorMessageList;
    }

    /**
     * このページの操作を実行可能なユーザー権限であるか検証する.
     * 実行不可の場合は {@link ServiceAbortException} をthrowする.
     */
    public void validatePermission() throws ServiceAbortException {
        // System Admin以外はエラー
        if (!isSystemAdmin()) {
            throw new ServiceAbortException(
                    ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT);
        }
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
     * 入力検証グループ名を返す.
     * @return 入力検証グループ名
     */
    public String getValidationGroups() {
        return new ValidationGroupBuilder().defaultGroup().toString();
//        if (isActionInvoked("form:create")) {
//            return new ValidationGroupBuilder().defaultGroup().toString();
//        } else {
//            return new ValidationGroupBuilder().skipValidationGroup().toString();
//        }
    }

    /**
     * 画面初期化アクション.
     * @author opentone
     */
    static class InitializeAction extends AbstractAction {
        /**
         * serialVersionUID.
         */
        private static final long serialVersionUID = 7121307925815826849L;
        /** アクション発生元ページ. */
        private MasterSettingPage page;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page
         *            ページ
         */
        public InitializeAction(MasterSettingPage page) {
            super(page);
            this.page = page;
        }

        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        @Override
        public void execute() throws ServiceAbortException {
            page.validatePermission();

            // 初期値・選択候補を設定
            page.processID = MasterDataImportProcessType.CREATE_OR_UPDATE.getValue();
            page.selectImportDataType =
                    page.viewHelper.createSelectItem(MasterDataImportType.values());
            page.selectProcessType =
                    page.viewHelper.createSelectItem(MasterDataImportProcessType.values());

            // 初期画面表示成功
            page.initialDisplaySuccess = true;
        }
    }

    /**
     * 保存アクション.
     */
    static class SaveAction extends AbstractAction {
        /**
        * SerialVersionUID.
        */
       private static final long serialVersionUID = 682084022967872569L;
       /** マスタ登録. */
       private MasterSettingPage page;

       /**
        * ページを指定してインスタンス化する.
        * @param page マスタ登録画面
        */
       public SaveAction(MasterSettingPage page) {
           super(page);
           this.page = page;
       }

       /* (non-Javadoc)
        * @see jp.co.opentone.bsol.framework.action.Action#execute()
        */
       @Override
    public void execute() throws ServiceAbortException {
           page.validatePermission();
           MasterDataImportModule<?> module = page.builder.build(
                   MasterDataImportType.valueOf(page.selectDataID));

           if (page.errorMessageList != null) {
               page.errorMessageList.clear();
           }
           module.execute(page);
       }
    }

    static class  CsvDownloadAction extends AbstractAction {
        /** マスタ登録画面. */
        private MasterSettingPage page;

        public CsvDownloadAction(MasterSettingPage page) {
            super(page);
            this.page = page;
        }

        @Override
        public void execute() throws ServiceAbortException {
            page.validatePermission();
            try {
                page.condition = new SearchProjectCondition();
                page.condition.setProjectId("");
                page.condition.setNameE("");

                String fileName = page.createFileName();
                byte[] data = null;

                switch (MasterDataImportType.valueOf(page.selectDataID)) {
                    case PROJECT:
                        //プロジェクト情報
                        fileName +=  "_Project.csv";

                        List<Project> list = page.pjService.findAllWithOutLearning();
                        data = page.pjService.generateCSV(list);
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
        public UploadingExceptionAction(MasterSettingPage page) {
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
     * メッセージ作成処理
     * @param message 表示内容
     * @param messageCode メッセージ内容番号
     * @return HTMLタグ付与後のメッセージ
     */
    private static String createMessage(String message, String messageCode) {
        String result = "";
        result = "<p class=\""+ messageCode + "\">";
        result +=  message;
        result += "</p>";

        return result;
    }

    public List<SelectItem> getSelectImportDataType() {
        return selectImportDataType;
    }

    public void setSelectImportDataType(List<SelectItem> selectImportDataType) {
        this.selectImportDataType = selectImportDataType;
    }

    public List<SelectItem> getSelectProcessType() {
        return selectProcessType;
    }

    public void setSelectProcessType(List<SelectItem> selectProcessType) {
        this.selectProcessType = selectProcessType;
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
        return MasterDataImportProcessType.valueOf(getProcessID());
    }

    /* (非 Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.view.admin
     *  .module.dataimport.MasterDataImportablePage#importSucceeded()
     */
    @Override
    public void importSucceeded() {
        setPageMessage(ApplicationMessageCode.FINISHED);
    }

    /*
     * (非 Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.view.admin
     *      .module.dataimport.MasterDataImportablePage#importFailed(java.util.List)
     */
    @Override
    public void importParseFailed(List<String> messages) {
        errorMessageList = messages;
    }
}
