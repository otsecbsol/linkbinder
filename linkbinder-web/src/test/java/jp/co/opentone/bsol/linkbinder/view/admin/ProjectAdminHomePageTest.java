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

import static jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode.*;
import static org.junit.Assert.*;

import javax.annotation.Resource;
import javax.faces.application.FacesMessage;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import jp.co.opentone.bsol.framework.core.exception.InvalidOperationRuntimeException;
import jp.co.opentone.bsol.framework.core.message.Messages;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.extension.jsf.FacesContextMock;
import jp.co.opentone.bsol.framework.test.AbstractTestCase;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.view.AbstractPage;
import mockit.Mock;
import mockit.MockUp;

public class ProjectAdminHomePageTest extends AbstractTestCase {

    /**
     * テスト対象.
     */
    @Resource
    private ProjectAdminHomePage page;

    /**
     * テストの前準備.
     */
    @BeforeClass
    public static void testSetUp() {
        FacesContextMock.initialize();
        new MockAbstractPage();
    }

    /**
     * 後始末.
     */
    @AfterClass
    public static void testTeardown() {
        new MockAbstractPage().tearDown();
        FacesContextMock.tearDown();
    }

    @Before
    public void setUp() {
        MockAbstractPage.RET_PROJECT_ID = "9-9999-9";
    }

    @After
    public void tearDown() {
        MockAbstractPage.RET_PROJECT_ID = null;
        MockAbstractPage.RET_SYSTEM_ADMIN = false;
        MockAbstractPage.RET_PROJECT_ADMIN = false;
        MockAbstractPage.RET_GROUP_ADMIN = false;
    }

    /**
     * 初期化アクションのテスト. SystemAdmin.
     */
    @Test
    public void testInitializeSystemAdmin() {
        MockAbstractPage.RET_SYSTEM_ADMIN = true;
        page.initialize();
        // エラーなし
    }

    /**
     * 初期化アクションのテスト. ProjectAdmin.
     */
    @Test
    public void testInitializeProjectAdmin() {
        MockAbstractPage.RET_PROJECT_ADMIN = true;
        page.initialize();
        // エラーなし
    }

    /**
     * 初期化アクションのテスト. Discipline.
     */
    @Test
    public void testInitializeGroupAdmin() {
        MockAbstractPage.RET_GROUP_ADMIN = true;
        page.initialize();
        // エラーなし
    }

    /**
     * 初期化アクションのテスト. 権限無し.
     */
    @Test
    public void testInitializeProjectError() {
        // 期待されるメッセージをセット
        FacesContextMock.EXPECTED_CLIENT_ID = null;
        FacesContextMock.EXPECTED_MESSAGE =
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                 FacesMessage.SEVERITY_ERROR.toString(),
                                 createExpectedMessageString(
                                     Messages.getMessageAsString(
                                         CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT),
                                         "Initialize"));

        try {
            page.initialize();
            fail("例外が発生していない");
        } catch (InvalidOperationRuntimeException expected) {
            assertTrue(expected.getCause() instanceof ServiceAbortException);
            assertEquals(((ServiceAbortException)expected.getCause()).getMessageCode(),
                        ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT);
        }
    }

    /**
     * 初期化アクションのテスト. プロジェクト未選択.
     */
    @Test
    public void testInitializeProjectNotSelected() {
        // 期待されるメッセージをセット
        MockAbstractPage.RET_PROJECT_ID = null;
        MockAbstractPage.RET_PROJECT_ADMIN = true;
        FacesContextMock.EXPECTED_CLIENT_ID = null;
        FacesContextMock.EXPECTED_MESSAGE =
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                 FacesMessage.SEVERITY_ERROR.toString(),
                                 createExpectedMessageString(
                                     Messages.getMessageAsString(
                                         CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT),
                                         "Initialize"));

        try {
            page.initialize();
            fail("例外が発生していない");
        } catch (InvalidOperationRuntimeException expected) {
            assertTrue(expected.getCause() instanceof ServiceAbortException);
            assertEquals(((ServiceAbortException)expected.getCause()).getMessageCode(),
                        ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT);
        }
    }

    public static class MockAbstractPage extends MockUp<AbstractPage> {
        static String RET_PROJECT_ID;
        static boolean RET_SYSTEM_ADMIN;
        static boolean RET_PROJECT_ADMIN;
        static boolean RET_GROUP_ADMIN;

        @Mock
        public String getCurrentProjectId() {
            return RET_PROJECT_ID;
        }

        @Mock
        public boolean isSystemAdmin() {
            return RET_SYSTEM_ADMIN;
        }

        @Mock
        public boolean isProjectAdmin(String projectId) {
            return RET_PROJECT_ADMIN;
        }

        @Mock
        public boolean isAnyGroupAdmin(String projectId) {
            return RET_GROUP_ADMIN;
        }
    }
}
