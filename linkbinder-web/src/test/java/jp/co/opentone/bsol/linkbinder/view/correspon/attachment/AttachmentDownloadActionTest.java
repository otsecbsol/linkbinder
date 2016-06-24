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

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import jp.co.opentone.bsol.framework.core.exception.ApplicationFatalRuntimeException;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.extension.jsf.FacesContextMock;
import jp.co.opentone.bsol.framework.test.AbstractTestCase;
import jp.co.opentone.bsol.framework.web.view.util.ViewHelper;
import jp.co.opentone.bsol.linkbinder.attachment.AttachmentInfo;
import jp.co.opentone.bsol.linkbinder.dto.Attachment;


/**
 * {@link AttachmentDownloadAction}のテストケース.
 * @author opentone
 */
public class AttachmentDownloadActionTest extends AbstractTestCase {

    private AttachmentDownloadAction action;
    private AttachmentInfo info;
    private MockAttachmentDownloadablePage page;

    @BeforeClass
    public static void testSetUp() {
        FacesContextMock.initialize();
        //TODO JMockitバージョンアップ対応
//        Mockit.redefineMethods(ViewHelper.class, MockViewHelper.class);
    }

    @AfterClass
    public static void testTeardown() {
        //TODO JMockitバージョンアップ対応
//        Mockit.tearDownMocks();
        FacesContextMock.tearDown();
    }

    @Before
    public void setUp() {
        final Attachment a = new Attachment();
        a.setContent("this is test".getBytes());
        info = new AttachmentInfo() {
            private static final long serialVersionUID = 1L;

            @Override
            public byte[] getContent() throws ServiceAbortException {
                return a.getContent();
            }

            @Override
            public Attachment toAttachment() throws ServiceAbortException {
                return null;
            }
        };

        page = new MockAttachmentDownloadablePage(info);
        action = new AttachmentDownloadAction(page);
    }

    /**
     * {@link AttachmentDownloadAction#execute()}の検証.
     * 正常にダウンロードできることを確認.
     * @throws Exception
     */
    @Test
    public void testExecute() throws Exception {
        // テスト対象メソッド実行
        // MockViewHelper#download()で、正しい引数が渡ってきているかを検証している
        // なので、ここでは上記メソッドが呼びだされていることのみ検証
        action.execute();
        assertTrue(page.helper.downloadCalled);
    }

    /**
     * {@link AttachmentDownloadAction#execute()}の検証.
     * ダウンロード対象ファイル情報が不正な場合に例外が発生することを確認.
     * @throws Exception
     */
    @Test(expected = ApplicationFatalRuntimeException.class)
    public void testExecuteInvalidState() throws Exception {
        page.info = null;
        action.execute();
    }




    static class MockAttachmentDownloadablePage implements AttachmentDownloadablePage {
        private static final long serialVersionUID = 1L;
        private MockViewHelper helper;
        AttachmentInfo info;
        public MockAttachmentDownloadablePage(AttachmentInfo info) {
            this.info = info;
            try {
                helper = new MockViewHelper(info.getFileName(), info.getContent());
            } catch (ServiceAbortException e) {
                throw new RuntimeException(e);
            }
        }
        /* (non-Javadoc)
         * @see jp.co.opentone.bsol.linkbinder.view.correspon.attachment.AttachmentDownloadablePage#getDownloadingAttachmentInfo()
         */
        public AttachmentInfo getDownloadingAttachmentInfo() {
            return info;
        }

        /* (non-Javadoc)
         * @see jp.co.opentone.bsol.linkbinder.view.correspon.attachment.AttachmentDownloadablePage#getViewHelper()
         */
        public ViewHelper getViewHelper() {
            return helper;
        }

        /* (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.view.Page#getActionName()
         */
        public String getActionName() { return null; }

        /* (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.view.Page#getBeanName()
         */
        public String getBeanName() { return null; }

        /* (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.view.Page#isTransferNext()
         */
        public boolean isTransferNext() { return false; }

        /* (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.view.Page#setTransferNext(boolean)
         */
        public void setTransferNext(boolean transferNext) {}

        /* (non-Javadoc)
         * @see Page#setUp()
         */
        public void setUp() {}
    }

    public static class MockViewHelper extends ViewHelper {
        private static final long serialVersionUID = 1L;
        boolean downloadCalled = false;
        String expectedFileName;
        byte[] expectedContent;

        MockViewHelper(String expectedFileName, byte[] expectedContent) {
            this.expectedFileName = expectedFileName;
            this.expectedContent = expectedContent;
        }

        @Override
        public void download(String fileName, byte[] content) throws IOException {
            download(fileName, content, false);
        }

        @Override
        public void download(String fileName, byte[] content, boolean downloadByRealFileName) throws IOException {
            assertEquals(this.expectedFileName, fileName);
            assertEquals(new String(this.expectedContent), new String(content));
            downloadCalled = true;
        }
    }
}
