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

import javax.annotation.Resource;

import org.dbunit.dataset.ITable;
import org.junit.Test;

import jp.co.opentone.bsol.framework.core.dao.KeyDuplicateException;
import jp.co.opentone.bsol.framework.core.extension.ibatis.DBValue;
import jp.co.opentone.bsol.framework.test.AbstractDaoTestCase;
import jp.co.opentone.bsol.linkbinder.dto.ProjectCompany;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCompanyCondition;

/**
 * ProjectCompanyDaoImplのテストケース
 * @author opentone
 */
public class ProjectCompanyDaoImplTest extends AbstractDaoTestCase {

    /**
     * テスト対象.
     */
    @Resource
    private ProjectCompanyDaoImpl dao;

    /**
     * {@link ProjectCompanyDaoImpl#create}のテストケース.
     * @throws Exception
     */
    @Test
    public void testCreate() throws Exception {
        // テストに必要なデータを作成
        ProjectCompany pc = new ProjectCompany();
        User loginUser = new User();
        loginUser.setEmpNo("ZZA01");

        pc.setProjectId("PJ2");
        pc.setCompanyId(3L);
        pc.setCreatedBy(loginUser);
        pc.setCreatedAt(DBValue.DATE_NULL);
        pc.setUpdatedBy(loginUser);
        pc.setCreatedAt(DBValue.DATE_NULL);
        Long id = null;

        // id（自動採番）を最大値にするためのループ
        while (true) {
            try {
                // 登録
                id = dao.create(pc);
                break;
            } catch (KeyDuplicateException e) {
                continue;
            }
        }

        assertNotNull(id);

        ProjectCompany actual = dao.findById(id);

        assertEquals(id, actual.getId());
        assertEquals(pc.getProjectId(), actual.getProjectId());
        assertEquals(pc.getCompanyId(), actual.getCompanyId());
        assertNull(actual.getRole());
        assertEquals(pc.getCreatedBy().toString(), actual.getCreatedBy().toString());
        assertNotNull(actual.getCreatedAt());
        assertEquals(pc.getUpdatedBy().toString(), actual.getUpdatedBy().toString());
        assertNotNull(actual.getUpdatedAt());
    }

    /**
     * {@link ProjectCompanyDaoImpl#findById}のテストケース.
     * @throws Exception
     */
    @Test
    public void testFindById() throws Exception {
        ProjectCompany pc = dao.findById(3L);

        assertNotNull(pc);
        assertDataSetEquals(newDataSet("ProjectCompanyDaoImplTest_testFindById_expected.xls"), pc);
    }

    /**
     * {@link ProjectCompanyDaoImpl#update}のテストケース
     * @throws Exception
     */
    @Test
    public void testUpdate() throws Exception {
        // テストに必要なデータを作成
        Long updateId = 1L;

        ProjectCompany pc = new ProjectCompany();
        User loginUser = new User();
        loginUser.setEmpNo("ZZA02");
        loginUser.setNameE("Tetsuo Aoki");

        pc.setUpdatedBy(loginUser);
        pc.setRole("Owner");

        // 更新前のデータを取得
        ProjectCompany beforeActual = dao.findById(updateId);
        pc.setId(beforeActual.getId());

        Integer updateRerord = dao.update(pc);

        assertNotNull(updateRerord);
        assertTrue(updateRerord == 1);

        // 更新後データ取得
        ProjectCompany afterActual = dao.findById(updateId);

        assertEquals(beforeActual.getProjectId(), afterActual.getProjectId());
        assertEquals(beforeActual.getCompanyId(), afterActual.getCompanyId());
        assertEquals(pc.getRole(), afterActual.getRole());
        assertEquals(beforeActual.getCreatedBy().toString(), afterActual.getCreatedBy().toString());
        assertEquals(beforeActual.getCreatedAt(), afterActual.getCreatedAt());
        assertEquals(loginUser.getEmpNo(), afterActual.getUpdatedBy().getEmpNo());
        assertNotNull(afterActual.getUpdatedAt());
        assertEquals(beforeActual.getDeleteNo(), afterActual.getDeleteNo());

    }

    /**
     * {@link ProjectCompanyDaoImpl#deleteById}のテストケース}
     * @throws Exception
     */
    @Test
    public void testDelete() throws Exception {
        // テストに必要なデータを作成する
        ProjectCompany projectCompany = new ProjectCompany();
        projectCompany.setId(1L);
        User loginUser = new User();
        loginUser.setEmpNo("ZZA02");
        projectCompany.setUpdatedBy(loginUser);

        // 削除前のデータを取得
        ProjectCompany beforeCompany = dao.findById(1L);

        Integer record = dao.delete(projectCompany);
        assertNotNull(record);
        assertTrue(record == 1);

        // 削除後のデータを取得する
        String sql = "select * from project_company where id = 1 and delete_no != 0";
        ITable afterActualTable = getConnection().createQueryTable("actual", sql);

        assertEquals(beforeCompany.getId().toString(), afterActualTable.getValue(0, "id")
                                                                       .toString());
        assertEquals(beforeCompany.getProjectId(), afterActualTable.getValue(0, "project_id"));
        assertEquals(beforeCompany.getCompanyId().toString(),
                     afterActualTable.getValue(0, "company_id").toString());
        assertEquals(beforeCompany.getRole(), afterActualTable.getValue(0, "role"));
        assertEquals(beforeCompany.getCreatedBy().getEmpNo(),
                     afterActualTable.getValue(0, "created_by"));
        assertEquals(beforeCompany.getCreatedAt(), afterActualTable.getValue(0, "created_at"));
        assertEquals(loginUser.getEmpNo(), afterActualTable.getValue(0, "updated_by"));
        assertNotNull(afterActualTable.getValue(0, "updated_at"));
        assertTrue(!afterActualTable.getValue(0, "delete_no").toString().equals("0"));

    }

    /**
     * {@link ProjectCompanyDaoImpl#countCheck}のテストケース
     * @throws Exception
     */
    @Test
    public void testCountCheck() throws Exception {
        SearchCompanyCondition condition = new SearchCompanyCondition();
        condition.setCompanyId(1L);
        int actual = dao.countCheck(condition);

        assertTrue(actual == 2);
    }
}
