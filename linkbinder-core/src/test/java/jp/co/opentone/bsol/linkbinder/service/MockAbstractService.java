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
package jp.co.opentone.bsol.linkbinder.service;

import java.util.List;

import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.linkbinder.dto.Correspon;
import jp.co.opentone.bsol.linkbinder.dto.Project;
import jp.co.opentone.bsol.linkbinder.dto.ProjectUser;
import jp.co.opentone.bsol.linkbinder.dto.User;
import mockit.Mock;
import mockit.MockUp;

public class MockAbstractService extends MockUp<AbstractService> {

    public static String CURRENT_PROJECT_ID;
    public static Project CURRENT_PROJECT;
    public static User CURRENT_USER;
    public static boolean IS_SYSTEM_ADMIN;
    public static boolean IS_PROJECT_ADMIN;
    public static boolean IS_ANY_GROUP_ADMIN;
    public static List<Long> IS_GROUP_ADMIN;
    public static List<Project> ACCESSIBLE_PROJECTS;
    public static ProjectUser PROJECT_USER;
    public static ProjectUser CURRENT_PROJECT_USER;
    public static ServiceAbortException VALIDATE_PROJECT_ID_EXCEPTION;
    public static String BASE_URL;

    @Mock
    public String getContextURL() {
        return "/";
    }

    @Mock
    public String getCurrentProjectId() {
        return CURRENT_PROJECT_ID;
    }

    @Mock
    public Project getCurrentProject() {
        return CURRENT_PROJECT;
    }

    @Mock
    public User getCurrentUser() {
        return CURRENT_USER;
    }

    @Mock
    public boolean isSystemAdmin(User user) {
        return IS_SYSTEM_ADMIN;
    }

    @Mock
    public boolean isProjectAdmin(User user, String projectId) {
        return IS_PROJECT_ADMIN;
    }

    @Mock
    public boolean isGroupAdmin(User user, Long corresponGroupId) {
        if (IS_GROUP_ADMIN == null) {
            return false;
        }
        return IS_GROUP_ADMIN.contains(corresponGroupId);
    }

    @Mock
    public boolean isAnyGroupAdmin(Correspon correspon) {
        return IS_ANY_GROUP_ADMIN;
    }

    @Mock
    public List<Project> getAccessibleProjects() {
        return ACCESSIBLE_PROJECTS;
    }

    @Mock
    public ProjectUser findProjectUser(String projectId, String empNo) {
        return PROJECT_USER;
    }

    @Mock
    public boolean isAnyGroupAdmin(String projectId) {
        return IS_ANY_GROUP_ADMIN;
    }

    @Mock
    public ProjectUser getCurrentProjectUser() {
        return CURRENT_PROJECT_USER;
    }

    @Mock
    public String getBaseURL() {
        return BASE_URL;
    }

    // public void validateProjectId(String projectId) throws
    // ServiceAbortException {
    // if (VALIDATE_PROJECT_ID_EXCEPTION != null) {
    // throw VALIDATE_PROJECT_ID_EXCEPTION;
    // }
    // }
}
