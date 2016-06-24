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
import jp.co.opentone.bsol.framework.test.AbstractDaoTestCase;
import jp.co.opentone.bsol.linkbinder.dto.Discipline;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchDisciplineCondition;

/**
 * DisciplineDaoImplのテストケース.
 * @author opentone
 */
public class DisciplineDaoImplTest extends AbstractDaoTestCase {

    /**
     * テスト対象.
     */
    @Resource
    private DisciplineDaoImpl dao;

    private static final int DEFAULT_PAGE_NO = 1;

    private static final int DEFAULT_ROW_NUM = 10;

    /**
     * {@link DisciplineDaoImpl#find} のテストケース.
     * @throws Exception
     */
    @Test
    public void testFind() throws Exception {
        // テストに必要なデータを作成
        SearchDisciplineCondition condition = new SearchDisciplineCondition();

        condition.setProjectId("PJ1");
        condition.setPageNo(DEFAULT_PAGE_NO);
        condition.setPageRowNum(DEFAULT_ROW_NUM);

        List<Discipline> actual = dao.find(condition);

        // 取得したオブジェクトと、Excelファイルに定義済の期待値を比較する
        assertNotNull(actual);
        assertDataSetEquals(newDataSet("DisciplineDaoImplTest_testFind_expected.xls"), actual);
    }

    /**
     * {@link DisciplineDaoImpl#find} のテストケース.
     * @throws Exception
     */
    @Test
    public void testFindCode() throws Exception {
        // テストに必要なデータを作成
        SearchDisciplineCondition condition = new SearchDisciplineCondition();

        condition.setProjectId("PJ1");
        // 前方一致検索
        condition.setDisciplineCd("PIP");
        condition.setPageNo(DEFAULT_PAGE_NO);
        condition.setPageRowNum(DEFAULT_ROW_NUM);

        List<Discipline> actual = dao.find(condition);

        // 取得したオブジェクトと、Excelファイルに定義済の期待値を比較する
        assertNotNull(actual);
        assertDataSetEquals(newDataSet("DisciplineDaoImplTest_testFindCode_expected.xls"), actual);
    }

    /**
     * {@link DisciplineDaoImpl#find} のテストケース.
     * @throws Exception
     */
    @Test
    public void testFindName() throws Exception {
        // テストに必要なデータを作成
        SearchDisciplineCondition condition = new SearchDisciplineCondition();

        condition.setProjectId("PJ1");
        // 前方一致検索
        condition.setName("B");
        condition.setPageNo(DEFAULT_PAGE_NO);
        condition.setPageRowNum(DEFAULT_ROW_NUM);

        List<Discipline> actual = dao.find(condition);

        // 取得したオブジェクトと、Excelファイルに定義済の期待値を比較する
        assertNotNull(actual);
        assertDataSetEquals(newDataSet("DisciplineDaoImplTest_testFindName_expected.xls"), actual);
    }

    /**
     * {@link DisciplineDaoImpl#count} のテストケース.
     * @throws Exception
     */
    @Test
    public void testCount() throws Exception {
        // テストに必要なデータを作成
        SearchDisciplineCondition condition = new SearchDisciplineCondition();

        condition.setProjectId("PJ1");
        condition.setPageNo(DEFAULT_PAGE_NO);
        condition.setPageRowNum(DEFAULT_ROW_NUM);

        int count = dao.count(condition);

        assertEquals(3, count);

    }

    /**
     * {@link DisciplineDaoImpl#count} のテストケース.
     * @throws Exception
     */
    @Test
    public void testCountCode() throws Exception {
        // テストに必要なデータを作成
        SearchDisciplineCondition condition = new SearchDisciplineCondition();

        // 前方一致検索
        condition.setDisciplineCd("PIP");
        condition.setProjectId("PJ1");
        condition.setPageNo(DEFAULT_PAGE_NO);
        condition.setPageRowNum(DEFAULT_ROW_NUM);

        int count = dao.count(condition);

        assertEquals(1, count);

    }

    /**
     * {@link DisciplineDaoImpl#count} のテストケース.
     * @throws Exception
     */
    @Test
    public void testCountName() throws Exception {
        // テストに必要なデータを作成
        SearchDisciplineCondition condition = new SearchDisciplineCondition();

        // 前方一致検索
        condition.setName("I");
        condition.setProjectId("PJ1");
        condition.setPageNo(DEFAULT_PAGE_NO);
        condition.setPageRowNum(DEFAULT_ROW_NUM);

        int count = dao.count(condition);

        assertEquals(1, count);

    }

    /**
     * {@link DisciplineDaoImpl#count} のテストケース.
     * @throws Exception
     */
    @Test
    public void testCountNoData() throws Exception {
        // テストに必要なデータを作成
        SearchDisciplineCondition condition = new SearchDisciplineCondition();

        // 存在しない組み合わせ
        condition.setName("I");
        condition.setDisciplineCd("PI");
        condition.setProjectId("PJ1");
        condition.setPageNo(DEFAULT_PAGE_NO);
        condition.setPageRowNum(DEFAULT_ROW_NUM);

        int count = dao.count(condition);

        assertEquals(0, count);
    }

    /**
     * {@link DisciplineDaoImpl#findById}のテストケース
     * @throws Exception
     */
    @Test
    public void testFindById() throws Exception {
        Discipline actual = dao.findById(1L);
        assertDataSetEquals(newDataSet("DisciplineDaoImplTest_testFindById_expected.xls"), actual);
    }

    /**
     * {@link DisciplineDaoImpl#create} のテストケース.
     * @throws Exception
     */
    @Test
    public void testCreate() throws Exception {
        // テストに必要なデータを作成
        Discipline discipline = new Discipline();
        discipline.setDisciplineCd("TEST");
        discipline.setName("Test");
        discipline.setProjectId("PJ1");

        User loginUser = new User();
        loginUser.setEmpNo("ZZA01");
        loginUser.setNameE("Yamada Taro");

        discipline.setCreatedBy(loginUser);
        discipline.setUpdatedBy(loginUser);

        Long id = 0L;
        while (true) {
            try {
                id = dao.create(discipline);
                break;
            } catch (KeyDuplicateException kde) {
                continue;
            }
        }

        Discipline actual = dao.findById(id);

        assertEquals(discipline.getDisciplineCd(), actual.getDisciplineCd());
        assertEquals(discipline.getProjectId(), actual.getProjectId());
        assertEquals(discipline.getName(), actual.getName());
        assertEquals(discipline.getCreatedBy().toString(), actual.getCreatedBy().toString());
        assertEquals(discipline.getUpdatedBy().toString(), actual.getUpdatedBy().toString());
        assertTrue(actual.getVersionNo() == 1L);
        assertTrue(actual.getDeleteNo() == 0);
        assertNotNull(actual.getCreatedAt());
        assertNotNull(actual.getUpdatedAt());

    }

    /**
     * {@link DisciplineDaoImpl#update}のテストケース.
     * @throws Exception
     */
    @Test
    public void testUpdate() throws Exception {
        // テストに必要なデータを作成
        Discipline discipline = new Discipline();
        User user = new User();
        user.setEmpNo("ZZA02");
        user.setNameE("Aoki Tetsuo");

        discipline.setId(1L);
        discipline.setUpdatedBy(user);
        discipline.setVersionNo(0L);
        discipline.setDisciplineCd("TEST");
        discipline.setName("Test Disicipline");

        // 更新前の部門情報を取得する
        Discipline beforeActual = dao.findById(discipline.getId());

        Integer updateRecord = dao.update(discipline);

        assertNotNull(updateRecord);
        assertEquals(String.valueOf(1), String.valueOf(updateRecord));

        // 更新後の部門情報を取得する
        Discipline afterActual = dao.findById(discipline.getId());

        assertEquals(beforeActual.getProjectId(), afterActual.getProjectId());
        assertFalse(beforeActual.getDisciplineCd().equals(afterActual.getDisciplineCd()));
        assertEquals(discipline.getDisciplineCd(), afterActual.getDisciplineCd());
        assertFalse(beforeActual.getName().equals(afterActual.getName()));
        assertEquals(discipline.getName(), afterActual.getName());
        assertEquals(beforeActual.getCreatedBy().toString(), afterActual.getCreatedBy().toString());
        assertEquals(beforeActual.getCreatedAt(), afterActual.getCreatedAt());
        assertFalse(beforeActual.getUpdatedBy().getEmpNo().equals(afterActual.getUpdatedBy()
            .getEmpNo()));
        assertEquals(discipline.getUpdatedBy().toString(), afterActual.getUpdatedBy().toString());
        assertNotNull(afterActual.getUpdatedAt());
        assertTrue(beforeActual.getVersionNo() + 1 == afterActual.getVersionNo());
        assertEquals(beforeActual.getDeleteNo(), afterActual.getDeleteNo());
    }

    /**
     * {@link DisciplineDaoImpl#delete}のテストケース.
     * @throws Exception
     */
    @Test
    public void testDelete() throws Exception {
        // テストに必要なデータを作成
        Discipline delete = new Discipline();
        User loginUser = new User();
        loginUser.setEmpNo("ZZA02");
        loginUser.setNameE("Aoki Tetsuo");

        delete.setId(1L);
        delete.setUpdatedBy(loginUser);

        //削除前のデータを取得
        Discipline before = dao.findById(delete.getId());
        delete.setVersionNo(before.getVersionNo());

        Integer deleteRecord = dao.delete(delete);

        assertNotNull(deleteRecord);
        assertEquals(String.valueOf(1), String.valueOf(deleteRecord));

        // 削除後のデータを取得する
        String sql = "select * from discipline where id = 1 and delete_no != 0";
        ITable after = getConnection().createQueryTable("actual", sql);

        assertEquals(before.getId().toString(), after.getValue(0, "id").toString());
        assertEquals(before.getProjectId(), after.getValue(0, "project_id"));
        assertEquals(before.getDisciplineCd(), after.getValue(0, "discipline_cd"));
        assertEquals(before.getName(), after.getValue(0, "name"));
        assertEquals(before.getCreatedBy().getEmpNo(), after.getValue(0, "created_by"));
        assertEquals(before.getCreatedAt(), after.getValue(0, "created_at"));
        assertEquals(loginUser.getEmpNo(), after.getValue(0, "updated_by"));
        assertFalse(before.getUpdatedAt() == after.getValue(0, "updated_at"));
        assertNotNull(after.getValue(0, "updated_at"));
        assertEquals(Long.valueOf(before.getVersionNo() + 1).toString(), after.getValue(0, "version_no").toString());
        assertFalse(before.getDeleteNo().equals(after.getValue(0, "delete_no")));

    }

    /**
     * {@link DisciplineDaoImpl#findNotExistCorresponGroup}のテストケース
     * @throws Exception
     */
    @Test
    public void testFindNotExistCorresponGroup() throws Exception {
        List<Discipline> actual = dao.findNotExistCorresponGroup("PJ2", 9999L);

        // 取得したオブジェクトと、Excelファイルに定義済の期待値を比較する
        assertNotNull(actual);
        assertDataSetEquals(newDataSet("DisciplineDaoImplTest_testFindNotExistCorresponGroup_expected.xls"),
            actual);

    }

    /**
     * {@link DisciplineDaoImpl#countCheck}のテストケース
     * 条件を全て指定
     * @throws Exception
     */
    @Test
    public void testCountCheckAll() throws Exception {
        SearchDisciplineCondition condition = new SearchDisciplineCondition();
        condition.setId(2L);
        condition.setDisciplineCd("PIPING");
        condition.setProjectId("PJ1");

        int actual = dao.countCheck(condition);

        assertTrue(0 == actual);
    }

    /**
     * {@link DisciplineDaoImpl#countCheck}のテストケース
     * IDを指定(ID以外)
     * @throws Exception
     */
    @Test
    public void testCountCheckId() throws Exception {
        SearchDisciplineCondition condition = new SearchDisciplineCondition();
        condition.setId(2L);

        int actual = dao.countCheck(condition);

        assertTrue(7 == actual);
    }

    /**
     * {@link DisciplineDaoImpl#countCheck}のテストケース
     * 部門コードを指定
     * @throws Exception
     */
    @Test
    public void testCountCheckDisciplineCd() throws Exception {
        SearchDisciplineCondition condition = new SearchDisciplineCondition();
        condition.setDisciplineCd("PIPING");

        int actual = dao.countCheck(condition);

        assertTrue(2 == actual);
    }

    /**
     * {@link DisciplineDaoImpl#countCheck}のテストケース
     * プロジェクトIDを指定
     * @throws Exception
     */
    @Test
    public void testCountCheckProjectId() throws Exception {
        SearchDisciplineCondition condition = new SearchDisciplineCondition();
        condition.setProjectId("PJ1");

        int actual = dao.countCheck(condition);

        assertTrue(3 == actual);
    }

}
