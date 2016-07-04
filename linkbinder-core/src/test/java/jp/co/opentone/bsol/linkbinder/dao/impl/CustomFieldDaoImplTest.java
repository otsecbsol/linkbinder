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
import jp.co.opentone.bsol.framework.core.dao.RecordNotFoundException;
import jp.co.opentone.bsol.framework.core.dao.StaleRecordException;
import jp.co.opentone.bsol.framework.test.AbstractDaoTestCase;
import jp.co.opentone.bsol.linkbinder.dto.CustomField;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.code.UseWhole;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCustomFieldCondition;

/**
 * {@link CustomFieldDaoImpl}のテストケース
 * @author opentone
 */
public class CustomFieldDaoImplTest extends AbstractDaoTestCase {

    /**
     * テスト対象.
     */
    @Resource
    private CustomFieldDaoImpl dao;

    private static final int DEFAULT_PAGE_NO = 1;

    private static final int DEFAULT_ROW_NUM = 10;

    /**
     * {@link CustomFieldDaoImpl#findByProjectId} のテストケース
     * @throws Exception
     */
    @Test
    public void testFindByProjectId() throws Exception {
        SearchCustomFieldCondition condition = new SearchCustomFieldCondition();
        condition.setProjectId("PJ1");
        List<CustomField> actual = dao.findByProjectId(condition);

        // 取得したオブジェクトと、Excelファイルに定義済の期待値を比較する
        assertNotNull(actual);
        assertDataSetEquals(newDataSet("CustomFieldDaoImplTest_testFindByProjectId_expected.xls"),
            actual);
    }

    /**
     * {@link CustomFieldDaoImpl#findByIdProjectId}のテストケース
     * @throws Exception
     */
    @Test
    public void testFindByIdProjectId() throws Exception {
        SearchCustomFieldCondition condition = new SearchCustomFieldCondition();
        condition.setId(1L);
        condition.setProjectId("PJ1");
        CustomField actual = dao.findByIdProjectId(condition);

        // 取得したオブジェクトと、Excelファイルに定義済の期待値を比較する
        assertNotNull(actual);
        assertDataSetEquals(newDataSet("CustomFieldDaoImplTest_testFindByIdProjectId_expected.xls"),
            actual);
    }

    /**
     * {@link CustomFieldDaoImpl#findByIdProjectId}のテストケース
     * レコードが取得できない
     * @throws Exception
     */
    @Test(expected = RecordNotFoundException.class)
    public void testFindByIdProjectIdNoData() throws Exception {
        SearchCustomFieldCondition condition = new SearchCustomFieldCondition();
        condition.setId(999L);
        condition.setProjectId("PJ999");
        dao.findByIdProjectId(condition);
    }

    /**
     * {@link CustomFieldDaoImpl#find}のテストケース
     * AdminHome
     * @throws Exception
     */
    @Test
    public void testFindAdmin() throws Exception {
        SearchCustomFieldCondition condition = new SearchCustomFieldCondition();
        condition.setPageNo(DEFAULT_PAGE_NO);
        condition.setPageRowNum(DEFAULT_ROW_NUM);
        List<CustomField> customFields = dao.find(condition);

        // 取得したオブジェクトと、Excelファイルに定義済の期待値を比較する
        assertNotNull(customFields);
        assertDataSetEquals(newDataSet("CustomFieldDaoImplTest_testFindAdmin_expected.xls"),
            customFields);
    }

    /**
     * {@link CustomFieldDaoImpl#find}のテストケース
     * AdminHomeでページ指定をした場合を検証
     * @throws Exception
     */
    @Test
    public void testFindAdminPage() throws Exception {
        SearchCustomFieldCondition condition = new SearchCustomFieldCondition();
        condition.setPageNo(2);
        condition.setPageRowNum(DEFAULT_ROW_NUM);
        List<CustomField> customFields = dao.find(condition);

        // 取得したオブジェクトと、Excelファイルに定義済の期待値を比較する
        assertNotNull(customFields);
        assertDataSetEquals(newDataSet("CustomFieldDaoImplTest_testFindAdminPage_expected.xls"),
            customFields);
    }

    /**
     * {@link CustomFieldDaoImpl#find}のテストケース
     * AdminHome、Label検索
     * @throws Exception
     */
    @Test
    public void testFindLabelAdmin() throws Exception {
        SearchCustomFieldCondition condition = new SearchCustomFieldCondition();
        condition.setPageNo(DEFAULT_PAGE_NO);
        condition.setPageRowNum(DEFAULT_ROW_NUM);
        condition.setLabel("A");
        List<CustomField> customFields = dao.find(condition);

        // 取得したオブジェクトと、Excelファイルに定義済の期待値を比較する
        assertNotNull(customFields);
        assertDataSetEquals(newDataSet("CustomFieldDaoImplTest_testFindLabelAdmin_expected.xls"),
            customFields);
    }

    /**
     * {@link CustomFieldDaoImpl#find}のテストケース
     * データが取得できない
     * @throws Exception
     */
    @Test
    public void testFindNoDataAdmin() throws Exception {
        SearchCustomFieldCondition condition = new SearchCustomFieldCondition();
        condition.setPageNo(DEFAULT_PAGE_NO);
        condition.setPageRowNum(DEFAULT_ROW_NUM);
        condition.setLabel("999");
        List<CustomField> customFields = dao.find(condition);

        assertNotNull(customFields);
        assertTrue(customFields.size() == 0);

    }

    /**
     * {@link CustomFieldDaoImpl#find}のテストケース
     * ProjectAdminHome
     * @throws Exception
     */
    @Test
    public void testFindProject() throws Exception {
        SearchCustomFieldCondition condition = new SearchCustomFieldCondition();
        condition.setPageNo(DEFAULT_PAGE_NO);
        condition.setPageRowNum(DEFAULT_ROW_NUM);
        condition.setProjectId("PJ1");
        List<CustomField> customFields = dao.find(condition);

        // 取得したオブジェクトと、Excelファイルに定義済の期待値を比較する
        assertNotNull(customFields);
        assertDataSetEquals(newDataSet("CustomFieldDaoImplTest_testFindProject_expected.xls"),
            customFields);
    }

    /**
     * {@link CustomFieldDaoImpl#find}のテストケース
     * ProjectAdminHome、Label検索
     * @throws Exception
     */
    @Test
    public void testFindLabelProject() throws Exception {
        SearchCustomFieldCondition condition = new SearchCustomFieldCondition();
        condition.setPageNo(DEFAULT_PAGE_NO);
        condition.setPageRowNum(DEFAULT_ROW_NUM);
        condition.setProjectId("PJ1");
        condition.setLabel("M");
        List<CustomField> customFields = dao.find(condition);

        // 取得したオブジェクトと、Excelファイルに定義済の期待値を比較する
        assertNotNull(customFields);
        assertDataSetEquals(newDataSet("CustomFieldDaoImplTest_testFindLabelProject_expected.xls"),
            customFields);
    }

    /**
     * {@link CustomFieldDaoImpl#find}のテストケース
     * ProjectAdminHome、データが取得できない
     * @throws Exception
     */
    @Test
    public void testFindNoDataProject() throws Exception {
        SearchCustomFieldCondition condition = new SearchCustomFieldCondition();
        condition.setPageNo(DEFAULT_PAGE_NO);
        condition.setPageRowNum(DEFAULT_ROW_NUM);
        condition.setProjectId("PJ1");
        condition.setLabel("999");
        List<CustomField> customFields = dao.find(condition);

        assertNotNull(customFields);
        assertTrue(customFields.size() == 0);
    }

    /**
     * {@link CustomFieldDaoImpl#count}のテストケース
     * AdminHome
     * @throws Exception
     */
    @Test
    public void testCountAdmin() throws Exception {
        SearchCustomFieldCondition condition = new SearchCustomFieldCondition();
        int count = dao.count(condition);
        assertTrue(13 == count);

    }

    /**
     * {@link CustomFieldDaoImpl#count}のテストケース
     * AdminHome、Label検索
     * @throws Exception
     */
    @Test
    public void testCountLabelAdmin() throws Exception {
        SearchCustomFieldCondition condition = new SearchCustomFieldCondition();
        condition.setLabel("A");
        int count = dao.count(condition);

        assertTrue(1 == count);

    }

    /**
     * {@link CustomFieldDaoImpl#count}のテストケース
     * AdminHome、データが取得できない
     * @throws Exception
     */
    @Test
    public void testCountNoDataAdmin() throws Exception {
        SearchCustomFieldCondition condition = new SearchCustomFieldCondition();
        condition.setLabel("999");
        int count = dao.count(condition);

        assertTrue(0 == count);

    }

    /**
     * {@link CustomFieldDaoImpl#count}のテストケース
     * AdminHome
     * @throws Exception
     */
    @Test
    public void testCountProject() throws Exception {
        SearchCustomFieldCondition condition = new SearchCustomFieldCondition();
        condition.setProjectId("PJ1");
        int count = dao.count(condition);

        assertTrue(11 == count);

    }

    /**
     * {@link CustomFieldDaoImpl#count}のテストケース
     * AdminHome、Label検索
     * @throws Exception
     */
    @Test
    public void testCountLabelProject() throws Exception {
        SearchCustomFieldCondition condition = new SearchCustomFieldCondition();
        condition.setProjectId("PJ1");
        condition.setLabel("M");
        int count = dao.count(condition);

        assertTrue(1 == count);

    }

    /**
     * {@link CustomFieldDaoImpl#count}のテストケース
     * AdminHome データが取得できない
     * @throws Exception
     */
    @Test
    public void testCountNoDataProject() throws Exception {
        SearchCustomFieldCondition condition = new SearchCustomFieldCondition();
        condition.setProjectId("PJ1");
        condition.setLabel("999");
        int count = dao.count(condition);

        assertTrue(0 == count);

    }

    /**
     * {@link CustomFieldDaoImpl#count}のテストケース
     * adminHomeフラグが立っている
     * @throws Exception
     */
    @Test
    public void testCountAdminFlagOn() throws Exception {
        SearchCustomFieldCondition condition = new SearchCustomFieldCondition();
        condition.setId(1L);
        condition.setAdminHome(true);
        int count = dao.count(condition);

        assertTrue(count == 1);
    }

    @Test
    public void testCountUseWhole() throws Exception {
        SearchCustomFieldCondition condition = new SearchCustomFieldCondition();
        condition.setUseWhole(UseWhole.ALL);

        int actual = dao.count(condition);
        assertTrue(actual == 12);
    }

    /**
     * {@link CustomFieldDaoImpl#findNotAssignTo}のテストケース
     * @throws Exception
     */
    @Test
    public void testFindNotAssignTo() throws Exception {
        List<CustomField> actual = dao.findNotAssignTo("PJ1");

        // 取得したオブジェクトと、Excelファイルに定義済の期待値を比較する
        assertNotNull(actual);
        assertDataSetEquals(newDataSet("CustomFieldDaoImplTest_testFindNotAssignTo_expected.xls"),
            actual);
    }

    /**
     * {@link CustomFieldDaoImpl#findById}のテストケース.
     * @throws Exception
     */
    @Test
    public void testFindById() throws Exception {
        CustomField actual = dao.findById(11L);

        // 取得したオブジェクトと、Excelファイルに定義済の期待値を比較する
        assertNotNull(actual);
        assertDataSetEquals(newDataSet("CustomFieldDaoImplTest_testFindById_expected.xls"), actual);
    }

    /**
     * {@link CustomFieldDaoImpl#create}のテストケース.
     * @throws Exception
     */
    @Test
    public void testCreate() throws Exception {
        CustomField customField = new CustomField();
        customField.setLabel("TEST");
        customField.setOrderNo(1L);
        customField.setUseWhole(UseWhole.ALL);
        User user = new User();
        user.setEmpNo("ZZA05");
        user.setNameE("Ogiwara Keiichi");

        customField.setCreatedBy(user);
        customField.setUpdatedBy(user);

        Long id = null;

        while (true) {
            try {
                // 登録
                id = dao.create(customField);
                break;
            } catch (KeyDuplicateException kde) {
                continue;
            }
        }
        assertNotNull(id);

        // 登録したデータを取得
        CustomField actual = dao.findById(id);

        assertEquals(customField.getLabel(), actual.getLabel());
        assertEquals(customField.getOrderNo(), actual.getOrderNo());
        assertEquals(customField.getUseWhole(), actual.getUseWhole());
        assertEquals(user.toString(), actual.getCreatedBy().toString());
        assertEquals(user.toString(), actual.getUpdatedBy().toString());
        assertNotNull(actual.getCreatedAt());
        assertNotNull(actual.getUpdatedAt());
        assertEquals(String.valueOf(1L), String.valueOf(actual.getVersionNo()));
        assertEquals(String.valueOf(0L), String.valueOf(actual.getDeleteNo()));

    }

    /**
     * {@link CustomFieldDaoImpl#update}のテストケース
     * @throws Exception
     */
    @Test
    public void testUpdate() throws Exception {
        // 更新前のデータを取得
        Long id = 1L;
        CustomField before = dao.findById(id);

        CustomField customField = new CustomField();
        customField.setId(id);
        customField.setLabel("TEST");
        customField.setOrderNo(2L);
        User user = new User();
        user.setEmpNo("ZZA05");
        user.setNameE("Ogiwara Keiichi");

        customField.setCreatedBy(user);
        customField.setUpdatedBy(user);
        customField.setUseWhole(before.getUseWhole());
        customField.setVersionNo(before.getVersionNo());
        customField.setDeleteNo(before.getDeleteNo());

        // 更新
        Integer record = dao.update(customField);

        // 更新後のデータを取得
        CustomField actual = dao.findById(id);

        assertNotNull(record);
        assertTrue(1 == record);

        assertFalse(before.getLabel().equals(actual.getLabel()));
        assertEquals(customField.getLabel(), actual.getLabel());
        assertFalse(before.getOrderNo().toString().equals(actual.getOrderNo().toString()));
        assertEquals(customField.getOrderNo(), actual.getOrderNo());
        assertEquals(before.getUseWhole().toString(), actual.getUseWhole().toString());
        assertEquals(before.getCreatedBy().toString(), actual.getCreatedBy().toString());
        assertEquals(before.getCreatedAt(), actual.getCreatedAt());
        assertFalse(before.getUpdatedBy().toString().equals(actual.getUpdatedBy().toString()));
        assertEquals(user.toString(), actual.getUpdatedBy().toString());
        assertFalse(before.getUpdatedAt().equals(actual.getUpdatedAt()));
        assertEquals(String.valueOf(before.getVersionNo() + 1), String.valueOf(actual
            .getVersionNo()));
        assertEquals(before.getDeleteNo().toString(), actual.getDeleteNo().toString());
    }

    /**
     * {@link CustomFieldDaoImpl#update}のテストケース
     * StaleRecordExceptionが発生
     * @throws Exception
     */
    @Test(expected = StaleRecordException.class)
    public void testUpdateStaleRecordException() throws Exception {
        // 更新前のデータを取得
        Long id = 1L;
        CustomField before = dao.findById(id);

        CustomField customField = new CustomField();
        customField.setId(id);
        customField.setLabel("TEST");
        customField.setOrderNo(2L);
        User user = new User();
        user.setEmpNo("ZZA05");
        user.setNameE("Keiichi Ogiwara");

        customField.setCreatedBy(user);
        customField.setUpdatedBy(user);
        customField.setUseWhole(before.getUseWhole());
        // 排他チェック
        customField.setVersionNo(before.getVersionNo()+ 1);
        customField.setDeleteNo(before.getDeleteNo());

        // 更新
        dao.update(customField);
    }

    /**
     * {@link CustomFieldDaoImpl#delete}のテストケース
     * @throws Exception
     */
    @Test
    public void testDelete() throws Exception {
        Long id = 1L;
        // 削除前のデータを取得
        CustomField before = dao.findById(id);

        CustomField customField = new CustomField();
        customField.setId(before.getId());
        User user = new User();
        user.setEmpNo("ZZA05");
        user.setNameE("Keiichi Ogiwara");

        customField.setUpdatedBy(user);
        customField.setVersionNo(before.getVersionNo());

        // テスト実行
        dao.delete(customField);

        // 削除後のデータを取得
        String sql = "select * from custom_field where id =" + id +  " and delete_no != 0";
        ITable actual = getConnection().createQueryTable("actual", sql);

        assertEquals(before.getLabel().toString(), actual.getValue(0, "label").toString());
        assertEquals(before.getOrderNo().toString(), actual.getValue(0, "order_no").toString());
        assertEquals(before.getUseWhole().getValue().toString(), actual.getValue(0, "use_whole").toString());
        assertEquals(before.getCreatedBy().getEmpNo().toString(), actual.getValue(0, "created_by").toString());
        assertEquals(before.getCreatedAt(), actual.getValue(0, "created_at"));
        assertFalse(before.getUpdatedBy().getEmpNo().toString().equals(actual.getValue(0, "updated_by").toString()));
        assertEquals(user.getEmpNo().toString(), actual.getValue(0, "updated_by").toString());
        assertFalse(before.getUpdatedAt().equals(actual.getValue(0, "updated_at")));
        assertEquals(String.valueOf(before.getVersionNo() + 1), String.valueOf(actual.getValue(0, "version_no")));
        assertFalse(before.getDeleteNo().toString().equals(actual.getValue(0, "delete_no").toString()));

    }

    /**
     * {@link CustomFieldDaoImpl#delete}のテストケース
     * バージョンナンバーが違う
     * @throws Exception
     */
    @Test(expected = StaleRecordException.class)
    public void testDeleteVersionNoDiff() throws Exception {
        Long id = 1L;
        // 削除前のデータを取得
        CustomField before = dao.findById(id);

        CustomField customField = new CustomField();
        customField.setId(before.getId());
        User user = new User();
        user.setEmpNo("ZZA05");
        user.setNameE("Keiichi Ogiwara");

        customField.setUpdatedBy(user);
        // バージョンナンバーが違う
        customField.setVersionNo(before.getVersionNo() + 1);

        // テスト実行
        dao.delete(customField);
    }

    /**
     * {@link CustomFieldDaoImpl#countCheck}のテストケース.
     * 条件を全て指定
     * @throws Exception
     */
    @Test
    public void testCountCheckAll() throws Exception {
        SearchCustomFieldCondition condition = new SearchCustomFieldCondition();
        condition.setId(1L);
        condition.setLabel("PCWBS");

        int actual = dao.countCheck(condition);
        assertTrue(0 == actual);
    }

    /**
     * {@link CustomFieldDaoImpl#countCheck}のテストケース.
     * IDを指定(指定したID以外)
     * @throws Exception
     */
    @Test
    public void testCountCheckId() throws Exception {
        SearchCustomFieldCondition condition = new SearchCustomFieldCondition();
        condition.setId(1L);
        int actual = dao.countCheck(condition);
        assertTrue(2 == actual);
    }

    /**
     * {@link CustomFieldDaoImpl#countCheck}のテストケース.
     * ラベルを指定
     * @throws Exception
     */
    @Test
    public void testCountCheckLabel() throws Exception {
        SearchCustomFieldCondition condition = new SearchCustomFieldCondition();
        condition.setLabel("LWBS");

        int actual = dao.countCheck(condition);
        assertTrue(1 == actual);
    }
}
