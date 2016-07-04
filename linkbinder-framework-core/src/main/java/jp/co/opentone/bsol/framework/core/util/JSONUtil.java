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

import net.arnx.jsonic.JSON;

/**
 * オブジェクトをJSON形式に変換するユーティリティ.
 * framework-web から移管。
 * @author opentone
 */
public class JSONUtil {

    /**
     * ユーティリティクラスなのでインスタンス化しない.
     */
    private JSONUtil() {
    }

    /**
     * オブジェクトをJSON形式に変換して返す.
     * @param source 変換対象. nullの場合例外が発生する.
     * @return 変換後のJSON文字列
     */
    public static String encode(Object source) {
        return replace(JSON.encode(source));
    }


    /**
     * JSON形式のテキストをエスケープする.
     * @param jsonText JSON形式のテキスト. nullの場合例外が発生する
     * @return エスケープ後の文字列
     */
    public static String replace(String jsonText) {
        // 必要な変換があればここに記載する

        return jsonText;
    }

    /**
     * JSON形式のテキストをオブジェクトに変換して返す.
     * @param json JSON形式テキスト
     * @return 変換後のオブジェクト
     */
    public static Object decode(String json) {
        return JSON.decode(json);
    }

    /**
     * JSON形式のテキストをオブジェクトに変換して返す.
     * @param <Object> オブジェクト
     * @param json JSON形式テキスト
     * @param clazz 変換後のクラス
     * @return 変換後のオブジェクト
     */
    @SuppressWarnings("hiding")
    public static <Object> Object decode(String json, Class<? extends Object> clazz) {
        return JSON.decode(json, clazz);
    }
}
