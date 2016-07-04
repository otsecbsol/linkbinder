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
package jp.co.opentone.bsol.linkbinder.dto;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DTOの抽象クラス.
 *
 * @author opentone
 *
 */
public abstract class AbstractDto implements Serializable, Cloneable {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -552468979469318253L;

    /**
     * このクラスのオブジェクトの文字列表現に含まないフィールドの名前.
     * @see #toString()
     */
    private static final Set<String> TO_STRING_IGNORE_FIELDS;
    static {
        Set<String> fields = new HashSet<String>();
        fields.add("log");
        fields.add("logger");
        TO_STRING_IGNORE_FIELDS = Collections.unmodifiableSet(fields);
    }

    /**
     * logger.
     */
    private final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * 前方一致検索時のエスケープ文字.
     */
    private String escapeChar = "@";

    /**
     * 空のインスタンスを生成する.
     */
    public AbstractDto() {
    }

    /*
     * (非 Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        // 親クラスを含む全てのインスタンスフィールドを一つの文字列に変換する
        ToStringBuilder tsb = new ToStringBuilder(this, ToStringStyle.DEFAULT_STYLE);
        try {
            for (Field field : getFields(this.getClass())) {
                if (isToStringIgnoreField(field.getName())) {
                    continue;
                }
                // フィールドへのアクセス可能フラグをONにして値を取得
                field.setAccessible(true);
                tsb.append(field.getName(), field.get(this));
            }
        } catch (IllegalArgumentException e) {
            log.warn(e.getMessage(), e);
        } catch (IllegalAccessException e) {
            log.warn(e.getMessage(), e);
        }

        // このオブジェクトの文字列表現を同値比較で使えるようにするため、
        // ObjectのIDを除去して返す
        return removeObjectId(tsb.toString());
    }

    /**
     * このオブジェクトの文字列表現から、オブジェクトのIDを表す部分を削除する.
     * @param str
     *            オブジェクトの文字列表現
     * @return オブジェクトIDを削除した文字列
     */
    private String removeObjectId(String str) {
        return str.replaceFirst("@(\\d|\\w)+\\[", "[");
    }

    /**
     * aClassに定義されたインスタンスフィールドをListに格納して返す.
     * @param aClass
     *            class
     * @return インスタンスフィールドリスト
     */
    private List<Field> getInstanceFields(Class<?> aClass) {

        List<Field> fields = new ArrayList<Field>();
        for (Field field : aClass.getDeclaredFields()) {
            if (!Modifier.isStatic(field.getModifiers())) {
                fields.add(field);
            }
        }
        return fields;
    }

    /**
     * clazzが保持する全てのインスタンスフィールドを返す. 親クラスのフィールドも含まれる.
     * @param clazz
     *            対象のクラス
     * @return 全てのインスタンスフィールド
     */
    private Field[] getFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<Field>();
        Class<?> aClass = clazz;
        while (true) {
            fields.addAll(getInstanceFields(aClass));

            // 当クラスまで階層を遡ってきたら終了
            if (aClass == AbstractDto.class) {
                break;
            }

            aClass = aClass.getSuperclass();
        }
        return fields.toArray(new Field[]{});
    }

    /**
     * {@link #toString()}での文字列表現に含まないフィールドの場合はtrueを返す.
     * @param fieldName フィールド名
     * @return 文字列表現に含まない場合はtrue
     */
    public boolean isToStringIgnoreField(String fieldName) {
        return TO_STRING_IGNORE_FIELDS.contains(fieldName);
    }

    /**
     * エスケープ文字を返却する.
     * @return エスケープ文字
     */
    public String getEscapeChar() {
        return escapeChar;
    }

    /**
     * エスケープ文字を設定する.
     * @param escapeChar エスケープ文字
     */
    public void setEscapeChar(String escapeChar) {
        this.escapeChar = escapeChar;
    }

    /**
     * Date型のcloneを作成します.
     * @param value 作成元オブジェクト
     * @return 作成したcloneオブジェクト. 引数valueがnullの場合はnull.
     */
    protected Date cloneField(Date value) {
        Date clone = null;
        if (null != value) {
            clone = (Date) value.clone();
        }
        return clone;
    }

    /**
     * Long型のcloneを作成します.
     * @param value 作成元オブジェクト
     * @return 作成したcloneオブジェクト. 引数valueがnullの場合はnull.
     */
    protected Long cloneField(Long value) {
        Long clone = null;
        if (null != value) {
            clone = new Long(value.longValue());
        }
        return clone;
    }

    /**
     * Integer型のcloneを作成します.
     * @param value 作成元オブジェクト
     * @return 作成したcloneオブジェクト. 引数valueがnullの場合はnull.
     */
    protected Integer cloneField(Integer value) {
        Integer clone = null;
        if (null != value) {
            clone = new Integer(value.intValue());
        }
        return clone;
    }

    /**
     * Double型のcloneを作成します.
     * BigDecimal型のcloneを作成します.
     * @param value 作成元オブジェクト
     * @return 作成したcloneオブジェクト. 引数valueがnullの場合はnull.
     */
    protected Double cloneField(Double value) {
        Double clone = null;
        if (null != value) {
            clone = new Double(value.doubleValue());
        }
        return clone;
    }

    protected Object cloneField(Serializable value) {
        Object rt = null;
        if (value != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos;
            try {
                oos = new ObjectOutputStream(baos);
                oos.writeObject(value);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            ObjectInputStream ois;
            try {
                ois = new ObjectInputStream(bais);
                rt = ois.readObject();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return rt;
    }

    /**
     * BigDecimal型のcloneを作成します.
     * @param value 作成元オブジェクト
     * @return 作成したcloneオブジェクト. 引数valueがnullの場合はnull.
     */
    protected BigDecimal cloneField(Object value) {
        BigDecimal clone = null;
        if (value != null) {
            if (value instanceof Long) {
                clone = new BigDecimal((Long) value);
            } else if (value instanceof Integer) {
                clone = new BigDecimal((Integer) value);
            } else if (value instanceof Double) {
                clone = new BigDecimal((Double) value);
            } else if (value instanceof BigDecimal) {
                clone = new BigDecimal(((BigDecimal) value).doubleValue());
            } else {
                throw new RuntimeException("IllegalArgument");
            }
        }
        return clone;
    }

    /**
     * ListのCloneを作成します(Deep Copy).
     * @param list 作成元オブジェクト.
     * @param <T> DTO型
     * @return 作成したcloneオブジェクト. 引数valueがnullの場合はnull.
     */
    @SuppressWarnings("unchecked")
    protected <T extends DistTemplateBase> List<T> cloneList(List<T> list) {
        List<T> result = null;
        if (null != list) {
            result = new ArrayList<T>();
            for (DistTemplateBase val : list) {
                result.add((T) val.clone());
            }
        }
        return result;
    }

    /**
     * オブジェクトのCloneを作成します.
     * @param value 作成元オブジェクト.
     * @param <T> DTO型
     * @return 作成したcloneオブジェクト. 引数valueがnullの場合はnull.
     */
    @SuppressWarnings("unchecked")
    protected <T extends DistTemplateBase> T cloneObject(T value) {
        T result = null;
        if (null != value) {
            result = (T) value.clone();
        }
        return result;
    }
}
