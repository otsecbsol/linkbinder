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

import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

import jp.co.opentone.bsol.framework.test.AbstractTestCase;


/**
 * {@link TimeZoneSelector}のテストケース.
 * @author opentone
 */
@ContextConfiguration(locations={"classpath:commonTestContext.xml"})
public class TimeZoneSelectorTest extends AbstractTestCase {

    @Test
    public void setGetSelector() {

        TimeZoneSelector selector = TimeZoneSelector.getSelector();

        assertNotNull(selector);
        //  1次リリースではFixedTimeZoneSelector固定
        assertTrue(selector instanceof FixedTimeZoneSelector);
    }
}
