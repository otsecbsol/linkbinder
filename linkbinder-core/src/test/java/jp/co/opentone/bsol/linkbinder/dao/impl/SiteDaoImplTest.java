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

import jp.co.opentone.bsol.framework.core.config.SystemConfig;
import jp.co.opentone.bsol.framework.core.dao.KeyDuplicateException;
import jp.co.opentone.bsol.framework.core.dao.StaleRecordException;
import jp.co.opentone.bsol.framework.test.AbstractDaoTestCase;
import jp.co.opentone.bsol.linkbinder.Constants;
import jp.co.opentone.bsol.linkbinder.dao.SiteDao;
import jp.co.opentone.bsol.linkbinder.dto.Site;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchSiteCondition;

/**
 * SiteDaoImplのテストケース.
 * @author opentone
 */
public class SiteDaoImplTest extends AbstractDaoTestCase {

    /**
     * テスト対象.
     */
    @Resource
    private SiteDaoImpl dao;

    private static final int DEFAULT_PAGE_NO = 1;

    private static final int DEFAULT_ROW_NUM = 10;

    /**
     * {@link SiteDaoImpl#find} のテストケース. SystemAdminの場合
     * @throws Exception
     */
    @Test
    public void testFindSystemAdmin() throws Exception {
        // テストに必要なデータを作成
        SearchSiteCondition condition = new SearchSiteCondition();

        condition.setProjectId("PJ1");
        condition.setPageNo(DEFAULT_PAGE_NO);
        condition.setPageRowNum(DEFAULT_ROW_NUM);
        condition.setSystemAdmin(true);

        List<Site> actual = dao.find(condition);

        // 取得したオブジェクトと、Excelファイルに定義済の期待値を比較する
        assertNotNull(actual);
        assertDataSetEquals(newDataSet("SiteDaoImplTest_testFind_expected.xls"), actual);

    }

    /**
     * {@link SiteDaoImpl#find} のテストケース.
     * 拠点コードで検索
     * @throws Exception
     */
    @Test
    public void testFindSiteCode() throws Exception {
        // テストに必要なデータを作成
        SearchSiteCondition condition = new SearchSiteCondition();

        condition.setProjectId("PJ1");
        condition.setSiteCd("YO");

        condition.setPageNo(DEFAULT_PAGE_NO);
        condition.setPageRowNum(DEFAULT_ROW_NUM);
        condition.setSystemAdmin(true);

        List<Site> actual = dao.find(condition);

        // 取得したオブジェクトと、Excelファイルに定義済の期待値を比較する
        assertNotNull(actual);
        assertDataSetEquals(newDataSet("SiteDaoImplTest_testFindSiteCode_expected.xls"), actual);

    }

    /**
     * {@link SiteDaoImpl#find} のテストケース.
     * 拠点名で検索
     * @throws Exception
     */
    @Test
    public void testFindName() throws Exception {
        // テストに必要なデータを作成
        SearchSiteCondition condition = new SearchSiteCondition();

        condition.setProjectId("PJ1");
        condition.setName("Sh");

        condition.setPageNo(DEFAULT_PAGE_NO);
        condition.setPageRowNum(DEFAULT_ROW_NUM);
        condition.setSystemAdmin(true);

        List<Site> actual = dao.find(condition);

        // 取得したオブジェクトと、Excelファイルに定義済の期待値を比較する
        assertNotNull(actual);
        assertDataSetEquals(newDataSet("SiteDaoImplTest_testFindName_expected.xls"), actual);

    }

    /**
     * {@link SiteDaoImpl#find} のテストケース. ProjectAdminの場合
     * @throws Exception
     */
    @Test
    public void testFindProjectAdmin() throws Exception {
        // テストに必要なデータを作成
        SearchSiteCondition condition = new SearchSiteCondition();

        condition.setProjectId("PJ1");
        condition.setPageNo(DEFAULT_PAGE_NO);
        condition.setPageRowNum(DEFAULT_ROW_NUM);
        condition.setProjectAdmin(true);

        List<Site> actual = dao.find(condition);

        // 取得したオブジェクトと、Excelファイルに定義済の期待値を比較する
        assertNotNull(actual);
        assertDataSetEquals(newDataSet("SiteDaoImplTest_testFind_expected.xls"), actual);

    }

    /**
     * {@link SiteDaoImpl#find} のテストケース. GroupAdmin
     * @throws Exception
     */
    @Test
    public void testFindGroupAdmin() throws Exception {
        // テストに必要なデータを作成
        SearchSiteCondition condition = new SearchSiteCondition();

        condition.setProjectId("PJ2");
        condition.setPageNo(DEFAULT_PAGE_NO);
        condition.setPageRowNum(DEFAULT_ROW_NUM);
        condition.setGroupAdmin(true);

        User user = new User();
        user.setEmpNo("ZZA01");
        user.setSecurityLevel(SystemConfig.getValue(Constants.KEY_SECURITY_LEVEL_GROUP_ADMIN));
        condition.setSearchUser(user);

        List<Site> actual = dao.find(condition);

        // 取得したオブジェクトと、Excelファイルに定義済の期待値を比較する
        assertNotNull(actual);
        assertDataSetEquals(newDataSet("SiteDaoImplTest_testFindGroupAdmin_expected.xls"),
            actual);

    }

    /**
     * {@link SiteDaoImpl#find} のテストケース. GroupAdmin
     * @throws Exception
     */
    @Test
    public void testFindCodeGroupAdmin() throws Exception {
        // テストに必要なデータを作成
        SearchSiteCondition condition = new SearchSiteCondition();

        condition.setProjectId("PJ2");
        condition.setSiteCd("Y");
        condition.setPageNo(DEFAULT_PAGE_NO);
        condition.setPageRowNum(DEFAULT_ROW_NUM);
        condition.setGroupAdmin(true);

        User user = new User();
        user.setEmpNo("ZZA01");
        user.setSecurityLevel(SystemConfig.getValue(Constants.KEY_SECURITY_LEVEL_GROUP_ADMIN));
        condition.setSearchUser(user);

        List<Site> actual = dao.find(condition);

        // 取得したオブジェクトと、Excelファイルに定義済の期待値を比較する
        assertNotNull(actual);
        assertDataSetEquals(newDataSet("SiteDaoImplTest_testFindCodeGroupAdmin_expected.xls"),
            actual);

    }

    /**
     * {@link SiteDaoImpl#find} のテストケース. GroupAdmin
     * @throws Exception
     */
    @Test
    public void testFindNameGroupAdmin() throws Exception {
        // テストに必要なデータを作成
        SearchSiteCondition condition = new SearchSiteCondition();

        condition.setProjectId("PJ2");
        condition.setName("Y");
        condition.setPageNo(DEFAULT_PAGE_NO);
        condition.setPageRowNum(DEFAULT_ROW_NUM);
        condition.setGroupAdmin(true);

        User user = new User();
        user.setEmpNo("ZZA01");
        user.setSecurityLevel(SystemConfig.getValue(Constants.KEY_SECURITY_LEVEL_GROUP_ADMIN));
        condition.setSearchUser(user);

        List<Site> actual = dao.find(condition);

        // 取得したオブジェクトと、Excelファイルに定義済の期待値を比較する
        assertNotNull(actual);
        assertDataSetEquals(newDataSet("SiteDaoImplTest_testFindNameGroupAdmin_expected.xls"),
            actual);

    }







    /**
     * {@link SiteDaoImpl#count} のテストケース. SystemAdmin
     * @throws Exception
     */
    @Test
    public void testCountSystemAdmin() throws Exception {
        // テストに必要なデータを作成
        SearchSiteCondition condition = new SearchSiteCondition();

        condition.setProjectId("PJ1");
        condition.setPageNo(DEFAULT_PAGE_NO);
        condition.setPageRowNum(DEFAULT_ROW_NUM);
        condition.setSystemAdmin(true);

        User user = new User();
        user.setEmpNo("ZZA01");
        user.setSecurityLevel(SystemConfig.getValue(Constants.KEY_SECURITY_LEVEL_GROUP_ADMIN));
        condition.setSearchUser(user);

        int actual = dao.count(condition);
        assertEquals(10, actual);

    }

    /**
     * {@link SiteDaoImpl#count} のテストケース. ProjectAdmin
     * @throws Exception
     */
    @Test
    public void testCountProjectAdmin() throws Exception {
        // テストに必要なデータを作成
        SearchSiteCondition condition = new SearchSiteCondition();

        condition.setProjectId("PJ1");
        condition.setPageNo(DEFAULT_PAGE_NO);
        condition.setPageRowNum(DEFAULT_ROW_NUM);
        condition.setProjectAdmin(true);

        User user = new User();
        user.setEmpNo("ZZA01");
        user.setSecurityLevel(SystemConfig.getValue(Constants.KEY_SECURITY_LEVEL_GROUP_ADMIN));
        condition.setSearchUser(user);

        int actual = dao.count(condition);
        assertEquals(10, actual);

    }

    /**
     * {@link SiteDaoImpl#count} のテストケース. GroupAdmin
     * @throws Exception
     */
    @Test
    public void testCountGroupAdmin() throws Exception {
        // テストに必要なデータを作成
        SearchSiteCondition condition = new SearchSiteCondition();

        condition.setProjectId("PJ2");
        condition.setPageNo(DEFAULT_PAGE_NO);
        condition.setPageRowNum(DEFAULT_ROW_NUM);
        condition.setGroupAdmin(true);

        User user = new User();
        user.setEmpNo("ZZA01");
        user.setSecurityLevel(SystemConfig.getValue(Constants.KEY_SECURITY_LEVEL_GROUP_ADMIN));
        condition.setSearchUser(user);

        int actual = dao.count(condition);
        assertEquals(1, actual);

    }

    /**
     * {@link SiteDaoImpl#findById}のテストケース
     * @throws Exception
     */
    @Test
    public void testFindById() throws Exception {
        // テストに必要なデータを作成
        Site actual = dao.findById(8L);
        // 取得したオブジェクトと、Excelファイルに定義済の期待値を比較する
        assertNotNull(actual);
        assertDataSetEquals(newDataSet("SiteDaoImplTest_testFindById_expected.xls"), actual);
    }

    /**
     * {@link SiteDaoImpl#create}のテストケース
     * @throws Exception
     */
    @Test
    public void testCreate() throws Exception {
        // テストに必要なデータを作成
        Site site = new Site();
        site.setSiteCd("TKY");
        site.setName("Tokio");
        site.setProjectId("PJ1");
        User loginUser = new User();
        loginUser.setEmpNo("ZZA02");
        loginUser.setNameE("Tetsu Aoki");
        site.setCreatedBy(loginUser);
        site.setUpdatedBy(loginUser);

        Long id = null;

        while (true) {
            try {
                id = dao.create(site);
                break;
            } catch (KeyDuplicateException e) {
                continue;
            }
        }

        // 登録したデータを取得
        Site actual = dao.findById(id);

        assertNotNull(id);
        assertEquals(site.getSiteCd(), actual.getSiteCd());
        assertEquals(site.getName(), actual.getName());
        assertEquals(site.getProjectId(), actual.getProjectId());
        assertEquals("Test Project1", actual.getProjectNameE());
        assertEquals(loginUser.getEmpNo(), actual.getCreatedBy().getEmpNo());
        assertNotNull(actual.getUpdatedAt());
        assertEquals(loginUser.getEmpNo(), actual.getUpdatedBy().getEmpNo());
        assertNotNull(actual.getUpdatedAt());
        assertEquals(String.valueOf(1L), String.valueOf(actual.getVersionNo()));
        assertEquals(String.valueOf(0L), String.valueOf(actual.getDeleteNo()));
    }

    /**
     * {@link SiteDaoImpl#create}のテストケース プロジェクトIDと、拠点コードが既にあるデータと同じ
     * @throws Exception
     */
    @Test(expected = KeyDuplicateException.class)
    public void testCreateKeyDuplicateException() throws Exception {
        // テストに必要なデータを作成
        Site site = new Site();
        site.setSiteCd("YOC");
        site.setName("Yocohama");
        site.setProjectId("PJ1");
        User loginUser = new User();
        loginUser.setEmpNo("ZZA02");
        loginUser.setNameE("Tetsu Aoki");
        site.setCreatedBy(loginUser);
        site.setUpdatedBy(loginUser);
        dao.create(site);
        fail("例外が発生していない");
    }

    /**
     * {@link SiteDaoImpl#update}のテストケース
     * @throws Exception
     */
    @Test
    public void testUpdate() throws Exception {
        // テストに必要なデータを作成
        Site site = new Site();
        site.setId(1L);
        site.setSiteCd("TKY");
        site.setName("Tokio");
        User loginUser = new User();
        loginUser.setEmpNo("ZZA02");
        loginUser.setNameE("Tetsu Aoki");
        site.setCreatedBy(loginUser);
        site.setUpdatedBy(loginUser);

        // 更新前のデータを取得
        Site before = dao.findById(site.getId());

        site.setVersionNo(before.getVersionNo());
        // 更新
        dao.update(site);

        // 更新後のデータを取得
        Site actual = dao.findById(site.getId());

        assertEquals(before.getId(), actual.getId());
        assertEquals(before.getProjectId(), actual.getProjectId());
        assertFalse(before.getSiteCd().equals(actual.getSiteCd()));
        assertEquals(site.getSiteCd(), actual.getSiteCd());
        assertFalse(before.getName().equals(actual.getName()));
        assertEquals(site.getName(), actual.getName());
        assertEquals(before.getCreatedBy().getEmpNo(), actual.getCreatedBy().getEmpNo());
        assertEquals(before.getCreatedAt(), actual.getCreatedAt());
        assertFalse(before.getUpdatedBy().getEmpNo().equals(actual.getUpdatedBy().getEmpNo()));
        assertEquals(site.getUpdatedBy().getEmpNo(), actual.getUpdatedBy().getEmpNo());
        assertFalse(before.getUpdatedAt().toString().equals(actual.getUpdatedAt().toString()));
        assertEquals(String.valueOf(before.getVersionNo() + 1), String.valueOf(actual
            .getVersionNo()));
        assertEquals(String.valueOf(before.getDeleteNo()), String.valueOf(actual.getDeleteNo()));

    }

    /**
     * {@link SiteDaoImpl#update}のテストケース StaleRecordExceptionが発生
     * @throws Exception
     */
    @Test(expected = StaleRecordException.class)
    public void testUpdateStaleRecordException() throws Exception {
        // テストに必要なデータを作成
        Site site = new Site();
        site.setId(1L);
        site.setSiteCd("TKY");
        site.setName("Tokio");
        User loginUser = new User();
        loginUser.setEmpNo("ZZA02");
        loginUser.setNameE("Tetsu Aoki");
        site.setCreatedBy(loginUser);
        site.setUpdatedBy(loginUser);

        // 更新
        dao.update(site);
    }

    /**
     * {@link SiteDaoImpl#delete}のテストケース
     * @throws Exception
     */
    @Test
    public void testDelete() throws Exception {
        // テストに必要なデータを作成
        // 削除前のデータを取得
        Site before = dao.findById(1L);

        Site site = new Site();
        site.setId(before.getId());
        site.setSiteCd(before.getSiteCd());
        site.setName(before.getName());
        site.setProjectId(before.getProjectId());
        site.setCreatedBy(before.getCreatedBy());

        User loginUser = new User();
        loginUser.setEmpNo("ZZA04");
        loginUser.setNameE("Tomoko Okada");
        site.setUpdatedBy(loginUser);
        site.setVersionNo(before.getVersionNo());
        site.setDeleteNo(before.getDeleteNo());

        dao.delete(site);

        // 削除後のデータを取得する
        String sql = "select * from site where id = 1 and delete_no != 0";
        ITable actual = getConnection().createQueryTable("actual", sql);

        assertEquals(before.getId().toString(), actual.getValue(0, "id").toString());
        assertEquals(before.getProjectId(), actual.getValue(0, "project_id").toString());
        assertEquals(before.getSiteCd(), actual.getValue(0, "site_cd").toString());
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
     * {@link SiteDaoImpl#delete}のテストケース 排他制御
     * @throws Exception
     */
    @Test(expected = StaleRecordException.class)
    public void testDeleteVersionNoDiff() throws Exception {
        // テストに必要なデータを作成
        // 削除前のデータを取得
        Site before = dao.findById(1L);

        Site site = new Site();
        site.setId(before.getId());
        site.setSiteCd(before.getSiteCd());
        site.setName(before.getName());
        site.setProjectId(before.getProjectId());
        site.setCreatedBy(before.getCreatedBy());

        User loginUser = new User();
        loginUser.setEmpNo("ZZA04");
        loginUser.setNameE("Tomoko Okada");
        site.setUpdatedBy(loginUser);
        site.setVersionNo(99L);
        site.setDeleteNo(before.getDeleteNo());

        dao.delete(site);
        fail("例外が発生していない");

    }

    /**
     * {@link SiteDao#countCheck}のテストケース
     * 条件を全て指定
     * @throws Exception
     */
    @Test
    public void testCountCheckAll() throws Exception {
        SearchSiteCondition condition = new SearchSiteCondition();
        condition.setSiteCd("YOC");
        condition.setProjectId("PJ2");
        condition.setId(2L);

        int actual = dao.countCheck(condition);
        assertTrue(0 == actual);
    }

    /**
     * {@link SiteDao#countCheck}のテストケース
     * 拠点コードが条件
     * @throws Exception
     */
    @Test
    public void testCountCheckSiteCd() throws Exception {
        SearchSiteCondition condition = new SearchSiteCondition();
        condition.setSiteCd("YOC");

        int actual = dao.countCheck(condition);
        assertTrue(3 == actual);
    }

    /**
     * {@link SiteDao#countCheck}のテストケース
     * プロジェクトIDが条件
     * @throws Exception
     */
    @Test
    public void testCountCheckProjectId() throws Exception {
        SearchSiteCondition condition = new SearchSiteCondition();
        condition.setProjectId("PJ1");

        int actual = dao.countCheck(condition);
        assertTrue(10 == actual);
    }

    /**
     * {@link SiteDao#countCheck}のテストケース
     * IDが条件（指定したID以外）
     * @throws Exception
     */
    @Test
    public void testCountCheckId() throws Exception {
        SearchSiteCondition condition = new SearchSiteCondition();
        condition.setId(1L);

        int actual = dao.countCheck(condition);
        assertTrue(11 == actual);
    }

}
