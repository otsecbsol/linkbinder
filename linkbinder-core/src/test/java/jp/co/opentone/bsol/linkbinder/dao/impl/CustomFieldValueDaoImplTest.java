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
import jp.co.opentone.bsol.linkbinder.dto.CustomFieldValue;
import jp.co.opentone.bsol.linkbinder.dto.User;


/**
 * {@link CustomFieldValueDaoImpl}のテストケース
 * @author opentone
 */
public class CustomFieldValueDaoImplTest extends AbstractDaoTestCase{

    /**
     * テスト対象.
     */
    @Resource
    private CustomFieldValueDaoImpl dao;

    /**
     * {@link CustomFieldValueDaoImpl#deleteByCustomFieldId}のテストケース
     * @throws Exception
     */
    @Test
    public void testDeleteByCustomFieldId() throws Exception {
        CustomFieldValue delete = new CustomFieldValue();
        delete.setCustomFieldId(1L);

        User user = new User();
        user.setEmpNo("ZZA05");
        user.setNameE("Keiichi Ogiwara");
        delete.setUpdatedBy(user);

        // 削除前のデータを取得する
        String sql = "select * from custom_field_value where custom_field_id = 1 and delete_no = 0";
        ITable beforeActualTable = getConnection().createQueryTable("before", sql);

        // テスト実行
        Integer record = dao.deleteByCustomFieldId(delete);

        // 削除後のデータを取得する
        String sqlAfter = "select * from custom_field_value where custom_field_id = 1 and delete_no != 0";
        ITable afterActualTable = getConnection().createQueryTable("after", sqlAfter);

        assertEquals(String.valueOf(beforeActualTable.getRowCount()), String.valueOf(record));

        for (int i = 0 ; i < record ; i++) {
            assertEquals(beforeActualTable.getValue(i, "id"), afterActualTable.getValue(i, "id"));
            assertEquals(beforeActualTable.getValue(i, "custom_field_id"), afterActualTable.getValue(i, "custom_field_id"));
            assertEquals(beforeActualTable.getValue(i, "value"), afterActualTable.getValue(i, "value"));
            assertEquals(beforeActualTable.getValue(i, "order_no"), afterActualTable.getValue(i, "order_no"));
            assertEquals(beforeActualTable.getValue(i, "created_by"), afterActualTable.getValue(i, "created_by"));
            assertEquals(beforeActualTable.getValue(i, "created_at"), afterActualTable.getValue(i, "created_at"));
            assertFalse(beforeActualTable.getValue(i, "updated_by").toString().equals(afterActualTable.getValue(i, "updated_by").toString()));
            assertEquals(user.getEmpNo(), afterActualTable.getValue(i, "updated_by").toString());
            assertFalse(beforeActualTable.getValue(i, "updated_at").equals(afterActualTable.getValue(i, "updated_at")));
            assertFalse(beforeActualTable.getValue(i, "delete_no").toString().equals(afterActualTable.getValue(i, "delete_no").toString()));

        }
    }

    /**
     * {@link CustomFieldValueDaoImpl#findByCustomFieldId}のテストケース
     * @throws Exception
     */
    @Test
    public void testFindByCustomFieldId() throws Exception {
        List<CustomFieldValue> actual = dao.findByCustomFieldId(1L);

        // 取得したオブジェクトと、Excelファイルに定義済の期待値を比較する
        assertNotNull(actual);
        assertDataSetEquals(newDataSet("CustomFieldValueDaoImpl_testFindByCustomFieldId_expected.xls"), actual);
    }

    /**
     * {@link CustomFieldValueDaoImpl#create}のテストケース
     * @throws Exception
     */
    @Test
    public void testCreate() throws Exception {
        CustomFieldValue customFieldValue = new CustomFieldValue();
        customFieldValue.setCustomFieldId(1L);
        customFieldValue.setValue("TEST");
        customFieldValue.setOrderNo(1L);
        User user = new User();
        user.setEmpNo("ZZA05");
        user.setNameE("Keiichi Ogiwara");
        customFieldValue.setCreatedBy(user);
        customFieldValue.setUpdatedBy(user);

        Long id = null;
        while(true) {
            try {
                // 登録
                id = dao.create(customFieldValue);
                break;
            } catch (KeyDuplicateException kde) {
                continue;
            }

        }

        assertNotNull(id);
        // 登録したデータを取得
        String sql = "select * from custom_field_value where id = " + id;
        ITable actual = getConnection().createQueryTable("after", sql);

        assertEquals(customFieldValue.getCustomFieldId().toString(), actual.getValue(0, "custom_field_id").toString());
        assertEquals(customFieldValue.getValue(), actual.getValue(0, "value").toString());
        assertEquals(customFieldValue.getOrderNo().toString(), actual.getValue(0, "order_no").toString());
        assertEquals(user.getEmpNo(), actual.getValue(0, "created_by").toString());
        assertNotNull(actual.getValue(0, "created_at"));
        assertEquals(user.getEmpNo(), actual.getValue(0, "updated_by"));
        assertNotNull(actual.getValue(0, "updated_at"));
    }
}
