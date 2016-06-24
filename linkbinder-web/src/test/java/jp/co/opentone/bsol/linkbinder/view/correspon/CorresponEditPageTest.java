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
package jp.co.opentone.bsol.linkbinder.view.correspon;

import static jp.co.opentone.bsol.framework.core.message.MessageCode.*;
import static jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode.*;
import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.annotation.Resource;
import javax.faces.application.FacesMessage;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import jp.co.opentone.bsol.framework.core.message.Messages;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.extension.jsf.FacesContextMock;
import jp.co.opentone.bsol.framework.test.AbstractTestCase;
import jp.co.opentone.bsol.framework.web.view.util.ViewHelper;
import jp.co.opentone.bsol.linkbinder.Constants;
import jp.co.opentone.bsol.linkbinder.attachment.AttachmentInfo;
import jp.co.opentone.bsol.linkbinder.dto.AddressCorresponGroup;
import jp.co.opentone.bsol.linkbinder.dto.AddressUser;
import jp.co.opentone.bsol.linkbinder.dto.Attachment;
import jp.co.opentone.bsol.linkbinder.dto.Correspon;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroup;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroupUser;
import jp.co.opentone.bsol.linkbinder.dto.CorresponType;
import jp.co.opentone.bsol.linkbinder.dto.CustomField;
import jp.co.opentone.bsol.linkbinder.dto.CustomFieldValue;
import jp.co.opentone.bsol.linkbinder.dto.DistTemplateHeader;
import jp.co.opentone.bsol.linkbinder.dto.Project;
import jp.co.opentone.bsol.linkbinder.dto.ProjectUser;
import jp.co.opentone.bsol.linkbinder.dto.UpdateMode;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.code.AddressType;
import jp.co.opentone.bsol.linkbinder.dto.code.AddressUserType;
import jp.co.opentone.bsol.linkbinder.dto.code.CorresponStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.ReplyRequired;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowStatus;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponGroupCondition;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponTypeCondition;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCustomFieldCondition;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchUserCondition;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.service.admin.impl.CorresponGroupServiceImpl;
import jp.co.opentone.bsol.linkbinder.service.admin.impl.CorresponTypeServiceImpl;
import jp.co.opentone.bsol.linkbinder.service.admin.impl.CustomFieldServiceImpl;
import jp.co.opentone.bsol.linkbinder.service.admin.impl.DistributionTemplateServiceImpl;
import jp.co.opentone.bsol.linkbinder.service.admin.impl.UserServiceImpl;
import jp.co.opentone.bsol.linkbinder.service.correspon.impl.CorresponServiceImpl;
import jp.co.opentone.bsol.linkbinder.service.correspon.impl.CorresponValidateServiceImpl;
import jp.co.opentone.bsol.linkbinder.util.view.correspon.CorresponPageFormatter;
import jp.co.opentone.bsol.linkbinder.view.AbstractPage;
import jp.co.opentone.bsol.linkbinder.view.correspon.util.CorresponEditMode;
import mockit.Mock;
import mockit.MockUp;

/**
 * {@link CorresponEditPage}のテストケース.
 * @author opentone
 */
public class CorresponEditPageTest extends AbstractTestCase {

    /**
     * テスト対象.
     */
    @Resource
    private CorresponEditPage page;
    /**
     * viewの共通処理を集めたオブジェクト.
     */
    @Resource
    protected ViewHelper viewHelper;

    /**
     * 現在選択中プロジェクト.
     */
    private Project currentProject;

    /**
     * 現在ログイン中ユーザー.
     */
    private User currentUser;
    /**
     * 現在ログイン中ユーザー(プロジェクト情報).
     */
    private ProjectUser currentProjectUser;

    /**
     * Mockから返される活動単位リスト.
     */
    private List<CorresponGroup> corresponGroups;

    /**
     * ユーザーが所属する活動単位リスト.
     */
    private List<CorresponGroup> userGroups;

    /**
     * Mockから返されるコレポン文書種別リスト.
     */
    private List<CorresponType> corresponTypes;

    /**
     * Mockから返されるプロジェクトユーザーリスト.
     */
    private List<ProjectUser> projectUsers;

    /**
     * Mockから返されるカスタムフィールドリスト.
     */
    private List<CustomField> customFields;

    /**
     * Mockから返される活動単位所属ユーザーリスト.
     */
    private List<CorresponGroupUser> corresponGroupUsers;

    /**
     * Mockから返されるカスタムフィールド設定値リスト.
     */
    private List<CustomFieldValue> customFieldValues;

    /**
     * 入力される日付のフォーマット.
     */
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    /**
     * テストの前準備.
     */
    @BeforeClass
    public static void testSetUp() {
        FacesContextMock.initialize();

        new MockAbstractPage();
        new MockCustomFieldService();
        new MockCorresponGroupService();
        new MockCorresponTypeService();
        new MockUserService();
        new MockCorresponService();
        new MockCorresponValidateService();
        new MockDistributionTemplateService();
    }

    /**
     * 後始末.
     */
    @AfterClass
    public static void testTeardown() {
        new MockAbstractPage().tearDown();
        new MockCustomFieldService().tearDown();
        new MockCorresponGroupService().tearDown();
        new MockCorresponTypeService().tearDown();
        new MockUserService().tearDown();
        new MockCorresponService().tearDown();
        new MockCorresponValidateService().tearDown();
        new MockDistributionTemplateService().tearDown();

        FacesContextMock.tearDown();
    }

    @Before
    public void setUp() {
        FacesContextMock.EXPECTED_MESSAGE = null;

        MockCorresponService.RET_FIND = null;
        MockCustomFieldService.RET_FIND = null;
        MockCustomFieldService.RET_FIND_VALUE = null;
        MockCorresponGroupService.RET_FIND = null;
        MockCorresponTypeService.RET_FIND = null;
        MockUserService.RET_SEARCH = null;
        MockCorresponService.EX_FIND_BY_ID = null;
        MockCorresponValidateService.EX_VALIDATE = null;

        currentProject = new Project();
        currentProject.setProjectId("PJ1");
        currentProject.setNameE("Test Project1");
        MockAbstractPage.RET_PROJECT = currentProject;

        currentUser = new User();
        currentUser.setEmpNo("ZZA01");
        currentUser.setNameE("Test User");

        currentProjectUser = new ProjectUser();
        currentProjectUser.setUser(currentUser);
        MockAbstractPage.RET_PROJECT_USER = currentProjectUser;


        //  期待値の準備
        corresponGroups = createCorresponGroups();
        MockCorresponGroupService.RET_FIND = corresponGroups;

        corresponTypes = createCorresponTypes();
        MockCorresponTypeService.RET_FIND = corresponTypes;

        projectUsers = createProjectUsers();
        MockUserService.RET_SEARCH = projectUsers;

        customFields = createCustomFields();
        MockCustomFieldService.RET_FIND = customFields;

        corresponGroupUsers = createCorresponGroupUsers();
        MockUserService.RET_SEARCH_CORRESPON_GROUP = corresponGroupUsers;

        customFieldValues = createCustomFieldValues();
        MockCustomFieldService.RET_FIND_VALUE = customFieldValues;

        //  ユーザーが所属する活動単位の準備
        userGroups = createUserCorresponGroups();

        page.setNewEdit(null);
        page.setAttachment1(null);
        page.setAttachment2(null);
        page.setAttachment3(null);
        page.setAttachment4(null);
        page.setAttachment5(null);
        page.setAttachments(new ArrayList<AttachmentInfo>());
        page.setAttachmentDeletedList(new ArrayList<Boolean>());
        page.setAddressUserValues(null);

        User u = new User();
        u.setEmpNo("11111");
        MockAbstractPage.RET_USER = u;
    }

    @After
    public void tearDown() {
        MockCorresponService.RET_FIND = null;

        MockCustomFieldService.RET_FIND = null;
        MockCustomFieldService.RET_FIND_VALUE = null;
        MockCorresponGroupService.RET_FIND = null;
        MockCorresponTypeService.RET_FIND = null;
        MockUserService.RET_SEARCH = null;
        MockCorresponService.EX_FIND_BY_ID = null;
        MockCorresponValidateService.EX_VALIDATE = null;

        MockAbstractPage.RET_PROJECT = null;
        MockAbstractPage.RET_PROJECT_USER = null;
    }

    private List<CustomField> createCustomFields() {
        List<CustomField> customFields = new ArrayList<CustomField>();

        CustomField customField = new CustomField();
        customField.setId(11L);
        customField.setProjectCustomFieldId(1L);
        customField.setLabel("Label1");
        customFields.add(customField);

        customField = new CustomField();
        customField.setId(22L);
        customField.setProjectCustomFieldId(2L);
        customField.setLabel("Label2");
        customFields.add(customField);
        return customFields;
    }

    private List<CorresponGroup> createCorresponGroups() {
        List<CorresponGroup> corresponGroups = new ArrayList<CorresponGroup>();

        CorresponGroup corresponGroup;
        corresponGroup = new CorresponGroup();
        corresponGroup.setId(1L);
        corresponGroup.setName("CorresponGroupName1");

        corresponGroups.add(corresponGroup);

        corresponGroup = new CorresponGroup();
        corresponGroup.setId(2L);
        corresponGroup.setName("CorresponGroupName2");

        corresponGroups.add(corresponGroup);

        return corresponGroups;
    }

    private List<CorresponType> createCorresponTypes() {
        List<CorresponType> corresponTypes = new ArrayList<CorresponType>();

        CorresponType corresponType = new CorresponType();
        corresponType.setId(1L);
        corresponType.setProjectCorresponTypeId(10L);
        corresponType.setCorresponType("TST");
        corresponType.setName("CorresponTypeName");

        corresponTypes.add(corresponType);
        return corresponTypes;
    }

    private List<ProjectUser> createProjectUsers() {
        List<ProjectUser> projectUsers = new ArrayList<ProjectUser>();
        ProjectUser projectUser = new ProjectUser();
        User user = new User();
        user.setEmpNo("00001");
        user.setNameE("UserEName");
        projectUser.setUser(user);
        projectUsers.add(projectUser);

        return projectUsers;
    }

    private List<CustomFieldValue> createCustomFieldValues() {
        List<CustomFieldValue> customFieldValues = new ArrayList<CustomFieldValue>();
        CustomFieldValue customFieldValue = new CustomFieldValue();
        customFieldValue.setId(new Long(1));
        customFieldValue.setValue("");
        customFieldValues.add(customFieldValue);

        return customFieldValues;
    }

    private List<CorresponGroupUser> createCorresponGroupUsers() {
        List<CorresponGroupUser> corresponGroupUsers = new ArrayList<CorresponGroupUser>();
        CorresponGroupUser corresponGroupUser = new CorresponGroupUser();

        List<CorresponGroup> corresponGroups = createCorresponGroups();
        corresponGroupUser.setCorresponGroup(corresponGroups.get(0));
        corresponGroupUsers.add(corresponGroupUser);

        return corresponGroupUsers;
    }

    private String getExpectedUserLabel(User user) {
        return String.format("%s/%s", user.getNameE(), user.getEmpNo());
    }

    private List<CorresponGroup> createUserCorresponGroups() {
        List<CorresponGroup> userGroups = new ArrayList<CorresponGroup>();
        List<CorresponGroupUser> groupUsers = createCorresponGroupUsers();
        for (CorresponGroupUser gu : groupUsers) {
            userGroups.add(gu.getCorresponGroup());
        }
        return userGroups;
    }

    /**
     * 初期化処理で、入力フィールドに設定されるべき内容が
     * 正しく設定されているかを検証する.
     */
    private void assertInitializeInputFieldValues() {

        //  活動単位
        assertEquals(userGroups.size(), page.getFromGroup().size());
        for (int i = 0; i < userGroups.size(); i++) {
            assertEquals(userGroups.get(i).getId(), page.getFromGroup().get(i).getId());
            assertEquals(userGroups.get(i).getName(), page.getFromGroup().get(i).getName());
        }
        assertEquals(userGroups.size(), page.getSelectFrom().size());
        for (int i = 0; i < userGroups.size(); i++) {
            assertEquals(userGroups.get(i).getId(), page.getSelectFrom().get(i).getValue());
            assertEquals(userGroups.get(i).getName(), page.getSelectFrom().get(i).getLabel());
        }

        //  コレポン文書種別
        assertEquals(corresponTypes.size(), page.getCorresponType().size());
        for (int i = 0; i < corresponTypes.size(); i++) {
            assertEquals(
                corresponTypes.get(i).getId(), page.getCorresponType().get(i).getId());
            assertEquals(
                corresponTypes.get(i).getName(), page.getCorresponType().get(i).getName());
            assertEquals(
                corresponTypes.get(i).getProjectCorresponTypeId(), page.getCorresponType().get(i).getProjectCorresponTypeId());
        }
        assertEquals(corresponTypes.size(), page.getSelectCorresponType().size());
        for (int i = 0; i < corresponTypes.size(); i++) {
            //  選択リストの値はコレポン種別.IDではなく、コレポン種別.プロジェクト-コレポン種別ID
            assertEquals(
                corresponTypes.get(i).getProjectCorresponTypeId(),
                page.getSelectCorresponType().get(i).getValue());
            assertEquals(
                corresponTypes.get(i).getLabel(),
                page.getSelectCorresponType().get(i).getLabel());
        }

        //  文書状態
        CorresponStatus[] expected;
        if (page.getCorrespon().getWorkflowStatus() == WorkflowStatus.ISSUED) {
            expected = CorresponStatus.values();
        } else {
            expected = new CorresponStatus[] { CorresponStatus.OPEN, CorresponStatus.CLOSED };
        }
        assertEquals(expected.length, page.getSelectCorresponStatus().size());
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i].getValue(),
                page.getSelectCorresponStatus().get(i).getValue());
            assertEquals(expected[i].getLabel(),
                page.getSelectCorresponStatus().get(i).getLabel());
        }

        //  選択候補のユーザー一覧
        assertEquals(projectUsers.size(), page.getProjectUsers().size());
        for (int i = 0; i < projectUsers.size(); i++) {
            assertEquals(projectUsers.get(i).getUser().getEmpNo(), page.getProjectUsers().get(i).getEmpNo());
            assertEquals(getExpectedUserLabel(projectUsers.get(i).getUser()),
                        page.getProjectUsers().get(i).getLabel());
        }

        //  To(User)の選択候補
        //  別テストメソッドで検証済

        //  Cc(User)の選択候補
        //  別テストメソッドで検証済

        //  To(Group)の選択候補
        //  別テストメソッドで検証済

        //  Cc(Group)の選択候補
        //  別テストメソッドで検証済
    }

    /**
     * 初期化アクションのテスト.
     * 新規作成の場合の検証.
     * @throws Exception
     */
    @Test
    public void testInitializeNew() throws Exception {

        //  テスト対象メソッド実行
        page.initialize();
        //  画面表示タイトル
        assertEquals(CorresponEditPage.NEW, page.getTitle());

        //  入力フィールドに表示する内容を検証
        assertInitializeInputFieldValues();

        //  コレポン文書番号は未設定
        assertEquals(CorresponPageFormatter.DEFAULT_CORRESPON_NO, page.getFormatter().getCorresponNo());

        assertEquals(Long.valueOf(1), page.getCorrespon().getCustomField1Id());
        assertEquals("Label1", page.getCorrespon().getCustomField1Label());
        assertEquals(Long.valueOf(2), page.getCorrespon().getCustomField2Id());
        assertEquals("Label2", page.getCorrespon().getCustomField2Label());
        assertNull(page.getCorrespon().getCustomField3Id());
        assertNull(page.getCorrespon().getCustomField4Id());
        assertNull(page.getCorrespon().getCustomField5Id());
        assertNull(page.getCorrespon().getCustomField6Id());
        assertNull(page.getCorrespon().getCustomField7Id());
        assertNull(page.getCorrespon().getCustomField8Id());
        assertNull(page.getCorrespon().getCustomField9Id());
        assertNull(page.getCorrespon().getCustomField10Id());

        assertEquals(WorkflowStatus.DRAFT, page.getCorrespon().getWorkflowStatus());

        assertEquals(new Long(1), page.getCustomFieldValue1().get(0).getId());
        assertEquals(1, page.getCustomFieldValue1().size());
        assertEquals(new Long(1), page.getCustomFieldValue2().get(0).getId());
        assertEquals(1, page.getCustomFieldValue2().size());
        //  プロジェクトカスタムフィールド自体が未定義の場合は、設定候補の値も取得していない
        assertNull(page.getCustomFieldValue3());
        assertNull(page.getCustomFieldValue4());
        assertNull(page.getCustomFieldValue5());
        assertNull(page.getCustomFieldValue6());
        assertNull(page.getCustomFieldValue7());
        assertNull(page.getCustomFieldValue8());
        assertNull(page.getCustomFieldValue9());
        assertNull(page.getCustomFieldValue10());
    }

    /**
     * 初期化アクションのテスト. 返信 成功
     * @throws Exception
     */
    @Test
    public void testInitializeReply() throws Exception {
        // Serviceが返すダミーのコレポン文書を設定

        Correspon correspon = new Correspon();
        // 必須のプロパティをセットして実行
        page.setEditMode(CorresponEditMode.REPLY.name());
        page.setId(new Long(10));
        correspon.setId(new Long(10));
        correspon.setSubject("Subject");
        correspon.setBody("Body1\nBody2");
        correspon.setCorresponNo("YOC:IT-12345");
        correspon.setCorresponStatus(CorresponStatus.OPEN);

        correspon.setProjectId(currentProject.getProjectId());

        correspon.setWorkflowStatus(WorkflowStatus.ISSUED);

        User created = new User();
        created.setEmpNo("00001");
        correspon.setCreatedBy(created);

        Date issued = new GregorianCalendar(2009, 7, 11).getTime();
        correspon.setIssuedAt(issued);

        // ------------------------
        //  宛先情報設定
        // ------------------------
        List<AddressCorresponGroup> addresses = new ArrayList<AddressCorresponGroup>();
        List<AddressUser> users;
        AddressCorresponGroup ag;
        CorresponGroup g;
        AddressUser au;
        User u;

        //TO
        ag = new AddressCorresponGroup();
        ag.setId(123L);
        ag.setAddressType(AddressType.TO);
        g = new CorresponGroup();
        g.setId(2L);
        ag.setCorresponGroup(g);
        users = new ArrayList<AddressUser>();
        au = new AddressUser();
        au.setId(1L);
        au.setAddressUserType(AddressUserType.ATTENTION);
        u = new User();
        u.setEmpNo("11111");
        au.setUser(u);
        users.add(au);
        ag.setUsers(users);
        addresses.add(ag);

        //CC
        ag = new AddressCorresponGroup();
        ag.setId(112L);
        ag.setAddressType(AddressType.CC);
        g = new CorresponGroup();
        g.setId(2L);
        ag.setCorresponGroup(g);
        users = new ArrayList<AddressUser>();
        ag.setUsers(users);
        addresses.add(ag);

        correspon.setAddressCorresponGroups(addresses);

        MockCorresponService.RET_FIND = correspon;

        page.initialize();
        //  画面表示タイトル
        assertEquals(CorresponEditPage.NEW, page.getTitle());

        assertInitializeInputFieldValues();

        assertEquals("Re: Subject", page.getSubject());

        assertEquals(Long.valueOf(1), page.getCorrespon().getCustomField1Id());
        assertEquals("Label1", page.getCorrespon().getCustomField1Label());
        assertEquals(Long.valueOf(2), page.getCorrespon().getCustomField2Id());
        assertEquals("Label2", page.getCorrespon().getCustomField2Label());
        assertNull(page.getCorrespon().getCustomField3Id());
        assertNull(page.getCorrespon().getCustomField4Id());
        assertNull(page.getCorrespon().getCustomField5Id());
        assertNull(page.getCorrespon().getCustomField6Id());
        assertNull(page.getCorrespon().getCustomField7Id());
        assertNull(page.getCorrespon().getCustomField8Id());
        assertNull(page.getCorrespon().getCustomField9Id());
        assertNull(page.getCorrespon().getCustomField10Id());

        assertEquals(WorkflowStatus.DRAFT, page.getCorrespon().getWorkflowStatus());

        assertEquals(new Long(1), page.getCustomFieldValue1().get(0).getId());
        assertEquals(1, page.getCustomFieldValue1().size());
        assertEquals(new Long(1), page.getCustomFieldValue2().get(0).getId());
        assertEquals(1, page.getCustomFieldValue2().size());
        //  プロジェクトカスタムフィールド自体が未定義の場合は、設定候補の値も取得していない
        assertNull(page.getCustomFieldValue3());
        assertNull(page.getCustomFieldValue4());
        assertNull(page.getCustomFieldValue5());
        assertNull(page.getCustomFieldValue6());
        assertNull(page.getCustomFieldValue7());
        assertNull(page.getCustomFieldValue8());
        assertNull(page.getCustomFieldValue9());
        assertNull(page.getCustomFieldValue10());

    }

    /**
     * 初期化アクションのテスト. 返信 返信元コレポン文書が存在しない際はエラー
     * @throws Exception
     */
    @Test
    public void testInitializeReplyOriginalNotFound() {
        // Serviceが返すダミーのコレポン文書を設定

        // 必須のプロパティをセットして実行
        page.setEditMode(CorresponEditMode.REPLY.name());
        page.setId(new Long(10));
        page.setActionName("Initialize");

        MockCorresponService.RET_FIND = null;
        MockCorresponService.EX_FIND_BY_ID =
            new ServiceAbortException(ApplicationMessageCode.NO_DATA_FOUND);

        // 期待されるメッセージをセット
        FacesContextMock.EXPECTED_CLIENT_ID = null;
        FacesContextMock.EXPECTED_MESSAGE =
                new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                    FacesMessage.SEVERITY_ERROR.toString(),
                    createExpectedMessageString(
                            Messages.getMessageAsString(CANNOT_PERFORM_BECAUSE_ORIGINAL_CORRESPON_NOT_EXIST),
                            "Initialize"));
        // テスト実行
        page.initialize();
        //  画面表示タイトル
        assertEquals(CorresponEditPage.NEW, page.getTitle());
    }

    /**
     * 初期化アクションのテスト. 返信 返信元コレポン文書のプロジェクトが現在選択中のプロジェクト以外はエラー
     * @throws Exception
     */
    @Test
    public void testInitializeReplyProjectDifferent() {
        // Serviceが返すダミーのコレポン文書を設定

        Correspon correspon = new Correspon();
        // 必須のプロパティをセットして実行
        page.setEditMode(CorresponEditMode.REPLY.name());
        page.setId(new Long(10));

        correspon.setProjectId("Pj001");
        correspon.setId(new Long(10));
        correspon.setCorresponNo("YOC:IT-00001");
        correspon.setSubject("Subject");
        correspon.setBody("Body");

        MockCorresponService.RET_FIND = correspon;

        // 期待されるメッセージをセット
        FacesContextMock.EXPECTED_CLIENT_ID = null;
        FacesContextMock.EXPECTED_MESSAGE =
                new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                    FacesMessage.SEVERITY_ERROR.toString(),
                    createExpectedMessageString(
                            Messages.getMessageAsString(INVALID_ORIGINAL_CORRESPON),
                            null,
                            correspon.getCorresponNo()));
        // テスト実行
        page.initialize();
        //  画面表示タイトル
        assertEquals(CorresponEditPage.NEW, page.getTitle());
    }

    /**
     * 初期化アクションのテスト. 返信 返信元コレポン文書の[5：Issued]以外に関してはエラー
     * @throws Exception
     */
    @Test
    public void testInitializeReplyOriginalNotIssued() throws Exception {
        // Serviceが返すダミーのコレポン文書を設定

        Correspon correspon = new Correspon();
        // 必須のプロパティをセットして実行
        page.setEditMode(CorresponEditMode.REPLY.name());
        page.setId(new Long(10));
        page.setActionName("Initialize");

        correspon.setProjectId(currentProject.getProjectId());
        correspon.setId(new Long(10));
        correspon.setCorresponNo("YOC:IT-00001");
        correspon.setSubject("Subject");
        correspon.setBody("Body");

        MockCorresponService.RET_FIND = correspon;

        // 期待されるメッセージをセット
        FacesContextMock.EXPECTED_CLIENT_ID = null;
        FacesContextMock.EXPECTED_MESSAGE =
                new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                    FacesMessage.SEVERITY_ERROR.toString(),
                    createExpectedMessageString(
                            Messages.getMessageAsString(CANNOT_PERFORM_BECAUSE_ORIGINAL_WORKFLOW_STATUS_INVALID),
                            "Initialize"));

        // 不正な全ての承認状態を設定してテスト実行
        WorkflowStatus[] invalidStatuses = {
            WorkflowStatus.DRAFT,
            WorkflowStatus.REQUEST_FOR_CHECK,
            WorkflowStatus.REQUEST_FOR_APPROVAL,
            WorkflowStatus.UNDER_CONSIDERATION,
            WorkflowStatus.DENIED
        };
        for (WorkflowStatus s : invalidStatuses) {
            correspon.setWorkflowStatus(s);
            page.initialize();
            //  画面表示タイトル
            assertEquals(CorresponEditPage.NEW, page.getTitle());
        }
    }

    /**
     * 初期化アクションのテスト. 更新 成功
     * @throws Exception
     */
    @Test
    public void testInitializeUpdate() throws Exception {

        // 必須のプロパティをセットして実行
        page.setEditMode(CorresponEditMode.UPDATE.name());
        page.setId(new Long(10));
        User user = new User();
        user.setEmpNo("00001");
        user.setNameE("UserEName");
        ProjectUser pu = new ProjectUser();
        pu.setUser(user);
        viewHelper.setSessionValue(Constants.KEY_PROJECT_USER,pu);

        Correspon correspon = new Correspon();
        correspon.setProjectId(currentProject.getProjectId());
        correspon.setReplyRequired(ReplyRequired.YES);
        correspon.setDeadlineForReply(new Date("31-Mar-2009"));
        correspon.setId(new Long(10));
        correspon.setSubject("Subject");
        correspon.setBody("Body");
        correspon.setCorresponNo("YOC:IT-00009");

        CorresponGroup from = new CorresponGroup();
        from.setId(10L);
        from.setName("CorresponGroupName10");
        correspon.setFromCorresponGroup(from);

        CorresponType corresponType = new CorresponType();
        corresponType.setId(2L);
        corresponType.setProjectCorresponTypeId(100L); //こちらがpageにセットされる
        correspon.setCorresponType(corresponType);

        // ------------------------
        //  宛先情報設定
        // ------------------------
        List<AddressCorresponGroup> addresses = new ArrayList<AddressCorresponGroup>();
        List<AddressUser> users;
        AddressCorresponGroup ag;
        CorresponGroup g;
        AddressUser au;
        User u;

        //TO
        ag = new AddressCorresponGroup();
        ag.setAddressType(AddressType.TO);
        g = new CorresponGroup();
        g.setId(2L);
        ag.setCorresponGroup(g);
        users = new ArrayList<AddressUser>();
        au = new AddressUser();
        au.setAddressUserType(AddressUserType.ATTENTION);
        u = new User();
        u.setEmpNo("ZZA02");
        au.setUser(u);
        users.add(au);
        addresses.add(ag);

        //CC
        ag = new AddressCorresponGroup();
        ag.setAddressType(AddressType.CC);
        g = new CorresponGroup();
        g.setId(2L);
        ag.setCorresponGroup(g);
        addresses.add(ag);

        correspon.setAddressCorresponGroups(addresses);

        correspon.setWorkflowStatus(WorkflowStatus.DRAFT);
        correspon.setParentCorresponId(null);
        MockCorresponService.RET_FIND = correspon;

        correspon.setCustomField1Value("cValue1");
        correspon.setCustomField2Value("cValue2");

        page.initialize();
        //  画面表示タイトル
        assertEquals(CorresponEditPage.UPDATE, page.getTitle());

        // ユーザーが関係するグループに加えてFromに設定されているグループも追加されるため、Fromに設定したグループを追加
        userGroups.add(from);
        assertInitializeInputFieldValues();

        // 他のテストに影響が出ないように削除しておく
        userGroups.remove(from);

        //  コレポン文書番号は更新対象のもの
        assertEquals(correspon.getCorresponNo(), page.getFormatter().getCorresponNo());

        assertEquals(correspon.getSubject(), page.getCorrespon().getSubject());
        assertEquals(correspon.getBody(), page.getCorrespon().getBody());
        assertNull(page.getCorrespon().getParentCorresponId());

        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);

        assertEquals(correspon.getFromCorresponGroup().getId(), page.getFrom());
        assertEquals(correspon.getCorresponType().getProjectCorresponTypeId(), page.getType());
        assertEquals(correspon.getReplyRequired().getValue(), page.getReplyRequired());
        assertEquals(sdf.format(correspon.getDeadlineForReply()), page.getDeadlineForReply());

        assertEquals(new Long(1), page.getCorrespon().getCustomField1Id());
        assertEquals("Label1", page.getCorrespon().getCustomField1Label());
        assertEquals(new Long(2), page.getCorrespon().getCustomField2Id());
        assertEquals("Label2", page.getCorrespon().getCustomField2Label());
        assertNull(page.getCorrespon().getCustomField3Id());
        assertNull(page.getCorrespon().getCustomField4Id());
        assertNull(page.getCorrespon().getCustomField5Id());
        assertNull(page.getCorrespon().getCustomField6Id());
        assertNull(page.getCorrespon().getCustomField7Id());
        assertNull(page.getCorrespon().getCustomField8Id());
        assertNull(page.getCorrespon().getCustomField9Id());
        assertNull(page.getCorrespon().getCustomField10Id());

        assertEquals(correspon.getCustomField1Value(), page.getCorrespon().getCustomField1Value());
        assertEquals(correspon.getCustomField2Value(), page.getCorrespon().getCustomField2Value());
        assertEquals(correspon.getCustomField3Value(), page.getCorrespon().getCustomField3Value());
        assertEquals(correspon.getCustomField4Value(), page.getCorrespon().getCustomField4Value());
        assertEquals(correspon.getCustomField5Value(), page.getCorrespon().getCustomField5Value());
        assertEquals(correspon.getCustomField6Value(), page.getCorrespon().getCustomField6Value());
        assertEquals(correspon.getCustomField7Value(), page.getCorrespon().getCustomField7Value());
        assertEquals(correspon.getCustomField7Value(), page.getCorrespon().getCustomField8Value());
        assertEquals(correspon.getCustomField9Value(), page.getCorrespon().getCustomField9Value());
        assertEquals(correspon.getCustomField10Value(), page.getCorrespon().getCustomField10Value());

        //assertEquals(WorkflowStatus.DRAFT, page.getCorrespon().getWorkflowStatus());

        assertEquals(new Long(1), page.getCustomFieldValue1().get(0).getId());
        assertEquals(1, page.getCustomFieldValue1().size());
        assertEquals(new Long(1), page.getCustomFieldValue2().get(0).getId());
        assertEquals(1, page.getCustomFieldValue2().size());
        //  プロジェクトカスタムフィールド自体が未定義の場合は、設定候補の値も取得していない
        assertNull(page.getCustomFieldValue3());
        assertNull(page.getCustomFieldValue4());
        assertNull(page.getCustomFieldValue5());
        assertNull(page.getCustomFieldValue6());
        assertNull(page.getCustomFieldValue7());
        assertNull(page.getCustomFieldValue8());
        assertNull(page.getCustomFieldValue9());
        assertNull(page.getCustomFieldValue10());
    }

    /**
     * 初期化アクションのテスト. 複写 成功
     * @throws Exception
     */
    @Test
    public void testInitializeCopy() throws Exception {

        Correspon correspon = new Correspon();
        // 必須のプロパティをセットして実行
        page.setEditMode(CorresponEditMode.COPY.name());
        page.setId(new Long(10));
        User user = new User();
        user.setEmpNo("00001");
        user.setNameE("UserEName");
        ProjectUser pu = new ProjectUser();
        pu.setUser(user);
        viewHelper.setSessionValue(Constants.KEY_PROJECT_USER,pu);

        correspon.setProjectId(currentProject.getProjectId());
        correspon.setDeadlineForReply(new Date("31-Mar-2009"));
        correspon.setId(new Long(10));
        correspon.setSubject("Subject");
        correspon.setBody("Body");

        CorresponGroup from = new CorresponGroup();
        from.setId(10L);
        from.setName("CorresponGroupName10");
        correspon.setFromCorresponGroup(from);

        CorresponType corresponType = new CorresponType();
        corresponType.setId(2L);
        corresponType.setProjectCorresponTypeId(100L); //こちらがpageにセットされる

        correspon.setCorresponType(corresponType);
        correspon.setWorkflowStatus(WorkflowStatus.ISSUED);
        correspon.setParentCorresponId(null);

        MockCorresponService.RET_FIND = correspon;

        page.initialize();
        //  画面表示タイトル
        assertEquals(CorresponEditPage.NEW, page.getTitle());

        // ユーザーが関係するグループに加えてFromに設定されているグループも追加されるため、Fromに設定したグループを追加
        userGroups.add(from);
        assertInitializeInputFieldValues();

        // 他のテストに影響が出ないように削除しておく
        userGroups.remove(from);

        assertNull(page.getCorrespon().getId());
        assertNull(page.getId());
        assertEquals(WorkflowStatus.DRAFT, page.getCorrespon().getWorkflowStatus());
        assertEquals(CorresponPageFormatter.DEFAULT_CORRESPON_NO, page.getFormatter().getCorresponNo());
        assertNull(page.getCorrespon().getParentCorresponId());

        assertEquals(correspon.getSubject(), page.getCorrespon().getSubject());
        assertEquals(correspon.getBody(), page.getCorrespon().getBody());

        assertEquals(correspon.getFromCorresponGroup().getId(), page.getFrom());
        assertEquals(correspon.getCorresponType().getProjectCorresponTypeId(), page.getType());
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        assertEquals(sdf.format(new Date("31-Mar-2009")), page.getDeadlineForReply());

        assertEquals(new Long(1), page.getCorrespon().getCustomField1Id());
        assertEquals("Label1", page.getCorrespon().getCustomField1Label());
        assertEquals(new Long(2), page.getCorrespon().getCustomField2Id());
        assertEquals("Label2", page.getCorrespon().getCustomField2Label());
        assertNull(page.getCorrespon().getCustomField3Id());
        assertNull(page.getCorrespon().getCustomField4Id());
        assertNull(page.getCorrespon().getCustomField5Id());
        assertNull(page.getCorrespon().getCustomField6Id());
        assertNull(page.getCorrespon().getCustomField7Id());
        assertNull(page.getCorrespon().getCustomField8Id());
        assertNull(page.getCorrespon().getCustomField9Id());
        assertNull(page.getCorrespon().getCustomField10Id());

        assertEquals(new Long(1), page.getCustomFieldValue1().get(0).getId());
        assertEquals(1, page.getCustomFieldValue1().size());
        assertEquals(new Long(1), page.getCustomFieldValue2().get(0).getId());
        assertEquals(1, page.getCustomFieldValue2().size());
        //  プロジェクトカスタムフィールド自体が未定義の場合は、設定候補の値も取得していない
        assertNull(page.getCustomFieldValue3());
        assertNull(page.getCustomFieldValue4());
        assertNull(page.getCustomFieldValue5());
        assertNull(page.getCustomFieldValue6());
        assertNull(page.getCustomFieldValue7());
        assertNull(page.getCustomFieldValue8());
        assertNull(page.getCustomFieldValue9());
        assertNull(page.getCustomFieldValue10());

    }

    /**
     * {@link CorresponEditPage#next()}のテスト.
     * ServiceでのValidationでエラーが発生せず次画面へ遷移する場合の検証.
     * @throws Exception
     */
    @Test
    public void testNextValidNewEdit() throws Exception {

        setUpValidInput();
        page.setNewEdit("1");

        // 期待されるメッセージをセット
        FacesContextMock.EXPECTED_CLIENT_ID = null;
        FacesContextMock.EXPECTED_MESSAGE =
                new FacesMessage(
                    FacesMessage.SEVERITY_INFO,
                    FacesMessage.SEVERITY_INFO.toString(),
                    createExpectedMessageString(
                            Messages.getMessageAsString(CONTENT_WILL_BE_SAVED),
                            null));

        page.setActionName("Next");
        assertEquals("corresponConfirmation?newEdit=1&projectId=PJ1", page.next());
    }

    /**
     * {@link CorresponEditPage#next()}のテスト.
     * ServiceでのValidationでエラーが発生せず次画面へ遷移する場合の検証.
     * @throws Exception
     */
    @Test
    public void testNextValid() throws Exception {
        /*
        Correspon correspon = new Correspon();
        page.setCorrespon(correspon);
        page.setNewEdit(null);
        */

        setUpValidInput();

        // 期待されるメッセージをセット
        FacesContextMock.EXPECTED_CLIENT_ID = null;
        FacesContextMock.EXPECTED_MESSAGE =
                new FacesMessage(
                    FacesMessage.SEVERITY_INFO,
                    FacesMessage.SEVERITY_INFO.toString(),
                    createExpectedMessageString(
                            Messages.getMessageAsString(CONTENT_WILL_BE_SAVED),
                            null));

        page.setActionName("Next");
        assertEquals("corresponConfirmation?newEdit=&projectId=PJ1", page.next());

        //  コレポン文書に正しく入力値が格納されているかを検証
        validateInputValues();
    }

    /**
     * {@link CorresponEditPage#next()}のテスト.
     * ServiceでのValidationでエラーが発生し、次画面へ遷移しない場合の検証.
     * @throws Exception
     */
    @Test
    public void testNextInvalid() throws Exception {
        Correspon correspon = new Correspon();
        page.setCorrespon(correspon);

        //  Serviceで発生する検証例外をセット
        MockCorresponValidateService.EX_VALIDATE =
            new ServiceAbortException(E_INVALID_INPUT);

        // 期待されるメッセージをセット
        FacesContextMock.EXPECTED_CLIENT_ID = null;
        FacesContextMock.EXPECTED_MESSAGE =
                new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                    FacesMessage.SEVERITY_ERROR.toString(),
                    createExpectedMessageString(
                            Messages.getMessageAsString(E_INVALID_INPUT),
                            null));

        //  遷移先はnull。次画面への遷移は発生しない
        page.setActionName("Next");
        assertNull(page.next());
    }

    /**
     * {@link CorresponEditPage#edit()}のテスト.
     * 新規登録時の宛先(To)追加の検証.
     * @throws Exception
     */
    @Test
    public void testEditNewTo() throws Exception {
        Correspon c = new Correspon();
        page.setCorrespon(c);
        assertNull(page.getToAddressValues());

        // １つ目を追加
        page.setDetectedAddressType(AddressType.TO.getValue());
        page.setDetectedAddressIndex(-1); // 追加
        page.setCorresponGroupId(100L);
        page.setAddressUserValues("ZZA01-100,ZZA02-100");
        page.edit();

        List<AddressCorresponGroup> acgs = c.getToAddressCorresponGroups();
        assertNotNull(acgs);
        assertEquals(1, acgs.size());

        AddressCorresponGroup acg = acgs.get(0);
        assertEquals(AddressType.TO, acg.getAddressType());
        assertEquals(UpdateMode.NEW, acg.getMode());

        CorresponGroup cg = acg.getCorresponGroup();
        assertEquals(100L, cg.getId().longValue());

        List<AddressUser> aus = acg.getUsers();
        assertNotNull(aus);
        assertEquals(2, aus.size());

        AddressUser au1 = aus.get(0);
        assertEquals("ZZA01", au1.getUser().getEmpNo());
        assertEquals(AddressUserType.ATTENTION, au1.getAddressUserType());

        AddressUser au2 = aus.get(1);
        assertEquals("ZZA02", au2.getUser().getEmpNo());
        assertEquals(AddressUserType.ATTENTION, au2.getAddressUserType());

        assertNotNull(page.getToAddressValues());
    }

    /**
     * {@link CorresponEditPage#edit()}のテスト.
     * 新規登録時の宛先(CC)追加の検証.
     * @throws Exception
     */
    @Test
    public void testEditNewCc() throws Exception {
        Correspon c = new Correspon();
        page.setCorrespon(c);

        // １つ目を追加
        page.setDetectedAddressType(AddressType.CC.getValue());
        page.setDetectedAddressIndex(-1); // 追加
        page.setCorresponGroupId(100L);
        page.setAddressUserValues("ZZA01-100,ZZA02-100");
        page.edit();

        List<AddressCorresponGroup> acgs = c.getCcAddressCorresponGroups();
        assertNotNull(acgs);
        assertEquals(1, acgs.size());

        AddressCorresponGroup acg = acgs.get(0);
        assertEquals(AddressType.CC, acg.getAddressType());
        assertEquals(UpdateMode.NEW, acg.getMode());

        CorresponGroup cg = acg.getCorresponGroup();
        assertEquals(100L, cg.getId().longValue());

        List<AddressUser> aus = acg.getUsers();
        assertNotNull(aus);
        assertEquals(2, aus.size());

        AddressUser au1 = aus.get(0);
        assertEquals("ZZA01", au1.getUser().getEmpNo());
        assertEquals(AddressUserType.NORMAL_USER, au1.getAddressUserType());

        AddressUser au2 = aus.get(1);
        assertEquals("ZZA02", au2.getUser().getEmpNo());
        assertEquals(AddressUserType.NORMAL_USER, au2.getAddressUserType());

        assertNull(page.getToAddressValues());
    }

    /**
     * {@link CorresponEditPage#deleteAll()}のテスト.
     * 新規登録時に宛先(To)に何もセットしていない状態での削除の検証.
     * @throws Exception
     */
    @Test
    public void testDeleteAllNewTo() throws Exception {
        Correspon c = new Correspon();
        page.setCorrespon(c);

        try {
            // 全て削除
            page.setDetectedAddressType(AddressType.TO.getValue());
            page.deleteAll();
        } catch (Exception e) {
            fail("例外が発生");
        }
        assertNull(page.getToAddressValues());
    }

    /**
     * {@link CorresponEditPage#deleteAll()}のテスト.
     * 新規登録時に宛先(CC)に何もセットしていない状態での削除の検証.
     * @throws Exception
     */
    @Test
    public void testDeleteAllNewCc() throws Exception {
        Correspon c = new Correspon();
        page.setCorrespon(c);

        try {
            // 全て削除
            page.setDetectedAddressType(AddressType.CC.getValue());
            page.deleteAll();
        } catch (Exception e) {
            fail("例外が発生");
        }
        assertNull(page.getToAddressValues());
    }

    /**
     * {@link CorresponEditPage#deleteAll()}のテスト.
     * 不正なAddressTypeが渡された場合の検証.
     * @throws Exception
     */
    @Test
    public void testDeleteAllInvalidAddressType() throws Exception {
        Correspon c = new Correspon();
        page.setCorrespon(c);

        try {
            // 全て削除
            page.setDetectedAddressType(3);
            page.deleteAll();
        } catch (Exception e) {
            fail("例外が発生");
        }
    }

    /**
     * {@link CorresponEditPage#delete()}のテスト.
     * 不正なAddressTypeが渡された場合の検証.
     * @throws Exception
     */
    @Test
    public void testDeleteInvalidAddressType() throws Exception {
        Correspon c = new Correspon();
        page.setCorrespon(c);

        try {
            // 全て削除
            page.setDetectedAddressType(3);
            page.delete();
        } catch (Exception e) {
            fail("例外が発生");
        }
    }

    /**
     * {@link CorresponEditPage#edit()}のテスト.
     * 不正なAddressTypeが渡された場合の検証.
     * @throws Exception
     */
    @Test
    public void testEditInvalidAddressType() throws Exception {
        Correspon c = new Correspon();
        page.setCorrespon(c);

        try {
            // 全て削除
            page.setDetectedAddressType(3);
            page.edit();
        } catch (Exception e) {
            fail("例外が発生");
        }
    }

    /**
     * {@link CorresponEditPage#delete()}のテスト.
     * 不正な行番号が渡された場合の検証.
     * @throws Exception
     */
    @Test
    public void testDeleteToInvalidAddressIndex() throws Exception {
        // テストデータ
        Correspon c = new Correspon();
        c.setId(1L);
        c.setAddressCorresponGroups(createUpAddressValues(1L));
        page.setCorrespon(c);

        try {
            // 全て削除
            page.setDetectedAddressType(AddressType.TO.getValue());
            page.setDetectedAddressIndex(100);
            page.delete();
        } catch (Exception e) {
            fail("例外が発生");
        }
        assertNotNull(page.getToAddressValues());
    }

    /**
     * {@link CorresponEditPage#delete()}のテスト.
     * 不正な行番号が渡された場合の検証.
     * @throws Exception
     */
    @Test
    public void testDeleteCcInvalidAddressIndex() throws Exception {
        // テストデータ
        Correspon c = new Correspon();
        c.setId(1L);
        c.setAddressCorresponGroups(createUpAddressValues(1L));
        page.setCorrespon(c);

        try {
            // 全て削除
            page.setDetectedAddressType(AddressType.CC.getValue());
            page.setDetectedAddressIndex(100);
            page.delete();
        } catch (Exception e) {
            fail("例外が発生");
        }
    }

    /**
     * 宛先編集・削除の検証.
     * 1．TO:追加
     * 2．TO:２行目編集
     * 3．TO:１行目を削除
     * 4．CC:１行目を削除
     * 5．CC:１行目を編集
     * 6．CC:追加
     * 7．TO:全て削除
     * 8．TO:全て削除
     * @throws Exception
     */
    @Test
    public void testEditUpdate() throws Exception {
        // テストデータ
        Correspon c = new Correspon();
        c.setId(1L);
        c.setAddressCorresponGroups(createUpAddressValues(1L));
        page.setCorrespon(c);

        // TO:３つ目追加
        page.setDetectedAddressType(AddressType.TO.getValue());
        page.setDetectedAddressIndex(-1); // 追加
        page.setCorresponGroupId(5L);
        page.setAddressUserValues("ZZA05-5");
        page.edit();

        List<AddressCorresponGroup> acgs = c.getToAddressCorresponGroups();
        assertNotNull(acgs);
        assertEquals(3, acgs.size());

        AddressCorresponGroup acg = acgs.get(2);
        assertEquals(AddressType.TO, acg.getAddressType());
        assertEquals(UpdateMode.NEW, acg.getMode());

        CorresponGroup cg = acg.getCorresponGroup();
        assertEquals(5L, cg.getId().longValue());

        List<AddressUser> aus = acg.getUsers();
        assertNotNull(aus);
        assertEquals(1, aus.size());

        AddressUser au = aus.get(0);
        assertEquals("ZZA05", au.getUser().getEmpNo());
        assertEquals(AddressUserType.ATTENTION, au.getAddressUserType());

        assertNotNull(page.getToAddressValues());

        // TO:２つ目編集
        page.setDetectedAddressType(AddressType.TO.getValue());
        page.setDetectedAddressIndex(1); // 追加
        page.setCorresponGroupId(2L);
        page.setAddressUserValues("ZZA04-2,ZZA06-2");
        page.edit();

        acgs = c.getToAddressCorresponGroups();
        assertNotNull(acgs);
        assertEquals(3, acgs.size());

        acg = acgs.get(1);
        assertEquals(AddressType.TO, acg.getAddressType());
        assertEquals(UpdateMode.UPDATE, acg.getMode());

        cg = acg.getCorresponGroup();
        assertEquals(2L, cg.getId().longValue());

        aus = acg.getUsers();
        assertNotNull(aus);
        assertEquals(2, aus.size());

        au = aus.get(0);
        assertEquals("ZZA04", au.getUser().getEmpNo());
        assertEquals(AddressUserType.ATTENTION, au.getAddressUserType());

        au = aus.get(1);
        assertEquals("ZZA06", au.getUser().getEmpNo());
        assertEquals(AddressUserType.ATTENTION, au.getAddressUserType());

        assertNotNull(page.getToAddressValues());

        // TO:１つ目を削除
        page.setDetectedAddressType(AddressType.TO.getValue());
        page.setDetectedAddressIndex(0); // 編集
        page.delete();

        acgs = c.getAddressCorresponGroups();
        assertNotNull(acgs);
        assertEquals(5, acgs.size());
        assertEquals(2, c.getToAddressCorresponGroups().size());
        assertEquals(2, c.getCcAddressCorresponGroups().size());

        acg = acgs.get(0); // 削除された行
        assertEquals(AddressType.TO, acg.getAddressType());
        assertEquals(UpdateMode.DELETE, acg.getMode());

        acg = acgs.get(1); // 更新された行
        assertEquals(AddressType.TO, acg.getAddressType());
        assertEquals(UpdateMode.UPDATE, acg.getMode());

        acg = acgs.get(2);
        assertEquals(AddressType.CC, acg.getAddressType());
        assertEquals(UpdateMode.NONE, acg.getMode());

        acg = acgs.get(3);
        assertEquals(AddressType.CC, acg.getAddressType());
        assertEquals(UpdateMode.NONE, acg.getMode());

        acg = acgs.get(4); // 追加された行
        assertEquals(AddressType.TO, acg.getAddressType());
        assertEquals(UpdateMode.NEW, acg.getMode());

        // CC:１つ目を削除
        page.setDetectedAddressType(AddressType.CC.getValue());
        page.setDetectedAddressIndex(0); // 追加
        page.delete();

        acgs = c.getAddressCorresponGroups();
        assertNotNull(acgs);
        assertEquals(5, acgs.size());
        assertEquals(2, c.getToAddressCorresponGroups().size());
        assertEquals(1, c.getCcAddressCorresponGroups().size());

        acg = acgs.get(0); // 削除された行
        assertEquals(AddressType.TO, acg.getAddressType());
        assertEquals(UpdateMode.DELETE, acg.getMode());

        acg = acgs.get(1); // 更新された行
        assertEquals(AddressType.TO, acg.getAddressType());
        assertEquals(UpdateMode.UPDATE, acg.getMode());

        acg = acgs.get(2); // 削除された行
        assertEquals(AddressType.CC, acg.getAddressType());
        assertEquals(UpdateMode.DELETE, acg.getMode());

        acg = acgs.get(3);
        assertEquals(AddressType.CC, acg.getAddressType());
        assertEquals(UpdateMode.NONE, acg.getMode());

        acg = acgs.get(4); // 追加された行
        assertEquals(AddressType.TO, acg.getAddressType());
        assertEquals(UpdateMode.NEW, acg.getMode());

        // CC:１つ目を編集（削除された行以外で先頭のCC）
        page.setDetectedAddressType(AddressType.CC.getValue());
        page.setDetectedAddressIndex(0); // 追加
        page.setCorresponGroupId(3L);
        page.setAddressUserValues("ZZA12-3,ZZA15-3");
        page.edit();

        acgs = c.getCcAddressCorresponGroups();
        assertNotNull(acgs);
        assertEquals(1, acgs.size());

        acg = acgs.get(0);
        assertEquals(AddressType.CC, acg.getAddressType());
        assertEquals(UpdateMode.UPDATE, acg.getMode());

        cg = acg.getCorresponGroup();
        assertEquals(3L, cg.getId().longValue());

        aus = acg.getUsers();
        assertNotNull(aus);
        assertEquals(2, aus.size());

        au = aus.get(0);
        assertEquals("ZZA12", au.getUser().getEmpNo());
        assertEquals(AddressUserType.NORMAL_USER, au.getAddressUserType());

        au = aus.get(1);
        assertEquals("ZZA15", au.getUser().getEmpNo());
        assertEquals(AddressUserType.NORMAL_USER, au.getAddressUserType());

        acgs = c.getAddressCorresponGroups();
        assertNotNull(acgs);
        assertEquals(5, acgs.size());

        acg = acgs.get(0); // 削除された行
        assertEquals(AddressType.TO, acg.getAddressType());
        assertEquals(UpdateMode.DELETE, acg.getMode());

        acg = acgs.get(1); // 更新された行
        assertEquals(AddressType.TO, acg.getAddressType());
        assertEquals(UpdateMode.UPDATE, acg.getMode());

        acg = acgs.get(2); // 削除された行
        assertEquals(AddressType.CC, acg.getAddressType());
        assertEquals(UpdateMode.DELETE, acg.getMode());

        acg = acgs.get(3); // 更新された行
        assertEquals(AddressType.CC, acg.getAddressType());
        assertEquals(UpdateMode.UPDATE, acg.getMode());

        acg = acgs.get(4); // 追加された行
        assertEquals(AddressType.TO, acg.getAddressType());
        assertEquals(UpdateMode.NEW, acg.getMode());

        // CC:２つ目追加
        page.setDetectedAddressType(AddressType.CC.getValue());
        page.setDetectedAddressIndex(-1); // 追加
        page.setCorresponGroupId(6L);
        page.setAddressUserValues("ZZA20-6,ZZA21-6");
        page.edit();

        acgs = c.getCcAddressCorresponGroups();
        assertNotNull(acgs);
        assertEquals(2, acgs.size());

        acg = acgs.get(1);
        assertEquals(AddressType.CC, acg.getAddressType());
        assertEquals(UpdateMode.NEW, acg.getMode());

        cg = acg.getCorresponGroup();
        assertEquals(6L, cg.getId().longValue());

        aus = acg.getUsers();
        assertNotNull(aus);
        assertEquals(2, aus.size());

        au = aus.get(0);
        assertEquals("ZZA20", au.getUser().getEmpNo());
        assertEquals(AddressUserType.NORMAL_USER, au.getAddressUserType());

        au = aus.get(1);
        assertEquals("ZZA21", au.getUser().getEmpNo());
        assertEquals(AddressUserType.NORMAL_USER, au.getAddressUserType());

        // 全て削除
        page.setDetectedAddressType(AddressType.TO.getValue());
        page.deleteAll();

        acgs = c.getAddressCorresponGroups();
        assertEquals(6, acgs.size());
        assertEquals(0, c.getToAddressCorresponGroups().size());
        assertEquals(2, c.getCcAddressCorresponGroups().size());

        acg = acgs.get(0); // 削除された行
        assertEquals(AddressType.TO, acg.getAddressType());
        assertEquals(UpdateMode.DELETE, acg.getMode());

        acg = acgs.get(1); // 削除された行
        assertEquals(AddressType.TO, acg.getAddressType());
        assertEquals(UpdateMode.DELETE, acg.getMode());

        acg = acgs.get(2); // 削除された行
        assertEquals(AddressType.CC, acg.getAddressType());
        assertEquals(UpdateMode.DELETE, acg.getMode());

        acg = acgs.get(3); // 更新された行
        assertEquals(AddressType.CC, acg.getAddressType());
        assertEquals(UpdateMode.UPDATE, acg.getMode());

        acg = acgs.get(4); // 削除された行
        assertEquals(AddressType.TO, acg.getAddressType());
        assertEquals(UpdateMode.DELETE, acg.getMode());

        acg = acgs.get(5); // 追加された行
        assertEquals(AddressType.CC, acg.getAddressType());
        assertEquals(UpdateMode.NEW, acg.getMode());
        assertNull(page.getToAddressValues());

        // 全て削除
        page.setDetectedAddressType(AddressType.CC.getValue());
        page.deleteAll();

        acgs = c.getAddressCorresponGroups();
        assertEquals(6, acgs.size());
        assertEquals(0, c.getToAddressCorresponGroups().size());
        assertEquals(0, c.getCcAddressCorresponGroups().size());

        acg = acgs.get(0); // 削除された行
        assertEquals(AddressType.TO, acg.getAddressType());
        assertEquals(UpdateMode.DELETE, acg.getMode());

        acg = acgs.get(1); // 削除された行
        assertEquals(AddressType.TO, acg.getAddressType());
        assertEquals(UpdateMode.DELETE, acg.getMode());

        acg = acgs.get(2); // 削除された行
        assertEquals(AddressType.CC, acg.getAddressType());
        assertEquals(UpdateMode.DELETE, acg.getMode());

        acg = acgs.get(3); // 削除された行
        assertEquals(AddressType.CC, acg.getAddressType());
        assertEquals(UpdateMode.DELETE, acg.getMode());

        acg = acgs.get(4); // 削除された行
        assertEquals(AddressType.TO, acg.getAddressType());
        assertEquals(UpdateMode.DELETE, acg.getMode());

        acg = acgs.get(5); // 削除された行
        assertEquals(AddressType.CC, acg.getAddressType());
        assertEquals(UpdateMode.DELETE, acg.getMode());
        assertNull(page.getToAddressValues());
    }

    private void validateInputValues() {
        Correspon actual = page.getCorrespon();
        assertEquals(page.getFrom(), actual.getFromCorresponGroup().getId());
        assertEquals(page.getCorresponStatus(), actual.getCorresponStatus().getValue());
        assertEquals(page.getType(), actual.getCorresponType().getProjectCorresponTypeId());
        assertEquals(page.getSubject(), actual.getSubject());
        assertEquals(page.getBody(), actual.getBody());
        assertEquals(page.getReplyRequired(), actual.getReplyRequired().getValue());
        if (page.getDeadlineForReply() != null && actual.getDeadlineForReply() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
            assertEquals(page.getDeadlineForReply(), sdf.format(actual.getDeadlineForReply()));
        }

        assertEquals(page.getCustomField1Value(), actual.getCustomField1Value());
        assertEquals(page.getCustomField2Value(), actual.getCustomField2Value());
        assertEquals(page.getCustomField3Value(), actual.getCustomField3Value());
        assertEquals(page.getCustomField4Value(), actual.getCustomField4Value());
        assertEquals(page.getCustomField5Value(), actual.getCustomField5Value());
        assertEquals(page.getCustomField6Value(), actual.getCustomField6Value());
        assertEquals(page.getCustomField7Value(), actual.getCustomField7Value());
        assertEquals(page.getCustomField8Value(), actual.getCustomField8Value());
        assertEquals(page.getCustomField9Value(), actual.getCustomField9Value());
        assertEquals(page.getCustomField10Value(), actual.getCustomField10Value());
    }

    private void setUpValidInput() {

        setUpInputValues();

        Correspon c = new Correspon();

        page.setCorrespon(c);

        //  入力値のシミュレート
        page.setFrom(1L);
        page.setCorresponStatus(CorresponStatus.CLOSED.getValue());
        page.setType(2L);
        page.setSubject("test subject");
        page.setBody("<p>test body</p>\n");
        page.setReplyRequired(3);
        page.setDeadlineForReply("13-Jul-2009");

        //  宛先
        setUpValidAddressInput(page);


        //  カスタムフィールド
        page.setCustomField1Value("c1");
        page.setCustomField2Value("c2");
        page.setCustomField3Value("c3");
        page.setCustomField4Value("c4");
        page.setCustomField5Value("c5");
        page.setCustomField6Value("c6");
        page.setCustomField7Value("c7");
        page.setCustomField8Value("c8");
        page.setCustomField9Value("c9");
        page.setCustomField10Value("c10");
    }

    private void setUpValidAddressInput(CorresponEditPage page) {
        List<AddressCorresponGroup> to = new ArrayList<AddressCorresponGroup>();
        List<AddressCorresponGroup> cc = new ArrayList<AddressCorresponGroup>();
        List<AddressCorresponGroup> toRemoved = new ArrayList<AddressCorresponGroup>();
        List<AddressCorresponGroup> ccRemoved = new ArrayList<AddressCorresponGroup>();
        AddressCorresponGroup ag;
        CorresponGroup g;
        List<AddressUser> users;
        AddressUser au;
        User u;

        //  To
        ag = new AddressCorresponGroup();
        ag.setId(null);
        ag.setAddressType(AddressType.TO);
        ag.setMode(UpdateMode.NEW);
        g = new CorresponGroup();
        g.setId(111L);
        g.setName("YOC:IT");
        ag.setCorresponGroup(g);

        users = new ArrayList<AddressUser>();
        au = new AddressUser();
        au.setId(null);
        au.setAddressUserType(AddressUserType.ATTENTION);
        u = new User();
        u.setEmpNo("00001");
        u.setNameE("User1");
        au.setUser(u);
        users.add(au);

        ag.setUsers(users);
        to.add(ag);

        //  Cc
        ag = new AddressCorresponGroup();
        ag.setId(null);
        ag.setAddressType(AddressType.CC);
        ag.setMode(UpdateMode.NONE);
        g = new CorresponGroup();
        g.setId(222L);
        g.setName("YOC:IT");
        ag.setCorresponGroup(g);

        users = new ArrayList<AddressUser>();
        au = new AddressUser();
        au.setId(null);
        au.setAddressUserType(AddressUserType.NORMAL_USER);
        u = new User();
        u.setEmpNo("00001");
        u.setNameE("User1");
        au.setUser(u);
        users.add(au);

        ag.setUsers(users);
        cc.add(ag);

        //  To (Removed)
        ag = new AddressCorresponGroup();
        ag.setId(null);
        ag.setAddressType(AddressType.TO);
        ag.setMode(UpdateMode.DELETE);
        g = new CorresponGroup();
        g.setId(333L);
        g.setName("YOC:IT");
        ag.setCorresponGroup(g);

        users = new ArrayList<AddressUser>();
        au = new AddressUser();
        au.setId(null);
        au.setAddressUserType(AddressUserType.ATTENTION);
        u = new User();
        u.setEmpNo("00001");
        u.setNameE("User1");
        au.setUser(u);
        users.add(au);

        ag.setUsers(users);
        toRemoved.add(ag);

        //  Cc (Removed)
        ag = new AddressCorresponGroup();
        ag.setId(null);
        ag.setAddressType(AddressType.CC);
        ag.setMode(UpdateMode.NONE);
        g = new CorresponGroup();
        g.setId(444L);
        g.setName("YOC:IT");
        ag.setCorresponGroup(g);

        users = new ArrayList<AddressUser>();
        au = new AddressUser();
        au.setId(null);
        au.setAddressUserType(AddressUserType.NORMAL_USER);
        u = new User();
        u.setEmpNo("00001");
        u.setNameE("User1");
        au.setUser(u);
        users.add(au);

        ag.setUsers(users);
        ccRemoved.add(ag);

        List<AddressCorresponGroup> addresses = new ArrayList<>();
        addresses.addAll(to);
        addresses.addAll(cc);

        page.getCorrespon().setAddressCorresponGroups(addresses);
        page.setAddressUserValues("test");
    }

    private void setUpInputValues() {
        //  From
        List<CorresponGroup> from = new ArrayList<CorresponGroup>();
        CorresponGroup g;
        g = new CorresponGroup();
        g.setId(2L);
        g.setName("Group2");
        from.add(g);

        g = new CorresponGroup();
        g.setId(1L);
        g.setName("Group1");
        from.add(g);

        page.setCorresponGroups(from);

        // 文書状態
        page.createSelectCorresponStatus(CorresponStatus.values());

        List<CorresponType> corresponTypes = new ArrayList<CorresponType>();
        CorresponType type;
        type = new CorresponType();
        type.setId(11L);
        type.setProjectCorresponTypeId(1L);
        type.setName("Type1");
        corresponTypes.add(type);

        type = new CorresponType();
        type.setId(22L);
        type.setProjectCorresponTypeId(2L);
        type.setName("Type2");
        corresponTypes.add(type);

        // コレポン文書種別
        page.setCorresponType(corresponTypes);
        page.createSelectCorresponType();

        //  返信要否
        page.createSelectReplyRequired(ReplyRequired.values());
    }

    private List<AddressCorresponGroup> createUpAddressValues(long corresponId) {
        List<AddressCorresponGroup> acgs = new ArrayList<AddressCorresponGroup>();

        // TO:１つ目
        AddressCorresponGroup acg = new AddressCorresponGroup();
        acg.setId(10L);
        acg.setCorresponId(corresponId);
        acg.setCorresponGroup(new CorresponGroup());
        acg.getCorresponGroup().setId(1L);
        acg.setAddressType(AddressType.TO);
        acgs.add(acg);

        List<AddressUser> aus = new ArrayList<AddressUser>();
        acg.setUsers(aus);

        AddressUser au = new AddressUser();
        au.setId(1000L);
        au.setAddressUserType(AddressUserType.ATTENTION);
        au.setUser(new User());
        au.getUser().setEmpNo("ZZA01");
        aus.add(au);

        au.setId(1001L);
        au.setAddressUserType(AddressUserType.ATTENTION);
        au.setUser(new User());
        au.getUser().setEmpNo("ZZA02");
        aus.add(au);

        // TO:２つ目
        acg = new AddressCorresponGroup();
        acg.setId(11L);
        acg.setCorresponId(corresponId);
        acg.setCorresponGroup(new CorresponGroup());
        acg.getCorresponGroup().setId(2L);
        acg.setAddressType(AddressType.TO);
        acgs.add(acg);

        aus = new ArrayList<AddressUser>();
        acg.setUsers(aus);

        au = new AddressUser();
        au.setId(1002L);
        au.setAddressUserType(AddressUserType.ATTENTION);
        au.setUser(new User());
        au.getUser().setEmpNo("ZZA03");
        aus.add(au);

        au.setId(1003L);
        au.setAddressUserType(AddressUserType.ATTENTION);
        au.setUser(new User());
        au.getUser().setEmpNo("ZZA04");
        aus.add(au);

        // CC:１つ目
        acg = new AddressCorresponGroup();
        acg.setId(12L);
        acg.setCorresponId(corresponId);
        acg.setCorresponGroup(new CorresponGroup());
        acg.getCorresponGroup().setId(3L);
        acg.setAddressType(AddressType.CC);
        acgs.add(acg);

        aus = new ArrayList<AddressUser>();
        acg.setUsers(aus);

        au = new AddressUser();
        au.setId(1004L);
        au.setAddressUserType(AddressUserType.NORMAL_USER);
        au.setUser(new User());
        au.getUser().setEmpNo("ZZA11");
        aus.add(au);

        au.setId(1005L);
        au.setAddressUserType(AddressUserType.NORMAL_USER);
        au.setUser(new User());
        au.getUser().setEmpNo("ZZA12");
        aus.add(au);

        // CC:２つ目
        acg = new AddressCorresponGroup();
        acg.setId(13L);
        acg.setCorresponId(corresponId);
        acg.setCorresponGroup(new CorresponGroup());
        acg.getCorresponGroup().setId(4L);
        acg.setAddressType(AddressType.CC);
        acgs.add(acg);

        aus = new ArrayList<AddressUser>();
        acg.setUsers(aus);

        au = new AddressUser();
        au.setId(1006L);
        au.setAddressUserType(AddressUserType.NORMAL_USER);
        au.setUser(new User());
        au.getUser().setEmpNo("ZZA13");
        aus.add(au);

        au.setId(1007L);
        au.setAddressUserType(AddressUserType.NORMAL_USER);
        au.setUser(new User());
        au.getUser().setEmpNo("ZZA14");
        aus.add(au);

        return acgs;
    }

    public static class MockCustomFieldService extends MockUp<CustomFieldServiceImpl> {
        static List<CustomField> RET_FIND;
        static List<CustomFieldValue> RET_FIND_VALUE;

        @Mock
        public List<CustomField> search(SearchCustomFieldCondition cust)
            throws ServiceAbortException {
            return RET_FIND;
        }

        @Mock
        public List<CustomFieldValue> findCustomFieldValue(Long id) throws ServiceAbortException {
            return RET_FIND_VALUE;
        }
    }

    public static class MockCorresponGroupService extends MockUp<CorresponGroupServiceImpl> {
        static List<CorresponGroup> RET_FIND;

        @Mock
        public List<CorresponGroup> search(SearchCorresponGroupCondition scgc)
            throws ServiceAbortException {
            return RET_FIND;
        }

        @Mock
        public CorresponGroup find(Long id) {
            CorresponGroup cg = new CorresponGroup();
            cg.setId(id);
            return cg;
        }

    }

    public static class MockCorresponTypeService extends MockUp<CorresponTypeServiceImpl> {
        static List<CorresponType> RET_FIND;

        @Mock
        public List<CorresponType> search(SearchCorresponTypeCondition condition)
            throws ServiceAbortException {
            return RET_FIND;
        }

    }

    public static class MockUserService extends MockUp<UserServiceImpl> {
        static List<ProjectUser> RET_SEARCH;
        static List<CorresponGroupUser> RET_SEARCH_CORRESPON_GROUP;

        @Mock
        public List<ProjectUser> search(SearchUserCondition conditionUser)
            throws ServiceAbortException {
            return RET_SEARCH;
        }

        @Mock
        public List<CorresponGroupUser> searchCorrseponGroup(String projectId, String empNo)
            throws ServiceAbortException {

            return RET_SEARCH_CORRESPON_GROUP;
        }

        @Mock
        public User findByEmpNo(String empNo) {
            User u = new User();
            u.setEmpNo(empNo);
            return u;
        }

    }

    public static class MockCorresponService extends MockUp<CorresponServiceImpl> {
        static Correspon RET_FIND;
        static ServiceAbortException EX_FIND_BY_ID;

        @Mock
        public Correspon find(Long id) throws ServiceAbortException {
            if (RET_FIND == null) {
                throw EX_FIND_BY_ID;
            }
            return RET_FIND;
        }

        @Mock
        public Attachment findAttachment(Long corresponId, Long attachmentId) {
            return new Attachment();
        }

        @Mock
        public void adjustCustomFields(Correspon c) throws ServiceAbortException {
            List<CustomField> master = MockCustomFieldService.RET_FIND;

            int i = 0;
            if (i < master.size()) {
                c.setCustomField1Id(master.get(i).getProjectCustomFieldId());
                c.setCustomField1Label(master.get(i).getLabel());
                i++;
            }
            if (i < master.size()) {
                c.setCustomField2Id(master.get(i).getProjectCustomFieldId());
                c.setCustomField2Label(master.get(i).getLabel());
                i++;
            }
            if (i < master.size()) {
                c.setCustomField3Id(master.get(i).getProjectCustomFieldId());
                c.setCustomField3Label(master.get(i).getLabel());
                i++;
            }
            if (i < master.size()) {
                c.setCustomField4Id(master.get(i).getProjectCustomFieldId());
                c.setCustomField4Label(master.get(i).getLabel());
                i++;
            }
            if (i < master.size()) {
                c.setCustomField5Id(master.get(i).getProjectCustomFieldId());
                c.setCustomField5Label(master.get(i).getLabel());
                i++;
            }
            if (i < master.size()) {
                c.setCustomField6Id(master.get(i).getProjectCustomFieldId());
                c.setCustomField6Label(master.get(i).getLabel());
                i++;
            }
            if (i < master.size()) {
                c.setCustomField7Id(master.get(i).getProjectCustomFieldId());
                c.setCustomField7Label(master.get(i).getLabel());
                i++;
            }
            if (i < master.size()) {
                c.setCustomField8Id(master.get(i).getProjectCustomFieldId());
                c.setCustomField8Label(master.get(i).getLabel());
                i++;
            }
            if (i < master.size()) {
                c.setCustomField9Id(master.get(i).getProjectCustomFieldId());
                c.setCustomField9Label(master.get(i).getLabel());
                i++;
            }
            if (i < master.size()) {
                c.setCustomField10Id(master.get(i).getProjectCustomFieldId());
                c.setCustomField10Label(master.get(i).getLabel());
                i++;
            }
        }
    }

    public static class MockCorresponValidateService extends MockUp<CorresponValidateServiceImpl> {
        static ServiceAbortException EX_VALIDATE;

        @Mock
        public boolean validate(Correspon correspon) throws ServiceAbortException {
            if (EX_VALIDATE != null) {
                throw EX_VALIDATE;
            }
            return true;
        }
    }

    public static class MockDistributionTemplateService extends MockUp<DistributionTemplateServiceImpl> {
        @Mock
        public List<DistTemplateHeader> findDistTemplateList(String projectId) {
            return new ArrayList<>();
        }
    }

    public static class MockAbstractPage extends MockUp<AbstractPage> {
        static Project RET_PROJECT;
        static String RET_PROJID;
        static User RET_USER;
        static ProjectUser RET_PROJECT_USER;
        static boolean IS_SYSTEM_ADMIN;
        static boolean IS_PROJECT_ADMIN;

        @Mock
        public User getCurrentUser() {
            return RET_USER;
        }

        @Mock
        public ProjectUser getCurrentProjectUser() {
            return RET_PROJECT_USER;
        }

        @Mock
        public Project getCurrentProject() {
            return RET_PROJECT;
        }

        @Mock
        public String getCurrentProjectId() {
            if (RET_PROJID != null) {
                return RET_PROJID;
            }
            if (RET_PROJECT != null) {
                return RET_PROJECT.getProjectId();
            }
            return null;
        }

        @Mock
        public boolean isSystemAdmin() {
            return IS_SYSTEM_ADMIN;
        }

        @Mock
        public boolean isProjectAdmin(String projectId) {
            return IS_PROJECT_ADMIN;
        }
    }
}
