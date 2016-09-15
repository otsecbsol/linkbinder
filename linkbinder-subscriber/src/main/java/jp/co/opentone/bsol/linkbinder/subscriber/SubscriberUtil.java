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

import com.fasterxml.jackson.databind.ObjectMapper;
import jp.co.opentone.bsol.framework.core.ProcessContext;
import jp.co.opentone.bsol.framework.core.config.SystemConfig;
import jp.co.opentone.bsol.framework.core.exception.ApplicationFatalRuntimeException;
import jp.co.opentone.bsol.linkbinder.Constants;
import jp.co.opentone.bsol.linkbinder.dto.Project;
import jp.co.opentone.bsol.linkbinder.dto.User;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * ユーティリティ.
 * @author opentone
 */
public class SubscriberUtil {

    /**
     * JSON文字列をオブジェクトに変換する.
     * @param json JSON
     * @param clazz 変換後オブジェクトのクラス名
     * @return 変換後オブジェクト
     */
    public static <T> T parseJsonMessage(String json, Class<T> clazz) {
        ObjectMapper m = new ObjectMapper();
        try {
            return m.readValue(json, clazz);
        } catch (IOException e) {
            throw new ApplicationFatalRuntimeException(e);
        }
    }

    /**
     * 処理情報を初期化する.
     * @param projectId プロジェクトID
     * @param user ユーザー
     */
    public static void setupProcessContext(String projectId, User user) {
        ProcessContext pc = ProcessContext.getCurrentContext();

        pc.setValue(SystemConfig.KEY_USER_ID, user.getUserId());
        pc.setValue(SystemConfig.KEY_USER, user);

        Map<String, Object> values = new HashMap<>();

        Project p = new Project();
        p.setProjectId(projectId);
        values.put(Constants.KEY_PROJECT, p);
        pc.setValue(SystemConfig.KEY_ACTION_VALUES, values);
    }
}
