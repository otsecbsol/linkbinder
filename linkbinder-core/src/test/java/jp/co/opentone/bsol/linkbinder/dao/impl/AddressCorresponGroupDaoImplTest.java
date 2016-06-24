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

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.dbunit.dataset.ITable;
import org.junit.Test;

import jp.co.opentone.bsol.framework.core.dao.KeyDuplicateException;
import jp.co.opentone.bsol.framework.core.dao.RecordNotFoundException;
import jp.co.opentone.bsol.framework.test.AbstractDaoTestCase;
import jp.co.opentone.bsol.linkbinder.dto.AddressCorresponGroup;
import jp.co.opentone.bsol.linkbinder.dto.AddressUser;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroup;
import jp.co.opentone.bsol.linkbinder.dto.PersonInCharge;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.code.AddressType;

/**
 * {@link AddressCorresponGroupDaoImpl}のテストケース.
 * @author opentone
 */
public class AddressCorresponGroupDaoImplTest extends AbstractDaoTestCase {

    /**
     * テスト対象.
     */
    @Resource
    private AddressCorresponGroupDaoImpl dao;

    /**
     * 該当データがある場合の検証.
     * @throws Exception
     */
    @Test
    public void testFindByCorresponId() throws Exception {
        List<AddressCorresponGroup> aus = dao.findByCorresponId(1L);

        assertNotNull(aus);

        // 検証のため、各宛先毎の宛先-ユーザーを一つにまとめたリストを作る
        List<AddressUser> users = new ArrayList<AddressUser>();
        for (AddressCorresponGroup g : aus) {
            users.addAll(g.getUsers());
        }

        // 検証のため、各宛先-ユーザー毎の担当者を一つにまとめたリストを作る
        List<PersonInCharge> pics = new ArrayList<PersonInCharge>();
        for (AddressUser u : users) {
            pics.addAll(u.getPersonInCharges());
        }

        assertDataSetEquals(
                            newDataSet("AddressCorresponGroupDaoImplTest_testFindByCorresponId_expected.xls"),
                            aus,
                            users,
                            pics);

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
     * {@link AddressCorresponGroupDaoImpl#create(jp.co.opentone.bsol.linkbinder.dto.AddressCorresponGroup)}
     * のテストケース. コレポン文書新規登録の検証.
     * @throws Exception
     */
    @Test
    public void testCreate() throws Exception {
        // ログインユーザーの準備
        User login = new User();
        login.setEmpNo("ZZA01");

        // 登録内容の準備
        AddressCorresponGroup a = new AddressCorresponGroup();
        a.setCorresponId(3L);

        CorresponGroup c = new CorresponGroup();
        c.setId(7L);
        a.setCorresponGroup(c);

        a.setAddressType(AddressType.TO);
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

        AddressCorresponGroup actual = dao.findById(id);

        assertEquals(id, actual.getId());
        assertEquals(a.getCorresponId(), actual.getCorresponId());
        assertEquals(a.getCorresponGroup().getId(), actual.getCorresponGroup().getId());
        assertEquals(a.getAddressType(), actual.getAddressType());
        assertEquals(a.getCreatedBy().getEmpNo(), actual.getCreatedBy().getEmpNo());
        assertEquals(a.getUpdatedBy().getEmpNo(), actual.getUpdatedBy().getEmpNo());
        assertEquals(Long.valueOf(0L), actual.getDeleteNo());
        assertNotNull(actual.getCreatedAt());
        assertNotNull(actual.getUpdatedAt());
    }

    /**
     * {@link AddressCorresponGroupDaoImpl#deleteByCorresponId(jp.co.opentone.bsol.linkbinder.dto.CorresponCustomField)}のテストケース.
     * 削除処理の検証.
     * @throws Exception
     */
    @Test
    public void testDeleteByCorresponId() throws Exception {

        // ログインユーザーの準備
        User login = new User();
        login.setEmpNo("ZZA01");

        // 登録内容の準備
        AddressCorresponGroup addressCorresponGroup = new AddressCorresponGroup();
        addressCorresponGroup.setCorresponId(1L);
        addressCorresponGroup.setUpdatedBy(login);

        Integer actual = dao.deleteByCorresponId(addressCorresponGroup);

        assertNotNull(actual);

        AddressCorresponGroup addressCorresponGroupData = new AddressCorresponGroup();

        try {
            addressCorresponGroupData = dao.findById(1L);
            fail("例外が発生していない");
        } catch (RecordNotFoundException e) {
            try {
                addressCorresponGroupData = dao.findById(5L);
            } catch (RecordNotFoundException ex) {
                fail("データを取得できない");
            }
        }
    }


    /**
     * {@link AddressCorresponGroupDaoImpl#update(jp.co.opentone.bsol.linkbinder.dto.AddressCorresponGroup)}
     * のテストケース.
     * @throws Exception
     */
    @Test
    public void testUpdate() throws Exception {
        // 更新前のデータを取得
        AddressCorresponGroup before = dao.findById(1L);

        AddressCorresponGroup addressGroup = new AddressCorresponGroup();
        addressGroup.setId(before.getId());
        addressGroup.setCorresponId(2L);
        CorresponGroup group = new CorresponGroup();
        group.setId(3L);
        addressGroup.setCorresponGroup(group);
        addressGroup.setAddressType(AddressType.CC);
        User loginUser = new User();
        loginUser.setEmpNo("80001");
        loginUser.setNameE("Test User");
        addressGroup.setUpdatedBy(loginUser);

        int record = dao.update(addressGroup);

        // 更新後のデータを取得
        AddressCorresponGroup actual = dao.findById(before.getId());

        assertTrue(1 == record);

        assertEquals(before.getId(), actual.getId());
        assertEquals(addressGroup.getCorresponId(), actual.getCorresponId());
        assertEquals(addressGroup.getCorresponGroup().getId(), actual.getCorresponGroup().getId());
        assertEquals(addressGroup.getAddressType(), actual.getAddressType());
        assertEquals(before.getCreatedBy().getEmpNo(), actual.getCreatedBy().getEmpNo());
        assertEquals(before.getCreatedAt(), actual.getCreatedAt());
        assertEquals(loginUser.getEmpNo(), actual.getUpdatedBy().getEmpNo());
        assertNotNull(actual.getUpdatedAt());
        assertFalse(before.getUpdatedAt().equals(actual.getUpdatedAt()));
        assertTrue(before.getDeleteNo().equals(actual.getDeleteNo()));
    }


    /**
     * {@link CorresponTypeDaoImpl#delete(jp.co.opentone.bsol.linkbinder.dto.AddressCorresponGroup)}
     * のテストケース.
     * @throws Exception
     */
    @Test
    public void testDelete() throws Exception {
        // 削除前のデータを取得
        AddressCorresponGroup before = dao.findById(1L);

        AddressCorresponGroup addressGroup = new AddressCorresponGroup();
        addressGroup.setId(before.getId());
        User loginUser = new User();
        loginUser.setEmpNo("80001");
        loginUser.setNameE("Test User");
        addressGroup.setUpdatedBy(loginUser);

        dao.delete(addressGroup);

        // 削除したデータを取得
        String sql = "select * from address_correspon_group where id =" + before.getId();
        ITable actual = getConnection().createQueryTable("actual", sql);

        assertEquals(before.getId().toString(), actual.getValue(0, "id").toString());
        assertEquals(before.getCorresponId().toString(),
                     actual.getValue(0, "correspon_id").toString());
        assertEquals(before.getCorresponGroup().getId().toString(),
                     actual.getValue(0, "correspon_group_id").toString());
        assertEquals(before.getAddressType().getValue().toString(),
                     actual.getValue(0, "address_type").toString());
        assertEquals(before.getCreatedBy().getEmpNo(), actual.getValue(0, "created_by"));
        assertEquals(before.getCreatedAt(), actual.getValue(0, "created_at"));
        assertEquals(loginUser.getEmpNo(), actual.getValue(0, "updated_by"));
        assertNotNull(actual.getValue(0, "updated_at"));
        assertFalse(before.getUpdatedAt().equals(actual.getValue(0, "updated_at")));
        assertFalse(String.valueOf(before.getDeleteNo()).equals(
                            actual.getValue(0, "delete_no").toString()));
    }
}
