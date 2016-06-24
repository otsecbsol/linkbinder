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

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import jp.co.opentone.bsol.framework.core.config.SystemConfig;
import jp.co.opentone.bsol.framework.core.dao.Entity;
import jp.co.opentone.bsol.framework.core.dao.KeyDuplicateException;
import jp.co.opentone.bsol.framework.core.dao.RecordNotFoundException;
import jp.co.opentone.bsol.framework.core.dao.StaleRecordException;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.core.util.DateUtil;
import jp.co.opentone.bsol.framework.test.AbstractTestCase;
import jp.co.opentone.bsol.linkbinder.dto.AddressCorresponGroup;
import jp.co.opentone.bsol.linkbinder.dto.Correspon;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroup;
import jp.co.opentone.bsol.linkbinder.dto.CorresponType;
import jp.co.opentone.bsol.linkbinder.dto.Project;
import jp.co.opentone.bsol.linkbinder.dto.ProjectUser;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.Workflow;
import jp.co.opentone.bsol.linkbinder.dto.WorkflowPattern;
import jp.co.opentone.bsol.linkbinder.dto.WorkflowTemplate;
import jp.co.opentone.bsol.linkbinder.dto.WorkflowTemplateUser;
import jp.co.opentone.bsol.linkbinder.dto.code.CorresponStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowProcessStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowType;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchUserCondition;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchWorkflowTemplateUserCondition;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.service.MockAbstractService;
import jp.co.opentone.bsol.linkbinder.service.correspon.CorresponWorkflowService;

/**
 * {@link CorresponWorkflowServiceImpl}のテストケース.
 * @author opentone
 */
public class CorresponWorkflowServiceImplTest extends AbstractTestCase {

    /**
     * テスト対象.
     */
    @Resource
    private CorresponWorkflowService service;

    /**
     * 承認パターン1の取得キー
     */
    private static final String KEY_PATTERN_1 = "workflow.pattern.1";
    /**
     * 承認パターン2の取得キー
     */
    private static final String KEY_PATTERN_2 = "workflow.pattern.2";
    /**
     * 承認パターン2の取得キー
     */
    private static final String KEY_PATTERN_3 = "workflow.pattern.3";

    /**
     * ログインユーザー
     */
    private User user = new User();

    @BeforeClass
    public static void testSetUp() {
        //TODO JMockitバージョンアップ対応
//        Mockit.redefineMethods(AbstractService.class, MockAbstractService.class);
//        Mockit.redefineMethods(CorresponDaoImpl.class, MockCorresponDao.class);
//        Mockit.redefineMethods(WorkflowDaoImpl.class, MockWorkflowDao.class);
//        Mockit.redefineMethods(AddressCorresponGroupDaoImpl.class,MockAddressCorresponGroupDao.class);
//        Mockit.redefineMethods(UserDaoImpl.class, MockUserDao.class);
//        Mockit.redefineMethods(AbstractDao.class, MockAbstractDao.class);
//        Mockit.redefineMethods(WorkflowTemplateDaoImpl.class, MockWorkflowTemplateDao.class);
//        Mockit.redefineMethods(WorkflowTemplateUserDaoImpl.class, MockWorkflowTemplateUserDao.class);
//        Mockit.redefineMethods(DateUtil.class, MockDateUtil.class);
//        Mockit.redefineMethods(CorresponSequenceServiceImpl.class,
//            MockCorresponSequenceService.class);

    }

    @AfterClass
    public static void testTearDown() {
        //TODO JMockitバージョンアップ対応
//        Mockit.tearDownMocks();
    }

    /**
     * テスト前準備.
     */
    @Before
    public void setUp() {
        user.setEmpNo("ZZA01");
        user.setNameE("Taro Yamada");
        MockAbstractService.CURRENT_USER = user;
    }

    /**
     * 後始末.
     */
    @After
    public void tearDown() {
        MockAddressCorresponGroupDao.RET_FIND_BY_CORRESPON_ID = null;
        MockWorkflowDao.RET_FIND_BY_CORRESPON_ID = null;
        MockWorkflowDao.RET_DELETE_BY_CORRESPON_ID_WORKFLOW_NO = null;
        MockWorkflowDao.RET_DELETE_BY_CORRESPON_ID = null;
        MockWorkflowDao.ACT_UPDATE_WORKFLOW_PROCESS_STATUS_BY_ID.clear();
        MockWorkflowDao.ACT_UPDATE_WORKFLOW_PROCESS_STATUSES_BY_CORRESPON_ID_AND_WORKFLOW_TYPE.clear();
        MockWorkflowTemplateDao.RET_FIND_BY_WORKFLOW_TEMPLATE_USER_ID = null;
        MockWorkflowTemplateDao.RET_DELETE_BY_WORKFLOW_TEMPLATE_USER_ID = null;
        MockWorkflowTemplateDao.CRT_DELETE_BY_WORKFLOW_TEMPLATE_USER_ID = null;
        MockWorkflowTemplateUserDao.RET_FIND = null;
        MockWorkflowTemplateUserDao.RET_COUNT_TEMPLATE_USER_CHECK = 0;
        MockDateUtil.RET_GETNOW = null;
        MockCorresponSequenceService.RET_CORRESPON_NO = null;

        tearDownMockAbstractDao();
        tearDownMockAbstractService();
    }

    private void tearDownMockAbstractDao() {
        MockAbstractDao.ACT_CREATE.clear();
        MockAbstractDao.ACT_UPDATE_CORRESPON.clear();
        MockAbstractDao.ACT_UPDATE_WORKFLOW.clear();
        MockAbstractDao.RET_CREATE = null;
        MockAbstractDao.RET_UPDATE = null;
        MockAbstractDao.RET_DELETE = null;
        MockAbstractDao.ACT_DELETE_WORKFLOW_TEMPLATE_USER = null;
        MockAbstractDao.RET_FIND_BY_ID.clear();
        MockAbstractDao.COUNT = 0;
        MockAbstractDao.ACT_WORKFLOW_TEMPLATE_USER = null;
        MockAbstractDao.ACT_WORKFLOW_TEMPLATE.clear();
    }

    private void tearDownMockAbstractService() {
        MockAbstractService.CURRENT_PROJECT_ID = null;
        MockAbstractService.CURRENT_PROJECT = null;
        MockAbstractService.CURRENT_USER = null;
        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        MockAbstractService.IS_ANY_GROUP_ADMIN = false;
        MockAbstractService.IS_GROUP_ADMIN = null;
        MockAbstractService.ACCESSIBLE_PROJECTS = null;

    }

    /**
     * validationの検証
     * saveメソッドの引数がnullではないか検証
     */
    @Test
    public void testSaveArgumentNull() throws Exception {
        try {
            service.save(null, null);
            fail("例外が発生していない");
        } catch (IllegalArgumentException actual) {
            // 正しいエラーコードがセットされた例外がthrowされていることを検証
            assertEquals("Argument must not be null.", actual.getMessage());
        }

    }

    /**
     * validationの検証
     * 指定のコレポン情報のプロジェクトIDと現在選択しているプロジェクトIDに差異があるか検証
     */
    @Test
    public void testSaveProjectDiff() throws Exception {
        Correspon c = createCorrespon();
        MockCorresponDao.RET_FIND_BY_ID.put("1", c);
        MockAbstractService.CURRENT_PROJECT_ID = "PJ2";

        try {
            service.save(c, new ArrayList<Workflow>());
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            // 正しいエラーコードがセットされた例外がthrowされていることを検証
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_PROJECT_DIFF, actual
                .getMessageCode());
        }

    }

    /**
     * validationの検証
     * 指定可能なApprover、Checkerは、当該コレポン文書に指定されたプロジェクトに参加しているユーザーか検証
     */
    @Test
    public void testSaveUserInProject() throws Exception {
        Correspon c = createCorrespon();
        MockCorresponDao.RET_FIND_BY_ID.put("1", c);

        List<ProjectUser> result = new ArrayList<ProjectUser>();
        ProjectUser pu;
        User u;
        u = new User();
        u.setEmpNo("10001");
        u.setNameE("Checker1 User");
        pu = new ProjectUser();
        pu.setUser(u);
        pu.setProjectId("PJ1");
        result.add(pu);

        u = new User();
        u.setEmpNo("10002");
        u.setNameE("Checker2 User");
        pu = new ProjectUser();
        pu.setUser(u);
        pu.setProjectId("PJ1");
        result.add(pu);

        u = new User();
        u.setEmpNo("10003");
        u.setNameE("Checker3 User");
        pu = new ProjectUser();
        pu.setUser(u);
        pu.setProjectId("PJ1");
        result.add(pu);
        MockUserDao.RET_FINDPROJECTUSER = result;

        List<Workflow> workflow = createWorkflowDummy();

        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        MockAbstractService.IS_SYSTEM_ADMIN = true;

        try {
            service.save(c, workflow);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            // 正しいエラーコードがセットされた例外がthrowされていることを検証
            assertEquals(ApplicationMessageCode.INVALID_USER, actual.getMessageCode());
            assertEquals(1, actual.getMessageVars().length);
            assertEquals("80001", actual.getMessageVars()[0]);
        }

    }

    /**
     * validationの検証
     * SystemAdmin、コレポン文書の承認作業状態が『1:Request for Check』、
     * 権限共通チェックを通過し、 該当のコレポン文書の承認フローパターンが1以外の場合の検証
     */
    @Test
    public void testSaveNotPattern1() throws Exception {
        // テストに必要な値を作成
        Correspon c = createCorrespon();

        List<ProjectUser> result = new ArrayList<ProjectUser>();
        ProjectUser pu;
        User u;
        u = new User();
        u.setEmpNo("80001");
        u.setNameE("Checker1 User");
        pu = new ProjectUser();
        pu.setUser(u);
        pu.setProjectId("PJ1");
        result.add(pu);

        u = new User();
        u.setEmpNo("80002");
        u.setNameE("Checker2 User");
        pu = new ProjectUser();
        pu.setUser(u);
        pu.setProjectId("PJ1");
        result.add(pu);

        u = new User();
        u.setEmpNo("80003");
        u.setNameE("Checker3 User");
        pu = new ProjectUser();
        pu.setUser(u);
        pu.setProjectId("PJ1");
        result.add(pu);
        MockUserDao.RET_FINDPROJECTUSER = result;

        List<Workflow> workflow = createWorkflowDummy();

        c.setWorkflows(workflow);
        WorkflowPattern wp = c.getCorresponType().getWorkflowPattern();
        wp.setWorkflowCd("pattern2");
        c.getCorresponType().setWorkflowPattern(wp);

        MockCorresponDao.RET_FIND_BY_ID.put("1", c);

        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        MockAbstractService.IS_SYSTEM_ADMIN = true;

        List<Workflow> workflows = createWorkflowDummy();
        try {
            service.save(c, workflows);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            // 正しいエラーコードがセットされた例外がthrowされていることを検証
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_WORKFLOW_PATTERN_INVALID,
                actual.getMessageCode());
        }

    }

    /**
     * SystemAdmin、コレポン文書の承認作業状態が『2:Under Consideration』、権限共通チェックを通過し、
     * 該当のコレポン文書の承認フローパターンが1以外の場合の検証
     */
    @Test
    public void testSavePattern2() throws Exception {
        // テストに必要な値を作成
        Correspon c = createCorrespon();
        MockCorresponDao.RET_FIND_BY_ID.put("1", c);

        List<ProjectUser> result = new ArrayList<ProjectUser>();
        ProjectUser pu;
        User u;
        u = new User();
        u.setEmpNo("80001");
        u.setNameE("Checker1 User");
        pu = new ProjectUser();
        pu.setUser(u);
        pu.setProjectId("PJ1");
        result.add(pu);

        u = new User();
        u.setEmpNo("80002");
        u.setNameE("Checker2 User");
        pu = new ProjectUser();
        pu.setUser(u);
        pu.setProjectId("PJ1");
        result.add(pu);

        u = new User();
        u.setEmpNo("80003");
        u.setNameE("Checker3 User");
        pu = new ProjectUser();
        pu.setUser(u);
        pu.setProjectId("PJ1");
        result.add(pu);
        MockUserDao.RET_FINDPROJECTUSER = result;

        List<Workflow> workflow = createWorkflowDummy();

        MockWorkflowDao.RET_FIND_BY_CORRESPON_ID = workflow;

        c.setWorkflowStatus(WorkflowStatus.UNDER_CONSIDERATION);
        c.setWorkflows(workflow);
        WorkflowPattern wp = c.getCorresponType().getWorkflowPattern();
        wp.setWorkflowCd("pattern2");
        c.getCorresponType().setWorkflowPattern(wp);

        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        MockAbstractService.IS_SYSTEM_ADMIN = true;

        List<Workflow> workflows = createWorkflowDummy();
        try {
            service.save(c, workflows);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            // 正しいエラーコードがセットされた例外がthrowされていることを検証
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_WORKFLOW_PATTERN_INVALID,
                actual.getMessageCode());
        }

    }

    /**
     * 承認フロー共通チェック2
     * SystemAdmin,ProjectAdmin,GroupAdmin,Checer以外の検証
     */
    @Test
    public void testSaveCommonToCheck2() throws Exception {
        // テストに必要な値を作成
        Correspon c = createCorrespon();
        MockCorresponDao.RET_FIND_BY_ID.put("1", c);

        List<ProjectUser> result = new ArrayList<ProjectUser>();
        ProjectUser pu;
        User u;
        u = new User();
        u.setEmpNo("80001");
        u.setNameE("Checker1 User");
        pu = new ProjectUser();
        pu.setUser(u);
        pu.setProjectId("PJ1");
        result.add(pu);

        u = new User();
        u.setEmpNo("80002");
        u.setNameE("Checker2 User");
        pu = new ProjectUser();
        pu.setUser(u);
        pu.setProjectId("PJ1");
        result.add(pu);

        u = new User();
        u.setEmpNo("80003");
        u.setNameE("Checker3 User");
        pu = new ProjectUser();
        pu.setUser(u);
        pu.setProjectId("PJ1");
        result.add(pu);
        MockUserDao.RET_FINDPROJECTUSER = result;

        List<Workflow> workflow = createWorkflowDummy();

        MockWorkflowDao.RET_FIND_BY_CORRESPON_ID = workflow;

        c.setWorkflowStatus(WorkflowStatus.DENIED);
        c.setWorkflows(workflow);

        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        List<Project> accessibleProjects = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        accessibleProjects.add(p);

        MockAbstractService.ACCESSIBLE_PROJECTS = accessibleProjects;
        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        MockAbstractService.IS_GROUP_ADMIN = null;

        List<Workflow> workflows = createWorkflowDummy();
        try {
            service.save(c, workflows);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            // 正しいエラーコードがセットされた例外がthrowされていることを検証
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT,
                actual.getMessageCode());
        }
    }

    /**
     * 承認フローが保存できるか検証
     */
    @Test
    public void testSave() throws Exception {
        // ダミーの戻り値をセット
        Correspon c = createCorrespon();
        c.setWorkflowStatus(WorkflowStatus.DRAFT);
        MockCorresponDao.RET_FIND_BY_ID.put("1", c);

        List<ProjectUser> result = new ArrayList<ProjectUser>();
        ProjectUser pu;
        User u;
        u = new User();
        u.setEmpNo("80001");
        u.setNameE("Checker1 User");
        pu = new ProjectUser();
        pu.setUser(u);
        pu.setProjectId("PJ1");
        result.add(pu);

        u = new User();
        u.setEmpNo("80002");
        u.setNameE("Checker2 User");
        pu = new ProjectUser();
        pu.setUser(u);
        pu.setProjectId("PJ1");
        result.add(pu);

        u = new User();
        u.setEmpNo("80003");
        u.setNameE("Checker3 User");
        pu = new ProjectUser();
        pu.setUser(u);
        pu.setProjectId("PJ1");
        result.add(pu);
        MockUserDao.RET_FINDPROJECTUSER = result;

        List<Workflow> workflow = createWorkflowDummyStatusNone();

        MockWorkflowDao.RET_FIND_BY_CORRESPON_ID = workflow;

        // コレポン文書にワークフローをセット
        c.setWorkflows(workflow);

        List<Project> projects = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        projects.add(p);

        MockAbstractService.ACCESSIBLE_PROJECTS = projects;

        MockAbstractService.CURRENT_USER = user;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        MockAbstractService.IS_GROUP_ADMIN = null;

        MockWorkflowDao.RET_CREATE = 1L;

        List<Workflow> workflows = createWorkflowDummy();

        service.save(c, workflows);

        assertEquals(workflows.toString(), MockWorkflowDao.ACT_CREATE.toString());

    }

    /**
     * 承認フローが保存できるか検証
     * コレポン文書のステータスがREQUEST_FOR_CHECKの場合を検証
     */
    @Test
    public void testSaveStatusRequestForCheck() throws Exception {
        // ダミーの戻り値をセット
        Correspon c = createCorrespon();
        MockCorresponDao.RET_FIND_BY_ID.put("1", c);

        List<ProjectUser> result = new ArrayList<ProjectUser>();
        ProjectUser pu;
        User u;
        u = new User();
        u.setEmpNo("80001");
        u.setNameE("Checker1 User");
        pu = new ProjectUser();
        pu.setUser(u);
        pu.setProjectId("PJ1");
        result.add(pu);

        u = new User();
        u.setEmpNo("80002");
        u.setNameE("Checker2 User");
        pu = new ProjectUser();
        pu.setUser(u);
        pu.setProjectId("PJ1");
        result.add(pu);

        u = new User();
        u.setEmpNo("80003");
        u.setNameE("Checker3 User");
        pu = new ProjectUser();
        pu.setUser(u);
        pu.setProjectId("PJ1");
        result.add(pu);
        MockUserDao.RET_FINDPROJECTUSER = result;

        List<Workflow> workflow = createWorkflowDummyStatusNone();
        //  承認フローパターン1なので先頭のCheckerは少なくともRequest for Check以上のステータスである
        workflow.get(0).setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_CHECK);

        MockWorkflowDao.RET_FIND_BY_CORRESPON_ID = workflow;

        // コレポン文書にワークフローをセット
        c.setWorkflows(workflow);

        List<Project> projects = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        projects.add(p);

        MockAbstractService.ACCESSIBLE_PROJECTS = projects;

        // ログインユーザー作成
        User user = new User();
        MockAbstractService.CURRENT_USER = user;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        MockAbstractService.IS_SYSTEM_ADMIN = true;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        MockAbstractService.IS_GROUP_ADMIN = null;

        MockWorkflowDao.RET_CREATE = 1L;
        List<Workflow> workflows = createWorkflowDummy();

        service.save(c, workflows);


        assertEquals(workflows.toString(), MockWorkflowDao.ACT_CREATE.toString());

    }

    /**
     * 承認フローが保存できるか検証
     * 設定できるCheckerの数を超えている
     */
    @Test
    public void testSaveOverChecker() throws Exception {
        // ダミーの戻り値をセット
        Correspon c = createCorrespon();
        MockCorresponDao.RET_FIND_BY_ID.put("1", c);

        List<ProjectUser> result = new ArrayList<ProjectUser>();
        ProjectUser pu;
        User u;
        u = new User();
        u.setEmpNo("80001");
        u.setNameE("Checker1 User");
        pu = new ProjectUser();
        pu.setUser(u);
        pu.setProjectId("PJ1");
        result.add(pu);

        u = new User();
        u.setEmpNo("80002");
        u.setNameE("Checker2 User");
        pu = new ProjectUser();
        pu.setUser(u);
        pu.setProjectId("PJ1");
        result.add(pu);

        u = new User();
        u.setEmpNo("80003");
        u.setNameE("Checker3 User");
        pu = new ProjectUser();
        pu.setUser(u);
        pu.setProjectId("PJ1");
        result.add(pu);
        MockUserDao.RET_FINDPROJECTUSER = result;

        List<Workflow> workflow = createWorkflowDummyStatusNone();

        List<Workflow> workflows = createWorkflowDummy();
        Workflow w;

        // Checkerを10人以上作成
        for (int i = 0; i < 9; i++) {
            w = new Workflow();

            u = new User();
            u.setEmpNo("80001");
            u.setNameE("Checker1 User");
            w.setUser(u);
            w.setCreatedBy(u);
            w.setUpdatedBy(u);

            w.setWorkflowType(WorkflowType.CHECKER);
            w.setWorkflowProcessStatus(WorkflowProcessStatus.NONE);
            workflows.add(2, w);

        }

        MockWorkflowDao.RET_FIND_BY_CORRESPON_ID = workflow;

        // コレポン文書にワークフローをセット
        c.setWorkflows(workflow);

        List<Project> projects = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        projects.add(p);

        MockAbstractService.ACCESSIBLE_PROJECTS = projects;

        // ログインユーザー作成
        User user = new User();
        MockAbstractService.CURRENT_USER = user;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        MockAbstractService.IS_SYSTEM_ADMIN = true;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        MockAbstractService.IS_GROUP_ADMIN = null;

        MockWorkflowDao.RET_CREATE = 1L;
        try {
            service.save(c, workflows);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            // 正しいエラーコードがセットされた例外がthrowされていることを検証
            assertEquals(ApplicationMessageCode.EXCEED_MAXIMUM_NUMBER_OF_CHECKERS, actual
                .getMessageCode());
            assertEquals(1, actual.getMessageVars().length);
            assertEquals(10, actual.getMessageVars()[0]);

        }
    }

    /**
     * 承認フローが保存できるか検証
     * 承認フローリストにApproverが2人以上いる時を検証
     */
    @Test
    public void testSaveOverApprover() throws Exception {
        // ダミーの戻り値をセット
        Correspon c = createCorrespon();
        MockCorresponDao.RET_FIND_BY_ID.put("1", c);

        List<ProjectUser> result = new ArrayList<ProjectUser>();
        ProjectUser pu;
        User u;
        u = new User();
        u.setEmpNo("80001");
        u.setNameE("Checker1 User");
        pu = new ProjectUser();
        pu.setUser(u);
        pu.setProjectId("PJ1");
        result.add(pu);

        u = new User();
        u.setEmpNo("80002");
        u.setNameE("Checker2 User");
        pu = new ProjectUser();
        pu.setUser(u);
        pu.setProjectId("PJ1");
        result.add(pu);

        u = new User();
        u.setEmpNo("80003");
        u.setNameE("Checker3 User");
        pu = new ProjectUser();
        pu.setUser(u);
        pu.setProjectId("PJ1");
        result.add(pu);
        MockUserDao.RET_FINDPROJECTUSER = result;

        List<Workflow> workflow = createWorkflowDummyStatusNone();
        List<Workflow> workflows = createWorkflowDummy();

        Workflow w;

        w = new Workflow();

        u = new User();
        u.setEmpNo("80001");
        u.setNameE("Checker1 User");
        w.setUser(u);
        w.setCreatedBy(u);
        w.setUpdatedBy(u);

        w.setWorkflowType(WorkflowType.APPROVER);
        w.setWorkflowProcessStatus(WorkflowProcessStatus.NONE);
        workflows.add(w);

        MockWorkflowDao.RET_FIND_BY_CORRESPON_ID = workflow;

        // コレポン文書にワークフローをセット
        c.setWorkflows(workflow);

        List<Project> projects = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        projects.add(p);

        MockAbstractService.ACCESSIBLE_PROJECTS = projects;

        // ログインユーザー作成
        User user = new User();
        MockAbstractService.CURRENT_USER = user;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        MockAbstractService.IS_SYSTEM_ADMIN = true;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        MockAbstractService.IS_GROUP_ADMIN = null;

        MockWorkflowDao.RET_CREATE = 1L;
        try {
            service.save(c, workflows);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            // 正しいエラーコードがセットされた例外がthrowされていることを検証
            assertEquals(ApplicationMessageCode.EXCEED_MAXIMUM_NUMBER_OF_APPROVERS, actual
                .getMessageCode());
            assertEquals(1, actual.getMessageVars().length);
            assertEquals(1, actual.getMessageVars()[0]);

        }
    }

    /**
     * 承認フローが保存できるか検証
     * 承認フローリストにApprover下のCheckerが存在してるか検証
     */
    @Test
    public void testSaveCheckerUnderApprover() throws Exception {
        // ダミーの戻り値をセット
        Correspon c = createCorrespon();
        MockCorresponDao.RET_FIND_BY_ID.put("1", c);

        List<ProjectUser> result = new ArrayList<ProjectUser>();
        ProjectUser pu;
        User u;
        u = new User();
        u.setEmpNo("80001");
        u.setNameE("Checker1 User");
        pu = new ProjectUser();
        pu.setUser(u);
        pu.setProjectId("PJ1");
        result.add(pu);

        u = new User();
        u.setEmpNo("80002");
        u.setNameE("Checker2 User");
        pu = new ProjectUser();
        pu.setUser(u);
        pu.setProjectId("PJ1");
        result.add(pu);

        u = new User();
        u.setEmpNo("80003");
        u.setNameE("Checker3 User");
        pu = new ProjectUser();
        pu.setUser(u);
        pu.setProjectId("PJ1");
        result.add(pu);
        MockUserDao.RET_FINDPROJECTUSER = result;

        List<Workflow> workflow = createWorkflowDummyStatusNone();
        List<Workflow> workflows = createWorkflowDummy();

        Workflow w;

        w = new Workflow();

        u = new User();
        u.setEmpNo("80001");
        u.setNameE("Checker1 User");
        w.setUser(u);
        w.setCreatedBy(u);
        w.setUpdatedBy(u);

        w.setWorkflowType(WorkflowType.CHECKER);
        w.setWorkflowProcessStatus(WorkflowProcessStatus.NONE);
        workflows.add(w);

        MockWorkflowDao.RET_FIND_BY_CORRESPON_ID = workflow;

        // コレポン文書にワークフローをセット
        c.setWorkflows(workflow);

        List<Project> projects = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        projects.add(p);

        MockAbstractService.ACCESSIBLE_PROJECTS = projects;

        // ログインユーザー作成
        User user = new User();
        MockAbstractService.CURRENT_USER = user;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        MockAbstractService.IS_SYSTEM_ADMIN = true;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        MockAbstractService.IS_GROUP_ADMIN = null;

        MockWorkflowDao.RET_CREATE = 1L;
        try {
            service.save(c, workflows);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            // 正しいエラーコードがセットされた例外がthrowされていることを検証
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_WORKFLOW_SEQUENCE_INVALID,
                actual.getMessageCode());
            assertEquals(0, actual.getMessageVars().length);
        }
    }

    /**
     * 承認フローが保存できるか検証
     * 承認フローリストにApproverが設定されていない時を検証
     */
    @Test
    public void testSaveNoCheckerUnderApprover() throws Exception {
        // ダミーの戻り値をセット
        Correspon c = createCorrespon();
        MockCorresponDao.RET_FIND_BY_ID.put("1", c);

        List<ProjectUser> result = new ArrayList<ProjectUser>();
        ProjectUser pu;
        User u;
        u = new User();
        u.setEmpNo("80001");
        u.setNameE("Checker1 User");
        pu = new ProjectUser();
        pu.setUser(u);
        pu.setProjectId("PJ1");
        result.add(pu);

        u = new User();
        u.setEmpNo("80002");
        u.setNameE("Checker2 User");
        pu = new ProjectUser();
        pu.setUser(u);
        pu.setProjectId("PJ1");
        result.add(pu);

        u = new User();
        u.setEmpNo("80003");
        u.setNameE("Checker3 User");
        pu = new ProjectUser();
        pu.setUser(u);
        pu.setProjectId("PJ1");
        result.add(pu);
        MockUserDao.RET_FINDPROJECTUSER = result;

        List<Workflow> workflow = createWorkflowDummy();

        Workflow w;

        w = new Workflow();

        u = new User();
        u.setEmpNo("80001");
        u.setNameE("Approver1 User");
        w.setUser(u);
        w.setCreatedBy(u);
        w.setUpdatedBy(u);

        w.setWorkflowType(WorkflowType.APPROVER);
        w.setWorkflowProcessStatus(WorkflowProcessStatus.NONE);
        workflow.add(w);

        w = new Workflow();

        u = new User();
        u.setEmpNo("80002");
        u.setNameE("Approver2 User");
        w.setUser(u);
        w.setCreatedBy(u);
        w.setUpdatedBy(u);

        w.setWorkflowType(WorkflowType.APPROVER);
        w.setWorkflowProcessStatus(WorkflowProcessStatus.NONE);
        workflow.add(w);
        MockWorkflowDao.RET_FIND_BY_CORRESPON_ID = workflow;

        // コレポン文書にワークフローをセット
        c.setWorkflows(workflow);

        List<Project> projects = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        projects.add(p);

        MockAbstractService.ACCESSIBLE_PROJECTS = projects;

        // ログインユーザー作成
        User user = new User();
        MockAbstractService.CURRENT_USER = user;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        MockAbstractService.IS_SYSTEM_ADMIN = true;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        MockAbstractService.IS_GROUP_ADMIN = null;

        MockWorkflowDao.RET_CREATE = 1L;
        List<Workflow> workflows = createWorkflowDummy();

        // Approverが設定されていない
        workflows.remove(2);

        try {
            service.save(c, workflows);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            // 正しいエラーコードがセットされた例外がthrowされていることを検証
            assertEquals(ApplicationMessageCode.EXCEED_MAXIMUM_NUMBER_OF_APPROVERS, actual
                .getMessageCode());
            assertEquals(1, actual.getMessageVars().length);
            assertEquals(1, actual.getMessageVars()[0]);

        }
    }

    /**
     * 承認フローが保存できるか検証
     * 承認フローパターンが1の際、Checkerが自分の前者かSystemAdmin、ProjectAdmin、GroupAdminが
     * 既に検証済みのCheckerを変更した際を検証する
     */
    @Test
    public void testSaveCheckerCheckedChange() throws Exception {
        // テストに必要な値を作成
        Correspon c = createCorrespon();
        MockCorresponDao.RET_FIND_BY_ID.put("1", c);

        List<ProjectUser> result = new ArrayList<ProjectUser>();
        ProjectUser pu;
        User u;
        u = new User();
        u.setEmpNo("80001");
        u.setNameE("Checker1 User");
        pu = new ProjectUser();
        pu.setUser(u);
        pu.setProjectId("PJ1");
        result.add(pu);

        u = new User();
        u.setEmpNo("80002");
        u.setNameE("Checker2 User");
        pu = new ProjectUser();
        pu.setUser(u);
        pu.setProjectId("PJ1");
        result.add(pu);

        u = new User();
        u.setEmpNo("80003");
        u.setNameE("Checker3 User");
        pu = new ProjectUser();
        pu.setUser(u);
        pu.setProjectId("PJ1");
        result.add(pu);
        MockUserDao.RET_FINDPROJECTUSER = result;

        // 検証用に承認フローリストを保持する
        // -------------------------------------------
        List<Workflow> listworkflow = new ArrayList<Workflow>();
        Workflow work = new Workflow();
        User user;

        work.setId(new Long(99));
        work.setWorkflowNo(new Long(1));

        user = new User();
        user.setEmpNo("80001");
        user.setNameE("Checker1 User");
        work.setUser(user);
        work.setCreatedBy(user);
        work.setUpdatedBy(user);

        work.setWorkflowType(WorkflowType.CHECKER);
        work.setWorkflowProcessStatus(WorkflowProcessStatus.CHECKED);
        listworkflow.add(work);

        work = new Workflow();
        work.setId(new Long(20));
        work.setWorkflowNo(new Long(2));

        user = new User();
        user.setEmpNo("80002");
        user.setNameE("Checker2 User");
        work.setUser(user);
        work.setCreatedBy(user);
        work.setUpdatedBy(user);

        work.setWorkflowType(WorkflowType.CHECKER);
        work.setWorkflowProcessStatus(WorkflowProcessStatus.CHECKED);

        user = new User();
        user.setEmpNo("80002");
        user.setNameE("Checker2 User");
        work.setFinishedBy(user);

        work.setFinishedAt(new GregorianCalendar(2009, 3, 1, 12, 34, 56).getTime());
        listworkflow.add(work);

        work = new Workflow();
        work.setId(new Long(30));
        work.setWorkflowNo(new Long(3));

        user = new User();
        user.setEmpNo("80003");
        user.setNameE("Approver User");
        work.setUser(user);
        work.setCreatedBy(user);
        work.setUpdatedBy(user);

        work.setWorkflowType(WorkflowType.APPROVER);
        work.setWorkflowProcessStatus(WorkflowProcessStatus.NONE);
        listworkflow.add(work);
        // -------------------------------------------
        List<Workflow> workflow = createWorkflowDummy();

        // 既に検証済みにCheckerを作成する為
        workflow.get(2).setWorkflowProcessStatus(WorkflowProcessStatus.CHECKED);

        MockWorkflowDao.RET_FIND_BY_CORRESPON_ID = listworkflow;

        c.setWorkflowStatus(WorkflowStatus.UNDER_CONSIDERATION);
        c.setWorkflows(listworkflow);
        WorkflowPattern wp = c.getCorresponType().getWorkflowPattern();
        wp.setWorkflowCd("pattern1");
        c.getCorresponType().setWorkflowPattern(wp);

        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        MockAbstractService.IS_SYSTEM_ADMIN = true;

        try {
            service.save(c, workflow);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            // 正しいエラーコードがセットされた例外がthrowされていることを検証
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_WORKFLOW_PATTERN_INVALID,
                actual.getMessageCode());
        }
    }

    /**
     * 承認フローが保存できるか検証
     * 承認フローパターンが1の際、Checkerが自分の前者を変更した際を検証する
     */
    @Test
    public void testSaveCheckerBeforeChange() throws Exception {
        // テストに必要な値を作成
        Correspon c = createCorrespon();
        MockCorresponDao.RET_FIND_BY_ID.put("1", c);

        List<ProjectUser> result = new ArrayList<ProjectUser>();
        ProjectUser pu;
        User u;
        u = new User();
        u.setEmpNo("80001");
        u.setNameE("Checker1 User");
        pu = new ProjectUser();
        pu.setUser(u);
        pu.setProjectId("PJ1");
        result.add(pu);

        u = new User();
        u.setEmpNo("80002");
        u.setNameE("Checker2 User");
        pu = new ProjectUser();
        pu.setUser(u);
        pu.setProjectId("PJ1");
        result.add(pu);

        u = new User();
        u.setEmpNo("80003");
        u.setNameE("Checker3 User");
        pu = new ProjectUser();
        pu.setUser(u);
        pu.setProjectId("PJ1");
        result.add(pu);
        MockUserDao.RET_FINDPROJECTUSER = result;

        // 検証用に承認フローリストを保持する
        // -------------------------------------------
        List<Workflow> listworkflow = new ArrayList<Workflow>();
        Workflow work = new Workflow();
        User user;

        work.setId(new Long(99));
        work.setWorkflowNo(new Long(1));

        user = new User();
        user.setEmpNo("80001");
        user.setNameE("Checker1 User");
        work.setUser(user);
        work.setCreatedBy(user);
        work.setUpdatedBy(user);

        work.setWorkflowType(WorkflowType.CHECKER);
        work.setWorkflowProcessStatus(WorkflowProcessStatus.CHECKED);
        listworkflow.add(work);

        work = new Workflow();
        work.setId(new Long(20));
        work.setWorkflowNo(new Long(2));

        user = new User();
        user.setEmpNo("80002");
        user.setNameE("Checker2 User");
        work.setUser(user);
        work.setCreatedBy(user);
        work.setUpdatedBy(user);

        work.setWorkflowType(WorkflowType.CHECKER);
        work.setWorkflowProcessStatus(WorkflowProcessStatus.CHECKED);

        user = new User();
        user.setEmpNo("80002");
        user.setNameE("Checker2 User");
        work.setFinishedBy(user);

        work.setFinishedAt(new GregorianCalendar(2009, 3, 1, 12, 34, 56).getTime());
        listworkflow.add(work);

        work = new Workflow();
        work.setId(new Long(30));
        work.setWorkflowNo(new Long(3));

        u = new User();
        u.setEmpNo("80003");
        u.setNameE("Approver User");
        work.setUser(u);
        work.setCreatedBy(user);
        work.setUpdatedBy(user);

        work.setWorkflowType(WorkflowType.APPROVER);
        work.setWorkflowProcessStatus(WorkflowProcessStatus.NONE);
        listworkflow.add(work);
        // -------------------------------------------
        List<Workflow> workflow = createWorkflowDummy();

        // 既に検証済みにCheckerの前に新規でCheckerを追加する為
        Workflow addWorkflow = new Workflow();
        User addUser = new User();

        addWorkflow.setId(new Long(0));
        addWorkflow.setWorkflowNo(new Long(1));

        addUser = new User();
        addUser.setEmpNo("80001");
        addUser.setNameE("Checker1 User");
        addWorkflow.setUser(addUser);
        addWorkflow.setCreatedBy(addUser);
        addWorkflow.setUpdatedBy(addUser);

        addWorkflow.setWorkflowType(WorkflowType.CHECKER);
        addWorkflow.setWorkflowProcessStatus(WorkflowProcessStatus.NONE);
        workflow.add(1, addWorkflow);

        MockWorkflowDao.RET_FIND_BY_CORRESPON_ID = listworkflow;

        c.setWorkflowStatus(WorkflowStatus.UNDER_CONSIDERATION);
        c.setWorkflows(listworkflow);
        WorkflowPattern wp = c.getCorresponType().getWorkflowPattern();
        wp.setWorkflowCd("pattern1");
        c.getCorresponType().setWorkflowPattern(wp);

        // ログインユーザー作成
        User loginUser = new User();
        loginUser.setEmpNo("80002");
        loginUser.setNameE("Checker2 User");

        MockAbstractService.CURRENT_USER = loginUser;

        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        List<Project> accessibleProjects = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        accessibleProjects.add(p);
        MockAbstractService.ACCESSIBLE_PROJECTS = accessibleProjects;

        MockAbstractService.IS_SYSTEM_ADMIN = false;
        try {
            service.save(c, workflow);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            // 正しいエラーコードがセットされた例外がthrowされていることを検証
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_WORKFLOW_PATTERN_INVALID,
                actual.getMessageCode());
        }
    }

    /**
     * 承認フローが保存できるか検証
     * 承認フローパターンが1の際、ProjectAdminが 既に検証済みのCheckerを変更した際を検証する
     */
    @Test
    public void testSaveCheckerCheckedChangeProjectAdmin() throws Exception {
        // テストに必要な値を作成
        Correspon c = createCorrespon();
        MockCorresponDao.RET_FIND_BY_ID.put("1", c);

        List<ProjectUser> result = new ArrayList<ProjectUser>();
        ProjectUser pu;
        User u;
        u = new User();
        u.setEmpNo("80001");
        u.setNameE("Checker1 User");
        pu = new ProjectUser();
        pu.setUser(u);
        pu.setProjectId("PJ1");
        result.add(pu);

        u = new User();
        u.setEmpNo("80002");
        u.setNameE("Checker2 User");
        pu = new ProjectUser();
        pu.setUser(u);
        pu.setProjectId("PJ1");
        result.add(pu);

        u = new User();
        u.setEmpNo("80003");
        u.setNameE("Checker3 User");
        pu = new ProjectUser();
        pu.setUser(u);
        pu.setProjectId("PJ1");
        result.add(pu);
        MockUserDao.RET_FINDPROJECTUSER = result;

        // 検証用に承認フローリストを保持する
        // -------------------------------------------
        List<Workflow> listworkflow = new ArrayList<Workflow>();
        Workflow work = new Workflow();
        User user;

        work.setId(new Long(99));
        work.setWorkflowNo(new Long(1));

        user = new User();
        user.setEmpNo("80001");
        user.setNameE("Checker1 User");
        work.setUser(user);
        work.setCreatedBy(user);
        work.setUpdatedBy(user);

        work.setWorkflowType(WorkflowType.CHECKER);
        work.setWorkflowProcessStatus(WorkflowProcessStatus.CHECKED);
        listworkflow.add(work);

        work = new Workflow();
        work.setId(new Long(20));
        work.setWorkflowNo(new Long(2));

        user = new User();
        user.setEmpNo("80002");
        user.setNameE("Checker2 User");
        work.setUser(user);
        work.setCreatedBy(user);
        work.setUpdatedBy(user);

        work.setWorkflowType(WorkflowType.CHECKER);
        work.setWorkflowProcessStatus(WorkflowProcessStatus.CHECKED);

        user = new User();
        user.setEmpNo("80002");
        user.setNameE("Checker2 User");
        work.setFinishedBy(user);

        work.setFinishedAt(new GregorianCalendar(2009, 3, 1, 12, 34, 56).getTime());
        listworkflow.add(work);

        work = new Workflow();
        work.setId(new Long(30));
        work.setWorkflowNo(new Long(3));

        user = new User();
        user.setEmpNo("80003");
        user.setNameE("Approver User");
        work.setUser(user);
        work.setCreatedBy(user);
        work.setUpdatedBy(user);

        work.setWorkflowType(WorkflowType.APPROVER);
        work.setWorkflowProcessStatus(WorkflowProcessStatus.NONE);
        listworkflow.add(work);
        // -------------------------------------------
        List<Workflow> workflow = createWorkflowDummy();

        // 既に検証済みのCheckerを変更する為
        workflow.remove(1);

        // おためし 削除して追加する
        user = new User();
        user.setEmpNo("80002");
        user.setNameE("Checker2 User");
        work = new Workflow();
        work.setUser(user);
        work.setCreatedBy(user);
        work.setUpdatedBy(user);

        work.setWorkflowType(WorkflowType.CHECKER);
        work.setWorkflowProcessStatus(WorkflowProcessStatus.NONE);

        workflow.add(work);

        // ------------------------------

        MockWorkflowDao.RET_FIND_BY_CORRESPON_ID = listworkflow;

        c.setWorkflowStatus(WorkflowStatus.UNDER_CONSIDERATION);
        c.setWorkflows(listworkflow);
        WorkflowPattern wp = c.getCorresponType().getWorkflowPattern();
        wp.setWorkflowCd("pattern1");
        c.getCorresponType().setWorkflowPattern(wp);

        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        List<Project> accessibleProjects = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        accessibleProjects.add(p);
        MockAbstractService.ACCESSIBLE_PROJECTS = accessibleProjects;

        MockAbstractService.IS_PROJECT_ADMIN = true;
        try {
            service.save(c, workflow);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            // 正しいエラーコードがセットされた例外がthrowされていることを検証
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_WORKFLOW_PATTERN_INVALID,
                actual.getMessageCode());
        }
    }

    /**
     * 承認フローが保存できるか検証
     * 承認フローパターンが2の際、ProjectAdminが 既に検証済みのCheckerを変更した際を検証する
     */
    @Test
    public void testSaveCheckerCheckedChangeProjectAdminPettern2() throws Exception {
        // テストに必要な値を作成
        Correspon c = createCorrespon();
        MockCorresponDao.RET_FIND_BY_ID.put("1", c);

        List<ProjectUser> result = new ArrayList<ProjectUser>();
        ProjectUser pu;
        User u;
        u = new User();
        u.setEmpNo("80001");
        u.setNameE("Checker1 User");
        pu = new ProjectUser();
        pu.setUser(u);
        pu.setProjectId("PJ1");
        result.add(pu);

        u = new User();
        u.setEmpNo("80002");
        u.setNameE("Checker2 User");
        pu = new ProjectUser();
        pu.setUser(u);
        pu.setProjectId("PJ1");
        result.add(pu);

        u = new User();
        u.setEmpNo("80003");
        u.setNameE("Checker3 User");
        pu = new ProjectUser();
        pu.setUser(u);
        pu.setProjectId("PJ1");
        result.add(pu);
        MockUserDao.RET_FINDPROJECTUSER = result;

        // 検証用に承認フローリストを保持する
        // -------------------------------------------
        List<Workflow> listworkflow = new ArrayList<Workflow>();
        Workflow work = new Workflow();
        User user;

        work.setId(new Long(99));
        work.setWorkflowNo(new Long(1));

        user = new User();
        user.setEmpNo("80001");
        user.setNameE("Checker1 User");
        work.setUser(user);
        work.setCreatedBy(user);
        work.setUpdatedBy(user);

        work.setWorkflowType(WorkflowType.CHECKER);
        work.setWorkflowProcessStatus(WorkflowProcessStatus.CHECKED);
        listworkflow.add(work);

        work = new Workflow();
        work.setId(new Long(20));
        work.setWorkflowNo(new Long(2));

        user = new User();
        user.setEmpNo("80002");
        user.setNameE("Checker2 User");
        work.setUser(user);
        work.setCreatedBy(user);
        work.setUpdatedBy(user);

        work.setWorkflowType(WorkflowType.CHECKER);
        work.setWorkflowProcessStatus(WorkflowProcessStatus.CHECKED);

        user = new User();
        user.setEmpNo("80002");
        user.setNameE("Checker2 User");
        work.setFinishedBy(user);

        work.setFinishedAt(new GregorianCalendar(2009, 3, 1, 12, 34, 56).getTime());
        listworkflow.add(work);

        work = new Workflow();
        work.setId(new Long(30));
        work.setWorkflowNo(new Long(3));

        user = new User();
        user.setEmpNo("80003");
        user.setNameE("Approver User");
        work.setUser(user);
        work.setCreatedBy(user);
        work.setUpdatedBy(user);

        work.setWorkflowType(WorkflowType.APPROVER);
        work.setWorkflowProcessStatus(WorkflowProcessStatus.NONE);
        listworkflow.add(work);
        // -------------------------------------------
        List<Workflow> workflow = createWorkflowDummy();

        // 既に検証済みのCheckerを変更する為
        workflow.remove(1);

        user = new User();
        user.setEmpNo("80002");
        user.setNameE("Checker2 User");
        work.setUser(user);
        work.setCreatedBy(user);
        work.setUpdatedBy(user);

        work.setWorkflowType(WorkflowType.CHECKER);
        work.setWorkflowProcessStatus(WorkflowProcessStatus.NONE);

        workflow.add(work);

        MockWorkflowDao.RET_FIND_BY_CORRESPON_ID = listworkflow;

        c.setWorkflowStatus(WorkflowStatus.UNDER_CONSIDERATION);
        c.setWorkflows(listworkflow);
        WorkflowPattern wp = c.getCorresponType().getWorkflowPattern();
        wp.setWorkflowCd("pattern2");
        c.getCorresponType().setWorkflowPattern(wp);

        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        List<Project> accessibleProjects = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        accessibleProjects.add(p);
        MockAbstractService.ACCESSIBLE_PROJECTS = accessibleProjects;

        MockAbstractService.IS_PROJECT_ADMIN = true;
        try {
            service.save(c, workflow);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            // 正しいエラーコードがセットされた例外がthrowされていることを検証
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_WORKFLOW_PATTERN_INVALID,
                actual.getMessageCode());
        }
    }

    /**
     * 承認フローが保存できるか検証
     * ログインユーザーがApproverの場合を検証
     */
    @Test
    public void testSaveLoginUserApprover() throws Exception {
        // テストに必要な値を作成
        Correspon c = createCorrespon();
        MockCorresponDao.RET_FIND_BY_ID.put("1", c);

        List<ProjectUser> result = new ArrayList<ProjectUser>();
        ProjectUser pu;
        User u;
        u = new User();
        u.setEmpNo("80001");
        u.setNameE("Checker1 User");
        pu = new ProjectUser();
        pu.setUser(u);
        pu.setProjectId("PJ1");
        result.add(pu);

        u = new User();
        u.setEmpNo("80002");
        u.setNameE("Checker2 User");
        pu = new ProjectUser();
        pu.setUser(u);
        pu.setProjectId("PJ1");
        result.add(pu);

        u = new User();
        u.setEmpNo("80003");
        u.setNameE("Checker3 User");
        pu = new ProjectUser();
        pu.setUser(u);
        pu.setProjectId("PJ1");
        result.add(pu);
        MockUserDao.RET_FINDPROJECTUSER = result;

        // 検証用に承認フローリストを保持する
        // -------------------------------------------
        List<Workflow> listworkflow = new ArrayList<Workflow>();
        Workflow work = new Workflow();
        User user;

        work.setId(new Long(99));
        work.setWorkflowNo(new Long(1));

        user = new User();
        user.setEmpNo("80001");
        user.setNameE("Checker1 User");
        work.setUser(user);
        work.setCreatedBy(user);
        work.setUpdatedBy(user);

        work.setWorkflowType(WorkflowType.CHECKER);
        work.setWorkflowProcessStatus(WorkflowProcessStatus.CHECKED);
        listworkflow.add(work);

        work = new Workflow();
        work.setId(new Long(20));
        work.setWorkflowNo(new Long(2));

        user = new User();
        user.setEmpNo("80002");
        user.setNameE("Checker2 User");
        work.setUser(user);
        work.setCreatedBy(user);
        work.setUpdatedBy(user);

        work.setWorkflowType(WorkflowType.CHECKER);
        work.setWorkflowProcessStatus(WorkflowProcessStatus.CHECKED);

        user = new User();
        user.setEmpNo("80002");
        user.setNameE("Checker2 User");
        work.setFinishedBy(user);

        work.setFinishedAt(new GregorianCalendar(2009, 3, 1, 12, 34, 56).getTime());
        listworkflow.add(work);

        work = new Workflow();
        work.setId(new Long(30));
        work.setWorkflowNo(new Long(3));

        u = new User();
        u.setEmpNo("80003");
        u.setNameE("Approver User");
        work.setUser(u);
        work.setCreatedBy(user);
        work.setUpdatedBy(user);

        work.setWorkflowType(WorkflowType.APPROVER);
        work.setWorkflowProcessStatus(WorkflowProcessStatus.NONE);
        listworkflow.add(work);
        // -------------------------------------------

        List<Workflow> workflow = createWorkflowDummy();

        // 既に検証済みにCheckerの前に新規でCheckerを追加する為
        Workflow addWorkflow = new Workflow();
        User addUser = new User();

        addWorkflow.setId(new Long(0));
        addWorkflow.setWorkflowNo(new Long(1));

        addUser = new User();
        addUser.setEmpNo("80001");
        addUser.setNameE("Checker1 User");
        addWorkflow.setUser(addUser);
        addWorkflow.setCreatedBy(addUser);
        addWorkflow.setUpdatedBy(addUser);

        addWorkflow.setWorkflowType(WorkflowType.CHECKER);
        addWorkflow.setWorkflowProcessStatus(WorkflowProcessStatus.NONE);
        workflow.add(1, addWorkflow);

        MockWorkflowDao.RET_FIND_BY_CORRESPON_ID = listworkflow;

        c.setWorkflowStatus(WorkflowStatus.UNDER_CONSIDERATION);
        c.setWorkflows(listworkflow);
        WorkflowPattern wp = c.getCorresponType().getWorkflowPattern();
        wp.setWorkflowCd("pattern1");
        c.getCorresponType().setWorkflowPattern(wp);

        // ログインユーザー作成
        User loginUser = new User();
        loginUser.setEmpNo("80003");
        loginUser.setNameE("Approver User");

        MockAbstractService.CURRENT_USER = loginUser;

        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        List<Project> accessibleProjects = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        accessibleProjects.add(p);
        MockAbstractService.ACCESSIBLE_PROJECTS = accessibleProjects;

        MockAbstractService.IS_SYSTEM_ADMIN = false;
        try {
            service.save(c, workflow);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            // 正しいエラーコードがセットされた例外がthrowされていることを検証
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT,
                actual.getMessageCode());
        }
    }

    /**
     * Checkerが重複.
     * 例外発生
     */
    @Test
    public void testSaveRepetitionChecker() throws Exception {
        // ダミーの戻り値をセット
        Correspon c = createCorrespon();
        MockCorresponDao.RET_FIND_BY_ID.put("1", c);
        c.setWorkflowStatus(WorkflowStatus.DRAFT);

        List<ProjectUser> result = new ArrayList<ProjectUser>();
        ProjectUser pu;
        User u;
        u = new User();
        u.setEmpNo("80001");
        u.setNameE("Checker1 User");
        pu = new ProjectUser();
        pu.setUser(u);
        pu.setProjectId("PJ1");
        result.add(pu);

        u = new User();
        u.setEmpNo("80002");
        u.setNameE("Checker2 User");
        pu = new ProjectUser();
        pu.setUser(u);
        pu.setProjectId("PJ1");
        result.add(pu);

        u = new User();
        u.setEmpNo("80003");
        u.setNameE("Checker3 User");
        pu = new ProjectUser();
        pu.setUser(u);
        pu.setProjectId("PJ1");
        result.add(pu);
        MockUserDao.RET_FINDPROJECTUSER = result;

        List<Workflow> workflow = createWorkflowDummyStatusNone();

        List<Workflow> workflows = createWorkflowDummy();
        // 重複用Checker
        Workflow approverWorkflow = new Workflow();
        approverWorkflow = workflow.get(workflows.size() - 1);
        workflows.remove(workflows.size() - 1);

        Workflow w = new Workflow();
        w.setId(new Long(40));
        w.setWorkflowNo(new Long(1));

        u = new User();
        u.setEmpNo("80001");
        u.setNameE("Checker1 User");
        w.setUser(u);
        w.setCreatedBy(u);
        w.setUpdatedBy(u);

        w.setWorkflowType(WorkflowType.CHECKER);
        w.setWorkflowProcessStatus(WorkflowProcessStatus.NONE);

        workflows.add(w);
        workflows.add(approverWorkflow);

        MockWorkflowDao.RET_FIND_BY_CORRESPON_ID = workflow;

        // コレポン文書にワークフローをセット
        c.setWorkflows(workflow);

        List<Project> projects = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        projects.add(p);

        MockAbstractService.ACCESSIBLE_PROJECTS = projects;

        // ログインユーザー作成
        User loginUser = new User();
        loginUser.setEmpNo("ZZA01");
        loginUser.setNameE("Taro Yamada");

        MockAbstractService.CURRENT_USER = loginUser;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        MockAbstractService.IS_GROUP_ADMIN = null;

        MockWorkflowDao.RET_CREATE = 1L;
        try {
            service.save(c, workflows);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            // 正しいエラーコードがセットされた例外がthrowされていることを検証
            assertEquals(ApplicationMessageCode.DUPLICATED_CHECKER, actual.getMessageCode());
            assertEquals(1, actual.getMessageVars().length);
            assertEquals("80001", actual.getMessageVars()[0]);
        }

    }

    /**
     * Approverが重複.
     * 例外発生
     */
    @Test
    public void testSaveRepetitionApprover() throws Exception {
        // ダミーの戻り値をセット
        Correspon c = createCorrespon();
        MockCorresponDao.RET_FIND_BY_ID.put("1", c);
        c.setWorkflowStatus(WorkflowStatus.DRAFT);

        List<ProjectUser> result = new ArrayList<ProjectUser>();
        ProjectUser pu;
        User u;
        u = new User();
        u.setEmpNo("80001");
        u.setNameE("Checker1 User");
        pu = new ProjectUser();
        pu.setUser(u);
        pu.setProjectId("PJ1");
        result.add(pu);

        u = new User();
        u.setEmpNo("80002");
        u.setNameE("Checker2 User");
        pu = new ProjectUser();
        pu.setUser(u);
        pu.setProjectId("PJ1");
        result.add(pu);

        u = new User();
        u.setEmpNo("80003");
        u.setNameE("Checker3 User");
        pu = new ProjectUser();
        pu.setUser(u);
        pu.setProjectId("PJ1");
        result.add(pu);
        MockUserDao.RET_FINDPROJECTUSER = result;

        List<Workflow> workflow = createWorkflowDummyStatusNone();

        List<Workflow> workflows = createWorkflowDummy();
        // 重複用Checker
        Workflow approverWorkflow = new Workflow();
        approverWorkflow = workflows.get(workflows.size() - 1);
        workflows.remove(workflows.size() - 1);

        Workflow w = new Workflow();
        w.setId(new Long(40));
        w.setWorkflowNo(new Long(1));

        u = new User();
        u.setEmpNo("80003");
        u.setNameE("Approver User");
        w.setUser(u);
        w.setCreatedBy(u);
        w.setUpdatedBy(u);

        w.setWorkflowType(WorkflowType.CHECKER);
        w.setWorkflowProcessStatus(WorkflowProcessStatus.NONE);

        workflows.add(w);
        workflows.add(approverWorkflow);

        MockWorkflowDao.RET_FIND_BY_CORRESPON_ID = workflow;

        // コレポン文書にワークフローをセット
        c.setWorkflows(workflow);

        List<Project> projects = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        projects.add(p);

        MockAbstractService.ACCESSIBLE_PROJECTS = projects;

        // ログインユーザー作成
        User loginUser = new User();
        loginUser.setEmpNo("ZZA01");
        loginUser.setNameE("Taro Yamada");

        MockAbstractService.CURRENT_USER = loginUser;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        MockAbstractService.IS_GROUP_ADMIN = null;

        MockWorkflowDao.RET_CREATE = 1L;
        try {
            service.save(c, workflows);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            // 正しいエラーコードがセットされた例外がthrowされていることを検証
            assertEquals(ApplicationMessageCode.DUPLICATED_CHECKER_APPROVER, actual
                .getMessageCode());
            assertEquals(1, actual.getMessageVars().length);
            assertEquals("80003", actual.getMessageVars()[0]);


        }
    }

    /**
     * 承認フローパターン1.
     * ログインユーザーがChecker.
     */
    @Test
    public void testSaveLoginUserIsCheckerPettern1() throws Exception {
        // ダミーの戻り値をセット
        Correspon c = createCorrespon();
        c.setWorkflowStatus(WorkflowStatus.REQUEST_FOR_CHECK);
        MockCorresponDao.RET_FIND_BY_ID.put("1", c);

        List<ProjectUser> result = new ArrayList<ProjectUser>();
        ProjectUser pu;
        User u;
        u = new User();
        u.setEmpNo("80001");
        u.setNameE("Checker1 User");
        pu = new ProjectUser();
        pu.setUser(u);
        pu.setProjectId("PJ1");
        result.add(pu);

        u = new User();
        u.setEmpNo("80002");
        u.setNameE("Checker2 User");
        pu = new ProjectUser();
        pu.setUser(u);
        pu.setProjectId("PJ1");
        result.add(pu);

        u = new User();
        u.setEmpNo("80003");
        u.setNameE("Checker3 User");
        pu = new ProjectUser();
        pu.setUser(u);
        pu.setProjectId("PJ1");
        result.add(pu);
        MockUserDao.RET_FINDPROJECTUSER = result;

        List<Workflow> workflow = createWorkflowDummyStatusNone();

        MockWorkflowDao.RET_FIND_BY_CORRESPON_ID = workflow;

        // コレポン文書にワークフローをセット
        c.setWorkflows(workflow);

        List<Project> projects = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        projects.add(p);

        MockAbstractService.ACCESSIBLE_PROJECTS = projects;

        WorkflowPattern wp = c.getCorresponType().getWorkflowPattern();
        wp.setWorkflowCd("001");
        c.getCorresponType().setWorkflowPattern(wp);

        // ログインユーザーがChecker
        User loginUser = new User();
        loginUser.setEmpNo("ZZA02");
        loginUser.setNameE("Tetsuo Aoki");

        MockAbstractService.CURRENT_USER = loginUser;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        MockAbstractService.IS_GROUP_ADMIN = null;

        MockWorkflowDao.RET_CREATE = 1L;
        List<Workflow> workflows = createWorkflow();
        //  自身の承認作業状態をREQUEST_FOR_CHECKにして検証開始
        for (Workflow w : workflows) {
            if (w.getUser().getEmpNo().equals(loginUser.getEmpNo())) {
                w.setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_CHECK);
            }
        }
        service.save(c, workflows);

        List<Workflow> resultWorkflows = new ArrayList<Workflow>();

        resultWorkflows.add((Workflow) workflows.get(2));

        assertEquals(resultWorkflows.toString(), MockWorkflowDao.ACT_CREATE.toString());

    }

    /**
     * 承認フローパターン2.
     * ログインユーザーがSystemAdmin,Approverを変更.
     */
    @Test
    public void testSaveLoginUserIsSystemAdminPettern2() throws Exception {
        // ダミーの戻り値をセット
        Correspon c = createCorrespon();
        c.setWorkflowStatus(WorkflowStatus.REQUEST_FOR_CHECK);
        MockCorresponDao.RET_FIND_BY_ID.put("1", c);

        MockUserDao.RET_FINDPROJECTUSER = createProjectUserList();

        List<Workflow> workflow = createWorkflow();

        MockWorkflowDao.RET_FIND_BY_CORRESPON_ID = workflow;

        // コレポン文書にワークフローをセット
        c.setWorkflows(workflow);

        List<Project> projects = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        projects.add(p);

        MockAbstractService.ACCESSIBLE_PROJECTS = projects;

        WorkflowPattern wp = c.getCorresponType().getWorkflowPattern();
        // 承認フローパターン2
        wp.setWorkflowCd("002");
        c.getCorresponType().setWorkflowPattern(wp);

        User loginUser = new User();
        loginUser.setEmpNo("ZZA05");
        loginUser.setNameE("Keiichi Ogiwara");

        MockAbstractService.CURRENT_USER = loginUser;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        // SystemAdmin
        MockAbstractService.IS_SYSTEM_ADMIN = true;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        MockAbstractService.IS_GROUP_ADMIN = null;
        MockAbstractService.IS_ANY_GROUP_ADMIN = false;

        MockWorkflowDao.RET_CREATE = 1L;
        List<Workflow> workflows = createWorkflow();
        // Approverのみ変更
        User approver = new User();
        approver.setEmpNo("ZZA05");
        approver.setNameE("Keiichi Ogiwara");
        workflows.get(workflows.size() - 1).setUser(approver);
        workflows.get(workflows.size() - 1).setId(0L);

        // テスト実行
        service.save(c, workflows);

        List<Workflow> resultWorkflows = createWorkflow();
        List<Workflow> actual = MockWorkflowDao.ACT_CREATE;

        assertTrue(1 == actual.size());
        assertEquals(c.getId(), actual.get(0).getCorresponId());
        assertEquals(approver.getEmpNo(), actual.get(0).getUser().getEmpNo());
        assertEquals(resultWorkflows.get(2).getWorkflowType(), actual.get(0).getWorkflowType());
        assertEquals(resultWorkflows.get(2).getWorkflowNo(), actual.get(0).getWorkflowNo());
        assertEquals(resultWorkflows.get(2).getWorkflowProcessStatus(), actual.get(0).getWorkflowProcessStatus());
        assertEquals(resultWorkflows.get(2).getCommentOn(), actual.get(0).getCommentOn());
        assertEquals(resultWorkflows.get(2).getCreatedBy().getEmpNo(), actual.get(0).getCreatedBy().getEmpNo());
        assertNull(actual.get(0).getCreatedAt());
        assertEquals(resultWorkflows.get(2).getUpdatedBy().getEmpNo(), actual.get(0).getUpdatedBy().getEmpNo());
        assertNull(actual.get(0).getUpdatedAt());
        assertEquals(resultWorkflows.get(2).getVersionNo(), actual.get(0).getVersionNo());
        assertNull(actual.get(0).getDeleteNo());
    }

    /**
     * 承認フローパターン2.
     * ログインユーザーがSystemAdmin,Checkerを変更.
     */
    @Test
    public void testSaveLoginUserIsSystemAdminPettern2ChangeChecker() throws Exception {
        // ダミーの戻り値をセット
        Correspon c = createCorrespon();
        c.setWorkflowStatus(WorkflowStatus.REQUEST_FOR_CHECK);
        MockCorresponDao.RET_FIND_BY_ID.put("1", c);

        MockUserDao.RET_FINDPROJECTUSER = createProjectUserList();

        List<Workflow> workflow = createWorkflow();

        MockWorkflowDao.RET_FIND_BY_CORRESPON_ID = workflow;

        // コレポン文書にワークフローをセット
        c.setWorkflows(workflow);

        List<Project> projects = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        projects.add(p);

        MockAbstractService.ACCESSIBLE_PROJECTS = projects;

        WorkflowPattern wp = c.getCorresponType().getWorkflowPattern();
        // 承認フローパターン2
        wp.setWorkflowCd("002");
        c.getCorresponType().setWorkflowPattern(wp);

        User loginUser = new User();
        loginUser.setEmpNo("ZZA05");
        loginUser.setNameE("Keiichi Ogiwara");

        MockAbstractService.CURRENT_USER = loginUser;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        // SystemAdmin
        MockAbstractService.IS_SYSTEM_ADMIN = true;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        MockAbstractService.IS_GROUP_ADMIN = null;
        MockAbstractService.IS_ANY_GROUP_ADMIN = false;

        MockWorkflowDao.RET_CREATE = 1L;
        List<Workflow> workflows = createWorkflow();

        // Checkerを変更
        User checker = new User();
        checker.setEmpNo("ZZA06");
        checker.setNameE("TEST6");
        workflows.get(1).setUser(checker);
        workflows.get(1).setId(0L);

        try {
            // テスト実行
            service.save(c, workflows);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_NOT_ALLOW_CHANGE_CHECKER, actual.getMessageCode());
        }
    }

    /**
     * 承認フローパターン2.
     * ログインユーザーがProjectAdmin,Approverを変更.
     */
    @Test
    public void testSaveLoginUserIsProjectAdminPettern2() throws Exception {
        // ダミーの戻り値をセット
        Correspon c = createCorrespon();
        c.setWorkflowStatus(WorkflowStatus.REQUEST_FOR_CHECK);
        MockCorresponDao.RET_FIND_BY_ID.put("1", c);

        MockUserDao.RET_FINDPROJECTUSER = createProjectUserList();

        List<Workflow> workflow = createWorkflow();

        MockWorkflowDao.RET_FIND_BY_CORRESPON_ID = workflow;

        // コレポン文書にワークフローをセット
        c.setWorkflows(workflow);

        List<Project> projects = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        projects.add(p);

        MockAbstractService.ACCESSIBLE_PROJECTS = projects;

        WorkflowPattern wp = c.getCorresponType().getWorkflowPattern();
        // 承認フローパターン2
        wp.setWorkflowCd("002");
        c.getCorresponType().setWorkflowPattern(wp);

        User loginUser = new User();
        loginUser.setEmpNo("ZZA05");
        loginUser.setNameE("Keiichi Ogiwara");

        MockAbstractService.CURRENT_USER = loginUser;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        // SystemAdmin
        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN = true;
        MockAbstractService.IS_GROUP_ADMIN = null;
        MockAbstractService.IS_ANY_GROUP_ADMIN = false;

        MockWorkflowDao.RET_CREATE = 1L;
        List<Workflow> workflows = createWorkflow();
        // Approverのみ変更
        User approver = new User();
        approver.setEmpNo("ZZA05");
        approver.setNameE("Keiichi Ogiwara");
        workflows.get(workflows.size() - 1).setUser(approver);
        workflows.get(workflows.size() - 1).setId(0L);

        // テスト実行
        service.save(c, workflows);

        List<Workflow> resultWorkflows = createWorkflow();
        List<Workflow> actual = MockWorkflowDao.ACT_CREATE;

        assertTrue(1 == actual.size());
        assertEquals(c.getId(), actual.get(0).getCorresponId());
        assertEquals(approver.getEmpNo(), actual.get(0).getUser().getEmpNo());
        assertEquals(resultWorkflows.get(2).getWorkflowType(), actual.get(0).getWorkflowType());
        assertEquals(resultWorkflows.get(2).getWorkflowNo(), actual.get(0).getWorkflowNo());
        assertEquals(resultWorkflows.get(2).getWorkflowProcessStatus(), actual.get(0).getWorkflowProcessStatus());
        assertEquals(resultWorkflows.get(2).getCommentOn(), actual.get(0).getCommentOn());
        assertEquals(resultWorkflows.get(2).getCreatedBy().getEmpNo(), actual.get(0).getCreatedBy().getEmpNo());
        assertNull(actual.get(0).getCreatedAt());
        assertEquals(resultWorkflows.get(2).getUpdatedBy().getEmpNo(), actual.get(0).getUpdatedBy().getEmpNo());
        assertNull(actual.get(0).getUpdatedAt());
        assertEquals(resultWorkflows.get(2).getVersionNo(), actual.get(0).getVersionNo());
        assertNull(actual.get(0).getDeleteNo());
    }

    /**
     * 承認フローパターン2.
     * ログインユーザーがProjectAdmin,Checkerを変更.
     */
    @Test
    public void testSaveLoginUserIsProjectAdminPettern2ChangeChecker() throws Exception {
        // ダミーの戻り値をセット
        Correspon c = createCorrespon();
        c.setWorkflowStatus(WorkflowStatus.REQUEST_FOR_CHECK);
        MockCorresponDao.RET_FIND_BY_ID.put("1", c);

        MockUserDao.RET_FINDPROJECTUSER = createProjectUserList();

        List<Workflow> workflow = createWorkflow();

        MockWorkflowDao.RET_FIND_BY_CORRESPON_ID = workflow;

        // コレポン文書にワークフローをセット
        c.setWorkflows(workflow);

        List<Project> projects = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        projects.add(p);

        MockAbstractService.ACCESSIBLE_PROJECTS = projects;

        WorkflowPattern wp = c.getCorresponType().getWorkflowPattern();
        // 承認フローパターン2
        wp.setWorkflowCd("002");
        c.getCorresponType().setWorkflowPattern(wp);

        User loginUser = new User();
        loginUser.setEmpNo("ZZA05");
        loginUser.setNameE("Keiichi Ogiwara");

        MockAbstractService.CURRENT_USER = loginUser;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        // SystemAdmin
        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN = true;
        MockAbstractService.IS_GROUP_ADMIN = null;
        MockAbstractService.IS_ANY_GROUP_ADMIN = false;

        MockWorkflowDao.RET_CREATE = 1L;
        List<Workflow> workflows = createWorkflow();

        // Checkerを変更
        User checker = new User();
        checker.setEmpNo("ZZA06");
        checker.setNameE("TEST6");
        workflows.get(1).setUser(checker);
        workflows.get(1).setId(0L);

        try {
            // テスト実行
            service.save(c, workflows);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_NOT_ALLOW_CHANGE_CHECKER, actual.getMessageCode());
        }
    }

    /**
     * 承認フローパターン2.
     * ログインユーザーがGroupAdmin,Approverを変更.
     */
    @Test
    public void testSaveLoginUserIsGroupAdminPettern2() throws Exception {
        // ダミーの戻り値をセット
        Correspon c = createCorrespon();
        c.setWorkflowStatus(WorkflowStatus.REQUEST_FOR_CHECK);
        MockCorresponDao.RET_FIND_BY_ID.put("1", c);

        MockUserDao.RET_FINDPROJECTUSER = createProjectUserList();

        List<Workflow> workflow = createWorkflow();

        MockWorkflowDao.RET_FIND_BY_CORRESPON_ID = workflow;

        // コレポン文書にワークフローをセット
        c.setWorkflows(workflow);

        List<Project> projects = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        projects.add(p);

        MockAbstractService.ACCESSIBLE_PROJECTS = projects;

        WorkflowPattern wp = c.getCorresponType().getWorkflowPattern();
        // 承認フローパターン2
        wp.setWorkflowCd("002");
        c.getCorresponType().setWorkflowPattern(wp);

        User loginUser = new User();
        loginUser.setEmpNo("ZZA05");
        loginUser.setNameE("Keiichi Ogiwara");

        MockAbstractService.CURRENT_USER = loginUser;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        // SystemAdmin
        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        MockAbstractService.IS_GROUP_ADMIN = null;
        MockAbstractService.IS_ANY_GROUP_ADMIN = false;
        List<Long> groupAdmin = new ArrayList<Long>();
        groupAdmin.add(5963L);
        MockAbstractService.IS_GROUP_ADMIN = groupAdmin;

        CorresponGroup cg = new CorresponGroup();
        cg.setId(5963L);
        c.setFromCorresponGroup(cg);


        MockWorkflowDao.RET_CREATE = 1L;
        List<Workflow> workflows = createWorkflow();
        // Approverのみ変更
        User approver = new User();
        approver.setEmpNo("ZZA05");
        approver.setNameE("Keiichi Ogiwara");
        workflows.get(workflows.size() - 1).setUser(approver);
        workflows.get(workflows.size() - 1).setId(0L);

        // テスト実行
        service.save(c, workflows);

        List<Workflow> resultWorkflows = createWorkflow();
        List<Workflow> actual = MockWorkflowDao.ACT_CREATE;

        assertTrue(1 == actual.size());
        assertEquals(c.getId(), actual.get(0).getCorresponId());
        assertEquals(approver.getEmpNo(), actual.get(0).getUser().getEmpNo());
        assertEquals(resultWorkflows.get(2).getWorkflowType(), actual.get(0).getWorkflowType());
        assertEquals(resultWorkflows.get(2).getWorkflowNo(), actual.get(0).getWorkflowNo());
        assertEquals(resultWorkflows.get(2).getWorkflowProcessStatus(), actual.get(0).getWorkflowProcessStatus());
        assertEquals(resultWorkflows.get(2).getCommentOn(), actual.get(0).getCommentOn());
        assertEquals(resultWorkflows.get(2).getCreatedBy().getEmpNo(), actual.get(0).getCreatedBy().getEmpNo());
        assertNull(actual.get(0).getCreatedAt());
        assertEquals(resultWorkflows.get(2).getUpdatedBy().getEmpNo(), actual.get(0).getUpdatedBy().getEmpNo());
        assertNull(actual.get(0).getUpdatedAt());
        assertEquals(resultWorkflows.get(2).getVersionNo(), actual.get(0).getVersionNo());
        assertNull(actual.get(0).getDeleteNo());
    }

    /**
     * 承認フローパターン2.
     * ログインユーザーがGroupAdmin,Checkerを変更.
     */
    @Test
    public void testSaveLoginUserIsGroupAdminPettern2ChangeChecker() throws Exception {
        // ダミーの戻り値をセット
        Correspon c = createCorrespon();
        c.setWorkflowStatus(WorkflowStatus.REQUEST_FOR_CHECK);
        MockCorresponDao.RET_FIND_BY_ID.put("1", c);

        MockUserDao.RET_FINDPROJECTUSER = createProjectUserList();

        List<Workflow> workflow = createWorkflow();

        MockWorkflowDao.RET_FIND_BY_CORRESPON_ID = workflow;

        // コレポン文書にワークフローをセット
        c.setWorkflows(workflow);

        List<Project> projects = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        projects.add(p);

        MockAbstractService.ACCESSIBLE_PROJECTS = projects;

        WorkflowPattern wp = c.getCorresponType().getWorkflowPattern();
        // 承認フローパターン2
        wp.setWorkflowCd("002");
        c.getCorresponType().setWorkflowPattern(wp);

        User loginUser = new User();
        loginUser.setEmpNo("ZZA05");
        loginUser.setNameE("Keiichi Ogiwara");

        MockAbstractService.CURRENT_USER = loginUser;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        // SystemAdmin
        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        MockAbstractService.IS_GROUP_ADMIN = null;
        MockAbstractService.IS_ANY_GROUP_ADMIN = false;
        List<Long> groupAdmin = new ArrayList<Long>();
        groupAdmin.add(5963L);
        MockAbstractService.IS_GROUP_ADMIN = groupAdmin;
        CorresponGroup cg = new CorresponGroup();
        cg.setId(5963L);
        c.setFromCorresponGroup(cg);

        MockWorkflowDao.RET_CREATE = 1L;
        List<Workflow> workflows = createWorkflow();

        // Checkerを変更
        User checker = new User();
        checker.setEmpNo("ZZA06");
        checker.setNameE("TEST6");
        workflows.get(1).setUser(checker);
        workflows.get(1).setId(0L);

        try {
            // テスト実行
            service.save(c, workflows);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_NOT_ALLOW_CHANGE_CHECKER, actual.getMessageCode());
        }
    }

    /**
     * 承認フローパターン2.
     * ログインユーザーがCheckerで、Approverを変更.
     */
    @Test
    public void testSaveLoginUserIsCheckerPettern2() throws Exception {
        // ダミーの戻り値をセット
        Correspon c = createCorrespon();
        c.setWorkflowStatus(WorkflowStatus.REQUEST_FOR_CHECK);
        MockCorresponDao.RET_FIND_BY_ID.put("1", c);

        MockUserDao.RET_FINDPROJECTUSER = createProjectUserList();

        List<Workflow> workflow = createWorkflow();

        MockWorkflowDao.RET_FIND_BY_CORRESPON_ID = workflow;

        // コレポン文書にワークフローをセット
        c.setWorkflows(workflow);

        List<Project> projects = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        projects.add(p);

        MockAbstractService.ACCESSIBLE_PROJECTS = projects;

        WorkflowPattern wp = c.getCorresponType().getWorkflowPattern();
        // 承認フローパターン2
        wp.setWorkflowCd("002");
        c.getCorresponType().setWorkflowPattern(wp);

        // ログインユーザーがChecker
        User loginUser = new User();
        loginUser.setEmpNo("ZZA02");
        loginUser.setNameE("Tetsuo Aoki");

        MockAbstractService.CURRENT_USER = loginUser;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        // SystemAdmin
        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        MockAbstractService.IS_GROUP_ADMIN = null;
        MockAbstractService.IS_ANY_GROUP_ADMIN = false;

        MockWorkflowDao.RET_CREATE = 1L;
        List<Workflow> workflows = createWorkflow();
        // Approverのみ変更
        User approver = new User();
        approver.setEmpNo("ZZA05");
        approver.setNameE("Keiichi Ogiwara");
        workflows.get(workflows.size() - 1).setUser(approver);
        workflows.get(workflows.size() - 1).setId(0L);

        // テスト実行
        service.save(c, workflows);

        List<Workflow> resultWorkflows = createWorkflow();
        List<Workflow> actual = MockWorkflowDao.ACT_CREATE;

        assertTrue(1 == actual.size());
        assertEquals(c.getId(), actual.get(0).getCorresponId());
        assertEquals(approver.getEmpNo(), actual.get(0).getUser().getEmpNo());
        assertEquals(resultWorkflows.get(2).getWorkflowType(), actual.get(0).getWorkflowType());
        assertEquals(resultWorkflows.get(2).getWorkflowNo(), actual.get(0).getWorkflowNo());
        assertEquals(resultWorkflows.get(2).getWorkflowProcessStatus(), actual.get(0).getWorkflowProcessStatus());
        assertEquals(resultWorkflows.get(2).getCommentOn(), actual.get(0).getCommentOn());
        assertEquals(resultWorkflows.get(2).getCreatedBy().getEmpNo(), actual.get(0).getCreatedBy().getEmpNo());
        assertNull(actual.get(0).getCreatedAt());
        assertEquals(resultWorkflows.get(2).getUpdatedBy().getEmpNo(), actual.get(0).getUpdatedBy().getEmpNo());
        assertNull(actual.get(0).getUpdatedAt());
        assertEquals(resultWorkflows.get(2).getVersionNo(), actual.get(0).getVersionNo());
        assertNull(actual.get(0).getDeleteNo());
    }

    /**
     * 承認フローパターン2.
     * ログインユーザーがChecker,CheckerとApproverを変更.
     */
    @Test
    public void testSaveLoginUserIsCheckerPettern2ChangeCheckerApprover() throws Exception {
        // ダミーの戻り値をセット
        Correspon c = createCorrespon();
        c.setWorkflowStatus(WorkflowStatus.REQUEST_FOR_CHECK);
        MockCorresponDao.RET_FIND_BY_ID.put("1", c);

        MockUserDao.RET_FINDPROJECTUSER = createProjectUserList();

        List<Workflow> workflow = createWorkflow();

        MockWorkflowDao.RET_FIND_BY_CORRESPON_ID = workflow;

        // コレポン文書にワークフローをセット
        c.setWorkflows(workflow);

        List<Project> projects = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        projects.add(p);

        MockAbstractService.ACCESSIBLE_PROJECTS = projects;

        WorkflowPattern wp = c.getCorresponType().getWorkflowPattern();
        // 承認フローパターン2
        wp.setWorkflowCd("002");
        c.getCorresponType().setWorkflowPattern(wp);

        // ログインユーザーがChecker
        User loginUser = new User();
        loginUser.setEmpNo("ZZA04");
        loginUser.setNameE("Tomoko Okada");

        MockAbstractService.CURRENT_USER = loginUser;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        // SystemAdmin
        MockAbstractService.IS_SYSTEM_ADMIN = true;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        MockAbstractService.IS_GROUP_ADMIN = null;
        MockAbstractService.IS_ANY_GROUP_ADMIN = false;

        MockWorkflowDao.RET_CREATE = 1L;
        List<Workflow> workflows = createWorkflow();

        // Checkerを変更
        User checker = new User();
        checker.setEmpNo("ZZA06");
        checker.setNameE("TEST6");
        workflows.get(1).setUser(checker);
        workflows.get(1).setId(0L);

        try {
            // テスト実行
            service.save(c, workflows);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_NOT_ALLOW_CHANGE_CHECKER, actual.getMessageCode());
        }
    }

    /**
     * 承認フローパターン2.
     * ログインユーザーがApprover,CheckerとApproverを変更.
     */
    @Test
    public void testSaveLoginUserIsApproverPettern2ChangeCheckerApprover() throws Exception {
        // ダミーの戻り値をセット
        Correspon c = createCorrespon();
        c.setWorkflowStatus(WorkflowStatus.REQUEST_FOR_CHECK);
        MockCorresponDao.RET_FIND_BY_ID.put("1", c);

        MockUserDao.RET_FINDPROJECTUSER = createProjectUserList();

        List<Workflow> workflow = createWorkflow();

        MockWorkflowDao.RET_FIND_BY_CORRESPON_ID = workflow;

        // コレポン文書にワークフローをセット
        c.setWorkflows(workflow);

        List<Project> projects = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        projects.add(p);

        MockAbstractService.ACCESSIBLE_PROJECTS = projects;

        WorkflowPattern wp = c.getCorresponType().getWorkflowPattern();
        // 承認フローパターン2
        wp.setWorkflowCd("002");
        c.getCorresponType().setWorkflowPattern(wp);

        // ログインユーザーがApprover
        User loginUser = new User();
        loginUser.setEmpNo("ZZA03");
        loginUser.setNameE("Atsushi Ishida");

        MockAbstractService.CURRENT_USER = loginUser;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";

        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        MockAbstractService.IS_GROUP_ADMIN = null;
        MockAbstractService.IS_ANY_GROUP_ADMIN = false;

        MockWorkflowDao.RET_CREATE = 1L;
        List<Workflow> workflows = createWorkflow();

        // Checkerを変更
        User checker = new User();
        checker.setEmpNo("ZZA06");
        checker.setNameE("TEST6");
        workflows.get(1).setUser(checker);
        workflows.get(1).setId(0L);

        // Approver変更
        User approver = new User();
        approver.setEmpNo("ZZA05");
        approver.setNameE("Keiichi Ogiwara");
        workflows.get(workflows.size() - 1).setUser(approver);
        workflows.get(workflows.size() - 1).setId(0L);

        try {
            // テスト実行
            service.save(c, workflows);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT, actual.getMessageCode());
        }
    }

    /**
     * ログインユーザーが、指定されたコレポン文書を検証済にするテスト.
     * Pattern1でほかに検証前のCheckerがいるパターン.
     * @throws Exception
     */
    @Test
    public void testCheckPattern1_1() throws Exception {
        Date expectedDate = new GregorianCalendar(2009, 4, 1, 1, 1, 1).getTime();
        MockDateUtil.RET_GETNOW = expectedDate;

        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        List<Project> accessibleProjects = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        accessibleProjects.add(p);
        MockAbstractService.ACCESSIBLE_PROJECTS = accessibleProjects;

        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        MockAbstractService.IS_ANY_GROUP_ADMIN = false;

        // 引数の作成
        String expectedEmpNo = "80001";
        User user = new User();
        user.setEmpNo(expectedEmpNo);
        MockAbstractService.CURRENT_USER = user;

        List<ProjectUser> mockUser = new ArrayList<ProjectUser>();
        ProjectUser pu = new ProjectUser();
        pu.setUser(user);
        pu.setProjectId("PJ1");
        mockUser.add(pu);
        MockUserDao.RET_FINDPROJECTUSER = mockUser;

        Correspon correspon = createCorrespon();
        List<Workflow> workflowList = createWorkflowDummy();
        workflowList.get(0).setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_CHECK);
        workflowList.get(1).setWorkflowProcessStatus(WorkflowProcessStatus.NONE);
        correspon.setWorkflows(workflowList);

        String expectedComment = "Comment**********";
        Workflow workflow = workflowList.get(0);
        workflow.setCommentOn(expectedComment);

        // 検証用のオブジェクト
        Correspon expCorrespon = new Correspon();
        expCorrespon.setId(correspon.getId());
        expCorrespon.setUpdatedBy(user);
        expCorrespon.setWorkflowStatus(WorkflowStatus.UNDER_CONSIDERATION);
        expCorrespon.setVersionNo(correspon.getVersionNo());

        Workflow expWorkflow = new Workflow();
        expWorkflow.setId(workflow.getId());
        expWorkflow.setUpdatedBy(user);
        expWorkflow.setWorkflowProcessStatus(WorkflowProcessStatus.CHECKED);
        expWorkflow.setCommentOn(expectedComment);
        expWorkflow.setFinishedAt(expectedDate);
        expWorkflow.setFinishedBy(user);
        expWorkflow.setVersionNo(workflow.getVersionNo());

        Workflow expWorkflowNext = new Workflow();
        expWorkflowNext.setId(workflowList.get(1).getId());
        expWorkflowNext.setUpdatedBy(user);
        expWorkflowNext.setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_CHECK);
        expWorkflowNext.setVersionNo(workflowList.get(1).getVersionNo());

        // テスト実行
        service.check(correspon, workflow);

        //  Checker自身を更新
        assertEquals(1, MockWorkflowDao.ACT_UPDATE_WORKFLOW.size());
        //  後続のCheckerの承認作業状態を更新
        assertEquals(1, MockWorkflowDao.ACT_UPDATE_WORKFLOW_PROCESS_STATUS_BY_ID.size());


        Correspon resultCorrespon = MockCorresponDao.ACT_UPDATE_CORRESPON.get(0);
        Workflow resultWorkflow = MockWorkflowDao.ACT_UPDATE_WORKFLOW.get(0);
        Workflow resultWorkflowNext = MockWorkflowDao.ACT_UPDATE_WORKFLOW_PROCESS_STATUS_BY_ID.get(0);

        assertEquals(expCorrespon.toString(), resultCorrespon.toString());
        assertEquals(expWorkflow.toString(), resultWorkflow.toString());
        assertEquals(expWorkflowNext.toString(), resultWorkflowNext.toString());
    }

    /**
     * ログインユーザーが、指定されたコレポン文書を検証済にするテスト.
     * Pattern1でほかに検証前のCheckerがいないパターン.
     * @throws Exception
     */
    @Test
    public void testCheckPattern1_2() throws Exception {
        Date expectedDate = new GregorianCalendar(2009, 4, 1, 1, 1, 1).getTime();
        MockDateUtil.RET_GETNOW = expectedDate;

        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        List<Project> accessibleProjects = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        accessibleProjects.add(p);
        MockAbstractService.ACCESSIBLE_PROJECTS = accessibleProjects;

        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        MockAbstractService.IS_ANY_GROUP_ADMIN = false;

        String expectedEmpNo = "80002";
        User user = new User();
        user.setEmpNo(expectedEmpNo);
        MockAbstractService.CURRENT_USER = user;

        List<ProjectUser> mockUser = new ArrayList<ProjectUser>();
        ProjectUser pu = new ProjectUser();
        pu.setUser(user);
        pu.setProjectId("PJ1");
        mockUser.add(pu);
        User newUser = new User();
        newUser.setEmpNo("80001");
        pu = new ProjectUser();
        pu.setUser(newUser);
        pu.setProjectId("PJ1");
        mockUser.add(pu);
        MockUserDao.RET_FINDPROJECTUSER = mockUser;

        // 引数の作成
        Correspon correspon = createCorrespon();
        List<Workflow> workflowList = createWorkflowDummy();
        workflowList.get(1).setWorkflowProcessStatus(WorkflowProcessStatus.UNDER_CONSIDERATION);
        correspon.setWorkflows(workflowList);

        String expectedComment = "Comment**********";
        Workflow workflow = workflowList.get(1);
        workflow.setCommentOn(expectedComment);

        // 検証用のオブジェクト
        Correspon expCorrespon = new Correspon();
        expCorrespon.setId(correspon.getId());
        expCorrespon.setUpdatedBy(user);
        expCorrespon.setWorkflowStatus(WorkflowStatus.REQUEST_FOR_APPROVAL);
        expCorrespon.setVersionNo(correspon.getVersionNo());

        Workflow expWorkflow = new Workflow();
        expWorkflow.setId(workflow.getId());
        expWorkflow.setUpdatedBy(user);
        expWorkflow.setWorkflowProcessStatus(WorkflowProcessStatus.CHECKED);
        expWorkflow.setCommentOn(expectedComment);
        expWorkflow.setFinishedAt(expectedDate);
        expWorkflow.setFinishedBy(user);
        expWorkflow.setVersionNo(workflow.getVersionNo());

        Workflow expWorkflowNext = new Workflow();
        expWorkflowNext.setId(workflowList.get(2).getId());
        expWorkflowNext.setUpdatedBy(user);
        expWorkflowNext.setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_APPROVAL);
        expWorkflowNext.setVersionNo(workflowList.get(2).getVersionNo());

        // テスト実行
        service.check(correspon, workflow);

        //  Checker自身を更新
        assertEquals(1, MockWorkflowDao.ACT_UPDATE_WORKFLOW.size());
        //  後続のApproverの承認作業状態を更新
        assertEquals(1, MockWorkflowDao.ACT_UPDATE_WORKFLOW_PROCESS_STATUS_BY_ID.size());

        Correspon resultCorrespon = MockCorresponDao.ACT_UPDATE_CORRESPON.get(0);
        Workflow resultWorkflow = MockWorkflowDao.ACT_UPDATE_WORKFLOW.get(0);
        Workflow resultWorkflowNext = MockWorkflowDao.ACT_UPDATE_WORKFLOW_PROCESS_STATUS_BY_ID.get(0);

        assertEquals(expCorrespon.toString(), resultCorrespon.toString());
        assertEquals(expWorkflow.toString(), resultWorkflow.toString());
        assertEquals(expWorkflowNext.toString(), resultWorkflowNext.toString());
    }

    /**
     * ログインユーザーが、指定されたコレポン文書を検証済にするテスト.
     * Pattern2でほかに検証前のCheckerがいるパターン.
     * @throws Exception
     */
    @Test
    public void testCheckPattern2_1() throws Exception {
        Date expectedDate = new GregorianCalendar(2009, 4, 1, 1, 1, 1).getTime();
        MockDateUtil.RET_GETNOW = expectedDate;

        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        List<Project> accessibleProjects = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        accessibleProjects.add(p);
        MockAbstractService.ACCESSIBLE_PROJECTS = accessibleProjects;

        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        MockAbstractService.IS_ANY_GROUP_ADMIN = false;

        String expectedEmpNo = "80002";
        User user = new User();
        user.setEmpNo(expectedEmpNo);
        MockAbstractService.CURRENT_USER = user;

        List<ProjectUser> mockUser = new ArrayList<ProjectUser>();
        User newUser = new User();
        newUser.setEmpNo("80001");
        ProjectUser pu = new ProjectUser();
        pu.setUser(newUser);
        pu.setProjectId("PJ1");
        mockUser.add(pu);
        newUser = new User();
        newUser.setEmpNo("80002");
        pu = new ProjectUser();
        pu.setUser(newUser);
        pu.setProjectId("PJ1");
        mockUser.add(pu);
        MockUserDao.RET_FINDPROJECTUSER = mockUser;

        // 引数の作成
        Correspon correspon = createCorrespon();
        List<Workflow> workflowList = createWorkflowDummy();
        workflowList.get(0).setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_CHECK);
        workflowList.get(0).setCorresponId(correspon.getId());
        workflowList.get(1).setWorkflowProcessStatus(WorkflowProcessStatus.UNDER_CONSIDERATION);
        workflowList.get(1).setCorresponId(correspon.getId());
        correspon.setWorkflows(workflowList);
        correspon.getCorresponType().getWorkflowPattern().setWorkflowCd(SystemConfig
            .getValue(KEY_PATTERN_2));

        String expectedComment = "Comment**********";
        Workflow workflow = workflowList.get(1);
        workflow.setCommentOn(expectedComment);

        // 検証用のオブジェクト
        Correspon expCorrespon = new Correspon();
        expCorrespon.setId(correspon.getId());
        expCorrespon.setUpdatedBy(user);
        expCorrespon.setWorkflowStatus(WorkflowStatus.UNDER_CONSIDERATION);
        expCorrespon.setVersionNo(correspon.getVersionNo());

        Workflow expWorkflow = new Workflow();
        expWorkflow.setId(workflow.getId());
        expWorkflow.setUpdatedBy(user);
        expWorkflow.setWorkflowProcessStatus(WorkflowProcessStatus.CHECKED);
        expWorkflow.setCommentOn(expectedComment);
        expWorkflow.setFinishedAt(expectedDate);
        expWorkflow.setFinishedBy(user);
        expWorkflow.setVersionNo(workflow.getVersionNo());

        // テスト実行
        service.check(correspon, workflow);

        // Approverは更新しない
        assertEquals(1, MockWorkflowDao.ACT_UPDATE_WORKFLOW.size());

        Correspon resultCorrespon = MockCorresponDao.ACT_UPDATE_CORRESPON.get(0);
        Workflow resultWorkflow = MockWorkflowDao.ACT_UPDATE_WORKFLOW.get(0);

        assertEquals(expCorrespon.toString(), resultCorrespon.toString());
        assertEquals(expWorkflow.toString(), resultWorkflow.toString());
    }

    /**
     * ログインユーザーが、指定されたコレポン文書を検証済にするテスト.
     * Pattern2でほかに検証前のCheckerがいないパターン.
     * @throws Exception
     */
    @Test
    public void testCheckPattern2_2() throws Exception {
        Date expectedDate = new GregorianCalendar(2009, 4, 1, 1, 1, 1).getTime();
        MockDateUtil.RET_GETNOW = expectedDate;

        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        List<Project> accessibleProjects = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        accessibleProjects.add(p);
        MockAbstractService.ACCESSIBLE_PROJECTS = accessibleProjects;

        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        MockAbstractService.IS_ANY_GROUP_ADMIN = false;

        String expectedEmpNo = "80002";
        User user = new User();
        user.setEmpNo(expectedEmpNo);
        MockAbstractService.CURRENT_USER = user;

        List<ProjectUser> mockUser = new ArrayList<ProjectUser>();
        User newUser = new User();
        newUser.setEmpNo("80001");
        ProjectUser pu = new ProjectUser();
        pu.setUser(newUser);
        pu.setProjectId("PJ1");
        mockUser.add(pu);
        newUser = new User();
        newUser.setEmpNo("80002");
        pu = new ProjectUser();
        pu.setUser(newUser);
        pu.setProjectId("PJ1");
        mockUser.add(pu);
        MockUserDao.RET_FINDPROJECTUSER = mockUser;

        // 引数の作成
        Correspon correspon = createCorrespon();
        List<Workflow> workflowList = createWorkflowDummy();
        workflowList.get(0).setWorkflowProcessStatus(WorkflowProcessStatus.CHECKED);
        workflowList.get(0).setCorresponId(correspon.getId());
        workflowList.get(1).setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_CHECK);
        workflowList.get(1).setCorresponId(correspon.getId());
        correspon.setWorkflows(workflowList);
        correspon.getCorresponType().getWorkflowPattern().setWorkflowCd(SystemConfig
            .getValue(KEY_PATTERN_2));

        String expectedComment = "Comment**********";
        Workflow workflow = workflowList.get(1);
        workflow.setCommentOn(expectedComment);

        // 検証用のオブジェクト
        Correspon expCorrespon = new Correspon();
        expCorrespon.setId(correspon.getId());
        expCorrespon.setUpdatedBy(user);
        expCorrespon.setWorkflowStatus(WorkflowStatus.REQUEST_FOR_APPROVAL);
        expCorrespon.setVersionNo(correspon.getVersionNo());

        Workflow expWorkflow = new Workflow();
        expWorkflow.setId(workflow.getId());
        expWorkflow.setUpdatedBy(user);
        expWorkflow.setWorkflowProcessStatus(WorkflowProcessStatus.CHECKED);
        expWorkflow.setCommentOn(expectedComment);
        expWorkflow.setFinishedAt(expectedDate);
        expWorkflow.setFinishedBy(user);
        expWorkflow.setVersionNo(workflow.getVersionNo());

        Workflow expWorkflowNext = new Workflow();
        expWorkflowNext.setId(workflowList.get(2).getId());
        expWorkflowNext.setUpdatedBy(user);
        expWorkflowNext.setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_APPROVAL);
        expWorkflowNext.setVersionNo(workflowList.get(2).getVersionNo());

        // テスト実行
        service.check(correspon, workflow);

        // Approverを更新する
        assertEquals(1, MockWorkflowDao.ACT_UPDATE_WORKFLOW.size());
        assertEquals(1, MockWorkflowDao.ACT_UPDATE_WORKFLOW_PROCESS_STATUS_BY_ID.size());

        Correspon resultCorrespon = MockCorresponDao.ACT_UPDATE_CORRESPON.get(0);
        Workflow resultWorkflow = MockWorkflowDao.ACT_UPDATE_WORKFLOW.get(0);
        Workflow resultWorkflowNext = MockWorkflowDao.ACT_UPDATE_WORKFLOW_PROCESS_STATUS_BY_ID.get(0);

        assertEquals(expCorrespon.toString(), resultCorrespon.toString());
        assertEquals(expWorkflow.toString(), resultWorkflow.toString());
        assertEquals(expWorkflowNext.toString(), resultWorkflowNext.toString());
    }

    /**
     * ログインユーザーが、指定されたコレポン文書を検証済にするテスト.
     * Pattern3で、ほかに検証前のCheckerがいるパターン.
     * @throws Exception
     */
    @Test
    public void testCheckPattern3_1() throws Exception {
        Date expectedDate = new GregorianCalendar(2009, 4, 1, 1, 1, 1).getTime();
        MockDateUtil.RET_GETNOW = expectedDate;

        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        List<Project> accessibleProjects = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        accessibleProjects.add(p);
        MockAbstractService.ACCESSIBLE_PROJECTS = accessibleProjects;

        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        MockAbstractService.IS_ANY_GROUP_ADMIN = false;

        String expectedEmpNo = "80002";
        User user = new User();
        user.setEmpNo(expectedEmpNo);
        MockAbstractService.CURRENT_USER = user;

        List<ProjectUser> mockUser = new ArrayList<ProjectUser>();
        User newUser = new User();
        newUser.setEmpNo("80001");
        ProjectUser pu = new ProjectUser();
        pu.setUser(newUser);
        pu.setProjectId("PJ1");
        mockUser.add(pu);
        newUser = new User();
        newUser.setEmpNo("80002");
        pu = new ProjectUser();
        pu.setUser(newUser);
        pu.setProjectId("PJ1");
        mockUser.add(pu);
        MockUserDao.RET_FINDPROJECTUSER = mockUser;

        // 引数の作成
        Correspon correspon = createCorrespon();
        List<Workflow> workflowList = createWorkflowDummy();
        workflowList.get(0).setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_CHECK);
        workflowList.get(0).setCorresponId(correspon.getId());
        workflowList.get(1).setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_CHECK);
        workflowList.get(1).setCorresponId(correspon.getId());
        correspon.setWorkflows(workflowList);
        correspon.getCorresponType().getWorkflowPattern().setWorkflowCd(SystemConfig
            .getValue(KEY_PATTERN_3));

        String expectedComment = "Comment**********";
        Workflow workflow = workflowList.get(1);
        workflow.setCommentOn(expectedComment);

        // 検証用のオブジェクト
        Correspon expCorrespon = new Correspon();
        expCorrespon.setId(correspon.getId());
        expCorrespon.setUpdatedBy(user);
        expCorrespon.setWorkflowStatus(WorkflowStatus.UNDER_CONSIDERATION);
        expCorrespon.setVersionNo(correspon.getVersionNo());

        Workflow expWorkflow = new Workflow();
        expWorkflow.setId(workflow.getId());
        expWorkflow.setUpdatedBy(user);
        expWorkflow.setWorkflowProcessStatus(WorkflowProcessStatus.CHECKED);
        expWorkflow.setCommentOn(expectedComment);
        expWorkflow.setFinishedAt(expectedDate);
        expWorkflow.setFinishedBy(user);
        expWorkflow.setVersionNo(workflow.getVersionNo());

        // テスト実行
        service.check(correspon, workflow);

        // Approverは更新しない
        assertEquals(1, MockWorkflowDao.ACT_UPDATE_WORKFLOW.size());

        Correspon resultCorrespon = MockCorresponDao.ACT_UPDATE_CORRESPON.get(0);
        Workflow resultWorkflow = MockWorkflowDao.ACT_UPDATE_WORKFLOW.get(0);

        assertEquals(expCorrespon.toString(), resultCorrespon.toString());
        assertEquals(expWorkflow.toString(), resultWorkflow.toString());
    }

    /**
     * ログインユーザーが、指定されたコレポン文書を検証済にするテスト.
     * Pattern3で、ほかに検証前のCheckerがいないパターン.
     * @throws Exception
     */
    @Test
    public void testCheckPattern3_2() throws Exception {
        Date expectedDate = new GregorianCalendar(2009, 4, 1, 1, 1, 1).getTime();
        MockDateUtil.RET_GETNOW = expectedDate;

        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        List<Project> accessibleProjects = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        accessibleProjects.add(p);
        MockAbstractService.ACCESSIBLE_PROJECTS = accessibleProjects;

        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        MockAbstractService.IS_ANY_GROUP_ADMIN = false;

        String expectedEmpNo = "80002";
        User user = new User();
        user.setEmpNo(expectedEmpNo);
        MockAbstractService.CURRENT_USER = user;

        List<ProjectUser> mockUser = new ArrayList<ProjectUser>();
        User newUser = new User();
        newUser.setEmpNo("80001");
        ProjectUser pu = new ProjectUser();
        pu.setUser(newUser);
        pu.setProjectId("PJ1");
        mockUser.add(pu);
        newUser = new User();
        newUser.setEmpNo("80002");
        pu = new ProjectUser();
        pu.setUser(newUser);
        pu.setProjectId("PJ1");
        mockUser.add(pu);
        MockUserDao.RET_FINDPROJECTUSER = mockUser;

        // 引数の作成
        Correspon correspon = createCorrespon();
        List<Workflow> workflowList = createWorkflowDummy();
        workflowList.get(0).setWorkflowProcessStatus(WorkflowProcessStatus.CHECKED);
        workflowList.get(0).setCorresponId(correspon.getId());
        workflowList.get(1).setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_CHECK);
        workflowList.get(1).setCorresponId(correspon.getId());
        correspon.setWorkflows(workflowList);
        correspon.getCorresponType().getWorkflowPattern().setWorkflowCd(SystemConfig
            .getValue(KEY_PATTERN_3));

        String expectedComment = "Comment**********";
        Workflow workflow = workflowList.get(1);
        workflow.setCommentOn(expectedComment);

        // 検証用のオブジェクト
        Correspon expCorrespon = new Correspon();
        expCorrespon.setId(correspon.getId());
        expCorrespon.setUpdatedBy(user);
        expCorrespon.setWorkflowStatus(WorkflowStatus.REQUEST_FOR_APPROVAL);
        expCorrespon.setVersionNo(correspon.getVersionNo());

        Workflow expWorkflow = new Workflow();
        expWorkflow.setId(workflow.getId());
        expWorkflow.setUpdatedBy(user);
        expWorkflow.setWorkflowProcessStatus(WorkflowProcessStatus.CHECKED);
        expWorkflow.setCommentOn(expectedComment);
        expWorkflow.setFinishedAt(expectedDate);
        expWorkflow.setFinishedBy(user);
        expWorkflow.setVersionNo(workflow.getVersionNo());

        // テスト実行
        service.check(correspon, workflow);

        // Approverは更新しない
        assertEquals(1, MockWorkflowDao.ACT_UPDATE_WORKFLOW.size());

        Correspon resultCorrespon = MockCorresponDao.ACT_UPDATE_CORRESPON.get(0);
        Workflow resultWorkflow = MockWorkflowDao.ACT_UPDATE_WORKFLOW.get(0);

        assertEquals(expCorrespon.toString(), resultCorrespon.toString());
        assertEquals(expWorkflow.toString(), resultWorkflow.toString());
    }

    /**
     * ログインユーザーが、指定されたコレポン文書を検証済にするテスト.
     * 引数Nullのチェック.
     * @throws Exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCheckNullError_1() throws Exception {
        // 引数の作成
        Workflow workflow = createWorkflowDummy().get(1);
        // テスト実行
        service.check(null, workflow);
        fail("例外が発生していない");
    }

    /**
     * ログインユーザーが、指定されたコレポン文書を検証済にするテスト.
     * 引数Nullのチェック.
     * @throws Exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCheckNullError_2() throws Exception {
        // 引数の作成
        Correspon correspon = createCorrespon();
        List<Workflow> workflowList = createWorkflowDummy();
        workflowList.get(0).setWorkflowProcessStatus(WorkflowProcessStatus.CHECKED);
        workflowList.get(1).setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_CHECK);
        correspon.setWorkflows(workflowList);
        correspon.getCorresponType().getWorkflowPattern().setWorkflowCd(SystemConfig
            .getValue(KEY_PATTERN_3));
        // テスト実行
        service.check(correspon, null);
        fail("例外が発生していない");
    }

    /**
     * ログインユーザーが、指定されたコレポン文書を検証済にするテスト.
     * 自身がCheckerで重複エラー.
     * @throws Exception
     */
    @Test
    public void testCheckDuplication() throws Exception {
        Date expectedDate = new GregorianCalendar(2009, 4, 1, 1, 1, 1).getTime();
        MockDateUtil.RET_GETNOW = expectedDate;

        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        List<Project> accessibleProjects = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        accessibleProjects.add(p);
        MockAbstractService.ACCESSIBLE_PROJECTS = accessibleProjects;

        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        MockAbstractService.IS_ANY_GROUP_ADMIN = false;

        // 引数の作成
        String expectedEmpNo = "80001";
        User user = new User();
        user.setEmpNo(expectedEmpNo);
        MockAbstractService.CURRENT_USER = user;

        List<ProjectUser> mockUser = new ArrayList<ProjectUser>();
        ProjectUser pu = new ProjectUser();
        pu.setUser(user);
        pu.setProjectId("PJ1");
        mockUser.add(pu);
        MockUserDao.RET_FINDPROJECTUSER = mockUser;

        Correspon correspon = createCorrespon();
        List<Workflow> workflowList = createWorkflowDummy();
        workflowList.get(0).setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_CHECK);
        User u = new User();
        u.setEmpNo("80001");
        u.setNameE("Checker1 Test");
        workflowList.get(1).setUser(user);
        workflowList.get(1).setWorkflowProcessStatus(WorkflowProcessStatus.NONE);
        correspon.setWorkflows(workflowList);

        String expectedComment = "Comment**********";
        Workflow workflow = workflowList.get(0);
        workflow.setCommentOn(expectedComment);

        // 検証用のオブジェクト
        Correspon expCorrespon = new Correspon();
        expCorrespon.setId(correspon.getId());
        expCorrespon.setUpdatedBy(user);
        expCorrespon.setWorkflowStatus(WorkflowStatus.UNDER_CONSIDERATION);
        expCorrespon.setVersionNo(correspon.getVersionNo());

        Workflow expWorkflow = new Workflow();
        expWorkflow.setId(workflow.getId());
        expWorkflow.setUpdatedBy(user);
        expWorkflow.setWorkflowProcessStatus(WorkflowProcessStatus.CHECKED);
        expWorkflow.setCommentOn(expectedComment);
        expWorkflow.setFinishedAt(expectedDate);
        expWorkflow.setFinishedBy(user);
        expWorkflow.setVersionNo(workflow.getVersionNo());

        Workflow expWorkflowNext = new Workflow();
        expWorkflowNext.setId(workflowList.get(1).getId());
        expWorkflowNext.setUpdatedBy(user);
        expWorkflowNext.setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_CHECK);
        expWorkflowNext.setVersionNo(workflowList.get(1).getVersionNo());

        // テスト実行
        try {
            service.check(correspon, workflow);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            // 正しいエラーコードがセットされた例外がthrowされていることを検証
            assertEquals(ApplicationMessageCode.DUPLICATED_CHECKER, actual.getMessageCode());
            assertEquals(1, actual.getMessageVars().length);
            assertEquals("80001", actual.getMessageVars()[0]);
        }

    }

    /**
     * ログインユーザーが、指定されたコレポン文書を検証済にするテスト.
     * PreparerとCheckerが重複.
     * @throws Exception
     */
    @Test
    public void testCheckDuplicationPreparer() throws Exception {
        Date expectedDate = new GregorianCalendar(2009, 4, 1, 1, 1, 1).getTime();
        MockDateUtil.RET_GETNOW = expectedDate;

        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        List<Project> accessibleProjects = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        accessibleProjects.add(p);
        MockAbstractService.ACCESSIBLE_PROJECTS = accessibleProjects;

        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        MockAbstractService.IS_ANY_GROUP_ADMIN = false;

        // 引数の作成
        String expectedEmpNo = "80001";
        User user = new User();
        user.setEmpNo(expectedEmpNo);
        MockAbstractService.CURRENT_USER = user;

        List<ProjectUser> mockUser = new ArrayList<ProjectUser>();
        ProjectUser pu = new ProjectUser();
        pu.setUser(user);
        pu.setProjectId("PJ1");
        mockUser.add(pu);
        MockUserDao.RET_FINDPROJECTUSER = mockUser;

        Correspon correspon = createCorrespon();
        correspon.setCreatedBy(user);
        List<Workflow> workflowList = createWorkflowDummy();
        User u = new User();
        u.setEmpNo("80001");
        u.setNameE("Preparer Test");
        workflowList.get(0).setUser(user);
        workflowList.get(0).setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_CHECK);
        workflowList.get(1).setWorkflowProcessStatus(WorkflowProcessStatus.NONE);
        correspon.setWorkflows(workflowList);

        String expectedComment = "Comment**********";
        Workflow workflow = workflowList.get(0);
        workflow.setCommentOn(expectedComment);

        // 検証用のオブジェクト
        Correspon expCorrespon = new Correspon();
        expCorrespon.setId(correspon.getId());
        expCorrespon.setUpdatedBy(user);
        expCorrespon.setWorkflowStatus(WorkflowStatus.UNDER_CONSIDERATION);
        expCorrespon.setVersionNo(correspon.getVersionNo());

        Workflow expWorkflow = new Workflow();
        expWorkflow.setId(workflow.getId());
        expWorkflow.setUpdatedBy(user);
        expWorkflow.setWorkflowProcessStatus(WorkflowProcessStatus.CHECKED);
        expWorkflow.setCommentOn(expectedComment);
        expWorkflow.setFinishedAt(expectedDate);
        expWorkflow.setFinishedBy(user);
        expWorkflow.setVersionNo(workflow.getVersionNo());

        Workflow expWorkflowNext = new Workflow();
        expWorkflowNext.setId(workflowList.get(1).getId());
        expWorkflowNext.setUpdatedBy(user);
        expWorkflowNext.setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_CHECK);
        expWorkflowNext.setVersionNo(workflowList.get(2).getVersionNo());

        // テスト実行
        service.check(correspon, workflow);

        assertEquals(1, MockWorkflowDao.ACT_UPDATE_WORKFLOW.size());
        assertEquals(1, MockWorkflowDao.ACT_UPDATE_WORKFLOW_PROCESS_STATUS_BY_ID.size());

        Correspon resultCorrespon = MockCorresponDao.ACT_UPDATE_CORRESPON.get(0);
        Workflow resultWorkflow = MockWorkflowDao.ACT_UPDATE_WORKFLOW.get(0);
        Workflow resultWorkflowNext = MockWorkflowDao.ACT_UPDATE_WORKFLOW_PROCESS_STATUS_BY_ID.get(0);

        assertEquals(expCorrespon.toString(), resultCorrespon.toString());
        assertEquals(expWorkflow.toString(), resultWorkflow.toString());
        assertEquals(expWorkflowNext.toString(), resultWorkflowNext.toString());
    }

    /**
     * ログインユーザーが、指定されたコレポン文書を検証済にするテスト.
     * コレポン文書のプロジェクトが現在選択中のプロジェクトか判定する.
     * @throws Exception
     */
    @Test
    public void testCheckProject() throws Exception {
        Date expectedDate = new GregorianCalendar(2009, 4, 1, 1, 1, 1).getTime();
        MockDateUtil.RET_GETNOW = expectedDate;

        MockAbstractService.CURRENT_PROJECT_ID = "PJ999";
        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        MockAbstractService.IS_ANY_GROUP_ADMIN = false;

        String expectedEmpNo = "99999";
        User user = new User();
        user.setEmpNo(expectedEmpNo);
        MockAbstractService.CURRENT_USER = user;

        // 引数の作成
        Correspon correspon = createCorrespon();
        List<Workflow> workflowList = createWorkflowDummy();
        workflowList.get(0).setWorkflowProcessStatus(WorkflowProcessStatus.CHECKED);
        workflowList.get(1).setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_CHECK);
        correspon.setWorkflows(workflowList);
        correspon.getCorresponType().getWorkflowPattern().setWorkflowCd(SystemConfig
            .getValue(KEY_PATTERN_3));

        String expectedComment = "Comment**********";
        Workflow workflow = workflowList.get(1);
        workflow.setCommentOn(expectedComment);

        try {
            service.check(correspon, workflow);

            fail("例外が発生していない");
        } catch (ServiceAbortException e) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_PROJECT_DIFF, e
                .getMessageCode());
        }
    }

    /**
     * ログインユーザーが、指定されたコレポン文書を検証済にするテスト.
     * SystemAdminも、現在選択中プロジェクトとコレポン文書のプロジェクトとの比較チェックは必要.
     * @throws Exception
     */
    @Test
    public void testCheckProjectAdmin() throws Exception {
        Date expectedDate = new GregorianCalendar(2009, 4, 1, 1, 1, 1).getTime();
        MockDateUtil.RET_GETNOW = expectedDate;

        MockAbstractService.CURRENT_PROJECT_ID = "PJ999";
        MockAbstractService.IS_SYSTEM_ADMIN = true;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        MockAbstractService.IS_ANY_GROUP_ADMIN = false;

        String expectedEmpNo = "99999";
        User user = new User();
        user.setEmpNo(expectedEmpNo);
        MockAbstractService.CURRENT_USER = user;

        // 引数の作成
        Correspon correspon = createCorrespon();
        List<Workflow> workflowList = createWorkflowDummy();
        workflowList.get(0).setWorkflowProcessStatus(WorkflowProcessStatus.CHECKED);
        workflowList.get(1).setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_CHECK);
        correspon.setWorkflows(workflowList);
        correspon.getCorresponType().getWorkflowPattern().setWorkflowCd(SystemConfig
            .getValue(KEY_PATTERN_3));

        String expectedComment = "Comment**********";
        Workflow workflow = workflowList.get(1);
        workflow.setCommentOn(expectedComment);

        // 検証用のオブジェクト
        Correspon expCorrespon = new Correspon();
        expCorrespon.setId(correspon.getId());
        expCorrespon.setUpdatedBy(user);
        expCorrespon.setWorkflowStatus(WorkflowStatus.REQUEST_FOR_APPROVAL);
        expCorrespon.setVersionNo(correspon.getVersionNo());

        Workflow expWorkflow = new Workflow();
        expWorkflow.setId(workflow.getId());
        expWorkflow.setUpdatedBy(user);
        expWorkflow.setWorkflowProcessStatus(WorkflowProcessStatus.CHECKED);
        expWorkflow.setCommentOn(expectedComment);
        expWorkflow.setFinishedAt(expectedDate);
        expWorkflow.setFinishedBy(user);
        expWorkflow.setVersionNo(workflow.getVersionNo());

        // System Adminでもプロジェクトのチェックは必要
        try {
            service.check(correspon, workflow);

            fail("例外が発生していない");
        } catch (ServiceAbortException e) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_PROJECT_DIFF, e
                .getMessageCode());
        }
    }

    /**
     * ログインユーザーが、指定されたコレポン文書を検証済にするテスト.
     * Checker承認作業の権限チェック.
     * @throws Exception
     */
    @Test
    public void testCheckValidate() throws Exception {
        Date expectedDate = new GregorianCalendar(2009, 4, 1, 1, 1, 1).getTime();
        MockDateUtil.RET_GETNOW = expectedDate;

        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        List<Project> accessibleProjects = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        accessibleProjects.add(p);
        MockAbstractService.ACCESSIBLE_PROJECTS = accessibleProjects;

        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        MockAbstractService.IS_ANY_GROUP_ADMIN = false;

        String expectedEmpNo = "99999";
        User user = new User();
        user.setEmpNo(expectedEmpNo);
        MockAbstractService.CURRENT_USER = user;

        // 引数の作成
        Correspon correspon = createCorrespon();
        List<Workflow> workflowList = createWorkflowDummy();
        workflowList.get(1).setWorkflowProcessStatus(WorkflowProcessStatus.CHECKED);
        workflowList.get(2).setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_CHECK);
        correspon.setWorkflows(workflowList);
        correspon.getCorresponType().getWorkflowPattern().setWorkflowCd(SystemConfig
            .getValue(KEY_PATTERN_3));

        String expectedComment = "Comment**********";
        Workflow workflow = workflowList.get(2);
        workflow.setCommentOn(expectedComment);

        try {
            service.check(correspon, workflow);

            fail("例外が発生していない");
        } catch (ServiceAbortException e) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT, e
                .getMessageCode());
        }
    }

    /**
     * ログインユーザーが、指定されたコレポン文書を検証済にするテスト.
     * Checker承認作業の権限チェック.(SystemAdminなのですり抜け)
     * @throws Exception
     */
    @Test
    public void testCheckValidateSystemAdmin() throws Exception {
        Date expectedDate = new GregorianCalendar(2009, 4, 1, 1, 1, 1).getTime();
        MockDateUtil.RET_GETNOW = expectedDate;

        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        MockAbstractService.IS_SYSTEM_ADMIN = true;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        MockAbstractService.IS_ANY_GROUP_ADMIN = false;

        String expectedEmpNo = "99999";
        User user = new User();
        user.setEmpNo(expectedEmpNo);
        MockAbstractService.CURRENT_USER = user;

        // 引数の作成
        Correspon correspon = createCorrespon();
        List<Workflow> workflowList = createWorkflowDummy();
        workflowList.get(0).setWorkflowProcessStatus(WorkflowProcessStatus.CHECKED);
        workflowList.get(0).setCorresponId(correspon.getId());
        workflowList.get(0).setUpdatedBy(correspon.getUpdatedBy());
        workflowList.get(1).setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_CHECK);
        workflowList.get(1).setCorresponId(correspon.getId());
        workflowList.get(1).setUpdatedBy(correspon.getUpdatedBy());
        correspon.setWorkflows(workflowList);
        correspon.getCorresponType().getWorkflowPattern().setWorkflowCd(SystemConfig
            .getValue(KEY_PATTERN_3));

        String expectedComment = "Comment**********";
        Workflow workflow = workflowList.get(1);
        workflow.setCommentOn(expectedComment);

        // 検証用のオブジェクト
        Correspon expCorrespon = new Correspon();
        expCorrespon.setId(correspon.getId());
        expCorrespon.setUpdatedBy(user);
        expCorrespon.setWorkflowStatus(WorkflowStatus.REQUEST_FOR_APPROVAL);
        expCorrespon.setVersionNo(correspon.getVersionNo());

        Workflow expWorkflow = new Workflow();
        expWorkflow.setId(workflow.getId());
        expWorkflow.setUpdatedBy(user);
        expWorkflow.setWorkflowProcessStatus(WorkflowProcessStatus.CHECKED);
        expWorkflow.setCommentOn(expectedComment);
        expWorkflow.setFinishedAt(expectedDate);
        expWorkflow.setFinishedBy(user);
        expWorkflow.setVersionNo(workflow.getVersionNo());

        // Adminでテスト実行 (例外は発生しない）
        service.check(correspon, workflow);

        // Approverは更新しない
        assertEquals(1, MockWorkflowDao.ACT_UPDATE_WORKFLOW.size());

        Correspon resultCorrespon = MockCorresponDao.ACT_UPDATE_CORRESPON.get(0);
        Workflow resultWorkflow = MockWorkflowDao.ACT_UPDATE_WORKFLOW.get(0);

        assertEquals(expCorrespon.toString(), resultCorrespon.toString());
        assertEquals(expWorkflow.toString(), resultWorkflow.toString());
    }

    /**
     * ログインユーザーが、指定されたコレポン文書を検証済にするテスト.
     * Checker承認作業の権限チェック.(ProjectAdminなのですり抜け)
     * @throws Exception
     */
    @Test
    public void testCheckValidateProjectAdmin() throws Exception {
        Date expectedDate = new GregorianCalendar(2009, 4, 1, 1, 1, 1).getTime();
        MockDateUtil.RET_GETNOW = expectedDate;

        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        List<Project> accessibleProjects = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        accessibleProjects.add(p);
        MockAbstractService.ACCESSIBLE_PROJECTS = accessibleProjects;

        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN = true;
        MockAbstractService.IS_ANY_GROUP_ADMIN = false;

        String expectedEmpNo = "99999";
        User user = new User();
        user.setEmpNo(expectedEmpNo);
        MockAbstractService.CURRENT_USER = user;

        // 引数の作成
        Correspon correspon = createCorrespon();
        List<Workflow> workflowList = createWorkflowDummy();
        workflowList.get(0).setWorkflowProcessStatus(WorkflowProcessStatus.CHECKED);
        workflowList.get(0).setCorresponId(correspon.getId());
        workflowList.get(0).setUpdatedBy(correspon.getUpdatedBy());
        workflowList.get(1).setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_CHECK);
        workflowList.get(1).setCorresponId(correspon.getId());
        workflowList.get(1).setUpdatedBy(correspon.getUpdatedBy());
        correspon.setWorkflows(workflowList);
        correspon.getCorresponType().getWorkflowPattern().setWorkflowCd(SystemConfig
            .getValue(KEY_PATTERN_3));

        String expectedComment = "Comment**********";
        Workflow workflow = workflowList.get(1);
        workflow.setCommentOn(expectedComment);

        // 検証用のオブジェクト
        Correspon expCorrespon = new Correspon();
        expCorrespon.setId(correspon.getId());
        expCorrespon.setUpdatedBy(user);
        expCorrespon.setWorkflowStatus(WorkflowStatus.REQUEST_FOR_APPROVAL);
        expCorrespon.setVersionNo(correspon.getVersionNo());

        Workflow expWorkflow = new Workflow();
        expWorkflow.setId(workflow.getId());
        expWorkflow.setUpdatedBy(user);
        expWorkflow.setWorkflowProcessStatus(WorkflowProcessStatus.CHECKED);
        expWorkflow.setCommentOn(expectedComment);
        expWorkflow.setFinishedAt(expectedDate);
        expWorkflow.setFinishedBy(user);
        expWorkflow.setVersionNo(workflow.getVersionNo());

        // Adminでテスト実行 (例外は発生しない）
        service.check(correspon, workflow);

        // Approverは更新しない
        assertEquals(1, MockWorkflowDao.ACT_UPDATE_WORKFLOW.size());

        Correspon resultCorrespon = MockCorresponDao.ACT_UPDATE_CORRESPON.get(0);
        Workflow resultWorkflow = MockWorkflowDao.ACT_UPDATE_WORKFLOW.get(0);

        assertEquals(expCorrespon.toString(), resultCorrespon.toString());
        assertEquals(expWorkflow.toString(), resultWorkflow.toString());
    }

    /**
     * ログインユーザーが、指定されたコレポン文書を検証済にするテスト.
     * Checker承認作業の権限チェック.(GroupAdminで、かつCheckerに含まれる)
     * @throws Exception
     */
    @Test
    public void testCheckValidateGroupAdmin_1() throws Exception {
        Date expectedDate = new GregorianCalendar(2009, 4, 1, 1, 1, 1).getTime();
        MockDateUtil.RET_GETNOW = expectedDate;

        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        List<Project> accessibleProjects = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        accessibleProjects.add(p);
        MockAbstractService.ACCESSIBLE_PROJECTS = accessibleProjects;

        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        MockAbstractService.IS_ANY_GROUP_ADMIN = false;
        List<Long> groupAdmin = new ArrayList<Long>();
        groupAdmin.add(5963L);
        MockAbstractService.IS_GROUP_ADMIN = groupAdmin;

        String expectedEmpNo = "99999";
        User user = new User();
        user.setEmpNo(expectedEmpNo);
        MockAbstractService.CURRENT_USER = user;

        List<ProjectUser> mockUser = new ArrayList<ProjectUser>();
        User newUser = new User();
        newUser.setEmpNo("99999");
        ProjectUser pu = new ProjectUser();
        pu.setUser(newUser);
        pu.setProjectId("PJ1");
        mockUser.add(pu);
        newUser = new User();
        newUser.setEmpNo("80002");
        pu = new ProjectUser();
        pu.setUser(newUser);
        pu.setProjectId("PJ1");
        mockUser.add(pu);
        MockUserDao.RET_FINDPROJECTUSER = mockUser;

        // 引数の作成
        Correspon correspon = createCorrespon();
        List<Workflow> workflowList = createWorkflowDummy();
        workflowList.get(0).setWorkflowProcessStatus(WorkflowProcessStatus.CHECKED);
        workflowList.get(0).setCorresponId(correspon.getId());
        workflowList.get(0).setUpdatedBy(correspon.getUpdatedBy());
        workflowList.get(0).setUser(user); // チェック済みのチェッカー
        workflowList.get(1).setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_CHECK);
        workflowList.get(1).setCorresponId(correspon.getId());
        workflowList.get(1).setUpdatedBy(correspon.getUpdatedBy());
        correspon.setWorkflows(workflowList);
        correspon.getCorresponType().getWorkflowPattern().setWorkflowCd(SystemConfig
            .getValue(KEY_PATTERN_3));
        CorresponGroup cg = new CorresponGroup();
        cg.setId(5963L);
        correspon.setFromCorresponGroup(cg);
        String expectedComment = "Comment**********";
        Workflow workflow = workflowList.get(1);
        workflow.setCommentOn(expectedComment);

        // 検証用のオブジェクト
        Correspon expCorrespon = new Correspon();
        expCorrespon.setId(correspon.getId());
        expCorrespon.setUpdatedBy(user);
        expCorrespon.setWorkflowStatus(WorkflowStatus.REQUEST_FOR_APPROVAL);
        expCorrespon.setVersionNo(correspon.getVersionNo());

        Workflow expWorkflow = new Workflow();
        expWorkflow.setId(workflow.getId());
        expWorkflow.setUpdatedBy(user);
        expWorkflow.setWorkflowProcessStatus(WorkflowProcessStatus.CHECKED);
        expWorkflow.setCommentOn(expectedComment);
        expWorkflow.setFinishedAt(expectedDate);
        expWorkflow.setFinishedBy(user);
        expWorkflow.setVersionNo(workflow.getVersionNo());

        // Adminでテスト実行 (例外は発生しない）
        service.check(correspon, workflow);

        // Approverは更新しない
        assertEquals(1, MockWorkflowDao.ACT_UPDATE_WORKFLOW.size());

        Correspon resultCorrespon = MockCorresponDao.ACT_UPDATE_CORRESPON.get(0);
        Workflow resultWorkflow = MockWorkflowDao.ACT_UPDATE_WORKFLOW.get(0);

        assertEquals(expCorrespon.toString(), resultCorrespon.toString());
        assertEquals(expWorkflow.toString(), resultWorkflow.toString());
    }

    /**
     * ログインユーザーが、指定されたコレポン文書を検証済にするテスト.
     * Checker承認作業の権限チェック.(GroupAdminで、かつCheckerに含まれない)
     * @throws Exception
     */
    @Test
    public void testCheckValidateGroupAdmin_2() throws Exception {
        Date expectedDate = new GregorianCalendar(2009, 4, 1, 1, 1, 1).getTime();
        MockDateUtil.RET_GETNOW = expectedDate;

        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        List<Project> accessibleProjects = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        accessibleProjects.add(p);
        MockAbstractService.ACCESSIBLE_PROJECTS = accessibleProjects;

        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        MockAbstractService.IS_ANY_GROUP_ADMIN = false;
        List<Long> groupAdmin = new ArrayList<Long>();
        groupAdmin.add(5963L);
        MockAbstractService.IS_GROUP_ADMIN = groupAdmin;

        String expectedEmpNo = "99999";
        User user = new User();
        user.setEmpNo(expectedEmpNo);
        MockAbstractService.CURRENT_USER = user;

        List<ProjectUser> mockUser = new ArrayList<ProjectUser>();
        User newUser = new User();
        newUser.setEmpNo("99999");
        ProjectUser pu = new ProjectUser();
        pu.setUser(newUser);
        pu.setProjectId("PJ1");
        mockUser.add(pu);
        newUser = new User();
        newUser.setEmpNo("80002");
        pu = new ProjectUser();
        pu.setUser(newUser);
        pu.setProjectId("PJ1");
        mockUser.add(pu);
        MockUserDao.RET_FINDPROJECTUSER = mockUser;

        // 引数の作成 -- chckerに含まれない
        Correspon correspon = createCorrespon();
        List<Workflow> workflowList = createWorkflowDummy();
        workflowList.get(0).setWorkflowProcessStatus(WorkflowProcessStatus.CHECKED);
        workflowList.get(0).setCorresponId(correspon.getId());
        workflowList.get(0).setUpdatedBy(correspon.getUpdatedBy());
        workflowList.get(1).setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_CHECK);
        workflowList.get(1).setCorresponId(correspon.getId());
        workflowList.get(1).setUpdatedBy(correspon.getUpdatedBy());
        correspon.setWorkflows(workflowList);
        correspon.getCorresponType().getWorkflowPattern().setWorkflowCd(SystemConfig
            .getValue(KEY_PATTERN_3));
        CorresponGroup cg = new CorresponGroup();
        cg.setId(5963L);
        correspon.setFromCorresponGroup(cg);

        String expectedComment = "Comment**********";
        Workflow workflow = workflowList.get(1);
        workflow.setCommentOn(expectedComment);

        try {
            service.check(correspon, workflow);
        } catch (ServiceAbortException e) {
            fail("例外が発生");
        }
    }

    /**
     * ログインユーザーが、指定されたコレポン文書を検証済にするテスト.
     * ワークフローの承認作業状態のチェック.
     * @throws Exception
     */
    @Test
    public void testCheckWorkflowStatus() throws Exception {
        Date expectedDate = new GregorianCalendar(2009, 4, 1, 1, 1, 1).getTime();
        MockDateUtil.RET_GETNOW = expectedDate;

        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        List<Project> accessibleProjects = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        accessibleProjects.add(p);
        MockAbstractService.ACCESSIBLE_PROJECTS = accessibleProjects;

        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        MockAbstractService.IS_ANY_GROUP_ADMIN = false;

        String expectedEmpNo = "80002";
        User user = new User();
        user.setEmpNo(expectedEmpNo);
        MockAbstractService.CURRENT_USER = user;

        List<ProjectUser> mockUser = new ArrayList<ProjectUser>();
        User newUser = new User();
        newUser.setEmpNo("80001");
        ProjectUser pu = new ProjectUser();
        pu.setUser(newUser);
        pu.setProjectId("PJ1");
        mockUser.add(pu);
        newUser = new User();
        newUser.setEmpNo("80002");
        pu = new ProjectUser();
        pu.setUser(newUser);
        pu.setProjectId("PJ1");
        mockUser.add(pu);
        MockUserDao.RET_FINDPROJECTUSER = mockUser;

        // 引数の作成
        Correspon correspon = createCorrespon();
        List<Workflow> workflowList = createWorkflowDummy();
        workflowList.get(0).setWorkflowProcessStatus(WorkflowProcessStatus.CHECKED);
        workflowList.get(1).setWorkflowProcessStatus(WorkflowProcessStatus.CHECKED);// 承認作業状態のエラー
        correspon.setWorkflows(workflowList);
        correspon.getCorresponType().getWorkflowPattern().setWorkflowCd(SystemConfig
            .getValue(KEY_PATTERN_3));

        String expectedComment = "Comment**********";
        Workflow workflow = workflowList.get(1);
        workflow.setCommentOn(expectedComment);

        try {
            service.check(correspon, workflow);

            fail("例外が発生していない");
        } catch (ServiceAbortException e) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_WORKFLOW_STATUS_INVALID_FOR_CHECKER,
                e.getMessageCode());
        }
    }

    /**
     * ログインユーザーが、指定されたコレポン文書を承認するテスト.
     * @throws Exception
     */
    @Test
    public void testApprove_1() throws Exception {
        Date expectedDate = new GregorianCalendar(2009, 4, 1, 1, 1, 1).getTime();
        MockDateUtil.RET_GETNOW = expectedDate;

        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        List<Project> accessibleProjects = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        accessibleProjects.add(p);
        MockAbstractService.ACCESSIBLE_PROJECTS = accessibleProjects;

        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        MockAbstractService.IS_ANY_GROUP_ADMIN = false;

        // 引数の作成
        String expectedEmpNo = "80003";
        User user = new User();
        user.setEmpNo(expectedEmpNo);
        MockAbstractService.CURRENT_USER = user;

        List<ProjectUser> mockUser = new ArrayList<ProjectUser>();
        ProjectUser pu = new ProjectUser();
        pu.setUser(user);
        pu.setProjectId("PJ1");
        mockUser.add(pu);
        MockUserDao.RET_FINDPROJECTUSER = mockUser;

        String expCorresponNo = "NEW:CORRESPON_NO:001";
        MockCorresponSequenceService.RET_CORRESPON_NO = expCorresponNo;

        Correspon correspon = createCorrespon();
        List<Workflow> workflowList = createWorkflowDummy();
        workflowList.get(2).setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_APPROVAL);
        correspon.setWorkflows(workflowList);

        String expectedComment = "Comment**********";
        Workflow workflow = workflowList.get(2);
        workflow.setCommentOn(expectedComment);

        // 検証用のオブジェクト
        Correspon expCorrespon = new Correspon();
        expCorrespon.setId(correspon.getId());
        expCorrespon.setUpdatedBy(user);
        expCorrespon.setWorkflowStatus(WorkflowStatus.ISSUED);
        expCorrespon.setCorresponNo(expCorresponNo);
        expCorrespon.setIssuedAt(DateUtil.getNow());
        expCorrespon.setIssuedBy(MockAbstractService.CURRENT_USER);
        expCorrespon.setVersionNo(correspon.getVersionNo());

        Workflow expWorkflow = new Workflow();
        expWorkflow.setId(workflow.getId());
        expWorkflow.setUpdatedBy(user);
        expWorkflow.setWorkflowProcessStatus(WorkflowProcessStatus.APPROVED);
        expWorkflow.setCommentOn(expectedComment);
        expWorkflow.setFinishedAt(expectedDate);
        expWorkflow.setFinishedBy(user);
        expWorkflow.setVersionNo(workflow.getVersionNo());

        // テスト実行
        service.approve(correspon, workflow);

        assertEquals(1, MockWorkflowDao.ACT_UPDATE_WORKFLOW.size());

        Correspon resultCorrespon = MockCorresponDao.ACT_UPDATE_CORRESPON.get(0);
        Workflow resultWorkflow = MockWorkflowDao.ACT_UPDATE_WORKFLOW.get(0);

        assertEquals(expCorrespon.toString(), resultCorrespon.toString());
        assertEquals(expWorkflow.toString(), resultWorkflow.toString());
    }

    /**
     * ログインユーザーが、指定されたコレポン文書を承認するテスト.
     * 自身がApproverでCheckerが重複している.
     * @throws Exception
     */
    @Test
    public void testApproveDuplication() throws Exception {
        Date expectedDate = new GregorianCalendar(2009, 4, 1, 1, 1, 1).getTime();
        MockDateUtil.RET_GETNOW = expectedDate;

        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        List<Project> accessibleProjects = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        accessibleProjects.add(p);
        MockAbstractService.ACCESSIBLE_PROJECTS = accessibleProjects;

        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        MockAbstractService.IS_ANY_GROUP_ADMIN = false;

        // 引数の作成
        String expectedEmpNo = "80003";
        User user = new User();
        user.setEmpNo(expectedEmpNo);
        MockAbstractService.CURRENT_USER = user;

        List<ProjectUser> mockUser = new ArrayList<ProjectUser>();
        ProjectUser pu = new ProjectUser();
        pu.setUser(user);
        pu.setProjectId("PJ1");
        mockUser.add(pu);
        MockUserDao.RET_FINDPROJECTUSER = mockUser;

        String expCorresponNo = "NEW:CORRESPON_NO:001";
        MockCorresponSequenceService.RET_CORRESPON_NO = expCorresponNo;

        Correspon correspon = createCorrespon();
        List<Workflow> workflowList = createWorkflowDummy();
        User u = new User();
        u.setEmpNo("80003");
        u.setNameE("Approver User");
        workflowList.get(1).setUser(u);

        workflowList.get(2).setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_APPROVAL);
        correspon.setWorkflows(workflowList);

        String expectedComment = "Comment**********";
        Workflow workflow = workflowList.get(2);
        workflow.setCommentOn(expectedComment);

        // 検証用のオブジェクト
        Correspon expCorrespon = new Correspon();
        expCorrespon.setId(correspon.getId());
        expCorrespon.setUpdatedBy(user);
        expCorrespon.setWorkflowStatus(WorkflowStatus.ISSUED);
        expCorrespon.setCorresponNo(expCorresponNo);
        expCorrespon.setIssuedAt(DateUtil.getNow());
        expCorrespon.setVersionNo(correspon.getVersionNo());

        Workflow expWorkflow = new Workflow();
        expWorkflow.setId(workflow.getId());
        expWorkflow.setUpdatedBy(user);
        expWorkflow.setWorkflowProcessStatus(WorkflowProcessStatus.APPROVED);
        expWorkflow.setCommentOn(expectedComment);
        expWorkflow.setFinishedAt(expectedDate);
        expWorkflow.setFinishedBy(user);
        expWorkflow.setVersionNo(workflow.getVersionNo());

        // テスト実行
        try {
            service.approve(correspon, workflow);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            // 正しいエラーコードがセットされた例外がthrowされていることを検証
            assertEquals(ApplicationMessageCode.DUPLICATED_CHECKER_APPROVER, actual
                .getMessageCode());
            assertEquals(1, actual.getMessageVars().length);
            assertEquals("80003", actual.getMessageVars()[0]);
        }
    }

    /**
     * ログインユーザーが、指定されたコレポン文書を承認するテスト.
     * PreparerとApproverが重複.
     * @throws Exception
     */
    @Test
    public void testApproveDuplicationPreparer() throws Exception {
        Date expectedDate = new GregorianCalendar(2009, 4, 1, 1, 1, 1).getTime();
        MockDateUtil.RET_GETNOW = expectedDate;

        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        List<Project> accessibleProjects = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        accessibleProjects.add(p);
        MockAbstractService.ACCESSIBLE_PROJECTS = accessibleProjects;

        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        MockAbstractService.IS_ANY_GROUP_ADMIN = false;

        // 引数の作成
        String expectedEmpNo = "ZZA01";
        User user = new User();
        user.setEmpNo(expectedEmpNo);
        MockAbstractService.CURRENT_USER = user;

        List<ProjectUser> mockUser = new ArrayList<ProjectUser>();
        ProjectUser pu = new ProjectUser();
        pu.setUser(user);
        pu.setProjectId("PJ1");
        mockUser.add(pu);
        MockUserDao.RET_FINDPROJECTUSER = mockUser;

        String expCorresponNo = "NEW:CORRESPON_NO:001";
        MockCorresponSequenceService.RET_CORRESPON_NO = expCorresponNo;

        Correspon correspon = createCorrespon();
        List<Workflow> workflowList = createWorkflowDummy();
        User u = new User();
        u.setEmpNo("ZZA01");
        u.setNameE("Taro Yamada");
        workflowList.get(2).setUser(u);
        workflowList.get(2).setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_APPROVAL);
        correspon.setWorkflows(workflowList);

        String expectedComment = "Comment**********";
        Workflow workflow = workflowList.get(2);
        workflow.setCommentOn(expectedComment);

        // 検証用のオブジェクト
        Correspon expCorrespon = new Correspon();
        expCorrespon.setId(correspon.getId());
        expCorrespon.setUpdatedBy(user);
        expCorrespon.setWorkflowStatus(WorkflowStatus.ISSUED);
        expCorrespon.setCorresponNo(expCorresponNo);
        expCorrespon.setIssuedAt(DateUtil.getNow());
        expCorrespon.setIssuedBy(MockAbstractService.CURRENT_USER);
        expCorrespon.setVersionNo(correspon.getVersionNo());

        Workflow expWorkflow = new Workflow();
        expWorkflow.setId(workflow.getId());
        expWorkflow.setUpdatedBy(user);
        expWorkflow.setWorkflowProcessStatus(WorkflowProcessStatus.APPROVED);
        expWorkflow.setCommentOn(expectedComment);
        expWorkflow.setFinishedAt(expectedDate);
        expWorkflow.setFinishedBy(user);
        expWorkflow.setVersionNo(workflow.getVersionNo());

        // テスト実行
        service.approve(correspon, workflow);

        assertEquals(1, MockWorkflowDao.ACT_UPDATE_WORKFLOW.size());
        Correspon resultCorrespon = MockCorresponDao.ACT_UPDATE_CORRESPON.get(0);
        Workflow resultWorkflow = MockWorkflowDao.ACT_UPDATE_WORKFLOW.get(0);
        assertEquals(expCorrespon.toString(), resultCorrespon.toString());
        assertEquals(expWorkflow.toString(), resultWorkflow.toString());
    }

    /**
     * ログインユーザーが、指定されたコレポン文書を承認するテスト. （承認作業状態とパターンを変更）
     * @throws Exception
     */
    @Test
    public void testApprove_2() throws Exception {
        Date expectedDate = new GregorianCalendar(2009, 4, 1, 1, 1, 1).getTime();
        MockDateUtil.RET_GETNOW = expectedDate;

        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        List<Project> accessibleProjects = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        accessibleProjects.add(p);
        MockAbstractService.ACCESSIBLE_PROJECTS = accessibleProjects;

        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        MockAbstractService.IS_ANY_GROUP_ADMIN = false;

        // 引数の作成
        String expectedEmpNo = "80003";
        User user = new User();
        user.setEmpNo(expectedEmpNo);
        MockAbstractService.CURRENT_USER = user;

        List<ProjectUser> mockUser = new ArrayList<ProjectUser>();
        ProjectUser pu = new ProjectUser();
        pu.setUser(user);
        pu.setProjectId("PJ1");
        mockUser.add(pu);
        MockUserDao.RET_FINDPROJECTUSER = mockUser;

        String expCorresponNo = "NEW:CORRESPON_NO:001";
        MockCorresponSequenceService.RET_CORRESPON_NO = expCorresponNo;

        Correspon correspon = createCorrespon();
        List<Workflow> workflowList = createWorkflowDummy();
        workflowList.get(2).setWorkflowProcessStatus(WorkflowProcessStatus.UNDER_CONSIDERATION);
        correspon.setWorkflows(workflowList);
        correspon.getCorresponType().getWorkflowPattern().setWorkflowCd(SystemConfig
            .getValue(KEY_PATTERN_2));

        String expectedComment = "Comment**********";
        Workflow workflow = workflowList.get(2);
        workflow.setCommentOn(expectedComment);

        // 検証用のオブジェクト
        Correspon expCorrespon = new Correspon();
        expCorrespon.setId(correspon.getId());
        expCorrespon.setUpdatedBy(user);
        expCorrespon.setWorkflowStatus(WorkflowStatus.ISSUED);
        expCorrespon.setCorresponNo(expCorresponNo);
        expCorrespon.setIssuedBy(MockAbstractService.CURRENT_USER);
        expCorrespon.setIssuedAt(DateUtil.getNow());
        expCorrespon.setVersionNo(correspon.getVersionNo());

        Workflow expWorkflow = new Workflow();
        expWorkflow.setId(workflow.getId());
        expWorkflow.setUpdatedBy(user);
        expWorkflow.setWorkflowProcessStatus(WorkflowProcessStatus.APPROVED);
        expWorkflow.setCommentOn(expectedComment);
        expWorkflow.setFinishedAt(expectedDate);
        expWorkflow.setFinishedBy(user);
        expWorkflow.setVersionNo(workflow.getVersionNo());

        // テスト実行
        service.approve(correspon, workflow);

        assertEquals(1, MockWorkflowDao.ACT_UPDATE_WORKFLOW.size());

        Correspon resultCorrespon = MockCorresponDao.ACT_UPDATE_CORRESPON.get(0);
        Workflow resultWorkflow = MockWorkflowDao.ACT_UPDATE_WORKFLOW.get(0);

        assertEquals(expCorrespon.toString(), resultCorrespon.toString());
        assertEquals(expWorkflow.toString(), resultWorkflow.toString());
    }

    /**
     * ログインユーザーが、指定されたコレポン文書を検証済にするテスト.
     * 引数Nullのチェック.
     * @throws Exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testApproveNullError_1() throws Exception {
        // 引数の作成
        Workflow workflow = createWorkflowDummy().get(2);

        // テスト実行
        service.approve(null, workflow);

        fail("例外が発生していない");
    }

    /**
     * ログインユーザーが、指定されたコレポン文書を検証済にするテスト.
     * 引数Nullのチェック.
     * @throws Exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testApproveNullError_2() throws Exception {
        // 引数の作成
        Correspon correspon = createCorrespon();
        List<Workflow> workflowList = createWorkflowDummy();
        workflowList.get(1).setWorkflowProcessStatus(WorkflowProcessStatus.CHECKED);
        workflowList.get(2).setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_CHECK);
        correspon.setWorkflows(workflowList);
        correspon.getCorresponType().getWorkflowPattern().setWorkflowCd(SystemConfig
            .getValue(KEY_PATTERN_3));

        // テスト実行
        service.approve(correspon, null);

        fail("例外が発生していない");
    }

    /**
     * ログインユーザーが、指定されたコレポン文書を検証済にするテスト.
     * コレポン文書のプロジェクトが現在選択中のプロジェクトか判定する.
     * @throws Exception
     */
    @Test
    public void testApproveProject() throws Exception {
        Date expectedDate = new GregorianCalendar(2009, 4, 1, 1, 1, 1).getTime();
        MockDateUtil.RET_GETNOW = expectedDate;

        MockAbstractService.CURRENT_PROJECT_ID = "PJ999";
        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        MockAbstractService.IS_ANY_GROUP_ADMIN = false;

        String expectedEmpNo = "99999";
        User user = new User();
        user.setEmpNo(expectedEmpNo);
        MockAbstractService.CURRENT_USER = user;

        String expCorresponNo = "NEW:CORRESPON_NO:001";
        MockCorresponSequenceService.RET_CORRESPON_NO = expCorresponNo;

        // 引数の作成
        Correspon correspon = createCorrespon();
        List<Workflow> workflowList = createWorkflowDummy();
        workflowList.get(2).setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_APPROVAL);
        correspon.setWorkflows(workflowList);
        correspon.getCorresponType().getWorkflowPattern().setWorkflowCd(SystemConfig
            .getValue(KEY_PATTERN_3));

        String expectedComment = "Comment**********";
        Workflow workflow = workflowList.get(2);
        workflow.setCommentOn(expectedComment);

        try {
            service.approve(correspon, workflow);

            fail("例外が発生していない");
        } catch (ServiceAbortException e) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_PROJECT_DIFF, e
                .getMessageCode());
        }
    }

    /**
     * ログインユーザーが、指定されたコレポン文書を承認済にするテスト.
     * SystemAdminも、現在選択中プロジェクトとコレポン文書のプロジェクトとの 比較チェックは必要.
     * @throws Exception
     */
    @Test
    public void testApproveProjectAdmin() throws Exception {
        Date expectedDate = new GregorianCalendar(2009, 4, 1, 1, 1, 1).getTime();
        MockDateUtil.RET_GETNOW = expectedDate;

        MockAbstractService.CURRENT_PROJECT_ID = "PJ999";
        MockAbstractService.IS_SYSTEM_ADMIN = true;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        MockAbstractService.IS_ANY_GROUP_ADMIN = false;

        String expectedEmpNo = "99999";
        User user = new User();
        user.setEmpNo(expectedEmpNo);
        MockAbstractService.CURRENT_USER = user;

        String expCorresponNo = "NEW:CORRESPON_NO:001";
        MockCorresponSequenceService.RET_CORRESPON_NO = expCorresponNo;

        // 引数の作成
        Correspon correspon = createCorrespon();
        List<Workflow> workflowList = createWorkflowDummy();
        workflowList.get(2).setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_APPROVAL);
        correspon.setWorkflows(workflowList);
        correspon.getCorresponType().getWorkflowPattern().setWorkflowCd(SystemConfig
            .getValue(KEY_PATTERN_3));

        String expectedComment = "Comment**********";
        Workflow workflow = workflowList.get(2);
        workflow.setCommentOn(expectedComment);

        // 検証用のオブジェクト
        Correspon expCorrespon = new Correspon();
        expCorrespon.setId(correspon.getId());
        expCorrespon.setUpdatedBy(user);
        expCorrespon.setWorkflowStatus(WorkflowStatus.ISSUED);
        expCorrespon.setCorresponNo(expCorresponNo);
        expCorrespon.setIssuedAt(DateUtil.getNow());
        expCorrespon.setVersionNo(correspon.getVersionNo());

        Workflow expWorkflow = new Workflow();
        expWorkflow.setId(workflow.getId());
        expWorkflow.setUpdatedBy(user);
        expWorkflow.setWorkflowProcessStatus(WorkflowProcessStatus.APPROVED);
        expWorkflow.setCommentOn(expectedComment);
        expWorkflow.setFinishedAt(expectedDate);
        expWorkflow.setFinishedBy(user);
        expWorkflow.setVersionNo(workflow.getVersionNo());

        // Adminでテスト実行
        try {
            service.approve(correspon, workflow);

            fail("例外が発生していない");
        } catch (ServiceAbortException e) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_PROJECT_DIFF, e
                .getMessageCode());
        }
    }

    /**
     * ログインユーザーが、指定されたコレポン文書を検証済にするテスト.
     * Checker承認作業の権限チェック.
     * @throws Exception
     */
    @Test
    public void testApproveValidate() throws Exception {
        Date expectedDate = new GregorianCalendar(2009, 4, 1, 1, 1, 1).getTime();
        MockDateUtil.RET_GETNOW = expectedDate;

        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        List<Project> accessibleProjects = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        accessibleProjects.add(p);
        MockAbstractService.ACCESSIBLE_PROJECTS = accessibleProjects;

        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        MockAbstractService.IS_ANY_GROUP_ADMIN = false;

        String expectedEmpNo = "99999";
        User user = new User();
        user.setEmpNo(expectedEmpNo);
        MockAbstractService.CURRENT_USER = user;

        // 引数の作成
        Correspon correspon = createCorrespon();
        List<Workflow> workflowList = createWorkflowDummy();
        workflowList.get(2).setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_APPROVAL);
        correspon.setWorkflows(workflowList);
        correspon.getCorresponType().getWorkflowPattern().setWorkflowCd(SystemConfig
            .getValue(KEY_PATTERN_3));

        String expectedComment = "Comment**********";
        Workflow workflow = workflowList.get(2);
        workflow.setCommentOn(expectedComment);

        try {
            service.approve(correspon, workflow);

            fail("例外が発生していない");
        } catch (ServiceAbortException e) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT, e
                .getMessageCode());
        }
    }

    /**
     * ログインユーザーが、指定されたコレポン文書を検証済にするテスト.
     * Checker承認作業の権限チェック.(SystemAdminなのですり抜け)
     * @throws Exception
     */
    @Test
    public void testApproveValidateSystemAdmin() throws Exception {
        Date expectedDate = new GregorianCalendar(2009, 4, 1, 1, 1, 1).getTime();
        MockDateUtil.RET_GETNOW = expectedDate;

        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        List<Project> accessibleProjects = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        accessibleProjects.add(p);
        MockAbstractService.ACCESSIBLE_PROJECTS = accessibleProjects;

        MockAbstractService.IS_SYSTEM_ADMIN = true;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        MockAbstractService.IS_ANY_GROUP_ADMIN = false;

        String expectedEmpNo = "99999";
        User user = new User();
        user.setEmpNo(expectedEmpNo);
        MockAbstractService.CURRENT_USER = user;

        String expCorresponNo = "NEW:CORRESPON_NO:001";
        MockCorresponSequenceService.RET_CORRESPON_NO = expCorresponNo;

        // 引数の作成
        Correspon correspon = createCorrespon();
        List<Workflow> workflowList = createWorkflowDummy();
        workflowList.get(2).setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_APPROVAL);
        correspon.setWorkflows(workflowList);
        correspon.getCorresponType().getWorkflowPattern().setWorkflowCd(SystemConfig
            .getValue(KEY_PATTERN_3));

        String expectedComment = "Comment**********";
        Workflow workflow = workflowList.get(2);
        workflow.setCommentOn(expectedComment);

        // 検証用のオブジェクト
        Correspon expCorrespon = new Correspon();
        expCorrespon.setId(correspon.getId());
        expCorrespon.setUpdatedBy(user);
        expCorrespon.setWorkflowStatus(WorkflowStatus.ISSUED);
        expCorrespon.setCorresponNo(expCorresponNo);
        expCorrespon.setIssuedBy(MockAbstractService.CURRENT_USER);
        expCorrespon.setIssuedAt(DateUtil.getNow());
        expCorrespon.setVersionNo(correspon.getVersionNo());

        Workflow expWorkflow = new Workflow();
        expWorkflow.setId(workflow.getId());
        expWorkflow.setUpdatedBy(user);
        expWorkflow.setWorkflowProcessStatus(WorkflowProcessStatus.APPROVED);
        expWorkflow.setCommentOn(expectedComment);
        expWorkflow.setFinishedAt(expectedDate);
        expWorkflow.setFinishedBy(user);
        expWorkflow.setVersionNo(workflow.getVersionNo());

        // Adminでテスト実行 (例外は発生しない）
        service.approve(correspon, workflow);

        Correspon resultCorrespon = MockCorresponDao.ACT_UPDATE_CORRESPON.get(0);
        Workflow resultWorkflow = MockWorkflowDao.ACT_UPDATE_WORKFLOW.get(0);

        assertEquals(expCorrespon.toString(), resultCorrespon.toString());
        assertEquals(expWorkflow.toString(), resultWorkflow.toString());
    }

    /**
     * ログインユーザーが、指定されたコレポン文書を検証済にするテスト.
     * Checker承認作業の権限チェック.(ProjectAdminなのですり抜け)
     * @throws Exception
     */
    @Test
    public void testApproveValidateProjectAdmin() throws Exception {
        Date expectedDate = new GregorianCalendar(2009, 4, 1, 1, 1, 1).getTime();
        MockDateUtil.RET_GETNOW = expectedDate;

        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        List<Project> accessibleProjects = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        accessibleProjects.add(p);
        MockAbstractService.ACCESSIBLE_PROJECTS = accessibleProjects;

        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN = true;
        MockAbstractService.IS_ANY_GROUP_ADMIN = false;

        String expectedEmpNo = "99999";
        User user = new User();
        user.setEmpNo(expectedEmpNo);
        MockAbstractService.CURRENT_USER = user;

        String expCorresponNo = "NEW:CORRESPON_NO:001";
        MockCorresponSequenceService.RET_CORRESPON_NO = expCorresponNo;

        // 引数の作成
        Correspon correspon = createCorrespon();
        List<Workflow> workflowList = createWorkflowDummy();
        workflowList.get(2).setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_APPROVAL);
        correspon.setWorkflows(workflowList);
        correspon.getCorresponType().getWorkflowPattern().setWorkflowCd(SystemConfig
            .getValue(KEY_PATTERN_3));

        String expectedComment = "Comment**********";
        Workflow workflow = workflowList.get(2);
        workflow.setCommentOn(expectedComment);

        // 検証用のオブジェクト
        Correspon expCorrespon = new Correspon();
        expCorrespon.setId(correspon.getId());
        expCorrespon.setUpdatedBy(user);
        expCorrespon.setWorkflowStatus(WorkflowStatus.ISSUED);
        expCorrespon.setCorresponNo(expCorresponNo);
        expCorrespon.setIssuedBy(MockAbstractService.CURRENT_USER);
        expCorrespon.setIssuedAt(DateUtil.getNow());
        expCorrespon.setVersionNo(correspon.getVersionNo());

        Workflow expWorkflow = new Workflow();
        expWorkflow.setId(workflow.getId());
        expWorkflow.setUpdatedBy(user);
        expWorkflow.setWorkflowProcessStatus(WorkflowProcessStatus.APPROVED);
        expWorkflow.setCommentOn(expectedComment);
        expWorkflow.setFinishedAt(expectedDate);
        expWorkflow.setFinishedBy(user);
        expWorkflow.setVersionNo(workflow.getVersionNo());

        // Adminでテスト実行 (例外は発生しない）
        service.approve(correspon, workflow);

        Correspon resultCorrespon = MockCorresponDao.ACT_UPDATE_CORRESPON.get(0);
        Workflow resultWorkflow = MockWorkflowDao.ACT_UPDATE_WORKFLOW.get(0);

        assertEquals(expCorrespon.toString(), resultCorrespon.toString());
        assertEquals(expWorkflow.toString(), resultWorkflow.toString());
    }

    /**
     * ログインユーザーが、指定されたコレポン文書を検証済にするテスト.
     * Checker承認作業の権限チェック.(GroupAdminで、かつApproverに含まれる)
     * @throws Exception
     */
    @Test
    public void testApproveValidateGroupAdmin_1() throws Exception {
        Date expectedDate = new GregorianCalendar(2009, 4, 1, 1, 1, 1).getTime();
        MockDateUtil.RET_GETNOW = expectedDate;

        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        List<Project> accessibleProjects = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        accessibleProjects.add(p);
        MockAbstractService.ACCESSIBLE_PROJECTS = accessibleProjects;

        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        MockAbstractService.IS_ANY_GROUP_ADMIN = false;
        List<Long> groupAdmin = new ArrayList<Long>();
        groupAdmin.add(5963L);
        MockAbstractService.IS_GROUP_ADMIN = groupAdmin;

        String expectedEmpNo = "99999";
        User user = new User();
        user.setEmpNo(expectedEmpNo);
        MockAbstractService.CURRENT_USER = user;

        List<ProjectUser> mockUser = new ArrayList<ProjectUser>();
        User newUser = new User();
        newUser.setEmpNo("99999");
        ProjectUser pu = new ProjectUser();
        pu.setUser(newUser);
        pu.setProjectId("PJ1");
        mockUser.add(pu);
        newUser = new User();
        newUser.setEmpNo("80002");
        pu = new ProjectUser();
        pu.setUser(newUser);
        pu.setProjectId("PJ1");
        mockUser.add(pu);
        MockUserDao.RET_FINDPROJECTUSER = mockUser;

        String expCorresponNo = "NEW:CORRESPON_NO:001";
        MockCorresponSequenceService.RET_CORRESPON_NO = expCorresponNo;

        // 引数の作成
        Correspon correspon = createCorrespon();
        List<Workflow> workflowList = createWorkflowDummy();
        workflowList.get(2).setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_APPROVAL);
        workflowList.get(2).setUser(user); // Approver
        correspon.setWorkflows(workflowList);
        correspon.getCorresponType().getWorkflowPattern().setWorkflowCd(SystemConfig
            .getValue(KEY_PATTERN_3));
        CorresponGroup cg = new CorresponGroup();
        cg.setId(5963L);
        correspon.setFromCorresponGroup(cg);

        String expectedComment = "Comment**********";
        Workflow workflow = workflowList.get(2);
        workflow.setCommentOn(expectedComment);

        // 検証用のオブジェクト
        Correspon expCorrespon = new Correspon();
        expCorrespon.setId(correspon.getId());
        expCorrespon.setUpdatedBy(user);
        expCorrespon.setWorkflowStatus(WorkflowStatus.ISSUED);
        expCorrespon.setCorresponNo(expCorresponNo);
        expCorrespon.setIssuedBy(MockAbstractService.CURRENT_USER);
        expCorrespon.setIssuedAt(DateUtil.getNow());
        expCorrespon.setVersionNo(correspon.getVersionNo());

        Workflow expWorkflow = new Workflow();
        expWorkflow.setId(workflow.getId());
        expWorkflow.setUpdatedBy(user);
        expWorkflow.setWorkflowProcessStatus(WorkflowProcessStatus.APPROVED);
        expWorkflow.setCommentOn(expectedComment);
        expWorkflow.setFinishedAt(expectedDate);
        expWorkflow.setFinishedBy(user);
        expWorkflow.setVersionNo(workflow.getVersionNo());

        // Adminでテスト実行 (例外は発生しない）
        service.approve(correspon, workflow);

        Correspon resultCorrespon = MockCorresponDao.ACT_UPDATE_CORRESPON.get(0);
        Workflow resultWorkflow = MockWorkflowDao.ACT_UPDATE_WORKFLOW.get(0);

        assertEquals(expCorrespon.toString(), resultCorrespon.toString());
        assertEquals(expWorkflow.toString(), resultWorkflow.toString());
    }

    /**
     * ログインユーザーが、指定されたコレポン文書を検証済にするテスト.
     * Checker承認作業の権限チェック.(GroupAdminで、かつApproverに含まれない)
     * @throws Exception
     */
    @Test
    public void testApproveValidateGroupAdmin_2() throws Exception {
        Date expectedDate = new GregorianCalendar(2009, 4, 1, 1, 1, 1).getTime();
        MockDateUtil.RET_GETNOW = expectedDate;

        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        List<Project> accessibleProjects = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        accessibleProjects.add(p);
        MockAbstractService.ACCESSIBLE_PROJECTS = accessibleProjects;

        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        MockAbstractService.IS_ANY_GROUP_ADMIN = false;
        List<Long> groupAdmin = new ArrayList<Long>();
        groupAdmin.add(5963L);
        MockAbstractService.IS_GROUP_ADMIN = groupAdmin;

        String expectedEmpNo = "99999";
        User user = new User();
        user.setEmpNo(expectedEmpNo);
        MockAbstractService.CURRENT_USER = user;

        List<ProjectUser> mockUser = new ArrayList<ProjectUser>();
        User newUser = new User();
        newUser.setEmpNo("99999");
        ProjectUser pu = new ProjectUser();
        pu.setUser(newUser);
        pu.setProjectId("PJ1");
        mockUser.add(pu);
        newUser = new User();
        newUser.setEmpNo("80002");
        pu = new ProjectUser();
        pu.setUser(newUser);
        pu.setProjectId("PJ1");
        mockUser.add(pu);
        MockUserDao.RET_FINDPROJECTUSER = mockUser;

        // 引数の作成 -- Approverに含まれない
        Correspon correspon = createCorrespon();
        List<Workflow> workflowList = createWorkflowDummy();
        workflowList.get(2).setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_APPROVAL);
        correspon.setWorkflows(workflowList);
        correspon.getCorresponType().getWorkflowPattern().setWorkflowCd(SystemConfig
            .getValue(KEY_PATTERN_3));
        CorresponGroup cg = new CorresponGroup();
        cg.setId(5963L);
        correspon.setFromCorresponGroup(cg);

        String expectedComment = "Comment**********";
        Workflow workflow = workflowList.get(2);
        workflow.setCommentOn(expectedComment);

        try {
            service.approve(correspon, workflow);
        } catch (ServiceAbortException e) {
            fail("例外が発生");
        }
    }

    /**
     * ログインユーザーが、指定されたコレポン文書を検証済にするテスト.
     * ワークフローの承認作業状態のチェック.
     * @throws Exception
     */
    @Test
    public void testApproveWorkflowStatus() throws Exception {
        Date expectedDate = new GregorianCalendar(2009, 4, 1, 1, 1, 1).getTime();
        MockDateUtil.RET_GETNOW = expectedDate;

        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        List<Project> accessibleProjects = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        accessibleProjects.add(p);
        MockAbstractService.ACCESSIBLE_PROJECTS = accessibleProjects;

        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        MockAbstractService.IS_ANY_GROUP_ADMIN = false;

        String expectedEmpNo = "80003";
        User user = new User();
        user.setEmpNo(expectedEmpNo);
        MockAbstractService.CURRENT_USER = user;

        List<ProjectUser> mockUser = new ArrayList<ProjectUser>();
        User newUser = new User();
        newUser.setEmpNo("80001");
        ProjectUser pu = new ProjectUser();
        pu.setUser(newUser);
        pu.setProjectId("PJ1");
        mockUser.add(pu);
        newUser = new User();
        newUser.setEmpNo("80002");
        pu = new ProjectUser();
        pu.setUser(newUser);
        pu.setProjectId("PJ1");
        mockUser.add(pu);
        MockUserDao.RET_FINDPROJECTUSER = mockUser;

        // 引数の作成
        Correspon correspon = createCorrespon();
        List<Workflow> workflowList = createWorkflowDummy();
        workflowList.get(2).setWorkflowProcessStatus(WorkflowProcessStatus.APPROVED);// 承認作業状態エラー
        correspon.setWorkflows(workflowList);
        correspon.getCorresponType().getWorkflowPattern().setWorkflowCd(SystemConfig
            .getValue(KEY_PATTERN_3));

        String expectedComment = "Comment**********";
        Workflow workflow = workflowList.get(2);
        workflow.setCommentOn(expectedComment);

        try {
            service.approve(correspon, workflow);

            fail("例外が発生していない");
        } catch (ServiceAbortException e) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_WORKFLOW_STATUS_INVALID_FOR_APPROVER,
                e.getMessageCode());
        }
    }

    /**
     * testApprove_1(ただし返信文書).
     * @throws Exception
     */
    @Test
    public void testApprove_reply01() throws Exception {
        Date expectedDate = new GregorianCalendar(2009, 4, 1, 1, 1, 1).getTime();
        MockDateUtil.RET_GETNOW = expectedDate;

        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        List<Project> accessibleProjects = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        accessibleProjects.add(p);
        MockAbstractService.ACCESSIBLE_PROJECTS = accessibleProjects;

        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        MockAbstractService.IS_ANY_GROUP_ADMIN = false;

        // 引数の作成
        String expectedEmpNo = "80003";
        User user = new User();
        user.setEmpNo(expectedEmpNo);
        MockAbstractService.CURRENT_USER = user;

        List<ProjectUser> mockUser = new ArrayList<ProjectUser>();
        ProjectUser pu = new ProjectUser();
        pu.setUser(user);
        pu.setProjectId("PJ1");
        mockUser.add(pu);
        MockUserDao.RET_FINDPROJECTUSER = mockUser;

        String expCorresponNo = "NEW:CORRESPON_NO:001";
        MockCorresponSequenceService.RET_CORRESPON_NO = expCorresponNo;

        Correspon correspon = createCorrespon();
        List<Workflow> workflowList = createWorkflowDummy();
        workflowList.get(2).setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_APPROVAL);
        correspon.setWorkflows(workflowList);

        String expectedComment = "Comment**********";
        Workflow workflow = workflowList.get(2);
        workflow.setCommentOn(expectedComment);

        // 検証用のオブジェクト
        Correspon expCorrespon = new Correspon();
        expCorrespon.setId(correspon.getId());
        expCorrespon.setUpdatedBy(user);
        expCorrespon.setWorkflowStatus(WorkflowStatus.ISSUED);
        expCorrespon.setCorresponNo(expCorresponNo);
        expCorrespon.setIssuedBy(MockAbstractService.CURRENT_USER);
        expCorrespon.setIssuedAt(DateUtil.getNow());
        expCorrespon.setVersionNo(correspon.getVersionNo());

        Workflow expWorkflow = new Workflow();
        expWorkflow.setId(workflow.getId());
        expWorkflow.setUpdatedBy(user);
        expWorkflow.setWorkflowProcessStatus(WorkflowProcessStatus.APPROVED);
        expWorkflow.setCommentOn(expectedComment);
        expWorkflow.setFinishedAt(expectedDate);
        expWorkflow.setFinishedBy(user);
        expWorkflow.setVersionNo(workflow.getVersionNo());

        // 返信元コレポン設定
        correspon.setParentCorresponId(1234L);
        Correspon oldCorrespon = new Correspon();
        oldCorrespon.setCorresponStatus(CorresponStatus.CLOSED);
        MockAbstractDao.RET_FIND_BY_ID.put("1", oldCorrespon);

        // テスト実行
        service.approve(correspon, workflow);

        assertEquals(1, MockWorkflowDao.ACT_UPDATE_WORKFLOW.size());

        Correspon resultCorrespon = MockCorresponDao.ACT_UPDATE_CORRESPON.get(0);
        Workflow resultWorkflow = MockWorkflowDao.ACT_UPDATE_WORKFLOW.get(0);

        assertEquals(expCorrespon.toString(), resultCorrespon.toString());
        assertEquals(expWorkflow.toString(), resultWorkflow.toString());
    }

    /**
     * testApprove_1(ただし返信文書。返信元コレポンが存在しない).
     * @throws Exception
     */
    @Test
    public void testApprove_reply02() throws Exception {
        Date expectedDate = new GregorianCalendar(2009, 4, 1, 1, 1, 1).getTime();
        MockDateUtil.RET_GETNOW = expectedDate;

        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        List<Project> accessibleProjects = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        accessibleProjects.add(p);
        MockAbstractService.ACCESSIBLE_PROJECTS = accessibleProjects;

        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        MockAbstractService.IS_ANY_GROUP_ADMIN = false;

        // 引数の作成
        String expectedEmpNo = "80003";
        User user = new User();
        user.setEmpNo(expectedEmpNo);
        MockAbstractService.CURRENT_USER = user;

        List<ProjectUser> mockUser = new ArrayList<ProjectUser>();
        ProjectUser pu = new ProjectUser();
        pu.setUser(user);
        pu.setProjectId("PJ1");
        mockUser.add(pu);
        MockUserDao.RET_FINDPROJECTUSER = mockUser;

        String expCorresponNo = "NEW:CORRESPON_NO:001";
        MockCorresponSequenceService.RET_CORRESPON_NO = expCorresponNo;

        Correspon correspon = createCorrespon();
        List<Workflow> workflowList = createWorkflowDummy();
        workflowList.get(2).setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_APPROVAL);
        correspon.setWorkflows(workflowList);

        String expectedComment = "Comment**********";
        Workflow workflow = workflowList.get(2);
        workflow.setCommentOn(expectedComment);

        // 検証用のオブジェクト
        Correspon expCorrespon = new Correspon();
        expCorrespon.setId(correspon.getId());
        expCorrespon.setUpdatedBy(user);
        expCorrespon.setWorkflowStatus(WorkflowStatus.ISSUED);
        expCorrespon.setCorresponNo(expCorresponNo);
        expCorrespon.setIssuedBy(MockAbstractService.CURRENT_USER);
        expCorrespon.setIssuedAt(DateUtil.getNow());
        expCorrespon.setVersionNo(correspon.getVersionNo());

        Workflow expWorkflow = new Workflow();
        expWorkflow.setId(workflow.getId());
        expWorkflow.setUpdatedBy(user);
        expWorkflow.setWorkflowProcessStatus(WorkflowProcessStatus.APPROVED);
        expWorkflow.setCommentOn(expectedComment);
        expWorkflow.setFinishedAt(expectedDate);
        expWorkflow.setFinishedBy(user);
        expWorkflow.setVersionNo(workflow.getVersionNo());

        // 返信元コレポンが存在しなくても、発行するコレポンの文書状態は変更なし
        correspon.setParentCorresponId(1234L);
        MockAbstractDao.RET_FIND_BY_ID.put("1", null);
        expCorrespon.setCorresponStatus(null);

        // テスト実行
        service.approve(correspon, workflow);

        assertEquals(1, MockWorkflowDao.ACT_UPDATE_WORKFLOW.size());

        Correspon resultCorrespon = MockCorresponDao.ACT_UPDATE_CORRESPON.get(0);
        Workflow resultWorkflow = MockWorkflowDao.ACT_UPDATE_WORKFLOW.get(0);

        assertEquals(expCorrespon.toString(), resultCorrespon.toString());
        assertEquals(expWorkflow.toString(), resultWorkflow.toString());
    }

    /**
     * testApprove_1(ただし返信文書。返信元コレポンがCanceled).
     * @throws Exception
     */
    @Test
    public void testApprove_reply03() throws Exception {
        Date expectedDate = new GregorianCalendar(2009, 4, 1, 1, 1, 1).getTime();
        MockDateUtil.RET_GETNOW = expectedDate;

        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        List<Project> accessibleProjects = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        accessibleProjects.add(p);
        MockAbstractService.ACCESSIBLE_PROJECTS = accessibleProjects;

        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        MockAbstractService.IS_ANY_GROUP_ADMIN = false;

        // 引数の作成
        String expectedEmpNo = "80003";
        User user = new User();
        user.setEmpNo(expectedEmpNo);
        MockAbstractService.CURRENT_USER = user;

        List<ProjectUser> mockUser = new ArrayList<ProjectUser>();
        ProjectUser pu = new ProjectUser();
        pu.setUser(user);
        pu.setProjectId("PJ1");
        mockUser.add(pu);
        MockUserDao.RET_FINDPROJECTUSER = mockUser;

        String expCorresponNo = "NEW:CORRESPON_NO:001";
        MockCorresponSequenceService.RET_CORRESPON_NO = expCorresponNo;

        Correspon correspon = createCorrespon();
        List<Workflow> workflowList = createWorkflowDummy();
        workflowList.get(2).setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_APPROVAL);
        correspon.setWorkflows(workflowList);

        String expectedComment = "Comment**********";
        Workflow workflow = workflowList.get(2);
        workflow.setCommentOn(expectedComment);

        // 検証用のオブジェクト
        Correspon expCorrespon = new Correspon();
        expCorrespon.setId(correspon.getId());
        expCorrespon.setUpdatedBy(user);
        expCorrespon.setWorkflowStatus(WorkflowStatus.ISSUED);
        expCorrespon.setCorresponNo(expCorresponNo);
        expCorrespon.setIssuedBy(MockAbstractService.CURRENT_USER);
        expCorrespon.setIssuedAt(DateUtil.getNow());
        expCorrespon.setVersionNo(correspon.getVersionNo());

        Workflow expWorkflow = new Workflow();
        expWorkflow.setId(workflow.getId());
        expWorkflow.setUpdatedBy(user);
        expWorkflow.setWorkflowProcessStatus(WorkflowProcessStatus.APPROVED);
        expWorkflow.setCommentOn(expectedComment);
        expWorkflow.setFinishedAt(expectedDate);
        expWorkflow.setFinishedBy(user);
        expWorkflow.setVersionNo(workflow.getVersionNo());

        // 返信元コレポンの文書状態がCanceledでも、発行するコレポンの文書状態は変更なし
        correspon.setParentCorresponId(1234L);
        Correspon oldCorrespon = new Correspon();
        oldCorrespon.setCorresponStatus(CorresponStatus.CANCELED);
        MockAbstractDao.RET_FIND_BY_ID.put("1", oldCorrespon);
        expCorrespon.setCorresponStatus(null);

        // テスト実行
        service.approve(correspon, workflow);

        assertEquals(1, MockWorkflowDao.ACT_UPDATE_WORKFLOW.size());

        Correspon resultCorrespon = MockCorresponDao.ACT_UPDATE_CORRESPON.get(0);
        Workflow resultWorkflow = MockWorkflowDao.ACT_UPDATE_WORKFLOW.get(0);

        assertEquals(expCorrespon.toString(), resultCorrespon.toString());
        assertEquals(expWorkflow.toString(), resultWorkflow.toString());
    }

    /**
     * ログインユーザーが、指定されたコレポン文書を否認するテスト.
     * 自身がCheckerのパターン.
     * @throws Exception
     */
    @Test
    public void testDenyChecker() throws Exception {
        Date expectedDate = new GregorianCalendar(2009, 4, 1, 1, 1, 1).getTime();
        MockDateUtil.RET_GETNOW = expectedDate;

        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        List<Project> accessibleProjects = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        accessibleProjects.add(p);
        MockAbstractService.ACCESSIBLE_PROJECTS = accessibleProjects;

        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        MockAbstractService.IS_ANY_GROUP_ADMIN = false;

        // 引数の作成
        String expectedEmpNo = "80001";
        User user = new User();
        user.setEmpNo(expectedEmpNo);
        MockAbstractService.CURRENT_USER = user;

        List<ProjectUser> mockUser = new ArrayList<ProjectUser>();
        ProjectUser pu = new ProjectUser();
        pu.setUser(user);
        pu.setProjectId("PJ1");
        mockUser.add(pu);
        MockUserDao.RET_FINDPROJECTUSER = mockUser;

        Correspon correspon = createCorrespon();
        List<Workflow> workflowList = createWorkflowDummy();

        workflowList.get(0).setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_CHECK);
        workflowList.get(1).setWorkflowProcessStatus(WorkflowProcessStatus.NONE);
        correspon.setWorkflows(workflowList);

        String expectedComment = "Comment**********";
        Workflow workflow = workflowList.get(0);
        workflow.setCommentOn(expectedComment);

        // 検証用のオブジェクト
        Correspon expCorrespon = new Correspon();
        expCorrespon.setId(correspon.getId());
        expCorrespon.setUpdatedBy(user);
        expCorrespon.setWorkflowStatus(WorkflowStatus.DENIED);
        expCorrespon.setVersionNo(correspon.getVersionNo());

        Workflow expWorkflow = new Workflow();
        expWorkflow.setId(workflow.getId());
        expWorkflow.setWorkflowProcessStatus(WorkflowProcessStatus.DENIED);
        expWorkflow.setCommentOn(expectedComment);
        expWorkflow.setFinishedBy(user);
        expWorkflow.setFinishedAt(expectedDate);
        expWorkflow.setUpdatedBy(user);
        expWorkflow.setVersionNo(workflow.getVersionNo());

        // テスト実行
        service.deny(correspon, workflow);

        assertEquals(1, MockWorkflowDao.ACT_UPDATE_WORKFLOW.size());

        Correspon resultCorrespon = MockCorresponDao.ACT_UPDATE_CORRESPON.get(0);
        Workflow resultWorkflow = MockWorkflowDao.ACT_UPDATE_WORKFLOW.get(0);

        assertEquals(expCorrespon.toString(), resultCorrespon.toString());
        assertEquals(expWorkflow.toString(), resultWorkflow.toString());
    }

    /**
     * ログインユーザーが、指定されたコレポン文書を否認するテスト.
     * 自身がCheckerで重複しているパターン.
     * @throws Exception
     */
    @Test
    public void testDenyCheckerDuplication() throws Exception {
        Date expectedDate = new GregorianCalendar(2009, 4, 1, 1, 1, 1).getTime();
        MockDateUtil.RET_GETNOW = expectedDate;

        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        List<Project> accessibleProjects = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        accessibleProjects.add(p);
        MockAbstractService.ACCESSIBLE_PROJECTS = accessibleProjects;

        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        MockAbstractService.IS_ANY_GROUP_ADMIN = false;

        // 引数の作成
        String expectedEmpNo = "80001";
        User user = new User();
        user.setEmpNo(expectedEmpNo);
        MockAbstractService.CURRENT_USER = user;

        List<ProjectUser> mockUser = new ArrayList<ProjectUser>();
        ProjectUser pu = new ProjectUser();
        pu.setUser(user);
        pu.setProjectId("PJ1");
        mockUser.add(pu);
        MockUserDao.RET_FINDPROJECTUSER = mockUser;

        Correspon correspon = createCorrespon();
        List<Workflow> workflowList = createWorkflowDummy();

        workflowList.get(0).setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_CHECK);
        User u = new User();
        u.setEmpNo("80001");
        u.setNameE("Checker2 User");
        workflowList.get(1).setUser(u);
        workflowList.get(1).setWorkflowProcessStatus(WorkflowProcessStatus.NONE);
        correspon.setWorkflows(workflowList);

        String expectedComment = "Comment**********";
        Workflow workflow = workflowList.get(0);
        workflow.setCommentOn(expectedComment);

        // 検証用のオブジェクト
        Correspon expCorrespon = new Correspon();
        expCorrespon.setId(correspon.getId());
        expCorrespon.setUpdatedBy(user);
        expCorrespon.setWorkflowStatus(WorkflowStatus.DENIED);
        expCorrespon.setVersionNo(correspon.getVersionNo());

        Workflow expWorkflow = new Workflow();
        expWorkflow.setId(workflow.getId());
        expWorkflow.setWorkflowProcessStatus(WorkflowProcessStatus.DENIED);
        expWorkflow.setCommentOn(expectedComment);
        expWorkflow.setFinishedBy(user);
        expWorkflow.setFinishedAt(expectedDate);
        expWorkflow.setUpdatedBy(user);
        expWorkflow.setVersionNo(workflow.getVersionNo());

        // テスト実行
        try {
            service.deny(correspon, workflow);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            // 正しいエラーコードがセットされた例外がthrowされていることを検証
            assertEquals(ApplicationMessageCode.DUPLICATED_CHECKER, actual.getMessageCode());
            assertEquals(1, actual.getMessageVars().length);
            assertEquals("80001", actual.getMessageVars()[0]);
        }

    }

    /**
     * ログインユーザーが、指定されたコレポン文書を否認するテスト.
     * PreparerとCheckerが重複.
     * @throws Exception
     */
    @Test
    public void testDenyCheckerDuplicationPreparer() throws Exception {
        Date expectedDate = new GregorianCalendar(2009, 4, 1, 1, 1, 1).getTime();
        MockDateUtil.RET_GETNOW = expectedDate;

        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        List<Project> accessibleProjects = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        accessibleProjects.add(p);
        MockAbstractService.ACCESSIBLE_PROJECTS = accessibleProjects;

        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        MockAbstractService.IS_ANY_GROUP_ADMIN = false;

        // 引数の作成
        String expectedEmpNo = "80001";
        User user = new User();
        user.setEmpNo(expectedEmpNo);
        MockAbstractService.CURRENT_USER = user;

        List<ProjectUser> mockUser = new ArrayList<ProjectUser>();
        ProjectUser pu = new ProjectUser();
        pu.setUser(user);
        pu.setProjectId("PJ1");
        mockUser.add(pu);
        MockUserDao.RET_FINDPROJECTUSER = mockUser;

        Correspon correspon = createCorrespon();
        List<Workflow> workflowList = createWorkflowDummy();

        workflowList.get(0).setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_CHECK);
        User u = new User();
        u.setEmpNo("80000");
        u.setNameE("Preparer User");
        workflowList.get(1).setUser(u);
        workflowList.get(1).setWorkflowProcessStatus(WorkflowProcessStatus.NONE);
        correspon.setWorkflows(workflowList);

        String expectedComment = "Comment**********";
        Workflow workflow = workflowList.get(0);
        workflow.setCommentOn(expectedComment);

        // 検証用のオブジェクト
        Correspon expCorrespon = new Correspon();
        expCorrespon.setId(correspon.getId());
        expCorrespon.setUpdatedBy(user);
        expCorrespon.setWorkflowStatus(WorkflowStatus.DENIED);
        expCorrespon.setVersionNo(correspon.getVersionNo());

        Workflow expWorkflow = new Workflow();
        expWorkflow.setId(workflow.getId());
        expWorkflow.setWorkflowProcessStatus(WorkflowProcessStatus.DENIED);
        expWorkflow.setCommentOn(expectedComment);
        expWorkflow.setFinishedBy(user);
        expWorkflow.setFinishedAt(expectedDate);
        expWorkflow.setUpdatedBy(user);
        expWorkflow.setVersionNo(workflow.getVersionNo());

        // テスト実行
        service.deny(correspon, workflow);

        assertEquals(1, MockWorkflowDao.ACT_UPDATE_WORKFLOW.size());

        Correspon resultCorrespon = MockCorresponDao.ACT_UPDATE_CORRESPON.get(0);
        Workflow resultWorkflow = MockWorkflowDao.ACT_UPDATE_WORKFLOW.get(0);

        assertEquals(expCorrespon.toString(), resultCorrespon.toString());
        assertEquals(expWorkflow.toString(), resultWorkflow.toString());

    }

    /**
     * ログインユーザーが、指定されたコレポン文書を否認するテスト.
     * 自身がApproverのパターン.
     * @throws Exception
     */
    @Test
    public void testDenyApprover() throws Exception {
        Date expectedDate = new GregorianCalendar(2009, 4, 1, 1, 1, 1).getTime();
        MockDateUtil.RET_GETNOW = expectedDate;

        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        List<Project> accessibleProjects = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        accessibleProjects.add(p);
        MockAbstractService.ACCESSIBLE_PROJECTS = accessibleProjects;

        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        MockAbstractService.IS_ANY_GROUP_ADMIN = false;

        // 引数の作成
        String expectedEmpNo = "80003";
        User user = new User();
        user.setEmpNo(expectedEmpNo);
        MockAbstractService.CURRENT_USER = user;

        List<ProjectUser> mockUser = new ArrayList<ProjectUser>();
        ProjectUser pu = new ProjectUser();
        pu.setUser(user);
        pu.setProjectId("PJ1");
        mockUser.add(pu);
        MockUserDao.RET_FINDPROJECTUSER = mockUser;

        Correspon correspon = createCorrespon();
        List<Workflow> workflowList = createWorkflowDummy();

        workflowList.get(2).setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_APPROVAL);
        correspon.setWorkflows(workflowList);

        String expectedComment = "Comment**********";
        Workflow workflow = workflowList.get(2);
        workflow.setCommentOn(expectedComment);

        // 検証用のオブジェクト
        Correspon expCorrespon = new Correspon();
        expCorrespon.setId(correspon.getId());
        expCorrespon.setUpdatedBy(user);
        expCorrespon.setWorkflowStatus(WorkflowStatus.DENIED);
        expCorrespon.setVersionNo(correspon.getVersionNo());

        Workflow expWorkflow = new Workflow();
        expWorkflow.setId(workflow.getId());
        expWorkflow.setWorkflowProcessStatus(WorkflowProcessStatus.DENIED);
        expWorkflow.setCommentOn(expectedComment);
        expWorkflow.setFinishedBy(user);
        expWorkflow.setFinishedAt(expectedDate);
        expWorkflow.setUpdatedBy(user);
        expWorkflow.setVersionNo(workflow.getVersionNo());

        // テスト実行
        service.deny(correspon, workflow);

        assertEquals(1, MockWorkflowDao.ACT_UPDATE_WORKFLOW.size());

        Correspon resultCorrespon = MockCorresponDao.ACT_UPDATE_CORRESPON.get(0);
        Workflow resultWorkflow = MockWorkflowDao.ACT_UPDATE_WORKFLOW.get(0);

        assertEquals(expCorrespon.toString(), resultCorrespon.toString());
        assertEquals(expWorkflow.toString(), resultWorkflow.toString());
    }

    /**
     * ログインユーザーが、指定されたコレポン文書を否認するテスト.
     * 自身がApproverで重複しているパターン.
     * @throws Exception
     */
    @Test
    public void testDenyApproverDuplication() throws Exception {
        Date expectedDate = new GregorianCalendar(2009, 4, 1, 1, 1, 1).getTime();
        MockDateUtil.RET_GETNOW = expectedDate;

        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        List<Project> accessibleProjects = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        accessibleProjects.add(p);
        MockAbstractService.ACCESSIBLE_PROJECTS = accessibleProjects;

        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        MockAbstractService.IS_ANY_GROUP_ADMIN = false;

        // 引数の作成
        String expectedEmpNo = "80003";
        User user = new User();
        user.setEmpNo(expectedEmpNo);
        MockAbstractService.CURRENT_USER = user;

        List<ProjectUser> mockUser = new ArrayList<ProjectUser>();
        ProjectUser pu = new ProjectUser();
        pu.setUser(user);
        pu.setProjectId("PJ1");
        mockUser.add(pu);
        MockUserDao.RET_FINDPROJECTUSER = mockUser;

        Correspon correspon = createCorrespon();
        List<Workflow> workflowList = createWorkflowDummy();

        User u = new User();
        u.setEmpNo("80003");
        u.setNameE("Approver2 User");
        workflowList.get(1).setUser(u);
        workflowList.get(2).setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_APPROVAL);
        correspon.setWorkflows(workflowList);

        String expectedComment = "Comment**********";
        Workflow workflow = workflowList.get(2);
        workflow.setCommentOn(expectedComment);

        // 検証用のオブジェクト
        Correspon expCorrespon = new Correspon();
        expCorrespon.setId(correspon.getId());
        expCorrespon.setUpdatedBy(user);
        expCorrespon.setWorkflowStatus(WorkflowStatus.DENIED);
        expCorrespon.setVersionNo(correspon.getVersionNo());

        Workflow expWorkflow = new Workflow();
        expWorkflow.setId(workflow.getId());
        expWorkflow.setWorkflowProcessStatus(WorkflowProcessStatus.DENIED);
        expWorkflow.setCommentOn(expectedComment);
        expWorkflow.setFinishedBy(user);
        expWorkflow.setFinishedAt(expectedDate);
        expWorkflow.setUpdatedBy(user);
        expWorkflow.setVersionNo(workflow.getVersionNo());

        // テスト実行
        try {
            service.deny(correspon, workflow);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            // 正しいエラーコードがセットされた例外がthrowされていることを検証
            assertEquals(ApplicationMessageCode.DUPLICATED_CHECKER_APPROVER, actual
                .getMessageCode());
            assertEquals(1, actual.getMessageVars().length);
            assertEquals("80003", actual.getMessageVars()[0]);
        }
    }

    /**
     * ログインユーザーが、指定されたコレポン文書を否認するテスト.
     * PreparerとApproverが重複.
     * @throws Exception
     */
    @Test
    public void testDenyApproverDuplicationPreparer() throws Exception {
        Date expectedDate = new GregorianCalendar(2009, 4, 1, 1, 1, 1).getTime();
        MockDateUtil.RET_GETNOW = expectedDate;

        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        List<Project> accessibleProjects = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        accessibleProjects.add(p);
        MockAbstractService.ACCESSIBLE_PROJECTS = accessibleProjects;

        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        MockAbstractService.IS_ANY_GROUP_ADMIN = false;

        // 引数の作成
        String expectedEmpNo = "80000";
        User user = new User();
        user.setEmpNo(expectedEmpNo);
        MockAbstractService.CURRENT_USER = user;

        List<ProjectUser> mockUser = new ArrayList<ProjectUser>();
        ProjectUser pu = new ProjectUser();
        pu.setUser(user);
        pu.setProjectId("PJ1");
        mockUser.add(pu);
        MockUserDao.RET_FINDPROJECTUSER = mockUser;

        Correspon correspon = createCorrespon();
        List<Workflow> workflowList = createWorkflowDummy();

        User u = new User();
        u.setEmpNo("80000");
        u.setNameE("Preparer User");
        workflowList.get(2).setUser(u);
        workflowList.get(2).setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_APPROVAL);
        correspon.setWorkflows(workflowList);

        String expectedComment = "Comment**********";
        Workflow workflow = workflowList.get(2);
        workflow.setCommentOn(expectedComment);

        // 検証用のオブジェクト
        Correspon expCorrespon = new Correspon();
        expCorrespon.setId(correspon.getId());
        expCorrespon.setUpdatedBy(user);
        expCorrespon.setWorkflowStatus(WorkflowStatus.DENIED);
        expCorrespon.setVersionNo(correspon.getVersionNo());

        Workflow expWorkflow = new Workflow();
        expWorkflow.setId(workflow.getId());
        expWorkflow.setWorkflowProcessStatus(WorkflowProcessStatus.DENIED);
        expWorkflow.setCommentOn(expectedComment);
        expWorkflow.setFinishedBy(user);
        expWorkflow.setFinishedAt(expectedDate);
        expWorkflow.setUpdatedBy(user);
        expWorkflow.setVersionNo(workflow.getVersionNo());

        // テスト実行
        service.deny(correspon, workflow);

        assertEquals(1, MockWorkflowDao.ACT_UPDATE_WORKFLOW.size());

        Correspon resultCorrespon = MockCorresponDao.ACT_UPDATE_CORRESPON.get(0);
        Workflow resultWorkflow = MockWorkflowDao.ACT_UPDATE_WORKFLOW.get(0);

        assertEquals(expCorrespon.toString(), resultCorrespon.toString());
        assertEquals(expWorkflow.toString(), resultWorkflow.toString());
    }

    /**
     * ログインユーザーが、指定されたコレポン文書を否認するテスト.
     * 自身がSystemAdminのパターン.
     * @throws Exception
     */
    @Test
    public void testDenySystemAdmin() throws Exception {
        Date expectedDate = new GregorianCalendar(2009, 4, 1, 1, 1, 1).getTime();
        MockDateUtil.RET_GETNOW = expectedDate;

        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        List<Project> accessibleProjects = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        accessibleProjects.add(p);
        MockAbstractService.ACCESSIBLE_PROJECTS = accessibleProjects;

        MockAbstractService.IS_SYSTEM_ADMIN = true;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        MockAbstractService.IS_ANY_GROUP_ADMIN = false;

        // 引数の作成
        String expectedEmpNo = "9999";
        User user = new User();
        user.setEmpNo(expectedEmpNo);
        MockAbstractService.CURRENT_USER = user;

        List<ProjectUser> mockUser = new ArrayList<ProjectUser>();
        ProjectUser pu = new ProjectUser();
        pu.setUser(user);
        pu.setProjectId("PJ1");
        mockUser.add(pu);
        MockUserDao.RET_FINDPROJECTUSER = mockUser;

        Correspon correspon = createCorrespon();
        List<Workflow> workflowList = createWorkflowDummy();

        workflowList.get(0).setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_CHECK);
        workflowList.get(1).setWorkflowProcessStatus(WorkflowProcessStatus.NONE);
        correspon.setWorkflows(workflowList);

        String expectedComment = "Comment**********";
        Workflow workflow = workflowList.get(0);
        workflow.setCommentOn(expectedComment);

        // 検証用のオブジェクト
        Correspon expCorrespon = new Correspon();
        expCorrespon.setId(correspon.getId());
        expCorrespon.setUpdatedBy(user);
        expCorrespon.setWorkflowStatus(WorkflowStatus.DENIED);
        expCorrespon.setVersionNo(correspon.getVersionNo());

        Workflow expWorkflow = new Workflow();
        expWorkflow.setId(workflow.getId());
        expWorkflow.setWorkflowProcessStatus(WorkflowProcessStatus.DENIED);
        expWorkflow.setCommentOn(expectedComment);
        expWorkflow.setFinishedBy(user);
        expWorkflow.setFinishedAt(expectedDate);
        expWorkflow.setUpdatedBy(user);
        expWorkflow.setVersionNo(workflow.getVersionNo());

        // テスト実行
        service.deny(correspon, workflow);

        assertEquals(1, MockWorkflowDao.ACT_UPDATE_WORKFLOW.size());

        Correspon resultCorrespon = MockCorresponDao.ACT_UPDATE_CORRESPON.get(0);
        Workflow resultWorkflow = MockWorkflowDao.ACT_UPDATE_WORKFLOW.get(0);

        assertEquals(expCorrespon.toString(), resultCorrespon.toString());
        assertEquals(expWorkflow.toString(), resultWorkflow.toString());
    }

    /**
     * ログインユーザーが、指定されたコレポン文書を否認するテスト.
     * 自身がProjectAdminのパターン.
     * @throws Exception
     */
    @Test
    public void testDenyProjectAdmin() throws Exception {
        Date expectedDate = new GregorianCalendar(2009, 4, 1, 1, 1, 1).getTime();
        MockDateUtil.RET_GETNOW = expectedDate;

        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        List<Project> accessibleProjects = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        accessibleProjects.add(p);
        MockAbstractService.ACCESSIBLE_PROJECTS = accessibleProjects;

        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN = true;
        MockAbstractService.IS_ANY_GROUP_ADMIN = false;

        // 引数の作成
        String expectedEmpNo = "9999";
        User user = new User();
        user.setEmpNo(expectedEmpNo);
        MockAbstractService.CURRENT_USER = user;

        List<ProjectUser> mockUser = new ArrayList<ProjectUser>();
        ProjectUser pu = new ProjectUser();
        pu.setUser(user);
        pu.setProjectId("PJ1");
        mockUser.add(pu);
        MockUserDao.RET_FINDPROJECTUSER = mockUser;

        Correspon correspon = createCorrespon();
        List<Workflow> workflowList = createWorkflowDummy();

        workflowList.get(0).setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_CHECK);
        workflowList.get(1).setWorkflowProcessStatus(WorkflowProcessStatus.NONE);
        correspon.setWorkflows(workflowList);

        String expectedComment = "Comment**********";
        Workflow workflow = workflowList.get(0);
        workflow.setCommentOn(expectedComment);

        // 検証用のオブジェクト
        Correspon expCorrespon = new Correspon();
        expCorrespon.setId(correspon.getId());
        expCorrespon.setUpdatedBy(user);
        expCorrespon.setWorkflowStatus(WorkflowStatus.DENIED);
        expCorrespon.setVersionNo(correspon.getVersionNo());

        Workflow expWorkflow = new Workflow();
        expWorkflow.setId(workflow.getId());
        expWorkflow.setWorkflowProcessStatus(WorkflowProcessStatus.DENIED);
        expWorkflow.setCommentOn(expectedComment);
        expWorkflow.setFinishedBy(user);
        expWorkflow.setFinishedAt(expectedDate);
        expWorkflow.setUpdatedBy(user);
        expWorkflow.setVersionNo(workflow.getVersionNo());

        // テスト実行
        service.deny(correspon, workflow);

        assertEquals(1, MockWorkflowDao.ACT_UPDATE_WORKFLOW.size());

        Correspon resultCorrespon = MockCorresponDao.ACT_UPDATE_CORRESPON.get(0);
        Workflow resultWorkflow = MockWorkflowDao.ACT_UPDATE_WORKFLOW.get(0);

        assertEquals(expCorrespon.toString(), resultCorrespon.toString());
        assertEquals(expWorkflow.toString(), resultWorkflow.toString());
    }

    /**
     * ログインユーザーが、指定されたコレポン文書を否認するテスト.
     * 自身がDiscipilineAdminかつCheckerのパターン.
     * @throws Exception
     */
    @Test
    public void testDenyDiscipilineAdminChecker() throws Exception {
        Date expectedDate = new GregorianCalendar(2009, 4, 1, 1, 1, 1).getTime();
        MockDateUtil.RET_GETNOW = expectedDate;

        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        List<Project> accessibleProjects = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        accessibleProjects.add(p);
        MockAbstractService.ACCESSIBLE_PROJECTS = accessibleProjects;

        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        MockAbstractService.IS_ANY_GROUP_ADMIN = false;
        List<Long> groupAdmin = new ArrayList<Long>();
        groupAdmin.add(5963L);
        MockAbstractService.IS_GROUP_ADMIN = groupAdmin;

        // 引数の作成
        String expectedEmpNo = "80001";
        User user = new User();
        user.setEmpNo(expectedEmpNo);
        MockAbstractService.CURRENT_USER = user;

        List<ProjectUser> mockUser = new ArrayList<ProjectUser>();
        ProjectUser pu = new ProjectUser();
        pu.setUser(user);
        pu.setProjectId("PJ1");
        mockUser.add(pu);
        MockUserDao.RET_FINDPROJECTUSER = mockUser;

        Correspon correspon = createCorrespon();
        List<Workflow> workflowList = createWorkflowDummy();

        workflowList.get(0).setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_CHECK);
        workflowList.get(1).setWorkflowProcessStatus(WorkflowProcessStatus.NONE);
        correspon.setWorkflows(workflowList);
        CorresponGroup cg = new CorresponGroup();
        cg.setId(5963L);
        correspon.setFromCorresponGroup(cg);

        String expectedComment = "Comment**********";
        Workflow workflow = workflowList.get(0);
        workflow.setCommentOn(expectedComment);

        // 検証用のオブジェクト
        Correspon expCorrespon = new Correspon();
        expCorrespon.setId(correspon.getId());
        expCorrespon.setUpdatedBy(user);
        expCorrespon.setWorkflowStatus(WorkflowStatus.DENIED);
        expCorrespon.setVersionNo(correspon.getVersionNo());

        Workflow expWorkflow = new Workflow();
        expWorkflow.setId(workflow.getId());
        expWorkflow.setWorkflowProcessStatus(WorkflowProcessStatus.DENIED);
        expWorkflow.setCommentOn(expectedComment);
        expWorkflow.setFinishedBy(user);
        expWorkflow.setFinishedAt(expectedDate);
        expWorkflow.setUpdatedBy(user);
        expWorkflow.setVersionNo(workflow.getVersionNo());

        // テスト実行
        service.deny(correspon, workflow);

        assertEquals(1, MockWorkflowDao.ACT_UPDATE_WORKFLOW.size());

        Correspon resultCorrespon = MockCorresponDao.ACT_UPDATE_CORRESPON.get(0);
        Workflow resultWorkflow = MockWorkflowDao.ACT_UPDATE_WORKFLOW.get(0);

        assertEquals(expCorrespon.toString(), resultCorrespon.toString());
        assertEquals(expWorkflow.toString(), resultWorkflow.toString());
    }

    /**
     * ログインユーザーが、指定されたコレポン文書を否認するテスト.
     * 自身がDiscipilineAdminかつApproverのパターン.
     * @throws Exception
     */
    @Test
    public void testDenyDiscipilineAdminApprover() throws Exception {
        Date expectedDate = new GregorianCalendar(2009, 4, 1, 1, 1, 1).getTime();
        MockDateUtil.RET_GETNOW = expectedDate;

        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        List<Project> accessibleProjects = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        accessibleProjects.add(p);
        MockAbstractService.ACCESSIBLE_PROJECTS = accessibleProjects;

        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        MockAbstractService.IS_ANY_GROUP_ADMIN = false;
        List<Long> groupAdmin = new ArrayList<Long>();
        groupAdmin.add(5963L);
        MockAbstractService.IS_GROUP_ADMIN = groupAdmin;

        // 引数の作成
        String expectedEmpNo = "80003";
        User user = new User();
        user.setEmpNo(expectedEmpNo);
        MockAbstractService.CURRENT_USER = user;

        List<ProjectUser> mockUser = new ArrayList<ProjectUser>();
        ProjectUser pu = new ProjectUser();
        pu.setUser(user);
        pu.setProjectId("PJ1");
        mockUser.add(pu);
        MockUserDao.RET_FINDPROJECTUSER = mockUser;

        Correspon correspon = createCorrespon();
        List<Workflow> workflowList = createWorkflowDummy();

        // Approverの承認作業状態をRequest_For_Approvalにする
        workflowList.get(2).setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_APPROVAL);
        correspon.setWorkflows(workflowList);
        CorresponGroup cg = new CorresponGroup();
        cg.setId(5963L);
        correspon.setFromCorresponGroup(cg);

        String expectedComment = "Comment**********";
        Workflow workflow = workflowList.get(2);
        workflow.setCommentOn(expectedComment);

        // 検証用のオブジェクト
        Correspon expCorrespon = new Correspon();
        expCorrespon.setId(correspon.getId());
        expCorrespon.setUpdatedBy(user);
        expCorrespon.setWorkflowStatus(WorkflowStatus.DENIED);
        expCorrespon.setVersionNo(correspon.getVersionNo());

        Workflow expWorkflow = new Workflow();
        expWorkflow.setId(workflow.getId());
        expWorkflow.setWorkflowProcessStatus(WorkflowProcessStatus.DENIED);
        expWorkflow.setCommentOn(expectedComment);
        expWorkflow.setFinishedBy(user);
        expWorkflow.setFinishedAt(expectedDate);
        expWorkflow.setUpdatedBy(user);
        expWorkflow.setVersionNo(workflow.getVersionNo());

        // テスト実行
        service.deny(correspon, workflow);

        assertEquals(1, MockWorkflowDao.ACT_UPDATE_WORKFLOW.size());

        Correspon resultCorrespon = MockCorresponDao.ACT_UPDATE_CORRESPON.get(0);
        Workflow resultWorkflow = MockWorkflowDao.ACT_UPDATE_WORKFLOW.get(0);

        assertEquals(expCorrespon.toString(), resultCorrespon.toString());
        assertEquals(expWorkflow.toString(), resultWorkflow.toString());
    }

    /**
     * ログインユーザーが、指定されたコレポン文書を否認するテスト.
     * 指定のコレポン情報のプロジェクトが現在選択中のプロジェクト以外を検証
     * @throws Exception
     */
    @Test
    public void testDenyInvalidProjectDiff() throws Exception {
        Date expectedDate = new GregorianCalendar(2009, 4, 1, 1, 1, 1).getTime();
        MockDateUtil.RET_GETNOW = expectedDate;

        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        List<Project> accessibleProjects = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        accessibleProjects.add(p);
        MockAbstractService.ACCESSIBLE_PROJECTS = accessibleProjects;

        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        MockAbstractService.IS_ANY_GROUP_ADMIN = false;

        // 引数の作成
        String expectedEmpNo = "80003";
        User user = new User();
        user.setEmpNo(expectedEmpNo);
        MockAbstractService.CURRENT_USER = user;

        List<ProjectUser> mockUser = new ArrayList<ProjectUser>();
        ProjectUser pu = new ProjectUser();
        pu.setUser(user);
        pu.setProjectId("PJ1");
        mockUser.add(pu);
        MockUserDao.RET_FINDPROJECTUSER = mockUser;

        Correspon correspon = createCorrespon();

        // 指定のコレポン情報のプロジェクトと現在選択中のコレポン情報に相違を出す
        correspon.setProjectId("PJ2");

        List<Workflow> workflowList = createWorkflowDummy();

        workflowList.get(2).setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_APPROVAL);
        correspon.setWorkflows(workflowList);

        String expectedComment = "Comment**********";
        Workflow workflow = workflowList.get(2);
        workflow.setCommentOn(expectedComment);

        try {
            // テスト実行
            service.deny(correspon, workflow);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_PROJECT_DIFF, actual
                .getMessageCode());
        }
    }

    /**
     * ログインユーザーが、指定されたコレポン文書を否認するテスト.
     * 指定のコレポン情報のプロジェクトが現在選択中のプロジェクト以外かつ
     * 自身がSystemAdminかつ自身がApproverのパターン.
     * @throws Exception
     */
    @Test
    public void testDenySystemAdminProjectDiff() throws Exception {
        Date expectedDate = new GregorianCalendar(2009, 4, 1, 1, 1, 1).getTime();
        MockDateUtil.RET_GETNOW = expectedDate;

        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        List<Project> accessibleProjects = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        accessibleProjects.add(p);
        MockAbstractService.ACCESSIBLE_PROJECTS = accessibleProjects;

        MockAbstractService.IS_SYSTEM_ADMIN = true;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        MockAbstractService.IS_ANY_GROUP_ADMIN = false;

        // 引数の作成
        String expectedEmpNo = "80003";
        User user = new User();
        user.setEmpNo(expectedEmpNo);
        MockAbstractService.CURRENT_USER = user;

        List<ProjectUser> mockUser = new ArrayList<ProjectUser>();
        ProjectUser pu = new ProjectUser();
        pu.setUser(user);
        pu.setProjectId("PJ1");
        mockUser.add(pu);
        MockUserDao.RET_FINDPROJECTUSER = mockUser;

        Correspon correspon = createCorrespon();
        // 指定のコレポン情報のプロジェクトと現在選択中のコレポン情報に相違を出す
        correspon.setProjectId("PJ2");

        List<Workflow> workflowList = createWorkflowDummy();

        workflowList.get(2).setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_APPROVAL);
        correspon.setWorkflows(workflowList);

        String expectedComment = "Comment**********";
        Workflow workflow = workflowList.get(2);
        workflow.setCommentOn(expectedComment);

        // 検証用のオブジェクト
        Correspon expCorrespon = new Correspon();
        expCorrespon.setId(correspon.getId());
        expCorrespon.setUpdatedBy(user);
        expCorrespon.setWorkflowStatus(WorkflowStatus.DENIED);
        expCorrespon.setVersionNo(correspon.getVersionNo());

        Workflow expWorkflow = new Workflow();
        expWorkflow.setId(workflow.getId());
        expWorkflow.setWorkflowProcessStatus(WorkflowProcessStatus.DENIED);
        expWorkflow.setCommentOn(expectedComment);
        expWorkflow.setFinishedBy(user);
        expWorkflow.setFinishedAt(expectedDate);
        expWorkflow.setUpdatedBy(user);
        expWorkflow.setVersionNo(workflow.getVersionNo());

        // テスト実行
        try {
            service.deny(correspon, workflow);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_PROJECT_DIFF, actual
                .getMessageCode());
        }
    }

    /**
     * ログインユーザーが、指定されたコレポン文書を否認するテスト.
     * ログインユーザーが、SystemAdmin,ProjectAdmin,GroupAdmin,Checker,Approver
     * 以外の場合を検証.
     * @throws Exception
     */
    @Test
    public void testDenyInvalidLoginUser() throws Exception {
        Date expectedDate = new GregorianCalendar(2009, 4, 1, 1, 1, 1).getTime();
        MockDateUtil.RET_GETNOW = expectedDate;

        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        List<Project> accessibleProjects = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        accessibleProjects.add(p);
        MockAbstractService.ACCESSIBLE_PROJECTS = accessibleProjects;

        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        MockAbstractService.IS_ANY_GROUP_ADMIN = false;

        // 引数の作成
        String expectedEmpNo = "99999";
        User user = new User();
        user.setEmpNo(expectedEmpNo);
        MockAbstractService.CURRENT_USER = user;

        List<ProjectUser> mockUser = new ArrayList<ProjectUser>();
        ProjectUser pu = new ProjectUser();
        pu.setUser(user);
        pu.setProjectId("PJ1");
        mockUser.add(pu);
        MockUserDao.RET_FINDPROJECTUSER = mockUser;

        Correspon correspon = createCorrespon();

        List<Workflow> workflowList = createWorkflowDummy();

        correspon.setWorkflows(workflowList);

        String expectedComment = "Comment**********";
        Workflow workflow = workflowList.get(2);
        workflow.setCommentOn(expectedComment);

        try {
            // テスト実行
            service.deny(correspon, workflow);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT,
                actual.getMessageCode());
        }
    }

    /**
     * ログインユーザーが、指定されたコレポン文書を否認するテスト.
     * 自身がDiscipilineAdminかつCheckerまたはApproverではない場合を検証.
     * @throws Exception
     */
    @Test
    public void testDenyInvalidAuthorityCheck() throws Exception {
        Date expectedDate = new GregorianCalendar(2009, 4, 1, 1, 1, 1).getTime();
        MockDateUtil.RET_GETNOW = expectedDate;

        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        List<Project> accessibleProjects = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        accessibleProjects.add(p);
        MockAbstractService.ACCESSIBLE_PROJECTS = accessibleProjects;

        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        MockAbstractService.IS_ANY_GROUP_ADMIN = false;
        List<Long> groupAdmin = new ArrayList<Long>();
        groupAdmin.add(5963L);
        MockAbstractService.IS_GROUP_ADMIN = groupAdmin;

        // 引数の作成
        String expectedEmpNo = "99999";
        User user = new User();
        user.setEmpNo(expectedEmpNo);
        MockAbstractService.CURRENT_USER = user;

        List<ProjectUser> mockUser = new ArrayList<ProjectUser>();
        ProjectUser pu = new ProjectUser();
        pu.setUser(user);
        pu.setProjectId("PJ1");
        mockUser.add(pu);
        MockUserDao.RET_FINDPROJECTUSER = mockUser;

        Correspon correspon = createCorrespon();
        CorresponGroup cg = new CorresponGroup();
        cg.setId(5963L);
        correspon.setFromCorresponGroup(cg);

        List<Workflow> workflowList = createWorkflowDummy();

        correspon.setWorkflows(workflowList);

        String expectedComment = "Comment**********";
        Workflow workflow = workflowList.get(2);
        workflow.setCommentOn(expectedComment);
        workflow.setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_APPROVAL);
        try {
            // テスト実行
            service.deny(correspon, workflow);
        } catch (ServiceAbortException actual) {
            actual.printStackTrace();
            fail("例外が発生");
        }
    }

    /**
     * ログインユーザーが、指定されたコレポン文書を否認するテスト.
     * 自身がCheckerかつ承認作業状態が[1：Request for Check]か[4：Under Consideration] 以外の場合を検証.
     * @throws Exception
     */
    @Test
    public void testDenyInvalidCheckerProcessStatus() throws Exception {
        Date expectedDate = new GregorianCalendar(2009, 4, 1, 1, 1, 1).getTime();
        MockDateUtil.RET_GETNOW = expectedDate;

        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        List<Project> accessibleProjects = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        accessibleProjects.add(p);
        MockAbstractService.ACCESSIBLE_PROJECTS = accessibleProjects;

        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        MockAbstractService.IS_ANY_GROUP_ADMIN = false;

        // 引数の作成
        String expectedEmpNo = "80002";
        User user = new User();
        user.setEmpNo(expectedEmpNo);
        MockAbstractService.CURRENT_USER = user;

        List<ProjectUser> mockUser = new ArrayList<ProjectUser>();
        ProjectUser pu = new ProjectUser();
        pu.setUser(user);
        pu.setProjectId("PJ1");
        mockUser.add(pu);
        MockUserDao.RET_FINDPROJECTUSER = mockUser;

        Correspon correspon = createCorrespon();

        List<Workflow> workflowList = createWorkflowDummy();
        // 自身の承認作業状態が[1：Request for Check]か[4：Under Consideration]以外を作成
        workflowList.get(0).setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_CHECK);
        workflowList.get(1).setWorkflowProcessStatus(WorkflowProcessStatus.NONE);

        correspon.setWorkflows(workflowList);

        String expectedComment = "Comment**********";
        Workflow workflow = workflowList.get(1);
        workflow.setCommentOn(expectedComment);

        try {
            // テスト実行
            service.deny(correspon, workflow);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_WORKFLOW_STATUS_INVALID_FOR_CHECKER,
                actual.getMessageCode());
        }
    }

    /**
     * ログインユーザーが、指定されたコレポン文書を否認するテスト.
     * 自身がApproverかつ承認作業状態が[3：Request for Approval]か[4：Under Consideration] 以外の場合を検証.
     * @throws Exception
     */
    @Test
    public void testDenyInvalidApproverProcessStatus() throws Exception {
        Date expectedDate = new GregorianCalendar(2009, 4, 1, 1, 1, 1).getTime();
        MockDateUtil.RET_GETNOW = expectedDate;

        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        List<Project> accessibleProjects = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        accessibleProjects.add(p);
        MockAbstractService.ACCESSIBLE_PROJECTS = accessibleProjects;

        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        MockAbstractService.IS_ANY_GROUP_ADMIN = false;

        // 引数の作成
        String expectedEmpNo = "80003";
        User user = new User();
        user.setEmpNo(expectedEmpNo);
        MockAbstractService.CURRENT_USER = user;

        List<ProjectUser> mockUser = new ArrayList<ProjectUser>();
        ProjectUser pu = new ProjectUser();
        pu.setUser(user);
        pu.setProjectId("PJ1");
        mockUser.add(pu);
        MockUserDao.RET_FINDPROJECTUSER = mockUser;

        Correspon correspon = createCorrespon();

        List<Workflow> workflowList = createWorkflowDummy();
        // 自身の承認作業状態が[1：Request for Check]か[4：Under Consideration]以外を作成
        workflowList.get(0).setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_CHECK);
        workflowList.get(1).setWorkflowProcessStatus(WorkflowProcessStatus.NONE);

        correspon.setWorkflows(workflowList);

        String expectedComment = "Comment**********";
        Workflow workflow = workflowList.get(2);
        workflow.setCommentOn(expectedComment);

        try {
            // テスト実行
            service.deny(correspon, workflow);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_WORKFLOW_STATUS_INVALID_FOR_APPROVER,
                actual.getMessageCode());
        }
    }

    /**
     * ログインユーザーが、指定されたコレポン文書を否認するテスト.
     * 第一引数がnullの場合を検証.
     * @throws Exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testDenyArgumentNullCheck1() throws Exception {
        // テストに必要なデータを作成
        Workflow workflow = new Workflow();
        // テスト実行
        service.deny(null, workflow);
        fail("例外が発生していない");
    }

    /**
     * ログインユーザーが、指定されたコレポン文書を否認するテスト.
     * 第二引数がnullの場合を検証.
     * @throws Exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testDenyArgumentNullCheck2() throws Exception {
        // テストに必要なデータを作成
        Correspon correspon = new Correspon();
        // テスト実行
        service.deny(correspon, null);
        fail("例外が発生していない");
    }

    /**
     * ログインユーザーが、指定されたコレポン文書を否認するテスト.
     * 自身がSystemAdminかつ対象のデータステータスが [1：Request for Check]か[4：Under Consideration]以外のパターン.
     * @throws Exception
     */
    @Test
    public void testDenySystemAdminInvalidProcessStatusChecker() throws Exception {
        Date expectedDate = new GregorianCalendar(2009, 4, 1, 1, 1, 1).getTime();
        MockDateUtil.RET_GETNOW = expectedDate;

        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        List<Project> accessibleProjects = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        accessibleProjects.add(p);
        MockAbstractService.ACCESSIBLE_PROJECTS = accessibleProjects;

        MockAbstractService.IS_SYSTEM_ADMIN = true;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        MockAbstractService.IS_ANY_GROUP_ADMIN = false;

        // 引数の作成
        String expectedEmpNo = "9999";
        User user = new User();
        user.setEmpNo(expectedEmpNo);
        MockAbstractService.CURRENT_USER = user;

        List<ProjectUser> mockUser = new ArrayList<ProjectUser>();
        ProjectUser pu = new ProjectUser();
        pu.setUser(user);
        pu.setProjectId("PJ1");
        mockUser.add(pu);
        MockUserDao.RET_FINDPROJECTUSER = mockUser;

        Correspon correspon = createCorrespon();
        List<Workflow> workflowList = createWorkflowDummy();

        workflowList.get(0).setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_CHECK);
        workflowList.get(1).setWorkflowProcessStatus(WorkflowProcessStatus.NONE);
        correspon.setWorkflows(workflowList);

        String expectedComment = "Comment**********";
        // 対象データがRequest_For_Check,Under_Consideration以外
        Workflow workflow = workflowList.get(1);
        workflow.setCommentOn(expectedComment);

        try {
            // テスト実行
            service.deny(correspon, workflow);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_WORKFLOW_STATUS_INVALID_FOR_CHECKER,
                actual.getMessageCode());
        }
    }

    /**
     * ログインユーザーが、指定されたコレポン文書を否認するテスト.
     * 自身がSystemAdminかつ対象のデータステータスが [3：Request
     * for Approval]か[4：Under Consideration]以外のパターン.
     * @throws Exception
     */
    @Test
    public void testDenySystemAdminInvalidProcessStatusApprover() throws Exception {
        Date expectedDate = new GregorianCalendar(2009, 4, 1, 1, 1, 1).getTime();
        MockDateUtil.RET_GETNOW = expectedDate;

        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        List<Project> accessibleProjects = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        accessibleProjects.add(p);
        MockAbstractService.ACCESSIBLE_PROJECTS = accessibleProjects;

        MockAbstractService.IS_SYSTEM_ADMIN = true;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        MockAbstractService.IS_ANY_GROUP_ADMIN = false;

        // 引数の作成
        String expectedEmpNo = "9999";
        User user = new User();
        user.setEmpNo(expectedEmpNo);
        MockAbstractService.CURRENT_USER = user;

        List<ProjectUser> mockUser = new ArrayList<ProjectUser>();
        ProjectUser pu = new ProjectUser();
        pu.setUser(user);
        pu.setProjectId("PJ1");
        mockUser.add(pu);
        MockUserDao.RET_FINDPROJECTUSER = mockUser;

        Correspon correspon = createCorrespon();
        List<Workflow> workflowList = createWorkflowDummy();

        workflowList.get(1).setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_CHECK);
        correspon.setWorkflows(workflowList);

        String expectedComment = "Comment**********";
        // 対象データがRequest_For_Approve,Under_Consideration以外
        Workflow workflow = workflowList.get(2);
        workflow.setCommentOn(expectedComment);

        try {
            // テスト実行
            service.deny(correspon, workflow);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_WORKFLOW_STATUS_INVALID_FOR_APPROVER,
                actual.getMessageCode());
        }
    }

    /**
     * ログインユーザーが、指定されたコレポン文書を否認するテスト.
     * 自身がProjectAdminかつ対象のデータステータスが [3：Request for Check]か[4：Under Consideration]以外のパターン.
     * @throws Exception
     */
    @Test
    public void testDenyProjectAdminInvalidProcessStatusChecker() throws Exception {
        Date expectedDate = new GregorianCalendar(2009, 4, 1, 1, 1, 1).getTime();
        MockDateUtil.RET_GETNOW = expectedDate;

        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        List<Project> accessibleProjects = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        accessibleProjects.add(p);
        MockAbstractService.ACCESSIBLE_PROJECTS = accessibleProjects;

        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN = true;
        MockAbstractService.IS_ANY_GROUP_ADMIN = false;

        // 引数の作成
        String expectedEmpNo = "9999";
        User user = new User();
        user.setEmpNo(expectedEmpNo);
        MockAbstractService.CURRENT_USER = user;

        List<ProjectUser> mockUser = new ArrayList<ProjectUser>();
        ProjectUser pu = new ProjectUser();
        pu.setUser(user);
        pu.setProjectId("PJ1");
        mockUser.add(pu);
        MockUserDao.RET_FINDPROJECTUSER = mockUser;

        Correspon correspon = createCorrespon();
        List<Workflow> workflowList = createWorkflowDummy();

        workflowList.get(0).setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_CHECK);
        workflowList.get(1).setWorkflowProcessStatus(WorkflowProcessStatus.NONE);
        correspon.setWorkflows(workflowList);

        String expectedComment = "Comment**********";
        // 対象データがRequest_For_Check,Under_Consideration以外
        Workflow workflow = workflowList.get(1);
        workflow.setCommentOn(expectedComment);

        try {
            // テスト実行
            service.deny(correspon, workflow);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_WORKFLOW_STATUS_INVALID_FOR_CHECKER,
                actual.getMessageCode());
        }
    }

    /**
     * ログインユーザーが、指定されたコレポン文書を否認するテスト.
     * 自身がProjectAdminかつ対象のデータステータスが [3：Request for Approval]か[4：Under Consideration]以外のパターン.
     * @throws Exception
     */
    @Test
    public void testDenyProjectAdminInvalidProcessStatusApprover() throws Exception {
        Date expectedDate = new GregorianCalendar(2009, 4, 1, 1, 1, 1).getTime();
        MockDateUtil.RET_GETNOW = expectedDate;

        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        List<Project> accessibleProjects = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        accessibleProjects.add(p);
        MockAbstractService.ACCESSIBLE_PROJECTS = accessibleProjects;

        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN = true;
        MockAbstractService.IS_ANY_GROUP_ADMIN = false;

        // 引数の作成
        String expectedEmpNo = "9999";
        User user = new User();
        user.setEmpNo(expectedEmpNo);
        MockAbstractService.CURRENT_USER = user;

        List<ProjectUser> mockUser = new ArrayList<ProjectUser>();
        ProjectUser pu = new ProjectUser();
        pu.setUser(user);
        pu.setProjectId("PJ1");
        mockUser.add(pu);
        MockUserDao.RET_FINDPROJECTUSER = mockUser;

        Correspon correspon = createCorrespon();
        List<Workflow> workflowList = createWorkflowDummy();

        workflowList.get(0).setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_CHECK);
        workflowList.get(1).setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_CHECK);
        correspon.setWorkflows(workflowList);

        String expectedComment = "Comment**********";
        // 対象データがRequest_For_Approval,Under_Consideration以外
        Workflow workflow = workflowList.get(2);
        workflow.setCommentOn(expectedComment);

        try {
            // テスト実行
            service.deny(correspon, workflow);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_WORKFLOW_STATUS_INVALID_FOR_APPROVER,
                actual.getMessageCode());
        }
    }

    /**
     * ログインユーザーが、指定されたコレポン文書を否認するテスト.
     * 自身がGroupAdminかつChecker対象のデータステータスが
     * [3：Request for Check]か[4：Under Consideration]以外のパターン.
     * @throws Exception
     */
    @Test
    public void testDenyDiscipilineAdminInvalidProcessStatusChecker() throws Exception {
        Date expectedDate = new GregorianCalendar(2009, 4, 1, 1, 1, 1).getTime();
        MockDateUtil.RET_GETNOW = expectedDate;

        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        List<Project> accessibleProjects = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        accessibleProjects.add(p);
        MockAbstractService.ACCESSIBLE_PROJECTS = accessibleProjects;

        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        MockAbstractService.IS_ANY_GROUP_ADMIN = false;
        List<Long> groupAdmin = new ArrayList<Long>();
        groupAdmin.add(5963L);
        MockAbstractService.IS_GROUP_ADMIN = groupAdmin;

        // 引数の作成
        String expectedEmpNo = "80002";
        User user = new User();
        user.setEmpNo(expectedEmpNo);
        MockAbstractService.CURRENT_USER = user;

        List<ProjectUser> mockUser = new ArrayList<ProjectUser>();
        ProjectUser pu = new ProjectUser();
        pu.setUser(user);
        pu.setProjectId("PJ1");
        mockUser.add(pu);
        MockUserDao.RET_FINDPROJECTUSER = mockUser;

        Correspon correspon = createCorrespon();
        List<Workflow> workflowList = createWorkflowDummy();

        workflowList.get(0).setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_CHECK);
        workflowList.get(1).setWorkflowProcessStatus(WorkflowProcessStatus.NONE);
        correspon.setWorkflows(workflowList);
        CorresponGroup cg = new CorresponGroup();
        cg.setId(5963L);
        correspon.setFromCorresponGroup(cg);

        String expectedComment = "Comment**********";
        // 対象データがRequest_For_Check,Under_Consideration以外
        Workflow workflow = workflowList.get(1);
        workflow.setCommentOn(expectedComment);

        try {
            // テスト実行
            service.deny(correspon, workflow);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_WORKFLOW_STATUS_INVALID_FOR_CHECKER,
                actual.getMessageCode());
        }
    }

    /**
     * ログインユーザーが、指定されたコレポン文書を否認するテスト.
     * 自身がGroupAdminかつChecker対象のデータステータスが
     * [3：Request for Approval]か[4：Under Consideration]以外のパターン.
     * @throws Exception
     */
    @Test
    public void testDenyDiscipilineAdminInvalidProcessStatusApproval() throws Exception {
        Date expectedDate = new GregorianCalendar(2009, 4, 1, 1, 1, 1).getTime();
        MockDateUtil.RET_GETNOW = expectedDate;

        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        List<Project> accessibleProjects = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        accessibleProjects.add(p);
        MockAbstractService.ACCESSIBLE_PROJECTS = accessibleProjects;

        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        MockAbstractService.IS_ANY_GROUP_ADMIN = false;
        List<Long> groupAdmin = new ArrayList<Long>();
        groupAdmin.add(5963L);
        MockAbstractService.IS_GROUP_ADMIN = groupAdmin;

        // 引数の作成
        String expectedEmpNo = "80003";
        User user = new User();
        user.setEmpNo(expectedEmpNo);
        MockAbstractService.CURRENT_USER = user;

        List<ProjectUser> mockUser = new ArrayList<ProjectUser>();
        ProjectUser pu = new ProjectUser();
        pu.setUser(user);
        pu.setProjectId("PJ1");
        mockUser.add(pu);
        MockUserDao.RET_FINDPROJECTUSER = mockUser;

        Correspon correspon = createCorrespon();
        List<Workflow> workflowList = createWorkflowDummy();
        CorresponGroup cg = new CorresponGroup();
        cg.setId(5963L);
        correspon.setFromCorresponGroup(cg);

        workflowList.get(0).setWorkflowProcessStatus(WorkflowProcessStatus.CHECKED);
        workflowList.get(1).setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_CHECK);
        correspon.setWorkflows(workflowList);

        String expectedComment = "Comment**********";
        // 対象データがRequest_For_Approval,Under_Consideration以外
        Workflow workflow = workflowList.get(2);
        workflow.setCommentOn(expectedComment);

        try {
            // テスト実行
            service.deny(correspon, workflow);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_WORKFLOW_STATUS_INVALID_FOR_APPROVER,
                actual.getMessageCode());
        }
    }

    /**
     * 承認フローテンプレートを設定する処理を検証.
     * @throws Exception
     */
    @Test
    public void testApply() throws Exception {
        List<WorkflowTemplate> workflowTemplate = createWorkflowTemplate();
        // 保持用
        List<WorkflowTemplate> workflowTemplateKeep = createWorkflowTemplate();
        MockWorkflowTemplateDao.RET_FIND_BY_WORKFLOW_TEMPLATE_USER_ID = workflowTemplate;

        MockWorkflowTemplateUserDao.RET_FIND_BY_ID.put("1", new WorkflowTemplateUser());
        // テスト実行
        List<WorkflowTemplate> actual = service.apply(1L);

        assertEquals(workflowTemplateKeep.toString(), actual.toString());
    }

    /**
     * 承認フローテンプレートを設定する処理を検証.
     * 指定のテンプレートが存在しない
     * @throws Exception
     */
    @Test
    public void testApplyNotExistWorkflowTemplateUser() throws Exception {
        List<WorkflowTemplate> workflowTemplate = createWorkflowTemplate();
        MockWorkflowTemplateDao.RET_FIND_BY_WORKFLOW_TEMPLATE_USER_ID = workflowTemplate;

        MockWorkflowTemplateUserDao.RET_FIND_BY_ID.put("1", null);

        try {
            // テスト実行
            service.apply(1L);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_TEMPLATE_NOT_EXIST, actual.getMessageCode());
        }
    }

    /**
     * 承認フローテンプレートを設定する処理を検証.
     * 引数がnull
     * @throws Exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testApplyArgumentNull() throws Exception {
        // テスト実行
        service.apply(null);
    }

    /**
     * 承認フローテンプレートを削除する処理を検証.
     * @throws Exception
     */
    @Test
    public void testDeleteTemplate() throws Exception {
        WorkflowTemplateUser templateUser = new WorkflowTemplateUser();
        templateUser.setId(1L);
        templateUser.setProjectId("PJ1");

        User u = new User();
        u.setEmpNo("ZZA05");
        u.setNameE("Keiichi Ogiwara");

        templateUser.setUser(u);
        templateUser.setName("TEST");
        templateUser.setCreatedBy(user);
        templateUser.setUpdatedBy(user);
        templateUser.setVersionNo(1L);
        templateUser.setDeleteNo(0L);

        MockWorkflowTemplateUserDao.RET_FIND_BY_ID.put("1", templateUser);
        MockWorkflowTemplateDao.RET_DELETE_BY_WORKFLOW_TEMPLATE_USER_ID = 5;
        MockWorkflowTemplateUserDao.RET_DELETE = 1;

        // テスト実行
        service.deleteTemplate(1L);

        WorkflowTemplate actualTemplate = MockWorkflowTemplateDao.CRT_DELETE_BY_WORKFLOW_TEMPLATE_USER_ID;
        WorkflowTemplateUser actualTemplateUser = MockWorkflowTemplateUserDao.ACT_DELETE_WORKFLOW_TEMPLATE_USER;

        assertEquals(templateUser.getId(), actualTemplateUser.getId());
        assertNull(actualTemplateUser.getProjectId());
        assertNull(actualTemplateUser.getUser());
        assertNull(actualTemplateUser.getCreatedBy());
        assertNull(actualTemplateUser.getCreatedAt());
        assertEquals(user.toString(), actualTemplateUser.getUpdatedBy().toString());
        assertNull(actualTemplateUser.getUpdatedAt());
        assertEquals(templateUser.getVersionNo(), actualTemplateUser.getVersionNo());
        assertNull(actualTemplateUser.getDeleteNo());

        assertEquals(templateUser.getId(), actualTemplate.getWorkflowTemplateUserId());
        assertNull(actualTemplate.getId());
        assertNull(actualTemplate.getUser());
        assertNull(actualTemplate.getWorkflowType());
        assertNull(actualTemplate.getWorkflowNo());
        assertNull(actualTemplate.getCreatedBy());
        assertNull(actualTemplate.getCreatedAt());
        assertEquals(user.toString(), actualTemplate.getUpdatedBy().toString());
        assertNull(actualTemplate.getUpdatedAt());
        assertNull(actualTemplate.getDeleteNo());
    }

    /**
     * 承認フローテンプレートを削除する処理を検証.
     * 指定のテンプレートが存在しない
     * @throws Exception
     */
    @Test
    public void testDeleteTemplateNotExistWorkflowTemplateUser() throws Exception {
        // 指定のテンプレートが存在しない
        MockWorkflowTemplateUserDao.RET_FIND_BY_ID.put("1", null);

        try {
            // テスト実行
            service.deleteTemplate(1L);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_TEMPLATE_NOT_EXIST, actual.getMessageCode());
        }
    }

    /**
     * 承認フローテンプレートを削除する処理を検証.
     * 引数がnull
     * @throws Exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testDeleteTemplateArgumentNull() throws Exception {
        service.deleteTemplate(null);
    }

    /**
     * 承認フローテンプレートを保存する処理を検証.
     * @throws Exception
     */
    @Test
    public void testSaveTemplate() throws Exception {
        List<Workflow> workflowList = createWorkflow();
        Correspon correspon = createCorrespon();
        String name = "TestTemplate";

        List<ProjectUser> projectUserList = new ArrayList<ProjectUser>();
        ProjectUser projectUser = new ProjectUser();

        User u = new User();
        u.setEmpNo("ZZA02");
        u.setNameE("Tetsuo Aoki");
        projectUser.setUser(u);
        projectUserList.add(projectUser);

        projectUser = new ProjectUser();
        u = new User();
        u.setEmpNo("ZZA03");
        u.setNameE("Atsushi Ishida");
        projectUser.setUser(u);
        projectUserList.add(projectUser);

        projectUser = new ProjectUser();
        u = new User();
        u.setEmpNo("ZZA04");
        u.setNameE("Tomoko Okada");
        projectUser.setUser(u);
        projectUserList.add(projectUser);

        MockUserDao.RET_FINDPROJECTUSER = projectUserList;
        MockWorkflowTemplateUserDao.RET_COUNT_TEMPLATE_USER_CHECK = 0;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        MockWorkflowTemplateUserDao.RET_CREATE = 1L;
        // テスト実行
        service.saveTemplate(name, workflowList, correspon);

        List<WorkflowTemplate> actualWorkflowTemplate = MockWorkflowTemplateDao.ACT_WORKFLOW_TEMPLATE;
        WorkflowTemplateUser actualWorkflowTemplateUser = MockWorkflowTemplateUserDao.ACT_WORKFLOW_TEMPLATE_USER;

        assertNull(actualWorkflowTemplateUser.getId());
        assertEquals("PJ1", actualWorkflowTemplateUser.getProjectId());
        assertEquals(user.toString(), actualWorkflowTemplateUser.getUser().toString());
        assertEquals(name, actualWorkflowTemplateUser.getName());
        assertEquals(user.toString(), actualWorkflowTemplateUser.getCreatedBy().toString());
        assertNull(actualWorkflowTemplateUser.getCreatedAt());
        assertEquals(user.toString(), actualWorkflowTemplateUser.getUpdatedBy().toString());
        assertNull(actualWorkflowTemplateUser.getUpdatedAt());
        assertNull(actualWorkflowTemplateUser.getVersionNo());
        assertNull(actualWorkflowTemplateUser.getDeleteNo());

        assertNull(actualWorkflowTemplate.get(0).getId());
        assertEquals(String.valueOf(1L), actualWorkflowTemplate.get(0).getWorkflowTemplateUserId().toString());
        assertEquals(workflowList.get(0).getUser().toString(), actualWorkflowTemplate.get(0).getUser().toString());
        assertEquals(workflowList.get(0).getWorkflowType(), actualWorkflowTemplate.get(0).getWorkflowType());
        assertEquals(workflowList.get(0).getWorkflowNo(), actualWorkflowTemplate.get(0).getWorkflowNo());
        assertEquals(user.toString(), actualWorkflowTemplate.get(0).getCreatedBy().toString());
        assertNull(actualWorkflowTemplate.get(0).getCreatedAt());
        assertEquals(user.toString(), actualWorkflowTemplate.get(0).getUpdatedBy().toString());
        assertNull(actualWorkflowTemplate.get(0).getUpdatedAt());
        assertNull(actualWorkflowTemplate.get(0).getDeleteNo());

        assertNull(actualWorkflowTemplate.get(1).getId());
        assertEquals(String.valueOf(1L), actualWorkflowTemplate.get(1).getWorkflowTemplateUserId().toString());
        assertEquals(workflowList.get(1).getUser().toString(), actualWorkflowTemplate.get(1).getUser().toString());
        assertEquals(workflowList.get(1).getWorkflowType(), actualWorkflowTemplate.get(1).getWorkflowType());
        assertEquals(workflowList.get(1).getWorkflowNo(), actualWorkflowTemplate.get(1).getWorkflowNo());
        assertEquals(user.toString(), actualWorkflowTemplate.get(1).getCreatedBy().toString());
        assertNull(actualWorkflowTemplate.get(1).getCreatedAt());
        assertEquals(user.toString(), actualWorkflowTemplate.get(1).getUpdatedBy().toString());
        assertNull(actualWorkflowTemplate.get(1).getUpdatedAt());
        assertNull(actualWorkflowTemplate.get(1).getDeleteNo());

        assertNull(actualWorkflowTemplate.get(2).getId());
        assertEquals(String.valueOf(1L), actualWorkflowTemplate.get(2).getWorkflowTemplateUserId().toString());
        assertEquals(workflowList.get(2).getUser().toString(), actualWorkflowTemplate.get(2).getUser().toString());
        assertEquals(workflowList.get(2).getWorkflowType(), actualWorkflowTemplate.get(2).getWorkflowType());
        assertEquals(workflowList.get(2).getWorkflowNo(), actualWorkflowTemplate.get(2).getWorkflowNo());
        assertEquals(user.toString(), actualWorkflowTemplate.get(2).getCreatedBy().toString());
        assertNull(actualWorkflowTemplate.get(2).getCreatedAt());
        assertEquals(user.toString(), actualWorkflowTemplate.get(2).getUpdatedBy().toString());
        assertNull(actualWorkflowTemplate.get(2).getUpdatedAt());
        assertNull(actualWorkflowTemplate.get(2).getDeleteNo());
    }

    /**
     * 承認フローテンプレートを保存する処理を検証.
     * 指定可能なCheckerの数を越えた場合(現在Max10)
     * @throws Exception
     */
    @Test
    public void testSaveTemplateCheckerOverTheMax() throws Exception {
        List<Workflow> workflowList = createOverTheMaxChecer();

        Correspon correspon = createCorrespon();
        String name = "TestTemplate";

        List<ProjectUser> projectUserList = new ArrayList<ProjectUser>();
        ProjectUser projectUser = new ProjectUser();

        User u = new User();
        u.setEmpNo("ZZA02");
        u.setNameE("Tetsuo Aoki");
        projectUser.setUser(u);
        projectUserList.add(projectUser);

        projectUser = new ProjectUser();
        u = new User();
        u.setEmpNo("ZZA03");
        u.setNameE("Atsushi Ishida");
        projectUser.setUser(u);
        projectUserList.add(projectUser);

        projectUser = new ProjectUser();
        u = new User();
        u.setEmpNo("ZZA04");
        u.setNameE("Tomoko Okada");
        projectUser.setUser(u);
        projectUserList.add(projectUser);

        MockUserDao.RET_FINDPROJECTUSER = projectUserList;
        MockWorkflowTemplateUserDao.RET_COUNT_TEMPLATE_USER_CHECK = 0;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        MockWorkflowTemplateUserDao.RET_CREATE = 1L;

        try {
            // テスト実行
            service.saveTemplate(name, workflowList, correspon);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.EXCEED_MAXIMUM_NUMBER_OF_CHECKERS, actual.getMessageCode());
        }
    }

    /**
     * 承認フローテンプレートを保存する処理を検証.
     * Approverの数が2人以上
     * @throws Exception
     */
    @Test
    public void testSaveTemplateApproverOverTheMax() throws Exception {
        List<Workflow> workflowList = createWorkflowDummyStatusNone();
        // Approverが2人以上
        Workflow workflow = new Workflow();
        workflow.setId(new Long(30));
        workflow.setWorkflowNo(new Long(3));

        User appvover = new User();
        appvover.setEmpNo("80003");
        appvover.setNameE("Approver User");
        workflow.setUser(appvover);
        workflow.setCreatedBy(appvover);
        workflow.setUpdatedBy(appvover);

        workflow.setWorkflowType(WorkflowType.APPROVER);
        workflow.setWorkflowProcessStatus(WorkflowProcessStatus.NONE);
        workflowList.add(workflow);

        Correspon correspon = createCorrespon();
        String name = "TestTemplate";

        List<ProjectUser> projectUserList = new ArrayList<ProjectUser>();
        ProjectUser projectUser = new ProjectUser();

        User u = new User();
        u.setEmpNo("ZZA02");
        u.setNameE("Tetsuo Aoki");
        projectUser.setUser(u);
        projectUserList.add(projectUser);

        projectUser = new ProjectUser();
        u = new User();
        u.setEmpNo("ZZA03");
        u.setNameE("Atsushi Ishida");
        projectUser.setUser(u);
        projectUserList.add(projectUser);

        projectUser = new ProjectUser();
        u = new User();
        u.setEmpNo("ZZA04");
        u.setNameE("Tomoko Okada");
        projectUser.setUser(u);
        projectUserList.add(projectUser);

        MockUserDao.RET_FINDPROJECTUSER = projectUserList;
        MockWorkflowTemplateUserDao.RET_COUNT_TEMPLATE_USER_CHECK = 0;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        MockWorkflowTemplateUserDao.RET_CREATE = 1L;

        try {
            // テスト実行
            service.saveTemplate(name, workflowList, correspon);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.EXCEED_MAXIMUM_NUMBER_OF_APPROVERS, actual.getMessageCode());
            assertEquals(1, actual.getMessageVars().length);
            assertEquals(1, actual.getMessageVars()[0]);
        }
    }

    /**
     * 承認フローテンプレートを保存する処理を検証.
     * Approver下のCheckerが存在する
     * @throws Exception
     */
    @Test
    public void testSaveTemplateApproverUnderTheChecker() throws Exception {
        List<Workflow> workflowList = createWorkflowDummyStatusNone();
        // Approver下にCheckerが存在
        Workflow workflow = new Workflow();
        workflow.setId(new Long(30));
        workflow.setWorkflowNo(new Long(3));

        User checker = new User();
        checker.setEmpNo("80003");
        checker.setNameE("Checker User");
        workflow.setUser(checker);
        workflow.setCreatedBy(checker);
        workflow.setUpdatedBy(checker);

        workflow.setWorkflowType(WorkflowType.CHECKER);
        workflow.setWorkflowProcessStatus(WorkflowProcessStatus.NONE);
        workflowList.add(workflow);

        Correspon correspon = createCorrespon();
        String name = "TestTemplate";

        List<ProjectUser> projectUserList = new ArrayList<ProjectUser>();
        ProjectUser projectUser = new ProjectUser();

        User u = new User();
        u.setEmpNo("ZZA02");
        u.setNameE("Tetsuo Aoki");
        projectUser.setUser(u);
        projectUserList.add(projectUser);

        projectUser = new ProjectUser();
        u = new User();
        u.setEmpNo("ZZA03");
        u.setNameE("Atsushi Ishida");
        projectUser.setUser(u);
        projectUserList.add(projectUser);

        projectUser = new ProjectUser();
        u = new User();
        u.setEmpNo("ZZA04");
        u.setNameE("Tomoko Okada");
        projectUser.setUser(u);
        projectUserList.add(projectUser);

        MockUserDao.RET_FINDPROJECTUSER = projectUserList;
        MockWorkflowTemplateUserDao.RET_COUNT_TEMPLATE_USER_CHECK = 0;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        MockWorkflowTemplateUserDao.RET_CREATE = 1L;

        try {
            // テスト実行
            service.saveTemplate(name, workflowList, correspon);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_WORKFLOW_SEQUENCE_INVALID, actual.getMessageCode());
        }
    }

    /**
     * 承認フローテンプレートを保存する処理を検証.
     * Checkerが重複
     * @throws Exception
     */
    @Test
    public void testSaveTemplateAgainChecker() throws Exception {
        List<Workflow> workflowList = createWorkflowDummyStatusNone();
        // Checkerが重複
        Workflow workflow = new Workflow();
        workflow.setId(new Long(99));
        workflow.setWorkflowNo(new Long(1));

        User checker = new User();
        checker.setEmpNo("80001");
        checker.setNameE("Checker1 User");
        workflow.setUser(checker);
        workflow.setCreatedBy(checker);
        workflow.setUpdatedBy(checker);

        workflow.setWorkflowType(WorkflowType.CHECKER);
        workflow.setWorkflowProcessStatus(WorkflowProcessStatus.NONE);
        workflowList.add(0,workflow);

        Correspon correspon = createCorrespon();
        String name = "TestTemplate";

        List<ProjectUser> projectUserList = new ArrayList<ProjectUser>();
        ProjectUser projectUser = new ProjectUser();

        User u = new User();
        u.setEmpNo("ZZA02");
        u.setNameE("Tetsuo Aoki");
        projectUser.setUser(u);
        projectUserList.add(projectUser);

        projectUser = new ProjectUser();
        u = new User();
        u.setEmpNo("ZZA03");
        u.setNameE("Atsushi Ishida");
        projectUser.setUser(u);
        projectUserList.add(projectUser);

        projectUser = new ProjectUser();
        u = new User();
        u.setEmpNo("ZZA04");
        u.setNameE("Tomoko Okada");
        projectUser.setUser(u);
        projectUserList.add(projectUser);

        MockUserDao.RET_FINDPROJECTUSER = projectUserList;
        MockWorkflowTemplateUserDao.RET_COUNT_TEMPLATE_USER_CHECK = 0;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        MockWorkflowTemplateUserDao.RET_CREATE = 1L;

        try {
            // テスト実行
            service.saveTemplate(name, workflowList, correspon);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.DUPLICATED_CHECKER, actual.getMessageCode());
            assertEquals(1, actual.getMessageVars().length);
            assertEquals("80001", actual.getMessageVars()[0]);
        }
    }

    /**
     * 承認フローテンプレートを保存する処理を検証.
     * CheckerとApproverが重複
     * @throws Exception
     */
    @Test
    public void testSaveTemplateCheckeExistApprover() throws Exception {
        List<Workflow> workflowList = createWorkflowDummyStatusNone();
        // CheckerとApproverが重複
        Workflow workflow = new Workflow();
        workflow.setId(new Long(30));
        workflow.setWorkflowNo(new Long(3));
        User checker = new User();
        checker.setEmpNo("80003");
        checker.setNameE("Approver User");
        workflow.setUser(checker);
        workflow.setCreatedBy(checker);
        workflow.setUpdatedBy(checker);
        workflow.setWorkflowType(WorkflowType.CHECKER);
        workflow.setWorkflowProcessStatus(WorkflowProcessStatus.NONE);
        workflowList.add(0,workflow);

        Correspon correspon = createCorrespon();
        String name = "TestTemplate";

        List<ProjectUser> projectUserList = new ArrayList<ProjectUser>();
        ProjectUser projectUser = new ProjectUser();

        User u = new User();
        u.setEmpNo("ZZA02");
        u.setNameE("Tetsuo Aoki");
        projectUser.setUser(u);
        projectUserList.add(projectUser);

        projectUser = new ProjectUser();
        u = new User();
        u.setEmpNo("ZZA03");
        u.setNameE("Atsushi Ishida");
        projectUser.setUser(u);
        projectUserList.add(projectUser);

        projectUser = new ProjectUser();
        u = new User();
        u.setEmpNo("ZZA04");
        u.setNameE("Tomoko Okada");
        projectUser.setUser(u);
        projectUserList.add(projectUser);

        MockUserDao.RET_FINDPROJECTUSER = projectUserList;
        MockWorkflowTemplateUserDao.RET_COUNT_TEMPLATE_USER_CHECK = 0;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        MockWorkflowTemplateUserDao.RET_CREATE = 1L;

        try {
            // テスト実行
            service.saveTemplate(name, workflowList, correspon);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.DUPLICATED_CHECKER_APPROVER, actual.getMessageCode());
            assertEquals(1, actual.getMessageVars().length);
            assertEquals("80003", actual.getMessageVars()[0]);
        }
    }

    /**
     * 承認フローテンプレートを保存する処理を検証.
     * Approver、Checkerが、当該コレポン文書に指定されたプロジェクトに参加しているユーザー以外
     * @throws Exception
     */
    @Test
    public void testSaveTemplateMemberProjectDiff() throws Exception {
        List<Workflow> workflowList = createWorkflowDummyStatusNone();

        Correspon correspon = createCorrespon();
        String name = "TestTemplate";

        // プロジェクトが違う
        List<ProjectUser> projectUserList = new ArrayList<ProjectUser>();
        ProjectUser projectUser = new ProjectUser();

        User u = new User();
        u.setEmpNo("ZZA02");
        u.setNameE("Tetsuo Aoki");
        projectUser.setUser(u);
        projectUserList.add(projectUser);

        projectUser = new ProjectUser();
        u = new User();
        u.setEmpNo("ZZA03");
        u.setNameE("Atsushi Ishida");
        projectUser.setUser(u);
        projectUserList.add(projectUser);

        projectUser = new ProjectUser();
        u = new User();
        u.setEmpNo("ZZA04");
        u.setNameE("Tomoko Okada");
        projectUser.setUser(u);
        projectUserList.add(projectUser);

        MockUserDao.RET_FINDPROJECTUSER = projectUserList;
        MockWorkflowTemplateUserDao.RET_COUNT_TEMPLATE_USER_CHECK = 0;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        MockWorkflowTemplateUserDao.RET_CREATE = 1L;

        try {
            // テスト実行
            service.saveTemplate(name, workflowList, correspon);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.INVALID_USER, actual.getMessageCode());
        }
    }

    /**
     * 承認フローテンプレートを保存する処理を検証.
     * プロジェクト内で同ユーザーが作成したテンプレートのテンプレート名が重複
     * @throws Exception
     */
    @Test
    public void testSaveTemplateExistTemplateName() throws Exception {
        List<Workflow> workflowList = createWorkflow();

        Correspon correspon = createCorrespon();
        String name = "TestTemplate";

        List<ProjectUser> projectUserList = new ArrayList<ProjectUser>();
        ProjectUser projectUser = new ProjectUser();

        User u = new User();
        u.setEmpNo("ZZA02");
        u.setNameE("Tetsuo Aoki");
        projectUser.setUser(u);
        projectUserList.add(projectUser);

        projectUser = new ProjectUser();
        u = new User();
        u.setEmpNo("ZZA03");
        u.setNameE("Atsushi Ishida");
        projectUser.setUser(u);
        projectUserList.add(projectUser);

        projectUser = new ProjectUser();
        u = new User();
        u.setEmpNo("ZZA04");
        u.setNameE("Tomoko Okada");
        projectUser.setUser(u);
        projectUserList.add(projectUser);

        MockUserDao.RET_FINDPROJECTUSER = projectUserList;
        // テンプレート名が重複
        MockWorkflowTemplateUserDao.RET_COUNT_TEMPLATE_USER_CHECK = 1;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        MockWorkflowTemplateUserDao.RET_CREATE = 1L;

        try {
            // テスト実行
            service.saveTemplate(name, workflowList, correspon);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_TEMPLATE_NAME_ALREADY_EXISTS, actual.getMessageCode());
            assertEquals(1, actual.getMessageVars().length);
            assertEquals("TestTemplate", actual.getMessageVars()[0]);
        }
    }

    /**
     * 承認フローテンプレートを保存する処理を検証.
     * 第一引数がnull
     * @throws Exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSaveTemplateArgumentNull1() throws Exception {
        service.saveTemplate(null, new ArrayList<Workflow>(), new Correspon());
    }

    /**
     * 承認フローテンプレートを保存する処理を検証.
     * 第二引数がnull
     * @throws Exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSaveTemplateArgumentNull2() throws Exception {
        service.saveTemplate("", null, new Correspon());
    }

    /**
     * 承認フローテンプレートを保存する処理を検証.
     * 第三引数がnull
     * @throws Exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSaveTemplateArgumentNull3() throws Exception {
        service.saveTemplate("", new ArrayList<Workflow>(), null);
    }

    private List<WorkflowTemplate> createWorkflowTemplate() {
        List<WorkflowTemplate> workflowTemplateList = new ArrayList<WorkflowTemplate>();
        WorkflowTemplate template = new WorkflowTemplate();

        User u = new User();
        u.setEmpNo("ZZA02");
        u.setNameE("Tetsuo Aoki");

        template.setId(1L);
        template.setWorkflowTemplateUserId(1L);
        template.setUser(u);
        template.setWorkflowType(WorkflowType.CHECKER);
        template.setWorkflowNo(1L);
        template.setCreatedBy(user);
        template.setUpdatedBy(user);
        template.setDeleteNo(0L);
        workflowTemplateList.add(template);

        template = new WorkflowTemplate();
        u = new User();
        u.setEmpNo("ZZA03");
        u.setNameE("Atsushi Ishida");

        template.setId(2L);
        template.setWorkflowTemplateUserId(1L);
        template.setUser(u);
        template.setWorkflowType(WorkflowType.CHECKER);
        template.setWorkflowNo(2L);
        template.setCreatedBy(user);
        template.setUpdatedBy(user);
        template.setDeleteNo(0L);
        workflowTemplateList.add(template);

        template = new WorkflowTemplate();
        u = new User();
        u.setEmpNo("ZZA04");
        u.setNameE("Tomoko Okada");

        template.setId(3L);
        template.setWorkflowTemplateUserId(1L);
        template.setUser(u);
        template.setWorkflowType(WorkflowType.APPROVER);
        template.setWorkflowNo(3L);
        template.setCreatedBy(user);
        template.setUpdatedBy(user);
        template.setDeleteNo(0L);
        workflowTemplateList.add(template);

        return workflowTemplateList;
    }

    public static class MockAbstractDao<T extends Entity> {
        static Long RET_CREATE;
        static List<Workflow> ACT_CREATE = new ArrayList<Workflow>();
        static Integer RET_UPDATE;
        static List<Correspon> ACT_UPDATE_CORRESPON = new ArrayList<Correspon>();
        static List<Workflow> ACT_UPDATE_WORKFLOW = new ArrayList<Workflow>();
        static WorkflowTemplateUser ACT_DELETE_WORKFLOW_TEMPLATE_USER;
        static Integer RET_DELETE;
        static Map<String, Object> RET_FIND_BY_ID = new HashMap<String, Object>();
        static int COUNT = 0;
        static WorkflowTemplateUser ACT_WORKFLOW_TEMPLATE_USER;
        static List<WorkflowTemplate> ACT_WORKFLOW_TEMPLATE = new ArrayList<WorkflowTemplate>();

        public MockAbstractDao() {

        }

        public MockAbstractDao(String namespace) {

        }

        public Long create(T entity) throws KeyDuplicateException {
            if (entity instanceof Workflow) {
                ACT_CREATE.add((Workflow) entity);
            } else if(entity instanceof WorkflowTemplateUser) {
                ACT_WORKFLOW_TEMPLATE_USER = (WorkflowTemplateUser)entity;
            } else if (entity instanceof WorkflowTemplate) {
                ACT_WORKFLOW_TEMPLATE.add((WorkflowTemplate)entity);
            }
            return RET_CREATE;
        }

        public Integer update(T entity) throws KeyDuplicateException, StaleRecordException {
            if (entity instanceof Correspon) {
                ACT_UPDATE_CORRESPON.add((Correspon) entity);
            } else if (entity instanceof Workflow) {
                ACT_UPDATE_WORKFLOW.add((Workflow) entity);
            }

            return RET_UPDATE;
        }

        public Integer delete(T entity) throws KeyDuplicateException, StaleRecordException {
            if (entity instanceof WorkflowTemplateUser) {
                ACT_DELETE_WORKFLOW_TEMPLATE_USER = (WorkflowTemplateUser)entity;
            }
            return RET_DELETE;
        }

        @SuppressWarnings("unchecked")
        public T findById(Long id) throws RecordNotFoundException {
            COUNT++;
            if (RET_FIND_BY_ID.get(String.valueOf(COUNT)) == null) {
                throw new RecordNotFoundException();
            }
            return (T) RET_FIND_BY_ID.get(String.valueOf(COUNT));
        }
    }

    @SuppressWarnings("unchecked")
    public static class MockCorresponDao extends MockAbstractDao {
        public MockCorresponDao() {
            super("mock");
        }
    }

    @SuppressWarnings("unchecked")
    public static class MockAddressCorresponGroupDao extends MockAbstractDao {
        static List<AddressCorresponGroup> RET_FIND_BY_CORRESPON_ID;

        public MockAddressCorresponGroupDao() {
            super("mock");
        }

        public List<AddressCorresponGroup> findByCorresponId(Long corresponId) {
            return RET_FIND_BY_CORRESPON_ID;
        }
    }

    @SuppressWarnings("unchecked")
    public static class MockWorkflowDao extends MockAbstractDao {
        static List<Workflow> RET_FIND_BY_CORRESPON_ID;
        static Integer RET_DELETE_BY_CORRESPON_ID_WORKFLOW_NO;
        static Integer RET_DELETE_BY_CORRESPON_ID;

        static List<Workflow> ACT_UPDATE_WORKFLOW_PROCESS_STATUS_BY_ID = new ArrayList<Workflow>();
        static List<Workflow> ACT_UPDATE_WORKFLOW_PROCESS_STATUSES_BY_CORRESPON_ID_AND_WORKFLOW_TYPE = new ArrayList<Workflow>();

        public MockWorkflowDao() {
            super("mock");
        }

        public List<Workflow> findByCorresponId(Long corresponId) {
            return RET_FIND_BY_CORRESPON_ID;
        }

        public Integer deleteByCorresponIdWorkflowNo(Long corresponId, Long workflowNo,
            User updateUser) throws KeyDuplicateException, StaleRecordException {
            return RET_DELETE_BY_CORRESPON_ID_WORKFLOW_NO;
        }

        public Integer deleteByCorresponId(Long corresponId, User updateUser) throws KeyDuplicateException, StaleRecordException {
            return RET_DELETE_BY_CORRESPON_ID;
        }

        public Integer updateWorkflowProcessStatusById(
            Workflow workflow, WorkflowProcessStatus currentStatus)
                throws KeyDuplicateException, StaleRecordException {
            // 必須パラメータの検証
            assertNotNull(workflow);
            assertNotNull(workflow.getWorkflowProcessStatus());
            assertNotNull(workflow.getUpdatedBy());
            assertNotNull(workflow.getUpdatedBy().getEmpNo());
            assertNotNull(workflow.getId());
            assertEquals(WorkflowProcessStatus.NONE, currentStatus);

            ACT_UPDATE_WORKFLOW_PROCESS_STATUS_BY_ID.add(workflow);

            return 1;
        }

        /* (non-Javadoc)
         * @see jp.co.opentone.bsol.linkbinder.dao.WorkflowDao#updateWorkflowProcessStatuses(jp.co.opentone.bsol.linkbinder.dto.Workflow, jp.co.opentone.bsol.linkbinder.dto.code.WorkflowProcessStatus)
         */
        public Integer updateWorkflowProcessStatusesByCorresponIdAndWorkflowType(
            Workflow workflow, WorkflowProcessStatus currentStatus)
                throws KeyDuplicateException, StaleRecordException {
            // 必須パラメータの検証
            assertNotNull(workflow);
            assertNotNull(workflow.getWorkflowProcessStatus());
            assertNotNull(workflow.getUpdatedBy());
            assertNotNull(workflow.getUpdatedBy().getEmpNo());
            assertNotNull(workflow.getCorresponId());
            assertNotNull(workflow.getWorkflowType());
            assertEquals(WorkflowProcessStatus.NONE, currentStatus);

            ACT_UPDATE_WORKFLOW_PROCESS_STATUSES_BY_CORRESPON_ID_AND_WORKFLOW_TYPE.add(workflow);

            return 1;
        }
    }

    @SuppressWarnings("unchecked")
    public static class MockUserDao extends MockAbstractDao {
        static List<ProjectUser> RET_FINDPROJECTUSER;

        public MockUserDao() {
            super("mock");
        }

        public List<ProjectUser> findProjectUser(SearchUserCondition condition) {
            return RET_FINDPROJECTUSER;
        }

    }

    public static class MockDateUtil {
        static Date RET_GETNOW;

        public Date getNow() {
            return RET_GETNOW;
        }
    }

    public static class MockCorresponSequenceService {
        static String RET_CORRESPON_NO;

        public String getCorresponNo(Correspon correspon) {
            return RET_CORRESPON_NO;
        }
    }

    public static class MockWorkflowTemplateDao extends MockAbstractDao<Entity> {
        static List<WorkflowTemplate> RET_FIND_BY_WORKFLOW_TEMPLATE_USER_ID;
        static Integer RET_DELETE_BY_WORKFLOW_TEMPLATE_USER_ID;
        static WorkflowTemplate CRT_DELETE_BY_WORKFLOW_TEMPLATE_USER_ID;

        public List<WorkflowTemplate> findByWorkflowTemplateUserId(Long workflowTemplateUserId) {
            return RET_FIND_BY_WORKFLOW_TEMPLATE_USER_ID;
        }

        public Integer deleteByWorkflowTemplateUserId(WorkflowTemplate workflowTemplate)
                throws KeyDuplicateException, StaleRecordException {
            CRT_DELETE_BY_WORKFLOW_TEMPLATE_USER_ID = workflowTemplate;
            return RET_DELETE_BY_WORKFLOW_TEMPLATE_USER_ID;
        }
    }

    public static class MockWorkflowTemplateUserDao extends MockAbstractDao<Entity> {
        static List<WorkflowTemplateUser> RET_FIND;
        static int RET_COUNT_TEMPLATE_USER_CHECK;



        public List<WorkflowTemplateUser> find(SearchWorkflowTemplateUserCondition condition) {
            return RET_FIND;
        }

        public int countTemplateUserCheck(SearchWorkflowTemplateUserCondition condition) {
            return RET_COUNT_TEMPLATE_USER_CHECK;
        }
    }

    private List<Workflow> createOverTheMaxChecer() {
        List<Workflow> wfs = new ArrayList<Workflow>();
        Workflow w = new Workflow();
        User u;

        w.setId(new Long(10));
        w.setWorkflowNo(new Long(1));

        u = new User();
        u.setEmpNo("ZZA04");
        u.setNameE("Tomoko Okada");
        w.setUser(u);
        w.setCreatedBy(u);
        w.setUpdatedBy(u);

        w.setWorkflowType(WorkflowType.CHECKER);
        w.setWorkflowProcessStatus(WorkflowProcessStatus.NONE);
        wfs.add(w);

        w = new Workflow();
        w.setId(new Long(20));
        w.setWorkflowNo(new Long(2));
        u = new User();
        u.setEmpNo("ZZA02");
        u.setNameE("Tetsuo Aoki");
        w.setUser(u);
        w.setCreatedBy(u);
        w.setUpdatedBy(u);

        w.setWorkflowType(WorkflowType.CHECKER);
        w.setWorkflowProcessStatus(WorkflowProcessStatus.NONE);
        wfs.add(w);

        w = new Workflow();
        w.setId(new Long(30));
        w.setWorkflowNo(new Long(3));
        u = new User();
        u.setEmpNo("ZZA05");
        u.setNameE("Keiichi Ogiwara");
        w.setUser(u);
        w.setCreatedBy(u);
        w.setUpdatedBy(u);

        w.setWorkflowType(WorkflowType.CHECKER);
        w.setWorkflowProcessStatus(WorkflowProcessStatus.NONE);
        wfs.add(w);


        w = new Workflow();
        w.setId(new Long(40));
        w.setWorkflowNo(new Long(4));
        u = new User();
        u.setEmpNo("ZZA06");
        u.setNameE("Test Rokurou");
        w.setUser(u);
        w.setCreatedBy(u);
        w.setUpdatedBy(u);

        w.setWorkflowType(WorkflowType.CHECKER);
        w.setWorkflowProcessStatus(WorkflowProcessStatus.NONE);
        wfs.add(w);

        w = new Workflow();
        w.setId(new Long(50));
        w.setWorkflowNo(new Long(5));
        u = new User();
        u.setEmpNo("ZZA07");
        u.setNameE("Test Nanarou");
        w.setUser(u);
        w.setCreatedBy(u);
        w.setUpdatedBy(u);

        w.setWorkflowType(WorkflowType.CHECKER);
        w.setWorkflowProcessStatus(WorkflowProcessStatus.NONE);
        wfs.add(w);

        w = new Workflow();
        w.setId(new Long(60));
        w.setWorkflowNo(new Long(6));
        u = new User();
        u.setEmpNo("ZZA08");
        u.setNameE("Test Hatirou");
        w.setUser(u);
        w.setCreatedBy(u);
        w.setUpdatedBy(u);

        w.setWorkflowType(WorkflowType.CHECKER);
        w.setWorkflowProcessStatus(WorkflowProcessStatus.NONE);
        wfs.add(w);

        w = new Workflow();
        w.setId(new Long(70));
        w.setWorkflowNo(new Long(7));
        u = new User();
        u.setEmpNo("ZZA09");
        u.setNameE("Test Kurou");
        w.setUser(u);
        w.setCreatedBy(u);
        w.setUpdatedBy(u);

        w.setWorkflowType(WorkflowType.CHECKER);
        w.setWorkflowProcessStatus(WorkflowProcessStatus.NONE);
        wfs.add(w);

        w = new Workflow();
        w.setId(new Long(80));
        w.setWorkflowNo(new Long(8));
        u = new User();
        u.setEmpNo("ZZA10");
        u.setNameE("Test Juurou");
        w.setUser(u);
        w.setCreatedBy(u);
        w.setUpdatedBy(u);

        w.setWorkflowType(WorkflowType.CHECKER);
        w.setWorkflowProcessStatus(WorkflowProcessStatus.NONE);
        wfs.add(w);

        w = new Workflow();
        w.setId(new Long(90));
        w.setWorkflowNo(new Long(9));
        u = new User();
        u.setEmpNo("ZZA11");
        u.setNameE("Test Juuitirou");
        w.setUser(u);
        w.setCreatedBy(u);
        w.setUpdatedBy(u);

        w.setWorkflowType(WorkflowType.CHECKER);
        w.setWorkflowProcessStatus(WorkflowProcessStatus.NONE);
        wfs.add(w);

        w = new Workflow();
        w.setId(new Long(100));
        w.setWorkflowNo(new Long(10));
        u = new User();
        u.setEmpNo("ZZA12");
        u.setNameE("Test Juunirou");
        w.setUser(u);
        w.setCreatedBy(u);
        w.setUpdatedBy(u);

        w.setWorkflowType(WorkflowType.CHECKER);
        w.setWorkflowProcessStatus(WorkflowProcessStatus.NONE);
        wfs.add(w);

        w = new Workflow();
        w.setId(new Long(110));
        w.setWorkflowNo(new Long(11));
        u = new User();
        u.setEmpNo("ZZA13");
        u.setNameE("Test Juusannrou");
        w.setUser(u);
        w.setCreatedBy(u);
        w.setUpdatedBy(u);

        w.setWorkflowType(WorkflowType.CHECKER);
        w.setWorkflowProcessStatus(WorkflowProcessStatus.NONE);
        wfs.add(w);

        w = new Workflow();
        w.setId(new Long(120));
        w.setWorkflowNo(new Long(12));
        u = new User();
        u.setEmpNo("ZZA03");
        u.setNameE("Atsushi Ishida");
        w.setUser(u);
        w.setCreatedBy(u);
        w.setUpdatedBy(u);

        w.setWorkflowType(WorkflowType.APPROVER);
        w.setWorkflowProcessStatus(WorkflowProcessStatus.NONE);
        wfs.add(w);

        return wfs;
    }


    /**
     * テスト用承認フローリストを作成する
     * @return
     */
    private List<Workflow> createWorkflow() throws Exception {
        List<Workflow> wfs = new ArrayList<Workflow>();
        Workflow w = new Workflow();
        User u;

        w.setId(new Long(10));
        w.setWorkflowNo(new Long(1));
        w.setCorresponId(99L);

        u = new User();
        u.setEmpNo("ZZA04");
        u.setNameE("Tomoko Okada");
        w.setUser(u);
        w.setCreatedBy(u);
        w.setUpdatedBy(u);

        w.setWorkflowType(WorkflowType.CHECKER);
        w.setWorkflowProcessStatus(WorkflowProcessStatus.CHECKED);
        wfs.add(w);

        w = new Workflow();
        w.setId(new Long(20));
        w.setWorkflowNo(new Long(2));
        w.setCorresponId(99L);

        u = new User();
        u.setEmpNo("ZZA02");
        u.setNameE("Tetsuo Aoki");
        w.setUser(u);
        w.setCreatedBy(u);
        w.setUpdatedBy(u);

        w.setWorkflowType(WorkflowType.CHECKER);
        w.setWorkflowProcessStatus(WorkflowProcessStatus.CHECKED);

        w.setFinishedBy(u);

        w.setFinishedAt(new GregorianCalendar(2009, 3, 1, 12, 34, 56).getTime());
        wfs.add(w);

        w = new Workflow();
        w.setId(new Long(30));
        w.setWorkflowNo(new Long(3));
        w.setCorresponId(99L);

        u = new User();
        u.setEmpNo("ZZA03");
        u.setNameE("Atsushi Ishida");
        w.setUser(u);
        w.setCreatedBy(u);
        w.setUpdatedBy(u);

        w.setWorkflowType(WorkflowType.APPROVER);
        w.setWorkflowProcessStatus(WorkflowProcessStatus.NONE);
        wfs.add(w);

        return wfs;

    }

    /**
     * テスト用承認フローリストを作成する
     * @return
     */
    private List<Workflow> createWorkflowDummy() throws Exception {
        List<Workflow> wfs = new ArrayList<Workflow>();
        Workflow w = new Workflow();
        User u;

        w.setId(new Long(99));
        w.setWorkflowNo(new Long(1));

        u = new User();
        u.setEmpNo("80001");
        u.setNameE("Checker1 User");
        w.setUser(u);
        w.setCreatedBy(u);
        w.setUpdatedBy(u);

        w.setWorkflowType(WorkflowType.CHECKER);
        w.setWorkflowProcessStatus(WorkflowProcessStatus.CHECKED);
        wfs.add(w);

        w = new Workflow();
        w.setId(new Long(20));
        w.setWorkflowNo(new Long(2));

        u = new User();
        u.setEmpNo("80002");
        u.setNameE("Checker2 User");
        w.setUser(u);
        w.setCreatedBy(u);
        w.setUpdatedBy(u);

        w.setWorkflowType(WorkflowType.CHECKER);
        w.setWorkflowProcessStatus(WorkflowProcessStatus.CHECKED);

        w.setFinishedBy(u);

        w.setFinishedAt(new GregorianCalendar(2009, 3, 1, 12, 34, 56).getTime());
        wfs.add(w);

        w = new Workflow();
        w.setId(new Long(30));
        w.setWorkflowNo(new Long(3));

        u = new User();
        u.setEmpNo("80003");
        u.setNameE("Approver User");
        w.setUser(u);
        w.setCreatedBy(u);
        w.setUpdatedBy(u);

        w.setWorkflowType(WorkflowType.APPROVER);
        w.setWorkflowProcessStatus(WorkflowProcessStatus.NONE);
        wfs.add(w);

        return wfs;

    }

    /**
     * テスト用承認フローリストを作成する
     * @return
     */
    private List<Workflow> createWorkflowDummyStatusNone() throws Exception {
        List<Workflow> wfs = new ArrayList<Workflow>();
        Workflow w = new Workflow();
        User u;

        w.setId(new Long(99));
        w.setWorkflowNo(new Long(1));

        u = new User();
        u.setEmpNo("80001");
        u.setNameE("Checker1 User");
        w.setUser(u);
        w.setCreatedBy(u);
        w.setUpdatedBy(u);

        w.setWorkflowType(WorkflowType.CHECKER);
        w.setWorkflowProcessStatus(WorkflowProcessStatus.NONE);
        wfs.add(w);

        w = new Workflow();
        w.setId(new Long(20));
        w.setWorkflowNo(new Long(2));

        u = new User();
        u.setEmpNo("80002");
        u.setNameE("Checker2 User");
        w.setUser(u);
        w.setCreatedBy(u);
        w.setUpdatedBy(u);

        w.setWorkflowType(WorkflowType.CHECKER);
        w.setWorkflowProcessStatus(WorkflowProcessStatus.NONE);

        w.setFinishedBy(u);

        w.setFinishedAt(new GregorianCalendar(2009, 3, 1, 12, 34, 56).getTime());
        wfs.add(w);

        w = new Workflow();
        w.setId(new Long(30));
        w.setWorkflowNo(new Long(3));

        u = new User();
        u.setEmpNo("80003");
        u.setNameE("Approver User");
        w.setUser(u);
        w.setCreatedBy(u);
        w.setUpdatedBy(u);

        w.setWorkflowType(WorkflowType.APPROVER);
        w.setWorkflowProcessStatus(WorkflowProcessStatus.NONE);
        wfs.add(w);

        return wfs;

    }

    private void setUpAddresCorresponGroup(Long... ids) {
        List<AddressCorresponGroup> groups = new ArrayList<AddressCorresponGroup>();
        for (Long id : ids) {
            AddressCorresponGroup ag = new AddressCorresponGroup();
            ag.setId(id);
            CorresponGroup g = new CorresponGroup();
            g.setId(id);
            ag.setCorresponGroup(g);
            groups.add(ag);
        }

        MockAddressCorresponGroupDao.RET_FIND_BY_CORRESPON_ID = groups;
    }

    private void setUpFromCorresponGroup(Correspon c, Long id) {
        CorresponGroup from = new CorresponGroup();
        from.setId(id);
        c.setFromCorresponGroup(from);
    }

    private Correspon createCorrespon() {
        Correspon c = new Correspon();
        c.setWorkflowStatus(WorkflowStatus.REQUEST_FOR_CHECK);
        c.setProjectId("PJ1");
        c.setSubject("mock");
        c.setCreatedBy(user);
        c.setUpdatedBy(user);
        c.setId(99L);
        c.setVersionNo(1L);

        setUpFromCorresponGroup(c, 1L);
        setUpAddresCorresponGroup(2L, 3L, 4L);
        c.setAddressCorresponGroups(MockAddressCorresponGroupDao.RET_FIND_BY_CORRESPON_ID);

        CorresponType ct = new CorresponType();
        WorkflowPattern wp = new WorkflowPattern();
        wp.setWorkflowCd(SystemConfig.getValue(KEY_PATTERN_1));
        ct.setWorkflowPattern(wp);
        c.setCorresponType(ct);

        return c;
    }

    private List<ProjectUser> createProjectUserList() {
        List<ProjectUser> projectUserList = new ArrayList<ProjectUser>();
        ProjectUser pu;
        User u;
        u = new User();
        u.setEmpNo("ZZA01");
        u.setNameE("Taro Yamada");
        pu = new ProjectUser();
        pu.setUser(u);
        pu.setProjectId("PJ1");
        projectUserList.add(pu);

        u = new User();
        u.setEmpNo("ZZA02");
        u.setNameE("Tetsuo Aoki");
        pu = new ProjectUser();
        pu.setUser(u);
        pu.setProjectId("PJ1");
        projectUserList.add(pu);

        u = new User();
        u.setEmpNo("ZZA03");
        u.setNameE("Atsushi Ishida");
        pu = new ProjectUser();
        pu.setUser(u);
        pu.setProjectId("PJ1");
        projectUserList.add(pu);

        u = new User();
        u.setEmpNo("ZZA04");
        u.setNameE("Tomoko Okada");
        pu = new ProjectUser();
        pu.setUser(u);
        pu.setProjectId("PJ1");
        projectUserList.add(pu);

        u = new User();
        u.setEmpNo("ZZA05");
        u.setNameE("Keiichi Ogiwara");
        pu = new ProjectUser();
        pu.setUser(u);
        pu.setProjectId("PJ1");
        projectUserList.add(pu);

        u = new User();
        u.setEmpNo("ZZA06");
        u.setNameE("TEST6");
        pu = new ProjectUser();
        pu.setUser(u);
        pu.setProjectId("PJ1");
        projectUserList.add(pu);

        return projectUserList;
    }

    private List<Workflow> createWorkflowList(WorkflowProcessStatus...status) {
        List<Workflow> list = new ArrayList<Workflow>();

        Long no = 1L;
        Workflow w;
        w = new Workflow();
        w.setWorkflowNo(no++);
        w.setWorkflowType(WorkflowType.CHECKER);
        w.setWorkflowProcessStatus(WorkflowProcessStatus.CHECKED);
        list.add(w);

        w = new Workflow();
        w.setWorkflowNo(no++);
        w.setWorkflowType(WorkflowType.CHECKER);
        w.setWorkflowProcessStatus(WorkflowProcessStatus.NONE);
        list.add(w);


        w = new Workflow();
        w.setWorkflowNo(no++);
        w.setWorkflowType(WorkflowType.CHECKER);
        w.setWorkflowProcessStatus(WorkflowProcessStatus.NONE);
        list.add(w);

        w = new Workflow();
        w.setWorkflowNo(no++);
        w.setWorkflowType(WorkflowType.CHECKER);
        w.setWorkflowProcessStatus(WorkflowProcessStatus.NONE);
        list.add(w);

        w = new Workflow();
        w.setWorkflowNo(no++);
        w.setWorkflowType(WorkflowType.APPROVER);
        w.setWorkflowProcessStatus(WorkflowProcessStatus.NONE);
        list.add(w);

        for (int i = 0; i < status.length; i++) {
            list.get(i).setWorkflowProcessStatus(status[i]);
        }
        return list;
    }

    @Test
    public void testNextWorkflowUpdatable() throws Exception {

        CorresponWorkflowServiceImpl s = (CorresponWorkflowServiceImpl) service;

        //  通常ケース
        //  先頭のCheckerがCheck、残りは全てNone
        List<Workflow> workflowList = createWorkflowList();
        Workflow current = workflowList.get(0);
        Workflow next =
            s.getFirstNotProcessedWorkflow(workflowList, current);
        assertNotNull(next);
        assertEquals(Long.valueOf(2L), next.getWorkflowNo());
        assertTrue(s.isNextProcessStatusUpdatable(workflowList, next, current));


        //  通常ケース
        //  先頭から順にCheckしCheckerの最後から2番目がCheck、以降はNone
        workflowList = createWorkflowList(WorkflowProcessStatus.CHECKED,
                                        WorkflowProcessStatus.CHECKED,
                                        WorkflowProcessStatus.CHECKED);
        current = workflowList.get(2);
        next = s.getFirstNotProcessedWorkflow(workflowList, current);
        assertNotNull(next);
        assertEquals(Long.valueOf(4L), next.getWorkflowNo());
        assertTrue(s.isNextProcessStatusUpdatable(workflowList, next, current));


        //  通常ケース
        //  先頭から順にCheckし最後のCheckerがCheck、以降はNone
        workflowList = createWorkflowList(WorkflowProcessStatus.CHECKED,
                                        WorkflowProcessStatus.CHECKED,
                                        WorkflowProcessStatus.CHECKED,
                                        WorkflowProcessStatus.CHECKED);
        current = workflowList.get(3);
        next = s.getFirstNotProcessedWorkflow(workflowList, current);
        assertNotNull(next);
        assertEquals(Long.valueOf(5L), next.getWorkflowNo());
        assertFalse(s.isNextProcessStatusUpdatable(workflowList, next, current));


        //  ワークフローパターン2から1に変更され、承認フローにCheckerを追加したケース
        //  次のCheckerがNoneだが、前のCheckerがまだCheckedではない
        workflowList = createWorkflowList(WorkflowProcessStatus.CHECKED,
                                        WorkflowProcessStatus.REQUEST_FOR_CHECK,
                                        WorkflowProcessStatus.CHECKED,
                                        WorkflowProcessStatus.NONE);
        current = workflowList.get(2);
        next = s.getFirstNotProcessedWorkflow(workflowList, current);
        assertNotNull(next);
        assertEquals(Long.valueOf(4L), next.getWorkflowNo());
        assertFalse(s.isNextProcessStatusUpdatable(workflowList, next, current));


        //  ワークフローパターン3から1に変更され、承認フローにCheckerを追加したケース
        //  次のCheckerがNone、ApproverがRequest for Approval
        workflowList = createWorkflowList(WorkflowProcessStatus.CHECKED,
                                        WorkflowProcessStatus.NONE,
                                        WorkflowProcessStatus.NONE,
                                        WorkflowProcessStatus.NONE,
                                        WorkflowProcessStatus.REQUEST_FOR_APPROVAL);
        current = workflowList.get(0);
        next = s.getFirstNotProcessedWorkflow(workflowList, current);
        assertNotNull(next);
        assertEquals(Long.valueOf(2L), next.getWorkflowNo());
        assertTrue(s.isNextProcessStatusUpdatable(workflowList, next, current));


        //  Checkerが一人のケース
        workflowList = createWorkflowList(WorkflowProcessStatus.CHECKED);
        while (workflowList.size() > 2) {
            workflowList.remove(1);
            workflowList.get(1).setWorkflowNo(2L);
        }

        current = workflowList.get(0);
        next = s.getFirstNotProcessedWorkflow(workflowList, current);
        assertNotNull(next);
        assertEquals(Long.valueOf(2L), next.getWorkflowNo());
        assertFalse(s.isNextProcessStatusUpdatable(workflowList, next, current));


        //  ワークフローパターン2から1に変更され、承認フローにCheckerを追加したケース
        //  直後のCheckerはCheckedでその次のCheckerがNone
        //  NoneのCheckerの前のCheckerは全てChecked
        workflowList = createWorkflowList(WorkflowProcessStatus.CHECKED,
                                        WorkflowProcessStatus.CHECKED,
                                        WorkflowProcessStatus.CHECKED,
                                        WorkflowProcessStatus.NONE,
                                        WorkflowProcessStatus.NONE);
        current = workflowList.get(1);
        next = s.getFirstNotProcessedWorkflow(workflowList, current);
        assertNotNull(next);
        assertEquals(Long.valueOf(4L), next.getWorkflowNo());
        assertTrue(s.isNextProcessStatusUpdatable(workflowList, next, current));


        //  ワークフローパターン2から1に変更されたケース
        //  最初のCheckerがCheck
        //  その他のCheckerは全て未Check
        workflowList = createWorkflowList(WorkflowProcessStatus.REQUEST_FOR_CHECK,
                                        WorkflowProcessStatus.CHECKED,
                                        WorkflowProcessStatus.REQUEST_FOR_CHECK,
                                        WorkflowProcessStatus.REQUEST_FOR_CHECK,
                                        WorkflowProcessStatus.NONE);
        current = workflowList.get(1);
        next = s.getFirstNotProcessedWorkflow(workflowList, current);
        assertNotNull(next);
        assertEquals(Long.valueOf(5L), next.getWorkflowNo());
        assertFalse(s.isNextProcessStatusUpdatable(workflowList, next, current));
    }
}
