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

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import jp.co.opentone.bsol.framework.core.exception.MethodInvocationRuntimeException;
import jp.co.opentone.bsol.framework.core.exception.ReflectionRuntimeException;

/**
 * メソッドを操作するユーティリティ.
 * @author opentone
 */
public class MethodUtil {

    /**
     * 空のインスタンスを生成する.
     * 外部からのインスタンス化禁止.
     */
    private MethodUtil() {
    }

    /**
     * clazzで定義されたメソッドのうち、annotationType型のアノテーションが 指定された全てのメソッドを返す.
     * @param clazz
     *            対象のクラス
     * @param annotationType
     *            アノテーション
     * @return annotationTypeが指定されている全てのメソッド
     */
    public static Method[] getMethodsWithAnnotation(Class<?> clazz,
                                                    Class<? extends Annotation> annotationType) {
        List<Method> result = new ArrayList<Method>();
        for (Class<?> c = clazz; c != null; c = c.getSuperclass()) {
            for (Method method : c.getMethods()) {
                if (method.getAnnotation(annotationType) != null && !contains(result, method)) {
                    // if (method.getAnnotation(annotationType) != null &&
                    // !result.contains(method)) {
                    result.add(method);
                }
            }
        }
        return result.toArray(new Method[0]);
    }

    private static boolean contains(List<Method> methods, Method candidate) {
        for (Method m : methods) {
            if (m.getName().equals(candidate.getName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * メソッドを起動する.
     * @param <T>
     *            戻り値の型
     * @param obj
     *            起動対象オブジェクト
     * @param method
     *            起動メソッド
     * @param args
     *            メソッド引数
     * @return メソッドの戻り値
     * @throws MethodInvocationRuntimeException
     *             メソッドの起動に失敗
     */
    @SuppressWarnings("unchecked")
    public static <T> T invoke(Object obj, Method method, Object... args)
        throws MethodInvocationRuntimeException {
        try {
            return (T) method.invoke(obj, args);
        } catch (InvocationTargetException e) {
            throw new MethodInvocationRuntimeException(
                    ((InvocationTargetException) e).getTargetException());
        } catch (Exception e) {
            throw new MethodInvocationRuntimeException(e);
        }
    }

    /**
     * 引数の無いメソッドを起動する.
     * @param <T>
     *            戻り値の型
     * @param obj
     *            起動対象オブジェクト
     * @param methodName
     *            起動メソッド
     * @return メソッドの戻り値
     */
    @SuppressWarnings("unchecked")
    public static <T> T invoke(Object obj, String methodName) {
        try {
            return (T) getMethod(obj.getClass(), methodName).invoke(obj, (Object[]) null);
        } catch (ReflectionRuntimeException e) {
            throw new MethodInvocationRuntimeException(e.getCause());
        } catch (InvocationTargetException e) {
            throw new MethodInvocationRuntimeException(
                    ((InvocationTargetException) e).getTargetException());
        } catch (Exception e) {
            throw new MethodInvocationRuntimeException(e);
        }
    }

    /**
     * メソッドを起動する.
     * @param <T>
     *            戻り値の型
     * @param obj
     *            起動対象オブジェクト
     * @param methodName
     *            起動メソッド
     * @param paramType
     *            引数の型
     * @param args
     *            メソッド引数
     * @return メソッドの戻り値
     * @throws MethodInvocationRuntimeException
     *             メソッドの起動に失敗
     */
    @SuppressWarnings("unchecked")
    public static <T> T invoke(Object obj, String methodName, Class<?>[] paramType, Object[] args)
        throws MethodInvocationRuntimeException {
        try {
            return (T) getMethod(obj.getClass(), methodName, paramType).invoke(obj, args);
        } catch (ReflectionRuntimeException e) {
            throw new MethodInvocationRuntimeException(e.getCause());
        } catch (Exception e) {
            throw new MethodInvocationRuntimeException(e);
        }
    }

    /**
     * 指定された名前、引数を持つメソッドを返す.
     * @param clazz
     *            対象のメソッドが定義されたクラス
     * @param methodName
     *            メソッド
     * @param paramType
     *            引数の型
     * @return メソッド
     * @throws ReflectionRuntimeException
     *             メソッドが見つからない
     */
    public static Method getMethod(Class<?> clazz, String methodName, Class<?>... paramType)
        throws ReflectionRuntimeException {
        try {
            return clazz.getDeclaredMethod(methodName, paramType);
        } catch (SecurityException e) {
            throw new ReflectionRuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new ReflectionRuntimeException(e);
        }
    }
}
