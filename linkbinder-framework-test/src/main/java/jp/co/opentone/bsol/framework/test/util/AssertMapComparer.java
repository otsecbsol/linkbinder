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

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import junit.framework.Assert;

/**
 * Map型のデータとPOJO型オブジェクト内のデータを比較するユーティリティクラス.
 * <p>
 * Mapに登録されているキーをフィールド名として、比較対象オブジェクト内から
 * フィールド値を取得して値を比較します.
 * <pre>
 * 例）
 * Map("projectId", "123-456")のように値が格納されている場合、
 * 比較対象オブジェクトのgetProjectIdで得られる値と内容を比較します.
 * </pre>
 * Mapのキー値とフィールド名が異なる場合や、一部のキー値を比較対象から除外する
 * 場合は、フィールドexceptMapを使用します.
 * @author opentone
 */
public class AssertMapComparer {

    /**
     * 比較方法を表す列挙値.
     */
    // CHECKSTYLE:OFF
    public enum AssertCompareType {
        STRING,         // 文字列型(デフォルト)
        BIGDECIMAL,     // BigDecimal型
        DATE,           // 日付型
        TIMESTAMP,      // 時刻型
    };
    // CHECKSTYLE:ON

    /**
     * 日付を比較する際の変換書式.
     */
    public static final String FORMAT_DATE = "yyyy/MM/dd";

    /**
     * 時刻を比較する際の変換書式.
     */
    public static final String FORMAT_TIME = "yyyy/MM/dd HH:mm:ss";

    /**
     * 比較するテーブル列名とその比較型.
     * <p>
     * Map内のキー名は設定名を大文字にした名前が対応する比較される.
     * Object内のフィールド名はアンダースコア後ろを大文字にしたフィールドが対応する.
     * <pre>
     * 例）
     * compMapに"project_id"を設定した場合は、
     * Mapの要素"PROJECT_ID"とObjectのフィールド名projectIdを比較する.
     * </pre>
     */
    private Map<String, AssertCompareType> compareKeys;

    /**
     * 比較対象のMap.
     */
    private Map<String, Object> compareMap;

    /**
     * 比較対象オブジェクト.
     */
    private Object compareObj;

    /**
     * 検証結果リスト.
     */
    private List<String> compareResult = new ArrayList<String>();

    /**
     * デフォルトコンストラクタ.
     */
    public AssertMapComparer() {
        compareKeys = new HashMap<String, AssertCompareType>();
    }

    /**
     * コンストラクタ.
     * @param compareMap 比較対象のMap
     * @param compareObj 比較対象のオブジェクト
     */
    public AssertMapComparer(Map<String, Object> compareMap, Object compareObj) {
        this();
        this.compareMap = compareMap;
        this.compareObj = compareObj;
    }

    /**
     * 比較キーを1件、文字列型で登録する.
     * @param compareKey 比較キー
     */
    public void addCompareKey(String compareKey) {
        this.compareKeys.put(compareKey, AssertCompareType.STRING);
    }

    /**
     * 比較キーを複数件、文字列型で登録する.
     * @param compareKeyArray 比較キー配列
     */
    public void addCompareKey(String[] compareKeyArray) {
        if (null != compareKeyArray) {
            for (String compareKey : compareKeyArray) {
                addCompareKey(compareKey);
            }
        }
    }

    /**
     * 比較キーを1件、指定した型で登録する.
     * キーが登録済みの場合は更新します.
     * @param compareKey 比較キー
     * @param compareType 比較タイプ
     */
    public void addCompareKey(String compareKey, AssertCompareType compareType) {
        if (StringUtils.isNotEmpty(compareKey)) {
            if (this.compareKeys.containsKey(compareKey)) {
                this.compareKeys.remove(compareKey);
            }
            this.compareKeys.put(compareKey, compareType);
        }
    }

    /**
     * 比較キーを1件、指定した型で登録する.
     * キーが未登録の場合は何も処理を行いません.
     * @param compareKey 比較キー
     * @param compareType 比較タイプ
     */
    public void updateCompareKey(String compareKey, AssertCompareType compareType) {
        if (StringUtils.isNotEmpty(compareKey)) {
            if (this.compareKeys.containsKey(compareKey)) {
                this.compareKeys.remove(compareKey);
                this.compareKeys.put(compareKey, compareType);
            }
        }
    }

    /**
     * 比較キーをクリアする.
     */
    public void clearCompareKey() {
        this.compareKeys.clear();
    }

    /**
     * 比較対象のMapを取得します.
     * @return 比較対象のMap.
     */
    public Map<String, Object> getCompareMap() {
        return compareMap;
    }

    /**
     * 比較対象のMapを設定します.
     * @param compareMap 比較対象のMap.
     */
    public void setCompareMap(Map<String, Object> compareMap) {
        this.compareMap = compareMap;
    }

    /**
     * 比較対象オブジェクトを取得します.
     * @return 比較対象オブジェクト.
     */
    public Object getCompareObj() {
        return compareObj;
    }

    /**
     * 比較対象オブジェクトを設定します.
     * @param compareObj 比較対象オブジェクト.
     */
    public void setCompareObj(Object compareObj) {
        this.compareObj = compareObj;
    }

    /**
     * 検証結果リスト.を取得します.
     * @return 検証結果リスト.
     */
    public List<String> getCompareResult() {
        return compareResult;
    }

    /**
     * 比較を行います.
     */
    public void assertCompare() {
        compareResult.clear();
        if (null == compareMap || null == compareObj) {
            throw new RuntimeException("Field[compareMap] and field[compareDto] must be set.");
        }
        if (null == compareKeys || 0 == compareKeys.size()) {
            throw new RuntimeException("Field[compareKeys] must be set.");
        }
        Set<Entry<String, AssertCompareType>> keySet = compareKeys.entrySet();
        Iterator<Entry<String, AssertCompareType>> iterator = keySet.iterator();
        while (iterator.hasNext()) {
            Entry<String, AssertCompareType> entry = iterator.next();
            String orgKey = entry.getKey();

            String mapKey = orgKey.toUpperCase();
            String objFieldName = toFieldName(orgKey);
            if (null != objFieldName) {
                Object mapValue =  compareMap.get(mapKey);
                Object objValue = getObjectValue(objFieldName);
                compare(orgKey, mapValue, objValue);
            }
        }
    }

    /**
     * 値を比較する.
     * @param mapKey キー名
     * @param mapValue 比較する値1
     * @param objValue 比較する値2
     */
    private void compare(String mapKey, Object mapValue, Object objValue) {
        // 両方がnullの場合は比較不要
        AssertCompareType ruledType = compareKeys.get(mapKey);
        if (null != mapValue && null != objValue) {
            Object convertMapValue = getTypeValue(ruledType, mapValue);
            Object convertObjValue = getTypeValue(ruledType, objValue);
            Assert.assertEquals(convertMapValue, convertObjValue);

            String resultMessage
                = String.format("compare[type=%s]:[key=%s]:exp=[%s], act=[%s]",
                        ruledType.toString(),
                        mapKey,
                        convertMapValue.toString(),
                        convertObjValue.toString());
            compareResult.add(resultMessage);
        } else if (null != mapValue || null != objValue) {
            Assert.assertEquals(String.format("varName=[%s]", mapKey), mapValue, objValue);
            if (null == mapValue) {
                compareResult.add(
                        String.format("compare[type=%s]:[key=%s]:exp=null, act=[%s]",
                                ruledType.toString(), mapKey, objValue.toString()));
            } else {
                compareResult.add(
                        String.format("compare[type=%s]:[key=%s]:exp=[%s], act=null",
                                ruledType.toString(), mapKey, mapValue.toString()));
            }
        } else {
            compareResult.add(
                    String.format("compare[type=%s]:[key=%s]:exp=null, act=null",
                            ruledType.toString(), mapKey));
        }
    }

    /**
     * 指定した比較タイプに応じたオブジェクト値の変換を行う.
     * @param type 比較タイプ
     * @param obj 変換対象のオブジェクト
     * @return 変換後のオブジェクト
     */
    private Object getTypeValue(AssertCompareType type, Object obj) {
        Object result = null;
        if (AssertCompareType.BIGDECIMAL == type) {
            result = toBigDecimal(obj);
        } else if (AssertCompareType.DATE == type) {
            result = toDate(obj);
        } else if (AssertCompareType.TIMESTAMP == type) {
            result = toTimestamp(obj);
        } else {
            result = obj;
        }
        return result;
    }
    /**
     * Reflectionを利用して、オブジェクトからgetter経由で値を取得する.
     * @param dtoKey フィールド名
     * @return 取得値. 取得できなかった場合はn
     */
    private Object getObjectValue(String objFieldName)
        throws IllegalArgumentException {
        if (StringUtils.isEmpty(objFieldName)) {
            throw new IllegalArgumentException("Arg[dtoFieldName] must not be empty.");
        }
        Object value = null;
        try {
            String methodName = String.format("%s%s%s",
                                            "get",
                                            objFieldName.substring(0, 1).toUpperCase(),
                                            objFieldName.substring(1));
            Method method = compareObj.getClass().getMethod(methodName, (Class[]) null);
            value = method.invoke(compareObj, (Object[]) null);
        } catch (Exception exp) {
            exp.printStackTrace();
            throw new RuntimeException(exp);
        }
        return value;
    }

    /**
     * 引数objから変換したBigDecimal型を返す.
     * objがBigDecimal型の場合は引数をそのまま返す.
     * @param obj 変換対象のオブジェクト
     * @return 変換したBigDecimalオブジェクト
     */
    private BigDecimal toBigDecimal(Object obj) {
        BigDecimal result = null;
        if (obj instanceof BigDecimal) {
            result = (BigDecimal) obj;
        } else {
            result = new BigDecimal(obj.toString());
        }
        return result;
    }

    /**
     * 引数objから生成した日付文字列を返す.
     * objがDate型でない場合は単にtoString()の結果を返す.
     * @param obj 対象のオブジェクト
     * @return 生成した文字列
     */
    private Date toDate(Object obj) {
        return convertDate(obj, FORMAT_DATE);
    }

    /**
     * 引数objから生成した時刻文字列を返す.
     * objがDate型でない場合は単にtoString()の結果を返す.
     * @param obj 対象のオブジェクト
     * @return 生成した文字列
     */
    private Date toTimestamp(Object obj) {
        return convertDate(obj, FORMAT_TIME);
    }

    /**
     * Dateオブジェクトを作成する.
     * objがDate型の場合は、そのままの内容を返す.
     * @param obj 対象のオブジェクト
     * @param format 書式
     * @return 生成した文字列
     */
    private Date convertDate(Object obj, String format) {
        Date result = null;
        try {
            if (null != obj) {
                if (obj instanceof Date) {
                    result = (Date) obj;
                } else if (obj instanceof oracle.sql.Datum) {
                    if (obj instanceof oracle.sql.TIMESTAMP) {
                        java.sql.Timestamp t
                            = (java.sql.Timestamp) ((oracle.sql.TIMESTAMP) obj).toJdbc();
                        result = new Date(t.getTime());
                    } else if (obj instanceof oracle.sql.DATE) {
                        java.sql.Date d
                        = (java.sql.Date) ((oracle.sql.DATE) obj).toJdbc();
                        result = new Date(d.getTime());
                    } else {
                        throw new IllegalArgumentException(
                            "not implemented. type[" + obj.getClass().toString() + "]");
                    }
                } else {
                    result = DateUtils.parseDate(
                            obj.toString(), new String[] {format});
                }
            }
        } catch (ParseException pe) {
            pe.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * キー名からフィールド名を生成する.
     * <p>
     * "project_id"が引数の場合は"projectId"が戻り値となる。
     * @param keyName 変換対象のキー名
     * @return フィールド名
     */
    private String toFieldName(String keyName) {
        String fieldName = null;
        if (null != keyName) {
            StringBuilder builder = new StringBuilder();
            boolean isPrevUnderScore = false;
            for (int i = 0; i < keyName.length(); i++) {
                char ch = keyName.charAt(i);
                if ('_' == ch) {
                    isPrevUnderScore = true;
                } else {
                    if (isPrevUnderScore) {
                        builder.append(Character.toUpperCase(ch));
                        isPrevUnderScore = false;
                    } else {
                        builder.append(ch);
                    }
                }
            }
            fieldName = builder.toString();
        }
        return fieldName;
    }
}
