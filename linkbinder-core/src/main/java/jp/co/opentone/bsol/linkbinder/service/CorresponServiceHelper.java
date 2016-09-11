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
package jp.co.opentone.bsol.linkbinder.service;

import jp.co.opentone.bsol.framework.core.ProcessContext;
import jp.co.opentone.bsol.framework.core.config.SystemConfig;
import jp.co.opentone.bsol.framework.core.dao.Dao;
import jp.co.opentone.bsol.framework.core.dao.KeyDuplicateException;
import jp.co.opentone.bsol.framework.core.dao.RecordNotFoundException;
import jp.co.opentone.bsol.framework.core.dao.StaleRecordException;
import jp.co.opentone.bsol.framework.core.filestore.FileStoreClient;
import jp.co.opentone.bsol.framework.core.filestore.FileStoreException;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.core.util.DateUtil;
import jp.co.opentone.bsol.framework.core.util.PropertyGetUtil;
import jp.co.opentone.bsol.linkbinder.Constants;
import jp.co.opentone.bsol.linkbinder.dao.AddressCorresponGroupDao;
import jp.co.opentone.bsol.linkbinder.dao.AttachmentDao;
import jp.co.opentone.bsol.linkbinder.dao.CorresponCustomFieldDao;
import jp.co.opentone.bsol.linkbinder.dao.CorresponDao;
import jp.co.opentone.bsol.linkbinder.dao.CorresponTypeDao;
import jp.co.opentone.bsol.linkbinder.dao.CustomFieldDao;
import jp.co.opentone.bsol.linkbinder.dao.DaoFinder;
import jp.co.opentone.bsol.linkbinder.dao.LearningLabelDao;
import jp.co.opentone.bsol.linkbinder.dao.LearningTagDao;
import jp.co.opentone.bsol.linkbinder.dao.WorkflowDao;
import jp.co.opentone.bsol.linkbinder.dto.Attachment;
import jp.co.opentone.bsol.linkbinder.dto.Correspon;
import jp.co.opentone.bsol.linkbinder.dto.CorresponCustomField;
import jp.co.opentone.bsol.linkbinder.dto.CorresponType;
import jp.co.opentone.bsol.linkbinder.dto.CustomField;
import jp.co.opentone.bsol.linkbinder.dto.Project;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.Workflow;
import jp.co.opentone.bsol.linkbinder.dto.code.CorresponStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowStatus;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCustomFieldCondition;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.service.common.CorresponSequenceService;
import jp.co.opentone.bsol.linkbinder.service.correspon.LearningLabelService;
import jp.co.opentone.bsol.linkbinder.service.correspon.LearningTagService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 文書サービス関連のヘルパ.
 * @author opentone
 */
@Service
public class CorresponServiceHelper implements Serializable, ApplicationContextAware {

    /**
     * logger.
     */
    private Logger log = LoggerFactory.getLogger(getClass());

    /**
     * ファイル名フォーマット形式.
     */
    private static final String FILENAME_FORMAT = "%s_%s(%s).html";

    /**
     * ファイル名用IDフォーマット形式.
     */
    private static final String FILENAME_ID_FORMAT = "%010d";

    /**
     * ファイル名禁止文字をプロパティから取得するKEY.
     */
    private static final String FILENAME_KEY_REGEX = "file.name.regex";

    /**
     * ファイル名禁止文字がプロパティに設定されていない場合のデフォルト値.
     */
    private static final String FILENAME_DEFAULT_REGEX = "[\\\\/:*?\"<>|]";

    /**
     * ファイル名置換文字をプロパティから取得するするKEY.
     */
    private static final String FILENAME_KEY_REPLACEMENT = "file.name.replacement";

    /**
     * ファイル名禁止文字がプロパティに設定されていない場合のデフォルト値.
     */
    private static final String FILENAME_DEFAULT_REPLACEMENT = "-";
    /**
     * 添付ファイル名フォーマット形式(ディレクトリ名付き).
     */
    private static final String ATTACHMENT_FILENAME_FORMAT = "Attachments-%s_%s(%s)\\%s";

    /**
     * Spring frameworkのApplicationContext.
     */
    private ApplicationContext applicationContext;

    /**
     * Dao取得クラス.
     */
    @Resource
    private DaoFinder daoFinder;

    /**
     * ワークフローヘルパ.
     */
    @Resource
    private WorkflowHelper workflowHelper;

    /**
     * ログインユーザー.
     */
    @Resource
    //CHECKSTYLE:OFF
    protected User currentUser;
    //CHECKSTYLE:ON

    /**
     * コレポン文書番号採番サービス.
     */
    @Resource
    private CorresponSequenceService corresponSequenceService;

    /**
     * 学習用文書ラベルサービス.
     */
    @Resource
    private LearningLabelService learningLabelService;

    /**
     * 学習用文書タグサービス.
     */
    @Resource
    private LearningTagService learningTagService;

    /**
     * 指定されたDaoインターフェイスの実装クラスのオブジェクトを返す.
     * @param <T>
     *            対象のDaoインターフェイス
     * @param daoClass
     *            対象のDaoインターフェイス
     * @return 実装クラスのオブジェクト
     */
    @SuppressWarnings("unchecked")
    public <T extends Dao> T getDao(Class<?> daoClass) {
        return (T) daoFinder.getDao(daoClass);
    }

    /**
     * 現在ログイン中のユーザーを返す.
     * @return ログインユーザー
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * 現在選択中のプロジェクトIDを返す.
     * @return 現在選択中のプロジェクトID
     */
    public String getCurrentProjectId() {
        Project p = getCurrentProject();
        if (p == null) {
            return null;
        }
        return p.getProjectId();
    }

    /**
     * 現在選択中のプロジェクト情報を返す.
     * @return 現在選択中のプロジェクト情報
     */
    public Project getCurrentProject() {
        ProcessContext container = ProcessContext.getCurrentContext();

        Map<String, Object> values = container.getValue(SystemConfig.KEY_ACTION_VALUES);
        return (Project) values.get(Constants.KEY_PROJECT);
    }

    /**
     * FileStoreシステムと連携するためのオブジェクトを返す.
     * @return FileStore連携オブジェクト
     */
    private FileStoreClient getFileStoreClient() {
        return (FileStoreClient) applicationContext.getBean("fileStoreClient");
    }

    /**
     * 文書を検索して返す.
     * @param id 文書ID
     * @return 文書
     * @throws RecordNotFoundException 文書が見つからない
     * @throws ServiceAbortException 処理に失敗
     */
    public Correspon findCorrespon(Long id) throws RecordNotFoundException, ServiceAbortException {
        CorresponDao dao = getDao(CorresponDao.class);
        Correspon c = dao.findById(id);
        loadCorresponType(c);
        adjustCustomFields(c);

        return c;
    }


    /**
     * 文書の情報・関連情報に表示用の情報を付与して返す.
     * @param id 文書ID
     * @return 文書
     * @throws ServiceAbortException 取得エラー
     */
    public Correspon findCorresponDetailWithDisplayInfo(Long id) throws ServiceAbortException {
        Correspon c = findCorresponDetail(id);
        c.setWorkflows(createDisplayWorkflowList(c));

        return c;
    }

    /**
     * コレポン文書の情報を関連する情報と一緒に取得する.
     * @param id 文書ID
     * @return 文書
     * @throws ServiceAbortException 取得エラー
     */
    public Correspon findCorresponDetail(Long id) throws ServiceAbortException {
        try {
            Correspon c = findCorrespon(id);
            // 関連情報を取得
            loadAddressCorresponGroup(c);
            loadWorkflow(c);
            loadLearningLabelAndTag(c);

            return c;
        } catch (ServiceAbortException | RecordNotFoundException e) {
            throw new ServiceAbortException(ApplicationMessageCode.NO_DATA_FOUND);
        }
    }

    /**
     * コレポン文書が保持するワークフローにPreparerの情報を付与したリストを返す.
     * @see WorkflowHelper#createWorkflowListWithPreparer(Correspon)
     * @param correspon コレポン文書
     * @return 先頭にPreparerの情報を付与したワークフロー一覧
     */
    public List<Workflow> createDisplayWorkflowList(Correspon correspon) {
        return workflowHelper.createWorkflowListWithPreparer(correspon);
    }

    /**
     * コレポン文書を作成する.
     * @param correspon
     *            コレポン文書
     * @throws ServiceAbortException 作成失敗
     */
    public Long createCorrespon(Correspon correspon) throws ServiceAbortException {
        log.trace("①コレポン文書(correspon)");
        try {
            CorresponDao dao = getDao(CorresponDao.class);
            if (log.isDebugEnabled()) {
                log.debug("" + correspon);
            }
            return dao.create(correspon);
        } catch (KeyDuplicateException kde) {
            throw new ServiceAbortException(kde);
        }
    }

    /**
     * コレポン文書を更新する.
     * @param correspon
     *            コレポン文書
     * @throws ServiceAbortException 更新失敗
     */
    public void updateCorrespon(Correspon correspon) throws ServiceAbortException {
        try {
            CorresponDao dao = getDao(CorresponDao.class);
            if (log.isDebugEnabled()) {
                log.debug("" + correspon);
            }
            dao.update(correspon);
        } catch (StaleRecordException sre) {
            throw new ServiceAbortException(
                    ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CORRESPON_ALREADY_UPDATED);
        } catch (KeyDuplicateException kde) {
            throw new ServiceAbortException(kde);
        }
    }

    /**
     * コレポン文書を削除する.
     * @param correspon コレポン文書
     * @throws ServiceAbortException 削除に失敗
     */
    public void deleteCorrespon(Correspon correspon) throws ServiceAbortException {
        try {
            CorresponDao dao = getDao(CorresponDao.class);
            dao.delete(correspon);
        } catch (StaleRecordException e) {
            throw new ServiceAbortException(
                    ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CORRESPON_ALREADY_UPDATED);
        } catch (KeyDuplicateException e) {
            throw new ServiceAbortException(e);
        }
    }

    /**
     * コレポン文書の承認状態を更新する.
     * @param correspon
     *            コレポン文書
     * @throws ServiceAbortException 更新失敗
     */
    public void updateCorresponForIssue(Correspon correspon) throws ServiceAbortException {
        Correspon newCorrespon = setupNewCorresponForUpdate(correspon, WorkflowStatus.ISSUED);
        // コレポン文書番号を生成
        newCorrespon.setCorresponNo(corresponSequenceService.getCorresponNo(correspon));
        newCorrespon.setIssuedAt(DateUtil.getNow());
        newCorrespon.setIssuedBy(getCurrentUser());

        // コレポン文書の更新
        updateCorrespon(newCorrespon);
    }

    /**
     * 承認フローを更新する.
     * @param workflow
     *            承認フロー
     * @throws ServiceAbortException 更新失敗
     */
    public void updateWorkflow(Workflow workflow) throws ServiceAbortException {
        try {
            WorkflowDao dao = getDao(WorkflowDao.class);
            dao.update(workflow);
        } catch (StaleRecordException sre) {
            throw new ServiceAbortException(
                    ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CORRESPON_ALREADY_UPDATED);
        } catch (KeyDuplicateException kde) {
            throw new ServiceAbortException(kde);
        }
    }

    /**
     * コレポン文書-カスタムフィールドの登録処理.
     *
     * @param correspon コレポン文書
     * @throws ServiceAbortException 更新失敗
     */
    public void createCorresponCustomField(Correspon correspon) throws ServiceAbortException {
        //コレポン文書-カスタムフィールド
        CorresponCustomFieldDao corresponCustomFieldDao = getDao(CorresponCustomFieldDao.class);
        List<CorresponCustomField> corresponCustomFields = setupCorresponCustomFieldData(correspon);
        for (CorresponCustomField corresponCustomField : corresponCustomFields) {
            if (corresponCustomField == null) {
                continue;
            }

            corresponCustomField.setCorresponId(correspon.getId());
            corresponCustomField.setCreatedBy(correspon.getCreatedBy());
            corresponCustomField.setUpdatedBy(getCurrentUser());
            log.trace("③コレポン文書-カスタムフィールド(correspon_custom_field)");
            try {
                if (log.isDebugEnabled()) {
                    log.debug("" + corresponCustomField);
                }
                corresponCustomFieldDao.create(corresponCustomField);
            } catch (KeyDuplicateException e) {
                throw new ServiceAbortException(e);
            }
        }
    }

    public void saveLearningLabel(Correspon correspon) throws ServiceAbortException {
        if (correspon.isLearningContents()) {
            learningLabelService.saveLearningLabels(correspon);
        } else {
            learningLabelService.clearAllLearningLabels(correspon);
        }
    }

    public void saveLearningTag(Correspon correspon) throws ServiceAbortException {
        if (correspon.isLearningContents()) {
            learningTagService.saveLearningTags(correspon);
        } else {
            learningTagService.clearAllLearningTags(correspon);
        }
    }

    public void saveAttachments(Correspon correspon) throws ServiceAbortException {
        List<Attachment> attachments = correspon.getUpdateAttachments();
        if (attachments == null) {
            return;
        }

        for (Attachment a : attachments) {
            a.setCorresponId(correspon.getId());
            a.setFileType(Attachment.detectFileTypeByFileName(a.getFileName()));
            a.setCreatedBy(getCurrentUser());
            a.setUpdatedBy(getCurrentUser());

            processSaveAttachment(a);
        }
    }

    /**
     * ZIPファイル内のHTMLファイルの名前を作成する.
     * <pre>
     * ファイル名：
     *     projectId_コレポン文書番号(コレポン文書ID).html
     *     ただし未発行の場合はprojectId_(コレポン文書ID).html
     *         ・コレポン文書IDは10桁前ゼロ埋め
     *         ・コレポン文書番号の「:」はWindowsのファイル名として使用できない文字なので、「-」に変換
     *         ・その他Windowsのファイル名として使用できない文字があれば「-」に変換
     * </pre>
     * @return HTMLファイル名
     */
    public String createCorresponHtmlName(Correspon correspon) {
        String corresponNo = correspon.getCorresponNo();
        if (corresponNo == null) { // NULLならば空文字に変換
            corresponNo = "";
        }
        String corresponId = String.format(FILENAME_ID_FORMAT, correspon.getId());

        String fileName = String.format(FILENAME_FORMAT,
                getCurrentProjectId(),
                corresponNo,
                corresponId);
        return convertFileName(fileName);
    }

    /**
     * ディレクトリ名を作成する.
     * @return ATTACHMENT名
     */
    public String createAttachmentFileName(Correspon correspon, String filename) {
        String corresponNo = correspon.getCorresponNo();
        if (corresponNo == null) { // NULLならば空文字に変換
            corresponNo = "";
        }
        String corresponId = String.format(FILENAME_ID_FORMAT, correspon.getId());

        String fileName = String.format(ATTACHMENT_FILENAME_FORMAT,
                getCurrentProjectId(),
                corresponNo,
                corresponId,
                filename);
        return convertFileName(fileName);
    }


    /**
     * Windowsのファイル名として使用できない文字を変換する.
     * @return ファイル名
     */
    public String convertFileName(String name) {
        String regex = SystemConfig.getValue(FILENAME_KEY_REGEX);
        if (StringUtils.isEmpty(regex)) {
            regex = FILENAME_DEFAULT_REGEX;
        }
        String replacement = SystemConfig.getValue(FILENAME_KEY_REPLACEMENT);
        if (replacement == null) { // 置換文字は空文字でも問題ない
            replacement = FILENAME_DEFAULT_REPLACEMENT;
        }
        return name.replaceAll(regex, replacement);
    }

    public void loadAddressCorresponGroup(Correspon c) {
        AddressCorresponGroupDao dao = getDao(AddressCorresponGroupDao.class);
        c.setAddressCorresponGroups(dao.findByCorresponId(c.getId()));
    }

    public void loadWorkflow(Correspon c) {
        WorkflowDao dao = getDao(WorkflowDao.class);
        List<Workflow> workflows = dao.findByCorresponId(c.getId());
        setupCorresponGroup(workflows);

        c.setWorkflows(workflows);
    }

    public void loadLearningLabelAndTag(Correspon c) {
        LearningLabelDao labelDao = getDao(LearningLabelDao.class);
        c.setLearningLabel(labelDao.findByCorresponId(c.getId()));

        LearningTagDao tagDao = getDao(LearningTagDao.class);
        c.setLearningTag(tagDao.findByCorresponId(c.getId()));
    }

    private void setupCorresponGroup(List<Workflow> workflows) {
        if (workflows == null) {
            return;
        }
        for (Workflow w : workflows) {
            w.setCorresponGroup(workflowHelper.findPrimaryCorresponGroup(w.getUser()));
        }
    }

    private void processSaveAttachment(Attachment attachment) throws ServiceAbortException {
        switch (attachment.getMode()) {
            case NEW:
                String fileStoreFileId = saveAttachmentContent(attachment);
                attachment.setFileId(fileStoreFileId);
                createAttachment(attachment);
                break;
            case UPDATE:
                updateAttachment(attachment);
                break;
            case DELETE :
                deleteAttachment(attachment);
                break;
            default:
                // 添付ファイルはNEW、DELETEのいずれかしかあり得ない (UPDATEは無し)
                break;
        }
    }

    /**
     * 添付ファイルをFileStoreシステムに保存する.
     * @param attachment 添付ファイル
     * @return FileStoreシステムが発番したオブジェクトID
     * @throws ServiceAbortException 保存に失敗
     */
    private String saveAttachmentContent(Attachment attachment)
            throws ServiceAbortException {

        FileStoreClient fileStore = getFileStoreClient();
        try {
            String result = fileStore.createFile(attachment.getSourcePath());
            if (log.isDebugEnabled()) {
                log.debug("Save an attachment to FileStore. {}, {}", attachment.getFileName(), result);
            }
            return result;
        } catch (FileStoreException e) {
            throw new ServiceAbortException(e.getMessage(),
                    e,
                    ApplicationMessageCode.ERROR_SAVING_FILE);
        }
    }

    /**
     * 添付ファイルを更新する.
     * @param attachment
     *            添付ファイル
     * @throws ServiceAbortException 更新失敗
     */
    public void updateAttachment(Attachment attachment) throws ServiceAbortException {
        try {
            AttachmentDao dao = getDao(AttachmentDao.class);
            dao.update(attachment);
        } catch (StaleRecordException sre) {
            throw new ServiceAbortException(
                    ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CORRESPON_ALREADY_UPDATED);
        } catch (KeyDuplicateException kde) {
            throw new ServiceAbortException(kde);
        }
    }

    /**
     * 添付ファイルを削除する.
     * @param attachment
     *            添付ファイル
     * @throws ServiceAbortException 更新失敗
     */
    public void deleteAttachment(Attachment attachment) throws ServiceAbortException {
        try {
            AttachmentDao dao = getDao(AttachmentDao.class);
            dao.delete(attachment);
        } catch (StaleRecordException sre) {
            throw new ServiceAbortException(
                    ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CORRESPON_ALREADY_UPDATED);
        } catch (KeyDuplicateException kde) {
            throw new ServiceAbortException(kde);
        }
    }

    /**
     * 添付ファイルを作成する.
     * @param attachment
     *            添付ファイル
     * @throws ServiceAbortException 更新失敗
     */
    public void createAttachment(Attachment attachment) throws ServiceAbortException {
        log.trace("②添付ファイル(attachment)");
        try {
            AttachmentDao dao = getDao(AttachmentDao.class);
            if (log.isDebugEnabled()) {
                log.debug("" + attachment);
            }
            dao.create(attachment);
        } catch (KeyDuplicateException e) {
            throw new ServiceAbortException(e);
        }
    }

    /**
     * 更新用のコレポン文書Dtoを生成する.
     * @param oldCorrespon
     *            コレポン文書
     * @param workflowStatus
     *            承認状態
     * @return 更新用コレポン文書
     */
    public Correspon setupNewCorresponForUpdate(Correspon oldCorrespon, WorkflowStatus workflowStatus) {
        Correspon newCorrespon = new Correspon();
        newCorrespon.setId(oldCorrespon.getId());
        newCorrespon.setUpdatedBy(getCurrentUser());
        newCorrespon.setWorkflowStatus(workflowStatus);
        newCorrespon.setVersionNo(oldCorrespon.getVersionNo());

        return newCorrespon;
    }

    /**
     * 文書状態削除用のオブジェクトを作成する.
     * @param old
     *            削除対象のコレポン文書
     * @return 削除用のオブジェクト
     */
    public Correspon setupCorresponForDelete(Correspon old) {
        Correspon correspon = new Correspon();
        correspon.setId(old.getId());
        correspon.setUpdatedBy(getCurrentUser());
        correspon.setVersionNo(old.getVersionNo());
        return correspon;
    }

    /**
     * コレポン入力情報CorresponCustomFieldをListに設定.
     *
     * @param correspon
     *            コレポン文書情報
     * @return CorresponCustomField情報
     * @throws ServiceAbortException 設定失敗
     */
    private List<CorresponCustomField> setupCorresponCustomFieldData(Correspon correspon)
            throws ServiceAbortException {
        List<CorresponCustomField> result = new ArrayList<>();
        setupCorresponCustomField(result,
                correspon.getCustomField1Id(),
                correspon.getCustomField1Value());
        setupCorresponCustomField(result,
                correspon.getCustomField2Id(),
                correspon.getCustomField2Value());
        setupCorresponCustomField(result,
                correspon.getCustomField3Id(),
                correspon.getCustomField3Value());
        setupCorresponCustomField(result,
                correspon.getCustomField4Id(),
                correspon.getCustomField4Value());
        setupCorresponCustomField(result,
                correspon.getCustomField5Id(),
                correspon.getCustomField5Value());
        setupCorresponCustomField(result,
                correspon.getCustomField6Id(),
                correspon.getCustomField6Value());
        setupCorresponCustomField(result,
                correspon.getCustomField7Id(),
                correspon.getCustomField7Value());
        setupCorresponCustomField(result,
                correspon.getCustomField8Id(),
                correspon.getCustomField8Value());
        setupCorresponCustomField(result,
                correspon.getCustomField9Id(),
                correspon.getCustomField9Value());
        setupCorresponCustomField(result,
                correspon.getCustomField10Id(),
                correspon.getCustomField10Value());
        return result;
    }

    private void setupCorresponCustomField(
            List<CorresponCustomField> fields,
            Long projectCustomFieldId,
            String value) {

        if (projectCustomFieldId != null) {
            CorresponCustomField f = new CorresponCustomField();
            f.setProjectCustomFieldId(projectCustomFieldId);
            f.setValue(value);
            fields.add(f);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    private void loadCorresponType(Correspon c) throws RecordNotFoundException {
        CorresponTypeDao dao = getDao(CorresponTypeDao.class);
        CorresponType ct =
                dao.findByProjectCorresponTypeId(
                        c.getCorresponType().getProjectCorresponTypeId());
        c.setCorresponType(ct);
    }

    public void adjustCustomFields(Correspon c) throws ServiceAbortException {
        SearchCustomFieldCondition condition = new SearchCustomFieldCondition();
        condition.setProjectId(c.getProjectId());
        CustomFieldDao dao = getDao(CustomFieldDao.class);
        List<CustomField> fields = dao.findByProjectId(condition);
        List<CorresponCustomField> currentValues = collectCurrentCorresponCustomField(c);

        final String idProperty = "customField%dId";
        final String labelProperty = "customField%dLabel";
        final String valueProperty = "customField%dValue";
        int maxCount =
                Integer.parseInt(SystemConfig.getValue(Constants.KEY_CUSTOM_FIELD_MAX_COUNT));
        int max = fields.size() > maxCount ? maxCount : fields.size();

        //  現在のカスタムフィールドマスタの並び順で並べ換える
        int i = 0;
        for (; i < max; i++) {
            CustomField f = fields.get(i);

            PropertyGetUtil.setProperty(
                    c, String.format(idProperty, (i + 1)), f.getProjectCustomFieldId());
            PropertyGetUtil.setProperty(
                    c, String.format(labelProperty, (i + 1)), f.getLabel());

            String value = null;
            for (CorresponCustomField v : currentValues) {
                if (f.getProjectCustomFieldId().equals(v.getProjectCustomFieldId())) {
                    value = v.getValue();
                    break;
                }
            }
            PropertyGetUtil.setProperty(
                    c, String.format(valueProperty, (i + 1)), value);
        }
        for (; i < maxCount; i++) {
            PropertyGetUtil.setProperty(
                    c, String.format(idProperty, (i + 1)), null);
            PropertyGetUtil.setProperty(
                    c, String.format(labelProperty, (i + 1)), null);
            PropertyGetUtil.setProperty(
                    c, String.format(valueProperty, (i + 1)), null);
        }
    }

    //  カスタムフィールド項目数が多いため行数オーバーする
    //CHECKSTYLE:OFF
    private List<CorresponCustomField> collectCurrentCorresponCustomField(Correspon c) {
        List<CorresponCustomField> fields = new ArrayList<>();
        CorresponCustomField f;
        if (c.getCustomField1Id() != null) {
            f = new CorresponCustomField();
            f.setProjectCustomFieldId(c.getCustomField1Id());
            f.setValue(c.getCustomField1Value());
            fields.add(f);
        }
        if (c.getCustomField2Id() != null) {
            f = new CorresponCustomField();
            f.setProjectCustomFieldId(c.getCustomField2Id());
            f.setValue(c.getCustomField2Value());
            fields.add(f);
        }
        if (c.getCustomField3Id() != null) {
            f = new CorresponCustomField();
            f.setProjectCustomFieldId(c.getCustomField3Id());
            f.setValue(c.getCustomField3Value());
            fields.add(f);
        }
        if (c.getCustomField4Id() != null) {
            f = new CorresponCustomField();
            f.setProjectCustomFieldId(c.getCustomField4Id());
            f.setValue(c.getCustomField4Value());
            fields.add(f);
        }
        if (c.getCustomField5Id() != null) {
            f = new CorresponCustomField();
            f.setProjectCustomFieldId(c.getCustomField5Id());
            f.setValue(c.getCustomField5Value());
            fields.add(f);
        }
        if (c.getCustomField6Id() != null) {
            f = new CorresponCustomField();
            f.setProjectCustomFieldId(c.getCustomField6Id());
            f.setValue(c.getCustomField6Value());
            fields.add(f);
        }
        if (c.getCustomField7Id() != null) {
            f = new CorresponCustomField();
            f.setProjectCustomFieldId(c.getCustomField7Id());
            f.setValue(c.getCustomField7Value());
            fields.add(f);
        }
        if (c.getCustomField8Id() != null) {
            f = new CorresponCustomField();
            f.setProjectCustomFieldId(c.getCustomField8Id());
            f.setValue(c.getCustomField8Value());
            fields.add(f);
        }
        if (c.getCustomField9Id() != null) {
            f = new CorresponCustomField();
            f.setProjectCustomFieldId(c.getCustomField9Id());
            f.setValue(c.getCustomField9Value());
            fields.add(f);
        }
        if (c.getCustomField10Id() != null) {
            f = new CorresponCustomField();
            f.setProjectCustomFieldId(c.getCustomField10Id());
            f.setValue(c.getCustomField10Value());
            fields.add(f);
        }
        return fields;
    }
    //CHECKSTYLE:ON

    /**
     * 文書状態(Open/Closed/Canceled)チェック.
     * <p>
     * チェック定義書の「文書状態(Open/Closed/Canceled)チェック」に記述された一連のチェックを行う<br />
     * 【新規追加】ただし、新規登録の際の文書状態チェックは「Canceledか否か」のチェックのみ行う
     * </p>
     * @param correspon
     *            コレポン文書情報
     * @throws ServiceAbortException 検証エラー
     */
    public void commonValidateCorresponStatus(Correspon correspon) throws ServiceAbortException {
        if (correspon.isNew()) { // コレポン文書の文書状態が[2:Canceled]の際はエラー
            log.trace("★文書状態(新規)");
            if (log.isDebugEnabled()) {
                log.debug("correspon.getCorresponStatus[" + correspon.getCorresponStatus() + "]");
            }
            validateCorresponStatusCanceled(correspon.getCorresponStatus());
        } else { // 更新前コレポン文書の文書状態が[2:Canceled]の際はエラー
            log.trace("★文書状態(更新)①");
            if (log.isDebugEnabled()) {
                log.debug("correspon.getId[" + correspon.getId() + "]");
            }
            Correspon oldCorrespon;
            try {
                oldCorrespon = findCorrespon(correspon.getId());
            } catch (RecordNotFoundException e) {
                log.warn(e.getMessageCode() + " correspon.getId[" + correspon.getId() + "]");
                throw new ServiceAbortException(ApplicationMessageCode.NO_DATA_FOUND);
            }
            if (log.isDebugEnabled()) {
                log.debug("oldCorrespon.getCorresponStatus[" + oldCorrespon.getCorresponStatus()
                        + "]");
            }
            if (CorresponStatus.CANCELED.equals(oldCorrespon.getCorresponStatus())) {
                log.warn(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CORREPON_CANCELED + " "
                        + oldCorrespon);
                throw new ServiceAbortException(
                        ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CORREPON_CANCELED);
            }
            // コレポン文書の承認状態が[5:Issued]以外で、文書状態を[2:Canceled]にする際はエラー
            log.trace("★文書状態(更新)②");
            if (log.isDebugEnabled()) {
                log.debug("oldCorrespon.getWorkflowStatus[" + oldCorrespon.getWorkflowStatus()
                        + "]");
                log.debug("correspon.getCorresponStatus[" + correspon.getCorresponStatus() + "]");
            }
            if (!WorkflowStatus.ISSUED.equals(oldCorrespon.getWorkflowStatus())
                    && CorresponStatus.CANCELED.equals(correspon.getCorresponStatus())) {
                log
                        .warn(ApplicationMessageCode
                                .CANNOT_PERFORM_BECAUSE_CORREPON_NOT_ISSUED_AND_CANCELED
                                + " " + oldCorrespon);
                throw new ServiceAbortException(
                        ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CORREPON_NOT_ISSUED_AND_CANCELED);
            }
        }
    }

    /**
     * 文書状態がCanceledかチェックする.
     * @param corresponStatus
     *            文書状態
     * @throws ServiceAbortException
     *             文書状態がCanceled
     */
    public void validateCorresponStatusCanceled(CorresponStatus corresponStatus)
            throws ServiceAbortException {
        if (CorresponStatus.CANCELED == corresponStatus) {
            throw new ServiceAbortException(
                    ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CORREPON_CANCELED);
        }
    }
}
