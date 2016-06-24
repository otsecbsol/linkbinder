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
package jp.co.opentone.bsol.framework.core.auth;

import jp.co.opentone.bsol.framework.core.dao.Dao;


/**
 * ユーザー認証のためのインターフェイス.
 * @author opentone
 */
public interface Authenticator extends Dao {

    /**
     * ユーザー認証を行い、認証済ユーザー情報を返す.
     * <p>
     * 正当なユーザーである場合は、{@link AuthUser} にユーザー情報を格納して返し、
     * 認証に失敗した場合は、失敗の原因を表す例外が呼出元にthrowされる.
     * </P>
     * @param userId
     *            ユーザーID
     * @param password
     *            暗号化されていないパスワード
     * @return 認証済ユーザー情報
     * @throws AuthenticateException 認証に失敗
     * @throws ExpiredPasswordException パスワードの有効期限切れ
     */
    AuthUser authenticate(String userId, String password) throws AuthenticateException,
        ExpiredPasswordException;
}
