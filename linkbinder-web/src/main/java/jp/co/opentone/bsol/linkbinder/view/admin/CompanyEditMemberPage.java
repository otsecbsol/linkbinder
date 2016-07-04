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
package jp.co.opentone.bsol.linkbinder.view.admin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.ManagedBean;
import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;

import jp.co.opentone.bsol.framework.core.message.MessageCode;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.core.util.JSONUtil;
import jp.co.opentone.bsol.framework.web.extension.jsf.annotation.Initialize;
import jp.co.opentone.bsol.framework.web.extension.jsf.annotation.Transfer;
import jp.co.opentone.bsol.linkbinder.action.AbstractAction;
import jp.co.opentone.bsol.linkbinder.dto.Company;
import jp.co.opentone.bsol.linkbinder.dto.CompanyUser;
import jp.co.opentone.bsol.linkbinder.dto.ProjectUser;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.condition.AbstractCondition;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCompanyCondition;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchUserCondition;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.service.admin.CompanyService;
import jp.co.opentone.bsol.linkbinder.service.admin.UserService;
import jp.co.opentone.bsol.linkbinder.validation.groups.ValidationGroupBuilder;
import jp.co.opentone.bsol.linkbinder.view.AbstractPage;

/**
 * 会社 - メンバー設定画面.
 * @author opentone
 */
@ManagedBean
@Scope("view")
public class CompanyEditMemberPage extends AbstractPage {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -1445139520488359001L;

    /**
     * 会社表示画面名.
     */
    private static final String PAGE_COMPANY = "company";

    /**
     * ユーザーデータの区切り文字.
     */
    public static final String DELIM_MEMBER = ",";

    /**
     * 会社情報サービス.
     */
    @Resource
    private CompanyService companyService;

    /**
     * ユーザーサービス.
     */
    @Resource
    private UserService userService;

    /**
     * backで遷移するページ名.
     * <p>
     * 起動元画面からのクエリパラメータが自動設定される.
     * </p>
     */
    @Transfer
    private String backPage;

    /**
     * 会社ID.
     * <p>
     * 起動元画面からのクエリパラメータが自動設定される.
     * </p>
     */
    @Transfer
    private Long id;

    /**
     * 会社情報.
     */
    @Transfer
    private Company company;

    /**
     * ユーザーリスト.
     */
    @Transfer
    private List<ProjectUser> userList = null;

    /**
     * メンバーリスト.
     */
    @Transfer
    private List<CompanyUser> memberList = null;

    /**
     * すべてのユーザーリスト.
     */
    @Transfer
    private List<User> allUserList = null;

    /**
     * 選択可能なユーザーのValue.
     */
    @Transfer
    private String candidateUserValue;

    /**
     * 選択されたメンバーのValue.
     */
    @Transfer
    private String selectedMemberValue;

    /**
     * 初期画面表示判定値.
     */
    @Transfer
    private boolean initialDisplaySuccess = false;

    /**
     * 検索条件.
     * 起動元画面からのFlashパラメータを設定する.
     */
    @Transfer
    private AbstractCondition previousCondition;

    /**
     * 空のインスタンスを生成する.
     */
    public CompanyEditMemberPage() {
    }

    /**
     * 入力検証グループ名を返す.
     * @return 入力検証グループ名
     */
    public String getValidationGroups() {
        if (isActionInvoked("form:save")) {
            return new ValidationGroupBuilder().defaultGroup().toString();
        } else {
            return new ValidationGroupBuilder().skipValidationGroup().toString();
        }
    }

    /**
     * 画面を初期化する.
     * <p>
     * 他画面から遷移した時に起動される. クエリパラメータで指定されたIDに対応する、会社情報を表示する.
     * </p>
     */
    @Initialize
    public void initialize() {
        handler.handleAction(new InitializeAction(this));
    }

    /**
     * メンバー設定を登録する.
     * @return null
     */
    public String save() {
        handler.handleAction(new SaveAction(this));
        return null;
    }

    /**
     * 起動元画面へ遷移.
     * @return 遷移先画面ID
     */
    public String back() {
        setNextSearchCondition(previousCondition);
        // 会社表示画面の場合はIDが必要
        if (PAGE_COMPANY.equals(backPage)) {
            return toUrl(String.format("company?id=%s", id), isLoginProject());
        }
        return toUrl(backPage, isLoginProject());
    }

    /**
     * backPageを取得します.
     * @return the backPage
     */
    public String getBackPage() {
        return backPage;
    }

    /**
     * backPageを設定します.
     * @param backPage the backPage to set
     */
    public void setBackPage(String backPage) {
        this.backPage = backPage;
    }

    /**
     * idを取得します.
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * idを設定します.
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * companyを取得します.
     * @return the company
     */
    public Company getCompany() {
        return company;
    }

    /**
     * companyを設定します.
     * @param company the company to set
     */
    public void setCompany(Company company) {
        this.company = company;
    }

    /**
     * userListを取得します.
     * @return the userList
     */
    public List<ProjectUser> getUserList() {
        return userList;
    }

    /**
     * userListを設定します.
     * @param userList the userList to set
     */
    public void setUserList(List<ProjectUser> userList) {
        this.userList = userList;
    }

    /**
     * allUserListを設定します.
     * @param allUserList the allUserList to set
     */
    public void setAllUserList(List<User> allUserList) {
        this.allUserList = allUserList;
    }

    /**
     * allUserListを取得します.
     * @return the allUserList
     */
    public List<User> getAllUserList() {
        return allUserList;
    }

    /**
     * memberListを取得します.
     * @return the memberList
     */
    public List<CompanyUser> getMemberList() {
        return memberList;
    }

    /**
     * memberListを設定します.
     * @param memberList the memberList to set
     */
    public void setMemberList(List<CompanyUser> memberList) {
        this.memberList = memberList;
    }

    /**
     * candidateUserValueを取得します.
     * @return the candidateUserValue
     */
    public String getCandidateUserValue() {
        return candidateUserValue;
    }

    /**
     * candidateUserValueを設定します.
     * @param candidateUserValue the candidateUserValue to set
     */
    public void setCandidateUserValue(String candidateUserValue) {
        this.candidateUserValue = candidateUserValue;
    }

    /**
     * selectedMemberValueを取得します.
     * @return the selectedMemberValue
     */
    public String getSelectedMemberValue() {
        return selectedMemberValue;
    }

    /**
     * selectedMemberValueを設定します.
     * @param selectedMemberValue the selectedMemberValue to set
     */
    public void setSelectedMemberValue(String selectedMemberValue) {
        this.selectedMemberValue = selectedMemberValue;
    }

    /**
     * initialDisplaySuccessを取得します.
     * @return the initialDisplaySuccess
     */
    public boolean isInitialDisplaySuccess() {
        return initialDisplaySuccess;
    }

    /**
     * initialDisplaySuccessを設定します.
     * @param initialDisplaySuccess the initialDisplaySuccess to set
     */
    public void setInitialDisplaySuccess(boolean initialDisplaySuccess) {
        this.initialDisplaySuccess = initialDisplaySuccess;
    }

    /**
     * previousConditionを設定します.
     * @param previousCondition the previousCondition to set
     */
    public void setPreviousCondition(AbstractCondition previousCondition) {
        this.previousCondition = previousCondition;
    }

    /**
     * previousConditionを取得します.
     * @return the previousCondition
     */
    public AbstractCondition getPreviousCondition() {
        return previousCondition;
    }

    /**
     * ユーザーのリストをJSON形式に変換して返す.
     * @return ユーザーのリストのJSON形式
     */
    public String getUserJSONString() {
        Map<String, User> users = new HashMap<String, User>();
        if (getAllUserList() != null) {
            for (User u : getAllUserList()) {
                users.put(u.getEmpNo(), u);
            }
        }
        return JSONUtil.encode(users);
    }

    /**
     * JSFの実装上必要なSetter.処理は何も行わない.
     * @param value 値
     */
    public void setUserJSONString(String value) {
        //  何もしない
    }

    /**
     * 追加可能なユーザの検索条件を作成する.
     * @return 検索条件
     */
    private SearchUserCondition createSearchUserCondition() {
        SearchUserCondition condition = new SearchUserCondition();
        condition.setProjectId(getCurrentProjectId());
        condition.setNotRelatedCompanyCondition();

        return condition;
    }

    /**
     * 画面初期化アクション.
     * @author opentone
     */
    static class InitializeAction extends AbstractAction {
        /**
         * serialVersionUID.
         */
        private static final long serialVersionUID = -32885029718268064L;
        /** アクション発生元ページ. */
        private CompanyEditMemberPage page;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page
         *            ページ
         */
        public InitializeAction(CompanyEditMemberPage page) {
            super(page);
            this.page = page;
        }

        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            page.initialDisplaySuccess = false;

            page.previousCondition
                = page.getPreviousSearchCondition(SearchCompanyCondition.class);
            page.checkProjectSelected();
            // 権限チェック
            if (!page.isSystemAdmin()
                    && !page.isProjectAdmin(page.getCurrentProjectId())) {
                throw new ServiceAbortException(
                    ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT);
            }

            // 必須パラメータチェック
            if (page.id == null) {
                throw new ServiceAbortException("ID is not specified.",
                                                MessageCode.E_INVALID_PARAMETER);
            } else if (page.backPage == null) {
                throw new ServiceAbortException("Back Page is not specified.",
                                                MessageCode.E_INVALID_PARAMETER);
            }

            page.company = page.companyService.find(page.id);
            // 追加可能なユーザを検索する
            page.userList = page.userService.search(page.createSearchUserCondition());
            page.candidateUserValue = createUserValue();
            // すでに設定されているユーザを検索する
            page.memberList = page.companyService.findMembers(page.company.getProjectCompanyId());
            page.selectedMemberValue = createMemberValue();

            page.allUserList = createAllUser();

            // 初期画面表示成功
            page.initialDisplaySuccess = true;
        }

        private List<User> createAllUser() {
            List<User> list = new ArrayList<User>();
            for (ProjectUser pUser : page.userList) {
                list.add(pUser.getUser());
            }

            for (CompanyUser cUser : page.memberList) {
                list.add(cUser.getUser());
            }

            return list;
        }

        private String createUserValue() {
            List<String> ids = new ArrayList<String>();
            for (ProjectUser pUser : page.userList) {
                ids.add(pUser.getUser().getEmpNo());
            }
            return StringUtils.join(ids.iterator(), DELIM_MEMBER);
        }

        private String createMemberValue() {
            List<String> ids = new ArrayList<String>();
            for (CompanyUser cUser : page.memberList) {
                ids.add(cUser.getUser().getEmpNo());
            }
            return StringUtils.join(ids.iterator(), DELIM_MEMBER);
        }
    }

    /**
     * メンバー設定保存アクション.
     * @author opentone
     */
    static class SaveAction extends AbstractAction {
        /**
         * serialVersionUID.
         */
        private static final long serialVersionUID = -7482583317283693041L;
        /** アクション発生元ページ. */
        private CompanyEditMemberPage page;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page
         *            ページ
         */
        public SaveAction(CompanyEditMemberPage page) {
            super(page);
            this.page = page;
        }

        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            page.companyService.saveMembers(page.company, getUserList());

            // 再取得
            page.setNextSearchCondition(page.previousCondition);
            page.initialize();

            page.setPageMessage(ApplicationMessageCode.SAVE_SUCCESSFUL);
        }

        /**
         * 登録するユーザー情報を取得する.
         */
        private List<User> getUserList() {
            List<User> newList = new ArrayList<User>();

            if (page.selectedMemberValue == null) {
                return newList;
            }

            String[] userIdArray = page.selectedMemberValue.split(DELIM_MEMBER);
            for (String userId : userIdArray) {
                User addUser = null;
                for (User user : page.allUserList) {
                    if (userId.equals(user.getUserId())) {
                        addUser = user;
                        break;
                    }
                }
                newList.add(addUser);
            }
            return newList;
        }
    }
}
