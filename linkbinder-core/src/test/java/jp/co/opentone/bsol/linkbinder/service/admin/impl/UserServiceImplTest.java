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
package jp.co.opentone.bsol.linkbinder.service.admin.impl;

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

import jp.co.opentone.bsol.framework.core.dao.RecordNotFoundException;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.extension.jsf.FacesContextMock;
import jp.co.opentone.bsol.framework.test.AbstractTestCase;
import jp.co.opentone.bsol.linkbinder.Constants;
import jp.co.opentone.bsol.linkbinder.dao.impl.CorresponGroupDaoImpl;
import jp.co.opentone.bsol.linkbinder.dao.impl.ProjectDaoImpl;
import jp.co.opentone.bsol.linkbinder.dao.impl.UserDaoImpl;
import jp.co.opentone.bsol.linkbinder.dao.impl.UserProfileDaoImpl;
import jp.co.opentone.bsol.linkbinder.dto.Company;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroup;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroupUser;
import jp.co.opentone.bsol.linkbinder.dto.Project;
import jp.co.opentone.bsol.linkbinder.dto.ProjectUser;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.UserIndex;
import jp.co.opentone.bsol.linkbinder.dto.UserProfile;
import jp.co.opentone.bsol.linkbinder.dto.UserSettings;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchUserCondition;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.service.AbstractService;
import jp.co.opentone.bsol.linkbinder.service.admin.UserService;
import mockit.Mock;
import mockit.MockUp;

/**
 * ユーザー情報Serviceのテストクラス.
 * @author opentone
 */
public class UserServiceImplTest extends AbstractTestCase {

    /**
     * テスト対象.
     */
    @Resource
    private UserService service;

    /**
     * プロパティに設定されたEXCELシート名のKEY.
     */
    private static final String SHEET_KEY = "excel.sheetname.userindex";

    /**
     * EXCELシート名がプロパティに設定されていない場合のデフォルト名.
     */
    private static final String SHEET_DEFAULT = "UserIndex";

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        new MockAbstractService();
        FacesContextMock.initialize();
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        new MockAbstractService().tearDown();
        FacesContextMock.tearDown();
    }

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        MockAbstractService.RET_FIND_PROJECT_USER = null;
        MockAbstractService.RET_USER = null;
        MockAbstractService.RET_PROJECT_ID = null;
        MockAbstractService.RET_SYSTEM_ADMIN = false;
        MockAbstractService.RET_PROJECT_ADMIN = false;
        MockAbstractService.RET_ANY_GROUP_ADMIN = false;
        MockAbstractService.RET_GROUP_ADMIN = false;
        MockSystemConfig.VALUES = new HashMap<String, String>();
        MockSystemConfig.VALUES.put(Constants.KEY_SECURITY_FLG_SYSTEM_ADMIN, "X");
        MockSystemConfig.VALUES.put(Constants.KEY_SECURITY_FLG_PROJECT_ADMIN, "X");
        MockSystemConfig.VALUES.put(Constants.KEY_SECURITY_LEVEL_GROUP_ADMIN, "30");
        MockSystemConfig.VALUES.put(Constants.KEY_SECURITY_LEVEL_NORMAL_USER, "40");
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.service.admin.impl.UserServiceImpl
     *         #search(jp.co.opentone.bsol.linkbinder.dto.condition.SearchUserCondition)} のためのテスト・メソッド.
     * プロジェクトID指定あり.
     */
    @Test
    public void testSearch() throws Exception {
        String projectId = "PJ1";
        SearchUserCondition condition = new SearchUserCondition();
        condition.setProjectId(projectId);

        List<ProjectUser> expected = new ArrayList<ProjectUser>();
        ProjectUser pUser = new ProjectUser();
        pUser.setProjectId(projectId);
        User user = new User();
        user.setEmpNo("00001");
        pUser.setUser(user);
        expected.add(pUser);
        pUser = new ProjectUser();
        pUser.setProjectId(projectId);
        user = new User();
        user.setEmpNo("00001");
        pUser.setUser(user);
        expected.add(pUser);

        // Mock準備
        new MockUp<UserDaoImpl>() {
            @Mock List<ProjectUser> findProjectUser(SearchUserCondition condition) {
                return expected;
            }
        };
        List<ProjectUser> actual = service.search(condition);

        assertEquals(expected, actual);
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.service.admin.impl.UserServiceImpl
     *         #search(jp.co.opentone.bsol.linkbinder.dto.condition.SearchUserCondition)} のためのテスト・メソッド.
     * プロジェクトID指定なし.
     */
    @Test
    public void testSearchCurrentProject() throws Exception {
        String projectId = "PJ1";
        SearchUserCondition condition = new SearchUserCondition();

        List<ProjectUser> expected = new ArrayList<ProjectUser>();
        ProjectUser pUser = new ProjectUser();
        pUser.setProjectId(projectId);
        User user = new User();
        user.setEmpNo("00001");
        pUser.setUser(user);
        expected.add(pUser);
        pUser = new ProjectUser();
        pUser.setProjectId(projectId);
        user = new User();
        user.setEmpNo("00001");
        pUser.setUser(user);
        expected.add(pUser);

        MockAbstractService.RET_PROJECT_ID = projectId;
        new MockUp<UserDaoImpl>() {
            @Mock List<ProjectUser> findProjectUser(SearchUserCondition condition) {
                return expected;
            }
        };

        List<ProjectUser> actual = service.search(condition);

        assertEquals(expected, actual);
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.service.admin.impl.UserServiceImpl
     *         #search(jp.co.opentone.bsol.linkbinder.dto.condition.SearchUserCondition)} のためのテスト・メソッド.
     * 引数NULL.
     */
    @Test(expected=IllegalArgumentException.class)
    public void testSearchNull() throws Exception {
        service.search(null);
        fail("例外が発生していない");
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.service.admin.impl.UserServiceImpl
     *         #findByEmpNo(java.lang.String)} のためのテスト・メソッド.
     */
    @Test
    public void testFindByEmpNo() throws Exception {
        String empNo = "00001";

        User expected = new User();
        expected.setEmpNo(empNo);

        // Mock準備
        new MockUp<UserDaoImpl>() {
            @Mock User findByEmpNo(String empNo) throws RecordNotFoundException {
                return expected;
            }
        };

        User actual = service.findByEmpNo(empNo);

        assertEquals(expected, actual);
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.service.admin.impl.UserServiceImpl
     *         #findByEmpNo(java.lang.String)} のためのテスト・メソッド.
     * 引数NULL.
     */
    @Test(expected=IllegalArgumentException.class)
    public void testFindByEmpNoNull() throws Exception {
        service.findByEmpNo(null);
        fail("例外が発生していない");
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.service.admin.impl.UserServiceImpl
     *         #findByEmpNo(java.lang.String)} のためのテスト・メソッド.
     * RecordNotFoundException.
     */
    @Test
    public void testFindByEmpNoNoRecord() throws Exception {
        // Mock準備
        new MockUp<UserDaoImpl>() {
            @Mock User findByEmpNo(String empNo) throws RecordNotFoundException {
                throw new RecordNotFoundException();
            }
        };

        try {
            service.findByEmpNo("00001");
            fail("例外が発生していない");
        } catch (ServiceAbortException e) {
            assertEquals(RecordNotFoundException.class,
                         e.getCause().getClass());
        }
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.service.admin.impl.UserServiceImpl
     *         #searchCorrseponGroup(java.lang.String, java.lang.String)} のためのテスト・メソッド.
     */
    @Test
    public void testSearchCorrseponGroup() throws Exception {
        Long[] ids = {new Long(1), new Long(2), new Long(3)};
        List<CorresponGroupUser> expected = createCorresponGroupUserList(ids);

        // Mock準備
        new MockUp<CorresponGroupDaoImpl>() {
            @Mock List<CorresponGroupUser> findByEmpNo(String projectId, String empNo) {
                return expected;
            }
        };

        List<CorresponGroupUser> actual = service.searchCorrseponGroup("PJ1", "00001");

        assertEquals(expected, actual);
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.service.admin.impl.UserServiceImpl
     *         #searchCorrseponGroup(java.lang.String, java.lang.String)} のためのテスト・メソッド.
     * 第一引数Null.
     */
    @Test(expected=IllegalArgumentException.class)
    public void testSearchCorrseponGroupNull_1() throws Exception {
        service.searchCorrseponGroup(null, "00001");
        fail("例外が発生していない");
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.service.admin.impl.UserServiceImpl
     *         #searchCorrseponGroup(java.lang.String, java.lang.String)} のためのテスト・メソッド.
     * 第二引数Null.
     */
    @Test(expected=IllegalArgumentException.class)
    public void testSearchCorrseponGroupNull_2() throws Exception {
        service.searchCorrseponGroup("PJ1", null);
        fail("例外が発生していない");
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.service.admin.impl.UserServiceImpl
     *         #findUserSettings(java.lang.String)} のためのテスト・メソッド.
     */
    @Test
    public void testFindUserSettings() throws Exception {
        String userId = "ZZA01";
        User user = new User();
        user.setEmpNo(userId);

        Long[] defaultLongIds = {new Long(1),new Long(2),new Long(3),new Long(4)};

        List<Project> projectList = new ArrayList<Project>();
        Project project = new Project();
        project.setProjectId("PJ1");
        projectList.add(project);
        project = new Project();
        project.setProjectId("PJ2");
        projectList.add(project);
        project = new Project();
        project.setProjectId("PJ3");
        projectList.add(project);
        project = new Project();
        project.setProjectId("PJ4");
        projectList.add(project);

        // Mock準備
        MockAbstractService.RET_USER = user;
        MockAbstractService.RET_FIND_PROJECT_USER = new ProjectUser();
        new MockUp<UserDaoImpl>() {
            @Mock User findByEmpNo(String empNo) throws RecordNotFoundException {
                return user;
            }

        };
        new MockUp<ProjectDaoImpl>() {
            @Mock List<Project> findByEmpNo(String empNo) {
                return projectList;
            }
        };
        new MockUp<CorresponGroupDaoImpl>() {
            @Mock List<CorresponGroupUser> findByEmpNo(
                    String projectId, String empNo, String sortColumn) {
                return createCorresponGroupUserList(defaultLongIds);
            }
        };
        new MockUp<UserProfileDaoImpl>() {
            @Mock UserProfile findByEmpNo(String empNo) {
                return new UserProfile();
            }
        };


        service.findUserSettings(userId);
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.service.admin.impl.UserServiceImpl
     *         #findUserSettings(java.lang.String)} のためのテスト・メソッド.
     * 引数Null.
     */
    @Test(expected=IllegalArgumentException.class)
    public void testFindUserSettingsNull() throws Exception {
        service.findUserSettings(null);
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.service.admin.impl.UserServiceImpl
     *         #findUserSettings(java.lang.String)} のためのテスト・メソッド.
     * ユーザーが見つからない.
     */
    @Test
    public void testFindUserSettingsNoUser() throws Exception {
        // Mock準備
        new MockUp<UserDaoImpl>() {
            @Mock User findByEmpNo(String empNo) throws RecordNotFoundException {
                // ユーザーが見つからない
                throw new RecordNotFoundException();
            }
        };
        try {
            service.findUserSettings("ZZA01");

            fail("例外が発生していない");
        } catch (ServiceAbortException e) {
            assertEquals(ApplicationMessageCode.NO_DATA_FOUND, e.getMessageCode());
        }
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.service.admin.impl.UserServiceImpl
     *         #save(jp.co.opentone.bsol.linkbinder.dto.UserSettings)} のためのテスト・メソッド.
     * 引数NULL.
     */
    @Test(expected=IllegalArgumentException.class)
    public void testSaveNull() throws Exception {
        service.save((UserSettings) null);
        fail("例外が発生していない");
    }
    /**
     * {@link jp.co.opentone.bsol.linkbinder.service.admin.impl.UserServiceImpl
     *         #searchPagingList(jp.co.opentone.bsol.linkbinder.dto.condition.SearchUserCondition)}
     * のためのテスト・メソッド.
     * 引数がNull.
     */
    @Test(expected=IllegalArgumentException.class)
    public void testSearchPagingListNull() throws Exception {
        service.searchPagingList(null);
        fail("例外が発生していない");
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.service.admin.impl.UserServiceImpl
     *         #generateExcel((java.util.List)}のためのテスト・メソッド.
     * シート名設定あり.
     */
    @Test
    public void testGenerateExcelSheetKey() throws Exception {
        String expSheetName = "UserIndex";
        MockSystemConfig.VALUES.put(SHEET_KEY, expSheetName);

        // 結果レコード
        List<Object> expected = new ArrayList<Object>();
        /* ヘッダ */
        List<Object> line = new ArrayList<Object>();
        line.add("ID");
        line.add("Name");
        line.add("Role");
        line.add("Company");
        line.add("Group");
        line.add("PA");
        line.add("GA");
        expected.add(line);

        /* 1行目 */
        line = new ArrayList<Object>();
        line.add("ZZA01");
        line.add("Test User 1");
        line.add("role A");
        line.add("CD1 : Company 1");
        line.add("Group 1");
        line.add("");
        line.add("");
        expected.add(line);

        /* 2行目 */
        line = new ArrayList<Object>();
        line.add("ZZA02");
        line.add("Test User 2");
        line.add("");
        line.add("CD2 : Company 2");
        line.add("Group 2");
        line.add("○");
        line.add("");
        expected.add(line);

        /* 3行目 */
        line = new ArrayList<Object>();
        line.add("ZZA03");
        line.add("Test User 3");
        line.add("role B");
        line.add("CD3 : Company 3");
        line.add("Group 3");
        line.add("");
        line.add("○");
        expected.add(line);

        // マスタ管理の為、リストを作成する際にnullを渡す。
        byte[] actual = service.generateExcel(createUserIndexList());

        // 作成したExcelを確認
        assertExcel(1, expSheetName, 3, expected, actual);
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.service.admin.impl.UserServiceImpl
     *         #generateExcel((java.util.List)}のためのテスト・メソッド.
     * シート名設定なし.
     */
    @Test
    public void testGenerateExcelSheetDefault() throws Exception {

        // 結果レコード
        List<Object> expected = new ArrayList<Object>();
        /* ヘッダ */
        List<Object> line = new ArrayList<Object>();
        line.add("ID");
        line.add("Name");
        line.add("Role");
        line.add("Company");
        line.add("Group");
        line.add("PA");
        line.add("GA");
        expected.add(line);

        /* 1行目 */
        line = new ArrayList<Object>();
        line.add("ZZA01");
        line.add("Test User 1");
        line.add("role A");
        line.add("CD1 : Company 1");
        line.add("Group 1");
        line.add("");
        line.add("");
        expected.add(line);

        /* 2行目 */
        line = new ArrayList<Object>();
        line.add("ZZA02");
        line.add("Test User 2");
        line.add("");
        line.add("CD2 : Company 2");
        line.add("Group 2");
        line.add("○");
        line.add("");
        expected.add(line);

        /* 3行目 */
        line = new ArrayList<Object>();
        line.add("ZZA03");
        line.add("Test User 3");
        line.add("role B");
        line.add("CD3 : Company 3");
        line.add("Group 3");
        line.add("");
        line.add("○");
        expected.add(line);

        // マスタ管理の為、リストを作成する際にnullを渡す。
        byte[] actual = service.generateExcel(createUserIndexList());

        // 作成したExcelを確認
        assertExcel(1, SHEET_DEFAULT, 3, expected, actual);
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.service.admin.impl.UserServiceImpl
     *         #generateExcel((java.util.List)}のためのテスト・メソッド.
     * 引数がNull.
     */
    @Test(expected=IllegalArgumentException.class)
    public void testGenerateExcelNull() throws Exception {
        service.generateExcel(null);
        fail("例外が発生していない");
    }

    private List<CorresponGroupUser> createCorresponGroupUserList(Long[] ids) {
        List<CorresponGroupUser> list = new ArrayList<CorresponGroupUser>();
        for (int i = 0; i < ids.length; i++) {
            CorresponGroupUser gUser = new CorresponGroupUser();
            CorresponGroup group = new CorresponGroup();
            group.setId(ids[i]);
            gUser.setCorresponGroup(group);
            list.add(gUser);
        }
        return list;
    }


    private List<UserIndex> createUserIndexList() {
        List<UserIndex> list = new ArrayList<UserIndex>();

        UserIndex index = new UserIndex();
        ProjectUser pUser = new ProjectUser();
        User user = new User();
        user.setEmpNo("ZZA01");
        user.setNameE("Test User 1");
        user.setRole("role A");
        pUser.setUser(user);
        Company company = new Company();
        company.setCompanyCd("CD1");
        company.setName("Company 1");
        pUser.setProjectCompany(company);
        index.setProjectUser(pUser);
        CorresponGroup group = new CorresponGroup();
        group.setName("Group 1");
        index.setCorresponGroup(group);
        index.setSystemAdmin(true);
        list.add(index);

        index = new UserIndex();
        pUser = new ProjectUser();
        user = new User();
        user.setEmpNo("ZZA02");
        user.setNameE("Test User 2");
        user.setRole("");
        pUser.setUser(user);
        company = new Company();
        company.setCompanyCd("CD2");
        company.setName("Company 2");
        pUser.setProjectCompany(company);
        index.setProjectUser(pUser);
        group = new CorresponGroup();
        group.setName("Group 2");
        index.setCorresponGroup(group);
        index.setProjectAdmin(true);
        list.add(index);

        index = new UserIndex();
        pUser = new ProjectUser();
        user = new User();
        user.setEmpNo("ZZA03");
        user.setNameE("Test User 3");
        user.setRole("role B");
        pUser.setUser(user);
        company = new Company();
        company.setCompanyCd("CD3");
        company.setName("Company 3");
        pUser.setProjectCompany(company);
        index.setProjectUser(pUser);
        group = new CorresponGroup();
        group.setName("Group 3");
        index.setCorresponGroup(group);
        index.setGroupAdmin(true);
        list.add(index);

        return list;
    }

    public static class MockAbstractService extends MockUp<AbstractService> {
        static ProjectUser RET_FIND_PROJECT_USER;
        static User RET_USER;
        static String RET_PROJECT_ID;
        static boolean RET_SYSTEM_ADMIN;
        static boolean RET_PROJECT_ADMIN;
        static boolean RET_ANY_GROUP_ADMIN;
        static boolean RET_GROUP_ADMIN;

        @Mock
        public ProjectUser findProjectUser(String projectId, String empNo) {
            return RET_FIND_PROJECT_USER;
        }

        @Mock
        public User getCurrentUser() {
            return RET_USER;
        }

        @Mock
        public String getCurrentProjectId() {
            return RET_PROJECT_ID;
        }

        @Mock
        public boolean isSystemAdmin(User user) {
            if (RET_USER != null && RET_USER.equals(user)) {
                return !RET_SYSTEM_ADMIN; // 一覧ユーザーと逆
            }
            return RET_SYSTEM_ADMIN;
        }

        @Mock
        public boolean isProjectAdmin(User user, String projectId) {
            if (RET_USER != null && RET_USER.equals(user)) {
                return !RET_PROJECT_ADMIN; // 一覧ユーザーと逆
            }
            return RET_PROJECT_ADMIN;
        }

        @Mock
        public boolean isAnyGroupAdmin(User user, String projectId) {
            return RET_ANY_GROUP_ADMIN;
        }

        @Mock
        public boolean isGroupAdmin(User user, Long corresponGroupId) {
            return RET_GROUP_ADMIN;
        }
    }

    public static class MockSystemConfig {
        public static Map<String, String> VALUES = new HashMap<String, String>();

        public static String getValue(String key) {
            return VALUES.get(key);
        }
    }
}
