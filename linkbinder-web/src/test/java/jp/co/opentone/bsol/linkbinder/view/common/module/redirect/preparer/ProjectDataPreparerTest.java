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
import java.util.HashMap;
import java.util.LinkedHashMap;
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
import jp.co.opentone.bsol.framework.web.view.action.ServiceActionHandler;
import jp.co.opentone.bsol.framework.web.view.util.ViewHelper;
import jp.co.opentone.bsol.linkbinder.Constants;
import jp.co.opentone.bsol.linkbinder.dto.Project;
import jp.co.opentone.bsol.linkbinder.service.admin.ProjectService;
import jp.co.opentone.bsol.linkbinder.service.admin.impl.ProjectServiceImpl;
import jp.co.opentone.bsol.linkbinder.view.AbstractPage;
import jp.co.opentone.bsol.linkbinder.view.common.module.redirect.RedirectModule;
import jp.co.opentone.bsol.linkbinder.view.common.module.redirect.RedirectProcessException;
import mockit.Mock;
import mockit.MockUp;

/**
 * {@link ProjectDataPreparer}のテストケース.}
 * @author opentone
 */
public class ProjectDataPreparerTest extends AbstractTestCase{
    /**
     * テスト対象
     */
    private ProjectDataPreparer preparer;

    @Resource
    private ViewHelper viewHelper;

    @Resource
    private ServiceActionHandler handler;

    @Resource
    private ProjectService projectService;

    @BeforeClass
    public static void testSetUp() {
        new MockProjectService();
        new MockViewHelper();
        new MockExternalContextMock();
    }

    @AfterClass
    public static void testTeardown() {
        new MockProjectService().tearDown();
        new MockViewHelper().tearDown();
        new MockExternalContextMock().tearDown();
    }

    /**
     * テスト前準備.
     */
    @Before
    public void setUp() {
        preparer = new ProjectDataPreparer();
        FacesContextMock.initialize();
    }

    /**
     * 後始末.
     */
    @After
    public void tearDown() {
        MockProjectService.RET_PROJECT = null;
        MockProjectService.SERVICE_ABORT_EXCEPTION = null;

        MockViewHelper.SESSION_MAP = null;

        MockExternalContextMock.REDIRECT_PATH = null;
        MockExternalContextMock.IO_EXCEPTION = null;
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.view.common.module.redirect.preparer.RedirectDataPreparerBase#process(jp.co.opentone.bsol.linkbinder.view.common.module.redirect.RedirectModule, java.util.Map)} のためのテスト・メソッド。
     * 正常終了
     */
    @Test
    public void testProcessSuccess() throws Exception{
        RedirectModule module = createRedirectModuleMock();
        Map<String,String> param = createRedirectProcessParameter();
        MockProjectService.RET_PROJECT = createRetProject();

        preparer.process(module, param);

        // 遷移準備データ
        Project stockedProject = (Project)MockViewHelper.SESSION_MAP.get(Constants.KEY_PROJECT);
        assertEquals("PJ1", stockedProject.getProjectId());
        assertEquals("Test Project 1",stockedProject.getNameE());
    }

    /**
      * {@link jp.co.opentone.bsol.linkbinder.view.common.module.redirect.preparer.RedirectDataPreparerBase#process(jp.co.opentone.bsol.linkbinder.view.common.module.redirect.RedirectModule, java.util.Map)} のためのテスト・メソッド。
     * 異常終了(プロジェクトID未設定)
     */
    @Test
    public void testProcessFailedWithNullProjectId() throws Exception {
        RedirectModule module = createRedirectModuleMock();
        Map<String,String> param = createRedirectProcessParameter();
        param.put("projectId", null);
        try{
            preparer.process(module, param);
            fail();
        }catch(RedirectProcessException rpe){
            assertEquals(RedirectProcessException.ErrorCode.SPECIFIED_PROJECT_NOT_FOUND, rpe.getErrorCode());
        }catch(Throwable t){
            fail();
        }
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.view.common.module.redirect.preparer.RedirectDataPreparerBase#process(jp.co.opentone.bsol.linkbinder.view.common.module.redirect.RedirectModule, java.util.Map)} のためのテスト・メソッド。
    * 異常終了(プロジェクトID検索該当なし)
    */
   @Test
   public void testProcessFailedWithInvalidProjectId() throws Exception {
       RedirectModule module = createRedirectModuleMock();
       Map<String,String> param = createRedirectProcessParameter();
       MockProjectService.RET_PROJECT =null;
       try{
           preparer.process(module, param);
           fail();
       }catch(RedirectProcessException rpe){
           assertEquals(RedirectProcessException.ErrorCode.SPECIFIED_PROJECT_NOT_FOUND, rpe.getErrorCode());
       }catch(Throwable t){
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
      Map<String,String> param = createRedirectProcessParameter();
      MockProjectService.SERVICE_ABORT_EXCEPTION =new ServiceAbortException("Test");
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

    private Project createRetProject() {
        Project project = new Project();
        project.setProjectId("PJ1");
        project.setNameE("Test Project 1");
        return project;
    }

    class RedirectModuleMock extends RedirectModule{
        private static final long serialVersionUID = -2870998494178113217L;
        ViewHelper viewHelper;
        RedirectModuleMock(ViewHelper vh ,ProjectService ps){
            this.viewHelper = vh;
            this.projectService = ps;
            this.basePage = new MockBasePage();
        }

        @Override
        public void setCurrentProjectInfo(Project project) {
            viewHelper.setSessionValue(Constants.KEY_PROJECT, project);
        }
    };

    private RedirectModule createRedirectModuleMock(){
        return new RedirectModuleMock(this.viewHelper, this.projectService);
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

    public static class MockBasePage extends AbstractPage{
        @Override
        public ViewHelper getViewHelper() {
            return new ViewHelper();
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
}
