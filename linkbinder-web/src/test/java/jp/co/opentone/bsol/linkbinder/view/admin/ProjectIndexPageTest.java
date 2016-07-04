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

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.faces.application.FacesMessage;

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
import jp.co.opentone.bsol.linkbinder.dto.Project;
import jp.co.opentone.bsol.linkbinder.dto.SearchProjectResult;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchProjectCondition;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.service.admin.impl.ProjectServiceImpl;
import jp.co.opentone.bsol.linkbinder.view.AbstractPage;
import mockit.Mock;
import mockit.MockUp;

/**
 * {@link ProjectIndexPage}のテストケース.
 * @author opentone
 */
public class ProjectIndexPageTest extends AbstractTestCase {

    /**
     * テスト対象クラス
     */
    @Resource
    private ProjectIndexPage page;

    /**
     * 1ページあたりの表示件数を取得するためのKEY.
     */
    private static final String KEY_PAGE_ROW = "projectindex.pagerow";

    /**
     * ページリンクの件数を取得するためのKEY.
     */
    private static final String KEY_PAGE_INDEX = "projectindex.pageindex";

    /**
     * 検索結果を1画面に表示する件数のデフォルト値.
     */
    public static final int DEFAULT_PAGE_ROW_NUMBER = 10;
    /**
     * 検索結果をページ遷移するためのリンクを表示する数のデフォルト値.
     */
    public static final int DEFAULT_PAGE_INDEX_NUMBER = 10;

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        FacesContextMock.initialize();
        new MockAbstractPage();
        new MockProjectService();
        new MockSystemConfig();
        new MockViewHelper();
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        new MockAbstractPage().tearDown();
        new MockProjectService().tearDown();
        new MockSystemConfig().tearDown();
        new MockViewHelper().tearDown();
        FacesContextMock.tearDown();
    }

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        MockSystemConfig.VALUES = new HashMap<String, String>();
        MockSystemConfig.VALUES.put(Constants.KEY_SECURITY_FLG_SYSTEM_ADMIN, "X");
        MockAbstractPage.IS_SYSTEM_ADMIN = false;
        MockAbstractPage.RET_FILE_NAME = null;
        MockProjectService.RET_SEARCH = null;
        MockProjectService.RET_GENERATE_EXCEL = null;
        MockProjectService.EX_SEARCH = false;
        MockViewHelper.SET_DATA = null;
        MockViewHelper.SET_FILENAME = null;
        MockViewHelper.EX_GENERATE = false;
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
        page.setCondition(null);
        page.setProjectId(null);
        page.setName(null);
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.view.admin.ProjectIndexPage#initialize()} のためのテスト・メソッド.
     * ページ行・数の設定なし.
     */
    @Test
    public void testInitialize() {
        int expCount = 15;
        List<Project> expList = createProjectList();

        SearchProjectResult result = new SearchProjectResult();
        result.setCount(expCount);
        result.setProjectList(expList);
        MockProjectService.RET_SEARCH = result;

        page.initialize();

        assertEquals(DEFAULT_PAGE_ROW_NUMBER, page.getPageRowNum());
        assertEquals(DEFAULT_PAGE_INDEX_NUMBER, page.getPageIndex());
        assertEquals(1, page.getPageNo());
        assertNull(page.getProjectId());
        assertNull(page.getName());

        assertEquals(DEFAULT_PAGE_ROW_NUMBER, page.getCondition().getPageRowNum());
        assertEquals(1, page.getCondition().getPageNo());
        assertNull(page.getCondition().getProjectId());
        assertNull(page.getCondition().getNameE());

        assertEquals(expCount, page.getDataCount());
        assertEquals(expList, page.getProjectList());

        assertEquals("1-10", page.getPageDisplayNo());
        assertEquals("1", page.getPagingNo()[0]);
        assertEquals("2", page.getPagingNo()[1]);
        assertFalse(page.getPrevious());
        assertTrue(page.getNext());
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.view.admin.ProjectIndexPage#initialize()} のためのテスト・メソッド.
     * ページ行・数の設定あり.
     */
    @Test
    public void testInitializePageSettings() {
        int nPageRow = 15;
        int nPageIndex = 5;
        MockSystemConfig.VALUES.put(KEY_PAGE_ROW, String.valueOf(nPageRow));
        MockSystemConfig.VALUES.put(KEY_PAGE_INDEX, String.valueOf(nPageIndex));

        int expCount = 15;
        List<Project> expList = createProjectList();

        SearchProjectResult result = new SearchProjectResult();
        result.setCount(expCount);
        result.setProjectList(expList);
        MockProjectService.RET_SEARCH = result;

        page.initialize();

        assertEquals(nPageRow, page.getPageRowNum());
        assertEquals(nPageIndex, page.getPageIndex());
        assertEquals(1, page.getPageNo());
        assertNull(page.getProjectId());
        assertNull(page.getName());

        assertEquals(nPageRow, page.getCondition().getPageRowNum());
        assertEquals(1, page.getCondition().getPageNo());
        assertNull(page.getCondition().getProjectId());
        assertNull(page.getCondition().getNameE());

        assertEquals(expCount, page.getDataCount());
        assertEquals(expList, page.getProjectList());

        assertEquals("1-15", page.getPageDisplayNo());
        assertEquals("1", page.getPagingNo()[0]);
        assertFalse(page.getPrevious());
        assertFalse(page.getNext());

        // リセット
        page.setPageRowNum(DEFAULT_PAGE_ROW_NUMBER);
        page.setPageIndex(DEFAULT_PAGE_INDEX_NUMBER);
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.view.admin.ProjectIndexPage#search()} のためのテスト・メソッド.
     */
    @Test
    public void testSearch() {
        int expCount = 15;
        List<Project> expList = createProjectList();

        SearchProjectResult result = new SearchProjectResult();
        result.setCount(expCount);
        result.setProjectList(expList);
        MockProjectService.RET_SEARCH = result;

        page.setCondition(new SearchProjectCondition());
        String expProjectId = "PJ";
        page.setProjectId(expProjectId);
        String expName = "J-";
        page.setName(expName);

        page.search();

        assertEquals(1, page.getPageNo());
        assertEquals(expProjectId, page.getProjectId());
        assertEquals(expName, page.getName());

        assertEquals(1, page.getCondition().getPageNo());
        assertEquals(expProjectId, page.getCondition().getProjectId());
        assertEquals(expName, page.getCondition().getNameE());

        assertEquals(expCount, page.getDataCount());
        assertEquals(expList, page.getProjectList());

        assertEquals("1-10", page.getPageDisplayNo());
        assertEquals("1", page.getPagingNo()[0]);
        assertEquals("2", page.getPagingNo()[1]);
        assertFalse(page.getPrevious());
        assertTrue(page.getNext());
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.view.admin.ProjectIndexPage#search()} のためのテスト・メソッド.
     * Service内でエラー発生.
     */
    @Test
    public void testSearchError() {
        MockProjectService.EX_SEARCH = true;;

        page.setDataCount(15);
        page.setProjectList(createProjectList());

        page.setCondition(new SearchProjectCondition());
        String expProjectId = "PJ";
        page.setProjectId(expProjectId);
        String expName = "J-";
        page.setName(expName);

        // 期待されるメッセージをセット
        FacesContextMock.EXPECTED_MESSAGE =
            new FacesMessage(
                FacesMessage.SEVERITY_ERROR,
                FacesMessage.SEVERITY_ERROR.toString(),
                createExpectedMessageString(
                    Messages.getMessageAsString(ApplicationMessageCode.NO_DATA_FOUND),
                    null));

        String actual = page.search();

        assertNull(actual);

        assertEquals(1, page.getPageNo());
        assertEquals(expProjectId, page.getProjectId());
        assertEquals(expName, page.getName());

        assertEquals(1, page.getCondition().getPageNo());
        assertEquals(expProjectId, page.getCondition().getProjectId());
        assertEquals(expName, page.getCondition().getNameE());

        assertEquals(0, page.getDataCount()); // 初期化
        assertEquals(0, page.getProjectList().size()); // 初期化
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.view.admin.ProjectIndexPage#downloadExcel()} のためのテスト・メソッド.
     */
    @Test
    public void testDownloadExcel() {
        String fileName = "File_Name";
        MockAbstractPage.RET_FILE_NAME = fileName;

        SearchProjectCondition condition = new SearchProjectCondition();
        condition.setEmpNo("0-");
        condition.setNameE("J-");
        page.setCondition(condition);

        List<Project> expIndexList = createProjectList();
        SearchProjectResult result = new SearchProjectResult();
        result.setCount(expIndexList.size());
        result.setProjectList(expIndexList);
        MockProjectService.RET_SEARCH = result;

        String expected = "EXPECTED_EXCEL_DATA";
        MockProjectService.RET_GENERATE_EXCEL = expected.getBytes();

        String actual = page.downloadExcel();

        assertNull(actual);

        assertEquals(fileName + ".xls", MockViewHelper.SET_FILENAME);
        assertEquals(expected, new String(MockViewHelper.SET_DATA));
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.view.admin.ProjectIndexPage#downloadExcel()} のためのテスト・メソッド.
     * Service内でエラー発生.
     */
    @Test
    public void testDownloadExcelError() {
        String fileName = "File_Name";
        MockAbstractPage.RET_FILE_NAME = fileName;

        SearchProjectCondition condition = new SearchProjectCondition();
        condition.setEmpNo("0-");
        condition.setNameE("J-");
        page.setCondition(condition);

        List<Project> expIndexList = createProjectList();
        SearchProjectResult result = new SearchProjectResult();
        result.setCount(expIndexList.size());
        result.setProjectList(expIndexList);
        MockProjectService.RET_SEARCH = result;

        String expected = "EXPECTED_EXCEL_DATA";
        MockProjectService.RET_GENERATE_EXCEL = expected.getBytes();

        MockViewHelper.EX_GENERATE = false;

        // 期待されるメッセージをセット
        FacesContextMock.EXPECTED_MESSAGE =
            new FacesMessage(
                FacesMessage.SEVERITY_ERROR,
                FacesMessage.SEVERITY_ERROR.toString(),
                createExpectedMessageString(
                    Messages.getMessageAsString(ApplicationMessageCode.E_DOWNLOAD_FAILED),
                    null));

        String actual = page.downloadExcel();

        assertNull(actual);

        assertEquals(fileName + ".xls", MockViewHelper.SET_FILENAME);
        assertEquals(expected, new String(MockViewHelper.SET_DATA));
    }
    /**
     * {@link jp.co.opentone.bsol.linkbinder.view.admin.ProjectIndexPage#movePrevious()} のためのテスト・メソッド.
     */
    @Test
    public void testMovePrevious() {
        int expCount = 15;
        List<Project> expList = createProjectList();

        SearchProjectResult result = new SearchProjectResult();
        result.setCount(expCount);
        result.setProjectList(expList);
        MockProjectService.RET_SEARCH = result;

        page.setCondition(new SearchProjectCondition());
        String expProjectId = "PJ";
        page.setProjectId(expProjectId);
        String expName = "J-";
        page.setName(expName);

        // 3ページ目から1つ戻る
        page.setPageNo(3);
        String actual = page.movePrevious();

        assertNull(actual);

        assertEquals(2, page.getPageNo());
        assertEquals(2, page.getCondition().getPageNo());

        assertEquals(expCount, page.getDataCount());
        assertEquals(expList, page.getProjectList());

        // 再設定
        int newCount = 16;
        List<Project> newList = createNewProjectList();
        SearchProjectResult newResult = new SearchProjectResult();
        newResult.setCount(newCount);
        newResult.setProjectList(newList);
        MockProjectService.RET_SEARCH = newResult;

        // さらに1つ戻る
        String actual2 =  page.movePrevious();

        assertNull(actual2);

        assertEquals(1, page.getPageNo());
        assertEquals(1, page.getCondition().getPageNo());
        assertEquals(newResult.getCount(), page.getDataCount());
        assertEquals(newList, page.getProjectList());
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.view.admin.ProjectIndexPage#moveNext()} のためのテスト・メソッド.
     */
    @Test
    public void testMoveNext() {
        int expCount = 15;
        List<Project> expList = createProjectList();

        SearchProjectResult result = new SearchProjectResult();
        result.setCount(expCount);
        result.setProjectList(expList);
        MockProjectService.RET_SEARCH = result;

        page.setCondition(new SearchProjectCondition());
        String expProjectId = "PJ";
        page.setProjectId(expProjectId);
        String expName = "J-";
        page.setName(expName);

        // 1ページ目から1つ進む
        page.setPageNo(1);
        String actual = page.moveNext();

        assertNull(actual);

        assertEquals(2, page.getPageNo());
        assertEquals(2, page.getCondition().getPageNo());

        assertEquals(expCount, page.getDataCount());
        assertEquals(expList, page.getProjectList());

        // 再設定
        int newCount = 16;
        List<Project> newList = createNewProjectList();
        SearchProjectResult newResult = new SearchProjectResult();
        newResult.setCount(newCount);
        newResult.setProjectList(newList);
        MockProjectService.RET_SEARCH = newResult;

        // さらに1つ進む
        String actual2 =  page.moveNext();

        assertNull(actual2);

        assertEquals(3, page.getPageNo());
        assertEquals(3, page.getCondition().getPageNo());
        assertEquals(newResult.getCount(), page.getDataCount());
        assertEquals(newList, page.getProjectList());
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.view.admin.ProjectIndexPage#changePage()} のためのテスト・メソッド.
     */
    @Test
    public void testChangePage() {
        int expCount = 15;
        List<Project> expList = createProjectList();

        SearchProjectResult result = new SearchProjectResult();
        result.setCount(expCount);
        result.setProjectList(expList);
        MockProjectService.RET_SEARCH = result;

        page.setCondition(new SearchProjectCondition());
        String expProjectId = "PJ";
        page.setProjectId(expProjectId);
        String expName = "J-";
        page.setName(expName);

        // 4ページ目へ
        page.setPageNo(4);
        String actual = page.changePage();

        assertNull(actual);

        assertEquals(4, page.getPageNo());
        assertEquals(4, page.getCondition().getPageNo());

        assertEquals(expCount, page.getDataCount());
        assertEquals(expList, page.getProjectList());

        // 再設定
        int newCount = 16;
        List<Project> newList = createNewProjectList();
        SearchProjectResult newResult = new SearchProjectResult();
        newResult.setCount(newCount);
        newResult.setProjectList(newList);
        MockProjectService.RET_SEARCH = newResult;

        // 2ページ目へ
        page.setPageNo(2);
        String actual2 =  page.changePage();

        assertNull(actual2);

        assertEquals(2, page.getPageNo());
        assertEquals(2, page.getCondition().getPageNo());
        assertEquals(newResult.getCount(), page.getDataCount());
        assertEquals(newList, page.getProjectList());
    }

    private List<Project> createProjectList() {
        List<Project> list = new ArrayList<Project>();

        Project project = new Project();
        project.setProjectId("PJ1");
        project.setNameE("Test Project 1");
        list.add(project);

        project = new Project();
        project.setProjectId("PJ2");
        project.setNameE("Test Project 2");
        list.add(project);

        project = new Project();
        project.setProjectId("PJ3");
        project.setNameE("Test Project 3");
        list.add(project);

        project = new Project();
        project.setProjectId("PJ4");
        project.setNameE("Test Project 4");
        list.add(project);

        return list;
    }

    private List<Project> createNewProjectList() {
        List<Project> list = new ArrayList<Project>();

        Project project = new Project();
        project.setProjectId("PJ5");
        project.setNameE("Test Project 5");
        list.add(project);

        project = new Project();
        project.setProjectId("PJ6");
        project.setNameE("Test Project 6");
        list.add(project);

        project = new Project();
        project.setProjectId("PJ7");
        project.setNameE("Test Project 7");
        list.add(project);

        project = new Project();
        project.setProjectId("PJ8");
        project.setNameE("Test Project 8");
        list.add(project);

        return list;
    }

    public static class MockSystemConfig extends MockUp<SystemConfig> {
        static Map<String, String> VALUES = new HashMap<String, String>();

        @Mock
        public static String getValue(String key) {
            return VALUES.get(key);
        }
    }

    public static class MockAbstractPage extends MockUp<AbstractPage> {
        static boolean IS_SYSTEM_ADMIN;
        static String RET_FILE_NAME;

        @Mock
        public boolean isSystemAdmin() {
            return IS_SYSTEM_ADMIN;
        }

        @Mock
        public String createFileName() {
            return RET_FILE_NAME;
        }
    }

    public static class MockProjectService extends MockUp<ProjectServiceImpl> {
        static SearchProjectResult RET_SEARCH;
        static boolean EX_SEARCH;
        static byte[] RET_GENERATE_EXCEL;

        @Mock
        public SearchProjectResult searchPagingList(SearchProjectCondition condition)
                throws ServiceAbortException {
            if (EX_SEARCH) {
                throw new ServiceAbortException(ApplicationMessageCode.NO_DATA_FOUND);
            }
            return RET_SEARCH;
        }

        @Mock
        public byte[] generateExcel(List<Project> projects) throws ServiceAbortException {
            return RET_GENERATE_EXCEL;
        }
    }

    public static class MockViewHelper extends MockUp<ViewHelper> {
        static byte[] SET_DATA;
        static String SET_FILENAME;
        static boolean EX_GENERATE;

        @Mock
        public void download(String fileName, byte[] content) throws IOException {
            if (EX_GENERATE) {
                throw new IOException();
            }
            SET_DATA = content;
            SET_FILENAME = fileName;
        }
    }
}
