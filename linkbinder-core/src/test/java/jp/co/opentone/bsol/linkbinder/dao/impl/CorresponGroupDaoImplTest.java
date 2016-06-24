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

import static org.junit.Assert.*;

import java.util.List;

import javax.annotation.Resource;

import org.dbunit.dataset.ITable;
import org.junit.Test;

import jp.co.opentone.bsol.framework.core.dao.KeyDuplicateException;
import jp.co.opentone.bsol.framework.core.dao.StaleRecordException;
import jp.co.opentone.bsol.framework.test.AbstractDaoTestCase;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroup;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroupUser;
import jp.co.opentone.bsol.linkbinder.dto.Discipline;
import jp.co.opentone.bsol.linkbinder.dto.Site;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponGroupCondition;

/**
 * {@link CorresponGroupDaoImpl}のテストケース.
 * @author opentone
 */
public class CorresponGroupDaoImplTest extends AbstractDaoTestCase {

    /**
     * テスト対象.
     */
    @Resource
    private CorresponGroupDaoImpl dao;

    /**
     * 該当データがある場合の検証.
     * @throws Exception
     */
    @Test
    public void testFind() throws Exception {
        // 取得行数を指定せず実行
        // 条件に該当する全ての行が取得できる
        SearchCorresponGroupCondition c = new SearchCorresponGroupCondition();
        c.setProjectId("PJ2");

        List<CorresponGroup> cgs = dao.find(c);

        assertNotNull(cgs);
        assertDataSetEquals(newDataSet("CorresponGroupDaoImplTest_testFind_expected.xls"), cgs);
    }

    /**
     * 該当データがある場合の検証(ソート列指定).
     * @throws Exception
     */
    @Test
    public void testFindSort() throws Exception {
        // 取得行数を指定せず実行
        // 条件に該当する全ての行が取得できる
        SearchCorresponGroupCondition c = new SearchCorresponGroupCondition();
        c.setProjectId("PJ2");
        c.setSortColumn("id");

        List<CorresponGroup> cgs = dao.find(c);

        assertNotNull(cgs);
        assertDataSetEquals(newDataSet("CorresponGroupDaoImplTest_testFindSort_expected.xls"), cgs);
    }
    /**
     * 検索をするユーザーがGroupAdminの場合の検索処理を検証.
     * @throws Exception
     */
    @Test
    public void testFindGroupAdmin() throws Exception {
        SearchCorresponGroupCondition condition = new SearchCorresponGroupCondition();
        condition.setGroupAdmin(true);
        condition.setProjectId("PJ1");

        User searchUser = new User();
        searchUser.setEmpNo("ZZA10");
        searchUser.setSecurityLevel("30");
        condition.setSearchUser(searchUser);

        List<CorresponGroup> actual = dao.find(condition);

        assertNotNull(actual);
        assertDataSetEquals(newDataSet("CorresponGroupDaoImplTest_testFindGroupAdmin_expected.xls"),
            actual);
    }

    /**
     * 1件レコード取得の検証.
     * @throws Exception
     */
    @Test
    public void testFindById() throws Exception {
        CorresponGroup cgs = dao.findById(10L);

        assertNotNull(cgs);
        assertDataSetEquals(newDataSet("CorresponGroupDaoImplTest_testFindById_expected.xls"), cgs);
    }

    /**
     * 該当データがある場合の検証. 無視する行数と取得件数を指定
     * @throws Exception
     */
    @Test
    public void testFindMaxResults() throws Exception {
        // 11件目から20件取得
        SearchCorresponGroupCondition c = new SearchCorresponGroupCondition();
        c.setPageNo(2);
        c.setPageRowNum(10);
        c.setProjectId("PJ2");

        List<CorresponGroup> cgs = dao.find(c);
        assertNotNull(cgs);
        assertDataSetEquals(newDataSet("CorresponGroupDaoImplTest_testFindMaxResults_expected.xls"),
            cgs);
    }

    /**
     * 該当データが無い場合の検証.
     * @throws Exception
     */
    @Test
    public void testFindEmpty() throws Exception {
        SearchCorresponGroupCondition c = new SearchCorresponGroupCondition();
        c.setProjectId("999");
        assertEquals(0, dao.find(c).size());
    }

    /**
     * 部門情報IDで活動単位を取得する場合を検証.
     * @throws Exception
     */
    @Test
    public void testFindByDisciplineId() throws Exception {
        List<CorresponGroup> cg = dao.findByDisciplineId(1L);
        assertDataSetEquals(newDataSet("CorresponGroupDaoImplTest_testFindByDisciplineId_expected.xls"),
            cg);
    }

    /**
     * 部門情報IDで活動単位を取得する場合を検証. 該当データがない場合
     * @throws Exception
     */
    @Test
    public void testFindByDisciplineIdNoData() throws Exception {
        List<CorresponGroup> actual = dao.findByDisciplineId(99L);
        assertTrue(actual.size() == 0);
    }

    /**
     * プロジェクトIDを指定して活動単位を取得する処理を検証
     * @throws Exception
     */
    @Test
    public void testFindByProjectId() throws Exception {
        SearchCorresponGroupCondition c = new SearchCorresponGroupCondition();
        c.setProjectId("PJ1");

        List<CorresponGroup> cgs = dao.find(c);

        assertNotNull(cgs);
        assertFalse(cgs.isEmpty());
        assertDataSetEquals(newDataSet("CorresponGroupDaoImplTest_testFindByProjectId_expected.xls"),
            cgs);
    }

    /**
     * 拠点情報IDを指定して活動単位を取得する処理を検証
     * @throws Exception
     */
    @Test
    public void testFindBySiteId() throws Exception {
        SearchCorresponGroupCondition c = new SearchCorresponGroupCondition();
        c.setSiteId(1L);

        List<CorresponGroup> cgs = dao.find(c);

        assertNotNull(cgs);
        assertFalse(cgs.isEmpty());
        assertDataSetEquals(newDataSet("CorresponGroupDaoImplTest_testFindBySiteId_expected.xls"),
            cgs);
    }

    /**
     * 拠点情報IDを指定して活動単位を取得する処理を検証
     * @throws Exception
     */
    @Test
    public void testCountCorresponGroup() throws Exception {
        SearchCorresponGroupCondition c = new SearchCorresponGroupCondition();
        c.setSiteId(1L);
        int count = dao.countCorresponGroup(c);
        assertTrue(3 == count);
    }

    /**
     * 拠点情報IDを指定して活動単位件数を取得する処理を検証. 件数を取得するユーザーがGroupAdminの場合
     * @throws Exception
     */
    @Test
    public void testCountCorresponGroupGroupAdmin() throws Exception {
        SearchCorresponGroupCondition condition = new SearchCorresponGroupCondition();
        condition.setGroupAdmin(true);
        condition.setProjectId("PJ1");
        condition.setSiteId(1L);

        User searchUser = new User();
        searchUser.setEmpNo("ZZA10");
        searchUser.setSecurityLevel("30");
        condition.setSearchUser(searchUser);

        int actual = dao.countCorresponGroup(condition);

        assertTrue(actual == 1);

    }

    /**
     * 活動単位を登録する処理を検証.
     * @throws Exception
     */
    @Test
    public void testCreate() throws Exception {
        CorresponGroup cg = new CorresponGroup();
        Site site = new Site();
        site.setId(3L);

        cg.setSite(site);

        Discipline dis = new Discipline();
        dis.setId(1L);

        cg.setDiscipline(dis);
        cg.setName("SHJ:BUILD");

        User loginUser = new User();
        loginUser.setEmpNo("ZZA05");

        cg.setCreatedBy(loginUser);
        cg.setUpdatedBy(loginUser);

        Long id = null;

        while (true) {
            try {
                id = dao.create(cg);
                break;
            } catch (KeyDuplicateException ked) {
                continue;
            }
        }

        // 登録したデータを取得
        CorresponGroup actual = dao.findById(id);

        assertEquals(site.getId(), actual.getSite().getId());
        assertEquals(dis.getId(), actual.getDiscipline().getId());
        assertEquals(cg.getName(), actual.getName());
        assertEquals(loginUser.toString(), actual.getCreatedBy().toString());
        assertEquals(loginUser.toString(), actual.getUpdatedBy().toString());
        assertEquals(String.valueOf(1L), String.valueOf(actual.getVersionNo()));
        assertEquals(String.valueOf(0L), String.valueOf(actual.getDeleteNo()));
    }

    /**
     * 活動単位を更新する処理を検証.
     * @throws Exception
     */
    @Test
    public void testUpdate() throws Exception {
        CorresponGroup cg = new CorresponGroup();
        cg.setName("UPDATE");

        User user = new User();
        user.setEmpNo("ZZA02");

        cg.setUpdatedBy(user);
        cg.setVersionNo(0L);

        // 更新前のデータを取得
        CorresponGroup before = dao.findById(1L);

        cg.setId(before.getId());

        dao.update(cg);

        // 更新後のデータを取得
        CorresponGroup after = dao.findById(before.getId());

        assertEquals(before.getId(), after.getId());
        assertEquals(before.getSite().toString(), after.getSite().toString());
        assertEquals(before.getDiscipline().toString(), after.getDiscipline().toString());
        assertFalse(before.getName().equals(after.getName()));
        assertEquals(cg.getName(), after.getName());
        assertEquals(before.getCreatedBy().toString(), after.getCreatedBy().toString());
        assertEquals(before.getCreatedAt(), after.getCreatedAt());
        assertFalse(before.getUpdatedBy().getEmpNo().equals(after.getUpdatedBy().getEmpNo()));
        assertEquals(cg.getUpdatedBy().getEmpNo(), after.getUpdatedBy().getEmpNo());
        assertEquals(String.valueOf(before.getVersionNo() + 1), after.getVersionNo().toString());
        assertEquals(String.valueOf(before.getDeleteNo()), String.valueOf(after.getDeleteNo()));
    }

    /**
     * 活動単位を更新する処理を検証. StaleRecordExceptionが発生
     * @throws Exception
     */
    @Test(expected = StaleRecordException.class)
    public void testUpdateStaleRecordException() throws Exception {
        CorresponGroup cg = new CorresponGroup();
        cg.setName("UPDATE");

        User user = new User();
        user.setEmpNo("ZZA02");

        cg.setUpdatedBy(user);
        cg.setVersionNo(1L);

        cg.setId(1L);

        dao.update(cg);
    }

    /**
     * 活動単位IDを指定して活動単位を削除する.
     * @throws Exception
     */
    @Test
    public void testDelete() throws Exception {
        // 削除前のデータを取得
        CorresponGroup before = dao.findById(1L);

        CorresponGroup cg = new CorresponGroup();
        cg.setId(before.getId());
        cg.setSite(before.getSite());
        cg.setDiscipline(before.getDiscipline());
        cg.setName(before.getName());
        cg.setCreatedBy(before.getCreatedBy());
        cg.setCreatedAt(before.getCreatedAt());
        User loginUser = new User();
        loginUser.setEmpNo("ZZA03");
        loginUser.setNameE("Atsushi Isida");

        cg.setUpdatedBy(loginUser);
        cg.setVersionNo(before.getVersionNo());
        cg.setDeleteNo(before.getDeleteNo());

        dao.delete(cg);

        // 削除後のデータを取得する
        String sql = "select * from correspon_group where id = 1 and delete_no != 0";
        ITable actual = getConnection().createQueryTable("actual", sql);

        assertEquals(before.getId().toString(), actual.getValue(0, "id").toString());
        assertEquals(before.getSite().getId().toString(), actual.getValue(0, "site_id").toString());
        assertEquals(before.getDiscipline().getId().toString(), actual.getValue(0, "discipline_id")
            .toString());
        assertEquals(before.getName(), actual.getValue(0, "name").toString());
        assertEquals(before.getCreatedBy().getEmpNo(), actual.getValue(0, "created_by").toString());
        assertEquals(before.getCreatedAt(), actual.getValue(0, "created_at"));
        assertFalse(before.getUpdatedBy().getEmpNo().equals(actual.getValue(0, "updated_by")
            .toString()));
        assertEquals(loginUser.getEmpNo(), actual.getValue(0, "updated_by").toString());
        assertFalse(before.getUpdatedAt().equals(actual.getValue(0, "updated_at")));
        assertEquals(String.valueOf(before.getVersionNo() + 1), actual.getValue(0, "version_no")
            .toString());
        assertFalse(String.valueOf(before.getDeleteNo()).equals(actual.getValue(0, "delete_no")
            .toString()));

    }

    /**
     * 活動単位IDを指定して活動単位を削除する. 排他制御
     * @throws Exception
     */
    @Test(expected = StaleRecordException.class)
    public void testDeleteVersionNoDiff() throws Exception {
        // 削除前のデータを取得
        CorresponGroup before = dao.findById(1L);

        CorresponGroup cg = new CorresponGroup();
        cg.setId(before.getId());
        cg.setSite(before.getSite());
        cg.setDiscipline(before.getDiscipline());
        cg.setName(before.getName());
        cg.setCreatedBy(before.getCreatedBy());
        cg.setCreatedAt(before.getCreatedAt());
        User loginUser = new User();
        loginUser.setEmpNo("ZZA03");
        loginUser.setNameE("Atsushi Isida");

        cg.setUpdatedBy(loginUser);
        cg.setVersionNo(99L);
        cg.setDeleteNo(before.getDeleteNo());

        dao.delete(cg);
        fail("例外が発生していない");
    }

    /**
     * 従業員番号を指定して活動単位ユーザーを取得する.
     * @throws Exception
     */
    @Test
    public void testFindByEmpNo() throws Exception {
        List<CorresponGroupUser> cgUser = dao.findByEmpNo("PJ1", "ZZA04");

        assertEquals("PJ1", cgUser.get(0).getCorresponGroup().getProjectId());
        assertEquals(String.valueOf(1L), cgUser.get(0).getCorresponGroup().getId().toString());
        assertEquals("YOC:IT", cgUser.get(0).getCorresponGroup().getName());
        assertEquals("ZZA04", cgUser.get(0).getUser().getEmpNo());
        assertEquals("40", cgUser.get(0).getSecurityLevel());
        assertEquals("ZZA01", cgUser.get(0).getCreatedBy().getEmpNo());
        assertEquals("ZZA01", cgUser.get(0).getUpdatedBy().getEmpNo());
        assertEquals(String.valueOf(0L), cgUser.get(0).getDeleteNo().toString());
    }

    /**
     * 従業員番号を指定して活動単位ユーザーを取得する(ソート列指定).
     * @throws Exception
     */
    @Test
    public void testFindByEmpNoSort() throws Exception {
        List<CorresponGroupUser> cgUser = dao.findByEmpNo("PJ1", "ZZA01" , "correspon_group_name");

        assertNotNull(cgUser);
        assertEquals(3, cgUser.size());

        assertEquals("PJ1", cgUser.get(0).getCorresponGroup().getProjectId());
        assertEquals(String.valueOf(3L), cgUser.get(0).getCorresponGroup().getId().toString());
        assertEquals("YOC:BUILDING", cgUser.get(0).getCorresponGroup().getName());
        assertEquals("ZZA01", cgUser.get(0).getUser().getEmpNo());
        assertEquals("30", cgUser.get(0).getSecurityLevel());
        assertEquals("ZZA01", cgUser.get(0).getCreatedBy().getEmpNo());
        assertEquals("ZZA01", cgUser.get(0).getUpdatedBy().getEmpNo());
        assertEquals(String.valueOf(0L), cgUser.get(0).getDeleteNo().toString());

        assertEquals("PJ1", cgUser.get(1).getCorresponGroup().getProjectId());
        assertEquals(String.valueOf(1L), cgUser.get(1).getCorresponGroup().getId().toString());
        assertEquals("YOC:IT", cgUser.get(1).getCorresponGroup().getName());
        assertEquals("ZZA01", cgUser.get(1).getUser().getEmpNo());
        assertEquals("30", cgUser.get(1).getSecurityLevel());
        assertEquals("ZZA01", cgUser.get(1).getCreatedBy().getEmpNo());
        assertEquals("ZZA01", cgUser.get(1).getUpdatedBy().getEmpNo());
        assertEquals(String.valueOf(0L), cgUser.get(1).getDeleteNo().toString());

        assertEquals("PJ1", cgUser.get(2).getCorresponGroup().getProjectId());
        assertEquals(String.valueOf(2L), cgUser.get(2).getCorresponGroup().getId().toString());
        assertEquals("YOC:PIPING", cgUser.get(2).getCorresponGroup().getName());
        assertEquals("ZZA01", cgUser.get(2).getUser().getEmpNo());
        assertEquals("30", cgUser.get(2).getSecurityLevel());
        assertEquals("ZZA01", cgUser.get(2).getCreatedBy().getEmpNo());
        assertEquals("ZZA01", cgUser.get(2).getUpdatedBy().getEmpNo());
        assertEquals(String.valueOf(0L), cgUser.get(2).getDeleteNo().toString());

        cgUser = dao.findByEmpNo("PJ1", "ZZA01" , "id");

        assertNotNull(cgUser);
        assertEquals(3, cgUser.size());

        assertEquals("PJ1", cgUser.get(0).getCorresponGroup().getProjectId());
        assertEquals(String.valueOf(1L), cgUser.get(0).getCorresponGroup().getId().toString());
        assertEquals("YOC:IT", cgUser.get(0).getCorresponGroup().getName());
        assertEquals("ZZA01", cgUser.get(0).getUser().getEmpNo());
        assertEquals("30", cgUser.get(0).getSecurityLevel());
        assertEquals("ZZA01", cgUser.get(0).getCreatedBy().getEmpNo());
        assertEquals("ZZA01", cgUser.get(0).getUpdatedBy().getEmpNo());
        assertEquals(String.valueOf(0L), cgUser.get(0).getDeleteNo().toString());

        assertEquals("PJ1", cgUser.get(1).getCorresponGroup().getProjectId());
        assertEquals(String.valueOf(2L), cgUser.get(1).getCorresponGroup().getId().toString());
        assertEquals("YOC:PIPING", cgUser.get(1).getCorresponGroup().getName());
        assertEquals("ZZA01", cgUser.get(1).getUser().getEmpNo());
        assertEquals("30", cgUser.get(1).getSecurityLevel());
        assertEquals("ZZA01", cgUser.get(1).getCreatedBy().getEmpNo());
        assertEquals("ZZA01", cgUser.get(1).getUpdatedBy().getEmpNo());
        assertEquals(String.valueOf(0L), cgUser.get(1).getDeleteNo().toString());

        assertEquals("PJ1", cgUser.get(2).getCorresponGroup().getProjectId());
        assertEquals(String.valueOf(3L), cgUser.get(2).getCorresponGroup().getId().toString());
        assertEquals("YOC:BUILDING", cgUser.get(2).getCorresponGroup().getName());
        assertEquals("ZZA01", cgUser.get(2).getUser().getEmpNo());
        assertEquals("30", cgUser.get(2).getSecurityLevel());
        assertEquals("ZZA01", cgUser.get(2).getCreatedBy().getEmpNo());
        assertEquals("ZZA01", cgUser.get(2).getUpdatedBy().getEmpNo());
        assertEquals(String.valueOf(0L), cgUser.get(2).getDeleteNo().toString());

    }
}
