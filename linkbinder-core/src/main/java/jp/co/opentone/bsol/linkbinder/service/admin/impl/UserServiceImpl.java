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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.co.opentone.bsol.framework.core.config.SystemConfig;
import jp.co.opentone.bsol.framework.core.dao.KeyDuplicateException;
import jp.co.opentone.bsol.framework.core.dao.RecordNotFoundException;
import jp.co.opentone.bsol.framework.core.dao.StaleRecordException;
import jp.co.opentone.bsol.framework.core.generator.GeneratorFailedException;
import jp.co.opentone.bsol.framework.core.generator.csv.CsvGenerator;
import jp.co.opentone.bsol.framework.core.generator.excel.WorkbookGenerator;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.core.util.ArgumentValidator;
import jp.co.opentone.bsol.framework.core.util.SQLConvertUtil;
import jp.co.opentone.bsol.linkbinder.dao.CorresponGroupDao;
import jp.co.opentone.bsol.linkbinder.dao.CorresponGroupUserDao;
import jp.co.opentone.bsol.linkbinder.dao.ProjectDao;
import jp.co.opentone.bsol.linkbinder.dao.ProjectUserProfileDao;
import jp.co.opentone.bsol.linkbinder.dao.UserDao;
import jp.co.opentone.bsol.linkbinder.dao.UserProfileDao;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroup;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroupUser;
import jp.co.opentone.bsol.linkbinder.dto.Project;
import jp.co.opentone.bsol.linkbinder.dto.ProjectUser;
import jp.co.opentone.bsol.linkbinder.dto.ProjectUserProfile;
import jp.co.opentone.bsol.linkbinder.dto.ProjectUserSetting;
import jp.co.opentone.bsol.linkbinder.dto.SearchUserResult;
import jp.co.opentone.bsol.linkbinder.dto.SysUsers;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.UserIndex;
import jp.co.opentone.bsol.linkbinder.dto.UserProfile;
import jp.co.opentone.bsol.linkbinder.dto.UserSettings;
import jp.co.opentone.bsol.linkbinder.dto.code.MasterDataImportResultStatus;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchProjectUserProfileCondition;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchUserCondition;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.service.AbstractService;
import jp.co.opentone.bsol.linkbinder.service.admin.UserService;
import jp.co.opentone.bsol.linkbinder.util.ValueFormatter;

/**
 * このサービスではユーザー情報に関する処理を提供する.
 * @author opentone
 */
@Service
@Transactional(rollbackFor = { ServiceAbortException.class })
public class UserServiceImpl extends AbstractService implements UserService {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 7212974195662208175L;

    /**
     * UserSetting画面のGroup(v_correspon_group_user)表示のソート列.
     */
    private static final String  USER_SETTING_GROUP_SORT_COLUMN = "correspon_group_name";

    /**
     * プロパティに設定されたEXCELシート名のKEY.
     */
    private static final String SHEET_KEY = "excel.sheetname.userindex";

    /**
     * EXCELシート名がプロパティに設定されていない場合のデフォルト名.
     */
    private static final String SHEET_DEFAULT = "UserIndex";

    /**
     * Excel出力する際のヘッダ名.
     */
    private static final List<String> HEADER;
    static {
        HEADER = new ArrayList<String>();
        HEADER.add("ユーザーID");
        HEADER.add("ユーザー名");
        HEADER.add("役割");
        HEADER.add("会社");
        HEADER.add("グループ");
        HEADER.add("プロジェクト管理者");
        HEADER.add("グループ管理者");
    }

    /**
     * Excel出力する際の出力項目.
     */
    private static final List<String> FIELDS;
    static {
        FIELDS = new ArrayList<String>();
        FIELDS.add("projectUser.user.empNo");
        FIELDS.add("projectUser.user.nameE");
        FIELDS.add("projectUser.user.role");
        FIELDS.add("projectUser.projectCompany.codeAndName");
        FIELDS.add("corresponGroup.name");
        FIELDS.add("projectAdminLabel");
        FIELDS.add("groupAdminLabel");
    }

    /**
     * ユーザー情報をCSV出力する際のヘッダ名.
     */
    private static final List<String> CSV_HEADER_USER;
    static {
        CSV_HEADER_USER = new ArrayList<String>();
        CSV_HEADER_USER.add("ユーザーID");
        CSV_HEADER_USER.add("ユーザー姓（英語）");
        CSV_HEADER_USER.add("ユーザーフルネーム（英語）");
        CSV_HEADER_USER.add("ユーザーフルネーム（日本語）");
        CSV_HEADER_USER.add("パスワード");
        CSV_HEADER_USER.add("メールアドレス");
        CSV_HEADER_USER.add("システム管理者");
        CSV_HEADER_USER.add("利用可否");
        CSV_HEADER_USER.add("プロジェクトID");
        CSV_HEADER_USER.add("プロジェクト管理者");
        CSV_HEADER_USER.add("ユーザーID利用開始日");
    }
    /**
     * SysUsersDTOとのマッピング.
     */
    private static final List<String> MAP_DTO_USER;
    static {
        MAP_DTO_USER = new ArrayList<String>();
        MAP_DTO_USER.add("empNo");
        MAP_DTO_USER.add("lastName");
        MAP_DTO_USER.add("nameE");
        MAP_DTO_USER.add("nameJ");
        MAP_DTO_USER.add("password");
        MAP_DTO_USER.add("mailAddress");
        MAP_DTO_USER.add("sysAdmFlg");
        MAP_DTO_USER.add("userRegistAprvFlg");
        MAP_DTO_USER.add("pjId");
        MAP_DTO_USER.add("pjAdmFlg");
        MAP_DTO_USER.add("userIdAt");
    }
    /**
     * CSV出力の際のエンコード
     */
    private static final String ENCODING = "csv.encoding";

    /** logger. */
    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    /*
     * (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.service.admin.UserService#search(jp.co.opentone.bsol.linkbinder.dto
     * .SearchUserCondition)
     */
    @Override
    @Transactional(readOnly = true)
    public List<ProjectUser> search(SearchUserCondition condition) throws ServiceAbortException {
        ArgumentValidator.validateNotNull(condition);

        if (StringUtils.isEmpty(condition.getProjectId())) {
            condition.setProjectId(getCurrentProjectId());
        }

        UserDao dao = getDao(UserDao.class);
        return dao.findProjectUser(condition);
    }

    /*
     * (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.service.admin.UserService#findByEmpNo(java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    public User findByEmpNo(String empNo) throws ServiceAbortException {
        ArgumentValidator.validateNotEmpty(empNo);

        UserDao dao = getDao(UserDao.class);
        try {
            return dao.findByEmpNo(empNo);
        } catch (RecordNotFoundException e) {
            throw new ServiceAbortException(e);
        }
    }

    /*
     * (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.service.admin.UserService#searchCorrseponGroup(java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    public List<CorresponGroupUser> searchCorrseponGroup(String projectId, String empNo)
            throws ServiceAbortException {
        ArgumentValidator.validateNotEmpty(empNo);
        ArgumentValidator.validateNotEmpty(projectId);

        CorresponGroupDao dao = getDao(CorresponGroupDao.class);

        return dao.findByEmpNo(projectId, empNo);
    }

    /*
     * (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.service.admin.UserService#searchCorrseponGroup(java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    public List<CorresponGroupUser> searchCorrseponGroupUsers(String projectId)
            throws ServiceAbortException {
        ArgumentValidator.validateNotEmpty(projectId);

        CorresponGroupUserDao dao = getDao(CorresponGroupUserDao.class);

        return dao.findProjectUserWithGroupByProjectId(projectId);
    }

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.service.admin.UserService#findUserSettings(java.lang.String)
     */
    @Override
    public UserSettings findUserSettings(String userId) throws ServiceAbortException {
        ArgumentValidator.validateNotEmpty(userId);

        User user = findUser(userId);

        List<Project> projects = findProjects(userId);
        List<ProjectUserSetting> projectUserSettings = createProjectUserSetting(userId, projects);

        return createUserSettings(user, projectUserSettings);
    }

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.service.admin.UserService#save(jp.co.opentone.bsol.linkbinder.dto.UserSettings)
     */
    @Override
    public void save(UserSettings settings) throws ServiceAbortException {
        ArgumentValidator.validateNotNull(settings);

        String userId = settings.getUser().getEmpNo();

        // デフォルトプロジェクトの設定
        String defaultProjectId = settings.getDefaultProjectId();
        //  保存対象のユーザーがSystem Adminの場合は、プロジェクトに所属していなくても
        //  デフォルトプロジェクトを保存できる
        if (!StringUtils.isEmpty(defaultProjectId)) {
            if (!settings.isSystemAdmin()) {
                checkDefaultProject(userId, defaultProjectId);
            }
        }
        //  排他制御のため、常にバージョンNoもを更新
        saveUserProfile(settings);

        // デフォルト活動単位の設定
        for (ProjectUserSetting projectSetting : settings.getProjectUserSettingList()) {
            Long defaultCorresponGroupId = projectSetting.getDefaultCorresponGroupId();
            String role = projectSetting.getRole();
            String projectId = projectSetting.getProject().getProjectId();
            CorresponGroup group = null;
            if (defaultCorresponGroupId != null) {
                group = checkDefaultCorresponGroup(userId, projectId, defaultCorresponGroupId);
            }
            saveProjectUserProfile(userId, projectId, group, role);
        }

        // メールアドレス/パスワード更新
        if (StringUtils.isNotEmpty(settings.getDefaultEmailAddress())
                || StringUtils.isNotEmpty(settings.getPassword())) {
            // メールアドレス重複チェック
            String mailAddress = settings.getDefaultEmailAddress();
            if (StringUtils.isNotEmpty(mailAddress)) {
                if (!validateMailAddressDuplicate(settings.getUser().getUserId(),
                        mailAddress)) {
                    throw new ServiceAbortException(ApplicationMessageCode.MAILADDRESS_DUPLICATED);
                }
            }

            SysUsers dto = new SysUsers();
            dto.setEmpNo(settings.getUser().getUserId());
            dto.setMailAddress(mailAddress);
            try {
                String password = settings.getPassword();
                if (StringUtils.isNotEmpty(password)) {
                    String key = settings.getUser().getEmpCreatedAt();
                    dto.setPassword(getPasswordHash(password, key));
                }
            } catch (Exception e) {
                throw new ServiceAbortException(
                        ApplicationMessageCode.ERROR_HASH_PASSWORD);
            }

             UserDao dao = getDao(UserDao.class);
             try {
                  dao.updateUserSetting(dto);
             } catch (RecordNotFoundException e) {
                 throw new ServiceAbortException(ApplicationMessageCode.ERROR_USER_NOT_EXIST);
             } catch (KeyDuplicateException | StaleRecordException e) {
                 throw new ServiceAbortException(ApplicationMessageCode.ERROR_UPDATE_USER);
             }
        }
    }

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.service.admin.UserService
     *     #searchPagingList(jp.co.opentone.bsol.linkbinder.dto.condition.SearchUserCondition)
     */
    @Override
    public SearchUserResult searchPagingList(SearchUserCondition condition)
            throws ServiceAbortException {
        ArgumentValidator.validateNotNull(condition);


        List<UserIndex> userIndexes = createUserIndexList(condition);

        // 指定のページを選択した際に、そのページに表示するデータが存在しない場合エラー
        checkPagingData(userIndexes);

        // 件数取得 -- 該当データ0件の場合エラー
        int count = getDataCount(condition);
        return createResult(count, userIndexes);
    }

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.service.admin.UserService#generateExcel(java.util.List)
     */
    @Override
    public byte[] generateExcel(List<UserIndex> userIndexs) throws ServiceAbortException {
        ArgumentValidator.validateNotNull(userIndexs);
        try {
            String sheetName = SystemConfig.getValue(SHEET_KEY);
            if (StringUtils.isEmpty(sheetName)) {
                sheetName = SHEET_DEFAULT;
            }
            WorkbookGenerator generator =
                    new WorkbookGenerator(
                        sheetName, userIndexs, FIELDS, HEADER, true);
            return generator.generate();
        } catch (GeneratorFailedException e) {
            throw new ServiceAbortException(e.getMessage());
        }
    }

    private User findUser(String userId) throws ServiceAbortException {
        try {
            UserDao dao = getDao(UserDao.class);
            return dao.findByEmpNo(userId);
        } catch (RecordNotFoundException e) {
            throw new ServiceAbortException(ApplicationMessageCode.NO_DATA_FOUND);
        }
    }

    private List<Project> findProjects(String userId) {
        ProjectDao dao = getDao(ProjectDao.class);
        return dao.findByEmpNo(userId);
    }

    private List<ProjectUserSetting> createProjectUserSetting(String userId,
                                          List<Project> projects) throws ServiceAbortException {
        List<ProjectUserSetting> list = new ArrayList<ProjectUserSetting>();
        for (Project project : projects) {
            ProjectUserSetting setting = new ProjectUserSetting();
            setting.setProject(project);
            setting.setProjectUser(findProjectUser(project.getProjectId(), userId));
            setting.setCorresponGroupUserList(
                findCorresponGroupUserList(userId, project.getProjectId(),
                                            USER_SETTING_GROUP_SORT_COLUMN));
            list.add(setting);

        }
        return list;
    }

    private List<CorresponGroupUser> findCorresponGroupUserList(String userId,
                                                                String projectId,
                                                                String sortColumn) {
        CorresponGroupDao dao = getDao(CorresponGroupDao.class);
        return dao.findByEmpNo(projectId, userId, SQLConvertUtil.encode(sortColumn));
    }

    private List<CorresponGroupUser> findCorresponGroupUserList(String userId, String projectId) {
        CorresponGroupDao dao = getDao(CorresponGroupDao.class);
        return dao.findByEmpNo(projectId, userId);
    }

    private UserSettings createUserSettings(User user,
                                            List<ProjectUserSetting> projectUserSettingList) {
        UserSettings settings = new UserSettings();
        settings.setUser(user);
        settings.setProjectUserSettingList(projectUserSettingList);

        // 現在のバージョンNoを取得し、自分自身ならばfeedKeyを取得する.
        UserProfile p = findUserProfile(user.getEmpNo());
        if (p != null) {
            settings.setUserProfileVersionNo(p.getVersionNo());
            if (getCurrentUser().getEmpNo().equals(user.getEmpNo())) {
                settings.setRssFeedKey(p.getFeedKey());
            }
        }
        return settings;
    }

    private void checkDefaultProject(String userId, String projectId) throws ServiceAbortException {
        if (findProjectUser(projectId, userId) == null) {
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_PROJECT_NOT_RELATED_WITH_USER);
        }
    }

    private void saveUserProfile(UserSettings settings) throws ServiceAbortException {
        UserProfile p = findUserProfile(settings.getUser().getEmpNo());
        if (p == null) {
            User u = findUser(settings.getUser().getEmpNo());
            u.setUseLearning(settings.getUser().isUseLearning());
            createUserProfile(u, settings.getDefaultProjectId());
        } else {
            //  更新前のバージョンNoを再設定して更新
            p.setVersionNo(settings.getUserProfileVersionNo());
            p.setUseLearning(settings.getUser().isUseLearning());
            updateUserProfile(p, settings.getDefaultProjectId());
        }
    }

    private UserProfile findUserProfile(String userId) {
        UserProfileDao dao = getDao(UserProfileDao.class);
        return dao.findByEmpNo(userId);
    }

    private void createUserProfile(User user, String projectId) throws ServiceAbortException {
        UserProfile profile = new UserProfile();
        profile.setUser(user);
        profile.setDefaultProjectId(projectId);
        profile.setUseLearning(user.isUseLearning());
        profile.setCreatedBy(getCurrentUser());
        profile.setUpdatedBy(getCurrentUser());

        UserProfileDao dao = getDao(UserProfileDao.class);
        try {
            dao.create(profile);
        } catch (KeyDuplicateException e) {
            throw new ServiceAbortException(e);
        }
    }

    private void updateUserProfile(UserProfile old, String projectId)
            throws ServiceAbortException {
        UserProfile profile = new UserProfile();
        profile.setId(old.getId());
        profile.setVersionNo(old.getVersionNo());
        profile.setDefaultProjectId(projectId);
        profile.setUseLearning(old.isUseLearning());
        profile.setUpdatedBy(getCurrentUser());

        UserProfileDao dao = getDao(UserProfileDao.class);
        try {
            dao.update(profile);
        } catch (KeyDuplicateException e) {
            throw new ServiceAbortException(e);
        } catch (StaleRecordException e) {
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_USER_SETTINGS_ALREADY_UPDATED);
        }
    }

    private CorresponGroup checkDefaultCorresponGroup(String userId,
                                            String projectId,
                                            Long corresponGroupId) throws ServiceAbortException {
        List<CorresponGroupUser> corresponGroupUserList
            = findCorresponGroupUserList(userId, projectId);
        for (CorresponGroupUser corresponGroupUser : corresponGroupUserList) {
            if (corresponGroupId.equals(corresponGroupUser.getCorresponGroup().getId())) {
                return corresponGroupUser.getCorresponGroup();
            }
        }

        //該当無し
        throw new ServiceAbortException(
            ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CORRESPON_GROUP_NOT_RELATED_WITH_USER);
    }

    private void saveProjectUserProfile(String userId,
                                        String projectId,
                                        CorresponGroup group,
                                        String role) throws ServiceAbortException {
        ProjectUserProfile userProfile = findProjectUserProfile(userId, projectId);
        if (userProfile == null) {
            User user = findUser(userId);
            createProjectUserProfile(user, projectId, group, role);
        } else {
            updateProjectUserProfile(userProfile, group, role);
        }
    }

    private ProjectUserProfile findProjectUserProfile(String userId, String projectId) {
        SearchProjectUserProfileCondition condition = new SearchProjectUserProfileCondition();
        condition.setEmpNo(userId);
        condition.setProjectId(projectId);
        ProjectUserProfileDao dao = getDao(ProjectUserProfileDao.class);
        return dao.find(condition);
    }

    private void createProjectUserProfile(User user,
                                          String projectId,
                                          CorresponGroup group,
                                          String role) throws ServiceAbortException {
        ProjectUserProfile profile = new ProjectUserProfile();
        profile.setUser(user);
        profile.setProjectId(projectId);
        profile.setRole(role);
        profile.setDefaultCorresponGroup(group);
        profile.setCreatedBy(getCurrentUser());
        profile.setUpdatedBy(getCurrentUser());

        ProjectUserProfileDao dao = getDao(ProjectUserProfileDao.class);
        try {
            dao.create(profile);
        } catch (KeyDuplicateException e) {
            throw new ServiceAbortException(e);
        }
    }

    private void updateProjectUserProfile(ProjectUserProfile old,
                                          CorresponGroup group,
                                          String role) throws ServiceAbortException {
        ProjectUserProfile profile = new ProjectUserProfile();
        profile.setId(old.getId());
        profile.setRole(role);
        profile.setDefaultCorresponGroup(group);
        profile.setUpdatedBy(getCurrentUser());

        ProjectUserProfileDao dao = getDao(ProjectUserProfileDao.class);
        try {
            dao.update(profile);
        } catch (KeyDuplicateException e) {
            throw new ServiceAbortException(e);
        } catch (StaleRecordException e) {
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_USER_SETTINGS_ALREADY_UPDATED);
        }
    }

    private int getDataCount(SearchUserCondition condition) throws ServiceAbortException {
        UserDao dao = getDao(UserDao.class);
        int count = dao.count(condition);
        // 該当データ0件の場合
        if (count == 0) {
            throw new ServiceAbortException(ApplicationMessageCode.NO_DATA_FOUND);
        }
        return count;
    }

    private void checkPagingData(List<UserIndex> userIndexList)
        throws ServiceAbortException {
        // 該当データ0件の場合
        if (userIndexList.size() == 0) {
            throw new ServiceAbortException(ApplicationMessageCode.NO_PAGE_FOUND);
        }
    }

    private List<UserIndex> createUserIndexList(SearchUserCondition condition)
            throws ServiceAbortException {

        List<UserIndex> userIndexList = new ArrayList<UserIndex>();
        List<ProjectUser> projectUserList = search(condition);

        for (ProjectUser pUser : projectUserList) {
            userIndexList.add(createUserIndex(pUser));
        }

        return userIndexList;
    }

    private UserIndex createUserIndex(ProjectUser pUser) throws ServiceAbortException {

        UserIndex index = new UserIndex();
        index.setProjectUser(pUser);
        index.setCorresponGroup(getCorresponGroup(pUser));
        User user = pUser.getUser();
        index.setSystemAdmin(isSystemAdmin(getUser(user.getEmpNo()))); // SystemAdmin判別のため再取得
        String projectId = pUser.getProjectId();
        index.setProjectAdmin(isProjectAdmin(user, projectId));
        index.setGroupAdmin(isAnyGroupAdmin(user, projectId));
        index.setPermitUpdate(checkPermitUpdate(projectId, getCurrentUser(), user));

        return index;
    }

    private SearchUserResult createResult(int count, List<UserIndex> userIndexes) {
        SearchUserResult result = new SearchUserResult();
        result.setCount(count);
        result.setUserIndexes(userIndexes);

        return result;
    }

    private CorresponGroup getCorresponGroup(ProjectUser projectUser) {
        if (projectUser.getDefaultCorresponGroup() != null) {
            return projectUser.getDefaultCorresponGroup();
        }

        List<CorresponGroupUser> corresponGroupUserList
            = findCorresponGroupUserList(projectUser.getUser().getEmpNo(),
                                         projectUser.getProjectId());
        if (!corresponGroupUserList.isEmpty()) {
            return corresponGroupUserList.get(0).getCorresponGroup();
        }

        return null;
    }

    private boolean checkPermitUpdate(String projectId, User loginUser, User indexUser) {
        return isSystemAdmin(loginUser)
                   || isProjectAdmin(loginUser, projectId)
                   || isAnyGroupGroupAdmin(projectId, loginUser, indexUser);
    }

    private User getUser(String empNo) throws ServiceAbortException {
        UserDao dao = getDao(UserDao.class);
        User user = null;
        try {
            user = dao.findByEmpNo(empNo);
        } catch (RecordNotFoundException e) {
            throw new ServiceAbortException(e);
        }
        return user;
    }

    private boolean isAnyGroupGroupAdmin(String projectId, User loginUser, User indexUser) {
        List<CorresponGroupUser> corresponGroupUserList
            = findCorresponGroupUserList(indexUser.getEmpNo(), projectId);

        for (CorresponGroupUser groupUser : corresponGroupUserList) {
            if (isGroupAdmin(loginUser, groupUser.getCorresponGroup().getId())) {
                return true;
            }
        }
        return false;
    }

    /**
     * メールアドレス取得処理.
     */
    @Override
    public String findEMailAddress(String empNo) throws ServiceAbortException {
        ArgumentValidator.validateNotEmpty(empNo);

        String eMailAddress = null;
        try {
            UserDao dao = getDao(UserDao.class);
            User user = null;
            user = dao.findByEmpNo(empNo);
            eMailAddress = user.getEmailAddress();

            return eMailAddress;
        } catch (Exception e) {
            log.warn("There was an error getting the e-mail address.[" + e.getMessage() + "]" , e);
            throw new ServiceAbortException(
                    e.getMessage(), e, ApplicationMessageCode.ERROR_GETTING_EMAIL_ADDRESS);
        }
    }
    /**
     * 新規登録か更新か判定する.
     * @param site
     *            拠点情報
     * @return 登録ならtrue / 更新ならfalse
     * @throws ServiceAbortException
     */
    private boolean isNew(String empId) throws ServiceAbortException {
        return  findSysUser(empId) == null ;

    }

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.service.admin.ProjectService#find(java.lang.Long)
     */
    public String findSysUser(String empId) throws ServiceAbortException {
        if (StringUtils.isEmpty(empId)) {
            return null;
        }
        SysUsers user = new SysUsers();
        user.setEmpNo(empId);
        UserDao dao = getDao(UserDao.class);
        try {
            return dao.findBySysUserId(user);
        } catch (RecordNotFoundException e) {
            return null;
        }
    }

    @Override
    public List<User> findAll() throws ServiceAbortException {
        UserDao dao = getDao(UserDao.class);
        try {
            return dao.findAll();
        } catch (RecordNotFoundException e) {
            return null;
        }
    }

    /**
     * CSV出力処理.
     */
    @Override
    public byte[] generateCSV(List<User> users)
            throws ServiceAbortException {
        ArgumentValidator.validateNotNull(users);
        try {

            CsvGenerator generator =
                    new CsvGenerator(users, MAP_DTO_USER, CSV_HEADER_USER, SystemConfig.getValue(ENCODING));
            return generator.generate();
        } catch (GeneratorFailedException e) {
            throw new ServiceAbortException(e.getMessage());
        }
    }

    private String getPasswordHash(String value, String key) {
        String hashValue = ValueFormatter.formatValueToHash(value, key);
        return hashValue;

    }


    @Override
    public int getSysUserCount() throws ServiceAbortException {
        int count = 0;
        try {
            UserDao dao = getDao(UserDao.class);
            count =dao.countSystemAdminUser();
        } catch (RecordNotFoundException e) {
            throw new ServiceAbortException(ApplicationMessageCode.ERROR_USER_NOT_EXIST);
        } catch (Exception e){
            throw new ServiceAbortException(ApplicationMessageCode.ERROR_USER_NOT_EXIST);
        }

        return count;
    }

    /* (非 Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.service.admin.UserService#save(java.util.List)
     */
    @Override
    public List<SysUsers> save(List<SysUsers> userList) throws ServiceAbortException {
        List<SysUsers> result = new ArrayList<>();

        for (SysUsers p : userList) {
            saveUser(p);
            result.add(p);
        }

        return result;
    }

    /* (非 Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.service.admin.UserService#delete(java.util.List)
     */
    @Override
    public List<SysUsers> delete(List<SysUsers> userList) throws ServiceAbortException {
        List<SysUsers> result = new ArrayList<>();

        for (SysUsers p : userList) {
            deleteUser(p);
            result.add(p);
        }

        return result;
    }

    private boolean validateMailAddressDuplicate(String empNo, String mailAddress) {
        UserDao dao = getDao(UserDao.class);
        return dao.countEmail(empNo, mailAddress) == 0;
    }

    private void saveUser(SysUsers user) throws ServiceAbortException {
        UserDao dao = getDao(UserDao.class);

        // メールアドレス重複チェック
        if (StringUtils.isNotEmpty(user.getMailAddress())) {
            if (!validateMailAddressDuplicate(user.getEmpNo(), user.getMailAddress())) {
                throw new ServiceAbortException(
                        "メールアドレス重複",
                        ApplicationMessageCode.MAILADDRESS_DUPLICATED_BULK_UPDATE,
                        user.getMailAddress());
            }
        }

        if (isNew(user.getEmpNo())) {
            // 新規登録
            try {
                // パスワードハッシュ化
                Date date = new Date();
                SimpleDateFormat sdf1 = new SimpleDateFormat(
                        "yyyy-MM-dd HH:mm:ss");
                String key = sdf1.format(date);
                if (StringUtils.isNotEmpty(user.getPassword())) {
                    user.setPassword(getPasswordHash(user.getPassword(), key));
                } else {
                    user.setPassword(null);
                }
                user.setCreatedIdAt(key);
                if (StringUtils.isEmpty(user.getPjId())) {
                    dao.create(user);

                } else {
                    dao.creteUser(user);
                }

                user.setImportResultStatus(MasterDataImportResultStatus.CREATED);
            } catch (KeyDuplicateException e) {
                throw new ServiceAbortException("ユーザーが重複しています。",
                        ApplicationMessageCode.ERROR_USER_DUPLICATED,
                        user.getEmpNo());
            }
        } else {
            // 更新
            try {
                User defaultUser = dao.findByEmpNo(user.getEmpNo());
                if (StringUtils.isNotEmpty(user.getPassword())) {
                    user.setPassword(getPasswordHash(user.getPassword(),
                            defaultUser.getEmpCreatedAt()));
                } else {
                    user.setPassword(null);
                }

                dao.updateUser(user);
                user.setImportResultStatus(MasterDataImportResultStatus.UPDATED);
            } catch (RecordNotFoundException e) {
                throw new ServiceAbortException("ユーザーが存在しません",
                        ApplicationMessageCode.ERROR_USER_NOT_FOUND,
                        user.getEmpNo());
            } catch (KeyDuplicateException | StaleRecordException e) {
                throw new ServiceAbortException("ユーザーの更新に失敗しました",
                        ApplicationMessageCode.ERROR_USER_FAILED_TO_UPDATE,
                        user.getEmpNo());
            }
        }
    }

    /**
     * 指定されたユーザ削除処理.
     * @throws ServiceAbortException
     */
    private void deleteUser(SysUsers user) throws ServiceAbortException {
        if (!isNew(user.getEmpNo())) {
            UserDao dao = getDao(UserDao.class);
            try {
                dao.deleateUser(user);
                user.setImportResultStatus(MasterDataImportResultStatus.DELETED);
            } catch (RecordNotFoundException e) {
                throw new ServiceAbortException("ユーザーが存在しません",
                        ApplicationMessageCode.ERROR_USER_NOT_FOUND,
                        user.getEmpNo());
            } catch (KeyDuplicateException | StaleRecordException e) {
                throw new ServiceAbortException("ユーザーの削除に失敗しました",
                        ApplicationMessageCode.ERROR_USER_FAILED_TO_DELETE,
                        user.getEmpNo());
            }
        } else {
            throw new ServiceAbortException("ユーザーが存在しません",
                    ApplicationMessageCode.ERROR_USER_NOT_FOUND,
                    user.getEmpNo());
        }
    }
}
