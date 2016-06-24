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
package jp.co.opentone.bsol.linkbinder.service;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import jp.co.opentone.bsol.framework.core.ProcessContext;
import jp.co.opentone.bsol.framework.core.config.SystemConfig;
import jp.co.opentone.bsol.framework.extension.jsf.FacesContextMock;
import jp.co.opentone.bsol.framework.test.AbstractTestCase;
import jp.co.opentone.bsol.linkbinder.Constants;
import jp.co.opentone.bsol.linkbinder.dao.impl.CorresponGroupDaoImpl;
import jp.co.opentone.bsol.linkbinder.dao.impl.CorresponGroupUserDaoImpl;
import jp.co.opentone.bsol.linkbinder.dao.impl.UserDaoImpl;
import jp.co.opentone.bsol.linkbinder.dto.AddressCorresponGroup;
import jp.co.opentone.bsol.linkbinder.dto.Correspon;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroup;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroupUser;
import jp.co.opentone.bsol.linkbinder.dto.Project;
import jp.co.opentone.bsol.linkbinder.dto.ProjectUser;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponGroupCondition;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchUserCondition;
import mockit.Invocation;
import mockit.Mock;
import mockit.MockUp;

public class UserPermissionHelperTest extends AbstractTestCase {

    /**
     * テスト対象
     */
    @Resource
    UserPermissionHelper helper;

    @Resource
    User currentUser;

    Project currentProject;

    /**
     * テストの前準備.
     */
    @BeforeClass
    public static void testSetUp() {
        FacesContextMock.initialize();
        new MockProjectUser();
        new MockProcessContext();
        new MockUserDao();
        new MockCorresponGroupUserDao();
        new MockCorresponGroupUser();
        new MockCorresponGroupDao();
    }

    /**
     * 後始末.
     */
    @AfterClass
    public static void testTeardown() {
        new MockProjectUser().tearDown();
        new MockProcessContext().tearDown();
        new MockUserDao().tearDown();
        new MockCorresponGroupUserDao().tearDown();
        new MockCorresponGroupUser().tearDown();
        new MockCorresponGroupDao().tearDown();
        FacesContextMock.tearDown();
    }

    @Before
    public void setup() {
        currentProject = new Project();
        currentProject.setProjectId("PJ1");

        MockProcessContext.RET_USER = null;
        Map<String, Object> values = new HashMap<String, Object>();
        values.put(Constants.KEY_PROJECT, currentProject);
        MockProcessContext.RET_VALUE = values;
    }

    /**
     * テスト後始末.
     */
    @After
    public void tearDown() {
        MockProjectUser.RET_PROJECT_ADMIN_FLG = null;
        MockProcessContext.RET_VALUE = null;
        MockUserDao.RET_PROJECT_USER = null;
        MockCorresponGroupUserDao.CHECK_ID = null;
        MockCorresponGroupUserDao.RET_GROUP_USER = null;
        MockCorresponGroupUserDao.RET_GROUP_USERS = null;
        MockCorresponGroupUser.RET_SECURITY_LEVEL = null;
        MockCorresponGroupDao.RET_FIND = null;

        MockProcessContext.RET_USER = null;
        MockProcessContext.RET_VALUE = null;

        currentUser.setEmpNo(null);
        currentUser.setSysAdminFlg(null);
    }

    /**
     * System Adminの判定テスト.
     */
    @Test
    public void testSystemAdmin() {
        // SystemAdmin
        User user = new User();
        user.setEmpNo("ZZA01");

        user.setSysAdminFlg("X");
        assertTrue(helper.isSystemAdmin(user));

        // SystemAdminではない
        user.setSysAdminFlg(null);
        assertFalse(helper.isSystemAdmin(user));
    }

    /**
     * System Adminの判定テスト. 引数Null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSystemAdminNull() {
        helper.isSystemAdmin(null);

        fail("例外が発生していない");
    }

    /**
     * Project Adminの判定テスト. ユーザ = ログインユーザ.
     */
    @Test
    public void testProjectAdmin1() {
        String projectId = "PJ1";
        String empNo = "80001";

        currentUser.setEmpNo(empNo);

        User user = new User();
        user.setEmpNo(empNo);
        ProjectUser pUser = new ProjectUser();
        pUser.setProjectId(projectId);
        pUser.setUser(user);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put(Constants.KEY_PROJECT_USER, pUser);
        MockProcessContext.RET_VALUE = map;

        MockProjectUser.RET_PROJECT_ADMIN_FLG = "X";

        assertTrue(helper.isProjectAdmin(user, projectId));

        MockProjectUser.RET_PROJECT_ADMIN_FLG = null;

        assertFalse(helper.isProjectAdmin(user, projectId));
    }

    /**
     * Project Adminの判定テスト. ユーザ != ログインユーザ.
     */
    @Test
    public void testProjectAdmin2() {
        String projectId = "PJ1";
        String empNo = "80001";

        currentUser.setEmpNo(empNo);

        User user = new User();
        user.setEmpNo(empNo);
        ProjectUser pUser = new ProjectUser();
        pUser.setProjectId(projectId);
        pUser.setUser(user);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put(Constants.KEY_PROJECT_USER, pUser);
        MockProcessContext.RET_VALUE = map;

        MockProjectUser.RET_PROJECT_ADMIN_FLG = "X";

        assertTrue(helper.isProjectAdmin(user, projectId));

        MockProjectUser.RET_PROJECT_ADMIN_FLG = null;

        assertFalse(helper.isProjectAdmin(user, projectId));
    }

    /**
     * Project Adminの判定テスト. 第一引数Null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testProjectAdminNull_1() {
        String projectId = "PJ1";
        helper.isProjectAdmin(null, projectId);
    }

    /**
     * Project Adminの判定テスト. 第二引数Null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testProjectAdminNull_2() {
        User user = new User();
        user.setEmpNo("80001");

        helper.isProjectAdmin(user, null);
    }

    /**
     * Group Adminの判定テスト.
     */
    @Test
    public void testGroupAdmin() {
//        Mockit.restoreOriginalDefinition(User.class);

        User user = new User();
        user.setEmpNo("ZZA01");
        Long corresponGroupId = 1L;

        List<CorresponGroupUser> users = new ArrayList<CorresponGroupUser>();
        CorresponGroupUser groupUser;
        CorresponGroup g;
        groupUser = new CorresponGroupUser();
        groupUser.setUser(user);
        g = new CorresponGroup();
        g.setId(corresponGroupId);
        groupUser.setCorresponGroup(g);
        users.add(groupUser);

        groupUser = new CorresponGroupUser();
        user = new User();
        user.setEmpNo("ZZA99");
        groupUser.setUser(user);
        g = new CorresponGroup();
        g.setId(corresponGroupId);
        groupUser.setCorresponGroup(g);
        users.add(groupUser);

        MockProcessContext.RET_USER = user;
        MockCorresponGroupUserDao.RET_GROUP_USERS = users;
        MockCorresponGroupUserDao.CHECK_ID = corresponGroupId;

        MockCorresponGroupUser.RET_SECURITY_LEVEL = "30";

        assertTrue(helper.isGroupAdmin(user, corresponGroupId));

        MockCorresponGroupUser.RET_SECURITY_LEVEL = "40";

        assertFalse(helper.isGroupAdmin(user, corresponGroupId));
    }

    /**
     * Group Adminの判定テスト. 第一引数Null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGroupAdminNull_1() {
        Long corresponGroupId = 1L;

        helper.isGroupAdmin(null, corresponGroupId);

        fail("例外が発生していない");
    }

    /**
     * Group Adminの判定テスト. 第二引数Null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGroupAdminNull_2() {
        User user = new User();

        helper.isGroupAdmin(user, null);

        fail("例外が発生していない");
    }

    /**
     * コレポン文書の送信元/宛先に、ログインユーザーがDisicpline Adminの権限を持っているかの判定テスト. 送信元に権限を持っている.
     */
    @Test
    public void testAnyGroupAdminFrom() {
        Long corresponGroupId = 1L;

        CorresponGroupUser groupUser = new CorresponGroupUser();
        MockCorresponGroupUserDao.RET_GROUP_USER = groupUser;
        MockCorresponGroupUserDao.CHECK_ID = corresponGroupId;

        MockCorresponGroupUser.RET_SECURITY_LEVEL = "30";

        Correspon correspon = new Correspon();
        CorresponGroup group = new CorresponGroup();
        group.setId(corresponGroupId); // 一致しないIDを入れる
        correspon.setFromCorresponGroup(group);
        List<AddressCorresponGroup> list = new ArrayList<AddressCorresponGroup>();
        AddressCorresponGroup aGroup = new AddressCorresponGroup();
        group = new CorresponGroup();
        group.setId(99L); // 一致するIDを入れる
        aGroup.setCorresponGroup(group);
        list.add(aGroup);
        correspon.setAddressCorresponGroups(list);

        helper.isAnyGroupAdmin(correspon);
    }

    /**
     * コレポン文書の送信元/宛先に、ログインユーザーがDisicpline Adminの権限を持っているかの判定テスト. 宛先に権限を持っている.
     */
    @Test
    public void testAnyGroupAdminTo() {
        Long corresponGroupId = 1L;

        CorresponGroupUser groupUser = new CorresponGroupUser();
        MockCorresponGroupUserDao.RET_GROUP_USER = groupUser;
        MockCorresponGroupUserDao.CHECK_ID = corresponGroupId;

        MockCorresponGroupUser.RET_SECURITY_LEVEL = "30";

        Correspon correspon = new Correspon();
        CorresponGroup group = new CorresponGroup();
        group.setId(99L); // 一致しないIDを入れる
        correspon.setFromCorresponGroup(group);
        List<AddressCorresponGroup> list = new ArrayList<AddressCorresponGroup>();
        AddressCorresponGroup aGroup = new AddressCorresponGroup();
        group = new CorresponGroup();
        group.setId(corresponGroupId); // 一致するIDを入れる
        aGroup.setCorresponGroup(group);
        list.add(aGroup);
        correspon.setAddressCorresponGroups(list);

        helper.isAnyGroupAdmin(correspon);
    }

    /**
     * コレポン文書の送信元/宛先に、ログインユーザーがDisicpline Adminの権限を持っているかの判定テスト. 引数Null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAnyGroupAdminNull() {
        Correspon correspon = null;
        helper.isAnyGroupAdmin(correspon);

        fail("例外が発生していない");
    }

    /**
     * isAnyGroupAdmin(String)の判定テスト.
     */
    @Test
    public void testAnyGroupAdminString() {
        Long corresponGroupId = 1L;

        CorresponGroupUser groupUser = new CorresponGroupUser();
        MockCorresponGroupUserDao.RET_GROUP_USER = groupUser;
        MockCorresponGroupUserDao.CHECK_ID = corresponGroupId;

        List<CorresponGroup> list = new ArrayList<CorresponGroup>();
        CorresponGroup group = new CorresponGroup();
        group.setId(1L);
        list.add(group);
        group = new CorresponGroup();
        group.setId(2L);
        list.add(group);
        group = new CorresponGroup();
        group.setId(3L);
        list.add(group);

        MockCorresponGroupDao.RET_FIND = list;

        MockCorresponGroupUserDao.RET_GROUP_USER = new CorresponGroupUser();

        User user = new User();
        user.setEmpNo("ZZA01");

        List<CorresponGroupUser> users = new ArrayList<CorresponGroupUser>();
        groupUser = new CorresponGroupUser();
        groupUser.setUser(user);
        group = new CorresponGroup();
        group.setId(corresponGroupId);
        groupUser.setCorresponGroup(group);
        users.add(groupUser);

        groupUser = new CorresponGroupUser();
        user = new User();
        user.setEmpNo("ZZA99");
        groupUser.setUser(user);
        group = new CorresponGroup();
        group.setId(99L);
        groupUser.setCorresponGroup(group);
        users.add(groupUser);

        MockCorresponGroupUserDao.RET_GROUP_USERS = users;

        currentUser.setEmpNo("ZZA01");
        MockCorresponGroupUser.RET_SECURITY_LEVEL = "30";

        assertTrue(helper.isAnyGroupAdmin("PJ1"));

        MockCorresponGroupUser.RET_SECURITY_LEVEL = "40";

        assertFalse(helper.isAnyGroupAdmin("PJ1"));
    }

    /**
     *isAnyGroupAdmin(String)の判定テスト. 引数Null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAnyGroupAdminStringNull() {
        String str = null;

        helper.isAnyGroupAdmin(str);

        fail("例外が発生していない");
    }

    /**
     * 現在ログイン中ユーザーのプロジェクトユーザー情報を返すテスト.
     */
    @Test
    public void testCurrentProjectUser() {
        User user = new User();
        user.setEmpNo("80001");
        ProjectUser expected = new ProjectUser();
        expected.setProjectId("PJ1");
        expected.setUser(user);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put(Constants.KEY_PROJECT_USER, expected);
        MockProcessContext.RET_VALUE = map;

        assertEquals(expected, helper.getCurrentProjectUser());
    }

    /**
     * プロジェクトユーザーを検索するテスト.
     */
    @Test
    public void testFindProjectUser() {
        String projectId = "PJ1";
        String empNo = "80001";

        User user = new User();
        user.setEmpNo(empNo);
        ProjectUser expected = new ProjectUser();
        expected.setProjectId(projectId);
        expected.setUser(user);

        List<ProjectUser> list = new ArrayList<ProjectUser>();
        list.add(expected);
        MockUserDao.RET_PROJECT_USER = list;
        currentUser.setEmpNo(empNo);

        assertEquals(expected, helper.findProjectUser(projectId, empNo));
    }

    /**
     * プロジェクトユーザーを検索するテスト. 第一引数Null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testFindProjectUserNull_1() {
        String empNo = "80001";

        helper.findProjectUser(null, empNo);
    }

    /**
     * プロジェクトユーザーを検索するテスト. 第二引数Null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testFindProjectUserNull_2() {
        String projectId = "PJ1";
        helper.findProjectUser(projectId, null);
    }

    public static class MockProjectUser extends MockUp<ProjectUser> {
        static String RET_PROJECT_ADMIN_FLG;

        @Mock
        public String getProjectAdminFlg() {
            return RET_PROJECT_ADMIN_FLG;
        }
    }

    public static class MockCorresponGroupUser extends MockUp<CorresponGroupUser> {
        static String RET_SECURITY_LEVEL;

        @Mock
        public String getSecurityLevel() {
            return RET_SECURITY_LEVEL;
        }
    }

    public static class MockProcessContext extends MockUp<ProcessContext> {
        static Map<String, Object> RET_VALUE;
        static User RET_USER;
        @Mock
        @SuppressWarnings("unchecked")
        public <T> T getValue(Invocation invocation, String key) {
            if (SystemConfig.KEY_USER.equals(key)) {
                return (T) RET_USER;
            } else {
                return (T) RET_VALUE;
            }
        }
    }

    public static class MockUserDao extends MockUp<UserDaoImpl> {
        static List<ProjectUser> RET_PROJECT_USER;

        @Mock
        public List<ProjectUser> findProjectUser(SearchUserCondition condition) {
            return RET_PROJECT_USER;
        }
    }

    public static class MockCorresponGroupUserDao extends MockUp<CorresponGroupUserDaoImpl> {
        static CorresponGroupUser RET_GROUP_USER;
        static List<CorresponGroupUser> RET_GROUP_USERS;
        static Long CHECK_ID;

        @Mock
        public CorresponGroupUser findByEmpNo(Long corresponId, String empNo) {
            if (CHECK_ID.equals(corresponId)) {
                return RET_GROUP_USER;
            }
            return null;
        }

        @Mock
        public List<CorresponGroupUser> findByCorresponGroupId(Long corresponGroupId) {
            return RET_GROUP_USERS;
        }

        @Mock
        public List<CorresponGroupUser> findByProjectId(String projectId) {
            return RET_GROUP_USERS;
        }
    }

    public static class MockCorresponGroupDao extends MockUp<CorresponGroupDaoImpl> {
        static List<CorresponGroup> RET_FIND;

        @Mock
        public List<CorresponGroup> find(SearchCorresponGroupCondition condition) {
            return RET_FIND;
        }
    }
}
