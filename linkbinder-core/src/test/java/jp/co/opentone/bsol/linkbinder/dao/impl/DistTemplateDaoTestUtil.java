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
import java.util.Map;

import org.springframework.orm.ibatis.SqlMapClientTemplate;
import org.springframework.stereotype.Repository;

/**
 * テスト実行のためのユーティリティDaoクラス.
 * @author opentone
 */
@Repository
public class DistTemplateDaoTestUtil extends DistTemplateDaoBaseImpl {

    /**
     * serialVersionUID.
     */
    private static final long serialVersionUID = 1516778800358756187L;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public List<Map> select(String queryId, Map queryParam) {
        SqlMapClientTemplate sqlMapClient = getSqlMapClientTemplate();
        List<Map> result = (List<Map>) sqlMapClient.queryForList(queryId, queryParam);
        return result;
    }

    public Object select(String queryId, Object queryParam) {
        SqlMapClientTemplate sqlMapClient = getSqlMapClientTemplate();
        Object result = sqlMapClient.queryForList(queryId, queryParam);
        return result;
    }

    /**
     * 削除を実行する.
     * @param queryId クエリーID
     * @return 削除行数
     */
    public int delete(String queryId) {
        SqlMapClientTemplate sqlMapClient = getSqlMapClientTemplate();
        int result = sqlMapClient.delete(queryId);
        return result;
    }

    /**
     * 登録を実行する.
     * @param queryId クエリーID
     * @param obj クエリー引数
     * @return 戻り値Object
     */
    public Object insert(String queryId, Object obj) {
        SqlMapClientTemplate sqlMapClient = getSqlMapClientTemplate();
        Object result = sqlMapClient.insert(queryId, obj);
        return result;
    }
}
