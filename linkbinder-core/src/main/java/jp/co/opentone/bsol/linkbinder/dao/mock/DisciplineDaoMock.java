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

import jp.co.opentone.bsol.framework.core.dao.KeyDuplicateException;
import jp.co.opentone.bsol.framework.core.dao.RecordNotFoundException;
import jp.co.opentone.bsol.framework.core.dao.StaleRecordException;
import jp.co.opentone.bsol.linkbinder.dao.AbstractDao;
import jp.co.opentone.bsol.linkbinder.dao.DisciplineDao;
import jp.co.opentone.bsol.linkbinder.dto.Discipline;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchDisciplineCondition;

/**
 *
 * @author opentone
 *
 */
public class DisciplineDaoMock extends AbstractDao<Discipline> implements DisciplineDao {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -8534011508218766184L;

    /**
     * @param namespace
     */
    public DisciplineDaoMock() {
        super("mock");
    }

    public int count(SearchDisciplineCondition condition) {
        return 3;
    }

    public List<Discipline> find(SearchDisciplineCondition condition) {
        User loginUser = new User();
        loginUser.setEmpNo("ZZA01");
        loginUser.setNameE("Taro Yamada");

        List<Discipline> lstDis = new ArrayList<Discipline>();
        Discipline dis = new Discipline();
        dis.setDisciplineCd("TEST");
        dis.setName("Test First");
        dis.setId(1L);
        dis.setProjectId("PJ1");
        dis.setProjectNameE("Test Project1");
        dis.setCreatedBy(loginUser);
        dis.setUpdatedBy(loginUser);
        dis.setVersionNo(1L);
        dis.setDeleteNo(0L);
        lstDis.add(dis);

        dis = new Discipline();
        dis.setDisciplineCd("TEST2");
        dis.setName("Test Second");
        dis.setId(2L);
        dis.setProjectId("PJ1");
        dis.setProjectNameE("Test Project1");
        dis.setCreatedBy(loginUser);
        dis.setUpdatedBy(loginUser);
        dis.setVersionNo(1L);
        dis.setDeleteNo(0L);
        lstDis.add(dis);

        dis = new Discipline();
        dis.setDisciplineCd("TEST3");
        dis.setName("Test Third");
        dis.setId(3L);
        dis.setProjectId("PJ1");
        dis.setProjectNameE("Test Project1");
        dis.setCreatedBy(loginUser);
        dis.setUpdatedBy(loginUser);
        dis.setVersionNo(1L);
        dis.setDeleteNo(0L);
        lstDis.add(dis);


        return lstDis;
    }

    @Override
    public Discipline findById(Long id) throws RecordNotFoundException {
        User loginUser = new User();
        loginUser.setEmpNo("ZZA01");
        loginUser.setNameE("Taro Yamada");

        Discipline dis = new Discipline();
        dis.setDisciplineCd("TEST");
        dis.setName("Test First");
        dis.setId(1L);
        dis.setProjectId("PJ1");
        dis.setProjectNameE("Test Project1");
        dis.setCreatedBy(loginUser);
        dis.setUpdatedBy(loginUser);
        dis.setVersionNo(1L);
        dis.setDeleteNo(0L);

        return dis;
    }

    @Override
    public Long create(Discipline entity) throws KeyDuplicateException {
        return 1L;
    }

    @Override
    public Integer delete(Discipline entity) throws KeyDuplicateException, StaleRecordException {
        return 1;
    }

    @Override
    public Integer update(Discipline entity) throws KeyDuplicateException, StaleRecordException {
        return 1;
    }

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.dao.DisciplineDao#findNotExistCorresponGroup(java.lang.String)
     */
    public List<Discipline> findNotExistCorresponGroup(String projectId, Long siteId) {
        User loginUser = new User();
        loginUser.setEmpNo("ZZA01");
        loginUser.setNameE("Taro Yamada");

        List<Discipline> lstDis = new ArrayList<Discipline>();
        Discipline dis = new Discipline();
        dis.setDisciplineCd("TEST");
        dis.setName("Test First");
        dis.setId(1L);
        dis.setProjectId("PJ1");
        dis.setProjectNameE("Test Project1");
        dis.setCreatedBy(loginUser);
        dis.setUpdatedBy(loginUser);
        dis.setVersionNo(1L);
        dis.setDeleteNo(0L);
        lstDis.add(dis);

        dis = new Discipline();
        dis.setDisciplineCd("TEST2");
        dis.setName("Test Second");
        dis.setId(2L);
        dis.setProjectId("PJ1");
        dis.setProjectNameE("Test Project1");
        dis.setCreatedBy(loginUser);
        dis.setUpdatedBy(loginUser);
        dis.setVersionNo(1L);
        dis.setDeleteNo(0L);
        lstDis.add(dis);

        dis = new Discipline();
        dis.setDisciplineCd("TEST3");
        dis.setName("Test Third");
        dis.setId(3L);
        dis.setProjectId("PJ1");
        dis.setProjectNameE("Test Project1");
        dis.setCreatedBy(loginUser);
        dis.setUpdatedBy(loginUser);
        dis.setVersionNo(1L);
        dis.setDeleteNo(0L);
        lstDis.add(dis);
        return lstDis;
    }

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.dao.DisciplineDao#countCheck(jp.co.opentone.bsol.linkbinder.dto.condition.SearchDisciplineCondition)
     */
    public int countCheck(SearchDisciplineCondition condition) {
        return 0;
    }

}
