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

import static jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode.*;
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
import jp.co.opentone.bsol.framework.core.message.Messages;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.extension.jsf.FacesContextMock;
import jp.co.opentone.bsol.framework.test.AbstractTestCase;
import jp.co.opentone.bsol.linkbinder.dto.CorresponType;
import jp.co.opentone.bsol.linkbinder.dto.ProjectCustomSetting;
import jp.co.opentone.bsol.linkbinder.dto.SearchCorresponTypeResult;
import jp.co.opentone.bsol.linkbinder.dto.WorkflowPattern;
import jp.co.opentone.bsol.linkbinder.dto.code.AllowApproverToBrowse;
import jp.co.opentone.bsol.linkbinder.dto.code.ForceToUseWorkflow;
import jp.co.opentone.bsol.linkbinder.dto.code.UseWhole;
import jp.co.opentone.bsol.linkbinder.dto.condition.AbstractCondition;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponTypeCondition;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.service.admin.impl.CorresponTypeServiceImpl;
import jp.co.opentone.bsol.linkbinder.service.admin.impl.ProjectCustomSettingServiceImpl;
import jp.co.opentone.bsol.linkbinder.view.AbstractPage;
import jp.co.opentone.bsol.linkbinder.view.admin.CorresponTypeIndexPageTest.MockDateUtil;
import jp.co.opentone.bsol.linkbinder.view.admin.CorresponTypeIndexPageTest.MockViewHelper;
import jp.co.opentone.bsol.linkbinder.view.admin.CustomFieldEditPageTest.MockCustomFieldService;
import mockit.Mock;
import mockit.MockUp;

/**
 * {@link CorresponTypeEditPage}のテストケース
 *
 * @author opentone
 */
public class CorresponTypeEditPageTest extends AbstractTestCase {

    /**
     * テスト対象.
     */
    @Resource
    private CorresponTypeEditPage page;

    /**
     * テストの前準備.
     */
    @BeforeClass
    public static void testSetUp() {
        FacesContextMock.initialize();
        new MockAbstractPage();
        new MockCorresponTypeService();
        new MockProjectCustomSettingService();
    }

    /**
     * 後始末.
     */
    @AfterClass
    public static void testTeardown() {
        new MockAbstractPage().tearDown();
        new MockCorresponTypeService().tearDown();
        new MockProjectCustomSettingService().tearDown();
        FacesContextMock.tearDown();
    }

    @Before
    public void setUp() {

    }

    @After
    public void tearDown() {
        MockAbstractPage.RET_PROJECT_ID = null;
        MockAbstractPage.IS_SYSTEM_ADMIN = false;
        MockAbstractPage.IS_PROJECT_ADMIN = false;
        MockCorresponTypeService.RET_SEARCH = null;
        MockCorresponTypeService.RET_EXCEL = null;
        MockCorresponTypeService.RET_ASSIGN_TO = null;
        MockCorresponTypeService.CRT_CORRESPONTYPE = null;
        MockCorresponTypeService.RET_FIND = null;
        MockCorresponTypeService.RET_SEARCH_WORKFLOWPATTERN = null;
        MockCorresponTypeService.RET_VALIDATE = false;
        MockProjectCustomSettingService.RET_SEARCH = null;
        MockViewHelper.RET_DATA = null;
        MockViewHelper.RET_FILENAME = null;
        MockDateUtil.RET_DATE = null;

    }

    /**
     * 画面初期化アクションを検証する. 新規登録
     *
     * @throws Exception
     */
    @Test
    public void testInitializeInsert() throws Exception {
        MockAbstractPage.IS_SYSTEM_ADMIN = true;

        List<WorkflowPattern> wpList = createWorkflowPatternList();
        MockCorresponTypeService.RET_SEARCH_WORKFLOWPATTERN = createWorkflowPatternList();

        page.initialize();

        assertTrue(page.isInitialDisplaySuccess());

        List<SelectItem> actual = page.getSelectWorkflowPatternList();

        assertTrue(3 == actual.size());

        assertEquals("文書種類新規作成", page.getTitle());

        assertEquals(wpList.get(0).getId().toString(), actual.get(0).getValue().toString());
        assertEquals(wpList.get(0).getName(), actual.get(0).getLabel());

        assertEquals(wpList.get(1).getId().toString(), actual.get(1).getValue().toString());
        assertEquals(wpList.get(1).getName(), actual.get(1).getLabel());

        assertEquals(wpList.get(2).getId().toString(), actual.get(2).getValue().toString());
        assertEquals(wpList.get(2).getName(), actual.get(2).getLabel());
    }

    /**
     * 画面初期化アクションを検証する. 更新
     *
     * @throws Exception
     */
    @Test
    public void testInitializeUpdate() throws Exception {
        MockAbstractPage.IS_SYSTEM_ADMIN = true;

        List<WorkflowPattern> wpList = createWorkflowPatternList();
        MockCorresponTypeService.RET_SEARCH_WORKFLOWPATTERN = createWorkflowPatternList();

        CorresponType corresponType = createCorresponType(true);
        MockCorresponTypeService.RET_FIND = createCorresponType(true);
        MockProjectCustomSettingService.RET_SEARCH = createProjectCustomSetting();

        page.setId(1L);

        page.initialize();

        assertTrue(page.isInitialDisplaySuccess());

        List<SelectItem> actual = page.getSelectWorkflowPatternList();

        assertTrue(3 == actual.size());

        assertEquals("文書種類更新", page.getTitle());

        assertEquals(corresponType.toString(), page.getCorresponType().toString());
        assertEquals(corresponType.getCorresponType(), page.getType());
        assertEquals(corresponType.getName(), page.getName());

        assertEquals(wpList.get(0).getId().toString(), actual.get(0).getValue().toString());
        assertEquals(wpList.get(0).getName(), actual.get(0).getLabel());

        assertEquals(wpList.get(1).getId().toString(), actual.get(1).getValue().toString());
        assertEquals(wpList.get(1).getName(), actual.get(1).getLabel());

        assertEquals(wpList.get(2).getId().toString(), actual.get(2).getValue().toString());
        assertEquals(wpList.get(2).getName(), actual.get(2).getLabel());
    }

    /**
     * 初期化アクションを検証する. 権限がない
     *
     * @throws Exception
     */
    @Test
    public void testInitializeInvalidPermission() throws Exception {
        MockAbstractPage.IS_SYSTEM_ADMIN = false;
        MockAbstractPage.IS_PROJECT_ADMIN = false;

        // 期待されるメッセージをセット
        FacesContextMock.EXPECTED_MESSAGE = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                FacesMessage.SEVERITY_ERROR.toString(),
                createExpectedMessageString(Messages
                        .getMessageAsString(CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT),
                        "Initialize"));

        try {
            page.initialize();
            fail("例外が発生していない");
        } catch (InvalidOperationRuntimeException e) {
            ServiceAbortException actual = (ServiceAbortException) e.getCause();
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT,
                    actual.getMessageCode());
        }

    }

    /**
     * 次画面遷移を検証する. 登録
     *
     * @throws Exception
     */
    @Test
    public void testNextInsert() throws Exception {
        MockCustomFieldService.RET_VALIDATE = true;

        // 期待されるメッセージをセット
        FacesContextMock.EXPECTED_MESSAGE = new FacesMessage(FacesMessage.SEVERITY_INFO,
                FacesMessage.SEVERITY_INFO.toString(),
                createExpectedMessageString(Messages.getMessageAsString(CONTENT_WILL_BE_SAVED),
                        "Initialize"));

        page.setType("TEST1");
        page.setName("Test1");

        page.setSelectWorkflowPattern(2L);
        page.setWorkflowPatternList(createWorkflowPatternList());

        // テスト実行
        String next = page.next();

        assertEquals("corresponTypeConfirmation", next);

        assertEquals("TEST1", page.getCorresponType().getCorresponType());
        assertEquals("Test1", page.getCorresponType().getName());
        assertNull(page.getCorresponType().getProjectId());
        assertEquals(String.valueOf(2L), page
                .getCorresponType()
                .getWorkflowPattern()
                .getId()
                .toString());
        assertEquals("pattern2", page.getCorresponType().getWorkflowPattern().getName());
        assertEquals(AllowApproverToBrowse.INVISIBLE, page
                .getCorresponType()
                .getAllowApproverToBrowse());
        assertEquals(ForceToUseWorkflow.OPTIONAL, page.getCorresponType().getForceToUseWorkflow());
    }

    /**
     * 次画面遷移を検証する. 更新
     *
     * @throws Exception
     */
    @Test
    public void testNextUpdate() throws Exception {
        MockCustomFieldService.RET_VALIDATE = true;

        // 期待されるメッセージをセット
        FacesContextMock.EXPECTED_MESSAGE = new FacesMessage(FacesMessage.SEVERITY_INFO,
                FacesMessage.SEVERITY_INFO.toString(),
                createExpectedMessageString(Messages.getMessageAsString(CONTENT_WILL_BE_SAVED),
                        "Initialize"));

        page.setCorresponType(createCorresponType(false));
        MockAbstractPage.RET_PROJECT_ID = "PJ1";

        page.setType("TEST1");
        page.setName("Test1");
        page.setAllow(true);
        page.setForce(true);

        page.setSelectWorkflowPattern(2L);
        page.setWorkflowPatternList(createWorkflowPatternList());

        // テスト実行
        String next = page.next();

        assertEquals("corresponTypeConfirmation", next);

        assertEquals("TEST1", page.getCorresponType().getCorresponType());
        assertEquals("Test1", page.getCorresponType().getName());
        assertEquals("PJ1", page.getCorresponType().getProjectId());
        assertEquals(String.valueOf(2L), page
                .getCorresponType()
                .getWorkflowPattern()
                .getId()
                .toString());
        assertEquals("pattern2", page.getCorresponType().getWorkflowPattern().getName());
        assertEquals(AllowApproverToBrowse.VISIBLE, page
                .getCorresponType()
                .getAllowApproverToBrowse());
        assertEquals(ForceToUseWorkflow.REQUIRED, page.getCorresponType().getForceToUseWorkflow());
    }

    /**
     * 前画面遷移を検証する.
     *
     * @throws Exception
     */
    @Test
    public void testBack() throws Exception {
        String back = page.back();
        assertEquals("corresponTypeIndex", back);
    }

    /**
     * テストで使用するコレポン文書種別を作成する.
     *
     * @param admin AdminHome,ProjectAdminHomeか判定するフラグ
     * @return コレポン文書種別
     */
    private CorresponType createCorresponType(boolean admin) {
        CorresponType type = new CorresponType();
        type.setId(11L);
        type.setProjectCorresponTypeId(111L);
        if (!admin) {
            type.setProjectId("PJ1");
            type.setProjectNameE("Project One");
            type.setCorresponType("Type-1");
        }
        type.setName("Name-1");
        WorkflowPattern pattern = new WorkflowPattern();
        pattern.setId(1L);
        pattern.setName("Pattern 1");
        pattern.setWorkflowCd("001");
        type.setWorkflowPattern(pattern);
        type.setAllowApproverToBrowse(AllowApproverToBrowse.INVISIBLE);
        type.setForceToUseWorkflow(ForceToUseWorkflow.OPTIONAL);
        type.setUseWhole(UseWhole.ALL);
        return type;
    }

    /**
     * テストで使用するコレポン文書種別を作成する.
     *
     * @return プロジェクトカスタム構成
     */
    private ProjectCustomSetting createProjectCustomSetting() {
        ProjectCustomSetting setting = new ProjectCustomSetting();
        setting.setUseCorresponAccessControl(true);
        return setting;
    }

    /**
     * 承認フローパターンリストを作成する.
     *
     * @return
     */
    private List<WorkflowPattern> createWorkflowPatternList() {
        List<WorkflowPattern> workflowPatternList = new ArrayList<WorkflowPattern>();
        WorkflowPattern wp = new WorkflowPattern();

        wp.setId(1L);
        wp.setName("pattern1");
        wp.setWorkflowCd("001");
        workflowPatternList.add(wp);

        wp = new WorkflowPattern();
        wp.setId(2L);
        wp.setName("pattern2");
        wp.setWorkflowCd("002");
        workflowPatternList.add(wp);

        wp = new WorkflowPattern();
        wp.setId(3L);
        wp.setName("pattern3");
        wp.setWorkflowCd("003");
        workflowPatternList.add(wp);

        return workflowPatternList;
    }

    public static class MockAbstractPage extends MockUp<AbstractPage> {
        static String RET_PROJECT_ID;
        static boolean IS_SYSTEM_ADMIN;
        static boolean IS_PROJECT_ADMIN;

        @Mock
        public String getCurrentProjectId() {
            return RET_PROJECT_ID;
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
        public AbstractCondition getPreviousSearchCondition(Class<? extends AbstractCondition> clazz) {
            return new AbstractCondition();
        }
    }

    public static class MockCorresponTypeService extends MockUp<CorresponTypeServiceImpl> {
        static SearchCorresponTypeResult RET_SEARCH;
        static byte[] RET_EXCEL;
        static Long RET_ASSIGN_TO;
        static CorresponType CRT_CORRESPONTYPE;
        static CorresponType RET_FIND;
        static List<WorkflowPattern> RET_SEARCH_WORKFLOWPATTERN;
        static boolean RET_VALIDATE;

        @Mock
        public SearchCorresponTypeResult searchPagingList(SearchCorresponTypeCondition condition) {
            return RET_SEARCH;
        }

        @Mock
        public byte[] generateExcel(List<CorresponType> corresponTypes)
                throws ServiceAbortException {
            return RET_EXCEL;
        }

        @Mock
        public Long assignTo(CorresponType corresponType) throws ServiceAbortException {
            CRT_CORRESPONTYPE = corresponType;
            return RET_ASSIGN_TO;
        }

        @Mock
        public CorresponType find(Long id) throws ServiceAbortException {
            return RET_FIND;
        }

        @Mock
        public List<WorkflowPattern> searchWorkflowPattern() {
            return RET_SEARCH_WORKFLOWPATTERN;
        }

        @Mock
        public boolean validate(CorresponType corresponType) throws ServiceAbortException {
            return RET_VALIDATE;
        }
    }

    public static class MockProjectCustomSettingService extends MockUp<ProjectCustomSettingServiceImpl> {
        static ProjectCustomSetting RET_SEARCH;

        @Mock
        public ProjectCustomSetting find(String projectId) {
            return RET_SEARCH;
        }
    }
}
