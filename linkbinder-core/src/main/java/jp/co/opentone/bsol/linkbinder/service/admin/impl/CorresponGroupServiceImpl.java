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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
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
import jp.co.opentone.bsol.linkbinder.dao.CorresponGroupDao;
import jp.co.opentone.bsol.linkbinder.dao.CorresponGroupUserDao;
import jp.co.opentone.bsol.linkbinder.dao.DisciplineDao;
import jp.co.opentone.bsol.linkbinder.dao.ProjectUserProfileDao;
import jp.co.opentone.bsol.linkbinder.dao.SiteDao;
import jp.co.opentone.bsol.linkbinder.dao.UserDao;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroup;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroupUser;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroupUserMapping;
import jp.co.opentone.bsol.linkbinder.dto.Discipline;
import jp.co.opentone.bsol.linkbinder.dto.ProjectUser;
import jp.co.opentone.bsol.linkbinder.dto.ProjectUserProfile;
import jp.co.opentone.bsol.linkbinder.dto.SearchCorresponGroupResult;
import jp.co.opentone.bsol.linkbinder.dto.Site;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponCondition;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponGroupCondition;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchProjectUserProfileCondition;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchUserCondition;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.service.AbstractService;
import jp.co.opentone.bsol.linkbinder.service.admin.CorresponGroupService;

/**
 * このサービスではグループ情報に関する処理を提供する.
 * @author opentone
 */
@Service
@Transactional(rollbackFor = { ServiceAbortException.class })
public class CorresponGroupServiceImpl extends AbstractService implements CorresponGroupService {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -64015626786529212L;

    /**
     * プロパティに設定されたEXCELシート名のKEY.
     */
    private static final String SHEET_KEY = "excel.sheetname.correspongroupindex";

    /**
     * EXCELシート名がプロパティに設定されていない場合のデフォルト名.
     */
    private static final String SHEET_DEFAULT = "CorresponGroupIndex";

    /**
     * セキュリティレベル：GroupAdminを取得するためのKEY.
     */
    private static final String KEY_GROUP_ADMIN = "securityLevel.groupAdmin";

    /**
     * 区切り文字.
     */
    private static final String DELIMITER = ":";

    /**
     * デフォルト活動単位IDを表すカラム名.
     */
    private static final String COLUMN_DEFAULT_CORRESPON_GROUP_ID = "default_correspon_group_id";

    /**
     * 活動単位件数.
     */
    private int dataCount;

    /**
     * Excel出力する際のヘッダ名.
     */
    private static final List<String> HEADER;
    static {
        HEADER = new ArrayList<String>();
        HEADER.add("ID");
        HEADER.add("Discipline");
        HEADER.add("Group");
    }

    /**
     * Excel出力する際の出力項目.
     */
    private static final List<String> FIELDS;
    static {
        FIELDS = new ArrayList<String>();
        FIELDS.add("id");
        FIELDS.add("discipline.codeAndName");
        FIELDS.add("name");
    }

    /*
     * (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.service.correspon.CorresponGroupService#search(
     *      jp.co.opentone.bsol.linkbinder.dto.SearchCorresponGroupCondition)
     *
     */
    @Transactional(readOnly = true)
    public List<CorresponGroup> search(SearchCorresponGroupCondition condition)
            throws ServiceAbortException {
        ArgumentValidator.validateNotNull(condition);
        // グループ情報取得
        return searchCorresponGroup(condition);
    }

    /*
     * (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.service.admin.CorresponGroupService#findMembers(java
     * .lang.Long)
     */
    @Transactional(readOnly = true)
    public List<CorresponGroupUser> findMembers(Long id) {
        ArgumentValidator.validateNotNull(id);
        return findCorresponGroupUserByCorresponGroupId(id);
    }


    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.service.admin.CorresponGroupService#findCorresponGroupUserMappings()
     */
    @Transactional(readOnly = true)
    public List<CorresponGroupUserMapping> findCorresponGroupUserMappings() {
        CorresponGroupUserDao dao = getDao(CorresponGroupUserDao.class);
        return dao.findCorresponGroupUserMapping(getCurrentProjectId());
    }

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.service.admin.CorresponGroupService#findCorresponGroupUserIdMappings()
     */
    @Transactional(readOnly = true)
    public List<CorresponGroupUserMapping> findCorresponGroupIdUserMappings() {
        CorresponGroupUserDao dao = getDao(CorresponGroupUserDao.class);
        return dao.findCorresponGroupIdUserMapping(getCurrentProjectId());
    }

    /*
     * (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.service.admin.CorresponGroupService#find(java.lang.Long)
     */
    @Transactional(readOnly = true)
    public CorresponGroup find(Long id) throws ServiceAbortException {
        ArgumentValidator.validateNotNull(id);
        CorresponGroup corresponGroup = null;
        corresponGroup = findById(id);
        return corresponGroup;
    }

    /*
     * (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.service.admin.CorresponGroupService#searchPagingList
     * (jp.co.opentone.bsol.linkbinder.dto.SearchCorresponTypeCondition)
     */
    @Transactional(readOnly = true)
    public SearchCorresponGroupResult searchPagingList(SearchCorresponGroupCondition condition)
            throws ServiceAbortException {
        ArgumentValidator.validateNotNull(condition);
        // 権限共通チェック【3】
        validatePermissionSearch();
        // 拠点に紐つく活動単位を取得
        List<CorresponGroup> corresponGroupList = searchCorresponGroup(condition);
        // 該当データ0件の場合
        validateNotExistCorresponGroup(condition);
        // 指定のページを指定した際にデータが存在しない場合はエラー
        validateNoPageData(corresponGroupList);
        return createResult(corresponGroupList);
    }

    /*
     * (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.service.admin.CorresponGroupService#generateExcel(java
     * .util.List)
     */
    @Transactional(readOnly = true)
    public byte[] generateExcel(List<CorresponGroup> corresponGroups) throws ServiceAbortException {
        ArgumentValidator.validateNotNull(corresponGroups);
        try {
            String sheetName = SystemConfig.getValue(SHEET_KEY);
            if (StringUtils.isEmpty(sheetName)) {
                sheetName = SHEET_DEFAULT;
            }
            WorkbookGenerator generator =
                    new WorkbookGenerator(
                        sheetName, corresponGroups, getOutputField(), getHeaderNames(), true);
            return generator.generate();
        } catch (GeneratorFailedException e) {
            throw new ServiceAbortException(e.getMessage());
        }
    }

    /*
     * (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.service.admin.CorresponGroupService#add(jp.co.opentone.bsol.linkbinder
     * .dto.Site, jp.co.opentone.bsol.linkbinder.dto.Discipline)
     */
    public Long add(Site site, Discipline discipline) throws ServiceAbortException {
        ArgumentValidator.validateNotNull(site);
        ArgumentValidator.validateNotNull(discipline);
        // 権限共通チェック【2】
        validateNotPermission();
        // 同一プロジェクトにある有効な活動単位の同じ拠点(site_id) と 部門(discipline_id)が既に登録されている際はエラー
        validateExistSiteIdDisciplineId(site, discipline);
        // 拠点がマスタに存在しない場合はエラー
        findSiteById(site.getId());
        // ②選択された拠点が同一プロジェクトに属していない際はエラー
        validateSiteProjectDiff(site);
        // 部門がマスタに存在しない場合はエラー
        findDisciplineById(discipline.getId());
        // ②選択された部門が同一プロジェクトに属していない際はエラー
        validateDisciplineProjectDiff(discipline);
        // 登録
        CorresponGroup insertCg = createCorresponGroup(site, discipline);
        return insertCorresponGroup(insertCg);
    }

    /*
     * (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.service.admin.CorresponGroupService#delete(
     *      jp.co.opentone.bsol.linkbinder.dto.CorresponGroup)
     *
     */
    public void delete(CorresponGroup corresponGroup) throws ServiceAbortException {
        ArgumentValidator.validateNotNull(corresponGroup);
        // 権限共通チェック【2】
        validateNotPermission();
        // 該当データ0件の場合はエラー
        findById(corresponGroup.getId());
        // 削除対象の活動単位にユーザーが関連付けられている場合はエラー
        validateRelationCorresponGroupUser(corresponGroup);
        // 削除対象の活動単位が、登録済のコレポン文書に関連付けられている場合はエラー
        validateRelationCorrespon(corresponGroup);
        // 削除
        deleteCorresponGroup(createDeletedCorresponGroup(corresponGroup));
    }

    /*
     * (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.service.admin.CorresponGroupService#save(
     *      jp.co.opentone.bsol.linkbinder.dto.CorresponGroup, java.util.List)
     */
    public void save(CorresponGroup corresponGroup, List<User> users) throws ServiceAbortException {
        ArgumentValidator.validateNotNull(corresponGroup);
        ArgumentValidator.validateNotNull(users);
        // 保存処理の入力値を検証する
        validateSave(corresponGroup, users);
        // ユーザーを全て削除する
        deleteAllUser(corresponGroup.getId());
        // 除外されるユーザーのデフォルト活動単位の設定を削除する
        deleteDefaultCorresponGroup(corresponGroup.getId(), users);
        // ユーザーを登録する
        List<CorresponGroupUser> lstCgu = createCorresponGroupUserList(corresponGroup, users);
        insertCorresponGroupUserList(lstCgu);
        // 活動単位情報と対象データのバージョンナンバーが違う場合はエラー（排他チェック）
        // 更新処理
        CorresponGroup updateCorresponGroup = createCorresponGroupForUpdate(corresponGroup);
        updateCorresponGroup(updateCorresponGroup);

    }

    /*
     * (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.service.admin.CorresponGroupService#searchNotAdd()
     */
    public List<Discipline> searchNotAdd(Long siteId) {
        return findNotAdd(siteId);
    }

    /**
     * 活動単位を検索する.
     * @param condition 検索条件
     * @return 活動単位
     */
    @Transactional(readOnly = true)
    private List<CorresponGroup> searchCorresponGroup(SearchCorresponGroupCondition condition) {
        CorresponGroupDao dao = getDao(CorresponGroupDao.class);
        return dao.find(condition);
    }

    /**
     * コレポン一覧画面の出力パラメータを取得する.
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
     * IDを指定して活動単位情報を取得する.
     * @param id ID
     * @return 取得した活動単位情報
     * @throws ServiceAbortException 活動単位情報が取得できない
     */
    private CorresponGroup findById(Long id) throws ServiceAbortException {
        ArgumentValidator.validateNotNull(id);
        CorresponGroupDao dao = getDao(CorresponGroupDao.class);
        try {
            return dao.findById(id);
        } catch (RecordNotFoundException rnfe) {
            throw new ServiceAbortException(ApplicationMessageCode.NO_DATA_FOUND);
        }
    }

    /**
     * 一覧画面表示用オブジェクトを作成する.
     * @param corresponGroupList 活動単位リスト
     * @param disciplineList 部門情報リスト
     * @return 一覧画面表示用オブジェクト
     */
    private SearchCorresponGroupResult createResult(List<CorresponGroup> corresponGroupList) {
        SearchCorresponGroupResult result = new SearchCorresponGroupResult();
        result.setCount(dataCount);
        result.setCorresponGroupList(corresponGroupList);

        return result;
    }

    /**
     * 活動単位に登録されていない部門を取得する.
     * @return 部門情報
     */
    private List<Discipline> findNotAdd(Long siteId) {
        DisciplineDao dao = getDao(DisciplineDao.class);
        return dao.findNotExistCorresponGroup(getCurrentProjectId(), siteId);
    }

    /**
     * 権限チェックを行う.
     * @return 権限があるtrue / ないfalse
     */
    private boolean isValidPermission() {
        return isSystemAdmin(getCurrentUser())
            || isProjectAdmin(getCurrentUser(), getCurrentProjectId());
    }

    /**
     * 検索処理の権限チェックがない場合はエラー.
     * @throws ServiceAbortException 権限がない
     */
    private void validatePermissionSearch() throws ServiceAbortException {
        if (!isValidPermissionThree()) {
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT);
        }
    }

    /**
     * 権限共通チェック【3】を行う.
     * @return 権限があるtrue / ないfalse
     */
    private boolean isValidPermissionThree() {
        User loginUser = getCurrentUser();
        String projectId = getCurrentProjectId();
        return isSystemAdmin(loginUser) || isProjectAdmin(loginUser, projectId)
            || isAnyGroupAdmin(projectId);
    }

    /**
     * 該当する活動単位が存在するか判定する.
     * @param condition 検索条件
     * @return 該当するtrue / 該当しないfalse
     * @throws ServiceAbortException 取得失敗
     */
    private boolean isExistCorresponGroup(SearchCorresponGroupCondition condition)
            throws ServiceAbortException {
        dataCount = countCorresponGroup(condition);

        return !(dataCount == 0);
    }

    /**
     * 該当する活動単位が存在しない場合はエラー.
     * @param condition 検索条件
     * @throws ServiceAbortException 存在しない
     */
    private void validateNotExistCorresponGroup(SearchCorresponGroupCondition condition)
            throws ServiceAbortException {
        if (!isExistCorresponGroup(condition)) {
            throw new ServiceAbortException(ApplicationMessageCode.NO_DATA_FOUND);
        }
    }

    /**
     * 指定のページを指定した際にデータが存在しない場合はエラー.
     * @param corresponGroupList 活動単位
     * @throws ServiceAbortException データが存在しない
     */
    private void validateNoPageData(List<CorresponGroup> corresponGroupList)
            throws ServiceAbortException {
        if (corresponGroupList.isEmpty()) {
            throw new ServiceAbortException(ApplicationMessageCode.NO_PAGE_FOUND);
        }
    }

    /**
     * 活動単位の件数を取得する.
     * @param condition 検索条件
     * @return 活動単位件数
     */
    private int countCorresponGroup(SearchCorresponGroupCondition condition) {
        CorresponGroupDao dao = getDao(CorresponGroupDao.class);
        return dao.countCorresponGroup(condition);

    }

    /**
     * 同一プロジェクトに同じ拠点、部門が既に登録されているか判定する.
     * @param corresponGroup 活動単位
     * @return 登録されているtrue / 登録されていないfalse
     */
    private boolean isExistSiteIdDisciplineId(Site site, Discipline discipline) {
        SearchCorresponGroupCondition condition = new SearchCorresponGroupCondition();
        condition.setProjectId(getCurrentProjectId());
        condition.setSiteId(site.getId());
        condition.setDisciplineId(discipline.getId());
        List<CorresponGroup> cg = searchCorresponGroup(condition);
        return cg.size() > 0;

    }

    /**
     * 拠点IDを指定して拠点情報を取得する.
     * @param siteId 拠点ID
     * @return 拠点情報
     * @throws ServiceAbortException 取得失敗
     */
    private Site findSiteById(Long siteId) throws ServiceAbortException {
        SiteDao dao = getDao(SiteDao.class);
        try {
            return dao.findById(siteId);
        } catch (RecordNotFoundException e) {
            throw new ServiceAbortException(ApplicationMessageCode.NO_DATA_FOUND);
        }
    }

    /**
     * 部門IDを指定して部門情報を取得する.
     * @param disciplineId 部門ID
     * @return 部門情報
     * @throws ServiceAbortException 取得失敗
     */
    private Discipline findDisciplineById(Long disciplineId) throws ServiceAbortException {
        DisciplineDao dao = getDao(DisciplineDao.class);
        try {
            return dao.findById(disciplineId);
        } catch (RecordNotFoundException e) {
            throw new ServiceAbortException(ApplicationMessageCode.NO_DATA_FOUND);
        }
    }

    /**
     * 活動単位を登録する.
     * @param cg 活動単位
     * @return 登録した活動単位のID
     * @throws ServiceAbortException 登録失敗
     */
    private Long insertCorresponGroup(CorresponGroup cg) throws ServiceAbortException {
        CorresponGroupDao dao = getDao(CorresponGroupDao.class);
        try {
            return dao.create(cg);
        } catch (KeyDuplicateException kde) {
            throw new ServiceAbortException(kde);
        }
    }

    /**
     * 登録用活動単位オブジェクトを作成する.
     * @param site 拠点情報
     * @param discipline 部門情報
     * @return 登録用活動単位
     */
    private CorresponGroup createCorresponGroup(Site site, Discipline discipline) {
        CorresponGroup cg = new CorresponGroup();
        cg.setSite(site);
        cg.setDiscipline(discipline);
        cg.setName(site.getSiteCd() + DELIMITER + discipline.getDisciplineCd());
        cg.setCreatedBy(getCurrentUser());
        cg.setUpdatedBy(getCurrentUser());
        return cg;
    }

    /**
     * 保存処理の入力値の検証を行う.
     * @param corresponGroup 活動単位
     * @param users ユーザー
     * @throws ServiceAbortException 検証エラー
     */
    private void validateSave(CorresponGroup corresponGroup, List<User> users)
            throws ServiceAbortException {
        ArgumentValidator.validateNotNull(corresponGroup);
        ArgumentValidator.validateNotNull(users);

        // 権限に関係するチェック
        validatePermissionSave(corresponGroup, users);

        // 該当データ0件の場合はエラー
        findById(corresponGroup.getId());

        // 活動単位のプロジェクトが現在選択中のプロジェクト以外はエラー
        validateProjectId(corresponGroup.getProjectId());

        // 選択されたuserがマスタに存在しない場合はエラー
        for (User u : users) {
            findUserByEmpNo(u.getEmpNo());
        }

        // 選択されたuserが同一プロジェクトに属していない場合はエラー
        validateUserProjectDiff(users);

    }

    /**
     * 従業員番号を指定して、ユーザー情報を取得する.
     * @param empNo 従業員番号
     * @return ユーザー情報
     * @throws ServiceAbortException 取得できない
     */
    private User findUserByEmpNo(String empNo) throws ServiceAbortException {
        UserDao dao = getDao(UserDao.class);
        try {
            return dao.findByEmpNo(empNo);
        } catch (RecordNotFoundException e) {
            throw new ServiceAbortException(ApplicationMessageCode.NO_DATA_FOUND);
        }
    }

    /**
     * ユーザーが同一プロジェクトに属していない場合はエラー.
     * @param users ユーザー
     * @throws ServiceAbortException 同一プロジェクトに属していない
     */
    private void validateUserProjectDiff(List<User> users) throws ServiceAbortException {
        String projectId = getCurrentProjectId();
        SearchUserCondition condition = new SearchUserCondition();
        condition.setProjectId(projectId);
        List<ProjectUser> projectUsers = findProjectUser(condition);
        for (final User u : users) {
            //  プロジェクトに所属するユーザーリストに登録対象ユーザーが存在しなければエラー
            Object ret = CollectionUtils.find(projectUsers, new Predicate() {
                public boolean evaluate(Object object) {
                    ProjectUser pu = (ProjectUser) object;
                    return u.getEmpNo().equals(pu.getUser().getEmpNo());
                }
            });

            if (ret == null) {
                throw new ServiceAbortException(
                    "invalid user",
                    ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_USER_ALREADY_DELETED,
                    u.getLabel());
            }
        }
    }

    /**
     * 指定したプロジェクトに所属するユーザーを取得する.
     * @param condition 検索条件
     * @return プロジェクトに所属するユーザー
     */
    private List<ProjectUser> findProjectUser(SearchUserCondition condition) {
        UserDao dao = getDao(UserDao.class);
        return dao.findProjectUser(condition);

    }

    /**
     * 活動単位IDを指定して活動単位ユーザーを削除する.
     * @param corresponGroupId 活動単位ID
     * @return 削除した件数
     * @throws ServiceAbortException 削除失敗
     */
    private Integer deleteAllUser(Long corresponGroupId) throws ServiceAbortException {
        CorresponGroupUserDao dao = getDao(CorresponGroupUserDao.class);
        try {
            return dao.deleteByCorresponGroupId(corresponGroupId, getCurrentUser());
        } catch (KeyDuplicateException kde) {
            throw new ServiceAbortException(kde);
        }
    }

    /**
     * 活動単位ユーザーを登録する.
     * @param cgu 活動単位ユーザー
     * @return 登録したID
     * @throws ServiceAbortException 登録失敗
     */
    private Long insertCorresponGroupUser(CorresponGroupUser cgu) throws ServiceAbortException {
        CorresponGroupUserDao dao = getDao(CorresponGroupUserDao.class);
        try {
            return dao.create(cgu);
        } catch (KeyDuplicateException kde) {
            throw new ServiceAbortException(kde);
        }
    }

    /**
     * 活動単位ユーザーをまとめて登録する.
     * @param lstCgu 活動単位ユーザーリスト
     * @throws ServiceAbortException 登録失敗
     */
    private void insertCorresponGroupUserList(List<CorresponGroupUser> lstCgu)
            throws ServiceAbortException {
        for (CorresponGroupUser cgu : lstCgu) {
            insertCorresponGroupUser(cgu);
        }
    }

    /**
     * 活動単位ユーザーリストを作成する.
     * @param corresponGroup 活動単位
     * @param users ユーザー
     * @return 活動単位ユーザーリスト
     */
    private List<CorresponGroupUser> createCorresponGroupUserList(CorresponGroup corresponGroup,
        List<User> users) {
        List<CorresponGroupUser> lstCgu = new ArrayList<CorresponGroupUser>();

        CorresponGroupUser cgu = null;
        User loginUser = getCurrentUser();

        for (User u : users) {
            cgu = new CorresponGroupUser();
            cgu.setCorresponGroup(corresponGroup);
            cgu.setUser(u);
            cgu.setSecurityLevel(u.getSecurityLevel());
            cgu.setCreatedBy(loginUser);
            cgu.setUpdatedBy(loginUser);
            lstCgu.add(cgu);
        }

        return lstCgu;
    }

    /**
     * 更新用の活動単位オブジェクトを作成する.
     * @param corresponGroup 活動単位
     * @return 更新用活動単位
     */
    private CorresponGroup createCorresponGroupForUpdate(CorresponGroup corresponGroup) {
        CorresponGroup updateCorresponGroup = new CorresponGroup();
        updateCorresponGroup.setId(corresponGroup.getId());
        updateCorresponGroup.setName(corresponGroup.getName());
        updateCorresponGroup.setUpdatedBy(getCurrentUser());
        updateCorresponGroup.setVersionNo(corresponGroup.getVersionNo());

        return updateCorresponGroup;
    }

    /**
     * 指定した活動単位を更新する.
     * @param updateCorresponGroup 活動単位
     * @return 該当件数
     * @throws ServiceAbortException 更新失敗
     */
    private Integer updateCorresponGroup(CorresponGroup updateCorresponGroup)
            throws ServiceAbortException {
        CorresponGroupDao dao = getDao(CorresponGroupDao.class);
        try {
            return dao.update(updateCorresponGroup);
        } catch (KeyDuplicateException kde) {
            throw new ServiceAbortException(kde);
        } catch (StaleRecordException sre) {
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CORRESPON_GROUP_ALREADY_UPDATED);
        }
    }

    /**
     * 保存処理の権限に関係するチェックを行う.
     * @param corresponGroup 活動単位
     * @param users ユーザー
     * @throws ServiceAbortException チェックに引っかかった場合
     */
    private void validatePermissionSave(CorresponGroup corresponGroup, List<User> users)
            throws ServiceAbortException {
        // 権限共通チェック【3】
        if (!isValidPermissionThree()) {
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT);
        }

        User loginUser = getCurrentUser();
        String projectId = getCurrentProjectId();

        // GroupAdminの場合
        if (!isSystemAdmin(loginUser) && !isProjectAdmin(loginUser, projectId)
            && isAnyGroupAdmin(projectId)) {
            // 更新する活動単位に自分がGroupAdmineとして所属してない場合はエラー
            if (!isValidGroupAdminCorresponGroup(corresponGroup)) {
                throw new ServiceAbortException(
                    ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_PROJECT_DIFF);
            }

            // GroupAdminは、自分を変更できない。
            if (!isNotChangeMySelf(corresponGroup, users)) {
                throw new ServiceAbortException(
                    ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT);
            }
        }
    }

    /**
     * 自身が活動単位のGroupAdminか判定する.
     * @return GroupAdminならtrue / GroupAdminでないfalse
     */
    private boolean isValidGroupAdminCorresponGroup(CorresponGroup corresponGroup)
            throws ServiceAbortException {
        List<CorresponGroupUser> lstCgu = findMembers(corresponGroup.getId());

        String groupAdmin = SystemConfig.getValue(KEY_GROUP_ADMIN);

        for (CorresponGroupUser cgu : lstCgu) {
            if (cgu.getUser().getEmpNo().equals(getCurrentUser().getEmpNo())) {
                if (cgu.getSecurityLevel().equals(groupAdmin)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 自身を変更していないかを判定する.
     * @return 変更していないtrue / 変更したfalse
     */
    private boolean isNotChangeMySelf(CorresponGroup corresponGroup, List<User> users)
            throws ServiceAbortException {
        // 活動単位に所属しているユーザーを取得する
        List<CorresponGroupUser> lstCgu = findMembers(corresponGroup.getId());
        String beforeSecurityLevel = null;
        String afterSecurityLevel = null;

        User loginUser = getCurrentUser();
        // DBに保存されている自身のセキュリティレベルを取得する
        for (CorresponGroupUser cgu : lstCgu) {
            if (cgu.getUser().getEmpNo().equals(loginUser.getEmpNo())) {
                beforeSecurityLevel = cgu.getSecurityLevel();
                break;
            }
        }
        // 更新する自身のセキュリティレベルを取得する
        for (User u : users) {
            if (u.getEmpNo().equals(loginUser.getEmpNo())) {
                afterSecurityLevel = u.getSecurityLevel();
                break;
            }
        }
        // 更新前、更新後のセキュリティレベルを比較する
        return isChangeValue(beforeSecurityLevel, afterSecurityLevel);
    }

    /**
     * 変更前、変更後の値に差異があるか判定する.
     * @param before 変更前の値
     * @param after 変更後の値
     * @return 変更前、変更後が等しいtrue / 等しくないfalse
     */
    private boolean isChangeValue(String before, String after) {
        return StringUtils.isNotEmpty(before) && StringUtils.isNotEmpty(after)
            && before.equals(after);
    }

    /**
     * 活動単位にユーザーが関連付けられているか判定する.
     * @param corresponGroup 活動単位
     * @return 関連付けられているtrue / 関連付けられていないfalse
     */
    private boolean isRelationCorresponGroupUser(CorresponGroup corresponGroup) {
        List<CorresponGroupUser> cguList =
                findCorresponGroupUserByCorresponGroupId(corresponGroup.getId());
        return !cguList.isEmpty();
    }

    /**
     * 活動単位にユーザーが関連付けられていたらエラー.
     * @param corresponGroup 活動単位
     * @throws ServiceAbortException 活動単位にユーザーが関連付いてる
     */
    private void validateRelationCorresponGroupUser(CorresponGroup corresponGroup)
            throws ServiceAbortException {
        if (isRelationCorresponGroupUser(corresponGroup)) {
            throw new ServiceAbortException(
                ApplicationMessageCode.
                    CANNOT_PERFORM_BECAUSE_CORRESPON_GROUP_ALREADY_RELATED_WITH_USER);
        }
    }

    /**
     * 活動単位IDを指定して活動単位ユーザーを取得する.
     * @param corresponGroupId 活動単位ID
     * @return 活動単位ユーザー
     */
    private List<CorresponGroupUser> findCorresponGroupUserByCorresponGroupId(
            Long corresponGroupId) {
        CorresponGroupUserDao dao = getDao(CorresponGroupUserDao.class);
        return dao.findByCorresponGroupId(corresponGroupId);
    }

    /**
     * 登録済のコレポン文書に関連付けられているか判定する.
     * @param corresponGroup 活動単位
     * @return 関連付けられているtrue / 関連付けられていないfalse
     */
    private boolean isRelationCorrespon(CorresponGroup corresponGroup) {
        int countCorrespon = countCorresponByCorresponGroup(corresponGroup);
        return countCorrespon > 0;
    }

    /**
     * 登録済みのコレポン文書に関連付いてる場合はエラー.
     * @param corresponGroup 活動単位
     * @throws ServiceAbortException 登録済みのコレポン文書に関連付いてる
     */
    private void validateRelationCorrespon(CorresponGroup corresponGroup)
            throws ServiceAbortException {
        if (isRelationCorrespon(corresponGroup)) {
            throw new ServiceAbortException(
                ApplicationMessageCode.
                    CANNOT_PERFORM_BECAUSE_CORRESPON_GROUP_ALREADY_ASSIGNED_TO_CORRESPON);
        }
    }

    /**
     * 活動単位を指定してコレポン文書件数を取得する.
     * @param corresponGroup 活動単位
     * @return コレポン文書件数
     */
    private int countCorresponByCorresponGroup(CorresponGroup corresponGroup) {
        CorresponGroup[] corresponGroups = {corresponGroup};
        SearchCorresponCondition condition;
        CorresponDao dao = getDao(CorresponDao.class);

        // Fromに設定されているグループ、To/Ccに設定されているグループの合計を返却
        int result = 0;
        condition = createCountCorresponGroupByCorresponGroupBaseCondition();
        condition.setFromGroups(corresponGroups);
        result += dao.count(condition);

        condition = createCountCorresponGroupByCorresponGroupBaseCondition();
        condition.setGroupTo(true);
        condition.setGroupCc(true);
        condition.setToGroups(corresponGroups);
        result += dao.count(condition);

        return result;
    }

    private SearchCorresponCondition createCountCorresponGroupByCorresponGroupBaseCondition() {
        SearchCorresponCondition condition = new SearchCorresponCondition();
        condition.setSimpleSearch(false);
        // 全てのコレポン文書を対象にする
        condition.setSystemAdmin(true);
        condition.setUserId(getCurrentUser().getEmpNo());
        condition.setProjectId(getCurrentProjectId());

        return condition;
    }

    /**
     * 削除する活動単位を作成する.
     * @param old
     *            削除対象の活動単位
     * @return 削除用のオブジェクト
     */
    private CorresponGroup createDeletedCorresponGroup(CorresponGroup old) {
        CorresponGroup corresponGroup = new CorresponGroup();
        corresponGroup.setId(old.getId());
        corresponGroup.setUpdatedBy(getCurrentUser());
        corresponGroup.setVersionNo(old.getVersionNo());
        return corresponGroup;
    }

    /**
     * 活動単位を削除する.
     * @param corresponGroup 活動単位
     * @return 該当件数
     * @throws ServiceAbortException 削除失敗
     */
    private Integer deleteCorresponGroup(CorresponGroup corresponGroup)
            throws ServiceAbortException {
        CorresponGroupDao dao = getDao(CorresponGroupDao.class);
        try {
            return dao.delete(corresponGroup);
        } catch (KeyDuplicateException kde) {
            throw new ServiceAbortException(kde);
        } catch (StaleRecordException sre) {
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CORRESPON_GROUP_ALREADY_UPDATED);
        }
    }

    /**
     * 権限がない場合はエラー.
     * @throws ServiceAbortException 権限がない場合
     */
    private void validateNotPermission() throws ServiceAbortException {
        if (!isValidPermission()) {
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT);
        }
    }

    /**
     * 同一プロジェクトにある有効な活動単位の同じ拠点(site_id)と 部門(discipline_id)が既に登録されている際はエラー.
     * @param site 拠点
     * @param discipline 部門
     * @throws ServiceAbortException 拠点、部門が既に登録されている
     */
    private void validateExistSiteIdDisciplineId(Site site, Discipline discipline)
            throws ServiceAbortException {
        if (isExistSiteIdDisciplineId(site, discipline)) {
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_SITE_DISCIPLINE_ALREADY_EXISTS,
                    (Object) (site.getSiteCd() + DELIMITER + discipline.getDisciplineCd()));
        }
    }

    /**
     * 選択された拠点が同一プロジェクトに属していない際はエラー.
     * @param site 拠点
     * @throws ServiceAbortException 拠点が同一プロジェクトに属していない
     */
    private void validateSiteProjectDiff(Site site) throws ServiceAbortException {
        if (!getCurrentProjectId().equals(site.getProjectId())) {
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_PROJECT_DIFF);
        }
    }

    /**
     * 選択された部門が同一プロジェクトに属していない際はエラー.
     * @param discipline 部門
     * @throws ServiceAbortException 部門が同一プロジェクトに属していない
     */
    private void validateDisciplineProjectDiff(Discipline discipline) throws ServiceAbortException {
        if (!getCurrentProjectId().equals(discipline.getProjectId())) {
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_PROJECT_DIFF);
        }
    }

    /**
     * 指定されたリストに含まれないユーザーのデフォルト活動単位の設定を削除する.
     * @param corresponGroupId 活動単位ID
     * @param users ユーザーリスト
     * @throws ServiceAbortException
     */
    private void deleteDefaultCorresponGroup(Long corresponGroupId,
                                             List<User> users) throws ServiceAbortException {
        List<String> empNoList = new ArrayList<String>();
        for (User user : users) {
            empNoList.add(user.getEmpNo());
        }
        SearchProjectUserProfileCondition condition = new SearchProjectUserProfileCondition();
        condition.setDefaultCorresponGroupId(corresponGroupId);
        List<ProjectUserProfile> userProfiles = findProjectUserProfile(condition);
        for (ProjectUserProfile userProfile : userProfiles) {
            if (!empNoList.contains(userProfile.getUser().getEmpNo())) {
                updateProjectUserProfile(userProfile);
            }
        }
    }

    private List<ProjectUserProfile> findProjectUserProfile(
            SearchProjectUserProfileCondition condition) throws ServiceAbortException {
        ProjectUserProfileDao dao = getDao(ProjectUserProfileDao.class);
        return dao.findList(condition);
    }

    private void updateProjectUserProfile(
            ProjectUserProfile projectUserProfile) throws ServiceAbortException {
        ProjectUserProfileDao dao = getDao(ProjectUserProfileDao.class);
        try {
            dao.update(createUpdateProjectUserProfile(projectUserProfile));
        } catch (KeyDuplicateException e) {
            throw new ServiceAbortException(e);
        } catch (StaleRecordException e) {
            throw new ServiceAbortException(e);
        }
    }

    private ProjectUserProfile createUpdateProjectUserProfile(ProjectUserProfile old) {
        ProjectUserProfile updateObject = new ProjectUserProfile();
        updateObject.setId(old.getId());
        updateObject.setNullColumn(COLUMN_DEFAULT_CORRESPON_GROUP_ID);
        updateObject.setUpdatedBy(getCurrentUser());

        return updateObject;
    }
}
