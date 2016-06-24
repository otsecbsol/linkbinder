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
package jp.co.opentone.bsol.linkbinder.util;

import java.io.Serializable;
import java.text.DecimalFormat;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.StringUtils;

import jp.co.opentone.bsol.linkbinder.dto.CorresponGroupUser;
import jp.co.opentone.bsol.linkbinder.dto.User;

/**
 * 値を変換するクラス.
 * @author opentone
 */
public class ValueFormatter implements Serializable {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 4315647716120851740L;

    /**
     * 表示用：条件に該当する場合の文字列.
     */
    public static final String MATCH = "○";

    /**
     * 表示用：ユーザ情報の区切り文字.
     */
    public static final String DELIM_USER = "/";

    /**
     * 表示用：コードの区切り文字.
     */
    public static final String DELIM_CODE = " : ";

    /**
     * 表示用：役職のフォーマット.
     */
    private static final String FORMAT_ROLE = " (%s)";

    /**
     * 表示用：数値のフォーマット.
     */
    private static final String FORMAT_NUMBER = "#,###";

    /**
     * 表示用：グループを囲む括弧(左側).
     */
    private static final String BRACKETS_LEFT = " [";

    /**
     * 表示用：グループを囲む括弧(右側).
     */
    private static final String BRACKETS_RIGHT = "]";

    /**
     * 内部用：グループに所属していないことを表す.
     */
    private static final Long JOIN_NO_GROUP = -1L;

    /**
     * コンストラクタ.
     */
    private ValueFormatter() {
    }

    /**
     * コードと名前を表示用の文字列に変換する.
     * 区切り文字は{@link #DELIM_CODE}.
     * @param code コード
     * @param name 名前
     * @return 表示用の文字列
     */
    public static String formatCodeAndName(String code, String name) {
        return formatCodeAndName(code, name, DELIM_CODE);
    }

    /**
     * コードと名前を表示用の文字列に変換する.
     * 区切り文字は指定文字.
     * @param code コード
     * @param name 名前
     * @param delim 区切り文字
     * @return 表示用の文字列
     */
    public static String formatCodeAndName(String code, String name, String delim) {
        String str = convertNullValue(code)
                + delim + convertNullValue(name);
        if (delim.equals(str)) {
            return "";
        }
        return str;
    }

    /**
     * 値がNULLの場合に空文字に変換する.
     * 値がNULLではない場合、値をそのまま返却する.
     * @param value 値
     * @return 空文字 or 値
     */
    private static String convertNullValue(String value) {
        if (value == null) {
            return "";
        }
        return value;
    }

    /**
     * 値がチェック値と等しければ、ラベルに変換する.
     * それ以外は空文字に変換する.
     * @param value 値
     * @param code チェック値
     * @return ラベル
     */
    public static String formatLabel(Object value, Object code) {
        if (code == null || !code.equals(value)) {
            return "";
        }
        return MATCH;
    }


    /**
     * ユーザーの名前と従業員番号を表示用の文字列に変換する.
     * @param user ユーザー
     * @return ラベル
     */
    public static String formatUserNameAndEmpNo(User user) {
        String str = convertNullValue(user.getNameE())
                        + DELIM_USER + convertNullValue(user.getEmpNo());
        if (DELIM_USER.equals(str)) {
            return "";
        }
        return str;
    }

    /**
     * ユーザーの名前と従業員番号と役職を表示用の文字列に変換する.
     * @param user ユーザー
     * @return ラベル
     */
    public static String formatUserNameAndEmpNoAndRole(User user) {
        String str = formatUserNameAndEmpNo(user);
        if (!StringUtils.isEmpty(user.getRole())) {
            str += String.format(FORMAT_ROLE, user.getRole());
        }
        return str;
    }

    /**
     * ユーザーの名前と従業員番号と所属グループを表示用の文字列に変換する.
     * @param correspoUser ユーザー
     * @param id コレポングループID
     * @return ラベル
     */
    public static String formatUserNameAndEmpNoAndGroup(
            CorresponGroupUser correspoUser, Long id) {
        String str = convertNullValue(correspoUser.getUser().getNameE())
                    + DELIM_USER
                    + convertNullValue(correspoUser.getUser().getEmpNo());
        if (DELIM_USER.equals(str)) {
            return "";
        }

        if (!id.equals(JOIN_NO_GROUP)) {
            str = str + BRACKETS_LEFT
                      + convertNullValue(
                              correspoUser.getCorresponGroup().getName())
                      + BRACKETS_RIGHT;
        }
        return str;
    }

    /**
     * ユーザーの名前と従業員番号と役職を表示用の文字列に変換する.
     * @param correspoUser ユーザー
     * @param id コレポングループID
     * @return ラベル
     */
    public static String formatUserNameAndEmpNoAndRoleAndGroup(
            CorresponGroupUser correspoUser, Long id) {
        String str = convertNullValue(correspoUser.getUser().getNameE())
                    + DELIM_USER
                    + convertNullValue(correspoUser.getUser().getEmpNo());
        if (DELIM_USER.equals(str)) {
            return "";
        }
        if (!StringUtils.isEmpty(correspoUser.getUser().getRole())) {
            str += String.format(FORMAT_ROLE, correspoUser.getUser().getRole());
        }
        if (!id.equals(JOIN_NO_GROUP)) {
            str = str + BRACKETS_LEFT
                      + convertNullValue(
                              correspoUser.getCorresponGroup().getName())
                      + BRACKETS_RIGHT;
        }
        return str;
    }

    /**
     * 数値を表示用の文字列(9,999,999)に変換する.
     * @param number 数値
     * @return 表示用数値文字列
     */
    public static String formatNumber(int number) {
        DecimalFormat formatter = new DecimalFormat(FORMAT_NUMBER);
        return formatter.format(number);
    }


    /**
     * 値をキーを用いてSHA256に変換する
     * @param value 変換する値
     * @param key キー
     * @return 変換後の値
     */
    public static String formatValueToHash(String value, String key) {
        byte[] result;
        try {
            SecretKeySpec sk = new SecretKeySpec(key.getBytes("UTF-8"), "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(sk);
            result = mac.doFinal(value.getBytes("UTF-8"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new String(Hex.encodeHex(result));

    }
}
