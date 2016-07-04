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

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

import jp.co.opentone.bsol.framework.test.AbstractTestCase;


/**
 * {@link ProcessContext}のテストケース.
 * @author opentone
 */
@ContextConfiguration(locations={"classpath:commonTestContext.xml"})
public class ProcessContextTest extends AbstractTestCase {

    @Before
    public void setUp() {
//        ProcessContext.getCurrentContext().clearMessages();
    }

    @Test
    public void testGetCurrentContext() throws Exception {
        ProcessContext c = ProcessContext.getCurrentContext();

        assertNotNull(c);
        assertEquals(c, ProcessContext.getCurrentContext());

        //  別スレッドで取得すると異なるインスタンスが返される
        final ProcessContext[] another = new ProcessContext[1];
        Thread t = new Thread() {
            @Override
            public void run() {
                another[0] = ProcessContext.getCurrentContext();
            }
        };
        t.start();
        t.join();

        assertNotNull(another[0]);
        assertFalse(c == another[0]);
    }

    @Test
    public void testGetSetValue() {
        ProcessContext c = ProcessContext.getCurrentContext();
        c.setValue("foo", "FOO");

        assertEquals("FOO", c.getValue("foo"));
    }

//    @Test
//    public void testAddMessage() {
//        ProcessContext c = ProcessContext.getCurrentContext();
//
//        c.addMessage("I902", "test");
//
//        int count = 0;
//        for (Message m : c.messages()) {
//            assertEquals("I902", m.getMessageCode());
//            assertEquals("test information message. test", m.getMessage());
//            count++;
//        }
//        assertEquals(1, count);
//
//        c.clearMessages();
//        count = 0;
//        for (Message m : c.messages()) {
//            count++;
//        }
//        assertEquals(0, count);
//    }
}
