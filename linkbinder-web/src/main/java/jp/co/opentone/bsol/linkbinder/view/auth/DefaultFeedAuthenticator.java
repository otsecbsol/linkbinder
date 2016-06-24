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
package jp.co.opentone.bsol.linkbinder.view.auth;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;

import jp.co.opentone.bsol.framework.core.auth.AuthUser;
import jp.co.opentone.bsol.framework.core.auth.AuthenticateException;
import jp.co.opentone.bsol.framework.core.auth.FeedAuthenticator;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.web.view.action.Action;
import jp.co.opentone.bsol.framework.web.view.action.ServiceActionHandler;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.service.common.LoginService;

/**
 * RSSフィードに対する要求を認証する.
 * @author opentone
 */
public class DefaultFeedAuthenticator implements FeedAuthenticator {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 9217332384481235766L;
    /**
     * 認証処理を行うサービス.
     */
    @Resource
    private LoginService service;
    /**
     * 認証アクションを制御するオブジェクト.
     */
    @Resource
    private ServiceActionHandler handler;

    /**
     * 現在実行中ユーザー.
     * {@link ServiceActionHandler}によるアクション起動のために必要だが
     * ここではダミーとして空のユーザーを設定するだけ.
     */
    @Resource
    private User currentUser;

    /**
     * 認証ユーザー.
     */
    private String userId;
    /**
     * 認証時に使用するRSSフィードキー.
     */
    private String feedKey;

    /**
     * 認証済ユーザー.
     * <p>
     * AuthenticateActionが処理結果を格納する.
     */
    private AuthUser result;

    /**
     * 空のインスタンスを生成する.
     */
    public DefaultFeedAuthenticator() {
    }

    /**
     * インスタンス生成後の処理.
     */
    @PostConstruct
    public void setup() {
        //  ダミーユーザー情報を格納
        handler.setUser(currentUser);
        //  通常のViewに関する処理は行わないようにする
        handler.setHandleView(false);
    }

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.view.auth.FeedAuthenticator
     *     #authenticate(java.lang.String, java.lang.String)
     */
    public AuthUser authenticate(String aUserId, String aFeedKey) throws AuthenticateException {
        this.userId = aUserId;
        this.feedKey = aFeedKey;

        handler.handleAction(new AuthenticateAction(this));
        if (result == null) {
            throw new AuthenticateException();
        }
        return result;
    }

    /**
     * 認証アクション.
     * @author opentone
     */
    public static class AuthenticateAction implements Action {
        /**
         * serialVersionUID.
         */
        private static final long serialVersionUID = 1839625895373317790L;
        /** このアクションの名前. */
        private static final String NAME = "Authenticate";
        /** アクションの起動元. */
        private DefaultFeedAuthenticator parent;
        /**
         * 起動元を指定してインスタンス化する.
         * @param parent 起動元
         */
        AuthenticateAction(DefaultFeedAuthenticator parent) {
            this.parent = parent;
        }

        /* (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            if (!validate()) {
                return;
            }

            //  認証処理はServiceに委譲
            User u = parent.service.authenticateWithFeedKey(parent.userId, parent.feedKey);
            if (u != null) {
                AuthUser authUser = new AuthUser();
                authUser.setUserId(parent.userId);
                parent.result = authUser;
            } else {
                parent.result = null;
            }
        }

        private boolean validate() {
            return StringUtils.isNotEmpty(parent.userId)
                && StringUtils.isNotEmpty(parent.feedKey);
        }

        /* (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#getName()
         */
        public String getName() {
            return NAME;
        }
    }
}
