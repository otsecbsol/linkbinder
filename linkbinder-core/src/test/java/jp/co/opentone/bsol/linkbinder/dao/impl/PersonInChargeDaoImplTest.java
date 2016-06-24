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
import jp.co.opentone.bsol.linkbinder.dto.PersonInCharge;
import jp.co.opentone.bsol.linkbinder.dto.User;

/**
 * PersonInChargeDaoImplのテストケース.
 * @author opentone
 */
public class PersonInChargeDaoImplTest extends AbstractDaoTestCase {

    @Resource
    private PersonInChargeDaoImpl dao;

    /**
     * {@link PersonInChargeDaoImpl#create(jp.co.opentone.bsol.linkbinder.dto.PersonInCharge)}
     * のテストケース. コレポン文書新規登録の検証.
     * @throws Exception
     */
    @Test
    public void testCreate() throws Exception {
        // ログインユーザーの準備
        User login = new User();
        login.setEmpNo("ZZA01");

        // 登録内容の準備
        PersonInCharge p = new PersonInCharge();
        p.setAddressUserId(2L);

        User u = new User();
        u.setEmpNo("ZZA02");
        p.setUser(u);

        p.setCreatedBy(login);
        p.setUpdatedBy(login);

        // 登録
        Long id = new Long(0L);

        // id（自動採番）を最大値にするためのループ
        while (true) {
            try {
                id = dao.create(p);
                break;
            } catch (KeyDuplicateException e) {
                continue;
            }
        }

        assertNotNull(id);

        PersonInCharge actual = dao.findById(id);

        assertEquals(id, actual.getId());
        assertEquals(p.getAddressUserId(), actual.getAddressUserId());
        assertEquals(p.getUser().getEmpNo(), actual.getUser().getEmpNo());
        assertEquals(p.getCreatedBy().getEmpNo(), actual.getCreatedBy().getEmpNo());
        assertEquals(p.getUpdatedBy().getEmpNo(), actual.getUpdatedBy().getEmpNo());
        assertEquals(Long.valueOf(0L), actual.getDeleteNo());
        assertNotNull(actual.getCreatedAt());
        assertNotNull(actual.getUpdatedAt());
    }

    /**
     * {@link PersonInChargeDaoImpl#findByAddressUserId}のテストケース.
     * @throws Exception
     */
    @Test
    public void testFindByAddressUserId() throws Exception {
        List<PersonInCharge> list = dao.findByAddressUserId(4L);

        // 取得したオブジェクトと、Excelファイルに定義済の期待値を比較する
        assertNotNull(list);
        assertDataSetEquals(newDataSet("PersonInChargeDaoImplTest_testFindByAddressUserId_expected.xls"), list);
    }

    /**
     * {@link PersonInChargeDaoImpl#deleteByAddressUserId(jp.co.opentone.bsol.linkbinder.dto.CorresponCustomField)}のテストケース.
     * 削除処理の検証.
     * @throws Exception
     */
    @Test
    public void testDeleteByCorresponId() throws Exception {

        // ログインユーザーの準備
        User login = new User();
        login.setEmpNo("ZZA01");

        // 登録内容の準備
        PersonInCharge personInCharge = new PersonInCharge();
        personInCharge.setAddressUserId(2L);
        personInCharge.setUpdatedBy(login);

        Integer actual = dao.deleteByAddressUserId(personInCharge);

        assertNotNull(actual);

        try {
            dao.findById(5L);
            fail("例外が発生していない");
        } catch (RecordNotFoundException e) {
            try {
                dao.findById(1L);
            } catch (RecordNotFoundException ex) {
                fail("データを取得できない");
            }


        }

    }
}
