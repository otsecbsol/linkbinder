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
import jp.co.opentone.bsol.framework.test.AbstractDaoTestCase;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.WorkflowTemplateUser;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchWorkflowTemplateUserCondition;


/**
 * {@link WorkflowTemplateUserDaoImpl}のテストケース.
 * @author opentone
 */
public class WorkflowTemplateUserDaoImplTest extends AbstractDaoTestCase {

    /**
     * テスト対象.
     */
    @Resource
    private WorkflowTemplateUserDaoImpl dao;

    /**
     * {@link WorkflowTemplateUserDaoImpl#find}のテストケース.
     * 条件を全て指定する
     * @throws Exception
     */
    @Test
    public void testFindAll() throws Exception {
        SearchWorkflowTemplateUserCondition condition = new SearchWorkflowTemplateUserCondition();
        condition.setProjectId("PJ1");
        User user = new User();
        user.setEmpNo("ZZA05");
        user.setNameE("Keiichi Ogiwara");
        condition.setUser(user);
        // テスト実行
        List<WorkflowTemplateUser> actual = dao.find(condition);

        // 取得したオブジェクトと、Excelファイルに定義済の期待値を比較する
        assertNotNull(actual);
        assertDataSetEquals(newDataSet("WorkflowTemplateUserDaoImplTest_testFindAll_expected.xls"),
            actual);
    }

    /**
     * {@link WorkflowTemplateUserDaoImpl#find}のテストケース.
     * プロジェクトIDを指定する
     * @throws Exception
     */
    @Test
    public void testFindProjectId() throws Exception {
        SearchWorkflowTemplateUserCondition condition = new SearchWorkflowTemplateUserCondition();
        condition.setProjectId("PJ1");
        // テスト実行
        List<WorkflowTemplateUser> actual = dao.find(condition);

        // 取得したオブジェクトと、Excelファイルに定義済の期待値を比較する
        assertNotNull(actual);
        assertDataSetEquals(newDataSet("WorkflowTemplateUserDaoImplTest_testFindProjectId_expected.xls"),
            actual);
    }

    /**
     * {@link WorkflowTemplateUserDaoImpl#find}のテストケース.
     * ユーザー番号を指定する
     * @throws Exception
     */
    @Test
    public void testFindEmpNo() throws Exception {
        SearchWorkflowTemplateUserCondition condition = new SearchWorkflowTemplateUserCondition();
        User user = new User();
        user.setEmpNo("ZZA05");
        user.setNameE("Keiichi Ogiwara");
        condition.setUser(user);
        // テスト実行
        List<WorkflowTemplateUser> actual = dao.find(condition);

        // 取得したオブジェクトと、Excelファイルに定義済の期待値を比較する
        assertNotNull(actual);
        assertDataSetEquals(newDataSet("WorkflowTemplateUserDaoImplTest_testFindEmpNo_expected.xls"),
            actual);
    }

    /**
     * {@link WorkflowTemplateUserDaoImpl#find}のテストケース.
     * データが取得できない
     * @throws Exception
     */
    @Test
    public void testFindNoData() throws Exception {
        SearchWorkflowTemplateUserCondition condition = new SearchWorkflowTemplateUserCondition();
        condition.setProjectId("PJ3");
        // テスト実行
        List<WorkflowTemplateUser> actual = dao.find(condition);
        assertNotNull(actual);
        assertTrue(0 == actual.size());
    }

    /**
     * {@link WorkflowTemplateUserDaoImpl#countTemplateUserCheck}のテストケース.
     * 条件を全て指定する
     * @throws Exception
     */
    @Test
    public void testCountTemplateUserCheckAll() throws Exception {
        SearchWorkflowTemplateUserCondition condition = new SearchWorkflowTemplateUserCondition();
        condition.setProjectId("PJ1");

        User user = new User();
        user.setEmpNo("ZZA05");
        user.setNameE("Keiichi Ogiwara");
        condition.setUser(user);
        condition.setName("Test Template1");
        // テスト実行
        int actual = dao.countTemplateUserCheck(condition);

        assertTrue(1 == actual);
    }

    /**
     * {@link WorkflowTemplateUserDaoImpl#countTemplateUserCheck}のテストケース.
     * プロジェクトIDを指定する
     * @throws Exception
     */
    @Test
    public void testCountTemplateUserCheckProjectId() throws Exception {
        SearchWorkflowTemplateUserCondition condition = new SearchWorkflowTemplateUserCondition();
        condition.setProjectId("PJ1");

        // テスト実行
        int actual = dao.countTemplateUserCheck(condition);

        assertTrue(2 == actual);
    }

    /**
     * {@link WorkflowTemplateUserDaoImpl#countTemplateUserCheck}のテストケース.
     * ユーザー番号を指定する
     * @throws Exception
     */
    @Test
    public void testCountTemplateUserCheckEmpNo() throws Exception {
        SearchWorkflowTemplateUserCondition condition = new SearchWorkflowTemplateUserCondition();

        User user = new User();
        user.setEmpNo("ZZA05");
        user.setNameE("Keiichi Ogiwara");
        condition.setUser(user);
        // テスト実行
        int actual = dao.countTemplateUserCheck(condition);

        assertTrue(1 == actual);
    }

    /**
     * {@link WorkflowTemplateUserDaoImpl#countTemplateUserCheck}のテストケース.
     * テンプレート名を指定する
     * @throws Exception
     */
    @Test
    public void testCountTemplateUserCheckName() throws Exception {
        SearchWorkflowTemplateUserCondition condition = new SearchWorkflowTemplateUserCondition();
        condition.setName("Test Template1");
        // テスト実行
        int actual = dao.countTemplateUserCheck(condition);

        assertTrue(1 == actual);
    }

    /**
     * {@link WorkflowTemplateUserDaoImpl#findById}のテストケース.
     * @throws Exception
     */
    @Test
    public void testFindById() throws Exception {
        // テスト実行
        WorkflowTemplateUser actual = dao.findById(2L);

        // 取得したオブジェクトと、Excelファイルに定義済の期待値を比較する
        assertNotNull(actual);
        assertDataSetEquals(newDataSet("WorkflowTemplateUserDaoImplTest_testFindById_expected.xls"),
            actual);
    }

    /**
     * {@link WorkflowTemplateUserDaoImpl#create}のテストケース.
     * @throws Exception
     */
    @Test
    public void testCreate() throws Exception {
        WorkflowTemplateUser entity = new WorkflowTemplateUser();
        entity.setProjectId("PJ1");

        User user = new User();
        user.setEmpNo("ZZA05");
        user.setNameE("Ogiwara Keiichi");

        entity.setUser(user);
        entity.setName("TEST");
        entity.setCreatedBy(user);
        entity.setUpdatedBy(user);

        Long id = null;
        while(true) {
            try {
                // テスト実行
                id = dao.create(entity);
                break;
            } catch (KeyDuplicateException e) {
                continue;
            }
        }
        assertNotNull(id);

       // 登録したデータを取得
       WorkflowTemplateUser actual = dao.findById(id);

       assertEquals(entity.getProjectId(), actual.getProjectId());
       assertEquals(entity.getUser().toString(), actual.getUser().toString());
       assertEquals(entity.getName(), actual.getName());
       assertEquals(entity.getCreatedBy().getEmpNo(), actual.getCreatedBy().getEmpNo());
       assertNotNull(actual.getCreatedAt());
       assertEquals(entity.getUpdatedBy().getEmpNo(), actual.getUpdatedBy().getEmpNo());
       assertNotNull(actual.getUpdatedAt());
       assertTrue(String.valueOf(1L).equals(actual.getVersionNo().toString()));
       assertTrue(String.valueOf(0).equals(actual.getDeleteNo().toString()));
    }

    /**
     * {@link WorkflowTemplateUserDaoImpl#delete}のテストケース.
     * @throws Exception
     */
    @Test
    public void testDelete() throws Exception {
        Long id = 1L;

        // 削除前のデータを取得
        WorkflowTemplateUser before = dao.findById(id);

        WorkflowTemplateUser entity = new WorkflowTemplateUser();
        entity.setId(id);

        User loginUser = new User();
        loginUser.setEmpNo("ZZA05");
        loginUser.setNameE("Keiichi Ogiwara");
        entity.setUpdatedBy(loginUser);
        entity.setVersionNo(before.getVersionNo());

        // テスト実行
        Integer record = dao.delete(entity);

        assertTrue(1 == record);

        // 削除後のデータを取得する
        String sql = "select * from workflow_template_user where id = " + id + "and delete_no != 0";
        ITable actual = getConnection().createQueryTable("actual", sql);

        assertEquals(before.getId().toString(), actual.getValue(0, "id").toString());
        assertEquals(before.getProjectId(), actual.getValue(0, "project_id").toString());
        assertEquals(before.getUser().getEmpNo(), actual.getValue(0, "emp_no").toString());
        assertEquals(before.getName(), actual.getValue(0, "name").toString());
        assertEquals(before.getCreatedBy().getEmpNo(), actual.getValue(0, "created_by").toString());
        assertEquals(before.getCreatedAt(), actual.getValue(0, "created_at"));
        assertEquals(loginUser.getEmpNo(), actual.getValue(0, "updated_by").toString());
        assertFalse(before.getUpdatedAt().toString().equals(actual.getValue(0, "updated_at").toString()));
        assertEquals(String.valueOf(before.getVersionNo() + 1), actual.getValue(0, "version_no").toString());
    }

    /**
     * {@link WorkflowTemplateUserDaoImpl#delete}のテストケース.
     * 排他制御
     * @throws Exception
     */
    @Test(expected = StaleRecordException.class)
    public void testDeleteStaleRecordException() throws Exception {
        WorkflowTemplateUser entity = new WorkflowTemplateUser();
        entity.setId(1L);

        User loginUser = new User();
        loginUser.setEmpNo("ZZA05");
        loginUser.setNameE("Keiichi Ogiwara");
        entity.setUpdatedBy(loginUser);
        // 排他制御
        entity.setVersionNo(-1L);

        // テスト実行
        dao.delete(entity);
    }

}
