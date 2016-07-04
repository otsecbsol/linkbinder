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

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.annotation.Resource;

import org.dbunit.dataset.ITable;
import org.junit.Test;

import jp.co.opentone.bsol.framework.core.dao.KeyDuplicateException;
import jp.co.opentone.bsol.framework.core.dao.RecordNotFoundException;
import jp.co.opentone.bsol.framework.core.dao.StaleRecordException;
import jp.co.opentone.bsol.framework.core.extension.ibatis.DBValue;
import jp.co.opentone.bsol.framework.core.extension.ibatis.exception.InvalidNullUpdatedRuntimeException;
import jp.co.opentone.bsol.framework.test.AbstractDaoTestCase;
import jp.co.opentone.bsol.linkbinder.dto.AddressCorresponGroup;
import jp.co.opentone.bsol.linkbinder.dto.Correspon;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroup;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroupSummary;
import jp.co.opentone.bsol.linkbinder.dto.CorresponResponseHistory;
import jp.co.opentone.bsol.linkbinder.dto.CorresponType;
import jp.co.opentone.bsol.linkbinder.dto.CorresponUserSummary;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.code.CorresponStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.ReadStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.ReplyRequired;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowProcessStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowStatus;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponCondition;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponUserSummaryCondition;

/**
 * CorresponDaoImplのテストケース.
 * @author opentone
 */
public class CorresponDaoImplTest extends AbstractDaoTestCase {

    /**
     * テスト対象.
     */
    @Resource
    private CorresponDaoImpl dao;

    /**
     * {@link CorresponDaoImpl#findById(Long)}のテストケース.
     * @throws Exception
     */
    @Test
    public void testFindById() throws Exception {
        Correspon c = dao.findById(1L);

        // 取得したオブジェクトと、Excelファイルに定義済の期待値を比較する
        assertNotNull(c);
        assertDataSetEquals(newDataSet("CorresponDaoImplTest_testFindById_expected.xls"), c);
    }

    /**
     * {@link CorresponDaoImpl#findById(Long)}のテストケース.
     * @throws Exception
     */
    @Test
    public void testFindById2() throws Exception {
        Correspon c = dao.findById(2L);

        // 取得したオブジェクトと、Excelファイルに定義済の期待値を比較する
        assertNotNull(c);
        assertDataSetEquals(newDataSet("CorresponDaoImplTest_testFindById2_expected.xls"), c);
    }

    /**
     * {@link CorresponDaoImpl#findById(Long)}のテストケース. レコードが無い場合に、
     * {@link RecordNotFoundException}が発生することを検証する.
     * @throws Exception
     */
    @Test(expected = RecordNotFoundException.class)
    public void testFindByIdRecordNotFound() throws Exception {
        dao.findById(-1L);
        fail("例外が発生していない");
    }

    /**
     * {@link CorresponDaoImpl#findById(Long)}のテストケース. レコードが削除済の場合に、
     * {@link RecordNotFoundException}が発生することを検証する.
     * @throws Exception
     */
    @Test(expected = RecordNotFoundException.class)
    public void testFindByIdRecordDeleted() throws Exception {
        dao.findById(3L); // 削除済レコードのID。 テストデータExcelを参照
        fail("例外が発生していない");
    }

    /**
     * 次の3つのメソッドに関するテストを一括実施するテストケース。(テスト実行速度向上を目的とする)
     * {@link CorresponDaoImpl#find(jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponCondition)}
     * {@link CorresponDaoImpl#count(jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponCondition)}
     * {@link CorresponDaoImpl#findId(jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponCondition)}
     */
    @Test
    public void testFindOnBatchExecution() throws Exception{
        testFind();
        testFindCondition1();
        testCountCondition1();
        testFindCondition2();
        testCountCondition2();
        testFindCondition3();
        testCountCondition3();
        testFindCondition4();
        testCountCondition4();
        testFindCondition5();
        testCountCondition5();
        testFindConditionCustomField1();
        testCountCustomField();
        testFindConditionCustomField2();
        testFindConditionCustomField3();
        testFindConditionCustomField4();
        testFindConditionCustomField5();
        testFindConditionCustomField6();
        testFindConditionCustomField7();
        testFindConditionCustomField8();
        testFindConditionCustomField9();
        testFindConditionCustomField10();
        testFindConditionIgnore1();
        testCountConditionIgnore1();
        testFindConditionIgnore2();
        testCountConditionIgnore2();
        testFindProjectAdmin();
        testCountProjectAdmin();
        testFindChecker();
        testCountChecker();
        testFindApprover();
        testCountApprover();
        testFindGroupAdmin();
        testCountGroupAdmin();
        testFindNormalUser();
        testCountNormalUser();
        testCount();
        testFindId();
    }

    /**
     * {@link CorresponDaoImpl#find(jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponCondition)}
     * のテストケース.
     * @throws Exception
     */
//  testFindOnBatchExecutionの一括実行対象
//  @Test
    public void testFind() throws Exception {
        SearchCorresponCondition condition = new SearchCorresponCondition();
        condition.setProjectId("PJ1");
        condition.setUserId("ZZA01");
        condition.setPageNo(2);
        condition.setPageRowNum(10);
        condition.setSort("id");
        condition.setAscending(false);

        List<Correspon> list = dao.find(condition);

        // 取得したオブジェクトと、Excelファイルに定義済の期待値を比較する
        assertNotNull(list);
        assertDataSetEquals(newDataSet("CorresponDaoImplTest_testFind_expected.xls"), list);
    }

    /**
     * {@link CorresponDaoImpl#find(jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponCondition)}
     * のテストケース.
     * 条件：No、CorresponNo、Type、WorkflowStatus、ReadStatus、Status
     * @throws Exception
     */
//  testFindOnBatchExecutionの一括実行対象
//  @Test
    public void testFindCondition1() throws Exception {
        SearchCorresponCondition condition = createFindCondition1();

        List<Correspon> list = dao.find(condition);

        // 取得したオブジェクトと、Excelファイルに定義済の期待値を比較する
        assertNotNull(list);
        assertDataSetEquals(newDataSet("CorresponDaoImplTest_testFindCondition1_expected.xls"), list);
    }

    /**
     * {@link CorresponDaoImpl#count(jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponCondition)}
     * のテストケース.
     * 条件：No、CorresponNo、Type、WorkflowStatus、ReadStatus、Status
     * @throws Exception
     */
//  testFindOnBatchExecutionの一括実行対象
//  @Test
    public void testCountCondition1() throws Exception {
        SearchCorresponCondition condition = createFindCondition1();

        int count = dao.count(condition);

        // 取得した件数と、Excelファイルに定義済の期待値の件数を比較する
        assertEquals(newDataSet(
            "CorresponDaoImplTest_testFindCondition1_expected.xls").getTable("correspon").getRowCount(), count);
    }

    /**
     * testFindCondition1用の検索条件オブジェクト生成
     * 条件：No、CorresponNo、Type、WorkflowStatus、ReadStatus、Status
     * @return 検索条件オブジェクト
     */
    private SearchCorresponCondition createFindCondition1() {
        SearchCorresponCondition condition = new SearchCorresponCondition();
        condition.setProjectId("0-5000-2");
        condition.setUserId("ZZA01");
        condition.setPageNo(1);
        condition.setPageRowNum(20);
        condition.setSort("id");
        condition.setAscending(true);

        condition.setSystemAdmin(true); // SystemAdmin
        condition.setFromId(26L);
        condition.setToId(34L);
        condition.setCorresponNo("YOC:OT:P"); // 前方一致
        CorresponType[] cType = new CorresponType[3];
        CorresponType type = new CorresponType();
        type.setProjectCorresponTypeId(11L);
        cType[0] = type;
        type = new CorresponType();
        type.setProjectCorresponTypeId(12L);
        cType[1] = type;
        type = new CorresponType();
        type.setProjectCorresponTypeId(13L);
        cType[2] = type;
        condition.setCorresponTypes(cType);
        WorkflowStatus[] wStatus = {WorkflowStatus.DENIED, WorkflowStatus.ISSUED};
        condition.setWorkflowStatuses(wStatus);
        ReadStatus[] rStatus = ReadStatus.values();
        condition.setReadStatuses(rStatus);
        CorresponStatus[] cStatus = {CorresponStatus.CLOSED};
        condition.setCorresponStatuses(cStatus);
        return condition;
    }

    /**
     * {@link CorresponDaoImpl#find(jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponCondition)}
     * のテストケース.
     * 条件：User（From）、User（Workflow）、WorkflowProcessStatus、Group（From）
     * @throws Exception
     */
//  testFindOnBatchExecutionの一括実行対象
//  @Test
    public void testFindCondition2() throws Exception {
        SearchCorresponCondition condition = createFindCondition2();

        List<Correspon> list = dao.find(condition);

        // 取得したオブジェクトと、Excelファイルに定義済の期待値を比較する
        assertNotNull(list);
        assertDataSetEquals(newDataSet("CorresponDaoImplTest_testFindCondition2_expected.xls"), list);
    }

    /**
     * {@link CorresponDaoImpl#count(jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponCondition)}
     * のテストケース.
     * 条件：User（From）、User（Workflow）、WorkflowProcessStatus、Group（From）
     * @throws Exception
     */
//  testFindOnBatchExecutionの一括実行対象
//  @Test
    public void testCountCondition2() throws Exception {
        SearchCorresponCondition condition = createFindCondition2();

        int count = dao.count(condition);

        // 取得した件数と、Excelファイルに定義済の期待値の件数を比較する
        assertEquals(newDataSet(
            "CorresponDaoImplTest_testFindCondition2_expected.xls").getTable("correspon").getRowCount(), count);
    }

    /**
     * testFindCondition2用の検索条件オブジェクト生成
     * 条件：User（From）、User（Workflow）、WorkflowProcessStatus、Group（From）
     * @return 検索条件オブジェクト
     */
    private SearchCorresponCondition createFindCondition2() {
        SearchCorresponCondition condition = new SearchCorresponCondition();
        condition.setProjectId("0-5000-2");
        condition.setUserId("ZZA01");
        condition.setPageNo(1);
        condition.setPageRowNum(20);
        condition.setSort("id");
        condition.setAscending(true);

        condition.setSystemAdmin(true); // SystemAdmin
        User user1 = new User();
        user1.setEmpNo("ZZA01");
        User user2 = new User();
        user2.setEmpNo("ZZA04");
        User user3 = new User();
        user3.setEmpNo("ZZA07");
        User[] addressUsers = {user1, user2, user3};
        condition.setFromUsers(addressUsers);
//        condition.setAddressUsers(addressUsers);
//        condition.setAddressFrom(true);
        User user = new User();
        user.setEmpNo("ZZA02");
        User[] workflowUsers = {user};
//        condition.setWorkflowUsers(workflowUsers);
        condition.setFromUsers(workflowUsers);
        condition.setUserPreparer(true);
        WorkflowProcessStatus[] workflowProcessStatuses = {WorkflowProcessStatus.NONE,
                                                           WorkflowProcessStatus.REQUEST_FOR_CHECK,
                                                           WorkflowProcessStatus.REQUEST_FOR_APPROVAL};
        condition.setWorkflowProcessStatuses(workflowProcessStatuses);
        CorresponGroup group = new CorresponGroup();
        group.setId(5L);
        CorresponGroup[] corresponGroups = {group};
        condition.setFromGroups(corresponGroups);
//        condition.setCorresponGroups(corresponGroups);
//        condition.setGroupFrom(true);
        return condition;
    }

    /**
     * {@link CorresponDaoImpl#find(jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponCondition)}
     * のテストケース.
     * 条件：CreatedOn（From、To）、UpdatedOn（From、To）、DeadlineForReply（From、To）
     * @throws Exception
     */
//  testFindOnBatchExecutionの一括実行対象
//  @Test
    public void testFindCondition3() throws Exception {
        SearchCorresponCondition condition = createFindCondition3();

        List<Correspon> list = dao.find(condition);

        // 取得したオブジェクトと、Excelファイルに定義済の期待値を比較する
        assertNotNull(list);
        assertDataSetEquals(newDataSet("CorresponDaoImplTest_testFindCondition3_expected.xls"), list);
    }

    /**
     * {@link CorresponDaoImpl#count(jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponCondition)}
     * のテストケース.
     * 条件：CreatedOn（From、To）、UpdatedOn（From、To）、DeadlineForReply（From、To）
     * @throws Exception
     */
//  testFindOnBatchExecutionの一括実行対象
//  @Test
    public void testCountCondition3() throws Exception {
        SearchCorresponCondition condition = createFindCondition3();

        int count = dao.count(condition);

        // 取得した件数と、Excelファイルに定義済の期待値の件数を比較する
        assertEquals(newDataSet(
            "CorresponDaoImplTest_testFindCondition3_expected.xls").getTable("correspon").getRowCount(), count);
    }

    /**
     * testFindCondition3用の検索条件オブジェクト生成
     * 条件：CreatedOn（From、To）、UpdatedOn（From、To）、DeadlineForReply（From、To）
     * @return 検索条件オブジェクト
     */
    private SearchCorresponCondition createFindCondition3() {
        SearchCorresponCondition condition = new SearchCorresponCondition();
        condition.setProjectId("0-5000-2");
        condition.setUserId("ZZA01");
        condition.setPageNo(1);
        condition.setPageRowNum(20);
        condition.setSort("id");
        condition.setAscending(true);

        condition.setSystemAdmin(true); // SystemAdmin
        Date date1 = new GregorianCalendar(2009, 3, 1).getTime();
        Date date2 = new GregorianCalendar(2009, 3, 10).getTime();
        Date date3 = new GregorianCalendar(2009, 3, 1).getTime();
        Date date4 = new GregorianCalendar(2009, 3, 10).getTime();
        Date date5 = new GregorianCalendar(2009, 3, 7).getTime();
        Date date6 = new GregorianCalendar(2009, 3, 16).getTime();
        condition.setFromCreatedOn(date1);
        condition.setToCreatedOn(date2);
        condition.setFromIssuedOn(date3);
        condition.setToIssuedOn(date4);
        condition.setFromDeadlineForReply(date5);
        condition.setToDeadlineForReply(date6);
        return condition;
    }

    /**
     * {@link CorresponDaoImpl#find(jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponCondition)}
     * のテストケース.
     * 条件： Including Revision、User(To(Attention), To(Charge), Cc)、Group（To、CC）
     * @throws Exception
     */
//  testFindOnBatchExecutionの一括実行対象
//  @Test
    public void testFindCondition4() throws Exception {
        SearchCorresponCondition condition = createFindCondition4();

        List<Correspon> list = dao.find(condition);

        // 取得したオブジェクトと、Excelファイルに定義済の期待値を比較する
        assertNotNull(list);
        assertDataSetEquals(newDataSet("CorresponDaoImplTest_testFindCondition4_expected.xls"), list);
    }

    /**
     * {@link CorresponDaoImpl#count(jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponCondition)}
     * のテストケース.
     * 条件： Including Revision、User(To(Attention), To(Charge), Cc)、Group（To、CC）
     * @throws Exception
     */
//  testFindOnBatchExecutionの一括実行対象
//  @Test
    public void testCountCondition4() throws Exception {
        SearchCorresponCondition condition = createFindCondition4();

        int count = dao.count(condition);

        // 取得した件数と、Excelファイルに定義済の期待値の件数を比較する
        assertEquals(newDataSet(
            "CorresponDaoImplTest_testFindCondition4_expected.xls").getTable("correspon").getRowCount(), count);
    }

    /**
     * testFindCondition4用の検索条件オブジェクト生成
     * 条件： Including Revision、User(To(Attention), To(Charge), Cc)、Group（To、CC）
     * @return 検索条件オブジェクト
     */
    private SearchCorresponCondition createFindCondition4() {
        SearchCorresponCondition condition = new SearchCorresponCondition();
        condition.setProjectId("0-5000-2");
        condition.setUserId("ZZA01");
        condition.setPageNo(1);
        condition.setPageRowNum(20);
        condition.setSort("id");
        condition.setAscending(true);

        condition.setSystemAdmin(true); // SystemAdmin
        condition.setCorresponNo("YOC:OT:P"); // 前方一致
        condition.setIncludingRevision(true);
        User user1 = new User();
        user1.setEmpNo("ZZA04");
        User user2 = new User();
        user2.setEmpNo("ZZA05");
        User[] addressUsers = {user1, user2};
//        condition.setAddressUsers(addressUsers);
        condition.setToUsers(addressUsers);
//        condition.setAddressCc(true);
//        condition.setAddressAttention(true);
//        condition.setAddressPersonInCharge(true);
        condition.setUserCc(true);
        condition.setUserAttention(true);
        condition.setUserPic(true);
        CorresponGroup group = new CorresponGroup();
        group.setId(5L);
        CorresponGroup[] corresponGroups = {group};
//        condition.setCorresponGroups(corresponGroups);
        condition.setToGroups(corresponGroups);
        condition.setGroupTo(true);
        condition.setGroupCc(true);
        return condition;
    }

    /**
     * {@link CorresponDaoImpl#find(jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponCondition)}
     * のテストケース.
     * 条件： User(To(Attention), To(Charge), Cc) + Unreplied
     * @throws Exception
     */
//  testFindOnBatchExecutionの一括実行対象
//  @Test
    public void testFindCondition5() throws Exception {
        SearchCorresponCondition condition = createFindCondition5();

        List<Correspon> list = dao.find(condition);

        // 取得したオブジェクトと、Excelファイルに定義済の期待値を比較する
        assertNotNull(list);
        assertEquals(3L, list.size());
        assertDataSetEquals(newDataSet("CorresponDaoImplTest_testFindCondition5_expected.xls"), list);
    }

    /**
     * {@link CorresponDaoImpl#count(jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponCondition)}
     * のテストケース.
     * 条件： User(To(Attention), To(Charge), Cc) + Unreplied
     * @throws Exception
     */
//  testFindOnBatchExecutionの一括実行対象
//  @Test
    public void testCountCondition5() throws Exception {
        SearchCorresponCondition condition = createFindCondition5();

        int count = dao.count(condition);

        // 取得した件数と、Excelファイルに定義済の期待値の件数を比較する
        assertEquals(newDataSet(
            "CorresponDaoImplTest_testFindCondition5_expected.xls").getTable("correspon").getRowCount(), count);
    }

    /**
     * testFindCondition5用の検索条件オブジェクト生成
     * 条件： User(To(Attention), To(Charge), Cc) + Unreplied
     * @return 検索条件オブジェクト
     */
    private SearchCorresponCondition createFindCondition5() {
        SearchCorresponCondition condition = new SearchCorresponCondition();
        condition.setProjectId("0-5000-2");
        condition.setUserId("ZZA01");
        condition.setPageNo(1);
        condition.setPageRowNum(20);
        condition.setSort("id");
        condition.setAscending(true);

        condition.setSystemAdmin(true); // SystemAdmin
        condition.setIncludingRevision(true);
        User user1 = new User();
        user1.setEmpNo("ZZA04");
        User user2 = new User();
        user2.setEmpNo("ZZA05");
        User[] addressUsers = {user1, user2};
//        condition.setAddressUsers(addressUsers);
        condition.setToUsers(addressUsers);
//        condition.setAddressCc(true);
//        condition.setAddressAttention(true);
//        condition.setAddressPersonInCharge(true);
//        condition.setAddressUnreplied(true);
        condition.setUserCc(true);
        condition.setUserAttention(true);
        condition.setUserPic(true);
        condition.setUserUnreplied(true);
        return condition;
    }

    /**
     * {@link CorresponDaoImpl#find(jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponCondition)}
     * のテストケース.
     * 条件：CustomField1
     * @throws Exception
     */
//  testFindOnBatchExecutionの一括実行対象
//  @Test
    public void testFindConditionCustomField1() throws Exception {
        SearchCorresponCondition condition = createFindConditionCustomField1();

        List<Correspon> list = dao.find(condition);

        // 取得したオブジェクトと、Excelファイルに定義済の期待値を比較する
        assertNotNull(list);
        assertDataSetEquals(newDataSet("CorresponDaoImplTest_testFindConditionCustomField_expected.xls"), list);
    }

    /**
     * {@link CorresponDaoImpl#count(jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponCondition)}
     * のテストケース.
     * 条件：CustomField1
     * @throws Exception
     */
//  testFindOnBatchExecutionの一括実行対象
//  @Test
    public void testCountCustomField() throws Exception {
        SearchCorresponCondition condition = createFindConditionCustomField1();

        int count = dao.count(condition);

        // 取得した件数と、Excelファイルに定義済の期待値の件数を比較する
        assertEquals(newDataSet(
            "CorresponDaoImplTest_testFindConditionCustomField_expected.xls").getTable("correspon").getRowCount(), count);
    }

    /**
     * testFindConditionCustomField1用の検索条件オブジェクト生成
     * 条件：CustomField1
     * @return 検索条件オブジェクト
     */
    private SearchCorresponCondition createFindConditionCustomField1() {
        SearchCorresponCondition condition = new SearchCorresponCondition();
        condition.setProjectId("0-5000-2");
        condition.setUserId("ZZA01");
        condition.setPageNo(1);
        condition.setPageRowNum(20);
        condition.setSort("id");
        condition.setAscending(true);

        condition.setSystemAdmin(true); // SystemAdmin
        condition.setCustomFieldNo(4L); // project_custom_field.id
        condition.setCustomFieldValue("value1");
        return condition;
    }

    /**
     * testFindConditionCustomField1用の検索条件オブジェクト生成
     * 条件：CustomField2
     * @return 検索条件オブジェクト
     */
//  testFindOnBatchExecutionの一括実行対象
//  @Test
    public void testFindConditionCustomField2() throws Exception {
        SearchCorresponCondition condition = new SearchCorresponCondition();
        condition.setProjectId("0-5000-2");
        condition.setUserId("ZZA01");
        condition.setPageNo(1);
        condition.setPageRowNum(20);
        condition.setSort("id");
        condition.setAscending(true);

        condition.setSystemAdmin(true); // SystemAdmin
        condition.setCustomFieldNo(5L);
        // customFieldValueの設定無し（nullで検索）

        List<Correspon> list = dao.find(condition);

        // 取得したオブジェクトと、Excelファイルに定義済の期待値を比較する
        assertNotNull(list);
        assertDataSetEquals(newDataSet("CorresponDaoImplTest_testFindConditionCustomFieldNull_expected.xls"), list);
    }

    /**
     * {@link CorresponDaoImpl#find(jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponCondition)}
     * のテストケース.
     * 条件：CustomField3
     * @throws Exception
     */
//  testFindOnBatchExecutionの一括実行対象
//  @Test
    public void testFindConditionCustomField3() throws Exception {
        SearchCorresponCondition condition = new SearchCorresponCondition();
        condition.setProjectId("0-5000-2");
        condition.setUserId("ZZA01");
        condition.setPageNo(1);
        condition.setPageRowNum(20);
        condition.setSort("id");
        condition.setAscending(true);

        condition.setSystemAdmin(true); // SystemAdmin
        condition.setCustomFieldNo(6L);
        condition.setCustomFieldValue("value3");


        List<Correspon> list = dao.find(condition);

        // 取得したオブジェクトと、Excelファイルに定義済の期待値を比較する
        assertNotNull(list);
        assertDataSetEquals(newDataSet("CorresponDaoImplTest_testFindConditionCustomField_expected.xls"), list);
    }

    /**
     * {@link CorresponDaoImpl#find(jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponCondition)}
     * のテストケース.
     * 条件：CustomField2
     * @throws Exception
     */
//  testFindOnBatchExecutionの一括実行対象
//  @Test
    public void testFindConditionCustomField4() throws Exception {
        SearchCorresponCondition condition = new SearchCorresponCondition();
        condition.setProjectId("0-5000-2");
        condition.setUserId("ZZA01");
        condition.setPageNo(1);
        condition.setPageRowNum(20);
        condition.setSort("id");
        condition.setAscending(true);

        condition.setSystemAdmin(true); // SystemAdmin
        condition.setCustomFieldNo(7L);
        // customFieldValueの設定無し（nullで検索）

        List<Correspon> list = dao.find(condition);

        // 取得したオブジェクトと、Excelファイルに定義済の期待値を比較する
        assertNotNull(list);
        assertDataSetEquals(newDataSet("CorresponDaoImplTest_testFindConditionCustomFieldNull_expected.xls"), list);
    }

    /**
     * {@link CorresponDaoImpl#find(jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponCondition)}
     * のテストケース.
     * 条件：CustomField1
     * @throws Exception
     */
//  testFindOnBatchExecutionの一括実行対象
//  @Test
    public void testFindConditionCustomField5() throws Exception {
        SearchCorresponCondition condition = new SearchCorresponCondition();
        condition.setProjectId("0-5000-2");
        condition.setUserId("ZZA01");
        condition.setPageNo(1);
        condition.setPageRowNum(20);
        condition.setSort("id");
        condition.setAscending(true);

        condition.setSystemAdmin(true); // SystemAdmin
        condition.setCustomFieldNo(8L);
        condition.setCustomFieldValue("value5");


        List<Correspon> list = dao.find(condition);

        // 取得したオブジェクトと、Excelファイルに定義済の期待値を比較する
        assertNotNull(list);
        assertDataSetEquals(newDataSet("CorresponDaoImplTest_testFindConditionCustomField_expected.xls"), list);
    }

    /**
     * {@link CorresponDaoImpl#find(jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponCondition)}
     * のテストケース.
     * 条件：CustomField2
     * @throws Exception
     */
//  testFindOnBatchExecutionの一括実行対象
//  @Test
    public void testFindConditionCustomField6() throws Exception {
        SearchCorresponCondition condition = new SearchCorresponCondition();
        condition.setProjectId("0-5000-2");
        condition.setUserId("ZZA01");
        condition.setPageNo(1);
        condition.setPageRowNum(20);
        condition.setSort("id");
        condition.setAscending(true);

        condition.setSystemAdmin(true); // SystemAdmin
        condition.setCustomFieldNo(9L);
        // customFieldValueの設定無し（nullで検索）

        List<Correspon> list = dao.find(condition);

        // 取得したオブジェクトと、Excelファイルに定義済の期待値を比較する
        assertNotNull(list);
        assertDataSetEquals(newDataSet("CorresponDaoImplTest_testFindConditionCustomFieldNull_expected.xls"), list);
    }

    /**
     * {@link CorresponDaoImpl#find(jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponCondition)}
     * のテストケース.
     * 条件：CustomField1
     * @throws Exception
     */
//  testFindOnBatchExecutionの一括実行対象
//  @Test
    public void testFindConditionCustomField7() throws Exception {
        SearchCorresponCondition condition = new SearchCorresponCondition();
        condition.setProjectId("0-5000-2");
        condition.setUserId("ZZA01");
        condition.setPageNo(1);
        condition.setPageRowNum(20);
        condition.setSort("id");
        condition.setAscending(true);

        condition.setSystemAdmin(true); // SystemAdmin
        condition.setCustomFieldNo(10L);
        condition.setCustomFieldValue("value7");


        List<Correspon> list = dao.find(condition);

        // 取得したオブジェクトと、Excelファイルに定義済の期待値を比較する
        assertNotNull(list);
        assertDataSetEquals(newDataSet("CorresponDaoImplTest_testFindConditionCustomField_expected.xls"), list);
    }

    /**
     * {@link CorresponDaoImpl#find(jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponCondition)}
     * のテストケース.
     * 条件：CustomField2
     * @throws Exception
     */
//  testFindOnBatchExecutionの一括実行対象
//  @Test
    public void testFindConditionCustomField8() throws Exception {
        SearchCorresponCondition condition = new SearchCorresponCondition();
        condition.setProjectId("0-5000-2");
        condition.setUserId("ZZA01");
        condition.setPageNo(1);
        condition.setPageRowNum(20);
        condition.setSort("id");
        condition.setAscending(true);

        condition.setSystemAdmin(true); // SystemAdmin
        condition.setCustomFieldNo(11L);
        // customFieldValueの設定無し（nullで検索）

        List<Correspon> list = dao.find(condition);

        // 取得したオブジェクトと、Excelファイルに定義済の期待値を比較する
        assertNotNull(list);
        assertDataSetEquals(newDataSet("CorresponDaoImplTest_testFindConditionCustomFieldNull_expected.xls"), list);
    }

    /**
     * {@link CorresponDaoImpl#find(jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponCondition)}
     * のテストケース.
     * 条件：CustomField1
     * @throws Exception
     */
//  testFindOnBatchExecutionの一括実行対象
//  @Test
    public void testFindConditionCustomField9() throws Exception {
        SearchCorresponCondition condition = new SearchCorresponCondition();
        condition.setProjectId("0-5000-2");
        condition.setUserId("ZZA01");
        condition.setPageNo(1);
        condition.setPageRowNum(20);
        condition.setSort("id");
        condition.setAscending(true);

        condition.setSystemAdmin(true); // SystemAdmin
        condition.setCustomFieldNo(12L);
        condition.setCustomFieldValue("value9");


        List<Correspon> list = dao.find(condition);

        // 取得したオブジェクトと、Excelファイルに定義済の期待値を比較する
        assertNotNull(list);
        assertDataSetEquals(newDataSet("CorresponDaoImplTest_testFindConditionCustomField_expected.xls"), list);
    }

    /**
     * {@link CorresponDaoImpl#find(jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponCondition)}
     * のテストケース.
     * 条件：CustomField2
     * @throws Exception
     */
//  testFindOnBatchExecutionの一括実行対象
//  @Test
    public void testFindConditionCustomField10() throws Exception {
        SearchCorresponCondition condition = new SearchCorresponCondition();
        condition.setProjectId("0-5000-2");
        condition.setUserId("ZZA01");
        condition.setPageNo(1);
        condition.setPageRowNum(20);
        condition.setSort("id");
        condition.setAscending(true);

        condition.setSystemAdmin(true); // SystemAdmin
        condition.setCustomFieldNo(13L);
        // customFieldValueの設定無し（nullで検索）

        List<Correspon> list = dao.find(condition);

        // 取得したオブジェクトと、Excelファイルに定義済の期待値を比較する
        assertNotNull(list);
        assertDataSetEquals(newDataSet("CorresponDaoImplTest_testFindConditionCustomFieldNull_expected.xls"), list);
    }

    /**
     * {@link CorresponDaoImpl#find(jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponCondition)}
     * のテストケース.
     * 無効となるパターン（１）
     * ・AddressFromを選択せずにAddressUserを指定
     * ・groupFromを選択せずにGroupを指定
     * ・CustomFieldを選択せずに値を入力
     * @throws Exception
     */
//  testFindOnBatchExecutionの一括実行対象
//  @Test
    public void testFindConditionIgnore1() throws Exception {
        SearchCorresponCondition condition = createFindConditionIgnore1();

        List<Correspon> list = dao.find(condition);

        // 取得したオブジェクトと、Excelファイルに定義済の期待値を比較する
        assertNotNull(list);
        assertDataSetEquals(newDataSet("CorresponDaoImplTest_testFindConditionIgnore_expected.xls"), list);
    }

    /**
     * {@link CorresponDaoImpl#count(jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponCondition)}
     * のテストケース.
     * 無効となるパターン（１）
     * ・AddressFromを選択せずにAddressUserを指定
     * ・groupFromを選択せずにGroupを指定
     * ・CustomFieldを選択せずに値を入力
     * @throws Exception
     */
//  testFindOnBatchExecutionの一括実行対象
//  @Test
    public void testCountConditionIgnore1() throws Exception {
        SearchCorresponCondition condition = createFindConditionIgnore1();

        int count = dao.count(condition);

        // 取得した件数と、Excelファイルに定義済の期待値の件数を比較する
        assertEquals(newDataSet(
            "CorresponDaoImplTest_testFindConditionIgnore_expected.xls").getTable("correspon").getRowCount(), count);
    }

    /**
     * testFindConditionIgnore1用の検索条件オブジェクト生成
     * 無効となるパターン（１）
     * ・AddressFromを選択せずにAddressUserを指定
     * ・groupFromを選択せずにGroupを指定
     * ・CustomFieldを選択せずに値を入力
     * @return 検索条件オブジェクト
     */
    private SearchCorresponCondition createFindConditionIgnore1() {
        SearchCorresponCondition condition = new SearchCorresponCondition();
        condition.setProjectId("0-5000-2");
        condition.setUserId("ZZA01");
        condition.setPageNo(1);
        condition.setPageRowNum(20);
        condition.setSort("id");
        condition.setAscending(true);

        condition.setSystemAdmin(true); // SystemAdmin
        User user = new User();
        user.setEmpNo("ZZA01");
        User[] addressUsers = {user};
//        condition.setAddressUsers(addressUsers);
        condition.setFromUsers(addressUsers);
        condition.setToUsers(addressUsers);
        condition.setUserPreparer(false);
//        condition.setAddressFrom(false);
//        condition.setAddressCc(false);
//        condition.setAddressAttention(false);
//        condition.setAddressPersonInCharge(false);
        condition.setUserCc(false);
        condition.setUserAttention(false);
        condition.setUserPic(false);
        CorresponGroup group = new CorresponGroup();
        group.setId(5L);
        CorresponGroup[] corresponGroups = {group};
//        condition.setCorresponGroups(corresponGroups);
//        condition.setGroupFrom(false);
        condition.setToGroups(corresponGroups);
        condition.setGroupTo(false);
        condition.setGroupCc(false);
        condition.setCustomFieldValue("value1");
        return condition;
    }

    /**
     * {@link CorresponDaoImpl#find(jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponCondition)}
     * のテストケース.
     * 無効となるパターン（２）
     * ・AddressUserを選択せずにAddressFromを指定
     * ・Groupを選択せずにgroupFromを指定
     * @throws Exception
     */
//  testFindOnBatchExecutionの一括実行対象
//  @Test
    public void testFindConditionIgnore2() throws Exception {
        SearchCorresponCondition condition = createFindConditionIgnore2();

        List<Correspon> list = dao.find(condition);

        // 取得したオブジェクトと、Excelファイルに定義済の期待値を比較する
        assertNotNull(list);
        assertDataSetEquals(newDataSet("CorresponDaoImplTest_testFindConditionIgnore_expected.xls"), list);
    }

    /**
     * {@link CorresponDaoImpl#count(jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponCondition)}
     * のテストケース.
     * 無効となるパターン（２）
     * ・AddressUserを選択せずにAddressFromを指定
     * ・Groupを選択せずにgroupFromを指定
     * @throws Exception
     */
//  testFindOnBatchExecutionの一括実行対象
//  @Test
    public void testCountConditionIgnore2() throws Exception {
        SearchCorresponCondition condition = createFindConditionIgnore1();

        int count = dao.count(condition);

        // 取得した件数と、Excelファイルに定義済の期待値の件数を比較する
        assertEquals(newDataSet(
            "CorresponDaoImplTest_testFindConditionIgnore_expected.xls").getTable("correspon").getRowCount(), count);
    }

    /**
     * testFindConditionIgnore2用の検索条件オブジェクト生成
     * 無効となるパターン（２）
     * ・AddressUserを選択せずにAddressFromを指定
     * ・Groupを選択せずにgroupFromを指定
     * @return 検索条件オブジェクト
     */
    private SearchCorresponCondition createFindConditionIgnore2() {
        SearchCorresponCondition condition = new SearchCorresponCondition();
        condition.setProjectId("0-5000-2");
        condition.setUserId("ZZA01");
        condition.setPageNo(1);
        condition.setPageRowNum(20);
        condition.setSort("id");
        condition.setAscending(true);

        condition.setSystemAdmin(true); // SystemAdmin
//        condition.setAddressFrom(true);
//        condition.setAddressCc(true);
//        condition.setAddressAttention(true);
//        condition.setAddressPersonInCharge(true);
        condition.setUserPreparer(true);
        condition.setUserCc(true);
        condition.setUserAttention(true);
        condition.setUserPic(true);
//        condition.setGroupFrom(true);
        condition.setGroupTo(true);
        condition.setGroupCc(true);
        return condition;
    }

    /**
     * {@link CorresponDaoImpl#find(jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponCondition)}
     * のテストケース.
     * ProjectAdminで検索（Draftの場合はPrepererが自身の文書しか見れない）
     * @throws Exception
     */
//  testFindOnBatchExecutionの一括実行対象
//  @Test
    public void testFindProjectAdmin() throws Exception {
        SearchCorresponCondition condition = createFindConditionProjectAdmin();

        List<Correspon> list = dao.find(condition);

        // 取得したオブジェクトと、Excelファイルに定義済の期待値を比較する
        assertNotNull(list);
        assertDataSetEquals(newDataSet("CorresponDaoImplTest_testFindProjectAdmin_expected.xls"), list);
    }

    /**
     * {@link CorresponDaoImpl#count(jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponCondition)}
     * のテストケース.
     * ProjectAdminで検索（Draftの場合はPrepererが自身の文書しか見れない）
     * @throws Exception
     */
//  testFindOnBatchExecutionの一括実行対象
//  @Test
    public void testCountProjectAdmin() throws Exception {
        SearchCorresponCondition condition = createFindConditionProjectAdmin();

        int count = dao.count(condition);

        // 取得した件数と、Excelファイルに定義済の期待値の件数を比較する
        assertEquals(newDataSet(
            "CorresponDaoImplTest_testFindProjectAdmin_expected.xls").getTable("correspon").getRowCount(), count);
    }

    /**
     * testFindProjectAdmin用の検索条件オブジェクト生成
     * ProjectAdminで検索
     * @return 検索条件オブジェクト
     */
    private SearchCorresponCondition createFindConditionProjectAdmin() {
        SearchCorresponCondition condition = new SearchCorresponCondition();
        condition.setProjectId("0-5000-2");
        condition.setUserId("ZZA07");
        condition.setPageNo(1);
        condition.setPageRowNum(20);
        condition.setSort("id");
        condition.setAscending(true);

        condition.setSystemAdmin(false);
        condition.setProjectAdmin(true); // ProjectAdmin
        return condition;
    }

    /**
     * {@link CorresponDaoImpl#find(jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponCondition)}
     * のテストケース.
     * Checkerで検索（Draft：×、Issued：○、それ以外は自身の承認状態がNone以外なら○）
     * @throws Exception
     */
//  testFindOnBatchExecutionの一括実行対象
//  @Test
    public void testFindChecker() throws Exception {
        SearchCorresponCondition condition = createFindConditionChecker();

        List<Correspon> list = dao.find(condition);

        // 取得したオブジェクトと、Excelファイルに定義済の期待値を比較する
        assertNotNull(list);
        assertDataSetEquals(newDataSet("CorresponDaoImplTest_testFindChecker_expected.xls"), list);
    }

    /**
     * {@link CorresponDaoImpl#count(jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponCondition)}
     * のテストケース.
     * Checkerで検索（Draft：×、Issued：○、それ以外は自身の承認状態がNone以外なら○）
     * @throws Exception
     */
//  testFindOnBatchExecutionの一括実行対象
//  @Test
    public void testCountChecker() throws Exception {
        SearchCorresponCondition condition = createFindConditionChecker();

        int count = dao.count(condition);

        // 取得した件数と、Excelファイルに定義済の期待値の件数を比較する
        assertEquals(newDataSet(
            "CorresponDaoImplTest_testFindChecker_expected.xls").getTable("correspon").getRowCount(), count);
    }

    /**
     * testFindChecker用の検索条件オブジェクト生成
     * Checkerで検索
     * @return 検索条件オブジェクト
     */
    private SearchCorresponCondition createFindConditionChecker() {
        SearchCorresponCondition condition = new SearchCorresponCondition();
        condition.setProjectId("0-5000-2");
        condition.setUserId("ZZA16"); // checker
        condition.setPageNo(1);
        condition.setPageRowNum(20);
        condition.setSort("id");
        condition.setAscending(true);
        return condition;
    }

    /**
     * {@link CorresponDaoImpl#find(jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponCondition)}
     * のテストケース.
     * Approverで検索（Draft：×、Issued：○、
     *                 それ以外は自身の承認状態がNone以外かApprover閲覧許可フラグが[Visible]なら○）
     * @throws Exception
     */
//  testFindOnBatchExecutionの一括実行対象
//  @Test
    public void testFindApprover() throws Exception {
        SearchCorresponCondition condition = createFindConditionApprover();

        List<Correspon> list = dao.find(condition);

        // 取得したオブジェクトと、Excelファイルに定義済の期待値を比較する
        assertNotNull(list);
        assertDataSetEquals(newDataSet("CorresponDaoImplTest_testFindApprover_expected.xls"), list);
    }

    /**
     * {@link CorresponDaoImpl#count(jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponCondition)}
     * のテストケース.
     * Approverで検索（Draft：×、Issued：○、
     *                 それ以外は自身の承認状態がNone以外かApprover閲覧許可フラグが[Visible]なら○）
     * @throws Exception
     */
//  testFindOnBatchExecutionの一括実行対象
//  @Test
    public void testCountApprover() throws Exception {
        SearchCorresponCondition condition = createFindConditionApprover();

        int count = dao.count(condition);

        // 取得した件数と、Excelファイルに定義済の期待値の件数を比較する
        assertEquals(newDataSet(
            "CorresponDaoImplTest_testFindApprover_expected.xls").getTable("correspon").getRowCount(), count);
    }

    /**
     * testFindApprover用の検索条件オブジェクト生成
     * Approverで検索
     * @return 検索条件オブジェクト
     */
    private SearchCorresponCondition createFindConditionApprover() {
        SearchCorresponCondition condition = new SearchCorresponCondition();
        condition.setProjectId("0-5000-2");
        condition.setUserId("ZZA17"); // approver
        condition.setPageNo(1);
        condition.setPageRowNum(20);
        condition.setSort("id");
        condition.setAscending(true);
        return condition;
    }

    /**
     * {@link CorresponDaoImpl#find(jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponCondition)}
     * のテストケース.
     * GroupAdminで検索（Draft：×、Issued：○、
     *                 それ以外はFrom、To(Group)、Cc(Group)に含まれる活動単位のいずれかについて、
     *                 Group Adminの権限を持つなら○）
     * @throws Exception
     */
//  testFindOnBatchExecutionの一括実行対象
//  @Test
    public void testFindGroupAdmin() throws Exception {
        SearchCorresponCondition condition = createFindConditionGroupAdmin();

        List<Correspon> list = dao.find(condition);

        // 取得したオブジェクトと、Excelファイルに定義済の期待値を比較する
        assertNotNull(list);
        assertDataSetEquals(newDataSet("CorresponDaoImplTest_testFindGroupAdmin_expected.xls"), list);
    }

    /**
     * {@link CorresponDaoImpl#count(jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponCondition)}
     * のテストケース.
     * GroupAdminで検索（Draft：×、Issued：○、
     *                 それ以外はFrom、To(Group)、Cc(Group)に含まれる活動単位のいずれかについて、
     *                 Group Adminの権限を持つなら○）
     * @throws Exception
     */
//  testFindOnBatchExecutionの一括実行対象
//  @Test
    public void testCountGroupAdmin() throws Exception {
        SearchCorresponCondition condition = createFindConditionGroupAdmin();

        int count = dao.count(condition);

        // 取得した件数と、Excelファイルに定義済の期待値の件数を比較する
        assertEquals(newDataSet(
            "CorresponDaoImplTest_testFindGroupAdmin_expected.xls").getTable("correspon").getRowCount(), count);
    }

    /**
     * testFindGroupAdmin用の検索条件オブジェクト生成
     * GroupAdminで検索
     * @return 検索条件オブジェクト
     */
    private SearchCorresponCondition createFindConditionGroupAdmin() {
        SearchCorresponCondition condition = new SearchCorresponCondition();
        condition.setProjectId("0-5000-2");
        condition.setUserId("ZZA18"); // GroupAdmin
        condition.setPageNo(1);
        condition.setPageRowNum(20);
        condition.setSort("id");
        condition.setAscending(true);
        return condition;
    }

    /**
     * {@link CorresponDaoImpl#find(jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponCondition)}
     * のテストケース.
     * NomalUserで検索（Issued：○、それ以外は×）
     * @throws Exception
     */
//  testFindOnBatchExecutionの一括実行対象
//  @Test
    public void testFindNormalUser() throws Exception {
        SearchCorresponCondition condition = createFindConditionNormalUser();

        List<Correspon> list = dao.find(condition);

        // 取得したオブジェクトと、Excelファイルに定義済の期待値を比較する
        assertNotNull(list);
        assertDataSetEquals(newDataSet("CorresponDaoImplTest_testFindNormalUser_expected.xls"), list);
    }

    /**
     * {@link CorresponDaoImpl#count(jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponCondition)}
     * のテストケース.
     * NomalUserで検索（Issued：○、それ以外は×）
     * @throws Exception
     */
//  testFindOnBatchExecutionの一括実行対象
//  @Test
    public void testCountNormalUser() throws Exception {
        SearchCorresponCondition condition = createFindConditionNormalUser();

        int count = dao.count(condition);

        // 取得した件数と、Excelファイルに定義済の期待値の件数を比較する
        assertEquals(newDataSet(
            "CorresponDaoImplTest_testFindNormalUser_expected.xls").getTable("correspon").getRowCount(), count);
    }

    /**
     * testFindNormalUser用の検索条件オブジェクト生成
     * NomalUserで検索
     * @return 検索条件オブジェクト
     */
    private SearchCorresponCondition createFindConditionNormalUser() {
        SearchCorresponCondition condition = new SearchCorresponCondition();
        condition.setProjectId("0-5000-2");
        condition.setUserId("ZZA19"); // NormalUser
        condition.setPageNo(1);
        condition.setPageRowNum(20);
        condition.setSort("id");
        condition.setAscending(true);
        return condition;
    }

    /**
    /**
     * {@link CorresponDaoImpl#count(jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponCondition)}
     * のテストケース.
     * @throws Exception
     */
//  testFindOnBatchExecutionの一括実行対象
//  @Test
    public void testCount() throws Exception {
        SearchCorresponCondition condition = new SearchCorresponCondition();
        condition.setProjectId("PJ1");
        condition.setUserId("ZZA01");
        condition.setPageNo(1);
        condition.setPageRowNum(10);
        condition.setSort("id");
        condition.setAscending(true);
        int count = dao.count(condition);

        assertEquals(23, count);
    }


    /**
     * {@link CorresponDaoImpl#findId(jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponCondition)}
     * のテストケース.
     * @throws Exception
     */
//  testFindOnBatchExecutionの一括実行対象
//  @Test
    public void testFindId() throws Exception {
        SearchCorresponCondition condition = new SearchCorresponCondition();
        condition.setProjectId("PJ1");
        condition.setUserId("ZZA01");
        condition.setSort("id");
        condition.setAscending(false);

        List<Long> list = dao.findId(condition);

        // 取得したオブジェクトと、Excelファイルに定義済の期待値を比較する
        assertNotNull(list);
        assertEquals(23, list.size());
    }

    /**
     * {@link CorresponDaoImpl#update(jp.co.opentone.bsol.linkbinder.dto.Correspon)}のテストケース.
     * @throws Exception
     */
    @Test
    public void testUpdate1() throws Exception {
        Correspon c = new Correspon();
        c.setId(4L);
        c.setVersionNo(0L);
        User user = new User();
        user.setEmpNo("ZZA04");
        user.setNameE("Tomoko Okada");
        c.setUpdatedBy(user);

        // 半分だけ更新（残りはtestUpdate2で行う）
        c.setCorresponNo("NEW:CORRESPON:001");
        c.setProjectId("PJ2");
        CorresponGroup group = new CorresponGroup();
        group.setId(3L);
        c.setFromCorresponGroup(group);
        CorresponType type = new CorresponType();
        type.setProjectCorresponTypeId(9L);
        c.setCorresponType(type);
        c.setSubject("NEW:Subject");

        Integer resultCount = dao.update(c);
        assertEquals(new Integer(1), resultCount);

        Correspon result = dao.findById(c.getId());

        assertEquals(c.getId(), result.getId());
        assertEquals(c.getCorresponNo(),result.getCorresponNo());
        assertEquals(c.getProjectId(), result.getProjectId());
        assertEquals(c.getFromCorresponGroup().getId(), result.getFromCorresponGroup().getId());
        assertEquals(c.getCorresponType().getProjectCorresponTypeId(), result.getCorresponType().getProjectCorresponTypeId());
        assertEquals(c.getSubject(), result.getSubject());
        assertEquals(Long.valueOf(1L), result.getVersionNo());
        assertEquals(Long.valueOf(0L), result.getDeleteNo());

//        assertDataSetEquals(newDataSet("CorresponDaoImplTest_testUpdate1_expected.xls"), result);

        // 更新日はExcelのデータから更新されている
        Date testDate = new GregorianCalendar(2009, 3, 2, 10, 40, 30).getTime();
        assertNotSame(testDate, result.getUpdatedAt());
    }

    /**
     * {@link CorresponDaoImpl#update(jp.co.opentone.bsol.linkbinder.dto.Correspon)}のテストケース.
     * @throws Exception
     */
    @Test
    public void testUpdate2() throws Exception {
        Correspon c = new Correspon();
        c.setId(4L);
        c.setVersionNo(0L);
        User user = new User();
        user.setEmpNo("ZZA04");
        user.setNameE("Tomoko Okada");

        // 半分だけ更新（残りはtestUpdate1で行う）
        c.setIssuedAt(new GregorianCalendar(2009, 3, 1, 1, 1, 1).getTime());
        c.setCorresponStatus(CorresponStatus.CLOSED);
        c.setDeadlineForReply(new GregorianCalendar(2009, 4, 1, 1, 1, 1).getTime());
        c.setRequestedApprovalAt(new GregorianCalendar(2009, 3, 3, 4, 4, 4).getTime());
        c.setWorkflowStatus(WorkflowStatus.DENIED);
        c.setBody("NEW:Body");
        c.setCreatedBy(user);
        c.setCreatedAt(new GregorianCalendar(2009, 3, 1, 1, 1, 1).getTime());
        c.setUpdatedBy(user);
        c.setUpdatedAt(new GregorianCalendar(2009, 3, 2, 2, 2, 2).getTime()); // 適用されない

        Integer resultCount = dao.update(c);
        assertEquals(new Integer(1), resultCount);

        Correspon result = dao.findById(c.getId());

        assertEquals(c.getId(), result.getId());
        assertEquals(c.getIssuedAt(),result.getIssuedAt());
        assertEquals(c.getCorresponStatus(), result.getCorresponStatus());
        assertEquals(c.getDeadlineForReply(), result.getDeadlineForReply());
        assertEquals(c.getRequestedApprovalAt(), result.getRequestedApprovalAt());
        assertEquals(c.getWorkflowStatus(), result.getWorkflowStatus());
        assertEquals(c.getBody(), result.getBody());
        assertEquals(c.getCreatedBy().getEmpNo(), result.getCreatedBy().getEmpNo());
        assertEquals(c.getCreatedAt(),result.getCreatedAt());
        assertEquals(Long.valueOf(1L), result.getVersionNo());
        assertEquals(Long.valueOf(0L), result.getDeleteNo());

        //        assertDataSetEquals(newDataSet("CorresponDaoImplTest_testUpdate2_expected.xls"), result);

        // 更新日はExcelのデータから更新されており、勝手に指定できない。
        Date testDate = new GregorianCalendar(2009, 3, 2, 10, 40, 30).getTime();
        System.out.println(result.getUpdatedAt());
        assertNotSame(testDate, result.getUpdatedAt());
        assertNotSame(c.getUpdatedAt(), result.getUpdatedAt());
    }

    /**
     * {@link CorresponDaoImpl#update(jp.co.opentone.bsol.linkbinder.dto.Correspon)}のテストケース.
     * コレポン文書更新が正常に行われることを検証する.
     * @throws Exception
     */
    @Test
    public void testUpdate3() throws Exception {
        Correspon c = new Correspon();
        c.setId(5L);
        c.setVersionNo(0L);
        User user = new User();
        user.setEmpNo("ZZA05");
        user.setNameE("Keiichi Ogiwara");

        CorresponGroup group = new CorresponGroup();
        group.setId(4L);
        c.setFromCorresponGroup(group);
        CorresponType type = new CorresponType();
        type.setProjectCorresponTypeId(9L);
        c.setCorresponType(type);

        c.setSubject("NEW:Subject");
        c.setBody("NEW:Body");

        c.setCorresponStatus(CorresponStatus.CLOSED);
        c.setDeadlineForReply(new GregorianCalendar(2009, 4, 1, 1, 1, 1).getTime());

        c.setUpdatedBy(user);
        c.setUpdatedAt(new GregorianCalendar(2009, 3, 2, 2, 2, 2).getTime()); // 適用されない

        Integer resultCount = dao.update(c);
        assertEquals(new Integer(1), resultCount);

        Correspon result = dao.findById(c.getId());

        assertEquals(c.getId(), result.getId());
        assertEquals(c.getFromCorresponGroup().getId(), result.getFromCorresponGroup().getId());
        assertEquals(c.getCorresponType().getProjectCorresponTypeId(), result.getCorresponType().getProjectCorresponTypeId());
        assertEquals(c.getSubject(), result.getSubject());
        assertEquals(c.getBody(), result.getBody());
        assertEquals(c.getCorresponStatus(), result.getCorresponStatus());
        assertEquals(c.getDeadlineForReply(), result.getDeadlineForReply());
        assertEquals(c.getUpdatedBy().getEmpNo(), result.getUpdatedBy().getEmpNo());
        assertEquals(Long.valueOf(1L), result.getVersionNo());
        assertEquals(Long.valueOf(0L), result.getDeleteNo());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());

        //assertDataSetEquals(newDataSet("CorresponDaoImplTest_testUpdate3_expected.xls"), result);

        // 更新日はExcelのデータから更新されており、勝手に指定できない。
        Date testDate = new GregorianCalendar(2009, 3, 2, 10, 40, 30).getTime();
        System.out.println(result.getUpdatedAt());
        assertNotSame(testDate, result.getUpdatedAt());
        assertNotSame(c.getUpdatedAt(), result.getUpdatedAt());
    }

    /**
     * {@link CorresponDaoImpl#update(jp.co.opentone.bsol.linkbinder.dto.Correspon)}のテストケース.
     * コレポン文書更新で、承認状態がDraftに更新することを検証する.
     * @throws Exception
     */
    @Test
    public void testUpdate4() throws Exception {
        Correspon c = new Correspon();
        c.setId(5L);
        c.setVersionNo(0L);
        User user = new User();
        user.setEmpNo("ZZA05");
        user.setNameE("Keiichi Ogiwara");

        CorresponGroup group = new CorresponGroup();
        group.setId(4L);
        c.setFromCorresponGroup(group);
        CorresponType type = new CorresponType();
        type.setProjectCorresponTypeId(9L);
        c.setCorresponType(type);

        c.setSubject("NEW:Subject");
        c.setBody("NEW:Body");

        c.setCorresponStatus(CorresponStatus.CLOSED);
        c.setDeadlineForReply(new GregorianCalendar(2009, 4, 1, 1, 1, 1).getTime());
        c.setWorkflowStatus(WorkflowStatus.DRAFT);

        c.setUpdatedBy(user);
        c.setUpdatedAt(new GregorianCalendar(2009, 3, 2, 2, 2, 2).getTime()); // 適用されない

        Integer resultCount = dao.update(c);
        assertEquals(new Integer(1), resultCount);

        Correspon result = dao.findById(c.getId());

        assertEquals(c.getId(), result.getId());
        assertEquals(c.getFromCorresponGroup().getId(), result.getFromCorresponGroup().getId());
        assertEquals(c.getCorresponType().getProjectCorresponTypeId(), result.getCorresponType().getProjectCorresponTypeId());
        assertEquals(c.getSubject(), result.getSubject());
        assertEquals(c.getBody(), result.getBody());
        assertEquals(c.getCorresponStatus(), result.getCorresponStatus());
        assertEquals(c.getDeadlineForReply(), result.getDeadlineForReply());
        assertEquals(c.getWorkflowStatus(), result.getWorkflowStatus());
        assertEquals(c.getUpdatedBy().getEmpNo(), result.getUpdatedBy().getEmpNo());
        assertEquals(Long.valueOf(1L), result.getVersionNo());
        assertEquals(Long.valueOf(0L), result.getDeleteNo());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());

        //        assertDataSetEquals(newDataSet("CorresponDaoImplTest_testUpdate4_expected.xls"), result);

        // 更新日はExcelのデータから更新されており、勝手に指定できない。
        Date testDate = new GregorianCalendar(2009, 3, 2, 10, 40, 30).getTime();
        System.out.println(result.getUpdatedAt());
        assertNotSame(testDate, result.getUpdatedAt());
        assertNotSame(c.getUpdatedAt(), result.getUpdatedAt());
    }

    /**
     * {@link CorresponDaoImpl#update(jp.co.opentone.bsol.linkbinder.dto.Correspon)}のテストケース.
     * NULL可のカラムにNULLをセットする.
     * @throws Exception
     */
    @Test
    public void testUpdateNull() throws Exception {
        Correspon c = new Correspon();
        c.setId(4L);
        c.setVersionNo(0L);
        User user = new User();
        user.setEmpNo("ZZA04");
        user.setNameE("Tomoko Okada");
        c.setUpdatedBy(user);

        // NULL可のカラム
        c.setCorresponNo(DBValue.STRING_NULL);
        c.setIssuedAt(DBValue.DATE_NULL);
        c.setDeadlineForReply(DBValue.DATE_NULL);
        c.setRequestedApprovalAt(DBValue.DATE_NULL);

        Integer resultCount = dao.update(c);
        assertEquals(new Integer(1), resultCount);

        Correspon result = dao.findById(c.getId());

        assertNull(result.getCorresponNo());
        assertNull(result.getIssuedAt());
        assertNull(result.getDeadlineForReply());
        assertNull(result.getRequestedApprovalAt());
    }

    /**
     * {@link CorresponDaoImpl#update(jp.co.opentone.bsol.linkbinder.dto.Correspon)}のテストケース.
     * NULL不可のカラムにNULLをセットした場合に、{@link InvalidNullUpdatedRuntimeException}
     * が発生することを検証する.
     * @throws Exception
     */
    @Test(expected = InvalidNullUpdatedRuntimeException.class)
    public void testUpdateNotNull() throws Exception {
        Correspon c = new Correspon();
        c.setId(4L);
        c.setVersionNo(0L);
        User user = new User();
        user.setEmpNo("ZZA04");
        user.setNameE("Tomoko Okada");
        c.setUpdatedBy(user);

        // NULL不可のカラム
        c.setProjectId(DBValue.STRING_NULL);

        dao.update(c);

        fail("例外が発生していない");
    }

    /**
     * {@link CorresponDaoImpl#update(jp.co.opentone.bsol.linkbinder.dto.Correspon)}のテストケース.
     * レコードが存在しない場合に、{@link StaleRecordException}が発生することを検証する.
     * @throws Exception
     */
    @Test(expected = StaleRecordException.class)
    public void testUpdateRecordNotFound() throws Exception {
        Correspon c = new Correspon();
        c.setId(-1L);
        c.setVersionNo(0L);
        User user = new User();
        user.setEmpNo("ZZA04");
        user.setNameE("Tomoko Okada");
        c.setUpdatedBy(user);

        dao.update(c);
        fail("例外が発生していない");
    }

    /**
     * {@link CorresponDaoImpl#update(jp.co.opentone.bsol.linkbinder.dto.Correspon)}のテストケース.
     * レコードが削除済の場合に、{@link StaleRecordException}が発生することを検証する.
     * @throws Exception
     */
    @Test(expected = StaleRecordException.class)
    public void testUpdateRecordDeleted() throws Exception {
        Correspon c = new Correspon();
        c.setId(3L); // 削除済レコードのID。 テストデータExcelを参照
        c.setVersionNo(0L);
        User user = new User();
        user.setEmpNo("ZZA04");
        user.setNameE("Tomoko Okada");
        c.setUpdatedBy(user);

        dao.update(c);

        fail("例外が発生していない");
    }

    /**
     * {@link CorresponDaoImpl#update(jp.co.opentone.bsol.linkbinder.dto.Correspon)}のテストケース.
     * 排他チェックにかかった場合、{@link StaleRecordException}が発生することを検証する.
     * @throws Exception
     */
    @Test(expected = StaleRecordException.class)
    public void testUpdateVersionNo() throws Exception {
        Correspon c = new Correspon();
        c.setId(4L);
        c.setVersionNo(99L); // 別Noを指定
        User user = new User();
        user.setEmpNo("ZZA04");
        user.setNameE("Tomoko Okada");
        c.setUpdatedBy(user);

        dao.update(c);

        fail("例外が発生していない");
    }

    /**
     * {@link CorresponDaoImpl#create(jp.co.opentone.bsol.linkbinder.dto.Correspon)}のテストケース.
     * コレポン文書新規登録の検証.
     * @throws Exception
     */
    @Test
    public void testCreate() throws Exception {
        // ログインユーザーの準備
        User login = new User();
        login.setEmpNo("ZZA01");

        // 登録内容の準備
        Correspon c = new Correspon();
        c.setProjectId("PROJECT001");

        CorresponGroup group = new CorresponGroup();
        group.setId(1L);
        c.setFromCorresponGroup(group);
        c.setPreviousRevCorresponId(1L);

        CorresponType type = new CorresponType();
        type.setId(5L);
        c.setCorresponType(type);

        c.setSubject("NEW:ENTRY:Subject");
        c.setBody("NEW:ENTRY:Body");
        c.getCorresponType().setProjectCorresponTypeId(15L);
        c.setCorresponStatus(CorresponStatus.CLOSED);
        c.setReplyRequired(ReplyRequired.YES);
        c.setDeadlineForReply(new GregorianCalendar(2009, 4, 1, 1, 1, 1).getTime());
        c.setWorkflowStatus(WorkflowStatus.DRAFT);
        c.setCreatedBy(login);
        c.setUpdatedBy(login);

        // 登録
        Long id = new Long(0L);

        // id（自動採番）を最大値にするためのループ
        while (true) {
            try {
                id = dao.create(c);
                break;
            } catch (KeyDuplicateException e) {
                continue;
            }
        }

        assertNotNull(id);

        Correspon actual = dao.findById(id);

        assertEquals(id, actual.getId());
        assertEquals(c.getProjectId(), actual.getProjectId());
        assertEquals(c.getFromCorresponGroup().getId(), actual.getFromCorresponGroup().getId());
        assertEquals(c.getPreviousRevCorresponId(), actual.getPreviousRevCorresponId());
        assertEquals(c.getCorresponType().getProjectCorresponTypeId(), actual.getCorresponType().getProjectCorresponTypeId());
        assertEquals(c.getSubject(), actual.getSubject());
        assertEquals(c.getBody(), actual.getBody());
        assertEquals(c.getCorresponStatus(), actual.getCorresponStatus());
        assertEquals(c.getReplyRequired(), actual.getReplyRequired());
        assertEquals(c.getDeadlineForReply(), actual.getDeadlineForReply());
        assertEquals(c.getWorkflowStatus(), actual.getWorkflowStatus());
        assertEquals(c.getCreatedBy().getEmpNo(), actual.getCreatedBy().getEmpNo());
        assertEquals(c.getUpdatedBy().getEmpNo(), actual.getUpdatedBy().getEmpNo());
        assertEquals(Long.valueOf(1L), actual.getVersionNo());
        assertEquals(Long.valueOf(0L), actual.getDeleteNo());
        assertNull(actual.getCorresponNo());
        assertNull(actual.getIssuedAt());
        assertNotNull(actual.getCreatedAt());
        assertNotNull(actual.getUpdatedAt());
    }

    /**
     * {@link CorresponDaoImpl#findTopParent(Long)}のテストケース.
     * @throws Exception
     */
    @Test
    public void testFindTopParent() throws Exception {
        Correspon c = dao.findTopParent(17L);

        // 取得したオブジェクトと、Excelファイルに定義済の期待値を比較する
        assertNotNull(c);
        assertDataSetEquals(newDataSet("CorresponDaoImplTest_testFindTopParent_expected.xls"), c);
    }

    /**
     * {@link CorresponDaoImpl#findTopParent(Long)}のテストケース. レコードが無い場合に、
     * {@link RecordNotFoundException}が発生することを検証する.
     * @throws Exception
     */
    @Test(expected = RecordNotFoundException.class)
    public void testFindTopParentRecordNotFound() throws Exception {
        dao.findTopParent(-1L);
        fail("例外が発生していない");
    }

    /**
     * {@link CorresponDaoImpl#findTopParent(Long)}のテストケース. レコードが削除済の場合に、
     * {@link RecordNotFoundException}が発生することを検証する.
     * @throws Exception
     */
    @Test(expected = RecordNotFoundException.class)
    public void testFindTopParentRecordDeleted() throws Exception {
        dao.findTopParent(3L); // 削除済レコードのID。 テストデータExcelを参照
        fail("例外が発生していない");
    }

    /**
     * {@link CorresponDaoImpl#findByIdForUpdate(Long)}のテストケース.
     * @throws Exception
     */
    @Test
    public void testFindByIdForUpdate() throws Exception {
        Correspon c = dao.findByIdForUpdate(19L);

        // 取得したオブジェクトと、Excelファイルに定義済の期待値を比較する
        assertEquals(1, c.getVersionNo().intValue());
    }

    /**
     * {@link CorresponDaoImpl#findByIdForUpdate(Long)}のテストケース. レコードが無い場合に、
     * {@link RecordNotFoundException}が発生することを検証する.
     * @throws Exception
     */
    @Test(expected = RecordNotFoundException.class)
    public void testFindByIdForUpdateRecordNotFound() throws Exception {
        dao.findTopParent(-2L);
        fail("例外が発生していない");
    }

    /**
     * {@link CorresponDaoImpl#update(jp.co.opentone.bsol.linkbinder.dto.Correspon)}のテストケース.
     * コレポン文書更新で、承認状態がDraftに更新することを検証する.
     * @throws Exception
     */
    @Test
    public void testDelete() throws Exception {
        Correspon c = new Correspon();
        c.setId(17L);
        c.setVersionNo(0L);
        User user = new User();
        user.setEmpNo("ZZA02");
        c.setUpdatedBy(user);
        c.setUpdatedAt(new GregorianCalendar(2009, 3, 2, 2, 2, 2).getTime()); // 適用されない

        // 削除前のデータを取得
        Correspon before = dao.findById(17L);

        Integer record = dao.delete(c);
        assertNotNull(record);
        assertEquals(Integer.valueOf(1), record);

        // 削除後のデータを取得する
        String sql = "select * from correspon where id = 17 and delete_no != 0";
        ITable afterActualTable = getConnection().createQueryTable("actual", sql);

        assertEquals(before.getId().toString(),
                     afterActualTable.getValue(0, "id").toString());
        assertEquals(before.getCorresponNo(),
                     afterActualTable.getValue(0, "correspon_no"));
        assertEquals(before.getProjectId(),
                     afterActualTable.getValue(0, "project_id"));
        assertEquals(before.getFromCorresponGroup().getId().toString(),
                     afterActualTable.getValue(0, "from_correspon_group_id").toString());
        assertEquals(before.getSubject(),
                     afterActualTable.getValue(0, "subject"));
        assertEquals(before.getIssuedAt(),
                     afterActualTable.getValue(0, "issued_at"));
        assertEquals(before.getCorresponStatus().getValue().toString(),
                     afterActualTable.getValue(0, "correspon_status").toString());
        assertEquals(before.getDeadlineForReply(),
                     afterActualTable.getValue(0, "deadline_for_reply"));
        assertEquals(before.getWorkflowStatus().getValue().toString(),
                     afterActualTable.getValue(0, "workflow_status").toString());
        assertEquals(before.getCreatedBy().getEmpNo(),
                     afterActualTable.getValue(0, "created_by"));
        assertEquals(before.getCreatedAt(),
                     afterActualTable.getValue(0, "created_at"));
        assertEquals(user.getEmpNo(),
                     afterActualTable.getValue(0, "updated_by"));
        assertNotNull(afterActualTable.getValue(0, "updated_at"));
        assertEquals(Long.valueOf(before.getVersionNo() + 1),
                     Long.valueOf(afterActualTable.getValue(0, "version_no").toString()));
        assertTrue(!afterActualTable.getValue(0, "delete_no").toString().equals("0"));

        // 更新日はExcelのデータから更新されており、勝手に指定できない。
        Date testDate = new GregorianCalendar(2009, 3, 2, 10, 40, 30).getTime();
        assertFalse(testDate.equals(afterActualTable.getValue(0, "updated_at")));
        assertFalse(c.getUpdatedAt().equals(afterActualTable.getValue(0, "updated_at")));
    }

    /**
     * {@link CorresponDaoImpl#update(jp.co.opentone.bsol.linkbinder.dto.Correspon)}のテストケース.
     * レコードが存在しない場合に、{@link StaleRecordException}が発生することを検証する.
     * @throws Exception
     */
    @Test(expected = StaleRecordException.class)
    public void testDeleteRecordNotFound() throws Exception {
        Correspon c = new Correspon();
        c.setId(-1L);
        c.setVersionNo(0L);
        User user = new User();
        user.setEmpNo("ZZA02");
        c.setUpdatedBy(user);

        dao.update(c);
        fail("例外が発生していない");
    }

    /**
     * {@link CorresponDaoImpl#update(jp.co.opentone.bsol.linkbinder.dto.Correspon)}のテストケース.
     * レコードが削除済の場合に、{@link StaleRecordException}が発生することを検証する.
     * @throws Exception
     */
    @Test(expected = StaleRecordException.class)
    public void testDeleteRecordDeleted() throws Exception {
        Correspon c = new Correspon();
        c.setId(3L); // 削除済レコードのID。 テストデータExcelを参照
        c.setVersionNo(0L);
        User user = new User();
        user.setEmpNo("ZZA02");
        c.setUpdatedBy(user);

        dao.update(c);
        fail("例外が発生していない");
    }

    /**
     * {@link CorresponDaoImpl#update(jp.co.opentone.bsol.linkbinder.dto.Correspon)}のテストケース.
     * 排他チェックにかかった場合、{@link StaleRecordException}が発生することを検証する.
     * @throws Exception
     */
    @Test(expected = StaleRecordException.class)
    public void testDeleteVersionNo() throws Exception {
        Correspon c = new Correspon();
        c.setId(4L);
        c.setVersionNo(99L); // 別Noを指定
        User user = new User();
        user.setEmpNo("ZZA02");
        c.setUpdatedBy(user);

        dao.update(c);

        fail("例外が発生していない");
    }

    /**
     * {@link CorresponDaoImpl#findCorresponGroupSummary(String)}のテストケース.
     * @throws Exception
     */
    @Test
    public void testFindCorresponGroupSummary() throws Exception {
        CorresponGroup group1 = new CorresponGroup();
        group1.setId(6L);
        CorresponGroup group2 = new CorresponGroup();
        group2.setId(7L);
        CorresponGroup[] groups = {group1, group2};
        List<CorresponGroupSummary> actual = dao.findCorresponGroupSummary("0-5000-2", groups);
        assertDataSetEquals(
            newDataSet("CorresponDaoImplTest_testFindCorresponGroupSummary_expected.xls"),
            actual);
    }

    /**
     * {@link CorresponDaoImpl#findCorresponGroupSummary(String)}のテストケース.
     * 活動単位リストが空.
     * @throws Exception
     */
    @Test
    public void testFindCorresponGroupSummaryNoGroups() throws Exception {
        List<CorresponGroupSummary> actual
            = dao.findCorresponGroupSummary("0-5000-2", new CorresponGroup[0]);
        assertDataSetEquals(
            newDataSet("CorresponDaoImplTest_testFindCorresponGroupSummaryNoGroups_expected.xls"),
            actual);
    }

    /**
     * {@link CorresponDaoImpl#findCorresponGroupSummary(String)}のテストケース.
     * 該当なし.
     * @throws Exception
     */
    @Test
    public void testFindCorresponGroupSummaryNoRecord() throws Exception {
        List<CorresponGroupSummary> actual
            = dao.findCorresponGroupSummary("XXXX", new CorresponGroup[0]);
        assertNotNull(actual);
    }

    /**
     * {@link CorresponDaoImpl#findCorresponUserSummary(String, String)}のテストケース.
     * SystemAdmin.
     * @throws Exception
     */
    @Test
    public void testFindCorresponUserSummarySystemAdmin() throws Exception {
        SearchCorresponUserSummaryCondition condition = new SearchCorresponUserSummaryCondition();
        condition.setProjectId("9-9999-2");
        condition.setUserId("ZZA01");
        condition.setSystemAdmin(true);
        condition.setProjectAdmin(false);
        condition.setUsePersonInCharge(true);
        CorresponUserSummary actual = dao.findCorresponUserSummary(condition);

        assertDataSetEquals(
            newDataSet("CorresponDaoImplTest_testFindCorresponUserSummarySystemAdmin_expected.xls"),
            actual);
    }

    /**
     * {@link CorresponDaoImpl#findCorresponUserSummary(String, String)}のテストケース.
     * SystemAdmin(Person in Charge not use).
     * @throws Exception
     */
    @Test
    public void testFindCorresponUserSummarySystemAdminNotUsePIC() throws Exception {
        SearchCorresponUserSummaryCondition condition = new SearchCorresponUserSummaryCondition();
        condition.setProjectId("9-9999-2");
        condition.setUserId("ZZA01");
        condition.setSystemAdmin(true);
        condition.setProjectAdmin(false);
        condition.setUsePersonInCharge(false);
        CorresponUserSummary actual = dao.findCorresponUserSummary(condition);

        assertDataSetEquals(
            newDataSet("CorresponDaoImplTest_testFindCorresponUserSummarySystemAdminNotUsePIC_expected.xls"),
            actual);
    }

    /**
     * {@link CorresponDaoImpl#findCorresponUserSummary(String, String)}のテストケース.
     * ProjectAdmin.
     * @throws Exception
     */
    @Test
    public void testFindCorresponUserSummaryProjectAdmin() throws Exception {
        SearchCorresponUserSummaryCondition condition = new SearchCorresponUserSummaryCondition();
        condition.setProjectId("9-9999-2");
        condition.setUserId("ZZA01");
        condition.setSystemAdmin(false);
        condition.setProjectAdmin(true);
        condition.setUsePersonInCharge(true);
        CorresponUserSummary actual = dao.findCorresponUserSummary(condition);

        assertDataSetEquals(
            newDataSet("CorresponDaoImplTest_testFindCorresponUserSummaryProjectAdmin_expected.xls"),
            actual);
    }

    /**
     * {@link CorresponDaoImpl#findCorresponUserSummary(String, String)}のテストケース.
     * ProjectAdmin(Person in Charge not use).
     * @throws Exception
     */
    @Test
    public void testFindCorresponUserSummaryProjectAdminNotUsePIC() throws Exception {
        SearchCorresponUserSummaryCondition condition = new SearchCorresponUserSummaryCondition();
        condition.setProjectId("9-9999-2");
        condition.setUserId("ZZA01");
        condition.setSystemAdmin(false);
        condition.setProjectAdmin(true);
        condition.setUsePersonInCharge(false);
        CorresponUserSummary actual = dao.findCorresponUserSummary(condition);

        assertDataSetEquals(
            newDataSet("CorresponDaoImplTest_testFindCorresponUserSummaryProjectAdminNotUsePIC_expected.xls"),
            actual);
    }

    /**
     * {@link CorresponDaoImpl#findCorresponUserSummary(String, String)}のテストケース.
     * その他のユーザー.
     * @throws Exception
     */
    @Test
    public void testFindCorresponUserSummaryOther() throws Exception {
        SearchCorresponUserSummaryCondition condition = new SearchCorresponUserSummaryCondition();
        condition.setProjectId("9-9999-2");
        condition.setUserId("ZZA01");
        condition.setSystemAdmin(false);
        condition.setProjectAdmin(false);
        condition.setUsePersonInCharge(true);
        CorresponUserSummary actual = dao.findCorresponUserSummary(condition);

        assertDataSetEquals(
            newDataSet("CorresponDaoImplTest_testFindCorresponUserSummaryOther_expected.xls"),
            actual);
    }

    /**
     * {@link CorresponDaoImpl#findCorresponUserSummary(String, String)}のテストケース.
     * その他のユーザー.
     * @throws Exception
     */
    @Test
    public void testFindCorresponUserSummaryOtherNotUsePIC() throws Exception {
        SearchCorresponUserSummaryCondition condition = new SearchCorresponUserSummaryCondition();
        condition.setProjectId("9-9999-2");
        condition.setUserId("ZZA01");
        condition.setSystemAdmin(false);
        condition.setProjectAdmin(false);
        condition.setUsePersonInCharge(false);
        CorresponUserSummary actual = dao.findCorresponUserSummary(condition);

        assertDataSetEquals(
            newDataSet("CorresponDaoImplTest_testFindCorresponUserSummaryOtherNotUsePIC_expected.xls"),
            actual);
    }

    /**
     * {@link CorresponDaoImpl#findCorresponUserSummary(String, String)}のテストケース.
     * 該当なし.
     * @throws Exception
     */
    @Test
    public void testFindCorresponUserSummaryNoRecord() throws Exception {
        SearchCorresponUserSummaryCondition condition = new SearchCorresponUserSummaryCondition();
        condition.setProjectId("XXXXXX");
        condition.setUserId("ZZA04");
        CorresponUserSummary actual = dao.findCorresponUserSummary(condition);
        assertNotNull(actual);
    }

    /**
     * {@link CorresponDaoImpl#countCorresponByCustomField}のテストケース
     * @throws Exception
     */
    @Test
    public void testCoundCorresponByCustomField() throws Exception {
        int actual = dao.countCorresponByCustomField(2L);

        assertTrue(actual == 1);
    }

    /**
     * {@link CorresponDaoImpl#findReplyCorresponByAddressUserId(Long)}のテストケース
     * @throws Exception
     */
    @Test
    public void testFindReplyCorresponByAddressUserId() throws Exception {
        List<Correspon> correspons = dao.findReplyCorresponByAddressUserId(1L);

        // 取得したオブジェクトと、Excelファイルに定義済の期待値を比較する
        assertNotNull(correspons);
        assertDataSetEquals(
            newDataSet("CorresponDaoImplTest_testFindReplyCorresponByAddressUserId_expected.xls"),
            correspons);
    }

    /**
     * {@link CorresponDaoImpl#findReplyCorresponByGroupId(Long)}のテストケース
     * @throws Exception
     */
    @Test
    public void testFindReplyCorresponByGroupId() throws Exception {
        List<Correspon> correspons = dao.findReplyCorresponByGroupId(90L, 5L);

        // 取得したオブジェクトと、Excelファイルに定義済の期待値を比較する
        assertNotNull(correspons);
        assertEquals(2, correspons.size());
        assertDataSetEquals(
            newDataSet("CorresponDaoImplTest_testFindReplyCorresponByGroupId_expected.xls"),
            correspons);
    }

    /**
     * {@link CorresponDaoImpl#countCorresponByCorresponType}のテストケース
     * @throws Exception
     */
    @Test
    public void testCountCorresponByCorresponType() throws Exception {
        int actual = dao.countCorresponByCorresponType(1L);
        assertTrue(actual == 46);
    }

    /**
     * {@link CorresponDaoImpl#findRootCorresponId(Long)}のテストケース
     * @throws Exception
     */
    @Test
    public void testFindRootCorresponId() throws Exception {
        Long rootCorresponId = dao.findRootCorresponId(88L);
        assertEquals(12L, rootCorresponId.longValue());
    }

    /**
     * {@link CorresponDaoImpl#findCorresponResponseHistory(Long)}のテストケース
     * @throws Exception
     */
    @Test
    public void testFindCorresponResponseHistory() throws Exception {
        List<CorresponResponseHistory> corresponResponseHistory = dao.findCorresponResponseHistory(12L, 12L);

        // 検証のため、各コレポン毎の宛先-グループを一つにまとめたリストを作る
        List<AddressCorresponGroup> acg = new ArrayList<AddressCorresponGroup>() ;
        for (CorresponResponseHistory crh : corresponResponseHistory) {
            List<AddressCorresponGroup> groups = crh.getCorrespon().getAddressCorresponGroups();
            for (AddressCorresponGroup group : groups) {
                acg.add(group);
            }
        }

        assertDataSetEquals(
            newDataSet("CorresponDaoImplTest_testFindCorresponResponseHistory_expected.xls"),
            corresponResponseHistory, acg);
    }

}
