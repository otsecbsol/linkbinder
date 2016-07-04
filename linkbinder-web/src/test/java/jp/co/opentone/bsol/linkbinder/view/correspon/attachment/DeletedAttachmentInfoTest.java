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

import org.junit.Before;
import org.junit.Test;

import jp.co.opentone.bsol.linkbinder.attachment.DeletedAttachmentInfo;
import jp.co.opentone.bsol.linkbinder.dto.Attachment;
import jp.co.opentone.bsol.linkbinder.dto.UpdateMode;


/**
 * {@link DeletedAttachmentInfo}のテストケース.
 * @author opentone
 */
public class DeletedAttachmentInfoTest {

    private String content;
    private DeletedAttachmentInfo info;
    private Attachment attachment;

    @Before
    public void setUp() {
        content = "this is test.";
        attachment = new Attachment();
        attachment.setId(1L);
        attachment.setCorresponId(2L);
        attachment.setFileId("12345");
        attachment.setFileName("fileName1");
        attachment.setContent(content.getBytes());

        info = new DeletedAttachmentInfo(attachment);
    }

    /**
     * {@link DeletedAttachmentInfo#getContent()}の検証.
     * @throws Exception
     */
    @Test
    public void testGetContent() throws Exception {
        //  削除済ファイルなのでファイルの中身は取得できない
        assertNull(info.getContent());
    }

    /**
     * {@link DeletedAttachmentInfo#toAttachment()}の検証.
     * @throws Exception
     */
    @Test
    public void testToAttachment() throws Exception {
        Attachment actual = info.toAttachment();

        //  オブジェクト生成時に指定したインスタンスと同じ、
        //  かつ編集モードがDELETE
        assertEquals(attachment, actual);
        assertEquals(UpdateMode.DELETE, actual.getMode());
    }
}

