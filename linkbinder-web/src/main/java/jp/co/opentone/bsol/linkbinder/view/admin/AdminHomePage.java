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

import org.springframework.context.annotation.Scope;

import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.web.extension.jsf.annotation.Initialize;
import jp.co.opentone.bsol.linkbinder.action.AbstractAction;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.view.AbstractPage;

/**
 * マスタ管理画面.
 * @author opentone
 */
@ManagedBean
@Scope("view")
public class AdminHomePage extends AbstractPage {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -333369401153082405L;

    /**
     * 空のインスタンスを生成する.
     */
    public AdminHomePage() {
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
     * 初期化アクション.
     * @author opentone
     */
    static class InitializeAction extends AbstractAction {
        /**
         * serialVersionUID.
         */
        private static final long serialVersionUID = -6226457639005387171L;
        /** アクション発生元ページ. */
        private AdminHomePage page;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page
         *            ページ
         */
        public InitializeAction(AdminHomePage page) {
            super(page);
            this.page = page;
        }

        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            // System Admin以外はエラー
            if (!page.isSystemAdmin()) {
                throw new ServiceAbortException(
                        ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT);
            }
        }
    }
}
