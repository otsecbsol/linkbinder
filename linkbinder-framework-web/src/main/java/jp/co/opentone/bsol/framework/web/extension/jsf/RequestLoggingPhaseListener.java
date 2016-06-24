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

import java.util.Enumeration;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

import jp.co.opentone.bsol.framework.core.util.LogUtil;

/**
 * {@link HttpServletRequest}の内容をログに出力する{@link PhaseListener}.
 * @author opentone
 */
public class RequestLoggingPhaseListener implements PhaseListener {

    /** logger. */
    private static Logger log = LogUtil.getLogger();

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 9021529363269848953L;

    /* (non-Javadoc)
     * @see javax.faces.event.PhaseListener#afterPhase(javax.faces.event.PhaseEvent)
     */
    @Override
    public void afterPhase(PhaseEvent event) {
    }

    /* (non-Javadoc)
     * @see javax.faces.event.PhaseListener#beforePhase(javax.faces.event.PhaseEvent)
     */
    @Override
    public void beforePhase(PhaseEvent event) {
        HttpServletRequest req =
            (HttpServletRequest) FacesContext.getCurrentInstance()
                                    .getExternalContext().getRequest();

        log.info("Requested: {} {}", req.getMethod(), req.getRequestURI());
        Enumeration<?> enm = req.getHeaderNames();
        log.info("Header:");
        while (enm.hasMoreElements()) {
            String name = (String) enm.nextElement();
            log.info("   {} = {}", name, req.getHeader(name));
        }

        log.info(" Parameters:");
        @SuppressWarnings("unchecked")
        Map<String, String[]> parameterMap = req.getParameterMap();
        for (Map.Entry<String, String[]> e : parameterMap.entrySet()) {
            log.info("  {} = {}", e.getKey(), StringUtils.join(e.getValue()));
        }
    }

    /* (non-Javadoc)
     * @see javax.faces.event.PhaseListener#getPhaseId()
     */
    @Override
    public PhaseId getPhaseId() {
        return PhaseId.RESTORE_VIEW;
    }
}
