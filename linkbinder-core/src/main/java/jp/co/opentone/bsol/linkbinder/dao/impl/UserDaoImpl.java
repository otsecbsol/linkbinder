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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import jp.co.opentone.bsol.framework.core.SuppressTrace;
import jp.co.opentone.bsol.framework.core.auth.AuthUser;
import jp.co.opentone.bsol.framework.core.auth.AuthenticateException;
import jp.co.opentone.bsol.framework.core.auth.AuthenticationParameter;
import jp.co.opentone.bsol.framework.core.auth.ExpiredPasswordException;
import jp.co.opentone.bsol.framework.core.dao.KeyDuplicateException;
import jp.co.opentone.bsol.framework.core.dao.RecordNotFoundException;
import jp.co.opentone.bsol.framework.core.dao.StaleRecordException;
import jp.co.opentone.bsol.linkbinder.dao.AbstractLegacyDao;
import jp.co.opentone.bsol.linkbinder.dao.UserDao;
import jp.co.opentone.bsol.linkbinder.dto.ProjectUser;
import jp.co.opentone.bsol.linkbinder.dto.SysUsers;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchUserCondition;
import jp.co.opentone.bsol.linkbinder.util.ValueFormatter;

/**
 * ユーザー情報を操作するDao.
 *
 * @author opentone
 *
 */
@Repository
public class UserDaoImpl extends AbstractLegacyDao<User> implements UserDao {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 368358941071595311L;

    /**
     * このクラスが利用するsqlMap.xmlのnamespace.
     */
    private static final String NAMESPACE = "user";

    /**
     * SQLID: 従業員番号を指定してユーザーを取得する.
     */
    private static final String SQL_FIND_BY_EMP_NO = "findByEmpNo";
    /**
     * SQLID: 検索条件を指定してプロジェクトに所属するユーザーを検索する.
     */
    private static final String SQL_FIND_PROJECT_USER = "findProjectUser";
    /**
     * SQLID: 全ユーザーの従業員番号を取得する.
     */
    private static final String SQL_FIND_EMP_NO = "findEmpNo";

    /**
     * SQLID: ユーザーを認証する.
     */
    private static final String SQL_AUTHENTICATE = "authenticate";


    /**
     * SQLID: 該当するレコードの件数を取得する.
     */
    private static final String SQL_COUNT = "count";

    /**
     * SQLID: 該当するレコードの件数を取得する.(エラーチェック).
     */
    private static final String SQL_COUNT_CHECK = "countCheck";

    /**
     * SQLID: コレポン文書情報を指定してメール受信を許可している担当者を取得.
     */
    private static final String SQL_FIND_SEND_APPLY_USER = "findSendApplyUser";


    /**
     * SQLID: 会社IDと従業員番号をキーにして該当するレコードを取得する.
     */
    private static final String SQL_FIND_SYS_USER = "findBySysUserId";

    /**
     * SQLID: ユーザーを登録する.
     */
    private static final String SQL_CREATED_USER = "createUsers";

    /**
     * SQLID: システム管理者ユーザーを登録する.
     */
    private static final String SQL_CREATED_SYSUSER = "createSysUser";
    /**
     * SQLID: ユーザー情報を更新する.
     */
    private static final String SQL_UPDATE_USER = "updateUsers";
    private static final String SQL_UPDATE_PJ_USER = "updatePjUsers";

    /**
     * SQLID: ユーザー情報を削除する.
     */
    private static final String SQL_DELETE_USER = "deleteUsers";
    private static final String SQL_DELETE_PJ_USER = "deletePjUsers";
    /**
     * SQLID: 検索条件を指定してユーザー情報(SYS_PJ)を取得する.
     */
    private static final String SQL_FIND_ALL = "findBySysUserAll";
    /**
     * SQLID: 従業員番号をキーにして該当するレコードを更新する.
     */
    private static final String SQLUSER_SETTING = "updateUsersSetting";

    /**
     * SQLID: 従業員番号とシステムコードで利用可能システムの該当件数を取得する。(利用可能チェック).
     */
    private static final String SQL_FIND_SYSUSER_COUNT = "findSysUserCount";

    /**
     * SQLID: メールアドレスをキーとして該当するレコード件数を取得する。(利用可能チェック).
     */
    private static final String SQL_COUNT_EMAIL = "countEmail";


    /**
     * 認証に失敗した際に、認証パッケージ<code>pkg_auth.authenticate/code>から返されるOracleエラーコード.
     * <p>
     * ORA-01403 No data found.
     * </p>
     */
    private static final int ERROR_AUTHENTICATION_FAILED = 1403;


    /**
     * パスワードの有効期限が過ぎていることを表すフラグの値.
     * <p>
     * {@link #authenticate(String, String)}で使用する.
     * </p>
     */
    private static final String FLG_EXPIRED_PASSWORD = "2";

    /**
     * 前方一致検索を行うフィールド名.
     */
    private static final List<String> FIELDS;
    static {
        FIELDS = new ArrayList<String>();
        FIELDS.add("empNo");
        FIELDS.add("nameE");
    }

    /**
     * 空のインスタンスを生成する.
     */
    public UserDaoImpl() {
        super(NAMESPACE);
    }

    /*
     * (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.dao.UserDao#findByEmpNo(java.lang.String)
     */
    @Override
    public User findByEmpNo(String empNo) throws RecordNotFoundException {
        User record =
                (User) getSqlMapClientTemplate()
                    .queryForObject(getSqlId(SQL_FIND_BY_EMP_NO), empNo);
        if (record == null) {
            throw new RecordNotFoundException(empNo);
        }
        return record;
    }

    /*
     * (non-Javadoc)
     * @seejp.co.opentone.bsol.linkbinder.dao.UserDao#findProjectUser(jp.co.opentone.bsol.linkbinder.dto.
     * SearchUserCondition)
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<ProjectUser> findProjectUser(SearchUserCondition condition) {
        // 前方一致検索を行う
        SearchUserCondition likeCondition = getLikeSearchCondition(condition, FIELDS);
        int skipResults = condition.getSkipNum();
        int maxResults = condition.getPageRowNum();
        return getSqlMapClientTemplate()
                    .queryForList(getSqlId(SQL_FIND_PROJECT_USER),
                likeCondition,
                skipResults,
                maxResults);
    }

    /*
     * (non-Javadoc)
     * @seejp.co.opentone.bsol.linkbinder.dao.UserDao#findEmpNo()
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<String> findEmpNo() {
        return getSqlMapClientTemplate()
                    .queryForList(getSqlId(SQL_FIND_EMP_NO));
    }

    /* (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.framework.auth.Authenticator#authenticate()
     */
    @Override
    public AuthUser authenticate(
            String userId,
            @SuppressTrace // パスワードはトレースログに出力しない
            String password)
                    throws AuthenticateException,
                    ExpiredPasswordException {

         AuthenticationParameter params = new AuthenticationParameter();
         params.setUserId(userId);
         params.setPassword(null);
         // 登録日を取得
         try {
             AuthenticationParameter idRecord = (AuthenticationParameter) getSqlMapClientTemplate()
             .queryForObject(getSqlId(SQL_AUTHENTICATE), params);
             if (idRecord == null)  {
                 throw new AuthenticateException();
             } else {
                 password = ValueFormatter.formatValueToHash(password, idRecord.getCreatedIdAt());
                 params.setPassword(password);
             }

             //ログイン認証を行なう
             AuthenticationParameter recorde = (AuthenticationParameter) getSqlMapClientTemplate()
                                     .queryForObject(getSqlId(SQL_AUTHENTICATE), params);
             if (recorde == null)  {
                 throw new AuthenticateException();
             }

         } catch (DataIntegrityViolationException  e) {
             handleExceptionAtAuthentication(e);
       }

         // TODO : パスワードの有効期限を検証
        //checkExpiredPassword(params);

        //  認証済ユーザー情報を生成して返す.
        //  パスワードは格納しない
        AuthUser u = new AuthUser();
        u.setUserId(userId);

        return u;

    }


    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.dao.UserDao#count(jp.co.opentone.bsol.linkbinder.dto.condition.SearchUserCondition)
     */
    @Override
    public int count(SearchUserCondition condition) {
        // 前方一致検索を行う
        SearchUserCondition likeCondition = getLikeSearchCondition(condition, FIELDS);
        return Integer.parseInt(getSqlMapClientTemplate().queryForObject(getSqlId(SQL_COUNT),
            likeCondition).toString());
    }

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.dao.UserDao#countCheck(
     * jp.co.opentone.bsol.linkbinder.dto.condition.SearchUserCondition)
     */
    @Override
    public int countCheck(SearchUserCondition condition) {
        return Integer.parseInt(getSqlMapClientTemplate().queryForObject(getSqlId(SQL_COUNT_CHECK),
            condition).toString());
    }

    /**
     * 認証処理で発生した例外を処理する.
     * <p>
     * 認証エラーによる例外の場合例外を{@link AuthenticateException}に変換し、
     * その他の例外の場合はそのままthrowする.
     * </p>
     * @param e 認証処理で発生した例外
     * @throws AuthenticateException 認証に失敗
     */
    private void handleExceptionAtAuthentication(DataIntegrityViolationException e)
        throws AuthenticateException {
        Throwable cause = e.getRootCause();
        if (cause instanceof SQLException
            && ((SQLException) cause).getErrorCode() == ERROR_AUTHENTICATION_FAILED) {
            throw new AuthenticateException();
        } else {
            throw e;
        }
    }

    /*
     * (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.dao.UserDao#findSendApplyUser(jp.co.opentone.bsol.linkbinder.dto.condition
     * .SearchUserCondition)
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<User> findSendApplyUser(SearchUserCondition condition) {
        return getSqlMapClientTemplate()
                .queryForList(getSqlId(SQL_FIND_SEND_APPLY_USER), condition);
    }

    @Override
    public void deleateUser(SysUsers user)
            throws KeyDuplicateException, StaleRecordException, RecordNotFoundException {
        int result = 0;
        if (0 < (result = getSqlMapClientTemplate().delete(getSqlId(SQL_DELETE_USER),
                user))) {
            if (!StringUtils.isEmpty(user.getPjId())) {
                result = getSqlMapClientTemplate().delete(getSqlId(SQL_DELETE_PJ_USER),
                        user);
            }
        } else {
            throw new StaleRecordException();
        }
        if (result == 0) {
            throw new StaleRecordException();
        }
    }

    @Override
    public void updateUser(SysUsers user)
            throws KeyDuplicateException, StaleRecordException, RecordNotFoundException {
        int result = 0;
        if (0 < getSqlMapClientTemplate().update(getSqlId(SQL_UPDATE_USER),
                user)) {
            result = getSqlMapClientTemplate().update(getSqlId(SQL_UPDATE_PJ_USER),
                    user);
        } else {
            throw new KeyDuplicateException();
        }

        if (result == 0) {
            throw new KeyDuplicateException();
        }

    }

    @Override
    public void creteUser(SysUsers user) throws KeyDuplicateException {
        getSqlMapClientTemplate()
                .insert(getSqlId(SQL_CREATED_USER), user);

    }

    @Override
    public void create(SysUsers user) throws KeyDuplicateException {
        getSqlMapClientTemplate()
                .insert(getSqlId(SQL_CREATED_SYSUSER), user);

    }


    @Override
    public String findBySysUserId(SysUsers user) throws RecordNotFoundException {
        String record = (String) getSqlMapClientTemplate()
                .queryForObject(getSqlId(SQL_FIND_SYS_USER), user);
            if (StringUtils.isEmpty(record)) {
                throw new RecordNotFoundException();
            }
            return record;
    }

    @Override
    public void updateUserSetting(SysUsers user)
            throws RecordNotFoundException,  KeyDuplicateException, StaleRecordException  {
            getSqlMapClientTemplate().update(getSqlId(SQLUSER_SETTING),
                    user);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<User> findAll() {
        return getSqlMapClientTemplate()
                .queryForList(getSqlId(SQL_FIND_ALL));
    }


    @Override
    public int countSystemAdminUser() throws RecordNotFoundException {
        return Integer.parseInt(getSqlMapClientTemplate()
                .queryForObject(getSqlId(SQL_FIND_SYSUSER_COUNT)).toString());
    }

    @Override
    public int countEmail(String empNo, String mailAddress) {
        Map<String, String> param = new HashMap<>();
        param.put("empNo", empNo);
        param.put("mailAddress", mailAddress);

        return Integer.parseInt(getSqlMapClientTemplate()
                .queryForObject(getSqlId(SQL_COUNT_EMAIL), param).toString());
    }
}
