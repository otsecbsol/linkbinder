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
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import jp.co.opentone.bsol.framework.core.dao.RecordNotFoundException;
import jp.co.opentone.bsol.framework.core.dao.StaleRecordException;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.test.AbstractTestCase;
import jp.co.opentone.bsol.linkbinder.dao.impl.CorresponGroupDaoImpl;
import jp.co.opentone.bsol.linkbinder.dao.impl.DisciplineDaoImpl;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroup;
import jp.co.opentone.bsol.linkbinder.dto.Discipline;
import jp.co.opentone.bsol.linkbinder.dto.Project;
import jp.co.opentone.bsol.linkbinder.dto.SearchDisciplineResult;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchDisciplineCondition;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.service.MockAbstractService;
import jp.co.opentone.bsol.linkbinder.service.admin.DisciplineService;
import mockit.Mock;
import mockit.MockUp;

/**
 * {@link DisciplineServiceImpl}のテストケース
 * @author opentone
 */
public class DisciplineServiceImplTest extends AbstractTestCase {

    /**
     * テスト対象.
     */
    @Resource
    private DisciplineService service;

    private User user = new User();

    @BeforeClass
    public static void testSetUp() {
        new MockAbstractService();
    }

    @AfterClass
    public static void testTeardown() {
        new MockAbstractService().tearDown();;
    }

    /**
     * テスト前準備.
     */
    @Before
    public void setUp() {
        user.setEmpNo("ZZA01");
        user.setNameE("Taro Yamada");
        MockAbstractService.CURRENT_USER = user;
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

    /**
     * 部門情報取得処理を検証する.
     * @throws Exception
     */
    @Test
    public void testSearch() throws Exception {
        // テストに必要なデータを作成する
        SearchDisciplineCondition condition = new SearchDisciplineCondition();
        condition.setProjectId("PJ1");

        List<Discipline> lstDis = createDisciplineList();

        // Mock準備
        MockAbstractService.IS_SYSTEM_ADMIN = true;
        new MockUp<DisciplineDaoImpl>() {
            @Mock List<Discipline> find(SearchDisciplineCondition condition) {
                return lstDis;
            }
            @Mock int count(SearchDisciplineCondition condition) {
                return lstDis.size();
            }
        };

        SearchDisciplineResult result = service.search(condition);
        assertEquals(lstDis.size(), result.getCount());

        assertEquals(lstDis.get(0).getId(), result.getDisciplineList().get(0).getId());
        assertEquals(lstDis.get(0).getProjectId(), result.getDisciplineList().get(0).getProjectId());
        assertEquals(lstDis.get(0).getProjectNameE(), result.getDisciplineList().get(0)
            .getProjectNameE());
        assertEquals(lstDis.get(0).getDisciplineCd(), result.getDisciplineList().get(0)
            .getDisciplineCd());
        assertEquals(lstDis.get(0).getName(), result.getDisciplineList().get(0).getName());
        assertEquals(lstDis.get(0).getCreatedBy().toString(), result.getDisciplineList().get(0)
            .getCreatedBy().toString());
        assertEquals(lstDis.get(0).getUpdatedBy().toString(), result.getDisciplineList().get(0)
            .getUpdatedBy().toString());
        assertEquals(lstDis.get(0).getVersionNo(), result.getDisciplineList().get(0).getVersionNo());
        assertEquals(lstDis.get(0).getDeleteNo(), result.getDisciplineList().get(0).getDeleteNo());

        assertEquals(lstDis.get(1).getId(), result.getDisciplineList().get(1).getId());
        assertEquals(lstDis.get(1).getProjectId(), result.getDisciplineList().get(1).getProjectId());
        assertEquals(lstDis.get(1).getProjectNameE(), result.getDisciplineList().get(1)
            .getProjectNameE());
        assertEquals(lstDis.get(1).getDisciplineCd(), result.getDisciplineList().get(1)
            .getDisciplineCd());
        assertEquals(lstDis.get(1).getName(), result.getDisciplineList().get(1).getName());
        assertEquals(lstDis.get(1).getCreatedBy().toString(), result.getDisciplineList().get(1)
            .getCreatedBy().toString());
        assertEquals(lstDis.get(1).getUpdatedBy().toString(), result.getDisciplineList().get(1)
            .getUpdatedBy().toString());
        assertEquals(lstDis.get(1).getVersionNo(), result.getDisciplineList().get(1).getVersionNo());
        assertEquals(lstDis.get(1).getDeleteNo(), result.getDisciplineList().get(1).getDeleteNo());

        assertEquals(lstDis.get(2).getId(), result.getDisciplineList().get(2).getId());
        assertEquals(lstDis.get(2).getProjectId(), result.getDisciplineList().get(2).getProjectId());
        assertEquals(lstDis.get(2).getProjectNameE(), result.getDisciplineList().get(2)
            .getProjectNameE());
        assertEquals(lstDis.get(2).getDisciplineCd(), result.getDisciplineList().get(2)
            .getDisciplineCd());
        assertEquals(lstDis.get(2).getName(), result.getDisciplineList().get(2).getName());
        assertEquals(lstDis.get(2).getCreatedBy().toString(), result.getDisciplineList().get(2)
            .getCreatedBy().toString());
        assertEquals(lstDis.get(2).getUpdatedBy().toString(), result.getDisciplineList().get(2)
            .getUpdatedBy().toString());
        assertEquals(lstDis.get(2).getVersionNo(), result.getDisciplineList().get(2).getVersionNo());
        assertEquals(lstDis.get(2).getDeleteNo(), result.getDisciplineList().get(2).getDeleteNo());
    }

    /**
     * 引数がnullの場合を検証
     * @throws Exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSearchArgumentNull() throws Exception {
        service.search(null);
    }

    /**
     * 部門情報検索処理を検証する.
     * SystemAdmin,ProjectAdmin以外の場合
     * @throws Exception
     */
    @Test
    public void testNotSystemAdminProjectAdminSearch() throws Exception {
        // テストに必要なデータを作成する
        SearchDisciplineCondition condition = new SearchDisciplineCondition();
        condition.setProjectId("PJ1");

        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN = false;

        try {
            service.search(condition);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT,
                actual.getMessageCode());
        }
    }

    /**
     * 部門情報検索処理を検証する.
     *  該当データ0件の場合
     * @throws Exception
     */
    @Test
    public void testNoDataAdminSearch() throws Exception {
        // テストに必要なデータを作成する
        SearchDisciplineCondition condition = new SearchDisciplineCondition();
        condition.setProjectId("PJ1");

        // Mock準備
        MockAbstractService.IS_SYSTEM_ADMIN = true;
        new MockUp<DisciplineDaoImpl>() {
            @Mock int count(SearchDisciplineCondition condition) {
                return 0;
            }
        };

        try {
            service.search(condition);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.NO_DATA_FOUND, actual.getMessageCode());
        }
    }

    /**
     * 部門情報検索処理を検証する.
     * 指定のページを選択した際にそのページに表示するデータが存在しない場合
     * @throws Exception
     */
    @Test
    public void testNoPageAdminSearch() throws Exception {
        // テストに必要なデータを作成する
        SearchDisciplineCondition condition = new SearchDisciplineCondition();
        condition.setProjectId("PJ1");

        // Mock準備
        MockAbstractService.IS_SYSTEM_ADMIN = true;
        new MockUp<DisciplineDaoImpl>() {
            @Mock List<Discipline> find(SearchDisciplineCondition condition) {
                return new ArrayList<>();
            }
            @Mock int count(SearchDisciplineCondition condition) {
                return 1;
            }
        };

        try {
            service.search(condition);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.NO_PAGE_FOUND, actual.getMessageCode());
        }

    }

    /**
     * 指定された会社情報一覧をExcel形式に変換して返す処理を検証
     */
    @Test
    public void testGenerateExcel() throws Exception {
        // 結果レコード
        List<Object> expected = new ArrayList<Object>();
        /* ヘッダ */
        List<Object> line = new ArrayList<Object>();
        line.add("ID");
        line.add("Code");
        line.add("Name");
        expected.add(line);

        /* 1行目 */
        line = new ArrayList<Object>();
        line.add(new Integer(1));
        line.add("BUILDING");
        line.add("Building");
        expected.add(line);

        /* 2行目 */
        line = new ArrayList<Object>();
        line.add(new Integer(2));
        line.add("PIPING");
        line.add("Piping");
        expected.add(line);

        /* 3行目 */
        line = new ArrayList<Object>();
        line.add(new Integer(3));
        line.add("IT");
        line.add("IT");
        expected.add(line);

        // マスタ管理の為、リストを作成する際にnullを渡す。
        byte[] actual = service.generateExcel(createDisciplineList());

        // 作成したExcelを確認
        assertExcel(1, "DisciplineIndex", 3, expected, actual);
    }

    /**
     * 入力値を検証する処理を検証する.
     * 引数がnullの場合
     * @throws Exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testValidateArgumentNull() throws Exception {
        // テストに必要なデータを作成
        service.validate(null);
    }

    /**
     * 入力値を検証する処理を検証する.
     * 登録の場合
     * @throws Exception
     */
    @Test
    public void testValidateInsert() throws Exception {
        // テストに必要なデータを作成
        Discipline discipline = new Discipline();
        discipline.setDisciplineCd("TEST");
        discipline.setName("Test");
        discipline.setCreatedBy(user);
        discipline.setUpdatedBy(user);

        // Mock準備
        MockAbstractService.IS_SYSTEM_ADMIN = true;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        new MockUp<DisciplineDaoImpl>() {
            @Mock List<Discipline> find(SearchDisciplineCondition condition) {
                return new ArrayList<>();
            }
            @Mock int countCheck(SearchDisciplineCondition condition) {
                return 0;
            }
        };

        boolean actual = service.validate(discipline);

        assertTrue(actual);
    }

    /**
     * 入力値を検証する処理を検証する.
     * 登録、SystemAdminProjectAdminではない場合
     * @throws Exception
     */
    @Test
    public void testValidateInsertNotSystemAdminProjectAdmin() throws Exception {
        // テストに必要なデータを作成
        Discipline discipline = new Discipline();
        discipline.setDisciplineCd("TEST");
        discipline.setName("Test");
        discipline.setCreatedBy(user);
        discipline.setUpdatedBy(user);

        // SystemAdmin,ProjectAdminではない
        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";

        try {
            service.validate(discipline);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT,
                actual.getMessageCode());
        }
    }

    /**
     * 入力値を検証する処理を検証する.
     * 登録、同一プロジェクトにある有効な部門の同じ部門コード（discipline_cd）が既に登録されている場合
     * @throws Exception
     */
    @Test
    public void testValidateInsertExistDisciplineCode() throws Exception {
        // テストに必要なデータを作成
        Discipline discipline = new Discipline();
        discipline.setDisciplineCd("TEST");
        discipline.setName("Test");
        discipline.setCreatedBy(user);
        discipline.setUpdatedBy(user);

        // Mock準備
        MockAbstractService.IS_PROJECT_ADMIN = true;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        new MockUp<DisciplineDaoImpl>() {
            @Mock int countCheck(SearchDisciplineCondition condition) {
                // 同じ部門コードが既に登録されている
                return 1;
            }
        };

        try {
            service.validate(discipline);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_DISCIPLINE_CODE_ALREADY_EXISTS,
                actual.getMessageCode());
            assertEquals(1, actual.getMessageVars().length);
            assertEquals("TEST", actual.getMessageVars()[0]);
        }
    }

    /**
     * 入力値を検証する処理を検証する.
     * 更新の場合
     * @throws Exception
     */
    @Test
    public void testValidateUpdate() throws Exception {
        // テストに必要なデータを作成
        Discipline discipline = new Discipline();
        discipline.setId(1L);
        discipline.setDisciplineCd("TEST");
        discipline.setName("Test");
        discipline.setCreatedBy(user);
        discipline.setUpdatedBy(user);

        // Mock準備
        MockAbstractService.IS_SYSTEM_ADMIN = true;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        new MockUp<DisciplineDaoImpl>() {
            @Mock Discipline findById(Long id) throws RecordNotFoundException {
                return discipline;
            }
            @Mock List<Discipline> find(SearchDisciplineCondition condition) {
                return new ArrayList<>();
            }
            @Mock int countCheck(SearchDisciplineCondition condition) {
                return 0;
            }
        };

        boolean actual = service.validate(discipline);

        assertTrue(actual);
    }

    /**
     * 入力値を検証する処理を検証する.
     * 更新の場合 該当データ0件の場合
     * @throws Exception
     */
    @Test
    public void testNoDataValidateUpdate() throws Exception {
        // テストに必要なデータを作成
        Discipline discipline = new Discipline();
        discipline.setId(1L);
        discipline.setDisciplineCd("TEST");
        discipline.setName("Test");
        discipline.setCreatedBy(user);
        discipline.setUpdatedBy(user);

        // Mock準備
        MockAbstractService.IS_SYSTEM_ADMIN = true;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        new MockUp<DisciplineDaoImpl>() {
            @Mock Discipline findById(Long id) throws RecordNotFoundException {
                // 該当データ0件
                throw new RecordNotFoundException();
            }
        };

        try {
            service.validate(discipline);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.NO_DATA_FOUND, actual.getMessageCode());
        }
    }

    /**
     * 入力値を検証する処理を検証する.
     * 更新、同一プロジェクトにある有効な部門の同じ部門コード（discipline_cd）が既に登録されている場合
     * @throws Exception
     */
    @Test
    public void testExistDisciplineValidateUpdate() throws Exception {
        // テストに必要なデータを作成
        Discipline discipline = new Discipline();
        discipline.setId(1L);
        discipline.setDisciplineCd("TEST");
        discipline.setName("Test");
        discipline.setCreatedBy(user);
        discipline.setUpdatedBy(user);

        // Mock準備
        MockAbstractService.IS_SYSTEM_ADMIN = true;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        new MockUp<DisciplineDaoImpl>() {
            @Mock Discipline findById(Long id) throws RecordNotFoundException {
                return discipline;
            }
            @Mock int countCheck(SearchDisciplineCondition condition) {
                // 同じ部門コードが既に登録されている
                return 1;
            }
        };

        try {
            service.validate(discipline);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_DISCIPLINE_CODE_ALREADY_EXISTS,
                actual.getMessageCode());
            assertEquals(1, actual.getMessageVars().length);
            assertEquals("TEST", actual.getMessageVars()[0]);
        }
    }

    /**
     * 指定された部門情報を保存する処理を検証する.
     * 引数がnullの場合
     * @throws Exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSaveArgumentNull() throws Exception {
        // テストに必要なデータを作成
        service.save(null);
    }

    /**
     * 指定された部門情報を保存する処理を検証する.
     * 登録の場合
     * @throws Exception
     */
    @Test
    public void testSaveInsert() throws Exception {
        // テストに必要なデータを作成
        Discipline discipline = new Discipline();
        discipline.setDisciplineCd("TEST");
        discipline.setName("Test");

        // Mock準備
        MockAbstractService.IS_PROJECT_ADMIN = true;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        new MockUp<DisciplineDaoImpl>() {
            @Mock Discipline findById(Long id) throws RecordNotFoundException {
                return discipline;
            }
            @Mock int countCheck(SearchDisciplineCondition condition) {
                return 0;
            }
            @Mock Long create(Discipline d) throws StaleRecordException {
                assertThat(d.getDisciplineCd(), is(discipline.getDisciplineCd()));
                assertThat(d.getName(), is(discipline.getName()));

                return 1L;
            }
        };

        Long id = service.save(discipline);
        assertThat(id, is(1L));
    }

    /**
     * 指定された部門情報を保存する処理を検証する.
     * 登録、SystemAdmin、ProjectAdmin以外の場合
     * @throws Exception
     */
    @Test
    public void testSaveInsertNotSystemAdminProjectAdmin() throws Exception {
        // テストに必要なデータを作成
        Discipline discipline = new Discipline();
        discipline.setDisciplineCd("TEST");
        discipline.setName("Test");

        // SystemAdmin,ProjectAdminではない
        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        try {
            service.save(discipline);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT,
                actual.getMessageCode());
        }

    }

    /**
     * 指定された部門情報を保存する処理を検証する.
     * 登録、同一プロジェクトにある有効な部門の同じ部門コード（discipline_cd）が既に登録されている場合
     * @throws Exception
     */
    @Test
    public void testSaveInsertExistDisciplineCode() throws Exception {
        // テストに必要なデータを作成
        Discipline discipline = new Discipline();
        discipline.setDisciplineCd("TEST");
        discipline.setName("Test");

        // Mock準備
        MockAbstractService.IS_PROJECT_ADMIN = true;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        new MockUp<DisciplineDaoImpl>() {
            @Mock Discipline findById(Long id) throws RecordNotFoundException {
                return discipline;
            }
            @Mock int countCheck(SearchDisciplineCondition condition) {
                // 同じ部門コードが既に登録されている
                return 1;
            }
        };

        try {
            service.save(discipline);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_DISCIPLINE_CODE_ALREADY_EXISTS,
                actual.getMessageCode());
            assertEquals(1, actual.getMessageVars().length);
            assertEquals("TEST", actual.getMessageVars()[0]);
        }

    }

    /**
     * 指定された部門情報を保存する処理を検証する.
     * 更新の場合
     * @throws Exception
     */
    @Test
    public void testSaveUpdate() throws Exception {
        // テストに必要なデータを作成
        Discipline discipline = new Discipline();
        discipline.setDisciplineCd("TEST");
        discipline.setName("Test");
        discipline.setId(1L);
        discipline.setProjectId("PJ1");
        discipline.setVersionNo(1L);

        List<Project> lstPj = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        lstPj.add(p);

        // Mock準備
        MockAbstractService.IS_PROJECT_ADMIN = true;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        MockAbstractService.ACCESSIBLE_PROJECTS = lstPj;
        new MockUp<DisciplineDaoImpl>() {
            @Mock Discipline findById(Long id) throws RecordNotFoundException {
                return discipline;
            }
            @Mock List<Discipline> find(SearchDisciplineCondition condition) {
                return new ArrayList<>();
            }
            @Mock int countCheck(SearchDisciplineCondition condition) {
                return 0;
            }
            @Mock int update(Discipline d) throws StaleRecordException {
                assertThat(d.getId(), is(discipline.getId()));
                assertThat(d.getDisciplineCd(), is(discipline.getDisciplineCd()));
                assertThat(d.getName(), is(discipline.getName()));

                return 1;
            }
        };

        Long updateRecord = service.save(discipline);
        assertThat(updateRecord, is(discipline.getId()));
    }

    /**
     * 指定された部門情報を保存する処理を検証する.
     * 更新、SystemAdmin,ProjectAdminではない場合
     * @throws Exception
     */
    @Test
    public void testNotSystemProjectAdminSaveUpdate() throws Exception {
        // テストに必要なデータを作成
        Discipline discipline = new Discipline();
        discipline.setDisciplineCd("TEST");
        discipline.setName("Test");
        discipline.setId(1L);
        discipline.setProjectId("PJ1");
        discipline.setVersionNo(1L);

        List<Project> lstPj = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        lstPj.add(p);

        // SystemAdmin,ProjectAdminではない
        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        MockAbstractService.ACCESSIBLE_PROJECTS = lstPj;

        try {
            service.save(discipline);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT,
                actual.getMessageCode());
        }
    }

    /**
     * 指定された部門情報を保存する処理を検証する.
     * 更新、同一プロジェクトにある有効な部門の同じ部門コード（discipline_cd）が既に登録されている場合
     * @throws Exception
     */
    @Test
    public void testSaveUpdateExistDisciplineCode() throws Exception {
        // テストに必要なデータを作成
        Discipline discipline = new Discipline();
        discipline.setDisciplineCd("TEST");
        discipline.setName("Test");
        discipline.setId(1L);
        discipline.setProjectId("PJ1");
        discipline.setVersionNo(1L);

        List<Project> lstPj = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        lstPj.add(p);

        // Mock準備
        MockAbstractService.IS_PROJECT_ADMIN = true;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        MockAbstractService.ACCESSIBLE_PROJECTS = lstPj;
        new MockUp<DisciplineDaoImpl>() {
            @Mock Discipline findById(Long id) throws RecordNotFoundException {
                return discipline;
            }
            @Mock int countCheck(SearchDisciplineCondition condition) {
                // 同じ部門コードが既に登録されている
                return 1;
            }
        };

        try {
            service.save(discipline);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_DISCIPLINE_CODE_ALREADY_EXISTS,
                actual.getMessageCode());
            assertEquals(1, actual.getMessageVars().length);
            assertEquals("TEST", actual.getMessageVars()[0]);
        }
    }

    /**
     * 指定された部門情報を保存する処理を検証する.
     * 更新、該当データ0件の場合
     * @throws Exception
     */
    @Test
    public void testSaveUpdateNoData() throws Exception {
        // テストに必要なデータを作成
        Discipline discipline = new Discipline();
        discipline.setDisciplineCd("TEST");
        discipline.setName("Test");
        discipline.setId(1L);
        discipline.setProjectId("PJ1");
        discipline.setVersionNo(1L);
        List<Project> lstPj = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        lstPj.add(p);

        // Mock準備
        MockAbstractService.IS_PROJECT_ADMIN = true;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        MockAbstractService.ACCESSIBLE_PROJECTS = lstPj;
        new MockUp<DisciplineDaoImpl>() {
            @Mock Discipline findById(Long id) throws RecordNotFoundException {
                throw new RecordNotFoundException();
            }
        };

        try {
            service.save(discipline);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.NO_DATA_FOUND, actual.getMessageCode());
        }
    }

    /**
     * 指定された部門情報を保存する処理を検証する.
     * 更新、部門のプロジェクトが現在選択中のプロジェクト以外の場合
     * @throws Exception
     */
    @Test
    public void testSaveUpdateDiffProject() throws Exception {
        // テストに必要なデータを作成
        Discipline discipline = new Discipline();
        discipline.setDisciplineCd("TEST");
        discipline.setName("Test");
        discipline.setId(1L);
        discipline.setProjectId("PJ1");
        discipline.setVersionNo(1L);

        List<Project> lstPj = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ2");
        lstPj.add(p);

        // Mock準備
        MockAbstractService.IS_PROJECT_ADMIN = true;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ2";
        MockAbstractService.ACCESSIBLE_PROJECTS = lstPj;
        new MockUp<DisciplineDaoImpl>() {
            @Mock Discipline findById(Long id) throws RecordNotFoundException {
                return discipline;
            }
            @Mock int countCheck(SearchDisciplineCondition condition) {
                return 0;
            }
        };

        try {
            service.save(discipline);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_PROJECT_DIFF, actual
                .getMessageCode());
        }
    }

    /**
     * StaleRecordException発生時のメッセージコードを検証する
     * 更新の場合
     * @throws Exception
     */
    @Test
    public void testUpdateStaleRecordException() throws Exception {
        // テストに必要なデータを作成
        Discipline discipline = new Discipline();
        discipline.setDisciplineCd("TEST");
        discipline.setName("Test");
        discipline.setId(1L);
        discipline.setProjectId("PJ1");
        discipline.setVersionNo(1L);

        List<Project> lstPj = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        lstPj.add(p);

        // Mock準備
        MockAbstractService.IS_PROJECT_ADMIN = true;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        MockAbstractService.ACCESSIBLE_PROJECTS = lstPj;
        new MockUp<DisciplineDaoImpl>() {
            @Mock Discipline findById(Long id) throws RecordNotFoundException {
                return discipline;
            }
            @Mock List<Discipline> find(SearchDisciplineCondition condition) {
                return new ArrayList<>();
            }
            @Mock int countCheck(SearchDisciplineCondition condition) {
                return 0;
            }
            @Mock int update(Discipline d) throws StaleRecordException {
                throw new StaleRecordException();
            }
        };

        try {
            service.save(discipline);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_DISCIPLINE_ALREADY_UPDATED,
                actual.getMessageCode());
        }
    }

    /**
     * IDを指定して部門情報を取得する処理を検証する.
     * @throws Exception
     */
    @Test
    public void testFind() throws Exception {
        // テストに必要なデータを作成
        Discipline dis = new Discipline();
        dis.setDisciplineCd("TEST");
        dis.setName("Test Discipline");
        dis.setId(1L);
        dis.setProjectId("PJ1");


        List<Project> lstPj = new ArrayList<Project>();
        Project project = new Project();
        project.setProjectId("PJ1");
        lstPj.add(project);

        // Mock準備
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        MockAbstractService.ACCESSIBLE_PROJECTS = lstPj;
        new MockUp<DisciplineDaoImpl>() {
            @Mock Discipline findById(Long id) throws RecordNotFoundException {
                return dis;
            }
        };

        Discipline actual = service.find(1L);

        assertEquals(dis.toString(), actual.toString());

    }

    /**
     * IDを指定して部門情報を取得する処理を検証する.
     * 引数がnullの場合
     * @throws Exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testFindArgumentNull() throws Exception {
        // テストに必要なデータを作成
        service.find(null);
    }

    /**
     * IDを指定して部門情報を取得する処理を検証する.
     * 取得できなかった場合
     * @throws Exception
     */
    @Test
    public void testFindNoData() throws Exception {
        // テストに必要なデータを作成
        Discipline dis = new Discipline();
        dis.setDisciplineCd("TEST");
        dis.setName("Test Discipline");
        dis.setId(1L);

        // Mock準備
        new MockUp<DisciplineDaoImpl>() {
            @Mock Discipline findById(Long id) throws RecordNotFoundException {
                throw new RecordNotFoundException();
            }
        };

        try {
            service.find(1L);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.NO_DATA_FOUND, actual.getMessageCode());
        }
    }

    /**
     * 部門情報を削除する処理を検証.
     * @throws Exception
     */
    @Test
    public void testDelete() throws Exception {
        // テストに必要なデータを作成
        Discipline dis = new Discipline();
        dis.setDisciplineCd("TEST");
        dis.setName("Test Discipline");
        dis.setId(1L);
        dis.setProjectId("PJ1");

        List<Project> lp = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        lp.add(p);

        // Mock準備
        MockAbstractService.ACCESSIBLE_PROJECTS = lp;
        MockAbstractService.IS_PROJECT_ADMIN = true;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        new MockUp<DisciplineDaoImpl>() {
            @Mock Discipline findById(Long id) throws RecordNotFoundException {
                return new Discipline();
            }
            @Mock int delete(Discipline d) throws StaleRecordException {
                assertThat(d.getId(), is(dis.getId()));

                return 1;
            }
        };
        new MockUp<CorresponGroupDaoImpl>() {
            @Mock List<CorresponGroup> findByDisciplineId(Long id) {
                return new ArrayList<>();
            }
        };

        service.delete(dis);
    }

    /**
     * 部門情報を削除する処理を検証.
     * 引数がnullの場合
     * @throws Exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testDeleteArgumentNull() throws Exception {
        service.delete(null);
    }

    /**
     * 部門情報を削除する処理を検証.
     * SystemAdmin,ProjectAdminではない場合
     * @throws Exception
     */
    @Test
    public void testDeleteNotSystemAdminProjectAdmin() throws Exception {
        // テストに必要なデータを作成
        Discipline dis = new Discipline();
        dis.setDisciplineCd("TEST");
        dis.setName("Test Discipline");
        dis.setId(1L);
        dis.setProjectId("PJ1");

        // SystemAdmin,ProjectAdminではない
        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";

        List<Project> lp = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        lp.add(p);
        MockAbstractService.ACCESSIBLE_PROJECTS = lp;

        try {
            service.delete(dis);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT,
                actual.getMessageCode());
        }

    }

    /**
     * 部門情報を削除する処理を検証.
     * 拠点に関連付けられ、活動単位として登録されている場合
     * @throws Exception
     */
    @Test
    public void testDeleteRelatedCorresponGroup() throws Exception {
        // テストに必要なデータを作成
        Discipline dis = new Discipline();
        dis.setDisciplineCd("TEST");
        dis.setName("Test Discipline");
        dis.setId(1L);
        dis.setProjectId("PJ1");


        // 拠点に関連付けられ、活動単位として登録されている
        List<CorresponGroup> cgList = new ArrayList<CorresponGroup>();
        cgList.add(new CorresponGroup());

        List<Project> lp = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        lp.add(p);

        // Mock準備
        MockAbstractService.IS_PROJECT_ADMIN = true;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        MockAbstractService.ACCESSIBLE_PROJECTS = lp;
        new MockUp<DisciplineDaoImpl>() {
            @Mock Discipline findById(Long id) throws RecordNotFoundException {
                return new Discipline();
            }
        };
        new MockUp<CorresponGroupDaoImpl>() {
            @Mock List<CorresponGroup> findByDisciplineId(Long id) {
                return cgList;
            }
        };

        try {
            service.delete(dis);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_DISCIPLINE_ALREADY_RELATED_WITH_SITE,
                actual.getMessageCode());
        }

    }

    /**
     * 部門情報を削除する処理を検証.
     * 該当データ0件の場合
     * @throws Exception
     */
    @Test
    public void testDeleteNoData() throws Exception {
        // テストに必要なデータを作成
        Discipline dis = new Discipline();
        dis.setDisciplineCd("TEST");
        dis.setName("Test Discipline");
        dis.setId(1L);
        dis.setProjectId("PJ1");


        List<Project> lp = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        lp.add(p);

        // Mock準備
        MockAbstractService.IS_PROJECT_ADMIN = true;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        MockAbstractService.ACCESSIBLE_PROJECTS = lp;
        new MockUp<DisciplineDaoImpl>() {
            @Mock Discipline findById(Long id) throws RecordNotFoundException {
                throw new RecordNotFoundException();
            }
        };
        new MockUp<CorresponGroupDaoImpl>() {
            @Mock List<CorresponGroup> findByDisciplineId(Long id) {
                return new ArrayList<>();
            }
        };

        try {
            service.delete(dis);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.NO_DATA_FOUND, actual.getMessageCode());
        }

    }

    /**
     * 部門情報を削除する処理を検証.
     * 部門のプロジェクトが現在選択中のプロジェクトと違う場合
     * @throws Exception
     */
    @Test
    public void testDeleteDiffProject() throws Exception {
        // テストに必要なデータを作成
        Discipline dis = new Discipline();
        dis.setDisciplineCd("TEST");
        dis.setName("Test Discipline");
        dis.setId(1L);
        dis.setProjectId("PJ2");

        List<Project> lp = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        lp.add(p);

        // Mock準備
        MockAbstractService.IS_PROJECT_ADMIN = true;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        MockAbstractService.ACCESSIBLE_PROJECTS = lp;
        new MockUp<DisciplineDaoImpl>() {
            @Mock Discipline findById(Long id) throws RecordNotFoundException {
                return new Discipline();
            }
        };
        new MockUp<CorresponGroupDaoImpl>() {
            @Mock List<CorresponGroup> findByDisciplineId(Long id) {
                return new ArrayList<>();
            }
        };

        try {
            service.delete(dis);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_PROJECT_DIFF, actual
                .getMessageCode());
        }

    }

    /**
     * 部門情報を削除する処理を検証.
     * StaleRecordExceptionが発生した場合（排他チェック）
     * @throws Exception
     */
    @Test
    public void testDeleteStaleRecordException() throws Exception {
        // テストに必要なデータを作成
        Discipline dis = new Discipline();
        dis.setDisciplineCd("TEST");
        dis.setName("Test Discipline");
        dis.setId(1L);
        dis.setProjectId("PJ1");


        List<Project> lp = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        lp.add(p);

        // Mock準備
        MockAbstractService.IS_PROJECT_ADMIN = true;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        MockAbstractService.ACCESSIBLE_PROJECTS = lp;
        new MockUp<DisciplineDaoImpl>() {
            @Mock Discipline findById(Long id) throws RecordNotFoundException {
                return new Discipline();
            }
            @Mock int delete(Discipline d) throws StaleRecordException {
                throw new StaleRecordException();
            }
        };
        new MockUp<CorresponGroupDaoImpl>() {
            @Mock List<CorresponGroup> findByDisciplineId(Long id) {
                return new ArrayList<>();
            }
        };

        try {
            service.delete(dis);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_DISCIPLINE_ALREADY_UPDATED,
                actual.getMessageCode());
        }
    }

    private List<Discipline> createDisciplineList() {
        List<Discipline> lstDis = new ArrayList<Discipline>();

        Discipline dis = new Discipline();
        dis.setId(1L);
        dis.setProjectId("PJ1");
        dis.setProjectNameE("Test Project1");
        dis.setDisciplineCd("BUILDING");
        dis.setName("Building");
        dis.setCreatedBy(user);
        dis.setUpdatedBy(user);
        dis.setVersionNo(1L);
        dis.setDeleteNo(0L);

        lstDis.add(dis);

        dis = new Discipline();
        dis.setId(2L);
        dis.setProjectId("PJ1");
        dis.setProjectNameE("Test Project1");
        dis.setDisciplineCd("PIPING");
        dis.setName("Piping");
        dis.setCreatedBy(user);
        dis.setUpdatedBy(user);
        dis.setVersionNo(1L);
        dis.setDeleteNo(0L);

        lstDis.add(dis);

        dis = new Discipline();
        dis.setId(3L);
        dis.setProjectId("PJ1");
        dis.setProjectNameE("Test Project1");
        dis.setDisciplineCd("IT");
        dis.setName("IT");
        dis.setCreatedBy(user);
        dis.setUpdatedBy(user);
        dis.setVersionNo(1L);
        dis.setDeleteNo(0L);

        lstDis.add(dis);
        return lstDis;
    }
}
