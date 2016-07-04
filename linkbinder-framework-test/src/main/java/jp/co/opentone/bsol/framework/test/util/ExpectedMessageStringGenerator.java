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
package jp.co.opentone.bsol.framework.test.util;

import static org.junit.Assert.*;

import java.text.MessageFormat;

import org.apache.commons.lang.StringUtils;

/**
 * 期待値として利用するメッセージ文字列を生成するクラス.
 * @author opentone
 */
public class ExpectedMessageStringGenerator {

    public static String generate(
        String msg, String actionName, Object... vars) {

        if (StringUtils.isNotEmpty(actionName)) {
            msg = msg.replace("$action$", "操作：[" + actionName + "]");
        }

        assertTrue(!msg.matches("\\$action\\$"));

        String result = MessageFormat.format(msg, vars);
        assertTrue(!result.matches("\\{[0-9]+\\}"));

        return result;
    }
}
