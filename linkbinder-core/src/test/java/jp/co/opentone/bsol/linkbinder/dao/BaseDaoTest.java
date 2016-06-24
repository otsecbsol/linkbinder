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
package jp.co.opentone.bsol.linkbinder.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import jp.co.opentone.bsol.framework.core.exception.ReflectionRuntimeException;
import jp.co.opentone.bsol.framework.test.AbstractDaoTestCase;


/**
 * {@link BaseDao}のテストケース.
 * @author opentone
 */
public class BaseDaoTest extends AbstractDaoTestCase {

    private BaseDao dao;
    private String namespace = "test";

//    @Override
    @Before
    public void setUp() {
        dao = new BaseDao(namespace);
    }

    /**
     * {@link BaseDao#getSqlId(String)}のテスト.
     */
    @Test
    public void testGetSqlId() {
        assertEquals("test.findById", dao.getSqlId("findById"));
        assertEquals("test.null", dao.getSqlId(null));
        assertEquals("test.", dao.getSqlId(""));

        dao = new BaseDao(null);
        assertEquals("findById", dao.getSqlId("findById"));
        assertNull(dao.getSqlId(null));
        assertEquals("", dao.getSqlId(""));
    }

    /**
     * {@link BaseDao#getLikeSearchCondition(Object, List<String>)}のテスト.
     */
    @Test
    public void testGetLikeSearchCondition() {
        TestCondition condition = new TestCondition();
        condition.setId(1L);
        condition.setName("N@M%E_");
        condition.setDate(new GregorianCalendar(2009, 5, 1, 10, 20, 30).getTime());

        List<String> fields = new ArrayList<String>();
        fields.add("name");

        dao = new BaseDao(null);
        TestCondition actual = dao.getLikeSearchCondition(condition, fields);

        assertNotSame(condition, actual);
        assertEquals(condition.getId(), actual.getId());
        assertEquals("N@@M@%E@_", actual.getName());
        assertEquals(condition.getDate(), actual.getDate());
        assertEquals(condition.getEscapeChar(), actual.getEscapeChar());
    }

    /**
     * {@link BaseDao#getLikeSearchCondition(Object, List<String>)}のテスト.
     * SetterGetterが正しくない＝NoSuchMethodException.
     */
    @Test
    public void testGetLikeSearchConditionNoSuchMethod_1() {
        dao = new BaseDao(null);
        try {
            dao.getLikeSearchCondition(new NoSuchMethodCondition(), new ArrayList<String>());
        } catch (ReflectionRuntimeException e) {
            assertEquals(NoSuchMethodException.class, e.getCause().getClass());
        }
    }

    /**
     * {@link BaseDao#getLikeSearchCondition(Object, List<String>)}のテスト.
     * 指定Fieldなし＝NoSuchMethodException.
     */
    @Test
    public void testGetLikeSearchConditionNoSuchMethod_2() {
        TestCondition condition = new TestCondition();
        condition.setId(1L);
        condition.setName("N@M%E_");
        condition.setDate(new GregorianCalendar(2009, 5, 1, 10, 20, 30).getTime());

        List<String> fields = new ArrayList<String>();
        fields.add("XXXXXX");

        dao = new BaseDao(null);
        try {
            dao.getLikeSearchCondition(condition, fields);
        } catch (ReflectionRuntimeException e) {
            assertEquals(NoSuchMethodException.class, e.getCause().getClass());
        }
    }

    public static class TestCondition {
        private String escapeChar = "@";
        private Long id;
        private String name;
        private Date date;

        public String getEscapeChar() {
            return escapeChar;
        }
        public void setEscapeChar(String escapeChar) {
            this.escapeChar = escapeChar;
        }
        public Long getId() {
            return id;
        }
        public void setId(Long id) {
            this.id = id;
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public Date getDate() {
            return date;
        }
        public void setDate(Date date) {
            this.date = date;
        }
    }

    public static class NoSuchMethodCondition {
        private String noMethod;

        public String getWrongName() {
            return noMethod;
        }
    }
}
