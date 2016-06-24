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
package jp.co.opentone.bsol.linkbinder.view.correspon.strategy;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import jp.co.opentone.bsol.linkbinder.attachment.AttachmentInfo;
import jp.co.opentone.bsol.linkbinder.attachment.CopiedAttachmentInfo;
import jp.co.opentone.bsol.linkbinder.dto.AddressCorresponGroup;
import jp.co.opentone.bsol.linkbinder.dto.AddressUser;
import jp.co.opentone.bsol.linkbinder.dto.Attachment;
import jp.co.opentone.bsol.linkbinder.dto.Correspon;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroup;
import jp.co.opentone.bsol.linkbinder.dto.CorresponType;
import jp.co.opentone.bsol.linkbinder.dto.PersonInCharge;
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
import jp.co.opentone.bsol.linkbinder.view.correspon.CorresponEditPage;
import jp.co.opentone.bsol.linkbinder.view.correspon.util.CorresponEditMode;


/**
 * {@link ReplyWithPreviousCorresponSetupStrategy}のテストケース.
 * @author opentone
 */
public class ReplyWithPreviousCorresponSetupStrategyTest extends AbstractCorresponSetupStrategyTestCase {
    /** 依頼文書. */
    private Correspon correspon;
    /** 引用する返信済文書. */
    private Correspon replied;
    /** 返信済文書を作成するためのページ情報. */
    @Resource
    private CorresponEditPage repliedPage;

    public ReplyWithPreviousCorresponSetupStrategyTest() {
        super(CorresponEditMode.REPLY_WITH_PREVIOUS_CORRESPON);
    }

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();

        MockCorresponService.RET_FIND = null;
        //  依頼文書、返信済文書を用意
        Correspon request = setupCorrespon();
        MockCorresponService.RET_CORRESPON_MAP.put(request.getId(), request);

        CorresponSetupStrategy s =
            CorresponSetupStrategy.getCorresponSetupStrategy(repliedPage, CorresponEditMode.REPLY);
        repliedPage.setId(request.getId());
        s.setup();

        replied = repliedPage.getCorrespon();
        //  発行済にエミュレート
        replied.setId(123L);
        replied.setWorkflowStatus(WorkflowStatus.ISSUED);
        replied.setCorresponNo("YOC:IT-00001-012");
        replied.setCorresponStatus(CorresponStatus.OPEN);
        replied.setReplyRequired(ReplyRequired.YES);
        replied.setCustomField1Value("replied value");
        setUpAttachment(replied);

        correspon = setupCorrespon();
        MockCorresponService.RET_CORRESPON_MAP.put(correspon.getId(), correspon);
        MockCorresponService.RET_CORRESPON_MAP.put(replied.getId(), replied);

        page.setId(correspon.getId());
        page.setRepliedId(replied.getId());
    }

    @Override
    @After
    public void teardown() {
        MockCorresponService.RET_FIND = null;
        MockCorresponService.RET_CORRESPON_MAP.clear();

        page.getAttachments().clear();

        MockAbstractPage.IS_SYSTEM_ADMIN = false;
        MockAbstractPage.IS_PROJECT_ADMIN = false;
        MockAbstractPage.IS_ANY_GROUP_ADMIN = false;

        super.teardown();
    }

    private Correspon setupCorrespon() {
        Correspon c = new Correspon();

        c.setId(10L);
        c.setProjectId(currentProject.getProjectId());
        c.setCorresponNo("YOC:IT-00001-001");
        c.setParentCorresponId(20L);
        c.setParentCorresponNo("YOC:IT-00001");
        c.setPreviousRevCorresponId(30L);
        c.setPreviousRevCorresponNo("YOC:IT-00000");

        CorresponGroup from = new CorresponGroup();
        from.setId(100L);
        from.setName("YOC:IT");
        c.setFromCorresponGroup(from);

        c.setCorresponStatus(CorresponStatus.OPEN);
        c.setWorkflowStatus(WorkflowStatus.ISSUED);
        c.setIssuedAt(new GregorianCalendar(2009, 7, 14).getTime());

        setUpAddress(c);

        CorresponType corresponType = new CorresponType();
        corresponType.setId(1L);
        corresponType.setProjectCorresponTypeId(11L);
        corresponType.setName("Query");
        c.setCorresponType(corresponType);

        c.setSubject("This is test.");
        c.setBody("<p>This is first line.</p><p>This is second line.</p>");

        c.setReplyRequired(ReplyRequired.YES);
        c.setDeadlineForReply(new GregorianCalendar(2009, 7, 14).getTime());

        setUpAttachment(c);
        setUpCustomField(c);

        //  Preparer, Checker, Approver
        User u;
        u = new User();
        u.setEmpNo("90001");
        u.setNameE("Preparer");
        c.setCreatedBy(u);

        List<Workflow> workflows = new ArrayList<Workflow>();
        Workflow w;
        w = new Workflow();
        w.setId(1L);
        w.setCorresponId(c.getId());
        w.setWorkflowType(WorkflowType.CHECKER);
        w.setWorkflowProcessStatus(WorkflowProcessStatus.CHECKED);
        u = new User();
        u.setEmpNo("90001");
        u.setNameE("Checker1");
        w.setUser(u);
        workflows.add(w);

        w = new Workflow();
        w.setId(2L);
        w.setCorresponId(c.getId());
        w.setWorkflowType(WorkflowType.APPROVER);
        w.setWorkflowProcessStatus(WorkflowProcessStatus.APPROVED);
        u = new User();
        u.setEmpNo("90002");
        u.setNameE("Approver");
        w.setUser(u);
        workflows.add(w);

        c.setWorkflows(workflows);

        return c;
    }

    private void setUpAddress(Correspon c) {
        AddressCorresponGroup ag;
        CorresponGroup g;
        List<AddressUser> users;
        AddressUser au;
        List<PersonInCharge> pics;
        PersonInCharge pic;
        User u;

        List<AddressCorresponGroup> addresses = new ArrayList<AddressCorresponGroup>();
        ag = new AddressCorresponGroup();
        ag.setId(1L);
        ag.setMode(UpdateMode.NONE);
        ag.setAddressType(AddressType.TO);
        g = new CorresponGroup();
        g.setId(11L);
        g.setName("Group1");
        ag.setCorresponGroup(g);

        users = new ArrayList<AddressUser>();
        au = new AddressUser();
        au.setId(111L);
        au.setAddressUserType(AddressUserType.ATTENTION);
        au.setAddressCorresponGroupId(ag.getId());
        u = new User();
        u.setEmpNo("ZZA01");
        u.setNameE("User1");
        au.setUser(u);
        users.add(au);

        //  これを返信ユーザーとして検証する
        au = new AddressUser();
        au.setId(112L);
        au.setAddressUserType(AddressUserType.ATTENTION);
        au.setAddressCorresponGroupId(ag.getId());
        u = new User();
        u.setEmpNo("00002");
        u.setNameE("User2");
        au.setUser(u);
        users.add(au);

        ag.setUsers(users);
        addresses.add(ag);

        ag = new AddressCorresponGroup();
        ag.setId(2L);
        ag.setMode(UpdateMode.NONE);
        ag.setAddressType(AddressType.TO);
        g = new CorresponGroup();
        g.setId(12L);
        g.setName("Group12");
        ag.setCorresponGroup(g);

        users = new ArrayList<AddressUser>();
        au = new AddressUser();
        au.setId(211L);
        au.setAddressUserType(AddressUserType.ATTENTION);
        au.setAddressCorresponGroupId(ag.getId());
        u = new User();
        u.setEmpNo("10001");
        u.setNameE("User11");
        au.setUser(u);
        users.add(au);

        au = new AddressUser();
        au.setId(212L);
        au.setAddressUserType(AddressUserType.ATTENTION);
        au.setAddressCorresponGroupId(ag.getId());
        u = new User();
        u.setEmpNo("10002");
        u.setNameE("User12");
        au.setUser(u);
        pics = new ArrayList<PersonInCharge>();

        //  これを返信ユーザーとして検証する
        pic = new PersonInCharge();
        pic.setId(11111L);
        pic.setAddressUserId(au.getId());
        u = new User();
        u.setEmpNo("11001");
        u.setNameE("PIC1");
        pic.setUser(u);
        pics.add(pic);
        au.setPersonInCharges(pics);

        users.add(au);

        ag.setUsers(users);
        addresses.add(ag);

        ag = new AddressCorresponGroup();
        ag.setId(3L);
        ag.setMode(UpdateMode.NONE);
        ag.setAddressType(AddressType.CC);
        g = new CorresponGroup();
        g.setId(22L);
        g.setName("Group2");
        ag.setCorresponGroup(g);

        users = new ArrayList<AddressUser>();
        //  これを返信ユーザーとして使用する
        au = new AddressUser();
        au.setId(222L);
        au.setAddressUserType(AddressUserType.NORMAL_USER);
        au.setAddressCorresponGroupId(ag.getId());
        u = new User();
        u.setEmpNo("00003");
        u.setNameE("User3");
        au.setUser(u);
        users.add(au);

        au = new AddressUser();
        au.setId(223L);
        au.setAddressUserType(AddressUserType.NORMAL_USER);
        au.setAddressCorresponGroupId(ag.getId());
        u = new User();
        u.setEmpNo("00004");
        u.setNameE("User4");
        au.setUser(u);
        users.add(au);

        ag.setUsers(users);
        addresses.add(ag);

        c.setAddressCorresponGroups(addresses);
    }

    private void setUpAttachment(Correspon c) {
        c.setFile1Id(1L);
        c.setFile1FileId("11111");
        c.setFile1FileName("file1");

        c.setFile2Id(2L);
        c.setFile2FileId("22222");
        c.setFile2FileName("file2");

        c.setFile3Id(3L);
        c.setFile3FileId("33333");
        c.setFile3FileName("file3");

        c.setFile4Id(4L);
        c.setFile4FileId("44444");
        c.setFile4FileName("file4");

        c.setFile5Id(5L);
        c.setFile5FileId("55555");
        c.setFile5FileName("file5");
    }

    private void setUpCustomField(Correspon c) {
        c.setCustomField1Id(10L);
        c.setCustomField1Label("label1");
        c.setCustomField1Value("value1");
        c.setCustomField2Id(20L);
        c.setCustomField2Label("label2");
        c.setCustomField2Value("value2");
        c.setCustomField3Id(30L);
        c.setCustomField3Label("label3");
        c.setCustomField3Value("value3");
        c.setCustomField4Id(40L);
        c.setCustomField4Label("label4");
        c.setCustomField4Value("value4");
        c.setCustomField5Id(50L);
        c.setCustomField5Label("label5");
        c.setCustomField5Value("value5");
        c.setCustomField6Id(60L);
        c.setCustomField6Label("label6");
        c.setCustomField6Value("value6");
        c.setCustomField7Id(70L);
        c.setCustomField7Label("label7");
        c.setCustomField7Value("value7");
        c.setCustomField8Id(80L);
        c.setCustomField8Label("label8");
        c.setCustomField8Value("value8");
        c.setCustomField9Id(90L);
        c.setCustomField9Label("label9");
        c.setCustomField9Value("value9");
        c.setCustomField10Id(100L);
        c.setCustomField10Label("label10");
        c.setCustomField10Value("value10");
    }

    /**
     * {@link CopyCorresponSetupStrategy#setup()}の検証.
     * page#idに指定したコレポン文書のコピーが生成できていることを検証する.
     * @throws Exception
     */
    @Test
    public void testSetupAttention() throws Exception {
        strategy.setup();

        Correspon actual = page.getCorrespon();
        assertNotNull(actual);

        //  返信元ユーザー情報(返信ユーザーは引き継がれない)
        assertNull(actual.getReplyAddressUserId());

        assertEquals(null, actual.getId());
        assertEquals(null, actual.getCorresponNo());
        assertEquals(correspon.getId(), actual.getParentCorresponId());
        assertEquals(correspon.getCorresponNo(), actual.getParentCorresponNo());
        assertEquals(null, actual.getPreviousRevCorresponId());
        assertEquals(null, actual.getPreviousRevCorresponNo());

        // 返信時もfromは引き継がない
        assertNull(actual.getFromCorresponGroup());

        assertEquals(replied.getCorresponStatus(), actual.getCorresponStatus());
        assertEquals(replied.getCorresponType(), actual.getCorresponType());

        // 宛先はやや複雑なので個別に検証
        assertTo(correspon, actual);
        assertEquals(replied.getCcAddressCorresponGroups().size(), actual.getCcAddressCorresponGroups().size());
        //  編集モードは全てNEW
        for (AddressCorresponGroup ag : actual.getAddressCorresponGroups()) {
            assertEquals(UpdateMode.NEW, ag.getMode());
            if (ag.getUsers() != null) {
                for (AddressUser au : ag.getUsers()) {
                    assertTrue(au.getPersonInCharges() == null || au.getPersonInCharges().isEmpty());
                    assertTrue(ag.getReplyCount() == null || ag.getReplyCount() == 0L);
                }
            }
        }

        //  件名、本文は返信済文書の内容がそのままコピーされている
        assertEquals(replied.getSubject(), actual.getSubject());
        assertEquals(replied.getBody(), actual.getBody());

        //  返信要否、返信期限は返信済文書から引き継ぎ
        assertEquals(replied.getReplyRequired(), actual.getReplyRequired());
        assertEquals(replied.getDeadlineForReply(), actual.getDeadlineForReply());

        //  attachments
        //  返信済文書からファイルを引き継ぐ
        //  が、ページに引き継いでいるのでコレポン文書には何も設定していない
        //  このメソッドの最後でページの検証をしている
        assertEquals(0, actual.getAttachments().size());
        //  custom fields
        assertEquals(replied.getCustomField1Value(), actual.getCustomField1Value());
        assertEquals(replied.getCustomField2Value(), actual.getCustomField2Value());
        assertEquals(replied.getCustomField3Value(), actual.getCustomField3Value());
        assertEquals(replied.getCustomField4Value(), actual.getCustomField4Value());
        assertEquals(replied.getCustomField5Value(), actual.getCustomField5Value());
        assertEquals(replied.getCustomField6Value(), actual.getCustomField6Value());
        assertEquals(replied.getCustomField7Value(), actual.getCustomField7Value());
        assertEquals(replied.getCustomField8Value(), actual.getCustomField8Value());
        assertEquals(replied.getCustomField9Value(), actual.getCustomField9Value());
        assertEquals(replied.getCustomField10Value(), actual.getCustomField10Value());

        //  新規登録なので承認状態はDraft、発行日も空
        assertEquals(WorkflowStatus.DRAFT, actual.getWorkflowStatus());
        assertEquals(null, actual.getIssuedAt());

        assertEquals(replied.getAttachments().size(), page.getAttachments().size());
        for (int i = 0; i < page.getAttachments().size(); i++) {
            Attachment a = replied.getAttachments().get(i);
            AttachmentInfo info = page.getAttachmentAt(i);

            assertTrue(CopiedAttachmentInfo.class.isAssignableFrom(info.getClass()));
            assertEquals(a.getId(), info.getFileId());
            assertEquals(a.getFileName(), info.getFileName());
        }

        //  新規登録になるのでidはnull
        assertNull(page.getId());
    }

    /**
     * {@link CopyCorresponSetupStrategy#setup()}の検証.
     * page#idに指定したコレポン文書のコピーが生成できていることを検証する.
     * @throws Exception
     */
    @Test
    public void testSetupPic() throws Exception {
        strategy.setup();

        Correspon actual = page.getCorrespon();
        assertNotNull(actual);

        //  返信元ユーザー情報(返信ユーザーは引き継がれない)
        assertNull(actual.getReplyAddressUserId());

        assertEquals(null, actual.getId());
        assertEquals(null, actual.getCorresponNo());
        assertEquals(correspon.getId(), actual.getParentCorresponId());
        assertEquals(correspon.getCorresponNo(), actual.getParentCorresponNo());
        assertEquals(null, actual.getPreviousRevCorresponId());
        assertEquals(null, actual.getPreviousRevCorresponNo());

        // 返信時もfromは引き継がない
        assertNull(actual.getFromCorresponGroup());

        assertEquals(replied.getCorresponStatus(), actual.getCorresponStatus());
        assertEquals(replied.getCorresponType(), actual.getCorresponType());

        // 宛先はやや複雑なので個別に検証
        assertTo(correspon, actual);
        assertEquals(replied.getCcAddressCorresponGroups().size(), actual.getCcAddressCorresponGroups().size());
        //  編集モードは全てNEW
        for (AddressCorresponGroup ag : actual.getAddressCorresponGroups()) {
            assertEquals(UpdateMode.NEW, ag.getMode());
        }

        //  件名、本文は返信済文書の内容がそのままコピーされている
        assertEquals(replied.getSubject(), actual.getSubject());
        assertEquals(replied.getBody(), actual.getBody());

        //  返信要否、返信期限は返信済文書から引き継ぎ
        assertEquals(replied.getReplyRequired(), actual.getReplyRequired());
        assertEquals(replied.getDeadlineForReply(), actual.getDeadlineForReply());

        //  attachments
        //  返信済文書からファイルを引き継ぐ
        //  が、ページに引き継いでいるのでコレポン文書には何も設定していない
        //  このメソッドの最後でページの検証をしている
        assertEquals(0, actual.getAttachments().size());
        //  custom fields
        assertEquals(replied.getCustomField1Value(), actual.getCustomField1Value());
        assertEquals(replied.getCustomField2Value(), actual.getCustomField2Value());
        assertEquals(replied.getCustomField3Value(), actual.getCustomField3Value());
        assertEquals(replied.getCustomField4Value(), actual.getCustomField4Value());
        assertEquals(replied.getCustomField5Value(), actual.getCustomField5Value());
        assertEquals(replied.getCustomField6Value(), actual.getCustomField6Value());
        assertEquals(replied.getCustomField7Value(), actual.getCustomField7Value());
        assertEquals(replied.getCustomField8Value(), actual.getCustomField8Value());
        assertEquals(replied.getCustomField9Value(), actual.getCustomField9Value());
        assertEquals(replied.getCustomField10Value(), actual.getCustomField10Value());

        //  新規登録なので承認状態はDraft、発行日も空
        assertEquals(WorkflowStatus.DRAFT, actual.getWorkflowStatus());
        assertEquals(null, actual.getIssuedAt());

        assertEquals(replied.getAttachments().size(), page.getAttachments().size());
        for (int i = 0; i < page.getAttachments().size(); i++) {
            Attachment a = replied.getAttachments().get(i);
            AttachmentInfo info = page.getAttachmentAt(i);

            assertTrue(CopiedAttachmentInfo.class.isAssignableFrom(info.getClass()));
            assertEquals(a.getId(), info.getFileId());
            assertEquals(a.getFileName(), info.getFileName());
        }
    }

    /**
     * {@link CopyCorresponSetupStrategy#setup()}の検証.
     * page#idに指定したコレポン文書のコピーが生成できていることを検証する.
     * @throws Exception
     */
    @Test
    public void testSetupCc() throws Exception {
        strategy.setup();

        Correspon actual = page.getCorrespon();
        assertNotNull(actual);

        //  返信元ユーザー情報(返信ユーザーは引き継がれない)
        assertNull(actual.getReplyAddressUserId());

        assertEquals(null, actual.getId());
        assertEquals(null, actual.getCorresponNo());
        assertEquals(correspon.getId(), actual.getParentCorresponId());
        assertEquals(correspon.getCorresponNo(), actual.getParentCorresponNo());
        assertEquals(null, actual.getPreviousRevCorresponId());
        assertEquals(null, actual.getPreviousRevCorresponNo());

        // 返信時もfromは引き継がない
        assertNull(actual.getFromCorresponGroup());

        assertEquals(replied.getCorresponStatus(), actual.getCorresponStatus());
        assertEquals(replied.getCorresponType(), actual.getCorresponType());

        // 宛先はやや複雑なので個別に検証
        assertTo(correspon, actual);
        assertEquals(replied.getCcAddressCorresponGroups().size(), actual.getCcAddressCorresponGroups().size());
        //  編集モードは全てNEW
        for (AddressCorresponGroup ag : actual.getAddressCorresponGroups()) {
            assertEquals(UpdateMode.NEW, ag.getMode());
        }

        //  件名、本文は返信済文書の内容がそのままコピーされている
        assertEquals(replied.getSubject(), actual.getSubject());
        assertEquals(replied.getBody(), actual.getBody());

        //  返信要否、返信期限は返信済文書から引き継ぎ
        assertEquals(replied.getReplyRequired(), actual.getReplyRequired());
        assertEquals(replied.getDeadlineForReply(), actual.getDeadlineForReply());

        //  attachments
        //  返信済文書からファイルを引き継ぐ
        //  が、ページに引き継いでいるのでコレポン文書には何も設定していない
        //  このメソッドの最後でページの検証をしている
        assertEquals(0, actual.getAttachments().size());
        //  custom fields
        assertEquals(replied.getCustomField1Value(), actual.getCustomField1Value());
        assertEquals(replied.getCustomField2Value(), actual.getCustomField2Value());
        assertEquals(replied.getCustomField3Value(), actual.getCustomField3Value());
        assertEquals(replied.getCustomField4Value(), actual.getCustomField4Value());
        assertEquals(replied.getCustomField5Value(), actual.getCustomField5Value());
        assertEquals(replied.getCustomField6Value(), actual.getCustomField6Value());
        assertEquals(replied.getCustomField7Value(), actual.getCustomField7Value());
        assertEquals(replied.getCustomField8Value(), actual.getCustomField8Value());
        assertEquals(replied.getCustomField9Value(), actual.getCustomField9Value());
        assertEquals(replied.getCustomField10Value(), actual.getCustomField10Value());

        //  新規登録なので承認状態はDraft、発行日も空
        assertEquals(WorkflowStatus.DRAFT, actual.getWorkflowStatus());
        assertEquals(null, actual.getIssuedAt());

        assertEquals(replied.getAttachments().size(), page.getAttachments().size());
        for (int i = 0; i < page.getAttachments().size(); i++) {
            Attachment a = replied.getAttachments().get(i);
            AttachmentInfo info = page.getAttachmentAt(i);

            assertTrue(CopiedAttachmentInfo.class.isAssignableFrom(info.getClass()));
            assertEquals(a.getId(), info.getFileId());
            assertEquals(a.getFileName(), info.getFileName());
        }

        //  新規登録になるのでidはnull
        assertNull(page.getId());
    }

    /**
     * {@link CopyCorresponSetupStrategy#setup()}の検証.
     * 引用文書の文書状態がCANCELEDの場合、OPENで生成できていることを検証する.
     * @throws Exception
     */
    @Test
    public void testSetupCorresponStatusCancel() throws Exception {
        // ステータスをCANCELEDに設定
        correspon.setCorresponStatus(CorresponStatus.CANCELED);

        strategy.setup();

        Correspon actual = page.getCorrespon();
        assertNotNull(actual);

        //  返信元ユーザー情報(返信ユーザーは引き継がれない)
        assertNull(actual.getReplyAddressUserId());

        assertEquals(null, actual.getId());
        assertEquals(null, actual.getCorresponNo());
        assertEquals(correspon.getId(), actual.getParentCorresponId());
        assertEquals(correspon.getCorresponNo(), actual.getParentCorresponNo());
        assertEquals(null, actual.getPreviousRevCorresponId());
        assertEquals(null, actual.getPreviousRevCorresponNo());

        // 返信時もfromは引き継がない
        assertNull(actual.getFromCorresponGroup());

        // 返信時は文書状態をOPENに変更
        assertEquals(CorresponStatus.OPEN, actual.getCorresponStatus());

        assertEquals(replied.getCorresponType(), actual.getCorresponType());

        // 宛先はやや複雑なので個別に検証
        assertTo(correspon, actual);
        assertEquals(replied.getCcAddressCorresponGroups().size(), actual.getCcAddressCorresponGroups().size());
        //  編集モードは全てNEW
        for (AddressCorresponGroup ag : actual.getAddressCorresponGroups()) {
            assertEquals(UpdateMode.NEW, ag.getMode());
            if (ag.getUsers() != null) {
                for (AddressUser au : ag.getUsers()) {
                    assertTrue(au.getPersonInCharges() == null || au.getPersonInCharges().isEmpty());
                    assertTrue(ag.getReplyCount() == null || ag.getReplyCount() == 0L);
                }
            }
        }

        //  件名、本文は返信済文書の内容がそのままコピーされている
        assertEquals(replied.getSubject(), actual.getSubject());
        assertEquals(replied.getBody(), actual.getBody());

        //  返信要否、返信期限は返信済文書から引き継ぎ
        assertEquals(replied.getReplyRequired(), actual.getReplyRequired());
        assertEquals(replied.getDeadlineForReply(), actual.getDeadlineForReply());

        //  attachments
        //  返信済文書からファイルを引き継ぐ
        //  が、ページに引き継いでいるのでコレポン文書には何も設定していない
        //  このメソッドの最後でページの検証をしている
        assertEquals(0, actual.getAttachments().size());
        //  custom fields
        assertEquals(replied.getCustomField1Value(), actual.getCustomField1Value());
        assertEquals(replied.getCustomField2Value(), actual.getCustomField2Value());
        assertEquals(replied.getCustomField3Value(), actual.getCustomField3Value());
        assertEquals(replied.getCustomField4Value(), actual.getCustomField4Value());
        assertEquals(replied.getCustomField5Value(), actual.getCustomField5Value());
        assertEquals(replied.getCustomField6Value(), actual.getCustomField6Value());
        assertEquals(replied.getCustomField7Value(), actual.getCustomField7Value());
        assertEquals(replied.getCustomField8Value(), actual.getCustomField8Value());
        assertEquals(replied.getCustomField9Value(), actual.getCustomField9Value());
        assertEquals(replied.getCustomField10Value(), actual.getCustomField10Value());

        //  新規登録なので承認状態はDraft、発行日も空
        assertEquals(WorkflowStatus.DRAFT, actual.getWorkflowStatus());
        assertEquals(null, actual.getIssuedAt());

        assertEquals(replied.getAttachments().size(), page.getAttachments().size());
        for (int i = 0; i < page.getAttachments().size(); i++) {
            Attachment a = replied.getAttachments().get(i);
            AttachmentInfo info = page.getAttachmentAt(i);

            assertTrue(CopiedAttachmentInfo.class.isAssignableFrom(info.getClass()));
            assertEquals(a.getId(), info.getFileId());
            assertEquals(a.getFileName(), info.getFileName());
        }

        //  新規登録になるのでidはnull
        assertNull(page.getId());
    }

    /**
     * 返信文書 To の検証.
     * <p>
     * 活動単位：依頼文書のFrom
     * ユーザー：依頼文書Preparer、Checker、Approver
     * </p>
     */
    private void assertTo(Correspon org, Correspon actual) {
        assertEquals(1, actual.getToAddressCorresponGroups().size());
        AddressCorresponGroup to = actual.getToAddressCorresponGroups().get(0);

        assertEquals(org.getFromCorresponGroup().getId(), to.getCorresponGroup().getId());

        List<User> expectedUsers = createdExpectedToUsers(org);
        assertEquals(expectedUsers.size(), to.getUsers().size());
        TOP:
        for (AddressUser au : to.getUsers()) {
            for (User u : expectedUsers) {
                if (au.getUser().getEmpNo().equals(u.getEmpNo())) {
                    continue TOP;
                }
            }
            fail(String.format("不正なユーザーが含まれている %s", au.getUser().getEmpNo()));
        }
    }

    private List<User> createdExpectedToUsers(Correspon org) {
        Set<String> empNo = new HashSet<String>();
        List<User> expectedUsers = new ArrayList<User>();
        expectedUsers.add(org.getCreatedBy());
        empNo.add(org.getCreatedBy().getEmpNo());
        for (Workflow w : org.getWorkflows()) {
            if (!empNo.contains(w.getUser().getEmpNo())) {
                expectedUsers.add(w.getUser());
                empNo.add(w.getUser().getEmpNo());
            }
        }
        return expectedUsers;
    }

//    public static class MockAbstractPage extends MockUp<AbstractPage> {
//        static boolean RET_IS_SYSTEM_ADMIN;
//        static boolean RET_IS_PROJECT_ADMIN;
//        static boolean RET_IS_ANY_GROUP_ADMIN;
//
//        @Mock
//        public boolean isSystemAdmin() {
//            return RET_IS_SYSTEM_ADMIN;
//        }
//
//        @Mock
//        public boolean isProjectAdmin(String checkProjectId) {
//            return RET_IS_PROJECT_ADMIN;
//        }
//
//        @Mock
//        public boolean isAnyGroupAdmin(String checkProjectId) {
//            return RET_IS_ANY_GROUP_ADMIN;
//        }
//    }
}
