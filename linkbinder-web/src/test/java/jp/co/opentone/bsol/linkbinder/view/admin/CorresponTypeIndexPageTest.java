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
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import jp.co.opentone.bsol.framework.core.message.Messages;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.core.util.DateUtil;
import jp.co.opentone.bsol.framework.extension.jsf.FacesContextMock;
import jp.co.opentone.bsol.framework.test.AbstractTestCase;
import jp.co.opentone.bsol.framework.web.view.util.ViewHelper;
import jp.co.opentone.bsol.linkbinder.dto.CorresponType;
import jp.co.opentone.bsol.linkbinder.dto.ProjectCustomSetting;
import jp.co.opentone.bsol.linkbinder.dto.SearchCorresponTypeResult;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.WorkflowPattern;
import jp.co.opentone.bsol.linkbinder.dto.code.AllowApproverToBrowse;
import jp.co.opentone.bsol.linkbinder.dto.code.ForceToUseWorkflow;
import jp.co.opentone.bsol.linkbinder.dto.code.UseWhole;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponTypeCondition;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.service.admin.impl.CorresponTypeServiceImpl;
import jp.co.opentone.bsol.linkbinder.service.admin.impl.ProjectCustomSettingServiceImpl;
import jp.co.opentone.bsol.linkbinder.view.AbstractPage;
import mockit.Mock;
import mockit.MockUp;

/**
 * {@link CorresponTypeIndexPage}のテストケース.
 * @author opentone
 */
public class CorresponTypeIndexPageTest extends AbstractTestCase {

    /**
     * テスト対象.
     */
    @Resource
    private CorresponTypeIndexPage page;

    /**
     * 検証用表示データ.
     */
    private SearchCorresponTypeResult result;

    /**
     * 検証用表示データ.
     */
    private ProjectCustomSetting pcsResult;

    /**
     * テストの前準備.
     */
    @BeforeClass
    public static void testSetUp() {
        FacesContextMock.initialize();
        new MockAbstractPage();
        new MockCorresponTypeService();
        new MockProjectCustomSettingService();
        new MockViewHelper();
        new MockDateUtil();
    }

    /**
     * 後始末.
     */
    @AfterClass
    public static void testTeardown() {
        new MockAbstractPage().tearDown();
        new MockCorresponTypeService().tearDown();
        new MockProjectCustomSettingService().tearDown();
        new MockViewHelper().tearDown();
        new MockDateUtil().tearDown();
        FacesContextMock.tearDown();
    }

    @Before
    public void setUp() {
        result = new SearchCorresponTypeResult();
        result.setCorresponTypeList(createCorresponTypeList());
        result.setCount(50);

        pcsResult = new ProjectCustomSetting();
        pcsResult.setUseCorresponAccessControl(true);

        page.setCorresponTypeList(new ArrayList<CorresponType>());
        page.setSelectTypeList(new ArrayList<SelectItem>());
        if (page.getCondition() != null) {
            page.getCondition().setProjectId(null);
        }

        MockAbstractPage.RET_PROJECT_ID = null;
        MockAbstractPage.IS_SYSTEM_ADMIN = false;
        MockAbstractPage.IS_PROJECT_ADMIN = false;
    }

    @After
    public void tearDown() {
        MockAbstractPage.RET_PROJECT_ID = null;
        MockAbstractPage.IS_SYSTEM_ADMIN = false;
        MockAbstractPage.IS_PROJECT_ADMIN = false;
        MockCorresponTypeService.RET_SEARCH = null;
        MockCorresponTypeService.RET_EXCEL = null;
        MockCorresponTypeService.RET_ASSIGN_TO = null;
        MockCorresponTypeService.CRT_CORRESPONTYPE = null;
        MockCorresponTypeService.RET_SEARCH_NOT_ASSIGNED = null;
        MockCorresponTypeService.CRT_DELETE = null;
        MockCorresponTypeService.EX_SEARCH = null;
        MockCorresponTypeService.SET_SEARCH_ERROR_PAGE = 0;
        MockProjectCustomSettingService.RET_SEARCH = null;
        MockViewHelper.RET_DATA = null;
        MockViewHelper.RET_FILENAME = null;
        MockDateUtil.RET_DATE = null;

        page.setProjectId(null);
    }

    /**
     * 初期化アクションのテスト.
     * SystemAdminの場合.
     * @throws Exception
     */
    @Test
    public void testInitializeAdmin() throws Exception {
        MockCorresponTypeService.RET_SEARCH = result;
        MockProjectCustomSettingService.RET_SEARCH = pcsResult;

        page.initialize();

        assertEquals(result.getCorresponTypeList(), page.getCorresponTypeList());
        assertEquals(50, page.getDataCount());
        assertEquals(0, page.getSelectTypeList().size());

        // 初期設定を確認
        assertFalse(page.getPrevious());
        assertTrue(page.getNext());
        assertEquals(10, page.getPageIndex());
        assertEquals(1, page.getPageNo());
        assertEquals(10, page.getPageRowNum());
        assertNull(page.getProjectId());
        // 検索条件
        assertNull(page.getCondition().getProjectId());
        assertEquals(UseWhole.ALL, page.getCondition().getUseWhole());
        assertEquals(1, page.getCondition().getPageNo());
        assertEquals(10, page.getCondition().getPageRowNum());
    }

    /**
     * 初期化アクションのテスト.
     * ProjectAdminの場合.
     * @throws Exception
     */
    @Test
    public void testInitializeProject() throws Exception {
        MockCorresponTypeService.RET_SEARCH = result;
        MockProjectCustomSettingService.RET_SEARCH = pcsResult;

        MockAbstractPage.RET_PROJECT_ID = "PJ1";

        List<CorresponType> projectNotCorresponTypeList = createProjectNotCorresponTypeList();

        MockCorresponTypeService.RET_SEARCH_NOT_ASSIGNED = projectNotCorresponTypeList;

        page.initialize();

        assertEquals(result.getCorresponTypeList(), page.getCorresponTypeList());
        assertEquals(50, page.getDataCount());
        for (int i = 0; i < page.getSelectTypeList().size(); i++) {
            SelectItem item = page.getSelectTypeList().get(i);
            CorresponType selectType = projectNotCorresponTypeList.get(i);
            assertEquals(String.valueOf(selectType.getId()), item.getValue().toString());
            assertEquals(selectType.getLabel(), item.getLabel());
        }

        // 初期設定を確認
        assertFalse(page.getPrevious());
        assertTrue(page.getNext());
        assertEquals(10, page.getPageIndex());
        assertEquals(1, page.getPageNo());
        assertEquals(10, page.getPageRowNum());
        assertEquals("PJ1", page.getProjectId());
        // 検索条件
        assertNull(page.getCondition().getUseWhole());
        assertEquals("PJ1", page.getCondition().getProjectId());
        assertEquals(1, page.getCondition().getPageNo());
        assertEquals(10, page.getCondition().getPageRowNum());
        assertTrue(page.isUseAccessControl());
    }

    /**
     * 検索アクションのテスト.
     * @throws Exception
     */
    @Test
    public void testSearchAdmin() throws Exception {
        // アクション名
        page.setActionName("Search");

        MockCorresponTypeService.RET_SEARCH = result;

        page.setCondition(new SearchCorresponTypeCondition());
        // 検索条件を入力
        String name = "SearchName";
        String type = "SearchType";
        page.setName(name);
        page.setCorresponType(type);
        page.setPageNo(3);

        page.search();

        assertEquals(1, page.getPageNo()); // 初期化

        assertEquals(result.getCorresponTypeList().toString(), page.getCorresponTypeList()
            .toString());
        assertEquals(50, page.getDataCount());
        // 検索条件
        assertEquals(1, page.getCondition().getPageNo());
        assertEquals(name, page.getCondition().getName());
        assertEquals(type, page.getCondition().getCorresponType());

        // 再設定
        List<CorresponType> newList = createNewList();
        result = new SearchCorresponTypeResult();
        result.setCorresponTypeList(createNewList());

        result.setCount(54); // 増加
        MockCorresponTypeService.RET_SEARCH = result;

        // 検索条件を入力
        String newName = "NewSearchName";
        String newType = "NewSearchType";
        page.setName(newName);
        page.setCorresponType(newType);
        page.setPageNo(5);

        page.search();

        assertEquals(1, page.getPageNo());
        assertEquals(newList.toString(), page.getCorresponTypeList().toString());
        assertEquals(54, page.getDataCount()); // 増加
        // 検索条件
        assertEquals(1, page.getCondition().getPageNo());
        assertEquals(newName, page.getCondition().getName());
        assertEquals(newType, page.getCondition().getCorresponType());
    }

    /**
     * 検索アクションのテスト.
     * @throws Exception
     */
    @Test
    public void testSearchProject() throws Exception {
        // アクション名
        page.setActionName("Search");

        page.setProjectId("PJ1");

        MockCorresponTypeService.RET_SEARCH = result;
        List<CorresponType> projectNotCorresponTypeList = createProjectNotCorresponTypeList();
        MockCorresponTypeService.RET_SEARCH_NOT_ASSIGNED = projectNotCorresponTypeList;

        page.setCondition(new SearchCorresponTypeCondition());
        // 検索条件を入力
        String name = "SearchName";
        String type = "SearchType";
        page.setName(name);
        page.setCorresponType(type);
        page.setPageNo(3);

        page.search();

        assertEquals(1, page.getPageNo()); // 初期化

        assertEquals(result.getCorresponTypeList().toString(), page.getCorresponTypeList()
            .toString());
        assertEquals(50, page.getDataCount());
        for (int i = 0; i < page.getSelectTypeList().size(); i++) {
            SelectItem item = page.getSelectTypeList().get(i);
            CorresponType selectType = projectNotCorresponTypeList.get(i);
            assertEquals(String.valueOf(selectType.getId()), item.getValue().toString());
            assertEquals(selectType.getLabel(), item.getLabel());
        }
        // 検索条件
        assertEquals(1, page.getCondition().getPageNo());
        assertEquals(name, page.getCondition().getName());
        assertEquals(type, page.getCondition().getCorresponType());

        // 再設定
        List<CorresponType> newList = createNewList();
        List<CorresponType> newSelectList = createNewSelectList();
        result = new SearchCorresponTypeResult();
        result.setCorresponTypeList(createNewList());

        MockCorresponTypeService.RET_SEARCH_NOT_ASSIGNED = newSelectList;
        result.setCount(54); // 増加
        MockCorresponTypeService.RET_SEARCH = result;

        // 検索条件を入力
        String newName = "NewSearchName";
        String newType = "NewSearchType";
        page.setName(newName);
        page.setCorresponType(newType);
        page.setPageNo(5);

        page.search();

        assertEquals(1, page.getPageNo());
        assertEquals(newList.toString(), page.getCorresponTypeList().toString());
        assertEquals(54, page.getDataCount()); // 増加
        for (int i = 0; i < page.getSelectTypeList().size(); i++) {
            SelectItem item = page.getSelectTypeList().get(i);
            CorresponType selectType = newSelectList.get(i);
            assertEquals(String.valueOf(selectType.getId()), item.getValue().toString());
            assertEquals(selectType.getLabel(), item.getLabel());
        }
        // 検索条件
        assertEquals(1, page.getCondition().getPageNo());
        assertEquals(newName, page.getCondition().getName());
        assertEquals(newType, page.getCondition().getCorresponType());
    }

    /**
     * Excelダウンロードアクションのテスト.
     * @throws Exception
     */
    @Test
    public void testDownloadExcel() throws Exception {
        // 変換後のデータを作成
        byte[] expected = "TEST-OUT-EXCEL".getBytes();
        MockCorresponTypeService.RET_EXCEL = expected;

        Date date = new GregorianCalendar(2009, 3, 1, 1, 1, 1).getTime();
        MockDateUtil.RET_DATE = date;

        MockCorresponTypeService.RET_SEARCH = result;
        page.setCondition(new SearchCorresponTypeCondition());

        page.downloadExcel();

        assertArrayEquals(expected, MockViewHelper.RET_DATA);
        assertEquals("20090401010101.xls", MockViewHelper.RET_FILENAME);

        // 2回目の作成
        byte[] newExpected = "NEW-TEST-OUT-EXCEL".getBytes();
        MockCorresponTypeService.RET_EXCEL = newExpected;

        Date newDate = new GregorianCalendar(2009, 3, 1, 11, 11, 11).getTime();
        MockDateUtil.RET_DATE = newDate;

        page.downloadExcel();

        assertArrayEquals(newExpected, MockViewHelper.RET_DATA);
        assertEquals("20090401111111.xls", MockViewHelper.RET_FILENAME);
    }

    /**
     * 前へ（<<）アクションのテスト.
     * @throws Exception
     */
    @Test
    public void testMovePreviousAdmin() throws Exception {
        // アクション名
        page.setActionName("Previous");
        // ダミーのコレポン文書を設定
        MockCorresponTypeService.RET_SEARCH = result;

        page.setCondition(new SearchCorresponTypeCondition());
        // 3ページ目から1つ戻る
        page.setPageNo(3);
        page.movePrevious();

        assertEquals(2, page.getPageNo());
        assertEquals(result.getCorresponTypeList().toString(), page.getCorresponTypeList()
            .toString());
        assertEquals(50, page.getDataCount());
        // 検索条件
        assertEquals(2, page.getCondition().getPageNo());

        // 再設定
        List<CorresponType> newList = createNewList();
        result = new SearchCorresponTypeResult();
        result.setCorresponTypeList(createNewList());
        result.setCount(54); // 増加
        MockCorresponTypeService.RET_SEARCH = result;

        // さらに1つ戻る
        page.movePrevious();

        assertEquals(1, page.getPageNo());
        assertEquals(newList.toString(), page.getCorresponTypeList().toString());
        assertEquals(54, page.getDataCount()); // 増加
        // 検索条件
        assertEquals(1, page.getCondition().getPageNo());
    }

    /**
     * 前へ（<<）アクションのテスト.
     * @throws Exception
     */
    @Test
    public void testMovePreviousProject() throws Exception {
        // アクション名
        page.setActionName("Previous");
        page.setProjectId("PJ1");

        // ダミーのコレポン文書を設定
        MockCorresponTypeService.RET_SEARCH = result;
        List<CorresponType> projectNotCorresponTypeList = createProjectNotCorresponTypeList();
        MockCorresponTypeService.RET_SEARCH_NOT_ASSIGNED = projectNotCorresponTypeList;

        page.setCondition(new SearchCorresponTypeCondition());
        // 3ページ目から1つ戻る
        page.setPageNo(3);
        page.movePrevious();

        assertEquals(2, page.getPageNo());
        assertEquals(result.getCorresponTypeList().toString(), page.getCorresponTypeList()
            .toString());
        assertEquals(50, page.getDataCount());
        for (int i = 0; i < page.getSelectTypeList().size(); i++) {
            SelectItem item = page.getSelectTypeList().get(i);
            CorresponType selectType = projectNotCorresponTypeList.get(i);
            assertEquals(String.valueOf(selectType.getId()), item.getValue().toString());
            assertEquals(selectType.getLabel(), item.getLabel());
        }
        // 検索条件
        assertEquals(2, page.getCondition().getPageNo());

        // 再設定
        List<CorresponType> newList = createNewList();
        List<CorresponType> newSelectList = createNewSelectList();
        result = new SearchCorresponTypeResult();
        result.setCorresponTypeList(createNewList());
        MockCorresponTypeService.RET_SEARCH_NOT_ASSIGNED = newSelectList;
        result.setCount(54); // 増加
        MockCorresponTypeService.RET_SEARCH = result;

        // さらに1つ戻る
        page.movePrevious();

        assertEquals(1, page.getPageNo());
        assertEquals(newList.toString(), page.getCorresponTypeList().toString());
        assertEquals(54, page.getDataCount()); // 増加
        for (int i = 0; i < page.getSelectTypeList().size(); i++) {
            SelectItem item = page.getSelectTypeList().get(i);
            CorresponType selectType = newSelectList.get(i);
            assertEquals(String.valueOf(selectType.getId()), item.getValue().toString());
            assertEquals(selectType.getLabel(), item.getLabel());
        }
        // 検索条件
        assertEquals(1, page.getCondition().getPageNo());
    }

    /**
     * 次へ（>>）アクションのテスト.
     * @throws Exception
     */
    @Test
    public void testMoveNextAdmin() throws Exception {
        // アクション名
        page.setActionName("Next");

        // ダミーのコレポン文書を設定
        MockCorresponTypeService.RET_SEARCH = result;

        page.setCondition(new SearchCorresponTypeCondition());
        // 1ページ目から1つ進む
        page.setPageNo(1);
        page.moveNext();

        assertEquals(2, page.getPageNo());
        assertEquals(result.getCorresponTypeList().toString(), page.getCorresponTypeList()
            .toString());
        assertEquals(50, page.getDataCount());
        // 検索条件
        assertEquals(2, page.getCondition().getPageNo());

        // 再設定
        List<CorresponType> newList = createNewList();
        result = new SearchCorresponTypeResult();
        result.setCorresponTypeList(createNewList());
        result.setCount(54); // 増加
        MockCorresponTypeService.RET_SEARCH = result;

        // さらに1つ進む
        page.moveNext();

        assertEquals(3, page.getPageNo());
        assertEquals(newList.toString(), page.getCorresponTypeList().toString());
        assertEquals(54, page.getDataCount()); // 増加
        // 検索条件
        assertEquals(3, page.getCondition().getPageNo());
    }

    /**
     * 次へ（>>）アクションのテスト.
     * @throws Exception
     */
    @Test
    public void testMoveNextProject() throws Exception {
        // アクション名
        page.setActionName("Next");
        page.setProjectId("PJ1");

        // ダミーのコレポン文書を設定
        MockCorresponTypeService.RET_SEARCH = result;

        List<CorresponType> projectNotCorresponTypeList = createProjectNotCorresponTypeList();
        MockCorresponTypeService.RET_SEARCH_NOT_ASSIGNED = projectNotCorresponTypeList;

        page.setCondition(new SearchCorresponTypeCondition());
        // 1ページ目から1つ進む
        page.setPageNo(1);
        page.moveNext();

        assertEquals(2, page.getPageNo());
        assertEquals(result.getCorresponTypeList().toString(), page.getCorresponTypeList()
            .toString());
        assertEquals(50, page.getDataCount());
        for (int i = 0; i < page.getSelectTypeList().size(); i++) {
            SelectItem item = page.getSelectTypeList().get(i);
            CorresponType selectType = projectNotCorresponTypeList.get(i);
            assertEquals(String.valueOf(selectType.getId()), item.getValue().toString());
            assertEquals(selectType.getLabel(), item.getLabel());
        }
        // 検索条件
        assertEquals(2, page.getCondition().getPageNo());

        // 再設定
        List<CorresponType> newList = createNewList();
        List<CorresponType> newSelectList = createNewSelectList();
        result = new SearchCorresponTypeResult();
        result.setCorresponTypeList(createNewList());
        MockCorresponTypeService.RET_SEARCH_NOT_ASSIGNED = newSelectList;
        result.setCount(54); // 増加
        MockCorresponTypeService.RET_SEARCH = result;

        // さらに1つ進む
        page.moveNext();

        assertEquals(3, page.getPageNo());
        assertEquals(newList.toString(), page.getCorresponTypeList().toString());
        assertEquals(54, page.getDataCount()); // 増加
        for (int i = 0; i < page.getSelectTypeList().size(); i++) {
            SelectItem item = page.getSelectTypeList().get(i);
            CorresponType selectType = newSelectList.get(i);
            assertEquals(String.valueOf(selectType.getId()), item.getValue().toString());
            assertEquals(selectType.getLabel(), item.getLabel());
        }
        // 検索条件
        assertEquals(3, page.getCondition().getPageNo());
    }

    /**
     * ページ移動アクションのテスト.
     * @throws Exception
     */
    @Test
    public void testChangePageAdmin() throws Exception {
        // アクション名
        page.setActionName("Change Page");

        // ダミーのコレポン文書を設定
        MockCorresponTypeService.RET_SEARCH = result;

        page.setCondition(new SearchCorresponTypeCondition());
        // 4ページ目へ
        page.setPageNo(4);
        page.changePage();

        assertEquals(4, page.getPageNo());
        assertEquals(result.getCorresponTypeList().toString(), page.getCorresponTypeList()
            .toString());
        assertEquals(50, page.getDataCount());
        // 検索条件
        assertEquals(4, page.getCondition().getPageNo());

        // 再設定
        List<CorresponType> newList = createNewList();
        result = new SearchCorresponTypeResult();
        result.setCorresponTypeList(createNewList());
        result.setCount(54); // 増加
        MockCorresponTypeService.RET_SEARCH = result;

        // 2ページ目へ
        page.setPageNo(2);
        page.changePage();

        assertEquals(2, page.getPageNo());
        assertEquals(newList.toString(), page.getCorresponTypeList().toString());
        assertEquals(54, page.getDataCount()); // 増加

        // 検索条件
        assertEquals(2, page.getCondition().getPageNo());
    }

    /**
     * ページ移動アクションのテスト.
     * @throws Exception
     */
    @Test
    public void testChangePageProject() throws Exception {
        // アクション名
        page.setActionName("Change Page");
        page.setProjectId("PJ1");

        // ダミーのコレポン文書を設定
        MockCorresponTypeService.RET_SEARCH = result;
        List<CorresponType> projectNotCorresponTypeList = createProjectNotCorresponTypeList();
        MockCorresponTypeService.RET_SEARCH_NOT_ASSIGNED = projectNotCorresponTypeList;

        page.setCondition(new SearchCorresponTypeCondition());
        // 4ページ目へ
        page.setPageNo(4);
        page.changePage();

        assertEquals(4, page.getPageNo());
        assertEquals(result.getCorresponTypeList().toString(), page.getCorresponTypeList()
            .toString());
        assertEquals(50, page.getDataCount());
        for (int i = 0; i < page.getSelectTypeList().size(); i++) {
            SelectItem item = page.getSelectTypeList().get(i);
            CorresponType selectType = projectNotCorresponTypeList.get(i);
            assertEquals(String.valueOf(selectType.getId()), item.getValue().toString());
            assertEquals(selectType.getLabel(), item.getLabel());
        }
        // 検索条件
        assertEquals(4, page.getCondition().getPageNo());

        // 再設定
        List<CorresponType> newList = createNewList();
        List<CorresponType> newSelectList = createNewSelectList();
        result = new SearchCorresponTypeResult();
        result.setCorresponTypeList(createNewList());
        MockCorresponTypeService.RET_SEARCH_NOT_ASSIGNED = newSelectList;
        result.setCount(54); // 増加
        MockCorresponTypeService.RET_SEARCH = result;

        // 2ページ目へ
        page.setPageNo(2);
        page.changePage();

        assertEquals(2, page.getPageNo());
        assertEquals(newList.toString(), page.getCorresponTypeList().toString());
        assertEquals(54, page.getDataCount()); // 増加
        for (int i = 0; i < page.getSelectTypeList().size(); i++) {
            SelectItem item = page.getSelectTypeList().get(i);
            CorresponType selectType = newSelectList.get(i);
            assertEquals(String.valueOf(selectType.getId()), item.getValue().toString());
            assertEquals(selectType.getLabel(), item.getLabel());
        }
        // 検索条件
        assertEquals(2, page.getCondition().getPageNo());
    }

    /**
     * 前へ（<<）リンク表示判定のテスト.
     */
    @Test
    public void testGetPrevious() {
        // 1以上なら表示
        page.setPageNo(0);
        assertFalse(page.getPrevious());
        page.setPageNo(1);
        assertFalse(page.getPrevious());

        page.setPageNo(2);
        assertTrue(page.getPrevious());
        page.setPageNo(5);
        assertTrue(page.getPrevious());
        page.setPageNo(999);
        assertTrue(page.getPrevious());
    }

    /**
     * 次へ（>>）リンク表示判定のテスト.
     */
    @Test
    public void testGetNext() {
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
     * コレポン文書種別をプロジェクトで利用可能にするテスト.
     * @throws Exception
     */
    @Test
    public void testAssignTo() throws Exception {
        MockAbstractPage.RET_PROJECT_ID = "PJ1";

        List<CorresponType> corresponTypeList = new ArrayList<CorresponType>();
        CorresponType ct = new CorresponType();
        ct.setId(1L);
        ct.setCorresponType("TEST1");
        ct.setName("TEST1");

        WorkflowPattern wp = new WorkflowPattern();
        wp.setId(1L);
        wp.setName("pattern1");
        wp.setWorkflowCd("001");

        ct.setWorkflowPattern(wp);
        ct.setAllowApproverToBrowse(AllowApproverToBrowse.VISIBLE);
        ct.setForceToUseWorkflow(ForceToUseWorkflow.REQUIRED);
        ct.setUseWhole(UseWhole.ALL);
        User user = new User();
        user.setEmpNo("ZZA01");
        user.setNameE("Taro Yamada");
        ct.setCreatedBy(user);
        ct.setUpdatedBy(user);
        ct.setVersionNo(1L);
        ct.setDeleteNo(0L);
        corresponTypeList.add(ct);

        ct = new CorresponType();
        ct.setId(2L);
        ct.setCorresponType("TEST2");
        ct.setName("TEST2");

        wp = new WorkflowPattern();
        wp.setId(1L);
        wp.setName("pattern1");
        wp.setWorkflowCd("001");

        ct.setWorkflowPattern(wp);
        ct.setAllowApproverToBrowse(AllowApproverToBrowse.VISIBLE);
        ct.setForceToUseWorkflow(ForceToUseWorkflow.REQUIRED);
        ct.setUseWhole(UseWhole.ALL);
        user.setEmpNo("ZZA01");
        user.setNameE("Taro Yamada");
        ct.setCreatedBy(user);
        ct.setUpdatedBy(user);
        ct.setVersionNo(1L);
        ct.setDeleteNo(0L);
        corresponTypeList.add(ct);

        ct = new CorresponType();
        ct.setId(3L);
        ct.setCorresponType("TEST3");
        ct.setName("TEST3");

        wp = new WorkflowPattern();
        wp.setId(2L);
        wp.setName("pattern2");
        wp.setWorkflowCd("002");

        ct.setWorkflowPattern(wp);
        ct.setAllowApproverToBrowse(AllowApproverToBrowse.INVISIBLE);
        ct.setForceToUseWorkflow(ForceToUseWorkflow.OPTIONAL);
        ct.setUseWhole(UseWhole.ALL);
        user.setEmpNo("ZZA01");
        user.setNameE("Taro Yamada");
        ct.setCreatedBy(user);
        ct.setUpdatedBy(user);
        ct.setVersionNo(1L);
        ct.setDeleteNo(0L);
        corresponTypeList.add(ct);

        page.setAssignableCorresponTypeList(corresponTypeList);
        page.setSelectType(1L);

        // 期待されるメッセージをセット
        FacesContextMock.EXPECTED_MESSAGE =
                new FacesMessage(
                    FacesMessage.SEVERITY_INFO, FacesMessage.SEVERITY_INFO.toString(),
                    createExpectedMessageString(Messages
                        .getMessageAsString(CORRESPON_TYPE_ASSIGNED), null));

        MockCorresponTypeService.RET_SEARCH = result;
        MockCorresponTypeService.RET_SEARCH_NOT_ASSIGNED =
                createProjectNotCorresponTypeList();
        MockCorresponTypeService.RET_ASSIGN_TO = 10L;
        page.setCondition(new SearchCorresponTypeCondition());
        page.setPageRowNum(10);
        page.setPageIndex(10);
        page.setPageNo(1);

        page.assignTo();

        CorresponType actual = MockCorresponTypeService.CRT_CORRESPONTYPE;

        assertEquals(String.valueOf(1L), actual.getId().toString());
        assertEquals("TEST1", actual.getCorresponType());
        assertEquals("TEST1", actual.getName());
        assertEquals(String.valueOf(1L), actual.getWorkflowPattern().getId().toString());
        assertEquals("pattern1", actual.getWorkflowPattern().getName());
        assertEquals("001", actual.getWorkflowPattern().getWorkflowCd());
        assertEquals(AllowApproverToBrowse.VISIBLE, actual.getAllowApproverToBrowse());
        assertEquals(ForceToUseWorkflow.REQUIRED, actual.getForceToUseWorkflow());
        assertEquals(UseWhole.ALL, actual.getUseWhole());
        assertEquals(user.toString(), actual.getCreatedBy().toString());
        assertEquals(user.toString(), actual.getUpdatedBy().toString());
        assertEquals(String.valueOf(1L), actual.getVersionNo().toString());
        assertEquals(String.valueOf(0L), actual.getDeleteNo().toString());

    }

    /**
     * 新規登録処理を検証する.
     * @throws Exception
     */
    @Test
    public void testCreate() throws Exception {
        page.setCondition(new SearchCorresponTypeCondition());
        String actual = page.create();
        assertEquals("corresponTypeEdit", actual);
    }

    /**
     * 更新処理を検証する.
     * @throws Exception
     */
    @Test
    public void testUpdate() throws Exception {
        page.setCondition(new SearchCorresponTypeCondition());
        DataModel dataModel = new ListDataModel(result.getCorresponTypeList());
        dataModel.setRowIndex(1);
        page.setDataModel(dataModel);
        page.setCorrTypeId(2L);
        page.setCorrTypeProjId(12L);
        String actual = page.update();
        assertEquals("corresponTypeEdit?id=2&projectCorresponTypeId=12", actual);
    }

    /**
     * カスタムフィールド削除アクションのテスト.
     * @throws Exception
     */
    @Test
    public void testDelete() throws Exception {
        // テストに必要なデータを作成
        List<CorresponType> lstCorresponType = result.getCorresponTypeList();
        page.setCorresponTypeList(lstCorresponType);
        page.getDataModel().setRowIndex(2); // 3行目を選択

        // 比較用
        CorresponType deleteCorresponType = lstCorresponType.get(2); // 3行目を選択
        page.setCorrTypeUseWhole(deleteCorresponType.getUseWhole());
        page.setCorrTypeId(deleteCorresponType.getId());
        page.setCorrTypeVerNo(deleteCorresponType.getVersionNo());
        page.setCorrTypeProjId(deleteCorresponType.getProjectCorresponTypeId());


        // 期待されるメッセージをセット
        FacesContextMock.EXPECTED_CLIENT_ID = null;
        FacesContextMock.EXPECTED_MESSAGE =
                new FacesMessage(
                    FacesMessage.SEVERITY_INFO,
                    FacesMessage.SEVERITY_INFO.toString(),
                    createExpectedMessageString(Messages.getMessageAsString(CORRESPON_TYPE_DELETED),
                        null));

        MockCorresponTypeService.RET_SEARCH = result;

        MockCorresponTypeService.RET_SEARCH_NOT_ASSIGNED = createNewList();

        MockAbstractPage.RET_PROJECT_ID = "PJ1";

        MockAbstractPage.IS_SYSTEM_ADMIN = true;

        page.setCondition(new SearchCorresponTypeCondition());

        page.setPageNo(2);

        page.delete();

        CorresponType actual = MockCorresponTypeService.CRT_DELETE;

        assertEquals(deleteCorresponType.getId(), actual.getId());
        assertEquals(deleteCorresponType.getVersionNo(), actual.getVersionNo());

        assertEquals(2, page.getPageNo());

    }
    /**
     * テスト用のコレポン文書種別リストを作成する.
     * @return
     */
    private List<CorresponType> createCorresponTypeList() {
        List<CorresponType> list = new ArrayList<CorresponType>();

        CorresponType type = new CorresponType();
        type.setId(1L);
        type.setProjectCorresponTypeId(11L);
        type.setProjectId("PJ1");
        type.setProjectNameE("Project One");
        type.setCorresponType("Type-1");
        type.setName("Name-1");
        WorkflowPattern pattern = new WorkflowPattern();
        pattern.setId(1L);
        pattern.setName("Pattern 1");
        pattern.setWorkflowCd("001");
        type.setWorkflowPattern(pattern);
        type.setAllowApproverToBrowse(AllowApproverToBrowse.INVISIBLE);
        type.setForceToUseWorkflow(ForceToUseWorkflow.OPTIONAL);
        type.setUseWhole(UseWhole.ALL);

        User user = new User();
        user.setEmpNo("ZZA01");
        user.setNameE("Taro Yamada");

        type.setCreatedBy(user);
        type.setUpdatedBy(user);
        list.add(type);

        type = new CorresponType();
        type.setId(2L);
        type.setProjectCorresponTypeId(12L);
        type.setProjectId("PJ1");
        type.setProjectNameE("Project One");
        type.setCorresponType("Type-2");
        type.setName("Name-2");
        pattern = new WorkflowPattern();
        pattern.setId(2L);
        pattern.setName("Pattern 2");
        pattern.setWorkflowCd("002");
        type.setWorkflowPattern(pattern);
        type.setAllowApproverToBrowse(AllowApproverToBrowse.VISIBLE);
        type.setForceToUseWorkflow(ForceToUseWorkflow.REQUIRED);
        type.setUseWhole(UseWhole.ALL);
        type.setCreatedBy(user);
        type.setUpdatedBy(user);
        list.add(type);

        type = new CorresponType();
        type.setId(3L);
        type.setProjectCorresponTypeId(13L);
        type.setProjectId("PJ1");
        type.setProjectNameE("Project One");
        type.setCorresponType("Type-3");
        type.setName("Name-3");
        pattern = new WorkflowPattern();
        pattern.setId(3L);
        pattern.setName("Pattern 3");
        pattern.setWorkflowCd("003");
        type.setWorkflowPattern(pattern);
        type.setAllowApproverToBrowse(AllowApproverToBrowse.INVISIBLE);
        type.setForceToUseWorkflow(ForceToUseWorkflow.REQUIRED);
        type.setUseWhole(UseWhole.EACH);
        type.setCreatedBy(user);
        type.setUpdatedBy(user);
        list.add(type);

        type = new CorresponType();
        type.setId(4L);
        type.setProjectCorresponTypeId(14L);
        type.setProjectId("PJ1");
        type.setProjectNameE("Project One");
        type.setCorresponType("Type-4");
        type.setName("Name-4");
        pattern = new WorkflowPattern();
        pattern.setId(1L);
        pattern.setName("Pattern 1");
        pattern.setWorkflowCd("001");
        type.setWorkflowPattern(pattern);
        type.setAllowApproverToBrowse(AllowApproverToBrowse.VISIBLE);
        type.setForceToUseWorkflow(ForceToUseWorkflow.OPTIONAL);
        type.setUseWhole(UseWhole.EACH);
        type.setCreatedBy(user);
        type.setUpdatedBy(user);
        list.add(type);

        type = new CorresponType();
        type.setId(5L);
        type.setProjectCorresponTypeId(15L);
        type.setProjectId("PJ1");
        type.setProjectNameE("Project One");
        type.setCorresponType("Type-5");
        type.setName("Name-5");
        pattern = new WorkflowPattern();
        pattern.setId(2L);
        pattern.setName("Pattern 2");
        pattern.setWorkflowCd("002");
        type.setWorkflowPattern(pattern);
        type.setAllowApproverToBrowse(AllowApproverToBrowse.VISIBLE);
        type.setForceToUseWorkflow(ForceToUseWorkflow.REQUIRED);
        type.setUseWhole(UseWhole.EACH);
        type.setCreatedBy(user);
        type.setUpdatedBy(user);
        list.add(type);

        return list;
    }

    /**
     * テスト用のコプロジェクトに属していないレポン文書種別リストを作成する.
     * @return
     */
    private List<CorresponType> createProjectNotCorresponTypeList() {
        List<CorresponType> list = new ArrayList<CorresponType>();

        CorresponType type = new CorresponType();
        type.setId(11L);
        type.setProjectCorresponTypeId(21L);
        type.setName("Name-1");
        WorkflowPattern pattern = new WorkflowPattern();
        pattern.setId(1L);
        pattern.setName("Pattern 1");
        pattern.setWorkflowCd("001");
        type.setWorkflowPattern(pattern);
        type.setAllowApproverToBrowse(AllowApproverToBrowse.INVISIBLE);
        type.setForceToUseWorkflow(ForceToUseWorkflow.OPTIONAL);
        type.setUseWhole(UseWhole.ALL);
        list.add(type);

        type = new CorresponType();
        type.setId(12L);
        type.setProjectCorresponTypeId(22L);
        type.setName("Name-2");
        pattern = new WorkflowPattern();
        pattern.setId(2L);
        pattern.setName("Pattern 2");
        pattern.setWorkflowCd("002");
        type.setWorkflowPattern(pattern);
        type.setAllowApproverToBrowse(AllowApproverToBrowse.VISIBLE);
        type.setForceToUseWorkflow(ForceToUseWorkflow.REQUIRED);
        type.setUseWhole(UseWhole.ALL);
        list.add(type);

        return list;
    }

    private List<CorresponType> createNewList() {
        List<CorresponType> list = new ArrayList<CorresponType>();

        CorresponType type = new CorresponType();
        type.setId(11L);
        type.setProjectCorresponTypeId(111L);
        type.setProjectId("PJ1");
        type.setProjectNameE("Project One");
        type.setCorresponType("Type-1");
        type.setName("Name-1");
        WorkflowPattern pattern = new WorkflowPattern();
        pattern.setId(1L);
        pattern.setName("Pattern 1");
        pattern.setWorkflowCd("001");
        type.setWorkflowPattern(pattern);
        type.setAllowApproverToBrowse(AllowApproverToBrowse.INVISIBLE);
        type.setForceToUseWorkflow(ForceToUseWorkflow.OPTIONAL);
        type.setUseWhole(UseWhole.ALL);
        list.add(type);

        type = new CorresponType();
        type.setId(12L);
        type.setProjectCorresponTypeId(112L);
        type.setProjectId("PJ1");
        type.setProjectNameE("Project One");
        type.setCorresponType("Type-2");
        type.setName("Name-2");
        pattern = new WorkflowPattern();
        pattern.setId(2L);
        pattern.setName("Pattern 2");
        pattern.setWorkflowCd("002");
        type.setWorkflowPattern(pattern);
        type.setAllowApproverToBrowse(AllowApproverToBrowse.VISIBLE);
        type.setForceToUseWorkflow(ForceToUseWorkflow.REQUIRED);
        type.setUseWhole(UseWhole.ALL);
        list.add(type);

        type = new CorresponType();
        type.setId(13L);
        type.setProjectCorresponTypeId(113L);
        type.setProjectId("PJ1");
        type.setProjectNameE("Project One");
        type.setCorresponType("Type-3");
        type.setName("Name-3");
        pattern = new WorkflowPattern();
        pattern.setId(3L);
        pattern.setName("Pattern 3");
        pattern.setWorkflowCd("003");
        type.setWorkflowPattern(pattern);
        type.setAllowApproverToBrowse(AllowApproverToBrowse.INVISIBLE);
        type.setForceToUseWorkflow(ForceToUseWorkflow.REQUIRED);
        type.setUseWhole(UseWhole.EACH);
        list.add(type);

        type = new CorresponType();
        type.setId(14L);
        type.setProjectCorresponTypeId(114L);
        type.setProjectId("PJ1");
        type.setProjectNameE("Project One");
        type.setCorresponType("Type-4");
        type.setName("Name-4");
        pattern = new WorkflowPattern();
        pattern.setId(1L);
        pattern.setName("Pattern 1");
        pattern.setWorkflowCd("001");
        type.setWorkflowPattern(pattern);
        type.setAllowApproverToBrowse(AllowApproverToBrowse.VISIBLE);
        type.setForceToUseWorkflow(ForceToUseWorkflow.OPTIONAL);
        type.setUseWhole(UseWhole.EACH);
        list.add(type);

        type = new CorresponType();
        type.setId(15L);
        type.setProjectCorresponTypeId(115L);
        type.setProjectId("PJ1");
        type.setProjectNameE("Project One");
        type.setCorresponType("Type-5");
        type.setName("Name-5");
        pattern = new WorkflowPattern();
        pattern.setId(2L);
        pattern.setName("Pattern 2");
        pattern.setWorkflowCd("002");
        type.setWorkflowPattern(pattern);
        type.setAllowApproverToBrowse(AllowApproverToBrowse.VISIBLE);
        type.setForceToUseWorkflow(ForceToUseWorkflow.REQUIRED);
        type.setUseWhole(UseWhole.EACH);
        list.add(type);

        return list;
    }

    private List<CorresponType> createNewSelectList() {
        List<CorresponType> list = new ArrayList<CorresponType>();

        CorresponType type = new CorresponType();
        type.setId(111L);
        type.setProjectCorresponTypeId(121L);
        type.setName("Name-1");
        WorkflowPattern pattern = new WorkflowPattern();
        pattern.setId(1L);
        pattern.setName("Pattern 1");
        pattern.setWorkflowCd("001");
        type.setWorkflowPattern(pattern);
        type.setAllowApproverToBrowse(AllowApproverToBrowse.INVISIBLE);
        type.setForceToUseWorkflow(ForceToUseWorkflow.OPTIONAL);
        type.setUseWhole(UseWhole.ALL);
        list.add(type);

        type = new CorresponType();
        type.setId(112L);
        type.setProjectCorresponTypeId(122L);
        type.setName("Name-2");
        pattern = new WorkflowPattern();
        pattern.setId(2L);
        pattern.setName("Pattern 2");
        pattern.setWorkflowCd("002");
        type.setWorkflowPattern(pattern);
        type.setAllowApproverToBrowse(AllowApproverToBrowse.VISIBLE);
        type.setForceToUseWorkflow(ForceToUseWorkflow.REQUIRED);
        type.setUseWhole(UseWhole.ALL);
        list.add(type);

        return list;
    }

    public static class MockAbstractPage extends MockUp<AbstractPage> {
        static String RET_PROJECT_ID;
        static boolean IS_SYSTEM_ADMIN;
        static boolean IS_PROJECT_ADMIN;
        @Mock
        public String getCurrentProjectId() {
            return RET_PROJECT_ID;
        }

        @Mock
        public boolean isSystemAdmin() {
            return IS_SYSTEM_ADMIN;
        }

        @Mock
        public boolean isProjectAdmin(String projectId) {
            return IS_PROJECT_ADMIN;
        }

    }

    public static class MockCorresponTypeService extends MockUp<CorresponTypeServiceImpl> {
        static SearchCorresponTypeResult RET_SEARCH;
        static byte[] RET_EXCEL;
        static Long RET_ASSIGN_TO;
        static CorresponType CRT_CORRESPONTYPE;
        static List<CorresponType> RET_SEARCH_NOT_ASSIGNED;
        static CorresponType CRT_DELETE;
        static ServiceAbortException EX_SEARCH;
        static int SET_SEARCH_ERROR_PAGE;

        @Mock
        public SearchCorresponTypeResult searchPagingList(SearchCorresponTypeCondition condition)
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
        public byte[] generateExcel(List<CorresponType> corresponTypes)
            throws ServiceAbortException {
            return RET_EXCEL;
        }

        @Mock
        public Long assignTo(CorresponType corresponType) throws ServiceAbortException {
            CRT_CORRESPONTYPE = corresponType;
            return RET_ASSIGN_TO;
        }

        @Mock
        public List<CorresponType> searchNotAssigned() {
            return RET_SEARCH_NOT_ASSIGNED;
        }

        @Mock
        public void delete(CorresponType corresponType) throws ServiceAbortException {
            CRT_DELETE = corresponType;
        }

    }

    public static class MockViewHelper extends MockUp<ViewHelper>{
        static byte[] RET_DATA;
        static String RET_FILENAME;

        @Mock
        public void download(String fileName, byte[] content) {
            RET_DATA = content;
            RET_FILENAME = fileName;
        }
    }

    public static class MockDateUtil extends MockUp<DateUtil> {
        static Date RET_DATE;

        @Mock
        public Date getNow() {
            return RET_DATE;
        }
    }

    public static class MockProjectCustomSettingService extends MockUp<ProjectCustomSettingServiceImpl> {
        static ProjectCustomSetting RET_SEARCH;

        @Mock
        public ProjectCustomSetting find(String projectId) {
            return RET_SEARCH;
        }
    }
}
