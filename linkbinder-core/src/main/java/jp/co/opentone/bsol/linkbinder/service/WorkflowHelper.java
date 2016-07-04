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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.co.opentone.bsol.framework.core.ProcessContext;
import jp.co.opentone.bsol.framework.core.config.SystemConfig;
import jp.co.opentone.bsol.framework.core.dao.Dao;
import jp.co.opentone.bsol.framework.core.util.ArgumentValidator;
import jp.co.opentone.bsol.linkbinder.Constants;
import jp.co.opentone.bsol.linkbinder.dao.CorresponGroupDao;
import jp.co.opentone.bsol.linkbinder.dao.DaoFinder;
import jp.co.opentone.bsol.linkbinder.dao.UserDao;
import jp.co.opentone.bsol.linkbinder.dto.Correspon;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroup;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroupUser;
import jp.co.opentone.bsol.linkbinder.dto.Project;
import jp.co.opentone.bsol.linkbinder.dto.ProjectUser;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.Workflow;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchUserCondition;

/**
 * ワークフローに関するヘルパークラス.
 * @author opentone
 */
@Service
@Transactional(readOnly = true)
public class WorkflowHelper implements Serializable {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 1745052831493009977L;

    /**
     * Dao取得クラス.
     */
    @Resource
    private DaoFinder daoFinder;

    /**
     * 空のインスタンスを生成する.
     */
    public WorkflowHelper() {
    }

    /**
     * コレポン文書が保持するワークフローにPreparerの情報を付与したリストを返す.
     * @param correspon コレポン文書
     * @return 先頭にPreparerの情報を付与したワークフロー一覧
     */
    public List<Workflow> createWorkflowListWithPreparer(Correspon correspon) {
        return createWorkflowListWithPreparer(correspon.getCreatedBy(), correspon.getWorkflows());
    }

    /**
     * ワークフローにPreparerの情報を付与したリストを返す.
     * @param preparer 追加するPreparer
     * @param workflows 追加対象
     * @return 先頭にPreparerの情報を付与したワークフロー一覧
     */
    public List<Workflow> createWorkflowListWithPreparer(User preparer, List<Workflow> workflows) {
        Workflow workflow = new Workflow();
        workflow.setUser(preparer);
        workflow.setCorresponGroup(findPrimaryCorresponGroup(preparer));

        return createWorkflowListWithPreparer(workflow, workflows);
    }

    /**
     * 表示用の承認フローを作成する（1行目にPrepererを追加する）.
     * @param preparer Preparer
     * @param workflows 承認フロー
     * @return Preparerを追加したリスト
     */
    public static List<Workflow> createWorkflowListWithPreparer(
                Workflow preparer, List<Workflow> workflows) {
        List<Workflow> result = new ArrayList<Workflow>();

        long no = 1;
        preparer.setWorkflowNo(no++);
        result.add(preparer);

        // ワークフロー既存情報を追加
        for (Workflow w : workflows) {
            w.setWorkflowNo(no++);
            result.add(w);
        }
        return result;
    }

    /**
     * 指定されたユーザーのプライマリ活動単位を返す.
     * ここで言う「プライマリ活動単位」は、次の順で決定される.
     * 活動単位に所属していないユーザーの場合はnullを返す.
     * <ol>
     * <li>ユーザーのデフォルト活動単位</li>
     * <li>ユーザーの所属する活動単位のうち、最初に見つかった活動単位</li>
     * </ol>
     * @param user 対象のユーザー
     * @return 活動単位
     */
    public CorresponGroup findPrimaryCorresponGroup(User user) {
        ArgumentValidator.validateNotNull(user);
        List<ProjectUser> users = findProjectUsers();
        //  デフォルト活動単位が設定済であればそれをセット
        CorresponGroup group = detectDefaultCorresponGroup(users, user);
        if (group != null) {
            return group;
        }

        // 1つ以上の活動単位に所属していれば
        // 最初の活動単位
        List<CorresponGroupUser> listCgu = findCorresponGroupUser(user);
        if (listCgu != null && !listCgu.isEmpty()) {
            group = listCgu.get(0).getCorresponGroup();
        }
        return group;
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
}
