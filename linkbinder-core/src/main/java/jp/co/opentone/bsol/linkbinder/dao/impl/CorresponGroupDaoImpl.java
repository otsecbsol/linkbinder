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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import jp.co.opentone.bsol.linkbinder.dao.AbstractDao;
import jp.co.opentone.bsol.linkbinder.dao.CorresponGroupDao;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroup;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroupUser;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponGroupCondition;

/**
 * correspon_group を操作するDao.
 *
 * @author opentone
 *
 */
@Repository
public class CorresponGroupDaoImpl extends AbstractDao<CorresponGroup>
    implements CorresponGroupDao {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -8737897486918546334L;
    /**
     * このクラスが利用するsqlMap.xmlのnamespace.
     */
    private static final String NAMESPACE = "corresponGroup";

    /**
     * SQLID: 検索条件を指定して活動単位を取得.
     */
    private static final String SQL_FIND = "find";

    /**
     * SQLID: 検索条件を指定して活動単位に所属するユーザーを1件検索する.
     */
    private static final String SQL_FIND_BY_EMP_NO = "findByEmpNo";

    /**
     * SQLID: 部門情報IDを指定して活動単位を1件取得する.
     */
    private static final String SQL_FIND_BY_DISCIPLINE_ID = "findByDisciplineId";

    /**
     * SQLID: 活動単位の件数を取得する.
     */
    private static final String SQL_COUNT_CORRESPON_GROUP = "countCorresponGroup";

    /**
     * 空のインスタンスを生成する.
     */
    public CorresponGroupDaoImpl() {
        super(NAMESPACE);
    }

    /*
     * (non-Javadoc)
     * @seejp.co.opentone.bsol.linkbinder.dao.CorresponGroupDao#find(jp.co.opentone.bsol.linkbinder.dto.
     * SearchCorresponGroupCondition)
     */
    @SuppressWarnings("unchecked")
    public List<CorresponGroup> find(SearchCorresponGroupCondition condition) {
        int skipResults = condition.getSkipNum();
        int maxResults = condition.getPageRowNum();
        return (List<CorresponGroup>) getSqlMapClientTemplate().queryForList(getSqlId(SQL_FIND),
            condition,
            skipResults,
            maxResults);
    }

    /*
     * (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.dao.CorresponGroupDao#findByEmpNo(java.lang.Long,
     * java.lang.String)
     */
    public List<CorresponGroupUser> findByEmpNo(String projectId, String empNo) {
        return findByEmpNo(projectId, empNo, null);
    }

    /*
     * (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.dao.CorresponGroupDao#findByEmpNo(java.lang.Long,
     * java.lang.String)
     */
    @SuppressWarnings("unchecked")
    public List<CorresponGroupUser> findByEmpNo(
        String projectId, String empNo, String sortColumn) {
        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("projectId", projectId);
        condition.put("empNo", empNo);
        condition.put("sortColumn", sortColumn);

        return (List<CorresponGroupUser>) getSqlMapClientTemplate()
            .queryForList(getSqlId(SQL_FIND_BY_EMP_NO), condition);
    }

    /*
     * (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.dao.CorresponGroupDao#findByDisciplineId(java.lang.Long)
     */
    @SuppressWarnings("unchecked")
    public List<CorresponGroup> findByDisciplineId(Long disciplineId) {
      return (List<CorresponGroup>) getSqlMapClientTemplate()
          .queryForList(getSqlId(SQL_FIND_BY_DISCIPLINE_ID), disciplineId);
    }

    /*
     * (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.dao.CorresponGroupDao#countCorresponGroup(jp.co.opentone.bsol.
     *      linkbinder.dto.SearchCorresponGroupCondition)
     */
    public int countCorresponGroup(SearchCorresponGroupCondition condition) {
        int count = 0;
        count =
                Integer.parseInt(getSqlMapClientTemplate()
                    .queryForObject(getSqlId(SQL_COUNT_CORRESPON_GROUP), condition).toString());
        return count;

    }

}
