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
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import javax.annotation.Resource;
import javax.faces.application.FacesMessage;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
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
import jp.co.opentone.bsol.linkbinder.dto.Correspon;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroup;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroupUser;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroupUserMapping;
import jp.co.opentone.bsol.linkbinder.dto.CorresponReadStatus;
import jp.co.opentone.bsol.linkbinder.dto.CorresponType;
import jp.co.opentone.bsol.linkbinder.dto.Project;
import jp.co.opentone.bsol.linkbinder.dto.ProjectUser;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.Workflow;
import jp.co.opentone.bsol.linkbinder.dto.WorkflowPattern;
import jp.co.opentone.bsol.linkbinder.dto.WorkflowTemplate;
import jp.co.opentone.bsol.linkbinder.dto.WorkflowTemplateUser;
import jp.co.opentone.bsol.linkbinder.dto.code.CorresponStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.ReadStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowProcessStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowType;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponGroupCondition;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchUserCondition;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
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
 * @author opentone
 */
public class WorkflowEditModuleTest extends AbstractTestCase {

    /**
     * ページクラス.
     */
    @Resource(name = "corresponPage")
    private CorresponPage page;

    /**
     * テスト対象.
     */
    @Resource
    private WorkflowEditModule workflowEditModule;

    /**
     * ログインユーザー
     */
    private User loginUser;

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
        new MockCorresponGroupService();
        new MockUserService();
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
        new MockCorresponGroupService().tearDown();
        new MockUserService().tearDown();
        new MockCorresponWorkflowService().tearDown();
        new MockCorresponReadStatusService().tearDown();
        new MockAbstractPage().tearDown();
        FacesContextMock.tearDown();
    }

    @Before
    public void setUp() {
        loginUser = new User();
        loginUser.setEmpNo("USER002");
        loginUser.setNameE("Taro Yamada");
        page.setCurrentUser(loginUser);

        MockAbstractPage.RET_PROJECT_ID = "PJ1";
    }

    @After
    public void tearDown() {
        MockCorresponWorkflowService.RET_SAVE = null;
        MockCorresponWorkflowService.RET_SEARCH_WORKFLOW_TEMPLATE_USER = null;
        MockCorresponWorkflowService.RET_APPLY = null;
        MockCorresponWorkflowService.CRT_DELETE_TEMPLATE = null;
        MockCorresponWorkflowService.CRT_SAVE_TEMPLATE_NAME = null;
        MockCorresponWorkflowService.CRT_SAVE_TEMPLATE_WORKFLOW = null;
        MockCorresponWorkflowService.CRT_SAVE_TEMPLATE_CORRESPON = null;
        MockCorresponGroupService.RET_FINDMENBERS = null;
        MockCorresponGroupService.RET_SEARCH = null;
        MockCorresponService.RET_FIND = null;
        MockUserService.RET_SEARCH = null;
        MockAbstractPage.RET_PROJECT_ID = null;

        MockCorresponReadStatusService.SET_CREATE = new ArrayList<CorresponReadStatus>();
        MockCorresponReadStatusService.SET_UPDATE = new ArrayList<CorresponReadStatus>();
        MockCorresponReadStatusService.SET_READ_CREATE = new CorresponReadStatus();
        MockCorresponReadStatusService.EX_UPDATE = null;

        page.setId(null);
    }

    /**
     * 初期化アクションのテスト.
     * @throws Exception
     */
    @Test
    public void testInitialize() throws Exception {
        setRequiredFields();

        User retUser = new User();
        retUser.setEmpNo("USER004");
        MockAbstractPage.RET_USER = retUser;

        Project project = new Project();
        project.setProjectId("PJ1");
        MockAbstractPage.RET_PROJECT = project;

        ProjectUser pu = new ProjectUser();
        pu.setProjectId(project.getProjectId());
        pu.setUser(retUser);
        MockAbstractPage.RET_PROJECT_USER = pu;

        // Serviceが返すダミーのコレポン文書を設定
        Correspon expected = new Correspon();
        expected.setId(99L);
        expected.setSubject("mock");

        User preperer = new User();
        preperer.setEmpNo("ZZA01");
        preperer.setRole("PA");
        expected.setCreatedBy(preperer);

        List<Workflow> listWorkflow = new ArrayList<Workflow>();
        Workflow workflow = new Workflow();
        listWorkflow.add(workflow);

        workflow = new Workflow();
        listWorkflow.add(workflow);

        workflow = new Workflow();
        listWorkflow.add(workflow);

        expected.setWorkflows(listWorkflow);

        expected.setProjectId("20");

        page.setCorrespon(expected);

        page.setId(expected.getId());

        // Serviceが返すダミーの活動単位を設定
        List<CorresponGroup> listGroup = createCorresponGroup();
        MockCorresponGroupService.RET_SEARCH = listGroup;
        // --------------------------------------

        // Serviceが返すダミーのユーザーを設定
        User u;
        List<ProjectUser> result = new ArrayList<ProjectUser>();
        pu = new ProjectUser();
        pu.setProjectId("PJ1");
        pu.setSecurityLevel("40");
        u = new User();
        u.setEmpNo("ZZA01");
        u.setLastName("Aaa");
        u.setNameE("aaa Aaa");
        pu.setUser(u);

        result.add(pu);

        pu = new ProjectUser();
        pu.setProjectId("PJ1");
        pu.setSecurityLevel("40");
        u = new User();
        u.setEmpNo("ZZA02");
        u.setLastName("Bbb");
        u.setNameE("bbb Bbb");
        pu.setUser(u);

        result.add(pu);

        List<User> listUser = new ArrayList<User>();

        for (ProjectUser projectUser : result) {
            listUser.add(projectUser.getUser());
        }

        MockUserService.RET_SEARCH = result;
        // ----------------------

        List<WorkflowTemplateUser> workflowTemplateUser = new ArrayList<WorkflowTemplateUser>();
        List<WorkflowTemplateUser> workflowTemplateUserKeep = new ArrayList<WorkflowTemplateUser>();
        WorkflowTemplateUser wt = new WorkflowTemplateUser();
        WorkflowTemplateUser wtKeep = new WorkflowTemplateUser();
        wt.setId(1L);
        wtKeep.setId(1L);
        wt.setProjectId("PJ1");
        wtKeep.setProjectId("PJ1");

        User user = new User();
        user.setEmpNo("ZZA05");
        user.setNameE("Keiichi Ogiwara");
        wt.setUser(user);
        wtKeep.setUser(user);

        User loginUser = new User();
        loginUser.setEmpNo("ZZA05");
        loginUser.setNameE("Keiichi Ogiwara");
        wt.setCreatedBy(loginUser);
        wtKeep.setCreatedBy(loginUser);
        wt.setUpdatedBy(loginUser);
        wtKeep.setUpdatedBy(loginUser);

        wt.setName("TEST");
        wtKeep.setName("TEST");
        wt.setVersionNo(1L);
        wtKeep.setVersionNo(1L);
        wt.setDeleteNo(0L);
        wtKeep.setDeleteNo(0L);

        workflowTemplateUser.add(wt);
        workflowTemplateUserKeep.add(wtKeep);

        MockCorresponWorkflowService.RET_SEARCH_WORKFLOW_TEMPLATE_USER = workflowTemplateUser;

        // テストで作成したリストを保持する
        List<CorresponGroup> listCg = new ArrayList<CorresponGroup>(listGroup);
        List<User> listUs = new ArrayList<User>(listUser);

        workflowEditModule.initialize();

        assertEquals(expected.toString(), page.getCorrespon().toString());
        assertEquals(listCg.toString(), page.getCorresponGroup().toString());
        assertEquals(listUs.toString(), page.getUser().toString());
        assertEquals(workflowTemplateUserKeep.toString(), page.getWorkflowTemplateUserList()
            .toString());
        assertEquals(preperer.toString(), page.getWorkflowForEditView().get(0).getUser().toString());
    }

    /**
     * 起動元からコレポン文書のIDが指定されない状態で実行した場合の検証.
     * @throws Exception
     */
    @Test
    public void testInitializedInvalidParam() throws Exception {
        setRequiredFields();

        // 期待されるメッセージをセット
        FacesContextMock.EXPECTED_MESSAGE =
                new FacesMessage(FacesMessage.SEVERITY_ERROR, FacesMessage.SEVERITY_ERROR
                    .toString(), createExpectedMessageString(Messages
                    .getMessageAsString(E_INVALID_PARAMETER), "Initialize"));
        try {
            workflowEditModule.initialize();
            fail("例外が発生していない");
        } catch (InvalidOperationRuntimeException e) {
            ServiceAbortException actual = (ServiceAbortException)e.getCause();
            assertEquals(ApplicationMessageCode.E_INVALID_PARAMETER, actual.getMessageCode());
        }
    }

    /**
     * 初期化アクションのテスト(「Delete/All Delete」表示).
     * @throws Exception
     */
    @Test
    public void testInitialize1() throws Exception {
        setRequiredFields();

        User retUser = new User();
        retUser.setEmpNo("USER004");
        MockAbstractPage.RET_USER = retUser;

        Project project = new Project();
        project.setProjectId("PJ1");
        MockAbstractPage.RET_PROJECT = project;

        ProjectUser pu = new ProjectUser();
        pu.setProjectId(project.getProjectId());
        pu.setUser(retUser);
        MockAbstractPage.RET_PROJECT_USER = pu;

        // Serviceが返すダミーのコレポン文書を設定
        Correspon expected = createCorrespon(99L);
        expected.getCreatedBy().setRole("PRE");
        expected.setProjectId("20");

        List<Workflow> workflowList = createWorkflowList();
        expected.setWorkflows(workflowList);
        expected.setWorkflowStatus(WorkflowStatus.UNDER_CONSIDERATION);

        CorresponType corresponType = expected.getCorresponType();
        WorkflowPattern workflowPattern = new WorkflowPattern();
        workflowPattern.setWorkflowCd("002");
        corresponType.setWorkflowPattern(workflowPattern);
        expected.setCorresponType(corresponType);

        page.setCorrespon(expected);
        page.setId(expected.getId());

        // Serviceが返すダミーの活動単位を設定
        List<CorresponGroup> listGroup = createCorresponGroup();
        MockCorresponGroupService.RET_SEARCH = listGroup;
        // --------------------------------------

        // Serviceが返すダミーのユーザーを設定
        User u;
        List<ProjectUser> result = new ArrayList<ProjectUser>();
        pu = new ProjectUser();
        pu.setProjectId("PJ1");
        pu.setSecurityLevel("40");
        u = new User();
        u.setEmpNo("ZZA01");
        u.setLastName("Aaa");
        u.setNameE("aaa Aaa");
        pu.setUser(u);

        result.add(pu);

        pu = new ProjectUser();
        pu.setProjectId("PJ1");
        pu.setSecurityLevel("40");
        u = new User();
        u.setEmpNo("ZZA02");
        u.setLastName("Bbb");
        u.setNameE("bbb Bbb");
        pu.setUser(u);

        result.add(pu);

        List<User> listUser = new ArrayList<User>();

        for (ProjectUser projectUser : result) {
            listUser.add(projectUser.getUser());
        }

        MockUserService.RET_SEARCH = result;
        // ----------------------

        // テストで作成したリストを保持する
        List<CorresponGroup> listCg = new ArrayList<CorresponGroup>(listGroup);
        List<User> listUs = new ArrayList<User>(listUser);

        List<WorkflowTemplateUser> workflowTemplateUser = new ArrayList<WorkflowTemplateUser>();
        List<WorkflowTemplateUser> workflowTemplateUserKeep = new ArrayList<WorkflowTemplateUser>();
        WorkflowTemplateUser wt = new WorkflowTemplateUser();
        WorkflowTemplateUser wtKeep = new WorkflowTemplateUser();
        wt.setId(1L);
        wtKeep.setId(1L);
        wt.setProjectId("PJ1");
        wtKeep.setProjectId("PJ1");

        User user = new User();
        user.setEmpNo("ZZA05");
        user.setNameE("Keiichi Ogiwara");
        wt.setUser(user);
        wtKeep.setUser(user);

        User loginUser = new User();
        loginUser.setEmpNo("ZZA05");
        loginUser.setNameE("Keiichi Ogiwara");
        wt.setCreatedBy(loginUser);
        wtKeep.setCreatedBy(loginUser);
        wt.setUpdatedBy(loginUser);
        wtKeep.setUpdatedBy(loginUser);

        wt.setName("TEST");
        wtKeep.setName("TEST");
        wt.setVersionNo(1L);
        wtKeep.setVersionNo(1L);
        wt.setDeleteNo(0L);
        wtKeep.setDeleteNo(0L);

        workflowTemplateUser.add(wt);
        workflowTemplateUserKeep.add(wtKeep);

        MockCorresponWorkflowService.RET_SEARCH_WORKFLOW_TEMPLATE_USER = workflowTemplateUser;
        // 期待されるメッセージをセット

        workflowEditModule.initialize();

        assertEquals(expected.toString(), page.getCorrespon().toString());
        assertEquals(listCg.toString(), page.getCorresponGroup().toString());
        assertEquals(listUs.toString(), page.getUser().toString());
        assertEquals(workflowTemplateUserKeep.toString(), page.getWorkflowTemplateUserList()
            .toString());
        assertEquals(expected.getCreatedBy().toString(), page.getWorkflowForEditView().get(0).getUser().toString());
    }

    /**
     * 初期化アクションのテスト(「Delete/All Delete」非表示).
     * @throws Exception
     */
    @Test
    public void testInitialize2() throws Exception {
        setRequiredFields();

        User retUser = new User();
        retUser.setEmpNo("USER004");
        MockAbstractPage.RET_USER = retUser;

        Project project = new Project();
        project.setProjectId("PJ1");
        MockAbstractPage.RET_PROJECT = project;

        ProjectUser pu = new ProjectUser();
        pu.setProjectId(project.getProjectId());
        pu.setUser(retUser);
        MockAbstractPage.RET_PROJECT_USER = pu;


        // Serviceが返すダミーのコレポン文書を設定
        Correspon expected = createCorrespon(99L);
        expected.setProjectId("20");

        List<Workflow> workflowList = createWorkflowList();
        expected.setWorkflows(workflowList);
        expected.setWorkflowStatus(WorkflowStatus.UNDER_CONSIDERATION);

        CorresponType corresponType = expected.getCorresponType();
        WorkflowPattern workflowPattern = new WorkflowPattern();
        workflowPattern.setWorkflowCd("001");
        corresponType.setWorkflowPattern(workflowPattern);

        page.setCorrespon(expected);
        page.setId(expected.getId());

        // Serviceが返すダミーの活動単位を設定
        List<CorresponGroup> listGroup = createCorresponGroup();
        MockCorresponGroupService.RET_SEARCH = listGroup;
        // --------------------------------------

        // Serviceが返すダミーのユーザーを設定
        User u;
        List<ProjectUser> result = new ArrayList<ProjectUser>();
        pu = new ProjectUser();
        pu.setProjectId("PJ1");
        pu.setSecurityLevel("40");
        u = new User();
        u.setEmpNo("ZZA01");
        u.setLastName("Aaa");
        u.setNameE("aaa Aaa");
        pu.setUser(u);

        result.add(pu);

        pu = new ProjectUser();
        pu.setProjectId("PJ1");
        pu.setSecurityLevel("40");
        u = new User();
        u.setEmpNo("ZZA02");
        u.setLastName("Bbb");
        u.setNameE("bbb Bbb");
        pu.setUser(u);

        result.add(pu);

        List<User> listUser = new ArrayList<User>();

        for (ProjectUser projectUser : result) {
            listUser.add(projectUser.getUser());
        }

        MockUserService.RET_SEARCH = result;
        // ----------------------

        // テストで作成したリストを保持する
        List<CorresponGroup> listCg = new ArrayList<CorresponGroup>(listGroup);
        List<User> listUs = new ArrayList<User>(listUser);

        List<WorkflowTemplateUser> workflowTemplateUser = new ArrayList<WorkflowTemplateUser>();
        List<WorkflowTemplateUser> workflowTemplateUserKeep = new ArrayList<WorkflowTemplateUser>();
        WorkflowTemplateUser wt = new WorkflowTemplateUser();
        WorkflowTemplateUser wtKeep = new WorkflowTemplateUser();
        wt.setId(1L);
        wtKeep.setId(1L);
        wt.setProjectId("PJ1");
        wtKeep.setProjectId("PJ1");

        User user = new User();
        user.setEmpNo("ZZA05");
        user.setNameE("Keiichi Ogiwara");
        wt.setUser(user);
        wtKeep.setUser(user);

        User loginUser = new User();
        loginUser.setEmpNo("ZZA05");
        loginUser.setNameE("Keiichi Ogiwara");
        wt.setCreatedBy(loginUser);
        wtKeep.setCreatedBy(loginUser);
        wt.setUpdatedBy(loginUser);
        wtKeep.setUpdatedBy(loginUser);

        wt.setName("TEST");
        wtKeep.setName("TEST");
        wt.setVersionNo(1L);
        wtKeep.setVersionNo(1L);
        wt.setDeleteNo(0L);
        wtKeep.setDeleteNo(0L);

        workflowTemplateUser.add(wt);
        workflowTemplateUserKeep.add(wtKeep);

        MockCorresponWorkflowService.RET_SEARCH_WORKFLOW_TEMPLATE_USER = workflowTemplateUser;

        workflowEditModule.initialize();

        assertEquals(expected.toString(), page.getCorrespon().toString());
        assertEquals(listCg.toString(), page.getCorresponGroup().toString());
        assertEquals(listUs.toString(), page.getUser().toString());
        assertEquals(workflowTemplateUserKeep.toString(), page.getWorkflowTemplateUserList()
            .toString());
    }

    /**
     * 承認フロー追加アクションのテスト.
     * @throws Exception
     */
    @Test
    public void testAddWorkflow() throws Exception {
        setRequiredFields();

        // Serviceが返すダミーのユーザーを設定
        User u;
        ProjectUser pu;
        List<ProjectUser> result = new ArrayList<ProjectUser>();
        pu = new ProjectUser();
        pu.setProjectId("PJ1");
        pu.setSecurityLevel("40");
        u = new User();
        u.setEmpNo("ZZA01");
        u.setLastName("Aaa");
        u.setNameE("aaa Aaa");
        pu.setUser(u);

        result.add(pu);

        pu = new ProjectUser();
        pu.setProjectId("PJ1");
        pu.setSecurityLevel("40");
        u = new User();
        u.setEmpNo("ZZA02");
        u.setLastName("Bbb");
        u.setNameE("bbb Bbb");
        pu.setUser(u);

        result.add(pu);

        List<User> listlistUser = new ArrayList<User>();

        for (ProjectUser projectUser : result) {
            listlistUser.add(projectUser.getUser());
        }

        MockUserService.RET_SEARCH = result;


        // 追加に必要な値をPAGEにセットする
        Workflow workflow = new Workflow();
        User user = new User();

        user.setEmpNo("TEST02");
        user.setNameE("JIRO TEST");

        page.setUserId("TEST02");
        page.setRole(1);

        workflow.setWorkflowType(WorkflowType.CHECKER);

        // コレポンIDをセットする
        page.setCorrespon(new Correspon());
        page.getCorrespon().setId(99L);

        workflow.setCorresponId(page.getCorrespon().getId());
        // バージョンナンバーをセットする
        workflow.setVersionNo((long) 1);

        // ワークフローに新規追加ユーザーを追加する
        workflow.setUser(user);

        // ログインユーザー
        User loginUser = new User();

        workflow.setCreatedBy(loginUser);
        workflow.setUpdatedBy(loginUser);

        // 承認フロー追加用
        List<User> listUser = new ArrayList<User>();
        List<Workflow> listWorkflow = new ArrayList<Workflow>();
        Workflow wf = new Workflow();

        // 承認フロー追加用画面表示用
        List<User> listUserIndex = new ArrayList<User>();
        List<Workflow> listWorkflowIndex = new ArrayList<Workflow>();
        Workflow wi = new Workflow();

        // 承認フロー保持用
        List<Workflow> listWorkflowKeep = new ArrayList<Workflow>();
        Workflow wfKeep = new Workflow();
        User usKeep = new User();

        // 承認フロー保持用画面表示用
        List<Workflow> listWorkflowIndexKeep = new ArrayList<Workflow>();
        Workflow wiKeep = new Workflow();
        User uiKeep = new User();

        // ------------------------------------------------
        // Preparerのデータ
        Workflow w = new Workflow();
        w.setUser(new User());

        // 表示するリストに追加
        listWorkflowIndex.add(0, w);
        listWorkflowIndexKeep.add(0, w);
        // ------------------------------------------------

        User us = new User();
        us.setEmpNo("TEST01");
        us.setNameE("TARO TEST");
        usKeep.setEmpNo("TEST01");
        usKeep.setNameE("TARO TEST");
        uiKeep.setEmpNo("TEST01");
        uiKeep.setNameE("TARO TEST");

        wf.setCorresponId(99L);
        wf.setVersionNo((long) 1);
        wf.setUser(us);
        wf.setWorkflowNo(1L);
        wf.setWorkflowType(WorkflowType.CHECKER);
        wf.setCreatedBy(loginUser);
        wf.setUpdatedBy(loginUser);
        wfKeep.setCorresponId(99L);
        wfKeep.setUser(usKeep);
        wfKeep.setWorkflowNo(1L);
        wfKeep.setWorkflowType(WorkflowType.CHECKER);
        wfKeep.setCreatedBy(loginUser);
        wfKeep.setUpdatedBy(loginUser);
        wfKeep.setVersionNo((long) 1);

        wi.setCorresponId(99L);
        wi.setVersionNo((long) 1);
        wi.setUser(us);
        wi.setWorkflowNo(2L);
        wi.setWorkflowType(WorkflowType.CHECKER);
        wi.setCreatedBy(loginUser);
        wi.setUpdatedBy(loginUser);
        wiKeep.setCorresponId(99L);
        wiKeep.setUser(usKeep);
        wiKeep.setWorkflowNo(2L);
        wiKeep.setWorkflowType(WorkflowType.CHECKER);
        wiKeep.setCreatedBy(loginUser);
        wiKeep.setUpdatedBy(loginUser);
        wiKeep.setVersionNo((long) 1);

        listWorkflow.add(wf);
        listUser.add(us);
        listWorkflowKeep.add(wfKeep);

        listWorkflowIndex.add(wi);
        listUserIndex.add(us);
        listWorkflowIndexKeep.add(wiKeep);

        wf = new Workflow();
        wi = new Workflow();
        us = new User();
        wfKeep = new Workflow();
        wiKeep = new Workflow();
        usKeep = new User();
        uiKeep = new User();

        us.setEmpNo("TEST02");
        us.setNameE("JIRO TEST");
        usKeep.setEmpNo("TEST02");
        usKeep.setNameE("JIRO TEST");
        uiKeep.setEmpNo("TEST02");
        uiKeep.setNameE("JIRO TEST");

        wf.setVersionNo((long) 1);
        wf.setCorresponId(99L);
        wf.setUser(us);
        wf.setWorkflowNo(2L);
        wf.setWorkflowType(WorkflowType.CHECKER);
        wf.setCreatedBy(loginUser);
        wf.setUpdatedBy(loginUser);
        wfKeep.setCorresponId(99L);
        wfKeep.setUser(usKeep);
        wfKeep.setWorkflowNo(2L);
        wfKeep.setWorkflowType(WorkflowType.CHECKER);
        wfKeep.setCreatedBy(loginUser);
        wfKeep.setUpdatedBy(loginUser);
        wfKeep.setVersionNo((long) 1);

        wi.setVersionNo((long) 1);
        wi.setCorresponId(99L);
        wi.setUser(us);
        wi.setWorkflowNo(3L);
        wi.setWorkflowType(WorkflowType.CHECKER);
        wi.setCreatedBy(loginUser);
        wi.setUpdatedBy(loginUser);
        wiKeep.setCorresponId(99L);
        wiKeep.setUser(usKeep);
        wiKeep.setWorkflowNo(3L);
        wiKeep.setWorkflowType(WorkflowType.CHECKER);
        wiKeep.setCreatedBy(loginUser);
        wiKeep.setUpdatedBy(loginUser);
        wiKeep.setVersionNo((long) 1);

        listWorkflow.add(wf);
        listUser.add(us);
        listWorkflowKeep.add(wfKeep);

        listWorkflowIndex.add(wi);
        listUserIndex.add(us);
        listWorkflowIndexKeep.add(wiKeep);

        wf = new Workflow();
        wi = new Workflow();
        us = new User();
        wfKeep = new Workflow();
        wiKeep = new Workflow();
        usKeep = new User();
        uiKeep = new User();

        us.setEmpNo("TEST03");
        us.setNameE("SABURO TEST");
        usKeep.setEmpNo("TEST03");
        usKeep.setNameE("SABURO TEST");
        uiKeep.setEmpNo("TEST03");
        uiKeep.setNameE("SABURO TEST");

        wf.setVersionNo((long) 1);
        wf.setCorresponId(99L);
        wf.setUser(us);
        wf.setWorkflowNo(3L);
        wf.setWorkflowType(WorkflowType.APPROVER);
        wf.setCreatedBy(loginUser);
        wf.setUpdatedBy(loginUser);
        wfKeep.setCorresponId(99L);
        wfKeep.setUser(us);
        wfKeep.setWorkflowNo(3L);
        wfKeep.setWorkflowType(WorkflowType.APPROVER);
        wfKeep.setCreatedBy(loginUser);
        wfKeep.setUpdatedBy(loginUser);
        wfKeep.setVersionNo((long) 1);

        wi.setVersionNo((long) 1);
        wi.setCorresponId(99L);
        wi.setUser(us);
        wi.setWorkflowNo(4L);
        wi.setWorkflowType(WorkflowType.APPROVER);
        wi.setCreatedBy(loginUser);
        wi.setUpdatedBy(loginUser);
        wiKeep.setCorresponId(99L);
        wiKeep.setUser(us);
        wiKeep.setWorkflowNo(4L);
        wiKeep.setWorkflowType(WorkflowType.APPROVER);
        wiKeep.setCreatedBy(loginUser);
        wiKeep.setUpdatedBy(loginUser);
        wiKeep.setVersionNo((long) 1);

        listWorkflow.add(wf);
        listUser.add(us);
        listWorkflowKeep.add(wfKeep);

        listWorkflowIndex.add(wi);
        listUserIndex.add(us);
        listWorkflowIndexKeep.add(wiKeep);

        page.setWorkflow(listWorkflow);
        page.setUser(listUser);

        page.setWorkflowForEditView(listWorkflowIndex);

        Long workflowNo = 4L;
        page.setWorkflowNo(workflowNo);

        // 承認フロー追加処理実行
        workflowEditModule.addWorkflow();

        // リストのサイズを比較する
        assertEquals(listWorkflowKeep.size(), page.getWorkflow().size() - 1);

        // リストの中身を比較する
        // Prepaerer
        assertEquals(listWorkflowKeep.get(0).toString(), page.getWorkflow().get(0).toString());

        assertEquals(listWorkflowKeep.get(0).getUser().getNameE(), page.getWorkflow().get(0)
            .getUser().getNameE());
        assertEquals(listWorkflowKeep.get(0).getUser().getEmpNo(), page.getWorkflow().get(0)
            .getUser().getEmpNo());
        assertEquals(listWorkflowKeep.get(0).getWorkflowType(), page.getWorkflow().get(0)
            .getWorkflowType());

        assertEquals(listWorkflowKeep.get(1).getUser().getNameE(), page.getWorkflow().get(1)
            .getUser().getNameE());
        assertEquals(listWorkflowKeep.get(1).getUser().getEmpNo(), page.getWorkflow().get(1)
            .getUser().getEmpNo());
        assertEquals(listWorkflowKeep.get(1).getWorkflowType(), page.getWorkflow().get(1)
            .getWorkflowType());
        assertEquals(listWorkflowKeep.get(1).getWorkflowNo().toString(), page.getWorkflow().get(1)
            .getWorkflowNo().toString());

        // 追加したデータ
        assertEquals("JIRO TEST", page.getWorkflow().get(2).getUser().getNameE());
        assertEquals("TEST02", page.getWorkflow().get(2).getUser().getEmpNo());
        assertEquals(WorkflowType.CHECKER, page.getWorkflow().get(2).getWorkflowType());
        assertEquals("3", page.getWorkflow().get(2).getWorkflowNo().toString());

        assertEquals(listWorkflowKeep.get(2).getUser().getNameE(), page.getWorkflow().get(3)
            .getUser().getNameE());
        assertEquals(listWorkflowKeep.get(2).getUser().getEmpNo(), page.getWorkflow().get(3)
            .getUser().getEmpNo());
        assertEquals(listWorkflowKeep.get(2).getWorkflowType(), page.getWorkflow().get(3)
            .getWorkflowType());
        assertFalse(listWorkflowKeep.get(2).getWorkflowNo().toString().equals(page.getWorkflow()
            .get(3).getWorkflowNo().toString()));

        // 画面表示用リストのサイズを比較する
        assertEquals(listWorkflowIndexKeep.size(), page.getWorkflowForEditView().size() - 1);

        // 画面表示用リストの中身を比較する
        // Prepaerer
        assertEquals(listWorkflowIndexKeep.get(0).toString(), page.getWorkflowForEditView().get(0)
            .toString());

        assertEquals(listWorkflowIndexKeep.get(0).getUser().getNameE(), page.getWorkflowForEditView()
            .get(0).getUser().getNameE());
        assertEquals(listWorkflowIndexKeep.get(0).getUser().getEmpNo(), page.getWorkflowForEditView()
            .get(0).getUser().getEmpNo());
        assertEquals(listWorkflowIndexKeep.get(0).getWorkflowType(), page.getWorkflowForEditView().get(0)
            .getWorkflowType());

        assertEquals(listWorkflowIndexKeep.get(1).getUser().getNameE(), page.getWorkflowForEditView()
            .get(1).getUser().getNameE());
        assertEquals(listWorkflowIndexKeep.get(1).getUser().getEmpNo(), page.getWorkflowForEditView()
            .get(1).getUser().getEmpNo());
        assertEquals(listWorkflowIndexKeep.get(1).getWorkflowType(), page.getWorkflowForEditView().get(1)
            .getWorkflowType());
        assertEquals(listWorkflowIndexKeep.get(1).getWorkflowNo().toString(), page
            .getWorkflowForEditView().get(1).getWorkflowNo().toString());

        assertEquals(listWorkflowIndexKeep.get(2).getUser().getNameE(), page.getWorkflowForEditView()
            .get(2).getUser().getNameE());
        assertEquals(listWorkflowIndexKeep.get(2).getUser().getEmpNo(), page.getWorkflowForEditView()
            .get(2).getUser().getEmpNo());
        assertEquals(listWorkflowIndexKeep.get(2).getWorkflowType(), page.getWorkflowForEditView().get(2)
            .getWorkflowType());
        assertEquals(listWorkflowIndexKeep.get(2).getWorkflowNo().toString(), page
            .getWorkflowForEditView().get(2).getWorkflowNo().toString());

        // 追加したデータ
        assertEquals("JIRO TEST", page.getWorkflowForEditView().get(3).getUser().getNameE());
        assertEquals("TEST02", page.getWorkflowForEditView().get(3).getUser().getEmpNo());
        assertEquals(WorkflowType.CHECKER, page.getWorkflowForEditView().get(3).getWorkflowType());
        assertEquals("4", page.getWorkflowForEditView().get(3).getWorkflowNo().toString());

        assertEquals(listWorkflowIndexKeep.get(3).getUser().getNameE(), page.getWorkflowForEditView()
            .get(4).getUser().getNameE());
        assertEquals(listWorkflowIndexKeep.get(3).getUser().getEmpNo(), page.getWorkflowForEditView()
            .get(4).getUser().getEmpNo());
        assertEquals(listWorkflowIndexKeep.get(3).getWorkflowType(), page.getWorkflowForEditView().get(4)
            .getWorkflowType());
        assertFalse(listWorkflowIndexKeep.get(3).getWorkflowNo().toString().equals(page
            .getWorkflowForEditView().get(4).getWorkflowNo().toString()));
    }

    /**
     * 承認フロー追加処理のテスト(承認フロー設定が存在しない)
     */
    @Test
    public void testAddWorkflowNoUser() throws Exception {
        setRequiredFields();

        // Serviceが返すダミーのユーザーを設定
        User u;
        ProjectUser pu;
        List<ProjectUser> result = new ArrayList<ProjectUser>();
        pu = new ProjectUser();
        pu.setProjectId("PJ1");
        pu.setSecurityLevel("40");
        u = new User();
        u.setEmpNo("ZZA01");
        u.setLastName("Aaa");
        u.setNameE("aaa Aaa");
        pu.setUser(u);

        result.add(pu);

        pu = new ProjectUser();
        pu.setProjectId("PJ1");
        pu.setSecurityLevel("40");
        u = new User();
        u.setEmpNo("ZZA02");
        u.setLastName("Bbb");
        u.setNameE("bbb Bbb");
        pu.setUser(u);

        result.add(pu);

        List<User> listlistUser = new ArrayList<User>();

        for (ProjectUser projectUser : result) {
            listlistUser.add(projectUser.getUser());
        }

        MockUserService.RET_SEARCH = result;


        // 追加に必要な値をPAGEにセットする
        Workflow workflow = new Workflow();
        User user = new User();

        user.setEmpNo("TEST02");
        user.setNameE("JIRO TEST");

        page.setUserId("TEST02");
        page.setRole(1);

        workflow.setWorkflowType(WorkflowType.CHECKER);

        // コレポンIDをセットする
        page.setCorrespon(new Correspon());
        page.getCorrespon().setId(99L);
        workflow.setCorresponId(page.getCorrespon().getId());

        // ワークフローに新規追加ユーザーを追加する
        workflow.setUser(user);

        // ログインユーザー
        User loginUser = new User();

        workflow.setCreatedBy(loginUser);
        workflow.setUpdatedBy(loginUser);

        List<User> listUser = new ArrayList<User>();

        User us = new User();
        us.setEmpNo("TEST01");
        us.setNameE("TARO TEST");
        listUser.add(us);

        us = new User();
        us.setEmpNo("TEST02");
        us.setNameE("JIRO TEST");
        listUser.add(us);

        us = new User();
        us.setEmpNo("TEST03");
        us.setNameE("SABURO TEST");
        listUser.add(us);

        page.setUser(listUser);

        List<Workflow> listWorkflow = new ArrayList<Workflow>();

        // 画面表示用
        List<Workflow> listWorkflowIndex = new ArrayList<Workflow>();
        List<Workflow> listWiKeep = new ArrayList<Workflow>();

        // ------------------------------------------------
        // Preparerのデータ
        Workflow w = new Workflow();
        w.setUser(new User());
        w.setWorkflowNo(1L);
        Workflow wKeep = new Workflow();
        wKeep.setUser(new User());
        wKeep.setWorkflowNo(1L);

        // 表示するリストに追加
        listWorkflowIndex.add(0, w);
        listWiKeep.add(0, wKeep);

        // ------------------------------------------------

        page.setWorkflow(listWorkflow);
        page.setWorkflowForEditView(listWorkflowIndex);

        // 追加するナンバーの最大値
        Long workflowNo = 10L;

        page.setWorkflowNo(workflowNo);

        // 作成したリストを保持する
        List<Workflow> listWf = new ArrayList<Workflow>(listWorkflow);

        // 承認フロー追加処理実行
        workflowEditModule.addWorkflow();

        // リストのサイズを比較する
        assertEquals(listWf.size(), page.getWorkflow().size() - 1);

        assertEquals(String.valueOf(1L), page.getWorkflow().get(0).getWorkflowNo().toString());
        assertEquals("TEST02", page.getWorkflow().get(0).getUser().getEmpNo());
        assertEquals("JIRO TEST", page.getWorkflow().get(0).getUser().getNameE());
        assertEquals("TEST02", page.getWorkflow().get(0).getUser().getUserId());
        assertEquals(Integer.toString(1), page.getWorkflow().get(0).getWorkflowType().getValue()
            .toString());
        assertEquals(String.valueOf(99L), page.getWorkflow().get(0).getCorresponId().toString());

        // 画面表示用
        assertEquals(listWiKeep.size(), page.getWorkflowForEditView().size() - 1);
        // Preperer
        assertEquals(listWiKeep.get(0).toString(), page.getWorkflowForEditView().get(0).toString());

        assertEquals(String.valueOf(2L), page.getWorkflowForEditView().get(1).getWorkflowNo().toString());
        assertEquals("TEST02", page.getWorkflowForEditView().get(1).getUser().getEmpNo());
        assertEquals("JIRO TEST", page.getWorkflowForEditView().get(1).getUser().getNameE());
        assertEquals("TEST02", page.getWorkflowForEditView().get(1).getUser().getUserId());
        assertEquals(Integer.toString(1), page.getWorkflowForEditView().get(1).getWorkflowType()
            .getValue().toString());
        assertEquals(String.valueOf(99L), page.getWorkflowForEditView().get(1).getCorresponId()
            .toString());
    }

    /**
     * 承認フロー全削除アクションのテスト.
     * @throws Exception
     */
    @Test
    public void testAllDelete() throws Exception {
        setRequiredFields();

        // 削除に必要な値をPAGEにセットする
        List<Workflow> listWorkflow = new ArrayList<Workflow>();
        List<Workflow> listWorkflowIndex = new ArrayList<Workflow>();

        Workflow wf = new Workflow();
        Workflow wi = new Workflow();

        User us = new User();

        us.setEmpNo("TEST00");
        us.setNameE("PRE TEST");
        wi.setUser(us);
        listWorkflowIndex.add(wi);
        wi = new Workflow();

        us = new User();
        us.setEmpNo("TEST01");
        us.setNameE("TARO TEST");
        wf.setUser(us);
        wi.setUser(us);
        listWorkflow.add(wf);
        listWorkflowIndex.add(wi);

        wf = new Workflow();
        us = new User();
        us.setEmpNo("TEST02");
        us.setNameE("JIRO TEST");
        wf.setUser(us);
        wi.setUser(us);
        listWorkflow.add(wf);
        listWorkflowIndex.add(wi);

        wf = new Workflow();
        us = new User();
        us.setEmpNo("TEST03");
        us.setNameE("SABURO TEST");
        wf.setUser(us);
        wi.setUser(us);
        listWorkflow.add(wf);
        listWorkflowIndex.add(wi);

        wf = new Workflow();
        us = new User();
        us.setEmpNo("TEST04");
        us.setNameE("SIRO TEST");
        wf.setUser(us);
        wi.setUser(us);
        listWorkflow.add(wf);
        listWorkflowIndex.add(wi);

        page.setWorkflow(listWorkflow);
        page.setWorkflowForEditView(listWorkflowIndex);

        User preperer = new User();
        preperer.setEmpNo("ZZA18");
        preperer.setNameE("Michele Jackson");
        Correspon correspon = new Correspon();
        correspon.setCreatedBy(preperer);
        page.setCorrespon(correspon);

        // 削除処理実行
        workflowEditModule.allDelete();

        assertEquals(page.getWorkflow().size(), 0);
        // Preparer分は必ず残る
        assertEquals(page.getWorkflowForEditView().size(), 1);
    }

    /**
     * 承認フローを全て削除する処理のテスト(承認フローが存在しない)
     */
    @Test
    public void testAllDelete2() throws Exception {
        setRequiredFields();

        // 削除に必要な値をPAGEにセットする
        List<Workflow> listWorkflow = new ArrayList<Workflow>();

        page.setWorkflow(listWorkflow);

        page.setWorkflowForEditView(new ArrayList<Workflow>());
        User loginUser = new User();
        loginUser.setEmpNo("ZZA05");
        loginUser.setNameE("Keiichi Ogiwara");

        Correspon correspon = new Correspon();
        correspon.setCreatedBy(loginUser);
        page.setCorrespon(correspon);

        // 削除処理実行
        workflowEditModule.allDelete();

        assertEquals(page.getWorkflow().size(), 0);
        // Preparer分は必ず残る
        assertEquals(page.getWorkflowForEditView().size(), 1);
    }

    /**
     * 承認フロー削除アクションのテスト.
     * @throws Exception
     */
    @Test
    public void testDelete() throws Exception {
        setRequiredFields();

        // 削除に必要な値をPAGEにセットする
        // 削除処理実行用
        List<Workflow> listWorkflow = new ArrayList<Workflow>();
        List<Workflow> listWorkflowIndex = new ArrayList<Workflow>();
        Workflow wf = new Workflow();
        Workflow wi = new Workflow();

        // 削除処理保持用
        List<Workflow> listWorkflowKeep = new ArrayList<Workflow>();
        Workflow wfKeep = new Workflow();
        User usKeep = new User();
        // 削除処理保持用画面表示用
        List<Workflow> listWorkflowIndexKeep = new ArrayList<Workflow>();
        Workflow wiKeep = new Workflow();
        User uiKeep = new User();

        User us = new User();

        // 画面表示用ワークフローにPrepererを追加
        us.setEmpNo("TEST00");
        us.setNameE("PRE TEST");

        uiKeep.setEmpNo("TEST00");
        uiKeep.setNameE("PRE TEST");

        usKeep.setEmpNo("TEST00");
        usKeep.setNameE("PRE TEST");
        wi.setUser(us);
        wi.setWorkflowNo(1L);

        wiKeep.setUser(us);
        wiKeep.setWorkflowNo(1L);

        listWorkflowIndex.add(wi);
        listWorkflowIndexKeep.add(wiKeep);

        wi = new Workflow();
        us = new User();
        wiKeep = new Workflow();
        uiKeep = new User();

        us.setEmpNo("TEST01");
        us.setNameE("TARO TEST");
        usKeep.setEmpNo("TEST01");
        usKeep.setNameE("TARO TEST");
        uiKeep.setEmpNo("TEST01");
        uiKeep.setNameE("TARO TEST");

        wf.setUser(us);
        wf.setWorkflowNo(1L);
        wfKeep.setUser(usKeep);
        wfKeep.setWorkflowNo(1L);

        wi.setUser(us);
        wi.setWorkflowNo(2L);
        wiKeep.setUser(usKeep);
        wiKeep.setWorkflowNo(2L);

        listWorkflow.add(wf);
        listWorkflowKeep.add(wfKeep);
        listWorkflowIndex.add(wi);
        listWorkflowIndexKeep.add(wiKeep);

        wf = new Workflow();
        wi = new Workflow();
        us = new User();
        wfKeep = new Workflow();
        usKeep = new User();
        wiKeep = new Workflow();
        uiKeep = new User();

        us.setEmpNo("TEST02");
        us.setNameE("JIRO TEST");
        usKeep.setEmpNo("TEST02");
        usKeep.setNameE("JIRO TEST");
        uiKeep.setEmpNo("TEST02");
        uiKeep.setNameE("JIRO TEST");

        wf.setUser(us);
        wf.setWorkflowNo(2L);

        wi.setUser(us);
        wi.setWorkflowNo(3L);
        wiKeep.setUser(uiKeep);
        wiKeep.setWorkflowNo(3L);

        wfKeep.setUser(usKeep);
        wfKeep.setWorkflowNo(2L);
        listWorkflow.add(wf);
        listWorkflowKeep.add(wfKeep);
        listWorkflowIndex.add(wi);
        listWorkflowIndexKeep.add(wiKeep);

        wf = new Workflow();
        wi = new Workflow();
        us = new User();
        wfKeep = new Workflow();
        usKeep = new User();
        wiKeep = new Workflow();
        uiKeep = new User();

        us.setEmpNo("TEST03");
        us.setNameE("SABURO TEST");
        usKeep.setEmpNo("TEST03");
        usKeep.setNameE("SABURO TEST");
        uiKeep.setEmpNo("TEST03");
        uiKeep.setNameE("SABURO TEST");

        wf.setUser(us);
        wf.setWorkflowNo(3L);

        wi.setUser(us);
        wi.setWorkflowNo(4L);
        wiKeep.setUser(uiKeep);
        wiKeep.setWorkflowNo(4L);

        wfKeep.setUser(usKeep);
        wfKeep.setWorkflowNo(3L);
        listWorkflow.add(wf);
        listWorkflowKeep.add(wfKeep);
        listWorkflowIndex.add(wi);
        listWorkflowIndexKeep.add(wiKeep);

        wf = new Workflow();
        wi = new Workflow();
        us = new User();
        wfKeep = new Workflow();
        usKeep = new User();
        wiKeep = new Workflow();
        uiKeep = new User();

        us.setEmpNo("TEST04");
        us.setNameE("SIRO TEST");
        usKeep.setEmpNo("TEST04");
        usKeep.setNameE("SIRO TEST");
        uiKeep.setEmpNo("TEST04");
        uiKeep.setNameE("SIRO TEST");

        wf.setUser(us);
        wf.setWorkflowNo(4L);

        wi.setUser(us);
        wi.setWorkflowNo(5L);
        wiKeep.setUser(uiKeep);
        wiKeep.setWorkflowNo(5L);

        wfKeep.setUser(usKeep);
        wfKeep.setWorkflowNo(4L);
        listWorkflow.add(wf);
        listWorkflowKeep.add(wfKeep);
        listWorkflowIndex.add(wi);
        listWorkflowIndexKeep.add(wiKeep);

        page.setWorkflow(listWorkflow);

        page.setWorkflowForEditView(listWorkflowIndex);

        DataModel dm = new ListDataModel();
        dm.setWrappedData(page.getWorkflowForEditView());
        // 削除する要素数を指定
        int del = 2;
        dm.setRowIndex(del);

        page.setWorkflowModel(dm);

        // 削除処理実行
        workflowEditModule.delete();

        // リストのサイズを比較する
        assertEquals(listWorkflowKeep.size(), page.getWorkflow().size() + 1);
        assertEquals(listWorkflowIndexKeep.size(), page.getWorkflowForEditView().size() + 1);

        // リストの中身を比較する
        assertEquals(listWorkflowKeep.get(0).getWorkflowNo().toString(), page.getWorkflow().get(0)
            .getWorkflowNo().toString());
        assertEquals(listWorkflowKeep.get(0).getUser().getEmpNo(), page.getWorkflow().get(0)
            .getUser().getEmpNo());
        assertEquals(listWorkflowKeep.get(0).getUser().getNameE(), page.getWorkflow().get(0)
            .getUser().getNameE());

        assertEquals(String.valueOf(listWorkflowKeep.get(2).getWorkflowNo() - 1), page
            .getWorkflow().get(1).getWorkflowNo().toString());
        assertEquals(listWorkflowKeep.get(2).getUser().getEmpNo(), page.getWorkflow().get(1)
            .getUser().getEmpNo());
        assertEquals(listWorkflowKeep.get(2).getUser().getNameE(), page.getWorkflow().get(1)
            .getUser().getNameE());

        assertEquals(String.valueOf(listWorkflowKeep.get(3).getWorkflowNo() - 1), page
            .getWorkflow().get(2).getWorkflowNo().toString());
        assertEquals(listWorkflowKeep.get(3).getUser().getEmpNo(), page.getWorkflow().get(2)
            .getUser().getEmpNo());
        assertEquals(listWorkflowKeep.get(3).getUser().getNameE(), page.getWorkflow().get(2)
            .getUser().getNameE());

        // リストの中身を比較する 画面表示用
        assertEquals(listWorkflowIndexKeep.get(0).getWorkflowNo().toString(), page
            .getWorkflowForEditView().get(0).getWorkflowNo().toString());
        assertEquals(listWorkflowIndexKeep.get(0).getUser().getEmpNo(), page.getWorkflowForEditView()
            .get(0).getUser().getEmpNo());
        assertEquals(listWorkflowIndexKeep.get(0).getUser().getNameE(), page.getWorkflowForEditView()
            .get(0).getUser().getNameE());

        assertEquals(listWorkflowIndexKeep.get(1).getWorkflowNo().toString(), page
            .getWorkflowForEditView().get(1).getWorkflowNo().toString());
        assertEquals(listWorkflowIndexKeep.get(1).getUser().getEmpNo(), page.getWorkflowForEditView()
            .get(1).getUser().getEmpNo());
        assertEquals(listWorkflowIndexKeep.get(1).getUser().getNameE(), page.getWorkflowForEditView()
            .get(1).getUser().getNameE());

        assertEquals(String.valueOf(listWorkflowIndexKeep.get(3).getWorkflowNo() - 1), page
            .getWorkflowForEditView().get(2).getWorkflowNo().toString());
        assertEquals(listWorkflowIndexKeep.get(3).getUser().getEmpNo(), page.getWorkflowForEditView()
            .get(2).getUser().getEmpNo());
        assertEquals(listWorkflowIndexKeep.get(3).getUser().getNameE(), page.getWorkflowForEditView()
            .get(2).getUser().getNameE());

        assertEquals(String.valueOf(listWorkflowIndexKeep.get(4).getWorkflowNo() - 1), page
            .getWorkflowForEditView().get(3).getWorkflowNo().toString());
        assertEquals(listWorkflowIndexKeep.get(4).getUser().getEmpNo(), page.getWorkflowForEditView()
            .get(3).getUser().getEmpNo());
        assertEquals(listWorkflowIndexKeep.get(4).getUser().getNameE(), page.getWorkflowForEditView()
            .get(3).getUser().getNameE());
    }

    /**
     * 活動単位変更アクションのテスト.
     * @throws Exception
     */
    @Test
    public void testChangeGroup() throws Exception {
        setRequiredFields();

        // テストに必要な値をPAGEにセットする
        // ダミーの活動単位を設定
        List<CorresponGroup> listGroup = createCorresponGroup();
        // --------------------------------------

        // ダミーのユーザーを設定
        User u;
        ProjectUser pu;
        List<ProjectUser> result = new ArrayList<ProjectUser>();
        pu = new ProjectUser();
        pu.setProjectId("PJ1");
        pu.setSecurityLevel("40");
        u = new User();
        u.setEmpNo("ZZA01");
        u.setLastName("Aaa");
        u.setNameE("aaa Aaa");
        pu.setUser(u);

        result.add(pu);

        pu = new ProjectUser();
        pu.setProjectId("PJ1");
        pu.setSecurityLevel("40");
        u = new User();
        u.setEmpNo("ZZA02");
        u.setLastName("Bbb");
        u.setNameE("bbb Bbb");
        pu.setUser(u);

        result.add(pu);
        List<User> listUser = new ArrayList<User>();

        for (ProjectUser projectUser : result) {
            listUser.add(projectUser.getUser());
        }
        // --------------------------------------

        // ダミーのグループユーザーを設定
        CorresponGroupUser gu;
        User us;
        List<CorresponGroupUser> listCg = new ArrayList<CorresponGroupUser>();

        gu = new CorresponGroupUser();
        gu.setId(1L);
        us = new User();
        us.setEmpNo("ZZA91");
        us.setNameE("Name1");
        gu.setUser(us);
        listCg.add(gu);

        gu = new CorresponGroupUser();
        gu.setId(2L);
        us = new User();
        us.setEmpNo("ZZA92");
        us.setNameE("Name2");
        gu.setUser(us);
        listCg.add(gu);

        gu = new CorresponGroupUser();
        gu.setId(3L);
        us = new User();
        us.setEmpNo("ZZA93");
        us.setNameE("Name3");
        gu.setUser(us);
        listCg.add(gu);
        // --------------------------------------

        // ページグループID
        page.setGroupId(2L);

        MockCorresponGroupService.RET_SEARCH = listGroup;
        MockCorresponGroupService.RET_FINDMENBERS = listCg;
        MockUserService.RET_SEARCH = result;

        // ユーザーをPAGEにセット
        page.setUser(listUser);
        // 活動単位をPAGEにセット
        page.setCorresponGroup(listGroup);

        // 実処理実行
        workflowEditModule.changeGroup();

        List<SelectItem> si = page.getSelectUser();

        // 値保持用
        List<SelectItem> listSi = new ArrayList<SelectItem>(si);

        // グループを変更
        page.setGroupId(-1L);
        workflowEditModule.changeGroup();

        List<SelectItem> listUs = page.getSelectUser();

        assertFalse(listSi.toString().equals(listUs.toString()));
    }

    /**
     * 承認フロー保存アクションのテスト.
     * @throws Exception
     */
    @Test
    public void testSave() throws Exception {
        setRequiredFields();

        User retUser = new User();
        retUser.setEmpNo("USER004");
        MockAbstractPage.RET_USER = retUser;
        MockCorresponReadStatusService.RET_CURRENT_USER = retUser;

        Project project = new Project();
        project.setProjectId("PJ1");
        MockAbstractPage.RET_PROJECT = project;

        // Serviceが返すダミーのコレポン文書を設定
        Correspon expected = createCorrespon(99L);

        // 承認フロー保存用
        List<User> listUser = new ArrayList<User>();
        List<Workflow> listWorkflow = new ArrayList<Workflow>();
        Workflow wf = new Workflow();

        // 承認フロー保持用
        List<User> listUserKeep = new ArrayList<User>();
        List<Workflow> listWorkflowKeep = new ArrayList<Workflow>();
        Workflow wfKeep = new Workflow();
        User usKeep = new User();

        // ログインユーザー
        User loginUser = new User();

        // Preparerのデータ
        Workflow w = new Workflow();
        w.setUser(new User());
        w.setWorkflowNo(1L);

        // 表示するリストに追加
        listWorkflow.add(0, w);
        listWorkflowKeep.add(0, w);

        // ----------------------------
        User us = new User();
        us.setEmpNo("TEST01");
        us.setNameE("TARO TEST");
        usKeep.setEmpNo("TEST01");
        usKeep.setNameE("TARO TEST");

        wf.setCorresponId(99L);
        wf.setVersionNo((long) 1);
        wf.setUser(us);
        wf.setWorkflowNo(2L);
        wf.setWorkflowType(WorkflowType.CHECKER);
        wf.setCreatedBy(loginUser);
        wf.setUpdatedBy(loginUser);
        wfKeep.setCorresponId(99L);
        wfKeep.setUser(usKeep);
        wfKeep.setWorkflowNo(2L);
        wfKeep.setWorkflowType(WorkflowType.CHECKER);
        wfKeep.setCreatedBy(loginUser);
        wfKeep.setUpdatedBy(loginUser);
        wfKeep.setVersionNo((long) 1);

        listWorkflow.add(wf);
        listUser.add(us);
        listUserKeep.add(usKeep);
        listWorkflowKeep.add(wfKeep);

        wf = new Workflow();
        us = new User();
        wfKeep = new Workflow();
        usKeep = new User();

        us.setEmpNo("TEST02");
        us.setNameE("JIRO TEST");
        usKeep.setEmpNo("TEST02");
        usKeep.setNameE("JIRO TEST");

        wf.setVersionNo((long) 1);
        wf.setCorresponId(99L);
        wf.setUser(us);
        wf.setWorkflowNo(3L);
        wf.setWorkflowType(WorkflowType.CHECKER);
        wf.setCreatedBy(loginUser);
        wf.setUpdatedBy(loginUser);
        wfKeep.setCorresponId(99L);
        wfKeep.setUser(usKeep);
        wfKeep.setWorkflowNo(3L);
        wfKeep.setWorkflowType(WorkflowType.CHECKER);
        wfKeep.setCreatedBy(loginUser);
        wfKeep.setUpdatedBy(loginUser);
        wfKeep.setVersionNo((long) 1);

        listWorkflow.add(wf);
        listUser.add(us);
        listUserKeep.add(usKeep);
        listWorkflowKeep.add(wfKeep);

        wf = new Workflow();
        us = new User();
        wfKeep = new Workflow();
        usKeep = new User();

        us.setEmpNo("TEST03");
        us.setNameE("SABURO TEST");
        usKeep.setEmpNo("TEST03");
        usKeep.setNameE("SABURO TEST");

        wf.setVersionNo((long) 1);
        wf.setCorresponId(99L);
        wf.setUser(us);
        wf.setWorkflowNo(4L);
        wf.setWorkflowType(WorkflowType.APPROVER);
        wf.setCreatedBy(loginUser);
        wf.setUpdatedBy(loginUser);
        wfKeep.setCorresponId(99L);
        wfKeep.setUser(us);
        wfKeep.setWorkflowNo(4L);
        wfKeep.setWorkflowType(WorkflowType.APPROVER);
        wfKeep.setCreatedBy(loginUser);
        wfKeep.setUpdatedBy(loginUser);
        wfKeep.setVersionNo((long) 1);

        listWorkflow.add(wf);
        listUser.add(us);
        listUserKeep.add(usKeep);
        listWorkflowKeep.add(wfKeep);

        expected.setWorkflows(listWorkflow);
        page.setCorrespon(expected);

        expected.setProjectId("20");

        MockCorresponService.RET_FIND = expected;

        page.setCorrespon(expected);
        page.setId(expected.getId());

        // Serviceが返すダミーの活動単位を設定
        List<CorresponGroup> listGroup = createCorresponGroup();

        page.setCorresponGroup(listGroup);
        MockCorresponGroupService.RET_SEARCH = listGroup;
        MockCorresponGroupService.RET_FIND_CORRESPON_GROUP_USER_MAPPINGS =
                new ArrayList<CorresponGroupUserMapping>();
        // --------------------------------------

        // Serviceが返すダミーのユーザーを設定
        User u;
        ProjectUser pu;
        List<ProjectUser> result = new ArrayList<ProjectUser>();
        pu = new ProjectUser();
        pu.setProjectId("PJ1");
        pu.setSecurityLevel("40");
        u = new User();
        u.setEmpNo("ZZA01");
        u.setLastName("Aaa");
        u.setNameE("aaa Aaa");
        pu.setUser(u);

        result.add(pu);

        pu = new ProjectUser();
        pu.setProjectId("PJ1");
        pu.setSecurityLevel("40");
        u = new User();
        u.setEmpNo("ZZA02");
        u.setLastName("Bbb");
        u.setNameE("bbb Bbb");
        pu.setUser(u);

        result.add(pu);

        List<User> listlistUser = new ArrayList<User>();

        for (ProjectUser projectUser : result) {
            listlistUser.add(projectUser.getUser());
        }

        MockUserService.RET_SEARCH = result;
        // ----------------------

        List<WorkflowTemplateUser> workflowTemplateUser = new ArrayList<WorkflowTemplateUser>();
        List<WorkflowTemplateUser> workflowTemplateUserKeep = new ArrayList<WorkflowTemplateUser>();
        WorkflowTemplateUser wt = new WorkflowTemplateUser();
        WorkflowTemplateUser wtKeep = new WorkflowTemplateUser();
        wt.setId(1L);
        wtKeep.setId(1L);
        wt.setProjectId("PJ1");
        wtKeep.setProjectId("PJ1");

        User user = new User();
        user.setEmpNo("ZZA05");
        user.setNameE("Keiichi Ogiwara");
        wt.setUser(user);
        wtKeep.setUser(user);

        wt.setCreatedBy(loginUser);
        wtKeep.setCreatedBy(loginUser);
        wt.setUpdatedBy(loginUser);
        wtKeep.setUpdatedBy(loginUser);

        wt.setName("TEST");
        wtKeep.setName("TEST");
        wt.setVersionNo(1L);
        wtKeep.setVersionNo(1L);
        wt.setDeleteNo(0L);
        wtKeep.setDeleteNo(0L);

        workflowTemplateUser.add(wt);
        workflowTemplateUserKeep.add(wtKeep);

        MockCorresponWorkflowService.RET_SEARCH_WORKFLOW_TEMPLATE_USER = workflowTemplateUser;

        // 期待されるメッセージをセット
        FacesContextMock.EXPECTED_CLIENT_ID = null;
        FacesContextMock.EXPECTED_MESSAGE =
                new FacesMessage(
                    FacesMessage.SEVERITY_INFO, FacesMessage.SEVERITY_INFO.toString(),
                    "保存が完了しました。");

        // 処理実行
        workflowEditModule.save();

        assertEquals("save", MockCorresponWorkflowService.RET_SAVE);
    }

    /**
     * キャンセルアクションのテスト.
     * @throws Exception
     */
    @Test
    public void testCancel() throws Exception {
        setRequiredFields();

        page.setWorkflowEditDisplay(true);

        workflowEditModule.cancel();

        assertFalse(page.isWorkflowEditDisplay());
    }

    /**
     * 承認フローテンプレート自動設定アクションのテスト.
     * @throws Exception
     */
    @Test
    public void testApply() throws Exception {
        setRequiredFields();

        List<WorkflowTemplate> templateList = createWorkflowTemplate();
        // 保持用
        List<WorkflowTemplate> templateListKeep = createWorkflowTemplate();

        MockCorresponWorkflowService.RET_APPLY = templateList;
        MockAbstractPage.RET_USER = loginUser;

        Correspon correspon = new Correspon();
        User createUser = new User();
        createUser.setEmpNo("ZZA04");
        createUser.setNameE("Tomoko Okada");

        correspon.setCreatedBy(createUser);
        correspon.setId(10L);
        page.setCorrespon(correspon);

        // テスト実行
        workflowEditModule.apply();

        assertTrue(3 == page.getWorkflow().size());
        assertTrue(4 == page.getWorkflowForEditView().size());

        assertEquals(String.valueOf(1L), page.getWorkflowForEditView().get(0).getWorkflowNo().toString());
        assertEquals(createUser.getEmpNo(), page.getWorkflowForEditView().get(0).getUser().getEmpNo());

        assertEquals(String.valueOf(templateListKeep.get(0).getWorkflowNo()), String.valueOf(page
            .getWorkflowForEditView().get(1).getWorkflowNo() - 1));
        assertEquals(templateListKeep.get(0).getUser().getEmpNo(), page.getWorkflowForEditView().get(1)
            .getUser().getEmpNo());
        assertEquals(templateListKeep.get(0).getWorkflowType(), page.getWorkflowForEditView().get(1)
            .getWorkflowType());
        assertEquals(String.valueOf(0L), page.getWorkflowForEditView().get(1).getId().toString());
        assertEquals(WorkflowProcessStatus.NONE, page.getWorkflowForEditView().get(1)
            .getWorkflowProcessStatus());
        assertEquals(String.valueOf(10L), page.getWorkflowForEditView().get(1).getCorresponId()
            .toString());
        assertEquals(loginUser.toString(), page.getWorkflowForEditView().get(1).getCreatedBy().toString());
        assertEquals(loginUser.toString(), page.getWorkflowForEditView().get(1).getUpdatedBy().toString());

        assertEquals(String.valueOf(templateListKeep.get(1).getWorkflowNo()), String.valueOf(page
            .getWorkflowForEditView().get(2).getWorkflowNo() - 1));
        assertEquals(templateListKeep.get(1).getUser().getEmpNo(), page.getWorkflowForEditView().get(2)
            .getUser().getEmpNo());
        assertEquals(templateListKeep.get(1).getWorkflowType(), page.getWorkflowForEditView().get(2)
            .getWorkflowType());
        assertEquals(String.valueOf(0L), page.getWorkflowForEditView().get(2).getId().toString());
        assertEquals(WorkflowProcessStatus.NONE, page.getWorkflowForEditView().get(2)
            .getWorkflowProcessStatus());
        assertEquals(String.valueOf(10L), page.getWorkflowForEditView().get(2).getCorresponId()
            .toString());
        assertEquals(loginUser.toString(), page.getWorkflowForEditView().get(2).getCreatedBy().toString());
        assertEquals(loginUser.toString(), page.getWorkflowForEditView().get(2).getUpdatedBy().toString());

        assertEquals(String.valueOf(templateListKeep.get(2).getWorkflowNo()), String.valueOf(page
            .getWorkflowForEditView().get(3).getWorkflowNo() - 1));
        assertEquals(templateListKeep.get(2).getUser().getEmpNo(), page.getWorkflowForEditView().get(3)
            .getUser().getEmpNo());
        assertEquals(templateListKeep.get(2).getWorkflowType(), page.getWorkflowForEditView().get(3)
            .getWorkflowType());
        assertEquals(String.valueOf(0L), page.getWorkflowForEditView().get(3).getId().toString());
        assertEquals(WorkflowProcessStatus.NONE, page.getWorkflowForEditView().get(3)
            .getWorkflowProcessStatus());
        assertEquals(String.valueOf(10L), page.getWorkflowForEditView().get(3).getCorresponId()
            .toString());
        assertEquals(loginUser.toString(), page.getWorkflowForEditView().get(3).getCreatedBy().toString());
        assertEquals(loginUser.toString(), page.getWorkflowForEditView().get(3).getUpdatedBy().toString());
    }

    /**
     * 承認フローテンプレート削除アクションのテスト.
     * @throws Exception
     */
    @Test
    public void testDeleteTemplate() throws Exception {
        setRequiredFields();

        page.setTemplate(1L);

        List<WorkflowTemplateUser> wtuList = new ArrayList<WorkflowTemplateUser>();
        WorkflowTemplateUser wtu = new WorkflowTemplateUser();
        wtu.setId(1L);
        wtu.setName("TEST");
        wtuList.add(wtu);

        MockCorresponWorkflowService.RET_SEARCH_WORKFLOW_TEMPLATE_USER = wtuList;

        // 期待されるメッセージをセット
        FacesContextMock.EXPECTED_CLIENT_ID = null;
        FacesContextMock.EXPECTED_MESSAGE =
                new FacesMessage(
                    FacesMessage.SEVERITY_INFO, FacesMessage.SEVERITY_INFO.toString(),
                    "指定されたワークフローテンプレートは削除されました。");

        // テスト実行
        workflowEditModule.deleteTemplate();

        assertEquals(wtuList.toString(),
            MockCorresponWorkflowService.RET_SEARCH_WORKFLOW_TEMPLATE_USER.toString());
    }

    /**
     * 承認フローテンプレート保存アクションのテスト.
     * @throws Exception
     */
    @Test
    public void testSaveTemplate() throws Exception {
        setRequiredFields();

        List<Workflow> workflowList = createWorkflowList();
        // 保持用
        List<Workflow> workflowListKeep = createWorkflowList();

        page.setWorkflow(workflowList);
        page.setTemplateName("Test");

        List<WorkflowTemplateUser> lstWtu = new ArrayList<WorkflowTemplateUser>();
        WorkflowTemplateUser wtu = new WorkflowTemplateUser();
        wtu.setId(1L);
        wtu.setName("TEST");
        lstWtu.add(wtu);

        Correspon correspon = new Correspon();
        Correspon corresponKeep = new Correspon();
        correspon.setProjectId("PJ1");
        corresponKeep.setProjectId("PJ1");
        page.setCorrespon(correspon);

        MockCorresponWorkflowService.RET_SEARCH_WORKFLOW_TEMPLATE_USER = lstWtu;

        // 期待されるメッセージをセット
        FacesContextMock.EXPECTED_CLIENT_ID = null;
        FacesContextMock.EXPECTED_MESSAGE =
                new FacesMessage(
                    FacesMessage.SEVERITY_INFO, FacesMessage.SEVERITY_INFO.toString(),
                    "保存が完了しました。");

        // テスト実行
        workflowEditModule.saveTemplate();

        assertEquals("Test", MockCorresponWorkflowService.CRT_SAVE_TEMPLATE_NAME);
        assertEquals(workflowListKeep.toString(),
            MockCorresponWorkflowService.CRT_SAVE_TEMPLATE_WORKFLOW.toString());
        assertEquals(corresponKeep.toString(), MockCorresponWorkflowService.CRT_SAVE_TEMPLATE_CORRESPON.toString());
    }

    private void setRequiredFields() {
        workflowEditModule.setCorresponPage(page);
        workflowEditModule.setServiceActionHandler(page.getHandler());
        workflowEditModule.setViewHelper(page.getViewHelper());
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
        wf.setWorkflowNo(1L);
        list.add(wf);

        wf = new Workflow();
        wf.setId(2L);
        wf.setUser(CHECKER_2);
        wf.setWorkflowType(WorkflowType.CHECKER);
        wf.setWorkflowNo(2L);
        list.add(wf);

        wf = new Workflow();
        wf.setId(3L);
        wf.setUser(CHECKER_3);
        wf.setWorkflowType(WorkflowType.CHECKER);
        wf.setWorkflowNo(3L);
        list.add(wf);

        wf = new Workflow();
        wf.setId(4L);
        wf.setUser(APPROVER);
        wf.setWorkflowType(WorkflowType.APPROVER);
        wf.setWorkflowNo(4L);
        list.add(wf);

        return list;
    }

    /**
     * テストで使用する承認フローテンプレートを作成する.
     * @return 承認フローテンプレート
     */
    private List<WorkflowTemplate> createWorkflowTemplate() {
        List<WorkflowTemplate> templateList = new ArrayList<WorkflowTemplate>();
        WorkflowTemplate template = new WorkflowTemplate();
        template.setId(1L);
        User user = new User();
        user.setEmpNo("ZZA01");
        user.setNameE("Taro Yamada");
        template.setUser(user);
        template.setWorkflowNo(1L);
        template.setWorkflowTemplateUserId(1L);
        template.setWorkflowType(WorkflowType.CHECKER);
        templateList.add(template);

        template = new WorkflowTemplate();
        template.setId(2L);
        user = new User();
        user.setEmpNo("ZZA02");
        user.setNameE("Tetsuo Aoki");
        template.setUser(user);
        template.setWorkflowNo(2L);
        template.setWorkflowTemplateUserId(1L);
        template.setWorkflowType(WorkflowType.CHECKER);
        templateList.add(template);

        template = new WorkflowTemplate();
        template.setId(3L);
        user = new User();
        user.setEmpNo("ZZA03");
        user.setNameE("Atsushi Ishida");
        template.setUser(user);
        template.setWorkflowNo(3L);
        template.setWorkflowTemplateUserId(1L);
        template.setWorkflowType(WorkflowType.APPROVER);
        templateList.add(template);

        return templateList;
    }

    /**
     * テスト用コレポン文書の作成
     */
    private Correspon createCorrespon(Long id) {
        Correspon c = new Correspon();
        c.setId(id);
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
        return c;
    }

    private List<CorresponGroup> createCorresponGroup() {
        List<CorresponGroup> listGroup = new ArrayList<CorresponGroup>();

        CorresponGroup cg = new CorresponGroup();
        cg.setId((long) 1);
        cg.setProjectId("1");
        cg.setName("Group1");
        listGroup.add(cg);

        cg = new CorresponGroup();
        cg.setId((long) 2);
        cg.setProjectId("2");
        cg.setName("Group2");
        listGroup.add(cg);

        cg = new CorresponGroup();
        cg.setId((long) 3);
        cg.setProjectId("3");
        cg.setName("Group3");
        listGroup.add(cg);
        return listGroup;
    }

    public static class MockCorresponWorkflowService extends MockUp<CorresponWorkflowServiceImpl> {
        static String RET_SAVE;
        static List<WorkflowTemplateUser> RET_SEARCH_WORKFLOW_TEMPLATE_USER;
        static List<WorkflowTemplate> RET_APPLY;
        static Long CRT_DELETE_TEMPLATE;
        static String CRT_SAVE_TEMPLATE_NAME;
        static List<Workflow> CRT_SAVE_TEMPLATE_WORKFLOW;
        static Correspon CRT_SAVE_TEMPLATE_CORRESPON;

        @Mock
        public void save(Correspon correspon, List<Workflow> workflows)
            throws ServiceAbortException {
            RET_SAVE = "save";
        }

        @Mock
        public List<WorkflowTemplateUser> searchWorkflowTemplateUser() {
            return RET_SEARCH_WORKFLOW_TEMPLATE_USER;
        }

        @Mock
        public List<WorkflowTemplate> apply(Long workflowTemplateUserId)
            throws ServiceAbortException {
            return RET_APPLY;
        }

        @Mock
        public void deleteTemplate(Long workflowTemplateUserId) throws ServiceAbortException {
            CRT_DELETE_TEMPLATE = workflowTemplateUserId;
        }

        @Mock
        public void saveTemplate(String name, List<Workflow> workflow, Correspon correspon) throws ServiceAbortException {
            CRT_SAVE_TEMPLATE_NAME = name;
            CRT_SAVE_TEMPLATE_WORKFLOW = workflow;
            CRT_SAVE_TEMPLATE_CORRESPON = correspon;
        }
    }

    public static class MockCorresponService extends MockUp<CorresponServiceImpl> {
        static Correspon RET_FIND;

        @Mock
        public Correspon find(Long id) throws ServiceAbortException {
            System.out.println("MockCorresponService#find:" + RET_FIND);
            return RET_FIND;
        }
    }

    public static class MockCorresponGroupService extends MockUp<CorresponGroupServiceImpl> {
        static List<CorresponGroup> RET_SEARCH;
        static List<CorresponGroupUser> RET_FINDMENBERS;
        static List<CorresponGroupUserMapping> RET_FIND_CORRESPON_GROUP_USER_MAPPINGS;

        @Mock
        public List<CorresponGroup> search(SearchCorresponGroupCondition condition)
            throws ServiceAbortException {
            return RET_SEARCH;
        }

        @Mock
        public List<CorresponGroupUser> findMembers(Long id) throws ServiceAbortException {
            return RET_FINDMENBERS;
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

    public static class MockAbstractPage extends MockUp<AbstractPage> {
        static User RET_USER;
        static Project RET_PROJECT;
        static ProjectUser RET_PROJECT_USER;
        static String RET_PROJECT_ID;

        @Mock
        public User getCurrentUser() {
            return RET_USER;
        }

        @Mock
        public Project getCurrentProject() {
            return RET_PROJECT;
        }

        @Mock
        public ProjectUser getCurrentProjectUser() {
            return RET_PROJECT_USER;
        }

        @Mock
        public String getCurrentProjectId() {
            return RET_PROJECT_ID;
        }
    }
}
