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
package jp.co.opentone.bsol.framework.core.extension.ibatis;

import static org.junit.Assert.*;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Calendar;
import java.util.Map;

import org.junit.Test;

import com.ibatis.sqlmap.client.extensions.ParameterSetter;
import com.ibatis.sqlmap.client.extensions.ResultGetter;

public class EnumValueHandlerTest {

    private EnumValueHandler handler = new FooValueHandler();

    @Test
    public void testValueOf() throws Exception {
        String s = "test";
        assertEquals(s, handler.valueOf(s));
    }

    @Test
    public void testGetResult() throws Exception {
        MockResultGetter getter = new MockResultGetter(Foo.HOGE.getValue());
        assertEquals(Foo.HOGE, handler.getResult(getter));

        getter = new MockResultGetter(null);
        assertNull(handler.getResult(getter));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetResultException() throws Exception {
        MockResultGetter getter = new MockResultGetter(100);
        handler.getResult(getter);
        fail("例外が発生していない");
    }

    @Test
    public void testSetParameter() throws Exception {
        MockParameterSetter setter = new MockParameterSetter(Foo.FUGA.getValue());
        handler.setParameter(setter, Foo.FUGA);
        handler.setParameter(setter, null);
    }

    class FooValueHandler extends EnumValueHandler {
        @Override
        public EnumValue[] getValues() {
            return Foo.values();
        }
    }

    enum Foo implements EnumValue {
        HOGE {
            @Override
            public Integer getValue() {
                return 1;
            }
        },
        FUGA {
            @Override
            public Integer getValue() {
                return 2;
            }
        },
        PIYO {
            @Override
            public Integer getValue() {
                return 3;
            }
        };
        @Override
        public Integer getValue() {
            return 0;
        }
    }

    class MockResultGetter implements ResultGetter {

        Integer value;

        public MockResultGetter(Integer value) {
            this.value = value;
        }

        @Override
        public Array getArray() throws SQLException {
            return null;
        }

        @Override
        public BigDecimal getBigDecimal() throws SQLException {
            return null;
        }

        @Override
        public Blob getBlob() throws SQLException {
            return null;
        }

        @Override
        public boolean getBoolean() throws SQLException {
            return false;
        }

        @Override
        public byte getByte() throws SQLException {
            return 0;
        }

        @Override
        public byte[] getBytes() throws SQLException {
            return null;
        }

        @Override
        public Clob getClob() throws SQLException {
            return null;
        }

        @Override
        public int getColumnIndex() {
            return 0;
        }

        @Override
        public String getColumnName() {
            return null;
        }

        @Override
        public Date getDate() throws SQLException {
            return null;
        }

        @Override
        public Date getDate(Calendar cal) throws SQLException {
            return null;
        }

        @Override
        public double getDouble() throws SQLException {
            return 0;
        }

        @Override
        public float getFloat() throws SQLException {
            return 0;
        }

        @Override
        public int getInt() throws SQLException {
            if (value == null)
                return Integer.MIN_VALUE;
            return value;
        }

        @Override
        public long getLong() throws SQLException {
            return 0;
        }

        @Override
        public Object getObject() throws SQLException {
            return null;
        }

        @Override
        public Object getObject(@SuppressWarnings("rawtypes") Map map) throws SQLException {
            return null;
        }

        @Override
        public Ref getRef() throws SQLException {
            return null;
        }

        @Override
        public ResultSet getResultSet() {
            return null;
        }

        @Override
        public short getShort() throws SQLException {
            return 0;
        }

        @Override
        public String getString() throws SQLException {
            return null;
        }

        @Override
        public Time getTime() throws SQLException {
            return null;
        }

        @Override
        public Time getTime(Calendar cal) throws SQLException {
            return null;
        }

        @Override
        public Timestamp getTimestamp() throws SQLException {
            return null;
        }

        @Override
        public Timestamp getTimestamp(Calendar cal) throws SQLException {
            return null;
        }

        @Override
        public URL getURL() throws SQLException {
            return null;
        }

        @Override
        public boolean wasNull() throws SQLException {
            return value == null;
        }
    }

    class MockParameterSetter implements ParameterSetter {

        Integer expected;

        public MockParameterSetter(Integer expected) {
            this.expected = expected;
        }

        @Override
        public int getParameterIndex() {
            return 0;
        }

        @Override
        public PreparedStatement getPreparedStatement() {
            return null;
        }

        @Override
        public void setArray(Array x) throws SQLException {

        }

        @Override
        public void setAsciiStream(InputStream x, int length) throws SQLException {

        }

        @Override
        public void setBigDecimal(BigDecimal x) throws SQLException {

        }

        @Override
        public void setBinaryStream(InputStream x, int length) throws SQLException {

        }

        @Override
        public void setBlob(Blob x) throws SQLException {

        }

        @Override
        public void setBoolean(boolean x) throws SQLException {

        }

        @Override
        public void setByte(byte x) throws SQLException {

        }

        @Override
        public void setBytes(byte[] x) throws SQLException {

        }

        @Override
        public void setCharacterStream(Reader reader, int length) throws SQLException {

        }

        @Override
        public void setClob(Clob x) throws SQLException {

        }

        @Override
        public void setDate(Date x) throws SQLException {

        }

        @Override
        public void setDate(Date x, Calendar cal) throws SQLException {

        }

        @Override
        public void setDouble(double x) throws SQLException {

        }

        @Override
        public void setFloat(float x) throws SQLException {

        }

        @Override
        public void setInt(int x) throws SQLException {
            assertEquals(expected.intValue(), x);
        }

        @Override
        public void setLong(long x) throws SQLException {

        }

        @Override
        public void setNull(int sqlType) throws SQLException {
            assertEquals(Types.INTEGER, sqlType);
        }

        @Override
        public void setNull(int sqlType, String typeName) throws SQLException {

        }

        @Override
        public void setObject(Object x) throws SQLException {

        }

        @Override
        public void setObject(Object x, int targetSqlType) throws SQLException {

        }

        @Override
        public void setObject(Object x, int targetSqlType, int scale) throws SQLException {

        }

        @Override
        public void setRef(Ref x) throws SQLException {

        }

        @Override
        public void setShort(short x) throws SQLException {

        }

        @Override
        public void setString(String x) throws SQLException {

        }

        @Override
        public void setTime(Time x) throws SQLException {

        }

        @Override
        public void setTime(Time x, Calendar cal) throws SQLException {

        }

        @Override
        public void setTimestamp(Timestamp x) throws SQLException {

        }

        @Override
        public void setTimestamp(Timestamp x, Calendar cal) throws SQLException {

        }

        @Override
        public void setURL(URL x) throws SQLException {

        }

    }
}
