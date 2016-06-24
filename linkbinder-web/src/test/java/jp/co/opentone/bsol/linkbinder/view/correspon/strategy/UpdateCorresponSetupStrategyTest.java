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

import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.linkbinder.attachment.AttachmentInfo;
import jp.co.opentone.bsol.linkbinder.attachment.SavedAttachmentInfo;
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
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.view.correspon.util.CorresponEditMode;


/**
 * {@link UpdateCorresponSetupStrategy}のテストケース.
 * @author opentone
 */
public class UpdateCorresponSetupStrategyTest extends AbstractCorresponSetupStrategyTestCase {

    private Correspon correspon;

    public UpdateCorresponSetupStrategyTest() {
        super(CorresponEditMode.UPDATE);
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
        MockAbstractPage.IS_SYSTEM_ADMIN = false;
        MockAbstractPage.IS_PROJECT_ADMIN = false;
        MockAbstractPage.IS_ANY_GROUP_ADMIN = false;
        MockAbstractPage.RET_PROJID = null;

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

        CorresponGroup from = new CorresponGroup();
        from.setId(100L);
        from.setName("YOC:IT");
        c.setFromCorresponGroup(from);

        c.setCorresponStatus(CorresponStatus.OPEN);
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
     * {@link UpdateCorresponSetupStrategy#setup()}の検証.
     * page#idに指定したコレポン文書が取得できていることを検証する.
     * @throws Exception
     */
    @Test
    public void testSetupSystemAdmin() throws Exception {
        MockAbstractPage.IS_SYSTEM_ADMIN = true;
        strategy.setup();

        Correspon actual = page.getCorrespon();
        assertNotNull(actual);

        assertEquals(correspon.getId(), actual.getId());
        assertEquals(correspon.getCorresponNo(), actual.getCorresponNo());
        assertEquals(correspon.getParentCorresponId(), actual.getParentCorresponId());
        assertEquals(correspon.getParentCorresponNo(), actual.getParentCorresponNo());
        assertEquals(correspon.getPreviousRevCorresponId(), actual.getPreviousRevCorresponId());
        assertEquals(correspon.getPreviousRevCorresponNo(), actual.getPreviousRevCorresponNo());
        assertEquals(correspon.getFromCorresponGroup(), actual.getFromCorresponGroup());
        assertEquals(correspon.getCorresponStatus(), actual.getCorresponStatus());
        assertEquals(correspon.getCorresponType(), actual.getCorresponType());

        assertEquals(correspon.getAddressCorresponGroups(), actual.getAddressCorresponGroups());

        assertEquals(correspon.getSubject(), actual.getSubject());
        assertEquals(correspon.getBody(), actual.getBody());
        assertEquals(correspon.getReplyRequired(), actual.getReplyRequired());
        assertEquals(correspon.getDeadlineForReply(), actual.getDeadlineForReply());
        //  attachments
        assertEquals(correspon.getAttachments().size(), actual.getAttachments().size());
        //  custom fields
        assertEquals(correspon.getCustomField1Value(), actual.getCustomField1Value());
        assertEquals(correspon.getCustomField2Value(), actual.getCustomField2Value());
        assertEquals(correspon.getCustomField3Value(), actual.getCustomField3Value());
        assertEquals(correspon.getCustomField4Value(), actual.getCustomField4Value());
        assertEquals(correspon.getCustomField5Value(), actual.getCustomField5Value());
        assertEquals(correspon.getCustomField6Value(), actual.getCustomField6Value());
        assertEquals(correspon.getCustomField7Value(), actual.getCustomField7Value());
        assertEquals(correspon.getCustomField8Value(), actual.getCustomField8Value());
        assertEquals(correspon.getCustomField9Value(), actual.getCustomField9Value());
        assertEquals(correspon.getCustomField10Value(), actual.getCustomField10Value());

        assertEquals(correspon.getWorkflowStatus(), actual.getWorkflowStatus());


        assertEquals(correspon.getAttachments().size(), page.getAttachments().size());
        for (int i = 0; i < page.getAttachments().size(); i++) {
            Attachment a = correspon.getAttachments().get(i);
            AttachmentInfo info = page.getAttachmentAt(i);

            assertTrue(SavedAttachmentInfo.class.isAssignableFrom(info.getClass()));
            assertEquals(a.getId(), info.getFileId());
            assertEquals(a.getFileName(), info.getFileName());
        }
    }

    /**
     * {@link UpdateCorresponSetupStrategy#setup()}の検証.
     * page#idに指定したコレポン文書が取得できていることを検証する.
     * @throws Exception
     */
    @Test
    public void testSetupProjectAdmin() throws Exception {
        MockAbstractPage.IS_PROJECT_ADMIN = true;
        strategy.setup();

        Correspon actual = page.getCorrespon();
        assertNotNull(actual);

        assertEquals(correspon.getId(), actual.getId());
        assertEquals(correspon.getCorresponNo(), actual.getCorresponNo());
        assertEquals(correspon.getParentCorresponId(), actual.getParentCorresponId());
        assertEquals(correspon.getParentCorresponNo(), actual.getParentCorresponNo());
        assertEquals(correspon.getPreviousRevCorresponId(), actual.getPreviousRevCorresponId());
        assertEquals(correspon.getPreviousRevCorresponNo(), actual.getPreviousRevCorresponNo());
        assertEquals(correspon.getFromCorresponGroup(), actual.getFromCorresponGroup());
        assertEquals(correspon.getCorresponStatus(), actual.getCorresponStatus());
        assertEquals(correspon.getCorresponType(), actual.getCorresponType());

        assertEquals(correspon.getAddressCorresponGroups(), actual.getAddressCorresponGroups());

        assertEquals(correspon.getSubject(), actual.getSubject());
        assertEquals(correspon.getBody(), actual.getBody());
        assertEquals(correspon.getReplyRequired(), actual.getReplyRequired());
        assertEquals(correspon.getDeadlineForReply(), actual.getDeadlineForReply());
        //  attachments
        assertEquals(correspon.getAttachments().size(), actual.getAttachments().size());
        //  custom fields
        assertEquals(correspon.getCustomField1Value(), actual.getCustomField1Value());
        assertEquals(correspon.getCustomField2Value(), actual.getCustomField2Value());
        assertEquals(correspon.getCustomField3Value(), actual.getCustomField3Value());
        assertEquals(correspon.getCustomField4Value(), actual.getCustomField4Value());
        assertEquals(correspon.getCustomField5Value(), actual.getCustomField5Value());
        assertEquals(correspon.getCustomField6Value(), actual.getCustomField6Value());
        assertEquals(correspon.getCustomField7Value(), actual.getCustomField7Value());
        assertEquals(correspon.getCustomField8Value(), actual.getCustomField8Value());
        assertEquals(correspon.getCustomField9Value(), actual.getCustomField9Value());
        assertEquals(correspon.getCustomField10Value(), actual.getCustomField10Value());

        assertEquals(correspon.getWorkflowStatus(), actual.getWorkflowStatus());


        assertEquals(correspon.getAttachments().size(), page.getAttachments().size());
        for (int i = 0; i < page.getAttachments().size(); i++) {
            Attachment a = correspon.getAttachments().get(i);
            AttachmentInfo info = page.getAttachmentAt(i);

            assertTrue(SavedAttachmentInfo.class.isAssignableFrom(info.getClass()));
            assertEquals(a.getId(), info.getFileId());
            assertEquals(a.getFileName(), info.getFileName());
        }
    }

    /**
     * {@link UpdateCorresponSetupStrategy#setup()}の検証.
     * page#idに指定したコレポン文書が取得できていることを検証する.
     * @throws Exception
     */
    @Test
    public void testSetupGroupAdmin() throws Exception {
        MockAbstractPage.IS_ANY_GROUP_ADMIN = true;
        strategy.setup();

        Correspon actual = page.getCorrespon();
        assertNotNull(actual);

        assertEquals(correspon.getId(), actual.getId());
        assertEquals(correspon.getCorresponNo(), actual.getCorresponNo());
        assertEquals(correspon.getParentCorresponId(), actual.getParentCorresponId());
        assertEquals(correspon.getParentCorresponNo(), actual.getParentCorresponNo());
        assertEquals(correspon.getPreviousRevCorresponId(), actual.getPreviousRevCorresponId());
        assertEquals(correspon.getPreviousRevCorresponNo(), actual.getPreviousRevCorresponNo());
        assertEquals(correspon.getFromCorresponGroup(), actual.getFromCorresponGroup());
        assertEquals(correspon.getCorresponStatus(), actual.getCorresponStatus());
        assertEquals(correspon.getCorresponType(), actual.getCorresponType());

        assertEquals(correspon.getAddressCorresponGroups(), actual.getAddressCorresponGroups());

        assertEquals(correspon.getSubject(), actual.getSubject());
        assertEquals(correspon.getBody(), actual.getBody());
        assertEquals(correspon.getReplyRequired(), actual.getReplyRequired());
        assertEquals(correspon.getDeadlineForReply(), actual.getDeadlineForReply());
        //  attachments
        assertEquals(correspon.getAttachments().size(), actual.getAttachments().size());
        //  custom fields
        assertEquals(correspon.getCustomField1Value(), actual.getCustomField1Value());
        assertEquals(correspon.getCustomField2Value(), actual.getCustomField2Value());
        assertEquals(correspon.getCustomField3Value(), actual.getCustomField3Value());
        assertEquals(correspon.getCustomField4Value(), actual.getCustomField4Value());
        assertEquals(correspon.getCustomField5Value(), actual.getCustomField5Value());
        assertEquals(correspon.getCustomField6Value(), actual.getCustomField6Value());
        assertEquals(correspon.getCustomField7Value(), actual.getCustomField7Value());
        assertEquals(correspon.getCustomField8Value(), actual.getCustomField8Value());
        assertEquals(correspon.getCustomField9Value(), actual.getCustomField9Value());
        assertEquals(correspon.getCustomField10Value(), actual.getCustomField10Value());

        assertEquals(correspon.getWorkflowStatus(), actual.getWorkflowStatus());


        assertEquals(correspon.getAttachments().size(), page.getAttachments().size());
        for (int i = 0; i < page.getAttachments().size(); i++) {
            Attachment a = correspon.getAttachments().get(i);
            AttachmentInfo info = page.getAttachmentAt(i);

            assertTrue(SavedAttachmentInfo.class.isAssignableFrom(info.getClass()));
            assertEquals(a.getId(), info.getFileId());
            assertEquals(a.getFileName(), info.getFileName());
        }
    }

    /**
     * {@link UpdateCorresponSetupStrategy#setup()}の検証.
     * page#idに指定したコレポン文書が取得できていることを検証する.
     * @throws Exception
     */
    @Test
    public void testSetupNormalUserWorkflowStatusIssued() throws Exception {
        try {
            strategy.setup();
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CORREPON_ALREADY_ISSUED, actual.getMessageCode());
        }
    }

    /**
     * {@link UpdateCorresponSetupStrategy#setup()}の検証.
     * page#idに指定したコレポン文書が取得できていることを検証する.
     * @throws Exception
     */
    @Test
    public void testSetupNormalUserStatusNotIssue() throws Exception {
        correspon.setWorkflowStatus(WorkflowStatus.DRAFT);
        strategy.setup();

        Correspon actual = page.getCorrespon();
        assertNotNull(actual);

        assertEquals(correspon.getId(), actual.getId());
        assertEquals(correspon.getCorresponNo(), actual.getCorresponNo());
        assertEquals(correspon.getParentCorresponId(), actual.getParentCorresponId());
        assertEquals(correspon.getParentCorresponNo(), actual.getParentCorresponNo());
        assertEquals(correspon.getPreviousRevCorresponId(), actual.getPreviousRevCorresponId());
        assertEquals(correspon.getPreviousRevCorresponNo(), actual.getPreviousRevCorresponNo());
        assertEquals(correspon.getFromCorresponGroup(), actual.getFromCorresponGroup());
        assertEquals(correspon.getCorresponStatus(), actual.getCorresponStatus());
        assertEquals(correspon.getCorresponType(), actual.getCorresponType());

        assertEquals(correspon.getAddressCorresponGroups(), actual.getAddressCorresponGroups());

        assertEquals(correspon.getSubject(), actual.getSubject());
        assertEquals(correspon.getBody(), actual.getBody());
        assertEquals(correspon.getReplyRequired(), actual.getReplyRequired());
        assertEquals(correspon.getDeadlineForReply(), actual.getDeadlineForReply());
        //  attachments
        assertEquals(correspon.getAttachments().size(), actual.getAttachments().size());
        //  custom fields
        assertEquals(correspon.getCustomField1Value(), actual.getCustomField1Value());
        assertEquals(correspon.getCustomField2Value(), actual.getCustomField2Value());
        assertEquals(correspon.getCustomField3Value(), actual.getCustomField3Value());
        assertEquals(correspon.getCustomField4Value(), actual.getCustomField4Value());
        assertEquals(correspon.getCustomField5Value(), actual.getCustomField5Value());
        assertEquals(correspon.getCustomField6Value(), actual.getCustomField6Value());
        assertEquals(correspon.getCustomField7Value(), actual.getCustomField7Value());
        assertEquals(correspon.getCustomField8Value(), actual.getCustomField8Value());
        assertEquals(correspon.getCustomField9Value(), actual.getCustomField9Value());
        assertEquals(correspon.getCustomField10Value(), actual.getCustomField10Value());

        assertEquals(correspon.getWorkflowStatus(), actual.getWorkflowStatus());


        assertEquals(correspon.getAttachments().size(), page.getAttachments().size());
        for (int i = 0; i < page.getAttachments().size(); i++) {
            Attachment a = correspon.getAttachments().get(i);
            AttachmentInfo info = page.getAttachmentAt(i);

            assertTrue(SavedAttachmentInfo.class.isAssignableFrom(info.getClass()));
            assertEquals(a.getId(), info.getFileId());
            assertEquals(a.getFileName(), info.getFileName());
        }
    }
}
