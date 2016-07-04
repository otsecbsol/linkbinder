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
package jp.co.opentone.bsol.framework.core;

import java.lang.reflect.Method;

import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.Order;

import jp.co.opentone.bsol.framework.core.auth.UserHolder;
import jp.co.opentone.bsol.framework.core.log.Markers;

/**
 * 各層のpublicメソッド呼び出しのトレースログを出力する.
 * @author opentone
 */
@Aspect
@Order(value = 2)
public class MethodTraceAdvice implements ApplicationContextAware {

    /**
     * logger.
     */
    private static final Logger log = LoggerFactory.getLogger(MethodTraceAdvice.class);

    /** メソッドの開始時間を格納するコンテナ. */
    private static ThreadLocal<Long> startedContainer = new ThreadLocal<Long>();

    /**
     * Spring frameworkのApplicationContext.
     */
    private ApplicationContext applicationContext;

    /**
     * 空のインスタンスを生成する.
     */
    public MethodTraceAdvice() {
    }

    /**
     * プレゼンテーション層のpublicメソッド呼び出し時のログを出力する.
     * @param jp
     *            JoinPoint
     * @return メソッドの戻り値
     * @throws Throwable
     *             メソッドで発生した例外
     */
    @Around("jp.co.opentone.bsol.framework.core.Architecture.presentation()")
    public Object tracePresentation(ProceedingJoinPoint jp) throws Throwable {
        return proceed(Markers.PRESENTATION, jp);
    }

    /**
     * サービス層のpublicメソッド呼び出し時のログを出力する.
     * @param jp
     *            JoinPoint
     * @return メソッドの戻り値
     * @throws Throwable
     *             メソッドで発生した例外
     */
    @Around("jp.co.opentone.bsol.framework.core.Architecture.service()")
    public Object traceService(ProceedingJoinPoint jp) throws Throwable {
        return proceed(Markers.SERVICE, jp);
    }

    /**
     * データアクセス層のpublicメソッド呼び出し時のログを出力する.
     * @param jp
     *            JoinPoint
     * @return メソッドの戻り値
     * @throws Throwable
     *             メソッドで発生した例外
     */
    @Around("jp.co.opentone.bsol.framework.core.Architecture.dataAccess()")
    public Object traceDataAccess(ProceedingJoinPoint jp) throws Throwable {
        return proceed(Markers.DATA_ACCESS, jp);
    }

    private Object proceed(Marker marker, ProceedingJoinPoint jp) throws Throwable {
        MethodTrace m = new MethodTrace(getUser(), getMethod(jp));
        boolean traceable = isTraceable(m);

        if (traceable) {
            start(marker, m, jp);
        }
        Object ret = jp.proceed();

        if (traceable) {
            finish(marker, m, ret, jp);
        }
        return ret;
    }

    private boolean isTraceable(MethodTrace m) {
        if (!log.isTraceEnabled()) {
            return false;
        }

        return m.isTraceable();
    }

    private void start(Marker marker, MethodTrace m, ProceedingJoinPoint jp) {
        String message = m.formatStartMethodString(getArguments(jp));
        log.trace(marker, message);

        startedContainer.set(System.currentTimeMillis());
    }

    private void finish(Marker marker, MethodTrace m, Object returnValue, ProceedingJoinPoint jp) {
        Long started = startedContainer.get();
        Long elapsed = 0L;
        if (started != null) {
            elapsed = System.currentTimeMillis() - started;
        }

        String message = m.formatFinishMethodString(getArguments(jp), returnValue);
        log.trace(marker, message + String.format(" elapsed: %d ms", elapsed));
    }

    private Method getMethod(ProceedingJoinPoint jp) {
        MethodSignature s = (MethodSignature) jp.getSignature();
        return s.getMethod();
    }

    private Object[] getArguments(ProceedingJoinPoint jp) {
        return jp.getArgs();
    }

    public String getUser() {
        UserHolder login =
            (UserHolder) applicationContext.getBean("currentUser");
//        UserHolder login =
//            (UserHolder) ProcessContext.getCurrentContext().getValue(SystemConfig.KEY_USER);
        if (login != null && StringUtils.isNotEmpty(login.getUserId())) {
            return login.getUserId();
        }
        return "-";
    }

    /* (non-Javadoc)
     * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
     */
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
