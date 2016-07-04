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

import org.aopalliance.intercept.MethodInvocation;

import jp.co.opentone.bsol.framework.core.SuppressTrace;
import jp.co.opentone.bsol.framework.core.aop.MethodTraceInterceptor;

/**
 * メソッド呼び出しの内容を出力する.
 * <p>
 * @author opentone
 */
public class MethodTracerImpl implements MethodTracer {
    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 3442419319886782980L;

    /*
     * (non-Jsdoc)
     * @see jp.co.opentone.bsol.framework.core.trace.MethodTracer#traceStart(
     *          MethodTraceInterceptor,
     *          org.aopalliance.intercept.MethodInvocation)
     */
    @Override
    public void traceStart(MethodTraceInterceptor interceptor,
            MethodInvocation invocation) {
        MethodInfo m = new MethodInfo(invocation);
        if (isTraceable(m)) {
            MethodTraceFormatter f = new MethodTraceFormatter(
                    interceptor.getCurrentUserId(),
                    interceptor.getIndent(),
                    m.getMethod());

            interceptor.trace(interceptor.getMarker(),
                    f.formatStartMethodString(invocation.getArguments()));
        }
    }

    /*
     * (non-Jsdoc)
     * @see jp.co.opentone.bsol.framework.core.trace.MethodTracer#traceEnd(
     *          MethodTraceInterceptor,
     *          org.aopalliance.intercept.MethodInvocation, java.lang.Object)
     */
    @Override
    public void traceEnd(MethodTraceInterceptor interceptor,
            MethodInvocation invocation, Object ret) {
        traceEnd(interceptor, invocation, ret, "");
    }

    /* (non-Javadoc)
     * @see MethodTracer#traceEnd(
     *          MethodTraceInterceptor,
     *          org.aopalliance.intercept.MethodInvocation, java.lang.Object, java.lang.String)
     */
    @Override
    public void traceEnd(MethodTraceInterceptor interceptor, MethodInvocation invocation,
            Object ret, String additionalInformation) {
        MethodInfo m = new MethodInfo(invocation);
        if (isTraceable(m)) {
            MethodTraceFormatter f = new MethodTraceFormatter(
                    interceptor.getCurrentUserId(),
                    interceptor.getIndent(),
                    m.getMethod());

            interceptor.trace(interceptor.getMarker(),
                    f.formatFinishMethodString(invocation.getArguments(), ret)
                    + additionalInformation);
        }
    }

    /**
     * メソッドがトレース可能な場合はtrueを返す.
     * @param m メソッド情報
     * @return トレース可能な場合はtrue
     */
    public boolean isTraceable(MethodInfo m) {
        //  出力を抑制するアノテーションが付与されていれば出力しない
        SuppressTrace suppression = m.getSuppressTrace();
        if (suppression != null) {
            return false;
        }
        suppression = m.getClassSuppressTrace();
        if (suppression != null) {
            return false;
        }

        if (m.isAccessor()) {
            return false;
        }

        return true;
    }
}
