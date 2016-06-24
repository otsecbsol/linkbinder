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
package jp.co.opentone.bsol.linkbinder.view.action.control;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import jp.co.opentone.bsol.linkbinder.dto.AddressCorresponGroup;
import jp.co.opentone.bsol.linkbinder.dto.AddressUser;
import jp.co.opentone.bsol.linkbinder.dto.Correspon;
import jp.co.opentone.bsol.linkbinder.dto.PersonInCharge;
import jp.co.opentone.bsol.linkbinder.dto.code.AddressType;
import jp.co.opentone.bsol.linkbinder.view.correspon.CorresponEditPage;


/**
 * @author opentone
 */
public class CorresponEditPageElementControlTest {

    /**
     * テスト対象.
     */
    private CorresponEditPageElementControl control;
    private CorresponEditPage page;
    private Correspon correspon;

    @Before
    public void setUp() {
        correspon = new Correspon();
        setUpCorrespon(correspon);

        page = new CorresponEditPage();
        page.setCorrespon(correspon);

        control = new CorresponEditPageElementControl();
        control.setUp(page);
    }

    @After
    public void tearDown() {
        // Nothing to do
        agId = 0;
    }

    private void setUpCorrespon(Correspon c) {
        c.setId(1L);
        c.setProjectId("0-0000-0");
        c.setSubject("test");
        c.setBody("This is test.");

        List<AddressCorresponGroup> addresses  = new ArrayList<AddressCorresponGroup>();
        addresses.add(createAddress(AddressType.TO));
        addresses.add(createAddress(AddressType.CC));

        c.setAddressCorresponGroups(addresses);
    }

    private long agId = 0L;
    private AddressCorresponGroup createAddress(AddressType type) {
        AddressCorresponGroup ag = new AddressCorresponGroup();
        ag.setId(++agId);
        ag.setAddressType(type);
        ag.setUsers(new ArrayList<AddressUser>());

        return ag;
    }

    private long auId = 0L;
    private void addAddressUser(AddressCorresponGroup ag) {
        AddressUser au = new AddressUser();
        au.setId(++auId);
        ag.getUsers().add(au);
    }

    private long picId = 0L;
    private void addAddressUserWithPic(AddressCorresponGroup ag) {
        AddressUser au = new AddressUser();
        au.setId(++auId);
        List<PersonInCharge> pics = new ArrayList<PersonInCharge>();
        PersonInCharge pic = new PersonInCharge();
        pic.setId(++picId);
        pics.add(pic);
        au.setPersonInCharges(pics);

        ag.getUsers().add(au);
    }

    private void addAddressUserWithReply(AddressCorresponGroup ag) {
        AddressUser au = new AddressUser();
        au.setId(++auId);
        ag.setReplyCount(1L);
        ag.getUsers().add(au);
    }

    private void removeLastUser(AddressCorresponGroup ag) {
        List<AddressUser> users = ag.getUsers();
        users.remove(users.size() - 1);
    }

    @Test
    public void testIsToAddressAddableRequest() {
        // 依頼文書
        assertTrue(control.isToAddressAddable());

        //  Ccに返信あり
        addAddressUserWithReply(correspon.getCcAddressCorresponGroups().get(0));
        assertTrue(control.isToAddressAddable());

        //  ユーザー設定済
        addAddressUser(correspon.getToAddressCorresponGroups().get(0));
        assertTrue(control.isToAddressAddable());

        //  PIC設定済
        addAddressUserWithPic(correspon.getToAddressCorresponGroups().get(0));
        assertTrue(control.isToAddressAddable());
    }

    @Test
    public void testIsToAddressAllDeletableRequest() {
        // 依頼文書
        assertTrue(control.isToAddressAllDeletable());

        //  Ccに返信あり
        addAddressUserWithReply(correspon.getCcAddressCorresponGroups().get(0));
        assertTrue(control.isToAddressAllDeletable());

        //  ユーザー設定済
        addAddressUser(correspon.getToAddressCorresponGroups().get(0));
        assertTrue(control.isToAddressAllDeletable());

        //  PIC設定済
        addAddressUserWithPic(correspon.getToAddressCorresponGroups().get(0));
        assertTrue(control.isToAddressAllDeletable());

        //  Toに返信あり
        //  PIC設定済は一旦削除する
        removeLastUser(correspon.getToAddressCorresponGroups().get(0));
        addAddressUserWithReply(correspon.getToAddressCorresponGroups().get(0));
        assertTrue(control.isToAddressAllDeletable());
    }

    @Test
    public void testIsToAddressAddableReply() {
        // 返信文書
        correspon.setParentCorresponId(100L);
        assertTrue(control.isToAddressAddable());

        //  Ccに返信あり
        addAddressUserWithReply(correspon.getCcAddressCorresponGroups().get(0));
        assertTrue(control.isToAddressAddable());

        //  ユーザー設定済
        addAddressUser(correspon.getToAddressCorresponGroups().get(0));
        assertTrue(control.isToAddressAddable());

        //  PIC設定済
        addAddressUserWithPic(correspon.getToAddressCorresponGroups().get(0));
        assertTrue(control.isToAddressAddable());
    }

    @Test
    public void testIsToAddressAllDeletableReply() {
        // 返信文書
        correspon.setParentCorresponId(100L);
        assertTrue(control.isToAddressAllDeletable());

        //  Ccに返信あり
        addAddressUserWithReply(correspon.getCcAddressCorresponGroups().get(0));
        assertTrue(control.isToAddressAllDeletable());

        //  ユーザー設定済
        addAddressUser(correspon.getToAddressCorresponGroups().get(0));
        assertTrue(control.isToAddressAllDeletable());

        //  PIC設定済
        addAddressUserWithPic(correspon.getToAddressCorresponGroups().get(0));
        assertTrue(control.isToAddressAllDeletable());

        //  Toに返信あり
        //  PIC設定済は一旦削除する
        removeLastUser(correspon.getToAddressCorresponGroups().get(0));
        addAddressUserWithReply(correspon.getToAddressCorresponGroups().get(0));
        assertTrue(control.isToAddressAllDeletable());
    }

    @Test
    public void testIsCcAddressAddableRequest() {
        // 依頼文書
        assertTrue(control.isCcAddressAddable());

        //  Toに返信あり
        addAddressUserWithReply(correspon.getCcAddressCorresponGroups().get(0));
        assertTrue(control.isCcAddressAddable());

        //  ユーザー設定済
        addAddressUser(correspon.getCcAddressCorresponGroups().get(0));
        assertTrue(control.isCcAddressAddable());

        //  PIC設定済
        addAddressUserWithPic(correspon.getCcAddressCorresponGroups().get(0));
        assertTrue(control.isCcAddressAddable());
    }

    @Test
    public void testIsCcAddressAllDeletableRequest() {
        // 依頼文書
        assertTrue(control.isCcAddressAllDeletable());

        //  Toに返信あり
        addAddressUserWithReply(correspon.getToAddressCorresponGroups().get(0));
        assertTrue(control.isCcAddressAllDeletable());

        //  ユーザー設定済
        addAddressUser(correspon.getCcAddressCorresponGroups().get(0));
        assertTrue(control.isCcAddressAllDeletable());

        //  PIC設定済
        addAddressUserWithPic(correspon.getCcAddressCorresponGroups().get(0));
        assertTrue(control.isCcAddressAllDeletable());

        //  Toに返信あり
        //  PIC設定済は一旦削除する
        removeLastUser(correspon.getCcAddressCorresponGroups().get(0));
        addAddressUserWithReply(correspon.getCcAddressCorresponGroups().get(0));
        assertTrue(control.isCcAddressAllDeletable());
    }

    @Test
    public void testIsCcAddressAddableReply() {
        // 返信文書
        correspon.setParentCorresponId(100L);
        assertTrue(control.isCcAddressAddable());

        //  Toに返信あり
        addAddressUserWithReply(correspon.getToAddressCorresponGroups().get(0));
        assertTrue(control.isCcAddressAddable());

        //  ユーザー設定済
        addAddressUser(correspon.getCcAddressCorresponGroups().get(0));
        assertTrue(control.isCcAddressAddable());

        //  PIC設定済
        addAddressUserWithPic(correspon.getCcAddressCorresponGroups().get(0));
        assertTrue(control.isCcAddressAddable());
    }

    @Test
    public void testIsCcAddressAllDeletableReply() {
        // 返信文書でも削除できる
        correspon.setParentCorresponId(100L);
        assertTrue(control.isCcAddressAllDeletable());

        //  Ccに返信あり
        addAddressUserWithReply(correspon.getCcAddressCorresponGroups().get(0));
        assertTrue(control.isCcAddressAllDeletable());

        //  ユーザー設定済
        addAddressUser(correspon.getCcAddressCorresponGroups().get(0));
        assertTrue(control.isCcAddressAllDeletable());

        //  PIC設定済
        addAddressUserWithPic(correspon.getCcAddressCorresponGroups().get(0));
        assertTrue(control.isCcAddressAllDeletable());

        //  Toに返信あり
        //  PIC設定済は一旦削除する
        removeLastUser(correspon.getCcAddressCorresponGroups().get(0));
        addAddressUserWithReply(correspon.getCcAddressCorresponGroups().get(0));
        assertTrue(control.isCcAddressAllDeletable());
    }

    @Test
    public void testIsFromEditable() {
        assertTrue(control.isFromEditable());

        // 返信文書でもFrom欄を変更できる
        correspon.setParentCorresponId(100L);
        assertTrue(control.isFromEditable());
    }

    @Test
    public void testIsCorresponStatusEditable() {
        assertTrue(control.isCorresponStatusEditable());

        // 返信文書でもStatus欄を変更できる
        correspon.setParentCorresponId(100L);
        assertTrue(control.isCorresponStatusEditable());
    }
}
