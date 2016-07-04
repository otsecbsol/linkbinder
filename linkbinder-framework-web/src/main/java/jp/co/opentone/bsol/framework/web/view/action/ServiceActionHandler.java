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
package jp.co.opentone.bsol.framework.web.view.action;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.co.opentone.bsol.framework.core.ProcessContext;
import jp.co.opentone.bsol.framework.core.auth.UserHolder;
import jp.co.opentone.bsol.framework.core.config.SystemConfig;
import jp.co.opentone.bsol.framework.core.exception.InvalidOperationRuntimeException;
import jp.co.opentone.bsol.framework.core.message.Message;
import jp.co.opentone.bsol.framework.core.message.MessageHolder;
import jp.co.opentone.bsol.framework.core.message.Messages;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.web.view.util.ViewHelper;

/**
 * アプリケーション内の一つの処理のまとまり({@link Action})を起動する.
 * @author opentone
 */
public class ServiceActionHandler implements Serializable {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 2669917889870224801L;

    /**
     * logger.
     */
    private static final Logger log = LoggerFactory.getLogger(ServiceActionHandler.class);

    /**
     * 不正な操作を表すエラーコードを取得するためのキー.
     */
    public static final String KEY_INVALID_OPERATION_ERRORS = "errors.invalid.operation";
    /**
     * 不正な操作を表すエラーコード格納コンテナ.
     */
    private Set<String> invalidOperationErrors;

    /**
     * アクション起動オブジェクト.
     */
    @Resource
    private TransactionalActionExecutor executor;

    /**
     * ユーザーのログイン情報を保持するオブジェクト.
     */
    private UserHolder user;

    /**
     * アクションの処理に必要な情報を格納するコンテナ.
     */
    private transient Map<String, Object> actionContextValue;

    /**
     * Viewとの連携のためのヘルパーオブジェクト.
     */
    @Resource
    private ViewHelper viewHelper;

    /**
     * View層に関する処理を行う場合はtrue.
     * <p>
     * デフォルトはtrue.
     * </p>
     */
    private boolean handleView = true;

    /**
     * 空のインスタンスを生成する.
     */
    public ServiceActionHandler() {
    }

    private void initInvalidOperationErrorsSet() {
        invalidOperationErrors = new HashSet<String>();
        String codes = SystemConfig.getValue(KEY_INVALID_OPERATION_ERRORS);
        if (StringUtils.isNotEmpty(codes)) {
            for (String code : codes.split("\\s*,\\s*")) {
                invalidOperationErrors.add(code);
            }
        }
    }

    /**
     * アクションを起動し、結果を返す. このメソッドから起動されたアクションは一つのトランザクション境界内で実行される.
     * @see TransactionalActionExecutor
     * @param action
     *            起動対象
     * @return actionが正常に終了した場合はtrue
     */
    public boolean handleAction(Action action) {
        if (invalidOperationErrors == null) {
            initInvalidOperationErrorsSet();
        }

        String actionName = action.getName();
        try {
            initContext();

            log.debug("action {}", actionName);
            executor.execute(action);
            log.debug("action {} succeeded.", actionName);

            return true;
        } catch (ServiceAbortException e) {
            log.debug("action {} failed.", actionName);
            log.debug(e.getMessage(), e);

            logServiceAbortException(e);
            handleServiceAbortException(e, actionName);
            return false;
        } finally {
            setMessagesToView(actionName);
        }
    }

    private void logServiceAbortException(ServiceAbortException e) {
        StackTraceElement[] elem = e.getStackTrace();
        if (elem != null && elem.length > 0) {
            try {
                StackTraceElement element = elem[0];
                String format = "%s.%s:%d [%s]";
                log.warn(String.format(format,
                    getSimpleClassName(element.getClassName()),
                    element.getMethodName(),
                    element.getLineNumber(),
                    e.getMessageCode()));
            } catch (Exception ex) {
                log.error(ex.getMessage(), ex);
            }
        }
    }

    private String getSimpleClassName(String className) {
        if (StringUtils.isEmpty(className)) {
            return "";
        }
        int i = className.lastIndexOf('.');
        if (i == -1) {
            return className;
        }
        return className.substring(i + 1);
    }

    /**
     * 現在実行中のユーザー情報を設定する.
     * @param user
     *            実行中ユーザー情報
     */
    public void setUser(UserHolder user) {
        this.user = user;
    }

    /**
     * アクションの処理に必要な情報を設定する.
     * @param values
     *            アクションの処理に必要な情報
     */
    public void setActionContextValues(Map<String, Object> values) {
        this.actionContextValue = values;
    }

    /**
     * 業務処理に必要な情報を格納するコンテナを初期化する.
     */
    protected void initContext() {
        ProcessContext pc = ProcessContext.getCurrentContext();

        // ユーザーID: DataSourceの選択に必要
        pc.setValue(SystemConfig.KEY_USER_ID, user.getUserId());
        pc.setValue(SystemConfig.KEY_USER, user);
        // アクションの処理に必要な情報を格納
        pc.setValue(SystemConfig.KEY_ACTION_VALUES, actionContextValue);
    }

    /**
     * 業務処理で発生した例外を処理する.
     * @param e
     *            業務処理で発生した例外
     * @param actionName アクション名
     */
    protected void handleServiceAbortException(ServiceAbortException e, String actionName) {
        ProcessContext pc = ProcessContext.getCurrentContext();
        pc.clearMessages();

        if (e.getMessageCode() != null) {
            pc.addMessage(e.getMessageCode(), e.getMessageVars());
        } else if (e.getCause() instanceof MessageHolder) {
            MessageHolder h = (MessageHolder) e.getCause();
            pc.addMessage(h.getMessageCode(), h.getMessageVars());
        }
        if (e.getMessageCode() != null
            && invalidOperationErrors.contains(e.getMessageCode())) {
            Message m;
            if (e.getCause() != null && e.getCause() instanceof MessageHolder) {
                MessageHolder h = (MessageHolder) e.getCause();
                m = Messages.getMessage(h.getMessageCode(), h.getMessageVars());
            } else {
                m = Messages.getMessage(e.getMessageCode(), e.getMessageVars());
            }
            m.applyActionName(actionName);
            throw new InvalidOperationRuntimeException(m, e);
        }
    }

    /**
     * 業務処理で発生した例外をプレゼンテーション層のメッセージに変換する.
     * @param actionName アクション名
     */
    protected void setMessagesToView(String actionName) {
        if (!isHandleView()) {
            return;
        }

        ProcessContext pc = ProcessContext.getCurrentContext();
        for (Message m : pc.messages()) {
            m.applyActionName(actionName);
            viewHelper.addMessage(m);
        }
        pc.clearMessages();
    }

    /**
     * @param handleView the handleView to set
     */
    public void setHandleView(boolean handleView) {
        this.handleView = handleView;
    }

    /**
     * @return the handleView
     */
    public boolean isHandleView() {
        return handleView;
    }
}
