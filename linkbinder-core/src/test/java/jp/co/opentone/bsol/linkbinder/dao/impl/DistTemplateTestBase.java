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

import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.time.DateUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import jp.co.opentone.bsol.framework.test.util.AssertMapComparer;
import jp.co.opentone.bsol.linkbinder.dao.DaoFinder;
import jp.co.opentone.bsol.linkbinder.dto.DistTemplateGroup;
import jp.co.opentone.bsol.linkbinder.dto.DistTemplateHeader;
import jp.co.opentone.bsol.linkbinder.dto.code.DistributionType;
import junit.framework.Assert;

/**
 * Daoテストクラスの基底クラスです.
 * @author opentone
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
                "classpath:scope.xml",
                "classpath:applicationContextTest.xml",
                "classpath:daoContextTest.xml"
                })
@Transactional
@Rollback
//public abstract class DistTemplateTestBase extends AbstractDaoTestCase {
public abstract class DistTemplateTestBase {

    /**
     * Loggerインスタンス.
     */
    // CHECKSTYLE:OFF
    protected Logger log = LoggerFactory.getLogger(getClass());
    // CHECKSTYLE:ON

    /**
     * テストユーティリティDao.
     */
    @Autowired
    // CHECKSTYLE:OFF
    protected DistTemplateDaoTestUtil distTestUtilDaoImpl;
    // CHECKSTYLE:ON

    /**
     * 日付を比較する際の変換書式.
     */
    protected static final String FORMAT_DATE = AssertMapComparer.FORMAT_DATE;

    /**
     * 時刻を比較する際の変換書式.
     */
    protected static final String FORMAT_TIME = AssertMapComparer.FORMAT_TIME;

    /**
     * Dao取得クラス.
     */
    @Resource
    // CHECKSTYLE:OFF
    protected DaoFinder daoFinder;
    // CHECKSTYLE:ON

    /**
     * 各テストクラス実行前の初期処理.
     * @throws Exception
     *             Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    /**
     * 各テストクラス実行後の終了処理.
     * @throws Exception 例外
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    /**
     * 各テストメソッド実行前の初期処理.
     * @throws Exception
     *             Exception
     */
    @Before
    public void setUp() throws Exception {
    }

    /**
     * 各テストメソッド実行後の終了処理.
     * @throws Exception 例外
     */
    @After
    public void tearDown() throws Exception {
    }

    /**
     * 日付書式の文字列を返します.
     * @param date 日付
     * @return 日付書式の文字列. 引数がnullの場合はnullを返します.
     */
    public String formatDate(Date date) {
        String result = null;
        if (null != date) {
            SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_DATE);
            result = sdf.format(date);
        }
        return result;
    }

    /**
     * 文字列を日付に変換します.
     * @param value 日付文字列
     * @return 変換した日付. 変換不可の場合はnullを返します.
     */
    public Date parseDate(String value) {
        Date result = null;
        // CHECKSTYLE:OFF
        try {
            result = DateUtils.parseDate(value, new String[] {FORMAT_DATE});
        } catch (ParseException pe) {
            // 変換不可の場合はnullを返します.
        }
        // CHECKSTYLE:ON
        return result;
    }

    /**
     * 時刻書式の文字列を返します.
     * @param timestamp 時刻
     * @return 日付書式の文字列. 引数がnullの場合はnullを返します.
     */
    public String formatTimestamp(Date timestamp) {
        String result = null;
        if (null != timestamp) {
            SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_TIME);
            result = sdf.format(timestamp);
        }
        return result;
    }

    /**
     * 文字列を時刻に変換します.
     * @param value 時刻文字列
     * @return 変換した時刻. 変換不可の場合はnullを返します.
     */
    public Date parseTimestamp(String value) {
        Date result = null;
        // CHECKSTYLE:OFF
        try {
            result = DateUtils.parseDate(value, new String[] {FORMAT_TIME});
        } catch (ParseException pe) {
            // 変換不可の場合はnullを返します.
        }
        // CHECKSTYLE:ON
        return result;
    }

    /**
     * McMasterを比較します.
     * @param expected 期待値
     * @param actual 比較値
     * @param fields 比較対象とするフィールド名配列
     */
    protected void assertEquals(Object expected, Object actual, String[] fields) {
        Assert.assertNotNull(expected);
        Assert.assertNotNull(actual);
        Assert.assertNotNull(fields);

        for (String field : fields) {
            try {
                String methodName
                    = String.format("get%s%s",
                            field.substring(0, 1).toUpperCase(), field.substring(1));
                Method expMethod = expected.getClass().getMethod(methodName, (Class[]) null);
                Object expObj = expMethod.invoke(expected, (Object[]) null);
                Method actMethod = actual.getClass().getMethod(methodName, (Class[]) null);
                Object actObj = actMethod.invoke(actual, (Object[]) null);
                Assert.assertEquals(expObj, actObj);
            } catch (Exception e) {
                Assert.fail(e.toString());
            }
        }
    }


    /**
     * テストデータを作成する.
     */
    protected void createDistHeaderTestData() {
        // 既存の参照用テストデータ以外を削除
        distTestUtilDaoImpl.delete("distTemplateGroup.testClearTable");
        distTestUtilDaoImpl.delete("distTemplateHeader.testClearTable");
        for (String[] data : TEST_DATAS) {
            DistTemplateHeaderDaoImplTest impl = new DistTemplateHeaderDaoImplTest();
            // CHECKSTYLE:OFF
            DistTemplateHeader header
                = impl.createDistTemplateHeader(Long.parseLong(data[0]),
                                                data[1],
                                                data[2],
                                                data[3],
                                                data[7],
                                                data[8],
                                                1L,
                                                Long.parseLong(data[9]));
            // CHECKSTYLE:ON
            distTestUtilDaoImpl.insert("distTemplateHeader.testCreate", header);
        }
    }

    /**
     * insertやupdateテスト用のDistTemplateHeaderを1件作成します.
     * @param id DistTemplateGroupのid
     * @param projectId projectId
     * @param templateCd templateCd
     * @param name name
     * @param createdBy DistTemplateGroupのcreated_by
     * @param updatedBy DistTemplateGroupのupdated_by
     * @param versionNo versionNo
     * @return DistTemplateGroup
     */
    protected DistTemplateHeader createDistTemplateHeader(long id,
                                                           String projectId,
                                                           String templateCd,
                                                           String name,
                                                           String createdBy,
                                                           String updatedBy,
                                                           Long versionNo) {
        return createDistTemplateHeader(id,
                                         projectId,
                                         templateCd,
                                         name,
                                         createdBy,
                                         updatedBy,
                                         versionNo,
                                         0L);
    }

    /**
     * insertやupdateテスト用のDistTemplateHeaderを1件作成します.
     * @param id DistTemplateGroupのid
     * @param projectId projectId
     * @param templateCd templateCd
     * @param name name
     * @param createdBy DistTemplateGroupのcreated_by
     * @param updatedBy DistTemplateGroupのupdated_by
     * @param versionNo versionNo
     * @param deleteNo deleteNo
     * @return DistTemplateGroup
     */
    protected DistTemplateHeader createDistTemplateHeader(long id,
                                                           String projectId,
                                                           String templateCd,
                                                           String name,
                                                           String createdBy,
                                                           String updatedBy,
                                                           Long versionNo,
                                                           Long deleteNo) {
        DistTemplateHeader distTemplateHeader = new DistTemplateHeader();
        distTemplateHeader.setId(id);
        distTemplateHeader.setProjectId(projectId);
        distTemplateHeader.setEmpNo("00000");
        distTemplateHeader.setTemplateCd(templateCd);
        distTemplateHeader.setName(name);
        distTemplateHeader.setCreatedBy(createdBy);
        String createdAt = "2010/04/23 45:54:32";
        distTemplateHeader.setCreatedAt(parseTimestamp(createdAt));
        distTemplateHeader.setUpdatedBy(updatedBy);
        String updateAt = "2010/02/15 12:34:56";
        distTemplateHeader.setUpdatedAt(parseTimestamp(updateAt));
        distTemplateHeader.setVersionNo(versionNo);
        distTemplateHeader.setDeleteNo(deleteNo);
        return distTemplateHeader;
    }

    /**
     * insertやupdateテスト用のDistTemplateGroupを1件作成します.
     * @param id DistTemplateGroupのid
     * @param headerId DistTemplateGroupのdist_template_header_id
     * @param distributionType DistTemplateGroupのdistribution_type
     * @param orderNo DistTemplateGroupのorder_no
     * @param groupId DistTemplateGroupのgroup_id
     * @param createdBy DistTemplateGroupのcreated_by
     * @param updatedBy DistTemplateGroupのupdated_by
     * @return DistTemplateGroup
     */
    protected DistTemplateGroup createDistTemplateGroup(long id,
                                                         long headerId,
                                                         String distributionType,
                                                         long orderNo,
                                                         long groupId,
                                                         String createdBy,
                                                         String updatedBy) {
        DistTemplateGroup distTemplateGroup = new DistTemplateGroup();
        distTemplateGroup.setId(id);
        distTemplateGroup.setGroupName("test");
        distTemplateGroup.setDistTemplateHeaderId(headerId);
        Map<String, DistributionType> typeMap = new HashMap<String, DistributionType>();
        typeMap.put("TO", DistributionType.TO);
        typeMap.put("CC", DistributionType.CC);
        distTemplateGroup.setDistributionType(typeMap.get(distributionType));
        distTemplateGroup.setOrderNo(orderNo);
        distTemplateGroup.setGroupId(groupId);
        distTemplateGroup.setCreatedBy(createdBy);
        String createDate = "2012/03/14 05:43:21";
        distTemplateGroup.setCreatedAt(parseTimestamp(createDate));
        distTemplateGroup.setUpdatedBy(updatedBy);
        distTemplateGroup.setUpdatedAt(distTemplateGroup.getCreatedAt());
        return distTemplateGroup;
    }

    /**
     * 検索系処理で使用するテストデータ.
     */
    // CHECKSTYLE:OFF
    protected static final String[][] TEST_DATAS = {
        // id, project_id, template_cd, name, option1, option2, option3, created_by, updated_by, delete_no
        // ただし、option1-3は現状は不要な列です.
        { "1000000001", "0-1111-0", "1", "Distribution Header-1", "", "", "", "ZZA01", "ZZA16", "0" },
        { "1000000002", "0-1111-1", "2", "Distribution Header-2", "B", "B", "B", "ZZA02", "ZZA17", "0" },
        { "1000000003", "0-1111-2", "3", "Distribution Header-3", "D", "D", "D", "ZZA03", "ZZA18", "0" },
        { "1000000004", "0-1111-3", "4", "Distribution Header-4", "C", "C", "C", "ZZA04", "ZZA01", "0" },
        { "1000000005", "0-1111-4", "5", "Distribution Header-5", "", "", "", "ZZA05", "ZZA02", "0" },
        { "1000000006", "0-1111-5", "6", "Distribution Header-6", "F", "F", "F", "ZZA06", "ZZA03", "0" },
        { "1000000007", "0-1111-0", "1", "Distribution Header-7", "", "", "", "ZZA07", "ZZA04", "1" },
        { "1000000008", "0-1111-0", "2", "Distribution Header-8", "H", "H", "H", "ZZA08", "ZZA05", "0" },
        { "1000000009", "0-1111-0", "3", "Distribution Header-9", "Z", "Z", "Z", "ZZA09", "ZZA06", "0" },
        { "1000000010", "0-1111-0", "4", "Apple Header－1", "W", "W", "W", "ZZA11", "ZZA07", "1" },
        { "1000000011", "0-1111-0", "5", "Apple Header－2", "WW", "WW", "WW", "ZZA12", "ZZA08", "0" },
        { "1000000012", "0-1111-0", "6", "Apple Header－3", "YYY", "YYY", "YYY", "ZZA13", "ZZA09", "0" },
        { "1000000013", "0-1111-0", "7", "Apple Header－4", "1", "1", "1", "ZZA14", "ZZA11", "0" },
        { "1000000014", "0-1111-0", "8", "Orange Header-1", "A", "A", "A", "ZZA15", "ZZA12", "0" },
        { "1000000015", "0-1111-0", "9", "Orange Header-2", "3", "3", "3", "ZZA16", "ZZA13", "1" },
        { "1000000016", "0-1111-0", "10", "Orange Header-3", "5", "5", "5", "ZZA17", "ZZA14", "0" },
        { "1000000017", "0-1111-0", "11", "Orange Header-4", "", "", "", "ZZA18", "ZZA15", "1" },
        { "1000000021", "0-1111-1", "1", "Orange Header-5", "8", "8", "8", "ZZA04", "ZZA16", "0" },
        { "1000000022", "0-1111-1", "2", "Apple Header－12", "9", "9", "9", "ZZA04", "ZZA17", "1" },
        { "1000000023", "0-1111-1", "3", "Apple Header－13", "0", "0", "0", "ZZA04", "ZZA18", "0" },
        { "1000000024", "0-1111-1", "4", "Apple Header－14", "A", "A", "A", "ZZA04", "ZZA16", "0" },
        { "1000000025", "0-1111-1", "5", "Apple Header－15", "6", "6", "6", "ZZA04", "ZZA17", "0" },
        { "1000000026", "0-2222-1", "1", "Dist Header-201", "J", "J", "J", "ZZA01", "ZZA08", "0" },
        { "1000000027", "0-2222-1", "2", "Dist Header-202", "J", "J", "J", "ZZA02", "ZZA09", "0" },
        { "1000000028", "0-2222-1", "3", "Dist Header-203", "J", "J", "J", "ZZA03", "ZZA11", "0" },
        { "1000000029", "0-2222-1", "4", "Dist Header-204", "J", "J", "J", "ZZA04", "ZZA12", "0" },
        { "1000000030", "0-2222-1", "5", "Dist Header-205", "D", "D", "D", "ZZA05", "ZZA12", "0" },
        { "1000000031", "0-2222-1", "6", "Dist Header-206", "S", "S", "S", "ZZA06", "ZZA18", "0" },
        { "1000000032", "0-2222-1", "7", "Dist Header-207", "A", "A", "A", "ZZA06", "ZZA16", "0" },
        { "1000000033", "0-2222-1", "8", "Dist Header-208", "1", "1", "1", "ZZA06", "ZZA17", "0" },
        { "1000000034", "0-2222-1", "9", "Dist Header-209", "9", "9", "9", "ZZA06", "ZZA12", "0" },
        { "1000000035", "0-2222-1", "10", "Dist Header-210", "5", "5", "5", "ZZA06", "ZZA18", "0" },
        { "1000000036", "0-2222-2", "1", "Dist Header-211", "3", "3", "3", "ZZA06", "ZZA16", "0" },
        { "1000000037", "0-2222-2", "2", "Dist Header-212", "2", "2", "2", "ZZA06", "ZZA17", "0" },
        { "1000000038", "0-2222-2", "3", "Dist Header-213", "3", "3", "3", "ZZA06", "ZZA18", "0" },
        { "1000000039", "0-2222-2", "4", "Dist Header-214", "1", "1", "1", "ZZA06", "ZZA16", "0" },
        { "1000000040", "0-2222-2", "5", "Dist Header-215", "4", "4", "4", "ZZA06", "ZZA17", "0" },
        { "1000000041", "0-2222-2", "6", "Dist Header-216", "3", "3", "3", "ZZA06", "ZZA18", "0" },
        { "1000000042", "0-2222-2", "7", "Dist Header-217", "6", "6", "6", "ZZA06", "ZZA16", "0" },
        { "1000000043", "0-2222-2", "8", "Dist Header-218", "Y", "Y", "Y", "ZZA06", "ZZA17", "0" },
        { "1000000044", "0-2222-2", "9", "Dist Header-219", "P", "P", "P", "ZZA06", "ZZA12", "0" },
        { "1000000045", "0-2222-3", "1", "testFindDistTemplateList01-Name01", "a", "a", "a", "ZZA05", "ZZA07", "0" },
        { "1000000046", "0-2222-3", "2", "testFindDistTemplateList01-Name02", "b", "b", "b", "ZZA05", "ZZA07", "0" },
        { "1000000047", "0-2222-3", "3", "testFindDistTemplateList01-Name03", "c", "c", "c", "ZZA05", "ZZA07", "0" },
        { "1000000048", "0-2222-3", "4", "testFindDistTemplateList01-Name04", "d", "d", "d", "ZZA05", "ZZA07", "0" },
        { "1000000049", "0-2222-3", "5", "testFindDistTemplateList01-Name05", "e", "e", "e", "ZZA05", "ZZA07", "0" },
        { "1000000050", "0-2222-3", "6", "testFindDistTemplateList01-Name06", "f", "f", "f", "ZZA05", "ZZA07", "0" },
        { "1000000051", "0-2222-3", "7", "testFindDistTemplateList01-Name07", "g", "g", "g", "ZZA05", "ZZA07", "0" },
        { "1000000052", "0-2222-3", "8", "testFindDistTemplateList01-Name08", "h", "h", "h", "ZZA05", "ZZA07", "0" },
        { "1000000053", "0-2222-3", "9", "testFindDistTemplateList01-Name09", "i", "i", "i", "ZZA05", "ZZA07", "0" },
        { "1000000054", "0-2222-3", "10", "testFindDistTemplateList01-Name10", "j", "j", "j", "ZZA05", "ZZA07", "0" },
        { "1000000055", "0-2222-3", "11", "testFindDistTemplateList01-Name11", "k", "k", "k", "ZZA05", "ZZA07", "0" },
        { "1000000056", "0-2222-3", "12", "testFindDistTemplateList01-Name12", "l", "l", "l", "ZZA05", "ZZA07", "0" },
        { "1000000057", "0-2222-3", "13", "testFindDistTemplateList01-Name13", "m", "m", "m", "ZZA05", "ZZA07", "0" },
        { "1000000058", "0-2222-3", "14", "testFindDistTemplateList01-Name14", "n", "n", "n", "ZZA05", "ZZA07", "0" },
        { "1000000059", "0-2222-3", "15", "testFindDistTemplateList01-Name15", "o", "o", "o", "ZZA05", "ZZA07", "0" },
        { "1000000060", "0-2222-3", "16", "testFindDistTemplateList01-Name16", "p", "p", "p", "ZZA05", "ZZA07", "0" },
        { "1000000061", "0-2222-3", "17", "testFindDistTemplateList01-Name17", "q", "q", "q", "ZZA05", "ZZA07", "0" },
        { "1000000062", "0-2222-3", "18", "testFindDistTemplateList01-Name18", "r", "r", "r", "ZZA05", "ZZA07", "0" },
        { "1000000063", "0-2222-3", "19", "testFindDistTemplateList01-Name19", "s", "s", "s", "ZZA05", "ZZA07", "0" },
        { "1000000064", "0-2222-3", "20", "testFindDistTemplateList01-Name20", "t", "t", "t", "ZZA05", "ZZA07", "0" },
        { "1000000065", "0-2222-3", "21", "testFindDistTemplateList01-Name21", "u", "u", "u", "ZZA05", "ZZA07", "0" },
        { "1000000066", "0-2222-4", "1", "testUpdate02-Name01", "1", "1", "1", "ZZA08", "ZZA09", "0" },
        { "1000000067", "0-2222-4", "2", "testUpdate02-Name02", "2", "2", "2", "ZZA08", "ZZA09", "0" },
        { "1000000068", "0-2222-4", "3", "testUpdate02-Name03", "3", "3", "3", "ZZA08", "ZZA09", "0" },
        { "1000000069", "0-2222-4", "4", "testUpdate02-Name04", "4", "4", "4", "ZZA08", "ZZA09", "0" },
        { "1000000070", "0-2222-4", "5", "testUpdate02-Name05", "5", "5", "5", "ZZA08", "ZZA09", "0" },
        { "1000000071", "0-2222-4", "6", "testUpdate02-Name06", "6", "6", "6", "ZZA08", "ZZA09", "0" },
        { "1000000072", "0-1111-0", "12", "Dist Header Name -01", "9", "9", "9", "ZZA04", "ZZA11", "0" },
        { "1000000073", "0-1111-0", "13", "Dist Header Name -02", "8", "8", "8", "ZZA04", "ZZA11", "0" },
        { "1000000074", "0-1111-0", "14", "Dist Header Name -03", "7", "7", "7", "ZZA04", "ZZA11", "0" },
        { "1000000075", "0-1111-0", "15", "Dist Header Name -04", "6", "6", "6", "ZZA04", "ZZA11", "0" },
        { "1000000076", "0-1111-0", "16", "Dist Header Name -05", "5", "5", "5", "ZZA04", "ZZA11", "0" },
        { "1000000077", "0-1111-0", "17", "Dist Header Name -06", "4", "4", "4", "ZZA04", "ZZA11", "0" },
        { "1000000078", "0-1111-0", "18", "Dist Header Name -07", "3", "3", "3", "ZZA04", "ZZA11", "0" },
        { "1000000079", "0-1111-0", "19", "Dist Header Name -08", "2", "2", "2", "ZZA04", "ZZA11", "0" },
        { "1000000080", "0-1111-0", "20", "Dist Header Name -09", "1", "1", "1", "ZZA04", "ZZA11", "0" },
        { "1000000081", "0-1111-0", "21", "Dist Header Name -10", "a", "a", "a", "ZZA04", "ZZA11", "0" },
        { "1000000082", "0-1111-0", "22", "Dist Header Name -11", "b", "b", "b", "ZZA04", "ZZA11", "0" },
        { "1000000083", "0-1111-0", "23", "Dist Header Name -12", "c", "c", "c", "ZZA04", "ZZA11", "0" },
        { "1000000084", "0-1111-0", "24", "Dist Header Name -13", "d", "d", "d", "ZZA04", "ZZA11", "0" },
        { "1000000085", "0-1111-0", "25", "Dist Header Name -14", "e", "e", "e", "ZZA04", "ZZA11", "0" },
        { "1000000086", "0-1111-0", "26", "Dist Header Name -15", "f", "f", "f", "ZZA04", "ZZA11", "0" },
        { "1000000087", "0-1111-0", "27", "Dist Header Name -16", "g", "g", "g", "ZZA04", "ZZA11", "0" },
        { "1000000088", "0-1111-0", "28", "Dist Header Name -17", "h", "h", "h", "ZZA04", "ZZA11", "0" },
        { "1000000089", "0-1111-0", "29", "Dist Header Name -18", "i", "i", "i", "ZZA04", "ZZA11", "0" },
        { "1000000090", "0-1111-0", "30", "Dist Header Name -19", "j", "j", "j", "ZZA04", "ZZA11", "0" },
        { "1000000091", "0-1111-0", "31", "Dist Header Name -20", "k", "k", "k", "ZZA04", "ZZA11", "0" },
        { "1000000092", "0-1111-0", "32", "Dist Header Name -21", "l", "l", "l", "ZZA04", "ZZA11", "0" },
    };
    // CHECKSTYLE:ON
}
