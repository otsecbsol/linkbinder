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

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

/**
 * AbstractDtoのテスト.
 * @author opentone
 */
public class AbstractDtoTest {

    /**
     * toString()のテスト.
     * <ol>
     * <li>privateフィールドの名前、値が戻り値に含まれていることを確認.</li>
     * <li>staticフィールドが戻り値に含まれていないことを確認.</li>
     * <li>親クラスのフィールドが戻り値に含まれていることを確認.</li>
     * </ol>
     */
    @Test
    public void testToString() {

        MockDto dto = new MockDto();
        dto.initValue();

        String actual = dto.toString();
        System.out.println(actual);
        assertTrue(actual.contains("foo"));
        assertTrue(actual.contains("FOO"));
        assertTrue(actual.contains("bar"));
        assertTrue(actual.contains("null"));
        assertTrue(actual.contains("intValue"));
        assertTrue(actual.contains("100"));
        assertTrue(actual.contains("doubleValue"));
        assertTrue(actual.contains("1.234"));
        assertTrue(actual.contains("list"));
        assertTrue(actual.contains("value1"));
        assertTrue(actual.contains("value2"));
        assertTrue(actual.contains("parentField"));
        assertTrue(actual.contains("PARENT"));

        assertFalse(actual.contains("STATIC_FIELD"));
    }

    /**
     * ParentDto.
     */
    static class ParentDto extends AbstractDto {
        /**
         * SerialVersionUID.
         */
        private static final long serialVersionUID = 6937004843343190612L;

        /**
         * parentField.
         */
        @SuppressWarnings("unused")
        private String parentField = "PARENT";
    }

    /**
     * MockDto.
     */
    static class MockDto extends ParentDto {
        /**
         * SerialVersionUID.
         */
        private static final long serialVersionUID = -2884638683890178910L;

        /**
         * STATIC_FIELD.
         */
        public static int STATIC_FIELD = 2;

        /**
         * foo.
         */
        @SuppressWarnings("unused")
        private String foo;

        /**
         * bar.
         */
        @SuppressWarnings("unused")
        private String bar;

        /**
         * intValue.
         */
        @SuppressWarnings("unused")
        private int intValue;

        /**
         * doubleValue.
         */
        @SuppressWarnings("unused")
        private double doubleValue;

        /**
         * list.
         */
        private List<String> list;

        public void initValue() {
            foo = "FOO";
            bar = null;
            intValue = 100;
            doubleValue = 1.234;
            list = new ArrayList<String>();
            list.add("value1");
            list.add("value2");
        }
    }
}
