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
package jp.co.opentone.bsol.linkbinder.view.action.control;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import jp.co.opentone.bsol.framework.core.config.SystemConfig;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.core.util.ArgumentValidator;
import jp.co.opentone.bsol.framework.extension.jsf.FacesContextMock;
import jp.co.opentone.bsol.framework.test.AbstractTestCase;
import jp.co.opentone.bsol.linkbinder.dto.AddressCorresponGroup;
import jp.co.opentone.bsol.linkbinder.dto.AddressUser;
import jp.co.opentone.bsol.linkbinder.dto.Correspon;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroup;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroupUserMapping;
import jp.co.opentone.bsol.linkbinder.dto.CorresponResponseHistory;
import jp.co.opentone.bsol.linkbinder.dto.CorresponType;
import jp.co.opentone.bsol.linkbinder.dto.PersonInCharge;
import jp.co.opentone.bsol.linkbinder.dto.Project;
import jp.co.opentone.bsol.linkbinder.dto.ProjectUser;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.Workflow;
import jp.co.opentone.bsol.linkbinder.dto.WorkflowPattern;
import jp.co.opentone.bsol.linkbinder.dto.code.AddressType;
import jp.co.opentone.bsol.linkbinder.dto.code.AllowApproverToBrowse;
import jp.co.opentone.bsol.linkbinder.dto.code.CorresponStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.ForceToUseWorkflow;
import jp.co.opentone.bsol.linkbinder.dto.code.ReadStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowProcessStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowType;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponGroupCondition;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchUserCondition;
import jp.co.opentone.bsol.linkbinder.service.admin.impl.CorresponGroupServiceImpl;
import jp.co.opentone.bsol.linkbinder.service.admin.impl.UserServiceImpl;
import jp.co.opentone.bsol.linkbinder.service.correspon.impl.CorresponReadStatusServiceImpl;
import jp.co.opentone.bsol.linkbinder.service.correspon.impl.CorresponServiceImpl;
import jp.co.opentone.bsol.linkbinder.service.correspon.impl.CorresponWorkflowServiceImpl;
import jp.co.opentone.bsol.linkbinder.view.AbstractPage;
import jp.co.opentone.bsol.linkbinder.view.correspon.CorresponPage;
import mockit.Mock;
import mockit.MockUp;

/**
 * {@link CorresponPageElementControl}のテストケース.
 * @author opentone
 */
public class CorresponPageElementControlTest extends AbstractTestCase {

    /** no role */
    private static final User NO_ROLE = new User();
    /** preparer */
    private static final User PREPARER = new User();
    /** checker1 */
    private static final User CHECKER_1 = new User();
    /** checker2 */
    private static final User CHECKER_2 = new User();
    /** checker3 */
    private static final User CHECKER_3 = new User();
    /** approver */
    private static final User APPROVER = new User();


    /** Attention */
    private static final Long ID_ATTENTION = 100L;
    private static final User ATTENTION = new User();
    /** Person in Charge */
    private static final User PIC = new User();
    /** Cc */
    private static final Long ID_CC = 102L;
    private static final User CC = new User();
    static {
        PREPARER.setEmpNo("00001");
        CHECKER_1.setEmpNo("USER001");
        CHECKER_2.setEmpNo("USER002");
        CHECKER_3.setEmpNo("USER003");
        APPROVER.setEmpNo("USER004");

        NO_ROLE.setEmpNo("USER999");


        ATTENTION.setEmpNo("USER101");
        PIC.setEmpNo("USER102");
        CC.setEmpNo("USER103");
    }

    /**
     * テスト対象.
     */
    @Resource(name="corresponPage")
    private CorresponPage page;
    private Project currentProject;


    /**
     * テストの前準備.
     */
    @BeforeClass
    public static void testSetUp() {
        FacesContextMock.initialize();
        new MockAbstractPage();
        new MockCorresponService();
        new MockCorresponReadStatusService();
        new MockCorresponWorkflowService();
        new MockCorresponGroupService();
        new MockUserService();
    }

    /**
     * 後始末.
     */
    @AfterClass
    public static void testTeardown() {
        new MockAbstractPage().tearDown();
        new MockCorresponService().tearDown();
        new MockCorresponReadStatusService().tearDown();
        new MockCorresponWorkflowService().tearDown();
        new MockCorresponGroupService().tearDown();
        new MockUserService().tearDown();
        FacesContextMock.tearDown();
    }

    @Before
    public void setUp() {
        FacesContextMock.EXPECTED_MESSAGE = null;
        page.setId(null);
        asNormalUser();

        currentProject = new Project();
        currentProject.setProjectId("PJ1");
        MockAbstractPage.RET_PROJECT = currentProject;
    }

    @After
    public void tearDown() {
        MockCorresponService.RET_FIND = null;
        MockCorresponService.RET_REQUESTFORAPPROVAL = null;

        MockCorresponWorkflowService.RET_METHOD = null;
        MockCorresponWorkflowService.RET_CORRESPON = null;
        MockCorresponWorkflowService.RET_WORKFLOW = null;
        MockAbstractPage.RET_USER = null;
        MockAbstractPage.RET_SYSTEM_ADMIN = false;
        MockAbstractPage.RET_PROJECT_ADMIN = false;
        MockAbstractPage.RET_GROUP_ADMIN = false;
        MockAbstractPage.RET_PROJECT = null;

        MockCorresponReadStatusService.RET_ID = null;

        MockCorresponGroupService.RET_FIND_CORRESPON_GROUP_USER_MAPPINGS = null;
        MockCorresponGroupService.RET_SEARCH = null;
        MockUserService.RET_SEARCH = null;

    }

    private void asSystemAdmin() {
        setAdmin(true, false, false);

    }

    private void asProjectAdmin() {
        setAdmin(false, true, false);
    }

    private void asGroupAdmin() {
        setAdmin(false, false, true);

    }

    private void asNormalUser() {
        setAdmin(false, false, false);
    }

    private void setAdmin(boolean systemAdmin, boolean projectAdmin, boolean groupAdmin) {
        MockAbstractPage.RET_SYSTEM_ADMIN = systemAdmin;
        MockAbstractPage.RET_PROJECT_ADMIN = projectAdmin;
        MockAbstractPage.RET_GROUP_ADMIN = groupAdmin;
    }

    private void asNoRole() {
        setCurrentUser(NO_ROLE);
    }

    private void asAttention() {
        setCurrentUser(ATTENTION);
    }

    private void asPic() {
        setCurrentUser(PIC);
    }

    private void asCc() {
        setCurrentUser(CC);
    }

    private void setCurrentUser(User u) {
        MockAbstractPage.RET_USER = u;
    }

    /**
     * 画面構成要素の制御クラスのテスト. SystemAdminの時.
     */
    @Test
    public void testInitializeSystemAdmin_Draft_Optional() {
        asSystemAdmin();
        asNoRole();

        MockCorresponGroupService.RET_FIND_CORRESPON_GROUP_USER_MAPPINGS = new ArrayList<CorresponGroupUserMapping>();
        MockCorresponGroupService.RET_SEARCH = new ArrayList<CorresponGroup>();
        MockUserService.RET_SEARCH = new ArrayList<ProjectUser>();

        Correspon expCorrespon = createCorrespon();
        List<Workflow> workflowList = createWorkflowList();
        expCorrespon.setWorkflows(workflowList);
        expCorrespon.setWorkflowStatus(WorkflowStatus.DRAFT);

        expCorrespon.getCorresponType().setForceToUseWorkflow(ForceToUseWorkflow.OPTIONAL);

        MockCorresponService.RET_FIND = expCorrespon;
        // 必須のプロパティをセットして実行
        page.setId(expCorrespon.getId());
        page.initialize();

        // 画面構成要素(リンク、ボタン)の確認
        assertTrue(page.getElemControl().isCopyLink());
        assertFalse(page.getElemControl().isForwardLink());
        assertTrue(page.getElemControl().isUpdateLink());
        assertTrue(page.getElemControl().isPrintLink());
        assertTrue(page.getElemControl().isDownloadLink());
        assertTrue(page.getElemControl().isWorkFlowLink());
        assertFalse(page.getElemControl().isVerificationButton());
        assertTrue(page.getElemControl().isIssueButton());
        assertTrue(page.getElemControl().isRequestForApprovalButton());
        assertTrue(page.getElemControl().isDeleteButton());

        //  追加要件・仕様変更により追加
        assertFalse(page.getElemControl().isReviseLink());
        assertFalse(page.getElemControl().isOpenButton());
        assertFalse(page.getElemControl().isCloseButton());
        assertFalse(page.getElemControl().isCancelButton());
        assertTrue(page.getElemControl().isZipLink());
    }

    /**
     * 画面構成要素の制御クラスのテスト. SystemAdminの時.
     */
    @Test
    public void testInitializeSystemAdmin_Draft_Required() {
        asSystemAdmin();
        asNoRole();

        Correspon expCorrespon = createCorrespon();
        List<Workflow> workflowList = createWorkflowList();
        expCorrespon.setWorkflows(workflowList);
        expCorrespon.setWorkflowStatus(WorkflowStatus.DRAFT);

        expCorrespon.getCorresponType().setForceToUseWorkflow(ForceToUseWorkflow.REQUIRED);

        MockCorresponGroupService.RET_FIND_CORRESPON_GROUP_USER_MAPPINGS = new ArrayList<CorresponGroupUserMapping>();
        MockCorresponGroupService.RET_SEARCH = new ArrayList<CorresponGroup>();
        MockUserService.RET_SEARCH = new ArrayList<ProjectUser>();

        MockCorresponService.RET_FIND = expCorrespon;
        // 必須のプロパティをセットして実行
        page.setId(expCorrespon.getId());
        page.initialize();

        // 画面構成要素(リンク、ボタン)の確認
        assertTrue(page.getElemControl().isCopyLink());
        assertFalse(page.getElemControl().isForwardLink());
        assertTrue(page.getElemControl().isUpdateLink());
        assertTrue(page.getElemControl().isPrintLink());
        assertTrue(page.getElemControl().isDownloadLink());
        assertTrue(page.getElemControl().isWorkFlowLink());
        assertFalse(page.getElemControl().isVerificationButton());
        assertFalse(page.getElemControl().isIssueButton());
        assertTrue(page.getElemControl().isRequestForApprovalButton());
        assertTrue(page.getElemControl().isDeleteButton());

        //  追加要件・仕様変更により追加
        assertFalse(page.getElemControl().isReviseLink());
        assertFalse(page.getElemControl().isOpenButton());
        assertFalse(page.getElemControl().isCloseButton());
        assertFalse(page.getElemControl().isCancelButton());
        assertTrue(page.getElemControl().isZipLink());

    }

    /**
     * 画面構成要素の制御クラスのテスト. SystemAdminの時.
     */
    @Test
    public void testInitializeSystemAdmin_Denied() {
        asSystemAdmin();
        asNoRole();

        Correspon expCorrespon = createCorrespon();
        List<Workflow> workflowList = createWorkflowList();
        expCorrespon.setWorkflows(workflowList);
        expCorrespon.setWorkflowStatus(WorkflowStatus.DENIED);

        expCorrespon.getCorresponType().setForceToUseWorkflow(ForceToUseWorkflow.REQUIRED);

        MockCorresponGroupService.RET_FIND_CORRESPON_GROUP_USER_MAPPINGS = new ArrayList<CorresponGroupUserMapping>();
        MockCorresponGroupService.RET_SEARCH = new ArrayList<CorresponGroup>();
        MockUserService.RET_SEARCH = new ArrayList<ProjectUser>();

        MockCorresponService.RET_FIND = expCorrespon;
        // 必須のプロパティをセットして実行
        page.setId(expCorrespon.getId());
        page.initialize();

        // 画面構成要素(リンク、ボタン)の確認
        assertTrue(page.getElemControl().isCopyLink());
        assertFalse(page.getElemControl().isForwardLink());
        assertTrue(page.getElemControl().isUpdateLink());
        assertTrue(page.getElemControl().isPrintLink());
        assertTrue(page.getElemControl().isDownloadLink());
        assertFalse(page.getElemControl().isWorkFlowLink());
        assertFalse(page.getElemControl().isVerificationButton());
        assertFalse(page.getElemControl().isIssueButton());
        assertFalse(page.getElemControl().isRequestForApprovalButton());
        assertFalse(page.getElemControl().isDeleteButton());

        //  追加要件・仕様変更により追加
        assertFalse(page.getElemControl().isReviseLink());
        assertFalse(page.getElemControl().isOpenButton());
        assertFalse(page.getElemControl().isCloseButton());
        assertFalse(page.getElemControl().isCancelButton());
        assertTrue(page.getElemControl().isZipLink());

    }

    /**
     * 画面構成要素の制御クラスのテスト. SystemAdminの時.
     */
    @Test
    public void testInitializeSystemAdmin_Issued() {
        asSystemAdmin();
        asNoRole();

        MockCorresponGroupService.RET_FIND_CORRESPON_GROUP_USER_MAPPINGS = new ArrayList<CorresponGroupUserMapping>();
        MockCorresponGroupService.RET_SEARCH = new ArrayList<CorresponGroup>();
        MockUserService.RET_SEARCH = new ArrayList<ProjectUser>();

        Correspon expCorrespon = createCorrespon();
        List<Workflow> workflowList = createWorkflowList();
        expCorrespon.setWorkflows(workflowList);
        expCorrespon.setWorkflowStatus(WorkflowStatus.ISSUED);

        expCorrespon.getCorresponType().setForceToUseWorkflow(ForceToUseWorkflow.REQUIRED);

        MockCorresponService.RET_FIND = expCorrespon;
        // 必須のプロパティをセットして実行
        page.setId(expCorrespon.getId());
        page.initialize();

        // 画面構成要素(リンク、ボタン)の確認
        assertTrue(page.getElemControl().isCopyLink());
        assertTrue(page.getElemControl().isForwardLink());
        assertTrue(page.getElemControl().isUpdateLink());
        assertTrue(page.getElemControl().isPrintLink());
        assertTrue(page.getElemControl().isDownloadLink());
        assertFalse(page.getElemControl().isWorkFlowLink());
        assertFalse(page.getElemControl().isVerificationButton());
        assertFalse(page.getElemControl().isIssueButton());
        assertFalse(page.getElemControl().isRequestForApprovalButton());
        assertFalse(page.getElemControl().isDeleteButton());
        assertTrue(page.getElemControl().isZipLink());
    }

    /**
     * 画面構成要素の制御クラスのテスト. SystemAdminの時.
     * 文書状態がCanceled.
     */
    @Test
    public void testInitializeSystemAdmin_Canceled() {
        asSystemAdmin();
        asNoRole();

        MockCorresponGroupService.RET_FIND_CORRESPON_GROUP_USER_MAPPINGS = new ArrayList<CorresponGroupUserMapping>();
        MockCorresponGroupService.RET_SEARCH = new ArrayList<CorresponGroup>();
        MockUserService.RET_SEARCH = new ArrayList<ProjectUser>();

        Correspon expCorrespon = createCorrespon();
        expCorrespon.setCorresponStatus(CorresponStatus.CANCELED);
        List<Workflow> workflowList = createWorkflowList();
        expCorrespon.setWorkflows(workflowList);
        expCorrespon.setWorkflowStatus(WorkflowStatus.ISSUED);

        expCorrespon.getCorresponType().setForceToUseWorkflow(ForceToUseWorkflow.REQUIRED);

        MockCorresponService.RET_FIND = expCorrespon;
        // 必須のプロパティをセットして実行
        page.setId(expCorrespon.getId());
        page.initialize();

        // 画面構成要素(リンク、ボタン)の確認
        assertTrue(page.getElemControl().isCopyLink());
        assertTrue(page.getElemControl().isForwardLink());
        assertFalse(page.getElemControl().isUpdateLink());
        assertTrue(page.getElemControl().isPrintLink());
        assertTrue(page.getElemControl().isDownloadLink());
        assertFalse(page.getElemControl().isWorkFlowLink());
        assertFalse(page.getElemControl().isVerificationButton());
        assertFalse(page.getElemControl().isIssueButton());
        assertFalse(page.getElemControl().isRequestForApprovalButton());
        assertFalse(page.getElemControl().isDeleteButton());
        assertTrue(page.getElemControl().isZipLink());
    }

    /**
     * 画面構成要素の制御クラスのテスト. SystemAdminの時.
     */
    @Test
    public void testInitializeSystemAdmin_Request_For_Check_CheckWorkflow() {
        MockAbstractPage.RET_SYSTEM_ADMIN = true;
        MockAbstractPage.RET_PROJECT_ADMIN = false;
        MockAbstractPage.RET_GROUP_ADMIN = false;
        User user = new User();
        user.setEmpNo("99999");
        MockAbstractPage.RET_USER = user;

        Correspon expCorrespon = createCorrespon();
        List<Workflow> workflowList = createWorkflowList();
        workflowList.get(0).setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_CHECK);

        MockCorresponGroupService.RET_FIND_CORRESPON_GROUP_USER_MAPPINGS = new ArrayList<CorresponGroupUserMapping>();
        MockCorresponGroupService.RET_SEARCH = new ArrayList<CorresponGroup>();
        MockUserService.RET_SEARCH = new ArrayList<ProjectUser>();

        expCorrespon.setWorkflows(workflowList);
        expCorrespon.setWorkflowStatus(WorkflowStatus.REQUEST_FOR_CHECK);

        WorkflowPattern wfp = new WorkflowPattern();
        wfp.setWorkflowCd(SystemConfig.getValue("workflow.pattern.1"));
        expCorrespon.getCorresponType().setWorkflowPattern(wfp);

        MockCorresponService.RET_FIND = expCorrespon;
        // 必須のプロパティをセットして実行
        page.setId(expCorrespon.getId());
        page.initialize();

        // 画面構成要素(リンク、ボタン)の確認
        assertTrue(page.getElemControl().isCopyLink());
        assertFalse(page.getElemControl().isForwardLink());
        assertTrue(page.getElemControl().isUpdateLink());
        assertTrue(page.getElemControl().isPrintLink());
        assertTrue(page.getElemControl().isDownloadLink());
        assertTrue(page.getElemControl().isWorkFlowLink());
        assertTrue(page.getElemControl().isVerificationButton());
        assertFalse(page.getElemControl().isIssueButton());
        assertFalse(page.getElemControl().isRequestForApprovalButton());
            assertFalse(page.getElemControl().isDeleteButton());
        assertTrue(page.getElemControl().isZipLink());

    }

    /**
     * 画面構成要素の制御クラスのテスト. SystemAdminの時.承認フローパターンが2の時.
     */
    @Test
    public void testInitializeSystemAdmin_Request_For_Check_CheckWorkflow_Pattern2_Approver_Status_None() {
        MockAbstractPage.RET_SYSTEM_ADMIN = true;
        MockAbstractPage.RET_PROJECT_ADMIN = false;
        MockAbstractPage.RET_GROUP_ADMIN = false;
        User user = new User();
        user.setEmpNo("99999");
        MockAbstractPage.RET_USER = user;

        Correspon expCorrespon = createCorrespon();
        List<Workflow> workflowList = createWorkflowList_Approver_Status_None();

        expCorrespon.setWorkflows(workflowList);
        expCorrespon.setWorkflowStatus(WorkflowStatus.REQUEST_FOR_CHECK);

        WorkflowPattern wfp = new WorkflowPattern();
        wfp.setWorkflowCd(SystemConfig.getValue("workflow.pattern.2"));
        expCorrespon.getCorresponType().setWorkflowPattern(wfp);

        MockCorresponService.RET_FIND = expCorrespon;

        MockCorresponGroupService.RET_FIND_CORRESPON_GROUP_USER_MAPPINGS = new ArrayList<CorresponGroupUserMapping>();
        MockCorresponGroupService.RET_SEARCH = new ArrayList<CorresponGroup>();
        MockUserService.RET_SEARCH = new ArrayList<ProjectUser>();

        // 必須のプロパティをセットして実行
        page.setId(expCorrespon.getId());
        page.initialize();

        // 画面構成要素(リンク、ボタン)の確認
        assertTrue(page.getElemControl().isCopyLink());
        assertFalse(page.getElemControl().isForwardLink());
        assertTrue(page.getElemControl().isUpdateLink());
        assertTrue(page.getElemControl().isPrintLink());
        assertTrue(page.getElemControl().isDownloadLink());
        assertTrue(page.getElemControl().isWorkFlowLink());
        assertTrue(page.getElemControl().isVerificationButton());
        assertFalse(page.getElemControl().isIssueButton());
        assertFalse(page.getElemControl().isRequestForApprovalButton());
        assertFalse(page.getElemControl().isDeleteButton());
        assertTrue(page.getElemControl().isZipLink());

    }

    /**
     * 画面構成要素の制御クラスのテスト. SystemAdminの時.承認フローパターンが2の時.
     */
    @Test
    public void testInitializeSystemAdmin_Request_For_Check_CheckWorkflow_Pattern2_Approver_Status_An_None() {
        MockAbstractPage.RET_SYSTEM_ADMIN = true;
        MockAbstractPage.RET_PROJECT_ADMIN = false;
        MockAbstractPage.RET_GROUP_ADMIN = false;
        User user = new User();
        user.setEmpNo("99999");
        MockAbstractPage.RET_USER = user;

        Correspon expCorrespon = createCorrespon();
        List<Workflow> workflowList = createWorkflowList_Approver_Status_None();
        //ApproverのステータスをNone以外にする
        workflowList.get(workflowList.size() - 1).setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_APPROVAL);

        expCorrespon.setWorkflows(workflowList);
        expCorrespon.setWorkflowStatus(WorkflowStatus.REQUEST_FOR_CHECK);

        WorkflowPattern wfp = new WorkflowPattern();
        wfp.setWorkflowCd(SystemConfig.getValue("workflow.pattern.2"));
        expCorrespon.getCorresponType().setWorkflowPattern(wfp);

        MockCorresponService.RET_FIND = expCorrespon;
        // 必須のプロパティをセットして実行
        page.setId(expCorrespon.getId());

        MockCorresponGroupService.RET_FIND_CORRESPON_GROUP_USER_MAPPINGS = new ArrayList<CorresponGroupUserMapping>();
        MockCorresponGroupService.RET_SEARCH = new ArrayList<CorresponGroup>();
        MockUserService.RET_SEARCH = new ArrayList<ProjectUser>();

        page.initialize();

        // 画面構成要素(リンク、ボタン)の確認
        assertTrue(page.getElemControl().isCopyLink());
        assertFalse(page.getElemControl().isForwardLink());
        assertTrue(page.getElemControl().isUpdateLink());
        assertTrue(page.getElemControl().isPrintLink());
        assertTrue(page.getElemControl().isDownloadLink());
        assertFalse(page.getElemControl().isWorkFlowLink());
        assertTrue(page.getElemControl().isVerificationButton());
        assertFalse(page.getElemControl().isIssueButton());
        assertFalse(page.getElemControl().isRequestForApprovalButton());
        assertFalse(page.getElemControl().isDeleteButton());
        assertTrue(page.getElemControl().isZipLink());

    }

    /**
     * 画面構成要素の制御クラスのテスト. SystemAdminの時.
     */
    @Test
    public void testInitializeSystemAdmin_Under_Consideration_CheckWorkflow() {
        MockAbstractPage.RET_SYSTEM_ADMIN = true;
        MockAbstractPage.RET_PROJECT_ADMIN = false;
        MockAbstractPage.RET_GROUP_ADMIN = false;
        User user = new User();
        user.setEmpNo("99999");
        MockAbstractPage.RET_USER = user;

        Correspon expCorrespon = createCorrespon();
        List<Workflow> workflowList = createWorkflowList();
        workflowList.get(0).setWorkflowProcessStatus(WorkflowProcessStatus.CHECKED);
        workflowList.get(1).setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_CHECK);

        expCorrespon.setWorkflows(workflowList);
        expCorrespon.setWorkflowStatus(WorkflowStatus.UNDER_CONSIDERATION);

        WorkflowPattern wfp = new WorkflowPattern();
        wfp.setWorkflowCd(SystemConfig.getValue("workflow.pattern.1"));
        expCorrespon.getCorresponType().setWorkflowPattern(wfp);

        MockCorresponService.RET_FIND = expCorrespon;

        MockCorresponGroupService.RET_FIND_CORRESPON_GROUP_USER_MAPPINGS = new ArrayList<CorresponGroupUserMapping>();
        MockCorresponGroupService.RET_SEARCH = new ArrayList<CorresponGroup>();
        MockUserService.RET_SEARCH = new ArrayList<ProjectUser>();

        // 必須のプロパティをセットして実行
        page.setId(expCorrespon.getId());
        page.initialize();

        // 画面構成要素(リンク、ボタン)の確認
        assertTrue(page.getElemControl().isCopyLink());
        assertFalse(page.getElemControl().isForwardLink());
        assertTrue(page.getElemControl().isUpdateLink());
        assertTrue(page.getElemControl().isPrintLink());
        assertTrue(page.getElemControl().isDownloadLink());
        assertTrue(page.getElemControl().isWorkFlowLink());
        assertTrue(page.getElemControl().isVerificationButton());
        assertFalse(page.getElemControl().isIssueButton());
        assertFalse(page.getElemControl().isRequestForApprovalButton());
        assertFalse(page.getElemControl().isDeleteButton());
        assertTrue(page.getElemControl().isZipLink());

    }

    /**
     * 画面構成要素の制御クラスのテスト. SystemAdminの時.承認フローパターンが2.
     */
    @Test
    public void testInitializeSystemAdmin_Under_Consideration_CheckWorkflow_Pattern2_Approver_Status_None() {
        MockAbstractPage.RET_SYSTEM_ADMIN = true;
        MockAbstractPage.RET_PROJECT_ADMIN = false;
        MockAbstractPage.RET_GROUP_ADMIN = false;
        User user = new User();
        user.setEmpNo("99999");
        MockAbstractPage.RET_USER = user;

        Correspon expCorrespon = createCorrespon();
        List<Workflow> workflowList = createWorkflowList_Approver_Status_None();

        expCorrespon.setWorkflows(workflowList);
        expCorrespon.setWorkflowStatus(WorkflowStatus.UNDER_CONSIDERATION);

        WorkflowPattern wfp = new WorkflowPattern();
        wfp.setWorkflowCd(SystemConfig.getValue("workflow.pattern.2"));
        expCorrespon.getCorresponType().setWorkflowPattern(wfp);

        MockCorresponService.RET_FIND = expCorrespon;

        MockCorresponGroupService.RET_FIND_CORRESPON_GROUP_USER_MAPPINGS = new ArrayList<CorresponGroupUserMapping>();
        MockCorresponGroupService.RET_SEARCH = new ArrayList<CorresponGroup>();
        MockUserService.RET_SEARCH = new ArrayList<ProjectUser>();

        // 必須のプロパティをセットして実行
        page.setId(expCorrespon.getId());
        page.initialize();

        // 画面構成要素(リンク、ボタン)の確認
        assertTrue(page.getElemControl().isCopyLink());
        assertFalse(page.getElemControl().isForwardLink());
        assertTrue(page.getElemControl().isUpdateLink());
        assertTrue(page.getElemControl().isPrintLink());
        assertTrue(page.getElemControl().isDownloadLink());
        assertTrue(page.getElemControl().isWorkFlowLink());
        assertTrue(page.getElemControl().isVerificationButton());
        assertFalse(page.getElemControl().isIssueButton());
        assertFalse(page.getElemControl().isRequestForApprovalButton());
        assertFalse(page.getElemControl().isDeleteButton());
        assertTrue(page.getElemControl().isZipLink());

    }

    /**
     * 画面構成要素の制御クラスのテスト. SystemAdminの時.承認フローパターンが2.
     */
    @Test
    public void testInitializeSystemAdmin_Under_Consideration_CheckWorkflow_Pattern2_Approver_Status_An_None() {
        MockAbstractPage.RET_SYSTEM_ADMIN = true;
        MockAbstractPage.RET_PROJECT_ADMIN = false;
        MockAbstractPage.RET_GROUP_ADMIN = false;
        User user = new User();
        user.setEmpNo("99999");
        MockAbstractPage.RET_USER = user;

        Correspon expCorrespon = createCorrespon();
        List<Workflow> workflowList = createWorkflowList_Approver_Status_None();
        // ApproverのステータスがNone以外
        workflowList.get(workflowList.size() - 1).setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_APPROVAL);

        expCorrespon.setWorkflows(workflowList);
        expCorrespon.setWorkflowStatus(WorkflowStatus.UNDER_CONSIDERATION);

        WorkflowPattern wfp = new WorkflowPattern();
        wfp.setWorkflowCd(SystemConfig.getValue("workflow.pattern.2"));
        expCorrespon.getCorresponType().setWorkflowPattern(wfp);

        MockCorresponService.RET_FIND = expCorrespon;

        MockCorresponGroupService.RET_FIND_CORRESPON_GROUP_USER_MAPPINGS = new ArrayList<CorresponGroupUserMapping>();
        MockCorresponGroupService.RET_SEARCH = new ArrayList<CorresponGroup>();
        MockUserService.RET_SEARCH = new ArrayList<ProjectUser>();

        // 必須のプロパティをセットして実行
        page.setId(expCorrespon.getId());
        page.initialize();

        // 画面構成要素(リンク、ボタン)の確認
        assertTrue(page.getElemControl().isCopyLink());
        assertFalse(page.getElemControl().isForwardLink());
        assertTrue(page.getElemControl().isUpdateLink());
        assertTrue(page.getElemControl().isPrintLink());
        assertTrue(page.getElemControl().isDownloadLink());
        assertFalse(page.getElemControl().isWorkFlowLink());
        assertTrue(page.getElemControl().isVerificationButton());
        assertFalse(page.getElemControl().isIssueButton());
        assertFalse(page.getElemControl().isRequestForApprovalButton());
        assertFalse(page.getElemControl().isDeleteButton());
        assertTrue(page.getElemControl().isZipLink());

    }


    /**
     * 画面構成要素の制御クラスのテスト. ProjectAdminの時.
     */
    @Test
    public void testInitializeProjectAdmin_Draft() {
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
        expCorrespon.setWorkflowStatus(WorkflowStatus.DRAFT);

        MockCorresponService.RET_FIND = expCorrespon;

        MockCorresponGroupService.RET_FIND_CORRESPON_GROUP_USER_MAPPINGS = new ArrayList<CorresponGroupUserMapping>();
        MockCorresponGroupService.RET_SEARCH = new ArrayList<CorresponGroup>();
        MockUserService.RET_SEARCH = new ArrayList<ProjectUser>();

        // 必須のプロパティをセットして実行
        page.setId(expCorrespon.getId());
        page.initialize();

        // 画面構成要素(リンク、ボタン)の確認
        assertTrue(page.getElemControl().isCopyLink());
        assertFalse(page.getElemControl().isForwardLink());
        assertFalse(page.getElemControl().isUpdateLink());
        assertFalse(page.getElemControl().isPrintLink());
        assertFalse(page.getElemControl().isDownloadLink());
        assertFalse(page.getElemControl().isWorkFlowLink());
        assertFalse(page.getElemControl().isVerificationButton());
        assertFalse(page.getElemControl().isIssueButton());
        assertTrue(page.getElemControl().isRequestForApprovalButton());
        assertFalse(page.getElemControl().isDeleteButton());
        assertTrue(page.getElemControl().isZipLink());
    }

    /**
     * 画面構成要素の制御クラスのテスト. ProjectAdminの時.
     */
    @Test
    public void testInitializeProjectAdmin_Issued() {
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

        MockCorresponGroupService.RET_FIND_CORRESPON_GROUP_USER_MAPPINGS = new ArrayList<CorresponGroupUserMapping>();
        MockCorresponGroupService.RET_SEARCH = new ArrayList<CorresponGroup>();
        MockUserService.RET_SEARCH = new ArrayList<ProjectUser>();

        // 必須のプロパティをセットして実行
        page.setId(expCorrespon.getId());
        page.initialize();

        // 画面構成要素(リンク、ボタン)の確認
        assertTrue(page.getElemControl().isCopyLink());
        assertTrue(page.getElemControl().isForwardLink());
        assertTrue(page.getElemControl().isUpdateLink());
        assertTrue(page.getElemControl().isPrintLink());
        assertTrue(page.getElemControl().isDownloadLink());
        assertFalse(page.getElemControl().isWorkFlowLink());
        assertFalse(page.getElemControl().isVerificationButton());
        assertFalse(page.getElemControl().isIssueButton());
        assertFalse(page.getElemControl().isRequestForApprovalButton());
        assertFalse(page.getElemControl().isDeleteButton());
        assertTrue(page.getElemControl().isZipLink());
    }

    /**
     * 画面構成要素の制御クラスのテスト. ProjectAdminの時.
     * 文書状態がCanceled.
     */
    @Test
    public void testInitializeProjectAdmin_Canceled() {
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
        expCorrespon.setCorresponStatus(CorresponStatus.CANCELED);
        List<Workflow> workflowList = createWorkflowList();

        expCorrespon.setWorkflows(workflowList);
        expCorrespon.setWorkflowStatus(WorkflowStatus.ISSUED);

        MockCorresponService.RET_FIND = expCorrespon;

        MockCorresponGroupService.RET_FIND_CORRESPON_GROUP_USER_MAPPINGS = new ArrayList<CorresponGroupUserMapping>();
        MockCorresponGroupService.RET_SEARCH = new ArrayList<CorresponGroup>();
        MockUserService.RET_SEARCH = new ArrayList<ProjectUser>();

        // 必須のプロパティをセットして実行
        page.setId(expCorrespon.getId());
        page.initialize();

        // 画面構成要素(リンク、ボタン)の確認
        assertTrue(page.getElemControl().isCopyLink());
        assertTrue(page.getElemControl().isForwardLink());
        assertFalse(page.getElemControl().isUpdateLink());
        assertTrue(page.getElemControl().isPrintLink());
        assertTrue(page.getElemControl().isDownloadLink());
        assertFalse(page.getElemControl().isWorkFlowLink());
        assertFalse(page.getElemControl().isVerificationButton());
        assertFalse(page.getElemControl().isIssueButton());
        assertFalse(page.getElemControl().isRequestForApprovalButton());
        assertFalse(page.getElemControl().isDeleteButton());
        assertTrue(page.getElemControl().isZipLink());
    }

    /**
    /**
     * 画面構成要素の制御クラスのテスト. ProjectAdminでPreparerの時.
     */
    @Test
    public void testInitializeProjectAdminPreparer_Draft() {
        MockAbstractPage.RET_SYSTEM_ADMIN = false;
        MockAbstractPage.RET_PROJECT_ADMIN = true;
        MockAbstractPage.RET_GROUP_ADMIN = false;
        User user = new User();
        user.setEmpNo("00001");
        MockAbstractPage.RET_USER = user;

        Project project = new Project();
        project.setProjectId("PJ1");
        MockAbstractPage.RET_PROJECT = project;

        Correspon expCorrespon = createCorrespon();
        List<Workflow> workflowList = createWorkflowList();

        expCorrespon.setWorkflows(workflowList);
        expCorrespon.setWorkflowStatus(WorkflowStatus.DRAFT);

        expCorrespon.getCorresponType().setForceToUseWorkflow(ForceToUseWorkflow.REQUIRED);
        MockCorresponService.RET_FIND = expCorrespon;

        MockCorresponGroupService.RET_FIND_CORRESPON_GROUP_USER_MAPPINGS = new ArrayList<CorresponGroupUserMapping>();
        MockCorresponGroupService.RET_SEARCH = new ArrayList<CorresponGroup>();
        MockUserService.RET_SEARCH = new ArrayList<ProjectUser>();

        // 必須のプロパティをセットして実行
        page.setId(expCorrespon.getId());
        page.initialize();

        // 画面構成要素(リンク、ボタン)の確認
        assertTrue(page.getElemControl().isCopyLink());
        assertFalse(page.getElemControl().isForwardLink());
        assertTrue(page.getElemControl().isUpdateLink());
        assertTrue(page.getElemControl().isPrintLink());
        assertTrue(page.getElemControl().isDownloadLink());
        assertTrue(page.getElemControl().isWorkFlowLink());
        assertFalse(page.getElemControl().isVerificationButton());
        assertFalse(page.getElemControl().isIssueButton());
        assertTrue(page.getElemControl().isRequestForApprovalButton());
        assertTrue(page.getElemControl().isDeleteButton());
        assertTrue(page.getElemControl().isZipLink());

    }

    /**
     * 画面構成要素の制御クラスのテスト. ProjectAdminでPreparerの時.
     */
    @Test
    public void testInitializeProjectAdminPreparer_Draft_Optional() {
        MockAbstractPage.RET_SYSTEM_ADMIN = false;
        MockAbstractPage.RET_PROJECT_ADMIN = true;
        MockAbstractPage.RET_GROUP_ADMIN = false;
        User user = new User();
        user.setEmpNo("00001");
        MockAbstractPage.RET_USER = user;

        Project project = new Project();
        project.setProjectId("PJ1");
        MockAbstractPage.RET_PROJECT = project;

        Correspon expCorrespon = createCorrespon();
        List<Workflow> workflowList = createWorkflowList();

        expCorrespon.setWorkflows(workflowList);
        expCorrespon.setWorkflowStatus(WorkflowStatus.DRAFT);

        expCorrespon.getCorresponType().setForceToUseWorkflow(ForceToUseWorkflow.OPTIONAL);

        MockCorresponService.RET_FIND = expCorrespon;

        MockCorresponGroupService.RET_FIND_CORRESPON_GROUP_USER_MAPPINGS = new ArrayList<CorresponGroupUserMapping>();
        MockCorresponGroupService.RET_SEARCH = new ArrayList<CorresponGroup>();
        MockUserService.RET_SEARCH = new ArrayList<ProjectUser>();

        // 必須のプロパティをセットして実行
        page.setId(expCorrespon.getId());
        page.initialize();

        // 画面構成要素(リンク、ボタン)の確認
        assertTrue(page.getElemControl().isCopyLink());
        assertFalse(page.getElemControl().isForwardLink());
        assertTrue(page.getElemControl().isUpdateLink());
        assertTrue(page.getElemControl().isPrintLink());
        assertTrue(page.getElemControl().isDownloadLink());
        assertTrue(page.getElemControl().isWorkFlowLink());
        assertFalse(page.getElemControl().isVerificationButton());
        assertTrue(page.getElemControl().isIssueButton());
        assertTrue(page.getElemControl().isRequestForApprovalButton());
        assertTrue(page.getElemControl().isDeleteButton());
        assertTrue(page.getElemControl().isZipLink());

    }

    /**
     * 画面構成要素の制御クラスのテスト. ProjectAdminの時.
     */
    @Test
    public void testInitializeProjectAdmin_Request_For_Check_CheckWorkflow() {
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
        List<Workflow> workflowList = createWorkflowList_Case1();
        workflowList.get(0).setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_CHECK);

        expCorrespon.setWorkflows(workflowList);
        expCorrespon.setWorkflowStatus(WorkflowStatus.REQUEST_FOR_CHECK);

        WorkflowPattern workflowPattern = new WorkflowPattern();
        workflowPattern.setWorkflowCd(SystemConfig.getValue("workflow.pattern.2"));
        expCorrespon.getCorresponType().setWorkflowPattern(workflowPattern);

        MockCorresponService.RET_FIND = expCorrespon;

        MockCorresponGroupService.RET_FIND_CORRESPON_GROUP_USER_MAPPINGS = new ArrayList<CorresponGroupUserMapping>();
        MockCorresponGroupService.RET_SEARCH = new ArrayList<CorresponGroup>();
        MockUserService.RET_SEARCH = new ArrayList<ProjectUser>();

        // 必須のプロパティをセットして実行
        page.setId(expCorrespon.getId());
        page.initialize();

        // 画面構成要素(リンク、ボタン)の確認
        assertTrue(page.getElemControl().isCopyLink());
        assertFalse(page.getElemControl().isForwardLink());
        assertTrue(page.getElemControl().isUpdateLink());
        assertTrue(page.getElemControl().isPrintLink());
        assertTrue(page.getElemControl().isDownloadLink());
        assertTrue(page.getElemControl().isWorkFlowLink());
        assertTrue(page.getElemControl().isVerificationButton());
        assertFalse (page.getElemControl().isIssueButton());
        assertFalse(page.getElemControl().isRequestForApprovalButton());
        assertFalse(page.getElemControl().isDeleteButton());
        assertTrue(page.getElemControl().isZipLink());


        // Project Admin兼Checker、承認フローパターン1の場合
        workflowPattern.setWorkflowCd(SystemConfig.getValue("workflow.pattern.1"));
        user.setEmpNo(CHECKER_2.getEmpNo());
        workflowList.get(1).setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_CHECK);
        page.initialize();

        // 画面構成要素(リンク、ボタン)の確認
        assertTrue(page.getElemControl().isCopyLink());
        assertFalse(page.getElemControl().isForwardLink());
        assertTrue(page.getElemControl().isUpdateLink());
        assertTrue(page.getElemControl().isPrintLink());
        assertTrue(page.getElemControl().isDownloadLink());
        assertTrue(page.getElemControl().isZipLink());

        // 承認フローパターン1なので後続のChecker/Approvaerが変更可能
        assertTrue(page.getElemControl().isWorkFlowLink());

        assertTrue(page.getElemControl().isVerificationButton());
        assertFalse (page.getElemControl().isIssueButton());
        assertFalse(page.getElemControl().isRequestForApprovalButton());
        assertFalse(page.getElemControl().isDeleteButton());

        // Project Admin兼Checker、承認フローパターン2の場合
        workflowPattern.setWorkflowCd(SystemConfig.getValue("workflow.pattern.2"));
        user.setEmpNo(CHECKER_2.getEmpNo());
        workflowList.get(1).setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_CHECK);
        page.initialize();

        // 画面構成要素(リンク、ボタン)の確認
        assertTrue(page.getElemControl().isCopyLink());
        assertFalse(page.getElemControl().isForwardLink());
        assertTrue(page.getElemControl().isUpdateLink());
        assertTrue(page.getElemControl().isPrintLink());
        assertTrue(page.getElemControl().isDownloadLink());
        assertTrue(page.getElemControl().isZipLink());

        // 承認フローパターン2なので後続のApprovaerが変更可能
        assertTrue(page.getElemControl().isWorkFlowLink());

        assertTrue(page.getElemControl().isVerificationButton());
        assertFalse (page.getElemControl().isIssueButton());
        assertFalse(page.getElemControl().isRequestForApprovalButton());
        assertFalse(page.getElemControl().isDeleteButton());

        // Project Admin兼Checker、承認フローパターン3の場合
        workflowPattern.setWorkflowCd(SystemConfig.getValue("workflow.pattern.3"));
        user.setEmpNo(CHECKER_2.getEmpNo());
        workflowList.get(1).setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_CHECK);
        page.initialize();

        // 画面構成要素(リンク、ボタン)の確認
        assertTrue(page.getElemControl().isCopyLink());
        assertFalse(page.getElemControl().isForwardLink());
        assertTrue(page.getElemControl().isUpdateLink());
        assertTrue(page.getElemControl().isPrintLink());
        assertTrue(page.getElemControl().isDownloadLink());
        assertTrue(page.getElemControl().isZipLink());

        // 承認フローパターン3なのでChecker/Approverは変更できない
        assertFalse(page.getElemControl().isWorkFlowLink());

        assertTrue(page.getElemControl().isVerificationButton());
        assertFalse (page.getElemControl().isIssueButton());
        assertFalse(page.getElemControl().isRequestForApprovalButton());
        assertFalse(page.getElemControl().isDeleteButton());
    }

    /**
     * 画面構成要素の制御クラスのテスト. ProjectAdminの時.
     */
    @Test
    public void testInitializeProjectAdmin_Request_For_Check_CheckWorkflow_Approver_Process_Status_None() {
        MockAbstractPage.RET_SYSTEM_ADMIN = false;
        MockAbstractPage.RET_PROJECT_ADMIN = true;
        MockAbstractPage.RET_GROUP_ADMIN = false;
        User user = new User();
        user.setEmpNo("ZZA02");
        MockAbstractPage.RET_USER = user;

        Project project = new Project();
        project.setProjectId("PJ1");
        MockAbstractPage.RET_PROJECT = project;

        Correspon expCorrespon = createCorrespon();
        List<Workflow> workflowList = createWorkflowList_Approver_Status_None();

        expCorrespon.setWorkflows(workflowList);
        expCorrespon.setWorkflowStatus(WorkflowStatus.REQUEST_FOR_CHECK);

        WorkflowPattern workflowPattern = new WorkflowPattern();
        workflowPattern.setWorkflowCd(SystemConfig.getValue("workflow.pattern.2"));
        expCorrespon.getCorresponType().setWorkflowPattern(workflowPattern);

        MockCorresponService.RET_FIND = expCorrespon;

        MockCorresponGroupService.RET_FIND_CORRESPON_GROUP_USER_MAPPINGS = new ArrayList<CorresponGroupUserMapping>();
        MockCorresponGroupService.RET_SEARCH = new ArrayList<CorresponGroup>();
        MockUserService.RET_SEARCH = new ArrayList<ProjectUser>();

        // 必須のプロパティをセットして実行
        page.setId(expCorrespon.getId());
        page.initialize();

        // 画面構成要素(リンク、ボタン)の確認
        assertTrue(page.getElemControl().isCopyLink());
        assertFalse(page.getElemControl().isForwardLink());
        assertTrue(page.getElemControl().isUpdateLink());
        assertTrue(page.getElemControl().isPrintLink());
        assertTrue(page.getElemControl().isDownloadLink());
        assertTrue(page.getElemControl().isWorkFlowLink());
        assertTrue(page.getElemControl().isVerificationButton());
        assertFalse (page.getElemControl().isIssueButton());
        assertFalse(page.getElemControl().isRequestForApprovalButton());
        assertFalse(page.getElemControl().isDeleteButton());
        assertTrue(page.getElemControl().isZipLink());

    }

    /**
     * 画面構成要素の制御クラスのテスト. ProjectAdminの時.
     */
    @Test
    public void testInitializeProjectAdmin_Is_Approver_Request_For_Check_CheckWorkflow() {
        MockAbstractPage.RET_SYSTEM_ADMIN = false;
        MockAbstractPage.RET_PROJECT_ADMIN = true;
        MockAbstractPage.RET_GROUP_ADMIN = false;
        User user = new User();
        user.setEmpNo("USER004");
        MockAbstractPage.RET_USER = user;

        Project project = new Project();
        project.setProjectId("PJ1");
        MockAbstractPage.RET_PROJECT = project;

        Correspon expCorrespon = createCorrespon();
        List<Workflow> workflowList = createWorkflowList_Approver_Status_None();

        expCorrespon.setWorkflows(workflowList);
        expCorrespon.setWorkflowStatus(WorkflowStatus.REQUEST_FOR_CHECK);

        WorkflowPattern workflowPattern = new WorkflowPattern();
        workflowPattern.setWorkflowCd(SystemConfig.getValue("workflow.pattern.2"));
        expCorrespon.getCorresponType().setWorkflowPattern(workflowPattern);

        MockCorresponService.RET_FIND = expCorrespon;

        MockCorresponGroupService.RET_FIND_CORRESPON_GROUP_USER_MAPPINGS = new ArrayList<CorresponGroupUserMapping>();
        MockCorresponGroupService.RET_SEARCH = new ArrayList<CorresponGroup>();
        MockUserService.RET_SEARCH = new ArrayList<ProjectUser>();
        // ---------------------------------------------------------

        // 必須のプロパティをセットして実行
        page.setId(expCorrespon.getId());
        page.initialize();

        // 画面構成要素(リンク、ボタン)の確認
        assertTrue(page.getElemControl().isCopyLink());
        assertFalse(page.getElemControl().isForwardLink());
        assertTrue(page.getElemControl().isUpdateLink());
        assertTrue(page.getElemControl().isPrintLink());
        assertTrue(page.getElemControl().isDownloadLink());
        assertTrue(page.getElemControl().isWorkFlowLink());
        assertTrue(page.getElemControl().isVerificationButton());
        assertFalse (page.getElemControl().isIssueButton());
        assertFalse(page.getElemControl().isRequestForApprovalButton());
        assertFalse(page.getElemControl().isDeleteButton());
        assertTrue(page.getElemControl().isZipLink());

    }

    /**
     * 画面構成要素の制御クラスのテスト. ProjectAdminの時.
     */
    @Test
    public void testInitializeProjectAdmin_Request_For_Check_CheckWorkflow1() {
        asProjectAdmin();
        asNoRole();

        User user = new User();
        user.setEmpNo("USER002");
        MockAbstractPage.RET_USER = user;

        Project project = new Project();
        project.setProjectId("PJ1");
        MockAbstractPage.RET_PROJECT = project;

        Correspon expCorrespon = createCorrespon();
        List<Workflow> workflowList = createWorkflowList_Case2();

        expCorrespon.setWorkflows(workflowList);
        expCorrespon.setWorkflowStatus(WorkflowStatus.REQUEST_FOR_CHECK);

        WorkflowPattern workflowPattern = new WorkflowPattern();
        workflowPattern.setWorkflowCd(SystemConfig.getValue("workflow.pattern.1"));
        expCorrespon.getCorresponType().setWorkflowPattern(workflowPattern);

        MockCorresponService.RET_FIND = expCorrespon;

        MockCorresponGroupService.RET_FIND_CORRESPON_GROUP_USER_MAPPINGS = new ArrayList<CorresponGroupUserMapping>();
        MockCorresponGroupService.RET_SEARCH = new ArrayList<CorresponGroup>();
        MockUserService.RET_SEARCH = new ArrayList<ProjectUser>();

        // 必須のプロパティをセットして実行
        page.setId(expCorrespon.getId());
        page.initialize();

        // 画面構成要素(リンク、ボタン)の確認
        assertTrue(page.getElemControl().isCopyLink());
        assertFalse(page.getElemControl().isForwardLink());
        assertTrue(page.getElemControl().isUpdateLink());
        assertTrue(page.getElemControl().isPrintLink());
        assertTrue(page.getElemControl().isDownloadLink());
        assertTrue(page.getElemControl().isWorkFlowLink());
        assertTrue(page.getElemControl().isVerificationButton());
        assertFalse (page.getElemControl().isIssueButton());
        assertFalse(page.getElemControl().isRequestForApprovalButton());
        assertFalse(page.getElemControl().isDeleteButton());
        assertTrue(page.getElemControl().isZipLink());

    }

    /**
     * 画面構成要素の制御クラスのテスト. ProjectAdminの時.
     */
    @Test
    public void testInitializeProjectAdmin_Request_For_Approval() {
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
        expCorrespon.setWorkflowStatus(WorkflowStatus.REQUEST_FOR_APPROVAL);

        MockCorresponService.RET_FIND = expCorrespon;

        MockCorresponGroupService.RET_FIND_CORRESPON_GROUP_USER_MAPPINGS = new ArrayList<CorresponGroupUserMapping>();
        MockCorresponGroupService.RET_SEARCH = new ArrayList<CorresponGroup>();
        MockUserService.RET_SEARCH = new ArrayList<ProjectUser>();

        // 必須のプロパティをセットして実行
        page.setId(expCorrespon.getId());
        page.initialize();

        // 画面構成要素(リンク、ボタン)の確認
        assertTrue(page.getElemControl().isCopyLink());
        assertFalse(page.getElemControl().isForwardLink());
        assertTrue(page.getElemControl().isUpdateLink());
        assertTrue(page.getElemControl().isPrintLink());
        assertTrue(page.getElemControl().isDownloadLink());
        assertFalse(page.getElemControl().isWorkFlowLink());
        assertTrue(page.getElemControl().isVerificationButton());
        assertFalse(page.getElemControl().isIssueButton());
        assertFalse(page.getElemControl().isRequestForApprovalButton());
        assertFalse(page.getElemControl().isDeleteButton());
        assertTrue(page.getElemControl().isZipLink());

    }

    /**
     * 画面構成要素の制御クラスのテスト. GroupAdminの時.
     */
    @Test
    public void testInitializeGroupAdmin_Denied() {
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
        expCorrespon.setWorkflowStatus(WorkflowStatus.DENIED);

        MockCorresponService.RET_FIND = expCorrespon;

        MockCorresponGroupService.RET_FIND_CORRESPON_GROUP_USER_MAPPINGS = new ArrayList<CorresponGroupUserMapping>();
        MockCorresponGroupService.RET_SEARCH = new ArrayList<CorresponGroup>();
        MockUserService.RET_SEARCH = new ArrayList<ProjectUser>();

        // 必須のプロパティをセットして実行
        page.setId(expCorrespon.getId());
        page.initialize();

        // 画面構成要素(リンク、ボタン)の確認
        assertTrue(page.getElemControl().isCopyLink());
        assertFalse(page.getElemControl().isForwardLink());
        assertFalse(page.getElemControl().isUpdateLink());
        assertTrue(page.getElemControl().isPrintLink());
        assertTrue(page.getElemControl().isDownloadLink());
        assertFalse(page.getElemControl().isWorkFlowLink());
        assertFalse(page.getElemControl().isVerificationButton());
        assertFalse(page.getElemControl().isIssueButton());
        assertFalse(page.getElemControl().isRequestForApprovalButton());
        assertFalse(page.getElemControl().isDeleteButton());
        assertTrue(page.getElemControl().isZipLink());

    }

    /**
     * 画面構成要素の制御クラスのテスト. ProjectAdminの時.
     */
    @Test
    public void testInitializeGroupAdmin_Request_For_Check_CheckWorkflow() {
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
        List<Workflow> workflowList = createWorkflowList_Case1();
        workflowList.get(0).setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_CHECK);

        expCorrespon.setWorkflows(workflowList);
        expCorrespon.setWorkflowStatus(WorkflowStatus.REQUEST_FOR_CHECK);

        WorkflowPattern workflowPattern = new WorkflowPattern();
        workflowPattern.setWorkflowCd(SystemConfig.getValue("workflow.pattern.2"));
        expCorrespon.getCorresponType().setWorkflowPattern(workflowPattern);

        MockCorresponService.RET_FIND = expCorrespon;

        MockCorresponGroupService.RET_FIND_CORRESPON_GROUP_USER_MAPPINGS = new ArrayList<CorresponGroupUserMapping>();
        MockCorresponGroupService.RET_SEARCH = new ArrayList<CorresponGroup>();
        MockUserService.RET_SEARCH = new ArrayList<ProjectUser>();

        // 必須のプロパティをセットして実行
        page.setId(expCorrespon.getId());
        page.initialize();

        // 画面構成要素(リンク、ボタン)の確認
        assertTrue(page.getElemControl().isCopyLink());
        assertFalse(page.getElemControl().isForwardLink());
        assertTrue(page.getElemControl().isUpdateLink());
        assertTrue(page.getElemControl().isPrintLink());
        assertTrue(page.getElemControl().isDownloadLink());
        assertTrue(page.getElemControl().isWorkFlowLink());
        assertTrue(page.getElemControl().isVerificationButton());
        assertFalse (page.getElemControl().isIssueButton());
        assertFalse(page.getElemControl().isRequestForApprovalButton());
        assertFalse(page.getElemControl().isDeleteButton());
        assertTrue(page.getElemControl().isZipLink());


        // Group Admin兼Checker、承認フローパターン1の場合
        workflowPattern.setWorkflowCd(SystemConfig.getValue("workflow.pattern.1"));
        user.setEmpNo(CHECKER_2.getEmpNo());
        workflowList.get(1).setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_CHECK);
        page.initialize();

        // 画面構成要素(リンク、ボタン)の確認
        assertTrue(page.getElemControl().isCopyLink());
        assertFalse(page.getElemControl().isForwardLink());
        assertTrue(page.getElemControl().isUpdateLink());
        assertTrue(page.getElemControl().isPrintLink());
        assertTrue(page.getElemControl().isDownloadLink());
        assertTrue(page.getElemControl().isZipLink());

        // 承認フローパターン1なので後続のChecker/Approvaerが変更可能
        assertTrue(page.getElemControl().isWorkFlowLink());

        assertTrue(page.getElemControl().isVerificationButton());
        assertFalse (page.getElemControl().isIssueButton());
        assertFalse(page.getElemControl().isRequestForApprovalButton());
        assertFalse(page.getElemControl().isDeleteButton());


        // Group Admin兼Checker、承認フローパターン2の場合
        workflowPattern.setWorkflowCd(SystemConfig.getValue("workflow.pattern.2"));
        user.setEmpNo(CHECKER_2.getEmpNo());
        workflowList.get(1).setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_CHECK);
        page.initialize();

        // 画面構成要素(リンク、ボタン)の確認
        assertTrue(page.getElemControl().isCopyLink());
        assertFalse(page.getElemControl().isForwardLink());
        assertTrue(page.getElemControl().isUpdateLink());
        assertTrue(page.getElemControl().isPrintLink());
        assertTrue(page.getElemControl().isDownloadLink());
        assertTrue(page.getElemControl().isZipLink());

        // 承認フローパターン2なので後続のApprovaerが変更可能
        assertTrue(page.getElemControl().isWorkFlowLink());

        assertTrue(page.getElemControl().isVerificationButton());
        assertFalse (page.getElemControl().isIssueButton());
        assertFalse(page.getElemControl().isRequestForApprovalButton());
        assertFalse(page.getElemControl().isDeleteButton());


        // Project Admin兼Checker、承認フローパターン3の場合
        workflowPattern.setWorkflowCd(SystemConfig.getValue("workflow.pattern.3"));
        user.setEmpNo(CHECKER_2.getEmpNo());
        workflowList.get(1).setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_CHECK);
        page.initialize();

        // 画面構成要素(リンク、ボタン)の確認
        assertTrue(page.getElemControl().isCopyLink());
        assertFalse(page.getElemControl().isForwardLink());
        assertTrue(page.getElemControl().isUpdateLink());
        assertTrue(page.getElemControl().isPrintLink());
        assertTrue(page.getElemControl().isDownloadLink());
        assertTrue(page.getElemControl().isZipLink());

        // 承認フローパターン3なのでChecker/Approverは変更できない
        assertFalse(page.getElemControl().isWorkFlowLink());

        assertTrue(page.getElemControl().isVerificationButton());
        assertFalse (page.getElemControl().isIssueButton());
        assertFalse(page.getElemControl().isRequestForApprovalButton());
        assertFalse(page.getElemControl().isDeleteButton());

        // Group Admin兼Checker、承認フローパターン2
        // Approverが承認依頼済の場合
        workflowPattern.setWorkflowCd(SystemConfig.getValue("workflow.pattern.2"));
        user.setEmpNo(CHECKER_2.getEmpNo());
        workflowList.get(1).setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_CHECK);
        workflowList.get(workflowList.size() - 1).setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_APPROVAL);
        page.initialize();

        // 画面構成要素(リンク、ボタン)の確認
        assertTrue(page.getElemControl().isCopyLink());
        assertFalse(page.getElemControl().isForwardLink());
        assertTrue(page.getElemControl().isUpdateLink());
        assertTrue(page.getElemControl().isPrintLink());
        assertTrue(page.getElemControl().isDownloadLink());
        assertTrue(page.getElemControl().isZipLink());

        // 承認フローパターン2だがApproverが承認依頼済なので変更できない
        assertFalse(page.getElemControl().isWorkFlowLink());

        assertTrue(page.getElemControl().isVerificationButton());
        assertFalse (page.getElemControl().isIssueButton());
        assertFalse(page.getElemControl().isRequestForApprovalButton());
        assertFalse(page.getElemControl().isDeleteButton());
    }

    /**
     * 画面構成要素の制御クラスのテスト. GroupAdminでPreparerの時.
     */
    @Test
    public void testInitializeGroupAdminPreparer_Draft() {
        MockAbstractPage.RET_SYSTEM_ADMIN = false;
        MockAbstractPage.RET_PROJECT_ADMIN = false;
        MockAbstractPage.RET_GROUP_ADMIN = true;
        User user = new User();
        user.setEmpNo("00001");
        MockAbstractPage.RET_USER = user;

        Project project = new Project();
        project.setProjectId("PJ1");
        MockAbstractPage.RET_PROJECT = project;

        Correspon expCorrespon = createCorrespon();
        List<Workflow> workflowList = createWorkflowList();

        expCorrespon.setWorkflows(workflowList);
        expCorrespon.setWorkflowStatus(WorkflowStatus.DRAFT);
        expCorrespon.getCorresponType().setForceToUseWorkflow(ForceToUseWorkflow.REQUIRED);

        MockCorresponService.RET_FIND = expCorrespon;

        MockCorresponGroupService.RET_FIND_CORRESPON_GROUP_USER_MAPPINGS = new ArrayList<CorresponGroupUserMapping>();
        MockCorresponGroupService.RET_SEARCH = new ArrayList<CorresponGroup>();
        MockUserService.RET_SEARCH = new ArrayList<ProjectUser>();

        // 必須のプロパティをセットして実行
        page.setId(expCorrespon.getId());
        page.initialize();

        // 画面構成要素(リンク、ボタン)の確認
        assertTrue(page.getElemControl().isCopyLink());
        assertFalse(page.getElemControl().isForwardLink());
        assertTrue(page.getElemControl().isUpdateLink());
        assertTrue(page.getElemControl().isPrintLink());
        assertTrue(page.getElemControl().isDownloadLink());
        assertTrue(page.getElemControl().isWorkFlowLink());
        assertFalse(page.getElemControl().isVerificationButton());
        assertFalse(page.getElemControl().isIssueButton());
        assertTrue(page.getElemControl().isRequestForApprovalButton());
        assertTrue(page.getElemControl().isDeleteButton());
        assertTrue(page.getElemControl().isZipLink());
    }

    /**
     * 画面構成要素の制御クラスのテスト. GroupAdminでPreparerの時.
     */
    @Test
    public void testInitializeGroupAdminPreparer_Draft_Optional() {
        MockAbstractPage.RET_SYSTEM_ADMIN = false;
        MockAbstractPage.RET_PROJECT_ADMIN = false;
        MockAbstractPage.RET_GROUP_ADMIN = true;
        User user = new User();
        user.setEmpNo("00001");
        MockAbstractPage.RET_USER = user;

        Project project = new Project();
        project.setProjectId("PJ1");
        MockAbstractPage.RET_PROJECT = project;

        Correspon expCorrespon = createCorrespon();
        List<Workflow> workflowList = createWorkflowList();

        expCorrespon.setWorkflows(workflowList);
        expCorrespon.setWorkflowStatus(WorkflowStatus.DRAFT);

        expCorrespon.getCorresponType().setForceToUseWorkflow(ForceToUseWorkflow.OPTIONAL);

        MockCorresponService.RET_FIND = expCorrespon;

        MockCorresponGroupService.RET_FIND_CORRESPON_GROUP_USER_MAPPINGS = new ArrayList<CorresponGroupUserMapping>();
        MockCorresponGroupService.RET_SEARCH = new ArrayList<CorresponGroup>();
        MockUserService.RET_SEARCH = new ArrayList<ProjectUser>();

        // 必須のプロパティをセットして実行
        page.setId(expCorrespon.getId());
        page.initialize();

        // 画面構成要素(リンク、ボタン)の確認
        assertTrue(page.getElemControl().isCopyLink());
        assertFalse(page.getElemControl().isForwardLink());
        assertTrue(page.getElemControl().isUpdateLink());
        assertTrue(page.getElemControl().isPrintLink());
        assertTrue(page.getElemControl().isDownloadLink());
        assertTrue(page.getElemControl().isWorkFlowLink());
        assertFalse(page.getElemControl().isVerificationButton());
        assertTrue(page.getElemControl().isIssueButton());
        assertTrue(page.getElemControl().isRequestForApprovalButton());
        assertTrue(page.getElemControl().isDeleteButton());
        assertTrue(page.getElemControl().isZipLink());

    }

    /**
     * 画面構成要素の制御クラスのテスト. GroupAdminの時.
     */
    @Test
    public void testInitializeGroupAdmin_Issued() {
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
        expCorrespon.setWorkflowStatus(WorkflowStatus.ISSUED);

        MockCorresponService.RET_FIND = expCorrespon;

        MockCorresponGroupService.RET_FIND_CORRESPON_GROUP_USER_MAPPINGS = new ArrayList<CorresponGroupUserMapping>();
        MockCorresponGroupService.RET_SEARCH = new ArrayList<CorresponGroup>();
        MockUserService.RET_SEARCH = new ArrayList<ProjectUser>();

        // 必須のプロパティをセットして実行
        page.setId(expCorrespon.getId());
        page.initialize();

        // 画面構成要素(リンク、ボタン)の確認
        assertTrue(page.getElemControl().isCopyLink());
        assertTrue(page.getElemControl().isForwardLink());
        assertTrue(page.getElemControl().isUpdateLink());
        assertTrue(page.getElemControl().isPrintLink());
        assertTrue(page.getElemControl().isDownloadLink());
        assertFalse(page.getElemControl().isWorkFlowLink());
        assertFalse(page.getElemControl().isVerificationButton());
        assertFalse(page.getElemControl().isIssueButton());
        assertFalse(page.getElemControl().isRequestForApprovalButton());
        assertFalse(page.getElemControl().isDeleteButton());
        assertTrue(page.getElemControl().isZipLink());

    }

    /**
     * 画面構成要素の制御クラスのテスト. GroupAdminの時.
     * 文書状態がCanceled.
     */
    @Test
    public void testInitializeGroupAdmin_Canceled() {
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
        expCorrespon.setCorresponStatus(CorresponStatus.CANCELED);
        List<Workflow> workflowList = createWorkflowList();

        expCorrespon.setWorkflows(workflowList);
        expCorrespon.setWorkflowStatus(WorkflowStatus.ISSUED);

        MockCorresponService.RET_FIND = expCorrespon;

        MockCorresponGroupService.RET_FIND_CORRESPON_GROUP_USER_MAPPINGS = new ArrayList<CorresponGroupUserMapping>();
        MockCorresponGroupService.RET_SEARCH = new ArrayList<CorresponGroup>();
        MockUserService.RET_SEARCH = new ArrayList<ProjectUser>();

        // 必須のプロパティをセットして実行
        page.setId(expCorrespon.getId());
        page.initialize();

        // 画面構成要素(リンク、ボタン)の確認
        assertTrue(page.getElemControl().isCopyLink());
        assertTrue(page.getElemControl().isForwardLink());
        assertFalse(page.getElemControl().isUpdateLink());
        assertTrue(page.getElemControl().isPrintLink());
        assertTrue(page.getElemControl().isDownloadLink());
        assertFalse(page.getElemControl().isWorkFlowLink());
        assertFalse(page.getElemControl().isVerificationButton());
        assertFalse(page.getElemControl().isIssueButton());
        assertFalse(page.getElemControl().isRequestForApprovalButton());
        assertFalse(page.getElemControl().isDeleteButton());
        assertTrue(page.getElemControl().isZipLink());

    }

    /**
     * 画面構成要素の制御クラスのテスト.
     */
    @Test
    public void testInitializeNormal() {
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

        expCorrespon.setWorkflows(workflowList);
        expCorrespon.setWorkflowStatus(WorkflowStatus.DRAFT);

        MockCorresponService.RET_FIND = expCorrespon;

        MockCorresponGroupService.RET_FIND_CORRESPON_GROUP_USER_MAPPINGS = new ArrayList<CorresponGroupUserMapping>();
        MockCorresponGroupService.RET_SEARCH = new ArrayList<CorresponGroup>();
        MockUserService.RET_SEARCH = new ArrayList<ProjectUser>();

        // 必須のプロパティをセットして実行
        page.setId(expCorrespon.getId());
        page.initialize();

        // 画面構成要素(リンク、ボタン)の確認
        assertTrue(page.getElemControl().isCopyLink());
        assertFalse(page.getElemControl().isForwardLink());
        assertFalse(page.getElemControl().isUpdateLink());
        assertFalse(page.getElemControl().isPrintLink());
        assertFalse(page.getElemControl().isDownloadLink());
        assertFalse(page.getElemControl().isWorkFlowLink());
        assertFalse(page.getElemControl().isVerificationButton());
        assertFalse(page.getElemControl().isIssueButton());
        assertTrue(page.getElemControl().isRequestForApprovalButton());
        assertFalse(page.getElemControl().isDeleteButton());
        assertTrue(page.getElemControl().isZipLink());

    }

    /**
     * 画面構成要素の制御クラスのテスト.
     */
    @Test
    public void testInitializeNormal_Issued() {
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

        expCorrespon.setWorkflows(workflowList);
        expCorrespon.setWorkflowStatus(WorkflowStatus.ISSUED);

        MockCorresponService.RET_FIND = expCorrespon;

        MockCorresponGroupService.RET_FIND_CORRESPON_GROUP_USER_MAPPINGS = new ArrayList<CorresponGroupUserMapping>();
        MockCorresponGroupService.RET_SEARCH = new ArrayList<CorresponGroup>();
        MockUserService.RET_SEARCH = new ArrayList<ProjectUser>();

        // 必須のプロパティをセットして実行
        page.setId(expCorrespon.getId());
        page.initialize();

        // 画面構成要素(リンク、ボタン)の確認
        assertTrue(page.getElemControl().isCopyLink());
        assertTrue(page.getElemControl().isForwardLink());
        assertFalse(page.getElemControl().isUpdateLink());
        assertTrue(page.getElemControl().isPrintLink());
        assertTrue(page.getElemControl().isDownloadLink());
        assertFalse(page.getElemControl().isWorkFlowLink());
        assertFalse(page.getElemControl().isVerificationButton());
        assertFalse(page.getElemControl().isIssueButton());
        assertFalse(page.getElemControl().isRequestForApprovalButton());
        assertFalse(page.getElemControl().isDeleteButton());
        assertTrue(page.getElemControl().isZipLink());

    }

    /**
     * 画面構成要素の制御クラスのテスト(Preparerの時).
     */
    @Test
    public void testInitializePreparer_Draft_Optional() {
        MockAbstractPage.RET_SYSTEM_ADMIN = false;
        MockAbstractPage.RET_PROJECT_ADMIN = false;
        MockAbstractPage.RET_GROUP_ADMIN = false;
        User user = new User();
        user.setEmpNo("00001");
        MockAbstractPage.RET_USER = user;

        Project project = new Project();
        project.setProjectId("PJ1");
        MockAbstractPage.RET_PROJECT = project;

        Correspon expCorrespon = createCorrespon();
        List<Workflow> workflowList = createWorkflowList();
        expCorrespon.setWorkflows(workflowList);
        expCorrespon.setWorkflowStatus(WorkflowStatus.DRAFT);

        expCorrespon.getCorresponType().setForceToUseWorkflow(ForceToUseWorkflow.OPTIONAL);

        MockCorresponService.RET_FIND = expCorrespon;

        MockCorresponGroupService.RET_FIND_CORRESPON_GROUP_USER_MAPPINGS = new ArrayList<CorresponGroupUserMapping>();
        MockCorresponGroupService.RET_SEARCH = new ArrayList<CorresponGroup>();
        MockUserService.RET_SEARCH = new ArrayList<ProjectUser>();

        // 必須のプロパティをセットして実行
        page.setId(expCorrespon.getId());
        page.initialize();

        // 画面構成要素(リンク、ボタン)の確認
        assertTrue(page.getElemControl().isCopyLink());
        assertFalse(page.getElemControl().isForwardLink());
        assertTrue(page.getElemControl().isUpdateLink());
        assertTrue(page.getElemControl().isPrintLink());
        assertTrue(page.getElemControl().isDownloadLink());
        assertTrue(page.getElemControl().isWorkFlowLink());
        assertFalse(page.getElemControl().isVerificationButton());
        assertTrue(page.getElemControl().isIssueButton());
        assertTrue(page.getElemControl().isRequestForApprovalButton());
        assertTrue(page.getElemControl().isDeleteButton());
        assertTrue(page.getElemControl().isZipLink());

    }

    /**
     * 画面構成要素の制御クラスのテスト(Preparerの時).
     */
    @Test
    public void testInitializePreparer_Draft_Required() {
        MockAbstractPage.RET_SYSTEM_ADMIN = false;
        MockAbstractPage.RET_PROJECT_ADMIN = false;
        MockAbstractPage.RET_GROUP_ADMIN = false;
        User user = new User();
        user.setEmpNo("00001");
        MockAbstractPage.RET_USER = user;

        Project project = new Project();
        project.setProjectId("PJ1");
        MockAbstractPage.RET_PROJECT = project;

        Correspon expCorrespon = createCorrespon();
        List<Workflow> workflowList = createWorkflowList();
        expCorrespon.setWorkflows(workflowList);
        expCorrespon.setWorkflowStatus(WorkflowStatus.DRAFT);

        expCorrespon.getCorresponType().setForceToUseWorkflow(ForceToUseWorkflow.REQUIRED);

        MockCorresponService.RET_FIND = expCorrespon;

        MockCorresponGroupService.RET_FIND_CORRESPON_GROUP_USER_MAPPINGS = new ArrayList<CorresponGroupUserMapping>();
        MockCorresponGroupService.RET_SEARCH = new ArrayList<CorresponGroup>();
        MockUserService.RET_SEARCH = new ArrayList<ProjectUser>();

        // 必須のプロパティをセットして実行
        page.setId(expCorrespon.getId());
        page.initialize();

        // 画面構成要素(リンク、ボタン)の確認
        assertTrue(page.getElemControl().isCopyLink());
        assertFalse(page.getElemControl().isForwardLink());
        assertTrue(page.getElemControl().isUpdateLink());
        assertTrue(page.getElemControl().isPrintLink());
        assertTrue(page.getElemControl().isDownloadLink());
        assertTrue(page.getElemControl().isWorkFlowLink());
        assertFalse(page.getElemControl().isVerificationButton());
        assertFalse(page.getElemControl().isIssueButton());
        assertTrue(page.getElemControl().isRequestForApprovalButton());
        assertTrue(page.getElemControl().isDeleteButton());
        assertTrue(page.getElemControl().isZipLink());

    }

    /**
     * 画面構成要素の制御クラスのテスト. Preparerの時.
     */
    @Test
    public void testInitializePreparer_Denied() {
        MockAbstractPage.RET_SYSTEM_ADMIN = false;
        MockAbstractPage.RET_PROJECT_ADMIN = false;
        MockAbstractPage.RET_GROUP_ADMIN = false;
        User user = new User();
        user.setEmpNo("00001");
        MockAbstractPage.RET_USER = user;

        Project project = new Project();
        project.setProjectId("PJ1");
        MockAbstractPage.RET_PROJECT = project;

        Correspon expCorrespon = createCorrespon();
        List<Workflow> workflowList = createWorkflowList();

        expCorrespon.setWorkflows(workflowList);
        expCorrespon.setWorkflowStatus(WorkflowStatus.DENIED);

        MockCorresponService.RET_FIND = expCorrespon;

        MockCorresponGroupService.RET_FIND_CORRESPON_GROUP_USER_MAPPINGS = new ArrayList<CorresponGroupUserMapping>();
        MockCorresponGroupService.RET_SEARCH = new ArrayList<CorresponGroup>();
        MockUserService.RET_SEARCH = new ArrayList<ProjectUser>();

        // 必須のプロパティをセットして実行
        page.setId(expCorrespon.getId());
        page.initialize();

        // 画面構成要素(リンク、ボタン)の確認
        assertTrue(page.getElemControl().isCopyLink());
        assertFalse(page.getElemControl().isForwardLink());
        assertTrue(page.getElemControl().isUpdateLink());
        assertTrue(page.getElemControl().isPrintLink());
        assertTrue(page.getElemControl().isDownloadLink());
        assertFalse(page.getElemControl().isWorkFlowLink());
        assertFalse(page.getElemControl().isVerificationButton());
        assertFalse(page.getElemControl().isIssueButton());
        assertFalse(page.getElemControl().isRequestForApprovalButton());
        assertFalse(page.getElemControl().isDeleteButton());
        assertTrue(page.getElemControl().isZipLink());

    }

    /**
     * 画面構成要素の制御クラスのテスト. Preparerの時.
     */
    @Test
    public void testInitializePreparer_Issued() {
        MockAbstractPage.RET_SYSTEM_ADMIN = false;
        MockAbstractPage.RET_PROJECT_ADMIN = false;
        MockAbstractPage.RET_GROUP_ADMIN = false;
        User user = new User();
        user.setEmpNo("00001");
        MockAbstractPage.RET_USER = user;

        Project project = new Project();
        project.setProjectId("PJ1");
        MockAbstractPage.RET_PROJECT = project;

        Correspon expCorrespon = createCorrespon();
        List<Workflow> workflowList = createWorkflowList();

        expCorrespon.setWorkflows(workflowList);
        expCorrespon.setWorkflowStatus(WorkflowStatus.ISSUED);

        MockCorresponService.RET_FIND = expCorrespon;

        MockCorresponGroupService.RET_FIND_CORRESPON_GROUP_USER_MAPPINGS = new ArrayList<CorresponGroupUserMapping>();
        MockCorresponGroupService.RET_SEARCH = new ArrayList<CorresponGroup>();
        MockUserService.RET_SEARCH = new ArrayList<ProjectUser>();

        // 必須のプロパティをセットして実行
        page.setId(expCorrespon.getId());
        page.initialize();

        // 画面構成要素(リンク、ボタン)の確認
        assertTrue(page.getElemControl().isCopyLink());
        assertTrue(page.getElemControl().isForwardLink());

        //発行済文書はPreparerであっても更新できない
        assertFalse(page.getElemControl().isUpdateLink());

        assertTrue(page.getElemControl().isPrintLink());
        assertTrue(page.getElemControl().isDownloadLink());
        assertFalse(page.getElemControl().isWorkFlowLink());
        assertFalse(page.getElemControl().isVerificationButton());
        assertFalse(page.getElemControl().isIssueButton());
        assertFalse(page.getElemControl().isRequestForApprovalButton());
        assertFalse(page.getElemControl().isDeleteButton());
        assertTrue(page.getElemControl().isZipLink());

    }

    /**
     * 画面構成要素の制御クラスのテスト. Preparerの時.
     */
    @Test
    public void testInitializePreparer_Under_Consideration() {
        MockAbstractPage.RET_SYSTEM_ADMIN = false;
        MockAbstractPage.RET_PROJECT_ADMIN = false;
        MockAbstractPage.RET_GROUP_ADMIN = false;
        User user = new User();
        user.setEmpNo("00001");
        MockAbstractPage.RET_USER = user;

        Project project = new Project();
        project.setProjectId("PJ1");
        MockAbstractPage.RET_PROJECT = project;

        Correspon expCorrespon = createCorrespon();
        List<Workflow> workflowList = createWorkflowList();

        expCorrespon.setWorkflows(workflowList);
        expCorrespon.setWorkflowStatus(WorkflowStatus.UNDER_CONSIDERATION);

        MockCorresponService.RET_FIND = expCorrespon;

        MockCorresponGroupService.RET_FIND_CORRESPON_GROUP_USER_MAPPINGS = new ArrayList<CorresponGroupUserMapping>();
        MockCorresponGroupService.RET_SEARCH = new ArrayList<CorresponGroup>();
        MockUserService.RET_SEARCH = new ArrayList<ProjectUser>();

        // 必須のプロパティをセットして実行
        page.setId(expCorrespon.getId());
        page.initialize();

        // 画面構成要素(リンク、ボタン)の確認
        assertTrue(page.getElemControl().isCopyLink());
        assertFalse(page.getElemControl().isForwardLink());
        assertFalse(page.getElemControl().isUpdateLink());
        assertTrue(page.getElemControl().isPrintLink());
        assertTrue(page.getElemControl().isDownloadLink());
        assertFalse(page.getElemControl().isWorkFlowLink());
        assertFalse(page.getElemControl().isVerificationButton());
        assertFalse(page.getElemControl().isIssueButton());
        assertFalse(page.getElemControl().isRequestForApprovalButton());
        assertFalse(page.getElemControl().isDeleteButton());
        assertTrue(page.getElemControl().isZipLink());

    }

    /**
     * 画面構成要素の制御クラスのテスト. Checkerの時.
     */
    @Test
    public void testInitializeChecker_Draft() {
        MockAbstractPage.RET_SYSTEM_ADMIN = false;
        MockAbstractPage.RET_PROJECT_ADMIN = false;
        MockAbstractPage.RET_GROUP_ADMIN = false;
        User user = new User();
        user.setEmpNo("USER001");
        MockAbstractPage.RET_USER = user;

        Project project = new Project();
        project.setProjectId("PJ1");
        MockAbstractPage.RET_PROJECT = project;

        Correspon expCorrespon = createCorrespon();
        List<Workflow> workflowList = createWorkflowList();

        expCorrespon.setWorkflows(workflowList);
        expCorrespon.setWorkflowStatus(WorkflowStatus.DRAFT);

        MockCorresponService.RET_FIND = expCorrespon;

        MockCorresponGroupService.RET_FIND_CORRESPON_GROUP_USER_MAPPINGS = new ArrayList<CorresponGroupUserMapping>();
        MockCorresponGroupService.RET_SEARCH = new ArrayList<CorresponGroup>();
        MockUserService.RET_SEARCH = new ArrayList<ProjectUser>();

        // 必須のプロパティをセットして実行
        page.setId(expCorrespon.getId());
        page.initialize();

        // 画面構成要素(リンク、ボタン)の確認
        assertTrue(page.getElemControl().isCopyLink());
        assertFalse(page.getElemControl().isForwardLink());
        assertFalse(page.getElemControl().isUpdateLink());
        assertFalse(page.getElemControl().isPrintLink());
        assertFalse(page.getElemControl().isDownloadLink());
        assertFalse(page.getElemControl().isWorkFlowLink());
        assertFalse(page.getElemControl().isVerificationButton());
        assertFalse(page.getElemControl().isIssueButton());
        assertTrue(page.getElemControl().isRequestForApprovalButton());
        assertFalse(page.getElemControl().isDeleteButton());
        assertTrue(page.getElemControl().isZipLink());

    }

    /**
     * 画面構成要素の制御クラスのテスト. Checkerの時.承認フローパターンが2の時.
     */
    @Test
    public void testInitializeChecker_Request_For_Check_Pattern_2_Approver_Status_None() {
        MockAbstractPage.RET_SYSTEM_ADMIN = false;
        MockAbstractPage.RET_PROJECT_ADMIN = false;
        MockAbstractPage.RET_GROUP_ADMIN = false;
        User user = new User();
        user.setEmpNo("USER002");
        MockAbstractPage.RET_USER = user;

        Project project = new Project();
        project.setProjectId("PJ1");
        MockAbstractPage.RET_PROJECT = project;

        Correspon expCorrespon = createCorrespon();
        List<Workflow> workflowList = createWorkflowList_Approver_Status_None();

        expCorrespon.setWorkflows(workflowList);
        expCorrespon.setWorkflowStatus(WorkflowStatus.REQUEST_FOR_CHECK);

        WorkflowPattern workflowPattern = new WorkflowPattern();
        workflowPattern.setWorkflowCd(SystemConfig.getValue("workflow.pattern.2"));
        expCorrespon.getCorresponType().setWorkflowPattern(workflowPattern);

        MockCorresponService.RET_FIND = expCorrespon;

        MockCorresponGroupService.RET_FIND_CORRESPON_GROUP_USER_MAPPINGS = new ArrayList<CorresponGroupUserMapping>();
        MockCorresponGroupService.RET_SEARCH = new ArrayList<CorresponGroup>();
        MockUserService.RET_SEARCH = new ArrayList<ProjectUser>();

        // 必須のプロパティをセットして実行
        page.setId(expCorrespon.getId());
        page.initialize();

        // 画面構成要素(リンク、ボタン)の確認
        assertTrue(page.getElemControl().isCopyLink());
        assertFalse(page.getElemControl().isForwardLink());
        assertTrue(page.getElemControl().isUpdateLink());
        assertTrue(page.getElemControl().isPrintLink());
        assertTrue(page.getElemControl().isDownloadLink());
        assertTrue(page.getElemControl().isWorkFlowLink());
        assertTrue(page.getElemControl().isVerificationButton());
        assertFalse(page.getElemControl().isIssueButton());
        assertFalse(page.getElemControl().isRequestForApprovalButton());
        assertFalse(page.getElemControl().isDeleteButton());
        assertTrue(page.getElemControl().isZipLink());

    }

    /**
     * 画面構成要素の制御クラスのテスト. Checkerの時.承認フローパターンが2の時.
     */
    @Test
    public void testInitializeChecker_Request_For_Check_Pattern_2_Approver_Status_An_None() {
        MockAbstractPage.RET_SYSTEM_ADMIN = false;
        MockAbstractPage.RET_PROJECT_ADMIN = false;
        MockAbstractPage.RET_GROUP_ADMIN = false;
        User user = new User();
        user.setEmpNo("USER002");
        MockAbstractPage.RET_USER = user;

        Project project = new Project();
        project.setProjectId("PJ1");
        MockAbstractPage.RET_PROJECT = project;

        Correspon expCorrespon = createCorrespon();
        List<Workflow> workflowList = createWorkflowList_Approver_Status_None();
        // ApproverのステータスをNone以外
        workflowList.get(workflowList.size() -1).setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_APPROVAL);

        expCorrespon.setWorkflows(workflowList);
        expCorrespon.setWorkflowStatus(WorkflowStatus.REQUEST_FOR_CHECK);

        WorkflowPattern workflowPattern = new WorkflowPattern();
        workflowPattern.setWorkflowCd(SystemConfig.getValue("workflow.pattern.2"));
        expCorrespon.getCorresponType().setWorkflowPattern(workflowPattern);

        MockCorresponService.RET_FIND = expCorrespon;

        MockCorresponGroupService.RET_FIND_CORRESPON_GROUP_USER_MAPPINGS = new ArrayList<CorresponGroupUserMapping>();
        MockCorresponGroupService.RET_SEARCH = new ArrayList<CorresponGroup>();
        MockUserService.RET_SEARCH = new ArrayList<ProjectUser>();

        // 必須のプロパティをセットして実行
        page.setId(expCorrespon.getId());
        page.initialize();

        // 画面構成要素(リンク、ボタン)の確認
        assertTrue(page.getElemControl().isCopyLink());
        assertFalse(page.getElemControl().isForwardLink());
        assertTrue(page.getElemControl().isUpdateLink());
        assertTrue(page.getElemControl().isPrintLink());
        assertTrue(page.getElemControl().isDownloadLink());
        assertFalse(page.getElemControl().isWorkFlowLink());
        assertTrue(page.getElemControl().isVerificationButton());
        assertFalse(page.getElemControl().isIssueButton());
        assertFalse(page.getElemControl().isRequestForApprovalButton());
        assertFalse(page.getElemControl().isDeleteButton());
        assertTrue(page.getElemControl().isZipLink());

    }

    /**
     * 画面構成要素の制御クラスのテスト. Checkerの時.
     */
    @Test
    public void testInitializeChecker_Request_For_Check_Case1() {
        MockAbstractPage.RET_SYSTEM_ADMIN = false;
        MockAbstractPage.RET_PROJECT_ADMIN = false;
        MockAbstractPage.RET_GROUP_ADMIN = false;
        User user = new User();
        user.setEmpNo("USER002");
        MockAbstractPage.RET_USER = user;

        Project project = new Project();
        project.setProjectId("PJ1");
        MockAbstractPage.RET_PROJECT = project;

        Correspon expCorrespon = createCorrespon();
        List<Workflow> workflowList = createWorkflowList_Case1();
        workflowList.get(1).setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_CHECK);

        expCorrespon.setWorkflows(workflowList);
        expCorrespon.setWorkflowStatus(WorkflowStatus.REQUEST_FOR_CHECK);

        WorkflowPattern workflowPattern = new WorkflowPattern();
        workflowPattern.setWorkflowCd(SystemConfig.getValue("workflow.pattern.2"));
        expCorrespon.getCorresponType().setWorkflowPattern(workflowPattern);

        MockCorresponService.RET_FIND = expCorrespon;

        MockCorresponGroupService.RET_FIND_CORRESPON_GROUP_USER_MAPPINGS = new ArrayList<CorresponGroupUserMapping>();
        MockCorresponGroupService.RET_SEARCH = new ArrayList<CorresponGroup>();
        MockUserService.RET_SEARCH = new ArrayList<ProjectUser>();

        // 必須のプロパティをセットして実行
        page.setId(expCorrespon.getId());
        page.initialize();

        // 画面構成要素(リンク、ボタン)の確認
        assertTrue(page.getElemControl().isCopyLink());
        assertFalse(page.getElemControl().isForwardLink());
        assertTrue(page.getElemControl().isUpdateLink());
        assertTrue(page.getElemControl().isPrintLink());
        assertTrue(page.getElemControl().isDownloadLink());
        assertTrue(page.getElemControl().isWorkFlowLink());
        assertTrue(page.getElemControl().isVerificationButton());
        assertFalse(page.getElemControl().isIssueButton());
        assertFalse(page.getElemControl().isRequestForApprovalButton());
        assertFalse(page.getElemControl().isDeleteButton());
        assertTrue(page.getElemControl().isZipLink());
    }

    /**
     * 画面構成要素の制御クラスのテスト. Checkerの時.
     */
    @Test
    public void testInitializeChecker_Request_For_Check_Case2() {
        MockAbstractPage.RET_SYSTEM_ADMIN = false;
        MockAbstractPage.RET_PROJECT_ADMIN = false;
        MockAbstractPage.RET_GROUP_ADMIN = false;
        User user = new User();
        user.setEmpNo("USER002");
        MockAbstractPage.RET_USER = user;

        Project project = new Project();
        project.setProjectId("PJ1");
        MockAbstractPage.RET_PROJECT = project;

        Correspon expCorrespon = createCorrespon();
        List<Workflow> workflowList = createWorkflowList_Case2();
        workflowList.get(1).setWorkflowProcessStatus(WorkflowProcessStatus.UNDER_CONSIDERATION);

        expCorrespon.setWorkflows(workflowList);
        expCorrespon.setWorkflowStatus(WorkflowStatus.REQUEST_FOR_CHECK);

        WorkflowPattern workflowPattern = new WorkflowPattern();
        workflowPattern.setWorkflowCd(SystemConfig.getValue("workflow.pattern.2"));
        expCorrespon.getCorresponType().setWorkflowPattern(workflowPattern);

        MockCorresponService.RET_FIND = expCorrespon;

        MockCorresponGroupService.RET_FIND_CORRESPON_GROUP_USER_MAPPINGS = new ArrayList<CorresponGroupUserMapping>();
        MockCorresponGroupService.RET_SEARCH = new ArrayList<CorresponGroup>();
        MockUserService.RET_SEARCH = new ArrayList<ProjectUser>();

        // 必須のプロパティをセットして実行
        page.setId(expCorrespon.getId());
        page.initialize();

        // 画面構成要素(リンク、ボタン)の確認
        assertTrue(page.getElemControl().isCopyLink());
        assertFalse(page.getElemControl().isForwardLink());
        assertTrue(page.getElemControl().isUpdateLink());
        assertTrue(page.getElemControl().isPrintLink());
        assertTrue(page.getElemControl().isDownloadLink());
        assertTrue(page.getElemControl().isWorkFlowLink());
        assertTrue(page.getElemControl().isVerificationButton());
        assertFalse(page.getElemControl().isIssueButton());
        assertFalse(page.getElemControl().isRequestForApprovalButton());
        assertFalse(page.getElemControl().isDeleteButton());
        assertTrue(page.getElemControl().isZipLink());
    }

    /**
     * 画面構成要素の制御クラスのテスト. Checkerの時.
     */
    @Test
    public void testInitializeChecker_Request_For_Check_Case3() {
        MockAbstractPage.RET_SYSTEM_ADMIN = false;
        MockAbstractPage.RET_PROJECT_ADMIN = false;
        MockAbstractPage.RET_GROUP_ADMIN = false;
        User user = new User();
        user.setEmpNo("USER002");
        MockAbstractPage.RET_USER = user;

        Project project = new Project();
        project.setProjectId("PJ1");
        MockAbstractPage.RET_PROJECT = project;

        Correspon expCorrespon = createCorrespon();
        List<Workflow> workflowList = createWorkflowList_Case2();
        workflowList.get(1).setWorkflowProcessStatus(WorkflowProcessStatus.CHECKED);

        expCorrespon.setWorkflows(workflowList);
        expCorrespon.setWorkflowStatus(WorkflowStatus.UNDER_CONSIDERATION);

        WorkflowPattern workflowPattern = new WorkflowPattern();
        workflowPattern.setWorkflowCd(SystemConfig.getValue("workflow.pattern.1"));
        expCorrespon.getCorresponType().setWorkflowPattern(workflowPattern);

        MockCorresponService.RET_FIND = expCorrespon;

        MockCorresponGroupService.RET_FIND_CORRESPON_GROUP_USER_MAPPINGS = new ArrayList<CorresponGroupUserMapping>();
        MockCorresponGroupService.RET_SEARCH = new ArrayList<CorresponGroup>();
        MockUserService.RET_SEARCH = new ArrayList<ProjectUser>();

        // 必須のプロパティをセットして実行
        page.setId(expCorrespon.getId());
        page.initialize();

        // 画面構成要素(リンク、ボタン)の確認
        assertTrue(page.getElemControl().isCopyLink());
        assertFalse(page.getElemControl().isForwardLink());
        assertFalse(page.getElemControl().isUpdateLink());
        assertTrue(page.getElemControl().isPrintLink());
        assertTrue(page.getElemControl().isDownloadLink());
        assertFalse(page.getElemControl().isWorkFlowLink());
        assertFalse(page.getElemControl().isVerificationButton());
        assertFalse(page.getElemControl().isIssueButton());
        assertFalse(page.getElemControl().isRequestForApprovalButton());
        assertFalse(page.getElemControl().isDeleteButton());
        assertTrue(page.getElemControl().isZipLink());
    }

    /**
     * 画面構成要素の制御クラスのテスト. Checkerの時.
     */
    @Test
    public void testInitializeChecker_Request_For_Check_Case4() {
        MockAbstractPage.RET_SYSTEM_ADMIN = false;
        MockAbstractPage.RET_PROJECT_ADMIN = false;
        MockAbstractPage.RET_GROUP_ADMIN = false;
        User user = new User();
        user.setEmpNo("USER003");
        MockAbstractPage.RET_USER = user;

        Project project = new Project();
        project.setProjectId("PJ1");
        MockAbstractPage.RET_PROJECT = project;

        Correspon expCorrespon = createCorrespon();
        List<Workflow> workflowList = createWorkflowList_Case7();

        expCorrespon.setWorkflows(workflowList);
        expCorrespon.setWorkflowStatus(WorkflowStatus.REQUEST_FOR_APPROVAL);

        WorkflowPattern workflowPattern = new WorkflowPattern();
        workflowPattern.setWorkflowCd(SystemConfig.getValue("workflow.pattern.1"));
        expCorrespon.getCorresponType().setWorkflowPattern(workflowPattern);

        MockCorresponService.RET_FIND = expCorrespon;

        MockCorresponGroupService.RET_FIND_CORRESPON_GROUP_USER_MAPPINGS = new ArrayList<CorresponGroupUserMapping>();
        MockCorresponGroupService.RET_SEARCH = new ArrayList<CorresponGroup>();
        MockUserService.RET_SEARCH = new ArrayList<ProjectUser>();

        // 必須のプロパティをセットして実行
        page.setId(expCorrespon.getId());
        page.initialize();

        // 画面構成要素(リンク、ボタン)の確認
        assertTrue(page.getElemControl().isCopyLink());
        assertFalse(page.getElemControl().isForwardLink());
        assertFalse(page.getElemControl().isUpdateLink());
        assertTrue(page.getElemControl().isPrintLink());
        assertTrue(page.getElemControl().isDownloadLink());
        assertFalse(page.getElemControl().isWorkFlowLink());
        assertFalse(page.getElemControl().isVerificationButton());
        assertFalse(page.getElemControl().isIssueButton());
        assertFalse(page.getElemControl().isRequestForApprovalButton());
        assertFalse(page.getElemControl().isDeleteButton());
        assertTrue(page.getElemControl().isZipLink());
    }

    /**
     * 画面構成要素の制御クラスのテスト. Checkerの時.
     */
    @Test
    public void testInitializeChecker_Request_For_Approval() {
        MockAbstractPage.RET_SYSTEM_ADMIN = false;
        MockAbstractPage.RET_PROJECT_ADMIN = false;
        MockAbstractPage.RET_GROUP_ADMIN = false;
        User user = new User();
        user.setEmpNo("USER001");
        MockAbstractPage.RET_USER = user;

        Project project = new Project();
        project.setProjectId("PJ1");
        MockAbstractPage.RET_PROJECT = project;

        Correspon expCorrespon = createCorrespon();
        List<Workflow> workflowList = createWorkflowList();

        expCorrespon.setWorkflows(workflowList);
        expCorrespon.setWorkflowStatus(WorkflowStatus.REQUEST_FOR_APPROVAL);

        MockCorresponService.RET_FIND = expCorrespon;

        MockCorresponGroupService.RET_FIND_CORRESPON_GROUP_USER_MAPPINGS = new ArrayList<CorresponGroupUserMapping>();
        MockCorresponGroupService.RET_SEARCH = new ArrayList<CorresponGroup>();
        MockUserService.RET_SEARCH = new ArrayList<ProjectUser>();

        // 必須のプロパティをセットして実行
        page.setId(expCorrespon.getId());
        page.initialize();

        // 画面構成要素(リンク、ボタン)の確認
        assertTrue(page.getElemControl().isCopyLink());
        assertFalse(page.getElemControl().isForwardLink());
        assertFalse(page.getElemControl().isUpdateLink());
        assertTrue(page.getElemControl().isPrintLink());
        assertTrue(page.getElemControl().isDownloadLink());
        assertFalse(page.getElemControl().isWorkFlowLink());
        assertFalse(page.getElemControl().isVerificationButton());
        assertFalse(page.getElemControl().isIssueButton());
        assertFalse(page.getElemControl().isRequestForApprovalButton());
        assertFalse(page.getElemControl().isDeleteButton());
        assertTrue(page.getElemControl().isZipLink());

    }

    /**
     * 画面構成要素の制御クラスのテスト. Checkerの時.
     */
    @Test
    public void testInitializeChecker_Denied() {
        MockAbstractPage.RET_SYSTEM_ADMIN = false;
        MockAbstractPage.RET_PROJECT_ADMIN = false;
        MockAbstractPage.RET_GROUP_ADMIN = false;
        User user = new User();
        user.setEmpNo("USER001");
        MockAbstractPage.RET_USER = user;

        Project project = new Project();
        project.setProjectId("PJ1");
        MockAbstractPage.RET_PROJECT = project;

        Correspon expCorrespon = createCorrespon();
        List<Workflow> workflowList = createWorkflowList();

        expCorrespon.setWorkflows(workflowList);
        expCorrespon.setWorkflowStatus(WorkflowStatus.DENIED);

        MockCorresponService.RET_FIND = expCorrespon;

        MockCorresponGroupService.RET_FIND_CORRESPON_GROUP_USER_MAPPINGS = new ArrayList<CorresponGroupUserMapping>();
        MockCorresponGroupService.RET_SEARCH = new ArrayList<CorresponGroup>();
        MockUserService.RET_SEARCH = new ArrayList<ProjectUser>();

        // 必須のプロパティをセットして実行
        page.setId(expCorrespon.getId());
        page.initialize();

        // 画面構成要素(リンク、ボタン)の確認
        assertTrue(page.getElemControl().isCopyLink());
        assertFalse(page.getElemControl().isForwardLink());
        assertFalse(page.getElemControl().isUpdateLink());
        assertTrue(page.getElemControl().isPrintLink());
        assertTrue(page.getElemControl().isDownloadLink());
        assertFalse(page.getElemControl().isWorkFlowLink());
        assertFalse(page.getElemControl().isVerificationButton());
        assertFalse(page.getElemControl().isIssueButton());
        assertFalse(page.getElemControl().isRequestForApprovalButton());
        assertFalse(page.getElemControl().isDeleteButton());
        assertTrue(page.getElemControl().isZipLink());

    }

    /**
     * 画面構成要素の制御クラスのテスト. Checkerの時.
     */
    @Test
    public void testInitializeChecker_Issued() {
        MockAbstractPage.RET_SYSTEM_ADMIN = false;
        MockAbstractPage.RET_PROJECT_ADMIN = false;
        MockAbstractPage.RET_GROUP_ADMIN = false;
        User user = new User();
        user.setEmpNo("USER001");
        MockAbstractPage.RET_USER = user;

        Project project = new Project();
        project.setProjectId("PJ1");
        MockAbstractPage.RET_PROJECT = project;

        Correspon expCorrespon = createCorrespon();
        List<Workflow> workflowList = createWorkflowList();

        expCorrespon.setWorkflows(workflowList);
        expCorrespon.setWorkflowStatus(WorkflowStatus.ISSUED);

        MockCorresponService.RET_FIND = expCorrespon;

        MockCorresponGroupService.RET_FIND_CORRESPON_GROUP_USER_MAPPINGS = new ArrayList<CorresponGroupUserMapping>();
        MockCorresponGroupService.RET_SEARCH = new ArrayList<CorresponGroup>();
        MockUserService.RET_SEARCH = new ArrayList<ProjectUser>();

        // 必須のプロパティをセットして実行
        page.setId(expCorrespon.getId());
        page.initialize();

        // 画面構成要素(リンク、ボタン)の確認
        assertTrue(page.getElemControl().isCopyLink());
        assertTrue(page.getElemControl().isForwardLink());

        //  発行済文書はCheckerであっても更新できない
        assertFalse(page.getElemControl().isUpdateLink());

        assertTrue(page.getElemControl().isPrintLink());
        assertTrue(page.getElemControl().isDownloadLink());
        assertFalse(page.getElemControl().isWorkFlowLink());
        assertFalse(page.getElemControl().isVerificationButton());
        assertFalse(page.getElemControl().isIssueButton());
        assertFalse(page.getElemControl().isRequestForApprovalButton());
        assertFalse(page.getElemControl().isDeleteButton());
        assertTrue(page.getElemControl().isZipLink());

    }

    /**
     * 画面構成要素の制御クラスのテスト. Approverの時.
     */
    @Test
    public void testInitializeApprover_Draft() {
        MockAbstractPage.RET_SYSTEM_ADMIN = false;
        MockAbstractPage.RET_PROJECT_ADMIN = false;
        MockAbstractPage.RET_GROUP_ADMIN = false;
        User user = new User();
        user.setEmpNo("USER004");
        MockAbstractPage.RET_USER = user;

        Project project = new Project();
        project.setProjectId("PJ1");
        MockAbstractPage.RET_PROJECT = project;

        Correspon expCorrespon = createCorrespon();
        List<Workflow> workflowList = createWorkflowList();

        expCorrespon.setWorkflows(workflowList);
        expCorrespon.setWorkflowStatus(WorkflowStatus.DRAFT);

        MockCorresponService.RET_FIND = expCorrespon;

        MockCorresponGroupService.RET_FIND_CORRESPON_GROUP_USER_MAPPINGS = new ArrayList<CorresponGroupUserMapping>();
        MockCorresponGroupService.RET_SEARCH = new ArrayList<CorresponGroup>();
        MockUserService.RET_SEARCH = new ArrayList<ProjectUser>();

        // 必須のプロパティをセットして実行
        page.setId(expCorrespon.getId());
        page.initialize();

        // 画面構成要素(リンク、ボタン)の確認
        assertTrue(page.getElemControl().isCopyLink());
        assertFalse(page.getElemControl().isForwardLink());
        assertFalse(page.getElemControl().isUpdateLink());
        assertFalse(page.getElemControl().isPrintLink());
        assertFalse(page.getElemControl().isDownloadLink());
        assertFalse(page.getElemControl().isWorkFlowLink());
        assertFalse(page.getElemControl().isVerificationButton());
        assertFalse(page.getElemControl().isIssueButton());
        assertTrue(page.getElemControl().isRequestForApprovalButton());
        assertFalse(page.getElemControl().isDeleteButton());
        assertTrue(page.getElemControl().isZipLink());

    }

    /**
     * 画面構成要素の制御クラスのテスト. Approverの時.
     */
    @Test
    public void testInitializeApprover_Under_Consideration_Case1() {
        MockAbstractPage.RET_SYSTEM_ADMIN = false;
        MockAbstractPage.RET_PROJECT_ADMIN = false;
        MockAbstractPage.RET_GROUP_ADMIN = false;
        User user = new User();
        user.setEmpNo("USER004");
        MockAbstractPage.RET_USER = user;

        Project project = new Project();
        project.setProjectId("PJ1");
        MockAbstractPage.RET_PROJECT = project;

        Correspon expCorrespon = createCorrespon();
        List<Workflow> workflowList = createWorkflowList_Case3();

        expCorrespon.setWorkflows(workflowList);
        expCorrespon.setWorkflowStatus(WorkflowStatus.UNDER_CONSIDERATION);

        WorkflowPattern workflowPattern = new WorkflowPattern();
        workflowPattern.setWorkflowCd(SystemConfig.getValue("workflow.pattern.1"));
        expCorrespon.getCorresponType().setWorkflowPattern(workflowPattern);
        expCorrespon.getCorresponType().setAllowApproverToBrowse(AllowApproverToBrowse.VISIBLE);

        MockCorresponService.RET_FIND = expCorrespon;

        MockCorresponGroupService.RET_FIND_CORRESPON_GROUP_USER_MAPPINGS = new ArrayList<CorresponGroupUserMapping>();
        MockCorresponGroupService.RET_SEARCH = new ArrayList<CorresponGroup>();
        MockUserService.RET_SEARCH = new ArrayList<ProjectUser>();

        // 必須のプロパティをセットして実行
        page.setId(expCorrespon.getId());
        page.initialize();

        // 画面構成要素(リンク、ボタン)の確認
        assertTrue(page.getElemControl().isCopyLink());
        assertFalse(page.getElemControl().isForwardLink());
        assertFalse(page.getElemControl().isUpdateLink());
        assertTrue(page.getElemControl().isPrintLink());
        assertTrue(page.getElemControl().isDownloadLink());
        assertFalse(page.getElemControl().isWorkFlowLink());
        assertFalse(page.getElemControl().isVerificationButton());
        assertFalse(page.getElemControl().isIssueButton());
        assertFalse(page.getElemControl().isRequestForApprovalButton());
        assertFalse(page.getElemControl().isDeleteButton());
        assertTrue(page.getElemControl().isZipLink());

    }

    /**
     * 画面構成要素の制御クラスのテスト. Approverの時.
     */
    @Test
    public void testInitializeApprover_Under_Consideration_Case2() {
        MockAbstractPage.RET_SYSTEM_ADMIN = false;
        MockAbstractPage.RET_PROJECT_ADMIN = false;
        MockAbstractPage.RET_GROUP_ADMIN = false;
        User user = new User();
        user.setEmpNo("USER004");
        MockAbstractPage.RET_USER = user;

        Project project = new Project();
        project.setProjectId("PJ1");
        MockAbstractPage.RET_PROJECT = project;

        Correspon expCorrespon = createCorrespon();
        List<Workflow> workflowList = createWorkflowList_Case4();

        expCorrespon.setWorkflows(workflowList);
        expCorrespon.setWorkflowStatus(WorkflowStatus.UNDER_CONSIDERATION);

        WorkflowPattern workflowPattern = new WorkflowPattern();
        workflowPattern.setWorkflowCd(SystemConfig.getValue("workflow.pattern.1"));
        expCorrespon.getCorresponType().setWorkflowPattern(workflowPattern);
        expCorrespon.getCorresponType().setAllowApproverToBrowse(AllowApproverToBrowse.VISIBLE);

        MockCorresponService.RET_FIND = expCorrespon;

        MockCorresponGroupService.RET_FIND_CORRESPON_GROUP_USER_MAPPINGS = new ArrayList<CorresponGroupUserMapping>();
        MockCorresponGroupService.RET_SEARCH = new ArrayList<CorresponGroup>();
        MockUserService.RET_SEARCH = new ArrayList<ProjectUser>();

        // 必須のプロパティをセットして実行
        page.setId(expCorrespon.getId());
        page.initialize();

        // 画面構成要素(リンク、ボタン)の確認
        assertTrue(page.getElemControl().isCopyLink());
        assertFalse(page.getElemControl().isForwardLink());
        assertTrue(page.getElemControl().isUpdateLink());
        assertTrue(page.getElemControl().isPrintLink());
        assertTrue(page.getElemControl().isDownloadLink());
        assertFalse(page.getElemControl().isWorkFlowLink());
        assertTrue(page.getElemControl().isVerificationButton());
        assertFalse(page.getElemControl().isIssueButton());
        assertFalse(page.getElemControl().isRequestForApprovalButton());
        assertFalse(page.getElemControl().isDeleteButton());
        assertTrue(page.getElemControl().isZipLink());

    }

    /**
     * 画面構成要素の制御クラスのテスト. Approverの時.
     */
    @Test
    public void testInitializeApprover_Under_Consideration_Case3() {
        MockAbstractPage.RET_SYSTEM_ADMIN = false;
        MockAbstractPage.RET_PROJECT_ADMIN = false;
        MockAbstractPage.RET_GROUP_ADMIN = false;
        User user = new User();
        user.setEmpNo("USER004");
        MockAbstractPage.RET_USER = user;

        Project project = new Project();
        project.setProjectId("PJ1");
        MockAbstractPage.RET_PROJECT = project;

        Correspon expCorrespon = createCorrespon();
        List<Workflow> workflowList = createWorkflowList_Case4();

        expCorrespon.setWorkflows(workflowList);
        expCorrespon.setWorkflowStatus(WorkflowStatus.UNDER_CONSIDERATION);

        WorkflowPattern workflowPattern = new WorkflowPattern();
        workflowPattern.setWorkflowCd(SystemConfig.getValue("workflow.pattern.1"));
        expCorrespon.getCorresponType().setWorkflowPattern(workflowPattern);

        MockCorresponService.RET_FIND = expCorrespon;

        MockCorresponGroupService.RET_FIND_CORRESPON_GROUP_USER_MAPPINGS = new ArrayList<CorresponGroupUserMapping>();
        MockCorresponGroupService.RET_SEARCH = new ArrayList<CorresponGroup>();
        MockUserService.RET_SEARCH = new ArrayList<ProjectUser>();

        // 必須のプロパティをセットして実行
        page.setId(expCorrespon.getId());
        page.initialize();

        // 画面構成要素(リンク、ボタン)の確認
        assertTrue(page.getElemControl().isCopyLink());
        assertFalse(page.getElemControl().isForwardLink());
        assertTrue(page.getElemControl().isUpdateLink());
        assertTrue(page.getElemControl().isPrintLink());
        assertTrue(page.getElemControl().isDownloadLink());
        assertFalse(page.getElemControl().isWorkFlowLink());
        assertTrue(page.getElemControl().isVerificationButton());
        assertFalse(page.getElemControl().isIssueButton());
        assertFalse(page.getElemControl().isRequestForApprovalButton());
        assertFalse(page.getElemControl().isDeleteButton());
        assertTrue(page.getElemControl().isZipLink());

    }

    /**
     * 画面構成要素の制御クラスのテスト. Approverの時.
     */
    @Test
    public void testInitializeApprover_Under_Consideration_Case4() {
        MockAbstractPage.RET_SYSTEM_ADMIN = false;
        MockAbstractPage.RET_PROJECT_ADMIN = false;
        MockAbstractPage.RET_GROUP_ADMIN = false;
        User user = new User();
        user.setEmpNo("USER004");
        MockAbstractPage.RET_USER = user;

        Project project = new Project();
        project.setProjectId("PJ1");
        MockAbstractPage.RET_PROJECT = project;

        Correspon expCorrespon = createCorrespon();
        List<Workflow> workflowList = createWorkflowList_Case5();

        expCorrespon.setWorkflows(workflowList);
        expCorrespon.setWorkflowStatus(WorkflowStatus.UNDER_CONSIDERATION);

        WorkflowPattern workflowPattern = new WorkflowPattern();
        workflowPattern.setWorkflowCd(SystemConfig.getValue("workflow.pattern.1"));
        expCorrespon.getCorresponType().setWorkflowPattern(workflowPattern);

        MockCorresponService.RET_FIND = expCorrespon;

        MockCorresponGroupService.RET_FIND_CORRESPON_GROUP_USER_MAPPINGS = new ArrayList<CorresponGroupUserMapping>();
        MockCorresponGroupService.RET_SEARCH = new ArrayList<CorresponGroup>();
        MockUserService.RET_SEARCH = new ArrayList<ProjectUser>();

        // 必須のプロパティをセットして実行
        page.setId(expCorrespon.getId());
        page.initialize();

        // 画面構成要素(リンク、ボタン)の確認
        assertTrue(page.getElemControl().isCopyLink());
        assertFalse(page.getElemControl().isForwardLink());
        assertTrue(page.getElemControl().isUpdateLink());
        assertTrue(page.getElemControl().isPrintLink());
        assertTrue(page.getElemControl().isDownloadLink());
        assertFalse(page.getElemControl().isWorkFlowLink());
        assertTrue(page.getElemControl().isVerificationButton());
        assertFalse(page.getElemControl().isIssueButton());
        assertFalse(page.getElemControl().isRequestForApprovalButton());
        assertFalse(page.getElemControl().isDeleteButton());
        assertTrue(page.getElemControl().isZipLink());
    }

    /**
     * 画面構成要素の制御クラスのテスト. Approverの時.
     */
    @Test
    public void testInitializeApprover_Under_Consideration_Case5() {
        MockAbstractPage.RET_SYSTEM_ADMIN = false;
        MockAbstractPage.RET_PROJECT_ADMIN = false;
        MockAbstractPage.RET_GROUP_ADMIN = false;
        User user = new User();
        user.setEmpNo("USER004");
        MockAbstractPage.RET_USER = user;

        Project project = new Project();
        project.setProjectId("PJ1");
        MockAbstractPage.RET_PROJECT = project;

        Correspon expCorrespon = createCorrespon();
        List<Workflow> workflowList = createWorkflowList_Case6();

        expCorrespon.setWorkflows(workflowList);
        expCorrespon.setWorkflowStatus(WorkflowStatus.UNDER_CONSIDERATION);

        WorkflowPattern workflowPattern = new WorkflowPattern();
        workflowPattern.setWorkflowCd(SystemConfig.getValue("workflow.pattern.3"));
        expCorrespon.getCorresponType().setWorkflowPattern(workflowPattern);

        MockCorresponService.RET_FIND = expCorrespon;

        MockCorresponGroupService.RET_FIND_CORRESPON_GROUP_USER_MAPPINGS = new ArrayList<CorresponGroupUserMapping>();
        MockCorresponGroupService.RET_SEARCH = new ArrayList<CorresponGroup>();
        MockUserService.RET_SEARCH = new ArrayList<ProjectUser>();

        // 必須のプロパティをセットして実行
        page.setId(expCorrespon.getId());
        page.initialize();

        // 画面構成要素(リンク、ボタン)の確認
        assertTrue(page.getElemControl().isCopyLink());
        assertFalse(page.getElemControl().isForwardLink());
        assertTrue(page.getElemControl().isUpdateLink());
        assertTrue(page.getElemControl().isPrintLink());
        assertTrue(page.getElemControl().isDownloadLink());
        assertFalse(page.getElemControl().isWorkFlowLink());
        assertTrue(page.getElemControl().isVerificationButton());
        assertFalse(page.getElemControl().isIssueButton());
        assertFalse(page.getElemControl().isRequestForApprovalButton());
        assertFalse(page.getElemControl().isDeleteButton());
        assertTrue(page.getElemControl().isZipLink());

    }

    /**
     * 画面構成要素の制御クラスのテスト. Approverの時.
     */
    @Test
    public void testInitializeApprover_Request_For_Approval() {
        MockAbstractPage.RET_SYSTEM_ADMIN = false;
        MockAbstractPage.RET_PROJECT_ADMIN = false;
        MockAbstractPage.RET_GROUP_ADMIN = false;
        User user = new User();
        user.setEmpNo("USER004");
        MockAbstractPage.RET_USER = user;

        Project project = new Project();
        project.setProjectId("PJ1");
        MockAbstractPage.RET_PROJECT = project;

        Correspon expCorrespon = createCorrespon();
        List<Workflow> workflowList = createWorkflowList();

        expCorrespon.setWorkflows(workflowList);
        expCorrespon.setWorkflowStatus(WorkflowStatus.REQUEST_FOR_APPROVAL);

        MockCorresponService.RET_FIND = expCorrespon;

        MockCorresponGroupService.RET_FIND_CORRESPON_GROUP_USER_MAPPINGS = new ArrayList<CorresponGroupUserMapping>();
        MockCorresponGroupService.RET_SEARCH = new ArrayList<CorresponGroup>();
        MockUserService.RET_SEARCH = new ArrayList<ProjectUser>();

        // 必須のプロパティをセットして実行
        page.setId(expCorrespon.getId());
        page.initialize();

        // 画面構成要素(リンク、ボタン)の確認
        assertTrue(page.getElemControl().isCopyLink());
        assertFalse(page.getElemControl().isForwardLink());
        assertTrue(page.getElemControl().isUpdateLink());
        assertTrue(page.getElemControl().isPrintLink());
        assertTrue(page.getElemControl().isDownloadLink());
        assertFalse(page.getElemControl().isWorkFlowLink());
        assertTrue(page.getElemControl().isVerificationButton());
        assertFalse(page.getElemControl().isIssueButton());
        assertFalse(page.getElemControl().isRequestForApprovalButton());
        assertFalse(page.getElemControl().isDeleteButton());
        assertTrue(page.getElemControl().isZipLink());

    }

    /**
     * 画面構成要素の制御クラスのテスト. Approverの時.
     */
    @Test
    public void testInitializeApprover_Denied() {
        MockAbstractPage.RET_SYSTEM_ADMIN = false;
        MockAbstractPage.RET_PROJECT_ADMIN = false;
        MockAbstractPage.RET_GROUP_ADMIN = false;
        User user = new User();
        user.setEmpNo("USER004");
        MockAbstractPage.RET_USER = user;

        Project project = new Project();
        project.setProjectId("PJ1");
        MockAbstractPage.RET_PROJECT = project;

        Correspon expCorrespon = createCorrespon();
        List<Workflow> workflowList = createWorkflowList();

        expCorrespon.setWorkflows(workflowList);
        expCorrespon.setWorkflowStatus(WorkflowStatus.DENIED);

        MockCorresponService.RET_FIND = expCorrespon;

        MockCorresponGroupService.RET_FIND_CORRESPON_GROUP_USER_MAPPINGS = new ArrayList<CorresponGroupUserMapping>();
        MockCorresponGroupService.RET_SEARCH = new ArrayList<CorresponGroup>();
        MockUserService.RET_SEARCH = new ArrayList<ProjectUser>();

        // 必須のプロパティをセットして実行
        page.setId(expCorrespon.getId());
        page.initialize();

        // 画面構成要素(リンク、ボタン)の確認
        assertTrue(page.getElemControl().isCopyLink());
        assertFalse(page.getElemControl().isForwardLink());
        assertFalse(page.getElemControl().isUpdateLink());
        assertTrue(page.getElemControl().isPrintLink());
        assertTrue(page.getElemControl().isDownloadLink());
        assertFalse(page.getElemControl().isWorkFlowLink());
        assertFalse(page.getElemControl().isVerificationButton());
        assertFalse(page.getElemControl().isIssueButton());
        assertFalse(page.getElemControl().isRequestForApprovalButton());
        assertFalse(page.getElemControl().isDeleteButton());
        assertTrue(page.getElemControl().isZipLink());

    }

    /**
     * 画面構成要素の制御クラスのテスト. Approverの時.
     */
    @Test
    public void testInitializeApprover_Issued() {
        MockAbstractPage.RET_SYSTEM_ADMIN = false;
        MockAbstractPage.RET_PROJECT_ADMIN = false;
        MockAbstractPage.RET_GROUP_ADMIN = false;
        User user = new User();
        user.setEmpNo("USER004");
        MockAbstractPage.RET_USER = user;

        Project project = new Project();
        project.setProjectId("PJ1");
        MockAbstractPage.RET_PROJECT = project;

        Correspon expCorrespon = createCorrespon();
        List<Workflow> workflowList = createWorkflowList();

        expCorrespon.setWorkflows(workflowList);
        expCorrespon.setWorkflowStatus(WorkflowStatus.ISSUED);

        MockCorresponService.RET_FIND = expCorrespon;

        MockCorresponGroupService.RET_FIND_CORRESPON_GROUP_USER_MAPPINGS = new ArrayList<CorresponGroupUserMapping>();
        MockCorresponGroupService.RET_SEARCH = new ArrayList<CorresponGroup>();
        MockUserService.RET_SEARCH = new ArrayList<ProjectUser>();

        // 必須のプロパティをセットして実行
        page.setId(expCorrespon.getId());
        page.initialize();

        // 画面構成要素(リンク、ボタン)の確認
        assertTrue(page.getElemControl().isCopyLink());
        assertTrue(page.getElemControl().isForwardLink());

        // 発行済文書はApproverであっても更新できない
        assertFalse(page.getElemControl().isUpdateLink());

        assertTrue(page.getElemControl().isPrintLink());
        assertTrue(page.getElemControl().isDownloadLink());
        assertFalse(page.getElemControl().isWorkFlowLink());
        assertFalse(page.getElemControl().isVerificationButton());
        assertFalse(page.getElemControl().isIssueButton());
        assertFalse(page.getElemControl().isRequestForApprovalButton());
        assertFalse(page.getElemControl().isDeleteButton());
        assertTrue(page.getElemControl().isZipLink());
    }


    /**
     * 改訂リンクの検証
     */
    @Test
    public void testInitialize_revise() {
        MockAbstractPage.RET_SYSTEM_ADMIN = false;
        MockAbstractPage.RET_PROJECT_ADMIN = false;
        MockAbstractPage.RET_GROUP_ADMIN = false;
        User user = new User();
        user.setEmpNo("USER004");
        MockAbstractPage.RET_USER = user;

        Project project = new Project();
        project.setProjectId("PJ1");
        MockAbstractPage.RET_PROJECT = project;

        Correspon expCorrespon = createCorrespon();
        List<Workflow> workflowList = createWorkflowList();

        expCorrespon.setWorkflows(workflowList);
        expCorrespon.setWorkflowStatus(WorkflowStatus.ISSUED);

        MockCorresponService.RET_FIND = expCorrespon;

        MockCorresponGroupService.RET_FIND_CORRESPON_GROUP_USER_MAPPINGS = new ArrayList<CorresponGroupUserMapping>();
        MockCorresponGroupService.RET_SEARCH = new ArrayList<CorresponGroup>();
        MockUserService.RET_SEARCH = new ArrayList<ProjectUser>();
        page.setId(expCorrespon.getId());

        //  権限に関係無く、文書状態がCanceledの場合のみtrueとなる
        expCorrespon.setCorresponStatus(CorresponStatus.OPEN);
        page.initialize();
        assertFalse(page.getElemControl().isReviseLink());
        assertTrue(page.getElemControl().isZipLink());

        expCorrespon.setCorresponStatus(CorresponStatus.CLOSED);
        page.initialize();
        assertFalse(page.getElemControl().isReviseLink());
        assertTrue(page.getElemControl().isZipLink());
        expCorrespon.setCorresponStatus(CorresponStatus.CANCELED);
        page.initialize();
        assertTrue(page.getElemControl().isReviseLink());
        assertTrue(page.getElemControl().isZipLink());

    }

    /**
     * コレポン文書状態を変更するボタン(Open/Close/Cancel)の検証
     */
    @Test
    public void testInitialize_updateCorresponStatus() {
        asNormalUser();
        asNoRole();

        Project project = new Project();
        project.setProjectId("PJ1");
        MockAbstractPage.RET_PROJECT = project;

        Correspon expCorrespon = createCorrespon();
        List<Workflow> workflowList = createWorkflowList();

        expCorrespon.setWorkflows(workflowList);
        expCorrespon.setWorkflowStatus(WorkflowStatus.ISSUED);

        MockCorresponService.RET_FIND = expCorrespon;

        MockCorresponGroupService.RET_FIND_CORRESPON_GROUP_USER_MAPPINGS = new ArrayList<CorresponGroupUserMapping>();
        MockCorresponGroupService.RET_SEARCH = new ArrayList<CorresponGroup>();
        MockUserService.RET_SEARCH = new ArrayList<ProjectUser>();
        page.setId(expCorrespon.getId());

        //  Roleをもたない通常ユーザーの場合は操作できない
        expCorrespon.setCorresponStatus(CorresponStatus.OPEN);
        page.initialize();
        assertFalse(page.getElemControl().isOpenButton());
        assertFalse(page.getElemControl().isCloseButton());
        assertFalse(page.getElemControl().isCancelButton());
        assertTrue(page.getElemControl().isZipLink());

        //  Preparer、Checker、Approverの場合は操作できる
        User[] allows = { PREPARER, CHECKER_1, APPROVER };
        for (User u : allows) {
            MockAbstractPage.RET_USER = u;
            page.initialize();
            assertTrue(page.getElemControl().isOpenButton());
            assertTrue(page.getElemControl().isCloseButton());
            assertTrue(page.getElemControl().isCancelButton());
            assertTrue(page.getElemControl().isZipLink());
        }

        //  返信文書であっても操作できる
        expCorrespon.setParentCorresponId(100L);
        for (User u : allows) {
            MockAbstractPage.RET_USER = u;
            page.initialize();
            assertTrue(page.getElemControl().isOpenButton());
            assertTrue(page.getElemControl().isCloseButton());
            assertTrue(page.getElemControl().isCancelButton());
            assertTrue(page.getElemControl().isZipLink());
        }

        // Roleがなくても管理者であれば操作できる
        asNoRole();

        asSystemAdmin();
        page.initialize();
        assertTrue(page.getElemControl().isOpenButton());
        assertTrue(page.getElemControl().isCloseButton());
        assertTrue(page.getElemControl().isCancelButton());
        assertTrue(page.getElemControl().isZipLink());

        asProjectAdmin();
        page.initialize();
        assertTrue(page.getElemControl().isOpenButton());
        assertTrue(page.getElemControl().isCloseButton());
        assertTrue(page.getElemControl().isCancelButton());
        assertTrue(page.getElemControl().isZipLink());

        asGroupAdmin();
        page.initialize();
        assertTrue(page.getElemControl().isOpenButton());
        assertTrue(page.getElemControl().isCloseButton());
        assertTrue(page.getElemControl().isCancelButton());
        assertTrue(page.getElemControl().isZipLink());


        // 未発行の場合は操作できない
        expCorrespon.setWorkflowStatus(WorkflowStatus.DENIED);
        asSystemAdmin();
        page.initialize();
        assertFalse(page.getElemControl().isOpenButton());
        assertFalse(page.getElemControl().isCloseButton());
        assertFalse(page.getElemControl().isCancelButton());
        assertTrue(page.getElemControl().isZipLink());
    }

    /**
     * 返信要否・返信期限を編集可能とするかを判定するメソッドの検証
     */
    @Test
    public void testIsReplyRequiredEditable() {
        asNormalUser();
        asNoRole();

        Project project = new Project();
        project.setProjectId("PJ1");
        MockAbstractPage.RET_PROJECT = project;

        Correspon expCorrespon = createCorrespon();
        List<Workflow> workflowList = createWorkflowList();

        expCorrespon.setWorkflows(workflowList);
        expCorrespon.setWorkflowStatus(WorkflowStatus.ISSUED);

        MockCorresponService.RET_FIND = expCorrespon;

        MockCorresponGroupService.RET_FIND_CORRESPON_GROUP_USER_MAPPINGS = new ArrayList<CorresponGroupUserMapping>();
        MockCorresponGroupService.RET_SEARCH = new ArrayList<CorresponGroup>();
        MockUserService.RET_SEARCH = new ArrayList<ProjectUser>();
        page.setId(expCorrespon.getId());

        //  Roleをもたない通常ユーザーの場合は操作できない
        expCorrespon.setCorresponStatus(CorresponStatus.OPEN);
        page.initialize();
        assertFalse(page.getElemControl().isReplyRequiredEditable());
        assertTrue(page.getElemControl().isZipLink());

        //  Preparer、Checker、Approverの場合は操作できる
        User[] allows = { PREPARER, CHECKER_1, APPROVER };
        for (User u : allows) {
            MockAbstractPage.RET_USER = u;
            page.initialize();
            assertTrue(page.getElemControl().isReplyRequiredEditable());
            assertTrue(page.getElemControl().isZipLink());
        }

        // Roleがなくても管理者であれば操作できる
        asNoRole();

        asSystemAdmin();
        page.initialize();
        assertTrue(page.getElemControl().isReplyRequiredEditable());
        assertTrue(page.getElemControl().isZipLink());

        asProjectAdmin();
        page.initialize();
        assertTrue(page.getElemControl().isReplyRequiredEditable());
        assertTrue(page.getElemControl().isZipLink());

        asGroupAdmin();
        page.initialize();
        assertTrue(page.getElemControl().isReplyRequiredEditable());
        assertTrue(page.getElemControl().isZipLink());


        // 未発行の場合は操作できない
        expCorrespon.setWorkflowStatus(WorkflowStatus.DENIED);
        asSystemAdmin();
        page.initialize();
        assertFalse(page.getElemControl().isReplyRequiredEditable());
        assertTrue(page.getElemControl().isZipLink());
    }

    /**
     * 引用して返信リンクを操作可能とするかを判定するメソッドの検証
     */
    @Test
    public void testIsReplyWithPreviousCorresponLink() {
        asNormalUser();
        asNoRole();

        Project project = new Project();
        project.setProjectId("PJ1");
        MockAbstractPage.RET_PROJECT = project;

        Correspon expCorrespon = createCorrespon();
        List<Workflow> workflowList = createWorkflowList();

        expCorrespon.setWorkflows(workflowList);
        expCorrespon.setWorkflowStatus(WorkflowStatus.ISSUED);

        MockCorresponService.RET_FIND = expCorrespon;

        MockCorresponGroupService.RET_FIND_CORRESPON_GROUP_USER_MAPPINGS = new ArrayList<CorresponGroupUserMapping>();
        MockCorresponGroupService.RET_SEARCH = new ArrayList<CorresponGroup>();
        MockUserService.RET_SEARCH = new ArrayList<ProjectUser>();
        page.setId(expCorrespon.getId());
        page.setDetectedAddressUserId(ID_ATTENTION);
        page.setCorrespon(expCorrespon);

        //  Attentionをもたない通常ユーザーも操作できる
        expCorrespon.setCorresponStatus(CorresponStatus.OPEN);
        page.initialize();
        assertTrue(page.getElemControl().isReplyWithPreviousCorresponLink());
        assertTrue(page.getElemControl().isZipLink());

        //  Attentionの場合は操作できる
        asAttention();
        page.initialize();
        assertTrue(page.getElemControl().isReplyWithPreviousCorresponLink());
        assertTrue(page.getElemControl().isZipLink());

        //  Person in Chargeの場合も操作できる
        asPic();
        page.initialize();
        assertTrue(page.getElemControl().isReplyWithPreviousCorresponLink());
        assertTrue(page.getElemControl().isZipLink());

        // Roleがなくても管理者であれば操作できる
        asNoRole();

        asSystemAdmin();
        page.initialize();
        assertTrue(page.getElemControl().isReplyWithPreviousCorresponLink());
        assertTrue(page.getElemControl().isZipLink());

        asProjectAdmin();
        page.initialize();
        assertTrue(page.getElemControl().isReplyWithPreviousCorresponLink());
        assertTrue(page.getElemControl().isZipLink());

        asGroupAdmin();
        page.initialize();
        assertTrue(page.getElemControl().isReplyWithPreviousCorresponLink());
        assertTrue(page.getElemControl().isZipLink());

        //  CCの場合も操作できる
        asNormalUser();
        asCc();
        page.setDetectedAddressUserId(ID_CC);
        page.initialize();
        assertTrue(page.getElemControl().isReplyWithPreviousCorresponLink());
        assertTrue(page.getElemControl().isZipLink());

        // キャンセル済の場合は操作できない
        expCorrespon.setCorresponStatus(CorresponStatus.CANCELED);
        asSystemAdmin();
        page.initialize();
        assertFalse(page.getElemControl().isReplyWithPreviousCorresponLink());
        assertTrue(page.getElemControl().isZipLink());

        // クローズの場合は操作できる
        expCorrespon.setCorresponStatus(CorresponStatus.CLOSED);
        asSystemAdmin();
        page.initialize();
        assertTrue(page.getElemControl().isReplyWithPreviousCorresponLink());
        assertTrue(page.getElemControl().isZipLink());

        // 未発行の場合は操作できない
        expCorrespon.setWorkflowStatus(WorkflowStatus.DENIED);
        asSystemAdmin();
        page.initialize();
        assertFalse(page.getElemControl().isReplyWithPreviousCorresponLink());
        assertTrue(page.getElemControl().isZipLink());
    }

    /**
     * 改訂リンクを操作可能とするかを判定するメソッドの検証
     */
    @Test
    public void testIsReviceLink() {
        asNormalUser();
        asNoRole();

        Project project = new Project();
        project.setProjectId("PJ1");
        MockAbstractPage.RET_PROJECT = project;

        Correspon expCorrespon = createCorrespon();
        List<Workflow> workflowList = createWorkflowList();

        expCorrespon.setWorkflows(workflowList);
        expCorrespon.setWorkflowStatus(WorkflowStatus.ISSUED);

        MockCorresponService.RET_FIND = expCorrespon;

        MockCorresponGroupService.RET_FIND_CORRESPON_GROUP_USER_MAPPINGS = new ArrayList<CorresponGroupUserMapping>();
        MockCorresponGroupService.RET_SEARCH = new ArrayList<CorresponGroup>();
        MockUserService.RET_SEARCH = new ArrayList<ProjectUser>();
        page.setId(expCorrespon.getId());
        page.setDetectedAddressUserId(ID_ATTENTION);
        page.setReplied(expCorrespon);

        //  Attentionをもたない通常ユーザーも操作できる
        expCorrespon.setCorresponStatus(CorresponStatus.CANCELED);
        page.initialize();
        assertTrue(page.getElemControl().isReviseLink());
        assertTrue(page.getElemControl().isZipLink());

        //  Attentionの場合は操作できる
        asAttention();
        page.initialize();
        assertTrue(page.getElemControl().isReviseLink());
        assertTrue(page.getElemControl().isZipLink());

        //  Person in Chargeの場合も操作できる
        asPic();
        page.initialize();
        assertTrue(page.getElemControl().isReviseLink());
        assertTrue(page.getElemControl().isZipLink());

        // Roleがなくても管理者であれば操作できる
        asNoRole();

        asSystemAdmin();
        page.initialize();
        assertTrue(page.getElemControl().isReviseLink());
        assertTrue(page.getElemControl().isZipLink());

        asProjectAdmin();
        page.initialize();
        assertTrue(page.getElemControl().isReviseLink());
        assertTrue(page.getElemControl().isZipLink());

        asGroupAdmin();
        page.initialize();
        assertTrue(page.getElemControl().isReviseLink());
        assertTrue(page.getElemControl().isZipLink());

        //  CCの場合も操作できる
        asNormalUser();
        asCc();
        page.setDetectedAddressUserId(ID_CC);
        page.initialize();
        assertTrue(page.getElemControl().isReviseLink());
        assertTrue(page.getElemControl().isZipLink());

        // オープンの場合は操作できない
        expCorrespon.setCorresponStatus(CorresponStatus.OPEN);
        asSystemAdmin();
        page.initialize();
        assertFalse(page.getElemControl().isReviseLink());
        assertTrue(page.getElemControl().isZipLink());

        // クローズの場合は操作できない
        expCorrespon.setCorresponStatus(CorresponStatus.CLOSED);
        asSystemAdmin();
        page.initialize();
        assertFalse(page.getElemControl().isReviseLink());
        assertTrue(page.getElemControl().isZipLink());
    }

    /**
     * RequestForApprovalButtonのテスト(要望対応) DRAFT.
     */
    @Test
    public void testRequestForApprovalButton1() {
        setUpTestRequestForApprovalButton();

        Correspon expCorrespon = createCorrespon();
        List<Workflow> workflowList = createWorkflowList();
        expCorrespon.setWorkflows(workflowList);
        expCorrespon.setWorkflowStatus(WorkflowStatus.DRAFT);
        MockCorresponService.RET_FIND = expCorrespon;

        // 必須なプロパティをセットして実行
        page.setId(expCorrespon.getId());
        page.initialize();

        // 画面構成要素(リンク、ボタン)の確認
        assertTrue(page.getElemControl().isRequestForApprovalButton());
        assertTrue(page.getElemControl().isZipLink());
    }

    /**
     * RequestForApprovalButtonのテスト(要望対応) REQUEST_FOR_CHECK.
     */
    @Test
    public void testRequestForApprovalButton2() {
        setUpTestRequestForApprovalButton();

        Correspon expCorrespon = createCorrespon();
        List<Workflow> workflowList = createWorkflowList();
        expCorrespon.setWorkflows(workflowList);
        expCorrespon.setWorkflowStatus(WorkflowStatus.REQUEST_FOR_CHECK);
        MockCorresponService.RET_FIND = expCorrespon;

        // 必須なプロパティをセットして実行
        page.setId(expCorrespon.getId());
        page.initialize();

        // 画面構成要素(リンク、ボタン)の確認
        assertFalse(page.getElemControl().isRequestForApprovalButton());
        assertTrue(page.getElemControl().isZipLink());
    }

    /**
     * RequestForApprovalButtonのテスト(要望対応) UNDER_CONSIDERATION.
     */
    @Test
    public void testRequestForApprovalButton3() {
        setUpTestRequestForApprovalButton();

        Correspon expCorrespon = createCorrespon();
        List<Workflow> workflowList = createWorkflowList();
        expCorrespon.setWorkflows(workflowList);
        expCorrespon.setWorkflowStatus(WorkflowStatus.UNDER_CONSIDERATION);
        MockCorresponService.RET_FIND = expCorrespon;

        // 必須なプロパティをセットして実行
        page.setId(expCorrespon.getId());
        page.initialize();

        // 画面構成要素(リンク、ボタン)の確認
        assertFalse(page.getElemControl().isRequestForApprovalButton());
        assertTrue(page.getElemControl().isZipLink());
    }

    /**
     * RequestForApprovalButtonのテスト(要望対応) REQUEST_FOR_APPROVAL.
     */
    @Test
    public void testRequestForApprovalButton4() {
        setUpTestRequestForApprovalButton();

        Correspon expCorrespon = createCorrespon();
        List<Workflow> workflowList = createWorkflowList();
        expCorrespon.setWorkflows(workflowList);
        expCorrespon.setWorkflowStatus(WorkflowStatus.REQUEST_FOR_APPROVAL);
        MockCorresponService.RET_FIND = expCorrespon;

        // 必須なプロパティをセットして実行
        page.setId(expCorrespon.getId());
        page.initialize();

        // 画面構成要素(リンク、ボタン)の確認
        assertFalse(page.getElemControl().isRequestForApprovalButton());
        assertTrue(page.getElemControl().isZipLink());
    }

    /**
     * RequestForApprovalButtonのテスト(要望対応) ISSUED.
     */
    @Test
    public void testRequestForApprovalButton5() {
        setUpTestRequestForApprovalButton();

        Correspon expCorrespon = createCorrespon();
        List<Workflow> workflowList = createWorkflowList();
        expCorrespon.setWorkflows(workflowList);
        expCorrespon.setWorkflowStatus(WorkflowStatus.ISSUED);
        MockCorresponService.RET_FIND = expCorrespon;

        // 必須なプロパティをセットして実行
        page.setId(expCorrespon.getId());
        page.initialize();

        // 画面構成要素(リンク、ボタン)の確認
        assertFalse(page.getElemControl().isRequestForApprovalButton());
        assertTrue(page.getElemControl().isZipLink());
    }

    /**
     * RequestForApprovalButtonのテスト(要望対応) DENIED.
     */
    @Test
    public void testRequestForApprovalButton6() {
        setUpTestRequestForApprovalButton();

        Correspon expCorrespon = createCorrespon();
        List<Workflow> workflowList = createWorkflowList();
        expCorrespon.setWorkflows(workflowList);
        expCorrespon.setWorkflowStatus(WorkflowStatus.DENIED);
        MockCorresponService.RET_FIND = expCorrespon;

        // 必須なプロパティをセットして実行
        page.setId(expCorrespon.getId());
        page.initialize();

        // 画面構成要素(リンク、ボタン)の確認
        assertFalse(page.getElemControl().isRequestForApprovalButton());
        assertTrue(page.getElemControl().isZipLink());
    }

    /**
     * RequestForApprovalButtonのテスト(要望対応) DRAFT(Workflow未設定).
     */
    @Test
    public void testRequestForApprovalButton7() {
        setUpTestRequestForApprovalButton();

        Correspon expCorrespon = createCorrespon();
        expCorrespon.setWorkflows(new ArrayList<Workflow>());
        expCorrespon.setWorkflowStatus(WorkflowStatus.DRAFT);
        MockCorresponService.RET_FIND = expCorrespon;

        // 必須なプロパティをセットして実行
        page.setId(expCorrespon.getId());
        page.initialize();

        // 画面構成要素(リンク、ボタン)の確認
        assertFalse(page.getElemControl().isRequestForApprovalButton());
        assertTrue(page.getElemControl().isZipLink());
    }

    /**
     * RequestForApprovalButtonのテスト(要望対応) DRAFT(WorkflowにApprover未設定).
     */
    @Test
    public void testRequestForApprovalButton8() {
        setUpTestRequestForApprovalButton();

        List<Workflow> list = new ArrayList<Workflow>();

        Workflow wf = new Workflow();
        wf.setId(1L);
        wf.setUser(CHECKER_1);
        wf.setWorkflowType(WorkflowType.CHECKER);
        wf.setWorkflowProcessStatus(WorkflowProcessStatus.NONE);
        list.add(wf);

        wf = new Workflow();
        wf.setId(2L);
        wf.setUser(CHECKER_2);
        wf.setWorkflowType(WorkflowType.CHECKER);
        wf.setWorkflowProcessStatus(WorkflowProcessStatus.NONE);
        list.add(wf);

        wf = new Workflow();
        wf.setId(3L);
        wf.setUser(CHECKER_3);
        wf.setWorkflowType(WorkflowType.CHECKER);
        wf.setWorkflowProcessStatus(WorkflowProcessStatus.NONE);
        list.add(wf);

        Correspon expCorrespon = createCorrespon();
        expCorrespon.setWorkflows(list);
        expCorrespon.setWorkflowStatus(WorkflowStatus.DRAFT);
        MockCorresponService.RET_FIND = expCorrespon;

        // 必須なプロパティをセットして実行
        page.setId(expCorrespon.getId());
        page.initialize();

        // 画面構成要素(リンク、ボタン)の確認
        assertFalse(page.getElemControl().isRequestForApprovalButton());
        assertTrue(page.getElemControl().isZipLink());
    }

    /**
     * 返信リンクを操作可能とするかを判定するメソッドの検証
     */
    @Test
    public void testIsReply() {
        asNormalUser();
        asNoRole();

        Project project = new Project();
        project.setProjectId("PJ1");
        MockAbstractPage.RET_PROJECT = project;

        Correspon expCorrespon = createCorrespon();
        List<Workflow> workflowList = createWorkflowList();

        expCorrespon.setWorkflows(workflowList);
        expCorrespon.setWorkflowStatus(WorkflowStatus.ISSUED);

        MockCorresponService.RET_FIND = expCorrespon;

        MockCorresponGroupService.RET_FIND_CORRESPON_GROUP_USER_MAPPINGS = new ArrayList<CorresponGroupUserMapping>();
        MockCorresponGroupService.RET_SEARCH = new ArrayList<CorresponGroup>();
        MockUserService.RET_SEARCH = new ArrayList<ProjectUser>();
        page.setId(expCorrespon.getId());
        page.setDetectedAddressUserId(ID_ATTENTION);
        page.setCorrespon(expCorrespon);

        //  Attentionをもたない通常ユーザーも操作できる
        expCorrespon.setCorresponStatus(CorresponStatus.OPEN);
        page.initialize();
        assertTrue(page.getElemControl().isReply());
        assertTrue(page.getElemControl().isZipLink());


        //  Attentionの場合は操作できる
        asAttention();
        page.initialize();
        assertTrue(page.getElemControl().isReply());
        assertTrue(page.getElemControl().isZipLink());

        //  Person in Chargeの場合も操作できる
        asPic();
        page.initialize();
        assertTrue(page.getElemControl().isReply());
        assertTrue(page.getElemControl().isZipLink());

        // Roleがなくても管理者であれば操作できる
        asNoRole();

        asSystemAdmin();
        page.initialize();
        assertTrue(page.getElemControl().isReply());
        assertTrue(page.getElemControl().isZipLink());

        asProjectAdmin();
        page.initialize();
        assertTrue(page.getElemControl().isReply());
        assertTrue(page.getElemControl().isZipLink());

        asGroupAdmin();
        page.initialize();
        assertTrue(page.getElemControl().isReply());
        assertTrue(page.getElemControl().isZipLink());

        //  CCの場合も操作できる
        asNormalUser();
        asCc();
        page.setDetectedAddressUserId(ID_CC);
        page.initialize();
        assertTrue(page.getElemControl().isReply());
        assertTrue(page.getElemControl().isZipLink());

        // キャンセル済の場合は操作できない
        expCorrespon.setCorresponStatus(CorresponStatus.CANCELED);
        asSystemAdmin();
        page.initialize();
        assertFalse(page.getElemControl().isReply());
        assertTrue(page.getElemControl().isZipLink());

        // クローズの場合は操作できる
        expCorrespon.setCorresponStatus(CorresponStatus.CLOSED);
        asSystemAdmin();
        page.initialize();
        assertTrue(page.getElemControl().isReply());
        assertTrue(page.getElemControl().isZipLink());

        // 未発行の場合は操作できない
        expCorrespon.setWorkflowStatus(WorkflowStatus.DENIED);
        asSystemAdmin();
        page.initialize();
        assertFalse(page.getElemControl().isReply());
        assertTrue(page.getElemControl().isZipLink());
    }

    /**
     * 返信リンクを操作不可能.
     * 存在しないIDを指定した場合と同等の処理.
     */
    @Test
    public void testIsReplyNoData() {
        page.getElemControl().clear();
        assertFalse(page.getElemControl().isReply());
    }

    /**
     * ZIPリンクを操作不可能.
     * 存在しないIDを指定した場合.
     */
    @Test
    public void testIsZipLinkNoData() {
        page.getElemControl().clear();
        assertFalse(page.getElemControl().isZipLink());
    }

    private void setUpTestRequestForApprovalButton(){
        MockAbstractPage.RET_SYSTEM_ADMIN = false;
        MockAbstractPage.RET_PROJECT_ADMIN = false;
        MockAbstractPage.RET_GROUP_ADMIN = false;
        User user = new User();
        user.setEmpNo("99999");
        MockAbstractPage.RET_USER = user;

        Project project = new Project();
        project.setProjectId("PJ1");
        MockAbstractPage.RET_PROJECT = project;

        MockCorresponGroupService.RET_FIND_CORRESPON_GROUP_USER_MAPPINGS = new ArrayList<CorresponGroupUserMapping>();
        MockCorresponGroupService.RET_SEARCH = new ArrayList<CorresponGroup>();
        MockUserService.RET_SEARCH = new ArrayList<ProjectUser>();
    }

    public static class MockCorresponService extends MockUp<CorresponServiceImpl> {
        static Correspon RET_FIND;
        static String RET_REQUESTFORAPPROVAL;
        static String RET_UPDATE;
        static String RET_COPY;

        @Mock
        public Correspon find(Long id) throws ServiceAbortException {
            return RET_FIND;
        }

        @Mock
        public void requestForApproval(Correspon correspon) throws ServiceAbortException {
            RET_REQUESTFORAPPROVAL = "success for requestForApproval";
        }

        @Mock
        List<CorresponResponseHistory> findCorresponResponseHistory(Correspon correspon)
                throws ServiceAbortException {
            return new ArrayList<>();
        }
    }

    public static class MockCorresponReadStatusService extends MockUp<CorresponReadStatusServiceImpl> {
        static Long RET_ID;

        @Mock
        public Long updateReadStatusById(Long id , ReadStatus readStatus)
                throws ServiceAbortException {
            return RET_ID;
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
        public boolean isAnyGroupAdmin(String projectId) {
            ArgumentValidator.validateNotEmpty(projectId);

            return RET_GROUP_ADMIN;
        }

        @Mock
        public boolean isGroupAdmin(Long corresponGroupId) {
            ArgumentValidator.validateNotNull(corresponGroupId);

            return RET_GROUP_ADMIN;
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

    public static class MockCorresponGroupService extends MockUp<CorresponGroupServiceImpl> {
        static List<CorresponGroup> RET_SEARCH;
        static List<CorresponGroupUserMapping> RET_FIND_CORRESPON_GROUP_USER_MAPPINGS;

        @Mock
        public List<CorresponGroup> search(SearchCorresponGroupCondition condition)
                throws ServiceAbortException {
            return RET_SEARCH;
        }

        @Mock
        public List<CorresponGroupUserMapping> findCorresponGroupUserMappings() {
            return RET_FIND_CORRESPON_GROUP_USER_MAPPINGS;
        }
    }

    public static class MockUserService extends MockUp<UserServiceImpl> {
        static List<ProjectUser> RET_SEARCH;

        @Mock
        public List<ProjectUser> search(SearchUserCondition condition) throws ServiceAbortException {
            return RET_SEARCH;
        }
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
        c.setCorresponType(ct);
        c.setWorkflowStatus(WorkflowStatus.DRAFT);
        c.setCreatedAt(new GregorianCalendar(2009, 3, 1, 1, 1, 1).getTime());
        c.setUpdatedAt(new GregorianCalendar(2009, 3, 10, 10, 10, 10).getTime());
        User u = new User();
        u.setEmpNo("00001");
        u.setNameE("Test User");
        c.setCreatedBy(u);
        c.setUpdatedBy(u);
        c.setDeadlineForReply(new GregorianCalendar(2009, 4, 1).getTime());
        c.setCorresponStatus(CorresponStatus.OPEN);

        c.setAddressCorresponGroups(createAddresses());
        return c;
    }

    private List<AddressCorresponGroup> createAddresses() {
        List<AddressCorresponGroup> addresses = new ArrayList<AddressCorresponGroup>();

        List<AddressUser> users;
        List<PersonInCharge> pics;
        AddressCorresponGroup ag;
        CorresponGroup g;
        AddressUser au;
        PersonInCharge pic;

        ag = new AddressCorresponGroup();
        ag.setId(1L);
        g = new CorresponGroup();
        g.setId(1001L);
        ag.setCorresponGroup(g);
        ag.setAddressType(AddressType.TO);

        users = new ArrayList<AddressUser>();
        au = new AddressUser();
        au.setId(ID_ATTENTION);
        au.setUser(ATTENTION);
        users.add(au);

        pics = new ArrayList<PersonInCharge>();
        pic = new PersonInCharge();
        pic.setUser(PIC);
        pics.add(pic);
        au.setPersonInCharges(pics);

        ag.setUsers(users);
        addresses.add(ag);

        ag = new AddressCorresponGroup();
        ag.setId(2L);
        g = new CorresponGroup();
        g.setId(1002L);
        ag.setCorresponGroup(g);
        ag.setAddressType(AddressType.CC);

        users = new ArrayList<AddressUser>();
        au = new AddressUser();
        au.setId(ID_CC);
        au.setUser(CC);
        users.add(au);

        ag.setUsers(users);
        addresses.add(ag);

        return addresses;
    }

    /**
     * テスト用ワークフローリストの作成
     */
    private List<Workflow> createWorkflowList() {
        List<Workflow> list = new ArrayList<Workflow>();

        Workflow wf = new Workflow();
        wf.setId(1L);
        wf.setUser(CHECKER_1);
        wf.setWorkflowType(WorkflowType.CHECKER);
        wf.setWorkflowProcessStatus(WorkflowProcessStatus.NONE);
        list.add(wf);

        wf = new Workflow();
        wf.setId(2L);
        wf.setUser(CHECKER_2);
        wf.setWorkflowType(WorkflowType.CHECKER);
        wf.setWorkflowProcessStatus(WorkflowProcessStatus.NONE);
        list.add(wf);

        wf = new Workflow();
        wf.setId(3L);
        wf.setUser(CHECKER_3);
        wf.setWorkflowType(WorkflowType.CHECKER);
        wf.setWorkflowProcessStatus(WorkflowProcessStatus.NONE);
        list.add(wf);

        wf = new Workflow();
        wf.setId(4L);
        wf.setUser(APPROVER);
        wf.setWorkflowType(WorkflowType.APPROVER);
        wf.setWorkflowProcessStatus(WorkflowProcessStatus.NONE);
        list.add(wf);

        return list;
    }

    /**
     * テスト用ワークフローリストの作成
     */
    private List<Workflow> createWorkflowList_Case1() {
        List<Workflow> list = new ArrayList<Workflow>();

        Workflow wf = new Workflow();
        wf.setId(1L);
        wf.setUser(CHECKER_1);
        wf.setWorkflowType(WorkflowType.CHECKER);
        wf.setWorkflowProcessStatus(WorkflowProcessStatus.NONE);
        list.add(wf);

        wf = new Workflow();
        wf.setId(2L);
        wf.setUser(CHECKER_2);
        wf.setWorkflowType(WorkflowType.CHECKER);
        wf.setWorkflowProcessStatus(WorkflowProcessStatus.NONE);
        list.add(wf);

        wf = new Workflow();
        wf.setId(3L);
        wf.setUser(CHECKER_3);
        wf.setWorkflowType(WorkflowType.CHECKER);
        wf.setWorkflowProcessStatus(WorkflowProcessStatus.NONE);
        list.add(wf);

        wf = new Workflow();
        wf.setId(4L);
        wf.setUser(APPROVER);
        wf.setWorkflowType(WorkflowType.APPROVER);
        wf.setWorkflowProcessStatus(WorkflowProcessStatus.NONE);
        list.add(wf);

        return list;
    }

    /**
     * テスト用ワークフローリストの作成
     */
    private List<Workflow> createWorkflowList_Case2() {
        List<Workflow> list = new ArrayList<Workflow>();

        Workflow wf = new Workflow();
        wf.setId(1L);
        wf.setUser(CHECKER_1);
        wf.setWorkflowType(WorkflowType.CHECKER);
        wf.setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_CHECK);
        list.add(wf);

        wf = new Workflow();
        wf.setId(2L);
        wf.setUser(CHECKER_2);
        wf.setWorkflowType(WorkflowType.CHECKER);
        wf.setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_CHECK);
        list.add(wf);

        wf = new Workflow();
        wf.setId(3L);
        wf.setUser(CHECKER_3);
        wf.setWorkflowType(WorkflowType.CHECKER);
        wf.setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_CHECK);
        list.add(wf);

        wf = new Workflow();
        wf.setId(4L);
        wf.setUser(APPROVER);
        wf.setWorkflowType(WorkflowType.APPROVER);
        wf.setWorkflowProcessStatus(WorkflowProcessStatus.NONE);
        list.add(wf);

        return list;
    }

    /**
     * テスト用ワークフローリストの作成
     */
    private List<Workflow> createWorkflowList_Case3() {
        List<Workflow> list = new ArrayList<Workflow>();

        Workflow wf = new Workflow();
        wf.setId(1L);
        wf.setUser(CHECKER_1);
        wf.setWorkflowType(WorkflowType.CHECKER);
        list.add(wf);

        wf = new Workflow();
        wf.setId(2L);
        wf.setUser(CHECKER_2);
        wf.setWorkflowType(WorkflowType.CHECKER);
        list.add(wf);

        wf = new Workflow();
        wf.setId(3L);
        wf.setUser(CHECKER_3);
        wf.setWorkflowType(WorkflowType.CHECKER);
        list.add(wf);

        wf = new Workflow();
        wf.setId(4L);
        wf.setUser(APPROVER);
        wf.setWorkflowType(WorkflowType.APPROVER);
        wf.setWorkflowProcessStatus(WorkflowProcessStatus.NONE);
        list.add(wf);

        return list;
    }

    /**
     * テスト用ワークフローリストの作成
     */
    private List<Workflow> createWorkflowList_Case4() {
        List<Workflow> list = new ArrayList<Workflow>();

        Workflow wf = new Workflow();
        wf.setId(1L);
        wf.setUser(CHECKER_1);
        wf.setWorkflowType(WorkflowType.CHECKER);
        list.add(wf);

        wf = new Workflow();
        wf.setId(2L);
        wf.setUser(CHECKER_2);
        wf.setWorkflowType(WorkflowType.CHECKER);
        list.add(wf);

        wf = new Workflow();
        wf.setId(3L);
        wf.setUser(CHECKER_3);
        wf.setWorkflowType(WorkflowType.CHECKER);
        list.add(wf);

        wf = new Workflow();
        wf.setId(4L);
        wf.setUser(APPROVER);
        wf.setWorkflowType(WorkflowType.APPROVER);
        wf.setWorkflowProcessStatus(WorkflowProcessStatus.UNDER_CONSIDERATION);
        list.add(wf);

        return list;
    }

    /**
     * テスト用ワークフローリストの作成
     */
    private List<Workflow> createWorkflowList_Case5() {
        List<Workflow> list = new ArrayList<Workflow>();

        Workflow wf = new Workflow();
        wf.setId(1L);
        wf.setUser(CHECKER_1);
        wf.setWorkflowType(WorkflowType.CHECKER);
        wf.setWorkflowProcessStatus(WorkflowProcessStatus.CHECKED);
        list.add(wf);

        wf = new Workflow();
        wf.setId(2L);
        wf.setUser(CHECKER_2);
        wf.setWorkflowType(WorkflowType.CHECKER);
        wf.setWorkflowProcessStatus(WorkflowProcessStatus.CHECKED);
        list.add(wf);

        wf = new Workflow();
        wf.setId(3L);
        wf.setUser(CHECKER_3);
        wf.setWorkflowType(WorkflowType.CHECKER);
        wf.setWorkflowProcessStatus(WorkflowProcessStatus.UNDER_CONSIDERATION);
        list.add(wf);

        wf = new Workflow();
        wf.setId(4L);
        wf.setUser(APPROVER);
        wf.setWorkflowType(WorkflowType.APPROVER);
        wf.setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_APPROVAL);
        list.add(wf);

        return list;
    }

    /**
     * テスト用ワークフローリストの作成
     */
    private List<Workflow> createWorkflowList_Case6() {
        List<Workflow> list = new ArrayList<Workflow>();

        Workflow wf = new Workflow();
        wf.setId(1L);
        wf.setUser(CHECKER_1);
        wf.setWorkflowType(WorkflowType.CHECKER);
        list.add(wf);

        wf = new Workflow();
        wf.setId(2L);
        wf.setUser(CHECKER_2);
        wf.setWorkflowType(WorkflowType.CHECKER);
        list.add(wf);

        wf = new Workflow();
        wf.setId(3L);
        wf.setUser(CHECKER_3);
        wf.setWorkflowType(WorkflowType.CHECKER);
        list.add(wf);

        wf = new Workflow();
        wf.setId(4L);
        wf.setUser(APPROVER);
        wf.setWorkflowType(WorkflowType.APPROVER);
        wf.setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_APPROVAL);
        list.add(wf);

        return list;
    }

    /**
     * テスト用ワークフローリストの作成
     */
    private List<Workflow> createWorkflowList_Case7() {
        List<Workflow> list = new ArrayList<Workflow>();

        Workflow wf = new Workflow();
        wf.setId(1L);
        wf.setUser(CHECKER_1);
        wf.setWorkflowType(WorkflowType.CHECKER);
        wf.setWorkflowProcessStatus(WorkflowProcessStatus.CHECKED);
        list.add(wf);

        wf = new Workflow();
        wf.setId(2L);
        wf.setUser(CHECKER_2);
        wf.setWorkflowType(WorkflowType.CHECKER);
        wf.setWorkflowProcessStatus(WorkflowProcessStatus.CHECKED);
        list.add(wf);

        wf = new Workflow();
        wf.setId(3L);
        wf.setUser(CHECKER_3);
        wf.setWorkflowType(WorkflowType.CHECKER);
        wf.setWorkflowProcessStatus(WorkflowProcessStatus.CHECKED);
        list.add(wf);

        wf = new Workflow();
        wf.setId(4L);
        wf.setUser(APPROVER);
        wf.setWorkflowType(WorkflowType.APPROVER);
        wf.setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_APPROVAL);
        list.add(wf);

        return list;
    }

    private List<Workflow> createWorkflowList_Approver_Status_None() {
        List<Workflow> list = new ArrayList<Workflow>();

        Workflow wf = new Workflow();
        wf.setId(1L);
        wf.setUser(CHECKER_1);
        wf.setWorkflowType(WorkflowType.CHECKER);
        wf.setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_CHECK);
        list.add(wf);

        wf = new Workflow();
        wf.setId(2L);
        wf.setUser(CHECKER_2);
        wf.setWorkflowType(WorkflowType.CHECKER);
        wf.setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_CHECK);
        list.add(wf);

        wf = new Workflow();
        wf.setId(3L);
        wf.setUser(CHECKER_3);
        wf.setWorkflowType(WorkflowType.CHECKER);
        wf.setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_CHECK);
        list.add(wf);

        wf = new Workflow();
        wf.setId(4L);
        wf.setUser(APPROVER);
        wf.setWorkflowType(WorkflowType.APPROVER);
        wf.setWorkflowProcessStatus(WorkflowProcessStatus.NONE);
        list.add(wf);

        return list;
    }

}
