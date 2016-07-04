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

import jp.co.opentone.bsol.framework.core.auth.AuthUser;
import jp.co.opentone.bsol.framework.core.auth.AuthenticateException;
import jp.co.opentone.bsol.framework.core.auth.ExpiredPasswordException;
import jp.co.opentone.bsol.framework.core.dao.KeyDuplicateException;
import jp.co.opentone.bsol.framework.core.dao.RecordNotFoundException;
import jp.co.opentone.bsol.framework.core.dao.StaleRecordException;
import jp.co.opentone.bsol.linkbinder.dao.AbstractLegacyDao;
import jp.co.opentone.bsol.linkbinder.dao.UserDao;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroup;
import jp.co.opentone.bsol.linkbinder.dto.ProjectUser;
import jp.co.opentone.bsol.linkbinder.dto.SysUsers;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchAvailableSystemCondition;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchUserCondition;

@Repository
public class UserDaoMock extends AbstractLegacyDao<User> implements UserDao {

    public UserDaoMock() {
        super("mock");
    }

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 1158533541650152561L;

    @Override
    public User findByEmpNo(String empNo) {
        User u;

        u = new User();
        u.setEmpNo("ZZA01");
        u.setLastName("Aaa");
        u.setNameE("aaa Aaa");

        return u;
    }

    @Override
    public List<ProjectUser> findProjectUser(SearchUserCondition condition) {

        User u;
        ProjectUser pu;
        CorresponGroup cg;

        List<ProjectUser> result = new ArrayList<ProjectUser>();
        pu = new ProjectUser();
        pu.setProjectId("PJ1");
        pu.setSecurityLevel("40");

        u = new User();
        u.setEmpNo("ZZA01");
        u.setLastName("Aaa");
        u.setNameE("aaa Aaa");
        pu.setUser(u);

        cg = new CorresponGroup();
        cg.setId(1L);
        cg.setName("Group1");
        pu.setDefaultCorresponGroup(cg);

        result.add(pu);

        pu = new ProjectUser();
        pu.setProjectId("PJ1");
        pu.setSecurityLevel("40");

        u = new User();
        u.setEmpNo("ZZA02");
        u.setLastName("Bbb");
        u.setNameE("bbb Bbb");
        pu.setUser(u);

        cg = new CorresponGroup();
        cg.setId(2L);
        cg.setName("Group2");
        pu.setDefaultCorresponGroup(cg);

        result.add(pu);

        pu = new ProjectUser();
        pu.setProjectId("PJ1");
        pu.setSecurityLevel("40");

        u = new User();
        u.setEmpNo("80001");
        u.setLastName("Ichiro");
        u.setNameE("Ichiro Suzuki");
        pu.setUser(u);

        cg = new CorresponGroup();
        cg.setId(3L);
        cg.setName("Group2");
        pu.setDefaultCorresponGroup(cg);

        result.add(pu);

        pu = new ProjectUser();
        pu.setProjectId("PJ1");
        pu.setSecurityLevel("40");

        u = new User();
        u.setEmpNo("80002");
        u.setLastName("Jiro");
        u.setNameE("Jiro Watanabe");
        pu.setUser(u);

        cg = new CorresponGroup();
        cg.setId(4L);
        cg.setName("Group2");
        pu.setDefaultCorresponGroup(cg);

        result.add(pu);

        pu = new ProjectUser();
        pu.setProjectId("PJ1");
        pu.setSecurityLevel("40");

        u = new User();
        u.setEmpNo("80003");
        u.setLastName("Saburo");
        u.setNameE("Saburo Eryto");
        pu.setUser(u);

        cg = new CorresponGroup();
        cg.setId(5L);
        cg.setName("Group2");
        pu.setDefaultCorresponGroup(cg);

        result.add(pu);

        return result;
    }

    @Override
    public List<String> findEmpNo() {
        List<String> empNo = new ArrayList<String>();
        empNo.add("100001");
        return empNo;
    }

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.framework.auth.Authenticator#authenticate(java.lang.String, java.lang.String)
     */
    @Override
    public AuthUser authenticate(String userId, String password) throws AuthenticateException,
        ExpiredPasswordException {
        throw new ExpiredPasswordException(userId);
    }

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.dao.UserDao#count(jp.co.opentone.bsol.linkbinder.dto.condition.SearchUserCondition)
     */
    @Override
    public int count(SearchUserCondition condition) {
        return 0;
    }

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.dao.UserDao#countCheck(jp.co.opentone.bsol.linkbinder.dto.condition.SearchUserCondition)
     */
    @Override
    public int countCheck(SearchUserCondition condition) {
        return 0;
    }

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.dao.UserDao#countAvailableAppSystemCode(SearchAuthenticatedAplSystemCodeCondition)
     */
    public int countAvailableAppSystemCode(SearchAvailableSystemCondition condition) {
        return 0;
    }

    @Override
    public List<User> findSendApplyUser(SearchUserCondition condition) {
        return new ArrayList<User>();
    }

    @Override
    public void deleateUser(SysUsers user) throws KeyDuplicateException, StaleRecordException {
        // TODO 自動生成されたメソッド・スタブ

    }

    @Override
    public void updateUser(SysUsers user) throws KeyDuplicateException, StaleRecordException {
        // TODO 自動生成されたメソッド・スタブ

    }

    @Override
    public void creteUser(SysUsers user) throws KeyDuplicateException {
        // TODO 自動生成されたメソッド・スタブ

    }

    @Override
    public String findBySysUserId(SysUsers user) throws RecordNotFoundException {
        // TODO 自動生成されたメソッド・スタブ
        return null;
    }

    @Override
    public List<User> findAll() throws RecordNotFoundException {
        // TODO 自動生成されたメソッド・スタブ
        return null;
    }

    @Override
    public void updateUserSetting(SysUsers user)
        throws RecordNotFoundException, KeyDuplicateException, StaleRecordException {
        // TODO 自動生成されたメソッド・スタブ

        }

    @Override
    public int countSystemAdminUser() throws RecordNotFoundException {
        // TODO 自動生成されたメソッド・スタブ
        return 0;
    }

    @Override
    public void create(SysUsers user) throws KeyDuplicateException {
        // TODO 自動生成されたメソッド・スタブ

    }

    @Override
    public int countEmail(String empNo, String mailAddress) {
        // TODO Auto-generated method stub
        return 0;
    }
}
