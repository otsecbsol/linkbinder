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
package jp.co.opentone.bsol.linkbinder.service.common.impl;

import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.test.AbstractTestCase;
import jp.co.opentone.bsol.linkbinder.dao.impl.CompanyDaoImpl;
import jp.co.opentone.bsol.linkbinder.dao.impl.CorresponDaoImpl;
import jp.co.opentone.bsol.linkbinder.dao.impl.CorresponGroupDaoImpl;
import jp.co.opentone.bsol.linkbinder.dao.impl.ProjectDaoImpl;
import jp.co.opentone.bsol.linkbinder.dto.Company;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroup;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroupSummary;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroupUser;
import jp.co.opentone.bsol.linkbinder.dto.CorresponType;
import jp.co.opentone.bsol.linkbinder.dto.CorresponUserSummary;
import jp.co.opentone.bsol.linkbinder.dto.Project;
import jp.co.opentone.bsol.linkbinder.dto.ProjectDetailsSummary;
import jp.co.opentone.bsol.linkbinder.dto.ProjectSummary;
import jp.co.opentone.bsol.linkbinder.dto.ProjectUser;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCompanyCondition;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponUserSummaryCondition;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchProjectCondition;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.service.AbstractService;
import jp.co.opentone.bsol.linkbinder.service.common.HomeService;
import mockit.Mock;
import mockit.MockUp;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * ホーム画面のサービスのテストクラス.
 * @author opentone
 */
public class HomeServiceImplTest  extends AbstractTestCase {

    /**
     * テスト対象.
     */
    @Resource
    private HomeService service;

    @BeforeClass
    public static void testSetUp() {
        new MockAbstractService();
    }

    @AfterClass
    public static void testTeardown() {
        new MockAbstractService().tearDown();
    }

    /**
     * 後始末.
     */
    @After
    public void tearDown() {
        MockAbstractService.RET_USER = null;
        MockAbstractService.RET_SYSTEM_ADMIN = false;
        MockAbstractService.RET_PROJECT_USER = null;
        MockAbstractService.RET_ANY_GROUP_ADMIN = false;
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.service.common.impl.HomeServiceImpl#findProjects()}
     *  のためのテスト・メソッド.
     */
    @Test
    public void testFindProjects() throws Exception {
        List<ProjectSummary> expList = createProjectList();

        User expUser = new User();
        expUser.setEmpNo("ZZA01");
        MockAbstractService.RET_USER = expUser;
        MockAbstractService.RET_SYSTEM_ADMIN = false;
        new MockUp<ProjectDaoImpl>() {
            @Mock List<ProjectSummary> findProjectSummary(SearchProjectCondition condition) {
                return expList;
            }
        };

        List<ProjectSummary> actual = service.findProjects();

        assertArrayEquals(expList.toArray(), actual.toArray());
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.service.common.impl.HomeServiceImpl#findProjects()}
     *  のためのテスト・メソッド.
     */
    @Test
    public void testFindProjectsSystemAdmin() throws Exception {
        List<ProjectSummary> expList = createProjectList();
        User expUser = new User();
        expUser.setEmpNo("ZZA01");

        // Mock準備
        MockAbstractService.RET_USER = expUser;
        MockAbstractService.RET_SYSTEM_ADMIN = true;
        new MockUp<ProjectDaoImpl>() {
            @Mock List<ProjectSummary> findProjectSummary(SearchProjectCondition condition) {
                return expList;
            }
        };

        List<ProjectSummary> actual = service.findProjects();

        assertArrayEquals(expList.toArray(), actual.toArray());
    }

    /**
     * {@link HomeServiceImpl#findProjectDetails(String)}のテスト・メソッド.
     * SystemAdmin.
     */
    @Test
    public void testFindProjectDetailsSystemAdmin01() throws Exception {
        String projectId = "PJ1";

        User user = new User();
        user.setEmpNo("00001");

        List<CorresponGroupSummary> corresponGroupSummaryList = createCorresponGroupSummaryList();
        CorresponUserSummary corresponUserSummary = createCorresponUserSummary();
        List<Company> companyList = createCompanyList();
        CorresponGroup[] userGroups = createUserCorresponGroups();

        // Mock準備
        MockAbstractService.RET_SYSTEM_ADMIN = true;
        MockAbstractService.RET_ANY_GROUP_ADMIN = true;
        MockAbstractService.RET_USER = user;
        new MockUp<CorresponDaoImpl>() {
            @Mock List<CorresponGroupSummary> findCorresponGroupSummary(String projectId,
                    CorresponGroup[] userGroups) {
                return corresponGroupSummaryList;
            }
            @Mock CorresponUserSummary findCorresponUserSummary(
                    SearchCorresponUserSummaryCondition condition) {
                return corresponUserSummary;
            }
        };
        new MockUp<CompanyDaoImpl>() {
            @Mock List<Company> find(SearchCompanyCondition condition) {
                return companyList;
            }
        };
        new MockUp<CorresponGroupDaoImpl>() {
            @Mock List<CorresponGroupUser> findByEmpNo(String projectId, String empNo) {
                return createCorresponGroupUserList(userGroups);
            }
        };

        ProjectDetailsSummary actual = service.findProjectDetails(projectId);

        assertEquals(corresponGroupSummaryList, actual.getCorresponGroupSummary());
        assertArrayEquals(userGroups, actual.getUserRelatedCorresponGroups());
        assertEquals(corresponUserSummary, actual.getCorresponUserSummary());
    }

    /**
     * {@link HomeServiceImpl#findProjectDetails(String, boolean)}のテスト・メソッド.
     * SystemAdmin.
     */
    @Test
    public void testFindProjectDetailsSystemAdmin02() throws Exception {
        String projectId = "PJ1";

        User user = new User();
        user.setEmpNo("00001");

        List<CorresponGroupSummary> corresponGroupSummaryList = createCorresponGroupSummaryList();
        CorresponUserSummary corresponUserSummary = createCorresponUserSummary();
        List<Company> companyList = createCompanyList();
        CorresponGroup[] userGroups = createUserCorresponGroups();

        // Mock準備
        MockAbstractService.RET_SYSTEM_ADMIN = true;
        MockAbstractService.RET_ANY_GROUP_ADMIN = true;
        MockAbstractService.RET_USER = user;
        new MockUp<CorresponDaoImpl>() {
            @Mock List<CorresponGroupSummary> findCorresponGroupSummary(String projectId,
                    CorresponGroup[] userGroups) {
                return corresponGroupSummaryList;
            }
            @Mock CorresponUserSummary findCorresponUserSummary(
                    SearchCorresponUserSummaryCondition condition) {
                return corresponUserSummary;
            }
        };
        new MockUp<CompanyDaoImpl>() {
            @Mock List<Company> find(SearchCompanyCondition condition) {
                return companyList;
            }
        };
        new MockUp<CorresponGroupDaoImpl>() {
            @Mock List<CorresponGroupUser> findByEmpNo(String projectId, String empNo) {
                return createCorresponGroupUserList(userGroups);
            }
        };

        ProjectDetailsSummary actual = service.findProjectDetails(projectId, false);

        assertEquals(corresponGroupSummaryList, actual.getCorresponGroupSummary());
        assertArrayEquals(userGroups, actual.getUserRelatedCorresponGroups());
        assertEquals(corresponUserSummary, actual.getCorresponUserSummary());
    }

    /**
     * {@link HomeServiceImpl#findProjectDetails(String)}のテスト・メソッド.
     * SystemAdmin以外.
     */
    @Test
    public void testFindProjectDetails01() throws Exception {
        String projectId = "PJ1";

        User user = new User();
        user.setEmpNo("00001");

        ProjectUser pUser = new ProjectUser();
        pUser.setUser(user);
        pUser.setProjectId(projectId);


        List<CorresponGroupSummary> corresponGroupSummaryList = createCorresponGroupSummaryList();
        CorresponUserSummary corresponUserSummary = createCorresponUserSummary();

        // Mock準備
        MockAbstractService.RET_USER = user;
        MockAbstractService.RET_PROJECT_USER = pUser;
        MockAbstractService.RET_ANY_GROUP_ADMIN = true;
        new MockUp<CorresponDaoImpl>() {
            @Mock List<CorresponGroupSummary> findCorresponGroupSummary(String projectId,
                    CorresponGroup[] userGroups) {
                return corresponGroupSummaryList;
            }
            @Mock CorresponUserSummary findCorresponUserSummary(
                    SearchCorresponUserSummaryCondition condition) {
                return corresponUserSummary;
            }
        };
        new MockUp<CorresponGroupDaoImpl>() {
            @Mock List<CorresponGroupUser> findByEmpNo(String projectId, String empNo) {
                return new ArrayList<>();
            }
        };


        ProjectDetailsSummary actual = service.findProjectDetails(projectId);

        assertEquals(corresponGroupSummaryList, actual.getCorresponGroupSummary());
        assertEquals(corresponUserSummary, actual.getCorresponUserSummary());
        assertEquals(0, actual.getUserRelatedCorresponGroups().length);
    }

    /**
     * {@link HomeServiceImpl#findProjectDetails(String, boolean)}のテスト・メソッド.
     * SystemAdmin以外.
     */
    @Test
    public void testFindProjectDetails02() throws Exception {
        String projectId = "PJ1";

        User user = new User();
        user.setEmpNo("00001");

        ProjectUser pUser = new ProjectUser();
        pUser.setUser(user);
        pUser.setProjectId(projectId);

        List<CorresponGroupSummary> corresponGroupSummaryList = createCorresponGroupSummaryList();
        CorresponUserSummary corresponUserSummary = createCorresponUserSummary();

        // Mock準備
        MockAbstractService.RET_USER = user;
        MockAbstractService.RET_PROJECT_USER = pUser;
        MockAbstractService.RET_ANY_GROUP_ADMIN = true;
        new MockUp<CorresponDaoImpl>() {
            @Mock List<CorresponGroupSummary> findCorresponGroupSummary(String projectId,
                    CorresponGroup[] userGroups) {
                return corresponGroupSummaryList;
            }
            @Mock CorresponUserSummary findCorresponUserSummary(
                    SearchCorresponUserSummaryCondition condition) {
                return corresponUserSummary;
            }
        };
        new MockUp<CorresponGroupDaoImpl>() {
            @Mock List<CorresponGroupUser> findByEmpNo(String projectId, String empNo) {
                return new ArrayList<>();
            }
        };


        ProjectDetailsSummary actual = service.findProjectDetails(projectId, false);

        assertEquals(corresponGroupSummaryList, actual.getCorresponGroupSummary());
        assertEquals(corresponUserSummary, actual.getCorresponUserSummary());
        assertEquals(0, actual.getUserRelatedCorresponGroups().length);
    }

    /**
     * {@link HomeServiceImpl#findProjectDetails(String)}のテスト・メソッド.
     * 引数Null.
     */
    @Test(expected=IllegalArgumentException.class)
    public void testFindProjectDetailsNull01() throws Exception {
        service.findProjectDetails(null);
    }

    /**
     * {@link HomeServiceImpl#findProjectDetails(String, boolean)}のテスト・メソッド.
     * 引数Null.
     */
    @Test(expected=IllegalArgumentException.class)
    public void testFindProjectDetailsNull02() throws Exception {
        service.findProjectDetails(null, false);
    }

    /**
     * {@link HomeServiceImpl#findProjectDetails(String)}のテスト・メソッド.
     * プロジェクトに属していない.
     */
    @Test
    public void testFindProjectDetailsNoProjectUser01() throws Exception {
        String projectId = "PJ1";

        User user = new User();
        user.setEmpNo("00001");
        MockAbstractService.RET_USER = user;

        try {
            service.findProjectDetails(projectId);
        } catch (ServiceAbortException e) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_PROJECT_DIFF,
                         e.getMessageCode());
        }
    }

    /**
     * {@link HomeServiceImpl#findProjectDetails(String, boolean)}のテスト・メソッド.
     * プロジェクトに属していない.
     */
    @Test
    public void testFindProjectDetailsNoProjectUser02() throws Exception {
        String projectId = "PJ1";

        User user = new User();
        user.setEmpNo("00001");
        MockAbstractService.RET_USER = user;

        try {
            service.findProjectDetails(projectId, false);
        } catch (ServiceAbortException e) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_PROJECT_DIFF,
                         e.getMessageCode());
        }
    }

    private List<ProjectSummary> createProjectList() {
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
        project = new Project();
        project.setProjectId("PJ4");
        summary = new ProjectSummary();
        summary.setProject(project);
        summary.setAttentionCount(31L);
        summary.setPersonInChargeCount(32L);
        summary.setCcCount(33L);
        list.add(summary);

        return list;
    }

    private List<CorresponGroupSummary> createCorresponGroupSummaryList() {
        List<CorresponGroupSummary> list = new ArrayList<CorresponGroupSummary>();

        CorresponGroupSummary summary = new CorresponGroupSummary();
        CorresponType corresponType = new CorresponType();
        corresponType.setCorresponType("Type A");
        summary.setCorresponType(corresponType);
        summary.setToCount(1L);
        summary.setCcCount(2L);
        list.add(summary);

        summary = new CorresponGroupSummary();
        corresponType = new CorresponType();
        corresponType.setCorresponType("Type B");
        summary.setCorresponType(corresponType);
        summary.setToCount(11L);
        summary.setCcCount(22L);
        list.add(summary);

        summary = new CorresponGroupSummary();
        corresponType = new CorresponType();
        corresponType.setCorresponType("Type C");
        summary.setCorresponType(corresponType);
        summary.setToCount(111L);
        summary.setCcCount(222L);
        list.add(summary);

        return list;
    }

    private CorresponUserSummary createCorresponUserSummary() {
        CorresponUserSummary summary = new CorresponUserSummary();
        summary.setDraftCount(1L);
        summary.setRequestForCheckCount(2L);
        summary.setRequestForApproveCount(3L);
        summary.setDeniedCount(4L);
        summary.setOpenCount(5L);
        summary.setClosedCount(6L);
        summary.setCanceledCount(7L);
        summary.setAttentionCount(8L);
        summary.setPersonInChargeCount(9L);
        summary.setCcCount(10L);

        return summary;
    }

    private List<Company> createCompanyList() {
        List<Company> list = new ArrayList<Company>();

        Company company = new Company();
        company.setId(1L);
        company.setName("Company Name A");
        list.add(company);

        company = new Company();
        company.setId(2L);
        company.setName("Company Name B");
        list.add(company);

        company = new Company();
        company.setId(3L);
        company.setName("Company Name C");
        list.add(company);

        return list;
    }

    private List<CorresponGroup> createCorresponGroupList() {
        List<CorresponGroup> list = new ArrayList<CorresponGroup>();

        CorresponGroup group = new CorresponGroup();
        group.setId(1L);
        group.setName("Group Name A");
        list.add(group);

        group = new CorresponGroup();
        group.setId(2L);
        group.setName("Group Name B");
        list.add(group);

        group = new CorresponGroup();
        group.setId(3L);
        group.setName("Group Name C");
        list.add(group);

        return list;
    }

    private CorresponGroup[] createUserCorresponGroups() {
        return createCorresponGroupList().toArray(new CorresponGroup[0]);
    }

    private List<CorresponGroupUser> createCorresponGroupUserList(CorresponGroup[] groups) {
        List<CorresponGroupUser> list = new ArrayList<CorresponGroupUser>();

        for (CorresponGroup group : groups) {
            CorresponGroupUser gUser = new CorresponGroupUser();
            gUser.setCorresponGroup(group);
            list.add(gUser);
        }

        return list;
    }

    public static class MockAbstractService extends MockUp<AbstractService> {
        static User RET_USER;
        static boolean RET_SYSTEM_ADMIN;
        static ProjectUser RET_PROJECT_USER;
        static boolean RET_ANY_GROUP_ADMIN;

        @Mock
        public User getCurrentUser() {
            return RET_USER;
        }

        @Mock
        public boolean isSystemAdmin(User user) {
            return RET_SYSTEM_ADMIN;
        }

        @Mock
        public boolean isProjectAdmin(User user, String projectId) {
            return "PROJECT_ADMIN".equals(projectId);
        }

        @Mock
        public ProjectUser findProjectUser(String projectId, String empNo) {
            return RET_PROJECT_USER;
        }

        @Mock
        public boolean isAnyGroupAdmin(User user, String projectId) {
            if ("NO_DISCIPLINE".equals(projectId)) {
                return false;
            }
            return RET_ANY_GROUP_ADMIN;
        }
    }
}
