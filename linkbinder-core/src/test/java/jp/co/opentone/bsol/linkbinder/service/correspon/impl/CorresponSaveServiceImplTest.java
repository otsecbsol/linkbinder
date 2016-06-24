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

import java.io.File;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.SerializationUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import jp.co.opentone.bsol.framework.core.config.SystemConfig;
import jp.co.opentone.bsol.framework.core.dao.Entity;
import jp.co.opentone.bsol.framework.core.dao.KeyDuplicateException;
import jp.co.opentone.bsol.framework.core.dao.RecordNotFoundException;
import jp.co.opentone.bsol.framework.core.dao.StaleRecordException;
import jp.co.opentone.bsol.framework.core.extension.ibatis.DBValue;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.test.AbstractTestCase;
import jp.co.opentone.bsol.linkbinder.dto.AddressCorresponGroup;
import jp.co.opentone.bsol.linkbinder.dto.AddressUser;
import jp.co.opentone.bsol.linkbinder.dto.Attachment;
import jp.co.opentone.bsol.linkbinder.dto.Correspon;
import jp.co.opentone.bsol.linkbinder.dto.CorresponCustomField;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroup;
import jp.co.opentone.bsol.linkbinder.dto.CorresponHierarchy;
import jp.co.opentone.bsol.linkbinder.dto.CorresponType;
import jp.co.opentone.bsol.linkbinder.dto.CustomField;
import jp.co.opentone.bsol.linkbinder.dto.ParentCorresponNoSeq;
import jp.co.opentone.bsol.linkbinder.dto.PersonInCharge;
import jp.co.opentone.bsol.linkbinder.dto.ProjectUser;
import jp.co.opentone.bsol.linkbinder.dto.UpdateMode;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.Workflow;
import jp.co.opentone.bsol.linkbinder.dto.code.AddressType;
import jp.co.opentone.bsol.linkbinder.dto.code.AddressUserType;
import jp.co.opentone.bsol.linkbinder.dto.code.CorresponStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.ReplyRequired;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowProcessStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowType;
import jp.co.opentone.bsol.linkbinder.dto.condition.ParentCorresponNoSeqCondition;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponGroupCondition;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCustomFieldCondition;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchUserCondition;
import jp.co.opentone.bsol.linkbinder.service.MockAbstractService;
import jp.co.opentone.bsol.linkbinder.service.correspon.CorresponSaveService;

/**
 * {@link CorresponServiceImpl}のテストケース.
 * @author opentone
 */
public class CorresponSaveServiceImplTest extends AbstractTestCase {

    /**
     * テスト対象.
     */
    @Resource
    private CorresponSaveService service;

    private User user = new User();

    /**
     * ファイル一時保存パスキー情報.
     */
    public static final String KEY_FILE_DIR_PATH = "dir.upload.temp";

    /** checker1 */
    private static final User CHECKER_1 = new User();
    static {
        CHECKER_1.setEmpNo("USER001");
    }

    /** checker2 */
    private static final User CHECKER_2 = new User();
    static {
        CHECKER_2.setEmpNo("USER002");
    }

    /** checker3 */
    private static final User CHECKER_3 = new User();
    static {
        CHECKER_3.setEmpNo("USER003");
    }

    /** approver */
    private static final User APPROVER = new User();
    static {
        APPROVER.setEmpNo("USER004");
    }


    @BeforeClass
    public static void testSetUp() {
    }

    public static void testTeardown() {
    }

    /**
     * テスト前準備.
     */
    @Before
    public void setUp() {
        user.setEmpNo("ZZA01");
        user.setNameE("Test User");
        MockAbstractService.CURRENT_USER = user;
        ProjectUser pUser = new ProjectUser();
        pUser.setUser(user);
        MockAbstractService.CURRENT_PROJECT_USER = pUser;
        MockAbstractDao.RET_UPDATE_WORKFLOW = new ArrayList<Workflow>();

        //TODO JMockitバージョンアップ対応
//        Mockit.redefineMethods(AbstractService.class, MockAbstractService.class);
//        Mockit.redefineMethods(CorresponDaoImpl.class, MockCorresponDao.class);
//        Mockit.redefineMethods(WorkflowDaoImpl.class, MockWorkflowDao.class);
//        Mockit.redefineMethods(AddressCorresponGroupDaoImpl.class,
//                               MockAddressCorresponGroupDao.class);
//        Mockit.redefineMethods(AddressUserDaoImpl.class, MockAddressUserDao.class);
//        Mockit.redefineMethods(PersonInChargeDaoImpl.class, MockPersonInChargeDao.class);
//        Mockit.redefineMethods(AbstractDao.class, MockAbstractDao.class);
//
//        Mockit.redefineMethods(CorresponWorkflowServiceImpl.class,
//                               MockCorresponWorkflowService.class);
//
//        Mockit.redefineMethods(UserDaoImpl.class, MockProjectUserDao.class);
//        Mockit.redefineMethods(CorresponGroupDaoImpl.class, MockCorresponGroupListDao.class);
//        Mockit.redefineMethods(AttachmentDaoImpl.class, MockAttachmentDao.class);
//        Mockit.redefineMethods(CorresponCustomFieldDaoImpl.class, MockCorresponCustomFieldDao.class);
//        Mockit.redefineMethods(CustomFieldDaoImpl.class, MockCustomFieldDao.class);
//        Mockit.redefineMethods(CorresponTypeDaoImpl.class, MockCorresponTypeDao.class);
//        Mockit.redefineMethods(User.class, MockUser.class);
//
//        Mockit
//        .redefineMethods(ParentCorresponNoSeqDaoImpl.class, MockParentCorresponNoSeqDao.class);
//        Mockit.redefineMethods(ProjectCustomFieldDaoImpl.class, MockProjectCustomFieldDao.class);
    }

    /**
     * 後始末.
     */
    @After
    public void tearDown() {
        // 差し換えたMockをクリアする
        //TODO JMockitバージョンアップ対応
//        Mockit.tearDownMocks();
        MockUser.IS_SYSTEM_ADMIN = false;

        MockAddressCorresponGroupDao.RET_FIND_BY_CORRESPON_ID = null;
        MockAddressUserDao.RET_FIND_BY_ADDRESS_CORRESPON_GROUP_ID = null;
        MockPersonInChargeDao.RET_FIND_BY_ADDRESS_USER_ID = null;
        MockWorkflowDao.RET_FIND_BY_CORRESPON_ID = null;
        MockAbstractDao.RET_UPDATE = null;
        MockAbstractDao.RET_UPDATE_CORRESPON = null;
        MockAbstractDao.RET_UPDATE_WORKFLOW = null;
        MockAbstractDao.CREATE_COUNT = 0;
        MockAbstractDao.countElement = 0;
        MockAbstractDao.RET_CREATE_ATTACHMENT = new ArrayList<Attachment>();

        MockAbstractDao.RET_FIND_BY_ID = new HashMap<String, Object>();
        MockAbstractDao.count = 0;
        MockAbstractDao.RET_FIND_BY_ID_CORRESPONGROUP = null;
        MockProjectUserDao.RET_FIND_BY_CORRESPON_ID = null;
        MockCorresponGroupListDao.RET_FIND_BY_CORRESPON_ID = null;

        MockAttachmentDao.RET_FIND_BY_CORRESPON_ID = null;
        MockCorresponCustomFieldDao.RET_FIND_BY_ID = null;

        MockCorresponWorkflowService.RET_METHOD = null;
        MockCorresponWorkflowService.RET_CORRESPON = null;
        MockCorresponWorkflowService.RET_WORKFLOW = null;

        tearDownMockAbstractService();

        MockCorresponTypeDao.RET_FIND_BY_PROJECT_CORRESPON_TYPE_ID = null;
        MockCorresponTypeDao.EX_FIND_BY_PROJECT_CORRESPON_TYPE_ID = null;
        MockCustomFieldDao.RET_FIND_BY_ID_PROJECT_ID = null;
        MockCustomFieldDao.EX_FIND_BY_PROJECT_ID = null;

        MockProjectCustomFieldDao.RET = new HashMap<Long, Long>();
    }

    private void tearDownMockAbstractService() {
        MockAbstractService.CURRENT_PROJECT_ID = null;
        MockAbstractService.CURRENT_PROJECT = null;
        MockAbstractService.CURRENT_USER = null;
        MockAbstractService.CURRENT_PROJECT_USER = null;
        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        MockAbstractService.IS_GROUP_ADMIN = null;
        MockAbstractService.IS_ANY_GROUP_ADMIN = false;
        MockAbstractService.ACCESSIBLE_PROJECTS = null;
        MockAbstractService.VALIDATE_PROJECT_ID_EXCEPTION = null;
        MockAbstractService.BASE_URL = null;
    }

    /**
     * コレポン文書新規登録処理の確認用データを作成する
     *
     * @return
     */
    private void createDummyForCreate01() {

        // コレポン文書
        Correspon c = new Correspon();
        c.setId(null);
        c.setCorresponNo(null);
        c.setParentCorresponId(null);
        c.setProjectId("PJ1");
        c.setProjectNameE("TestProject");

        CorresponGroup from = new CorresponGroup();
        from.setId(new Long(2L));
        c.setFromCorresponGroup(from);
        c.setPreviousRevCorresponId(943L);

        CorresponType ct = new CorresponType();
        ct.setId(new Long(2L));
        ct.setProjectId("PJ1");
        ct.setProjectCorresponTypeId(new Long(20));
        c.setCorresponType(ct);

        c.setSubject("TestData");
        c.setBody("Testです");
        c.setIssuedAt(null);
        c.setCorresponStatus(CorresponStatus.OPEN);
        c.setReplyRequired(ReplyRequired.YES);
        c.setDeadlineForReply(new GregorianCalendar(2009, 3, 3).getTime());
        c.setWorkflowStatus(WorkflowStatus.DRAFT);

        User u = new User();
        u.setEmpNo("00001");
        u.setNameE("Test User");
        c.setCreatedBy(MockAbstractService.CURRENT_USER);
        c.setUpdatedBy(MockAbstractService.CURRENT_USER);
        c.setReplyAddressUserId(9876L);
        MockAbstractDao.RET_UPDATE_CORRESPON = c;

        // 添付ファイル
        List<Attachment> createAttachments = new ArrayList<Attachment>();
        Attachment createA = new Attachment();
        createA.setCorresponId(99L);
        createA.setFileId("12345upload.xls");
        createA.setFileName("File1.xls");
        createA.setCreatedBy(MockAbstractService.CURRENT_USER);
        createA.setUpdatedBy(MockAbstractService.CURRENT_USER);
        createAttachments.add(createA);

        Attachment createA1 = new Attachment();
        createA1.setCorresponId(99L);
        createA1.setFileId("12345File.xls");
        createA1.setFileName("File2.xls");
        createA1.setCreatedBy(MockAbstractService.CURRENT_USER);
        createA1.setUpdatedBy(MockAbstractService.CURRENT_USER);

        createAttachments.add(createA1);

        MockAbstractDao.RET_CREATE_ATTACHMENT = createAttachments;

        // コレポン文書-カスタムフィールド
        List<CorresponCustomField> createCorresponCustomFields = new ArrayList<CorresponCustomField>();
        CorresponCustomField ccf1 = new CorresponCustomField();
        ccf1.setCorresponId(99L);
        ccf1.setProjectCustomFieldId(new Long(2));
        ccf1.setValue("CustomTest1");
        ccf1.setCreatedBy(MockAbstractService.CURRENT_USER);
        ccf1.setUpdatedBy(MockAbstractService.CURRENT_USER);
        createCorresponCustomFields.add(ccf1);
        MockProjectCustomFieldDao.RET.put(1L, 2L);


        CorresponCustomField ccf2 = new CorresponCustomField();
        ccf2.setCorresponId(99L);
        ccf2.setProjectCustomFieldId(new Long(1));
        ccf2.setValue(null);
        ccf2.setCreatedBy(MockAbstractService.CURRENT_USER);
        ccf2.setUpdatedBy(MockAbstractService.CURRENT_USER);
        createCorresponCustomFields.add(ccf2);
        MockProjectCustomFieldDao.RET.put(2L, 1L);

        MockAbstractDao.RET_CREATE_CORRESPONCUSTOMFIELD = createCorresponCustomFields;

        // 宛先-ユーザー
        List<AddressUser> createAddressUsers = new ArrayList<AddressUser>();
        AddressUser au1 = new AddressUser();
        au1.setAddressCorresponGroupId(99L);
        User u2 = new User();
        u2.setEmpNo("00001");
        u2.setNameE("Test User");
        au1.setUser(u2);
        au1.setAddressUserType(AddressUserType.ATTENTION);
        au1.setCreatedBy(MockAbstractService.CURRENT_USER);
        au1.setUpdatedBy(MockAbstractService.CURRENT_USER);

        createAddressUsers.add(au1);

        AddressUser au2 = new AddressUser();
        au2.setAddressCorresponGroupId(99L);
        User u3 = new User();
        u3.setEmpNo("00002");
        u3.setNameE("Test User");
        au2.setUser(u3);
        au2.setAddressUserType(AddressUserType.NORMAL_USER);
        au2.setCreatedBy(MockAbstractService.CURRENT_USER);
        au2.setUpdatedBy(MockAbstractService.CURRENT_USER);

        createAddressUsers.add(au2);

        MockAbstractDao.RET_CREATE_ADDRESSUSER = createAddressUsers;

        // 宛先-活動単位
        List<AddressCorresponGroup> createAddressCorresponGroups = new ArrayList<AddressCorresponGroup>();
        AddressCorresponGroup acg1 = new AddressCorresponGroup();
        acg1.setCorresponId(99L);
        CorresponGroup cg = new CorresponGroup();
        cg.setId(new Long(1));
        acg1.setCorresponGroup(cg);
        acg1.setAddressType(AddressType.TO);
        acg1.setCreatedBy(MockAbstractService.CURRENT_USER);
        acg1.setUpdatedBy(MockAbstractService.CURRENT_USER);
        acg1.setUsers(MockAbstractDao.RET_CREATE_ADDRESSUSER);

        createAddressCorresponGroups.add(acg1);

        MockAbstractDao.RET_CREATE_ADDRESSCORRESPONGROUP = createAddressCorresponGroups;

        // 担当
        List<PersonInCharge> createPersonInCharges = new ArrayList<PersonInCharge>();
        PersonInCharge pic1 = new PersonInCharge();
        pic1.setAddressUserId(99L);
        User u4 = new User();
        u4.setEmpNo("00002");
        u4.setNameE("Test User");
        pic1.setUser(u4);
        pic1.setCreatedBy(MockAbstractService.CURRENT_USER);
        pic1.setUpdatedBy(MockAbstractService.CURRENT_USER);

        createPersonInCharges.add(pic1);

        PersonInCharge pic2 = new PersonInCharge();
        pic2.setAddressUserId(99L);
        User u5 = new User();
        u5.setEmpNo("00003");
        u5.setNameE("Test User");
        pic2.setUser(u5);
        pic2.setCreatedBy(MockAbstractService.CURRENT_USER);
        pic2.setUpdatedBy(MockAbstractService.CURRENT_USER);

        createPersonInCharges.add(pic2);

        MockAbstractDao.RET_CREATE_PERSONINCHARGE = createPersonInCharges;

    }

    /**
     * コレポン文書新規登録処理の確認用データを作成する
     *
     * @return
     */
    private void createDummyForCreate02() {

        // コレポン文書
        Correspon c = new Correspon();
        c.setId(null);
        c.setProjectId("PJ1");
        c.setProjectNameE("TestProject");

        CorresponGroup from = new CorresponGroup();
        from.setId(new Long(2L));
        c.setFromCorresponGroup(from);
        c.setPreviousRevCorresponId(943L);

        CorresponType ct = new CorresponType();
        ct.setId(new Long(2L));
        ct.setProjectId("PJ1");
        ct.setProjectCorresponTypeId(new Long(20));
        c.setCorresponType(ct);
        c.setReplyAddressUserId(9876L);

        c.setSubject("TestData");
        c.setBody("Testです");
        c.setIssuedAt(null);
        c.setCorresponStatus(CorresponStatus.OPEN);
        c.setDeadlineForReply(new GregorianCalendar(2009, 3, 3).getTime());
        c.setWorkflowStatus(WorkflowStatus.DRAFT);
        c.setReplyRequired(ReplyRequired.YES);

        User u = new User();
        u.setEmpNo("00001");
        u.setNameE("Test User");
        c.setCreatedBy(MockAbstractService.CURRENT_USER);
        c.setUpdatedBy(MockAbstractService.CURRENT_USER);
        c.setReplyAddressUserId(9876L);

        MockAbstractDao.RET_UPDATE_CORRESPON = c;

        // 添付ファイル
        List<Attachment> createAttachments = new ArrayList<Attachment>();
        Attachment createA = new Attachment();
        createA.setCorresponId(99L);
        createA.setFileId("12345upload.xls");
        createA.setFileName("File1.xls");
        createA.setCreatedBy(MockAbstractService.CURRENT_USER);
        createA.setUpdatedBy(MockAbstractService.CURRENT_USER);

        createAttachments.add(createA);

        Attachment createA1 = new Attachment();
        createA1.setCorresponId(99L);
        createA1.setFileId("12345File.xls");
        createA1.setFileName("File2.xls");
        createA1.setCreatedBy(MockAbstractService.CURRENT_USER);
        createA1.setUpdatedBy(MockAbstractService.CURRENT_USER);

        createAttachments.add(createA1);

        MockAbstractDao.RET_CREATE_ATTACHMENT = createAttachments;

        // コレポン文書-カスタムフィールド
        List<CorresponCustomField> createCorresponCustomFields = new ArrayList<CorresponCustomField>();
        CorresponCustomField ccf1 = new CorresponCustomField();
        ccf1.setCorresponId(99L);
        ccf1.setProjectCustomFieldId(new Long(2));
        ccf1.setValue("CustomTest1");
        ccf1.setCreatedBy(MockAbstractService.CURRENT_USER);
        ccf1.setUpdatedBy(MockAbstractService.CURRENT_USER);
        createCorresponCustomFields.add(ccf1);
        MockProjectCustomFieldDao.RET.put(1L, 2L);

        CorresponCustomField ccf2 = new CorresponCustomField();
        ccf2.setCorresponId(99L);
        ccf2.setProjectCustomFieldId(new Long(1));
        ccf2.setValue("PJ1");
        ccf2.setCreatedBy(MockAbstractService.CURRENT_USER);
        ccf2.setUpdatedBy(MockAbstractService.CURRENT_USER);
        createCorresponCustomFields.add(ccf2);
        MockProjectCustomFieldDao.RET.put(2L, 1L);

        MockAbstractDao.RET_CREATE_CORRESPONCUSTOMFIELD = createCorresponCustomFields;

        // 宛先-ユーザー
        List<AddressUser> userByAddressGroupUsers = new ArrayList<AddressUser>();
        List<AddressUser> createAddressUsers = new ArrayList<AddressUser>();
        AddressUser au1 = new AddressUser();
        au1.setAddressCorresponGroupId(99L);
        User u2 = new User();
        u2.setEmpNo("00001");
        u2.setNameE("Test User");
        au1.setUser(u2);
        au1.setAddressUserType(AddressUserType.ATTENTION);
        au1.setCreatedBy(MockAbstractService.CURRENT_USER);
        au1.setUpdatedBy(MockAbstractService.CURRENT_USER);
        userByAddressGroupUsers.add(au1);
        createAddressUsers.add(au1);

        AddressUser au2 = new AddressUser();
        au2.setAddressCorresponGroupId(99L);
        User u3 = new User();
        u3.setEmpNo("00002");
        u3.setNameE("Test User");
        au2.setUser(u3);
        au2.setAddressUserType(AddressUserType.NORMAL_USER);
        au2.setCreatedBy(MockAbstractService.CURRENT_USER);
        au2.setUpdatedBy(MockAbstractService.CURRENT_USER);
        userByAddressGroupUsers.add(au2);
        createAddressUsers.add(au2);

        createAddressUsers.add(au1);
        createAddressUsers.add(au2);

        MockAbstractDao.RET_CREATE_ADDRESSUSER = createAddressUsers;

        // 宛先-活動単位
        List<AddressCorresponGroup> createAddressCorresponGroups = new ArrayList<AddressCorresponGroup>();
        AddressCorresponGroup acg1 = new AddressCorresponGroup();
        acg1.setCorresponId(99L);
        CorresponGroup cg1 = new CorresponGroup();
        cg1.setId(new Long(1));
        acg1.setUsers(userByAddressGroupUsers);
        acg1.setCorresponGroup(cg1);
        acg1.setAddressType(AddressType.TO);
        acg1.setCreatedBy(MockAbstractService.CURRENT_USER);
        acg1.setUpdatedBy(MockAbstractService.CURRENT_USER);

        createAddressCorresponGroups.add(acg1);

        AddressCorresponGroup acg2 = new AddressCorresponGroup();
        acg2.setCorresponId(99L);
        CorresponGroup cg2 = new CorresponGroup();
        cg2.setId(new Long(2));
        acg2.setUsers(userByAddressGroupUsers);
        acg2.setCorresponGroup(cg2);
        acg2.setAddressType(AddressType.CC);
        acg2.setCreatedBy(MockAbstractService.CURRENT_USER);
        acg2.setUpdatedBy(MockAbstractService.CURRENT_USER);

        createAddressCorresponGroups.add(acg2);

        MockAbstractDao.RET_CREATE_ADDRESSCORRESPONGROUP = createAddressCorresponGroups;

        // 担当
        List<PersonInCharge> createPersonInCharges = new ArrayList<PersonInCharge>();
        PersonInCharge pic1 = new PersonInCharge();
        pic1.setAddressUserId(99L);
        User u4 = new User();
        u4.setEmpNo("00002");
        u4.setNameE("Test User");
        pic1.setUser(u4);
        pic1.setCreatedBy(MockAbstractService.CURRENT_USER);
        pic1.setUpdatedBy(MockAbstractService.CURRENT_USER);

        createPersonInCharges.add(pic1);

        PersonInCharge pic2 = new PersonInCharge();
        pic2.setAddressUserId(99L);
        User u5 = new User();
        u5.setEmpNo("00003");
        u5.setNameE("Test User");
        pic2.setUser(u5);
        pic2.setCreatedBy(MockAbstractService.CURRENT_USER);
        pic2.setUpdatedBy(MockAbstractService.CURRENT_USER);

        createPersonInCharges.add(pic2);

        MockAbstractDao.RET_CREATE_PERSONINCHARGE = createPersonInCharges;

        // コレポン文書階層
        CorresponHierarchy h;
        User user;

        h = new CorresponHierarchy();
        h.setId(null);
        h.setParentCorresponId(new Long(100));
        h.setChildCorresponId(new Long(99));
        h.setReplyAddressUserId(9876L);

        user = new User();
        user.setEmpNo("00001");
        user.setNameE("Test User");
        h.setCreatedBy(MockAbstractService.CURRENT_USER);
        h.setUpdatedBy(MockAbstractService.CURRENT_USER);

        MockAbstractDao.RET_CREATE_HIERARCHY = h;

    }

    /**
     * コレポン文書更新処理に必要なデータを作成する
     *
     * @return
     */
    private void createDummyForUpdate04() {

        // コレポン文書
        Correspon c = new Correspon();
        c.setId(new Long(90));

        CorresponGroup from = new CorresponGroup();
        from.setId(new Long(1L));
        c.setFromCorresponGroup(from);

        CorresponType ct = new CorresponType();
        ct.setId(new Long(1L));
        c.setCorresponType(ct);

        c.setSubject("更新後SUBJECT");
        c.setBody("更新後BODY");
        c.setReplyAddressUserId(9876L);

        c.setCorresponStatus(CorresponStatus.OPEN);
        c.setDeadlineForReply(new GregorianCalendar(2009, 3, 3).getTime());
        c.setWorkflowStatus(WorkflowStatus.DRAFT);
        c.setReplyRequired(ReplyRequired.YES);

        c.setUpdatedBy(MockAbstractService.CURRENT_USER);

        MockAbstractDao.RET_UPDATE_CORRESPON = c;

        // 添付ファイル
        List<Attachment> attachments = new ArrayList<Attachment>();
        Attachment a = new Attachment();
        a.setId(12345L);
        a.setUpdatedBy(MockAbstractService.CURRENT_USER);

        attachments.add(a);

        Attachment a1 = new Attachment();
        a1.setId(2L);
        a1.setUpdatedBy(MockAbstractService.CURRENT_USER);

        attachments.add(a1);

        MockAbstractDao.RET_UPDATE_ATTACHMENT = attachments;

        // 添付ファイル
        List<Attachment> createAttachments = new ArrayList<Attachment>();
        Attachment createA = new Attachment();
        createA.setCorresponId(90L);
        createA.setFileId("12345upload.xls");
        createA.setFileName("upload.xls");
        User user = new User();
        user.setEmpNo("ZZA01");
        user.setNameE("Test User");
        createA.setCreatedBy(user);
        createA.setUpdatedBy(MockAbstractService.CURRENT_USER);
        createAttachments.add(createA);

        Attachment createA1 = new Attachment();
        createA1.setCorresponId(90L);
        createA1.setFileId("12345File.xls");
        createA1.setFileName("File.xls");
        createA1.setCreatedBy(user);
        createA1.setUpdatedBy(MockAbstractService.CURRENT_USER);
        createAttachments.add(createA1);

        Attachment createA3 = new Attachment();
        createA3.setCorresponId(90L);
        createA3.setFileId("12345File.xls");
        createA3.setFileName("File.xls");
        createA3.setCreatedBy(user);
        createA3.setUpdatedBy(MockAbstractService.CURRENT_USER);
        createAttachments.add(createA3);

        MockAbstractDao.RET_CREATE_ATTACHMENT = createAttachments;

        // コレポン文書-カスタムフィールド
        CorresponCustomField ccf = new CorresponCustomField();
        ccf.setCorresponId(90L);
        ccf.setUpdatedBy(MockAbstractService.CURRENT_USER);

        MockCorresponCustomFieldDao.RET_UPDATE_CORRESPONCUSTOMFIELD = ccf;

        // コレポン文書-カスタムフィールド
        List<CorresponCustomField> createCorresponCustomFields = new ArrayList<CorresponCustomField>();
        CorresponCustomField ccf1 = new CorresponCustomField();
        ccf1.setCorresponId(90L);
        ccf1.setProjectCustomFieldId(new Long(2));
        ccf1.setValue("PJ1");
        ccf1.setCreatedBy(user);
        ccf1.setUpdatedBy(MockAbstractService.CURRENT_USER);
        createCorresponCustomFields.add(ccf1);
        MockProjectCustomFieldDao.RET.put(1L, 2L);

        CorresponCustomField ccf2 = new CorresponCustomField();
        ccf2.setCorresponId(90L);
        ccf2.setProjectCustomFieldId(new Long(1));
        ccf2.setValue("PJ2");
        ccf2.setCreatedBy(user);
        ccf2.setUpdatedBy(MockAbstractService.CURRENT_USER);
        createCorresponCustomFields.add(ccf2);
        MockProjectCustomFieldDao.RET.put(2L, 1L);

        MockAbstractDao.RET_CREATE_CORRESPONCUSTOMFIELD = createCorresponCustomFields;


        // 宛先-ユーザー
        AddressUser au = new AddressUser();
        au.setAddressCorresponGroupId(95L);
        au.setUpdatedBy(MockAbstractService.CURRENT_USER);

        MockAddressUserDao.RET_UPDATE_ADDRESSUSER = au;

        // 宛先-ユーザー
        List<AddressUser> createAddressUsers = new ArrayList<AddressUser>();
        AddressUser au1 = new AddressUser();
        au1.setAddressCorresponGroupId(90L);
        User u2 = new User();
        u2.setEmpNo("00001");
        u2.setNameE("Test User");
        au1.setUser(u2);
        au1.setAddressUserType(AddressUserType.ATTENTION);
        au1.setCreatedBy(user);
        au1.setUpdatedBy(MockAbstractService.CURRENT_USER);

        createAddressUsers.add(au1);

        AddressUser au2 = new AddressUser();
        au2.setAddressCorresponGroupId(90L);
        User u3 = new User();
        u3.setEmpNo("00002");
        u3.setNameE("Test User");
        au2.setUser(u3);
        au2.setAddressUserType(AddressUserType.NORMAL_USER);
        au2.setCreatedBy(user);
        au2.setUpdatedBy(MockAbstractService.CURRENT_USER);

        createAddressUsers.add(au2);

        MockAbstractDao.RET_CREATE_ADDRESSUSER = createAddressUsers;


        // 宛先-活動単位
        AddressCorresponGroup acg = new AddressCorresponGroup();
        acg.setCorresponId(90L);
        acg.setUpdatedBy(MockAbstractService.CURRENT_USER);

        MockAddressCorresponGroupDao.RET_UPDATE_ADDRESSCORRESPONGROUP = acg;

        // 宛先-活動単位
        List<AddressCorresponGroup> createAddressCorresponGroups = new ArrayList<AddressCorresponGroup>();
        AddressCorresponGroup acg1 = new AddressCorresponGroup();
        CorresponGroup cg = new CorresponGroup();
        cg.setId(new Long(1));
        acg1.setId(95L);
        acg1.setCorresponId(90L);
        acg1.setCorresponGroup(cg);
        acg1.setAddressType(AddressType.TO);
        acg1.setCreatedBy(user);
        acg1.setUpdatedBy(MockAbstractService.CURRENT_USER);

        createAddressCorresponGroups.add(acg1);

        MockAbstractDao.RET_CREATE_ADDRESSCORRESPONGROUP = createAddressCorresponGroups;

        // 担当
        PersonInCharge pic = new PersonInCharge();
        pic.setAddressUserId(90L);
        pic.setUpdatedBy(MockAbstractService.CURRENT_USER);

        MockPersonInChargeDao.RET_UPDATE_PERSONINCHARGE = pic;

        // 担当
        List<PersonInCharge> createPersonInCharges = new ArrayList<PersonInCharge>();
        PersonInCharge pic1 = new PersonInCharge();
        pic1.setAddressUserId(90L);
        User u4 = new User();
        u4.setEmpNo("00002");
        u4.setNameE("Test User");
        pic1.setUser(u4);
        pic1.setCreatedBy(user);
        pic1.setUpdatedBy(MockAbstractService.CURRENT_USER);

        createPersonInCharges.add(pic1);

        PersonInCharge pic2 = new PersonInCharge();
        pic2.setAddressUserId(90L);
        User u5 = new User();
        u5.setEmpNo("00003");
        u5.setNameE("Test User");
        pic2.setUser(u5);
        pic2.setCreatedBy(user);
        pic2.setUpdatedBy(MockAbstractService.CURRENT_USER);

        createPersonInCharges.add(pic2);

        MockAbstractDao.RET_CREATE_PERSONINCHARGE = createPersonInCharges;

        // 承認フロー
        Workflow w;
        List<Workflow> wfs = new ArrayList<Workflow>();

        w = new Workflow();
        w.setId(new Long(99));
        w.setCorresponId(new Long(90));
        w.setWorkflowNo(new Long(1));
        w.setVersionNo(1L);

        user = new User();
        user.setEmpNo("80001");
        user.setNameE("Checker1 User");
        w.setUser(user);
        user = new User();
        user.setEmpNo(DBValue.STRING_NULL);
        w.setFinishedBy(user);
        w.setCreatedBy(user);
        w.setUpdatedBy(MockAbstractService.CURRENT_USER);

        w.setWorkflowType(WorkflowType.CHECKER);
        w.setWorkflowProcessStatus(WorkflowProcessStatus.NONE);

        wfs.add(w);

        w = new Workflow();
        w.setId(new Long(98));
        w.setCorresponId(new Long(90));
        w.setWorkflowNo(new Long(2));
        w.setVersionNo(1L);

        user = new User();
        user.setEmpNo("80002");
        user.setNameE("Checker2 User");
        user = new User();
        user.setEmpNo(DBValue.STRING_NULL);
        w.setFinishedBy(user);
        w.setUser(user);
        w.setCreatedBy(user);
        w.setUpdatedBy(MockAbstractService.CURRENT_USER);

        w.setWorkflowType(WorkflowType.CHECKER);
        w.setWorkflowProcessStatus(WorkflowProcessStatus.NONE);

        wfs.add(w);

        MockWorkflowDao.RET_FIND_BY_CORRESPON_ID = wfs;
        MockWorkflowDao.RET_UPDATE_BY_CORRESPONID_WORKFLOW = wfs;

    }

    /**
     * コレポン文書更新処理に必要なデータを作成する
     *
     * @return
     */
    private void createDummyForUpdate05() {

        // コレポン文書
        Correspon c = new Correspon();
        c.setId(new Long(90));

        CorresponGroup from = new CorresponGroup();
        from.setId(new Long(1L));
        c.setFromCorresponGroup(from);

        CorresponType ct = new CorresponType();
        ct.setId(new Long(1L));
        c.setCorresponType(ct);

        c.setSubject("更新後SUBJECT");
        c.setBody("更新後BODY");

        c.setCorresponStatus(CorresponStatus.OPEN);
        c.setDeadlineForReply(new GregorianCalendar(2009, 3, 3).getTime());
        c.setWorkflowStatus(WorkflowStatus.DRAFT);
        c.setReplyRequired(ReplyRequired.YES);
        c.setReplyAddressUserId(9876L);

        User u = new User();
        u.setEmpNo("ZZA01");
        u.setNameE("Test User");
        c.setUpdatedBy(MockAbstractService.CURRENT_USER);

        MockAbstractDao.RET_UPDATE_CORRESPON = c;

        // 添付ファイル
        List<Attachment> attachments = new ArrayList<Attachment>();
        Attachment a = new Attachment();
        a.setId(12345L);
        a.setUpdatedBy(MockAbstractService.CURRENT_USER);

        attachments.add(a);

        Attachment a1 = new Attachment();
        a1.setId(2L);
        a1.setUpdatedBy(MockAbstractService.CURRENT_USER);

        attachments.add(a1);

        MockAbstractDao.RET_UPDATE_ATTACHMENT = attachments;

        // 添付ファイル
        List<Attachment> createAttachments = new ArrayList<Attachment>();
        Attachment createA = new Attachment();
        createA.setCorresponId(90L);
        createA.setFileId("12345upload.xls");
        createA.setFileName("upload.xls");
        User user = new User();
        user.setEmpNo("00001");
        user.setNameE("Test User");
        createA.setCreatedBy(user);
        createA.setUpdatedBy(MockAbstractService.CURRENT_USER);

        createAttachments.add(createA);

        Attachment createA1 = new Attachment();
        createA1.setCorresponId(90L);
        createA1.setFileId("12345File.xls");
        createA1.setFileName("File.xls");
        createA1.setCreatedBy(user);
        createA1.setUpdatedBy(MockAbstractService.CURRENT_USER);

        createAttachments.add(createA1);

        MockAbstractDao.RET_CREATE_ATTACHMENT = createAttachments;

        // コレポン文書-カスタムフィールド
        CorresponCustomField ccf = new CorresponCustomField();
        ccf.setCorresponId(90L);
        ccf.setUpdatedBy(MockAbstractService.CURRENT_USER);

        MockCorresponCustomFieldDao.RET_UPDATE_CORRESPONCUSTOMFIELD = ccf;

        // コレポン文書-カスタムフィールド
        List<CorresponCustomField> createCorresponCustomFields = new ArrayList<CorresponCustomField>();
        CorresponCustomField ccf1 = new CorresponCustomField();
        ccf1.setCorresponId(90L);
        ccf1.setProjectCustomFieldId(new Long(2));
        ccf1.setValue(null);
        ccf1.setCreatedBy(user);
        ccf1.setUpdatedBy(MockAbstractService.CURRENT_USER);
        createCorresponCustomFields.add(ccf1);
        MockProjectCustomFieldDao.RET.put(1L, 2L);

        CorresponCustomField ccf2 = new CorresponCustomField();
        ccf2.setCorresponId(90L);
        ccf2.setProjectCustomFieldId(new Long(1));
        ccf2.setValue("PJ1");
        ccf2.setCreatedBy(user);
        ccf2.setUpdatedBy(MockAbstractService.CURRENT_USER);
        createCorresponCustomFields.add(ccf2);
        MockProjectCustomFieldDao.RET.put(2L, 1L);

        MockAbstractDao.RET_CREATE_CORRESPONCUSTOMFIELD = createCorresponCustomFields;


        // 宛先-ユーザー
        AddressUser au = new AddressUser();
        au.setAddressCorresponGroupId(95L);
        au.setUpdatedBy(MockAbstractService.CURRENT_USER);

        MockAddressUserDao.RET_UPDATE_ADDRESSUSER = au;

        // 宛先-ユーザー
        List<AddressUser> createAddressUsers = new ArrayList<AddressUser>();
        AddressUser au1 = new AddressUser();
        au1.setAddressCorresponGroupId(90L);
        User u2 = new User();
        u2.setEmpNo("00001");
        u2.setNameE("Test User");
        au1.setUser(u2);
        au1.setAddressUserType(AddressUserType.ATTENTION);
        au1.setCreatedBy(user);
        au1.setUpdatedBy(MockAbstractService.CURRENT_USER);

        createAddressUsers.add(au1);

        AddressUser au2 = new AddressUser();
        au2.setAddressCorresponGroupId(90L);
        User u3 = new User();
        u3.setEmpNo("00002");
        u3.setNameE("Test User");
        au2.setUser(u3);
        au2.setAddressUserType(AddressUserType.NORMAL_USER);
        au2.setCreatedBy(user);
        au2.setUpdatedBy(MockAbstractService.CURRENT_USER);

        createAddressUsers.add(au2);

        MockAbstractDao.RET_CREATE_ADDRESSUSER = createAddressUsers;


        // 宛先-活動単位
        AddressCorresponGroup acg = new AddressCorresponGroup();
        acg.setCorresponId(90L);
        acg.setUpdatedBy(MockAbstractService.CURRENT_USER);

        MockAddressCorresponGroupDao.RET_UPDATE_ADDRESSCORRESPONGROUP = acg;

        // 宛先-活動単位
        List<AddressCorresponGroup> createAddressCorresponGroups = new ArrayList<AddressCorresponGroup>();
        AddressCorresponGroup acg1 = new AddressCorresponGroup();
        acg1.setCorresponId(90L);
        CorresponGroup cg = new CorresponGroup();
        cg.setId(new Long(1));
        acg1.setId(95L);
        acg1.setCorresponGroup(cg);
        acg1.setAddressType(AddressType.TO);
        acg1.setCreatedBy(user);
        acg1.setUpdatedBy(MockAbstractService.CURRENT_USER);

        createAddressCorresponGroups.add(acg1);

        MockAbstractDao.RET_CREATE_ADDRESSCORRESPONGROUP = createAddressCorresponGroups;

        // 担当
        PersonInCharge pic = new PersonInCharge();
        pic.setAddressUserId(90L);
        pic.setUpdatedBy(MockAbstractService.CURRENT_USER);

        MockPersonInChargeDao.RET_UPDATE_PERSONINCHARGE = pic;

        // 担当
        List<PersonInCharge> createPersonInCharges = new ArrayList<PersonInCharge>();
        PersonInCharge pic1 = new PersonInCharge();
        pic1.setAddressUserId(90L);
        User u4 = new User();
        u4.setEmpNo("00002");
        u4.setNameE("Test User");
        pic1.setUser(u4);
        pic1.setCreatedBy(user);
        pic1.setUpdatedBy(MockAbstractService.CURRENT_USER);

        createPersonInCharges.add(pic1);

        PersonInCharge pic2 = new PersonInCharge();
        pic2.setAddressUserId(90L);
        User u5 = new User();
        u5.setEmpNo("00003");
        u5.setNameE("Test User");
        pic2.setUser(u5);
        pic2.setCreatedBy(user);
        pic2.setUpdatedBy(MockAbstractService.CURRENT_USER);

        createPersonInCharges.add(pic2);

        MockAbstractDao.RET_CREATE_PERSONINCHARGE = createPersonInCharges;

        // 承認フロー
        Workflow w;
        List<Workflow> wfs = new ArrayList<Workflow>();

        w = new Workflow();
        w.setId(new Long(99));
        w.setCorresponId(new Long(90));
        w.setWorkflowNo(new Long(1));
        w.setVersionNo(1L);

        user = new User();
        user.setEmpNo("80001");
        user.setNameE("Checker1 User");
        w.setUser(user);
        user = new User();
        user.setEmpNo(DBValue.STRING_NULL);
        w.setFinishedBy(user);
        w.setCreatedBy(user);
        w.setUpdatedBy(MockAbstractService.CURRENT_USER);

        w.setWorkflowType(WorkflowType.CHECKER);
        w.setWorkflowProcessStatus(WorkflowProcessStatus.NONE);

        wfs.add(w);

        w = new Workflow();
        w.setId(new Long(98));
        w.setCorresponId(new Long(90));
        w.setWorkflowNo(new Long(2));
        w.setVersionNo(1L);

        user = new User();
        user.setEmpNo("80002");
        user.setNameE("Checker2 User");
        user = new User();
        user.setEmpNo(DBValue.STRING_NULL);
        w.setFinishedBy(user);
        w.setUser(user);
        w.setCreatedBy(user);
        w.setUpdatedBy(MockAbstractService.CURRENT_USER);

        w.setWorkflowType(WorkflowType.CHECKER);
        w.setWorkflowProcessStatus(WorkflowProcessStatus.NONE);

        wfs.add(w);

        MockWorkflowDao.RET_FIND_BY_CORRESPON_ID = wfs;
        MockWorkflowDao.RET_UPDATE_BY_CORRESPONID_WORKFLOW = wfs;

    }

    /**
     * コレポン文書更新処理に必要なデータを作成する
     *
     * @return
     */
    private void createDummyForUpdate06() {

        // コレポン文書
        Correspon c = new Correspon();
        c.setId(new Long(90));

        CorresponGroup from = new CorresponGroup();
        from.setId(new Long(1L));
        c.setFromCorresponGroup(from);

        CorresponType ct = new CorresponType();
        ct.setId(new Long(1L));
        c.setCorresponType(ct);

        c.setSubject("更新後SUBJECT");
        c.setBody("更新後BODY");

        c.setCorresponStatus(CorresponStatus.OPEN);
        c.setDeadlineForReply(new GregorianCalendar(2009, 3, 3).getTime());
        c.setWorkflowStatus(WorkflowStatus.UNDER_CONSIDERATION);
        c.setReplyRequired(ReplyRequired.NO);
        c.setReplyAddressUserId(9876L);

        User u = new User();
        u.setEmpNo("ZZA01");
        u.setNameE("Test User");
        c.setUpdatedBy(MockAbstractService.CURRENT_USER);

        MockAbstractDao.RET_UPDATE_CORRESPON = c;

        // 添付ファイル
        List<Attachment> attachments = new ArrayList<Attachment>();
        Attachment a = new Attachment();
        a.setId(12345L);
        a.setUpdatedBy(MockAbstractService.CURRENT_USER);

        attachments.add(a);

        Attachment a1 = new Attachment();
        a1.setId(2L);
        a1.setUpdatedBy(MockAbstractService.CURRENT_USER);

        attachments.add(a1);

        MockAbstractDao.RET_UPDATE_ATTACHMENT = attachments;

        // 添付ファイル
        List<Attachment> createAttachments = new ArrayList<Attachment>();
        Attachment createA = new Attachment();
        createA.setCorresponId(90L);
        createA.setFileId("12345upload.xls");
        createA.setFileName("upload.xls");
        User user = new User();
        user.setEmpNo("ZZA01");
        user.setNameE("Test User");
        createA.setCreatedBy(user);
        createA.setUpdatedBy(MockAbstractService.CURRENT_USER);

        createAttachments.add(createA);

        Attachment createA1 = new Attachment();
        createA1.setCorresponId(90L);
        createA1.setFileId("12345File.xls");
        createA1.setFileName("File.xls");
        createA1.setCreatedBy(user);
        createA1.setUpdatedBy(MockAbstractService.CURRENT_USER);

        createAttachments.add(createA1);

        MockAbstractDao.RET_CREATE_ATTACHMENT = createAttachments;

        // コレポン文書-カスタムフィールド
        CorresponCustomField ccf = new CorresponCustomField();
        ccf.setCorresponId(90L);
        ccf.setUpdatedBy(MockAbstractService.CURRENT_USER);

        MockCorresponCustomFieldDao.RET_UPDATE_CORRESPONCUSTOMFIELD = ccf;

        // コレポン文書-カスタムフィールド
        List<CorresponCustomField> createCorresponCustomFields = new ArrayList<CorresponCustomField>();
        CorresponCustomField ccf1 = new CorresponCustomField();
        ccf1.setCorresponId(90L);
        ccf1.setProjectCustomFieldId(new Long(2));
        ccf1.setValue(null);
        ccf1.setCreatedBy(user);
        ccf1.setUpdatedBy(MockAbstractService.CURRENT_USER);
        createCorresponCustomFields.add(ccf1);
        MockProjectCustomFieldDao.RET.put(1L, 2L);

        CorresponCustomField ccf2 = new CorresponCustomField();
        ccf2.setCorresponId(90L);
        ccf2.setProjectCustomFieldId(new Long(1));
        ccf2.setValue(null);
        ccf2.setCreatedBy(user);
        ccf2.setUpdatedBy(MockAbstractService.CURRENT_USER);
        createCorresponCustomFields.add(ccf2);
        MockProjectCustomFieldDao.RET.put(2L, 1L);

        MockAbstractDao.RET_CREATE_CORRESPONCUSTOMFIELD = createCorresponCustomFields;


        // 宛先-ユーザー
        AddressUser au = new AddressUser();
        au.setAddressCorresponGroupId(95L);
        au.setUpdatedBy(MockAbstractService.CURRENT_USER);

        MockAddressUserDao.RET_UPDATE_ADDRESSUSER = au;

        // 宛先-ユーザー
        List<AddressUser> createAddressUsers = new ArrayList<AddressUser>();
        AddressUser au1 = new AddressUser();
        au1.setAddressCorresponGroupId(90L);
        User u2 = new User();
        u2.setEmpNo("00001");
        u2.setNameE("Test User");
        au1.setUser(u2);
        au1.setAddressUserType(AddressUserType.ATTENTION);
        au1.setCreatedBy(user);
        au1.setUpdatedBy(MockAbstractService.CURRENT_USER);

        createAddressUsers.add(au1);

        AddressUser au2 = new AddressUser();
        au2.setAddressCorresponGroupId(90L);
        User u3 = new User();
        u3.setEmpNo("00002");
        u3.setNameE("Test User");
        au2.setUser(u3);
        au2.setAddressUserType(AddressUserType.NORMAL_USER);
        au2.setCreatedBy(user);
        au2.setUpdatedBy(MockAbstractService.CURRENT_USER);

        createAddressUsers.add(au2);

        MockAbstractDao.RET_CREATE_ADDRESSUSER = createAddressUsers;


        // 宛先-活動単位
        AddressCorresponGroup acg = new AddressCorresponGroup();
        acg.setCorresponId(90L);
        acg.setUpdatedBy(MockAbstractService.CURRENT_USER);

        MockAddressCorresponGroupDao.RET_UPDATE_ADDRESSCORRESPONGROUP = acg;

        // 宛先-活動単位
        List<AddressCorresponGroup> createAddressCorresponGroups = new ArrayList<AddressCorresponGroup>();
        AddressCorresponGroup acg1 = new AddressCorresponGroup();
        acg1.setCorresponId(90L);
        CorresponGroup cg = new CorresponGroup();
        cg.setId(new Long(1));
        acg1.setId(95L);
        acg1.setCorresponGroup(cg);
        acg1.setAddressType(AddressType.TO);
        acg1.setCreatedBy(user);
        acg1.setUpdatedBy(MockAbstractService.CURRENT_USER);

        createAddressCorresponGroups.add(acg1);

        MockAbstractDao.RET_CREATE_ADDRESSCORRESPONGROUP = createAddressCorresponGroups;

        // 担当
        PersonInCharge pic = new PersonInCharge();
        pic.setAddressUserId(90L);
        pic.setUpdatedBy(MockAbstractService.CURRENT_USER);

        MockPersonInChargeDao.RET_UPDATE_PERSONINCHARGE = pic;

        // 担当
        List<PersonInCharge> createPersonInCharges = new ArrayList<PersonInCharge>();
        PersonInCharge pic1 = new PersonInCharge();
        pic1.setAddressUserId(90L);
        User u4 = new User();
        u4.setEmpNo("00002");
        u4.setNameE("Test User");
        pic1.setUser(u4);
        pic1.setCreatedBy(user);
        pic1.setUpdatedBy(MockAbstractService.CURRENT_USER);

        createPersonInCharges.add(pic1);

        PersonInCharge pic2 = new PersonInCharge();
        pic2.setAddressUserId(90L);
        User u5 = new User();
        u5.setEmpNo("00003");
        u5.setNameE("Test User");
        pic2.setUser(u5);
        pic2.setCreatedBy(user);
        pic2.setUpdatedBy(MockAbstractService.CURRENT_USER);

        createPersonInCharges.add(pic2);

        MockAbstractDao.RET_CREATE_PERSONINCHARGE = createPersonInCharges;

        // 承認フロー
        Workflow w;
        List<Workflow> wfs = new ArrayList<Workflow>();

        w = new Workflow();
        w.setId(new Long(99));
        w.setCorresponId(new Long(90));
        w.setWorkflowNo(new Long(1));
        w.setVersionNo(1L);

        user = new User();
        user.setEmpNo("80001");
        user.setNameE("Checker1 User");
        w.setUser(user);
        w.setCreatedBy(user);
        w.setUpdatedBy(MockAbstractService.CURRENT_USER);

        w.setWorkflowType(WorkflowType.CHECKER);
        w.setWorkflowProcessStatus(WorkflowProcessStatus.NONE);

        wfs.add(w);

        MockWorkflowDao.RET_FIND_BY_CORRESPON_ID = wfs;
        MockAbstractDao.RET_UPDATE_CORRESPON_WORKFLOW = w;


    }

    /**
     * コレポン文書更新処理に必要なデータを作成する
     *
     * @return
     */
    private void createDummyForUpdate07() {

        // コレポン文書
        Correspon c = new Correspon();
        c.setId(new Long(90));

        CorresponGroup from = new CorresponGroup();
        from.setId(new Long(1L));
        c.setFromCorresponGroup(from);

        CorresponType ct = new CorresponType();
        ct.setId(new Long(1L));
        c.setCorresponType(ct);

        c.setSubject("更新後SUBJECT");
        c.setBody("更新後BODY");

        c.setCorresponStatus(CorresponStatus.OPEN);
        c.setDeadlineForReply(new GregorianCalendar(2009, 3, 3).getTime());
        c.setWorkflowStatus(WorkflowStatus.UNDER_CONSIDERATION);
        c.setReplyRequired(ReplyRequired.NO);
        c.setReplyAddressUserId(9876L);

        User u = new User();
        u.setEmpNo("ZZA01");
        u.setNameE("Test User");
        c.setUpdatedBy(MockAbstractService.CURRENT_USER);
        c.setVersionNo(1L);

        MockAbstractDao.RET_UPDATE_CORRESPON = c;

        // 添付ファイル
        List<Attachment> attachments = new ArrayList<Attachment>();
        Attachment a = new Attachment();
        a.setId(12345L);
        a.setUpdatedBy(MockAbstractService.CURRENT_USER);

        attachments.add(a);

        Attachment a1 = new Attachment();
        a1.setId(2L);
        a1.setUpdatedBy(MockAbstractService.CURRENT_USER);

        attachments.add(a1);

        MockAbstractDao.RET_UPDATE_ATTACHMENT = attachments;

        // 添付ファイル
        List<Attachment> createAttachments = new ArrayList<Attachment>();
        Attachment createA = new Attachment();
        createA.setCorresponId(90L);
        createA.setFileId("12345upload.xls");
        createA.setFileName("upload.xls");
        User user = new User();
        user.setEmpNo("ZZA01");
        user.setNameE("Test User");
        createA.setCreatedBy(user);
        createA.setUpdatedBy(MockAbstractService.CURRENT_USER);

        createAttachments.add(createA);

        Attachment createA1 = new Attachment();
        createA1.setCorresponId(90L);
        createA1.setFileId("12345File.xls");
        createA1.setFileName("File.xls");
        createA1.setCreatedBy(user);
        createA1.setUpdatedBy(MockAbstractService.CURRENT_USER);

        createAttachments.add(createA1);

        MockAbstractDao.RET_CREATE_ATTACHMENT = createAttachments;

        // コレポン文書-カスタムフィールド
        CorresponCustomField ccf = new CorresponCustomField();
        ccf.setCorresponId(90L);
        ccf.setUpdatedBy(MockAbstractService.CURRENT_USER);

        MockCorresponCustomFieldDao.RET_UPDATE_CORRESPONCUSTOMFIELD = ccf;

        // コレポン文書-カスタムフィールド
        List<CorresponCustomField> createCorresponCustomFields = new ArrayList<CorresponCustomField>();
        CorresponCustomField ccf1 = new CorresponCustomField();
        ccf1.setCorresponId(90L);
        ccf1.setProjectCustomFieldId(new Long(2));
        ccf1.setValue("PJ1");
        ccf1.setCreatedBy(user);
        ccf1.setUpdatedBy(MockAbstractService.CURRENT_USER);
        createCorresponCustomFields.add(ccf1);
        MockProjectCustomFieldDao.RET.put(1L, 2L);

        CorresponCustomField ccf2 = new CorresponCustomField();
        ccf2.setCorresponId(90L);
        ccf2.setProjectCustomFieldId(new Long(1));
        ccf2.setValue("PJ2");
        ccf2.setCreatedBy(user);
        ccf2.setUpdatedBy(MockAbstractService.CURRENT_USER);
        createCorresponCustomFields.add(ccf2);
        MockProjectCustomFieldDao.RET.put(2L, 1L);

        MockAbstractDao.RET_CREATE_CORRESPONCUSTOMFIELD = createCorresponCustomFields;


        // 宛先-ユーザー
        AddressUser au = new AddressUser();
        au.setAddressCorresponGroupId(95L);
        au.setUpdatedBy(MockAbstractService.CURRENT_USER);

        MockAddressUserDao.RET_UPDATE_ADDRESSUSER = au;

        // 宛先-ユーザー
        List<AddressUser> createAddressUsers = new ArrayList<AddressUser>();
        AddressUser au1 = new AddressUser();
        au1.setAddressCorresponGroupId(90L);
        User u2 = new User();
        u2.setEmpNo("00001");
        u2.setNameE("Test User");
        au1.setUser(u2);
        au1.setAddressUserType(AddressUserType.ATTENTION);
        au1.setCreatedBy(user);
        au1.setUpdatedBy(MockAbstractService.CURRENT_USER);

        createAddressUsers.add(au1);

        AddressUser au2 = new AddressUser();
        au2.setAddressCorresponGroupId(90L);
        User u3 = new User();
        u3.setEmpNo("00002");
        u3.setNameE("Test User");
        au2.setUser(u3);
        au2.setAddressUserType(AddressUserType.NORMAL_USER);
        au2.setCreatedBy(user);
        au2.setUpdatedBy(MockAbstractService.CURRENT_USER);

        createAddressUsers.add(au2);

        MockAbstractDao.RET_CREATE_ADDRESSUSER = createAddressUsers;


        // 宛先-活動単位
        AddressCorresponGroup acg = new AddressCorresponGroup();
        acg.setCorresponId(90L);
        acg.setUpdatedBy(MockAbstractService.CURRENT_USER);

        MockAddressCorresponGroupDao.RET_UPDATE_ADDRESSCORRESPONGROUP = acg;

        // 宛先-活動単位
        List<AddressCorresponGroup> createAddressCorresponGroups = new ArrayList<AddressCorresponGroup>();
        AddressCorresponGroup acg1 = new AddressCorresponGroup();
        acg1.setCorresponId(90L);
        CorresponGroup cg = new CorresponGroup();
        cg.setId(new Long(1));
        acg1.setId(95L);
        acg1.setCorresponGroup(cg);
        acg1.setAddressType(AddressType.TO);
        acg1.setCreatedBy(user);
        acg1.setUpdatedBy(MockAbstractService.CURRENT_USER);

        createAddressCorresponGroups.add(acg1);

        MockAbstractDao.RET_CREATE_ADDRESSCORRESPONGROUP = createAddressCorresponGroups;

        // 担当
        PersonInCharge pic = new PersonInCharge();
        pic.setAddressUserId(90L);
        pic.setUpdatedBy(MockAbstractService.CURRENT_USER);

        MockPersonInChargeDao.RET_UPDATE_PERSONINCHARGE = pic;

        // 担当
        List<PersonInCharge> createPersonInCharges = new ArrayList<PersonInCharge>();
        PersonInCharge pic1 = new PersonInCharge();
        pic1.setAddressUserId(90L);
        User u4 = new User();
        u4.setEmpNo("00002");
        u4.setNameE("Test User");
        pic1.setUser(u4);
        pic1.setCreatedBy(user);
        pic1.setUpdatedBy(MockAbstractService.CURRENT_USER);

        createPersonInCharges.add(pic1);

        PersonInCharge pic2 = new PersonInCharge();
        pic2.setAddressUserId(90L);
        User u5 = new User();
        u5.setEmpNo("00003");
        u5.setNameE("Test User");
        pic2.setUser(u5);
        pic2.setCreatedBy(user);
        pic2.setUpdatedBy(MockAbstractService.CURRENT_USER);

        createPersonInCharges.add(pic2);

        MockAbstractDao.RET_CREATE_PERSONINCHARGE = createPersonInCharges;

        // 承認フロー
        Workflow w;
        List<Workflow> wfs = new ArrayList<Workflow>();

        w = new Workflow();
        w.setId(new Long(99));
        w.setVersionNo(1L);

        user = new User();
        user.setEmpNo("ZZA01");
        user.setNameE("Test User");
        w.setUpdatedBy(MockAbstractService.CURRENT_USER);

        w.setWorkflowProcessStatus(WorkflowProcessStatus.UNDER_CONSIDERATION);

        wfs.add(w);

        MockAbstractDao.RET_UPDATE_CORRESPON_WORKFLOW = w;

        MockWorkflowDao.RET_FIND_BY_CORRESPON_ID = wfs;
    }

    /**
     * コレポン文書更新処理に必要なデータを作成する
     *
     * @return
     */
    private void createDummyForUpdate08() {

        // コレポン文書
        Correspon c = new Correspon();
        c.setId(new Long(90));

        CorresponGroup from = new CorresponGroup();
        from.setId(new Long(1L));
        c.setFromCorresponGroup(from);

        CorresponType ct = new CorresponType();
        ct.setId(new Long(1L));
        c.setCorresponType(ct);

        c.setSubject("更新後SUBJECT");
        c.setBody("更新後BODY");

        c.setCorresponStatus(CorresponStatus.OPEN);
        c.setDeadlineForReply(new GregorianCalendar(2009, 3, 3).getTime());
        c.setWorkflowStatus(WorkflowStatus.UNDER_CONSIDERATION);
        c.setReplyRequired(ReplyRequired.NO);
        c.setReplyAddressUserId(9876L);

        User u = new User();
        u.setEmpNo("ZZA01");
        u.setNameE("Test User");
        c.setUpdatedBy(MockAbstractService.CURRENT_USER);
        c.setVersionNo(1L);

        MockAbstractDao.RET_UPDATE_CORRESPON = c;

        // 添付ファイル
        List<Attachment> attachments = new ArrayList<Attachment>();
        Attachment a = new Attachment();
        a.setId(12345L);
        a.setUpdatedBy(MockAbstractService.CURRENT_USER);

        attachments.add(a);

        Attachment a1 = new Attachment();
        a1.setId(2L);
        a1.setUpdatedBy(MockAbstractService.CURRENT_USER);

        attachments.add(a1);

        MockAbstractDao.RET_UPDATE_ATTACHMENT = attachments;

        // 添付ファイル
        List<Attachment> createAttachments = new ArrayList<Attachment>();
        Attachment createA = new Attachment();
        createA.setCorresponId(90L);
        createA.setFileId("12345upload.xls");
        createA.setFileName("upload1.xls");
        User user = new User();
        user.setEmpNo("ZZA01");
        user.setNameE("Test User");

        createA.setCreatedBy(user);
        createA.setUpdatedBy(MockAbstractService.CURRENT_USER);

        createAttachments.add(createA);

        Attachment createA1 = new Attachment();
        createA1.setCorresponId(90L);
        createA1.setFileId("12345File.xls");
        createA1.setFileName("upload2.xls");
        createA1.setCreatedBy(user);
        createA1.setUpdatedBy(MockAbstractService.CURRENT_USER);

        createAttachments.add(createA1);

        MockAbstractDao.RET_CREATE_ATTACHMENT = createAttachments;

        // コレポン文書-カスタムフィールド
        CorresponCustomField ccf = new CorresponCustomField();
        ccf.setCorresponId(90L);
        ccf.setUpdatedBy(MockAbstractService.CURRENT_USER);

        MockCorresponCustomFieldDao.RET_UPDATE_CORRESPONCUSTOMFIELD = ccf;

        // コレポン文書-カスタムフィールド
        List<CorresponCustomField> createCorresponCustomFields = new ArrayList<CorresponCustomField>();
        CorresponCustomField ccf1 = new CorresponCustomField();
        ccf1.setCorresponId(90L);
        ccf1.setProjectCustomFieldId(new Long(2));
        ccf1.setValue("PJ1Value");
        ccf1.setCreatedBy(user);
        ccf1.setUpdatedBy(MockAbstractService.CURRENT_USER);
        createCorresponCustomFields.add(ccf1);
        MockProjectCustomFieldDao.RET.put(1L, 2L);

        CorresponCustomField ccf2 = new CorresponCustomField();
        ccf2.setCorresponId(90L);
        ccf2.setProjectCustomFieldId(new Long(1));
        ccf2.setValue("PJ2Value");
        ccf2.setCreatedBy(user);
        ccf2.setUpdatedBy(MockAbstractService.CURRENT_USER);
        createCorresponCustomFields.add(ccf2);
        MockProjectCustomFieldDao.RET.put(2L, 1L);

        MockAbstractDao.RET_CREATE_CORRESPONCUSTOMFIELD = createCorresponCustomFields;


        // 宛先-ユーザー
        AddressUser au = new AddressUser();
        au.setAddressCorresponGroupId(95L);
        au.setUpdatedBy(MockAbstractService.CURRENT_USER);

        MockAddressUserDao.RET_UPDATE_ADDRESSUSER = au;

        // 宛先-ユーザー
        List<AddressUser> createAddressUsers = new ArrayList<AddressUser>();
        AddressUser au1 = new AddressUser();
        au1.setAddressCorresponGroupId(95L);
        User u2 = new User();
        u2.setEmpNo("00001");
        u2.setNameE("Test User");
        au1.setUser(u2);
        au1.setAddressUserType(AddressUserType.ATTENTION);
        au1.setCreatedBy(user);
        au1.setUpdatedBy(MockAbstractService.CURRENT_USER);

        createAddressUsers.add(au1);

        AddressUser au2 = new AddressUser();
        au2.setAddressCorresponGroupId(95L);
        User u3 = new User();
        u3.setEmpNo("00002");
        u3.setNameE("Test User");
        au2.setUser(u3);
        au2.setAddressUserType(AddressUserType.NORMAL_USER);
        au2.setCreatedBy(user);
        au2.setUpdatedBy(MockAbstractService.CURRENT_USER);

        createAddressUsers.add(au2);

        MockAbstractDao.RET_CREATE_ADDRESSUSER = createAddressUsers;


        // 宛先-活動単位
        AddressCorresponGroup acg = new AddressCorresponGroup();
        acg.setCorresponId(90L);
        acg.setUpdatedBy(MockAbstractService.CURRENT_USER);

        MockAddressCorresponGroupDao.RET_UPDATE_ADDRESSCORRESPONGROUP = acg;

        // 宛先-活動単位
        List<AddressCorresponGroup> createAddressCorresponGroups = new ArrayList<AddressCorresponGroup>();
        AddressCorresponGroup acg1 = new AddressCorresponGroup();
        acg1.setCorresponId(90L);
        CorresponGroup cg = new CorresponGroup();
        cg.setId(new Long(1));
        acg1.setId(95L);
        acg1.setCorresponGroup(cg);
        acg1.setAddressType(AddressType.TO);
        acg1.setCreatedBy(user);
        acg1.setUpdatedBy(MockAbstractService.CURRENT_USER);

        createAddressCorresponGroups.add(acg1);

        MockAbstractDao.RET_CREATE_ADDRESSCORRESPONGROUP = createAddressCorresponGroups;

        // 担当
        PersonInCharge pic = new PersonInCharge();
        pic.setAddressUserId(90L);
        pic.setUpdatedBy(MockAbstractService.CURRENT_USER);

        MockPersonInChargeDao.RET_UPDATE_PERSONINCHARGE = pic;

        // 担当
        List<PersonInCharge> createPersonInCharges = new ArrayList<PersonInCharge>();
        PersonInCharge pic1 = new PersonInCharge();
        pic1.setAddressUserId(90L);
        User u4 = new User();
        u4.setEmpNo("00002");
        u4.setNameE("Test User");
        pic1.setUser(u4);
        pic1.setCreatedBy(user);
        pic1.setUpdatedBy(MockAbstractService.CURRENT_USER);

        createPersonInCharges.add(pic1);

        PersonInCharge pic2 = new PersonInCharge();
        pic2.setAddressUserId(90L);
        User u5 = new User();
        u5.setEmpNo("00003");
        u5.setNameE("Test User");
        pic2.setUser(u5);
        pic2.setCreatedBy(user);
        pic2.setUpdatedBy(MockAbstractService.CURRENT_USER);

        createPersonInCharges.add(pic2);

        MockAbstractDao.RET_CREATE_PERSONINCHARGE = createPersonInCharges;

        // 承認フロー
        Workflow w;
        List<Workflow> wfs = new ArrayList<Workflow>();

        w = new Workflow();
        w.setId(new Long(90));
        w.setVersionNo(1L);

        user = new User();
        user.setEmpNo("ZZA01");
        user.setNameE("Test User");
        w.setUpdatedBy(MockAbstractService.CURRENT_USER);

        w.setWorkflowProcessStatus(WorkflowProcessStatus.UNDER_CONSIDERATION);

        wfs.add(w);

        MockAbstractDao.RET_UPDATE_CORRESPON_WORKFLOW = w;

        MockWorkflowDao.RET_FIND_BY_CORRESPON_ID = wfs;
    }

    /**
     * コレポン文書更新処理に必要なデータを作成する
     *
     * @return
     */
    private void createRequestForUpdateData5() {

        // コレポン文書
        Correspon c = new Correspon();
        c.setId(new Long(90));
        c.setProjectId("PJ1");
        c.setCorresponNo("YOC:OT:BUILDING-00002");
        c.setParentCorresponId(new Long(1L));
        c.setCorresponStatus(CorresponStatus.CLOSED);
        c.setWorkflowStatus(WorkflowStatus.DENIED);
        c.setVersionNo(1L);

        CorresponGroup from = new CorresponGroup();
        from.setId(new Long(1L));
        from.setName("YOC:BUILDING");
        c.setFromCorresponGroup(from);

        CorresponType ct = new CorresponType();
        ct.setId(new Long(1L));
        ct.setName("Request");
        c.setCorresponType(ct);
        c.setSubject("Mock");
        c.setBody("This is test.\nThis correspondence is generated by CorresponServiceImpl.");
        c.setDeadlineForReply(new GregorianCalendar(2009, 3, 3).getTime());
        c.setFile1FileId("12345test.xls");
        c.setFile1Id(12345L);
        c.setFile1FileName("test.xls");
        c.setFile2Id(new Long(2));
        c.setFile2FileId("2text.txt");
        c.setFile2FileName("test.txt");
        c.setCustomField1Id(new Long(99));
        c.setCustomField1Label("CustomFieldLabel1");
        c.setCustomField1Value("This is customField1Value.");
        c.setCreatedAt(new GregorianCalendar(2009, 3, 1, 9, 3, 23).getTime());
        User u = new User();
        u.setEmpNo("ZZA01");
        u.setNameE("Test User");
        c.setCreatedBy(u);

        MockCorresponDao.RET_FIND_BY_ID.put("1", c);

        // 添付ファイル
        List<Attachment> attachments = new ArrayList<Attachment>();
        Attachment a = new Attachment();
        a.setId(12345L);
        a.setCorresponId(90L);
        a.setFileId("12345test.xls");
        a.setFileName("test.xls");
        a.setCreatedAt(new GregorianCalendar(2009, 3, 1, 9, 3, 23).getTime());
        a.setCreatedBy(u);

        attachments.add(a);

        Attachment a1 = new Attachment();
        a1.setId(2L);
        a1.setCorresponId(90L);
        a1.setFileId("2text.txt");
        a1.setFileName("test.txt");
        a1.setCreatedAt(new GregorianCalendar(2009, 3, 1, 9, 3, 23).getTime());
        a1.setCreatedBy(u);

        attachments.add(a1);

        MockAttachmentDao.RET_FIND_BY_CORRESPON_ID = attachments;

        // コレポン文書-カスタムフィールド
        CorresponCustomField ccf = new CorresponCustomField();
        ccf.setId(99L);
        ccf.setCorresponId(90L);
        ccf.setProjectCustomFieldId(new Long(2));
        ccf.setValue("This is customField1Value.");
        ccf.setCreatedAt(new GregorianCalendar(2009, 3, 1, 9, 3, 23).getTime());
        ccf.setCreatedBy(u);

        MockCorresponCustomFieldDao.RET_FIND_BY_ID = ccf;

        // 宛先-ユーザー
        List<AddressUser> addressUsers = new ArrayList<AddressUser>();
        AddressUser au = new AddressUser();
        au.setId(99L);
        au.setAddressCorresponGroupId(90L);
        User u2 = new User();
        u2.setEmpNo("00001");
        u2.setNameE("Test User");
        au.setUser(u2);
        au.setAddressUserType(AddressUserType.ATTENTION);
        au.setCreatedAt(new GregorianCalendar(2009, 3, 1, 9, 3, 23).getTime());
        au.setCreatedBy(u);

        addressUsers.add(au);

        MockAddressUserDao.RET_FIND_BY_ADDRESS_CORRESPON_GROUP_ID = addressUsers;

        // 宛先-活動単位
        List<AddressCorresponGroup> addressCorresponGroups = new ArrayList<AddressCorresponGroup>();
        AddressCorresponGroup acg = new AddressCorresponGroup();
        acg.setId(99L);
        acg.setCorresponId(90L);
        CorresponGroup cg = new CorresponGroup();
        cg.setId(new Long(1));
        acg.setCorresponGroup(cg);
        acg.setUsers(addressUsers);
        acg.setAddressType(AddressType.TO);
        acg.setCreatedAt(new GregorianCalendar(2009, 3, 1, 9, 3, 23).getTime());
        acg.setCreatedBy(u);

        addressCorresponGroups.add(acg);

        MockAddressCorresponGroupDao.RET_FIND_BY_CORRESPON_ID = addressCorresponGroups;

        // 担当
        List<PersonInCharge> personInCharges = new ArrayList<PersonInCharge>();
        PersonInCharge pic = new PersonInCharge();
        pic.setId(99L);
        pic.setAddressUserId(90L);
        pic.setUser(u2);
        pic.setCreatedAt(new GregorianCalendar(2009, 3, 1, 9, 3, 23).getTime());
        pic.setCreatedBy(u);

        personInCharges.add(pic);

        MockPersonInChargeDao.RET_FIND_BY_ADDRESS_USER_ID = personInCharges;

        // 承認フロー
        Workflow w;
        User user;
        List<Workflow> wfs = new ArrayList<Workflow>();

        w = new Workflow();
        w.setId(new Long(99));
        w.setCorresponId(new Long(90));
        w.setWorkflowNo(new Long(1));
        w.setVersionNo(1L);

        user = new User();
        user.setEmpNo("80001");
        user.setNameE("Checker1 User");
        w.setUser(user);
        w.setCreatedBy(user);
        w.setUpdatedBy(user);

        w.setWorkflowType(WorkflowType.CHECKER);
        w.setWorkflowProcessStatus(WorkflowProcessStatus.NONE);

        wfs.add(w);

        MockWorkflowDao.RET_FIND_BY_CORRESPON_ID = wfs;

    }

    /**
     * コレポン文書更新処理に必要なデータを作成する
     *
     * @return
     */
    private void createRequestForUpdateData10() {

        // コレポン文書
        Correspon c = new Correspon();
        c.setId(new Long(90));
        c.setProjectId("PJ1");
        c.setCorresponNo("YOC:OT:BUILDING-00002");
        c.setParentCorresponId(new Long(1L));
        c.setCorresponStatus(CorresponStatus.CLOSED);
        c.setWorkflowStatus(WorkflowStatus.UNDER_CONSIDERATION);
        c.setVersionNo(1L);

        CorresponGroup from = new CorresponGroup();
        from.setId(new Long(1L));
        from.setName("YOC:BUILDING");
        c.setFromCorresponGroup(from);

        CorresponType ct = new CorresponType();
        ct.setId(new Long(1L));
        ct.setName("Request");
        c.setCorresponType(ct);
        c.setSubject("Mock");
        c.setBody("This is test.\nThis correspondence is generated by CorresponServiceImpl.");
        c.setDeadlineForReply(new GregorianCalendar(2009, 3, 3).getTime());
        c.setFile1FileId("12345test.xls");
        c.setFile1Id(12345L);
        c.setFile1FileName("test.xls");
        c.setFile2Id(new Long(2));
        c.setFile2FileId("2text.txt");
        c.setFile2FileName("test.txt");
        c.setCustomField1Id(new Long(99));
        c.setCustomField1Label("CustomFieldLabel1");
        c.setCustomField1Value("This is customField1Value.");
        c.setCreatedAt(new GregorianCalendar(2009, 3, 1, 9, 3, 23).getTime());
        User u = new User();
        u.setEmpNo("ZZA01");
        u.setNameE("Test User");
        c.setCreatedBy(u);

        MockCorresponDao.RET_FIND_BY_ID.put("1", c);

        // 添付ファイル
        List<Attachment> attachments = new ArrayList<Attachment>();
        Attachment a = new Attachment();
        a.setId(12345L);
        a.setCorresponId(90L);
        a.setFileId("12345test.xls");
        a.setFileName("test.xls");
        a.setCreatedAt(new GregorianCalendar(2009, 3, 1, 9, 3, 23).getTime());
        a.setCreatedBy(u);

        attachments.add(a);

        Attachment a1 = new Attachment();
        a1.setId(2L);
        a1.setCorresponId(90L);
        a1.setFileId("2text.txt");
        a1.setFileName("test.txt");
        a1.setCreatedAt(new GregorianCalendar(2009, 3, 1, 9, 3, 23).getTime());
        a1.setCreatedBy(u);

        attachments.add(a1);

        MockAttachmentDao.RET_FIND_BY_CORRESPON_ID = attachments;

        // コレポン文書-カスタムフィールド
        CorresponCustomField ccf = new CorresponCustomField();
        ccf.setId(99L);
        ccf.setCorresponId(90L);
        ccf.setProjectCustomFieldId(new Long(1));
        ccf.setValue("This is customField1Value.");
        ccf.setCreatedAt(new GregorianCalendar(2009, 3, 1, 9, 3, 23).getTime());
        ccf.setCreatedBy(u);

        MockCorresponCustomFieldDao.RET_FIND_BY_ID = ccf;

        // 宛先-ユーザー
        List<AddressUser> addressUsers = new ArrayList<AddressUser>();
        AddressUser au = new AddressUser();
        au.setId(99L);
        au.setAddressCorresponGroupId(90L);
        User u2 = new User();
        u2.setEmpNo("00001");
        u2.setNameE("Test User");
        au.setUser(u2);
        au.setAddressUserType(AddressUserType.ATTENTION);
        au.setCreatedAt(new GregorianCalendar(2009, 3, 1, 9, 3, 23).getTime());
        au.setCreatedBy(u);

        addressUsers.add(au);

        MockAddressUserDao.RET_FIND_BY_ADDRESS_CORRESPON_GROUP_ID = addressUsers;

        // 宛先-活動単位
        List<AddressCorresponGroup> addressCorresponGroups = new ArrayList<AddressCorresponGroup>();
        AddressCorresponGroup acg = new AddressCorresponGroup();
        acg.setId(99L);
        acg.setCorresponId(90L);
        CorresponGroup cg = new CorresponGroup();
        cg.setId(new Long(1));
        acg.setCorresponGroup(cg);
        acg.setAddressType(AddressType.TO);
        acg.setCreatedAt(new GregorianCalendar(2009, 3, 1, 9, 3, 23).getTime());
        acg.setCreatedBy(u);

        addressCorresponGroups.add(acg);

        MockAddressCorresponGroupDao.RET_FIND_BY_CORRESPON_ID = addressCorresponGroups;

        // 担当
        List<PersonInCharge> personInCharges = new ArrayList<PersonInCharge>();
        PersonInCharge pic = new PersonInCharge();
        pic.setId(99L);
        pic.setAddressUserId(90L);
        pic.setUser(u2);
        pic.setCreatedAt(new GregorianCalendar(2009, 3, 1, 9, 3, 23).getTime());
        pic.setCreatedBy(u);

        personInCharges.add(pic);

        MockPersonInChargeDao.RET_FIND_BY_ADDRESS_USER_ID = personInCharges;

        // 承認フロー
        Workflow w;
        User user;
        List<Workflow> wfs = new ArrayList<Workflow>();

        w = new Workflow();
        w.setId(new Long(99));
        w.setCorresponId(new Long(90));
        w.setWorkflowNo(new Long(1));
        w.setVersionNo(1L);

        user = new User();
        user.setEmpNo("80001");
        user.setNameE("Checker1 User");
        w.setUser(user);
        w.setCreatedBy(user);
        w.setUpdatedBy(user);

        w.setWorkflowType(WorkflowType.CHECKER);
        w.setWorkflowProcessStatus(WorkflowProcessStatus.NONE);

        wfs.add(w);

        MockWorkflowDao.RET_FIND_BY_CORRESPON_ID = wfs;

    }

    /**
     * コレポン文書更新処理に必要なデータを作成する
     *
     * @return
     */
    private void createRequestForUpdateData11() {

        // コレポン文書
        Correspon c = new Correspon();
        c.setId(new Long(90));
        c.setProjectId("PJ1");
        c.setCorresponNo("YOC:OT:BUILDING-00002");
        c.setParentCorresponId(new Long(1L));
        c.setCorresponStatus(CorresponStatus.CLOSED);
        c.setWorkflowStatus(WorkflowStatus.REQUEST_FOR_CHECK);
        c.setVersionNo(1L);

        CorresponGroup from = new CorresponGroup();
        from.setId(new Long(1L));
        from.setName("YOC:BUILDING");
        c.setFromCorresponGroup(from);

        CorresponType ct = new CorresponType();
        ct.setId(new Long(1L));
        ct.setName("Request");
        c.setCorresponType(ct);
        c.setSubject("Mock");
        c.setBody("This is test.\nThis correspondence is generated by CorresponServiceImpl.");
        c.setDeadlineForReply(new GregorianCalendar(2009, 3, 3).getTime());
        c.setFile1FileId("12345test.xls");
        c.setFile1Id(12345L);
        c.setFile1FileName("test.xls");
        c.setFile2Id(new Long(2));
        c.setFile2FileId("2text.txt");
        c.setFile2FileName("test.txt");
        c.setCustomField1Id(new Long(99));
        c.setCustomField1Label("CustomFieldLabel1");
        c.setCustomField1Value("This is customField1Value.");
        c.setCreatedAt(new GregorianCalendar(2009, 3, 1, 9, 3, 23).getTime());
        User u = new User();
        u.setEmpNo("ZZA01");
        u.setNameE("Test User");
        c.setCreatedBy(u);

        MockCorresponDao.RET_FIND_BY_ID.put("1", c);

        // 添付ファイル
        List<Attachment> attachments = new ArrayList<Attachment>();
        Attachment a = new Attachment();
        a.setId(12345L);
        a.setCorresponId(90L);
        a.setFileId("12345test.xls");
        a.setFileName("test.xls");
        a.setCreatedAt(new GregorianCalendar(2009, 3, 1, 9, 3, 23).getTime());
        a.setCreatedBy(u);

        attachments.add(a);

        Attachment a1 = new Attachment();
        a1.setId(2L);
        a1.setCorresponId(90L);
        a1.setFileId("2text.txt");
        a1.setFileName("test.txt");
        a1.setCreatedAt(new GregorianCalendar(2009, 3, 1, 9, 3, 23).getTime());
        a1.setCreatedBy(u);

        attachments.add(a1);

        MockAttachmentDao.RET_FIND_BY_CORRESPON_ID = attachments;

        // コレポン文書-カスタムフィールド
        CorresponCustomField ccf = new CorresponCustomField();
        ccf.setId(99L);
        ccf.setCorresponId(90L);
        ccf.setProjectCustomFieldId(new Long(1));
        ccf.setValue("This is customField1Value.");
        ccf.setCreatedAt(new GregorianCalendar(2009, 3, 1, 9, 3, 23).getTime());
        ccf.setCreatedBy(u);

        MockCorresponCustomFieldDao.RET_FIND_BY_ID = ccf;

        // 宛先-ユーザー
        List<AddressUser> addressUsers = new ArrayList<AddressUser>();
        AddressUser au = new AddressUser();
        au.setId(99L);
        au.setAddressCorresponGroupId(90L);
        User u2 = new User();
        u2.setEmpNo("00001");
        u2.setNameE("Test User");
        au.setUser(u2);
        au.setAddressUserType(AddressUserType.ATTENTION);
        au.setCreatedAt(new GregorianCalendar(2009, 3, 1, 9, 3, 23).getTime());
        au.setCreatedBy(u);

        addressUsers.add(au);

        MockAddressUserDao.RET_FIND_BY_ADDRESS_CORRESPON_GROUP_ID = addressUsers;

        // 宛先-活動単位
        List<AddressCorresponGroup> addressCorresponGroups = new ArrayList<AddressCorresponGroup>();
        AddressCorresponGroup acg = new AddressCorresponGroup();
        acg.setId(99L);
        acg.setCorresponId(90L);
        CorresponGroup cg = new CorresponGroup();
        cg.setId(new Long(1));
        acg.setCorresponGroup(cg);
        acg.setAddressType(AddressType.TO);
        acg.setCreatedAt(new GregorianCalendar(2009, 3, 1, 9, 3, 23).getTime());
        acg.setCreatedBy(u);

        addressCorresponGroups.add(acg);

        MockAddressCorresponGroupDao.RET_FIND_BY_CORRESPON_ID = addressCorresponGroups;

        // 担当
        List<PersonInCharge> personInCharges = new ArrayList<PersonInCharge>();
        PersonInCharge pic = new PersonInCharge();
        pic.setId(99L);
        pic.setAddressUserId(90L);
        pic.setUser(u2);
        pic.setCreatedAt(new GregorianCalendar(2009, 3, 1, 9, 3, 23).getTime());
        pic.setCreatedBy(u);

        personInCharges.add(pic);

        MockPersonInChargeDao.RET_FIND_BY_ADDRESS_USER_ID = personInCharges;

        // 承認フロー
        Workflow w;
        User user;
        List<Workflow> wfs = new ArrayList<Workflow>();

        w = new Workflow();
        w.setId(new Long(99));
        w.setCorresponId(new Long(90));
        w.setWorkflowNo(new Long(1));
        w.setVersionNo(1L);

        user = new User();
        user.setEmpNo("80001");
        user.setNameE("Checker1 User");
        w.setUser(user);
        w.setCreatedBy(user);
        w.setUpdatedBy(user);

        w.setWorkflowType(WorkflowType.CHECKER);
        w.setWorkflowProcessStatus(WorkflowProcessStatus.NONE);

        wfs.add(w);

        w = new Workflow();
        w.setId(new Long(90));
        w.setCorresponId(new Long(90));
        w.setWorkflowNo(new Long(2));
        w.setVersionNo(1L);

        user = new User();
        user.setEmpNo("80002");
        user.setNameE("Approver User");
        w.setUser(user);
        w.setCreatedBy(user);
        w.setUpdatedBy(user);

        w.setWorkflowType(WorkflowType.APPROVER);
        w.setWorkflowProcessStatus(WorkflowProcessStatus.NONE);

        wfs.add(w);

        MockWorkflowDao.RET_FIND_BY_CORRESPON_ID = wfs;

    }

    /**
     * コレポン文書更新処理に必要なデータを作成する
     *
     * @return
     */
    private void createRequestForUpdateData12() {

        // コレポン文書
        Correspon c = new Correspon();
        c.setId(new Long(90));
        c.setProjectId("PJ1");
        c.setCorresponNo("YOC:OT:BUILDING-00002");
        c.setParentCorresponId(new Long(1L));
        c.setCorresponStatus(CorresponStatus.CLOSED);
        c.setWorkflowStatus(WorkflowStatus.UNDER_CONSIDERATION);
        c.setVersionNo(1L);

        CorresponGroup from = new CorresponGroup();
        from.setId(new Long(1L));
        from.setName("YOC:BUILDING");
        c.setFromCorresponGroup(from);

        CorresponType ct = new CorresponType();
        ct.setId(new Long(1L));
        ct.setName("Request");
        c.setCorresponType(ct);
        c.setSubject("Mock");
        c.setBody("This is test.\nThis correspondence is generated by CorresponServiceImpl.");
        c.setDeadlineForReply(new GregorianCalendar(2009, 3, 3).getTime());
        c.setFile1FileId("12345test.xls");
        c.setFile1Id(12345L);
        c.setFile1FileName("test.xls");
        c.setFile2Id(new Long(2));
        c.setFile2FileId("2text.txt");
        c.setFile2FileName("test.txt");
        c.setCustomField1Id(new Long(99));
        c.setCustomField1Label("CustomFieldLabel1");
        c.setCustomField1Value("This is customField1Value.");
        c.setCreatedAt(new GregorianCalendar(2009, 3, 1, 9, 3, 23).getTime());
        User u = new User();
        u.setEmpNo("ZZA01");
        u.setNameE("Test User");
        c.setCreatedBy(u);

        MockCorresponDao.RET_FIND_BY_ID.put("1", c);

        // 添付ファイル
        List<Attachment> attachments = new ArrayList<Attachment>();
        Attachment a = new Attachment();
        a.setId(12345L);
        a.setCorresponId(90L);
        a.setFileId("12345test.xls");
        a.setFileName("test.xls");
        a.setCreatedAt(new GregorianCalendar(2009, 3, 1, 9, 3, 23).getTime());
        a.setCreatedBy(u);

        attachments.add(a);

        Attachment a1 = new Attachment();
        a1.setId(2L);
        a1.setCorresponId(90L);
        a1.setFileId("2text.txt");
        a1.setFileName("test.txt");
        a1.setCreatedAt(new GregorianCalendar(2009, 3, 1, 9, 3, 23).getTime());
        a1.setCreatedBy(u);

        attachments.add(a1);

        MockAttachmentDao.RET_FIND_BY_CORRESPON_ID = attachments;

        // コレポン文書-カスタムフィールド
        CorresponCustomField ccf = new CorresponCustomField();
        ccf.setId(99L);
        ccf.setCorresponId(90L);
        ccf.setProjectCustomFieldId(new Long(1));
        ccf.setValue("This is customField1Value.");
        ccf.setCreatedAt(new GregorianCalendar(2009, 3, 1, 9, 3, 23).getTime());
        ccf.setCreatedBy(u);

        MockCorresponCustomFieldDao.RET_FIND_BY_ID = ccf;

        // 宛先-ユーザー
        List<AddressUser> addressUsers = new ArrayList<AddressUser>();
        AddressUser au = new AddressUser();
        au.setId(99L);
        au.setAddressCorresponGroupId(90L);
        User u2 = new User();
        u2.setEmpNo("00001");
        u2.setNameE("Test User");
        au.setUser(u2);
        au.setAddressUserType(AddressUserType.ATTENTION);
        au.setCreatedAt(new GregorianCalendar(2009, 3, 1, 9, 3, 23).getTime());
        au.setCreatedBy(u);

        addressUsers.add(au);

        MockAddressUserDao.RET_FIND_BY_ADDRESS_CORRESPON_GROUP_ID = addressUsers;

        // 宛先-活動単位
        List<AddressCorresponGroup> addressCorresponGroups = new ArrayList<AddressCorresponGroup>();
        AddressCorresponGroup acg = new AddressCorresponGroup();
        acg.setId(99L);
        acg.setCorresponId(90L);
        CorresponGroup cg = new CorresponGroup();
        cg.setId(new Long(1));
        acg.setCorresponGroup(cg);
        acg.setAddressType(AddressType.TO);
        acg.setCreatedAt(new GregorianCalendar(2009, 3, 1, 9, 3, 23).getTime());
        acg.setCreatedBy(u);

        addressCorresponGroups.add(acg);

        MockAddressCorresponGroupDao.RET_FIND_BY_CORRESPON_ID = addressCorresponGroups;

        // 担当
        List<PersonInCharge> personInCharges = new ArrayList<PersonInCharge>();
        PersonInCharge pic = new PersonInCharge();
        pic.setId(99L);
        pic.setAddressUserId(90L);
        pic.setUser(u2);
        pic.setCreatedAt(new GregorianCalendar(2009, 3, 1, 9, 3, 23).getTime());
        pic.setCreatedBy(u);

        personInCharges.add(pic);

        MockPersonInChargeDao.RET_FIND_BY_ADDRESS_USER_ID = personInCharges;

        // 承認フロー
        Workflow w;
        User user;
        List<Workflow> wfs = new ArrayList<Workflow>();

        w = new Workflow();
        w.setId(new Long(99));
        w.setCorresponId(new Long(90));
        w.setWorkflowNo(new Long(1));
        w.setVersionNo(1L);

        user = new User();
        user.setEmpNo("80001");
        user.setNameE("Checker1 User");
        w.setUser(user);
        w.setCreatedBy(user);
        w.setUpdatedBy(user);

        w.setWorkflowType(WorkflowType.CHECKER);
        w.setWorkflowProcessStatus(WorkflowProcessStatus.NONE);

        wfs.add(w);

        w = new Workflow();
        w.setId(new Long(90));
        w.setCorresponId(new Long(90));
        w.setWorkflowNo(new Long(2));
        w.setVersionNo(1L);

        user = new User();
        user.setEmpNo("80002");
        user.setNameE("Approver User");
        w.setUser(user);
        w.setCreatedBy(user);
        w.setUpdatedBy(user);

        w.setWorkflowType(WorkflowType.APPROVER);
        w.setWorkflowProcessStatus(WorkflowProcessStatus.NONE);

        wfs.add(w);

        MockWorkflowDao.RET_FIND_BY_CORRESPON_ID = wfs;

    }

    /**
     * コレポン文書更新処理に必要なデータを作成する(マスタ).
     *
     * @return
     */
    private void createMasterForUpdateData() {

        //
        CorresponGroup corresponGroup = new CorresponGroup();
        corresponGroup.setId(new Long(2));
        corresponGroup.setName("testGroup02");
        corresponGroup.setProjectId("PJ1");

        MockAbstractDao.RET_FIND_BY_ID.put("1", corresponGroup);

        //
        List<CorresponGroup> corresponGroups = new ArrayList<CorresponGroup>();

        corresponGroup = new CorresponGroup();
        corresponGroup.setId(new Long(1));
        corresponGroup.setName("testGroup");

        corresponGroups.add(corresponGroup);

        corresponGroup = new CorresponGroup();
        corresponGroup.setId(new Long(2));
        corresponGroup.setName("testGroup02");

        corresponGroups.add(corresponGroup);

        MockCorresponGroupListDao.RET_FIND_BY_CORRESPON_ID = corresponGroups;

        List<ProjectUser> userList = new ArrayList<ProjectUser>();
        ProjectUser projectUser = new ProjectUser();
        user = new User();
        user.setEmpNo("00001");
        user.setNameE("test");

        projectUser.setUser(user);
        projectUser.setProjectId("PJ1");
        userList.add(projectUser);

        projectUser = new ProjectUser();
        user = new User();
        user.setEmpNo("00002");
        user.setNameE("test2");

        projectUser.setUser(user);
        projectUser.setProjectId("PJ1");
        userList.add(projectUser);

        projectUser = new ProjectUser();
        user = new User();
        user.setEmpNo("00003");
        user.setNameE("test3");

        projectUser.setUser(user);
        projectUser.setProjectId("PJ1");
        userList.add(projectUser);

        MockProjectUserDao.RET_FIND_BY_CORRESPON_ID = userList;

        CorresponType corresponType = new CorresponType();
        corresponType.setId(new Long(2));
        corresponType.setProjectId("PJ1");
        corresponType.setProjectCorresponTypeId(new Long(20));

        MockAbstractDao.RET_FIND_BY_ID.put("2", corresponType);

        CustomField customField = new CustomField();
        customField.setId(new Long(1));
        customField.setProjectId("PJ1");

        MockAbstractDao.RET_FIND_BY_ID.put("3", customField);
        MockAbstractDao.RET_FIND_BY_ID.put("4", customField);
        MockAbstractDao.RET_FIND_BY_ID.put("5", customField);
        MockAbstractDao.RET_FIND_BY_ID.put("6", customField);
        MockAbstractDao.RET_FIND_BY_ID.put("7", customField);
        MockAbstractDao.RET_FIND_BY_ID.put("8", customField);
        MockAbstractDao.RET_FIND_BY_ID.put("9", customField);
        MockAbstractDao.RET_FIND_BY_ID.put("10", customField);
        MockAbstractDao.RET_FIND_BY_ID.put("11", customField);
        MockAbstractDao.RET_FIND_BY_ID.put("12", customField);

    }

   /**
     * 指定されたコレポン文書を保存する 新規登録 成功
     */
    @Test
    public void testSave01() throws Exception {

        createMasterForUpdateData();

        // 期待値をセットする
        createDummyForCreate01();

        Correspon correspon = new Correspon();

        CorresponGroup corresponGroup = new CorresponGroup();
        corresponGroup.setId(new Long(2));

        CorresponType corresponType = new CorresponType();
        corresponType.setId(new Long(2));
        corresponType.setProjectId("PJ1");
        corresponType.setProjectCorresponTypeId(new Long(20));

        MockCorresponTypeDao.RET_FIND_BY_PROJECT_CORRESPON_TYPE_ID = corresponType;

        User user = new User();
        user.setEmpNo("00001");
        user.setNameE("Test User");



        // コレポン文書情報
        correspon.setCorresponNo(null);
        correspon.setParentCorresponId(null);
        correspon.setProjectId("PJ1");
        correspon.setProjectNameE("TestProject");
        correspon.setReplyAddressUserId(9876L);

        correspon.setFromCorresponGroup(corresponGroup);
        correspon.setPreviousRevCorresponId(943L);
        correspon.setCorresponType(corresponType);
        correspon.setSubject("TestData");
        correspon.setBody("Testです");
        correspon.setIssuedAt(null);
        correspon.setCorresponStatus(CorresponStatus.OPEN);
        correspon.setReplyRequired(ReplyRequired.YES);
        correspon.setDeadlineForReply(new GregorianCalendar(2009, 3, 3).getTime());
        correspon.setWorkflowStatus(WorkflowStatus.DRAFT);
        correspon.setAddressCorresponGroups(MockAbstractDao.RET_CREATE_ADDRESSCORRESPONGROUP);
        correspon.setCreatedBy(MockAbstractService.CURRENT_USER);
        correspon.setUpdatedBy(MockAbstractService.CURRENT_USER);

        String path = SystemConfig.getValue(KEY_FILE_DIR_PATH);
        File file = new File(path + "upload_File1.xls");
        file.createNewFile();
        file = new File(path + "upload_File2.xls");
        file.createNewFile();

        correspon.setFile1FileName("File1.xls");

        correspon.setFile2FileName("File2.xls");

        correspon.setCustomField1Value("CustomTest1");

        MockAbstractDao.EDITMODE = MockAbstractDao.DEFAULT_MODE;

        assertNotNull(service.save(correspon));
    }

    /**
     * 指定されたコレポン文書を保存する 返信情報 成功
     */
    @Test
    public void testSave02() throws Exception {

        createMasterForUpdateData();

        // 期待値をセットする
        createDummyForCreate02();

        // コレポン文書情報
        Correspon correspon = new Correspon();

        correspon.setProjectId("PJ1");
        correspon.setProjectNameE("TestProject");

        CorresponGroup corresponGroup = new CorresponGroup();
        corresponGroup.setId(new Long(2));

        CorresponType corresponType = new CorresponType();
        corresponType.setId(new Long(2));
        corresponType.setProjectId("PJ1");
        corresponType.setProjectCorresponTypeId(new Long(20));

        MockCorresponTypeDao.RET_FIND_BY_PROJECT_CORRESPON_TYPE_ID = corresponType;

        User user = new User();
        user.setEmpNo("00001");
        user.setNameE("Test User");

        correspon.setFromCorresponGroup(corresponGroup);
        correspon.setCorresponType(corresponType);
        correspon.setSubject("TestData");
        correspon.setBody("Testです");
        correspon.setIssuedAt(null);
        correspon.setCorresponStatus(CorresponStatus.OPEN);
        correspon.setDeadlineForReply(new GregorianCalendar(2009, 3, 3).getTime());
        correspon.setWorkflowStatus(WorkflowStatus.ISSUED);
        correspon.setCreatedBy(MockAbstractService.CURRENT_USER);
        correspon.setUpdatedBy(MockAbstractService.CURRENT_USER);
        correspon.setReplyAddressUserId(9876L);
        correspon.setPreviousRevCorresponId(943L);
        correspon.setReplyAddressUserId(9876L);
        correspon.setReplyRequired(ReplyRequired.YES);

        String path = SystemConfig.getValue(KEY_FILE_DIR_PATH);
        File file = new File(path + "upload_File1.xls");
        file.createNewFile();
        file = new File(path + "upload_File2.xls");
        file.createNewFile();

        correspon.setFile1FileName("File1.xls");

        correspon.setFile2FileName("File2.xls");

        correspon.setCustomField1Value("CustomTest1");
        correspon.setCustomField2Value("PJ1");

        correspon.setAddressCorresponGroups(MockAbstractDao.RET_CREATE_ADDRESSCORRESPONGROUP);

        correspon.setParentCorresponId(new Long(100));

        MockAbstractDao.EDITMODE = MockAbstractDao.DEFAULT_MODE;

        assertNotNull(service.save(correspon));

    }

    /**
     * Preparerがコレポン文書を更新する 成功.
     * ワークフローは更新する.
     */
    @Test
    public void testSave04() throws Exception {

        createMasterForUpdateData();

        // テストに必要な値をセットする
        createRequestForUpdateData5();

        // 期待値をセットする
        createDummyForUpdate04();

        Correspon correspon = new Correspon();

        User user = new User();
        user.setEmpNo("ZZA01");
        user.setNameE("Test User");

        // コレポン文書情報
        correspon.setId(new Long(90));
        correspon.setProjectId("PJ1");
        correspon.setReplyAddressUserId(9876L);
        correspon.setReplyRequired(ReplyRequired.YES);

        CorresponGroup corresponGroup = new CorresponGroup();
        corresponGroup.setId(new Long(1));
        correspon.setFromCorresponGroup(corresponGroup);

        CorresponType corresponType = new CorresponType();
        corresponType.setId(new Long(1));
        corresponType.setProjectId(correspon.getProjectId());
        corresponType.setProjectCorresponTypeId(new Long(1));
        MockCorresponTypeDao.RET_FIND_BY_PROJECT_CORRESPON_TYPE_ID = corresponType;
        correspon.setCorresponType(corresponType);

        correspon.setSubject("更新後SUBJECT");
        correspon.setBody("更新後BODY");

        correspon.setCorresponStatus(CorresponStatus.OPEN);
        correspon.setDeadlineForReply(new GregorianCalendar(2009, 3, 3).getTime());
        correspon.setWorkflowStatus(WorkflowStatus.DENIED);

        correspon.setCreatedBy(user);
        correspon.setUpdatedBy(MockAbstractService.CURRENT_USER);

        String path = SystemConfig.getValue(KEY_FILE_DIR_PATH);
        File file = new File(path + File.separatorChar + "upload_File1.xls");
        file.createNewFile();
        file = new File(path + File.separatorChar + "upload_File2.xls");
        file.createNewFile();

        correspon.setFile1FileName("upload.xls");

        correspon.setFile2FileName("test.txt");

        correspon.setFile3FileName("File.xls");

        correspon.setCustomField1Value("PJ1");
        correspon.setCustomField2Value("PJ2");

        correspon.setAddressCorresponGroups(MockAbstractDao.RET_CREATE_ADDRESSCORRESPONGROUP);

        Workflow w;
        user = new User();
        List<Workflow> wfs = new ArrayList<Workflow>();

        w = new Workflow();
        user = new User();
        user.setEmpNo(DBValue.STRING_NULL);
        w.setFinishedBy(user);
        w.setCorresponId(correspon.getId());
        w.setWorkflowProcessStatus(WorkflowProcessStatus.NONE);

        wfs.add(w);

        correspon.setWorkflows(wfs);

        // Exception初期化
        MockAbstractDao.EXCEPTION = false;
        MockAbstractDao.ExceptionWorkflow = false;
        MockAbstractDao.EDITMODE = MockAbstractDao.UPDATE_MODE;
        MockAbstractDao.countElement = 0;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";

        assertEquals(correspon.getId(),service.save(correspon));

    }

    /**
     * System Adminがコレポン文書を更新する 成功.
     * ワークフローを更新する.
     */
    @Test
    public void testSave05() throws Exception {
        MockUser.IS_SYSTEM_ADMIN = true;
        MockAbstractService.IS_SYSTEM_ADMIN = true;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        MockAbstractService.IS_ANY_GROUP_ADMIN = false;

        User loginUser = new User();
        loginUser.setEmpNo("ZZA01");
        loginUser.setNameE("Current User");
        MockAbstractService.CURRENT_USER = loginUser;

        ProjectUser pUser = new ProjectUser();
        pUser.setUser(loginUser);
        MockAbstractService.CURRENT_PROJECT_USER = pUser;

        createMasterForUpdateData();

        // テストに必要な値をセットする
        createRequestForUpdateData5();

        // 期待値をセットする
        createDummyForUpdate05();

        Correspon correspon = new Correspon();

        User user = new User();
        user.setEmpNo("00001");
        user.setNameE("Test User");

        // コレポン文書情報
        correspon.setId(new Long(90));
        correspon.setProjectId("PJ1");

        CorresponGroup corresponGroup = new CorresponGroup();
        corresponGroup.setId(new Long(1));
        correspon.setFromCorresponGroup(corresponGroup);

        CorresponType corresponType = new CorresponType();
        corresponType.setId(new Long(1));
        corresponType.setProjectId(correspon.getProjectId());
        corresponType.setProjectCorresponTypeId(new Long(1));
        MockCorresponTypeDao.RET_FIND_BY_PROJECT_CORRESPON_TYPE_ID = corresponType;
        correspon.setCorresponType(corresponType);

        correspon.setSubject("更新後SUBJECT");
        correspon.setBody("更新後BODY");

        correspon.setCorresponStatus(CorresponStatus.OPEN);
        correspon.setDeadlineForReply(new GregorianCalendar(2009, 3, 3).getTime());
        correspon.setWorkflowStatus(WorkflowStatus.DENIED);

        correspon.setCreatedBy(user);
        correspon.setUpdatedBy(MockAbstractService.CURRENT_USER);
        correspon.setReplyAddressUserId(9876L);
        correspon.setReplyRequired(ReplyRequired.YES);

        String path = SystemConfig.getValue(KEY_FILE_DIR_PATH);
        File file = new File(path + File.separatorChar + "upload_File1.xls");
        file.createNewFile();
        file = new File(path + File.separatorChar + "upload_File2.xls");
        file.createNewFile();

        correspon.setFile1FileName("upload.xls");

        correspon.setFile2FileName("test.txt");

        correspon.setFile3FileName("File.xls");

        correspon.setCustomField1Value(null);
        correspon.setCustomField2Value("PJ1");

        correspon.setAddressCorresponGroups(MockAbstractDao.RET_CREATE_ADDRESSCORRESPONGROUP);

        Workflow w;
        user = new User();
        List<Workflow> wfs = new ArrayList<Workflow>();

        w = new Workflow();
        user = new User();
        user.setEmpNo(DBValue.STRING_NULL);
        w.setFinishedBy(user);
        w.setCorresponId(correspon.getId());
        w.setWorkflowProcessStatus(WorkflowProcessStatus.NONE);

        wfs.add(w);

        correspon.setWorkflows(wfs);

        // Exception初期化
        MockAbstractDao.EXCEPTION = false;
        MockAbstractDao.ExceptionWorkflow = false;
        MockAbstractDao.EDITMODE = MockAbstractDao.UPDATE_MODE;
        MockAbstractDao.countElement = 0;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";

        assertEquals(correspon.getId(), service.save(correspon));
    }

    /**
     * System Adminがコレポン文書を更新する 成功.
     * ワークフローを更新しない.
     */
    @Test
    public void testSave06() throws Exception {
        MockUser.IS_SYSTEM_ADMIN = true;
        MockAbstractService.IS_SYSTEM_ADMIN = true;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        MockAbstractService.IS_ANY_GROUP_ADMIN = false;

        User loginUser = new User();
        loginUser.setEmpNo("ZZA02");
        loginUser.setNameE("Current User");
        MockAbstractService.CURRENT_USER = loginUser;

        ProjectUser pUser = new ProjectUser();
        pUser.setUser(loginUser);
        MockAbstractService.CURRENT_PROJECT_USER = pUser;

        createMasterForUpdateData();

        // テストに必要な値をセットする
        createRequestForUpdateData10();

        // 期待値をセットする
        createDummyForUpdate06();

        Correspon correspon = new Correspon();

        User user = new User();
        user.setEmpNo("ZZA01");
        user.setNameE("Test User");

        // コレポン文書情報
        correspon.setId(new Long(90));
        correspon.setProjectId("PJ1");

        CorresponGroup corresponGroup = new CorresponGroup();
        corresponGroup.setId(new Long(1));
        correspon.setFromCorresponGroup(corresponGroup);

        CorresponType corresponType = new CorresponType();
        corresponType.setId(new Long(1));
        corresponType.setProjectId(correspon.getProjectId());
        corresponType.setProjectCorresponTypeId(new Long(1));
        MockCorresponTypeDao.RET_FIND_BY_PROJECT_CORRESPON_TYPE_ID = corresponType;
        correspon.setCorresponType(corresponType);

        correspon.setSubject("更新後SUBJECT");
        correspon.setBody("更新後BODY");
        correspon.setReplyAddressUserId(9876L);
        correspon.setReplyRequired(ReplyRequired.NO);

        correspon.setCorresponStatus(CorresponStatus.OPEN);
        correspon.setDeadlineForReply(new GregorianCalendar(2009, 3, 3).getTime());
        correspon.setWorkflowStatus(WorkflowStatus.UNDER_CONSIDERATION);

        correspon.setCreatedBy(user);
        correspon.setUpdatedBy(user);

        String path = SystemConfig.getValue(KEY_FILE_DIR_PATH);
        File file = new File(path + File.separatorChar + "upload_File1.xls");
        file.createNewFile();
        file = new File(path + File.separatorChar + "upload_File2.xls");
        file.createNewFile();

        correspon.setFile1FileName("upload.xls");

        correspon.setFile2FileName("test.txt");

        correspon.setFile3FileName("File.xls");

        correspon.setCustomField1Value(null);
        correspon.setCustomField2Value(null);

        correspon.setAddressCorresponGroups(MockAbstractDao.RET_CREATE_ADDRESSCORRESPONGROUP);

        Workflow w;
        user = new User();
        List<Workflow> wfs = new ArrayList<Workflow>();

        w = new Workflow();
        w.setCorresponId(correspon.getId());
        w.setWorkflowProcessStatus(WorkflowProcessStatus.NONE);
        w.setUser(user);

        wfs.add(w);

        correspon.setWorkflows(wfs);

        // Exception初期化
        MockAbstractDao.EXCEPTION = false;
        MockAbstractDao.ExceptionWorkflow = false;
        MockAbstractDao.EDITMODE = MockAbstractDao.UPDATE_MODE;
        MockAbstractDao.countElement = 0;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";

        assertEquals(service.save(correspon), correspon.getId());

    }

    /**
     * Checkerがコレポン文書を更新する 成功.
     * ワークフローを更新する.
     */
    @Test
    public void testSave07() throws Exception {

        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        MockAbstractService.IS_ANY_GROUP_ADMIN = false;

        User loginUser = new User();
        loginUser.setEmpNo("80001");
        loginUser.setNameE("Checker1 User");
        MockAbstractService.CURRENT_USER = loginUser;

        ProjectUser pUser = new ProjectUser();
        pUser.setUser(loginUser);
        MockAbstractService.CURRENT_PROJECT_USER = pUser;

        createMasterForUpdateData();

        // テストに必要な値をセットする
        createRequestForUpdateData11();

        // 期待値をセットする
        createDummyForUpdate07();

        Correspon correspon = new Correspon();

        User user = new User();
        user.setEmpNo("ZZA01");
        user.setNameE("Test User");

        // コレポン文書情報
        correspon.setId(new Long(90));
        correspon.setProjectId("PJ1");

        CorresponGroup corresponGroup = new CorresponGroup();
        corresponGroup.setId(new Long(1));
        correspon.setFromCorresponGroup(corresponGroup);

        CorresponType corresponType = new CorresponType();
        corresponType.setId(new Long(1));
        corresponType.setProjectId(correspon.getProjectId());
        corresponType.setProjectCorresponTypeId(new Long(1));
        MockCorresponTypeDao.RET_FIND_BY_PROJECT_CORRESPON_TYPE_ID = corresponType;
        correspon.setCorresponType(corresponType);
        correspon.setReplyAddressUserId(9876L);
        correspon.setReplyRequired(ReplyRequired.NO);

        correspon.setSubject("更新後SUBJECT");
        correspon.setBody("更新後BODY");

        correspon.setCorresponStatus(CorresponStatus.OPEN);
        correspon.setDeadlineForReply(new GregorianCalendar(2009, 3, 3).getTime());
        correspon.setWorkflowStatus(WorkflowStatus.REQUEST_FOR_CHECK);

        correspon.setCreatedBy(user);
        correspon.setUpdatedBy(user);
        correspon.setVersionNo(1L);

        String path = SystemConfig.getValue(KEY_FILE_DIR_PATH);
        File file = new File(path + File.separatorChar + "upload_File1.xls");
        file.createNewFile();
        file = new File(path + File.separatorChar + "upload_File2.xls");
        file.createNewFile();

        correspon.setFile1FileName("upload.xls");

        correspon.setFile2FileName("test.txt");

        correspon.setFile3FileName("File.xls");

        correspon.setCustomField1Value("PJ1");
        correspon.setCustomField2Value("PJ2");

        correspon.setAddressCorresponGroups(MockAbstractDao.RET_CREATE_ADDRESSCORRESPONGROUP);

        Workflow w;
        user = new User();
        List<Workflow> wfs = new ArrayList<Workflow>();

        w = new Workflow();
        w.setId(new Long(99));
        w.setCorresponId(correspon.getId());
        w.setWorkflowNo(new Long(1));
        w.setVersionNo(1L);

        user = new User();
        user.setEmpNo("80001");
        user.setNameE("Checker1 User");
        w.setUser(user);
        w.setCreatedBy(user);
        w.setUpdatedBy(user);

        w.setWorkflowType(WorkflowType.CHECKER);
        w.setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_CHECK);

        wfs.add(w);

        w = new Workflow();
        w.setId(new Long(90));
        w.setCorresponId(correspon.getId());
        w.setWorkflowNo(new Long(2));
        w.setVersionNo(1L);

        user = new User();
        user.setEmpNo("80002");
        user.setNameE("Approver User");
        w.setUser(user);
        w.setCreatedBy(user);
        w.setUpdatedBy(user);

        w.setWorkflowType(WorkflowType.APPROVER);
        w.setWorkflowProcessStatus(WorkflowProcessStatus.NONE);

        wfs.add(w);

        correspon.setWorkflows(wfs);

        // Exception初期化
        MockAbstractDao.EXCEPTION = false;
        MockAbstractDao.ExceptionWorkflow = false;
        MockAbstractDao.EDITMODE = MockAbstractDao.UPDATE_MODE;
        MockAbstractDao.countElement = 0;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";

        assertEquals(service.save(correspon), correspon.getId());

    }

    /**
     * Approverがコレポン文書を更新する 成功.
     * ワークフローを更新する.
     */
    @Test
    public void testSave08() throws Exception {
        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        MockAbstractService.IS_ANY_GROUP_ADMIN = false;

        User loginUser = new User();
        loginUser.setEmpNo("80002");
        loginUser.setNameE("Approver User");
        MockAbstractService.CURRENT_USER = loginUser;

        ProjectUser pUser = new ProjectUser();
        pUser.setUser(loginUser);
        MockAbstractService.CURRENT_PROJECT_USER = pUser;

        createMasterForUpdateData();

        // テストに必要な値をセットする
        createRequestForUpdateData12();

        // 期待値をセットする
        createDummyForUpdate08();

        Correspon correspon = new Correspon();

        User user = new User();
        user.setEmpNo("ZZA01");
        user.setNameE("Test User");

        // コレポン文書情報
        correspon.setId(new Long(90));
        correspon.setProjectId("PJ1");
        correspon.setReplyAddressUserId(9876L);
        correspon.setReplyRequired(ReplyRequired.NO);

        CorresponGroup corresponGroup = new CorresponGroup();
        corresponGroup.setId(new Long(1));
        correspon.setFromCorresponGroup(corresponGroup);

        CorresponType corresponType = new CorresponType();
        corresponType.setId(new Long(1));
        corresponType.setProjectId(correspon.getProjectId());
        corresponType.setProjectCorresponTypeId(new Long(1));
        MockCorresponTypeDao.RET_FIND_BY_PROJECT_CORRESPON_TYPE_ID = corresponType;
        correspon.setCorresponType(corresponType);

        correspon.setSubject("更新後SUBJECT");
        correspon.setBody("更新後BODY");

        correspon.setCorresponStatus(CorresponStatus.OPEN);
        correspon.setDeadlineForReply(new GregorianCalendar(2009, 3, 3).getTime());
        correspon.setWorkflowStatus(WorkflowStatus.UNDER_CONSIDERATION);

        correspon.setCreatedBy(user);
        correspon.setUpdatedBy(user);
        correspon.setVersionNo(1L);

        String path = SystemConfig.getValue(KEY_FILE_DIR_PATH);
        File file = new File(path + File.separatorChar + "upload_File1.xls");
        file.createNewFile();
        file = new File(path + File.separatorChar + "upload_File2.xls");
        file.createNewFile();

        correspon.setFile1FileName("upload1.xls");

        correspon.setFile2FileName("upload2.txt");

        correspon.setCustomField1Value("PJ1Value");
        correspon.setCustomField2Value("PJ2Value");

        correspon.setAddressCorresponGroups(MockAbstractDao.RET_CREATE_ADDRESSCORRESPONGROUP);

        Workflow w;
        user = new User();
        List<Workflow> wfs = new ArrayList<Workflow>();

        w = new Workflow();
        w.setId(new Long(99));
        w.setCorresponId(correspon.getId());
        w.setWorkflowNo(new Long(1));
        w.setVersionNo(1L);

        user = new User();
        user.setEmpNo("80001");
        user.setNameE("Checker1 User");
        w.setUser(user);
        w.setCreatedBy(user);
        w.setUpdatedBy(user);

        w.setWorkflowType(WorkflowType.CHECKER);
        w.setWorkflowProcessStatus(WorkflowProcessStatus.CHECKED);

        wfs.add(w);

        w = new Workflow();
        w.setId(new Long(90));
        w.setCorresponId(correspon.getId());
        w.setWorkflowNo(new Long(2));
        w.setVersionNo(1L);

        user = new User();
        user.setEmpNo("80002");
        user.setNameE("Approver User");
        w.setUser(user);
        w.setCreatedBy(user);
        w.setUpdatedBy(user);

        w.setWorkflowType(WorkflowType.APPROVER);
        w.setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_APPROVAL);

        wfs.add(w);

        correspon.setWorkflows(wfs);

        // Exception初期化
        MockAbstractDao.EXCEPTION = false;
        MockAbstractDao.ExceptionWorkflow = false;
        MockAbstractDao.EDITMODE = MockAbstractDao.UPDATE_MODE;
        MockAbstractDao.countElement = 0;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";

        assertEquals(correspon.getId(), service.save(correspon));

    }

    /**
     * PICが登録された状態でコレポン文書を更新する 成功.
     */
    @Test
    public void testSave09() throws Exception {
        createMasterForUpdateData();

        MockAbstractService.IS_SYSTEM_ADMIN = true;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        MockAbstractService.IS_ANY_GROUP_ADMIN = false;

        User loginUser = new User();
        loginUser.setEmpNo("80002");
        loginUser.setNameE("Approver User");
        MockAbstractService.CURRENT_USER = loginUser;

        ProjectUser pUser = new ProjectUser();
        pUser.setUser(loginUser);
        MockAbstractService.CURRENT_PROJECT_USER = pUser;
        CorresponGroup cg1 = new CorresponGroup();
        cg1.setId(2L);
        cg1.setName("GRP:A01");

        CorresponGroup cg = cg1;

        Correspon correspon = new Correspon();
        correspon.setId(100L); // 更新にするためidをセットする
        correspon.setCreatedBy(loginUser);
        correspon.setWorkflowStatus(WorkflowStatus.ISSUED);
        correspon.setCorresponStatus(CorresponStatus.OPEN);
        correspon.setFromCorresponGroup(cg);
        correspon.setSubject("PICが設定された状態でコレポン文書を更新");
        correspon.setBody("****本文****");
        correspon.setReplyRequired(ReplyRequired.NO);
        correspon.setUpdatedBy(loginUser);
        correspon.setVersionNo(1L);
        correspon.setProjectId("PJ1");

        User picUser = new User();
        picUser.setEmpNo("TEST_01");

        User createdBy = new User();
        createdBy.setEmpNo("CREATE_01");
        createdBy.setNameE("Creater_01");

        User updatedBy = new User();
        updatedBy.setEmpNo("UPDATE_01");
        updatedBy.setNameE("Updater_01");

        User attention1 = new User();
        attention1.setEmpNo("ATTENTION_01");
        attention1.setNameE("Attention_01");

        CorresponType ct = new CorresponType();
        ct.setId(1L);
        ct.setCorresponType("P1");
        correspon.setCorresponType(ct);

        List<AddressCorresponGroup> acgs = new ArrayList<AddressCorresponGroup>();
        AddressCorresponGroup acg = new AddressCorresponGroup();
        acg.setId(10L);
        acg.setAddressType(AddressType.TO);
        acg.setMode(UpdateMode.UPDATE);
        acgs.add(acg);
        correspon.setAddressCorresponGroups(acgs);

        List<AddressUser> aus = new ArrayList<AddressUser>();
        AddressUser au1 = new AddressUser();
        au1.setId(1000L);
        au1.setAddressUserType(AddressUserType.ATTENTION);
        au1.setAddressCorresponGroupId(10L);
        au1.setUser(attention1);
        aus.add(au1);
        acg.setUsers(aus);

        CorresponCustomField ccf = new CorresponCustomField();
        ccf.setCorresponId(100L);
        ccf.setUpdatedBy(loginUser);
        MockCorresponCustomFieldDao.RET_UPDATE_CORRESPONCUSTOMFIELD = ccf;

        AddressUser expectAu = new AddressUser();
        expectAu.setAddressCorresponGroupId(10L);
        expectAu.setUpdatedBy(loginUser);
        MockAddressUserDao.RET_UPDATE_ADDRESSUSER = expectAu;

        List<AddressUser> exp_au = new ArrayList<AddressUser>();
        expectAu = new AddressUser();
        expectAu.setAddressCorresponGroupId(acg.getId());
        expectAu.setAddressUserType(AddressUserType.ATTENTION);
        expectAu.setUser(attention1);
        expectAu.setCreatedBy(correspon.getCreatedBy());
        expectAu.setUpdatedBy(loginUser);
        exp_au.add(expectAu);
        MockAbstractDao.RET_CREATE_ADDRESSUSER = exp_au;

        List<PersonInCharge> pics1 = new ArrayList<PersonInCharge>();
        PersonInCharge pic1 = new PersonInCharge();
        pic1.setId(100L);
        pic1.setAddressUserId(1000L);
        pic1.setUser(picUser);
        pic1.setCreatedBy(createdBy);
        pic1.setUpdatedBy(loginUser);
        pics1.add(pic1);
        MockPersonInChargeDao.RET_FIND_BY_ADDRESS_USER_ID = pics1;

        PersonInCharge pic2 = (PersonInCharge) SerializationUtils.clone(pic1);
        pic2.setUpdatedBy(loginUser);
        MockPersonInChargeDao.RET_UPDATE_PERSONINCHARGE = pic2;

        List<PersonInCharge> exp_pic = new ArrayList<PersonInCharge>();
        PersonInCharge expect = new PersonInCharge();
        expect.setAddressUserId(99L);
        expect.setUser(picUser);
        expect.setCreatedBy(createdBy);
        expect.setUpdatedBy(loginUser);
        exp_pic.add(expect);
        MockAbstractDao.RET_CREATE_PERSONINCHARGE = exp_pic;

        Correspon expectCorrespon = new Correspon();
        expectCorrespon.setId(100L); // 更新にするためidをセットする
        expectCorrespon.setCreatedBy(null);
        expectCorrespon.setWorkflowStatus(null);
        expectCorrespon.setCorresponStatus(CorresponStatus.OPEN);
        expectCorrespon.setFromCorresponGroup(cg);
        expectCorrespon.setSubject("PICが設定された状態でコレポン文書を更新");
        expectCorrespon.setBody("****本文****");
        expectCorrespon.setReplyRequired(ReplyRequired.NO);
        expectCorrespon.setUpdatedBy(loginUser);
        expectCorrespon.setVersionNo(1L);
        expectCorrespon.setCorresponType(ct);
        expectCorrespon.setAddressCorresponGroups(acgs);
        MockAbstractDao.RET_UPDATE_CORRESPON = expectCorrespon;

        MockAddressCorresponGroupDao.RET_FIND_BY_CORRESPON_ID = acgs;

        assertEquals(correspon.getId(), service.save(correspon));
    }



    public static class MockAbstractDao<T extends Entity> {
        static Long RET_CREATE;
        static Integer RET_UPDATE;
        static Integer RET_DELETE;
        static boolean EXCEPTION = false;
        static boolean ExceptionWorkflow = false;
        static List<Workflow> RET_UPDATE_WORKFLOW;
        static Workflow RET_UPDATE_CORRESPON_WORKFLOW;
        static Correspon RET_UPDATE_CORRESPON;
        static List<Attachment> RET_UPDATE_ATTACHMENT;
        static Correspon RET_DELETE_CORRESPON;

        static List<Attachment> RET_CREATE_ATTACHMENT = new ArrayList<Attachment>();
        static List<CorresponCustomField> RET_CREATE_CORRESPONCUSTOMFIELD;
        static List<AddressUser> RET_CREATE_ADDRESSUSER;
        static List<AddressCorresponGroup> RET_CREATE_ADDRESSCORRESPONGROUP;
        static List<PersonInCharge> RET_CREATE_PERSONINCHARGE;
        static CorresponHierarchy RET_CREATE_HIERARCHY;

        static int EDITMODE;
        static final int UPDATE_MODE = 1;
        static final int DEFAULT_MODE = 0;

        static List<CorresponGroup> RET_FIND_BY_ID_CORRESPONGROUP;

        public MockAbstractDao() {

        }

        public MockAbstractDao(String namespace) {

        }

        public Integer update(T entity) throws KeyDuplicateException, StaleRecordException {
            if (EXCEPTION) {
                throw new KeyDuplicateException();
            }

            if (EDITMODE == UPDATE_MODE) {
                if (entity instanceof Correspon) {

                    checkCorresponFields(RET_UPDATE_CORRESPON, (Correspon) entity);

                }

                if (entity instanceof Workflow) {

                    checkWorkflowFields(RET_UPDATE_CORRESPON_WORKFLOW, (Workflow) entity);

                }

            } else {
                if (entity instanceof Workflow && ExceptionWorkflow) {
                    throw new KeyDuplicateException();
                }

                if (entity instanceof Workflow) {
                    RET_UPDATE_WORKFLOW.add((Workflow) entity);
                }

                if (entity instanceof Correspon) {
                    RET_UPDATE_CORRESPON = (Correspon) entity;
                }

            }

            return RET_UPDATE;
        }

        static int countElement = 0;
        static String ObjectName = null;
        static int CREATE_COUNT = 0;

        public Long create(T entity) throws KeyDuplicateException {
            CREATE_COUNT++;
            if (entity instanceof Correspon) {

                checkCorresponFields(RET_UPDATE_CORRESPON, (Correspon) entity);
                ObjectName = "Correspon";

            }

            if (entity instanceof Attachment) {
                if (!StringUtils.equals(ObjectName, "Attachment")) {
                  countElement = 0;
                }
                assertEquals(RET_CREATE_ATTACHMENT.get(countElement).getCorresponId(), ((Attachment) entity).getCorresponId());
                //assertEquals(RET_CREATE_ATTACHMENT.get(countElement).getFileId(), ((Attachment) entity).getFileId());
                assertEquals(RET_CREATE_ATTACHMENT.get(countElement).getFileName(), ((Attachment) entity).getFileName());
                assertEquals(RET_CREATE_ATTACHMENT.get(countElement).getCreatedBy().getEmpNo(), ((Attachment) entity).getCreatedBy().getEmpNo());
                assertEquals(RET_CREATE_ATTACHMENT.get(countElement).getCreatedBy().getNameE(), ((Attachment) entity).getCreatedBy().getNameE());
                assertEquals(RET_CREATE_ATTACHMENT.get(countElement).getUpdatedBy().getEmpNo(), ((Attachment) entity).getUpdatedBy().getEmpNo());
                assertEquals(RET_CREATE_ATTACHMENT.get(countElement).getUpdatedBy().getNameE(), ((Attachment) entity).getUpdatedBy().getNameE());
                countElement++;
                ObjectName = "Attachment";
            }

            if (entity instanceof CorresponCustomField) {
                if (!StringUtils.equals(ObjectName, "CorresponCustomField")) {
                    countElement = 0;
                }
                assertEquals(RET_CREATE_CORRESPONCUSTOMFIELD.get(countElement).getCorresponId(), ((CorresponCustomField) entity).getCorresponId());
                assertEquals(RET_CREATE_CORRESPONCUSTOMFIELD.get(countElement).getProjectCustomFieldId(), ((CorresponCustomField) entity).getProjectCustomFieldId());
                assertEquals(RET_CREATE_CORRESPONCUSTOMFIELD.get(countElement).getValue(), ((CorresponCustomField) entity).getValue());
                assertEquals(RET_CREATE_CORRESPONCUSTOMFIELD.get(countElement).getCreatedBy().getEmpNo(), ((CorresponCustomField) entity).getCreatedBy().getEmpNo());
                assertEquals(RET_CREATE_CORRESPONCUSTOMFIELD.get(countElement).getCreatedBy().getNameE(), ((CorresponCustomField) entity).getCreatedBy().getNameE());
                assertEquals(RET_CREATE_CORRESPONCUSTOMFIELD.get(countElement).getUpdatedBy().getEmpNo(), ((CorresponCustomField) entity).getUpdatedBy().getEmpNo());
                assertEquals(RET_CREATE_CORRESPONCUSTOMFIELD.get(countElement).getUpdatedBy().getNameE(), ((CorresponCustomField) entity).getUpdatedBy().getNameE());
                countElement++;
                ObjectName = "CorresponCustomField";
            }

            if (entity instanceof AddressUser) {
                if (!StringUtils.equals(ObjectName, "AddressUser")) {
                   countElement = 0;
                }
                assertEquals(RET_CREATE_ADDRESSUSER.get(countElement).getAddressCorresponGroupId(), ((AddressUser) entity).getAddressCorresponGroupId());
                assertEquals(RET_CREATE_ADDRESSUSER.get(countElement).getUser().getEmpNo(), ((AddressUser) entity).getUser().getEmpNo());
                assertEquals(RET_CREATE_ADDRESSUSER.get(countElement).getAddressUserType(), ((AddressUser) entity).getAddressUserType());
                assertEquals(RET_CREATE_ADDRESSUSER.get(countElement).getCreatedBy().getEmpNo(), ((AddressUser) entity).getCreatedBy().getEmpNo());
                assertEquals(RET_CREATE_ADDRESSUSER.get(countElement).getCreatedBy().getNameE(), ((AddressUser) entity).getCreatedBy().getNameE());
                assertEquals(RET_CREATE_ADDRESSUSER.get(countElement).getUpdatedBy().getEmpNo(), ((AddressUser) entity).getUpdatedBy().getEmpNo());
                assertEquals(RET_CREATE_ADDRESSUSER.get(countElement).getUpdatedBy().getNameE(), ((AddressUser) entity).getUpdatedBy().getNameE());
                countElement++;
                ObjectName = "AddressUser";
            }

            if (entity instanceof AddressCorresponGroup) {
                if (!StringUtils.equals(ObjectName, "AddressCorresponGroup")) {
                    countElement = 0;
                }
                assertEquals(RET_CREATE_ADDRESSCORRESPONGROUP.get(countElement).getCorresponId(), ((AddressCorresponGroup) entity).getCorresponId());
                assertEquals(RET_CREATE_ADDRESSCORRESPONGROUP.get(countElement).getCorresponGroup().getId(), ((AddressCorresponGroup) entity).getCorresponGroup().getId());
                assertEquals(RET_CREATE_ADDRESSCORRESPONGROUP.get(countElement).getAddressType(), ((AddressCorresponGroup) entity).getAddressType());
                assertEquals(RET_CREATE_ADDRESSCORRESPONGROUP.get(countElement).getCreatedBy().getEmpNo(), ((AddressCorresponGroup) entity).getCreatedBy().getEmpNo());
                assertEquals(RET_CREATE_ADDRESSCORRESPONGROUP.get(countElement).getCreatedBy().getNameE(), ((AddressCorresponGroup) entity).getCreatedBy().getNameE());
                assertEquals(RET_CREATE_ADDRESSCORRESPONGROUP.get(countElement).getUpdatedBy().getEmpNo(), ((AddressCorresponGroup) entity).getUpdatedBy().getEmpNo());
                assertEquals(RET_CREATE_ADDRESSCORRESPONGROUP.get(countElement).getUpdatedBy().getNameE(), ((AddressCorresponGroup) entity).getUpdatedBy().getNameE());
                countElement++;
                ObjectName = "AddressCorresponGroup";
            }

            if (entity instanceof PersonInCharge) {
//                fail("今、このテーブルは保存しません");
                if (!StringUtils.equals(ObjectName, "PersonInCharge")) {
                    countElement = 0;
                }
                assertEquals(RET_CREATE_PERSONINCHARGE.get(countElement).getAddressUserId(), ((PersonInCharge) entity).getAddressUserId());
                assertEquals(RET_CREATE_PERSONINCHARGE.get(countElement).getUser().getEmpNo(), ((PersonInCharge) entity).getUser().getEmpNo());
                assertEquals(RET_CREATE_PERSONINCHARGE.get(countElement).getCreatedBy().getEmpNo(), ((PersonInCharge) entity).getCreatedBy().getEmpNo());
                assertEquals(RET_CREATE_PERSONINCHARGE.get(countElement).getCreatedBy().getNameE(), ((PersonInCharge) entity).getCreatedBy().getNameE());
                assertEquals(RET_CREATE_PERSONINCHARGE.get(countElement).getUpdatedBy().getEmpNo(), ((PersonInCharge) entity).getUpdatedBy().getEmpNo());
                assertEquals(RET_CREATE_PERSONINCHARGE.get(countElement).getUpdatedBy().getNameE(), ((PersonInCharge) entity).getUpdatedBy().getNameE());
                countElement++;
                ObjectName = "PersonInCharge";
            }

            if (entity instanceof CorresponHierarchy) {
                assertEquals(RET_CREATE_HIERARCHY.getParentCorresponId(), ((CorresponHierarchy) entity).getParentCorresponId());
                assertEquals(RET_CREATE_HIERARCHY.getChildCorresponId(), ((CorresponHierarchy) entity).getChildCorresponId());
                assertEquals(RET_CREATE_HIERARCHY.getReplyAddressUserId(), ((CorresponHierarchy) entity).getReplyAddressUserId());
                assertEquals(RET_CREATE_HIERARCHY.getCreatedBy().getEmpNo(), ((CorresponHierarchy) entity).getCreatedBy().getEmpNo());
                assertEquals(RET_CREATE_HIERARCHY.getCreatedBy().getNameE(), ((CorresponHierarchy) entity).getCreatedBy().getNameE());
                assertEquals(RET_CREATE_HIERARCHY.getUpdatedBy().getEmpNo(), ((CorresponHierarchy) entity).getUpdatedBy().getEmpNo());
                assertEquals(RET_CREATE_HIERARCHY.getUpdatedBy().getNameE(), ((CorresponHierarchy) entity).getUpdatedBy().getNameE());
                ObjectName = "CorresponHierarchy";

            }

            RET_CREATE = new Long(99);
            return RET_CREATE;
        }

        public Integer delete(T entity) throws KeyDuplicateException, StaleRecordException {

            if (EDITMODE == UPDATE_MODE) {
                if (entity instanceof Attachment) {
                    assertEquals(RET_UPDATE_ATTACHMENT.get(countElement).getId(), ((Attachment) entity).getId());
                    assertEquals(RET_UPDATE_ATTACHMENT.get(countElement).getUpdatedBy().getEmpNo(), ((Attachment) entity).getUpdatedBy().getEmpNo());
                    assertEquals(RET_UPDATE_ATTACHMENT.get(countElement).getUpdatedBy().getNameE(), ((Attachment) entity).getUpdatedBy().getNameE());
                    countElement++;
                }

            } else {

                if (entity instanceof Correspon) {
                    assertEquals(RET_DELETE_CORRESPON.getId(), ((Correspon) entity).getId());
                    assertEquals(RET_DELETE_CORRESPON.getUpdatedBy().getEmpNo(), ((Correspon) entity).getUpdatedBy().getEmpNo());
                    assertEquals(RET_DELETE_CORRESPON.getUpdatedBy().getNameE(), ((Correspon) entity).getUpdatedBy().getNameE());
                }

            }

            return RET_DELETE;
        }
        static Map<String, Object> RET_FIND_BY_ID = new HashMap<String, Object>();

        static int count = 0;

        @SuppressWarnings("unchecked")
        public T findById(Long id) throws RecordNotFoundException {

            if (RET_FIND_BY_ID_CORRESPONGROUP == null) {
                count++;
                if (RET_FIND_BY_ID.get(String.valueOf(count)) == null) {
                    throw new RecordNotFoundException();
                }
                return (T) RET_FIND_BY_ID.get(String.valueOf(count));
            } else {
                return (T) RET_FIND_BY_ID_CORRESPONGROUP.get(0);
            }
        }

        private void checkCorresponFields(Correspon expectCorrespon, Correspon entryCorrespon) {

            assertEquals(expectCorrespon.getId(), entryCorrespon.getId());
            assertEquals(expectCorrespon.getCorresponNo(), entryCorrespon.getCorresponNo());
            assertEquals(expectCorrespon.getParentCorresponId(), entryCorrespon.getParentCorresponId());
            assertEquals(expectCorrespon.getProjectId(), entryCorrespon.getProjectId());
            assertEquals(expectCorrespon.getProjectNameE(), entryCorrespon.getProjectNameE());
            assertEquals(expectCorrespon.getFromCorresponGroup().getId(), entryCorrespon.getFromCorresponGroup().getId());
            assertEquals(expectCorrespon.getFromCorresponGroup().getName(), entryCorrespon.getFromCorresponGroup().getName());
            assertEquals(expectCorrespon.getPreviousRevCorresponId(), entryCorrespon.getPreviousRevCorresponId());
            assertEquals(expectCorrespon.getCorresponType().getId(), entryCorrespon.getCorresponType().getId());
            assertEquals(expectCorrespon.getCorresponType().getName(), entryCorrespon.getCorresponType().getName());
            assertEquals(expectCorrespon.getSubject(), entryCorrespon.getSubject());
            assertEquals(expectCorrespon.getBody(), entryCorrespon.getBody());
            assertEquals(expectCorrespon.getIssuedAt(), entryCorrespon.getIssuedAt());
            assertEquals(expectCorrespon.getCorresponStatus().getValue(), entryCorrespon.getCorresponStatus().getValue());
            assertEquals(expectCorrespon.getReplyRequired(), entryCorrespon.getReplyRequired());
            assertEquals(expectCorrespon.getDeadlineForReply(), entryCorrespon.getDeadlineForReply());
            if (expectCorrespon.getWorkflowStatus() == null) {
                assertNull(entryCorrespon.getWorkflowStatus());
            } else {
                System.out.println(entryCorrespon.getWorkflowStatus());
                System.out.println(entryCorrespon.getWorkflowStatus().getValue());
                assertEquals(expectCorrespon.getWorkflowStatus().getValue(), entryCorrespon.getWorkflowStatus().getValue());
            }
            assertEquals(expectCorrespon.getCustomField1Id(), entryCorrespon.getCustomField1Id());
            assertEquals(expectCorrespon.getCustomField1Label(), entryCorrespon.getCustomField1Label());
            assertEquals(expectCorrespon.getCustomField1Value(), entryCorrespon.getCustomField1Value());
            assertEquals(expectCorrespon.getCustomField2Id(), entryCorrespon.getCustomField2Id());
            assertEquals(expectCorrespon.getCustomField2Label(), entryCorrespon.getCustomField2Label());
            assertEquals(expectCorrespon.getCustomField2Value(), entryCorrespon.getCustomField2Value());
            assertEquals(expectCorrespon.getCustomField3Id(), entryCorrespon.getCustomField3Id());
            assertEquals(expectCorrespon.getCustomField3Label(), entryCorrespon.getCustomField3Label());
            assertEquals(expectCorrespon.getCustomField3Value(), entryCorrespon.getCustomField3Value());
            assertEquals(expectCorrespon.getCustomField4Id(), entryCorrespon.getCustomField4Id());
            assertEquals(expectCorrespon.getCustomField4Label(), entryCorrespon.getCustomField4Label());
            assertEquals(expectCorrespon.getCustomField4Value(), entryCorrespon.getCustomField4Value());
            assertEquals(expectCorrespon.getCustomField5Id(), entryCorrespon.getCustomField5Id());
            assertEquals(expectCorrespon.getCustomField5Label(), entryCorrespon.getCustomField5Label());
            assertEquals(expectCorrespon.getCustomField5Value(), entryCorrespon.getCustomField5Value());
            assertEquals(expectCorrespon.getCustomField6Id(), entryCorrespon.getCustomField6Id());
            assertEquals(expectCorrespon.getCustomField6Label(), entryCorrespon.getCustomField6Label());
            assertEquals(expectCorrespon.getCustomField6Value(), entryCorrespon.getCustomField6Value());
            assertEquals(expectCorrespon.getCustomField7Id(), entryCorrespon.getCustomField7Id());
            assertEquals(expectCorrespon.getCustomField7Label(), entryCorrespon.getCustomField7Label());
            assertEquals(expectCorrespon.getCustomField7Value(), entryCorrespon.getCustomField7Value());
            assertEquals(expectCorrespon.getCustomField8Id(), entryCorrespon.getCustomField8Id());
            assertEquals(expectCorrespon.getCustomField8Label(), entryCorrespon.getCustomField8Label());
            assertEquals(expectCorrespon.getCustomField8Value(), entryCorrespon.getCustomField8Value());
            assertEquals(expectCorrespon.getCustomField9Id(), entryCorrespon.getCustomField9Id());
            assertEquals(expectCorrespon.getCustomField9Label(), entryCorrespon.getCustomField9Label());
            assertEquals(expectCorrespon.getCustomField9Value(), entryCorrespon.getCustomField9Value());
            assertEquals(expectCorrespon.getCustomField10Id(), entryCorrespon.getCustomField10Id());
            assertEquals(expectCorrespon.getCustomField10Label(), entryCorrespon.getCustomField10Label());
            assertEquals(expectCorrespon.getCustomField10Value(), entryCorrespon.getCustomField10Value());
            assertEquals(expectCorrespon.getFile1Id(), entryCorrespon.getFile1Id());
            assertEquals(expectCorrespon.getFile1FileId(), entryCorrespon.getFile1FileId());
            assertEquals(expectCorrespon.getFile1FileName(), entryCorrespon.getFile1FileName());
            assertEquals(expectCorrespon.getFile2Id(), entryCorrespon.getFile2Id());
            assertEquals(expectCorrespon.getFile2FileId(), entryCorrespon.getFile2FileId());
            assertEquals(expectCorrespon.getFile2FileName(), entryCorrespon.getFile2FileName());
            assertEquals(expectCorrespon.getFile3Id(), entryCorrespon.getFile3Id());
            assertEquals(expectCorrespon.getFile3FileId(), entryCorrespon.getFile3FileId());
            assertEquals(expectCorrespon.getFile3FileName(), entryCorrespon.getFile3FileName());
            assertEquals(expectCorrespon.getFile4Id(), entryCorrespon.getFile4Id());
            assertEquals(expectCorrespon.getFile4FileId(), entryCorrespon.getFile4FileId());
            assertEquals(expectCorrespon.getFile4FileName(), entryCorrespon.getFile4FileName());
            assertEquals(expectCorrespon.getFile5Id(), entryCorrespon.getFile5Id());
            assertEquals(expectCorrespon.getFile5FileId(), entryCorrespon.getFile5FileId());
            assertEquals(expectCorrespon.getFile5FileName(), entryCorrespon.getFile5FileName());
            if (expectCorrespon.getCreatedBy() == null) {
                assertNull(entryCorrespon.getCreatedBy());
            } else {
                assertEquals(expectCorrespon.getCreatedBy().getEmpNo(), entryCorrespon.getCreatedBy().getEmpNo());
                assertEquals(expectCorrespon.getCreatedBy().getNameE(), entryCorrespon.getCreatedBy().getNameE());
            }
            assertEquals(expectCorrespon.getCreatedAt(), entryCorrespon.getCreatedAt());
            assertEquals(expectCorrespon.getUpdatedBy().getEmpNo(), entryCorrespon.getUpdatedBy().getEmpNo());
            assertEquals(expectCorrespon.getUpdatedBy().getNameE(), entryCorrespon.getUpdatedBy().getNameE());
            assertEquals(expectCorrespon.getUpdatedAt(), entryCorrespon.getUpdatedAt());
            assertEquals(expectCorrespon.getVersionNo(), entryCorrespon.getVersionNo());
            assertEquals(expectCorrespon.getDeleteNo(), entryCorrespon.getDeleteNo());
            assertEquals(expectCorrespon.getReplyAddressUserId(), entryCorrespon.getReplyAddressUserId());

        }

    private void checkWorkflowFields(Workflow expectWorkflow, Workflow entryWorkflow) {

        assertEquals(expectWorkflow.getId(), entryWorkflow.getId());
        assertEquals(expectWorkflow.getCorresponId(), entryWorkflow.getCorresponId());
        assertEquals(expectWorkflow.getUser(), entryWorkflow.getUser());
        assertEquals(expectWorkflow.getWorkflowType(), entryWorkflow.getWorkflowType());
        assertEquals(expectWorkflow.getWorkflowNo(), entryWorkflow.getWorkflowNo());
        assertEquals(expectWorkflow.getWorkflowProcessStatus().getLabel(), entryWorkflow.getWorkflowProcessStatus().getLabel());
        assertEquals(expectWorkflow.getWorkflowProcessStatus().getValue(), entryWorkflow.getWorkflowProcessStatus().getValue());
        assertEquals(expectWorkflow.getCommentOn(), entryWorkflow.getCommentOn());
        if (expectWorkflow.getFinishedBy() == null) {
            assertNull(entryWorkflow.getFinishedBy());
        } else {
            assertEquals(expectWorkflow.getFinishedBy().getEmpNo(), entryWorkflow.getFinishedBy().getEmpNo());
            assertEquals(expectWorkflow.getFinishedBy().getNameE(), entryWorkflow.getFinishedBy().getNameE());
            assertEquals(expectWorkflow.getFinishedAt(),entryWorkflow.getFinishedAt());
        }
        assertNull(entryWorkflow.getCreatedBy());
        assertNull(entryWorkflow.getCreatedAt());
        assertEquals(expectWorkflow.getUpdatedBy().getEmpNo(), entryWorkflow.getUpdatedBy().getEmpNo());
        assertEquals(expectWorkflow.getUpdatedBy().getNameE(), entryWorkflow.getUpdatedBy().getNameE());
        assertEquals(expectWorkflow.getUpdatedAt(), entryWorkflow.getUpdatedAt());
        assertEquals(expectWorkflow.getVersionNo(), entryWorkflow.getVersionNo());
        assertEquals(expectWorkflow.getDeleteNo(), entryWorkflow.getDeleteNo());

    }

}

    public static class MockCorresponDao extends MockAbstractDao<Entity> {
        static List<Correspon> RET_FIND_REPLY_CORRESPONS;

        public List<Correspon> findReplyCorresponByAddressUserId(Long addressUserId) {
            return RET_FIND_REPLY_CORRESPONS;
        }
    }

    public static class MockAddressCorresponGroupDao {
        static List<AddressCorresponGroup> RET_FIND_BY_CORRESPON_ID;
        static Integer RET_DELETE_BY_CORRESPONID;
        static AddressCorresponGroup RET_UPDATE_ADDRESSCORRESPONGROUP;

        public List<AddressCorresponGroup> findByCorresponId(Long corresponId) {
            return RET_FIND_BY_CORRESPON_ID;
        }

        public Integer deleteByCorresponId(AddressCorresponGroup addressCorresponGroup) {

            assertEquals(RET_UPDATE_ADDRESSCORRESPONGROUP.getCorresponId(), addressCorresponGroup.getCorresponId());
            assertEquals(RET_UPDATE_ADDRESSCORRESPONGROUP.getUpdatedBy().getEmpNo(), addressCorresponGroup.getUpdatedBy().getEmpNo());
            assertEquals(RET_UPDATE_ADDRESSCORRESPONGROUP.getUpdatedBy().getNameE(), addressCorresponGroup.getUpdatedBy().getNameE());

            return RET_DELETE_BY_CORRESPONID;
          }

    }

    public static class MockPersonInChargeDao {
        static List<PersonInCharge> RET_FIND_BY_ADDRESS_USER_ID;
        static Integer RET_DELETE_BY_CORRESPONID;
        static PersonInCharge RET_UPDATE_PERSONINCHARGE;

        public Integer deleteByAddressUserId(PersonInCharge personInCharge) {

            assertEquals(RET_UPDATE_PERSONINCHARGE.getAddressUserId(), personInCharge.getAddressUserId());
            assertEquals(RET_UPDATE_PERSONINCHARGE.getUpdatedBy().getEmpNo(), personInCharge.getUpdatedBy().getEmpNo());
            assertEquals(RET_UPDATE_PERSONINCHARGE.getUpdatedBy().getNameE(), RET_UPDATE_PERSONINCHARGE.getUpdatedBy().getNameE());

            return RET_DELETE_BY_CORRESPONID;
        }

        public List<PersonInCharge> findByAddressUserId(Long addressUserId) {
            System.out.println(RET_FIND_BY_ADDRESS_USER_ID);
            return RET_FIND_BY_ADDRESS_USER_ID;
       }
    }

    public static class MockAttachmentDao {
        static List<Attachment> RET_FIND_BY_CORRESPON_ID;

        public List<Attachment> findByCorresponId(Long corresponId) {
            return RET_FIND_BY_CORRESPON_ID;
        }

    }

    public static class MockAddressUserDao {
        static List<AddressUser> RET_FIND_BY_ADDRESS_CORRESPON_GROUP_ID;
        static Integer RET_DELETE_BY_CORRESPONID;
        static AddressUser RET_UPDATE_ADDRESSUSER;

        public List<AddressUser> findByAddressCorresponGroupId(Long addressCorresponGroupId) {
            return RET_FIND_BY_ADDRESS_CORRESPON_GROUP_ID;
        }

        public Integer deleteByAddressCorresponGroupId(AddressUser addressUser) {

          assertEquals(RET_UPDATE_ADDRESSUSER.getAddressCorresponGroupId(), addressUser.getAddressCorresponGroupId());
          assertEquals(RET_UPDATE_ADDRESSUSER.getUpdatedBy().getEmpNo(), addressUser.getUpdatedBy().getEmpNo());
          assertEquals(RET_UPDATE_ADDRESSUSER.getUpdatedBy().getNameE(), addressUser.getUpdatedBy().getNameE());

          return RET_DELETE_BY_CORRESPONID;
        }

    }

    public static class MockCorresponCustomFieldDao {
        static Integer RET_DELETE_BY_CORRESPONID;
        static CorresponCustomField RET_FIND_BY_ID;
        static CorresponCustomField RET_UPDATE_CORRESPONCUSTOMFIELD;

        public Integer deleteByCorresponId(CorresponCustomField corresponCustomField) {

            assertEquals(RET_UPDATE_CORRESPONCUSTOMFIELD.getCorresponId(), corresponCustomField.getCorresponId());
            assertEquals(RET_UPDATE_CORRESPONCUSTOMFIELD.getUpdatedBy().getEmpNo(), corresponCustomField.getUpdatedBy().getEmpNo());
            assertEquals(RET_UPDATE_CORRESPONCUSTOMFIELD.getUpdatedBy().getNameE(), corresponCustomField.getUpdatedBy().getNameE());

            return RET_DELETE_BY_CORRESPONID;
        }
    }

    public static class MockWorkflowDao extends MockAbstractDao<Entity> {
        static List<Workflow> RET_FIND_BY_CORRESPON_ID;
        static List<Workflow> RET_UPDATE_BY_CORRESPONID_WORKFLOW;
        static Integer RET_UPDATE_BY_CORRESPON_ID;


        public List<Workflow> findByCorresponId(Long corresponId) {
            return RET_FIND_BY_CORRESPON_ID;
        }

        public Integer updateByCorresponId(Workflow entryWorkflow) {

            for (Workflow w : RET_UPDATE_BY_CORRESPONID_WORKFLOW) {
                assertEquals(w.getCorresponId(), entryWorkflow.getCorresponId());
                assertEquals(w.getWorkflowProcessStatus().getLabel(), entryWorkflow.getWorkflowProcessStatus().getLabel());
                assertEquals(w.getFinishedBy().getEmpNo(), entryWorkflow.getFinishedBy().getEmpNo());
                assertEquals(w.getFinishedBy().getNameE(), entryWorkflow.getFinishedBy().getNameE());
                assertEquals(DBValue.DATE_NULL, entryWorkflow.getFinishedAt());
                assertEquals(w.getUpdatedBy().getEmpNo(), entryWorkflow.getUpdatedBy().getEmpNo());
                assertEquals(w.getUpdatedBy().getNameE(), entryWorkflow.getUpdatedBy().getNameE());
                assertEquals(w.getUpdatedAt(), entryWorkflow.getUpdatedAt());
            }

            return RET_UPDATE_BY_CORRESPON_ID;
        }
    }

    public static class MockCorresponTypeDao extends MockAbstractDao<Entity> {
        static CorresponType RET_FIND_BY_PROJECT_CORRESPON_TYPE_ID;
        static RecordNotFoundException EX_FIND_BY_PROJECT_CORRESPON_TYPE_ID;

        public CorresponType findByProjectCorresponTypeId(Long projectCorresponTypeId)
                throws RecordNotFoundException {
            if (EX_FIND_BY_PROJECT_CORRESPON_TYPE_ID != null) {
                throw EX_FIND_BY_PROJECT_CORRESPON_TYPE_ID;
            }
            return RET_FIND_BY_PROJECT_CORRESPON_TYPE_ID;
        }
    }

    public static class MockCorresponGroupListDao {
        static List<CorresponGroup> RET_FIND_BY_CORRESPON_ID;

        public List<CorresponGroup> find(SearchCorresponGroupCondition condition) {
            return RET_FIND_BY_CORRESPON_ID;
        }
    }

    //
    public static class MockProjectUserDao {
        static List<ProjectUser> RET_FIND_BY_CORRESPON_ID;

        public List<ProjectUser> findProjectUser(SearchUserCondition condition) {
            return RET_FIND_BY_CORRESPON_ID;
        }
    }

    public static class MockCustomFieldDao {
        static CustomField RET_FIND_BY_ID_PROJECT_ID;
        static RecordNotFoundException EX_FIND_BY_PROJECT_ID;

        public CustomField findByIdProjectId(SearchCustomFieldCondition condition)
                throws RecordNotFoundException {
            if (EX_FIND_BY_PROJECT_ID != null) {
                throw EX_FIND_BY_PROJECT_ID;
            }
            return RET_FIND_BY_ID_PROJECT_ID;
        }
    }

    public static class MockCorresponWorkflowService {
        static String RET_METHOD;
        static Correspon RET_CORRESPON;
        static Workflow RET_WORKFLOW;

        public void check(Correspon correspon, Workflow workflow) throws ServiceAbortException {
            RET_METHOD = "check";
            RET_CORRESPON = correspon;
            RET_WORKFLOW = workflow;
        }

        public void approve(Correspon correspon, Workflow workflow) throws ServiceAbortException {
            RET_METHOD = "approve";
            RET_CORRESPON = correspon;
            RET_WORKFLOW = workflow;
        }

        public void deny(Correspon correspon, Workflow workflow) throws ServiceAbortException {
            RET_METHOD = "deny";
            RET_CORRESPON = correspon;
            RET_WORKFLOW = workflow;
        }
    }

    public static class MockParentCorresponNoSeqDao extends MockAbstractDao<ParentCorresponNoSeq> {
        static ParentCorresponNoSeq RET_FIND_FOR_UPDATE;

        public MockParentCorresponNoSeqDao() {

        }

        public ParentCorresponNoSeq findForUpdate(ParentCorresponNoSeqCondition condition) {
            return RET_FIND_FOR_UPDATE;
        }
    }

    public static class MockUser {
        static boolean IS_SYSTEM_ADMIN = false;

        public boolean isSystemAdmin() {
            return IS_SYSTEM_ADMIN;
        }
    }

    public static class MockProjectCustomFieldDao  {
        static Map<Long, Long> RET = new HashMap<Long, Long>();

        public Long findProjectCustomFieldIdByProjectIdNo(SearchCustomFieldCondition condition) throws RecordNotFoundException {
            if (RET.get(condition.getNo()) == null) {
                throw new RecordNotFoundException();
            }
            return RET.get(condition.getNo());
        }

    }
}
