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
package jp.co.opentone.bsol.linkbinder.view;

import jp.co.opentone.bsol.framework.core.ProcessContext;
import jp.co.opentone.bsol.framework.core.config.SystemConfig;
import jp.co.opentone.bsol.framework.core.exception.ReflectionRuntimeException;
import jp.co.opentone.bsol.framework.core.message.Message;
import jp.co.opentone.bsol.framework.core.message.Messages;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.core.util.ArgumentValidator;
import jp.co.opentone.bsol.framework.core.util.DateUtil;
import jp.co.opentone.bsol.framework.web.extension.jsf.FacesHelper;
import jp.co.opentone.bsol.framework.web.extension.jsf.annotation.Transfer;
import jp.co.opentone.bsol.framework.web.view.action.ServiceActionHandler;
import jp.co.opentone.bsol.framework.web.view.flash.Flash;
import jp.co.opentone.bsol.framework.web.view.util.ViewHelper;
import jp.co.opentone.bsol.linkbinder.Constants;
import jp.co.opentone.bsol.linkbinder.action.AbstractAction;
import jp.co.opentone.bsol.linkbinder.dto.Correspon;
import jp.co.opentone.bsol.linkbinder.dto.LoginUserInfo;
import jp.co.opentone.bsol.linkbinder.dto.Project;
import jp.co.opentone.bsol.linkbinder.dto.ProjectCustomSetting;
import jp.co.opentone.bsol.linkbinder.dto.ProjectUser;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.Workflow;
import jp.co.opentone.bsol.linkbinder.dto.code.CorresponStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.ForLearning;
import jp.co.opentone.bsol.linkbinder.dto.condition.AbstractCondition;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponCondition;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchFullTextSearchCorresponCondition;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchUserCondition;
import jp.co.opentone.bsol.linkbinder.event.EventBus;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.service.UserPermissionHelper;
import jp.co.opentone.bsol.linkbinder.service.UserRoleHelper;
import jp.co.opentone.bsol.linkbinder.service.WorkflowHelper;
import jp.co.opentone.bsol.linkbinder.service.admin.ProjectCustomSettingService;
import jp.co.opentone.bsol.linkbinder.service.admin.UserService;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Pageの親クラス.
 * @author opentone
 */
public abstract class AbstractPage implements LinkBinderPage, Serializable {

    /**
     * SerialVersionID.
     */
    private static final long serialVersionUID = 8042658538697619215L;

    /**
     * ページ初期化時のデフォルトアクション名.
     */
    public static final String DEFAULT_ACTION_NAME = "Initialize";

    /**
     * 一覧表示用：「前へ」のリンク表示.
     */
    public static final String PREVIOUS = "<<";

    /**
     * 一覧表示用：「次へ」のリンク表示.
     */
    public static final String NEXT = ">>";

    /**
     * 学習用コンテンツエリアのタイトルを取得して返す.
     * @return 学習用コンテンツエリアタイトル
     */
    public String getLearningContentsTitle() {
        return SystemConfig.getValue(Constants.KEY_LEARNING_TITLE);
    }

    /**
     * ログイン情報を保持するManagedBean.
     */
    @Resource
    private LoginUserInfoHolder loginUserInfoHolder;

    /**
     * 現在処理中のプロジェクトID.
     */
    private String loginProjectId;
    /**
     * ログインユーザー.
     */
    @Resource
    private User currentUser;

    /**
     * ページの各アクションを起動するオブジェクト.
     * <p>
     * Service層を呼び出す全てのアクションはこのオブジェクト経由で起動すること!
     * </p>
     */
    @Resource
    //CHECKSTYLE:OFF
    protected ServiceActionHandler handler;
    //CHECKSTYLE:ON
    /**
     * viewの共通処理を集めたオブジェクト.
     */
    @Resource
    //CHECKSTYLE:OFF
    protected ViewHelper viewHelper;
    //CHECKSTYLE:ON

    /**
     * ユーザーの権限に関する共通処理を集めたヘルパーオブジェクト.
     */
    @Resource
    private UserPermissionHelper userPermissionHelper;

    /**
     * ユーザーの役割に関する共通処理を集めたヘルパーオブジェクト.
     */
    @Resource
    private UserRoleHelper userRoleHelper;

    /**
     * ワークフローに関する共通処理を集めたヘルパーオブジェクト.
     */
    @Resource
    private WorkflowHelper workflowHelper;

    /**
     * ユーザー処理クラス.
     */
    @Resource
    private UserService userService;

    /**
     * プロジェクトカスタム設定情報処理クラス.
     */
    @Resource
    private ProjectCustomSettingService projectCustomSettingService;

    @Resource
    protected EventBus eventBus;

    /**
     * 遷移先画面に値を引き継ぐ判定.
     */
    private boolean transferNext;

    /**
     * SystemAdmin判定.
     */
    private boolean systemAdmin;

    /**
     * ProjectAdmin判定.
     */
    private boolean projectAdmin;

    /**
     * GroupAdmin判定.
     */
    private boolean groupAdmin;

    /**
     * コレポン文書の送信元/宛先のGroupAdmin判定.
     */
    private boolean anyGroupAdmin;

    /**
     * 表示用ワークフロー処理結果.
     */
    private List<Workflow> displayWorkflowList;

    /**
     * プロジェクトID.
     */
    private String projectId;

    /**
     * コレポン文書活動単位ID.
     */
    private Long corresponGroupId;

    /**
     * 現在実行中アクションの名前.
     */
    private String actionName;

    /**
     * 空のインスタンスを生成する.
     */
    public AbstractPage() {
    }

    /**
     * インスタンス生成後の処理.
     */
    @PostConstruct
    public final void afterInstanciated() {
        setUpLoginProjectId();
        setHeaderInfoToFlash();

        setActionName(DEFAULT_ACTION_NAME);

        // ログインユーザーの情報をActionHandlerに設定
        handler.setUser(currentUser);

        // 現在選択中のプロジェクト情報を設定
        Map<String, Object> values = new HashMap<String, Object>();
        values.put(Constants.KEY_PROJECT, getCurrentProject());

        // ベースURLを設定
        HttpServletRequest request
            = (HttpServletRequest) FacesContext.getCurrentInstance(
                ).getExternalContext().getRequest();
        if (request != null) {
            values.put(Constants.KEY_BASE_URL, viewHelper.getBasePath());
            values.put(Constants.KEY_CONTEXT_URL, request.getContextPath());
        }
        handler.setActionContextValues(values);

    }
    /**
     * リクエストイベント処理開始前の初期化処理.
     */
    @Override
    public final void setUp() {
        setHeaderInfoToFlash();
    }

    public void setHeaderInfoToFlash() {
        LoginUserInfo info;
        if (StringUtils.isNotEmpty(loginProjectId)) {
            info = loginUserInfoHolder.getLoginProjectUserInfo(loginProjectId);
        } else {
            info = loginUserInfoHolder.getLoginUserInfo();
        }
        setHeaderInfoToFlash(info);
    }

    public void setHeaderInfoToFlash(LoginUserInfo info) {
        if (info == null) {
            return;
        }
        javax.faces.context.Flash f = viewHelper.getExternalContext().getFlash();
        if (info.getLoginProject() != null) {
            f.putNow("projectId", info.getLoginProject().getProjectId());
            f.putNow("projectNameE", info.getLoginProject().getNameE());
            f.putNow("projectClientNameE", info.getLoginProject().getClientNameE());
        } else {
            f.putNow("projectId", null);
            f.putNow("projectNameE", null);
            f.putNow("projectClientNameE", null);
        }
        if (info.getLoginUser() != null) {
            f.putNow("userId", info.getLoginUser().getUserId());
            f.putNow("userLabel", info.getLoginUser().getLabel());
        }
    }

    private void setUpLoginProjectId() {
        FacesHelper h = new FacesHelper(FacesContext.getCurrentInstance());
        if (h.isGetRequest()) {
            String id =
                (String) viewHelper.getExternalContext().getRequestParameterMap().get("projectId");
            setLoginProjectId(id);
        }
    }

    /**
     * 次画面遷移用のURL文字列に変換する.
     *
     *
     * @param path 基準となるパス表現
     * @return 変換後の文字列
     */
    public String toUrl(String path) {
        return toUrl(path, true);
    }

    /**
     * 画面遷移用のURL文字列に変換する.
     * 指定されたフラグに従いプロジェクトID情報を付与する.
     *
     * @param path 変換後の文字列
     * @param withProjectId URLにプロジェクトID情報を付与する場合はtrue
     * @return 変換後の文字列
     */
    public String toUrl(String path, boolean withProjectId) {
        // pathがnullの場合そのまま返す
        if (path == null) {
            return path;
        }

        if (!withProjectId || containsProjectId(path)) {
            return path;
        }

        StringBuilder result = new StringBuilder(path);
        result.append(path.indexOf('?') != -1 ? '&' : '?');
        String pjId = getCurrentProjectId();
        if (StringUtils.isEmpty(pjId)) {
            pjId = loginProjectId;
        }
        result.append("projectId=").append(pjId);

        return result.toString();
    }

    private boolean containsProjectId(String path) {
        return path.matches(".*projectId=.*");
    }

     /**
     * 現在のリクエストがactionNameにより起動されている場合はtrueを返す.
     * @param action
     *            アクション名. submitボタンのvalue
     * @return actionNameにより起動されている場合はtrue
     */
    public boolean isActionInvoked(String action) {
        HttpServletRequest req =
            (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext()
                    .getRequest();
        if (isPartialAjax()) {
            return StringUtils
                    .equals(action, req.getParameter("javax.faces.source"));
        } else {
            return StringUtils.isNotEmpty(req.getParameter(action));
        }
    }

    /**
     * 現在のリクエストがJSF2のAjaxによるPartial Requestの場合はtrueを返す.
     * @return Partial Requestの場合はtrue
     */
    public boolean isPartialAjax() {
        String value =
            ((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext()
                    .getRequest()).getParameter("javax.faces.partial.ajax");

        return Boolean.parseBoolean(value);
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    /**
     * このオブジェクトのbean名を返す.
     * @return bean名
     */
    public String getBeanName() {
        String name = this.getClass().getSimpleName();
        return name.substring(0, 1).toLowerCase() + name.substring(1);
    }

    /**
     * @return the viewHelper
     */
    public ViewHelper getViewHelper() {
        return viewHelper;
    }

    /**
     * @param viewHelper
     *            the viewHelper to set
     */
    public void setViewHelper(ViewHelper viewHelper) {
        this.viewHelper = viewHelper;
    }

    public ServiceActionHandler getHandler() {
        return handler;
    }

    public void setHandler(ServiceActionHandler handler) {
        this.handler = handler;
    }

    /*
     * (non-Javadoc)
     * @see jp.co.opentone.bsol.framework.view.Page#isTransferNext()
     */
    public boolean isTransferNext() {
        return transferNext;
    }

    /*
     * (non-Javadoc)
     * @see jp.co.opentone.bsol.framework.view.Page#setTransferNext(boolean)
     */
    public void setTransferNext(boolean transferNext) {
        this.transferNext = transferNext;

        if (transferNext) {
            // 次ページへの引き継ぎ対象の値をFlashに保存
            Map<String, Object> values = collectTransferValues();
            Flash flash = new Flash();
            if (isTransferNext()) {
                flash.setValue(KEY_TRANSFER, values);
            }
        }
    }

    private Map<String, Object> collectTransferValues() {
        Map<String, Object> values = new HashMap<String, Object>();
        for (Field field : getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Transfer.class)) {
                field.setAccessible(true);
                try {
                    Object value = field.get(this);
                    if (value != null) {
                        values.put(field.getName(), field.get(this));
                    }
                } catch (IllegalArgumentException e) {
                    throw new ReflectionRuntimeException(e);
                } catch (IllegalAccessException e) {
                    throw new ReflectionRuntimeException(e);
                }
            }
        }
        return values;
    }

    /**
     * 現在選択中のプロジェクトIDを返す.
     * <p>
     * プロジェクトを選択せずに利用可能なページでは<code>null</code>の可能性がある.
     * </p>
     * @return プロジェクトID
     */
    public String getCurrentProjectId() {
        Project p = getCurrentProject();
        if (p != null) {
            return p.getProjectId();
        }
        return null;
    }

    /**
     * 現在選択中のプロジェクト情報を返す.
     * <p>
     * プロジェクトを選択せずに利用可能なページでは<code>null</code>の可能性がある.
     * </p>
     * @return プロジェクト情報
     */
    public Project getCurrentProject() {
        String p = getLoginProjectId();
        if (StringUtils.isEmpty(p)) {
            return null;
        }
        return loginUserInfoHolder.getLoginProjectInfo(p);
    }

    /**
     * 現在選択中プロジェクトのUse Person in Chargeを取得する.
     * プロジェクトカスタム設定情報が未設定の場合、
     * defualtValueのプロジェクトカスタム設定情報のUse Person in Chargeを取得する.
     * @return Person in Chargeの使用可否
     */
    public boolean isUsePersonInCharge() {
        if (getCurrentProject() != null) {
            if (getCurrentProject().getProjectCustomSetting() != null) {
                return getCurrentProject().getProjectCustomSetting().isUsePersonInCharge();
            }
        }
        return ProjectCustomSetting.DEFAULT_USE_PERSON_IN_CHARGE;
    }

    /**
     * 現在選択中プロジェクトのDefault Statusを取得する.
     * プロジェクトカスタム設定情報が未設定の場合、
     * defualtValueのプロジェクトカスタム設定情報のDefault Statusを取得する.
     * @return Person in Chargeの使用可否
     */
    public CorresponStatus getDefaultStatus() {
        if (getCurrentProject() != null) {
            if (getCurrentProject().getProjectCustomSetting() != null) {
                return getCurrentProject().getProjectCustomSetting().getDefaultStatus();
            }
        }
        return ProjectCustomSetting.DEFAULT_CORRESPON_STATUS;
    }

    public boolean isLearningProject() {
        Project project = getCurrentProject();
        if(project != null) {
            if (ForLearning.LEARNING.equals(project.getForLearning())) {
                return true;
            }
        }
        return false;
    }

    /**
     * ログインユーザーが学習用文書を利用できる場合はtrueを返す.
     * @return 利用可の場合はtrue
     */
    public boolean isUseLearning() {
        return getCurrentUser().isUseLearning();
    }

    /**
     * 現在のプロジェクトが学習用文書機能を利用できる場合はtrueを返す.
     * @return
     */
    public boolean isProjectUseLearning() {
        Project p = getCurrentProject();
        if (p != null && p.getProjectCustomSetting() != null) {
            return p.getProjectCustomSetting().isUseLearning();
        }

        return false;
    }

    /**
     * ログインユーザーの情報のうち、現在選択中プロジェクトに関する情報を返す.
     * <p>
     * プロジェクトを選択せずに利用可能なページでは<code>null</code>の可能性がある.
     * </p>
     * @return プロジェクトユーザー情報
     */
    public ProjectUser getCurrentProjectUser() {
        return loginUserInfoHolder.getLoginUserInfo(getLoginProjectId());
    }

    /**
     * @param currentUser
     *            the currentUser to set
     */
    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    /**
     * @return the currentUser
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * ログインユーザーがSystem Adminの権限を持つ場合はtrue.
     * @return System Adminの権限を持つ場合はtrue、それ以外はfalse
     */
    public boolean isSystemAdmin() {
        handler.handleAction(new SystemAdminAction(this));

        return systemAdmin;
    }

    /**
     * ログインユーザーが、指定されたプロジェクトに対するProject Adminの権限を持つ場合はtrue.
     * @param checkProjectId
     *            対象プロジェクトID
     * @return Project Adminの権限を持つ場合はtrue、それ以外はfalse
     */
    public boolean isProjectAdmin(String checkProjectId) {
        if (StringUtils.isEmpty(checkProjectId)) {
            return false;
        }
        projectId = checkProjectId;

        handler.handleAction(new ProjectAdminAction(this));

        return projectAdmin;
    }

    /**
     * ログインユーザーが、指定された活動単位に対するGroup Adminの権限を持つ場合はtrue.
     * @param groupId
     *            活動単位を識別するID
     * @return Group Adminの権限を持つ場合はtrue、それ以外はfalse
     */
    public boolean isGroupAdmin(Long groupId) {
        ArgumentValidator.validateNotNull(groupId);
        corresponGroupId = groupId;

        handler.handleAction(new GroupAdminAction(this));

        return groupAdmin;
    }

    /**
     * コレポン文書の送信元/宛先に、ログインユーザーがGroup Adminの権限を持つ
     * 活動単位が含まれる場合はtrueを返す.
     * @param c
     *            コレポン文書
     * @return ログインユーザーがGroup Adminの権限を持つ活動単位が含まれる場合はtrue
     */
    public boolean isAnyGroupAdmin(Correspon c) {
        ArgumentValidator.validateNotNull(c);
        handler.handleAction(new AnyGroupAdminAction(this, c));
        return anyGroupAdmin;
    }

    /**
     * プロジェクトの中にログインユーザーがGroup Adminの権限を持つ活動単位が含まれるか判定する.
     *
     * @param checkProjectId プロジェクトID
     * @return ログインユーザーがGroup Adminの権限を持つ活動単位が含まれる場合はtrue
     */
    public boolean isAnyGroupAdmin(String checkProjectId) {
        ArgumentValidator.validateNotNull(checkProjectId);
        return getUserPermissionHelper().isAnyGroupAdmin(checkProjectId);
    }

    /**
     * プロジェクトが選択されているかどうか判定する.
     * @return プロジェクトが選択されていればtrue
     */
    public boolean isProjectSelected() {
        return getCurrentProjectId() != null;
    }

    /**
     * プロジェクトが選択されているか判定する.
     * 選択されていない場合は例外が発生する.
     * @throws ServiceAbortException プロジェクトが選択されていない
     */
    public void checkProjectSelected() throws ServiceAbortException {
        if (!isProjectSelected()) {
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT);
        }
    }

    /**
     * 次ページに表示するメッセージを設定する.
     * @param messageKey メッセージのKEY
     * @param object 置換パラメータ
     */
    public void setNextPageMessage(String messageKey, Object... object) {
        Flash flash = new Flash();
        Message message = Messages.getMessage(messageKey, object);
        flash.setValue(KEY_FLASH_MASSAGE, viewHelper.createFacesMessage(message));
    }

    /**
     * 自ページに表示するメッセージを設定する.
     * @param messageKey メッセージのKEY
     * @param object 置換パラメータ
     */
    public void setPageMessage(String messageKey, Object... object) {
        ProcessContext pc = ProcessContext.getCurrentContext();
        pc.clearMessages();
        pc.addMessage(messageKey, object);
    }

    private LoginUserInfo getCurrentLoginUserInfo() {
        if (loginUserInfoHolder == null) {
            return null;
        }

        LoginUserInfo info;
        if (StringUtils.isNotEmpty(loginProjectId)) {
            info = loginUserInfoHolder.getLoginProjectUserInfo(loginProjectId);
        } else {
            info = loginUserInfoHolder.getLoginUserInfo();
        }
        return info;
    }

    /**
     * 検索条件をフラッシュに保存する.
     * @param condition 検索条件
     */
    public void setNextSearchCondition(AbstractCondition condition) {
        LoginUserInfo info = getCurrentLoginUserInfo();
        if (info != null) {
            info.setValue(createSearchConditionKey(condition), condition);
        }
    }

    private String createSearchConditionKey(AbstractCondition condition) {
        return condition != null
            ? createSearchConditionKey(condition.getClass())
            : KEY_SEARCH_CONDITION;
    }

    private String createSearchConditionKey(Class<? extends AbstractCondition> clazz) {
        return KEY_SEARCH_CONDITION + "_" + clazz.getSimpleName();
    }

    /**
     * 検索条件をフラッシュから取得する.
     * @param clazz 取得対象の検索条件クラス
     * @return 検索条件
     */
    public AbstractCondition getPreviousSearchCondition(Class<? extends AbstractCondition> clazz) {
        LoginUserInfo info = getCurrentLoginUserInfo();
        if (info != null) {
            Object result = info.getValue(createSearchConditionKey(clazz));
            if (result != null && result.getClass().isAssignableFrom(clazz)) {
                return (AbstractCondition) result;
            }
        }
        return null;
    }

    /**
     * セッションに保存されたコレポン文書検索条件を取得する.
     * @return コレポン文書検索条件
     */
    public SearchCorresponCondition getCurrentSearchCorresponCondition() {
        if (StringUtils.isNotEmpty(loginProjectId)) {
            LoginUserInfo info = loginUserInfoHolder.getLoginProjectUserInfo(loginProjectId);
            if (info != null) {
                return (SearchCorresponCondition) info.getValue(
                                Constants.KEY_SEARCH_CORRESPON_CONDITION);
            }
        }
        return null;
    }

    /**
     * コレポン文書検索条件をセッションに保存する.
     * <p>
     * loginProjectIdのプロジェクトの検索条件として設定する
     * @param condition コレポン文書検索条件
     */
    public void setCurrentSearchCorresponCondition(SearchCorresponCondition condition) {
        setCurrentSearchCorresponCondition(condition, loginProjectId);
    }

    /**
     * コレポン文書検索条件をセッションに保存する.
     * @param condition コレポン文書検索条件
     * @param selectedProjectId プロジェクトID
     */
    public void setCurrentSearchCorresponCondition(
            SearchCorresponCondition condition, String selectedProjectId) {
        if (StringUtils.isNotEmpty(selectedProjectId)) {
            LoginUserInfo info = loginUserInfoHolder.getLoginProjectUserInfo(selectedProjectId);
            if (info != null) {
                info.setValue(Constants.KEY_SEARCH_CORRESPON_CONDITION, condition);
            }
        }
    }

    /**
     * セッションに保存されコレポン文書全文検索条件を取得する.
     * @return コレポン文書全文検索条件
     */
    public SearchFullTextSearchCorresponCondition
                getCurrentSearchFullTextSearchCorresponCondition() {
        if (StringUtils.isNotEmpty(loginProjectId)) {
            LoginUserInfo info = loginUserInfoHolder.getLoginProjectUserInfo(loginProjectId);
            if (info != null) {
                return (SearchFullTextSearchCorresponCondition) info.getValue(
                                Constants.KEY_SEARCH_FULL_TEXT_SEARCH_CORRESPON_CONDTION);
            }
        }
        return null;
    }

    /**
     * コレポン文書全文検索条件をセッションに保存する.
     * @param condition コレポン文書全文検索条件
     */
    public void setCurrentSearchFullTextSearchCorresponCondition(
        SearchFullTextSearchCorresponCondition condition) {
        if (StringUtils.isNotEmpty(loginProjectId)) {
            LoginUserInfo info = loginUserInfoHolder.getLoginProjectUserInfo(loginProjectId);
            if (info != null) {
                info.setValue(
                        Constants.KEY_SEARCH_FULL_TEXT_SEARCH_CORRESPON_CONDTION,
                        condition);
            }
        }
    }

    /**
     * CurrentProjectとCurrentProjectUserをセッションに保存する.
     * @param project プロジェクト
     */
    public void setCurrentProjectInfo(Project project) {
        handler.handleAction(new SetCurrentProjectAction(this, project));
    }

    /**
     * @param actionName
     *            the actionName to set
     */
    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    /**
     * @return the actionName
     */
    public String getActionName() {
        return actionName;
    }

    /**
     * リクエストから直接actionNameの値を取得する.
     * @return actionName
     */
    public String getRequestedActionName() {
        HttpServletRequest request =
            (HttpServletRequest) FacesContext
                        .getCurrentInstance().getExternalContext().getRequest();
        if (request == null) {
            return "";
        }

        String result = request.getParameter("form:actionName");
        if (result == null) {
            return "";
        }
        return result;
    }

    /**
     * 出力ファイルのファイル名を生成する.
     * @return ファイル名
     */
    public String createFileName() {
        return new SimpleDateFormat("yyyyMMddHHmmss").format(DateUtil.getNow());
    }

    /**
     * 一覧表示用：「前へ」の表示用文字列を取得する.
     * @return 「前へ」の表示用文字列
     */
    public String getPreviousLabel() {
        return PREVIOUS;
    }

    /**
     * 一覧表示用：「次へ」の表示用文字列を取得する.
     * @return 「次へ」の表示用文字列
     */
    public String getNextLabel() {
        return NEXT;
    }

    /**
     * 一覧表示用：指定した検索条件をコピーし、全件取得する検索条件として取得する.
     * @param condition 検索条件
     * @return 全件取得用検索条件
     */
    public AbstractCondition cloneToAllRowCondition(AbstractCondition condition) {
        AbstractCondition clone = null;
        try {
            clone = condition.getClass().getConstructor().newInstance();

            PropertyUtils.copyProperties(clone, condition);

            //全件取得
            clone.setPageNo(1);
            clone.setPageRowNum(Integer.MAX_VALUE);

        } catch (NoSuchMethodException e) {
            throw new ReflectionRuntimeException(e);
        } catch (IllegalArgumentException e) {
            throw new ReflectionRuntimeException(e);
        } catch (SecurityException e) {
            throw new ReflectionRuntimeException(e);
        } catch (InstantiationException e) {
            throw new ReflectionRuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new ReflectionRuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new ReflectionRuntimeException(e);
        }

        return clone;
    }

    /**
     * オブジェクトのフィールドをコピーした新しいオブジェクトを取得する.
     * ただし、ディープコピーには対応していない.
     * @param object オブジェクト
     * @return コピーしたオブジェクト
     */
    public Object cloneToObject(Object object) {
        Object clone = null;
        try {
            clone = object.getClass().getConstructor().newInstance();
            PropertyUtils.copyProperties(clone, object);
        } catch (NoSuchMethodException e) {
            throw new ReflectionRuntimeException(e);
        } catch (IllegalArgumentException e) {
            throw new ReflectionRuntimeException(e);
        } catch (SecurityException e) {
            throw new ReflectionRuntimeException(e);
        } catch (InstantiationException e) {
            throw new ReflectionRuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new ReflectionRuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new ReflectionRuntimeException(e);
        }
        return clone;
    }

    /**
     * @return the userPermissionHelper
     */
    public UserPermissionHelper getUserPermissionHelper() {
        return userPermissionHelper;
    }

    /**
     * @return the userRoleHelper
     */
    public UserRoleHelper getUserRoleHelper() {
        return userRoleHelper;
    }

    public List<Workflow> createDisplayWorkflowList(Correspon correspon) {
        ArgumentValidator.validateNotNull(correspon);
        handler.handleAction(new CreateDisplayWorkflowListAction(this, correspon));

        return displayWorkflowList;
    }

    public List<Workflow> createDisplayWorkflowList(User preparer, List<Workflow> workflows) {
        ArgumentValidator.validateNotNull(preparer);
        ArgumentValidator.validateNotNull(workflows);

        Correspon c = new Correspon();
        c.setCreatedBy(preparer);
        c.setWorkflows(workflows);
        handler.handleAction(new CreateDisplayWorkflowListAction(this, c));

        return displayWorkflowList;
    }

    /**
     * @param loginUserInfoHolder the loginUserInfoHolder to set
     */
    public void setLoginUserInfoHolder(LoginUserInfoHolder loginUserInfoHolder) {
        this.loginUserInfoHolder = loginUserInfoHolder;
    }

    /**
     * @return the loginUserInfoHolder
     */
    public LoginUserInfoHolder getLoginUserInfoHolder() {
        return loginUserInfoHolder;
    }

    /**
     * @param loginProjectId the loginProjectId to set
     */
    public void setLoginProjectId(String loginProjectId) {
        this.loginProjectId = loginProjectId;
    }

    /**
     * @return the loginProjectId
     */
    public String getLoginProjectId() {
        return loginProjectId;
    }

    /**
     * プロジェクトを選択中かを返す.
     *
     * @return 選択中:true
     */
    public boolean isLoginProject() {
        if (StringUtils.isNotEmpty(loginProjectId)) {
           return true;
        }
        return false;
    }

    static class SystemAdminAction extends AbstractAction {
        /**
         * SerialVersionUID.
         */
        private static final long serialVersionUID = -6342245885831727090L;

        /** アクション呼び出し元ページ. */
        private AbstractPage page;

        public SystemAdminAction(AbstractPage page) {
            super(page);
            this.page = page;
        }

        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            page.systemAdmin = page.getUserPermissionHelper().isSystemAdmin(page.currentUser);
        }
    }

    static class ProjectAdminAction extends AbstractAction {
        /**
         * SerialVersionUID.
         */
        private static final long serialVersionUID = 1909154663217521352L;

        /** アクション呼び出し元ページ. */
        private AbstractPage page;

        public ProjectAdminAction(AbstractPage page) {
            super(page);
            this.page = page;
        }

        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            page.projectAdmin =
                page.getUserPermissionHelper().isProjectAdmin(page.currentUser, page.projectId);
        }
    }

    static class GroupAdminAction extends AbstractAction {
        /**
         * SerialVersionUID.
         */
        private static final long serialVersionUID = -2416142075620249874L;

        /** アクション呼び出し元ページ. */
        private AbstractPage page;

        public GroupAdminAction(AbstractPage page) {
            super(page);
            this.page = page;
        }

        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            page.groupAdmin =
                page.getUserPermissionHelper().isGroupAdmin(page.currentUser,
                                                            page.corresponGroupId);
        }
    }

    static class AnyGroupAdminAction extends AbstractAction {
        /**
         * SerialVersionUID.
         */
        private static final long serialVersionUID = 3410090289269928398L;

        /** アクション呼び出し元ページ. */
        private AbstractPage page;
        /** コレポン文書. */
        private Correspon c;

        public AnyGroupAdminAction(AbstractPage page, Correspon c) {
            super(page);
            this.page = page;
            this.c = c;
        }

        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            page.anyGroupAdmin = page.getUserPermissionHelper().isAnyGroupAdmin(c);
        }
    }

    /**
     * CurrentProjectとCurrentProjectUserを設定する.
     * @author opentone
     */
    static class SetCurrentProjectAction extends AbstractAction {
        /**
         * SerialVersionUID.
         */
        private static final long serialVersionUID = -7699429985264318982L;

        /** アクション呼び出し元ページ. */
        private AbstractPage page;
        /** プロジェクト. */
        private Project project;

        public SetCurrentProjectAction(AbstractPage page, Project project) {
            super(page);
            this.page = page;
            this.project = project;
        }

        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            // プロジェクトカスタム設定情報をプロジェクトに設定
            project.setProjectCustomSetting(
                page.projectCustomSettingService.find(project.getProjectId(), false));

            SearchUserCondition condition = new SearchUserCondition();
            condition.setProjectId(project.getProjectId());
            condition.setEmpNo(page.getCurrentUser().getEmpNo());
            ProjectUser pu = null;
            List<ProjectUser> list = page.userService.search(condition);
            if (list.size() > 0) {
                pu = list.get(0);
            }
            try {
                addLoginProjectInfo(page.currentUser, project, pu);
            } catch (IllegalUserLoginException e) {
                throw new ServiceAbortException(
                        "Invalid user", e, ApplicationMessageCode.INVALID_USER);
            }
        }

        private void addLoginProjectInfo(User user, Project p, ProjectUser pu)
                throws IllegalUserLoginException {
            LoginUserInfo info = new LoginUserInfo();
            info.setLoginUser(user);
            info.setLoginProject(p);
            info.setProjectUser(pu);

            page.loginUserInfoHolder.addLoginProject(info);
        }
    }

    /**
     * Project情報をクリアする.
     * @author opentone
     */
    static class ClearCurrentProjectAction extends AbstractAction {
        /**
         * SerialVersionUID.
         */
        private static final long serialVersionUID = -3225783751851640016L;

        /** アクション呼び出し元ページ. */
        private AbstractPage page;

        public ClearCurrentProjectAction(AbstractPage page) {
            super(page);
            this.page = page;
        }

        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            page.viewHelper.removeSessionValue(Constants.KEY_PROJECT);
            page.viewHelper.removeSessionValue(Constants.KEY_PROJECT_USER);
            page.viewHelper.removeSessionValue(Constants.KEY_SEARCH_CORRESPON_CONDITION);
            page.viewHelper.removeSessionValue(
                Constants.KEY_SEARCH_FULL_TEXT_SEARCH_CORRESPON_CONDTION);
        }
    }

    static class CreateDisplayWorkflowListAction extends AbstractAction {
        /**
         * SerialVersionUID.
         */
        private static final long serialVersionUID = -624113434207894144L;

        /** アクション呼び出し元ページ. */
        private AbstractPage page;
        /** コレポン文書. */
        private Correspon c;

        public CreateDisplayWorkflowListAction(AbstractPage page, Correspon c) {
            super(page);
            this.page = page;
            this.c = c;
        }

        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            page.displayWorkflowList = page.workflowHelper.createWorkflowListWithPreparer(c);
        }
    }
}
