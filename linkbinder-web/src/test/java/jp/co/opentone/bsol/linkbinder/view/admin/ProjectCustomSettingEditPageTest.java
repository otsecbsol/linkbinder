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
import jp.co.opentone.bsol.linkbinder.dto.Project;
import jp.co.opentone.bsol.linkbinder.dto.ProjectCustomSetting;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.code.CorresponStatus;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.service.admin.impl.ProjectCustomSettingServiceImpl;
import jp.co.opentone.bsol.linkbinder.view.AbstractPage;
import mockit.Mock;
import mockit.MockUp;

/**
 * @author opentone
 */
public class ProjectCustomSettingEditPageTest extends AbstractTestCase{

    /**
     * 自分の情報設定時の表題.
     */
    private static final String TITLE_MINE = "プロジェクト設定";

    /**
     * テスト対象.
     */
    @Resource
    private ProjectCustomSettingEditPage page;

    /**
     * テストの前準備.
     */
    @BeforeClass
    public static void testSetUp() {
        FacesContextMock.initialize();
        new MockAbstractPage();
        new MockProjectCustomSettingService();
    }

    /**
     * 後始末.
     */
    @AfterClass
    public static void testTeardown() {
        new MockAbstractPage().tearDown();
        new MockProjectCustomSettingService().tearDown();
        FacesContextMock.tearDown();
        MockAbstractPage.RET_SYSTEM_ADMIN = true;
        MockAbstractPage.RET_PROJECT_ADMIN = true;
        MockAbstractPage.RET_GROUP_ADMIN = true;
        MockAbstractPage.RET_NORMAL_USER = true;
        MockProjectCustomSettingService.IS_ERROR = true;
        FacesContextMock.EXPECTED_MESSAGE = null;
    }

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        MockProjectCustomSettingService.RET_FIND_PROJECT_SETTINGS = null;

        MockAbstractPage.RET_SYSTEM_ADMIN = false;
        MockAbstractPage.RET_PROJECT_ADMIN = false;
        MockAbstractPage.RET_GROUP_ADMIN = false;
        MockAbstractPage.RET_NORMAL_USER = false;
        MockAbstractPage.RET_CURRENT_PROJECT_ID = null;
        MockProjectCustomSettingService.IS_ERROR = false;
        page.setSettingUsePersonInCharge(false);
        page.setSettingDefaultStatus(1);
        page.setSettingUseAccessControl(true);
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {}

    /**
     * {@link ProjectCustomSettingEditPage.java#initialize()} のためのテスト・メソッド.
     * (SystemAdmin)
     */
    @Test
    public void testInitializeSystemAdmin() throws Exception {
        String userId = "00001";
        User loginUser = new User();
        loginUser.setEmpNo(userId);
        MockAbstractPage.RET_CURRENT_USER = loginUser;
        MockAbstractPage.RET_CURRENT_PROJECT = getProjectDummy();
        MockAbstractPage.RET_CURRENT_PROJECT_ID = MockAbstractPage.RET_CURRENT_PROJECT.getProjectId();
        MockAbstractPage.RET_SYSTEM_ADMIN = true;
        ProjectCustomSetting settings = getProjectCustomSettingDummy(CorresponStatus.CLOSED, false, false);
        MockProjectCustomSettingService.RET_FIND_PROJECT_SETTINGS = settings;

        page.initialize();

        assertNotNull(page);
        assertEquals(TITLE_MINE, page.getTitle());
        assertEquals(settings.getDefaultStatus().getValue(), page.getSettingDefaultStatus());
        assertEquals(settings.isUsePersonInCharge(), page.isSettingUsePersonInCharge());
        assertEquals(settings.getDefaultStatus(),
            page.getCurrentProject().getProjectCustomSetting().getDefaultStatus());
        assertEquals(settings.isUsePersonInCharge(),
            page.getCurrentProject().getProjectCustomSetting().isUsePersonInCharge());
        assertEquals(settings.isUseCorresponAccessControl(),
                page.getCurrentProject().getProjectCustomSetting().isUseCorresponAccessControl());
    }

    /**
     * {@link ProjectCustomSettingEditPage.java#initialize()} のためのテスト・メソッド.
     * (ProjectAdmin)
     */
    @Test
    public void testInitializeProjectAdmin() throws Exception {
        String userId = "00001";
        User loginUser = new User();
        loginUser.setEmpNo(userId);
        MockAbstractPage.RET_CURRENT_USER = loginUser;
        MockAbstractPage.RET_CURRENT_PROJECT = getProjectDummy();
        MockAbstractPage.RET_CURRENT_PROJECT_ID = MockAbstractPage.RET_CURRENT_PROJECT.getProjectId();
        MockAbstractPage.RET_PROJECT_ADMIN = true;
        ProjectCustomSetting settings = getProjectCustomSettingDummy(CorresponStatus.CLOSED, false, false);
        MockProjectCustomSettingService.RET_FIND_PROJECT_SETTINGS = settings;

        page.initialize();

        assertNotNull(page);
        assertEquals(TITLE_MINE, page.getTitle());
        assertEquals(settings.getDefaultStatus().getValue(), page.getSettingDefaultStatus());
        assertEquals(settings.isUsePersonInCharge(), page.isSettingUsePersonInCharge());
        assertEquals(settings.getDefaultStatus(),
            page.getCurrentProject().getProjectCustomSetting().getDefaultStatus());
        assertEquals(settings.isUsePersonInCharge(),
            page.getCurrentProject().getProjectCustomSetting().isUsePersonInCharge());
        assertEquals(settings.isUseCorresponAccessControl(),
                page.getCurrentProject().getProjectCustomSetting().isUseCorresponAccessControl());
    }

    /**
     * {@link ProjectCustomSettingEditPage.java#initialize()} のためのテスト・メソッド.
     * (GroupAdmin)
     * @throws Exception
     */
    @Test
    public void testInitializeGroupAdmin() throws Exception {
        // 期待されるメッセージをセット
        FacesContextMock.EXPECTED_MESSAGE =
                new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                    FacesMessage.SEVERITY_ERROR.toString(),
                    createExpectedMessageString(
                        Messages.getMessageAsString(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT),
                        "Initialize"));

        String userId = "00001";
        User loginUser = new User();
        loginUser.setEmpNo(userId);
        MockAbstractPage.RET_CURRENT_USER = loginUser;
        MockAbstractPage.RET_CURRENT_PROJECT = getProjectDummy();
        MockAbstractPage.RET_CURRENT_PROJECT_ID = MockAbstractPage.RET_CURRENT_PROJECT.getProjectId();
        MockAbstractPage.RET_GROUP_ADMIN = true;
        ProjectCustomSetting settings = getProjectCustomSettingDummy(CorresponStatus.CLOSED, false, false);
        MockProjectCustomSettingService.RET_FIND_PROJECT_SETTINGS = settings;

        try {
            page.initialize();
            fail("例外が発生していない");
        } catch (InvalidOperationRuntimeException e) {
            ServiceAbortException actual = (ServiceAbortException) e.getCause();
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT, actual.getMessageCode());
        }
    }

    /**
     * {@link ProjectCustomSettingEditPage.java#initialize()} のためのテスト・メソッド.
     * (NormalUser)
     * @throws Exception
     */
    @Test
    public void testInitializeNormalUser() throws Exception {
        // 期待されるメッセージをセット
        FacesContextMock.EXPECTED_MESSAGE =
                new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                    FacesMessage.SEVERITY_ERROR.toString(),
                    createExpectedMessageString(
                        Messages.getMessageAsString(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT),
                        "Initialize"));

        String userId = "00001";
        User loginUser = new User();
        loginUser.setEmpNo(userId);
        MockAbstractPage.RET_CURRENT_USER = loginUser;
        MockAbstractPage.RET_CURRENT_PROJECT = getProjectDummy();
        MockAbstractPage.RET_CURRENT_PROJECT_ID = MockAbstractPage.RET_CURRENT_PROJECT.getProjectId();
        ProjectCustomSetting settings = getProjectCustomSettingDummy(CorresponStatus.CLOSED, false, false);
        MockProjectCustomSettingService.RET_FIND_PROJECT_SETTINGS = settings;

        try {
            page.initialize();
            fail("例外が発生していない");
        } catch (InvalidOperationRuntimeException e) {
            ServiceAbortException actual = (ServiceAbortException) e.getCause();
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT, actual.getMessageCode());
        }
    }

    /**
     * {@link ProjectCustomSettingEditPage#save()} のためのテスト・メソッド.
     * (SystemAdmin)
     */
    @Test
    public void testSaveSystemAdmin() {
        // 期待されるメッセージをセット
        FacesContextMock.EXPECTED_MESSAGE =
                new FacesMessage(
                    FacesMessage.SEVERITY_INFO,
                    FacesMessage.SEVERITY_INFO.toString(),
                    Messages.getMessageAsString(ApplicationMessageCode.PROJECT_CUSTOM_SETTING_SAVED));

        String userId = "00001";
        User loginUser = new User();
        loginUser.setEmpNo(userId);
        MockAbstractPage.RET_CURRENT_PROJECT = getProjectDummy();
        ProjectCustomSetting pcs = getProjectCustomSettingDummy(CorresponStatus.CLOSED, false, false);
        MockProjectCustomSettingService.RET_FIND_PROJECT_SETTINGS = pcs;
        MockAbstractPage.RET_SYSTEM_ADMIN = true;

        page.setProjectCustomSetting(pcs);
        page.setSettingDefaultStatus(pcs.getDefaultStatus().getValue());
        page.setSettingUsePersonInCharge(pcs.isUsePersonInCharge());

        page.save();

        assertEquals(pcs.getDefaultStatus(), MockProjectCustomSettingService.SET_SAVE.getDefaultStatus());
        assertEquals(pcs.isUsePersonInCharge(), MockProjectCustomSettingService.SET_SAVE.isUsePersonInCharge());

        assertEquals(pcs.getDefaultStatus().getValue(), page.getSettingDefaultStatus());
        assertEquals(pcs.isUsePersonInCharge(), page.isSettingUsePersonInCharge());
        assertEquals(pcs.getDefaultStatus(), page.getCurrentProject().getProjectCustomSetting().getDefaultStatus());
        assertEquals(pcs.isUsePersonInCharge(), page.getCurrentProject().getProjectCustomSetting().isUsePersonInCharge());
        assertEquals(pcs.isUseCorresponAccessControl(), page.getCurrentProject().getProjectCustomSetting().isUseCorresponAccessControl());

    }

    /**
     * {@link ProjectCustomSettingEditPage#save()} のためのテスト・メソッド.
     * (ProjectAdmin)
     */
    @Test
    public void testSaveProjectAdmin() {
        // 期待されるメッセージをセット
        FacesContextMock.EXPECTED_MESSAGE =
                new FacesMessage(
                    FacesMessage.SEVERITY_INFO,
                    FacesMessage.SEVERITY_INFO.toString(),
                    Messages.getMessageAsString(ApplicationMessageCode.PROJECT_CUSTOM_SETTING_SAVED));

        String userId = "00001";
        User loginUser = new User();
        loginUser.setEmpNo(userId);
        MockAbstractPage.RET_CURRENT_PROJECT = getProjectDummy();
        ProjectCustomSetting pcs = getProjectCustomSettingDummy(CorresponStatus.CLOSED, false, false);
        MockProjectCustomSettingService.RET_FIND_PROJECT_SETTINGS = pcs;
        MockAbstractPage.RET_PROJECT_ADMIN = true;

        page.setProjectCustomSetting(pcs);
        page.setSettingDefaultStatus(pcs.getDefaultStatus().getValue());
        page.setSettingUsePersonInCharge(pcs.isUsePersonInCharge());

        page.save();

        assertEquals(pcs.getDefaultStatus(), MockProjectCustomSettingService.SET_SAVE.getDefaultStatus());
        assertEquals(pcs.isUsePersonInCharge(), MockProjectCustomSettingService.SET_SAVE.isUsePersonInCharge());

        assertEquals(pcs.getDefaultStatus().getValue(), page.getSettingDefaultStatus());
        assertEquals(pcs.isUsePersonInCharge(), page.isSettingUsePersonInCharge());
        assertEquals(pcs.getDefaultStatus(), page.getCurrentProject().getProjectCustomSetting().getDefaultStatus());
        assertEquals(pcs.isUsePersonInCharge(), page.getCurrentProject().getProjectCustomSetting().isUsePersonInCharge());
        assertEquals(pcs.isUseCorresponAccessControl(), page.getCurrentProject().getProjectCustomSetting().isUseCorresponAccessControl());

    }

    @Test
    public void testSaveFailure() {
        // 期待されるメッセージをセット
        FacesContextMock.EXPECTED_MESSAGE =
                new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                    FacesMessage.SEVERITY_ERROR.toString(),
                    createExpectedMessageString(
                        Messages.getMessageAsString(
                            ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_PROJECT_CUSTOM_SETTING_NOT_EXIST).replace("'", "'''"),
                            "Initialize"));

        String userId = "00001";
        User loginUser = new User();
        loginUser.setEmpNo(userId);
        MockAbstractPage.RET_CURRENT_PROJECT = getProjectDummy();
        ProjectCustomSetting pcs = getProjectCustomSettingDummy(CorresponStatus.CLOSED, false, false);
        MockProjectCustomSettingService.RET_FIND_PROJECT_SETTINGS = pcs;
        MockAbstractPage.RET_PROJECT_ADMIN = true;
        MockProjectCustomSettingService.IS_ERROR = true;

        page.setProjectCustomSetting(pcs);
        page.setSettingDefaultStatus(pcs.getDefaultStatus().getValue());
        page.setSettingUsePersonInCharge(pcs.isUsePersonInCharge());
        page.setSettingUseAccessControl(pcs.isUseCorresponAccessControl());

        page.save();
    }


    private Project getProjectDummy() {
        Project result = new Project();

        result.setProjectId("TEST-TEST");
        result.setProjectCustomSetting(getProjectCustomSettingDummy());

        return result;
    }

    private ProjectCustomSetting getProjectCustomSettingDummy() {
        return getProjectCustomSettingDummy(CorresponStatus.OPEN, true, false);
    }
    private ProjectCustomSetting getProjectCustomSettingDummy(CorresponStatus cs, boolean usePic, boolean useAcc) {
        ProjectCustomSetting rt = new ProjectCustomSetting();
        rt.setDefaultStatus(cs);
        rt.setUsePersonInCharge(usePic);
        rt.setUseCorresponAccessControl(useAcc);
        return rt;
    }

    public static class MockProjectCustomSettingService extends MockUp<ProjectCustomSettingServiceImpl> {
        static ProjectCustomSetting RET_FIND_PROJECT_SETTINGS;
        static ProjectCustomSetting SET_SAVE;
        static final Long RET_ID = null;
        static boolean IS_ERROR;

        @Mock
        public ProjectCustomSetting find(String projectId) throws ServiceAbortException {
            return RET_FIND_PROJECT_SETTINGS;
        }

        @Mock
        public Long save(ProjectCustomSetting projectCustomSetting) throws ServiceAbortException {
            if (IS_ERROR) {
//                ServiceAbortException actual = (ServiceAbortException) e.getCause();
                throw new ServiceAbortException(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_PROJECT_CUSTOM_SETTING_NOT_EXIST);
            }
            SET_SAVE = projectCustomSetting;
            return RET_ID;
        }

    }

    public static class MockAbstractPage extends MockUp<AbstractPage> {
        static User RET_CURRENT_USER;
        static boolean RET_SYSTEM_ADMIN;
        static boolean RET_PROJECT_ADMIN;
        static boolean RET_GROUP_ADMIN;
        static boolean RET_NORMAL_USER;
        static String RET_CURRENT_PROJECT_ID;
        static Project RET_CURRENT_PROJECT;

        @Mock
        public boolean isSystemAdmin() {
            return RET_SYSTEM_ADMIN;
        }
        @Mock
        public boolean isProjectAdmin(String checkProjectId) {
            return RET_PROJECT_ADMIN;
        }
        @Mock
        public boolean isGroupAdmin(Long corresponGroupId) {
            return RET_GROUP_ADMIN;
        }
        @Mock
        public String getCurrentProjectId() {
            return RET_CURRENT_PROJECT_ID;
        }
        @Mock
        public Project getCurrentProject() {
            return RET_CURRENT_PROJECT;
        }
        @Mock
        public void setCurrentProjectInfo(Project project) {
            RET_CURRENT_PROJECT = project;
            RET_CURRENT_PROJECT.setProjectCustomSetting(MockProjectCustomSettingService.RET_FIND_PROJECT_SETTINGS);
            RET_CURRENT_PROJECT_ID = project.getProjectId();
        }
    }
}
