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
package jp.co.opentone.bsol.linkbinder.view.common;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.faces.model.DataModel;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import jp.co.opentone.bsol.framework.extension.jsf.FacesContextMock;
import jp.co.opentone.bsol.framework.test.AbstractTestCase;
import jp.co.opentone.bsol.linkbinder.dto.Project;
import jp.co.opentone.bsol.linkbinder.dto.ProjectSummary;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.code.ReadStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowStatus;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponCondition;
import jp.co.opentone.bsol.linkbinder.service.common.impl.HomeServiceImpl;
import jp.co.opentone.bsol.linkbinder.view.AbstractPage;
import mockit.Mock;
import mockit.MockUp;

/**
 * @author opentone
 */
public class HomePageTest extends AbstractTestCase {

    /**
     * テスト対象.
     */
    @Resource
    private HomePage page;

    /**
     * テストの前準備.
     */
    @BeforeClass
    public static void testSetUp() {
        FacesContextMock.initialize();
        new MockAbstractPage();
        new MockHomeService();
    }

    /**
     * 後始末.
     */
    @AfterClass
    public static void testTeardown() {
        new MockAbstractPage().tearDown();
        new MockHomeService().tearDown();
        FacesContextMock.tearDown();
    }

    @Before
    public void setup() {
        page.setCcSearch(false);
        page.setAttentionSearch(false);
    }

    @After
    public void tearDown() {
        MockHomeService.RET_FIND_PROJECTS = null;
        MockAbstractPage.SET_CONDITION = null;
        MockAbstractPage.SET_PROJECT = null;
        MockAbstractPage.RET_USER = null;
        MockAbstractPage.RET_SYSTEM_ADMIN = false;
        MockAbstractPage.RET_PROJECT_ADMIN = false;
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.view.common.HomePage#initialize()} のためのテスト・メソッド.
     * （デフォルトプロジェクト無し）
     */
    @Test
    public void testInitialize() {
        User login = new User();
        login.setEmpNo("ZZA01");
        MockAbstractPage.RET_USER = login;

        List<ProjectSummary> expList = createProjectSummaryList();
        MockHomeService.RET_FIND_PROJECTS = expList;

        page.initialize();

        assertEquals(expList, page.getProjectSummaryList());
        assertEquals(expList.size(), page.getDataModel().getRowCount());
        DataModel model = page.getDataModel();
        for (int i = 0; i < expList.size(); i++) {
            ProjectSummary expected = expList.get(i);
            model.setRowIndex(i);
            ProjectSummary actual = (ProjectSummary) model.getRowData();
            assertEquals(expected.getProject().getProjectId(), actual.getProject().getProjectId());
            assertEquals(expected.getAttentionCount(), actual.getAttentionCount());
            assertEquals(expected.getPersonInChargeCount(), actual.getPersonInChargeCount());
            assertEquals(expected.getCcCount(), actual.getCcCount());
        }
        // 初期設定
        assertFalse(page.isAttentionSearch());
        assertFalse(page.isPersonInChargeSearch());
        assertFalse(page.isCcSearch());
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.view.common.HomePage#initialize()} のためのテスト・メソッド.
     * （デフォルトプロジェクト有り）
     */
    @Test
    public void testInitializeDefaultSort() {
        User login = new User();
        login.setEmpNo("ZZA02");
        login.setDefaultProjectId("PJ3");
        MockAbstractPage.RET_USER = login;

        List<ProjectSummary> list = createProjectSummaryList();
        MockHomeService.RET_FIND_PROJECTS = list;

        // ソートされるはず
        List<ProjectSummary> expList = new ArrayList<ProjectSummary>();
        Project project = new Project();
        project.setProjectId("PJ3");
        ProjectSummary summary = new ProjectSummary();
        summary.setProject(project);
        summary.setAttentionCount(21L);
        summary.setPersonInChargeCount(22L);
        summary.setCcCount(23L);
        expList.add(summary);
        project = new Project();
        project.setProjectId("PJ1");
        summary = new ProjectSummary();
        summary.setProject(project);
        summary.setAttentionCount(1L);
        summary.setPersonInChargeCount(2L);
        summary.setCcCount(3L);
        expList.add(summary);
        project = new Project();
        project.setProjectId("PJ2");
        summary = new ProjectSummary();
        summary.setProject(project);
        summary.setAttentionCount(11L);
        summary.setPersonInChargeCount(12L);
        summary.setCcCount(13L);
        expList.add(summary);

        page.initialize();

        assertEquals(expList.size(), page.getProjectSummaryList().size());
        for (int i = 0; i < expList.size(); i++) {
            ProjectSummary expected = expList.get(i);
            ProjectSummary actual = page.getProjectSummaryList().get(i);
            assertEquals(expected.getProject().getProjectId(), actual.getProject().getProjectId());
            assertEquals(expected.getAttentionCount(), actual.getAttentionCount());
            assertEquals(expected.getPersonInChargeCount(), actual.getPersonInChargeCount());
            assertEquals(expected.getCcCount(), actual.getCcCount());
        }
        assertEquals(expList.size(), page.getDataModel().getRowCount());
        DataModel model = page.getDataModel();
        for (int i = 0; i < expList.size(); i++) {
            ProjectSummary expected = expList.get(i);
            model.setRowIndex(i);
            ProjectSummary actual = (ProjectSummary) model.getRowData();
            assertEquals(expected.getProject().getProjectId(), actual.getProject().getProjectId());
            assertEquals(expected.getAttentionCount(), actual.getAttentionCount());
            assertEquals(expected.getPersonInChargeCount(), actual.getPersonInChargeCount());
            assertEquals(expected.getCcCount(), actual.getCcCount());
        }
        // 初期設定
        assertFalse(page.isAttentionSearch());
        assertFalse(page.isPersonInChargeSearch());
        assertFalse(page.isCcSearch());
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.view.common.HomePage#initialize()} のためのテスト・メソッド.
     * （所属しているProject無し）
     */
    @Test
    public void testInitializeNoProject() {
        User login = new User();
        login.setEmpNo("ZZA02");
        login.setDefaultProjectId("PJ3");
        MockAbstractPage.RET_USER = login;

        MockHomeService.RET_FIND_PROJECTS = new ArrayList<ProjectSummary>();

        page.initialize();

        assertEquals(0, page.getProjectSummaryList().size());
        assertEquals(0, page.getDataModel().getRowCount());
        // 初期設定
        assertFalse(page.isAttentionSearch());
        assertFalse(page.isPersonInChargeSearch());
        assertFalse(page.isCcSearch());
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.view.common.HomePage#goProjectHome()} のためのテスト・メソッド.
     */
    @Test
    public void testGoProjectHome() {
        List<ProjectSummary> list = createProjectSummaryList();

        page.setProjectSummaryList(list);
        page.getDataModel().setRowIndex(2); // 2行目のプロジェクトを選択

        Project expProject = list.get(2).getProject();
        String expected = "projectHome?projectId=" + expProject.getProjectId();

        String actual = page.goProjectHome();

        assertEquals(expected, actual);
        assertEquals(expProject, MockAbstractPage.SET_PROJECT);
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.view.common.HomePage#goCorresponIndex()} のためのテスト・メソッド.
     * （Attention）
     */
    @Test
    public void testGoCorresponIndexAttention() {
        List<ProjectSummary> list = createProjectSummaryList();

        User login = new User();
        login.setEmpNo("ZZA01");

        MockAbstractPage.RET_USER = login;
        MockAbstractPage.RET_SYSTEM_ADMIN = true;
        MockAbstractPage.RET_PROJECT_ADMIN = true;

        Project expProject = list.get(2).getProject();
        String expected = "correspon/corresponIndex?projectId=PJ3";

        page.setProjectSummaryList(list);
        page.getDataModel().setRowIndex(2); // 2行目のプロジェクトを選択
        page.setAttentionSearch(true); // Attentionのリンク押下
        page.setPersonInChargeSearch(false);
        page.setCcSearch(false);

        String actual = page.goCorresponIndex();
        SearchCorresponCondition actCondition = MockAbstractPage.SET_CONDITION;

        assertEquals(expected, actual);
        assertEquals(expProject, MockAbstractPage.SET_PROJECT);
        assertEquals(expProject.getProjectId(), actCondition.getProjectId());
        assertEquals(login.getEmpNo(), actCondition.getUserId());

        assertArrayEquals(new WorkflowStatus[] {WorkflowStatus.ISSUED },
                          actCondition.getWorkflowStatuses());

        assertTrue(actCondition.isSystemAdmin());
        assertTrue(actCondition.isProjectAdmin());
//        assertTrue(actCondition.isAddressAttention());
//        assertFalse(actCondition.isAddressPersonInCharge());
//        assertFalse(actCondition.isAddressCc());
        assertTrue(actCondition.isUserAttention());
        assertFalse(actCondition.isUserPic());
        assertFalse(actCondition.isUserCc());
//        assertEquals(login, actCondition.getAddressUsers()[0]);
        assertEquals(login, actCondition.getToUsers()[0]);
        assertEquals(ReadStatus.NEW, actCondition.getReadStatuses()[0]);
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.view.common.HomePage#goCorresponIndex()} のためのテスト・メソッド.
     * （Person in Charge）
     */
    @Test
    public void testGoCorresponIndexPersonInCharge() {
        List<ProjectSummary> list = createProjectSummaryList();

        User login = new User();
        login.setEmpNo("ZZA01");

        MockAbstractPage.RET_USER = login;
        MockAbstractPage.RET_SYSTEM_ADMIN = true;
        MockAbstractPage.RET_PROJECT_ADMIN = true;

        Project expProject = list.get(2).getProject();
        String expected = "correspon/corresponIndex?projectId=PJ3";

        page.setProjectSummaryList(list);
        page.getDataModel().setRowIndex(2); // 2行目のプロジェクトを選択
        page.setAttentionSearch(false);
        page.setPersonInChargeSearch(true); // Person in Chargeのリンク押下
        page.setCcSearch(false);

        String actual = page.goCorresponIndex();
        SearchCorresponCondition actCondition = MockAbstractPage.SET_CONDITION;

        assertEquals(expected, actual);
        assertEquals(expProject, MockAbstractPage.SET_PROJECT);
        assertEquals(expProject.getProjectId(), actCondition.getProjectId());
        assertEquals(login.getEmpNo(), actCondition.getUserId());

        assertArrayEquals(new WorkflowStatus[] {WorkflowStatus.ISSUED },
                          actCondition.getWorkflowStatuses());

        assertTrue(actCondition.isSystemAdmin());
        assertTrue(actCondition.isProjectAdmin());
//        assertFalse(actCondition.isAddressAttention());
//        assertTrue(actCondition.isAddressPersonInCharge());
//        assertFalse(actCondition.isAddressCc());
        assertFalse(actCondition.isUserAttention());
        assertTrue(actCondition.isUserPic());
        assertFalse(actCondition.isUserCc());
//        assertEquals(login, actCondition.getAddressUsers()[0]);
        assertEquals(login, actCondition.getToUsers()[0]);
        assertEquals(ReadStatus.NEW, actCondition.getReadStatuses()[0]);
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.view.common.HomePage#goCorresponIndex()} のためのテスト・メソッド.
     * （Person in Charge）
     */
    @Test
    public void testGoCorresponIndexCc() {
        List<ProjectSummary> list = createProjectSummaryList();

        User login = new User();
        login.setEmpNo("ZZA01");

        MockAbstractPage.RET_USER = login;
        MockAbstractPage.RET_SYSTEM_ADMIN = true;
        MockAbstractPage.RET_PROJECT_ADMIN = true;

        Project expProject = list.get(2).getProject();
        String expected = "correspon/corresponIndex?projectId=PJ3";

        page.setProjectSummaryList(list);
        page.getDataModel().setRowIndex(2); // 2行目のプロジェクトを選択
        page.setAttentionSearch(false);
        page.setPersonInChargeSearch(false);
        page.setCcSearch(true); // Ccのリンク押下

        String actual = page.goCorresponIndex();
        SearchCorresponCondition actCondition = MockAbstractPage.SET_CONDITION;

        assertEquals(expected, actual);
        assertEquals(expProject, MockAbstractPage.SET_PROJECT);
        assertEquals(expProject.getProjectId(), actCondition.getProjectId());
        assertEquals(login.getEmpNo(), actCondition.getUserId());

        assertArrayEquals(new WorkflowStatus[] {WorkflowStatus.ISSUED },
                          actCondition.getWorkflowStatuses());

        assertTrue(actCondition.isSystemAdmin());
        assertTrue(actCondition.isProjectAdmin());
//        assertFalse(actCondition.isAddressAttention());
//        assertFalse(actCondition.isAddressPersonInCharge());
//        assertTrue(actCondition.isAddressCc());
        assertFalse(actCondition.isUserAttention());
        assertFalse(actCondition.isUserPic());
        assertTrue(actCondition.isUserCc());
//        assertEquals(login, actCondition.getAddressUsers()[0]);
        assertEquals(login, actCondition.getToUsers()[0]);
        assertEquals(ReadStatus.NEW, actCondition.getReadStatuses()[0]);
    }

    private List<ProjectSummary> createProjectSummaryList() {
        List<ProjectSummary> list = new ArrayList<ProjectSummary>();
        Project project = new Project();
        project.setProjectId("PJ1");
        ProjectSummary summary = new ProjectSummary();
        summary.setProject(project);
        summary.setAttentionCount(1L);
        summary.setPersonInChargeCount(2L);
        summary.setCcCount(3L);
        list.add(summary);
        project = new Project();
        project.setProjectId("PJ2");
        summary = new ProjectSummary();
        summary.setProject(project);
        summary.setAttentionCount(11L);
        summary.setPersonInChargeCount(12L);
        summary.setCcCount(13L);
        list.add(summary);
        project = new Project();
        project.setProjectId("PJ3");
        summary = new ProjectSummary();
        summary.setProject(project);
        summary.setAttentionCount(21L);
        summary.setPersonInChargeCount(22L);
        summary.setCcCount(23L);
        list.add(summary);

        return list;
    }
    public static class MockHomeService extends MockUp<HomeServiceImpl> {
        static List<ProjectSummary> RET_FIND_PROJECTS;

        @Mock
        public List<ProjectSummary> findProjects() {
            return RET_FIND_PROJECTS;
        }
    }

    public static class MockAbstractPage extends MockUp<AbstractPage> {
        static SearchCorresponCondition SET_CONDITION;
        static Project SET_PROJECT;
        static User RET_USER;
        static boolean RET_SYSTEM_ADMIN;
        static boolean RET_PROJECT_ADMIN;

        @Mock
        public void setCurrentSearchCorresponCondition(SearchCorresponCondition condition) {
            SET_CONDITION = condition;
        }
        @Mock
        public void setCurrentSearchCorresponCondition(SearchCorresponCondition condition, String projectId) {
            SET_CONDITION = condition;
        }

        @Mock
        public void setCurrentProjectInfo(Project project) {
            SET_PROJECT = project;
        }

        @Mock
        public User getCurrentUser() {
            return RET_USER;
        }

        @Mock
        public boolean isSystemAdmin() {
            return RET_SYSTEM_ADMIN;
        }

        @Mock
        public boolean isProjectAdmin(String checkProjectId) {
            return RET_PROJECT_ADMIN;
        }
    }
}
