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
package jp.co.opentone.bsol.linkbinder.view.common;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.ManagedBean;
import javax.annotation.Resource;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import jp.co.opentone.bsol.framework.core.config.SystemConfig;
import jp.co.opentone.bsol.linkbinder.Constants;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;

import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.web.extension.jsf.annotation.Initialize;
import jp.co.opentone.bsol.framework.web.extension.jsf.annotation.Transfer;
import jp.co.opentone.bsol.linkbinder.action.AbstractAction;
import jp.co.opentone.bsol.linkbinder.dto.ProjectSummary;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.code.ReadStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowStatus;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponCondition;
import jp.co.opentone.bsol.linkbinder.service.common.HomeService;
import jp.co.opentone.bsol.linkbinder.view.AbstractPage;

/**
 * 当システムにログイン後、最初に表示されるホーム画面.
 * @author opentone
 */
@ManagedBean
@Scope("view")
public class HomePage extends AbstractPage {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -1911947820084637576L;

    /**
     * ユーザーサービス.
     */
    @Resource
    private HomeService homeService;

    /**
     * プロジェクトサマリリスト.
     */
    @Transfer
    private List<ProjectSummary> projectSummaryList = null;

    /**
     * 学習用プロジェクトサマリリスト.
     */
    @Transfer
    private List<ProjectSummary> learningProjectSummaryList = null;

    /**
     * データのDataModel.
     */
    private DataModel<?> dataModel = null;

    /**
     * 学習用文書のDataModel.
     */
    private DataModel<?> learningDataModel = null;

    /**
     * コレポン文書一覧検索条件：Attention.
     */
    @Transfer
    private boolean attentionSearch;

    /**
     * コレポン文書一覧検索条件：Person in Charge.
     */
    @Transfer
    private boolean personInChargeSearch;

    /**
     * コレポン文書一覧検索条件：Cc.
     */
    @Transfer
    private boolean ccSearch;

    /**
     * 空のインスタンスを生成する.
     */
    public HomePage() {
    }


    /**
     * projectSummaryListを取得します.
     * @return the projectSummaryList
     */
    public List<ProjectSummary> getProjectSummaryList() {
        return projectSummaryList;
    }


    /**
     * projectSummaryListを設定します.
     * @param projectSummaryList the projectSummaryList to set
     */
    public void setProjectSummaryList(List<ProjectSummary> projectSummaryList) {
        this.projectSummaryList = projectSummaryList;
    }


    /**
     * dataModelを取得します.
     * @return the dataModel
     */
    public DataModel<?> getDataModel() {
        if (projectSummaryList != null) {
            dataModel = new ListDataModel<ProjectSummary>(projectSummaryList);
        }
        return dataModel;
    }


    /**
     * dataModelを設定します.
     * @param dataModel the dataModel to set
     */
    public void setDataModel(DataModel<?> dataModel) {
        this.dataModel = dataModel;
    }


    /**
     * attentionSearchを取得します.
     * @return the attentionSearch
     */
    public boolean isAttentionSearch() {
        return attentionSearch;
    }


    /**
     * attentionSearchを設定します.
     * @param attentionSearch the attentionSearch to set
     */
    public void setAttentionSearch(boolean attentionSearch) {
        this.attentionSearch = attentionSearch;
    }


    /**
     * personInChargeSearchを取得します.
     * @return the personInChargeSearch
     */
    public boolean isPersonInChargeSearch() {
        return personInChargeSearch;
    }


    /**
     * personInChargeSearchを設定します.
     * @param personInChargeSearch the personInChargeSearch to set
     */
    public void setPersonInChargeSearch(boolean personInChargeSearch) {
        this.personInChargeSearch = personInChargeSearch;
    }


    /**
     * ccSearchを取得します.
     * @return the ccSearch
     */
    public boolean isCcSearch() {
        return ccSearch;
    }


    /**
     * ccSearchを設定します.
     * @param ccSearch the ccSearch to set
     */
    public void setCcSearch(boolean ccSearch) {
        this.ccSearch = ccSearch;
    }

    /**
     * 学習用プロジェクトサマリリストを取得します.
     */
    public List<ProjectSummary> getLearningProjectSummaryList() {
        return this.learningProjectSummaryList;
    }

    /**
     * 学習用プロジェクトサマリを設定します.
     */
    public void setLearningProjectSummaryList(List projectList) {
        this.learningProjectSummaryList = projectList;
    }

    /**
     * 画面を初期化する.
     * <p>
     * 他画面から遷移した時に起動される. ページの初期化に必要な情報を準備する.
     * </p>
     */
    @Initialize
    public void initialize() {
        handler.handleAction(new InitializeAction(this));
    }

    /**
     * プロジェクトホーム画面に遷移する.
     * @return null
     */
    public String goProjectHome() {
        setProjectInfo();
        return toUrl(String.format("projectHome?projectId=%s", getSeletedProjectId()));
    }

    /**
     * コレポン文書一覧画面に遷移する.
     * @return null
     */
    public String goCorresponIndex() {
        setProjectInfo();
        setCorresponSearchCondition();
        return toUrl(
                String.format("correspon/corresponIndex?projectId=%s", getSeletedProjectId()));
    }


    /**
     * 選択された行のプロジェクトIDを取得する.
     * @return プロジェクトID
     */
    private String getSeletedProjectId() {
        return ((ProjectSummary) dataModel.getRowData()).getProject().getProjectId();
    }

    /**
     * プロジェクト情報をセッションに設定する.
     */
    private void setProjectInfo() {
        setCurrentProjectInfo(((ProjectSummary) dataModel.getRowData()).getProject());
    }

    /**
     * 選択された行のプロジェクトIDを取得する（学習用コンテンツ用）.
     * @return プロジェクトID
     */
        // TODO:選択された項目に対応するプロジェクトIDを取得する処理
//    private String getSeletedLearningProjectId() {
//        return ((ProjectSummary) learningDataModel.getRowData()).getProject().getProjectId();
//    }


    /**
     * プロジェクト情報をセッションに設定する（学習用コンテンツ用）.
     */
    private void setLearningProjectInfo() {
        //TODO:選択された内容に対応したプロジェクト情報を設定する処理
//        setCurrentProjectInfo(((ProjectSummary) learningDataModel.getRowData()).getProject());
    }


    /**
     * コレポン文書検索条件を設定する.
     */
    private void setCorresponSearchCondition() {
        SearchCorresponCondition condition = new SearchCorresponCondition();
        condition.setProjectId(getSeletedProjectId());
        condition.setUserId(getCurrentUser().getUserId());
        condition.setSystemAdmin(isSystemAdmin());
        condition.setProjectAdmin(isProjectAdmin(getSeletedProjectId()));

        //  発行済
        condition.setWorkflowStatuses(new WorkflowStatus[] {WorkflowStatus.ISSUED});

        User[] users = {getCurrentUser()};
        condition.setToUsers(users);
        ReadStatus[] readStatuses = {ReadStatus.NEW};
        condition.setReadStatuses(readStatuses);
        condition.setUserAttention(attentionSearch);
        condition.setUserPic(personInChargeSearch);
        condition.setUserCc(ccSearch);

        setCurrentSearchCorresponCondition(condition, getSeletedProjectId());
    }

    /**
     * コレポン文書検索条件を設定する(学習用コンテンツ用).
     */
    private void setCorresponSearchConditionForLearning() {
        SearchCorresponCondition condition = new SearchCorresponCondition();
        condition.setProjectId(SystemConfig.getValue(Constants.KEY_LEARNING_PJ));
        condition.setUserId(getCurrentUser().getUserId());
        condition.setSystemAdmin(isSystemAdmin());
        condition.setProjectAdmin(isProjectAdmin(SystemConfig.getValue(Constants.KEY_LEARNING_PJ)));

        //  発行済
        condition.setWorkflowStatuses(new WorkflowStatus[] {WorkflowStatus.ISSUED});

        User[] users = {getCurrentUser()};
        condition.setToUsers(users);
        ReadStatus[] readStatuses = {ReadStatus.NEW};
        condition.setReadStatuses(readStatuses);
        condition.setUserAttention(attentionSearch);
        condition.setUserPic(personInChargeSearch);
        condition.setUserCc(ccSearch);

        setCurrentSearchCorresponCondition(condition, SystemConfig.getValue(Constants.KEY_LEARNING_PJ));
    }

    /**
     * 画面初期化アクション.
     * @author opentone
     */
    static class InitializeAction extends AbstractAction {
        /**
         * serialVersionUID.
         */
        private static final long serialVersionUID = -4973116494274359233L;
        /** アクション発生元ページ. */
        private HomePage page;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page
         *            ページ
         */
        public InitializeAction(HomePage page) {
            super(page);
            this.page = page;
        }

        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            page.projectSummaryList = page.homeService.findProjects();

            // リストから学習用コンテンツプロジェクトを削除し、別途保持する。
            List<ProjectSummary> newList = new ArrayList<ProjectSummary>();
            for(int i = 0;i < page.projectSummaryList.size();i++) {
                String flg = page.projectSummaryList.get(i).getProject().getForLearning();
                if(null != flg && flg.equals("X")) {
                    newList.add(page.projectSummaryList.get(i));
                    page.projectSummaryList.remove(i);
                }
            }
            page.learningProjectSummaryList = newList;

            // デフォルトプロジェクトがある場合は先頭に並び替え
            if (!StringUtils.isEmpty(page.getCurrentUser().getDefaultProjectId())) {
                sortProjectSummary();
            }
        }

        private void sortProjectSummary() {
            List<ProjectSummary> newList = new ArrayList<ProjectSummary>();
            for (ProjectSummary summary : page.projectSummaryList) {
                if (summary.getProject().getProjectId().equals(
                        page.getCurrentUser().getDefaultProjectId())) {
                    newList.add(summary);
                    page.projectSummaryList.remove(summary);
                    break;
                }
            }
            newList.addAll(page.projectSummaryList);
            page.projectSummaryList = newList;
        }
    }
}
