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

/*
 * RSSServlet.java
 */
package jp.co.opentone.bsol.linkbinder.view.servlet;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.core.util.ArgumentValidator;
import jp.co.opentone.bsol.framework.web.view.util.RequestUtil;
import jp.co.opentone.bsol.linkbinder.service.rss.RSSService;

/**
 * RSSを返却するサーブレット.
 * @author opentone
 */
public class RSSServlet extends HttpServlet {
    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -6349285526293740803L;

    /**
     * logger.
     */
    private static Logger log = LoggerFactory.getLogger(RSSServlet.class);

     /**
     * RSSのCONTENT_TYPE.
     */
    private static final String RSS_CONTENT_TYPE = "application/rss+xml";

    /**
     * RSSの文字コード.
     */
    private static final String RSS_CHARSET = "UTF-8";

    /**
     * queryパラメータ ユーザID.
     */
    private static final String QUERY_USER_ID = "userId";

    /**
     * コンストラクタ.
     */
    public RSSServlet() {
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
        if (log.isDebugEnabled()) {
            log.debug("userId[{}] baseURL[{}]"
                , request.getParameter(QUERY_USER_ID)
                , getBaseURL(request));
        }

        // RSS出力処理
        try {
            byte[] rss = getRSS(request);
            response.setContentType(RSS_CONTENT_TYPE);
            ServletOutputStream sos = response.getOutputStream();
            sos.write(rss);
            sos.close();
        } catch (IllegalArgumentException e) {
            log.error("Forbidden", e);
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
        } catch (ServiceAbortException e) {
            log.error("InternalServerError", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * RSSを取得する.
     * @param request リクエスト
     * @return RSSService
     * @throws IllegalArgumentException query引数異常
     * @throws UnsupportedEncodingException 文字コード異常(発生しない)
     * @throws ServiceAbortException 処理例外
     */
    private byte[] getRSS(HttpServletRequest request)
        throws IllegalArgumentException, UnsupportedEncodingException, ServiceAbortException {
        String userId = request.getParameter(QUERY_USER_ID);
        ArgumentValidator.validateNotEmpty(userId);

        ServletContext context = request.getSession().getServletContext();
        ApplicationContext applicationContext =
            WebApplicationContextUtils.getRequiredWebApplicationContext(context);
        RSSService rssService = (RSSService) applicationContext.getBean("RSSServiceImpl");
        return rssService.getRSS(userId, getBaseURL(request)).getBytes(RSS_CHARSET);
    }

    /**
     * リクエストらBaseURLを取得する.
     * (http://localhost:8080/feed/correspon?correspon=ZZB00&feedKey=12345678890
     *  → http://localhost:8080/)
     * @param request リクエスト
     * @return BaseURL
     */
    private String getBaseURL(HttpServletRequest request) {
        String base = RequestUtil.getBaseURL(request);
        if (base.startsWith("https")) {
            base = base.replaceFirst("https", "http");
        }
        if (base.charAt(base.length() - 1) != '/') {
            base = base + '/';
        }
        return base;
    }
 }
