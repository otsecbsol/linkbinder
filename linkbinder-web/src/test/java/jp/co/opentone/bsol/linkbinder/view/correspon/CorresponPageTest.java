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
package jp.co.opentone.bsol.linkbinder.view.correspon;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
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

import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.core.util.ArgumentValidator;
import jp.co.opentone.bsol.framework.extension.jsf.FacesContextMock;
import jp.co.opentone.bsol.framework.test.AbstractTestCase;
import jp.co.opentone.bsol.framework.web.view.util.ViewHelper;
import jp.co.opentone.bsol.linkbinder.dto.Correspon;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroup;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroupUserMapping;
import jp.co.opentone.bsol.linkbinder.dto.CorresponReadStatus;
import jp.co.opentone.bsol.linkbinder.dto.CorresponResponseHistory;
import jp.co.opentone.bsol.linkbinder.dto.CorresponType;
import jp.co.opentone.bsol.linkbinder.dto.Project;
import jp.co.opentone.bsol.linkbinder.dto.ProjectUser;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.Workflow;
import jp.co.opentone.bsol.linkbinder.dto.code.CorresponStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.ReadStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowType;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponGroupCondition;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchUserCondition;
import jp.co.opentone.bsol.linkbinder.service.admin.impl.CorresponGroupServiceImpl;
import jp.co.opentone.bsol.linkbinder.service.admin.impl.UserServiceImpl;
import jp.co.opentone.bsol.linkbinder.service.correspon.impl.CorresponReadStatusServiceImpl;
import jp.co.opentone.bsol.linkbinder.service.correspon.impl.CorresponServiceImpl;
import jp.co.opentone.bsol.linkbinder.util.view.correspon.CorresponResponseHistoryModel;
import jp.co.opentone.bsol.linkbinder.view.AbstractPage;
import jp.co.opentone.bsol.linkbinder.view.correspon.module.DefaultModule;
import jp.co.opentone.bsol.linkbinder.view.correspon.module.VerificationModule;
import jp.co.opentone.bsol.linkbinder.view.correspon.module.WorkflowEditModule;
import mockit.Mock;
import mockit.MockUp;

/**
 * {@link CorresponPage}のテストケース.
 * @author opentone
 */
public class CorresponPageTest extends AbstractTestCase {

    /**
     * テスト対象.
     */
    @Resource(name = "corresponPage")
    private CorresponPage page;

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
     * ログインユーザー
     */
    private User loginUser;

    /**
     * テストの前準備.
     */
    @BeforeClass
    public static void testSetUp() {
        FacesContextMock.initialize();
        new MockCorresponService();
        new MockAbstractPage();
        new MockAbstractCorresponPage();
        new MockViewHelper();
        new MockCorresponReadStatusService();
        new MockCorresponGroupService();
        new MockUserService();
        //TODO JMockitバージョンアップ対応
//        Mockit.redefineMethods(CorresponServiceImpl.class, MockCorresponService.class);
//        Mockit.redefineMethods(CorresponWorkflowServiceImpl.class,
//            MockCorresponWorkflowService.class);
//        Mockit.redefineMethods(AbstractPage.class, MockAbstractPage.class);
//        Mockit.redefineMethods(AbstractCorresponPage.class, MockAbstractCorresponPage.class);
//        Mockit.redefineMethods(ViewHelper.class, MockViewHelper.class);
//
//        Mockit.redefineMethods(CorresponReadStatusServiceImpl.class,
//            MockCorresponReadStatusService.class);
//        Mockit.redefineMethods(CorresponGroupServiceImpl.class, MockCorresponGroupService.class);
//        Mockit.redefineMethods(UserServiceImpl.class, MockUserService.class);
//        Mockit.redefineMethods(UserServiceImpl.class, MockUserService.class);
    }

    /**
     * 後始末.
     */
    @AfterClass
    public static void testTeardown() {
        new MockCorresponService().tearDown();
        new MockAbstractPage().tearDown();
        new MockAbstractCorresponPage().tearDown();
        new MockViewHelper().tearDown();
        new MockCorresponReadStatusService().tearDown();
        new MockCorresponGroupService().tearDown();
        new MockUserService().tearDown();
        FacesContextMock.tearDown();
    }

    @Before
    public void setUp() {
        FacesContextMock.EXPECTED_MESSAGE = null;
        loginUser = new User();
        loginUser.setEmpNo("USER002");
        loginUser.setNameE("Taro Yamada");
        page.setCurrentUser(loginUser);
        page.setId(null);
        page.setWorkflowEditDisplay(false);
        page.setVerificationDisplay(false);
    }

    @After
    public void tearDown() {
        MockCorresponService.RET_FIND = null;
        MockCorresponService.RET_REQUESTFORAPPROVAL = null;
        MockCorresponService.RET_HTML = null;
        MockCorresponService.SET_CORRESPON = null;
        MockCorresponService.CRT_SAVE_PARTIAL = null;
        MockViewHelper.EX_RESPONSE = null;
        MockViewHelper.SET_DATA = null;

        MockCorresponReadStatusService.SET_CREATE = new ArrayList<CorresponReadStatus>();
        MockCorresponReadStatusService.SET_UPDATE = new ArrayList<CorresponReadStatus>();
        MockCorresponReadStatusService.SET_READ_CREATE = new CorresponReadStatus();
        MockCorresponReadStatusService.EX_UPDATE = null;

        MockCorresponGroupService.RET_FIND_CORRESPON_GROUP_USER_MAPPINGS = null;
        MockCorresponGroupService.RET_SEARCH = null;
        MockUserService.RET_SEARCH = null;

    }

    /**
     * 初期化アクションのテスト.
     * @throws Exception
     */
    @Test
    public void testInitialize() throws Exception {
        MockAbstractPage.RET_SYSTEM_ADMIN = false;
        MockAbstractPage.RET_PROJECT_ADMIN = true;
        MockAbstractPage.RET_GROUP_ADMIN = false;

        User user = new User();
        user.setEmpNo("USER004");
        MockAbstractPage.RET_USER = user;
        MockCorresponReadStatusService.RET_CURRENT_USER = user;

        Project project = new Project();
        project.setProjectId("PJ1");
        MockAbstractPage.RET_PROJECT = project;

        // Serviceが返すダミーのコレポン文書を設定
        Correspon expCorrespon = createCorrespon();
        List<Workflow> workflowList = createWorkflowList();

        expCorrespon.setWorkflows(workflowList);
        expCorrespon.setWorkflowStatus(WorkflowStatus.DENIED);
        expCorrespon.setBody("<p>1234567890</p>");
        MockCorresponService.RET_FIND = expCorrespon;

        MockCorresponGroupService.RET_FIND_CORRESPON_GROUP_USER_MAPPINGS =
                new ArrayList<CorresponGroupUserMapping>();
        MockCorresponGroupService.RET_SEARCH = new ArrayList<CorresponGroup>();
        MockUserService.RET_SEARCH = new ArrayList<ProjectUser>();

        // ダイアログ表示状態を設定（通常両方設定されることはない）
        page.setWorkflowEditDisplay(true);
        page.setVerificationDisplay(true);

        // 応答履歴をセット
        page.setCorresponResponseHistory(createCorresponResponseHistoryList(10L, 2));
        page.setResponseHistoryDetail(CorresponPage.LABEL_HIDE_DETAILS);
        page.setResponseHistoryDisplay(CorresponPage.STYLE_SHOW);

        // 必須のプロパティをセットして実行
        page.setId(expCorrespon.getId());
        page.initialize();

        // ダイアログ表示状態を設定（通常両方設定されることはない）
        assertFalse(page.isWorkflowEditDisplay());
        assertFalse(page.isVerificationDisplay());

        page.prerender();
        assertEquals(expCorrespon.toString(), page.getCorrespon().toString());
        assertNull(page.getCorresponResponseHistory());
        assertEquals(CorresponPage.LABEL_HIDE_DETAILS, page.getResponseHistoryDetail());
        assertEquals(CorresponPage.STYLE_SHOW, page.getResponseHistoryDisplay());
    }

    /**
     * 初期化アクションのテスト.
     * @throws Exception
     */
    @Test
    public void testReload() throws Exception {
        MockAbstractPage.RET_SYSTEM_ADMIN = false;
        MockAbstractPage.RET_PROJECT_ADMIN = true;
        MockAbstractPage.RET_GROUP_ADMIN = false;

        User user = new User();
        user.setEmpNo("USER004");
        MockAbstractPage.RET_USER = user;
        MockCorresponReadStatusService.RET_CURRENT_USER = user;

        Project project = new Project();
        project.setProjectId("PJ1");
        MockAbstractPage.RET_PROJECT = project;

        // Serviceが返すダミーのコレポン文書を設定
        Correspon expCorrespon = createCorrespon();
        List<Workflow> workflowList = createWorkflowList();

        expCorrespon.setWorkflows(workflowList);
        expCorrespon.setWorkflowStatus(WorkflowStatus.DENIED);
        expCorrespon.setBody("<p>1234567890</p>");
        MockCorresponService.RET_FIND = expCorrespon;

        MockCorresponGroupService.RET_FIND_CORRESPON_GROUP_USER_MAPPINGS =
                new ArrayList<CorresponGroupUserMapping>();
        MockCorresponGroupService.RET_SEARCH = new ArrayList<CorresponGroup>();
        MockUserService.RET_SEARCH = new ArrayList<ProjectUser>();

        // 応答履歴をセット
        List<CorresponResponseHistory> histories = createCorresponResponseHistoryList(10L, 2);
        page.setCorresponResponseHistory(histories);
        page.setResponseHistoryDetail(CorresponPage.LABEL_HIDE_DETAILS);
        page.setResponseHistoryDisplay(CorresponPage.STYLE_SHOW);

        // 必須のプロパティをセットして実行
        page.setId(expCorrespon.getId());
        page.reload();

        page.prerender();
        assertEquals(expCorrespon.toString(), page.getCorrespon().toString());
        assertEquals(CorresponPage.LABEL_HIDE_DETAILS, page.getResponseHistoryDetail());
        assertEquals(CorresponPage.STYLE_SHOW, page.getResponseHistoryDisplay());
    }

    /**
     * 承認フロー編集ダイアログ表示のテスト.
     */
    @Test
    public void testEditWorkflow() {
        MockAbstractPage.RET_SYSTEM_ADMIN = false;
        MockAbstractPage.RET_PROJECT_ADMIN = true;
        MockAbstractPage.RET_GROUP_ADMIN = false;

        User user = new User();
        user.setEmpNo("USER004");
        MockAbstractPage.RET_USER = user;
        MockCorresponReadStatusService.RET_CURRENT_USER = user;

        Project project = new Project();
        project.setProjectId("PJ1");
        MockAbstractPage.RET_PROJECT = project;

        // Serviceが返すダミーのコレポン文書を設定
        Correspon c = createCorrespon();
        List<Workflow> workflowList = createWorkflowList();

        c.setWorkflows(workflowList);
        c.setWorkflowStatus(WorkflowStatus.DENIED);
        c.setBody("<p>1234567890</p>");
        MockCorresponService.RET_FIND = c;

        MockCorresponGroupService.RET_SEARCH = new ArrayList<CorresponGroup>();
        MockUserService.RET_SEARCH = new ArrayList<ProjectUser>();

        page.setCorrespon(c);
        page.setId(c.getId());

        page.editWorkflow();

        assertTrue(page.isWorkflowEditDisplay());
    }

    /**
     * 承認フロー編集ダイアログ表示のテスト.
     */
    @Test
    public void testVerify() {
        MockAbstractPage.RET_SYSTEM_ADMIN = false;
        MockAbstractPage.RET_PROJECT_ADMIN = true;
        MockAbstractPage.RET_GROUP_ADMIN = false;

        User user = new User();
        user.setEmpNo("USER004");
        MockAbstractPage.RET_USER = user;
        MockCorresponReadStatusService.RET_CURRENT_USER = user;

        Project project = new Project();
        project.setProjectId("PJ1");
        MockAbstractPage.RET_PROJECT = project;

        // Serviceが返すダミーのコレポン文書を設定
        Correspon c = createCorrespon();
        List<Workflow> workflowList = createWorkflowList();

        c.setWorkflows(workflowList);
        c.setWorkflowStatus(WorkflowStatus.DENIED);
        c.setBody("<p>1234567890</p>");
        MockCorresponService.RET_FIND = c;

        MockCorresponGroupService.RET_SEARCH = new ArrayList<CorresponGroup>();
        MockUserService.RET_SEARCH = new ArrayList<ProjectUser>();

        page.setCorrespon(c);
        page.setId(c.getId());

        page.verify();

        assertTrue(page.isVerificationDisplay());
    }

    /**
     * 初期化アクションのテスト.
     * @throws Exception
     */
    @Test
    public void testGetModule() {
        assertTrue(page.getModule() instanceof DefaultModule);
        assertFalse(page.getModule() instanceof WorkflowEditModule);
        assertFalse(page.getModule() instanceof VerificationModule);

        page.setWorkflowEditDisplay(true);
        assertTrue(page.getModule() instanceof WorkflowEditModule);

        page.setWorkflowEditDisplay(false);
        page.setVerificationDisplay(true);
        assertTrue(page.getModule() instanceof VerificationModule);
    }


    /**
     * コレポン一覧上の文書「前へ」リンクを操作可能とするかを判定するメソッドの検証
     */
    @Test
    public void testIsPreviousLink() {
        MockAbstractPage.RET_SYSTEM_ADMIN = false;
        MockAbstractPage.RET_PROJECT_ADMIN = false;
        MockAbstractPage.RET_GROUP_ADMIN = false;

        User user = new User();
        user.setEmpNo("USER004");
        MockAbstractPage.RET_USER = user;
        MockCorresponReadStatusService.RET_CURRENT_USER = user;

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
        page.setDetectedAddressUserId(100L);
        page.setCorrespon(expCorrespon);

        //  Attentionをもたない通常ユーザーも操作できる
        expCorrespon.setCorresponStatus(CorresponStatus.OPEN);
        page.initialize();

        page.setDisplayIndex(0);
        assertFalse(page.isPreviousLink());

        page.setDisplayIndex(1);
        assertTrue(page.isPreviousLink());
    }

    /**
     * テスト用コレポン文書の作成
     */
    private Correspon createCorrespon() {
        Correspon c = new Correspon();
        c.setId(10L);
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

    /**
     * テスト用ワークフローリストの作成
     */
    private List<Workflow> createWorkflowList() {
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
        list.add(wf);

        return list;
    }

    /**
     * テスト用応答履歴リストの作成
     */
    private List<CorresponResponseHistory> createCorresponResponseHistoryList(Long start, int len) {
        List<CorresponResponseHistory> crh = new ArrayList<CorresponResponseHistory>();
        for (int i = 0 ; i < len ; i++ ) {
            crh.add(createCorresponResponseHistory(start++));
        }
        return crh;
    }

    /**
     * テスト用応答履歴の作成
     */
    private CorresponResponseHistory createCorresponResponseHistory(Long id) {
        CorresponResponseHistory history = new CorresponResponseHistory();
        Correspon correspon = new Correspon();
        correspon.setId(id);
        history.setCorrespon(correspon);
        return history;
    }

    public static class MockAbstractPage extends MockUp<AbstractPage> {
        static User RET_USER;
        static Project RET_PROJECT;
        static ProjectUser RET_PROJECT_USER;
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
        public ProjectUser getCurrentProjectUser() {
            if (RET_PROJECT_USER != null) {
                return RET_PROJECT_USER;
            }

            if (RET_USER != null && RET_PROJECT != null) {
                ProjectUser pu = new ProjectUser();
                pu.setUser(RET_USER);
                pu.setProjectId(RET_PROJECT.getProjectId());
                return pu;
            }
            return null;
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
    }

    public static class MockAbstractCorresponPage extends MockUp<AbstractCorresponPage> {
        static List<Long> RET_CORRESPON_ID_LIST;

        @Mock
        public List<Long> getCorresponIds() {
            return RET_CORRESPON_ID_LIST;
        }
    }

    public static class MockCorresponService extends MockUp<CorresponServiceImpl> {
        static Correspon RET_FIND;
        static String RET_REQUESTFORAPPROVAL;
        static String RET_UPDATE;
        static String RET_DELETE;
        static String RET_ISSUE;
        static byte[] RET_HTML;
        static Correspon SET_CORRESPON;
        static ServiceAbortException EX_VALIDATE;
        static Correspon CRT_SAVE_PARTIAL;
        static List<CorresponResponseHistory> RET_CORRESPON_RESPONSE_HISTORY;

        @Mock
        public Correspon find(Long id) throws ServiceAbortException {
            return RET_FIND;
        }

        @Mock
        public void requestForApproval(Correspon correspon) throws ServiceAbortException {
            RET_REQUESTFORAPPROVAL = "success for requestForApproval";
        }

        @Mock
        public byte[] generateHTML(Correspon correspon,
            List<CorresponResponseHistoryModel> corresponResponseHistory) throws ServiceAbortException {
            SET_CORRESPON = correspon;
            return RET_HTML;
        }

        @Mock
        public void delete(Correspon correspon) throws ServiceAbortException {
            if (EX_VALIDATE != null) {
                throw EX_VALIDATE;
            }
            RET_DELETE = "The correspondence has been deleted.";
        }

        @Mock
        public void issue(Correspon correspon) throws ServiceAbortException {
            if (EX_VALIDATE != null) {
                throw EX_VALIDATE;
            }
            RET_ISSUE = "The correspondence has been issued.";
        }

        @Mock
        public void savePartial(Correspon correspon) throws ServiceAbortException {
            CRT_SAVE_PARTIAL = correspon;
        }

        @Mock
        public List<CorresponResponseHistory> findCorresponResponseHistory(Correspon correspon)
            throws ServiceAbortException {
            return RET_CORRESPON_RESPONSE_HISTORY;
        }
    }

    public static class MockViewHelper extends MockUp<ViewHelper> {
        static byte[] SET_DATA;
        static IOException EX_RESPONSE;
        static Map<String, Object> map = new HashMap<String, Object>();

        @Mock
        public void requestResponse(byte[] content, String charset) throws IOException {
            if (EX_RESPONSE != null) {
                throw EX_RESPONSE;
            }
            SET_DATA = content;
        }

        @Mock
        @SuppressWarnings("unchecked")
        public <T> T getSessionValue(String key) {
            return (T) map.get(key);
        }

        @Mock
        public void setSessionValue(String key, Object value) {
            map.put(key, value);
        }

        @Mock
        public void removeSessionValue(String key) {
            map.remove(key);
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
}
