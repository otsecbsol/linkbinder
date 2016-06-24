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
import org.hibernate.validator.constraints.Length;
import org.springframework.context.annotation.Scope;

import jp.co.opentone.bsol.framework.core.config.SystemConfig;
import jp.co.opentone.bsol.framework.core.message.MessageCode;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.core.util.JSONUtil;
import jp.co.opentone.bsol.framework.core.validation.constraints.Required;
import jp.co.opentone.bsol.framework.web.extension.jsf.annotation.Initialize;
import jp.co.opentone.bsol.framework.web.extension.jsf.annotation.Transfer;
import jp.co.opentone.bsol.linkbinder.action.AbstractAction;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroup;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroupUser;
import jp.co.opentone.bsol.linkbinder.dto.ProjectUser;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.condition.AbstractCondition;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponGroupCondition;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchUserCondition;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.service.admin.CorresponGroupService;
import jp.co.opentone.bsol.linkbinder.service.admin.UserService;
import jp.co.opentone.bsol.linkbinder.validation.groups.ValidationGroupBuilder;
import jp.co.opentone.bsol.linkbinder.view.AbstractPage;

/**
 * 活動単位入力画面.
 * @author opentone
 */
@ManagedBean
@Scope("view")
public class GroupEditPage extends AbstractPage {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -1445139520488359001L;

    /**
     * セキュリティレベル：GroupAdminを取得するためのKEY.
     */
    private static final String KEY_GROUP_ADMIN = "securityLevel.groupAdmin";

    /**
     * セキュリティレベル：NormalUserを取得するためのKEY.
     */
    private static final String KEY_NORMAL_USER = "securityLevel.normalUser";

    /**
     * ユーザーデータの区切り文字.
     */
    public static final String DELIM_MEMBER = ",";

    /**
     * 活動単位サービス.
     */
    @Resource
    private CorresponGroupService corresponGroupService;

    /**
     * ユーザーサービス.
     */
    @Resource
    private UserService userService;

    /**
     * 活動単位ID.
     * <p>
     * 起動元画面からのクエリパラメータが自動設定される.
     * </p>
     */
    @Transfer
    private Long id;

    /**
     * 起動元画面の起動元画面名.
     * <p>
     * 起動元画面からのクエリパラメータが自動設定される.
     * </p>
     */
    @Transfer
    private String backPage;

    /**
     * 拠点ID.
     * <p>
     * 起動元画面からのクエリパラメータが自動設定される.
     * </p>
     */
    @Transfer
    private Long siteId;

    /**
     * 検索条件.
     * 起動元画面からのFlashパラメータを設定する.
     */
    @Transfer
    private AbstractCondition previousCondition;

    /**
     * 活動単位情報.
     */
    @Transfer
    private CorresponGroup corresponGroup;

    /**
     * 入力項目：活動単位名.
     */
//    @SkipValidation("#{!groupEditPage.saveAction}")
    @Transfer
    @Required
    //CHECKSTYLE:OFF
    @Length(max = 100)
    //CHECKSTYLE:ON
    private String name;

    /**
     * ユーザーリスト.
     */
    @Transfer
    private List<User> userList = null;

    /**
     * 部門管理者リスト.
     */
    @Transfer
    private List<CorresponGroupUser> adminList = null;

    /**
     * 通常ユーザーリスト.
     */
    @Transfer
    private List<CorresponGroupUser> memberList = null;

    /**
     * すべてのユーザーリスト.
     */
    @Transfer
    private List<User> allUserList = null;

    /**
     * ユーザーのValue.
     */
    @Transfer
    private String candidateUserValue;

    /**
     * 選択された部門管理者のValue.
     */
    @Transfer
    private String selectedAdminValue;

    /**
     * 選択された通常ユーザーのValue.
     */
    @Transfer
    private String selectedMemberValue;

    /**
     * 登録済管理者から削除できないユーザーの従業員番号.
     * <p>
     * ログインユーザーが編集対象の活動単位のGroup Adminである場合、
     * このフィールドにログインユーザーの従業員番号が設定される.
     *
     */
    @Transfer
    private String unremovableAdmin;

    /**
     * 初期画面表示判定値.
     */
    @Transfer
    private boolean initialDisplaySuccess = false;

    /**
     * 空のインスタンスを生成する.
     */
    public GroupEditPage() {
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
     * 活動単位の名称、メンバーを登録する.
     * @return null
     */
    public String save() {
        handler.handleAction(new SaveAction(this));
        return null;
    }

    /**
     * 活動単位一覧に遷移する.
     * @return 活動単位一覧
     */
    public String back() {
        setNextSearchCondition(previousCondition);
        return toUrl(String.format("groupIndex?id=%s&backPage=%s",
                             siteId,
                             backPage));
    }

    /**
     * 活動単位IDを取得する.
     * @return 活動単位ID
     */
    public Long getId() {
        return id;
    }

    /**
     * 活動単位IDをセットする.
     * @param id 活動単位ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 起動元画面をセットする.
     * @param backPage 起動元画面
     */
    public void setBackPage(String backPage) {
        this.backPage = backPage;
    }

    /**
     * 起動元画面を取得する.
     * @return 起動元画面
     */
    public String getBackPage() {
        return backPage;
    }

    /**
     * siteIdを設定します.
     * @param siteId the siteId to set
     */
    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    /**
     * siteIdを取得します.
     * @return the siteId
     */
    public Long getSiteId() {
        return siteId;
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
     * 活動単位名をセットする.
     * @param name 活動単位名
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 活動単位名を取得する.
     * @return 活動単位名
     */
    public String getName() {
        return name;
    }

    /**
     * 活動単位を取得する.
     * @return 活動単位
     */
    public CorresponGroup getCorresponGroup() {
        return corresponGroup;
    }

    /**
     * 活動単位をセットする.
     * @param corresponGroup 活動単位
     */
    public void setCorresponGroup(CorresponGroup corresponGroup) {
        this.corresponGroup = corresponGroup;
    }

    /**
     * 選択した活動単位に属していないユーザーリストを取得する.
     * @return 選択した活動単位に属していないユーザーリスト
     */
    public List<User> getUserList() {
        return userList;
    }

    /**
     * 選択した活動単位に属していないユーザーリストをセットする.
     * @param userList 選択した活動単位に属していないユーザーリスト
     */
    public void setUserList(List<User> userList) {
        this.userList = userList;
    }

    /**
     * 選択した活動単位のGroupAdminユーザーリストを取得する.
     * @return 選択した活動単位のGroupAdminのユーザーリスト
     */
    public List<CorresponGroupUser> getAdminList() {
        return adminList;
    }

    /**
     * 選択した活動単位のGroupAdminユーザーリストをセットする.
     * @param adminList 選択した活動単位のGroupAdminユーザーリスト
     */
    public void setAdminList(List<CorresponGroupUser> adminList) {
        this.adminList = adminList;
    }

    /**
     * 選択した活動単位のユーザーリストを取得する.
     * @return 選択した活動単位のユーザーリスト
     */
    public List<CorresponGroupUser> getMemberList() {
        return memberList;
    }

    /**
     * 選択した活動単位のユーザーリストをセットする.
     * @param memberList 選択した活動単位のユーザーリスト
     */
    public void setMemberList(List<CorresponGroupUser> memberList) {
        this.memberList = memberList;
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
     * selectedAdminValueを取得します.
     * @return the selectedAdminValue
     */
    public String getSelectedAdminValue() {
        return selectedAdminValue;
    }

    /**
     * selectedAdminValueを設定します.
     * @param selectedAdminValue the selectedAdminValue to set
     */
    public void setSelectedAdminValue(String selectedAdminValue) {
        this.selectedAdminValue = selectedAdminValue;
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
     * 初期化処理が完了したか判定する.
     * @return 初期化完了true / 初期化失敗false
     */
    public boolean isInitialDisplaySuccess() {
        return initialDisplaySuccess;
    }

    /**
     * 初期化処理の結果をセットする.
     * @param initialDisplaySuccess 初期化処理結果
     */
    public void setInitialDisplaySuccess(boolean initialDisplaySuccess) {
        this.initialDisplaySuccess = initialDisplaySuccess;
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

        return condition;
    }

    /**
     * 現在のリクエストがSaveアクションによって発生した場合はtrue.
     * @return Saveアクションの場合true
     */
    public boolean isSaveAction() {
        return isActionInvoked("form:save");
    }


    /**
     * @param unremovableAdmin the unremovableAdmin to set
     */
    public void setUnremovableAdmin(String unremovableAdmin) {
        this.unremovableAdmin = unremovableAdmin;
    }

    /**
     * @return the unremovableAdmin
     */
    public String getUnremovableAdmin() {
        return unremovableAdmin;
    }


    /**
     * 画面初期化アクション.
     * @author opentone
     */
    static class InitializeAction extends AbstractAction {
        /**
         * serialVersionUID.
         */
        private static final long serialVersionUID = 66137020415922423L;
        /** アクション発生元ページ. */
        private GroupEditPage page;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page
         *            ページ
         */
        public InitializeAction(GroupEditPage page) {
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
                = page.getPreviousSearchCondition(SearchCorresponGroupCondition.class);
            page.checkProjectSelected();
            // 権限チェック
            checkPermission(page.id);
            // 必須パラメータチェック
            if (page.id == null) {
                throw new ServiceAbortException(
                    "ID is not specified.", MessageCode.E_INVALID_PARAMETER);
            } else if (StringUtils.isEmpty(page.backPage)) {
                throw new ServiceAbortException(
                    "Back Page is not specified.", MessageCode.E_INVALID_PARAMETER);
            }
            page.corresponGroup = page.corresponGroupService.find(page.id);
            page.name = page.corresponGroup.getName();
            // 部門管理者と一般ユーザを検索
            List<CorresponGroupUser> groupUserlist =
                    page.corresponGroupService.findMembers(page.id);
            // プロジェクトに所属しているユーザを検索
            List<ProjectUser> pUserlist = page.userService.search(page.createSearchUserCondition());
            // ユーザーの選択肢を設定
            setAddUsers(groupUserlist, pUserlist);
            setGroupUsers(groupUserlist);

            page.candidateUserValue = createUserValue();
            page.selectedAdminValue = createAdminValue();
            page.selectedMemberValue = createMemberValue();
            setUnremovableAdmin(page.id);
            // 初期画面表示成功
            page.initialDisplaySuccess = true;
        }

        private void setUnremovableAdmin(Long groupId) {
            //  ログインユーザーがこの活動単位のGroup Adminであれば
            //  削除できない管理者として設定する
            if (page.isGroupAdmin(page.id)) {
                page.unremovableAdmin = page.getCurrentUser().getEmpNo();
            }
        }

        /**
         * 権限チェックを行う.
         * @param 編集対象の活動単位ID
         * @throws ServiceAbortException 権限エラー
         */
        private void checkPermission(Long groupId) throws ServiceAbortException {
            if (!page.isSystemAdmin()
                    && !page.isProjectAdmin(page.getCurrentProjectId())
                    && !page.isGroupAdmin(groupId != null ? groupId : 0L)) {
                throw new ServiceAbortException(
                    ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT);
            }
        }

        /**
         * 追加可能なユーザを設定する.
         * プロジェクトに所属しているユーザーで、 まだ活動単位に設定されていないユーザーを設定.
         */
        private void setAddUsers(List<CorresponGroupUser> groupUserList,
                                 List<ProjectUser> pUserlist) {
            List<User> users = new ArrayList<User>();
            for (ProjectUser pUser : pUserlist) {
                User user = pUser.getUser();
                boolean add = true;
                for (CorresponGroupUser gUser : groupUserList) {
                    User checkUser = gUser.getUser();
                    if (user.getEmpNo().equals(checkUser.getEmpNo())) {
                        add = false;
                        break;
                    }
                }
                if (add) {
                    users.add(user);
                }
            }

            page.userList = users;
        }

        /**
         * すでに設定されているユーザと追加可能なユーザーを設定する.
         * また、追加可能なユーザとあわせてすべてのユーザーを設定する.
         */
        private void setGroupUsers(List<CorresponGroupUser> groupUserList) {
            List<CorresponGroupUser> admins = new ArrayList<CorresponGroupUser>();
            List<CorresponGroupUser> members = new ArrayList<CorresponGroupUser>();
            List<User> allUsers = new ArrayList<User>();

            String groupAdmin = SystemConfig.getValue(KEY_GROUP_ADMIN);
            String normalUser = SystemConfig.getValue(KEY_NORMAL_USER);
            for (CorresponGroupUser user : groupUserList) {
                if (groupAdmin.equals(user.getSecurityLevel())) {
                    admins.add(user);
                } else if (normalUser.equals(user.getSecurityLevel())) {
                    members.add(user);
                }
                allUsers.add(user.getUser());
            }

            allUsers.addAll(page.userList);

            page.adminList = admins;
            page.memberList = members;
            page.allUserList = allUsers;
        }

        private String createUserValue() {
            List<String> ids = new ArrayList<String>();
            for (User user : page.userList) {
                ids.add(user.getEmpNo());
            }
            return StringUtils.join(ids.iterator(), ",");
        }

        private String createAdminValue() {
            List<String> ids = new ArrayList<String>();
            for (CorresponGroupUser cUser : page.adminList) {
                ids.add(cUser.getUser().getEmpNo());
            }
            return StringUtils.join(ids.iterator(), ",");
        }

        private String createMemberValue() {
            List<String> ids = new ArrayList<String>();
            for (CorresponGroupUser cUser : page.memberList) {
                ids.add(cUser.getUser().getEmpNo());
            }
            return StringUtils.join(ids.iterator(), ",");
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
        private static final long serialVersionUID = 495759833176416372L;
        /** アクション発生元ページ. */
        private GroupEditPage page;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page
         *            ページ
         */
        public SaveAction(GroupEditPage page) {
            super(page);
            this.page = page;
        }

        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            page.corresponGroupService.save(getCorrsponGroup(), getUserList());
            page.setNextSearchCondition(page.previousCondition);
            page.initialize();

            page.setPageMessage(ApplicationMessageCode.SAVE_SUCCESSFUL);
        }

        /**
         * 更新する活動単位を取得する.
         */
        private CorresponGroup getCorrsponGroup() {
            page.corresponGroup.setName(page.name);
            return page.corresponGroup;
        }

        /**
         * 登録するユーザー情報を取得する.
         * @return 登録するユーザーリスト
         * @throws ServiceAbortException リクエストパラメータに該当するユーザーがいない
         */
        private List<User> getUserList() throws ServiceAbortException {
            String groupAdmin = SystemConfig.getValue(KEY_GROUP_ADMIN);
            String normalUser = SystemConfig.getValue(KEY_NORMAL_USER);

            List<User> newList = new ArrayList<User>();

            if (StringUtils.isNotEmpty(page.selectedAdminValue)) {
                setParameterUser(newList, page.selectedAdminValue, groupAdmin);
            }
            if (StringUtils.isNotEmpty(page.selectedMemberValue)) {
                setParameterUser(newList, page.selectedMemberValue, normalUser);
            }

            return newList;
        }

        /**
         * リクエストパラメータからユーザーを取得する.
         * @param parameter
         *            リクエストパラメータ
         * @throws ServiceAbortException リクエストパラメータに該当するユーザーがいない
         */
        private void setParameterUser(List<User> newList, String parameter, String securityLevel)
                throws ServiceAbortException {
            String[] userIdArray = parameter.split(DELIM_MEMBER);
            for (String userId : userIdArray) {
                User addUser = searchUserList(userId, securityLevel);
                if (addUser == null) {
                    addUser = searchCorresponGroupUserList(
                        userId, securityLevel, page.getAdminList());
                }
                if (addUser == null) {
                    addUser = searchCorresponGroupUserList(
                        userId, securityLevel, page.getMemberList());
                }
                if (addUser == null) {
                    throw new ServiceAbortException(
                        "Selected User is not found.", MessageCode.E_INVALID_PARAMETER);
                }
                newList.add(addUser);
            }
        }

        /**
         * 追加可能ユーザーリストからユーザを検索する.
         * @param userId ユーザID
         * @param securityLevel 権限レベル
         * @return ユーザ
         */
        private User searchUserList(String userId, String securityLevel) {
            User searchUser = null;
            for (User user : page.userList) {
                if (userId.equals(user.getUserId())) {
                    user.setSecurityLevel(securityLevel);
                    searchUser = user;
                    break;
                }
            }
            return searchUser;
        }

        /**
         * 指定された活動単位ユーザーリストからユーザを検索する.
         * @param userId ユーザID
         * @param securityLevel 権限レベル
         * @param list 活動単位ユーザーリスト
         * @return ユーザ
         */
        private User searchCorresponGroupUserList(String userId,
                                                  String securityLevel,
                                                  List<CorresponGroupUser> list) {
            User searchUser = null;
            for (CorresponGroupUser member : list) {
                User user = member.getUser();
                if (userId.equals(user.getUserId())) {
                    user.setSecurityLevel(securityLevel);
                    searchUser = user;
                    break;
                }
            }
            return searchUser;
        }
    }
}
