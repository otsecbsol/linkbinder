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
import jp.co.opentone.bsol.linkbinder.dao.AddressCorresponGroupDao;
import jp.co.opentone.bsol.linkbinder.dto.AddressCorresponGroup;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroup;
import jp.co.opentone.bsol.linkbinder.dto.code.AddressType;

@Repository
public class AddressCorresponGroupDaoMock extends AbstractDao<AddressCorresponGroup> implements
        AddressCorresponGroupDao {

    public AddressCorresponGroupDaoMock() {
        super("mock");
    }

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public List<AddressCorresponGroup> findByCorresponId(Long corresponId) {
        AddressCorresponGroup g;
        CorresponGroup cg;
        List<AddressCorresponGroup> groups = new ArrayList<AddressCorresponGroup>();

        g = new AddressCorresponGroup();
        g.setId(new Long(1));

        cg = new CorresponGroup();
        cg.setName("YOC:IT");
        g.setCorresponGroup(cg);

        g.setAddressType(AddressType.TO);
        groups.add(g);

        g = new AddressCorresponGroup();
        g.setId(new Long(2));

        cg = new CorresponGroup();
        cg.setName("YOC:PIPING");
        g.setCorresponGroup(cg);

        g.setAddressType(AddressType.CC);
        groups.add(g);

        return groups;
    }

    public Integer deleteByCorresponId(AddressCorresponGroup addressCorresponGroup) {
        return 0;
    }

}
