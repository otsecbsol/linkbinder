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

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import jp.co.opentone.bsol.framework.core.dao.KeyDuplicateException;
import jp.co.opentone.bsol.framework.core.dao.RecordNotFoundException;
import jp.co.opentone.bsol.framework.core.dao.StaleRecordException;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.test.AbstractTestCase;
import jp.co.opentone.bsol.linkbinder.dao.impl.CorresponTypeDaoImpl;
import jp.co.opentone.bsol.linkbinder.dao.impl.ProjectCorresponTypeDaoImpl;
import jp.co.opentone.bsol.linkbinder.dao.impl.WorkflowPatternDaoImpl;
import jp.co.opentone.bsol.linkbinder.dto.CorresponType;
import jp.co.opentone.bsol.linkbinder.dto.Project;
import jp.co.opentone.bsol.linkbinder.dto.ProjectCorresponType;
import jp.co.opentone.bsol.linkbinder.dto.SearchCorresponTypeResult;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.WorkflowPattern;
import jp.co.opentone.bsol.linkbinder.dto.code.AllowApproverToBrowse;
import jp.co.opentone.bsol.linkbinder.dto.code.ForceToUseWorkflow;
import jp.co.opentone.bsol.linkbinder.dto.code.UseWhole;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponTypeCondition;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.service.MockAbstractService;
import jp.co.opentone.bsol.linkbinder.service.admin.CorresponTypeService;
import jp.co.opentone.bsol.linkbinder.service.correspon.impl.CorresponServiceImpl;
import mockit.Mock;
import mockit.MockUp;

/**
 * {@link CorresponServiceImpl}のテストケース.
 * @author opentone
 */
public class CorresponTypeServiceImplTest extends AbstractTestCase {

    /**
     * テスト対象.
     */
    @Resource
    private CorresponTypeService service;

    @BeforeClass
    public static void testSetUp() {
        new MockAbstractService();
    }

    /**
     * 後始末.
     */
    @After
    public void tearDown() {
        tearDownMockAbstractService();
    }

    private void tearDownMockAbstractService() {
        MockAbstractService.CURRENT_PROJECT_ID = null;
        MockAbstractService.CURRENT_PROJECT = null;
        MockAbstractService.CURRENT_USER = null;
        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        MockAbstractService.IS_GROUP_ADMIN = null;
        MockAbstractService.IS_ANY_GROUP_ADMIN = false;
        MockAbstractService.ACCESSIBLE_PROJECTS = null;
        MockAbstractService.VALIDATE_PROJECT_ID_EXCEPTION = null;
    }

    @AfterClass
    public static void testTeardown() {
        new MockAbstractService().tearDown();
    }

    /**
     * 指定された条件に該当するコレポン文書種別を検索し、指定ページのレコードだけを返すテスト.
     * SystemAdminで検索.
     */
    @Test
    public void testSearchPagingListAdmin() throws Exception {
        List<CorresponType> expected = createCorresponTypeList();

        // Mock準備
        MockAbstractService.IS_SYSTEM_ADMIN = true;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        new MockUp<CorresponTypeDaoImpl>() {
            @Mock List<CorresponType> find(SearchCorresponTypeCondition condition) {
                return expected;
            }
            @Mock int count(SearchCorresponTypeCondition condition) {
                return expected.size();
            }
        };

        // テスト実行
        SearchCorresponTypeResult actual =
                service.searchPagingList(new SearchCorresponTypeCondition());
        assertEquals(expected.size(), actual.getCount());
        assertEquals(expected, actual.getCorresponTypeList());
    }

    /**
     * 指定された条件に該当するコレポン文書種別を検索し、指定ページのレコードだけを返すテスト.
     * ProjectAdminで検索.
     */
    @Test
    public void testSearchPagingListProject() throws Exception {
        List<CorresponType> expList = createCorresponTypeList();
        List<CorresponType> expSelectList = createCorresponTypeSelectList();

        // Mock準備
        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN = true;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        new MockUp<CorresponTypeDaoImpl>() {
            @Mock List<CorresponType> find(SearchCorresponTypeCondition condition) {
                return expList;
            }
            @Mock int count(SearchCorresponTypeCondition condition) {
                return expList.size();
            }
            @Mock List<CorresponType> findNotExist(String id) {
                return expSelectList;
            }
        };

        SearchCorresponTypeCondition condition = new SearchCorresponTypeCondition();
        condition.setProjectId("PJ1");
        // テスト実行
        SearchCorresponTypeResult actual = service.searchPagingList(condition);

        assertEquals(expList.size(), actual.getCount());
        assertEquals(expList, actual.getCorresponTypeList());
    }

    /**
     * 指定された条件に該当するコレポン文書種別を検索し、指定ページのレコードだけを返すテスト.
     * 権限チェック：SytemAdminではない.
     */
    @Test
    public void testSearchPagingListErrorAdmin() throws Exception {
        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN = false;

        try {
            // テスト実行
            service.searchPagingList(new SearchCorresponTypeCondition());
            fail("例外が発生していない");
        } catch (ServiceAbortException e) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT, e
                .getMessageCode());
        }
    }

    /**
     * 指定された条件に該当するコレポン文書種別を検索し、指定ページのレコードだけを返すテスト.
     * 権限チェック：ProjectAdminではない.
     */
    @Test
    public void testSearchPagingListErrorProject() throws Exception {
        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        try {
            SearchCorresponTypeCondition condition = new SearchCorresponTypeCondition();
            condition.setProjectId("PJ1");
            // テスト実行
            service.searchPagingList(condition);
            fail("例外が発生していない");
        } catch (ServiceAbortException e) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT, e
                .getMessageCode());
        }
    }

    /**
     * 指定された条件に該当するコレポン文書種別を検索し、指定ページのレコードだけを返すテスト.
     * 検索条件に該当するレコードがない.
     */
    @Test
    public void testSearchPagingListNoRecord() throws Exception {
        // Mock準備
        MockAbstractService.IS_SYSTEM_ADMIN = true;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        new MockUp<CorresponTypeDaoImpl>() {
            @Mock List<CorresponType> find(SearchCorresponTypeCondition condition) {
                return new ArrayList<>();
            }
            @Mock int count(SearchCorresponTypeCondition condition) {
                return 0;
            }
        };

        try {
            // テスト実行
            service.searchPagingList(new SearchCorresponTypeCondition());
            fail("例外が発生していない");
        } catch (ServiceAbortException e) {
            assertEquals(ApplicationMessageCode.NO_DATA_FOUND, e.getMessageCode());
        }
    }

    /**
     * 指定された条件に該当するコレポン文書種別を検索し、指定ページのレコードだけを返すテスト.
     * 指定されたページに該当するレコードがない.
     */
    @Test
    public void testSearchPagingListNoPage() throws Exception {
        // Mock準備
        MockAbstractService.IS_SYSTEM_ADMIN = true;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        new MockUp<CorresponTypeDaoImpl>() {
            @Mock List<CorresponType> find(SearchCorresponTypeCondition condition) {
                return new ArrayList<>();
            }
            @Mock int count(SearchCorresponTypeCondition condition) {
                return 5;
            }
        };

        try {
            // テスト実行
            service.searchPagingList(new SearchCorresponTypeCondition());
            fail("例外が発生していない");
        } catch (ServiceAbortException e) {
            assertEquals(ApplicationMessageCode.NO_PAGE_FOUND, e.getMessageCode());
        }
    }

    /**
     * 指定された条件に該当するコレポン文書種別を検索し、指定ページのレコードだけを返すテスト.
     * 引数がNull.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSearchPagingListNull() throws Exception {
        // テスト実行
        service.searchPagingList(null);
    }

    /**
     * 指定されたコレポン文書種別一覧をExcel形式に変換して返すテスト.
     */
    @Test
    public void testGenerateExcel() throws Exception {
        // 結果レコード
        List<Object> expected = new ArrayList<Object>();
        /* ヘッダ */
        List<Object> line = new ArrayList<Object>();
        line.add("ID");
        line.add("Type");
        line.add("Name");
        line.add("WorkflowPattern");
        line.add("Allow\nApprover");
        line.add("Use\nworkflow");
        expected.add(line);

        /* 1行目 */
        line = new ArrayList<Object>();
        line.add(new Integer(1));
        line.add("Type-1");
        line.add("Name-1");
        line.add("Pattern 1");
        line.add("");
        line.add("");
        expected.add(line);

        /* 2行目 */
        line = new ArrayList<Object>();
        line.add(new Integer(2));
        line.add("Type-2");
        line.add("Name-2");
        line.add("Pattern 2");
        line.add("○");
        line.add("○");
        expected.add(line);

        /* 3行目 */
        line = new ArrayList<Object>();
        line.add(new Integer(3));
        line.add("Type-3");
        line.add("Name-3");
        line.add("Pattern 3");
        line.add("");
        line.add("○");
        expected.add(line);

        /* 4行目 */
        line = new ArrayList<Object>();
        line.add(new Integer(4));
        line.add("Type-4");
        line.add("Name-4");
        line.add("Pattern 1");
        line.add("○");
        line.add("");
        expected.add(line);

        /* 5行目 */
        line = new ArrayList<Object>();
        line.add(new Integer(5));
        line.add("Type-5");
        line.add("Name-5");
        line.add("Pattern 2");
        line.add("○");
        line.add("○");
        expected.add(line);

        // テスト実行
        byte[] actual = service.generateExcel(createCorresponTypeList());

        // 作成したExcelを確認
        assertExcel(1, "CorresponTypeIndex", 5, expected, actual);
    }

    /**
     * 指定されたコレポン文書種別一覧をExcel形式に変換して返すテスト.
     * 特定のプロジェクトでの操作を想定.
     */
    @Test
    public void testGenerateExcelPerProject() throws Exception {
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";

        // 結果レコード
        List<Object> expected = new ArrayList<Object>();
        /* ヘッダ */
        List<Object> line = new ArrayList<Object>();
        line.add("ID");
        line.add("Type");
        line.add("Name");
        line.add("WorkflowPattern");
        line.add("Allow\nApprover");
        line.add("Use\nworkflow");
        expected.add(line);

        /* 1行目 */
        line = new ArrayList<Object>();
        line.add(new Integer(11));
        line.add("Type-1");
        line.add("Name-1");
        line.add("Pattern 1");
        line.add("");
        line.add("");
        expected.add(line);

        /* 2行目 */
        line = new ArrayList<Object>();
        line.add(new Integer(12));
        line.add("Type-2");
        line.add("Name-2");
        line.add("Pattern 2");
        line.add("○");
        line.add("○");
        expected.add(line);

        /* 3行目 */
        line = new ArrayList<Object>();
        line.add(new Integer(13));
        line.add("Type-3");
        line.add("Name-3");
        line.add("Pattern 3");
        line.add("");
        line.add("○");
        expected.add(line);

        /* 4行目 */
        line = new ArrayList<Object>();
        line.add(new Integer(14));
        line.add("Type-4");
        line.add("Name-4");
        line.add("Pattern 1");
        line.add("○");
        line.add("");
        expected.add(line);

        /* 5行目 */
        line = new ArrayList<Object>();
        line.add(new Integer(15));
        line.add("Type-5");
        line.add("Name-5");
        line.add("Pattern 2");
        line.add("○");
        line.add("○");
        expected.add(line);

        // テスト実行
        byte[] actual = service.generateExcel(createCorresponTypeList());

        // 作成したExcelを確認
        assertExcel(1, "CorresponTypeIndex", 5, expected, actual);
    }

    /**
     * 指定したコレポン文書種別をプロジェクトで利用可能にする処理を検証.
     * @throws Exception
     */
    @Test
    public void testAssignTo() throws Exception {
        CorresponType corresponType = createCorresponType();

        User user = new User();
        user.setEmpNo("ZZA01");
        user.setNameE("Taro Yamada");

        // Mock準備
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        MockAbstractService.CURRENT_USER = user;
        MockAbstractService.IS_PROJECT_ADMIN = true;
        new MockUp<CorresponTypeDaoImpl>() {
            @Mock CorresponType findById(Long id) throws RecordNotFoundException {
                return createCorresponType();
            }
            @Mock int countCheck(SearchCorresponTypeCondition condition) {
                return 0;
            }
        };
        new MockUp<ProjectCorresponTypeDaoImpl>() {
            @Mock int countByCorresponTypeIdProjectId(Long corresponTypeId, String projectId) {
                return 0;
            }
            @Mock Long create(ProjectCorresponType entity) throws KeyDuplicateException {
                assertThat(entity.getProjectId(), is("PJ1"));

                return 1L;
            }
        };

        // テスト実行
        Long id = service.assignTo(corresponType);
        assertNotNull(id);
    }

    /**
     * 指定したコレポン文書種別をプロジェクトで利用可能にする処理を検証. 権限がない
     * @throws Exception
     */
    @Test
    public void testAssignToNoPermission() throws Exception {
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        // 権限がない
        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN = false;

        try {
            // テスト実行
            service.assignTo(new CorresponType());
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT,
                actual.getMessageCode());
        }
    }

    /**
     * 指定したコレポン文書種別をプロジェクトで利用可能にする処理を検証.
     * 指定したコレポン文書種別が存在しない
     * @throws Exception
     */
    @Test
    public void testAssignToNotExistCorresponType() throws Exception {
        // Mock準備
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        MockAbstractService.IS_SYSTEM_ADMIN = true;
        new MockUp<CorresponTypeDaoImpl>() {
            @Mock CorresponType findById(Long id) throws RecordNotFoundException {
                // 指定された文書種別が存在しない
                throw new RecordNotFoundException();
            }
        };

        try {
            // テスト実行
            service.assignTo(new CorresponType());
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.NO_DATA_FOUND, actual.getMessageCode());
        }
    }

    /**
     * 指定したコレポン文書種別をプロジェクトで利用可能にする処理を検証.
     * 指定のプロジェクトに同じコレポン文書種別が既に紐付いている場合
     * @throws Exception
     */
    @Test
    public void testAssignToExistProjectCorresponType() throws Exception {
        // Mock準備
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        MockAbstractService.IS_SYSTEM_ADMIN = true;
        new MockUp<CorresponTypeDaoImpl>() {
            @Mock CorresponType findById(Long id) throws RecordNotFoundException {
                return createCorresponType();
            }
            @Mock int countCheck(SearchCorresponTypeCondition condition) {
                // 指定のプロジェクトに同じコレポン文書種別が既に紐付いている
                return 1;
            }
        };

        try {
            // テスト実行
            service.assignTo(new CorresponType());
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CORRESPON_TYPE_ALREADY_ASSIGNED_TO_PROJECT,
                actual.getMessageCode());
        }
    }

    /**
     * 指定したコレポン文書種別をプロジェクトで利用可能にする処理を検証.
     * 引数がnull
     * @throws Exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAssignToArgumentNull() throws Exception {
        // テスト実行
        service.assignTo(null);
    }

    /**
     * 指定したコレポン文書種別を取得する処理を検証.
     * AdminHome
     * @throws Exception
     */
    @Test
    public void testFindAdmin() throws Exception {
        CorresponType corresponType = createCorresponType();
        // Mock準備
        new MockUp<CorresponTypeDaoImpl>() {
            @Mock CorresponType findById(Long id) throws RecordNotFoundException {
                return createCorresponType();
            }
        };

        // テスト実行
        CorresponType actual = service.find(1L);

        assertEquals(corresponType.toString(), actual.toString());
    }

    /**
     * 指定したコレポン文書種別を取得する処理を検証.
     * データがない
     * @throws Exception
     */
    @Test
    public void testFindAdminNoData() throws Exception {
        // Mock準備
        new MockUp<CorresponTypeDaoImpl>() {
            @Mock CorresponType findById(Long id) throws RecordNotFoundException {
                throw new RecordNotFoundException();
            }
        };

        try {
            // テスト実行
            service.find(1L);
            fail("例外が発生しない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.NO_DATA_FOUND, actual.getMessageCode());
        }
    }

    /**
     * 指定したコレポン文書種別を取得する処理を検証.
     * ProjectAdminHome
     * @throws Exception
     */
    @Test
    public void testFindProject() throws Exception {
        CorresponType corresponType = createCorresponType();

        List<Project> projectList = new ArrayList<Project>();
        Project project = new Project();
        project.setProjectId("PJ1");
        projectList.add(project);

        // Mock準備
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        MockAbstractService.ACCESSIBLE_PROJECTS = projectList;
        new MockUp<CorresponTypeDaoImpl>() {
            @Mock CorresponType findByIdProjectId(Long id, String projectId)
                    throws RecordNotFoundException {
                return createCorresponType();
            }
        };

        // テスト実行
        CorresponType actual = service.find(1L);

        assertEquals(corresponType.toString(), actual.toString());
    }

    /**
     * 指定したコレポン文書種別を取得する処理を検証.
     * データがない
     * @throws Exception
     */
    @Test
    public void testFindProjectNoData() throws Exception {
        // データがない
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        List<Project> projectList = new ArrayList<Project>();
        Project project = new Project();
        project.setProjectId("PJ1");
        projectList.add(project);
        MockAbstractService.ACCESSIBLE_PROJECTS = projectList;
        new MockUp<CorresponTypeDaoImpl>() {
            @Mock CorresponType findByIdProjectId(Long id, String projectId)
                    throws RecordNotFoundException {
                throw new RecordNotFoundException();
            }
        };

        try {
            // テスト実行
            service.find(1L);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.NO_DATA_FOUND, actual.getMessageCode());
        }
    }

    /**
     * 指定したコレポン文書種別を取得する処理を検証.
     * ProjectAdminHome、コレポン文書種別のプロジェクトが現在選択中のプロジェクト以外
     * @throws Exception
     */
    @Test
    public void testFindProjectDiffProject() throws Exception {
        CorresponType findByProject = createCorresponType();
        // プロジェクトが違う
        findByProject.setProjectId("PJ2");

        List<Project> projectList = new ArrayList<Project>();
        Project project = new Project();
        project.setProjectId("PJ1");
        projectList.add(project);

        // Mock準備
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        MockAbstractService.ACCESSIBLE_PROJECTS = projectList;
        new MockUp<CorresponTypeDaoImpl>() {
            @Mock CorresponType findByIdProjectId(Long id, String projectId)
                    throws RecordNotFoundException {
                // プロジェクトが違う
                return findByProject;
            }
        };

        try {
            // テスト実行
            service.find(1L);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_PROJECT_DIFF, actual
                .getMessageCode());
        }
    }

    /**
     * 指定したコレポン文書種別を取得する処理を検証. 引数がnull
     * @throws Exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testFindArgumentNull() throws Exception {
        // テスト実行
        service.find(null);
    }

    /**
     * 指定したコレポン文書種別を保存する処理を検証する.
     * 登録、AdminHome
     * @throws Exception
     */
    @Test
    public void testSaveInsertAdmin() throws Exception {
        CorresponType corresponType = createCorresponType();
        // 登録
        corresponType.setId(null);

        User loginUser = new User();
        loginUser.setEmpNo("ZZA01");
        loginUser.setNameE("Taro Yamada");

        // Mock準備
        MockAbstractService.IS_SYSTEM_ADMIN = true;
        MockAbstractService.CURRENT_USER = loginUser;
        new MockUp<CorresponTypeDaoImpl>() {
            @Mock int countCheck(SearchCorresponTypeCondition condition) {
                return 0;
            }

            @Mock Long create(CorresponType e) {
                assertThat(e.getCorresponType(), is(corresponType.getCorresponType()));
                assertThat(e.getName(), is(corresponType.getName()));
                assertThat(e.getCreatedBy().toString(), is(loginUser.toString()));

                return 10L;
            }
        };

        // テスト実行
        Long id = service.save(corresponType);
        assertEquals(String.valueOf(10L), id.toString());
    }

    /**
     * 指定したコレポン文書種別を保存する処理を検証する.
     * 登録、AdminHome、権限がない
     * @throws Exception
     */
    @Test
    public void testSaveInsertAdminNoPermission() throws Exception {
        CorresponType corresponType = createCorresponType();
        // 登録
        corresponType.setId(null);
        User loginUser = new User();
        loginUser.setEmpNo("ZZA01");
        loginUser.setNameE("Taro Yamada");

        // 権限がない
        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.CURRENT_USER = loginUser;

        try {
            // テスト実行
            service.save(corresponType);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT,
                actual.getMessageCode());
        }
    }

    /**
     * 指定したコレポン文書種別を保存する処理を検証する.
     * 登録、AdminHome、同じ文書種別（correspon_type）が既に登録されている
     * @throws Exception
     */
    @Test
    public void testSaveInsertAdminExistCorresponType() throws Exception {
        CorresponType corresponType = createCorresponType();
        // 登録
        corresponType.setId(null);
        User loginUser = new User();
        loginUser.setEmpNo("ZZA01");
        loginUser.setNameE("Taro Yamada");

        MockAbstractService.IS_SYSTEM_ADMIN = true;
        MockAbstractService.CURRENT_USER = loginUser;
        new MockUp<CorresponTypeDaoImpl>() {
            @Mock int countCheck(SearchCorresponTypeCondition condition) {
                // 既に登録されている
                return 1;
            }
        };

        try {
            // テスト実行
            service.save(corresponType);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CORRESPON_TYPE_CODE_ALREADY_EXISTS,
                actual.getMessageCode());
            assertEquals(1, actual.getMessageVars().length);
            assertEquals("Type-1", actual.getMessageVars()[0]);
        }
    }

    /**
     * 指定したコレポン文書種別を保存する処理を検証する.
     * 登録、ProjectAdminHome
     * @throws Exception
     */
    @Test
    public void testSaveInsertProject() throws Exception {
        CorresponType corresponType = createCorresponType();
        // 登録
        corresponType.setId(null);

        User loginUser = new User();
        loginUser.setEmpNo("ZZA01");
        loginUser.setNameE("Taro Yamada");

        MockAbstractService.IS_SYSTEM_ADMIN = true;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        MockAbstractService.CURRENT_USER = loginUser;
        new MockUp<CorresponTypeDaoImpl>() {
            @Mock int countCheck(SearchCorresponTypeCondition condition) {
                // 既に登録されている
                return 0;
            }
            @Mock Long create(CorresponType e) {
                return 10L;
            }
        };
        new MockUp<ProjectCorresponTypeDaoImpl>() {
            @Mock Long create(ProjectCorresponType e) {
                assertThat(e.getCorresponTypeId(), is(10L));

                return 10L;
            }
        };

        // テスト実行
        Long id = service.save(corresponType);

        assertEquals(String.valueOf(10L), id.toString());
    }

    /**
     * 指定したコレポン文書種別を保存する処理を検証する.
     * 登録、ProjectAdminHome、権限がない
     * @throws Exception
     */
    @Test
    public void testSaveInsertProjectNoPermission() throws Exception {
        CorresponType corresponType = createCorresponType();
        // 登録
        corresponType.setId(null);
        // 権限がない
        User loginUser = new User();
        loginUser.setEmpNo("ZZA01");
        loginUser.setNameE("Taro Yamada");
        MockAbstractService.CURRENT_USER = loginUser;
        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";

        try {
            // テスト実行
            service.save(corresponType);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT,
                actual.getMessageCode());
        }
    }

    /**
     * 指定したコレポン文書種別を保存する処理を検証する.
     * 登録、ProjectAdminHome同じ文書種別（correspon_type）が既に登録されている
     * @throws Exception
     */
    @Test
    public void testSaveInsertProjectExistCorresponType() throws Exception {
        CorresponType corresponType = createCorresponType();
        // 登録
        corresponType.setId(null);

        User loginUser = new User();
        loginUser.setEmpNo("ZZA01");
        loginUser.setNameE("Taro Yamada");

        // Mock準備
        MockAbstractService.IS_SYSTEM_ADMIN = true;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        MockAbstractService.CURRENT_USER = loginUser;
        new MockUp<CorresponTypeDaoImpl>() {
            @Mock int countCheck(SearchCorresponTypeCondition condition) {
                // 既に登録されている
                return 1;
            }
        };

        try {
            // テスト実行
            service.save(corresponType);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CORRESPON_TYPE_CODE_ALREADY_EXISTS,
                actual.getMessageCode());
            assertEquals(1, actual.getMessageVars().length);
            assertEquals("Type-1", actual.getMessageVars()[0]);
        }
    }


    /**
     * 指定したコレポン文書種別を保存する処理を検証する.
     * 更新、AdminHome、権限がない
     * @throws Exception
     */
    @Test
    public void testSaveUpdateAdminNoPermission() throws Exception {
        CorresponType corresponType = createCorresponType();

        User loginUser = new User();
        loginUser.setEmpNo("ZZA01");
        loginUser.setNameE("Taro Yamada");
        // 権限がない
        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.CURRENT_USER = loginUser;

        try {
            // テスト実行
            service.save(corresponType);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT,
                actual.getMessageCode());
        }
    }

    /**
     * 指定したコレポン文書種別を保存する処理を検証する.
     * 更新、AdminHome、同じ文書種別（correspon_type）が既に登録されている際はエラー
     * @throws Exception
     */
    @Test
    public void testSaveUpdateAdminExistCorresponType() throws Exception {
        CorresponType corresponType = createCorresponType();

        User loginUser = new User();
        loginUser.setEmpNo("ZZA01");
        loginUser.setNameE("Taro Yamada");

        // Mock準備
        MockAbstractService.IS_SYSTEM_ADMIN = true;
        MockAbstractService.CURRENT_USER = loginUser;
        new MockUp<CorresponTypeDaoImpl>() {
            @Mock int countCheck(SearchCorresponTypeCondition condition) {
                // 既に登録されている
                return 1;
            }
        };

        try {
            // テスト実行
            service.save(corresponType);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CORRESPON_TYPE_CODE_ALREADY_EXISTS,
                actual.getMessageCode());
            assertEquals(1, actual.getMessageVars().length);
            assertEquals("Type-1", actual.getMessageVars()[0]);
        }

    }

    /**
     * 指定したコレポン文書種別を保存する処理を検証する.
     * 更新、AdminHome、StaleRecordExceptionが発生
     * @throws Exception
     */
    @Test
    public void testSaveUpdateAdminStaleRecordException() throws Exception {
        CorresponType corresponType = createCorresponType();

        User loginUser = new User();
        loginUser.setEmpNo("ZZA01");
        loginUser.setNameE("Taro Yamada");

        // Mock準備
        MockAbstractService.IS_SYSTEM_ADMIN = true;
        MockAbstractService.CURRENT_USER = loginUser;
        new MockUp<CorresponTypeDaoImpl>() {
            @Mock int countCheck(SearchCorresponTypeCondition condition) {
                return 0;
            }
            @Mock Long update(CorresponType e) throws StaleRecordException {
                throw new StaleRecordException();
            }
        };

        try {
            // テスト実行
            service.save(corresponType);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CORRESPON_TYPE_ALREADY_UPDATED,
                actual.getMessageCode());
        }
    }

    /**
     * 指定したコレポン文書種別を保存する処理を検証する.
     * 更新、ProjectAdminHome、権限がない
     * @throws Exception
     */
    @Test
    public void testSaveUpdateProjectNoPermission() throws Exception {
        CorresponType corresponType = createCorresponType();

        // 権限がない
        User loginUser = new User();
        loginUser.setEmpNo("ZZA01");
        loginUser.setNameE("Taro Yamada");

        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        MockAbstractService.CURRENT_USER = loginUser;

        try {
            // テスト実行
            service.save(corresponType);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT,
                actual.getMessageCode());
        }
    }

    /**
     * 指定したコレポン文書種別を保存する処理を検証する.
     * 引数がnull
     * @throws Exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSaveArgumentNull() throws Exception {
        service.save(null);
    }

    /**
     * 入力値を検証する処理を検証する.
     * 登録、AdminHome、権限がない
     * @throws Exception
     */
    @Test
    public void testValidateInsertAdminNoPermission() throws Exception {
        // 権限がない
        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN = true;

        CorresponType corresponType = createCorresponType();
        // 登録
        corresponType.setId(null);

        try {
            // テスト実行
            service.validate(corresponType);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT,
                actual.getMessageCode());
        }

    }

    /**
     * 入力値を検証する処理を検証する.
     * 引数がnull
     * @throws Exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testValidateArgumentNull() throws Exception {
        // テスト実行
        service.validate(null);
    }

    /**
     * 承認フローパターンを取得する処理を検証する.
     * @throws Exception
     */
    @Test
    public void testSearchWorkflowPattern() throws Exception {
        List<WorkflowPattern> workflowPatternList = new ArrayList<WorkflowPattern>();
        WorkflowPattern wp = new WorkflowPattern();
        wp.setId(1L);
        wp.setName("pattern1");
        wp.setWorkflowCd("001");
        workflowPatternList.add(wp);

        wp = new WorkflowPattern();
        wp.setId(2L);
        wp.setName("pattern2");
        wp.setWorkflowCd("002");
        workflowPatternList.add(wp);

        wp = new WorkflowPattern();
        wp.setId(3L);
        wp.setName("pattern3");
        wp.setWorkflowCd("003");
        workflowPatternList.add(wp);

        // Mock準備
        new MockUp<WorkflowPatternDaoImpl>() {
            @Mock List<WorkflowPattern> findAll() {
                return workflowPatternList;
            }
        };

        // テスト実行
        List<WorkflowPattern> actual = service.searchWorkflowPattern();
        assertEquals(workflowPatternList.toString(), actual.toString());
    }

    /**
     * コレポン文書種別を削除する処理を検証.
     * AdminHome、権限がない
     * @throws Exception
     */
    @Test
    public void testDelteAdminNoPermission() throws Exception {
        User loginUser = new User();
        loginUser.setEmpNo("ZZA05");
        loginUser.setNameE("Keiichi Ogiwara");

        // 権限がない
        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN = true;
        MockAbstractService.CURRENT_USER = loginUser;

        CorresponType corresponType = createCorresponType();
        try {
            // テスト実行
            service.delete(corresponType);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT, actual.getMessageCode());
        }
    }

    /**
     * コレポン文書種別を削除する処理を検証.
     * ProjectAdminHome、権限がない
     * @throws Exception
     */
    @Test
    public void testDeleteProjectNoPermission() throws Exception {
        List<Project> lstProject = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        lstProject.add(p);
        User loginUser = new User();
        loginUser.setEmpNo("ZZA05");
        loginUser.setNameE("Keiichi Ogiwara");

        // 権限がない
        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        MockAbstractService.ACCESSIBLE_PROJECTS = lstProject;
        MockAbstractService.CURRENT_USER = loginUser;

        CorresponType corresponType = createCorresponType();
        try {
            // テスト実行
            service.delete(corresponType);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT, actual.getMessageCode());
        }
    }

    /**
     * コレポン文書種別を削除する処理を検証.
     * 引数がnull
     * @throws Exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testDeleteArgumentNull() throws Exception {
        // テスト実行
        service.delete(null);
    }

    /**
     * テスト用のコレポン文書種別を作成する.
     * @return コレポン文書種別
     */
    private CorresponType createCorresponType() {
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
        type.setVersionNo(1L);
        type.setDeleteNo(0L);
        return type;

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
        list.add(type);

        return list;
    }

    /**
     * テスト用のコレポン文書種別リスト（Select用）を作成する.
     * @return
     */
    private List<CorresponType> createCorresponTypeSelectList() {
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
}
