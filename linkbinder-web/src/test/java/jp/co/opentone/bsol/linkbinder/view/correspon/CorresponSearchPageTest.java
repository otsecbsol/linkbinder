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
package jp.co.opentone.bsol.linkbinder.view.correspon;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.faces.application.FacesMessage;

import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import jp.co.opentone.bsol.framework.core.config.SystemConfig;
import jp.co.opentone.bsol.framework.core.message.Messages;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.extension.jsf.FacesContextMock;
import jp.co.opentone.bsol.framework.test.AbstractTestCase;
import jp.co.opentone.bsol.framework.web.view.util.ViewHelper;
import jp.co.opentone.bsol.linkbinder.Constants;
import jp.co.opentone.bsol.linkbinder.dto.Attachment;
import jp.co.opentone.bsol.linkbinder.dto.Correspon;
import jp.co.opentone.bsol.linkbinder.dto.FullTextSearchCorresponsResult;
import jp.co.opentone.bsol.linkbinder.dto.FullTextSearchSummaryData;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchFullTextSearchCorresponCondition;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.service.correspon.impl.CorresponFullTextSearchServiceImpl;
import jp.co.opentone.bsol.linkbinder.service.correspon.impl.CorresponServiceImpl;
import jp.co.opentone.bsol.linkbinder.view.AbstractPage;
import mockit.Mock;
import mockit.MockUp;

/**
 * {@link CorresponSearchPage}のテストケース.
 * @author opentone
 */
public class CorresponSearchPageTest extends AbstractTestCase {

    /**
     * テスト対象.
     */
    @Resource
    private CorresponSearchPage page;

    /**
     * 検証用データ
     */
    private List<FullTextSearchCorresponsResult> searchList;

    /**
     * テストの前準備.
     */
    @BeforeClass
    public static void testSetUp() {
        FacesContextMock.initialize();
        new MockAbstractPage();
        new MockCorresponFullTextSearchService();
        new MockSystemConfig();
        new MockViewHelper();
        new MockCorresponService();
    }

    /**
     * 後始末.
     */
    @AfterClass
    public static void testTeardown() {
        new MockAbstractPage().tearDown();
        new MockCorresponFullTextSearchService().tearDown();
        new MockSystemConfig().tearDown();
        new MockViewHelper().tearDown();
        new MockCorresponService().tearDown();
    }

    @Before
    public void setUp() {
        MockSystemConfig.RET_GETNALUE_PAGEROW = null;
        MockSystemConfig.RET_GETNALUE_PAGEINDEX = null;

        MockViewHelper.map.clear();

        page.setKeyword(null);
        page.setPageRowNum(10);
        page.setPageIndex(10);
    }

    @After
    public void tearDown() {

        MockCorresponFullTextSearchService.RET_SEARCH = null;
        MockCorresponService.RET_ATTACHMENT = null;
        MockViewHelper.EX_DOWNLOAD = null;
        MockViewHelper.RET_ACTION = null;
        MockViewHelper.RET_DATA = null;
        MockViewHelper.RET_FILENAME = null;
        MockCorresponService.RET_FIND_EXCEPTION = false;

        MockSystemConfig.RET_GETNALUE_PAGEROW = null;
        MockSystemConfig.RET_GETNALUE_PAGEINDEX = null;
    }

    /**
     * 初期化アクションのテスト.
     * @throws Exception
     */
    @Test
    public void testInitialize() throws Exception {
        MockSystemConfig.RET_GETNALUE_PAGEROW = "5";
        MockSystemConfig.RET_GETNALUE_PAGEINDEX = "4";

        page.initialize();

        assertNull(page.getKeyword());
        assertEquals(0, page.getDataCount());
        assertNull(page.getFullTextSearchCorresponsResultList());
        assertEquals(MockSystemConfig.RET_GETNALUE_PAGEROW, String.valueOf(page.getPageRowNum()));
        assertEquals(MockSystemConfig.RET_GETNALUE_PAGEINDEX, String.valueOf(page.getPageIndex()));
    }

    /**
     * 初期化アクションのテスト.
     * <p>
     * コンフィグファイルに設定無し.
     * </p>
     *
     * @throws Exception
     */
    @Test
    public void testInitializeNotConfig() throws Exception {
        MockSystemConfig.RET_GETNALUE_PAGEROW = null;
        MockSystemConfig.RET_GETNALUE_PAGEINDEX = null;

        page.initialize();

        assertNull(page.getKeyword());
        assertEquals(0, page.getDataCount());
        assertNull(page.getFullTextSearchCorresponsResultList());
        assertEquals(CorresponSearchPage.DEFAULT_PAGE_ROW_NUMBER, page.getPageRowNum());
        assertEquals(CorresponSearchPage.DEFAULT_PAGE_INDEX_NUMBER, page.getPageIndex());
        assertNotNull(page.getCondition());
    }

    /**
     * 初期化アクションのテスト.
     * 初期化時に検索を実行する.
     * @throws Exception
     */
    @Test
    public void testInitializeSearch() throws Exception {
        page.setPageRowNum(CorresponSearchPage.DEFAULT_PAGE_ROW_NUMBER);
        page.setPageIndex(CorresponSearchPage.DEFAULT_PAGE_INDEX_NUMBER);

        // 初期化時に検索実行
        MockCorresponFullTextSearchService.RET_SEARCH = getListMultiData(30);

        SearchFullTextSearchCorresponCondition condition = new SearchFullTextSearchCorresponCondition();
        condition.setKeyword("test");
        condition.setPageNo(2);

        MockViewHelper viewHelper = new MockViewHelper();
        viewHelper.setSessionValue(Constants.KEY_SEARCH_FULL_TEXT_SEARCH_CORRESPON_CONDTION, condition);
        page.setUseSessionPageNo(true);
        page.initialize();

        List<FullTextSearchCorresponsResult> list = page.getFullTextSearchCorresponsResultList();
        assertNotNull(list);
        assertEquals(10L, list.size());
        assertNotNull(page.getCondition());

        condition = page.getCondition();
        assertEquals(30, page.getDataCount());
        assertEquals("test", condition.getKeyword());
        assertEquals(10, condition.getPageRowNum());
        assertEquals(2, condition.getPageNo());
    }

    /**
     * 検索アクションのテスト.
     * <p>
     * ０件取得.
     * </p>
     *
     * @throws Exception
     */
    @Test
    public void testSearchNoData() throws Exception {
        page.initialize();

        MockCorresponFullTextSearchService.RET_SEARCH = null;
        // 期待されるメッセージをセット
        FacesContextMock.EXPECTED_CLIENT_ID = null;
        FacesContextMock.EXPECTED_MESSAGE =
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                 FacesMessage.SEVERITY_ERROR.toString(),
                                 createExpectedMessageString(
                                     Messages.getMessageAsString(ApplicationMessageCode.NO_DATA_FOUND),
                                     "Search"));

        // 検索条件を初期化
        SearchFullTextSearchCorresponCondition condition = new SearchFullTextSearchCorresponCondition();
        page.setCondition(condition);
        page.setActionName("Search");
        String result = page.search();

        assertEquals(1, page.getPageNo());
        assertEquals(0, page.getDataCount());
        assertNull(result);
        assertEquals(CorresponSearchPage.DEFAULT_PAGE_ROW_NUMBER, page.getPageRowNum());
        assertEquals(CorresponSearchPage.DEFAULT_PAGE_INDEX_NUMBER, page.getPageIndex());

    }

    /**
     * 検索アクションのテスト.
     * <p>
     * １件取得.
     * </p>
     *
     * @throws Exception
     */
    @Test
    public void testSearchExistData() throws Exception {
        MockSystemConfig.RET_GETVALUE_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

        setUpSearchExistData();
        MockCorresponFullTextSearchService.RET_SEARCH = getListOneData();

        // 検索条件を初期化
        SearchFullTextSearchCorresponCondition condition = new SearchFullTextSearchCorresponCondition();
        page.setKeyword("test");
        page.setCondition(condition);
        page.search();

        assertEquals(1, page.getPageNo());
        assertEquals(1, page.getDataCount());

        List<FullTextSearchSummaryData> list = new ArrayList<FullTextSearchSummaryData>();
        list = searchList.get(0).getSummaryList();
        for (int i=0; i<list.size(); i++) {
            assertEquals(list.get(i).getValue(),
                page.getFullTextSearchCorresponsResultList().get(0).getSummaryList().get(i).getValue());
            assertEquals(list.get(i).getEscape(),
                page.getFullTextSearchCorresponsResultList().get(0).getSummaryList().get(i).getEscape());
        }
        assertTrue(page.getFullTextSearchCorresponsResultList().get(0).isSummaryViewFlag());
        assertEquals(CorresponSearchPage.DEFAULT_PAGE_ROW_NUMBER, page.getPageRowNum());
        assertEquals(CorresponSearchPage.DEFAULT_PAGE_INDEX_NUMBER, page.getPageIndex());
    }

    /**
     * 検索アクションのテスト.
     * <p>
     * 複数件取得.
     * </p>
     *
     * @throws Exception
     */
    @Test
    public void testSearchExistData2() throws Exception {
        MockSystemConfig.RET_GETVALUE_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

        setUpSearchExistData2();
        MockCorresponFullTextSearchService.RET_SEARCH = getListPluralData();

        // 検索条件を初期化
        SearchFullTextSearchCorresponCondition condition = new SearchFullTextSearchCorresponCondition();
        page.setKeyword("test");
        page.setCondition(condition);
        page.search();

        assertEquals(1, page.getPageNo());
        assertEquals(14, page.getDataCount());
        for (int i=0; i<page.getFullTextSearchCorresponsResultList().size(); i++) {
            List<FullTextSearchSummaryData> list = new ArrayList<FullTextSearchSummaryData>();
            list = searchList.get(i).getSummaryList();
            for (int n = 0; n < list.size(); n++) {
                assertEquals(list.get(n).getValue(), page
                        .getFullTextSearchCorresponsResultList()
                        .get(i)
                        .getSummaryList()
                        .get(n)
                        .getValue());
                assertEquals(list.get(n).getEscape(), page
                        .getFullTextSearchCorresponsResultList()
                        .get(i)
                        .getSummaryList()
                        .get(n)
                        .getEscape());
            }
            if (i>4) {
                assertFalse(page.getFullTextSearchCorresponsResultList().get(i).isSummaryViewFlag());
            } else {
                assertTrue(page.getFullTextSearchCorresponsResultList().get(i).isSummaryViewFlag());
            }

        }
        assertEquals(CorresponSearchPage.DEFAULT_PAGE_ROW_NUMBER, page.getPageRowNum());
        assertEquals(CorresponSearchPage.DEFAULT_PAGE_INDEX_NUMBER, page.getPageIndex());
    }

    /**
     * 検索アクションのテスト.
     * <p>
     * 件数が減った場合にページ番号を補正する.
     * </p>
     *
     * @throws Exception
     */
    @Test
    public void testSearchCorrectionPageNo() throws Exception {
        MockCorresponFullTextSearchService.RET_SEARCH = getListMultiData(100);
        MockAbstractPage.RET_CURRENT_PROJECT_ID = "0-1111-2";

        // 検索条件を初期化
        SearchFullTextSearchCorresponCondition condition = new SearchFullTextSearchCorresponCondition();
        condition.setKeyword("test");
        page.setPageNo(100);
        page.setPageRowNum(10);
        page.setCondition(condition);
        page.changePage();

        assertEquals(10, page.getPageNo());
        assertEquals(10, page.getCondition().getPageNo());
        assertEquals(100, page.getDataCount());
    }

    /**
    *
    */
   private void setUpSearchExistData() {

       searchList = new ArrayList<FullTextSearchCorresponsResult>();

       MockFullTextSearchCorresponsResult temp = new MockFullTextSearchCorresponsResult();
       Map<String, String> attr = new HashMap<String, String>();

       attr.put("ID", "123456789");
       attr.put("ATTACHMENTID", "File1");
       attr.put("CORRESPONTYPE", "11");
       attr.put("PROJECTID", "0-5000-2");
       attr.put("WORKFLOWSTATUS", "5");
       attr.put("MDATE", "28-May-2009 00:00:00");
       attr.put("URI", "DB:LINKBINDER:123456789:File1");
       attr.put("TITLE", "testTitle");
       temp.setAttributeTest(attr);

       List<FullTextSearchSummaryData> summaryList = new ArrayList<FullTextSearchSummaryData>();
       String start = "<span style=\"font-weight: bold;\">";
       String end = "</span>";
       summaryList.add(new FullTextSearchSummaryData("サマリ１", true));
       summaryList.add(new FullTextSearchSummaryData(start, false));
       summaryList.add(new FullTextSearchSummaryData("太字", true));
       summaryList.add(new FullTextSearchSummaryData(end, false));
       summaryList.add(new FullTextSearchSummaryData("細字", true));
       temp.setSummaryList(summaryList);

       searchList.add(temp);
   }

   /**
    *
    */
   private void setUpSearchExistData2() {

       searchList = new ArrayList<FullTextSearchCorresponsResult>();

       MockFullTextSearchCorresponsResult temp = new MockFullTextSearchCorresponsResult();
       Map<String, String> attr = new HashMap<String, String>();

       attr.put("ID", "123456789");
       attr.put("ATTACHMENTID", "File1");
       attr.put("CORRESPONTYPE", "11");
       attr.put("PROJECTID", "0-5000-2");
       attr.put("WORKFLOWSTATUS", "5");
       attr.put("MDATE", "17-Jul-1997 04:20:30");
       attr.put("URI", "DB:LINKBINDER:123456789:File1");
       attr.put("TITLE", "testTitle");
       temp.setAttributeTest(attr);

       List<FullTextSearchSummaryData> summaryList = new ArrayList<FullTextSearchSummaryData>();
       String start = "<span style=\"font-weight: bold;\">";
       String end = "</span>";
       summaryList.add(new FullTextSearchSummaryData("サマリ１", true));
       summaryList.add(new FullTextSearchSummaryData(start, false));
       summaryList.add(new FullTextSearchSummaryData("太字", true));
       summaryList.add(new FullTextSearchSummaryData(end, false));
       summaryList.add(new FullTextSearchSummaryData("細字", true));
       temp.setSummaryList(summaryList);

       searchList.add(temp);

       temp = new MockFullTextSearchCorresponsResult();
       attr = new HashMap<String, String>();
       attr.put("ID", "2");
       attr.put("ATTACHMENTID", null);
       attr.put("CORRESPONTYPE", "11");
       attr.put("PROJECTID", "0-5000-2");
       attr.put("WORKFLOWSTATUS", "5");
       attr.put("MDATE", "17-Jul-1997 04:30:30");
       attr.put("URI", "DB:LINKBINDER:2");
       attr.put("TITLE", "testTitle2");
       attr.put("SUMMARY", null);
       temp.setAttributeTest(attr);
       searchList.add(temp);

       temp = new MockFullTextSearchCorresponsResult();
       attr = new HashMap<String, String>();
       attr.put("ID", "3");
       attr.put("ATTACHMENTID", null);
       attr.put("CORRESPONTYPE", "11");
       attr.put("PROJECTID", "0-5000-2");
       attr.put("WORKFLOWSTATUS", "5");
       attr.put("MDATE", "17-Jul-1997 04:40:30");
       attr.put("URI", "DB:LINKBINDER:3");
       attr.put("TITLE", "testTitle3");
       attr.put("SUMMARY", null);
       temp.setAttributeTest(attr);
       searchList.add(temp);

       temp = new MockFullTextSearchCorresponsResult();
       attr = new HashMap<String, String>();
       attr.put("ID", "4");
       attr.put("ATTACHMENTID", null);
       attr.put("CORRESPONTYPE", "11");
       attr.put("PROJECTID", "0-5000-2");
       attr.put("WORKFLOWSTATUS", "5");
       attr.put("MDATE", "17-Jul-1997 04:40:30");
       attr.put("URI", "DB:LINKBINDER:4");
       attr.put("TITLE", "testTitle4");
       attr.put("SUMMARY", null);
       temp.setAttributeTest(attr);
       searchList.add(temp);

       temp = new MockFullTextSearchCorresponsResult();
       attr = new HashMap<String, String>();
       attr.put("ID", "5");
       attr.put("ATTACHMENTID", null);
       attr.put("CORRESPONTYPE", "11");
       attr.put("PROJECTID", "0-5000-2");
       attr.put("WORKFLOWSTATUS", "5");
       attr.put("MDATE", "17-Jul-1997 04:40:30");
       attr.put("URI", "DB:LINKBINDER:5");
       attr.put("TITLE", "testTitle5");
       attr.put("SUMMARY", null);
       temp.setAttributeTest(attr);
       searchList.add(temp);

       temp = new MockFullTextSearchCorresponsResult();
       attr = new HashMap<String, String>();
       attr.put("ID", "6");
       attr.put("ATTACHMENTID", null);
       attr.put("CORRESPONTYPE", "11");
       attr.put("PROJECTID", "0-5000-2");
       attr.put("WORKFLOWSTATUS", "0");
       attr.put("MDATE", "17-Jul-1997 04:40:30");
       attr.put("URI", "DB:LINKBINDER:6");
       attr.put("TITLE", "testTitle6");
       attr.put("SUMMARY", null);
       temp.setAttributeTest(attr);
       searchList.add(temp);

       temp = new MockFullTextSearchCorresponsResult();
       attr = new HashMap<String, String>();
       attr.put("ID", "7");
       attr.put("ATTACHMENTID", null);
       attr.put("CORRESPONTYPE", "11");
       attr.put("PROJECTID", "0-5000-2");
       attr.put("WORKFLOWSTATUS", "0");
       attr.put("MDATE", "17-Jul-1997 04:40:30");
       attr.put("URI", "DB:LINKBINDER:7");
       attr.put("TITLE", "testTitle7");
       attr.put("SUMMARY", null);
       temp.setAttributeTest(attr);
       searchList.add(temp);

       temp = new MockFullTextSearchCorresponsResult();
       attr = new HashMap<String, String>();
       attr.put("ID", "8");
       attr.put("ATTACHMENTID", null);
       attr.put("CORRESPONTYPE", "11");
       attr.put("PROJECTID", "0-5000-2");
       attr.put("WORKFLOWSTATUS", "0");
       attr.put("MDATE", "17-Jul-1997 04:40:30");
       attr.put("URI", "DB:LINKBINDER:8");
       attr.put("TITLE", "testTitle8");
       attr.put("SUMMARY", null);
       temp.setAttributeTest(attr);
       searchList.add(temp);

       temp = new MockFullTextSearchCorresponsResult();
       attr = new HashMap<String, String>();
       attr.put("ID", "9");
       attr.put("ATTACHMENTID", null);
       attr.put("CORRESPONTYPE", "11");
       attr.put("PROJECTID", "0-5000-2");
       attr.put("WORKFLOWSTATUS", "0");
       attr.put("MDATE", "17-Jul-1997 04:40:30");
       attr.put("URI", "DB:LINKBINDER:9");
       attr.put("TITLE", "testTitle9");
       attr.put("SUMMARY", null);
       temp.setAttributeTest(attr);
       searchList.add(temp);

       temp = new MockFullTextSearchCorresponsResult();
       attr = new HashMap<String, String>();
       attr.put("ID", "10");
       attr.put("ATTACHMENTID", null);
       attr.put("CORRESPONTYPE", "11");
       attr.put("PROJECTID", "0-5000-2");
       attr.put("WORKFLOWSTATUS", "0");
       attr.put("MDATE", "17-Jul-1997 04:40:30");
       attr.put("URI", "DB:LINKBINDER:10");
       attr.put("TITLE", "testTitle10");
       attr.put("SUMMARY", null);
       temp.setAttributeTest(attr);
       searchList.add(temp);

       temp = new MockFullTextSearchCorresponsResult();
       attr = new HashMap<String, String>();
       attr.put("ID", "11");
       attr.put("ATTACHMENTID", null);
       attr.put("CORRESPONTYPE", "11");
       attr.put("PROJECTID", "0-5000-2");
       attr.put("WORKFLOWSTATUS", "5");
       attr.put("MDATE", "17-Jul-1997 04:40:30");
       attr.put("URI", "DB:LINKBINDER:11");
       attr.put("TITLE", "testTitle11");
       attr.put("SUMMARY", null);
       temp.setAttributeTest(attr);
       searchList.add(temp);

       temp = new MockFullTextSearchCorresponsResult();
       attr = new HashMap<String, String>();
       attr.put("ID", "12");
       attr.put("ATTACHMENTID", null);
       attr.put("CORRESPONTYPE", "11");
       attr.put("PROJECTID", "0-5000-2");
       attr.put("WORKFLOWSTATUS", "5");
       attr.put("MDATE", "17-Jul-1997 04:40:30");
       attr.put("URI", "DB:LINKBINDER:12");
       attr.put("TITLE", "testTitle12");
       attr.put("SUMMARY", null);
       temp.setAttributeTest(attr);
       searchList.add(temp);

       temp = new MockFullTextSearchCorresponsResult();
       attr = new HashMap<String, String>();
       attr.put("ID", "13");
       attr.put("ATTACHMENTID", null);
       attr.put("CORRESPONTYPE", "11");
       attr.put("PROJECTID", "0-5000-2");
       attr.put("WORKFLOWSTATUS", "5");
       attr.put("MDATE", "17-Jul-1997 04:40:30");
       attr.put("URI", "DB:LINKBINDER:13");
       attr.put("TITLE", "testTitle13");
       attr.put("SUMMARY", null);
       temp.setAttributeTest(attr);
       searchList.add(temp);

       temp = new MockFullTextSearchCorresponsResult();
       attr = new HashMap<String, String>();
       attr.put("ID", "14");
       attr.put("ATTACHMENTID", null);
       attr.put("CORRESPONTYPE", "11");
       attr.put("PROJECTID", "0-5000-2");
       attr.put("WORKFLOWSTATUS", "5");
       attr.put("MDATE", "17-Jul-1997 04:40:30");
       attr.put("URI", "DB:LINKBINDER:14");
       attr.put("TITLE", "testTitle14");
       attr.put("SUMMARY", null);
       temp.setAttributeTest(attr);
       searchList.add(temp);

   }

   /**
   *
   */
  private void setUpSearchExistData3() {

      searchList = new ArrayList<FullTextSearchCorresponsResult>();

      MockFullTextSearchCorresponsResult temp = new MockFullTextSearchCorresponsResult();
      Map<String, String> attr = new HashMap<String, String>();

      attr.put("ID", "123456789");
      attr.put("ATTACHMENTID", "File1");
      attr.put("CORRESPONTYPE", "11");
      attr.put("PROJECTID", "0-5000-2");
      attr.put("WORKFLOWSTATUS", "5");
      attr.put("MDATE", "17-Jul-1997 04:20:30");
      attr.put("URI", "DB:LINKBINDER:123456789:File1");
      attr.put("TITLE", "testTitle");
      temp.setAttributeTest(attr);

      List<FullTextSearchSummaryData> summaryList = new ArrayList<FullTextSearchSummaryData>();
      String start = "<span style=\"font-weight: bold;\">";
      String end = "</span>";
      summaryList.add(new FullTextSearchSummaryData("サマリ１", true));
      summaryList.add(new FullTextSearchSummaryData(start, false));
      summaryList.add(new FullTextSearchSummaryData("太字", true));
      summaryList.add(new FullTextSearchSummaryData(end, false));
      summaryList.add(new FullTextSearchSummaryData("細字", true));
      temp.setSummaryList(summaryList);
      searchList.add(temp);

      temp = new MockFullTextSearchCorresponsResult();
      attr = new HashMap<String, String>();
      attr.put("ID", "2");
      attr.put("ATTACHMENTID", null);
      attr.put("CORRESPONTYPE", "11");
      attr.put("PROJECTID", "0-5000-2");
      attr.put("WORKFLOWSTATUS", "5");
      attr.put("MDATE", "17-Jul-1997 04:30:30");
      attr.put("URI", "DB:LINKBINDER:2");
      attr.put("TITLE", "testTitle2");
      attr.put("SUMMARY", null);
      temp.setAttributeTest(attr);
      searchList.add(temp);

      temp = new MockFullTextSearchCorresponsResult();
      attr = new HashMap<String, String>();
      attr.put("ID", "3");
      attr.put("ATTACHMENTID", null);
      attr.put("CORRESPONTYPE", "11");
      attr.put("PROJECTID", "0-5000-2");
      attr.put("WORKFLOWSTATUS", "5");
      attr.put("MDATE", "17-Jul-1997 04:40:30");
      attr.put("URI", "DB:LINKBINDER:3");
      attr.put("TITLE", "testTitle3");
      attr.put("SUMMARY", null);
      temp.setAttributeTest(attr);
      searchList.add(temp);

      temp = new MockFullTextSearchCorresponsResult();
      attr = new HashMap<String, String>();
      attr.put("ID", "4");
      attr.put("ATTACHMENTID", null);
      attr.put("CORRESPONTYPE", "11");
      attr.put("PROJECTID", "0-5000-2");
      attr.put("WORKFLOWSTATUS", "5");
      attr.put("MDATE", "17-Jul-1997 04:40:30");
      attr.put("URI", "DB:LINKBINDER:4");
      attr.put("TITLE", "testTitle4");
      attr.put("SUMMARY", null);
      temp.setAttributeTest(attr);
      searchList.add(temp);

      temp = new MockFullTextSearchCorresponsResult();
      attr = new HashMap<String, String>();
      attr.put("ID", "5");
      attr.put("ATTACHMENTID", null);
      attr.put("CORRESPONTYPE", "11");
      attr.put("PROJECTID", "0-5000-2");
      attr.put("WORKFLOWSTATUS", "5");
      attr.put("MDATE", "17-Jul-1997 04:40:30");
      attr.put("URI", "DB:LINKBINDER:5");
      attr.put("TITLE", "testTitle5");
      attr.put("SUMMARY", null);
      temp.setAttributeTest(attr);
      searchList.add(temp);

      temp = new MockFullTextSearchCorresponsResult();
      attr = new HashMap<String, String>();
      attr.put("ID", "6");
      attr.put("ATTACHMENTID", null);
      attr.put("CORRESPONTYPE", "11");
      attr.put("PROJECTID", "0-5000-2");
      attr.put("WORKFLOWSTATUS", "5");
      attr.put("MDATE", "17-Jul-1997 04:40:30");
      attr.put("URI", "DB:LINKBINDER:6");
      attr.put("TITLE", "testTitle6");
      attr.put("SUMMARY", null);
      temp.setAttributeTest(attr);
      searchList.add(temp);

      temp = new MockFullTextSearchCorresponsResult();
      attr = new HashMap<String, String>();
      attr.put("ID", "7");
      attr.put("ATTACHMENTID", null);
      attr.put("CORRESPONTYPE", "11");
      attr.put("PROJECTID", "0-5000-2");
      attr.put("WORKFLOWSTATUS", "5");
      attr.put("MDATE", "17-Jul-1997 04:40:30");
      attr.put("URI", "DB:LINKBINDER:7");
      attr.put("TITLE", "testTitle7");
      attr.put("SUMMARY", null);
      temp.setAttributeTest(attr);
      searchList.add(temp);

      temp = new MockFullTextSearchCorresponsResult();
      attr = new HashMap<String, String>();
      attr.put("ID", "8");
      attr.put("ATTACHMENTID", null);
      attr.put("CORRESPONTYPE", "11");
      attr.put("PROJECTID", "0-5000-2");
      attr.put("WORKFLOWSTATUS", "5");
      attr.put("MDATE", "17-Jul-1997 04:40:30");
      attr.put("URI", "DB:LINKBINDER:8");
      attr.put("TITLE", "testTitle8");
      attr.put("SUMMARY", null);
      temp.setAttributeTest(attr);
      searchList.add(temp);

      temp = new MockFullTextSearchCorresponsResult();
      attr = new HashMap<String, String>();
      attr.put("ID", "9");
      attr.put("ATTACHMENTID", null);
      attr.put("CORRESPONTYPE", "11");
      attr.put("PROJECTID", "0-5000-2");
      attr.put("WORKFLOWSTATUS", "5");
      attr.put("MDATE", "17-Jul-1997 04:40:30");
      attr.put("URI", "DB:LINKBINDER:9");
      attr.put("TITLE", "testTitle9");
      attr.put("SUMMARY", null);
      temp.setAttributeTest(attr);
      searchList.add(temp);

      temp = new MockFullTextSearchCorresponsResult();
      attr = new HashMap<String, String>();
      attr.put("ID", "10");
      attr.put("ATTACHMENTID", null);
      attr.put("CORRESPONTYPE", "11");
      attr.put("PROJECTID", "0-5000-2");
      attr.put("WORKFLOWSTATUS", "5");
      attr.put("MDATE", "17-Jul-1997 04:40:30");
      attr.put("URI", "DB:LINKBINDER:10");
      attr.put("TITLE", "testTitle10");
      attr.put("SUMMARY", null);
      temp.setAttributeTest(attr);
      searchList.add(temp);

      temp = new MockFullTextSearchCorresponsResult();
      attr = new HashMap<String, String>();
      attr.put("ID", "11");
      attr.put("ATTACHMENTID", null);
      attr.put("CORRESPONTYPE", "11");
      attr.put("PROJECTID", "0-5000-2");
      attr.put("WORKFLOWSTATUS", "5");
      attr.put("MDATE", "17-Jul-1997 04:40:30");
      attr.put("URI", "DB:LINKBINDER:11");
      attr.put("TITLE", "testTitle11");
      attr.put("SUMMARY", null);
      temp.setAttributeTest(attr);
      searchList.add(temp);

      temp = new MockFullTextSearchCorresponsResult();
      attr = new HashMap<String, String>();
      attr.put("ID", "12");
      attr.put("ATTACHMENTID", null);
      attr.put("CORRESPONTYPE", "11");
      attr.put("PROJECTID", "0-5000-2");
      attr.put("WORKFLOWSTATUS", "5");
      attr.put("MDATE", "17-Jul-1997 04:40:30");
      attr.put("URI", "DB:LINKBINDER:12");
      attr.put("TITLE", "testTitle12");
      attr.put("SUMMARY", null);
      temp.setAttributeTest(attr);
      searchList.add(temp);

      temp = new MockFullTextSearchCorresponsResult();
      attr = new HashMap<String, String>();
      attr.put("ID", "13");
      attr.put("ATTACHMENTID", null);
      attr.put("CORRESPONTYPE", "11");
      attr.put("PROJECTID", "0-5000-2");
      attr.put("WORKFLOWSTATUS", "5");
      attr.put("MDATE", "17-Jul-1997 04:40:30");
      attr.put("URI", "DB:LINKBINDER:13");
      attr.put("TITLE", "testTitle13");
      attr.put("SUMMARY", null);
      temp.setAttributeTest(attr);
      searchList.add(temp);

      temp = new MockFullTextSearchCorresponsResult();
      attr = new HashMap<String, String>();
      attr.put("ID", "14");
      attr.put("ATTACHMENTID", null);
      attr.put("CORRESPONTYPE", "11");
      attr.put("PROJECTID", "0-5000-2");
      attr.put("WORKFLOWSTATUS", "5");
      attr.put("MDATE", "17-Jul-1997 04:40:30");
      attr.put("URI", "DB:LINKBINDER:14");
      attr.put("TITLE", "testTitle14");
      attr.put("SUMMARY", null);
      temp.setAttributeTest(attr);
      searchList.add(temp);

      temp = new MockFullTextSearchCorresponsResult();
      attr = new HashMap<String, String>();
      attr.put("ID", "223456789");
      attr.put("ATTACHMENTID", "File15");
      attr.put("CORRESPONTYPE", "11");
      attr.put("PROJECTID", "0-5000-2");
      attr.put("WORKFLOWSTATUS", "5");
      attr.put("MDATE", "17-Jul-1997 04:20:30");
      attr.put("URI", "DB:LINKBINDER:223456789:File15");
      attr.put("TITLE", "testTitle15");
      temp.setAttributeTest(attr);

      summaryList = new ArrayList<FullTextSearchSummaryData>();
      summaryList.add(new FullTextSearchSummaryData("サマリ１５", true));
      summaryList.add(new FullTextSearchSummaryData(start, false));
      summaryList.add(new FullTextSearchSummaryData("太字１５", true));
      summaryList.add(new FullTextSearchSummaryData(end, false));
      summaryList.add(new FullTextSearchSummaryData("細字１５", true));
      temp.setSummaryList(summaryList);
      searchList.add(temp);

      temp = new MockFullTextSearchCorresponsResult();
      attr = new HashMap<String, String>();
      attr.put("ID", "16");
      attr.put("ATTACHMENTID", null);
      attr.put("CORRESPONTYPE", "11");
      attr.put("PROJECTID", "0-5000-2");
      attr.put("WORKFLOWSTATUS", "5");
      attr.put("MDATE", "17-Jul-1997 04:30:30");
      attr.put("URI", "DB:LINKBINDER:16");
      attr.put("TITLE", "testTitle16");
      attr.put("SUMMARY", null);
      temp.setAttributeTest(attr);
      searchList.add(temp);

      temp = new MockFullTextSearchCorresponsResult();
      attr = new HashMap<String, String>();
      attr.put("ID", "17");
      attr.put("ATTACHMENTID", null);
      attr.put("CORRESPONTYPE", "11");
      attr.put("PROJECTID", "0-5000-2");
      attr.put("WORKFLOWSTATUS", "5");
      attr.put("MDATE", "17-Jul-1997 04:40:30");
      attr.put("URI", "DB:LINKBINDER:17");
      attr.put("TITLE", "testTitle17");
      attr.put("SUMMARY", null);
      temp.setAttributeTest(attr);
      searchList.add(temp);

      temp = new MockFullTextSearchCorresponsResult();
      attr = new HashMap<String, String>();
      attr.put("ID", "18");
      attr.put("ATTACHMENTID", null);
      attr.put("CORRESPONTYPE", "11");
      attr.put("PROJECTID", "0-5000-2");
      attr.put("WORKFLOWSTATUS", "5");
      attr.put("MDATE", "17-Jul-1997 04:40:30");
      attr.put("URI", "DB:LINKBINDER:18");
      attr.put("TITLE", "testTitle18");
      attr.put("SUMMARY", null);
      temp.setAttributeTest(attr);
      searchList.add(temp);

      temp = new MockFullTextSearchCorresponsResult();
      attr = new HashMap<String, String>();
      attr.put("ID", "19");
      attr.put("ATTACHMENTID", null);
      attr.put("CORRESPONTYPE", "11");
      attr.put("PROJECTID", "0-5000-2");
      attr.put("WORKFLOWSTATUS", "5");
      attr.put("MDATE", "17-Jul-1997 04:40:30");
      attr.put("URI", "DB:LINKBINDER:19");
      attr.put("TITLE", "testTitle19");
      attr.put("SUMMARY", null);
      temp.setAttributeTest(attr);
      searchList.add(temp);

      temp = new MockFullTextSearchCorresponsResult();
      attr = new HashMap<String, String>();
      attr.put("ID", "20");
      attr.put("ATTACHMENTID", null);
      attr.put("CORRESPONTYPE", "11");
      attr.put("PROJECTID", "0-5000-2");
      attr.put("WORKFLOWSTATUS", "5");
      attr.put("MDATE", "17-Jul-1997 04:40:30");
      attr.put("URI", "DB:LINKBINDER:20");
      attr.put("TITLE", "testTitle20");
      attr.put("SUMMARY", null);
      temp.setAttributeTest(attr);
      searchList.add(temp);

      temp = new MockFullTextSearchCorresponsResult();
      attr = new HashMap<String, String>();
      attr.put("ID", "21");
      attr.put("ATTACHMENTID", null);
      attr.put("CORRESPONTYPE", "11");
      attr.put("PROJECTID", "0-5000-2");
      attr.put("WORKFLOWSTATUS", "5");
      attr.put("MDATE", "17-Jul-1997 04:40:30");
      attr.put("URI", "DB:LINKBINDER:21");
      attr.put("TITLE", "testTitle21");
      attr.put("SUMMARY", null);
      temp.setAttributeTest(attr);
      searchList.add(temp);

      temp = new MockFullTextSearchCorresponsResult();
      attr = new HashMap<String, String>();
      attr.put("ID", "22");
      attr.put("ATTACHMENTID", null);
      attr.put("CORRESPONTYPE", "11");
      attr.put("PROJECTID", "0-5000-2");
      attr.put("WORKFLOWSTATUS", "5");
      attr.put("MDATE", "17-Jul-1997 04:40:30");
      attr.put("URI", "DB:LINKBINDER:22");
      attr.put("TITLE", "testTitle22");
      attr.put("SUMMARY", null);
      temp.setAttributeTest(attr);
      searchList.add(temp);

      temp = new MockFullTextSearchCorresponsResult();
      attr = new HashMap<String, String>();
      attr.put("ID", "23");
      attr.put("ATTACHMENTID", null);
      attr.put("CORRESPONTYPE", "11");
      attr.put("PROJECTID", "0-5000-2");
      attr.put("WORKFLOWSTATUS", "5");
      attr.put("MDATE", "17-Jul-1997 04:40:30");
      attr.put("URI", "DB:LINKBINDER:23");
      attr.put("TITLE", "testTitle23");
      attr.put("SUMMARY", null);
      temp.setAttributeTest(attr);
      searchList.add(temp);

      temp = new MockFullTextSearchCorresponsResult();
      attr = new HashMap<String, String>();
      attr.put("ID", "24");
      attr.put("ATTACHMENTID", null);
      attr.put("CORRESPONTYPE", "11");
      attr.put("PROJECTID", "0-5000-2");
      attr.put("WORKFLOWSTATUS", "5");
      attr.put("MDATE", "17-Jul-1997 04:40:30");
      attr.put("URI", "DB:LINKBINDER:24");
      attr.put("TITLE", "testTitle24");
      attr.put("SUMMARY", null);
      temp.setAttributeTest(attr);
      searchList.add(temp);

      temp = new MockFullTextSearchCorresponsResult();
      attr = new HashMap<String, String>();
      attr.put("ID", "25");
      attr.put("ATTACHMENTID", null);
      attr.put("CORRESPONTYPE", "11");
      attr.put("PROJECTID", "0-5000-2");
      attr.put("WORKFLOWSTATUS", "5");
      attr.put("MDATE", "17-Jul-1997 04:40:30");
      attr.put("URI", "DB:LINKBINDER:25");
      attr.put("TITLE", "testTitle25");
      attr.put("SUMMARY", null);
      temp.setAttributeTest(attr);
      searchList.add(temp);

      temp = new MockFullTextSearchCorresponsResult();
      attr = new HashMap<String, String>();
      attr.put("ID", "26");
      attr.put("ATTACHMENTID", null);
      attr.put("CORRESPONTYPE", "11");
      attr.put("PROJECTID", "0-5000-2");
      attr.put("WORKFLOWSTATUS", "5");
      attr.put("MDATE", "17-Jul-1997 04:40:30");
      attr.put("URI", "DB:LINKBINDER:26");
      attr.put("TITLE", "testTitle26");
      attr.put("SUMMARY", null);
      temp.setAttributeTest(attr);
      searchList.add(temp);

      temp = new MockFullTextSearchCorresponsResult();
      attr = new HashMap<String, String>();
      attr.put("ID", "27");
      attr.put("ATTACHMENTID", null);
      attr.put("CORRESPONTYPE", "11");
      attr.put("PROJECTID", "0-5000-2");
      attr.put("WORKFLOWSTATUS", "5");
      attr.put("MDATE", "17-Jul-1997 04:40:30");
      attr.put("URI", "DB:LINKBINDER:27");
      attr.put("TITLE", "testTitle27");
      attr.put("SUMMARY", null);
      temp.setAttributeTest(attr);
      searchList.add(temp);

      temp = new MockFullTextSearchCorresponsResult();
      attr = new HashMap<String, String>();
      attr.put("ID", "28");
      attr.put("ATTACHMENTID", null);
      attr.put("CORRESPONTYPE", "11");
      attr.put("PROJECTID", "0-5000-2");
      attr.put("WORKFLOWSTATUS", "5");
      attr.put("MDATE", "17-Jul-1997 04:40:30");
      attr.put("URI", "DB:LINKBINDER:28");
      attr.put("TITLE", "testTitle28");
      attr.put("SUMMARY", null);
      temp.setAttributeTest(attr);
      searchList.add(temp);

  }

   private List<FullTextSearchCorresponsResult> getListOneData() {
       List<FullTextSearchCorresponsResult> list = new ArrayList<FullTextSearchCorresponsResult>();
       MockFullTextSearchCorresponsResult temp = new MockFullTextSearchCorresponsResult();
       Map<String, String> attr = new HashMap<String, String>();
       List<FullTextSearchSummaryData> summaryList = new ArrayList<FullTextSearchSummaryData>();

       temp.setAttributeTest(attr);

       String start = "<span style=\"font-weight: bold;\">";
       String end = "</span>";
       summaryList.add(new FullTextSearchSummaryData("サマリ１", true));
       summaryList.add(new FullTextSearchSummaryData(start, false));
       summaryList.add(new FullTextSearchSummaryData("太字", true));
       summaryList.add(new FullTextSearchSummaryData(end, false));
       summaryList.add(new FullTextSearchSummaryData("細字", true));
       temp.setSummaryList(summaryList);
       list.add(temp);

       return list;
   }

   private List<FullTextSearchCorresponsResult> getListPluralData() {
       // ダミーの戻り値をセット
       List<FullTextSearchCorresponsResult> list = new ArrayList<FullTextSearchCorresponsResult>();
       MockFullTextSearchCorresponsResult temp = new MockFullTextSearchCorresponsResult();
       Map<String, String> attr = new HashMap<String, String>();
       List<FullTextSearchSummaryData> summaryList = new ArrayList<FullTextSearchSummaryData>();

       temp.setAttributeTest(attr);

       String start = "<span style=\"font-weight: bold;\">";
       String end = "</span>";
       summaryList.add(new FullTextSearchSummaryData("サマリ１", true));
       summaryList.add(new FullTextSearchSummaryData(start, false));
       summaryList.add(new FullTextSearchSummaryData("太字", true));
       summaryList.add(new FullTextSearchSummaryData(end, false));
       summaryList.add(new FullTextSearchSummaryData("細字", true));
       temp.setSummaryList(summaryList);
       list.add(temp);

       temp = new MockFullTextSearchCorresponsResult();
       attr = new HashMap<String, String>();

       temp.setAttributeTest(attr);
       list.add(temp);

       temp = new MockFullTextSearchCorresponsResult();
       attr = new HashMap<String, String>();

       temp.setAttributeTest(attr);
       list.add(temp);

       temp = new MockFullTextSearchCorresponsResult();
       attr = new HashMap<String, String>();

       temp.setAttributeTest(attr);
       list.add(temp);

       temp = new MockFullTextSearchCorresponsResult();
       attr = new HashMap<String, String>();

       temp.setAttributeTest(attr);
       list.add(temp);

       temp = new MockFullTextSearchCorresponsResult();
       attr = new HashMap<String, String>();

       temp.setAttributeTest(attr);
       list.add(temp);

       temp = new MockFullTextSearchCorresponsResult();
       attr = new HashMap<String, String>();

       temp.setAttributeTest(attr);
       list.add(temp);

       temp = new MockFullTextSearchCorresponsResult();
       attr = new HashMap<String, String>();

       temp.setAttributeTest(attr);
       list.add(temp);

       temp = new MockFullTextSearchCorresponsResult();
       attr = new HashMap<String, String>();

       temp.setAttributeTest(attr);
       list.add(temp);

       temp = new MockFullTextSearchCorresponsResult();
       attr = new HashMap<String, String>();

       temp.setAttributeTest(attr);
       list.add(temp);

       temp = new MockFullTextSearchCorresponsResult();
       attr = new HashMap<String, String>();

       temp.setAttributeTest(attr);
       list.add(temp);

       temp = new MockFullTextSearchCorresponsResult();
       attr = new HashMap<String, String>();

       temp.setAttributeTest(attr);
       list.add(temp);

       temp = new MockFullTextSearchCorresponsResult();
       attr = new HashMap<String, String>();

       temp.setAttributeTest(attr);
       list.add(temp);

       temp = new MockFullTextSearchCorresponsResult();
       attr = new HashMap<String, String>();

       temp.setAttributeTest(attr);
       list.add(temp);

       return list;
   }

   private List<FullTextSearchCorresponsResult> getListPluralData2() {
       // ダミーの戻り値をセット
       List<FullTextSearchCorresponsResult> list = new ArrayList<FullTextSearchCorresponsResult>();
       MockFullTextSearchCorresponsResult temp = new MockFullTextSearchCorresponsResult();
       Map<String, String> attr = new HashMap<String, String>();
       List<FullTextSearchSummaryData> summaryList = new ArrayList<FullTextSearchSummaryData>();

       temp.setAttributeTest(attr);

       String start = "<span style=\"font-weight: bold;\">";
       String end = "</span>";
       summaryList.add(new FullTextSearchSummaryData("サマリ１", true));
       summaryList.add(new FullTextSearchSummaryData(start, false));
       summaryList.add(new FullTextSearchSummaryData("太字", true));
       summaryList.add(new FullTextSearchSummaryData(end, false));
       summaryList.add(new FullTextSearchSummaryData("細字", true));
       temp.setSummaryList(summaryList);
       list.add(temp);

       temp = new MockFullTextSearchCorresponsResult();
       attr = new HashMap<String, String>();

       temp.setAttributeTest(attr);
       list.add(temp);

       temp = new MockFullTextSearchCorresponsResult();
       attr = new HashMap<String, String>();

       temp.setAttributeTest(attr);
       list.add(temp);

       temp = new MockFullTextSearchCorresponsResult();
       attr = new HashMap<String, String>();

       temp.setAttributeTest(attr);
       list.add(temp);

       temp = new MockFullTextSearchCorresponsResult();
       attr = new HashMap<String, String>();

       temp.setAttributeTest(attr);
       list.add(temp);

       temp = new MockFullTextSearchCorresponsResult();
       attr = new HashMap<String, String>();

       temp.setAttributeTest(attr);
       list.add(temp);

       temp = new MockFullTextSearchCorresponsResult();
       attr = new HashMap<String, String>();

       temp.setAttributeTest(attr);
       list.add(temp);

       temp = new MockFullTextSearchCorresponsResult();
       attr = new HashMap<String, String>();

       temp.setAttributeTest(attr);
       list.add(temp);

       temp = new MockFullTextSearchCorresponsResult();
       attr = new HashMap<String, String>();

       temp.setAttributeTest(attr);
       list.add(temp);

       temp = new MockFullTextSearchCorresponsResult();
       attr = new HashMap<String, String>();

       temp.setAttributeTest(attr);
       list.add(temp);

       temp = new MockFullTextSearchCorresponsResult();
       attr = new HashMap<String, String>();

       temp.setAttributeTest(attr);
       list.add(temp);

       temp = new MockFullTextSearchCorresponsResult();
       attr = new HashMap<String, String>();

       temp.setAttributeTest(attr);
       list.add(temp);

       temp = new MockFullTextSearchCorresponsResult();
       attr = new HashMap<String, String>();

       temp.setAttributeTest(attr);
       list.add(temp);

       temp = new MockFullTextSearchCorresponsResult();
       attr = new HashMap<String, String>();

       temp.setAttributeTest(attr);
       list.add(temp);

       temp = new MockFullTextSearchCorresponsResult();
       attr = new HashMap<String, String>();
       summaryList = new ArrayList<FullTextSearchSummaryData>();

       temp.setAttributeTest(attr);

       summaryList.add(new FullTextSearchSummaryData("サマリ１５", true));
       summaryList.add(new FullTextSearchSummaryData(start, false));
       summaryList.add(new FullTextSearchSummaryData("太字１５", true));
       summaryList.add(new FullTextSearchSummaryData(end, false));
       summaryList.add(new FullTextSearchSummaryData("細字１５", true));
       temp.setSummaryList(summaryList);
       list.add(temp);

       temp = new MockFullTextSearchCorresponsResult();
       attr = new HashMap<String, String>();

       temp.setAttributeTest(attr);
       list.add(temp);

       temp = new MockFullTextSearchCorresponsResult();
       attr = new HashMap<String, String>();

       temp.setAttributeTest(attr);
       list.add(temp);

       temp = new MockFullTextSearchCorresponsResult();
       attr = new HashMap<String, String>();

       temp.setAttributeTest(attr);
       list.add(temp);

       temp = new MockFullTextSearchCorresponsResult();
       attr = new HashMap<String, String>();

       temp.setAttributeTest(attr);
       list.add(temp);

       temp = new MockFullTextSearchCorresponsResult();
       attr = new HashMap<String, String>();

       temp.setAttributeTest(attr);
       list.add(temp);

       temp = new MockFullTextSearchCorresponsResult();
       attr = new HashMap<String, String>();

       temp.setAttributeTest(attr);
       list.add(temp);

       temp = new MockFullTextSearchCorresponsResult();
       attr = new HashMap<String, String>();

       temp.setAttributeTest(attr);
       list.add(temp);

       temp = new MockFullTextSearchCorresponsResult();
       attr = new HashMap<String, String>();

       temp.setAttributeTest(attr);
       list.add(temp);

       temp = new MockFullTextSearchCorresponsResult();
       attr = new HashMap<String, String>();

       temp.setAttributeTest(attr);
       list.add(temp);

       temp = new MockFullTextSearchCorresponsResult();
       attr = new HashMap<String, String>();

       temp.setAttributeTest(attr);
       list.add(temp);

       temp = new MockFullTextSearchCorresponsResult();
       attr = new HashMap<String, String>();

       temp.setAttributeTest(attr);
       list.add(temp);

       temp = new MockFullTextSearchCorresponsResult();
       attr = new HashMap<String, String>();

       temp.setAttributeTest(attr);
       list.add(temp);

       temp = new MockFullTextSearchCorresponsResult();
       attr = new HashMap<String, String>();

       temp.setAttributeTest(attr);
       list.add(temp);

       return list;
   }

   private List<FullTextSearchCorresponsResult> getListMultiData(int count) {
       List<FullTextSearchCorresponsResult> list = new ArrayList<FullTextSearchCorresponsResult>();
       MockFullTextSearchCorresponsResult temp = null;
       Map<String, String> attr = null;
       List<FullTextSearchSummaryData> summaryList = null;

       for (int index = 0 ; index < count ; index++) {
           temp = new MockFullTextSearchCorresponsResult();
           attr = new HashMap<String, String>();
           summaryList = new ArrayList<FullTextSearchSummaryData>();
           temp.setAttributeTest(attr);

           String start = "<span style=\"font-weight: bold;\">";
           String end = "</span>";
           summaryList.add(new FullTextSearchSummaryData("サマリ１", true));
           summaryList.add(new FullTextSearchSummaryData(start, false));
           summaryList.add(new FullTextSearchSummaryData("太字", true));
           summaryList.add(new FullTextSearchSummaryData(end, false));
           summaryList.add(new FullTextSearchSummaryData("細字", true));
           temp.setSummaryList(summaryList);
           list.add(temp);
       }

       return list;
   }

   /**
    * 添付ファイルダウンロードのテスト.
    * @throws Exception
    */
   @Test
   public void testDownload() throws Exception {
       // 変換後のデータを作成
       byte[] expected = "TEST-OUT-ATTACHMENT".getBytes();
       Attachment attachment = new Attachment();
       attachment.setContent(expected);
       attachment.setFileName("testFileName.txt");
       Correspon correspon = new Correspon();

       MockCorresponService.RET_ATTACHMENT = attachment;
       MockCorresponService.RET_CORRESPON = correspon;

       page.setAttachmentId(new Long(123456789));
       page.setId(new Long(123456789));
       page.download();

       assertEquals("download", MockViewHelper.RET_ACTION);
       assertArrayEquals(expected, MockViewHelper.RET_DATA);
       assertEquals("testFileName.txt", MockViewHelper.RET_FILENAME);

   }

   /**
    * 添付ファイルダウンロードのテスト(失敗).
    * @throws Exception
    */
   @Test
   public void testDownloadNotFileId() throws Exception {
       // 変換後のデータを作成
       byte[] expected = "TEST-OUT-ATTACHMENT".getBytes();
       Attachment attachment = new Attachment();
       attachment.setContent(expected);
       attachment.setFileName("testFileName.txt");
       Correspon correspon = new Correspon();

       MockCorresponService.RET_ATTACHMENT = attachment;
       MockCorresponService.RET_CORRESPON = correspon;

       // 期待されるエラーメッセージを用意
       FacesContextMock.EXPECTED_CLIENT_ID = null;
       FacesContextMock.EXPECTED_MESSAGE =
               new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                FacesMessage.SEVERITY_ERROR.toString(),
                                "パラメータが正しくありません。リクエストは失敗しました。");

       page.download();
   }

   /**
    * 添付ファイルダウンロードのテスト(失敗).
    * @throws Exception
    */
   @Test
   public void testDownloadIOException() throws Exception {
       // 変換後のデータを作成
       byte[] expected = "TEST-OUT-ATTACHMENT".getBytes();
       Attachment attachment = new Attachment();
       attachment.setContent(expected);
       attachment.setFileName("testFileName.txt");
       Correspon correspon = new Correspon();

       MockCorresponService.RET_ATTACHMENT = attachment;
       MockCorresponService.RET_CORRESPON = correspon;

       MockViewHelper.EX_DOWNLOAD = new IOException();

       // 期待されるメッセージをセット
       FacesContextMock.EXPECTED_CLIENT_ID = null;
       FacesContextMock.EXPECTED_MESSAGE =
               new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                FacesMessage.SEVERITY_ERROR.toString(),
                                "ダウンロードに失敗しました。");

       page.setAttachmentId(new Long(123456789));
       page.setId(new Long(123456789));
       page.download();
   }

    /**
     * 前ページ（<<）アクションのテスト.
     * @throws Exception
     */
    @Test
    public void testMovePrevious() throws Exception {
        MockSystemConfig.RET_GETVALUE_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
        // ダミーのコレポン文書を設定
        setUpSearchExistData2();
        MockCorresponFullTextSearchService.RET_SEARCH = getListPluralData();

        // 検索条件をセット(Transferの代わり)
        SearchFullTextSearchCorresponCondition condition = new SearchFullTextSearchCorresponCondition();
        condition.setKeyword("test");
        page.setCondition(condition);

        /* 全30件を10行ずつ表示 -- 全3ページ */
        page.setPageRowNum(10);
        // 3ページ目から1つ戻る
        page.setPageNo(3);
        page.movePrevious();

        assertEquals(2, page.getPageNo());
        assertEquals(14, page.getDataCount());

        List<FullTextSearchCorresponsResult> selectList = new ArrayList<FullTextSearchCorresponsResult>();
        for (int i=10; i<searchList.size(); i++) {
            FullTextSearchCorresponsResult selectResult = new FullTextSearchCorresponsResult();
            selectResult = (FullTextSearchCorresponsResult)searchList.get(i);
            selectList.add(selectResult);
        }

        for (int i=0; i<page.getFullTextSearchCorresponsResultList().size(); i++) {

            List<FullTextSearchSummaryData> list = new ArrayList<FullTextSearchSummaryData>();
            list = selectList.get(i).getSummaryList();
            for (int n = 0; n < list.size(); n++) {
                assertEquals(list.get(n).getValue(), page
                        .getFullTextSearchCorresponsResultList()
                        .get(i)
                        .getSummaryList()
                        .get(n)
                        .getValue());
                assertEquals(list.get(n).getEscape(), page
                        .getFullTextSearchCorresponsResultList()
                        .get(i)
                        .getSummaryList()
                        .get(n)
                        .getEscape());
            }
            assertTrue(page.getFullTextSearchCorresponsResultList().get(i).isSummaryViewFlag());

        }
    }

    /**
     * 次ページ（>>）アクションのテスト.
     * @throws Exception
     */
    @Test
    public void testMoveNext() throws Exception {
        MockSystemConfig.RET_GETVALUE_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
        // ダミーのコレポン文書を設定
        setUpSearchExistData2();
        MockCorresponFullTextSearchService.RET_SEARCH = getListPluralData();

        // 検索条件をセット(Transferの代わり)
        SearchFullTextSearchCorresponCondition condition = new SearchFullTextSearchCorresponCondition();
        condition.setKeyword("test");
        page.setCondition(condition);

        /* 全30件を10行ずつ表示 -- 全3ページ */
        page.setPageRowNum(10);
        // 3ページ目から1つ戻る
        page.setPageNo(1);
        page.moveNext();

        assertEquals(2, page.getPageNo());
        assertEquals(14, page.getDataCount());

        List<FullTextSearchCorresponsResult> selectList = new ArrayList<FullTextSearchCorresponsResult>();
        for (int i=10; i<searchList.size(); i++) {
            FullTextSearchCorresponsResult selectResult = new FullTextSearchCorresponsResult();
            selectResult = (FullTextSearchCorresponsResult)searchList.get(i);
            selectList.add(selectResult);
        }

        for (int i=0; i<page.getFullTextSearchCorresponsResultList().size(); i++) {
            List<FullTextSearchSummaryData> list = new ArrayList<FullTextSearchSummaryData>();
            list = selectList.get(i).getSummaryList();
            if (list == null) {
                assertNull(page.getFullTextSearchCorresponsResultList().get(i).getSummaryList());
            } else {
                for (int n=0; n<list.size(); n++) {
                    assertEquals(list.get(n).getValue(),
                        page.getFullTextSearchCorresponsResultList().get(i).getSummaryList().get(n).getValue());
                    assertEquals(list.get(n).getEscape(),
                        page.getFullTextSearchCorresponsResultList().get(i).getSummaryList().get(n).getEscape());
                }
            }
            assertTrue(page.getFullTextSearchCorresponsResultList().get(i).isSummaryViewFlag());

        }
    }

    /**
     * ページ遷移アクションのテスト.
     * @throws Exception
     */
    @Test
    public void testChangePage() throws Exception {
        MockSystemConfig.RET_GETVALUE_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

        // ダミーのコレポン文書を設定
        setUpSearchExistData3();
        MockCorresponFullTextSearchService.RET_SEARCH = getListPluralData2();

        // 検索条件をセット(Transferの代わり)
        SearchFullTextSearchCorresponCondition condition = new SearchFullTextSearchCorresponCondition();
        condition.setKeyword("test");
        page.setCondition(condition);

        /* 全30件を10行ずつ表示 -- 全3ページ */
        page.setPageRowNum(10);
        page.setPageIndex(10);
        // 3ページ目から1つ戻る
        page.setPageNo(3);
        page.changePage();

        assertEquals(3, page.getPageNo());
        assertEquals(28, page.getDataCount());

        List<FullTextSearchCorresponsResult> selectList = new ArrayList<FullTextSearchCorresponsResult>();
        for (int i=20; i<searchList.size(); i++) {
            FullTextSearchCorresponsResult selectResult = new FullTextSearchCorresponsResult();
            selectResult = (FullTextSearchCorresponsResult)searchList.get(i);
            selectList.add(selectResult);
        }

        for (int i=0; i<page.getFullTextSearchCorresponsResultList().size(); i++) {
            List<FullTextSearchSummaryData> list = new ArrayList<FullTextSearchSummaryData>();
            list = selectList.get(i).getSummaryList();
            if (list == null) {
                assertNull(page.getFullTextSearchCorresponsResultList().get(i).getSummaryList());
            } else {
                for (int n=0; n<list.size(); n++) {
                    assertEquals(list.get(n).getValue(),
                        page.getFullTextSearchCorresponsResultList().get(i).getSummaryList().get(n).getValue());
                    assertEquals(list.get(n).getEscape(),
                        page.getFullTextSearchCorresponsResultList().get(i).getSummaryList().get(n).getEscape());
                }
            }
            assertTrue(page.getFullTextSearchCorresponsResultList().get(i).isSummaryViewFlag());
        }
    }

    /**
     * 画面表示件数のテスト.
     * @throws Exception
     */
    @Test
    public void testPageDisplayNo() throws Exception {

        /* 全23件を10件ずつ表示の時 */
        page.setPageRowNum(10);
        page.setDataCount(23);

        page.setPageNo(1);
        assertEquals("1-10", page.getPageDisplayNo());
        page.setPageNo(2);
        assertEquals("11-20", page.getPageDisplayNo());
        page.setPageNo(3);
        assertEquals("21-23", page.getPageDisplayNo());

        /* 全22件を7件ずつ表示の時 */
        page.setPageRowNum(7);
        page.setDataCount(22);

        page.setPageNo(1);
        assertEquals("1-7", page.getPageDisplayNo());
        page.setPageNo(2);
        assertEquals("8-14", page.getPageDisplayNo());
        page.setPageNo(3);
        assertEquals("15-21", page.getPageDisplayNo());
        page.setPageNo(4);
        assertEquals("22-22", page.getPageDisplayNo());

        /* 全16件を20件ずつ表示の時 */
        page.setPageRowNum(20);
        page.setDataCount(16);

        page.setPageNo(1);
        assertEquals("1-16", page.getPageDisplayNo());
    }

    /**
     * ページリンク表示のテスト.
     * @throws Exception
     */
    @Test
    public void testPagingNo() throws Exception {

        String[] result = null;

        /* 全136件を10件ごと、10ページ分表示の時 */
        page.setPageRowNum(10);
        page.setDataCount(136);
        page.setPageIndex(10);

        // 5ページまでは表示が変わらない
        String[] expected = { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" };
        for (int i = 0; i < 5; i++) {
            page.setPageNo(i + 1);
            result = page.getPagingNo();

            assertEquals(10, result.length);
            for (int j = 0; j < 10; j++) {
                assertEquals(expected[j], result[j]);
            }
        }

        // 6ページからは1ページずつ表示がずれる
        String[] expected6 = { "2", "3", "4", "5", "6", "7", "8", "9", "10", "11" };
        page.setPageNo(6);
        result = page.getPagingNo();

        assertEquals(10, result.length);
        for (int i = 0; i < 10; i++) {
            assertEquals(expected6[i], result[i]);
        }
        String[] expected7 = { "3", "4", "5", "6", "7", "8", "9", "10", "11", "12" };
        page.setPageNo(7);
        result = page.getPagingNo();

        assertEquals(10, result.length);
        for (int i = 0; i < 10; i++) {
            assertEquals(expected7[i], result[i]);
        }
        String[] expected8 = { "4", "5", "6", "7", "8", "9", "10", "11", "12", "13" };
        page.setPageNo(8);
        result = page.getPagingNo();

        assertEquals(10, result.length);
        for (int i = 0; i < 10; i++) {
            assertEquals(expected8[i], result[i]);
        }

        // 9ページからは表示が変わらない
        String[] expectedEnd = { "5", "6", "7", "8", "9", "10", "11", "12", "13", "14" };
        for (int i = 8; i < 14; i++) {
            page.setPageNo(i + 1);
            result = page.getPagingNo();

            assertEquals(10, result.length);
            for (int j = 0; j < 10; j++) {
                assertEquals(expectedEnd[j], result[j]);
            }
        }

        /* 全56件を7件ごと、5ページ分表示の時 */
        page.setPageRowNum(7);
        page.setDataCount(56);
        page.setPageIndex(5);

        // 2ページまでは表示が変わらない
        String[] expectedHalf = { "1", "2", "3", "4", "5" };
        for (int i = 0; i < 3; i++) {
            page.setPageNo(i + 1);
            result = page.getPagingNo();

            assertEquals(5, result.length);
            for (int j = 0; j < 5; j++) {
                assertEquals(expectedHalf[j], result[j]);
            }
        }

        // 3ページからは1ページずつ表示がずれる
        String[] expectedHalf4 = { "2", "3", "4", "5", "6" };
        page.setPageNo(4);
        result = page.getPagingNo();

        assertEquals(5, result.length);
        for (int i = 0; i < 5; i++) {
            assertEquals(expectedHalf4[i], result[i]);
        }
        String[] expectedHalf5 = { "3", "4", "5", "6", "7" };
        page.setPageNo(5);
        result = page.getPagingNo();

        assertEquals(5, result.length);
        for (int i = 0; i < 5; i++) {
            assertEquals(expectedHalf5[i], result[i]);
        }
        // 5ページからは表示が変わらない
        String[] expectedHalfEnd = { "4", "5", "6", "7", "8" };
        for (int i = 5; i < 8; i++) {
            page.setPageNo(i + 1);
            result = page.getPagingNo();

            assertEquals(5, result.length);
            for (int j = 0; j < 5; j++) {
                assertEquals(expectedHalfEnd[j], result[j]);
            }
        }

        /* 全317件を15件ごと、20ページ分表示の時 */
        page.setPageRowNum(15);
        page.setDataCount(317);
        page.setPageIndex(20);

        // 10ページまでは表示が変わらない
        String[] expectedDouble =
                { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15",
                 "16", "17", "18", "19", "20" };
        for (int i = 0; i < 10; i++) {
            page.setPageNo(i + 1);
            result = page.getPagingNo();

            assertEquals(20, result.length);
            for (int j = 0; j < 20; j++) {
                assertEquals(expectedDouble[j], result[j]);
            }
        }

        // 11ページで1ページ表示がずれる
        String[] expectedDouble11 =
                { "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16",
                 "17", "18", "19", "20", "21" };
        page.setPageNo(11);
        result = page.getPagingNo();

        assertEquals(20, result.length);
        for (int i = 0; i < 20; i++) {
            assertEquals(expectedDouble11[i], result[i]);
        }

        // 12ページからは表示が変わらない
        String[] expectedDoubleEnd =
                { "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16",
                 "17", "18", "19", "20", "21", "22" };
        for (int i = 11; i < 22; i++) {
            page.setPageNo(i + 1);
            result = page.getPagingNo();

            assertEquals(20, result.length);
            for (int j = 0; j < 20; j++) {
                assertEquals(expectedDoubleEnd[j], result[j]);
            }
        }

        /* 全1件を1件ごと、1ページ分表示の時 */
        page.setPageRowNum(1);
        page.setDataCount(1);
        page.setPageIndex(1);
        String[] expectedMin = { "1" };

        page.setPageNo(1);
        result = page.getPagingNo();

        assertEquals(1, result.length);
        for (int i = 0; i < 1; i++) {
            assertEquals(expectedMin[i], result[i]);
        }
    }

    /**
     * 前ページ（<<）のリンク表示判定テスト.
     * @throws Exception
     */
    @Test
    public void testPrevious() throws Exception {
        // 1ページ目のみリンク表示しない
        page.setPageNo(1);
        assertFalse(page.isPrevious());

        for (int i = 2; i < 10; i++) {
            page.setPageNo(i);
            assertTrue(page.isPrevious());
        }
    }

    /**
     * 次ページ（>>）のリンク表示判定テスト.
     * @throws Exception
     */
    @Test
    public void testNext() throws Exception {
        /* 全136件を10件ごと、10ページ分表示の時 */
        page.setPageRowNum(10);
        page.setDataCount(136);
        page.setPageIndex(10);

        for (int i = 1; i < 14; i++) {
            page.setPageNo(i);
            assertTrue(page.isNext());
        }
        // 最終ページのみリンク表示しない
        page.setPageNo(14);
        assertFalse(page.isNext());

        /* 全56件を7件ごと、5ページ分表示の時 */
        page.setPageRowNum(7);
        page.setDataCount(56);
        page.setPageIndex(5);

        for (int i = 1; i < 8; i++) {
            page.setPageNo(i);
            assertTrue(page.isNext());
        }
        // 最終ページのみリンク表示しない
        page.setPageNo(8);
        assertFalse(page.isNext());

        /* 全317件を15件ごと、20ページ分表示の時 */
        page.setPageRowNum(15);
        page.setDataCount(317);
        page.setPageIndex(20);

        for (int i = 1; i < 22; i++) {
            page.setPageNo(i);
            assertTrue(page.isNext());
        }
        // 最終ページのみリンク表示しない
        page.setPageNo(22);
        assertFalse(page.isNext());
    }

    /**
     * コレポン文書表示画面へ遷移(表示可能状態).
     * @throws Exception
     */
    @Test
    public void testShow1() throws Exception {
        MockCorresponService.RET_FIND_EXCEPTION = false;
        MockAbstractPage.RET_CURRENT_PROJECT_ID = "0-1111-2";
        page.setId(1L);

        String nextPage = page.show();

        assertEquals(nextPage,
            String.format("correspon?id=%s&projectId=0-1111-2&backPage=corresponSearch&fromIndex=1", page.getId()));
    }

    /**
     * コレポン文書表示画面へ遷移(表示不可状態).
     * @throws Exception
     */
    @Test
    public void testShow2() throws Exception {
        MockCorresponService.RET_FIND_EXCEPTION = true;
        MockAbstractPage.RET_CURRENT_PROJECT_ID = "0-1111-2";

        // 期待されるメッセージをセット
        FacesContextMock.EXPECTED_CLIENT_ID = null;
        FacesContextMock.EXPECTED_MESSAGE =
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                 FacesMessage.SEVERITY_ERROR.toString(),
                                 createExpectedMessageString(
                                     Messages.getMessageAsString(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_INVISIBLE_CORRESPON),
                                     "Show"));
        page.setActionName("Show");
        String nextPage = null;
             nextPage = page.show();
        assertNull(nextPage);
        /*
        try {
             nextPage = page.show();
            fail("例外が発生していない");
        } catch (InvalidOperationRuntimeException e) {
            ServiceAbortException actual = (ServiceAbortException)e.getCause();
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT, actual.getMessageCode());
        }
        assertNull(nextPage);
        */
    }

    public static class MockAbstractPage extends MockUp<AbstractPage> {
        static String RET_CURRENT_PROJECT_ID;
        @Mock
        public String getCurrentProjectId() {
            return RET_CURRENT_PROJECT_ID;
        }
    }

    public static class MockCorresponFullTextSearchService extends MockUp<CorresponFullTextSearchServiceImpl> {
        static List<FullTextSearchCorresponsResult> RET_SEARCH;
        static byte[] RET_DOWNLOAD;

        @Mock
        public List<FullTextSearchCorresponsResult> search(
            SearchFullTextSearchCorresponCondition condition)
        throws ServiceAbortException {
            if (RET_SEARCH == null) {
                throw new ServiceAbortException(ApplicationMessageCode.NO_DATA_FOUND);
            }
            return RET_SEARCH;
        }
    }

    public static class MockSystemConfig extends MockUp<SystemConfig> {
        static String RET_GETNALUE_PAGEROW;
        static String RET_GETNALUE_PAGEINDEX;
        static String RET_GETVALUE_DATE_FORMAT;

        @Mock
        public String getValue(String key) {

            if (StringUtils.equals("corresponsearch.pagerow", key)) {
                return RET_GETNALUE_PAGEROW;
            } else if (StringUtils.equals("corresponsearch.pageindex", key)) {
                return RET_GETNALUE_PAGEINDEX;
            } else if (StringUtils.equals("date.format", key)) {
                return RET_GETVALUE_DATE_FORMAT;
            } else if (StringUtils.equals("errors.invalid.operation", key)) {
                return "E016,E904";
            }

            return null;
        }
    }

    public static class MockViewHelper extends MockUp<ViewHelper> {
        static String RET_ACTION;
        static byte[] RET_DATA;
        static String RET_FILENAME;
        static IOException EX_DOWNLOAD;
        static Map<String, Object> map = new HashMap<String, Object>();

        @Mock
        public void download(String fileName, byte[] content) throws IOException {
            download(fileName, content, false);
        }

        @Mock
        public void download(String fileName, byte[] content, boolean downloadByRealFileName) throws IOException {
            if (EX_DOWNLOAD != null) {
                throw EX_DOWNLOAD;
            }
            RET_ACTION = "download";
            RET_DATA = content;
            RET_FILENAME = fileName;
            if (!downloadByRealFileName && fileName.endsWith(".xls")) {
                RET_FILENAME = RET_FILENAME.replaceAll("\\.xls$", ".xml");
            }
        }

        @Mock
        @SuppressWarnings("unchecked")
        public <T> T getSessionValue(String key) {
            return (T) map.get(key);
        }

        @Mock
        public void setSessionValue(String key, Object value) {
            map.put(key, value);
        }

        @Mock
        public void removeSessionValue(String key) {
            map.remove(key);
        }
    }

    public static class MockCorresponService extends MockUp<CorresponServiceImpl> {
        static Attachment RET_ATTACHMENT;
        static Correspon RET_CORRESPON;
        static boolean RET_FIND_EXCEPTION;

        @Mock
        public Attachment findAttachment(Long id, Long fileId) throws ServiceAbortException {
            return RET_ATTACHMENT;
        }

        @Mock
        public Correspon find(Long id) throws ServiceAbortException {
            if (RET_FIND_EXCEPTION) {
                throw new ServiceAbortException(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT);
            }
            return RET_CORRESPON;
        }
    }

    public static class MockFullTextSearchCorresponsResult extends FullTextSearchCorresponsResult {
        private static final long serialVersionUID = 1L;

        public void setAttributeTest(Map<String, String> attrs) {
        }
    }
}
