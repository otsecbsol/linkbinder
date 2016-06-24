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

import java.util.EnumSet;

import javax.annotation.ManagedBean;
import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;

import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.web.extension.jsf.annotation.Initialize;
import jp.co.opentone.bsol.framework.web.extension.jsf.annotation.Transfer;
import jp.co.opentone.bsol.linkbinder.action.AbstractAction;
import jp.co.opentone.bsol.linkbinder.dto.CorresponType;
import jp.co.opentone.bsol.linkbinder.dto.code.CorresponTypeAdmittee;
import jp.co.opentone.bsol.linkbinder.dto.condition.AbstractCondition;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponTypeCondition;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.service.admin.CorresponTypeService;
import jp.co.opentone.bsol.linkbinder.view.AbstractPage;

/**
 * コレポン文書種別確認画面.
 * @author opentone
 */
@ManagedBean
@Scope("view")
public class CorresponTypeConfirmationPage extends AbstractPage {

    /**
     * serialVersionUID.
     */
    private static final long serialVersionUID = -5984033041848584360L;

    /**
     * チェックボックスにチェックしている時に表示する.
     */
    private static final String LABEL_CHECKED = "○";

    /**
     * チェックボックスにチェックしていない時に表示する.
     */
    private static final String LABEL_UNCHECKED = "×";

    /**
     * コレポン文書種別サービス.
     */
    @Resource
    private CorresponTypeService service;

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
     * 新規登録ならばnull
     * </p>
     */
    @Transfer
    private Long id;

    /**
     * projectCorresponTypeId.
     * <p>
     * 新規登録ならばnull
     * </p>
     */
    @Transfer
    private Long projectCorresponTypeId;


    /**
     * コレポン文書種別.
     */
    @Transfer
    private CorresponType corresponType;

    /**
     * コレポン文書種別タイプ.
     */
    @Transfer
    private String type;

    /**
     * コレポン文書種別名.
     */
    @Transfer
    private String name;

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
     * 前画面に、この画面から戻ったことを通知するフラグ.
     */
    @Transfer
    private boolean back;

    /**
     * 検索条件. 起動元画面からのFlashパラメータを設定する.
     */
    @Transfer
    private AbstractCondition previousCondition;

    /**
     * アクセスコントロールを使用するかどうかを示すフラグ.
     */
    @Transfer
    private boolean useAccessControl;

    /**
     * この CorresponType を利用可能なユーザー種別.
     */
    @Transfer
    private EnumSet<CorresponTypeAdmittee> accessControlFlags;


    /**
     * 空のインスタンスを生成する.
     */
    public CorresponTypeConfirmationPage() {
        // デフォルトはすべてのユーザーが利用可能
        accessControlFlags = EnumSet.allOf(CorresponTypeAdmittee.class);
    }

    /**
     * 画面を初期化する.
     */
    @Initialize
    public void initialize() {
        handler.handleAction(new InitializeAction(this));
    }

    /**
     * コレポン文書種別表示画面へ遷移.
     * @return 遷移先画面ID
     */
    public String save() {
        if (handler.handleAction(new SaveAction(this))) {
            setNextSearchCondition(previousCondition);
            setTransferNext(true);
            return toUrl(String.format("corresponType?id=%s", id), isLoginProject());
        }
        return null;
    }

    /**
     * コレポン文書種別入力画面へ遷移.
     * @return 遷移先画面ID
     */
    public String back() {
        setNextSearchCondition(previousCondition);
        back = true;
        setTransferNext(true);
        return toUrl("corresponTypeEdit", isLoginProject());
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
     * @param initialDisplaySuccess
     *            the initialDisplaySuccess to set
     */
    public void setInitialDisplaySuccess(boolean initialDisplaySuccess) {
        this.initialDisplaySuccess = initialDisplaySuccess;
    }

    /**
     * titleを取得します.
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * titleを設定します.
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
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
     * コレポン文書種別を取得します.
     * @return the corresponType
     */
    public CorresponType getCorresponType() {
        return corresponType;
    }

    /**
     * コレポン文書種別を設定します.
     * @param corresponType the corresponType to set
     */
    public void setCorresponType(CorresponType corresponType) {
        this.corresponType = corresponType;
    }

    /**
     * コレポン文書種別タイプを取得します.
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * コレポン文書種別タイプを設定します.
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * nameを取得します.
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * nameを設定します.
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * previousConditionを取得します.
     * @return the previousCondition
     */
    public AbstractCondition getPreviousCondition() {
        return previousCondition;
    }

    /**
     * previousConditionを設定します.
     * @param previousCondition the previousCondition to set
     */
    public void setPreviousCondition(AbstractCondition previousCondition) {
        this.previousCondition = previousCondition;
    }

    /**
     * allowフラグを設定する.
     * @param allow フラグ
     */
    public void setAllow(boolean allow) {
        this.allow = allow;
    }

    /**
     * allowフラグ.
     * @return allowフラグ
     */
    public boolean isAllow() {
        return allow;
    }

    /**
     * forceフラグ.
     * @return フラグ
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
     * この CorresponType を project Admin が利用可能かどうかを返す.
     * @return 利用可能な場合は true, それ以外は false
     */
    public boolean isProjectAdminAccessAlow() {
        return isCorresponTypeAvailable(CorresponTypeAdmittee.PROJECT_ADMIN);
    }

    /**
     * この CorresponType を Group Admin が利用可能かどうかを返す.
     * @return 利用可能な場合は true, それ以外は false
     */
    public boolean isGroupAdminAccessAlow() {
        return isCorresponTypeAvailable(CorresponTypeAdmittee.GROUP_ADMIN);
    }

    /**
     * この CorresponType を Normal User が利用可能かどうかを返す.
     * @return 利用可能な場合は true, それ以外は false
     */
    public boolean isNormalUserAccessAlow() {
        return isCorresponTypeAvailable(CorresponTypeAdmittee.NORMAL_USER);
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
     * この CorresponType を引数で指定された種別のユーザーが利用可能かどうかに応じて "Yes" /
     * "No" を返す.
     * @param userType ユーザー種別を示す CorresponTypeAdmittee 列挙子
     * @return 利用可能な場合は  "Yes", それ以外は  "No"
     */
    private String getAccessAlowCheckLabel(CorresponTypeAdmittee userType) {
        return isCorresponTypeAvailable(userType)
                   ? LABEL_CHECKED : LABEL_UNCHECKED;
    }


    /**
     * Allow Approve to browseを有効にするか判定し結果を返す.
     * @return 判定結果
     */
    public String getAllowCheck() {
        if (allow) {
            return LABEL_CHECKED;
        } else {
            return LABEL_UNCHECKED;
        }
    }

    /**
     * Force to use workflowを有効にするか判定し結果を返す.
     * @return 判定結果
     */
    public String getForceCheck() {
        if (force) {
            return LABEL_CHECKED;
        } else {
            return LABEL_UNCHECKED;
        }
    }

    /**
     * Project Adminを有効にするか判定し結果を返す.
     * @return 判定結果
     */
    public String getProjectAdminCheck() {
        return getAccessAlowCheckLabel(CorresponTypeAdmittee.PROJECT_ADMIN);
    }

    /**
     * Group Adminを有効にするか判定し結果を返す.
     * @return 判定結果
     */
    public String getGroupAdminCheck() {
        return getAccessAlowCheckLabel(CorresponTypeAdmittee.GROUP_ADMIN);
    }

    /**
     * Normal Userを有効にするか判定し結果を返す.
     * @return 判定結果
     */
    public String getNormalUserCheck() {
        return getAccessAlowCheckLabel(CorresponTypeAdmittee.NORMAL_USER);
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
     * 画面初期化アクション.
     * @author opentone
     */
    static class InitializeAction extends AbstractAction {
        /**
         * serialVersionUID.
         */
        private static final long serialVersionUID = -5277774075244658378L;
        /** アクション発生元ページ. */
        private CorresponTypeConfirmationPage page;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page
         *            ページ
         */
        public InitializeAction(CorresponTypeConfirmationPage page) {
            super(page);
            this.page = page;
        }

        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            page.projectId = page.getCurrentProjectId();
            page.previousCondition =
                    page.getPreviousSearchCondition(SearchCorresponTypeCondition.class);
            // 権限チェック
            if (!page.isSystemAdmin() && !page.isProjectAdmin(page.getCurrentProjectId())) {
                throw new ServiceAbortException(
                    ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT);
            }
            // 不正アクセス防止
            if (page.getType() == null || page.getName() == null) {
                throw new ServiceAbortException(
                        ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT);
            }
            // 初期画面表示成功
            page.initialDisplaySuccess = true;
        }
    }

    /**
     * 保存アクション.
     * @author opentone
     */
    static class SaveAction extends AbstractAction {
        /**
         * serialVersionUID.
         */
        private static final long serialVersionUID = -6016862170454539667L;
        /** アクション発生元ページ. */
        private CorresponTypeConfirmationPage page;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page
         *            ページ
         */
        public SaveAction(CorresponTypeConfirmationPage page) {
            super(page);
            this.page = page;
        }

        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            page.id = page.service.save(page.corresponType);

            page.setNextPageMessage(ApplicationMessageCode.CORRESPON_TYPE_SAVED);
        }
    }
}
