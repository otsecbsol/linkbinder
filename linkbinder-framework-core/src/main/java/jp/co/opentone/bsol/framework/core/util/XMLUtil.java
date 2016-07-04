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
package jp.co.opentone.bsol.framework.core.util;

import org.jdom.Verifier;

/**
 * XML文書に関する汎用ユーティリティ.
 * @author opentone
 */
public class XMLUtil {

    /**
     * 外部からのインスタンス化禁止.
     */
    private XMLUtil() {
    }

    /**
     * XML1.0で使用が禁止されている文字を取り除く.
     * @param str 対象の文字列
     * @return 結果文字列
     */
    public static String removeInvalidChars(String str) {
        StringBuilder result = new StringBuilder();
        for (char c : str.toCharArray()) {
            if (Verifier.isXMLCharacter(c)
                || Verifier.isXMLWhitespace(c)) {
                result.append(c);
            }
        }
        return result.toString();
    }
}
