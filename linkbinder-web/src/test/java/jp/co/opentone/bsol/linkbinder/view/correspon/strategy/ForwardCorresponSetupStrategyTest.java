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
import java.util.List;

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
import jp.co.opentone.bsol.linkbinder.dto.UpdateMode;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.code.AddressType;
import jp.co.opentone.bsol.linkbinder.dto.code.AddressUserType;
import jp.co.opentone.bsol.linkbinder.dto.code.CorresponStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.ReplyRequired;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowStatus;
import jp.co.opentone.bsol.linkbinder.view.correspon.util.CorresponEditMode;


/**
 * {@link ForwardCorresponSetupStrategy}のテストケース.
 * @author opentone
 */
public class ForwardCorresponSetupStrategyTest extends AbstractCorresponSetupStrategyTestCase {

    private Correspon correspon;

    public ForwardCorresponSetupStrategyTest() {
        super(CorresponEditMode.FORWARD);
    }

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();

        correspon = setupCorrespon();
        MockCorresponService.RET_FIND = correspon;
        page.setId(correspon.getId());
    }

    @Override
    @After
    public void teardown() {
        MockCorresponService.RET_FIND = null;
        page.getAttachments().clear();
        super.teardown();
    }
    private Correspon setupCorrespon() {
        Correspon c = new Correspon();

        c.setId(10L);
        c.setCorresponNo("YOC:IT-00001-001");
        c.setParentCorresponId(20L);
        c.setParentCorresponNo("YOC:IT-00001");
        c.setPreviousRevCorresponId(30L);
        c.setPreviousRevCorresponNo("YOC:IT-00000");

        User createdBy = new User();
        createdBy.setEmpNo("ZZ099");
        createdBy.setNameE("USER99");
        c.setCreatedBy(createdBy);

        CorresponGroup from = new CorresponGroup();
        from.setId(100L);
        from.setName("YOC:IT");
        c.setFromCorresponGroup(from);

        c.setCorresponStatus(CorresponStatus.CLOSED);
        c.setWorkflowStatus(WorkflowStatus.ISSUED);

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

        return c;
    }


    private void setUpAddress(Correspon c) {
        AddressCorresponGroup ag;
        CorresponGroup g;
        List<AddressUser> users;
        AddressUser au;
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
        u.setEmpNo("00001");
        u.setNameE("User1");
        au.setUser(u);
        users.add(au);

        ag.setUsers(users);
        addresses.add(ag);

        ag = new AddressCorresponGroup();
        ag.setId(2L);
        ag.setMode(UpdateMode.NONE);
        ag.setAddressType(AddressType.CC);
        g = new CorresponGroup();
        g.setId(22L);
        g.setName("Group2");
        ag.setCorresponGroup(g);

        users = new ArrayList<AddressUser>();
        au = new AddressUser();
        au.setId(222L);
        au.setAddressUserType(AddressUserType.NORMAL_USER);
        au.setAddressCorresponGroupId(ag.getId());
        ag.setReplyCount(2L);  //  返信済を表す
        u = new User();
        u.setEmpNo("00002");
        u.setNameE("User2");
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

        page.setAttachment1Transfer(false);
        page.setAttachment2Transfer(false);
        page.setAttachment3Transfer(false);
        page.setAttachment4Transfer(false);
        page.setAttachment5Transfer(false);
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
     * {@link ForwardCorresponSetupStrategy#setup()}の検証.
     * page#idに指定したコレポン文書の転送用コピーが生成できていることを検証する.
     * @throws Exception
     */
    @Test
    public void testSetup() throws Exception {
        strategy.setup();

        Correspon actual = page.getCorrespon();
        assertNotNull(actual);

        assertEquals(null, actual.getCreatedBy());
        assertEquals(null, actual.getCreatedAt());
        assertEquals(null, actual.getUpdatedBy());
        assertEquals(null, actual.getUpdatedAt());
        assertEquals(null, actual.getIssuedBy());
        assertEquals(null, actual.getIssuedAt());

        assertEquals(null, actual.getId());
        assertEquals(null, actual.getCorresponNo());
        assertEquals(null, actual.getParentCorresponId());
        assertEquals(null, actual.getParentCorresponNo());
        assertEquals(null, actual.getPreviousRevCorresponId());
        assertEquals(null, actual.getPreviousRevCorresponNo());
        assertEquals(null, actual.getFromCorresponGroup());
        assertEquals(CorresponStatus.OPEN, actual.getCorresponStatus());
        assertEquals(null, actual.getCorresponType());

        assertEquals(null, actual.getAddressCorresponGroups());

        /**
         *  件名、本文は転送用に加工されている
         *  ここでは加工内容の詳細は検証しない
         */
        assertTrue(actual.getSubject().contains(correspon.getSubject()));
        assertTrue(actual.getBody().contains(correspon.getBody()));

//        assertEquals(correspon.getSubject(), actual.getSubject());
//        assertEquals(correspon.getBody(), actual.getBody());
        assertEquals(ReplyRequired.NO, actual.getReplyRequired());
//        assertEquals(null, actual.getDeadlineForReply());
        //  attachments
        //  ファイルの引き継ぎ設定をしていないので添付ファイルは空
        assertEquals(0, actual.getAttachments().size());
        //  custom fields
        assertEquals(null, actual.getCustomField1Value());
        assertEquals(null, actual.getCustomField2Value());
        assertEquals(null, actual.getCustomField3Value());
        assertEquals(null, actual.getCustomField4Value());
        assertEquals(null, actual.getCustomField5Value());
        assertEquals(null, actual.getCustomField6Value());
        assertEquals(null, actual.getCustomField7Value());
        assertEquals(null, actual.getCustomField8Value());
        assertEquals(null, actual.getCustomField9Value());
        assertEquals(null, actual.getCustomField10Value());

        //  新規登録なので承認状態はDraft
        assertEquals(WorkflowStatus.DRAFT, actual.getWorkflowStatus());

        //  ファイルの引き継ぎ設定をしていないので添付ファイルは無し
        assertEquals(0, page.getAttachments().size());

        //  新規登録になるのでidはnull
        assertNull(page.getId());
    }

    /**
     * {@link ForwardCorresponSetupStrategy#setup()}の検証.
     * page#idに指定したコレポン文書のコピーが生成できていることを検証する.
     * 引き継ぎ指定した添付ファイルのみ反映されていること検証する.
     * @throws Exception
     */
    @Test
    public void testSetupAttachemtsTransfer() throws Exception {
        //  添付ファイル1と5のみ引き継ぎ
        page.setAttachment1Transfer(true);
        page.setAttachment5Transfer(true);
        //  後で検証するため値を保存
        Attachment file1 = correspon.getAttachments().get(0);
        Attachment file5 = correspon.getAttachments().get(4);

        strategy.setup();

        Correspon actual = page.getCorrespon();
        assertNotNull(actual);

        assertEquals(null, actual.getCreatedBy());
        assertEquals(null, actual.getCreatedAt());
        assertEquals(null, actual.getUpdatedBy());
        assertEquals(null, actual.getUpdatedAt());
        assertEquals(null, actual.getIssuedBy());
        assertEquals(null, actual.getIssuedAt());

        assertEquals(null, actual.getId());
        assertEquals(null, actual.getCorresponNo());
        assertEquals(null, actual.getParentCorresponId());
        assertEquals(null, actual.getParentCorresponNo());
        assertEquals(null, actual.getPreviousRevCorresponId());
        assertEquals(null, actual.getPreviousRevCorresponNo());
        assertEquals(null, actual.getFromCorresponGroup());
        assertEquals(CorresponStatus.OPEN, actual.getCorresponStatus());
        assertEquals(null, actual.getCorresponType());

        assertEquals(null, actual.getAddressCorresponGroups());

        /**
         *  件名、本文は返信用に加工されている
         *  ここでは加工内容の詳細は検証しない
         */
        assertTrue(actual.getSubject().contains(correspon.getSubject()));
        assertTrue(actual.getBody().contains(correspon.getBody()));

//        assertEquals(correspon.getSubject(), actual.getSubject());
//        assertEquals(correspon.getBody(), actual.getBody());
        assertEquals(ReplyRequired.NO, actual.getReplyRequired());
//        assertEquals(null, actual.getDeadlineForReply());
        //  attachments
        //  引き継いでもPageにしか値を持たないため０
        assertEquals(0, actual.getAttachments().size());

        //  custom fields
        assertEquals(null, actual.getCustomField1Value());
        assertEquals(null, actual.getCustomField2Value());
        assertEquals(null, actual.getCustomField3Value());
        assertEquals(null, actual.getCustomField4Value());
        assertEquals(null, actual.getCustomField5Value());
        assertEquals(null, actual.getCustomField6Value());
        assertEquals(null, actual.getCustomField7Value());
        assertEquals(null, actual.getCustomField8Value());
        assertEquals(null, actual.getCustomField9Value());
        assertEquals(null, actual.getCustomField10Value());

        //  新規登録なので承認状態はDraft
        assertEquals(WorkflowStatus.DRAFT, actual.getWorkflowStatus());


        //  添付ファイル1と5が引き継がれている
        assertEquals(2, page.getAttachments().size());

        Attachment a = file1;
        AttachmentInfo info = page.getAttachmentAt(0);
        assertTrue(CopiedAttachmentInfo.class.isAssignableFrom(info.getClass()));
        assertEquals(a.getId(), info.getFileId());
        assertEquals(a.getFileName(), info.getFileName());

        a = file5;
        info = page.getAttachmentAt(1);
        assertTrue(CopiedAttachmentInfo.class.isAssignableFrom(info.getClass()));
        assertEquals(a.getId(), info.getFileId());
        assertEquals(a.getFileName(), info.getFileName());

        //  新規登録になるのでidはnull
        assertNull(page.getId());
    }
}
