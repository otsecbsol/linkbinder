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
package jp.co.opentone.bsol.linkbinder.view.common;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.faces.application.FacesMessage;
import javax.faces.model.SelectItem;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import jp.co.opentone.bsol.framework.core.exception.InvalidOperationRuntimeException;
import jp.co.opentone.bsol.framework.core.message.MessageCode;
import jp.co.opentone.bsol.framework.core.message.Messages;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.extension.jsf.FacesContextMock;
import jp.co.opentone.bsol.framework.test.AbstractTestCase;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroup;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroupUser;
import jp.co.opentone.bsol.linkbinder.dto.Project;
import jp.co.opentone.bsol.linkbinder.dto.ProjectUser;
import jp.co.opentone.bsol.linkbinder.dto.ProjectUserSetting;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.UserSettings;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.service.admin.impl.UserServiceImpl;
import jp.co.opentone.bsol.linkbinder.view.AbstractPage;
import mockit.Mock;
import mockit.MockUp;

/**
 * ユーザー情報表示・変更画面のテスト.
 * @author opentone
 */
public class UserSettingsPageTest extends AbstractTestCase {

    /**
     * テスト対象.
     */
    @Resource
    private UserSettingsPage page;

    /**
     * 結果：部門管理者の値.
     * ※linkbinder-config.properties参照.
     */
    private final String GROUP_ADMIN = "30";

    /**
     * 結果：一般ユーザーの値.
     * ※linkbinder-config.properties参照.
     */
    private final String NORMAL_USER = "40";

    /**
     * 結果：表題（自身のユーザー設定）.
     */
    private final String TITLE_MINE = "個人設定";

    /**
     * 結果：表題（他ユーザーのユーザー設定）.
     */
    private final String TITLE_USER = "ユーザー設定";

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        FacesContextMock.initialize();
        new MockAbstractPage();
        new MockUserService();
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        new MockAbstractPage().tearDown();
        new MockUserService().tearDown();
        FacesContextMock.tearDown();
    }

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        MockUserService.RET_FIND_USER_SETTINGS = null;
        MockAbstractPage.RET_CURRENT_USER = null;
        MockAbstractPage.RET_SYSTEM_ADMIN = false;
        MockAbstractPage.RET_PROJECT_ADMIN = false;
        MockAbstractPage.RET_GROUP_ADMIN = false;
        MockAbstractPage.RET_CURRENT_PROJECT_ID = null;

        page.setId(null);
        page.setEditingProject(false);
        page.setViewAllProject(false);
        page.setEditingProjectUserProfile(false);
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.view.common.UserSettingsPage#initialize()} のためのテスト・メソッド.
     * SystemAdminが自身の情報を設定. + デフォルト設定無し.
     */
    @Test
    public void testInitializeSystemAdmin_Mine() {
        String userId = "00001";

        User loginUser = new User();
        loginUser.setEmpNo(userId);
        MockAbstractPage.RET_CURRENT_USER = loginUser;

        MockAbstractPage.RET_SYSTEM_ADMIN = true;

        UserSettings settings = new UserSettings();
        settings.setUser(loginUser);
        List<ProjectUserSetting> pSettings = createProjectUserSettingList(false);
        settings.setProjectUserSettingList(pSettings);
        MockUserService.RET_FIND_USER_SETTINGS = settings;
        MockAbstractPage.RET_CURRENT_PROJECT_ID = pSettings.get(0).getProject().getProjectId();

        page.setId(userId);
        page.setBackPage("UserIndex");
        page.initialize();

        assertEquals(settings.getUser(), page.getUserSettings().getUser());
        assertEquals(pSettings.size(),
                     page.getUserSettings().getProjectUserSettingList().size());
        for (int i = 0; i < pSettings.size(); i++) {
            ProjectUserSetting expSetting = pSettings.get(i);
            ProjectUserSetting actSetting = page.getUserSettings().getProjectUserSettingList().get(i);
            assertEquals(expSetting.getProject().getProjectId(), actSetting.getProject().getProjectId());
        }
        assertNull(page.getDefaultProjectId());
        for (ProjectUserSetting pSetting : page.getUserSettings().getProjectUserSettingList()) {
            assertNull(pSetting.getDefaultCorresponGroupId());
            assertNull(pSetting.getRole());
        }

        assertEquals(GROUP_ADMIN, page.getGroupAdmin());
        assertEquals(NORMAL_USER, page.getNormalUser());

        assertEquals(TITLE_MINE, page.getTitle());

        assertTrue(page.isEditingProject());
        assertTrue(page.isViewAllProject());
        assertTrue(page.isEditingProjectUserProfile());

        assertEquals(pSettings.size(),
                     page.getProjectSelectItems().size());
        for (int i = 0; i < pSettings.size(); i++) {
            SelectItem item = page.getProjectSelectItems().get(i);
            Project project = pSettings.get(i).getProject();
            assertEquals(project.getProjectId(), item.getValue());
            assertEquals(project.getProjectId() + " : " + project.getNameE(), item.getLabel());
        }
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.view.common.UserSettingsPage#initialize()} のためのテスト・メソッド.
     * SystemAdminが他ユーザの情報を設定. + デフォルト設定あり.
     */
    @Test
    public void testInitializeSystemAdmin_User() {
        String userId = "00001";
        String defaultProject = "PJ2";

        User loginUser = new User();
        loginUser.setEmpNo("00002");
        MockAbstractPage.RET_CURRENT_USER = loginUser;

        MockAbstractPage.RET_SYSTEM_ADMIN = true;

        UserSettings settings = new UserSettings();
        User user = new User();
        user.setEmpNo(userId);
        user.setDefaultProjectId(defaultProject);
        settings.setUser(user);
        List<ProjectUserSetting> pSettings = createProjectUserSettingList(false);
        settings.setProjectUserSettingList(pSettings);
        MockUserService.RET_FIND_USER_SETTINGS = settings;
        MockAbstractPage.RET_CURRENT_PROJECT_ID = pSettings.get(0).getProject().getProjectId();

        page.setId(userId);
        page.setBackPage("UserIndex");
        page.initialize();

        assertEquals(pSettings.size(),
                     page.getUserSettings().getProjectUserSettingList().size());
        for (int i = 0; i < pSettings.size(); i++) {
           ProjectUserSetting expSetting = pSettings.get(i);
           ProjectUserSetting actSetting = page.getUserSettings().getProjectUserSettingList().get(i);
           assertEquals(expSetting.getProject().getProjectId(), actSetting.getProject().getProjectId());
        }
        assertEquals(defaultProject, page.getDefaultProjectId());
        assertEquals(pSettings.size(),
                     page.getUserSettings().getProjectUserSettingList().size());
        for (int i = 0; i < pSettings.size(); i++) {
            ProjectUserSetting expSetting = pSettings.get(i);
            ProjectUserSetting actSetting = page.getUserSettings().getProjectUserSettingList().get(i);
            // デフォルト設定についてはcreateProjectUserSettingList()参照
            assertEquals(expSetting.getCorresponGroupUserList().get(0).getId(),
                         actSetting.getDefaultCorresponGroupId());
            assertNull(actSetting.getRole());
        }

        assertEquals(GROUP_ADMIN, page.getGroupAdmin());
        assertEquals(NORMAL_USER, page.getNormalUser());

        assertEquals(TITLE_USER, page.getTitle());

        assertTrue(page.isEditingProject());
        assertTrue(page.isViewAllProject());
        assertTrue(page.isEditingProjectUserProfile());

        assertEquals(pSettings.size(),
                     page.getProjectSelectItems().size());
        for (int i = 0; i < pSettings.size(); i++) {
            SelectItem item = page.getProjectSelectItems().get(i);
            Project project = pSettings.get(i).getProject();
            assertEquals(project.getProjectId(), item.getValue());
            assertEquals(project.getProjectId() + " : " + project.getNameE(), item.getLabel());
        }
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.view.common.UserSettingsPage#initialize()} のためのテスト・メソッド.
     * ProjectAdminが自身のユーザ情報を設定. + デフォルト設定あり.
     */
    @Test
    public void testInitializeProjectAdmin_Mine() {
        String userId = "00001";
        String defaultProject = "PJ1";

        User loginUser = new User();
        loginUser.setEmpNo(userId);
        loginUser.setDefaultProjectId(defaultProject);
        MockAbstractPage.RET_CURRENT_USER = loginUser;

        MockAbstractPage.RET_PROJECT_ADMIN = true;
        MockAbstractPage.RET_CURRENT_PROJECT_ID = "PJ2";

        UserSettings settings = new UserSettings();
        settings.setUser(loginUser);
        List<ProjectUserSetting> pSettings = createProjectUserSettingList(true);
        settings.setProjectUserSettingList(pSettings);
        MockUserService.RET_FIND_USER_SETTINGS = settings;
        MockAbstractPage.RET_CURRENT_PROJECT_ID = pSettings.get(0).getProject().getProjectId();

        page.setId(userId);
        page.setBackPage("UserIndex");
        page.initialize();

        assertEquals(pSettings.size(),
                     page.getUserSettings().getProjectUserSettingList().size());
        for (int i = 0; i < pSettings.size(); i++) {
            ProjectUserSetting expSetting = pSettings.get(i);
            ProjectUserSetting actSetting = page.getUserSettings().getProjectUserSettingList().get(i);
            assertEquals(expSetting.getProject().getProjectId(), actSetting.getProject().getProjectId());
         }
        assertEquals(defaultProject, page.getDefaultProjectId());
        assertEquals(pSettings.size(),
                     page.getUserSettings().getProjectUserSettingList().size());
        for (int i = 0; i < pSettings.size(); i++) {
            ProjectUserSetting expSetting = pSettings.get(i);
            ProjectUserSetting actSetting = page.getUserSettings().getProjectUserSettingList().get(i);
            // デフォルト設定についてはcreateProjectUserSettingList()参照
            assertEquals(expSetting.getCorresponGroupUserList().get(0).getCorresponGroup().getId(),
                         actSetting.getDefaultCorresponGroupId());
            assertEquals(expSetting.getProjectUser().getUser().getRole(),
                         actSetting.getRole());
        }

        assertEquals(GROUP_ADMIN, page.getGroupAdmin());
        assertEquals(NORMAL_USER, page.getNormalUser());

        assertEquals(TITLE_MINE, page.getTitle());

        assertTrue(page.isEditingProject());
        assertTrue(page.isViewAllProject());
        assertTrue(page.isEditingProjectUserProfile());

        assertEquals(pSettings.size(),
                     page.getProjectSelectItems().size());
        for (int i = 0; i < pSettings.size(); i++) {
            SelectItem item = page.getProjectSelectItems().get(i);
            Project project = pSettings.get(i).getProject();
            assertEquals(project.getProjectId(), item.getValue());
            assertEquals(project.getProjectId() + " : " + project.getNameE(), item.getLabel());
        }
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.view.common.UserSettingsPage#initialize()} のためのテスト・メソッド.
     * ProjectAdminが他ユーザの情報を設定. + デフォルト設定なし.
     */
    @Test
    public void testInitializeProjectmAdmin_User() {
        String userId = "00001";

        User loginUser = new User();
        loginUser.setEmpNo("00002");
        MockAbstractPage.RET_CURRENT_USER = loginUser;

        MockAbstractPage.RET_PROJECT_ADMIN = true;
        MockAbstractPage.RET_CURRENT_PROJECT_ID = "PJ2";

        UserSettings settings = new UserSettings();
        User user = new User();
        user.setEmpNo(userId);
        settings.setUser(user);
        List<ProjectUserSetting> pSettings = createProjectUserSettingList(false);
        settings.setProjectUserSettingList(pSettings);
        MockUserService.RET_FIND_USER_SETTINGS = settings;

        ProjectUserSetting expProjectSettings = pSettings.get(1); // PJ2

        page.setId(userId);
        page.setBackPage("UserIndex");
        page.initialize();

        // フィルタリングされている
        assertEquals(1,
                     page.getUserSettings().getProjectUserSettingList().size());
        assertEquals(expProjectSettings.getProject().getProjectId(),
                     page.getUserSettings().getProjectUserSettingList().get(0)
                         .getProject().getProjectId());
        assertNull(page.getDefaultProjectId());
        // フィルタリングされている
        assertEquals(1,
                     page.getUserSettings().getProjectUserSettingList().size());
        assertNull(page.getUserSettings().getProjectUserSettingList().get(0)
                       .getDefaultCorresponGroupId());
        assertNull(page.getUserSettings().getProjectUserSettingList().get(0).getRole());

        assertEquals(GROUP_ADMIN, page.getGroupAdmin());
        assertEquals(NORMAL_USER, page.getNormalUser());

        assertEquals(TITLE_USER, page.getTitle());

        assertFalse(page.isEditingProject());
        assertFalse(page.isViewAllProject());
        assertTrue(page.isEditingProjectUserProfile());

        assertEquals(1,
                     page.getProjectSelectItems().size());
        SelectItem item = page.getProjectSelectItems().get(0);
        Project project = expProjectSettings.getProject();
        assertEquals(project.getProjectId(), item.getValue());
        assertEquals(project.getProjectId() + " : " + project.getNameE(), item.getLabel());
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.view.common.UserSettingsPage#initialize()} のためのテスト・メソッド.
     * GroupAdminが自身のユーザ情報を設定. + デフォルト設定なし.
     */
    @Test
    public void testInitializeGroupAdmin_Mine() {
        String userId = "00001";

        User loginUser = new User();
        loginUser.setEmpNo(userId);
        MockAbstractPage.RET_CURRENT_USER = loginUser;

        MockAbstractPage.RET_GROUP_ADMIN = true;
        MockAbstractPage.RET_CURRENT_PROJECT_ID = "PJ2";

        UserSettings settings = new UserSettings();
        settings.setUser(loginUser);
        List<ProjectUserSetting> pSettings = createProjectUserSettingList(false);
        settings.setProjectUserSettingList(pSettings);
        MockUserService.RET_FIND_USER_SETTINGS = settings;
        MockAbstractPage.RET_CURRENT_PROJECT_ID = pSettings.get(0).getProject().getProjectId();

        page.setId(userId);
        page.setBackPage("UserIndex");
        page.initialize();

        assertEquals(pSettings.size(),
                     page.getUserSettings().getProjectUserSettingList().size());
        for (int i = 0; i < pSettings.size(); i++) {
            assertNull(page.getUserSettings().getProjectUserSettingList().get(0)
                           .getDefaultCorresponGroupId());
            assertNull(page.getUserSettings().getProjectUserSettingList().get(0).getRole());
        }
        assertNull(page.getDefaultProjectId());
        assertEquals(pSettings.size(),
                     page.getUserSettings().getProjectUserSettingList().size());
        for (int i = 0; i < pSettings.size(); i++) {
            assertNull(page.getUserSettings().getProjectUserSettingList().get(i)
                           .getDefaultCorresponGroupId());
            assertNull(page.getUserSettings().getProjectUserSettingList().get(i).getRole());
        }

        assertEquals(GROUP_ADMIN, page.getGroupAdmin());
        assertEquals(NORMAL_USER, page.getNormalUser());

        assertEquals(TITLE_MINE, page.getTitle());

        assertTrue(page.isEditingProject());
        assertTrue(page.isViewAllProject());
        assertTrue(page.isEditingProjectUserProfile());

        assertEquals(pSettings.size(),
                     page.getProjectSelectItems().size());
        for (int i = 0; i < pSettings.size(); i++) {
            SelectItem item = page.getProjectSelectItems().get(i);
            Project project = pSettings.get(i).getProject();
            assertEquals(project.getProjectId(), item.getValue());
            assertEquals(project.getProjectId() + " : " + project.getNameE(), item.getLabel());
        }
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.view.common.UserSettingsPage#initialize()} のためのテスト・メソッド.
     * GroupAdminが他ユーザの情報を設定. + デフォルト設定あり.
     */
    @Test
    public void testInitializeGroupAdmin_User() {
        String userId = "00001";
        String defaultProject = "PJ1";

        User loginUser = new User();
        loginUser.setEmpNo("00002");
        MockAbstractPage.RET_CURRENT_USER = loginUser;

        MockAbstractPage.RET_GROUP_ADMIN = true;
        MockAbstractPage.RET_CURRENT_PROJECT_ID = "PJ2";

        UserSettings settings = new UserSettings();
        User user = new User();
        user.setEmpNo(userId);
        user.setDefaultProjectId(defaultProject);
        settings.setUser(user);
        List<ProjectUserSetting> pSettings = createProjectUserSettingList(true);
        settings.setProjectUserSettingList(pSettings);
        MockUserService.RET_FIND_USER_SETTINGS = settings;

        ProjectUserSetting expProjectSettings = pSettings.get(1); // PJ2

        page.setId(userId);
        page.setBackPage("UserIndex");
        page.initialize();

        // フィルタリングされている
        assertEquals(1,
                     page.getUserSettings().getProjectUserSettingList().size());
        assertEquals(expProjectSettings.getProject().getProjectId(),
                     page.getUserSettings().getProjectUserSettingList().get(0)
                         .getProject().getProjectId());
        assertEquals(defaultProject, page.getDefaultProjectId());
        // フィルタリングされている
        assertEquals(1,
                     page.getUserSettings().getProjectUserSettingList().size());
        ProjectUserSetting actSetting = page.getUserSettings().getProjectUserSettingList().get(0);
        // デフォルト設定についてはcreateProjectUserSettingList()参照
        assertEquals(expProjectSettings.getCorresponGroupUserList().get(0).getCorresponGroup().getId(),
                     actSetting.getDefaultCorresponGroupId());
        assertEquals(expProjectSettings.getProjectUser().getUser().getRole(),
                     actSetting.getRole());

        assertEquals(GROUP_ADMIN, page.getGroupAdmin());
        assertEquals(NORMAL_USER, page.getNormalUser());

        assertEquals(TITLE_USER, page.getTitle());

        assertFalse(page.isEditingProject());
        assertFalse(page.isViewAllProject());
        assertTrue(page.isEditingProjectUserProfile());

        assertEquals(1,
                     page.getProjectSelectItems().size());
        SelectItem item = page.getProjectSelectItems().get(0);
        Project project = expProjectSettings.getProject();
        assertEquals(project.getProjectId(), item.getValue());
        assertEquals(project.getProjectId() + " : " + project.getNameE(), item.getLabel());
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.view.common.UserSettingsPage#initialize()} のためのテスト・メソッド.
     * NormalUserが自身のユーザ情報を設定. + デフォルト設定あり.
     */
    @Test
    public void testInitializeNormalUser_Mine() {
        String userId = "00001";
        String defaultProject = "PJ1";

        User loginUser = new User();
        loginUser.setEmpNo(userId);
        loginUser.setDefaultProjectId(defaultProject);
        MockAbstractPage.RET_CURRENT_USER = loginUser;

        MockAbstractPage.RET_CURRENT_PROJECT_ID = "PJ2";

        UserSettings settings = new UserSettings();
        settings.setUser(loginUser);
        List<ProjectUserSetting> pSettings = createProjectUserSettingList(true);
        settings.setProjectUserSettingList(pSettings);
        MockUserService.RET_FIND_USER_SETTINGS = settings;
        MockAbstractPage.RET_CURRENT_PROJECT_ID = pSettings.get(0).getProject().getProjectId();

        page.setId(userId);
        page.setBackPage("UserIndex");
        page.initialize();

        assertEquals(pSettings.size(),
                     page.getUserSettings().getProjectUserSettingList().size());
        for (int i = 0; i < pSettings.size(); i++) {
           ProjectUserSetting expSetting = pSettings.get(i);
           ProjectUserSetting actSetting = page.getUserSettings().getProjectUserSettingList().get(i);
           assertEquals(expSetting.getProject().getProjectId(), actSetting.getProject().getProjectId());
        }
        assertEquals(defaultProject, page.getDefaultProjectId());
        assertEquals(pSettings.size(),
                     page.getUserSettings().getProjectUserSettingList().size());
        for (int i = 0; i < pSettings.size(); i++) {
           ProjectUserSetting expSetting = pSettings.get(i);
           ProjectUserSetting actSetting = page.getUserSettings().getProjectUserSettingList().get(i);
           // デフォルト設定についてはcreateProjectUserSettingList()参照
           assertEquals(expSetting.getCorresponGroupUserList().get(0).getCorresponGroup().getId(),
                        actSetting.getDefaultCorresponGroupId());
           assertEquals(expSetting.getProjectUser().getUser().getRole(),
                        actSetting.getRole());
        }

        assertEquals(GROUP_ADMIN, page.getGroupAdmin());
        assertEquals(NORMAL_USER, page.getNormalUser());

        assertEquals(TITLE_MINE, page.getTitle());

        assertTrue(page.isEditingProject());
        assertTrue(page.isViewAllProject());
        assertTrue(page.isEditingProjectUserProfile());

        assertEquals(pSettings.size(),
                     page.getProjectSelectItems().size());
        for (int i = 0; i < pSettings.size(); i++) {
            SelectItem item = page.getProjectSelectItems().get(i);
            Project project = pSettings.get(i).getProject();
            assertEquals(project.getProjectId(), item.getValue());
            assertEquals(project.getProjectId() + " : " + project.getNameE(), item.getLabel());
        }
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.view.common.UserSettingsPage#initialize()} のためのテスト・メソッド.
     * NormalUserが他ユーザの情報を設定. + デフォルト設定なし.
     */
    @Test
    public void testInitializeNormalUser_User() {
        String userId = "00001";

        User loginUser = new User();
        loginUser.setEmpNo("00002");
        MockAbstractPage.RET_CURRENT_USER = loginUser;

        MockAbstractPage.RET_CURRENT_PROJECT_ID = "PJ2";

        UserSettings settings = new UserSettings();
        User user = new User();
        user.setEmpNo(userId);
        settings.setUser(user);
        List<ProjectUserSetting> pSettings = createProjectUserSettingList(false);
        settings.setProjectUserSettingList(pSettings);
        MockUserService.RET_FIND_USER_SETTINGS = settings;

        ProjectUserSetting expProjectSettings = pSettings.get(1); // PJ2

        page.setId(userId);
        page.setBackPage("UserIndex");
        page.initialize();

        // フィルタリングされている
        assertEquals(1,
                     page.getUserSettings().getProjectUserSettingList().size());
        assertEquals(expProjectSettings.getProject().getProjectId(),
                     page.getUserSettings().getProjectUserSettingList().get(0)
                         .getProject().getProjectId());
        assertNull(page.getDefaultProjectId());
        // フィルタリングされている
        assertEquals(1,
                     page.getUserSettings().getProjectUserSettingList().size());
        ProjectUserSetting actSetting = page.getUserSettings().getProjectUserSettingList().get(0);
        assertNull(actSetting.getDefaultCorresponGroupId());
        assertNull(actSetting.getRole());

        assertEquals(GROUP_ADMIN, page.getGroupAdmin());
        assertEquals(NORMAL_USER, page.getNormalUser());

        assertEquals(TITLE_USER, page.getTitle());

        assertFalse(page.isEditingProject());
        assertFalse(page.isViewAllProject());
        assertFalse(page.isEditingProjectUserProfile());

        assertEquals(1,
                     page.getProjectSelectItems().size());
        SelectItem item = page.getProjectSelectItems().get(0);
        Project project = expProjectSettings.getProject();
        assertEquals(project.getProjectId(), item.getValue());
        assertEquals(project.getProjectId() + " : " + project.getNameE(), item.getLabel());
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.view.common.UserSettingsPage#initialize()} のためのテスト・メソッド.
     * 起動元からのパラメータがNULL.
     */
    @Test
    public void testInitializeNullId() {
        // 期待されるメッセージをセット
        FacesContextMock.EXPECTED_MESSAGE =
                new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                    FacesMessage.SEVERITY_ERROR.toString(),
                    createExpectedMessageString(
                        Messages.getMessageAsString(MessageCode.E_INVALID_PARAMETER),
                        null));

        page.setId(null);
        page.setBackPage("UserIndex");
        try {
            page.initialize();
            fail("例外が発生していない");
        } catch (InvalidOperationRuntimeException e) {
            ServiceAbortException actual = (ServiceAbortException)e.getCause();
            assertEquals(ApplicationMessageCode.E_INVALID_PARAMETER, actual.getMessageCode());
        }
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.view.common.UserSettingsPage#save()} のためのテスト・メソッド.
     * プロジェクト情報編集あり.
     */
    @Test
    public void testSaveProjectEditOn() {
        // 期待されるメッセージをセット
        FacesContextMock.EXPECTED_MESSAGE =
                new FacesMessage(
                    FacesMessage.SEVERITY_INFO,
                    FacesMessage.SEVERITY_INFO.toString(),
                    createExpectedMessageString(
                        Messages.getMessageAsString(ApplicationMessageCode.SAVE_SUCCESSFUL),
                        null));

        String defaultProjectId = "PJ3";
        UserSettings settings = new UserSettings();
        User user = new User();
        user.setEmpNo("00003");
        settings.setUser(user);
        List<ProjectUserSetting> pSettings = createProjectUserSettingList(true);
        settings.setProjectUserSettingList(pSettings);

        MockUserService.RET_FIND_USER_SETTINGS = settings;
        page.setEditingProject(true);
        page.setUserSettings(settings);
        page.setDefaultProjectId(defaultProjectId);
        page.save();

        assertEquals(defaultProjectId, MockUserService.SET_SAVE.getDefaultProjectId());
        assertEquals(user, MockUserService.SET_SAVE.getUser());
        assertEquals(pSettings, MockUserService.SET_SAVE.getProjectUserSettingList());
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.view.common.UserSettingsPage#save()} のためのテスト・メソッド.
     * プロジェクト情報編集なし.
     */
    @Test
    public void testSaveProjectEditOff() {
        // 期待されるメッセージをセット
        FacesContextMock.EXPECTED_MESSAGE =
                new FacesMessage(
                    FacesMessage.SEVERITY_INFO,
                    FacesMessage.SEVERITY_INFO.toString(),
                    createExpectedMessageString(
                        Messages.getMessageAsString(ApplicationMessageCode.SAVE_SUCCESSFUL),
                        null));

        String defaultProjectId = "PJ3";
        UserSettings settings = new UserSettings();
        User user = new User();
        user.setEmpNo("00003");
        settings.setUser(user);
        List<ProjectUserSetting> pSettings = createProjectUserSettingList(true);
        settings.setProjectUserSettingList(pSettings);

        MockUserService.RET_FIND_USER_SETTINGS = settings;
        page.setEditingProject(false);
        page.setUserSettings(settings);
        page.setDefaultProjectId(defaultProjectId);
        page.save();

        assertNull(MockUserService.SET_SAVE.getDefaultProjectId());
        assertEquals(user, MockUserService.SET_SAVE.getUser());
        assertEquals(pSettings, MockUserService.SET_SAVE.getProjectUserSettingList());
    }

    /**
     * テスト用のユーザー設定情報を作成する.
     * @param defalt デフォルト活動単位と役職を設定する
     * @return ユーザー設定情報
     */
    private List<ProjectUserSetting> createProjectUserSettingList(boolean defalt) {
        List<ProjectUserSetting> list = new ArrayList<ProjectUserSetting>();

        for (int i = 1; i < 6; i++) {
            ProjectUserSetting setting = new ProjectUserSetting();
            List<CorresponGroupUser> groupUserList = new ArrayList<CorresponGroupUser>();
            CorresponGroupUser groupUser = new CorresponGroupUser();
            CorresponGroup group1 = new CorresponGroup();
            group1.setId(new Long(i * 10));
            group1.setName("Group Name " + i * 10);
            groupUser.setCorresponGroup(group1);
            groupUserList.add(groupUser);
            groupUser = new CorresponGroupUser();
            CorresponGroup group2 = new CorresponGroup();
            group2 = new CorresponGroup();
            group2.setId(new Long(i * 10 + 1));
            group2.setName("Group Name " + i * 10 + 1);
            groupUser.setCorresponGroup(group2);
            groupUserList.add(groupUser);
            setting.setCorresponGroupUserList(groupUserList);
            Project project = new Project();
            project.setProjectId("PJ" + i);
            project.setNameE("Project Name " + i);
            setting.setProject(project);
            ProjectUser pUser = new ProjectUser();
            if (defalt) {
                pUser.setDefaultCorresponGroup(group1);
                User user = new User();
                user.setRole("Role" + i);
                pUser.setUser(user);
            } else {
                pUser.setUser(new User());
            }
            setting.setProjectUser(pUser);
            list.add(setting);
        }

        return list;
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.view.common.UserSettingsPage#initialize()} のためのテスト・メソッド.
     * RSS表示無効.
     */
    @Test
    public void testInitializeRSSDisabled() {
        String userId = "00001";
        String defaultProject = "PJ1";

        User loginUser = new User();
        loginUser.setEmpNo(userId);
        loginUser.setDefaultProjectId(defaultProject);
        MockAbstractPage.RET_CURRENT_USER = loginUser;

        MockAbstractPage.RET_CURRENT_PROJECT_ID = "PJ2";

        UserSettings settings = new UserSettings();
        settings.setUser(loginUser);
        List<ProjectUserSetting> pSettings = createProjectUserSettingList(true);
        settings.setProjectUserSettingList(pSettings);
        MockUserService.RET_FIND_USER_SETTINGS = settings;
        MockAbstractPage.RET_CURRENT_PROJECT_ID = pSettings.get(0).getProject().getProjectId();

        page.setId(userId);
        page.setBackPage("UserIndex");
        page.initialize();

//        assertFalse(page.isEnableRSSView());
        assertEquals(pSettings.size(),
                     page.getUserSettings().getProjectUserSettingList().size());
        for (int i = 0; i < pSettings.size(); i++) {
           ProjectUserSetting expSetting = pSettings.get(i);
           ProjectUserSetting actSetting = page.getUserSettings().getProjectUserSettingList().get(i);
           assertEquals(expSetting.getProject().getProjectId(), actSetting.getProject().getProjectId());
        }
        assertEquals(defaultProject, page.getDefaultProjectId());
        assertEquals(pSettings.size(),
                     page.getUserSettings().getProjectUserSettingList().size());
        for (int i = 0; i < pSettings.size(); i++) {
           ProjectUserSetting expSetting = pSettings.get(i);
           ProjectUserSetting actSetting = page.getUserSettings().getProjectUserSettingList().get(i);
           // デフォルト設定についてはcreateProjectUserSettingList()参照
           assertEquals(expSetting.getCorresponGroupUserList().get(0).getCorresponGroup().getId(),
                        actSetting.getDefaultCorresponGroupId());
           assertEquals(expSetting.getProjectUser().getUser().getRole(),
                        actSetting.getRole());
        }

        assertEquals(GROUP_ADMIN, page.getGroupAdmin());
        assertEquals(NORMAL_USER, page.getNormalUser());

        assertEquals(TITLE_MINE, page.getTitle());

        assertTrue(page.isEditingProject());
        assertTrue(page.isViewAllProject());
        assertTrue(page.isEditingProjectUserProfile());

        assertEquals(pSettings.size(),
                     page.getProjectSelectItems().size());
        for (int i = 0; i < pSettings.size(); i++) {
            SelectItem item = page.getProjectSelectItems().get(i);
            Project project = pSettings.get(i).getProject();
            assertEquals(project.getProjectId(), item.getValue());
            assertEquals(project.getProjectId() + " : " + project.getNameE(), item.getLabel());
        }
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.view.common.UserSettingsPage#initialize()} のためのテスト・メソッド.
     * RSS表示有効.
     */
    @Test
    public void testInitializeRSSEnabled() {
        String userId = "00001";
        String defaultProject = "PJ1";

        User loginUser = new User();
        loginUser.setEmpNo(userId);
        loginUser.setDefaultProjectId(defaultProject);
        MockAbstractPage.RET_CURRENT_USER = loginUser;

        MockAbstractPage.RET_CURRENT_PROJECT_ID = "PJ2";

        UserSettings settings = new UserSettings();
        settings.setUser(loginUser);
        List<ProjectUserSetting> pSettings = createProjectUserSettingList(true);
        settings.setProjectUserSettingList(pSettings);
//        settings.setRssFeedKey("abceefg12345");
        MockUserService.RET_FIND_USER_SETTINGS = settings;
        MockAbstractPage.RET_CURRENT_PROJECT_ID = pSettings.get(0).getProject().getProjectId();

        page.setId(userId);
        page.setBackPage("UserIndex");
        page.initialize();

//        assertTrue(page.isEnableRSSView());
//        assertEquals(settings.getRssFeedKey(), page.getUserSettings().getRssFeedKey());
        assertEquals(pSettings.size(),
                     page.getUserSettings().getProjectUserSettingList().size());
        for (int i = 0; i < pSettings.size(); i++) {
           ProjectUserSetting expSetting = pSettings.get(i);
           ProjectUserSetting actSetting = page.getUserSettings().getProjectUserSettingList().get(i);
           assertEquals(expSetting.getProject().getProjectId(), actSetting.getProject().getProjectId());
        }
        assertEquals(defaultProject, page.getDefaultProjectId());
        assertEquals(pSettings.size(),
                     page.getUserSettings().getProjectUserSettingList().size());
        for (int i = 0; i < pSettings.size(); i++) {
           ProjectUserSetting expSetting = pSettings.get(i);
           ProjectUserSetting actSetting = page.getUserSettings().getProjectUserSettingList().get(i);
           // デフォルト設定についてはcreateProjectUserSettingList()参照
           assertEquals(expSetting.getCorresponGroupUserList().get(0).getCorresponGroup().getId(),
                        actSetting.getDefaultCorresponGroupId());
           assertEquals(expSetting.getProjectUser().getUser().getRole(),
                        actSetting.getRole());
        }

        assertEquals(GROUP_ADMIN, page.getGroupAdmin());
        assertEquals(NORMAL_USER, page.getNormalUser());

        assertEquals(TITLE_MINE, page.getTitle());

        assertTrue(page.isEditingProject());
        assertTrue(page.isViewAllProject());
        assertTrue(page.isEditingProjectUserProfile());

        assertEquals(pSettings.size(),
                     page.getProjectSelectItems().size());
        for (int i = 0; i < pSettings.size(); i++) {
            SelectItem item = page.getProjectSelectItems().get(i);
            Project project = pSettings.get(i).getProject();
            assertEquals(project.getProjectId(), item.getValue());
            assertEquals(project.getProjectId() + " : " + project.getNameE(), item.getLabel());
        }
    }


    public static class MockUserService extends MockUp<UserServiceImpl> {
        static UserSettings RET_FIND_USER_SETTINGS;
        static UserSettings SET_SAVE;

        @Mock
        public UserSettings findUserSettings(String userId) throws ServiceAbortException {
            return RET_FIND_USER_SETTINGS;
        }

        @Mock
        public void save(UserSettings settings) throws ServiceAbortException {
            SET_SAVE = settings;
        }
    }

    public static class MockAbstractPage extends MockUp<AbstractPage> {
        static User RET_CURRENT_USER;
        static boolean RET_SYSTEM_ADMIN;
        static boolean RET_PROJECT_ADMIN;
        static boolean RET_GROUP_ADMIN;
        static String RET_CURRENT_PROJECT_ID;

        @Mock
        public User getCurrentUser() {
            return RET_CURRENT_USER;
        }

        @Mock
        public boolean isSystemAdmin() {
            return RET_SYSTEM_ADMIN;
        }

        @Mock
        public boolean isProjectAdmin(String checkProjectId) {
            return RET_PROJECT_ADMIN;
        }

        @Mock
        public boolean isGroupAdmin(Long groupId) {
            return RET_GROUP_ADMIN;
        }

        @Mock
        public String getCurrentProjectId() {
            return RET_CURRENT_PROJECT_ID;
        }
    }
}
