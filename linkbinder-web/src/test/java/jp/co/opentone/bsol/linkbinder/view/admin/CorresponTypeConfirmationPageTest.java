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
import jp.co.opentone.bsol.linkbinder.dto.CorresponType;
import jp.co.opentone.bsol.linkbinder.dto.WorkflowPattern;
import jp.co.opentone.bsol.linkbinder.dto.code.AllowApproverToBrowse;
import jp.co.opentone.bsol.linkbinder.dto.code.ForceToUseWorkflow;
import jp.co.opentone.bsol.linkbinder.dto.code.UseWhole;
import jp.co.opentone.bsol.linkbinder.dto.condition.AbstractCondition;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.service.admin.impl.CorresponTypeServiceImpl;
import jp.co.opentone.bsol.linkbinder.view.AbstractPage;
import mockit.Mock;
import mockit.MockUp;

/**
 * {@link CorresponTypeConfirmationPage}のテストケース.
 * @author opentone
 */
public class CorresponTypeConfirmationPageTest extends AbstractTestCase {

    /**
     * テスト対象.
     */
    @Resource
    private CorresponTypeConfirmationPage page;

    /**
     * テストの前準備.
     */
    @BeforeClass
    public static void testSetUp() {
        FacesContextMock.initialize();
        new MockAbstractPage();
        new MockCorresponTypeService();
    }

    /**
     * 後始末.
     */
    @AfterClass
    public static void testTeardown() {
        new MockAbstractPage().tearDown();
        new MockCorresponTypeService().tearDown();
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
        MockCorresponTypeService.RET_SAVE = null;
        MockCorresponTypeService.CRT_SAVE = null;
    }

    /**
     * 初期化アクションを検証する.
     * @throws Exception
     */
    @Test
    public void testInitialize() throws Exception {
        MockAbstractPage.IS_SYSTEM_ADMIN = true;

        // 前画面からのパラメータ設定
        page.setType("T");
        page.setName("xxx");

        page.initialize();

        assertTrue(page.isInitialDisplaySuccess());
    }

    /**
     * 初期化アクションを検証する.
     * ProjectAdmin
     * @throws Exception
     */
    @Test
    public void testInitializeProject() throws Exception {
        MockAbstractPage.IS_PROJECT_ADMIN = true;
        MockAbstractPage.RET_PROJECT_ID = "PJ1";
        page.initialize();

        assertTrue(page.isInitialDisplaySuccess());
    }

    /**
     * 初期化アクションを検証する. 権限がない
     * @throws Exception
     */
    @Test
    public void testInitializeInvalidPermission() throws Exception {
        MockAbstractPage.IS_SYSTEM_ADMIN = false;
        MockAbstractPage.IS_PROJECT_ADMIN = false;

        // 期待されるメッセージをセット
        FacesContextMock.EXPECTED_MESSAGE =
                new FacesMessage(FacesMessage.SEVERITY_ERROR, FacesMessage.SEVERITY_ERROR
                    .toString(), createExpectedMessageString(Messages
                    .getMessageAsString(CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT),
                    "Initialize"));

        try {
            page.initialize();
            fail("例外が発生していない");
        } catch (InvalidOperationRuntimeException e) {
            ServiceAbortException actual = (ServiceAbortException)e.getCause();
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT, actual.getMessageCode());
        }
    }

    /**
     * 保存アクションを検証する.
     * @throws Exception
     */
    @Test
    public void testSave() throws Exception {
        CorresponType keep = createCorresponType(true);
        page.setCorresponType(createCorresponType(true));
        MockCorresponTypeService.RET_SAVE = 1L;

        // 期待されるメッセージをセット
        FacesContextMock.EXPECTED_MESSAGE =
                new FacesMessage(
                    FacesMessage.SEVERITY_INFO, FacesMessage.SEVERITY_INFO.toString(),
                    createExpectedMessageString(Messages.getMessageAsString(CORRESPON_TYPE_SAVED),
                        "Save"));

        page.save();

        assertEquals(keep.toString(), MockCorresponTypeService.CRT_SAVE.toString());
    }

    /**
     * 前画面へ遷移する処理を検証する.
     * @throws Exception
     */
    @Test
    public void testBack() throws Exception {
        String back = page.back();
        assertEquals("corresponTypeEdit", back);
    }

    /**
     * テストで使用するコレポン文書種別を作成する.
     * @param admin
     *            AdminHome,ProjectAdminHomeか判定するフラグ
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
        static Long RET_SAVE;
        static CorresponType CRT_SAVE;

        @Mock
        public Long save(CorresponType corresponType) throws ServiceAbortException {
            CRT_SAVE = corresponType;
            return RET_SAVE;
        }
    }
}
