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
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import javax.annotation.ManagedBean;
import javax.annotation.Resource;
import javax.faces.model.SelectItem;

import org.hibernate.validator.constraints.Length;
import org.springframework.context.annotation.Scope;

import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.core.util.JSONUtil;
import jp.co.opentone.bsol.framework.core.validation.constraints.Alphanumeric;
import jp.co.opentone.bsol.framework.core.validation.constraints.Required;
import jp.co.opentone.bsol.framework.web.extension.jsf.annotation.Initialize;
import jp.co.opentone.bsol.framework.web.extension.jsf.annotation.Transfer;
import jp.co.opentone.bsol.linkbinder.action.AbstractAction;
import jp.co.opentone.bsol.linkbinder.dto.CorresponType;
import jp.co.opentone.bsol.linkbinder.dto.ProjectCustomSetting;
import jp.co.opentone.bsol.linkbinder.dto.WorkflowPattern;
import jp.co.opentone.bsol.linkbinder.dto.code.AllowApproverToBrowse;
import jp.co.opentone.bsol.linkbinder.dto.code.CorresponTypeAdmittee;
import jp.co.opentone.bsol.linkbinder.dto.code.ForceToUseWorkflow;
import jp.co.opentone.bsol.linkbinder.dto.condition.AbstractCondition;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponTypeCondition;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.service.admin.CorresponTypeService;
import jp.co.opentone.bsol.linkbinder.service.admin.ProjectCustomSettingService;
import jp.co.opentone.bsol.linkbinder.validation.groups.ValidationGroupBuilder;
import jp.co.opentone.bsol.linkbinder.view.AbstractPage;

/**
 * コレポン文書種別入力画面.
 * @author opentone
 */
@ManagedBean
@Scope("view")
public class CorresponTypeEditPage extends AbstractPage {

    /**
     * serialVersionUID.
     */
    private static final long serialVersionUID = -7005475910392795129L;

    /**
     * 表題（新規登録）ラベル.
     */
    private static final String NEW = "文書種類新規作成";

    /**
     * 表題（更新）ラベル.
     */
    private static final String UPDATE = "文書種類更新";

    /**
     * コレポン文書種別サービス.
     */
    @Resource
    private CorresponTypeService service;

    /**
     * プロジェクトカスタム設定画面サービス.
     */
    @Resource
    private ProjectCustomSettingService projectCustomSettingService;

    /**
     * 初期画面表示判定値.
     */
    @Transfer
    private boolean initialDisplaySuccess = false;

    /**
     * プロジェクトID. SystemAdminの場合はnull.
     */
    @Transfer
    private String projectId;

    /**
     * 表示タイトル.
     */
    @Transfer
    private String title;

    /**
     * ID.
     * <p>
     * 起動元画面からのクエリパラメータが自動設定される. 新規登録ならばnull
     * </p>
     */
    @Transfer
    private Long id;

    /**
     * projectCorresponTypeId.
     * <p>
     * 起動元画面からのクエリパラメータが自動設定される. 新規登録ならばnull
     * </p>
     */
    @Transfer
    private Long projectCorresponTypeId;

    /**
     * コレポン文書種別.
     * <p>
     * 新規登録ならばnull
     * </p>
     */
    @Transfer
    private CorresponType corresponType;

    /**
     * コレポン文書種別コード.
     */
//    @SkipValidation("#{!corresponTypeEditPage.nextAction}")
    @Transfer
    @Required
    @Alphanumeric
    //CHECKSTYLE:OFF
    @Length(max = 10)
    //CHECKSTYLE:ON
    private String type;

    /**
     * コレポン文書種別名.
     */
//    @SkipValidation("#{!corresponTypeEditPage.nextAction}")
    @Transfer
    @Required
    //CHECKSTYLE:OFF
    @Length(max = 100)
    //CHECKSTYLE:ON
    private String name;

    /**
     * 検索条件. 起動元画面からのFlashパラメータを設定する.
     */
    @Transfer
    private AbstractCondition previousCondition;

    /**
     * 承認フローパターン.
     */
    @Transfer
    private List<WorkflowPattern> workflowPatternList;

    /**
     * 登録、または更新したい承認フローパターン.
     */
//    @SkipValidation("#{!corresponTypeEditPage.nextAction}")
    @Required
    @Transfer
    private Long selectWorkflowPattern;

    /**
     * 登録、または更新したい承認フローパターンの選択肢.
     */
    @Transfer
    private List<SelectItem> selectWorkflowPatternList = new ArrayList<SelectItem>();

    /**
     * allowフラグ.
     */
    @Transfer
    private boolean allow;

    /**
     * forceフラグ.
     */
    @Transfer
    private boolean force;

    /**
     * 確認画面から戻ってきた場合はtrue.
     */
    private boolean back;


    /**
     * この CorresponType を利用可能なユーザー種別.
     */
    @Transfer
    private EnumSet<CorresponTypeAdmittee> accessControlFlags;

    /**
     * アクセスコントロールを使用するかどうかを示すフラグ.
     */
    @Transfer
    private boolean useAccessControl;

    /**
     * コンストラクタ.
     */
    public CorresponTypeEditPage() {
        // デフォルトはすべてのユーザーが利用可能
        accessControlFlags = EnumSet.allOf(CorresponTypeAdmittee.class);
    }

    /**
     * 入力検証グループ名を返す.
     * @return 入力検証グループ名
     */
    public String getValidationGroups() {
        if (isActionInvoked("form:next")) {
            return new ValidationGroupBuilder().defaultGroup().toString();
        } else {
            return new ValidationGroupBuilder().skipValidationGroup().toString();
        }
    }

    /**
     * 画面を初期化する.
     * <p>
     * 他画面から遷移した時に起動される. クエリパラメータで指定されたIDに対応する、コレポン文書種別を表示する.
     * </p>
     */
    @Initialize
    public void initialize() {
        handler.handleAction(new InitializeAction(this));
    }

    /**
     * コレポン文書種別入力確認画面へ遷移 （validateチェック）.
     * @return 遷移先画面ID
     */
    public String next() {
        if (handler.handleAction(new ValidateAction(this))) {
            setNextSearchCondition(previousCondition);
            setTransferNext(true);
            return toUrl("corresponTypeConfirmation", isLoginProject());
        }
        return null;
    }

    /**
     * 一覧画面へ遷移する.
     * @return 一覧画面
     */
    public String back() {
        setNextSearchCondition(previousCondition);
        return toUrl("corresponTypeIndex", isLoginProject());
    }

    /**
     * initialDisplaySuccessを返す.
     * @return 初期画面表示判定値
     */
    public boolean isInitialDisplaySuccess() {
        return initialDisplaySuccess;
    }

    /**
     * initialDisplaySuccessを設定する.
     * @param initialDisplaySuccess 初期画面表示判定値t
     */
    public void setInitialDisplaySuccess(boolean initialDisplaySuccess) {
        this.initialDisplaySuccess = initialDisplaySuccess;
    }

    /**
     * 表示タイトルを返す.
     * @return 表示タイトル
     */
    public String getTitle() {
        return title;
    }

    /**
     * 表示タイトルを設定する.
     * @param title 表示タイトル
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * idを返す.
     * @return ID
     */
    public Long getId() {
        return id;
    }

    /**
     * idを設定する.
     * @param id ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * コレポン文書種別を返す.
     * @return コレポン文書種別
     */
    public CorresponType getCorresponType() {
        return corresponType;
    }

    /**
     * コレポン文書種別を設定する.
     * @param corresponType コレポン文書種別
     */
    public void setCorresponType(CorresponType corresponType) {
        this.corresponType = corresponType;
    }

    /**
     * コレポン文書種別タイプを返す.
     * @return コレポン文書種別タイプ
     */
    public String getType() {
        return type;
    }

    /**
     * コレポン文書種別タイプを設定する.
     * @param type コレポン文書種別タイプ
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * コレポン文書種別名を返す.
     * @return コレポン文書種別名
     */
    public String getName() {
        return name;
    }

    /**
     * コレポン文書種別名を設定する.
     * @param name コレポン文書種別名
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * previousConditionを返す.
     * @return the previousCondition
     */
    public AbstractCondition getPreviousCondition() {
        return previousCondition;
    }

    /**
     * previousConditionを設定する.
     * @param previousCondition the previousCondition to set
     */
    public void setPreviousCondition(AbstractCondition previousCondition) {
        this.previousCondition = previousCondition;
    }

    /**
     * 承認フローパターンを返す.
     * @return 承認フローパターン
     */
    public List<WorkflowPattern> getWorkflowPatternList() {
        return workflowPatternList;
    }

    /**
     * 承認フローパターンを返す.
     * @return 承認フローパターン
     */
    public String getWorkflowPatternJson() {
        if (workflowPatternList == null) {
            return "{}";
        }
        Map<Long, WorkflowPattern> map =
                workflowPatternList.stream().collect(
                        Collectors.toMap(WorkflowPattern::getId, UnaryOperator.identity()));
        return JSONUtil.encode(map);
    }

    /**
     * 承認フローパターンを設定する.
     * @param workflowPatternList 承認フローパターン
     */
    public void setWorkflowPatternList(List<WorkflowPattern> workflowPatternList) {
        this.workflowPatternList = workflowPatternList;
    }

    /**
     * 登録、または更新したい承認フローパターンを返す.
     * @return 承認フローパターンID
     */
    public Long getSelectWorkflowPattern() {
        return selectWorkflowPattern;
    }

    /**
     * 登録、または更新したい承認フローパターンを設定する.
     * @param selectWorkflowPattern 承認フローパターンID
     */
    public void setSelectWorkflowPattern(Long selectWorkflowPattern) {
        this.selectWorkflowPattern = selectWorkflowPattern;
    }

    /**
     * 登録、または更新したい承認フローパターンの選択肢を返す.
     * @return 承認フローパターンの選択肢
     */
    public List<SelectItem> getSelectWorkflowPatternList() {
        return selectWorkflowPatternList;
    }

    /**
     * 登録、または更新したい承認フローパターンの選択肢を設定する.
     * @param selectWorkflowPatternList 承認フローパターンの選択肢
     */
    public void setSelectWorkflowPatternList(List<SelectItem> selectWorkflowPatternList) {
        this.selectWorkflowPatternList = selectWorkflowPatternList;
    }

    /**
     * allowフラグを返す.
     * @return allow フラグ
     */
    public boolean isAllow() {
        return allow;
    }

    /**
     * allowフラグを設定する.
     * @param allow フラグ
     */
    public void setAllow(boolean allow) {
        this.allow = allow;
    }

    /**
     * forceフラグを返す.
     * @return forceフラグ
     */
    public boolean isForce() {
        return force;
    }

    /**
     * forceフラグを設定する.
     * @param force フラグ
     */
    public void setForce(boolean force) {
        this.force = force;
    }

    /**
     * 現在のリクエストがNextアクションによって発生した場合はtrue.
     * @return Nextアクションの場合true
     */
    public boolean isNextAction() {
        return isActionInvoked("form:next");
    }

    /**
     * @param projectCorresponTypeId the projectCorresponTypeId to set
     */
    public void setProjectCorresponTypeId(Long projectCorresponTypeId) {
        this.projectCorresponTypeId = projectCorresponTypeId;
    }

    /**
     * @return the projectCorresponTypeId
     */
    public Long getProjectCorresponTypeId() {
        return projectCorresponTypeId;
    }

    /**
     * @param projectId the projectId to set
     */
    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    /**
     * @return the projectId
     */
    public String getProjectId() {
        return projectId;
    }

    /**
     * @param back the back to set
     */
    public void setBack(boolean back) {
        this.back = back;
    }

    /**
     * @return the back
     */
    public boolean isBack() {
        return back;
    }

    /**
     * この CorresponType を project Admin が利用可能かどうかを返す.
     * @return 利用可能な場合は true, それ以外は false
     */
    public boolean getProjectAdminAccessAlow() {
        return isCorresponTypeAvailable(CorresponTypeAdmittee.PROJECT_ADMIN);
    }

    /**
     * この CorresponType を利用可能なユーザー種別を取得する.
     * @return ユーザー種別を示す CorresponTypeAdmittee 列挙子の Set
     */
    public EnumSet<CorresponTypeAdmittee> getAccessControlFlags() {
        return accessControlFlags;
    }

    /**
     * この CorresponType を利用可能なユーザー種別を設定する.
     * @param accessControlFlags ユーザー種別を示す CorresponTypeAdmittee の Set
     */
    public void setAccessControlFlags(
            EnumSet<CorresponTypeAdmittee> accessControlFlags) {
        this.accessControlFlags = accessControlFlags;
    }


    /**
     * この CorresponType を project Admin が利用可能/利用不可能に設定する.
     * @param enable 利用可能にする場合は true, 利用不可能にする場合は false
     */
    public void setProjectAdminAccessAlow(boolean enable) {
        if (enable) {
            addCorresponTypeAdmittee(CorresponTypeAdmittee.PROJECT_ADMIN);
        } else {
            removeCorresponTypeAdmittee(CorresponTypeAdmittee.PROJECT_ADMIN);
        }
    }

    /**
     * この CorresponType を Group Admin が利用可能かどうかを返す.
     * @return 利用可能な場合は true, それ以外は false
     */
    public boolean getGroupAdminAccessAlow() {
        return isCorresponTypeAvailable(CorresponTypeAdmittee.GROUP_ADMIN);
    }

    /**
     * この CorresponType を Group Admin が利用可能/利用不可能に設定する.
     * @param enable 利用可能にする場合は true, 利用不可能にする場合は false
     */
    public void setGroupAdminAccessAlow(boolean enable) {
        if (enable) {
            addCorresponTypeAdmittee(CorresponTypeAdmittee.GROUP_ADMIN);
        } else {
            removeCorresponTypeAdmittee(CorresponTypeAdmittee.GROUP_ADMIN);
        }
    }

    /**
     * この CorresponType を Normal User が利用可能かどうかを返す.
     * @return 利用可能な場合は true, それ以外は false
     */
    public boolean getNormalUserAccessAlow() {
        return isCorresponTypeAvailable(CorresponTypeAdmittee.NORMAL_USER);
    }

    /**
     * この CorresponType を Normal User が利用可能/利用不可能に設定する.
     * @param enable 利用可能にする場合は true, 利用不可能にする場合は false
     */
    public void setNormalUserAccessAlow(boolean enable) {
        if (enable) {
            addCorresponTypeAdmittee(CorresponTypeAdmittee.NORMAL_USER);
        } else {
            removeCorresponTypeAdmittee(CorresponTypeAdmittee.NORMAL_USER);
        }
    }

    /**
     * @return the useAccessControl
     */
    public boolean isUseAccessControl() {
        return useAccessControl;
    }

    /**
     * @param useAccessControl the useAccessControl to set
     */
    public void setUseAccessControl(boolean useAccessControl) {
        this.useAccessControl = useAccessControl;
    }

    /**
     * この CorresponType を引数で指定された種別のユーザーが利用可能かどうかを返す.
     * @param userType ユーザー種別を示す CorresponTypeAdmittee 列挙子
     * @return 利用可能な場合は true, それ以外は false
     */
    private boolean isCorresponTypeAvailable(CorresponTypeAdmittee userType) {
        return accessControlFlags.contains(userType);
    }

    /**
     * この CorresponType を利用可能なユーザー種別に引数で指定されたユーザー種別を追加する.
     * @param userType ユーザー種別を示す CorresponTypeAdmittee 列挙子
     */
    private void addCorresponTypeAdmittee(CorresponTypeAdmittee userType) {
        accessControlFlags.add(userType);
    }

    /**
     * この CorresponType を利用可能なユーザー種別から引数で指定されたユーザー種別を削除する.
     * @param userType ユーザー種別を示す CorresponTypeAdmittee 列挙子
     */
    private void removeCorresponTypeAdmittee(CorresponTypeAdmittee userType) {
        accessControlFlags.remove(userType);
    }


    /**
     * 画面初期化アクション.
     * @author opentone
     */
    static class InitializeAction extends AbstractAction {
        /**
         * serialVersionUID.
         */
        private static final long serialVersionUID = -5705464825282267026L;
        /** アクション発生元ページ. */
        private CorresponTypeEditPage page;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page
         *            ページ
         */
        public InitializeAction(CorresponTypeEditPage page) {
            super(page);
            this.page = page;
        }

        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            page.initialDisplaySuccess = false;
            page.projectId = page.getCurrentProjectId();
            page.previousCondition =
                    page.getPreviousSearchCondition(SearchCorresponTypeCondition.class);
            // 権限チェック
            if (!page.isSystemAdmin() && !page.isProjectAdmin(page.getCurrentProjectId())) {
                throw new ServiceAbortException(
                    ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT);
            }
            if (!page.isBack()) {
                clearCorresponType();
                if (page.id == null) {
                    // 新規登録
                    page.title = NEW;
                } else {
                    // 更新
                    page.title = UPDATE;
                    page.corresponType = page.service.find(page.id);
                }
                page.useAccessControl = isUseAccessControl(page);
            }
            setupCorresponType();
            // 承認フローパターンを取得
            page.workflowPatternList = page.service.searchWorkflowPattern();
            page.selectWorkflowPatternList =
                    page.viewHelper.createSelectItem(page.workflowPatternList, "id", "name");
            page.initialDisplaySuccess = true;
        }

        private void clearCorresponType() {
            page.corresponType = null;
            page.type = null;
            page.name = null;
            page.allow = false;
            page.force = false;
            page.selectWorkflowPattern = null;
            page.accessControlFlags =
                EnumSet.allOf(CorresponTypeAdmittee.class);
        }

        private void setupCorresponType() {
            if (page.corresponType != null) {
                page.type = page.corresponType.getCorresponType();
                page.name = page.corresponType.getName();
                page.allow = page.corresponType.isAllowApproverVisible();
                page.force = page.corresponType.isForceToUseWorkflowRequired();
                page.selectWorkflowPattern = page.corresponType.getWorkflowPattern().getId();
                page.accessControlFlags =
                    CorresponTypeAdmittee.getEnumSetFromDbValue(
                    page.corresponType.getCorresponAccessControlFlags());
            }
        }

        /**
         * 現在のプロジェクトで  CorresponType のアクセス制限機能が有効であるかどうかを返す.
         * 注: projectId が null (System Admin Home) の場合は false を返す.
         * @param editPage Page クラスのインスタンス
         * @return CorresponType のアクセス制限機能が有効であれば true, それ以外は false
         * @throws ServiceAbortException サービスの処理が失敗したことを示す例外
         */
        private boolean isUseAccessControl(CorresponTypeEditPage editPage)
            throws ServiceAbortException {

            boolean result = false;
            String pjId = editPage.projectId;
            if (pjId != null) {
                ProjectCustomSetting pcs =
                    editPage.projectCustomSettingService.find(pjId);
                result = pcs.isUseCorresponAccessControl();
            }
            return result;
        }
    }

    /**
     * 入力値検証アクション.
     * @author opentone
     */
    static class ValidateAction extends AbstractAction {
        /**
         * serialVersionUID.
         */
        private static final long serialVersionUID = 6395778488906970075L;
        /** アクション発生元ページ. */
        private CorresponTypeEditPage page;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page
         *            ページ
         */
        public ValidateAction(CorresponTypeEditPage page) {
            super(page);
            this.page = page;
        }

        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            setCorresponTypeInfo();
            if (page.service.validate(page.corresponType)) {
                page.setNextPageMessage(ApplicationMessageCode.CONTENT_WILL_BE_SAVED);
            }
        }

        /**
         * コレポン文書種別を作成する.
         */
        private void setCorresponTypeInfo() {
            if (page.corresponType == null) {
                page.corresponType = new CorresponType();
            }
            page.corresponType.setCorresponType(page.type);
            page.corresponType.setName(page.name);
            page.corresponType.setProjectId(page.getCurrentProjectId());

            for (WorkflowPattern wp : page.workflowPatternList) {
                if (wp.getId().equals(page.getSelectWorkflowPattern())) {
                    page.corresponType.setWorkflowPattern(wp);
                    break;
                }
            }
            if (page.allow) {
                page.corresponType.setAllowApproverToBrowse(AllowApproverToBrowse.VISIBLE);
            } else {
                page.corresponType.setAllowApproverToBrowse(AllowApproverToBrowse.INVISIBLE);
            }
            if (page.force) {
                page.corresponType.setForceToUseWorkflow(ForceToUseWorkflow.REQUIRED);
            } else {
                page.corresponType.setForceToUseWorkflow(ForceToUseWorkflow.OPTIONAL);
            }

            page.corresponType.setCorresponAccessControlFlags(
                CorresponTypeAdmittee.getIntValueFromEnumSet(page.accessControlFlags));
        }
    }

}
