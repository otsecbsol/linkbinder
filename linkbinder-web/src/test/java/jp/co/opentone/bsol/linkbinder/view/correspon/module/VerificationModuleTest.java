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
package jp.co.opentone.bsol.linkbinder.view.correspon.module;

import static jp.co.opentone.bsol.framework.core.message.MessageCode.*;
import static jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

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
import jp.co.opentone.bsol.framework.core.util.ArgumentValidator;
import jp.co.opentone.bsol.framework.extension.jsf.FacesContextMock;
import jp.co.opentone.bsol.framework.test.AbstractTestCase;
import jp.co.opentone.bsol.linkbinder.dto.Correspon;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroup;
import jp.co.opentone.bsol.linkbinder.dto.CorresponReadStatus;
import jp.co.opentone.bsol.linkbinder.dto.CorresponType;
import jp.co.opentone.bsol.linkbinder.dto.Project;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.Workflow;
import jp.co.opentone.bsol.linkbinder.dto.WorkflowPattern;
import jp.co.opentone.bsol.linkbinder.dto.code.CorresponStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.ReadStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowProcessStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowType;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.service.correspon.impl.CorresponReadStatusServiceImpl;
import jp.co.opentone.bsol.linkbinder.service.correspon.impl.CorresponServiceImpl;
import jp.co.opentone.bsol.linkbinder.service.correspon.impl.CorresponWorkflowServiceImpl;
import jp.co.opentone.bsol.linkbinder.view.AbstractPage;
import jp.co.opentone.bsol.linkbinder.view.correspon.CorresponPage;
import mockit.Mock;
import mockit.MockUp;

/**
 * @author opentone
 */
public class VerificationModuleTest extends AbstractTestCase {

    /**
     * ページクラス.
     */
    @Resource(name = "corresponPage")
    private CorresponPage page;

    /**
     * テスト対象.
     */
    @Resource
    private VerificationModule verificationModule;

    /** checker1 */
    private static final User CHECKER_1 = new User();
    static {
        CHECKER_1.setEmpNo("USER001");
    }

    /** checker2 */
    private static final User CHECKER_2 = new User();
    static {
        CHECKER_2.setEmpNo("USER002");
    }

    /** checker3 */
    private static final User CHECKER_3 = new User();
    static {
        CHECKER_3.setEmpNo("USER003");
    }

    /** approver */
    private static final User APPROVER = new User();
    static {
        APPROVER.setEmpNo("USER004");
    }

    /**
     * テストの前準備.
     */
    @BeforeClass
    public static void testSetUp() {
        FacesContextMock.initialize();
        new MockCorresponService();
        new MockCorresponWorkflowService();
        new MockCorresponReadStatusService();
        new MockAbstractPage();
    }

    /**
     * 後始末.
     */
    @AfterClass
    public static void testTeardown() {
        new MockCorresponService().tearDown();
        new MockCorresponWorkflowService().tearDown();
        new MockCorresponReadStatusService().tearDown();
        new MockAbstractPage().tearDown();
        FacesContextMock.tearDown();
    }

    @Before
    public void setUp() {
        MockCorresponService.RET_FIND = null;
        MockCorresponWorkflowService.RET_METHOD = null;
        MockCorresponWorkflowService.RET_CORRESPON = null;
        MockCorresponWorkflowService.RET_WORKFLOW = null;
        MockAbstractPage.RET_USER = null;
        MockAbstractPage.RET_SYSTEM_ADMIN = false;
        MockAbstractPage.RET_PROJECT_ADMIN = false;
        MockAbstractPage.RET_GROUP_ADMIN = false;
        MockAbstractPage.RET_PROJECT = null;

        page.setUsersWorkflow(null);
        page.setBackPage(null);
    }

    @After
    public void tearDown() {
        MockCorresponService.RET_FIND = null;
        MockCorresponWorkflowService.RET_METHOD = null;
        MockCorresponWorkflowService.RET_CORRESPON = null;
        MockCorresponWorkflowService.RET_WORKFLOW = null;
        MockAbstractPage.RET_USER = null;
        MockAbstractPage.RET_SYSTEM_ADMIN = false;
        MockAbstractPage.RET_PROJECT_ADMIN = false;
        MockAbstractPage.RET_GROUP_ADMIN = false;
        MockAbstractPage.RET_PROJECT = null;
    }

    /**
     * 初期化アクションのテスト. SystemAdminの時、すべてのボタンを表示.
     */
    @Test
    public void testInitializeSystemAdmin_1() {
        setRequiredFields();

        MockAbstractPage.RET_SYSTEM_ADMIN = true;
        MockAbstractPage.RET_PROJECT_ADMIN = false;
        MockAbstractPage.RET_GROUP_ADMIN = false;
        User user = new User();
        user.setEmpNo("99999");
        MockAbstractPage.RET_USER = user;

        Correspon expCorrespon = createCorrespon();
        List<Workflow> workflowList = createWorkflowList();
        expCorrespon.setWorkflows(workflowList);
        expCorrespon.setWorkflowStatus(WorkflowStatus.REQUEST_FOR_CHECK);

        MockCorresponService.RET_FIND = expCorrespon;
        // 必須のプロパティをセットして実行
        page.setCorrespon(expCorrespon);
        page.setId(expCorrespon.getId());
        verificationModule.initialize();

        assertEquals(expCorrespon.toString(), page.getCorrespon().toString());

        // 画面制御 + 自身のワークフローなし
        assertTrue(page.isCheck());
        assertTrue(page.isApprove());
        assertTrue(page.isDeny());
        assertNull(page.getUsersWorkflow());

        List<Workflow> expectedList = createWorkflowList();
        List<Workflow> resultList = page.getWorkflow();
        // Preperer
        Workflow resultPre = resultList.get(0);
        assertEquals(expCorrespon.getCreatedBy(), resultPre.getUser());
        // Prepererセットのために2行目以降に繰り下げ
        for (int i = 1; i < resultList.size(); i++) {
            Workflow expected = expectedList.get(i - 1);
            Workflow result = resultList.get(i);
            assertEquals(String.valueOf(expected.getId()), String.valueOf(result.getId()));
            assertEquals(expected.getWorkflowType(), result.getWorkflowType());
            assertEquals(expected.getUser().getEmpNo(), result.getUser().getEmpNo());
        }
        assertEquals("odd,even,odd,even,odd", page.getTrClassId());
    }

    /**
     * 初期化アクションのテスト. SystemAdminでもDENIEDの時はボタン非表示.
     */
    @Test
    public void testInitializeSystemAdmin_2() {
        setRequiredFields();

        MockAbstractPage.RET_SYSTEM_ADMIN = true;
        MockAbstractPage.RET_PROJECT_ADMIN = false;
        MockAbstractPage.RET_GROUP_ADMIN = false;
        User user = new User();
        user.setEmpNo("99999");
        MockAbstractPage.RET_USER = user;

        Correspon expCorrespon = createCorrespon();
        List<Workflow> workflowList = createWorkflowList();
        expCorrespon.setWorkflows(workflowList);
        expCorrespon.setWorkflowStatus(WorkflowStatus.DENIED);

        MockCorresponService.RET_FIND = expCorrespon;
        // 必須のプロパティをセットして実行
        page.setCorrespon(expCorrespon);
        page.setId(expCorrespon.getId());
        verificationModule.initialize();

        assertEquals(expCorrespon.toString(), page.getCorrespon().toString());

        // 画面制御 + 自身のワークフローなし
        assertFalse(page.isCheck());
        assertFalse(page.isApprove());
        assertFalse(page.isDeny());
        assertNull(page.getUsersWorkflow());

        List<Workflow> expectedList = createWorkflowList();
        List<Workflow> resultList = page.getWorkflow();
        // Preperer
        Workflow resultPre = resultList.get(0);
        assertEquals(expCorrespon.getCreatedBy(), resultPre.getUser());
        // Prepererセットのために2行目以降に繰り下げ
        for (int i = 1; i < resultList.size(); i++) {
            Workflow expected = expectedList.get(i - 1);
            Workflow result = resultList.get(i);
            assertEquals(String.valueOf(expected.getId()), String.valueOf(result.getId()));
            assertEquals(expected.getWorkflowType(), result.getWorkflowType());
            assertEquals(expected.getUser().getEmpNo(), result.getUser().getEmpNo());
        }
        assertEquals("odd,even,odd,even,odd", page.getTrClassId());
    }

    /**
     * 初期化アクションのテスト. SystemAdminでもISSUEDの時はボタン非表示.
     */
    @Test
    public void testInitializeSystemAdmin_3() {
        setRequiredFields();

        MockAbstractPage.RET_SYSTEM_ADMIN = true;
        MockAbstractPage.RET_PROJECT_ADMIN = false;
        MockAbstractPage.RET_GROUP_ADMIN = false;
        User user = new User();
        user.setEmpNo("99999");
        MockAbstractPage.RET_USER = user;

        Correspon expCorrespon = createCorrespon();
        List<Workflow> workflowList = createWorkflowList();
        expCorrespon.setWorkflows(workflowList);
        expCorrespon.setWorkflowStatus(WorkflowStatus.ISSUED);

        MockCorresponService.RET_FIND = expCorrespon;
        // 必須のプロパティをセットして実行
        page.setCorrespon(expCorrespon);
        page.setId(expCorrespon.getId());
        verificationModule.initialize();

        assertEquals(expCorrespon.toString(), page.getCorrespon().toString());

        // 画面制御 + 自身のワークフローなし
        assertFalse(page.isCheck());
        assertFalse(page.isApprove());
        assertFalse(page.isDeny());
        assertNull(page.getUsersWorkflow());

        List<Workflow> expectedList = createWorkflowList();
        List<Workflow> resultList = page.getWorkflow();
        // Preperer
        Workflow resultPre = resultList.get(0);
        assertEquals(expCorrespon.getCreatedBy(), resultPre.getUser());
        // Prepererセットのために2行目以降に繰り下げ
        for (int i = 1; i < resultList.size(); i++) {
            Workflow expected = expectedList.get(i - 1);
            Workflow result = resultList.get(i);
            assertEquals(String.valueOf(expected.getId()), String.valueOf(result.getId()));
            assertEquals(expected.getWorkflowType(), result.getWorkflowType());
            assertEquals(expected.getUser().getEmpNo(), result.getUser().getEmpNo());
        }
        assertEquals("odd,even,odd,even,odd", page.getTrClassId());
    }

    /**
     * 初期化アクションのテスト. ProjectAdminですべてのボタンを表示.
     */
    @Test
    public void testInitializeProjectAdmin_1() {
        setRequiredFields();

        MockAbstractPage.RET_SYSTEM_ADMIN = false;
        MockAbstractPage.RET_PROJECT_ADMIN = true;
        MockAbstractPage.RET_GROUP_ADMIN = false;
        User user = new User();
        user.setEmpNo("99999");
        MockAbstractPage.RET_USER = user;
        Project project = new Project();
        project.setProjectId("PJ1");
        MockAbstractPage.RET_PROJECT = project;

        Correspon expCorrespon = createCorrespon();
        List<Workflow> workflowList = createWorkflowList();
        expCorrespon.setWorkflows(workflowList);
        expCorrespon.setWorkflowStatus(WorkflowStatus.REQUEST_FOR_CHECK);

        MockCorresponService.RET_FIND = expCorrespon;
        // 必須のプロパティをセットして実行
        page.setCorrespon(expCorrespon);
        page.setId(expCorrespon.getId());
        verificationModule.initialize();

        assertEquals(expCorrespon.toString(), page.getCorrespon().toString());

        // 画面制御 + 自身のワークフローなし
        assertTrue(page.isCheck());
        assertTrue(page.isApprove());
        assertTrue(page.isDeny());
        assertNull(page.getUsersWorkflow());

        List<Workflow> expectedList = createWorkflowList();
        List<Workflow> resultList = page.getWorkflow();
        // Preperer
        Workflow resultPre = resultList.get(0);
        assertEquals(expCorrespon.getCreatedBy(), resultPre.getUser());
        // Prepererセットのために2行目以降に繰り下げ
        for (int i = 1; i < resultList.size(); i++) {
            Workflow expected = expectedList.get(i - 1);
            Workflow result = resultList.get(i);
            assertEquals(String.valueOf(expected.getId()), String.valueOf(result.getId()));
            assertEquals(expected.getWorkflowType(), result.getWorkflowType());
            assertEquals(expected.getUser().getEmpNo(), result.getUser().getEmpNo());
        }
        assertEquals("odd,even,odd,even,odd", page.getTrClassId());
    }

    /**
     * 初期化アクションのテスト. ProjectAdminでもDENIEDの時はボタン非表示.
     */
    @Test
    public void testInitializeProjectAdmin_2() {
        setRequiredFields();

        MockAbstractPage.RET_SYSTEM_ADMIN = false;
        MockAbstractPage.RET_PROJECT_ADMIN = true;
        MockAbstractPage.RET_GROUP_ADMIN = false;
        User user = new User();
        user.setEmpNo("99999");
        MockAbstractPage.RET_USER = user;
        Project project = new Project();
        project.setProjectId("PJ1");
        MockAbstractPage.RET_PROJECT = project;

        Correspon expCorrespon = createCorrespon();
        List<Workflow> workflowList = createWorkflowList();
        expCorrespon.setWorkflows(workflowList);
        expCorrespon.setWorkflowStatus(WorkflowStatus.DENIED);

        MockCorresponService.RET_FIND = expCorrespon;
        // 必須のプロパティをセットして実行
        page.setCorrespon(expCorrespon);
        page.setId(expCorrespon.getId());
        verificationModule.initialize();

        assertEquals(expCorrespon.toString(), page.getCorrespon().toString());

        // 画面制御 + 自身のワークフローなし
        assertFalse(page.isCheck());
        assertFalse(page.isApprove());
        assertFalse(page.isDeny());
        assertNull(page.getUsersWorkflow());

        List<Workflow> expectedList = createWorkflowList();
        List<Workflow> resultList = page.getWorkflow();
        // Preperer
        Workflow resultPre = resultList.get(0);
        assertEquals(expCorrespon.getCreatedBy(), resultPre.getUser());
        // Prepererセットのために2行目以降に繰り下げ
        for (int i = 1; i < resultList.size(); i++) {
            Workflow expected = expectedList.get(i - 1);
            Workflow result = resultList.get(i);
            assertEquals(String.valueOf(expected.getId()), String.valueOf(result.getId()));
            assertEquals(expected.getWorkflowType(), result.getWorkflowType());
            assertEquals(expected.getUser().getEmpNo(), result.getUser().getEmpNo());
        }
        assertEquals("odd,even,odd,even,odd", page.getTrClassId());
    }

    /**
     * 初期化アクションのテスト. ProjectAdminでもISSUEDの時はボタン非表示.
     */
    @Test
    public void testInitializeProjectAdmin_3() {
        setRequiredFields();

        MockAbstractPage.RET_SYSTEM_ADMIN = false;
        MockAbstractPage.RET_PROJECT_ADMIN = true;
        MockAbstractPage.RET_GROUP_ADMIN = false;
        User user = new User();
        user.setEmpNo("99999");
        MockAbstractPage.RET_USER = user;
        Project project = new Project();
        project.setProjectId("PJ1");
        MockAbstractPage.RET_PROJECT = project;

        Correspon expCorrespon = createCorrespon();
        List<Workflow> workflowList = createWorkflowList();
        expCorrespon.setWorkflows(workflowList);
        expCorrespon.setWorkflowStatus(WorkflowStatus.ISSUED);

        MockCorresponService.RET_FIND = expCorrespon;
        // 必須のプロパティをセットして実行
        page.setCorrespon(expCorrespon);
        page.setId(expCorrespon.getId());
        verificationModule.initialize();

        assertEquals(expCorrespon.toString(), page.getCorrespon().toString());

        // 画面制御 + 自身のワークフローなし
        assertFalse(page.isCheck());
        assertFalse(page.isApprove());
        assertFalse(page.isDeny());
        assertNull(page.getUsersWorkflow());

        List<Workflow> expectedList = createWorkflowList();
        List<Workflow> resultList = page.getWorkflow();
        // Preperer
        Workflow resultPre = resultList.get(0);
        assertEquals(expCorrespon.getCreatedBy(), resultPre.getUser());
        // Prepererセットのために2行目以降に繰り下げ
        for (int i = 1; i < resultList.size(); i++) {
            Workflow expected = expectedList.get(i - 1);
            Workflow result = resultList.get(i);
            assertEquals(String.valueOf(expected.getId()), String.valueOf(result.getId()));
            assertEquals(expected.getWorkflowType(), result.getWorkflowType());
            assertEquals(expected.getUser().getEmpNo(), result.getUser().getEmpNo());
        }
        assertEquals("odd,even,odd,even,odd", page.getTrClassId());
    }



    /**
     * 初期化アクションのテスト. ProjectAdminですべてのボタンを表示.
     */
    @Test
    public void testInitializeGroupAdmin_1() {
        setRequiredFields();

        MockAbstractPage.RET_SYSTEM_ADMIN = false;
        MockAbstractPage.RET_PROJECT_ADMIN = false;
        MockAbstractPage.RET_GROUP_ADMIN = true;
        User user = new User();
        user.setEmpNo("99999");
        MockAbstractPage.RET_USER = user;
        Project project = new Project();
        project.setProjectId("PJ1");
        MockAbstractPage.RET_PROJECT = project;

        Correspon expCorrespon = createCorrespon();
        List<Workflow> workflowList = createWorkflowList();
        expCorrespon.setWorkflows(workflowList);
        expCorrespon.setWorkflowStatus(WorkflowStatus.REQUEST_FOR_CHECK);

        MockCorresponService.RET_FIND = expCorrespon;
        // 必須のプロパティをセットして実行
        page.setCorrespon(expCorrespon);
        page.setId(expCorrespon.getId());
        verificationModule.initialize();

        assertEquals(expCorrespon.toString(), page.getCorrespon().toString());

        // 画面制御
        assertTrue(page.isCheck());
        assertTrue(page.isApprove());
        assertTrue(page.isDeny());


        List<Workflow> expectedList = createWorkflowList();
        List<Workflow> resultList = page.getWorkflow();
        // Preperer
        Workflow resultPre = resultList.get(0);
        assertEquals(expCorrespon.getCreatedBy(), resultPre.getUser());
        // Prepererセットのために2行目以降に繰り下げ
        for (int i = 1; i < resultList.size(); i++) {
            Workflow expected = expectedList.get(i - 1);
            Workflow result = resultList.get(i);
            assertEquals(String.valueOf(expected.getId()), String.valueOf(result.getId()));
            assertEquals(expected.getWorkflowType(), result.getWorkflowType());
            assertEquals(expected.getUser().getEmpNo(), result.getUser().getEmpNo());
        }
        assertEquals("odd,even,odd,even,odd", page.getTrClassId());
    }


    /**
     * 初期化アクションのテスト. GroupAdminで対象のCheckerのボタンを表示.
     */
    @Test
    public void testInitializeGroupAdmin_2() {
        setRequiredFields();

        MockAbstractPage.RET_SYSTEM_ADMIN = false;
        MockAbstractPage.RET_PROJECT_ADMIN = false;
        MockAbstractPage.RET_GROUP_ADMIN = true;
        User user = new User();
        user.setEmpNo("USER001");
        MockAbstractPage.RET_USER = user;
        Project project = new Project();
        project.setProjectId("PJ1");
        MockAbstractPage.RET_PROJECT = project;

        Correspon expCorrespon = createCorrespon();
        List<Workflow> workflowList = createWorkflowList();
        expCorrespon.setWorkflows(workflowList);
        expCorrespon.setWorkflowStatus(WorkflowStatus.REQUEST_FOR_CHECK);

        MockCorresponService.RET_FIND = expCorrespon;
        // 必須のプロパティをセットして実行
        page.setCorrespon(expCorrespon);
        page.setId(expCorrespon.getId());
        verificationModule.initialize();

        assertEquals(expCorrespon.toString(), page.getCorrespon().toString());

        // 画面制御
        assertTrue(page.isCheck());
        assertTrue(page.isApprove());
        assertTrue(page.isDeny());

        List<Workflow> expectedList = createWorkflowList();
        List<Workflow> resultList = page.getWorkflow();
        // Preperer
        Workflow resultPre = resultList.get(0);
        assertEquals(expCorrespon.getCreatedBy(), resultPre.getUser());
        // Prepererセットのために2行目以降に繰り下げ
        for (int i = 1; i < resultList.size(); i++) {
            Workflow expected = expectedList.get(i - 1);
            Workflow result = resultList.get(i);
            assertEquals(String.valueOf(expected.getId()), String.valueOf(result.getId()));
            assertEquals(expected.getWorkflowType(), result.getWorkflowType());
            assertEquals(expected.getUser().getEmpNo(), result.getUser().getEmpNo());
        }
        assertEquals("odd,even,odd,even,odd", page.getTrClassId());
    }
    /**
     * 初期化アクションのテスト. REQUEST_FOR_CHECKのCheckerでCheckとDenyのボタンを表示.
     */
    @Test
    public void testInitializeGroupAdmin_3() {
        setRequiredFields();

        MockAbstractPage.RET_SYSTEM_ADMIN = false;
        MockAbstractPage.RET_PROJECT_ADMIN = false;
        MockAbstractPage.RET_GROUP_ADMIN = true;

        User user = new User();
        user.setEmpNo("99999");
        MockAbstractPage.RET_USER = user;

        Project project = new Project();
        project.setProjectId("PJ1");
        MockAbstractPage.RET_PROJECT = project;

        Correspon expCorrespon = createCorrespon();
        List<Workflow> workflowList = createWorkflowList();
        workflowList.get(1).setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_CHECK);
        expCorrespon.setWorkflows(workflowList);

        MockCorresponService.RET_FIND = expCorrespon;

        // checker2がログイン
        MockAbstractPage.RET_USER = CHECKER_2;

        Workflow expected = workflowList.get(1);

        // 必須のプロパティをセットして実行
        page.setCorrespon(expCorrespon);
        page.setId(expCorrespon.getId());
        verificationModule.initialize();
        assertEquals(expCorrespon.toString(), page.getCorrespon().toString());

        // 画面制御 + 自身のワークフローあり
        assertTrue(page.isCheck());
        assertTrue(page.isApprove()); // 表示しない
        assertTrue(page.isDeny());
        assertEquals(expected, page.getWorkflow().get(2));

        List<Workflow> expectedList = createWorkflowList();
        List<Workflow> resultList = page.getWorkflow();
        // Preperer
        Workflow resultPre = resultList.get(0);
        assertEquals(expCorrespon.getCreatedBy(), resultPre.getUser());
        // Prepererセットのために2行目以降に繰り下げ
        for (int i = 1; i < resultList.size(); i++) {
            Workflow expected2 = expectedList.get(i - 1);
            Workflow result = resultList.get(i);
            assertEquals(String.valueOf(expected2.getId()), String.valueOf(result.getId()));
            assertEquals(expected2.getWorkflowType(), result.getWorkflowType());
            assertEquals(expected2.getUser().getEmpNo(), result.getUser().getEmpNo());
        }
        assertEquals("odd,even,role,even,odd", page.getTrClassId());
    }


    /**
     * 初期化アクションのテスト. REQUEST_FOR_CHECKのCheckerでCheckとDenyのボタンを表示.
     */
    @Test
    public void testInitializeChecker_1() {
        setRequiredFields();

        MockAbstractPage.RET_SYSTEM_ADMIN = false;
        MockAbstractPage.RET_PROJECT_ADMIN = false;
        MockAbstractPage.RET_GROUP_ADMIN = false;

        Project project = new Project();
        project.setProjectId("PJ1");
        MockAbstractPage.RET_PROJECT = project;

        Correspon expCorrespon = createCorrespon();
        List<Workflow> workflowList = createWorkflowList();
        workflowList.get(1).setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_CHECK);
        expCorrespon.setWorkflows(workflowList);

        MockCorresponService.RET_FIND = expCorrespon;

        // checker2がログイン
        MockAbstractPage.RET_USER = CHECKER_2;

        Workflow expected = workflowList.get(1);

        // 必須のプロパティをセットして実行
        page.setCorrespon(expCorrespon);
        page.setId(expCorrespon.getId());
        verificationModule.initialize();
        assertEquals(expCorrespon.toString(), page.getCorrespon().toString());

        // 画面制御 + 自身のワークフローあり
        assertTrue(page.isCheck());
        assertFalse(page.isApprove()); // 表示しない
        assertTrue(page.isDeny());
        assertEquals(expected, page.getUsersWorkflow());

        List<Workflow> expectedList = createWorkflowList();
        List<Workflow> resultList = page.getWorkflow();
        // Preperer
        Workflow resultPre = resultList.get(0);
        assertEquals(expCorrespon.getCreatedBy(), resultPre.getUser());
        // Prepererセットのために2行目以降に繰り下げ
        for (int i = 1; i < resultList.size(); i++) {
            Workflow expected2 = expectedList.get(i - 1);
            Workflow result = resultList.get(i);
            assertEquals(String.valueOf(expected2.getId()), String.valueOf(result.getId()));
            assertEquals(expected2.getWorkflowType(), result.getWorkflowType());
            assertEquals(expected2.getUser().getEmpNo(), result.getUser().getEmpNo());
        }
        assertEquals("odd,even,role,even,odd", page.getTrClassId());
    }

    /**
     * 初期化アクションのテスト. UNDER_CONSIDERATIONのCheckerでCheckとDenyのボタンを表示.
     */
    @Test
    public void testInitializeChecker_2() {
        setRequiredFields();

        MockAbstractPage.RET_SYSTEM_ADMIN = false;
        MockAbstractPage.RET_PROJECT_ADMIN = false;
        MockAbstractPage.RET_GROUP_ADMIN = false;
        User user = new User();
        user.setEmpNo("99999");
        MockAbstractPage.RET_USER = user;

        Project project = new Project();
        project.setProjectId("PJ1");
        MockAbstractPage.RET_PROJECT = project;

        Correspon expCorrespon = createCorrespon();
        List<Workflow> workflowList = createWorkflowList();
        workflowList.get(1).setWorkflowProcessStatus(WorkflowProcessStatus.UNDER_CONSIDERATION);
        expCorrespon.setWorkflows(workflowList);

        MockCorresponService.RET_FIND = expCorrespon;

        // checker2がログイン
        MockAbstractPage.RET_USER = CHECKER_2;

        Workflow expected = workflowList.get(1);

        // 必須のプロパティをセットして実行
        page.setCorrespon(expCorrespon);
        page.setId(expCorrespon.getId());
        verificationModule.initialize();
        assertEquals(expCorrespon.toString(), page.getCorrespon().toString());

        // 画面制御 + 自身のワークフローあり
        assertTrue(page.isCheck());
        assertFalse(page.isApprove()); // 表示しない
        assertTrue(page.isDeny());
        assertEquals(expected, page.getUsersWorkflow());

        List<Workflow> expectedList = createWorkflowList();
        List<Workflow> resultList = page.getWorkflow();
        // Preperer
        Workflow resultPre = resultList.get(0);
        assertEquals(expCorrespon.getCreatedBy(), resultPre.getUser());
        // Prepererセットのために2行目以降に繰り下げ
        for (int i = 1; i < resultList.size(); i++) {
            Workflow expected2 = expectedList.get(i - 1);
            Workflow result = resultList.get(i);
            assertEquals(String.valueOf(expected2.getId()), String.valueOf(result.getId()));
            assertEquals(expected2.getWorkflowType(), result.getWorkflowType());
            assertEquals(expected2.getUser().getEmpNo(), result.getUser().getEmpNo());
        }
        assertEquals("odd,even,role,even,odd", page.getTrClassId());
    }

    /**
     * 初期化アクションのテスト. CHECKEDのCheckerはボタン非表示.
     */
    @Test
    public void testInitializeChecker_3() {
        setRequiredFields();

        MockAbstractPage.RET_SYSTEM_ADMIN = false;
        MockAbstractPage.RET_PROJECT_ADMIN = false;
        MockAbstractPage.RET_GROUP_ADMIN = false;
        User user = new User();
        user.setEmpNo("99999");
        MockAbstractPage.RET_USER = user;

        Project project = new Project();
        project.setProjectId("PJ1");
        MockAbstractPage.RET_PROJECT = project;

        Correspon expCorrespon = createCorrespon();
        List<Workflow> workflowList = createWorkflowList();
        workflowList.get(1).setWorkflowProcessStatus(WorkflowProcessStatus.CHECKED);
        expCorrespon.setWorkflows(workflowList);

        MockCorresponService.RET_FIND = expCorrespon;

        // checker2がログイン
        MockAbstractPage.RET_USER = CHECKER_2;

        Workflow expected = workflowList.get(1);

        // 必須のプロパティをセットして実行
        page.setCorrespon(expCorrespon);
        page.setId(expCorrespon.getId());
        verificationModule.initialize();
        assertEquals(expCorrespon.toString(), page.getCorrespon().toString());

        // 画面制御 + 自身のワークフローあり
        assertFalse(page.isCheck());
        assertFalse(page.isApprove());
        assertFalse(page.isDeny());
        assertEquals(expected, page.getUsersWorkflow());

        List<Workflow> expectedList = createWorkflowList();
        List<Workflow> resultList = page.getWorkflow();
        // Preperer
        Workflow resultPre = resultList.get(0);
        assertEquals(expCorrespon.getCreatedBy(), resultPre.getUser());
        // Prepererセットのために2行目以降に繰り下げ
        for (int i = 1; i < resultList.size(); i++) {
            Workflow expected2 = expectedList.get(i - 1);
            Workflow result = resultList.get(i);
            assertEquals(String.valueOf(expected2.getId()), String.valueOf(result.getId()));
            assertEquals(expected2.getWorkflowType(), result.getWorkflowType());
            assertEquals(expected2.getUser().getEmpNo(), result.getUser().getEmpNo());
        }
        assertEquals("odd,even,odd,even,odd", page.getTrClassId());
    }

    /**
     * 初期化アクションのテスト. REQUEST_FOR_APPROVALのApproverでApproveとDenyのボタンを表示.
     */
    @Test
    public void testInitializeApprover_1() {
        setRequiredFields();

        MockAbstractPage.RET_SYSTEM_ADMIN = false;
        MockAbstractPage.RET_PROJECT_ADMIN = false;
        MockAbstractPage.RET_GROUP_ADMIN = false;

        Project project = new Project();
        project.setProjectId("PJ1");
        MockAbstractPage.RET_PROJECT = project;

        Correspon expCorrespon = createCorrespon();
        List<Workflow> workflowList = createWorkflowList();
        workflowList.get(3).setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_APPROVAL);
        expCorrespon.setWorkflows(workflowList);

        MockCorresponService.RET_FIND = expCorrespon;

        // approverがログイン
        MockAbstractPage.RET_USER = APPROVER;

        Workflow expected = workflowList.get(3);

        // 必須のプロパティをセットして実行
        page.setCorrespon(expCorrespon);
        page.setId(expCorrespon.getId());
        verificationModule.initialize();

        assertEquals(expCorrespon.toString(), page.getCorrespon().toString());

        // 画面制御 + 自身のワークフローあり
        assertFalse(page.isCheck()); // 表示しない
        assertTrue(page.isApprove());
        assertTrue(page.isDeny());
        assertEquals(expected, page.getUsersWorkflow());

        List<Workflow> expectedList = createWorkflowList();
        List<Workflow> resultList = page.getWorkflow();
        // Preperer
        Workflow resultPre = resultList.get(0);
        assertEquals(expCorrespon.getCreatedBy(), resultPre.getUser());
        // Prepererセットのために2行目以降に繰り下げ
        for (int i = 1; i < resultList.size(); i++) {
            Workflow expected2 = expectedList.get(i - 1);
            Workflow result = resultList.get(i);
            assertEquals(String.valueOf(expected2.getId()), String.valueOf(result.getId()));
            assertEquals(expected2.getWorkflowType(), result.getWorkflowType());
            assertEquals(expected2.getUser().getEmpNo(), result.getUser().getEmpNo());
        }
        assertEquals("odd,even,odd,even,role", page.getTrClassId());
    }

    /**
     * 初期化アクションのテスト. UNDER_CONSIDERATIONのApproverでApproveとDenyのボタンを表示.
     */
    @Test
    public void testInitializeApprover_2() {
        setRequiredFields();

        MockAbstractPage.RET_SYSTEM_ADMIN = false;
        MockAbstractPage.RET_PROJECT_ADMIN = false;
        MockAbstractPage.RET_GROUP_ADMIN = false;

        Project project = new Project();
        project.setProjectId("PJ1");
        MockAbstractPage.RET_PROJECT = project;

        Correspon expCorrespon = createCorrespon();
        List<Workflow> workflowList = createWorkflowList();
        workflowList.get(3).setWorkflowProcessStatus(WorkflowProcessStatus.UNDER_CONSIDERATION);
        expCorrespon.setWorkflows(workflowList);

        MockCorresponService.RET_FIND = expCorrespon;

        // approverがログイン
        MockAbstractPage.RET_USER = APPROVER;

        Workflow expected = workflowList.get(3);

        // 必須のプロパティをセットして実行
        page.setCorrespon(expCorrespon);
        page.setId(expCorrespon.getId());
        verificationModule.initialize();

        assertEquals(expCorrespon.toString(), page.getCorrespon().toString());

        // 画面制御 + 自身のワークフローあり
        assertFalse(page.isCheck()); // 表示しない
        assertTrue(page.isApprove());
        assertTrue(page.isDeny());
        assertEquals(expected, page.getUsersWorkflow());

        List<Workflow> expectedList = createWorkflowList();
        List<Workflow> resultList = page.getWorkflow();
        // Preperer
        Workflow resultPre = resultList.get(0);
        assertEquals(expCorrespon.getCreatedBy(), resultPre.getUser());
        // Prepererセットのために2行目以降に繰り下げ
        for (int i = 1; i < resultList.size(); i++) {
            Workflow expected2 = expectedList.get(i - 1);
            Workflow result = resultList.get(i);
            assertEquals(String.valueOf(expected2.getId()), String.valueOf(result.getId()));
            assertEquals(expected2.getWorkflowType(), result.getWorkflowType());
            assertEquals(expected2.getUser().getEmpNo(), result.getUser().getEmpNo());
        }
        assertEquals("odd,even,odd,even,role", page.getTrClassId());
    }

    /**
     * 初期化アクションのテスト. APPROVEDのApproverはボタン非表示.
     */
    @Test
    public void testInitializeApprover_3() {
        setRequiredFields();

        MockAbstractPage.RET_SYSTEM_ADMIN = false;
        MockAbstractPage.RET_PROJECT_ADMIN = false;
        MockAbstractPage.RET_GROUP_ADMIN = false;

        Project project = new Project();
        project.setProjectId("PJ1");
        MockAbstractPage.RET_PROJECT = project;

        Correspon expCorrespon = createCorrespon();
        List<Workflow> workflowList = createWorkflowList();
        workflowList.get(3).setWorkflowProcessStatus(WorkflowProcessStatus.APPROVED);
        expCorrespon.setWorkflows(workflowList);

        MockCorresponService.RET_FIND = expCorrespon;

        // approverがログイン
        MockAbstractPage.RET_USER = APPROVER;

        Workflow expected = workflowList.get(3);

        // 必須のプロパティをセットして実行
        page.setCorrespon(expCorrespon);
        page.setId(expCorrespon.getId());
        verificationModule.initialize();

        assertEquals(expCorrespon.toString(), page.getCorrespon().toString());

        // 画面制御 + 自身のワークフローあり
        assertFalse(page.isCheck());
        assertFalse(page.isApprove());
        assertFalse(page.isDeny());
        assertEquals(expected, page.getUsersWorkflow());

        List<Workflow> expectedList = createWorkflowList();
        List<Workflow> resultList = page.getWorkflow();
        // Preperer
        Workflow resultPre = resultList.get(0);
        assertEquals(expCorrespon.getCreatedBy(), resultPre.getUser());
        // Prepererセットのために2行目以降に繰り下げ
        for (int i = 1; i < resultList.size(); i++) {
            Workflow expected2 = expectedList.get(i - 1);
            Workflow result = resultList.get(i);
            assertEquals(String.valueOf(expected2.getId()), String.valueOf(result.getId()));
            assertEquals(expected2.getWorkflowType(), result.getWorkflowType());
            assertEquals(expected2.getUser().getEmpNo(), result.getUser().getEmpNo());
        }
        assertEquals("odd,even,odd,even,odd", page.getTrClassId());
    }

    /**
     * 起動元からコレポン文書のIDが指定されない状態で実行した場合の検証.
     * @throws Exception
     */
    @Test
    public void testInitializedInvalidParam() throws Exception {
        setRequiredFields();

        // 期待されるエラーメッセージを用意
        FacesContextMock.EXPECTED_CLIENT_ID = null;
        FacesContextMock.EXPECTED_MESSAGE =
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                 FacesMessage.SEVERITY_ERROR.toString(),
                                 createExpectedMessageString(
                                     Messages.getMessageAsString(E_INVALID_PARAMETER),
                                     null));

        // 必須のプロパティをセットせず実行
        page.setId(null);
        page.setCorrespon(null);
        page.setWorkflow(null);
        try {
            verificationModule.initialize();
            fail("例外が発生していない");
        } catch (InvalidOperationRuntimeException e) {
            ServiceAbortException actual = (ServiceAbortException)e.getCause();
            assertEquals(ApplicationMessageCode.E_INVALID_PARAMETER, actual.getMessageCode());
        }

        assertNull(page.getCorrespon());
        assertNull(page.getWorkflow());
    }

    /**
     * 検証アクションのテスト.
     * @throws Exception
     */
    @Test
    public void testCheck() throws Exception {
        setRequiredFields();

        User retUser = new User();
        retUser.setEmpNo("USER004");
        MockAbstractPage.RET_USER = retUser;
        MockCorresponReadStatusService.RET_CURRENT_USER = retUser;

        List<Workflow> workflowList = createWorkflowList();
        Correspon expCorrespon = createCorrespon();
        expCorrespon.setWorkflows(workflowList);

        page.setId(expCorrespon.getId());
        page.setWorkflow(workflowList);
        page.setUsersWorkflow(workflowList.get(1));
        page.setCorrespon(expCorrespon);

        MockCorresponService.RET_FIND = expCorrespon;

        Project project = new Project();
        project.setProjectId("PJ1");
        MockAbstractPage.RET_PROJECT = project;

        // checker2がログイン
        MockAbstractPage.RET_USER = CHECKER_2;
        // checke
        page.getDataModel().setRowIndex(1);

        // 期待されるメッセージをセット
        FacesContextMock.EXPECTED_CLIENT_ID = null;
        FacesContextMock.EXPECTED_MESSAGE =
                new FacesMessage(FacesMessage.SEVERITY_INFO,
                                 FacesMessage.SEVERITY_INFO.toString(),
                                 createExpectedMessageString(
                                     Messages.getMessageAsString(CORRESPON_CHECKED),
                                     null));

        String url = verificationModule.check();

        // checker2のワークフロー
        Workflow expWorkflow = workflowList.get(1);

        assertEquals("corresponIndex?afterAction=true&sessionSort=1&sessionPageNo=1", url);
        assertEquals("check", MockCorresponWorkflowService.RET_METHOD);
        assertEquals(expCorrespon.toString(), MockCorresponWorkflowService.RET_CORRESPON.toString());
        assertEquals(expWorkflow.toString(), MockCorresponWorkflowService.RET_WORKFLOW.toString());
    }

    /**
     * 承認アクションのテスト.
     * @throws Exception
     */
    @Test
    public void testApprove() throws Exception {
        setRequiredFields();

        User retUser = new User();
        retUser.setEmpNo("USER004");
        MockAbstractPage.RET_USER = retUser;
        MockCorresponReadStatusService.RET_CURRENT_USER = retUser;

        List<Workflow> workflowList = createWorkflowList();
        Correspon expCorrespon = createCorrespon();
        expCorrespon.setWorkflows(workflowList);

        page.setId(expCorrespon.getId());
        page.setWorkflow(workflowList);
        page.setUsersWorkflow(workflowList.get(3));
        page.setCorrespon(expCorrespon);

        Project project = new Project();
        project.setProjectId("PJ1");
        MockAbstractPage.RET_PROJECT = project;

        MockCorresponService.RET_FIND = expCorrespon;
        // approverがログイン
        MockAbstractPage.RET_USER = APPROVER;
        // approve
        page.getDataModel().setRowIndex(3);

        // 期待されるメッセージをセット
        FacesContextMock.EXPECTED_CLIENT_ID = null;
        FacesContextMock.EXPECTED_MESSAGE =
                new FacesMessage(FacesMessage.SEVERITY_INFO,
                                 FacesMessage.SEVERITY_INFO.toString(),
                                 createExpectedMessageString(
                                     Messages.getMessageAsString(CORRESPON_APPROVED),
                                     null));

        String url = verificationModule.approve();

        // approverのワークフロー
        Workflow expWorkflow = workflowList.get(3);

        assertEquals("corresponIndex?afterAction=true&sessionSort=1&sessionPageNo=1", url);
        assertEquals("approve", MockCorresponWorkflowService.RET_METHOD);
        assertEquals(expCorrespon.toString(), MockCorresponWorkflowService.RET_CORRESPON.toString());
        assertEquals(expWorkflow.toString(), MockCorresponWorkflowService.RET_WORKFLOW.toString());
    }

    /**
     * 否認アクションのテスト.
     * @throws Exception
     */
    @Test
    public void testDenyChecker() throws Exception {
        setRequiredFields();

        // テストに必要な値をセット
        MockAbstractPage.RET_SYSTEM_ADMIN = false;
        MockAbstractPage.RET_PROJECT_ADMIN = false;
        MockAbstractPage.RET_GROUP_ADMIN = false;

        Project project = new Project();
        project.setProjectId("PJ1");
        MockAbstractPage.RET_PROJECT = project;

        Correspon expCorrespon = createCorrespon();
        List<Workflow> workflowList = createWorkflowList();
        workflowList.get(0).setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_CHECK);
        expCorrespon.setWorkflows(workflowList);

        MockCorresponService.RET_FIND = expCorrespon;

        // checker1がログイン
        MockAbstractPage.RET_USER = CHECKER_1;
        MockCorresponReadStatusService.RET_CURRENT_USER = CHECKER_1;
        // checke
        page.getDataModel().setRowIndex(0);

        Workflow expected = workflowList.get(0);

        // 期待されるメッセージをセット
        FacesContextMock.EXPECTED_CLIENT_ID = null;
        FacesContextMock.EXPECTED_MESSAGE =
                new FacesMessage(FacesMessage.SEVERITY_INFO,
                                 FacesMessage.SEVERITY_INFO.toString(),
                                 createExpectedMessageString(
                                     Messages.getMessageAsString(CORRESPON_DENIED),
                                     null));

        // 必須のプロパティをセットして実行
        page.setId(expCorrespon.getId());
        page.setWorkflow(workflowList);
        page.setCorrespon(expCorrespon);
        page.setUsersWorkflow(expected);

        String url = verificationModule.deny();

        // Checkerのワークフロー
        Workflow expWorkflow = workflowList.get(0);

        assertEquals("corresponIndex?afterAction=true&sessionSort=1&sessionPageNo=1", url);
        assertEquals("deny", MockCorresponWorkflowService.RET_METHOD);
        assertEquals(expCorrespon.toString(), MockCorresponWorkflowService.RET_CORRESPON.toString());
        assertEquals(expWorkflow.toString(), MockCorresponWorkflowService.RET_WORKFLOW.toString());

        // ダイアログを閉じてコレポンページが初期化されるため全てfalse
        assertFalse(page.isCheck());
        assertFalse(page.isApprove());
        assertFalse(page.isDeny());
        assertEquals(expected, page.getUsersWorkflow());
    }

    /**
     * Approverの否認アクションのテスト.
     */
    @Test
    public void testDenyApprover() {
        setRequiredFields();

        // テストに必要な値をセット
        MockAbstractPage.RET_SYSTEM_ADMIN = false;
        MockAbstractPage.RET_PROJECT_ADMIN = false;
        MockAbstractPage.RET_GROUP_ADMIN = false;

        Project project = new Project();
        project.setProjectId("PJ1");
        MockAbstractPage.RET_PROJECT = project;

        Correspon expCorrespon = createCorrespon();
        List<Workflow> workflowList = createWorkflowList();
        workflowList.get(3).setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_APPROVAL);
        expCorrespon.setWorkflows(workflowList);

        MockCorresponService.RET_FIND = expCorrespon;

        // approver1がログイン
        MockAbstractPage.RET_USER = APPROVER;
        MockCorresponReadStatusService.RET_CURRENT_USER = APPROVER;
        // Approve
        page.getDataModel().setRowIndex(3);

        Workflow expected = workflowList.get(3);

        // 期待されるメッセージをセット
        FacesContextMock.EXPECTED_CLIENT_ID = null;
        FacesContextMock.EXPECTED_MESSAGE =
                new FacesMessage(FacesMessage.SEVERITY_INFO,
                                 FacesMessage.SEVERITY_INFO.toString(),
                                 createExpectedMessageString(
                                     Messages.getMessageAsString(CORRESPON_DENIED),
                                     null));

        // 必須のプロパティをセットして実行
        page.setId(expCorrespon.getId());
        page.setWorkflow(workflowList);
        page.setCorrespon(expCorrespon);
        page.setUsersWorkflow(expected);

        String url = verificationModule.deny();

        // Checkerのワークフロー
        Workflow expWorkflow = workflowList.get(3);

        assertEquals("corresponIndex?afterAction=true&sessionSort=1&sessionPageNo=1", url);
        assertEquals("deny", MockCorresponWorkflowService.RET_METHOD);
        assertEquals(expCorrespon.toString(), MockCorresponWorkflowService.RET_CORRESPON.toString());
        assertEquals(expWorkflow.toString(), MockCorresponWorkflowService.RET_WORKFLOW.toString());

        // ダイアログを閉じてコレポンページが初期化されるため全てfalse
        assertFalse(page.isCheck());
        assertFalse(page.isApprove());
        assertFalse(page.isDeny());
        assertEquals(expected, page.getUsersWorkflow());
    }

    /**
     * SystemAdminかつCheckerの否認アクションのテスト.
     */
    @Test
    public void testDenySystemAdminChecker() {
        setRequiredFields();

        // テストに必要な値をセット
        MockAbstractPage.RET_SYSTEM_ADMIN = true;
        MockAbstractPage.RET_PROJECT_ADMIN = false;
        MockAbstractPage.RET_GROUP_ADMIN = false;

        Project project = new Project();
        project.setProjectId("PJ1");
        MockAbstractPage.RET_PROJECT = project;

        Correspon expCorrespon = createCorrespon();
        List<Workflow> workflowList = createWorkflowList();
        expCorrespon.setWorkflows(workflowList);
        expCorrespon.setWorkflowStatus(WorkflowStatus.UNDER_CONSIDERATION);

        MockCorresponService.RET_FIND = expCorrespon;

        // checker1がログイン
        MockAbstractPage.RET_USER = CHECKER_1;
        MockCorresponReadStatusService.RET_CURRENT_USER = CHECKER_1;
        // checke
        page.getDataModel().setRowIndex(0);

        Workflow expected = workflowList.get(0);

        // 期待されるメッセージをセット
        FacesContextMock.EXPECTED_CLIENT_ID = null;
        FacesContextMock.EXPECTED_MESSAGE =
                new FacesMessage(FacesMessage.SEVERITY_INFO,
                                 FacesMessage.SEVERITY_INFO.toString(),
                                 createExpectedMessageString(
                                     Messages.getMessageAsString(CORRESPON_DENIED),
                                     null));

        // 必須のプロパティをセットして実行
        page.setId(expCorrespon.getId());
        page.setWorkflow(workflowList);
        page.setCorrespon(expCorrespon);
        page.setUsersWorkflow(expected);

        String url = verificationModule.deny();

        // Checkerのワークフロー
        Workflow expWorkflow = workflowList.get(0);

        assertEquals("corresponIndex?afterAction=true&sessionSort=1&sessionPageNo=1", url);
        assertEquals("deny", MockCorresponWorkflowService.RET_METHOD);
        assertEquals(expCorrespon.toString(), MockCorresponWorkflowService.RET_CORRESPON.toString());
        assertEquals(expWorkflow.toString(), MockCorresponWorkflowService.RET_WORKFLOW.toString());

        // ダイアログを閉じてコレポンページが初期化されるため全てfalse
        assertFalse(page.isCheck());
        assertFalse(page.isApprove());
        assertFalse(page.isDeny());
        assertEquals(expected, page.getUsersWorkflow());
    }

    /**
     * SystemAdminかつApproverの否認アクションのテスト.
     */
    @Test
    public void testDenySystemAdminApprover() {
        setRequiredFields();

        // テストに必要な値をセット
        MockAbstractPage.RET_SYSTEM_ADMIN = true;
        MockAbstractPage.RET_PROJECT_ADMIN = false;
        MockAbstractPage.RET_GROUP_ADMIN = false;

        Project project = new Project();
        project.setProjectId("PJ1");
        MockAbstractPage.RET_PROJECT = project;

        Correspon expCorrespon = createCorrespon();
        List<Workflow> workflowList = createWorkflowList();
        expCorrespon.setWorkflows(workflowList);
        expCorrespon.setWorkflowStatus(WorkflowStatus.UNDER_CONSIDERATION);

        MockCorresponService.RET_FIND = expCorrespon;

        // checker1がログイン
        MockAbstractPage.RET_USER = APPROVER;
        MockCorresponReadStatusService.RET_CURRENT_USER = APPROVER;
        // checke
        page.getDataModel().setRowIndex(3);

        Workflow expected = workflowList.get(3);

        // 期待されるメッセージをセット
        FacesContextMock.EXPECTED_CLIENT_ID = null;
        FacesContextMock.EXPECTED_MESSAGE =
                new FacesMessage(FacesMessage.SEVERITY_INFO,
                                 FacesMessage.SEVERITY_INFO.toString(),
                                 createExpectedMessageString(
                                     Messages.getMessageAsString(CORRESPON_DENIED),
                                     null));

        // 必須のプロパティをセットして実行
        page.setId(expCorrespon.getId());
        page.setWorkflow(workflowList);
        page.setCorrespon(expCorrespon);
        page.setUsersWorkflow(expected);

        String url = verificationModule.deny();

        // Checkerのワークフロー
        Workflow expWorkflow = workflowList.get(3);

        assertEquals("corresponIndex?afterAction=true&sessionSort=1&sessionPageNo=1", url);
        assertEquals("deny", MockCorresponWorkflowService.RET_METHOD);
        assertEquals(expCorrespon.toString(), MockCorresponWorkflowService.RET_CORRESPON.toString());
        assertEquals(expWorkflow.toString(), MockCorresponWorkflowService.RET_WORKFLOW.toString());

        // ダイアログを閉じてコレポンページが初期化されるため全てfalse
        assertFalse(page.isCheck());
        assertFalse(page.isApprove());
        assertFalse(page.isDeny());
        assertEquals(expected, page.getUsersWorkflow());
    }

    /**
     * ProjectAdminかつCheckerの否認アクションのテスト.
     */
    @Test
    public void testDenyProjectAdminChecker() {
        setRequiredFields();

        // テストに必要な値をセット
        MockAbstractPage.RET_SYSTEM_ADMIN = false;
        MockAbstractPage.RET_PROJECT_ADMIN = true;
        MockAbstractPage.RET_GROUP_ADMIN = false;

        Project project = new Project();
        project.setProjectId("PJ1");
        MockAbstractPage.RET_PROJECT = project;

        Correspon expCorrespon = createCorrespon();
        List<Workflow> workflowList = createWorkflowList();
        expCorrespon.setWorkflows(workflowList);
        expCorrespon.setWorkflowStatus(WorkflowStatus.UNDER_CONSIDERATION);

        MockCorresponService.RET_FIND = expCorrespon;

        // checker1がログイン
        MockAbstractPage.RET_USER = CHECKER_1;
        MockCorresponReadStatusService.RET_CURRENT_USER = CHECKER_1;
        // checke
        page.getDataModel().setRowIndex(0);

        Workflow expected = workflowList.get(0);

        // 期待されるメッセージをセット
        FacesContextMock.EXPECTED_CLIENT_ID = null;
        FacesContextMock.EXPECTED_MESSAGE =
                new FacesMessage(FacesMessage.SEVERITY_INFO,
                                 FacesMessage.SEVERITY_INFO.toString(),
                                 createExpectedMessageString(
                                     Messages.getMessageAsString(CORRESPON_DENIED),
                                     null));

        // 必須のプロパティをセットして実行
        page.setId(expCorrespon.getId());
        page.setWorkflow(workflowList);
        page.setCorrespon(expCorrespon);
        page.setUsersWorkflow(expected);

        String url = verificationModule.deny();

        // Checkerのワークフロー
        Workflow expWorkflow = workflowList.get(0);

        assertEquals("corresponIndex?afterAction=true&sessionSort=1&sessionPageNo=1", url);
        assertEquals("deny", MockCorresponWorkflowService.RET_METHOD);
        assertEquals(expCorrespon.toString(), MockCorresponWorkflowService.RET_CORRESPON.toString());
        assertEquals(expWorkflow.toString(), MockCorresponWorkflowService.RET_WORKFLOW.toString());

        // ダイアログを閉じてコレポンページが初期化されるため全てfalse
        assertFalse(page.isCheck());
        assertFalse(page.isApprove());
        assertFalse(page.isDeny());
        assertEquals(expected, page.getUsersWorkflow());
    }

    /**
     * ProjectAdminかつApproverの否認アクションのテスト.
     */
    @Test
    public void testDenyProjectAdminApprover() {
        setRequiredFields();

        // テストに必要な値をセット
        MockAbstractPage.RET_SYSTEM_ADMIN = false;
        MockAbstractPage.RET_PROJECT_ADMIN = true;
        MockAbstractPage.RET_GROUP_ADMIN = false;

        Project project = new Project();
        project.setProjectId("PJ1");
        MockAbstractPage.RET_PROJECT = project;

        Correspon expCorrespon = createCorrespon();
        List<Workflow> workflowList = createWorkflowList();
        expCorrespon.setWorkflows(workflowList);
        expCorrespon.setWorkflowStatus(WorkflowStatus.UNDER_CONSIDERATION);

        MockCorresponService.RET_FIND = expCorrespon;

        // checker1がログイン
        MockAbstractPage.RET_USER = APPROVER;
        MockCorresponReadStatusService.RET_CURRENT_USER = APPROVER;
        // Approve
        page.getDataModel().setRowIndex(3);

        Workflow expected = workflowList.get(3);

        // 期待されるメッセージをセット
        FacesContextMock.EXPECTED_CLIENT_ID = null;
        FacesContextMock.EXPECTED_MESSAGE =
                new FacesMessage(FacesMessage.SEVERITY_INFO,
                                 FacesMessage.SEVERITY_INFO.toString(),
                                 createExpectedMessageString(
                                     Messages.getMessageAsString(CORRESPON_DENIED),
                                     null));

        // 必須のプロパティをセットして実行
        page.setId(expCorrespon.getId());
        page.setWorkflow(workflowList);
        page.setCorrespon(expCorrespon);
        page.setUsersWorkflow(expected);

        String url = verificationModule.deny();

        // Checkerのワークフロー
        Workflow expWorkflow = workflowList.get(3);

        assertEquals("corresponIndex?afterAction=true&sessionSort=1&sessionPageNo=1", url);
        assertEquals("deny", MockCorresponWorkflowService.RET_METHOD);
        assertEquals(expCorrespon.toString(), MockCorresponWorkflowService.RET_CORRESPON.toString());
        assertEquals(expWorkflow.toString(), MockCorresponWorkflowService.RET_WORKFLOW.toString());

        // ダイアログを閉じてコレポンページが初期化されるため全てfalse
        assertFalse(page.isCheck());
        assertFalse(page.isApprove());
        assertFalse(page.isDeny());
        assertEquals(expected, page.getUsersWorkflow());
    }

    /**
     * DiscipilineAdminかつCheckerの否認アクションのテスト.
     */
    @Test
    public void testDenyDiscipilineAdminChecker() {
        setRequiredFields();

        // テストに必要な値をセット
        MockAbstractPage.RET_SYSTEM_ADMIN = false;
        MockAbstractPage.RET_PROJECT_ADMIN = false;
        MockAbstractPage.RET_GROUP_ADMIN = true;

        Project project = new Project();
        project.setProjectId("PJ1");
        MockAbstractPage.RET_PROJECT = project;

        Correspon expCorrespon = createCorrespon();
        List<Workflow> workflowList = createWorkflowList();
        workflowList.get(0).setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_CHECK);
        expCorrespon.setWorkflows(workflowList);
        expCorrespon.setWorkflowStatus(WorkflowStatus.UNDER_CONSIDERATION);

        MockCorresponService.RET_FIND = expCorrespon;

        // checker1がログイン
        MockAbstractPage.RET_USER = CHECKER_1;
        MockCorresponReadStatusService.RET_CURRENT_USER = CHECKER_1;
        // checke
        page.getDataModel().setRowIndex(0);

        Workflow expected = workflowList.get(0);

        // 期待されるメッセージをセット
        FacesContextMock.EXPECTED_CLIENT_ID = null;
        FacesContextMock.EXPECTED_MESSAGE =
                new FacesMessage(FacesMessage.SEVERITY_INFO,
                                 FacesMessage.SEVERITY_INFO.toString(),
                                 "The correspondence has been denied.");

        // 必須のプロパティをセットして実行
        page.setId(expCorrespon.getId());
        page.setWorkflow(workflowList);
        page.setCorrespon(expCorrespon);
        page.setUsersWorkflow(expected);
        page.setBackPage("corresponSearch");

        String url = verificationModule.deny();

        // Checkerのワークフロー
        Workflow expWorkflow = workflowList.get(0);

        assertEquals("corresponSearch?afterAction=true&sessionSort=1&sessionPageNo=1", url);
        assertEquals("deny", MockCorresponWorkflowService.RET_METHOD);
        assertEquals(expCorrespon.toString(), MockCorresponWorkflowService.RET_CORRESPON.toString());
        assertEquals(expWorkflow.toString(), MockCorresponWorkflowService.RET_WORKFLOW.toString());

        // ダイアログを閉じてコレポンページが初期化されるため全てfalse
        assertFalse(page.isCheck());
        assertFalse(page.isApprove());
        assertFalse(page.isDeny());
        assertEquals(expected, page.getUsersWorkflow());
    }

    /**
     * DiscipilineAdminかつApproverの否認アクションのテスト.
     */
    @Test
    public void testDenyDiscipilineAdminApprover() {
        setRequiredFields();

        // テストに必要な値をセット
        MockAbstractPage.RET_SYSTEM_ADMIN = false;
        MockAbstractPage.RET_PROJECT_ADMIN = false;
        MockAbstractPage.RET_GROUP_ADMIN = true;

        Project project = new Project();
        project.setProjectId("PJ1");
        MockAbstractPage.RET_PROJECT = project;

        Correspon expCorrespon = createCorrespon();
        List<Workflow> workflowList = createWorkflowList();
        workflowList.get(3).setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_APPROVAL);
        expCorrespon.setWorkflows(workflowList);
        expCorrespon.setWorkflowStatus(WorkflowStatus.UNDER_CONSIDERATION);

        MockCorresponService.RET_FIND = expCorrespon;

        // checker1がログイン
        MockAbstractPage.RET_USER = APPROVER;
        MockCorresponReadStatusService.RET_CURRENT_USER = APPROVER;
        // Approve
        page.getDataModel().setRowIndex(3);

        Workflow expected = workflowList.get(3);

        // 期待されるメッセージをセット
        FacesContextMock.EXPECTED_CLIENT_ID = null;
        FacesContextMock.EXPECTED_MESSAGE =
                new FacesMessage(FacesMessage.SEVERITY_INFO,
                                 FacesMessage.SEVERITY_INFO.toString(),
                                 createExpectedMessageString(
                                     Messages.getMessageAsString(CORRESPON_DENIED),
                                     null));

        // 必須のプロパティをセットして実行
        page.setId(expCorrespon.getId());
        page.setWorkflow(workflowList);
        page.setCorrespon(expCorrespon);
        page.setUsersWorkflow(expected);
        page.setBackPage("corresponSearch");

        String url = verificationModule.deny();

        // Checkerのワークフロー
        Workflow expWorkflow = workflowList.get(3);

        assertEquals("corresponSearch?afterAction=true&sessionSort=1&sessionPageNo=1", url);
        assertEquals("deny", MockCorresponWorkflowService.RET_METHOD);
        assertEquals(expCorrespon.toString(), MockCorresponWorkflowService.RET_CORRESPON.toString());
        assertEquals(expWorkflow.toString(), MockCorresponWorkflowService.RET_WORKFLOW.toString());

        // ダイアログを閉じてコレポンページが初期化されるため全てfalse
        assertFalse(page.isCheck());
        assertFalse(page.isApprove());
        assertFalse(page.isDeny());
        assertEquals(expected, page.getUsersWorkflow());
    }

    /**
     * キャンセルアクションのテスト.
     * @throws Exception
     */
    @Test
    public void testCancel() throws Exception {
        setRequiredFields();

        page.setVerificationDisplay(true);

        verificationModule.cancel();

        assertFalse(page.isVerificationDisplay());
    }

    private void setRequiredFields() {
        verificationModule.setCorresponPage(page);
        verificationModule.setServiceActionHandler(page.getHandler());
        verificationModule.setViewHelper(page.getViewHelper());
    }

    /**
     * テスト用コレポン文書の作成
     */
    private Correspon createCorrespon() {
        Correspon c = new Correspon();
        c.setId(1L);
        c.setCorresponNo("YOC:OT:BUILDING-00001");
        CorresponGroup from = new CorresponGroup();
        from.setId(new Long(1L));
        from.setName("YOC:BUILDING");
        c.setFromCorresponGroup(from);
        c.setSubject("Mock");
        CorresponType ct = new CorresponType();
        ct.setId(new Long(1L));
        ct.setName("Request");
        WorkflowPattern wp = new WorkflowPattern();
        wp.setWorkflowCd("001");
        ct.setWorkflowPattern(wp);
        c.setCorresponType(ct);
        c.setWorkflowStatus(WorkflowStatus.REQUEST_FOR_CHECK);
        c.setCreatedAt(new GregorianCalendar(2009, 3, 1, 1, 1, 1).getTime());
        c.setUpdatedAt(new GregorianCalendar(2009, 3, 10, 10, 10, 10).getTime());
        User u = new User();
        u.setEmpNo("00001");
        u.setNameE("Test User");
        c.setCreatedBy(u);
        c.setUpdatedBy(u);
        c.setDeadlineForReply(new GregorianCalendar(2009, 4, 1).getTime());
        c.setCorresponStatus(CorresponStatus.OPEN);

        return c;
    }

    /**
     * テスト用ワークフローリストの作成
     */
    private List<Workflow> createWorkflowList() {
        List<Workflow> list = new ArrayList<Workflow>();

        Workflow wf = new Workflow();
        wf.setId(1L);
        wf.setUser(CHECKER_1);
        wf.setWorkflowProcessStatus(WorkflowProcessStatus.NONE);
        wf.setWorkflowType(WorkflowType.CHECKER);
        list.add(wf);

        wf = new Workflow();
        wf.setId(2L);
        wf.setUser(CHECKER_2);
        wf.setWorkflowProcessStatus(WorkflowProcessStatus.NONE);
        wf.setWorkflowType(WorkflowType.CHECKER);
        list.add(wf);

        wf = new Workflow();
        wf.setId(3L);
        wf.setUser(CHECKER_3);
        wf.setWorkflowProcessStatus(WorkflowProcessStatus.NONE);
        wf.setWorkflowType(WorkflowType.CHECKER);
        list.add(wf);

        wf = new Workflow();
        wf.setId(4L);
        wf.setUser(APPROVER);
        wf.setWorkflowProcessStatus(WorkflowProcessStatus.NONE);
        wf.setWorkflowType(WorkflowType.APPROVER);
        list.add(wf);

        return list;
    }

    public static class MockCorresponService extends MockUp<CorresponServiceImpl> {
        static Correspon RET_FIND;

        @Mock
        public Correspon find(Long id) throws ServiceAbortException {
            System.out.println("MockCorresponService:" + RET_FIND);
            return RET_FIND;
        }
    }

    public static class MockCorresponWorkflowService extends MockUp<CorresponWorkflowServiceImpl> {
        static String RET_METHOD;
        static Correspon RET_CORRESPON;
        static Workflow RET_WORKFLOW;

        @Mock
        public void check(Correspon correspon, Workflow workflow) throws ServiceAbortException {
            RET_METHOD = "check";
            RET_CORRESPON = correspon;
            RET_WORKFLOW = workflow;
        }

        @Mock
        public void approve(Correspon correspon, Workflow workflow) throws ServiceAbortException {
            RET_METHOD = "approve";
            RET_CORRESPON = correspon;
            RET_WORKFLOW = workflow;
        }

        @Mock
        public void deny(Correspon correspon, Workflow workflow) throws ServiceAbortException {
            RET_METHOD = "deny";
            RET_CORRESPON = correspon;
            RET_WORKFLOW = workflow;
        }
    }

    public static class MockAbstractPage extends MockUp<AbstractPage> {
        static User RET_USER;
        static Project RET_PROJECT;
        static boolean RET_SYSTEM_ADMIN;
        static boolean RET_PROJECT_ADMIN;
        static boolean RET_GROUP_ADMIN;

        @Mock
        public User getCurrentUser() {
            return RET_USER;
        }

        @Mock
        public Project getCurrentProject() {
            return RET_PROJECT;
        }

        @Mock
        public boolean isSystemAdmin() {
            return RET_SYSTEM_ADMIN;
        }

        @Mock
        public boolean isProjectAdmin(String projectId) {
            ArgumentValidator.validateNotEmpty(projectId);

            return RET_PROJECT_ADMIN;
        }

        @Mock
        public boolean isAnyGroupAdmin(Correspon c) {
            ArgumentValidator.validateNotNull(c);

            return RET_GROUP_ADMIN;
        }

        @Mock
        public boolean isGroupAdmin(Long corresponGroupId) {
            return RET_GROUP_ADMIN;
        }
    }

    public static class MockCorresponReadStatusService extends MockUp<CorresponReadStatusServiceImpl> {
        static Integer RET_FIND = 2;
        static Long RET_FIND_UNIT = 2L;
        static List<CorresponReadStatus> SET_CREATE = new ArrayList<CorresponReadStatus>();
        static List<CorresponReadStatus> SET_UPDATE = new ArrayList<CorresponReadStatus>();
        static CorresponReadStatus SET_READ_CREATE = new CorresponReadStatus();
        static User RET_CURRENT_USER;
        static ServiceAbortException EX_UPDATE;

        @Mock
        public Long updateReadStatusById(Long id, ReadStatus readStatus)
            throws ServiceAbortException {
            if (EX_UPDATE != null) {
                throw EX_UPDATE;
            }
            SET_READ_CREATE = new CorresponReadStatus();
            SET_READ_CREATE.setCorresponId(id);

            SET_READ_CREATE.setEmpNo(RET_CURRENT_USER.getEmpNo());
            SET_READ_CREATE.setReadStatus(readStatus);
            if (SET_READ_CREATE.getId() == null) {
                SET_READ_CREATE.setCreatedBy(RET_CURRENT_USER);
                SET_READ_CREATE.setUpdatedBy(RET_CURRENT_USER);
                SET_CREATE.add(SET_READ_CREATE);
            } else {
                SET_READ_CREATE.setUpdatedBy(RET_CURRENT_USER);
                SET_UPDATE.add(SET_READ_CREATE);
            }
            return 1L;
        }

        @Mock
        public Integer updateReadStatusByCorresponId(Long id, ReadStatus readStatus)
            throws ServiceAbortException {
            if (EX_UPDATE != null) {
                throw EX_UPDATE;
            }
            SET_READ_CREATE = new CorresponReadStatus();
            SET_READ_CREATE.setCorresponId(id);

            SET_READ_CREATE.setEmpNo(RET_CURRENT_USER.getEmpNo());
            SET_READ_CREATE.setReadStatus(readStatus);
            if (SET_READ_CREATE.getId() == null) {
                SET_READ_CREATE.setCreatedBy(RET_CURRENT_USER);
                SET_READ_CREATE.setUpdatedBy(RET_CURRENT_USER);
                SET_CREATE.add(SET_READ_CREATE);
            } else {
                SET_READ_CREATE.setUpdatedBy(RET_CURRENT_USER);
                SET_UPDATE.add(SET_READ_CREATE);
            }
            return 1;
        }
    }
}
