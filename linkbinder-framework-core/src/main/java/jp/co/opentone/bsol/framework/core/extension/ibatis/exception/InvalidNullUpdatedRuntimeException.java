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
package jp.co.opentone.bsol.framework.core.extension.ibatis.exception;

import org.springframework.dao.DataAccessException;

/**
 * NOT NULLの列にNULLを更新しようとしたことを表す例外.
 * @author opentone
 */
public class InvalidNullUpdatedRuntimeException extends DataAccessException {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -8801181338582231896L;

    /**
     * メッセージを指定してインスタンス化する.
     * @param msg
     *            メッセージ
     */
    public InvalidNullUpdatedRuntimeException(String msg) {
        super(msg);
    }

    /**
     * 空のインスタンスを生成する.
     */
    public InvalidNullUpdatedRuntimeException() {
        this("cannot update to NULL");
    }
}
