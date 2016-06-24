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
package jp.co.opentone.bsol.framework.core.util;

import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import org.junit.Test;

import jp.co.opentone.bsol.framework.core.extension.ibatis.DBValue;

/**
 * {@link CloneUtil}のテストケース.
 * @author opentone
 */
public class CloneUtilTest {

    /**
     * {@link CloneUtil#cloneDate(Date)}の検証.
     */
    @Test
    public void testCloneDate() {
        assertNull(CloneUtil.cloneDate(null));

        Date d = new GregorianCalendar(2009, 0, 2, 10, 20, 30).getTime();
        assertEquals("2009-01-02 10:20:30", formatDate(CloneUtil.cloneDate(d)));
        assertEquals(DBValue.DATE_NULL, CloneUtil.cloneDate(DBValue.DATE_NULL));
    }

    /**
     * {@link CloneUtil#cloneArray(int[])}の検証.
     */
    @Test
    public void testCloneArrayInt() {
        //  nullの配列を渡した場合は空の配列が返される
        assertEquals(0, CloneUtil.cloneArray((int[])null).length);

        int[] i = {1, 2, 3};
        int[] iActual = CloneUtil.cloneArray(i);
        assertEquals(i.length, iActual.length);
        for (int j = 0; j < i.length; j++) {
            assertEquals(i[j], iActual[j]);
        }
    }

    /**
     * {@link CloneUtil#cloneArray(Object[])}の検証.
     */
    @Test
    public void testCloneArrayAny() {
        //  nullの配列を渡した場合は空の配列が返される
        assertEquals(0, CloneUtil.cloneArray(Object.class, (Object[])null).length);
        assertEquals(0, CloneUtil.cloneArray(Object.class, null).length);

        Object[] s =
            {"foo", "bar", "baz"};
        Object[] sActual = CloneUtil.cloneArray(Object.class, s);
        assertEquals(s.length, sActual.length);
        for (int i = 0; i < s.length; i++) {
            assertEquals(s[i], sActual[i]);
        }
    }

    private static String formatDate(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }
}
