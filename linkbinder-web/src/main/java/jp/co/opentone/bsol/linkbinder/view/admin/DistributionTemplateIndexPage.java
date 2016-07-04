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

import java.util.List;

import javax.annotation.ManagedBean;
import javax.annotation.Resource;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import org.springframework.context.annotation.Scope;

import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.web.extension.jsf.annotation.Initialize;
import jp.co.opentone.bsol.framework.web.extension.jsf.annotation.Transfer;
import jp.co.opentone.bsol.linkbinder.action.AbstractAction;
import jp.co.opentone.bsol.linkbinder.dto.DistTemplateHeader;
import jp.co.opentone.bsol.linkbinder.dto.DistTemplateHeaderDelete;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.service.admin.DistributionTemplateService;
import jp.co.opentone.bsol.linkbinder.view.AbstractPage;

/**
 * Distribution Template一覧Page.
 * @author opentone
 */
@ManagedBean
@Scope("view")
public class DistributionTemplateIndexPage extends AbstractPage {

    /**
     * serialVersionUID.
     */
    private static final long serialVersionUID = 7292204405466651080L;

    /**
     * 宛先テンプレートサービス.
     */
    @Resource
    private DistributionTemplateService service;

    /**
     * 初期画面表示判定値.
     */
    @Transfer
    private boolean initialDisplaySuccess = false;

    /**
     * 検索結果.
     */
    private List<DistTemplateHeader> list;

    /**
     * データのDataModel.
     */
    private DataModel<?> dataModel;

    /**
     * 削除フラグ.
     */
    private boolean deleteFlg = false;

    /**
     * 選択されたDistributionテンプレートID.
     */
    private Long id;

    /**
     * 選択されたDistributionテンプレートレコードのversionNo.
     */
    private Long versionNo;

    /**
     * 一覧に表示するDataModelを取得する.
     * @return ataModel
     */
    public DataModel<?> getDataModel() {
        if (list != null) {
            dataModel = new ListDataModel<DistTemplateHeader>(list);
        }
        return dataModel;
    }

    /**
     * 削除フラグを取得する.
     * @return 削除フラグ
     */
    public boolean getDeleteFlg() {
        return deleteFlg;
    }
    /**
     * 削除フラグを設定する.
     * @param deleteFlg 削除フラグ
     */
    public void setDeleteFlg(boolean deleteFlg) {
        this.deleteFlg = deleteFlg;
    }

    /**
     * 選択されたDistributionテンプレートID.を取得します.
     * @return 選択されたDistributionテンプレートID.
     */
    public Long getId() {
        return id;
    }

    /**
     * 選択されたDistributionテンプレートID.を設定します.
     * @param id 選択されたDistributionテンプレートID.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 一覧表示非表示判定フラグを取得する.
     * @return 一覧表示非表示判定値
     */
    public boolean isViewRender() {
        boolean result = false;
        if (list != null && list.size() > 0) {
            result = true;
        }
        return result;
    }

    /**
     * CopyIdを取得します.
     * Indexページは必ず0を返す
     * @return 0
     */
    public Long getCopyId() {
        return 0L;
    }

    /**
     * 画面を初期化する.
     * <p>
     * 他画面から遷移した時に起動される. ページの初期化に必要な情報を準備し、ユーザーがアクセス可能な状態であるか検証する.
     * </p>
     */
    @Initialize
    public void initialize() {
        handler.handleAction(new InitializeAction(this));
    }

    /**
     * 編集画面に遷移.
     * @return 遷移先
     */
    public String edit() {
        return toUrl(String.format("distributionTemplateEdit?id=%s&projectId=%s&copyId=%s",
                             ((DistTemplateHeader) dataModel.getRowData()).getId(),
                             getCurrentProjectId(),
                             getCopyId()
                             ));
    }

    /**
     * 削除.
     * @return 遷移先
     */
    public String delete() {
        if (handler.handleAction(new DeleteAction(this))) {
            load();
        }
        return null;
    }

    private void load() {
        handler.handleAction(new SearchAction(this));
    }

    /**
     * initialDisplaySuccessを設定する.
     * @param initialDisplaySuccess the initialDisplaySuccess to set
     */
    public void setInitialDisplaySuccess(boolean initialDisplaySuccess) {
        this.initialDisplaySuccess = initialDisplaySuccess;
    }

    /**
     * initialDisplaySuccessを取得する.
     * @return the initialDisplaySuccess
     */
    public boolean isInitialDisplaySuccess() {
        return initialDisplaySuccess;
    }

    /**
     * @param versionNo the versionNo to set
     */
    public void setVersionNo(Long versionNo) {
        this.versionNo = versionNo;
    }

    /**
     * @return the versionNo
     */
    public Long getVersionNo() {
        return versionNo;
    }

    /**
     * 画面初期化アクション.
     * @author opentone
     */
    static class InitializeAction extends AbstractAction {
        /**
         * serialVersionUID.
         */
        private static final long serialVersionUID = -1535014384956420021L;
        /** アクション発生元ページ. */
        private DistributionTemplateIndexPage page;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page
         *            ページ
         */
        public InitializeAction(DistributionTemplateIndexPage page) {
            super(page);
            this.page = page;
        }

        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            page.checkProjectSelected();
            checkPermission();

            // 一覧取得処理
            page.load();
        }

        /**
         * 権限チェックを行う.
         * @throws ServiceAbortException 権限エラー
         */
        private void checkPermission() throws ServiceAbortException {
            if (!page.isSystemAdmin()
                    && !page.isProjectAdmin(page.getCurrentProjectId())) {
                throw new ServiceAbortException(
                    ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT);
            }
        }
    }

    /**
     * 検索アクション.
     * @author opentone
     */
    static class SearchAction extends AbstractAction {
        /**
         * serialVersionUID.
         */
        private static final long serialVersionUID = 1470552657640507350L;
        /** アクション発生元ページ. */
        private DistributionTemplateIndexPage page;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page
         *            ページ
         */
        public SearchAction(DistributionTemplateIndexPage page) {
            super(page);
            this.page = page;
        }

        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            try {
                page.list = page.service.findDistTemplateList(page.getCurrentProjectId());
                page.setInitialDisplaySuccess(true);
            } catch (ServiceAbortException e) {
                page.setPageMessage(e.getMessageCode());
                if (ApplicationMessageCode.NO_DATA_FOUND.equals(e.getMessageCode())) {
                    page.list = null;
                }
            }
        }
    }

    /**
     * 削除アクション.
     * @author opentone
     */
    static class DeleteAction extends AbstractAction {
        /**
         * serialVersionUID.
         */
        private static final long serialVersionUID = 1470552657640507350L;
        /** アクション発生元ページ. */
        private DistributionTemplateIndexPage page;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page ページ
         */
        public DeleteAction(DistributionTemplateIndexPage page) {
            super(page);
            this.page = page;
        }

        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            try {
                DistTemplateHeaderDelete header = DistTemplateHeader.newDistTemplateHeaderDelete();
                header.setId(page.getId());
                header.setVersionNo(page.getVersionNo());
                header.setProjectId(page.getCurrentProjectId());
                // 削除実行
                page.service.delete(header, page.getCurrentUser().getEmpNo());
                page.setPageMessage(ApplicationMessageCode.DISTRIBUTION_TEMPLATE_DELETED);
            } catch (ServiceAbortException e) {
                page.setPageMessage(e.getMessageCode());
            }
        }
    }
}
