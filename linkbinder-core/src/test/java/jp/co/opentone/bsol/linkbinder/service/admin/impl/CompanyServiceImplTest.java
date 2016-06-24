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
import jp.co.opentone.bsol.linkbinder.dao.impl.CompanyDaoImpl;
import jp.co.opentone.bsol.linkbinder.dao.impl.CompanyUserDaoImpl;
import jp.co.opentone.bsol.linkbinder.dao.impl.ProjectCompanyDaoImpl;
import jp.co.opentone.bsol.linkbinder.dao.impl.UserDaoImpl;
import jp.co.opentone.bsol.linkbinder.dto.Company;
import jp.co.opentone.bsol.linkbinder.dto.CompanyUser;
import jp.co.opentone.bsol.linkbinder.dto.Project;
import jp.co.opentone.bsol.linkbinder.dto.ProjectCompany;
import jp.co.opentone.bsol.linkbinder.dto.ProjectUser;
import jp.co.opentone.bsol.linkbinder.dto.SearchCompanyResult;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCompanyCondition;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchUserCondition;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.service.MockAbstractService;
import jp.co.opentone.bsol.linkbinder.service.admin.CompanyService;
import mockit.Mock;
import mockit.MockUp;

/**
 * {@link CompanyServiceImpl}のテストケース.
 * @author opentone
 */
public class CompanyServiceImplTest extends AbstractTestCase {

    /**
     * テスト対象.
     */
    @Resource
    private CompanyService service;

    private User user = new User();

    @BeforeClass
    public static void testSetUp() {
        new MockAbstractService();
    }

    @AfterClass
    public static void testTeardown() {
        new MockAbstractService().tearDown();
    }

    /**
     * テスト前準備.
     */
    @Before
    public void setUp() {
        user.setEmpNo("ZZA01");
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
        MockAbstractService.PROJECT_USER = null;
    }

    private List<Company> createCompanyList() {
        List<Company> companyList = new ArrayList<Company>();

        Company company = new Company();
        company.setCompanyCd("AAAAA");
        company.setName("Cupsule Corporation");
        company.setId(1L);
        companyList.add(company);

        company = new Company();
        company.setCompanyCd("BBBBB");
        company.setName("Micheal Corporation");
        company.setId(2L);
        companyList.add(company);

        company = new Company();
        company.setCompanyCd("CCCCC");
        company.setName("Roland Corporation");
        company.setId(3L);
        companyList.add(company);

        return companyList;
    }

    private ProjectUser createProjectUser(User user) {
        ProjectUser projectUser = new ProjectUser();
        projectUser.setProjectId("PJ1");
        projectUser.setUser(user);

        return projectUser;
    }

    /**
     * マスタ管理会社情報を取得できるか検証する.
     * @throws Exception
     */
    @Test
    public void testSearchMaster() throws Exception {
        // テストに必要な値を作成
        SearchCompanyCondition condition = new SearchCompanyCondition();
        List<Company> companyList = createCompanyList();

        // SystemAdmin
        MockAbstractService.IS_SYSTEM_ADMIN = true;
        // Mock準備
        new MockUp<CompanyDaoImpl>() {
            @Mock List<Company> find(SearchCompanyCondition condition) {
                return companyList;
            }
            @Mock int countCompany(SearchCompanyCondition condition) {
                return companyList.size();
            }
        };

        // テスト実行
        SearchCompanyResult result = service.search(condition);

        assertEquals(companyList.toString(), result.getCompanyList().toString());
        assertEquals(companyList.size(), result.getCount());
    }

    /**
     * プロジェクトマスタ会社情報を取得できるか検証する. ProjectAdminの場合
     * @throws Exception
     */
    @Test
    public void testSearchProjectProjectAdmin() throws Exception {
        // テストに必要な値を作成
        SearchCompanyCondition condition = new SearchCompanyCondition();
        condition.setProjectId("project");

        List<Company> companyList = createCompanyList();
        ProjectUser projectUser = createProjectUser(user);

        // ProjectAdmin
        MockAbstractService.IS_PROJECT_ADMIN = true;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        MockAbstractService.PROJECT_USER = projectUser;
        // Mock準備
        new MockUp<CompanyDaoImpl>() {
            @Mock List<Company> find(SearchCompanyCondition condition) {
                return companyList;
            }
            @Mock int countCompany(SearchCompanyCondition condition) {
                return companyList.size();
            }
        };

        // テスト実行
        SearchCompanyResult result = service.search(condition);

        assertEquals(companyList.toString(), result.getCompanyList().toString());
        assertEquals(companyList.size(), result.getCount());
    }

    /**
     * プロジェクトマスタ会社情報を取得できるか検証する. Admin権限なし
     * @throws Exception
     */
    @Test
    public void testSearchProject() throws Exception {
        // テストに必要な値を作成
        SearchCompanyCondition condition = new SearchCompanyCondition();
        condition.setProjectId("project");

        List<Company> companyList = createCompanyList();
        ProjectUser projectUser = createProjectUser(user);

        // SystemAdmin,ProjectAdminではない
        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";

        MockAbstractService.PROJECT_USER = projectUser;
        // Mock準備
        new MockUp<CompanyDaoImpl>() {
            @Mock List<Company> find(SearchCompanyCondition condition) {
                return companyList;
            }
            @Mock int countCompany(SearchCompanyCondition condition) {
                return companyList.size();
            }
        };

        // テスト実行
        SearchCompanyResult result = service.search(condition);

        assertEquals(companyList.toString(), result.getCompanyList().toString());
        assertEquals(companyList.size(), result.getCount());
    }

    /**
     * プロジェクトマスタ会社情報を取得できるか検証する. SystemAdminの場合
     * @throws Exception
     */
    @Test
    public void testSearchProjectSystemAdmin() throws Exception {
        // テストに必要な値を作成
        SearchCompanyCondition condition = new SearchCompanyCondition();
        condition.setProjectId("project");

        List<Company> companyList = createCompanyList();

        // ProjectAdmin
        MockAbstractService.IS_SYSTEM_ADMIN = true;
        // Mock準備
        new MockUp<CompanyDaoImpl>() {
            @Mock List<Company> find(SearchCompanyCondition condition) {
                return companyList;
            }
            @Mock int countCompany(SearchCompanyCondition condition) {
                return companyList.size();
            }
        };

        // テスト実行
        SearchCompanyResult result = service.search(condition);

        assertEquals(companyList.toString(), result.getCompanyList().toString());
        assertEquals(companyList.size(), result.getCount());
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
     * プロジェクトに所属する会社情報を取得する.
     * @throws Exception
     */
    @Test
    public void testSearchRelatedToProject() throws Exception {
        String projectId = "PJ1";

        List<Company> expected = createCompanyList(projectId);
        new MockUp<CompanyDaoImpl>() {
            @Mock List<Company> find(SearchCompanyCondition condition) {
                return expected;
            }
        };

        List<Company> actual = service.searchRelatedToProject(projectId);

        assertEquals(expected, actual);
    }

    /**
     * プロジェクトに所属する会社情報を取得する.
     * 引数がNull.
     * @throws Exception
     */
    @Test(expected=IllegalArgumentException.class)
    public void testSearchRelatedToProjectNull() throws Exception {
        service.searchRelatedToProject(null);
    }

    /**
     * マスタ管理会社情報検索処理を検証 SystemAdminではない場合
     * @throws Exception
     */
    @Test
    public void testSearchNotSystemAdmin() throws Exception {
        MockAbstractService.IS_SYSTEM_ADMIN = false;
        try {
            // テスト実行
            service.search(new SearchCompanyCondition());
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT,
                actual.getMessageCode());
        }
    }

    /**
     * マスタ管理会社情報検索処理を検証 該当データが0件の場合
     * @throws Exception
     */
    @Test
    public void testSearchDataNone() throws Exception {
        MockAbstractService.IS_SYSTEM_ADMIN = true;
        // Mock準備
        new MockUp<CompanyDaoImpl>() {
            @Mock List<Company> find(SearchCompanyCondition condition) {
                return new ArrayList<>();
            }
            @Mock int countCompany(SearchCompanyCondition condition) {
                return 0;
            }
        };

        try {
            // テスト実行
            service.search(new SearchCompanyCondition());
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.NO_DATA_FOUND, actual.getMessageCode());

        }
    }

    /**
     * マスタ管理会社情報検索処理を検証 指定のページを選択した際に表示するデータが存在しない場合
     * @throws Exception
     */
    @Test
    public void testSearchSpecifyPageNoData() throws Exception {
        MockAbstractService.IS_SYSTEM_ADMIN = true;
        // Mock準備
        new MockUp<CompanyDaoImpl>() {
            @Mock List<Company> find(SearchCompanyCondition condition) {
                return new ArrayList<>();
            }
            @Mock int countCompany(SearchCompanyCondition condition) {
                return 10;
            }
        };

        try {
            // テスト実行
            service.search(new SearchCompanyCondition());
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.NO_PAGE_FOUND, actual.getMessageCode());
        }
    }

    /**
     * プロジェクトマスタ管理会社情報検索処理を検証 権限共通チェック[2](SystemAdmin,ProjectAdmin以外)の場合
     * @throws Exception
     */
    @Test
    public void testSearchProjectInvalidCommonCheck() throws Exception {
        // SystemAdmin,ProjectAdmin以外
        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        try {
            // テスト実行
            service.search(new SearchCompanyCondition());
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT,
                actual.getMessageCode());
        }
    }

    /**
     * プロジェクトマスタ管理会社情報検索処理を検証 該当データ0件の場合
     * @throws Exception
     */
    @Test
    public void testSearchProjectDataNone() throws Exception {
        // SystemAdmin,ProjectAdmin以外
        MockAbstractService.IS_SYSTEM_ADMIN = true;
        // Mock準備
        new MockUp<CompanyDaoImpl>() {
            @Mock List<Company> find(SearchCompanyCondition condition) {
                return new ArrayList<>();
            }
            @Mock int countCompany(SearchCompanyCondition condition) {
                return 0;
            }
        };

        try {
            // テスト実行
            service.search(new SearchCompanyCondition());
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.NO_DATA_FOUND, actual.getMessageCode());

        }
    }

    /**
     * プロジェクトマスタ管理会社情報検索処理を検証 指定のページを選択した際に表示するデータが存在しない場合
     * @throws Exception
     */
    @Test
    public void testSearchProjectSpecifyPageNoData() throws Exception {
        // SystemAdmin,ProjectAdmin以外
        MockAbstractService.IS_SYSTEM_ADMIN = true;
        // Mock準備
        new MockUp<CompanyDaoImpl>() {
            @Mock List<Company> find(SearchCompanyCondition condition) {
                return new ArrayList<>();
            }
            @Mock int countCompany(SearchCompanyCondition condition) {
                return 10;
            }
        };

        try {
            // テスト実行
            service.search(new SearchCompanyCondition());
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.NO_PAGE_FOUND, actual.getMessageCode());

        }
    }

    /**
     * プロジェクトマスタ会社情報を取得できるか検証する. ログインユーザーが選択しているプロジェクトに属していない
     * @throws Exception
     */    @Test
    public void testSearchProjectUserDiff() throws Exception{
        // TODO
        // テストに必要な値を作成
        SearchCompanyCondition condition = new SearchCompanyCondition();
        condition.setProjectId("project");
        List<Company> companyList = createCompanyList();

        // SystemAdmin,ProjectAdminではない
        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        // ログインユーザーが選択しているプロジェクトに属していない
        MockAbstractService.PROJECT_USER = null;
        // Mock準備
        new MockUp<CompanyDaoImpl>() {
            @Mock List<Company> find(SearchCompanyCondition condition) {
                return companyList;
            }
            @Mock int countCompany(SearchCompanyCondition condition) {
                return companyList.size();
            }
        };

        try {
            // テスト実行
            service.search(condition);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_PROJECT_DIFF, actual.getMessageCode());
        }
    }

    /**
     * 指定された会社情報一覧をExcel形式に変換して返すテスト. マスタ管理の場合を検証
     */
    @Test
    public void testGenerateExcelMaster() throws Exception {
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
        line.add("OT");
        line.add("OT Corporation");
        expected.add(line);

        /* 2行目 */
        line = new ArrayList<Object>();
        line.add(new Integer(2));
        line.add("OT");
        line.add("Open Tone");
        expected.add(line);

        /* 3行目 */
        line = new ArrayList<Object>();
        line.add(new Integer(3));
        line.add("CC");
        line.add("Cupusule Corporation");
        expected.add(line);

        // マスタ管理の為、リストを作成する際にnullを渡す。
        byte[] actual = service.generateExcel(createCompanyList(null));

        // 作成したExcelを確認
        assertExcel(1, "CompanyIndex", 3, expected, actual);
    }

    /**
     * 指定された会社情報一覧をExcel形式に変換して返すテスト. プロジェクトマスタ管理の場合を検証
     */
    @Test
    public void testGenerateExcelProject() throws Exception {
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";

        // 結果レコード
        List<Object> expected = new ArrayList<Object>();
        /* ヘッダ */
        List<Object> line = new ArrayList<Object>();
        line.add("ID");
        line.add("Code");
        line.add("Name");
        line.add("Role");
        expected.add(line);

        /* 1行目 */
        line = new ArrayList<Object>();
        line.add(new Integer(11));
        line.add("OT");
        line.add("OT Corporation");
        line.add("Owner");
        expected.add(line);

        /* 2行目 */
        line = new ArrayList<Object>();
        line.add(new Integer(22));
        line.add("OT");
        line.add("Open Tone");
        line.add("Owner");
        expected.add(line);

        /* 3行目 */
        line = new ArrayList<Object>();
        line.add(new Integer(33));
        line.add("CC");
        line.add("Cupusule Corporation");
        line.add("Owner");
        expected.add(line);

        // プロジェクトマスタ管理の為、リストを作る際に引数を渡す
        byte[] actual = service.generateExcel(createCompanyList("PJ1"));

        // 作成したExcelを確認
        assertExcel(1, "CompanyIndex", 3, expected, actual);
    }

    /**
     * 会社情報を新規登録する マスタ管理の場合を検証
     */
    @Test
    public void testSaveMaster() throws Exception {
        // テストに必要なデータを作成する
        Company company = new Company();
        company.setCompanyCd("ABC");

        company.setName("ABC Company");
        company.setVersionNo(1L);

        List<Company> companyList = createCompanyList(null);

        MockAbstractService.IS_SYSTEM_ADMIN = true;
        // Mock準備
        new MockUp<CompanyDaoImpl>() {
            @Mock List<Company> find(SearchCompanyCondition condition) {
                return companyList;
            }
            @Mock int countCompany(SearchCompanyCondition condition) {
                return companyList.size();
            }
            @Mock int countCheck(SearchCompanyCondition condition) {
                return 0;
            }

            @Mock Long create(Company c) {
                assertThat(c.getCompanyCd(), is(company.getCompanyCd()));
                assertThat(c.getName(), is(company.getName()));
                assertThat(c.getCreatedBy(), is(user));
                assertThat(c.getUpdatedBy(), is(user));

                return 1L;
            }
        };

        // テスト実行
        Long id = service.save(company);
        assertThat(id, is(1L));
    }

    /**
     * 会社情報を新規登録する マスタ管理かつSystemAdminではない場合を検証
     */
    @Test
    public void testInvalidSystemAdminSaveMaster() throws Exception {
        // テストに必要なデータを作成する
        Company company = new Company();
        company.setCompanyCd("ABC");
        // company.setId(1L);
        company.setName("ABC Company");
        company.setVersionNo(1L);

        List<Company> companyList = createCompanyList(null);

        MockAbstractService.IS_SYSTEM_ADMIN = false;
        // Mock準備
        new MockUp<CompanyDaoImpl>() {
            @Mock List<Company> find(SearchCompanyCondition condition) {
                return companyList;
            }
            @Mock int countCompany(SearchCompanyCondition condition) {
                return companyList.size();
            }
            @Mock int countCheck(SearchCompanyCondition condition) {
                return 0;
            }
        };

        // テスト実行
        try {
            service.save(company);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT,
                actual.getMessageCode());
        }
    }

    /**
     * 会社情報を新規登録する マスタ管理かつ有効な会社情報の同じ会社コード（company_cd）が既に登録されている場合を検証
     */
    @Test
    public void testInvalidAlreadyCompanyExistSaveMaster() throws Exception {
        // テストに必要なデータを作成する
        Company company = new Company();
        company.setCompanyCd("ABC");
        company.setName("ABC Company");
        company.setVersionNo(1L);

        MockAbstractService.IS_SYSTEM_ADMIN = true;
        // Mock準備
        new MockUp<CompanyDaoImpl>() {
            @Mock int countCheck(SearchCompanyCondition condition) {
                // 同じ会社コードが既に登録されている
                return 1;
            }
        };

        // テスト実行
        try {
            service.save(company);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_COMPANY_CODE_ALREADY_EXISTS,
                actual.getMessageCode());
            assertEquals(1, actual.getMessageVars().length);
            assertEquals("ABC", actual.getMessageVars()[0]);
        }
    }

    /**
     * 会社情報を新規登録する マスタ管理かつ有効な会社情報の同じ会社コード（company_cd）が既に登録されている場合を検証
     */
    @Test
    public void testInvalidAlreadyCompanyExistSaveMasterUpdate() throws Exception {
        // テストに必要なデータを作成する
        Company company = new Company();
        company.setId(1L);
        company.setCompanyCd("ABC");
        company.setName("ABC Company");
        company.setVersionNo(1L);

        MockAbstractService.IS_SYSTEM_ADMIN = true;
        // Mock準備
        new MockUp<CompanyDaoImpl>() {
            @Mock Company findById(Long id) throws RecordNotFoundException {
                return company;
            }

            @Mock int countCheck(SearchCompanyCondition condition) {
                // 同じ会社コードが既に登録されている
                return 1;
            }
        };

        // テスト実行
        try {
            service.save(company);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_COMPANY_CODE_ALREADY_EXISTS,
                actual.getMessageCode());
            assertEquals(1, actual.getMessageVars().length);
            assertEquals("ABC", actual.getMessageVars()[0]);
        }
    }

    /**
     * 会社情報をプロジェクトに登録する処理を検証
     */
    @Test
    public void testAssignToProject() throws Exception {
        // テストに必要なデータを作成する
        Company company = new Company();

        company.setProjectId("PJ1");
        company.setCompanyCd("ABC");
        company.setId(3L);
        company.setName("ABC Company");
        company.setVersionNo(1L);
        company.setRole("Owner");

        MockAbstractService.IS_PROJECT_ADMIN = true;
        // Mock準備
        new MockUp<CompanyDaoImpl>() {
            @Mock Company findById(Long id) throws RecordNotFoundException {
                return company;
            }

            @Mock int countCompany(SearchCompanyCondition condition) {
                return 1;
            }

            @Mock int countCheck(SearchCompanyCondition condition) {
                // 同じ会社コードが既に登録されている
                return 1;
            }

            @Mock Company findProjectCompanyById(Long id, String projectId)
                    throws RecordNotFoundException {
                throw new RecordNotFoundException();
            }
        };
        new MockUp<ProjectCompanyDaoImpl>() {
            @Mock Long create(ProjectCompany c) {
                assertThat(c.getCompanyId(), is(company.getId()));
                assertThat(c.getCreatedBy(), is(user));
                assertThat(c.getUpdatedBy(), is(user));

                return 1L;
            }
        };

        // テスト実行
        Long entryId = service.assignTo(company);
        assertThat(entryId, is(1L));
    }

    /**
     * 会社情報をプロジェクトに登録する. SystemAdmin、ProjectAdminではない場合を検証する
     */
    @Test
    public void testInvalidSystemAdminAssignToProject() throws Exception {
        // テストに必要なデータを作成する
        Company company = new Company();

        company.setProjectId("PJ1");
        company.setCompanyCd("ABC");
        company.setId(1L);
        company.setName("ABC Company");
        company.setVersionNo(1L);
        company.setRole("Owner");

        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        // テスト実行
        try {
            service.assignTo(company);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(actual.getMessageCode(),
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT);
        }
    }

    /**
     * 会社情報をプロジェクトに登録する. 登録する会社が存在しない場合を検証
     */
    @Test
    public void testInvalidCompanyCountAssignToProject() throws Exception {
        // テストに必要なデータを作成する
        Company company = new Company();

        company.setProjectId("PJ1");
        company.setCompanyCd("ABC");
        company.setId(1L);
        company.setName("ABC Company");
        company.setVersionNo(1L);
        company.setRole("Owner");

        MockAbstractService.IS_PROJECT_ADMIN = true;
        // Mock準備
        new MockUp<CompanyDaoImpl>() {
            @Mock Company findById(Long id) throws RecordNotFoundException {
                // レコードが存在しない
                throw new RecordNotFoundException();
            }
        };

        // テスト実行
        try {
            service.assignTo(company);
            fail("例外発生していない");
        } catch (ServiceAbortException actual) {
            assertThat(actual.getMessageCode(), is(ApplicationMessageCode.NO_DATA_FOUND));
        }
    }

    /**
     * 会社情報をプロジェクトに登録する. 登録する会社が既にプロジェクト登録済の場合を検証する.
     */
    @Test
    public void testInvalidAlreadyAssignedAssignToProject() throws Exception {
        // テストに必要なデータを作成する
        Company company = new Company();

        company.setProjectId("PJ1");
        company.setCompanyCd("ABC");
        company.setId(1L);
        company.setName("ABC Company");
        company.setVersionNo(1L);
        company.setRole("Owner");

        MockAbstractService.IS_PROJECT_ADMIN = true;
        // Mock準備
        new MockUp<CompanyDaoImpl>() {
            @Mock Company findById(Long id) throws RecordNotFoundException {
                return company;
            }
            @Mock Company findProjectCompanyById(Long id, String projectId) {
                // 既に登録済み
                return company;
            }
        };

        // テスト実行
        try {
            service.assignTo(company);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(actual.getMessageCode(),
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_COMPANY_ALREADY_ASSIGNED_TO_PROJECT);
        }
    }

    /**
     * 指定した会社情報を取得する処理を検証.
     * @throws Exception
     */
    @Test
    public void testFindMaster() throws Exception {
        // テストに必要な値を作成
        User user = new User();
        user.setEmpNo("TEST1");
        Company company = new Company();
        company.setCompanyCd("TEST");
        company.setId(1L);
        company.setName("TEST_COMPANY");
        company.setCreatedBy(user);
        company.setUpdatedBy(user);

        MockAbstractService.IS_SYSTEM_ADMIN = true;
        // Mock準備
        new MockUp<CompanyDaoImpl>() {
            @Mock Company findById(Long id) throws RecordNotFoundException {
                return company;
            }
        };

        assertEquals(company.toString(), service.find(1L).toString());
    }

    /**
     * 指定した会社情報を取得する処理を検証. 該当データ0件の場合
     * @throws Exception
     */
    @Test
    public void testNotDataFind() throws Exception {
        MockAbstractService.IS_SYSTEM_ADMIN = true;
        // Mock準備
        new MockUp<CompanyDaoImpl>() {
            @Mock Company findById(Long id) throws RecordNotFoundException {
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
     * 指定した会社情報を取得する処理を検証. プロジェクトマスタ管理
     * @throws Exception
     */
    @Test
    public void testFindProject() throws Exception {
        // テストに必要な値を作成
        User user = new User();
        user.setEmpNo("TEST1");
        Company company = new Company();
        company.setCompanyCd("TEST");
        company.setId(1L);
        company.setName("TEST_COMPANY");
        company.setCreatedBy(user);
        company.setUpdatedBy(user);
        company.setProjectId("PJ1");

        List<Project> lp = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        lp.add(p);

        // Mock準備
        MockAbstractService.ACCESSIBLE_PROJECTS = lp;
        MockAbstractService.IS_PROJECT_ADMIN = true;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        new MockUp<CompanyDaoImpl>() {
            @Mock Company findProjectCompanyById(Long id, String projectId) {
                return company;
            }
        };

        assertEquals(company, service.find(1L));
    }

    /**
     * 指定した会社情報を取得する処理を検証. プロジェクトマスタ管理 会社のプロジェクトが現在選択中のプロジェクト以外の場合
     * @throws Exception
     */
    @Test
    public void testProjectDiffFindProject() throws Exception {
        // テストに必要な値を作成
        User user = new User();
        user.setEmpNo("TEST1");
        Company company = new Company();
        company.setCompanyCd("TEST");
        company.setId(1L);
        company.setName("TEST_COMPANY");
        company.setCreatedBy(user);
        company.setUpdatedBy(user);
        company.setProjectId("PJ2");

        List<Project> lp = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ2");
        lp.add(p);

        // Mock準備
        MockAbstractService.ACCESSIBLE_PROJECTS = lp;
        MockAbstractService.IS_PROJECT_ADMIN = true;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        new MockUp<CompanyDaoImpl>() {
            @Mock Company findProjectCompanyById(Long id, String projectId) {
                return company;
            }
        };
        try {
            service.find(1L);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_PROJECT_DIFF, actual
                .getMessageCode());
        }
    }

    /**
     * Validateチェックを検証する マスタ管理新規登録
     * @throws Exception
     */
    @Test
    public void testValidateMaster() throws Exception {
        // テストに必要な値を作成
        User user = new User();
        user.setEmpNo("TEST1");
        Company company = new Company();
        company.setCompanyCd("TEST");
        company.setName("TEST_COMPANY");
        company.setCreatedBy(user);
        company.setUpdatedBy(user);

        // Mock準備
        MockAbstractService.IS_SYSTEM_ADMIN = true;
        new MockUp<CompanyDaoImpl>() {
            @Mock int countCheck(SearchCompanyCondition condition) {
                return 0;
            }
        };

        boolean validate = service.validate(company);
        assertTrue(validate);
    }

    /**
     * Validateチェックを検証する マスタ管理新規登録 SystemAdminではない場合
     * @throws Exception
     */
    @Test
    public void testValidateNotSystemAdminMaster() throws Exception {
        // テストに必要な値を作成
        User user = new User();
        user.setEmpNo("TEST1");
        Company company = new Company();
        company.setCompanyCd("TEST");
        company.setName("TEST_COMPANY");
        company.setCreatedBy(user);
        company.setUpdatedBy(user);

        MockAbstractService.IS_PROJECT_ADMIN = true;
        try {
            service.validate(company);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT,
                actual.getMessageCode());
        }
    }

    /**
     * Validateチェックを検証する マスタ管理更新
     * @throws Exception
     */
    @Test
    public void testValidateUpdateMaster() throws Exception {
        // テストに必要な値を作成
        User user = new User();
        user.setEmpNo("TEST1");
        Company company = new Company();
        company.setCompanyCd("TEST");
        company.setId(1L);
        company.setName("TEST_COMPANY");
        company.setCreatedBy(user);
        company.setUpdatedBy(user);

        // Mock準備
        MockAbstractService.IS_SYSTEM_ADMIN = true;
        new MockUp<CompanyDaoImpl>() {
            @Mock Company findById(Long id) throws RecordNotFoundException {
                return company;
            }
            @Mock int countCheck(SearchCompanyCondition condition) {
                return 0;
            }
        };


        boolean validate = service.validate(company);
        assertTrue(validate);
    }

    /**
     * Validateチェックを検証する マスタ管理新規登録 同じ会社コードの会社がすでに登録されている場合
     * @throws Exception
     */
    @Test
    public void testValidateExistCompanyCodeMaster() throws Exception {
        // テストに必要な値を作成
        User user = new User();
        user.setEmpNo("TEST1");
        Company company = new Company();
        company.setCompanyCd("TEST");
        company.setName("TEST_COMPANY");
        company.setCreatedBy(user);
        company.setUpdatedBy(user);

        // Mock準備
        MockAbstractService.IS_SYSTEM_ADMIN = true;
        new MockUp<CompanyDaoImpl>() {
            @Mock int countCheck(SearchCompanyCondition condition) {
                // 同じ会社コードが既に登録されている
                return 1;
            }
        };


        try {
            service.validate(company);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_COMPANY_CODE_ALREADY_EXISTS,
                actual.getMessageCode());
            assertEquals(1, actual.getMessageVars().length);
            assertEquals("TEST", actual.getMessageVars()[0]);
        }
    }

    /**
     * Validateチェックを検証する マスタ管理新規登録 同じ会社コードの会社がすでに登録されている場合
     * @throws Exception
     */
    @Test
    public void testValidateExistCompanyCodeMasterUpdate() throws Exception {
        // テストに必要な値を作成
        User user = new User();
        user.setEmpNo("TEST1");
        Company company = new Company();
        company.setId(1L);
        company.setCompanyCd("TEST");
        company.setName("TEST_COMPANY");
        company.setCreatedBy(user);
        company.setUpdatedBy(user);

        // Mock準備
        MockAbstractService.IS_SYSTEM_ADMIN = true;
        new MockUp<CompanyDaoImpl>() {
            @Mock Company findById(Long id) throws RecordNotFoundException {
                return company;
            }
            @Mock int countCheck(SearchCompanyCondition condition) {
                // 同じ会社コードが既に登録されている
                return 1;
            }
        };

        try {
            service.validate(company);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_COMPANY_CODE_ALREADY_EXISTS,
                actual.getMessageCode());
            assertEquals(1, actual.getMessageVars().length);
            assertEquals("TEST", actual.getMessageVars()[0]);
        }
    }

    /**
     * Validateチェックを検証する マスタ管理更新 更新対象データが0件の場合
     * @throws Exception
     */
    @Test
    public void testValidateNoDataUpdateMaster() throws Exception {
        // テストに必要な値を作成
        User user = new User();
        user.setEmpNo("TEST1");
        Company company = new Company();
        company.setCompanyCd("TEST");
        company.setId(1L);
        company.setName("TEST_COMPANY");
        company.setCreatedBy(user);
        company.setUpdatedBy(user);

        // Mock準備
        MockAbstractService.IS_SYSTEM_ADMIN = true;
        new MockUp<CompanyDaoImpl>() {
            @Mock Company findById(Long id) throws RecordNotFoundException {
                // レコードが存在しない
                throw new RecordNotFoundException();
            }
            @Mock int countCheck(SearchCompanyCondition condition) {
                return 0;
            }
        };

        try {
            service.validate(company);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.NO_DATA_FOUND, actual.getMessageCode());
        }

    }

    /**
     * Validateチェックを検証する プロジェクトマスタ管理更新
     * @throws Exception
     */
    @Test
    public void testValidateProject() throws Exception {
        // テストに必要な値を作成
        User user = new User();
        user.setEmpNo("TEST1");
        Company company = new Company();
        company.setCompanyCd("TEST");
        company.setName("TEST_COMPANY");
        company.setCreatedBy(user);
        company.setUpdatedBy(user);
        company.setId(1L);
        company.setProjectId("PJ1");

        List<Project> pl = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        pl.add(p);

        // Mock準備
        MockAbstractService.IS_PROJECT_ADMIN = true;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        MockAbstractService.ACCESSIBLE_PROJECTS = pl;
        new MockUp<CompanyDaoImpl>() {
            @Mock Company findProjectCompanyById(Long id, String projectId) {
                return company;
            }
        };

        boolean validate = service.validate(company);
        assertTrue(validate);
    }

    /**
     * Validateチェックを検証する プロジェクトマスタ管理更新 SystemAdmin,ProjectAdmin以外の場合
     * @throws Exception
     */
    @Test
    public void testValidateNotSystemAdminProjectAdminProject() throws Exception {
        // テストに必要な値を作成
        User user = new User();
        user.setEmpNo("TEST1");
        Company company = new Company();
        company.setCompanyCd("TEST");
        company.setName("TEST_COMPANY");
        company.setCreatedBy(user);
        company.setUpdatedBy(user);
        company.setId(1L);
        company.setProjectId("PJ1");

        List<Project> pl = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        pl.add(p);

        // SystemAdmin,ProjectAdminではない
        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        MockAbstractService.ACCESSIBLE_PROJECTS = pl;

        try {
            service.validate(company);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT,
                actual.getMessageCode());
        }
    }

    /**
     * Validateチェックを検証する プロジェクトマスタ管理更新 該当データが0件の場合
     * @throws Exception
     */
    @Test
    public void testValidateNoDataProject() throws Exception {
        // テストに必要な値を作成
        User user = new User();
        user.setEmpNo("TEST1");
        Company company = new Company();
        company.setCompanyCd("TEST");
        company.setName("TEST_COMPANY");
        company.setCreatedBy(user);
        company.setUpdatedBy(user);
        company.setId(1L);
        company.setProjectId("PJ1");


        List<Project> pl = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        pl.add(p);

        // Mock準備
        MockAbstractService.IS_PROJECT_ADMIN = true;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        MockAbstractService.ACCESSIBLE_PROJECTS = pl;
        new MockUp<CompanyDaoImpl>() {
            @Mock Company findProjectCompanyById(Long id, String projectId) throws RecordNotFoundException {
                throw new RecordNotFoundException();
            }
        };
        try {
            service.validate(company);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.NO_DATA_FOUND, actual.getMessageCode());
        }
    }

    /**
     * Validateチェックを検証する プロジェクトマスタ管理更新 会社のプロジェクトが現在選択中のプロジェクト以外の場合
     * @throws Exception
     */
    @Test
    public void testValidateProjectDiffProject() throws Exception {
        // テストに必要な値を作成
        User user = new User();
        user.setEmpNo("TEST1");
        Company company = new Company();
        company.setCompanyCd("TEST");
        company.setName("TEST_COMPANY");
        company.setCreatedBy(user);
        company.setUpdatedBy(user);
        company.setId(1L);
        company.setProjectId("PJ1");

        List<Project> pl = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ2");
        pl.add(p);

        // Mock準備
        MockAbstractService.IS_PROJECT_ADMIN = true;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        MockAbstractService.ACCESSIBLE_PROJECTS = pl;
        new MockUp<CompanyDaoImpl>() {
            @Mock Company findProjectCompanyById(Long id, String projectId) throws RecordNotFoundException {
                return company;
            }
        };

        try {
            service.validate(company);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_PROJECT_DIFF, actual
                .getMessageCode());
        }
    }

    /**
     * 指定した会社情報を取得する処理 引数がない場合を検証
     * @throws Exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testFindNoArgument() throws Exception {
        service.find(null);
    }

    /**
     * 会社情報を更新する
     * @throws Exception
     */
    @Test
    public void testSaveUpdateCompanyMaster() throws Exception {
        // テストに必要なデータを作成
        User user = new User();
        user.setEmpNo("ZZA01");
        user.setNameE("Test Taro");
        Company company = new Company();
        company.setCompanyCd("TEST");
        company.setName("TestCompany");
        company.setCreatedBy(user);
        company.setUpdatedBy(user);
        company.setId(1L);
        company.setVersionNo(1L);

        MockAbstractService.IS_SYSTEM_ADMIN = true;
        // Mock準備
        new MockUp<CompanyDaoImpl>() {
            @Mock Company findById(Long id) {
                return company;
            }
            @Mock int countCheck(SearchCompanyCondition condition) {
                return 0;
            }

            @Mock Integer update(Company c) {
                assertThat(c.getId(), is(company.getId()));
                assertThat(c.getCompanyCd(), is(company.getCompanyCd()));
                assertThat(c.getName(), is(company.getName()));

                return 1;
            }
        };

        // 実行
        Long id = service.save(company);
        assertThat(id, is(company.getId()));
    }

    /**
     * プロジェクト会社情報を更新する
     * @throws Exception
     */
    @Test
    public void testSaveUpdateProjectCompany() throws Exception {
        // テストに必要なデータを作成
        User user = new User();
        user.setEmpNo("ZZA01");
        user.setNameE("Test Taro");
        Company company = new Company();
        company.setCompanyCd("TEST");
        company.setName("TestCompany");
        company.setCreatedBy(user);
        company.setUpdatedBy(user);
        company.setId(1L);
        company.setVersionNo(1L);
        company.setProjectId("PJ1");
        company.setRole("Owner");

        company.setProjectCompanyId(4L);
        Company findProjectCompanyById = new Company();

        findProjectCompanyById.setUpdatedBy(user);
        findProjectCompanyById.setProjectCompanyId(4L);

        User loginUser = new User();
        loginUser.setEmpNo("TEST1");
        loginUser.setNameE("LOGIN TARO");

        List<Project> pl = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        pl.add(p);

        // Mock準備
        MockAbstractService.IS_PROJECT_ADMIN = true;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        MockAbstractService.CURRENT_USER = loginUser;
        MockAbstractService.ACCESSIBLE_PROJECTS = pl;
        new MockUp<CompanyDaoImpl>() {
            @Mock Company findProjectCompanyById(Long id, String projectId) {
                return company;
            }

            @Mock Integer update(Company c) {
                assertThat(c.getId(), is(company.getId()));
                return 1;
            }
        };
        new MockUp<ProjectCompanyDaoImpl>() {
            @Mock Company findById(Long id) {
                return company;
            }
            @Mock Integer update(ProjectCompany c) {
                assertThat(c.getId(), is(company.getProjectCompanyId()));

                return 1;
            }
        };

        // 実行
        Long id = service.save(company);
        assertThat(id, is(company.getId()));
    }

    /**
     * 会社情報を更新する マスタ管理 StaleRecordExceptionが発生した場合
     * @throws Exception
     */
    @Test
    public void testSaveUpdateCompanyMasterStaleRecordException() throws Exception {
        // テストに必要なデータを作成
        User user = new User();
        user.setEmpNo("ZZA01");
        user.setNameE("Test Taro");
        Company company = new Company();
        company.setCompanyCd("TEST");
        company.setName("TestCompany");
        company.setCreatedBy(user);
        company.setUpdatedBy(user);
        company.setId(1L);
        company.setVersionNo(1L);

        // Mock準備
        MockAbstractService.IS_SYSTEM_ADMIN = true;
        new MockUp<CompanyDaoImpl>() {
            @Mock Company findById(Long id) {
                return company;
            }
            @Mock int countCheck(SearchCompanyCondition condition) {
                return 0;
            }

            @Mock Integer update(Company c) throws StaleRecordException {
                throw new StaleRecordException();
            }
        };

        // 実行
        try {
            service.save(company);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_COMPANY_ALREADY_UPDATED,
                actual.getMessageCode());
        }
    }

    /**
     * プロジェクト会社情報を更新する
     * @throws Exception
     */
    @Test
    public void testSaveUpdateProjectCompanyStaleRecordException() throws Exception {
        // テストに必要なデータを作成
        User user = new User();
        user.setEmpNo("ZZA01");
        user.setNameE("Test Taro");
        Company company = new Company();
        company.setCompanyCd("TEST");
        company.setName("TestCompany");
        company.setCreatedBy(user);
        company.setUpdatedBy(user);
        company.setId(1L);
        company.setVersionNo(1L);
        company.setProjectId("PJ1");
        company.setRole("Owner");

        Company findProjectCompanyById = new Company();

        findProjectCompanyById.setUpdatedBy(user);
        findProjectCompanyById.setProjectCompanyId(4L);

        List<Project> pl = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        pl.add(p);

        // Mock準備
        MockAbstractService.IS_PROJECT_ADMIN = true;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        MockAbstractService.ACCESSIBLE_PROJECTS = pl;
        new MockUp<CompanyDaoImpl>() {
            @Mock Company findProjectCompanyById(Long id, String projectId) {
                return company;
            }
        };
        new MockUp<ProjectCompanyDaoImpl>() {
            @Mock Company findById(Long id) {
                return company;
            }
            @Mock Integer update(ProjectCompany c) throws StaleRecordException {
                throw new StaleRecordException();
            }
        };

        try {
            // 実行
            service.save(company);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_COMPANY_ALREADY_UPDATED,
                actual.getMessageCode());
        }
    }

    /**
     * 会社に所属するユーザーを検索する処理を検証.
     * @throws Exception
     */
    @Test
    public void testFindMembers() throws Exception {
        // テストに必要な値を作成
        List<CompanyUser> listCompanyUser = createFindMembers();

        // Mock準備
        MockAbstractService.IS_SYSTEM_ADMIN = true;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        new MockUp<CompanyDaoImpl>() {
            @Mock List<CompanyUser> findMembers(Long id) {
                return listCompanyUser;
            }
        };

        assertEquals(listCompanyUser, service.findMembers(1L));
    }

    /**
     * 会社に所属するユーザーを検索する処理を検証. SystemAdmin,ProjectAdminではない場合
     * @throws Exception
     */
    @Test
    public void testNotSystemAdminProjectAdminFindMembers() throws Exception {
        // SystemAdmin,ProjectAdminではない
        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        try {
            service.findMembers(1L);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT,
                actual.getMessageCode());
        }
    }

    /**
     * 会社に所属するユーザーを検索する処理を検証. 引数が渡ってこなかった場合
     * @throws Exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testFindMembersArgumentNull() throws Exception {
        // テストに必要な値を作成
        service.findMembers(null);
    }

    /**
     * 会社に所属するユーザーを登録する処理を検証.
     * @throws Exception
     */
    @Test
    public void testSaveMembers() throws Exception {
        // テストに必要なデータを作成する
        Company company = new Company();
        company.setId(1L);
        company.setProjectCompanyId(1234L);
        company.setVersionNo(0L);
        company.setUpdatedBy(user);

        List<User> listUser = new ArrayList<User>();
        User u = new User();
        u.setEmpNo("ZZA02");
        u.setNameE("Test Jiro");
        listUser.add(u);

        u = new User();
        u.setEmpNo("ZZA03");
        u.setNameE("Test Gouki");
        listUser.add(u);

        u = new User();
        u.setEmpNo("ZZA07");
        u.setNameE("Test Seven");
        listUser.add(u);

        List<ProjectUser> listPu = new ArrayList<ProjectUser>();
        for (User user : listUser) {
            ProjectUser pu = new ProjectUser();
            pu.setUser(user);
            listPu.add(pu);
        }

        // Mock準備
        MockAbstractService.IS_PROJECT_ADMIN = true;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        new MockUp<UserDaoImpl>() {
            @Mock List<ProjectUser> findProjectUser(SearchUserCondition condition) {
                return listPu;
            }
            @Mock int countCheck(SearchUserCondition condition) {
                return 1;
            }
        };
        new MockUp<CompanyDaoImpl>() {
            @Mock int update(Company u) {
                return 1;
            }
        };
        new MockUp<CompanyUserDaoImpl>() {
            @Mock Integer deleteByCompanyId(CompanyUser companyUser) {
                return listPu.size();
            }

            int index = 0;
            @Mock Long create(CompanyUser u) {
                assertThat(u.getUser().getEmpNo(), is(listUser.get(index).getEmpNo()));

                index++;
                return 1L;
            }
        };


        service.saveMembers(company, listUser);
    }

    /**
     * 会社に所属するユーザーを登録する処理を検証. 引数がnullの場合
     * @throws Exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSaveMembersArgumentNull1() throws Exception {
        service.saveMembers(null, null);
    }

    /**
     * 会社に所属するユーザーを登録する処理を検証. 引数がnullの場合
     * @throws Exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSaveMembersArgumentNull2() throws Exception {
        service.saveMembers(new Company(), null);
    }

    /**
     * 会社に所属するユーザーを登録する処理を検証. 引数がnullの場合
     * @throws Exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSaveMembersArgumentNull3() throws Exception {
        service.saveMembers(null, new ArrayList<User>());
    }

    /**
     * 会社に所属するユーザーを登録する処理を検証. SystemAdmin,ProjectAdminではない場合
     * @throws Exception
     */
    @Test
    public void testSaveMembersNotSystemAdminProjectAdmin() throws Exception {
        // SystemAdmin,ProjectAdminではない
        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN = false;

        try {
            service.saveMembers(new Company(), new ArrayList<User>());
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT,
                actual.getMessageCode());
        }
    }

    /**
     * 会社に所属するユーザーを登録する処理を検証.
     * 指定したユーザーがプロジェクトマスタに存在しない場合.
     * @throws Exception
     */
    @Test
    public void testSaveMembersNotExistProjectUser() throws Exception {
        // テストに必要なデータを作成する
        Company company = new Company();
        company.setId(1L);

        List<User> listUser = new ArrayList<User>();
        User u = new User();
        u.setEmpNo("00001");
        listUser.add(u);

        List<ProjectUser> listPu = new ArrayList<ProjectUser>();
        for (User user : listUser) {
            ProjectUser pu = new ProjectUser();
            pu.setUser(user);
            listPu.add(pu);
        }

        // Mock準備
        MockAbstractService.IS_PROJECT_ADMIN = true;
        new MockUp<UserDaoImpl>() {
            @Mock List<ProjectUser> findProjectUser(SearchUserCondition condition) {
                return listPu;
            }
            @Mock int countCheck(SearchUserCondition condition) {
                return 0;
            }
        };

        try {
            service.saveMembers(company, listUser);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.NO_DATA_FOUND, actual
                .getMessageCode());
        }
    }

    /**
     * 会社に所属するユーザーを登録する処理を検証. 選択されたプロジェクトユーザーのユーザーが同一プロジェクトに属していない場合
     * @throws Exception
     */
    @Test
    public void testSaveMembersNotExistProject() throws Exception {
        // テストに必要なデータを作成する
        Company company = new Company();
        company.setId(1L);

        List<User> listUser = new ArrayList<User>();
        listUser.add(new User());

        // Mock準備
        MockAbstractService.IS_PROJECT_ADMIN = true;
        new MockUp<UserDaoImpl>() {
            @Mock int countCheck(SearchUserCondition condition) {
                return 0;
            }
        };

        try {
            service.saveMembers(company, listUser);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.NO_DATA_FOUND, actual
                .getMessageCode());
        }
    }

    /**
     * 会社に所属するユーザーを登録する処理を検証. StaleRecordExceptionが発生した場合
     * @throws Exception
     */
    @Test
    public void testSaveMembersStaleRecordException() throws Exception {
        // テストに必要なデータを作成する
        Company company = new Company();
        company.setId(1L);
        company.setProjectCompanyId(1234L);
        company.setVersionNo(0L);
        company.setUpdatedBy(user);

        List<User> listUser = new ArrayList<User>();

        User u = new User();
        u.setEmpNo("ZZA02");
        u.setNameE("Test Jiro");
        listUser.add(u);

        u = new User();
        u.setEmpNo("ZZA03");
        u.setNameE("Test Gouki");
        listUser.add(u);

        u = new User();
        u.setEmpNo("ZZA07");
        u.setNameE("Test Seven");
        listUser.add(u);

        List<ProjectUser> listPu = new ArrayList<ProjectUser>();
        for (User user : listUser) {
            ProjectUser pu = new ProjectUser();
            pu.setUser(user);
            listPu.add(pu);
        }

        // Mock準備
        MockAbstractService.IS_PROJECT_ADMIN = true;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        new MockUp<UserDaoImpl>() {
            @Mock List<ProjectUser> findProjectUser(SearchUserCondition condition) {
                return listPu;
            }
            @Mock int countCheck(SearchUserCondition condition) {
                return 1;
            }
        };
        new MockUp<CompanyDaoImpl>() {
            @Mock int update(Company u) throws StaleRecordException {
                throw new StaleRecordException();
            }
        };
        new MockUp<CompanyUserDaoImpl>() {
            @Mock Integer deleteByCompanyId(CompanyUser companyUser) {
                return listPu.size();
            }
            int index = 0;
            @Mock Long create(CompanyUser u) {
                assertThat(u.getUser().getEmpNo(), is(listUser.get(index).getEmpNo()));

                index++;
                return 1L;
            }
        };


        try {
            service.saveMembers(company, listUser);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_COMPANY_ALREADY_UPDATED,
                actual.getMessageCode());
        }

    }

    /**
     * マスタ管理、指定会社情報を削除する処理を検証
     * @throws Exception
     */
    @Test
    public void testDeleteMaster() throws Exception {
        // テストに必要なデータを削除
        Company company = new Company();
        company.setId(1L);
        company.setCompanyCd("TEST");
        company.setName("Test Company");
        company.setVersionNo(1L);

        MockAbstractService.IS_SYSTEM_ADMIN = true;
        new MockUp<CompanyDaoImpl>() {
            @Mock Company findById(Long id) {
                return new Company();
            }
            @Mock int countCompany(SearchCompanyCondition condition) {
                return 1;
            }
            @Mock int delete(Company u) throws StaleRecordException {
                assertThat(u.getId(), is(company.getId()));
                return 1;
            }
        };
        new MockUp<ProjectCompanyDaoImpl>() {
            @Mock int countCheck(SearchCompanyCondition condition) {
                return 0;
            }
        };

        service.delete(company);
    }

    /**
     * マスタ管理、指定会社情報を削除する処理を検証 SystemAdminではない場合
     * @throws Exception
     */
    @Test
    public void testNotSystemAdminDeleteMaster() throws Exception {
        // テストに必要なデータを削除
        Company company = new Company();
        company.setId(1L);
        company.setCompanyCd("TEST");
        company.setName("Test Company");

        // SystemAdminではない
        MockAbstractService.IS_SYSTEM_ADMIN = false;
        try {
            service.delete(company);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT,
                actual.getMessageCode());
        }
    }

    /**
     * マスタ管理、指定会社情報を削除する処理を検証 削除対象の会社が、プロジェクトに関連付けられている場合
     * @throws Exception
     */
    @Test
    public void testRelatedProjectDeleteMaster() throws Exception {
        // テストに必要なデータを削除
        Company company = new Company();
        company.setId(1L);
        company.setCompanyCd("TEST");
        company.setName("Test Company");

        List<Company> lstCom = new ArrayList<Company>();
        lstCom.add(new Company());

        // Mock準備
        MockAbstractService.IS_SYSTEM_ADMIN = true;
        new MockUp<CompanyDaoImpl>() {
            @Mock List<Company> find(SearchCompanyCondition condition) {
                return lstCom;
            }
            @Mock int countCompany(SearchCompanyCondition condition) {
                return 1;
            }
        };
        new MockUp<ProjectCompanyDaoImpl>() {
            @Mock int countCheck(SearchCompanyCondition condition) {
                return 1;
            }
        };

        try {
            service.delete(company);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_COMPANY_ALREADY_ASSIGNED_TO_PROJECT,
                actual.getMessageCode());
        }
    }

    /**
     * マスタ管理、指定会社情報を削除する処理を検証 該当データ0件の場合
     * @throws Exception
     */
    @Test
    public void testNoDataDeleteMaster() throws Exception {
        // テストに必要なデータを削除
        Company company = new Company();
        company.setId(1L);
        company.setCompanyCd("TEST");
        company.setName("Test Company");

        // Mock準備
        MockAbstractService.IS_SYSTEM_ADMIN = true;
        new MockUp<CompanyDaoImpl>() {
            @Mock Company findById(Long id) throws RecordNotFoundException {
                throw new RecordNotFoundException();
            }
        };
        new MockUp<ProjectCompanyDaoImpl>() {
            @Mock int countCheck(SearchCompanyCondition condition) {
                return 0;
            }
        };

        try {
            service.delete(company);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.NO_DATA_FOUND, actual.getMessageCode());
        }
    }

    /**
     * プロジェクトマスタ管理、指定会社情報を削除する処理を検証
     * @throws Exception
     */
    @Test
    public void testDeleteProject() throws Exception {
        // テストに必要なデータを削除
        Company company = new Company();
        company.setId(1L);
        company.setCompanyCd("TEST");
        company.setName("Test Company");
        company.setProjectCompanyId(34L);
        company.setProjectId("PJ1");

        List<Project> lstPj = new ArrayList<Project>();
        Project project = new Project();
        project.setProjectId("PJ1");
        lstPj.add(project);

        // Mock準備
        MockAbstractService.IS_PROJECT_ADMIN = true;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        MockAbstractService.ACCESSIBLE_PROJECTS = lstPj;
        new MockUp<CompanyDaoImpl>() {
            @Mock Company findById(Long id) {
                return new Company();
            }
            @Mock int countCompany(SearchCompanyCondition condition) {
                return 1;
            }
            @Mock List<CompanyUser> findMembers(Long id) {
                return new ArrayList<>();
            }
            @Mock Company findProjectCompanyById(Long id, String projectId) {
                return new Company();
            }
            @Mock int update(Company u) throws StaleRecordException {
                assertThat(u.getId(), is(company.getId()));
                return 1;
            }
        };
        new MockUp<ProjectCompanyDaoImpl>() {
            @Mock int countCheck(SearchCompanyCondition condition) {
                return 0;
            }
            @Mock int delete(ProjectCompany u) throws StaleRecordException {
                assertThat(u.getId(), is(company.getProjectCompanyId()));
                return 1;
            }
        };


        service.delete(company);
    }

    /**
     * プロジェクトマスタ管理、指定会社情報を削除する処理を検証 SystemAdmin,ProjectAdminではない場合
     * @throws Exception
     */
    @Test
    public void testNotSystemAdminProjectAdminDeleteProject() throws Exception {
        // テストに必要なデータを削除
        Company company = new Company();
        company.setId(1L);
        company.setCompanyCd("TEST");
        company.setName("Test Company");
        company.setProjectCompanyId(34L);
        company.setProjectId("PJ1");

        // SystemAdminProjectAdminではない
        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        try {
            service.delete(company);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT,
                actual.getMessageCode());
        }
    }

    /**
     * プロジェクトマスタ管理、指定会社情報を削除する処理を検証 削除対象の会社に、活動するユーザーが関連付けられている場合。
     * @throws Exception
     */
    @Test
    public void testRelatedUserDeleteProject() throws Exception {
        // テストに必要なデータを削除
        Company company = new Company();
        company.setId(1L);
        company.setCompanyCd("TEST");
        company.setName("Test Company");
        company.setProjectCompanyId(34L);
        company.setProjectId("PJ1");


        List<Project> lstPj = new ArrayList<Project>();
        Project project = new Project();
        project.setProjectId("PJ1");
        lstPj.add(project);

        CompanyUser cu = new CompanyUser();
        List<CompanyUser> lstCu = new ArrayList<CompanyUser>();
        lstCu.add(cu);

        // Mock準備
        MockAbstractService.IS_PROJECT_ADMIN = true;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        MockAbstractService.ACCESSIBLE_PROJECTS = lstPj;
        new MockUp<CompanyDaoImpl>() {
            @Mock List<CompanyUser> findMembers(Long id) {
                return lstCu;
            }
        };
        try {
            service.delete(company);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_COMPANY_ALREADY_RELATED_WITH_USER,
                actual.getMessageCode());
        }
    }

    /**
     * プロジェクトマスタ管理、指定会社情報を削除する処理を検証 該当データ0件の場合
     * @throws Exception
     */
    @Test
    public void testNoDataDeleteProject() throws Exception {
        // テストに必要なデータを削除
        Company company = new Company();
        company.setId(1L);
        company.setCompanyCd("TEST");
        company.setName("Test Company");
        company.setProjectCompanyId(34L);
        company.setProjectId("PJ1");


        List<Project> lstPj = new ArrayList<Project>();
        Project project = new Project();
        project.setProjectId("PJ1");
        lstPj.add(project);

        // Mock準備
        MockAbstractService.IS_PROJECT_ADMIN = true;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        MockAbstractService.ACCESSIBLE_PROJECTS = lstPj;
        new MockUp<CompanyDaoImpl>() {
            @Mock List<Company> find(SearchCompanyCondition condition) {
                return new ArrayList<>();
            }
            @Mock int countCompany(SearchCompanyCondition condition) {
                return 0;
            }
            @Mock List<CompanyUser> findMembers(Long id) {
                return new ArrayList<>();
            }
            @Mock Company findProjectCompanyById(Long id, String projectId) throws RecordNotFoundException {
                throw new RecordNotFoundException();
            }
        };

        try {
            service.delete(company);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.NO_DATA_FOUND, actual.getMessageCode());
        }
    }

    /**
     *Projectに追加する会社情報を取得する処理の検証.
     * @throws Exception
     */
    @Test
    public void testFindNotAssignTo() throws Exception {
        // テストに必要なデータを作成する
        List<Company> projectNotCompanyList = new ArrayList<Company>();

        Company company = new Company();
        company.setId(1L);
        company.setCompanyCd("TEST1");
        company.setName("Test1");
        projectNotCompanyList.add(company);

        company = new Company();
        company.setId(2L);
        company.setCompanyCd("TEST2");
        company.setName("Test2");
        projectNotCompanyList.add(company);

        company = new Company();
        company.setId(3L);
        company.setCompanyCd("TEST3");
        company.setName("Test3");
        projectNotCompanyList.add(company);

        // Mock準備
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        new MockUp<CompanyDaoImpl>() {
            @Mock List<Company> findNotAssignTo(String projectId) {
                return projectNotCompanyList;
            }
        };

        List<Company> actual = service.searchNotAssigned();

        assertEquals(projectNotCompanyList.toString(), actual.toString());
    }

    private List<CompanyUser> createFindMembers() {
        List<CompanyUser> listCompanyUser = new ArrayList<CompanyUser>();

        User u = new User();
        u.setEmpNo("ZZA02");
        u.setNameE("Test Jiro");

        CompanyUser cu = new CompanyUser();
        cu.setProjectCompanyId(4L);
        cu.setProjectId("PJ1");
        cu.setUser(u);
        listCompanyUser.add(cu);

        u = new User();
        u.setEmpNo("ZZA03");
        u.setNameE("Test Gouki");

        cu = new CompanyUser();
        cu.setProjectCompanyId(5L);
        cu.setProjectId("PJ1");
        cu.setUser(u);
        listCompanyUser.add(cu);

        u = new User();
        u.setEmpNo("ZZA07");
        u.setNameE("Test Seven");

        cu = new CompanyUser();
        cu.setProjectCompanyId(7L);
        cu.setProjectId("PJ1");
        cu.setUser(u);
        listCompanyUser.add(cu);
        return listCompanyUser;
    }

    private List<Company> createCompanyList(String projectId) {
        List<Company> company = new ArrayList<Company>();
        Company com = new Company();

        com.setCompanyCd("OT");
        com.setName("OT Corporation");
        com.setId(1L);

        if (projectId != null) {
            com.setProjectCompanyId(11L);
            com.setRole("Owner");
            com.setProjectId(projectId);
        }

        company.add(com);

        com = new Company();
        com.setCompanyCd("OT");
        com.setName("Open Tone");
        com.setId(2L);

        if (projectId != null) {
            com.setProjectCompanyId(22L);
            com.setRole("Owner");
            com.setProjectId(projectId);
        }

        company.add(com);

        com = new Company();
        com.setCompanyCd("CC");
        com.setName("Cupusule Corporation");
        com.setId(3L);

        if (projectId != null) {
            com.setProjectCompanyId(33L);
            com.setRole("Owner");
            com.setProjectId(projectId);
        }

        company.add(com);

        return company;
    }
}
