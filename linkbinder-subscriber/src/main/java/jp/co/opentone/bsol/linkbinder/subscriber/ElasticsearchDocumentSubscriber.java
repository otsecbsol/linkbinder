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
package jp.co.opentone.bsol.linkbinder.subscriber;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.linkbinder.dto.Attachment;
import jp.co.opentone.bsol.linkbinder.dto.Correspon;
import jp.co.opentone.bsol.linkbinder.event.CorresponCreated;
import jp.co.opentone.bsol.linkbinder.event.CorresponDeleted;
import jp.co.opentone.bsol.linkbinder.event.CorresponEvent;
import jp.co.opentone.bsol.linkbinder.event.CorresponIssued;
import jp.co.opentone.bsol.linkbinder.event.CorresponUpdated;
import jp.co.opentone.bsol.linkbinder.event.CorresponWorkflowStatusChanged;
import jp.co.opentone.bsol.linkbinder.service.correspon.CorresponFullTextSearchService;
import jp.co.opentone.bsol.linkbinder.service.correspon.CorresponService;

/**
 * @author opentone
 */
@Component
public class ElasticsearchDocumentSubscriber extends Subscriber {

    /** 文書サービス. */
    @Autowired
    private CorresponService corresponService;
    /** 全文検索サービス. */
    @Autowired
    private CorresponFullTextSearchService service;

    /*
     * (非 Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.event.DomainEventListener#getMyEvents()
     */
    @Override
    public String[] getMyEvents() {
        return new String[] {
                CorresponCreated.NAME,
                CorresponUpdated.NAME,
                CorresponDeleted.NAME,
                CorresponIssued.NAME,
                CorresponWorkflowStatusChanged.NAME
        };
    }


    /* (非 Javadoc)
     * @see redis.clients.jedis.JedisPubSub#onMessage(java.lang.String, java.lang.String)
     */
    @Override
    public void onMessage(String channel, String message) {
        log.info("[{}] RECEIVED: {}, {}", new Object[] { getClass().getSimpleName(), channel, message });
        CorresponEvent event = parseJsonMessage(message, CorresponEvent.class);
        try {
            setupProcessContext(event.getProjectId());

            Correspon correspon = corresponService.find(event.getId());
            if (StringUtils.equals(CorresponDeleted.NAME, event.getEventName())) {
                log.info("[{}] DELETE from index: {}", getClass().getSimpleName(), correspon.getId());
                service.deleteFromIndex(correspon);
            } else {
                log.info("[{}] ADD to index: {}", getClass().getSimpleName(), correspon.getId());
                List<Attachment> attachments =
                        corresponService.findAttachments(correspon.getId());
                service.addToIndex(correspon, attachments);
            }

        } catch (ServiceAbortException | RuntimeException e) {
            log.error("indexの登録に失敗しました", e);
        }
    }
}
