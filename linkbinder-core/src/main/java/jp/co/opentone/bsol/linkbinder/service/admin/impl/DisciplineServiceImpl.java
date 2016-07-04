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
import jp.co.opentone.bsol.linkbinder.dao.CorresponGroupDao;
import jp.co.opentone.bsol.linkbinder.dao.DisciplineDao;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroup;
import jp.co.opentone.bsol.linkbinder.dto.Discipline;
import jp.co.opentone.bsol.linkbinder.dto.SearchDisciplineResult;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchDisciplineCondition;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.service.AbstractService;
import jp.co.opentone.bsol.linkbinder.service.admin.DisciplineService;

/**
 * このサービスでは部門情報に関する処理を提供する.
 * @author opentone
 */
@Service
@Transactional(rollbackFor = { ServiceAbortException.class })
public class DisciplineServiceImpl extends AbstractService implements DisciplineService {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 7970336194861719548L;

    /**
     * プロパティに設定されたEXCELシート名のKEY.
     */
    private static final String SHEET_KEY = "excel.sheetname.disciplineindex";

    /**
     * EXCELシート名がプロパティに設定されていない場合のデフォルト名.
     */
    private static final String SHEET_DEFAULT = "DisciplineIndex";

    /**
     * 部門情報件数.
     */
    private int dataCount;

    /**
     * Excel出力する際のヘッダ名(マスタ管理).
     */
    private static final List<String> HEADER;
    static {
        HEADER = new ArrayList<String>();
        HEADER.add("ID");
        HEADER.add("Code");
        HEADER.add("Name");
    }

    /**
     * Excel出力する際の出力項目（マスタ管理）.
     */
    private static final List<String> FIELDS;
    static {
        FIELDS = new ArrayList<String>();
        FIELDS.add("id");
        FIELDS.add("disciplineCd");
        FIELDS.add("name");
    }

    /*
     * (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.service.admin.DisciplineService#delete(jp.co.opentone.bsol.linkbinder
     * .dto.Discipline)
     */
    public void delete(Discipline discipline) throws ServiceAbortException {
        ArgumentValidator.validateNotNull(discipline);

        // 権限チェック【2】
        validatePermission();
        // 拠点に関連付けられ、活動単位として登録されている場合はエラー
        validateRelationCorresponGroup(discipline);
        // 該当データ0件の場合エラー
        findById(discipline.getId());
        // 部門のプロジェクトが現在選択中のプロジェクト以外はエラー
        validateProjectId(discipline.getProjectId());
        // 部門情報と対象データのバージョンナンバーが違う場合はエラー（排他チェック）
        // 削除実行
        deleteDiscipline(createDeletedDiscipline(discipline));
    }

    /*
     * (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.service.admin.DisciplineService#find(java.lang.Long)
     */
    @Transactional(readOnly = true)
    public Discipline find(Long id) throws ServiceAbortException {
        ArgumentValidator.validateNotNull(id);

        Discipline discipline = findById(id);
        // 部門の情報が現在選択中のプロジェクト以外はエラー
        validateProjectId(discipline.getProjectId());
        return discipline;
    }

    /*
     * (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.service.admin.DisciplineService#generateExcel(java.util
     * .List)
     */
    public byte[] generateExcel(List<Discipline> disciplines) throws ServiceAbortException {
        try {
            String sheetName = SystemConfig.getValue(SHEET_KEY);
            if (StringUtils.isEmpty(sheetName)) {

                sheetName = SHEET_DEFAULT;
            }
            WorkbookGenerator generator =
                    new WorkbookGenerator(
                        sheetName, disciplines, getOutputField(), getHeaderNames(), true);
            return generator.generate();
        } catch (GeneratorFailedException e) {
            throw new ServiceAbortException(e.getMessage());
        }
    }

    /*
     * (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.service.admin.DisciplineService#save(jp.co.opentone.bsol.linkbinder
     * .dto.Discipline)
     */
    public Long save(Discipline discipline) throws ServiceAbortException {
        ArgumentValidator.validateNotNull(discipline);

        validate(discipline);

        // 新規登録するときのエラーチェック処理
        if (discipline.isNew()) {
            // 登録処理
            Discipline insertDiscipline = createInsertDiscipline(discipline);
            return insertDiscipline(insertDiscipline);
        } else {
            // 部門の情報が現在選択中のプロジェクト以外はエラー
            validateProjectId(discipline.getProjectId());

            // 更新処理
            // 部門情報情報と対照データのバージョンナンバーが違う場合はエラー（排他）
            return updateDiscipline(createUpdateDiscipline(discipline));
        }

    }

    /*
     * (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.service.admin.DisciplineService#search(jp.co.opentone.bsol.linkbinder
     * .dto.SearchDisciplineCondition)
     */
    @Transactional(readOnly = true)
    public SearchDisciplineResult search(SearchDisciplineCondition condition)
        throws ServiceAbortException {
        ArgumentValidator.validateNotNull(condition);

        // 権限共通チェック【2】
        validatePermission();
        // 該当データ0件の場合はエラー
        validateExistDiscipline(condition);
        List<Discipline> lstDiscipline = findDiscipline(condition);
        // 指定のページを選択した際、そのページに表示するデータが存在しない場合はエラー
        validatePageSearchNotExist(lstDiscipline);

        return createResult(lstDiscipline);
    }

    /*
     * (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.service.admin.DisciplineService#validate(jp.co.opentone.bsol.linkbinder
     * .dto.Discipline)
     */
    @Transactional(readOnly = true)
    public boolean validate(Discipline discipline) throws ServiceAbortException {
        ArgumentValidator.validateNotNull(discipline);

        // 権限共通チェック【2】
        validatePermission();

        // 更新処理
        if (!discipline.isNew()) {
            // 該当データ0件の場合はエラー
            findById(discipline.getId());
        }
        // 同一プロジェクトにある有効な部門の同じ部門コード（discipline_cd）が既に登録されている際はエラー
        validateExistDisciplineCode(discipline);

        return true;

    }

    /**
     * 部門情報の出力パラメータを取得する.
     * @return 出力パラメータ
     */
    private List<String> getOutputField() {
        return FIELDS;
    }

    /**
     * コレポン一覧画面のヘッダ名を取得する.
     * @return ヘッダ名
     */
    private List<String> getHeaderNames() {
        return HEADER;
    }

    /**
     * SystemAdmin,ProjectAdmin,GroupAdminか判定する.
     * @return SystemAdmin,ProjectAdmin,GroupAdminのいづれかの場合true / 以外はfalse
     */
    private boolean isValidPermission() {
        return isSystemAdmin(getCurrentUser())
            || isProjectAdmin(getCurrentUser(), getCurrentProjectId());
    }

    /**
     * 権限がない場合はエラー.
     * @throws ServiceAbortException 権限がない.
     */
    private void validatePermission() throws ServiceAbortException {
        if (!isValidPermission()) {
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT);
        }
    }

    /**
     * 部門情報件数を取得する.
     * @param condition 検索条件
     * @return int 部門情報件数
     */
    private int countDiscipline(SearchDisciplineCondition condition) {
        DisciplineDao dao = getDao(DisciplineDao.class);
        return dao.count(condition);
    }

    /**
     * 部門情報が存在するか判定する.
     * @param condition 検索条件
     * @return 存在するtrue / 存在しないfalse
     */
    private boolean isExistDiscipline(SearchDisciplineCondition condition) {
        dataCount = countDiscipline(condition);

        return dataCount > 0;
    }

    /**
     * 部門情報が存在しない場合はエラー.
     * @param condition 検索条件
     * @throws ServiceAbortException 部門情報が既に
     */
    private void validateExistDiscipline(SearchDisciplineCondition condition)
        throws ServiceAbortException {
        if (!isExistDiscipline(condition)) {
            throw new ServiceAbortException(ApplicationMessageCode.NO_DATA_FOUND);
        }
    }

    /**
     * 部門情報を検索条件で取得する.
     * @param condition 検索条件
     * @return 部門情報
     */
    private List<Discipline> findDiscipline(SearchDisciplineCondition condition) {
        DisciplineDao dao = getDao(DisciplineDao.class);
        return dao.find(condition);
    }

    /**
     * ページを指定した際に表示するデータが存在するか判定する.
     * @param discipline 部門情報
     * @return 部門情報が存在しない場合はtrue / 存在する場合はfalse
     */
    private boolean isPageSearchNotExist(List<Discipline> discipline) {
        return discipline.isEmpty();
    }

    /**
     * ページを指定した際に表示するデータがない場合はエラー.
     * @param discipline 部門情報リスト
     * @throws ServiceAbortException データが存在しない
     */
    private void validatePageSearchNotExist(List<Discipline> discipline)
        throws ServiceAbortException {
        if (isPageSearchNotExist(discipline)) {
            throw new ServiceAbortException(ApplicationMessageCode.NO_PAGE_FOUND);
        }
    }

    /**
     * 部門情報一覧画面に渡すオブジェクトを生成する.
     * @param disciplineList 一覧データ
     * @return 一覧表示用オブジェクト
     */
    private SearchDisciplineResult createResult(List<Discipline> disciplineList) {
        SearchDisciplineResult result = new SearchDisciplineResult();
        result.setDisciplineList(disciplineList);
        result.setCount(dataCount);

        return result;
    }

    /**
     * 同一プロジェクトにある有効な部門の同じ部門コードが既に登録されているかチェックする.
     * @param discipline 部門情報
     * @return 異常なしtrue / 異常ありfalse
     */
    private boolean isExistDisciplineCode(Discipline discipline) {
        SearchDisciplineCondition condition = new SearchDisciplineCondition();
        condition.setProjectId(getCurrentProjectId());
        condition.setDisciplineCd(discipline.getDisciplineCd());

        // 更新
        if (!discipline.isNew()) {
            condition.setId(discipline.getId());
        }
        return countCheck(condition) == 0;
    }

    /**
     * 条件を指定して部門情報件数を取得する(エラーチェック用).
     * @param condition 検索条件
     * @return 部門情報件数
     */
    private int countCheck(SearchDisciplineCondition condition) {
        DisciplineDao dao = getDao(DisciplineDao.class);
        return dao.countCheck(condition);
    }

    /**
     * 同じ部門コードが既に登録されている場合はエラー.
     * @param discipline 部門情報
     * @throws ServiceAbortException 同じ部門コードが既に登録されている
     */
    private void validateExistDisciplineCode(Discipline discipline) throws ServiceAbortException {
        if (!isExistDisciplineCode(discipline)) {
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_DISCIPLINE_CODE_ALREADY_EXISTS,
                    (Object) discipline.getDisciplineCd());
        }
    }

    /**
     * IDを指定して部門情報を取得する.
     * @param discipline 部門情報
     * @return 部門情報
     * @throws ServiceAbortException データが取得できない
     */
    private Discipline findById(Long id) throws ServiceAbortException {
        try {
            DisciplineDao dao = getDao(DisciplineDao.class);
            return dao.findById(id);
        } catch (RecordNotFoundException e) {
            throw new ServiceAbortException(ApplicationMessageCode.NO_DATA_FOUND);
        }
    }

    /**
     * 部門情報を登録する.
     * @return 部門情報ID
     * @throws ServiceAbortException 登録失敗
     */
    private Long insertDiscipline(Discipline discipline) throws ServiceAbortException {
        DisciplineDao dao = getDao(DisciplineDao.class);
        try {
            return dao.create(discipline);
        } catch (KeyDuplicateException kde) {
            throw new ServiceAbortException(kde);

        }
    }

    /**
     * 部門情報を更新する.
     * @return 部門情報
     * @throws ServiceAbortException 更新失敗
     */
    private Long updateDiscipline(Discipline discipline) throws ServiceAbortException {
        try {
            DisciplineDao dao = getDao(DisciplineDao.class);
            dao.update(discipline);
        } catch (KeyDuplicateException kde) {
            throw new ServiceAbortException(kde);
        } catch (StaleRecordException sre) {
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_DISCIPLINE_ALREADY_UPDATED);
        }
        return discipline.getId();
    }

    /**
     * 更新用の部門情報を作成する.
     * @param discipline 部門情報
     * @return 更新用部門情報
     */
    private Discipline createUpdateDiscipline(Discipline discipline) {
        Discipline updateDiscipline = new Discipline();
        updateDiscipline.setId(discipline.getId());
        updateDiscipline.setDisciplineCd(discipline.getDisciplineCd());
        updateDiscipline.setName(discipline.getName());
        updateDiscipline.setUpdatedBy(getCurrentUser());
        updateDiscipline.setVersionNo(discipline.getVersionNo());

        return updateDiscipline;
    }

    /**
     * 登録用の部門情報を作成する.
     * @param discipline 部門情報
     * @return 部門情報
     */
    private Discipline createInsertDiscipline(Discipline discipline) {
        Discipline insertDiscipline = new Discipline();
        insertDiscipline.setDisciplineCd(discipline.getDisciplineCd());
        insertDiscipline.setName(discipline.getName());
        insertDiscipline.setProjectId(getCurrentProjectId());
        insertDiscipline.setCreatedBy(getCurrentUser());
        insertDiscipline.setUpdatedBy(getCurrentUser());

        return insertDiscipline;
    }

    /**
     * 指定した部門情報が活動単位に関連付いているか判定する.
     * @return 活動単位に関連ついている場合true / 関連ついていない場合false
     */
    private boolean isRelationCorresponGroup(Discipline discipline) {
        CorresponGroupDao dao = getDao(CorresponGroupDao.class);
        List<CorresponGroup> cgList = dao.findByDisciplineId(discipline.getId());
        return cgList.size() > 0;
    }

    /**
     * 指定した部門情報が活動単位に関連ついている場合はエラー.
     * @param discipline 部門情報
     * @throws ServiceAbortException 活動単位に関連ついている
     */
    private void validateRelationCorresponGroup(Discipline discipline)
            throws ServiceAbortException {
        if (isRelationCorresponGroup(discipline)) {
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_DISCIPLINE_ALREADY_RELATED_WITH_SITE);
        }
    }

    /**
     * 指定した部門情報を削除する.
     * @param discipline
     * @return 削除した件数
     * @throws ServiceAbortException 削除失敗
     */
    private Integer deleteDiscipline(Discipline discipline) throws ServiceAbortException {
        try {
            DisciplineDao dao = getDao(DisciplineDao.class);
            return dao.delete(discipline);
        } catch (KeyDuplicateException kde) {
            throw new ServiceAbortException(kde);
        } catch (StaleRecordException sre) {
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_DISCIPLINE_ALREADY_UPDATED);
        }
    }

    /**
     * 部門削除用のオブジェクトを作成する.
     * @param old
     *            削除対象の部門
     * @return 削除用のオブジェクト
     */
    private Discipline createDeletedDiscipline(Discipline old) {
        Discipline discipline = new Discipline();
        discipline.setId(old.getId());
        discipline.setUpdatedBy(getCurrentUser());
        discipline.setVersionNo(old.getVersionNo());
        return discipline;
    }
}
