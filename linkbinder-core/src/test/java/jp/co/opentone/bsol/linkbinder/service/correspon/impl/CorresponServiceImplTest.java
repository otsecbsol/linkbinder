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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import jp.co.opentone.bsol.framework.core.ProcessContext;
import jp.co.opentone.bsol.framework.core.config.SystemConfig;
import jp.co.opentone.bsol.framework.core.dao.Entity;
import jp.co.opentone.bsol.framework.core.dao.KeyDuplicateException;
import jp.co.opentone.bsol.framework.core.dao.RecordNotFoundException;
import jp.co.opentone.bsol.framework.core.dao.StaleRecordException;
import jp.co.opentone.bsol.framework.core.exception.ApplicationFatalRuntimeException;
import jp.co.opentone.bsol.framework.core.extension.ibatis.DBValue;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.test.AbstractTestCase;
import jp.co.opentone.bsol.linkbinder.Constants;
import jp.co.opentone.bsol.linkbinder.dto.AddressCorresponGroup;
import jp.co.opentone.bsol.linkbinder.dto.AddressUser;
import jp.co.opentone.bsol.linkbinder.dto.Attachment;
import jp.co.opentone.bsol.linkbinder.dto.Company;
import jp.co.opentone.bsol.linkbinder.dto.Correspon;
import jp.co.opentone.bsol.linkbinder.dto.CorresponCustomField;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroup;
import jp.co.opentone.bsol.linkbinder.dto.CorresponHierarchy;
import jp.co.opentone.bsol.linkbinder.dto.CorresponResponseHistory;
import jp.co.opentone.bsol.linkbinder.dto.CorresponType;
import jp.co.opentone.bsol.linkbinder.dto.CustomField;
import jp.co.opentone.bsol.linkbinder.dto.Discipline;
import jp.co.opentone.bsol.linkbinder.dto.ParentCorresponNoSeq;
import jp.co.opentone.bsol.linkbinder.dto.PersonInCharge;
import jp.co.opentone.bsol.linkbinder.dto.Project;
import jp.co.opentone.bsol.linkbinder.dto.ProjectCustomField;
import jp.co.opentone.bsol.linkbinder.dto.ProjectUser;
import jp.co.opentone.bsol.linkbinder.dto.Site;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.Workflow;
import jp.co.opentone.bsol.linkbinder.dto.WorkflowPattern;
import jp.co.opentone.bsol.linkbinder.dto.code.AddressType;
import jp.co.opentone.bsol.linkbinder.dto.code.AddressUserType;
import jp.co.opentone.bsol.linkbinder.dto.code.AllowApproverToBrowse;
import jp.co.opentone.bsol.linkbinder.dto.code.CorresponStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.ForceToUseWorkflow;
import jp.co.opentone.bsol.linkbinder.dto.code.ReadStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.ReplyRequired;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowProcessStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowType;
import jp.co.opentone.bsol.linkbinder.dto.condition.ParentCorresponNoSeqCondition;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponGroupCondition;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCustomFieldCondition;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchUserCondition;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.service.MockAbstractService;
import jp.co.opentone.bsol.linkbinder.service.common.impl.CorresponSequenceServiceImpl;
import jp.co.opentone.bsol.linkbinder.service.correspon.CorresponService;
import jp.co.opentone.bsol.linkbinder.service.correspon.impl.CorresponValidateServiceImplTest.MockProjectCustomFieldDao;
import jp.co.opentone.bsol.linkbinder.service.correspon.impl.CorresponValidateServiceImplTest.MockUserDao;
import jp.co.opentone.bsol.linkbinder.util.view.correspon.CorresponPageFormatter;

/**
 * {@link CorresponServiceImpl}のテストケース.
 * @author opentone
 */
public class CorresponServiceImplTest extends AbstractTestCase {

    /**
     * テスト対象.
     */
    @Resource
    private CorresponService service;

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

    @AfterClass
    public static void testTeardown() {
        //TODO JMockitバージョンアップ対応
//        Mockit.tearDownMocks();
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
//        Mockit.redefineMethods(User.class, MockUser.class);
//        Mockit.redefineMethods(ProjectUser.class, MockProjectUser.class);
//        Mockit.redefineMethods(AbstractService.class, MockAbstractService.class);
//        Mockit.redefineMethods(CorresponDaoImpl.class, MockCorresponDao.class);
//        Mockit.redefineMethods(WorkflowDaoImpl.class, MockWorkflowDao.class);
//        Mockit.redefineMethods(AddressCorresponGroupDaoImpl.class,
//            MockAddressCorresponGroupDao.class);
//        Mockit.redefineMethods(AddressUserDaoImpl.class, MockAddressUserDao.class);
//        Mockit.redefineMethods(PersonInChargeDaoImpl.class, MockPersonInChargeDao.class);
//        Mockit.redefineMethods(AbstractDao.class, MockAbstractDao.class);
//
//        Mockit.redefineMethods(CorresponWorkflowServiceImpl.class,
//            MockCorresponWorkflowService.class);
//
//        Mockit.redefineMethods(UserDaoImpl.class, MockProjectUserDao.class);
//        Mockit.redefineMethods(CorresponGroupDaoImpl.class, MockCorresponGroupListDao.class);
//        Mockit.redefineMethods(AttachmentDaoImpl.class, MockAttachmentDao.class);
//        Mockit
//            .redefineMethods(CorresponCustomFieldDaoImpl.class, MockCorresponCustomFieldDao.class);
//        Mockit.redefineMethods(CustomFieldDaoImpl.class, MockCustomFieldDao.class);
//        Mockit.redefineMethods(CorresponTypeDaoImpl.class, MockCorresponTypeDao.class);
//
//        Mockit
//            .redefineMethods(ParentCorresponNoSeqDaoImpl.class, MockParentCorresponNoSeqDao.class);
//        Mockit.redefineMethods(CorresponHierarchyDaoImpl.class, MockCorresponHierarchyDao.class);
//        Mockit.redefineMethods(ProjectDaoImpl.class, MockProjectDao.class);
//        Mockit.redefineMethods(UserRoleHelper.class, MockUserRoleHelper.class);
//        Mockit.redefineMethods(CorresponSequenceServiceImpl.class, MockCorresponSequenceService.class);
//        Mockit.redefineMethods(CorresponReadStatusServiceImpl.class, MockCorresponReadStatusService.class);
//        Mockit.redefineMethods(CorresponHTMLGeneratorUtil.class, MockCorresponHTMLGeneratorUtil.class);

        MockProjectCustomFieldDao.RET = new HashMap<Long, Long>();
        MockCustomFieldDao.RET_FIND_BY_PROJECT_ID = new ArrayList<CustomField>();

        MockAbstractService.IS_GROUP_ADMIN = new ArrayList<Long>();


    }

    /**
     * 後始末.
     */
    @After
    public void tearDown() {
        // 差し換えたMockをクリアする
        //TODO JMockitバージョンアップ対応
//        Mockit.tearDownMocks();
        MockAddressCorresponGroupDao.RET_FIND_BY_CORRESPON_ID = null;
        MockAddressUserDao.RET_FIND_BY_ADDRESS_CORRESPON_GROUP_ID = null;
        MockPersonInChargeDao.RET_FIND_BY_CORRESPON_ID = null;
        MockWorkflowDao.RET_FIND_BY_CORRESPON_ID = null;
        MockProjectUserDao.RET_FIND_BY_CORRESPON_ID = null;
        MockCorresponGroupListDao.RET_FIND_BY_CORRESPON_ID = null;
        MockAttachmentDao.RET_FIND_BY_CORRESPON_ID = null;
        MockCorresponCustomFieldDao.RET_FIND_BY_ID = null;
        MockCorresponWorkflowService.RET_METHOD = null;
        MockCorresponWorkflowService.RET_CORRESPON = null;
        MockCorresponWorkflowService.RET_WORKFLOW = null;
        MockUser.RET_USER = false;
        MockProjectUser.RET_PROJECT_USER = false;
        MockCorresponTypeDao.RET_FIND_BY_PROJECT_CORRESPON_TYPE_ID = null;
        MockCorresponTypeDao.EX_FIND_BY_PROJECT_CORRESPON_TYPE_ID = null;
        MockCustomFieldDao.RET_FIND_BY_ID_PROJECT_ID = null;
        MockCustomFieldDao.EX_FIND_BY_PROJECT_ID = null;
        MockProjectUserDao.FIND_BY_EMP_NO = null;
        MockCorresponHierarchyDao.COUNT = 0;
        MockPersonInChargeDao.RET_DELETE_BY_CORRESPONID = 0;
        MockPersonInChargeDao.RET_FIND_BY_ADDRESS_USER_ID = null;

        MockUserRoleHelper.IS_CHECKER = false;
        MockUserRoleHelper.IS_APPROVER = false;

        MockProjectCustomFieldDao.RET = null;

        tearDownMockAbstractService();
        tearDownMockAbstractDao();
    }

    private void tearDownMockAbstractDao() {
        MockAbstractDao.RET_UPDATE = null;
        MockAbstractDao.RET_UPDATE_CORRESPON = null;
        MockAbstractDao.RET_UPDATE_WORKFLOW = null;
        MockAbstractDao.RET_FIND_BY_ID = new HashMap<String, Object>();
        MockAbstractDao.count = 0;
        MockAbstractDao.RET_FIND_BY_ID_CORRESPONGROUP = null;
        MockAbstractDao.createPersonInChargeCount = 0;
        MockAbstractDao.RET_CREATE_PERSONINCHARGE = null;
        MockAbstractDao.countElement = 0;
        MockAbstractDao.STALE_RECORD_EXCEPTION = false;
        MockAbstractDao.EDITMODE = MockAbstractDao.DEFAULT_MODE;
        MockAbstractDao.RET_UPDATE_CORRESPON = null;
        MockCorresponSequenceService.CORRESPON_NO = null;
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

    private void setUpCorresponType(Correspon c) {
        CorresponType ct = new CorresponType();
        ct.setProjectCorresponTypeId(1L);
        ct.setCorresponType("Query");
        ct.setName("Name Query");
        WorkflowPattern p = new WorkflowPattern();
        p.setId(100L);
        p.setWorkflowCd("Pattern1");
        p.setName("Workflow Pattern 1");
        ct.setWorkflowPattern(p);

        c.setCorresponType(ct);

        MockCorresponTypeDao.RET_FIND_BY_PROJECT_CORRESPON_TYPE_ID = ct;
    }

    /**
     * コレポン文書が取得できるか検証する.
     * @throws Exception
     */
    @Test
    public void testFindById() throws Exception {
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";

        // ダミーの戻り値をセット
        Correspon c = new Correspon();
        c.setWorkflowStatus(WorkflowStatus.DRAFT);
        c.setProjectId(MockAbstractService.CURRENT_PROJECT_ID );
        c.setSubject("mock");
        c.setCreatedBy(user);
        c.setUpdatedBy(user);


        //  カスタムフィールドが並びかえられているかを検証するためのデータ
        c.setCustomField1Id(1L);
        c.setCustomField2Id(2L);
        c.setCustomField3Id(3L);
        c.setCustomField4Id(4L);
        c.setCustomField5Id(5L);
        c.setCustomField6Id(6L);
        c.setCustomField7Id(7L);
        c.setCustomField8Id(8L);
        c.setCustomField9Id(9L);
        c.setCustomField10Id(10L);
        c.setCustomField1Label("label1");
        c.setCustomField2Label("label2");
        c.setCustomField3Label("label3");
        c.setCustomField4Label("label4");
        c.setCustomField5Label("label5");
        c.setCustomField6Label("label6");
        c.setCustomField7Label("label7");
        c.setCustomField8Label("label8");
        c.setCustomField9Label("label9");
        c.setCustomField10Label("label10");
        c.setCustomField1Value("v1");
        c.setCustomField2Value("v2");
        c.setCustomField3Value("v3");
        c.setCustomField4Value("v4");
        c.setCustomField5Value("v5");
        c.setCustomField6Value("v6");
        c.setCustomField7Value("v7");
        c.setCustomField8Value("v8");
        c.setCustomField9Value("v9");
        c.setCustomField10Value("v10");

        //  マスタデータは並べ換えられている
        //  数も減っている
        CustomField f;
        f = new CustomField();
        f.setId(51L);
        f.setProjectCustomFieldId(5L);
        f.setLabel("label5");
        MockCustomFieldDao.RET_FIND_BY_PROJECT_ID.add(f);
        f = new CustomField();
        f.setId(41L);
        f.setProjectCustomFieldId(4L);
        f.setLabel("label4");
        MockCustomFieldDao.RET_FIND_BY_PROJECT_ID.add(f);
        f = new CustomField();
        f.setId(31L);
        f.setProjectCustomFieldId(3L);
        f.setLabel("label3");
        MockCustomFieldDao.RET_FIND_BY_PROJECT_ID.add(f);
        f = new CustomField();
        f.setId(21L);
        f.setProjectCustomFieldId(2L);
        f.setLabel("label2");
        MockCustomFieldDao.RET_FIND_BY_PROJECT_ID.add(f);
        f = new CustomField();
        f.setId(11L);
        f.setProjectCustomFieldId(1L);
        f.setLabel("label1");
        MockCustomFieldDao.RET_FIND_BY_PROJECT_ID.add(f);


        setUpCorresponType(c);
        MockCorresponDao.RET_FIND_BY_ID.put("1", c);

        List<Project> projects = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        projects.add(p);
        MockAbstractService.ACCESSIBLE_PROJECTS = projects;

        assertEquals(c, service.find(1L));


        //  カスタムフィールドの並べかえを検証
        List<CustomField> master = MockCustomFieldDao.RET_FIND_BY_PROJECT_ID;
        assertEquals(master.get(0).getProjectCustomFieldId(), c.getCustomField1Id());
        assertEquals(master.get(0).getLabel(), c.getCustomField1Label());
        assertEquals("v5", c.getCustomField1Value());
        assertEquals(master.get(1).getProjectCustomFieldId(), c.getCustomField2Id());
        assertEquals(master.get(1).getLabel(), c.getCustomField2Label());
        assertEquals("v4", c.getCustomField2Value());
        assertEquals(master.get(2).getProjectCustomFieldId(), c.getCustomField3Id());
        assertEquals(master.get(2).getLabel(), c.getCustomField3Label());
        assertEquals("v3", c.getCustomField3Value());
        assertEquals(master.get(3).getProjectCustomFieldId(), c.getCustomField4Id());
        assertEquals(master.get(3).getLabel(), c.getCustomField4Label());
        assertEquals("v2", c.getCustomField4Value());
        assertEquals(master.get(4).getProjectCustomFieldId(), c.getCustomField5Id());
        assertEquals(master.get(4).getLabel(), c.getCustomField5Label());
        assertEquals("v1", c.getCustomField5Value());

        //  あとはクリアされている
        assertNull(c.getCustomField6Id());
        assertNull(c.getCustomField6Label());
        assertNull(c.getCustomField6Value());
        assertNull(c.getCustomField7Id());
        assertNull(c.getCustomField7Label());
        assertNull(c.getCustomField7Value());
        assertNull(c.getCustomField8Id());
        assertNull(c.getCustomField8Label());
        assertNull(c.getCustomField8Value());
        assertNull(c.getCustomField9Id());
        assertNull(c.getCustomField9Label());
        assertNull(c.getCustomField9Value());
        assertNull(c.getCustomField10Id());
        assertNull(c.getCustomField10Label());
        assertNull(c.getCustomField10Value());
    }

    /**
     * validationの検証. 承認状態 = Draft の文書に対するアクセス権限チェック.
     * @throws Exception
     */
    @Test
    public void testFindByIdCheck1() throws Exception {
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";

        // 他人が作成したDraft状態の文書は閲覧できない
        // ダミーの戻り値をセット
        Correspon c = new Correspon();
        c.setWorkflowStatus(WorkflowStatus.DRAFT);
        c.setProjectId(MockAbstractService.CURRENT_PROJECT_ID);
        c.setSubject("mock");
        User createdBy = new User();
        createdBy.setEmpNo("ZZA99");
        c.setCreatedBy(createdBy);
        c.setUpdatedBy(user);

        setUpCorresponType(c);
        MockCorresponDao.RET_FIND_BY_ID.put("1", c);

        List<Project> projects = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        projects.add(p);
        MockAbstractService.ACCESSIBLE_PROJECTS = projects;

        // ログインユーザーの権限はSystem Admin以外
        MockAbstractService.IS_SYSTEM_ADMIN = false;
        try {
            service.find(1L);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            // 正しいエラーコードがセットされた例外がthrowされていることを検証
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT,
                actual.getMessageCode());
        }
        MockCorresponDao.RET_FIND_BY_ID.put("2", c);

        // System Adminの場合は全ての文書を閲覧可能
        MockAbstractService.IS_SYSTEM_ADMIN = true;
        Correspon actual = service.find(1L);
        assertEquals(c, actual);
    }

    /**
     * validationの検証. 承認状態 = Issued の文書に対するアクセス権限チェック.
     * @throws Exception
     */
    @Test
    public void testFindByIdCheck2() throws Exception {

        // 現在選択中プロジェクト以外のコレポン文書の場合はエラー
        Correspon c = new Correspon();
        c.setWorkflowStatus(WorkflowStatus.ISSUED);
        c.setProjectId("PJ1");
        c.setSubject("mock");
        User createdBy = new User();
        createdBy.setEmpNo("ZZA99");
        c.setCreatedBy(createdBy);
        c.setUpdatedBy(user);

        setUpCorresponType(c);
        MockCorresponDao.RET_FIND_BY_ID.put("1", c);
        MockCorresponDao.RET_FIND_BY_ID.put("2", c);
        MockCorresponDao.RET_FIND_BY_ID.put("3", c);

        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ2";

        try {
            service.find(1L);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            // 正しいエラーコードがセットされた例外がthrowされていることを検証
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_PROJECT_DIFF, actual
                .getMessageCode());
        }

        // ログインユーザーがアクセスできないはずのプロジェクトの場合は
        // もちろんアクセスできない
        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";

        List<Project> projects = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ3");
        projects.add(p);
        p = new Project();
        p.setProjectId("PJ4");
        projects.add(p);
        MockAbstractService.ACCESSIBLE_PROJECTS = projects;
        try {
            service.find(1L);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            // 正しいエラーコードがセットされた例外がthrowされていることを検証
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_PROJECT_DIFF, actual
                .getMessageCode());
        }

        // System Adminであっても、現在のプロジェクト以外のコレポン文書には
        // アクセスできない
        MockAbstractService.IS_SYSTEM_ADMIN = true;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ2";

        try {
            service.find(1L);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            // 正しいエラーコードがセットされた例外がthrowされていることを検証
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_PROJECT_DIFF, actual
                .getMessageCode());
        }

        // ログインユーザーがアクセス可能なプロジェクトのコレポン文書であれば
        // アクセス可能
        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";

        projects = new ArrayList<Project>();
        p = new Project();
        p.setProjectId("PJ1");
        projects.add(p);
        MockAbstractService.ACCESSIBLE_PROJECTS = projects;

        MockCorresponDao.RET_FIND_BY_ID.put("4", c);

        Correspon actual = service.find(1L);
        assertEquals(c, actual);
    }

    /**
     * validationの検証. 承認状態 != (Draft or Issued) の文書に対するアクセス権限チェック.
     * <ul>
     * <li>Preparerはアクセス可能</li>
     * <li>承認作業中のCheckerはアクセス可能</li>
     * <li>承認作業中のApproverはアクセス可能</li>
     * <li>System Adminはアクセス可能</li>
     * <li>Project Adminはアクセス可能</li>
     * <li>送信元活動単位か宛先に含まれる活動単位のGroup Adminはアクセス可能</li>
     * </ul>
     * @throws Exception
     */
    @Test
    public void testFindByIdCheck3() throws Exception {
        WorkflowStatus[] statuses =
                { WorkflowStatus.REQUEST_FOR_CHECK, WorkflowStatus.UNDER_CONSIDERATION,
                    WorkflowStatus.REQUEST_FOR_APPROVAL, WorkflowStatus.DENIED, };

        List<Project> projects = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        projects.add(p);
        MockAbstractService.ACCESSIBLE_PROJECTS = projects;

        // Preparer
        for (WorkflowStatus s : statuses) {
            MockCorresponDao.count = 0;

            Correspon c = new Correspon();
            c.setWorkflowStatus(s);
            c.setProjectId("PJ1");
            c.setSubject("mock");
            c.setCreatedBy(user);
            c.setUpdatedBy(user);
            setUpFromCorresponGroup(c, 1L);
            setUpAddresCorresponGroup(2L, 3L, 4L);

            setUpCorresponType(c);
            MockCorresponDao.RET_FIND_BY_ID.put("1", c);
            MockAbstractService.IS_SYSTEM_ADMIN = false;
            MockAbstractService.IS_PROJECT_ADMIN = false;
            MockAbstractService.IS_GROUP_ADMIN = null;
            MockAbstractService.IS_ANY_GROUP_ADMIN = false;
            MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
            Correspon actual = service.find(1L);
            assertEquals(c, actual);

        }

        // Checker
        // アクセス可能な場合を先に確認
        for (WorkflowStatus s : statuses) {
            Correspon c = new Correspon();
            c.setWorkflowStatus(s);
            c.setProjectId("PJ1");
            c.setSubject("mock");
            User preparer = new User();
            preparer.setEmpNo("ZZA99");
            c.setCreatedBy(preparer);
            c.setUpdatedBy(preparer);
            setUpFromCorresponGroup(c, 1L);
            setUpAddresCorresponGroup(2L, 3L, 4L);

            // Checker自身の承認作業状態を作業中に変更
            WorkflowProcessStatus[] processStatuses =
                    { WorkflowProcessStatus.REQUEST_FOR_CHECK,
                        WorkflowProcessStatus.UNDER_CONSIDERATION, WorkflowProcessStatus.CHECKED,
                        WorkflowProcessStatus.DENIED, };
            for (WorkflowProcessStatus ps : processStatuses) {
                MockCorresponDao.count = 0;

                Workflow w = new Workflow();
                w.setId(1L);
                w.setUser(user);
                w.setWorkflowType(WorkflowType.CHECKER);
                w.setWorkflowProcessStatus(ps);
                List<Workflow> workflows = new ArrayList<Workflow>();
                workflows.add(w);
                MockWorkflowDao.RET_FIND_BY_CORRESPON_ID = workflows;

                setUpCorresponType(c);
                MockCorresponDao.RET_FIND_BY_ID.put("1", c);
                MockAbstractService.IS_SYSTEM_ADMIN = false;
                MockAbstractService.IS_PROJECT_ADMIN = false;
                MockAbstractService.IS_GROUP_ADMIN = null;
                MockAbstractService.IS_ANY_GROUP_ADMIN = false;
                MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
                Correspon actual = service.find(1L);
                assertEquals(c, actual);
            }
        }

        // Approver
        // アクセス可能な場合を先に確認
        for (WorkflowStatus s : statuses) {
            Correspon c = new Correspon();
            c.setWorkflowStatus(s);
            c.setProjectId("PJ1");
            c.setSubject("mock");

            CorresponType ct = new CorresponType();
            ct.setAllowApproverToBrowse(AllowApproverToBrowse.INVISIBLE);
            c.setCorresponType(ct);

            User preparer = new User();
            preparer.setEmpNo("ZZA99");
            c.setCreatedBy(preparer);
            c.setUpdatedBy(preparer);
            setUpFromCorresponGroup(c, 1L);
            setUpAddresCorresponGroup(2L, 3L, 4L);

            // Approver自身の承認作業状態を作業中に変更
            WorkflowProcessStatus[] processStatuses =
                    { WorkflowProcessStatus.REQUEST_FOR_APPROVAL,
                        WorkflowProcessStatus.UNDER_CONSIDERATION, WorkflowProcessStatus.APPROVED,
                        WorkflowProcessStatus.DENIED, };
            for (WorkflowProcessStatus ps : processStatuses) {
                Workflow w = new Workflow();
                w.setId(1L);
                w.setUser(user);
                w.setWorkflowType(WorkflowType.APPROVER);
                w.setWorkflowProcessStatus(ps);
                List<Workflow> workflows = new ArrayList<Workflow>();
                workflows.add(w);
                MockWorkflowDao.RET_FIND_BY_CORRESPON_ID = workflows;

                MockCorresponDao.count = 0;
                MockCorresponDao.RET_FIND_BY_ID.put("1", c);
                MockAbstractService.IS_SYSTEM_ADMIN = false;
                MockAbstractService.IS_PROJECT_ADMIN = false;
                MockAbstractService.IS_GROUP_ADMIN = null;
                MockAbstractService.IS_ANY_GROUP_ADMIN = false;
                MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
                Correspon actual = service.find(1L);
                assertEquals(c, actual);
            }
        }

        // Approver
        // 承認作業状態がNONEでも、Approver閲覧許可フラグがVisibleであればアクセス可能
        for (WorkflowStatus s : statuses) {
            Correspon c = new Correspon();
            c.setWorkflowStatus(s);
            c.setProjectId("PJ1");
            c.setSubject("mock");

            CorresponType ct = new CorresponType();
            ct.setAllowApproverToBrowse(AllowApproverToBrowse.VISIBLE);
            c.setCorresponType(ct);

            User preparer = new User();
            preparer.setEmpNo("ZZA99");
            c.setCreatedBy(preparer);
            c.setUpdatedBy(preparer);
            setUpFromCorresponGroup(c, 1L);
            setUpAddresCorresponGroup(2L, 3L, 4L);

            Workflow w = new Workflow();
            w.setId(1L);
            w.setUser(user);
            w.setWorkflowType(WorkflowType.APPROVER);
            w.setWorkflowProcessStatus(WorkflowProcessStatus.NONE);
            List<Workflow> workflows = new ArrayList<Workflow>();
            workflows.add(w);
            MockWorkflowDao.RET_FIND_BY_CORRESPON_ID = workflows;

            MockCorresponDao.count = 0;
            MockCorresponDao.RET_FIND_BY_ID.put("1", c);
            MockAbstractService.IS_SYSTEM_ADMIN = false;
            MockAbstractService.IS_PROJECT_ADMIN = false;
            MockAbstractService.IS_GROUP_ADMIN = null;
            MockAbstractService.IS_ANY_GROUP_ADMIN = false;
            MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
            Correspon actual = service.find(1L);
            assertEquals(c, actual);
        }

        // Approver
        // 承認作業状態がNONEでも、承認フローパターンが３であればアクセス可能
        for (WorkflowStatus s : statuses) {
            Correspon c = new Correspon();
            c.setWorkflowStatus(s);
            c.setProjectId("PJ1");
            c.setSubject("mock");

            CorresponType ct = new CorresponType();
            WorkflowPattern pattern = new WorkflowPattern();
            pattern.setId(3L);
            pattern.setName("Pattern3");
            ct.setWorkflowPattern(pattern);
            c.setCorresponType(ct);

            User preparer = new User();
            preparer.setEmpNo("ZZA99");
            c.setCreatedBy(preparer);
            c.setUpdatedBy(preparer);
            setUpFromCorresponGroup(c, 1L);
            setUpAddresCorresponGroup(2L, 3L, 4L);

            Workflow w = new Workflow();
            w.setId(1L);
            w.setUser(user);
            w.setWorkflowType(WorkflowType.APPROVER);
            w.setWorkflowProcessStatus(WorkflowProcessStatus.NONE);
            List<Workflow> workflows = new ArrayList<Workflow>();
            workflows.add(w);
            MockWorkflowDao.RET_FIND_BY_CORRESPON_ID = workflows;

            MockCorresponDao.count = 0;
            MockCorresponDao.RET_FIND_BY_ID.put("1", c);
            MockAbstractService.IS_SYSTEM_ADMIN = false;
            MockAbstractService.IS_PROJECT_ADMIN = false;
            MockAbstractService.IS_GROUP_ADMIN = null;
            MockAbstractService.IS_ANY_GROUP_ADMIN = false;
            MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
            Correspon actual = service.find(1L);
            assertEquals(c, actual);
        }

        // Project Admin
        // アクセス可能
        for (WorkflowStatus s : statuses) {
            Correspon c = new Correspon();
            c.setWorkflowStatus(s);
            c.setProjectId("PJ1");
            c.setSubject("mock");
            User preparer = new User();
            preparer.setEmpNo("ZZA99");
            c.setCreatedBy(preparer);
            c.setUpdatedBy(preparer);
            setUpFromCorresponGroup(c, 1L);
            setUpAddresCorresponGroup(2L, 3L, 4L);

            Workflow w = new Workflow();
            w.setId(1L);
            User checker = new User();
            checker.setEmpNo("ZZA88");
            w.setUser(checker);
            w.setWorkflowType(WorkflowType.CHECKER);
            w.setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_CHECK);
            List<Workflow> workflows = new ArrayList<Workflow>();
            workflows.add(w);
            MockWorkflowDao.RET_FIND_BY_CORRESPON_ID = workflows;

            MockCorresponDao.count = 0;
            setUpCorresponType(c);
            MockCorresponDao.RET_FIND_BY_ID.put("1", c);
            MockAbstractService.IS_SYSTEM_ADMIN = false;
            MockAbstractService.IS_PROJECT_ADMIN = true;
            MockAbstractService.IS_GROUP_ADMIN = null;
            MockAbstractService.IS_ANY_GROUP_ADMIN = false;
            MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
            Correspon actual = service.find(1L);
            assertEquals(c, actual);
        }

        // Group Admin
        // 送信元か宛先に含まれる活動単位のGroup Adminであればアクセス可能
        for (WorkflowStatus s : statuses) {
            Correspon c = new Correspon();
            c.setWorkflowStatus(s);
            c.setProjectId("PJ1");
            c.setSubject("mock");
            User preparer = new User();
            preparer.setEmpNo("ZZA99");
            c.setCreatedBy(preparer);
            c.setUpdatedBy(preparer);
            setUpFromCorresponGroup(c, 1L);
            setUpAddresCorresponGroup(2L, 3L, 4L);

            Workflow w = new Workflow();
            w.setId(1L);
            User checker = new User();
            checker.setEmpNo("ZZA88");
            w.setUser(checker);
            w.setWorkflowType(WorkflowType.CHECKER);
            w.setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_CHECK);
            List<Workflow> workflows = new ArrayList<Workflow>();
            workflows.add(w);
            MockWorkflowDao.RET_FIND_BY_CORRESPON_ID = workflows;

            MockCorresponDao.count = 0;
            setUpCorresponType(c);
            MockCorresponDao.RET_FIND_BY_ID.put("1", c);
            MockAbstractService.IS_SYSTEM_ADMIN = false;
            MockAbstractService.IS_PROJECT_ADMIN = false;
            List<Long> groupAdminId = new ArrayList<Long>();
            groupAdminId.add(c.getFromCorresponGroup().getId());

            MockAbstractService.IS_GROUP_ADMIN = groupAdminId;
            MockAbstractService.IS_ANY_GROUP_ADMIN = false;

            MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
            Correspon actual = service.find(1L);
            assertEquals(c, actual);
        }

        MockAbstractService.IS_GROUP_ADMIN.clear();

        // Checker
        // アクセスできない場合を確認
        for (WorkflowStatus s : statuses) {
            Correspon c = new Correspon();
            c.setWorkflowStatus(s);
            c.setProjectId("PJ1");
            c.setSubject("mock");
            User preparer = new User();
            preparer.setEmpNo("ZZA99");
            c.setCreatedBy(preparer);
            c.setUpdatedBy(preparer);
            setUpFromCorresponGroup(c, 1L);
            setUpAddresCorresponGroup(2L, 3L, 4L);

            Workflow w = new Workflow();
            w.setId(1L);
            w.setUser(user);
            w.setWorkflowType(WorkflowType.CHECKER);
            w.setWorkflowProcessStatus(WorkflowProcessStatus.NONE);
            List<Workflow> workflows = new ArrayList<Workflow>();
            workflows.add(w);
            MockWorkflowDao.RET_FIND_BY_CORRESPON_ID = workflows;

            MockCorresponDao.count = 0;
            setUpCorresponType(c);
            MockCorresponDao.RET_FIND_BY_ID.put("1", c);
            MockAbstractService.IS_SYSTEM_ADMIN = false;
            MockAbstractService.IS_PROJECT_ADMIN = false;
            MockAbstractService.IS_ANY_GROUP_ADMIN = false;
            MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
            try {
                service.find(1L);
                fail("例外が発生していない");
            } catch (ServiceAbortException actual) {
                assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_WORKFLOW_STATUS_INVALID,
                    actual.getMessageCode());

            }
        }

        // Approver
        // アクセスできない場合を確認
        for (WorkflowStatus s : statuses) {
            Correspon c = new Correspon();
            c.setWorkflowStatus(s);
            c.setProjectId("PJ1");
            c.setSubject("mock");

            //  アクセスできないコレポン文書種別を明示的に設定
            CorresponType ct = new CorresponType();
            ct.setAllowApproverToBrowse(AllowApproverToBrowse.INVISIBLE);
            c.setCorresponType(ct);
            MockCorresponTypeDao.RET_FIND_BY_PROJECT_CORRESPON_TYPE_ID = ct;

            User preparer = new User();
            preparer.setEmpNo("ZZA99");
            c.setCreatedBy(preparer);
            c.setUpdatedBy(preparer);
            setUpFromCorresponGroup(c, 1L);
            setUpAddresCorresponGroup(2L, 3L, 4L);

            Workflow w = new Workflow();
            w.setId(1L);
            w.setUser(user);
            w.setWorkflowType(WorkflowType.APPROVER);
            w.setWorkflowProcessStatus(WorkflowProcessStatus.NONE);
            List<Workflow> workflows = new ArrayList<Workflow>();
            workflows.add(w);
            MockWorkflowDao.RET_FIND_BY_CORRESPON_ID = workflows;

            MockCorresponDao.count = 0;
            MockCorresponDao.RET_FIND_BY_ID.put("1", c);
            MockAbstractService.IS_SYSTEM_ADMIN = false;
            MockAbstractService.IS_PROJECT_ADMIN = false;
            MockAbstractService.IS_ANY_GROUP_ADMIN = false;
            MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
            try {
                service.find(1L);
                fail("例外が発生していない");
            } catch (ServiceAbortException actual) {
                assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_WORKFLOW_STATUS_INVALID,
                    actual.getMessageCode());

            }
        }

        // その他の人はアクセスできない
        for (WorkflowStatus s : statuses) {
            Correspon c = new Correspon();
            c.setWorkflowStatus(s);
            c.setProjectId("PJ1");
            c.setSubject("mock");
            User preparer = new User();
            preparer.setEmpNo("ZZA99");
            c.setCreatedBy(preparer);
            c.setUpdatedBy(preparer);
            setUpFromCorresponGroup(c, 1L);
            setUpAddresCorresponGroup(2L, 3L, 4L);

            Workflow w = new Workflow();
            w.setId(1L);
            User checker = new User();
            checker.setEmpNo("ZZA88");
            w.setUser(checker);
            w.setWorkflowType(WorkflowType.CHECKER);
            w.setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_CHECK);
            List<Workflow> workflows = new ArrayList<Workflow>();
            workflows.add(w);
            MockWorkflowDao.RET_FIND_BY_CORRESPON_ID = workflows;

            MockCorresponDao.count = 0;
            setUpCorresponType(c);
            MockCorresponDao.RET_FIND_BY_ID.put("1", c);
            MockAbstractService.IS_SYSTEM_ADMIN = false;
            MockAbstractService.IS_PROJECT_ADMIN = false;
            MockAbstractService.IS_ANY_GROUP_ADMIN = false;

            MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
            try {
                service.find(1L);
                fail("例外が発生していない");
            } catch (ServiceAbortException actual) {
                assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT,
                    actual.getMessageCode());

            }
        }
    }

    private void setUpAddresCorresponGroup(Long... ids) {
        List<AddressCorresponGroup> groups = new ArrayList<AddressCorresponGroup>();
        for (Long id : ids) {
            AddressCorresponGroup ag = new AddressCorresponGroup();
            ag.setId(id);
            CorresponGroup g = new CorresponGroup();
            g.setId(id);
            ag.setCorresponGroup(g);
            groups.add(ag);
        }

        MockAddressCorresponGroupDao.RET_FIND_BY_CORRESPON_ID = groups;
    }

    private void setUpFromCorresponGroup(Correspon c, Long id) {
        CorresponGroup from = new CorresponGroup();
        from.setId(id);
        c.setFromCorresponGroup(from);
    }

    /**
     * 不正な引数で例外が発生するか検証する.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testFindByIdException() throws Exception {
        service.find(null);
        fail("例外が発生していない");
    }

    /**
     * レコードが見つからない場合、正しい例外が発生するか検証する.
     */
    @Test
    public void testFindByIdRecordNotFound() throws Exception {
        MockCorresponDao.RET_FIND_BY_ID.put("1", null);
        try {
            service.find(100L);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.NO_DATA_FOUND, actual.getMessageCode());
        }
    }

    /**
     * 検証・承認を依頼する処理のテスト 承認パターン1の場合
     */
    @Test
    public void testRequestForApproval1() throws Exception {
        // テストに必要な値をセットする
        Correspon c = createRequestForVerificationData();

        // 承認フロー種別にpattern1をセットする
        WorkflowPattern wp = new WorkflowPattern();
        wp.setWorkflowCd("001");
        CorresponType ct = c.getCorresponType();
        ct.setWorkflowPattern(wp);
        c.setCorresponType(ct);

        MockCorresponDao.RET_FIND_BY_ID.put("1", c);

        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        List<Project> accessibleProjects = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        accessibleProjects.add(p);
        MockAbstractService.ACCESSIBLE_PROJECTS = accessibleProjects;

        // ログインユーザー
        User loginUser = new User();
        loginUser.setEmpNo("00001");
        loginUser.setNameE("Test User");
        MockAbstractService.CURRENT_USER = loginUser;

        assertEquals(WorkflowStatus.DRAFT, c.getWorkflowStatus());
        // テスト実行
        service.requestForApproval(c);

        assertEquals(WorkflowStatus.DRAFT, c.getWorkflowStatus());
        assertEquals(WorkflowProcessStatus.NONE, c.getWorkflows().get(0).getWorkflowProcessStatus());
        assertEquals(WorkflowProcessStatus.NONE, c.getWorkflows().get(1).getWorkflowProcessStatus());
        assertEquals(WorkflowProcessStatus.NONE, c.getWorkflows().get(2).getWorkflowProcessStatus());
        assertEquals("YOC:OT:BUILDING-00002", c.getCorresponNo());
        assertEquals("80001", c.getWorkflows().get(0).getUser().getEmpNo());
        assertEquals("80002", c.getWorkflows().get(1).getUser().getEmpNo());
        assertEquals("80003", c.getWorkflows().get(2).getUser().getEmpNo());

        // 更新後のコレポン文書、承認フローを確認
        assertEquals(WorkflowStatus.REQUEST_FOR_CHECK, MockCorresponDao.RET_UPDATE_CORRESPON
            .getWorkflowStatus());
        assertEquals(loginUser.toString(), MockCorresponDao.RET_UPDATE_CORRESPON.getUpdatedBy()
            .toString());
        assertEquals(c.getId(), MockCorresponDao.RET_UPDATE_CORRESPON.getId());
        assertEquals(c.getVersionNo(), MockCorresponDao.RET_UPDATE_CORRESPON.getVersionNo());
        assertNotNull(MockCorresponDao.RET_UPDATE_CORRESPON.getRequestedApprovalAt());

        assertEquals(MockWorkflowDao.RET_UPDATE_WORKFLOW.size(), 1);
        assertEquals(WorkflowProcessStatus.REQUEST_FOR_CHECK, MockWorkflowDao.RET_UPDATE_WORKFLOW
            .get(0).getWorkflowProcessStatus());
        assertEquals(c.getWorkflows().get(0).getId(), MockWorkflowDao.RET_UPDATE_WORKFLOW.get(0)
            .getId());
        assertEquals(loginUser.toString(), MockWorkflowDao.RET_UPDATE_WORKFLOW.get(0)
            .getUpdatedBy().toString());
        assertEquals(c.getWorkflows().get(0).getVersionNo(), MockWorkflowDao.RET_UPDATE_WORKFLOW
            .get(0).getVersionNo());
    }

    /**
     * 検証・承認を依頼する処理のテスト 承認パターン2の場合
     */
    @Test
    public void testRequestForApproval2() throws Exception {
        // テストに必要な値をセットする
        Correspon c = createRequestForVerificationData();

        // 承認フロー種別にpattern2をセットする
        WorkflowPattern wp = new WorkflowPattern();
        wp.setWorkflowCd("002");
        CorresponType ct = c.getCorresponType();
        ct.setWorkflowPattern(wp);
        c.setCorresponType(ct);

        MockCorresponDao.RET_FIND_BY_ID.put("1", c);

        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        List<Project> accessibleProjects = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        accessibleProjects.add(p);
        MockAbstractService.ACCESSIBLE_PROJECTS = accessibleProjects;
        // ログインユーザー
        User loginUser = new User();
        loginUser.setEmpNo("00001");
        loginUser.setNameE("Test User");
        MockAbstractService.CURRENT_USER = loginUser;

        assertEquals(WorkflowStatus.DRAFT, c.getWorkflowStatus());

        // テスト実行
        service.requestForApproval(c);

        assertEquals(WorkflowStatus.DRAFT, c.getWorkflowStatus());
        assertEquals(WorkflowProcessStatus.NONE, c.getWorkflows().get(0).getWorkflowProcessStatus());
        assertEquals(WorkflowProcessStatus.NONE, c.getWorkflows().get(1).getWorkflowProcessStatus());
        assertEquals(WorkflowProcessStatus.NONE, c.getWorkflows().get(2).getWorkflowProcessStatus());
        assertEquals("YOC:OT:BUILDING-00002", c.getCorresponNo());
        assertEquals("80001", c.getWorkflows().get(0).getUser().getEmpNo());
        assertEquals("80002", c.getWorkflows().get(1).getUser().getEmpNo());
        assertEquals("80003", c.getWorkflows().get(2).getUser().getEmpNo());

        // 更新後のコレポン文書、承認フローを確認
        assertEquals(WorkflowStatus.REQUEST_FOR_CHECK, MockCorresponDao.RET_UPDATE_CORRESPON
            .getWorkflowStatus());
        assertEquals(loginUser.toString(), MockCorresponDao.RET_UPDATE_CORRESPON.getUpdatedBy()
            .toString());
        assertEquals(c.getId(), MockCorresponDao.RET_UPDATE_CORRESPON.getId());
        assertEquals(c.getVersionNo(), MockCorresponDao.RET_UPDATE_CORRESPON.getVersionNo());

        assertEquals(MockWorkflowDao.RET_UPDATE_WORKFLOW.size(), 2);
        assertEquals(WorkflowProcessStatus.REQUEST_FOR_CHECK, MockWorkflowDao.RET_UPDATE_WORKFLOW
            .get(0).getWorkflowProcessStatus());
        assertEquals(c.getWorkflows().get(0).getId(), MockWorkflowDao.RET_UPDATE_WORKFLOW.get(0)
            .getId());
        assertEquals(loginUser.toString(), MockWorkflowDao.RET_UPDATE_WORKFLOW.get(0)
            .getUpdatedBy().toString());
        assertEquals(c.getWorkflows().get(0).getVersionNo(), MockWorkflowDao.RET_UPDATE_WORKFLOW
            .get(0).getVersionNo());
        assertNotNull(MockCorresponDao.RET_UPDATE_CORRESPON.getRequestedApprovalAt());

        assertEquals(WorkflowProcessStatus.REQUEST_FOR_CHECK, MockWorkflowDao.RET_UPDATE_WORKFLOW
            .get(1).getWorkflowProcessStatus());
        assertEquals(c.getWorkflows().get(1).getId(), MockWorkflowDao.RET_UPDATE_WORKFLOW.get(1)
            .getId());
        assertEquals(loginUser.toString(), MockWorkflowDao.RET_UPDATE_WORKFLOW.get(1)
            .getUpdatedBy().toString());
        assertEquals(c.getWorkflows().get(1).getVersionNo(), MockWorkflowDao.RET_UPDATE_WORKFLOW
            .get(1).getVersionNo());
    }

    /**
     * 検証・承認を依頼する処理のテスト 承認パターン3の場合
     */
    @Test
    public void testRequestForApproval3() throws Exception {
        // テストに必要な値をセットする
        Correspon c = createRequestForVerificationData();

        // 承認フロー種別にpattern3をセットする
        WorkflowPattern wp = new WorkflowPattern();
        wp.setWorkflowCd("003");
        CorresponType ct = c.getCorresponType();
        ct.setWorkflowPattern(wp);
        c.setCorresponType(ct);

        MockCorresponDao.RET_FIND_BY_ID.put("1", c);

        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        List<Project> accessibleProjects = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        accessibleProjects.add(p);
        MockAbstractService.ACCESSIBLE_PROJECTS = accessibleProjects;
        // ログインユーザー
        User loginUser = new User();
        loginUser.setEmpNo("00001");
        loginUser.setNameE("Test User");
        MockAbstractService.CURRENT_USER = loginUser;

        assertEquals(WorkflowStatus.DRAFT, c.getWorkflowStatus());

        // テスト実行
        service.requestForApproval(c);

        assertEquals(WorkflowStatus.DRAFT, c.getWorkflowStatus());
        assertEquals(WorkflowProcessStatus.NONE, c.getWorkflows().get(0).getWorkflowProcessStatus());
        assertEquals(WorkflowProcessStatus.NONE, c.getWorkflows().get(1).getWorkflowProcessStatus());
        assertEquals(WorkflowProcessStatus.NONE, c.getWorkflows().get(2).getWorkflowProcessStatus());
        assertEquals("YOC:OT:BUILDING-00002", c.getCorresponNo());
        assertEquals("80001", c.getWorkflows().get(0).getUser().getEmpNo());
        assertEquals("80002", c.getWorkflows().get(1).getUser().getEmpNo());
        assertEquals("80003", c.getWorkflows().get(2).getUser().getEmpNo());

        // 更新後のコレポン文書、承認フローを確認
        assertEquals(WorkflowStatus.REQUEST_FOR_CHECK, MockCorresponDao.RET_UPDATE_CORRESPON
            .getWorkflowStatus());
        assertEquals(loginUser.toString(), MockCorresponDao.RET_UPDATE_CORRESPON.getUpdatedBy()
            .toString());
        assertEquals(c.getId(), MockCorresponDao.RET_UPDATE_CORRESPON.getId());
        assertEquals(c.getVersionNo(), MockCorresponDao.RET_UPDATE_CORRESPON.getVersionNo());

        assertEquals(MockWorkflowDao.RET_UPDATE_WORKFLOW.size(), 3);
        assertEquals(WorkflowProcessStatus.REQUEST_FOR_CHECK, MockWorkflowDao.RET_UPDATE_WORKFLOW
            .get(0).getWorkflowProcessStatus());
        assertEquals(c.getWorkflows().get(0).getId(), MockWorkflowDao.RET_UPDATE_WORKFLOW.get(0)
            .getId());
        assertEquals(loginUser.toString(), MockWorkflowDao.RET_UPDATE_WORKFLOW.get(0)
            .getUpdatedBy().toString());
        assertEquals(c.getWorkflows().get(0).getVersionNo(), MockWorkflowDao.RET_UPDATE_WORKFLOW
            .get(0).getVersionNo());
        assertNotNull(MockCorresponDao.RET_UPDATE_CORRESPON.getRequestedApprovalAt());

        assertEquals(WorkflowProcessStatus.REQUEST_FOR_CHECK, MockWorkflowDao.RET_UPDATE_WORKFLOW
            .get(1).getWorkflowProcessStatus());
        assertEquals(c.getWorkflows().get(1).getId(), MockWorkflowDao.RET_UPDATE_WORKFLOW.get(1)
            .getId());
        assertEquals(loginUser.toString(), MockWorkflowDao.RET_UPDATE_WORKFLOW.get(1)
            .getUpdatedBy().toString());
        assertEquals(c.getWorkflows().get(1).getVersionNo(), MockWorkflowDao.RET_UPDATE_WORKFLOW
            .get(1).getVersionNo());

        assertEquals(WorkflowProcessStatus.REQUEST_FOR_APPROVAL,
            MockWorkflowDao.RET_UPDATE_WORKFLOW.get(2).getWorkflowProcessStatus());
        assertEquals(c.getWorkflows().get(2).getId(), MockWorkflowDao.RET_UPDATE_WORKFLOW.get(2)
            .getId());
        assertEquals(loginUser.toString(), MockWorkflowDao.RET_UPDATE_WORKFLOW.get(2)
            .getUpdatedBy().toString());
        assertEquals(c.getWorkflows().get(2).getVersionNo(), MockWorkflowDao.RET_UPDATE_WORKFLOW
            .get(2).getVersionNo());
    }

    /**
     * 検証・承認を依頼する処理のテスト Approverが一人のみ存在するパターンを検証
     */
    @Test
    public void testRequestForApproval4() throws Exception {
        // テストに必要な値をセットする
        Correspon c = createRequestForVerificationData();

        // 承認フローにApproverのみ存在
        List<Workflow> w = c.getWorkflows();
        w.remove(0);
        w.remove(0);

        // 承認フロー種別にpattern3をセットする
        WorkflowPattern wp = new WorkflowPattern();
        wp.setWorkflowCd("001");
        CorresponType ct = c.getCorresponType();
        ct.setWorkflowPattern(wp);
        c.setCorresponType(ct);

        MockCorresponDao.RET_FIND_BY_ID.put("1", c);

        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        List<Project> accessibleProjects = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        accessibleProjects.add(p);
        MockAbstractService.ACCESSIBLE_PROJECTS = accessibleProjects;
        // ログインユーザー
        User loginUser = new User();
        loginUser.setEmpNo("00001");
        loginUser.setNameE("Test User");
        MockAbstractService.CURRENT_USER = loginUser;


        assertEquals(WorkflowStatus.DRAFT, c.getWorkflowStatus());

        // テスト実行
        service.requestForApproval(c);

        assertEquals(WorkflowStatus.DRAFT, c.getWorkflowStatus());
        assertEquals(WorkflowProcessStatus.NONE, c.getWorkflows().get(0).getWorkflowProcessStatus());
        assertEquals("YOC:OT:BUILDING-00002", c.getCorresponNo());

        // 更新後のコレポン文書、承認フローを確認
        assertEquals(WorkflowStatus.REQUEST_FOR_APPROVAL, MockCorresponDao.RET_UPDATE_CORRESPON
            .getWorkflowStatus());
        assertEquals(loginUser.toString(), MockCorresponDao.RET_UPDATE_CORRESPON.getUpdatedBy()
            .toString());
        assertEquals(c.getId(), MockCorresponDao.RET_UPDATE_CORRESPON.getId());
        assertEquals(c.getVersionNo(), MockCorresponDao.RET_UPDATE_CORRESPON.getVersionNo());
        assertNotNull(MockCorresponDao.RET_UPDATE_CORRESPON.getRequestedApprovalAt());

        assertEquals(MockWorkflowDao.RET_UPDATE_WORKFLOW.size(), 1);
        assertEquals(WorkflowProcessStatus.REQUEST_FOR_APPROVAL,
            MockWorkflowDao.RET_UPDATE_WORKFLOW.get(0).getWorkflowProcessStatus());
        assertEquals(c.getWorkflows().get(0).getId(), MockWorkflowDao.RET_UPDATE_WORKFLOW.get(0)
            .getId());
        assertEquals(loginUser.toString(), MockWorkflowDao.RET_UPDATE_WORKFLOW.get(0)
            .getUpdatedBy().toString());
        assertEquals(c.getWorkflows().get(0).getVersionNo(), MockWorkflowDao.RET_UPDATE_WORKFLOW
            .get(0).getVersionNo());
    }

    /**
     * 検証・承認を依頼する処理のテスト CheckerもApproverも存在しないパターンを検証
     */
    @Test
    public void testRequestForApproval5() throws Exception {
        // テストに必要な値をセットする
        Correspon c = createRequestForVerificationData();

        // 承認フローにApproverのみ存在
        List<Workflow> w = c.getWorkflows();
        for (Workflow workflow : w) {
            workflow.setWorkflowType(null);
        }

        // 承認フロー種別にpattern3をセットする
        WorkflowPattern wp = new WorkflowPattern();
        wp.setWorkflowCd("001");
        CorresponType ct = c.getCorresponType();
        ct.setWorkflowPattern(wp);
        c.setCorresponType(ct);

        MockCorresponDao.RET_FIND_BY_ID.put("1", c);

        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        List<Project> accessibleProjects = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        accessibleProjects.add(p);
        MockAbstractService.ACCESSIBLE_PROJECTS = accessibleProjects;
        // ログインユーザー
        User loginUser = new User();
        loginUser.setEmpNo("00001");
        loginUser.setNameE("Test User");
        MockAbstractService.CURRENT_USER = loginUser;

        try {
            // テスト実行
            service.requestForApproval(c);

            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.NO_APPROVER_SPECIFIED, actual.getMessageCode());
        }
    }

    /**
     * 指定のコレポン情報のプロジェクトが現在選択中のプロジェクトと違う場合を検証
     * @throws Exception
     */
    @Test
    public void testRequestForApprovalCheck1() throws Exception {
        // テストに必要なデータを作成
        Correspon c = createRequestForVerificationData();

        MockCorresponDao.RET_FIND_BY_ID.put("1", c);

        MockAbstractService.CURRENT_PROJECT_ID = "PJ2";
        try {
            // テスト実行
            service.requestForApproval(c);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_PROJECT_DIFF, actual
                .getMessageCode());
        }
    }

    /**
     * 不正な引数で例外が発生するか検証する.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testRequestForApprovalCheck2() throws Exception {
        // テスト実行
        service.requestForApproval(null);
        fail("例外が発生していない");

    }

    /**
     * SystemAdmin又は、Preparer以外のユーザーを検証する
     */
    @Test
    public void testRequestForApprovalCheck4() throws Exception {
        // テストに必要なデータを作成
        Correspon c = createRequestForVerificationData();

        MockCorresponDao.RET_FIND_BY_ID.put("1", c);
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        List<Project> accessibleProjects = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        accessibleProjects.add(p);
        MockAbstractService.ACCESSIBLE_PROJECTS = accessibleProjects;

        MockAbstractService.IS_SYSTEM_ADMIN = false;
        // ログインユーザー
        User loginUser = new User();
        loginUser.setEmpNo("00003");
        loginUser.setNameE("Test User3");
        MockAbstractService.CURRENT_USER = loginUser;

        try {
            // テスト実行
            service.requestForApproval(c);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT,
                actual.getMessageCode());
        }
    }

    /**
     * SystemAdmin又は、Preparerで指定のコレポン文書の承認状態がDRAFT以外の場合を検証
     */
    @Test
    public void testRequestForApprovalCheck5() throws Exception {
        // テストに必要なデータを作成
        Correspon c = createRequestForVerificationData();
        c.setWorkflowStatus(WorkflowStatus.REQUEST_FOR_CHECK);

        MockCorresponDao.RET_FIND_BY_ID.put("1", c);
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        MockAbstractService.IS_SYSTEM_ADMIN = true;
        // ログインユーザー
        User loginUser = new User();
        loginUser.setEmpNo("00003");
        loginUser.setNameE("Test User3");
        MockAbstractService.CURRENT_USER = loginUser;

        try {
            // テスト実行
            service.requestForApproval(c);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CORRESPON_STATUS_INVALID,
                actual.getMessageCode());
        }
    }

    /**
     * 承認フローにApproverが設定されていない場合を検証
     */
    @Test
    public void testRequestForApprovalCheck6() throws Exception {
        // テストに必要なデータを作成
        Correspon c = createRequestForVerificationData();
        List<Workflow> w = c.getWorkflows();
        // 承認フローに設定されている役割を全てCheckerにする
        w.get(2).setWorkflowType(WorkflowType.CHECKER);

        MockCorresponDao.RET_FIND_BY_ID.put("1", c);
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        MockAbstractService.IS_SYSTEM_ADMIN = true;
        // ログインユーザー
        User loginUser = new User();
        loginUser.setEmpNo("00003");
        loginUser.setNameE("Test User3");
        MockAbstractService.CURRENT_USER = loginUser;

        try {
            // テスト実行
            service.requestForApproval(c);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.NO_APPROVER_SPECIFIED, actual.getMessageCode());
        }
    }

    /**
     * 承認フローがない場合を検証(承認フローリストが空)
     */
    @Test
    public void testRequestForApprovalCheck7() throws Exception {
        // テストに必要なデータを作成
        Correspon c = createRequestForVerificationData();
        c.setWorkflows(new ArrayList<Workflow>());

        MockCorresponDao.RET_FIND_BY_ID.put("1", c);
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        MockAbstractService.IS_SYSTEM_ADMIN = true;
        // ログインユーザー
        User loginUser = new User();
        loginUser.setEmpNo("00003");
        loginUser.setNameE("Test User3");
        MockAbstractService.CURRENT_USER = loginUser;

        try {
            // テスト実行
            service.requestForApproval(c);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.NO_APPROVER_SPECIFIED, actual.getMessageCode());
        }
    }

    /**
     * 承認フローがない場合を検証(承認フローリストがnull)
     */
    @Test
    public void testRequestForApprovalCheck8() throws Exception {
        // テストに必要なデータを作成
        Correspon c = createRequestForVerificationData();
        c.setWorkflows(null);

        MockCorresponDao.RET_FIND_BY_ID.put("1", c);
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        MockAbstractService.IS_SYSTEM_ADMIN = true;
        // ログインユーザー
        User loginUser = new User();
        loginUser.setEmpNo("00003");
        loginUser.setNameE("Test User3");
        MockAbstractService.CURRENT_USER = loginUser;

        try {
            // テスト実行
            service.requestForApproval(c);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.NO_APPROVER_SPECIFIED, actual.getMessageCode());
        }
    }

    /**
     * 承認フローパターンが予期せぬ値の場合の検証
     */
    @Test
    public void testRequestForApprovalCheck9() throws Exception {
        // テストに必要な値をセットする
        Correspon c = createRequestForVerificationData();

        // 承認フロー種別にpattern3をセットする
        WorkflowPattern wp = new WorkflowPattern();
        wp.setWorkflowCd("pattern5");
        CorresponType ct = c.getCorresponType();
        ct.setWorkflowPattern(wp);
        c.setCorresponType(ct);

        MockCorresponDao.RET_FIND_BY_ID.put("1", c);

        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        List<Project> accessibleProjects = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        accessibleProjects.add(p);
        MockAbstractService.ACCESSIBLE_PROJECTS = accessibleProjects;
        // ログインユーザー
        User loginUser = new User();
        loginUser.setEmpNo("00001");
        loginUser.setNameE("Test User");
        MockAbstractService.CURRENT_USER = loginUser;

        try {
            // テスト実行
            service.requestForApproval(c);
            fail("例外が発生していない");
        } catch (ApplicationFatalRuntimeException actual) {
            assertEquals("WorkflowPattern Invalid pattern5", actual.getMessage());
        }

    }

    /**
     * 検証・承認を依頼する処理のテスト コレポン文書の更新処理に失敗した場合を検証.
     */
    @Test
    public void testRequestForApprovalCheckException1() throws Exception {
        // テストに必要な値をセットする
        Correspon c = createRequestForVerificationData();

        // 承認フロー種別にpattern1をセットする
        WorkflowPattern wp = new WorkflowPattern();
        wp.setWorkflowCd("001");
        CorresponType ct = c.getCorresponType();
        ct.setWorkflowPattern(wp);
        c.setCorresponType(ct);

        MockCorresponDao.RET_FIND_BY_ID.put("1", c);

        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        List<Project> accessibleProjects = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        accessibleProjects.add(p);
        MockAbstractService.ACCESSIBLE_PROJECTS = accessibleProjects;

        // ログインユーザー
        User loginUser = new User();
        loginUser.setEmpNo("00001");
        loginUser.setNameE("Test User");
        MockAbstractService.CURRENT_USER = loginUser;

        assertEquals(WorkflowStatus.DRAFT, c.getWorkflowStatus());

        // Exceptionを発生させる
        MockAbstractDao.EXCEPTION = true;

        try {
            // テスト実行
            service.requestForApproval(c);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(WorkflowStatus.DRAFT, c.getWorkflowStatus());
            assertEquals(WorkflowProcessStatus.NONE, c.getWorkflows().get(0)
                .getWorkflowProcessStatus());
            assertEquals(WorkflowProcessStatus.NONE, c.getWorkflows().get(1)
                .getWorkflowProcessStatus());
            assertEquals(WorkflowProcessStatus.NONE, c.getWorkflows().get(2)
                .getWorkflowProcessStatus());
            assertEquals("YOC:OT:BUILDING-00002", c.getCorresponNo());
            assertEquals("80001", c.getWorkflows().get(0).getUser().getEmpNo());
            assertEquals("80002", c.getWorkflows().get(1).getUser().getEmpNo());
            assertEquals("80003", c.getWorkflows().get(2).getUser().getEmpNo());

        }

    }

    /**
     * 検証・承認を依頼する処理のテスト 承認フローの更新処理に失敗した場合を検証.
     */
    @Test
    public void testRequestForApprovalCheckException2() throws Exception {
        // テストに必要な値をセットする
        Correspon c = createRequestForVerificationData();

        // 承認フロー種別にpattern1をセットする
        WorkflowPattern wp = new WorkflowPattern();
        wp.setWorkflowCd("001");
        CorresponType ct = c.getCorresponType();
        ct.setWorkflowPattern(wp);
        c.setCorresponType(ct);

        MockCorresponDao.RET_FIND_BY_ID.put("1", c);

        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        List<Project> accessibleProjects = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        accessibleProjects.add(p);
        MockAbstractService.ACCESSIBLE_PROJECTS = accessibleProjects;

        // ログインユーザー
        User loginUser = new User();
        loginUser.setEmpNo("00001");
        loginUser.setNameE("Test User");
        MockAbstractService.CURRENT_USER = loginUser;

        assertEquals(WorkflowStatus.DRAFT, c.getWorkflowStatus());

        // Exceptionを発生させる
        MockAbstractDao.ExceptionWorkflow = true;

        try {
            // テスト実行
            service.requestForApproval(c);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(WorkflowStatus.DRAFT, c.getWorkflowStatus());
            assertEquals(WorkflowProcessStatus.NONE, c.getWorkflows().get(0)
                .getWorkflowProcessStatus());
            assertEquals(WorkflowProcessStatus.NONE, c.getWorkflows().get(1)
                .getWorkflowProcessStatus());
            assertEquals(WorkflowProcessStatus.NONE, c.getWorkflows().get(2)
                .getWorkflowProcessStatus());
            assertEquals("YOC:OT:BUILDING-00002", c.getCorresponNo());
            assertEquals("80001", c.getWorkflows().get(0).getUser().getEmpNo());
            assertEquals("80002", c.getWorkflows().get(1).getUser().getEmpNo());
            assertEquals("80003", c.getWorkflows().get(2).getUser().getEmpNo());

        }

    }

    /**
     * 検証・承認を依頼する処理に必要なデータを作成する
     * (WorkflowPettern.workflowCdについては各メソッド毎にテスト内容に応じてセットする)
     * @return
     */
    private Correspon createRequestForVerificationData() {
        Correspon c = new Correspon();
        c.setId(99L);
        c.setProjectId("PJ1");
        c.setCorresponNo("YOC:OT:BUILDING-00002");
        c.setParentCorresponId(new Long(1L));
        c.setCorresponStatus(CorresponStatus.OPEN);
        c.setWorkflowStatus(WorkflowStatus.DRAFT);
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
        c.setFile1FileId("12345");
        c.setFile1Id(new Long(1));
        c.setFile1FileName("test.xls");
        c.setFile2Id(new Long(2));
        c.setFile2FileId("67890");
        c.setFile2FileName("test.txt");
        c.setCustomField1Id(new Long(1));
        c.setCustomField1Label("CustomFieldLabel1");
        c.setCustomField1Value("This is customField1Value.");
        c.setCreatedAt(new GregorianCalendar(2009, 3, 1, 9, 3, 23).getTime());
        User u = new User();
        u.setEmpNo("00001");
        u.setNameE("Test User");
        c.setCreatedBy(u);

        // 承認フロー
        Workflow w;
        User user;
        List<Workflow> wfs = new ArrayList<Workflow>();

        w = new Workflow();
        w.setId(new Long(10));
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
        w.setId(new Long(20));
        w.setWorkflowNo(new Long(2));
        w.setVersionNo(1L);

        user = new User();
        user.setEmpNo("80002");
        user.setNameE("Checker2 User");
        w.setUser(user);
        w.setCreatedBy(user);
        w.setUpdatedBy(user);

        w.setWorkflowType(WorkflowType.CHECKER);
        w.setWorkflowProcessStatus(WorkflowProcessStatus.NONE);

        user = new User();
        user.setEmpNo("80002");
        user.setNameE("Checker2 User");
        w.setFinishedBy(user);

        w.setFinishedAt(new GregorianCalendar(2009, 3, 1, 12, 34, 56).getTime());
        wfs.add(w);

        w = new Workflow();
        w.setId(new Long(30));
        w.setWorkflowNo(new Long(3));
        w.setVersionNo(1L);

        user = new User();
        user.setEmpNo("80003");
        user.setNameE("Approver User");
        w.setUser(user);
        w.setCreatedBy(user);
        w.setUpdatedBy(user);

        w.setWorkflowType(WorkflowType.APPROVER);
        w.setWorkflowProcessStatus(WorkflowProcessStatus.NONE);
        wfs.add(w);

        c.setWorkflows(wfs);

        return c;
    }

    /**
     * 指定されたコレポン文書をHTML形式に変換して返すテストケース.
     */
    @Test
    public void testGenerateHTML() throws Exception {
        MockAbstractService.BASE_URL = "http://localhost:8001/";

        Correspon c = new Correspon();
        c.setId(1L);
        c.setCorresponNo("YOC:OT:BUILDING-00001");
        c.setParentCorresponId(2L);
        c.setParentCorresponNo("YOC:OT:BUILDING-00000");
        c.setPreviousRevCorresponId(3L);
        c.setPreviousRevCorresponNo("YOC:OT:IT-99999");
        CorresponGroup from = new CorresponGroup();
        from.setId(new Long(1L));
        from.setName("YOC:BUILDING");
        c.setFromCorresponGroup(from);
        c.setSubject("Mock");
        c.setBody("this is test.");
        List<AddressCorresponGroup> addressGroups = new ArrayList<AddressCorresponGroup>();
        AddressCorresponGroup addressGroup = new AddressCorresponGroup();
        CorresponGroup group = new CorresponGroup();
        group.setId(2L);
        group.setName("YOC:IT");
        addressGroup.setCorresponGroup(group);
        addressGroup.setAddressType(AddressType.TO);
        List<AddressUser> users = new ArrayList<AddressUser>();
        AddressUser addressUser = new AddressUser();
        addressUser.setAddressUserType(AddressUserType.ATTENTION);
        User u = new User();
        u.setEmpNo("00011");
        u.setNameE("Attention User");
        addressUser.setUser(u);
        List<PersonInCharge> personInCharges = new ArrayList<PersonInCharge>();
        PersonInCharge pic = new PersonInCharge();
        u = new User();
        u.setEmpNo("00022");
        u.setNameE("PIC User");
        pic.setUser(u);
        personInCharges.add(pic);
        addressUser.setPersonInCharges(personInCharges);
        users.add(addressUser);
        addressUser = new AddressUser();
        addressUser.setAddressUserType(AddressUserType.NORMAL_USER);
        u = new User();
        u.setEmpNo("00033");
        u.setNameE("Normal User");
        addressUser.setUser(u);
        users.add(addressUser);
        addressGroup.setUsers(users);
        addressGroups.add(addressGroup);
        addressGroup = new AddressCorresponGroup();
        group = new CorresponGroup();
        group.setId(2L);
        group.setName("TOK:BUILDING");
        addressGroup.setCorresponGroup(group);
        addressGroup.setAddressType(AddressType.CC);
        users = new ArrayList<AddressUser>();
        addressUser = new AddressUser();
        addressUser.setAddressUserType(AddressUserType.NORMAL_USER);
        u = new User();
        u.setEmpNo("00044");
        u.setNameE("Normal User");
        addressUser.setUser(u);
        users.add(addressUser);
        addressGroup.setUsers(users);
        addressGroups.add(addressGroup);
        c.setAddressCorresponGroups(addressGroups);
        CorresponType ct = new CorresponType();
        ct.setId(new Long(1L));
        ct.setName("Request");
        c.setCorresponType(ct);
        c.setWorkflowStatus(WorkflowStatus.DRAFT);
        c.setCreatedAt(new GregorianCalendar(2009, 3, 1, 1, 1, 1).getTime());
        c.setUpdatedAt(new GregorianCalendar(2009, 3, 10, 10, 10, 10).getTime());
        u = new User();
        u.setEmpNo("00001");
        u.setNameE("Test User");
        c.setCreatedBy(u);
        c.setUpdatedBy(u);
        c.setReplyRequired(ReplyRequired.YES);
        c.setDeadlineForReply(new GregorianCalendar(2009, 4, 1).getTime());
        c.setCorresponStatus(CorresponStatus.OPEN);
        c.setCustomField1Label("FIELD1");
        c.setCustomField1Value("VALUE1");
        c.setCustomField2Label("FIELD2");
        c.setCustomField2Value("VALUE2");
        c.setCustomField3Label("FIELD3");
        c.setCustomField3Value("VALUE3");
        c.setCustomField4Label("FIELD4");
        c.setCustomField4Value("VALUE4");
        c.setCustomField5Label("FIELD5");
        c.setCustomField5Value("VALUE5");
        c.setCustomField6Label("FIELD6");
        c.setCustomField6Value("VALUE6");
        c.setCustomField7Label("FIELD7");
        c.setCustomField7Value("VALUE7");
        c.setCustomField8Label("FIELD8");
        c.setCustomField8Value("VALUE8");
        c.setCustomField9Label("FIELD9");
        c.setCustomField9Value("VALUE9");
        c.setCustomField10Label("FIELD10");
        c.setCustomField10Value("VALUE10");

        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat f2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String html = new String(service.generateHTML(c, null));

        // 印刷用のscriptが表示されていること
        assertTrue(html.contains("<body onload=\"window.print();\">"));
        assertTrue(html.contains(">Print</a>"));
        assertTrue(html.contains(">Close</a>"));
        assertFalse(html.contains(">ZIP</a>"));

        // 各値の確認
        assertTrue(c.getId().toString(), html.contains(getExpectedHTMLString(c.getId().toString())));
        assertTrue(c.getCorresponStatus().getLabel(), html.contains(getExpectedHTMLString(c
            .getCorresponStatus().getLabel())));
        assertTrue(c.getCorresponNo(), html.contains(getExpectedHTMLString(c.getCorresponNo())));
        assertTrue(c.getPreviousRevCorresponNo(), html.contains(getExpectedHTMLString(c
            .getPreviousRevCorresponNo())));
        assertTrue(c.getParentCorresponNo(), html.contains(getExpectedHTMLString(c
            .getParentCorresponNo())));
        assertTrue(c.getWorkflowStatus().getLabel(), html.contains(getExpectedHTMLString(c
            .getWorkflowStatus().getLabel())));
        assertTrue(c.getFromCorresponGroup().getName(), html.contains(getExpectedHTMLString(c
            .getFromCorresponGroup().getName())));
        if (c.getAddressCorresponGroups() != null) {
            for (AddressCorresponGroup addressCorresponGroup : c.getAddressCorresponGroups()) {
                assertTrue(addressCorresponGroup.getCorresponGroup().getName(), html
                    .contains(addressCorresponGroup.getCorresponGroup().getName()));

                for (AddressUser aUser : addressCorresponGroup.getUsers()) {
                    if (addressCorresponGroup.isTo() && aUser.isAttention()) {
                        assertTrue(aUser.getUser().getLabel(), html.contains(aUser.getUser()
                            .getLabel()));
                        assertTrue(aUser.getPersonInCharges().get(0).getUser().getLabel(), html
                            .contains(aUser.getPersonInCharges().get(0).getUser().getLabel()));
                    } else if (addressCorresponGroup.isTo() && !aUser.isAttention()) {
                        assertFalse(aUser.getUser().getLabel(), html.contains(aUser.getUser()
                            .getLabel()));
                    } else if (addressCorresponGroup.isCc() && aUser.isCc()) {
                        assertTrue(aUser.getUser().getLabel(), html.contains(aUser.getUser()
                            .getLabel()));
                    }
                }
            }
        }
        assertTrue(c.getCorresponType().getLabel(), html.contains(getExpectedHTMLString(c
            .getCorresponType().getLabel())));
        assertTrue(c.getSubject(), html.contains(getExpectedHTMLString(c.getSubject())));
        assertTrue(c.getBody(), html.contains(getExpectedString(c.getBody())));
        assertTrue(f.format(c.getDeadlineForReply()), html.contains(f.format(c
            .getDeadlineForReply())));
        if (c.getFile1FileName() != null) {
            assertTrue(c.getFile1FileName(), html.contains(c.getFile1FileName()));
        }
        if (c.getFile2FileName() != null) {
            assertTrue(c.getFile2FileName(), html.contains(c.getFile2FileName()));
        }
        if (c.getFile3FileName() != null) {
            assertTrue(c.getFile3FileName(), html.contains(c.getFile3FileName()));
        }
        if (c.getFile4FileName() != null) {
            assertTrue(c.getFile4FileName(), html.contains(c.getFile4FileName()));
        }
        if (c.getFile5FileName() != null) {
            assertTrue(c.getFile5FileName(), html.contains(c.getFile5FileName()));
        }
        if (c.getCustomField1Label() != null) {
            assertTrue(c.getCustomField1Label(), html.contains(getExpectedHTMLString(c
                .getCustomField1Label())));
            assertTrue(c.getCustomField1Value(), html.contains(c.getCustomField1Value()));
        }
        if (c.getCustomField2Label() != null) {
            assertTrue(c.getCustomField2Label(), html.contains(getExpectedHTMLString(c
                .getCustomField2Label())));
            assertTrue(c.getCustomField2Value(), html.contains(c.getCustomField2Value()));
        }
        if (c.getCustomField3Label() != null) {
            assertTrue(c.getCustomField3Label(), html.contains(getExpectedHTMLString(c
                .getCustomField3Label())));
            assertTrue(c.getCustomField3Value(), html.contains(c.getCustomField3Value()));
        }
        if (c.getCustomField4Label() != null) {
            assertTrue(c.getCustomField4Label(), html.contains(getExpectedHTMLString(c
                .getCustomField4Label())));
            assertTrue(c.getCustomField4Value(), html.contains(c.getCustomField4Value()));
        }
        if (c.getCustomField5Label() != null) {
            assertTrue(c.getCustomField5Label(), html.contains(getExpectedHTMLString(c
                .getCustomField5Label())));
            assertTrue(c.getCustomField5Value(), html.contains(c.getCustomField5Value()));
        }
        if (c.getCustomField6Label() != null) {
            assertTrue(c.getCustomField6Label(), html.contains(getExpectedHTMLString(c
                .getCustomField6Label())));
            assertTrue(c.getCustomField6Value(), html.contains(c.getCustomField6Value()));
        }
        if (c.getCustomField7Label() != null) {
            assertTrue(c.getCustomField7Label(), html.contains(getExpectedHTMLString(c
                .getCustomField7Label())));
            assertTrue(c.getCustomField7Value(), html.contains(c.getCustomField7Value()));
        }
        if (c.getCustomField8Label() != null) {
            assertTrue(c.getCustomField8Label(), html.contains(getExpectedHTMLString(c
                .getCustomField8Label())));
            assertTrue(c.getCustomField8Value(), html.contains(c.getCustomField8Value()));
        }
        if (c.getCustomField9Label() != null) {
            assertTrue(c.getCustomField9Label(), html.contains(getExpectedHTMLString(c
                .getCustomField9Label())));
            assertTrue(c.getCustomField9Value(), html.contains(c.getCustomField9Value()));
        }
        if (c.getCustomField10Label() != null) {
            assertTrue(c.getCustomField10Label(), html.contains(getExpectedHTMLString(c
                .getCustomField10Label())));
            assertTrue(c.getCustomField10Value(), html.contains(c.getCustomField10Value()));
        }
        assertTrue(c.getUpdatedAt().toString(), html.contains(getExpectedHTMLString(f2.format(c
            .getUpdatedAt()))));
        assertTrue(c.getUpdatedBy().getNameE() + "/" + c.getUpdatedBy().getEmpNo(), html.contains(c
            .getUpdatedBy().getNameE()
            + "/" + c.getUpdatedBy().getEmpNo()));
        if (c.getWorkflows() != null) {
            for (Workflow flow : c.getWorkflows()) {
                assertTrue(flow.getWorkflowNo().toString(), html
                    .contains(getExpectedHTMLString(flow.getWorkflowNo().toString())));
                assertTrue(flow.getWorkflowType().getLabel(), html
                    .contains(getExpectedHTMLString(flow.getWorkflowType().getLabel())));
                assertTrue(flow.getUser().getNameE() + "/" + flow.getUser().getEmpNo(), html
                    .contains(getExpectedHTMLString(flow.getUser().getNameE() + "/"
                        + flow.getUser().getEmpNo())));
                assertTrue(flow.getWorkflowProcessStatus().getLabel(), html
                    .contains(getExpectedHTMLString(flow.getWorkflowProcessStatus().getLabel())));
                if (flow.getUpdatedBy() != null) {
                    assertTrue(flow.getUpdatedBy().getNameE() + "/"
                        + flow.getUpdatedBy().getEmpNo(), html.contains(getExpectedHTMLString(flow
                        .getUpdatedBy().getNameE()
                        + "/" + flow.getUpdatedBy().getEmpNo())));
                    assertTrue(flow.getUpdatedAt().toString(), html.contains(f2.format(flow
                        .getUpdatedAt())));
                }
                assertTrue(flow.getCommentOn(), html.contains(getExpectedHTMLString(flow
                    .getCommentOn())));
            }
        }
    }

    /**
     * 指定されたコレポン文書をHTML形式に変換して返すテストケース. コレポン文書番号未設定の場合
     */
    @Test
    public void testGenerateHTMLCorresponNoNotAssined() throws Exception {
        Correspon c = new Correspon();
        c.setId(1L);
        c.setCorresponNo(null);
        c.setParentCorresponId(2L);
        c.setParentCorresponNo("YOC:OT:BUILDING-00000");
        c.setPreviousRevCorresponId(3L);
        c.setPreviousRevCorresponNo("YOC:OT:IT-99999");
        CorresponGroup from = new CorresponGroup();
        from.setId(new Long(1L));
        from.setName("YOC:BUILDING");
        c.setFromCorresponGroup(from);
        c.setSubject("Mock");
        c.setBody("This is test.");
        List<AddressCorresponGroup> addressGroups = new ArrayList<AddressCorresponGroup>();
        AddressCorresponGroup addressGroup = new AddressCorresponGroup();
        CorresponGroup group = new CorresponGroup();
        group.setId(2L);
        group.setName("YOC:IT");
        addressGroup.setCorresponGroup(group);
        addressGroup.setAddressType(AddressType.TO);
        List<AddressUser> users = new ArrayList<AddressUser>();
        AddressUser addressUser = new AddressUser();
        addressUser.setAddressUserType(AddressUserType.ATTENTION);
        User u = new User();
        u.setEmpNo("00011");
        u.setNameE("Attention User");
        addressUser.setUser(u);
        List<PersonInCharge> personInCharges = new ArrayList<PersonInCharge>();
        PersonInCharge pic = new PersonInCharge();
        u = new User();
        u.setEmpNo("00022");
        u.setNameE("PIC User");
        pic.setUser(u);
        personInCharges.add(pic);
        addressUser.setPersonInCharges(personInCharges);
        users.add(addressUser);
        addressUser = new AddressUser();
        addressUser.setAddressUserType(AddressUserType.NORMAL_USER);
        u = new User();
        u.setEmpNo("00033");
        u.setNameE("Normal User");
        addressUser.setUser(u);
        users.add(addressUser);
        addressGroup.setUsers(users);
        addressGroups.add(addressGroup);
        addressGroup = new AddressCorresponGroup();
        group = new CorresponGroup();
        group.setId(2L);
        group.setName("TOK:BUILDING");
        addressGroup.setCorresponGroup(group);
        addressGroup.setAddressType(AddressType.CC);
        users = new ArrayList<AddressUser>();
        addressUser = new AddressUser();
        addressUser.setAddressUserType(AddressUserType.NORMAL_USER);
        u = new User();
        u.setEmpNo("00044");
        u.setNameE("Normal User");
        addressUser.setUser(u);
        users.add(addressUser);
        addressGroup.setUsers(users);
        addressGroups.add(addressGroup);
        c.setAddressCorresponGroups(addressGroups);
        CorresponType ct = new CorresponType();
        ct.setId(new Long(1L));
        ct.setName("Request");
        c.setCorresponType(ct);
        c.setWorkflowStatus(WorkflowStatus.DRAFT);
        c.setCreatedAt(new GregorianCalendar(2009, 3, 1, 1, 1, 1).getTime());
        c.setUpdatedAt(new GregorianCalendar(2009, 3, 10, 10, 10, 10).getTime());
        u = new User();
        u.setEmpNo("00001");
        u.setNameE("Test User");
        c.setCreatedBy(u);
        c.setUpdatedBy(u);
        c.setReplyRequired(ReplyRequired.YES);
        c.setDeadlineForReply(new GregorianCalendar(2009, 4, 1).getTime());
        c.setCorresponStatus(CorresponStatus.OPEN);
        c.setCustomField1Label("FIELD1");
        c.setCustomField1Value("VALUE1");
        c.setCustomField2Label("FIELD2");
        c.setCustomField2Value("VALUE2");
        c.setCustomField3Label("FIELD3");
        c.setCustomField3Value("VALUE3");
        c.setCustomField4Label("FIELD4");
        c.setCustomField4Value("VALUE4");
        c.setCustomField5Label("FIELD5");
        c.setCustomField5Value("VALUE5");
        c.setCustomField6Label("FIELD6");
        c.setCustomField6Value("VALUE6");
        c.setCustomField7Label("FIELD7");
        c.setCustomField7Value("VALUE7");
        c.setCustomField8Label("FIELD8");
        c.setCustomField8Value("VALUE8");
        c.setCustomField9Label("FIELD9");
        c.setCustomField9Value("VALUE9");
        c.setCustomField10Label("FIELD10");
        c.setCustomField10Value("VALUE10");

        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat f2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String html = new String(service.generateHTML(c, null));

        // 印刷用のscriptが表示されていること
        assertTrue(html.contains("<body onload=\"window.print();\">"));
        assertTrue(html.contains(">Print</a>"));
        assertTrue(html.contains(">Close</a>"));
        assertFalse(html.contains(">ZIP</a>"));

        // 各値の確認
        assertTrue(c.getId().toString(), html.contains(getExpectedHTMLString(c.getId().toString())));
        assertTrue(c.getCorresponStatus().getLabel(), html.contains(getExpectedHTMLString(c
            .getCorresponStatus().getLabel())));

        // 通常の検証との相違点
        // コレポン文書番号未設定を表す文言が設定されている
        assertTrue(c.getCorresponNo(), html
            .contains(getExpectedHTMLString(CorresponPageFormatter.DEFAULT_CORRESPON_NO)));

        assertTrue(c.getPreviousRevCorresponNo(), html.contains(getExpectedHTMLString(c
            .getPreviousRevCorresponNo())));
        assertTrue(c.getParentCorresponNo(), html.contains(getExpectedHTMLString(c
            .getParentCorresponNo())));

        assertTrue(c.getWorkflowStatus().getLabel(), html.contains(getExpectedHTMLString(c
            .getWorkflowStatus().getLabel())));
        assertTrue(c.getFromCorresponGroup().getName(), html.contains(getExpectedHTMLString(c
            .getFromCorresponGroup().getName())));
        if (c.getAddressCorresponGroups() != null) {
            for (AddressCorresponGroup addressCorresponGroup : c.getAddressCorresponGroups()) {
                assertTrue(addressCorresponGroup.getCorresponGroup().getName(), html
                    .contains(addressCorresponGroup.getCorresponGroup().getName()));

                for (AddressUser aUser : addressCorresponGroup.getUsers()) {
                    if (addressCorresponGroup.isTo() && aUser.isAttention()) {
                        assertTrue(aUser.getUser().getLabel(), html.contains(aUser.getUser()
                            .getLabel()));
                        assertTrue(aUser.getPersonInCharges().get(0).getUser().getLabel(), html
                            .contains(aUser.getPersonInCharges().get(0).getUser().getLabel()));
                    } else if (addressCorresponGroup.isTo() && !aUser.isAttention()) {
                        assertFalse(aUser.getUser().getLabel(), html.contains(aUser.getUser()
                            .getLabel()));
                    } else if (addressCorresponGroup.isCc() && aUser.isCc()) {
                        assertTrue(aUser.getUser().getLabel(), html.contains(aUser.getUser()
                            .getLabel()));
                    }
                }
            }
        }
        assertTrue(c.getCorresponType().getLabel(), html.contains(getExpectedHTMLString(c
            .getCorresponType().getLabel())));
        assertTrue(c.getSubject(), html.contains(getExpectedHTMLString(c.getSubject())));
        assertTrue(c.getBody(), html.contains(getExpectedString(c.getBody())));
        assertTrue(c.getDeadlineForReply().toString(), html.contains(f.format(c
            .getDeadlineForReply())));
        if (c.getFile1FileName() != null) {
            assertTrue(c.getFile1FileName(), html.contains(c.getFile1FileName()));
        }
        if (c.getFile2FileName() != null) {
            assertTrue(c.getFile2FileName(), html.contains(c.getFile2FileName()));
        }
        if (c.getFile3FileName() != null) {
            assertTrue(c.getFile3FileName(), html.contains(c.getFile3FileName()));
        }
        if (c.getFile4FileName() != null) {
            assertTrue(c.getFile4FileName(), html.contains(c.getFile4FileName()));
        }
        if (c.getFile5FileName() != null) {
            assertTrue(c.getFile5FileName(), html.contains(c.getFile5FileName()));
        }
        if (c.getCustomField1Label() != null) {
            assertTrue(c.getCustomField1Label(), html.contains(getExpectedHTMLString(c
                .getCustomField1Label())));
            assertTrue(c.getCustomField1Value(), html.contains(c.getCustomField1Value()));
        }
        if (c.getCustomField2Label() != null) {
            assertTrue(c.getCustomField2Label(), html.contains(getExpectedHTMLString(c
                .getCustomField2Label())));
            assertTrue(c.getCustomField2Value(), html.contains(c.getCustomField2Value()));
        }
        if (c.getCustomField3Label() != null) {
            assertTrue(c.getCustomField3Label(), html.contains(getExpectedHTMLString(c
                .getCustomField3Label())));
            assertTrue(c.getCustomField3Value(), html.contains(c.getCustomField3Value()));
        }
        if (c.getCustomField4Label() != null) {
            assertTrue(c.getCustomField4Label(), html.contains(getExpectedHTMLString(c
                .getCustomField4Label())));
            assertTrue(c.getCustomField4Value(), html.contains(c.getCustomField4Value()));
        }
        if (c.getCustomField5Label() != null) {
            assertTrue(c.getCustomField5Label(), html.contains(getExpectedHTMLString(c
                .getCustomField5Label())));
            assertTrue(c.getCustomField5Value(), html.contains(c.getCustomField5Value()));
        }
        if (c.getCustomField6Label() != null) {
            assertTrue(c.getCustomField6Label(), html.contains(getExpectedHTMLString(c
                .getCustomField6Label())));
            assertTrue(c.getCustomField6Value(), html.contains(c.getCustomField6Value()));
        }
        if (c.getCustomField7Label() != null) {
            assertTrue(c.getCustomField7Label(), html.contains(getExpectedHTMLString(c
                .getCustomField7Label())));
            assertTrue(c.getCustomField7Value(), html.contains(c.getCustomField7Value()));
        }
        if (c.getCustomField8Label() != null) {
            assertTrue(c.getCustomField8Label(), html.contains(getExpectedHTMLString(c
                .getCustomField8Label())));
            assertTrue(c.getCustomField8Value(), html.contains(c.getCustomField8Value()));
        }
        if (c.getCustomField9Label() != null) {
            assertTrue(c.getCustomField9Label(), html.contains(getExpectedHTMLString(c
                .getCustomField9Label())));
            assertTrue(c.getCustomField9Value(), html.contains(c.getCustomField9Value()));
        }
        if (c.getCustomField10Label() != null) {
            assertTrue(c.getCustomField10Label(), html.contains(getExpectedHTMLString(c
                .getCustomField10Label())));
            assertTrue(c.getCustomField10Value(), html.contains(c.getCustomField10Value()));
        }
        assertTrue(c.getUpdatedAt().toString(), html.contains(getExpectedHTMLString(f2.format(c
            .getUpdatedAt()))));
        assertTrue(c.getUpdatedBy().getNameE() + "/" + c.getUpdatedBy().getEmpNo(), html.contains(c
            .getUpdatedBy().getNameE()
            + "/" + c.getUpdatedBy().getEmpNo()));
        if (c.getWorkflows() != null) {
            for (Workflow flow : c.getWorkflows()) {
                assertTrue(flow.getWorkflowNo().toString(), html
                    .contains(getExpectedHTMLString(flow.getWorkflowNo().toString())));
                assertTrue(flow.getWorkflowType().getLabel(), html
                    .contains(getExpectedHTMLString(flow.getWorkflowType().getLabel())));
                assertTrue(flow.getUser().getNameE() + "/" + flow.getUser().getEmpNo(), html
                    .contains(getExpectedHTMLString(flow.getUser().getNameE() + "/"
                        + flow.getUser().getEmpNo())));
                assertTrue(flow.getWorkflowProcessStatus().getLabel(), html
                    .contains(getExpectedHTMLString(flow.getWorkflowProcessStatus().getLabel())));
                if (flow.getUpdatedBy() != null) {
                    assertTrue(flow.getUpdatedBy().getNameE() + "/"
                        + flow.getUpdatedBy().getEmpNo(), html.contains(getExpectedHTMLString(flow
                        .getUpdatedBy().getNameE()
                        + "/" + flow.getUpdatedBy().getEmpNo())));
                    assertTrue(flow.getUpdatedAt().toString(), html.contains(f2.format(flow
                        .getUpdatedAt())));
                }
                assertTrue(flow.getCommentOn(), html.contains(getExpectedHTMLString(flow
                    .getCommentOn())));
            }
        }
    }

    /**
     * 指定されたコレポン文書をHTML形式に変換して返すテストケース. コレポン文書発行済み
     */
    @Test
    public void testGenerateHTMLCorresponIssued() throws Exception {
        Correspon c = new Correspon();
        c.setId(1L);
        c.setCorresponNo("IT:000001");
        c.setParentCorresponId(2L);
        c.setParentCorresponNo("YOC:OT:BUILDING-00000");
        c.setPreviousRevCorresponId(3L);
        c.setPreviousRevCorresponNo("YOC:OT:IT-99999");
        CorresponGroup from = new CorresponGroup();
        from.setId(new Long(1L));
        from.setName("YOC:BUILDING");
        c.setFromCorresponGroup(from);
        c.setSubject("Mock");
        c.setBody("This is test.");
        List<AddressCorresponGroup> addressGroups = new ArrayList<AddressCorresponGroup>();
        AddressCorresponGroup addressGroup = new AddressCorresponGroup();
        CorresponGroup group = new CorresponGroup();
        group.setId(2L);
        group.setName("YOC:IT");
        addressGroup.setCorresponGroup(group);
        addressGroup.setAddressType(AddressType.TO);
        List<AddressUser> users = new ArrayList<AddressUser>();
        AddressUser addressUser = new AddressUser();
        addressUser.setAddressUserType(AddressUserType.ATTENTION);
        User u = new User();
        u.setEmpNo("00011");
        u.setNameE("Attention User");
        addressUser.setUser(u);
        List<PersonInCharge> personInCharges = new ArrayList<PersonInCharge>();
        PersonInCharge pic = new PersonInCharge();
        u = new User();
        u.setEmpNo("00022");
        u.setNameE("PIC User");
        pic.setUser(u);
        personInCharges.add(pic);
        addressUser.setPersonInCharges(personInCharges);
        users.add(addressUser);
        addressUser = new AddressUser();
        addressUser.setAddressUserType(AddressUserType.NORMAL_USER);
        u = new User();
        u.setEmpNo("00033");
        u.setNameE("Normal User");
        addressUser.setUser(u);
        users.add(addressUser);
        addressGroup.setUsers(users);
        addressGroups.add(addressGroup);
        addressGroup = new AddressCorresponGroup();
        group = new CorresponGroup();
        group.setId(2L);
        group.setName("TOK:BUILDING");
        addressGroup.setCorresponGroup(group);
        addressGroup.setAddressType(AddressType.CC);
        users = new ArrayList<AddressUser>();
        addressUser = new AddressUser();
        addressUser.setAddressUserType(AddressUserType.NORMAL_USER);
        u = new User();
        u.setEmpNo("00044");
        u.setNameE("Normal User");
        addressUser.setUser(u);
        users.add(addressUser);
        addressGroup.setUsers(users);
        addressGroups.add(addressGroup);
        c.setAddressCorresponGroups(addressGroups);
        CorresponType ct = new CorresponType();
        ct.setId(new Long(1L));
        ct.setName("Request");
        c.setCorresponType(ct);
        c.setWorkflowStatus(WorkflowStatus.ISSUED);
        c.setCreatedAt(new GregorianCalendar(2009, 3, 1, 1, 1, 1).getTime());
        c.setUpdatedAt(new GregorianCalendar(2009, 3, 10, 10, 10, 10).getTime());
        c.setIssuedAt(new GregorianCalendar(2009, 3, 11, 10, 10, 10).getTime());
        u = new User();
        u.setEmpNo("00001");
        u.setNameE("Test User");
        c.setCreatedBy(u);
        c.setUpdatedBy(u);
        c.setIssuedBy(u);
        c.setReplyRequired(ReplyRequired.YES);
        c.setDeadlineForReply(new GregorianCalendar(2009, 4, 1).getTime());
        c.setCorresponStatus(CorresponStatus.OPEN);
        c.setCustomField1Label("FIELD1");
        c.setCustomField1Value("VALUE1");
        c.setCustomField2Label("FIELD2");
        c.setCustomField2Value("VALUE2");
        c.setCustomField3Label("FIELD3");
        c.setCustomField3Value("VALUE3");
        c.setCustomField4Label("FIELD4");
        c.setCustomField4Value("VALUE4");
        c.setCustomField5Label("FIELD5");
        c.setCustomField5Value("VALUE5");
        c.setCustomField6Label("FIELD6");
        c.setCustomField6Value("VALUE6");
        c.setCustomField7Label("FIELD7");
        c.setCustomField7Value("VALUE7");
        c.setCustomField8Label("FIELD8");
        c.setCustomField8Value("VALUE8");
        c.setCustomField9Label("FIELD9");
        c.setCustomField9Value("VALUE9");
        c.setCustomField10Label("FIELD10");
        c.setCustomField10Value("VALUE10");

        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat f2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String html = new String(service.generateHTML(c, null));

        // 印刷用のscriptが表示されていること
        assertTrue(html.contains("<body onload=\"window.print();\">"));
        assertTrue(html.contains(">Print</a>"));
        assertTrue(html.contains(">Close</a>"));

        // 各値の確認
        assertTrue(c.getId().toString(), html.contains(getExpectedHTMLString(c.getId().toString())));
        assertTrue(c.getCorresponStatus().getLabel(), html.contains(getExpectedHTMLString(c
            .getCorresponStatus().getLabel())));

        // 通常の検証との相違点
        // コレポン文書番号未設定を表す文言が設定されている
        assertTrue(c.getCorresponNo(), html
            .contains(getExpectedHTMLString(c.getCorresponNo())));

        assertTrue(c.getPreviousRevCorresponNo(), html.contains(getExpectedHTMLString(c
            .getPreviousRevCorresponNo())));
        assertTrue(c.getParentCorresponNo(), html.contains(getExpectedHTMLString(c
            .getParentCorresponNo())));

        assertTrue(c.getWorkflowStatus().getLabel(), html.contains(getExpectedHTMLString(c
            .getWorkflowStatus().getLabel())));
        assertTrue(c.getFromCorresponGroup().getName(), html.contains(getExpectedHTMLString(c
            .getFromCorresponGroup().getName())));
        if (c.getAddressCorresponGroups() != null) {
            for (AddressCorresponGroup addressCorresponGroup : c.getAddressCorresponGroups()) {
                assertTrue(addressCorresponGroup.getCorresponGroup().getName(), html
                    .contains(addressCorresponGroup.getCorresponGroup().getName()));

                for (AddressUser aUser : addressCorresponGroup.getUsers()) {
                    if (addressCorresponGroup.isTo() && aUser.isAttention()) {
                        assertTrue(aUser.getUser().getLabel(), html.contains(aUser.getUser()
                            .getLabel()));
                        assertTrue(aUser.getPersonInCharges().get(0).getUser().getLabel(), html
                            .contains(aUser.getPersonInCharges().get(0).getUser().getLabel()));
                    } else if (addressCorresponGroup.isTo() && !aUser.isAttention()) {
                        assertFalse(aUser.getUser().getLabel(), html.contains(aUser.getUser()
                            .getLabel()));
                    } else if (addressCorresponGroup.isCc() && aUser.isCc()) {
                        assertTrue(aUser.getUser().getLabel(), html.contains(aUser.getUser()
                            .getLabel()));
                    }
                }
            }
        }
        assertTrue(c.getCorresponType().getLabel(), html.contains(getExpectedHTMLString(c
            .getCorresponType().getLabel())));
        assertTrue(c.getSubject(), html.contains(getExpectedHTMLString(c.getSubject())));
        assertTrue(c.getBody(), html.contains(getExpectedString(c.getBody())));
        assertTrue(c.getDeadlineForReply().toString(), html.contains(f.format(c
            .getDeadlineForReply())));
        if (c.getFile1FileName() != null) {
            assertTrue(c.getFile1FileName(), html.contains(c.getFile1FileName()));
        }
        if (c.getFile2FileName() != null) {
            assertTrue(c.getFile2FileName(), html.contains(c.getFile2FileName()));
        }
        if (c.getFile3FileName() != null) {
            assertTrue(c.getFile3FileName(), html.contains(c.getFile3FileName()));
        }
        if (c.getFile4FileName() != null) {
            assertTrue(c.getFile4FileName(), html.contains(c.getFile4FileName()));
        }
        if (c.getFile5FileName() != null) {
            assertTrue(c.getFile5FileName(), html.contains(c.getFile5FileName()));
        }
        if (c.getCustomField1Label() != null) {
            assertTrue(c.getCustomField1Label(), html.contains(getExpectedHTMLString(c
                .getCustomField1Label())));
            assertTrue(c.getCustomField1Value(), html.contains(c.getCustomField1Value()));
        }
        if (c.getCustomField2Label() != null) {
            assertTrue(c.getCustomField2Label(), html.contains(getExpectedHTMLString(c
                .getCustomField2Label())));
            assertTrue(c.getCustomField2Value(), html.contains(c.getCustomField2Value()));
        }
        if (c.getCustomField3Label() != null) {
            assertTrue(c.getCustomField3Label(), html.contains(getExpectedHTMLString(c
                .getCustomField3Label())));
            assertTrue(c.getCustomField3Value(), html.contains(c.getCustomField3Value()));
        }
        if (c.getCustomField4Label() != null) {
            assertTrue(c.getCustomField4Label(), html.contains(getExpectedHTMLString(c
                .getCustomField4Label())));
            assertTrue(c.getCustomField4Value(), html.contains(c.getCustomField4Value()));
        }
        if (c.getCustomField5Label() != null) {
            assertTrue(c.getCustomField5Label(), html.contains(getExpectedHTMLString(c
                .getCustomField5Label())));
            assertTrue(c.getCustomField5Value(), html.contains(c.getCustomField5Value()));
        }
        if (c.getCustomField6Label() != null) {
            assertTrue(c.getCustomField6Label(), html.contains(getExpectedHTMLString(c
                .getCustomField6Label())));
            assertTrue(c.getCustomField6Value(), html.contains(c.getCustomField6Value()));
        }
        if (c.getCustomField7Label() != null) {
            assertTrue(c.getCustomField7Label(), html.contains(getExpectedHTMLString(c
                .getCustomField7Label())));
            assertTrue(c.getCustomField7Value(), html.contains(c.getCustomField7Value()));
        }
        if (c.getCustomField8Label() != null) {
            assertTrue(c.getCustomField8Label(), html.contains(getExpectedHTMLString(c
                .getCustomField8Label())));
            assertTrue(c.getCustomField8Value(), html.contains(c.getCustomField8Value()));
        }
        if (c.getCustomField9Label() != null) {
            assertTrue(c.getCustomField9Label(), html.contains(getExpectedHTMLString(c
                .getCustomField9Label())));
            assertTrue(c.getCustomField9Value(), html.contains(c.getCustomField9Value()));
        }
        if (c.getCustomField10Label() != null) {
            assertTrue(c.getCustomField10Label(), html.contains(getExpectedHTMLString(c
                .getCustomField10Label())));
            assertTrue(c.getCustomField10Value(), html.contains(c.getCustomField10Value()));
        }
        assertTrue(c.getIssuedAt().toString(), html.contains(getExpectedHTMLString(f2.format(c
            .getIssuedAt()))));
        assertTrue(c.getUpdatedAt().toString(), html.contains(getExpectedHTMLString(f2.format(c
            .getUpdatedAt()))));
        assertTrue(c.getIssuedBy().getNameE() + "/" + c.getIssuedBy().getEmpNo(), html.contains(c
            .getIssuedBy().getNameE()
            + "/" + c.getIssuedBy().getEmpNo()));
        assertTrue(c.getUpdatedBy().getNameE() + "/" + c.getUpdatedBy().getEmpNo(), html.contains(c
            .getUpdatedBy().getNameE()
            + "/" + c.getUpdatedBy().getEmpNo()));
        if (c.getWorkflows() != null) {
            for (Workflow flow : c.getWorkflows()) {
                assertTrue(flow.getWorkflowNo().toString(), html
                    .contains(getExpectedHTMLString(flow.getWorkflowNo().toString())));
                assertTrue(flow.getWorkflowType().getLabel(), html
                    .contains(getExpectedHTMLString(flow.getWorkflowType().getLabel())));
                assertTrue(flow.getUser().getNameE() + "/" + flow.getUser().getEmpNo(), html
                    .contains(getExpectedHTMLString(flow.getUser().getNameE() + "/"
                        + flow.getUser().getEmpNo())));
                assertTrue(flow.getWorkflowProcessStatus().getLabel(), html
                    .contains(getExpectedHTMLString(flow.getWorkflowProcessStatus().getLabel())));
                if (flow.getUpdatedBy() != null) {
                    assertTrue(flow.getUpdatedBy().getNameE() + "/"
                        + flow.getUpdatedBy().getEmpNo(), html.contains(getExpectedHTMLString(flow
                        .getUpdatedBy().getNameE()
                        + "/" + flow.getUpdatedBy().getEmpNo())));
                    assertTrue(flow.getUpdatedAt().toString(), html.contains(f2.format(flow
                        .getUpdatedAt())));
                }
                assertTrue(flow.getCommentOn(), html.contains(getExpectedHTMLString(flow
                    .getCommentOn())));
            }
        }
    }

    private String getExpectedString(String value) {
        String param = "";
        if (value != null) {
            param = value;
        }
        return param;
    }

    private String getExpectedHTMLString(String value) {
        String param = "";
        if (value != null) {
            param = value;
        }
        // <td ...>value</td>
        return String.format(">%s</", param);
    }

    /**
     * 指定されたコレポン文書をHTML形式に変換して返すテストケース. 引数Null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGenerateHTMLNull() throws Exception {
        service.generateHTML(null, null);
        fail("例外が発生していない");
    }

    /**
     * ZIPダウンロードの検証.
     */
    @Test
    public void testGenerateZip() throws Exception {
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";

        Correspon c = new Correspon();
        c.setId(1L);
        c.setCorresponNo("YOC:OT:BUILDING-00007");
        c.setProjectId("PJ1");
        CorresponGroup from = new CorresponGroup();
        from.setId(new Long(6L));
        from.setName("YOC:BUILDING");
        c.setFromCorresponGroup(from);
        c.setSubject("Mock");
        List<AddressCorresponGroup> addressGroups = new ArrayList<AddressCorresponGroup>();
        AddressCorresponGroup addressGroup = new AddressCorresponGroup();
        CorresponGroup group = new CorresponGroup();
        group.setId(2L);
        group.setName("YOC:IT");
        addressGroup.setCorresponGroup(group);
        addressGroup.setAddressType(AddressType.TO);
        addressGroups.add(addressGroup);
        addressGroup = new AddressCorresponGroup();
        group = new CorresponGroup();
        group.setId(2L);
        group.setName("TOK:BUILDING");
        addressGroup.setCorresponGroup(group);
        addressGroup.setAddressType(AddressType.CC);
        addressGroups.add(addressGroup);
        c.setAddressCorresponGroups(addressGroups);
        CorresponType ct = new CorresponType();
        ct.setId(new Long(1L));
        ct.setCorresponType("Request");
        ct.setProjectCorresponTypeId(new Long(1L));
        c.setCorresponType(ct);
        c.setWorkflowStatus(WorkflowStatus.ISSUED);
        c.setIssuedAt(new GregorianCalendar(2009, 3, 10, 1, 1, 1).getTime());
        c.setCreatedAt(new GregorianCalendar(2009, 3, 1, 1, 1, 1).getTime());
        c.setUpdatedAt(new GregorianCalendar(2009, 3, 10, 10, 10, 10).getTime());
        User u = new User();
        u.setEmpNo("00001");
        u.setNameE("Test User");
        c.setIssuedBy(u);
        c.setCreatedBy(u);
        c.setUpdatedBy(u);
        c.setCorresponStatus(CorresponStatus.OPEN);
        c.setCustomField1Label("FIELD1");
        c.setCustomField1Value("VALUE1");
        c.setCustomField2Label("FIELD2");
        c.setCustomField2Value("VALUE2");
        c.setCustomField3Label("FIELD3");
        c.setCustomField3Value("VALUE3");
        c.setCustomField4Label("FIELD4");
        c.setCustomField4Value("VALUE4");
        c.setCustomField5Label("FIELD5");
        c.setCustomField5Value("VALUE5");
        c.setCustomField6Label("FIELD6");
        c.setCustomField6Value("VALUE6");
        c.setCustomField7Label("FIELD7");
        c.setCustomField7Value("VALUE7");
        c.setCustomField8Label("FIELD8");
        c.setCustomField8Value("VALUE8");
        c.setCustomField9Label("FIELD9");
        c.setCustomField9Value("VALUE9");
        c.setCustomField10Label("FIELD10");
        c.setCustomField10Value("VALUE10");
        c.setFile1Id(100L);
        c.setFile2Id(200L);
        c.setFile3Id(300L);
        c.setFile4Id(400L);
        c.setFile5Id(500L);
        c.setFile1FileId("100");
        c.setFile2FileId("200");
        c.setFile3FileId("300");
        c.setFile4FileId("400");
        c.setFile5FileId("500");
        c.setFile1FileName("filename100.txt");
        c.setFile2FileName("filename200.txt");
        c.setFile3FileName("filename300.txt");
        c.setFile4FileName("ファイル名400.txt");
        c.setFile5FileName("ﾌｧｲﾙ名500.txt");

        c.setWorkflows(new ArrayList<Workflow>());
        c.setPreviousRevCorresponId(null);
        group = new CorresponGroup();
        group.setName("YOC:IT");
        c.setToCorresponGroup(group);
        c.setToCorresponGroupCount(2L);
        c.setReplyRequired(null);
        c.setDeadlineForReply(null);

        List<Workflow> workflows = new ArrayList<Workflow>();
        Workflow workflow = new Workflow();
        User user = new User();

        workflow.setId(1L);
        workflow.setWorkflowProcessStatus(WorkflowProcessStatus.CHECKED);
        workflow.setWorkflowNo(1L);
        workflow.setWorkflowType(WorkflowType.CHECKER);
        user.setNameE("User01");
        user.setEmpNo("ZZA02");
        workflow.setUser(user);
        workflows.add(workflow);
        c.setWorkflows(workflows) ;

        List<Project> projects = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        projects.add(p);
        MockAbstractService.ACCESSIBLE_PROJECTS = projects;

        ProcessContext pc = ProcessContext.getCurrentContext();
        Map<String, Object> values = new HashMap<String, Object>();
        values.put(Constants.KEY_PROJECT, p);
        pc.setValue(SystemConfig.KEY_ACTION_VALUES, values);

        Attachment a = new Attachment();
        a.setId(100L);
        a.setCorresponId(1L);
        a.setFileId("100");
        a.setFileName("filename100.txt");
        MockAbstractDao.RET_FIND_BY_ID_ATTACHMENT.put("100", a);
        a = new Attachment();
        a.setId(200L);
        a.setCorresponId(1L);
        a.setFileId("200");
        a.setFileName("filename200.txt");
        MockAbstractDao.RET_FIND_BY_ID_ATTACHMENT.put("200", a);
        a = new Attachment();
        a.setId(300L);
        a.setCorresponId(1L);
        a.setFileId("300");
        a.setFileName("filename300.txt");
        MockAbstractDao.RET_FIND_BY_ID_ATTACHMENT.put("300", a);
        a = new Attachment();
        a.setId(400L);
        a.setCorresponId(1L);
        a.setFileId("400");
        a.setFileName("ファイル名400.txt");
        MockAbstractDao.RET_FIND_BY_ID_ATTACHMENT.put("400", a);
        a = new Attachment();
        a.setId(500L);
        a.setCorresponId(1L);
        a.setFileId("500");
        a.setFileName("ﾌｧｲﾙ名500.txt");
        MockAbstractDao.RET_FIND_BY_ID_ATTACHMENT.put("500", a);

        MockCorresponDao.RET_FIND_CORRESPONS_RESPONSE_HISTORY = createResponseHistoryList(4L, 2);
        MockWorkflowDao.RET_FIND_BY_CORRESPON_ID = workflows;
        MockCorresponDao.init = true;
        MockCorresponDao.count = 0;
        MockCorresponDao.RET_FIND_BY_ID.put("1", c);
        MockCorresponTypeDao.RET_FIND_BY_PROJECT_CORRESPON_TYPE_ID = ct;
        MockAttachmentDao.RET_FIND_BY_CORRESPON_ID = c.getAttachments();
        MockProjectUserDao.FIND_BY_EMP_NO = user ;

        // 一度ﾌｧｲﾙに書き込んでから読みだす
        byte[] actual = service.generateZip(c);
        File temp = new File("TestTemp.zip") ;
        FileOutputStream fos = new FileOutputStream(temp);
        fos.write(actual);
        ZipFile zf = new ZipFile(temp, "Windows-31J");

        Set<String> names = new HashSet<String>();
        names.add("PJ1_YOC-OT-BUILDING-00007(0000000001).html");
        names.add("Attachments-PJ1_YOC-OT-BUILDING-00007(0000000001)\\filename100.txt");
        names.add("Attachments-PJ1_YOC-OT-BUILDING-00007(0000000001)\\filename200.txt");
        names.add("Attachments-PJ1_YOC-OT-BUILDING-00007(0000000001)\\filename300.txt");
        names.add("Attachments-PJ1_YOC-OT-BUILDING-00007(0000000001)\\ファイル名400.txt");
        names.add("Attachments-PJ1_YOC-OT-BUILDING-00007(0000000001)\\ﾌｧｲﾙ名500.txt");

        int index = 0;
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat f2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        InputStream is = null;
        try {
            for (Enumeration e = zf.getEntries(); e.hasMoreElements(); index++) {
                ZipEntry ze = (ZipEntry)e.nextElement();
                assertTrue(names.contains(ze.getName()));
                if (ze.isDirectory()) {
                    continue;
                }

                if (ze.getName().endsWith(".html")) {
                    StringBuilder sb = new StringBuilder();
                    byte[] b = new byte[1024];
                    int i;
                    is = zf.getInputStream(ze);
                    while ((i = is.read(b, 0, b.length)) != -1) {
                        sb.append(new String(b, 0, i));
                    }
                    String html = sb.toString();

                    // 印刷用のscriptが表示されていないこと
                    assertTrue(html.contains("<body>"));
                    assertFalse(html.contains(">Print</a>"));
                    assertFalse(html.contains(">Close</a>"));

                    // 各値の確認
                    assertTrue(c.getId().toString(),
                        html.contains(getExpectedString(c.getId().toString())));
                    assertTrue(c.getCorresponStatus().getLabel(),
                        html.contains(getExpectedHTMLString(c.getCorresponStatus().getLabel())));

                    //  コレポン文書番号未設定の場合は
                    //  それとわかるラベルが出力されている
                    if (c.getCorresponNo() == null) {
                        assertTrue(c.getCorresponNo(),
                            html.contains(getExpectedHTMLString(CorresponPageFormatter.DEFAULT_CORRESPON_NO)));
                    } else {
                        assertTrue(c.getCorresponNo(),
                            html.contains(getExpectedHTMLString(c.getCorresponNo())));
                    }

                    assertTrue(c.getWorkflowStatus().getLabel(),
                        html.contains(getExpectedHTMLString(c.getWorkflowStatus().getLabel())));
                    if (c.getParentCorresponId() != null) {
                        assertTrue(c.getParentCorresponNo(),
                            html.contains(getExpectedHTMLString(c.getParentCorresponNo())));
                    }
                    if (c.getPreviousRevCorresponId() != null) {
                        assertTrue(c.getPreviousRevCorresponNo(),
                            html.contains(getExpectedHTMLString(c.getPreviousRevCorresponNo())));
                    }
                    assertTrue(c.getFromCorresponGroup().getName() + " " + index,
                        html.contains(getExpectedString(c.getFromCorresponGroup().getName())));
                    if (c.getAddressCorresponGroups() != null) {
                        for (AddressCorresponGroup addressCorresponGroup: c.getAddressCorresponGroups()) {
                            assertTrue(addressCorresponGroup.getCorresponGroup().getName(),
                                html.contains(addressCorresponGroup.getCorresponGroup().getName()));
                            if (addressCorresponGroup.getUsers() == null) {
                                continue;
                            }
                            for (AddressUser aUser : addressCorresponGroup.getUsers()) {
                                if (addressCorresponGroup.isTo() && aUser.isAttention()) {
                                    assertTrue(aUser.getUser().getLabel(),
                                               html.contains(aUser.getUser().getLabel()));
                                    if (aUser.getPersonInCharges() != null) {
                                        assertTrue(aUser.getPersonInCharges().get(0).getUser().getLabel(),
                                                   html.contains(
                                                       aUser.getPersonInCharges().get(0).getUser().getLabel()));
                                    }
                                } else if (addressCorresponGroup.isTo() && !aUser.isAttention()) {
                                    assertFalse(aUser.getUser().getLabel(),
                                                html.contains(aUser.getUser().getLabel()));
                                } else if (addressCorresponGroup.isCc() && aUser.isCc()) {
                                    assertTrue(aUser.getUser().getLabel(),
                                               html.contains(aUser.getUser().getLabel()));
                                }
                            }
                        }
                    }
                    assertTrue(c.getCorresponType().getName(),
                        html.contains(getExpectedHTMLString(c.getCorresponType().getName())));
                    assertTrue(c.getSubject(),
                        html.contains(getExpectedHTMLString(c.getSubject())));
                    assertTrue(c.getBody(),
                        html.contains(getExpectedHTMLString(c.getBody())));
                    if (ReplyRequired.YES.equals(c.getReplyRequired())) {
                        assertTrue(c.getDeadlineForReply().toString(),
                            html.contains(f.format(c.getDeadlineForReply())));
                    }
                    if (c.getFile1FileName() != null) {
                        assertTrue(c.getFile1FileName(),
                            html.contains(c.getFile1FileName()));
                    }
                    if (c.getFile2FileName() != null) {
                        assertTrue(c.getFile2FileName(),
                            html.contains(c.getFile2FileName()));
                    }
                    if (c.getFile3FileName() != null) {
                        assertTrue(c.getFile3FileName(),
                            html.contains(c.getFile3FileName()));
                    }
                    if (c.getFile4FileName() != null) {
                        assertTrue(c.getFile4FileName(),
                            html.contains(c.getFile4FileName()));
                    }
                    if (c.getFile5FileName() != null) {
                        assertTrue(c.getFile5FileName(),
                            html.contains(c.getFile5FileName()));
                    }
                    if (c.getCustomField1Label() != null) {
                        assertTrue(c.getCustomField1Label(),
                            html.contains(getExpectedHTMLString(c.getCustomField1Label())));
                        assertTrue(c.getCustomField1Value(),
                            html.contains(c.getCustomField1Value()));
                    }
                    if (c.getCustomField2Label() != null) {
                        assertTrue(c.getCustomField2Label(),
                            html.contains(getExpectedHTMLString(c.getCustomField2Label())));
                        assertTrue(c.getCustomField2Value(),
                            html.contains(c.getCustomField2Value()));
                    }
                    if (c.getCustomField3Label() != null) {
                        assertTrue(c.getCustomField3Label(),
                            html.contains(getExpectedHTMLString(c.getCustomField3Label())));
                        assertTrue(c.getCustomField3Value(),
                            html.contains(c.getCustomField3Value()));
                    }
                    if (c.getCustomField4Label() != null) {
                        assertTrue(c.getCustomField4Label(),
                            html.contains(getExpectedHTMLString(c.getCustomField4Label())));
                        assertTrue(c.getCustomField4Value(),
                            html.contains(c.getCustomField4Value()));
                    }
                    if (c.getCustomField5Label() != null) {
                        assertTrue(c.getCustomField5Label(),
                            html.contains(getExpectedHTMLString(c.getCustomField5Label())));
                        assertTrue(c.getCustomField5Value(),
                            html.contains(c.getCustomField5Value()));
                    }
                    if (c.getCustomField6Label() != null) {
                        assertTrue(c.getCustomField6Label(),
                            html.contains(getExpectedHTMLString(c.getCustomField6Label())));
                        assertTrue(c.getCustomField6Value(),
                            html.contains(c.getCustomField6Value()));
                    }
                    if (c.getCustomField7Label() != null) {
                        //  何故か出力されたHTMLにはスペースが入っているので検証に失敗する...
                        //  実際に画面に表示したりダウンロードしたファイルを確認したら問題無かったので
                        //  コメントアウトする
                          assertTrue("" + index + "*"+c.getCustomField7Label()+"*",
                              html.contains(getExpectedHTMLString(c.getCustomField7Label())));
                          assertTrue(c.getCustomField7Value(),
                              html.contains(c.getCustomField7Value()));
                    }
                    if (c.getCustomField8Label() != null) {
                        assertTrue(c.getCustomField8Label(),
                            html.contains(getExpectedHTMLString(c.getCustomField8Label())));
                        assertTrue(c.getCustomField8Value(),
                            html.contains(c.getCustomField8Value()));
                    }
                    if (c.getCustomField9Label() != null) {
                        assertTrue(c.getCustomField9Label(),
                            html.contains(getExpectedHTMLString(c.getCustomField9Label())));
                        assertTrue(c.getCustomField9Value(),
                            html.contains(c.getCustomField9Value()));
                    }
                    if (c.getCustomField10Label() != null) {
                        assertTrue(c.getCustomField10Label(),
                            html.contains(getExpectedHTMLString(c.getCustomField10Label())));
                        assertTrue(c.getCustomField10Value(),
                            html.contains(c.getCustomField10Value()));
                    }
                    if (c.getIssuedAt() != null) {
                        assertTrue(c.getIssuedAt().toString(),
                        html.contains(getExpectedHTMLString(f2.format(c.getIssuedAt()))));
                    }
                    assertTrue(c.getUpdatedAt().toString(),
                        html.contains(getExpectedHTMLString(f2.format(c.getUpdatedAt()))));
                    if ((c.getIssuedBy() != null) && (c.getIssuedBy().getNameE() != null) && (c.getIssuedBy().getEmpNo() != null)) {
                        assertTrue(c.getIssuedBy().getNameE() + "/" + c.getIssuedBy().getEmpNo(),
                            html.contains(
                                c.getIssuedBy().getNameE() + "/" + c.getIssuedBy().getEmpNo()));
                    }
                    assertTrue(c.getUpdatedBy().getNameE() + "/" + c.getUpdatedBy().getEmpNo(),
                        html.contains(
                            c.getUpdatedBy().getNameE() + "/" + c.getUpdatedBy().getEmpNo()));
                    if (c.getWorkflows() != null) {
                        for (Workflow flow: c.getWorkflows()) {
                            assertTrue(flow.getWorkflowNo().toString(),
                                html.contains(getExpectedHTMLString(flow.getWorkflowNo().toString())));
                            assertTrue(flow.getWorkflowType().getLabel(),
                                html.contains(getExpectedHTMLString(flow.getWorkflowType().getLabel())));
                            assertTrue(flow.getUser().getNameE() + "/" + flow.getUser().getEmpNo(),
                                html.contains(getExpectedString(
                                    flow.getUser().getNameE() + "/" + flow.getUser().getEmpNo())));
                            assertTrue(flow.getWorkflowProcessStatus().getLabel(),
                                html.contains(getExpectedString(flow.getWorkflowProcessStatus().getLabel())));
                            if (flow.getUpdatedBy() != null) {
                                assertTrue(flow.getUpdatedBy().getNameE() + "/" + flow.getUpdatedBy().getEmpNo(),
                                    html.contains(getExpectedString(
                                        flow.getUpdatedBy().getNameE() + "/" + flow.getUpdatedBy().getEmpNo())));
                                assertTrue(flow.getUpdatedAt().toString(),
                                    html.contains(f2.format(flow.getUpdatedAt())));
                            }
                            assertTrue(flow.getCommentOn(),
                                html.contains(getExpectedString(flow.getCommentOn())));
                        }
                    }
                }
            }
        } finally {
            if (is != null) {
              is.close();
            }
        }
    }

    /**
     * コレポン文書を削除する. （Preparer）
     */
    @Test
    public void testDelete01() throws Exception {

        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        MockAbstractService.IS_ANY_GROUP_ADMIN = false;

        Correspon expCorrespon = createCorrespon01();
        expCorrespon.setWorkflowStatus(WorkflowStatus.DRAFT);

        // テスト用コレポン文書
        MockCorresponDao.RET_DELETE_CORRESPON = expCorrespon;

        service.delete(expCorrespon);

    }

    /**
     * コレポン文書を削除する. （SystemAdmin）
     */
    @Test
    public void testDelete02() throws Exception {

        MockAbstractService.IS_SYSTEM_ADMIN = true;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        MockAbstractService.IS_ANY_GROUP_ADMIN = false;

        Correspon expCorrespon = createCorrespon02();
        expCorrespon.setWorkflowStatus(WorkflowStatus.DRAFT);

        // テスト用コレポン文書
        MockCorresponDao.RET_DELETE_CORRESPON = expCorrespon;

        service.delete(expCorrespon);

    }

    /**
     * コレポン文書を削除する(承認状態が不正). （SystemAdmin）
     */
    @Test
    public void testDelete03() throws Exception {

        MockAbstractService.IS_SYSTEM_ADMIN = true;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        MockAbstractService.IS_ANY_GROUP_ADMIN = false;

        Correspon expCorrespon = createCorrespon02();
        expCorrespon.setWorkflowStatus(WorkflowStatus.DENIED);

        // テスト用コレポン文書
        MockCorresponDao.RET_DELETE_CORRESPON = expCorrespon;

        try {
            service.delete(expCorrespon);
            fail("例外が発生していない");

        } catch (ServiceAbortException e) {

            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CORRESPON_STATUS_INVALID, e
                .getMessageCode());

        }

    }

    /**
     * コレポン文書を削除する(ユーザーが不正). （ProjectAdmin）
     */
    @Test
    public void testDelete04() throws Exception {

        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN = true;
        MockAbstractService.IS_ANY_GROUP_ADMIN = false;

        Correspon expCorrespon = createCorrespon02();
        expCorrespon.setWorkflowStatus(WorkflowStatus.DRAFT);

        // テスト用コレポン文書
        MockCorresponDao.RET_DELETE_CORRESPON = expCorrespon;

        try {
            service.delete(expCorrespon);
            fail("例外が発生していない");

        } catch (ServiceAbortException e) {

            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT, e
                .getMessageCode());

        }

    }

    /**
     * コレポン文書を発行する. （Preparer）
     */
    @Test
    public void testIssue01() throws Exception {

        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        MockAbstractService.IS_ANY_GROUP_ADMIN = false;

        List<Project> projects = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        projects.add(p);
        MockAbstractService.ACCESSIBLE_PROJECTS = projects;

        createCorresponNo();

        Correspon expCorrespon = createCorrespon01();
        expCorrespon.setProjectId("PJ1");
        expCorrespon.setWorkflowStatus(WorkflowStatus.DRAFT);

        CorresponType corresponType = new CorresponType();
        corresponType.setForceToUseWorkflow(ForceToUseWorkflow.OPTIONAL);
        expCorrespon.setCorresponType(corresponType);;

        // Exception初期化
        MockAbstractDao.EXCEPTION = false;
        MockAbstractDao.ExceptionWorkflow = false;
        MockAbstractDao.EDITMODE = MockAbstractDao.DEFAULT_MODE;
        MockAbstractDao.countElement = 0;

        service.issue(expCorrespon);

        // 確認
        assertEquals(MockAbstractDao.RET_UPDATE_CORRESPON.getId(), expCorrespon.getId());
        assertNotNull(MockAbstractDao.RET_UPDATE_CORRESPON.getCorresponNo());
        assertNotNull(MockAbstractDao.RET_UPDATE_CORRESPON.getIssuedAt());
        assertNotNull(MockAbstractDao.RET_UPDATE_CORRESPON.getWorkflowStatus());
        assertEquals(MockAbstractDao.RET_UPDATE_CORRESPON.getUpdatedBy().getEmpNo(),
            MockAbstractService.CURRENT_USER.getEmpNo());
        assertEquals(MockAbstractDao.RET_UPDATE_CORRESPON.getUpdatedBy().getNameE(),
            MockAbstractService.CURRENT_USER.getNameE());
        assertNull(MockAbstractDao.RET_UPDATE_CORRESPON.getProjectId());
        assertNull(MockAbstractDao.RET_UPDATE_CORRESPON.getFromCorresponGroup());
        assertNull(MockAbstractDao.RET_UPDATE_CORRESPON.getCorresponType());
        assertNull(MockAbstractDao.RET_UPDATE_CORRESPON.getSubject());
        assertNull(MockAbstractDao.RET_UPDATE_CORRESPON.getDeadlineForReply());
        assertNull(MockAbstractDao.RET_UPDATE_CORRESPON.getCorresponStatus());
        assertNull(MockAbstractDao.RET_UPDATE_CORRESPON.getCreatedBy());
        assertNull(MockAbstractDao.RET_UPDATE_CORRESPON.getCreatedAt());
        assertNull(MockAbstractDao.RET_UPDATE_CORRESPON.getBody());
    }

    /**
     * コレポン文書を発行する. （SystemAdmin）
     */
    @Test
    public void testIssue02() throws Exception {

        MockAbstractService.IS_SYSTEM_ADMIN = true;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        MockAbstractService.IS_ANY_GROUP_ADMIN = false;

        List<Project> projects = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        projects.add(p);
        MockAbstractService.ACCESSIBLE_PROJECTS = projects;

        createCorresponNo();

        Correspon expCorrespon = createCorrespon02();
        expCorrespon.setProjectId("PJ1");
        expCorrespon.setWorkflowStatus(WorkflowStatus.DRAFT);

        CorresponType corresponType = new CorresponType();
        corresponType.setForceToUseWorkflow(ForceToUseWorkflow.OPTIONAL);
        expCorrespon.setCorresponType(corresponType);;

        // Exception初期化
        MockAbstractDao.EXCEPTION = false;
        MockAbstractDao.ExceptionWorkflow = false;
        MockAbstractDao.EDITMODE = MockAbstractDao.DEFAULT_MODE;
        MockAbstractDao.countElement = 0;

        service.issue(expCorrespon);

        // 確認
        assertEquals(MockAbstractDao.RET_UPDATE_CORRESPON.getId(), expCorrespon.getId());
        assertNotNull(MockAbstractDao.RET_UPDATE_CORRESPON.getCorresponNo());
        assertNotNull(MockAbstractDao.RET_UPDATE_CORRESPON.getIssuedAt());
        assertNotNull(MockAbstractDao.RET_UPDATE_CORRESPON.getWorkflowStatus());
        assertEquals(MockAbstractDao.RET_UPDATE_CORRESPON.getUpdatedBy().getEmpNo(),
            MockAbstractService.CURRENT_USER.getEmpNo());
        assertEquals(MockAbstractDao.RET_UPDATE_CORRESPON.getUpdatedBy().getNameE(),
            MockAbstractService.CURRENT_USER.getNameE());
        assertNull(MockAbstractDao.RET_UPDATE_CORRESPON.getProjectId());
        assertNull(MockAbstractDao.RET_UPDATE_CORRESPON.getFromCorresponGroup());
        assertNull(MockAbstractDao.RET_UPDATE_CORRESPON.getCorresponType());
        assertNull(MockAbstractDao.RET_UPDATE_CORRESPON.getSubject());
        assertNull(MockAbstractDao.RET_UPDATE_CORRESPON.getDeadlineForReply());
        assertNull(MockAbstractDao.RET_UPDATE_CORRESPON.getCorresponStatus());
        assertNull(MockAbstractDao.RET_UPDATE_CORRESPON.getCreatedBy());
        assertNull(MockAbstractDao.RET_UPDATE_CORRESPON.getCreatedAt());
        assertNull(MockAbstractDao.RET_UPDATE_CORRESPON.getBody());
    }

    /**
     * コレポン文書を発行する(承認状態が不正). （SystemAdmin）
     */
    @Test
    public void testIssue03() throws Exception {

        MockAbstractService.IS_SYSTEM_ADMIN = true;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        MockAbstractService.IS_ANY_GROUP_ADMIN = false;

        List<Project> projects = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        projects.add(p);
        MockAbstractService.ACCESSIBLE_PROJECTS = projects;

        createCorresponNo();

        Correspon expCorrespon = createCorrespon02();
        expCorrespon.setProjectId("PJ1");
        expCorrespon.setWorkflowStatus(WorkflowStatus.DENIED);

        CorresponType corresponType = new CorresponType();
        corresponType.setForceToUseWorkflow(ForceToUseWorkflow.OPTIONAL);
        expCorrespon.setCorresponType(corresponType);;

        try {
            service.issue(expCorrespon);
            fail("例外が発生していない");

        } catch (ServiceAbortException e) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CORRESPON_STATUS_INVALID, e
                .getMessageCode());

        }

    }

    /**
     * コレポン文書を発行する(ユーザーが不正). （ProjectAdmin）
     */
    @Test
    public void testIssue04() throws Exception {

        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN = true;
        MockAbstractService.IS_ANY_GROUP_ADMIN = false;

        List<Project> projects = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        projects.add(p);
        MockAbstractService.ACCESSIBLE_PROJECTS = projects;

        createCorresponNo();

        Correspon expCorrespon = createCorrespon02();
        expCorrespon.setProjectId("PJ1");
        expCorrespon.setWorkflowStatus(WorkflowStatus.DRAFT);

        CorresponType corresponType = new CorresponType();
        corresponType.setForceToUseWorkflow(ForceToUseWorkflow.OPTIONAL);
        expCorrespon.setCorresponType(corresponType);;

        try {
            service.issue(expCorrespon);
            fail("例外が発生していない");

        } catch (ServiceAbortException e) {

            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT, e
                .getMessageCode());

        }

    }

    /**
     * コレポン文書を発行する(承認パターン適用フラグが必須). （Preparer）
     */
    @Test
    public void testIssue05() throws Exception {

        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN = true;
        MockAbstractService.IS_ANY_GROUP_ADMIN = false;

        List<Project> projects = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        projects.add(p);
        MockAbstractService.ACCESSIBLE_PROJECTS = projects;

        createCorresponNo();

        Correspon expCorrespon = createCorrespon01();
        expCorrespon.setProjectId("PJ1");
        expCorrespon.setWorkflowStatus(WorkflowStatus.DRAFT);

        CorresponType corresponType = new CorresponType();
        corresponType.setForceToUseWorkflow(ForceToUseWorkflow.REQUIRED);
        expCorrespon.setCorresponType(corresponType);;

        try {
            service.issue(expCorrespon);
            fail("例外が発生していない");

        } catch (ServiceAbortException e) {

            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT, e
                .getMessageCode());

        }

    }
    /**
     * testIssue01(コレポン文書を発行する. （Preparer）)
     * ただし返信。
     */
    @Test
    public void testIssue06() throws Exception {

        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        MockAbstractService.IS_ANY_GROUP_ADMIN = false;

        List<Project> projects = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        projects.add(p);
        MockAbstractService.ACCESSIBLE_PROJECTS = projects;

        createCorresponNo();

        Correspon expCorrespon = createCorrespon01();
        expCorrespon.setProjectId("PJ1");
        expCorrespon.setWorkflowStatus(WorkflowStatus.DRAFT);

        CorresponType corresponType = new CorresponType();
        corresponType.setForceToUseWorkflow(ForceToUseWorkflow.OPTIONAL);
        expCorrespon.setCorresponType(corresponType);;

        // Exception初期化
        MockAbstractDao.EXCEPTION = false;
        MockAbstractDao.ExceptionWorkflow = false;
        MockAbstractDao.EDITMODE = MockAbstractDao.DEFAULT_MODE;
        MockAbstractDao.countElement = 0;

        // 返信元コレポンの文書状態がCanceledならば、発行するコレポンの文書状態は強制的にCancel
        expCorrespon.setParentCorresponId(1234L);
        MockCorresponSequenceService.CORRESPON_NO = "9999";
        Correspon oldCorrespon = new Correspon();
        oldCorrespon.setCorresponStatus(CorresponStatus.CLOSED);
        MockAbstractDao.RET_FIND_BY_ID_CORRESPONGROUP = null;
        setUpCorresponType(oldCorrespon);
        MockAbstractDao.RET_FIND_BY_ID.put("1", oldCorrespon);

        service.issue(expCorrespon);

        // 確認
        assertEquals(MockAbstractDao.RET_UPDATE_CORRESPON.getId(), expCorrespon.getId());
        assertNotNull(MockAbstractDao.RET_UPDATE_CORRESPON.getCorresponNo());
        assertNotNull(MockAbstractDao.RET_UPDATE_CORRESPON.getIssuedAt());
        assertNotNull(MockAbstractDao.RET_UPDATE_CORRESPON.getWorkflowStatus());
        assertEquals(MockAbstractDao.RET_UPDATE_CORRESPON.getUpdatedBy().getEmpNo(),
            MockAbstractService.CURRENT_USER.getEmpNo());
        assertEquals(MockAbstractDao.RET_UPDATE_CORRESPON.getUpdatedBy().getNameE(),
            MockAbstractService.CURRENT_USER.getNameE());
        assertNull(MockAbstractDao.RET_UPDATE_CORRESPON.getProjectId());
        assertNull(MockAbstractDao.RET_UPDATE_CORRESPON.getFromCorresponGroup());
        assertNull(MockAbstractDao.RET_UPDATE_CORRESPON.getCorresponType());
        assertNull(MockAbstractDao.RET_UPDATE_CORRESPON.getSubject());
        assertNull(MockAbstractDao.RET_UPDATE_CORRESPON.getDeadlineForReply());
        assertNull(MockAbstractDao.RET_UPDATE_CORRESPON.getCorresponStatus());
        assertNull(MockAbstractDao.RET_UPDATE_CORRESPON.getCreatedBy());
        assertNull(MockAbstractDao.RET_UPDATE_CORRESPON.getCreatedAt());
        assertNull(MockAbstractDao.RET_UPDATE_CORRESPON.getBody());
    }

    /**
     * testIssue01(コレポン文書を発行する. （Preparer）)
     * 返信で、返信元コレポンが既に存在しなくても、文書状態は変わらない(Canceledにならない)
     */
    @Test
    public void testIssue07() throws Exception {

        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        MockAbstractService.IS_ANY_GROUP_ADMIN = false;

        List<Project> projects = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        projects.add(p);
        MockAbstractService.ACCESSIBLE_PROJECTS = projects;

        createCorresponNo();

        Correspon expCorrespon = createCorrespon01();
        expCorrespon.setProjectId("PJ1");
        expCorrespon.setWorkflowStatus(WorkflowStatus.DRAFT);

        CorresponType corresponType = new CorresponType();
        corresponType.setForceToUseWorkflow(ForceToUseWorkflow.OPTIONAL);
        expCorrespon.setCorresponType(corresponType);;

        // Exception初期化
        MockAbstractDao.EXCEPTION = false;
        MockAbstractDao.ExceptionWorkflow = false;
        MockAbstractDao.EDITMODE = MockAbstractDao.DEFAULT_MODE;
        MockAbstractDao.countElement = 0;

        // 返信元コレポンの文書状態がCanceledならば、発行するコレポンの文書状態は強制的にCancel
        expCorrespon.setParentCorresponId(1234L);
        MockCorresponSequenceService.CORRESPON_NO = "9999";
        MockAbstractDao.RET_FIND_BY_ID_CORRESPONGROUP = null;
        MockAbstractDao.RET_FIND_BY_ID.put("1", null);

        service.issue(expCorrespon);

        // 確認
        assertEquals(MockAbstractDao.RET_UPDATE_CORRESPON.getId(), expCorrespon.getId());
        assertNotNull(MockAbstractDao.RET_UPDATE_CORRESPON.getCorresponNo());
        assertNotNull(MockAbstractDao.RET_UPDATE_CORRESPON.getIssuedAt());
        assertNotNull(MockAbstractDao.RET_UPDATE_CORRESPON.getWorkflowStatus());
        assertEquals(MockAbstractDao.RET_UPDATE_CORRESPON.getUpdatedBy().getEmpNo(),
            MockAbstractService.CURRENT_USER.getEmpNo());
        assertEquals(MockAbstractDao.RET_UPDATE_CORRESPON.getUpdatedBy().getNameE(),
            MockAbstractService.CURRENT_USER.getNameE());
        assertNull(MockAbstractDao.RET_UPDATE_CORRESPON.getProjectId());
        assertNull(MockAbstractDao.RET_UPDATE_CORRESPON.getFromCorresponGroup());
        assertNull(MockAbstractDao.RET_UPDATE_CORRESPON.getCorresponType());
        assertNull(MockAbstractDao.RET_UPDATE_CORRESPON.getSubject());
        assertNull(MockAbstractDao.RET_UPDATE_CORRESPON.getDeadlineForReply());
        assertNull(MockAbstractDao.RET_UPDATE_CORRESPON.getCorresponStatus());
        assertNull(MockAbstractDao.RET_UPDATE_CORRESPON.getCreatedBy());
        assertNull(MockAbstractDao.RET_UPDATE_CORRESPON.getCreatedAt());
        assertNull(MockAbstractDao.RET_UPDATE_CORRESPON.getBody());
    }
    /**
     * testIssue01(コレポン文書を発行する. （Preparer）)
     * 返信で、返信元コレポンの文書状態がCanceledでも、文書状態は変わらない(Canceledにならない)
     */
    @Test
    public void testIssue08() throws Exception {

        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        MockAbstractService.IS_ANY_GROUP_ADMIN = false;

        List<Project> projects = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        projects.add(p);
        MockAbstractService.ACCESSIBLE_PROJECTS = projects;

        createCorresponNo();

        Correspon expCorrespon = createCorrespon01();
        expCorrespon.setProjectId("PJ1");
        expCorrespon.setWorkflowStatus(WorkflowStatus.DRAFT);

        CorresponType corresponType = new CorresponType();
        corresponType.setForceToUseWorkflow(ForceToUseWorkflow.OPTIONAL);
        expCorrespon.setCorresponType(corresponType);;

        // Exception初期化
        MockAbstractDao.EXCEPTION = false;
        MockAbstractDao.ExceptionWorkflow = false;
        MockAbstractDao.EDITMODE = MockAbstractDao.DEFAULT_MODE;
        MockAbstractDao.countElement = 0;

        // 返信元コレポンの文書状態がCanceledならば、発行するコレポンの文書状態は強制的にCancel
        expCorrespon.setParentCorresponId(1234L);
        MockCorresponSequenceService.CORRESPON_NO = "9999";
        Correspon oldCorrespon = new Correspon();
        oldCorrespon.setCorresponStatus(CorresponStatus.CANCELED);
        MockAbstractDao.RET_FIND_BY_ID_CORRESPONGROUP = null;
        setUpCorresponType(oldCorrespon);
        MockAbstractDao.RET_FIND_BY_ID.put("1", oldCorrespon);

        service.issue(expCorrespon);

        // 確認
        assertEquals(MockAbstractDao.RET_UPDATE_CORRESPON.getId(), expCorrespon.getId());
        assertNotNull(MockAbstractDao.RET_UPDATE_CORRESPON.getCorresponNo());
        assertNotNull(MockAbstractDao.RET_UPDATE_CORRESPON.getIssuedAt());
        assertNotNull(MockAbstractDao.RET_UPDATE_CORRESPON.getWorkflowStatus());
        assertEquals(MockAbstractDao.RET_UPDATE_CORRESPON.getUpdatedBy().getEmpNo(),
            MockAbstractService.CURRENT_USER.getEmpNo());
        assertEquals(MockAbstractDao.RET_UPDATE_CORRESPON.getUpdatedBy().getNameE(),
            MockAbstractService.CURRENT_USER.getNameE());
        assertNull(MockAbstractDao.RET_UPDATE_CORRESPON.getProjectId());
        assertNull(MockAbstractDao.RET_UPDATE_CORRESPON.getFromCorresponGroup());
        assertNull(MockAbstractDao.RET_UPDATE_CORRESPON.getCorresponType());
        assertNull(MockAbstractDao.RET_UPDATE_CORRESPON.getSubject());
        assertNull(MockAbstractDao.RET_UPDATE_CORRESPON.getDeadlineForReply());
        assertNull(MockAbstractDao.RET_UPDATE_CORRESPON.getCorresponStatus());
        assertNull(MockAbstractDao.RET_UPDATE_CORRESPON.getCreatedBy());
        assertNull(MockAbstractDao.RET_UPDATE_CORRESPON.getCreatedAt());
        assertNull(MockAbstractDao.RET_UPDATE_CORRESPON.getBody());
    }


    /**
     * テスト用コレポン文書の作成
     */
    private Correspon createCorrespon01() {
        Correspon c = new Correspon();
        c.setId(1L);
        c.setCorresponNo("YOC:OT:BUILDING-00001");
        CorresponGroup from = new CorresponGroup();
        from.setId(new Long(1L));
        from.setName("YOC:BUILDING");
        c.setFromCorresponGroup(from);
        c.setSubject("Mock");
        CorresponType ct = new CorresponType();
        ct.setId(new Long(1L));
        ct.setName("Request");
        c.setCorresponType(ct);
        c.setWorkflowStatus(WorkflowStatus.DRAFT);
        c.setCreatedAt(new GregorianCalendar(2009, 3, 1, 1, 1, 1).getTime());
        c.setUpdatedAt(new GregorianCalendar(2009, 3, 10, 10, 10, 10).getTime());
        c.setCreatedBy(user);
        c.setUpdatedBy(user);
        c.setDeadlineForReply(new GregorianCalendar(2009, 4, 1).getTime());
        c.setCorresponStatus(CorresponStatus.OPEN);

        return c;
    }

    /**
     * テスト用コレポン文書の作成
     */
    private Correspon createCorrespon02() {
        Correspon c = new Correspon();
        c.setId(1L);
        c.setCorresponNo("YOC:OT:BUILDING-00001");
        CorresponGroup from = new CorresponGroup();
        from.setId(new Long(1L));
        from.setName("YOC:BUILDING");
        c.setFromCorresponGroup(from);
        c.setSubject("Mock");
        CorresponType ct = new CorresponType();
        ct.setId(new Long(1L));
        ct.setName("Request");
        c.setCorresponType(ct);
        c.setWorkflowStatus(WorkflowStatus.DRAFT);
        c.setCreatedAt(new GregorianCalendar(2009, 3, 1, 1, 1, 1).getTime());
        c.setUpdatedAt(new GregorianCalendar(2009, 3, 10, 10, 10, 10).getTime());
        User u = new User();
        u.setEmpNo("00005");
        u.setNameE("Delete User");
        c.setCreatedBy(u);
        c.setUpdatedBy(user);
        c.setDeadlineForReply(new GregorianCalendar(2009, 4, 1).getTime());
        c.setCorresponStatus(CorresponStatus.OPEN);

        return c;
    }

    /**
     * テスト用文書番号の作成
     */
    private void createCorresponNo() {
        Site site = new Site();
        site.setId(1L);
        site.setSiteCd("SITE");
        Company company = new Company();
        company.setId(2L);
        company.setCompanyCd("COMPANY");
        Discipline discipline = new Discipline();
        discipline.setId(3L);
        discipline.setDisciplineCd("DISCIPLINE");

        ProjectUser projectUser = new ProjectUser();
        projectUser.setProjectCompany(company);
        MockAbstractService.PROJECT_USER = projectUser;

        MockParentCorresponNoSeqDao.RET_FIND_FOR_UPDATE = null;
        MockAbstractDao.RET_CREATE = 1L;

        CorresponGroup corresponGroup = new CorresponGroup();
        corresponGroup.setId(1L);
        corresponGroup.setSite(site);
        corresponGroup.setDiscipline(discipline);
        List<CorresponGroup> list = new ArrayList<CorresponGroup>();
        list.add(corresponGroup);
        MockAbstractDao.RET_FIND_BY_ID_CORRESPONGROUP = list;

    }

    /**
     * {@link CorresponServiceImpl#findReplyCorrespons(Correspon, Long)}のテストケース.
     * 正常系
     * @throws Exception
     */
    @Test
    public void testFindReplyCorrespons() throws Exception {
        Correspon c;
        // ダミーの戻り値をセット
        List<Correspon> replyCorrespons = new ArrayList<Correspon>();
        c = new Correspon();
        c.setId(13L);
        c.setCorresponNo("YOC:OT:IT-ZZA11");
        replyCorrespons.add(c);

        c = new Correspon();
        c.setId(88L);
        c.setCorresponNo("YOC:OT:IT-ZZA24");
        replyCorrespons.add(c);

        MockCorresponDao.RET_FIND_REPLY_CORRESPONS = replyCorrespons;

        Correspon parentCorrespon = new Correspon();
        parentCorrespon.setId(12L);
        parentCorrespon.setProjectId("PJ1");
        parentCorrespon.setWorkflowStatus(WorkflowStatus.ISSUED);
        User u = new User();
        u.setEmpNo("ZZA99");
        parentCorrespon.setCreatedBy(u);

        List<Project> projects = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        projects.add(p);
        MockAbstractService.ACCESSIBLE_PROJECTS = projects;

        List<Correspon> actual = service.findReplyCorrespons(parentCorrespon, 1L);
        assertNotNull(actual);
        assertEquals(replyCorrespons.size(), actual.size());
    }

    /**
     * {@link CorresponServiceImpl#findReplyCorrespons(Correspon, Long)}のテストケース.
     * 依頼コレポン文書がnullの場合の検証.
     * @throws Exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testFindReplyCorresponsParentCorresponIsNull() throws Exception {
        service.findReplyCorrespons(null, 1L);
    }

    /**
     * {@link CorresponServiceImpl#findReplyCorrespons(Correspon, Long)}のテストケース.
     * 宛先-ユーザーIDがnullの場合の検証.
     * @throws Exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testFindReplyCorresponsIdIsNull() throws Exception {
        service.findReplyCorrespons(new Correspon(), null);
    }

    /**
     * {@link CorresponServiceImpl#assignPersonInCharge(Correspon, AddressUser,
     * List<PersonInCharge>)}のテストケース. 新規登録(PICが設定されていないところにPIC登録)
     */
    @Test
    public void testAssignPersonInCharge01() throws Exception {
        // パラメータ設定
        Correspon correspon = new Correspon();
        correspon.setId(987001L);
        correspon.setProjectId("testassignProjectId");

        AddressUser addressUser = new AddressUser();
        addressUser.setId(987002L);
        User user = new User();
        user.setEmpNo("testassignUser");
        addressUser.setUser(user);

        List<PersonInCharge> picList = new ArrayList<PersonInCharge>();
        PersonInCharge pic = new PersonInCharge();
        user = new User();
        user.setEmpNo("PIC01");
        pic.setAddressUserId(addressUser.getId());
        pic.setUser(user);
        picList.add(pic);
        pic = new PersonInCharge();
        user = new User();
        user.setEmpNo("PIC02");
        pic.setAddressUserId(addressUser.getId());
        pic.setUser(user);
        picList.add(pic);

        MockPersonInChargeDao.RET_FIND_BY_ADDRESS_USER_ID = Collections.emptyList();

        // テスト環境設定
        user = new User();
        user.setEmpNo("ZZA01");
        MockAbstractService.CURRENT_USER = user;

        MockProjectUserDao.FIND_BY_EMP_NO = new User(); // PICユーザ存在チェック回避
        MockAbstractService.IS_SYSTEM_ADMIN = true; // PICユーザ権限チェック回避

        // 期待値設定
        List<PersonInCharge> expectedPicList = new ArrayList<PersonInCharge>();
        PersonInCharge expectedPic = new PersonInCharge();
        user = new User();
        user.setEmpNo("PIC01");
        expectedPic.setAddressUserId(987002L);
        expectedPic.setUser(user);
        expectedPic.setCreatedBy(MockAbstractService.CURRENT_USER);
        expectedPic.setUpdatedBy(MockAbstractService.CURRENT_USER);
        expectedPicList.add(expectedPic);
        expectedPic = new PersonInCharge();
        user = new User();
        user.setEmpNo("PIC02");
        expectedPic.setAddressUserId(987002L);
        expectedPic.setUser(user);
        expectedPic.setCreatedBy(MockAbstractService.CURRENT_USER);
        expectedPic.setUpdatedBy(MockAbstractService.CURRENT_USER);
        expectedPicList.add(expectedPic);

        MockAbstractDao.RET_CREATE_PERSONINCHARGE = expectedPicList;

        // テスト
        service.assignPersonInCharge(correspon, addressUser, picList);

        assertEquals(2, MockAbstractDao.createPersonInChargeCount);
    }

    /**
     * {@link CorresponServiceImpl#assignPersonInCharge(Correspon, AddressUser,
     * List<PersonInCharge>)}のテストケース. 更新(PICが設定されているころにPIC登録)
     */
    @Test
    public void testAssignPersonInCharge02() throws Exception {
        // パラメータ設定
        Correspon correspon = new Correspon();
        correspon.setId(987001L);
        correspon.setProjectId("testassignProjectId");

        AddressUser addressUser = new AddressUser();
        addressUser.setId(987002L);
        User user = new User();
        user.setEmpNo("testassignUser");
        addressUser.setUser(user);

        List<PersonInCharge> picList = new ArrayList<PersonInCharge>();
        PersonInCharge pic = new PersonInCharge();
        user = new User();
        user.setEmpNo("PIC01");
        pic.setAddressUserId(addressUser.getId());
        pic.setUser(user);
        picList.add(pic);
        pic = new PersonInCharge();
        user = new User();
        user.setEmpNo("PIC02");
        pic.setAddressUserId(addressUser.getId());
        pic.setUser(user);
        picList.add(pic);

        List<PersonInCharge> oldPicList = new ArrayList<PersonInCharge>();
        PersonInCharge oldPic = new PersonInCharge();
        user = new User();
        user.setEmpNo("PIC00");
        oldPic.setAddressUserId(addressUser.getId());
        oldPic.setUser(user);
        oldPicList.add(oldPic);
        oldPic = new PersonInCharge();
        user = new User();
        user.setEmpNo("PIC99");
        oldPic.setAddressUserId(addressUser.getId());
        oldPic.setUser(user);
        oldPicList.add(oldPic);
        MockPersonInChargeDao.RET_FIND_BY_ADDRESS_USER_ID = oldPicList;

        // テスト環境設定
        user = new User();
        user.setEmpNo("ZZA01");
        MockAbstractService.CURRENT_USER = user;

        MockProjectUserDao.FIND_BY_EMP_NO = new User(); // PICユーザ存在チェック回避
        MockAbstractService.IS_SYSTEM_ADMIN = true; // PICユーザ権限チェック回避

        MockPersonInChargeDao.RET_DELETE_BY_CORRESPONID = 2; // ★削除アリ
        pic = new PersonInCharge();
        pic.setAddressUserId(987002L);
        pic.setUpdatedBy(MockAbstractService.CURRENT_USER);
        MockPersonInChargeDao.RET_UPDATE_PERSONINCHARGE = pic;

        // 期待値設定
        List<PersonInCharge> expectedPicList = new ArrayList<PersonInCharge>();
        PersonInCharge expectedPic = new PersonInCharge();
        user = new User();
        user.setEmpNo("PIC01");
        expectedPic.setAddressUserId(987002L);
        expectedPic.setUser(user);
        expectedPic.setCreatedBy(MockAbstractService.CURRENT_USER);
        expectedPic.setUpdatedBy(MockAbstractService.CURRENT_USER);
        expectedPicList.add(expectedPic);
        expectedPic = new PersonInCharge();
        user = new User();
        user.setEmpNo("PIC02");
        expectedPic.setAddressUserId(987002L);
        expectedPic.setUser(user);
        expectedPic.setCreatedBy(MockAbstractService.CURRENT_USER);
        expectedPic.setUpdatedBy(MockAbstractService.CURRENT_USER);
        expectedPicList.add(expectedPic);

        MockAbstractDao.RET_CREATE_PERSONINCHARGE = expectedPicList;

        // テスト
        service.assignPersonInCharge(correspon, addressUser, picList);

        assertEquals(2, MockAbstractDao.createPersonInChargeCount);
        assertEquals(2, MockAbstractDao.createPersonInChargeCount);
    }

    /**
     * {@link CorresponServiceImpl#assignPersonInCharge(Correspon, AddressUser,
     * List<PersonInCharge>)}のテストケース. PICユーザが存在しない
     */
    @Test
    public void testAssignPersonInCharge03() throws Exception {
        // パラメータ設定
        Correspon correspon = new Correspon();
        correspon.setId(987001L);
        correspon.setProjectId("testassignProjectId");

        AddressUser addressUser = new AddressUser();
        addressUser.setId(987002L);
        User user = new User();
        user.setEmpNo("testassignUser");
        addressUser.setUser(user);

        List<PersonInCharge> picList = new ArrayList<PersonInCharge>();
        PersonInCharge pic = new PersonInCharge();
        user = new User();
        user.setEmpNo("PIC01");
        pic.setAddressUserId(addressUser.getId());
        pic.setUser(user);
        picList.add(pic);
        pic = new PersonInCharge();
        user = new User();
        user.setEmpNo("PIC02");
        pic.setAddressUserId(addressUser.getId());
        pic.setUser(user);
        picList.add(pic);

        // テスト環境設定
        user = new User();
        user.setEmpNo("ZZA01");
        MockAbstractService.CURRENT_USER = user;

        MockProjectUserDao.FIND_BY_EMP_NO = null; // ★PICユーザ存在チェック回避
        MockAbstractService.IS_SYSTEM_ADMIN = true; // PICユーザ権限チェック回避

        // テスト
        try {
            service.assignPersonInCharge(correspon, addressUser, picList);
            fail("例外が発生しませんでした");
        } catch (ServiceAbortException e) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_PERSON_IN_CHARGE_NOT_EXIST,
                e.getMessageCode());
        }
        assertEquals(0, MockAbstractDao.createPersonInChargeCount);
    }

    /**
     * {@link CorresponServiceImpl#assignPersonInCharge(Correspon, AddressUser,
     * List<PersonInCharge>)}のテストケース. PICユーザにコレポンprojectの権限が無い
     */
    @Test
    public void testAssignPersonInCharge04() throws Exception {
        // パラメータ設定
        Correspon correspon = new Correspon();
        correspon.setId(987001L);
        correspon.setProjectId("testassignProjectId");

        AddressUser addressUser = new AddressUser();
        addressUser.setId(987002L);
        User user = new User();
        user.setEmpNo("testassignUser");
        addressUser.setUser(user);

        List<PersonInCharge> picList = new ArrayList<PersonInCharge>();
        PersonInCharge pic = new PersonInCharge();
        user = new User();
        user.setEmpNo("PIC01");
        pic.setAddressUserId(addressUser.getId());
        pic.setUser(user);
        picList.add(pic);
        pic = new PersonInCharge();
        user = new User();
        user.setEmpNo("PIC02");
        pic.setAddressUserId(addressUser.getId());
        pic.setUser(user);
        picList.add(pic);

        // テスト環境設定
        user = new User();
        user.setEmpNo("ZZA01");
        MockAbstractService.CURRENT_USER = user;

        MockProjectUserDao.FIND_BY_EMP_NO = new User(); // PICユーザ存在チェック回避
        MockAbstractService.IS_SYSTEM_ADMIN = false; // ★PICユーザ権限チェック回避

        // テスト
        try {
            service.assignPersonInCharge(correspon, addressUser, picList);
            fail("例外が発生しませんでした");
        } catch (ServiceAbortException e) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_INVALID_PERSON_IN_CHARGE, e
                .getMessageCode());
        }
        assertEquals(0, MockAbstractDao.createPersonInChargeCount);
    }

    /**
     * {@link CorresponServiceImpl#assignPersonInCharge(Correspon, AddressUser,
     * List<PersonInCharge>)}のテストケース. そのAddressUserは既にコレポンを作成済み
     */
    @Test
    public void testAssignPersonInCharge05() throws Exception {
        // パラメータ設定
        Correspon correspon = new Correspon();
        correspon.setId(987001L);
        correspon.setProjectId("testassignProjectId");

        AddressUser addressUser = new AddressUser();
        addressUser.setId(987002L);
        User user = new User();
        user.setEmpNo("testassignUser");
        addressUser.setUser(user);

        List<PersonInCharge> dummy = new ArrayList<PersonInCharge>();
        List<PersonInCharge> picList = new ArrayList<PersonInCharge>();
        PersonInCharge pic = new PersonInCharge();
        user = new User();
        user.setEmpNo("PIC01");
        pic.setAddressUserId(addressUser.getId());
        pic.setUser(user);
        picList.add(pic);

        PersonInCharge temp = new PersonInCharge();
        User createdBy = new User();
        createdBy.setEmpNo("ZZA01");
        User updatedBy = new User();
        updatedBy.setEmpNo("ZZA01");
        temp.setAddressUserId(addressUser.getId());
        temp.setUser(user);
        temp.setCreatedBy(createdBy);
        temp.setCreatedAt(new Date());
        temp.setUpdatedBy(updatedBy);
        temp.setUpdatedAt(new Date());
        dummy.add(temp);

        pic = new PersonInCharge();
        user = new User();
        user.setEmpNo("PIC02");
        pic.setAddressUserId(addressUser.getId());
        pic.setUser(user);
        picList.add(pic);

        temp = new PersonInCharge();
        temp.setAddressUserId(addressUser.getId());
        temp.setUser(user);
        temp.setCreatedBy(createdBy);
        temp.setCreatedAt(new Date());
        temp.setUpdatedBy(updatedBy);
        temp.setUpdatedAt(new Date());
        dummy.add(temp);
        MockAbstractDao.RET_CREATE_PERSONINCHARGE = dummy;

        List<PersonInCharge> oldPicList = new ArrayList<PersonInCharge>();
        PersonInCharge oldPic = new PersonInCharge();
        user = new User();
        user.setEmpNo("PIC00");
        oldPic.setAddressUserId(addressUser.getId());
        oldPic.setUser(user);
        oldPicList.add(oldPic);
        oldPic = new PersonInCharge();
        user = new User();
        user.setEmpNo("PIC99");
        oldPic.setAddressUserId(addressUser.getId());
        oldPic.setUser(user);
        oldPicList.add(oldPic);
        MockPersonInChargeDao.RET_FIND_BY_ADDRESS_USER_ID = oldPicList;

        // テスト環境設定
        user = new User();
        user.setEmpNo("ZZA01");
        MockAbstractService.CURRENT_USER = user;

        MockProjectUserDao.FIND_BY_EMP_NO = new User(); // PICユーザ存在チェック回避
        MockAbstractService.IS_SYSTEM_ADMIN = true; // PICユーザ権限チェック回避
        MockCorresponHierarchyDao.COUNT = 1; // ★コレポン文書階層にレコードあり(コレポン作成済み)

        // テスト
        try {
            // Attentionの返信有無にかかわらずPICの設定は可能
            service.assignPersonInCharge(correspon, addressUser, picList);
        } catch (ServiceAbortException e) {
            fail("例外が発生しました");
        }
        assertEquals(2, MockAbstractDao.createPersonInChargeCount);
        assertEquals(2, MockAbstractDao.createPersonInChargeCount);
    }

    /**
     * {@link CorresponServiceImpl#findCorresponResponseHistory(Correspon)}のテストケース.
     * 大本のコレポン文書を使用.
     */
    @Test
    public void testFindCorresponResponseHistory01() throws Exception {
        MockCorresponDao.RET_FIND_CORRESPONS_RESPONSE_HISTORY = createResponseHistoryList(10L, 3);

        Correspon correspon = new Correspon();
        correspon.setId(10L);
        correspon.setParentCorresponId(null);
        correspon.setParentCorresponNo(null);

        List<CorresponResponseHistory> crh
            = service.findCorresponResponseHistory(correspon);

        assertEquals(3, crh.size());
    }

    /**
     * {@link CorresponServiceImpl#findCorresponResponseHistory(Correspon)}のテストケース.
     * 返信文書を使用.
     */
    @Test
    public void testFindCorresponResponseHistory02() throws Exception {
        MockCorresponDao.RET_FIND_CORRESPONS_RESPONSE_HISTORY = createResponseHistoryList(4L, 2);

        Correspon correspon = new Correspon();
        correspon.setId(4L);
        correspon.setParentCorresponId(3L);
        correspon.setParentCorresponNo("AA");

        List<CorresponResponseHistory> crh
            = service.findCorresponResponseHistory(correspon);

        assertEquals(5, crh.size());
        assertEquals(MockCorresponDao.ROOT_CORRESPON_ID, crh.get(0).getCorrespon().getId());
    }

    private List<CorresponResponseHistory> createResponseHistoryList(Long id, int count) {
        List<CorresponResponseHistory> histories = new ArrayList<CorresponResponseHistory>();
        Correspon correspon = null;
        CorresponResponseHistory history = null;
        for (int i = 0 ; i < count ; i++) {
            correspon = new Correspon();
            correspon.setId(id++);
            correspon.setSubject("Subject:" + i);
            List<AddressCorresponGroup> groups = new ArrayList<AddressCorresponGroup>();
            groups.add(new AddressCorresponGroup());
            correspon.setAddressCorresponGroups(groups);
            history = new CorresponResponseHistory();
            history.setCorrespon(correspon);
            history.setLevel(new Long(i));
            histories.add(history);
        }
        return histories;
    }

    /**
     * コレポン文書状態更新 Preparerが更新
     * @throws Exception
     */
    @Test
    public void testUpdateCorresponStatus01() throws Exception {
        Correspon correspon = createCorresponUpdate01();
        Correspon expected = createCorresponUpdate01();
        CorresponStatus status = CorresponStatus.CLOSED;

        MockAbstractDao.RET_FIND_BY_ID_CORRESPONGROUP = null;
        MockAbstractDao.RET_FIND_BY_ID.put("1", correspon);

        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        MockAbstractService.IS_GROUP_ADMIN = null;

        MockCorresponReadStatusService.SET_READ_STATUS = ReadStatus.READ ;

        // Preparer
        User currentUser = new User();
        currentUser.setEmpNo("currentUser");
        MockAbstractService.CURRENT_USER = currentUser;
        correspon.setCreatedBy(MockAbstractService.CURRENT_USER);
        expected.setCreatedBy(correspon.getCreatedBy());

        MockUserRoleHelper.IS_CHECKER = false;
        MockUserRoleHelper.IS_APPROVER = false;

        expected.setCorresponStatus(status);
        expected.setUpdatedBy(MockAbstractService.CURRENT_USER);
        MockAbstractDao.RET_UPDATE_CORRESPON = expected;

        MockAbstractDao.EDITMODE = MockAbstractDao.UPDATE_MODE;
        service.updateCorresponStatus(correspon, status);

        // closeの場合既読・未読状態は変わらない
        assertEquals(ReadStatus.READ, MockCorresponReadStatusService.SET_READ_STATUS);
    }

    /**
     * コレポン文書状態更新 Checkerが更新
     * @throws Exception
     */
    @Test
    public void testUpdateCorresponStatus02() throws Exception {
        Correspon correspon = createCorresponUpdate01();
        Correspon expected = createCorresponUpdate01();
        CorresponStatus status = CorresponStatus.CLOSED;

        MockAbstractDao.RET_FIND_BY_ID_CORRESPONGROUP = null;
        MockAbstractDao.RET_FIND_BY_ID.put("1", correspon);

        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        MockAbstractService.IS_GROUP_ADMIN = null;

        MockCorresponReadStatusService.SET_READ_STATUS = ReadStatus.READ ;

        // Preparer
        User currentUser = new User();
        currentUser.setEmpNo("currentUser");
        MockAbstractService.CURRENT_USER = currentUser;
        User createdBy = new User();
        createdBy.setEmpNo("createdBy");
        correspon.setCreatedBy(createdBy);
        expected.setCreatedBy(correspon.getCreatedBy());

        MockUserRoleHelper.IS_CHECKER = true;
        MockUserRoleHelper.IS_APPROVER = false;

        expected.setCorresponStatus(status);
        expected.setUpdatedBy(MockAbstractService.CURRENT_USER);
        MockAbstractDao.RET_UPDATE_CORRESPON = expected;

        MockAbstractDao.EDITMODE = MockAbstractDao.UPDATE_MODE;
        service.updateCorresponStatus(correspon, status);

        // closeの場合既読・未読状態は変わらない
        assertEquals(ReadStatus.READ, MockCorresponReadStatusService.SET_READ_STATUS);
    }

    /**
     * コレポン文書状態更新 Approverが更新
     * @throws Exception
     */
    @Test
    public void testUpdateCorresponStatus03() throws Exception {
        Correspon correspon = createCorresponUpdate01();
        Correspon expected = createCorresponUpdate01();
        CorresponStatus status = CorresponStatus.CLOSED;

        MockAbstractDao.RET_FIND_BY_ID_CORRESPONGROUP = null;
        MockAbstractDao.RET_FIND_BY_ID.put("1", correspon);

        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        MockAbstractService.IS_GROUP_ADMIN = null;

        MockCorresponReadStatusService.SET_READ_STATUS = ReadStatus.READ ;

        // Preparer
        User currentUser = new User();
        currentUser.setEmpNo("currentUser");
        MockAbstractService.CURRENT_USER = currentUser;
        User createdBy = new User();
        createdBy.setEmpNo("createdBy");
        correspon.setCreatedBy(createdBy);
        expected.setCreatedBy(correspon.getCreatedBy());

        MockUserRoleHelper.IS_CHECKER = false;
        MockUserRoleHelper.IS_APPROVER = true;

        expected.setCorresponStatus(status);
        expected.setUpdatedBy(MockAbstractService.CURRENT_USER);
        MockAbstractDao.RET_UPDATE_CORRESPON = expected;

        MockAbstractDao.EDITMODE = MockAbstractDao.UPDATE_MODE;
        service.updateCorresponStatus(correspon, status);

        // closeの場合既読・未読状態は変わらない
        assertEquals(ReadStatus.READ, MockCorresponReadStatusService.SET_READ_STATUS);
    }

    /**
     * コレポン文書状態更新 System Adminが更新
     * @throws Exception
     */
    @Test
    public void testUpdateCorresponStatus04() throws Exception {
        Correspon correspon = createCorresponUpdate01();
        Correspon expected = createCorresponUpdate01();
        CorresponStatus status = CorresponStatus.CLOSED;

        MockAbstractDao.RET_FIND_BY_ID_CORRESPONGROUP = null;
        MockAbstractDao.RET_FIND_BY_ID.put("1", correspon);

        MockAbstractService.IS_SYSTEM_ADMIN = true;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        MockAbstractService.IS_GROUP_ADMIN = null;

        MockCorresponReadStatusService.SET_READ_STATUS = ReadStatus.READ ;

        // Preparer
        User currentUser = new User();
        currentUser.setEmpNo("currentUser");
        MockAbstractService.CURRENT_USER = currentUser;
        User createdBy = new User();
        createdBy.setEmpNo("createdBy");
        correspon.setCreatedBy(createdBy);
        expected.setCreatedBy(correspon.getCreatedBy());

        MockUserRoleHelper.IS_CHECKER = false;
        MockUserRoleHelper.IS_APPROVER = false;

        expected.setCorresponStatus(status);
        expected.setUpdatedBy(MockAbstractService.CURRENT_USER);
        MockAbstractDao.RET_UPDATE_CORRESPON = expected;

        MockAbstractDao.EDITMODE = MockAbstractDao.UPDATE_MODE;
        service.updateCorresponStatus(correspon, status);

        // closeの場合既読・未読状態は変わらない
        assertEquals(ReadStatus.READ, MockCorresponReadStatusService.SET_READ_STATUS);
    }

    /**
     * コレポン文書状態更新 Project Adminが更新
     * @throws Exception
     */
    @Test
    public void testUpdateCorresponStatus05() throws Exception {
        Correspon correspon = createCorresponUpdate01();
        Correspon expected = createCorresponUpdate01();
        CorresponStatus status = CorresponStatus.CLOSED;

        MockAbstractDao.RET_FIND_BY_ID_CORRESPONGROUP = null;
        MockAbstractDao.RET_FIND_BY_ID.put("1", correspon);

        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN = true;
        MockAbstractService.IS_GROUP_ADMIN = null;

        MockCorresponReadStatusService.SET_READ_STATUS = ReadStatus.READ ;

        // Preparer
        User currentUser = new User();
        currentUser.setEmpNo("currentUser");
        MockAbstractService.CURRENT_USER = currentUser;
        User createdBy = new User();
        createdBy.setEmpNo("createdBy");
        correspon.setCreatedBy(createdBy);
        expected.setCreatedBy(correspon.getCreatedBy());

        MockUserRoleHelper.IS_CHECKER = false;
        MockUserRoleHelper.IS_APPROVER = false;

        expected.setCorresponStatus(status);
        expected.setUpdatedBy(MockAbstractService.CURRENT_USER);
        MockAbstractDao.RET_UPDATE_CORRESPON = expected;

        MockAbstractDao.EDITMODE = MockAbstractDao.UPDATE_MODE;
        service.updateCorresponStatus(correspon, status);

        // closeの場合既読・未読状態は変わらない
        assertEquals(ReadStatus.READ, MockCorresponReadStatusService.SET_READ_STATUS);
    }

    /**
     * コレポン文書状態更新 Group Adminが更新
     * @throws Exception
     */
    @Test
    public void testUpdateCorresponStatus06() throws Exception {
        Correspon correspon = createCorresponUpdate01();
        Correspon expected = createCorresponUpdate01();
        CorresponStatus status = CorresponStatus.CLOSED;

        MockAbstractDao.RET_FIND_BY_ID_CORRESPONGROUP = null;
        MockAbstractDao.RET_FIND_BY_ID.put("1", correspon);

        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        MockAbstractService.IS_GROUP_ADMIN = new ArrayList<Long>();
        MockAbstractService.IS_GROUP_ADMIN.add(correspon.getFromCorresponGroup().getId());

        MockCorresponReadStatusService.SET_READ_STATUS = ReadStatus.READ ;

        // Preparer
        User currentUser = new User();
        currentUser.setEmpNo("currentUser");
        MockAbstractService.CURRENT_USER = currentUser;
        User createdBy = new User();
        createdBy.setEmpNo("createdBy");
        correspon.setCreatedBy(createdBy);
        expected.setCreatedBy(correspon.getCreatedBy());

        MockUserRoleHelper.IS_CHECKER = false;
        MockUserRoleHelper.IS_APPROVER = false;

        expected.setCorresponStatus(status);
        expected.setUpdatedBy(MockAbstractService.CURRENT_USER);
        MockAbstractDao.RET_UPDATE_CORRESPON = expected;

        MockAbstractDao.EDITMODE = MockAbstractDao.UPDATE_MODE;
        service.updateCorresponStatus(correspon, status);

        // closeの場合既読・未読状態は変わらない
        assertEquals(ReadStatus.READ, MockCorresponReadStatusService.SET_READ_STATUS);
    }

    /**
     * コレポン文書状態更新 権限なしで失敗
     * @throws Exception
     */
    @Test
    public void testUpdateCorresponStatus07() throws Exception {
        Correspon correspon = createCorresponUpdate01();
        Correspon expected = createCorresponUpdate01();
        CorresponStatus status = CorresponStatus.CLOSED;

        MockAbstractDao.RET_FIND_BY_ID_CORRESPONGROUP = null;
        MockAbstractDao.RET_FIND_BY_ID.put("1", correspon);

        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        MockAbstractService.IS_GROUP_ADMIN = null;

        // Preparer
        User currentUser = new User();
        currentUser.setEmpNo("currentUser");
        MockAbstractService.CURRENT_USER = currentUser;
        User createdBy = new User();
        createdBy.setEmpNo("createdBy");
        correspon.setCreatedBy(createdBy);
        expected.setCreatedBy(correspon.getCreatedBy());

        MockUserRoleHelper.IS_CHECKER = false;
        MockUserRoleHelper.IS_APPROVER = false;

        expected.setCorresponStatus(status);
        expected.setUpdatedBy(MockAbstractService.CURRENT_USER);
        MockAbstractDao.RET_UPDATE_CORRESPON = expected;

        MockAbstractDao.EDITMODE = MockAbstractDao.UPDATE_MODE;
        try {
            service.updateCorresponStatus(correspon, status);
            fail("例外が発生しませんでした");
        } catch (ServiceAbortException e) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT, e
                .getMessageCode());
        }
    }

    /**
     * コレポン文書状態更新 Preparerが更新
     * Canceledに更新
     * @throws Exception
     */
    @Test
    public void testUpdateCorresponStatus08() throws Exception {
        Correspon correspon = createCorresponUpdate01();
        Correspon expected = createCorresponUpdate01();
        CorresponStatus status = CorresponStatus.CANCELED;

        MockAbstractDao.RET_FIND_BY_ID_CORRESPONGROUP = null;
        MockAbstractDao.RET_FIND_BY_ID.put("1", correspon);

        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        MockAbstractService.IS_GROUP_ADMIN = null;

        MockCorresponReadStatusService.SET_READ_STATUS = ReadStatus.READ ;

        // Preparer
        User currentUser = new User();
        currentUser.setEmpNo("currentUser");
        MockAbstractService.CURRENT_USER = currentUser;
        correspon.setCreatedBy(MockAbstractService.CURRENT_USER);
        expected.setCreatedBy(correspon.getCreatedBy());

        MockUserRoleHelper.IS_CHECKER = false;
        MockUserRoleHelper.IS_APPROVER = false;

        expected.setCorresponStatus(status);
        expected.setUpdatedBy(MockAbstractService.CURRENT_USER);
        MockAbstractDao.RET_UPDATE_CORRESPON = expected;

        MockAbstractDao.EDITMODE = MockAbstractDao.UPDATE_MODE;
        service.updateCorresponStatus(correspon, status);

        assertEquals(correspon.getId(), MockCorresponReadStatusService.SET_ID);
        assertEquals(ReadStatus.NEW, MockCorresponReadStatusService.SET_READ_STATUS);
    }

    private Correspon createCorresponNew01() {
        Correspon c = new Correspon();

        // 新規
        c.setId(null);
        c.setCorresponNo("corresponNo01");

        // 現在のユーザー設定
        User user = new User();
        user.setEmpNo("testuser001");
        MockAbstractService.CURRENT_USER = user;

        // 文書状態
        c.setCorresponStatus(CorresponStatus.OPEN);

        // From①
        CorresponGroup cg = new CorresponGroup();
        cg.setId(64L);
        cg.setProjectId("PJtest01");
        cg.setName("CorresponGroup.setName()");

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
        MockAddressUserDao.RET_FIND_BY_ADDRESS_CORRESPON_GROUP_ID = auList;

        List<AddressCorresponGroup> acgList = new ArrayList<AddressCorresponGroup>();
        AddressCorresponGroup acg = null;
        acg = new AddressCorresponGroup();
        acg.setAddressType(AddressType.TO);
        acg.setId(1L);
        acgList.add(acg);
        acg = new AddressCorresponGroup();
        acg.setAddressType(AddressType.CC);
        acg.setId(2L);
        acgList.add(acg);
        c.setAddressCorresponGroups(acgList);
        MockUserDao.RET_FIND_BY_EMP_NO = new User();

        // To/Cc④
        MockAbstractService.CURRENT_PROJECT_ID = "PJtest01";
        MockAbstractService.IS_SYSTEM_ADMIN = true;
        List<Project> pjList = new ArrayList<Project>();
        Project pj = new Project();
        pj.setProjectId("PJtest01");
        pjList.add(pj);

        // Correspondence Type①
//        CorresponType ct = new CorresponType();
//        ct.setId(128L);
//        ct.setProjectId("PJtest01");
//        c.setCorresponType(ct);
        setUpCorresponType(c);

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
        MockProjectCustomFieldDao.RET.put(1L, 9801L);
        MockProjectCustomFieldDao.RET.put(2L, 9802L);
        MockProjectCustomFieldDao.RET.put(3L, 9803L);
        MockProjectCustomFieldDao.RET.put(4L, 9804L);
        MockProjectCustomFieldDao.RET.put(5L, 9805L);
        MockProjectCustomFieldDao.RET.put(6L, 9806L);
        MockProjectCustomFieldDao.RET.put(7L, 9807L);
        MockProjectCustomFieldDao.RET.put(8L, 9808L);
        MockProjectCustomFieldDao.RET.put(9L, 9809L);
        MockProjectCustomFieldDao.RET.put(10L, 9810L);

        // 返信①
        c.setParentCorresponId(2L);

        // 返信②
        Correspon sourceC = new Correspon();
        sourceC.setProjectId("PJtest01");

        // 返信③
        sourceC.setWorkflowStatus(WorkflowStatus.ISSUED);

        return c;
    }

    private Correspon createCorresponUpdate01() {
        Correspon c = createCorresponNew01();
        // 文書状態を取り消し
        c.setCorresponStatus(null);
        // 返信を取り消し
        c.setParentCorresponId(null);

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
        c.setCustomField1Label("label1");
        c.setCustomField2Label("label2");
        c.setCustomField3Label("label3");
        c.setCustomField4Label("label4");
        c.setCustomField5Label("label5");
        c.setCustomField6Label("label6");
        c.setCustomField7Label("label7");
        c.setCustomField8Label("label8");
        c.setCustomField9Label("label9");
        c.setCustomField10Label("label10");
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

//        CorresponCustomField ccf = null;
//        ccf = new CorresponCustomField();
//        ccf.setProjectCustomFieldId(20001L);
//        ccf = new CorresponCustomField();
//        ccf.setProjectCustomFieldId(20002L);
//        ccf = new CorresponCustomField();
//        ccf.setProjectCustomFieldId(20003L);
//        ccf = new CorresponCustomField();
//        ccf.setProjectCustomFieldId(20004L);
//        ccf = new CorresponCustomField();
//        ccf.setProjectCustomFieldId(20005L);
//        ccf = new CorresponCustomField();
//        ccf.setProjectCustomFieldId(20006L);
//        ccf = new CorresponCustomField();
//        ccf.setProjectCustomFieldId(20007L);
//        ccf = new CorresponCustomField();
//        ccf.setProjectCustomFieldId(20008L);
//        ccf = new CorresponCustomField();
//        ccf.setProjectCustomFieldId(20009L);
//        ccf = new CorresponCustomField();
//        ccf.setProjectCustomFieldId(20010L);

        // Field②③
        ProjectCustomField pcf = null;
        pcf = new ProjectCustomField();
        pcf.setProjectId(c.getProjectId());

        // 更新の際①
        c.setId(1024L);
        Correspon oldCorrespon = createCorresponNew01();

        // 更新の際②
        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        MockAbstractService.IS_GROUP_ADMIN = null;
        c.setWorkflowStatus(WorkflowStatus.DRAFT);

        // 文書状態①
        // 文書状態②
        oldCorrespon.setCorresponStatus(CorresponStatus.OPEN);
        oldCorrespon.setWorkflowStatus(WorkflowStatus.DRAFT);
        c.setCorresponStatus(CorresponStatus.OPEN);

        // プロジェクトチェック
        c.setProjectId("PJtest01");
        MockAbstractService.CURRENT_PROJECT_ID = "PJtest01";

        // 承認状態チェック
        c.setProjectId("PJtest01");
        MockAbstractService.CURRENT_PROJECT_ID = "PJtest01";
        c.setWorkflowStatus(WorkflowStatus.DRAFT);
        oldCorrespon.setWorkflowStatus(WorkflowStatus.DRAFT);

        // 文書更新チェック(Prepare)
        User createdBy = new User();
        c.setCreatedBy(createdBy);

        return c;
    }

    /**
     * {@link CorresponServiceImpl#savePartial}のテストケース. SystemAdmin
     * @throws Exception
     */
    @Test
    public void testSavePartialSystemAdmin() throws Exception {
        User loginUser = new User();
        loginUser.setEmpNo("ZZA05");
        MockAbstractService.CURRENT_USER = loginUser;

        MockAbstractService.IS_SYSTEM_ADMIN = true;

        Correspon correspon = createCorresponSavePatial();
        // テスト実行
        service.savePartial(correspon);

        Correspon actual = MockCorresponDao.RET_UPDATE_CORRESPON;

        assertEquals(correspon.getId(), actual.getId());
        assertEquals(correspon.getReplyRequired(), actual.getReplyRequired());
        assertEquals(correspon.getDeadlineForReply(), actual.getDeadlineForReply());
        assertEquals(loginUser.getEmpNo(), actual.getUpdatedBy().getEmpNo());
        assertEquals(correspon.getVersionNo(), actual.getVersionNo());
        assertNull(actual.getCorresponNo());
        assertNull(actual.getProjectId());
        assertNull(actual.getFromCorresponGroup());
        assertNull(actual.getCorresponType());
        assertNull(actual.getSubject());
        assertNull(actual.getBody());
        assertNull(actual.getIssuedAt());
        assertNull(actual.getIssuedBy());
        assertNull(actual.getWorkflowStatus());
        assertNull(actual.getCreatedBy());
        assertNull(actual.getCreatedAt());
        assertNull(actual.getDeleteNo());

        assertEquals(correspon.getId(), MockCorresponReadStatusService.SET_ID);
        assertEquals(ReadStatus.NEW, MockCorresponReadStatusService.SET_READ_STATUS);
}

    /**
     * {@link CorresponServiceImpl#savePartial}のテストケース. ProjectAdmin
     * @throws Exception
     */
    @Test
    public void testSavePartialProjectAdmin() throws Exception {
        User loginUser = new User();
        loginUser.setEmpNo("ZZA05");
        MockAbstractService.CURRENT_USER = loginUser;

        MockAbstractService.IS_PROJECT_ADMIN = true;

        Correspon correspon = createCorresponSavePatial();
        // テスト実行
        service.savePartial(correspon);

        Correspon actual = MockCorresponDao.RET_UPDATE_CORRESPON;

        assertEquals(correspon.getId(), actual.getId());
        assertEquals(correspon.getReplyRequired(), actual.getReplyRequired());
        assertEquals(correspon.getDeadlineForReply(), actual.getDeadlineForReply());
        assertEquals(loginUser.getEmpNo(), actual.getUpdatedBy().getEmpNo());
        assertEquals(correspon.getVersionNo(), actual.getVersionNo());
        assertNull(actual.getCorresponNo());
        assertNull(actual.getProjectId());
        assertNull(actual.getFromCorresponGroup());
        assertNull(actual.getCorresponType());
        assertNull(actual.getSubject());
        assertNull(actual.getBody());
        assertNull(actual.getIssuedAt());
        assertNull(actual.getIssuedBy());
        assertNull(actual.getWorkflowStatus());
        assertNull(actual.getCreatedBy());
        assertNull(actual.getCreatedAt());
        assertNull(actual.getDeleteNo());

        assertEquals(correspon.getId(), MockCorresponReadStatusService.SET_ID);
        assertEquals(ReadStatus.NEW, MockCorresponReadStatusService.SET_READ_STATUS);
    }

    /**
     * {@link CorresponServiceImpl#savePartial}のテストケース. GroupAdmin
     * @throws Exception
     */
    @Test
    public void testSavePartialGroupAdmin() throws Exception {
        User loginUser = new User();
        loginUser.setEmpNo("ZZA05");
        MockAbstractService.CURRENT_USER = loginUser;


        Correspon correspon = createCorresponSavePatial();
        MockAbstractService.IS_GROUP_ADMIN.add(correspon.getFromCorresponGroup().getId());
        // テスト実行
        service.savePartial(correspon);

        Correspon actual = MockCorresponDao.RET_UPDATE_CORRESPON;

        assertEquals(correspon.getId(), actual.getId());
        assertEquals(correspon.getReplyRequired(), actual.getReplyRequired());
        assertEquals(correspon.getDeadlineForReply(), actual.getDeadlineForReply());
        assertEquals(loginUser.getEmpNo(), actual.getUpdatedBy().getEmpNo());
        assertEquals(correspon.getVersionNo(), actual.getVersionNo());
        assertNull(actual.getCorresponNo());
        assertNull(actual.getProjectId());
        assertNull(actual.getFromCorresponGroup());
        assertNull(actual.getCorresponType());
        assertNull(actual.getSubject());
        assertNull(actual.getBody());
        assertNull(actual.getIssuedAt());
        assertNull(actual.getIssuedBy());
        assertNull(actual.getWorkflowStatus());
        assertNull(actual.getCreatedBy());
        assertNull(actual.getCreatedAt());
        assertNull(actual.getDeleteNo());

        assertEquals(correspon.getId(), MockCorresponReadStatusService.SET_ID);
        assertEquals(ReadStatus.NEW, MockCorresponReadStatusService.SET_READ_STATUS);
    }

    /**
     * {@link CorresponServiceImpl#savePartial}のテストケース. Preperer
     * @throws Exception
     */
    @Test
    public void testSavePartialPreperer() throws Exception {
        User loginUser = new User();
        loginUser.setEmpNo("ZZA05");
        MockAbstractService.CURRENT_USER = loginUser;

        Correspon correspon = createCorresponSavePatial();

        // Preperer
        correspon.setCreatedBy(loginUser);
        // テスト実行
        service.savePartial(correspon);

        Correspon actual = MockCorresponDao.RET_UPDATE_CORRESPON;

        assertEquals(correspon.getId(), actual.getId());
        assertEquals(correspon.getReplyRequired(), actual.getReplyRequired());
        assertEquals(correspon.getDeadlineForReply(), actual.getDeadlineForReply());
        assertEquals(loginUser.getEmpNo(), actual.getUpdatedBy().getEmpNo());
        assertEquals(correspon.getVersionNo(), actual.getVersionNo());
        assertNull(actual.getCorresponNo());
        assertNull(actual.getProjectId());
        assertNull(actual.getFromCorresponGroup());
        assertNull(actual.getCorresponType());
        assertNull(actual.getSubject());
        assertNull(actual.getBody());
        assertNull(actual.getIssuedAt());
        assertNull(actual.getIssuedBy());
        assertNull(actual.getWorkflowStatus());
        assertNull(actual.getCreatedBy());
        assertNull(actual.getCreatedAt());
        assertNull(actual.getDeleteNo());

        assertEquals(correspon.getId(), MockCorresponReadStatusService.SET_ID);
        assertEquals(ReadStatus.NEW, MockCorresponReadStatusService.SET_READ_STATUS);
    }

    /**
     * {@link CorresponServiceImpl#savePartial}のテストケース. Checker
     * @throws Exception
     */
    @Test
    public void testSavePartialChecker() throws Exception {
        User loginUser = new User();
        // Checker
        loginUser.setEmpNo("ZZA02");
        MockAbstractService.CURRENT_USER = loginUser;

        MockAbstractService.IS_SYSTEM_ADMIN = true;

        Correspon correspon = createCorresponSavePatial();
        // テスト実行
        service.savePartial(correspon);

        Correspon actual = MockCorresponDao.RET_UPDATE_CORRESPON;

        assertEquals(correspon.getId(), actual.getId());
        assertEquals(correspon.getReplyRequired(), actual.getReplyRequired());
        assertEquals(correspon.getDeadlineForReply(), actual.getDeadlineForReply());
        assertEquals(loginUser.getEmpNo(), actual.getUpdatedBy().getEmpNo());
        assertEquals(correspon.getVersionNo(), actual.getVersionNo());
        assertNull(actual.getCorresponNo());
        assertNull(actual.getProjectId());
        assertNull(actual.getFromCorresponGroup());
        assertNull(actual.getCorresponType());
        assertNull(actual.getSubject());
        assertNull(actual.getBody());
        assertNull(actual.getIssuedAt());
        assertNull(actual.getIssuedBy());
        assertNull(actual.getWorkflowStatus());
        assertNull(actual.getCreatedBy());
        assertNull(actual.getCreatedAt());
        assertNull(actual.getDeleteNo());

        assertEquals(correspon.getId(), MockCorresponReadStatusService.SET_ID);
        assertEquals(ReadStatus.NEW, MockCorresponReadStatusService.SET_READ_STATUS);
    }

    /**
     * {@link CorresponServiceImpl#savePartial}のテストケース. Approver
     * @throws Exception
     */
    @Test
    public void testSavePartialApprover() throws Exception {
        User loginUser = new User();
        // Approver
        loginUser.setEmpNo("ZZA04");
        MockAbstractService.CURRENT_USER = loginUser;

        MockAbstractService.IS_SYSTEM_ADMIN = true;

        Correspon correspon = createCorresponSavePatial();
        // テスト実行
        service.savePartial(correspon);

        Correspon actual = MockCorresponDao.RET_UPDATE_CORRESPON;

        assertEquals(correspon.getId(), actual.getId());
        assertEquals(correspon.getReplyRequired(), actual.getReplyRequired());
        assertEquals(correspon.getDeadlineForReply(), actual.getDeadlineForReply());
        assertEquals(loginUser.getEmpNo(), actual.getUpdatedBy().getEmpNo());
        assertEquals(correspon.getVersionNo(), actual.getVersionNo());
        assertNull(actual.getCorresponNo());
        assertNull(actual.getProjectId());
        assertNull(actual.getFromCorresponGroup());
        assertNull(actual.getCorresponType());
        assertNull(actual.getSubject());
        assertNull(actual.getBody());
        assertNull(actual.getIssuedAt());
        assertNull(actual.getIssuedBy());
        assertNull(actual.getWorkflowStatus());
        assertNull(actual.getCreatedBy());
        assertNull(actual.getCreatedAt());
        assertNull(actual.getDeleteNo());

        assertEquals(correspon.getId(), MockCorresponReadStatusService.SET_ID);
        assertEquals(ReadStatus.NEW, MockCorresponReadStatusService.SET_READ_STATUS);
    }

    /**
     * {@link CorresponServiceImpl#savePartial}のテストケース. 権限がない
     * @throws Exception
     */
    @Test
    public void testSavePartialInvalidPermission() throws Exception {
        User loginUser = new User();
        loginUser.setEmpNo("ZZA05");
        MockAbstractService.CURRENT_USER = loginUser;

        Correspon correspon = createCorresponSavePatial();

        try {
            // テスト実行
            service.savePartial(correspon);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT,
                actual.getMessageCode());
        }
    }

    /**
     * {@link CorresponServiceImpl#savePartial}のテストケース. 発行状態がISSUEDではない
     * @throws Exception
     */
    @Test
    public void testSavePartialInvalidWorkflowStatus() throws Exception {
        User loginUser = new User();
        loginUser.setEmpNo("ZZA05");
        MockAbstractService.CURRENT_USER = loginUser;

        MockAbstractService.IS_SYSTEM_ADMIN = true;

        Correspon correspon = createCorresponSavePatial();
        // 発行状態がISSUEDではない
        correspon.setWorkflowStatus(WorkflowStatus.REQUEST_FOR_APPROVAL);
        try {
            // テスト実行
            service.savePartial(correspon);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CORRESPON_STATUS_INVALID,
                actual.getMessageCode());
        }
    }

    /**
     * {@link CorresponServiceImpl#savePartial}のテストケース. 文書状態がCANCELED
     * @throws Exception
     */
    @Test
    public void testSavePartialInvalidCorresponStatus() throws Exception {
        User loginUser = new User();
        loginUser.setEmpNo("ZZA05");
        MockAbstractService.CURRENT_USER = loginUser;

        MockAbstractService.IS_SYSTEM_ADMIN = true;

        Correspon correspon = createCorresponSavePatial();
        // 文書状態がCANSELED
        correspon.setCorresponStatus(CorresponStatus.CANCELED);
        try {
            // テスト実行
            service.savePartial(correspon);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CORREPON_CANCELED, actual
                .getMessageCode());
        }
    }

    /**
     * {@link CorresponServiceImpl#savePartial}のテストケース. バージョンナンバーが違う
     * @throws Exception
     */
    @Test
    public void testSavePartialStaleRecordException() throws Exception {
        User loginUser = new User();
        loginUser.setEmpNo("ZZA05");
        MockAbstractService.CURRENT_USER = loginUser;

        MockAbstractService.IS_SYSTEM_ADMIN = true;
        MockCorresponDao.STALE_RECORD_EXCEPTION = true;

        Correspon correspon = createCorresponSavePatial();
        try {
            // テスト実行
            service.savePartial(correspon);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CORRESPON_ALREADY_UPDATED,
                actual.getMessageCode());
        }
    }

    /**
     * テスト用コレポン文書の作成
     */
    private Correspon createCorresponSavePatial() {
        Correspon c = new Correspon();
        c.setId(1L);
        c.setCorresponNo("YOC:OT:BUILDING-00001");
        CorresponGroup from = new CorresponGroup();
        from.setId(new Long(1L));
        from.setName("YOC:BUILDING");
        c.setFromCorresponGroup(from);
        c.setSubject("Mock");
        CorresponType ct = new CorresponType();
        ct.setId(new Long(1L));
        ct.setName("Request");
        c.setCorresponType(ct);
        c.setWorkflowStatus(WorkflowStatus.ISSUED);
        c.setCreatedAt(new GregorianCalendar(2009, 3, 1, 1, 1, 1).getTime());
        c.setUpdatedAt(new GregorianCalendar(2009, 3, 10, 10, 10, 10).getTime());
        c.setCreatedBy(user);
        c.setUpdatedBy(user);
        c.setDeadlineForReply(new GregorianCalendar(2009, 4, 1).getTime());
        c.setReplyRequired(ReplyRequired.YES);
        c.setCorresponStatus(CorresponStatus.OPEN);

        List<Workflow> workflows = new ArrayList<Workflow>();
        Workflow workflow = new Workflow();
        User user = new User();

        workflow.setId(1L);
        workflow.setWorkflowProcessStatus(WorkflowProcessStatus.CHECKED);
        workflow.setWorkflowNo(1L);
        workflow.setWorkflowType(WorkflowType.CHECKER);
        user.setEmpNo("ZZA02");
        workflow.setUser(user);
        workflows.add(workflow);

        workflow = new Workflow();
        user = new User();

        workflow.setId(2L);
        workflow.setWorkflowProcessStatus(WorkflowProcessStatus.CHECKED);
        workflow.setWorkflowNo(2L);
        workflow.setWorkflowType(WorkflowType.CHECKER);
        user.setEmpNo("ZZA03");
        workflow.setUser(user);
        workflows.add(workflow);

        workflow.setId(3L);
        workflow.setWorkflowProcessStatus(WorkflowProcessStatus.APPROVED);
        workflow.setWorkflowNo(3L);
        workflow.setWorkflowType(WorkflowType.APPROVER);
        user.setEmpNo("ZZA04");
        workflow.setUser(user);
        workflows.add(workflow);

        c.setWorkflows(workflows);

        return c;
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

        static List<Attachment> RET_CREATE_ATTACHMENT;
        static List<CorresponCustomField> RET_CREATE_CORRESPONCUSTOMFIELD;
        static List<AddressUser> RET_CREATE_ADDRESSUSER;
        static List<AddressCorresponGroup> RET_CREATE_ADDRESSCORRESPONGROUP;
        static List<PersonInCharge> RET_CREATE_PERSONINCHARGE;
        static CorresponHierarchy RET_CREATE_HIERARCHY;

        static int EDITMODE;
        static final int UPDATE_MODE = 1;
        static final int DEFAULT_MODE = 0;

        static List<CorresponGroup> RET_FIND_BY_ID_CORRESPONGROUP;
        static boolean STALE_RECORD_EXCEPTION;

        public MockAbstractDao() {

        }

        public MockAbstractDao(String namespace) {

        }

        public Integer update(T entity) throws KeyDuplicateException, StaleRecordException {
            if (EXCEPTION) {
                throw new KeyDuplicateException();
            }
            if (STALE_RECORD_EXCEPTION) {
                throw new StaleRecordException();
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
        static int createPersonInChargeCount = 0;

        public Long create(T entity) throws KeyDuplicateException {
            if (entity instanceof Correspon) {

                checkCorresponFields(RET_UPDATE_CORRESPON, (Correspon) entity);
                ObjectName = "Correspon";

            }

            if (entity instanceof Attachment) {
                if (!StringUtils.equals(ObjectName, "Attachment")) {
                    countElement = 0;
                }
                assertEquals(RET_CREATE_ATTACHMENT.get(countElement).getCorresponId(),
                    ((Attachment) entity).getCorresponId());
                // assertEquals(RET_CREATE_ATTACHMENT.get(countElement).getFileId(),
                // ((Attachment) entity).getFileId());
                assertEquals(RET_CREATE_ATTACHMENT.get(countElement).getFileName(),
                    ((Attachment) entity).getFileName());
                assertEquals(RET_CREATE_ATTACHMENT.get(countElement).getCreatedBy().getEmpNo(),
                    ((Attachment) entity).getCreatedBy().getEmpNo());
                assertEquals(RET_CREATE_ATTACHMENT.get(countElement).getCreatedBy().getNameE(),
                    ((Attachment) entity).getCreatedBy().getNameE());
                assertEquals(RET_CREATE_ATTACHMENT.get(countElement).getUpdatedBy().getEmpNo(),
                    ((Attachment) entity).getUpdatedBy().getEmpNo());
                assertEquals(RET_CREATE_ATTACHMENT.get(countElement).getUpdatedBy().getNameE(),
                    ((Attachment) entity).getUpdatedBy().getNameE());
                countElement++;
                ObjectName = "Attachment";
            }

            if (entity instanceof CorresponCustomField) {
                if (!StringUtils.equals(ObjectName, "CorresponCustomField")) {
                    countElement = 0;
                }
                assertEquals(RET_CREATE_CORRESPONCUSTOMFIELD.get(countElement).getCorresponId(),
                    ((CorresponCustomField) entity).getCorresponId());
                assertEquals(RET_CREATE_CORRESPONCUSTOMFIELD.get(countElement)
                    .getProjectCustomFieldId(), ((CorresponCustomField) entity)
                    .getProjectCustomFieldId());
                assertEquals(RET_CREATE_CORRESPONCUSTOMFIELD.get(countElement).getValue(),
                    ((CorresponCustomField) entity).getValue());
                assertEquals(RET_CREATE_CORRESPONCUSTOMFIELD.get(countElement).getCreatedBy()
                    .getEmpNo(), ((CorresponCustomField) entity).getCreatedBy().getEmpNo());
                assertEquals(RET_CREATE_CORRESPONCUSTOMFIELD.get(countElement).getCreatedBy()
                    .getNameE(), ((CorresponCustomField) entity).getCreatedBy().getNameE());
                assertEquals(RET_CREATE_CORRESPONCUSTOMFIELD.get(countElement).getUpdatedBy()
                    .getEmpNo(), ((CorresponCustomField) entity).getUpdatedBy().getEmpNo());
                assertEquals(RET_CREATE_CORRESPONCUSTOMFIELD.get(countElement).getUpdatedBy()
                    .getNameE(), ((CorresponCustomField) entity).getUpdatedBy().getNameE());
                countElement++;
                ObjectName = "CorresponCustomField";
            }

            if (entity instanceof AddressUser) {
                if (!StringUtils.equals(ObjectName, "AddressUser")) {
                    countElement = 0;
                }
                assertEquals(RET_CREATE_ADDRESSUSER.get(countElement).getAddressCorresponGroupId(),
                    ((AddressUser) entity).getAddressCorresponGroupId());
                assertEquals(RET_CREATE_ADDRESSUSER.get(countElement).getUser().getEmpNo(),
                    ((AddressUser) entity).getUser().getEmpNo());
                assertEquals(RET_CREATE_ADDRESSUSER.get(countElement).getAddressUserType(),
                    ((AddressUser) entity).getAddressUserType());
                assertEquals(RET_CREATE_ADDRESSUSER.get(countElement).getCreatedBy().getEmpNo(),
                    ((AddressUser) entity).getCreatedBy().getEmpNo());
                assertEquals(RET_CREATE_ADDRESSUSER.get(countElement).getCreatedBy().getNameE(),
                    ((AddressUser) entity).getCreatedBy().getNameE());
                assertEquals(RET_CREATE_ADDRESSUSER.get(countElement).getUpdatedBy().getEmpNo(),
                    ((AddressUser) entity).getUpdatedBy().getEmpNo());
                assertEquals(RET_CREATE_ADDRESSUSER.get(countElement).getUpdatedBy().getNameE(),
                    ((AddressUser) entity).getUpdatedBy().getNameE());
                countElement++;
                ObjectName = "AddressUser";
            }

            if (entity instanceof AddressCorresponGroup) {
                if (!StringUtils.equals(ObjectName, "AddressCorresponGroup")) {
                    countElement = 0;
                }
                assertEquals(RET_CREATE_ADDRESSCORRESPONGROUP.get(countElement).getCorresponId(),
                    ((AddressCorresponGroup) entity).getCorresponId());
                assertEquals(RET_CREATE_ADDRESSCORRESPONGROUP.get(countElement).getCorresponGroup()
                    .getId(), ((AddressCorresponGroup) entity).getCorresponGroup().getId());
                assertEquals(RET_CREATE_ADDRESSCORRESPONGROUP.get(countElement).getAddressType(),
                    ((AddressCorresponGroup) entity).getAddressType());
                assertEquals(RET_CREATE_ADDRESSCORRESPONGROUP.get(countElement).getCreatedBy()
                    .getEmpNo(), ((AddressCorresponGroup) entity).getCreatedBy().getEmpNo());
                assertEquals(RET_CREATE_ADDRESSCORRESPONGROUP.get(countElement).getCreatedBy()
                    .getNameE(), ((AddressCorresponGroup) entity).getCreatedBy().getNameE());
                assertEquals(RET_CREATE_ADDRESSCORRESPONGROUP.get(countElement).getUpdatedBy()
                    .getEmpNo(), ((AddressCorresponGroup) entity).getUpdatedBy().getEmpNo());
                assertEquals(RET_CREATE_ADDRESSCORRESPONGROUP.get(countElement).getUpdatedBy()
                    .getNameE(), ((AddressCorresponGroup) entity).getUpdatedBy().getNameE());
                countElement++;
                ObjectName = "AddressCorresponGroup";
            }

            if (entity instanceof PersonInCharge) {
                createPersonInChargeCount++;
                if (!StringUtils.equals(ObjectName, "PersonInCharge")) {
                    countElement = 0;
                }
                assertEquals(RET_CREATE_PERSONINCHARGE.get(countElement).getAddressUserId(),
                    ((PersonInCharge) entity).getAddressUserId());
                assertEquals(RET_CREATE_PERSONINCHARGE.get(countElement).getUser().getEmpNo(),
                    ((PersonInCharge) entity).getUser().getEmpNo());
                assertEquals(RET_CREATE_PERSONINCHARGE.get(countElement).getCreatedBy().getEmpNo(),
                    ((PersonInCharge) entity).getCreatedBy().getEmpNo());
                assertEquals(RET_CREATE_PERSONINCHARGE.get(countElement).getCreatedBy().getNameE(),
                    ((PersonInCharge) entity).getCreatedBy().getNameE());
                assertEquals(RET_CREATE_PERSONINCHARGE.get(countElement).getUpdatedBy().getEmpNo(),
                    ((PersonInCharge) entity).getUpdatedBy().getEmpNo());
                assertEquals(RET_CREATE_PERSONINCHARGE.get(countElement).getUpdatedBy().getNameE(),
                    ((PersonInCharge) entity).getUpdatedBy().getNameE());
                countElement++;
                ObjectName = "PersonInCharge";
            }

            if (entity instanceof CorresponHierarchy) {

                assertEquals(RET_CREATE_HIERARCHY.getParentCorresponId(),
                    ((CorresponHierarchy) entity).getParentCorresponId());
                assertEquals(RET_CREATE_HIERARCHY.getChildCorresponId(),
                    ((CorresponHierarchy) entity).getChildCorresponId());
                assertEquals(RET_CREATE_HIERARCHY.getCreatedBy().getEmpNo(),
                    ((CorresponHierarchy) entity).getCreatedBy().getEmpNo());
                assertEquals(RET_CREATE_HIERARCHY.getCreatedBy().getNameE(),
                    ((CorresponHierarchy) entity).getCreatedBy().getNameE());
                assertEquals(RET_CREATE_HIERARCHY.getUpdatedBy().getEmpNo(),
                    ((CorresponHierarchy) entity).getUpdatedBy().getEmpNo());
                assertEquals(RET_CREATE_HIERARCHY.getUpdatedBy().getNameE(),
                    ((CorresponHierarchy) entity).getUpdatedBy().getNameE());
                ObjectName = "CorresponHierarchy";

            }

            RET_CREATE = new Long(99);
            return RET_CREATE;
        }

        public Integer delete(T entity) throws KeyDuplicateException, StaleRecordException {

            if (EDITMODE == UPDATE_MODE) {
                if (entity instanceof Attachment) {
                    assertEquals(RET_UPDATE_ATTACHMENT.get(countElement).getId(),
                        ((Attachment) entity).getId());
                    assertEquals(RET_UPDATE_ATTACHMENT.get(countElement).getUpdatedBy().getEmpNo(),
                        ((Attachment) entity).getUpdatedBy().getEmpNo());
                    assertEquals(RET_UPDATE_ATTACHMENT.get(countElement).getUpdatedBy().getNameE(),
                        ((Attachment) entity).getUpdatedBy().getNameE());
                    countElement++;
                }

            } else {

                if (entity instanceof Correspon) {
                    assertEquals(RET_DELETE_CORRESPON.getId(), ((Correspon) entity).getId());
                    assertEquals(RET_DELETE_CORRESPON.getUpdatedBy().getEmpNo(),
                        ((Correspon) entity).getUpdatedBy().getEmpNo());
                    assertEquals(RET_DELETE_CORRESPON.getUpdatedBy().getNameE(),
                        ((Correspon) entity).getUpdatedBy().getNameE());
                }

            }

            return RET_DELETE;
        }

        static Map<String, Object> RET_FIND_BY_ID = new HashMap<String, Object>();
        static Map<String, Object> RET_FIND_BY_ID_ATTACHMENT = new HashMap<String, Object>();

        static boolean init = false;
        static int count = 0;

        @SuppressWarnings("unchecked")
        public T findById(Long id) throws RecordNotFoundException {
            try {
                if (RET_FIND_BY_ID_CORRESPONGROUP == null) {
                    count++;
                    if (RET_FIND_BY_ID_ATTACHMENT.get(String.valueOf(id)) == null) {
                        if (RET_FIND_BY_ID.get(String.valueOf(count)) == null) {
                            throw new RecordNotFoundException();
                        }
                        return (T) RET_FIND_BY_ID.get(String.valueOf(count));
                    }
                    return (T) RET_FIND_BY_ID_ATTACHMENT.get(String.valueOf(id));
                } else {
                    return (T) RET_FIND_BY_ID_CORRESPONGROUP.get(0);
                }
            }
            finally {
                if (init) {
                    count = 0;
                }
            }
        }

        private void checkCorresponFields(Correspon expectCorrespon, Correspon entryCorrespon) {

            assertEquals(expectCorrespon.getId(), entryCorrespon.getId());
            assertEquals(expectCorrespon.getCorresponNo(), entryCorrespon.getCorresponNo());
            assertEquals(expectCorrespon.getParentCorresponId(), entryCorrespon
                .getParentCorresponId());
            assertEquals(expectCorrespon.getProjectId(), entryCorrespon.getProjectId());
            assertEquals(expectCorrespon.getProjectNameE(), entryCorrespon.getProjectNameE());
            assertEquals(expectCorrespon.getFromCorresponGroup().getId(), entryCorrespon
                .getFromCorresponGroup().getId());
            assertEquals(expectCorrespon.getFromCorresponGroup().getName(), entryCorrespon
                .getFromCorresponGroup().getName());
            assertEquals(expectCorrespon.getCorresponType().getId(), entryCorrespon
                .getCorresponType().getId());
            assertEquals(expectCorrespon.getCorresponType().getName(), entryCorrespon
                .getCorresponType().getName());
            assertEquals(expectCorrespon.getSubject(), entryCorrespon.getSubject());
            assertEquals(expectCorrespon.getBody(), entryCorrespon.getBody());
            assertEquals(expectCorrespon.getIssuedAt(), entryCorrespon.getIssuedAt());
            assertEquals(expectCorrespon.getCorresponStatus().getValue(), entryCorrespon
                .getCorresponStatus().getValue());
            assertEquals(expectCorrespon.getDeadlineForReply(), entryCorrespon
                .getDeadlineForReply());
            if (expectCorrespon.getWorkflowStatus() == null) {
                assertNull(entryCorrespon.getWorkflowStatus());
            } else {
                assertEquals(expectCorrespon.getWorkflowStatus().getValue(), entryCorrespon
                    .getWorkflowStatus().getValue());
            }
            assertEquals(expectCorrespon.getCustomField1Id(), entryCorrespon.getCustomField1Id());
            assertEquals(expectCorrespon.getCustomField1Label(), entryCorrespon
                .getCustomField1Label());
            assertEquals(expectCorrespon.getCustomField1Value(), entryCorrespon
                .getCustomField1Value());
            assertEquals(expectCorrespon.getCustomField2Id(), entryCorrespon.getCustomField2Id());
            assertEquals(expectCorrespon.getCustomField2Label(), entryCorrespon
                .getCustomField2Label());
            assertEquals(expectCorrespon.getCustomField2Value(), entryCorrespon
                .getCustomField2Value());
            assertEquals(expectCorrespon.getCustomField3Id(), entryCorrespon.getCustomField3Id());
            assertEquals(expectCorrespon.getCustomField3Label(), entryCorrespon
                .getCustomField3Label());
            assertEquals(expectCorrespon.getCustomField3Value(), entryCorrespon
                .getCustomField3Value());
            assertEquals(expectCorrespon.getCustomField4Id(), entryCorrespon.getCustomField4Id());
            assertEquals(expectCorrespon.getCustomField4Label(), entryCorrespon
                .getCustomField4Label());
            assertEquals(expectCorrespon.getCustomField4Value(), entryCorrespon
                .getCustomField4Value());
            assertEquals(expectCorrespon.getCustomField5Id(), entryCorrespon.getCustomField5Id());
            assertEquals(expectCorrespon.getCustomField5Label(), entryCorrespon
                .getCustomField5Label());
            assertEquals(expectCorrespon.getCustomField5Value(), entryCorrespon
                .getCustomField5Value());
            assertEquals(expectCorrespon.getCustomField6Id(), entryCorrespon.getCustomField6Id());
            assertEquals(expectCorrespon.getCustomField6Label(), entryCorrespon
                .getCustomField6Label());
            assertEquals(expectCorrespon.getCustomField6Value(), entryCorrespon
                .getCustomField6Value());
            assertEquals(expectCorrespon.getCustomField7Id(), entryCorrespon.getCustomField7Id());
            assertEquals(expectCorrespon.getCustomField7Label(), entryCorrespon
                .getCustomField7Label());
            assertEquals(expectCorrespon.getCustomField7Value(), entryCorrespon
                .getCustomField7Value());
            assertEquals(expectCorrespon.getCustomField8Id(), entryCorrespon.getCustomField8Id());
            assertEquals(expectCorrespon.getCustomField8Label(), entryCorrespon
                .getCustomField8Label());
            assertEquals(expectCorrespon.getCustomField8Value(), entryCorrespon
                .getCustomField8Value());
            assertEquals(expectCorrespon.getCustomField9Id(), entryCorrespon.getCustomField9Id());
            assertEquals(expectCorrespon.getCustomField9Label(), entryCorrespon
                .getCustomField9Label());
            assertEquals(expectCorrespon.getCustomField9Value(), entryCorrespon
                .getCustomField9Value());
            assertEquals(expectCorrespon.getCustomField10Id(), entryCorrespon.getCustomField10Id());
            assertEquals(expectCorrespon.getCustomField10Label(), entryCorrespon
                .getCustomField10Label());
            assertEquals(expectCorrespon.getCustomField10Value(), entryCorrespon
                .getCustomField10Value());
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
                assertEquals(expectCorrespon.getCreatedBy().getEmpNo(), entryCorrespon
                    .getCreatedBy().getEmpNo());
                assertEquals(expectCorrespon.getCreatedBy().getNameE(), entryCorrespon
                    .getCreatedBy().getNameE());
            }
            assertEquals(expectCorrespon.getCreatedAt(), entryCorrespon.getCreatedAt());
            assertEquals(expectCorrespon.getUpdatedBy().getEmpNo(), entryCorrespon.getUpdatedBy()
                .getEmpNo());
            assertEquals(expectCorrespon.getUpdatedBy().getNameE(), entryCorrespon.getUpdatedBy()
                .getNameE());
            assertEquals(expectCorrespon.getUpdatedAt(), entryCorrespon.getUpdatedAt());
            assertEquals(expectCorrespon.getVersionNo(), entryCorrespon.getVersionNo());
            assertEquals(expectCorrespon.getDeleteNo(), entryCorrespon.getDeleteNo());

        }

        private void checkWorkflowFields(Workflow expectWorkflow, Workflow entryWorkflow) {

            assertEquals(expectWorkflow.getId(), entryWorkflow.getId());
            assertEquals(expectWorkflow.getCorresponId(), entryWorkflow.getCorresponId());
            assertEquals(expectWorkflow.getUser(), entryWorkflow.getUser());
            assertEquals(expectWorkflow.getWorkflowType(), entryWorkflow.getWorkflowType());
            assertEquals(expectWorkflow.getWorkflowNo(), entryWorkflow.getWorkflowNo());
            assertEquals(expectWorkflow.getWorkflowProcessStatus().getLabel(), entryWorkflow
                .getWorkflowProcessStatus().getLabel());
            assertEquals(expectWorkflow.getWorkflowProcessStatus().getValue(), entryWorkflow
                .getWorkflowProcessStatus().getValue());
            assertEquals(expectWorkflow.getCommentOn(), entryWorkflow.getCommentOn());
            if (expectWorkflow.getFinishedBy() == null) {
                assertNull(entryWorkflow.getFinishedBy());
            } else {
                assertEquals(expectWorkflow.getFinishedBy().getEmpNo(), entryWorkflow
                    .getFinishedBy().getEmpNo());
                assertEquals(expectWorkflow.getFinishedBy().getNameE(), entryWorkflow
                    .getFinishedBy().getNameE());
                assertEquals(expectWorkflow.getFinishedAt(), entryWorkflow.getFinishedAt());
            }
            assertNull(entryWorkflow.getCreatedBy());
            assertNull(entryWorkflow.getCreatedAt());
            assertEquals(expectWorkflow.getUpdatedBy().getEmpNo(), entryWorkflow.getUpdatedBy()
                .getEmpNo());
            assertEquals(expectWorkflow.getUpdatedBy().getNameE(), entryWorkflow.getUpdatedBy()
                .getNameE());
            assertEquals(expectWorkflow.getUpdatedAt(), entryWorkflow.getUpdatedAt());
            assertEquals(expectWorkflow.getVersionNo(), entryWorkflow.getVersionNo());
            assertEquals(expectWorkflow.getDeleteNo(), entryWorkflow.getDeleteNo());

        }

    }

    public static class MockCorresponDao extends MockAbstractDao<Entity> {
        static List<Correspon> RET_FIND_REPLY_CORRESPONS;
        static List<CorresponResponseHistory> RET_FIND_CORRESPONS_RESPONSE_HISTORY;
        static final Long ROOT_CORRESPON_ID = 1L;

        public List<Correspon> findReplyCorresponByAddressUserId(Long addressUserId) {
            return RET_FIND_REPLY_CORRESPONS;
        }
        public List<Correspon> findReplyCorresponByGroupId(Long corresponId, Long groupId) {
            return RET_FIND_REPLY_CORRESPONS;
        }
        public Long findRootCorresponId(Long corresponId) {
            List<CorresponResponseHistory> histories = new ArrayList<CorresponResponseHistory>();
            Correspon correspon = null;
            CorresponResponseHistory history = null;

            // 指定のコレポン文書の大本の文書から返信元文書までのダミー作成
            for (long l = ROOT_CORRESPON_ID ; l < corresponId ; l++) {
                correspon = new Correspon();
                correspon.setId(l);
                history = new CorresponResponseHistory();
                history.setCorrespon(correspon);
                histories.add(history);
            }

            for (CorresponResponseHistory crh : RET_FIND_CORRESPONS_RESPONSE_HISTORY) {
                histories.add(crh);
            }
            RET_FIND_CORRESPONS_RESPONSE_HISTORY = histories;
            return ROOT_CORRESPON_ID;
        }
        public List<CorresponResponseHistory> findCorresponResponseHistory(
            Long corresponId, Long currentCorresponId) {
            return RET_FIND_CORRESPONS_RESPONSE_HISTORY;
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

            assertEquals(RET_UPDATE_ADDRESSCORRESPONGROUP.getCorresponId(), addressCorresponGroup
                .getCorresponId());
            assertEquals(RET_UPDATE_ADDRESSCORRESPONGROUP.getUpdatedBy().getEmpNo(),
                addressCorresponGroup.getUpdatedBy().getEmpNo());
            assertEquals(RET_UPDATE_ADDRESSCORRESPONGROUP.getUpdatedBy().getNameE(),
                addressCorresponGroup.getUpdatedBy().getNameE());

            return RET_DELETE_BY_CORRESPONID;
        }

    }

    public static class MockPersonInChargeDao {
        static List<PersonInCharge> RET_FIND_BY_CORRESPON_ID;
        static Integer RET_DELETE_BY_CORRESPONID = 0;
        static PersonInCharge RET_UPDATE_PERSONINCHARGE = null;
        static List<PersonInCharge> RET_FIND_BY_ADDRESS_USER_ID;
        public Integer deleteByAddressUserId(PersonInCharge personInCharge) {
            if (RET_DELETE_BY_CORRESPONID > 0) {
                assertEquals(RET_UPDATE_PERSONINCHARGE.getAddressUserId(), personInCharge
                    .getAddressUserId());
                assertEquals(RET_UPDATE_PERSONINCHARGE.getUpdatedBy(), personInCharge
                    .getUpdatedBy());
            }
            return 1;
        }

        public List<PersonInCharge> findByAddressUserId(Long addressUserId){
            return RET_FIND_BY_ADDRESS_USER_ID;
        }

    }

    public static class MockAttachmentDao extends MockAbstractDao<Entity> {
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

            assertEquals(RET_UPDATE_ADDRESSUSER.getAddressCorresponGroupId(), addressUser
                .getAddressCorresponGroupId());
            assertEquals(RET_UPDATE_ADDRESSUSER.getUpdatedBy().getEmpNo(), addressUser
                .getUpdatedBy().getEmpNo());
            assertEquals(RET_UPDATE_ADDRESSUSER.getUpdatedBy().getNameE(), addressUser
                .getUpdatedBy().getNameE());

            return RET_DELETE_BY_CORRESPONID;
        }

    }

    public static class MockCorresponCustomFieldDao {
        static Integer RET_DELETE_BY_CORRESPONID;
        static CorresponCustomField RET_FIND_BY_ID;
        static CorresponCustomField RET_UPDATE_CORRESPONCUSTOMFIELD;

        public Integer deleteByCorresponId(CorresponCustomField corresponCustomField) {

            assertEquals(RET_UPDATE_CORRESPONCUSTOMFIELD.getCorresponId(), corresponCustomField
                .getCorresponId());
            assertEquals(RET_UPDATE_CORRESPONCUSTOMFIELD.getUpdatedBy().getEmpNo(),
                corresponCustomField.getUpdatedBy().getEmpNo());
            assertEquals(RET_UPDATE_CORRESPONCUSTOMFIELD.getUpdatedBy().getNameE(),
                corresponCustomField.getUpdatedBy().getNameE());

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
                assertEquals(w.getWorkflowProcessStatus().getLabel(), entryWorkflow
                    .getWorkflowProcessStatus().getLabel());
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
        static User FIND_BY_EMP_NO;

        public List<ProjectUser> findProjectUser(SearchUserCondition condition) {
            return RET_FIND_BY_CORRESPON_ID;
        }

        public User findByEmpNo(String empNo) throws RecordNotFoundException {
            if (FIND_BY_EMP_NO == null) {
                throw new RecordNotFoundException();
            }
            return FIND_BY_EMP_NO;
        }
    }

    public static class MockCustomFieldDao {
        static CustomField RET_FIND_BY_ID_PROJECT_ID;
        static RecordNotFoundException EX_FIND_BY_PROJECT_ID;
        static List<CustomField> RET_FIND_BY_PROJECT_ID = new ArrayList<CustomField>();

        public CustomField findByIdProjectId(SearchCustomFieldCondition condition)
            throws RecordNotFoundException {
            if (EX_FIND_BY_PROJECT_ID != null) {
                throw EX_FIND_BY_PROJECT_ID;
            }
            return RET_FIND_BY_ID_PROJECT_ID;
        }

        public List<CustomField> findByProjectId(SearchCustomFieldCondition condition) {
            return RET_FIND_BY_PROJECT_ID;
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

    public static class MockProjectUser {
        static boolean RET_PROJECT_USER;

        public boolean isProjectAdmin() {
            return RET_PROJECT_USER;
        }
    }

    public static class MockUser {
        static boolean RET_USER;

        public boolean isSystemAdmin() {
            return RET_USER;
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

    public static class MockCorresponHierarchyDao {
        static int COUNT;

        public int countByParentCorresponIdReplyAddressUserId(Long parentCorresponId,
            Long replyAddressUserId) {
            return COUNT;
        }
    }

    public static class MockProjectDao {
        public List<Project> findByEmpNo(String empNo) {
            List<Project> ret = new ArrayList<Project>();
            return ret;
        }
    }

    public static class MockUserRoleHelper {
        static boolean IS_CHECKER = false;
        static boolean IS_APPROVER = false;

        public boolean isWorkflowChecker(Correspon correspon, User user) {
            return IS_CHECKER;
        }

        public boolean isWorkflowApprover(Correspon correspon, User user) {
            return IS_APPROVER;
        }
    }

    public static class MockCorresponSequenceService extends CorresponSequenceServiceImpl{
        /**
         *
         */
        private static final long serialVersionUID = 2068189652892328126L;
        static String CORRESPON_NO = null;
        public String createReplyCorresponNo(Correspon correspon) throws ServiceAbortException {
            if (CORRESPON_NO == null) {
                throw new ServiceAbortException(ApplicationMessageCode.NO_DATA_FOUND);
            }
            return CORRESPON_NO;
        }
    }

    public static class MockCorresponReadStatusService {
        static Long SET_ID;
        static ReadStatus SET_READ_STATUS;
        public Integer updateReadStatusByCorresponId(Long id , ReadStatus readStatus)
                throws ServiceAbortException {
            SET_ID = id;
            SET_READ_STATUS = readStatus;
            return 1;
        }
    }

    public static class MockCorresponHTMLGeneratorUtil {
        public String getStylesheetContent() throws IOException {
            return "mock stylesheet";
        }
    }
}
