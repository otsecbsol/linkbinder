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

import jp.co.opentone.bsol.linkbinder.dao.AbstractDao;
import jp.co.opentone.bsol.linkbinder.dao.SiteDao;
import jp.co.opentone.bsol.linkbinder.dto.Site;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchSiteCondition;

/**
 * site を操作するDao.
 *
 * @author opentone
 *
 */
@Repository
public class SiteDaoImpl extends AbstractDao<Site> implements SiteDao {
    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -8737897486918546334L;
    /**
     * このクラスが利用するsqlMap.xmlのnamespace.
     */
    private static final String NAMESPACE = "site";

    /**
     * SQLID: 検索条件を指定して拠点情報を取得するID.
     */
    private static final String SQL_FIND = "find";

    /**
     * SQLID: 検索条件を指定して拠点情報件数を取得するID.
     */
    private static final String SQL_COUNT = "count";

    /**
     * SQLID: 検索条件を指定して拠点情報件数を取得するID（チェック用）.
     */
    private static final String SQL_COUNT_CHECK = "countCheck";

    /**
     * 前方一致検索を行うフィールド名.
     */
    private static final List<String> FIELDS;
    static {
        FIELDS = new ArrayList<String>();
        FIELDS.add("siteCd");
        FIELDS.add("name");
    }

    /**
     * 空のインスタンスを生成する.
     */
    public SiteDaoImpl() {
        super(NAMESPACE);
    }

    /*
     * (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.dao.SiteDao#find(jp.co.opentone.bsol.linkbinder.dto.SearchSiteCondition
     * )
     */
    @SuppressWarnings("unchecked")
    public List<Site> find(SearchSiteCondition condition) {
        // 前方一致検索を行う
        SearchSiteCondition likeCondition = getLikeSearchCondition(condition, FIELDS);
        int skipResults = condition.getSkipNum();
        int maxResults = condition.getPageRowNum();
        return (List<Site>) getSqlMapClientTemplate().queryForList(getSqlId(SQL_FIND),
            likeCondition,
            skipResults,
            maxResults);
    }

    /*
     * (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.dao.SiteDao#count(jp.co.opentone.bsol.linkbinder.dto.SearchSiteCondition
     * )
     */
    public int count(SearchSiteCondition condition) {
        int count = 0;
        // 前方一致検索を行う
        SearchSiteCondition likeCondition = getLikeSearchCondition(condition, FIELDS);
        count =
                Integer.parseInt(getSqlMapClientTemplate().queryForObject(getSqlId(SQL_COUNT),
                    likeCondition).toString());
        return count;
    }

    /*
     * (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.dao.SiteDao#countCheck(jp.co.opentone.bsol.linkbinder.dto.condition
     * .SearchSiteCondition)
     */
    public int countCheck(SearchSiteCondition condition) {
        return Integer.parseInt(getSqlMapClientTemplate().queryForObject(getSqlId(SQL_COUNT_CHECK),
            condition).toString());
    }

}
