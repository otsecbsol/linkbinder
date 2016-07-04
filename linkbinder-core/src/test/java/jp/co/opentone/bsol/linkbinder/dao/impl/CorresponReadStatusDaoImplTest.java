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

import java.util.Date;
import java.util.GregorianCalendar;

import javax.annotation.Resource;

import org.junit.Test;

import jp.co.opentone.bsol.framework.core.dao.Entity;
import jp.co.opentone.bsol.framework.core.dao.KeyDuplicateException;
import jp.co.opentone.bsol.framework.core.dao.RecordNotFoundException;
import jp.co.opentone.bsol.framework.core.dao.StaleRecordException;
import jp.co.opentone.bsol.framework.test.AbstractDaoTestCase;
import jp.co.opentone.bsol.linkbinder.dto.CorresponReadStatus;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.code.ReadStatus;

/**
 * @link CorresponReadStatusDaoImpl}のテストケース.
 * @author opentone
 */
public class CorresponReadStatusDaoImplTest extends AbstractDaoTestCase {

    /**
     * テスト対象.
     */
    @Resource
    private CorresponReadStatusDaoImpl dao;

    /**
     * {@link jp.co.opentone.bsol.linkbinder.dao.AbstractDao#c.framework.core.daoc.framework.core.dao.Entity)} のためのテスト・メソッド.
     */
    @Test
    public void testCreate() throws Exception {
        // ログインユーザーの準備
        User login = new User();
        login.setEmpNo("ZZA02");

        // 登録内容の準備
        CorresponReadStatus status = new CorresponReadStatus();
        status.setEmpNo("ZZA02");
        status.setCorresponId(2L);
        status.setReadStatus(ReadStatus.READ);
        status.setCreatedBy(login);
        status.setUpdatedBy(login);

        // 登録
        Long id = new Long(0L);

        // id（自動採番）を最大値にするためのループ
        while (true) {
            try {
                id = dao.create(status);
                break;
            } catch (KeyDuplicateException e) {
                continue;
            }
        }

        assertNotNull(id);

        CorresponReadStatus actual = dao.findById(id);

        assertEquals(id, actual.getId());
        assertEquals(status.getEmpNo(), actual.getEmpNo());
        assertEquals(status.getCorresponId(), actual.getCorresponId());
        assertEquals(status.getReadStatus(), actual.getReadStatus());
        assertEquals(status.getCreatedBy().getEmpNo(), actual.getCreatedBy().getEmpNo());
        assertEquals(status.getUpdatedBy().getEmpNo(), actual.getUpdatedBy().getEmpNo());
        assertEquals(Long.valueOf(1L), actual.getVersionNo());
        assertEquals(Long.valueOf(0L), actual.getDeleteNo());
        assertNotNull(actual.getCreatedAt());
        assertNotNull(actual.getUpdatedAt());
    }

    /**
     * 一意エラーの場合、{@link KeyDuplicateException}が発生することを検証する.
     * @throws Exception
     */
    @Test(expected = KeyDuplicateException.class)
    public void testCreateDuplicate() throws Exception {
        // ログインユーザーの準備
        User login = new User();
        login.setEmpNo("ZZA01");

        // 更新内容の準備
        CorresponReadStatus status = new CorresponReadStatus();
        status.setEmpNo("ZZA01");
        status.setCorresponId(1L);
        status.setReadStatus(ReadStatus.READ);
        status.setCreatedBy(login);
        status.setUpdatedBy(login);

        dao.create(status);

        fail("例外が発生していない");
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.dao.Abs.framework.core.daoe( Entity )} のためのテスト・メソッド.
     */
    @Test
    public void testUpdate() throws Exception {
        // ログインユーザーの準備
        User login = new User();
        login.setEmpNo("ZZA03");

        // 更新内容の準備
        CorresponReadStatus status = new CorresponReadStatus();
        status.setId(3L);
        status.setReadStatus(ReadStatus.NEW);
        status.setCreatedBy(login);
        status.setUpdatedBy(login);
        status.setVersionNo(0L);

        Integer resultCount = dao.update(status);
        assertEquals(new Integer(1), resultCount);

        CorresponReadStatus actual = dao.findById(status.getId());

        assertEquals(status.getId(), actual.getId());
        assertEquals("ZZA02", actual.getEmpNo()); // Excelデータ参照
        assertEquals(Long.valueOf(1L), actual.getCorresponId()); // Excelデータ参照
        assertEquals(status.getReadStatus(), actual.getReadStatus());
        assertEquals("ZZA02", actual.getCreatedBy().getEmpNo()); // Excelデータ参照
        assertEquals(status.getUpdatedBy().getEmpNo(), actual.getUpdatedBy().getEmpNo());
        assertEquals(Long.valueOf(1L), actual.getVersionNo());
        assertEquals(Long.valueOf(0L), actual.getDeleteNo());
        assertNotNull(actual.getCreatedAt());
        assertNotNull(actual.getUpdatedAt());

        Date createDate = new GregorianCalendar(2009, 3, 2, 10, 20, 31).getTime(); // Excelデータ参照
        Date oldDate = new GregorianCalendar(2009, 3, 2, 10, 40, 31).getTime();

        assertEquals(createDate, actual.getCreatedAt());
        assertFalse(oldDate.equals(actual.getUpdatedAt()));
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.da.framework.core.daoupdate( Entity )} のためのテスト・メソッド.
     * レコードが存在しない場合に、{@link StaleRecordException}が発生することを検証する.
     * @throws Exception
     */
    @Test(expected = StaleRecordException.class)
    public void testUpdateRecordNotFound() throws Exception {
        // ログインユーザーの準備
        User login = new User();
        login.setEmpNo("ZZA03");

        // 更新内容の準備
        CorresponReadStatus status = new CorresponReadStatus();
        status.setId(-1L);
        status.setReadStatus(ReadStatus.NEW);
        status.setCreatedBy(login);
        status.setUpdatedBy(login);
        status.setVersionNo(0L);

        dao.update(status);
        fail("例外が発生していない");
    }


    /**
     * {@link jp.co.opentone.bsol.jcot.framework.core.daotDao#update(Entity)} のためのテスト・メソッド.
     */
    @Test
    public void testUpdateByCorresponId_01() throws Exception {

        // ログインユーザーの準備
        User login = new User();
        login.setEmpNo("ZZA01");

        // 更新内容の準備
        CorresponReadStatus status = new CorresponReadStatus();

        status.setCorresponId(1L);
        status.setReadStatus(ReadStatus.NEW);
        status.setCreatedBy(login);
        status.setUpdatedBy(login);

        Integer resultCount = dao.updateByCorresponId(status);
        assertEquals(new Integer(3), resultCount);

        CorresponReadStatus actual = dao.findById(3L);

        assertEquals(Long.valueOf(3), actual.getId());
        assertEquals("ZZA02", actual.getEmpNo()); // Excelデータ参照
        assertEquals(Long.valueOf(1L), actual.getCorresponId()); // Excelデータ参照
        assertEquals(status.getReadStatus(), actual.getReadStatus());
        assertEquals("ZZA02", actual.getCreatedBy().getEmpNo()); // Excelデータ参照
        assertEquals(status.getUpdatedBy().getEmpNo(), actual.getUpdatedBy().getEmpNo());
        assertEquals(Long.valueOf(1L), actual.getVersionNo());
        assertEquals(Long.valueOf(0L), actual.getDeleteNo());
        assertNotNull(actual.getCreatedAt());
        assertNotNull(actual.getUpdatedAt());

//        Date createDate = new GregorianCalendar(2009, 3, 2, 10, 20, 31).getTime(); // Excelデータ参照
        Date oldDate = new GregorianCalendar(2009, 3, 2, 10, 40, 31).getTime();

//        assertEquals(createDate, actual.getCreatedAt());
        assertFalse(oldDate.equals(actual.getUpdatedAt()));
    }


    /**
     * {@link jp.co.opentone.bsol.framework.core.daostractDao#update(Entity)} のためのテスト・メソッド.
     */
    @Test
    public void testUpdateByCorresponId_02() throws Exception {

        // ログインユーザーの準備
        User login = new User();
        login.setEmpNo("ZZA01");

        // 更新内容の準備
        CorresponReadStatus status = new CorresponReadStatus();

        status.setCorresponId(3L);
        status.setReadStatus(ReadStatus.NEW);
        status.setCreatedBy(login);
        status.setUpdatedBy(login);

        Integer resultCount = dao.updateByCorresponId(status);
        assertEquals(new Integer(1), resultCount);

        CorresponReadStatus actual = dao.findById(6L);

        assertEquals(Long.valueOf(6), actual.getId());
        assertEquals("ZZA01", actual.getEmpNo()); // Excelデータ参照
        assertEquals(Long.valueOf(3L), actual.getCorresponId()); // Excelデータ参照
        assertEquals(status.getReadStatus(), actual.getReadStatus());
        assertEquals("ZZA01", actual.getCreatedBy().getEmpNo()); // Excelデータ参照
        assertEquals(status.getUpdatedBy().getEmpNo(), actual.getUpdatedBy().getEmpNo());
        assertEquals(Long.valueOf(2L), actual.getVersionNo());
        assertEquals(Long.valueOf(0L), actual.getDeleteNo());
        assertNotNull(actual.getCreatedAt());
        assertNotNull(actual.getUpdatedAt());

//        Date createDate = new GregorianCalendar(2009, 3, 2, 10, 20, 31).getTime(); // Excelデータ参照
        Date oldDate = new GregorianCalendar(2009, 3, 2, 10, 40, 31).getTime();

//        assertEquals(createDate, actual.getCreatedAt());
        assertFalse(oldDate.equals(actual.getUpdatedAt()));
    }


    /**
     * {@link jp.c.framework.core.daoao.AbstractDao#update(Entity)} のためのテスト・メソッド.
     * 排他チェックにかかった場合、{@link StaleRecordException}が発生することを検証する.
     * @throws Exception
     */
    @Test(expected = StaleRecordException.class)
    public void testUpdateVersionNo() throws Exception {
        // ログインユーザーの準備
        User login = new User();
        login.setEmpNo("ZZA03");

        // 更新内容の準備
        CorresponReadStatus status = new CorresponReadStatus();
        status.setId(3L);
        status.setReadStatus(ReadStatus.NEW);
        status.setCreatedBy(login);
        status.setUpdatedBy(login);
        // 排他
        status.setVersionNo(9L);

        dao.update(status);

        fail("例外が発生していない");
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.dao.AbstractDao#findById(java.lang.Long)} のためのテスト・メソッド.
     */
    @Test
    public void testFindById() throws Exception {
        CorresponReadStatus actual = dao.findById(1L);

        assertDataSetEquals(newDataSet("CorresponReadStatusDaoImplTest_testFindById_expected.xls"),
                            actual);
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.dao.AbstractDao#findById(java.lang.Long)} のためのテスト・メソッド.
     */
    @Test
    public void testFindByEmpNo() throws Exception {
        CorresponReadStatus actual = dao.findByEmpNo(1L, "ZZA01");

        assertDataSetEquals(newDataSet("CorresponReadStatusDaoImplTest_testFindByEmpNo_expected.xls"),
                            actual);
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.dao.AbstractDao#findById(java.lang.Long)} のためのテスト・メソッド.
     * （存在しないID）
     */
    @Test(expected = RecordNotFoundException.class)
    public void testFindByIdRecordNotFound() throws Exception {
        dao.findById(-1L);
        fail("例外が発生していない");
    }


    /**
     * {@link jp.co.opentone.bsol.linkbinder.dao.AbstractDao#findById(java.lang.Long)} のためのテスト・メソッド.
     * （削除済みレコード）
     */
    @Test(expected = RecordNotFoundException.class)
    public void testFindByIdDeleteRecord() throws Exception {
        dao.findById(2L);
        fail("例外が発生していない");
    }
}
