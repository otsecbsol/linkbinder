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
import org.junit.BeforeClass;
import org.junit.Test;

import jp.co.opentone.bsol.framework.core.exception.InvalidOperationRuntimeException;
import jp.co.opentone.bsol.framework.core.message.Messages;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.extension.jsf.FacesContextMock;
import jp.co.opentone.bsol.framework.test.AbstractTestCase;
import jp.co.opentone.bsol.linkbinder.dto.Company;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.service.admin.impl.CompanyServiceImpl;
import jp.co.opentone.bsol.linkbinder.view.AbstractPage;
import mockit.Mock;
import mockit.MockUp;

/**
 * {@link CompanyConfirmationPage}のテストケース.
 *
 * @author opentone
 */
public class CompanyConfirmationPageTest extends AbstractTestCase {

    /**
     * テスト対象クラス
     */
    @Resource
    private CompanyConfirmationPage page;


    /**
     * テストの前準備.
     */
    @BeforeClass
    public static void testSetUp() {
        FacesContextMock.initialize();
        new MockAbstractPage();
        new MockCompanyService();
    }

    /**
     * 後始末.
     */
    @AfterClass
    public static void testTeardown() {
        new MockAbstractPage().tearDown();
        new MockCompanyService().tearDown();
        FacesContextMock.tearDown();
    }

    @After
    public void tearDown() {
        MockAbstractPage.RET_PROJID = null;
        MockAbstractPage.RET_IS_SYSTEM_ADMIN = false;
        MockAbstractPage.RET_IS_PROJECT_ADMIN = false;
        MockCompanyService.RET_SAVE = null;

        page.setCompany(null);
    }

    /**
     * 初期化アクションを検証する.
     * マスタ管理、登録の場合
     *
     * @throws Exception
     */
    @Test
    public void testInsertInitializeMaster() throws Exception {
        MockAbstractPage.RET_IS_SYSTEM_ADMIN = true;
        Company company = new Company();
        company.setCompanyCd("TEST");
        company.setName("test");
        page.setCompany(company);
        page.initialize();

        assertTrue(page.isInitialDisplaySuccess());
    }

    /**
     * 初期化アクションを検証する.
     * マスタ管理、更新の場合
     *
     * @throws Exception
     */
    @Test
    public void testUpdateInitializeMaster() throws Exception {
        MockAbstractPage.RET_IS_SYSTEM_ADMIN = true;
        page.setId(1L);
        Company company = new Company();
        company.setId(1L);
        page.setCompany(company);
        page.initialize();

        assertTrue(page.isInitialDisplaySuccess());
    }

    /**
     * 初期化アクションを検証する.
     * プロジェクトマスタ管理、更新の場合
     *
     * @throws Exception
     */
    @Test
    public void testUpdateInitializeProject() throws Exception {
        MockAbstractPage.RET_IS_PROJECT_ADMIN = true;
        page.setId(1L);
        Company company = new Company();
        company.setId(1L);

        page.setCompany(company);
        page.initialize();

        assertTrue(page.isInitialDisplaySuccess());
    }

    /**
     * 初期化アクションを検証する.
     * マスタ管理、登録、SystemAdminではない場合
     *
     * @throws Exception
     */
    @Test
    public void testNotSystemAdminInsertInitializeMaster() throws Exception {
        // 期待されるメッセージをセット
        FacesContextMock.EXPECTED_CLIENT_ID = null;
        FacesContextMock.EXPECTED_MESSAGE =
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                 FacesMessage.SEVERITY_ERROR.toString(),
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
     * マスタ管理、更新、SystemAdminではない場合
     *
     * @throws Exception
     */
    @Test
    public void testNotSystemAdminUpdateInitializeMaster() throws Exception {
        // 期待されるメッセージをセット
        page.setId(1L);
        FacesContextMock.EXPECTED_CLIENT_ID = null;
        FacesContextMock.EXPECTED_MESSAGE =
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                 FacesMessage.SEVERITY_ERROR.toString(),
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
     * プロジェクトマスタ管理、登録の場合
     *
     * @throws Exception
     */
    @Test
    public void testInvalidInsertInitializeProject() throws Exception {
        MockAbstractPage.RET_IS_PROJECT_ADMIN = true;
        MockAbstractPage.RET_PROJID = "PJ1";

        // 期待されるメッセージをセット
        FacesContextMock.EXPECTED_CLIENT_ID = null;
        FacesContextMock.EXPECTED_MESSAGE =
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                 FacesMessage.SEVERITY_ERROR.toString(),
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
     * プロジェクトマスタ管理、更新、ProjectAdminではない場合
     *
     * @throws Exception
     */
    @Test
    public void testInvalidUpdateInitializeProject() throws Exception {
        MockAbstractPage.RET_PROJID = "PJ1";

        // 期待されるメッセージをセット
        FacesContextMock.EXPECTED_CLIENT_ID = null;
        FacesContextMock.EXPECTED_MESSAGE =
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                 FacesMessage.SEVERITY_ERROR.toString(),
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
     * 保存処理を検証する.
     * 登録の場合
     *
     * @throws Exception
     */
    @Test
    public void testSaveInsert() throws Exception {
        //テストに必要なデータを作成する
        Company company = new Company();
        company.setCompanyCd("TEST");
        company.setName("Test_Company");
        page.setCompany(company);

        String nextPage = page.save();

        assertEquals("company?id=1", nextPage);
        assertEquals(String.valueOf(1L), page.getId().toString());
    }

    /**
     * 保存処理を検証する.
     * 更新の場合
     *
     * @throws Exception
     */
    @Test
    public void testSaveUpdate() throws Exception {
        //テストに必要なデータを作成する
        Company company = new Company();
        company.setCompanyCd("TEST");
        company.setName("Test_Company");
        company.setId(1L);
        page.setCompany(company);

        String nextPage = page.save();

        assertEquals("company?id=1", nextPage);
        assertEquals(String.valueOf(1L), page.getId().toString());
    }

    public static class MockCompanyService extends MockUp<CompanyServiceImpl> {
        static Long RET_SAVE;

        @Mock
        public Long save(Company company) throws ServiceAbortException {
            if (company.getId() == null) {
                RET_SAVE = 1L;
            } else {
                RET_SAVE = company.getId();
            }
            return RET_SAVE;
        }

    }

    public static class MockAbstractPage extends MockUp<AbstractPage> {
        static String RET_PROJID;
        static boolean RET_IS_SYSTEM_ADMIN;
        static boolean RET_IS_PROJECT_ADMIN;

        @Mock
        public String getCurrentProjectId() {
            return RET_PROJID;
        }

        @Mock
        public boolean isSystemAdmin() {
            return RET_IS_SYSTEM_ADMIN;
        }

        @Mock
        public boolean isProjectAdmin(String projectId) {
            return RET_IS_PROJECT_ADMIN;
        }
    }
}
