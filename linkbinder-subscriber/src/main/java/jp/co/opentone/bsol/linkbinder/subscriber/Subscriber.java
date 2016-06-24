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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;

import jp.co.opentone.bsol.framework.core.ProcessContext;
import jp.co.opentone.bsol.framework.core.config.SystemConfig;
import jp.co.opentone.bsol.framework.core.exception.ApplicationFatalRuntimeException;
import jp.co.opentone.bsol.framework.core.util.LogUtil;
import jp.co.opentone.bsol.linkbinder.Constants;
import jp.co.opentone.bsol.linkbinder.dto.Project;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.event.DomainEventListener;
import jp.co.opentone.bsol.linkbinder.event.EventBus;

/**
 * Redisのsubscriber.
 * @author opentone
 */
public abstract class Subscriber extends DomainEventListener {
    /** logger. */
    protected Logger log = LogUtil.getLogger();

    /** Event Bus. */
    @Autowired
    private EventBus eventBus;

    /** ユーザー情報. */
    @Autowired
    private User user;

    /**
     * 初期化処理.
     */
    @PostConstruct
    public void setup() {
    }

    /**
     * 処理情報を初期化する.
     * @param projectId プロジェクトID
     */
    protected void setupProcessContext(String projectId) {
        ProcessContext pc = ProcessContext.getCurrentContext();

        pc.setValue(SystemConfig.KEY_USER_ID, user.getUserId());
        pc.setValue(SystemConfig.KEY_USER, user);

        Map<String, Object> values = new HashMap<String, Object>();

        Project p = new Project();
        p.setProjectId(projectId);
        values.put(Constants.KEY_PROJECT, p);
        pc.setValue(SystemConfig.KEY_ACTION_VALUES, values);
    }

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

    /**
     * JSON文字列をオブジェクトに変換する.
     * @param json JSON
     * @param clazz 変換後オブジェクトのクラス名
     * @return 変換後オブジェクト
     */
    protected <T> T parseJsonMessage(String json, Class<T> clazz) {
        ObjectMapper m = new ObjectMapper();
        try {
            return m.readValue(json, clazz);
        } catch (IOException e) {
            throw new ApplicationFatalRuntimeException(e);
        }
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
}
