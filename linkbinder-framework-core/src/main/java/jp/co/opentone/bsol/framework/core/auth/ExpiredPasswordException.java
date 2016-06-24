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

/**
 * パスワードの有効期限が過ぎたことを表す例外.
 * @author opentone
 */
public class ExpiredPasswordException extends Exception {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -8128081391170705138L;

    /**
     * ユーザーID.
     */
    private String userId;

    /**
     * 空のインスタンスを生成する.
     */
    public ExpiredPasswordException() {
    }

    /**
     * パスワードの有効期限が過ぎたユーザーIDを指定してインスタンスを生成する.
     * @param userId
     *            ユーザーID
     */
    public ExpiredPasswordException(String userId) {
        this.setUserId(userId);
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }
}
