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
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroup;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroupUser;
import jp.co.opentone.bsol.linkbinder.dto.Discipline;
import jp.co.opentone.bsol.linkbinder.dto.ProjectUser;
import jp.co.opentone.bsol.linkbinder.dto.SearchCorresponGroupResult;
import jp.co.opentone.bsol.linkbinder.dto.Site;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponGroupCondition;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchUserCondition;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.service.admin.impl.CorresponGroupServiceImpl;
import jp.co.opentone.bsol.linkbinder.service.admin.impl.SiteServiceImpl;
import jp.co.opentone.bsol.linkbinder.service.admin.impl.UserServiceImpl;
import jp.co.opentone.bsol.linkbinder.view.AbstractPage;
import mockit.Mock;
import mockit.MockUp;

/**
 * {@link GroupEditPage}のテストケース
 * @author opentone
 */
public class GroupEditPageTest extends AbstractTestCase {

    /**
     * テスト対象クラス
     */
    @Resource
    private GroupEditPage page;

    /**
     * テストの前準備.
     */
    @BeforeClass
    public static void testSetUp() {
        FacesContextMock.initialize();
        new MockAbstractPage();
        new MockSiteService();
        new MockCorresponGroupService();
        new MockUserService();
    }

    /**
     * 後始末.
     */
    @AfterClass
    public static void testTeardown() {
        new MockAbstractPage().tearDown();
        new MockSiteService().tearDown();
        new MockCorresponGroupService().tearDown();
        new MockUserService().tearDown();
        FacesContextMock.tearDown();
    }

    @Before
    public void setUp() {

    }

    @After
    public void tearDown() {
        MockAbstractPage.RET_PROJID = null;
        MockAbstractPage.IS_SYSTEM_ADMIN = false;
        MockAbstractPage.IS_PROJECT_ADMIN = false;
        MockAbstractPage.IS_GROUP_ADMIN = false;
        MockSiteService.RET_FIND = null;
        MockCorresponGroupService.CRT_SAVE_CORRESPON_GROUP = null;
        MockCorresponGroupService.CRT_SAVE_USERS = null;
        MockCorresponGroupService.RET_FIND = null;
        MockCorresponGroupService.RET_SEARCH_PAGING_LIST = null;
        MockUserService.RET_SEARCH = null;

        FacesContextMock.EXPECTED_CLIENT_ID = null;
        FacesContextMock.EXPECTED_MESSAGE = null;
    }

    /**
     * 初期化アクションを検証する.
     * SystemAdmin.
     * @throws Exception
     */
    @Test
    public void testInitializeSystemAdmin() throws Exception {
        // テストに必要なデータを作成
        page.setId(1L);
        page.setBackPage("groupIndex");

        List<CorresponGroupUser> cgu = createCorresponGroupUserListInitialize();
        MockCorresponGroupService.RET_FIND_MEMBERS = cgu;
        List<ProjectUser> lpu = createProjectUserListInitialize();
        MockUserService.RET_SEARCH = lpu;

        MockAbstractPage.RET_PROJID = "9-9999-9";
        MockAbstractPage.IS_SYSTEM_ADMIN = true;

        page.initialize();

        assertTrue(page.isInitialDisplaySuccess());

        assertTrue(1 == page.getAdminList().size());
        assertTrue(3 == page.getMemberList().size());
        assertTrue(2 == page.getUserList().size());

        assertEquals(cgu.get(3).getUser(), page.getAdminList().get(0).getUser());

        assertEquals(cgu.get(0).getUser(), page.getMemberList().get(0).getUser());
        assertEquals(cgu.get(1).getUser(), page.getMemberList().get(1).getUser());
        assertEquals(cgu.get(2).getUser(), page.getMemberList().get(2).getUser());

        assertEquals(lpu.get(0).getUser(), page.getUserList().get(0));
        assertEquals(lpu.get(1).getUser(), page.getUserList().get(1));

    }

    /**
     * 初期化アクションを検証する.
     * ProjectAdmin.
     * @throws Exception
     */
    @Test
    public void testInitializeProjectAdmin() throws Exception {
        // テストに必要なデータを作成
        page.setId(1L);
        page.setBackPage("groupIndex");

        List<CorresponGroupUser> cgu = createCorresponGroupUserListInitialize();
        MockCorresponGroupService.RET_FIND_MEMBERS = cgu;
        List<ProjectUser> lpu = createProjectUserListInitialize();
        MockUserService.RET_SEARCH = lpu;

        MockAbstractPage.RET_PROJID = "9-9999-9";
        MockAbstractPage.IS_PROJECT_ADMIN = true;

        page.initialize();

        assertTrue(page.isInitialDisplaySuccess());

        assertTrue(1 == page.getAdminList().size());
        assertTrue(3 == page.getMemberList().size());
        assertTrue(2 == page.getUserList().size());

        assertEquals(cgu.get(3).getUser(), page.getAdminList().get(0).getUser());

        assertEquals(cgu.get(0).getUser(), page.getMemberList().get(0).getUser());
        assertEquals(cgu.get(1).getUser(), page.getMemberList().get(1).getUser());
        assertEquals(cgu.get(2).getUser(), page.getMemberList().get(2).getUser());

        assertEquals(lpu.get(0).getUser(), page.getUserList().get(0));
        assertEquals(lpu.get(1).getUser(), page.getUserList().get(1));

    }

    /**
     * 初期化アクションを検証する.
     * GroupAdmin.
     * @throws Exception
     */
    @Test
    public void testInitializeGroupAdmin() throws Exception {
        // テストに必要なデータを作成
        page.setId(1L);
        page.setBackPage("groupIndex");

        List<CorresponGroupUser> cgu = createCorresponGroupUserListInitialize();
        MockCorresponGroupService.RET_FIND_MEMBERS = cgu;
        List<ProjectUser> lpu = createProjectUserListInitialize();
        MockUserService.RET_SEARCH = lpu;

        MockAbstractPage.RET_PROJID = "9-9999-9";
        MockAbstractPage.IS_GROUP_ADMIN = true;

        page.initialize();

        assertTrue(page.isInitialDisplaySuccess());

        assertTrue(1 == page.getAdminList().size());
        assertTrue(3 == page.getMemberList().size());
        assertTrue(2 == page.getUserList().size());

        assertEquals(cgu.get(3).getUser(), page.getAdminList().get(0).getUser());

        assertEquals(cgu.get(0).getUser(), page.getMemberList().get(0).getUser());
        assertEquals(cgu.get(1).getUser(), page.getMemberList().get(1).getUser());
        assertEquals(cgu.get(2).getUser(), page.getMemberList().get(2).getUser());

        assertEquals(lpu.get(0).getUser(), page.getUserList().get(0));
        assertEquals(lpu.get(1).getUser(), page.getUserList().get(1));

    }

    /**
     * 初期化アクションを検証する.
     * 権限エラー.
     * @throws Exception
     */
    @Test
    public void testInitializeInvalidPermission() throws Exception {
        page.setId(1L);
        page.setBackPage("groupIndex");
        // テストに必要なデータを作成
        // 権限がない
        MockAbstractPage.IS_SYSTEM_ADMIN = false;
        MockAbstractPage.IS_PROJECT_ADMIN = false;
        MockAbstractPage.IS_GROUP_ADMIN = false;

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
     * 保存アクションを検証する.
     * @throws Exception
     */
    @Test
    public void testSave() throws Exception {
        // テストに必要なデータを作成
        User loginUser = new User();
        loginUser.setEmpNo("ZZA04");

        CorresponGroup cg = createCorresponGroup(loginUser);

        page.setCorresponGroup(cg);

        List<User> users = createUserList();

        page.setUserList(users);

        page.setId(1L);
        page.setBackPage("groupIndex");

        List<CorresponGroupUser> cgu = createCorresponGroupUserListSave(loginUser);

        MockCorresponGroupService.RET_FIND_MEMBERS = cgu;

        List<ProjectUser> lpu = createProjectUserListSave();

        MockUserService.RET_SEARCH = lpu;

        MockAbstractPage.RET_PROJID = "9-9999-9";
        MockAbstractPage.IS_PROJECT_ADMIN = true;

        page.setSelectedAdminValue("ZZA03,ZZA04");
        page.setSelectedMemberValue("ZZA01,ZZA02");

        // 期待されるメッセージをセット
        FacesContextMock.EXPECTED_CLIENT_ID = null;
        FacesContextMock.EXPECTED_MESSAGE =
                new FacesMessage(
                    FacesMessage.SEVERITY_INFO, FacesMessage.SEVERITY_INFO.toString(),
                    createExpectedMessageString(
                        Messages.getMessageAsString(SAVE_SUCCESSFUL),
                        null));

        page.save();

        CorresponGroup actCg = MockCorresponGroupService.CRT_SAVE_CORRESPON_GROUP;
        List<User> actLu = MockCorresponGroupService.CRT_SAVE_USERS;

        assertEquals(4, actLu.size());
        assertEquals(users.get(0).toString(), actLu.get(2).toString());
        assertEquals(users.get(1).toString(), actLu.get(3).toString());
        assertEquals(users.get(2).toString(), actLu.get(0).toString());
        assertEquals(users.get(3).toString(), actLu.get(1).toString());
        assertEquals(cg.toString(), actCg.toString());

    }

    /**
     * 保存アクションを検証する.
     * 管理者が空.
     * @throws Exception
     */
    @Test
    public void testSaveNoAdminUser() throws Exception {
        // テストに必要なデータを作成
        User loginUser = new User();
        loginUser.setEmpNo("ZZA04");

        CorresponGroup cg = createCorresponGroup(loginUser);

        page.setCorresponGroup(cg);

        List<User> users = createUserList();

        page.setUserList(users);

        page.setId(1L);
        page.setBackPage("groupIndex");

        List<CorresponGroupUser> cgu = createCorresponGroupUserListSave(loginUser);

        MockCorresponGroupService.RET_FIND_MEMBERS = cgu;

        List<ProjectUser> lpu = createProjectUserListSave();

        MockUserService.RET_SEARCH = lpu;

        MockAbstractPage.RET_PROJID = "9-9999-9";
        MockAbstractPage.IS_PROJECT_ADMIN = true;

        page.setSelectedAdminValue(""); // 管理者が空
        page.setSelectedMemberValue("ZZA01,ZZA02");

        // 期待されるメッセージをセット
        FacesContextMock.EXPECTED_CLIENT_ID = null;
        FacesContextMock.EXPECTED_MESSAGE =
                new FacesMessage(
                    FacesMessage.SEVERITY_INFO, FacesMessage.SEVERITY_INFO.toString(),
                    createExpectedMessageString(
                        Messages.getMessageAsString(SAVE_SUCCESSFUL),
                        null));

        page.save();

        CorresponGroup actCg = MockCorresponGroupService.CRT_SAVE_CORRESPON_GROUP;
        List<User> actLu = MockCorresponGroupService.CRT_SAVE_USERS;

        assertEquals(2, actLu.size());
        assertEquals(users.get(0).toString(), actLu.get(0).toString());
        assertEquals(users.get(1).toString(), actLu.get(1).toString());
        assertEquals(cg.toString(), actCg.toString());

    }

    /**
     * 保存アクションを検証する.
     * 一般ユーザーが空.
     * @throws Exception
     */
    @Test
    public void testSaveNoMemberUser() throws Exception {
        // テストに必要なデータを作成
        User loginUser = new User();
        loginUser.setEmpNo("ZZA04");

        CorresponGroup cg = createCorresponGroup(loginUser);

        page.setCorresponGroup(cg);

        List<User> users = createUserList();

        page.setUserList(users);

        page.setId(1L);
        page.setBackPage("groupIndex");

        List<CorresponGroupUser> cgu = createCorresponGroupUserListSave(loginUser);

        MockCorresponGroupService.RET_FIND_MEMBERS = cgu;

        List<ProjectUser> lpu = createProjectUserListSave();

        MockUserService.RET_SEARCH = lpu;

        MockAbstractPage.RET_PROJID = "9-9999-9";
        MockAbstractPage.IS_PROJECT_ADMIN = true;

        page.setSelectedAdminValue("ZZA03,ZZA04");
        page.setSelectedMemberValue(""); // 一般ユーザーが空

        // 期待されるメッセージをセット
        FacesContextMock.EXPECTED_CLIENT_ID = null;
        FacesContextMock.EXPECTED_MESSAGE =
                new FacesMessage(
                    FacesMessage.SEVERITY_INFO, FacesMessage.SEVERITY_INFO.toString(),
                    createExpectedMessageString(
                        Messages.getMessageAsString(SAVE_SUCCESSFUL),
                        null));

        page.save();

        CorresponGroup actCg = MockCorresponGroupService.CRT_SAVE_CORRESPON_GROUP;
        List<User> actLu = MockCorresponGroupService.CRT_SAVE_USERS;

        assertEquals(2, actLu.size());
        assertEquals(users.get(2).toString(), actLu.get(0).toString());
        assertEquals(users.get(3).toString(), actLu.get(1).toString());
        assertEquals(cg.toString(), actCg.toString());

    }


    /**
     * 保存アクションを検証する.
     * 管理者も一般ユーザーも空.
     * @throws Exception
     */
    @Test
    public void testSaveNoUser() throws Exception {
        // テストに必要なデータを作成
        User loginUser = new User();
        loginUser.setEmpNo("ZZA04");

        CorresponGroup cg = createCorresponGroup(loginUser);

        page.setCorresponGroup(cg);

        List<User> users = createUserList();

        page.setUserList(users);

        page.setId(1L);
        page.setBackPage("groupIndex");

        List<CorresponGroupUser> cgu = createCorresponGroupUserListSave(loginUser);

        MockCorresponGroupService.RET_FIND_MEMBERS = cgu;

        List<ProjectUser> lpu = createProjectUserListSave();

        MockUserService.RET_SEARCH = lpu;

        MockAbstractPage.RET_PROJID = "9-9999-9";
        MockAbstractPage.IS_PROJECT_ADMIN = true;

        page.setSelectedAdminValue(""); // 管理者が空
        page.setSelectedMemberValue(""); // 一般ユーザーが空

        // 期待されるメッセージをセット
        FacesContextMock.EXPECTED_CLIENT_ID = null;
        FacesContextMock.EXPECTED_MESSAGE =
                new FacesMessage(
                    FacesMessage.SEVERITY_INFO, FacesMessage.SEVERITY_INFO.toString(),
                    createExpectedMessageString(
                        Messages.getMessageAsString(SAVE_SUCCESSFUL),
                        null));

        page.save();

        CorresponGroup actCg = MockCorresponGroupService.CRT_SAVE_CORRESPON_GROUP;
        List<User> actLu = MockCorresponGroupService.CRT_SAVE_USERS;

        assertEquals(0, actLu.size());
        assertEquals(cg.toString(), actCg.toString());

    }

    private List<CorresponGroupUser> createCorresponGroupUserListInitialize() {
        User loginUser = new User();
        loginUser.setEmpNo("ZZA02");
        loginUser.setNameE("Tetsuo Aoki");

        List<CorresponGroupUser> cgu = new ArrayList<CorresponGroupUser>();

        CorresponGroupUser gu = new CorresponGroupUser();

        // -------------------------------------------------
        gu.setId(1L);

        CorresponGroup coreGro = new CorresponGroup();
        coreGro.setId(1L);
        coreGro.setName("YOC:PIPING");

        MockCorresponGroupService.RET_FIND = coreGro;

        gu.setCorresponGroup(coreGro);

        User user = new User();
        user.setEmpNo("ZZA04");
        user.setNameE("Tomoko Okada");

        gu.setUser(user);
        gu.setSecurityLevel("40");

        gu.setCreatedBy(loginUser);
        gu.setUpdatedBy(loginUser);
        gu.setDeleteNo(0L);

        cgu.add(gu);

        // -------------------------------------------------
        gu = new CorresponGroupUser();
        gu.setId(2L);
        gu.setCorresponGroup(coreGro);

        user = new User();
        user.setEmpNo("ZZA05");
        user.setNameE("Keiichi Ogiwara");

        gu.setUser(user);
        gu.setSecurityLevel("40");
        gu.setCreatedBy(loginUser);
        gu.setUpdatedBy(loginUser);
        gu.setDeleteNo(0L);

        cgu.add(gu);
        // -------------------------------------------------
        gu = new CorresponGroupUser();
        gu.setId(3L);
        gu.setCorresponGroup(coreGro);

        user = new User();
        user.setEmpNo("ZZA01");
        user.setNameE("Tetsu Aoki");

        gu.setUser(user);
        gu.setSecurityLevel("40");
        gu.setCreatedBy(loginUser);
        gu.setUpdatedBy(loginUser);
        gu.setDeleteNo(0L);

        cgu.add(gu);

        // -------------------------------------------------
        gu = new CorresponGroupUser();
        gu.setId(4L);
        gu.setCorresponGroup(coreGro);

        user = new User();
        user.setEmpNo("ZZA03");
        user.setNameE("Atsushi Ishda");

        gu.setUser(user);
        gu.setSecurityLevel("30");
        gu.setCreatedBy(loginUser);
        gu.setUpdatedBy(loginUser);
        gu.setDeleteNo(0L);

        cgu.add(gu);
        // -------------------------------------------------

        return cgu;
    }

    private  List<ProjectUser> createProjectUserListInitialize() {
        List<ProjectUser> lpu = new ArrayList<ProjectUser>();

        // ------------------------------------------------

        ProjectUser pu = new ProjectUser();
        pu.setProjectId("PJ1");

        User projectUser = new User();
        projectUser.setEmpNo("ZZA06");
        projectUser.setNameE("Shinobu Miyabe");
        projectUser.setSecurityLevel("40");

        pu.setUser(projectUser);

        lpu.add(pu);

        // ------------------------------------------------

        pu = new ProjectUser();
        pu.setProjectId("PJ1");

        projectUser = new User();
        projectUser.setEmpNo("ZZA07");
        projectUser.setNameE("Takeshi Sugayama");
        projectUser.setSecurityLevel("40");

        pu.setUser(projectUser);
        lpu.add(pu);

        // ------------------------------------------------

        pu = new ProjectUser();
        pu.setProjectId("PJ1");

        projectUser = new User();
        projectUser.setEmpNo("ZZA03");
        projectUser.setNameE("Atsushi Ishda");
        projectUser.setSecurityLevel("40");

        pu.setUser(projectUser);
        lpu.add(pu);

        // ------------------------------------------------

        return lpu;
    }

    private CorresponGroup createCorresponGroup(User loginUser) {
        CorresponGroup cg = new CorresponGroup();
        cg.setId(1L);

        Site site = new Site();
        site.setId(1L);
        site.setSiteCd("YOC");
        site.setName("Yocohama");
        site.setCreatedBy(loginUser);
        site.setUpdatedBy(loginUser);
        site.setVersionNo(1L);
        site.setDeleteNo(0L);

        cg.setSite(site);

        Discipline dis = new Discipline();
        dis.setId(1L);

        cg.setDiscipline(dis);
        cg.setName("YOC:IT");

        cg.setCreatedBy(loginUser);
        cg.setUpdatedBy(loginUser);
        cg.setVersionNo(1L);
        cg.setDeleteNo(0L);

        return cg;
    }

    private List<User> createUserList() {
        List<User> users = new ArrayList<User>();
        User user = new User();
        user.setEmpNo("ZZA01");
        user.setNameE("Taro Yamada");
        user.setSecurityLevel("40");

        users.add(user);

        user = new User();
        user.setEmpNo("ZZA02");
        user.setNameE("Tetsuo Aoki");
        user.setSecurityLevel("40");

        users.add(user);

        user = new User();
        user.setEmpNo("ZZA03");
        user.setNameE("Atsushi Isida");
        user.setSecurityLevel("30");

        users.add(user);

        user = new User();
        user.setEmpNo("ZZA04");
        user.setNameE("Tomokoi Okada");
        user.setSecurityLevel("10");

        users.add(user);

        return users;
    }

    private List<CorresponGroupUser> createCorresponGroupUserListSave(User loginUser) {
        List<CorresponGroupUser> cgu = new ArrayList<CorresponGroupUser>();

        CorresponGroupUser gu = new CorresponGroupUser();

        // -------------------------------------------------
        gu.setId(1L);

        CorresponGroup coreGro = new CorresponGroup();
        coreGro.setId(1L);
        coreGro.setName("YOC:PIPING");

        MockCorresponGroupService.RET_FIND = coreGro;

        gu.setCorresponGroup(coreGro);

        User initUser = new User();
        initUser.setEmpNo("ZZA01");
        initUser.setNameE("Taro Yamada");

        gu.setUser(initUser);
        gu.setSecurityLevel("40");

        gu.setCreatedBy(loginUser);
        gu.setUpdatedBy(loginUser);
        gu.setDeleteNo(0L);

        cgu.add(gu);

        // -------------------------------------------------
        gu = new CorresponGroupUser();
        gu.setId(2L);
        gu.setCorresponGroup(coreGro);

        initUser = new User();
        initUser.setEmpNo("ZZA02");
        initUser.setNameE("Tetsuo Aoki");

        gu.setUser(initUser);
        gu.setSecurityLevel("40");
        gu.setCreatedBy(loginUser);
        gu.setUpdatedBy(loginUser);
        gu.setDeleteNo(0L);

        cgu.add(gu);
        // -------------------------------------------------
        gu = new CorresponGroupUser();
        gu.setId(3L);
        gu.setCorresponGroup(coreGro);

        initUser = new User();
        initUser.setEmpNo("ZZA03");
        initUser.setNameE("Atsushi Isida");

        gu.setUser(initUser);
        gu.setSecurityLevel("30");
        gu.setCreatedBy(loginUser);
        gu.setUpdatedBy(loginUser);
        gu.setDeleteNo(0L);

        cgu.add(gu);

        // -------------------------------------------------
        gu = new CorresponGroupUser();
        gu.setId(4L);
        gu.setCorresponGroup(coreGro);

        User user = new User();
        user.setEmpNo("ZZA04");
        user.setNameE("Tomoko Okada");

        gu.setUser(user);
        gu.setSecurityLevel("10");
        gu.setCreatedBy(loginUser);
        gu.setUpdatedBy(loginUser);
        gu.setDeleteNo(0L);

        cgu.add(gu);
        // -------------------------------------------------
        return cgu;
    }

    private List<ProjectUser> createProjectUserListSave() {
        List<ProjectUser> lpu = new ArrayList<ProjectUser>();

        // ------------------------------------------------

        ProjectUser pu = new ProjectUser();
        pu.setProjectId("PJ1");

        User projectUser = new User();
        projectUser.setEmpNo("ZZA06");
        projectUser.setNameE("Shinobu Miyabe");
        projectUser.setSecurityLevel("40");

        pu.setUser(projectUser);

        lpu.add(pu);

        // ------------------------------------------------

        pu = new ProjectUser();
        pu.setProjectId("PJ1");

        projectUser = new User();
        projectUser.setEmpNo("ZZA07");
        projectUser.setNameE("Takeshi Sugayama");
        projectUser.setSecurityLevel("40");

        pu.setUser(projectUser);
        lpu.add(pu);

        // ------------------------------------------------

        pu = new ProjectUser();
        pu.setProjectId("PJ1");

        projectUser = new User();
        projectUser.setEmpNo("ZZA03");
        projectUser.setNameE("Atsushi Ishda");
        projectUser.setSecurityLevel("40");

        pu.setUser(projectUser);
        lpu.add(pu);

        // ------------------------------------------------

        pu = new ProjectUser();
        pu.setProjectId("PJ1");

        projectUser = new User();
        projectUser.setEmpNo("ZZA05");
        projectUser.setNameE("Keiichi Ogiwara");
        projectUser.setSecurityLevel("40");

        pu.setUser(projectUser);
        lpu.add(pu);

        // ------------------------------------------------

        return lpu;
    }

    public static class MockAbstractPage extends MockUp<AbstractPage> {
        static String RET_PROJID;
        static boolean IS_SYSTEM_ADMIN;
        static boolean IS_PROJECT_ADMIN;
//        static boolean IS_ANY_GROUP_ADMIN;
        static boolean IS_GROUP_ADMIN;

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

        @Mock
        public boolean isGroupAdmin(Long groupId) {
            return IS_GROUP_ADMIN;
        }
    }

    public static class MockSiteService extends MockUp<SiteServiceImpl> {
        static Site RET_FIND;

        @Mock
        public Site find(Long id) throws ServiceAbortException {
            return RET_FIND;
        }
    }

    public static class MockCorresponGroupService extends MockUp<CorresponGroupServiceImpl> {
        static SearchCorresponGroupResult RET_SEARCH_PAGING_LIST;
        static CorresponGroup RET_FIND;
        static CorresponGroup CRT_SAVE_CORRESPON_GROUP;
        static List<User> CRT_SAVE_USERS;
        static List<CorresponGroupUser> RET_FIND_MEMBERS;

        @Mock
        public SearchCorresponGroupResult searchPagingList(SearchCorresponGroupCondition condition)
            throws ServiceAbortException {
            return RET_SEARCH_PAGING_LIST;
        }

        @Mock
        public CorresponGroup find(Long id) throws ServiceAbortException {
            return RET_FIND;
        }

        @Mock
        public void save(CorresponGroup corresponGroup, List<User> users)
            throws ServiceAbortException {
            CRT_SAVE_CORRESPON_GROUP = corresponGroup;
            CRT_SAVE_USERS = users;
        }

        @Mock
        public List<CorresponGroupUser> findMembers(Long id) throws ServiceAbortException {
            return RET_FIND_MEMBERS;
        }

    }

    public static class MockUserService extends MockUp<UserServiceImpl> {
        static List<ProjectUser> RET_SEARCH;

        @Mock
        public List<ProjectUser> search(SearchUserCondition condition) throws ServiceAbortException {
            return RET_SEARCH;
        }
    }

}
