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
package jp.co.opentone.bsol.linkbinder.view.common.module.redirect.preparer;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.extension.jsf.FacesContextMock;
import jp.co.opentone.bsol.framework.extension.jsf.FacesContextMock.ExternalContextMock;
import jp.co.opentone.bsol.framework.test.AbstractTestCase;
import jp.co.opentone.bsol.framework.web.view.util.ViewHelper;
import jp.co.opentone.bsol.linkbinder.dto.Project;
import jp.co.opentone.bsol.linkbinder.dto.ProjectUser;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchUserCondition;
import jp.co.opentone.bsol.linkbinder.service.admin.UserService;
import jp.co.opentone.bsol.linkbinder.service.admin.impl.UserServiceImpl;
import jp.co.opentone.bsol.linkbinder.view.AbstractPage;
import jp.co.opentone.bsol.linkbinder.view.LoginUserInfoHolder;
import jp.co.opentone.bsol.linkbinder.view.common.module.redirect.RedirectModule;
import jp.co.opentone.bsol.linkbinder.view.common.module.redirect.RedirectProcessException;
import mockit.Mock;
import mockit.MockUp;

/**
 * {@link ProjectDataPreparer}のテストケース.}
 * @author opentone
 */
public class ProjectUserPreparerTest extends AbstractTestCase{
    /**
     * テスト対象
     */
    private RedirectDataPreparerBase preparer;

    @Resource
    private ViewHelper viewHelper;

    @Resource
    private UserService userService;

    private AbstractPage page = new MockAbstractPage();

    @BeforeClass
    public static void testSetUp() {
        new MockUserService();
        new MockViewHelper();
        new MockExternalContextMock();
    }

    @AfterClass
    public static void testTeardown() {
        new MockUserService().tearDown();
        new MockViewHelper().tearDown();
        new MockExternalContextMock().tearDown();
    }

    /**
     * テスト前準備.
     */
    @Before
    public void setUp() {
        preparer = new ProjectUserPreparer();
        FacesContextMock.initialize();
        //TODO JMockitバージョンアップ対応
//        Mockit.redefineMethods(ViewHelper.class, MockViewHelper.class);
//        Mockit.redefineMethods(UserServiceImpl.class, MockUserService.class);
//        Mockit.redefineMethods(ExternalContextMock.class, MockExternalContextMock.class);
//        Mockit.redefineMethods(AbstractPage.class, MockAbstractPage.class);
    }

    /**
     * 後始末.
     */
    @After
    public void tearDown() {
        RedirectModuleMock.CURRENT_PROJECT = null;
        RedirectModuleMock.CURRENT_USER= null;

        MockUserService.RET_PROJECT_USER_LIST = null;
        MockUserService.SERVICE_ABORT_EXCEPTION = null;
        MockUserService.SET_SEARCH_USER_CONDITION = null;

        MockViewHelper.SESSION_MAP = null;

        MockExternalContextMock.REDIRECT_PATH = null;
        MockExternalContextMock.IO_EXCEPTION = null;

        // 差し換えたMockをクリアする
        //TODO JMockitバージョンアップ対応
//        Mockit.tearDownMocks();
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.view.common.module.redirect.preparer.RedirectDataPreparerBase#process(jp.co.opentone.bsol.linkbinder.view.common.module.redirect.RedirectModule, java.util.Map)} のためのテスト・メソッド。
     * 正常終了
     */
    @Test
    public void testProcessSuccess() throws Exception{
        RedirectModule module = createRedirectModuleMock();
        RedirectModuleMock.CURRENT_PROJECT = createProject();
        RedirectModuleMock.CURRENT_USER = createUser();

        Map<String,String> param = createRedirectProcessParameter();
        MockUserService.RET_PROJECT_USER_LIST = createRetProjectUserList();
        page.setLoginProjectId(RedirectModuleMock.CURRENT_PROJECT.getProjectId());

        preparer.process(module, param);


        // 検索条件は正しくセットされたか
        assertEquals("PJ1",MockUserService.SET_SEARCH_USER_CONDITION.getProjectId());
        assertEquals("00999",MockUserService.SET_SEARCH_USER_CONDITION.getEmpNo());


        // 遷移準備データとして検索結果0番目が格納されているか
        ProjectUser stockedProjectUser = page.getCurrentProjectUser();
//        ProjectUser stockedProjectUser = (ProjectUser) MockViewHelper.SESSION_MAP.get(Constants.KEY_PROJECT_USER);
        assertEquals("PJ1", stockedProjectUser.getProjectId());
    }

    /**
      * {@link jp.co.opentone.bsol.linkbinder.view.common.module.redirect.preparer.RedirectDataPreparerBase#process(jp.co.opentone.bsol.linkbinder.view.common.module.redirect.RedirectModule, java.util.Map)} のためのテスト・メソッド。
     * 異常終了(カレントプロジェクト情報未設定)
     */
    @Test
    public void testProcessFailedWithNullCurrentProject() throws Exception {
        RedirectModule module = createRedirectModuleMock();
        RedirectModuleMock.CURRENT_PROJECT = null;
        RedirectModuleMock.CURRENT_USER = createUser();

        Map<String,String> param = createRedirectProcessParameter();
        try{
            preparer.process(module, param);
            fail();
        }catch(RedirectProcessException rpe){
            assertEquals(RedirectProcessException.ErrorCode.CURRENT_PROJECT_IS_NULL, rpe.getErrorCode());
        }catch(Throwable t){
            t.printStackTrace();
            fail();
        }
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.view.common.module.redirect.preparer.RedirectDataPreparerBase#process(jp.co.opentone.bsol.linkbinder.view.common.module.redirect.RedirectModule, java.util.Map)} のためのテスト・メソッド。
    * 異常終了(カレントユーザ情報未設定)
    */
   @Test
   public void testProcessFailedWithNullCurrentUser() throws Exception {
       RedirectModule module = createRedirectModuleMock();
       RedirectModuleMock.CURRENT_PROJECT = createProject();
       RedirectModuleMock.CURRENT_USER = null;

       Map<String,String> param = createRedirectProcessParameter();
       try{
           preparer.process(module, param);
           fail();
       }catch(RedirectProcessException rpe){
           assertEquals(RedirectProcessException.ErrorCode.CURRENT_USER_IS_NULL, rpe.getErrorCode());
       }catch(Throwable t){
           t.printStackTrace();
           fail();
       }
   }

   /**
    * {@link jp.co.opentone.bsol.linkbinder.view.common.module.redirect.preparer.RedirectDataPreparerBase#process(jp.co.opentone.bsol.linkbinder.view.common.module.redirect.RedirectModule, java.util.Map)} のためのテスト・メソッド。
   * 異常終了(カレントユーザ情報の従業員番号が空)
   */
  @Test
  public void testProcessFailedWithEmptyEmpNoOfCurrentUser() throws Exception {
      RedirectModule module = createRedirectModuleMock();
      RedirectModuleMock.CURRENT_PROJECT = createProject();
      User user = createUser();
      user.setEmpNo(null);
      RedirectModuleMock.CURRENT_USER = user;

      Map<String,String> param = createRedirectProcessParameter();
      try{
          preparer.process(module, param);
          fail();
      }catch(RedirectProcessException rpe){
          assertEquals(RedirectProcessException.ErrorCode.CURRENT_USER_IS_NULL, rpe.getErrorCode());
      }catch(Throwable t){
          t.printStackTrace();
          fail();
      }
  }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.view.common.module.redirect.preparer.RedirectDataPreparerBase#process(jp.co.opentone.bsol.linkbinder.view.common.module.redirect.RedirectModule, java.util.Map)} のためのテスト・メソッド。
    * 異常終了(プロジェクトユーザ検索該当なし)
    */
   @Test
   public void testProcessFailedWithInvalidProjectId() throws Exception {
       RedirectModule module = createRedirectModuleMock();
       RedirectModuleMock.CURRENT_PROJECT = createProject();
       RedirectModuleMock.CURRENT_USER = createUser();

       Map<String,String> param = createRedirectProcessParameter();
       MockUserService.RET_PROJECT_USER_LIST =new ArrayList<ProjectUser>();
       try{
           preparer.process(module, param);
           fail();
       }catch(RedirectProcessException rpe){
           assertEquals(RedirectProcessException.ErrorCode.SPECIFIED_PROJECT_USER_NOT_FOUND, rpe.getErrorCode());
       }catch(Throwable t){
           t.printStackTrace();
           fail();
       }
   }

   /**
    * {@link jp.co.opentone.bsol.linkbinder.view.common.module.redirect.preparer.RedirectDataPreparerBase#process(jp.co.opentone.bsol.linkbinder.view.common.module.redirect.RedirectModule, java.util.Map)} のためのテスト・メソッド。
   * 異常終了(プロジェクト取得サービス例外発生)
   */
  @Test
  public void testProcessFailedByServiceAbortException() throws Exception {
      RedirectModule module = createRedirectModuleMock();
      RedirectModuleMock.CURRENT_PROJECT = createProject();
      RedirectModuleMock.CURRENT_USER = createUser();

      Map<String,String> param = createRedirectProcessParameter();
      MockUserService.SERVICE_ABORT_EXCEPTION =new ServiceAbortException("Test");
      try{
          preparer.process(module, param);
          fail();
      }catch(RedirectProcessException rpe){
          assertEquals(RedirectProcessException.ErrorCode.OTHER_REASON, rpe.getErrorCode());
          assertNotNull(rpe.getCause());
      }catch(Throwable t){
          fail();
      }
  }

    private Map<String, String> createRedirectProcessParameter() {
        Map<String, String> ret = new LinkedHashMap<String, String>();
        ret.put("projectId", "PJ1");
        return ret;
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

    private Project createProject() {
        Project project = new Project();
        project.setProjectId("PJ1");
        project.setNameE("Test Project 1");
        return project;
    }

    private User createUser() {
        User user = new User();
        user.setEmpNo("00999");
        return user;
    }

    static class RedirectModuleMock extends RedirectModule{
        private static final long serialVersionUID = -2870998494178113217L;
        ViewHelper viewHelper;
        AbstractPage page;
        public static User CURRENT_USER;
        public static Project CURRENT_PROJECT;
        RedirectModuleMock(ViewHelper vh, UserService us, AbstractPage page){
            this.viewHelper = vh;
            this.userService = us;
            this.page = page;
        }

        @Override
        public User getCurrentUser() {
            return CURRENT_USER;
        }

       @Override
       public Project getCurrentProject() {
           return CURRENT_PROJECT;
       }

       @Override
       public ViewHelper getViewHelper() {
           return viewHelper;
       }

       @Override
       public AbstractPage getBasePage() {
           return page;
       }

    };


    private RedirectModule createRedirectModuleMock(){
        RedirectModuleMock mock =  new RedirectModuleMock(
            this.viewHelper,
            this.userService,
            this.page);
        return mock;
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

    public static class MockViewHelper extends MockUp<ViewHelper> {
        public static String BASE_PATH = "http://test.com/";

        @Mock
        public String getBasePath() {
            return BASE_PATH;
        }

        public static Map<String, Object> SESSION_MAP;

        @Mock
        public void setSessionValue(String key, Object value) {
            if (SESSION_MAP == null) {
                SESSION_MAP = new HashMap<String, Object>();
            }
            SESSION_MAP.put(key, value);
        }
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

    public static class MockAbstractPage extends AbstractPage {
        public LoginUserInfoHolder getLoginUserInfoHolder() {
            if (super.getLoginUserInfoHolder() == null) {
                LoginUserInfoHolder h = new LoginUserInfoHolder();
                super.setLoginUserInfoHolder(h);
            }
            return super.getLoginUserInfoHolder();
        }
    }
}
