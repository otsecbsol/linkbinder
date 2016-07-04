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

package jp.co.opentone.bsol.framework.web.view.listener;

import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;

import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;

/**
 * システムのActionListenerの振舞いを規定するインターフェイス.
 * @author opentone
 */
public interface ExtendedActionListener extends ActionListener {

    /**
     * 各派生クラスで実装する固有の処理です.
     * @param event ActionEvent
     * @throws ServiceAbortException 処理例外
     */
    void processOriginalAction(ActionEvent event) throws ServiceAbortException;

    /**
     * 表示用メッセージを設定する.
     * @param pageIdParameter メッセージID
     */
    void addViewMessage(String pageIdParameter);

    /**
     * メッセージを設定する.
     */
    void setViewMessage();
}
