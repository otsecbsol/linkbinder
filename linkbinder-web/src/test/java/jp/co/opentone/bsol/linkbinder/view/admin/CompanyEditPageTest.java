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

import static jp.co.opentone.bsol.framework.core.message.MessageCode.*;
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
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.service.admin.impl.CompanyServiceImpl;
import jp.co.opentone.bsol.linkbinder.view.AbstractPage;
import mockit.Mock;
import mockit.MockUp;

/**
 * {@link CompanyEditPage}のテストケース.
 * @author opentone
 */
public class CompanyEditPageTest extends AbstractTestCase {

    /**
     * テスト対象クラス
     */
    @Resource
    private CompanyEditPage page;

    // 表題（新規登録）
    private static final String NEW = "会社新規登録";

    // 表題（更新）
    private static final String UPDATE = "会社更新";

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
        MockCompanyService.RET_VALIDATE = false;
        MockAbstractPage.RET_IS_SYSTEM_ADMIN = false;
        MockAbstractPage.RET_IS_PROJECT_ADMIN = false;
    }

    /**
     * 初期化アクションを検証する. マスタ管理、会社情報登録の場合
     * @throws Exception
     */
    @Test
    public void testInsertInitializeMaster() throws Exception {
        MockAbstractPage.RET_IS_SYSTEM_ADMIN = true;
        page.initialize();
        assertTrue(page.isInitialDisplaySuccess());
        assertEquals(NEW, page.getTitle());
    }

    /**
     * 初期化アクションを検証する. マスタ管理、会社情報更新の場合
     * @throws Exception
     */
    @Test
    public void testUpdateInitializeMaster() throws Exception {
        // テストに必要なデータを作成する
        Company company = createCompany();
        MockCompanyService.RET_FIND = company;
        MockAbstractPage.RET_IS_SYSTEM_ADMIN = true;

        page.setId(1L);

        page.initialize();

        assertEquals(UPDATE, page.getTitle());
        assertTrue(page.isInitialDisplaySuccess());
    }

    /**
     * 初期化アクションを検証する. プロジェクトマスタ管理、会社情報更新の場合
     * @throws Exception
     */
    @Test
    public void testUpdateInitializeProject() throws Exception {
        // テストに必要なデータを作成する
        Company company = createCompany();
        MockCompanyService.RET_FIND = company;
        MockAbstractPage.RET_IS_PROJECT_ADMIN = true;

        page.setId(1L);

        MockAbstractPage.RET_PROJID = "PJ1";

        page.initialize();

        assertTrue(page.isInitialDisplaySuccess());
        assertEquals("PJ1", page.getProjectId());
        assertEquals(UPDATE, page.getTitle());
    }

    /**
     * 初期化アクションを検証する. 権限がない場合
     * @throws Exception
     */
    @Test
    public void testInvalidCompetenceInitialize() throws Exception {
        // テストに必要なデータを作成する
        Company company = createCompany();
        MockCompanyService.RET_FIND = company;

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
     * 初期化アクションを検証する. プロジェクトマスタ管理、会社情報登録の場合
     * @throws Exception
     */
    @Test
    public void testInvalidInsertInitializeProject() throws Exception {
        // テストに必要なデータを作成する
        Company company = createCompany();
        MockCompanyService.RET_FIND = company;
        MockAbstractPage.RET_PROJID = "PJ1";
        MockAbstractPage.RET_IS_PROJECT_ADMIN = true;

        // 期待されるメッセージをセット
        FacesContextMock.EXPECTED_CLIENT_ID = null;
        FacesContextMock.EXPECTED_MESSAGE =
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                 FacesMessage.SEVERITY_ERROR.toString(),
                                 createExpectedMessageString(
                                     Messages.getMessageAsString(E_INVALID_PARAMETER),
                                     null));
        try {
            page.initialize();
        } catch (InvalidOperationRuntimeException e) {
            ServiceAbortException actual = (ServiceAbortException)e.getCause();
            assertEquals(ApplicationMessageCode.E_INVALID_PARAMETER, actual.getMessageCode());
        }
    }

    /**
     * 次画面遷移を検証する. マスタ管理、登録処理の場合
     * @throws Exception
     */
    @Test
    public void testInsertNextMaster() throws Exception {
        // テストに必要なデータを作成する
        Company company = createCompany();
        page.setCode(company.getCompanyCd());
        page.setName(company.getName());
        MockCompanyService.RET_VALIDATE = true;
        MockAbstractPage.RET_IS_SYSTEM_ADMIN = true;

        String nextPage = page.next();
        assertEquals("companyConfirmation", nextPage);
        assertEquals(company.getCompanyCd(), page.getCompany().getCompanyCd());
        assertEquals(company.getName(), page.getCompany().getName());
    }

    /**
     * 次画面遷移を検証する. マスタ管理、更新処理の場合
     * @throws Exception
     */
    @Test
    public void testUpdateNextMaster() throws Exception {
        // テストに必要なデータを作成する
        Company company = createCompany();
        page.setId(company.getId());
        page.setCode(company.getCompanyCd());
        page.setName(company.getName());
        MockCompanyService.RET_VALIDATE = true;
        MockAbstractPage.RET_IS_SYSTEM_ADMIN = true;

        String nextPage = page.next();
        assertEquals("companyConfirmation", nextPage);
        assertEquals(company.getCompanyCd(), page.getCompany().getCompanyCd());
        assertEquals(company.getName(), page.getCompany().getName());
    }

    /**
     * 次画面遷移を検証する. プロジェクトマスタ管理、更新処理の場合
     * @throws Exception
     */
    @Test
    public void testUpdateNextProject() throws Exception {
        // テストに必要なデータを作成する
        Company company = createCompany();
        company.setProjectId("PJ1");
        company.setRole("Owner");
        page.setId(company.getId());
        page.setCode(company.getCompanyCd());
        page.setName(company.getName());
        page.setProjectId(company.getProjectId());
        page.setRole(company.getRole());
        MockCompanyService.RET_VALIDATE = true;
        MockAbstractPage.RET_IS_PROJECT_ADMIN = true;

        String nextPage = page.next();
        assertEquals("companyConfirmation", nextPage);
        assertEquals(company.getCompanyCd(), page.getCompany().getCompanyCd());
        assertEquals(company.getName(), page.getCompany().getName());
        assertEquals(company.getProjectId(), page.getCompany().getProjectId());
        assertEquals(company.getRole(), page.getCompany().getRole());

    }

    /**
     * テストに必要な会社情報を作成する.
     * @return 会社情報
     */
    private Company createCompany() {
        User user = new User();
        user.setEmpNo("ZZA01");
        Company c = new Company();
        c.setId(1L);
        c.setCompanyCd("TEST");
        c.setName("TestCompany");
        c.setCreatedBy(user);
        c.setUpdatedBy(user);
        return c;
    }

    public static class MockCompanyService extends MockUp<CompanyServiceImpl> {
        static Company RET_FIND;
        static boolean RET_VALIDATE;

        @Mock
        public Company find(Long id) throws ServiceAbortException {
            return RET_FIND;
        }

        @Mock
        public boolean validate(Company company) throws ServiceAbortException {
            return RET_VALIDATE;
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
