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

import org.h2.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.linkbinder.event.ProjectCreated;
import jp.co.opentone.bsol.linkbinder.event.ProjectDeleted;
import jp.co.opentone.bsol.linkbinder.event.ProjectEvent;
import jp.co.opentone.bsol.linkbinder.service.correspon.CorresponFullTextSearchService;

/**
 * @author opentone
 */
@Component
public class ElasticsearchIndexSubscriber extends Subscriber {
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
                ProjectCreated.NAME,
                ProjectDeleted.NAME
        };
    }


    /* (非 Javadoc)
     * @see redis.clients.jedis.JedisPubSub#onMessage(java.lang.String, java.lang.String)
     */
    @Override
    public void onMessage(String channel, String message) {
        log.info("[{}] RECEIVED: {}, {}", new Object[] { getClass().getSimpleName(), channel, message });
        ProjectEvent event = parseJsonMessage(message, ProjectEvent.class);
        try {
            setupProcessContext(event.getProjectId());
            if (StringUtils.equals(ProjectCreated.NAME, event.getEventName())) {
                log.info("[{}] CREATE INDEX: {}", getClass().getSimpleName(), event.getProjectId());
                service.createIndex(event.getProjectId());
            } else if (StringUtils.equals(ProjectDeleted.NAME, event.getEventName())) {
                //FIXME 何もしない
            }
        } catch (ServiceAbortException | RuntimeException e) {
            log.error("indexの作成/削除に失敗しました", e);
        }
    }
}
