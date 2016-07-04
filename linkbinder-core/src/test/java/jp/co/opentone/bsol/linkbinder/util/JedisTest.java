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
package jp.co.opentone.bsol.linkbinder.util;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;

/**
 * @author opentone
 */
public class JedisTest {

    private JedisPool pool;
    private String channel = "elasticsearch.add.to.index";

    @Before
    public void setup() {
        pool = new JedisPool(new JedisPoolConfig(), "192.168.99.100");
    }

    @After
    public void teardown() {
        if (pool != null) {
            pool.destroy();
        }
    }

    @Test
    public void testSubscribe() throws Exception {
        try (Jedis jedis = pool.getResource()) {
            jedis.subscribe(new JedisListener(), channel);
        }
    }

    public static class JedisListener extends JedisPubSub {
        /* (非 Javadoc)
         * @see redis.clients.jedis.JedisPubSub#onSubscribe(java.lang.String, int)
         */
        @Override
        public void onSubscribe(String channel, int subscribedChannels) {
            System.out.println("SUBSCRIBED channel=" + channel + ", " + subscribedChannels);
        }
        /* (非 Javadoc)
         * @see redis.clients.jedis.JedisPubSub#onMessage(java.lang.String, java.lang.String)
         */
        @Override
        public void onMessage(String channel, String message) {
            System.out.println("channel=" + channel);
            System.out.println("message=" + message);
        }
    }
}
