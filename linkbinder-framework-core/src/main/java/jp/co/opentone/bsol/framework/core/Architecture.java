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

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * アプリケーション各層のJoinPoint定義.
 * @author opentone
 */
@Aspect
public class Architecture {

    /**
     * プレゼンテーション層のためのJoinPoint.
     */
    @Pointcut("within(jp.co.opentone.bsol..view..*)")
    public void inPresentationLayer() {
    }

    /**
     * サービス層のためのJoinPoint.
     */
    @Pointcut("within(jp.co.opentone.bsol..service..*)")
    public void inServiceLayer() {
    }

    /**
     * データアクセス層のためのJoinPoint.
     */
    @Pointcut("within(jp.co.opentone.bsol..dao..*)")
    public void inDataAccessLayer() {
    }

    @Pointcut("execution(* jp.co.opentone.bsol..view..*Page.*(..)) " +
           "|| execution(* jp.co.opentone.bsol..view..*Module.*(..))")
    public void presentation() {
    }

    @Pointcut("execution(* jp.co.opentone.bsol..service..*Service*.*(..))")
    public void service() {
    }

    @Pointcut("execution(* jp.co.opentone.bsol..dao..*Dao*.*(..))")
    public void dataAccess() {
    }
}
