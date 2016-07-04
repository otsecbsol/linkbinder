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
package jp.co.opentone.bsol.linkbinder.view.admin;

import static jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.annotation.Resource;
import javax.faces.application.FacesMessage;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import jp.co.opentone.bsol.framework.core.exception.InvalidOperationRuntimeException;
import jp.co.opentone.bsol.framework.core.message.Messages;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.core.util.DateUtil;
import jp.co.opentone.bsol.framework.extension.jsf.FacesContextMock;
import jp.co.opentone.bsol.framework.test.AbstractTestCase;
import jp.co.opentone.bsol.framework.web.view.util.ViewHelper;
import jp.co.opentone.bsol.linkbinder.dto.Discipline;
import jp.co.opentone.bsol.linkbinder.dto.SearchDisciplineResult;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchDisciplineCondition;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.service.admin.impl.DisciplineServiceImpl;
import jp.co.opentone.bsol.linkbinder.view.AbstractPage;
import mockit.Mock;
import mockit.MockUp;

/**
 * {@link DisciplineIndexPage}のテストケース.
 * @author opentone
 */
public class DisciplineIndexPageTest extends AbstractTestCase {

    /**
     * テスト対象クラス
     */
    @Resource
    private DisciplineIndexPage page;

    /**
     * 検証用データ
     */
    private List<Discipline> list;

    /**
     * テストの前準備.
     */
    @BeforeClass
    public static void testSetUp() {
        FacesContextMock.initialize();
        new MockAbstractPage();
        new MockDisciplineService();
        new MockViewHelper();
        new MockDateUtil();
    }

    /**
     * 後始末.
     */
    @AfterClass
    public static void testTeardown() {
        new MockAbstractPage().tearDown();
        new MockDisciplineService().tearDown();
        new MockViewHelper().tearDown();
        new MockDateUtil().tearDown();
        FacesContextMock.tearDown();
    }

    @Before
    public void setUp() {
        list = getList();
    }

    @After
    public void tearDown() {
        MockAbstractPage.RET_PROJID = null;
        MockDisciplineService.RET_SEARCH = null;
        MockDisciplineService.CRT_DELETE = null;
        MockDisciplineService.EX_SEARCH = null;
        MockDisciplineService.SET_SEARCH_ERROR_PAGE = -1;
        FacesContextMock.EXPECTED_MESSAGE = null;
        FacesContextMock.EXPECTED_CLIENT_ID = null;
        page.setCode(null);
        page.setName(null);
        page.setDataModel(null);
        if (page.getDisciplineList() != null) {
            page.getDisciplineList().clear();
        }
    }

    /**
     * 初期化アクションを検証する.
     * @throws Exception
     */
    @Test
    public void testInitialize() throws Exception {
        // テストに必要なデータを作成する
        SearchDisciplineResult result = createDisciplineResult();

        MockDisciplineService.RET_SEARCH = result;
        MockAbstractPage.RET_PROJID = "PJ1";

        // テスト実行
        page.initialize();

        assertEquals(list.toString(), page.getDisciplineList().toString());
        assertEquals(3, page.getDataCount());

        // 初期設定を確認
        assertFalse(page.getNext());
        assertFalse(page.getPrevious());
        assertEquals(10, page.getPageIndex());
        assertEquals(1, page.getPageNo());
        assertEquals(10, page.getPageRowNum());

        // 検索条件
        assertNull(page.getCode());
        assertNull(page.getName());
        assertEquals(1, page.getCondition().getPageNo());
        assertEquals(10, page.getCondition().getPageRowNum());
        assertEquals("PJ1", page.getCondition().getProjectId());
    }

    /**
     * 初期化アクションを検証する.
     * プロジェクト未選択の場合.
     * @throws Exception
     */
    @Test
    public void testInitializeProjectNoSelected() throws Exception {
        // テストに必要なデータを作成する
        SearchDisciplineResult result = createDisciplineResult();

        MockDisciplineService.RET_SEARCH = result;
        MockAbstractPage.RET_PROJID = null;

        // 期待されるメッセージをセット
        FacesContextMock.EXPECTED_MESSAGE =
                new FacesMessage(
                    FacesMessage.SEVERITY_ERROR, FacesMessage.SEVERITY_ERROR.toString(),
                    createExpectedMessageString(
                        Messages.getMessageAsString(CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT),
                        "Initialize"));
        // テスト実行
        try {
            page.initialize();
        } catch (InvalidOperationRuntimeException e) {
            ServiceAbortException actual = (ServiceAbortException) e.getCause();
            assertEquals(CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT,
                        actual.getMessageCode());
        }
    }

    /**
     * 検索
     * @throws Exception
     */
    @Test
    public void testSearch() throws Exception {
        // テストに必要なデータを作成
        SearchDisciplineResult result = createDisciplineResult();
        result.getDisciplineList().remove(1);
        result.getDisciplineList().remove(1);
        result.setCount(result.getDisciplineList().size());

        MockDisciplineService.RET_SEARCH = result;
        MockAbstractPage.RET_PROJID = "PJ1";

        page.setCondition(new SearchDisciplineCondition());
        page.setDisciplineList(getList());

        page.setCode("P");

        page.search();

        assertEquals(result.getDisciplineList().toString(), page.getDisciplineList().toString());
        assertEquals(result.getCount(), page.getDataCount());
        // 検索条件
        assertEquals("P", page.getCondition().getDisciplineCd());
        assertNull(page.getCondition().getName());
    }

    /**
     * 前ページ（<<）アクションのテスト.
     * @throws Exception
     */
    @Test
    public void testMovePrevious() throws Exception {
        // テストに必要なデータを作成
        SearchDisciplineResult result = createDisciplineResult();
        result.setCount(30);
        MockDisciplineService.RET_SEARCH = result;
        MockAbstractPage.RET_PROJID = "PJ1";

        /* 全30件を10行ずつ表示 -- 全3ページ */
        page.setPageRowNum(10);
        SearchDisciplineCondition condition = new SearchDisciplineCondition();
        condition.setPageRowNum(10);
        page.setCondition(condition);

        // 3ページ目から1つ戻る
        page.setPageNo(3);
        page.movePrevious();

        assertEquals(2, page.getPageNo());
        assertEquals(list.toString(), page.getDisciplineList().toString());
        assertEquals(30, page.getDataCount());
        // 検索条件
        assertEquals(2, page.getCondition().getPageNo());

        // 再設定
        List<Discipline> newList = getNewList();
        result = new SearchDisciplineResult();
        result.setDisciplineList(newList);
        result.setCount(32); // 増加
        MockDisciplineService.RET_SEARCH = result;

        // さらに1つ戻る
        page.movePrevious();

        assertEquals(1, page.getPageNo());
        assertEquals(newList.toString(), page.getDisciplineList().toString());
        assertEquals(32, page.getDataCount()); // 増加
        // 検索条件
        assertEquals(1, page.getCondition().getPageNo());
    }

    /**
     * 次ページ（>>）アクションのテスト.
     * @throws Exception
     */
    @Test
    public void testMoveNext() throws Exception {
        // ダミー部門情報を設定
        SearchDisciplineResult result = createDisciplineResult();
        result.setCount(30);
        MockDisciplineService.RET_SEARCH = result;
        MockAbstractPage.RET_PROJID = "PJ1";

        /* 全30件を10行ずつ表示 -- 全3ページ */
        page.setPageRowNum(10);
        SearchDisciplineCondition condition = new SearchDisciplineCondition();
        condition.setPageRowNum(10);
        page.setCondition(condition);

        // 1ページ目から1つ進む
        page.setPageNo(1);
        page.moveNext();

        assertEquals(2, page.getPageNo());
        assertEquals(list.toString(), page.getDisciplineList().toString());
        assertEquals(30, page.getDataCount());
        // 検索条件
        assertEquals(2, page.getCondition().getPageNo());

        // 再設定
        List<Discipline> newList = getNewList();
        result = new SearchDisciplineResult();
        result.setDisciplineList(newList);
        result.setCount(32); // 増加
        MockDisciplineService.RET_SEARCH = result;

        // さらに1つ進む
        page.moveNext();

        assertEquals(3, page.getPageNo());
        assertEquals(newList.toString(), page.getDisciplineList().toString());
        assertEquals(32, page.getDataCount()); // 増加
        // 検索条件
        assertEquals(3, page.getCondition().getPageNo());
    }

    /**
     * ページ遷移アクションのテスト.
     * @throws Exception
     */
    @Test
    public void testChangePage() throws Exception {
        // 部門情報を設定
        SearchDisciplineResult result = createDisciplineResult();
        result.setDisciplineList(result.getDisciplineList());
        result.setCount(50);
        MockDisciplineService.RET_SEARCH = result;
        MockAbstractPage.RET_PROJID = "PJ1";

        /* 全50件を10行ずつ表示 -- 全5ページ */
        page.setPageRowNum(10);
        page.setPageIndex(10);
        SearchDisciplineCondition condition = new SearchDisciplineCondition();
        condition.setPageRowNum(10);
        page.setCondition(condition);

        // 4ページ目へ
        page.setPageNo(4);
        page.changePage();

        assertEquals(4, page.getPageNo());
        assertEquals(list.toString(), page.getDisciplineList().toString());
        assertEquals(50, page.getDataCount());
        // 検索条件
        assertEquals(4, page.getCondition().getPageNo());

        // 再設定
        List<Discipline> newList = getNewList();
        result = new SearchDisciplineResult();
        result.setDisciplineList(newList);
        result.setCount(54); // 増加
        MockDisciplineService.RET_SEARCH = result;

        // 2ページ目へ
        page.setPageNo(2);
        page.changePage();

        assertEquals(2, page.getPageNo());
        assertEquals(newList.toString(), page.getDisciplineList().toString());
        assertEquals(54, page.getDataCount()); // 増加
        // 検索条件
        assertEquals(2, page.getCondition().getPageNo());
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
        assertFalse(page.getPrevious());

        for (int i = 2; i < 10; i++) {
            page.setPageNo(i);
            assertTrue(page.getPrevious());
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
            assertTrue(page.getNext());
        }
        // 最終ページのみリンク表示しない
        page.setPageNo(14);
        assertFalse(page.getNext());

        /* 全56件を7件ごと、5ページ分表示の時 */
        page.setPageRowNum(7);
        page.setDataCount(56);
        page.setPageIndex(5);

        for (int i = 1; i < 8; i++) {
            page.setPageNo(i);
            assertTrue(page.getNext());
        }
        // 最終ページのみリンク表示しない
        page.setPageNo(8);
        assertFalse(page.getNext());

        /* 全317件を15件ごと、20ページ分表示の時 */
        page.setPageRowNum(15);
        page.setDataCount(317);
        page.setPageIndex(20);

        for (int i = 1; i < 22; i++) {
            page.setPageNo(i);
            assertTrue(page.getNext());
        }
        // 最終ページのみリンク表示しない
        page.setPageNo(22);
        assertFalse(page.getNext());
    }

    /**
     * Excelダウンロードアクションのテスト.
     * @throws Exception
     */
    @Test
    public void testDownloadExcel() throws Exception {
        // 変換後のデータを作成
        byte[] expected = "TEST-OUT-EXCEL".getBytes();
        MockDisciplineService.RET_EXCEL = expected;

        Date date = new GregorianCalendar(2009, 3, 1, 1, 1, 1).getTime();
        MockDateUtil.RET_DATE = date;

        SearchDisciplineResult result = createDisciplineResult();

        MockDisciplineService.RET_SEARCH = result;
        MockAbstractPage.RET_PROJID = "PJ1";

        page.downloadExcel();

        assertArrayEquals(expected, MockViewHelper.RET_DATA);
        assertEquals("20090401010101.xls", MockViewHelper.RET_FILENAME);

        // 2回目の作成
        byte[] newExpected = "NEW-TEST-OUT-EXCEL".getBytes();
        MockDisciplineService.RET_EXCEL = newExpected;

        Date newDate = new GregorianCalendar(2009, 3, 1, 11, 11, 11).getTime();
        MockDateUtil.RET_DATE = newDate;

        page.downloadExcel();

        assertArrayEquals(newExpected, MockViewHelper.RET_DATA);
        assertEquals("20090401111111.xls", MockViewHelper.RET_FILENAME);
    }

    /**
     * 部門情報削除アクションのテスト.
     * @throws Exception
     */
    @Test
    public void testDelete() throws Exception{
        List<Discipline> lstDis = createDisciplineList();
        page.setDisciplineList(lstDis);
        page.getDataModel().setRowIndex(2); // 3行目を選択

        //比較用
        Discipline deleteDis = lstDis.get(2); // 3行目を選択
        page.setDisciplineRowId(deleteDis.getId());
        page.setDisciplineRowProjId(deleteDis.getProjectId());
        page.setDisciplineRowVerNo(deleteDis.getVersionNo());

        page.setPageIndex(15);
        page.setPageNo(3);
        page.setPageRowNum(25);

        SearchDisciplineResult result = new SearchDisciplineResult();
        result.setDisciplineList(lstDis);
        result.setCount(52);
        MockAbstractPage.RET_PROJID = "PJ1";
        MockDisciplineService.RET_SEARCH = result;

        // 期待されるメッセージをセット
        FacesContextMock.EXPECTED_MESSAGE =
                new FacesMessage(
                    FacesMessage.SEVERITY_INFO, FacesMessage.SEVERITY_INFO.toString(),
                    createExpectedMessageString(
                        Messages.getMessageAsString(DISCIPLINE_DELETED),
                        null));

        page.delete();

        assertFalse(page.getNext());
        assertTrue(page.getPrevious());
        assertEquals(15, page.getPageIndex());
        assertEquals(3, page.getPageNo()); // 同一ページ
        assertEquals(25, page.getPageRowNum());

        // 検索条件
        assertNull(page.getCode());
        assertNull(page.getName());
        assertEquals(3, page.getCondition().getPageNo());
        assertEquals(25, page.getCondition().getPageRowNum());

    }

    /**
     * テストに必要な部門のリストを作成する.
     * @return 部門のリスト
     */
    private List<Discipline> createDisciplineList() {
        List<Discipline> lstDis = new ArrayList<Discipline>();

        Discipline discipline = new Discipline();
        discipline.setId(1L);
        discipline.setDisciplineCd("TEST1");
        discipline.setName("TestDisicpline 1");
        discipline.setVersionNo(1L);
        discipline.setDeleteNo(0L);

        User loginUser = new User();
        loginUser.setEmpNo("ZZA01");
        loginUser.setNameE("Taro Yamada");

        discipline.setCreatedBy(loginUser);
        discipline.setUpdatedBy(loginUser);
        lstDis.add(discipline);

        discipline = new Discipline();
        discipline.setId(2L);
        discipline.setDisciplineCd("TEST2");
        discipline.setName("TestDisicpline 2");
        discipline.setVersionNo(1L);
        discipline.setDeleteNo(0L);

        discipline.setCreatedBy(loginUser);
        discipline.setUpdatedBy(loginUser);
        lstDis.add(discipline);

        discipline = new Discipline();
        discipline.setId(3L);
        discipline.setDisciplineCd("TEST3");
        discipline.setName("TestDisicpline 3");
        discipline.setVersionNo(1L);
        discipline.setDeleteNo(0L);


        discipline.setCreatedBy(loginUser);
        discipline.setUpdatedBy(loginUser);
        lstDis.add(discipline);

        discipline = new Discipline();
        discipline.setId(4L);
        discipline.setDisciplineCd("TEST4");
        discipline.setName("TestDisicpline 4");
        discipline.setVersionNo(1L);
        discipline.setDeleteNo(0L);


        discipline.setCreatedBy(loginUser);
        discipline.setUpdatedBy(loginUser);
        lstDis.add(discipline);

        return lstDis;
    }
    public static class MockDateUtil extends MockUp<DateUtil> {
        static Date RET_DATE;

        @Mock
        public Date getNow() {
            return RET_DATE;
        }

    }

    public static class MockAbstractPage extends MockUp<AbstractPage> {
        static String RET_PROJID;
        @Mock
        public String getCurrentProjectId() {
            return RET_PROJID;
        }
    }

    public static class MockDisciplineService extends MockUp<DisciplineServiceImpl> {
        static SearchDisciplineResult RET_SEARCH;
        static byte[] RET_EXCEL;
        static Discipline CRT_DELETE;
        static ServiceAbortException EX_SEARCH;
        static int SET_SEARCH_ERROR_PAGE;

        @Mock
        public SearchDisciplineResult search(SearchDisciplineCondition condition)
            throws ServiceAbortException {
            if (condition.getPageNo() == SET_SEARCH_ERROR_PAGE) {
                throw new ServiceAbortException(ApplicationMessageCode.NO_PAGE_FOUND);
            }
            if (EX_SEARCH != null) {
                throw EX_SEARCH;
            }
            return RET_SEARCH;
        }

        @Mock
        public byte[] generateExcel(List<Discipline> disciplines) throws ServiceAbortException {
            return RET_EXCEL;
        }

        @Mock
        public void delete(Discipline discipline) throws ServiceAbortException {
            CRT_DELETE = discipline;
        }

    }

    public static class MockViewHelper extends MockUp<ViewHelper> {
        static String RET_ACTION;
        static byte[] RET_DATA;
        static String RET_FILENAME;

        @Mock
        public void download(String fileName, byte[] content) {
            RET_ACTION = "download";
            RET_DATA = content;
            RET_FILENAME = fileName;
        }

        @Mock
        public void requestResponse(byte[] content, String charset) {
            RET_ACTION = "requestResponse";
            RET_DATA = content;
        }

        @Mock
        public String getCookieValue(String key) {
            if (Integer.parseInt(key) % 2 == 0) {
                return "true";
            }
            return "false";
        }
    }

    /**
     * 部門情報リストを作成する.
     * @return SearchDisciplineResult 部門情報
     */
    private SearchDisciplineResult createDisciplineResult() {
        List<Discipline> lstDis = getList();

        SearchDisciplineResult result = new SearchDisciplineResult();
        result.setDisciplineList(lstDis);
        result.setCount(lstDis.size());

        return result;

    }

    private List<Discipline> getList() {
        List<Discipline> lstDis = new ArrayList<Discipline>();
        User loginUser = new User();
        loginUser.setEmpNo("ZZA01");
        loginUser.setNameE("Taro Yamada");

        Discipline dis = new Discipline();
        dis.setId(1L);
        dis.setProjectId("PJ1");
        dis.setProjectNameE("Test Project1");
        dis.setDisciplineCd("PIPING");
        dis.setName("Piping");

        dis.setCreatedBy(loginUser);
        dis.setUpdatedBy(loginUser);
        dis.setVersionNo(1L);
        dis.setDeleteNo(0L);
        lstDis.add(dis);

        dis = new Discipline();
        dis.setId(2L);
        dis.setProjectId("PJ1");
        dis.setProjectNameE("Test Project1");
        dis.setDisciplineCd("IT");
        dis.setName("IT");

        dis.setCreatedBy(loginUser);
        dis.setUpdatedBy(loginUser);
        dis.setVersionNo(1L);
        dis.setDeleteNo(0L);
        lstDis.add(dis);

        dis = new Discipline();
        dis.setId(3L);
        dis.setProjectId("PJ1");
        dis.setProjectNameE("Test Project1");
        dis.setDisciplineCd("BUILDING");
        dis.setName("Building");

        dis.setCreatedBy(loginUser);
        dis.setUpdatedBy(loginUser);
        dis.setVersionNo(1L);
        dis.setDeleteNo(0L);
        lstDis.add(dis);

        return lstDis;
    }

    private List<Discipline> getNewList() {
        // ダミーの戻り値をセット
        List<Discipline> disciplineList = new ArrayList<Discipline>();
        Discipline dis = new Discipline();

        dis.setDisciplineCd("KKK");
        dis.setId(11L);
        dis.setName("KKK");
        dis.setVersionNo(1L);

        disciplineList.add(dis);

        dis = new Discipline();
        dis.setDisciplineCd("LLL");
        dis.setId(12L);
        dis.setName("LLL");
        dis.setVersionNo(1L);

        disciplineList.add(dis);

        dis = new Discipline();
        dis.setDisciplineCd("MMM");
        dis.setId(13L);
        dis.setName("MMM");
        dis.setVersionNo(1L);

        disciplineList.add(dis);

        dis = new Discipline();
        dis.setDisciplineCd("OOO");
        dis.setId(14L);
        dis.setName("OOO");
        dis.setVersionNo(1L);

        disciplineList.add(dis);

        dis = new Discipline();
        dis.setDisciplineCd("PPP");
        dis.setId(14L);
        dis.setName("PPP Corporation");
        dis.setVersionNo(1L);

        disciplineList.add(dis);

        dis = new Discipline();
        dis.setDisciplineCd("QQQ");
        dis.setId(15L);
        dis.setName("QQQ");
        dis.setVersionNo(1L);

        disciplineList.add(dis);

        dis = new Discipline();
        dis.setDisciplineCd("RRR");
        dis.setId(16L);
        dis.setName("FFF");
        dis.setVersionNo(1L);

        disciplineList.add(dis);

        dis = new Discipline();
        dis.setDisciplineCd("SSS");
        dis.setId(17L);
        dis.setName("GGG");
        dis.setVersionNo(1L);

        disciplineList.add(dis);

        dis = new Discipline();
        dis.setDisciplineCd("TTT");
        dis.setId(18L);
        dis.setName("HHH");
        dis.setVersionNo(1L);

        disciplineList.add(dis);

        dis = new Discipline();
        dis.setDisciplineCd("UUU");
        dis.setId(19L);
        dis.setName("III");
        dis.setVersionNo(1L);

        disciplineList.add(dis);

        dis = new Discipline();
        dis.setDisciplineCd("VVV");
        dis.setId(20L);
        dis.setName("VVV");
        dis.setVersionNo(1L);

        disciplineList.add(dis);

        SearchDisciplineResult result = new SearchDisciplineResult();
        result.setDisciplineList(disciplineList);
        result.setCount(disciplineList.size());

        return disciplineList;
    }

}
