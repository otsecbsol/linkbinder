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

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.co.opentone.bsol.framework.core.ProcessContext;
import jp.co.opentone.bsol.framework.core.config.SystemConfig;
import jp.co.opentone.bsol.framework.core.util.ArgumentValidator;
import jp.co.opentone.bsol.linkbinder.Constants;
import jp.co.opentone.bsol.linkbinder.dao.CorresponGroupDao;
import jp.co.opentone.bsol.linkbinder.dao.CorresponGroupUserDao;
import jp.co.opentone.bsol.linkbinder.dao.DaoFinder;
import jp.co.opentone.bsol.linkbinder.dao.UserDao;
import jp.co.opentone.bsol.linkbinder.dto.AddressCorresponGroup;
import jp.co.opentone.bsol.linkbinder.dto.Correspon;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroup;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroupUser;
import jp.co.opentone.bsol.linkbinder.dto.Project;
import jp.co.opentone.bsol.linkbinder.dto.ProjectUser;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponGroupCondition;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchUserCondition;

/**
 * このクラスではユーザの権限チェック処理に関する処理を提供する.
 * @author opentone
 */
@Service
@Transactional(readOnly = true)
public class UserPermissionHelper implements Serializable {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 6024517134304775837L;

    /**
     * ログインユーザー.
     */
    @Resource
    private User currentUser;

    /**
     * Dao取得クラス.
     */
    @Resource
    private DaoFinder daoFinder;

    /**
     * プロジェクト毎のユーザー情報.
     * 同一インスタンスに対する複数回の判定処理呼び出し時のパフォーマンス改善のため
     * 一度取得した情報をキャッシュする.
     */
    private Map<String, List<ProjectUser>> usersPerProject;
    /**
     * プロジェクト毎の活動単位.
     * 同一インスタンスに対する複数回の判定処理呼び出し時のパフォーマンス改善のため
     * 一度取得した情報をキャッシュする.
     */
    private Map<String, List<CorresponGroup>> groupsPerProject;
    /**
     * プロジェクト毎の活動単位ユーザー情報.
     * 同一インスタンスに対する複数回の判定処理呼び出し時のパフォーマンス改善のため
     * 一度取得した情報をキャッシュする.
     */
    private Map<String, List<CorresponGroupUser>> groupUsersPerProject;

    /**
     * 空のインスタンスを生成する.
     */
    public UserPermissionHelper() {
        usersPerProject = new HashMap<String, List<ProjectUser>>();
        groupsPerProject = new HashMap<String, List<CorresponGroup>>();
        groupUsersPerProject = new HashMap<String, List<CorresponGroupUser>>();
    }

    /**
     * 指定されたユーザーがSystem Adminの権限を持つ場合はtrue.
     * @param user
     *            対象ユーザー
     * @return System Adminの権限を持つ場合はtrue、それ以外はfalse
     */
    public boolean isSystemAdmin(User user) {
        ArgumentValidator.validateNotNull(user);

        return user.isSystemAdmin();
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

        // 現在ログイン中ユーザーで比較する場合は
        // 実行コンテキストから情報を取得
        if (user.getEmpNo().equals(currentUser.getEmpNo())) {
            ProjectUser pu = getCurrentProjectUser();
            if (pu != null && projectId.equals(pu.getProjectId())) {
                return pu.isProjectAdmin();
            }
        }

        ProjectUser pu = findProjectUser(projectId, user.getEmpNo());
        if (pu == null) {
            return false;
        }
        return pu.isProjectAdmin();
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
        return isGroupAdmin(user, getCurrentProjectId(), corresponGroupId);
    }

    /**
     * 指定されたユーザーが、指定された活動単位に対するGroup Adminの権限を持つ場合はtrue.
     * @param user
     *            対象ユーザー
     * @param projectId プロジェクトID
     * @param corresponGroupId
     *            活動単位を識別するID
     * @return Group Adminの権限を持つ場合はtrue、それ以外はfalse
     */
    public boolean isGroupAdmin(User user, String projectId, Long corresponGroupId) {
        ArgumentValidator.validateNotNull(user);
        ArgumentValidator.validateNotNull(corresponGroupId);

        CorresponGroupUser gu =
                findCorresponGroupUser(projectId, corresponGroupId, user.getEmpNo());
        if (gu == null) {
            return false;
        }
        return gu.isGroupAdmin();
    }

    /**
     * コレポン文書の送信元/宛先に、ログインユーザーがDisicpline Adminの権限を持つ 活動単位が含まれる場合はtrueを返す.
     * @param c
     *            コレポン文書
     * @return ログインユーザーがGroup Adminの権限を持つ活動単位が含まれる場合はtrue
     */
    public boolean isAnyGroupAdmin(Correspon c) {
        ArgumentValidator.validateNotNull(c);

        Long from = c.getFromCorresponGroup().getId();
        if (isGroupAdmin(currentUser, from)) {
            return true;
        }
        if (c.getAddressCorresponGroups() != null) {
            for (AddressCorresponGroup group : c.getAddressCorresponGroups()) {
                if (isGroupAdmin(currentUser, c.getProjectId(),
                                 group.getCorresponGroup().getId())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * プロジェクトの活動単位の中に、ログインユーザーがDisicpline Adminの権限を持つものが含まれる場合はtrueを返す.
     * @param projectId
     *            プロジェクトID
     * @return ログインユーザーがGroup Adminの権限を持つ活動単位が含まれる場合はtrue
     */
    public boolean isAnyGroupAdmin(String projectId) {
        ArgumentValidator.validateNotNull(projectId);

        return isAnyGroupAdmin(currentUser, projectId);
    }

    /**
     * プロジェクトの活動単位の中に、指定ユーザーがGroup Adminの権限を持つものが含まれる場合はtrueを返す.
     * @param user ユーザー
     * @param projectId
     *            プロジェクトID
     * @return ログインユーザーがGroup Adminの権限を持つ活動単位が含まれる場合はtrue
     */
    public boolean isAnyGroupAdmin(User user, String projectId) {
        ArgumentValidator.validateNotNull(user);
        ArgumentValidator.validateNotNull(projectId);

        if (!groupsPerProject.containsKey(projectId)) {
            loadCorresponGroups(projectId);
        }
        List<CorresponGroup> groups = groupsPerProject.get(projectId);
        boolean result = false;
        if (groups != null) {
            for (CorresponGroup group : groups) {
                if (isGroupAdmin(user, projectId, group.getId())) {
                    result = true;
                    break;
                }
            }
        }
        return result;
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
     * プロジェクトユーザーを検索する.
     * @param projectId プロジェクトID
     * @param empNo 従業員No
     * @return プロジェクトユーザー
     */
    public ProjectUser findProjectUser(String projectId, String empNo) {
        ArgumentValidator.validateNotEmpty(projectId);
        ArgumentValidator.validateNotEmpty(empNo);

        if (!usersPerProject.containsKey(projectId)) {
            loadProjectUsers(projectId);
        }
        List<ProjectUser> users = usersPerProject.get(projectId);
        ProjectUser result = null;
        if (users != null) {
            for (ProjectUser u : users) {
                if (u.getUser().getEmpNo().equals(empNo)) {
                    result = u;
                    break;
                }
            }
        }
        return result;
    }

    private CorresponGroupUser findCorresponGroupUser(
                String projectId, Long corresponGroupId, String empNo) {
        if (!groupUsersPerProject.containsKey(projectId)) {
            loadCorresponGroupUser(projectId);
        }
        List<CorresponGroupUser> users = groupUsersPerProject.get(projectId);
        CorresponGroupUser result = null;
        if (users != null) {
            for (CorresponGroupUser u : users) {
                if (u.getCorresponGroup().getId().equals(corresponGroupId)
                    && u.getUser().getEmpNo().equals(empNo)) {
                    result = u;
                    break;
                }
            }
        }
        return result;
    }

    private void loadProjectUsers(String projectId) {
        SearchUserCondition condition = new SearchUserCondition();
        condition.setProjectId(projectId);

        UserDao dao = daoFinder.getDao(UserDao.class);
        List<ProjectUser> users = dao.findProjectUser(condition);

        usersPerProject.put(projectId, users);
    }

    private void loadCorresponGroups(String projectId) {
        SearchCorresponGroupCondition condition = new SearchCorresponGroupCondition();
        condition.setProjectId(projectId);

        CorresponGroupDao dao = daoFinder.getDao(CorresponGroupDao.class);
        List<CorresponGroup> groups = dao.find(condition);

        groupsPerProject.put(projectId, groups);
    }

    private void loadCorresponGroupUser(String projectId) {
        CorresponGroupUserDao dao = daoFinder.getDao(CorresponGroupUserDao.class);
        List<CorresponGroupUser> users = dao.findByProjectId(projectId);

        groupUsersPerProject.put(projectId, users);
    }
}
