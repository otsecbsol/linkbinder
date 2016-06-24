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

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FilenameUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.mock.web.MockHttpSession;

import jp.co.opentone.bsol.framework.core.config.SystemConfig;
import jp.co.opentone.bsol.framework.extension.jsf.FacesContextMock;
import jp.co.opentone.bsol.framework.test.AbstractTestCase;
import jp.co.opentone.bsol.framework.web.view.util.ViewHelper;
import jp.co.opentone.bsol.linkbinder.attachment.AttachmentInfo;
import jp.co.opentone.bsol.linkbinder.attachment.UploadedFile;
import jp.co.opentone.bsol.linkbinder.view.correspon.CorresponEditPage;
import mockit.Mock;
import mockit.MockUp;


/**
 * {@link AttachmentUploadAction}のテストケース.
 * コレポン文書に添付した電子ファイルのアップロード処理を検証する.
 * <p>
 * $Date: 2011-06-10 10:08:38 +0900 (金, 10  6 2011) $
 * $Rev: 4065 $
 * $Author: nemoto $
 */
public class AttachmentUploadActionTest extends AbstractTestCase {

    /**
     * テスト対象
     */
    private AttachmentUploadAction action;

    @Resource
    private CorresponEditPage page;

    private UploadedFile file1;
    private UploadedFile file2;
    private UploadedFile file3;
    private UploadedFile file4;
    private UploadedFile file5;

    @BeforeClass
    public static void testSetUp() {
        FacesContextMock.initialize();
        new MockViewHelper();
    }

    @AfterClass
    public static void testTeardown() {
        new MockViewHelper().tearDown();
        FacesContextMock.tearDown();
    }

    @Before
    public void setUp() {
        action = new AttachmentUploadAction(page);
    }

    @After
    public void teardown() {
        page.getAttachments().clear();
    }

    /**
     * ファイルアップロードをエミュレートするメソッド.
     */
    private void upload() {
        page.setAttachment1(file1);
        page.setAttachment2(file2);
        page.setAttachment3(file3);
        page.setAttachment4(file4);
        page.setAttachment5(file5);
    }

    /**
     * 全て新規追加の場合
     * @throws Exception
     */
    @Test
    public void testExecuteAddAll() throws Exception {
        // TODO 確認後削除
//        file1 = new MockUploadedFile("/work/fil;:|?*e1.txt", "this is file1.");
//        file2 = new MockUploadedFile("/work/file2.txt", "this is file2.");
//        file3 = new MockUploadedFile("/work/file3.txt", "this is file3.");
//        file4 = new MockUploadedFile("/work/file4.txt", "this is file4.");
//        file5 = new MockUploadedFile("\\work\\file5.txt", "this is file5.");
        file1 = new UploadedFile();
        file2 = new UploadedFile();
        file3 = new UploadedFile();
        file4 = new UploadedFile();
        file5 = new UploadedFile();

        assertEquals(0, page.getAttachedCount());
        assertEquals(0, page.getAttachments().size());
        //  アップロード
        upload();
        //  テスト対象を実行
        action.execute();


        //  アップロードファイルの件数確認
        assertEquals(5, page.getAttachedCount());
        assertEquals(5, page.getAttachments().size());
        //  アップロードファイルの内容確認
        int i = 0;
        AttachmentInfo info;
        info = page.getAttachments().get(i++);
        assertNull(info.getFileId());
        assertNotNull(info.getSourcePath());
        assertEquals(getExpectedFileName(file1), info.getFileName());

        info = page.getAttachments().get(i++);
        assertNull(info.getFileId());
        assertNotNull(info.getSourcePath());
        assertEquals(getExpectedFileName(file2), info.getFileName());

        info = page.getAttachments().get(i++);
        assertNull(info.getFileId());
        assertNotNull(info.getSourcePath());
        assertEquals(getExpectedFileName(file3), info.getFileName());

        info = page.getAttachments().get(i++);
        assertNull(info.getFileId());
        assertNotNull(info.getSourcePath());
        assertEquals(getExpectedFileName(file4), info.getFileName());

        info = page.getAttachments().get(i++);
        assertNull(info.getFileId());
        assertNotNull(info.getSourcePath());
        assertEquals(getExpectedFileName(file5), info.getFileName());
    }

    /**
     * 一部新規追加の場合
     * @throws Exception
     */
    @Test
    public void testExecuteAddPartial() throws Exception {
        // TODO 確認後削除
//        file1 = new MockUploadedFile("/work/file1.txt", "this is file1.");
//        file3 = new MockUploadedFile("/work/file3.txt", "this is file3.");
        file1 = new UploadedFile();
        file3 = new UploadedFile();

        assertEquals(0, page.getAttachedCount());
        assertEquals(0, page.getAttachments().size());
        //  アップロード
        upload();
        //  テスト対象を実行
        action.execute();


        //  アップロードファイルの件数確認
        assertEquals(2, page.getAttachedCount());
        assertEquals(2, page.getAttachments().size());
        //  アップロードファイルの内容確認
        int i = 0;
        AttachmentInfo info;
        info = page.getAttachments().get(i++);
        assertNull(info.getFileId());
        assertNotNull(info.getSourcePath());
        assertEquals(getExpectedFileName(file1), info.getFileName());

        info = page.getAttachments().get(i++);
        assertNull(info.getFileId());
        assertNotNull(info.getSourcePath());
        assertEquals(getExpectedFileName(file3), info.getFileName());
    }


    private String getExpectedFileName(UploadedFile file) {
        String result;
        // TODO 確認後削除
//        if (file.getName().indexOf('/') != -1) {
//            result = FilenameUtils.getName(file.getName());
//        } else if (file.getName().indexOf('\\') != -1) {
//            result = FilenameUtils.getName(file.getName());
//        } else {
//            result = file.getName();
//        }
        if (file.getFilename().indexOf('/') != -1) {
            result = FilenameUtils.getName(file.getFilename());
        } else if (file.getFilename().indexOf('\\') != -1) {
            result = FilenameUtils.getName(file.getFilename());
        } else {
            result = file.getFilename();
        }
        String regex = SystemConfig.getValue("server.file.name.regex");
        String replacement = SystemConfig.getValue("server.file.name.replacement");

        System.out.printf("regex=%s, rep=%s, result=%s%n",
                          regex,
                          replacement,
                          result.replaceAll(regex, replacement));

        return result.replaceAll(regex, replacement);
    }

    public static class MockViewHelper extends MockUp<ViewHelper> {
        static String SESSION_ID = "test";
        @Mock
        public HttpSession getHttpSession() {
            MockHttpSession session = new MockHttpSession();
//            session.setId(SESSION_ID);
            return session;
        }
    }
}

