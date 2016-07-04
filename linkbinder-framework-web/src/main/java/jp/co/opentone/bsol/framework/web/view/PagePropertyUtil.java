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
package jp.co.opentone.bsol.framework.web.view;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;

import jp.co.opentone.bsol.framework.core.exception.ReflectionRuntimeException;
import jp.co.opentone.bsol.framework.web.extension.jsf.annotation.Transfer;

/**
 * Pageオブジェクトのプロパティに関するを操作を集めたユーティリティクラス.
 * @author opentone
 */
public class PagePropertyUtil {

    /**
     * 空のインスタンスを生成する.
     * 外部からのインスタンス化禁止.
     */
    private PagePropertyUtil() {
    }

    /**
     * pageオブジェクトの、{@link Transfer}アノテーションが付与されたフィールドの値を収集して返す.
     * <p>
     * 対象のフィールドには適切なアクセサメソッドが定義されていなければならない.
     * </p>
     * @param page
     *            pageオブジェクト
     * @return {@link Transfer}アノーテションが付いたフィールドの値
     */
    public static Map<String, Object> collectTransferValues(Page page) {
        Map<String, Object> values = new HashMap<String, Object>();

        for (Class<?> c = page.getClass(); c != null; c = c.getSuperclass()) {
            for (Field field : c.getDeclaredFields()) {
                if (field.isAnnotationPresent(Transfer.class)) {
                    String name = field.getName();
                    Object value;
                    try {
                        value = PropertyUtils.getProperty(page, name);
                        if (value != null) {
                            values.put(name, value);
                        }
                    } catch (IllegalAccessException e) {
                        throw new ReflectionRuntimeException(e);
                    } catch (InvocationTargetException e) {
                        throw new ReflectionRuntimeException(e);
                    } catch (NoSuchMethodException e) {
                        throw new ReflectionRuntimeException(e);
                    }
                }
            }
        }
        return values;
    }

    /**
     * pageオブジェクトのプロパティに、valuesの値をコピーする.
     * <p>
     * 対象のフィールドには適切なアクセサメソッドが定義されていなければならない.
     * </p>
     * @param page
     *            pageオブジェクト
     * @param values
     *            設定する値
     */
    public static void copyProperties(Page page, Map<String, Object> values) {
        if (values != null && !values.isEmpty()) {
            try {
                PropertyUtils.copyProperties(page, values);
            } catch (IllegalAccessException e) {
                throw new ReflectionRuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new ReflectionRuntimeException(e);
            } catch (NoSuchMethodException e) {
                throw new ReflectionRuntimeException(e);
            }
        }
    }
}
