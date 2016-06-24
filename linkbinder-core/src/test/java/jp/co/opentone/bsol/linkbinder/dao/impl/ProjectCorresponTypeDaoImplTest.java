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
import jp.co.opentone.bsol.linkbinder.dto.ProjectCorresponType;
import jp.co.opentone.bsol.linkbinder.dto.User;


/**
 * {@link ProjectCorresponTypeDaoImpl}を検証する.
 * @author opentone
 */
public class ProjectCorresponTypeDaoImplTest extends AbstractDaoTestCase{

    /**
     * テスト対象.
     */
    @Resource
    private ProjectCorresponTypeDaoImpl dao;

    /**
     * {@link ProjectCorresponTypeDaoImpl#countByCorresponTypeIdProjectId}のテストケース.
     * @throws Exception
     */
    @Test
    public void testCountByCorresponTypeIdProjectId() throws Exception {
        int actual = dao.countByCorresponTypeIdProjectId(1L, "PJ1");

        assertTrue(1 == actual);
    }

    /**
     * {@link ProjectCorresponTypeDaoImpl#countByCorresponTypeIdProjectId}のテストケース.
     * @throws Exception
     */
    @Test
    public void testCountByCorresponTypeIdProjectIdNoData() throws Exception {
        int actual = dao.countByCorresponTypeIdProjectId(1L, "PJ4");

        assertTrue(0 == actual);
    }

    /**
     * {@link ProjectCorresponTypeDaoImpl#countByCorresponTypeIdProjectId}のテストケース.
     * 検索条件がない
     * @throws Exception
     */
    @Test
    public void testCountByCorresponTypeIdProjectIdNoCondition() throws Exception {
        int actual = dao.countByCorresponTypeIdProjectId(null,null);

        assertTrue(34 == actual);
    }

    /**
     * {@link ProjectCorresponTypeDaoImpl#countByCorresponTypeIdProjectId}のテストケース.
     * コレポン文書種別IDのみで検索
     * @throws Exception
     */
    @Test
    public void testCountByCorresponTypeIdProjectIdCorresponTypeId() throws Exception {
        int actual = dao.countByCorresponTypeIdProjectId(1L,null);

        assertTrue(2 == actual);
    }

    /**
     * {@link ProjectCorresponTypeDaoImpl#countByCorresponTypeIdProjectId}のテストケース.
     * プロジェクトIDのみで検索
     * @throws Exception
     */
    @Test
    public void testCountByCorresponTypeIdProjectIdProjectId() throws Exception {
        int actual = dao.countByCorresponTypeIdProjectId(null,"PJ1");

        assertTrue(20 == actual);
    }

    /**
     * 指定したプロジェクトコレポン文書種別を登録する.
     * @throws Exception
     */
    @Test
    public void testCreate() throws Exception {
        ProjectCorresponType pc = new ProjectCorresponType();
        pc.setProjectId("PJ3");
        pc.setCorresponTypeId(2L);

        User loginUser = new User();
        loginUser.setEmpNo("ZZA05");
        loginUser.setNameE("Keiichi Ogiwara");

        pc.setCreatedBy(loginUser);
        pc.setUpdatedBy(loginUser);

        Long id = null;
        while (true) {
            try {
                id = dao.create(pc);
                break;
            } catch (KeyDuplicateException e) {
                continue;
            }
        }

        // 登録したデータを取得
        String sql = "select * from project_correspon_type where id =" + id;
        ITable actual = getConnection().createQueryTable("actual", sql);

        assertEquals(pc.getProjectId(), actual.getValue(0, "project_id"));
        assertEquals(pc.getCorresponTypeId().toString(), actual.getValue(0, "correspon_type_id").toString());
        assertEquals(loginUser.getEmpNo(), actual.getValue(0, "created_by").toString());
        assertNotNull(actual.getValue(0, "created_at"));
        assertEquals(loginUser.getEmpNo(), actual.getValue(0, "updated_by").toString());
        assertNotNull(actual.getValue(0, "updated_at"));
        assertTrue(String.valueOf(0).equals(actual.getValue(0, "delete_no").toString()));

    }

    /**
     * {@link ProjectCorresponTypeDaoImpl#delete}のテストケース
     * @throws Exception
     */
    @Test
    public void testDelete() throws Exception {
        Long id = 1L;
        // 削除前のデータを取得
        String sql = "select * from project_correspon_type where id =" + id;
        ITable before = getConnection().createQueryTable("actual", sql);

        ProjectCorresponType pt = new ProjectCorresponType();
        pt.setId(id);

        User loginUser = new User();
        loginUser.setEmpNo("ZZA05");
        loginUser.setNameE("Keiichi Ogiwara");

        pt.setUpdatedBy(loginUser);

        // テスト実行
        dao.delete(pt);

        // 削除後のデータを取得
        String sqlAfter = "select * from project_correspon_type where id =" + id;
        ITable actual = getConnection().createQueryTable("actual", sqlAfter);

        assertEquals(before.getValue(0, "id"), actual.getValue(0, "id"));
        assertEquals(before.getValue(0, "project_id"), actual.getValue(0, "project_id"));
        assertEquals(before.getValue(0, "correspon_type_id"), actual.getValue(0, "correspon_type_id"));
        assertEquals(before.getValue(0, "created_by"), actual.getValue(0, "created_by"));
        assertEquals(before.getValue(0, "created_at"), actual.getValue(0, "created_at"));
        assertEquals(loginUser.getEmpNo(), actual.getValue(0, "updated_by").toString());
        assertFalse(before.getValue(0, "updated_at").equals(actual.getValue(0, "updated_at")));
        assertFalse(before.getValue(0, "delete_no").equals(actual.getValue(0, "delete_no")));

    }
}
