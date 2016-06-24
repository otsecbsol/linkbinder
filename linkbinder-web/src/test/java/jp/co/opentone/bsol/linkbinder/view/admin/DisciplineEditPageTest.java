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
 * {@link DisciplineEditPage}のテストケース.
 * @author opentone
 */
public class DisciplineEditPageTest extends AbstractTestCase {

    /**
     * テスト対象クラス
     */
    @Resource
    private DisciplineEditPage page;

    /**
     * ログインユーザー
     */
    private User loginUser;

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
        loginUser = new User();
        loginUser.setEmpNo("ZZA01");
        loginUser.setNameE("Taro Yamada");

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
     * 初期化アクションを検証する. 登録、SystemAdminの場合
     * @throws Exception
     */
    @Test
    public void testInsertInitializeSystemAdmin() throws Exception {
        // テストに必要なデータを作成
        MockAbstractPage.IS_SYSTEM_ADMIN = true;
        MockAbstractPage.RET_PROJID = "PJ1";

        page.initialize();

        assertTrue(page.isInitialDisplaySuccess());
        assertEquals("部門新規登録", page.getTitle());

    }

    /**
     * 初期化アクションを検証する. 登録、ProjectAdminの場合
     * @throws Exception
     */
    @Test
    public void testInsertInitializeProjectAdmin() throws Exception {
        // テストに必要なデータを作成
        MockAbstractPage.IS_PROJECT_ADMIN = true;
        MockAbstractPage.RET_PROJID = "PJ1";

        page.initialize();

        assertTrue(page.isInitialDisplaySuccess());
        assertEquals("部門新規登録", page.getTitle());

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
     * プロジェクトが選択されていない
     * @throws Exception
     */
    @Test
    public void testInitializeInvalidParameter() throws Exception {
        // テストに必要なデータを作成
        MockAbstractPage.IS_SYSTEM_ADMIN = true;
        // プロジェクトIDがない
        MockAbstractPage.RET_PROJID = null;

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
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT,
                        actual.getMessageCode());
        }
    }

    /**
     * 初期化アクションを検証する. 更新、SystemAdminの場合
     * @throws Exception
     */
    @Test
    public void testUpdateInitializeSystemAdmin() throws Exception {
        // テストに必要なデータを作成
        MockAbstractPage.IS_SYSTEM_ADMIN = true;
        MockAbstractPage.RET_PROJID = "PJ1";

        page.setId(1L);

        Discipline find = new Discipline();
        find.setId(1L);
        find.setProjectId("PJ1");
        find.setProjectNameE("Test Project1");
        find.setDisciplineCd("TEST");
        find.setName("Test Discipline");
        find.setCreatedBy(loginUser);
        find.setUpdatedBy(loginUser);
        find.setVersionNo(1L);
        find.setDeleteNo(0L);

        MockDisciplineService.RET_FIND = find;
        page.initialize();

        assertTrue(page.isInitialDisplaySuccess());
        assertEquals("部門更新", page.getTitle());
        assertEquals(find.getId(), page.getDiscipline().getId());
        assertEquals(find.getProjectId(), page.getDiscipline().getProjectId());
        assertEquals(find.getProjectNameE(), page.getDiscipline().getProjectNameE());
        assertEquals(find.getDisciplineCd(), page.getDiscipline().getDisciplineCd());
        assertEquals(find.getDisciplineCd(), page.getCode());
        assertEquals(find.getName(), page.getDiscipline().getName());
        assertEquals(find.getName(), page.getName());
        assertEquals(find.getCreatedBy().toString(), page.getDiscipline().getCreatedBy().toString());
        assertEquals(find.getUpdatedBy().toString(), page.getDiscipline().getUpdatedBy().toString());
        assertEquals(find.getVersionNo(), page.getDiscipline().getVersionNo());
        assertEquals(find.getDeleteNo(), page.getDiscipline().getDeleteNo());

    }

    /**
     * 初期化アクションを検証する. 更新、ProjectAdminの場合
     * @throws Exception
     */
    @Test
    public void testUpdateInitializeProjectAdmin() throws Exception {
        // テストに必要なデータを作成
        MockAbstractPage.IS_PROJECT_ADMIN = true;
        MockAbstractPage.RET_PROJID = "PJ1";

        page.setId(1L);

        Discipline find = new Discipline();
        find.setId(1L);
        find.setProjectId("PJ1");
        find.setProjectNameE("Test Project1");
        find.setDisciplineCd("TEST");
        find.setName("Test Discipline");
        find.setCreatedBy(loginUser);
        find.setUpdatedBy(loginUser);
        find.setVersionNo(1L);
        find.setDeleteNo(0L);

        MockDisciplineService.RET_FIND = find;
        page.initialize();

        assertTrue(page.isInitialDisplaySuccess());
        assertEquals("部門更新", page.getTitle());
        assertEquals(find.getId(), page.getDiscipline().getId());
        assertEquals(find.getProjectId(), page.getDiscipline().getProjectId());
        assertEquals(find.getProjectNameE(), page.getDiscipline().getProjectNameE());
        assertEquals(find.getDisciplineCd(), page.getDiscipline().getDisciplineCd());
        assertEquals(find.getDisciplineCd(), page.getCode());
        assertEquals(find.getName(), page.getDiscipline().getName());
        assertEquals(find.getName(), page.getName());
        assertEquals(find.getCreatedBy().toString(), page.getDiscipline().getCreatedBy().toString());
        assertEquals(find.getUpdatedBy().toString(), page.getDiscipline().getUpdatedBy().toString());
        assertEquals(find.getVersionNo(), page.getDiscipline().getVersionNo());
        assertEquals(find.getDeleteNo(), page.getDiscipline().getDeleteNo());
    }

    /**
     * 次画面遷移を検証する.
     * @throws Exception
     */
    @Test
    public void testNext() throws Exception {
        // テストに必要なデータを作成
        page.setCode("TEST");
        page.setName("Test");

        MockAbstractPage.IS_SYSTEM_ADMIN = true;
        MockAbstractPage.RET_PROJID = "PJ1";
        MockDisciplineService.RET_VALIDATE = true;

        String nextPage = page.next();

        assertEquals("TEST", page.getDiscipline().getDisciplineCd());
        assertEquals("Test", page.getDiscipline().getName());
        assertEquals("PJ1", page.getDiscipline().getProjectId());
        assertEquals("disciplineConfirmation?projectId=PJ1", nextPage);
    }

    public static class MockDisciplineService extends MockUp<DisciplineServiceImpl> {
        static SearchDisciplineResult RET_SEARCH;
        static Discipline RET_FIND;
        static boolean RET_VALIDATE;

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
