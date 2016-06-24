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

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.co.opentone.bsol.framework.core.ProcessContext;
import jp.co.opentone.bsol.framework.core.SuppressTrace;
import jp.co.opentone.bsol.framework.core.auth.AuthUser;
import jp.co.opentone.bsol.framework.core.auth.AuthenticateException;
import jp.co.opentone.bsol.framework.core.auth.ExpiredPasswordException;
import jp.co.opentone.bsol.framework.core.config.SystemConfig;
import jp.co.opentone.bsol.framework.core.dao.KeyDuplicateException;
import jp.co.opentone.bsol.framework.core.dao.RecordNotFoundException;
import jp.co.opentone.bsol.framework.core.dao.StaleRecordException;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.core.util.ArgumentValidator;
import jp.co.opentone.bsol.framework.core.util.DateUtil;
import jp.co.opentone.bsol.framework.core.util.HashUtil;
import jp.co.opentone.bsol.linkbinder.dao.UserDao;
import jp.co.opentone.bsol.linkbinder.dao.UserProfileDao;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.UserProfile;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.service.AbstractService;
import jp.co.opentone.bsol.linkbinder.service.common.LoginService;

/**
 *
 * 当システムのログイン・ログアウトに関するサービスを提供する.
 * @author opentone
 */
@Service
@Transactional(rollbackFor = { ServiceAbortException.class })
public class LoginServiceImpl extends AbstractService implements LoginService {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 1749606388424343930L;

    /**
     * ホーム画面に関するサービス.
     */
    @Resource
    private HomeServiceImpl homeService;

    // ----- ダミーデータ -----
    /**
     * ダミーユーザーID
     */
    public static final String DUMMY_EMP_NO = "dummy01";
    /**
     * セキュリティレベル
     */
    public static final String SECURITYLEVEL = null;
    /**
     * ダミー苗字
     */
    public static final String LASTNAME = "SETTING";
    /**
     * ダミー名（英語）
     */
    public static final String NAMEE = "SETTING MEMBER";
    /**
     * ダミー名
     */
    public static final String NAMEJ = "初期設定";
    /**
     * ダミーシステム管理者フラグ
     */
    public static final String SYSADMINFLG = "X";


    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.service.common.LoginService#login(java.lang.String, java.lang.String)
     */
    public User login(
                    String userId,
                    @SuppressTrace // パスワードはトレースログに出力しない
                    String password)
            throws ServiceAbortException {

        ArgumentValidator.validateNotEmpty(userId);
        ArgumentValidator.validateNotEmpty(password);

        //  接続先データベース切り替えのため現在実行中ユーザーを指定されたユーザーIDに設定
        ProcessContext pc = ProcessContext.getCurrentContext();
        pc.setValue(SystemConfig.KEY_USER_ID, userId);

        User login = doLogin(userId, password);
        if (login != null) {
            processAfterLoggedIn(login);
        }

        return login;
    }


    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.service.common.LoginService
     *     #authenticateWithFeedKey(java.lang.String, java.lang.String)
     */
    public User authenticateWithFeedKey(
            String userId,
            @SuppressTrace // RSSフィードキーはトレースログに出力しない
            String feedKey)
        throws ServiceAbortException {
        ArgumentValidator.validateNotEmpty(userId);
        ArgumentValidator.validateNotEmpty(feedKey);

        //  接続先データベース切り替えのため現在実行中ユーザーを指定されたユーザーIDに設定
        ProcessContext pc = ProcessContext.getCurrentContext();
        pc.setValue(SystemConfig.KEY_USER_ID, userId);

        try {
            User u = findUser(userId);
            UserProfile p = findUserProfile(userId);
            if (feedKey.equals(p.getFeedKey())) {
                return u;
            }
            throw new RecordNotFoundException(String.format("User %s is invalid.", userId));
        } catch (RecordNotFoundException e) {
            throw new ServiceAbortException(e.getMessage(),
                                        e,
                                        ApplicationMessageCode.LOGIN_FAILED);
        }
    }

    private User findUser(String userId) throws RecordNotFoundException {
        UserDao dao = getDao(UserDao.class);
        return dao.findByEmpNo(userId);
    }

    private UserProfile findUserProfile(String userId) throws RecordNotFoundException {
        UserProfileDao dao = getDao(UserProfileDao.class);
        return dao.findByEmpNo(userId);
    }

    /**
     * システムにログインし、認証に成功した場合は当該ユーザーの情報を収集して返す.
     * @param userId ユーザーID
     * @param password パスワード
     * @return ユーザー情報
     * @throws ServiceAbortException 何らかの理由で認証に失敗
     */
    private User doLogin(String userId, String password) throws ServiceAbortException {
        UserDao dao = getDao(UserDao.class);
        try {
            //  認証に成功した場合、ユーザーの情報を再取得して返す
            AuthUser u = dao.authenticate(userId, password);
            return dao.findByEmpNo(u.getUserId());
        } catch (AuthenticateException e) {
            throw new ServiceAbortException(e.getMessage(),
                                e,
                                ApplicationMessageCode.LOGIN_FAILED);
        } catch (ExpiredPasswordException e) {
            throw new ServiceAbortException(e.getMessage(),
                                e,
                                ApplicationMessageCode.PASSWORD_EXPIRED);
        } catch (RecordNotFoundException e) {
            throw new ServiceAbortException(e.getMessage(),
                                e,
                                ApplicationMessageCode.LOGIN_FAILED);
        }
    }

    public User dummyLogin() throws ServiceAbortException {
         User user = new User();
        user.setEmpNo(DUMMY_EMP_NO);
        user.setSecurityLevel(SECURITYLEVEL);
        user.setLastName(LASTNAME);
        user.setNameE(NAMEE);
        user.setNameJ(NAMEJ);
        user.setSysAdminFlg(SYSADMINFLG);
        //  接続先データベース切り替えのため現在実行中ユーザーを指定されたユーザーIDに設定
        ProcessContext pc = ProcessContext.getCurrentContext();
        pc.setValue(SystemConfig.KEY_USER_ID, DUMMY_EMP_NO);

        return user;
    }

    /**
     * システムの利用権限をチェックし、権限がある場合は当該ユーザーの情報を収集して返す.
     * @param userId ユーザーID
     * @return ユーザー情報
     * @throws RecordNotFoundException 利用権限がない場合
     */
    private User doAuthorize(String userId) throws RecordNotFoundException {
        UserDao dao = getDao(UserDao.class);
        return dao.findByEmpNo(userId);
    }

    /**
     * ユーザーの最終ログイン時刻を更新する.
     * @param user ユーザー
     * @throws ServiceAbortException 更新に失敗
     */
    private void updateLastLoggedInAt(User user) throws ServiceAbortException {
        UserProfileDao dao = getDao(UserProfileDao.class);
        UserProfile profile = createUserProfileInstance(user);
        try {
            if (profile.isNew()) {
                dao.create(profile);
            } else {
                dao.update(profile);
            }
        } catch (KeyDuplicateException e) {
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_USER_SETTINGS_ALREADY_UPDATED);
        } catch (StaleRecordException e) {
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_USER_SETTINGS_ALREADY_UPDATED);
        }
    }

    private UserProfile createUserProfileInstance(User user)
        throws ServiceAbortException {
        UserProfile p = new UserProfile();

        p.setId(user.getUserProfileId());
        p.setUser(user);
        p.setLastLoggedInAt(DateUtil.getNow());
        if (p.isNew()) {
            p.setCreatedBy(user);
            p.setFeedKey(HashUtil.getRandomString(user.getEmpNo()));
        }
        p.setUpdatedBy(user);
        p.setVersionNo(user.getUserProfileVersionNo());

        return p;
    }

    /**
     * feedKeyが存在しない場合、値を設定する.
     * @param user ユーザー
     * @throws ServiceAbortException 値の設定に失敗
     */
    private void updateFeedKey(User user) throws ServiceAbortException {
        UserProfileDao dao = getDao(UserProfileDao.class);
        UserProfile p = dao.findByEmpNo(user.getEmpNo());
        if (StringUtils.isEmpty(p.getFeedKey())) {
            p.setFeedKey(HashUtil.getRandomString(user.getEmpNo()));
            p.setUpdatedBy(user);
            try {
                dao.update(p);
            } catch (KeyDuplicateException e) {
                throw new ServiceAbortException(
                    ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_USER_SETTINGS_ALREADY_UPDATED);
            } catch (StaleRecordException e) {
                throw new ServiceAbortException(
                    ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_USER_SETTINGS_ALREADY_UPDATED);
            }
        }

    }

    /**
     * ログイン認証後の各種登録処理を行う.
     * @param login ログインユーザ情報
     * @throws ServiceAbortException 登録に失敗した場合
     */
    private void processAfterLoggedIn(User login) throws ServiceAbortException {
        // 最終更新日時を記録
        updateLastLoggedInAt(login);
        // FeedKeyを設定
        updateFeedKey(login);
    }

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.service.common.LoginService#logout(java.lang.String)
     */
    public void logout(String userId) throws ServiceAbortException {
        // TODO Auto-generated method stub
    }
}
