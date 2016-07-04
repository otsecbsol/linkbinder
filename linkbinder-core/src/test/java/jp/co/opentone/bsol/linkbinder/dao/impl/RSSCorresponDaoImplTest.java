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
package jp.co.opentone.bsol.linkbinder.dao.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;

import jp.co.opentone.bsol.framework.test.AbstractDaoTestCase;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroup;
import jp.co.opentone.bsol.linkbinder.dto.RSSCorrespon;
import jp.co.opentone.bsol.linkbinder.dto.code.RSSCategory;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchRSSCorresponCondition;

/**
 * CorresponDaoImplのテストケース.
 * @author opentone
 */
public class RSSCorresponDaoImplTest extends AbstractDaoTestCase {

    /**
     * テスト対象.
     */
    @Resource
    private CorresponDaoImpl dao;
    /**
     * @throws Exception
     */
    @Test
    public void testFindRSSCorrespon1() throws Exception {
        SearchRSSCorresponCondition condition = new SearchRSSCorresponCondition();

        condition.setUserId("ZZA01");
        String targetDateText = "2010/01/01";

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        Date dayPeriod = sdf.parse(targetDateText);
        condition.setDayPeriod(dayPeriod);

        List<RSSCorrespon> list = dao.findRSSCorrespon(condition);
        assertEquals(getExpected1().size(), list.size());
        Object[] expectedObject = getExpected1().toArray();
        Object[] resultObject = list.toArray();
        for(int i = 0; i < list.size(); i++) {
            RSSCorrespon expected = (RSSCorrespon)expectedObject[i];
            RSSCorrespon result = (RSSCorrespon)resultObject[i];
            assertEquals(expected.getId(), result.getId());
            assertEquals(expected.getCorresponNo(), result.getCorresponNo());
            assertEquals(expected.getSubject(), result.getSubject());
            assertEquals(expected.getToGroupName(), result.getToGroupName());
            assertEquals(expected.getCcGroupName(), result.getCcGroupName());
            assertEquals(expected.getCategory(), result.getCategory());
        }
    }

    private List<RSSCorrespon> getExpected1(){
        List<RSSCorrespon> list = new ArrayList<RSSCorrespon>();
        RSSCorrespon c1 = new RSSCorrespon();
        c1.setId(1L);
        c1.setCorresponNo("a");
        c1.setSubject("aaa");
        c1.setCategory(RSSCategory.ISSUE_NOTICE_ATTENTION);
        CorresponGroup toCg1 = new CorresponGroup();
        toCg1.setName("YOC:IT");
        c1.setToCorresponGroup(toCg1);
        c1.setToCorresponGroupCount(2L);
        CorresponGroup ccCg1 = new CorresponGroup();
        ccCg1.setName("YOC:PIPING");
        c1.setCcCorresponGroup(ccCg1);
        c1.setCcCorresponGroupCount(1L);

        RSSCorrespon c2 = new RSSCorrespon();
        c2.setId(2L);
        c2.setCorresponNo("b");
        c2.setSubject("bbb");
        c2.setCategory(RSSCategory.ISSUE_NOTICE_ATTENTION);
        CorresponGroup toCg2 = new CorresponGroup();
        toCg2.setName("YOC:BUILDING");
        c2.setToCorresponGroup(toCg2);
        c2.setToCorresponGroupCount(1L);
        CorresponGroup ccCg2 = new CorresponGroup();
        ccCg2.setName("YOC:PIPING");
        c2.setCcCorresponGroup(ccCg2);
        c2.setCcCorresponGroupCount(1L);

        RSSCorrespon c3= new RSSCorrespon();
        c3.setId(3L);
        c3.setCorresponNo("c");
        c3.setSubject("ccc");
        c3.setCategory(RSSCategory.ISSUE_NOTICE_ATTENTION);
        CorresponGroup toCg3 = new CorresponGroup();
        toCg3.setName("YOC:IT");
        c3.setToCorresponGroup(toCg3);
        c3.setToCorresponGroupCount(1L);

        RSSCorrespon c7= new RSSCorrespon();
        c7.setId(7L);
        c7.setCorresponNo("d");
        c7.setSubject("ddd");
        c7.setCategory(RSSCategory.ISSUE_NOTICE_CC);
        CorresponGroup toCg7 = new CorresponGroup();
        toCg7.setName("YOC:BUILDING");
        c7.setToCorresponGroup(toCg7);
        c7.setToCorresponGroupCount(1L);
        CorresponGroup ccCg7 = new CorresponGroup();
        ccCg7.setName("YOC:PIPING");
        c7.setCcCorresponGroup(ccCg7);
        c7.setCcCorresponGroupCount(1L);

        RSSCorrespon c8= new RSSCorrespon();
        c8.setId(8L);
        c8.setCorresponNo("e");
        c8.setSubject("eee");
        c8.setCategory(RSSCategory.ISSUE_NOTICE_CC);
        CorresponGroup toCg8 = new CorresponGroup();
        toCg8.setName("YOC:IT");
        c8.setToCorresponGroup(toCg8);
        c8.setToCorresponGroupCount(1L);
        CorresponGroup ccCg8 = new CorresponGroup();
        ccCg8.setName("YOC:PIPING");
        c8.setCcCorresponGroup(ccCg8);
        c8.setCcCorresponGroupCount(1L);

        RSSCorrespon c12= new RSSCorrespon();
        c12.setId(12L);
        c12.setCorresponNo("f");
        c12.setSubject("fff");
        c12.setCategory(RSSCategory.PERSON_IN_CHARGE);
        CorresponGroup toCg12 = new CorresponGroup();
        toCg12.setName("YOC:BUILDING");
        c12.setToCorresponGroup(toCg12);
        c12.setToCorresponGroupCount(1L);

        RSSCorrespon c13= new RSSCorrespon();
        c13.setId(13L);
        c13.setCorresponNo("g");
        c13.setSubject("ggg");
        c13.setCategory(RSSCategory.PERSON_IN_CHARGE);
        CorresponGroup toCg13 = new CorresponGroup();
        toCg13.setName("YOC:PIPING");
        c13.setToCorresponGroup(toCg13);
        c13.setToCorresponGroupCount(1L);

        RSSCorrespon c17= new RSSCorrespon();
        c17.setId(17L);
        c17.setCorresponNo("h");
        c17.setSubject("hhh");
        c17.setCategory(RSSCategory.DENIED);
        RSSCorrespon c18= new RSSCorrespon();
        c18.setId(18L);
        c18.setCorresponNo("i");
        c18.setSubject("iii");
        c18.setCategory(RSSCategory.DENIED);
        RSSCorrespon c19= new RSSCorrespon();
        c19.setId(19L);
        c19.setCorresponNo("j");
        c19.setSubject("jjj");
        c19.setCategory(RSSCategory.DENIED);
        RSSCorrespon c22= new RSSCorrespon();
        c22.setId(22L);
        c22.setCorresponNo("k");
        c22.setSubject("kkk");
        c22.setCategory(RSSCategory.REQUEST_FOR_CHECK);
        RSSCorrespon c26= new RSSCorrespon();
        c26.setId(26L);
        c26.setCorresponNo("l");
        c26.setSubject("lll");
        c26.setCategory(RSSCategory.REQUEST_FOR_CHECK);
        RSSCorrespon c27= new RSSCorrespon();
        c27.setId(27L);
        c27.setCorresponNo("m");
        c27.setSubject("mmm");
        c27.setCategory(RSSCategory.REQUEST_FOR_CHECK);
        RSSCorrespon c30= new RSSCorrespon();
        c30.setId(30L);
        c30.setCorresponNo("n");
        c30.setSubject("nnn");
        c30.setCategory(RSSCategory.REQUEST_FOR_APPROVAL);
        RSSCorrespon c34= new RSSCorrespon();
        c34.setId(34L);
        c34.setCorresponNo("o");
        c34.setSubject("ooo");
        c34.setCategory(RSSCategory.REQUEST_FOR_APPROVAL);
        RSSCorrespon c35= new RSSCorrespon();
        c35.setId(35L);
        c35.setCorresponNo("p");
        c35.setSubject("ppp");
        c35.setCategory(RSSCategory.REQUEST_FOR_APPROVAL);

        list.add(c35);
        list.add(c27);
        list.add(c34);
        list.add(c26);
        list.add(c19);
        list.add(c3);
        list.add(c18);
        list.add(c13);
        list.add(c8);
        list.add(c2);
        list.add(c17);
        list.add(c12);
        list.add(c30);
        list.add(c22);
        list.add(c7);
        list.add(c1);

        return list;
    }

    /**
     * @throws Exception
     */
    @Test
    public void testFindRSSCorrespon2() throws Exception {
        SearchRSSCorresponCondition condition = new SearchRSSCorresponCondition();

        condition.setUserId("ZZA01");
        String targetDateText = "2010/01/03";

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        Date dayPeriod = sdf.parse(targetDateText);
        condition.setDayPeriod(dayPeriod);

        List<RSSCorrespon> list = dao.findRSSCorrespon(condition);

        assertEquals(getExpected2().size(), list.size());
        Object[] expectedObject = getExpected2().toArray();
        Object[] resultObject = list.toArray();
        for(int i = 0; i < list.size(); i++) {
            RSSCorrespon expected = (RSSCorrespon)expectedObject[i];
            RSSCorrespon result = (RSSCorrespon)resultObject[i];
            assertEquals(expected.getId(), result.getId());
            assertEquals(expected.getCorresponNo(), result.getCorresponNo());
            assertEquals(expected.getSubject(), result.getSubject());
            assertEquals(expected.getToGroupName(), result.getToGroupName());
            assertEquals(expected.getCcGroupName(), result.getCcGroupName());
            assertEquals(expected.getCategory(), result.getCategory());
        }
    }

    private List<RSSCorrespon> getExpected2(){
        List<RSSCorrespon> list = new ArrayList<RSSCorrespon>();
        RSSCorrespon c1 = new RSSCorrespon();
        c1.setId(1L);
        c1.setCorresponNo("a");
        c1.setSubject("aaa");
        c1.setCategory(RSSCategory.ISSUE_NOTICE_ATTENTION);
        CorresponGroup toCg1 = new CorresponGroup();
        toCg1.setName("YOC:IT");
        c1.setToCorresponGroup(toCg1);
        c1.setToCorresponGroupCount(1L);
        CorresponGroup ccCg1 = new CorresponGroup();
        ccCg1.setName("YOC:PIPING");
        c1.setCcCorresponGroup(ccCg1);
        c1.setCcCorresponGroupCount(1L);

        RSSCorrespon c2 = new RSSCorrespon();
        c2.setId(2L);
        c2.setCorresponNo("b");
        c2.setSubject("bbb");
        c2.setCategory(RSSCategory.ISSUE_NOTICE_ATTENTION);
        CorresponGroup toCg2 = new CorresponGroup();
        toCg2.setName("YOC:BUILDING");
        c2.setToCorresponGroup(toCg2);
        c2.setToCorresponGroupCount(1L);
        CorresponGroup ccCg2 = new CorresponGroup();
        ccCg2.setName("YOC:PIPING");
        c2.setCcCorresponGroup(ccCg2);
        c2.setCcCorresponGroupCount(1L);

        RSSCorrespon c3= new RSSCorrespon();
        c3.setId(3L);
        c3.setCorresponNo("c");
        c3.setSubject("ccc");
        c3.setCategory(RSSCategory.ISSUE_NOTICE_ATTENTION);
        CorresponGroup toCg3 = new CorresponGroup();
        toCg3.setName("YOC:IT");
        c3.setToCorresponGroup(toCg3);
        c3.setToCorresponGroupCount(1L);

        RSSCorrespon c7= new RSSCorrespon();
        c7.setId(7L);
        c7.setCorresponNo("d");
        c7.setSubject("ddd");
        c7.setCategory(RSSCategory.ISSUE_NOTICE_CC);
        CorresponGroup toCg7 = new CorresponGroup();
        toCg7.setName("YOC:BUILDING");
        c7.setToCorresponGroup(toCg7);
        c7.setToCorresponGroupCount(1L);
        CorresponGroup ccCg7 = new CorresponGroup();
        ccCg7.setName("YOC:PIPING");
        c7.setCcCorresponGroup(ccCg7);
        c7.setCcCorresponGroupCount(1L);

        RSSCorrespon c8= new RSSCorrespon();
        c8.setId(8L);
        c8.setCorresponNo("e");
        c8.setSubject("eee");
        c8.setCategory(RSSCategory.ISSUE_NOTICE_CC);
        CorresponGroup toCg8 = new CorresponGroup();
        toCg8.setName("YOC:IT");
        c8.setToCorresponGroup(toCg8);
        c8.setToCorresponGroupCount(1L);
        CorresponGroup ccCg8 = new CorresponGroup();
        ccCg8.setName("YOC:PIPING");
        c8.setCcCorresponGroup(ccCg8);
        c8.setCcCorresponGroupCount(1L);

        RSSCorrespon c12= new RSSCorrespon();
        c12.setId(12L);
        c12.setCorresponNo("f");
        c12.setSubject("fff");
        c12.setCategory(RSSCategory.PERSON_IN_CHARGE);
        CorresponGroup toCg12 = new CorresponGroup();
        toCg12.setName("YOC:BUILDING");
        c12.setToCorresponGroup(toCg12);
        c12.setToCorresponGroupCount(1L);

        RSSCorrespon c13= new RSSCorrespon();
        c13.setId(13L);
        c13.setCorresponNo("g");
        c13.setSubject("ggg");
        c13.setCategory(RSSCategory.PERSON_IN_CHARGE);
        CorresponGroup toCg13 = new CorresponGroup();
        toCg13.setName("YOC:PIPING");
        c13.setToCorresponGroup(toCg13);
        c13.setToCorresponGroupCount(1L);

        RSSCorrespon c17= new RSSCorrespon();
        c17.setId(17L);
        c17.setCorresponNo("h");
        c17.setSubject("hhh");
        c17.setCategory(RSSCategory.DENIED);
        RSSCorrespon c18= new RSSCorrespon();
        c18.setId(18L);
        c18.setCorresponNo("i");
        c18.setSubject("iii");
        c18.setCategory(RSSCategory.DENIED);
        RSSCorrespon c19= new RSSCorrespon();
        c19.setId(19L);
        c19.setCorresponNo("j");
        c19.setSubject("jjj");
        c19.setCategory(RSSCategory.DENIED);
        RSSCorrespon c22= new RSSCorrespon();
        c22.setId(22L);
        c22.setCorresponNo("k");
        c22.setSubject("kkk");
        c22.setCategory(RSSCategory.REQUEST_FOR_CHECK);
        RSSCorrespon c26= new RSSCorrespon();
        c26.setId(26L);
        c26.setCorresponNo("l");
        c26.setSubject("lll");
        c26.setCategory(RSSCategory.REQUEST_FOR_CHECK);
        RSSCorrespon c27= new RSSCorrespon();
        c27.setId(27L);
        c27.setCorresponNo("m");
        c27.setSubject("mmm");
        c27.setCategory(RSSCategory.REQUEST_FOR_CHECK);
        RSSCorrespon c30= new RSSCorrespon();
        c30.setId(30L);
        c30.setCorresponNo("n");
        c30.setSubject("nnn");
        c30.setCategory(RSSCategory.REQUEST_FOR_APPROVAL);
        RSSCorrespon c34= new RSSCorrespon();
        c34.setId(34L);
        c34.setCorresponNo("o");
        c34.setSubject("ooo");
        c34.setCategory(RSSCategory.REQUEST_FOR_APPROVAL);
        RSSCorrespon c35= new RSSCorrespon();
        c35.setId(35L);
        c35.setCorresponNo("p");
        c35.setSubject("ppp");
        c35.setCategory(RSSCategory.REQUEST_FOR_APPROVAL);

        list.add(c35);
        list.add(c27);
        list.add(c34);
        list.add(c26);
        list.add(c19);
        list.add(c3);

        return list;
    }

    /**
     * @throws Exception
     */
    @Test
    public void testFindRSSCorrespon3() throws Exception {
        SearchRSSCorresponCondition condition = new SearchRSSCorresponCondition();

        condition.setUserId("ZZA01");
        String targetDateText = "2015/01/31";

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        Date dayPeriod = sdf.parse(targetDateText);
        condition.setDayPeriod(dayPeriod);

        List<RSSCorrespon> list = dao.findRSSCorrespon(condition);
        assertEquals(0, list.size());
    }

    /**
     * @throws Exception
     */
    @Test
    public void testFindRSSCorrespon4() throws Exception {
        SearchRSSCorresponCondition condition = new SearchRSSCorresponCondition();

        condition.setUserId("ZZB99");
        String targetDateText = "2001/01/01";

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        Date dayPeriod = sdf.parse(targetDateText);
        condition.setDayPeriod(dayPeriod);

        List<RSSCorrespon> list = dao.findRSSCorrespon(condition);
        assertEquals(0, list.size());
    }


}
