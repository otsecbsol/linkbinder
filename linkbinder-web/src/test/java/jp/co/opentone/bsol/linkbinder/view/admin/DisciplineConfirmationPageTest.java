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
import jp.co.opentone.bsol.linkbinder.dto.Discipline;
import jp.co.opentone.bsol.linkbinder.dto.SearchDisciplineResult;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchDisciplineCondition;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.service.admin.impl.DisciplineServiceImpl;
import jp.co.opentone.bsol.linkbinder.view.AbstractPage;
import mockit.Mock;
import mockit.MockUp;

/**
 * {@link DisciplineConfirmationPage}のテストケース.
 * @author opentone
 */
public class DisciplineConfirmationPageTest extends AbstractTestCase {

    /**
     * テスト対象クラス
     */
    @Resource
    private DisciplineConfirmationPage page;

    /**
     * テストの前準備.
     */
    @BeforeClass
    public static void testSetUp() {
        FacesContextMock.initialize();
        FacesContextMock.EXPECTED_MESSAGE = null;
        new MockAbstractPage();
        new MockDisciplineService();
    }

    /**
     * 後始末.
     */
    @AfterClass
    public static void testTeardown() {
        new MockAbstractPage().tearDown();
        new MockDisciplineService().tearDown();
        FacesContextMock.tearDown();
    }

    @Before
    public void setUp() {
        User user = new User();
        user.setEmpNo("ZZA01");
        user.setNameE("Taro Yamada");

    }

    @After
    public void tearDown() {
        MockAbstractPage.RET_PROJID = null;
        MockAbstractPage.IS_SYSTEM_ADMIN = false;
        MockAbstractPage.IS_PROJECT_ADMIN = false;
        MockDisciplineService.RET_SEARCH = null;
        MockDisciplineService.RET_FIND = null;
        MockDisciplineService.RET_VALIDATE = false;

        FacesContextMock.EXPECTED_CLIENT_ID = null;
        FacesContextMock.EXPECTED_MESSAGE = null;
        page.setDiscipline(null);
    }

    /**
     * 初期化アクションを検証する. SystemAdminの場合
     * @throws Exception
     */
    @Test
    public void testInitializeSystemAdmin() throws Exception {
        // テストに必要なデータを作成
        MockAbstractPage.RET_PROJID = "9-9999-9";
        MockAbstractPage.IS_SYSTEM_ADMIN = true;
        page.setDiscipline(new Discipline());
        page.initialize();
        assertTrue(page.isInitialDisplaySuccess());
    }

    /**
     * 初期化アクションを検証する. ProjectAdminの場合
     * @throws Exception
     */
    @Test
    public void testInitializeProjectAdmin() throws Exception {
        // テストに必要なデータを作成
        MockAbstractPage.RET_PROJID = "9-9999-9";
        MockAbstractPage.IS_PROJECT_ADMIN = true;
        page.setDiscipline(new Discipline());
        page.initialize();
        assertTrue(page.isInitialDisplaySuccess());
    }

    /**
     * 初期化アクションを検証する. SystemAdmin,ProjectAdminではない場合
     * @throws Exception
     */
    @Test
    public void testInitializeNotSystemAdminProjectAdmin() throws Exception {
        // テストに必要なデータを作成
        // SysetmAdmin,ProjectAdminではない
        MockAbstractPage.IS_SYSTEM_ADMIN = false;
        MockAbstractPage.IS_PROJECT_ADMIN = false;
        MockAbstractPage.RET_PROJID = "PJ1";

        // 期待されるメッセージをセット
        FacesContextMock.EXPECTED_CLIENT_ID = null;
        FacesContextMock.EXPECTED_MESSAGE =
                new FacesMessage(
                    FacesMessage.SEVERITY_ERROR, FacesMessage.SEVERITY_ERROR.toString(),
                    createExpectedMessageString(
                            Messages.getMessageAsString(CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT),
                            "Initialize"));
        try {
            page.initialize();
            fail("例外が発生していない");
        } catch (InvalidOperationRuntimeException e) {
            ServiceAbortException actual = (ServiceAbortException)e.getCause();
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT, actual.getMessageCode());
        }
    }


    /**
     * 初期化アクションを検証する.
     * プロジェクト未選択の場合.
     * @throws Exception
     */
    @Test
    public void testInitializeProjectNotSelected() throws Exception {
        // テストに必要なデータを作成
        MockAbstractPage.RET_PROJID = null;
        MockAbstractPage.IS_SYSTEM_ADMIN = true;
        page.setInitialDisplaySuccess(false);
        // 期待されるメッセージをセット
        FacesContextMock.EXPECTED_CLIENT_ID = null;
        FacesContextMock.EXPECTED_MESSAGE =
                new FacesMessage(
                    FacesMessage.SEVERITY_ERROR, FacesMessage.SEVERITY_ERROR.toString(),
                    createExpectedMessageString(
                            Messages.getMessageAsString(CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT),
                            "Initialize"));
        try {
            page.initialize();
            fail("例外が発生していない");
        } catch (InvalidOperationRuntimeException e) {
            ServiceAbortException actual = (ServiceAbortException)e.getCause();
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT, actual.getMessageCode());
        }
        assertFalse(page.isInitialDisplaySuccess());
    }

    /**
     * 保存アクションを検証する.
     * @throws Exception
     */
    @Test
    public void testSave() throws Exception {
        // テストに必要なデータを作成する
        Discipline discipline = new Discipline();
        discipline.setDisciplineCd("TEST");
        discipline.setName("Test");
        discipline.setProjectId("PJ1");
        page.setDiscipline(discipline);

        MockDisciplineService.RET_SAVE = 1L;
        MockAbstractPage.RET_PROJID = "PJ1";

        String nextPage = page.save();
        assertEquals("discipline?id=1", nextPage);
        assertEquals(discipline.toString(), MockDisciplineService.CRT_DISCIPLINE.toString());
    }

    public static class MockDisciplineService extends MockUp<DisciplineServiceImpl> {
        static SearchDisciplineResult RET_SEARCH;
        static Discipline RET_FIND;
        static boolean RET_VALIDATE;
        static Long RET_SAVE;
        static Discipline CRT_DISCIPLINE;

        @Mock
        public SearchDisciplineResult search(SearchDisciplineCondition condition)
            throws ServiceAbortException {
            return RET_SEARCH;
        }
        @Mock
        public Discipline find(Long id) throws ServiceAbortException {
            return RET_FIND;
        }

        @Mock
        public boolean validate(Discipline discipline) throws ServiceAbortException {
            return RET_VALIDATE;
        }

        @Mock
        public Long save(Discipline discipline) throws ServiceAbortException {
            CRT_DISCIPLINE = discipline;
            return RET_SAVE;
        }
    }

    public static class MockAbstractPage extends MockUp<AbstractPage> {
        static String RET_PROJID;
        static boolean IS_SYSTEM_ADMIN;
        static boolean IS_PROJECT_ADMIN;

        @Mock
        public String getCurrentProjectId() {
            return RET_PROJID;
        }

        @Mock
        public boolean isSystemAdmin() {
            return IS_SYSTEM_ADMIN;
        }

        @Mock
        public boolean isProjectAdmin(String projectId) {
            return IS_PROJECT_ADMIN;
        }
    }
}
