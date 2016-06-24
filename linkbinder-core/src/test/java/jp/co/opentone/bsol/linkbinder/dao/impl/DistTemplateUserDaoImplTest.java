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
import jp.co.opentone.bsol.linkbinder.dao.DistTemplateUserDao;
import jp.co.opentone.bsol.linkbinder.dto.DistTemplateGroup;
import jp.co.opentone.bsol.linkbinder.dto.DistTemplateUser;

/**
 * DistTemplateUserDaoImplのテストクラス.
 * @author opentone
 */
public class DistTemplateUserDaoImplTest extends DistTemplateTestBase {

    /**
     * テスト対象のDaoインスタンス.
     */
    private DistTemplateUserDao distTemplateUserDao;

    /**
     * データをinsertした場合の検証対象列.
     */
    private static final String[] TEST_INSERTED = {
        "id",
        "dist_template_group_id",
        "order_no",
        "emp_no",
        "created_by",
        "created_at",
        "updated_by",
        "updated_at",
        "delete_no"
    };

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        distTemplateUserDao = daoFinder.getDao(DistTemplateUserDao.class);
        // 既存の参照用テストデータ以外を削除
        //distTestUtilDaoImpl.delete("distTemplateUser.testClearTable");
        createDistHeaderTestData();
    }

    /**
     * create(正常系).
     * <p>
     * dist template group id=1000000041でデータを作成
     * データを作成し、作成したデータと元のデータを比較確認。
     */
    @Test
    @Rollback(true)
    public void testCreate01() {
        // CHECKSTYLE:OFF
        // 新規データ(グループ)
        DistTemplateGroup distTemplateGroup = createTestGroupData();
        DistTemplateGroupDao gDao =  daoFinder.getDao(DistTemplateGroupDao.class);
        Long gId = gDao.create(distTemplateGroup);
        // 新規データ(ユーザー)
        DistTemplateUser distTemplateUser = createDistTemplateUser(0L,
                                                                   gId.longValue(),
                                                                   1L,
                                                                   "ZZA01",
                                                                   "ZZA02",
                                                                   "ZZA03"
                                                                   );
        // CHECKSTYLE:ON

        // create処理を実行
        Long id = distTemplateUserDao.create(distTemplateUser);
        Assert.assertNotNull(id);
        Assert.assertTrue(0 < id);

        // 比較のために期待値を追加
        distTemplateUser.setId(id);
        distTemplateUser.setVersionNo(1L);
        distTemplateUser.setDeleteNo(0L);

        assertRow(id, distTemplateUser, TEST_INSERTED);
    }

    /**
     * テストグループデータ作成.
     * @return テストグループ
     */
    private DistTemplateGroup createTestGroupData() {
        // CHECKSTYLE:OFF
        DistTemplateGroup distTemplateGroup
            = createDistTemplateGroup(0,
                                      1000000004L,
                                      "TO",
                                      2L,
                                      1000000002L,
                                      "ZZ001",
                                      "ZZ002");
        // CHECKSTYLE:ON
        return distTemplateGroup;
    }
    /**
     * create(異常系. 必須パラメータ不正).
     * <p>
     * dist template group id=1000000041でデータを作成
     * 必須項目にNULLを指定し、どの場合も例外がthrowされることを確認。
     */
    // CHECKSTYLE:ON
    @Test
    @Rollback(true)
    public void testCreate02() {
        // CHECKSTYLE:OFF
        // 新規データ(グループ)
        DistTemplateGroup distTemplateGroup = createTestGroupData();
        DistTemplateGroupDao gDao =  daoFinder.getDao(DistTemplateGroupDao.class);
        Long gId = gDao.create(distTemplateGroup);

        DistTemplateUser[] distTemplateUsers = new DistTemplateUser[8];
        for(int i = 0; i < distTemplateUsers.length; i++) {
            // 新規データの作成(必須全てが埋まっている状態)
            distTemplateUsers[i] = createDistTemplateUser(0L,
                                                          gId.longValue(),
                                                          1L + i,
                                                          "ZZA04",
                                                          "ZZA05",
                                                          "ZZA06"
                                                          );
        }

        distTemplateUsers[0] = null;
        //distTemplateUsers[1].setId(null);
        distTemplateUsers[1].setDistTemplateGroupId(null);
        distTemplateUsers[2].setOrderNo(null);
        distTemplateUsers[3].setEmpNo(null);
        distTemplateUsers[4].setCreatedBy(null);
        distTemplateUsers[5].setCreatedAt(null);
        distTemplateUsers[6].setUpdatedBy(null);
        distTemplateUsers[7].setUpdatedAt(null);
        //distTemplateUsers[8].setDeleteNo(null);
        // CHECKSTYLE:ON

        // どの場合も例外発生
        for (DistTemplateUser distTemplateUser : distTemplateUsers) {
            try {
                distTemplateUserDao.create(distTemplateUser);
            } catch (IllegalArgumentException e) {
                continue;
            }
            Assert.fail("Can't catch IllegalArgumentException.");
        }
    }

    /**
     * create(異常系. 代理キーパラメータ不正).
     * <p>
     * 代理キー(dist_tempalte_group_id, order_no)の組み合わせが
     * 同一のデーターを作成し、例外がthrowされることを確認。
     */
    // CHECKSTYLE:ON
    @Test
    @Rollback(true)
    public void testCreate03() {
        // CHECKSTYLE:OFF
        // 新規データ(グループ)
        DistTemplateGroup distTemplateGroup = createTestGroupData();
        DistTemplateGroupDao gDao =  daoFinder.getDao(DistTemplateGroupDao.class);
        Long gId = gDao.create(distTemplateGroup);

        DistTemplateUser[] distTemplateUsers = new DistTemplateUser[2];
        for(int i = 0; i < distTemplateUsers.length; i++) {
            // 新規データの作成(必須全てが埋まっている状態)
            distTemplateUsers[i] = createDistTemplateUser(0L,
                                                          gId.longValue(),
                                                          1L,
                                                          "ZZA11",
                                                          "ZZA12",
                                                          "ZZA13"
                                                          );
        }

        try {
            distTemplateUserDao.create(distTemplateUsers[0]);
            // 代理キーのデータの組み合わせが同一のためエラーが発生しなければならない
            distTemplateUserDao.create(distTemplateUsers[1]);
        } catch (DataIntegrityViolationException e) {
            // OK
        } catch (Exception e) {
            Assert.fail(e.toString());
        }
    }

    /**
     * deleteByDistTemplateGroupId(正常系).
     * <p>
     * 更新対象列が変更されていることを確認。
     */
    // CHECKSTYLE:ON
    @Test
    @Rollback(true)
    public void testDeleteByDistTemplateGroupId01() {
        // CHECKSTYLE:OFF
        // 新規データ(グループ)
        DistTemplateGroup distTemplateGroup = createTestGroupData();
        DistTemplateGroupDao gDao =  daoFinder.getDao(DistTemplateGroupDao.class);
        Long gId = gDao.create(distTemplateGroup);

        DistTemplateUser distTemplateUser = createDistTemplateUser(0L,
                                                          gId.longValue(),
                                                          1L,
                                                          "ZZA11",
                                                          "ZZA12",
                                                          "ZZA13"
                                                          );

        Long id = distTemplateUserDao.create(distTemplateUser);
        distTemplateUser.setId(id);

        DistTemplateUser deleteDistTemplateUser = distTemplateUser.clone();
        // Logicにて設定するデータを格納し、論理削除処理を実行
        deleteDistTemplateUser.setUpdatedBy("ZZA14");
        String updateDate = "2011/03/31 10:00:00";
        deleteDistTemplateUser.setUpdatedAt(parseTimestamp(updateDate));
        try {
            distTemplateUserDao.deleteByDistTemplateGroupId(deleteDistTemplateUser);
            // 削除後のレコードを確認
            @SuppressWarnings("rawtypes")
            Map recMap = getDistTemplateMap(id, "distTemplateUser.testFindById");
            Assert.assertTrue(
                    0L < ((BigDecimal) recMap.get("DELETE_NO")).longValue());
            Assert.assertEquals(deleteDistTemplateUser.getUpdatedBy(), recMap.get("UPDATED_BY"));
            Assert.assertEquals(
                    super.formatTimestamp(deleteDistTemplateUser.getUpdatedAt()), recMap.get("UPDATED_AT"));
        } catch (Exception e) {
            Assert.fail(e.toString());
        }
    }

    /**
     * deleteByDistTemplateGroupId(異常系. 必須パラメータ不足).
     * <p>
     * @throws MerRecordIllegalUpdatedException MerRecordIllegalUpdatedException
     * @throws MerRecordNotUpdatedException MerRecordNotUpdatedException
     */
    // CHECKSTYLE:ON
    @Rollback(true)
    public void testDeleteByDistTemplateGroupId02() {
        // CHECKSTYLE:OFF
        // 新規データ(グループ)
        DistTemplateGroup distTemplateGroup = createTestGroupData();
        DistTemplateGroupDao gDao =  daoFinder.getDao(DistTemplateGroupDao.class);
        Long gId = gDao.create(distTemplateGroup);

        DistTemplateUser[] distTemplateUsers = new DistTemplateUser[3];
        for(int i = 0; i < distTemplateUsers.length; i++) {
            // 新規データの作成(必須全てが埋まっている状態)
            distTemplateUsers[i] = createDistTemplateUser(0L,
                                                          gId.longValue(),
                                                          1L + i,
                                                          "ZZA15",
                                                          "ZZA16",
                                                          "ZZA17"
                                                          );
        }
        distTemplateUsers[0] = null;
        distTemplateUsers[2].setDistTemplateGroupId(null);
        distTemplateUsers[3].setUpdatedBy(null);
        // CHECKSTYLE:ON

        // どの場合も例外発生
        for (DistTemplateUser distTemplateUser : distTemplateUsers) {
            try {
                distTemplateUserDao.deleteByDistTemplateGroupId(distTemplateUser);
            } catch (IllegalArgumentException e) {
                continue;
            }
            Assert.fail("Can't catch MerIllegalArgumentException.");
        }
    }

    /**
     * 1行分のレコードを検証します.
     * @param id 対象のレコードid
     * @param distTemplateUser 検証対象のオブジェクト
     */
    private void assertRow(Long id, DistTemplateUser distTemplateUser, String[] fields) {
        @SuppressWarnings("rawtypes")
        Map expMap = getDistTemplateMap(id, "distTemplateUser.testFindById");
        assertDistTemplateUser(expMap, distTemplateUser, fields);
    }

    /**
     * テストユーティリティを使用してdistTemplateUserを1件取得する.
     * @param id id
     * @param queryId クエリーid
     * @return Map
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private Map getDistTemplateMap(Long id, String queryId) {
        HashMap param = new HashMap();
        param.put("id", id);
        List<Map> distTemplateUserList
            = distTestUtilDaoImpl.select(queryId, param);
        Assert.assertNotNull(distTemplateUserList);
        Assert.assertEquals(1, distTemplateUserList.size());
        return distTemplateUserList.get(0);
    }

    /**
     * insertやupdateテスト用のDistTemplateUserを1件作成します.
     * @param id ID
     * @param distTemplateGroupId Distributionテンプレート活動単位ID
     * @param orderNo 表示順
     * @param empNo 宛先従業員番号
     * @param createdBy 作成者従業員番号
     * @param updatedBy 更新者従業員番号
     * @return DistTemplateUser
     */
    private DistTemplateUser createDistTemplateUser(long id,
                                                    long distTemplateGroupId,
                                                    long orderNo,
                                                    String empNo,
                                                    String createdBy,
                                                    String updatedBy) {
        DistTemplateUser distTemplateUser = new DistTemplateUser();
        distTemplateUser.setId(id);
        distTemplateUser.setDistTemplateGroupId(distTemplateGroupId);
        distTemplateUser.setOrderNo(orderNo);
        distTemplateUser.setEmpNo(empNo);
        distTemplateUser.setCreatedBy(createdBy);
        String createDate = "2010/01/23 10:12:34";
        distTemplateUser.setCreatedAt(parseTimestamp(createDate));
        distTemplateUser.setUpdatedBy(updatedBy);
        distTemplateUser.setUpdatedAt(parseTimestamp(createDate));
        return distTemplateUser;
    }

    /**
     * Map型とDistTemplateUser型のレコードを比較します.
     * @param map Map型のレコードオブジェクト
     * @param distTemplateUser DistTemplateUser型のレコードオブジェクト
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void assertDistTemplateUser(Map map,
                                        DistTemplateUser distTemplateUser,
                                        String[] fields) {
        AssertMapComparer comp = new AssertMapComparer(map, distTemplateUser);
        comp.addCompareKey(fields);
        // String型以外の設定
        comp.updateCompareKey("id", AssertCompareType.BIGDECIMAL);
        comp.updateCompareKey("dist_template_group_id", AssertCompareType.BIGDECIMAL);
        comp.updateCompareKey("order_no", AssertCompareType.BIGDECIMAL);
        comp.updateCompareKey("created_at", AssertCompareType.TIMESTAMP);
        comp.updateCompareKey("updated_at", AssertCompareType.TIMESTAMP);
        comp.updateCompareKey("delete_no", AssertCompareType.BIGDECIMAL);

        comp.assertCompare();
        for (String message : comp.getCompareResult()) {
            log.debug(message);
        }
    }
}
