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
package jp.co.opentone.bsol.linkbinder.service.correspon.impl;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import jp.co.opentone.bsol.framework.core.dao.Entity;
import jp.co.opentone.bsol.framework.core.dao.RecordNotFoundException;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.test.AbstractTestCase;
import jp.co.opentone.bsol.linkbinder.dao.impl.AddressUserDaoImpl;
import jp.co.opentone.bsol.linkbinder.dao.impl.CorresponDaoImpl;
import jp.co.opentone.bsol.linkbinder.dao.impl.CorresponGroupDaoImpl;
import jp.co.opentone.bsol.linkbinder.dao.impl.CorresponTypeDaoImpl;
import jp.co.opentone.bsol.linkbinder.dao.impl.CustomFieldDaoImpl;
import jp.co.opentone.bsol.linkbinder.dao.impl.ProjectDaoImpl;
import jp.co.opentone.bsol.linkbinder.dao.impl.UserDaoImpl;
import jp.co.opentone.bsol.linkbinder.dto.AddressCorresponGroup;
import jp.co.opentone.bsol.linkbinder.dto.AddressUser;
import jp.co.opentone.bsol.linkbinder.dto.Attachment;
import jp.co.opentone.bsol.linkbinder.dto.Correspon;
import jp.co.opentone.bsol.linkbinder.dto.CorresponCustomField;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroup;
import jp.co.opentone.bsol.linkbinder.dto.CorresponType;
import jp.co.opentone.bsol.linkbinder.dto.CustomField;
import jp.co.opentone.bsol.linkbinder.dto.Project;
import jp.co.opentone.bsol.linkbinder.dto.ProjectCustomField;
import jp.co.opentone.bsol.linkbinder.dto.ProjectUser;
import jp.co.opentone.bsol.linkbinder.dto.UpdateMode;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.Workflow;
import jp.co.opentone.bsol.linkbinder.dto.code.AddressType;
import jp.co.opentone.bsol.linkbinder.dto.code.AddressUserType;
import jp.co.opentone.bsol.linkbinder.dto.code.CorresponStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowProcessStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowType;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCustomFieldCondition;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchUserCondition;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.service.AbstractService;
import jp.co.opentone.bsol.linkbinder.service.correspon.CorresponValidateService;
import mockit.Mock;
import mockit.MockUp;

/**
 * {@link CorresponValidateServiceImpl}のテストケース.
 * @author opentone
 */
public class CorresponValidateServiceImplTest extends AbstractTestCase {

    /**
     *  testValidateNewXXのテストを実行するか否か.
     */
    private boolean executeNewTest = true;

    /**
     *  testValidateUpdateXXのテストを実行するか否か.
     */
    private boolean executeUpdateTest = true;

    /**
     * テスト対象.
     */
    @Resource
    private CorresponValidateService service;

    @BeforeClass
    public static void testSetup() {
        new MockAbstractService();
    }

    @AfterClass
    public static void testTeardown() {
        new MockAbstractService().tearDown();
    }

    /**
     * テスト前準備.
     */
    @Before
    public void setUp() {
        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        MockAbstractService.IS_GROUP_ADMIN = false;
        MockAbstractService.IS_PREPARER = false;
        MockAbstractService.IS_CHEKER = false;
        MockAbstractService.IS_APPROVER = false;
        MockProjectCustomFieldDao.RET = new HashMap<Long, Long>();
        MockCustomFieldDao.RET_FIND_BY_PROJECT_ID = new ArrayList<CustomField>();
    }

    /**
     * 後始末.
     */
    @After
    public void tearDown() {
    }

    /**
     * 新規 項番1.
     */
    @Test
    public void testValidateNew01() throws Exception {
        if (executeNewTest){
            Correspon correspon = createCorresponNew01();
            try {
                // テスト実行
                service.validate(correspon);
            } catch (ServiceAbortException e) {
                e.printStackTrace();
                fail("例外が発生." + e);
            }
        }
    }

    /**
     * 新規 項番2.
     */
    @Test
    public void testValidateNew02() throws Exception {
        if (executeNewTest){
            Correspon correspon = createCorresponNew02();
            try {
                // テスト実行
                service.validate(correspon);
            } catch (ServiceAbortException e) {
                e.printStackTrace();
                fail("例外が発生." + e);
            }
        }
    }

    /**
     * 新規 項番3.
     */
    @Test
    public void testValidateNew03() throws Exception {
        if (executeNewTest){
            Correspon correspon = createCorresponNew03();
            try {
                // テスト実行
                service.validate(correspon);
                fail("例外が発生しませんでした");
            } catch (ServiceAbortException e) {
                assertEquals(
                    ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_PROJECT_DIFF,
                    e.getMessageCode());
            }
        }
    }

    /**
     * 新規 項番4.
     */
    @Test
    public void testValidateNew04() throws Exception {
        if (executeNewTest){
            Correspon correspon = createCorresponNew04();
            try {
                // テスト実行
                service.validate(correspon);
                fail("例外が発生しませんでした");
            } catch (ServiceAbortException e) {
                assertEquals(
                    ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CORREPON_CANCELED,
                    e.getMessageCode());
            }
        }
    }

    /**
     * 新規 項番5.
     */
    @Test
    public void testValidateNew05() throws Exception {
        if (executeNewTest){
            Correspon correspon = createCorresponNew05();
            try {
                // テスト実行
                service.validate(correspon);
                fail("例外が発生しませんでした");
            } catch (ServiceAbortException e) {
                assertEquals(
                    ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_GROUP_NOT_EXIST,
                    e.getMessageCode());
                Object[] obj = e.getMessageVars();
                assertEquals("CorresponGroup.setName()", (String) obj[0]);

            }
        }
    }

    /**
     * 新規 項番6.
     */
    @Test
    public void testValidateNew06() throws Exception {
        if (executeNewTest){
            Correspon correspon = createCorresponNew06();
            try {
                // テスト実行
                service.validate(correspon);
                fail("例外が発生しませんでした");
            } catch (ServiceAbortException e) {
                assertEquals(
                    ApplicationMessageCode.INVALID_GROUP,
                    e.getMessageCode());
                Object[] obj = e.getMessageVars();
                assertEquals("CorresponGroup.setName()", (String) obj[0]);
            }
        }
    }

    /**
     * 新規 項番7.
     */
    @Test
    public void testValidateNew07() throws Exception {
        if (executeNewTest){
            Correspon correspon = createCorresponNew07();
            try {
                // テスト実行
                service.validate(correspon);
                fail("例外が発生しませんでした");
            } catch (ServiceAbortException e) {
                assertEquals(
                    ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ATTENTION_NOT_EXIST,
                    e.getMessageCode());
                Object[] obj = e.getMessageVars();
                assertEquals("EMP01nameE/EMP01", (String) obj[0]);
            }
        }
    }
    /**
     * 新規 項番8.
     */
    @Test
    public void testValidateNew08() throws Exception {
        if (executeNewTest){
            Correspon correspon = createCorresponNew08();
            try {
                // テスト実行
                service.validate(correspon);
                fail("例外が発生しませんでした");
            } catch (ServiceAbortException e) {
                assertEquals(
                    ApplicationMessageCode.INVALID_ATTENTION,
                    e.getMessageCode());
                Object[] obj = e.getMessageVars();
                assertEquals("EMP01nameE/EMP01", (String) obj[0]);
            }
        }
    }
    /**
     * 新規 項番9.
     */
    @Test
    public void testValidateNew09() throws Exception {
        if (executeNewTest){
            Correspon correspon = createCorresponNew09();
            try {
                // テスト実行
                service.validate(correspon);
                fail("例外が発生しませんでした");
            } catch (ServiceAbortException e) {
                assertEquals(
                    ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_USER_NOT_EXIST,
                    e.getMessageCode());
            }
        }
    }
    /**
     * 新規 項番10.
     */
    @Test
    public void testValidateNew10() throws Exception {
        if (executeNewTest){
            Correspon correspon = createCorresponNew10();
            try {
                // テスト実行
                service.validate(correspon);
                fail("例外が発生しませんでした");
            } catch (ServiceAbortException e) {
                assertEquals(
                    ApplicationMessageCode.INVALID_USER,
                    e.getMessageCode());
                Object[] obj = e.getMessageVars();
                assertEquals("EMP02NameE/EMP02", (String) obj[0]);
            }
        }
    }
    /**
     * 新規 項番11.
     */
    @Test
    public void testValidateNew11() throws Exception {
        if (executeNewTest){
            Correspon correspon = createCorresponNew11();
            try {
                // テスト実行
                service.validate(correspon);
                fail("例外が発生しませんでした");
            } catch (ServiceAbortException e) {
                assertEquals(
                    ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CORRESPON_TYPE_NOT_EXIST,
                    e.getMessageCode());
            }
        }
    }
    /**
     * 新規 項番12.
     */
    @Test
    public void testValidateNew12() throws Exception {
        if (executeNewTest){
            Correspon correspon = createCorresponNew12();
            try {
                // テスト実行
                service.validate(correspon);
                fail("例外が発生しませんでした");
            } catch (ServiceAbortException e) {
                assertEquals(
                    ApplicationMessageCode.INVALID_CORRESPON_TYPE,
                    e.getMessageCode());
                Object[] obj = e.getMessageVars();
                assertEquals("ct.CorresponType", (String) obj[0]);
            }
        }
    }
    /**
     * 新規 項番13.
     */
    @Test
    public void testValidateNew13() throws Exception {
        if (executeNewTest){
            Correspon correspon = createCorresponNew13();
            try {
                // テスト実行
                service.validate(correspon);
                fail("例外が発生しませんでした");
            } catch (ServiceAbortException e) {
                assertEquals(
                    ApplicationMessageCode.FILE_ALREADY_ATTACHED,
                    e.getMessageCode());
                Object[] obj = e.getMessageVars();
                assertEquals("filename1and2", (String) obj[0]);
            }
        }
    }
    /**
     * 新規 項番14.
     */
    @Test
    public void testValidateNew14() throws Exception {
        if (executeNewTest){
            Correspon correspon = createCorresponNew14();
            try {
                // テスト実行
                service.validate(correspon);
                fail("例外が発生しませんでした");
            } catch (ServiceAbortException e) {
                assertEquals(
                    ApplicationMessageCode.FILE_ALREADY_ATTACHED,
                    e.getMessageCode());
                Object[] obj = e.getMessageVars();
                assertEquals("filename1and5", (String) obj[0]);
            }
        }
    }
    /**
     * 新規 項番15.
     */
    @Test
    public void testValidateNew15() throws Exception {
        if (executeNewTest){
            Correspon correspon = createCorresponNew15();
            try {
                // テスト実行
                service.validate(correspon);
                fail("例外が発生しませんでした");
            } catch (ServiceAbortException e) {
                assertEquals(
                    ApplicationMessageCode.FILE_ALREADY_ATTACHED,
                    e.getMessageCode());
                Object[] obj = e.getMessageVars();
                assertEquals("filename2and3", (String) obj[0]);
            }
        }
    }
    /**
     * 新規 項番16.
     */
    @Test
    public void testValidateNew16() throws Exception {
        if (executeNewTest){
            Correspon correspon = createCorresponNew16();
            try {
                // テスト実行
                service.validate(correspon);
                fail("例外が発生しませんでした");
            } catch (ServiceAbortException e) {
                assertEquals(
                    ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CUSTOM_FIELD_NOT_EXIST,
                    e.getMessageCode());
            }
        }
    }
    /**
     * 新規 項番17.
     */
    @Test
    public void testValidateNew17() throws Exception {
        if (executeNewTest){
            Correspon correspon = createCorresponNew17();
            try {
                // テスト実行
                service.validate(correspon);
                fail("例外が発生しませんでした");
            } catch (ServiceAbortException e) {
                assertEquals(
                    ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CUSTOM_FIELD_NOT_EXIST,
                    e.getMessageCode());
            }
        }
    }

    /**
     * 新規 項番20.
     */
    @Test
    public void testValidateNew20() throws Exception {
        if (executeNewTest){
            Correspon correspon = createCorresponNew20();
            try {
                // テスト実行
                service.validate(correspon);
                fail("例外が発生しませんでした");
            } catch (ServiceAbortException e) {
                assertEquals(
                    ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ORIGINAL_CORRESPON_FOR_REPLY_NOT_EXIST,
                    e.getMessageCode());
            }
        }
    }
    /**
     * 新規 項番21.
     */
    @Test
    public void testValidateNew21() throws Exception {
        if (executeNewTest){
            Correspon correspon = createCorresponNew21();
            try {
                // テスト実行
                service.validate(correspon);
                fail("例外が発生しませんでした");
            } catch (ServiceAbortException e) {
                assertEquals(
                    ApplicationMessageCode.INVALID_ORIGINAL_CORRESPON,
                    e.getMessageCode());
                Object[] obj = e.getMessageVars();
                assertEquals("correspon21", (String) obj[0]);
            }
        }
    }
    /**
     * 新規 項番22.
     */
    @Test
    public void testValidateNew22() throws Exception {
        if (executeNewTest){
            Correspon correspon = createCorresponNew22();
            try {
                // テスト実行
                service.validate(correspon);
                fail("例外が発生しませんでした");
            } catch (ServiceAbortException e) {
                assertEquals(
                    ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ORIGINAL_WORKFLOW_STATUS_INVALID,
                    e.getMessageCode());
            }
        }
    }
    /**
     * 新規 項番23.
     */
    @Test
    public void testValidateNew23() throws Exception {
        if (executeNewTest){
            Correspon correspon = createCorresponNew23();
            try {
                // テスト実行
                service.validate(correspon);
                fail("例外が発生しませんでした");
            } catch (ServiceAbortException e) {
                assertEquals(
                    ApplicationMessageCode.INVALID_ORIGINAL_CORRESPON,
                    e.getMessageCode());
            }
        }
    }
    /**
     * 新規 項番24.
     */
    @Test
    public void testValidateNew24() throws Exception {
        if (executeNewTest){
            Correspon correspon = createCorresponNew24();
            try {
                // テスト実行
                service.validate(correspon);
                fail("例外が発生しませんでした");
            } catch (ServiceAbortException e) {
                assertEquals(
                    ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ORIGINAL_CORRESPON_FOR_REVISION_NOT_EXIST,
                    e.getMessageCode());
            }
        }
    }
    /**
     * 新規 項番25.
     */
    @Test
    public void testValidateNew25() throws Exception {
        if (executeNewTest){
            Correspon correspon = createCorresponNew25();
            try {
                // テスト実行
                service.validate(correspon);
                fail("例外が発生しませんでした");
            } catch (ServiceAbortException e) {
                assertEquals(
                    ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CORREPON_NOT_CANCELED,
                    e.getMessageCode());
            }
        }
    }

    /**
     * 新規 宛先が重複している.
     */
    @Test
    public void testValidateNewToDuplicated() throws Exception {
        if (executeNewTest){
            Correspon correspon = createCorresponNew01();
            List<AddressCorresponGroup> acgList = new ArrayList<AddressCorresponGroup>();
            AddressCorresponGroup acg = null;
            acg = new AddressCorresponGroup();
            acg.setMode(UpdateMode.NEW);
            acg.setAddressType(AddressType.TO);
            CorresponGroup corresponGroup = new CorresponGroup();
            corresponGroup.setId(13L);
            acg.setCorresponGroup(corresponGroup);
            acgList.add(acg);
            acg = new AddressCorresponGroup();
            acg.setAddressType(AddressType.TO);
            acg.setMode(UpdateMode.NEW);
            acg.setCorresponGroup(corresponGroup);
            acg.setId(1L);
            acgList.add(acg);
            correspon.setAddressCorresponGroups(acgList);

            // to/Cc①②
            CorresponGroup toCcCg = new CorresponGroup();
            toCcCg.setProjectId(correspon.getProjectId());
            MockAbstractDao.RET_FIND_BY_ID.put(13L, toCcCg);

            try {
                // テスト実行
                service.validate(correspon);
                fail("例外が発生しませんでした");
            } catch (ServiceAbortException e) {
                assertEquals(
                    ApplicationMessageCode.DUPLICATED_TO_GROUP,
                    e.getMessageCode());
            }
        }
    }
    /**
     * 新規 Ccが重複している.
     */
    @Test
    public void testValidateNewCcDuplicated() throws Exception {
        if (executeNewTest){
            Correspon correspon = createCorresponNew01();
            List<AddressCorresponGroup> acgList = new ArrayList<AddressCorresponGroup>();
            AddressCorresponGroup acg = null;
            acg = new AddressCorresponGroup();
            acg.setMode(UpdateMode.NEW);
            acg.setAddressType(AddressType.CC);
            CorresponGroup corresponGroup = new CorresponGroup();
            corresponGroup.setId(14L);
            acg.setCorresponGroup(corresponGroup);
            acgList.add(acg);
            acg = new AddressCorresponGroup();
            acg.setAddressType(AddressType.CC);
            acg.setMode(UpdateMode.NEW);
            acg.setCorresponGroup(corresponGroup);
            acg.setId(1L);
            acgList.add(acg);
            correspon.setAddressCorresponGroups(acgList);

            // to/Cc①②
            CorresponGroup toCcCg = new CorresponGroup();
            toCcCg.setProjectId(correspon.getProjectId());
            MockAbstractDao.RET_FIND_BY_ID.put(14L, toCcCg);

            try {
                // テスト実行
                service.validate(correspon);
                fail("例外が発生しませんでした");
            } catch (ServiceAbortException e) {
                assertEquals(
                    ApplicationMessageCode.DUPLICATED_CC_GROUP,
                    e.getMessageCode());
            }
        }
    }

    /**
     * 新規 To①.
     */
    @Test
    public void testValidateNewTo1() throws Exception {
        if (executeNewTest){
            Correspon c =createCorresponNew01();
            MockAbstractDao.RET_FIND_BY_ID.put(11L, null);
            try {
                // テスト実行
                service.validate(c);
                fail("例外が発生しませんでした");
            } catch (ServiceAbortException e) {
                assertEquals(
                    ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_GROUP_NOT_EXIST,
                    e.getMessageCode());
                Object[] obj = e.getMessageVars();
                assertEquals("c-group", (String) obj[0]);
            }
        }
    }

    /**
     * 新規 To②.
     */
    @Test
    public void testValidateNewTo2() throws Exception {
        if (executeNewTest){
            Correspon c =createCorresponNew01();
            CorresponGroup toCcCg = new CorresponGroup();
            toCcCg.setProjectId(c.getProjectId() + "diff");
            MockAbstractDao.RET_FIND_BY_ID.put(11L, toCcCg);

            try {
                // テスト実行
                service.validate(c);
                fail("例外が発生しませんでした");
            } catch (ServiceAbortException e) {
                assertEquals(
                    ApplicationMessageCode.INVALID_GROUP,
                    e.getMessageCode());
                Object[] obj = e.getMessageVars();
                assertEquals("c-group", (String) obj[0]);
            }
        }
    }

    /**
     * 新規 Cc①.
     */
    @Test
    public void testValidateNewCc1() throws Exception {
        if (executeNewTest){
            Correspon correspon = createCorresponNew10();
            MockAbstractDao.RET_FIND_BY_ID.put(12L, null);
            try {
                // テスト実行
                service.validate(correspon);
                fail("例外が発生しませんでした");
            } catch (ServiceAbortException e) {
                assertEquals(
                    ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_GROUP_NOT_EXIST,
                    e.getMessageCode());
                Object[] obj = e.getMessageVars();
                assertEquals("Cc-group", (String) obj[0]);
            }
        }
    }

    /**
     * 新規 Cc②.
     */
    @Test
    public void testValidateNewCc2() throws Exception {
        if (executeNewTest){
            Correspon correspon = createCorresponNew10();
            CorresponGroup toCcCg = new CorresponGroup();
            toCcCg.setProjectId(correspon.getProjectId() + "diff");
            MockAbstractDao.RET_FIND_BY_ID.put(12L, toCcCg);

            try {
                // テスト実行
                service.validate(correspon);
                fail("例外が発生しませんでした");
            } catch (ServiceAbortException e) {
                assertEquals(
                    ApplicationMessageCode.INVALID_GROUP,
                    e.getMessageCode());
                Object[] obj = e.getMessageVars();
                assertEquals("Cc-group", (String) obj[0]);
            }
        }
    }

    /**
     * 新規(返信文書の改訂)
     */
    @Test
    public void testValidateNewReviseOfReply() throws Exception {
        if (executeNewTest){
            Correspon correspon = createCorresponNewReviseOfReply();
            try {
                // テスト実行
                service.validate(correspon);
            } catch (ServiceAbortException e) {
                e.printStackTrace();
                fail("例外が発生." + e);
            }
        }
    }

    /////////////////////////////////////////////////////
    /**
     * 新規 項番1に必要なデータを作成する.
     *
     * @return
     */
    private Correspon createCorresponNew01() {
        Correspon c = new Correspon();

        // 新規
        c.setId(null);
        c.setCorresponNo("corresponNo01");

        // 活動単位ユーザーチェック
        MockAbstractService.VALIDATE_PROJECT_ID = null;

        // 現在のユーザー設定
        User user = new User();
        user.setEmpNo("testuser001");
        MockAbstractService.CURRENT_USER = user;

        // 文書状態
        c.setCorresponStatus(CorresponStatus.OPEN);


        Map<Long, CorresponGroup> findByIdResultMap = new HashMap<>();
        // From①
        CorresponGroup cg = new CorresponGroup();
        cg.setId(64L);
        cg.setProjectId("PJtest01");
        cg.setName("CorresponGroup.setName()");

        findByIdResultMap.put(cg.getId(), cg);

        // From②
        c.setFromCorresponGroup(cg);
        c.setProjectId(cg.getProjectId());


        // To/Cc③
        List<AddressUser> auList = new ArrayList<AddressUser>();
        AddressUser au = null;
        User u = null;

        u = new User();
        u.setEmpNo("EMP01");
        u.setNameE("EMP01nameE");
        u.setNameJ("EMP01nameJ");
        au = new AddressUser();
        au.setUser(u);
        au.setAddressUserType(AddressUserType.ATTENTION);
        auList.add(au);
        u = new User();
        u.setEmpNo("EMP02");
        u.setNameE("EMP02nameE");
        u.setNameJ("EMP02nameJ");
        au = new AddressUser();
        au.setUser(u);
        au.setAddressUserType(AddressUserType.NORMAL_USER);
        auList.add(au);

        new MockUp<AddressUserDaoImpl>() {
            @Mock List<AddressUser> findByAddressCorresponGroupId(Long addressCorresponGroupId) {
                return auList;
            }
        };

        List<AddressCorresponGroup> acgList = new ArrayList<AddressCorresponGroup>();
        AddressCorresponGroup acg = null;
        acg = new AddressCorresponGroup();
        acg.setMode(UpdateMode.NEW);
        acg.setAddressType(AddressType.TO);
        acg.setId(1L);
        acg.setUsers(auList);
        CorresponGroup group = new CorresponGroup();
        group.setId(11L);
        group.setName("c-group");
        acg.setCorresponGroup(group);
        acgList.add(acg);
        acg = new AddressCorresponGroup();
        acg.setMode(UpdateMode.NONE);
        acg.setAddressType(AddressType.CC);
        acg.setId(2L);
        group = new CorresponGroup();
        group.setId(11L);
        acg.setCorresponGroup(group);
        acgList.add(acg);
        c.setAddressCorresponGroups(acgList);

        new MockUp<UserDaoImpl>() {
            @Mock User findByEmpNo(String empNo) throws RecordNotFoundException {
                return new User();
            }
        };

        // to/Cc①②
        CorresponGroup toCcCg = new CorresponGroup();
        toCcCg.setProjectId(c.getProjectId());

        findByIdResultMap.put(11L, toCcCg);


        // To/Cc④
        MockAbstractService.CURRENT_PROJECT_ID = "PJtest01";
        MockAbstractService.IS_SYSTEM_ADMIN = true;
        List<Project> pjList = new ArrayList<Project>();
        Project pj = new Project();
        pj.setProjectId("PJtest01");
        pjList.add(pj);

        new MockUp<ProjectDaoImpl>() {
            @Mock List<Project> findByEmpNo(String empNo) {
                return pjList;
            }
        };

        // Correspondence Type①
        CorresponType ct = new CorresponType();
        ct.setId(128L);
        ct.setProjectId("PJtest01");
        ct.setCorresponType("ct.CorresponType");
        c.setCorresponType(ct);

        new MockUp<CorresponTypeDaoImpl>() {
            @Mock CorresponType findById(Long id) {
                return ct;
            }
            @Mock CorresponType findByIdProjectId(Long id, String projectId)
                    throws RecordNotFoundException {
                return new CorresponType();
            }
        };

        // Attachments
        c.setFile1FileName("filename1");
        c.setFile2FileName("filename2");
        c.setFile3FileName("filename3");
        c.setFile4FileName("filename4");
        c.setFile5FileName("filename5");

        // Field
        c.setCustomField1Id(9801L);
        c.setCustomField2Id(9802L);
        c.setCustomField3Id(9803L);
        c.setCustomField4Id(9804L);
        c.setCustomField5Id(9805L);
        c.setCustomField6Id(9806L);
        c.setCustomField7Id(9807L);
        c.setCustomField8Id(9808L);
        c.setCustomField9Id(9809L);
        c.setCustomField10Id(9810L);
        c.setCustomField1Value("CustomField1Value");
        c.setCustomField2Value("CustomField2Value");
        c.setCustomField3Value("CustomField3Value");
        c.setCustomField4Value("CustomField4Value");
        c.setCustomField5Value("CustomField5Value");
        c.setCustomField6Value("CustomField6Value");
        c.setCustomField7Value("CustomField7Value");
        c.setCustomField8Value("CustomField8Value");
        c.setCustomField9Value("CustomField9Value");
        c.setCustomField10Value("CustomField10Value");

        List<CustomField> findResult = new ArrayList<>();
        CustomField f;
        f = new CustomField();
        f.setId(1L);
        f.setProjectCustomFieldId(9801L);
        f.setLabel("label1");
        findResult.add(f);
        f = new CustomField();
        f.setId(2L);
        f.setProjectCustomFieldId(9802L);
        f.setLabel("label2");
        findResult.add(f);
        f = new CustomField();
        f.setId(3L);
        f.setProjectCustomFieldId(9803L);
        f.setLabel("label3");
        findResult.add(f);
        f = new CustomField();
        f.setId(4L);
        f.setProjectCustomFieldId(9804L);
        f.setLabel("label4");
        findResult.add(f);
        f = new CustomField();
        f.setId(5L);
        f.setProjectCustomFieldId(9805L);
        f.setLabel("label5");
        findResult.add(f);
        f = new CustomField();
        f.setId(6L);
        f.setProjectCustomFieldId(9806L);
        f.setLabel("label6");
        findResult.add(f);
        f = new CustomField();
        f.setId(7L);
        f.setProjectCustomFieldId(9807L);
        f.setLabel("label7");
        findResult.add(f);
        f = new CustomField();
        f.setId(8L);
        f.setProjectCustomFieldId(9808L);
        f.setLabel("label8");
        findResult.add(f);
        f = new CustomField();
        f.setId(9L);
        f.setProjectCustomFieldId(9809L);
        f.setLabel("label9");
        findResult.add(f);
        f = new CustomField();
        f.setId(10L);
        f.setProjectCustomFieldId(9810L);
        f.setLabel("label10");
        findResult.add(f);

        new MockUp<CustomFieldDaoImpl>() {
            @Mock List<CustomField> findByProjectId(SearchCustomFieldCondition condition) {
                return findResult;
            }
        };
//
//        MockProjectCustomFieldDao.RET.put(1L,9801L);
//        MockProjectCustomFieldDao.RET.put(2L,9802L);
//        MockProjectCustomFieldDao.RET.put(3L,9803L);
//        MockProjectCustomFieldDao.RET.put(4L,9804L);
//        MockProjectCustomFieldDao.RET.put(5L,9805L);
//        MockProjectCustomFieldDao.RET.put(6L,9806L);
//        MockProjectCustomFieldDao.RET.put(7L,9807L);
//        MockProjectCustomFieldDao.RET.put(8L,9808L);
//        MockProjectCustomFieldDao.RET.put(9L,9809L);
//        MockProjectCustomFieldDao.RET.put(10L,9810L);

        // 返信①
        c.setParentCorresponId(2L);

        // 返信②
        Correspon sourceC = new Correspon();
        sourceC.setProjectId("PJtest01");

        // 返信③
        sourceC.setWorkflowStatus(WorkflowStatus.ISSUED);

        // 返信④
        sourceC.setCorresponStatus(CorresponStatus.CLOSED);

        new MockUp<CorresponDaoImpl>() {
            @Mock Correspon findById(Long id) throws RecordNotFoundException {
                return sourceC;
            }
        };

        List<String> empNos = new ArrayList<String>();
        empNos.add("EMP01");
        empNos.add("EMP02");

        new MockUp<UserDaoImpl>() {
            @Mock List<String> findEmpNo() {
                return empNos;
            }
        };

        new MockUp<CorresponGroupDaoImpl>() {
            @Mock CorresponGroup findById(Long id) throws RecordNotFoundException {
                if (findByIdResultMap.containsKey(id)) {
                    return findByIdResultMap.get(id);
                }
                throw new RecordNotFoundException();
            }
        };

        return c;
    }

    /**
     * 新規 項番2に必要なデータを作成する.
     *
     * @return
     */
    private Correspon createCorresponNew02() {
        Correspon c =createCorresponNew01();

        // 返信を取り消し
        c.setParentCorresponId(null);
        MockAbstractDao.RET_FIND_BY_ID.put(2L, null);

        // 改定①
        c.setPreviousRevCorresponId(2L);

        // 改定②
        Correspon sourceC = new Correspon();
        sourceC.setCorresponStatus(CorresponStatus.CANCELED);
        MockAbstractDao.RET_FIND_BY_ID.put(2L, sourceC);

        List<String> empNos = new ArrayList<String>();
        empNos.add("EMP01");
        empNos.add("EMP02");
        MockUserDao.RET_FIND_EMP_NO = empNos;

        return c;
    }

    /**
     * 新規 項番3に必要なデータを作成する.
     *
     * @return
     */
    private Correspon createCorresponNew03() {
        Correspon c =createCorresponNew01();
        // 活動単位ユーザーチェック
        MockAbstractService.VALIDATE_PROJECT_ID = new ServiceAbortException(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_PROJECT_DIFF);
        return c;
    }

    /**
     * 新規 項番4に必要なデータを作成する.
     *
     * @return
     */
    private Correspon createCorresponNew04() {
        Correspon c =createCorresponNew01();
        c.setCorresponStatus(CorresponStatus.CANCELED);
        return c;
    }

    /**
     * 新規 項番5に必要なデータを作成する.
     *
     * @return
     */
    private Correspon createCorresponNew05() {
        Correspon c =createCorresponNew01();
        MockAbstractDao.RET_FIND_BY_ID.put(64L, null);
        return c;
    }

    /**
     * 新規 項番6に必要なデータを作成する.
     *
     * @return
     */
    private Correspon createCorresponNew06() {
        Correspon c =createCorresponNew01();
        CorresponGroup cg = new CorresponGroup();
        cg.setId(64L);
        cg.setProjectId("PJtest01-diff");
        MockAbstractDao.RET_FIND_BY_ID.put(64L, cg);
        return c;
    }
    /**
     * 新規 項番7に必要なデータを作成する.
     *
     * @return
     */
    private Correspon createCorresponNew07() {
        Correspon c =createCorresponNew01();
        List<String> empNos = new ArrayList<String>();
        empNos.add("EMP03");
        MockUserDao.RET_FIND_EMP_NO = empNos;
        return c;
    }
    /**
     * 新規 項番8に必要なデータを作成する.
     *
     * @return
     */
    private Correspon createCorresponNew08() {
        Correspon c =createCorresponNew01();
        List<String> empNos = new ArrayList<String>();
        empNos.add("EMP01");
        empNos.add("EMP02");
        MockUserDao.RET_FIND_EMP_NO = empNos;

        List<ProjectUser> puList = new ArrayList<ProjectUser>();
        User user = new User();
        user.setEmpNo("EMP03");
        ProjectUser pu = new ProjectUser();
        pu.setUser(user);
        puList.add(pu);
        MockUserDao.RET_FIND_PROJECT_USER = puList;
        MockAbstractService.IS_SYSTEM_ADMIN = false;
        return c;
    }
    /**
     * 新規 項番9に必要なデータを作成する.
     *
     * @return
     */
    private Correspon createCorresponNew09() {
        Correspon c =createCorresponNew01();

        List<AddressUser> auList = new ArrayList<AddressUser>();
        AddressUser au = null;
        User u = null;

        u = new User();
        u.setEmpNo("EMP01");
        au = new AddressUser();
        au.setUser(u);
        au.setAddressUserType(AddressUserType.ATTENTION);
        auList.add(au);
        u = new User();
        u.setEmpNo("EMP02");
        u.setNameE("EMP02NameE");
        u.setNameJ("EMP02NameJ");
        au = new AddressUser();
        au.setUser(u);
        au.setAddressUserType(AddressUserType.NORMAL_USER);
        auList.add(au);
        MockAddressUserDao.RET_FIND_BY_ADDRESS_CORRESPON_GROUP_ID = auList;

        List<AddressCorresponGroup> acgList = new ArrayList<AddressCorresponGroup>();
        AddressCorresponGroup acg = null;
        acg = new AddressCorresponGroup();
        acg.setMode(UpdateMode.NONE);
        acg.setAddressType(AddressType.CC);
        acg.setId(1L);
        CorresponGroup group = new CorresponGroup();
        group.setId(11L);
        acg.setCorresponGroup(group);
        acgList.add(acg);
        acg = new AddressCorresponGroup();
        acg.setMode(UpdateMode.UPDATE);
        acg.setAddressType(AddressType.CC);
        acg.setId(2L);
        acg.setUsers(auList);
        group = new CorresponGroup();
        group.setId(12L);
        acg.setCorresponGroup(group);
        acgList.add(acg);
        c.setAddressCorresponGroups(acgList);

        CorresponGroup toCcCg = new CorresponGroup();
        toCcCg.setProjectId(c.getProjectId());
        MockAbstractDao.RET_FIND_BY_ID.put(12L, toCcCg);

        List<String> empNos = new ArrayList<String>();
        empNos.add("EMP03");
        MockUserDao.RET_FIND_EMP_NO = empNos;

        MockUserDao.RET_FIND_BY_EMP_NO = null;
        return c;
    }
    /**
     * 新規 項番10に必要なデータを作成する.
     *
     * @return
     */
    private Correspon createCorresponNew10() {
        Correspon c =createCorresponNew01();

        List<AddressUser> auList = new ArrayList<AddressUser>();
        AddressUser au = null;
        User u = null;

        u = new User();
        u.setEmpNo("EMP01");
        au = new AddressUser();
        au.setUser(u);
        au.setAddressUserType(AddressUserType.ATTENTION);
        auList.add(au);
        u = new User();
        u.setEmpNo("EMP02");
        u.setNameE("EMP02NameE");
        u.setNameJ("EMP02NameJ");
        au = new AddressUser();
        au.setUser(u);
        au.setAddressUserType(AddressUserType.NORMAL_USER);
        auList.add(au);
        MockAddressUserDao.RET_FIND_BY_ADDRESS_CORRESPON_GROUP_ID = auList;
        new MockUp<AddressUserDaoImpl>() {
            @Mock List<AddressUser> findByAddressCorresponGroupId(Long addressCorresponGroupId) {
                return auList;
            }
        };

        List<AddressCorresponGroup> acgList = new ArrayList<AddressCorresponGroup>();
        AddressCorresponGroup acg = null;
        acg = new AddressCorresponGroup();
        acg.setMode(UpdateMode.NEW);
        acg.setAddressType(AddressType.CC);
        acg.setId(1L);
        acg.setUsers(auList);
        CorresponGroup group = new CorresponGroup();
        group.setId(12L);
        group.setName("Cc-group");
        acg.setCorresponGroup(group);
        acgList.add(acg);
        acg = new AddressCorresponGroup();
        acg.setMode(UpdateMode.DELETE);
        acg.setAddressType(AddressType.CC);
        acg.setId(2L);
        acgList.add(acg);
        c.setAddressCorresponGroups(acgList);
        MockUserDao.RET_FIND_BY_EMP_NO = new User();

        CorresponGroup toCcCg = new CorresponGroup();
        toCcCg.setProjectId(c.getProjectId());
        MockAbstractDao.RET_FIND_BY_ID.put(12L, toCcCg);
        new MockUp<CorresponGroupDaoImpl>() {
            @Mock CorresponGroup findById(Long id) {
                return toCcCg;
            }
        };

        List<String> empNos = new ArrayList<String>();
        empNos.add("EMP01");
        empNos.add("EMP02");
        MockUserDao.RET_FIND_EMP_NO = empNos;

        List<ProjectUser> puList = new ArrayList<ProjectUser>();
        User user = new User();
        user.setEmpNo("EMP03");
        ProjectUser pu = new ProjectUser();
        pu.setUser(user);
        puList.add(pu);
        MockUserDao.RET_FIND_PROJECT_USER = puList;

        new MockUp<UserDaoImpl>() {
            @Mock User findByEmpNo(String empNo) throws RecordNotFoundException {
                return new User();
            }

            @Mock List<String> findEmpNo() {
                return empNos;
            }

            @Mock List<ProjectUser> findProjectUser(SearchUserCondition condition) {
                return puList;
            }
        };

        List<Project> pjList = new ArrayList<Project>();
        MockProjectDao.RET_FIND_BY_EMP_NO = pjList;

        new MockUp<ProjectDaoImpl>() {
            @Mock List<Project> findByEmpNo(String empNo) {
                return pjList;
            }
        };

        MockAbstractService.IS_SYSTEM_ADMIN = false;
        return c;
    }

    /**
     * 新規 項番11に必要なデータを作成する.
     *
     * @return
     */
    private Correspon createCorresponNew11() {
        Correspon c =createCorresponNew01();
        MockAbstractDao.RET_FIND_BY_ID.put(c.getCorresponType().getId(), null);
        return c;
    }

    /**
     * 新規 項番12に必要なデータを作成する.
     *
     * @return
     */
    private Correspon createCorresponNew12() {
        Correspon c =createCorresponNew01();

        // Correspondence Type②
        MockCorresponTypeDao.RET_FIND_BY_ID_PROJECT__ID  = null;

        return c;
    }

    /**
     * 新規 項番13に必要なデータを作成する.
     *
     * @return
     */
    private Correspon createCorresponNew13() {
        Correspon c = createCorresponNew01();

        List<Attachment> attachments = new ArrayList<Attachment>();
        Attachment a;

        a = new Attachment();
        a.setMode(UpdateMode.NEW);
        a.setFileName("filename1and2");
        attachments.add(a);

        a = new Attachment();
        a.setMode(UpdateMode.NONE);     // 保存済ファイルと重複している場合を確認
        a.setFileName("filename1and2");
        attachments.add(a);

        a = new Attachment();
        a.setMode(UpdateMode.NEW);
        a.setFileName("filename3");
        attachments.add(a);

        a = new Attachment();
        a.setMode(UpdateMode.DELETE);   // 削除済なので同名ファイルがあってもOK
        a.setFileName("filename3");
        attachments.add(a);

        a = new Attachment();
        a.setMode(UpdateMode.NEW);
        a.setFileName("filename4");
        attachments.add(a);

        a = new Attachment();
        a.setMode(UpdateMode.NEW);
        a.setFileName("filename5");
        attachments.add(a);

        c.setUpdateAttachments(attachments);

        return c;
    }
    /**
     * 新規 項番14に必要なデータを作成する.
     *
     * @return
     */
    private Correspon createCorresponNew14() {
        Correspon c =createCorresponNew01();

        List<Attachment> attachments = new ArrayList<Attachment>();
        Attachment a;

        a = new Attachment();
        a.setMode(UpdateMode.NEW);
        a.setFileName("filename1and5");
        attachments.add(a);

        a = new Attachment();
        a.setMode(UpdateMode.NONE);
        a.setFileName("filename1");
        attachments.add(a);

        a = new Attachment();
        a.setMode(UpdateMode.DELETE);   // 削除済なので同名ファイルがあってもOK
        a.setFileName("filename2");
        attachments.add(a);

        a = new Attachment();
        a.setMode(UpdateMode.NEW);
        a.setFileName("filename3");
        attachments.add(a);

        a = new Attachment();
        a.setMode(UpdateMode.NEW);
        a.setFileName("filename4");
        attachments.add(a);

        a = new Attachment();
        a.setMode(UpdateMode.NEW);
        a.setFileName("filename1and5");
        attachments.add(a);

        c.setUpdateAttachments(attachments);

        return c;
    }
    /**
     * 新規 項番15に必要なデータを作成する.
     *
     * @return
     */
    private Correspon createCorresponNew15() {
        Correspon c =createCorresponNew01();

        List<Attachment> attachments = new ArrayList<Attachment>();
        Attachment a;

        a = new Attachment();
        a.setMode(UpdateMode.NEW);
        a.setFileName("filename1");
        attachments.add(a);

        a = new Attachment();
        a.setMode(UpdateMode.NONE);
        a.setFileName("filename2and3");
        attachments.add(a);

        a = new Attachment();
        a.setMode(UpdateMode.NEW);   // 削除済なので同名ファイルがあってもOK
        a.setFileName("filename2and3");
        attachments.add(a);

        a = new Attachment();
        a.setMode(UpdateMode.NEW);
        a.setFileName("filename4");
        attachments.add(a);

        a = new Attachment();
        a.setMode(UpdateMode.NEW);
        a.setFileName("filename5");
        attachments.add(a);

        c.setUpdateAttachments(attachments);

        return c;
    }
    /**
     * 新規 項番16に必要なデータを作成する.
     *
     * @return
     */
    private Correspon createCorresponNew16() {
        Correspon c =createCorresponNew01();
        // 先頭のカスタムフィールドがマスタに存在しない状態にする
        MockCustomFieldDao.RET_FIND_BY_PROJECT_ID.remove(0);
//        MockProjectCustomFieldDao.RET.put(1L, null);
        return c;
    }
    /**
     * 新規 項番17に必要なデータを作成する.
     *
     * @return
     */
    private Correspon createCorresponNew17() {
        Correspon c =createCorresponNew01();
        // 末尾のカスタムフィールドがマスタに存在しない状態にする
        List<CustomField> fields = MockCustomFieldDao.RET_FIND_BY_PROJECT_ID;
        fields.remove(fields.size() -1);
//        MockProjectCustomFieldDao.RET.put(10L, null);
        return c;
    }


    /**
     * 新規 項番20に必要なデータを作成する.
     *
     * @return
     */
    private Correspon createCorresponNew20() {
        Correspon c =createCorresponNew01();
        MockAbstractDao.RET_FIND_BY_ID.put(2L, null);
        return c;
    }

    /**
     * 新規 項番21に必要なデータを作成する.
     *
     * @return
     */
    private Correspon createCorresponNew21() {
        Correspon c =createCorresponNew01();
        Correspon sourceC = new Correspon();
        sourceC.setProjectId("PJtest-no01");
        sourceC.setCorresponNo("correspon21");
        sourceC.setWorkflowStatus(WorkflowStatus.ISSUED);
        MockAbstractDao.RET_FIND_BY_ID.put(2L, sourceC);
        return c;
    }

    /**
     * 新規 項番22に必要なデータを作成する.
     *
     * @return
     */
    private Correspon createCorresponNew22() {
        Correspon c =createCorresponNew01();
        Correspon sourceC = new Correspon();
        sourceC.setProjectId("PJtest01");
        sourceC.setWorkflowStatus(WorkflowStatus.DRAFT);
        MockAbstractDao.RET_FIND_BY_ID.put(2L, sourceC);
        return c;
    }

    /**
     * 新規 項番23に必要なデータを作成する.
     *
     * @return
     */
    private Correspon createCorresponNew23() {
        Correspon c =createCorresponNew01();
        Correspon sourceC = new Correspon();
        sourceC.setProjectId("PJtest01");
        sourceC.setWorkflowStatus(WorkflowStatus.ISSUED);
        sourceC.setCorresponStatus(CorresponStatus.CANCELED);
        MockAbstractDao.RET_FIND_BY_ID.put(2L, sourceC);
        return c;
    }
    /**
     * 新規 項番24に必要なデータを作成する.
     *
     * @return
     */
    private Correspon createCorresponNew24() {
        Correspon c =createCorresponNew02();
        MockAbstractDao.RET_FIND_BY_ID.put(2L, null);

        return c;
    }
    /**
     * 新規 項番25に必要なデータを作成する.
     *
     * @return
     */
    private Correspon createCorresponNew25() {
        Correspon c =createCorresponNew02();
        Correspon sourceC = new Correspon();
        sourceC.setCorresponStatus(CorresponStatus.OPEN);
        MockAbstractDao.RET_FIND_BY_ID.put(2L, sourceC);

        return c;
    }

    /**
     * 新規(返信文書)の改訂に必要なデータを作成する.
     *
     * @return
     */
    private Correspon createCorresponNewReviseOfReply() {
        Correspon c =createCorresponNew01();
        Correspon sourceC = new Correspon();

        // 返信
        c.setParentCorresponId(2L);
        sourceC.setProjectId("PJtest01");
        sourceC.setWorkflowStatus(WorkflowStatus.ISSUED);
        sourceC.setCorresponStatus(CorresponStatus.CLOSED);

        // 改定
        c.setPreviousRevCorresponId(2L);
        sourceC.setCorresponStatus(CorresponStatus.CANCELED);

        MockAbstractDao.RET_FIND_BY_ID.put(2L, sourceC);
        return c;
    }

    ///////////////////////////////////////////////////////////////////////////////////////
    /**
     * 更新 項番1.
     */
    @Test
    public void testValidateUpdate01() throws Exception {
        if (executeUpdateTest) {
            Correspon correspon = createCorresponUpdate01();
            try {
                // テスト実行
                service.validate(correspon);
            } catch (ServiceAbortException e) {
                e.printStackTrace();
                fail("例外が発生." + e);
            }
        }
    }
    /**
     * 更新 項番2.
     */
    @Test
    public void testValidateUpdate02() throws Exception {
        if (executeUpdateTest) {
            Correspon correspon = createCorresponUpdate02();
            try {
                // テスト実行
                service.validate(correspon);
            } catch (ServiceAbortException e) {
                e.printStackTrace();
                fail("例外が発生." + e);
                Object[] vars = e.getMessageVars();
                for (Object o : vars) {
                    System.out.println(o);
                }
            }
        }
    }
    /**
     * 更新 項番3.
     */
    @Test
    public void testValidateUpdate03() throws Exception {
        if (executeUpdateTest) {
            Correspon correspon = createCorresponUpdate03();
            try {
                // テスト実行
                service.validate(correspon);
            } catch (ServiceAbortException e) {
                e.printStackTrace();
                fail("例外が発生." + e);
            }
        }
    }
    /**
     * 更新 項番4.
     */
    @Test
    public void testValidateUpdate04() throws Exception {
        if (executeUpdateTest) {
            Correspon correspon = createCorresponUpdate04();
            try {
                // テスト実行
                service.validate(correspon);
            } catch (ServiceAbortException e) {
                e.printStackTrace();
                fail("例外が発生." + e);
            }
        }
    }
    /**
     * 更新 項番5.
     */
    @Test
    public void testValidateUpdate05() throws Exception {
        if (executeUpdateTest) {
            Correspon correspon = createCorresponUpdate05();
            try {
                // テスト実行
                service.validate(correspon);
            } catch (ServiceAbortException e) {
                e.printStackTrace();
                fail("例外が発生." + e);
            }
        }
    }
    /**
     * 更新 項番6.
     */
    @Test
    public void testValidateUpdate06() throws Exception {
        if (executeUpdateTest) {
            Correspon correspon = createCorresponUpdate06();
            try {
                // テスト実行
                service.validate(correspon);
            } catch (ServiceAbortException e) {
                e.printStackTrace();
                fail("例外が発生." + e);
            }
        }
    }
    /**
     * 更新 項番7.
     */
    @Test
    public void testValidateUpdate07() throws Exception {
        if (executeUpdateTest) {
            Correspon correspon = createCorresponUpdate07();
            try {
                // テスト実行
                service.validate(correspon);
            } catch (ServiceAbortException e) {
                e.printStackTrace();
                fail("例外が発生." + e);
            }
        }
    }
    /**
     * 更新 項番8.
     */
    @Test
    public void testValidateUpdate08() throws Exception {
        if (executeUpdateTest) {
            Correspon correspon = createCorresponUpdate08();
            try {
                // テスト実行
                service.validate(correspon);
            } catch (ServiceAbortException e) {
                e.printStackTrace();
                fail("例外が発生." + e);
            }
        }
    }
    /**
     * 更新 項番9.
     */
    @Test
    public void testValidateUpdate09() throws Exception {
        if (executeUpdateTest) {
            Correspon correspon = createCorresponUpdate09();
            try {
                // テスト実行
                service.validate(correspon);
            } catch (ServiceAbortException e) {
                e.printStackTrace();
                fail("例外が発生." + e);
            }
        }
    }
    /**
     * 更新 項番10.
     */
    @Test
    public void testValidateUpdate10() throws Exception {
        if (executeUpdateTest) {
            Correspon correspon = createCorresponUpdate10();
            try {
                // テスト実行
                service.validate(correspon);
            } catch (ServiceAbortException e) {
                e.printStackTrace();
                fail("例外が発生." + e);
            }
        }
    }
    /**
     * 更新 項番11.
     */
    @Test
    public void testValidateUpdate11() throws Exception {
        if (executeUpdateTest) {
            Correspon correspon = createCorresponUpdate11();
            try {
                // テスト実行
                service.validate(correspon);
            } catch (ServiceAbortException e) {
                e.printStackTrace();
                fail("例外が発生." + e);
            }
        }
    }
    /**
     * 更新 項番12.
     */
    @Test
    public void testValidateUpdate12() throws Exception {
        if (executeUpdateTest) {
            Correspon correspon = createCorresponUpdate12();
            try {
                // テスト実行
                service.validate(correspon);
            } catch (ServiceAbortException e) {
                e.printStackTrace();
                fail("例外が発生." + e);
            }
        }
    }
    /**
     * 更新 項番13.
     */
    @Test
    public void testValidateUpdate13() throws Exception {
        if (executeUpdateTest) {
            Correspon correspon = createCorresponUpdate13();
            try {
                // テスト実行
                service.validate(correspon);
            } catch (ServiceAbortException e) {
                e.printStackTrace();
                fail("例外が発生." + e);
            }
        }
    }
    /**
     * 更新 項番14.
     */
    @Test
    public void testValidateUpdate14() throws Exception {
        if (executeUpdateTest) {
            Correspon correspon = createCorresponUpdate14();
            try {
                service.validate(correspon);
                fail("例外が発生しませんでした");
            } catch (ServiceAbortException e) {
                assertEquals(
                    ApplicationMessageCode.NO_DATA_FOUND,
                    e.getMessageCode());
            }
        }
    }
    /**
     * 更新 項番15.
     */
    @Test
    public void testValidateUpdate15() throws Exception {
        if (executeUpdateTest) {
            Correspon correspon = createCorresponUpdate15();
            try {
                service.validate(correspon);
                fail("例外が発生しませんでした");
            } catch (ServiceAbortException e) {
                assertEquals(
                    ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CORREPON_ALREADY_ISSUED,
                    e.getMessageCode());
            }
        }
    }
    /**
     * 更新 項番16.
     */
    @Test
    public void testValidateUpdate16() throws Exception {
        if (executeUpdateTest) {
            Correspon correspon = createCorresponUpdate16();
            try {
                service.validate(correspon);
                fail("例外が発生しませんでした");
            } catch (ServiceAbortException e) {
                assertEquals(
                    ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CORREPON_CANCELED,
                    e.getMessageCode());
            }
        }
    }
    /**
     * 更新 項番17.
     */
    @Test
    public void testValidateUpdate17() throws Exception {
        if (executeUpdateTest) {
            Correspon correspon = createCorresponUpdate17();
            try {
                service.validate(correspon);
                fail("例外が発生しませんでした");
            } catch (ServiceAbortException e) {
                assertEquals(
                    ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CORREPON_NOT_ISSUED_AND_CANCELED,
                    e.getMessageCode());
            }
        }
    }
    /**
     * 更新 項番18.
     */
    @Test
    public void testValidateUpdate18() throws Exception {
        if (executeUpdateTest) {
            Correspon correspon = createCorresponUpdate18();
            try {
                service.validate(correspon);
                fail("例外が発生しませんでした");
            } catch (ServiceAbortException e) {
                assertEquals(
                    ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_PROJECT_DIFF,
                    e.getMessageCode());
            }
        }
    }
    /**
     * 更新 項番19.
     */
    @Test
    public void testValidateUpdate19() throws Exception {
        if (executeUpdateTest) {
            Correspon correspon = createCorresponUpdate19();
            try {
                service.validate(correspon);
                fail("例外が発生しませんでした");
            } catch (ServiceAbortException e) {
                assertEquals(
                    ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CORRESPON_ALREADY_UPDATED,
                    e.getMessageCode());
            }
        }
    }
    /**
     * 更新 項番20.
     */
    @Test
    public void testValidateUpdate20() throws Exception {
        if (executeUpdateTest) {
            Correspon correspon = createCorresponUpdate20();
            try {
                service.validate(correspon);
                fail("例外が発生しませんでした");
            } catch (ServiceAbortException e) {
                assertEquals(
                    ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT,
//                    ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CORRESPON_STATUS_INVALID,
                    e.getMessageCode());
            }
        }
    }
    /**
     * 更新 項番21.
     */
    @Test
    public void testValidateUpdate21() throws Exception {
        if (executeUpdateTest) {
            Correspon correspon = createCorresponUpdate21();
            try {
                service.validate(correspon);
                fail("例外が発生しませんでした");
            } catch (ServiceAbortException e) {
                assertEquals(
                    ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT,
//                    ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CORRESPON_STATUS_INVALID,
                    e.getMessageCode());
            }
        }
    }
    /**
     * 更新 項番22.
     */
    @Test
    public void testValidateUpdate22() throws Exception {
        if (executeUpdateTest) {
            Correspon correspon = createCorresponUpdate22();
            try {
                service.validate(correspon);
                fail("例外が発生しませんでした");
            } catch (ServiceAbortException e) {
                assertEquals(
                    ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CORRESPON_STATUS_INVALID,
                    e.getMessageCode());
            }
        }
    }
    /**
     * 更新 項番23.
     */
    @Test
    public void testValidateUpdate23() throws Exception {
        if (executeUpdateTest) {
            Correspon correspon = createCorresponUpdate23();
            try {
                service.validate(correspon);
                fail("例外が発生しませんでした");
            } catch (ServiceAbortException e) {
                assertEquals(
                    ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CORRESPON_STATUS_INVALID,
                    e.getMessageCode());
            }
        }
    }
    /**
     * 更新 項番24.
     */
    @Test
    public void testValidateUpdate24() throws Exception {
        if (executeUpdateTest) {
            Correspon correspon = createCorresponUpdate24();
            try {
                service.validate(correspon);
                fail("例外が発生しませんでした");
            } catch (ServiceAbortException e) {
                assertEquals(
                    ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CORRESPON_STATUS_INVALID,
                    e.getMessageCode());
            }
        }
    }
    /**
     * 更新 項番25.
     */
    @Test
    public void testValidateUpdate25() throws Exception {
        if (executeUpdateTest) {
            Correspon correspon = createCorresponUpdate25();
            try {
                service.validate(correspon);
                fail("例外が発生しませんでした");
            } catch (ServiceAbortException e) {
                assertEquals(
                    ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_WORKFLOW_STATUS_INVALID_FOR_CHECKER,
                    e.getMessageCode());
            }
        }
    }
    /**
     * 更新 項番26.
     */
    @Test
    public void testValidateUpdate26() throws Exception {
        if (executeUpdateTest) {
            Correspon correspon = createCorresponUpdate26();
            try {
                service.validate(correspon);
                fail("例外が発生しませんでした");
            } catch (ServiceAbortException e) {
                assertEquals(
                    ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_WORKFLOW_STATUS_INVALID_FOR_CHECKER,
                    e.getMessageCode());
            }
        }
    }
    /**
     * 更新 項番27.
     */
    @Test
    public void testValidateUpdate27() throws Exception {
        if (executeUpdateTest) {
            Correspon correspon = createCorresponUpdate27();
            try {
                service.validate(correspon);
                fail("例外が発生しませんでした");
            } catch (ServiceAbortException e) {
                assertEquals(
                    ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_WORKFLOW_STATUS_INVALID_FOR_CHECKER,
                    e.getMessageCode());
            }
        }
    }
    /**
     * 更新 項番28.
     */
    @Test
    public void testValidateUpdate28() throws Exception {
        if (executeUpdateTest) {
            Correspon correspon = createCorresponUpdate28();
            try {
                service.validate(correspon);
                fail("例外が発生しませんでした");
            } catch (ServiceAbortException e) {
                assertEquals(
                    ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_WORKFLOW_STATUS_INVALID_FOR_CHECKER,
                    e.getMessageCode());
            }
        }
    }
    /**
     * 更新 項番29.
     */
    @Test
    public void testValidateUpdate29() throws Exception {
        if (executeUpdateTest) {
            Correspon correspon = createCorresponUpdate29();
            try {
                service.validate(correspon);
                fail("例外が発生しませんでした");
            } catch (ServiceAbortException e) {
                assertEquals(
                    ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_WORKFLOW_STATUS_INVALID_FOR_CHECKER,
                    e.getMessageCode());
            }
        }
    }
    /**
     * 更新 項番30.
     */
    @Test
    public void testValidateUpdate30() throws Exception {
        if (executeUpdateTest) {
            Correspon correspon = createCorresponUpdate30();
            try {
                service.validate(correspon);
                fail("例外が発生しませんでした");
            } catch (ServiceAbortException e) {
                assertEquals(
                    ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT,
//                    ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CORRESPON_STATUS_INVALID,
                    e.getMessageCode());
            }
        }
    }
    /**
     * 更新 項番31.
     */
    @Test
    public void testValidateUpdate31() throws Exception {
        if (executeUpdateTest) {
            Correspon correspon = createCorresponUpdate31();
            try {
                service.validate(correspon);
                fail("例外が発生しませんでした");
            } catch (ServiceAbortException e) {
                assertEquals(
                    ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CORRESPON_STATUS_INVALID,
                    e.getMessageCode());
            }
        }
    }
    /**
     * 更新 項番32.
     */
    @Test
    public void testValidateUpdate32() throws Exception {
        if (executeUpdateTest) {
            Correspon correspon = createCorresponUpdate32();
            try {
                service.validate(correspon);
                fail("例外が発生しませんでした");
            } catch (ServiceAbortException e) {
                assertEquals(
                    ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CORRESPON_STATUS_INVALID,
                    e.getMessageCode());
            }
        }
    }
    /**
     * 更新 項番33.
     */
    @Test
    public void testValidateUpdate33() throws Exception {
        if (executeUpdateTest) {
            Correspon correspon = createCorresponUpdate33();
            try {
                service.validate(correspon);
                fail("例外が発生しませんでした");
            } catch (ServiceAbortException e) {
                assertEquals(
                    ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_WORKFLOW_STATUS_INVALID_FOR_APPROVER,
                    e.getMessageCode());
            }
        }
    }
    /**
     * 更新 項番34.
     */
    @Test
    public void testValidateUpdate34() throws Exception {
        if (executeUpdateTest) {
            Correspon correspon = createCorresponUpdate34();
            //  承認フローパターン3の場合はRequest for Checkでも
            //  Approverは更新可能でなければならない
            service.validate(correspon);
        }
    }
    /**
     * 更新 項番35.
     */
    @Test
    public void testValidateUpdate35() throws Exception {
        if (executeUpdateTest) {
            Correspon correspon = createCorresponUpdate35();
            try {
                service.validate(correspon);
                fail("例外が発生しませんでした");
            } catch (ServiceAbortException e) {
                assertEquals(
                    ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_WORKFLOW_STATUS_INVALID_FOR_APPROVER,
                    e.getMessageCode());
            }
        }
    }
    /**
     * 更新 項番36.
     */
    @Test
    public void testValidateUpdate36() throws Exception {
        if (executeUpdateTest) {
            Correspon correspon = createCorresponUpdate36();
            try {
                service.validate(correspon);
                fail("例外が発生しませんでした");
            } catch (ServiceAbortException e) {
                assertEquals(
                    ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_WORKFLOW_STATUS_INVALID_FOR_APPROVER,
                    e.getMessageCode());
            }
        }
    }
    /**
     * 更新 項番37.
     */
    @Test
    public void testValidateUpdate37() throws Exception {
        if (executeUpdateTest) {
            Correspon correspon = createCorresponUpdate37();
            try {
                service.validate(correspon);
                fail("例外が発生しませんでした");
            } catch (ServiceAbortException e) {
                assertEquals(
                    ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_WORKFLOW_STATUS_INVALID_FOR_APPROVER,
                    e.getMessageCode());
            }
        }
    }
    /**
     * 更新 項番38.
     */
    @Test
    public void testValidateUpdate38() throws Exception {
        if (executeUpdateTest) {
            Correspon correspon = createCorresponUpdate38();
            try {
                service.validate(correspon);
                fail("例外が発生しませんでした");
            } catch (ServiceAbortException e) {
                assertEquals(
                    ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT,
                    e.getMessageCode());
            }
        }
    }
    /**
     * 更新 項番39.
     */
    @Test
    public void testValidateUpdate39() throws Exception {
        if (executeUpdateTest) {
            Correspon correspon = createCorresponUpdate39();
            //  承認フローパターン3の場合はRequest for Checkでも
            //  Approverは更新可能でなければならない
            service.validate(correspon);
        }
    }
    /**
     * 更新 項番40.
     */
    @Test
    public void testValidateUpdate40() throws Exception {
        if (executeUpdateTest) {
            Correspon correspon = createCorresponUpdate40();
            try {
                service.validate(correspon);
                fail("例外が発生しませんでした");
            } catch (ServiceAbortException e) {
                assertEquals(
                    ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CORRESPON_STATUS_INVALID,
                    e.getMessageCode());
            }
        }
    }
    /**
     * 更新 項番41.
     */
    @Test
    public void testValidateUpdate41() throws Exception {
        if (executeUpdateTest) {
            Correspon correspon = createCorresponUpdate41();
            try {
                service.validate(correspon);
                fail("例外が発生しませんでした");
            } catch (ServiceAbortException e) {
                assertEquals(
                    ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT,
                    e.getMessageCode());
            }
        }
    }
    ////////////////////////////////////////////////////////////////
    /**
     * 更新 項番1に必要なデータを作成する.
     *
     * @return
     */
    private Correspon createCorresponUpdate01() {
        Correspon c = createCorresponNew01();
        Correspon oldCorrespon = createCorresponNew01();

        // 文書状態を取り消し
        c.setCorresponStatus(null);
        // 返信を取り消し
        c.setParentCorresponId(null);
        MockAbstractDao.RET_FIND_BY_ID.put(2L, null);

        // Field①
        c.setCustomField1Id(10001L);
        c.setCustomField2Id(10002L);
        c.setCustomField3Id(10003L);
        c.setCustomField4Id(10004L);
        c.setCustomField5Id(10005L);
        c.setCustomField6Id(10006L);
        c.setCustomField7Id(10007L);
        c.setCustomField8Id(10008L);
        c.setCustomField9Id(10009L);
        c.setCustomField10Id(10010L);
        c.setCustomField1Value("CustomField1Value");
        c.setCustomField2Value("CustomField2Value");
        c.setCustomField3Value("CustomField3Value");
        c.setCustomField4Value("CustomField4Value");
        c.setCustomField5Value("CustomField5Value");
        c.setCustomField6Value("CustomField6Value");
        c.setCustomField7Value("CustomField7Value");
        c.setCustomField8Value("CustomField8Value");
        c.setCustomField9Value("CustomField9Value");
        c.setCustomField10Value("CustomField10Value");

        CustomField f;
        f = new CustomField();
        f.setId(1L);
        f.setProjectCustomFieldId(10001L);
        f.setLabel("label1");
        MockCustomFieldDao.RET_FIND_BY_PROJECT_ID.add(f);
        f = new CustomField();
        f.setId(2L);
        f.setProjectCustomFieldId(10002L);
        f.setLabel("label2");
        MockCustomFieldDao.RET_FIND_BY_PROJECT_ID.add(f);
        f = new CustomField();
        f.setId(3L);
        f.setProjectCustomFieldId(10003L);
        f.setLabel("label3");
        MockCustomFieldDao.RET_FIND_BY_PROJECT_ID.add(f);
        f = new CustomField();
        f.setId(4L);
        f.setProjectCustomFieldId(10004L);
        f.setLabel("label4");
        MockCustomFieldDao.RET_FIND_BY_PROJECT_ID.add(f);
        f = new CustomField();
        f.setId(5L);
        f.setProjectCustomFieldId(10005L);
        f.setLabel("label5");
        MockCustomFieldDao.RET_FIND_BY_PROJECT_ID.add(f);
        f = new CustomField();
        f.setId(6L);
        f.setProjectCustomFieldId(10006L);
        f.setLabel("label6");
        MockCustomFieldDao.RET_FIND_BY_PROJECT_ID.add(f);
        f = new CustomField();
        f.setId(7L);
        f.setProjectCustomFieldId(10007L);
        f.setLabel("label7");
        MockCustomFieldDao.RET_FIND_BY_PROJECT_ID.add(f);
        f = new CustomField();
        f.setId(8L);
        f.setProjectCustomFieldId(10008L);
        f.setLabel("label8");
        MockCustomFieldDao.RET_FIND_BY_PROJECT_ID.add(f);
        f = new CustomField();
        f.setId(9L);
        f.setProjectCustomFieldId(10009L);
        f.setLabel("label9");
        MockCustomFieldDao.RET_FIND_BY_PROJECT_ID.add(f);
        f = new CustomField();
        f.setId(10L);
        f.setProjectCustomFieldId(10010L);
        f.setLabel("label10");
        MockCustomFieldDao.RET_FIND_BY_PROJECT_ID.add(f);

//        MockProjectCustomFieldDao.RET.put(1L,10001L);
//        MockProjectCustomFieldDao.RET.put(2L,10002L);
//        MockProjectCustomFieldDao.RET.put(3L,10003L);
//        MockProjectCustomFieldDao.RET.put(4L,10004L);
//        MockProjectCustomFieldDao.RET.put(5L,10005L);
//        MockProjectCustomFieldDao.RET.put(6L,10006L);
//        MockProjectCustomFieldDao.RET.put(7L,10007L);
//        MockProjectCustomFieldDao.RET.put(8L,10008L);
//        MockProjectCustomFieldDao.RET.put(9L,10009L);
//        MockProjectCustomFieldDao.RET.put(10L,10010L);

        CorresponCustomField ccf = null;
        ccf = new CorresponCustomField();
        ccf.setProjectCustomFieldId(20001L);
        MockAbstractDao.RET_FIND_BY_ID.put(c.getCustomField1Id(), ccf);
        ccf = new CorresponCustomField();
        ccf.setProjectCustomFieldId(20002L);
        MockAbstractDao.RET_FIND_BY_ID.put(c.getCustomField2Id(), ccf);
        ccf = new CorresponCustomField();
        ccf.setProjectCustomFieldId(20003L);
        MockAbstractDao.RET_FIND_BY_ID.put(c.getCustomField3Id(), ccf);
        ccf = new CorresponCustomField();
        ccf.setProjectCustomFieldId(20004L);
        MockAbstractDao.RET_FIND_BY_ID.put(c.getCustomField4Id(), ccf);
        ccf = new CorresponCustomField();
        ccf.setProjectCustomFieldId(20005L);
        MockAbstractDao.RET_FIND_BY_ID.put(c.getCustomField5Id(), ccf);
        ccf = new CorresponCustomField();
        ccf.setProjectCustomFieldId(20006L);
        MockAbstractDao.RET_FIND_BY_ID.put(c.getCustomField6Id(), ccf);
        ccf = new CorresponCustomField();
        ccf.setProjectCustomFieldId(20007L);
        MockAbstractDao.RET_FIND_BY_ID.put(c.getCustomField7Id(), ccf);
        ccf = new CorresponCustomField();
        ccf.setProjectCustomFieldId(20008L);
        MockAbstractDao.RET_FIND_BY_ID.put(c.getCustomField8Id(), ccf);
        ccf = new CorresponCustomField();
        ccf.setProjectCustomFieldId(20009L);
        MockAbstractDao.RET_FIND_BY_ID.put(c.getCustomField9Id(), ccf);
        ccf = new CorresponCustomField();
        ccf.setProjectCustomFieldId(20010L);
        MockAbstractDao.RET_FIND_BY_ID.put(c.getCustomField10Id(), ccf);

        // Field②③
        ProjectCustomField pcf = null;
        pcf = new ProjectCustomField();
        pcf.setProjectId(c.getProjectId());
        MockAbstractDao.RET_FIND_BY_ID.put(20001L, pcf);
        MockAbstractDao.RET_FIND_BY_ID.put(20002L, pcf);
        MockAbstractDao.RET_FIND_BY_ID.put(20003L, pcf);
        MockAbstractDao.RET_FIND_BY_ID.put(20004L, pcf);
        MockAbstractDao.RET_FIND_BY_ID.put(20005L, pcf);
        MockAbstractDao.RET_FIND_BY_ID.put(20006L, pcf);
        MockAbstractDao.RET_FIND_BY_ID.put(20007L, pcf);
        MockAbstractDao.RET_FIND_BY_ID.put(20008L, pcf);
        MockAbstractDao.RET_FIND_BY_ID.put(20009L, pcf);
        MockAbstractDao.RET_FIND_BY_ID.put(20010L, pcf);


        // 更新の際①
        c.setId(1024L);
        MockAbstractDao.RET_FIND_BY_ID.put(1024L, oldCorrespon);

        // 更新の際②
        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN= false;
        MockAbstractService.IS_GROUP_ADMIN = false;
        c.setWorkflowStatus(WorkflowStatus.DRAFT);

        // 文書状態①
        // 文書状態②
        oldCorrespon.setCorresponStatus(CorresponStatus.OPEN);
        oldCorrespon.setWorkflowStatus(WorkflowStatus.DRAFT);
        MockAbstractDao.RET_FIND_BY_ID.put(1024L, oldCorrespon);
        c.setCorresponStatus(CorresponStatus.OPEN);

        // プロジェクトチェック
        c.setProjectId("PJtest01");
        MockAbstractService.CURRENT_PROJECT_ID = "PJtest01";

        // 承認状態チェック
        c.setProjectId("PJtest01");
        MockAbstractService.CURRENT_PROJECT_ID = "PJtest01";
        c.setWorkflowStatus(WorkflowStatus.DRAFT);
        oldCorrespon.setWorkflowStatus(WorkflowStatus.DRAFT);
        MockAbstractDao.RET_FIND_BY_ID.put(1024L, oldCorrespon);

        // 文書更新チェック(Prepare)
        MockAbstractService.IS_PREPARER = true;
        User createdBy = new User();
        c.setCreatedBy(createdBy);

        List<ProjectUser> puList = new ArrayList<ProjectUser>();
        User user = new User();
        user.setEmpNo("EMP01");
        ProjectUser pu = new ProjectUser();
        pu.setUser(user);
        puList.add(pu);
        user = new User();
        user.setEmpNo("EMP02");
        pu = new ProjectUser();
        pu.setUser(user);
        puList.add(pu);
        user = new User();
//        user.setEmpNo("testuser001");
//        pu = new ProjectUser();
//        pu.setUser(user);
//        puList.add(pu);
        MockUserDao.RET_FIND_PROJECT_USER = puList;

        return c;
    }

    /**
     * 更新 項番2に必要なデータを作成する.
     *
     * @return
     */
    private Correspon createCorresponUpdate02() {
        Correspon c = createCorresponUpdate01();
        MockAbstractService.IS_PREPARER = false;

        // 文書更新チェック(Checker)
        MockAbstractService.IS_CHEKER = true;

        // Checker①
        List<Workflow> wfList = new ArrayList<Workflow>();
        Workflow wf = new Workflow();
        wf.setUser(MockAbstractService.CURRENT_USER);
        wf.setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_CHECK);
        wf.setWorkflowType(WorkflowType.CHECKER);
        wfList.add(wf);
        wf = new Workflow();
        wf.setUser(MockAbstractService.CURRENT_USER);
        wf.setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_CHECK);
        wf.setWorkflowType(WorkflowType.APPROVER);
        wfList.add(wf);
        wf = new Workflow();
        User user = new User();
        user.setEmpNo("otheruser");
        wf.setUser(user);
        wf.setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_CHECK);
        wf.setWorkflowType(WorkflowType.CHECKER);
        wfList.add(wf);
        c.setWorkflows(wfList);

        // Checker②
        // (承認状態チェック)
        c.setWorkflowStatus(WorkflowStatus.REQUEST_FOR_CHECK);
        Correspon oldCorrespon = new Correspon();
        oldCorrespon.setWorkflowStatus(WorkflowStatus.REQUEST_FOR_CHECK);
        oldCorrespon.setCorresponStatus(CorresponStatus.OPEN);
        MockAbstractDao.RET_FIND_BY_ID.put(1024L, oldCorrespon);

        return c;
    }

    /**
     * 更新 項番3に必要なデータを作成する.
     *
     * @return
     */
    private Correspon createCorresponUpdate03() {
        Correspon c = createCorresponUpdate01();
        MockAbstractService.IS_PREPARER = false;

        // 文書更新チェック(Approver)
        MockAbstractService.IS_APPROVER = true;

        // Approver①
        List<Workflow> wfList = new ArrayList<Workflow>();
        User user = new User();
        user.setEmpNo("testuser001");
        Workflow wf = new Workflow();
        MockAbstractService.CURRENT_USER = user;
        wf.setUser(user);
        wf.setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_APPROVAL);
        wf.setWorkflowType(WorkflowType.APPROVER);
        wfList.add(wf);
        wf = new Workflow();
        wf.setUser(user);
        wf.setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_APPROVAL);
        wf.setWorkflowType(WorkflowType.CHECKER);
        wfList.add(wf);
        wf = new Workflow();
        user = new User();
        user.setEmpNo("otheruser");
        wf.setUser(user);
        wf.setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_APPROVAL);
        wf.setWorkflowType(WorkflowType.APPROVER);
        wfList.add(wf);

        c.setWorkflows(wfList);

        // Approver②
        // (承認状態チェック)
        c.setWorkflowStatus(WorkflowStatus.UNDER_CONSIDERATION);
        Correspon oldCorrespon = new Correspon();
        oldCorrespon.setWorkflowStatus(WorkflowStatus.UNDER_CONSIDERATION);
        oldCorrespon.setCorresponStatus(CorresponStatus.OPEN);
        MockAbstractDao.RET_FIND_BY_ID.put(1024L, oldCorrespon);

        return c;
    }

    /**
     * 更新 項番4に必要なデータを作成する.
     *
     * @return
     */
    private Correspon createCorresponUpdate04() {
        Correspon c = createCorresponUpdate01();
        MockAbstractService.IS_PREPARER = true;

        MockAbstractService.IS_SYSTEM_ADMIN = true;
        c.setWorkflowStatus(WorkflowStatus.DRAFT);

        return c;
    }

    /**
     * 更新 項番5に必要なデータを作成する.
     *
     * @return
     */
    private Correspon createCorresponUpdate05() {
        Correspon c = createCorresponUpdate01();
        MockAbstractService.IS_PREPARER = false;
        MockAbstractService.IS_PROJECT_ADMIN = true;

        // (承認状態チェック)
        c.setWorkflowStatus(WorkflowStatus.UNDER_CONSIDERATION);
        Correspon oldCorrespon = new Correspon();
        oldCorrespon.setWorkflowStatus(WorkflowStatus.UNDER_CONSIDERATION);
        oldCorrespon.setCorresponStatus(CorresponStatus.OPEN);
        MockAbstractDao.RET_FIND_BY_ID.put(1024L, oldCorrespon);

        return c;
    }


    /**
     * 更新 項番6に必要なデータを作成する.
     *
     * @return
     */
    private Correspon createCorresponUpdate06() {
        Correspon c = createCorresponUpdate01();
        MockAbstractService.IS_PREPARER = false;
        MockAbstractService.IS_CHEKER = true;
        MockAbstractService.IS_GROUP_ADMIN = true;

        // (承認状態チェック)
        c.setWorkflowStatus(WorkflowStatus.UNDER_CONSIDERATION);
        Correspon oldCorrespon = new Correspon();
        oldCorrespon.setWorkflowStatus(WorkflowStatus.UNDER_CONSIDERATION);
        oldCorrespon.setCorresponStatus(CorresponStatus.OPEN);
        MockAbstractDao.RET_FIND_BY_ID.put(1024L, oldCorrespon);

        return c;
    }

    /**
     * 更新 項番7に必要なデータを作成する.
     *
     * @return
     */
    private Correspon createCorresponUpdate07() {
        Correspon c = createCorresponUpdate04();
        c.setWorkflowStatus(WorkflowStatus.ISSUED);

        Correspon oldCorrespon = new Correspon();
        oldCorrespon.setWorkflowStatus(c.getWorkflowStatus());
        oldCorrespon.setCorresponStatus(CorresponStatus.OPEN);
        MockAbstractDao.RET_FIND_BY_ID.put(1024L, oldCorrespon);

        return c;
    }

    /**
     * 更新 項番8に必要なデータを作成する.
     *
     * @return
     */
    private Correspon createCorresponUpdate08() {
        Correspon c = createCorresponUpdate05();
        c.setWorkflowStatus(WorkflowStatus.ISSUED);
        Correspon oldCorrespon = new Correspon();
        oldCorrespon.setWorkflowStatus(c.getWorkflowStatus());
        oldCorrespon.setCorresponStatus(CorresponStatus.OPEN);
        MockAbstractDao.RET_FIND_BY_ID.put(1024L, oldCorrespon);
        return c;
    }


    /**
     * 更新 項番9に必要なデータを作成する.
     *
     * @return
     */
    private Correspon createCorresponUpdate09() {
        Correspon c = createCorresponUpdate06();
        c.setWorkflowStatus(WorkflowStatus.ISSUED);
        Correspon oldCorrespon = new Correspon();
        oldCorrespon.setWorkflowStatus(c.getWorkflowStatus());
        oldCorrespon.setCorresponStatus(CorresponStatus.OPEN);
        MockAbstractDao.RET_FIND_BY_ID.put(1024L, oldCorrespon);
        return c;
    }

    /**
     * 更新 項番10に必要なデータを作成する.
     *
     * @return
     */
    private Correspon createCorresponUpdate10() {
        Correspon c = createCorresponUpdate01();
        c.setWorkflowStatus(WorkflowStatus.ISSUED);
        MockAbstractService.IS_SYSTEM_ADMIN = true;

        Correspon oldCorrespon = new Correspon();
        oldCorrespon.setCorresponStatus(CorresponStatus.OPEN);
        oldCorrespon.setWorkflowStatus(WorkflowStatus.ISSUED);
        MockAbstractDao.RET_FIND_BY_ID.put(1024L, oldCorrespon);
        c.setCorresponStatus(CorresponStatus.CANCELED);
        return c;
    }

    /**
     * 更新 項番11に必要なデータを作成する.
     *
     * @return
     */
    private Correspon createCorresponUpdate11() {
        Correspon c = createCorresponUpdate01();
        // 承認状態チェック
        c.setWorkflowStatus(WorkflowStatus.DENIED);
        Correspon oldCorrespon = new Correspon();
        oldCorrespon.setCorresponStatus(CorresponStatus.OPEN);
        oldCorrespon.setWorkflowStatus(WorkflowStatus.DENIED);
        MockAbstractDao.RET_FIND_BY_ID.put(1024L, oldCorrespon);
        return c;
    }

    /**
     * 更新 項番12に必要なデータを作成する.
     *
     * @return
     */
    private Correspon createCorresponUpdate12() {
        Correspon c = createCorresponUpdate02();
        // Checker①
        List<Workflow> wfList = new ArrayList<Workflow>();
        Workflow wf = new Workflow();
        wf.setUser(MockAbstractService.CURRENT_USER);
        wf.setWorkflowProcessStatus(WorkflowProcessStatus.UNDER_CONSIDERATION);
        wf.setWorkflowType(WorkflowType.CHECKER);
        wfList.add(wf);
        c.setWorkflows(wfList);

        // Checker②
        // (承認状態チェック)
        c.setWorkflowStatus(WorkflowStatus.UNDER_CONSIDERATION);
        Correspon oldCorrespon = new Correspon();
        oldCorrespon.setWorkflowStatus(WorkflowStatus.UNDER_CONSIDERATION);
        oldCorrespon.setCorresponStatus(CorresponStatus.OPEN);
        MockAbstractDao.RET_FIND_BY_ID.put(1024L, oldCorrespon);

        return c;
    }
    /**
     * 更新 項番13に必要なデータを作成する.
     *
     * @return
     */
    private Correspon createCorresponUpdate13() {
        Correspon c = createCorresponUpdate03();
        // Checker①
        List<Workflow> wfList = new ArrayList<Workflow>();
        User user = new User();
        user.setEmpNo("testuser001");
        Workflow wf = new Workflow();
        MockAbstractService.CURRENT_USER = user;
        wf.setUser(user);
        wf.setWorkflowProcessStatus(WorkflowProcessStatus.UNDER_CONSIDERATION);
        wf.setWorkflowType(WorkflowType.APPROVER);
        wfList.add(wf);
        c.setWorkflows(wfList);

        // Checker②
        // (承認状態チェック)
        c.setWorkflowStatus(WorkflowStatus.REQUEST_FOR_APPROVAL);
        Correspon oldCorrespon = new Correspon();
        oldCorrespon.setWorkflowStatus(WorkflowStatus.REQUEST_FOR_APPROVAL);
        oldCorrespon.setCorresponStatus(CorresponStatus.OPEN);
        MockAbstractDao.RET_FIND_BY_ID.put(1024L, oldCorrespon);

        return c;
    }
    /**
     * 更新 項番14に必要なデータを作成する.
     *
     * @return
     */
    private Correspon createCorresponUpdate14() {
        Correspon c = createCorresponUpdate01();
        MockAbstractDao.RET_FIND_BY_ID.put(1024L, null);
        return c;
    }
    /**
     * 更新 項番15に必要なデータを作成する.
     *
     * @return
     */
    private Correspon createCorresponUpdate15() {
        Correspon c = createCorresponUpdate01();
        c.setWorkflowStatus(WorkflowStatus.ISSUED);
        return c;
    }

    /**
     * 更新 項番16に必要なデータを作成する.
     *
     * @return
     */
    private Correspon createCorresponUpdate16() {
        Correspon c = createCorresponUpdate01();
        Correspon oldCorrespon = new Correspon();
        oldCorrespon.setCorresponStatus(CorresponStatus.CANCELED);
        oldCorrespon.setWorkflowStatus(WorkflowStatus.DRAFT);
        MockAbstractDao.RET_FIND_BY_ID.put(1024L, oldCorrespon);
        return c;
    }

    /**
     * 更新 項番17に必要なデータを作成する.
     *
     * @return
     */
    private Correspon createCorresponUpdate17() {
        Correspon c = createCorresponUpdate01();
        Correspon oldCorrespon = new Correspon();
        oldCorrespon.setCorresponStatus(CorresponStatus.OPEN);
        oldCorrespon.setWorkflowStatus(WorkflowStatus.DRAFT);
        MockAbstractDao.RET_FIND_BY_ID.put(1024L, oldCorrespon);
        c.setCorresponStatus(CorresponStatus.CANCELED);
        return c;
    }

    /**
     * 更新 項番18に必要なデータを作成する.
     *
     * @return
     */
    private Correspon createCorresponUpdate18() {
        Correspon c = createCorresponUpdate01();
        c.setProjectId("PJtest01");
        MockAbstractService.CURRENT_PROJECT_ID = "PJtest01diff";
        MockAbstractService.IS_SYSTEM_ADMIN = true;
        return c;
    }

    /**
     * 更新 項番19に必要なデータを作成する.
     *
     * @return
     */
    private Correspon createCorresponUpdate19() {
        Correspon c = createCorresponUpdate01();
        c.setWorkflowStatus(WorkflowStatus.REQUEST_FOR_CHECK);
        return c;
    }

    /**
     * 更新 項番20に必要なデータを作成する.
     *
     * @return
     */
    private Correspon createCorresponUpdate20() {
        Correspon c = createCorresponUpdate05();
        c.setWorkflowStatus(WorkflowStatus.DRAFT);

        Correspon oldCorrespon = new Correspon();
        oldCorrespon.setWorkflowStatus(WorkflowStatus.DRAFT);
        MockAbstractDao.RET_FIND_BY_ID.put(1024L, oldCorrespon);

        return c;
    }

    /**
     * 更新 項番21に必要なデータを作成する.
     *
     * @return
     */
    private Correspon createCorresponUpdate21() {
        Correspon c = createCorresponUpdate06();
        c.setWorkflowStatus(WorkflowStatus.DRAFT);

        Correspon oldCorrespon = new Correspon();
        oldCorrespon.setWorkflowStatus(WorkflowStatus.DRAFT);
        MockAbstractDao.RET_FIND_BY_ID.put(1024L, oldCorrespon);

        return c;
    }

    /**
     * 更新 項番22に必要なデータを作成する.
     *
     * @return
     */
    private Correspon createCorresponUpdate22() {
        Correspon c = createCorresponUpdate01();
        c.setWorkflowStatus(WorkflowStatus.REQUEST_FOR_CHECK);

        Correspon oldCorrespon = new Correspon();
        oldCorrespon.setWorkflowStatus(WorkflowStatus.REQUEST_FOR_CHECK);
        MockAbstractDao.RET_FIND_BY_ID.put(1024L, oldCorrespon);

        return c;
    }
    /**
     * 更新 項番23に必要なデータを作成する.
     *
     * @return
     */
    private Correspon createCorresponUpdate23() {
        Correspon c = createCorresponUpdate01();
        c.setWorkflowStatus(WorkflowStatus.UNDER_CONSIDERATION);

        Correspon oldCorrespon = new Correspon();
        oldCorrespon.setWorkflowStatus(WorkflowStatus.UNDER_CONSIDERATION);
        MockAbstractDao.RET_FIND_BY_ID.put(1024L, oldCorrespon);

        return c;
    }
    /**
     * 更新 項番24に必要なデータを作成する.
     *
     * @return
     */
    private Correspon createCorresponUpdate24() {
        Correspon c = createCorresponUpdate01();
        c.setWorkflowStatus(WorkflowStatus.REQUEST_FOR_APPROVAL);

        Correspon oldCorrespon = new Correspon();
        oldCorrespon.setWorkflowStatus(WorkflowStatus.REQUEST_FOR_APPROVAL);
        MockAbstractDao.RET_FIND_BY_ID.put(1024L, oldCorrespon);

        return c;
    }
    /**
     * 更新 項番25に必要なデータを作成する.
     *
     * @return
     */
    private Correspon createCorresponUpdate25() {
        Correspon c = createCorresponUpdate02();
        // Checker①
        List<Workflow> wfList = new ArrayList<Workflow>();
        Workflow wf = new Workflow();
        wf.setUser(MockAbstractService.CURRENT_USER);
        wf.setWorkflowProcessStatus(WorkflowProcessStatus.NONE);
        wf.setWorkflowType(WorkflowType.CHECKER);
        wfList.add(wf);
        c.setWorkflows(wfList);
        return c;
    }

    /**
     * 更新 項番26に必要なデータを作成する.
     *
     * @return
     */
    private Correspon createCorresponUpdate26() {
        Correspon c = createCorresponUpdate02();
        // Checker①
        List<Workflow> wfList = new ArrayList<Workflow>();
        Workflow wf = new Workflow();
        wf.setUser(MockAbstractService.CURRENT_USER);
        wf.setWorkflowProcessStatus(WorkflowProcessStatus.CHECKED);
        wf.setWorkflowType(WorkflowType.CHECKER);
        wfList.add(wf);
        c.setWorkflows(wfList);
        return c;
    }

    /**
     * 更新 項番27に必要なデータを作成する.
     *
     * @return
     */
    private Correspon createCorresponUpdate27() {
        Correspon c = createCorresponUpdate02();
        // Checker①
        List<Workflow> wfList = new ArrayList<Workflow>();
        Workflow wf = new Workflow();
        wf.setUser(MockAbstractService.CURRENT_USER);
        //TODO テストデータ不備。CheckerでRequest for Approvalはあり得ない
        wf.setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_APPROVAL);
        wf.setWorkflowType(WorkflowType.CHECKER);
        wfList.add(wf);
        c.setWorkflows(wfList);
        return c;
    }

    /**
     * 更新 項番28に必要なデータを作成する.
     *
     * @return
     */
    private Correspon createCorresponUpdate28() {
        Correspon c = createCorresponUpdate02();
        // Checker①
        List<Workflow> wfList = new ArrayList<Workflow>();
        Workflow wf = new Workflow();
        wf.setUser(MockAbstractService.CURRENT_USER);
        wf.setWorkflowProcessStatus(WorkflowProcessStatus.DENIED);
        wf.setWorkflowType(WorkflowType.CHECKER);
        wfList.add(wf);
        c.setWorkflows(wfList);
        return c;
    }

    /**
     * 更新 項番29に必要なデータを作成する.
     *
     * @return
     */
    private Correspon createCorresponUpdate29() {
        Correspon c = createCorresponUpdate02();
        // Checker①
        List<Workflow> wfList = new ArrayList<Workflow>();
        Workflow wf = new Workflow();
        wf.setUser(MockAbstractService.CURRENT_USER);
        wf.setWorkflowProcessStatus(WorkflowProcessStatus.APPROVED);
        wf.setWorkflowType(WorkflowType.CHECKER);
        wfList.add(wf);
        c.setWorkflows(wfList);
        return c;
    }

    /**
     * 更新 項番30に必要なデータを作成する.
     *
     * @return
     */
    private Correspon createCorresponUpdate30() {
        Correspon c = createCorresponUpdate02();

        c.setWorkflowStatus(WorkflowStatus.DRAFT);
        Correspon oldCorrespon = new Correspon();
        oldCorrespon.setWorkflowStatus(WorkflowStatus.DRAFT);
        oldCorrespon.setCorresponStatus(CorresponStatus.OPEN);
        MockAbstractDao.RET_FIND_BY_ID.put(1024L, oldCorrespon);
        return c;
    }
    /**
     * 更新 項番31に必要なデータを作成する.
     *
     * @return
     */
    private Correspon createCorresponUpdate31() {
        Correspon c = createCorresponUpdate02();

        c.setWorkflowStatus(WorkflowStatus.REQUEST_FOR_APPROVAL);
        Correspon oldCorrespon = new Correspon();
        oldCorrespon.setWorkflowStatus(WorkflowStatus.REQUEST_FOR_APPROVAL);
        oldCorrespon.setCorresponStatus(CorresponStatus.OPEN);
        MockAbstractDao.RET_FIND_BY_ID.put(1024L, oldCorrespon);
        return c;
    }
    /**
     * 更新 項番32に必要なデータを作成する.
     *
     * @return
     */
    private Correspon createCorresponUpdate32() {
        Correspon c = createCorresponUpdate02();

        c.setWorkflowStatus(WorkflowStatus.DENIED);
        Correspon oldCorrespon = new Correspon();
        oldCorrespon.setWorkflowStatus(WorkflowStatus.DENIED);
        oldCorrespon.setCorresponStatus(CorresponStatus.OPEN);
        MockAbstractDao.RET_FIND_BY_ID.put(1024L, oldCorrespon);
        return c;
    }

    /**
     * 更新 項番33に必要なデータを作成する.
     *
     * @return
     */
    private Correspon createCorresponUpdate33() {
        Correspon c = createCorresponUpdate03();

        List<Workflow> wfList = new ArrayList<Workflow>();
        Workflow wf = new Workflow();
        wf.setUser(MockAbstractService.CURRENT_USER);
        wf.setWorkflowProcessStatus(WorkflowProcessStatus.NONE);
        wf.setWorkflowType(WorkflowType.APPROVER);
        wfList.add(wf);
        c.setWorkflows(wfList);
        return c;
    }

    /**
     * 更新 項番34に必要なデータを作成する.
     *
     * @return
     */
    private Correspon createCorresponUpdate34() {
        Correspon c = createCorresponUpdate03();

        List<Workflow> wfList = new ArrayList<Workflow>();
        Workflow wf = new Workflow();
        wf.setUser(MockAbstractService.CURRENT_USER);
        wf.setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_APPROVAL);
        wf.setWorkflowType(WorkflowType.APPROVER);
        wfList.add(wf);
        c.setWorkflows(wfList);
        return c;
    }

    /**
     * 更新 項番35に必要なデータを作成する.
     *
     * @return
     */
    private Correspon createCorresponUpdate35() {
        Correspon c = createCorresponUpdate03();

        List<Workflow> wfList = new ArrayList<Workflow>();
        Workflow wf = new Workflow();
        wf.setUser(MockAbstractService.CURRENT_USER);
        wf.setWorkflowProcessStatus(WorkflowProcessStatus.APPROVED);
        wf.setWorkflowType(WorkflowType.APPROVER);
        wfList.add(wf);
        c.setWorkflows(wfList);
        return c;
    }

    /**
     * 更新 項番36に必要なデータを作成する.
     *
     * @return
     */
    private Correspon createCorresponUpdate36() {
        Correspon c = createCorresponUpdate03();

        List<Workflow> wfList = new ArrayList<Workflow>();
        Workflow wf = new Workflow();
        wf.setUser(MockAbstractService.CURRENT_USER);
        wf.setWorkflowProcessStatus(WorkflowProcessStatus.DENIED);
        wf.setWorkflowType(WorkflowType.APPROVER);
        wfList.add(wf);
        c.setWorkflows(wfList);
        return c;
    }

    /**
     * 更新 項番37に必要なデータを作成する.
     *
     * @return
     */
    private Correspon createCorresponUpdate37() {
        Correspon c = createCorresponUpdate03();

        List<Workflow> wfList = new ArrayList<Workflow>();
        Workflow wf = new Workflow();
        wf.setUser(MockAbstractService.CURRENT_USER);
        wf.setWorkflowProcessStatus(WorkflowProcessStatus.APPROVED);
        wf.setWorkflowType(WorkflowType.APPROVER);
        wfList.add(wf);
        c.setWorkflows(wfList);
        return c;
    }

    /**
     * 更新 項番38に必要なデータを作成する.
     *
     * @return
     */
    private Correspon createCorresponUpdate38() {
        Correspon c = createCorresponUpdate03();
        c.setWorkflowStatus(WorkflowStatus.DRAFT);
        Correspon oldCorrespon = new Correspon();
        oldCorrespon.setWorkflowStatus(WorkflowStatus.DRAFT);
        oldCorrespon.setCorresponStatus(CorresponStatus.OPEN);
        MockAbstractDao.RET_FIND_BY_ID.put(1024L, oldCorrespon);
        return c;
    }

    /**
     * 更新 項番39に必要なデータを作成する.
     *
     * @return
     */
    private Correspon createCorresponUpdate39() {
        Correspon c = createCorresponUpdate03();
        c.setWorkflowStatus(WorkflowStatus.REQUEST_FOR_CHECK);
        Correspon oldCorrespon = new Correspon();
        oldCorrespon.setWorkflowStatus(WorkflowStatus.REQUEST_FOR_CHECK);
        oldCorrespon.setCorresponStatus(CorresponStatus.OPEN);
        MockAbstractDao.RET_FIND_BY_ID.put(1024L, oldCorrespon);
        return c;
    }

    /**
     * 更新 項番40に必要なデータを作成する.
     *
     * @return
     */
    private Correspon createCorresponUpdate40() {
        Correspon c = createCorresponUpdate03();
        c.setWorkflowStatus(WorkflowStatus.DENIED);
        Correspon oldCorrespon = new Correspon();
        oldCorrespon.setWorkflowStatus(WorkflowStatus.DENIED);
        oldCorrespon.setCorresponStatus(CorresponStatus.OPEN);
        MockAbstractDao.RET_FIND_BY_ID.put(1024L, oldCorrespon);
        return c;
    }

    /**
     * 更新 項番41に必要なデータを作成する.
     *
     * @return
     */
    private Correspon createCorresponUpdate41() {
        Correspon c = createCorresponUpdate01();
        MockAbstractService.IS_PREPARER = false;
        return c;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * AbstractServiceのMockクラス
     * @author opentone
     */
    public static class MockAbstractService extends MockUp<AbstractService> {
        public static User CURRENT_USER;
        public static String CURRENT_PROJECT_ID;
        public static ServiceAbortException VALIDATE_PROJECT_ID = null;
        public static boolean IS_SYSTEM_ADMIN = false;
        public static boolean IS_PROJECT_ADMIN = false;
        public static boolean IS_GROUP_ADMIN = false;
        public static boolean IS_PREPARER = false;
        public static boolean IS_CHEKER = false;
        public static boolean IS_APPROVER = false;

        @Mock
        public User getCurrentUser() {
            return CURRENT_USER;
        }
        @Mock
        public String getCurrentProjectId() {
            return CURRENT_PROJECT_ID;
        }
        @Mock
        public void validateProjectId(String projectId) throws ServiceAbortException {
            if (VALIDATE_PROJECT_ID != null){
                throw VALIDATE_PROJECT_ID;
            }
        }

        @Mock
        public boolean isSystemAdmin(User user){
            return IS_SYSTEM_ADMIN;
        }
        @Mock
        public boolean isProjectAdmin(User user, String projectId) {
            return IS_PROJECT_ADMIN;
        }
        @Mock
        public boolean isGroupAdmin(User user, Long corresponGroupId) {
            return IS_GROUP_ADMIN;
        }

        @Mock
        public boolean isPreparer(String corresponEmpNo) {
            return IS_PREPARER;
        }
        @Mock
        public boolean isChecker(Correspon correspon) {
            return IS_CHEKER;
        }
        @Mock
        public boolean isApprover(Correspon correspon) {
            return IS_APPROVER;
        }
    }

    /**
     * AbstractDaoのMockクラス
     * @author opentone
     */
    public static class MockAbstractDao<T extends Entity> {
        static Map<Long, Object> RET_FIND_BY_ID = new HashMap<Long, Object>();

        @SuppressWarnings("unchecked")
        public T findById(Long id) throws RecordNotFoundException {
            if (RET_FIND_BY_ID.get(id) == null ) {
                throw new RecordNotFoundException(id + "");
            }

            return (T) RET_FIND_BY_ID.get(id);
        }
    }

    /**
     * AddressUserDaoのMockクラス
     * @author opentone
     */
    public static class MockAddressUserDao {
        static List<AddressUser> RET_FIND_BY_ADDRESS_CORRESPON_GROUP_ID;

        public List<AddressUser> findByAddressCorresponGroupId(Long addressCorresponGroupId) {
            return RET_FIND_BY_ADDRESS_CORRESPON_GROUP_ID;
        }

    }

    /**
     * UserDaoのMockクラス
     * @author opentone
     */
    public static class MockUserDao {
        static User RET_FIND_BY_EMP_NO = null;
        static List<String> RET_FIND_EMP_NO = null;
        static List<ProjectUser> RET_FIND_PROJECT_USER = null;

        public User findByEmpNo(String empNo)
        throws RecordNotFoundException {
            if (RET_FIND_BY_EMP_NO == null) {
                throw new RecordNotFoundException();
            }
            return RET_FIND_BY_EMP_NO;
        }

        public List<String> findEmpNo() {
            return RET_FIND_EMP_NO;
        }

        public List<ProjectUser> findProjectUser(SearchUserCondition condition) {
            return RET_FIND_PROJECT_USER;
        }
    }

    /**
     * ProjectDaoのMockクラス
     * @author opentone
     */
    public static class MockProjectDao {
        static List<Project> RET_FIND_BY_EMP_NO = new ArrayList<Project>();

        public List<Project> findByEmpNo(String empNo){
            return RET_FIND_BY_EMP_NO;
        }
    }

    /**
     * ProjectCustomFieldDaoのMockクラス
     * @author opentone
     */
    public static class MockProjectCustomFieldDao  {
        static Map<Long, Long> RET = new HashMap<Long, Long>();

        public Long findIdByProjectIdNo(SearchCustomFieldCondition condition) throws RecordNotFoundException {
            if (RET.get(condition.getNo()) == null) {
                throw new RecordNotFoundException();
            }
            return RET.get(condition.getNo());
        }
    }

    public static class MockCustomFieldDao {
        static List<CustomField> RET_FIND_BY_PROJECT_ID;
        public List<CustomField> findByProjectId(SearchCustomFieldCondition condition) {
            return RET_FIND_BY_PROJECT_ID;
        }
    }

    /**
     * CorresponTypeDaoのMockクラス
     * @author opentone
     */
    public static class MockCorresponTypeDao extends MockAbstractDao<Entity> {
        static CorresponType RET_FIND_BY_ID_PROJECT__ID;

        public CorresponType findByIdProjectId(Long id, String projectId)
            throws RecordNotFoundException {
            if (RET_FIND_BY_ID_PROJECT__ID == null) {
                throw new RecordNotFoundException();
            }
            return RET_FIND_BY_ID_PROJECT__ID;
        }
    }}
