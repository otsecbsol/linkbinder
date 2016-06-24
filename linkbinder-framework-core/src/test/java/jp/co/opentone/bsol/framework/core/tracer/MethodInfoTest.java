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

import static org.junit.Assert.*;

import org.junit.Test;

import jp.co.opentone.bsol.framework.core.SuppressTrace;


/**
 * {@link MethodInfo}のテストケース.
 * <p>
 * @author opentone
 */
public class MethodInfoTest {

    @Test
    public void testGetSuppressTrace() throws Exception {
        MockMethodInvocation invocation =
            new MockMethodInvocation(this.getClass().getMethod("methodWithSuppressTrace"));
        MethodInfo info = new MethodInfo(invocation);

        assertEquals("xxx", info.getSuppressTrace().alternative());
    }

    @Test
    public void testGetSuppressTraceReturnNull() throws Exception {
        MockMethodInvocation invocation =
            new MockMethodInvocation(this.getClass().getMethod("testGetSuppressTraceReturnNull"));
        MethodInfo info = new MethodInfo(invocation);

        assertNull(info.getSuppressTrace());
    }

    @Test
    public void testGetClassSuppressTrace() throws Exception {
        MockMethodInvocation invocation =
            new MockMethodInvocation(ClassWithSuppressTrace.class.getMethod("foo"));
        MethodInfo info = new MethodInfo(invocation);

        assertEquals("yyy", info.getClassSuppressTrace().alternative());
    }


    @Test
    public void testGetClassSuppressTraceReturnNull() throws Exception {
        MockMethodInvocation invocation =
            new MockMethodInvocation(this.getClass().getMethod("testGetClassSuppressTraceReturnNull"));
        MethodInfo info = new MethodInfo(invocation);

        assertNull(info.getClassSuppressTrace());
    }

    @Test
    public void testIsAccessorSetter() throws Exception {
        MockMethodInvocation invocation =
            new MockMethodInvocation(this.getClass().getMethod("setFoo", String.class));
        MethodInfo info = new MethodInfo(invocation);

        assertTrue(info.isAccessor());
    }


    @Test
    public void testIsAccessorGetter() throws Exception {
        MockMethodInvocation invocation =
            new MockMethodInvocation(this.getClass().getMethod("getFoo"));
        MethodInfo info = new MethodInfo(invocation);

        assertTrue(info.isAccessor());
    }

    @Test
    public void testIsAccessorBooleanSetter() throws Exception {
        MockMethodInvocation invocation =
            new MockMethodInvocation(this.getClass().getMethod("setHoge", boolean.class));
        MethodInfo info = new MethodInfo(invocation);

        assertTrue(info.isAccessor());
    }


    @Test
    public void testIsAccessorBooleanGetter() throws Exception {
        MockMethodInvocation invocation =
            new MockMethodInvocation(this.getClass().getMethod("isHoge"));
        MethodInfo info = new MethodInfo(invocation);

        assertTrue(info.isAccessor());
    }

    @Test
    public void testIsAccessorNotSetter() throws Exception {
        MockMethodInvocation invocation =
            new MockMethodInvocation(this.getClass().getMethod("Setbar", String.class));
        MethodInfo info = new MethodInfo(invocation);

        assertFalse(info.isAccessor());
    }

    @Test
    public void testIsAccessorNotGetter() throws Exception {
        MockMethodInvocation invocation =
            new MockMethodInvocation(this.getClass().getMethod("Getbar"));
        MethodInfo info = new MethodInfo(invocation);

        assertFalse(info.isAccessor());
    }

    @SuppressTrace(alternative="xxx")
    public void methodWithSuppressTrace() {
    }

    @SuppressTrace(alternative="yyy")
    public static class ClassWithSuppressTrace {
        public void foo() {}
    }

    /* accessors */
    public void setFoo(String foo) {}
    public String getFoo() { return null; }
    public void setHoge(boolean hoge) {}
    public boolean isHoge() { return false; }

    /* not accessors */
    public void Setbar(String bar) {}
    public String Getbar() { return null; }
}
