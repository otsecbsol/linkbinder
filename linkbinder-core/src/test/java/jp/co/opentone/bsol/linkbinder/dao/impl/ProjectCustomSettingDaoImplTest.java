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
import jp.co.opentone.bsol.framework.core.dao.StaleRecordException;
import jp.co.opentone.bsol.framework.core.extension.ibatis.exception.InvalidNullUpdatedRuntimeException;
import jp.co.opentone.bsol.framework.test.AbstractDaoTestCase;
import jp.co.opentone.bsol.linkbinder.dto.ProjectCustomSetting;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.code.CorresponStatus;

/**
 * ProjectCustomSettingDaoImpl{@link ProjectCustomSettingDaoImpl}のテストケース.
 * @author opentone
 */
public class ProjectCustomSettingDaoImplTest extends AbstractDaoTestCase {

    /**
     * テスト対象.
     */
    @Resource
    private ProjectCustomSettingDaoImpl dao;

    /**
     * {@link ProjectCustomSettingDaoImpl#findByProjectId} のテストケース.
     * 正常系
     * @throws Exception
     */
    @Test
    public void testFindByProjectId() throws Exception {
        String projectId = "TEST-PJ1";

        ProjectCustomSetting actual = dao.findByProjectId(projectId);

        // 取得したオブジェクトと、Excelファイルに定義済の期待値を比較する
        assertNotNull(actual);
        assertDataSetEquals(newDataSet("ProjectCustomSettingDaoImplTest_testFindByProjectId_expected.xls"), actual);
    }

    /**
     * {@link ProjectCustomSettingDaoImpl#findByProjectId} のテストケース.
     * 正常系(該当レコードがない場合nullを返す)
     * @throws Exception
     */
    @Test
    public void testFindByProjectIdNotRecord() throws Exception {
        String projectId = "TEST-PJ3";

        ProjectCustomSetting actual = dao.findByProjectId(projectId);

        assertNull(actual);
    }

    /**
     * {@link ProjectCustomSettingDaoImpl#create} のテストケース.
     * 正常系
     * @throws Exception
     */
    @Test
    public void testCreate() throws Exception {
        User user = new User();
        user.setEmpNo("ZZA01");
        ProjectCustomSetting p = new ProjectCustomSetting();
        p.setProjectId("TEST-PJ3");
        p.setDefaultStatus(CorresponStatus.CLOSED);
        p.setUsePersonInCharge(false);
        p.setCreatedBy(user);
        p.setUpdatedBy(user);

        Long id = null;

        while (true) {
            try {
                id = dao.create(p);
                break;
            } catch (KeyDuplicateException kde) {
                continue;
            }
        }

        assertNotNull(id);

        // 登録したデータを取得する
        String sql = "select * from project_custom_setting where id =" + id;
        ITable actual = getConnection().createQueryTable("actual", sql);

        assertEquals(p.getProjectId(), actual.getValue(0, "project_id"));
        assertEquals(p.getDefaultStatus().getValue().toString(),
            actual.getValue(0, "default_status").toString());
        assertEquals((Long.valueOf(p.isUsePersonInCharge() ? 1 : 0)).toString(),
            actual.getValue(0, "use_person_in_charge").toString());
        assertEquals(p.getCreatedBy().getEmpNo(), actual.getValue(0, "created_by"));
        assertNotNull(actual.getValue(0, "created_at"));
        assertEquals(p.getUpdatedBy().getEmpNo(), actual.getValue(0, "updated_by"));
        assertNotNull(actual.getValue(0, "updated_at"));
        assertEquals(String.valueOf(1L), actual.getValue(0, "version_no").toString());
        assertEquals(String.valueOf(0L), actual.getValue(0, "delete_no").toString());
    }

    /**
     * {@link ProjectCustomSettingDaoImpl#create} のテストケース.
     * 異常系(AKが重複する)
     * @throws Exception
     */
    @Test(expected = KeyDuplicateException.class)
    public void testCreateKeyDuplicate() throws Exception {
        User user = new User();
        user.setEmpNo("ZZA01");
        ProjectCustomSetting p = new ProjectCustomSetting();
        p.setProjectId("TEST-PJ1");
        p.setDefaultStatus(CorresponStatus.CLOSED);
        p.setUsePersonInCharge(false);
        p.setCreatedBy(user);
        p.setUpdatedBy(user);

        dao.create(p);
    }

    /**
     * {@link ProjectCustomSettingDaoImpl#update} のテストケース.
     * 正常系
     * @throws Exception
     */
    @Test
    public void testUpdate()  throws Exception {
        String projectId = "TEST-PJ1";
        ProjectCustomSetting p = dao.findByProjectId(projectId);
        User user = new User();
        user.setEmpNo("ZZA02");
        p.setDefaultStatus(CorresponStatus.CLOSED);
        p.setUsePersonInCharge(false);
        p.setUpdatedBy(user);

        Integer i = dao.update(p);

        assertEquals(Integer.valueOf(1).intValue(), i.intValue());

        // 登録したデータを取得する
        ProjectCustomSetting q = dao.findByProjectId(projectId);

        assertEquals(p.getProjectId(), q.getProjectId());
        assertEquals(p.getDefaultStatus(), q.getDefaultStatus());
        assertEquals(p.isUsePersonInCharge(), q.isUsePersonInCharge());
        assertEquals(p.getCreatedBy().getEmpNo(), q.getCreatedBy().getEmpNo());
        assertTrue(p.getCreatedAt().equals(q.getCreatedAt()));
        assertEquals(p.getUpdatedBy().getEmpNo(), q.getUpdatedBy().getEmpNo());
        assertFalse(p.getUpdatedAt().equals(q.getUpdatedAt()));
        assertEquals(p.getVersionNo().longValue() + 1L, q.getVersionNo().longValue());
        assertEquals(p.getDeleteNo().longValue(), q.getDeleteNo().longValue());
    }


    /**
     * {@link ProjectCustomSettingDaoImpl#update} のテストケース.
     * 異常系(version_noが異なる)
     * @throws Exception
     */
    @Test(expected = StaleRecordException.class)
    public void testUpdateFraudVersionNo()  throws Exception {
        String projectId = "TEST-PJ1";
        ProjectCustomSetting p = dao.findByProjectId(projectId);
        User user = new User();
        user.setEmpNo("ZZA02");
        p.setDefaultStatus(CorresponStatus.CLOSED);
        p.setUsePersonInCharge(false);
        p.setUpdatedBy(user);
        p.setVersionNo(Long.valueOf(100));

        dao.update(p);
    }

    /**
     * {@link ProjectCustomSettingDaoImpl#update} のテストケース.
     * 異常系(NotNullのカラムにnullを設定)
     * @throws Exception
     */
    @Test(expected = InvalidNullUpdatedRuntimeException.class)
    public void testUpdateNotNull() throws Exception {
        String projectId = "TEST-PJ1";
        ProjectCustomSetting p = dao.findByProjectId(projectId);
        User user = new User();
        user.setEmpNo("ZZA02");
        p.setDefaultStatus(null);
        p.setUsePersonInCharge(false);
        p.setUpdatedBy(user);

        dao.update(p);
    }

    /**
     * {@link ProjectCustomSettingDaoImpl#update} のテストケース.
     * 正常系(異なるプロジェクトIDを設定しても、変更されたプロジェクトIDは適応させない)
     * @throws Exception
     */
    @Test
    public void testUpdateChangeProjectId() throws Exception {
        String projectId = "TEST-PJ1";
        ProjectCustomSetting p = dao.findByProjectId(projectId);
        User user = new User();
        user.setEmpNo("ZZA02");
        p.setProjectId("TEST-PJ2");
        p.setDefaultStatus(CorresponStatus.OPEN);
        p.setUsePersonInCharge(true);
        p.setUpdatedBy(user);

        Integer i = dao.update(p);

        assertEquals(Integer.valueOf(1).intValue(), i.intValue());

        // 登録したデータを取得する
        ProjectCustomSetting q = dao.findByProjectId(projectId);

        assertFalse(p.getProjectId().equals(q.getProjectId()));
        assertEquals(p.getDefaultStatus(), q.getDefaultStatus());
        assertEquals(p.isUsePersonInCharge(), q.isUsePersonInCharge());
        assertEquals(p.getCreatedBy().getEmpNo(), q.getCreatedBy().getEmpNo());
        assertTrue(p.getCreatedAt().equals(q.getCreatedAt()));
        assertEquals(p.getUpdatedBy().getEmpNo(), q.getUpdatedBy().getEmpNo());
        assertFalse(p.getUpdatedAt().equals(q.getUpdatedAt()));
        assertEquals(p.getVersionNo().longValue() + 1L, q.getVersionNo().longValue());
        assertEquals(p.getDeleteNo().longValue(), q.getDeleteNo().longValue());
    }
}
