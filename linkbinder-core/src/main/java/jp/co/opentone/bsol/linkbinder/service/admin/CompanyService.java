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
import jp.co.opentone.bsol.linkbinder.dto.Company;
import jp.co.opentone.bsol.linkbinder.dto.CompanyUser;
import jp.co.opentone.bsol.linkbinder.dto.SearchCompanyResult;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCompanyCondition;

/**
 * このサービスでは会社情報に関する処理を提供する.
 * @author opentone
 */
public interface CompanyService extends IService {

    /**
     * 指定された条件に該当する会社を検索し返す.
     * @param condition
     *            検索条件
     * @return SearchCompanyResult 会社情報
     * @throws ServiceAbortException
     *             会社情報検索失敗
     */
    SearchCompanyResult search(SearchCompanyCondition condition) throws ServiceAbortException;

    /**
     * プロジェクトに所属する会社情報を取得する.
     * @param projectId プロジェクトID
     * @return 会社情報
     */
    List<Company> searchRelatedToProject(String projectId);

    /**
     * 追加する会社情報を取得する.
     * @return 会社情報
     */
    List<Company> searchNotAssigned();

    /**
     * 会社情報一覧のデータをExcelに出力する.
     * @param companies
     *            会社情報
     * @return Excelのデータ
     * @throws ServiceAbortException
     *             Excel出力失敗
     */
    byte[] generateExcel(List<Company> companies) throws ServiceAbortException;

    /**
     * 指定した会社情報を削除する.
     * @param company
     *            会社情報
     * @throws ServiceAbortException
     *             指定した会社情報削除に失敗
     */
    void delete(Company company) throws ServiceAbortException;

    /**
     * 指定した会社をプロジェクトに登録する.
     * @param company
     *            会社情報
     * @return Long 登録した会社情報のID
     * @throws ServiceAbortException
     *             会社をプロジェクトに登録失敗
     */
    Long assignTo(Company company) throws ServiceAbortException;

    /**
     * 指定した会社を取得する.
     * @param id
     *            会社情報ID
     * @return 会社情報
     * @throws ServiceAbortException
     *             会社取得失敗
     */
    Company find(Long id) throws ServiceAbortException;

    /**
     * 入力値を検証する.
     * @param company
     *            会社情報
     * @return 検証結果に問題がない場合true / 問題がある場合false
     * @throws ServiceAbortException
     *             入力値検証に引っかかった場合
     */
    boolean validate(Company company) throws ServiceAbortException;

    /**
     * 指定された会社情報を保存する.
     * @param company
     *            会社情報
     * @return 登録した会社情報のID
     * @throws ServiceAbortException
     *             保存失敗
     */
    Long save(Company company) throws ServiceAbortException;

    /**
     * 指定された会社に所属するユーザーを取得する.
     * @param id
     *            会社ID
     * @return 会社に所属するユーザー
     * @throws ServiceAbortException
     *             会社に所属するユーザー取得失敗
     */
    List<CompanyUser> findMembers(Long id) throws ServiceAbortException;

    /**
     * 指定した会社に所属するユーザーを保存する.
     * @param company
     *            会社情報
     * @param users
     *            保存対象ユーザー
     * @throws ServiceAbortException
     *             ユーザー保存失敗
     */
    void saveMembers(Company company, List<User> users) throws ServiceAbortException;

}
