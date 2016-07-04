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

import static jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode.*;
import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import jp.co.opentone.bsol.framework.core.config.SystemConfig;
import jp.co.opentone.bsol.framework.core.exception.ApplicationFatalRuntimeException;
import jp.co.opentone.bsol.framework.core.message.Messages;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.extension.jsf.FacesContextMock;
import jp.co.opentone.bsol.framework.test.AbstractTestCase;
import jp.co.opentone.bsol.framework.web.view.util.ViewHelper;
import jp.co.opentone.bsol.linkbinder.Constants;
import jp.co.opentone.bsol.linkbinder.dto.Project;
import jp.co.opentone.bsol.linkbinder.dto.ProjectUser;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchUserCondition;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.service.admin.impl.ProjectServiceImpl;
import jp.co.opentone.bsol.linkbinder.service.admin.impl.UserServiceImpl;
import jp.co.opentone.bsol.linkbinder.service.common.impl.LoginServiceImpl;
import jp.co.opentone.bsol.linkbinder.view.AbstractPage;
import jp.co.opentone.bsol.linkbinder.view.common.module.redirect.RedirectProcessParameterKey;
import jp.co.opentone.bsol.linkbinder.view.common.module.redirect.RedirectScreenId;
import mockit.Mock;
import mockit.MockUp;


/**
 * {@link LoginPage}のテストケース.
 * @author opentone
 */
public class LoginPageTest extends AbstractTestCase {

    /**
     * テスト対象.
     */
    @Resource
    private LoginPage page;

    /**
     * テストの前準備.
     */
    @BeforeClass
    public static void testSetUp() {
        FacesContextMock.initialize();
        new MockAbstractPage();
        new MockLoginService();
        new MockProjectService();
        new MockUserService();
        new MockExternalContextMock();
        new MockViewHelper();
        crearSessionMap();
    }

    private static void crearSessionMap() {
        FacesContext context = FacesContextMock.getCurrentInstance();
        Map<String, Object> sessionMap = context.getExternalContext().getSessionMap();
        sessionMap.put(Constants.KEY_CURRENT_USER, null);
        sessionMap.put(Constants.KEY_PROJECT, null);
        sessionMap.put(Constants.KEY_PROJECT_USER, null);
        sessionMap.put(Constants.KEY_SEARCH_CORRESPON_CONDITION, null);
    }

    /**
     * 後始末.
     */
    @AfterClass
    public static void testTeardown() {
        new MockAbstractPage().tearDown();
        new MockLoginService().tearDown();
        new MockProjectService().tearDown();
        new MockUserService().tearDown();
        new MockExternalContextMock().tearDown();
        new MockViewHelper().tearDown();
        FacesContextMock.tearDown();
    }

    @Before
    public void setUp() {
        FacesContextMock.EXPECTED_MESSAGE = null;
        MockLoginService.RET_LOGIN = null;
        MockLoginService.LOGIN_EXCEPTION = null;
        MockLoginService.RET_AUTHORIZE= null;
        MockLoginService.AUTHORIZE_EXCEPTION = null;

        MockProjectService.RET_PROJECT = null;
        MockProjectService.SERVICE_ABORT_EXCEPTION = null;

        MockExternalContextMock.REDIRECT_PATH = null;
        MockExternalContextMock.IO_EXCEPTION = null;

        MockAbstractPage.RET_PROJECT = null;
        MockAbstractPage.RET_PROJID = null;
        MockAbstractPage.IS_PROJECT_ADMIN = false;
        MockAbstractPage.IS_SYSTEM_ADMIN = false;
    }

    @After
    public void tearDown() {
        MockLoginService.LOGIN_EXCEPTION = null;
    }

    /**
     * 画面初期化のテスト.(リダイレクトリクエスト)
     * @throws Exception
     */
    @Test
    public void testInitializeRedirect() throws Exception {
        //  ログイン後アプリケーションが生成するsession内の値を擬似的に設定
        FacesContext context = FacesContextMock.getCurrentInstance();
        Map<String, Object> sessionMap = context.getExternalContext().getSessionMap();
        sessionMap.put(Constants.KEY_CURRENT_USER, null);
        sessionMap.put(Constants.KEY_PROJECT, null);
        sessionMap.put(Constants.KEY_PROJECT_USER, null);
        sessionMap.put(Constants.KEY_SEARCH_CORRESPON_CONDITION, null);
        sessionMap.put(Constants.KEY_REDIRECT_SCREEN_ID, RedirectScreenId.CORRESPON);
        sessionMap.put(RedirectProcessParameterKey.ID, "1000");
        sessionMap.put(RedirectProcessParameterKey.PROJECT_ID, "0-1111-2");
        page.initialize();
    }

    /**
     * 画面初期化のテスト.(リダイレクトリクエスト：要求画面不正)
     * @throws Exception
     */
    @Test
    public void testInitializeRedirectInvalidScreenId() throws Exception {
        //  ログイン後アプリケーションが生成するsession内の値を擬似的に設定
        FacesContext context = FacesContextMock.getCurrentInstance();
        Map<String, Object> sessionMap = context.getExternalContext().getSessionMap();

        // リダイレクトに対応していないページを指定
        sessionMap.put(Constants.KEY_REDIRECT_SCREEN_ID, RedirectScreenId.PROJECT_HOME);
        sessionMap.put(RedirectProcessParameterKey.ID, "1000");
        sessionMap.put(RedirectProcessParameterKey.PROJECT_ID, "0-1111-2");

        try {
            page.initialize();
            fail();
        }
        catch (ApplicationFatalRuntimeException e) {
            assertEquals("invalid screen id", e.getMessage());
        }
    }

    /**
     * 画面初期化のテスト.(リダイレクトリクエスト：パラメータ不正)
     * @throws Exception
     */
    @Test
    public void testInitializeRedirectInvalidParameter() throws Exception {
        //  ログイン後アプリケーションが生成するsession内の値を擬似的に設定
        FacesContext context = FacesContextMock.getCurrentInstance();
        Map<String, Object> sessionMap = context.getExternalContext().getSessionMap();

        sessionMap.put(Constants.KEY_REDIRECT_SCREEN_ID, RedirectScreenId.CORRESPON);
        sessionMap.put(RedirectProcessParameterKey.PROJECT_ID, "0-1111-2");

        try {
            page.initialize();
        }
        catch (IllegalArgumentException e) {
            assertEquals(RedirectProcessParameterKey.ID + " must not be empty", e.getMessage());
        }

        page.getViewHelper().setSessionValue(
            Constants.KEY_REDIRECT_SCREEN_ID, RedirectScreenId.CORRESPON);
        page.getViewHelper().setSessionValue(RedirectProcessParameterKey.ID, "1000");

        try {
            page.initialize();
        }
        catch (IllegalArgumentException e) {
            assertEquals(RedirectProcessParameterKey.PROJECT_ID + " must not be empty", e.getMessage());
        }

        sessionMap.put(Constants.KEY_REDIRECT_SCREEN_ID, null);
        sessionMap.put(RedirectProcessParameterKey.ID, null);
        sessionMap.put(RedirectProcessParameterKey.PROJECT_ID, null);
    }

    /**
     * ログインのテスト.
     * @throws Exception
     */
    @Test
    public void testLogin() throws Exception {
        String userId = "ZZA001";
        String password = "linkbinder";
        page.setUserId(userId);
        page.setPassword(password);

        //  ログイン情報をクリア
        User currentUser = page.getCurrentUser();
        currentUser.clearProperties();

        //  ログイン処理で返されるダミーの戻り値を準備
        User u = new User();
        u.setEmpNo(userId);
        u.setNameE("Test User");
        u.setSysAdminFlg("X");
        u.setSecurityLevel("10");

        MockLoginService.RET_LOGIN = u;

        //  ログインに成功した場合は
        //  ログインユーザー情報がセットされた状態で
        //  ホーム画面へ遷移する
        assertEquals("home", page.login());
        assertEquals(u.getEmpNo(), page.getCurrentUser().getEmpNo());
        assertEquals(u.getNameE(), page.getCurrentUser().getNameE());
        assertEquals(u.getSysAdminFlg(), page.getCurrentUser().getSysAdminFlg());
        assertEquals(u.getSecurityLevel(), page.getCurrentUser().getSecurityLevel());

    }

    /**
     * ログインのテスト.
     * ユーザーIDまたはパスワードが不正でログインに失敗する場合の検証
     * @throws Exception
     */
    @Test
    public void testLoginFailure() throws Exception {
        String userId = "ZZA001";
        String password = "invalidpassword";
        page.setUserId(userId);
        page.setPassword(password);

        MockLoginService.LOGIN_EXCEPTION =
            new ServiceAbortException(ApplicationMessageCode.LOGIN_FAILED);

        // 期待されるメッセージをセット
        FacesContextMock.EXPECTED_MESSAGE =
            new FacesMessage(FacesMessage.SEVERITY_ERROR,
                             FacesMessage.SEVERITY_ERROR.toString(),
                             createExpectedMessageString(
                                 Messages.getMessageAsString(LOGIN_FAILED),
                                 null));

        //  次画面への遷移は行わない
        assertNull(page.login());
        //  パスワード有効期限切れフラグはOFF
        assertFalse(page.isPasswordExpired());
    }

    /**
     * ログインのテスト.
     * パスワードの有効期限切れでログインに失敗する場合の検証
     * @throws Exception
     */
    @Test
    public void testLoginPasswordExpired() throws Exception {
        String userId = "ZZA001";
        String password = "linkbinder";
        page.setUserId(userId);
        page.setPassword(password);

        MockLoginService.LOGIN_EXCEPTION =
            new ServiceAbortException(ApplicationMessageCode.PASSWORD_EXPIRED);

        // 期待されるメッセージをセット
        FacesContextMock.EXPECTED_MESSAGE =
            new FacesMessage(FacesMessage.SEVERITY_ERROR,
                             FacesMessage.SEVERITY_ERROR.toString(),
                             createExpectedMessageString(
                                 Messages.getMessageAsString(PASSWORD_EXPIRED),
                                 null));

        //  次画面への遷移は行わない
        //  有効期限切れを表すフラグがON
        assertNull(page.login());
        assertTrue(page.isPasswordExpired());
        assertEquals(SystemConfig.getValue(Constants.KEY_PASSWORD_MANAGEMENT_URL),
                     page.getExternalLink());
    }

    public static class MockLoginService extends MockUp<LoginServiceImpl> {
        public static User RET_LOGIN;
        public static ServiceAbortException LOGIN_EXCEPTION;
        @Mock
        public static User login(String userId, String password) throws ServiceAbortException {
            if (LOGIN_EXCEPTION != null) {
                throw LOGIN_EXCEPTION;
            }
            return RET_LOGIN;
        }

        public static User RET_AUTHORIZE;
        public static ServiceAbortException AUTHORIZE_EXCEPTION;
    }

    public static class MockViewHelper extends MockUp<ViewHelper> {
        public static String BASE_PATH = "http://test.com/linkbinder";

        @Mock
        public String getBasePath() {
            return BASE_PATH;
        }

        @Mock
        public void setSessionValue(String key, Object value) {
            FacesContext context = FacesContextMock.getCurrentInstance();
            Map<String, Object> sessionMap = context.getExternalContext().getSessionMap();
            sessionMap.put(key, value);
        }
    }

    public static class MockExternalContextMock extends MockUp<ExternalContext> {
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

    public static class MockProjectService extends MockUp<ProjectServiceImpl> {
        public static Project RET_PROJECT;
        public static ServiceAbortException SERVICE_ABORT_EXCEPTION;
        @Mock
        public Project find(String id) throws ServiceAbortException {
            if (SERVICE_ABORT_EXCEPTION != null) {
                throw SERVICE_ABORT_EXCEPTION;
            }
            return RET_PROJECT;
        }
    }

    public static class MockUserService extends MockUp<UserServiceImpl> {
        public static List<ProjectUser> RET_PROJECT_USER_LIST;
        public static ServiceAbortException SERVICE_ABORT_EXCEPTION;
        @Mock
        public List<ProjectUser> search(SearchUserCondition condition) throws ServiceAbortException {
            if (SERVICE_ABORT_EXCEPTION != null) {
                throw SERVICE_ABORT_EXCEPTION;
            }
            return RET_PROJECT_USER_LIST;
        }
    }

    public static class MockAbstractPage extends MockUp<AbstractPage> {
        static String RET_PROJID;
        static Project RET_PROJECT;
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

        @Mock
        public Project getCurrentProject() {
            return RET_PROJECT;
        }
    }
}
