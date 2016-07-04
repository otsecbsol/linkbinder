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
package jp.co.opentone.bsol.framework.core.generator.csv;

import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import jp.co.opentone.bsol.framework.core.generator.GeneratorFailedException;

public class CsvGeneratorTest {

    private CsvGenerator generator;

    private static final String ENCODING = "Windows-31J";

    private String pId = "Parent Id";
    private String pName = "Parent Name";
    private String pDate = "Parent Date";
    private String pTimestamp = "Parent Timestamp";
    private String cId = "Child Id";
    private String cName = "Child Name";
    private String cDate = "Child Date";
    private String cTimestamp = "Child Timestamp";
    private String gId = "Grandchild Id";
    private String gDate = "Grandchild Date";
    private String gName = "Grandchild Name";
    private String gTimestamp = "Grandchild Timestamp";

    private Long id = new Long(1);
    private String name = "NAME-001";
    private Date date = new GregorianCalendar(2009, 3, 2).getTime();
    private Date timestamp = new GregorianCalendar(2009, 3, 2, 10, 10, 10).getTime();
    private Long childId = new Long(11);
    private String childName = "NAME-011";
    private Date childDate = new GregorianCalendar(2009, 3, 2).getTime();
    private Date childTimestamp = new GregorianCalendar(2009, 3, 12, 11, 11, 11).getTime();
    private Long gChildId = new Long(111);
    private String gChildName = "NAME-111";
    private Date gChildDate = new GregorianCalendar(2009, 3, 2).getTime();
    private Date gChildTimestamp = new GregorianCalendar(2009, 3, 22, 12, 12, 12).getTime();

    /**
     * ヘッダあり、データあり
     */
    @Test
    public void testGenerate1() {

        List<Object> list = getObjectList();
        List<String> outputFieldNames = getOutputFieldNames();
        List<String> headerNames = getHeaderNames();

        generator =
                new CsvGenerator(list, outputFieldNames, headerNames, ENCODING, getFormatPatterns());

        byte[] actual = generator.generate();

        String expected = getHeaderLine() + getDataLine();

        assertEquals(expected, new String(actual));
    }

    /**
     * ヘッダなし、データあり
     */
    @Test
    public void testGenerate2() {
        List<Object> list = getObjectList();
        List<String> outputFieldNames = getOutputFieldNames();
        List<String> headerNames = new ArrayList<String>();

        generator =
                new CsvGenerator(list, outputFieldNames, headerNames, ENCODING, getFormatPatterns());
        byte[] actual = generator.generate();

        String expected = getDataLine();

        assertEquals(expected, new String(actual));

    }

    /**
     * ヘッダあり、データなし
     */
    @Test
    public void testGenerate3() {
        List<Object> list = new ArrayList<Object>();
        List<String> outputFieldNames = getOutputFieldNames();
        List<String> headerNames = getHeaderNames();

        generator =
                new CsvGenerator(list, outputFieldNames, headerNames, ENCODING, getFormatPatterns());

        byte[] actual = generator.generate();

        String expected = getHeaderLine();

        assertEquals(expected, new String(actual));

    }

    /**
     * ヘッダnull、データあり
     */
    @Test
    public void testGenerate4() {
        List<Object> list = getObjectList();
        List<String> outputFieldNames = getOutputFieldNames();

        generator = new CsvGenerator(list, outputFieldNames, null, ENCODING, getFormatPatterns());

        byte[] actual = generator.generate();

        String expected = getDataLine();

        assertEquals(expected, new String(actual));

    }

    /**
     * ヘッダあり、データnull
     */
    @Test
    public void testGenerate5() {

        try {
            List<String> outputFieldNames = getOutputFieldNames();
            List<String> headerNames = getHeaderNames();

            generator =
                    new CsvGenerator(null, outputFieldNames, headerNames, ENCODING,
                                     getFormatPatterns());

            generator.generate();

            // 失敗
            fail();
        } catch (GeneratorFailedException e) {
            // 成功
        } catch (Exception e) {
            // 失敗
            fail();
        }
    }

    /**
     * データフォーマットなし
     */
    @Test
    public void testGenerate6() {

        try {
            List<Object> list = getObjectList();
            List<String> outputFieldNames = getOutputFieldNames();
            List<String> headerNames = getHeaderNames();

            generator = new CsvGenerator(list, outputFieldNames, headerNames, ENCODING);

            generator.generate();

            // 失敗
            fail();
        } catch (GeneratorFailedException e) {
            // 成功
        } catch (Exception e) {
            // 失敗
            fail();
        }
    }

    private List<Object> getObjectList() {
        MockData gChild = new MockData(gChildId, gChildName, gChildDate, gChildTimestamp, null);
        MockData child = new MockData(childId, childName, childDate, childTimestamp, gChild);
        MockData data = new MockData(id, name, date, timestamp, child);

        List<Object> list = new ArrayList<Object>();
        list.add(data);
        list.add(child);
        list.add(gChild);

        return list;
    }

    private List<String> getOutputFieldNames() {
        List<String> outputFieldNames = new ArrayList<String>();
        outputFieldNames.add("id");
        outputFieldNames.add("name");
        outputFieldNames.add("date");
        outputFieldNames.add("timestamp");
        outputFieldNames.add("child.id");
        outputFieldNames.add("child.name");
        outputFieldNames.add("child.date");
        outputFieldNames.add("child.timestamp");
        outputFieldNames.add("child.child.id");
        outputFieldNames.add("child.child.name");
        outputFieldNames.add("child.child.date");
        outputFieldNames.add("child.child.timestamp");

        return outputFieldNames;
    }

    private List<String> getHeaderNames() {
        List<String> headerNames = new ArrayList<String>();
        headerNames.add(pId);
        headerNames.add(pName);
        headerNames.add(pDate);
        headerNames.add(pTimestamp);
        headerNames.add(cId);
        headerNames.add(cName);
        headerNames.add(cDate);
        headerNames.add(cTimestamp);
        headerNames.add(gId);
        headerNames.add(gName);
        headerNames.add(gDate);
        headerNames.add(gTimestamp);

        return headerNames;
    }

    private String getHeaderLine() {
        String str =
                "\"" + pId + "\",\"" + pName + "\",\"" + pDate + "\",\"" + pTimestamp + "\",\""
                        + cId + "\",\"" + cName + "\",\""
                        + cDate + "\",\"" + cTimestamp + "\",\"" + gId + "\",\""
                        + gName + "\",\"" + gDate + "\",\""
                        + gTimestamp + "\"\n";

        return str;
    }

    private String getDataLine() {
        /** 1行目 */
        String str =
                "\"" + id + "\",\"" + name + "\",\"" + formatDate(date) + "\",\""
                        + formatDateTime(timestamp) + "\",\"" + childId + "\",\"" + childName
                        + "\",\"" + formatDate(childDate) + "\",\""
                        + formatDateTime(childTimestamp) + "\",\"" + gChildId + "\",\""
                        + gChildName + "\",\"" + formatDate(gChildDate) + "\",\""
                        + formatDateTime(gChildTimestamp) + "\"\n"
                        /** 2行目 */
                        + "\"" + childId + "\",\"" + childName + "\",\"" + formatDate(childDate)
                        + "\",\"" + formatDateTime(childTimestamp) + "\",\"" + gChildId + "\",\""
                        + gChildName + "\",\"" + formatDate(gChildDate) + "\",\""
                        + formatDateTime(gChildTimestamp) + "\",\"" + "\",\"" + "\",\"" + "\",\""
                        + "\"\n"
                        /** 3行目 */
                        + "\"" + gChildId + "\",\"" + gChildName + "\",\"" + formatDate(gChildDate)
                        + "\",\"" + formatDateTime(gChildTimestamp) + "\",\"" + "\",\"" + "\",\""
                        + "\",\"" + "\",\"" + "\",\"" + "\",\"" + "\",\"" + "\"\n";

        return str;
    }

    private String formatDate(Date date) {
        return new SimpleDateFormat("dd/MMM/yyyy").format(date);
    }

    private String formatDateTime(Date date) {
        return new SimpleDateFormat("dd/MMM/yyyy HH:mm:ss").format(date);
    }

    private Map<String, String> getFormatPatterns() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("date", "dd/MMM/yyyy");
        map.put("timestamp", "dd/MMM/yyyy HH:mm:ss");
        map.put("child.date", "dd/MMM/yyyy");
        map.put("child.timestamp", "dd/MMM/yyyy HH:mm:ss");
        map.put("child.child.date", "dd/MMM/yyyy");
        map.put("child.child.timestamp", "dd/MMM/yyyy HH:mm:ss");

        return map;
    }

    public static class MockData {

        private Long id;
        private String name;
        private Date date;
        private Date timestamp;
        private MockData child;

        public MockData(Long id, String name, Date date, Date timestamp, MockData child) {
            this.id = id;
            this.name = name;
            this.date = date;
            this.timestamp = timestamp;
            this.child = child;
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

        public Date getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(Date timestamp) {
            this.timestamp = timestamp;
        }

        public MockData getChild() {
            return child;
        }

        public void setChild(MockData child) {
            this.child = child;
        }
    }

}
