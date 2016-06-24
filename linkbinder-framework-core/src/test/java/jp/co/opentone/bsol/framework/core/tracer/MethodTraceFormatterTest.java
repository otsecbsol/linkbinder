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




/**
 * {@link MethodTraceFormatter}のテストケース.
 * <p>
 * @author opentone
 */
public class MethodTraceFormatterTest {

    private String userId = "12345";

    @Test
    public void testFormatStartMethodString() throws Exception {
        MethodTraceFormatter f =
            new MethodTraceFormatter(userId, " ", this.getClass().getMethod("search", Long.class));

        String result = f.formatStartMethodString(new Object[] {Long.valueOf("1")});
        assertStartTraceLog(result, userId, "search(1)");
    }

    @Test
    public void testFormatStartMethodStringWithoutArguments() throws Exception {
        MethodTraceFormatter f =
            new MethodTraceFormatter(userId, " ", this.getClass().getMethod("increment"));

        String result = f.formatStartMethodString(new Object[] {});
        assertStartTraceLog(result, userId, "increment()");
    }

    @Test
    public void testFormatFinishedMethodStringWithoutReturnValue() throws Exception {
        MethodTraceFormatter f =
            new MethodTraceFormatter(userId, " ", this.getClass().getMethod("update"));

        String result = f.formatFinishMethodString(new Object[]{}, null);
        assertFinishedTraceLog(result, userId, "update()");
    }

    @Test
    public void testFormatFinishedMethodStringWithReturnValue() throws Exception {
        MethodTraceFormatter f =
            new MethodTraceFormatter(userId, " ", this.getClass().getMethod("increment"));

        String result = f.formatFinishMethodString(new Object[]{}, 1L);
        assertFinishedTraceLog(result, userId, "increment() returning: 1");
    }


    public static void assertStartTraceLog(String log, String userId, String method) {
        System.out.println(log);

        String expected = String.format("start: %s  %s.%s",
                        userId,
                        MethodTraceFormatterTest.class.getSimpleName(),
                        method);

        assertEquals(expected, log);
    }

    public static void assertFinishedTraceLog(String log, String userId, String method) {
        System.out.println(log);

        String expected = String.format("end  : %s  %s.%s",
                        userId,
                        MethodTraceFormatterTest.class.getSimpleName(),
                        method);

        assertEquals(expected, log);
    }

    public String search(Long id) {
        return id.toString();
    }

    public Long increment() {
        return 1L;
    }

    public void update() {
    }
}
