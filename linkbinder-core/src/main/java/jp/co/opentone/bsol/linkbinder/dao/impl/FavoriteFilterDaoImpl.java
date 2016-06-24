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

import java.util.List;

import org.springframework.stereotype.Repository;

import jp.co.opentone.bsol.linkbinder.dao.AbstractDao;
import jp.co.opentone.bsol.linkbinder.dao.FavoriteFilterDao;
import jp.co.opentone.bsol.linkbinder.dto.FavoriteFilter;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchFavoriteFilterCondition;

/**
 * Favorite filter を操作するDao.
 *
 * @author opentone
 *
 */
@Repository
public class FavoriteFilterDaoImpl extends AbstractDao<FavoriteFilter> implements
        FavoriteFilterDao {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = 7473745769676504858L;

    /**
     * このクラスが利用するsqlMap.xmlのnamespace.
     */
    private static final String NAMESPACE = "favoriteFilter";

    /**
     * SQLID: favorite filter 情報を取得するID.
     */
    private static final String SQL_ID_FIND_FAVORITE_FILTER = "findFavoriteFilter";

    /**
     * 空のインスタンスを生成する.
     * @param namespace
     */
    public FavoriteFilterDaoImpl() {
        super(NAMESPACE);
    }

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.dao.FavoriteFilterDao#find
     * (jp.co.opentone.bsol.linkbinder.dto.condition.SearchFavoriteFilterCondition)
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<FavoriteFilter> find(SearchFavoriteFilterCondition condition) {
        List<FavoriteFilter> record = null;
        int skipResults = condition.getSkipNum();
        int maxResults = condition.getPageRowNum();
        record =
            getSqlMapClientTemplate().queryForList(getSqlId(SQL_ID_FIND_FAVORITE_FILTER),
                condition,
                skipResults,
                maxResults);
        return record;
    }

}
