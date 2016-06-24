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

import java.lang.reflect.Array;
import java.util.Date;

import org.apache.commons.lang.ArrayUtils;

/**
 * オブジェクトの内容をコピーするユーティリティ.
 * @author opentone
 */
public class CloneUtil {

    /**
     * ユーティリティクラスのため外部からのインスタンス化禁止.
     */
    private CloneUtil() {
    }

    /**
     * 日付オブジェクトのコピーを返す.
     * @param date コピー元
     * @return コピーしたオブジェクト
     */
    public static Date cloneDate(Date date) {
        if (date == null) {
            return null;
        }
        return new Date(date.getTime());
    }

    /**
     * int配列のコピーを返す.
     * @param array コピー元
     * @return コピーしたオブジェクト. arrayがnullの場合は空の配列が返される.
     */
    public static int[] cloneArray(int[] array) {
        if (array == null) {
            return new int[0];
        }
        return ArrayUtils.clone(array);
    }

    /**
     * 任意の型の配列のコピーを返す.
     * @param <T> 配列の型
     * @param clazz 配列のクラス
     * @param array コピー元
     * @return コピーしたオブジェクト. arrayがnullの場合は空の配列が返される.
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] cloneArray(Class<T> clazz, T[] array) {
        if (array == null) {
            return (T[]) Array.newInstance(clazz, 0);
        }
        return (T[]) ArrayUtils.clone(array);
    }
}
