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
package jp.co.opentone.bsol.linkbinder.service.common.impl;

import static org.junit.Assert.*;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import jp.co.opentone.bsol.framework.core.auth.AuthUser;
import jp.co.opentone.bsol.framework.core.auth.AuthenticateException;
import jp.co.opentone.bsol.framework.core.auth.ExpiredPasswordException;
import jp.co.opentone.bsol.framework.core.dao.RecordNotFoundException;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.test.AbstractTestCase;
import jp.co.opentone.bsol.linkbinder.dao.impl.UserDaoImpl;
import jp.co.opentone.bsol.linkbinder.dao.impl.UserProfileDaoImpl;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.UserProfile;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.service.MockAbstractService;
import jp.co.opentone.bsol.linkbinder.service.common.LoginService;
import mockit.Mock;
import mockit.MockUp;


/**
 * {@link LoginServiceImpl}のテストケース.
 * @author opentone
 */
public class LoginServiceImplTest extends AbstractTestCase {

    /**
     * テスト対象.
     */
    @Resource
    private LoginService service;

    @BeforeClass
    public static void testSetUp() {
        new MockAbstractService();
    }

    @AfterClass
    public static void testTeardown() {
        new MockAbstractService().tearDown();;
    }

    /**
     * テスト前準備.
     */
    @Before
    public void setUp() {
    }

    /**
     * 後始末.
     */
    @After
    public void tearDown() {
    }

    /**
     * {@link LoginService#login(String, String)}のテスト.
     * ユーザーID、パスワードが正しく入力され認証に成功する場合の検証.
     * ユーザー設定テーブルに新しいレコードが作成されることを検証.
     * @throws Exception
     */
    @Test
    public void testLoginSuccess() throws Exception {
        String userId = "ZZA01";
        String password = "linkbinder";

        // Daoから返されるダミー認証情報を設定
        AuthUser actual = new AuthUser();
        actual.setUserId(userId);
        User user = new User();
        user.setEmpNo(userId);

        UserProfile u = new UserProfile();
        u.setFeedKey("abc");

        // Mock準備
        new MockUp<UserDaoImpl>() {
            @Mock AuthUser authenticate(String userId, String password)
                    throws AuthenticateException, ExpiredPasswordException {
                return actual;
            }
            @Mock User findByEmpNo(String empNo) throws RecordNotFoundException {
                return user;
            }
        };
        new MockUp<UserProfileDaoImpl>() {
            @Mock UserProfile findByEmpNo(String userId) {
                return u;
            }
            @Mock Long create(UserProfile actual) {
                return 1L;
            }
        };

        // テスト対象実行、戻り値を確認
        User login = service.login(userId, password);
        assertNotNull(login);
        assertEquals(userId, login.getEmpNo());
    }

    /**
     * {@link LoginService#login(String, String)}のテスト.
     * ユーザーID、パスワードが正しく入力され認証に成功する場合の検証.
     * ユーザー設定テーブルのレコードが更新されることを検証.
     * @throws Exception
     */
    @Test
    public void testLoginSuccessUpdate() throws Exception {
        String userId = "ZZA01";
        String password = "linkbinder";

        // Daoから返されるダミー認証情報を設定
        AuthUser actual = new AuthUser();
        actual.setUserId(userId);
        User user = new User();
        user.setEmpNo(userId);
        user.setUserProfileId(10L);

        UserProfile u = new UserProfile();
        u.setFeedKey("abc");

        // Mock準備
        new MockUp<UserDaoImpl>() {
            @Mock AuthUser authenticate(String userId, String password)
                    throws AuthenticateException, ExpiredPasswordException {
                return actual;
            }
            @Mock User findByEmpNo(String empNo) throws RecordNotFoundException {
                return user;
            }
        };
        new MockUp<UserProfileDaoImpl>() {
            @Mock UserProfile findByEmpNo(String userId) {
                return u;
            }
            @Mock int update(UserProfile actual) {
                return 1;
            }
        };

        // テスト対象実行、戻り値を確認
        User login = service.login(userId, password);
        assertNotNull(login);
        assertEquals(userId, login.getEmpNo());
    }

    /**
     * {@link LoginService#login(String, String)}のテスト.
     * 認証に失敗した場合の検証.
     * @throws Exception
     */
    @Test
    public void testLoginAuthenticateFailure() throws Exception {
        String userId = "ZZA01";
        String password = "linkbinder";

        // Daoからthrowされる認証例外を設定
        new MockUp<UserDaoImpl>() {
            @Mock AuthUser authenticate(String userId, String password)
                    throws AuthenticateException, ExpiredPasswordException {
                throw new AuthenticateException();
            }
        };

        // テスト対象実行、戻り値を確認
        try {
            service.login(userId, password);
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.LOGIN_FAILED, actual.getMessageCode());
        }
    }

    /**
     * {@link LoginService#login(String, String)}のテスト.
     * パスワードの有効期限が過ぎている場合の検証.
     * @throws Exception
     */
    @Test
    public void testLoginExpiredPassword() throws Exception {
        String userId = "ZZA01";
        String password = "linkbinder";

        // Daoからthrowされるパスワード期限切れ例外を設定
        new MockUp<UserDaoImpl>() {
            @Mock AuthUser authenticate(String userId, String password)
                    throws AuthenticateException, ExpiredPasswordException {
                throw new ExpiredPasswordException();
            }
        };

        // テスト対象実行、戻り値を確認
        try {
            service.login(userId, password);
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.PASSWORD_EXPIRED, actual.getMessageCode());
        }
    }

    /**
     * {@link LoginService#login(String, String)}のテスト.
     * 引数のユーザーIDが不正な場合の検証.
     * @throws Exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testLoginIllegalUserId() throws Exception {
        service.login("", "linkbinder");
    }
    /**
     * {@link LoginService#login(String, String)}のテスト.
     * 引数のパスワードが不正な場合の検証.
     * @throws Exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testLoginIllegalPassword() throws Exception {
        service.login("ZZA01", null);
    }
}
