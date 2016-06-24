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
package jp.co.opentone.bsol.linkbinder.dto;

import static org.junit.Assert.*;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.time.DateUtils;
import org.junit.Before;
import org.junit.Test;

import jp.co.opentone.bsol.framework.test.AbstractTestCase;
import jp.co.opentone.bsol.linkbinder.dto.code.AddressType;
import jp.co.opentone.bsol.linkbinder.dto.code.AddressUserType;

public class CorresponTest extends AbstractTestCase {

    private Correspon correspon;

    @Before
    public void setUp() {
        correspon = new Correspon();
    }

    @Test
    public void testProperties() throws Exception {
        Correspon expected = new Correspon();
        initProperties(expected);
        PropertyUtils.copyProperties(correspon, expected);

        assertEquals(expected.toString(), correspon.toString());
    }

    @Test
    public void testGetToAddressCorresponGroups() {
        Correspon c = new Correspon();
        c.setAddressCorresponGroups(createAddressCorresponGroups());

        List<AddressCorresponGroup> to = c.getToAddressCorresponGroups();
        assertNotNull(to);

        for (AddressCorresponGroup ag : to) {
            assertEquals(AddressType.TO, ag.getAddressType());
        }
    }

    @Test
    public void testGetToAddressCorresponGroupsNull() {
        Correspon c = new Correspon();
        // 宛先情報未設定で実行

        List<AddressCorresponGroup> to = c.getToAddressCorresponGroups();
        assertNotNull(to);
        assertTrue(to.isEmpty());
    }

    @Test
    public void testGetCcAddressCorresponGroups() {
        Correspon c = new Correspon();
        c.setAddressCorresponGroups(createAddressCorresponGroups());

        List<AddressCorresponGroup> cc = c.getCcAddressCorresponGroups();
        assertNotNull(cc);

        for (AddressCorresponGroup ag : cc) {
            assertEquals(AddressType.CC, ag.getAddressType());
        }
    }

    @Test
    public void testGetCcAddressCorresponGroupsNull() {
        Correspon c = new Correspon();
        // 宛先情報未設定で実行

        List<AddressCorresponGroup> cc = c.getCcAddressCorresponGroups();
        assertNotNull(cc);
        assertTrue(cc.isEmpty());
    }

    /**
     * @return
     */
    private List<AddressCorresponGroup> createAddressCorresponGroups() {
        List<AddressCorresponGroup> groups = new ArrayList<AddressCorresponGroup>();
        AddressCorresponGroup ag;
        CorresponGroup g;
        List<AddressUser> users;
        AddressUser au;
        User u;

        ag = new AddressCorresponGroup();
        ag.setId(1L);
        ag.setAddressType(AddressType.TO);
        g = new CorresponGroup();
        g.setId(11L);
        ag.setCorresponGroup(g);

        users = new ArrayList<AddressUser>();

        au = new AddressUser();
        au.setId(111L);
        au.setAddressUserType(AddressUserType.ATTENTION);
        u = new User();
        u.setEmpNo("00001");
        au.setUser(u);

        users.add(au);
        ag.setUsers(users);
        groups.add(ag);

        ag = new AddressCorresponGroup();
        ag.setId(2L);
        ag.setAddressType(AddressType.TO);
        g = new CorresponGroup();
        g.setId(22L);
        ag.setCorresponGroup(g);

        users = new ArrayList<AddressUser>();

        au = new AddressUser();
        au.setId(222L);
        au.setAddressUserType(AddressUserType.ATTENTION);
        u = new User();
        u.setEmpNo("00002");
        au.setUser(u);

        users.add(au);
        ag.setUsers(users);
        groups.add(ag);

        ag = new AddressCorresponGroup();
        ag.setId(3L);
        ag.setAddressType(AddressType.CC);
        g = new CorresponGroup();
        g.setId(33L);
        ag.setCorresponGroup(g);

        users = new ArrayList<AddressUser>();

        au = new AddressUser();
        au.setId(333L);
        au.setAddressUserType(AddressUserType.NORMAL_USER);
        u = new User();
        u.setEmpNo("00003");
        au.setUser(u);

        users.add(au);
        ag.setUsers(users);
        groups.add(ag);

        return groups;
    }

    private void initProperties(Correspon c) throws ParseException {
        c.setId(1L);
        c.setCorresponNo("YOC:OT:IT-00001");
        c.setSubject("test");
        c.setBody("This is test.");
        User u = new User();
        c.setCreatedBy(u);
        c.setCreatedAt(DateUtils.parseDate("2009-04-01", new String[]{ "yyyy-MM-dd" }));
        u = new User();
        c.setUpdatedBy(u);
        c.setUpdatedAt(DateUtils.parseDate("2009-05-02", new String[]{ "yyyy-MM-dd" }));
        c.setDeleteNo(0L);
        c.setVersionNo(1L);
    }
}
