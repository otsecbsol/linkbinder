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
package jp.co.opentone.bsol.linkbinder.service.admin.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.co.opentone.bsol.framework.core.config.SystemConfig;
import jp.co.opentone.bsol.framework.core.dao.KeyDuplicateException;
import jp.co.opentone.bsol.framework.core.dao.RecordNotFoundException;
import jp.co.opentone.bsol.framework.core.dao.StaleRecordException;
import jp.co.opentone.bsol.framework.core.generator.GeneratorFailedException;
import jp.co.opentone.bsol.framework.core.generator.excel.WorkbookGenerator;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.core.util.ArgumentValidator;
import jp.co.opentone.bsol.linkbinder.dao.CorresponDao;
import jp.co.opentone.bsol.linkbinder.dao.CustomFieldDao;
import jp.co.opentone.bsol.linkbinder.dao.CustomFieldValueDao;
import jp.co.opentone.bsol.linkbinder.dao.ProjectCustomFieldDao;
import jp.co.opentone.bsol.linkbinder.dto.CustomField;
import jp.co.opentone.bsol.linkbinder.dto.CustomFieldValue;
import jp.co.opentone.bsol.linkbinder.dto.ProjectCustomField;
import jp.co.opentone.bsol.linkbinder.dto.SearchCustomFieldResult;
import jp.co.opentone.bsol.linkbinder.dto.code.UseWhole;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCustomFieldCondition;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.service.AbstractService;
import jp.co.opentone.bsol.linkbinder.service.admin.CustomFieldService;

/**
 * このサービスではカスタムフィールドに関する処理を提供する.
 * @author opentone
 */
@Service
@Transactional(rollbackFor = { ServiceAbortException.class })
public class CustomFieldServiceImpl extends AbstractService implements CustomFieldService {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 4004774537778286277L;

    /**
     * プロパティに設定されたEXCELシート名のKEY.
     */
    private static final String SHEET_KEY = "excel.sheetname.customfieldindex";

    /**
     * EXCELシート名がプロパティに設定されていない場合のデフォルト名.
     */
    private static final String SHEET_DEFAULT = "CustomFieldIndex";

    /**
     * カスタムフィールド件数.
     */
    private int dataCount;

    /**
     * Excel出力する際のヘッダ名.
     */
    private static final List<String> HEADER;
    static {
        HEADER = new ArrayList<String>();
        HEADER.add("ID");
        HEADER.add("Label");
        HEADER.add("Order No.");
    }

    /**
     * Excel出力する際の出力項目.
     */
    private static final List<String> FIELDS;
    static {
        FIELDS = new ArrayList<String>();
        FIELDS.add("id");
        FIELDS.add("label");
        FIELDS.add("orderNo");
    }

    /**
     * Excel出力する際の出力項目.
     */
    private static final List<String> PROJECT_FIELDS;
    static {
        PROJECT_FIELDS = new ArrayList<String>();
        PROJECT_FIELDS.add("projectCustomFieldId");
        PROJECT_FIELDS.add("label");
        PROJECT_FIELDS.add("orderNo");
    }

    /**
     * 空のインスタンスを生成する.
     */
    public CustomFieldServiceImpl() {
    }

    /*
     * (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.service.admin.CustomFieldService#findCustomFieldValue
     * (java.lang.Long)
     */
    public List<CustomFieldValue> findCustomFieldValue(Long customFieldId) {
        ArgumentValidator.validateNotNull(customFieldId);
        return findByCustomFieldId(customFieldId);
    }

    /*
     * (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.service.admin.CustomFieldService#search(jp.co.opentone.bsol.linkbinder
     * .dto.condition.SearchCustomFieldCondition)
     */
    public List<CustomField> search(SearchCustomFieldCondition condition) {
        ArgumentValidator.validateNotNull(condition);
        return searchCustomFieldCondition(condition);
    }

    /*
     * (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.service.admin.CustomFieldService#assignTo(
     *      jp.co.opentone.bsol.linkbinder.dto.CustomField)
     */
    public Long assignTo(CustomField customField) throws ServiceAbortException {
        ArgumentValidator.validateNotNull(customField);
        validatePermission();
        // 指定のカスタムフィールド情報が存在しない場合はエラー
        findById(customField.getId());
        // 指定のプロジェクトに同じカスタムフィールド情報が既に紐付いてる際はエラー
        validateExistCustomFieldProject(customField.getId());

        // 指定のプロジェクトに同じラベルが登録されている際はエラー
        validateExistLabel(customField);

        // プロジェクトに追加する
        return insertProjectCustomField(createInsertProjectCustomField(customField));
    }

    /*
     * (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.service.admin.CustomFieldService#delete(
     *      jp.co.opentone.bsol.linkbinder.dto.CustomField)
     */
    public void delete(CustomField customField) throws ServiceAbortException {
        ArgumentValidator.validateNotNull(customField);
        if (isAdminHome()) {
            validateSystemAdmin();
            // 該当データ0件の場合はエラー
            findById(customField.getId());
            // 削除対象のカスタムフィールドが、プロジェクトに関連付けられている場合はエラー
            validateRelationProject(customField);
            deleteCustomField(createDeleteCustomField(customField));
        } else {
            validatePermission();
            // 該当データ0件の場合はエラー
            findByIdProjectId(customField.getId());
            // 削除対象のカスタムフィールドが、登録済みのコレポン文書に関連付けられている場合はエラー
            validateRelationCorrespon(customField);
            // カスタムフィールドのプロジェクトが現在選択中のプロジェクト以外はエラー
            validateProjectId(customField.getProjectId());
            // プロジェクトカスタムフィールド削除処理（更新）
            deleteProjectCustomField(createDeleteProjectCustomField(customField));
            updateCustomField(createUpdateVersionNo(customField));
        }
    }

    /*
     * (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.service.admin.CustomFieldService#find(java.lang.Long)
     */
    @Transactional(readOnly = true)
    public CustomField find(Long id) throws ServiceAbortException {
        ArgumentValidator.validateNotNull(id);
        CustomField customField = null;
        if (isAdminHome()) {
            customField = findById(id);
        } else {
            customField = findByIdProjectId(id);
            // カスタムフィールドのプロジェクトが現在選択中のプロジェクト以外はエラー
            validateProjectId(customField.getProjectId());
        }
        return customField;
    }

    /*
     * (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.service.admin.CustomFieldService#generateExcel(java.
     * util.List)
     */
    @Transactional(readOnly = true)
    public byte[] generateExcel(List<CustomField> customFields) throws ServiceAbortException {
        ArgumentValidator.validateNotNull(customFields);
        try {
            String sheetName = SystemConfig.getValue(SHEET_KEY);
            if (StringUtils.isEmpty(sheetName)) {
                sheetName = SHEET_DEFAULT;
            }
            WorkbookGenerator generator =
                    new WorkbookGenerator(
                        sheetName, customFields, getOutputField(), getHeaderNames(), true);
            return generator.generate();
        } catch (GeneratorFailedException e) {
            throw new ServiceAbortException(e.getMessage());
        }
    }

    /*
     * (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.service.admin.CustomFieldService#save(
     *      jp.co.opentone.bsol.linkbinder.dto.CustomField)
     */
    public Long save(CustomField customField) throws ServiceAbortException {
        ArgumentValidator.validateNotNull(customField);
        validate(customField);
        Long saveId = null;
        if (isAdminHome()) {
            saveId = saveAdminHome(customField);
        } else {
            saveId = saveProjectAdminHome(customField);
        }
        // カスタムフィールド設定値を保存
        deleteInsertCustomFieldValue(customField, saveId);
        return saveId;
    }

    /*
     * (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.service.admin.CustomFieldService#searchNotAssigned()
     */
    @Transactional(readOnly = true)
    public List<CustomField> searchNotAssigned() throws ServiceAbortException {
        return findNotAssignTo();
    }

    /*
     * (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.service.admin.CustomFieldService#searchPagingList(
     *      jp.co.opentone.bsol.linkbinder.dto.condition.SearchCustomFieldCondition)
     */
    @Transactional(readOnly = true)
    public SearchCustomFieldResult searchPagingList(SearchCustomFieldCondition condition)
            throws ServiceAbortException {
        ArgumentValidator.validateNotNull(condition);
        if (isAdminHome()) {
            validateSystemAdmin();
        } else {
            validatePermission();
        }
        // 該当データ0件の場合はエラー
        validateExistCustomField(condition);
        List<CustomField> customFields = findCustomField(condition);
        // 指定のページを選択した際にそのページに表示するデータが存在しない場合はエラー
        validatePageNoData(customFields);
        return createResult(customFields);
    }

    /*
     * (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.service.admin.CustomFieldService#validate(
     *      jp.co.opentone.bsol.linkbinder.dto.CustomField)
     */
    @Transactional(readOnly = true)
    public boolean validate(CustomField customField) throws ServiceAbortException {
        ArgumentValidator.validateNotNull(customField);
        validateExistCustomFieldValue(customField.getCustomFieldValues());
        if (isAdminHome()) {
            validateSystemAdmin();
            // 有効なカスタムフィールドの同じ項目名（label）が既に登録されている際はエラー
            validateExistLabel(customField);
            if (!customField.isNew()) {
                // 該当データが0件の場合はエラー
                findById(customField.getId());
            }
        } else {
            validatePermission();
            // カスタムフィールドのプロジェクトが現在選択中のプロジェクトと違う場合はエラー
            validateProjectId(customField.getProjectId());
            // 有効なカスタムフィールドの同じ項目名（label）が既に登録されている際はエラー
            validateExistLabel(customField);
            if (!customField.isNew()) {
                // 該当データ0件の場合はエラー
                findProjectCustomFieldById(customField.getProjectCustomFieldId());
            }
        }
        return true;
    }

    /**
     * 条件を指定してカスタムフィールドを取得する.
     * @param condition
     *            検索条件
     * @return カスタムフィールド
     */
    private List<CustomField> searchCustomFieldCondition(SearchCustomFieldCondition condition) {
        CustomFieldDao dao = getDao(CustomFieldDao.class);
        return dao.findByProjectId(condition);
    }

    /**
     * カスタムフィールドIDを指定してかステムフィールド設定値を取得する.
     * @param customFieldId
     *            カスタムフィールドID
     * @return カスタムフィールド設定値
     */
    private List<CustomFieldValue> findByCustomFieldId(Long customFieldId) {
        CustomFieldValueDao dao = getDao(CustomFieldValueDao.class);
        return dao.findByCustomFieldId(customFieldId);
    }

    /**
     * カスタムフィールド一覧画面の出力パラメータを取得する.
     * @return 出力パラメータ
     */
    private List<String> getOutputField() {
        if (StringUtils.isEmpty(getCurrentProjectId())) {
            return FIELDS;
        } else {
            return PROJECT_FIELDS;
        }
    }

    /**
     * カスタムフィールド一覧画面のヘッダ名を取得する.
     * @return ヘッダ名
     */
    private List<String> getHeaderNames() {
        return HEADER;
    }

    /**
     * SystemAdminではない場合はエラー.
     * @throws ServiceAbortException SystemAdminではない
     */
    private void validateSystemAdmin() throws ServiceAbortException {
        if (!isSystemAdmin(getCurrentUser())) {
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT);
        }
    }

    /**
     * 権限があるか判定する.
     * @return 権限があるtrue / 権限がないfalse
     */
    private boolean isValidPermission() {
        return isSystemAdmin(getCurrentUser())
            || isProjectAdmin(getCurrentUser(), getCurrentProjectId());
    }

    /**
     * 権限チェックを行う.
     * @throws ServiceAbortException 権限がない
     */
    private void validatePermission() throws ServiceAbortException {
        if (!isValidPermission()) {
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT);
        }
    }

    /**
     * カスタムフィールドが存在するか判定する.
     * @param condition 検索条件
     * @return 存在するtrue / 存在しないfalse
     */
    private boolean isExistCustomField(SearchCustomFieldCondition condition) {
        dataCount = countCustomField(condition);
        return dataCount > 0;
    }

    /**
     * 該当データが存在するかチェックする.
     * @param condition 検索条件
     * @throws ServiceAbortException 該当データが存在しない
     */
    private void validateExistCustomField(SearchCustomFieldCondition condition)
            throws ServiceAbortException {
        if (!isExistCustomField(condition)) {
            throw new ServiceAbortException(ApplicationMessageCode.NO_DATA_FOUND);
        }
    }

    /**
     * カスタムフィールド件数を取得する.
     * @param condition 検索条件
     * @return int カスタムフィールド件数
     */
    private int countCustomField(SearchCustomFieldCondition condition) {
        CustomFieldDao dao = getDao(CustomFieldDao.class);
        return dao.count(condition);
    }

    /**
     * 検索条件に該当するカスタムフィールドを取得する.
     * @param condition 検索条件
     * @return カスタムフィールド
     */
    private List<CustomField> findCustomField(SearchCustomFieldCondition condition) {
        CustomFieldDao dao = getDao(CustomFieldDao.class);
        return dao.find(condition);
    }

    /**
     * 指定のページを選択した際にそのページに表示するデータが存在するかチェックする.
     * @param customFields カスタムフィールド
     * @throws ServiceAbortException データが存在しない
     */
    private void validatePageNoData(List<CustomField> customFields) throws ServiceAbortException {
        if (customFields.isEmpty()) {
            throw new ServiceAbortException(ApplicationMessageCode.NO_PAGE_FOUND);
        }
    }

    /**
     * 一覧画面表示用オブジェクトを作成する.
     * @param customFields カスタムフィールド
     * @return 一覧画面表示用オブジェクト
     */
    private SearchCustomFieldResult createResult(List<CustomField> customFields) {
        SearchCustomFieldResult result = new SearchCustomFieldResult();
        result.setCount(dataCount);
        result.setCustomFieldList(customFields);
        return result;
    }

    /**
     * 現在のプロジェクトに属さないカスタムフィールドを取得する.
     * @return 現在のプロジェクトに属さないカスタムフィールド
     */
    private List<CustomField> findNotAssignTo() {
        CustomFieldDao dao = getDao(CustomFieldDao.class);
        return dao.findNotAssignTo(getCurrentProjectId());
    }

    /**
     * IDを指定してProjectCusotmFieldを取得する.
     * @return プロジェクトカスタムフィールド
     * @throws ServiceAbortException 取得失敗
     */
    private ProjectCustomField findProjectCustomFieldById(Long id) throws ServiceAbortException {
        try {
            ProjectCustomFieldDao dao = getDao(ProjectCustomFieldDao.class);
            return dao.findById(id);
        } catch (RecordNotFoundException rnfe) {
            throw new ServiceAbortException(ApplicationMessageCode.NO_DATA_FOUND);
        }
    }

    /**
     * IDを指定してカスタムフィールドを取得.
     * @param id ID
     * @return カスタムフィールド
     * @throws ServiceAbortException 取得失敗
     */
    private CustomField findById(Long id) throws ServiceAbortException {
        try {
            CustomFieldDao dao = getDao(CustomFieldDao.class);
            return dao.findById(id);
        } catch (RecordNotFoundException rnfe) {
            throw new ServiceAbortException(ApplicationMessageCode.NO_DATA_FOUND);
        }
    }

    /**
     * 既にカスタムフィールドがプロジェクトに紐付いてるか判定する.
     * @param id ID
     * @return 紐付いているtrue / 紐付いていないfalse
     */
    private boolean isExistCustomFieldProject(Long id) {
        try {
            findByIdProjectId(id);
            return true;
        } catch (ServiceAbortException sae) {
            return false;
        }
    }

    /**
     * 既にカスタムフィールドがプロジェクトに紐付いている場合はエラー.
     * @param id ID
     * @throws ServiceAbortException 紐付いている
     */
    private void validateExistCustomFieldProject(Long id) throws ServiceAbortException {
        if (isExistCustomFieldProject(id)) {
            throw new ServiceAbortException(
                ApplicationMessageCode.
                    CANNOT_PERFORM_BECAUSE_CUSTOM_FIELD_ALREADY_ASSIGNED_TO_PROJECT);
        }
    }

    /**
     * IDとProjectIDを指定してカスタムフィールドを取得する.
     * @param id ID
     * @return カスタムフィールド
     * @throws ServiceAbortException 取得失敗
     */
    private CustomField findByIdProjectId(Long id) throws ServiceAbortException {
        SearchCustomFieldCondition condition = new SearchCustomFieldCondition();
        condition.setId(id);
        condition.setProjectId(getCurrentProjectId());
        try {
            CustomFieldDao dao = getDao(CustomFieldDao.class);
            return dao.findByIdProjectId(condition);
        } catch (RecordNotFoundException rnfe) {
            throw new ServiceAbortException(ApplicationMessageCode.NO_DATA_FOUND);
        }

    }

    /**
     * プロジェクトカスタムフィールドを登録する.
     * @param projectCustomField プロジェクトカスタムフィールド
     * @return 登録したID
     * @throws ServiceAbortException 登録失敗
     */
    private Long insertProjectCustomField(ProjectCustomField projectCustomField)
            throws ServiceAbortException {
        try {
            ProjectCustomFieldDao dao = getDao(ProjectCustomFieldDao.class);
            return dao.create(projectCustomField);
        } catch (KeyDuplicateException kde) {
            throw new ServiceAbortException(kde);
        }
    }

    /**
     * カスタムフィールドを登録する.
     * @param customField カスタムフィールド
     * @return 登録したID
     * @throws ServiceAbortException 登録失敗
     */
    private Long insertCustomField(CustomField customField) throws ServiceAbortException {
        try {
            CustomFieldDao dao = getDao(CustomFieldDao.class);
            return dao.create(customField);
        } catch (KeyDuplicateException kde) {
            throw new ServiceAbortException(kde);
        }
    }

    /**
     * 登録用プロジェクトカスタムフィールドオブジェクトを作成する.
     * @param customField カスタムフィールド
     * @return プロジェクトカスタムフィールド
     */
    private ProjectCustomField createInsertProjectCustomField(CustomField customField) {
        ProjectCustomField pcf = new ProjectCustomField();
        pcf.setProjectId(getCurrentProjectId());
        pcf.setCustomFieldId(customField.getId());
        pcf.setLabel(customField.getLabel());
        pcf.setOrderNo(customField.getOrderNo());
        pcf.setCreatedBy(getCurrentUser());
        pcf.setUpdatedBy(getCurrentUser());
        return pcf;
    }

    /**
     * 登録用カスタムフィールドオブジェクトを作成する.
     * @param customField カスタムフィールド
     * @return カスタムフィールド
     */
    private CustomField createInsertCustomField(CustomField customField) {
        CustomField insertCf = new CustomField();
        insertCf.setLabel(customField.getLabel());
        insertCf.setOrderNo(customField.getOrderNo());
        if (isAdminHome()) {
            insertCf.setUseWhole(UseWhole.ALL);
        } else {
            insertCf.setUseWhole(UseWhole.EACH);
        }
        insertCf.setCreatedBy(getCurrentUser());
        insertCf.setUpdatedBy(getCurrentUser());
        return insertCf;
    }

    /**
     * AdminHomeか判定する.
     * @return AdminHomeならtrue / 違うならfalse
     */
    private boolean isAdminHome() {
        return StringUtils.isEmpty(getCurrentProjectId());
    }

    /**
     * 同じLabelが既に登録されているか判定する.
     * @param customField カスタムフィールド
     * @return 既に登録されているtrue / 登録されていないfalse
     */
    private boolean isExistLabel(CustomField customField) {
        SearchCustomFieldCondition condition = new SearchCustomFieldCondition();
        condition.setLabel(customField.getLabel());
        condition.setProjectId(getCurrentProjectId());

        if (!customField.isNew()) {
            condition.setId(customField.getId());
        }
        return countCheckAdmin(condition) > 0;
    }

    /**
     * 同じLabelが既に登録されている場合はエラー.
     * @param customField カスタムフィールド
     * @throws ServiceAbortException 既に登録されている
     */
    private void validateExistLabel(CustomField customField) throws ServiceAbortException {
        if (isExistLabel(customField)) {
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CUSTOM_FIELD_LABEL_ALREADY_EXISTS,
                (Object) customField.getLabel());
        }
    }

    /**
     * 条件を指定してカスタムフィールド件数を取得する(エラーチェック用).
     * @param condition 検索条件
     * @return カスタムフィールド件数
     */
    private int countCheckAdmin(SearchCustomFieldCondition condition) {
        CustomFieldDao dao = getDao(CustomFieldDao.class);
        return dao.countCheck(condition);
    }

    /**
     * 更新用カスタムフィールドオブジェクトを作成する.
     * @param customField カスタムフィールド
     * @return カスタムフィールド
     */
    private CustomField createUpdateCustomField(CustomField customField) {
        CustomField updateCustomField = new CustomField();
        updateCustomField.setId(customField.getId());
        if (isAdminHome()) {
            updateCustomField.setLabel(customField.getLabel());
            updateCustomField.setOrderNo(customField.getOrderNo());
        } else if (customField.getUseWhole() == UseWhole.EACH) {
            updateCustomField.setLabel(customField.getLabel());
            updateCustomField.setOrderNo(customField.getOrderNo());
        }
        updateCustomField.setUpdatedBy(getCurrentUser());
        updateCustomField.setVersionNo(customField.getVersionNo());
        return updateCustomField;
    }

    /**
     * カスタムフィールドを更新する.
     * @param customField カスタムフィールド
     * @return 更新したカスタムフィールドID
     * @throws ServiceAbortException 更新失敗
     */
    private Long updateCustomField(CustomField customField) throws ServiceAbortException {
        try {
            CustomFieldDao dao = getDao(CustomFieldDao.class);
            dao.update(customField);
            return customField.getId();
        } catch (KeyDuplicateException kde) {
            throw new ServiceAbortException(kde);
        } catch (StaleRecordException sre) {
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CUSTOM_FIELD_ALREADY_UPDATED);
        }
    }

    /**
     * 更新用プロジェクトカスタムフィールドオブジェクトを作成する.
     * @param customField カスタムフィールド
     * @return プロジェクトカスタムフィールド
     */
    private ProjectCustomField createUpdateProjectCustomField(CustomField customField) {
        ProjectCustomField updatePrjectCustomField = new ProjectCustomField();
        updatePrjectCustomField.setId(customField.getProjectCustomFieldId());
        updatePrjectCustomField.setLabel(customField.getLabel());
        updatePrjectCustomField.setOrderNo(customField.getOrderNo());
        updatePrjectCustomField.setUpdatedBy(getCurrentUser());
        return updatePrjectCustomField;
    }

    /**
     * プロジェクトカスタムフィールドを更新する.
     * @param projectCustomField プロジェクトカスタムフィールド
     * @return 更新したプロジェクトカスタムフィールドID
     * @throws ServiceAbortException 更新失敗
     */
    private Long updateProjectCustomField(ProjectCustomField projectCustomField)
            throws ServiceAbortException {
        try {
            ProjectCustomFieldDao dao = getDao(ProjectCustomFieldDao.class);
            dao.update(projectCustomField);
            return projectCustomField.getId();
        } catch (KeyDuplicateException kde) {
            throw new ServiceAbortException(kde);
        } catch (StaleRecordException sre) {
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CUSTOM_FIELD_ALREADY_UPDATED);
        }
    }

    /**
     * 削除用カスタムフィールド設定値オブジェクトを作成する.
     * @param customField カスタムフィールド
     * @return カスタムフィールド設定値
     */
    private CustomFieldValue createDeleteCustomFieldValue(CustomField customField) {
        CustomFieldValue customFieldValue = new CustomFieldValue();
        customFieldValue.setCustomFieldId(customField.getId());
        customFieldValue.setUpdatedBy(getCurrentUser());
        return customFieldValue;
    }

    /**
     * 登録用カスタムフィールド設定値オブジェクトを作成する.
     * @param customField カスタムフィールド
     * @return カスタムフィールド設定値
     */
    private CustomFieldValue createInsertCustomFieldValue(CustomField customField,
        CustomFieldValue customFieldValue, Long id) {
        CustomFieldValue insertCustomFieldValue = new CustomFieldValue();
        if (customField.isNew()) {
            insertCustomFieldValue.setCustomFieldId(id);
        } else {
            insertCustomFieldValue.setCustomFieldId(customField.getId());
        }
        insertCustomFieldValue.setValue(customFieldValue.getValue());
        insertCustomFieldValue.setOrderNo(customField.getOrderNo());
        insertCustomFieldValue.setCreatedBy(getCurrentUser());
        insertCustomFieldValue.setUpdatedBy(getCurrentUser());
        return insertCustomFieldValue;
    }

    /**
     * カスタムフィールド設定値を削除する.
     * @param customFieldValue カスタムフィールド設定値
     * @return 削除件数
     * @throws ServiceAbortException 削除失敗
     */
    private Integer deleteCustomFieldValue(CustomFieldValue customFieldValue)
            throws ServiceAbortException {
        try {
            CustomFieldValueDao dao = getDao(CustomFieldValueDao.class);
            return dao.deleteByCustomFieldId(customFieldValue);
        } catch (KeyDuplicateException kde) {
            throw new ServiceAbortException(kde);
        }
    }

    /**
     * カスタムフィールド設定値を登録する.
     * @param customFieldValue カスタムフィールド設定値
     * @return カスタムフィールド設定値ID
     * @throws ServiceAbortException 登録失敗
     */
    private Long insertCustomFieldValue(CustomFieldValue customFieldValue)
            throws ServiceAbortException {
        try {
            CustomFieldValueDao dao = getDao(CustomFieldValueDao.class);
            return dao.create(customFieldValue);
        } catch (KeyDuplicateException kde) {
            throw new ServiceAbortException(kde);
        }
    }

    /**
     * カスタムフィールド設定値を削除、登録する.
     * @param customField カスタムフィールド
     * @throws ServiceAbortException 削除、登録失敗
     */
    private void deleteInsertCustomFieldValue(CustomField customField, Long id)
            throws ServiceAbortException {
        if (!customField.isNew()) {
            // カスタムフィールド設定値を削除する
            deleteCustomFieldValue(createDeleteCustomFieldValue(customField));
        }

        if (customField.getCustomFieldValues() != null
            && !customField.getCustomFieldValues().isEmpty()) {
            // カスタムフィールド設定値を登録する
            for (CustomFieldValue customFieldValue : customField.getCustomFieldValues()) {
                insertCustomFieldValue(createInsertCustomFieldValue(customField,
                    customFieldValue,
                    id));
            }
        }
    }

    /**
     * マスタ管理の保存処理を行う.
     * @param cusotmField カスタムフィールド
     * @return 保存したカスタムフィールドID
     * @throws ServiceAbortException 保存処理失敗
     */
    private Long saveAdminHome(CustomField customField) throws ServiceAbortException {
        Long saveId = null;
        if (customField.isNew()) {
            saveId = insertCustomField(createInsertCustomField(customField));
        } else {
            saveId = updateCustomField(createUpdateCustomField(customField));
        }
        return saveId;
    }

    /**
     * プロジェクトマスタ管理の保存処理を行う.
     * @param customField カスタムフィールド
     * @return 保存したプロジェクトカスタムフィールドID
     * @throws ServiceAbortException 保存失敗
     */
    private Long saveProjectAdminHome(CustomField customField) throws ServiceAbortException {
        Long saveId = null;
        if (customField.isNew()) {
            saveId = insertCustomField(createInsertCustomField(customField));
            CustomField relationCustomField = findById(saveId);
            insertProjectCustomField(createInsertProjectCustomField(relationCustomField));
        } else {
            updateProjectCustomField(createUpdateProjectCustomField(customField));
            saveId = updateCustomField(createUpdateCustomField(customField));
        }
        return saveId;
    }

    /**
     * 指定したカスタムフィールドがプロジェクトに関連付いている場合はエラー.
     * @param customField カスタムフィールド
     * @throws ServiceAbortException プロジェクトに関連付いている
     */
    private void validateRelationProject(CustomField customField) throws ServiceAbortException {
        SearchCustomFieldCondition condition = new SearchCustomFieldCondition();
        condition.setId(customField.getId());
        condition.setAdminHome(true);
        // 1件でも取得できた場合はエラー
        if (countCustomField(condition) > 0) {
            throw new ServiceAbortException(
                ApplicationMessageCode.
                    CANNOT_PERFORM_BECAUSE_CUSTOM_FIELD_ALREADY_ASSIGNED_TO_PROJECT);
        }
    }

    /**
     * 指定したカスタムフィールドに紐付いているコレポン文書の件数を検索する.
     * @param condition 検索条件
     * @return コレポン文書の件数
     */
    private int countCorresponByCustomField(Long projectCustomFieldId) {
        CorresponDao dao = getDao(CorresponDao.class);
        return dao.countCorresponByCustomField(projectCustomFieldId);
    }

    /**
     * 指定したカスタムフィールドが、登録済みのコレポン文書に関連付けられている場合はエラー.
     * @param customField カスタムフィールド
     * @throws ServiceAbortException 関連付いている
     */
    private void validateRelationCorrespon(CustomField customField) throws ServiceAbortException {
        if (countCorresponByCustomField(customField.getProjectCustomFieldId()) > 0) {
            throw new ServiceAbortException(
                ApplicationMessageCode.
                    CANNOT_PERFORM_BECAUSE_CUSTOM_FIELD_ALREADY_RELATED_WITH_CORRESPON);
        }
    }

    /**
     * プロジェクトカスタムフィールドを削除する.
     * @param projectCustomField プロジェクトカスタムフィールド
     * @return 削除件数
     * @throws ServiceAbortException 削除失敗
     */
    private Integer deleteProjectCustomField(ProjectCustomField projectCustomField)
            throws ServiceAbortException {
        try {
            ProjectCustomFieldDao dao = getDao(ProjectCustomFieldDao.class);
            return dao.delete(projectCustomField);
        } catch (KeyDuplicateException kde) {
            throw new ServiceAbortException(kde);
        } catch (StaleRecordException sre) {
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CUSTOM_FIELD_ALREADY_UPDATED);
        }
    }

    /**
     * 削除用プロジェクトカスタムフィールドオブジェクトを作成する.
     * @param customField カスタムフィールド
     * @return プロジェクトカスタムフィールド
     */
    private ProjectCustomField createDeleteProjectCustomField(CustomField customField) {
        ProjectCustomField projectCustomField = new ProjectCustomField();
        projectCustomField.setId(customField.getProjectCustomFieldId());
        projectCustomField.setUpdatedBy(getCurrentUser());
        return projectCustomField;
    }

    /**
     * カスタムフィールドを削除する.
     * @param customField カスタムフィールド
     * @return 削除件数
     * @throws ServiceAbortException 削除失敗
     */
    private Integer deleteCustomField(CustomField customField) throws ServiceAbortException {
        try {
            CustomFieldDao dao = getDao(CustomFieldDao.class);
            return dao.delete(customField);
        } catch (KeyDuplicateException kde) {
            throw new ServiceAbortException(kde);
        } catch (StaleRecordException sre) {
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CUSTOM_FIELD_ALREADY_UPDATED);
        }
    }

    /**
     * 削除用カスタムフィールドオブジェクトを作成する.
     * @param customField カスタムフィールド
     * @return カスタムフィールド
     */
    private CustomField createDeleteCustomField(CustomField customField) {
        CustomField delete = new CustomField();
        delete.setId(customField.getId());
        delete.setUpdatedBy(getCurrentUser());
        delete.setVersionNo(customField.getVersionNo());
        return delete;
    }

    /**
     * バージョンナンバーを更新するカスタムフィールドオブジェクトを作成する.
     * @param customField カスタムフィールド
     * @return カスタムフィールド
     */
    private CustomField createUpdateVersionNo(CustomField customField) {
        CustomField updateVersionNo = new CustomField();
        updateVersionNo.setId(customField.getId());
        updateVersionNo.setUpdatedBy(getCurrentUser());
        updateVersionNo.setVersionNo(customField.getVersionNo());
        return updateVersionNo;
    }

    /**
     * カスタムフィールド設定値が重複している場合はエラー.
     * @param customFieldValues カスタムフィールド設定値
     * @throws ServiceAbortException 既に登録されている
     */
    private void validateExistCustomFieldValue(List<CustomFieldValue> customFieldValues)
            throws ServiceAbortException {
        String existValue = getExistCustomFieldValue(customFieldValues);
        if (StringUtils.isNotEmpty(existValue)) {
            throw new ServiceAbortException(
                ApplicationMessageCode.DUPLICATED_CUSTOM_FIELD_VALUES, (Object) existValue);
        }
    }

    /**
     * カスタムフィールド設定値が重複しているか判定する.
     * @param customFieldValues カスタムフィールド設定値
     * @return 重複している場合は重複している設定値/重複していない場合は空文字
     */
    private String getExistCustomFieldValue(List<CustomFieldValue> customFieldValues) {
        String existCustomFieldValue = "";
        if (customFieldValues != null) {
            for (CustomFieldValue value : customFieldValues) {
                int count = 0;
                for (CustomFieldValue values : customFieldValues) {
                    if (value.getValue().equals(values.getValue())) {
                        count++;
                    }
                }
                if (count > 1) {
                    existCustomFieldValue = value.getValue();
                    break;
                }
            }
        }
        return existCustomFieldValue;
    }
}
