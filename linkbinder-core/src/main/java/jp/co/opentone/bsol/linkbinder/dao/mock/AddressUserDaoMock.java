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
package jp.co.opentone.bsol.linkbinder.dao.mock;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import jp.co.opentone.bsol.linkbinder.dao.AbstractDao;
import jp.co.opentone.bsol.linkbinder.dao.AddressUserDao;
import jp.co.opentone.bsol.linkbinder.dto.AddressUser;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.code.AddressUserType;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchAddressUserCondition;

@Repository
public class AddressUserDaoMock extends AbstractDao<AddressUser> implements AddressUserDao {

    public AddressUserDaoMock() {
        super("mock");
    }

    /**
     *
     */
    private static final long serialVersionUID = -159141397625699436L;

    public List<AddressUser> findByAddressCorresponGroupId(Long addressCorresponGroupId) {
        AddressUser u;
        User user;
        List<AddressUser> users = new ArrayList<AddressUser>();

        u = new AddressUser();
        u.setId(new Long(1));

        user = new User();
        user.setEmpNo("00001");
        user.setNameE("Test User1");
        u.setUser(user);

        u.setAddressUserType(AddressUserType.ATTENTION);
        users.add(u);

        u = new AddressUser();
        u.setId(new Long(2));

        user = new User();
        user.setEmpNo("00002");
        user.setNameE("Test User2");
        u.setUser(user);

        u.setAddressUserType(AddressUserType.ATTENTION);
        users.add(u);

        return users;
    }

    public List<AddressUser> findSendApplyUser(SearchAddressUserCondition condition) {
        return new ArrayList<AddressUser>();
    }

    public List<AddressUser> findSendApplyUserForPersonInCharge(SearchAddressUserCondition condition) {
        return new ArrayList<AddressUser>();
    }

    public Integer deleteByAddressCorresponGroupId(AddressUser addressUser) {
        return 0;
    }

}
