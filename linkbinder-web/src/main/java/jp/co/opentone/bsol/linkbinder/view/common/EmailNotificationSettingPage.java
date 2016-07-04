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

import java.util.List;

import javax.annotation.ManagedBean;
import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;

import jp.co.opentone.bsol.framework.core.message.MessageCode;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.web.extension.jsf.annotation.Initialize;
import jp.co.opentone.bsol.framework.web.extension.jsf.annotation.Transfer;
import jp.co.opentone.bsol.linkbinder.action.AbstractAction;
import jp.co.opentone.bsol.linkbinder.dto.EmailNoticeRecvSettingResult;
import jp.co.opentone.bsol.linkbinder.dto.UserProfile;
import jp.co.opentone.bsol.linkbinder.dto.code.EmailNoticeReceivable;
import jp.co.opentone.bsol.linkbinder.dto.condition.AbstractCondition;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchUserCondition;
import jp.co.opentone.bsol.linkbinder.dto.notice.EmailNoticeRecvSetting;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.service.notice.EmailNoticeService;
import jp.co.opentone.bsol.linkbinder.view.AbstractPage;

/**
 * メール通知受信設定画面.
 *
 * @author opentone
 */
@ManagedBean
@Scope("view")
public class EmailNotificationSettingPage extends AbstractPage {
    /**
     *
     */
    private static final long serialVersionUID = 812291022632407105L;

    /**
     * E-mail通知に関するサービスを提供する.
     */
    @Resource
    private EmailNoticeService emailNoticeService;

    /**
     * ユーザーID. 起動元画面から自動的にセットされる.
     */
    @Transfer
    private String id;

    /**
     * 遷移元ページ. 起動元画面から自動的にセットされる.
     */
    @Transfer
    private String backPage;

    /**
     * ユーザー一覧検索条件. 起動元画面がユーザー一覧の場合、セットされる.
     */
    @Transfer
    private AbstractCondition condition;

    /**
     * メール通知受信設定情報List(画面一覧を表示する).
     */
    @Transfer
    private List<EmailNoticeRecvSetting> noticeSetting;

    /**
     * 排他制御用のUser profile.
     */
    @Transfer
    private UserProfile userProfile;

    /**
     * メール受信設定を設定できる.
     */
    @Transfer
    private boolean editEmailNotificationSetting;

    /**
     * 空のインスタンスを生成する.
     */
    public EmailNotificationSettingPage() {
    }

    /**
     * idを取得します.
     *
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * idを設定します.
     *
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * backPageを取得します.
     *
     * @return the backPage
     */
    public String getBackPage() {
        return backPage;
    }

    /**
     * backPageを設定します.
     *
     * @param backPage the backPage to set
     */
    public void setBackPage(String backPage) {
        this.backPage = backPage;
    }

    /**
     * conditionを取得します.
     *
     * @return the condition
     */
    public AbstractCondition getCondition() {
        return condition;
    }

    /**
     * conditionを設定します.
     *
     * @param condition the condition to set
     */
    public void setCondition(AbstractCondition condition) {
        this.condition = condition;
    }

    /**
     * 画面を初期化する.
     * <p>
     * 他画面から遷移した時に起動される. ページの初期化に必要な情報を準備する.
     * </p>
     */
    @Initialize
    public void initialize() {
        if (handler.handleAction(new InitializeAction(this))) {
            search();
        }
    }

    /**
     * 引数で渡された検索条件で検索する.
     *
     * @return null
     */
    public String search() {
        handler.handleAction(new SearchAction(this));
        return null;
    }

    /**
     * メール通知受信設定を保存する.
     *
     * @return null
     */
    public String save() {
        if (handler.handleAction(new SaveAction(this))) {
            search();
        }
        return null;
    }

    /**
     * 遷移元画面に遷移する.
     *
     * @return 遷移元画面
     */
    public String back() {
        if (StringUtils.isNotEmpty(getBackPage())) {
            StringBuilder param = new StringBuilder("userSettings?id=%s&backPage=");
            param.append(getBackPage());
            setNextSearchCondition(condition);
            return toUrl(String.format(param.toString(), getId()));
        } else {
            return toUrl(String.format("userSettings?id=%s", getId()), super.isProjectSelected());
        }
    }

    /**
     * 画面初期化アクション.
     *
     * @author opentone
     */
    static class InitializeAction extends AbstractAction {
        /**
         *
         */
        private static final long serialVersionUID = 7017505832524813280L;

        /** アクション発生元ページ. */
        private EmailNotificationSettingPage page;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         *
         * @param page ページ
         */
        public InitializeAction(EmailNotificationSettingPage page) {
            super(page);
            this.page = page;
        }

        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        @Override
        public void execute() throws ServiceAbortException {
            page.editEmailNotificationSetting = false;

            page.condition = page.getPreviousSearchCondition(SearchUserCondition.class);
            // 起動元からの必須パラメーター
            if (StringUtils.isEmpty(page.id)) {
                throw new ServiceAbortException("ID is not specified.",
                        MessageCode.E_INVALID_PARAMETER);
            }

            if (page.id.equals(page.getCurrentUser().getEmpNo())
                    || page.isSystemAdmin()) {
                page.editEmailNotificationSetting = true;
            } else {
                page.editEmailNotificationSetting = false;
            }
        }
    }

    /**
     * 保存アクション.
     *
     * @author opentone
     */
    static class SaveAction extends AbstractAction {
        /**
         * serialVersionUID.
         */
        private static final long serialVersionUID = 458418676878975254L;
        /** アクション発生元ページ. */
        private EmailNotificationSettingPage page;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         *
         * @param page ページ
         */
        public SaveAction(EmailNotificationSettingPage page) {
            super(page);
            this.page = page;
        }

        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {

            // 権限チェック
            if (!page.isSystemAdmin() && !page.getCurrentUser().getEmpNo().equals(page.id)) {
                throw new ServiceAbortException(
                        ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT);
            }
            page.emailNoticeService.save(page.getNoticeSetting(), page.getUserProfile());

            page.setNextSearchCondition(page.condition);

            page.setPageMessage(ApplicationMessageCode.SAVE_SUCCESSFUL);
        }
    }

    /**
     * 検索アクション.
     *
     * @author opentone
     */
    static class SearchAction extends AbstractAction {
        /**
         * serialVersionUID.
         */
        private static final long serialVersionUID = 322493185721558218L;

        /** アクション発生元ページ. */
        private EmailNotificationSettingPage page;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         *
         * @param page ページ
         */
        public SearchAction(EmailNotificationSettingPage page) {
            super(page);
            this.page = page;
        }

        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            if (page.condition == null) {
                page.condition = new SearchUserCondition();
            }

            search();
        }

        /**
         * 検索を行う.
         *
         * @throws ServiceAbortException 検索時にエラー
         */
        private void search() throws ServiceAbortException {
            EmailNoticeRecvSetting condition = new EmailNoticeRecvSetting();
            condition.setEmpNo(page.id);
            String empNo = page.id;
            EmailNoticeRecvSettingResult result = page.emailNoticeService
                    .findEmailNoticeRecvSetting(empNo);

            // 権限チェック
            // システム管理者もしくは、ログインユーザー＝設定対象のユーザーであること
            if (!page.isSystemAdmin() && !page.getCurrentUser().getEmpNo().equals(empNo)) {
                throw new ServiceAbortException(
                        ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT);
            }

            for (EmailNoticeRecvSetting setting : result.getEmailNoticeRecvSettingList()) {
                EmailNoticeReceivable receiveWorkflow = setting.getReceiveWorkflow();
                if (null == receiveWorkflow
                        || EmailNoticeReceivable.YES == receiveWorkflow) {
                    setting.setReceiveWorkflowChk(EmailNoticeReceivable.YES.getFlag());
                }

                EmailNoticeReceivable recvDistributionAttention = setting
                        .getRecvDistributionAttention();
                if (null == recvDistributionAttention
                        || EmailNoticeReceivable.YES == recvDistributionAttention) {
                    setting.setRecvDistributionAttentionChk(EmailNoticeReceivable.YES.getFlag());
                }

                EmailNoticeReceivable recvDistributionCc = setting.getRecvDistributionCc();
                if (null == recvDistributionCc
                        || EmailNoticeReceivable.YES == recvDistributionCc) {
                    setting.setRecvDistributionCcChk(EmailNoticeReceivable.YES.getFlag());
                }
            }
            page.noticeSetting = result.getEmailNoticeRecvSettingList();
            page.userProfile = result.getUserProfile();
        }
    }

    /**
     * @return the noticeSetting
     */
    public List<EmailNoticeRecvSetting> getNoticeSetting() {
        return noticeSetting;
    }

    /**
     * @param noticeSetting the noticeSetting to set
     */
    public void setNoticeSetting(List<EmailNoticeRecvSetting> noticeSetting) {
        this.noticeSetting = noticeSetting;
    }

    /**
     * @return the userProfile
     */
    public UserProfile getUserProfile() {
        return userProfile;
    }

    /**
     * @param userProfile the userProfile to set
     */
    public void setUserProfile(UserProfile userProfile) {
        this.userProfile = userProfile;
    }

    /**
     * @return the editEmailNotificationSetting
     */
    public boolean isEditEmailNotificationSetting() {
        return editEmailNotificationSetting;
    }

    /**
     * @param editEmailNotificationSetting the editEmailNotificationSetting to set
     */
    public void setEditEmailNotificationSetting(boolean editEmailNotificationSetting) {
        this.editEmailNotificationSetting = editEmailNotificationSetting;
    }
}
