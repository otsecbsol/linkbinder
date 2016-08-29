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
import jp.co.opentone.bsol.linkbinder.dao.CorresponTypeDao;
import jp.co.opentone.bsol.linkbinder.dao.ProjectCorresponTypeDao;
import jp.co.opentone.bsol.linkbinder.dao.WorkflowPatternDao;
import jp.co.opentone.bsol.linkbinder.dto.CorresponType;
import jp.co.opentone.bsol.linkbinder.dto.ProjectCorresponType;
import jp.co.opentone.bsol.linkbinder.dto.SearchCorresponTypeResult;
import jp.co.opentone.bsol.linkbinder.dto.WorkflowPattern;
import jp.co.opentone.bsol.linkbinder.dto.code.UseWhole;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponTypeCondition;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.service.AbstractService;
import jp.co.opentone.bsol.linkbinder.service.admin.CorresponTypeService;

/**
 * このサービスではコレポン文書種別に関する処理を提供する.
 * @author opentone
 */
@Service
@Transactional(rollbackFor = { ServiceAbortException.class })
public class CorresponTypeServiceImpl extends AbstractService implements CorresponTypeService {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -2795456058133877087L;

    /**
     * プロパティに設定されたEXCELシート名のKEY.
     */
    private static final String SHEET_KEY = "excel.sheetname.correspontypeindex";

    /**
     * EXCELシート名がプロパティに設定されていない場合のデフォルト名.
     */
    private static final String SHEET_DEFAULT = "CorresponTypeIndex";

    /**
     * Excel出力する際のヘッダ名.
     */
    private static final List<String> HEADER;
    static {
        HEADER = new ArrayList<String>();
        HEADER.add("ID");
        HEADER.add("文書種類");
        HEADER.add("文書種類名");
        HEADER.add("ワークフローパターン");
        HEADER.add("承認者閲覧許可");
        HEADER.add("ワークフロー利用強制");
    }

    /**
     * Excel出力する際の出力項目.
     */
    private static final List<String> FIELDS;
    static {
        FIELDS = new ArrayList<String>();
        FIELDS.add("id");
        FIELDS.add("corresponType");
        FIELDS.add("name");
        FIELDS.add("workflowPattern.name");
        FIELDS.add("allowApprover");
        FIELDS.add("useWorkflow");
    }

    /**
     * Excel出力する際の出力項目.
     */
    private static final List<String> PROJECT_FIELDS;
    static {
        PROJECT_FIELDS = new ArrayList<String>();
        PROJECT_FIELDS.add("projectCorresponTypeId");
        PROJECT_FIELDS.add("corresponType");
        PROJECT_FIELDS.add("name");
        PROJECT_FIELDS.add("workflowPattern.name");
        PROJECT_FIELDS.add("allowApprover");
        PROJECT_FIELDS.add("useWorkflow");
    }

    /**
     * 空のインスタンスをセットする.
     */
    public CorresponTypeServiceImpl() {
    }

    /*
     * (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.service.admin.CorresponTypeService#search(
     * jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponTypeCondition)
     */
    @Transactional(readOnly = true)
    public List<CorresponType> search(SearchCorresponTypeCondition condition)
        throws ServiceAbortException {
        List<CorresponType> corresponType = find(condition);

        return corresponType;
    }

    /*
     * (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.service.admin.CorresponTypeService#searchPagingList(
     * jp.co.opentone.bsol.linkbinder.dto.SearchCorresponTypeCondition)
     */
    @Transactional(readOnly = true)
    public SearchCorresponTypeResult searchPagingList(SearchCorresponTypeCondition condition)
        throws ServiceAbortException {
        ArgumentValidator.validateNotNull(condition);

        // 権限チェック
        checkPermission(condition.getProjectId());
        // 該当データの存在チェック
        int count = getDataCount(condition);
        // 指定の条件に該当するコレポン文書種別一覧情報を取得
        List<CorresponType> corresponTypeList = find(condition);
        // ページングデータのチェック
        checkPagingData(corresponTypeList);

        return createResult(corresponTypeList, count);
    }

    /*
     * (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.service.admin.CorresponTypeService#searchNotAssigned()
     */
    @Transactional(readOnly = true)
    public List<CorresponType> searchNotAssigned() {
        // Projectに追加するコレポン文書種別リストを取得
        return findAssignToType(getCurrentProjectId());

    }

    /*
     * (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.service.admin.CorresponTypeService#generateExcel(java
     * .util.List)
     */
    public byte[] generateExcel(List<CorresponType> corresponTypes) throws ServiceAbortException {
        try {
            String sheetName = SystemConfig.getValue(SHEET_KEY);
            if (sheetName == null) {
                sheetName = SHEET_DEFAULT;
            }
            WorkbookGenerator generator =
                    new WorkbookGenerator(sheetName, corresponTypes, getFields(), HEADER, true);
            return generator.generate();
        } catch (GeneratorFailedException e) {
            throw new ServiceAbortException(e.getMessage());
        }
    }

    private List<String> getFields() {
        if (StringUtils.isEmpty(getCurrentProjectId())) {
            return FIELDS;
        } else {
            return PROJECT_FIELDS;
        }
    }

    /*
     * (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.service.admin.CorresponTypeService#find(java.lang.Long)
     */
    @Transactional(readOnly = true)
    public CorresponType find(Long id) throws ServiceAbortException {
        ArgumentValidator.validateNotNull(id);

        CorresponType corresponType = null;
        if (isAdminHome()) {
            corresponType = findById(id);
        } else {
            corresponType = findByIdProjectId(id);
            // コレポン文書種別のプロジェクトが現在選択中のプロジェクト以外ならエラー
            validateProjectId(corresponType.getProjectId());
        }
        return corresponType;
    }

    /*
     * (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.service.admin.CorresponTypeService#
     * findByProjectCorresponTypeId(java.lang.Long)
     */
    public CorresponType findByProjectCorresponTypeId(Long projectCorresponTypeId)
            throws ServiceAbortException {
        ArgumentValidator.validateNotNull(projectCorresponTypeId);
        try {
            CorresponTypeDao dao = getDao(CorresponTypeDao.class);
            return dao.findByProjectCorresponTypeId(projectCorresponTypeId);
        } catch (RecordNotFoundException e) {
            throw new ServiceAbortException(ApplicationMessageCode.NO_DATA_FOUND);
        }

    }

    /*
     * (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.service.admin.CorresponTypeService#assignTo(
     *      jp.co.opentone.bsol.linkbinder.dto.CorresponType)
     *
     */
    public Long assignTo(CorresponType corresponType) throws ServiceAbortException {
        ArgumentValidator.validateNotNull(corresponType);
        // 権限共通チェック
        checkPermission(getCurrentProjectId());
        // 指定のコレポン文書種別が存在しない場合はエラー
        findById(corresponType.getId());
        // 指定のプロジェクトに同じコレポン文書種別情報が既に紐付いている際はエラー
        validateExistCorresponType(createCorresponTypeForExistCorresponTypeAssignTo(corresponType));

        // 登録
        return insertProjectCorresponType(createInsertProjectCorresponType(corresponType.getId(), corresponType));
    }

    /*
     * (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.service.admin.CorresponTypeService#validate(
     *      jp.co.opentone.bsol.linkbinder.dto.CorresponType)
     *
     */
    @Transactional(readOnly = true)
    public boolean validate(CorresponType corresponType) throws ServiceAbortException {
        ArgumentValidator.validateNotNull(corresponType);
        // 権限チェック
        checkPermission(getCurrentProjectId());

        // 有効なコレポン文書種別の同じ文書種別（correspon_type）が既に登録されている際はエラー
        validateExitsCorresponType(corresponType);
        if (isAdminHome()) {
            // 更新のときは該当データ0件の場合はエラー
            if (!corresponType.isNew()) {
                findById(corresponType.getId());
            }
        } else {
            // 更新時は該当データ0件の場合はエラー
            if (!corresponType.isNew()) {
                findByProjectCorresponTypeId(corresponType.getProjectCorresponTypeId());
            }
            // コレポン文書種別のプロジェクトが現在選択中のプロジェクト以外はエラー
            validateProjectId(corresponType.getProjectId());
        }
        return true;
    }

    /*
     * (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.service.admin.CorresponTypeService#searchWorkflowPattern
     * ()
     */
    @Transactional(readOnly = true)
    public List<WorkflowPattern> searchWorkflowPattern() {
        // 現在登録されている承認フローパターンをDBから取得する。
        return findWorkflowPattern();
    }

    /*
     * (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.service.admin.CorresponTypeService#delete(
     *      jp.co.opentone.bsol.linkbinder.dto.CorresponType)
     */
    public void delete(CorresponType corresponType) throws ServiceAbortException {
        ArgumentValidator.validateNotNull(corresponType);
        // 権限チェック
        checkPermission(getCurrentProjectId());
        if (isAdminHome()) {
            // 削除対象のコレポン文書種別が、プロジェクトに関連付けられている場合は削除不可
            validateRelationProjectCorresponType(corresponType);
            // バージョンナンバーが違う場合はエラー
            deleteCorresponType(createDeleteCorresponType(corresponType));
        } else {
            // コレポン文書種別のプロジェクトが現在選択中のプロジェクト以外はエラー
            validateProjectId(corresponType.getProjectId());
            // 削除対象のコレポン文書種別が、登録済みのコレポン文書に関連付けられている場合は削除不可とする
            // CorresponDaoでカウントを定義する。（project_correspon_type_idでカウントをとる）
            validateRelationCorrespon(corresponType);
            // プロジェクトコレポン文書種別を削除
            deleteProjectCorresponType(createDeleteProjectCorresponType(corresponType));
            // コレポン文書種別を削除する
            deleteOrUpdateCorresponType(corresponType);
        }
    }

    /*
     * (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.service.admin.CorresponTypeService#save(
     *      jp.co.opentone.bsol.linkbinder.dto.CorresponType)
     */
    public Long save(CorresponType corresponType) throws ServiceAbortException {
        ArgumentValidator.validateNotNull(corresponType);
        // 権限チェック
        checkPermission(getCurrentProjectId());

        // 有効なコレポン文書種別の同じ文書種別（correspon_type）が既に登録されている際はエラー
        validateExitsCorresponType(corresponType);

        Long id = null;
        if (isAdminHome()) {
            // AdminHome
            id = saveAdminHome(corresponType);
        } else {
            // ProjectAdminHome
            id = saveProjectAdminHome(corresponType);
        }
        return id;
    }

    /**
     * AdminHomeの保存処理を行う.
     * @param corresponType コレポン文書種別
     * @return 保存したコレポン文書種別ID
     * @throws ServiceAbortException 保存失敗
     */
    private Long saveAdminHome(CorresponType corresponType) throws ServiceAbortException {
        Long id = null;
        if (!corresponType.isNew()) {
            // 更新処理
            // バージョンナンバーが違う場合はエラー
            id = updateCorresponType(createUpdateCorresponType(corresponType));
        } else {
            // 登録処理
            id = insertCorresponType(createInsertCorrepsonType(corresponType));
        }
        return id;
    }

    /**
     * ProjectAdminHomeの保存処理を行う.
     * @param corresponType コレポン文書種別
     * @return 保存したコレポン文書種別ID
     * @throws ServiceAbortException 保存失敗
     */
    private Long saveProjectAdminHome(CorresponType corresponType) throws ServiceAbortException {
        Long id = null;
        // 更新処理
        if (!corresponType.isNew()) {
            // コレポン文書種別のプロジェクトが現在選択中のプロジェクト以外はエラー
            validateProjectId(corresponType.getProjectId());
            // バージョンナンバーが違う場合はエラー
            id = updateCorresponType(createUpdateCorresponType(corresponType));
            updateProjectCorresponType(createUpdateProjectCorresponType(
                    corresponType));
        } else {
            // 登録処理
            id = insertCorresponType(createInsertCorrepsonType(corresponType));
            insertProjectCorresponType(
                    createInsertProjectCorresponType(id, corresponType));
        }
        return id;
    }

    /**
     * 承認フローパターンを全件取得する.
     * @return 承認フローパターン
     */
    private List<WorkflowPattern> findWorkflowPattern() {
        WorkflowPatternDao dao = getDao(WorkflowPatternDao.class);
        return dao.findAll();
    }

    /**
     * 同じ文書種別が既に登録されている場合はエラーにする.
     * @param corresponType コレポン文書種別
     * @throws ServiceAbortException 既に登録されている
     */
    private void validateExitsCorresponType(CorresponType corresponType)
            throws ServiceAbortException {
        if (isExistCorresponType(corresponType)) {
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CORRESPON_TYPE_CODE_ALREADY_EXISTS,
                    (Object) corresponType.getCorresponType());
        }
    }

    /**
     * 同じ文書種別が既に登録されているか判定する.
     * @param corresponType コレポン文書種別
     * @return 登録されているtrue / 登録されていないfalse
     */
    private boolean isExistCorresponType(CorresponType corresponType) {
        return countByCorresponType(corresponType) > 0;
    }

    /**
     * 文書種別を指定してコレポン文書種別の件数を取得する.
     * @param corresponType コレポン文書種別
     * @return コレポン文書種別
     */
    private int countByCorresponType(CorresponType corresponType) {
        CorresponTypeDao dao = getDao(CorresponTypeDao.class);
        SearchCorresponTypeCondition condition = new SearchCorresponTypeCondition();
        condition.setCorresponType(corresponType.getCorresponType());
        condition.setProjectId(getCurrentProjectId());

        // 更新
        if (!corresponType.isNew()) {
            condition.setId(corresponType.getId());
        }
        return dao.countCheck(condition);
    }

    /**
     * AdminHomeか判定する.
     * @return AdminHomeならtrue / ProjectAdminHomeならfalse
     */
    private boolean isAdminHome() {
        return StringUtils.isEmpty(getCurrentProjectId());
    }

    /**
     * IDを指定してコレポン文書種別を取得する.
     * @param id コレポン文書種別ID
     * @return コレポン文書種別
     * @throws ServiceAbortException 取得失敗
     */
    private CorresponType findById(Long id) throws ServiceAbortException {
        try {
            CorresponTypeDao dao = getDao(CorresponTypeDao.class);
            return dao.findById(id);
        } catch (RecordNotFoundException e) {
            throw new ServiceAbortException(ApplicationMessageCode.NO_DATA_FOUND);
        }
    }

    /**
     * 登録用プロジェクトコレポン文書種別を作成する.
     * @param corresponTypeId 登録したコレポン文書種別ID
     * @param corresponType コレポン文書種別
     * @return 登録用プロジェクトコレポン文書種別
     */
    private ProjectCorresponType createInsertProjectCorresponType(
            Long corresponTypeId, CorresponType corresponType) {
        ProjectCorresponType insertProjectCorresponType
            = new ProjectCorresponType();
        insertProjectCorresponType.setProjectId(getCurrentProjectId());
        insertProjectCorresponType.setCorresponTypeId(corresponTypeId);
        insertProjectCorresponType.setCorresponAccessControlFlags(
                corresponType.getCorresponAccessControlFlags());
        insertProjectCorresponType.setCreatedBy(getCurrentUser());
        insertProjectCorresponType.setUpdatedBy(getCurrentUser());
        return insertProjectCorresponType;
    }

    /**
     * 更新用プロジェクトコレポン文書種別を作成する.
     * @param corresponType コレポン文書種別
     * @return 更新用プロジェクトコレポン文書種別
     */
    private ProjectCorresponType createUpdateProjectCorresponType(
            CorresponType corresponType) {
        ProjectCorresponType updateProjectCorresponType
            = new ProjectCorresponType();
        updateProjectCorresponType.setProjectId(getCurrentProjectId());
        updateProjectCorresponType.setId(
                corresponType.getProjectCorresponTypeId());
        updateProjectCorresponType.setCorresponAccessControlFlags(
                corresponType.getCorresponAccessControlFlags());
        updateProjectCorresponType.setCreatedBy(getCurrentUser());
        updateProjectCorresponType.setUpdatedBy(getCurrentUser());
        return updateProjectCorresponType;
    }

    /**
     * プロジェクトコレポン文書種別を登録する.
     * @param projectCorresponType プロジェクトコレポン文書種別
     * @return 登録したプロジェクトコレポン文書種別ID
     * @throws ServiceAbortException 登録失敗
     */
    private Long insertProjectCorresponType(ProjectCorresponType projectCorresponType)
            throws ServiceAbortException {
        ProjectCorresponTypeDao dao = getDao(ProjectCorresponTypeDao.class);
        try {
            return dao.create(projectCorresponType);
        } catch (KeyDuplicateException kde) {
            throw new ServiceAbortException(kde);
        }
    }

    /**
     * 更新用コレポン文書種別オブジェクトを作成する.
     * @param corresponType コレポン文書種別
     * @return 更新用コレポン文書種別
     */
    private CorresponType createUpdateCorresponType(CorresponType corresponType) {
        CorresponType updateCorresponType = new CorresponType();
        updateCorresponType.setId(corresponType.getId());
        updateCorresponType.setCorresponType(corresponType.getCorresponType());
        updateCorresponType.setName(corresponType.getName());
        updateCorresponType.setWorkflowPattern(corresponType.getWorkflowPattern());
        updateCorresponType.setAllowApproverToBrowse(corresponType.getAllowApproverToBrowse());
        updateCorresponType.setForceToUseWorkflow(corresponType.getForceToUseWorkflow());
        updateCorresponType.setUpdatedBy(getCurrentUser());
        updateCorresponType.setVersionNo(corresponType.getVersionNo());
        updateCorresponType.setCorresponAccessControlFlags(corresponType.getCorresponAccessControlFlags());
        return updateCorresponType;
    }

    /**
     * コレポン文書種別を更新する.
     * @param corresponType コレポン文書種別
     * @return 更新したコレポン文書種別ID
     * @throws ServiceAbortException 更新失敗
     */
    private Long updateCorresponType(CorresponType corresponType) throws ServiceAbortException {
        try {
            CorresponTypeDao dao = getDao(CorresponTypeDao.class);
            dao.update(corresponType);
        } catch (KeyDuplicateException kde) {
            throw new ServiceAbortException(kde);
        } catch (StaleRecordException sre) {
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CORRESPON_TYPE_ALREADY_UPDATED);
        }
        return corresponType.getId();
    }

    /**
     * コレポン文書種別を登録する.
     * @param corresponType コレポン文書種別
     * @return 登録したコレポン文書種別ID
     * @throws ServiceAbortException 登録失敗
     */
    private Long insertCorresponType(CorresponType corresponType) throws ServiceAbortException {
        try {
            CorresponTypeDao dao = getDao(CorresponTypeDao.class);
            return dao.create(corresponType);
        } catch (KeyDuplicateException kde) {
            throw new ServiceAbortException(kde);
        }
    }

    /**
     * 登録用のコレポン文書種別オブジェクトを作成する.
     * @param corresponType コレポン文書種別
     * @return 登録用コレポン文書種別
     */
    private CorresponType createInsertCorrepsonType(CorresponType corresponType) {
        CorresponType insertCorresponType = new CorresponType();
        insertCorresponType.setCorresponType(corresponType.getCorresponType());
        insertCorresponType.setName(corresponType.getName());
        insertCorresponType.setWorkflowPattern(corresponType.getWorkflowPattern());
        insertCorresponType.setAllowApproverToBrowse(corresponType.getAllowApproverToBrowse());
        insertCorresponType.setForceToUseWorkflow(corresponType.getForceToUseWorkflow());

        if (isAdminHome()) {
            insertCorresponType.setUseWhole(UseWhole.ALL);
        } else {
            insertCorresponType.setUseWhole(UseWhole.EACH);
        }

        insertCorresponType.setCreatedBy(getCurrentUser());
        insertCorresponType.setUpdatedBy(getCurrentUser());
        return insertCorresponType;
    }

    /**
     * 権限チェックを行う.
     * @param conditon 検索条件
     * @return コレポン文書種別一覧
     * @throws ServiceAbortException 権限エラー
     */
    private void checkPermission(String projectId) throws ServiceAbortException {
        if (isAdminHome()) {
            // adminHome
            if (!isSystemAdmin(getCurrentUser())) {
                throw new ServiceAbortException(
                    ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT);
            }
        } else {
            // projectAdminHome
            if (!isSystemAdmin(getCurrentUser()) && !isProjectAdmin(getCurrentUser(), projectId)) {
                throw new ServiceAbortException(
                    ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT);
            }
        }
    }

    /**
     * コレポン文書種別を取得する.
     * @param conditon 検索条件
     * @return コレポン文書種別一覧
     */
    private List<CorresponType> find(SearchCorresponTypeCondition conditon) {
        CorresponTypeDao dao = getDao(CorresponTypeDao.class);
        return dao.find(conditon);
    }

    /**
     * Projectに追加するコレポン文書種別を取得する.
     * @param ProjectId プロジェクトID
     * @return コレポン文書種別一覧
     */
    private List<CorresponType> findAssignToType(String projectId) {
        if (StringUtils.isNotEmpty(projectId)) {
            CorresponTypeDao dao = getDao(CorresponTypeDao.class);
            return dao.findNotExist(projectId);
        }
        return new ArrayList<CorresponType>(); // NULLにはしない
    }

    /**
     * 該当データの件数を取得する. 0件の場合、エラー.
     * @param condition 検索条件
     * @return 件数
     * @throws ServiceAbortException 件数が0件
     */
    private int getDataCount(SearchCorresponTypeCondition condition) throws ServiceAbortException {
        CorresponTypeDao dao = getDao(CorresponTypeDao.class);
        int count = dao.count(condition);
        // 該当データ0件の場合
        if (count == 0) {
            throw new ServiceAbortException(ApplicationMessageCode.NO_DATA_FOUND);
        }
        return count;
    }

    /**
     * ページングで指定されたページにデータが存在するかチェックする.
     * @param corresponTypeList 一覧データ
     * @throws ServiceAbortException 指定されたページにデータが存在しない
     */
    private void checkPagingData(List<CorresponType> corresponTypeList)
            throws ServiceAbortException {
        // 該当データ0件の場合
        if (corresponTypeList.size() == 0) {
            throw new ServiceAbortException(ApplicationMessageCode.NO_PAGE_FOUND);
        }
    }

    /**
     * コレポン種別一覧画面に渡すオブジェクトを生成する.
     * @param corresponTypeList 一覧データ
     * @param count 総件数
     * @return 一覧表示用オブジェクト
     */
    private SearchCorresponTypeResult createResult(
            List<CorresponType> corresponTypeList, int count) {
        SearchCorresponTypeResult result = new SearchCorresponTypeResult();
        result.setCorresponTypeList(corresponTypeList);
        result.setCount(count);

        return result;
    }

    /**
     * コレポン文書種別IDとプロジェクトIDを指定してプロジェクトコレポン文書種別の件数を取得する.
     * @param corresponTypeId コレポン文書種別ID
     * @return プロジェクトコレポン文書種別の件数
     */
    private int countByCorresponTypeIdProjectId(Long corresponTypeId) {
        ProjectCorresponTypeDao dao = getDao(ProjectCorresponTypeDao.class);
        return dao.countByCorresponTypeIdProjectId(corresponTypeId, getCurrentProjectId());
    }

    /**
     * 指定のプロジェクトに同じコレポン文書種別が既に紐付いている場合はエラーにする.
     * @param corresponType コレポン文書種別
     * @throws ServiceAbortException 既に紐付いている
     */
    private void validateRelationProjectCorresponType(CorresponType corresponType)
            throws ServiceAbortException {
        if (countByCorresponTypeIdProjectId(corresponType.getId()) > 0) {
            throw new ServiceAbortException(
                ApplicationMessageCode.
                    CANNOT_PERFORM_BECAUSE_CORRESPON_TYPE_ALREADY_ASSIGNED_TO_PROJECT);
        }
    }

    /**
     * 同じコレポン文書種別が存在した場合はエラー.
     * @param corresponType コレポン文書
     * @throws ServiceAbortException 存在する
     */
    private void validateExistCorresponType(CorresponType corresponType)
            throws ServiceAbortException {
        if (isExistCorresponType(corresponType)) {
            throw new ServiceAbortException(
                ApplicationMessageCode.
                    CANNOT_PERFORM_BECAUSE_CORRESPON_TYPE_ALREADY_ASSIGNED_TO_PROJECT);
          }
    }

    /**
     * IDとプロジェクトIDを指定してコレポン文書種別を取得する.
     * @param id ID
     * @return コレポン文書種別
     * @throws ServiceAbortException データが取得できない
     */
    private CorresponType findByIdProjectId(Long id) throws ServiceAbortException {
        try {
            CorresponTypeDao dao = getDao(CorresponTypeDao.class);
            return dao.findByIdProjectId(id, getCurrentProjectId());
        } catch (RecordNotFoundException e) {
            throw new ServiceAbortException(ApplicationMessageCode.NO_DATA_FOUND);
        }
    }

    /**
     * コレポン文書種別を削除する.
     * @param corresponType コレポン文書種別
     * @return 削除した件数
     * @throws ServiceAbortException 削除失敗
     */
    private Integer deleteCorresponType(CorresponType corresponType) throws ServiceAbortException {
        try {
            CorresponTypeDao dao = getDao(CorresponTypeDao.class);
            return dao.delete(corresponType);
        } catch (KeyDuplicateException e) {
            throw new ServiceAbortException(e);
        } catch (StaleRecordException e) {
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CORRESPON_TYPE_ALREADY_UPDATED);
        }
    }

    /**
     * 指定したコレポン文書種別がコレポン文書に関連付いている場合はエラーにする.
     * @param corresponType コレポン文書種別
     * @throws ServiceAbortException コレポン文書に関連付いている
     */
    private void validateRelationCorrespon(CorresponType corresponType)
            throws ServiceAbortException {
        if (isRelationCorrespon(corresponType.getProjectCorresponTypeId())) {
            throw new ServiceAbortException(
                ApplicationMessageCode.
                    CANNOT_PERFORM_BECAUSE_CORRESPON_TYPE_ALREADY_ASSIGNED_TO_CORRESPON);
        }
    }

    /**
     * 指定したコレポン文書種別がコレポン文書に関連付いているか判定する.
     * @param projectCorresponTypeId プロジェクトコレポン文書種別ID
     * @return 関連付いているtrue / 関連付いていないfalse
     */
    private boolean isRelationCorrespon(Long projectCorresponTypeId) {
        return countRelationCorrespon(projectCorresponTypeId) > 0;
    }

    /**
     * 指定したコレポン文書種別が紐付いているコレポン文書件数を取得する.
     * @param projectCorresponTypeId プロジェクトコレポン文書種別ID
     * @return コレポン文書件数
     */
    private int countRelationCorrespon(Long projectCorresponTypeId) {
        CorresponDao dao = getDao(CorresponDao.class);
        return dao.countCorresponByCorresponType(projectCorresponTypeId);
    }

    /**
     * プロジェクトコレポン文書種別を削除する.
     * @param projectCorresponType プロジェクトコレポン文書種別
     * @return 削除した件数
     * @throws ServiceAbortException 削除失敗
     */
    private Integer deleteProjectCorresponType(ProjectCorresponType projectCorresponType)
            throws ServiceAbortException {
        try {
            ProjectCorresponTypeDao dao = getDao(ProjectCorresponTypeDao.class);
            return dao.delete(projectCorresponType);
        } catch (KeyDuplicateException e) {
            throw new ServiceAbortException(e);
        } catch (StaleRecordException e) {
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CORRESPON_TYPE_ALREADY_UPDATED);
        }
    }

    /**
     * 削除用のプロジェクトコレポン文書種別オブジェクトを作成する.
     * @param corresponType コレポン文書種別
     * @return プロジェクトコレポン文書種別
     */
    private ProjectCorresponType createDeleteProjectCorresponType(CorresponType corresponType) {
        ProjectCorresponType pt = new ProjectCorresponType();
        pt.setId(corresponType.getProjectCorresponTypeId());
        pt.setUpdatedBy(getCurrentUser());
        return pt;
    }

    /**
     * 削除用のコレポン文書種別オブジェクトを作成する.
     * @param corresponType コレポン文書種別
     * @return コレポン文書種別
     */
    private CorresponType createDeleteCorresponType(CorresponType corresponType) {
        CorresponType deleteCorresponType = new CorresponType();
        deleteCorresponType.setId(corresponType.getId());
        deleteCorresponType.setUpdatedBy(getCurrentUser());
        deleteCorresponType.setVersionNo(corresponType.getVersionNo());
        return deleteCorresponType;
    }

    /**
     * コレポン文書種別を削除または更新する.
     * @param corresponType コレポン文書種別
     * @throws ServiceAbortException 削除、更新失敗
     */
    private void deleteOrUpdateCorresponType(CorresponType corresponType)
            throws ServiceAbortException {
        switch (corresponType.getUseWhole()) {
        case ALL:
            // コレポン文書種別を更新
            // バージョンナンバーが違う場合はエラー
            updateCorresponType(createDeleteCorresponType(corresponType));
            break;

        case EACH:
            // コレポン文書種別を削除
            deleteCorresponType(createDeleteCorresponType(corresponType));
            break;
        default:
            break;
        }
    }

    /**
     * AssignToのチェック処理用のオブジェクトを作成する.
     * @param corresponType コレポン文書種別
     * @return コレポン文書種別
     */
    private CorresponType createCorresponTypeForExistCorresponTypeAssignTo(
            CorresponType corresponType) {
        CorresponType assignCorresponType = new CorresponType();
        assignCorresponType.setCorresponType(corresponType.getCorresponType());
        assignCorresponType.setProjectId(getCurrentProjectId());
        return assignCorresponType;
    }

    /**
     * プロジェクトコレポン文書種別を更新する.
     * @param projectCorresponType プロジェクトコレポン文書種別
     * @throws ServiceAbortException 更新失敗
     */
    private void updateProjectCorresponType(
            ProjectCorresponType projectCorresponType)
            throws ServiceAbortException {
        try {
            ProjectCorresponTypeDao dao = getDao(ProjectCorresponTypeDao.class);
            dao.update(projectCorresponType);
        } catch (KeyDuplicateException kde) {
            throw new ServiceAbortException(kde);
        } catch (StaleRecordException sre) {
            throw new ServiceAbortException(
                    ApplicationMessageCode
                    .CANNOT_PERFORM_BECAUSE_CORRESPON_TYPE_ALREADY_UPDATED);
        }
    }
}
