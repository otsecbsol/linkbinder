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

import java.io.Serializable;

import org.aopalliance.intercept.MethodInvocation;

import jp.co.opentone.bsol.framework.core.aop.MethodTraceInterceptor;

/**
 * メソッド呼出の内容を出力する.
 * <p>
 * @author opentone
 */
public interface MethodTracer extends Serializable {

    /**
     * メソッド開始ログを出力する.
     * @param interceptor メソッド呼び出しをトレースするInterceptor
     * @param invocation メソッド情報
     */
    void traceStart(MethodTraceInterceptor interceptor, MethodInvocation invocation);

    /**
     * メソッド終了ログを出力する.
     * @param interceptor メソッド呼び出しをトレースするInterceptor
     * @param invocation メソッド情報
     * @param ret メソッドの戻り値
     */
    void traceEnd(MethodTraceInterceptor interceptor, MethodInvocation invocation, Object ret);

    /**
     * メソッド終了ログを出力する.
     * @param interceptor メソッド呼び出しをトレースするInterceptor
     * @param invocation メソッド情報
     * @param ret メソッドの戻り値
     * @param additionalInformation 追加出力情報
     */
    void traceEnd(MethodTraceInterceptor interceptor, MethodInvocation invocation, Object ret,
            String additionalInformation);
}
