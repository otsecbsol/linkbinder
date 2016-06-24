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
package jp.co.opentone.bsol.linkbinder.view.correspon.attachment;

import static org.junit.Assert.*;

import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.extension.jsf.FacesContextMock;
import jp.co.opentone.bsol.framework.test.AbstractTestCase;
import jp.co.opentone.bsol.linkbinder.attachment.CopiedAttachmentInfo;
import jp.co.opentone.bsol.linkbinder.dto.AddressUser;
import jp.co.opentone.bsol.linkbinder.dto.Attachment;
import jp.co.opentone.bsol.linkbinder.dto.Correspon;
import jp.co.opentone.bsol.linkbinder.dto.CorresponResponseHistory;
import jp.co.opentone.bsol.linkbinder.dto.PersonInCharge;
import jp.co.opentone.bsol.linkbinder.dto.UpdateMode;
import jp.co.opentone.bsol.linkbinder.dto.code.CorresponStatus;
import jp.co.opentone.bsol.linkbinder.service.correspon.CorresponService;
import jp.co.opentone.bsol.linkbinder.util.view.correspon.CorresponResponseHistoryModel;


/**
 * {@link CopiedAttachmentInfo}のテストケース.
 * @author opentone
 */
public class CopiedAttachmentInfoTest extends AbstractTestCase {
    private String content;
    private CopiedAttachmentInfo info;
    private CorresponService service;
    private Attachment attachment;

    @BeforeClass
    public static void testSetUp() {
        FacesContextMock.initialize();
        // TODO 確認後削除
//        Mockit.redefineMethods(ViewHelper.class, MockViewHelper.class);
    }

    @AfterClass
    public static void testTeardown() {
        //TODO JMockitバージョンアップ対応
//        Mockit.tearDownMocks();
    }

    @Before
    public void setUp() throws Exception {
        content = "this is test.";

        attachment = new Attachment();
        attachment.setId(1L);
        attachment.setFileId("1234567");
        attachment.setCorresponId(11L);
        attachment.setFileName("fileName1");

        service = new MockCorresponService(attachment, content);

        info = new CopiedAttachmentInfo(attachment, service);
    }

    /**
     * {@link CopiedAttachmentInfo#getContent()}の検証.
     * @throws Exception
     */
    @Test
    public void testGetContent() throws Exception {
        byte[] actual = info.getContent();
        assertEquals(content, new String(actual));
    }

    /**
     * {@link CopiedAttachmentInfo#toAttachment()}の検証.
     * @throws Exception
     */
    @Test
    public void testToAttachment() throws Exception {
        Attachment actual = info.toAttachment();

        //  返信元、改訂元から引き継いだファイル情報なので値を引き継いで、編集モードはNEWである
        assertEquals(attachment.getId(), actual.getId());
        assertEquals(UpdateMode.NEW, actual.getMode());
        assertEquals(attachment.getFileId(), actual.getFileId());
        assertEquals(attachment.getFileName(), actual.getFileName());
        assertEquals(content, new String(actual.getContent()));
        assertEquals(attachment.getSourcePath(), actual.getSourcePath());
    }

    static class MockCorresponService implements CorresponService {
        private static final long serialVersionUID = 1L;
        private Attachment attachment;
        private String content;

        public MockCorresponService(Attachment attachment, String content) throws Exception {
            Attachment a = new Attachment();
            PropertyUtils.copyProperties(a, attachment);
            this.attachment = a;
            this.content = content;
        }
        /* (non-Javadoc)
         * @see jp.co.opentone.bsol.linkbinder.service.correspon.CorresponService#findAttachment(java.lang.Long, java.lang.Long)
         */
        public Attachment findAttachment(Long corresponId, Long attachmentId)
            throws ServiceAbortException {
            assertEquals(attachment.getCorresponId(), corresponId);
            assertEquals(attachment.getId(), attachmentId);

            attachment.setContent(content.getBytes());

            return attachment;
        }

        /* (non-Javadoc)
         * @see jp.co.opentone.bsol.linkbinder.service.correspon.CorresponService#assignPersonInCharge(jp.co.opentone.bsol.linkbinder.dto.Correspon, jp.co.opentone.bsol.linkbinder.dto.AddressUser, java.util.List)
         */
        public void assignPersonInCharge(Correspon correspon, AddressUser addressUser,
            List<PersonInCharge> pics) throws ServiceAbortException {
        }

        /* (non-Javadoc)
         * @see jp.co.opentone.bsol.linkbinder.service.correspon.CorresponService#delete(jp.co.opentone.bsol.linkbinder.dto.Correspon)
         */
        public void delete(Correspon correspon) throws ServiceAbortException {
        }

        /* (non-Javadoc)
         * @see jp.co.opentone.bsol.linkbinder.service.correspon.CorresponService#find(java.lang.Long)
         */
        public Correspon find(Long id) throws ServiceAbortException {
            return null;
        }
        /* (non-Javadoc)
         * @see jp.co.opentone.bsol.linkbinder.service.correspon.CorresponService#findReplyCorrespons(jp.co.opentone.bsol.linkbinder.dto.Correspon, java.lang.Long)
         */
        public List<Correspon> findReplyCorrespons(Correspon parentCorrespon, Long addressUserId)
            throws ServiceAbortException {
            return null;
        }
        /* (non-Javadoc)
         * @see jp.co.opentone.bsol.linkbinder.service.correspon.CorresponService
         * #findCorresponResponseHistory(jp.co.opentone.bsol.linkbinder.dto.Correspon)
         */
        public List<CorresponResponseHistory> findCorresponResponseHistory(Correspon correspon)
            throws ServiceAbortException {
            return null;
        }

        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.linkbinder.service.correspon.CorresponService
         * #generateHTML(jp.co.opentone.bsol.linkbinder.dto.Correspon, java.util.List)
         */
        public byte[] generateHTML(Correspon correspon,
            List<CorresponResponseHistoryModel> corresponResponseHistoryModel) throws ServiceAbortException {
            return null;
        }

        /* (non-Javadoc)
         * @see jp.co.opentone.bsol.linkbinder.service.correspon.CorresponService
         * #generateHTML(jp.co.opentone.bsol.linkbinder.dto.Correspon, java.util.List, boolean)
         */
        public byte[] generateHTML(Correspon correspon,
            List<CorresponResponseHistoryModel> corresponResponseHistory, boolean usePersonInCharge)
            throws ServiceAbortException {
            return null;
        }

        /* (non-Javadoc)
         * @see jp.co.opentone.bsol.linkbinder.service.correspon.CorresponService#issue(jp.co.opentone.bsol.linkbinder.dto.Correspon)
         */
        public void issue(Correspon correspon) throws ServiceAbortException {
        }

        /* (non-Javadoc)
         * @see jp.co.opentone.bsol.linkbinder.service.correspon.CorresponService#requestForVerification(jp.co.opentone.bsol.linkbinder.dto.Correspon)
         */
        public void requestForApproval(Correspon correspon) throws ServiceAbortException {
        }

        /* (non-Javadoc)
         * @see jp.co.opentone.bsol.linkbinder.service.correspon.CorresponService#savePartial(jp.co.opentone.bsol.linkbinder.dto.Correspon)
         */
        public void savePartial(Correspon correspon) throws ServiceAbortException {
        }

        /* (non-Javadoc)
         * @see jp.co.opentone.bsol.linkbinder.service.correspon.CorresponService#updateCorresponStatus(jp.co.opentone.bsol.linkbinder.dto.Correspon, jp.co.opentone.bsol.linkbinder.dto.code.CorresponStatus)
         */
        public void updateCorresponStatus(Correspon correspon, CorresponStatus status)
            throws ServiceAbortException {
        }

        /* (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.service.IService#getCurrentProjectId()
         */
        public String getCurrentProjectId() {
            return null;
        }
        /* (non-Javadoc)
         * @see jp.co.opentone.bsol.linkbinder.service.correspon.CorresponService#adjustCustomFields(jp.co.opentone.bsol.linkbinder.dto.Correspon)
         */
        public void adjustCustomFields(Correspon correspon) throws ServiceAbortException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.linkbinder.service.correspon.CorresponService
         * #generateZip(jp.co.opentone.bsol.linkbinder.dto.Correspon)
         */
        public byte[] generateZip(Correspon correspon) throws ServiceAbortException {
            return null;
        }

        /* (non-Javadoc)
         * @see jp.co.opentone.bsol.linkbinder.service.correspon.CorresponService
         * #generateZip(jp.co.opentone.bsol.linkbinder.dto.Correspon, boolean)
         */
        public byte[] generateZip(Correspon correspons, boolean usePersonInCharge)
            throws ServiceAbortException {
            return null;
        }
        /* (非 Javadoc)
         * @see jp.co.opentone.bsol.linkbinder.service.correspon.CorresponService#findAttachments(java.lang.Long)
         */
        @Override
        public List<Attachment> findAttachments(Long corresponId) throws ServiceAbortException {
            // TODO 自動生成されたメソッド・スタブ
            return null;
        }
    }

    // TODO 確認後削除
//    public static class MockViewHelper {
//        static String SESSION_ID = "test";
//        public HttpSession getHttpSession() {
//            MockHttpSession session = new MockHttpSession();
//            session.setId(SESSION_ID);
//            return session;
//        }
//    }
}
