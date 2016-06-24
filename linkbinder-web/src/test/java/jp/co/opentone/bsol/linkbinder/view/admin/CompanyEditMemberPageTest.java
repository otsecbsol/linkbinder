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

import java.util.ArrayList;
import java.util.List;

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
import jp.co.opentone.bsol.linkbinder.dto.Company;
import jp.co.opentone.bsol.linkbinder.dto.CompanyUser;
import jp.co.opentone.bsol.linkbinder.dto.ProjectUser;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchUserCondition;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.service.admin.impl.CompanyServiceImpl;
import jp.co.opentone.bsol.linkbinder.service.admin.impl.UserServiceImpl;
import jp.co.opentone.bsol.linkbinder.view.AbstractPage;
import mockit.Mock;
import mockit.MockUp;

/**
 * @author opentone
 */
public class CompanyEditMemberPageTest extends AbstractTestCase {

    /**
     * テスト対象クラス
     */
    @Resource
    private CompanyEditMemberPage page;

    /** USER00001 */
    private static final User USER00001 = new User();
    static {
        USER00001.setEmpNo("00001");
        USER00001.setNameE("Test Member");
    }

    /** USER00002 */
    private static final User USER00002 = new User();
    static {
        USER00002.setEmpNo("00002");
        USER00002.setNameE("Dummy Member");
    }

    /** USER00003 */
    private static final User USER00003 = new User();
    static {
        USER00003.setEmpNo("00003");
        USER00003.setNameE("Mock Member");
    }

    /** USER00011 */
    private static final User USER00011 = new User();
    static {
        USER00011.setEmpNo("00011");
        USER00011.setNameE("Test User");
    }

    /** USER00022 */
    private static final User USER00022 = new User();
    static {
        USER00022.setEmpNo("00022");
        USER00022.setNameE("Dummy User");
    }

    /** USER00033 */
    private static final User USER00033 = new User();
    static {
        USER00033.setEmpNo("00033");
        USER00033.setNameE("Mock User");
    }

    /**
     * テストの前準備.
     */
    @BeforeClass
    public static void testSetUp() {
        new MockAbstractPage();
        new MockUserService();
        new MockCompanyService();
        FacesContextMock.initialize();
    }

    /**
     * 後始末.
     */
    @AfterClass
    public static void testTeardown() {
        new MockAbstractPage().tearDown();
        new MockUserService().tearDown();
        new MockCompanyService().tearDown();
        FacesContextMock.tearDown();
    }

    @Before
    public void setup() {
        MockAbstractPage.RET_PROJID = "PJ1";
    }
    @After
    public void tearDown() {
        MockCompanyService.RET_FIND = null;
        MockCompanyService.RET_FIND_MEMBERS = null;
        MockCompanyService.RET_SAVE_COMPANY = null;
        MockCompanyService.RET_SAVE_USERS = null;
        MockUserService.RET_SEARCH = null;
        MockAbstractPage.IS_SYSTEM_ADMIN = false;
        MockAbstractPage.IS_PROJECT_ADMIN = false;
        MockAbstractPage.RET_PROJID = null;
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.view.admin.CompanyEditMemberPage#initialize()} のためのテスト・メソッド。
     */
    @Test
    public void testInitializeSystemAdmin() {
        Company expCompany = new Company();
        expCompany.setId(1L);
        expCompany.setCompanyCd("CompanyCode");
        expCompany.setName("CompnayName");

        List<CompanyUser> expCompanyUser = createCompanyUserList();

        List<ProjectUser> expProjectUser = createProjectUserList();

        MockCompanyService.RET_FIND = expCompany;
        MockCompanyService.RET_FIND_MEMBERS = expCompanyUser;
        MockUserService.RET_SEARCH = expProjectUser;
        MockAbstractPage.IS_SYSTEM_ADMIN = true;

        page.setId(1L);
        page.setBackPage("IndexPage");
        page.setInitialDisplaySuccess(false);

        page.initialize();

        assertTrue(page.isInitialDisplaySuccess());
        assertEquals(expCompany, page.getCompany());
        assertEquals(expCompanyUser, page.getMemberList());
        assertEquals(expProjectUser, page.getUserList());

        assertEquals("00011,00022,00033", page.getCandidateUserValue());
        assertEquals("00001,00002,00003", page.getSelectedMemberValue());
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.view.admin.CompanyEditMemberPage#initialize()} のためのテスト・メソッド。
     */
    @Test
    public void testInitializeProjectAdmin() {
        Company expCompany = new Company();
        expCompany.setId(1L);
        expCompany.setCompanyCd("CompanyCode");
        expCompany.setName("CompnayName");

        List<CompanyUser> expCompanyUser = createCompanyUserList();

        List<ProjectUser> expProjectUser = createProjectUserList();

        MockCompanyService.RET_FIND = expCompany;
        MockCompanyService.RET_FIND_MEMBERS = expCompanyUser;
        MockUserService.RET_SEARCH = expProjectUser;
        MockAbstractPage.IS_PROJECT_ADMIN = true;

        page.setId(1L);
        page.setBackPage("IndexPage");
        page.setInitialDisplaySuccess(false);

        page.initialize();

        assertTrue(page.isInitialDisplaySuccess());
        assertEquals(expCompany, page.getCompany());
        assertEquals(expCompanyUser, page.getMemberList());
        assertEquals(expProjectUser, page.getUserList());

        assertEquals("00011,00022,00033", page.getCandidateUserValue());
        assertEquals("00001,00002,00003", page.getSelectedMemberValue());
    }

    /**
     * {@link CompanyEditMemberPage#initialize}のテストケース.
     * 権限がない
     * @throws Exception
     */
    @Test
    public void testInitializeNoPermission() throws Exception {
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
     * {@link jp.co.opentone.bsol.linkbinder.view.admin.CompanyEditMemberPage#initialize()} のためのテスト・メソッド。
     * 遷移元から遷移元画面名がわたってきていない
     */
    @Test
    public void testInitializeBackPageError() {
        // 期待されるメッセージをセット
        FacesContextMock.EXPECTED_CLIENT_ID = null;
        FacesContextMock.EXPECTED_MESSAGE =
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                 FacesMessage.SEVERITY_ERROR.toString(),
                                 createExpectedMessageString(
                                     Messages.getMessageAsString(E_INVALID_PARAMETER),
                                     null));

        page.setId(1L);
        page.setBackPage(null);
        page.setInitialDisplaySuccess(false);
        MockAbstractPage.IS_SYSTEM_ADMIN = true;

        try {
            page.initialize();
            fail("例外が発生していない");
        } catch (InvalidOperationRuntimeException e) {
            ServiceAbortException actual = (ServiceAbortException)e.getCause();
            assertEquals(ApplicationMessageCode.E_INVALID_PARAMETER, actual.getMessageCode());
        }

        assertFalse(page.isInitialDisplaySuccess());
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.view.admin.CompanyEditMemberPage#save()} のためのテスト・メソッド。
     */
    @Test
    public void testSave() {
        Company expCompany = new Company();
        expCompany.setId(1L);
        expCompany.setCompanyCd("CompanyCode");
        expCompany.setName("CompnayName");

        List<CompanyUser> expCompanyUser = createCompanyUserList();

        List<ProjectUser> expProjectUser = createProjectUserList();

        // 再表示用
        MockCompanyService.RET_FIND = expCompany;
        MockCompanyService.RET_FIND_MEMBERS = expCompanyUser;
        MockUserService.RET_SEARCH = expProjectUser;

        // 期待されるメッセージをセット
        FacesContextMock.EXPECTED_MESSAGE =
            new FacesMessage(FacesMessage.SEVERITY_INFO,
                             FacesMessage.SEVERITY_INFO.toString(),
                             "保存が完了しました。");

        page.setId(1L);
        page.setBackPage("IndexPage");
        page.setInitialDisplaySuccess(false);
        page.setCompany(expCompany);
        page.setMemberList(expCompanyUser);
        page.setUserList(expProjectUser);
        page.setAllUserList(createAllUserList());
        page.setSelectedMemberValue("00001,00002,00011,00033");

        MockAbstractPage.IS_SYSTEM_ADMIN = true;

        page.save();

        List<User> expUserList = new ArrayList<User>();
        expUserList.add(USER00001);
        expUserList.add(USER00002);
        expUserList.add(USER00011);
        expUserList.add(USER00033);

        assertEquals(expCompany, MockCompanyService.RET_SAVE_COMPANY);
        assertEquals(expUserList, MockCompanyService.RET_SAVE_USERS);

        // 再表示
        assertTrue(page.isInitialDisplaySuccess());
        assertEquals(expCompany, page.getCompany());
        assertEquals(expCompanyUser, page.getMemberList());
        assertEquals(expProjectUser, page.getUserList());
        assertEquals("00011,00022,00033", page.getCandidateUserValue());
        assertEquals("00001,00002,00003", page.getSelectedMemberValue());
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.view.admin.CompanyEditMemberPage#back()} のためのテスト・メソッド。
     * 一覧ページへ戻る
     */
    @Test
    public void testBackIndex() {
        page.setId(1L);
        page.setBackPage("companyIndex");

        String actual = page.back();

        assertEquals("companyIndex", actual);
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.view.admin.CompanyEditMemberPage#back()} のためのテスト・メソッド。
     * 会社表示画面へ戻る
     */
    @Test
    public void testBackCompany() {
        page.setId(1L);
        page.setBackPage("company");

        String actual = page.back();

        assertEquals("company?id=1", actual);
    }

    /**
     * Memberデータの作成
     */
    private List<CompanyUser> createCompanyUserList() {
        List<CompanyUser> list = new ArrayList<CompanyUser>();
        CompanyUser cUser = new CompanyUser();
        cUser.setUser(USER00001);
        list.add(cUser);
        cUser = new CompanyUser();
        cUser.setUser(USER00002);
        list.add(cUser);
        cUser = new CompanyUser();
        cUser.setUser(USER00003);
        list.add(cUser);

        return list;
    }

    /**
     * Userデータの作成
     */
    private List<ProjectUser> createProjectUserList() {
        List<ProjectUser> list = new ArrayList<ProjectUser>();
        ProjectUser pUser = new ProjectUser();
        pUser.setUser(USER00011);
        list.add(pUser);;
        pUser = new ProjectUser();
        pUser.setUser(USER00022);
        list.add(pUser);
        pUser = new ProjectUser();
        pUser.setUser(USER00033);
        list.add(pUser);

        return list;
    }

    /**
     * All Userデータの作成
     */
    private List<User> createAllUserList() {
        List<User> list = new ArrayList<User>();
        list.add(USER00001);
        list.add(USER00002);
        list.add(USER00003);
        list.add(USER00011);
        list.add(USER00022);
        list.add(USER00033);

        return list;
    }

    public static class MockCompanyService extends MockUp<CompanyServiceImpl> {
        static Company RET_FIND;
        static List<CompanyUser> RET_FIND_MEMBERS;
        static Company RET_SAVE_COMPANY;
        static List<User> RET_SAVE_USERS;

        @Mock
        public Company find(Long id) throws ServiceAbortException {
            return RET_FIND;
        }

        @Mock
        public List<CompanyUser> findMembers(Long id) throws ServiceAbortException {
            return RET_FIND_MEMBERS;
        }

        @Mock
        public void saveMembers(Company company, List<User> users)throws ServiceAbortException {
            RET_SAVE_COMPANY = company;
            RET_SAVE_USERS = users;
        }
    }

    public static class MockUserService extends MockUp<UserServiceImpl> {
        static List<ProjectUser> RET_SEARCH;

        @Mock
        public List<ProjectUser> search(SearchUserCondition condition) throws ServiceAbortException {
            return RET_SEARCH;
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
