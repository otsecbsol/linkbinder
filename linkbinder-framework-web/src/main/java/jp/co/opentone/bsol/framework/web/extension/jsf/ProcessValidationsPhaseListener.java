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
package jp.co.opentone.bsol.framework.web.extension.jsf;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

import jp.co.opentone.bsol.framework.core.message.Message;
import jp.co.opentone.bsol.framework.core.message.MessageCode;
import jp.co.opentone.bsol.framework.core.message.Messages;

/**
 * JSFのProcess Validation Phaseで起動されるリスナー.
 * @author opentone
 */
@SuppressWarnings("serial")
public class ProcessValidationsPhaseListener implements PhaseListener {

    /* (non-Javadoc)
     * @see javax.faces.event.PhaseListener#afterPhase(javax.faces.event.PhaseEvent)
     */
    @Override
    public void afterPhase(PhaseEvent event) {
        if (!getPhaseId().equals(event.getPhaseId())) {
            return;
        }

        //  いずれかのコンポーネントにメッセージが設定されている場合は
        //  入力エラーがあることを表すメッセージを追加する
        if (FacesContext.getCurrentInstance().getClientIdsWithMessages().hasNext()) {
            Message m = Messages.getMessage(MessageCode.E_INVALID_INPUT);
            FacesMessage fm = new FacesMessage(
                                    FacesMessage.SEVERITY_ERROR,
                                    m.getSummary(),
                                    m.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, fm);
        }
    }

    /* (non-Javadoc)
     * @see javax.faces.event.PhaseListener#beforePhase(javax.faces.event.PhaseEvent)
     */
    @Override
    public void beforePhase(PhaseEvent event) {
        //  Validationの事前処理は特になし
    }

    /* (non-Javadoc)
     * @see javax.faces.event.PhaseListener#getPhaseId()
     */
    @Override
    public PhaseId getPhaseId() {
        return PhaseId.PROCESS_VALIDATIONS;
    }
}
