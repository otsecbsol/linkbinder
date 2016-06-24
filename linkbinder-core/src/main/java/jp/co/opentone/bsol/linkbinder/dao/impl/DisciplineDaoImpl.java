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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import jp.co.opentone.bsol.linkbinder.dao.AbstractDao;
import jp.co.opentone.bsol.linkbinder.dao.DisciplineDao;
import jp.co.opentone.bsol.linkbinder.dto.Discipline;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchDisciplineCondition;

/**
 * discipline を操作するDao.
 *
 * @author opentone
 *
 */
@Repository
public class DisciplineDaoImpl extends AbstractDao<Discipline> implements DisciplineDao {
    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -8737897486918546334L;
    /**
     * このクラスが利用するsqlMap.xmlのnamespace.
     */
    private static final String NAMESPACE = "discipline";

    /**
     * SQLID: 部門情報をカウントするID.
     */
    private static final String SQL_ID_COUNT = "count";

    /**
     * SQLID: 部門情報を検索するID.
     */
    private static final String SQL_ID_FIND = "find";

    /**
     * SQLID: 活動単位に登録されていない部門を検索するID.
     */
    private static final String SQL_ID_FIND_NOT_EXIST_CORRESPON_GROUP =
            "findNotExistCorresponGroup";

    /**
     * SQLID: 条件を指定して部門情報件数を取得する(エラーチェック用).
     */
    private static final String SQL_ID_COUNT_CHECK = "countCheck";

    /**
     * 前方一致検索を行うフィールド名.
     */
    private static final List<String> FIELDS;
    static {
        FIELDS = new ArrayList<String>();
        FIELDS.add("disciplineCd");
        FIELDS.add("name");
    }

    /**
     * 空のインスタンスを生成する.
     */
    public DisciplineDaoImpl() {
        super(NAMESPACE);
    }

    /*
     * (non-Javadoc)
     * @seejp.co.opentone.bsol.linkbinder.dao.DisciplineDao#count(jp.co.opentone.bsol.linkbinder.dto.
     * SearchDisciplineCondition)
     */
    public int count(SearchDisciplineCondition condition) {
        int count = 0;
        // 前方一致検索を行う
        SearchDisciplineCondition likeCondition = getLikeSearchCondition(condition, FIELDS);
        count =
                Integer.parseInt(getSqlMapClientTemplate().queryForObject(getSqlId(SQL_ID_COUNT),
                    likeCondition).toString());
        return count;
    }

    /*
     * (non-Javadoc)
     * @seejp.co.opentone.bsol.linkbinder.dao.DisciplineDao#find(jp.co.opentone.bsol.linkbinder.dto.
     * SearchDisciplineCondition)
     */
    @SuppressWarnings("unchecked")
    public List<Discipline> find(SearchDisciplineCondition condition) {
        List<Discipline> record = null;
        int skipResults = condition.getSkipNum();
        int maxResults = condition.getPageRowNum();

        // 前方一致検索を行う;
        SearchDisciplineCondition likeCondition = getLikeSearchCondition(condition, FIELDS);
        record =
                getSqlMapClientTemplate().queryForList(getSqlId(SQL_ID_FIND),
                    likeCondition,
                    skipResults,
                    maxResults);
        return record;
    }

    /*
     * (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.dao.DisciplineDao#findNotExistCorresponGroup(java.lang
     * .String)
     */
    @SuppressWarnings("unchecked")
    public List<Discipline> findNotExistCorresponGroup(String projectId, Long siteId) {
        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("projectId", projectId);
        condition.put("siteId", siteId);
        return (List<Discipline>) getSqlMapClientTemplate()
            .queryForList(getSqlId(SQL_ID_FIND_NOT_EXIST_CORRESPON_GROUP), condition);
    }

    /*
     * (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.dao.DisciplineDao#countCheck(jp.co.opentone.bsol.linkbinder.dto.condition
     * .SearchDisciplineCondition)
     */
    public int countCheck(SearchDisciplineCondition condition) {
        return Integer.parseInt(getSqlMapClientTemplate()
            .queryForObject(getSqlId(SQL_ID_COUNT_CHECK), condition).toString());
    }

}
