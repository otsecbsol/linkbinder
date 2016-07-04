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

import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;

/**
 * 画面で発生したアクション.
 * <p>
 * @author opentone
 */
public interface Action extends Serializable {

    /**
     * このアクションの名前を返す.
     * @return 名前
     */
    String getName();

//    /**
//     * このアクションの名前を設定する.
//     * @param name 名前
//     */
//    void setName(String name);

    /**
     * アクションを実行し結果を返す.
     * @throws ServiceAbortException アクションの実行に失敗
     */
    void execute() throws ServiceAbortException;

//    /**
//     * このアクションの呼出元を設定する.
//     * @param caller 呼出元オブジェクト
//     */
//    void setCaller(Object caller);
}
