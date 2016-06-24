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
package jp.co.opentone.bsol.linkbinder.view.common.module.redirect;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.faces.context.FacesContext;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import jp.co.opentone.bsol.framework.core.exception.ApplicationFatalRuntimeException;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.extension.jsf.FacesContextMock;
import jp.co.opentone.bsol.framework.extension.jsf.FacesContextMock.ExternalContextMock;
import jp.co.opentone.bsol.framework.test.AbstractTestCase;
import jp.co.opentone.bsol.framework.web.view.util.ViewHelper;
import jp.co.opentone.bsol.linkbinder.Constants;
import jp.co.opentone.bsol.linkbinder.dto.Project;
import jp.co.opentone.bsol.linkbinder.dto.ProjectUser;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchUserCondition;
import jp.co.opentone.bsol.linkbinder.service.admin.impl.ProjectServiceImpl;
import jp.co.opentone.bsol.linkbinder.service.admin.impl.UserServiceImpl;
import jp.co.opentone.bsol.linkbinder.view.common.LoginPage;
import mockit.Mock;
import mockit.MockUp;

/**
 * {@link RedirectModule}のテストケース.}
 * @author opentone
 */
public class RedirectModuleTest extends AbstractTestCase {
    /**
     * テスト対象.
     */
    @Resource
    private RedirectModule module;

    @Resource
    private LoginPage loginPange;

    @BeforeClass
    public static void testSetUp() {
        FacesContextMock.initialize();
        new MockProjectService();
        new MockUserService();
        new MockViewHelper();
        new MockExternalContextMock();
    }

    @AfterClass
    public static void testTeardown() {
        new MockProjectService().tearDown();
        new MockUserService().tearDown();
        new MockViewHelper().tearDown();
        new MockExternalContextMock().tearDown();
        FacesContextMock.tearDown();
    }

    /**
     * テスト前準備.
     */
    @Before
    public void setUp() {
        getSessionMap().put(Constants.KEY_PROJECT, null);
        getSessionMap().put(Constants.KEY_PROJECT_USER, null);
        this.loginPange.setCurrentUser(createLoginUser());
        this.module.setBasePage(loginPange);
    }

    /**
     * 後始末.
     */
    @After
    public void tearDown() {
        MockProjectService.RET_PROJECT = null;
        MockUserService.RET_PROJECT_USER_LIST = null;
        MockProjectService.REDIRECT_PROCESS_EXCEPTION = null;

        MockExternalContextMock.REDIRECT_PATH = null;
        MockExternalContextMock.IO_EXCEPTION = null;
    }

    /**
     * {@link RedirectModule#setupRedirect(jp.co.opentone.bsol.linkbinder.view.common.module.redirect.RedirectScreenId, java.util.Map)}
     * のためのテスト・メソッド。
     * 正常終了(リダイレクト先：Project Home)
     */
    @Test
    public void testSetupRedirectSuccessToProjectHome() throws Exception {

        RedirectScreenId redirectTo = RedirectScreenId.PROJECT_HOME_LINKBINDER;
        Map<String, String> param = createRedirectProcessParameterToProjectHome();

        MockProjectService.RET_PROJECT = createRetProject();
        MockUserService.RET_PROJECT_USER_LIST = createRetProjectUserList();
        getSessionMap().put(Constants.KEY_PROJECT, MockProjectService.RET_PROJECT);
        getSessionMap().put(Constants.KEY_PROJECT_USER, MockUserService.RET_PROJECT_USER_LIST.get(0));

        module.setupRedirect(redirectTo, param);

        // 遷移準備データ
        Project stockedProject = (Project)getSessionMap().get(Constants.KEY_PROJECT);
        assertEquals("PJ1", stockedProject.getProjectId());
        assertEquals("Test Project 1",stockedProject.getNameE());
        ProjectUser stockedProjectUser = (ProjectUser)getSessionMap().get(Constants.KEY_PROJECT_USER);
        assertEquals("PJ1", stockedProjectUser.getProjectId());

    }

    /**
     * {@link RedirectModule#setupRedirect(jp.co.opentone.bsol.linkbinder.view.common.module.redirect.RedirectScreenId, java.util.Map)}
     * のためのテスト・メソッド。
     * 異常終了(リダイレクト先：Project Home、プロジェクトID未設定)
     */
    @Test
    public void testSetupRedirectFailedToProjectHomeWithNullProjectId() throws Exception {
        RedirectScreenId redirectTo = RedirectScreenId.PROJECT_HOME_LINKBINDER;
        Map<String, String> param = createRedirectProcessParameterToProjectHome();
        param.put("projectId", null);

        try{
            module.setupRedirect(redirectTo, param);
            fail();
        }catch(RedirectProcessException rpe){
            assertEquals(RedirectProcessException.ErrorCode.SPECIFIED_PROJECT_NOT_FOUND, rpe.getErrorCode());
        }catch(Throwable t){
            fail();
        }
    }

    /**
     * {@link RedirectModule#setupRedirect(jp.co.opentone.bsol.linkbinder.view.common.module.redirect.RedirectScreenId, java.util.Map)}
     * のためのテスト・メソッド。
     * 異常終了(リダイレクト先：Project Home、プロジェクト検索該当なし)
     */
    @Test
    public void testSetupRedirectFailedToProjectHomeWithInvalidProjectId() throws Exception {
        RedirectScreenId redirectTo = RedirectScreenId.PROJECT_HOME_LINKBINDER;
        Map<String, String> param = createRedirectProcessParameterToProjectHome();
        MockProjectService.RET_PROJECT = null;
        try{
            module.setupRedirect(redirectTo, param);
            fail();
        }catch(RedirectProcessException rpe){
            assertEquals(RedirectProcessException.ErrorCode.SPECIFIED_PROJECT_NOT_FOUND, rpe.getErrorCode());
        }catch(Throwable t){
            fail();
        }
    }


    /**
     * {@link RedirectModule#setupRedirect(jp.co.opentone.bsol.linkbinder.view.common.module.redirect.RedirectScreenId, java.util.Map)}
     * のためのテスト・メソッド。
     * 正常終了(リダイレクト先：Correspon表示画面)
     */
    @Test
    public void testSetupRedirectSuccessToCorresopn() throws Exception {
        RedirectScreenId redirectTo = RedirectScreenId.CORRESPON_LINKBINDER;
        Map<String, String> param = createRedirectProcessParameterToCorrespon();

        MockProjectService.RET_PROJECT = createRetProject();
        MockUserService.RET_PROJECT_USER_LIST = createRetProjectUserList();
        getSessionMap().put(Constants.KEY_PROJECT, MockProjectService.RET_PROJECT);
        getSessionMap().put(Constants.KEY_PROJECT_USER, MockUserService.RET_PROJECT_USER_LIST.get(0));

        module.setupRedirect(redirectTo, param);

        // 遷移準備データ
        Project stockedProject = (Project)getSessionMap().get(Constants.KEY_PROJECT);
        assertEquals("PJ1", stockedProject.getProjectId());
        assertEquals("Test Project 1",stockedProject.getNameE());
        ProjectUser stockedProjectUser = (ProjectUser)getSessionMap().get(Constants.KEY_PROJECT_USER);
        assertEquals("PJ1", stockedProjectUser.getProjectId());
    }

    /**
     * {@link RedirectModule#setupRedirect(jp.co.opentone.bsol.linkbinder.view.common.module.redirect.RedirectScreenId, java.util.Map)}
     * のためのテスト・メソッド。
     * 異常終了(リダイレクト先：Correspon表示画面、プロジェクトID未設定)
     */
    @Test
    public void testSetupRedirectFailedToCorresponWithNullProjectId() throws Exception {
        RedirectScreenId redirectTo = RedirectScreenId.CORRESPON_LINKBINDER;
        Map<String, String> param = createRedirectProcessParameterToCorrespon();
        param.put("projectId", null);

        try{
            module.setupRedirect(redirectTo, param);
            fail();
        }catch(RedirectProcessException rpe){
            assertEquals(RedirectProcessException.ErrorCode.SPECIFIED_PROJECT_NOT_FOUND, rpe.getErrorCode());
        }catch(Throwable t){
            fail();
        }
    }

    /**
     * {@link RedirectModule#setupRedirect(jp.co.opentone.bsol.linkbinder.view.common.module.redirect.RedirectScreenId, java.util.Map)}
     * のためのテスト・メソッド。
     * 異常終了(リダイレクト先：Correspon表示画面、プロジェクト該当なし)
     */
    @Test
    public void testSetupRedirectFailedToCorresponWithInvalidProjectId() throws Exception {
        RedirectScreenId redirectTo = RedirectScreenId.CORRESPON_LINKBINDER;
        Map<String, String> param = createRedirectProcessParameterToCorrespon();
        MockProjectService.RET_PROJECT = null;
        try{
            module.setupRedirect(redirectTo, param);
            fail();
        }catch(RedirectProcessException rpe){
            assertEquals(RedirectProcessException.ErrorCode.SPECIFIED_PROJECT_NOT_FOUND, rpe.getErrorCode());
        }catch(Throwable t){
            fail();
        }
    }

    /**
     * {@link RedirectModule#setupRedirect(jp.co.opentone.bsol.linkbinder.view.common.module.redirect.RedirectScreenId, java.util.Map)}
     * のためのテスト・メソッド。
     * 異常終了(FacesContextへリダイレクト先設定時にIOException発生)
     */
    @Test
    public void testSetupRedirectFailedByFacesContextRedirectSettingError() throws Exception {
        RedirectScreenId redirectTo = RedirectScreenId.CORRESPON_LINKBINDER;
        Map<String, String> param = createRedirectProcessParameterToCorrespon();
        MockProjectService.RET_PROJECT = createRetProject();
        MockUserService.RET_PROJECT_USER_LIST = createRetProjectUserList();
        MockExternalContextMock.IO_EXCEPTION = new IOException("TEST");
        try{
          //  module.setBasePage(basePage);
            module.setupRedirect(redirectTo, param);
            fail();
        }catch(ApplicationFatalRuntimeException afre){
            assertEquals("TEST", afre.getCause().getMessage());
        }catch(Throwable t){
            t.printStackTrace();
            fail();
        }
    }

    private Map<String, String> createRedirectProcessParameterToProjectHome() {
        Map<String, String> ret = new LinkedHashMap<String, String>();
        ret.put("projectId", "PJ1");
        return ret;
    }

    private Map<String, String> createRedirectProcessParameterToCorrespon() {
        Map<String, String> ret = new LinkedHashMap<String, String>();
        ret.put("projectId", "PJ1");
        ret.put("id", "123");
        ret.put("Startup", "1");
        return ret;
    }

    private Project createRetProject() {
        Project project = new Project();
        project.setProjectId("PJ1");
        project.setNameE("Test Project 1");
        return project;
    }

    private User createLoginUser() {
        User lastLogin = new User();
        lastLogin.setEmpNo("ZZA01");
        lastLogin.setNameE("Test User");
        lastLogin.setDefaultProjectId("PJ1");
        lastLogin.setSecurityLevel("10");
        lastLogin.setSysAdminFlg("X");
        return lastLogin;
    }

    private List<ProjectUser>  createRetProjectUserList() {
        List<ProjectUser> ret = new ArrayList<ProjectUser>();
        for(int i=0;i<3;i++){
            ProjectUser pu = new ProjectUser();
            pu.setProjectId("PJ"+(i+1));
            ret.add(pu);
        }
        return ret;
    }

    public static class MockProjectService extends MockUp<ProjectServiceImpl> {
        public static Project RET_PROJECT;
        public static RedirectProcessException REDIRECT_PROCESS_EXCEPTION;

        @Mock
        public Project find(String id) throws RedirectProcessException {
            if (REDIRECT_PROCESS_EXCEPTION != null) {
                throw REDIRECT_PROCESS_EXCEPTION;
            }
            return RET_PROJECT;
        }
    }

    public static class MockViewHelper extends MockUp<ViewHelper> {
        public static String BASE_PATH = "http://test.com/";

        @Mock
        public String getBasePath() {
            return BASE_PATH;
        }

        @Mock
        public void setSessionValue(String key, Object value) {
            Map<String, Object> sessionMap = getSessionMap();
            sessionMap.put(key, value);
        }
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> getSessionMap() {
        FacesContext context = FacesContextMock.getCurrentInstance();
        Map<String, Object> sessionMap = context.getExternalContext().getSessionMap();
        return sessionMap;
    }

    public static class MockExternalContextMock extends MockUp<ExternalContextMock> {
        public static String REDIRECT_PATH;
        public static IOException IO_EXCEPTION;
        @Mock
        public void redirect(String requestURI) throws IOException {
            if (IO_EXCEPTION != null) {
                throw IO_EXCEPTION;
            }
            REDIRECT_PATH = requestURI;
        }
    }

    public static class MockUserService extends MockUp<UserServiceImpl> {
        public static List<ProjectUser> RET_PROJECT_USER_LIST;
        public static ServiceAbortException SERVICE_ABORT_EXCEPTION;
        public static SearchUserCondition SET_SEARCH_USER_CONDITION;
        @Mock
        public List<ProjectUser> search(SearchUserCondition condition) throws ServiceAbortException {
            SET_SEARCH_USER_CONDITION = condition;
            if (SERVICE_ABORT_EXCEPTION != null) {
                throw SERVICE_ABORT_EXCEPTION;
            }
            return RET_PROJECT_USER_LIST;
        }
    }
}
