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

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Webアプリケーションルート配下の各種リソースを配信する.
 * <p>
 * 対象となるのは次のディレクトリ配下のもの.
 * <ul>
 * <li>javascript</li>
 * <li>stylesheet</li>
 * </ul>
 * </p>
 *
 * @author opentone
 */
public class WebResourceServlet extends HttpServlet {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 2014590036921683048L;
    /** logger. */
    private static final Logger log = LoggerFactory.getLogger(WebResourceServlet.class);

    public static final String DEFAULT_JAVASCRIPT = "javascript";
    public static final String DEFAULT_STYLESHEET = "stylesheet";

    public static final String REGEX_PATH = "/.+/%s/(.+)(\\?.+)?";

    private String javascript = DEFAULT_JAVASCRIPT;
    private String stylesheet = DEFAULT_STYLESHEET;

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServlet
     * #doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
        IOException {
        String path = req.getPathInfo();
        if (isJavascript(path)) {
            serveResource(req, resp, javascript, path);
        } else if (isStylesheet(path)) {
            serveResource(req, resp, stylesheet, path);
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    protected boolean isJavascript(String path) {
        return path.matches(String.format(REGEX_PATH, javascript));
    }
    protected boolean isStylesheet(String path) {
        return path.matches(String.format(REGEX_PATH, stylesheet));
    }

    protected void serveResource(
        HttpServletRequest req, HttpServletResponse resp, String root, String path)
            throws ServletException, IOException {
        String filename = parse(root, path);
        if (StringUtils.isNotEmpty(filename)) {
            log.debug("serve {}", filename);
            setHeader(resp, path);

            String realPath = String.format("/%s/%s", root, filename);
            write(resp, realPath);
        }
    }

    protected void write(HttpServletResponse resp, String path)
            throws ServletException, IOException {
        InputStream in = getServletContext().getResourceAsStream(path);
        if (in != null) {
            try {
                OutputStream o = resp.getOutputStream();
                byte[] b = new byte[4096];
                int i = 0;
                while ((i = in.read(b, 0, b.length)) != -1) {
                    o.write(b, 0, i);
                }
            } finally {
                in.close();
            }
        }
    }

    protected void setHeader(HttpServletResponse response, String path) {
        response.setDateHeader("Last-Modified", System.currentTimeMillis());

        Calendar expires = Calendar.getInstance();
        expires.add(Calendar.DAY_OF_YEAR, 7);
        response.setDateHeader("Expires", expires.getTimeInMillis());

        //12 hours: 43200 = 60s * 60 * 12
        response.setHeader("Cache-Control", "max-age=43200");
        response.setHeader("Pragma", "");

        String contentType = getContentType(path);
        if (StringUtils.isNotEmpty(contentType)) {
            response.setContentType(contentType);
        }
    }

    protected String parse(String root, String path) {
        Pattern p = Pattern.compile(String.format(REGEX_PATH, root));
        Matcher m = p.matcher(path);
        return m.matches() ? m.group(1) : null;
    }

    protected String getContentType(String path) {
        if (isJavascript(path)) {
            return "text/javascript";
        } else if (isStylesheet(path)) {
            return "text/css";
        } else {
            return StringUtils.EMPTY;
        }
    }
}
