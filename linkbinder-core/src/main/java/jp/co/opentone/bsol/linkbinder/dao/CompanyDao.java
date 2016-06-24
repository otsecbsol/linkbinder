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

import jp.co.opentone.bsol.framework.core.dao.GenericDao;
import jp.co.opentone.bsol.framework.core.dao.RecordNotFoundException;
import jp.co.opentone.bsol.linkbinder.dto.Company;
import jp.co.opentone.bsol.linkbinder.dto.CompanyUser;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCompanyCondition;

/**
 * company を操作するDao.
 *
 * @author opentone
 *
 */
public interface CompanyDao extends GenericDao<Company> {

    /**
     * 検索条件に該当する会社を検索する.
     * @param condition
     *            検索条件
     * @return 会社情報一覧
     */
    List<Company> find(SearchCompanyCondition condition);

    /**
     * 会社の件数を取得する.
     * @param condition
     *            検索条件
     * @return int 検索件数
     */
    int countCompany(SearchCompanyCondition condition);

    /**
     * プロジェクトに属さない会社情報を取得する.
     * @param projectId
     *            プロジェクトID
     * @return 会社情報一覧
     */
    List<Company> findNotAssignTo(String projectId);

    /**
     * v_project_companyから指定したIDで会社情報を取得する.
     * @param id
     *            ID
     * @param projectId
     *            プロジェクトID
     * @return 会社情報
     * @throws RecordNotFoundException
     *             取得できなかった場合
     */
    Company findProjectCompanyById(Long id, String projectId) throws RecordNotFoundException;

    /**
     * company_userから指定したPROJECTCOMPANYIDで会社情報を取得する.
     * @param projectCompanyId
     *            プロジェクト会社ID
     * @return 会社に所属しているユーザーリスト
     */
    List<CompanyUser> findMembers(Long projectCompanyId);

    /**
     * 指定した条件で会社情報件数を取得する（エラーチェック用）.
     * @param condition 検索条件
     * @return 会社情報件数
     */
    int countCheck(SearchCompanyCondition condition);

}
