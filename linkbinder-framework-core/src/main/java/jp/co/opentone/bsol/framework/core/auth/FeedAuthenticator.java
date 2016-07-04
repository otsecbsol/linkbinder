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

import java.io.Serializable;


/**
 * RSSフィードを要求するユーザーを認証する.
 * @author opentone
 */
public interface FeedAuthenticator extends Serializable {

    /**
     * ユーザーIDとFeed Keyで認証する.
     * <p>
     * 認証に成功した場合は認証済ユーザー情報を返し、
     * 認証できない場合は{@link AuthenticateException}が発生する.
     * </p>
     * @param userId ユーザーID
     * @param feedKey ユーザーとRSSフィードを関連付ける一意な値
     * @return 認証済ユーザー情報
     * @throws 認証に失敗
     */
    AuthUser authenticate(String userId, String feedKey) throws AuthenticateException;
}
