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
import jp.co.opentone.bsol.linkbinder.dao.PersonInChargeDao;
import jp.co.opentone.bsol.linkbinder.dto.PersonInCharge;
import jp.co.opentone.bsol.linkbinder.dto.User;

@Repository
public class PersonInChargeDaoMock extends AbstractDao<PersonInCharge> implements PersonInChargeDao {

    public PersonInChargeDaoMock() {
        super("mock");
    }

    /**
     *
     */
    private static final long serialVersionUID = 2281517404428789272L;

    public List<PersonInCharge> findByCorresponId(Long corresponId) {
        PersonInCharge p;
        User u;
        List<PersonInCharge> pics = new ArrayList<PersonInCharge>();

        p = new PersonInCharge();
        p.setId(new Long(1));

        u = new User();
        u.setEmpNo("90001");
        u.setNameE("PIC 1");
        p.setUser(u);
        pics.add(p);

        p = new PersonInCharge();
        p.setId(new Long(2));

        u = new User();
        u.setEmpNo("90002");
        u.setNameE("PIC 2");
        p.setUser(u);
        pics.add(p);

        return pics;
    }

    public List<PersonInCharge> findByAddressUserId(Long addressUserId) {
        return null;
    }

    public Integer deleteByAddressUserId(PersonInCharge personInCharge) {
        return 0;
    }

}
