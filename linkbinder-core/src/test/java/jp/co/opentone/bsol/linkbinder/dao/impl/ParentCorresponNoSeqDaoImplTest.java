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
import jp.co.opentone.bsol.linkbinder.dto.ParentCorresponNoSeq;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.condition.ParentCorresponNoSeqCondition;

/**
 * ReplyCorresponNoSeqDaoImplのテストケース.
 * @author opentone
 */
public class ParentCorresponNoSeqDaoImplTest extends AbstractDaoTestCase {

    /**
     * テスト対象.
     */
    @Resource
    private ParentCorresponNoSeqDaoImpl dao;

    /**
     * {@link ReplyCorresponNoSeqDaoImpl#findById(java.lang.Long)}のテストケース.
     * @throws Exception
     */
    @Test
    public void testFindById_1() throws Exception {
        Long id = 1L;

        ParentCorresponNoSeq reply = dao.findById(id);

        assertNotNull(reply);
        assertDataSetEquals(
                            newDataSet("ParentCorresponNoSeqDaoImplTest_testFindById_expected.xls"),
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
        ParentCorresponNoSeqCondition condition = new ParentCorresponNoSeqCondition();
        condition.setSiteId(1L);
        condition.setDisciplineId(1L);

        ParentCorresponNoSeq reply = dao.findForUpdate(condition);

        assertNotNull(reply);
        assertDataSetEquals(
                            newDataSet("ParentCorresponNoSeqDaoImplTest_testFindForUpdate_expected.xls"),
                            reply);
    }

    /**
     * {@link ReplyCorresponNoSeqDaoImpl#searchForUpdate(java.lang.Long)}
     * のテストケース.
     * @throws Exception
     */
    @Test
    public void testFindForUpdate_2() throws Exception {
        ParentCorresponNoSeqCondition condition = new ParentCorresponNoSeqCondition();
        condition.setSiteId(99L);
        condition.setDisciplineId(99L);

        ParentCorresponNoSeq reply = dao.findForUpdate(condition);

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
        ParentCorresponNoSeq parent = new ParentCorresponNoSeq();
        // 登録される値
        parent.setSiteId(3L);
        parent.setDisciplineId(2L);
        parent.setNo(1L);
        parent.setCreatedBy(user);
        parent.setUpdatedBy(user);

        // 登録されない値
        parent.setCreatedAt(new GregorianCalendar(2009, 1, 1).getTime());
        parent.setUpdatedAt(new GregorianCalendar(2009, 1, 1).getTime());
        parent.setVersionNo(999L);
        parent.setDeleteNo(999L);

        // 登録
        Long id = null;
        while (true) {
            try {
                id = dao.create(parent);
                break;
            } catch (KeyDuplicateException e) {
                continue;
            } catch (Exception e) {
                break;
            }
        }
        assertNotNull(id);

        ParentCorresponNoSeq actual = dao.findById(id);

        assertEquals(id, actual.getId());
        assertEquals(parent.getSiteId(), actual.getSiteId());
        assertEquals(parent.getDisciplineId(), actual.getDisciplineId());
        assertEquals(parent.getNo(), actual.getNo());
        assertEquals(parent.getCreatedBy().getEmpNo(), actual.getCreatedBy().getEmpNo());
        assertEquals(parent.getUpdatedBy().getEmpNo(), actual.getUpdatedBy().getEmpNo());
        assertEquals(Long.valueOf(1L), actual.getVersionNo());
        assertEquals(Long.valueOf(0L), actual.getDeleteNo());
        assertNotNull(actual.getCreatedAt());
        assertNotNull(actual.getUpdatedAt());

        // 登録されない値
        assertFalse(parent.getCreatedAt().equals(actual.getCreatedAt()));
        assertFalse(parent.getUpdatedAt().equals(actual.getUpdatedAt()));
        assertFalse(parent.getVersionNo().equals(actual.getVersionNo()));
        assertFalse(parent.getDeleteNo().equals(actual.getDeleteNo()));
    }

    /**
     * のテストケース.
     * @throws Exception
     */
    @Test(expected = KeyDuplicateException.class)
    public void testCreateDuplication() throws Exception {
        User user = new User();
        user.setEmpNo("USER1");

        // 登録内容の準備
        ParentCorresponNoSeq parent = new ParentCorresponNoSeq();
        parent.setSiteId(1L); // 重複
        parent.setDisciplineId(1L); // 重複
        parent.setNo(1L);
        parent.setCreatedBy(user);
        parent.setUpdatedBy(user);

        // 登録
        dao.create(parent);
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
        ParentCorresponNoSeq parent = new ParentCorresponNoSeq();
        parent.setId(id);
        parent.setVersionNo(1L);

        // 更新される値
        parent.setSiteId(3L);
        parent.setDisciplineId(2L);
        parent.setUpdatedBy(user);

        // 更新されない値
        parent.setUpdatedAt(new GregorianCalendar(2009, 1, 1).getTime());
        parent.setDeleteNo(999L);

        // 更新
        int count = dao.update(parent);
        assertEquals(1, count);

        ParentCorresponNoSeq actual = dao.findById(id);

        assertEquals(id, actual.getId());
        assertEquals(parent.getSiteId(), actual.getSiteId());
        assertEquals(parent.getDisciplineId(), actual.getDisciplineId());
        assertEquals(parent.getUpdatedBy().getEmpNo(), actual.getUpdatedBy().getEmpNo());
        assertEquals(Long.valueOf(2L), actual.getVersionNo()); // 上がっている
        assertEquals(Long.valueOf(0L), actual.getDeleteNo());
        assertNotNull(actual.getUpdatedAt());

        // 変更なし
        assertEquals("TEST", actual.getCreatedBy().getEmpNo());
        assertEquals(Long.valueOf(2L), actual.getNo());

        // 更新されない値
        assertFalse(parent.getUpdatedAt().equals(actual.getUpdatedAt()));
        assertFalse(parent.getDeleteNo().equals(actual.getDeleteNo()));
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
        ParentCorresponNoSeq parent = new ParentCorresponNoSeq();
        parent.setId(id);
        parent.setVersionNo(1L);

        // 更新される値
        parent.setNo(5L);
        parent.setCreatedBy(user);
        parent.setUpdatedBy(user);

        // 更新されない値
        parent.setCreatedAt(new GregorianCalendar(2009, 1, 1).getTime());
        parent.setUpdatedAt(new GregorianCalendar(2009, 1, 1).getTime());
        parent.setDeleteNo(999L);

        // 更新
        int count = dao.update(parent);
        assertEquals(1, count);

        ParentCorresponNoSeq actual = dao.findById(id);

        assertEquals(id, actual.getId());
        assertEquals(parent.getNo(), actual.getNo());
        assertEquals(parent.getCreatedBy().getEmpNo(), actual.getCreatedBy().getEmpNo());
        assertEquals(parent.getCreatedAt(), actual.getCreatedAt());
        assertEquals(parent.getUpdatedBy().getEmpNo(), actual.getUpdatedBy().getEmpNo());
        assertEquals(Long.valueOf(2L), actual.getVersionNo()); // 上がっている
        assertEquals(Long.valueOf(0L), actual.getDeleteNo());
        assertNotNull(actual.getUpdatedAt());

        // 変更なし
        assertEquals(Long.valueOf(1L), actual.getSiteId());
        assertEquals(Long.valueOf(1L), actual.getDisciplineId());

        // 更新されない値
        assertFalse(parent.getUpdatedAt().equals(actual.getUpdatedAt()));
        assertFalse(parent.getDeleteNo().equals(actual.getDeleteNo()));
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
        ParentCorresponNoSeq parent = new ParentCorresponNoSeq();
        parent.setId(id);
        parent.setVersionNo(1L);
        parent.setUpdatedBy(user);

        // 重複する値
        parent.setSiteId(2L); // 重複
        parent.setDisciplineId(2L); // 重複

        // 更新
        dao.update(parent);
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
        ParentCorresponNoSeq parent = new ParentCorresponNoSeq();
        parent.setId(id);

        // 排他エラー
        parent.setVersionNo(0L);

        // 更新
        dao.update(parent);
        fail("例外が発生していない");
    }
}
