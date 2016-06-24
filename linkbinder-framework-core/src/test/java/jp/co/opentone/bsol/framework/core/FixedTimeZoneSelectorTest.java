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

import java.util.TimeZone;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

import jp.co.opentone.bsol.framework.core.config.SystemConfig;
import jp.co.opentone.bsol.framework.test.AbstractTestCase;
import mockit.Mock;
import mockit.MockUp;


/**
 * {@link FixedTimeZoneSelector}のテストケース.
 * @author opentone
 */
@ContextConfiguration(locations={"classpath:commonTestContext.xml"})
public class FixedTimeZoneSelectorTest extends AbstractTestCase {

    /**
     * テスト対象.
     */
    private FixedTimeZoneSelector selector;

    @BeforeClass
    public static void classSetUp() {
        new MockSystemConfig();
        MockSystemConfig.VALUE = "Asia/Tokyo";
    }

    @AfterClass
    public static void classTearDown() {
        new MockSystemConfig().tearDown();
    }

    @Before
    public void setUp() {
        selector = new FixedTimeZoneSelector();
    }

    /**
     * {@link FixedTimeZoneSelector#getTimeZone()}のテストケース.
     * {@link SystemConfig}から取得できるTimeZoneが返されるか検証する.
     */
    @Test
    public void testGetTimeZone() {
        assertEquals(TimeZone.getTimeZone("Asia/Tokyo"), selector.getTimeZone());
    }

    public static class MockSystemConfig extends MockUp<SystemConfig> {
        public static String VALUE;
        @Mock
        public String getValue(String key) {
            return VALUE;
        }
    }
}
