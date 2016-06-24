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
import jp.co.opentone.bsol.framework.core.dao.RecordNotFoundException;
import jp.co.opentone.bsol.framework.test.AbstractDaoTestCase;
import jp.co.opentone.bsol.linkbinder.dto.ProjectCustomField;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCustomFieldCondition;

/**
 * {@link ProjectCustomFieldDaoImpl}のテストケース
 * @author opentone
 */
public class ProjectCustomFieldDaoImplTest extends AbstractDaoTestCase {

    /**
     * テスト対象.
     */
    @Resource
    private ProjectCustomFieldDaoImpl dao;

    /**
     * {@link ProjectCustomFieldDaoImpl#create}のテストケース
     * @throws Exception
     */
    @Test
    public void testCreate() throws Exception {
        ProjectCustomField projectCustomField = new ProjectCustomField();
        projectCustomField.setProjectId("PJ2");
        projectCustomField.setCustomFieldId(1L);
        projectCustomField.setLabel("TEST");
        projectCustomField.setOrderNo(1L);
        User user = new User();
        user.setEmpNo("ZZA01");
        user.setNameE("Yamada Taro");
        projectCustomField.setCreatedBy(user);
        projectCustomField.setUpdatedBy(user);

        Long id = null;

        while (true) {
            try {
                // 登録
                id = dao.create(projectCustomField);
                break;
            } catch (KeyDuplicateException kde) {
                continue;
            }

        }

        // 登録したデータを取得する
        String sql = "select * from project_custom_field where id =" + id;
        ITable actual = getConnection().createQueryTable("actual", sql);

        assertEquals(projectCustomField.getProjectId(), actual.getValue(0, "project_id"));
        assertEquals(projectCustomField.getCustomFieldId().toString(), actual.getValue(0,
            "custom_field_id").toString());
        assertEquals(projectCustomField.getLabel(), actual.getValue(0, "label"));
        assertEquals(projectCustomField.getOrderNo().toString(), actual.getValue(0, "order_no")
            .toString());
        assertEquals(projectCustomField.getCreatedBy().getEmpNo(), actual.getValue(0, "created_by"));
        assertNotNull(actual.getValue(0, "created_at"));
        assertEquals(projectCustomField.getUpdatedBy().getEmpNo(), actual.getValue(0, "updated_by"));
        assertNotNull(actual.getValue(0, "updated_at"));
        assertEquals(String.valueOf(0L), actual.getValue(0, "delete_no").toString());
    }

    /**
     * {@link ProjectCustomFieldDaoImpl#update}のテストケース
     * @throws Exception
     */
    @Test
    public void testUpdate() throws Exception {
        Long id = 1L;
        // 更新前のデータを取得する
        String sql = "select * from project_custom_field where id =" + id;
        ITable before = getConnection().createQueryTable("before", sql);

        ProjectCustomField projectCustomField = new ProjectCustomField();
        projectCustomField.setId(id);
        projectCustomField.setProjectId(before.getValue(0, "project_id").toString());
        projectCustomField.setCustomFieldId(Long.valueOf(before.getValue(0, "custom_field_id")
            .toString()));
        projectCustomField.setLabel("TEST");
        projectCustomField.setOrderNo(1L);

        User user = new User();
        user.setEmpNo("ZZA05");
        user.setNameE("Ogiwara Keiichi");
        projectCustomField.setCreatedBy(user);
        projectCustomField.setUpdatedBy(user);
        projectCustomField.setDeleteNo(0L);

        // 更新
        Integer record = dao.update(projectCustomField);

        // 更新後のデータを取得する
        String sqlAfter = "select * from project_custom_field where id =" + id;
        ITable after = getConnection().createQueryTable("after", sqlAfter);

        assertTrue(1 == record);

        assertEquals(before.getValue(0, "project_id").toString(), after.getValue(0, "project_id")
            .toString());
        assertEquals(before.getValue(0, "custom_field_id").toString(), after.getValue(0,
            "custom_field_id").toString());
        assertFalse(before.getValue(0, "label").toString().equals(after.getValue(0, "label")
            .toString()));
        assertEquals(projectCustomField.getLabel(), after.getValue(0, "label").toString());
        assertFalse(before.getValue(0, "order_no").toString().equals(after.getValue(0, "order_no")
            .toString()));
        assertEquals(projectCustomField.getOrderNo().toString(), after.getValue(0, "order_no")
            .toString());
        assertEquals(before.getValue(0, "created_by").toString(), after.getValue(0, "created_by")
            .toString());
        assertEquals(before.getValue(0, "created_at").toString(), after.getValue(0, "created_at")
            .toString());
        assertFalse(before.getValue(0, "updated_by").toString().equals(after.getValue(0,
            "updated_by").toString()));
        assertEquals(user.getEmpNo(), after.getValue(0, "updated_by").toString());
        assertFalse(before.getValue(0, "updated_at").toString().equals(after.getValue(0,
            "updated_at").toString()));
        assertEquals(before.getValue(0, "delete_no").toString(), after.getValue(0, "delete_no")
            .toString());

    }

    /**
     * {@link ProjectCustomFieldDaoImpl#update}のテストケース
     * @throws Exception
     */
    @Test
    public void testUpdateNoLabelNoOrderNo() throws Exception {
        Long id = 1L;
        // 更新前のデータを取得する
        String sql = "select * from project_custom_field where id =" + id;
        ITable before = getConnection().createQueryTable("before", sql);

        ProjectCustomField projectCustomField = new ProjectCustomField();
        projectCustomField.setId(id);
        projectCustomField.setProjectId(before.getValue(0, "project_id").toString());
        projectCustomField.setCustomFieldId(Long.valueOf(before.getValue(0, "custom_field_id")
            .toString()));

        User user = new User();
        user.setEmpNo("ZZA05");
        user.setNameE("Keiichi Ogiwara");
        projectCustomField.setCreatedBy(user);
        projectCustomField.setUpdatedBy(user);
        projectCustomField.setDeleteNo(0L);

        // 更新
        Integer record = dao.update(projectCustomField);

        // 更新後のデータを取得する
        String sqlAfter = "select * from project_custom_field where id =" + id;
        ITable after = getConnection().createQueryTable("after", sqlAfter);

        assertTrue(1 == record);

        assertEquals(before.getValue(0, "project_id").toString(), after.getValue(0, "project_id")
            .toString());
        assertEquals(before.getValue(0, "custom_field_id").toString(), after.getValue(0,
            "custom_field_id").toString());
        assertEquals(before.getValue(0, "label").toString(), after.getValue(0, "label").toString());
        assertEquals(before.getValue(0, "order_no").toString(), after.getValue(0, "order_no")
            .toString());
        assertEquals(before.getValue(0, "created_by").toString(), after.getValue(0, "created_by")
            .toString());
        assertEquals(before.getValue(0, "created_at").toString(), after.getValue(0, "created_at")
            .toString());
        assertFalse(before.getValue(0, "updated_by").toString().equals(after.getValue(0,
            "updated_by").toString()));
        assertEquals(user.getEmpNo(), after.getValue(0, "updated_by").toString());
        assertFalse(before.getValue(0, "updated_at").toString().equals(after.getValue(0,
            "updated_at").toString()));
        assertEquals(before.getValue(0, "delete_no").toString(), after.getValue(0, "delete_no")
            .toString());

    }

    /**
     * {@link ProjectCustomFieldDaoImpl#findById}のテストケース
     * @throws Exception
     */
    @Test
    public void testFindById() throws Exception {
        ProjectCustomField actual = dao.findById(1L);

        // 取得したオブジェクトと、Excelファイルに定義済の期待値を比較する
        assertNotNull(actual);
        assertDataSetEquals(newDataSet("ProjectCustomFieldDaoImplTest_testFindById_expected.xls"), actual);

    }

    /**
     * {@link ProjectCustomFieldDaoImpl#findById}のテストケース
     * @throws Exception
     */
    @Test(expected = RecordNotFoundException.class)
    public void testFindByIdNoData() throws Exception {
        dao.findById(0L);
    }

    /**
     * {@link ProjectCustomFieldDaoImpl#delete}のテストケース
     * @throws Exception
     */
    @Test
    public void testDelete() throws Exception {
        Long id = 1L;
        // 削除前のデータを取得
        ProjectCustomField before = dao.findById(id);

        ProjectCustomField projectCustomField = new ProjectCustomField();
        projectCustomField.setId(id);
        User loginUser = new User();
        loginUser.setEmpNo("ZZA05");
        loginUser.setNameE("Keiichi Ogiwara");
        projectCustomField.setUpdatedBy(loginUser);

        // 削除実行
        dao.delete(projectCustomField);

        // 削除後のデータを取得
        String sql = "select * from project_custom_field where id =" + id +  " and delete_no != 0";
        ITable actual = getConnection().createQueryTable("actual", sql);

        assertEquals(before.getProjectId(), actual.getValue(0, "project_id").toString());
        assertEquals(before.getCustomFieldId().toString(), actual.getValue(0, "custom_field_id").toString());
        assertEquals(before.getLabel(), actual.getValue(0, "label").toString());
        assertEquals(before.getOrderNo().toString(), actual.getValue(0, "order_no").toString());
        assertEquals(before.getCreatedBy().getEmpNo(), actual.getValue(0, "created_by").toString());
        assertEquals(before.getCreatedAt(), actual.getValue(0, "created_at"));
        assertFalse(before.getUpdatedBy().getEmpNo().equals(actual.getValue(0, "updated_by").toString()));
        assertEquals(loginUser.getEmpNo(), actual.getValue(0, "updated_by").toString());
        assertFalse(before.getUpdatedAt().equals(actual.getValue(0, "updated_at")));

    }

    /**
     * {@link ProjectCustomFieldDaoImpl#countCheck}のテストケース.
     * 条件を全て指定
     * @throws Exception
     */
    @Test
    public void testCountCheckAll() throws Exception {
        SearchCustomFieldCondition condition = new SearchCustomFieldCondition();
        condition.setProjectCustomFieldId(1L);
        condition.setLabel("PCWBS");
        condition.setProjectId("PJ1");

        int actual = dao.countCheck(condition);
        assertTrue(0 == actual);
    }

    /**
     * {@link ProjectCustomFieldDaoImpl#countCheck}のテストケース.
     * IDを指定（指定したID以外）
     * @throws Exception
     */
    @Test
    public void testCountCheckId() throws Exception {
        SearchCustomFieldCondition condition = new SearchCustomFieldCondition();
        condition.setProjectCustomFieldId(1L);

        int actual = dao.countCheck(condition);
        assertTrue(11 == actual);
    }

    /**
     * {@link ProjectCustomFieldDaoImpl#countCheck}のテストケース.
     * ラベルを指定
     * @throws Exception
     */
    @Test
    public void testCountCheckLabel() throws Exception {
        SearchCustomFieldCondition condition = new SearchCustomFieldCondition();
        condition.setLabel("PCWBS");

        int actual = dao.countCheck(condition);
        assertTrue(1 == actual);
    }

    /**
     * {@link ProjectCustomFieldDaoImpl#countCheck}のテストケース.
     * プロジェクトIDを指定
     * @throws Exception
     */
    @Test
    public void testCountCheckProjectId() throws Exception {
        SearchCustomFieldCondition condition = new SearchCustomFieldCondition();
        condition.setProjectId("PJ1");

        int actual = dao.countCheck(condition);
        assertTrue(11 == actual);
    }

    /**
     * {@link ProjectCustomFieldDaoImpl#findIdByProjectIdNo}のテストケース.
     * @throws Exception
     */
    @Test
    public void testFindIdByProjectIdNo() throws Exception {
        SearchCustomFieldCondition condition = new SearchCustomFieldCondition();
        condition.setProjectId("PJ2");
        condition.setNo(1L);

        Long actual = dao.findIdByProjectIdNo(condition);
        org.junit.Assert.assertEquals(2L, actual.longValue());
    }

    /**
     * {@link ProjectCustomFieldDaoImpl#findProjectCustomFieldIdByProjectIdNo}のテストケース.
     * @throws Exception
     */
    @Test
    public void testFindProjectCustomFieldIdByProjectIdNo() throws Exception {
        SearchCustomFieldCondition condition = new SearchCustomFieldCondition();
        condition.setProjectId("PJ2");
        condition.setNo(1L);

        Long actual = dao.findProjectCustomFieldIdByProjectIdNo(condition);
        org.junit.Assert.assertEquals(3L, actual.longValue());
    }

}
