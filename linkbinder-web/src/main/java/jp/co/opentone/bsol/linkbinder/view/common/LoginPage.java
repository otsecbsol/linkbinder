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
package jp.co.opentone.bsol.linkbinder.view.common;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.ManagedBean;
import javax.annotation.Resource;
import javax.faces.event.ComponentSystemEvent;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.constraints.Length;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import jp.co.opentone.bsol.framework.core.config.SystemConfig;
import jp.co.opentone.bsol.framework.core.exception.ApplicationFatalRuntimeException;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.core.util.ArgumentValidator;
import jp.co.opentone.bsol.framework.core.validation.constraints.Alphanumeric;
import jp.co.opentone.bsol.framework.core.validation.constraints.Required;
import jp.co.opentone.bsol.framework.web.extension.jsf.annotation.Initialize;
import jp.co.opentone.bsol.linkbinder.Constants;
import jp.co.opentone.bsol.linkbinder.action.AbstractAction;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.service.common.LoginService;
import jp.co.opentone.bsol.linkbinder.view.AbstractPage;
import jp.co.opentone.bsol.linkbinder.view.LoginUserInfoHolder;
import jp.co.opentone.bsol.linkbinder.view.common.module.redirect.RedirectModule;
import jp.co.opentone.bsol.linkbinder.view.common.module.redirect.RedirectProcessException;
import jp.co.opentone.bsol.linkbinder.view.common.module.redirect.RedirectProcessParameterKey;
import jp.co.opentone.bsol.linkbinder.view.common.module.redirect.RedirectScreenId;

/**
 * 当システムのログイン画面.
 * @author opentone
 */
@Component
@ManagedBean
@Scope("view")
public class LoginPage extends AbstractPage {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 401285411829698123L;
    /**
     * ログ出力オブジェクト.
     */
    private static final Logger LOG = LoggerFactory.getLogger(LoginPage.class);

    /**
     * ユーザーID.
     */
    @Required
    //CHECKSTYLE:OFF
    @Length(max = 5)
    //CHECKSTYLE:ON
    @Alphanumeric(allowAlphaNumericOnly = true, allowUpperCaseOnly = true)
    private String userId;
    /**
     * パスワード.
     */
    @Required
    //CHECKSTYLE:OFF
    @Length(max = 30)
    //CHECKSTYLE:ON
    @Alphanumeric(allowAlphaNumericOnly = true)
    private String password;

    /**
     * 遷移元から明示的にログアウトを指定されたことを表すパラメーター.
     * このパラメーターが指定されている場合は、明示的にログアウトしたことを表すログが出力される.
     * <p>
     * ログアウトが指定されていない場合はnull
     * </p>
     */
    private String logout;

    /**
     * パスワードを変更するためのリンク先.
     * <p>
     * 当システムではパスワードの変更機能を提供しないため
     * ユーザーは外部システムでパスワードを設定することになる.
     * </p>
     */
    private String externalLink =
        SystemConfig.getValue(Constants.KEY_PASSWORD_MANAGEMENT_URL);

    /**
     * パスワードの有効期限が切れたことを表すフラグ.
     */
    private boolean passwordExpired;

    /**
     * セッションがタイムアウトしたことを表すパラメータ.
     */
    private String timeout;

    /**
     * ログインに関する処理を提供するサービス.
     */
    @Resource
    private LoginService loginService;

    /**
     * リダイレクト処理に関する処理を実施するモジュール.
     */
    @Resource
    private RedirectModule redirectModule;

    /**
     * 空のインスタンスを生成する.
     */
    public LoginPage() {
    }

    /**
     * ページの初期化を行う.
     */
    @Initialize
    public void initialize() {
        handler.handleAction(new InitializeAction(this));
    }

    /**
     * ユーザーID、パスワードで認証を行い、システムにログインする.
     * @return 遷移先
     */
    public String login() {
        if (handler.handleAction(new LoginAction(this))) {
            if (isDirectLogin()) {
                clearRedirectValue();
                return null;
            }
            return "home";
        } else {
            return null;
        }
    }

    /**
     * ユーザーがログアウトしたことを表すログを出力する.
     * @param user ユーザー
     */
    public void logLogout(User user) {
        LOG.info("user {}/{} logged out.", user.getEmpNo(), user.getNameE());
    }

    /**
     * ユーザーIDを設定する.
     * @param userId the userId to set
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * ユーザーIDを返す.
     * @return the userId
     */
    public String getUserId() {
        return userId;
    }

    /**
     * パスワードを設定する.
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * パスワードを返す.
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * パスワードを変更する為のリンク先を設定する.
     * @param externalLink the externalLink to set
     */
    public void setExternalLink(String externalLink) {
        this.externalLink = externalLink;
    }

    /**
     * パスワードを変更する為のリンク先を返す.
     * @return the externalLink
     */
    public String getExternalLink() {
        return externalLink;
    }

    /**
     * パスワードが切れた事を表すフラグを設定する.
     * @param passwordExpired the passwordExpired to set
     */
    public void setPasswordExpired(boolean passwordExpired) {
        this.passwordExpired = passwordExpired;
    }

    /**
     * パスワードが切れた事を表すフラグを返す.
     * @return the passwordExpired
     */
    public boolean isPasswordExpired() {
        return passwordExpired;
    }

    /**
     * ログアウトパラメーターを設定する.
     * @param logout the logout to set
     */
    public void setLogout(String logout) {
        this.logout = logout;
    }

    /**
     * ログアウトパラメーターを返す.
     * @return the logout
     */
    public String getLogout() {
        return logout;
    }

    /**
     * セッションタイムアウトパラメーターを設定する.
     * @param timeout the timeout to set
     */
    public void setTimeout(String timeout) {
        this.timeout = timeout;
    }

    /**
     * セッションタイムアウトパラメータを返す.
     * @return the timeout
     */
    public String getTimeout() {
        return timeout;
    }

    /**
     * セッションタイムアウト状態かどうかを判定した結果を返す.
     * @return タイムアウト状態の場合true
     */
    public boolean isSessionTimeout() {
        return StringUtils.isNotEmpty(timeout);
    }

    /**
     * ダイレクトログインの判定.
     * @return ダイレクトログインの場合true
     */
    public boolean isDirectLogin() {
        return viewHelper.getSessionValue(Constants.KEY_REDIRECT_SCREEN_ID) != null;
    }

    private void storeLoginUser(User login) {
        // 認証済ユーザー情報を、sessionに格納されたユーザーオブジェクトにコピー
        // このオブジェクトはSpringの管理下にあるためインスタンスごと置き換えてはいけない
        try {
            User current = getCurrentUser();
            if (current != null) {
                if (current.getUserId() != null && !current.getUserId().equals(login.getUserId())) {
                    clearLoginInfo();
                }
                PropertyUtils.copyProperties(current, login);
            }
            LoginUserInfoHolder holder = getLoginUserInfoHolder();
            holder.setLoginUser(current);

        } catch (IllegalAccessException e) {
            throw new ApplicationFatalRuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new ApplicationFatalRuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new ApplicationFatalRuntimeException(e);
        }
    }

    private void clearRedirectValue() {
        // セッションにセットされた情報を消す
        viewHelper.removeSessionValue(Constants.KEY_REDIRECT_SCREEN_ID);
        viewHelper.removeSessionValue(RedirectProcessParameterKey.ID);
        viewHelper.removeSessionValue(RedirectProcessParameterKey.PROJECT_ID);
    }

    /**
     * ページを初期化する.
     * @author opentone
     */
    static class InitializeAction extends AbstractAction {
        /**
         * SerialVersionUID.
         */
        private static final long serialVersionUID = -91538137494122937L;
        /** ログイン画面. */
        private LoginPage page;
        /**
         * ページを指定してインスタンス化する.
         * @param page ログイン画面
         */
        public InitializeAction(LoginPage page) {
            super(page);
            this.page = page;
        }

        /* (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            if (page.isDirectLogin()) {
                validateRedirectParametar();
            }

            User current = page.getCurrentUser();
            //  ログイン情報のクリア
            if (current != null) {
                // 明示的にログアウトで遷移してきた場合は記録としてログを出力
                // 明示的にログアウトで遷移してきた場合はログイン情報を初期化
                if (StringUtils.isNotEmpty(page.logout)
                        && StringUtils.isNotEmpty(current.getEmpNo())) {
                    page.logLogout(current);

                    current.clearProperties();

                    page.clearLoginInfo();
                }
            }

            if (page.isSessionTimeout()) {
                page.setPageMessage(ApplicationMessageCode.SESSION_HAS_TIMED_OUT);
            }
        }

        private void validateRedirectParametar() {
            RedirectScreenId id = page.viewHelper.getSessionValue(Constants.KEY_REDIRECT_SCREEN_ID);
            switch (id) {
            case CORRESPON:
                ArgumentValidator.validateNotEmpty((String) page.viewHelper.getSessionValue(
                        RedirectProcessParameterKey.ID), RedirectProcessParameterKey.ID);
                ArgumentValidator.validateNotEmpty((String) page.viewHelper.getSessionValue(
                        RedirectProcessParameterKey.PROJECT_ID),
                        RedirectProcessParameterKey.PROJECT_ID);
                break;
            default:
                throw new ApplicationFatalRuntimeException("invalid screen id");
            }
        }
    }

    /**
     * ユーザーを認証し、システムにログインする.
     * @author opentone
     */
    static class LoginAction extends AbstractAction {
        /**
         * SerialVersionUID.
         */
        private static final long serialVersionUID = 682084022967872569L;
        /** ログイン画面. */
        private LoginPage page;
        /**
         * ページを指定してインスタンス化する.
         * @param page ログイン画面
         */
        public LoginAction(LoginPage page) {
            super(page);
            this.page = page;
        }
        /* (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            User login = login();
            page.storeLoginInfo(login);
            if (page.isDirectLogin()) {
                redirect();
            }

            page.viewHelper.setSessionValue(Constants.KEY_START_NEW_SESSION, "true");
        }

        private User login() throws ServiceAbortException {
            page.passwordExpired = false;
            try {
                return page.loginService.login(page.getUserId(),
                                               page.getPassword());
            } catch (ServiceAbortException e) {
                if (ApplicationMessageCode.PASSWORD_EXPIRED.equals(e.getMessageCode())) {
                    page.passwordExpired = true;
                }
                throw e;
            }
        }

        private void redirect() throws ServiceAbortException {
            RedirectScreenId screenId =
                page.viewHelper.getSessionValue(Constants.KEY_REDIRECT_SCREEN_ID);
            Map<String, String> map = new HashMap<String, String>();
            map.put(RedirectProcessParameterKey.ID,
                (String) page.viewHelper.getSessionValue(RedirectProcessParameterKey.ID));
            map.put(RedirectProcessParameterKey.PROJECT_ID,
                (String) page.viewHelper.getSessionValue(RedirectProcessParameterKey.PROJECT_ID));

            try {
                page.redirectModule.setBasePage(page);
                page.redirectModule.setupRedirect(screenId, map);
            } catch (RedirectProcessException e) {
                // ログイン状態を無効にする
                page.viewHelper.removeSessionValue(Constants.KEY_PROJECT);
                page.viewHelper.removeSessionValue(Constants.KEY_CURRENT_USER);
                // リダイレクトできないため、情報をクリアしてリダイレクトをキャンセル
                page.clearRedirectValue();
                handleRedirectProcessException(e);
            }
        }

        /**
         * リダイレクト処理例外のエラーコードに対応するメッセージを格納したサービス例外を生成して返す.
         * @param exception リダイレクト処理例外
         * @throws ServiceAbortException リダイレクト処理例外のコードに対応したサービス処理例外
         */
        private void handleRedirectProcessException(RedirectProcessException exception)
            throws ServiceAbortException {
            switch (exception.getErrorCode()) {
            case SPECIFIED_PROJECT_NOT_FOUND:
                throw new ServiceAbortException(
                    ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_PROJECT_DIFF);
            case SPECIFIED_PROJECT_USER_NOT_FOUND:
                throw new ServiceAbortException(
                    ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_PROJECT_DIFF);
            default:
                if (exception.getCause() != null) {
                    throw new ServiceAbortException(
                        exception.getCause().getMessage(), exception,
                        ApplicationMessageCode.CANNOT_START_BECAUSE_OF_ANY_REASON);
                } else {
                    throw new ServiceAbortException(
                        exception.getErrorCode().toString(),
                        ApplicationMessageCode.CANNOT_START_BECAUSE_OF_ANY_REASON);
                }
            }
        }
    }

    /**
     *
     */
    public void clearLoginInfo() {
        LoginUserInfoHolder holder = getLoginUserInfoHolder();
        if (holder != null) {
            holder.clearAllLoginProject();
            holder.setLoginUser(null);
        }
    }

    public void storeLoginInfo(User login) {
        LoginUserInfoHolder holder = getLoginUserInfoHolder();
        if (holder != null) {
            holder.setLoginUser(login);
            storeLoginUser(login);
        }
    }

    public void preValidate(ComponentSystemEvent e) {
        setPasswordExpired(false);
    }
}
