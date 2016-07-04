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
package jp.co.opentone.bsol.linkbinder.service.common;

import jp.co.opentone.bsol.framework.core.service.IService;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.linkbinder.dto.User;

/**
 * 当システムのログイン・ログアウトに関するサービスを提供する.
 * @author opentone
 */
public interface LoginService extends IService {

    /**
     * ユーザーID、パスワードでシステムにログインする.
     * @param userId ユーザーID
     * @param password パスワード
     * @return 認証済ユーザー情報
     * @throws ServiceAbortException 認証に失敗
     */
    User login(String userId, String password) throws ServiceAbortException;


    /**
     * ユーザーID、RSSフィードキーで認証する.
     * @param userId ユーザーID
     * @param feedKey RSSフィードキー
     * @return 認証済ユーザー情報
     * @throws ServiceAbortException 認証に失敗
     */
    User authenticateWithFeedKey(String userId, String feedKey) throws ServiceAbortException;

    /**
     * ユーザーIDでログイン中のユーザーをシステムからログアウトさせる.
     * @param userId ユーザーID
     * @throws ServiceAbortException ログアウトに失敗
     */
    void logout(String userId) throws ServiceAbortException;

    /**
     * ダミーのユーザーを設定する.
     * @return ユーザー情報
     * @throws ServiceAbortException
     */
    User dummyLogin() throws ServiceAbortException;

}
