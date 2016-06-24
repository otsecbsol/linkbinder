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

import org.junit.Before;
import org.junit.Test;

import jp.co.opentone.bsol.linkbinder.util.ValueFormatter;


/**
 * {@link CorresponType}のテストケース.
 * @author opentone
 */
public class CorresponTypeTest {

    /**
     * テスト対象.
     */
    private CorresponType type;

    @Before
    public void setUp() {
        type = new CorresponType();
        type.setId(1L);
        type.setCorresponType("Query");
        type.setName("This is Query");
    }
    /**
     * {@link CorresponType#getLabel()}の検証.
     */
    @Test
    public void testGetLabel() {
        assertEquals(type.getCorresponType() + ValueFormatter.DELIM_CODE + type.getName(),
                    type.getLabel());
    }
}
