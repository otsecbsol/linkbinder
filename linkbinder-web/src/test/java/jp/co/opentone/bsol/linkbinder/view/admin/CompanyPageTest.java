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
import static junit.framework.Assert.*;

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
 * {@link CompanyPage}のテストケース.
 *
 * @author opentone
 */
public class CompanyPageTest extends AbstractTestCase {

    /**
     * テスト対象クラス
     */
    @Resource
    private CompanyPage page;

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
        MockCompanyService.RET_FIND = null;
        page.setId(null);
    }

    /**
     * 初期化アクションを検証する.
     * SystemAdminの場合
     *
     * @throws Exception
     */
    @Test
    public void testInitializeSystemAdmin() throws Exception {
        //テストに必要なデータを作成
        User user = new User();
        user.setEmpNo("ZZA01");
        Company company = new Company();
        company.setId(1L);
        company.setCompanyCd("TEST");
        company.setName("Test_Company");
        company.setCreatedBy(user);
        company.setUpdatedBy(user);
        MockCompanyService.RET_FIND = company;

        page.setId(1L);

        MockAbstractPage.RET_IS_SYSTEM_ADMIN = true;

        page.initialize();

        assertEquals(company, page.getCompany());
    }

    /**
     * 初期化アクションを検証する.
     * ProjectAdminの場合
     *
     * @throws Exception
     */
    @Test
    public void testInitializeProjectAdmin() throws Exception {
        //テストに必要なデータを作成
        User user = new User();
        user.setEmpNo("ZZA01");
        Company company = new Company();
        company.setId(1L);
        company.setCompanyCd("TEST");
        company.setName("Test_Company");
        company.setCreatedBy(user);
        company.setUpdatedBy(user);
        MockCompanyService.RET_FIND = company;

        page.setId(1L);

        MockAbstractPage.RET_IS_PROJECT_ADMIN= true;
        MockAbstractPage.RET_PROJID = "PJ1";

        page.initialize();

        assertEquals(company, page.getCompany());
    }

    /**
     * 初期化アクションを検証する.
     * SystemAdmin、ProjectAdminではない場合
     *
     * @throws Exception
     */
    @Test
    public void testNotSystemProjectAdminInitialize() throws Exception {
        //テストに必要なデータを作成
        page.setId(1L);

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
     * IDがない場合
     *
     * @throws Exception
     */
    @Test
    public void testNoneIdInitialize() throws Exception {
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
            fail("例外が発生していない");
        } catch (InvalidOperationRuntimeException e) {
            ServiceAbortException actual = (ServiceAbortException)e.getCause();
            assertEquals(ApplicationMessageCode.E_INVALID_PARAMETER, actual.getMessageCode());
        }
    }

    public static class MockCompanyService extends MockUp<CompanyServiceImpl> {
        static Company RET_FIND;

        @Mock
        public Company find(Long id) throws ServiceAbortException {
            return RET_FIND;
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
