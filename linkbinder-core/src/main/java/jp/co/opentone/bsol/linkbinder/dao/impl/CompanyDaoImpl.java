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

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import jp.co.opentone.bsol.framework.core.dao.RecordNotFoundException;
import jp.co.opentone.bsol.linkbinder.dao.AbstractDao;
import jp.co.opentone.bsol.linkbinder.dao.CompanyDao;
import jp.co.opentone.bsol.linkbinder.dto.Company;
import jp.co.opentone.bsol.linkbinder.dto.CompanyUser;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCompanyCondition;

/**
 * company を操作するDao.
 *
 * @author opentone
 *
 */
@Repository
public class CompanyDaoImpl extends AbstractDao<Company> implements CompanyDao {
    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -8737897486918546334L;
    /**
     * このクラスが利用するsqlMap.xmlのnamespace.
     */
    private static final String NAMESPACE = "company";

    /**
     * SQLID: マスタ管理の会社情報を取得するID.
     */
    private static final String SQL_ID_FIND_COMPANY = "findCompany";

    /**
     * SQLID: プロジェクトマスタ管理の会社情報を取得するID.
     */
    private static final String SQL_ID_FIND_PROJECT_COMPANY = "findProjectCompany";

    /**
     * SQLID: 会社情報をカウントするID.
     */
    private static final String SQL_ID_COUNT_COMPANY = "countCompany";

    /**
     * SQLID: プロジェクトに登録されていないレコードを取得.
     */
    private static final String SQL_ID_FIND_NOT_ASSIGN_TO = "findNotAssignTo";

    /**
     * SQLID: IDを指定して会社情報を取得.
     */
    private static final String SQL_ID_FIND_PROJECT_COMPANY_BY_ID = "findProjectCompanyById";

    /**
     * SQLID: 指定した条件で会社情報件数を取得する（エラーチェック用）.
     */
    private static final String SQL_ID_COUNT_CHECK = "countCheck";

    /**
     * SQLID: 会社ユーザーを取得する.
     */
    private static final String SQL_ID_FIND_MEMBERS = "findMembers";

    /**
     * 前方一致検索を行うフィールド名.
     */
    private static final List<String> FIELDS;
    static {
        FIELDS = new ArrayList<String>();
        FIELDS.add("companyCd");
        FIELDS.add("name");
        FIELDS.add("role");
    }

    /**
     * 空のインスタンスを生成する.
     */
    public CompanyDaoImpl() {
        super(NAMESPACE);
    }

    /*
     * (non-Javadoc)
     * @seejp.co.opentone.bsol.linkbinder.dao.CompanyDao#find(jp.co.opentone.bsol.linkbinder.dto.
     * SearchCompanyCondition)
     */
    @SuppressWarnings("unchecked")
    public List<Company> find(SearchCompanyCondition condition) {
        List<Company> record = null;
        String projectId = condition.getProjectId();
        int skipResults = condition.getSkipNum();
        int maxResults = condition.getPageRowNum();

        // 前方一致検索を行う
        SearchCompanyCondition likeCondition = getLikeSearchCondition(condition, FIELDS);
        // マスタ管理
        if (projectId == null) {
            record =
                    getSqlMapClientTemplate().queryForList(getSqlId(SQL_ID_FIND_COMPANY),
                        likeCondition,
                        skipResults,
                        maxResults);
        } else {
            // プロジェクトマスタ管理
            record =
                    getSqlMapClientTemplate().queryForList(getSqlId(SQL_ID_FIND_PROJECT_COMPANY),
                        likeCondition,
                        skipResults,
                        maxResults);
        }
        return record;
    }

    /*
     * (non-Javadoc)
     * @seejp.co.opentone.bsol.linkbinder.dao.CompanyDao#count(jp.co.opentone.bsol.linkbinder.dto.
     * SearchCompanyCondition)
     */
    public int countCompany(SearchCompanyCondition condition) {
        int count = 0;
        // 前方一致検索を行う
        SearchCompanyCondition likeCondition = getLikeSearchCondition(condition, FIELDS);
        // マスタ管理
        count =
                Integer.parseInt(getSqlMapClientTemplate()
                    .queryForObject(getSqlId(SQL_ID_COUNT_COMPANY), likeCondition).toString());
        return count;
    }

    /*
     * (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.dao.CompanyDao#findNotAssignTo(java.lang.String)
     */
    @SuppressWarnings("unchecked")
    public List<Company> findNotAssignTo(String projectId) {
        return (List<Company>) getSqlMapClientTemplate()
            .queryForList(getSqlId(SQL_ID_FIND_NOT_ASSIGN_TO), projectId);
    }

    /*
     * (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.dao.CompanyDao#findProjectCompanyById(java.lang.Long)
     */
    public Company findProjectCompanyById(Long id, String projectId)
            throws RecordNotFoundException {
        SearchCompanyCondition condition = new SearchCompanyCondition();
        condition.setId(id);
        condition.setProjectId(projectId);
        Company record =
                (Company) getSqlMapClientTemplate()
                    .queryForObject(getSqlId(SQL_ID_FIND_PROJECT_COMPANY_BY_ID), condition);
        if (record == null) {
            throw new RecordNotFoundException(id.toString());
        }
        return record;

    }

    /*
     * (non-Javadoc)
     * @seejp.co.opentone.bsol.linkbinder.dao.CompanyDao#findMembers(jp.co.opentone.bsol.linkbinder.dto.
     * SearchCompanyCondition)
     */
    @SuppressWarnings("unchecked")
    public List<CompanyUser> findMembers(Long projectCompanyId) {
        return (List<CompanyUser>) getSqlMapClientTemplate()
            .queryForList(getSqlId(SQL_ID_FIND_MEMBERS), projectCompanyId);

    }

    /*
     * (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.dao.CompanyDao#countCheck(jp.co.opentone.bsol.linkbinder.dto.condition
     * .SearchCompanyCondition)
     */
    public int countCheck(SearchCompanyCondition condition) {
        return Integer.parseInt(getSqlMapClientTemplate()
            .queryForObject(getSqlId(SQL_ID_COUNT_CHECK), condition).toString());
    }

}
