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

import javax.annotation.ManagedBean;
import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;

import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.web.extension.jsf.annotation.Initialize;
import jp.co.opentone.bsol.framework.web.extension.jsf.annotation.Transfer;
import jp.co.opentone.bsol.linkbinder.action.AbstractAction;
import jp.co.opentone.bsol.linkbinder.dto.Site;
import jp.co.opentone.bsol.linkbinder.dto.condition.AbstractCondition;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchSiteCondition;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.service.admin.SiteService;
import jp.co.opentone.bsol.linkbinder.view.AbstractPage;

/**
 * 拠点情報入力画面.
 * @author
 */
@ManagedBean
@Scope("view")
public class SiteConfirmationPage extends AbstractPage {
    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 8186671266016330700L;

    /**
     * 拠点情報サービス.
     */
    @Resource
    private SiteService service;

    /**
     * 初期画面表示判定値.
     */
    @Transfer
    private boolean initialDisplaySuccess = false;

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
     * 拠点情報.
     * <p>
     * 新規登録ならばnull
     * </p>
     */
    @Transfer
    private Site site;

    /**
     * 拠点コード.
     */
    @Transfer
    private String code;

    /**
     * 拠点名.
     */
    @Transfer
    private String name;

    /**
     * 前画面に、この画面から戻ったことを通知するフラグ.
     */
    @Transfer
    private boolean back;
    /**
     * 検索条件.
     * 起動元画面からのFlashパラメータを設定する.
     */
    @Transfer
    private AbstractCondition previousCondition;

    /**
     * 空のインスタンスを生成する.
     */
    public SiteConfirmationPage() {
    }

    /**
     * 画面を初期化する.
     */
    @Initialize
    public void initialize() {
        handler.handleAction(new InitializeAction(this));
    }

    /**
     * 拠点情報画面へ遷移.
     * @return 遷移先画面ID
     */
    public String save() {
        if (handler.handleAction(new SaveAction(this))) {
            setNextSearchCondition(previousCondition);
            setTransferNext(true);
            return toUrl(String.format("site?id=%s", id));
        }
        return null;
    }

    /**
     * 拠点情報入力画面へ遷移.
     * @return 遷移先画面ID
     */
    public String back() {
        setNextSearchCondition(previousCondition);
        back = true;
        setTransferNext(true);
        return toUrl("siteEdit");
    }

    /**
     * 表示タイトルを取得する.
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
     * 拠点IDを取得する.
     * @return 拠点ID
     */
    public Long getId() {
        return id;
    }

    /**
     * 拠点IDを設定する.
     * @param id 拠点ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 拠点コードを返却する.
     * @return 拠点コード
     */
    public String getCode() {
        return code;
    }

    /**
     * 拠点コードを設定する.
     * @param code 拠点コード
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * 拠点情報を設定する.
     * @param site 拠点情報
     */
    public void setSite(Site site) {
        this.site = site;
    }

    /**
     * 拠点情報を取得する.
     * @return 拠点情報
     */
    public Site getSite() {
        return site;
    }

    /**
     * 拠点名を取得する.
     * @return 拠点名
     */
    public String getName() {
        return name;
    }

    /**
     * 拠点名を設定する.
     * @param name 拠点名
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 初期画面表示判定を取得する.
     * @return 初期画面表示判定
     */
    public boolean getInitialDisplaySuccess() {
        return initialDisplaySuccess;
    }

    /**
     * 初期画面表示判定を設定する.
     * @param initialDisplaySuccess 初期画面表示判定
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
     * 画面初期化アクション.
     * @author opentone
     */
    static class InitializeAction extends AbstractAction {
        /**
         * serialVersionUID.
         */
        private static final long serialVersionUID = 7121307925815826849L;
        /** アクション発生元ページ. */
        private SiteConfirmationPage page;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page
         *            ページ
         */
        public InitializeAction(SiteConfirmationPage page) {
            super(page);
            this.page = page;
        }

        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            page.previousCondition = page.getPreviousSearchCondition(SearchSiteCondition.class);
            initializeCheck();
            // 初期画面表示成功
            page.initialDisplaySuccess = true;
        }

        /**
         * 初期表示時のチェック処理を行う.
         * @throws ServiceAbortException 初期表示失敗
         */
        private void initializeCheck() throws ServiceAbortException {
            // 権限チェック
            page.checkProjectSelected();
            if (!page.isSystemAdmin() && !page.isProjectAdmin(page.getCurrentProjectId())) {
                throw new ServiceAbortException(
                    ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT);
            }
            // 不正アクセス防止
            if (page.site == null) {
                throw new ServiceAbortException(
                        ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT);
            }
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
        private static final long serialVersionUID = 2324791120055663360L;
        /** アクション発生元ページ. */
        private SiteConfirmationPage page;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page
         *            ページ
         */
        public SaveAction(SiteConfirmationPage page) {
            super(page);
            this.page = page;
        }

        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            page.id = page.service.save(page.site);

            page.setNextPageMessage(ApplicationMessageCode.SITE_SAVED);
        }
    }
}
