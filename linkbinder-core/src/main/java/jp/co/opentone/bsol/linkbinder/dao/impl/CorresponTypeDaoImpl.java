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
import jp.co.opentone.bsol.linkbinder.dao.CorresponTypeDao;
import jp.co.opentone.bsol.linkbinder.dto.CorresponType;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponTypeCondition;

/**
 * correspon_type を操作するDao.
 * @author opentone
 */
@Repository
public class CorresponTypeDaoImpl extends AbstractDao<CorresponType> implements CorresponTypeDao {
    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -8737897486918546334L;
    /**
     * このクラスが利用するsqlMap.xmlのnamespace.
     */
    private static final String NAMESPACE = "corresponType";

    /**
     * SQLID: コレポン文書種別を取得.
     */
    private static final String SQL_FIND = "find";

    /**
     * SQLID: コレポン文書種別件数取得.
     */
    private static final String SQL_COUNT = "count";

    /**
     * SQLID: プロジェクトに登録されていないレコードを取得.
     */
    private static final String SQL_FIND_NOT_EXIST = "findNotExist";

    /**
     * SQLID: プロジェクトに登録されていないレコードを取得.
     */
    private static final String SQL_FIND_BY_PROJECT_CORRESPON_TYPE_ID =
            "findByProjectCorresponTypeId";

    /**
     * SQLID: 条件を指定して、コレポン文書種別の件数を取得（エラーチェック用）.
     */
    private static final String SQL_COUNT_CHECK = "countCheck";

    /**
     * SQLID: IDとプロジェクトIDを指定してコレポン文書種別を取得.
     */
    private static final String SQL_FIND_BY_ID_PROJECT_ID = "findByIdProjectId";

    /**
     * 前方一致検索を行うフィールド名.
     */
    private static final List<String> FIELDS;
    static {
        FIELDS = new ArrayList<String>();
        FIELDS.add("corresponType");
        FIELDS.add("name");
    }

    /**
     * 空のインスタンスを生成する.
     */
    public CorresponTypeDaoImpl() {
        super(NAMESPACE);

    }

    /*
     * (non-Javadoc)
     * @seejp.co.opentone.bsol.linkbinder.dao.CorresponTypeDao#find(jp.co.opentone.bsol.linkbinder.dto.
     * SearchCorresponTypeCondition)
     */
    @SuppressWarnings("unchecked")
    public List<CorresponType> find(SearchCorresponTypeCondition condition) {
        int skipResults = condition.getSkipNum();
        int maxResults = condition.getPageRowNum();

        List<CorresponType> result = null;
        // 前方一致検索を行う
        SearchCorresponTypeCondition likeCondition = getLikeSearchCondition(condition, FIELDS);
        result =
                (List<CorresponType>) getSqlMapClientTemplate().queryForList(getSqlId(SQL_FIND),
                    likeCondition,
                    skipResults,
                    maxResults);
        return result;
    }

    /*
     * (non-Javadoc)
     * @seejp.co.opentone.bsol.linkbinder.dao.CorresponTypeDao#count(jp.co.opentone.bsol.linkbinder.dto.
     * SearchCorresponTypeCondition)
     */
    public int count(SearchCorresponTypeCondition condition) {
        // 前方一致検索を行う
        SearchCorresponTypeCondition likeCondition = getLikeSearchCondition(condition, FIELDS);
        return Integer.parseInt(getSqlMapClientTemplate().queryForObject(getSqlId(SQL_COUNT),
            likeCondition).toString());
    }

    /*
     * (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.dao.CorresponTypeDao#findNotExist(java.lang.String)
     */
    @SuppressWarnings("unchecked")
    public List<CorresponType> findNotExist(String projectId) {
        return (List<CorresponType>) getSqlMapClientTemplate()
            .queryForList(getSqlId(SQL_FIND_NOT_EXIST), projectId);
    }

    /*
     * (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.dao.CorresponTypeDao#findByProjectCorresponTypeId(java
     * .lang.Long)
     */
    public CorresponType findByProjectCorresponTypeId(Long projectCorresponTypeId)
        throws RecordNotFoundException {
        CorresponType corresponType =
                (CorresponType) getSqlMapClientTemplate()
                    .queryForObject(getSqlId(SQL_FIND_BY_PROJECT_CORRESPON_TYPE_ID),
                        projectCorresponTypeId);

        if (corresponType == null) {
            throw new RecordNotFoundException();
        }
        return corresponType;
    }

    /*
     * (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.dao.CorresponTypeDao#countCheck(jp.co.opentone.bsol.linkbinder.dto
     * .condition.SearchCorresponTypeCondition)
     */
    public int countCheck(SearchCorresponTypeCondition condition) {
        return Integer.parseInt(getSqlMapClientTemplate().queryForObject(getSqlId(SQL_COUNT_CHECK),
            condition).toString());
    }

    /*
     * (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.dao.CorresponTypeDao#findByIdProjectId(java.lang.Long,
     * java.lang.String)
     */
    public CorresponType findByIdProjectId(Long id, String projectId)
        throws RecordNotFoundException {
        SearchCorresponTypeCondition condition = new SearchCorresponTypeCondition();
        condition.setId(id);
        condition.setProjectId(projectId);

        CorresponType corresponType =
                (CorresponType) getSqlMapClientTemplate()
                    .queryForObject(getSqlId(SQL_FIND_BY_ID_PROJECT_ID), condition);

        if (corresponType == null) {
            throw new RecordNotFoundException();
        }

        return corresponType;
    }

}
