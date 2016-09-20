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

import jp.co.opentone.bsol.framework.core.util.LogUtil;
import jp.co.opentone.bsol.linkbinder.event.AbstractDomainEvent;
import jp.co.opentone.bsol.linkbinder.event.DomainEventListener;
import jp.co.opentone.bsol.linkbinder.event.EventBus;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPubSub;

import java.util.Set;

/**
 * Redisのsubscriber.
 * @author opentone
 */
@Component
public class RedisSubscriber extends JedisPubSub implements DomainEventListener {
    /** logger. */
    protected Logger log = LogUtil.getLogger();

    /** Event Bus. */
    @Autowired
    private EventBus eventBus;

    private Set<Subscriber> subscribers;

    /**
     * 購読を開始する.
     */
    public void start() {
        eventBus.addEventListener(this);
    }

    /**
     * 購読を停止する.
     */
    public void stop() {
        eventBus.removeEventListener(this);
    }

    /* (非 Javadoc)
     * @see redis.clients.jedis.JedisPubSub#onSubscribe(java.lang.String, int)
     */
    @Override
    public void onSubscribe(String channel, int subscribedChannels) {
        log.info("[{}] SUBSCRIBED: {}", getClass().getSimpleName(), channel);
    }

    /* (非 Javadoc)
     * @see redis.clients.jedis.JedisPubSub#onUnsubscribe(java.lang.String, int)
     */
    @Override
    public void onUnsubscribe(String channel, int subscribedChannels) {
        log.info("[{}] UNSUBSCRIBED: {}", getClass().getSimpleName(), channel);
    }

    /* (非 Javadoc)
     * @see redis.clients.jedis.JedisPubSub#onMessage(java.lang.String, java.lang.String)
     */
    @Override
    public void onMessage(String channel, String message) {
        log.info("[{}] RECEIVED: {}, {}", new Object[] { getClass().getSimpleName(), channel, message });
        AbstractDomainEvent event = SubscriberUtil.parseJsonMessage(message, AbstractDomainEvent.class);
        subscribers.forEach(s -> {
            String[] eventNames = s.getMyEvents();
            if (ArrayUtils.contains(eventNames, event.getEventName())) {
                s.onMessage(event.getEventName(), message);
            }
        });
    }

    public Set<Subscriber> getSubscribers() {
        return subscribers;
    }

    public void setSubscribers(Set<Subscriber> subscribers) {
        this.subscribers = subscribers;
    }
}
