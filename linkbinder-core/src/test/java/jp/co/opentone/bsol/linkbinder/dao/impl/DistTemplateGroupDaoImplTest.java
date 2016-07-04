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

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.Rollback;

import jp.co.opentone.bsol.framework.test.util.AssertMapComparer;
import jp.co.opentone.bsol.framework.test.util.AssertMapComparer.AssertCompareType;
import jp.co.opentone.bsol.linkbinder.dao.DistTemplateGroupDao;
import jp.co.opentone.bsol.linkbinder.dto.DistTemplateGroup;
import jp.co.opentone.bsol.linkbinder.dto.code.DistributionType;

/**
 * DistTemplateGroupDaoImplのテストクラス.
 * @author opentone
 */
public class DistTemplateGroupDaoImplTest extends DistTemplateTestBase {

    /**
     * テスト対象のDaoインスタンス.
     */
    private DistTemplateGroupDao distTemplateGroupDao;

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        distTemplateGroupDao = daoFinder.getDao(DistTemplateGroupDao.class);
        createFindTestData();
    }

    /**
     * テストデータを作成する.
     */
    public void createFindTestData() {
        // 所定のテストデータ以外を削除
        super.createDistHeaderTestData();
        distTestUtilDaoImpl.delete("distTemplateGroup.testClearTable");
    }
    /**
     * データをinsertした場合の検証対象列.
     */
    private static final String[] TEST_INSERTED = {
        "id",
        "dist_template_header_id",
        // "distribution_type",個別に検証
        "order_no",
        "group_id",
        "created_by",
        "created_at",
        "updated_by",
        "updated_at"
    };

    /**
     * データをupdateした場合の検証対象列.
     */
    private static final String[] TEST_UPDATED = {
        "dist_template_header_id",
        // "distribution_type",個別に検証
        "order_no",
        "group_id",
        "updated_by",
        "updated_at",
        "delete_no"
    };

    /**
     * データをupdateした場合の非検証対象列.
     */
    private static final String[] TEST_NOT_UPDATED = {
        "id",
        "created_by",
        "created_at"
    };

    /**
     * create(正常系).
     * <p>
     * データを作成し、作成したデータと元のデータを比較確認。
     */
    @SuppressWarnings("rawtypes")
    // CHECKSTYLE:ON
    @Test
    @Rollback(false)
    public void testCreate01() {
        // CHECKSTYLE:OFF
        // 新規データ作成
        DistTemplateGroup distTemplateGroup = createDistTemplateGroup(0,
                                                                      1000000003L,
                                                                      "TO",
                                                                      2L,
                                                                      1000000002L,
                                                                      "ZZA01",
                                                                      "ZZA02");
        // CHECKSTYLE:ON
        // create処理を実行
        Long id = distTemplateGroupDao.create(distTemplateGroup);
        Assert.assertNotNull(id);
        Assert.assertTrue(0 < id);

        // Distribution Type の比較
        Map expMap = getDistTemplateGroupMap(id, "distTemplateGroup.testFindById");
        Assert.assertEquals(Integer.valueOf(expMap.get("DISTRIBUTION_TYPE").toString()),
                distTemplateGroup.getDistributionType().getValue());
        // 作成したデータと元のデータを比較
        assertRow(id, distTemplateGroup, TEST_INSERTED);
    }

    /**
     * create(異常系. 外部キー参照不可).
     * <p>
     * DistribuitonテンプレートヘッダーIDに1999999999を設定し、
     * 参照先テーブル(dist_template_header)にIDが存在しないため、
     * 例外が発生することを確認。
     */
    // CHECKSTYLE:ON
    @Test
    @Rollback(false)
    public void testCreate02() {
        // CHECKSTYLE:OFF
        // 新規データ作成
        DistTemplateGroup distTemplateGroup = createDistTemplateGroup(0,
                                                                      1999999999L,
                                                                      "TO",
                                                                      1L,
                                                                      1000000002L,
                                                                      "ZZ001",
                                                                      "ZZ002");
        // CHECKSTYLE:ON
        try {
            // create処理を実行
            distTemplateGroupDao.create(distTemplateGroup);
            Assert.fail("Can't catch DataInterfrityViolationException.");
        } catch (Exception e) {
            // 外部キーがないため例外が発生することを確認。
            e.printStackTrace();
            Assert.assertTrue(e.toString(), true);
        }
    }

    /**
     * create(異常系. 代理キー重複).
     * <p>
     * 代理キーが同一のデーターを作成し、例外が発生することを確認。
     */
    // CHECKSTYLE:ON
    @Test
    @Rollback(false)
    public void testCreate03() {
        // CHECKSTYLE:OFF
        // 新規データ作成
        DistTemplateGroup distTemplateGroup = createDistTemplateGroup(0,
                                                                      1000000001L,
                                                                      "TO",
                                                                      1L,
                                                                      1000000002L,
                                                                      "ZZ001",
                                                                      "ZZ002");
        // CHECKSTYLE:ON
        try {
            // create処理を実行
            distTemplateGroupDao.create(distTemplateGroup);
            distTemplateGroupDao.create(distTemplateGroup);
            Assert.fail("Can't catch DataIntegrityViolationException.");
        } catch (DataIntegrityViolationException e) {
            // 代理キーが重なるため例外が発生することを確認。
            e.printStackTrace();
            Assert.assertTrue(e.toString(), true);
        } catch (Exception e) {
            Assert.fail(e.toString());
        }
    }

    /**
     * create(異常系. 必須パラメータ不足).
     * <p>
     * 一つずつ必須項目にNULLを指定し、どの場合も例外が発生することを確認。
     * 例外：MerIllegalArgumentException(引数不正)
     *
     */
    // CHECKSTYLE:ON
    @Test
    @Rollback(false)
    public void testCreate04() {
        // CHECKSTYLE:OFF
        DistTemplateGroup[] distTemplateGroups = new DistTemplateGroup[9];
        for (int i = 1; i < distTemplateGroups.length; i++) {
            // 新規データ作成
            distTemplateGroups[i] = createDistTemplateGroup(0,
                                                            100000010L + i,
                                                            "TO",
                                                            1L + i,
                                                            1000000002L,
                                                            "ZZ001",
                                                            "ZZ002");
        }
        // IDはSQLにて設定のため検証対象から除外
        distTemplateGroups[0] = null;
        distTemplateGroups[1].setDistTemplateHeaderId(null);
        distTemplateGroups[2].setDistributionType(null);
        distTemplateGroups[3].setOrderNo(null);
        distTemplateGroups[4].setGroupId(null);
        distTemplateGroups[5].setCreatedBy(null);
        distTemplateGroups[6].setCreatedAt(null);
        distTemplateGroups[7].setUpdatedBy(null);
        distTemplateGroups[8].setUpdatedAt(null);
        // CHECKSTYLE:ON
        // どの場合も例外が発生しなければならない
        for (DistTemplateGroup distTemplateGroup : distTemplateGroups) {
            try {
                // create処理を実行
                distTemplateGroupDao.create(distTemplateGroup);
            } catch (IllegalArgumentException e) {
                continue;
            }
            Assert.fail("Can't catch MerIllegalArgumentException.");
        }
    }

    /**
     * delete(正常系).
     * <p>
     * 作成したデータが論理削除されていることを確認。
     * 最終更新者が変更されていることを確認。
     */
    // CHECKSTYLE:ON
    @SuppressWarnings({ "rawtypes" })
    @Test
    @Rollback(false)
    public void testDelete01() {
        // CHECKSTYLE:OFF
        // 新規データ作成
        DistTemplateGroup distTemplateGroup = createDistTemplateGroup(0,
                                                                      1000000004L,
                                                                      "TO",
                                                                      2L,
                                                                      1000000002L,
                                                                      "ZZ001",
                                                                      "ZZ002");
        // CHECKSTYLE:ON
        // create処理を実行
        Long id = distTemplateGroupDao.create(distTemplateGroup);
        // 削除前テストデータ取得
        distTemplateGroup.setId(id);
        distTemplateGroup.setDeleteNo(0L);

        // 作成したテストデータを削除
        distTemplateGroup.setUpdatedBy("ZZ003");
        String deleteAt = "2010/01/23 06:54:32";
        distTemplateGroup.setUpdatedAt(parseTimestamp(deleteAt));
        try {
            distTemplateGroupDao.delete(distTemplateGroup);
        } catch (Exception e) {
            Assert.fail(e.toString());
        }
        // 削除後テストデータ取得
        Map deleteAfterMap = getDistTemplateGroupMap(id, "distTemplateGroup.testFindById");
        // 最終更新者が変更されていること
        Assert.assertEquals(distTemplateGroup.getUpdatedBy(), deleteAfterMap.get("UPDATED_BY"));
        Assert.assertEquals(super.formatTimestamp(distTemplateGroup.getUpdatedAt()),
                deleteAfterMap.get("UPDATED_AT"));
        // 作成したデータが論理削除されていること
        Assert.assertTrue(
                0L < ((BigDecimal) deleteAfterMap.get("DELETE_NO")).longValue());
    }

    /**
     * delete(異常系).
     * idが一致しないために削除できない
     *
     * id=1999999999で検証(存在しないID)
     * 更新結果が0件であることを確認。
     */
    // CHECKSTYLE:ON
    @Test
    @Rollback(false)
    public void testDelete02() {
        // CHECKSTYLE:OFF
        // 新規データ作成
        DistTemplateGroup distTemplateGroup = createDistTemplateGroup(0,
                                                                      1000000005L,
                                                                      "TO",
                                                                      2L,
                                                                      1000000002L,
                                                                      "ZZ001",
                                                                      "ZZ002");
        // create処理を実行
        distTemplateGroupDao.create(distTemplateGroup);

        // 削除用データを作成
        distTemplateGroup.setId(1999999999L);
        distTemplateGroupDao.delete(distTemplateGroup);
    }

    /**
     * update(正常系).
     *
     * データを更新。
     * 更新対象列の更新を確認。
     * 非更新対象列の非更新を確認。
     */
    @SuppressWarnings({ "rawtypes" })
    // CHECKSTYLE:ON
    @Test
    @Rollback(false)
    public void testUpdate01() {
        // 新規データ作成
        // CHECKSTYLE:OFF
        DistTemplateGroup distTemplateGroup = createDistTemplateGroup(0,
                                                                      1000000006L,
                                                                      "TO",
                                                                      2L,
                                                                      1000000002L,
                                                                      "ZZ001",
                                                                      "ZZ002");
        // create処理を実行
        Long id = distTemplateGroupDao.create(distTemplateGroup);
        distTemplateGroup.setId(id);
        distTemplateGroup.setDeleteNo(0L);

        // 更新用データを作成
        DistTemplateGroup updateDistTemplateGroup = distTemplateGroup.clone();
//        updateDistTemplateGroup.setDistTemplateHeaderId(11111L);
        updateDistTemplateGroup.setDistributionType(DistributionType.CC);
        updateDistTemplateGroup.setOrderNo(2L);
        updateDistTemplateGroup.setGroupId(1000000005L);
        updateDistTemplateGroup.setCreatedBy("ZZ004");
        updateDistTemplateGroup.setUpdatedBy(updateDistTemplateGroup.getCreatedBy());
        String updateAt = "2020/11/22 11:22:33";
        updateDistTemplateGroup.setCreatedAt(parseTimestamp(updateAt));
        updateDistTemplateGroup.setUpdatedAt(updateDistTemplateGroup.getCreatedAt());

        // CHECKSTYLE:ON
        // update処理実行
        try {
            distTemplateGroupDao.update(updateDistTemplateGroup);
            updateDistTemplateGroup.setDeleteNo(0L);
            // 更新対象の確認
            // Distribution Type の比較
            Map expMap = getDistTemplateGroupMap(id, "distTemplateGroup.testFindById");
            Assert.assertEquals(Integer.valueOf(expMap.get("DISTRIBUTION_TYPE").toString()),
                    updateDistTemplateGroup.getDistributionType().getValue());
            assertRow(id, updateDistTemplateGroup, TEST_UPDATED);
            assertRow(id, distTemplateGroup, TEST_NOT_UPDATED);
        } catch (Exception e) {
            Assert.fail(e.toString());
        }
    }

    /**
     * update(異常系).
     * idが一致しないために更新できない
     *
     * id=19999999999(存在しないID)で検証
     * 更新件数が0件であることを確認。
     */
    // CHECKSTYLE:ON
    @Test
    @Rollback(false)
    public void testUpdate02() {
        // 新規データ作成
        // CHECKSTYLE:OFF
        DistTemplateGroup distTemplateGroup = createDistTemplateGroup(0,
                                                                      1000000007L,
                                                                      "TO",
                                                                      99L,
                                                                      1000000002L,
                                                                      "ZZ001",
                                                                      "ZZ002");
        // create処理を実行
        distTemplateGroupDao.create(distTemplateGroup);
        // 更新用データを作成
        distTemplateGroup.setId(19999999999L);
        // CHECKSTYLE:ON
        // update処理実行
        distTemplateGroupDao.update(distTemplateGroup);
    }

    /**
     * 1行分のレコードを検証します.
     * @param id 対象のレコードid
     * @param distTemplateHeader 検証対象のオブジェクト
     */
    @SuppressWarnings({ "rawtypes" })
    private void assertRow(Long id,
                                 DistTemplateGroup distTemplateGroup,
                                 String[] fields) {
        Map expMap = getDistTemplateGroupMap(id, "distTemplateGroup.testFindById");
        assertDistTemplateGroup(expMap, distTemplateGroup, fields);
    }

    /**
     * テストユーティリティを使用してDistTemplateGroupを1件取得する.
     * @param id id
     * @param queryId クエリーid
     * @return DistTemplateGroup
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private Map getDistTemplateGroupMap(Long id, String queryId) {
        HashMap param = new HashMap();
        param.put("id", id);
        List<Map> distTemplateGroupList = distTestUtilDaoImpl.select(queryId, param);
        Assert.assertNotNull(distTemplateGroupList);
        Assert.assertEquals(1, distTemplateGroupList.size());
        return distTemplateGroupList.get(0);
    }

    /**
     * Map型とDistTemplateHeader型のレコードを比較します.
     * @param map Map型のレコードオブジェクト
     * @param distTemplateHeader DistTemplateHeader型のレコードオブジェクト
     *
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void assertDistTemplateGroup(Map map,
                                         DistTemplateGroup distTemplateGroup,
                                         String[] fields) {
        AssertMapComparer comp = new AssertMapComparer(map, distTemplateGroup);
        comp.addCompareKey(fields);
        // String型以外の設定
        comp.updateCompareKey("id", AssertCompareType.BIGDECIMAL);
        comp.updateCompareKey("dist_template_header_id", AssertCompareType.BIGDECIMAL);
        comp.updateCompareKey("order_no", AssertCompareType.BIGDECIMAL);
        comp.updateCompareKey("group_id", AssertCompareType.BIGDECIMAL);
        comp.updateCompareKey("created_at", AssertCompareType.TIMESTAMP);
        comp.updateCompareKey("updated_at", AssertCompareType.TIMESTAMP);
        comp.updateCompareKey("version_no", AssertCompareType.BIGDECIMAL);
        comp.updateCompareKey("delete_no", AssertCompareType.BIGDECIMAL);

        comp.assertCompare();
        for (String message : comp.getCompareResult()) {
            log.debug(message);
        }
    }
}

