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

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import jp.co.opentone.bsol.linkbinder.attachment.NewAttachmentInfo;
import jp.co.opentone.bsol.linkbinder.dto.Attachment;
import jp.co.opentone.bsol.linkbinder.dto.UpdateMode;


/**
 * {@link NewAttachmentInfo}のテストケース.
 * @author opentone
 */
public class NewAttachmentInfoTest {

    private NewAttachmentInfo info;

    private File dir;
    private File file;

    private String content = "this is test";
    @Before
    public void setUp() throws Exception {
        dir = new File("tmp");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        file = new File(dir.getAbsolutePath(), "AttachmentInfoTest.tmp");
        FileUtils.writeByteArrayToFile(file, content.getBytes());

        info = new NewAttachmentInfo(file.getName(), file.getAbsolutePath());
    }

    @After
    public void teardown() {
        if (file != null && file.exists()) {
            file.delete();
        }
        if (dir != null && dir.exists()) {
            dir.delete();
        }
    }


    /**
     * {@link NewAttachmentInfo#getContent()}の検証.
     * @throws Exception
     */
    @Test
    public void testGetContent() throws Exception {
        byte[] actual = info.getContent();
        assertEquals(content, new String(actual));
    }

    /**
     * {@link NewAttachmentInfo#toAttachment()}の検証.
     * @throws Exception
     */
    @Test
    public void testToAttachment() throws Exception {
        Attachment actual = info.toAttachment();

        assertEquals(UpdateMode.NEW, actual.getMode());
        assertEquals(info.getFileId(), actual.getFileId());
        assertEquals(info.getFileName(), actual.getFileName());
        assertEquals(new String(info.getContent()), new String(actual.getContent()));
        assertEquals(info.getSourcePath(), actual.getSourcePath());
    }
}
