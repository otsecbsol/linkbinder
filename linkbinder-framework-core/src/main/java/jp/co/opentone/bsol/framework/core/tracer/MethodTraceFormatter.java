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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import jp.co.opentone.bsol.framework.core.SuppressTrace;
import jp.co.opentone.bsol.framework.core.util.ArgumentValidator;
import jp.co.opentone.bsol.framework.core.util.CloneUtil;

/**
 * メソッド呼び出しトレース用文字列を生成する.
 * <p>
 * @author opentone
 */
public class MethodTraceFormatter {
    /**
     * 出力フォーマット：開始.
     */
    public static final String FORMAT_START = "start: %5s %s%s";
    /**
     * 出力フォーマット：終了.
     */
    public static final String FORMAT_END = "end  : %5s %s%s%s";
    /**
     * ユーザーIDが空のときに出力する文字列.
     */
    public static final String EMPTY_USER_ID = "-";

    /**
     * 実行ユーザーID.
     */
    private final String userId;
    /**
     * インデントする文字.
     */
    private final String indent;
    /**
     * 対象メソッド.
     */
    private final Method method;

    /**
     * メソッドが定義されたクラス名.
     */
    private String className;
    /**
     * メソッド名.
     */
    private String methodName;
    /**
     * メソッド引数の型.
     */
    private Class<?>[] parameterTypes;
    /**
     * メソッドの戻り値の型.
     */
    private Class<?> returnType;

    public MethodTraceFormatter(String userId, String indent, Method method) {
        ArgumentValidator.validateNotNull(method);

        this.userId = userId != null ? userId : EMPTY_USER_ID;
        this.indent = indent;
        this.method = method;
    }

    /**
     * メソッドの処理開始を表す文字列を返す.
     * @param arguments メソッド引数
     * @return トレース開始を表す文字列
     */
    public String formatStartMethodString(Object[] arguments) {
        return String.format(FORMAT_START, userId, indent, getSignature(arguments));
    }

    /**
     * メソッドの処理終了を表す文字列を返す.
     * @param arguments メソッド引数
     * @param returnValue メソッドからの戻り値
     * @return トレース開始を表す文字列
     */
    public String formatFinishMethodString(Object[] arguments, Object returnValue) {
        return String.format(FORMAT_END,
                userId,
                indent,
                getSignature(arguments),
                getReturnValueAsString(returnValue));
    }

    /**
     * メソッドシグネチャを表す文字列を返す.
     * className.methodName(arguments...)の形式.
     * @param arguments 引数の値
     * @return メソッドシグネチャ
     */
    public String getSignature(Object[] arguments) {
        String strArgs = parseArguments(method.getParameterAnnotations(), arguments);
        return String.format("%s.%s(%s)", getClassName(), getMethodName(), strArgs);
    }

    /**
     * 引数の値を解析して出力用に整形して返す.
     * @param paramAnnotations 引数に指定されたアノテーションの配列
     * @param arguments 引数の値
     * @return 解析結果
     */
    public String parseArguments(Annotation[][] paramAnnotations, Object[] arguments) {
        String[] result = new String[arguments.length];
        for (int i = 0; i < arguments.length; i++) {
            Object val = formatValue(arguments[i]);
            SuppressTrace st = findSuppressTrace(paramAnnotations[i]);
            if (st != null) {
                val = StringUtils.isNotEmpty(st.alternative()) ? st.alternative() : "-";
            }
            result[i] = val == null ? "null" : val.toString();
        }

        return StringUtils.join(result, ", ");
    }

    /**
     * 値を出力用に整形して返す.
     * @param value 値
     * @return 整形結果
     */
    private String formatValue(Object value) {
        if (value instanceof List<?>) {
            return String.format("List#size=%d", ((List<?>) value).size());
        } else {
            return value == null ? "null" : value.toString();
        }
    }

    /**
     * アノテーションの配列から最初に見つかった{@link SuppressTrace}を返す.
     * @param annotations アノテーションの配列
     * @return 最初に見つかった{@link SuppressTrace}. 見つからない場合はnull
     */
    private SuppressTrace findSuppressTrace(Annotation[] annotations) {
        for (Annotation a : annotations) {
            if (a instanceof SuppressTrace) {
                return (SuppressTrace) a;
            }
        }
        return null;
    }

    /**
     * 対象メソッドが定義されたクラス名を返す.
     * <p>
     * パッケージ名は含まない.
     * </p>
     * @return クラス名
     */
    public String getClassName() {
        if (className == null) {
            className = method.getDeclaringClass().getSimpleName();
        }
        return className;
    }

    /**
     * 対象メソッドの名前を返す.
     * @return メソッド名
     */
    public String getMethodName() {
        if (methodName == null) {
            methodName = method.getName();
        }
        return methodName;
    }

    /**
     * 対象メソッドの引数の型を返す.
     * @return 引数の型
     */
    public Class<?>[] getParameterTypes() {
        if (parameterTypes == null) {
            parameterTypes = method.getParameterTypes();
        }
        return CloneUtil.cloneArray(Class.class, parameterTypes);
    }

    /**
     * 対象メソッドの戻り値の型を返す.
     * @return 戻り値の型
     */
    public Class<?> getReturnType() {
        if (returnType == null) {
            returnType = method.getReturnType();
        }
        return returnType;
    }

    /**
     * メソッドの戻り値を出力用の文字列に変換して返す.
     * @param returnValue 戻り値
     * @return 変換結果
     */
    public String getReturnValueAsString(Object returnValue) {
        if (getReturnType() == void.class) {
            return "";
        }
        return String.format(" returning: %s", formatValue(returnValue));
    }
}
