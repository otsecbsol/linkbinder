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
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.WorkflowTemplate;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowType;

/**
 * WorkflowTemplateDaoImplのテストケース.
 * @author opentone
 */
public class WorkflowTemplateDaoImplTest extends AbstractDaoTestCase {

    /**
     * テスト対象.
     */
    @Resource
    private WorkflowTemplateDaoImpl dao;

    /**
     * {@link WorkflowTemplateDaoImpl#findByWorkflowTemplateUserId}のテストケース.
     * @throws Exception
     */
    @Test
    public void testFindByWorkflowTemplateUserId() throws Exception {
        // テスト実行
        List<WorkflowTemplate> actual = dao.findByWorkflowTemplateUserId(1L);

        // 取得したオブジェクトと、Excelファイルに定義済の期待値を比較する
        assertNotNull(actual);
        assertDataSetEquals(newDataSet("WorkflowTemplateDaoImplTest_testFindByWorkflowTemplateUserId_expected.xls"),
            actual);
    }

    /**
     * {@link WorkflowTemplateDaoImpl#findByWorkflowTemplateUserId}のテストケース.
     * データが取得できない
     * @throws Exception
     */
    @Test
    public void testFindByWorkflowTemplateUserIdNoData() throws Exception {
        // テスト実行
        List<WorkflowTemplate> actual = dao.findByWorkflowTemplateUserId(999L);

        // 取得したオブジェクトと、Excelファイルに定義済の期待値を比較する
        assertNotNull(actual);
        assertTrue(actual.isEmpty());
    }

    /**
     * {@link WorkflowTemplateDaoImpl#create}のテストケース.
     * @throws Exception
     */
    @Test
    public void testCreate() throws Exception {
        WorkflowTemplate entity = new WorkflowTemplate();
        entity.setWorkflowTemplateUserId(1L);

        User user = new User();
        user.setEmpNo("ZZA10");
        user.setNameE("John Winston Ono Lennon");

        entity.setUser(user);
        entity.setWorkflowType(WorkflowType.CHECKER);
        entity.setWorkflowNo(9L);

        User loginUser = new User();
        loginUser.setEmpNo("ZZA05");
        loginUser.setNameE("Keiichi Ogiwara");
        entity.setCreatedBy(loginUser);
        entity.setUpdatedBy(loginUser);

        Long id = null;
        // テスト実行
        while(true) {
            try {
                id = dao.create(entity);
                break;
            } catch (KeyDuplicateException e) {
                continue;
            }
        }
        assertNotNull(id);

        // 登録したデータを取得する
        String sql = "select * from workflow_template where id = " + id;
        ITable actual = getConnection().createQueryTable("actual", sql);

        assertEquals(id.toString(), actual.getValue(0, "id").toString());
        assertEquals(entity.getWorkflowTemplateUserId().toString(), actual.getValue(0, "workflow_template_user_id").toString());
        assertEquals(user.getEmpNo(), actual.getValue(0, "emp_no").toString());
        assertEquals(entity.getWorkflowType().getValue().toString(), actual.getValue(0, "workflow_type").toString());
        assertEquals(entity.getWorkflowNo().toString(), actual.getValue(0, "workflow_no").toString());
        assertEquals(entity.getCreatedBy().getEmpNo(), actual.getValue(0, "created_by").toString());
        assertNotNull(actual.getValue(0, "created_at"));
        assertEquals(entity.getUpdatedBy().getEmpNo(), actual.getValue(0, "updated_by").toString());
        assertEquals(String.valueOf(0L), actual.getValue(0, "delete_no").toString());
    }

    /**
     * {@link WorkflowTemplateDaoImpl#deleteByWorkflowTemplateUserId}のテストケース.
     * @throws Exception
     */
    @Test
    public void testDeleteByWorkflowTemplateUserId() throws Exception {
        Long id = 1L;
        WorkflowTemplate workflowTemplate = new WorkflowTemplate();
        workflowTemplate.setWorkflowTemplateUserId(id);

        User loginUser = new User();
        loginUser.setEmpNo("ZZA05");
        loginUser.setNameE("Keiichi Ogiwara");
        workflowTemplate.setUpdatedBy(loginUser);

        // 削除前のデータを取得
        String sql = "select * from workflow_template where workflow_template_user_id = " + id;
        ITable before = getConnection().createQueryTable("actual", sql);

        // テスト実行
        Integer record = dao.deleteByWorkflowTemplateUserId(workflowTemplate);

        assertTrue(8 == record);
        // 削除後のデータを取得
        ITable after = getConnection().createQueryTable("actual", sql);

        assertEquals(before.getRowCount(), after.getRowCount());

        for (int i = 0 ; i < record ; i++) {
            assertEquals(before.getValue(i, "id").toString(), after.getValue(i, "id").toString());
            assertEquals(before.getValue(i, "workflow_template_user_id").toString(), after.getValue(i, "workflow_template_user_id").toString());
            assertEquals(before.getValue(i, "emp_no"), after.getValue(i, "emp_no"));
            assertEquals(before.getValue(i, "workflow_type"), after.getValue(i, "workflow_type"));
            assertEquals(before.getValue(i, "workflow_no"), after.getValue(i, "workflow_no"));
            assertEquals(before.getValue(i, "created_by"), after.getValue(i, "created_by"));
            assertEquals(before.getValue(i, "created_at"), after.getValue(i, "created_at"));
            assertEquals(loginUser.getEmpNo(), after.getValue(i, "updated_by").toString());
            assertFalse(before.getValue(i, "updated_at").toString().equals(after.getValue(i, "updated_at").toString()));
            assertFalse(before.getValue(i, "delete_no").toString().equals(after.getValue(i, "delete_no").toString()));
        }
    }
}
