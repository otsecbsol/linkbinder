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
package jp.co.opentone.bsol.linkbinder.dao;

import java.util.List;

import jp.co.opentone.bsol.framework.core.auth.Authenticator;
import jp.co.opentone.bsol.framework.core.dao.KeyDuplicateException;
import jp.co.opentone.bsol.framework.core.dao.LegacyGenericDao;
import jp.co.opentone.bsol.framework.core.dao.RecordNotFoundException;
import jp.co.opentone.bsol.framework.core.dao.StaleRecordException;
import jp.co.opentone.bsol.linkbinder.dto.ProjectUser;
import jp.co.opentone.bsol.linkbinder.dto.SysUsers;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchUserCondition;

/**
 * ユーザーに関する情報を操作するDao.
 * <p>
 * 従業員番号を指定して、ユーザーに関連する情報を取得したり
 * ユーザー認証を行うためのメソッドを提供する.
 *
 * @author opentone
 *
 */
public interface UserDao extends LegacyGenericDao<User>, Authenticator {

    /**
     * 従業員番号でユーザーを取得する.
     * @param empNo
     *            従業員番号
     * @return ユーザー
     * @throws RecordNotFoundException
     *             該当レコードが無い
     */
    User findByEmpNo(String empNo) throws RecordNotFoundException;

    /**
     * 指定された条件に該当するプロジェクトユーザーを返す.
     * @param condition
     *            検索条件
     * @return プロジェクトに所属するユーザー
     */
    List<ProjectUser> findProjectUser(SearchUserCondition condition);

    /**
     * マスタから全ユーザーの従業員番号を取得する.
     * @return 全ユーザーの従業員番号
     */
    List<String> findEmpNo();

    /**
     * 指定された条件に該当する件数を取得する.
     * @param condition 検索条件
     * @return 件数
     */
    int count(SearchUserCondition condition);

    /**
     * 指定された条件に該当する件数を取得する.（エラーチェック）.
     * @param condition 検索条件
     * @return 件数
     */
    int countCheck(SearchUserCondition condition);

    /**
     * 指定されたユーザー検索条件に設定された全てのユーザーを返す.
     * @param condition
     *            ユーザー検索条件
     * @return ユーザー情報
     */
    List<User> findSendApplyUser(SearchUserCondition condition);

    /**
     * 指定されたレコードを削除する.
     * @param user
     * @return
     * @throws KeyDuplicateException, StaleRecordException
     */
    void deleateUser(SysUsers user) throws KeyDuplicateException, StaleRecordException, RecordNotFoundException;

    /**
     * 指定されたレコードを更新する.
     * @param user
     * @return
     * @throws KeyDuplicateException, StaleRecordException
     */
    void updateUser(SysUsers user) throws KeyDuplicateException, StaleRecordException, RecordNotFoundException;

    /**
     * レコードを追加する.
     * @param user
     * @return
     * @throws KeyDuplicateException
     * @throws StaleRecordException
     */
    void creteUser(SysUsers user) throws KeyDuplicateException;
    /**
     * システム管理者レコードを追加する.
     * @param user
     * @return
     * @throws KeyDuplicateException
     * @throws StaleRecordException
     */
    void create(SysUsers user) throws KeyDuplicateException;

    /**
     * 指定された会社名と従業員番号を検索する.
     * @param cnpyId
     * @param empId
     * @return
     */
    String findBySysUserId(SysUsers user) throws RecordNotFoundException;

    /**
     * 指定されたユーザーの設定情報を更新する.
     * @param user ユーザー情報
     * @throws RecordNotFoundException
     * @throws KeyDuplicateException
     * @throws StaleRecordException
     */
    void updateUserSetting(SysUsers user)
            throws RecordNotFoundException,  KeyDuplicateException, StaleRecordException;


    /**
     * SYS_USERSと関連するSYS_PJ_USERSの情報を取得する
     * @return ユーザー情報
     * @throws RecordNotFoundException
     */
    List<User> findAll() throws RecordNotFoundException;

    /**
     * システムユーザ権限を持つユーザー数を取得する.
     * @return ユーザー数
     * @throws RecordNotFoundException
     */
    int countSystemAdminUser() throws RecordNotFoundException;

    /**
     * メールアドレスをキーとして該当するレコード数を取得する.
     * @param empNo ユーザーID
     * @param mailAddress メールアドレス
     * @return レコード数
     */
    int countEmail(String empNo, String mailAddress);
}
