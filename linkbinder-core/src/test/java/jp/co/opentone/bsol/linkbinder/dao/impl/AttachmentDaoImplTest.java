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
package jp.co.opentone.bsol.linkbinder.dao.impl;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;

import jp.co.opentone.bsol.framework.core.dao.KeyDuplicateException;
import jp.co.opentone.bsol.framework.core.dao.RecordNotFoundException;
import jp.co.opentone.bsol.framework.test.AbstractDaoTestCase;
import jp.co.opentone.bsol.linkbinder.dto.Attachment;
import jp.co.opentone.bsol.linkbinder.dto.User;

public class AttachmentDaoImplTest extends AbstractDaoTestCase {

    @Resource
    private AttachmentDaoImpl dao;

    /**
     * 該当データがある場合の検証.
     * @throws Exception
     */
    @Test
    public void testFindById() throws Exception {
        Attachment a = dao.findById(1L);

        assertNotNull(a);
        assertDataSetEquals(newDataSet("AttachmentDaoImplTest_testFindById_expected.xls"), a);
    }

    /**
     * 該当データが無い場合の検証.
     * @throws Exception
     */
    @Test(expected = RecordNotFoundException.class)
    public void testFindByIdNotFound() throws Exception {
        dao.findById(-1L);
    }

    /**
     * {@link AttachmentDaoImpl#create(jp.co.opentone.bsol.linkbinder.dto.Attachment)}のテストケース.
     * コレポン文書新規登録の検証.
     * @throws Exception
     */
    @Test
    public void testCreate() throws Exception {
        // ログインユーザーの準備
        User login = new User();
        login.setEmpNo("ZZA01");

        // 登録内容の準備
        Attachment a = new Attachment();
        a.setCorresponId(2L);

        a.setFileId("05740CH");
        a.setFileName("test6.xls");
        a.setCreatedBy(login);
        a.setUpdatedBy(login);

        // 登録
        Long id = new Long(0L);

        // id（自動採番）を最大値にするためのループ
        while (true) {
            try {
                id = dao.create(a);
                break;
            } catch (KeyDuplicateException e) {
                continue;
            }
        }

        assertNotNull(id);

        Attachment actual = dao.findById(id);

        assertEquals(id, actual.getId());
        assertEquals(a.getCorresponId(), actual.getCorresponId());
        assertEquals(a.getFileId(), actual.getFileId());
        assertEquals(a.getFileName(), actual.getFileName());
        assertEquals(a.getCreatedBy().getEmpNo(), actual.getCreatedBy().getEmpNo());
        assertEquals(a.getUpdatedBy().getEmpNo(), actual.getUpdatedBy().getEmpNo());
        assertEquals(Long.valueOf(0L), actual.getDeleteNo());
        assertNotNull(actual.getCreatedAt());
        assertNotNull(actual.getUpdatedAt());
    }

    /**
     * 該当データがある場合の検証.
     * @throws Exception
     */
    @Test
    public void testFindByCorresponId() throws Exception {
        List<Attachment> aus = dao.findByCorresponId(2L);

        assertNotNull(aus);
        assertDataSetEquals(newDataSet("AttachmentDaoImplTest_testFindByCorresponId_expected.xls"),
                            aus);

    }

    /**
     * 該当データが無い場合の検証.
     * @throws Exception
     */
    @Test
    public void testFindByCorresponIdEmpty() throws Exception {
        assertEquals(0, dao.findByCorresponId(-1L).size());
    }

    /**
     * {@link AttachmentDaoImpl#delete(jp.co.opentone.bsol.linkbinder.dto.Attachment)}のテストケース.
     * 削除処理の検証.
     * @throws Exception
     */
    @Test
    public void testDelete() throws Exception {

        // ログインユーザーの準備
        User login = new User();
        login.setEmpNo("ZZA01");

        // 登録内容の準備
        Attachment attachment = new Attachment();
        attachment.setId(1L);
        attachment.setUpdatedBy(login);

        Integer actual = dao.delete(attachment);

        assertNotNull(actual);

        Attachment attachmentData = new Attachment();

        try {
            attachmentData = dao.findById(1L);
            fail("例外が発生していない");
        } catch (RecordNotFoundException e) {

        }

    }

}
