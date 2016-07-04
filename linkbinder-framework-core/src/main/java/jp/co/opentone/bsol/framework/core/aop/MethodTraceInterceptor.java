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
package jp.co.opentone.bsol.framework.core.aop;

import javax.annotation.Resource;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import jp.co.opentone.bsol.framework.core.auth.UserHolder;
import jp.co.opentone.bsol.framework.core.tracer.MethodTracer;

/**
 * メソッド呼出時のトレースログを出力するInterceptor.
 * @author opentone
 */
public class MethodTraceInterceptor implements MethodInterceptor, ApplicationContextAware {

    /** logger. */
    private final Logger log = LoggerFactory.getLogger(MethodTraceInterceptor.class);

    /** メソッドの開始時間を格納するコンテナ. */
    private static ThreadLocal<Long> startedContainer = new ThreadLocal<Long>();

    /** メソッド呼び出しに合わせ出力するログをインデントするための情報を保持する. */
    private static ThreadLocal<Integer> level = new ThreadLocal<Integer>();

    /**
     * SpringのApplicationContext.
     * @see ApplicationContextAware
     */
    private ApplicationContext applicationContext;

    /**
     * メソッド呼出ログを出力するオブジェクト.
     */
    @Resource
    private MethodTracer tracer;

    /**
     * ログのMarker.
     * @see Marker
     */
    private Marker marker;

    /* (non-Javadoc)
     * @see org.aopalliance.intercept
     * .MethodInterceptor#invoke(org.aopalliance.intercept.MethodInvocation)
     */
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        traceStart(invocation);
        Object ret = invocation.proceed();
        traceEnd(invocation, ret);

        return ret;
    }

    private void indent() {
        Integer i = level.get();
        if (i == null) {
            i = 0;
        } else {
            i += 1;
        }
        level.set(i);
    }
    private void unindent() {
        Integer i = level.get();
        if (i == null) {
            i = 0;
        } else {
            i -= 1;
        }
        level.set(i);
    }

    /**
     * メソッド開始ログを出力する.
     * @param invocation メソッド情報
     */
    protected void traceStart(MethodInvocation invocation) {
        indent();
        if (log.isTraceEnabled()) {
            tracer.traceStart(this, invocation);
        }
        startedContainer.set(System.currentTimeMillis());
    }

    /**
     * メソッド終了ログを出力する.
     * @param invocation メソッド情報
     * @param ret メソッドからの戻り値
     */
    protected void traceEnd(MethodInvocation invocation, Object ret) {
        Long started = startedContainer.get();
        Long elapsed = 0L;
        if (started != null) {
            elapsed = System.currentTimeMillis() - started;
        }

        if (log.isTraceEnabled()) {
            tracer.traceEnd(this, invocation, ret, String.format(" elapsed: %d ms", elapsed));
        }
        unindent();
    }

    /**
     * トレースログを出力する.
     * @param m SLF4JのMarker
     * @param message メッセージ
     * @see Marker
     */
    public void trace(Marker m, String message) {
        if (log.isTraceEnabled()) {
            log.trace(m, message);
        }
    }

    public String getIndent() {
        StringBuilder result = new StringBuilder();
        Integer indent = level.get();
        if (indent == null) {
            indent = 0;
        }
        for (int i = 0; i < indent; i++) {
            result.append(' ');
        }
        return result.toString();
    }

    /**
     * 現在の実行コンテキストのユーザーIDを返す.
     * @return ユーザーID. 実行コンテキストにユーザーが設定されていない場合はnull
     */
    public String getCurrentUserId() {
        String result = null;
        //  このメソッドが呼び出されたタイミングで有効なユーザー情報を取得
        UserHolder u = applicationContext.getBean(UserHolder.class);
        if (u != null) {
            result = u.getUserId();
        }
        return result;
    }

    /* (non-Javadoc)
     * @see org.springframework.context.ApplicationContextAware#setApplicationContext(
     *      org.springframework.context.ApplicationContext)
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext)
                    throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * markerを設定する.
     * @param marker SLF4JのMarker
     */
    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    /**
     * markerを返す.
     * @return SLF4JのMarker
     */
    public Marker getMarker() {
        return this.marker;
    }
}
