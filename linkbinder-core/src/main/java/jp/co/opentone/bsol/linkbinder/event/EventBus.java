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
package jp.co.opentone.bsol.linkbinder.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jp.co.opentone.bsol.framework.core.config.SystemConfig;
import jp.co.opentone.bsol.framework.core.exception.ApplicationFatalRuntimeException;
import jp.co.opentone.bsol.linkbinder.Constants;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.Serializable;

/**
 * @author opentone
 */
@Component
public class EventBus implements Serializable {

    private JedisPool pool;

    public static final String CHANNEL = "LINKBINDER";

    @PostConstruct
    public void setup() {
        pool = new JedisPool(new JedisPoolConfig(), SystemConfig.getValue(Constants.KEY_REDIS_HOST));
    }

    @PreDestroy
    public void destroy() {
        if (pool != null) {
            pool.destroy();
        }
    }

    public <T extends JedisPubSub> void addEventListener(T l) {
        try (Jedis jedis = pool.getResource()) {
            jedis.subscribe(l, CHANNEL);
        }
    }

    public <T extends JedisPubSub> void removeEventListener(T l) {
//        try (Jedis jedis = pool.getResource()) {
//            jedis.subscribe(l, l.getMyChannels());
//        }
    }

    /**
     * イベントを発生させる.
     * @param event イベント
     */
    public void raiseEvent(DomainEvent event) {
        try (Jedis jedis = pool.getResource()) {
            ObjectMapper m = new ObjectMapper();
            String jsonMessage = m.writeValueAsString(event);

            jedis.publish(CHANNEL, jsonMessage);
//            jedis.publish(event.getEventName(), jsonMessage);
        } catch (JsonProcessingException e) {
            throw new ApplicationFatalRuntimeException(e);
        }
    }
}
