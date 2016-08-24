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
package jp.co.opentone.bsol.linkbinder.view.filter;

import java.io.IOException;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import jp.co.opentone.bsol.linkbinder.dto.code.ForLearning;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import jp.co.opentone.bsol.framework.core.exception.ApplicationFatalRuntimeException;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.linkbinder.Constants;
import jp.co.opentone.bsol.linkbinder.dto.LoginUserInfo;
import jp.co.opentone.bsol.linkbinder.dto.Project;
import jp.co.opentone.bsol.linkbinder.dto.ProjectSummary;
import jp.co.opentone.bsol.linkbinder.dto.ProjectUser;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchUserCondition;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.service.admin.ProjectCustomSettingService;
import jp.co.opentone.bsol.linkbinder.service.admin.UserService;
import jp.co.opentone.bsol.linkbinder.service.common.HomeService;
import jp.co.opentone.bsol.linkbinder.view.IllegalUserLoginException;
import jp.co.opentone.bsol.linkbinder.view.LoginUserInfoHolder;
import jp.co.opentone.bsol.linkbinder.view.common.module.redirect.RedirectProcessParameterKey;


/**
 * ユーザーのログイン状態を管理する{@link Filter}.
 * @author opentone
 */
public class ProjectSwitchFilter extends AbstractFilter {

    /* (non-Javadoc)
     * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        super.init(filterConfig);
    }

    /* (non-Javadoc)
     * @see javax.servlet.Filter#destroy()
     */
    @Override
    public void destroy() {
        super.destroy();
    }

    private Project getProjectAlreadyLoggedIn(HttpSession session, String projectId) {
        LoginUserInfoHolder h = getLoginUserInfoHolder(session);
        Project project = null;
        if (StringUtils.isNotEmpty(projectId)) {
            project = h.getLoginProjectInfo(projectId);
        }
        return project;
    }

    private boolean isProjectNotLoggedIn(String projectId, Project project) {
        return StringUtils.isNotEmpty(projectId)
                && (project == null || !project.getProjectId().equals(projectId));
    }

    /* (non-Javadoc)
     * @see javax.servlet.Filter
     *          #doFilter(javax.servlet.ServletRequest,
     *                    javax.servlet.ServletResponse,
     *                    javax.servlet.FilterChain)
     */
    public void doFilter(ServletRequest request, ServletResponse response,
        FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession session = req.getSession(false);
        String uri = req.getRequestURI();
        String projectId = req.getParameter(RedirectProcessParameterKey.PROJECT_ID);

        /*
         * 1.ログイン状態の管理が不要なページはリクエストを処理して即終了
         * 2.セッションが無効な場合、プロジェクト切替処理を行わず終了
         *   通常はLoginFilterでハンドリングされログイン画面がリクエストされている
         * 3.リダイレクト時のプロジェクト設定はリダイレクトモジュールで行うため終了
         */
        if (isIgnore(uri)
            || (session == null)
            || isRedirectRequest(session)) {
            chain.doFilter(request, response);
            return;
        }

        LoginUserInfoHolder h = getLoginUserInfoHolder(session);
        Project project = getProjectAlreadyLoggedIn(session, projectId);
        if (isProjectNotLoggedIn(projectId, project)) {
            project = getProject(session, projectId);
            if (project == null) {
                // 指定されたプロジェクトが存在しない、もしくは見れない場合、汎用エラーページを表示
                session.setAttribute("errorMessageCode",
                    ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_PROJECT_DIFF);
                redirectTo(req, res, errorPage);
                return;
            }

            ProjectUser pu = getProjectUser(session, projectId, h.getUserId());
            LoginUserInfo i = new LoginUserInfo();
            i.setLoginUser(h.getLoginUser());
            i.setLoginProject(project);
            i.setProjectUser(pu);
            try {
                h.addLoginProject(i);
            } catch (IllegalUserLoginException e) {
                throw new ApplicationFatalRuntimeException(e);
            }
        }

        chain.doFilter(request, response);
    }

    private Project getProject(HttpSession session, String projectId) {
        HomeService homeService = getHomeService(session);
        ProjectCustomSettingService projectCustomSettingService
                = getProjectCustomSettingService(session);

        try {
            /**プロジェクトの妥当性検査.*/
            List<ProjectSummary> projectSummaries = homeService.findProjects(ForLearning.NORMAL);
            for (ProjectSummary projectSummary : projectSummaries) {
                if (projectSummary.getProject().getProjectId().equals(projectId)) {
                    // プロジェクトカスタム設定情報をプロジェクトに設定
                    Project p = projectSummary.getProject();
                    p.setProjectCustomSetting(
                            projectCustomSettingService.find(p.getProjectId(), false));
                    return p;
                }
            }
            return null;
        } catch (ServiceAbortException e) {
            throw new ApplicationFatalRuntimeException(e);
        }
    }

    private ProjectUser getProjectUser(HttpSession session, String projectId, String empNo) {
        SearchUserCondition condition = new SearchUserCondition();
        condition.setProjectId(projectId);
        condition.setEmpNo(empNo);

        UserService userService = getUserService(session);
        ProjectUser pu = null;
        List<ProjectUser> list;
        try {
            list = userService.search(condition);
            if (list.size() > 0) {
                pu = list.get(0);
            }
            return pu;
        } catch (ServiceAbortException e) {
            throw new ApplicationFatalRuntimeException(e);
        }
    }

    /**
     * リダイレクトによるリクエストかどうかを判定した結果を返す.
     * リダイレクト先の画面IDがセットされているかどうかで判定を行う.
     *
     * @param session セッション
     * @return リダイレクトによるリクエストの場合true
     */
    private boolean isRedirectRequest(HttpSession session) {
        return session != null && session.getAttribute(Constants.KEY_REDIRECT_SCREEN_ID) != null;
    }

    private UserService getUserService(HttpSession session) {
        ApplicationContext applicationContext = createApplicationContext(session);
        return applicationContext.getBean(UserService.class);
    }

    private HomeService getHomeService(HttpSession session) {
        ApplicationContext applicationContext = createApplicationContext(session);
        return applicationContext.getBean(HomeService.class);
    }

    private ProjectCustomSettingService getProjectCustomSettingService(HttpSession session) {
        ApplicationContext applicationContext = createApplicationContext(session);
        return applicationContext.getBean(ProjectCustomSettingService.class);
    }

    private LoginUserInfoHolder getLoginUserInfoHolder(HttpSession session) {
        ApplicationContext applicationContext = createApplicationContext(session);
        return applicationContext.getBean(LoginUserInfoHolder.class);
    }

    private ApplicationContext createApplicationContext(HttpSession session) {
        ServletContext context = session.getServletContext();
        ApplicationContext applicationContext =
            WebApplicationContextUtils.getRequiredWebApplicationContext(context);

        return applicationContext;
    }
}
