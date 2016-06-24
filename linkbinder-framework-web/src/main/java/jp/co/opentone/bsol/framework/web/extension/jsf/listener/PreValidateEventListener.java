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
package jp.co.opentone.bsol.framework.web.extension.jsf.listener;

import javax.faces.component.UIViewRoot;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;
import javax.faces.event.PreValidateEvent;
import javax.faces.event.SystemEvent;

/**
 * PreValidateイベントリスナです.
 * @author opentone
 */
public class PreValidateEventListener extends ExtendedSystemEventListener {

    /**
     * PreValidateEventListener設定のプリフィックス.
     */
    private static final String PROP_VALIDATOR_PREFIX = "validator";

    @Override
    public void processEvent(SystemEvent event) throws AbortProcessingException {
        if (event instanceof PreValidateEvent) {
            UIViewRoot viewRoot = getUIViewRoot(event);
            if (null != viewRoot) {
                ActionListener listener
                    = createActionEventListener(event, PROP_VALIDATOR_PREFIX);
                if (null != listener) {
                    // PreValidaeEventは必要な場合だけ登録されるので、存在しない場合でも
                    // エラーではありません.
                    ActionEvent ae = new ActionEvent(viewRoot);
                    listener.processAction(ae);
                }
            }
        }
    }
}
