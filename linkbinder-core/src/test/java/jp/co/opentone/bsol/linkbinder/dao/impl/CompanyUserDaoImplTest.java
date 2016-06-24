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
import jp.co.opentone.bsol.framework.test.AbstractDaoTestCase;
import jp.co.opentone.bsol.linkbinder.dto.CompanyUser;
import jp.co.opentone.bsol.linkbinder.dto.User;

/**
 * @author opentone
 */
public class CompanyUserDaoImplTest extends AbstractDaoTestCase {

    /**
     * テスト対象.
     */
    @Resource
    private CompanyUserDaoImpl dao;

    /**
     * {@link CompanyUserDaoImpl#deleteByCompanyId}のテストケース
     * @throws Exception
     */
    @Test
    public void testDeleteByCompanyId() throws Exception {
        // テストに必要なデータを作成
        CompanyUser cu = new CompanyUser();
        cu.setProjectCompanyId(1L);
        User loginUser = new User();
        loginUser.setEmpNo("ZZA02");
        loginUser.setNameE("Tetsuo Aoki");
        cu.setUpdatedBy(loginUser);

        // 削除前のデータを取得する
        String beforeUpdateSql = "select * from company_user where project_company_id = 1";
        ITable beforeActualTable = getConnection().createQueryTable("actual", beforeUpdateSql);

        Integer deleteRecord = dao.deleteByCompanyId(cu);

        assertNotNull(deleteRecord);
        assertTrue(deleteRecord == 10);

        // 削除後のデータを取得する
        String sql = "select * from company_user where project_company_id = 1";
        ITable afterActualTable = getConnection().createQueryTable("actual", sql);

        assertEquals(beforeActualTable.getRowCount(), afterActualTable.getRowCount());

        for (int i = 0; i < deleteRecord; i++) {
            assertEquals(beforeActualTable.getValue(i, "project_company_id"),
                         afterActualTable.getValue(i, "project_company_id"));
            assertEquals(beforeActualTable.getValue(i, "emp_no"),
                         afterActualTable.getValue(i, "emp_no"));
            assertEquals(beforeActualTable.getValue(i, "created_by"),
                         afterActualTable.getValue(i, "created_by"));
            assertEquals(beforeActualTable.getValue(i, "created_at"),
                         afterActualTable.getValue(i, "created_at"));
            assertFalse(beforeActualTable.getValue(i, "updated_by")
                                         .equals(afterActualTable.getValue(i, "updated_by")));
            assertFalse(beforeActualTable.getValue(i, "updated_at")
                                         .equals(afterActualTable.getValue(i, "updated_at")));
            assertFalse(afterActualTable.getValue(i, "delete_no").equals("0"));
        }
    }

    /**
     * {@link CompanyUserDaoImpl#create}のテストケース
     * @throws Exception
     */
    @Test
    public void testCreate() throws Exception {
        // テストに必要なデータを作成
        User loginUser = new User();
        loginUser.setEmpNo("ZZA02");
        loginUser.setNameE("Tetsuo Aoki");

        User user = new User();
        user.setEmpNo("ZZA03");
        user.setNameE("Atsushi Ishda");

        CompanyUser cu = new CompanyUser();
        cu.setProjectCompanyId(2L);
        cu.setProjectId("PJ2");
        cu.setUser(user);
        cu.setCreatedBy(loginUser);
        cu.setUpdatedBy(loginUser);

        // id（自動採番）を最大値にするためのループ
        Long id = null;
        while (true) {
            try {
                id = dao.create(cu);
                break;
            } catch (KeyDuplicateException e) {
                continue;
            }
        }

        assertNotNull(id);

        String sql = "select * from company_user where id = " + id;
        ITable actualTable = getConnection().createQueryTable("actual", sql);

        assertNotNull(actualTable.getValue(0, "id"));
        assertEquals(cu.getProjectCompanyId().toString(),
                     actualTable.getValue(0, "project_company_id").toString());
        assertEquals(cu.getProjectId(), actualTable.getValue(0, "project_id"));
        assertEquals(cu.getUser().getEmpNo(), actualTable.getValue(0, "emp_no"));
        assertEquals(loginUser.getEmpNo(), actualTable.getValue(0, "created_by"));
        assertNotNull(actualTable.getValue(0, "created_at"));
        assertEquals(loginUser.getEmpNo(), actualTable.getValue(0, "updated_by"));
        assertNotNull(actualTable.getValue(0, "updated_at"));
        assertEquals(Long.valueOf(0), Long.valueOf(actualTable.getValue(0, "delete_no").toString()));

    }
}
