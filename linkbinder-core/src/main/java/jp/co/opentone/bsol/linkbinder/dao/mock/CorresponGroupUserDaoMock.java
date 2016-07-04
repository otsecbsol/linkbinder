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

import jp.co.opentone.bsol.framework.core.dao.KeyDuplicateException;
import jp.co.opentone.bsol.linkbinder.dao.AbstractDao;
import jp.co.opentone.bsol.linkbinder.dao.CorresponGroupUserDao;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroupUser;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroupUserMapping;
import jp.co.opentone.bsol.linkbinder.dto.User;

@Repository
public class CorresponGroupUserDaoMock extends AbstractDao<CorresponGroupUser> implements
        CorresponGroupUserDao {

    public CorresponGroupUserDaoMock() {
        super("mock");
    }

    /**
     *
     */
    private static final long serialVersionUID = 2281517404428789272L;

    public List<CorresponGroupUser> findByCorresponGroupId(Long corresponId) {

        CorresponGroupUser gu;
        User u;
        List<CorresponGroupUser> result = new ArrayList<CorresponGroupUser>();

        gu = new CorresponGroupUser();
        gu.setId(1L);
        u = new User();
        u.setEmpNo("ZZA91");
        u.setNameE("Name1");
        gu.setUser(u);
        result.add(gu);

        gu = new CorresponGroupUser();
        gu.setId(2L);
        u = new User();
        u.setEmpNo("ZZA92");
        u.setNameE("Name2");
        gu.setUser(u);
        result.add(gu);

        gu = new CorresponGroupUser();
        gu.setId(3L);
        u = new User();
        u.setEmpNo("ZZA93");
        u.setNameE("Name3");
        gu.setUser(u);
        result.add(gu);

        return result;
    }

    public CorresponGroupUser findByEmpNo(Long corresponId, String empNo) {
        CorresponGroupUser gu;
        User u;

        gu = new CorresponGroupUser();
        gu.setId(1L);
        u = new User();
        u.setEmpNo("ZZA91");
        u.setNameE("Name1");
        gu.setUser(u);

        return gu;
    }

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.dao.CorresponGroupUserDao#deleteByCorresponGroupId(java.lang.Long, jp.co.opentone.bsol.linkbinder.dto.User)
     */
    public Integer deleteByCorresponGroupId(Long corresponGroupId, User updateUser)
        throws KeyDuplicateException{
        return 4;
    }

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.dao.CorresponGroupUserDao#findCorresponGroupUserMapping(java.lang.String)
     */
    public List<CorresponGroupUserMapping> findCorresponGroupUserMapping(String projectId) {
        return null;
    }

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.dao.CorresponGroupUserDao#findByProjectId(java.lang.String)
     */
    public List<CorresponGroupUser> findByProjectId(String projectId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<CorresponGroupUserMapping> findCorresponGroupIdUserMapping(String projectId) {
        // TODO 自動生成されたメソッド・スタブ
        return null;
    }

    @Override
    public List<CorresponGroupUser> findProjectUserWithGroupByProjectId(String projectId) {
        // TODO 自動生成されたメソッド・スタブ
        return null;
    }
}
