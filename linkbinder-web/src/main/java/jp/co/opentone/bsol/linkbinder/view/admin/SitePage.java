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

import jp.co.opentone.bsol.framework.core.message.MessageCode;
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
 * @author opentone
 */
@ManagedBean
@Scope("view")
public class SitePage extends AbstractPage {
    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 8710449544916087169L;

    /**
     * 拠点情報サービス.
     */
    @Resource
    private SiteService service;

    /**
     * ID.
     * <p>
     * 起動元画面からのクエリパラメータが自動設定される.
     * </p>
     */
    @Transfer
    private Long id;

    /**
     * 拠点情報.
     */
    @Transfer
    private Site site;

    /**
     * 検索条件.
     * 起動元画面からのFlashパラメータを設定する.
     */
    @Transfer
    private AbstractCondition previousCondition;

    /**
     * 空のインスタンスを生成する.
     */
    public SitePage() {
    }

    /**
     * 画面を初期化する.
     */
    @Initialize
    public void initialize() {
        handler.handleAction(new InitializeAction(this));
    }

    /**
     * 拠点一覧画面に遷移する.
     * @return 拠点一覧画面
     */
    public String goIndex() {
        setNextSearchCondition(previousCondition);
        return toUrl("siteIndex");
    }

    /**
     * 活動単位一覧画面に遷移する.
     * @return 活動単位一覧画面
     */
    public String goGroupIndex() {
        setNextSearchCondition(previousCondition);
        return toUrl(String.format("groupIndex?id=%s&backPage=site", id));
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
     * 保存アクション.
     * @author opentone
     */
    static class InitializeAction extends AbstractAction {
        /**
         * serialVersionUID.
         */
        private static final long serialVersionUID = 3974477637197466269L;
        /** アクション発生元ページ. */
        private SitePage page;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page
         *            ページ
         */
        public InitializeAction(SitePage page) {
            super(page);
            this.page = page;
        }

        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            page.previousCondition = page.getPreviousSearchCondition(SearchSiteCondition.class);
            page.checkProjectSelected();
            // 権限チェック
            if (!page.isSystemAdmin() && !page.isProjectAdmin(page.getCurrentProjectId())) {
                throw new ServiceAbortException(
                    ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT);
            }

            // このページの起動元から拠点情報のIDが指定されていなければならない
            if (page.id == null) {
                throw new ServiceAbortException(
                    "ID is not specified.", MessageCode.E_INVALID_PARAMETER);
            }

            page.site = page.service.find(page.id);
        }
    }
}
