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
package jp.co.opentone.bsol.linkbinder.service.admin;

import java.util.List;

import jp.co.opentone.bsol.framework.core.service.IService;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroupUser;
import jp.co.opentone.bsol.linkbinder.dto.ProjectUser;
import jp.co.opentone.bsol.linkbinder.dto.SearchUserResult;
import jp.co.opentone.bsol.linkbinder.dto.SysUsers;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.UserIndex;
import jp.co.opentone.bsol.linkbinder.dto.UserSettings;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchUserCondition;

/**
 * このサービスではユーザー情報に関する処理を提供する.
 * @author opentone
 */
public interface UserService extends IService {

    /**
     * 指定された条件に該当するユーザーを検索して返す.
     * <p>
     * 対象となるのは、現在選択中プロジェクトのユーザー.
     * </p>
     * @param condition
     *            検索条件
     * @return ユーザー
     * @throws ServiceAbortException
     *             検索に失敗
     */
    List<ProjectUser> search(SearchUserCondition condition) throws ServiceAbortException;

    /**
     * 従業員番号でユーザーを取得する.
     * @param empNo
     *            従業員番号
     * @return ユーザー
     * @throws ServiceAbortException
     *             取得に失敗
     */
    User findByEmpNo(String empNo) throws ServiceAbortException;

    /**
     * 指定されたユーザーの所属する活動単位を検索し返す。.
     * @param projectId
     *            プロジェクトID
     * @param empNo
     *            従業員番号
     * @return 活動単位
     * @throws ServiceAbortException
     *             取得に失敗
     */
    List<CorresponGroupUser> searchCorrseponGroup(String projectId,
                                                  String empNo) throws ServiceAbortException;

    /**
     * 指定されたユーザーの所属する活動単位を検索し返す(プロジェクトIDがキー).
     * @param projectId
     *            プロジェクトID
     * @return 活動単位
     * @throws ServiceAbortException
     *             取得に失敗
     */
    List<CorresponGroupUser> searchCorrseponGroupUsers(String projectId) throws ServiceAbortException;

    /**
     * 指定されたユーザーの設定情報を返す.
     * @param userId 従業員番号
     * @return ユーザー設定情報
     * @throws ServiceAbortException 取得に失敗
     */
    UserSettings findUserSettings(String userId) throws ServiceAbortException;

    /**
     * 指定されたユーザー設定情報を保存する.
     * @param settings ユーザー設定情報
     * @throws ServiceAbortException 保存に失敗
     */
    void save(UserSettings settings) throws ServiceAbortException;

    /**
     * 指定された条件に該当する一覧情報を検索して返す.
     * ページング処理を行う.
     * <p>
     * 対象となるのは、現在選択中プロジェクトのユーザー.
     * </p>
     * @param condition
     *            検索条件
     * @return 一覧情報
     * @throws ServiceAbortException
     *             検索に失敗
     */
    SearchUserResult searchPagingList(SearchUserCondition condition) throws ServiceAbortException;

    /**
     * 指定されたユーザー一覧をExcel形式に変換して返す.
     * @param userIndexs ユーザー情報
     * @return Excel変換データ
     * @throws ServiceAbortException 変換に失敗
     */
    byte[] generateExcel(List<UserIndex> userIndexs) throws ServiceAbortException;

    /**
     * 指定されたユーザーのメールアドレスを返す.
     * @param empNo 従業員番号
     * @return メールアドレス
     * @throws ServiceAbortException 取得に失敗
     */
    String findEMailAddress(String empNo) throws ServiceAbortException;

    /**
     * 全ユーザー情報を返す.
     * @return 検索結果。一致結果がない場合はnullを返却する。
     * @throws ServiceAbortException
     *             検索に失敗
     */
    List<User> findAll() throws ServiceAbortException;

    /**
     * 指定されたプロジェクト情報一覧をCSV形式に変換して返す.
     * @param list projects
     *            プロジェクト情報
     * @param csvHeaderPj ヘッダー情報
     * @param filedName フィールド情報
     * @return
     */
    byte[] generateCSV(List<User> listUser) throws ServiceAbortException;

    /**
     * システム管理者権限をもつユーザー数を取得する.
     * @return 検索結果
     * @throws ServiceAbortException
     */
    int getSysUserCount() throws ServiceAbortException;

    /**
     * 指定されたユーザーを保存する.
     *
     * 各要素の処理結果は戻り値の {@link SysUsers#getImportResultStatus()}
     * で確認することができる.
     *
     * @param userList 登録対象
     * @return 処理結果を格納したリスト
     * @throws ServiceAbortException 保存失敗
     */
    List<SysUsers> save(List<SysUsers> userList) throws ServiceAbortException;

    /**
     * 指定されたユーザーを削除する.
     *
     * 各要素の処理結果は戻り値の {@link SysUsers#getImportResultStatus()}
     * で確認することができる.
     *
     * @param userList 削除対象
     * @return 処理結果を格納したリスト
     * @throws ServiceAbortException 削除失敗
     */
    List<SysUsers> delete(List<SysUsers> userList) throws ServiceAbortException;
}
