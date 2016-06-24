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
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroup;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroupUser;
import jp.co.opentone.bsol.linkbinder.dto.User;

/**
 * @author opentone
 */
public class CorresponGroupUserDaoImplTest extends AbstractDaoTestCase {

    /**
     * テスト対象.
     */
    @Resource
    private CorresponGroupUserDaoImpl dao;

    /**
     * {@link jp.co.opentone.bsol.linkbinder.dao.impl.CorresponGroupUserDaoImpl#findByCorresponGroupId(java.lang.Long)}
     * のためのテスト・メソッド。
     */
    @Test
    public void testFindByCorresponGroupId() throws Exception {
        Long corresponGroupId = 1L;

        List<CorresponGroupUser> actual = dao.findByCorresponGroupId(corresponGroupId);

        assertNotNull(actual);
        assertDataSetEquals(newDataSet("CorresponGroupUserDaoImplTest_testFindByCorresponGroupId_expected.xls"),
            actual);
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.dao.impl.CorresponGroupUserDaoImpl#findByCorresponGroupId(java.lang.Long)}
     * のためのテスト・メソッド。 レコード無し。
     */
    @Test
    public void testFindByCorresponGroupIdNoRecord() throws Exception {
        Long corresponGroupId = 99L;

        List<CorresponGroupUser> actual = dao.findByCorresponGroupId(corresponGroupId);

        assertNotNull(actual);
        assertEquals(0, actual.size());
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.dao.impl.CorresponGroupUserDaoImpl#findByEmpNo(java.lang.Long, java.lang.String)}
     * のためのテスト・メソッド。
     */
    @Test
    public void testFindByEmpNo() throws Exception {
        Long corresponGroupId = 1L;
        String empNo = "ZZA04";

        CorresponGroupUser actual = dao.findByEmpNo(corresponGroupId, empNo);

        assertNotNull(actual);
        assertDataSetEquals(newDataSet("CorresponGroupUserDaoImplTest_testFindByEmpNo_expected.xls"),
            actual);
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.dao.impl.CorresponGroupUserDaoImpl#findByEmpNo(java.lang.Long, java.lang.String)}
     * のためのテスト・メソッド。 レコード無し。
     */
    @Test
    public void testFindByEmpNoNoRecord() throws Exception {
        Long corresponGroupId = 1L;
        String empNo = "ZZA99";

        CorresponGroupUser actual = dao.findByEmpNo(corresponGroupId, empNo);

        assertNull(actual);
    }

    /**
     * {@link CorresponGroupUserDaoImpl#deleteByCorresponGroupId}のためのテスト・メソッド.
     * @throws Exception
     */
    @Test
    public void testDeleteByCorresponGroupId() throws Exception {
        User user = new User();
        user.setEmpNo("ZZA04");

        // 削除前のデータを取得する
        String sql =
                "select * from correspon_group_user where correspon_group_id = 1 and delete_no = 0 order by id";
        ITable before = getConnection().createQueryTable("before", sql);

        Integer record = dao.deleteByCorresponGroupId(1L, user);
        assertTrue(5 == record);

        // 削除後のデータを取得
        // 削除後のデータを取得する
        String sqlAfter =
                "select * from correspon_group_user where correspon_group_id = 1 and delete_no != 0 order by id";
        ITable after = getConnection().createQueryTable("after", sqlAfter);

        assertEquals(before.getRowCount(), after.getRowCount());
        assertTrue(record == before.getRowCount());

        assertEquals(before.getValue(0, "id").toString(), after.getValue(0, "id").toString());
        assertEquals(before.getValue(0, "correspon_group_id").toString(), after.getValue(0,
            "correspon_group_id").toString());
        assertEquals(before.getValue(0, "emp_no").toString(), after.getValue(0, "emp_no")
            .toString());
        assertEquals(before.getValue(0, "security_level").toString(), after.getValue(0,
            "security_level").toString());
        assertEquals(before.getValue(0, "created_by").toString(), after.getValue(0, "created_by")
            .toString());
        assertTrue(before.getValue(0, "created_at").toString().equals(after.getValue(0,
            "created_at").toString()));
        assertFalse(before.getValue(0, "updated_by").toString().equals(after.getValue(0,
            "updated_by").toString()));
        assertEquals(user.getEmpNo(), after.getValue(0, "updated_by").toString());
        assertFalse(before.getValue(0, "updated_at").toString().equals(after.getValue(0,
            "updated_at").toString()));
        assertFalse(before.getValue(0, "delete_no").toString().equals(after
            .getValue(0, "delete_no").toString()));

        assertEquals(before.getValue(1, "id").toString(), after.getValue(1, "id").toString());
        assertEquals(before.getValue(1, "correspon_group_id").toString(), after.getValue(1,
            "correspon_group_id").toString());
        assertEquals(before.getValue(1, "emp_no").toString(), after.getValue(1, "emp_no")
            .toString());
        assertEquals(before.getValue(1, "security_level").toString(), after.getValue(1,
            "security_level").toString());
        assertEquals(before.getValue(1, "created_by").toString(), after.getValue(1, "created_by")
            .toString());
        assertTrue(before.getValue(1, "created_at").toString().equals(after.getValue(1,
            "created_at").toString()));
        assertFalse(before.getValue(1, "updated_by").toString().equals(after.getValue(1,
            "updated_by").toString()));
        assertEquals(user.getEmpNo(), after.getValue(1, "updated_by").toString());
        assertFalse(before.getValue(1, "updated_at").toString().equals(after.getValue(1,
            "updated_at").toString()));
        assertFalse(before.getValue(1, "delete_no").toString().equals(after
            .getValue(1, "delete_no").toString()));

        assertEquals(before.getValue(2, "id").toString(), after.getValue(2, "id").toString());
        assertEquals(before.getValue(2, "correspon_group_id").toString(), after.getValue(2,
            "correspon_group_id").toString());
        assertEquals(before.getValue(2, "emp_no").toString(), after.getValue(2, "emp_no")
            .toString());
        assertEquals(before.getValue(2, "security_level").toString(), after.getValue(2,
            "security_level").toString());
        assertEquals(before.getValue(2, "created_by").toString(), after.getValue(2, "created_by")
            .toString());
        assertTrue(before.getValue(2, "created_at").toString().equals(after.getValue(2,
            "created_at").toString()));
        assertFalse(before.getValue(2, "updated_by").toString().equals(after.getValue(2,
            "updated_by").toString()));
        assertEquals(user.getEmpNo(), after.getValue(2, "updated_by").toString());
        assertFalse(before.getValue(2, "updated_at").toString().equals(after.getValue(2,
            "updated_at").toString()));
        assertFalse(before.getValue(2, "delete_no").toString().equals(after
            .getValue(2, "delete_no").toString()));

        assertEquals(before.getValue(3, "id").toString(), after.getValue(3, "id").toString());
        assertEquals(before.getValue(3, "correspon_group_id").toString(), after.getValue(3,
            "correspon_group_id").toString());
        assertEquals(before.getValue(3, "emp_no").toString(), after.getValue(3, "emp_no")
            .toString());
        assertEquals(before.getValue(3, "security_level").toString(), after.getValue(3,
            "security_level").toString());
        assertEquals(before.getValue(3, "created_by").toString(), after.getValue(3, "created_by")
            .toString());
        assertTrue(before.getValue(3, "created_at").toString().equals(after.getValue(3,
            "created_at").toString()));
        assertFalse(before.getValue(3, "updated_by").toString().equals(after.getValue(3,
            "updated_by").toString()));
        assertEquals(user.getEmpNo(), after.getValue(3, "updated_by").toString());
        assertFalse(before.getValue(3, "updated_at").toString().equals(after.getValue(3,
            "updated_at").toString()));
        assertFalse(before.getValue(3, "delete_no").toString().equals(after
            .getValue(3, "delete_no").toString()));

        assertEquals(before.getValue(4, "id").toString(), after.getValue(4, "id").toString());
        assertEquals(before.getValue(4, "correspon_group_id").toString(), after.getValue(4,
            "correspon_group_id").toString());
        assertEquals(before.getValue(4, "emp_no").toString(), after.getValue(4, "emp_no")
            .toString());
        assertEquals(before.getValue(4, "security_level").toString(), after.getValue(4,
            "security_level").toString());
        assertEquals(before.getValue(4, "created_by").toString(), after.getValue(4, "created_by")
            .toString());
        assertTrue(before.getValue(4, "created_at").toString().equals(after.getValue(4,
            "created_at").toString()));
        assertFalse(before.getValue(4, "updated_by").toString().equals(after.getValue(4,
            "updated_by").toString()));
        assertEquals(user.getEmpNo(), after.getValue(4, "updated_by").toString());
        assertFalse(before.getValue(4, "updated_at").toString().equals(after.getValue(4,
            "updated_at").toString()));
        assertFalse(before.getValue(4, "delete_no").toString().equals(after
            .getValue(4, "delete_no").toString()));

    }

    /**
     * {@link CorresponGroupUserDaoImpl#create}のためのテスト・メソッド.
     * @throws Exception
     */
    @Test
    public void testCreate() throws Exception {
        CorresponGroupUser cgu = new CorresponGroupUser();

        Long id = 1L;

        CorresponGroup cg = new CorresponGroup();
        cg.setId(id);

        cgu.setCorresponGroup(cg);

        User user = new User();
        user.setEmpNo("ZZA06");

        cgu.setUser(user);

        cgu.setSecurityLevel("40");

        User loginUser = new User();
        loginUser.setEmpNo("ZZA01");

        cgu.setCreatedBy(loginUser);
        cgu.setUpdatedBy(loginUser);

        // 登録前のユーザーを取得
        String sql =
                "select * from correspon_group_user where correspon_group_id = 1 and delete_no = 0 order by id";
        ITable before = getConnection().createQueryTable("before", sql);

        while (true) {
            try {
                dao.create(cgu);
                break;
            } catch (KeyDuplicateException e) {
                continue;
            }

        }
        // 登録後のユーザーを取得
        String sqlAfter =
                "select * from correspon_group_user where correspon_group_id = 1 and delete_no = 0 order by id";
        ITable after = getConnection().createQueryTable("before", sqlAfter);
        assertEquals(String.valueOf(before.getRowCount() + 1), String.valueOf(after.getRowCount()));

        assertEquals(cgu.getCorresponGroup().getId().toString(), after.getValue(5,
            "correspon_group_id").toString());
        assertEquals(cgu.getUser().getEmpNo(), after.getValue(5, "emp_no").toString());
        assertEquals(cgu.getSecurityLevel().toString(), after.getValue(5, "security_level")
            .toString());
        assertEquals(cgu.getCreatedBy().getEmpNo(), after.getValue(5, "created_by").toString());
        assertNotNull(after.getValue(5, "created_at"));
        assertEquals(cgu.getUpdatedBy().getEmpNo(), after.getValue(5, "updated_by").toString());
        assertNotNull(after.getValue(5, "updated_at"));
        assertEquals(String.valueOf(0L), after.getValue(5, "delete_no").toString());

    }

}
