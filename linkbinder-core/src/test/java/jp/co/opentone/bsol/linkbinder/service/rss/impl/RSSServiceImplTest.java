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
package jp.co.opentone.bsol.linkbinder.service.rss.impl;

import static org.junit.Assert.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.test.AbstractTestCase;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroup;
import jp.co.opentone.bsol.linkbinder.dto.CorresponType;
import jp.co.opentone.bsol.linkbinder.dto.RSSCorrespon;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.code.CorresponStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.RSSCategory;
import jp.co.opentone.bsol.linkbinder.dto.code.ReplyRequired;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowStatus;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchRSSCorresponCondition;
import jp.co.opentone.bsol.linkbinder.service.rss.RSSService;

/**
 * RSSServiceImplのテストケース.
 * @author opentone
 */
public class RSSServiceImplTest extends AbstractTestCase {

    /**
     * テスト対象.
     */
    @Resource
    private RSSService service;

    /**
     * 改行コードCRLF.
     */
    private static final String CRLF = "\r\n";
    /**
     * 改行コードLF.
     */
    private static final String LF = "\n";

    /**
     * RSSの先頭部分(Itemが空).
     */
    private static final String RSS_HEADER_EMPTY =
          "<?xml version=\"1.0\" encoding=\"UTF-8\"?><rss version=\"2.0\">"
        + "<channel><title>Internal Correspondence</title><link /><description />";

    /**
     * RSSの先頭部分.
     */
    private static final String RSS_HEADER =
          "<?xml version=\"1.0\" encoding=\"UTF-8\"?><rss xmlns:dc=\"http://purl.org/dc/elements/1.1/\" version=\"2.0\">"
        + "<channel><title>Internal Correspondence</title><link /><description />";

    /**
     * RSSの先頭部分.
     */
    private static final String RSS_FOOTER ="</channel></rss>";

    /** Date設定に使う時刻フォーマット. */
    private static final SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    /**
     * テスト前準備.
     */
    @Before
    public void setUp() {
        //TODO JMockitバージョンアップ対応
//        Mockit.redefineMethods(CorresponDaoImpl.class, MockCorresponDao.class);
//        Mockit.redefineMethods(DateUtil.class, MockDateUtil.class);
//        Mockit.redefineMethods(SystemConfig.class, MockSystemConfig.class);
    }

    /**
     * 後始末.
     */
    @After
    public void tearDown() {
        MockCorresponDao.FIND_RESULT = null;
        MockCorresponDao.CONDITION = null;
        MockDateUtil.DATE = new Date();;
        MockSystemConfig.VALUE = "20";

        // 差し換えたMockをクリアする
        //TODO JMockitバージョンアップ対応
//        Mockit.tearDownMocks();
    }

    /**
     * テスト01 引数エラー.
     */
    @Test
    public void testGetRSS01ArgumentError() throws Exception {
        // テスト実行
        try{
            service.getRSS(null, "http://foo.example.com/");
            fail("例外が発生していない");
        } catch(IllegalArgumentException e){
        }

        try{
            service.getRSS("", "http://foo.example.com/");
            fail("例外が発生していない");
        } catch(IllegalArgumentException e){
        }
        try{
            service.getRSS("ZZB00", null);
            fail("例外が発生していない");
        } catch(IllegalArgumentException e){
        }
        try{
            service.getRSS("ZZB00", "");
            fail("例外が発生していない");
        } catch(IllegalArgumentException e){
        }
    }

    /**
     * テスト02 取得0件.
     */
    @Test
    public void testGetRSS02FindRSSCorresponResultIsNone() throws Exception {
        String actual = null;

        MockCorresponDao.FIND_RESULT = null;
        actual = service.getRSS("ZZB01", "http://foo.example.com/");
        assertXMLEquals(null, actual);

        MockCorresponDao.FIND_RESULT = new ArrayList<RSSCorrespon>();
        actual = service.getRSS("ZZB01", "http://foo.example.com/");
        assertXMLEquals(null, actual);
    }

    /**
     * テスト03 日時テスト.
     */
    @Test
    public void testGetRSS03Date() throws Exception {
        String date = "2009-02-20 12:34:56.789";
        String expectedDate = "2009-02-01 00:00:00.000";
        String userId = "ZZA00";

        SearchRSSCorresponCondition expected = new SearchRSSCorresponCondition();
        expected.setDayPeriod(convertStringToDate(expectedDate));
        expected.setUserId(userId);

        MockDateUtil.DATE = convertStringToDate(date);
        MockCorresponDao.FIND_RESULT = null;

        // 期待値通りのUserId,日付になっているかをCONDITIONに設定して確認
        MockCorresponDao.CONDITION = expected;
        String actual = service.getRSS(userId, "http://foo.example.com/");
        assertXMLEquals(null, actual);
    }

    /**
     * テスト04 日時テスト2.
     */
    @Test
    public void testGetRSS04Date() throws Exception {
        String date = "2010-01-19 23:59:59.999";
        String expectedDate = "2009-12-31 00:00:00.000";
        String userId = "ZZA00";

        SearchRSSCorresponCondition expected = new SearchRSSCorresponCondition();
        expected.setDayPeriod(convertStringToDate(expectedDate));
        expected.setUserId(userId);

        MockDateUtil.DATE = convertStringToDate(date);
        MockCorresponDao.FIND_RESULT = null;

        // 期待値通りのUserId,日付になっているかをCONDITIONに設定して確認
        MockCorresponDao.CONDITION = expected;
        String actual = service.getRSS(userId, "http://foo.example.com/");
        assertXMLEquals(null, actual);
    }

    /**
     * テスト05 日時テスト3.
     */
    @Test
    public void testGetRSS05Date() throws Exception {
        String date = "2010-01-20 00:00:00.000";
        String expectedDate = "2010-01-01 00:00:00.000";
        String userId = "ZZA00";

        SearchRSSCorresponCondition expected = new SearchRSSCorresponCondition();
        expected.setDayPeriod(convertStringToDate(expectedDate));
        expected.setUserId(userId);

        MockDateUtil.DATE = convertStringToDate(date);
        MockCorresponDao.FIND_RESULT = null;

        // 期待値通りのUserId,日付になっているかをCONDITIONに設定して確認
        MockCorresponDao.CONDITION = expected;
        String actual = service.getRSS(userId, "http://foo.example.com/");
        assertXMLEquals(null, actual);
    }

    /**
     * テスト06 RSS出力テキストチェック.
     */
    @Test
    public void testGetRSS06() throws Exception {

        MockCorresponDao.FIND_RESULT = makeFindResult();

        String actual = service.getRSS("ZZB01", "http://foo.example.com/");

        String expectedC1Description =
            "No. : 1"+ "&lt;br /&gt;"
          + "Correspondence No. : c1CorresponNo" + "&lt;br /&gt;"
          + "From : c1Groupname" + "&lt;br /&gt;"
          + "To : c1ToCorresponGroup..." + "&lt;br /&gt;"
          + "Cc : c1CcCorresponGroup..." + "&lt;br /&gt;"
          + "Type : c1CorresponType : c1CorresponTypeName" + "&lt;br /&gt;"
          + "Subject : c1Subject" + "&lt;br /&gt;"
          + "Workflow Status : Draft" + "&lt;br /&gt;"
          + "Created on : 01-Apr-2011" + "&lt;br /&gt;"
          + "Issued on : 23-Jan-2010" + "&lt;br /&gt;"
          + "Deadline for Reply : 31-Dec-2009" + "&lt;br /&gt;"
          + "Created by : c1CreatedUserNameE/c1CreatedUserEmpNo" + "&lt;br /&gt;"
          + "Issued by : c1IssuedUserNameE/c1IssuedUserEmpNo" + "&lt;br /&gt;"
          + "Reply Required : Yes" + "&lt;br /&gt;"
          + "URL : http://foo.example.com/correspon/correspon.jsf?id=1&amp;projectId=c1ProjectId";
      String expectedC1Item = "<item>"
          + "<title>c1Subject</title>"
          + "<link>http://foo.example.com/correspon/correspon.jsf?id=1&amp;projectId=c1ProjectId</link>"
          + "<description>" + expectedC1Description  + "</description>"
          + "<category>Issue Notice(Attention)</category>"
          + "<pubDate>Wed, 02 Aug 2045 15:12:34 GMT</pubDate>"
          + "<author>c1ProjectId : c1ProjectName</author>"
          + "<guid>http://foo.example.com/correspon/correspon.jsf?id=1&amp;projectId=c1ProjectId</guid>"
          + "<dc:date>2045-08-02T15:12:34Z</dc:date>"
          + "</item>";

      String expectedC2Description =
          "No. : 2"+ "&lt;br /&gt;"
        + "Correspondence No. : " + "&lt;br /&gt;"
        + "From : c2Groupname" + "&lt;br /&gt;"
        + "To : c2ToCorresponGroup" + "&lt;br /&gt;"
        + "Cc : " + "&lt;br /&gt;"
        + "Type : c2CorresponType : c2CorresponTypeName" + "&lt;br /&gt;"
        + "Subject : c2Subject" + "&lt;br /&gt;"
        + "Workflow Status : Request for Approval" + "&lt;br /&gt;"
        + "Created on : 01-Apr-2011" + "&lt;br /&gt;"
        + "Issued on : " + "&lt;br /&gt;"
        + "Deadline for Reply : " + "&lt;br /&gt;"
        + "Created by : c2CreatedUserNameE/c2CreatedUserEmpNo" + "&lt;br /&gt;"
        + "Issued by : " + "&lt;br /&gt;"
        + "Reply Required : " + "&lt;br /&gt;"
        + "URL : http://foo.example.com/correspon/correspon.jsf?id=2&amp;projectId=c2ProjectId";
    String expectedC2Item = "<item>"
        + "<title>c2Subject</title>"
        + "<link>http://foo.example.com/correspon/correspon.jsf?id=2&amp;projectId=c2ProjectId</link>"
        + "<description>" + expectedC2Description  + "</description>"
        + "<category>Request for Check</category>"
        + "<pubDate>Sun, 01 Apr 2012 14:00:45 GMT</pubDate>"
        + "<author>c2ProjectId : c2ProjectName</author>"
        + "<guid>http://foo.example.com/correspon/correspon.jsf?id=2&amp;projectId=c2ProjectId</guid>"
        + "<dc:date>2012-04-01T14:00:45Z</dc:date>"
        + "</item>";

        assertXMLEquals(expectedC1Item + expectedC2Item, actual);
    }
 // Serviceがスキーマ(http/https)を意識するのはおかしいので処理をServletに移動した。
 // よってこのテストは不要
//
//    /**
//     * テスト06 RSS出力テキストチェック(baseURLがhttps).
//     */
//    @Test
//    public void testGetRSS07Https() throws Exception {
//
//        MockCorresponDao.FIND_RESULT = makeFindResult();
//
//        String actual = service.getRSS("ZZB01", "https://foo.example.com/");
//
//        String expectedC1Description =
//            "No : 1"+ "&lt;br /&gt;"
//          + "Correspondence No : c1CorresponNo" + "&lt;br /&gt;"
//          + "From : c1Groupname" + "&lt;br /&gt;"
//          + "To : c1ToCorresponGroup..." + "&lt;br /&gt;"
//          + "Cc : c1CcCorresponGroup..." + "&lt;br /&gt;"
//          + "Type : c1CorresponType : c1CorresponTypeName" + "&lt;br /&gt;"
//          + "Subject : c1Subject" + "&lt;br /&gt;"
//          + "Workflow Status : Draft" + "&lt;br /&gt;"
//          + "Created on : 01-Apr-2011" + "&lt;br /&gt;"
//          + "Issued on : 23-Jan-2010" + "&lt;br /&gt;"
//          + "Deadline for Reply : 31-Dec-2009" + "&lt;br /&gt;"
//          + "Created by : c1CreatedUserNameE/c1CreatedUserEmpNo" + "&lt;br /&gt;"
//          + "Issued by : c1IssuedUserNameE/c1IssuedUserEmpNo" + "&lt;br /&gt;"
//          + "Reply Required : Yes" + "&lt;br /&gt;"
//          + "URL : http://foo.example.com/correspon/correspon.jsf?id=1";
//      String expectedC1Item = "<item>"
//          + "<title>c1Subject</title>"
//          + "<link>http://foo.example.com/?id=1</link>"
//          + "<description>" + expectedC1Description  + "</description>"
//          + "<category>Issued Notice(Attention)</category>"
//          + "<pubDate>Wed, 02 Aug 2045 15:12:34 GMT</pubDate>"
//          + "<author>c1ProjectId : c1ProjectName</author>"
//          + "<guid>http://foo.example.com/?id=1</guid>"
//          + "<dc:date>2045-08-02T15:12:34Z</dc:date>"
//          + "</item>";
//
//      String expectedC2Description =
//          "No : 2"+ "&lt;br /&gt;"
//        + "Correspondence No : " + "&lt;br /&gt;"
//        + "From : c2Groupname" + "&lt;br /&gt;"
//        + "To : c2ToCorresponGroup" + "&lt;br /&gt;"
//        + "Cc : " + "&lt;br /&gt;"
//        + "Type : c2CorresponType : c2CorresponTypeName" + "&lt;br /&gt;"
//        + "Subject : c2Subject" + "&lt;br /&gt;"
//        + "Workflow Status : Request for Approval" + "&lt;br /&gt;"
//        + "Created on : 01-Apr-2011" + "&lt;br /&gt;"
//        + "Issued on : " + "&lt;br /&gt;"
//        + "Deadline for Reply : " + "&lt;br /&gt;"
//        + "Created by : c2CreatedUserNameE/c2CreatedUserEmpNo" + "&lt;br /&gt;"
//        + "Issued by : " + "&lt;br /&gt;"
//        + "Reply Required : " + "&lt;br /&gt;"
//        + "URL : http://foo.example.com/correspon/correspon.jsf?id=2";
//    String expectedC2Item = "<item>"
//        + "<title>c2Subject</title>"
//        + "<link>http://foo.example.com/?id=2</link>"
//        + "<description>" + expectedC2Description  + "</description>"
//        + "<category>Request for Check</category>"
//        + "<pubDate>Sun, 01 Apr 2012 14:00:45 GMT</pubDate>"
//        + "<author>c2ProjectId : c2ProjectName</author>"
//        + "<guid>http://foo.example.com/?id=2</guid>"
//        + "<dc:date>2012-04-01T14:00:45Z</dc:date>"
//        + "</item>";
//
//        assertXMLEquals(expectedC1Item + expectedC2Item, actual);
//    }
//
    /**
     * テスト08 Config値不正.
     */
    @Test
    public void testGetRSS08ConfigError() throws Exception {
        MockSystemConfig.VALUE = "34a";
        try {
            service.getRSS("ZZA00", "http://foo.example.com/");
            fail("例外が発生しませんでした");
        } catch (ServiceAbortException e) {
            assertEquals("config parameter[rss.dayperiod] error.　value["
                + MockSystemConfig.VALUE + "]", e.getMessage());
        }

        MockSystemConfig.VALUE = "";
        try {
            service.getRSS("ZZA00", "http://foo.example.com/");
            fail("例外が発生しませんでした");
        } catch (ServiceAbortException e) {
            assertEquals("config parameter[rss.dayperiod] error.　value["
                + MockSystemConfig.VALUE + "]", e.getMessage());
        }

        MockSystemConfig.VALUE = null;
        try {
            service.getRSS("ZZA00", "http://foo.example.com/");
            fail("例外が発生しませんでした");
        } catch (ServiceAbortException e) {
            assertEquals("config parameter[rss.dayperiod] error.　value["
                + MockSystemConfig.VALUE + "]", e.getMessage());
        }
    }


    private List<RSSCorrespon> makeFindResult(){
        List<RSSCorrespon> result = new ArrayList<RSSCorrespon>();

        // 全項目を設定
        RSSCorrespon c1 = new RSSCorrespon();
        c1.setId(1L);
        c1.setCorresponNo("c1CorresponNo");
        c1.setProjectId("c1ProjectId");
        c1.setProjectNameE("c1ProjectName");
        CorresponGroup c1From = new CorresponGroup();
        c1From.setName("c1Groupname");
        c1.setFromCorresponGroup(c1From);
        CorresponType c1CorresponType = new CorresponType();
        c1CorresponType.setCorresponType("c1CorresponType");
        c1CorresponType.setName("c1CorresponTypeName");
        c1.setCorresponType(c1CorresponType);
        c1.setSubject("c1Subject");
        User c1IssuedUser = new User();
        c1IssuedUser.setEmpNo("c1IssuedUserEmpNo");
        c1IssuedUser.setNameE("c1IssuedUserNameE");
        c1.setIssuedBy(c1IssuedUser);
        c1.setIssuedAt(convertStringToDate("2010-01-23 12:34:56.789"));
        c1.setCorresponStatus(CorresponStatus.OPEN);
        c1.setReplyRequired(ReplyRequired.YES);
        c1.setDeadlineForReply(convertStringToDate("2009-12-31 01:23:00.123"));
        c1.setWorkflowStatus(WorkflowStatus.DRAFT);
        User c1CreatedUser = new User();
        c1CreatedUser.setEmpNo("c1CreatedUserEmpNo");
        c1CreatedUser.setNameE("c1CreatedUserNameE");
        c1.setCreatedBy(c1CreatedUser);
        c1.setCreatedAt(convertStringToDate("2011-04-01 23:00:45.123"));
        User c1UpdatedUser = new User();
        c1UpdatedUser.setEmpNo("c1UdatedUserEmpNo");
        c1UpdatedUser.setNameE("c1UdatedUserNameE");
        c1.setUpdatedBy(c1UpdatedUser);
        c1.setUpdatedAt(convertStringToDate("2045-08-03 00:12:34.123"));
        CorresponGroup c1ToCorresponGroup = new CorresponGroup();
        c1ToCorresponGroup.setName("c1ToCorresponGroup");
        c1.setToCorresponGroup(c1ToCorresponGroup);
        c1.setToCorresponGroupCount(2L);
        CorresponGroup c1CcCorresponGroup = new CorresponGroup();
        c1CcCorresponGroup.setName("c1CcCorresponGroup");
        c1.setCcCorresponGroup(c1CcCorresponGroup);
        c1.setCcCorresponGroupCount(2L);
        c1.setCategory(RSSCategory.ISSUE_NOTICE_ATTENTION);
        result.add(c1);

        // 必要最小限の箇所のみ設定
        RSSCorrespon c2 = new RSSCorrespon();
        c2.setId(2L);
        c2.setCorresponNo(null);
        c2.setProjectId("c2ProjectId");
        c2.setProjectNameE("c2ProjectName");
        CorresponGroup c2From = new CorresponGroup();
        c2From.setName("c2Groupname");
        c2.setFromCorresponGroup(c2From);
        CorresponType c2CorresponType = new CorresponType();
        c2CorresponType.setCorresponType("c2CorresponType");
        c2CorresponType.setName("c2CorresponTypeName");
        c2.setCorresponType(c2CorresponType);
        //制御文字を挿入
        c2.setSubject(insertControlCharacter("c2Subject"));
//        c2.setSubject("c2Subject");
        c2.setIssuedBy(null);
        c2.setIssuedAt(null);
        c2.setCorresponStatus(CorresponStatus.CANCELED);
        c2.setReplyRequired(null);
        c2.setDeadlineForReply(null);
        c2.setWorkflowStatus(WorkflowStatus.REQUEST_FOR_APPROVAL);
        User c2CreatedUser = new User();
        c2CreatedUser.setEmpNo("c2CreatedUserEmpNo");
        c2CreatedUser.setNameE("c2CreatedUserNameE");
        c2.setCreatedBy(c2CreatedUser);
        c2.setCreatedAt(convertStringToDate("2011-04-01 23:00:45.123"));
        User c2UpdatedUser = new User();
        c2UpdatedUser.setEmpNo("c2UdatedUserEmpNo");
        c2UpdatedUser.setNameE("c2UdatedUserNameE");
        c2.setUpdatedBy(c2UpdatedUser);
        c2.setUpdatedAt(convertStringToDate("2012-04-01 23:00:45.123"));
        CorresponGroup c2ToCorresponGroup = new CorresponGroup();
        c2ToCorresponGroup.setName("c2ToCorresponGroup");
        c2.setToCorresponGroup(c2ToCorresponGroup);
        c2.setToCorresponGroupCount(1L);
        c2.setCcCorresponGroup(null);
        c2.setCcCorresponGroupCount(0L);
        c2.setCategory(RSSCategory.REQUEST_FOR_CHECK);
        result.add(c2);
        return result;
    }

    private String insertControlCharacter(String str) {
        //XML1.0仕様で使用が禁止されている制御文字を挿入
        StringBuilder buf = new StringBuilder(str);

        for (char c = 0; c <= 0x1f; c++) {
            if (c == 0x09 || c == 0x0a || c == 0x0d) {
                continue;
            }
            buf.append(c);
        }
        return buf.toString();
    }


    /**
     * 改行コードを取り除き、かつタグとタグの間の空白を削除してRSS用XML文字列比較をする.
     * @param expected
     * @param actual
     */
    private void assertXMLEquals(String expected, String actual){
        actual = actual.replaceAll(CRLF, "");
        actual = actual.replaceAll(LF, "");
        actual = actual.replaceAll("> +<", "><");
        if (StringUtils.isEmpty(expected)) {
            assertEquals(RSS_HEADER_EMPTY + RSS_FOOTER, actual);
        } else {
            assertEquals(RSS_HEADER + expected + RSS_FOOTER, actual);
        }
    }

    private Date convertStringToDate(String date){
        try {
            return DATE_FORMAT.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////
    public static class MockCorresponDao {
        static List<RSSCorrespon>  FIND_RESULT = null;
        static SearchRSSCorresponCondition CONDITION = null;
        public List<RSSCorrespon> findRSSCorrespon(SearchRSSCorresponCondition cond){
            if (CONDITION != null) {
                assertEquals(CONDITION.getUserId(), cond.getUserId());
                assertEquals(CONDITION.getDayPeriod(), cond.getDayPeriod());
            }
            return FIND_RESULT;
        }
    }

    public static class MockDateUtil {
        static Date DATE = new Date();;
        public Date getNow(){
            return DATE;
        }
    }

    public static class MockSystemConfig {
        public static String VALUE = "20";

        public static String getValue(String key) {
            return VALUE;
        }
    }
}

