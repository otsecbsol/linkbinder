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

import jp.co.opentone.bsol.framework.core.auth.AuthUser;
import jp.co.opentone.bsol.framework.core.auth.AuthenticateException;
import jp.co.opentone.bsol.framework.core.auth.ExpiredPasswordException;
import jp.co.opentone.bsol.framework.test.AbstractDaoTestCase;
import jp.co.opentone.bsol.linkbinder.dto.ProjectUser;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchUserCondition;

/**
 * {@link UserDaoImpl}のテストケース.
 * @author opentone
 */
public class UserDaoImplTest extends AbstractDaoTestCase {

    /**
     * テスト対象.
     */
    @Resource
    private UserDaoImpl dao;

    /**
     * 該当データがある場合の検証.
     * 全件検索.
     * @throws Exception
     */
    @Test
    public void testFindProjectUser() throws Exception {
        SearchUserCondition c = new SearchUserCondition();
        c.setProjectId("PJ1");
        List<ProjectUser> pus = dao.findProjectUser(c);

        assertNotNull(pus);
        assertDataSetEquals(newDataSet("UserDaoImplTest_testFindProjectUser_expected.xls"), pus);
    }

    /**
     * 該当データがある場合の検証.
     * ページング.
     * @throws Exception
     */
    @Test
    public void testFindProjectUserPaging() throws Exception {
        SearchUserCondition c = new SearchUserCondition();
        c.setProjectId("PJ1");
        // 1ページ5件ずつ表示：2ページ目
        c.setPageNo(2);
        c.setPageRowNum(5);
        List<ProjectUser> pus = dao.findProjectUser(c);

        assertNotNull(pus);
        assertDataSetEquals(newDataSet("UserDaoImplTest_testFindProjectUserPaging_expected.xls"), pus);
    }

    /**
     * 該当データがある場合の検証.
     * ページング.SystemAdmin.
     * @throws Exception
     */
    @Test
    public void testFindProjectUserPagingSystemAdmin() throws Exception {
        SearchUserCondition c = new SearchUserCondition();
        c.setProjectId("PJ1");
        c.setSysAdminFlg("X");
        List<ProjectUser> pus = dao.findProjectUser(c);
        assertNotNull(pus);
        assertTrue(pus.size() == 0);
    }

    /**
     * 該当データがある場合の検証.
     * ページング.ProjectAdmin.
     * @throws Exception
     */
    @Test
    public void testFindProjectUserPagingProjectAdmin() throws Exception {
        SearchUserCondition c = new SearchUserCondition();
        c.setProjectId("PJ1");
        c.setProjectAdmin("X");

        List<ProjectUser> pus = dao.findProjectUser(c);

        assertNotNull(pus);
        assertDataSetEquals(newDataSet("UserDaoImplTest_testFindProjectUserProjectAdmin_expected.xls"), pus);
    }

    /**
     * 該当データがある場合の検証.
     * ページング.GroupAdmin.
     * @throws Exception
     */
    @Test
    public void testFindProjectUserPagingGroupAdmin() throws Exception {
        SearchUserCondition c = new SearchUserCondition();
        c.setProjectId("PJ1");
        c.setGroupAdmin("30");

        List<ProjectUser> pus = dao.findProjectUser(c);

        assertNotNull(pus);
        assertDataSetEquals(newDataSet("UserDaoImplTest_testFindProjectUserGroupAdmin_expected.xls"), pus);
    }

    /**
     * 該当データがある場合の検証.
     * ページング.NormalUser.
     * @throws Exception
     */
    @Test
    public void testFindProjectUserPagingNormalUser() throws Exception {
        SearchUserCondition c = new SearchUserCondition();
        c.setProjectId("PJ1");
        c.setSysAdminFlg("X");
        c.setProjectAdmin("X");
        c.setGroupAdmin("30");
        c.setSecurityLevel("40");

        List<ProjectUser> pus = dao.findProjectUser(c);

        assertNotNull(pus);
        assertDataSetEquals(newDataSet("UserDaoImplTest_testFindProjectUserNormalUser_expected.xls"), pus);
    }

    /**
     * 該当データがある場合の検証.
     * 前方一致検索.
     * @throws Exception
     */
    @Test
    public void testFindProjectUserLikeSearch() throws Exception {
        SearchUserCondition c = new SearchUserCondition();
        c.setProjectId("PJ1");
        c.setEmpNo("ZZA0");
        c.setNameE("T");

        List<ProjectUser> pus = dao.findProjectUser(c);

        assertNotNull(pus);
        assertDataSetEquals(newDataSet("UserDaoImplTest_testFindProjectUserLikeSearch_expected.xls"), pus);
    }

    /**
     * 該当データがある場合の検証.
     * ページング.会社・グループ指定.
     * @throws Exception
     */
    @Test
    public void testFindProjectUserSearch() throws Exception {
        SearchUserCondition c = new SearchUserCondition();
        c.setProjectId("PJ1");
        c.setCompanyId(1L);
        c.setCorresponGroupId(2L);

        List<ProjectUser> pus = dao.findProjectUser(c);

        assertNotNull(pus);
        assertDataSetEquals(newDataSet("UserDaoImplTest_testFindProjectUserSearch_expected.xls"), pus);
    }

    /**
     * 該当データが無い場合の検証.
     * @throws Exception
     */
    @Test
    public void testFindProjectUserEmpty() throws Exception {
        SearchUserCondition c = new SearchUserCondition();
        c.setProjectId("PJ1");
        c.setEmpNo("XXXXX");
        assertEquals(0, dao.findProjectUser(c).size());
    }

    /**
     * NULLのカラムを指定した検索のテストケース.
     * @throws Exception
     */
    @Test
    public void testFindProjectUserConditions() throws Exception {
        SearchUserCondition c = new SearchUserCondition();
        c.setProjectId("PJ1");
        c.setNullColumn("project_company_id");
        List<ProjectUser> pus = dao.findProjectUser(c);

        assertNotNull(pus);
        assertDataSetEquals(newDataSet("UserDaoImplTest_testFindProjectUserConditions_expected.xls"), pus);
    }

    /**
     * NULLのカラムを指定した検索のテストケース.
     * （該当データが無し）
     * @throws Exception
     */
    @Test
    public void testFindProjectUserConditionsEmpty() throws Exception {
        SearchUserCondition c = new SearchUserCondition();
        c.setProjectId("PJ1");
        c.setNullColumn("emp_no");
        assertEquals(0, dao.findProjectUser(c).size());
    }

    /**
     * 該当データがある場合の検証.
     * @throws Exception
     */
    @Test
    public void testFindEmpNo() throws Exception {
        List<String> empNos = dao.findEmpNo();
        assertEquals(164, empNos.size());
    }

    /**
     * {@link UserDaoImpl#authenticate(String, String)}のテストケース.
     * 正常系の検証.
     * @throws Exception
     */
    @Test
    public void testAuthenticateSuccess() throws Exception {

        String userId = "ZZA01";
        String password = "linkbinder";

        AuthUser user = dao.authenticate(userId, password);
        assertNotNull(user);
        assertEquals(userId, user.getUserId());

        //  パスワードが格納されていないことを確認
        assertNull(user.getPassword());
    }

    /**
     * {@link UserDaoImpl#authenticate(String, String)}のテストケース.
     * ユーザーID不正の場合の検証.
     * @throws Exception
     */
    @Test(expected = AuthenticateException.class)
    public void testAuthenticateInvalidUserId() throws Exception {

        String userId = "notexist";
        String password = "linkbinder";

        dao.authenticate(userId, password);
    }

    /**
     * {@link UserDaoImpl#authenticate(String, String)}のテストケース.
     * パスワード不正の場合の検証.
     * @throws Exception
     */
    @Test(expected = AuthenticateException.class)
    public void testAuthenticateInvalidPassword() throws Exception {

        String userId = "ZZA01";
        String password = "xxxxx";

        dao.authenticate(userId, password);
    }

    /**
     * {@link UserDaoImpl#authenticate(String, String)}のテストケース.
     * パスワード有効期限切れの場合の検証.
     * @throws Exception
     */
    @Test(expected = ExpiredPasswordException.class)
    public void testAuthenticateExpired() throws Exception {

        //  テスト、デモユーザーはチェック対象外となっているため
        //  本番用ユーザーIDで確認
        String userId = "00001";
        String password = "linkbinder";

        dao.authenticate(userId, password);
    }

    /**
     * {@link UserDaoImpl#count(jp.co.opentone.bsol.linkbinder.dto.condition.SearchUserCondition)}のテストケース.
     * @throws Exception
     */
    @Test
    public void testCount() throws Exception {
        SearchUserCondition condition = new SearchUserCondition();
        condition.setProjectId("PJ1");

        assertEquals(18, dao.count(condition));
    }

    /**
     * {@link UserDaoImpl#count(jp.co.opentone.bsol.linkbinder.dto.condition.SearchUserCondition)}のテストケース.
     * 該当なし.
     * @throws Exception
     */
    @Test
    public void testCountNoRecord() throws Exception {
        SearchUserCondition condition = new SearchUserCondition();
        condition.setProjectId("PJ1");
        condition.setEmpNo("XXXXX");

        assertEquals(0, dao.count(condition));
    }

    /**
     * {@link UserDaoImpl#countCheck}のテストケース.
     * プロジェクトIDが条件.
     * @throws Exception
     */
    @Test
    public void testCountCheckProjectId() throws Exception {
        SearchUserCondition condition = new SearchUserCondition();
        condition.setProjectId("PJ1");

        assertEquals(18, dao.countCheck(condition));
    }

    /**
     * [{@link UserDaoImpl#countCheck]のテストケース.
     * 従業員番号が条件.
     * @throws Exception
     */
    @Test
    public void testCountCheckEmpNo() throws Exception {
        SearchUserCondition condition = new SearchUserCondition();
        condition.setEmpNo("ZZA02");

        assertEquals(2, dao.countCheck(condition));
    }
}
