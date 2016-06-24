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

import jp.co.opentone.bsol.framework.core.message.MessageCode;
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
 * コレポン文書種別表示画面.
 * @author opentone
 */
@ManagedBean
@Scope("view")
public class CorresponTypePage extends AbstractPage {

    /**
     * serialVersionUID.
     */
    private static final long serialVersionUID = 5469038668130206154L;

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
     * プロジェクトID. SystemAdminの場合はnull.
     */
    @Transfer
    private String projectId;

    /**
     * ID.
     * <p>
     * 起動元画面からのクエリパラメータが自動設定される.
     * </p>
     */
    @Transfer
    private Long id;

    /**
     * コレポン文書種別.
     */
    @Transfer
    private CorresponType corresponType;

    /**
     * 検索条件. 起動元画面からのFlashパラメータを設定する.
     */
    @Transfer
    private AbstractCondition previousCondition;

    /**
     * useAccessControlフラグ.
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
    public CorresponTypePage() {
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
     * コレポン文書種別一覧画面に遷移する.
     * @return コレポン文書種別一覧画面
     */
    public String goIndex() {
        setNextSearchCondition(previousCondition);
        return toUrl("corresponTypeIndex", isLoginProject());
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
     * @param id
     *            the id to set
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
     * @param corresponType
     *            the corresponType to set
     */
    public void setCorresponType(CorresponType corresponType) {
        this.corresponType = corresponType;
    }

    /**
     * previousConditionを設定します.
     * @param previousCondition
     *            the previousCondition to set
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
     * Allow Approve to browseを有効にするか判定し結果を返す.
     * @return 判定結果
     */
    public String getAllowCheck() {
        if (corresponType != null
                && corresponType.isAllowApproverVisible()) {
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
        if (corresponType != null
                && corresponType.isForceToUseWorkflowRequired()) {
            return LABEL_CHECKED;
        } else {
            return LABEL_UNCHECKED;
        }
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
     * 初期化アクション.
     * @author opentone
     */
    static class InitializeAction extends AbstractAction {
        /**
         * serialVersionUID.
         */
        private static final long serialVersionUID = 5985344847763505312L;
        /** アクション発生元ページ. */
        private CorresponTypePage page;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page
         *            ページ
         */
        public InitializeAction(CorresponTypePage page) {
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
            // このページの起動元からコレポン文書種別のIDが指定されていなければならない
            if (page.id == null) {
                throw new ServiceAbortException(
                    "ID is not specified.", MessageCode.E_INVALID_PARAMETER);
            }

            // 権限チェック
            if (!page.isSystemAdmin() && !page.isProjectAdmin(page.getCurrentProjectId())) {
                throw new ServiceAbortException(
                    ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT);
            }

            page.corresponType = page.service.find(page.id);
        }
    }
}
