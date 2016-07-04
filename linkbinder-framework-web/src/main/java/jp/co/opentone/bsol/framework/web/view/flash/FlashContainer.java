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
package jp.co.opentone.bsol.framework.web.view.flash;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * リクエスト間でデータを保持するコンテナ.
 * @author opentone
 */
class FlashContainer implements Serializable {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 3470081553468144467L;

    /**
     * 前のリクエストの値を保持するコンテナ.
     */
    private Map<String, Object> previousValues = createMap();
    /**
     * 現在のリクエストの値を保持するコンテナ.
     */
    private Map<String, Object> currentValues = createMap();

    /**
     * 空のインスタンスを生成する.
     */
    public FlashContainer() {
    }

    private Map<String, Object> createMap() {
        return Collections.synchronizedMap(new HashMap<String, Object>());
    }

    public void setValue(String key, Object value) {
        currentValues.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T getValue(String key) {
        if (currentValues.containsKey(key)) {
            return (T) currentValues.get(key);
        }
        return (T) previousValues.get(key);
    }

    public void transfer(String key) {
        setValue(key, getValue(key));
    }

    public void transferAll() {
        currentValues.putAll(previousValues);
        previousValues.clear();
    }

    public void refresh() {
        previousValues.clear();
        previousValues = currentValues;
        currentValues = createMap();
    }
}
