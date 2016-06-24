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
package jp.co.opentone.bsol.linkbinder.action;

import static org.junit.Assert.*;

import org.junit.Test;

import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.web.view.Page;
import jp.co.opentone.bsol.linkbinder.view.AbstractPage;

/**
 * {@link AbstractAction}のテストケース.
 * @author opentone
 */
public class AbstractActionTest {

    /**
     * テストのための仮ページ.
     */
    private MockPage page;
    /**
     * テスト対象.
     */
    private MockAction action;

    /**
     * コンストラクタのテスト. pageオブジェクトに設定されたページ名が、 actionに設定されていることを検証する.
     */
    @Test
    public void testInitialize() {
        page = new MockPage("Test");
        action = new MockAction(page);

        assertEquals(page.getActionName(), action.getName());
        action.setName("Updated");
        assertEquals("Updated", action.getName());
    }

    public static class MockPage extends AbstractPage {
        /**
         *
         */
        private static final long serialVersionUID = 7191640092307200738L;

        public MockPage(String actionName) {
            super.setActionName(actionName);
        }
    }

    @SuppressWarnings("serial")
    public static class MockAction extends AbstractAction {
        public MockAction(Page page) {
            super(page);
        }

        public void execute() throws ServiceAbortException {
        }
    }
}
