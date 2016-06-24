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
import java.util.Calendar;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import jp.co.opentone.bsol.framework.core.dao.KeyDuplicateException;
import jp.co.opentone.bsol.framework.core.dao.StaleRecordException;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.test.AbstractTestCase;
import jp.co.opentone.bsol.linkbinder.dao.impl.ProjectCustomSettingDaoImpl;
import jp.co.opentone.bsol.linkbinder.dto.Project;
import jp.co.opentone.bsol.linkbinder.dto.ProjectCustomSetting;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.code.CorresponStatus;
import jp.co.opentone.bsol.linkbinder.service.MockAbstractService;
import jp.co.opentone.bsol.linkbinder.service.admin.ProjectCustomSettingService;
import mockit.Mock;
import mockit.MockUp;

/**
 * {@link ProjectCustomSettingServiceImpl}のテストケース
 * @author opentone
 */
public class ProjectCustomSettingServiceImplTest extends AbstractTestCase {

    /**
     * テスト対象.
     */
    @Resource
    private ProjectCustomSettingService service;

    private User user = new User();
    private Project project = new Project();

    @BeforeClass
    public static void testSetUp() {
        new MockAbstractService();
    }

    @AfterClass
    public static void testTeardown() {
        new MockAbstractService().tearDown();
    }

    /**
     * テスト前準備.
     */
    @Before
    public void setUp() {
        user.setEmpNo("ZZA01");
        user.setNameE("Taro Yamada");
        MockAbstractService.CURRENT_USER = user;

        project.setProjectId("TEST-PJ1");
        MockAbstractService.CURRENT_PROJECT = project;
        MockAbstractService.CURRENT_PROJECT_ID = project.getProjectId();

        MockAbstractService.ACCESSIBLE_PROJECTS = new ArrayList<Project>();
        MockAbstractService.ACCESSIBLE_PROJECTS.add(project);
    }

    /**
     * 後始末.
     */
    @After
    public void tearDown() {
        tearDownMockAbstractService();
    }

    private void tearDownMockAbstractService() {
        MockAbstractService.CURRENT_PROJECT_ID = null;
        MockAbstractService.CURRENT_PROJECT = null;
        MockAbstractService.CURRENT_USER = null;
        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        MockAbstractService.IS_GROUP_ADMIN = null;
        MockAbstractService.IS_ANY_GROUP_ADMIN = false;
        MockAbstractService.ACCESSIBLE_PROJECTS = null;
        MockAbstractService.VALIDATE_PROJECT_ID_EXCEPTION = null;
    }

    /**
     * {@link ProjectCustomSettingServiceImpl#find(String)}のテストケース.
     * 正常系(プロジェクトカスタム設定情報取得成功)
     * @throws Exception
     */
    @Test
    public void testFind() throws Exception {
        String projectId = "TEST-PJ1";
        ProjectCustomSetting expected = createProjectCustomSettingDummy();

        // Mock準備
        new MockUp<ProjectCustomSettingDaoImpl>() {
            @Mock ProjectCustomSetting findByProjectId(String projectId) {
                return expected;
            }
        };

        ProjectCustomSetting actual = service.find(projectId);

        assertNotNull(actual);
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getProjectId(), actual.getProjectId());
        assertEquals(expected.getDefaultStatus(), actual.getDefaultStatus());
        assertEquals(expected.isUsePersonInCharge(), actual.isUsePersonInCharge());
        assertEquals(expected.getCreatedBy().getEmpNo(), actual.getCreatedBy().getEmpNo());
        assertNotNull(actual.getCreatedAt());
        assertEquals(expected.getUpdatedBy().getEmpNo(), actual.getUpdatedBy().getEmpNo());
        assertNotNull(actual.getUpdatedAt());
        assertEquals(expected.getVersionNo(), actual.getVersionNo());
        assertEquals(expected.getDeleteNo(), actual.getDeleteNo());
    }

    /**
     * {@link ProjectCustomSettingServiceImpl#find(String)}のテストケース.
     * 正常系(プロジェクトカスタム設定情報が未設定)
     * @throws Exception
     */
    @Test
    public void testFindNull() throws Exception {
        String projectId = "TEST-PJ1";
        ProjectCustomSetting expected = new ProjectCustomSetting();

        // Mock準備
        new MockUp<ProjectCustomSettingDaoImpl>() {
            @Mock ProjectCustomSetting findByProjectId(String projectId) {
                return null;
            }
        };

        ProjectCustomSetting actual = service.find(projectId);


        assertNotNull(actual);
        assertNull(actual.getId());
        assertNull(actual.getProjectId());
        assertEquals(expected.getDefaultStatus(), actual.getDefaultStatus());
        assertEquals(expected.isUsePersonInCharge(), actual.isUsePersonInCharge());
        assertNull(actual.getCreatedBy());
        assertNull(actual.getCreatedAt());
        assertNull(actual.getUpdatedBy());
        assertNull(actual.getUpdatedAt());
        assertNull(actual.getVersionNo());
        assertNull(actual.getDeleteNo());
    }

    /**
     * {@link ProjectCustomSettingServiceImpl#find(String)}のテストケース.
     * 異常系(projectId が null)
     * @throws Exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testFindArgumentNull() throws Exception {
        String projectId = null;

        service.find(projectId);
    }

    /**
     * {@link ProjectCustomSettingServiceImpl#find(String)}のテストケース.
     * 異常系(projectId が ""(空))
     * @throws Exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testFindArgumentEmpty() throws Exception {
        String projectId = "";

        service.find(projectId);
    }

    /**
     * {@link ProjectCustomSettingServiceImpl#find(String)}のテストケース.
     * 異常系(projectId が CurrentProject と異なる)
     * @throws Exception
     */
    @Test(expected = ServiceAbortException.class)
    public void testFindNotCurrentProjectId01() throws Exception {
        String projectId = "TEST-PJ2";

        service.find(projectId);
    }

    /**
     * {@link ProjectCustomSettingServiceImpl#find(String)}のテストケース.
     * 正常系(projectId が CurrentProject と異なる)
     * @throws Exception
     */
    @Test
    public void testFindNotCurrentProjectId02() throws Exception {
        String projectId = "TEST-PJ2";
        ProjectCustomSetting expected = createProjectCustomSettingDummy();

        // Mock準備
        new MockUp<ProjectCustomSettingDaoImpl>() {
            @Mock ProjectCustomSetting findByProjectId(String projectId) {
                return expected;
            }
        };

        ProjectCustomSetting actual = service.find(projectId, false);

        assertNotNull(actual);
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getProjectId(), actual.getProjectId());
        assertEquals(expected.getDefaultStatus(), actual.getDefaultStatus());
        assertEquals(expected.isUsePersonInCharge(), actual.isUsePersonInCharge());
        assertEquals(expected.getCreatedBy().getEmpNo(), actual.getCreatedBy().getEmpNo());
        assertNotNull(actual.getCreatedAt());
        assertEquals(expected.getUpdatedBy().getEmpNo(), actual.getUpdatedBy().getEmpNo());
        assertNotNull(actual.getUpdatedAt());
        assertEquals(expected.getVersionNo(), actual.getVersionNo());
        assertEquals(expected.getDeleteNo(), actual.getDeleteNo());
    }

    /**
     * {@link ProjectCustomSettingServiceImpl#save(ProjectCustomSetting)}のテストケース.
     * 正常系(新規登録)
     * @throws Exception
     */
    @Test
    public void testSaveForInsert() throws Exception {
        ProjectCustomSetting arg = new ProjectCustomSetting();

        // Mock準備
        MockAbstractService.IS_SYSTEM_ADMIN = true;
        new MockUp<ProjectCustomSettingDaoImpl>() {
            @Mock Long create(ProjectCustomSetting actual) {
                assertNotNull(actual);
                assertNull(actual.getId());
                assertEquals(project.getProjectId(), actual.getProjectId());
                assertEquals(arg.getDefaultStatus(), actual.getDefaultStatus());
                assertEquals(arg.isUsePersonInCharge(), actual.isUsePersonInCharge());
                assertEquals(user.getEmpNo(), actual.getCreatedBy().getEmpNo());
                assertNull(actual.getCreatedAt());
                assertEquals(user.getEmpNo(), actual.getUpdatedBy().getEmpNo());
                assertNull(actual.getUpdatedAt());
                assertNull(actual.getVersionNo());
                assertNull(actual.getDeleteNo());

                return 1L;
            }
        };

        Long id = service.save(arg);
        assertNotNull(id);
    }

    /**
     * {@link ProjectCustomSettingServiceImpl#save(ProjectCustomSetting)}のテストケース.
     * 正常系(更新)
     * @throws Exception
     */
    @Test
    public void testSaveForUpdate() throws Exception {
        ProjectCustomSetting arg = createProjectCustomSettingDummy();

        // Mock準備
        MockAbstractService.IS_PROJECT_ADMIN = true;
        new MockUp<ProjectCustomSettingDaoImpl>() {
            @Mock int update(ProjectCustomSetting actual) {
                assertNotNull(actual);
                assertNotNull(actual.getId());
                assertEquals(arg.getProjectId(), actual.getProjectId());
                assertEquals(arg.getDefaultStatus(), actual.getDefaultStatus());
                assertEquals(arg.isUsePersonInCharge(), actual.isUsePersonInCharge());

                return 1;
            }
        };

        Long id = service.save(arg);
        assertNotNull(id);
    }

    /**
     * {@link ProjectCustomSettingServiceImpl#save(ProjectCustomSetting)}のテストケース.
     * 異常系(projectCustomSetting が null)
     * @throws Exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSaveArgumentNull() throws Exception {
        ProjectCustomSetting arg = null;
        MockAbstractService.IS_PROJECT_ADMIN = true;

        service.save(arg);
    }

    /**
     * {@link ProjectCustomSettingServiceImpl#save(ProjectCustomSetting)}のテストケース.
     * 異常系(GroupAdmin権限)
     * @throws Exception
     */
    @Test(expected = ServiceAbortException.class)
    public void testSaveByGroupAdmin() throws Exception {
        ProjectCustomSetting arg = createProjectCustomSettingDummy();
        MockAbstractService.IS_ANY_GROUP_ADMIN = true;

        service.save(arg);
    }

    /**
     * {@link ProjectCustomSettingServiceImpl#save(ProjectCustomSetting)}のテストケース.
     * 異常系(NormalUser権限)
     * @throws Exception
     */
    @Test(expected = ServiceAbortException.class)
    public void testSaveByNormalUser() throws Exception {
        ProjectCustomSetting arg = createProjectCustomSettingDummy();

        service.save(arg);
    }

    /**
     * {@link ProjectCustomSettingServiceImpl#save(ProjectCustomSetting)}のテストケース.
     * 異常系(新規登録でKeyDuplicateExceptionが発生)
     * @throws Exception
     */
    @Test(expected = ServiceAbortException.class)
    public void testSaveForInsertKeyDuplicateException() throws Exception {
        ProjectCustomSetting arg = new ProjectCustomSetting();

        // Mock準備
        MockAbstractService.IS_SYSTEM_ADMIN = true;
        new MockUp<ProjectCustomSettingDaoImpl>() {
            @Mock Long create(ProjectCustomSetting e) throws KeyDuplicateException {
                throw new KeyDuplicateException();
            }
        };

        service.save(arg);
    }

    /**
     * {@link ProjectCustomSettingServiceImpl#save(ProjectCustomSetting)}のテストケース.
     * 異常系(更新でKeyDuplicateExceptionが発生)
     * @throws Exception
     */
    @Test(expected = ServiceAbortException.class)
    public void testSaveForUpdateKeyDuplicateException() throws Exception {
        ProjectCustomSetting arg = createProjectCustomSettingDummy();

        // Mock準備
        MockAbstractService.IS_PROJECT_ADMIN = true;
        new MockUp<ProjectCustomSettingDaoImpl>() {
            @Mock int update(ProjectCustomSetting e) throws KeyDuplicateException {
                throw new KeyDuplicateException();
            }
        };

        service.save(arg);
    }

    /**
     * {@link ProjectCustomSettingServiceImpl#save(ProjectCustomSetting)}のテストケース.
     * 異常系(更新でStaleEwcordExceptionが発生)
     * @throws Exception
     */
    @Test(expected = ServiceAbortException.class)
    public void testSaveForUpdateStaleEwcordException() throws Exception {
        ProjectCustomSetting arg = createProjectCustomSettingDummy();

        // Mock準備
        MockAbstractService.IS_PROJECT_ADMIN = true;
        new MockUp<ProjectCustomSettingDaoImpl>() {
            @Mock int update(ProjectCustomSetting e) throws KeyDuplicateException, StaleRecordException {
                throw new StaleRecordException();
            }
        };

        service.save(arg);
    }

    private ProjectCustomSetting createProjectCustomSettingDummy() {
        ProjectCustomSetting rt = new ProjectCustomSetting();
        rt.setId(100L);
        rt.setProjectId(project.getProjectId());
        rt.setDefaultStatus(CorresponStatus.CLOSED);
        rt.setUsePersonInCharge(false);
        User u1 = new User();
        u1.setEmpNo("ZZA02");
        rt.setCreatedBy(u1);
        rt.setCreatedAt(Calendar.getInstance().getTime());
        User u2 = new User();
        u2.setEmpNo("ZZA03");
        rt.setUpdatedBy(u2);
        rt.setUpdatedAt(Calendar.getInstance().getTime());
        rt.setVersionNo(1L);
        rt.setDeleteNo(0L);
        return rt;
    }
}
