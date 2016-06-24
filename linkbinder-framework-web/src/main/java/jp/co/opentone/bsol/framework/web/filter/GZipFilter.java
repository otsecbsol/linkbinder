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
package jp.co.opentone.bsol.framework.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

/**
 * レスポンスをGzip形式で圧縮するFilter.
 * @author opentone
 */
public class GZipFilter implements Filter {

    /** このフィルタが既に処理したことを表す. */
    public static final String FILTERED = GZipFilter.class.getName() + "_filtered";

    /* (non-Javadoc)
     * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // 何もしない
    }

    /* (non-Javadoc)
     * @see javax.servlet.Filter#doFilter(
     *      javax.servlet.ServletRequest,
     *      javax.servlet.ServletResponse, javax.servlet.FilterChain)
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        if (isFiltered(req)) {
            chain.doFilter(req, res);
            return;
        }
        req.setAttribute(FILTERED, "true");

        if (!isCompressionSupported(req)) {
            chain.doFilter(req, res);
            return;
        }

        GZipResponseWrapper wrapper = new GZipResponseWrapper(res);
        try {
            chain.doFilter(req, wrapper);
        } finally {
            wrapper.finish();
        }
    }

    private boolean isFiltered(HttpServletRequest request) {
        return request.getAttribute(FILTERED) != null;
    }

    private boolean isCompressionSupported(HttpServletRequest request) {
        String acceptEncoding = request.getHeader("accept-encoding");
        return StringUtils.isNotEmpty(acceptEncoding)
                && acceptEncoding.contains("gzip");
    }

    /* (non-Javadoc)
     * @see javax.servlet.Filter#destroy()
     */
    @Override
    public void destroy() {
    }
}
