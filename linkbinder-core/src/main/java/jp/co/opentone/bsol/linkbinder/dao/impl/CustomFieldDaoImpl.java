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

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import jp.co.opentone.bsol.framework.core.dao.RecordNotFoundException;
import jp.co.opentone.bsol.linkbinder.dao.AbstractDao;
import jp.co.opentone.bsol.linkbinder.dao.CustomFieldDao;
import jp.co.opentone.bsol.linkbinder.dto.CustomField;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCustomFieldCondition;

/**
 * custom_field を操作するDao.
 *
 * @author opentone
 *
 */
@Repository
public class CustomFieldDaoImpl extends AbstractDao<CustomField> implements CustomFieldDao {
    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -8737897486918546334L;
    /**
     * このクラスが利用するsqlMap.xmlのnamespace.
     */
    private static final String NAMESPACE = "customField";

    /**
     * SQLID: プロジェクトIDを指定してカスタムフィールドを取得.
     */
    private static final String SQL_FIND_BY_PROJECT_ID = "findByProjectId";

    /**
     * SQLID: IDとプロジェクトIDを指定してカスタムフィールドを取得.
     */
    private static final String SQL_FIND_BY_ID_PROJECT_ID = "findByIdProjectId";

    /**
     * SQLID: カスタムフィールド件数を取得.
     */
    private static final String SQL_COUNT = "count";

    /**
     * SQLID: カスタムフィールドを取得.
     */
    private static final String SQL_FIND_CUSTOM_FIELD = "findCustomField";

    /**
     * SQLID: プロジェクトカスタムフィールドを取得.
     */
    private static final String SQL_FIND_PROJECT_CUSTOM_FIELD = "findProjectCustomField";

    /**
     * SQLID: 現在のプロジェクトに属していないカスタムフィールドを取得.
     */
    private static final String SQL_FIND_NOT_ASSIGN_TO = "findNotAssignTo";

    /**
     * SQLID: 条件を指定してカスタムフィールド件数を取得(エラーチェック用).
     */
    private static final String SQL_COUNT_CHECK = "countCheck";

    /**
     * 前方一致検索を行うフィールド名.
     */
    private static final List<String> FIELDS;
    static {
        FIELDS = new ArrayList<String>();
        FIELDS.add("label");
    }

    /**
     * 空のインスタンスを生成する.
     */
    public CustomFieldDaoImpl() {
        super(NAMESPACE);
    }

    /*
     * (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.dao.CustomFieldDao#findByProjectId(jp.co.opentone.bsol.linkbinder.
     * dto.condition.SearchCustomFieldCondition)
     */
    @SuppressWarnings("unchecked")
    public List<CustomField> findByProjectId(SearchCustomFieldCondition condition) {
        return (List<CustomField>) getSqlMapClientTemplate()
            .queryForList(getSqlId(SQL_FIND_BY_PROJECT_ID), condition);
    }

    /*
     * (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.dao.CustomFieldDao#findByIdProjectId(jp.co.opentone.bsol.linkbinder
     * .dto.condition.SearchCustomFieldCondition)
     */
    public CustomField findByIdProjectId(SearchCustomFieldCondition condition)
        throws RecordNotFoundException {
        CustomField record =
                (CustomField) getSqlMapClientTemplate()
                    .queryForObject(getSqlId(SQL_FIND_BY_ID_PROJECT_ID), condition);
        if (record == null) {
            throw new RecordNotFoundException(condition.getId().toString());
        }
        return record;
    }

    /*
     * (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.dao.CustomFieldDao#count(jp.co.opentone.bsol.linkbinder.dto.condition
     * .SearchCustomFieldCondition)
     */
    public int count(SearchCustomFieldCondition condition) {
        // 前方一致検索を行う
        SearchCustomFieldCondition likeCondition = getLikeSearchCondition(condition, FIELDS);
        // マスタ管理
        return Integer.parseInt(getSqlMapClientTemplate().queryForObject(getSqlId(SQL_COUNT),
            likeCondition).toString());
    }

    /*
     * (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.dao.CustomFieldDao#find(jp.co.opentone.bsol.linkbinder.dto.condition
     * .SearchCustomFieldCondition)
     */
    @SuppressWarnings("unchecked")
    public List<CustomField> find(SearchCustomFieldCondition condition) {
        List<CustomField> record = null;
        String projectId = condition.getProjectId();
        int skipResults = condition.getSkipNum();
        int maxResults = condition.getPageRowNum();

        // 前方一致検索を行う
        SearchCustomFieldCondition likeCondition = getLikeSearchCondition(condition, FIELDS);
        // マスタ管理
        if (StringUtils.isEmpty(projectId)) {
            record =
                    getSqlMapClientTemplate().queryForList(getSqlId(SQL_FIND_CUSTOM_FIELD),
                        likeCondition,
                        skipResults,
                        maxResults);
        } else {
            // プロジェクトマスタ管理
            record =
                    getSqlMapClientTemplate().queryForList(getSqlId(SQL_FIND_PROJECT_CUSTOM_FIELD),
                        likeCondition,
                        skipResults,
                        maxResults);
        }
        return record;
    }

    /*
     * (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.dao.CustomFieldDao#findNotAssignTo(java.lang.String)
     */
    @SuppressWarnings("unchecked")
    public List<CustomField> findNotAssignTo(String projectId) {
        return getSqlMapClientTemplate().queryForList(getSqlId(SQL_FIND_NOT_ASSIGN_TO), projectId);
    }

    /*
     * (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.dao.CustomFieldDao#countCheck(jp.co.opentone.bsol.linkbinder.dto.condition
     * .SearchCustomFieldCondition)
     */
    public int countCheck(SearchCustomFieldCondition condition) {
        return Integer.parseInt(getSqlMapClientTemplate().queryForObject(getSqlId(SQL_COUNT_CHECK),
            condition).toString());
    }

}
