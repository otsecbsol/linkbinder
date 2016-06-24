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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import org.apache.commons.codec.binary.Hex;

/**
 * ハッシュを取得するためのクラス.
 * @author opentone
 */
public class HashUtil {
    /** ハッシュを求めるアルゴリズム. */
    private static final String ALGORITHM = "SHA-1";

    /**
     * インタンス化禁止.
     */
    private HashUtil() {
    }

    /**
     * ある決まった法則で文字列からハッシュを取得するためのメソッド.
     * <h3>法則の詳細</h3>
     * <pre>
     * 1. 文字列を3つ繋げる
     * 2. その文字列をインクリメント(a→b, 0→1, ...)する
     * 3. 定数ALGORITHMのアルゴリズムを利用してハッシュ値を求める
     *　</pre>
     * @param   parameter 暗号化対象文字列
     * @return  ハッシュ文字列
     */
    public static String getString(String parameter) {
        String result = null;

        if (parameter != null) {
            String newParameter = parameter + parameter + parameter;
            byte[] encodedParameter = newParameter.getBytes();
            for (int i = 0; i < encodedParameter.length; i++) {
                encodedParameter[i] = (byte) (encodedParameter[i] + 1);
            }
            try {
                MessageDigest md = MessageDigest.getInstance(ALGORITHM);
                md.update(encodedParameter);
                result = new String(Hex.encodeHex(md.digest()));
            } catch (NoSuchAlgorithmException e) {
                // 定数ALGORITHMが異常な場合。よってここには到達しない。
                throw new RuntimeException(e);
            }
        }
        return result;
    }

    /**
     * 常にランダムな値(ハッシュ値)を取得する(UUID.randomUUID()を使用).
     * @param   parameter ハッシュ生成の際、UUIDと同時に使用するハッシュ生成元文字列
     * @return  ハッシュ文字列
     */
    public static String getRandomString(String parameter) {
        String result = null;
        String salt = parameter + UUID.randomUUID().toString();

        try {
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            md.update(salt.getBytes());
            result = new String(Hex.encodeHex(md.digest()));
        } catch (NoSuchAlgorithmException e) {
            // 定数ALGORITHMが異常な場合。よってここには到達しない。
            throw new RuntimeException(e);
        }
        return result;
    }
}
