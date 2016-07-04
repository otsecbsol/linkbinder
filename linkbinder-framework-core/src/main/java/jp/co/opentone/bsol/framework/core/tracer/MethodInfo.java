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
package jp.co.opentone.bsol.framework.core.tracer;

import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInvocation;

import jp.co.opentone.bsol.framework.core.SuppressTrace;

/**
 * トレース出力対象のメソッド情報.
 * <p>
 * @author opentone
 */
public class MethodInfo {

    /**
     * 対象のメソッド実行情報.
     */
    private final MethodInvocation invocation;

    public MethodInfo(MethodInvocation invocation) {
        this.invocation = invocation;
    }

    /**
     * メソッドに指定されたアノテーション{@link SuppressTrace}を返す.
     * 指定されていない場合はnullを返す.
     * @return アノテーション
     */
    public SuppressTrace getSuppressTrace() {
        return getMethod().getAnnotation(SuppressTrace.class);
    }

    /**
     * メソッドが定義されたクラスに指定されたアノテーション{@link SuppressTrace}を返す.
     * 指定されていない場合はnullを返す.
     * @return アノテーション
     */
    public SuppressTrace getClassSuppressTrace() {
        return getMethod().getDeclaringClass().getAnnotation(SuppressTrace.class);
    }

    public Method getMethod() {
        return invocation.getMethod();
    }

    /**
     * アクセサメソッドの場合はtrue.
     * @return アクセサメソッドの場合はtrue
     */
    public boolean isAccessor() {
        return isGetter() || isSetter();
    }

    /**
     * Getterの場合はtrue.
     * @return Getterの場合はtrue
     */
    public boolean isGetter() {
        Method m = getMethod();
        String name = m.getName();
        Class<?>[] paramTypes = m.getParameterTypes();
        Class<?> retType = m.getReturnType();

        boolean maybe =
            (paramTypes == null || paramTypes.length == 0)
            && retType != void.class;
        if (!maybe) {
            return false;
        }

        return name.startsWith("get")
        || (name.startsWith("is") && isBooleanType(retType));
    }

    /**
     * Setterの場合はtrue.
     * @return Setterの場合はtrue
     */
    public boolean isSetter() {
        Method m = getMethod();
        String name = m.getName();
        Class<?>[] paramTypes = m.getParameterTypes();
        Class<?> retType = m.getReturnType();

        if (name.startsWith("set")) {
            if (paramTypes != null && paramTypes.length == 1
                    && retType == void.class) {
                return true;
            }
        }
        return false;
    }

    /**
     * typeがBoolean型の場合はtrue.
     * @param type 型
     * @return Boolean型の場合はtrue
     */
    private static boolean isBooleanType(Class<?> type) {
        return type == Boolean.class || type == boolean.class;
    }
}
