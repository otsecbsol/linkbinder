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

import org.apache.commons.lang.StringUtils;

/**
 * 引数を検証するクラス.
 * @author opentone
 */
public final class ArgumentValidator {

    /**
     * コンストラクタ. 外部からのインスタンス化はできない.
     */
    private ArgumentValidator() {
    }

    /**
     * {@link #validateNotNull(Object, String)}.
     * @param arg {@link #validateNotNull(Object, String)}
     */

    public static void validateNotNull(Object arg) {
        validateNotNull(arg, null);
    }

    /**
     * 引数がnullでないことを検証する. nullの場合はIllegalArgumentExceptionが発生する.
     * @param arg 検証対象の引数
     * @param name 引数の名前
     */
    public static void validateNotNull(Object arg, String name) {
        if (arg == null) {
            String n = getName(name);
            throw new IllegalArgumentException(n + " must not be null.");
        }
    }

    /**
     * {@link #validateNotEmpty(String, String)}.
     * @param arg {@link #validateNotEmpty(String, String)}
     */
    public static void validateNotEmpty(String arg) {
        validateNotEmpty(arg, null);
    }

    /**
     * 引数が空でないことを検証する. 空の場合はIllegalArgumentExceptionが発生する.
     * @param arg
     *            検証対象の引数
     * @param name
     *            引数の名前
     */
    public static void validateNotEmpty(String arg, String name) {
        if (StringUtils.isEmpty(arg)) {
            String n = getName(name);
            throw new IllegalArgumentException(n + " must not be empty");
        }
    }

    /**
     * {@link #validateGreaterThan(Long, Long, String)}.
     * @param arg {@link #validateGreaterThan(Long, Long, String)}
     * @param compValue {@link #validateGreaterThan(Long, Long, String)}
     */
    public static void validateGreaterThan(Long arg, Long compValue) {
        validateGreaterThan(arg, compValue, null);
    }

    /**
     * 値が指定した値より大きいことを検証する.
     * 同じかまたは小さい場合はIllegalArgumentExceptionが発生する.
     * 引数の少なくとも一方がnullの場合もIllegalArgumentExceptionが発生する.
     * @param arg 検証対象の値
     * @param compValue 比較する値
     * @param name 名前
     */
    public static void validateGreaterThan(Long arg, Long compValue, String name) {
        validateNotNull(arg, name);
        validateNotNull(compValue, name);
        if (arg == compValue || arg < compValue) {
            String n = getName(name);
            throw new IllegalArgumentException(n + " must greater than compare value");
        }
    }

    /**
     * {@link #validateEquals(String, String, String)}.
     * @param arg {@link #validateEquals(String, String, String)}
     * @param compValue {@link #validateEquals(String, String, String)}
     */
    public static void validateEquals(String arg, String compValue) {
        validateEquals(arg, compValue, null);
    }

    /**
     * 値が指定した値より大きいことを検証する.
     * 同じかまたは小さい場合はIllegalArgumentExceptionが発生する.
     * 引数の少なくとも一方がnullの場合もIllegalArgumentExceptionが発生する.
     * @param arg 検証対象の値
     * @param compValue 比較する値
     * @param name 名前
     */
    public static void validateEquals(String arg, String compValue, String name) {
        validateNotNull(arg);
        validateNotNull(compValue);
        if (0 != arg.compareTo(compValue)) {
            String n = getName(name);
            throw new IllegalArgumentException(n + " should be equal to compare value");
        }
    }

    /**
     * メッセージに設定する名前を取得する.
     * @param name 引数で渡されたname
     * @return メッセージに設定する名前
     */
    private static String getName(String name) {
        return (StringUtils.isNotEmpty(name)) ? name :  "Argument";
    }
}
