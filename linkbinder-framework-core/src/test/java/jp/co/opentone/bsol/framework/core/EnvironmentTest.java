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

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

import jp.co.opentone.bsol.framework.core.config.SystemConfig;
import jp.co.opentone.bsol.framework.core.exception.ApplicationFatalRuntimeException;
import jp.co.opentone.bsol.framework.test.AbstractTestCase;


/**
 * {@link Environment}のテストケース.
 * @author opentone
 */
@ContextConfiguration(locations={"classpath:commonTestContext.xml"})
public class EnvironmentTest extends AbstractTestCase {

    @BeforeClass
    public static void testSetup() {
    }

    @AfterClass
    public static void testTeardown() {
        setUserId(null);
    }

    /**
     * 本番環境の検証.
     */
    @Test
    public void testProduction() {
        setUserId("01234");
        assertEquals(Environment.PRODUCTION, Environment.getEnvironment());
        assertEquals("production", Environment.getEnvironment().toString());
    }

    /**
     * デモ環境の検証.
     */
    @Test
    public void testDemo() {
        setUserId("ZZB01");
        assertEquals(Environment.DEMO, Environment.getEnvironment());
        assertEquals("demo", Environment.getEnvironment().toString());
    }

    /**
     * テスト環境の検証.
     */
    @Test
    public void testTest() {
        setUserId("ZZA01");
        assertEquals(Environment.TEST, Environment.getEnvironment());
        assertEquals("test", Environment.getEnvironment().toString());
    }

    /**
     * 不正なユーザーIDの検証.
     */
    @Test(expected = ApplicationFatalRuntimeException.class)
    public void testInvalidUserId() {
        setUserId(null);
        Environment.getEnvironment();
    }

    private static void setUserId(String userId) {
        ProcessContext c = ProcessContext.getCurrentContext();

        c.setValue(SystemConfig.KEY_USER_ID, userId);
    }
}
