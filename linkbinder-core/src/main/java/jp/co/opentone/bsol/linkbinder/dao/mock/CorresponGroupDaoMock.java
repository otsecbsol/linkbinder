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
import jp.co.opentone.bsol.linkbinder.dao.CorresponGroupDao;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroup;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroupUser;
import jp.co.opentone.bsol.linkbinder.dto.Discipline;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponGroupCondition;

@Repository
public class CorresponGroupDaoMock extends AbstractDao<CorresponGroup> implements CorresponGroupDao {

    /**
     *
     */
    private static final long serialVersionUID = -6453543351313861778L;

    public CorresponGroupDaoMock() {
        super("mock");
    }

    public List<CorresponGroup> find(SearchCorresponGroupCondition condition) {

        List<CorresponGroup> list = new ArrayList<CorresponGroup>();

        CorresponGroup cg = new CorresponGroup();
        cg.setId((long) 1);
        cg.setProjectId("1");
        cg.setName("Group1");

        list.add(cg);

        cg = new CorresponGroup();
        cg.setId((long) 2);
        cg.setProjectId("2");
        cg.setName("Group2");

        list.add(cg);

        cg = new CorresponGroup();
        cg.setId((long) 3);
        cg.setProjectId("3");
        cg.setName("Group3");

        list.add(cg);

        return list;
    }

    @Override
    public CorresponGroup findById(Long id) {

        CorresponGroup cg = new CorresponGroup();
        cg.setId((long) 1);
        cg.setProjectId("1");
        cg.setName("Group1");

        return cg;
    }

    public List<CorresponGroupUser> findByEmpNo(String projectId, String empNo) {

        List<CorresponGroupUser> cgu = new ArrayList<CorresponGroupUser>();

        return cgu;
    }

    public List<CorresponGroup> findByDisciplineId(Long disciplineId) {
        CorresponGroup cg = new CorresponGroup();
        cg.setId(1L);

        User loginUser = new User();
        loginUser.setEmpNo("ZZA01");
        loginUser.setNameE("Taro Yamada");

        Discipline discipline = new Discipline();
        discipline.setId(1L);
        discipline.setDisciplineCd("TEST");
        discipline.setName("Test Discipline");
        discipline.setCreatedBy(loginUser);
        discipline.setUpdatedBy(loginUser);
        cg.setDiscipline(discipline);

        cg.setCreatedBy(loginUser);
        cg.setUpdatedBy(loginUser);

        List<CorresponGroup> cgList = new ArrayList<CorresponGroup>();
        cgList.add(cg);

        return cgList;
    }

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.dao.CorresponGroupDao#findBySiteId(java.lang.Long)
     */
    public List<CorresponGroup> findBySiteId(SearchCorresponGroupCondition condition) {
        List<CorresponGroup> list = new ArrayList<CorresponGroup>();

        CorresponGroup cg = new CorresponGroup();
        cg.setId((long) 1);
        cg.setProjectId("1");
        cg.setName("Group1");

        list.add(cg);

        cg = new CorresponGroup();
        cg.setId((long) 2);
        cg.setProjectId("2");
        cg.setName("Group2");

        list.add(cg);

        cg = new CorresponGroup();
        cg.setId((long) 3);
        cg.setProjectId("3");
        cg.setName("Group3");

        list.add(cg);

        return list;

    }

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.dao.CorresponGroupDao#countCorresponGroup(jp.co.opentone.bsol.linkbinder.dto.SearchCorresponGroupCondition)
     */
    public int countCorresponGroup(SearchCorresponGroupCondition condition) {
        return 1;
    }

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.dao.CorresponGroupDao#findByEmpNo(java.lang.String, java.lang.String, java.lang.String)
     */
    public List<CorresponGroupUser> findByEmpNo(String projectId, String empNo, String sortColumn) {
        // TODO Auto-generated method stub
        return null;
    }

}
