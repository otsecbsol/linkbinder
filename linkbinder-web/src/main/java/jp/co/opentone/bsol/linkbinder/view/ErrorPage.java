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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

import javax.annotation.ManagedBean;
import javax.faces.context.FacesContext;
import javax.servlet.ServletException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import jp.co.opentone.bsol.framework.core.config.SystemConfig;
import jp.co.opentone.bsol.framework.core.message.Message;
import jp.co.opentone.bsol.framework.core.message.Messages;
import jp.co.opentone.bsol.framework.web.extension.jsf.annotation.Initialize;
import jp.co.opentone.bsol.framework.web.view.flash.Flash;

/**
 * 共通エラーページ.
 * @author opentone
 */
@Component
@ManagedBean
@Scope("request")
public class ErrorPage extends AbstractPage {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -6640814374134885856L;

    /** logger. */
    private static final Logger LOG = LoggerFactory.getLogger(ErrorPage.class);

    /**
     * クローズリンク表示フラグ取得キー.
     */
    public static final String KEY_SHOW_CLOSE_LINK = "error.show.closelink";

    /**
     * スタックトレース表示要否フラグ取得キー.
     */
    public static final String KEY_SHOW_STACKTRACE = "error.show.stacktrace";

    /**
     * セッションからエラーメッセージコードを取得するキー.
     */
    public static final String KEY_ERROR_MESSAGE_CODE = "errorMessageCode";

    /**
     * ページの初期化を行う.
     */
    @Initialize
    public void initialize() {
        // エラーメッセージコードが設定されている場合のみ、そのメッセージを表示する。
        // 設定されていない場合は、自動で設定される例外のメッセージを表示する。
        String errorMessageCode = getErrorMessageCode();
        if (StringUtils.isNotEmpty(errorMessageCode)) {
            Message message = getMessage(errorMessageCode);
            if (message != null) {
                //  ここで生成するメッセージのアクション名は不明なのでnullを設定
                message.applyActionName(null);
                addMessage(message);
            }
            removeErrorMessageCode();
        }
    }

    /**
     * 指定されたメッセージコードからメッセージを取得する.
     * @return メッセージ
     */
    private Message getMessage(String messageCode, Object... vars) {
        return Messages.getMessage(messageCode, vars);
    }

    /**
     * 画面表示用にメッセージをセットする.
     */
    private void addMessage(Message message) {
        viewHelper.addMessage(message);
    }

    /**
     * 明示的にセットされたエラーメッセージコードを取得する.
     * @return エラーメッセージコード
     */
    private String getErrorMessageCode() {
        return viewHelper.getSessionValue(KEY_ERROR_MESSAGE_CODE);
    }

    /**
     * エラーメッセージコードを削除する.
     */
    private void removeErrorMessageCode() {
        viewHelper.removeSessionValue(KEY_ERROR_MESSAGE_CODE);
    }

    public String getStackTrace() {
        FacesContext context = FacesContext.getCurrentInstance();
        Map<String, Object> sessionMap = context.getExternalContext().getSessionMap();
        Throwable ex = (Throwable) sessionMap.get("jp.co.opentone.bsol.exception");
        sessionMap.remove("jp.co.opentone.bsol.exception");

        StringWriter w = new StringWriter();
        PrintWriter pw = new PrintWriter(w);
        fillStackTrace(ex, pw);

        return w.toString();
    }

    public void fillStackTrace(Throwable ex, PrintWriter pw) {
        if (ex == null) {
            return;
        }

        ex.printStackTrace(pw);
        if (ex instanceof ServletException) {
            Throwable cause = ((ServletException) ex).getRootCause();
            if (cause != null) {
                pw.println("Root Cause: ");
                fillStackTrace(cause, pw);
            }
        } else {
            Throwable cause = ex.getCause();
            if (cause != null) {
                pw.println("Cause: ");
                fillStackTrace(cause, pw);
            }
        }
    }

    /**
     * 画面にスタックトレースを表示する場合はtrue.
     * @return スタックトレースを表示する場合はtrue
     */
    public boolean isShowStackTrace() {
        try {
            String val = SystemConfig.getValue(KEY_SHOW_STACKTRACE);
            return Boolean.parseBoolean(val);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return true;
        }
    }

    /**
     * 画面にクローズリンクを表示する場合はtrue.
     * @return クローズリンクを表示する場合はtrue
     */
    public boolean isShowCloseLink() {
        try {
            Boolean flg = new Flash().getValue(KEY_SHOW_CLOSE_LINK);
            return (flg != null && flg.booleanValue());
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return false;
        }
    }
}
