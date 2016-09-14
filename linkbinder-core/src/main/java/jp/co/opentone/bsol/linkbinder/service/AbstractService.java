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
import jp.co.opentone.bsol.framework.core.filestore.FileStoreClient;
import jp.co.opentone.bsol.framework.core.service.IService;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.core.util.ArgumentValidator;
import jp.co.opentone.bsol.linkbinder.Constants;
import jp.co.opentone.bsol.linkbinder.dao.CorresponGroupDao;
import jp.co.opentone.bsol.linkbinder.dao.DaoFinder;
import jp.co.opentone.bsol.linkbinder.dao.ProjectDao;
import jp.co.opentone.bsol.linkbinder.dao.UserDao;
import jp.co.opentone.bsol.linkbinder.dto.Correspon;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroup;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroupUser;
import jp.co.opentone.bsol.linkbinder.dto.Project;
import jp.co.opentone.bsol.linkbinder.dto.ProjectUser;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.code.ForLearning;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchUserCondition;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Servcieの抽象クラス.
 * @author opentone
 */
public abstract class AbstractService implements ApplicationContextAware, IService, Serializable {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -185094583208229621L;

    /**
     * Daoの実装クラスとMockクラスを切り替えるためのキー情報.
     */
    public static final String KEY_DAO_USE_MOCK = "dao.use.mock";

    /**
     * logger.
     */
    private Logger log = LoggerFactory.getLogger(getClass());

    /**
     * Spring frameworkのApplicationContext.
     * <p>
     * {@link AbstractService#getDao(Class)}で、Springの管理下にある Daoオブジェクトを取得するために利用
     * </p>
     */
    private ApplicationContext applicationContext;

    /**
     * ログインユーザー.
     */
    @Resource
    //CHECKSTYLE:OFF
    protected User currentUser;
    //CHECKSTYLE:ON

    /**
     * ユーザー権限クラス.
     */
    @Resource
    private UserPermissionHelper userPermissionHelper;

    /**
     * ユーザー承認権限クラス.
     */
    @Resource
    private UserRoleHelper userRoleHelper;

    /**
     * ワークフローヘルパ.
     */
    @Resource
    private WorkflowHelper workflowHelper;

    /**
     * Dao取得クラス.
     */
    @Resource
    private DaoFinder daoFinder;

    /**
     * 空のインスタンスを生成する.
     */
    public AbstractService() {
    }

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
     * 現在のプロジェクトが学習用プロジェクトか否かを返す.
     * @return 学習用プロジェクトであればtrue.
     */
    public boolean isLearningProject() {
        Project project = getCurrentProject();
        if (project.getForLearning() == ForLearning.LEARNING) {
            return true;
        } else {
            return false;
        }
    }

    /*
     * (非 Javadoc)
     * @see
     * org.springframework.context.ApplicationContextAware#setApplicationContext
     * (org.springframework.context.ApplicationContext)
     */
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * 現在ログイン中のユーザーを返す.
     * @return ログインユーザー
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * 指定されたユーザーがSystem Adminの権限を持つ場合はtrue.
     * @param user
     *            対象ユーザー
     * @return System Adminの権限を持つ場合はtrue、それ以外はfalse
     */
    public boolean isSystemAdmin(User user) {
        ArgumentValidator.validateNotNull(user);

        return userPermissionHelper.isSystemAdmin(user);
    }

    /**
     * 指定されたユーザーが、指定されたプロジェクトに対するProject Adminの権限を持つ場合はtrue.
     * @param user
     *            対象ユーザー
     * @param projectId
     *            対象プロジェクトID
     * @return Project Adminの権限を持つ場合はtrue、それ以外はfalse
     */
    public boolean isProjectAdmin(User user, String projectId) {
        ArgumentValidator.validateNotNull(user);
        ArgumentValidator.validateNotEmpty(projectId);

        return userPermissionHelper.isProjectAdmin(user, projectId);
    }

    /**
     * 指定されたユーザーのプロジェクト-ユーザー情報を返す.
     * @param projectId プロジェクトID
     * @param empNo 従業員番号
     * @return プロジェクトユーザー情報
     */
    protected ProjectUser findProjectUser(String projectId, String empNo) {
        return userPermissionHelper.findProjectUser(projectId, empNo);
    }

    /**
     * 現在ログイン中ユーザーのプロジェクトユーザー情報を返す.
     * @return プロジェクトユーザー情報
     */
    public ProjectUser getCurrentProjectUser() {
        ProcessContext container = ProcessContext.getCurrentContext();

        Map<String, Object> values = container.getValue(SystemConfig.KEY_ACTION_VALUES);
        return (ProjectUser) values.get(Constants.KEY_PROJECT_USER);
    }

    /**
     * 指定されたユーザーが、指定された活動単位に対するGroup Adminの権限を持つ場合はtrue.
     * @param user
     *            対象ユーザー
     * @param corresponGroupId
     *            活動単位を識別するID
     * @return Group Adminの権限を持つ場合はtrue、それ以外はfalse
     */
    public boolean isGroupAdmin(User user, Long corresponGroupId) {
        ArgumentValidator.validateNotNull(user);
        ArgumentValidator.validateNotNull(corresponGroupId);

        return userPermissionHelper.isGroupAdmin(user, corresponGroupId);
    }

    /**
     * 現在ログイン中のユーザーが参照可能なプロジェクトを返す.
     * @return ログインユーザーが参照可能なプロジェクトの一覧
     */
    public List<Project> getAccessibleProjects() {
        ProjectDao dao = getDao(ProjectDao.class);
        return dao.findByEmpNo(getCurrentUser().getEmpNo());
    }

    /**
     * 現在ログイン中のユーザーが指定されたプロジェクトIDを参照可能であるか検証する.
     * @param projectId
     *            操作対象のプロジェクトID
     * @throws ServiceAbortException
     *             プロジェクトID参照不可
     */
    public void validateProjectId(String projectId) throws ServiceAbortException {
        ArgumentValidator.validateNotEmpty(projectId);

        // プロジェクト選択が前提のサービスの場合は
        // 異なるプロジェクトの情報にアクセスしてはいけない
        String currentProjectId = getCurrentProjectId();
        if (StringUtils.isNotEmpty(currentProjectId) && !currentProjectId.equals(projectId)) {
            throw new ServiceAbortException(
                        String.format(
                               "Invalid operation. "
                             + "Project '%s' is not equal a current project '%s'.",
                               projectId, currentProjectId),
                        ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_PROJECT_DIFF);
        }

        // System Adminの場合は全プロジェクトを参照可能なのでチェックしない
        if (isSystemAdmin(getCurrentUser())) {
            return;
        }

        boolean contains = false;
        for (Project p : getAccessibleProjects()) {
            if (projectId.equals(p.getProjectId())) {
                contains = true;
                break;
            }
        }
        if (!contains) {
            throw new ServiceAbortException(
                        String.format(
                            "Invalid operation. Project is not accessible. %s",
                            projectId),
                        ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_PROJECT_DIFF);
        }
    }

    /**
     * FileStoreシステムと連携するためのオブジェクトを返す.
     * @return FileStore連携オブジェクト
     */
    public FileStoreClient getFileStoreClient() {
        return (FileStoreClient) applicationContext.getBean("fileStoreClient");
    }

    /**
     * コレポン文書の送信元/宛先に、ログインユーザーがDisicpline Adminの権限を持つ 活動単位が含まれる場合はtrueを返す.
     * @param c
     *            コレポン文書
     * @return ログインユーザーがGroup Adminの権限を持つ活動単位が含まれる場合はtrue
     */
    public boolean isAnyGroupAdmin(Correspon c) {
        ArgumentValidator.validateNotNull(c);

        return userPermissionHelper.isAnyGroupAdmin(c);
    }

    /**
     * ログインユーザーがPreparerか判定する.
     * @param corresponEmpNo
     *            コレポン文書作成者従業員番号
     * @return Preparerならtrue/Preparerではなかったらfalse
     */
    protected boolean isPreparer(String corresponEmpNo) {
        return getCurrentUser().getEmpNo().equals(corresponEmpNo);
    }

    /**
     * ログインユーザーがCheckerか判定する.
     * @param correspon
     *            コレポン文書
     * @return Checkerならtrue/Checkerではなかったらfalse
     */
    protected boolean isChecker(Correspon correspon) {
        return userRoleHelper.isWorkflowChecker(correspon, getCurrentUser());
    }

    /**
     * ログインユーザーがApproverか判定する.
     * @param correspon
     *            コレポン文書
     * @return Approverならtrue/Approverではなかったらfalse
     */
    protected boolean isApprover(Correspon correspon) {
        return userRoleHelper.isWorkflowApprover(correspon, getCurrentUser());
    }

    /**
     * プロジェクトの中にログインユーザーがGroup Adminの権限を持つ活動単位が含まれるか判定する.
     *
     * @param projectId プロジェクトID
     * @return ログインユーザーがGroup Adminの権限を持つ活動単位が含まれる場合はtrue
     */
    public boolean isAnyGroupAdmin(String projectId) {
        ArgumentValidator.validateNotNull(projectId);
        return userPermissionHelper.isAnyGroupAdmin(projectId);
    }

    /**
     * プロジェクトの中に指定ユーザーがGroup Adminの権限を持つ活動単位が含まれるか判定する.
     *
     * @param user ユーザー
     * @param projectId プロジェクトID
     * @return ログインユーザーがGroup Adminの権限を持つ活動単位が含まれる場合はtrue
     */
    public boolean isAnyGroupAdmin(User user, String projectId) {
        ArgumentValidator.validateNotNull(user);
        ArgumentValidator.validateNotNull(projectId);
        return userPermissionHelper.isAnyGroupAdmin(user, projectId);
    }

    /**
     * サーバーのURLを取得する.
     * @return サーバーのURL
     */
    public String getBaseURL() {
        ProcessContext container = ProcessContext.getCurrentContext();

        Map<String, Object> values = container.getValue(SystemConfig.KEY_ACTION_VALUES);
        return (String) values.get(Constants.KEY_BASE_URL);
    }

    /**
     * アプリケーションのコンテキストURLを取得する.
     * @return アプリケーションコンテキストURL
     */
    public String getContextURL() {
        ProcessContext container = ProcessContext.getCurrentContext();

        Map<String, Object> values = container.getValue(SystemConfig.KEY_ACTION_VALUES);
        return (String) values.get(Constants.KEY_CONTEXT_URL);
    }
//    /**
//     * サーバーのベースパスを取得する.
//     * @return サーバーのベースパス
//     */
//    public String getBasePath() {
//        ProcessContext container = ProcessContext.getCurrentContext();
//
//        Map<String, Object> values = container.getValue(SystemConfig.KEY_ACTION_VALUES);
//        return (String) values.get(Constants.KEY_BASE_PATH);
//    }

    private List<ProjectUser> findProjectUsers() {
        SearchUserCondition condition = new SearchUserCondition();
        condition.setProjectId(getCurrentProjectId());

        UserDao dao = getDao(UserDao.class);
        return dao.findProjectUser(condition);
    }

    private List<CorresponGroupUser> findCorresponGroupUser(User u) {
        CorresponGroupDao gDao = getDao(CorresponGroupDao.class);
        return gDao.findByEmpNo(getCurrentProjectId(), u.getEmpNo());
    }

    private CorresponGroup detectDefaultCorresponGroup(List<ProjectUser> users, User u) {
        if (users == null) {
            return null;
        }

        for (ProjectUser pu : users) {
            if (u.getEmpNo().equals(pu.getUser().getEmpNo())) {
                if (pu.getDefaultCorresponGroup() != null) {
                    return pu.getDefaultCorresponGroup();
                }
            }
        }
        return null;
    }
}
