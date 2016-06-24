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

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.stereotype.Service;

import jp.co.opentone.bsol.framework.core.ProcessContext;
import jp.co.opentone.bsol.framework.core.config.SystemConfig;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.test.AbstractTestCase;
import jp.co.opentone.bsol.linkbinder.Constants;
import jp.co.opentone.bsol.linkbinder.dao.CorresponDao;
import jp.co.opentone.bsol.linkbinder.dao.ProjectDao;
import jp.co.opentone.bsol.linkbinder.dao.UserDao;
import jp.co.opentone.bsol.linkbinder.dao.WorkflowDao;
import jp.co.opentone.bsol.linkbinder.dao.impl.CorresponDaoImpl;
import jp.co.opentone.bsol.linkbinder.dao.impl.ProjectDaoImpl;
import jp.co.opentone.bsol.linkbinder.dao.impl.UserDaoImpl;
import jp.co.opentone.bsol.linkbinder.dao.impl.WorkflowDaoImpl;
import jp.co.opentone.bsol.linkbinder.dao.mock.CorresponDaoMock;
import jp.co.opentone.bsol.linkbinder.dao.mock.UserDaoMock;
import jp.co.opentone.bsol.linkbinder.dto.Correspon;
import jp.co.opentone.bsol.linkbinder.dto.Project;
import jp.co.opentone.bsol.linkbinder.dto.ProjectUser;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import mockit.Mock;
import mockit.MockUp;

/**
 * {@link AbstractService}のテストケース.
 * @author opentone
 */
public class AbstractServiceTest extends AbstractTestCase {

    /**
     * テスト対象.
     * <p>
     * 抽象クラスなので継承したクラスを対象とする.
     * </p>
     */
    @Resource
    private ExtendedAbstractService service;

    /**
     * テスト準備.
     */
    @Before
    public void setUp() {
        new MockSystemConfig();
        new MockUserPermissionHelper();
        new MockProjectDao();
    }

    /**
     * テスト後始末.
     */
    @After
    public void tearDown() {
        new MockSystemConfig().tearDown();;
        new MockUserPermissionHelper().tearDown();
        new MockProjectDao().tearDown();
        MockUserPermissionHelper.IS_DISICPLINE_ADMIN = false;
        MockUserPermissionHelper.IS_PROJECT_ADMIN = false;
        MockUserPermissionHelper.IS_SYSTEM_ADMIN = false;
        MockUserPermissionHelper.PROJECT_USER = null;
        MockProjectDao.FIND_BY_EMP_NO = null;

        MockSystemConfig.VALUES.clear();
//        Mockit.tearDownMocks();
    }

    /**
     * {@link AbstractService#getDao(Class)}のテスト.
     * @throws Exception
     */
    @Test
    public void testGetDao() throws Exception {
        // 実装クラスのオブジェクトが取得できているか
        assertThat(service.getDao(UserDao.class), instanceOf(UserDaoImpl.class));
        assertThat(service.getDao(CorresponDao.class), instanceOf(CorresponDaoImpl.class));

        // Mock取得のための定義を設定して実行すると
        // Mockオブジェクトが取得できているか
        MockSystemConfig.VALUES.put(AbstractService.KEY_DAO_USE_MOCK, "true");
        MockSystemConfig.VALUES.put(AbstractService.KEY_DAO_USE_MOCK + ".UserDao", "true");
        MockSystemConfig.VALUES.put(AbstractService.KEY_DAO_USE_MOCK + ".CorresponDao", "true");

        assertThat(service.getDao(UserDao.class), instanceOf(UserDaoMock.class));
        assertThat(service.getDao(CorresponDao.class), instanceOf(CorresponDaoMock.class));

        // user.mock = trueでも
        // Dao毎の設定が無い or falseであれば実装クラスのオブジェクトが取得できる
        assertThat(service.getDao(WorkflowDao.class), instanceOf(WorkflowDaoImpl.class));

        MockSystemConfig.VALUES.put(AbstractService.KEY_DAO_USE_MOCK + ".ProjectDao", "false");
        assertThat(service.getDao(ProjectDao.class), instanceOf(ProjectDaoImpl.class));
    }

    /**
     * {@link AbstractService#getDao(Class)}のテスト(異常系).
     * @throws Exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetDaoException() throws Exception {
        service.getDao(null);
    }

    /**
     * {@link AbstractService#getCurrentProject()}のテストケース.
     * @throws Exception
     */
    @Test
    public void testGetCurrentProject() throws Exception {
        // ProcessContextに設定済のプロジェクトが取得できる
        Project expected = new Project();
        expected.setProjectId("PJ1");

        ProcessContext c = ProcessContext.getCurrentContext();
        Map<String, Object> values = new HashMap<String, Object>();
        values.put(Constants.KEY_PROJECT, expected);
        c.setValue(SystemConfig.KEY_ACTION_VALUES, values);

        assertThat(service.getCurrentProject(), equalTo(expected));

        // プロジェクトが設定されていない状態でも
        // 例外が発生せず終了する
        values.remove(Constants.KEY_PROJECT);
        assertNull(service.getCurrentProject());
    }

    /**
     * {@link AbstractService#getCurrentProjectId()}のテストケース.
     * @throws Exception
     */
    @Test
    public void testGetCurrentProjectId() throws Exception {
        // ProcessContextに設定済のプロジェクトが取得できる
        Project expected = new Project();
        expected.setProjectId("PJ1");

        ProcessContext c = ProcessContext.getCurrentContext();
        Map<String, Object> values = new HashMap<String, Object>();
        values.put(Constants.KEY_PROJECT, expected);
        c.setValue(SystemConfig.KEY_ACTION_VALUES, values);

        assertThat(service.getCurrentProjectId(), equalTo(expected.getProjectId()));

        // プロジェクトが設定されていない状態でも
        // 例外が発生せず終了する
        values.remove(Constants.KEY_PROJECT);
        assertNull(service.getCurrentProjectId());
    }

    /**
     * {@link AbstractService#getCurrentUser()}のテストケース.
     * @throws Exception
     */
    @Test
    public void testGetCurrentUser() throws Exception {
        User currentUser = (User) applicationContext.getBean("currentUser");
        currentUser.setEmpNo("00001");

        assertThat(service.getCurrentUser(), equalTo(currentUser));
        assertThat(service.getCurrentUser().getEmpNo(), equalTo(currentUser.getEmpNo()));
    }

    /**
     * {@link AbstractService#isSystemAdmin(User)}のテストケース.
     */
    @Test
    public void testIsSystemAdmin() {
        // UserPermissionHelperに委譲するよう変更したので
        // 呼び出しさえ確認できればOK
        User u = new User();

        MockUserPermissionHelper.IS_SYSTEM_ADMIN = true;
        assertTrue(service.isSystemAdmin(u));

        MockUserPermissionHelper.IS_SYSTEM_ADMIN = false;
        assertFalse(service.isSystemAdmin(u));
    }

    /**
     * {@link AbstractService#isSystemAdmin(User)}のテストケース(異常系).
     */
    @Test(expected = IllegalArgumentException.class)
    public void testIsSystemAdminException() {
        service.isSystemAdmin(null);
    }

    /**
     * {@link AbstractService#isProjectAdmin(User, String)}のテストケース.
     */
    @Test
    public void testIsProjectAdmin() {
        User user = new User();
        String projectId = "PJ1";

        // UserPermissionHelperに委譲するよう変更したので
        // 呼び出しさえ確認できればOK
        MockUserPermissionHelper.IS_PROJECT_ADMIN = true;
        assertTrue(service.isProjectAdmin(user, projectId));

        MockUserPermissionHelper.IS_PROJECT_ADMIN = false;
        assertFalse(service.isProjectAdmin(user, projectId));
    }

    /**
     * {@link AbstractService#findProjectUser(String, String)}のテストケース.
     */
    @Test
    public void testFindProjectUser() {
        ProjectUser pu = new ProjectUser();
        pu.setProjectId("PJ1");
        User user = new User();
        user.setEmpNo("00001");
        pu.setUser(user);

        MockUserPermissionHelper.PROJECT_USER = pu;

        String projectId = "PJ1";
        String empNo = "00001";
        assertNotNull(service.findProjectUser(projectId, empNo));

    }

    /**
     * {@link AbstractService#getCurrentProjectUser()}のテストケース.
     */
    @Test
    public void testGetCurrentProjectUser() throws Exception {
        User u = new User();
        u.setEmpNo("00001");
        ProjectUser expected = new ProjectUser();
        expected.setUser(u);

        ProcessContext c = ProcessContext.getCurrentContext();
        Map<String, Object> values = new HashMap<String, Object>();
        values.put(Constants.KEY_PROJECT_USER, expected);
        c.setValue(SystemConfig.KEY_ACTION_VALUES, values);

        assertThat(service.getCurrentProjectUser(), equalTo(expected));
        assertThat(service.getCurrentProjectUser().getUser().getEmpNo(), equalTo(u.getEmpNo()));
    }

    /**
     * {@link AbstractService#isGroupAdmin(User, Long)}のテストケース.
     */
    @Test
    public void testIsGroupAdmin() {
        User user = new User();
        Long corresponGroupId = 1L;
        // UserPermissionHelperに委譲しているので呼出だけ確認する
        MockUserPermissionHelper.IS_DISICPLINE_ADMIN = true;
        assertTrue(service.isGroupAdmin(user, corresponGroupId));

        MockUserPermissionHelper.IS_DISICPLINE_ADMIN = false;
        assertFalse(service.isGroupAdmin(user, corresponGroupId));
    }

    /**
     * {@link AbstractService#isGroupAdmin(User, Long)}のテストケース. 引数が不正な場合を検証する.
     * @throws Exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testIsGroupAdminUserNull() throws Exception {
        User user = null;
        Long corresponGroupId = 1L;

        service.isGroupAdmin(user, corresponGroupId);
    }

    /**
     * {@link AbstractService#isGroupAdmin(User, Long)}のテストケース. 引数が不正な場合を検証する.
     * @throws Exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testIsGroupAdminUserIdNull() throws Exception {
        User user = new User();
        Long corresponGroupId = null;

        service.isGroupAdmin(user, corresponGroupId);
    }

    /**
     * {@link AbstractService#getAccessibleProjects()}のテストケース.
     */
    @Test
    public void testGetAccessibleProjects() {
        User currentUser = (User) applicationContext.getBean("currentUser");
        currentUser.setEmpNo("00001");

        List<Project> expected = new ArrayList<Project>();
        Project p;
        p = new Project();
        p.setProjectId("PJ1");
        expected.add(p);

        p = new Project();
        p.setProjectId("PJ2");
        expected.add(p);

        MockProjectDao.FIND_BY_EMP_NO = expected;

        List<Project> actual = service.getAccessibleProjects();
        assertEquals(expected.size(), actual.size());
    }

    /**
     * {@link AbstractService#validateProjectId(String)}のテストケース.
     * 現在のプロジェクトと異なるプロジェクトの場合はエラー.
     */
    @Test
    public void testValidateProjectIdInvalidProject() throws Exception {
        // ProcessContextに現在のプロジェクトを設定
        Project current = new Project();
        current.setProjectId("PJ1");

        ProcessContext c = ProcessContext.getCurrentContext();
        Map<String, Object> values = new HashMap<String, Object>();
        values.put(Constants.KEY_PROJECT, current);
        c.setValue(SystemConfig.KEY_ACTION_VALUES, values);

        try {
            service.validateProjectId("PJ2");
            fail("例外が発生していない");
        } catch (ServiceAbortException expected) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_PROJECT_DIFF, expected
                .getMessageCode());
        }
    }

    /**
     * {@link AbstractService#validateProjectId(String)}のテストケース.
     * 現在のプロジェクトだが、アクセス可能なプロジェクトに含まれない場合はエラー.
     */
    @Test
    public void testValidateProjectIdNotContainsAccessibleProjects() throws Exception {

        // ユーザー情報、アクセス可能なプロジェクトを設定
        User currentUser = (User) applicationContext.getBean("currentUser");
        currentUser.setEmpNo("00001");
        List<Project> projects = new ArrayList<Project>();
        Project p;
        p = new Project();
        p.setProjectId("PJ1");
        projects.add(p);

        p = new Project();
        p.setProjectId("PJ2");
        projects.add(p);

        MockProjectDao.FIND_BY_EMP_NO = projects;

        // ProcessContextに現在のプロジェクトを設定
        Project current = new Project();
        current.setProjectId("PJ3");

        ProcessContext c = ProcessContext.getCurrentContext();
        Map<String, Object> values = new HashMap<String, Object>();
        values.put(Constants.KEY_PROJECT, current);
        c.setValue(SystemConfig.KEY_ACTION_VALUES, values);

        // SystemAdmin以外であることを設定
        MockUserPermissionHelper.IS_SYSTEM_ADMIN = false;

        try {
            // 現在のプロジェクトと一致するが
            // ログインユーザーがアクセス可能なプロジェクトには含まれていない
            service.validateProjectId("PJ3");
            fail("例外が発生していない");
        } catch (ServiceAbortException expected) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_PROJECT_DIFF, expected
                .getMessageCode());
        }
    }

    /**
     * {@link AbstractService#validateProjectId(String)}のテストケース.
     * 現在のプロジェクトで、System Adminの場合はOK.
     */
    @Test
    public void testValidateProjectIdSystemAdmin() throws Exception {

        // ユーザー情報、アクセス可能なプロジェクトを設定
        User currentUser = (User) applicationContext.getBean("currentUser");
        currentUser.setEmpNo("00001");
        List<Project> projects = new ArrayList<Project>();
        Project p;
        p = new Project();
        p.setProjectId("PJ1");
        projects.add(p);

        p = new Project();
        p.setProjectId("PJ2");
        projects.add(p);

        MockProjectDao.FIND_BY_EMP_NO = projects;

        // ProcessContextに現在のプロジェクトを設定
        Project current = new Project();
        current.setProjectId("PJ2");

        ProcessContext c = ProcessContext.getCurrentContext();
        Map<String, Object> values = new HashMap<String, Object>();
        values.put(Constants.KEY_PROJECT, current);
        c.setValue(SystemConfig.KEY_ACTION_VALUES, values);

        // SystemAdminであることを設定
        MockUserPermissionHelper.IS_SYSTEM_ADMIN = true;

        // 現在のプロジェクトと一致しているのでOK
        service.validateProjectId("PJ2");
    }

    /**
     * {@link AbstractService#validateProjectId(String)}のテストケース.
     * ユーザーがアクセス可能なプロジェクトに含まれているのでOK.
     */
    @Test
    public void testValidateProjectValid() throws Exception {

        // ユーザー情報、アクセス可能なプロジェクトを設定
        User currentUser = (User) applicationContext.getBean("currentUser");
        currentUser.setEmpNo("00001");
        List<Project> projects = new ArrayList<Project>();
        Project p;
        p = new Project();
        p.setProjectId("PJ1");
        projects.add(p);

        p = new Project();
        p.setProjectId("PJ2");
        projects.add(p);

        MockProjectDao.FIND_BY_EMP_NO = projects;

        // ProcessContextに現在のプロジェクトを設定
        Project current = new Project();
        current.setProjectId("PJ2");

        ProcessContext c = ProcessContext.getCurrentContext();
        Map<String, Object> values = new HashMap<String, Object>();
        values.put(Constants.KEY_PROJECT, current);
        c.setValue(SystemConfig.KEY_ACTION_VALUES, values);

        // SystemAdminでないことを設定
        MockUserPermissionHelper.IS_SYSTEM_ADMIN = false;

        // 現在のプロジェクトと一致しているのでOK
        service.validateProjectId("PJ2");
    }

    /**
     * {@link AbstractService#isAnyGroupAdmin(jp.co.opentone.bsol.linkbinder.dto.Correspon)}
     * のテストケース.
     */
    @Test
    public void testIsAnyGroupAdmin() {
        Correspon c = new Correspon();
        c.setId(1L);

        // UserPermissionHelperに委譲しているので呼出だけ確認する
        MockUserPermissionHelper.IS_ANY_GROUP_ADMIN = true;
        assertTrue(service.isAnyGroupAdmin(c));

        MockUserPermissionHelper.IS_ANY_GROUP_ADMIN = false;
        assertFalse(service.isAnyGroupAdmin(c));
    }

    @Service
    public static class ExtendedAbstractService extends AbstractService {

        /**
         * SerialVersionUID.
         */
        private static final long serialVersionUID = 7336367468167790098L;

    }

    public static class MockSystemConfig extends MockUp<SystemConfig> {
        public static Map<String, String> VALUES = new HashMap<String, String>();

        @Mock
        public static String getValue(String key) {
            return VALUES.get(key);
        }
    }

    public static class MockUserPermissionHelper extends MockUp<UserPermissionHelper> {

        public static boolean IS_SYSTEM_ADMIN;
        public static boolean IS_PROJECT_ADMIN;
        public static boolean IS_DISICPLINE_ADMIN;
        public static boolean IS_ANY_GROUP_ADMIN;
        public static ProjectUser PROJECT_USER;

        @Mock
        public boolean isSystemAdmin(User user) {
            return IS_SYSTEM_ADMIN;
        }

        @Mock
        public boolean isProjectAdmin(User user, String projectId) {
            return IS_PROJECT_ADMIN;
        }

        @Mock
        public boolean isGroupAdmin(User user, Long corresponGroupId) {
            return IS_DISICPLINE_ADMIN;
        }

        @Mock
        public ProjectUser findProjectUser(String projectId, String empNo) {
            return PROJECT_USER;
        }

        @Mock
        public boolean isAnyGroupAdmin(Correspon c) {
            return IS_ANY_GROUP_ADMIN;
        }
    }

    public static class MockProjectDao extends MockUp<ProjectDaoImpl> {
        public static List<Project> FIND_BY_EMP_NO;

        @Mock
        public List<Project> findByEmpNo(String empNo) {
            assertNotNull(empNo);
            return FIND_BY_EMP_NO;
        }
    }
}
