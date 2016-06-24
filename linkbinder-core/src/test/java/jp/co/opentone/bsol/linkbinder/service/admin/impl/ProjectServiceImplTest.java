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
package jp.co.opentone.bsol.linkbinder.service.admin.impl;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import jp.co.opentone.bsol.framework.core.dao.RecordNotFoundException;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.extension.jsf.FacesContextMock;
import jp.co.opentone.bsol.framework.test.AbstractTestCase;
import jp.co.opentone.bsol.linkbinder.dao.impl.ProjectDaoImpl;
import jp.co.opentone.bsol.linkbinder.dto.Project;
import jp.co.opentone.bsol.linkbinder.dto.SearchProjectResult;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchProjectCondition;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.service.MockAbstractService;
import jp.co.opentone.bsol.linkbinder.service.admin.ProjectService;
import mockit.Mock;
import mockit.MockUp;

/**
 * {@link ProjectServiceImpl}のテストケース
 * @author opentone
 */
public class ProjectServiceImplTest extends AbstractTestCase {

    /**
     * テスト対象.
     */
    @Resource
    private ProjectService service;

    /**
     * EXCELシート名がプロパティに設定されていない場合のデフォルト名.
     */
    private static final String SHEET_DEFAULT = "ProjectIndex";

    /**
     * ログインユーザー.
     */
    private static final User LOGIN_USER = new User();
    static {
        LOGIN_USER.setEmpNo("00001");
        LOGIN_USER.setNameE("Test User");
    }

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        new MockAbstractService();
        FacesContextMock.initialize();
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        FacesContextMock.tearDown();
        new MockAbstractService().tearDown();
    }

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        MockAbstractService.CURRENT_USER = LOGIN_USER;
        MockAbstractService.IS_SYSTEM_ADMIN = false;
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.service.admin.impl.ProjectServiceImpl
     *     #searchPagingList(jp.co.opentone.bsol.linkbinder.dto.condition.SearchProjectCondition)} のためのテスト・メソッド.
     */
    @Test
    public void testSearchPagingList() throws Exception {
        List<Project> expList = createProjectList();

        // Mock準備
        MockAbstractService.IS_SYSTEM_ADMIN = true;
        new MockUp<ProjectDaoImpl>() {
            @Mock int count(SearchProjectCondition condition) {
                return expList.size();
            }
            @Mock List<Project> find(SearchProjectCondition condition) {
                return expList;
            }
        };

        SearchProjectResult actual = service.searchPagingList(new SearchProjectCondition());

        assertEquals(expList.size(), actual.getCount());
        assertEquals(expList, actual.getProjectList());
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.service.admin.impl.ProjectServiceImpl
     *     #searchPagingList(jp.co.opentone.bsol.linkbinder.dto.condition.SearchProjectCondition)} のためのテスト・メソッド.
     * 引数Null.
     */
    @Test(expected=IllegalArgumentException.class)
    public void testSearchPagingListNull() throws Exception {
        service.searchPagingList(null);
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.service.admin.impl.ProjectServiceImpl
     *     #searchPagingList(jp.co.opentone.bsol.linkbinder.dto.condition.SearchProjectCondition)} のためのテスト・メソッド.
     * SystemAdminではない.
     */
    @Test
    public void testSearchPagingListNotSystemAdmin() throws Exception {
        try {
            service.searchPagingList(new SearchProjectCondition());

            fail("例外が発生していない");
        } catch (ServiceAbortException e ) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT,
                         e.getMessageCode());
        }
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.service.admin.impl.ProjectServiceImpl
     *     #searchPagingList(jp.co.opentone.bsol.linkbinder.dto.condition.SearchProjectCondition)} のためのテスト・メソッド.
     * 条件に該当するデータなし.
     */
    @Test
    public void testSearchPagingListNoData() throws Exception {
        // Mock準備
        MockAbstractService.IS_SYSTEM_ADMIN = true;
        new MockUp<ProjectDaoImpl>() {
            @Mock int count(SearchProjectCondition condition) {
                return 0;
            }
        };

        try {
            service.searchPagingList(new SearchProjectCondition());

            fail("例外が発生していない");
        } catch (ServiceAbortException e ) {
            assertEquals(ApplicationMessageCode.NO_DATA_FOUND,
                         e.getMessageCode());
        }
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.service.admin.impl.ProjectServiceImpl
     *     #searchPagingList(jp.co.opentone.bsol.linkbinder.dto.condition.SearchProjectCondition)} のためのテスト・メソッド.
     * ページに表示するデータなし.
     */
    @Test
    public void testSearchPagingListNoPage() throws Exception {
        // Mock準備
        MockAbstractService.IS_SYSTEM_ADMIN = true;
        new MockUp<ProjectDaoImpl>() {
            @Mock int count(SearchProjectCondition condition) {
                return 15;
            }
            @Mock List<Project> find(SearchProjectCondition condition) {
                return new ArrayList<>();
            }
        };

        try {
            service.searchPagingList(new SearchProjectCondition());

            fail("例外が発生していない");
        } catch (ServiceAbortException e ) {
            assertEquals(ApplicationMessageCode.NO_PAGE_FOUND,
                         e.getMessageCode());
        }
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.service.admin.impl.ProjectServiceImpl
     *     #generateExcel(java.util.List)} のためのテスト・メソッド.
     * シート名設定あり.
     */
    @Test
    public void testGenerateExcelSheetKey() throws Exception {
        String expSheetName = "ProjectIndex";

        // 結果レコード
        List<Object> expected = new ArrayList<Object>();
        /* ヘッダ */
        List<Object> line = new ArrayList<Object>();
        line.add("Project ID");
        line.add("Project Name");
        expected.add(line);

        /* 1行目 */
        line = new ArrayList<Object>();
        line.add("PJ1");
        line.add("Test Project 1");
        expected.add(line);

        /* 2行目 */
        line = new ArrayList<Object>();
        line.add("PJ2");
        line.add("Test Project 2");
        expected.add(line);

        /* 3行目 */
        line = new ArrayList<Object>();
        line.add("PJ3");
        line.add("Test Project 3");
        expected.add(line);

        /* 4行目 */
        line = new ArrayList<Object>();
        line.add("PJ4");
        line.add("Test Project 4");
        expected.add(line);

        byte[] actual = service.generateExcel(createProjectList());

        // 作成したExcelを確認
        assertExcel(1, expSheetName, 4, expected, actual);
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.service.admin.impl.UserServiceImpl
     *         #generateExcel((java.util.List)}のためのテスト・メソッド.
     * シート名設定なし.
     */
    @Test
    public void testGenerateExcelSheetDefault() throws Exception {
        // 結果レコード
        List<Object> expected = new ArrayList<Object>();
        /* ヘッダ */
        List<Object> line = new ArrayList<Object>();
        line.add("Project ID");
        line.add("Project Name");
        expected.add(line);

        /* 1行目 */
        line = new ArrayList<Object>();
        line.add("PJ1");
        line.add("Test Project 1");
        expected.add(line);

        /* 2行目 */
        line = new ArrayList<Object>();
        line.add("PJ2");
        line.add("Test Project 2");
        expected.add(line);

        /* 3行目 */
        line = new ArrayList<Object>();
        line.add("PJ3");
        line.add("Test Project 3");
        expected.add(line);

        /* 4行目 */
        line = new ArrayList<Object>();
        line.add("PJ4");
        line.add("Test Project 4");
        expected.add(line);

        // マスタ管理の為、リストを作成する際にnullを渡す。
        byte[] actual = service.generateExcel(createProjectList());

        // 作成したExcelを確認
        assertExcel(1, SHEET_DEFAULT, 4, expected, actual);
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.service.admin.impl.UserServiceImpl
     *         #generateExcel((java.util.List)}のためのテスト・メソッド.
     * 引数がNull.
     */
    @Test(expected=IllegalArgumentException.class)
    public void testGenerateExcelNull() throws Exception {
        service.generateExcel(null);
        fail("例外が発生していない");
    }

    /**
     * {@link @link jp.co.opentone.bsol.linkbinder.service.admin.impl.ProjectServiceImpl
     *         #find(String)}のためのテスト・メソッド.
     * 正常終了
     */
    @Test
    public void testFindSuccess() throws Exception{
        // Mock準備
        new MockUp<ProjectDaoImpl>() {
            @Mock Project findById(String projectId) throws RecordNotFoundException {
                return createProject();
            }
        };

        Project project = service.find("PJ1");
        assertEquals("PJ1",project.getProjectId());
        assertEquals("Test Project 1",project.getNameE());
    }

    /**
     * {@link @link jp.co.opentone.bsol.linkbinder.service.admin.impl.ProjectServiceImpl
     *         #find(String)}のためのテスト・メソッド.
     * 該当なし.
     */
    @Test
    public void testFindNoRecordFoundNotFound() throws Exception{
        // Mock準備
        new MockUp<ProjectDaoImpl>() {
            @Mock Project findById(String projectId) throws RecordNotFoundException {
                throw new RecordNotFoundException();
            }
        };

        assertNull(service.find("NOT FOUND"));
    }

    /**
     * {@link @link jp.co.opentone.bsol.linkbinder.service.admin.impl.ProjectServiceImpl
     *         #find(String)}のためのテスト・メソッド.
     * 引数がNull.
     */
    @Test
    public void testFindNoRecordFoundNull() throws Exception{
        assertNull(service.find(null));
    }


    private Project createProject(){
        Project project = new Project();
        project.setProjectId("PJ1");
        project.setNameE("Test Project 1");
        return  project;
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
}
