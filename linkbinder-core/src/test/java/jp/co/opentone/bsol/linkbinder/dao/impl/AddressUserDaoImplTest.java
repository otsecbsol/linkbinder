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
import jp.co.opentone.bsol.linkbinder.dto.AddressUser;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.code.AddressUserType;

/**
 * {@link AddressUserDaoImpl}のテストケース.
 * @author opentone
 */
public class AddressUserDaoImplTest extends AbstractDaoTestCase {

    /**
     * テスト対象.
     */
    @Resource
    private AddressUserDaoImpl dao;

    /**
     * 該当データがある場合の検証.
     * @throws Exception
     */
    @Test
    public void testFindByAddressCorresponGroupId() throws Exception {
        List<AddressUser> aus = dao.findByAddressCorresponGroupId(1L);

        assertNotNull(aus);
        assertDataSetEquals(
                            newDataSet("AddressUserDaoImplTest_testFindByAddressCorresponGroupId_expected.xls"),
                            aus);

    }

    /**
     * 該当データが無い場合の検証.
     * @throws Exception
     */
    @Test
    public void testFindByAddressCorresponGroupIdEmpty() throws Exception {
        assertEquals(0, dao.findByAddressCorresponGroupId(-1L).size());
    }

    /**
     * {@link AddressUserDaoImpl#create(jp.co.opentone.bsol.linkbinder.dto.AddressUser)}
     * のテストケース. コレポン文書新規登録の検証.
     * @throws Exception
     */
    @Test
    public void testCreate() throws Exception {
        // ログインユーザーの準備
        User login = new User();
        login.setEmpNo("ZZA01");

        // 登録内容の準備
        AddressUser a = new AddressUser();
        a.setAddressCorresponGroupId(3L);

        User u = new User();
        u.setEmpNo("ZZA02");
        a.setUser(u);

        a.setAddressUserType(AddressUserType.NORMAL_USER);
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

        AddressUser actual = dao.findById(id);

        assertEquals(id, actual.getId());
        assertEquals(a.getAddressCorresponGroupId(), actual.getAddressCorresponGroupId());
        assertEquals(a.getUser().getEmpNo(), actual.getUser().getEmpNo());
        assertEquals(a.getAddressUserType(), actual.getAddressUserType());
        assertEquals(a.getCreatedBy().getEmpNo(), actual.getCreatedBy().getEmpNo());
        assertEquals(a.getUpdatedBy().getEmpNo(), actual.getUpdatedBy().getEmpNo());
        assertEquals(Long.valueOf(0L), actual.getDeleteNo());
        assertNotNull(actual.getCreatedAt());
        assertNotNull(actual.getUpdatedAt());
    }

    /**
     * {@link AddressUserDaoImpl#deleteByAddressCorresponGroupId(jp.co.opentone.bsol.linkbinder.dto.CorresponCustomField)}のテストケース.
     * 削除処理の検証.
     * @throws Exception
     */
    @Test
    public void testDeleteByAddressCorresponGroupId() throws Exception {

        // ログインユーザーの準備
        User login = new User();
        login.setEmpNo("ZZA01");

        // 登録内容の準備
        AddressUser addressUser = new AddressUser();
        addressUser.setAddressCorresponGroupId(1L);
        addressUser.setUpdatedBy(login);

        Integer actual = dao.deleteByAddressCorresponGroupId(addressUser);

        assertNotNull(actual);

        AddressUser addressUserData = new AddressUser();

        try {
            addressUserData = dao.findById(1L);
            fail("例外が発生していない");
        } catch (RecordNotFoundException e) {
            try {
                addressUserData = dao.findById(5L);
                fail("例外が発生していない");
            } catch (RecordNotFoundException ex) {

            }


        }

    }
}
