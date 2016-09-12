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
package jp.co.opentone.bsol.linkbinder.view.correspon;

import jp.co.opentone.bsol.framework.core.message.MessageCode;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.web.extension.jsf.annotation.Initialize;
import jp.co.opentone.bsol.framework.web.extension.jsf.annotation.Transfer;
import jp.co.opentone.bsol.linkbinder.Constants;
import jp.co.opentone.bsol.linkbinder.action.AbstractAction;
import jp.co.opentone.bsol.linkbinder.attachment.AttachmentInfo;
import jp.co.opentone.bsol.linkbinder.dto.AddressCorresponGroup;
import jp.co.opentone.bsol.linkbinder.dto.Correspon;
import jp.co.opentone.bsol.linkbinder.event.CorresponCreated;
import jp.co.opentone.bsol.linkbinder.event.CorresponUpdated;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.service.admin.CorresponGroupService;
import jp.co.opentone.bsol.linkbinder.service.admin.CorresponTypeService;
import jp.co.opentone.bsol.linkbinder.service.correspon.CorresponSaveService;
import jp.co.opentone.bsol.linkbinder.service.correspon.CorresponValidateService;
import jp.co.opentone.bsol.linkbinder.service.correspon.LearningLabelService;
import jp.co.opentone.bsol.linkbinder.util.view.correspon.CorresponPageFormatter;
import jp.co.opentone.bsol.linkbinder.view.correspon.attachment.AttachmentDownloadAction;
import jp.co.opentone.bsol.linkbinder.view.correspon.attachment.AttachmentDownloadablePage;
import jp.co.opentone.bsol.linkbinder.view.correspon.util.CorresponDataSource;
import jp.co.opentone.bsol.linkbinder.view.correspon.util.CorresponEditMode;
import jp.co.opentone.bsol.linkbinder.view.util.RichTextUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;

import javax.annotation.ManagedBean;
import javax.annotation.Resource;
import java.util.List;

/**
 * コレポン文書入力確認画面.
 * @author opentone
 */
@ManagedBean
@Scope("view")
public class CorresponConfirmationPage extends AbstractCorresponPage
        implements AttachmentDownloadablePage {
    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -6819171074955985797L;
    /**
     * logger.
     */
    private static final Logger log = LoggerFactory.getLogger(CorresponConfirmationPage.class);
    /**
     * 表題（新規登録）ラベル.
     */
    private static final String NEW = "文書 新規登録";
    /**
     * 表題（更新）ラベル.
     */
    private static final String UPDATE = "文書 更新";

    /**
     * コレポン文書関連画面で利用する共通データ.
     */
    @Resource
    @Transfer
    private CorresponDataSource dataSource;

    /**
     * コレポン文書の表示内容を整形するオブジェクト.
     */
    private CorresponPageFormatter formatter;
    /**
     * コレポン文書検証サービス.
     */
    @Resource
    private CorresponValidateService corresponValidateService;
    /**
     * コレポン文書保存サービス.
     */
    @Resource
    private CorresponSaveService corresponSaveService;
    /**
     * 活動単位サービス.
     */
    @Resource
    private CorresponGroupService corresponGroupService;
    /**
     * コレポン文書種別サービス.
     */
    @Resource
    private CorresponTypeService corresponTypeService;
    /**
     * 学習用ラベルサービス.
     */
    @Resource
    private LearningLabelService learningLabelService;
    /**
     * 初期画面表示判定値.
     */
    @Transfer
    private boolean initialDisplaySuccess = false;
    /**
     * 表示対象のコレポン文書.
     */
    @Transfer
    private Correspon correspon = null;
    /**
     * 表示タイトル.
     */
    @Transfer
    private String title;
    /**
     * 新規登録処理を表すクエリパラメータ.
     */
    @Transfer
    private String newEdit;

    //Javadocコメントに対する警告を抑制
    //CHECKSTYLE:OFF
    /**
     * Toに設定された値のJSON形式の文字列.
     * <p>
     * この値は{@code List<AddressCorresponGroup>}に変換可能.
     * </p>
     * @see AddressCorresponGroup
     */
    @Transfer
    private String toAddressValues;

    /**
     * 表示対象displayBody.
     * <p>
     * body.
     * </p>
     */
    @Transfer
    private String displayBody;

    /**
     * 登録対象の添付ファイル.
     */
    @Transfer
    private List<AttachmentInfo> attachments;
    /**
     * 削除された添付ファイル.
     */
    @Transfer
    private List<AttachmentInfo> removedAttachments;

    /**
     * 表示制御.
     */
    @Transfer
    private String editMode;
    /**
     * ダウンロード対象ファイルのID.
     */
    private Long fileId;
    /**
     * ダウンロード対象ファイルのNo.
     */
    private int fileNo;

    /**
     * 学習用文書の表示用ラベル
     */
    @Transfer
    private String learningCorresponTitleLabel = this.getLearningCorresponLabel();

    /**
     * 画面初期表示判定値を返す.
     * @return the initialDisplaySuccess
     */
    public boolean getInitialDisplaySuccess() {
        return initialDisplaySuccess;
    }
    /**
     * コレポン文書を設定する.
     * @param correspon
     *            the correspon to set
     */
    public void setCorrespon(Correspon correspon) {
        this.correspon = correspon;
    }
    /**
     * コレポン文書を返す.
     * @return the correspon
     */
    public Correspon getCorrespon() {
        return correspon;
    }
    /**
     * 表示タイトルを返す.
     * @return the title
     */
    public String getTitle() {
        return title;
    }
    /**
     * 表示タイトルを設定する.
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }
    /**
     * @param displayBody
     *            the displayBody to set
     */
    public void setDisplayBody(String displayBody) {
        this.displayBody = displayBody;
    }
    /**
     * @return the displayBody
     */
    public String getDisplayBody() {
        return new RichTextUtil().createRichText(this.displayBody);
    }
    /**
     * @return the fileId
     */
    public Long getFileId() {
        return fileId;
    }
    /**
     * @param fileId the fileId to set
     */
    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }
    /**
     * @return the fileNo
     */
    public int getFileNo() {
        return fileNo;
    }
    /**
     * @param fileNo
     *            the fileNo to set
     */
    public void setFileNo(int fileNo) {
        this.fileNo = fileNo;
    }
    /**
     * @return the editMode
     */
    public String getEditMode() {
        return editMode;
    }
    /**
     * @param editMode
     *            the editMode to set
     */
    public void setEditMode(String editMode) {
        this.editMode = editMode;
    }

    /**
     * 空のインスタンスを生成する.
     */
    public CorresponConfirmationPage() {
    }

    /**
     * 画面を初期化する.
     * <p>
     * 他画面から遷移した時に起動される. クエリパラメータで指定されたIDに対応する、コレポン文書を表示する.
     * </p>
     */
    @Initialize
    public void initialize() {
        handler.handleAction(new InitializeAction(this));
    }

    /**
     * コレポンの入力内容を登録し表示画面へ遷移 validateチェック.
     * @return 遷移先画面ID
     * @throws ServiceAbortException
     * @throws ServiceAbortException
     */
    public String save() {
        transferCorresponDisplayInfo();
        if (handler.handleAction(new SaveAction(this))) {
            setEditMode(CorresponEditMode.NEW.name());
            transferBackPage();
            return String.format("correspon?id=%s&projectId=%s&readMode=%s&fromEdit=1",
                            correspon.getId(),
                            getCurrentProjectId(),
                            Constants.READ_STATUS_MODE);
        }
        return null;
    }

    /**
     * コレポン入力確認画面へ遷移.
     * @return 遷移先画面ID
     */
    public String back() {
        setEditMode(CorresponEditMode.BACK.name());
        setTransferNext(true);
        transferBackPage();
        return toUrl(String.format("corresponEdit?newEdit=%s", newEdit != null ? newEdit : ""));
    }

    /**
     * 指定された添付ファイルをダウンロードする.
     * @return 遷移先. 常にnull
     */
    public String download() {
        handler.handleAction(new AttachmentDownloadAction(this));
        return null;
    }

    /**
     * コレポン文書の表示内容を整形するオブジェクトを返す.
     * @return コレポン文書整形オブジェクト
     */
    public CorresponPageFormatter getFormatter() {
        if (formatter == null) {
            formatter = new CorresponPageFormatter(correspon);
        }
        formatter.setCorrespon(correspon);
        return formatter;
    }

    /**
     * コレポン文書状態を表示する場合はtrue.
     * @return 文書状態を表示する場合はtrue
     */
    public boolean isDisplayCorresponStatus() {
        //  返信文書でも文書状態を表示可能とする
        return correspon != null;
    }

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.view.correspon.attachment
     *     .AttachmentDownloadablePage#getDownloadingAttachmentInfo()
     */
    public AttachmentInfo getDownloadingAttachmentInfo() {
        return attachments.get(getFileNo() - 1);
    }

    /**
     * @param dataSource the dataSource to set
     */
    public void setDataSource(CorresponDataSource dataSource) {
        this.dataSource = dataSource;
    }
    /**
     * @return the dataSource
     */
    public CorresponDataSource getDataSource() {
        return dataSource;
    }

    /**
     * @param toAddressValues the toAddressValues to set
     */
    public void setToAddressValues(String toAddressValues) {
        this.toAddressValues = toAddressValues;
    }
    /**
     * @return the toAddressValues
     */
    public String getToAddressValues() {
        return toAddressValues;
    }

    /**
     * @return the toAddressCorresponGroups
     */
    public List<AddressCorresponGroup> getToAddressCorresponGroups() {
        if (correspon == null) {
            return null;
        }
        return correspon.getToAddressCorresponGroups();
    }

    /**
     * @return the ccAddressCorresponGroups
     */
    public List<AddressCorresponGroup> getCcAddressCorresponGroups() {
        if (correspon == null) {
            return null;
        }
        return correspon.getCcAddressCorresponGroups();
    }

    /**
     * @param attachments the attachments to set
     */
    public void setAttachments(List<AttachmentInfo> attachments) {
        this.attachments = attachments;
    }
    /**
     * @return the attachments
     */
    public List<AttachmentInfo> getAttachments() {
        return attachments;
    }

    /**
     * @param removedAttachments the removedAttachments to set
     */
    public void setRemovedAttachments(List<AttachmentInfo> removedAttachments) {
        this.removedAttachments = removedAttachments;
    }
    /**
     * @return the removedAttachments
     */
    public List<AttachmentInfo> getRemovedAttachments() {
        return removedAttachments;
    }

    /**
     * @param newEdit the newEdit to set
     */
    public void setNewEdit(String newEdit) {
        this.newEdit = newEdit;
    }
    /**
     * @return the newEdit
     */
    public String getNewEdit() {
        return newEdit;
    }

    /**
     * HTMLに表示するアイコンを取得する.
     * @return アイコン
     */
    public String getIconPathName() {
        CorresponPageFormatter corresponPageFormatter = new CorresponPageFormatter();
        if (getCurrentProject() == null) {
            return null;
        }
        return corresponPageFormatter.getProjectLogoUrl(getCurrentProject().getProjectId());
    }

    /**
     * 画面初期化アクション.
     * @author opentone
     */
    static class InitializeAction extends AbstractAction {
        /**
         * serialVersionUID.
         */
        private static final long serialVersionUID = 4526129697265572141L;
        /** アクション発生元ページ. */
        private CorresponConfirmationPage page;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page
         *            ページ
         */
        public InitializeAction(CorresponConfirmationPage page) {
            super(page);
            this.page = page;
        }

        /* (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            // URLパラメータチェック
            if (StringUtils.isEmpty(page.getCurrentProjectId())) {
                throw new ServiceAbortException(
                    "Project ID is not specified.", MessageCode.E_INVALID_PARAMETER);
            }

            // フィールドcorresponがない = 確認画面リダイレクト
            // (編集画面からの正常な遷移ではない)
            // アクセスレベルが異常な旨のメッセージを出力
            if (page.correspon == null) {
                throw new ServiceAbortException(
                        ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT);
            }

            if (page.correspon.isNew()) {
                // 新規登録
                page.title = NEW;
            } else {
                // 更新
                page.title = UPDATE;
            }
            Long id = page.correspon.getCorresponType().getProjectCorresponTypeId();
            page.correspon.setCorresponType(
                      page.corresponTypeService.findByProjectCorresponTypeId(id));

            id = page.correspon.getFromCorresponGroup().getId();
            page.correspon.setFromCorresponGroup(page.corresponGroupService.find(id));

            // Body情報を表示用Body情報に設定
            page.displayBody = page.correspon.getBody();
        }
    }

    /**
     * Saveアクション.
     * @author opentone
     */
    static class SaveAction extends AbstractAction {
        /**
         * serialVersionUID.
         */
        private static final long serialVersionUID = -281118687347151403L;
        /** アクション発生元ページ. */
        private CorresponConfirmationPage page;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page
         *            ページ
         */
        public SaveAction(CorresponConfirmationPage page) {
            super(page);
            this.page = page;
        }

        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            page.corresponValidateService.validate(page.correspon);
            if (log.isDebugEnabled()) {
                log.debug("Validattion successful.");
            }

            boolean isNew = page.correspon.isNew();
            Long id = page.corresponSaveService.save(page.correspon);
            if (log.isDebugEnabled()) {
                log.debug("Save successful.");
            }

            // 一時ファイルの削除
            cleanUp();

            page.correspon.setId(id);
            page.setNextPageMessage(ApplicationMessageCode.CORRESPON_SAVED);

            //イベント発火
            page.eventBus.raiseEvent(isNew
                    ? new CorresponCreated(id, page.correspon.getProjectId())
                    : new CorresponUpdated(id, page.correspon.getProjectId()));
        }

        /**
         * 一時ファイルを削除する.
         */
        private void cleanUp() {
            List<AttachmentInfo> saved = page.getAttachments();
            if (saved != null) {
                for (AttachmentInfo info : saved) {
                    info.delete();
                }
            }

            List<AttachmentInfo> deleted = page.getRemovedAttachments();
            if (deleted != null) {
                for (AttachmentInfo info : deleted) {
                    info.delete();
                }
            }
        }
    }

    /**
     * 学習用文書の表示用ラベルを返す.
     */
    public String getLearningCorresponTitleLabel() {
        return this.learningCorresponTitleLabel;
    }

    /**
     * 学習用文書の表示用ラベルを設定する.
     */
    public void setLearningCorresponTitleLabel(String learningCorresponTitle) {
        this.learningCorresponTitleLabel = learningCorresponTitle;
    }
}
