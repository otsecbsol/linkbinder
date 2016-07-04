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

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInvocation;

/**
 * {@link MethodInvocation}のモッククラス.
 * <p>
 * @author opentone
 */
public class MockMethodInvocation implements MethodInvocation {

    public Method method;
    public Object[] arguments;
    public Object returnValue;
    public Object thisObject;
    public AccessibleObject staticPart;

    public MockMethodInvocation() {
    }

    public MockMethodInvocation(Method method) {
        this.method = method;
    }

    /* (non-Javadoc)
     * @see org.aopalliance.intercept.Invocation#getArguments()
     */
    @Override
    public Object[] getArguments() {
        return null;
    }

    /* (non-Javadoc)
     * @see org.aopalliance.intercept.Joinpoint#proceed()
     */
    @Override
    public Object proceed() throws Throwable {
        return null;
    }

    /* (non-Javadoc)
     * @see org.aopalliance.intercept.Joinpoint#getThis()
     */
    @Override
    public Object getThis() {
        return null;
    }

    /* (non-Javadoc)
     * @see org.aopalliance.intercept.Joinpoint#getStaticPart()
     */
    @Override
    public AccessibleObject getStaticPart() {
        return null;
    }

    /* (non-Javadoc)
     * @see org.aopalliance.intercept.MethodInvocation#getMethod()
     */
    @Override
    public Method getMethod() {
        return method;
    }

}
