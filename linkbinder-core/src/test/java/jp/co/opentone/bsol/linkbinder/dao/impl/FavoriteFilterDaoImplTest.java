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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.ITable;
import org.junit.Test;

import jp.co.opentone.bsol.framework.core.dao.KeyDuplicateException;
import jp.co.opentone.bsol.framework.core.dao.RecordNotFoundException;
import jp.co.opentone.bsol.framework.core.dao.StaleRecordException;
import jp.co.opentone.bsol.framework.core.extension.ibatis.DBValue;
import jp.co.opentone.bsol.framework.core.util.JSONUtil;
import jp.co.opentone.bsol.framework.test.AbstractDaoTestCase;
import jp.co.opentone.bsol.linkbinder.dto.FavoriteFilter;
import jp.co.opentone.bsol.linkbinder.dto.Project;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponCondition;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchFavoriteFilterCondition;

/**
 * FavoriteFilterDaoImpl のテストケース.
 * @author opentone
 */
public class FavoriteFilterDaoImplTest extends AbstractDaoTestCase {
    /**
     * テスト対象.
     */
    @Resource
    private FavoriteFilterDaoImpl dao;

    private static final int DEFAULT_PAGE_NO = 1;

    private static final int DEFAULT_ROW_NUM = 10;

    /**
     * {@link FavoriteFilterDaoImpl#find(SearchFavoriteFilterCondition)}のテストケース.
     * 1件取得するケース。（FavoriteName最大長のデータの取得チェックを兼ねる）。
     * FavoriteFilterDaoImplTest.xls 参照。
     * @throws Exception
     */
    @Test
    public void testFindNormalToGetOneRecord() throws Exception {
        // prepare
        Project project = new Project();
        project.setProjectId("PJ_FIND");
        User user = new User();
        user.setEmpNo("ZZA01");
        SearchFavoriteFilterCondition condition = new SearchFavoriteFilterCondition(project, user);
        condition.setPageNo(DEFAULT_PAGE_NO);
        condition.setPageRowNum(DEFAULT_ROW_NUM);

        // execute
        List<FavoriteFilter> filterList = dao.find(condition);

        // verify
        // 取得したオブジェクトと、Excelファイルに定義済の期待値を比較する
        assertNotNull(filterList);
        assertTrue("filter list size must be more than zero.", filterList.size() > 0);
        assertDataSetEquals(newDataSet("FavoriteFilterDaoImplTest_testFindNormal01_expected.xls"),
                filterList);
    }
    /**
     * {@link FavoriteFilterDaoImpl#find(SearchFavoriteFilterCondition)}のテストケース.
     * 複数件取得するテスト。
     * FavoriteFilterDaoImplTest.xls 参照。
     * @throws Exception
     */
    @Test
    public void testFindNormalToGetMultiRecords() throws Exception {
        // prepare
        Project project = new Project();
        project.setProjectId("PJ_FIND");
        User user = new User();
        user.setEmpNo("ZZA02");
        SearchFavoriteFilterCondition condition = new SearchFavoriteFilterCondition(project, user);
        condition.setPageNo(DEFAULT_PAGE_NO);
        condition.setPageRowNum(DEFAULT_ROW_NUM);

        // execute
        List<FavoriteFilter> filterList = dao.find(condition);

        // verify
        // 取得したオブジェクトと、Excelファイルに定義済の期待値を比較する
        assertNotNull(filterList);
        assertTrue("filter list size must be more than zero.", filterList.size() > 0);
        assertDataSetEquals(newDataSet("FavoriteFilterDaoImplTest_testFindNormal02_expected.xls"),
                filterList);
    }
    /**
     * {@link FavoriteFilterDaoImpl#find(SearchFavoriteFilterCondition)}のテストケース.
     * 複数件取得するテスト。
     * 削除済みは取得しない事。
     * FavoriteFilterDaoImplTest.xls 参照。
     * @throws Exception
     */
    @Test
    public void testFindNormalToGetMultiRecordsExceptDeleted() throws Exception {
        // prepare
        Project project = new Project();
        project.setProjectId("PJ_FIND");
        User user = new User();
        user.setEmpNo("ZZA03");
        SearchFavoriteFilterCondition condition = new SearchFavoriteFilterCondition(project, user);
        condition.setPageNo(DEFAULT_PAGE_NO);
        condition.setPageRowNum(DEFAULT_ROW_NUM);

        // execute
        List<FavoriteFilter> filterList = dao.find(condition);

        // verify
        // 取得したオブジェクトと、Excelファイルに定義済の期待値を比較する
        assertNotNull(filterList);
        assertTrue("filter list size must be more than zero.", filterList.size() > 0);
        assertDataSetEquals(newDataSet("FavoriteFilterDaoImplTest_testFindNormal03_expected.xls"),
                filterList);
    }
    /**
     * {@link FavoriteFilterDaoImpl#find(SearchFavoriteFilterCondition)}のテストケース.
     * 取得しないテスト。
     * プロジェクトIDなし＋従業員NOあり。
     * FavoriteFilterDaoImplTest.xls 参照。
     * @throws Exception
     */
    @Test
    public void testFindNormalToGetNothingForNotExistsProjectId() throws Exception {
        // prepare
        Project project = new Project();
        project.setProjectId("DUMMY");
        User user = new User();
        user.setEmpNo("ZZA01");
        SearchFavoriteFilterCondition condition = new SearchFavoriteFilterCondition(project, user);
        condition.setPageNo(DEFAULT_PAGE_NO);
        condition.setPageRowNum(DEFAULT_ROW_NUM);

        // execute
        List<FavoriteFilter> filterList = dao.find(condition);

        // verify
        // 取得したオブジェクトと、Excelファイルに定義済の期待値を比較する
        assertNotNull(filterList);
        assertTrue("filter list size must be zero.", filterList.size() == 0);
    }
    /**
     * {@link FavoriteFilterDaoImpl#find(SearchFavoriteFilterCondition)}のテストケース.
     * 取得しないテスト。
     * プロジェクトIDあり＋従業員NOなし。
     * FavoriteFilterDaoImplTest.xls 参照。
     * @throws Exception
     */
    @Test
    public void testFindNormalToGetNothingForNotExistsEmpNo() throws Exception {
        // prepare
        Project project = new Project();
        project.setProjectId("PJ_FIND");
        User user = new User();
        user.setEmpNo("DUMMY");
        SearchFavoriteFilterCondition condition = new SearchFavoriteFilterCondition(project, user);
        condition.setPageNo(DEFAULT_PAGE_NO);
        condition.setPageRowNum(DEFAULT_ROW_NUM);

        // execute
        List<FavoriteFilter> filterList = dao.find(condition);

        // verify
        // 取得したオブジェクトと、Excelファイルに定義済の期待値を比較する
        assertNotNull(filterList);
        assertTrue("filter list size must be zero.", filterList.size() == 0);
    }
    /**
     * {@link FavoriteFilterDaoImpl#find(SearchFavoriteFilterCondition)}のテストケース.
     * 取得しないテスト。
     * プロジェクトIDなし＋従業員NOなし。
     * FavoriteFilterDaoImplTest.xls 参照。     * @throws Exception
     */
    @Test
    public void testFindNormalToGetNoghingForNotExistsProjectIdAndEmpNo() throws Exception {
        // prepare
        Project project = new Project();
        project.setProjectId("DUMMY");
        User user = new User();
        user.setEmpNo("DUMMY");
        SearchFavoriteFilterCondition condition = new SearchFavoriteFilterCondition(project, user);
        condition.setPageNo(DEFAULT_PAGE_NO);
        condition.setPageRowNum(DEFAULT_ROW_NUM);

        // execute
        List<FavoriteFilter> filterList = dao.find(condition);

        // verify
        // 取得したオブジェクトと、Excelファイルに定義済の期待値を比較する
        assertNotNull(filterList);
        assertTrue("filter list size must be zero.", filterList.size() == 0);
    }
    /**
     * {@link FavoriteFilterDaoImpl#find(SearchFavoriteFilterCondition)}のテストケース.
     * 取得しないテスト。
     * 全て削除済み。
     * FavoriteFilterDaoImplTest.xls 参照。     * @throws Exception
     */
    @Test
    public void testFindNormalToGetNogthingForNotExistsActiveRecords() throws Exception {
        // prepare
        Project project = new Project();
        project.setProjectId("PJ_FIND");
        User user = new User();
        user.setEmpNo("ZZA04");
        SearchFavoriteFilterCondition condition = new SearchFavoriteFilterCondition(project, user);
        condition.setPageNo(DEFAULT_PAGE_NO);
        condition.setPageRowNum(DEFAULT_ROW_NUM);

        // execute
        List<FavoriteFilter> filterList = dao.find(condition);

        // verify
        // 取得したオブジェクトと、Excelファイルに定義済の期待値を比較する
        assertNotNull(filterList);
        assertTrue("filter list size must be zero.", filterList.size() == 0);
    }

    /**
     * {@link FavoriteFilterDaoImpl#findById}のテストケース.
     * IDを指定して取得するテスト。
     * @throws Exception
     */
    @Test
    public void testFindById() throws Exception {
        FavoriteFilter f = dao.findById(13L);

        assertNotNull(f);
        assertDataSetEquals(newDataSet("FavoriteFilterDaoImplTest_testFindById01_expected.xls"), f);
    }
    /**
     * {@link FavoriteFilterDaoImpl#findById}のテストケース.
     * IDを指定して取得するテスト。
     * 削除済みでも取得する事を確認。
     * @throws Exception
     */
    @Test(expected=RecordNotFoundException.class)
    public void testFindByIdToGetNothingForNotExistsActiveRecords() throws Exception {
        dao.findById(14L);

        fail("record must be not found.");
    }

    /**
     * {@link FavoriteFilterDaoImpl#create}のテストケース.
     * 1件作成するテスト。
     * @throws Exception
     */
    @Test
    public void testCreateRecord() throws Exception {
        // テストに必要なデータを作成
        FavoriteFilter f = new FavoriteFilter();
        User loginUser = new User();
        loginUser.setEmpNo("ZZA01");
        loginUser.setNameE("Yamada Taro");

        f.setProjectId("TEST");
        f.setUser(loginUser);
        f.setFavoriteName("dummy name");
        SearchCorresponCondition cond = new SearchCorresponCondition();
        cond.setCorresponNo("1");
        f.setSearchConditionsToJson(cond);
        f.setCreatedBy(loginUser);
        f.setCreatedAt(DBValue.DATE_NULL);
        f.setUpdatedBy(loginUser);
        f.setCreatedAt(DBValue.DATE_NULL);

        // 登録
        Long id = null;
        while (true) {
            try {
                id = dao.create(f);
                break;
            } catch (KeyDuplicateException e) {
                continue;
            }
        }
        assertNotNull(id);

        FavoriteFilter actual = dao.findById(id);

        assertEquals(id, actual.getId());
        assertEquals(f.getProjectId(), actual.getProjectId());
        assertNotNull(actual.getUser());
        assertEquals(f.getUser().getEmpNo(), actual.getUser().getEmpNo());
        assertEquals(f.getUser().getNameE(), actual.getUser().getNameE());
        assertEquals(f.getFavoriteName(), actual.getFavoriteName());
        assertEquals(f.getSearchConditions(), actual.getSearchConditions());
        assertEquals(f.getCreatedBy().toString(), actual.getCreatedBy().toString());
        assertNotNull(actual.getCreatedAt());
        assertEquals(f.getUpdatedBy().toString(), actual.getUpdatedBy().toString());
        assertNotNull(actual.getUpdatedAt());
    }
    /**
     * {@link FavoriteFilterDaoImpl#create}のテストケース.
     * 重複IDを指定しても、別IDで作成する事を確認。（ID自動採番）
     * エラー（DuplicateKeyException）を返す。
     * @throws Exception
     */
    @Test
    public void testCreateRecordDespiteSettingDuplicateKey() throws Exception {
        // テストに必要なデータを作成
        FavoriteFilter f = new FavoriteFilter();
        User loginUser = new User();
        loginUser.setEmpNo("ZZA01");
        loginUser.setNameE("Yamada Taro");

        Long id = 15L;
        f.setId(id);
        f.setProjectId("DUPLI_TEST");
        f.setUser(loginUser);
        f.setFavoriteName("bookmark01");
        SearchCorresponCondition cond = new SearchCorresponCondition();
        cond.setCorresponNo("1");
        f.setSearchConditionsToJson(cond);
        f.setCreatedBy(loginUser);
        f.setCreatedAt(DBValue.DATE_NULL);
        f.setUpdatedBy(loginUser);
        f.setCreatedAt(DBValue.DATE_NULL);

        // 登録
        Long result = dao.create(f);

        assertFalse(result == id);
    }
    /**
     * {@link FavoriteFilterDaoImpl#create}のテストケース.
     * 不正データを作成するテスト。
     * 名称がNULL。
     * エラー（KeyDuplicateException）を返す。
     * @throws Exception
     */
    @Test(expected=KeyDuplicateException.class)
    public void testCreateNoRecordForNullInsert01() throws Exception {
        // テストに必要なデータを作成
        FavoriteFilter f = new FavoriteFilter();
        User loginUser = new User();
        loginUser.setEmpNo("ZZA01");
        loginUser.setNameE("Yamada Taro");

        f.setProjectId("NULL_TEST");
        f.setUser(loginUser);
        // 名称をNULL
        f.setFavoriteName(null);
        SearchCorresponCondition cond = new SearchCorresponCondition();
        cond.setCorresponNo("1");
        f.setSearchConditionsToJson(cond);
        f.setCreatedBy(loginUser);
        f.setCreatedAt(DBValue.DATE_NULL);
        f.setUpdatedBy(loginUser);
        f.setCreatedAt(DBValue.DATE_NULL);

        // 登録
        Long result = dao.create(f);

        fail("KeyDuplicateException must be occured. But " + result + " record created.");
    }
    /**
     * {@link FavoriteFilterDaoImpl#create}のテストケース.
     * 不正データを作成するテスト。
     * 条件がNULL。
     * エラー（KeyDuplicateException）を返す。
     * @throws Exception
     */
    @Test(expected=KeyDuplicateException.class)
    public void testCreateNoRecordForNullInsert02() throws Exception {
        // テストに必要なデータを作成
        FavoriteFilter f = new FavoriteFilter();
        User loginUser = new User();
        loginUser.setEmpNo("ZZA01");
        loginUser.setNameE("Yamada Taro");

        f.setProjectId("DUPLI_TEST");
        f.setUser(loginUser);
        // 名称をNULL
        f.setFavoriteName("dummy name");
        f.setSearchConditions(null);
        f.setCreatedBy(loginUser);
        f.setCreatedAt(DBValue.DATE_NULL);
        f.setUpdatedBy(loginUser);
        f.setCreatedAt(DBValue.DATE_NULL);

        // 登録
        dao.create(f);

        fail("KeyDuplicateException must be occured.");
    }

    /**
     * {@link FavoriteFilterDaoImpl#update}のテストケース}
     * 1件更新するテスト。
     * 名称、条件を更新する。
     * @throws Exception
     */
    @Test
    public void testUpdateNameCondition() throws Exception {
        // テストに必要なデータを作成
        Long id = 1L;

        FavoriteFilter f = new FavoriteFilter();
        User loginUser = new User();
        loginUser.setEmpNo("ZZA01");
        loginUser.setNameE("Taro Yamada");

        f.setId(id);
        f.setProjectId("TEST_PJ");
        f.setFavoriteName("更新検査 updated for ut.");
        SearchCorresponCondition cond = new SearchCorresponCondition();
        cond.setCorresponNo("1");
        f.setSearchConditionsToJson(cond);

        // 更新前データ
        FavoriteFilter actualBefore = dao.findById(id);

        f.setUpdatedBy(loginUser);
        f.setVersionNo(actualBefore.getVersionNo());

        // execute
        Integer updateRecord = dao.update(f);
        assertNotNull(updateRecord);
        assertTrue(updateRecord == 1);

        // verify
        FavoriteFilter actualAfter = dao.findById(id);

        // 対象項目以外は変更していない事
        assertEquals(actualBefore.getCreatedBy().toString(), actualAfter.getCreatedBy().toString());
        assertEquals(actualBefore.getCreatedAt(), actualAfter.getCreatedAt());
        assertEquals(actualBefore.getId(), actualAfter.getId());
        assertEquals(actualBefore.getProjectId(), actualAfter.getProjectId());
        assertEquals(actualBefore.getUser().getEmpNo(), actualAfter.getUser().getEmpNo());
        assertEquals(actualBefore.getUser().getNameE(), actualAfter.getUser().getNameE());

        // 対象項目が更新されている事
        assertEquals(f.getFavoriteName(), actualAfter.getFavoriteName());
        assertEquals(f.getSearchConditions(), actualAfter.getSearchConditions());
        assertEquals(loginUser.getEmpNo(), actualAfter.getUpdatedBy().getEmpNo());
        assertNotNull(actualAfter.getUpdatedAt());
        assertEquals(Long.valueOf(actualBefore.getVersionNo() + 1), actualAfter.getVersionNo());
    }
    /**
     * {@link FavoriteFilterDaoImpl#update}のテストケース}
     * 1件更新するテスト。
     * 同じプロジェクトID、従業員NOのデータがある場合、該当のデータのみを更新している事を確認。
     * @throws Exception
     */
    @Test
    public void testUpdateTheTargetRecordExactly() throws Exception {
        // 同じプロジェクトID、従業員NOのデータがあるIDを指定
        Long target = 17L;

        // prepare
        User loginUser = new User();
        loginUser.setEmpNo("ZZA01");
        loginUser.setNameE("Taro Yamada");
        String projectId = "PJ_UPDATE00";
        String favoriteName = "UPDATED NAME!";
        SearchCorresponCondition cond = new SearchCorresponCondition();
        cond.setCorresponNo("1");

        FavoriteFilter f = new FavoriteFilter();

        f.setId(target);
        f.setProjectId(projectId);
        f.setFavoriteName(favoriteName);
        f.setSearchConditionsToJson(cond);
        f.setUpdatedBy(loginUser);

        // 更新前データ
        FavoriteFilter before = dao.findById(target);
        f.setVersionNo(before.getVersionNo());

        // 同じプロジェクトID、従業員NOの別データ
        FavoriteFilter data1 = dao.findById(18L);
        FavoriteFilter data2 = dao.findById(19L);

        // execute
        Integer updateRecord = dao.update(f);
        assertNotNull(updateRecord);
        assertTrue(updateRecord == 1);

        // verify
        FavoriteFilter after = dao.findById(target);

        // 対象項目以外は更新していない事
        assertEquals(before.getCreatedBy().toString(), after.getCreatedBy().toString());
        assertEquals(before.getCreatedAt(), after.getCreatedAt());
        assertEquals(before.getId(), after.getId());
        assertEquals(before.getProjectId(), after.getProjectId());
        assertEquals(before.getUser().getEmpNo(), after.getUser().getEmpNo());
        assertEquals(before.getUser().getNameE(), after.getUser().getNameE());

        // 対象項目が更新されている事
        assertEquals(favoriteName, after.getFavoriteName());
        // NULLの扱いが異なるので、JSON変換をかまして検証。
//        assertEquals(cond, after.getSearchConditionsAsObject());
        assertEquals(JSONUtil.encode(cond), after.getSearchConditions());
        assertEquals(loginUser.getEmpNo(), after.getUpdatedBy().getEmpNo());
        assertNotNull(after.getUpdatedAt());
        assertTrue(after.getUpdatedAt().compareTo(before.getUpdatedAt()) > 0);
        assertEquals(Long.valueOf(before.getVersionNo() + 1), after.getVersionNo());

        // 同じプロジェクトID、従業員NOの別データを更新していない事
        assertFalse(favoriteName.equals(data1.getFavoriteName()));
        assertFalse(cond.equals(data1.getSearchConditions()));
        assertTrue("updated_at of data1 is [" + data1.getUpdatedAt().toString()
                + "]: of after is [" + after.getUpdatedAt().toString() + "]",
                data1.getUpdatedAt().compareTo(after.getUpdatedAt()) <= 0);
        assertFalse(favoriteName.equals(data2.getFavoriteName()));
        assertFalse(cond.equals(data2.getSearchConditions()));
        assertTrue("updated_at of data2 is [" + data2.getUpdatedAt().toString()
                + "]: of after is [" + after.getUpdatedAt().toString() + "]",
                data1.getUpdatedAt().compareTo(after.getUpdatedAt()) <= 0);
    }
    /**
     * {@link FavoriteFilterDaoImpl#update}のテストケース}
     * 1件更新するテスト。
     * 名称のみ更新する。
     * @throws Exception
     */
    @Test
    public void testUpdateOnlyFavoriteName() throws Exception {
        // IDを指定
        Long target = 16L;

        // prepare
        User loginUser = new User();
        loginUser.setEmpNo("ZZA01");
        loginUser.setNameE("Taro Yamada");
        String favoriteName = "UPDATED NAME!";

        // 更新前データ
        FavoriteFilter before = dao.findById(target);

        // execute
        FavoriteFilter f = new FavoriteFilter();
        f.setId(target);
        f.setFavoriteName(favoriteName);
        f.setUpdatedBy(loginUser);
        f.setVersionNo(before.getVersionNo());
        Integer updateRecord = dao.update(f);


        // verify
        assertNotNull(updateRecord);
        assertTrue(updateRecord == 1);

        FavoriteFilter after = dao.findById(target);

        // 対象項目以外は更新していない事
        assertEquals(before.getCreatedBy().toString(), after.getCreatedBy().toString());
        assertEquals(before.getCreatedAt(), after.getCreatedAt());
        assertEquals(before.getId(), after.getId());
        assertEquals(before.getProjectId(), after.getProjectId());
        assertEquals(before.getUser().getEmpNo(), after.getUser().getEmpNo());
        assertEquals(before.getUser().getNameE(), after.getUser().getNameE());
        assertEquals(before.getSearchConditions(), after.getSearchConditions());

        // 対象項目が更新されている事
        assertEquals(favoriteName, after.getFavoriteName());
        assertEquals(loginUser.getEmpNo(), after.getUpdatedBy().getEmpNo());
        assertNotNull(after.getUpdatedAt());
        assertTrue(after.getUpdatedAt().compareTo(before.getUpdatedAt()) > 0);
        assertEquals(Long.valueOf(before.getVersionNo() + 1), after.getVersionNo());
    }
    /**
     * {@link FavoriteFilterDaoImpl#update}のテストケース}
     * 1件更新するテスト。
     * 条件のみ更新する。
     * @throws Exception
     */
    @Test
    public void testUpdateOnlySearchConditions() throws Exception {
        // IDを指定
        Long id = 16L;

        // prepare
        User loginUser = new User();
        loginUser.setEmpNo("ZZA01");
        loginUser.setNameE("Taro Yamada");
        SearchCorresponCondition cond = new SearchCorresponCondition();
        cond.setCorresponNo("1");

        // 更新前データ
        FavoriteFilter before = dao.findById(id);

        // execute
        FavoriteFilter f = new FavoriteFilter();
        f.setId(id);
        f.setSearchConditionsToJson(cond);
        f.setUpdatedBy(loginUser);
        f.setVersionNo(before.getVersionNo());
        Integer updateRecord = dao.update(f);


        // verify
        assertNotNull(updateRecord);
        assertTrue(updateRecord == 1);

        FavoriteFilter after = dao.findById(id);

        // 対象項目以外は更新していない事
        assertEquals(before.getCreatedBy().toString(), after.getCreatedBy().toString());
        assertEquals(before.getCreatedAt(), after.getCreatedAt());
        assertEquals(before.getId(), after.getId());
        assertEquals(before.getProjectId(), after.getProjectId());
        assertEquals(before.getUser().getEmpNo(), after.getUser().getEmpNo());
        assertEquals(before.getUser().getNameE(), after.getUser().getNameE());
        assertEquals(before.getFavoriteName(), after.getFavoriteName());

        // 対象項目が更新されている事
        // NULLの扱いが異なるので、JSON変換をかまして検証。
//      assertEquals(cond, after.getSearchConditionsAsObject());
        assertEquals(JSONUtil.encode(cond), after.getSearchConditions());
        assertEquals(loginUser.getEmpNo(), after.getUpdatedBy().getEmpNo());
        assertNotNull(after.getUpdatedAt());
        assertTrue(after.getUpdatedAt().compareTo(before.getUpdatedAt()) > 0);
        assertEquals(Long.valueOf(before.getVersionNo() + 1), after.getVersionNo());
    }
    /**
     * {@link FavoriteFilterDaoImpl#update}のテストケース}
     * 更新エラーのテスト。
     * 削除されたデータを更新する。
     * エラー（StaleRecordException）を返す。
     * @throws Exception
     */
    @Test(expected=StaleRecordException.class)
    public void testUpdateDeletedRecord() throws Exception {
        // 削除済みデータのIDを指定
        Long target = 20L;

        // prepare
        User loginUser = new User();
        loginUser.setEmpNo("ZZA01");
        loginUser.setNameE("Taro Yamada");
        String name = "UPDATED NAME!";
        SearchCorresponCondition cond = new SearchCorresponCondition();
        cond.setCorresponNo("1");

        // execute
        FavoriteFilter f = new FavoriteFilter();
        f.setId(target);
        f.setFavoriteName(name);
        f.setSearchConditionsToJson(cond);
        f.setUpdatedBy(loginUser);
        dao.update(f);

        fail("StaleRecordException must be occured.");
    }
    /**
     * {@link FavoriteFilterDaoImpl#update}のテストケース}
     * 更新エラーのテスト。
     * 既に更新されたデータ（VersionNo違い）を更新する。
     * エラー（StaleRecordException）を返す。
     * @throws Exception
     */
    @Test(expected=StaleRecordException.class)
    public void testUpdateExclusiveRecord() throws Exception {
        // 更新済みチェック用データのIDを指定
        Long target = 21L;

        // prepare
        User loginUser = new User();
        loginUser.setEmpNo("ZZA01");
        loginUser.setNameE("Taro Yamada");
        String name = "UPDATED NAME!";
        SearchCorresponCondition cond = new SearchCorresponCondition();
        cond.setCorresponNo("1");

        // execute
        FavoriteFilter f = new FavoriteFilter();
        f.setId(target);
        f.setFavoriteName(name);
        f.setSearchConditionsToJson(cond);
        f.setUpdatedBy(loginUser);
        f.setVersionNo(1L);
        dao.update(f);

        fail("StaleRecordException must be occured.");
    }

    /**
     * {@link FavoriteFilterDaoImpl#update}のテストケース}
     * 更新エラーのテスト。
     * 存在しないデータを更新する。
     * エラー（StaleRecordException）を返す。
     * @throws Exception
     */
    @Test(expected=StaleRecordException.class)
    public void testUpdateNotExistsRecord() throws Exception {
        // 存在しないIDを指定
        Long target = 99L;

        // prepare
        User loginUser = new User();
        loginUser.setEmpNo("ZZA01");
        loginUser.setNameE("Taro Yamada");
        String name = "UPDATED NAME!";
        SearchCorresponCondition cond = new SearchCorresponCondition();
        cond.setCorresponNo("1");

        // execute
        FavoriteFilter f = new FavoriteFilter();
        f.setId(target);
        f.setFavoriteName(name);
        f.setSearchConditionsToJson(cond);
        f.setUpdatedBy(loginUser);
        f.setVersionNo(1L);
        dao.update(f);

        fail("StaleRecordException must be occured.");
    }
    /**
     * {@link FavoriteFilterDaoImpl#update}のテストケース}
     * 更新テスト。
     * 名称をNULLでデータを更新した場合、名称が更新されない事を確認。
     * @throws Exception
     */
    @Test
    public void testUpdateNullName() throws Exception {
        // 更新可能なIDを指定
        Long id = 16L;

        // prepare
        User loginUser = new User();
        loginUser.setEmpNo("ZZA01");
        loginUser.setNameE("Taro Yamada");
        String name = null;
        SearchCorresponCondition cond = new SearchCorresponCondition();
        cond.setCorresponNo("1");

        // 更新前データ
        FavoriteFilter before = dao.findById(id);

        // execute
        FavoriteFilter f = new FavoriteFilter();
        f.setId(id);
        f.setFavoriteName(name);
        f.setSearchConditionsToJson(cond);
        f.setUpdatedBy(loginUser);
        f.setVersionNo(1L);
        Integer updateRecord = dao.update(f);

        // verify
        assertNotNull(updateRecord);
        assertTrue(updateRecord == 1);

        FavoriteFilter after = dao.findById(id);

        // 対象項目以外は更新していない事
        assertEquals(before.getCreatedBy().toString(), after.getCreatedBy().toString());
        assertEquals(before.getCreatedAt(), after.getCreatedAt());
        assertEquals(before.getId(), after.getId());
        assertEquals(before.getProjectId(), after.getProjectId());
        assertEquals(before.getUser().getEmpNo(), after.getUser().getEmpNo());
        assertEquals(before.getUser().getNameE(), after.getUser().getNameE());
        assertEquals(before.getFavoriteName(), after.getFavoriteName());

        // 対象項目が更新されている事
        // NULLの扱いが異なるので、JSON変換をかまして検証。
//      assertEquals(cond, after.getSearchConditionsAsObject());
        assertEquals(JSONUtil.encode(cond), after.getSearchConditions());
        assertEquals(loginUser.getEmpNo(), after.getUpdatedBy().getEmpNo());
        assertNotNull(after.getUpdatedAt());
        assertTrue(after.getUpdatedAt().compareTo(before.getUpdatedAt()) > 0);
        assertEquals(Long.valueOf(before.getVersionNo() + 1), after.getVersionNo());
    }
    /**
     * {@link FavoriteFilterDaoImpl#update}のテストケース}
     * 更新エラーのテスト。
     * 条件をNULLでデータを更新する。
     * エラー（KeyDuplicateException）を返す。
     * @throws Exception
     */
    @Test
    public void testUpdateNullConditions() throws Exception {
        // 更新可能なIDを指定
        Long id = 16L;

        // prepare
        User loginUser = new User();
        loginUser.setEmpNo("ZZA01");
        loginUser.setNameE("Taro Yamada");
        String name = "UPDATED NAME!";

        // 更新前データ
        FavoriteFilter before = dao.findById(id);

        // execute
        FavoriteFilter f = new FavoriteFilter();
        f.setId(id);
        f.setFavoriteName(name);
        f.setSearchConditions(null);
        f.setUpdatedBy(loginUser);
        f.setVersionNo(1L);
        Integer updateRecord = dao.update(f);

        // verify
        assertNotNull(updateRecord);
        assertTrue(updateRecord == 1);

        FavoriteFilter after = dao.findById(id);

        // 対象項目以外は更新していない事
        assertEquals(before.getCreatedBy().toString(), after.getCreatedBy().toString());
        assertEquals(before.getCreatedAt(), after.getCreatedAt());
        assertEquals(before.getId(), after.getId());
        assertEquals(before.getProjectId(), after.getProjectId());
        assertEquals(before.getUser().getEmpNo(), after.getUser().getEmpNo());
        assertEquals(before.getUser().getNameE(), after.getUser().getNameE());
        assertEquals(before.getSearchConditions(), after.getSearchConditions());

        // 対象項目が更新されている事
        assertEquals(name, after.getFavoriteName());
        assertEquals(loginUser.getEmpNo(), after.getUpdatedBy().getEmpNo());
        assertNotNull(after.getUpdatedAt());
        assertTrue(after.getUpdatedAt().compareTo(before.getUpdatedAt()) > 0);
        assertEquals(Long.valueOf(before.getVersionNo() + 1), after.getVersionNo());
    }
    /**
     * {@link FavoriteFilterDaoImpl#update}のテストケース}
     * 更新エラーのテスト。
     * 名称、条件をNULLでデータを更新する。
     * エラー（KeyDuplicateException）を返す。
     * @throws Exception
     */

    @Test
    public void testUpdateNullNameNullConditions() throws Exception {
        // 更新可能なIDを指定
        Long id = 16L;

        // prepare
        User loginUser = new User();
        loginUser.setEmpNo("ZZA01");
        loginUser.setNameE("Taro Yamada");
        String name = null;

        // 更新前データ
        FavoriteFilter before = dao.findById(id);

        // execute
        FavoriteFilter f = new FavoriteFilter();
        f.setId(id);
        f.setFavoriteName(name);
        f.setSearchConditions(null);
        f.setUpdatedBy(loginUser);
        f.setVersionNo(1L);
        Integer updateRecord = dao.update(f);

        // verify
        assertNotNull(updateRecord);
        assertTrue(updateRecord == 1);

        FavoriteFilter after = dao.findById(id);

        // 対象項目以外は更新していない事
        assertEquals(before.getCreatedBy().toString(), after.getCreatedBy().toString());
        assertEquals(before.getCreatedAt(), after.getCreatedAt());
        assertEquals(before.getId(), after.getId());
        assertEquals(before.getProjectId(), after.getProjectId());
        assertEquals(before.getUser().getEmpNo(), after.getUser().getEmpNo());
        assertEquals(before.getUser().getNameE(), after.getUser().getNameE());
        assertEquals(before.getFavoriteName(), after.getFavoriteName());
        assertEquals(before.getSearchConditions(), after.getSearchConditions());

        // 対象項目が更新されている事
        assertEquals(loginUser.getEmpNo(), after.getUpdatedBy().getEmpNo());
        assertNotNull(after.getUpdatedAt());
        assertTrue(after.getUpdatedAt().compareTo(before.getUpdatedAt()) > 0);
        assertEquals(Long.valueOf(before.getVersionNo() + 1), after.getVersionNo());
    }

    /**
     * {@link FavoriteFilterDaoImpl#deleteById}のテストケース}
     * 1件削除するテスト。
     * @throws Exception
     */
    @Test
    public void testDeleteRecord() throws Exception {
        // prepare テストに必要なデータを作成する
        Long id = 24L;
        FavoriteFilter filter = new FavoriteFilter();
        filter.setId(id);
        User loginUser = new User();
        loginUser.setEmpNo("ZZA01");
        filter.setUpdatedBy(loginUser);
        filter.setVersionNo(1L);

        // 削除前のデータを取得
        FavoriteFilter before = dao.findById(id);

        // execute
        Integer record = dao.delete(filter);
        assertNotNull(record);
        assertTrue(record == 1);

        // verify 削除後のデータを取得する
        String sql = "select * from favorite_filter where id = " + id.toString() + " and delete_no != 0";
        ITable afterActualTable = getConnection().createQueryTable("actual", sql);
        FavoriteFilter after = getObjectFromTable(afterActualTable);

        // 削除NO、更新日以外変更していない事
        assertTrue(after.getId() != null);
        assertNotSame("0", after.getDeleteNo().toString());
        assertEquals("id is not equals.", before.getId().toString(), after.getId().toString());
        assertEquals("project is id not equals.", before.getProjectId(), after.getProjectId());
        assertEquals("user is not equals.", before.getUser().getEmpNo(), after.getUser().getEmpNo());
        assertEquals("favorite name is not equals.", before.getFavoriteName(), after.getFavoriteName());
        assertEquals("search conditions is not equals.", before.getSearchConditions(), after.getSearchConditions());
        assertEquals("created by is not equals.", before.getCreatedBy().getEmpNo(),after.getCreatedBy().getEmpNo());
        assertEquals("created at is not equals.", before.getCreatedAt(), after.getCreatedAt());
        assertEquals("updated by is not updated.", loginUser.getEmpNo(), after.getUpdatedBy().getEmpNo());
        // 更新日は、処理時のTimestampの為、（Excel投入データより）最新であるべき。
        assertTrue("updated at is not updated. before is [" + before.getUpdatedAt().toString()
                + "]: of after is [" + after.getUpdatedAt().toString() + "]",
                before.getUpdatedAt().compareTo(after.getUpdatedAt()) < 0);
        assertEquals("version no is not incremented.", Long.valueOf(before.getVersionNo() + 1),
                     Long.valueOf(after.getVersionNo()));

    }
    /**
     * {@link FavoriteFilterDaoImpl#deleteById}のテストケース}
     * 既に削除されたデータを削除するテスト。
     * エラー（StaleRecordException）を返す。
     * @throws Exception
     */
    @Test(expected=StaleRecordException.class)
    public void testDeleteDeletedRecord() throws Exception {
        Long id = 22L;
        // prepare テストに必要なデータを作成する
        FavoriteFilter filter = new FavoriteFilter();
        filter.setId(id);
        User loginUser = new User();
        loginUser.setEmpNo("ZZA01");
        filter.setUpdatedBy(loginUser);
        filter.setVersionNo(1L);

        // execute
        dao.delete(filter);

        fail("StaleRecordException is occured.");
    }
    /**
     * {@link FavoriteFilterDaoImpl#deleteById}のテストケース}
     * 既に更新されたデータを削除するテスト。（VersionNo違い）
     * エラー（StaleRecordException）を返す。
     * @throws Exception
     */
    @Test(expected=StaleRecordException.class)
    public void testDeleteUpdatedRecord() throws Exception {
        Long id = 23L;
        // prepare テストに必要なデータを作成する
        FavoriteFilter filter = new FavoriteFilter();
        filter.setId(id);
        User loginUser = new User();
        loginUser.setEmpNo("ZZA01");
        filter.setUpdatedBy(loginUser);
        filter.setVersionNo(1L);

        // execute
        dao.delete(filter);

        fail("StaleRecordException is occured.");
    }
    /**
     * {@link FavoriteFilterDaoImpl#deleteById}のテストケース}
     * 存在しないデータを削除するテスト。
     * エラー（StaleRecordException）を返す。
     * @throws Exception
     */
    @Test(expected=StaleRecordException.class)
    public void testDeleteNotExistsRecord() throws Exception {
        // 存在しないIDを指定
        Long id = 99L;
        // prepare テストに必要なデータを作成する
        FavoriteFilter filter = new FavoriteFilter();
        filter.setId(id);
        User loginUser = new User();
        loginUser.setEmpNo("ZZA01");
        filter.setUpdatedBy(loginUser);
        filter.setVersionNo(1L);

        // execute
        dao.delete(filter);

        fail("StaleRecordException is occured.");
    }
    /**
     * {@link FavoriteFilterDaoImpl#deleteById}のテストケース}
     * 項目の値を変更して削除するテスト。
     * VersionNo、DeleteNo、UpdatedAt/By項目以外を変更していない事を確認。
     * @throws Exception
     */
    @Test
    public void testDeleteRecordAsOtherValueUpdated() throws Exception {
        // prepare テストに必要なデータを作成する
        Long id = 24L;
        FavoriteFilter filter = new FavoriteFilter();
        filter.setId(id);
        User loginUser = new User();
        loginUser.setEmpNo("ZZA01");
        filter.setUpdatedBy(loginUser);
        filter.setVersionNo(1L);
        filter.setProjectId("Dummy Id");
        filter.setFavoriteName("Dummy Name");
        SearchCorresponCondition cond = new SearchCorresponCondition();
        cond.setCorresponNo("1");
        filter.setSearchConditionsToJson(cond);

        // 削除前のデータを取得
        FavoriteFilter before = dao.findById(id);

        // execute
        Integer record = dao.delete(filter);
        assertNotNull(record);
        assertTrue(record == 1);

        // verify 削除後のデータを取得する
        String sql = "select * from favorite_filter where id = " + id.toString() + " and delete_no != 0";
        ITable afterActualTable = getConnection().createQueryTable("actual", sql);
        FavoriteFilter after = getObjectFromTable(afterActualTable);

        // 削除NO、更新日以外変更していない事
        assertTrue(after.getId() != null);
        assertNotSame("0", after.getDeleteNo().toString());
        assertEquals("id is not equals.", before.getId().toString(), after.getId().toString());
        assertEquals("project is id not equals.", before.getProjectId(), after.getProjectId());
        assertEquals("user is not equals.", before.getUser().getEmpNo(), after.getUser().getEmpNo());
        assertEquals("favorite name is not equals.", before.getFavoriteName(), after.getFavoriteName());
        assertEquals("search conditions is not equals.", before.getSearchConditions(), after.getSearchConditions());
        assertEquals("created by is not equals.", before.getCreatedBy().getEmpNo(),after.getCreatedBy().getEmpNo());
        assertEquals("created at is not equals.", before.getCreatedAt(), after.getCreatedAt());
        assertEquals("updated by is not updated.", loginUser.getEmpNo(), after.getUpdatedBy().getEmpNo());
        assertTrue("updated at is not updated.", before.getUpdatedAt().compareTo(after.getUpdatedAt()) < 0);
        assertEquals("version no is not incremented.", Long.valueOf(before.getVersionNo() + 1),
                     Long.valueOf(after.getVersionNo()));

    }

    /**
     * ITable を FavoriteFilterクラスに変換する.
     * @param table
     * @return
     */
    private FavoriteFilter getObjectFromTable(ITable table) {
        FavoriteFilter result = new FavoriteFilter();
        if(table == null || table.getRowCount() <= 0) {
            return result;
        }
        try {
            SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.S");

            result.setId(Long.valueOf(table.getValue(0, "id").toString()));
            result.setProjectId(table.getValue(0, "project_id").toString());
            User user = new User();
            user.setEmpNo(table.getValue(0, "emp_no").toString());
            result.setUser(user);
            result.setFavoriteName(table.getValue(0, "favorite_name").toString());
            result.setSearchConditions(table.getValue(0, "search_conditions").toString());
            User created = new User();
            created.setEmpNo(table.getValue(0, "created_by").toString());
            result.setCreatedBy(created);
            Date createdAt = fm.parse(table.getValue(0, "created_at").toString());
            result.setCreatedAt(createdAt);
            User updated = new User();
            updated.setEmpNo(table.getValue(0, "updated_by").toString());
            result.setUpdatedBy(updated);
            Date updatedAt = fm.parse(table.getValue(0, "updated_at").toString());
            result.setUpdatedAt(updatedAt);
            result.setVersionNo(Long.valueOf(table.getValue(0, "version_no").toString()));
            result.setDeleteNo(Long.valueOf(table.getValue(0, "delete_no").toString()));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (DataSetException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

}
