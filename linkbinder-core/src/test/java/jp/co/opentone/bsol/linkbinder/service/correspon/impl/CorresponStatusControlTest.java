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
package jp.co.opentone.bsol.linkbinder.service.correspon.impl;

import static org.junit.Assert.*;

import java.io.File;
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
import jp.co.opentone.bsol.framework.test.AbstractTestCase;
import jp.co.opentone.bsol.linkbinder.dto.AddressCorresponGroup;
import jp.co.opentone.bsol.linkbinder.dto.AddressUser;
import jp.co.opentone.bsol.linkbinder.dto.Correspon;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroup;
import jp.co.opentone.bsol.linkbinder.dto.CorresponType;
import jp.co.opentone.bsol.linkbinder.dto.PersonInCharge;
import jp.co.opentone.bsol.linkbinder.dto.ProjectUser;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.Workflow;
import jp.co.opentone.bsol.linkbinder.dto.code.AddressType;
import jp.co.opentone.bsol.linkbinder.dto.code.AddressUserType;
import jp.co.opentone.bsol.linkbinder.dto.code.CorresponStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowProcessStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowType;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.service.MockAbstractService;

/**
 * {@link CorresponStatusControl}のテストケース.
 * @author opentone
 */
public class CorresponStatusControlTest extends AbstractTestCase {

    /**
     * テスト対象.
     */
    @Resource
    private CorresponStatusControl service;

    /**
     * ファイル一時保存パスキー情報.
     */
    public static final String KEY_FILE_DIR_PATH = "dir.upload.temp";

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


    @BeforeClass
    public static void testSetUp() {
        new MockAbstractService();
    }

    @AfterClass
    public static void testTeardown() {
        new MockAbstractService().tearDown();;
    }

    /**
     * テスト前準備.
     */
    @Before
    public void setUp() {
        MockAbstractService.IS_GROUP_ADMIN = new ArrayList<Long>();
    }

    /**
     * 後始末.
     */
    @After
    public void tearDown() {
        MockAbstractService.IS_GROUP_ADMIN.clear();

        tearDownMockAbstractService();
    }

    private void tearDownMockAbstractService() {
        MockAbstractService.CURRENT_PROJECT_ID = null;
        MockAbstractService.CURRENT_PROJECT = null;
        MockAbstractService.CURRENT_USER = null;
        MockAbstractService.PROJECT_USER = null;
        MockAbstractService.CURRENT_PROJECT_USER = null;
        MockAbstractService.IS_GROUP_ADMIN = null;
        MockAbstractService.IS_ANY_GROUP_ADMIN = false;
        MockAbstractService.ACCESSIBLE_PROJECTS = null;
        MockAbstractService.VALIDATE_PROJECT_ID_EXCEPTION = null;
        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN = false;
    }

    /**
     * Preparerで承認状態が[DRAFT][Denied][Issued]以外.例外発生.
     */
    @Test
    public void testSaveByPreparer_WorkflowStatusError() throws Exception {
        User user = new User();
        user.setEmpNo("ZZA01");
        MockAbstractService.CURRENT_USER = user;

        Correspon correspon = createBasicCorresponData();

        correspon.setWorkflowStatus(WorkflowStatus.UNDER_CONSIDERATION);

        try {
            // テスト実行
            service.setup(correspon);
            fail("例外が発生していない");
        } catch (ServiceAbortException e) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CORRESPON_STATUS_INVALID, e.getMessageCode());

        }
    }

    /**
     * PreparerかつCheckerで承認状態が[Request for Check/Under Consideration]
     * 例外が発生してはいけない.
     */
    @Test
    public void testSaveByPreparerChecker_Verifying() throws Exception {
        User user = new User();
        user.setEmpNo("ZZA01");
        MockAbstractService.CURRENT_USER = user;

        Correspon correspon = createBasicCorresponData();

        //  自身をCheckerに登録
        Workflow w = new Workflow();
        w.setUser(user);
        w.setWorkflowType(WorkflowType.CHECKER);
        correspon.getWorkflows().add(0, w);

        WorkflowProcessStatus[] pStatuses = {
            WorkflowProcessStatus.REQUEST_FOR_CHECK,
            WorkflowProcessStatus.UNDER_CONSIDERATION,
        };

        for (WorkflowProcessStatus ps : pStatuses) {
            w.setWorkflowProcessStatus(ps);

            WorkflowStatus[] statuses = {
                WorkflowStatus.REQUEST_FOR_CHECK,
                WorkflowStatus.UNDER_CONSIDERATION,
                };

            for (WorkflowStatus s : statuses) {
                correspon.setWorkflowStatus(s);

                // テスト実行
                service.setup(correspon);

                assertEquals("" + s, WorkflowStatus.UNDER_CONSIDERATION, service.getWorkflowStatus());
                assertEquals("" + s, WorkflowProcessStatus.UNDER_CONSIDERATION, service.getWorkflowProcessStatus());
            }

        }
    }

    /**
     * PreparerかつApproverで承認状態が[Request for Check/Under Consideration/Request for Approval]
     * 例外が発生してはいけない.
     */
    @Test
    public void testSaveByPreparerApprover_Verifying() throws Exception {
        User user = new User();
        user.setEmpNo("ZZA01");
        MockAbstractService.CURRENT_USER = user;

        Correspon correspon = createBasicCorresponData();

        //  自身をApproverに登録
        Workflow w = new Workflow();
        w.setUser(user);
        w.setWorkflowType(WorkflowType.APPROVER);
        correspon.getWorkflows().set(correspon.getWorkflows().size() -1, w);

        WorkflowProcessStatus[] pStatuses = {
            WorkflowProcessStatus.REQUEST_FOR_APPROVAL,
            WorkflowProcessStatus.UNDER_CONSIDERATION,
        };

        for (WorkflowProcessStatus ps : pStatuses) {
            w.setWorkflowProcessStatus(ps);

            WorkflowStatus[] statuses = {
                WorkflowStatus.REQUEST_FOR_CHECK,
                WorkflowStatus.UNDER_CONSIDERATION,
                WorkflowStatus.REQUEST_FOR_APPROVAL,
            };

            for (WorkflowStatus s : statuses) {
                correspon.setWorkflowStatus(s);

                // テスト実行
                service.setup(correspon);

                assertEquals("" + s, WorkflowStatus.UNDER_CONSIDERATION, service.getWorkflowStatus());
                assertEquals("" + s, WorkflowProcessStatus.UNDER_CONSIDERATION, service.getWorkflowProcessStatus());
            }
        }
    }

    /**
     * Checkerで承認作業状態が[REQUEST_FOR_CHECK][UNDER_CONSIDERATION]以外.例外発生.
     */
    @Test
    public void testSaveByChecker_WorkflowProcessStatusError() throws Exception {
        MockAbstractService.CURRENT_USER = CHECKER_1;

        Correspon correspon = createBasicCorresponData();

        correspon.setWorkflowStatus(WorkflowStatus.UNDER_CONSIDERATION);

        try {
            // テスト実行
            service.setup(correspon);
            fail("例外が発生していない");
        } catch (ServiceAbortException e) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_WORKFLOW_STATUS_INVALID_FOR_CHECKER, e.getMessageCode());

        }

    }

    /**
     * Checkerで承認状態が[REQUEST_FOR_CHECK][UNDER_CONSIDERATION]以外.例外発生.
     */
    @Test
    public void testSaveByChecker_WorkflowStatusError() throws Exception {
        MockAbstractService.CURRENT_USER = CHECKER_2;

        Correspon correspon = createBasicCorresponData();

        correspon.setWorkflowStatus(WorkflowStatus.DENIED);

        try {
            // テスト実行
            service.setup(correspon);
            fail("例外が発生していない");
        } catch (ServiceAbortException e) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CORRESPON_STATUS_INVALID, e.getMessageCode());

        }

    }

    /**
     * Approverで承認作業状態が[UNDER_CONSIDERATION][REQUEST_FOR_APPROVAL]以外.例外発生.
     */
    @Test
    public void testSaveByApprover_WorkflowProcessStatusError() throws Exception {
        MockAbstractService.CURRENT_USER = APPROVER;

        Correspon correspon = createBasicCorresponData();
        List<Workflow> list = correspon.getWorkflows();
        list.get(list.size() - 1).setWorkflowProcessStatus(WorkflowProcessStatus.NONE);
        correspon.setWorkflows(list);

        correspon.setWorkflowStatus(WorkflowStatus.REQUEST_FOR_APPROVAL);

        try {
            // テスト実行
            service.setup(correspon);
            fail("例外が発生していない");
        } catch (ServiceAbortException e) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_WORKFLOW_STATUS_INVALID_FOR_APPROVER, e.getMessageCode());

        }

    }

    /**
     * Approverで承認状態が[REQUEST_FOR_CHECK]で承認作業状態が[REQUEST_FOR_APPROVAL]以外.例外発生.
     */
    @Test
    public void testSaveByApprover_WorkflowStatusError() throws Exception {
        MockAbstractService.CURRENT_USER = APPROVER;

        Correspon correspon = createBasicCorresponData();

        correspon.setWorkflowStatus(WorkflowStatus.DENIED);

        try {
            // テスト実行
            service.setup(correspon);
            fail("例外が発生していない");
        } catch (ServiceAbortException e) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CORRESPON_STATUS_INVALID, e.getMessageCode());

        }

    }

    /**
     * ProjectAdminで承認状態が[DRAFT].例外発生.
     */
    @Test
    public void testSaveByProjectAdmin_Draft() throws Exception {
        MockAbstractService.IS_PROJECT_ADMIN = true;

        Correspon correspon = createBasicCorresponData();

        User user = new User();
        user.setEmpNo("ZZA02");
        MockAbstractService.CURRENT_USER = user;

        ProjectUser pUser = new ProjectUser();
        pUser.setUser(user);
        MockAbstractService.CURRENT_PROJECT_USER = pUser;

        correspon.setWorkflowStatus(WorkflowStatus.DRAFT);

        try {
            // テスト実行
            service.setup(correspon);
            fail("例外が発生していない");
        } catch (ServiceAbortException e) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT, e.getMessageCode());

        }

    }

    /**
     * ProjectAdminかつPreparerで承認状態が[DRAFT]
     */
    @Test
    public void testSaveByProjectAdminPreparer_Draft() throws Exception {
        MockAbstractService.IS_PROJECT_ADMIN = true;

        Correspon correspon = createBasicCorresponData();

        User user = new User();
        user.setEmpNo("ZZA02");
        MockAbstractService.CURRENT_USER = user;
        correspon.setCreatedBy(user);

        ProjectUser pUser = new ProjectUser();
        pUser.setUser(user);
        MockAbstractService.CURRENT_PROJECT_USER = pUser;

        correspon.setWorkflowStatus(WorkflowStatus.DRAFT);

        service.setup(correspon);
        //  PreparerがDraft文書を保存してもステータスに変更が無いのでnullが返される
        assertNull(service.getWorkflowStatus());
        assertNull(service.getWorkflowProcessStatus());
    }

    /**
     * ProjectAdminかつPreparerで承認状態が[DRAFT]
     */
    @Test
    public void testSaveByProjectAdminPreparer_Denied() throws Exception {
        MockAbstractService.IS_PROJECT_ADMIN = true;

        Correspon correspon = createBasicCorresponData();

        User user = new User();
        user.setEmpNo("ZZA02");
        MockAbstractService.CURRENT_USER = user;
        correspon.setCreatedBy(user);

        ProjectUser pUser = new ProjectUser();
        pUser.setUser(user);
        MockAbstractService.CURRENT_PROJECT_USER = pUser;

        correspon.setWorkflowStatus(WorkflowStatus.DENIED);

        service.setup(correspon);
        assertEquals(WorkflowStatus.DRAFT, service.getWorkflowStatus());
        assertEquals(WorkflowProcessStatus.NONE, service.getWorkflowProcessStatus());
    }

    /**
     * GroupAdminで承認状態が[DRAFT].例外発生.
     */
    @Test
    public void testSaveByGroupAdmin_Draft() throws Exception {
        MockAbstractService.IS_ANY_GROUP_ADMIN = true;

        Correspon correspon = createBasicCorresponData();

        User user = new User();
        user.setEmpNo("ZZA02");
        MockAbstractService.CURRENT_USER = user;

        ProjectUser pUser = new ProjectUser();
        pUser.setUser(user);
        MockAbstractService.CURRENT_PROJECT_USER = pUser;

        correspon.setWorkflowStatus(WorkflowStatus.DRAFT);

        try {
            // テスト実行
            service.setup(correspon);
            fail("例外が発生していない");
        } catch (ServiceAbortException e) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT, e.getMessageCode());

        }

    }
    /**
     * GroupAdminかつPreparerで承認状態が[DRAFT]
     */
    @Test
    public void testSaveByGroupAdminPreparer_Draft() throws Exception {
        MockAbstractService.IS_ANY_GROUP_ADMIN = true;

        Correspon correspon = createBasicCorresponData();

        User user = new User();
        user.setEmpNo("ZZA02");
        MockAbstractService.CURRENT_USER = user;
        correspon.setCreatedBy(user);

        ProjectUser pUser = new ProjectUser();
        pUser.setUser(user);
        MockAbstractService.CURRENT_PROJECT_USER = pUser;

        correspon.setWorkflowStatus(WorkflowStatus.DRAFT);

        service.setup(correspon);
        //  PreparerがDraft文書を保存してもステータスに変更が無いのでnullが返される
        assertNull(service.getWorkflowStatus());
        assertNull(service.getWorkflowProcessStatus());
    }

    /**
     * GroupAdminかつPreparerで承認状態が[DENIED]
     */
    @Test
    public void testSaveByGroupAdminPreparer_Denied() throws Exception {
        MockAbstractService.IS_ANY_GROUP_ADMIN = true;

        Correspon correspon = createBasicCorresponData();

        User user = new User();
        user.setEmpNo("ZZA02");
        MockAbstractService.CURRENT_USER = user;
        correspon.setCreatedBy(user);

        ProjectUser pUser = new ProjectUser();
        pUser.setUser(user);
        MockAbstractService.CURRENT_PROJECT_USER = pUser;

        correspon.setWorkflowStatus(WorkflowStatus.DENIED);

        service.setup(correspon);
        assertEquals(WorkflowStatus.DRAFT, service.getWorkflowStatus());
        assertEquals(WorkflowProcessStatus.NONE, service.getWorkflowProcessStatus());
    }

    /**
     * NormalUserで実行.例外発生.
     */
    @Test
    public void testSaveByNormalUser() throws Exception {
        Correspon correspon = createBasicCorresponData();

        User user = new User();
        user.setEmpNo("ZZA02");
        MockAbstractService.CURRENT_USER = user;

        ProjectUser pUser = new ProjectUser();
        pUser.setUser(user);
        MockAbstractService.CURRENT_PROJECT_USER = pUser;

        correspon.setWorkflowStatus(WorkflowStatus.REQUEST_FOR_CHECK);

        try {
            // テスト実行
            service.setup(correspon);
            fail("例外が発生していない");
        } catch (ServiceAbortException e) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT, e.getMessageCode());

        }

    }

    /**
     * Preparerで承認状態が[DENIED]で、承認状態を[DRAFT]に承認作業状態を[NONE]に変更.
     */
    @Test
    public void testSaveByPreparer_Denied() throws Exception {
        Correspon correspon = createBasicCorresponData();

        User user = new User();
        user.setEmpNo("ZZA01");
        MockAbstractService.CURRENT_USER = user;

        ProjectUser pUser = new ProjectUser();
        pUser.setUser(user);
        MockAbstractService.CURRENT_PROJECT_USER = pUser;

        correspon.setWorkflowStatus(WorkflowStatus.DENIED);

        // テスト実行
        service.setup(correspon);

        assertEquals(WorkflowStatus.DRAFT, service.getWorkflowStatus());
        assertEquals(WorkflowProcessStatus.NONE, service.getWorkflowProcessStatus());

    }

    /**
     * Checkerで承認状態が[REQUEST_FOR_CHECK]で、承認状態を[UNDER_CONSIDERATION]に承認作業状態を[UNDER_CONSIDERATION]に変更.
     */
    @Test
    public void testSaveByChecker_Request_For_Check() throws Exception {
        Correspon correspon = createBasicCorresponData();

        MockAbstractService.CURRENT_USER = CHECKER_1;

        ProjectUser pUser = new ProjectUser();
        pUser.setUser(CHECKER_1);
        MockAbstractService.CURRENT_PROJECT_USER = pUser;

        correspon.setWorkflowStatus(WorkflowStatus.REQUEST_FOR_CHECK);
        correspon.getWorkflows().get(0).setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_CHECK);

        // テスト実行
        service.setup(correspon);

        assertEquals(WorkflowStatus.UNDER_CONSIDERATION, service.getWorkflowStatus());
        assertEquals(WorkflowProcessStatus.UNDER_CONSIDERATION, service.getWorkflowProcessStatus());

    }

    /**
     * Approverで承認状態が[REQUEST_FOR_APPROVAL]で、承認状態を[UNDER_CONSIDERATION]に承認作業状態を[UNDER_CONSIDERATION]に変更.
     */
    @Test
    public void testSaveByApprover_Request_For_Approval() throws Exception {
        Correspon correspon = createBasicCorresponData();

        MockAbstractService.CURRENT_USER = APPROVER;

        ProjectUser pUser = new ProjectUser();
        pUser.setUser(APPROVER);
        MockAbstractService.CURRENT_PROJECT_USER = pUser;

        correspon.setWorkflowStatus(WorkflowStatus.REQUEST_FOR_APPROVAL);
        correspon.getWorkflows().get(1).setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_APPROVAL);

        // テスト実行
        service.setup(correspon);

        assertEquals(WorkflowStatus.UNDER_CONSIDERATION, service.getWorkflowStatus());
        assertEquals(WorkflowProcessStatus.UNDER_CONSIDERATION, service.getWorkflowProcessStatus());

    }

    /**
     * Group Adminで承認状態が[REQUEST_FOR_APPROVAL]で、承認状態を[UNDER_CONSIDERATION]に変更し、承認作業状態は変わりなし.
     */
    @Test
    public void testSaveByGroupAdmin_Under_Consideration() throws Exception {

        Correspon correspon = createBasicCorresponData();
        MockAbstractService.IS_GROUP_ADMIN.add(correspon.getFromCorresponGroup().getId());

        User user = new User();
        user.setEmpNo("ZZA02");
        MockAbstractService.CURRENT_USER = user;

        ProjectUser pUser = new ProjectUser();
        pUser.setUser(user);
        MockAbstractService.CURRENT_PROJECT_USER = pUser;

        correspon.setWorkflowStatus(WorkflowStatus.REQUEST_FOR_APPROVAL);

        // テスト実行
        service.setup(correspon);

        assertEquals(WorkflowStatus.UNDER_CONSIDERATION, service.getWorkflowStatus());
        assertNull(service.getWorkflowProcessStatus());
        assertEquals(WorkflowProcessStatus.CHECKED, correspon.getWorkflows().get(0).getWorkflowProcessStatus());
        assertEquals(WorkflowProcessStatus.REQUEST_FOR_APPROVAL, correspon.getWorkflows().get(2).getWorkflowProcessStatus());

    }

    /**
     * System Adminで承認状態が[DENIED]で、承認状態を[DRAFT]に承認作業状態を[NONE]に変更.
     */
    @Test
    public void testSaveBySystemAdmin_Denied() throws Exception {
        MockAbstractService.IS_SYSTEM_ADMIN = true;

        Correspon correspon = createBasicCorresponData();

        User user = new User();
        user.setEmpNo("ZZA02");
        MockAbstractService.CURRENT_USER = user;

        ProjectUser pUser = new ProjectUser();
        pUser.setUser(user);
        MockAbstractService.CURRENT_PROJECT_USER = pUser;

        correspon.setWorkflowStatus(WorkflowStatus.DENIED);

        // テスト実行
        service.setup(correspon);

        assertEquals(WorkflowStatus.DRAFT, service.getWorkflowStatus());
        assertEquals(WorkflowProcessStatus.NONE, service.getWorkflowProcessStatus());

    }

    /**
     * System AdminかつCheckerで承認状態が[DENIED]
     * 更新すると、承認状態が[DRAFT]に、承認作業状態が[NONE]に変更される.
     */
    @Test
    public void testSaveBySystemAdminChecker_Denied() throws Exception {
        MockAbstractService.IS_SYSTEM_ADMIN = true;

        Correspon correspon = createBasicCorresponData();

        User user = new User();
        user.setEmpNo("ZZA02");
        MockAbstractService.CURRENT_USER = user;

        ProjectUser pUser = new ProjectUser();
        pUser.setUser(user);
        MockAbstractService.CURRENT_PROJECT_USER = pUser;

        correspon.setWorkflowStatus(WorkflowStatus.DENIED);

        // Checkerに自身を登録
        Workflow w = new Workflow();
        w.setUser(user);
        w.setWorkflowType(WorkflowType.CHECKER);
        w.setWorkflowProcessStatus(WorkflowProcessStatus.DENIED);
        correspon.getWorkflows().add(0, w);

        // テスト実行
        service.setup(correspon);

        assertEquals(WorkflowStatus.DRAFT, service.getWorkflowStatus());
        assertEquals(WorkflowProcessStatus.NONE, service.getWorkflowProcessStatus());

    }

    /**
     * System AdminかつApproverで承認状態が[DENIED]
     * 更新すると、承認状態が[DRAFT]に、承認作業状態が[NONE]に変更される.
     */
    @Test
    public void testSaveBySystemAdminApprover_Denied() throws Exception {
        MockAbstractService.IS_SYSTEM_ADMIN = true;

        Correspon correspon = createBasicCorresponData();

        User user = new User();
        user.setEmpNo("ZZA02");
        MockAbstractService.CURRENT_USER = user;

        ProjectUser pUser = new ProjectUser();
        pUser.setUser(user);
        MockAbstractService.CURRENT_PROJECT_USER = pUser;

        correspon.setWorkflowStatus(WorkflowStatus.DENIED);

        // Approverに自身を登録
        Workflow w = new Workflow();
        w.setUser(user);
        w.setWorkflowType(WorkflowType.APPROVER);
        w.setWorkflowProcessStatus(WorkflowProcessStatus.NONE);
        correspon.getWorkflows().set(correspon.getWorkflows().size() - 1, w);

        // テスト実行
        service.setup(correspon);

        assertEquals(WorkflowStatus.DRAFT, service.getWorkflowStatus());
        assertEquals(WorkflowProcessStatus.NONE, service.getWorkflowProcessStatus());
    }

    /**
     * コレポン文書更新処理に必要なデータを作成する
     *
     * @return
     */
    private Correspon createBasicCorresponData() throws Exception {

        Correspon correspon = new Correspon();

        User user = new User();
        user.setEmpNo("ZZA01");
        user.setNameE("Test User");

        // コレポン文書情報
        correspon.setId(new Long(90));
        correspon.setProjectId("PJ1");

        CorresponGroup corresponGroup = new CorresponGroup();
        corresponGroup.setId(new Long(1));
        correspon.setFromCorresponGroup(corresponGroup);

        CorresponType corresponType = new CorresponType();
        corresponType.setId(new Long(1));
        corresponType.setProjectCorresponTypeId(new Long(1));
        correspon.setCorresponType(corresponType);

        correspon.setSubject("更新後SUBJECT");
        correspon.setBody("更新後BODY");

        correspon.setCorresponStatus(CorresponStatus.OPEN);
        correspon.setDeadlineForReply(new GregorianCalendar(2009, 3, 3).getTime());
        correspon.setWorkflowStatus(WorkflowStatus.REQUEST_FOR_CHECK);

        correspon.setCreatedBy(user);
        correspon.setUpdatedBy(user);

        String path = SystemConfig.getValue(KEY_FILE_DIR_PATH);
        File file = new File(path + File.separatorChar + "upload_File1.xls");
        file.createNewFile();
        file = new File(path + File.separatorChar + "upload_File2.xls");
        file.createNewFile();

        correspon.setFile1FileId("12345upload.xls");
        correspon.setFile1FileName("upload.xls");
        correspon.setFile1Id(new Long(5));

        correspon.setFile2FileId("2test.txt");
        correspon.setFile2FileName("test.txt");
        correspon.setFile2Id(new Long(2));

        correspon.setFile3FileName("File.xls");

        correspon.setCustomField1Id(new Long(1));
        correspon.setCustomField1Value("PJ1");

        correspon.setCustomField2Id(new Long(1));
        correspon.setCustomField2Value("PJ1");

        List<AddressCorresponGroup> addressCorresponGroups = new ArrayList<AddressCorresponGroup>();
        AddressCorresponGroup addressCorresponGroup = new AddressCorresponGroup();

        corresponGroup = new CorresponGroup();
        corresponGroup.setId(new Long(1));
        addressCorresponGroup.setId(1L);
        addressCorresponGroup.setCorresponGroup(corresponGroup);
        addressCorresponGroup.setAddressType(AddressType.TO);

        List<AddressUser> addressUsers = new ArrayList<AddressUser>();
        AddressUser addressUser = new AddressUser();

        user = new User();
        user.setEmpNo("00001");
        user.setNameE("Test User");
        addressUser.setUser(user);
        addressUser.setAddressUserType(AddressUserType.ATTENTION);

        List<PersonInCharge> personInCharges = new ArrayList<PersonInCharge>();

        PersonInCharge personInCharge = new PersonInCharge();
        user = new User();
        user.setEmpNo("00002");
        user.setNameE("Test User");
        personInCharge.setUser(user);
        personInCharges.add(personInCharge);

        personInCharge = new PersonInCharge();
        user = new User();
        user.setEmpNo("00003");
        user.setNameE("Test User");
        personInCharge.setUser(user);
        personInCharges.add(personInCharge);

        addressUser.setPersonInCharges(personInCharges);

        addressUsers.add(addressUser);

        addressUser = new AddressUser();

        user = new User();
        user.setEmpNo("00002");
        user.setNameE("Test User");
        addressUser.setUser(user);
        addressUser.setAddressUserType(AddressUserType.NORMAL_USER);

        addressUsers.add(addressUser);



        addressCorresponGroup.setUsers(addressUsers);

        addressCorresponGroups.add(addressCorresponGroup);

        correspon.setAddressCorresponGroups(addressCorresponGroups);


        Workflow w;
        user = new User();
        List<Workflow> wfs = new ArrayList<Workflow>();

        w = new Workflow();
        w.setId(new Long(98));
        w.setCorresponId(new Long(90));
        w.setWorkflowNo(new Long(1));
        w.setVersionNo(1L);

        w.setUser(CHECKER_1);
        w.setCreatedBy(CHECKER_1);
        w.setUpdatedBy(CHECKER_1);

        w.setWorkflowType(WorkflowType.CHECKER);
        w.setWorkflowProcessStatus(WorkflowProcessStatus.CHECKED);

        wfs.add(w);

        w = new Workflow();
        w.setId(new Long(98));
        w.setCorresponId(new Long(90));
        w.setWorkflowNo(new Long(1));
        w.setVersionNo(1L);

        w.setUser(CHECKER_2);
        w.setCreatedBy(CHECKER_2);
        w.setUpdatedBy(CHECKER_2);

        w.setWorkflowType(WorkflowType.CHECKER);
        w.setWorkflowProcessStatus(WorkflowProcessStatus.UNDER_CONSIDERATION);

        wfs.add(w);

        w = new Workflow();
        w.setId(new Long(99));
        w.setCorresponId(new Long(90));
        w.setWorkflowNo(new Long(2));
        w.setVersionNo(1L);

        w.setUser(APPROVER);
        w.setCreatedBy(APPROVER);
        w.setUpdatedBy(APPROVER);

        w.setWorkflowType(WorkflowType.APPROVER);
        w.setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_APPROVAL);

        wfs.add(w);

        correspon.setWorkflows(wfs);

        return correspon;

    }
}
