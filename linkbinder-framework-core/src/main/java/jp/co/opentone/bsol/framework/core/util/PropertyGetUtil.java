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

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.apache.commons.beanutils.NestedNullException;
import org.apache.commons.beanutils.PropertyUtils;

import jp.co.opentone.bsol.framework.core.exception.ApplicationFatalRuntimeException;
import jp.co.opentone.bsol.framework.core.exception.ReflectionRuntimeException;

/**
 * データ取得のユーティリティクラス.
 * @author opentone
 */
public class PropertyGetUtil implements Serializable {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -8496944150493148796L;

    /**
     * 空のインスタンスを生成する.
     * 外部からのインスタンス化禁止.
     */
    private PropertyGetUtil() {
    }

    /**
     * Dtoオブジェクトから値を取り出す. プロパティにDtoオブジェクトがある場合、KEY名を「プロパティ名.子のプロパティ名」とすること.
     * （何階層でも対応可能)
     * @param object
     *            Dtoオブジェクト.
     * @param key
     *            フィールドインスタンス名.
     * @return keyで指定されたフィールドの値.
     * @throws IllegalAccessException プロパティへの不正アクセス
     * @throws InvocationTargetException プロパティ取得メソッド起動失敗
     * @throws NoSuchMethodException プロパティに対応するメソッドが無い
     */
    public static Object getValue(Object object, String key) throws IllegalAccessException,
        InvocationTargetException, NoSuchMethodException {

        Map<?, ?> map = PropertyUtils.describe(object);

        if (key.indexOf(".") > -1) {
            Object bean = map.get(key.substring(0, key.indexOf(".")));

            if (bean != null) {
                return getValue(bean, key.substring(key.indexOf(".") + 1));
            } else {
                return null;
            }
        } else {
            return map.get(key);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T getProperty(Object bean, String name) {
        try {
            return (T) PropertyUtils.getProperty(bean, name);
        } catch (IllegalAccessException e) {
            throw new ReflectionRuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new ReflectionRuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new ReflectionRuntimeException(e);
        }
    }

    public static void setProperty(Object bean, String name, Object value) {
        try {
            PropertyUtils.setProperty(bean, name, value);
        } catch (IllegalAccessException e) {
            throw new ReflectionRuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new ReflectionRuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new ReflectionRuntimeException(e);
        }
    }

    public static Object getNestedProperty(Object data, String name) {
        try {
            if (!name.contains(".")) {
                return PropertyUtils.getProperty(data, name);
            }
            if (name.contains("[")) {
                String[] names = name.split("\\.", 2);
                Object obj = null;
                try {
                    obj = PropertyUtils.getIndexedProperty(data, names[0]);
                } catch (NullPointerException npe) {
                    // アクセスする配列オブジェクトがnullの場合
                    obj = null;
                }
                if (obj == null) {
                    return null;
                }
                return getNestedProperty(obj, names[1]);
            }
            try {
                return PropertyUtils.getNestedProperty(data, name);
            } catch (NestedNullException nne) {
                // ネストしたプロパティの途中がnullの場合はエラーとせず
                // nullを返す
                return null;
            }
        } catch (Exception e) {
            throw new ApplicationFatalRuntimeException("invalid property : " + name, e);
        }
    }
}
