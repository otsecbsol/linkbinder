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
package jp.co.opentone.bsol.framework.test.scope;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;

/**
 * JUnitでの単体テスト用クラス.request/sessionスコープのダミー.
 *
 * @author opentone
 */
public class MockScope implements Scope {

    /**
     * bean格納コンテナ.
     */
    private final Map<String, Object> beans = new HashMap<String, Object>();

    /**
     * デフォルトコンストラクタ.
     */
    public MockScope() {
    }

    @Override
    public Object get(String name, ObjectFactory<?> factory) {
        if (!beans.containsKey(name)) {
            Object bean = factory.getObject();
            beans.put(name, bean);
        }
        return beans.get(name);
    }

    @Override
    public String getConversationId() {
        return null;
    }

    @Override
    public void registerDestructionCallback(String arg0, Runnable arg1) {
    }

    @Override
    public Object remove(String name) {
        if (beans.containsKey(name)) {
            return beans.remove(name);
        }
        return null;
    }

    @Override
    public Object resolveContextualObject(String key) {
        return null;
    }
}
