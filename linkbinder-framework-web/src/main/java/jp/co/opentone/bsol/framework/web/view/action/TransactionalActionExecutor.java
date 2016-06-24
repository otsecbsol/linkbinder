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

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;

/**
 * アクションを一つのトランザクション境界内で実行するクラス.
 * <p>
 * Springの{@link Transactionall}アノテーションを利用し 宣言的トランザクションによるトラクザクション制御を行う.
 * </p>
 * @author opentone
 */
@Component
//CHECKSTYLE:OFF
@Transactional(
    rollbackFor = { ServiceAbortException.class },
    timeout     = 1800)
//CHECKSTYLE:ON
public class TransactionalActionExecutor implements Serializable {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -4183803784051894386L;

    /**
     * 指定されたアクションを一つのトランザクション境界内で実行する.
     * <p>
     * トラクザクションがロールバックされるのは次のいずれかの場合.
     * <ol>
     * <li>{@link Action#execute()}から{@link RuntimeException}
     * 及びそのサブクラスがthrowされた場合</li>
     * <li>{@link Action#execute()}から{@link ServiceAbortException}
     * 及びそのサブクラスがthrowsされた場合</li>
     * </ol>
     * </p>
     * @param action
     *            実行対象
     * @throws ServiceAbortException 実行に失敗
     */
    public void execute(Action action) throws ServiceAbortException {
        action.execute();
    }
}
