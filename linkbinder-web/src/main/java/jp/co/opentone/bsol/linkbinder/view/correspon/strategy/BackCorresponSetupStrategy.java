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
package jp.co.opentone.bsol.linkbinder.view.correspon.strategy;

import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.linkbinder.view.correspon.util.CorresponEditMode;


/**
 * コレポン文書入力画面に初期表示するコレポン文書オブジェクトを生成する.
 * 編集モード @{link {@link CorresponEditMode#BACK}に対応する.
 * @author opentone
 */
public class BackCorresponSetupStrategy extends CorresponSetupStrategy {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -4986761230514507795L;

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.view.correspon.util.setup.CorresponSetup#setup()
     */
    @Override
    public void setup() throws ServiceAbortException {
        //  入力値が格納されたコレポン文書オブジェクトの内容を
        //  pageオブジェクトにコピー
        page.setSubject(page.getCorrespon().getSubject());
        page.setBody(page.getCorrespon().getBody());
    }

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.view.correspon.util.setup.CorresponSetup#getPageTitle()
     */
    @Override
    protected String getPageTitle() {
        // 「戻る」なので、既にページに設定済の値を引き継ぐ
        if (page == null) {
            return null;
        }
        return page.getTitle();
    }
}
