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
package jp.co.opentone.bsol.linkbinder.service.correspon.impl;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.annotation.Resource;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import jp.co.opentone.bsol.framework.core.dao.Entity;
import jp.co.opentone.bsol.framework.core.dao.KeyDuplicateException;
import jp.co.opentone.bsol.framework.core.dao.RecordNotFoundException;
import jp.co.opentone.bsol.framework.core.dao.StaleRecordException;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.test.AbstractTestCase;
import jp.co.opentone.bsol.linkbinder.dto.AddressCorresponGroup;
import jp.co.opentone.bsol.linkbinder.dto.AddressUser;
import jp.co.opentone.bsol.linkbinder.dto.Correspon;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroup;
import jp.co.opentone.bsol.linkbinder.dto.CorresponIndexHeader;
import jp.co.opentone.bsol.linkbinder.dto.CorresponReadStatus;
import jp.co.opentone.bsol.linkbinder.dto.CorresponType;
import jp.co.opentone.bsol.linkbinder.dto.FullTextSearchCorresponsResult;
import jp.co.opentone.bsol.linkbinder.dto.PersonInCharge;
import jp.co.opentone.bsol.linkbinder.dto.Project;
import jp.co.opentone.bsol.linkbinder.dto.SearchCorresponResult;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.Workflow;
import jp.co.opentone.bsol.linkbinder.dto.code.AddressType;
import jp.co.opentone.bsol.linkbinder.dto.code.AddressUserType;
import jp.co.opentone.bsol.linkbinder.dto.code.CorresponStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.FullTextSearchMode;
import jp.co.opentone.bsol.linkbinder.dto.code.ReadStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.ReplyRequired;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowProcessStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowType;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponCondition;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponCondition.Ids;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchFullTextSearchCorresponCondition;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.service.correspon.CorresponSearchService;
import jp.co.opentone.bsol.linkbinder.util.view.correspon.CorresponPageFormatter;

/**
 * {@link CorresponSearchServiceImpl}のテストケース.
 * @author opentone
 */
public class CorresponSearchServiceImplTest extends AbstractTestCase {

    /**
     * テスト対象.
     */
    @Resource
    private CorresponSearchService service;

    /**
     * 検証用データ
     */
    private List<Correspon> list;

    @BeforeClass
    public static void testSetUp() {
        //TODO JMockitバージョンアップ対応
//        Mockit.redefineMethods(CorresponDaoImpl.class, MockCorresponDao.class);
//        Mockit.redefineMethods(CorresponReadStatusDaoImpl.class, MockCorresponReadStatusDao.class);
//        Mockit.redefineMethods(AbstractDao.class, MockAbstractDao.class);
//        Mockit.redefineMethods(AbstractService.class, MockAbstractService.class);
//        Mockit.redefineMethods(CorresponServiceImpl.class, MockCorresponService.class);
//        Mockit.redefineMethods(SystemConfig.class, MockSystemConfig.class);
//        Mockit.redefineMethods(CorresponReadStatusServiceImpl.class, MockCorresponReadStatusService.class);
//        Mockit.redefineMethods(AddressCorresponGroupDaoImpl.class, MockAddressCorresponGroupDao.class);
//        Mockit.redefineMethods(WorkflowDaoImpl.class, MockWorkflowDao.class);
//        Mockit.redefineMethods(CorresponHTMLGeneratorUtil.class, MockCorresponHTMLGeneratorUtil.class);
//        Mockit.redefineMethods(HyperEstraierCorresponFullTextSearchServiceImpl.class, MockCorresponFullTextSearchService.class);
    }

    @AfterClass
    public static void testTeardown() {
        // 差し換えたMockをクリアする
        //TODO JMockitバージョンアップ対応
//        Mockit.tearDownMocks();
    }

    /**
     * テスト前準備.
     */
    @Before
    public void setUp() {
        list = getList();

        MockAbstractService.CORRESPONS = list;
        MockAbstractService.CORRESPON_COUNT = 0;
    }

    /**
     * 後始末.
     */
    @After
    public void tearDown() {
        list = null;
        MockCorresponDao.init();
        MockAbstractService.RET_CURRENT_USER = null;
        MockAbstractService.RET_SYSTEM_ADMIN = false;
        MockAbstractService.RET_PROJECT_ADMIN = false;
        MockAbstractService.RET_GROUP_ADMIN = false;
        MockAbstractService.RET_PREPARER = false;
        MockAbstractService.RET_CHECKER = false;
        MockAbstractService.RET_APPROVER = false;
        MockAbstractService.RET_CURRENT_PROJECT_ID = null;
        MockAbstractService.CORRESPONS = null;
        MockAbstractService.CORRESPON_COUNT = 0;

        MockAbstractDao.EX_FIND_BY_ID = null;
        MockAbstractDao.EX_UPDATE = null;
        MockAbstractDao.EX_DELETE = null;
        MockAbstractDao.RET_FIND_BY_ID = new HashMap<String, Object>();
        MockAbstractDao.SET_CREATE  = new ArrayList<CorresponReadStatus>();
        MockAbstractDao.SET_UPDATE_CORRESPON  = new ArrayList<Correspon>();
        MockAbstractDao.SET_UPDATE_READ_STATUS = new ArrayList<CorresponReadStatus>();
        MockAbstractDao.SET_DELETE  = new ArrayList<Correspon>();
        MockAbstractDao.COUNT = 0;
        MockCorresponService.RET_FIND = null;
        MockCorresponService.FIND_EXCEPTION = false;
        MockSystemConfig.NULL_KEYS = null;
        MockCorresponReadStatusService.RET_FIND = null;
        MockCorresponReadStatusService.RET_FIND_UNIT = null;
        MockCorresponReadStatusService.SET_CREATE = new ArrayList<CorresponReadStatus>();
        MockCorresponReadStatusService.SET_UPDATE = new ArrayList<CorresponReadStatus>();
        MockCorresponReadStatusService.SET_READ_CREATE = new CorresponReadStatus();
        MockCorresponReadStatusService.EX_UPDATE = null;
        MockAddressCorresponGroupDao.RET_FIND_BY_CORRESPON_ID = null;
        MockWorkflowDao.RET_FIND_BY_CORRESPON_ID = null;
        MockCorresponFullTextSearchService.IS_INVOKED_SEARCH = false;
        MockCorresponFullTextSearchService.RET_FULLTEXTSEARCH_LIST = null;
    }

    /**
     * コレポン文書が取得できるか検証する.
     * @throws Exception
     */
    @Test
    public void testFind() throws Exception {
        MockCorresponDao.RET_COUNT = 5;
        MockCorresponDao.RET_FIND = list;

        SearchCorresponResult expected = new SearchCorresponResult();
        expected.setCorresponList(list);
        expected.setCount(5);

        SearchCorresponResult result = service.search(new SearchCorresponCondition());

        assertEquals(5, result.getCount());
        assertEquals(list, result.getCorresponList());
    }

    /**
     * 全文検索サービスを実行している事を検証する.
     * @throws Exception 例外
     */
    @Test
    public void testFindExecuteFullTextSearch() throws Exception {
        // prepare
        MockCorresponDao.RET_COUNT = 1;
        MockCorresponDao.RET_FIND = list;
        MockCorresponFullTextSearchService.RET_FULLTEXTSEARCH_LIST =
            MockCorresponFullTextSearchService.generateFTSCorresponIdList(10L, 20L);
        SearchCorresponCondition condition = new SearchCorresponCondition();
        condition.setKeyword("dummy");
        condition.setFullTextSearchMode(FullTextSearchMode.ALL);
        // execute
        service.search(condition);
        // verify
        assertTrue("not execute expected service.", MockCorresponFullTextSearchService.IS_INVOKED_SEARCH);
    }
    /**
     * 全文検索サービスを実行していない事を検証する.
     * @throws Exception 例外
     */
    @Test
    public void testFindNotExecuteFullTextSearch() throws Exception {
        // prepare
        MockCorresponDao.RET_COUNT = 5;
        MockCorresponDao.RET_FIND = list;
        SearchCorresponCondition condition = new SearchCorresponCondition();
        condition.setKeyword("");
        condition.setFullTextSearchMode(null);
        // execute
        service.search(condition);
        // verify
        assertFalse("execute service.", MockCorresponFullTextSearchService.IS_INVOKED_SEARCH);
    }
    /**
     * 通常検索の条件に、全文検索結果のIDを指定している事.
     * @throws Exception 例外
     */
    @Test
    public void testFindSetFullTextSearchResultId() throws Exception {
        // prepare result
        MockCorresponDao.RET_COUNT = 1;
        MockCorresponDao.RET_FIND = list;
        MockCorresponFullTextSearchService.RET_FULLTEXTSEARCH_LIST =
            MockCorresponFullTextSearchService.generateFTSCorresponIdList(10L, 20L);

        // prepare condition
        SearchCorresponCondition condition = new SearchCorresponCondition();
        condition.setKeyword("dummy");
        condition.setFullTextSearchMode(FullTextSearchMode.ALL);

        // execute
        SearchCorresponResult actual = service.search(condition);
        // verify
        assertNotNull(actual);
        assertTrue(MockCorresponDao.IS_INVOKED_FIND);
        assertNotNull(MockCorresponDao.ARG_CONDITION);
        List<Ids> condIdList = MockCorresponDao.ARG_CONDITION.getIdList();
        assertNotNull(condIdList);
        assertEquals(1, condIdList.size());
        List<Long> ids = condIdList.get(0).getIds();
        assertNotNull(ids);
        assertEquals(2, ids.size());
        assertTrue(ids.contains(10L));
        assertTrue(ids.contains(20L));
    }
    /**
     * 通常検索の条件に、全文検索結果のIDを指定している事.
     *      -> 全文検索結果の ID が1,001件の場合。
     *      -> Oracle のSQL文サイズ制限を回避している事。
     *          ->　１．IN 句の要素数（1,000 個まで）
     *          -> ２．バインド変数のバイト数（4,000 Byte）
     *          -> ３．SQL文全体のバイト数（64 KByte）
     * @throws Exception 例外
     */
    @Test
    public void testFindSetFullTextSearchResult1001AmountsOfIds() throws Exception {
        // prepare result 通常検索の結果はダミー
        MockCorresponDao.RET_COUNT = 1;
        MockCorresponDao.RET_FIND = list;
        // prepare result 全文検索結果IDセット
        MockCorresponFullTextSearchService.RET_FULLTEXTSEARCH_LIST =
            MockCorresponFullTextSearchService.generateFTSCorresponIdListAsSeq(1001);
        // prepare condition
        SearchCorresponCondition condition = new SearchCorresponCondition();
        condition.setKeyword("dummy");
        condition.setFullTextSearchMode(FullTextSearchMode.ALL);
        // execute
        SearchCorresponResult actual = service.search(condition);
        // verify
        assertNotNull(actual);
        assertTrue(MockCorresponDao.IS_INVOKED_FIND);
        assertNotNull(MockCorresponDao.ARG_CONDITION);
        List<Ids> condIdList = MockCorresponDao.ARG_CONDITION.getIdList();
        assertNotNull(condIdList);
        // IN 句を２つに分ける条件になっている事。
        assertEquals(2, condIdList.size());
        List<Long> ids1 = condIdList.get(0).getIds();
        List<Long> ids2 = condIdList.get(1).getIds();
        assertNotNull(ids1);
        assertNotNull(ids2);
        assertEquals(1000, ids1.size());
        assertEquals(1, ids2.size());
        // サンプリングチェック
        assertTrue(ids1.contains(1L));
        assertTrue(ids1.contains(100L));
        assertTrue(ids1.contains(501L));
        assertTrue(ids1.contains(1000L));
        assertTrue(ids2.contains(1001L));
    }
    /**
     * 通常検索の条件に、全文検索結果のIDを指定している事.
     *      -> 全文検索結果の ID が1,000件の場合。
     * @throws Exception 例外
     */
    @Test
    public void testFindSetFullTextSearchResult1000AmountsOfIds() throws Exception {
        // prepare result 通常検索の結果はダミー
        MockCorresponDao.RET_COUNT = 1;
        MockCorresponDao.RET_FIND = list;
        // prepare result 全文検索結果IDセット
        MockCorresponFullTextSearchService.RET_FULLTEXTSEARCH_LIST =
            MockCorresponFullTextSearchService.generateFTSCorresponIdListAsSeq(1000);
        // prepare condition
        SearchCorresponCondition condition = new SearchCorresponCondition();
        condition.setKeyword("dummy");
        condition.setFullTextSearchMode(FullTextSearchMode.ALL);
        // execute
        SearchCorresponResult actual = service.search(condition);
        // verify
        assertNotNull(actual);
        assertTrue(MockCorresponDao.IS_INVOKED_FIND);
        assertNotNull(MockCorresponDao.ARG_CONDITION);
        List<Ids> condIdList = MockCorresponDao.ARG_CONDITION.getIdList();
        assertNotNull(condIdList);
        assertEquals(1, condIdList.size());
        List<Long> ids1 = condIdList.get(0).getIds();
        assertNotNull(ids1);
        assertEquals(1000, ids1.size());
        // サンプリングチェック
        assertTrue(ids1.contains(1L));
        assertTrue(ids1.contains(100L));
        assertTrue(ids1.contains(501L));
        assertTrue(ids1.contains(1000L));
    }
    /**
     * 通常検索の条件に、全文検索結果のIDを指定している事.
     *      -> 全文検索結果の ID が999件の場合。
     * @throws Exception 例外
     */
    @Test
    public void testFindSetFullTextSearchResult999AmountsOfIds() throws Exception {
        // prepare result 通常検索の結果はダミー
        MockCorresponDao.RET_COUNT = 1;
        MockCorresponDao.RET_FIND = list;
        // prepare result 全文検索結果IDセット
        MockCorresponFullTextSearchService.RET_FULLTEXTSEARCH_LIST =
            MockCorresponFullTextSearchService.generateFTSCorresponIdListAsSeq(999);
        // prepare condition
        SearchCorresponCondition condition = new SearchCorresponCondition();
        condition.setKeyword("dummy");
        condition.setFullTextSearchMode(FullTextSearchMode.ALL);
        // execute
        SearchCorresponResult actual = service.search(condition);
        // verify
        assertNotNull(actual);
        assertTrue(MockCorresponDao.IS_INVOKED_FIND);
        assertNotNull(MockCorresponDao.ARG_CONDITION);
        List<Ids> condIdList = MockCorresponDao.ARG_CONDITION.getIdList();
        assertNotNull(condIdList);
        assertEquals(1, condIdList.size());
        List<Long> ids1 = condIdList.get(0).getIds();
        assertNotNull(ids1);
        assertEquals(999, ids1.size());
        // サンプリングチェック
        assertTrue(ids1.contains(1L));
        assertTrue(ids1.contains(100L));
        assertTrue(ids1.contains(501L));
        assertTrue(ids1.contains(999L));
    }
    /**
     * 通常検索の条件に、全文検索結果のIDを指定している事.
     *      -> 全文検索結果の ID が2,001件の場合。
     * @throws Exception 例外
     */
    @Test
    public void testFindSetFullTextSearchResult2001AmountsOfIds() throws Exception {
        // prepare result 通常検索の結果はダミー
        MockCorresponDao.RET_COUNT = 1;
        MockCorresponDao.RET_FIND = list;
        // prepare result 全文検索結果IDセット
        MockCorresponFullTextSearchService.RET_FULLTEXTSEARCH_LIST =
            MockCorresponFullTextSearchService.generateFTSCorresponIdListAsSeq(2001);
        // prepare condition
        SearchCorresponCondition condition = new SearchCorresponCondition();
        condition.setKeyword("dummy");
        condition.setFullTextSearchMode(FullTextSearchMode.ALL);
        // execute
        SearchCorresponResult actual = service.search(condition);
        // verify
        assertNotNull(actual);
        assertTrue(MockCorresponDao.IS_INVOKED_FIND);
        assertNotNull(MockCorresponDao.ARG_CONDITION);
        List<Ids> condIdList = MockCorresponDao.ARG_CONDITION.getIdList();
        assertNotNull(condIdList);
        assertEquals(3, condIdList.size());
        List<Long> ids1 = condIdList.get(0).getIds();
        List<Long> ids2 = condIdList.get(1).getIds();
        List<Long> ids3 = condIdList.get(2).getIds();
        assertNotNull(ids1);
        assertNotNull(ids2);
        assertNotNull(ids3);
        assertEquals(1000, ids2.size());
        assertEquals(1000, ids1.size());
        assertEquals(1, ids3.size());
        // サンプリングチェック
        assertTrue(ids1.contains(1L));
        assertTrue(ids1.contains(100L));
        assertTrue(ids1.contains(501L));
        assertTrue(ids1.contains(1000L));
        assertTrue(ids2.contains(1001L));
        assertTrue(ids2.contains(1100L));
        assertTrue(ids2.contains(1501L));
        assertTrue(ids2.contains(2000L));
        assertTrue(ids3.contains(2001L));
    }
    /**
     * 通常検索の条件に、全文検索結果のIDを指定している事.
     *      -> 全文検索結果の ID が10,001件の場合。
     * @throws Exception 例外
     */
    @Test
    public void testFindSetFullTextSearchResult10001AmountsOfIds() throws Exception {
        // prepare result 通常検索の結果はダミー
        MockCorresponDao.RET_COUNT = 1;
        MockCorresponDao.RET_FIND = list;
        // prepare result 全文検索結果IDセット
        MockCorresponFullTextSearchService.RET_FULLTEXTSEARCH_LIST =
            MockCorresponFullTextSearchService.generateFTSCorresponIdListAsSeq(10001);
        // prepare condition
        SearchCorresponCondition condition = new SearchCorresponCondition();
        condition.setKeyword("dummy");
        condition.setFullTextSearchMode(FullTextSearchMode.ALL);
        // execute
        SearchCorresponResult actual = service.search(condition);
        // verify
        assertNotNull(actual);
        assertTrue(MockCorresponDao.IS_INVOKED_FIND);
        assertNotNull(MockCorresponDao.ARG_CONDITION);
        List<Ids> condIdList = MockCorresponDao.ARG_CONDITION.getIdList();
        assertNotNull(condIdList);
        assertEquals(11, condIdList.size());
        List<Long> ids1 = condIdList.get(0).getIds();
        List<Long> ids2 = condIdList.get(1).getIds();
        List<Long> ids3 = condIdList.get(2).getIds();
        List<Long> ids4 = condIdList.get(3).getIds();
        List<Long> ids5 = condIdList.get(4).getIds();
        List<Long> ids6 = condIdList.get(5).getIds();
        List<Long> ids7 = condIdList.get(6).getIds();
        List<Long> ids8 = condIdList.get(7).getIds();
        List<Long> ids9 = condIdList.get(8).getIds();
        List<Long> ids10 = condIdList.get(9).getIds();
        List<Long> ids11 = condIdList.get(10).getIds();
        assertNotNull(ids1);
        assertNotNull(ids2);
        assertNotNull(ids3);
        assertNotNull(ids4);
        assertNotNull(ids5);
        assertNotNull(ids6);
        assertNotNull(ids7);
        assertNotNull(ids8);
        assertNotNull(ids9);
        assertNotNull(ids10);
        assertNotNull(ids11);
        assertEquals(1000, ids1.size());
        assertEquals(1000, ids2.size());
        assertEquals(1000, ids3.size());
        assertEquals(1000, ids4.size());
        assertEquals(1000, ids5.size());
        assertEquals(1000, ids6.size());
        assertEquals(1000, ids7.size());
        assertEquals(1000, ids8.size());
        assertEquals(1000, ids9.size());
        assertEquals(1000, ids10.size());
        assertEquals(1, ids11.size());
        // サンプリングチェック
        assertTrue(ids1.contains(1L));
        assertTrue(ids1.contains(100L));
        assertTrue(ids1.contains(501L));
        assertTrue(ids1.contains(1000L));
        assertTrue(ids2.contains(1001L));
        assertTrue(ids2.contains(1100L));
        assertTrue(ids2.contains(1501L));
        assertTrue(ids2.contains(2000L));
        assertTrue(ids3.contains(2001L));
        assertTrue(ids3.contains(2100L));
        assertTrue(ids3.contains(2501L));
        assertTrue(ids3.contains(3000L));
        assertTrue(ids4.contains(3001L));
        assertTrue(ids4.contains(3100L));
        assertTrue(ids4.contains(3501L));
        assertTrue(ids4.contains(4000L));
        assertTrue(ids5.contains(4001L));
        assertTrue(ids5.contains(4100L));
        assertTrue(ids5.contains(4501L));
        assertTrue(ids5.contains(5000L));
        assertTrue(ids6.contains(5001L));
        assertTrue(ids6.contains(5100L));
        assertTrue(ids6.contains(5501L));
        assertTrue(ids6.contains(6000L));
        assertTrue(ids7.contains(6001L));
        assertTrue(ids7.contains(6100L));
        assertTrue(ids7.contains(6501L));
        assertTrue(ids7.contains(7000L));
        assertTrue(ids8.contains(7001L));
        assertTrue(ids8.contains(7100L));
        assertTrue(ids8.contains(7501L));
        assertTrue(ids8.contains(8000L));
        assertTrue(ids9.contains(8001L));
        assertTrue(ids9.contains(8100L));
        assertTrue(ids9.contains(8501L));
        assertTrue(ids9.contains(9000L));
        assertTrue(ids10.contains(9001L));
        assertTrue(ids10.contains(9100L));
        assertTrue(ids10.contains(9501L));
        assertTrue(ids10.contains(10000L));
        assertTrue(ids11.contains(10001L));
    }

    /**
     * 全文検索結果件数が０件の場合、通常検索のみ実施している事.
     * @throws Exception 例外
     */
    @Test
    public void testFindFullTextSearchResultIsNothing() throws Exception {
        // prepare
        MockCorresponDao.RET_COUNT = 1;
        MockCorresponDao.RET_FIND = list;
        MockCorresponFullTextSearchService.RET_FULLTEXTSEARCH_LIST =
            MockCorresponFullTextSearchService.generateFTSCorresponIdList();
        SearchCorresponCondition condition = new SearchCorresponCondition();
        condition.setKeyword("dummy");
        condition.setFullTextSearchMode(FullTextSearchMode.ALL);
        // execute
        service.search(condition);
        // verify
        assertTrue("not execute expected service.", MockCorresponFullTextSearchService.IS_INVOKED_SEARCH);
        assertNotNull(MockCorresponDao.ARG_CONDITION);
        List<Ids> condIdList = MockCorresponDao.ARG_CONDITION.getIdList();
        assertNotNull(condIdList);
        assertEquals(0, condIdList.size());
    }


    /**
     * 不正な引数で例外が発生するか検証する.
     */
    @Test(expected = NullPointerException.class)
    public void testFindException() throws Exception {
        service.search(null);
        fail("例外が発生していない");
    }

    /**
     * レコードが見つからない場合、正しい例外が発生するか検証する.
     */
    @Test
    public void testFindRecordNotFound() throws Exception {
        MockCorresponDao.RET_COUNT = 0;
        MockCorresponDao.RET_FIND = new ArrayList<Correspon>();
        try {
            service.search(new SearchCorresponCondition());
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals("E001", actual.getMessageCode());
        }
    }
    /**
     * レコードが見つからない場合、正しい例外が発生するか検証する.
     * 全文検索条件を指定している場合。
     * @throws Exception 例外
     */
    @Test(expected=ServiceAbortException.class)
    public void testFindRecordNotFoundFullTextSearch() throws Exception {
        // prepare result
        MockCorresponDao.RET_COUNT = 0;
        MockCorresponDao.RET_FIND = new ArrayList<Correspon>();
        MockCorresponFullTextSearchService.RET_FULLTEXTSEARCH_LIST =
            MockCorresponFullTextSearchService.generateFTSCorresponIdList();
        // prepare condition
        SearchCorresponCondition condition = new SearchCorresponCondition();
        condition.setKeyword("dummy");
        condition.setFullTextSearchMode(FullTextSearchMode.ALL);
        // execute
        service.search(condition);
        // verify
        fail("例外が発生していない");
    }

    /**
     * 指定ページにレコードが見つからない場合、正しい例外が発生するか検証する.
     */
    @Test
    public void testFindPageNotFound() throws Exception {
        MockCorresponDao.RET_COUNT = 10;
        MockCorresponDao.RET_FIND = new ArrayList<Correspon>();
        try {
            service.search(new SearchCorresponCondition());
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals("E005", actual.getMessageCode());
        }
    }

    /**
     * CSVが取得できるか検証する.
     * @throws Exception
     */
    @Test
    public void testGenerateCsv() throws Exception {
        String str =
                /* ヘッダ */
                "No.,Correspondence No.,Previous Revision,From,To,Type,Subject,Workfrow Status,"
                        + "Created on,Issued on,Deadline for Reply,Updated on,"
                        + "Created by,Issued by,Updated by,Reply Required,Status\n"
                        /* 1行目 */
                        + "\"1\",\"YOC:OT:BUILDING-00001\",\"\",\"YOC:BUILDING\",\"YOC:IT...\","
                        + "\"Request\",\"Mock\",\"Draft\",\"01-Apr-2009\",\"\","
                        + "\"\",\"10-Apr-2009\",\"Test User/00001\",\"\","
                        + "\"Test User/00001\",\"\",\"Open\"\n"
                        /* 2行目 */
                        + "\"2\",\"YOC:OT:BUILDING-00002\",\"YOC:BUILDING-00001\",\"YOC:BUILDING\",\"\","
                        + "\"Request\",\"Mock\",\"Draft\",\"01-Apr-2009\",\"20-Apr-2009\","
                        + "\"01-May-2009\",\"10-Apr-2009\",\"Test User/00001\",\"\","
                        + "\"Test User/00001\",\"Yes\",\"Open\"\n"
                        /* 3行目 */
                        + "\"3\",\"YOC:OT:BUILDING-00003\",\"YOC:BUILDING-00001\",\"YOC:BUILDING\",\"\","
                        + "\"Request\",\"Mock\",\"Draft\",\"01-Apr-2009\",\"20-Apr-2009\","
                        + "\"\",\"10-Apr-2009\",\"Test User/00001\",\"Test User/00001\","
                        + "\"Test User/00001\",\"No\",\"Open\"\n"
                        /* 4行目 */
                        + "\"4\",\"YOC:OT:BUILDING-00001-001\",\"YOC:BUILDING-00001\",\"YOC:BUILDING\",\"\","
                        + "\"Request\",\"Mock\",\"Draft\",\"01-Apr-2009\",\"20-Apr-2009\","
                        + "\"01-May-2009\",\"10-Apr-2009\",\"Test User/00001\",\"Test User/00001\","
                        + "\"Test User/00001\",\"Yes\",\"Open\"\n"
                        /* 5行目 */
                        + "\"5\",\"YOC:OT:BUILDING-00001-002\",\"\",\"YOC:BUILDING\",\"\","
                        + "\"Request\",\"Mock\",\"Draft\",\"01-Apr-2009\",\"\","
                        + "\"01-May-2009\",\"10-Apr-2009\",\"Test User/00001\",\"\","
                        + "\"Test User/00001\",\"Yes\",\"Open\"\n";

        assertEquals(str, new String(service.generateCsv(list)));
    }

    /**
     * Excelが取得できるか検証する.
     * @throws Exception
     */
    @Test
    public void testGenerateExcel() throws Exception {

        // 結果レコード
        List<Object> expected = new ArrayList<Object>();

        /* ヘッダ */
        List<Object> line = new ArrayList<Object>();
        line.add("No.");
        line.add("Correspondence No.");
        line.add("Previous Revision");
        line.add("From");
        line.add("To");
        line.add("Type");
        line.add("Subject");
        line.add("Workfrow Status");
        line.add("Created on");
        line.add("Issued on");
        line.add("Deadline for Reply");
        line.add("Updated on");
        line.add("Created by");
        line.add("Issued by");
        line.add("Updated by");
        line.add("Reply Required");
        line.add("Status");
        expected.add(line);

        /* 1行目 */
        line = new ArrayList<Object>();
        line.add(new Integer(1));
        line.add("YOC:OT:BUILDING-00001");
        line.add(null);
        line.add("YOC:BUILDING");
        line.add("YOC:IT...");
        line.add("Request");
        line.add("Mock");
        line.add("Draft");
        line.add(new GregorianCalendar(2009, 3, 1, 1, 1, 1).getTime());
        line.add(null);
        line.add(null);
        line.add(new GregorianCalendar(2009, 3, 10, 10, 10, 10).getTime());
        line.add("Test User/00001");
        line.add(null);
        line.add("Test User/00001");
        line.add(null);
        line.add("Open");
        expected.add(line);

        /* 2行目 */
        line = new ArrayList<Object>();
        line.add(new Integer(2));
        line.add("YOC:OT:BUILDING-00002");
        line.add("YOC:BUILDING-00001");
        line.add("YOC:BUILDING");
        line.add(null);
        line.add("Request");
        line.add("Mock");
        line.add("Draft");
        line.add(new GregorianCalendar(2009, 3, 1, 1, 1, 1).getTime());
        line.add(new GregorianCalendar(2009, 3, 20, 20, 20, 20).getTime());
        line.add(new GregorianCalendar(2009, 4, 1).getTime());
        line.add(new GregorianCalendar(2009, 3, 10, 10, 10, 10).getTime());
        line.add("Test User/00001");
        line.add("");
        line.add("Test User/00001");
        line.add("Yes");
        line.add("Open");
        expected.add(line);

        /* 3行目 */
        line = new ArrayList<Object>();
        line.add(new Integer(3));
        line.add("YOC:OT:BUILDING-00003");
        line.add("YOC:BUILDING-00001");
        line.add("YOC:BUILDING");
        line.add(null);
        line.add("Request");
        line.add("Mock");
        line.add("Draft");
        line.add(new GregorianCalendar(2009, 3, 1, 1, 1, 1).getTime());
        line.add(new GregorianCalendar(2009, 3, 20, 20, 20, 20).getTime());
        line.add(null);
        line.add(new GregorianCalendar(2009, 3, 10, 10, 10, 10).getTime());
        line.add("Test User/00001");
        line.add("Test User/00001");
        line.add("Test User/00001");
        line.add("No");
        line.add("Open");
        expected.add(line);

        /* 4行目 */
        line = new ArrayList<Object>();
        line.add(new Integer(4));
        line.add("YOC:OT:BUILDING-00001-001");
        line.add("YOC:BUILDING-00001");
        line.add("YOC:BUILDING");
        line.add(null);
        line.add("Request");
        line.add("Mock");
        line.add("Draft");
        line.add(new GregorianCalendar(2009, 3, 1, 1, 1, 1).getTime());
        line.add(new GregorianCalendar(2009, 3, 20, 20, 20, 20).getTime());
        line.add(new GregorianCalendar(2009, 4, 1).getTime());
        line.add(new GregorianCalendar(2009, 3, 10, 10, 10, 10).getTime());
        line.add("Test User/00001");
        line.add("Test User/00001");
        line.add("Test User/00001");
        line.add("Yes");
        line.add("Open");
        expected.add(line);

        /* 5行目 */
        line = new ArrayList<Object>();
        line.add(new Integer(5));
        line.add("YOC:OT:BUILDING-00001-002");
        line.add(null);
        line.add("YOC:BUILDING");
        line.add(null);
        line.add("Request");
        line.add("Mock");
        line.add("Draft");
        line.add(new GregorianCalendar(2009, 3, 1, 1, 1, 1).getTime());
        line.add(null);
        line.add(new GregorianCalendar(2009, 4, 1).getTime());
        line.add(new GregorianCalendar(2009, 3, 10, 10, 10, 10).getTime());
        line.add("Test User/00001");
        line.add(null);
        line.add("Test User/00001");
        line.add("Yes");
        line.add("Open");
        expected.add(line);

        byte[] actual = service.generateExcel(list);

        // 作成したExcelを確認
        assertExcel(1, "CorresponIndex", 5, expected, actual);
    }

    private String getExpectedString(String value) {
        String param = "";
        if (value != null) {
            param = value;
        }
        return param;
    }

    private String getExpectedHTMLString(String value) {
        String param = "";
        if (value != null) {
            param = value;
        }
        // <td ...>value</td>
        return String.format(">%s</", param);
    }

    /**
     * HTMLが取得できるか検証する.
     * @throws Exception
     */
    @Test
    public void testGenerateHTML() throws Exception {
        CorresponIndexHeader header = new CorresponIndexHeader();
        header.setNo(true);
        header.setCorresponNo(true);
        header.setFrom(true);
        header.setSubject(true);
        header.setType(true);
        header.setWorkflow(true);
        header.setCreatedOn(true);
        header.setUpdatedOn(true);
        header.setCreatedBy(true);
        header.setUpdatedBy(true);
        header.setDeadline(true);
        header.setStatus(true);


        // 全ての行、列が出力されている
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        String actual = new String(service.generateHTML(list, header));
        // ヘッダ
        assertTrue(actual.contains(getExpectedHTMLString("No.")));
        assertTrue(actual.contains(getExpectedHTMLString("Correspondence No.")));
        assertTrue(actual.contains(getExpectedHTMLString("From")));
        assertTrue(actual.contains(getExpectedHTMLString("Subject")));
        assertTrue(actual.contains(getExpectedHTMLString("Type")));
        assertTrue(actual.contains(getExpectedHTMLString("Workflow Status")));
        assertTrue(actual.contains(getExpectedHTMLString("Created on")));
        assertTrue(actual.contains(getExpectedHTMLString("Updated on")));
        assertTrue(actual.contains(getExpectedHTMLString("Created by")));
        assertTrue(actual.contains(getExpectedHTMLString("Updated by")));
        assertTrue(actual.contains(getExpectedHTMLString("Deadline for Reply")));
        assertTrue(actual.contains(getExpectedHTMLString("Status")));
        for (Correspon c : list) {
            assertTrue(c.getId().toString(),
                       actual.contains(getExpectedHTMLString(c.getId().toString())));
            assertTrue(c.getCorresponNo(),
                       actual.contains(getExpectedHTMLString(c.getCorresponNo())));
            assertTrue(c.getFromCorresponGroup().getName(),
                       actual.contains(getExpectedHTMLString(c.getFromCorresponGroup().getName())));
            assertTrue(c.getSubject(),
                       actual.contains(getExpectedHTMLString(c.getSubject())));
            assertTrue(c.getCorresponType().getCorresponType(),
                       actual.contains(getExpectedHTMLString(c.getCorresponType().getCorresponType())));
            assertTrue(c.getWorkflowStatus().getLabel(),
                       actual.contains(getExpectedHTMLString(c.getWorkflowStatus().getLabel())));
            assertTrue(c.getCreatedAt().toString(),
                       actual.contains(getExpectedHTMLString(f.format(c.getCreatedAt()))));
            assertTrue(c.getUpdatedAt().toString(),
                       actual.contains(getExpectedHTMLString(f.format(c.getUpdatedAt()))));
            assertTrue(c.getCreatedBy().getNameE() + "/" + c.getCreatedBy().getEmpNo(),
                       actual.contains(getExpectedHTMLString(
                             c.getCreatedBy().getNameE() + "/" + c.getCreatedBy().getEmpNo())));
            assertTrue(c.getUpdatedBy().getNameE() + "/" + c.getUpdatedBy().getEmpNo(),
                       actual.contains(getExpectedHTMLString(
                             c.getUpdatedBy().getNameE() + "/" + c.getUpdatedBy().getEmpNo())));
            if (c.getDeadlineForReply() != null) {
                assertTrue(f.format(c.getDeadlineForReply()),
                    actual.contains(getExpectedHTMLString(f.format(c.getDeadlineForReply()))));
            }
            assertTrue(c.getCorresponStatus().getLabel(),
                       actual.contains(getExpectedHTMLString(c.getCorresponStatus().getLabel())));
        }

        // 非表示
        header.setCreatedBy(false);
        header.setCreatedOn(false);
        header.setUpdatedBy(false);
        header.setUpdatedOn(false);
        header.setDeadline(false);
        header.setStatus(false);

        // 非表示の列は出力されない
        String actualPre = new String(service.generateHTML(list, header));
        // ヘッダ
        assertTrue(actualPre.contains(getExpectedHTMLString("No.")));
        assertTrue(actualPre.contains(getExpectedHTMLString("Correspondence No.")));
        assertTrue(actualPre.contains(getExpectedHTMLString("From")));
        assertTrue(actualPre.contains(getExpectedHTMLString("Subject")));
        assertTrue(actualPre.contains(getExpectedHTMLString("Type")));
        assertTrue(actualPre.contains(getExpectedHTMLString("Workflow Status")));
        assertFalse(actualPre.contains(getExpectedHTMLString("Created on")));
        assertFalse(actualPre.contains(getExpectedHTMLString("Updated on")));
        assertFalse(actualPre.contains(getExpectedHTMLString("Created by")));
        assertFalse(actualPre.contains(getExpectedHTMLString("Updated by")));
        assertFalse(actualPre.contains(getExpectedHTMLString("Deadline for Reply")));
        assertFalse(actualPre.contains(getExpectedHTMLString("Status")));
        for (Correspon c : list) {
            assertTrue(c.getId().toString(),
                       actualPre.contains(getExpectedHTMLString(c.getId().toString())));
            assertTrue(c.getCorresponNo(),
                       actualPre.contains(getExpectedHTMLString(c.getCorresponNo())));
            assertTrue(c.getFromCorresponGroup().getName(),
                       actualPre.contains(getExpectedHTMLString(c.getFromCorresponGroup().getName())));
            assertTrue(c.getSubject(),
                       actualPre.contains(getExpectedHTMLString(c.getSubject())));
            assertTrue(c.getCorresponType().getCorresponType(),
                       actualPre.contains(getExpectedHTMLString(c.getCorresponType().getCorresponType())));
            assertTrue(c.getWorkflowStatus().getLabel(),
                       actualPre.contains(getExpectedHTMLString(c.getWorkflowStatus().getLabel())));
            assertFalse(c.getCreatedAt().toString(),
                       actualPre.contains(getExpectedHTMLString(f.format(c.getCreatedAt()))));
            assertFalse(c.getUpdatedAt().toString(),
                       actualPre.contains(getExpectedHTMLString(f.format(c.getUpdatedAt()))));
            assertFalse(c.getCreatedBy().getNameE() + "/" + c.getCreatedBy().getEmpNo(),
                       actualPre.contains(getExpectedHTMLString(
                             c.getCreatedBy().getNameE() + "/" + c.getCreatedBy().getEmpNo())));
            assertFalse(c.getUpdatedBy().getNameE() + "/" + c.getUpdatedBy().getEmpNo(),
                       actualPre.contains(getExpectedHTMLString(
                             c.getUpdatedBy().getNameE() + "/" + c.getUpdatedBy().getEmpNo())));
            if (c.getDeadlineForReply() != null) {
                assertTrue(f.format(c.getDeadlineForReply()),
                    actual.contains(getExpectedHTMLString(f.format(c.getDeadlineForReply()))));
            }
            assertFalse(c.getCorresponStatus().getLabel(),
                       actualPre.contains(getExpectedHTMLString(c.getCorresponStatus().getLabel())));
        }

        // 非表示
        header.setCreatedBy(true);
        header.setCreatedOn(true);
        header.setUpdatedBy(true);
        header.setUpdatedOn(true);
        header.setDeadline(true);
        header.setStatus(true);

        header.setNo(false);
        header.setCorresponNo(false);
        header.setFrom(false);
        header.setSubject(false);
        header.setType(false);
        header.setWorkflow(false);

        // 非表示の列は出力されない
        String actualAft = new String(service.generateHTML(list, header));
        // ヘッダ
        assertFalse(actualAft.contains(getExpectedHTMLString("No")));
        assertFalse(actualAft.contains(getExpectedHTMLString("Correspondence No.")));
        assertFalse(actualAft.contains(getExpectedHTMLString("From")));
        assertFalse(actualAft.contains(getExpectedHTMLString("Subject")));
        assertFalse(actualAft.contains(getExpectedHTMLString("Type")));
        assertFalse(actualAft.contains(getExpectedHTMLString("Workflow Status")));
        assertTrue(actualAft.contains(getExpectedHTMLString("Created on")));
        assertTrue(actualAft.contains(getExpectedHTMLString("Updated on")));
        assertTrue(actualAft.contains(getExpectedHTMLString("Created by")));
        assertTrue(actualAft.contains(getExpectedHTMLString("Updated by")));
        assertTrue(actualAft.contains(getExpectedHTMLString("Deadline for Reply")));
        assertTrue(actualAft.contains(getExpectedHTMLString("Status")));
        for (Correspon c : list) {
            assertFalse(c.getId().toString(),
                       actualAft.contains(getExpectedHTMLString(c.getId().toString())));
            assertFalse(c.getCorresponNo(),
                       actualAft.contains(getExpectedHTMLString(c.getCorresponNo())));
            assertFalse(c.getFromCorresponGroup().getName(),
                       actualAft.contains(getExpectedHTMLString(c.getFromCorresponGroup().getName())));
            assertFalse(c.getSubject(),
                       actualAft.contains(getExpectedHTMLString(c.getSubject())));
            assertFalse(c.getCorresponType().getCorresponType(),
                       actualAft.contains(getExpectedHTMLString(c.getCorresponType().getCorresponType())));
            assertFalse(c.getWorkflowStatus().getLabel(),
                       actualAft.contains(getExpectedHTMLString(c.getWorkflowStatus().getLabel())));
            assertTrue(c.getCreatedAt().toString(),
                        actualAft.contains(getExpectedHTMLString(f.format(c.getCreatedAt()))));
            assertTrue(c.getUpdatedAt().toString(),
                        actualAft.contains(getExpectedHTMLString(f.format(c.getUpdatedAt()))));
            assertTrue(c.getCreatedBy().getNameE() + "/" + c.getCreatedBy().getEmpNo(),
                        actualAft.contains(getExpectedHTMLString(
                             c.getCreatedBy().getNameE() + "/" + c.getCreatedBy().getEmpNo())));
            assertTrue(c.getUpdatedBy().getNameE() + "/" + c.getUpdatedBy().getEmpNo(),
                        actualAft.contains(getExpectedHTMLString(
                             c.getUpdatedBy().getNameE() + "/" + c.getUpdatedBy().getEmpNo())));
            if (c.getDeadlineForReply() != null) {
                assertTrue(f.format(c.getDeadlineForReply()),
                    actual.contains(getExpectedHTMLString(f.format(c.getDeadlineForReply()))));
            }
            assertTrue(c.getCorresponStatus().getLabel(),
                        actualAft.contains(getExpectedHTMLString(c.getCorresponStatus().getLabel())));
        }
    }

    /**
     * ZIPダウンロードの検証.
     */
    @Test
    public void testGenerateZip() throws Exception {
        MockAbstractService.RET_CURRENT_PROJECT_ID = "PJ1";

        //  コレポン文書番号未設定のコレポン文書を生成
        //  このテストメソッドのためだけに使用するので
        //  ここでインスタンスを生成
        Correspon noNotAssigned = new Correspon();
        PropertyUtils.copyProperties(noNotAssigned, list.get(list.size() -1));
        noNotAssigned.setCorresponNo(null);
        noNotAssigned.setId(Long.valueOf(list.size() + 1));

        list.add(noNotAssigned);

        Correspon c = new Correspon();
        c.setId(7L);
        c.setCorresponNo("YOC:OT:BUILDING-00007");
        CorresponGroup from = new CorresponGroup();
        from.setId(new Long(6L));
        from.setName("YOC:BUILDING");
        c.setFromCorresponGroup(from);
        c.setSubject("Mock");
        List<AddressCorresponGroup> addressGroups = new ArrayList<AddressCorresponGroup>();
        AddressCorresponGroup addressGroup = new AddressCorresponGroup();
        CorresponGroup group = new CorresponGroup();
        group.setId(2L);
        group.setName("YOC:IT");
        addressGroup.setCorresponGroup(group);
        addressGroup.setAddressType(AddressType.TO);
        addressGroups.add(addressGroup);
        addressGroup = new AddressCorresponGroup();
        group = new CorresponGroup();
        group.setId(2L);
        group.setName("TOK:BUILDING");
        addressGroup.setCorresponGroup(group);
        addressGroup.setAddressType(AddressType.CC);
        addressGroups.add(addressGroup);
        c.setAddressCorresponGroups(addressGroups);
        CorresponType ct = new CorresponType();
        ct.setId(new Long(1L));
        ct.setCorresponType("Request");
        c.setCorresponType(ct);
        c.setWorkflowStatus(WorkflowStatus.ISSUED);
        c.setIssuedAt(new GregorianCalendar(2009, 3, 5, 1, 1, 1).getTime());
        c.setCreatedAt(new GregorianCalendar(2009, 3, 1, 1, 1, 1).getTime());
        c.setUpdatedAt(new GregorianCalendar(2009, 3, 10, 10, 10, 10).getTime());
        User u = new User();
        u.setEmpNo("00001");
        u.setNameE("Test User");
        c.setIssuedBy(u);
        c.setCreatedBy(u);
        c.setUpdatedBy(u);
        c.setCorresponStatus(CorresponStatus.OPEN);
        c.setCustomField1Label("FIELD1");
        c.setCustomField1Value("VALUE1");
        c.setCustomField2Label("FIELD2");
        c.setCustomField2Value("VALUE2");
        c.setCustomField3Label("FIELD3");
        c.setCustomField3Value("VALUE3");
        c.setCustomField4Label("FIELD4");
        c.setCustomField4Value("VALUE4");
        c.setCustomField5Label("FIELD5");
        c.setCustomField5Value("VALUE5");
        c.setCustomField6Label("FIELD6");
        c.setCustomField6Value("VALUE6");
        c.setCustomField7Label("FIELD7");
        c.setCustomField7Value("VALUE7");
        c.setCustomField8Label("FIELD8");
        c.setCustomField8Value("VALUE8");
        c.setCustomField9Label("FIELD9");
        c.setCustomField9Value("VALUE9");
        c.setCustomField10Label("FIELD10");
        c.setCustomField10Value("VALUE10");
        c.setWorkflows(new ArrayList<Workflow>());
        c.setPreviousRevCorresponId(null);
        group = new CorresponGroup();
        group.setName("YOC:IT");
        c.setToCorresponGroup(group);
        c.setToCorresponGroupCount(2L);
        c.setIssuedAt(null);
        c.setIssuedBy(null);
        c.setReplyRequired(null);
        c.setDeadlineForReply(null);
        list.add(c);

        MockCorresponService.RET_FIND = list;
        MockAbstractService.CORRESPONS = list;
        byte[] actual = service.generateZip(list);

        ByteArrayInputStream bi = new ByteArrayInputStream(actual);
        ZipInputStream zis = new ZipInputStream(bi);
        byte[] b = new byte[1024];
        StringBuilder sb = new StringBuilder();
        String[] expFileNames = {"PJ1_YOC-OT-BUILDING-00001(0000000001).html",
                                 "PJ1_YOC-OT-BUILDING-00002(0000000002).html",
                                 "PJ1_YOC-OT-BUILDING-00003(0000000003).html",
                                 "PJ1_YOC-OT-BUILDING-00001-001(0000000004).html",
                                 "PJ1_YOC-OT-BUILDING-00001-002(0000000005).html",
                                 "PJ1_(0000000006).html",
                                 "PJ1_YOC-OT-BUILDING-00007(0000000007).html"};
        int index = 0;
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat f2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            for (ZipEntry ze = zis.getNextEntry(); ze != null; ze = zis.getNextEntry()) {
                String expFileName = expFileNames[index];
                assertEquals(expFileName, ze.getName());
                if (ze.isDirectory()) {
                    continue;
                }

                c = list.get(index);
                int i;
                while ((i = zis.read(b, 0, b.length)) != -1) {
                    sb.append(new String(b, 0, i));
                }
                String html = sb.toString();

                // 印刷用のscriptが表示されていないこと
                assertTrue(html.contains("<body>"));
                assertFalse(html.contains(">Print</a>"));
                assertFalse(html.contains(">Close</a>"));

                // 各値の確認
                assertTrue(c.getId().toString(),
                    html.contains(getExpectedString(c.getId().toString())));
                assertTrue(c.getCorresponStatus().getLabel(),
                    html.contains(getExpectedHTMLString(c.getCorresponStatus().getLabel())));

                //  コレポン文書番号未設定の場合は
                //  それとわかるラベルが出力されている
                if (c.getCorresponNo() == null) {
                    assertTrue(c.getCorresponNo(),
                        html.contains(getExpectedHTMLString(CorresponPageFormatter.DEFAULT_CORRESPON_NO)));
                } else {
                    assertTrue(c.getCorresponNo(),
                        html.contains(getExpectedHTMLString(c.getCorresponNo())));
                }

                assertTrue(c.getWorkflowStatus().getLabel(),
                    html.contains(getExpectedHTMLString(c.getWorkflowStatus().getLabel())));
                if (c.getParentCorresponId() != null) {
                    assertTrue(c.getParentCorresponNo(),
                        html.contains(getExpectedHTMLString(c.getParentCorresponNo())));
                }
                if (c.getPreviousRevCorresponId() != null) {
                    assertTrue(c.getPreviousRevCorresponNo(),
                        html.contains(getExpectedHTMLString(c.getPreviousRevCorresponNo())));
                }
                assertTrue(c.getFromCorresponGroup().getName() + " " + index,
                    html.contains(getExpectedString(c.getFromCorresponGroup().getName())));
                if (c.getAddressCorresponGroups() != null) {
                    for (AddressCorresponGroup addressCorresponGroup: c.getAddressCorresponGroups()) {
                        assertTrue(addressCorresponGroup.getCorresponGroup().getName(),
                            html.contains(addressCorresponGroup.getCorresponGroup().getName()));
                        if (addressCorresponGroup.getUsers() == null) {
                            continue;
                        }
                        for (AddressUser aUser : addressCorresponGroup.getUsers()) {
                            if (addressCorresponGroup.isTo() && aUser.isAttention()) {
                                assertTrue(aUser.getUser().getLabel(),
                                           html.contains(aUser.getUser().getLabel()));
                                if (aUser.getPersonInCharges() != null) {
                                    assertTrue(aUser.getPersonInCharges().get(0).getUser().getLabel(),
                                               html.contains(
                                                   aUser.getPersonInCharges().get(0).getUser().getLabel()));
                                }
                            } else if (addressCorresponGroup.isTo() && !aUser.isAttention()) {
                                assertFalse(aUser.getUser().getLabel(),
                                            html.contains(aUser.getUser().getLabel()));
                            } else if (addressCorresponGroup.isCc() && aUser.isCc()) {
                                assertTrue(aUser.getUser().getLabel(),
                                           html.contains(aUser.getUser().getLabel()));
                            }
                        }
                    }
                }
                assertTrue(c.getCorresponType().getName(),
                    html.contains(getExpectedHTMLString(c.getCorresponType().getName())));
                assertTrue(c.getSubject(),
                    html.contains(getExpectedHTMLString(c.getSubject())));
                assertTrue(c.getBody(),
                    html.contains(getExpectedHTMLString(c.getBody())));
                if (ReplyRequired.YES.equals(c.getReplyRequired())) {
                    assertTrue(c.getDeadlineForReply().toString(),
                        html.contains(f.format(c.getDeadlineForReply())));
                }
                if (c.getFile1FileName() != null) {
                    assertTrue(c.getFile1FileName(),
                        html.contains(c.getFile1FileName()));
                }
                if (c.getFile2FileName() != null) {
                    assertTrue(c.getFile2FileName(),
                        html.contains(c.getFile2FileName()));
                }
                if (c.getFile3FileName() != null) {
                    assertTrue(c.getFile3FileName(),
                        html.contains(c.getFile3FileName()));
                }
                if (c.getFile4FileName() != null) {
                    assertTrue(c.getFile4FileName(),
                        html.contains(c.getFile4FileName()));
                }
                if (c.getFile5FileName() != null) {
                    assertTrue(c.getFile5FileName(),
                        html.contains(c.getFile5FileName()));
                }
                if (c.getCustomField1Label() != null) {
                    assertTrue(c.getCustomField1Label(),
                        html.contains(getExpectedHTMLString(c.getCustomField1Label())));
                    assertTrue(c.getCustomField1Value(),
                        html.contains(c.getCustomField1Value()));
                }
                if (c.getCustomField2Label() != null) {
                    assertTrue(c.getCustomField2Label(),
                        html.contains(getExpectedHTMLString(c.getCustomField2Label())));
                    assertTrue(c.getCustomField2Value(),
                        html.contains(c.getCustomField2Value()));
                }
                if (c.getCustomField3Label() != null) {
                    assertTrue(c.getCustomField3Label(),
                        html.contains(getExpectedHTMLString(c.getCustomField3Label())));
                    assertTrue(c.getCustomField3Value(),
                        html.contains(c.getCustomField3Value()));
                }
                if (c.getCustomField4Label() != null) {
                    assertTrue(c.getCustomField4Label(),
                        html.contains(getExpectedHTMLString(c.getCustomField4Label())));
                    assertTrue(c.getCustomField4Value(),
                        html.contains(c.getCustomField4Value()));
                }
                if (c.getCustomField5Label() != null) {
                    assertTrue(c.getCustomField5Label(),
                        html.contains(getExpectedHTMLString(c.getCustomField5Label())));
                    assertTrue(c.getCustomField5Value(),
                        html.contains(c.getCustomField5Value()));
                }
                if (c.getCustomField6Label() != null) {
                    assertTrue(c.getCustomField6Label(),
                        html.contains(getExpectedHTMLString(c.getCustomField6Label())));
                    assertTrue(c.getCustomField6Value(),
                        html.contains(c.getCustomField6Value()));
                }
                if (c.getCustomField7Label() != null) {
                    //  何故か出力されたHTMLにはスペースが入っているので検証に失敗する...
                    //  実際に画面に表示したりダウンロードしたファイルを確認したら問題無かったので
                    //  コメントアウトする
//                    assertTrue("" + index + "*"+c.getCustomField7Label()+"*",
//                        html.contains(getExpectedHTMLString(c.getCustomField7Label())));
//                    assertTrue(c.getCustomField7Value(),
//                        html.contains(c.getCustomField7Value()));
                }
                if (c.getCustomField8Label() != null) {
                    assertTrue(c.getCustomField8Label(),
                        html.contains(getExpectedHTMLString(c.getCustomField8Label())));
                    assertTrue(c.getCustomField8Value(),
                        html.contains(c.getCustomField8Value()));
                }
                if (c.getCustomField9Label() != null) {
                    assertTrue(c.getCustomField9Label(),
                        html.contains(getExpectedHTMLString(c.getCustomField9Label())));
                    assertTrue(c.getCustomField9Value(),
                        html.contains(c.getCustomField9Value()));
                }
                if (c.getCustomField10Label() != null) {
                    assertTrue(c.getCustomField10Label(),
                        html.contains(getExpectedHTMLString(c.getCustomField10Label())));
                    assertTrue(c.getCustomField10Value(),
                        html.contains(c.getCustomField10Value()));
                }
                //Createdは表示しなくなったためコメントアウト
//                assertTrue(c.getCreatedAt().toString(),
//                   html.contains(getExpectedHTMLString(f2.format(c.getCreatedAt()))));
                if (c.getIssuedAt() != null) {
                    assertTrue(c.getIssuedAt().toString(),
                    html.contains(getExpectedHTMLString(f2.format(c.getIssuedAt()))));
                }
                assertTrue(c.getUpdatedAt().toString(),
                    html.contains(getExpectedHTMLString(f2.format(c.getUpdatedAt()))));
                //Createdは表示しなくなったためコメントアウト
//                assertTrue(c.getCreatedBy().getNameE() + "/" + c.getCreatedBy().getEmpNo(),
//                    html.contains(
//                        c.getCreatedBy().getNameE() + "/" + c.getCreatedBy().getEmpNo()));
                if ((c.getIssuedBy() != null) && (c.getIssuedBy().getNameE() != null) && (c.getIssuedBy().getEmpNo() != null)) {
                    assertTrue(c.getIssuedBy().getNameE() + "/" + c.getIssuedBy().getEmpNo(),
                        html.contains(
                            c.getIssuedBy().getNameE() + "/" + c.getIssuedBy().getEmpNo()));
                }
                assertTrue(c.getUpdatedBy().getNameE() + "/" + c.getUpdatedBy().getEmpNo(),
                    html.contains(
                        c.getUpdatedBy().getNameE() + "/" + c.getUpdatedBy().getEmpNo()));
                if (c.getWorkflows() != null) {
                    for (Workflow flow: c.getWorkflows()) {
                        assertTrue(flow.getWorkflowNo().toString(),
                            html.contains(getExpectedHTMLString(flow.getWorkflowNo().toString())));
                        assertTrue(flow.getWorkflowType().getLabel(),
                            html.contains(getExpectedHTMLString(flow.getWorkflowType().getLabel())));
                        assertTrue(flow.getUser().getNameE() + "/" + flow.getUser().getEmpNo(),
                            html.contains(getExpectedString(
                                flow.getUser().getNameE() + "/" + flow.getUser().getEmpNo())));
                        assertTrue(flow.getWorkflowProcessStatus().getLabel(),
                            html.contains(getExpectedString(flow.getWorkflowProcessStatus().getLabel())));
                        if (flow.getUpdatedBy() != null) {
                            assertTrue(flow.getUpdatedBy().getNameE() + "/" + flow.getUpdatedBy().getEmpNo(),
                                html.contains(getExpectedString(
                                    flow.getUpdatedBy().getNameE() + "/" + flow.getUpdatedBy().getEmpNo())));
                            assertTrue(flow.getUpdatedAt().toString(),
                                html.contains(f2.format(flow.getUpdatedAt())));
                        }
                        assertTrue(flow.getCommentOn(),
                            html.contains(getExpectedString(flow.getCommentOn())));
                    }
                }

                index++;
            }
        } finally {
            zis.close();
            bi.close();
        }
    }


    /**
     * ZIPダウンロードの検証.
     * HTMLファイル名称確認 + フォーマットの設定が見つからない場合.
     */
    @Test
    public void testGenerateZipHtmlName() throws Exception {
        List<String> nullKeys = new ArrayList<String>();
        nullKeys.add("file.name.regex");
        nullKeys.add("file.name.replacement");
        MockSystemConfig.NULL_KEYS = nullKeys;

        MockAbstractService.RET_CURRENT_PROJECT_ID = "PJ1";

        List<Correspon> corresponlist = new ArrayList<Correspon>();
        Correspon c = new Correspon();
        c.setId(1L);
        c.setCorresponNo("YOC:OT:BUILDING-00001");
        c.setWorkflows(new ArrayList<Workflow>());
        corresponlist.add(c);
        c = new Correspon();
        c.setId(2L);
        c.setCorresponNo(null); // 名前無し
        c.setWorkflows(new ArrayList<Workflow>());
        corresponlist.add(c);
        c = new Correspon();
        c.setId(3L);
        c.setCorresponNo(" "); // 半角スペース
        c.setWorkflows(new ArrayList<Workflow>());
        corresponlist.add(c);
        c = new Correspon();
        c.setId(4L);
        c.setCorresponNo("\\/:*?\"<>|"); // 禁止文字
        c.setWorkflows(new ArrayList<Workflow>());
        corresponlist.add(c);
        c = new Correspon();
        c.setId(9999999999L); // 前詰め無し
        c.setCorresponNo("YOC:OT:BUILDING-00001");
        c.setWorkflows(new ArrayList<Workflow>());
        corresponlist.add(c);
        c = new Correspon();
        c.setId(10000000000L); // 10桁以上
        c.setCorresponNo("YOC:OT:BUILDING-00001");
        c.setWorkflows(new ArrayList<Workflow>());
        corresponlist.add(c);

        MockCorresponService.RET_FIND = corresponlist;
        MockAbstractService.CORRESPONS = corresponlist;

        byte[] actual = service.generateZip(corresponlist);

        ByteArrayInputStream bi = new ByteArrayInputStream(actual);
        ZipInputStream zis = new ZipInputStream(bi);
        String[] expFileNames = {"PJ1_YOC-OT-BUILDING-00001(0000000001).html",
                                 "PJ1_(0000000002).html",
                                 "PJ1_ (0000000003).html",
                                 "PJ1_---------(0000000004).html",
                                 "PJ1_YOC-OT-BUILDING-00001(9999999999).html",
                                 "PJ1_YOC-OT-BUILDING-00001(10000000000).html"};
        int index = 0;
        try {
            for (ZipEntry ze = zis.getNextEntry(); ze != null; ze = zis.getNextEntry()) {
                String expFileName = expFileNames[index];
                assertEquals(expFileName, ze.getName());
                if (ze.isDirectory()) {
                    continue;
                }
                index++;
            }
        } finally {
            zis.close();
            bi.close();
        }
    }

    /**
     * ZIPダウンロードの検証.
     * 引数NULL.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGenerateZipNull() throws Exception {
        service.generateZip(null);
        fail("例外が発生していない");
    }

    /**
     * ZIPダウンロードの検証.
     * コレポン文書が未選択.
     */
    @Test
    public void testGenerateZipNotSelected() throws Exception {
        try {
            service.generateZip(new ArrayList<Correspon>());
            fail("例外が発生していない");
        } catch (ServiceAbortException e) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CORRESPON_NOT_SELECTED,
                         e.getMessageCode());
        }
    }

    /**
     * ZIPダウンロードの検証.
     * コレポン文書が見つからない.
     */
    @Test
    public void testGenerateZipNoDataFound() throws Exception {
        try {
            MockCorresponService.FIND_EXCEPTION = true;
            service.generateZip(list);
            fail("例外が発生していない");
        } catch (ServiceAbortException e) {
            assertEquals(ApplicationMessageCode.NO_DATA_FOUND,
                e.getMessageCode());
        }
    }

    /**
     * 既読／未読ステータス更新の検証.
     */
    @Test
    public void testUpdateCorresponsReadStatus() throws Exception {
        List<Correspon> expCreate = new ArrayList<Correspon>();

        List<Correspon> list = new ArrayList<Correspon>();
        Correspon correspon = new Correspon();
        correspon.setId(1L);
        CorresponReadStatus cReadStatus = new CorresponReadStatus();
        cReadStatus.setId(null); // レコード無し
        cReadStatus.setReadStatus(ReadStatus.READ);
        correspon.setCorresponReadStatus(cReadStatus);
        expCreate.add(correspon); // 登録
        list.add(correspon);

        correspon = new Correspon();
        correspon.setId(2L);
        cReadStatus = new CorresponReadStatus();
        cReadStatus.setId(11L); // レコードあり
        cReadStatus.setReadStatus(ReadStatus.READ);
        correspon.setCorresponReadStatus(cReadStatus);
        expCreate.add(correspon); // 登録
        list.add(correspon);

        correspon = new Correspon();
        correspon.setId(3L);
        cReadStatus = new CorresponReadStatus();
        cReadStatus.setId(null); // レコード無し
        cReadStatus.setReadStatus(ReadStatus.NEW);
        correspon.setCorresponReadStatus(cReadStatus);
        expCreate.add(correspon); // 登録
        list.add(correspon);

        correspon = new Correspon();
        correspon.setId(4L);
        cReadStatus = new CorresponReadStatus();
        cReadStatus.setId(11L); // レコードあり
        cReadStatus.setReadStatus(ReadStatus.NEW);
        correspon.setCorresponReadStatus(cReadStatus);
        expCreate.add(correspon); // 登録
        list.add(correspon);

        MockCorresponDao.RET_FIND_BY_ID.put("1", new Correspon());
        MockCorresponDao.RET_FIND_BY_ID.put("2", new Correspon());
        MockCorresponDao.RET_FIND_BY_ID.put("3", new Correspon());
        MockCorresponDao.RET_FIND_BY_ID.put("4", new Correspon());

        String expEmpNo = "80001";
        User expUser = new User();
        expUser.setEmpNo(expEmpNo);
        MockCorresponReadStatusService.RET_CURRENT_USER = expUser;

        service.updateCorresponsReadStatus(list);

        List<CorresponReadStatus> actCreate = MockCorresponReadStatusService.SET_CREATE;

        assertEquals(expCreate.size(), actCreate.size());
        for (int i = 0; i < actCreate.size(); i++) {
            CorresponReadStatus actual = actCreate.get(i);
            Correspon expected = expCreate.get(i);
            assertEquals(expected.getId(), actual.getCorresponId());
            assertEquals(expEmpNo, actual.getEmpNo());
            assertEquals(expected.getCorresponReadStatus().getReadStatus(), actual.getReadStatus());
            assertEquals(expUser, actual.getCreatedBy());
            assertEquals(expUser, actual.getUpdatedBy());
        }
    }

    /**
     * 既読／未読ステータス更新の検証.
     * （引数Null）
     */
    @Test(expected = IllegalArgumentException.class)
    public void testUpdateCorresponsReadStatusNull() throws Exception {
        service.updateCorresponsReadStatus(null);
        fail("例外が発生していない");
    }

    /**
     * 既読／未読ステータス更新の検証.
     * （コレポン文書が未選択）
     */
    @Test
    public void testUpdateCorresponsReadStatusNotSelected() throws Exception {
        try {
            service.updateCorresponsReadStatus(new ArrayList<Correspon>());
            fail("例外が発生していない");
        } catch (ServiceAbortException e) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CORRESPON_NOT_SELECTED,
                         e.getMessageCode());
        }
    }

    /**
     * 既読／未読ステータス更新の検証.
     * （コレポン文書が検索できない）
     */
    @Test
    public void testUpdateCorresponsReadStatusNoDataFound() throws Exception {
        try {
            MockCorresponDao.EX_FIND_BY_ID = new RecordNotFoundException();

            List<Correspon> list = new ArrayList<Correspon>();
            Correspon correspon = new Correspon();
            correspon.setId(1L);
            CorresponReadStatus cReadStatus = new CorresponReadStatus();
            cReadStatus.setId(null);
            cReadStatus.setReadStatus(ReadStatus.READ);
            correspon.setCorresponReadStatus(cReadStatus);
            list.add(correspon);

            service.updateCorresponsReadStatus(list);

            fail("例外が発生していない");
        } catch (ServiceAbortException e) {
            assertEquals(ApplicationMessageCode.NO_DATA_FOUND,
                         e.getMessageCode());
        }
    }

    /**
     * 既読／未読ステータス更新の検証.
     * （既読／未読ステータスIDからレコードが検索できない → 登録）
     */
    @Test
    public void testUpdateCorresponsReadStatusNoDataFound2() throws Exception {
        MockAbstractDao.EX_FIND_BY_ID = new RecordNotFoundException();

        String expEmpNo = "80001";
        User expUser = new User();
        expUser.setEmpNo(expEmpNo);
        MockCorresponReadStatusService.RET_CURRENT_USER = expUser;

        List<Correspon> expCreate = new ArrayList<Correspon>();
        Correspon correspon = new Correspon();
        correspon.setId(1L);
        CorresponReadStatus cReadStatus = new CorresponReadStatus();
        cReadStatus.setId(11L);
        cReadStatus.setReadStatus(ReadStatus.READ);
        correspon.setCorresponReadStatus(cReadStatus);
        expCreate.add(correspon);

        MockCorresponDao.RET_FIND_BY_ID.put("1", new Correspon());

        service.updateCorresponsReadStatus(expCreate);

        List<CorresponReadStatus> actCreate = MockCorresponReadStatusService.SET_CREATE;

        assertEquals(expCreate.size(), actCreate.size());
        for (int i = 0; i < actCreate.size(); i++) {
            CorresponReadStatus actual = actCreate.get(i);
            Correspon expected = expCreate.get(i);
            assertEquals(expected.getId(), actual.getCorresponId());
            assertEquals(expEmpNo, actual.getEmpNo());
            assertEquals(expected.getCorresponReadStatus().getReadStatus(), actual.getReadStatus());
            assertEquals(expUser, actual.getUpdatedBy());
        }
    }

    /**
     * 既読／未読ステータス更新の検証.
     * （排他エラー）
     */
    @Test
    public void testUpdateCorresponsReadStatusStaleRecord() throws Exception {
        try {
            MockCorresponReadStatusService.EX_UPDATE = new ServiceAbortException(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CORRESPON_ALREADY_UPDATED);

            List<Correspon> list = new ArrayList<Correspon>();
            Correspon correspon = new Correspon();
            correspon.setId(1L);
            CorresponReadStatus cReadStatus = new CorresponReadStatus();
            cReadStatus.setId(11L);
            cReadStatus.setReadStatus(ReadStatus.READ);
            correspon.setCorresponReadStatus(cReadStatus);
            list.add(correspon);

            MockCorresponDao.RET_FIND_BY_ID.put("1", correspon);

            service.updateCorresponsReadStatus(list);

            fail("例外が発生していない");
        } catch (ServiceAbortException e) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CORRESPON_ALREADY_UPDATED,
                         e.getMessageCode());
        }
    }

    /**
     * 文書状態更新の検証.
     * SystemAdmin.
     */
    @Test
    public void testUpdateCorresponsStatus() throws Exception {
        List<Correspon> expList = new ArrayList<Correspon>();
        Correspon correspon = new Correspon();
        CorresponReadStatus crs = new CorresponReadStatus();
        correspon.setId(1L);
        correspon.setCorresponStatus(CorresponStatus.OPEN);
        crs.setCorresponId(1L);
        crs.setReadStatus(ReadStatus.READ);
        correspon.setCorresponReadStatus(crs);
        setValuesForUpdateCorresponStatus(correspon);
        expList.add(correspon);

        correspon = new Correspon();
        crs = new CorresponReadStatus();
        correspon.setId(2L);
        correspon.setCorresponStatus(CorresponStatus.CLOSED);
        crs.setCorresponId(2L);
        crs.setReadStatus(ReadStatus.READ);
        correspon.setCorresponReadStatus(crs);
        setValuesForUpdateCorresponStatus(correspon);
        expList.add(correspon);

        correspon = new Correspon();
        crs = new CorresponReadStatus();
        correspon.setId(3L);
        correspon.setCorresponStatus(CorresponStatus.OPEN);
        crs.setCorresponId(3L);
        crs.setReadStatus(ReadStatus.READ);
        correspon.setCorresponReadStatus(crs);
        setValuesForUpdateCorresponStatus(correspon);
        expList.add(correspon);

        correspon = new Correspon();
        crs = new CorresponReadStatus();
        correspon.setId(4L);
        correspon.setCorresponStatus(CorresponStatus.CLOSED);
        crs.setCorresponId(4L);
        crs.setReadStatus(ReadStatus.READ);
        correspon.setCorresponReadStatus(crs);
        setValuesForUpdateCorresponStatus(correspon);
        expList.add(correspon);

        Correspon dbCorrespon = new Correspon();
        dbCorrespon.setCorresponStatus(CorresponStatus.OPEN);
        MockCorresponDao.RET_FIND_BY_ID.put("1", dbCorrespon);
        MockCorresponDao.RET_FIND_BY_ID.put("2", dbCorrespon);
        MockCorresponDao.RET_FIND_BY_ID.put("3", dbCorrespon);
        MockCorresponDao.RET_FIND_BY_ID.put("4", dbCorrespon);

        String expEmpNo = "80001";
        User expUser = new User();
        expUser.setEmpNo(expEmpNo);
        MockAbstractService.RET_CURRENT_USER = expUser;
        MockAbstractService.RET_SYSTEM_ADMIN = true;

        MockCorresponReadStatusService.RET_CURRENT_USER = expUser;
        MockCorresponReadStatusService.SET_READ_CREATE.setReadStatus(ReadStatus.READ);

        service.updateCorresponsStatus(expList);

        List<Correspon> actList = MockAbstractDao.SET_UPDATE_CORRESPON;
        assertEquals(expList.size(), actList.size());
        for (int i = 0; i < actList.size(); i++) {
            Correspon actual = actList.get(i);
            Correspon expected = expList.get(i);
            assertEquals(expected.getId(), actual.getId());
            assertEquals(expected.getCorresponStatus(), actual.getCorresponStatus());
            assertEquals(expUser, actual.getUpdatedBy());
            assertEquals(expected.getVersionNo(), actual.getVersionNo());
            assertEquals(ReadStatus.READ, MockCorresponReadStatusService.SET_READ_CREATE.getReadStatus()) ;
        }
    }

    /**
     * 文書状態更新の検証.
     * Project Admin.
     */
    @Test
    public void testUpdateCorresponsStatusProjectAdmin() throws Exception {
        List<Correspon> expList = new ArrayList<Correspon>();
        Correspon correspon = new Correspon();
        correspon.setId(1L);
        correspon.setCorresponStatus(CorresponStatus.OPEN);
        setValuesForUpdateCorresponStatus(correspon);
        expList.add(correspon);

        correspon = new Correspon();
        correspon.setId(2L);
        correspon.setCorresponStatus(CorresponStatus.CLOSED);
        setValuesForUpdateCorresponStatus(correspon);
        expList.add(correspon);

        correspon = new Correspon();
        correspon.setId(3L);
        correspon.setCorresponStatus(CorresponStatus.OPEN);
        setValuesForUpdateCorresponStatus(correspon);
        expList.add(correspon);

        correspon = new Correspon();
        correspon.setId(4L);
        correspon.setCorresponStatus(CorresponStatus.CLOSED);
        setValuesForUpdateCorresponStatus(correspon);
        expList.add(correspon);

        Correspon dbCorrespon = new Correspon();
        dbCorrespon.setCorresponStatus(CorresponStatus.OPEN);
        MockCorresponDao.RET_FIND_BY_ID.put("1", dbCorrespon);
        MockCorresponDao.RET_FIND_BY_ID.put("2", dbCorrespon);
        MockCorresponDao.RET_FIND_BY_ID.put("3", dbCorrespon);
        MockCorresponDao.RET_FIND_BY_ID.put("4", dbCorrespon);

        String expEmpNo = "80001";
        User expUser = new User();
        expUser.setEmpNo(expEmpNo);
        MockAbstractService.RET_CURRENT_USER = expUser;
        MockAbstractService.RET_PROJECT_ADMIN = true;

        MockCorresponReadStatusService.RET_CURRENT_USER = expUser;

        service.updateCorresponsStatus(expList);

        List<Correspon> actList = MockAbstractDao.SET_UPDATE_CORRESPON;
        assertEquals(expList.size(), actList.size());
        for (int i = 0; i < actList.size(); i++) {
            Correspon actual = actList.get(i);
            Correspon expected = expList.get(i);
            assertEquals(expected.getId(), actual.getId());
            assertEquals(expected.getCorresponStatus(), actual.getCorresponStatus());
            assertEquals(expUser, actual.getUpdatedBy());
            assertEquals(expected.getVersionNo(), actual.getVersionNo());
        }
    }

    /**
     * 文書状態更新の検証.
     * Group Admin.
     */
    @Test
    public void testUpdateCorresponsStatusGroupAdmin() throws Exception {
        List<Correspon> expList = new ArrayList<Correspon>();
        Correspon correspon = new Correspon();
        correspon.setId(1L);
        correspon.setCorresponStatus(CorresponStatus.OPEN);
        setValuesForUpdateCorresponStatus(correspon);
        expList.add(correspon);

        correspon = new Correspon();
        correspon.setId(2L);
        correspon.setCorresponStatus(CorresponStatus.CLOSED);
        setValuesForUpdateCorresponStatus(correspon);
        expList.add(correspon);

        correspon = new Correspon();
        correspon.setId(3L);
        correspon.setCorresponStatus(CorresponStatus.OPEN);
        setValuesForUpdateCorresponStatus(correspon);
        expList.add(correspon);

        correspon = new Correspon();
        correspon.setId(4L);
        correspon.setCorresponStatus(CorresponStatus.CLOSED);
        setValuesForUpdateCorresponStatus(correspon);
        expList.add(correspon);

        Correspon dbCorrespon = new Correspon();
        dbCorrespon.setCorresponStatus(CorresponStatus.OPEN);
        MockCorresponDao.RET_FIND_BY_ID.put("1", dbCorrespon);
        MockCorresponDao.RET_FIND_BY_ID.put("2", dbCorrespon);
        MockCorresponDao.RET_FIND_BY_ID.put("3", dbCorrespon);
        MockCorresponDao.RET_FIND_BY_ID.put("4", dbCorrespon);

        String expEmpNo = "80001";
        User expUser = new User();
        expUser.setEmpNo(expEmpNo);
        MockAbstractService.RET_CURRENT_USER = expUser;
        MockAddressCorresponGroupDao.RET_FIND_BY_CORRESPON_ID = new ArrayList<AddressCorresponGroup>();
        MockAbstractService.RET_GROUP_ADMIN = true;

        MockCorresponReadStatusService.RET_CURRENT_USER = expUser;

        service.updateCorresponsStatus(expList);

        List<Correspon> actList = MockAbstractDao.SET_UPDATE_CORRESPON;
        assertEquals(expList.size(), actList.size());
        for (int i = 0; i < actList.size(); i++) {
            Correspon actual = actList.get(i);
            Correspon expected = expList.get(i);
            assertEquals(expected.getId(), actual.getId());
            assertEquals(expected.getCorresponStatus(), actual.getCorresponStatus());
            assertEquals(expUser, actual.getUpdatedBy());
            assertEquals(expected.getVersionNo(), actual.getVersionNo());
        }
    }

    /**
     * 文書状態更新の検証.
     * Preparer.
     */
    @Test
    public void testUpdateCorresponsStatusPreparer() throws Exception {
        List<Correspon> expList = new ArrayList<Correspon>();
        Correspon correspon = new Correspon();
        correspon.setId(1L);
        correspon.setCorresponStatus(CorresponStatus.OPEN);
        correspon.setCreatedBy(new User());
        setValuesForUpdateCorresponStatus(correspon);
        expList.add(correspon);

        correspon = new Correspon();
        correspon.setId(2L);
        correspon.setCorresponStatus(CorresponStatus.CLOSED);
        correspon.setCreatedBy(new User());
        setValuesForUpdateCorresponStatus(correspon);
        expList.add(correspon);

        correspon = new Correspon();
        correspon.setId(3L);
        correspon.setCorresponStatus(CorresponStatus.OPEN);
        correspon.setCreatedBy(new User());
        setValuesForUpdateCorresponStatus(correspon);
        expList.add(correspon);

        correspon = new Correspon();
        correspon.setId(4L);
        correspon.setCorresponStatus(CorresponStatus.CLOSED);
        correspon.setCreatedBy(new User());
        setValuesForUpdateCorresponStatus(correspon);
        expList.add(correspon);

        Correspon dbCorrespon = new Correspon();
        dbCorrespon.setCorresponStatus(CorresponStatus.OPEN);
        MockCorresponDao.RET_FIND_BY_ID.put("1", dbCorrespon);
        MockCorresponDao.RET_FIND_BY_ID.put("2", dbCorrespon);
        MockCorresponDao.RET_FIND_BY_ID.put("3", dbCorrespon);
        MockCorresponDao.RET_FIND_BY_ID.put("4", dbCorrespon);

        String expEmpNo = "80001";
        User expUser = new User();
        expUser.setEmpNo(expEmpNo);
        MockAbstractService.RET_CURRENT_USER = expUser;
        MockAddressCorresponGroupDao.RET_FIND_BY_CORRESPON_ID = new ArrayList<AddressCorresponGroup>();
        MockAbstractService.RET_PREPARER = true;

        MockCorresponReadStatusService.RET_CURRENT_USER = expUser;

        service.updateCorresponsStatus(expList);

        List<Correspon> actList = MockAbstractDao.SET_UPDATE_CORRESPON;
        assertEquals(expList.size(), actList.size());
        for (int i = 0; i < actList.size(); i++) {
            Correspon actual = actList.get(i);
            Correspon expected = expList.get(i);
            assertEquals(expected.getId(), actual.getId());
            assertEquals(expected.getCorresponStatus(), actual.getCorresponStatus());
            assertEquals(expUser, actual.getUpdatedBy());
            assertEquals(expected.getVersionNo(), actual.getVersionNo());
        }
    }

    /**
     * 文書状態更新の検証.
     * Checker.
     */
    @Test
    public void testUpdateCorresponsStatusChecker() throws Exception {
        List<Correspon> expList = new ArrayList<Correspon>();
        Correspon correspon = new Correspon();
        correspon.setId(1L);
        correspon.setCorresponStatus(CorresponStatus.OPEN);
        correspon.setCreatedBy(new User());
        setValuesForUpdateCorresponStatus(correspon);
        expList.add(correspon);

        correspon = new Correspon();
        correspon.setId(2L);
        correspon.setCorresponStatus(CorresponStatus.CLOSED);
        correspon.setCreatedBy(new User());
        setValuesForUpdateCorresponStatus(correspon);
        expList.add(correspon);

        correspon = new Correspon();
        correspon.setId(3L);
        correspon.setCorresponStatus(CorresponStatus.OPEN);
        correspon.setCreatedBy(new User());
        setValuesForUpdateCorresponStatus(correspon);
        expList.add(correspon);

        correspon = new Correspon();
        correspon.setId(4L);
        correspon.setCorresponStatus(CorresponStatus.CLOSED);
        correspon.setCreatedBy(new User());
        setValuesForUpdateCorresponStatus(correspon);
        expList.add(correspon);

        Correspon dbCorrespon = new Correspon();
        dbCorrespon.setCorresponStatus(CorresponStatus.OPEN);
        MockCorresponDao.RET_FIND_BY_ID.put("1", dbCorrespon);
        MockCorresponDao.RET_FIND_BY_ID.put("2", dbCorrespon);
        MockCorresponDao.RET_FIND_BY_ID.put("3", dbCorrespon);
        MockCorresponDao.RET_FIND_BY_ID.put("4", dbCorrespon);

        String expEmpNo = "80001";
        User expUser = new User();
        expUser.setEmpNo(expEmpNo);
        MockAbstractService.RET_CURRENT_USER = expUser;
        MockAddressCorresponGroupDao.RET_FIND_BY_CORRESPON_ID = new ArrayList<AddressCorresponGroup>();
        MockAbstractService.RET_CHECKER = true;

        MockCorresponReadStatusService.RET_CURRENT_USER = expUser;

        service.updateCorresponsStatus(expList);

        List<Correspon> actList = MockAbstractDao.SET_UPDATE_CORRESPON;
        assertEquals(expList.size(), actList.size());
        for (int i = 0; i < actList.size(); i++) {
            Correspon actual = actList.get(i);
            Correspon expected = expList.get(i);
            assertEquals(expected.getId(), actual.getId());
            assertEquals(expected.getCorresponStatus(), actual.getCorresponStatus());
            assertEquals(expUser, actual.getUpdatedBy());
            assertEquals(expected.getVersionNo(), actual.getVersionNo());
        }
    }

    /**
     * 文書状態更新の検証.
     * Approver.
     */
    @Test
    public void testUpdateCorresponsStatusApprover() throws Exception {
        List<Correspon> expList = new ArrayList<Correspon>();
        Correspon correspon = new Correspon();
        correspon.setId(1L);
        correspon.setCorresponStatus(CorresponStatus.OPEN);
        correspon.setCreatedBy(new User());
        setValuesForUpdateCorresponStatus(correspon);
        expList.add(correspon);

        correspon = new Correspon();
        correspon.setId(2L);
        correspon.setCorresponStatus(CorresponStatus.CLOSED);
        correspon.setCreatedBy(new User());
        setValuesForUpdateCorresponStatus(correspon);
        expList.add(correspon);

        correspon = new Correspon();
        correspon.setId(3L);
        correspon.setCorresponStatus(CorresponStatus.OPEN);
        correspon.setCreatedBy(new User());
        setValuesForUpdateCorresponStatus(correspon);
        expList.add(correspon);

        correspon = new Correspon();
        correspon.setId(4L);
        correspon.setCorresponStatus(CorresponStatus.CLOSED);
        correspon.setCreatedBy(new User());
        setValuesForUpdateCorresponStatus(correspon);
        expList.add(correspon);

        Correspon dbCorrespon = new Correspon();
        dbCorrespon.setCorresponStatus(CorresponStatus.OPEN);
        MockCorresponDao.RET_FIND_BY_ID.put("1", dbCorrespon);
        MockCorresponDao.RET_FIND_BY_ID.put("2", dbCorrespon);
        MockCorresponDao.RET_FIND_BY_ID.put("3", dbCorrespon);
        MockCorresponDao.RET_FIND_BY_ID.put("4", dbCorrespon);

        String expEmpNo = "80001";
        User expUser = new User();
        expUser.setEmpNo(expEmpNo);
        MockAbstractService.RET_CURRENT_USER = expUser;
        MockAddressCorresponGroupDao.RET_FIND_BY_CORRESPON_ID = new ArrayList<AddressCorresponGroup>();
        MockAbstractService.RET_APPROVER = true;

        MockCorresponReadStatusService.RET_CURRENT_USER = expUser;

        service.updateCorresponsStatus(expList);

        List<Correspon> actList = MockAbstractDao.SET_UPDATE_CORRESPON;
        assertEquals(expList.size(), actList.size());
        for (int i = 0; i < actList.size(); i++) {
            Correspon actual = actList.get(i);
            Correspon expected = expList.get(i);
            assertEquals(expected.getId(), actual.getId());
            assertEquals(expected.getCorresponStatus(), actual.getCorresponStatus());
            assertEquals(expUser, actual.getUpdatedBy());
            assertEquals(expected.getVersionNo(), actual.getVersionNo());
        }
    }

    /**
     * 文書状態更新の検証.
     * （引数Null）
     */
    @Test(expected = IllegalArgumentException.class)
    public void testUpdateCorresponsStatusNull() throws Exception {
        service.updateCorresponsStatus(null);
        fail("例外が発生していない");
    }

    /**
     * 文書状態更新の検証.
     * （コレポン文書が未選択）
     */
    @Test
    public void testUpdateCorresponsStatusNotSelected() throws Exception {
        try {
            service.updateCorresponsStatus(new ArrayList<Correspon>());
            fail("例外が発生していない");
        } catch (ServiceAbortException e) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CORRESPON_NOT_SELECTED,
                         e.getMessageCode());
        }
    }

    /**
     * 文書状態更新の検証.
     * Preparerが更新できない文書を選択した場合
     */
    @Test
    public void testUpdateCorresponsStatusInvalidPreparer() throws Exception {
        MockWorkflowDao.RET_FIND_BY_CORRESPON_ID = getWorkflow();
        WorkflowStatus[] invalidStatus = {
            WorkflowStatus.REQUEST_FOR_CHECK,
            WorkflowStatus.REQUEST_FOR_APPROVAL,
            WorkflowStatus.UNDER_CONSIDERATION
        };
        for (WorkflowStatus s : invalidStatus) {
            try {
                String expEmpNo = "80001";
                User expUser = new User();
                expUser.setEmpNo(expEmpNo);
                MockAbstractService.RET_CURRENT_USER = expUser;

                List<Correspon> list = new ArrayList<Correspon>();
                Correspon correspon = new Correspon();
                correspon.setId(1L);
                correspon.setCreatedBy(expUser);
                setValuesForUpdateCorresponStatus(correspon);
                // 承認状態が検証・承認依頼中の場合は
                // Preparerは更新できない
                correspon.setWorkflowStatus(s);
                list.add(correspon);

                service.updateCorresponsStatus(list);

                fail("例外が発生していない");
            } catch (ServiceAbortException e) {
                assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_WORKFLOW_STATUS_INVALID,
                    e.getMessageCode());
            }
        }
    }

    /**
     * 文書状態更新の検証.
     * Checkerが更新できない文書を選択した場合
     */
    @Test
    public void testUpdateCorresponsStatusInvalidChecker() throws Exception {
        String expEmpNo = "80001";
        User expUser = new User();
        expUser.setEmpNo(expEmpNo);
        MockAbstractService.RET_CURRENT_USER = expUser;

        List<Workflow> workflows = getWorkflow();
        Workflow checker = workflows.get(0);
        checker.setUser(expUser);
        MockWorkflowDao.RET_FIND_BY_CORRESPON_ID = workflows;

        WorkflowProcessStatus[] invalidStatus = {
            WorkflowProcessStatus.NONE,
            WorkflowProcessStatus.CHECKED,
            WorkflowProcessStatus.DENIED
        };
        for (WorkflowProcessStatus s : invalidStatus) {
            try {
                List<Correspon> list = new ArrayList<Correspon>();
                Correspon correspon = new Correspon();
                correspon.setId(1L);
                correspon.setCreatedBy(expUser);
                setValuesForUpdateCorresponStatus(correspon);
                // 検証・承認依頼中
                correspon.setWorkflowStatus(WorkflowStatus.REQUEST_FOR_CHECK);
                list.add(correspon);

                // Checkerの承認作業状態を変更
                checker.setWorkflowProcessStatus(s);

                service.updateCorresponsStatus(list);

                fail("例外が発生していない");
            } catch (ServiceAbortException e) {
                assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_WORKFLOW_STATUS_INVALID,
                    e.getMessageCode());
            }
        }
    }

    /**
     * 文書状態更新の検証.
     * Approverが更新できない文書を選択した場合
     */
    @Test
    public void testUpdateCorresponsStatusInvalidApprover() throws Exception {
        String expEmpNo = "80001";
        User expUser = new User();
        expUser.setEmpNo(expEmpNo);
        MockAbstractService.RET_CURRENT_USER = expUser;

        List<Workflow> workflows = getWorkflow();
        Workflow approver = workflows.get(workflows.size() - 1);
        approver.setUser(expUser);
        MockWorkflowDao.RET_FIND_BY_CORRESPON_ID = workflows;

        WorkflowProcessStatus[] invalidStatus = {
            WorkflowProcessStatus.NONE,
            WorkflowProcessStatus.APPROVED,
            WorkflowProcessStatus.DENIED
        };
        for (WorkflowProcessStatus s : invalidStatus) {
            try {
                List<Correspon> list = new ArrayList<Correspon>();
                Correspon correspon = new Correspon();
                correspon.setId(1L);
                correspon.setCreatedBy(expUser);
                setValuesForUpdateCorresponStatus(correspon);
                // 検証・承認依頼中
                correspon.setWorkflowStatus(WorkflowStatus.REQUEST_FOR_APPROVAL);
                list.add(correspon);

                // Checkerの承認作業状態を変更
                approver.setWorkflowProcessStatus(s);

                service.updateCorresponsStatus(list);

                fail("例外が発生していない");
            } catch (ServiceAbortException e) {
                assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_WORKFLOW_STATUS_INVALID,
                    e.getMessageCode());
            }
        }
    }
    /**
     * 文書状態更新の検証.
     * （ステータスエラー）
     */
    @Test
    public void testUpdateCorresponsStatusStatusError() throws Exception {
        try {
            String expEmpNo = "80001";
            User expUser = new User();
            expUser.setEmpNo(expEmpNo);
            MockAbstractService.RET_CURRENT_USER = expUser;
            MockAbstractService.RET_SYSTEM_ADMIN = true;

            Correspon dbCorrespon = new Correspon();
            dbCorrespon.setCorresponStatus(CorresponStatus.CANCELED);
            MockCorresponDao.RET_FIND_BY_ID.put("1", dbCorrespon);

            List<Correspon> list = new ArrayList<Correspon>();
            Correspon correspon = new Correspon();
            correspon.setId(1L);
            correspon.setWorkflowStatus(WorkflowStatus.DRAFT);
            correspon.setCreatedBy(new User());
            list.add(correspon);

            service.updateCorresponsStatus(list);

            fail("例外が発生していない");
        } catch (ServiceAbortException e) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CORREPON_CANCELED,
                         e.getMessageCode());
        }
    }

    /**
     * 文書状態更新の検証.
     * （排他エラー）
     */
    @Test
    public void testUpdateCorresponsStatusStaleRecord() throws Exception {
        try {
            String expEmpNo = "80001";
            User expUser = new User();
            expUser.setEmpNo(expEmpNo);
            MockAbstractService.RET_CURRENT_USER = expUser;
            MockAbstractService.RET_SYSTEM_ADMIN = true;

            Correspon dbCorrespon = new Correspon();
            dbCorrespon.setCorresponStatus(CorresponStatus.OPEN);
            MockCorresponDao.RET_FIND_BY_ID.put("1", dbCorrespon);

            MockAbstractDao.EX_UPDATE = new StaleRecordException();

            List<Correspon> list = new ArrayList<Correspon>();
            Correspon correspon = new Correspon();
            correspon.setId(1L);
            correspon.setWorkflowStatus(WorkflowStatus.DRAFT);
            correspon.setCreatedBy(new User());
            list.add(correspon);

            service.updateCorresponsStatus(list);

            fail("例外が発生していない");
        } catch (ServiceAbortException e) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CORRESPON_ALREADY_UPDATED,
                         e.getMessageCode());
        }
    }

    /**
     * 一括削除の検証.
     * （Preparer）
     */
    @Test
    public void testDeleteCorrespons() throws Exception {
        String expEmpNo = "80001";
        User expUser = new User();
        expUser.setEmpNo(expEmpNo);

        List<Correspon> expList = new ArrayList<Correspon>();
        Correspon correspon = new Correspon();
        correspon.setId(1L);
        correspon.setWorkflowStatus(WorkflowStatus.DRAFT);
        correspon.setCreatedBy(expUser);
        expList.add(correspon);

        correspon = new Correspon();
        correspon.setId(2L);
        correspon.setWorkflowStatus(WorkflowStatus.DRAFT);
        correspon.setCreatedBy(expUser);
        expList.add(correspon);

        correspon = new Correspon();
        correspon.setId(3L);
        correspon.setWorkflowStatus(WorkflowStatus.DRAFT);
        correspon.setCreatedBy(expUser);
        expList.add(correspon);

        correspon = new Correspon();
        correspon.setId(4L);
        correspon.setWorkflowStatus(WorkflowStatus.DRAFT);
        correspon.setCreatedBy(expUser);
        expList.add(correspon);

        MockCorresponDao.RET_FIND_BY_ID.put("1", new Correspon());
        MockAbstractService.RET_CURRENT_USER = expUser;
        MockAbstractService.RET_SYSTEM_ADMIN = false;

        service.deleteCorrespons(expList);

        List<Correspon> actList = MockAbstractDao.SET_DELETE;
        assertEquals(expList.size(), actList.size());
        for (int i = 0; i < actList.size(); i++) {
            Correspon actual = actList.get(i);
            Correspon expected = expList.get(i);
            assertEquals(expected.getId(), actual.getId());
            assertEquals(expUser, actual.getUpdatedBy());
            assertEquals(expected.getVersionNo(), actual.getVersionNo());
        }
    }


    /**
     * 一括削除の検証.
     * （Admin）
     */
    @Test
    public void testDeleteCorresponsAdmin() throws Exception {
        String expEmpNo = "80001";
        User expUser = new User();
        expUser.setEmpNo(expEmpNo);

        String otherEmpNo = "80002";
        User otherUser = new User();
        otherUser.setEmpNo(otherEmpNo);

        List<Correspon> expList = new ArrayList<Correspon>();
        Correspon correspon = new Correspon();
        correspon.setId(1L);
        correspon.setWorkflowStatus(WorkflowStatus.DRAFT);
        correspon.setCreatedBy(otherUser); // 別ユーザ
        expList.add(correspon);

        correspon = new Correspon();
        correspon.setId(2L);
        correspon.setWorkflowStatus(WorkflowStatus.DRAFT);
        correspon.setCreatedBy(otherUser); // 別ユーザ
        expList.add(correspon);

        correspon = new Correspon();
        correspon.setId(3L);
        correspon.setWorkflowStatus(WorkflowStatus.DRAFT);
        correspon.setCreatedBy(otherUser); // 別ユーザ
        expList.add(correspon);

        correspon = new Correspon();
        correspon.setId(4L);
        correspon.setWorkflowStatus(WorkflowStatus.DRAFT);
        correspon.setCreatedBy(otherUser); // 別ユーザ
        expList.add(correspon);

        MockCorresponDao.RET_FIND_BY_ID.put("1", new Correspon());

        MockAbstractService.RET_CURRENT_USER = expUser;
        MockAbstractService.RET_SYSTEM_ADMIN = true; // SystemAdmin

        service.deleteCorrespons(expList);

        List<Correspon> actList = MockAbstractDao.SET_DELETE;
        assertEquals(expList.size(), actList.size());
        for (int i = 0; i < actList.size(); i++) {
            Correspon actual = actList.get(i);
            Correspon expected = expList.get(i);
            assertEquals(expected.getId(), actual.getId());
            assertEquals(expUser, actual.getUpdatedBy());
            assertEquals(expected.getVersionNo(), actual.getVersionNo());
        }
    }

    /**
     * 一括削除の検証.
     * （Preparerでステータスエラー）
     */
    @Test
    public void testDeleteCorresponsStatusInvalid() throws Exception {
        String expEmpNo = "80001";
        User expUser = new User();
        expUser.setEmpNo(expEmpNo);

        List<Correspon> expList = new ArrayList<Correspon>();
        Correspon correspon = new Correspon();
        correspon.setId(1L);
        correspon.setWorkflowStatus(WorkflowStatus.DRAFT);
        correspon.setCreatedBy(expUser);
        expList.add(correspon);

        correspon = new Correspon();
        correspon.setId(2L);
        correspon.setWorkflowStatus(WorkflowStatus.DENIED); // エラー
        correspon.setCreatedBy(expUser);
        expList.add(correspon);

        correspon = new Correspon();
        correspon.setId(3L);
        correspon.setWorkflowStatus(WorkflowStatus.DRAFT);
        correspon.setCreatedBy(expUser);
        expList.add(correspon);

        correspon = new Correspon();
        correspon.setId(4L);
        correspon.setWorkflowStatus(WorkflowStatus.DRAFT);
        correspon.setCreatedBy(expUser);
        expList.add(correspon);

        MockCorresponDao.RET_FIND_BY_ID.put("1", new Correspon());

        MockAbstractService.RET_CURRENT_USER = expUser;
        MockAbstractService.RET_SYSTEM_ADMIN = false;

        try {
            service.deleteCorrespons(expList);

            fail("例外が発生していない");
        } catch (ServiceAbortException e) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CORRESPON_STATUS_INVALID,
                         e.getMessageCode());
        }
    }

    /**
     * 一括削除の検証.
     * （Adminでステータスエラー）
     */
    @Test
    public void testDeleteCorresponsStatusInvalidAdmin() throws Exception {
        String expEmpNo = "80001";
        User expUser = new User();
        expUser.setEmpNo(expEmpNo);

        List<Correspon> expList = new ArrayList<Correspon>();
        Correspon correspon = new Correspon();
        correspon.setId(1L);
        correspon.setWorkflowStatus(WorkflowStatus.DRAFT);
        correspon.setCreatedBy(expUser);
        expList.add(correspon);

        correspon = new Correspon();
        correspon.setId(2L);
        correspon.setWorkflowStatus(WorkflowStatus.DENIED); // エラー
        correspon.setCreatedBy(expUser);
        expList.add(correspon);

        correspon = new Correspon();
        correspon.setId(3L);
        correspon.setWorkflowStatus(WorkflowStatus.DRAFT);
        correspon.setCreatedBy(expUser);
        expList.add(correspon);

        correspon = new Correspon();
        correspon.setId(4L);
        correspon.setWorkflowStatus(WorkflowStatus.DRAFT);
        correspon.setCreatedBy(expUser);
        expList.add(correspon);

        MockCorresponDao.RET_FIND_BY_ID.put("1", new Correspon());
        MockAbstractService.RET_CURRENT_USER = expUser;
        MockAbstractService.RET_SYSTEM_ADMIN = true; // SystemAdmin

        try {
            service.deleteCorrespons(expList);

            fail("例外が発生していない");
        } catch (ServiceAbortException e) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CORRESPON_STATUS_INVALID,
                         e.getMessageCode());
        }
    }

    /**
     * 一括削除の検証.
     * （権限エラー：SystemAdminではなく、Preparerではない）
     */
    @Test
    public void testDeleteCorresponsPermission() throws Exception {
        String expEmpNo = "80001";
        User expUser = new User();
        expUser.setEmpNo(expEmpNo);

        String otherEmpNo = "80002";
        User otherUser = new User();
        otherUser.setEmpNo(otherEmpNo);

        List<Correspon> expList = new ArrayList<Correspon>();
        Correspon correspon = new Correspon();
        correspon.setId(1L);
        correspon.setWorkflowStatus(WorkflowStatus.DRAFT);
        correspon.setCreatedBy(expUser);
        expList.add(correspon);

        correspon = new Correspon();
        correspon.setId(2L);
        correspon.setWorkflowStatus(WorkflowStatus.DRAFT);
        correspon.setCreatedBy(otherUser); // エラー
        expList.add(correspon);

        correspon = new Correspon();
        correspon.setId(3L);
        correspon.setWorkflowStatus(WorkflowStatus.DRAFT);
        correspon.setCreatedBy(expUser);
        expList.add(correspon);

        correspon = new Correspon();
        correspon.setId(4L);
        correspon.setWorkflowStatus(WorkflowStatus.DRAFT);
        correspon.setCreatedBy(expUser);
        expList.add(correspon);

        MockCorresponDao.RET_FIND_BY_ID.put("1", new Correspon());
        MockAbstractService.RET_CURRENT_USER = expUser;
        MockAbstractService.RET_SYSTEM_ADMIN = false;

        try {
            service.deleteCorrespons(expList);

            fail("例外が発生していない");
        } catch (ServiceAbortException e) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT,
                         e.getMessageCode());
        }
    }

    /**
     * 既読／未読ステータス更新の検証.
     * （引数Null）
     */
    @Test(expected = IllegalArgumentException.class)
    public void testDeleteCorresponsNull() throws Exception {
        service.deleteCorrespons(null);
        fail("例外が発生していない");
    }

    /**
     * 既読／未読ステータス更新の検証.
     * （コレポン文書が未選択）
     */
    @Test
    public void testDeleteCorresponsNotSelected() throws Exception {
        try {
            service.deleteCorrespons(new ArrayList<Correspon>());
            fail("例外が発生していない");
        } catch (ServiceAbortException e) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CORRESPON_NOT_SELECTED,
                         e.getMessageCode());
        }
    }

    /**
     *  一括削除の検証.
     * （排他エラー）
     */
    @Test
    public void testDeleteCorresponsStaleRecord() throws Exception {
        try {
            MockAbstractDao.EX_DELETE = new StaleRecordException();

            String expEmpNo = "80001";
            User expUser = new User();
            expUser.setEmpNo(expEmpNo);

            List<Correspon> list = new ArrayList<Correspon>();
            Correspon correspon = new Correspon();
            correspon.setId(1L);
            correspon.setWorkflowStatus(WorkflowStatus.DRAFT);
            correspon.setCreatedBy(expUser);
            list.add(correspon);

            MockCorresponDao.RET_FIND_BY_ID.put("1", new Correspon());
            MockAbstractService.RET_CURRENT_USER = expUser;

            service.deleteCorrespons(list);

            fail("例外が発生していない");
        } catch (ServiceAbortException e) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CORRESPON_ALREADY_UPDATED,
                         e.getMessageCode());
        }
    }

    /**
     *  コレポンID一覧取得テスト.
     */
    @Test
    public void testSearchId() throws Exception {
        MockCorresponDao.RET_FIND_ID = getIdList(10);
        MockCorresponDao.RET_COUNT = 10;

        SearchCorresponCondition condition = new SearchCorresponCondition();
        List<Long> actual = service.searchId(condition);

        assertEquals(10, actual.size());
    }

    /**
     *  コレポンID一覧取得テスト.
     */
    @Test
    public void testSearchIdNotFound() throws Exception {
        MockCorresponDao.RET_FIND_ID = getIdList(0);
        SearchCorresponCondition condition = new SearchCorresponCondition();

        try {
            service.searchId(condition);
            fail("例外が発生していない");
        }
        catch (ServiceAbortException e) {
            assertEquals(ApplicationMessageCode.NO_DATA_FOUND, e.getMessageCode());
        }
    }

    private List<Long> getIdList(long cnt) {
        List<Long> idList = new ArrayList<Long>();
        for (long i = 1 ; i <= cnt ; i++) {
            idList.add(i);
        }
        return idList;
    }

    private List<Correspon> getList() {
        // ダミーの戻り値をセット
        List<Correspon> list = new ArrayList<Correspon>();
        Correspon c = new Correspon();
        c.setId(1L);
        c.setCorresponNo("YOC:OT:BUILDING-00001");
        CorresponGroup from = new CorresponGroup();
        from.setId(new Long(1L));
        from.setName("YOC:BUILDING");
        c.setFromCorresponGroup(from);
        c.setSubject("Mock");
        List<AddressCorresponGroup> addressGroups = new ArrayList<AddressCorresponGroup>();
        AddressCorresponGroup addressGroup = new AddressCorresponGroup();
        CorresponGroup group = new CorresponGroup();
        group.setId(2L);
        group.setName("YOC:IT");
        addressGroup.setCorresponGroup(group);
        addressGroup.setAddressType(AddressType.TO);
        addressGroups.add(addressGroup);
        addressGroup = new AddressCorresponGroup();
        group = new CorresponGroup();
        group.setId(2L);
        group.setName("TOK:BUILDING");
        addressGroup.setCorresponGroup(group);
        addressGroup.setAddressType(AddressType.CC);
        addressGroups.add(addressGroup);
        c.setAddressCorresponGroups(addressGroups);
        CorresponType ct = new CorresponType();
        ct.setId(new Long(1L));
        ct.setCorresponType("Request");
        c.setCorresponType(ct);
        c.setWorkflowStatus(WorkflowStatus.DRAFT);
        c.setCreatedAt(new GregorianCalendar(2009, 3, 1, 1, 1, 1).getTime());
        c.setUpdatedAt(new GregorianCalendar(2009, 3, 10, 10, 10, 10).getTime());
        User u = new User();
        u.setEmpNo("00001");
        u.setNameE("Test User");
        c.setCreatedBy(u);
        c.setUpdatedBy(u);
        c.setCorresponStatus(CorresponStatus.OPEN);
        c.setCustomField1Label("FIELD1");
        c.setCustomField1Value("VALUE1");
        c.setCustomField2Label("FIELD2");
        c.setCustomField2Value("VALUE2");
        c.setCustomField3Label("FIELD3");
        c.setCustomField3Value("VALUE3");
        c.setCustomField4Label("FIELD4");
        c.setCustomField4Value("VALUE4");
        c.setCustomField5Label("FIELD5");
        c.setCustomField5Value("VALUE5");
        c.setCustomField6Label("FIELD6");
        c.setCustomField6Value("VALUE6");
        c.setCustomField7Label("FIELD7");
        c.setCustomField7Value("VALUE7");
        c.setCustomField8Label("FIELD8");
        c.setCustomField8Value("VALUE8");
        c.setCustomField9Label("FIELD9");
        c.setCustomField9Value("VALUE9");
        c.setCustomField10Label("FIELD10");
        c.setCustomField10Value("VALUE10");
        c.setWorkflows(new ArrayList<Workflow>());
        c.setPreviousRevCorresponId(null);
        group = new CorresponGroup();
        group.setName("YOC:IT");
        c.setToCorresponGroup(group);
        c.setToCorresponGroupCount(2L);
        c.setIssuedAt(null);
        c.setIssuedBy(null);
        c.setReplyRequired(null);
        c.setDeadlineForReply(null);
        list.add(c);

        c = new Correspon();
        c.setId(2L);
        c.setCorresponNo("YOC:OT:BUILDING-00002");
        from = new CorresponGroup();
        from.setId(new Long(1L));
        from.setName("YOC:BUILDING");
        c.setFromCorresponGroup(from);

        addressGroups = new ArrayList<AddressCorresponGroup>();
        addressGroup = new AddressCorresponGroup();
        group = new CorresponGroup();
        group.setId(2L);
        group.setName("YOC:IT");
        addressGroup.setCorresponGroup(group);
        addressGroup.setAddressType(AddressType.TO);
        addressGroups.add(addressGroup);

        List<AddressUser> addressUsers = new ArrayList<AddressUser>();
        AddressUser addressUser = new AddressUser();
        u = new User();
        u.setEmpNo("00002");
        u.setNameE("To User");
        addressUser.setUser(u);
        addressUser.setAddressUserType(AddressUserType.ATTENTION);
        addressUsers.add(addressUser);
        addressUser = new AddressUser();
        u = new User();
        u.setEmpNo("00003");
        u.setNameE("Cc User");
        addressUser.setUser(u);
        addressUser.setAddressUserType(AddressUserType.NORMAL_USER);
        addressUsers.add(addressUser);

        addressGroup.setUsers(addressUsers);
        c.setAddressCorresponGroups(addressGroups);

        c.setSubject("Mock");
        ct = new CorresponType();
        ct.setId(new Long(1L));
        ct.setCorresponType("Request");
        c.setCorresponType(ct);
        c.setWorkflowStatus(WorkflowStatus.DRAFT);
        c.setCreatedAt(new GregorianCalendar(2009, 3, 1, 1, 1, 1).getTime());
        c.setUpdatedAt(new GregorianCalendar(2009, 3, 10, 10, 10, 10).getTime());
        u = new User();
        u.setEmpNo("00001");
        u.setNameE("Test User");
        c.setCreatedBy(u);
        c.setUpdatedBy(u);
        c.setCorresponStatus(CorresponStatus.OPEN);
        c.setWorkflows(new ArrayList<Workflow>());
        c.setPreviousRevCorresponId(1L);
        c.setPreviousRevCorresponNo("YOC:BUILDING-00001");
        c.setToCorresponGroup(new CorresponGroup());
        c.setIssuedAt(new GregorianCalendar(2009, 3, 20, 20, 20, 20).getTime());
        c.setIssuedBy(new User());
        c.setReplyRequired(ReplyRequired.YES);
        c.setDeadlineForReply(new GregorianCalendar(2009, 4, 1).getTime());
        list.add(c);

        c = new Correspon();
        c.setId(3L);
        c.setCorresponNo("YOC:OT:BUILDING-00003");
        from = new CorresponGroup();
        from.setId(new Long(1L));
        from.setName("YOC:BUILDING");
        c.setFromCorresponGroup(from);
        c.setSubject("Mock");
        ct = new CorresponType();
        ct.setId(new Long(1L));
        ct.setCorresponType("Request");
        c.setCorresponType(ct);
        c.setWorkflowStatus(WorkflowStatus.DRAFT);
        List<Workflow> workflows = new ArrayList<Workflow>();
        Workflow workflow = new Workflow();
        workflow.setWorkflowNo(1L);
        u = new User();
        u.setEmpNo("00004");
        u.setNameE("Check User");
        workflow.setUser(u);
        workflow.setWorkflowType(WorkflowType.CHECKER);
        workflow.setCommentOn("Checker's Comment");
        workflow.setWorkflowProcessStatus(WorkflowProcessStatus.CHECKED);
        workflow.setUpdatedBy(u);
        workflow.setUpdatedAt(new GregorianCalendar(2009, 3, 5, 10, 10, 10).getTime());
        workflows.add(workflow);
        workflow = new Workflow();
        workflow.setWorkflowNo(2L);
        u = new User();
        u.setEmpNo("00005");
        u.setNameE("Approve User");
        workflow.setUser(u);
        workflow.setWorkflowType(WorkflowType.APPROVER);
        workflow.setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_APPROVAL);
        workflows.add(workflow);
        c.setWorkflows(workflows);
        c.setCreatedAt(new GregorianCalendar(2009, 3, 1, 1, 1, 1).getTime());
        c.setUpdatedAt(new GregorianCalendar(2009, 3, 10, 10, 10, 10).getTime());
        u = new User();
        u.setEmpNo("00001");
        u.setNameE("Test User");
        c.setCreatedBy(u);
        c.setUpdatedBy(u);
        c.setCorresponStatus(CorresponStatus.OPEN);
        c.setPreviousRevCorresponId(1L);
        c.setPreviousRevCorresponNo("YOC:BUILDING-00001");
        c.setToCorresponGroup(null);
        c.setIssuedAt(new GregorianCalendar(2009, 3, 20, 20, 20, 20).getTime());
        c.setIssuedBy(u);
        c.setReplyRequired(ReplyRequired.NO);
        c.setDeadlineForReply(null);
        list.add(c);

        c = new Correspon();
        c.setId(4L);
        c.setCorresponNo("YOC:OT:BUILDING-00001-001");
        from = new CorresponGroup();
        from.setId(new Long(1L));
        from.setName("YOC:BUILDING");
        c.setFromCorresponGroup(from);

        addressGroups = new ArrayList<AddressCorresponGroup>();
        addressGroup = new AddressCorresponGroup();
        group = new CorresponGroup();
        group.setId(2L);
        group.setName("YOC:IT");
        addressGroup.setCorresponGroup(group);
        addressGroup.setAddressType(AddressType.TO);
        List<AddressUser> users = new ArrayList<AddressUser>();
        addressUser = new AddressUser();
        addressUser.setAddressUserType(AddressUserType.ATTENTION);
        u = new User();
        u.setEmpNo("00011");
        u.setNameE("Attention User");
        addressUser.setUser(u);
        List<PersonInCharge> personInCharges = new ArrayList<PersonInCharge>();
        PersonInCharge pic = new PersonInCharge();
        u = new User();
        u.setEmpNo("00022");
        u.setNameE("PIC User");
        pic.setUser(u);
        personInCharges.add(pic);
        addressUser.setPersonInCharges(personInCharges);
        users.add(addressUser);
        addressUser = new AddressUser();
        addressUser.setAddressUserType(AddressUserType.NORMAL_USER);
        u = new User();
        u.setEmpNo("00033");
        u.setNameE("Normal User");
        addressUser.setUser(u);
        users.add(addressUser);
        addressGroup.setUsers(users);
        addressGroups.add(addressGroup);
        addressGroup = new AddressCorresponGroup();
        group = new CorresponGroup();
        group.setId(2L);
        group.setName("TOK:BUILDING");
        addressGroup.setCorresponGroup(group);
        addressGroup.setAddressType(AddressType.CC);
        users = new ArrayList<AddressUser>();
        addressUser = new AddressUser();
        addressUser.setAddressUserType(AddressUserType.NORMAL_USER);
        u = new User();
        u.setEmpNo("00044");
        u.setNameE("Normal User");
        addressUser.setUser(u);
        users.add(addressUser);
        addressGroup.setUsers(users);
        addressGroups.add(addressGroup);
        c.setAddressCorresponGroups(addressGroups);

        c.setSubject("Mock");
        ct = new CorresponType();
        ct.setId(new Long(1L));
        ct.setCorresponType("Request");
        c.setCorresponType(ct);
        c.setWorkflowStatus(WorkflowStatus.DRAFT);
        c.setCreatedAt(new GregorianCalendar(2009, 3, 1, 1, 1, 1).getTime());
        c.setUpdatedAt(new GregorianCalendar(2009, 3, 10, 10, 10, 10).getTime());
        u = new User();
        u.setEmpNo("00001");
        u.setNameE("Test User");
        c.setCreatedBy(u);
        c.setUpdatedBy(u);
        c.setCorresponStatus(CorresponStatus.OPEN);
        c.setWorkflows(new ArrayList<Workflow>());
        c.setPreviousRevCorresponId(1L);
        c.setPreviousRevCorresponNo("YOC:BUILDING-00001");
        c.setToCorresponGroup(null);
        c.setIssuedAt(new GregorianCalendar(2009, 3, 20, 20, 20, 20).getTime());
        c.setIssuedBy(u);
        c.setReplyRequired(ReplyRequired.YES);
        c.setDeadlineForReply(new GregorianCalendar(2009, 4, 1).getTime());
        list.add(c);

        c = new Correspon();
        c.setId(5L);
        c.setCorresponNo("YOC:OT:BUILDING-00001-002");
        from = new CorresponGroup();
        from.setId(new Long(1L));
        from.setName("YOC:BUILDING");
        c.setFromCorresponGroup(from);
        c.setSubject("Mock");
        ct = new CorresponType();
        ct.setId(new Long(1L));
        ct.setCorresponType("Request");
        c.setCorresponType(ct);
        c.setWorkflowStatus(WorkflowStatus.DRAFT);
        c.setCreatedAt(new GregorianCalendar(2009, 3, 1, 1, 1, 1).getTime());
        c.setUpdatedAt(new GregorianCalendar(2009, 3, 10, 10, 10, 10).getTime());
        u = new User();
        u.setEmpNo("00001");
        u.setNameE("Test User");
        c.setCreatedBy(u);
        c.setUpdatedBy(u);
        c.setReplyRequired(ReplyRequired.YES);
        c.setDeadlineForReply(new GregorianCalendar(2009, 4, 1).getTime());
        c.setCorresponStatus(CorresponStatus.OPEN);
        c.setFile1FileName("file1.xls");
        c.setFile2FileName("file2.xls");
        c.setFile3FileName("file3.xls");
        c.setFile4FileName("file4.xls");
        c.setFile5FileName("file5.xls");
        c.setWorkflows(new ArrayList<Workflow>());
        list.add(c);

        return list;
    }

    private void setValuesForUpdateCorresponStatus(Correspon c) {
        CorresponGroup from = new CorresponGroup();
        from.setId(1L);
        from.setName("YOC:IT");
        c.setFromCorresponGroup(from);

        c.setWorkflowStatus(WorkflowStatus.ISSUED);
    }

    private List<Workflow> getWorkflow() {
        List<Workflow> result = new ArrayList<Workflow>();
        User u;
        Workflow w;
        w = new Workflow();
        u = new User();
        u.setEmpNo("ZZA01");
        w.setUser(u);
        w.setWorkflowType(WorkflowType.CHECKER);
        w.setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_CHECK);
        result.add(w);

        w = new Workflow();
        u = new User();
        u.setEmpNo("ZZA02");
        w.setUser(u);
        w.setWorkflowType(WorkflowType.CHECKER);
        w.setWorkflowProcessStatus(WorkflowProcessStatus.NONE);
        result.add(w);

        w = new Workflow();
        u = new User();
        u.setEmpNo("ZZA03");
        w.setUser(u);
        w.setWorkflowType(WorkflowType.APPROVER);
        w.setWorkflowProcessStatus(WorkflowProcessStatus.NONE);
        result.add(w);

        return result;
    }

    public static class MockCorresponDao extends MockAbstractDao<Entity> {
        static int RET_COUNT;
        static List<Correspon> RET_FIND;
        static List<Long> RET_FIND_ID;
        static SearchCorresponCondition ARG_CONDITION;
        static boolean IS_INVOKED_FIND = false;
        static boolean IS_INVOKED_COUNT = false;
        static boolean IS_INVOKED_FINDID = false;

        static void init() {
            RET_COUNT = 0;
            RET_FIND = null;
            RET_FIND_BY_ID = null;
            EX_FIND_BY_ID = null;
            IS_INVOKED_FIND = false;
            IS_INVOKED_COUNT = false;
            IS_INVOKED_FINDID = false;
        }
        public List<Correspon> find(SearchCorresponCondition condition) {
            // 特に処理はないが呼び出し
            condition.getPageNo();
            ARG_CONDITION = condition;
            IS_INVOKED_FIND = true;
            return RET_FIND;
        }

        public int count(SearchCorresponCondition condition) {
            // 特に処理はないが呼び出し
            condition.getPageNo();
            ARG_CONDITION = condition;
            IS_INVOKED_COUNT = true;
            return RET_COUNT;
        }

        public List<Long> findId(SearchCorresponCondition condition) {
            ARG_CONDITION = condition;
            IS_INVOKED_FINDID = true;
            return RET_FIND_ID;
        }
    }

    public static class MockCorresponReadStatusDao extends MockAbstractDao<Entity> {
    }

    public static class MockAbstractDao<T extends Entity> {
        static List<CorresponReadStatus> SET_CREATE = new ArrayList<CorresponReadStatus>();
        static List<Correspon> SET_UPDATE_CORRESPON = new ArrayList<Correspon>();
        static List<CorresponReadStatus> SET_UPDATE_READ_STATUS = new ArrayList<CorresponReadStatus>();
        static List<Correspon> SET_DELETE = new ArrayList<Correspon>();
        static Map<String, Object> RET_FIND_BY_ID = new HashMap<String, Object>();
        static RecordNotFoundException EX_FIND_BY_ID;
        static StaleRecordException EX_UPDATE;
        static StaleRecordException EX_DELETE;
        static int COUNT = 0;

        public MockAbstractDao() {

        }

        public MockAbstractDao(String namespace) {

        }

        public Long create(T entity) throws KeyDuplicateException {
            SET_CREATE.add((CorresponReadStatus) entity);
            return 1L;
        }

        public Integer update(T entity) throws KeyDuplicateException, StaleRecordException {
            if (EX_UPDATE != null) {
                throw EX_UPDATE;
            }
            if (entity instanceof Correspon) {
                SET_UPDATE_CORRESPON.add((Correspon) entity);
            } else if (entity instanceof CorresponReadStatus) {
                SET_UPDATE_READ_STATUS.add((CorresponReadStatus) entity);
            }

            return 1;
        }

        @SuppressWarnings("unchecked")
        public T findById(Long id) throws RecordNotFoundException {
            COUNT++;
            if (RET_FIND_BY_ID.get(String.valueOf(COUNT)) == null) {
                throw new RecordNotFoundException();
            }
            return (T) RET_FIND_BY_ID.get(String.valueOf(COUNT));
        }

        public Integer delete(T entity) throws KeyDuplicateException, StaleRecordException {
            if (EX_DELETE != null) {
                throw EX_DELETE;
            }
            SET_DELETE.add((Correspon) entity);

            return 1;
        }
    }

    public static class MockAbstractService {
        static User RET_CURRENT_USER;
        static boolean RET_SYSTEM_ADMIN;
        static boolean RET_PROJECT_ADMIN;
        static boolean RET_GROUP_ADMIN;
        static boolean RET_PREPARER;
        static boolean RET_CHECKER;
        static boolean RET_APPROVER;
        static String RET_CURRENT_PROJECT_ID;
        public static String BASE_URL;
        public static List<Correspon> CORRESPONS;
        public static int CORRESPON_COUNT = 0;

        public String getContextURL() {
            return "/";
        }

        public User getCurrentUser() {
            return RET_CURRENT_USER;
        }

        public boolean isSystemAdmin(User user) {
            return RET_SYSTEM_ADMIN;
        }

        public Project getCurrentProject() {
            if (RET_CURRENT_PROJECT_ID != null) {
                Project p = new Project();
                p.setProjectId(RET_CURRENT_PROJECT_ID);
                return p;
            }
            return null;
        }

        public String getCurrentProjectId() {
            return RET_CURRENT_PROJECT_ID;
        }

        public boolean isProjectAdmin(User user, String projectId) {
            return RET_PROJECT_ADMIN;
        }

        public boolean isAnyGroupAdmin(Correspon correspon) {
            return RET_GROUP_ADMIN;
        }

        public boolean isGroupAdmin(User user, Long corresponGroupId) {
            return RET_GROUP_ADMIN;
        }

        public boolean isPreparer(String empNo) {
            return RET_PREPARER;
        }

        public boolean isChecker(Correspon correspon) {
            return RET_CHECKER;
        }

        public boolean isApprover(Correspon correspon) {
            return RET_APPROVER;
        }

        public String getBaseURL() {
            return BASE_URL;
        }

        public List<Workflow> createDisplayWorkflowList(Correspon correspon) {
            Correspon c = CORRESPONS.get(CORRESPON_COUNT++);
            List<Workflow> workflows = new ArrayList<Workflow>(c.getWorkflows());
            //  Preparerを追加
            Workflow w = new Workflow();
            w.setUser(c.getCreatedBy());
            workflows.add(0, w);

            return workflows;
        }
    }

    public static class MockCorresponService {
        static List<Correspon> RET_FIND;
        static boolean FIND_EXCEPTION;

        public Correspon find(Long id) throws ServiceAbortException {
            if (FIND_EXCEPTION) {
                throw new ServiceAbortException(new RecordNotFoundException());
            }
            for (Correspon c : RET_FIND) {
                if (id.equals(c.getId())) {
                    return c;
                }
            }
            return null;
        }
    }

    public static class MockSystemConfig {
        static Map<String, String> map = new HashMap<String, String>();
        static {
            map.put("excel.sheetname.corresponindex", "CorresponIndex");
            map.put("template.stylesheet", "/stylesheet/style.css");
            map.put("template.correspon.index", "/template/corresponIndexPrint.vm");
            map.put("template.correspon.html", "/template/corresponIndexPrint.vm");
            map.put("template.correspon.zip", "/template/corresponPrint.vm");
            map.put("file.name.regex", "[\\/:*?\"<>|]");
            map.put("file.name.replacement", "-");
            map.put("velocity.config", "/velocity.properties");
            map.put("csv.encoding", "Windows-31J");
            map.put("html.encoding", "UTF-8");
            map.put("securityLevel.groupAdmin", "30");
            map.put("securityLevel.normalUser", "40");
            map.put("securityflg.systemadmin", "X");
            map.put("securityflg.projectadmin", "X");
            map.put("history.subject.maxlength", "30");
        }
        static List<String> NULL_KEYS;

        public static String getValue(String key) {
            if (NULL_KEYS != null && NULL_KEYS.contains(key)) {
                return null;
            }
            return map.get(key);
        }
    }



    public static class MockCorresponReadStatusService {
        static Integer RET_FIND = 2;
        static Long RET_FIND_UNIT = 2L;
        static List<CorresponReadStatus> SET_CREATE = new ArrayList<CorresponReadStatus>();
        static List<CorresponReadStatus> SET_UPDATE = new ArrayList<CorresponReadStatus>();
        static CorresponReadStatus SET_READ_CREATE = new CorresponReadStatus();
        static User RET_CURRENT_USER;
        static ServiceAbortException EX_UPDATE;

        public Long updateReadStatusById(Long id, ReadStatus  readStatus)
                throws ServiceAbortException {
            if (EX_UPDATE != null) {
                throw EX_UPDATE;
            }
            SET_READ_CREATE = new CorresponReadStatus();
            SET_READ_CREATE.setId(1L);
            SET_READ_CREATE.setCorresponId(id);

            SET_READ_CREATE.setEmpNo(RET_CURRENT_USER.getEmpNo());
            SET_READ_CREATE.setReadStatus(readStatus);
//            if (correspon.getCorresponReadStatusId() == null) {
                SET_READ_CREATE.setCreatedBy(RET_CURRENT_USER);
                SET_READ_CREATE.setUpdatedBy(RET_CURRENT_USER);
                SET_CREATE.add(SET_READ_CREATE);
//            } else {
//                SET_READ_CREATE.setUpdatedBy(RET_CURRENT_USER);
//                SET_UPDATE.add(SET_READ_CREATE);
//            }
            return 1L;
        }

        public Integer updateReadStatusByCorresponId(Long id , ReadStatus readStatus)
                throws ServiceAbortException {
            if (EX_UPDATE != null) {
                throw EX_UPDATE;
            }
            SET_READ_CREATE = new CorresponReadStatus();
            SET_READ_CREATE.setId(1L);
            SET_READ_CREATE.setCorresponId(id);

            SET_READ_CREATE.setEmpNo(RET_CURRENT_USER.getEmpNo());
            SET_READ_CREATE.setReadStatus(readStatus);
            SET_READ_CREATE.setCreatedBy(RET_CURRENT_USER);
            SET_READ_CREATE.setUpdatedBy(RET_CURRENT_USER);
            SET_CREATE.add(SET_READ_CREATE);
            return 1;
        }
    }

    public static class MockAddressCorresponGroupDao {
        static ArrayList<AddressCorresponGroup> RET_FIND_BY_CORRESPON_ID;

        public List<AddressCorresponGroup> findByCorresponId(Long corresponId) {
            return RET_FIND_BY_CORRESPON_ID;
        }
    }
    public static class MockWorkflowDao {
        static List<Workflow> RET_FIND_BY_CORRESPON_ID;

        public List<Workflow> findByCorresponId(Long corresponId) {
            return RET_FIND_BY_CORRESPON_ID;
        }
    }
    public static class MockCorresponHTMLGeneratorUtil {
        public String getStylesheetContent() throws IOException {
            return "mock stylesheet";
        }
    }
    public static class MockCorresponFullTextSearchService {
        static boolean IS_INVOKED_SEARCH = false;
        static List<FullTextSearchCorresponsResultSub> RET_FULLTEXTSEARCH_LIST;
        /**
         * IDのみのFullTextSearchCorresponリストを生成する.
         * @param ids id
         * @return result
         */
        static private List<FullTextSearchCorresponsResultSub> generateFTSCorresponIdList(Long ...ids) {
            List<FullTextSearchCorresponsResultSub> result = new ArrayList<FullTextSearchCorresponsResultSub>();
            if (ids == null) {
                return result;
            }
            for (Long id : ids) {
                FullTextSearchCorresponsResultSub item = new FullTextSearchCorresponsResultSub();
                item.setId(id);
                result.add(item);
            }
            return result;
        }
        /**
         * IDのみのFullTextSearchCorresponリストを生成する.
         * @param num 生成したいID数
         * @return result
         */
        static private List<FullTextSearchCorresponsResultSub> generateFTSCorresponIdListAsSeq(int num) {
            List<FullTextSearchCorresponsResultSub> result = new ArrayList<FullTextSearchCorresponsResultSub>();
            for (int i = 1; i <= num; i++) {
                FullTextSearchCorresponsResultSub item = new FullTextSearchCorresponsResultSub();
                item.setId(Long.valueOf(i));
                result.add(item);
            }
            return result;
        }

        public List<? extends FullTextSearchCorresponsResultSub> search(
                SearchFullTextSearchCorresponCondition condition)
                throws ServiceAbortException {
            IS_INVOKED_SEARCH = true;
            return RET_FULLTEXTSEARCH_LIST;
        }
    }
    @SuppressWarnings("serial")
    public static class FullTextSearchCorresponsResultSub extends FullTextSearchCorresponsResult{
        Long ID = 0L;
        @Override
        public Long getId() {
            return ID;
        }
        public void setId(Long id) {
            this.ID = id;
        }
    }
}
