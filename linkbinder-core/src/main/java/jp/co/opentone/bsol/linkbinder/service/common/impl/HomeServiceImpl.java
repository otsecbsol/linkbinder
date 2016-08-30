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
package jp.co.opentone.bsol.linkbinder.service.common.impl;

import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.core.util.ArgumentValidator;
import jp.co.opentone.bsol.linkbinder.dao.CorresponDao;
import jp.co.opentone.bsol.linkbinder.dao.CorresponGroupDao;
import jp.co.opentone.bsol.linkbinder.dao.ProjectDao;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroup;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroupSummary;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroupUser;
import jp.co.opentone.bsol.linkbinder.dto.CorresponUserSummary;
import jp.co.opentone.bsol.linkbinder.dto.ProjectDetailsSummary;
import jp.co.opentone.bsol.linkbinder.dto.ProjectSummary;
import jp.co.opentone.bsol.linkbinder.dto.code.ForLearning;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponUserSummaryCondition;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchProjectCondition;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.service.AbstractService;
import jp.co.opentone.bsol.linkbinder.service.common.HomeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 *
 * ホーム画面に関するサービスを提供する.
 * @author opentone
 */
@Service
@Transactional(rollbackFor = { ServiceAbortException.class })
public class HomeServiceImpl extends AbstractService implements HomeService {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -8730574042319563347L;

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.service.common.HomeService#findProjects(ForLearning learn)
     */
    @Transactional(readOnly = true)
    public List<ProjectSummary> findProjects(ForLearning learn) throws ServiceAbortException {
        return findProjectSummarys(createFindProjectsCondition(learn));
    }

    /*
     * (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.service.common.HomeService#findProjectDetails(java.lang.String)
     */
    public ProjectDetailsSummary findProjectDetails(String projectId) throws ServiceAbortException {
        return findProjectDetails(projectId, true);
    }

    /*
     * (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.service.common.HomeService
     * #findProjectDetails(java.lang.String, boolean)
     */
    public ProjectDetailsSummary findProjectDetails(String projectId, boolean usePersonInCharge)
        throws ServiceAbortException {
        ArgumentValidator.validateNotEmpty(projectId);
        // 権限チェック
        checkPermission(projectId);
        return createProjectDetailsSummary(projectId, usePersonInCharge);
    }

    /**
     * プロジェクトサマリのリストを取得する.
     * @param condition 検索条件
     * @return プロジェクトリスト
     */
    private List<ProjectSummary> findProjectSummarys(SearchProjectCondition condition) {
        ProjectDao dao = getDao(ProjectDao.class);
        return dao.findProjectSummary(condition);
    }

    /**
     * Home画面に表示するための検索条件を作成する.
     * @return 検索条件
     */
    private SearchProjectCondition createFindProjectsCondition() {
        SearchProjectCondition condition = new SearchProjectCondition();
        condition.setEmpNo(getCurrentUser().getEmpNo());
        condition.setSystemAdmin(isSystemAdmin(getCurrentUser()));

        return condition;
    }
    /**
     * Home画面に表示するための検索条件を作成する
     * @param learn (0:通常PJを検索,1:学習用PJのみを検索, その他数値:全てのPJを検索).
     * @return 検索条件
     */
    private SearchProjectCondition createFindProjectsCondition(ForLearning learn) {
        SearchProjectCondition condition = new SearchProjectCondition();
        condition.setEmpNo(getCurrentUser().getEmpNo());
        condition.setSystemAdmin(isSystemAdmin(getCurrentUser()));
        // 学習用プロジェクトは権限に関わらず参照可
        if (ForLearning.LEARNING == learn) {
            condition.setSystemAdmin(true);
        }
        condition.setForLearning(learn);

        return condition;
    }

    /**
     * System Admin以外は自身が所属しているプロジェクト以外はエラー.
     * @param projectId プロジェクトID
     * @throws ServiceAbortException 権限エラー
     */
    private void checkPermission(String projectId) throws ServiceAbortException {
        if (!isSystemAdmin(getCurrentUser())
                && findProjectUser(projectId, getCurrentUser().getEmpNo()) == null) {
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_PROJECT_DIFF);
        }
    }

    /**
     * プロジェクト詳細サマリを作成する.
     * @param projectId プロジェクトID
     * @return プロジェクト詳細サマリ
     */
    private ProjectDetailsSummary createProjectDetailsSummary(
            String projectId, boolean usePersonInCharge) {
        ProjectDetailsSummary summary = new ProjectDetailsSummary();
        summary.setUserRelatedCorresponGroups(findUserRelatedCorresponGroups(projectId));
        summary.setCorresponGroupSummary(
            findCorresponGroupSummary(projectId, summary.getUserRelatedCorresponGroups()));
        summary.setCorresponUserSummary(findCorresponUserSummary(projectId, usePersonInCharge));

        return summary;
    }

    /**
     * ユーザーが所属する活動単位を検索する.
     * @param projectId プロジェクトID
     * @return ユーザーが所属する活動単位
     */
    private CorresponGroup[] findUserRelatedCorresponGroups(String projectId) {
        CorresponGroupDao dao = getDao(CorresponGroupDao.class);
        List<CorresponGroupUser> userGroups
            = dao.findByEmpNo(projectId, getCurrentUser().getEmpNo());
        CorresponGroup[] groups = new CorresponGroup[userGroups.size()];
        for (int i = 0; i < userGroups.size(); i++) {
            CorresponGroupUser gUser = userGroups.get(i);
            groups[i] = gUser.getCorresponGroup();
        }
        return groups;
    }

    /**
     * コレポン文書種別と活動単位でのサマリ情報を検索する.
     * @param projectId プロジェクトID
     * @param userGroups 活動単位
     * @return コレポン文書種別と活動単位でのサマリ情報
     */
    private List<CorresponGroupSummary> findCorresponGroupSummary(
            String projectId, CorresponGroup[] userGroups) {
        CorresponDao dao = getDao(CorresponDao.class);
        return dao.findCorresponGroupSummary(projectId, userGroups);
    }

    /**
     * ユーザー情報でのサマリ情報を検索する.
     * @param projectId プロジェクトID
     * @return ユーザー情報でのサマリ情報
     */
    private CorresponUserSummary findCorresponUserSummary(
            String projectId, boolean usePersonInCharge) {
        SearchCorresponUserSummaryCondition condition = new SearchCorresponUserSummaryCondition();
        condition.setProjectId(projectId);
        condition.setUserId(getCurrentUser().getEmpNo());
        condition.setSystemAdmin(isSystemAdmin(getCurrentUser()));
        condition.setProjectAdmin(isProjectAdmin(getCurrentUser(), projectId));
        condition.setUsePersonInCharge(usePersonInCharge);

        CorresponDao dao = getDao(CorresponDao.class);
        return dao.findCorresponUserSummary(condition);
    }
}
