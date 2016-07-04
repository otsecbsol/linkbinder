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
package jp.co.opentone.bsol.linkbinder.view.correspon;

import javax.annotation.ManagedBean;
import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;

import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.web.extension.jsf.annotation.Initialize;
import jp.co.opentone.bsol.linkbinder.action.AbstractAction;
import jp.co.opentone.bsol.linkbinder.dto.Correspon;
import jp.co.opentone.bsol.linkbinder.dto.code.ReadStatus;
import jp.co.opentone.bsol.linkbinder.service.correspon.CorresponReadStatusService;
import jp.co.opentone.bsol.linkbinder.service.correspon.CorresponService;
import jp.co.opentone.bsol.linkbinder.view.correspon.attachment.AttachmentDownloadablePage;
import jp.co.opentone.bsol.linkbinder.view.correspon.util.AddressModel;

/**
 * コレポン文書表示画面.
 * @author opentone
 */
@ManagedBean
@Scope("view")
public class CorresponRepliedPage extends CorresponPage implements AttachmentDownloadablePage {
    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -6819171074955985797L;

    /**
     * コレポン文書サービス.
     */
    @Resource
    private CorresponService corresponService;

    /**
     * コレポン文書既読・未読サービス.
     */
    @Resource
    private CorresponReadStatusService corresponReadStatusService;

    /**
     * 空のインスタンスを生成する.
     */
    public CorresponRepliedPage() {
    }

    /**
     * 画面を初期化する.
     * <p>
     * クエリパラメータで指定されたIDに対応する、コレポン文書を表示する.
     * </p>
     */
    @Override
    @Initialize
    public void initialize() {
        super.initialize();
        handler.handleAction(new InitializeAction(this));
    }

    /**
     * 画面初期化アクション.
     * @author opentone
     */
    static class InitializeAction extends AbstractAction {
        /**
         * serialVersionUID.
         */
        private static final long serialVersionUID = 5761178296922393585L;
        /** アクション発生元ページ. */
        private CorresponRepliedPage page;
        /**
         * @param page
         */
        public InitializeAction(CorresponRepliedPage page) {
            super(page);
            this.page = page;
        }

        /* (non-Javadoc)
         * @see Action#execute()
         */
        @Override
        public void execute() throws ServiceAbortException {
            setUpCorrespon();

            // 応答履歴をデフォルト表示するために取得する
            if (page.getCorresponResponseHistory() == null) {
                page.getModule().showResponseHistory();
            }

            page.transferCorresponDisplayInfo();
        }

        private void setUpCorrespon() throws ServiceAbortException {
            // 表示データを取得し、各詳細情報の表示状態の初期値を設定する
            Correspon c = page.corresponService.find(page.getId());

            setUpAddress(c);
            setUpWorkflow(c);
            updateReadStatus(c);

            page.setReplied(c);
            page.setDisplayReplied(true);
        }

        private void setUpAddress(Correspon c) {
            page.setToAddressModel(AddressModel.createAddressModelList(
                        c.getAddressCorresponGroups(),
                        true,
//                        page.getUsers(),
                        page.getElemControl()));
            page.setCcAddressModel(AddressModel.createAddressModelList(
                        c.getAddressCorresponGroups(),
                        false,
//                        page.getUsers(),
                        page.getElemControl()));
        }

        private void setUpWorkflow(Correspon c) {
            // 表示用承認フローにPrepererをセットする
            page.setWorkflowList(page.createDisplayWorkflowList(c));
        }

        /**
         * 既読・未読処理.
         *
         * @throws ServiceAbortException 既読・未読処理エラー
         */
        private void updateReadStatus(Correspon c) throws ServiceAbortException {
            // コレポン文書を表示した為、未読→既読に変更
            page.corresponReadStatusService.updateReadStatusById(c.getId(), ReadStatus.READ);
        }
    }
}
