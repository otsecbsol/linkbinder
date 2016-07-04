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
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroup;
import jp.co.opentone.bsol.linkbinder.dto.Discipline;
import jp.co.opentone.bsol.linkbinder.dto.SearchCorresponGroupResult;
import jp.co.opentone.bsol.linkbinder.dto.Site;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponGroupCondition;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.service.admin.impl.CorresponGroupServiceImpl;
import jp.co.opentone.bsol.linkbinder.service.admin.impl.SiteServiceImpl;
import jp.co.opentone.bsol.linkbinder.view.AbstractPage;
import mockit.Mock;
import mockit.MockUp;

/**
 * {@link GroupIndexPage}のテストケース
 * @author opentone
 */
public class GroupIndexPageTest extends AbstractTestCase {

    /**
     * テスト対象クラス
     */
    @Resource
    private GroupIndexPage page;

    /**
     * 検証用データ
     */
    private List<CorresponGroup> list;

    /**
     * テストの前準備.
     */
    @BeforeClass
    public static void testSetUp() {
        FacesContextMock.initialize();
        new MockAbstractPage();
        new MockSiteService();
        new MockCorresponGroupService();
        new MockViewHelper();
        new MockDateUtil();
    }

    /**
     * 後始末.
     */
    @AfterClass
    public static void testTeardown() {
        new MockAbstractPage().tearDown();
        new MockSiteService().tearDown();
        new MockCorresponGroupService().tearDown();
        new MockViewHelper().tearDown();
        new MockDateUtil().tearDown();
        FacesContextMock.tearDown();
    }

    @Before
    public void setUp() {
        list = createCorresponGroupList();
    }

    @After
    public void tearDown() {
        MockAbstractPage.RET_PROJID = null;
        MockAbstractPage.IS_SYSTEM_ADMIN = false;
        MockAbstractPage.IS_PROJECT_ADMIN = false;
        MockAbstractPage.IS_ANY_GROUP_ADMIN = false;
        MockAbstractPage.RET_CURRENT_USER = null;
        MockCorresponGroupService.RET_SEARCH_PAGING_LIST = null;
        MockCorresponGroupService.RET_EXCEL = null;
        MockCorresponGroupService.RET_SEARCH_NOT_ADD = null;
        MockSiteService.RET_FIND = null;
        MockCorresponGroupService.CRT_DELETE = null;
        MockCorresponGroupService.EX_SEARCH = null;
        MockCorresponGroupService.SET_SEARCH_ERROR_PAGE = -1;
        MockCorresponGroupService.RET_ADD = null;
        MockCorresponGroupService.SET_ADD_DISCIPLINE = null;
        MockCorresponGroupService.SET_ADD_SITE = null;

        FacesContextMock.EXPECTED_CLIENT_ID = null;
        FacesContextMock.EXPECTED_MESSAGE = null;
    }

    /**
     * 初期化アクションを検証する.
     * @throws Exception
     */
    @Test
    public void testInitialize() throws Exception {
        // テストに必要なデータを作成する
        Site site = new Site();
        site.setId(1L);
        site.setSiteCd("YOC");
        site.setName("Yocohama");

        MockSiteService.RET_FIND = site;

        MockAbstractPage.RET_PROJID = "PJ1";
        MockAbstractPage.IS_PROJECT_ADMIN = true;

        List<CorresponGroup> lg = createCorresponGroupList();

        List<Discipline> ld = createDisciplineList();

        SearchCorresponGroupResult result = new SearchCorresponGroupResult();
        result.setCorresponGroupList(lg);
        result.setCount(lg.size());

        MockCorresponGroupService.RET_SEARCH_NOT_ADD = ld;

        MockCorresponGroupService.RET_SEARCH_PAGING_LIST = result;

        page.setId(1L);
        page.setBackPage("site");
        page.initialize();

        assertEquals(lg.toString(), page.getCorresponGroupList().toString());
        assertEquals(lg.size(), page.getDataCount());
        assertEquals(ld.toString(), page.getDisciplineList().toString());

        Discipline d = ld.get(0);
        assertEquals(d.getId(), page.getSelectDisciplineList().get(0).getValue());
        assertEquals(d.getDisciplineCd() + " : " + d.getName(), page.getSelectDisciplineList()
            .get(0).getLabel());

        d = ld.get(1);
        assertEquals(d.getId(), page.getSelectDisciplineList().get(1).getValue());
        assertEquals(d.getDisciplineCd() + " : " + d.getName(), page.getSelectDisciplineList()
            .get(1).getLabel());

        d = ld.get(2);
        assertEquals(d.getId(), page.getSelectDisciplineList().get(2).getValue());
        assertEquals(d.getDisciplineCd() + " : " + d.getName(), page.getSelectDisciplineList()
            .get(2).getLabel());
    }

    /**
     * 初期化アクションを検証する.
     * GroupAdmin
     * @throws Exception
     */
    @Test
    public void testInitializeGroupAdmin() throws Exception {
        // テストに必要なデータを作成する
        Site site = new Site();
        site.setId(1L);
        site.setSiteCd("YOC");
        site.setName("Yocohama");

        MockSiteService.RET_FIND = site;

        MockAbstractPage.RET_PROJID = "PJ1";
        // GroupAdmin
        MockAbstractPage.IS_ANY_GROUP_ADMIN = true;
        User loginUser = new User();
        loginUser.setEmpNo("ZZA05");
        loginUser.setNameE("Keiichi Ogiwara");
        MockAbstractPage.RET_CURRENT_USER = loginUser;

        List<CorresponGroup> lg = createCorresponGroupList();

        List<Discipline> ld = createDisciplineList();

        SearchCorresponGroupResult result = new SearchCorresponGroupResult();
        result.setCorresponGroupList(lg);
        result.setCount(lg.size());

        MockCorresponGroupService.RET_SEARCH_NOT_ADD = ld;

        MockCorresponGroupService.RET_SEARCH_PAGING_LIST = result;

        page.initialize();

        assertFalse(page.getCondition().isSystemAdmin());
        assertFalse(page.getCondition().isProjectAdmin());
        assertTrue(page.getCondition().isGroupAdmin());
        assertEquals(loginUser.getEmpNo(), page.getCondition().getSearchUser().getEmpNo());

        assertEquals(lg.toString(), page.getCorresponGroupList().toString());
        assertEquals(lg.size(), page.getDataCount());
        assertEquals(ld.toString(), page.getDisciplineList().toString());

        Discipline d = ld.get(0);
        assertEquals(d.getId(), page.getSelectDisciplineList().get(0).getValue());
        assertEquals(d.getDisciplineCd() + " : " + d.getName(), page.getSelectDisciplineList()
            .get(0).getLabel());

        d = ld.get(1);
        assertEquals(d.getId(), page.getSelectDisciplineList().get(1).getValue());
        assertEquals(d.getDisciplineCd() + " : " + d.getName(), page.getSelectDisciplineList()
            .get(1).getLabel());

        d = ld.get(2);
        assertEquals(d.getId(), page.getSelectDisciplineList().get(2).getValue());
        assertEquals(d.getDisciplineCd() + " : " + d.getName(), page.getSelectDisciplineList()
            .get(2).getLabel());
    }

    /**
     * 初期化処理のテスト.
     * プロジェクト未選択の場合.
     * @throws Exception
     */
    @Test
    public void testInitializeProjectNotSelected() throws Exception {
        MockAbstractPage.RET_PROJID = null;
        MockAbstractPage.IS_ANY_GROUP_ADMIN = true;

        // 期待されるメッセージをセット
        FacesContextMock.EXPECTED_MESSAGE =
                new FacesMessage(
                    FacesMessage.SEVERITY_ERROR, FacesMessage.SEVERITY_ERROR.toString(),
                    createExpectedMessageString(
                        Messages.getMessageAsString(CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT),
                        "Initialize"));

        try {
            page.initialize();
            fail("例外が発生していない");
        } catch (InvalidOperationRuntimeException e) {
            ServiceAbortException actual = (ServiceAbortException) e.getCause();
            assertEquals(CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT,
                        actual.getMessageCode());
        }
    }

    /**
     * 前ページ（<<）アクションのテスト.
     * @throws Exception
     */
    @Test
    public void testMovePrevious() throws Exception {
        // テストに必要なデータを作成
        SearchCorresponGroupResult result = createCorresponGroupResult();
        result.setCount(30);
        MockCorresponGroupService.RET_SEARCH_PAGING_LIST = result;

        MockCorresponGroupService.RET_SEARCH_NOT_ADD = createDisciplineList();

        /* 全30件を10行ずつ表示 -- 全3ページ */
        page.setPageRowNum(10);
        SearchCorresponGroupCondition condition = new SearchCorresponGroupCondition();
        condition.setPageRowNum(10);
        page.setCondition(condition);

        // 3ページ目から1つ戻る
        page.setPageNo(3);
        page.movePrevious();

        assertEquals(2, page.getPageNo());
        assertEquals(list.toString(), page.getCorresponGroupList().toString());
        assertEquals(30, page.getDataCount());
        // 検索条件
        assertEquals(2, page.getCondition().getPageNo());

        // 再設定
        List<CorresponGroup> newList = getNewList();
        result = new SearchCorresponGroupResult();
        result.setCorresponGroupList(newList);

        MockCorresponGroupService.RET_SEARCH_NOT_ADD = createDisciplineList();

        result.setCount(32); // 増加
        MockCorresponGroupService.RET_SEARCH_PAGING_LIST = result;

        // さらに1つ戻る
        page.movePrevious();

        assertEquals(1, page.getPageNo());
        assertEquals(newList.toString(), page.getCorresponGroupList().toString());
        assertEquals(32, page.getDataCount()); // 増加
        // 検索条件
        assertEquals(1, page.getCondition().getPageNo());
    }

    /**
     * 前ページ（<<）アクションのテスト.
     * GroupAdminの場合
     * @throws Exception
     */
    @Test
    public void testMovePreviousGroupAdmin() throws Exception {
        // テストに必要なデータを作成
        SearchCorresponGroupResult result = createCorresponGroupResult();
        result.setCount(30);
        MockCorresponGroupService.RET_SEARCH_PAGING_LIST = result;

        MockCorresponGroupService.RET_SEARCH_NOT_ADD = createDisciplineList();

        MockAbstractPage.IS_ANY_GROUP_ADMIN = true;
        MockAbstractPage.RET_PROJID = "PJ1";

        User loginUser = new User();
        loginUser.setEmpNo("ZZA05");
        loginUser.setNameE("Keiichi Ogiwara");

        MockAbstractPage.RET_CURRENT_USER = loginUser;

        /* 全30件を10行ずつ表示 -- 全3ページ */
        page.setPageRowNum(10);
        SearchCorresponGroupCondition condition = new SearchCorresponGroupCondition();
        condition.setPageRowNum(10);
        page.setCondition(condition);

        // 3ページ目から1つ戻る
        page.setPageNo(3);
        page.movePrevious();

        assertEquals(2, page.getPageNo());
        assertEquals(list.toString(), page.getCorresponGroupList().toString());
        assertEquals(30, page.getDataCount());
        // 検索条件
        assertEquals(2, page.getCondition().getPageNo());

        assertFalse(page.getCondition().isSystemAdmin());
        assertFalse(page.getCondition().isProjectAdmin());
        assertTrue(page.getCondition().isGroupAdmin());
        assertEquals(loginUser.getEmpNo(), page.getCondition().getSearchUser().getEmpNo());

        // 再設定
        List<CorresponGroup> newList = getNewList();
        result = new SearchCorresponGroupResult();
        result.setCorresponGroupList(newList);

        MockCorresponGroupService.RET_SEARCH_NOT_ADD = createDisciplineList();

        result.setCount(32); // 増加
        MockCorresponGroupService.RET_SEARCH_PAGING_LIST = result;

        // さらに1つ戻る
        page.movePrevious();

        assertEquals(1, page.getPageNo());
        assertEquals(newList.toString(), page.getCorresponGroupList().toString());
        assertEquals(32, page.getDataCount()); // 増加
        // 検索条件
        assertEquals(1, page.getCondition().getPageNo());

        assertFalse(page.getCondition().isSystemAdmin());
        assertFalse(page.getCondition().isProjectAdmin());
        assertTrue(page.getCondition().isGroupAdmin());
        assertEquals(loginUser.getEmpNo(), page.getCondition().getSearchUser().getEmpNo());

    }

    /**
     * 次ページ（>>）アクションのテスト.
     * @throws Exception
     */
    @Test
    public void testMoveNext() throws Exception {
        // ダミー部門情報を設定
        SearchCorresponGroupResult result = createCorresponGroupResult();
        result.setCount(30);
        MockCorresponGroupService.RET_SEARCH_PAGING_LIST = result;

        MockCorresponGroupService.RET_SEARCH_NOT_ADD = createDisciplineList();

        /* 全30件を10行ずつ表示 -- 全3ページ */
        page.setPageRowNum(10);
        SearchCorresponGroupCondition condition = new SearchCorresponGroupCondition();
        condition.setPageRowNum(10);
        page.setCondition(condition);

        // 1ページ目から1つ進む
        page.setPageNo(1);
        page.moveNext();

        assertEquals(2, page.getPageNo());
        assertEquals(list.toString(), page.getCorresponGroupList().toString());
        assertEquals(30, page.getDataCount());
        // 検索条件
        assertEquals(2, page.getCondition().getPageNo());

        // 再設定
        List<CorresponGroup> newList = getNewList();
        result = new SearchCorresponGroupResult();
        result.setCorresponGroupList(newList);

        MockCorresponGroupService.RET_SEARCH_NOT_ADD = createDisciplineList();

        result.setCount(32); // 増加
        MockCorresponGroupService.RET_SEARCH_PAGING_LIST = result;

        // さらに1つ進む
        page.moveNext();

        assertEquals(3, page.getPageNo());
        assertEquals(newList.toString(), page.getCorresponGroupList().toString());
        assertEquals(32, page.getDataCount()); // 増加
        // 検索条件
        assertEquals(3, page.getCondition().getPageNo());
    }

    /**
     * 次ページ（>>）アクションのテスト.
     * GroupAdminの場合
     * @throws Exception
     */
    @Test
    public void testMoveNextGroupAdmin() throws Exception {
        // ダミー部門情報を設定
        SearchCorresponGroupResult result = createCorresponGroupResult();
        result.setCount(30);
        MockCorresponGroupService.RET_SEARCH_PAGING_LIST = result;

        MockCorresponGroupService.RET_SEARCH_NOT_ADD = createDisciplineList();

        MockAbstractPage.IS_ANY_GROUP_ADMIN = true;
        User loginUser = new User();
        loginUser.setEmpNo("ZZA05");
        loginUser.setNameE("Keiichi Ogiwara");
        MockAbstractPage.RET_CURRENT_USER = loginUser;
        MockAbstractPage.RET_PROJID = "PJ1";

        /* 全30件を10行ずつ表示 -- 全3ページ */
        page.setPageRowNum(10);
        SearchCorresponGroupCondition condition = new SearchCorresponGroupCondition();
        condition.setPageRowNum(10);
        page.setCondition(condition);

        // 1ページ目から1つ進む
        page.setPageNo(1);
        page.moveNext();

        assertEquals(2, page.getPageNo());
        assertEquals(list.toString(), page.getCorresponGroupList().toString());
        assertEquals(30, page.getDataCount());
        // 検索条件
        assertEquals(2, page.getCondition().getPageNo());

        assertFalse(page.getCondition().isSystemAdmin());
        assertFalse(page.getCondition().isProjectAdmin());
        assertTrue(page.getCondition().isGroupAdmin());
        assertEquals(loginUser.getEmpNo(), page.getCondition().getSearchUser().getEmpNo());

        // 再設定
        List<CorresponGroup> newList = getNewList();
        result = new SearchCorresponGroupResult();
        result.setCorresponGroupList(newList);

        MockCorresponGroupService.RET_SEARCH_NOT_ADD = createDisciplineList();

        result.setCount(32); // 増加
        MockCorresponGroupService.RET_SEARCH_PAGING_LIST = result;

        // さらに1つ進む
        page.moveNext();

        assertEquals(3, page.getPageNo());
        assertEquals(newList.toString(), page.getCorresponGroupList().toString());
        assertEquals(32, page.getDataCount()); // 増加
        // 検索条件
        assertEquals(3, page.getCondition().getPageNo());

        assertFalse(page.getCondition().isSystemAdmin());
        assertFalse(page.getCondition().isProjectAdmin());
        assertTrue(page.getCondition().isGroupAdmin());
        assertEquals(loginUser.getEmpNo(), page.getCondition().getSearchUser().getEmpNo());
    }



    /**
     * ページ遷移アクションのテスト.
     * @throws Exception
     */
    @Test
    public void testChangePage() throws Exception {
        // 部門情報を設定
        SearchCorresponGroupResult result = createCorresponGroupResult();
        result.setCorresponGroupList(result.getCorresponGroupList());
        result.setCount(50);
        MockCorresponGroupService.RET_SEARCH_PAGING_LIST = result;

        MockCorresponGroupService.RET_SEARCH_NOT_ADD = createDisciplineList();

        /* 全50件を10行ずつ表示 -- 全5ページ */
        page.setPageRowNum(10);
        page.setPageIndex(10);
        SearchCorresponGroupCondition condition = new SearchCorresponGroupCondition();
        condition.setPageRowNum(10);
        page.setCondition(condition);

        // 4ページ目へ
        page.setPageNo(4);
        page.changePage();

        assertEquals(4, page.getPageNo());
        assertEquals(list.toString(), page.getCorresponGroupList().toString());
        assertEquals(50, page.getDataCount());
        // 検索条件
        assertEquals(4, page.getCondition().getPageNo());

        // 再設定
        List<CorresponGroup> newList = getNewList();
        result = new SearchCorresponGroupResult();
        result.setCorresponGroupList(newList);

        MockCorresponGroupService.RET_SEARCH_NOT_ADD = createDisciplineList();

        result.setCount(54); // 増加
        MockCorresponGroupService.RET_SEARCH_PAGING_LIST = result;

        // 2ページ目へ
        page.setPageNo(2);
        page.changePage();

        assertEquals(2, page.getPageNo());
        assertEquals(newList.toString(), page.getCorresponGroupList().toString());
        assertEquals(54, page.getDataCount()); // 増加
        // 検索条件
        assertEquals(2, page.getCondition().getPageNo());
    }

    /**
     * ページ遷移アクションのテスト.
     * GroupAdminの場合
     * @throws Exception
     */
    @Test
    public void testChangePageGroupAdmin() throws Exception {
        // 部門情報を設定
        SearchCorresponGroupResult result = createCorresponGroupResult();
        result.setCorresponGroupList(result.getCorresponGroupList());
        result.setCount(50);
        MockCorresponGroupService.RET_SEARCH_PAGING_LIST = result;

        MockCorresponGroupService.RET_SEARCH_NOT_ADD = createDisciplineList();

        MockAbstractPage.IS_ANY_GROUP_ADMIN = true;
        User loginUser = new User();
        loginUser.setEmpNo("ZZA05");
        loginUser.setNameE("Keiichi Ogiwara");
        MockAbstractPage.RET_CURRENT_USER = loginUser;
        MockAbstractPage.RET_PROJID = "PJ1";

        /* 全50件を10行ずつ表示 -- 全5ページ */
        page.setPageRowNum(10);
        page.setPageIndex(10);
        SearchCorresponGroupCondition condition = new SearchCorresponGroupCondition();
        condition.setPageRowNum(10);
        page.setCondition(condition);

        // 4ページ目へ
        page.setPageNo(4);
        page.changePage();

        assertEquals(4, page.getPageNo());
        assertEquals(list.toString(), page.getCorresponGroupList().toString());
        assertEquals(50, page.getDataCount());
        // 検索条件
        assertEquals(4, page.getCondition().getPageNo());
        assertFalse(page.getCondition().isSystemAdmin());
        assertFalse(page.getCondition().isProjectAdmin());
        assertTrue(page.getCondition().isGroupAdmin());
        assertEquals(loginUser.getEmpNo(), page.getCondition().getSearchUser().getEmpNo());

        // 再設定
        List<CorresponGroup> newList = getNewList();
        result = new SearchCorresponGroupResult();
        result.setCorresponGroupList(newList);

        MockCorresponGroupService.RET_SEARCH_NOT_ADD = createDisciplineList();

        result.setCount(54); // 増加
        MockCorresponGroupService.RET_SEARCH_PAGING_LIST = result;

        // 2ページ目へ
        page.setPageNo(2);
        page.changePage();

        assertEquals(2, page.getPageNo());
        assertEquals(newList.toString(), page.getCorresponGroupList().toString());
        assertEquals(54, page.getDataCount()); // 増加
        // 検索条件
        assertEquals(2, page.getCondition().getPageNo());
        assertFalse(page.getCondition().isSystemAdmin());
        assertFalse(page.getCondition().isProjectAdmin());
        assertTrue(page.getCondition().isGroupAdmin());
        assertEquals(loginUser.getEmpNo(), page.getCondition().getSearchUser().getEmpNo());

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
        MockCorresponGroupService.RET_EXCEL = expected;

        Date date = new GregorianCalendar(2009, 3, 1, 1, 1, 1).getTime();
        MockDateUtil.RET_DATE = date;

        SearchCorresponGroupResult result = createCorresponGroupResult();

        MockCorresponGroupService.RET_SEARCH_PAGING_LIST = result;

        page.downloadExcel();

        assertArrayEquals(expected, MockViewHelper.RET_DATA);
        assertEquals("20090401010101.xls", MockViewHelper.RET_FILENAME);

        // 2回目の作成
        byte[] newExpected = "NEW-TEST-OUT-EXCEL".getBytes();
        MockCorresponGroupService.RET_EXCEL = newExpected;

        Date newDate = new GregorianCalendar(2009, 3, 1, 11, 11, 11).getTime();
        MockDateUtil.RET_DATE = newDate;

        page.downloadExcel();

        assertArrayEquals(newExpected, MockViewHelper.RET_DATA);
        assertEquals("20090401111111.xls", MockViewHelper.RET_FILENAME);
    }

    /**
     * 起動元画面に戻る処理のテスト.
     * @throws Exception
     */
    @Test
    public void testBack() throws Exception {
        // テストに必要なデータを作成する
        page.setBackPage("site");
        page.setId(1L);
        page.back();

        assertEquals("site?id=1", page.getBackPage());

    }

    /**
     * 活動単位追加処理のテスト.
     * @throws Exception
     */
    @Test
    public void testAdd() throws Exception {
        // テストに必要なデータを作成する.
        List<Discipline> ld = createDisciplineList();

        page.setSelectDiscipline(4L);

        page.setDisciplineList(ld);

        Site site = new Site();
        site.setId(1L);
        site.setSiteCd("YOC");
        site.setName("Yocohama");

        page.setSite(site);

        MockCorresponGroupService.RET_ADD = 1L;

        MockSiteService.RET_FIND = site;
        MockAbstractPage.RET_PROJID = "PJ1";
        MockCorresponGroupService.RET_SEARCH_PAGING_LIST = createCorresponGroupResult();

        MockCorresponGroupService.RET_SEARCH_NOT_ADD = createDisciplineList();

        // 期待されるメッセージをセット
        FacesContextMock.EXPECTED_MESSAGE =
                new FacesMessage(
                    FacesMessage.SEVERITY_INFO, FacesMessage.SEVERITY_INFO.toString(),
                    createExpectedMessageString(
                        Messages.getMessageAsString(DISCIPLINE_ADDED),
                        null));

        page.setPageRowNum(10);
        page.setPageIndex(10);
        page.setPageNo(2);

        page.add();

        assertEquals(site.getId(), MockCorresponGroupService.SET_ADD_SITE.getId());
        assertEquals(Long.valueOf(4L), MockCorresponGroupService.SET_ADD_DISCIPLINE.getId());

        assertEquals(2, page.getPageNo()); // 同一ページでリロード
        assertEquals(10, page.getPageRowNum());
        assertEquals(10, page.getPageRowNum());

    }

    /**
     * 活動単位追加処理のテスト. 削除の結果、レコード件数が0件.
     * @throws Exception
     */
    @Test
    public void testAddNoDataFound() throws Exception {
        // テストに必要なデータを作成する.
        List<Discipline> ld = createDisciplineList();

        page.setSelectDiscipline(4L);

        page.setDisciplineList(ld);

        Site site = new Site();
        site.setId(1L);
        site.setSiteCd("YOC");
        site.setName("Yocohama");

        page.setSite(site);

        MockCorresponGroupService.RET_ADD = 1L;

        MockSiteService.RET_FIND = site;
        MockAbstractPage.RET_PROJID = "PJ1";
        MockCorresponGroupService.RET_SEARCH_PAGING_LIST = createCorresponGroupResult();
        MockCorresponGroupService.RET_SEARCH_NOT_ADD = createDisciplineList();

        // 期待されるメッセージをセット
        FacesContextMock.EXPECTED_MESSAGE =
            new FacesMessage(
                FacesMessage.SEVERITY_INFO, FacesMessage.SEVERITY_INFO.toString(),
                createExpectedMessageString(
                    Messages.getMessageAsString(DISCIPLINE_ADDED),
                    null));

        page.setPageIndex(15);
        page.setPageNo(3);
        page.setPageRowNum(25);

        page.setCorresponGroupList(new ArrayList<CorresponGroup>());
        page.setCondition(new SearchCorresponGroupCondition());

        MockCorresponGroupService.EX_SEARCH =
                new ServiceAbortException(ApplicationMessageCode.NO_DATA_FOUND);

        page.add();

        assertEquals(site.getId(), MockCorresponGroupService.SET_ADD_SITE.getId());
        assertEquals(Long.valueOf(4L), MockCorresponGroupService.SET_ADD_DISCIPLINE.getId());

        assertEquals(0, page.getDataCount());
    }

    /**
     * 活動単位追加処理のテスト. 削除の結果、レコード件数が0件.
     * @throws Exception
     */
    @Test
    public void testAddNoPageFound() throws Exception {
        // テストに必要なデータを作成する.
        List<Discipline> ld = createDisciplineList();

        page.setSelectDiscipline(4L);

        page.setDisciplineList(ld);

        Site site = new Site();
        site.setId(1L);
        site.setSiteCd("YOC");
        site.setName("Yocohama");

        page.setSite(site);

        MockCorresponGroupService.RET_ADD = 1L;

        MockSiteService.RET_FIND = site;
        MockAbstractPage.RET_PROJID = "PJ1";
        MockCorresponGroupService.RET_SEARCH_PAGING_LIST = createCorresponGroupResult();

        MockCorresponGroupService.RET_SEARCH_NOT_ADD = createDisciplineList();

        // 期待されるメッセージをセット
        FacesContextMock.EXPECTED_MESSAGE =
            new FacesMessage(
                FacesMessage.SEVERITY_INFO, FacesMessage.SEVERITY_INFO.toString(),
                createExpectedMessageString(
                    Messages.getMessageAsString(DISCIPLINE_ADDED),
                    null));

        page.setPageIndex(15);
        page.setPageNo(3);
        page.setPageRowNum(25);

        MockCorresponGroupService.SET_SEARCH_ERROR_PAGE = 3;

        page.setCorresponGroupList(new ArrayList<CorresponGroup>());
        page.setCondition(new SearchCorresponGroupCondition());

        page.add();

        assertEquals(site.getId(), MockCorresponGroupService.SET_ADD_SITE.getId());
        assertEquals(Long.valueOf(4L), MockCorresponGroupService.SET_ADD_DISCIPLINE.getId());

        assertFalse(page.getNext());
        assertTrue(page.getPrevious());
        assertEquals(15, page.getPageIndex());
        assertEquals(2, page.getPageNo()); // １つ前のページ
        assertEquals(25, page.getPageRowNum());
    }

    /**
     * 拠点情報削除アクションのテスト.
     * @throws Exception
     */
    @Test
    public void testDelete() throws Exception {
        // テストに必要なデータを作成
        SearchCorresponGroupResult result = createCorresponGroupResult();
        List<CorresponGroup> lstCorresponGroup = result.getCorresponGroupList();
        page.setCorresponGroupList(lstCorresponGroup);
        page.getDataModel().setRowIndex(2); // 3行目を選択

        // 比較用
        CorresponGroup deleteCorresponGroup = lstCorresponGroup.get(2); // 3行目を選択

        // 期待されるメッセージをセット
        FacesContextMock.EXPECTED_CLIENT_ID = null;
        FacesContextMock.EXPECTED_MESSAGE =
                new FacesMessage(
                    FacesMessage.SEVERITY_INFO, FacesMessage.SEVERITY_INFO.toString(),
                    createExpectedMessageString(
                        Messages.getMessageAsString(CORRESPON_GROUP_DELETED),
                        null));

        MockCorresponGroupService.RET_SEARCH_PAGING_LIST = result;

        MockCorresponGroupService.RET_SEARCH_NOT_ADD = createDisciplineList();

        MockAbstractPage.RET_PROJID = "PJ1";

        MockAbstractPage.IS_SYSTEM_ADMIN = true;

        page.setCondition(new SearchCorresponGroupCondition());

        page.setPageNo(2);

        page.delete();

        CorresponGroup actual = MockCorresponGroupService.CRT_DELETE;

        assertEquals(deleteCorresponGroup.getId(), actual.getId());
        assertEquals(deleteCorresponGroup.getSite().getId(), actual.getSite().getId());
        assertEquals(deleteCorresponGroup.getDiscipline().getId(), actual.getDiscipline().getId());
        assertEquals(deleteCorresponGroup.getName(), actual.getName());
        assertEquals(deleteCorresponGroup.getCreatedBy().toString(), actual.getCreatedBy()
            .toString());
        assertEquals(deleteCorresponGroup.getUpdatedBy().toString(), actual.getUpdatedBy()
            .toString());
        assertEquals(deleteCorresponGroup.getVersionNo(), actual.getVersionNo());
        assertEquals(deleteCorresponGroup.getDeleteNo(), actual.getDeleteNo());

        assertEquals(2, page.getPageNo());

    }

    /**
     * 拠点情報削除アクションのテスト. 削除の結果、レコード件数が0件.
     * @throws Exception
     */
    @Test
    public void testDeleteNoDataFound() throws Exception {
        SearchCorresponGroupResult result = createCorresponGroupResult();
        List<CorresponGroup> lstCorresponGroup = result.getCorresponGroupList();
        page.setCorresponGroupList(lstCorresponGroup);
        page.getDataModel().setRowIndex(2); // 3行目を選択

        // 比較用
        CorresponGroup deleteCorresponGroup = lstCorresponGroup.get(2); // 3行目を選択

        page.setPageIndex(15);
        page.setPageNo(3);
        page.setPageRowNum(25);

        MockCorresponGroupService.EX_SEARCH =
                new ServiceAbortException(ApplicationMessageCode.NO_DATA_FOUND);

        // 期待されるメッセージをセット
        FacesContextMock.EXPECTED_MESSAGE =
                new FacesMessage(
                    FacesMessage.SEVERITY_INFO, FacesMessage.SEVERITY_INFO.toString(),
                    createExpectedMessageString(
                        Messages.getMessageAsString(CORRESPON_GROUP_DELETED),
                        null));
        MockCorresponGroupService.RET_SEARCH_NOT_ADD = createDisciplineList();

        page.delete();

        assertEquals(deleteCorresponGroup.toString(), MockCorresponGroupService.CRT_DELETE
            .toString());
        assertEquals(0, page.getDataCount());
    }

    /**
     * 拠点情報削除アクションのテスト. 削除の結果、レコード件数が0件.
     * @throws Exception
     */
    @Test
    public void testDeleteNoPageFound() throws Exception {
        SearchCorresponGroupResult result = createCorresponGroupResult();
        List<CorresponGroup> lstCorresponGroup = result.getCorresponGroupList();
        List<Discipline> lstDiscipline = createDisciplineList();

        page.setCorresponGroupList(lstCorresponGroup);
        page.setDisciplineList(lstDiscipline);
        page.getDataModel().setRowIndex(2); // 3行目を選択

        // 比較用
        CorresponGroup deleteCorresponGroup = lstCorresponGroup.get(2); // 3行目を選択

        page.setPageIndex(15);
        page.setPageNo(3);
        page.setPageRowNum(25);

        MockCorresponGroupService.SET_SEARCH_ERROR_PAGE = 3;

        SearchCorresponGroupResult searchResult = new SearchCorresponGroupResult();
        searchResult.setCorresponGroupList(lstCorresponGroup);

        MockCorresponGroupService.RET_SEARCH_NOT_ADD = lstDiscipline;

        searchResult.setCount(49);
        MockCorresponGroupService.RET_SEARCH_PAGING_LIST = searchResult;

        page.setCondition(new SearchCorresponGroupCondition());

        MockCorresponGroupService.RET_SEARCH_NOT_ADD = createDisciplineList();

        // 期待されるメッセージをセット
        FacesContextMock.EXPECTED_MESSAGE =
                new FacesMessage(
                    FacesMessage.SEVERITY_INFO, FacesMessage.SEVERITY_INFO.toString(),
                    createExpectedMessageString(
                        Messages.getMessageAsString(CORRESPON_GROUP_DELETED),
                        null));

        page.delete();

        assertEquals(deleteCorresponGroup.toString(), MockCorresponGroupService.CRT_DELETE
            .toString());
        assertFalse(page.getNext());
        assertTrue(page.getPrevious());
        assertEquals(15, page.getPageIndex());
        assertEquals(2, page.getPageNo()); // １つ前のページ
        assertEquals(25, page.getPageRowNum());
    }

    public static class MockSiteService extends MockUp<SiteServiceImpl> {
        static Site RET_FIND;

        @Mock
        public Site find(Long id) throws ServiceAbortException {
            return RET_FIND;
        }
    }

    public static class MockCorresponGroupService extends MockUp<CorresponGroupServiceImpl> {
        static SearchCorresponGroupResult RET_SEARCH_PAGING_LIST;
        static byte[] RET_EXCEL;
        static Long RET_ADD;
        static Site SET_ADD_SITE;
        static Discipline SET_ADD_DISCIPLINE;
        static CorresponGroup CRT_DELETE;
        static ServiceAbortException EX_SEARCH;
        static int SET_SEARCH_ERROR_PAGE;
        static List<Discipline> RET_SEARCH_NOT_ADD;

        @Mock
        public SearchCorresponGroupResult searchPagingList(SearchCorresponGroupCondition condition)
            throws ServiceAbortException {
            if (condition.getPageNo() == SET_SEARCH_ERROR_PAGE) {
                throw new ServiceAbortException(ApplicationMessageCode.NO_PAGE_FOUND);
            }
            if (EX_SEARCH != null) {
                throw EX_SEARCH;
            }
            return RET_SEARCH_PAGING_LIST;
        }

        @Mock
        public byte[] generateExcel(List<CorresponGroup> corresponGroups)
            throws ServiceAbortException {
            return RET_EXCEL;
        }

        @Mock
        public Long add(Site site, Discipline discipline) throws ServiceAbortException {
            SET_ADD_SITE = site;
            SET_ADD_DISCIPLINE = discipline;

            return RET_ADD;
        }

        @Mock
        public void delete(CorresponGroup corresponGroup) throws ServiceAbortException {
            CRT_DELETE = corresponGroup;
        }

        @Mock
        public List<Discipline> searchNotAdd(Long siteId) {
            return RET_SEARCH_NOT_ADD;
        }

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
        static boolean IS_SYSTEM_ADMIN;
        static boolean IS_PROJECT_ADMIN;
        static boolean IS_ANY_GROUP_ADMIN;
        static User RET_CURRENT_USER;

        @Mock
        public String getCurrentProjectId() {
            return RET_PROJID;
        }

        @Mock
        public boolean isSystemAdmin() {
            return IS_SYSTEM_ADMIN;
        }

        @Mock
        public boolean isProjectAdmin(String projectId) {
            return IS_PROJECT_ADMIN;
        }

        @Mock
        public boolean isAnyGroupAdmin(String projectId) {
            return IS_ANY_GROUP_ADMIN;
        }

        @Mock
        public User getCurrentUser() {
            return RET_CURRENT_USER;
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
     * 活動単位リストを作成する.
     * @return 活動単位リスト
     */
    private List<CorresponGroup> createCorresponGroupList() {
        List<CorresponGroup> lg = new ArrayList<CorresponGroup>();
        CorresponGroup cg = new CorresponGroup();

        User loginUser = new User();
        loginUser.setEmpNo("ZZA02");
        loginUser.setNameE("Tetsuo Aoki");

        // 拠点情報
        Site site = new Site();
        site.setId(1L);
        site.setSiteCd("YOC");
        site.setName("Yocohama");

        // 部門情報
        Discipline dis = new Discipline();
        dis.setId(1L);
        dis.setDisciplineCd("IT");
        dis.setName("It");

        cg.setId(1L);
        cg.setProjectId("PJ1");
        cg.setSite(site);
        cg.setDiscipline(dis);
        cg.setName("YOC:IT");
        cg.setCreatedBy(loginUser);
        cg.setUpdatedBy(loginUser);

        lg.add(cg);

        cg = new CorresponGroup();
        cg.setId(2L);
        cg.setProjectId("PJ1");
        cg.setSite(site);
        cg.setCreatedBy(loginUser);
        cg.setUpdatedBy(loginUser);

        dis = new Discipline();
        dis.setId(2L);
        dis.setDisciplineCd("PIPING");
        dis.setName("Pinping");

        cg.setDiscipline(dis);
        cg.setName("YOC:PIPING");

        lg.add(cg);

        cg = new CorresponGroup();
        cg.setId(3L);
        cg.setProjectId("PJ1");
        cg.setSite(site);
        cg.setCreatedBy(loginUser);
        cg.setUpdatedBy(loginUser);

        dis = new Discipline();
        dis.setId(3L);
        dis.setDisciplineCd("BUILDING");
        dis.setName("Building");

        cg.setDiscipline(dis);
        cg.setName("YOC:BUILDING");

        lg.add(cg);

        return lg;
    }

    /**
     * 部門情報リストを作成する.
     * @return 部門情報リスト
     */
    private List<Discipline> createDisciplineList() {
        List<Discipline> ld = new ArrayList<Discipline>();

        Discipline dis = new Discipline();
        dis.setId(4L);
        dis.setProjectId("PJ1");
        dis.setDisciplineCd("AAABUMON");
        dis.setName("AAA");

        ld.add(dis);

        dis = new Discipline();
        dis.setId(5L);
        dis.setProjectId("PJ1");
        dis.setDisciplineCd("BBBUMON");
        dis.setName("BBB");

        ld.add(dis);

        dis = new Discipline();
        dis.setId(6L);
        dis.setProjectId("PJ1");
        dis.setDisciplineCd("CCCUMON");
        dis.setName("CCC");

        ld.add(dis);

        return ld;
    }

    /**
     * 一覧表示用活動単位オブジェクトを作成する.
     * @return 一覧表示用活動単位オブジェクト
     */
    private SearchCorresponGroupResult createCorresponGroupResult() {
        List<CorresponGroup> lg = createCorresponGroupList();

        SearchCorresponGroupResult result = new SearchCorresponGroupResult();
        result.setCorresponGroupList(lg);
        result.setCount(lg.size());

        return result;
    }

    private List<CorresponGroup> getNewList() {
        // ダミーの戻り値をセット
        List<CorresponGroup> cgList = new ArrayList<CorresponGroup>();
        CorresponGroup cg = null;

        // 拠点情報
        Site site = new Site();
        site.setId(1L);
        site.setSiteCd("YOC");
        site.setName("Yocohama");

        // 部門情報
        Discipline dis = new Discipline();

        cg = new CorresponGroup();
        cg.setId(11L);
        cg.setProjectId("PJ1");
        cg.setSite(site);

        dis = new Discipline();
        dis.setId(4L);
        dis.setDisciplineCd("ZZZ");
        dis.setName("Zzz");

        cg.setDiscipline(dis);
        cg.setName("YOC:ZZZ");

        cgList.add(cg);

        cg = new CorresponGroup();
        cg.setId(12L);
        cg.setProjectId("PJ1");
        cg.setSite(site);

        dis = new Discipline();
        dis.setId(5L);
        dis.setDisciplineCd("YYY");
        dis.setName("Yyy");

        cg.setDiscipline(dis);
        cg.setName("YOC:YYY");

        cgList.add(cg);

        cg = new CorresponGroup();
        cg.setId(13L);
        cg.setProjectId("PJ1");
        cg.setSite(site);

        dis = new Discipline();
        dis.setId(6L);
        dis.setDisciplineCd("XXX");
        dis.setName("Xxx");

        cg.setDiscipline(dis);
        cg.setName("YOC:XXX");

        cgList.add(cg);

        cg = new CorresponGroup();
        cg.setId(14L);
        cg.setProjectId("PJ1");
        cg.setSite(site);

        dis = new Discipline();
        dis.setId(7L);
        dis.setDisciplineCd("WWW");
        dis.setName("Www");

        cg.setDiscipline(dis);
        cg.setName("YOC:WWW");

        cgList.add(cg);

        cg = new CorresponGroup();
        cg.setId(15L);
        cg.setProjectId("PJ1");
        cg.setSite(site);

        dis = new Discipline();
        dis.setId(8L);
        dis.setDisciplineCd("VVV");
        dis.setName("Vvv");

        cg.setDiscipline(dis);
        cg.setName("YOC:VVV");

        cgList.add(cg);

        cg = new CorresponGroup();
        cg.setId(16L);
        cg.setProjectId("PJ1");
        cg.setSite(site);

        dis = new Discipline();
        dis.setId(9L);
        dis.setDisciplineCd("UUU");
        dis.setName("Uuu");

        cg.setDiscipline(dis);
        cg.setName("YOC:UUU");

        cgList.add(cg);

        cg = new CorresponGroup();
        cg.setId(17L);
        cg.setProjectId("PJ1");
        cg.setSite(site);

        dis = new Discipline();
        dis.setId(10L);
        dis.setDisciplineCd("TTT");
        dis.setName("Ttt");

        cg.setDiscipline(dis);
        cg.setName("YOC:TTT");

        cgList.add(cg);

        cg = new CorresponGroup();
        cg.setId(18L);
        cg.setProjectId("PJ1");
        cg.setSite(site);

        dis = new Discipline();
        dis.setId(11L);
        dis.setDisciplineCd("SSS");
        dis.setName("Sss");

        cg.setDiscipline(dis);
        cg.setName("YOC:SSS");

        cgList.add(cg);

        cg = new CorresponGroup();
        cg.setId(19L);
        cg.setProjectId("PJ1");
        cg.setSite(site);

        dis = new Discipline();
        dis.setId(12L);
        dis.setDisciplineCd("RRR");
        dis.setName("Rrr");

        cg.setDiscipline(dis);
        cg.setName("YOC:RRR");

        cgList.add(cg);

        cg = new CorresponGroup();
        cg.setId(20L);
        cg.setProjectId("PJ1");
        cg.setSite(site);

        dis = new Discipline();
        dis.setId(13L);
        dis.setDisciplineCd("QQQ");
        dis.setName("Qqq");

        cg.setDiscipline(dis);
        cg.setName("YOC:QQQ");

        cgList.add(cg);

        return cgList;
    }
}
