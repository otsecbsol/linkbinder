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
package jp.co.opentone.bsol.linkbinder.service.admin.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.annotation.Rollback;

import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.test.AbstractTestCase;
import jp.co.opentone.bsol.linkbinder.dto.DistTemplateGroup;
import jp.co.opentone.bsol.linkbinder.dto.DistTemplateGroupCreate;
import jp.co.opentone.bsol.linkbinder.dto.DistTemplateHeader;
import jp.co.opentone.bsol.linkbinder.dto.DistTemplateHeaderCreate;
import jp.co.opentone.bsol.linkbinder.dto.DistTemplateHeaderDelete;
import jp.co.opentone.bsol.linkbinder.dto.DistTemplateHeaderUpdate;
import jp.co.opentone.bsol.linkbinder.dto.DistTemplateUser;
import jp.co.opentone.bsol.linkbinder.dto.DistTemplateUserCreate;
import jp.co.opentone.bsol.linkbinder.dto.UpdateMode;
import jp.co.opentone.bsol.linkbinder.dto.code.DistributionType;
import jp.co.opentone.bsol.linkbinder.service.admin.DistributionTemplateService;

/**
 * DistributionTemplateServiceクラスのテストクラス.
 * @author opentone
 */
public class DistributionTemplateServiceTest extends AbstractTestCase {

    /**
     * テスト対象.
     */
    @Resource
    private DistributionTemplateService service;

    @Before
    public void setUp() throws Exception {
        ((DistributionTemplateServiceImpl) service).setTestMode(true);
    }

    /**
     * findDistTemplateListメソッドのテスト.
     */
    @Test
    @Rollback(false)
    public void testFindDistTemplateList() {
        DistTemplateHeaderCreate header = createTestHeaderData();
        String createEmpNo = "ZZA01";
        try {
            List<DistTemplateHeader> list = service.findDistTemplateList(header.getProjectId());
            int size = list.size();

            // 1件追加する
            DistTemplateHeaderCreate result
                = service.save((DistTemplateHeader) header, createEmpNo);
            Assert.assertNotNull(result);
            list = service.findDistTemplateList(header.getProjectId());
            Assert.assertEquals(size + 1, list.size());


        } catch (Exception e) {
            Assert.fail(e.toString());
        }
    }

    /**
     * Saveメソッドのテスト.
     * 正常系：Group,User無しのヘッダーレコードを新規作成する場合
     */
    @Test
    @Rollback(true)
    public void testSave01() {
        DistTemplateHeaderCreate header = createTestHeaderData();
        try {
            String createEmpNo = "ZZA01";
            DistTemplateHeader result = service.save((DistTemplateHeader) header, createEmpNo);
            Assert.assertNotNull(result);
            Assert.assertNotNull(result.getId());
            Assert.assertTrue(0L < result.getId().longValue());
            Assert.assertNotNull(result.getCreatedAt());
            Assert.assertEquals(createEmpNo, result.getCreatedBy());
            Assert.assertNotNull(result.getTemplateCd());
            Assert.assertEquals(result.getCreatedAt(), result.getUpdatedAt());
            Assert.assertEquals(createEmpNo, result.getUpdatedBy());
            Assert.assertEquals(1L, result.getVersionNo().longValue());
            Assert.assertEquals(0L, result.getDeleteNo().longValue());
            // データを検索して取得する.
            DistTemplateHeader dth = service.find(result.getId());
            Assert.assertNotNull(dth);
            Assert.assertEquals(result.getProjectId(), dth.getProjectId());
            Assert.assertEquals(
                DistributionTemplateService.DIST_TEMP_HEADER_EMP_NO, dth.getEmpNo());
            Assert.assertEquals(result.getTemplateCd(), dth.getTemplateCd());
            Assert.assertEquals(result.getName(), dth.getName());
            Assert.assertEquals(result.getCreatedBy(), dth.getCreatedBy());
            Assert.assertEquals(result.getCreatedAt(), dth.getCreatedAt());
            Assert.assertEquals(result.getUpdatedBy(), dth.getUpdatedBy());
            Assert.assertEquals(result.getUpdatedAt(), dth.getUpdatedAt());
        } catch (Exception e) {
            Assert.fail(e.toString());
        }
    }

    /**
     * Saveメソッドのテスト.
     * 正常系：Group,User無しのヘッダーレコードを更新作成する場合
     */
    @Test
    @Rollback(true)
    public void testSave02() {
        DistTemplateHeaderCreate header = createTestHeaderData();
        try {
            String createEmpNo = "ZZA01";
            String updateEmpNo = "ZZA02";
            // 新規作成
            DistTemplateHeader createResult
                = service.save((DistTemplateHeader) header, createEmpNo);
            Assert.assertNotNull(createResult);

            // 更新データを作成
            DistTemplateHeaderUpdate update = (DistTemplateHeaderUpdate) createResult;
            update.setName("Updated name");
            update.setUpdatedBy("ZZA02");
            DistTemplateHeader updateResult = service.save(createResult, updateEmpNo);
            Assert.assertNotNull(updateResult);

            Assert.assertEquals(createEmpNo, updateResult.getCreatedBy());
            Assert.assertEquals(updateEmpNo, updateResult.getUpdatedBy());
            Assert.assertEquals(2L, updateResult.getVersionNo().longValue());
            Assert.assertEquals(0L, updateResult.getDeleteNo().longValue());

            // データを検索して取得する.
            DistTemplateHeader dth = service.find(updateResult.getId());
            Assert.assertNotNull(dth);
            Assert.assertEquals(update.getName(), dth.getName());
            Assert.assertEquals(update.getCreatedBy(), dth.getCreatedBy());
            Assert.assertTrue(0 < dth.getUpdatedAt().compareTo(dth.getCreatedAt()));
        } catch (Exception e) {
            Assert.fail(e.toString());
        }
    }

    /**
     * Saveメソッドのテスト.
     * 正常系：新規登録時(header, groupを登録)
     */
    @Test
    @Rollback(true)
    public void testSave11() {
        DistTemplateHeaderCreate header = createTestHeaderData();
        // 1件目のグループ
        // CHECKSTYLE:OFF
        long groupId = 100042L;
        // CHECKSTYLE:ON
        List<DistTemplateGroup> list = new ArrayList<DistTemplateGroup>();
        DistTemplateGroupCreate group1 = createTestToGroupData(++groupId);
        list.add((DistTemplateGroup) group1);
        DistTemplateGroupCreate group2 = createTestToGroupData(++groupId);
        list.add((DistTemplateGroup) group2);
        DistTemplateGroupCreate group3 = createTestCcGroupData(++groupId);
        list.add((DistTemplateGroup) group3);
        header.setDistTemplateGroups(list);

        String createEmpNo = "ZZA01";
        try {
            // 登録処理の実行
            DistTemplateHeader result = service.save((DistTemplateHeader) header, createEmpNo);
            Assert.assertNotNull(result);
            Assert.assertNotNull(result.getDistTemplateGroups());
            Assert.assertEquals(list.size(), result.getDistTemplateGroups().size());
            Assert.assertEquals(2, result.getToDistTemplateGroups().size());
            Assert.assertEquals(1, result.getCcDistTemplateGroups().size());

            // Toの2件目で検証
            DistTemplateGroup g = result.getToDistTemplateGroups().get(1);
            Assert.assertNotNull(g.getId());
            Assert.assertEquals(group2.getGroupId(), g.getGroupId());
            Assert.assertNotNull(g.getCreatedAt());
            Assert.assertEquals(g.getCreatedAt(), g.getUpdatedAt());
            Assert.assertEquals(createEmpNo, g.getCreatedBy());
            Assert.assertEquals(createEmpNo, g.getUpdatedBy());
            Assert.assertEquals(2L, g.getOrderNo().longValue());
            Assert.assertEquals(DistributionType.TO, g.getDistributionType());

            // Ccの1件目で検証
            g = result.getCcDistTemplateGroups().get(0);
            Assert.assertNotNull(g.getId());
            Assert.assertEquals(group3.getGroupId(), g.getGroupId());
            Assert.assertNotNull(g.getCreatedAt());
            Assert.assertEquals(g.getCreatedAt(), g.getUpdatedAt());
            Assert.assertEquals(createEmpNo, g.getCreatedBy());
            Assert.assertEquals(createEmpNo, g.getUpdatedBy());
            Assert.assertEquals(1L, g.getOrderNo().longValue());
            Assert.assertEquals(DistributionType.CC, g.getDistributionType());

            // データを検索して取得する.
            DistTemplateHeader dth = service.find(result.getId());
            Assert.assertNotNull(dth);
            Assert.assertNotNull(result.getDistTemplateGroups());
            Assert.assertEquals(list.size(), result.getDistTemplateGroups().size());
            Assert.assertEquals(2, result.getToDistTemplateGroups().size());
            Assert.assertEquals(1, result.getCcDistTemplateGroups().size());
            Assert.assertEquals(group1.getGroupId(),
                    dth.getToDistTemplateGroups().get(0).getGroupId());
            Assert.assertEquals(1L, dth.getToDistTemplateGroups().get(0).getOrderNo().longValue());
            Assert.assertEquals(group2.getGroupId(),
                    dth.getToDistTemplateGroups().get(1).getGroupId());
            Assert.assertEquals(2L, dth.getToDistTemplateGroups().get(1).getOrderNo().longValue());
            Assert.assertEquals(group3.getGroupId(),
                    dth.getCcDistTemplateGroups().get(0).getGroupId());
            Assert.assertEquals(1L, dth.getCcDistTemplateGroups().get(0).getOrderNo().longValue());
            for (DistTemplateGroup group : dth.getDistTemplateGroups()) {
                Assert.assertEquals(createEmpNo, group.getCreatedBy());
                Assert.assertEquals(createEmpNo, group.getUpdatedBy());
            }
        } catch (Exception e) {
            Assert.fail(e.toString());
        }
    }

    /**
     * Saveメソッドのテスト.
     * 正常系：更新登録時
     * 1. TOに2件, CCに1件登録されていた
     * 2. TOの2件目を削除して3件目を追加した
     * 3. CCの1件目の前に1件追加登録した
     */
    @Test
    @Rollback(true)
    public void testSave12() {
        DistTemplateHeaderCreate header = createTestHeaderData();
        // 1件目のグループ
        // CHECKSTYLE:OFF
        long groupId = 100042L;
        // CHECKSTYLE:ON
        List<DistTemplateGroup> list = new ArrayList<DistTemplateGroup>();
        DistTemplateGroupCreate group1 = createTestToGroupData(++groupId);
        list.add((DistTemplateGroup) group1);
        DistTemplateGroupCreate group2 = createTestToGroupData(++groupId);
        list.add((DistTemplateGroup) group2);
        DistTemplateGroupCreate group3 = createTestCcGroupData(++groupId);
        list.add((DistTemplateGroup) group3);
        header.setDistTemplateGroups(list);

        String createEmpNo = "ZZA01";
        String updateEmpNo = "ZZA02";
        DistTemplateHeader findHeader = null;
        try {
            // 登録処理の実行
            DistTemplateHeader createResult
                = service.save((DistTemplateHeader) header, createEmpNo);
            findHeader = service.find(createResult.getId());
            Assert.assertEquals(2, findHeader.getToDistTemplateGroups().size());
            Assert.assertEquals(1, findHeader.getCcDistTemplateGroups().size());
        } catch (Exception e) {
            Assert.fail(e.toString());
        }

        // 更新用のデータ準備
        List<DistTemplateGroup> allList = findHeader.getDistTemplateGroups();
        List<DistTemplateGroup> toList = findHeader.getToDistTemplateGroups();
        List<DistTemplateGroup> ccList = findHeader.getCcDistTemplateGroups();
        // 2件目削除
        toList.get(1).setMode(UpdateMode.DELETE);
        // 3件目追加
        final long addToGroupId = 100050L;
        DistTemplateGroupCreate addToGroup = createTestToGroupData(addToGroupId);
        allList.add((DistTemplateGroup) addToGroup);

        // CCの挿入
        final long adadCcGroupId = 100051L;
        DistTemplateGroupCreate addCcGroup = createTestCcGroupData(adadCcGroupId);
        long curCcGroupId = ccList.get(0).getId().longValue();
        int currentIndex = findHeader.getDistTemplateGroupIndex(curCcGroupId);
        allList.add(currentIndex, (DistTemplateGroup) addCcGroup);

        // 更新処理
        try {
            // 登録処理の実行
            DistTemplateHeader updateResult = service.save(findHeader, updateEmpNo);
            Assert.assertEquals(2, updateResult.getToDistTemplateGroups().size());
            Assert.assertEquals(2, updateResult.getCcDistTemplateGroups().size());

            // 並び順を確認する
            toList = updateResult.getToDistTemplateGroups();
            ccList = updateResult.getCcDistTemplateGroups();
            Assert.assertEquals(group1.getGroupId(), toList.get(0).getGroupId());
            Assert.assertEquals(addToGroup.getGroupId(), toList.get(1).getGroupId());
            Assert.assertEquals(addCcGroup.getGroupId(), ccList.get(0).getGroupId());
            Assert.assertEquals(group3.getGroupId(), ccList.get(1).getGroupId());

            // findして確認する.
            findHeader = service.find(updateResult.getId());
            toList = findHeader.getToDistTemplateGroups();
            ccList = findHeader.getCcDistTemplateGroups();
            Assert.assertEquals(group1.getGroupId(), toList.get(0).getGroupId());
            Assert.assertEquals(createEmpNo, toList.get(0).getCreatedBy());
            Assert.assertEquals(updateEmpNo, toList.get(0).getUpdatedBy());
            Assert.assertEquals(addToGroup.getGroupId(), toList.get(1).getGroupId());
            Assert.assertEquals(updateEmpNo, toList.get(1).getCreatedBy());
            Assert.assertEquals(updateEmpNo, toList.get(1).getUpdatedBy());
            Assert.assertEquals(addCcGroup.getGroupId(), ccList.get(0).getGroupId());
            Assert.assertEquals(updateEmpNo, ccList.get(0).getCreatedBy());
            Assert.assertEquals(updateEmpNo, ccList.get(0).getUpdatedBy());
            Assert.assertEquals(group3.getGroupId(), ccList.get(1).getGroupId());
            Assert.assertEquals(createEmpNo, ccList.get(1).getCreatedBy());
            Assert.assertEquals(updateEmpNo, ccList.get(1).getUpdatedBy());
        } catch (Exception e) {
            Assert.fail(e.toString());
        }
    }

    /**
     * Saveメソッドのテスト.
     * 正常系：更新登録時
     * 1. TOに2件, CCに3件登録されていた
     * 2. すべて削除した
     */
    @Test
    @Rollback(true)
    public void testSave13() {
        DistTemplateHeaderCreate header = createTestHeaderData();
        // 1件目のグループ
        // CHECKSTYLE:OFF
        long groupId = 100042L;
        // CHECKSTYLE:ON
        List<DistTemplateGroup> list = new ArrayList<DistTemplateGroup>();
        DistTemplateGroupCreate groupTo1 = createTestToGroupData(++groupId);
        list.add((DistTemplateGroup) groupTo1);
        DistTemplateGroupCreate groupTo2 = createTestToGroupData(++groupId);
        list.add((DistTemplateGroup) groupTo2);
        DistTemplateGroupCreate groupCc1 = createTestCcGroupData(++groupId);
        list.add((DistTemplateGroup) groupCc1);
        DistTemplateGroupCreate groupCc2 = createTestCcGroupData(++groupId);
        list.add((DistTemplateGroup) groupCc2);
        DistTemplateGroupCreate groupCc3 = createTestCcGroupData(++groupId);
        list.add((DistTemplateGroup) groupCc3);
        header.setDistTemplateGroups(list);

        String createEmpNo = "ZZA01";
        String updateEmpNo = "ZZA02";
        DistTemplateHeader findHeader = null;
        try {
            // 登録処理の実行
            DistTemplateHeader createResult
                = service.save((DistTemplateHeader) header, createEmpNo);
            findHeader = service.find(createResult.getId());
            Assert.assertEquals(2, findHeader.getToDistTemplateGroups().size());
            Assert.assertEquals(3, findHeader.getCcDistTemplateGroups().size());
        } catch (Exception e) {
            Assert.fail(e.toString());
        }

        // 全ての削除モードを設定
        for (DistTemplateGroup group : findHeader.getDistTemplateGroups()) {
            group.setMode(UpdateMode.DELETE);
        }
        // 更新
        try {
            // 更新処理の実行
            DistTemplateHeader updateResult = service.save(findHeader, updateEmpNo);
            // 戻り値からリストが削除されている
            Assert.assertEquals(0, updateResult.getToDistTemplateGroups().size());
            Assert.assertEquals(0, updateResult.getToDistTemplateGroups().size());
            Assert.assertEquals(0, updateResult.getCcDistTemplateGroups().size());

            // 検索処理
            findHeader = service.find(updateResult.getId());
            Assert.assertEquals(0, findHeader.getToDistTemplateGroups().size());
            Assert.assertEquals(0, findHeader.getToDistTemplateGroups().size());
            Assert.assertEquals(0, findHeader.getCcDistTemplateGroups().size());
        } catch (Exception e) {
            Assert.fail(e.toString());
        }
    }

    /**
     * Saveメソッドのテスト.
     * 異常系：同一グループがTOに重複して設定されている(新規登録時)
     */
    @Test
    @Rollback(true)
    public void testSaveNG11() {
        DistTemplateHeaderCreate header = createTestHeaderData();
        // 1件目のグループ
        // CHECKSTYLE:OFF
        long groupId = 100042L;
        // CHECKSTYLE:ON
        List<DistTemplateGroup> list = new ArrayList<DistTemplateGroup>();
        DistTemplateGroupCreate groupTo1 = createTestToGroupData(++groupId);
        list.add((DistTemplateGroup) groupTo1);
        DistTemplateGroupCreate groupTo2 = createTestToGroupData(groupId);
        list.add((DistTemplateGroup) groupTo2);
        DistTemplateGroupCreate groupCc1 = createTestCcGroupData(++groupId);
        list.add((DistTemplateGroup) groupCc1);
        DistTemplateGroupCreate groupCc2 = createTestCcGroupData(++groupId);
        list.add((DistTemplateGroup) groupCc2);
        header.setDistTemplateGroups(list);

        String createEmpNo = "ZZA01";
        try {
            // 登録処理の実行
            service.save((DistTemplateHeader) header, createEmpNo);
            Assert.fail("ServiceAbortException is not thrown.");
        } catch (ServiceAbortException e) {
            // この例外が発生するのが正しい振る舞い.
            // メッセージコードの比較
            Assert.assertEquals(
                DistributionTemplateService.MSG_GROUP_DUPLICATE, e.getMessageCode());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.toString());
        }
    }

    /**
     * Saveメソッドのテスト.
     * 異常系：同一グループがCCに重複して設定されている(更新登録時)
     */
    @Test
    @Rollback(true)
    public void testSaveNG12() {
        DistTemplateHeaderCreate header = createTestHeaderData();
        // 1件目のグループ
        // CHECKSTYLE:OFF
        long groupId = 100042L;
        // CHECKSTYLE:ON
        List<DistTemplateGroup> list = new ArrayList<DistTemplateGroup>();
        DistTemplateGroupCreate groupTo1 = createTestToGroupData(++groupId);
        list.add((DistTemplateGroup) groupTo1);
        DistTemplateGroupCreate groupTo2 = createTestToGroupData(++groupId);
        list.add((DistTemplateGroup) groupTo2);
        DistTemplateGroupCreate groupCc1 = createTestCcGroupData(++groupId);
        list.add((DistTemplateGroup) groupCc1);
        DistTemplateGroupCreate groupCc2 = createTestCcGroupData(++groupId);
        list.add((DistTemplateGroup) groupCc2);
        header.setDistTemplateGroups(list);

        String createEmpNo = "ZZA01";
        String updateEmpNo = "ZZA02";
        DistTemplateHeader findHeader = null;
        try {
            // 登録処理の実行
            DistTemplateHeader createResult
                = service.save((DistTemplateHeader) header, createEmpNo);
            findHeader = service.find(createResult.getId());
        } catch (Exception e) {
            Assert.fail(e.toString());
        }

        // Ccリストに重複したグループIDを追加して更新する
        DistTemplateGroupCreate groupCc3 = createTestCcGroupData(groupCc1.getGroupId());
        findHeader.getDistTemplateGroups().add((DistTemplateGroup) groupCc3);
        try {
            // 登録処理の実行
            service.save(findHeader, updateEmpNo);
            Assert.fail("ServiceAbortException is not thrown.");
        } catch (ServiceAbortException e) {
            // この例外が発生するのが正しい振る舞い.
            // メッセージコードの比較
            Assert.assertEquals(
                DistributionTemplateService.MSG_GROUP_DUPLICATE, e.getMessageCode());
        } catch (Exception e) {
            Assert.fail(e.toString());
        }
    }

    /**
     * Saveメソッドのテスト.
     * 正常系：新規登録時(header, group, userを登録)
     */
    @Test
    @Rollback(true)
    public void testSave21() {
        DistTemplateHeaderCreate header = createTestHeaderData();
        // 1件目のグループ
        // CHECKSTYLE:OFF
        long groupId = 100042L;
        // CHECKSTYLE:ON
        List<DistTemplateGroup> list = new ArrayList<DistTemplateGroup>();
        DistTemplateGroupCreate group1 = createTestToGroupData(groupId, true);
        list.add((DistTemplateGroup) group1);
        header.setDistTemplateGroups(list);

        String createEmpNo = "ZZA01";
        try {
            // 登録処理の実行
            DistTemplateHeader result = service.save((DistTemplateHeader) header, createEmpNo);
            Assert.assertNotNull(result);
            Assert.assertNotNull(result.getDistTemplateGroups());
            Assert.assertEquals(list.size(), result.getDistTemplateGroups().size());

            // ユーザーレコードの検証
            String[] empNoList = {
                "ZZA11", "ZZA12", "ZZA13", "ZZA14", "ZZA15"
            };
            DistTemplateGroup group = result.getDistTemplateGroups().get(0);
            for (int i = 0; i < group.getUsers().size(); i++) {
                DistTemplateUser user = group.getUsers().get(i);
                Assert.assertNotNull(user.getId());
                Assert.assertEquals(group.getId(), user.getDistTemplateGroupId());
                Assert.assertEquals(i + 1, user.getOrderNo().longValue());
                Assert.assertEquals(empNoList[i], user.getEmpNo());
                Assert.assertEquals(createEmpNo, user.getCreatedBy());
                Assert.assertNotNull(user.getCreatedAt());
                Assert.assertEquals(createEmpNo, user.getUpdatedBy());
                Assert.assertNotNull(user.getUpdatedAt());
            }

            // 検索結果との突き合わせ
            DistTemplateHeader findHeader = service.find(result.getId());
            Assert.assertNotNull(findHeader.getDistTemplateGroups());
            Assert.assertEquals(1, findHeader.getDistTemplateGroups().size());
            DistTemplateGroup findGroup = findHeader.getDistTemplateGroups().get(0);
            Assert.assertNotNull(findGroup.getUsers());
            Assert.assertEquals(group1.getUsers().size(), findGroup.getUsers().size());
            for (int i = 0; i < findGroup.getUsers().size(); i++) {
                DistTemplateUser user = group.getUsers().get(i);
                Assert.assertNotNull(user.getId());
                Assert.assertEquals(group.getId(), user.getDistTemplateGroupId());
                Assert.assertEquals(i + 1, user.getOrderNo().longValue());
                Assert.assertEquals(empNoList[i], user.getEmpNo());
                Assert.assertEquals(createEmpNo, user.getCreatedBy());
                Assert.assertNotNull(user.getCreatedAt());
                Assert.assertEquals(createEmpNo, user.getUpdatedBy());
                Assert.assertNotNull(user.getUpdatedAt());
            }
        } catch (Exception e) {
            Assert.fail(e.toString());
        }
    }

    /**
     * Saveメソッドのテスト.
     * 正常系：更新登録時(header, group, userを登録)
     */
    @Test
    @Rollback(true)
    public void testSave22() {
        DistTemplateHeaderCreate header = createTestHeaderData();
        // 1件目のグループ
        // CHECKSTYLE:OFF
        long groupId = 100042L;
        // CHECKSTYLE:ON
        List<DistTemplateGroup> list = new ArrayList<DistTemplateGroup>();
        DistTemplateGroupCreate group1 = createTestToGroupData(groupId, true);
        list.add((DistTemplateGroup) group1);
        header.setDistTemplateGroups(list);

        String createEmpNo = "ZZA01";
        String updateEmpNo = "ZZA02";
        try {
            // 登録処理の実行
            DistTemplateHeader result = service.save((DistTemplateHeader) header, createEmpNo);
            Assert.assertNotNull(result);
            Assert.assertNotNull(result.getDistTemplateGroups());
            Assert.assertEquals(list.size(), result.getDistTemplateGroups().size());

            // ユーザー情報を変更する.
            String[] updateEmpNoList = {
                "ZZA41", "ZZA42", "ZZA43"
            };
            List<DistTemplateUser> updateUserList = new ArrayList<DistTemplateUser>();
            for (int i = 0; i < updateEmpNoList.length; i++) {
                DistTemplateUser user
                    = result.getDistTemplateGroups().get(0).getUsers().get(i);
                user.setEmpNo(updateEmpNoList[i]);
                updateUserList.add(user);
            }
            result.getDistTemplateGroups().get(0).setUsers(updateUserList);
            result.getDistTemplateGroups().get(0).setMode(UpdateMode.UPDATE);
            // 更新処理の実行
            DistTemplateHeader updateResult = service.save(result, updateEmpNo);
            Assert.assertNotNull(updateResult);
            Assert.assertNotNull(updateResult.getDistTemplateGroups());
            List<DistTemplateUser> userList
                = updateResult.getDistTemplateGroups().get(0).getUsers();
            Assert.assertNotNull(userList);
            Assert.assertEquals(updateEmpNoList.length, userList.size());
            for (int i = 0; i < updateEmpNoList.length; i++) {
                Assert.assertEquals(updateEmpNoList[i], userList.get(i).getEmpNo());
            }

            DistTemplateHeader findHeader = service.find(updateResult.getId());
            Assert.assertNotNull(findHeader.getDistTemplateGroups());
            Assert.assertEquals(1, findHeader.getDistTemplateGroups().size());
            DistTemplateGroup findGroup = findHeader.getDistTemplateGroups().get(0);
            Assert.assertNotNull(findGroup.getUsers());
            Assert.assertEquals(updateEmpNoList.length, findGroup.getUsers().size());
            for (int i = 0; i < updateEmpNoList.length; i++) {
                Assert.assertEquals(updateEmpNoList[i], findGroup.getUsers().get(i).getEmpNo());
            }
        } catch (Exception e) {
            Assert.fail(e.toString());
        }
    }

    /**
     * Saveメソッドのテスト.
     * 異常系：同一グループに同一ユーザーが重複して設定されている(新規登録時)
     */
    @Test
    @Rollback(true)
    public void testSaveNG21() {
        DistTemplateHeaderCreate header = createTestHeaderData();
        // 1件目のグループ
        // CHECKSTYLE:OFF
        long groupId = 100042L;
        // CHECKSTYLE:ON
        List<DistTemplateGroup> list = new ArrayList<DistTemplateGroup>();
        DistTemplateGroupCreate group1 = createTestToGroupData(groupId, true);
        String empNo1 = group1.getUsers().get(0).getEmpNo();
        group1.getUsers().get(1).setEmpNo(empNo1);
        list.add((DistTemplateGroup) group1);
        header.setDistTemplateGroups(list);

        String createEmpNo = "ZZA01";
        try {
            // 登録処理の実行
            service.save((DistTemplateHeader) header, createEmpNo);
            Assert.fail("ServiceAbortException is not thrown.");
        } catch (ServiceAbortException e) {
            // この例外が発生するのが正しい振る舞い.
            // メッセージコードの比較
            Assert.assertEquals(
                DistributionTemplateService.MSG_USER_DUPLICATE, e.getMessageCode());
        } catch (Exception e) {
            Assert.fail(e.toString());
        }
    }

    /**
     * Deleteメソッドのテスト.
     * 正常系：通常の削除
     */
    @Test
    @Rollback(true)
    public void testDelete01() {
        DistTemplateHeaderCreate header = createTestHeaderData();
        String updateEmpNo = "ZZA01";
        try {
            // 1件追加する
            DistTemplateHeader result = service.save((DistTemplateHeader) header, updateEmpNo);
            Assert.assertNotNull(result);

            // 削除する
            DistTemplateHeaderDelete deleteHeader
                = DistTemplateHeader.newDistTemplateHeaderDelete();
            deleteHeader.setId(result.getId());
            deleteHeader.setProjectId(result.getProjectId());
            deleteHeader.setVersionNo(result.getVersionNo());

            service.delete(deleteHeader, updateEmpNo);

        } catch (Exception e) {
            Assert.fail(e.toString());
        }

    }

    /**
     * Deleteメソッドのテスト.
     * 異常系：versionNo, id, projectIdがマッチしない場合
     */
    @Test
    @Rollback(true)
    public void testDeleteNG01() {
        DistTemplateHeaderCreate header = createTestHeaderData();
        String updateEmpNo = "ZZA01";
        DistTemplateHeader result = null;
        try {
            // 1件追加する
            result = service.save((DistTemplateHeader) header, updateEmpNo);
            Assert.assertNotNull(result);
        } catch (Exception e) {
            Assert.fail(e.toString());
        }
        try {
            // 削除する
            DistTemplateHeaderDelete deleteHeader
                = DistTemplateHeader.newDistTemplateHeaderDelete();
            deleteHeader.setId(result.getId());
            deleteHeader.setProjectId("9-9999-9");
            deleteHeader.setVersionNo(result.getVersionNo());

            service.delete(deleteHeader, updateEmpNo);
            Assert.fail("Exception is not thrown.");
        } catch (ServiceAbortException e) {
            Assert.assertEquals(
                DistributionTemplateService.MSG_FAIL_DELETE, e.getMessageCode());
        } catch (Exception e) {
            Assert.fail(e.toString());
        }

    }

    /**
     * DistTemplateHeaderテストデータを作成する.
     * @return
     */
    private DistTemplateHeaderCreate createTestHeaderData() {
        DistTemplateHeaderCreate header = DistTemplateHeader.newDistTemplateHeaderCreate();
        header.setProjectId("1-2345-6");
        header.setCreatedBy("ZZA01");
        header.setUpdatedBy("ZZA01");
        header.setName("templateXXX");
        return header;
    }

    /**
     * ユーザー情報を含まないDistTemplateGroup(TO)テストデータを作成する.
     * @return
     */
    private DistTemplateGroupCreate createTestToGroupData(long groupId) {
        return createTestToGroupData(groupId, false);
    }

    /**
     * DistTemplateGroup(TO)テストデータを作成する.
     * @return
     */
    private DistTemplateGroupCreate createTestToGroupData(long groupId, boolean hasUser) {
        DistTemplateGroupCreate group = createTestGroupData(groupId, hasUser);
        group.setDistributionType(DistributionType.TO);
        return group;
    }

    /**
     * ユーザー情報を含まないDistTemplateGroup(CC)テストデータを作成する.
     * @return
     */
    private DistTemplateGroupCreate createTestCcGroupData(long groupId) {
        return createTestCcGroupData(groupId, false);
    }

    /**
     * DistTemplateGroup(CC)テストデータを作成する.
     * @return
     */
    private DistTemplateGroupCreate createTestCcGroupData(long groupId, boolean hasUser) {
        DistTemplateGroupCreate group = createTestGroupData(groupId, hasUser);
        group.setDistributionType(DistributionType.CC);
        return group;
    }

    /**
     * DistTemplateGroupテストデータを作成する.
     * @return
     */
    private DistTemplateGroupCreate createTestGroupData(long groupId, boolean hasUser) {
        DistTemplateGroupCreate group = DistTemplateGroup.newDistTemplateGroupCreate();
        group.setGroupId(groupId);
        group.setCreatedBy("ZZA01");
        group.setUpdatedBy("ZZA01");
        if (hasUser) {
            // ユーザーレコードを追加
            group = createTestUserData(group);
        }
        return group;
    }

    /**
     * DistTemplateUserテストデータを作成する.
     * @return
     */
    private DistTemplateGroupCreate createTestUserData(DistTemplateGroupCreate group) {
        String[] empNoList = {
            "ZZA11", "ZZA12", "ZZA13", "ZZA14", "ZZA15"
        };
        List<DistTemplateUser> userList = new ArrayList<DistTemplateUser>();
        for (String empNo : empNoList) {
            DistTemplateUserCreate user = DistTemplateUser.newDistTemplateUserCreate();
            user.setEmpNo(empNo);
            userList.add((DistTemplateUser) user);
        }
        group.setUsers(userList);
        return group;
    }
}
