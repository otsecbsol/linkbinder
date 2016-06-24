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
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.test.annotation.Rollback;

import jp.co.opentone.bsol.framework.test.util.AssertMapComparer;
import jp.co.opentone.bsol.framework.test.util.AssertMapComparer.AssertCompareType;
import jp.co.opentone.bsol.linkbinder.dao.DistTemplateHeaderDao;
import jp.co.opentone.bsol.linkbinder.dto.DistTemplateHeader;
/**
 * DistTemplateHeaderDaoImplTestのTestクラス.
 * @author opentone
 */
public class DistTemplateHeaderDaoImplTest extends DistTemplateTestBase {

    /**
     * serialVersionUID.
     */
    private static final long serialVersionUID = 5389019017253856103L;

    /**
     * テスト対象のDaoインスタンス.
     */
    private DistTemplateHeaderDao distTemplateHeaderDao;

    /**
     * データをselectした場合の検証対象列.
     */
    private static final String[] TEST_SELECTED = {
        "id",
        "project_id",
        "emp_no",
        "template_cd",
        "name",
        "created_by",
        "created_at",
        "updated_by",
        "updated_at",
        "version_no"
    };

    /**
     * データをinsertした場合の検証対象列.
     */
    private static final String[] TEST_INSERTED = {
        "id",
        "project_id",
        "emp_no",
        "template_cd",
        "name",
        "created_by",
        "created_at",
        "updated_by",
        "updated_at",
        "version_no",
        "delete_no"
    };

    /**
     * データをupdateした場合の検証対象列(更新すべき列).
     */
    private static final String[] TEST_UPDATED = {
        "template_cd",
        "name",
        "updated_by",
        "updated_at",
        "version_no",
    };

    /**
     * データをupdateした場合の検証対象列(更新すべきではない列).
     */
    private static final String[] TEST_NOT_UPDATED = {
        "id",
        "project_id",
        "emp_no",
        "created_by",
        "created_at",
        "delete_no"
    };


    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        //DistTemplateTestBase.setUpBeforeClass();
        //createFindTestData();
    }

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        distTemplateHeaderDao = daoFinder.getDao(DistTemplateHeaderDao.class);
        createDistHeaderTestData();
    }

    /**
     * findByProjectId
     * データの取得に対するテスト.
     * <p>
     * Project id=0-1111-1, 0-2222-1で検証
     *
     * プロジェクト毎にデータが取得できていることをデータ件数で確認。
     * 期待通りにソートされていることを確認。
     */
    // CHECKSTYLE:ON
    @Test
    public void testFindByProjectId01() {
        try {
            String[][] projectId = {{"0-1111-1", "5"},      // 6件中1件削除済み
                                    {"0-2222-1", "10"}};    // 10桁目あり

            for (int i = 0; i < projectId.length; i++) {
                List<DistTemplateHeader> distHeaderList01
                    = distTemplateHeaderDao.findByProjectId(projectId[i][0]);

                // CHECKSTYLE:OFF
                Assert.assertEquals(projectId[i][1], Integer.toString(distHeaderList01.size()));
                // CHECKSTYLE:ON

                // プロジェクトIDで取得できていることを確認。
                Assert.assertNotNull(distHeaderList01);
                for (int j = 0; j < distHeaderList01.size(); j++) {
                    DistTemplateHeader list =  distHeaderList01.get(j);
                    // プロジェクトIDで取得できていることを確認。
                    Assert.assertEquals(projectId[i][0], list.getProjectId());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.toString());
        }
    }

    /**
     * findByProjectId
     * 存在しないレコードに対するテスト.
     * <p>
     *  Project id=1999999999(存在しないレコード)
     *  リストが空であることを確認。
     */
    // CHECKSTYLE:ON
    @Test
    public void testFindByProjectId02() {
        String projectId = "1999999999";
        List<DistTemplateHeader> list = distTemplateHeaderDao.findByProjectId(projectId);
        Assert.assertNotNull(list);
        Assert.assertEquals(0, list.size());
    }

    /**
     * findById
     * 該当のHeader idに対するテスト.
     * <p>
     * Header id=1000000009, 1000000024
     * データ比較確認。
     */
    // CHECKSTYLE:ON
    @Test
    public void testFindById01() {
        // CHECKSTYLE:OFF
        Long[] headerId = {1000000009L, 1000000024L};
        // CHECKSTYLE:ON
        for (int i = 0; i < headerId.length; i++) {
            DistTemplateHeader distTemplateHeader = distTemplateHeaderDao.findById(headerId[i]);
            Long id = distTemplateHeader.getId();
            // DBのデータと比較。
            assertRow(id, distTemplateHeader, TEST_SELECTED);
        }
    }

    /**
     * findById
     * 削除されたレコードに対するテスト.
     * <p>
     * Header id=1000000007が削除済み
     * id=1999999999は存在しないレコード
     * Project id=0-1111-0
     *
     * Header id=1000000007, 1999999999の戻り値がNULLであることを確認。
     */
    // CHECKSTYLE:ON
    @Test
    public void testFindById02() {
        // CHECKSTYLE:OFF
        Long[] headerId = {1000000007L, 1999999999L};
        // CHECKSTYLE:ON
        for (int i = 0; i < headerId.length; i++) {
            DistTemplateHeader distTemplateHeader = distTemplateHeaderDao.findById(headerId[i]);
            Assert.assertNull(distTemplateHeader);
        }
    }

    /**
     * create(正常系).
     * <p>
     * データを作成し、作成したデータと元のデータを比較確認。
     */
    // CHECKSTYLE:ON
    @Test
    @Rollback(true)
    public void testCreate01() {
        DistTemplateHeader distTemplateHeader
            = createDistTemplateHeader(0,
                                       "21000",
                                       "2",
                                       "Header Name Create01 Test",
                                       "ZZ001",
                                       "ZZ002",
                                       1L);
        // create処理を実行
        Long id = distTemplateHeaderDao.create(distTemplateHeader);
        Assert.assertNotNull(id);
        Assert.assertTrue(0 < id);

        // 登録データと作成したデータを比較
        assertRow(id, distTemplateHeader, TEST_INSERTED);
    }

    /**
     * create(異常系. 必須パラメータ不足).
     * <p>
     * 一つずつ必須項目にNULLを指定し、どの場合も例外が発生することを確認。
     */
    // CHECKSTYLE:ON
    @Test
    @Rollback(true)
    public void testCreate02() {
        // CHECKSTYLE:OFF
        DistTemplateHeader[] distTemplateHeaders = new DistTemplateHeader[10];
        for(int i = 0; i < distTemplateHeaders.length; i++) {
            // 新規データの作成(必須全てが埋まっている状態)
            distTemplateHeaders[i]
                = createDistTemplateHeader(0,
                                           "2200" + i,
                                           "2",
                                           "Header Name Create02 Test" + i,
                                           "ZZ001",
                                           "ZZ002",
                                           0L);
        }
        // IDはSQLにて取得のためNULL指定から除外
        distTemplateHeaders[0] = null;
        distTemplateHeaders[1].setProjectId(null);
        distTemplateHeaders[2].setEmpNo(null);
        distTemplateHeaders[3].setTemplateCd(null);
        distTemplateHeaders[4].setName(null);
        distTemplateHeaders[5].setCreatedBy(null);
        distTemplateHeaders[6].setCreatedAt(null);
        distTemplateHeaders[7].setUpdatedBy(null);
        distTemplateHeaders[8].setUpdatedAt(null);
        distTemplateHeaders[9].setOption1(null);
        // CHECKSTYLE:ON

        // どの場合も例外が発生しなければならない
        for (int i = 0; i < distTemplateHeaders.length - 1; i++) {
            try {
                distTemplateHeaderDao.create(distTemplateHeaders[i]);
            } catch (IllegalArgumentException miae) {
                continue;
            }
            Assert.fail("Can't catch MerIllegalArgumentException.");
        }
        // option1は必須ではないのでエラーにならない
        // CHECKSTYLE:OFF
        distTemplateHeaderDao.create(distTemplateHeaders[9]);
        // CHECKSTYLE:ON
    }


    /**
     * delete(正常系).
     * <p>
     * データを作成し、データがあることを確認。最終更新者の確認。
     * 作成したデータを削除し、データが無いことを確認。
     * 最終更新者が変更されていることを確認。
     * ヴァージョン番号が変更されていることを確認。
     */
    @SuppressWarnings({ "rawtypes" })
    // CHECKSTYLE:ON
    @Test
    @Rollback(true)
    public void testDelete01() {
        DistTemplateHeader distTemplateHeader
            = createDistTemplateHeader(0,
                                       "31000",
                                       "2",
                                       "Header Name Delete01 Test",
                                       "ZZ001",
                                       "ZZ002",
                                       1L);
        // create処理を実行
        Long id = distTemplateHeaderDao.create(distTemplateHeader);
        // 削除までのテストデータ
        distTemplateHeader.setId(id);

        try {
            // 作成したデータを論理削除
            distTemplateHeader.setUpdatedBy("AA002");
            String deleteAt = "2020/11/22 11:22:33";
            distTemplateHeader.setUpdatedAt(parseTimestamp(deleteAt));
            Integer count = distTemplateHeaderDao.delete(distTemplateHeader);
            Assert.assertEquals(1, count.intValue());
            Map deleteAfterMap = getDistTemplateHeaderMap(id,
                    "distTemplateHeader.testFindByAfterUpdate");
            // 最終更新者が変更されていること
            Assert.assertTrue(
                    0L < ((BigDecimal) deleteAfterMap.get("DELETE_NO")).longValue());
            Assert.assertEquals(distTemplateHeader.getUpdatedBy(),
                    deleteAfterMap.get("UPDATED_BY"));
            Assert.assertEquals(
                    super.formatTimestamp(distTemplateHeader.getUpdatedAt()),
                    deleteAfterMap.get("UPDATED_AT"));
            Assert.assertEquals(
                    2L, ((BigDecimal) deleteAfterMap.get("VERSION_NO")).longValue());
        } catch (Exception e) {
            Assert.fail(e.toString());
        }
    }

    /**
     * delete(異常系).
     * Header idが一致しないために削除できない
     * <p>
     *
     * Header id=1999999999で検証(存在しないID)
     *
     * 例外が発生することを確認。
     */
    // CHECKSTYLE:ON
    @Test
    @Rollback(true)
    public void testDelete02() {
        DistTemplateHeader distTemplateHeader
            = createDistTemplateHeader(0,
                                       "32000",
                                       "2",
                                       "Header Name Delete02 Test",
                                       "ZZ001",
                                       "ZZ002",
                                       0L);
        // create処理を実行
        distTemplateHeaderDao.create(distTemplateHeader);
        // CHECKSTYLE:OFF
        distTemplateHeader.setId(1999999999L);
        // CHECKSTYLE:ON
        distTemplateHeader.setUpdatedBy("ZZ003");
        String deleteAt = "2020/11/22 11:22:33";
        distTemplateHeader.setUpdatedAt(parseTimestamp(deleteAt));
        distTemplateHeader.setVersionNo(2L);
        distTemplateHeader.setDeleteNo(0L);

        Integer count = distTemplateHeaderDao.delete(distTemplateHeader);
        Assert.assertEquals(0, count.intValue());
    }

    /**
     * delete(異常系).
     * idとversionNoの組み合わせが一致しないために削除できない
     * <p>
     *
     * Version No=が0のためMerRecordNotUpdatedExceptionが発生することを確認。
     */
    // CHECKSTYLE:ON
    @Test
    @Rollback(true)
    public void testDelete03() {
        DistTemplateHeader distTemplateHeader
            = createDistTemplateHeader(0,
                                       "33000",
                                       "2",
                                       "Header Name Delete03 Test",
                                       "ZZ001",
                                       "ZZ002",
                                       0L);
        // create処理を実行
        Long id = distTemplateHeaderDao.create(distTemplateHeader);
        distTemplateHeader.setId(id);
        distTemplateHeader.setUpdatedBy("ZZ003");
        String deleteAt = "2020/11/22 11:22:33";
        distTemplateHeader.setUpdatedAt(parseTimestamp(deleteAt));
        // CHECKSTYLE:OFF
        distTemplateHeader.setVersionNo(0L);
        // CHECKSTYLE:ON
        distTemplateHeader.setDeleteNo(0L);
        distTemplateHeader.setProjectId("deleted");

        Integer count = distTemplateHeaderDao.delete(distTemplateHeader);
        Assert.assertEquals(0, count.intValue());
    }

    /**
     * update(正常系).
     * <p>
     *
     * データを作成し、作成したデータを確認。
     * データを更新。
     * 更新対象列の更新を確認。
     * 非更新対象列の非更新を確認。
     */
    // CHECKSTYLE:ON
    @Test
    @Rollback(true)
    public void testUpdate01() {
        DistTemplateHeader distTemplateHeader
            = createDistTemplateHeader(0,
                                       "41000",
                                       "2",
                                       "Header Name Update01 Test",
                                       "ZZ001",
                                       "ZZ002",
                                       0L);
        // create処理を実行
        Long id = distTemplateHeaderDao.create(distTemplateHeader);
        distTemplateHeader.setId(id);
        distTemplateHeader.setVersionNo(1L);
        distTemplateHeader.setDeleteNo(0L);

        // 更新用データを作成
        DistTemplateHeader updateDistTempHeader = distTemplateHeader.clone();
        updateDistTempHeader.setTemplateCd("3");
        updateDistTempHeader.setName("Header Name Updated!!");
        updateDistTempHeader.setUpdatedBy("AA001");
        String updateAt = "2020/11/22 11:22:33";
        updateDistTempHeader.setUpdatedAt(parseTimestamp(updateAt));

        try {
            Integer count = distTemplateHeaderDao.update(updateDistTempHeader);
            Assert.assertEquals(1, count.intValue());
            // 更新対象の確認
            updateDistTempHeader.setVersionNo(distTemplateHeader.getVersionNo() + 1);
            asserUpdatedRow(id, updateDistTempHeader, TEST_UPDATED);
            asserUpdatedRow(id, distTemplateHeader, TEST_NOT_UPDATED);
        } catch (Exception e) {
            Assert.fail(e.toString());
        }
    }

    /**
     * update(異常系).
     * idとProjectNoの組み合わせが一致しないために更新できない
     * <p>
     *
     * MerRecordNotUpdatedExceptionが発生することを確認。
     */
    // CHECKSTYLE:ON
    @Test
    @Rollback(true)
    public void testUpdate02() {
        DistTemplateHeader distTemplateHeader
            = createDistTemplateHeader(0,
                                      "42000",
                                      "2",
                                      "Header Name Update02 Test",
                                      "ZZ001",
                                      "ZZ002",
                                      0L);
        // create処理を実行
        Long id = distTemplateHeaderDao.create(distTemplateHeader);
        // Logicで設定する値を格納
        distTemplateHeader.setId(id);
        distTemplateHeader.setVersionNo(1L);
        distTemplateHeader.setDeleteNo(0L);

        // 更新用データを作成
        DistTemplateHeader updateDistTempHeader = distTemplateHeader.clone();
        updateDistTempHeader.setProjectId("updated");
        updateDistTempHeader.setTemplateCd("3");
        updateDistTempHeader.setName("Header Name Updated!!");
        updateDistTempHeader.setOption1("Y");
        updateDistTempHeader.setUpdatedBy("AA001");
        String updateAt = "2020/11/22 11:22:33";
        updateDistTempHeader.setUpdatedAt(parseTimestamp(updateAt));

        Integer count = distTemplateHeaderDao.update(updateDistTempHeader);
        Assert.assertEquals(0, count.intValue());
    }

    /**
     * update(異常系).
     * idとversionNoが一致しないために更新できない
     * <p>
     *
     * MerRecordNotUpdatedExceptionが発生することを確認。
     */
    // CHECKSTYLE:ON
    @Test
    @Rollback(true)
    public void testUpdate03() {
        DistTemplateHeader distTemplateHeader
            = createDistTemplateHeader(0,
                                      "43000",
                                      "2",
                                      "Header Name Update03 Test",
                                      "ZZ001",
                                      "ZZ002",
                                      0L);
        // create処理を実行
        Long id = distTemplateHeaderDao.create(distTemplateHeader);
        // Logicで設定する値を格納
        distTemplateHeader.setId(id);
        distTemplateHeader.setVersionNo(1L);
        distTemplateHeader.setDeleteNo(0L);

        // 更新用データを作成
        DistTemplateHeader updateDistTempHeader = distTemplateHeader.clone();
        updateDistTempHeader.setTemplateCd("3");
        updateDistTempHeader.setName("Header Name Updated!!");
        updateDistTempHeader.setOption1("Y");
        updateDistTempHeader.setUpdatedBy("AA001");
        String updateAt = "2020/11/22 11:22:33";
        updateDistTempHeader.setUpdatedAt(parseTimestamp(updateAt));
        // CHECKSTYLE:OFF
        updateDistTempHeader.setVersionNo(100L);
        // CHECKSTYLE:ON

        Integer count = distTemplateHeaderDao.update(updateDistTempHeader);
        Assert.assertEquals(0, count.intValue());
    }


    /**
     * findByProjectIdForUpdate(正常系).
     * プロジェクトの通番が取得出来ていることを確認する。
     * <p>
     * project id=0-2222-1で検証しProjectID内での通番の最大値であることを確認。
     */
    // CHECKSTYLE:ON
    @Test
    @Rollback(true)
    public void testFindByProjectIdForUpdate01() {

        String templateCd = distTemplateHeaderDao.getTemplateCodeByProject("0-2222-1");
        Assert.assertEquals("11", templateCd);
    }

    /**
     * findByProjectIdForUpdate(正常系).
     * プロジェクトIDが存在しないため1番が採番されることを確認。
     * <p>
     * Project id=1999999999で検証。
     */
    // CHECKSTYLE:ON
    @Test
    @Rollback(true)
    public void testFindByProjectIdForUpdate02() {
        try {
            String templateCd = distTemplateHeaderDao.getTemplateCodeByProject("1999999999");
            // Project idが未設定の場合は1番を採番することを確認
            Assert.assertEquals("1", templateCd);
        } catch (IllegalArgumentException e) {
            Assert.fail("catch IllegalArgumentException.");
        }
    }

    /**
     * findByProjectIdForUpdate(異常系).
     * プロジェクトIDがNULLのためエラーが発生することを確認。
     * <p>
     */
    // CHECKSTYLE:ON
    @Test(expected = IllegalArgumentException.class)
    @Rollback(true)
    public void testFindByProjectIdForUpdate03() {
        distTemplateHeaderDao.getTemplateCodeByProject(null);
    }

    /**
     * テストユーティリティを使用してDistTemplateHeaderを1件取得する.
     * @param id id
     * @param queryId クエリーid
     * @return DistTemplateHeader
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private Map getDistTemplateHeaderMap(Long id, String queryId) {
        HashMap param = new HashMap();
        param.put("id", id);
        List<Map> distTemplateHeaderList
            = distTestUtilDaoImpl.select(queryId, param);
        Assert.assertNotNull(distTemplateHeaderList);
        Assert.assertEquals(1, distTemplateHeaderList.size());
        return distTemplateHeaderList.get(0);
    }

    /**
     * 1行分のレコードを検証します.
     * @param id 対象のレコードid
     * @param distTemplateHeader 検証対象のオブジェクト
     */
    @SuppressWarnings({ "rawtypes" })
    private void assertRow(Long id,
                                 DistTemplateHeader distTemplateHeader,
                                 String[] fields) {
        Map expMap = getDistTemplateHeaderMap(id, "distTemplateHeader.testFindById");
        assertDistTemplateHeader(expMap, distTemplateHeader, fields);
    }

    /**
     * 1行分のレコードを検証します.
     * @param id 対象のレコードid
     * @param distTemplateHeader 検証対象のオブジェクト
     */
    @SuppressWarnings("rawtypes")
    private void asserUpdatedRow(Long id,
                                 DistTemplateHeader distTemplateHeader,
                                 String[] fields) {
        Map expMap = getDistTemplateHeaderMap(id, "distTemplateHeader.testFindByAfterUpdate");
        assertDistTemplateHeader(expMap, distTemplateHeader, fields);
    }

    /**
     * Map型とDistTemplateHeader型のレコードを比較します.
     * @param map Map型のレコードオブジェクト
     * @param distTemplateHeader DistTemplateHeader型のレコードオブジェクト
     *
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void assertDistTemplateHeader(Map map,
                                         DistTemplateHeader distTemplateHeader,
                                         String[] fields) {

        AssertMapComparer comp = new AssertMapComparer(map, distTemplateHeader);
        comp.addCompareKey(fields);
        // String型以外の設定
        comp.updateCompareKey("id", AssertCompareType.BIGDECIMAL);
        comp.updateCompareKey("application_type", AssertCompareType.BIGDECIMAL);
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
