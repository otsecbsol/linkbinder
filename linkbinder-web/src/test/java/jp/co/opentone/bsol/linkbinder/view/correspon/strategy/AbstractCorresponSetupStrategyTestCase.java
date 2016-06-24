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
package jp.co.opentone.bsol.linkbinder.view.correspon.strategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;

import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.extension.jsf.FacesContextMock;
import jp.co.opentone.bsol.framework.test.AbstractTestCase;
import jp.co.opentone.bsol.linkbinder.dto.Correspon;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroup;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroupUser;
import jp.co.opentone.bsol.linkbinder.dto.Project;
import jp.co.opentone.bsol.linkbinder.dto.ProjectUser;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchUserCondition;
import jp.co.opentone.bsol.linkbinder.service.admin.impl.UserServiceImpl;
import jp.co.opentone.bsol.linkbinder.service.correspon.impl.CorresponServiceImpl;
import jp.co.opentone.bsol.linkbinder.view.AbstractPage;
import jp.co.opentone.bsol.linkbinder.view.correspon.CorresponEditPage;
import jp.co.opentone.bsol.linkbinder.view.correspon.util.CorresponEditMode;
import junit.framework.AssertionFailedError;
import mockit.Mock;
import mockit.MockUp;

/**
 * @author opentone
 */
//CHECKSTYLE:OFF
@Ignore
public class AbstractCorresponSetupStrategyTestCase extends AbstractTestCase {

    protected CorresponEditMode mode;

    @Resource
    protected CorresponEditPage page;
    protected CorresponSetupStrategy strategy;

    //  ログイン状態を表現するのに必要な情報
    protected Project currentProject;
    protected User currentUser;
    protected ProjectUser currentProjectUser;

    /**
     * ユーザーが所属する活動単位リスト.
     */
    protected List<CorresponGroup> userGroups;
    /**
     * Mockから返されるプロジェクトユーザーリスト.
     */
    protected List<ProjectUser> projectUsers;
    /**
     * Mockから返される活動単位所属ユーザーリスト.
     */
    protected List<CorresponGroupUser> corresponGroupUsers;


    public AbstractCorresponSetupStrategyTestCase(CorresponEditMode mode) {
        this.mode = mode;
    }

    @BeforeClass
    public static void testSetUp() {
        FacesContextMock.initialize();
        new MockAbstractPage();
        new MockCorresponService();
        new MockUserService();
    }

    @AfterClass
    public static void testTeardown() {
        new MockAbstractPage().tearDown();
        new MockCorresponService().tearDown();
        new MockUserService().tearDown();
        FacesContextMock.tearDown();
    }

    @Before
    public void setUp() throws Exception {
        currentProject = new Project();
        currentProject.setProjectId("PJ1");
        currentProject.setNameE("Test Project1");
        MockAbstractPage.RET_PROJECT = currentProject;

        currentUser = new User();
        currentUser.setEmpNo("ZZA01");
        currentUser.setNameE("Test User");
        MockAbstractPage.RET_USER = currentUser;

        currentProjectUser = new ProjectUser();
        currentProjectUser.setUser(currentUser);
        MockAbstractPage.RET_PROJECT_USER = currentProjectUser;

        strategy = CorresponSetupStrategy.getCorresponSetupStrategy(page, mode);

        projectUsers = createProjectUsers();
        MockUserService.RET_SEARCH = projectUsers;

        corresponGroupUsers = createCorresponGroupUsers();
        MockUserService.RET_SEARCH_CORRESPON_GROUP = corresponGroupUsers;
    }

    @After
    public void teardown() {
        page.setCorrespon(null);
        MockCorresponService.RET_FIND = null;
        MockCorresponService.RET_CORRESPON_MAP.clear();
        MockUserService.RET_SEARCH = null;
    }

    private List<ProjectUser> createProjectUsers() {
        List<ProjectUser> projectUsers = new ArrayList<ProjectUser>();
        ProjectUser projectUser = new ProjectUser();
        User user = new User();
        user.setEmpNo("00001");
        user.setNameE("UserEName");
        projectUser.setUser(user);
        projectUsers.add(projectUser);

        return projectUsers;
    }

    private List<CorresponGroupUser> createCorresponGroupUsers() {
        List<CorresponGroupUser> corresponGroupUsers = new ArrayList<CorresponGroupUser>();
        CorresponGroupUser corresponGroupUser = new CorresponGroupUser();

        List<CorresponGroup> corresponGroups = createCorresponGroups();
        corresponGroupUser.setCorresponGroup(corresponGroups.get(0));
        corresponGroupUsers.add(corresponGroupUser);

        return corresponGroupUsers;
    }

    private List<CorresponGroup> createCorresponGroups() {
        List<CorresponGroup> corresponGroups = new ArrayList<CorresponGroup>();

        CorresponGroup corresponGroup;
        corresponGroup = new CorresponGroup();
        corresponGroup.setId(1L);
        corresponGroup.setName("CorresponGroupName1");

        corresponGroups.add(corresponGroup);

        corresponGroup = new CorresponGroup();
        corresponGroup.setId(2L);
        corresponGroup.setName("CorresponGroupName2");

        corresponGroups.add(corresponGroup);

        return corresponGroups;
    }

    public static class MockAbstractPage extends MockUp<AbstractPage> {
        public static Project RET_PROJECT;
        public static String RET_PROJID;
        public static User RET_USER;
        public static ProjectUser RET_PROJECT_USER;
        public static boolean IS_SYSTEM_ADMIN;
        public static boolean IS_PROJECT_ADMIN;
        public static boolean IS_ANY_GROUP_ADMIN;

        @Mock
        public ProjectUser getCurrentProjectUser() {
            return RET_PROJECT_USER;
        }
        @Mock
        public User getCurrentUser() {
            return RET_USER;
        }

        @Mock
        public Project getCurrentProject() {
            return RET_PROJECT;
        }

        @Mock
        public String getCurrentProjectId() {
            if (RET_PROJID != null) {
                return RET_PROJID;
            }
            if (RET_PROJECT != null) {
                return RET_PROJECT.getProjectId();
            }
            return null;
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
        public boolean isAnyGroupAdmin(String checkProjectId) {
            return IS_ANY_GROUP_ADMIN;
        }
        @Mock
        public boolean isAnyGroupAdmin(Correspon c) {
            return IS_ANY_GROUP_ADMIN;
        }
    }

    public static class MockCorresponService extends MockUp<CorresponServiceImpl> {
        static Correspon RET_FIND;
        static ServiceAbortException EX_FIND_BY_ID;
        static Map<Long, Correspon> RET_CORRESPON_MAP = new HashMap<Long, Correspon>();

        @Mock
        public Correspon find(Long id) throws ServiceAbortException {
            if (EX_FIND_BY_ID != null) {
                throw EX_FIND_BY_ID;
            }
            Correspon result = new Correspon();
            Correspon ret;
            if (RET_FIND != null) {
                ret = RET_FIND;
            } else {
                ret = RET_CORRESPON_MAP.get(id);
            }
            try {
                PropertyUtils.copyProperties(result, ret);
            } catch (Exception e) {
                e.printStackTrace();
                throw new AssertionFailedError(e.getMessage());
            }
            return result;
        }
    }

    public static class MockUserService extends MockUp<UserServiceImpl> {
        static List<ProjectUser> RET_SEARCH;
        static List<CorresponGroupUser> RET_SEARCH_CORRESPON_GROUP;

        @Mock
        public List<ProjectUser> search(SearchUserCondition conditionUser)
            throws ServiceAbortException {
            return RET_SEARCH;
        }

        @Mock
        public List<CorresponGroupUser> searchCorrseponGroup(String projectId, String empNo)
            throws ServiceAbortException {
            return RET_SEARCH_CORRESPON_GROUP;
        }
    }
}
//CHECKSTYLE:ON
