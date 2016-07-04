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

import org.hibernate.validator.constraints.Length;
import org.springframework.context.annotation.Scope;

import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.core.validation.constraints.Alphanumeric;
import jp.co.opentone.bsol.framework.core.validation.constraints.Required;
import jp.co.opentone.bsol.framework.web.extension.jsf.annotation.Initialize;
import jp.co.opentone.bsol.framework.web.extension.jsf.annotation.Transfer;
import jp.co.opentone.bsol.linkbinder.action.AbstractAction;
import jp.co.opentone.bsol.linkbinder.dto.Site;
import jp.co.opentone.bsol.linkbinder.dto.condition.AbstractCondition;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchSiteCondition;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.service.admin.SiteService;
import jp.co.opentone.bsol.linkbinder.validation.groups.ValidationGroupBuilder;
import jp.co.opentone.bsol.linkbinder.view.AbstractPage;

/**
 * 拠点情報入力画面.
 * @author opentone
 * <p>
 * $Date: 2011-06-22 17:37:10 +0900 (水, 22  6 2011) $
 * $Rev: 4225 $
 * $Author: nemoto $
 */
@ManagedBean
@Scope("view")
public class SiteEditPage extends AbstractPage {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -8057609824840135445L;

    /**
     * 表題（新規登録）ラベル.
     */
    private static final String NEW = "拠点新規登録";

    /**
     * 表題（更新）ラベル.
     */
    private static final String UPDATE = "拠点更新";

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
     * 起動元画面からのクエリパラメータが自動設定される. 新規登録ならばnull
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
//    @SkipValidation("#{!siteEditPage.nextAction}")
    @Transfer
    @Required
    @Alphanumeric
    //CHECKSTYLE:OFF
    @Length(max = 10)
    //CHECKSTYLE:ON
    private String code;

    /**
     * 拠点名.
     */
//    @SkipValidation("#{!siteEditPage.nextAction}")
    @Transfer
    @Required
    //CHECKSTYLE:OFF
    @Length(max = 100)
    //CHECKSTYLE:ON
    private String name;

    /**
     * 確認画面から戻ってきた場合はtrue.
     */
    private boolean back;

    /**
     * 検索条件. 起動元画面からのFlashパラメータを設定する.
     */
    @Transfer
    private AbstractCondition previousCondition;

    /**
     * 空のインスタンスを生成する.
     */
    public SiteEditPage() {
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
     * 他画面から遷移した時に起動される. クエリパラメータで指定されたIDに対応する、コレポン文書を表示する.
     * </p>
     */
    @Initialize
    public void initialize() {
        handler.handleAction(new InitializeAction(this));
    }

    /**
     * 拠点情報入力確認画面へ遷移 （validateチェック）.
     * @return 遷移先画面ID
     */
    public String next() {
        if (handler.handleAction(new ValidateAction(this))) {
            setNextSearchCondition(previousCondition);
            setTransferNext(true);
            return toUrl("siteConfirmation");
        }
        return null;
    }

    /**
     * 一覧画面へ遷移する.
     * @return 一覧画面
     */
    public String back() {
        setNextSearchCondition(previousCondition);
        return toUrl("siteIndex");
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
     * IDを返す.
     * @return ID
     */
    public Long getId() {
        return id;
    }

    /**
     * IDを設定する.
     * @param id ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 拠点コードを返す.
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
     * 拠点情報を返す.
     * @return 拠点情報
     */
    public Site getSite() {
        return site;
    }

    /**
     * 拠点名を返す.
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
     * 初期画面表示判定値を返す.
     * @return 初期画面表示判定値
     */
    public boolean getInitialDisplaySuccess() {
        return initialDisplaySuccess;
    }

    /**
     * 初期画面表示判定値を設定する.
     * @param initialDisplaySuccess 初期画面表示判定値
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
     * 現在のリクエストがnextアクションによって発生した場合はtrue.
     * @return nextアクションの場合true
     */
    public boolean isNextAction() {
        return isActionInvoked("form:next");
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
        private static final long serialVersionUID = 6929922647085171352L;
        /** アクション発生元ページ. */
        private SiteEditPage page;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page ページ
         */
        public InitializeAction(SiteEditPage page) {
            super(page);
            this.page = page;
        }

        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            page.initialDisplaySuccess = false;
            page.previousCondition = page.getPreviousSearchCondition(SearchSiteCondition.class);
            // 権限チェック
            page.checkProjectSelected();
            if (!page.isSystemAdmin() && !page.isProjectAdmin(page.getCurrentProjectId())) {
                throw new ServiceAbortException(
                    ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT);
            }

            if (!page.isBack()) {
                clearSite();
                if (page.id == null) {
                    // 新規登録
                    page.title = NEW;
                } else {
                    // 更新
                    page.title = UPDATE;
                    page.site = page.service.find(page.id);
                    page.code = page.site.getSiteCd();
                    page.name = page.site.getName();
                }
            }
            // 初期画面表示成功
            page.initialDisplaySuccess = true;
        }

        private void clearSite() {
            page.site = null;
            page.code = null;
            page.name = null;
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
        private static final long serialVersionUID = 8891138830142460118L;
        /** アクション発生元ページ. */
        private SiteEditPage page;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page ページ
         */
        public ValidateAction(SiteEditPage page) {
            super(page);
            this.page = page;
        }

        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            setSiteInfo();
            page.service.validate(page.site);
            page.setNextPageMessage(ApplicationMessageCode.CONTENT_WILL_BE_SAVED);
        }

        /**
         * 拠点情報を作成する.
         */
        private void setSiteInfo() {
            if (page.site == null) {
                page.site = new Site();
            }
            page.site.setSiteCd(page.code);
            page.site.setName(page.name);
            page.site.setProjectId(page.getCurrentProjectId());
        }
    }
}
