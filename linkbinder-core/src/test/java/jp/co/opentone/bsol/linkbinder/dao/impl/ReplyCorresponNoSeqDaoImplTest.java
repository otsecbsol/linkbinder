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

import java.util.GregorianCalendar;

import javax.annotation.Resource;

import org.junit.Test;

import jp.co.opentone.bsol.framework.core.dao.KeyDuplicateException;
import jp.co.opentone.bsol.framework.core.dao.RecordNotFoundException;
import jp.co.opentone.bsol.framework.core.dao.StaleRecordException;
import jp.co.opentone.bsol.framework.test.AbstractDaoTestCase;
import jp.co.opentone.bsol.linkbinder.dto.ReplyCorresponNoSeq;
import jp.co.opentone.bsol.linkbinder.dto.User;

/**
 * ReplyCorresponNoSeqDaoImplのテストケース.
 * @author opentone
 */
public class ReplyCorresponNoSeqDaoImplTest extends AbstractDaoTestCase {

    /**
     * テスト対象.
     */
    @Resource
    private ReplyCorresponNoSeqDaoImpl dao;

    /**
     * {@link ReplyCorresponNoSeqDaoImpl#findById(java.lang.Long)}のテストケース.
     * @throws Exception
     */
    @Test
    public void testFindById_1() throws Exception {
        Long id = 1L;

        ReplyCorresponNoSeq reply = dao.findById(id);

        assertNotNull(reply);
        assertDataSetEquals(newDataSet("ReplyCorresponNoSeqDaoImplTest_testFindById_expected.xls"),
                            reply);
    }

    /**
     * {@link ReplyCorresponNoSeqDaoImpl#findById(java.lang.Long)}のテストケース.
     * 削除されたレコード.
     * @throws Exception
     */
    @Test(expected = RecordNotFoundException.class)
    public void testFindByIdRecordNotFound_1() throws Exception {
        Long id = 3L;

        dao.findById(id);
        fail("例外が発生していない");
    }

    /**
     * {@link ReplyCorresponNoSeqDaoImpl#findById(java.lang.Long)}のテストケース.
     * 存在しないレコード.
     * @throws Exception
     */
    @Test(expected = RecordNotFoundException.class)
    public void testFindByIdRecordNotFound_2() throws Exception {
        Long id = -1L;

        dao.findById(id);
        fail("例外が発生していない");
    }

    /**
     * {@link ReplyCorresponNoSeqDaoImpl#searchForUpdate(java.lang.Long)}
     * のテストケース.
     * @throws Exception
     */
    @Test
    public void testFindForUpdate_1() throws Exception {
        Long parentCorresponId = 1L;

        ReplyCorresponNoSeq reply = dao.findForUpdate(parentCorresponId);

        assertNotNull(reply);
        assertDataSetEquals(
                            newDataSet("ReplyCorresponNoSeqDaoImplTest_testFindForUpdate_expected.xls"),
                            reply);
    }

    /**
     * {@link ReplyCorresponNoSeqDaoImpl#searchForUpdate(java.lang.Long)}
     * のテストケース.
     * @throws Exception
     */
    @Test
    public void testFindForUpdate_2() throws Exception {
        Long parentCorresponId = -1L;

        ReplyCorresponNoSeq reply = dao.findForUpdate(parentCorresponId);

        // 例外は発生しない
        assertNull(reply);
    }

    /**
     * {@link ReplyCorresponNoSeqDaoImpl#c.framework.core.daoc.framework.core.dao.Entity)}
     * のテストケース.
     * @throws Exception
     */
    @Test
    public void testCreate() throws Exception {
        User user = new User();
        user.setEmpNo("USER1");

        // 登録内容の準備
        ReplyCorresponNoSeq reply = new ReplyCorresponNoSeq();
        // 登録される値
        reply.setParentCorresponId(3L);
        reply.setNo(1L);
        reply.setCreatedBy(user);
        reply.setUpdatedBy(user);

        // 登録されない値
        reply.setCreatedAt(new GregorianCalendar(2009, 1, 1).getTime());
        reply.setUpdatedAt(new GregorianCalendar(2009, 1, 1).getTime());
        reply.setVersionNo(999L);
        reply.setDeleteNo(999L);

        // 登録
        Long id = dao.create(reply);
        assertNotNull(id);

        ReplyCorresponNoSeq actual = dao.findById(id);

        assertEquals(id, actual.getId());
        assertEquals(reply.getParentCorresponId(), actual.getParentCorresponId());
        assertEquals(reply.getNo(), actual.getNo());
        assertEquals(reply.getCreatedBy().getEmpNo(), actual.getCreatedBy().getEmpNo());
        assertEquals(reply.getUpdatedBy().getEmpNo(), actual.getUpdatedBy().getEmpNo());
        assertEquals(Long.valueOf(1L), actual.getVersionNo());
        assertEquals(Long.valueOf(0L), actual.getDeleteNo());
        assertNotNull(actual.getCreatedAt());
        assertNotNull(actual.getUpdatedAt());

        // 登録されない値
        assertFalse(reply.getCreatedAt().equals(actual.getCreatedAt()));
        assertFalse(reply.getUpdatedAt().equals(actual.getUpdatedAt()));
        assertFalse(reply.getVersionNo().equals(actual.getVersionNo()));
        assertFalse(reply.getDeleteNo().equals(actual.getDeleteNo()));
    }

    @Test(expected = KeyDuplicateException.class)
    public void testCreateDuplication() throws Exception {
        User user = new User();
        user.setEmpNo("USER1");

        // 登録内容の準備
        ReplyCorresponNoSeq reply = new ReplyCorresponNoSeq();
        reply.setParentCorresponId(1L); // 重複
        reply.setNo(1L);
        reply.setCreatedBy(user);
        reply.setUpdatedBy(user);

        // 登録
        dao.create(reply);
        fail("例外が発生していない");
    }

/**
     * {@link ReplyCorresponNoSe.framework.core.daoe( Entity )のテストケース.
     * NULLの箇所は更新されないことを確認する_1.
     *
     * @throws Exception
     */
    @Test
    public void testUpdate_1() throws Exception {
        User user = new User();
        user.setEmpNo("USER1");

        // 更新内容の準備
        Long id = 1L;
        ReplyCorresponNoSeq reply = new ReplyCorresponNoSeq();
        reply.setId(id);
        reply.setVersionNo(1L);

        // 更新される値
        reply.setParentCorresponId(3L);
        reply.setUpdatedBy(user);

        // 更新されない値
        reply.setUpdatedAt(new GregorianCalendar(2009, 1, 1).getTime());
        reply.setDeleteNo(999L);

        // 更新
        int count = dao.update(reply);
        assertEquals(1, count);

        ReplyCorresponNoSeq actual = dao.findById(id);

        assertEquals(id, actual.getId());
        assertEquals(reply.getParentCorresponId(), actual.getParentCorresponId());
        assertEquals(reply.getUpdatedBy().getEmpNo(), actual.getUpdatedBy().getEmpNo());
        assertEquals(Long.valueOf(2L), actual.getVersionNo()); // 上がっている
        assertEquals(Long.valueOf(0L), actual.getDeleteNo());
        assertNotNull(actual.getUpdatedAt());

        // 変更なし
        assertEquals("TEST", actual.getCreatedBy().getEmpNo());
        assertEquals(Long.valueOf(2L), actual.getNo());

        // 更新されない値
        assertFalse(reply.getUpdatedAt().equals(actual.getUpdatedAt()));
        assertFalse(reply.getDeleteNo().equals(actual.getDeleteNo()));
    }

/**
     * {@link ReplyCorrespo.framework.core.daoupdate( Entity )のテストケース.
     * NULLの箇所は更新されないことを確認する_2.
     *
     * @throws Exception
     */
    @Test
    public void testUpdate_2() throws Exception {
        User user = new User();
        user.setEmpNo("USER1");

        // 更新内容の準備
        Long id = 1L;
        ReplyCorresponNoSeq reply = new ReplyCorresponNoSeq();
        reply.setId(id);
        reply.setVersionNo(1L);

        // 更新される値
        reply.setNo(5L);
        reply.setCreatedBy(user);
        reply.setUpdatedBy(user);

        // 更新されない値
        reply.setCreatedAt(new GregorianCalendar(2009, 1, 1).getTime());
        reply.setUpdatedAt(new GregorianCalendar(2009, 1, 1).getTime());
        reply.setDeleteNo(999L);

        // 更新
        int count = dao.update(reply);
        assertEquals(1, count);

        ReplyCorresponNoSeq actual = dao.findById(id);

        assertEquals(id, actual.getId());
        assertEquals(reply.getNo(), actual.getNo());
        assertEquals(reply.getCreatedBy().getEmpNo(), actual.getCreatedBy().getEmpNo());
        assertEquals(reply.getCreatedAt(), actual.getCreatedAt());
        assertEquals(reply.getUpdatedBy().getEmpNo(), actual.getUpdatedBy().getEmpNo());
        assertEquals(Long.valueOf(2L), actual.getVersionNo()); // 上がっている
        assertEquals(Long.valueOf(0L), actual.getDeleteNo());
        assertNotNull(actual.getUpdatedAt());

        // 変更なし
        assertEquals(Long.valueOf(1L), actual.getParentCorresponId());

        // 更新されない値
        assertFalse(reply.getUpdatedAt().equals(actual.getUpdatedAt()));
        assertFalse(reply.getDeleteNo().equals(actual.getDeleteNo()));
    }

/**
     * {@link ReplyCor.framework.core.daoImpl#update(Entity)のテストケース.
     * キー重複エラー.
     *
     * @throws Exception
     */
    @Test(expected = KeyDuplicateException.class)
    public void testUpdateDuplicate() throws Exception {
        User user = new User();
        user.setEmpNo("USER1");

        // 更新内容の準備
        Long id = 1L;
        ReplyCorresponNoSeq reply = new ReplyCorresponNoSeq();
        reply.setId(id);
        reply.setVersionNo(1L);
        reply.setUpdatedBy(user);

        // 重複する値
        reply.setParentCorresponId(2L);

        // 更新
        dao.update(reply);
        fail("例外が発生していない");
    }

/**
     * {@link Rep.framework.core.daoeqDaoImpl#update(Entity)のテストケース.
     * 排他エラー.
     *
     * @throws Exception
     */
    @Test(expected = StaleRecordException.class)
    public void testUpdateVarsionNo() throws Exception {
        User user = new User();
        user.setEmpNo("USER1");

        // 更新内容の準備
        Long id = 1L;
        ReplyCorresponNoSeq reply = new ReplyCorresponNoSeq();
        reply.setId(id);

        // 排他エラー
        reply.setVersionNo(0L);

        // 更新
        dao.update(reply);
        fail("例外が発生していない");
    }
}
