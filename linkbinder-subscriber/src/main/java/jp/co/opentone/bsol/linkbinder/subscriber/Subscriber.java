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
import jp.co.opentone.bsol.linkbinder.dto.User;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

/**
 * Redisのsubscriber.
 * @author opentone
 */
public abstract class Subscriber {
    /** logger. */
    protected Logger log = LogUtil.getLogger();

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
     * このオブジェクトが対象とするイベント名の一覧を返す.
     * @return イベント名の一覧
     */
    public abstract String[] getMyEvents();

    /**
     * 処理情報を初期化する.
     * @param projectId プロジェクトID
     */
    protected void setupProcessContext(String projectId) {
        SubscriberUtil.setupProcessContext(projectId, user);
    }

    /**
     * JSON文字列をオブジェクトに変換する.
     * @param json JSON
     * @param clazz 変換後オブジェクトのクラス名
     * @return 変換後オブジェクト
     */
    protected <T> T parseJsonMessage(String json, Class<T> clazz) {
        return SubscriberUtil.parseJsonMessage(json, clazz);
    }

    public abstract  void onMessage(String channel, String message);
}
