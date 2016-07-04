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
package jp.co.opentone.bsol.linkbinder.view;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import javax.faces.application.FacesMessage;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import jp.co.opentone.bsol.framework.core.message.Messages;
import jp.co.opentone.bsol.framework.extension.jsf.FacesContextMock;
import jp.co.opentone.bsol.framework.test.AbstractTestCase;
import jp.co.opentone.bsol.framework.web.view.flash.Flash;
import jp.co.opentone.bsol.framework.web.view.util.ViewHelper;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;

/**
 * {@link ErrorPage} のためのテスト・クラス。
 * @author opentone
 */
public class ErrorPageTest extends AbstractTestCase{
    /**
     * テスト対象
     */
    ErrorPage page;

    @BeforeClass
    public static void testSetUp() {
        //TODO JMockitバージョンアップ対応
//        Mockit.redefineMethods(ViewHelper.class, MockViewHelper.class);
    }

    @AfterClass
    public static void testTeardown() {
    }

    /**
     * テスト前準備.
     */
    @Before
    public void setUp() {
        FacesContextMock.initialize();
        page = new ErrorPage();
    }

    /**
     * 後始末.
     */
    @After
    public void tearDown() {
        // 差し換えたMockをクリアする
        //TODO JMockitバージョンアップ対応
//        Mockit.tearDownMocks();
    }

    /**
     * {@link ErrorPage#isShowCloseLink()} のためのテスト・メソッド。
     */
    @Test
    public void testIsShowCloseLink() {
        new Flash().setValue(ErrorPage.KEY_SHOW_CLOSE_LINK, null);
        assertFalse(page.isShowCloseLink());

        new Flash().setValue(ErrorPage.KEY_SHOW_CLOSE_LINK, false);
        assertFalse(page.isShowCloseLink());

        new Flash().setValue(ErrorPage.KEY_SHOW_CLOSE_LINK, true);
        assertTrue(page.isShowCloseLink());
    }

    @Test
    public void testIsShowCloseLinkExceptionOccured() {
        new Flash().setValue(ErrorPage.KEY_SHOW_CLOSE_LINK, new String("Illegal Object"));
        assertFalse(page.isShowCloseLink());

    }

    @Test
    public void testInitialize() {
        page.setViewHelper(new ViewHelper());

        // 何も起きずに処理が終わることを確認
        page.initialize();
    }

    @Test
    public void testInitializeTimeout() {
        page.setViewHelper(new ViewHelper());
        MockViewHelper.RET_SESSION_VALUE.put(ErrorPage.KEY_ERROR_MESSAGE_CODE, ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_PROJECT_DIFF);

        // 期待されるメッセージをセット
        FacesContextMock.EXPECTED_MESSAGE =
                new FacesMessage(
                    FacesMessage.SEVERITY_ERROR, FacesMessage.SEVERITY_ERROR.toString(),
                    createExpectedMessageString(Messages.getMessageAsString(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_PROJECT_DIFF),
                        null));

        page.initialize();

        assertNull(page.viewHelper.getSessionValue(ErrorPage.KEY_ERROR_MESSAGE_CODE));
    }

    public static class MockViewHelper {
        static Map<String, Object> RET_SESSION_VALUE = new HashMap<String, Object>();

        @SuppressWarnings("unchecked")
        public <T> T getSessionValue(String key) {
            return (T) RET_SESSION_VALUE.get(key);
        }

        public void removeSessionValue(String key) {
            RET_SESSION_VALUE.remove(key);
        }
    }
}
