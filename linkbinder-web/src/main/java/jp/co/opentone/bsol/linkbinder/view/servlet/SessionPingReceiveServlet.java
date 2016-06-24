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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.co.opentone.bsol.framework.core.util.DateUtil;
import jp.co.opentone.bsol.linkbinder.Constants;
import jp.co.opentone.bsol.linkbinder.dto.User;

/**
 * 定期発行リクエスト受付サーブレット.
 *
 * @author opentone
 */
public class SessionPingReceiveServlet extends HttpServlet {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 8255605770876443878L;

    /** 区切り文字(/). */
    private static final String DELIMITER_SLASH = "/";

    /**
     * Referer取得キー.
     */
    private static final String KEY_HEADER_REFERER = "referer";

    /**
     * logger.
     */
    private static Logger log = LoggerFactory.getLogger(SessionPingReceiveServlet.class);

    public SessionPingReceiveServlet() {
    }

    /*
     * (non-Javadoc)
     * @see
     * javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest
     * , javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        // セッション取得
        HttpSession session = request.getSession(false);
        String userId = "-";
        if (session != null) {
            User currentUser = (User) session.getAttribute(Constants.KEY_CURRENT_USER);
            if (currentUser != null && currentUser.getUserId() != null) {
                userId = currentUser.getUserId();
            }
        }

        // リクエスト発行元画面名を取得
        String requestViewName = getRequestViewName(request);

        Object[] logParams =
                {userId, requestViewName,
                    DateUtil.convertDateToStringForView(new Date())};

        log.info("ping from {}, view:{} at {}", logParams);

        // ステータスコード200を返す
        response.setStatus(HttpServletResponse.SC_OK);

    }

    /**
     * リクエスト元画面名を取得する.
     * @param request リクエスト
     * @return リクエスト元画面名
     */
    private String getRequestViewName(HttpServletRequest request) {
        String requestUrl = request.getHeader(KEY_HEADER_REFERER);
        // クエリパラメーターが付与されている場合
        if (requestUrl.indexOf("?") != -1) {
            requestUrl = requestUrl.substring(0, requestUrl.indexOf("?"));
        }

        return requestUrl.substring(
            requestUrl.lastIndexOf(DELIMITER_SLASH) + DELIMITER_SLASH.length());
    }

}
