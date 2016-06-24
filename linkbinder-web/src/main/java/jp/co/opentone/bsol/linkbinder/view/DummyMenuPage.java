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
package jp.co.opentone.bsol.linkbinder.view;

import java.util.List;

import javax.annotation.ManagedBean;
import javax.annotation.Resource;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import jp.co.opentone.bsol.framework.core.exception.ApplicationFatalRuntimeException;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.web.extension.jsf.annotation.Initialize;
import jp.co.opentone.bsol.framework.web.extension.jsf.annotation.Transfer;
import jp.co.opentone.bsol.linkbinder.Constants;
import jp.co.opentone.bsol.linkbinder.action.AbstractAction;
import jp.co.opentone.bsol.linkbinder.dto.Project;
import jp.co.opentone.bsol.linkbinder.dto.ProjectUser;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchProjectCondition;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchUserCondition;
import jp.co.opentone.bsol.linkbinder.service.admin.ProjectService;
import jp.co.opentone.bsol.linkbinder.service.admin.UserService;

/**
 * 開発中に利用するメニュー.
 * @author opentone
 */
@Component
@ManagedBean
@Scope("request")
public class DummyMenuPage extends AbstractPage {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 1516481062923223347L;

    // 開発用ソースコードなのでCheckStyleの警告を抑止
    //CHECKSTYLE:OFF
    @Resource
    private UserService userService;

    @Resource
    private ProjectService projectService;

    @Transfer
    private String projectId;

    @Transfer
    private List<Project> projects;

    @Transfer
    private List<SelectItem> projectList;

    @Transfer
    private DataModel<?> users;
    //CHECKSTYLE:ON

    /**
     * 空のインスタンスを生成する.
     */
    public DummyMenuPage() {
    }

    @Initialize
    public void initialize() {
        handler.handleAction(new InitializeAction(this));
    }

    public String changeProject() {
        handler.handleAction(new SelectProjectAction(this));
        return null;
    }

    public String login() {
        if (handler.handleAction(new LoginAction(this))) {
            return "correspon/corresponIndex";
        } else {
            return null;
        }
    }

    public String loginAsAdmin() {
        if (handler.handleAction(new AdminLoginAction(this))) {
            return "admin/adminHome";
        } else {
            return null;
        }
    }

    public String loginAsProjectAdmin() {
        if (handler.handleAction(new ProjectAdminLoginAction(this))) {
            return "admin/projectAdminHome";
        } else {
            return null;
        }
    }

    /**
     * @param projectId
     *            the projectId to set
     */
    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    /**
     * @return the projectId
     */
    public String getProjectId() {
        return projectId;
    }

    /**
     * @param projectList
     *            the projectList to set
     */
    public void setProjectList(List<SelectItem> projectList) {
        this.projectList = projectList;
    }

    /**
     * @return the projectList
     */
    public List<SelectItem> getProjectList() {
        return projectList;
    }

    /**
     * @param users
     *            the users to set
     */
    public void setUsers(DataModel<?> users) {
        this.users = users;
    }

    /**
     * @return the users
     */
    public DataModel<?> getUsers() {
        return users;
    }

    /**
     * @param projects
     *            the projects to set
     */
    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }

    /**
     * @return the projects
     */
    public List<Project> getProjects() {
        return projects;
    }

    static class InitializeAction extends AbstractAction {
        /**
         * serialVersionUID.
         */
        private static final long serialVersionUID = -1520340982385594473L;
        /** page. */
        private DummyMenuPage page;

        public InitializeAction(DummyMenuPage page) {
            super(page);
            this.page = page;
        }

        public void execute() throws ServiceAbortException {
            clearLoginInfo();

            List<Project> projects
                = page.projectService.searchPagingList(
                        new SearchProjectCondition()).getProjectList();
            page.projects = projects;
            page.setProjectList(page.viewHelper.createSelectItem(projects, "projectId", "nameE"));

            if (!projects.isEmpty()) {
                if (projects.size() > 1) {
                    page.setProjectId(projects.get(1).getProjectId());
                } else {
                    page.setProjectId(projects.get(0).getProjectId());
                }
                page.changeProject();
            }
        }

        private void clearLoginInfo() {
            page.viewHelper.removeSessionValue(Constants.KEY_PROJECT);
            page.viewHelper.removeSessionValue(Constants.KEY_PROJECT_USER);
            User login = page.getCurrentUser();
            if (login != null) {
                login.setNameE(null);
                login.setSecurityLevel(null);
            }
        }
    }

    static class SelectProjectAction extends AbstractAction {
        /**
         * serialVersionUID.
         */
        private static final long serialVersionUID = -23707579994245530L;
        /** page. */
        private DummyMenuPage page;

        public SelectProjectAction(DummyMenuPage page) {
            super(page);
            this.page = page;
        }

        public void execute() throws ServiceAbortException {
            SearchUserCondition c = new SearchUserCondition();
            c.setProjectId(page.getProjectId());
            List<ProjectUser> users = page.userService.search(c);
            for (ProjectUser pu : users) {
                pu.setUser(page.userService.findByEmpNo(pu.getUser().getEmpNo()));
            }
            page.setUsers(new ListDataModel<ProjectUser>(users));
        }
    }

    static class LoginAction extends AbstractAction {
        /**
         * serialVersionUID.
         */
        private static final long serialVersionUID = 1611925504084413329L;
        /** page. */
        private DummyMenuPage page;

        public LoginAction(DummyMenuPage page) {
            super(page);
            this.page = page;
        }

        public void execute() throws ServiceAbortException {
            ProjectUser pu = (ProjectUser) page.getUsers().getRowData();

            for (Project p : page.projects) {
                if (p.getProjectId().equals(pu.getProjectId())) {
                    page.viewHelper.setSessionValue(Constants.KEY_PROJECT, p);
                }
            }
            page.viewHelper.setSessionValue(Constants.KEY_PROJECT_USER, pu);

            User login = page.getCurrentUser();
            try {
                PropertyUtils.copyProperties(login, pu.getUser());
            } catch (Exception e) {
                throw new ApplicationFatalRuntimeException(e);
            }
        }
    }

    static class AdminLoginAction extends AbstractAction {
        /**
         * serialVersionUID.
         */
        private static final long serialVersionUID = -7176860757735425322L;
        /** page. */
        private DummyMenuPage page;

        public AdminLoginAction(DummyMenuPage page) {
            super(page);
            this.page = page;
        }

        public void execute() throws ServiceAbortException {
            ProjectUser pu = (ProjectUser) page.getUsers().getRowData();
            User login = page.getCurrentUser();
            try {
                PropertyUtils.copyProperties(login, pu.getUser());
            } catch (Exception e) {
                throw new ApplicationFatalRuntimeException(e);
            }
        }
    }

    static class ProjectAdminLoginAction extends AbstractAction {
        /**
         * serialVersionUID.
         */
        private static final long serialVersionUID = -5491610046756221971L;
        /** page. */
        private DummyMenuPage page;

        public ProjectAdminLoginAction(DummyMenuPage page) {
            super(page);
            this.page = page;
        }

        public void execute() throws ServiceAbortException {
            ProjectUser pu = (ProjectUser) page.getUsers().getRowData();

            for (Project p : page.projects) {
                if (p.getProjectId().equals(pu.getProjectId())) {
                    page.viewHelper.setSessionValue(Constants.KEY_PROJECT, p);
                }
            }
            page.viewHelper.setSessionValue(Constants.KEY_PROJECT_USER, pu);

            User login = page.getCurrentUser();
            try {
                PropertyUtils.copyProperties(login, pu.getUser());
            } catch (Exception e) {
                throw new ApplicationFatalRuntimeException(e);
            }
        }
    }
}
