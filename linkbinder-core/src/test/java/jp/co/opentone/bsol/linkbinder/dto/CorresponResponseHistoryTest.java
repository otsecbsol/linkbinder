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
package jp.co.opentone.bsol.linkbinder.dto;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import jp.co.opentone.bsol.framework.core.config.SystemConfig;
import mockit.Mock;
import mockit.MockUp;


/**
 * {@link CorresponResponseHistory}のテストケース.
 * @author opentone
 */
public class CorresponResponseHistoryTest {

    private CorresponResponseHistory h;
    private Correspon c;

    @BeforeClass
    public static void testSetup() {
        new MockSystemConfig();
    }

    @AfterClass
    public static void testTeardown() {
        new MockSystemConfig().tearDown();;
    }

    @Before
    public void setUp() {
        c = new Correspon();
        c.setSubject("This is test.これはテストです。");

        h = new CorresponResponseHistory();
        h.setCorrespon(c);
    }

    /**
     * 件名が指定バイト数で切り捨てられることを検証する.
     */
    @Test
    public void testGetSubject() {
        c.setSubject("This is test. これはテストです。this is test.");

        MockSystemConfig.VALUE = "1";
        assertEquals("T...", h.getSubject());

        MockSystemConfig.VALUE = "5";
        assertEquals("This ...", h.getSubject());

        MockSystemConfig.VALUE = "15";
        assertEquals("This is test. ...", h.getSubject());

        MockSystemConfig.VALUE = "17";
        assertEquals("This is test. こ...", h.getSubject());

        MockSystemConfig.VALUE = "18";
        assertEquals("This is test. これ...", h.getSubject());

        MockSystemConfig.VALUE = "44";
        assertEquals("This is test. これはテストです。this is test...", h.getSubject());

        MockSystemConfig.VALUE = "45";
        assertEquals("This is test. これはテストです。this is test.", h.getSubject());

        MockSystemConfig.VALUE = "46";
        assertEquals("This is test. これはテストです。this is test.", h.getSubject());

        MockSystemConfig.VALUE = "100";
        assertEquals("This is test. これはテストです。this is test.", h.getSubject());
    }

    public static class MockSystemConfig extends MockUp<SystemConfig> {
        public static String VALUE;
        @Mock
        public static String getValue(String key) {
            return VALUE;
        }
    }
}
