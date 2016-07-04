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
package jp.co.opentone.bsol.linkbinder.view.servlet;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import jp.co.opentone.bsol.linkbinder.view.logo.ProjectLogo;
import jp.co.opentone.bsol.linkbinder.view.logo.ProjectLogoManager;

/**
 * プロジェクトロゴ画像を返却するサーブレット.
 * @author opentone
 */
public class ProjectLogoServlet extends HttpServlet {
    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 7687906472272248579L;

    /**
     * logger.
     */
    private static Logger log = LoggerFactory.getLogger(ProjectLogoServlet.class);

    /**
     * プロジェクトロゴ管理.
     */
    //@Resource
    private ProjectLogoManager projectLogoManager;


    /**
     * HTTPリクエスト引数(プロジェクトID).
     */
    private static final String PROJECT_ID = "projectId";

    /**
     * HTTP Last-Modifiedヘッダ.
     */
    private static final String LAST_MODIFIED = "Last-Modified";

    /**
     * HTTP Last-Modifiedヘッダ.
     */
    private static final String IF_MODIFIED_SINCE = "If-Modified-Since";


    /**
     * PNGのcontent-type.
     */
    private static final String CONTENT_TYPE_PNG = "image/png";

    /**
     * コンストラクタ.
     */
    public ProjectLogoServlet() {
        super();
    }

    /**
     * HTTPリクエストを受信する.
     *
     * @param request
     *           リクエスト
     * @param response
     *           レスポンス
     * @throws ServletException
     *           サーブレットの例外
     * @throws IOException
     *           入出力例外
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
                                                throws ServletException, IOException {
        String projectId = request.getParameter(PROJECT_ID);
        String ifModifiedSince = request.getHeader(IF_MODIFIED_SINCE);

        if (log.isDebugEnabled()) {
            log.debug("projectId[" + projectId + "]");
            log.debug("ifModifiedSince[" + ifModifiedSince + "]");
        }

        if (!projectLogoManager.isModified(projectId, ifModifiedSince)) {
            response.sendError(HttpServletResponse.SC_NOT_MODIFIED);
        } else {
            ProjectLogo projectLogo = projectLogoManager.get(projectId);
            if (log.isDebugEnabled()) {
                if (projectLogo != null) {
                    log.debug("projectLogo.getImage.length[" + projectLogo.getImage().length + "]");
                    log.debug("projectLogo.getLastModified["
                        + new Date(projectLogo.getLastModified()) + "]");
                } else {
                    log.debug("projectLogo is null");
                }
            }

            if (projectLogo != null) {
                response.setContentType(CONTENT_TYPE_PNG);
                response.setDateHeader(LAST_MODIFIED, projectLogo.getLastModified());
                ServletOutputStream sos = response.getOutputStream();
                sos.write(projectLogo.getImage());
                sos.close();
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        }
    }

    /* (non-Javadoc)
     * @see javax.servlet.GenericServlet#init(javax.servlet.ServletConfig)
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        WebApplicationContext applicationContext =
            WebApplicationContextUtils.getRequiredWebApplicationContext(config.getServletContext());
        this.projectLogoManager =
            (ProjectLogoManager) applicationContext.getBean("projectLogoManager");
    }

 }
