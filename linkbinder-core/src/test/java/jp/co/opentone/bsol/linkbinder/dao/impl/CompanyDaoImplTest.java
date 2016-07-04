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
package jp.co.opentone.bsol.linkbinder.dao.impl;

import java.util.List;

import javax.annotation.Resource;

import org.dbunit.dataset.ITable;
import org.junit.Test;

import jp.co.opentone.bsol.framework.core.dao.KeyDuplicateException;
import jp.co.opentone.bsol.framework.core.dao.StaleRecordException;
import jp.co.opentone.bsol.framework.core.extension.ibatis.DBValue;
import jp.co.opentone.bsol.framework.test.AbstractDaoTestCase;
import jp.co.opentone.bsol.linkbinder.dto.Company;
import jp.co.opentone.bsol.linkbinder.dto.CompanyUser;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCompanyCondition;

/**
 * CompanyDaoImplのテストケース.
 * @author opentone
 */
public class CompanyDaoImplTest extends AbstractDaoTestCase {

    /**
     * テスト対象.
     */
    @Resource
    private CompanyDaoImpl dao;

    private static final int DEFAULT_PAGE_NO = 1;

    private static final int DEFAULT_ROW_NUM = 10;

    /**
     * {@link CompanyDaoImpl#find(SearchCompanyCondition)}のテストケース. マスタ管理の場合を検証
     * @throws Exception
     */
    @Test
    public void testFindMaster() throws Exception {
        SearchCompanyCondition condition = new SearchCompanyCondition();
        condition.setPageNo(DEFAULT_PAGE_NO);
        condition.setPageRowNum(DEFAULT_ROW_NUM);
        List<Company> companyList = dao.find(condition);

        // 取得したオブジェクトと、Excelファイルに定義済の期待値を比較する
        assertNotNull(companyList);
        assertDataSetEquals(newDataSet("CompanyDaoImplTest_testFindMaster_expected.xls"),
                            companyList);
    }

    /**
     * {@link CompanyDaoImpl#find(SearchCompanyCondition)}のテストケース.
     * プロジェクトマスタ管理の場合を検証
     * @throws Exception
     */
    @Test
    public void testFindProject() throws Exception {
        SearchCompanyCondition condition = new SearchCompanyCondition();
        condition.setPageNo(DEFAULT_PAGE_NO);
        condition.setPageRowNum(DEFAULT_ROW_NUM);
        condition.setProjectId("PJ1");

        List<Company> companyList = dao.find(condition);

        // 取得したオブジェクトと、Excelファイルに定義済みの期待値を比較する
        assertNotNull(companyList);
        assertDataSetEquals(newDataSet("CompanyDaoImplTest_testFindProject_expected.xls"),
                            companyList);

    }

    /**
     * {@link CompanyDaoImpl#count(SearchCompanyCondition)}のテストケース. マスタ管理の場合を検証
     * @throws Exception
     */
    @Test
    public void testCountMaster() throws Exception {
        SearchCompanyCondition condition = new SearchCompanyCondition();
        int companyCount = dao.countCompany(condition);

        // 取得したカウント数を確認 削除した会社情報は取得カウントしない
        assertEquals(14, companyCount);

    }

    /**
     * {@link CompanyDaoImpl#count(SearchCompanyCondition)}のテストケース.
     * プロジェクトマスタ管理の場合を検証
     * @throws Exception
     */
    @Test
    public void testCountProject() throws Exception {
        SearchCompanyCondition condition = new SearchCompanyCondition();
        condition.setProjectId("PJ1");

        int companyCount = dao.countCompany(condition);

        // 取得したカウント数を確認 削除した会社情報は取得カウントしない
        assertEquals(14, companyCount);
    }

    /**
     * {@link CompanyDaoImpl#find(SearchCompanyCondition)}のテストケース.
     * マスタ管理で検索条件を設定した場合を検証
     * @throws Exception
     */
    @Test
    public void testFindMasterSearch() throws Exception {
        SearchCompanyCondition condition = new SearchCompanyCondition();
        condition.setPageNo(DEFAULT_PAGE_NO);
        condition.setPageRowNum(DEFAULT_ROW_NUM);
        condition.setCompanyCd("A");

        List<Company> companyList = dao.find(condition);

        // 取得したオブジェクトと、Excelファイルに定義済の期待値を比較する
        assertNotNull(companyList);
        assertDataSetEquals(newDataSet("CompanyDaoImplTest_testFindMasterSearch_expected.xls"),
                            companyList);
    }

    /**
     * {@link CompanyDaoImpl#find(SearchCompanyCondition)}のテストケース.
     * プロジェクトマスタ管理の場合を検証
     * @throws Exception
     */
    @Test
    public void testFindProjectSearch() throws Exception {
        SearchCompanyCondition condition = new SearchCompanyCondition();
        condition.setPageNo(DEFAULT_PAGE_NO);
        condition.setPageRowNum(DEFAULT_ROW_NUM);
        condition.setProjectId("PJ1");
        condition.setCompanyCd("O");

        List<Company> companyList = dao.find(condition);

        // 取得したオブジェクトと、Excelファイルに定義済みの期待値を比較する
        assertNotNull(companyList);
        assertDataSetEquals(newDataSet("CompanyDaoImplTest_testFindProjectSearch_expected.xls"),
                            companyList);

    }

    /**
     * {@link CompanyDaoImpl#find(SearchCompanyCondition)}のテストケース.
     * マスタ管理でページ指定した場合を検証
     * @throws Exception
     */
    @Test
    public void testFindMasterPage() throws Exception {
        SearchCompanyCondition condition = new SearchCompanyCondition();
        condition.setPageNo(2);
        condition.setPageRowNum(DEFAULT_ROW_NUM);

        List<Company> companyList = dao.find(condition);

        // 取得したオブジェクトと、Excelファイルに定義済の期待値を比較する
        assertNotNull(companyList);
        assertDataSetEquals(newDataSet("CompanyDaoImplTest_testFindMasterPage_expected.xls"),
                            companyList);
    }

    /**
     * {@link CompanyDaoImpl#find(SearchCompanyCondition)}のテストケース.
     * プロジェクトマスタ管理でページ指定した場合を検証
     * @throws Exception
     */
    @Test
    public void testFindProjectPage() throws Exception {
        SearchCompanyCondition condition = new SearchCompanyCondition();
        condition.setPageNo(2);
        condition.setPageRowNum(DEFAULT_ROW_NUM);
        condition.setProjectId("PJ1");

        List<Company> companyList = dao.find(condition);

        // 取得したオブジェクトと、Excelファイルに定義済みの期待値を比較する
        assertNotNull(companyList);
        assertDataSetEquals(newDataSet("CompanyDaoImplTest_testFindProjectPage_expected.xls"),
                            companyList);

    }

    /**
     * {@link CompanyDaoImpl#find(SearchCompanyCondition)}のテストケース.
     * マスタ管理で検索条件を設定した場合を検証
     * @throws Exception
     */
    @Test
    public void testFindMasterSearchNoData() throws Exception {
        SearchCompanyCondition condition = new SearchCompanyCondition();
        condition.setPageNo(DEFAULT_PAGE_NO);
        condition.setPageRowNum(DEFAULT_ROW_NUM);
        condition.setCompanyCd("A");
        condition.setName("z");

        List<Company> companyList = dao.find(condition);

        // 検索条件のnameが存在しないので、取得できない
        assertEquals(0, companyList.size());

    }

    /**
     * {@link CompanyDaoImpl#find(SearchCompanyCondition)}のテストケース.
     * プロジェクトマスタ管理の場合を検証
     * @throws Exception
     */
    @Test
    public void testFindProjectSearchNoData() throws Exception {
        SearchCompanyCondition condition = new SearchCompanyCondition();
        condition.setPageNo(DEFAULT_PAGE_NO);
        condition.setPageRowNum(DEFAULT_ROW_NUM);
        condition.setProjectId("PJ1");
        condition.setCompanyCd("O");
        condition.setRole("User");

        List<Company> companyList = dao.find(condition);

        // 検索条件のRoleが存在しないので、取得できない。
        assertEquals(0, companyList.size());
    }

    /**
     * {@link CompanyDaoImpl#find(SearchCompanyCondition)}のテストケース.
     * ユーザーが「%」を入力した場合は、文字列として扱う。
     * @throws Exception
     */
    @Test
    public void testFindEscape1() throws Exception {
        SearchCompanyCondition condition = new SearchCompanyCondition();
        condition.setPageNo(DEFAULT_PAGE_NO);
        condition.setPageRowNum(DEFAULT_ROW_NUM);
        condition.setCompanyCd("A%");

        List<Company> companyList = dao.find(condition);

        assertEquals(0, companyList.size());
    }

    /**
     * {@link CompanyDaoImpl#find(SearchCompanyCondition)}のテストケース.
     * ユーザーが「%」を入力した場合は、文字列として扱う。
     * @throws Exception
     */
    @Test
    public void testFindEscape2() throws Exception {
        SearchCompanyCondition condition = new SearchCompanyCondition();
        condition.setPageNo(DEFAULT_PAGE_NO);
        condition.setPageRowNum(DEFAULT_ROW_NUM);
        condition.setName("A_");

        List<Company> companyList = dao.find(condition);

        assertEquals(0, companyList.size());
    }

    /**
     * {@link CompanyDaoImpl#count(SearchCompanyCondition)}のテストケース. マスタ管理の場合を検証
     * @throws Exception
     */
    @Test
    public void testCountMasterSearch() throws Exception {
        SearchCompanyCondition condition = new SearchCompanyCondition();
        condition.setCompanyCd("A");
        int companyCount = dao.countCompany(condition);

        // 取得したカウント数を確認
        assertEquals(1, companyCount);

    }

    /**
     * {@link CompanyDaoImpl#count(SearchCompanyCondition)}のテストケース.
     * プロジェクトマスタ管理の場合を検証
     * @throws Exception
     */
    @Test
    public void testCountProjectSearch() throws Exception {
        SearchCompanyCondition condition = new SearchCompanyCondition();
        condition.setProjectId("PJ1");
        condition.setRole("Owner");

        int companyCount = dao.countCompany(condition);

        // 取得したカウント数を確認 削除した会社情報は取得カウントしない
        assertEquals(13, companyCount);
    }

    /**
     * {@link CompanyDaoImpl#findNotAssignTo()}のテストケース.
     * @throws Exception
     */
    @Test
    public void testFindNotAssignTo() throws Exception {
        String projectId = "PJ2";
        List<Company> company = dao.findNotAssignTo(projectId);

        // 取得したオブジェクトと、Excelファイルに定義済の期待値を比較する
        assertNotNull(company);
        assertDataSetEquals(newDataSet("CompanyDaoImplTest_testfindNotAssignTo_expected.xls"),
                            company);
    }

    /**
     * {@link CompanyDaoImpl#findById}のテストケース.
     * @throws Exception
     */
    @Test
    public void testFindById() throws Exception {
        Company c = dao.findById(1L);

        assertNotNull(c);
        assertDataSetEquals(newDataSet("CompanyDaoImplTest_testfindById_expected.xls"), c);
    }

    /**
     * {@link CompanyDaoImpl#create}のテストケース.
     * @throws Exception
     */
    @Test
    public void testCreate() throws Exception {
        // テストに必要なデータを作成
        Company c = new Company();
        User loginUser = new User();
        loginUser.setEmpNo("ZZA01");
        loginUser.setNameE("Yamada Taro");

        c.setCompanyCd("TEST");
        c.setName("Test_Company");
        c.setCreatedBy(loginUser);
        c.setCreatedAt(DBValue.DATE_NULL);
        c.setUpdatedBy(loginUser);
        c.setCreatedAt(DBValue.DATE_NULL);

        // 登録
        Long id = null;
        while (true) {
            try {
                id = dao.create(c);
                break;
            } catch (KeyDuplicateException e) {
                continue;
            }
        }
        assertNotNull(id);

        Company actual = dao.findById(id);

        assertEquals(id, actual.getId());
        assertEquals(c.getCompanyCd(), actual.getCompanyCd());
        assertEquals(c.getName(), actual.getName());
        assertEquals(c.getCreatedBy().toString(), actual.getCreatedBy().toString());
        assertNotNull(actual.getCreatedAt());
        assertEquals(c.getUpdatedBy().toString(), actual.getUpdatedBy().toString());
        assertNotNull(actual.getUpdatedAt());

    }

    /**
     * {@link CompanyDaoImpl#update}のテストケース}
     * @throws Exception
     */
    @Test
    public void testUpdate() throws Exception {
        // テストに必要なデータを作成
        Long companyId = 1L;

        Company c = new Company();
        User loginUser = new User();
        loginUser.setEmpNo("ZZA01");
        loginUser.setNameE("Taro Yamada");

        c.setId(companyId);
        c.setCompanyCd("TEST");
        c.setName("Test Corpo");

        // 更新前データ
        Company beforeActual = dao.findById(companyId);

        c.setUpdatedBy(loginUser);
        c.setVersionNo(beforeActual.getVersionNo());

        Integer updateRecord = dao.update(c);
        assertNotNull(updateRecord);
        assertTrue(updateRecord == 1);

        Company afterActual = dao.findById(companyId);

        assertEquals(beforeActual.getCreatedBy().toString(), afterActual.getCreatedBy().toString());
        assertEquals(beforeActual.getCreatedAt(), afterActual.getCreatedAt());
        assertEquals(beforeActual.getId(), afterActual.getId());

        assertEquals(c.getCompanyCd(), afterActual.getCompanyCd());
        assertEquals(c.getName(), afterActual.getName());
        assertEquals(loginUser.getEmpNo(), afterActual.getUpdatedBy().getEmpNo());
        assertNotNull(afterActual.getUpdatedAt());
        assertEquals(Long.valueOf(beforeActual.getVersionNo() + 1), afterActual.getVersionNo());
    }

    /**
     * {@link CompanyDaoImpl#update}のテストケース}
     * @throws Exception
     */
    @Test(expected = StaleRecordException.class)
    public void testUpdateExclusive() throws Exception {
        // テストに必要なデータを作成
        Long companyId = 1L;

        Company c = new Company();
        User loginUser = new User();
        loginUser.setEmpNo("ZZA01");
        loginUser.setNameE("Taro Yamada");
        c.setId(companyId);
        c.setVersionNo(-1L);
        c.setUpdatedBy(loginUser);

        dao.update(c);
        fail("例外が発生していない");
    }

    /**
     * {@link CompanyDaoImpl#findMembers}のテストケース}
     * @throws Exception
     */
    @Test
    public void testFindMembers() throws Exception {
        // テストに必要なデータを作成する
        List<CompanyUser> actual = dao.findMembers(1L);

        assertDataSetEquals(newDataSet("CompanyDaoImplTest_testFindMembers_expected.xls"),
                            actual);
    }

    /**
     * {@link CompanyDaoImpl#deleteById}のテストケース}
     * @throws Exception
     */
    @Test
    public void testDelete() throws Exception {
        // テストに必要なデータを作成する
        Company company = new Company();
        company.setId(1L);
        User loginUser = new User();
        loginUser.setEmpNo("ZZA02");
        company.setUpdatedBy(loginUser);
        company.setVersionNo(0L);

        // 削除前のデータを取得
        Company beforeCompany = dao.findById(1L);

        Integer record = dao.delete(company);
        assertNotNull(record);
        assertTrue(record == 1);

        // 削除後のデータを取得する
        String sql = "select * from company where id = 1 and delete_no != 0";
        ITable afterActualTable = getConnection().createQueryTable("actual", sql);

        assertEquals(beforeCompany.getId().toString(), afterActualTable.getValue(0, "id")
                                                                       .toString());
        assertEquals(beforeCompany.getCompanyCd(), afterActualTable.getValue(0, "company_cd"));
        assertEquals(beforeCompany.getName(), afterActualTable.getValue(0, "name"));
        assertEquals(beforeCompany.getCreatedBy().getEmpNo(),
                     afterActualTable.getValue(0, "created_by"));
        assertEquals(beforeCompany.getCreatedAt(), afterActualTable.getValue(0, "created_at"));
        assertEquals(loginUser.getEmpNo(), afterActualTable.getValue(0, "updated_by"));
        assertNotNull(afterActualTable.getValue(0, "updated_at"));
        assertEquals(Long.valueOf(beforeCompany.getVersionNo() + 1),
                     Long.valueOf(afterActualTable.getValue(0, "version_no").toString()));
        assertTrue(!afterActualTable.getValue(0, "delete_no").toString().equals("0"));

    }

    /**
     * {@link CompanyDaoImpl#delete}のテストケース} 排他チェックにかかった場合、
     * {@link StaleRecordException}が発生することを検証する.
     * @throws Exception
     */
    @Test(expected = StaleRecordException.class)
    public void testDiffVersionNoDelete() throws Exception {
        // テストに必要なデータを作成する
        Company company = new Company();
        company.setId(1L);
        User loginUser = new User();
        loginUser.setEmpNo("ZZA02");
        company.setUpdatedBy(loginUser);
        company.setVersionNo(-1L);

        // 削除前のデータを取得
        dao.delete(company);
        fail("例外が発生していない");

    }

    /**
     * {@link CompanyDaoImpl#findProjectCompanyById}のテストケース} 排他チェックにかかった場合、
     * @throws Exception
     */
    @Test
    public void testFindProjectCompanyById() throws Exception {
        //テストに必要なデータを作成する
        Company actual = dao.findProjectCompanyById(1L, "PJ1");

        assertNotNull(actual);
        assertDataSetEquals(newDataSet("CompanyDaoImplTest_testFindProjectCompanyById.xls"), actual);
    }

    /**
     * {@link CompanyDaoImpl#countCheck}のテストケース.
     * 条件を全て指定
     * @throws Exception
     */
    @Test
    public void testCountCheckAll() throws Exception {
        SearchCompanyCondition condition = new SearchCompanyCondition();
        condition.setCompanyCd("OT");
        condition.setId(1L);
        int actual = dao.countCheck(condition);
        assertTrue(0 == actual);
    }

    /**
     * {@link CompanyDaoImpl#countCheck}のテストケース.
     * 会社コードを指定
     * @throws Exception
     */
    @Test
    public void testCountCheckCompanyCd() throws Exception {
        SearchCompanyCondition condition = new SearchCompanyCondition();
        condition.setCompanyCd("OT");
        int actual = dao.countCheck(condition);
        assertTrue(1 == actual);
    }

    /**
     * {@link CompanyDaoImpl#countCheck}のテストケース.
     * IDを指定（指定したID以外）
     * @throws Exception
     */
    @Test
    public void testCountCheckId() throws Exception {
        SearchCompanyCondition condition = new SearchCompanyCondition();
        condition.setId(1L);
        int actual = dao.countCheck(condition);
        assertTrue(13 == actual);
    }

}
