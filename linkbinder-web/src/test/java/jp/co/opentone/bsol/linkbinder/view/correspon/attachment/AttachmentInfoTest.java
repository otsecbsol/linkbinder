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

import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.linkbinder.attachment.AttachmentInfo;
import jp.co.opentone.bsol.linkbinder.dto.Attachment;


/**
 * {@link AttachmentInfo}のテストケース.
 * @author opentone
 */
public class AttachmentInfoTest {

    private File dir;
    private File file;

    @Before
    public void setUp() throws Exception {
        dir = new File("tmp");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        file = new File(dir.getAbsolutePath(), "AttachmentInfoTest.tmp");
        FileUtils.writeByteArrayToFile(file, "this is test".getBytes());
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

    @Test
    public void testDelete() {
        AttachmentInfo info = new AttachmentInfo() {
            private static final long serialVersionUID = 1L;

            @Override
            public byte[] getContent() throws ServiceAbortException {
                return null;
            }

            @Override
            public Attachment toAttachment() throws ServiceAbortException {
                return null;
            }
        };

        info.setSourcePath(file.getAbsolutePath());


        //  テスト対象を実行
        assertTrue(file.exists());
        info.delete();
        assertFalse(file.exists());
    }
}
