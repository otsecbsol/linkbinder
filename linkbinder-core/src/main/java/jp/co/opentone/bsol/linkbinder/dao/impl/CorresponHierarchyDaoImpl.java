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
import java.util.Map;

import org.springframework.stereotype.Repository;

import jp.co.opentone.bsol.linkbinder.dao.AbstractDao;
import jp.co.opentone.bsol.linkbinder.dao.CorresponHierarchyDao;
import jp.co.opentone.bsol.linkbinder.dto.CorresponHierarchy;

/**
 * correspon_hierarchy を操作するDao.
 * @author opentone
 */
@Repository
public class CorresponHierarchyDaoImpl extends AbstractDao<CorresponHierarchy> implements
        CorresponHierarchyDao {
    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -8737897486918546334L;
    /**
     * このクラスが利用するsqlMap.xmlのnamespace.
     */
    private static final String NAMESPACE = "corresponHierarchy";

    /**
     * SQLID: 検索条件を指定してコレポン文書階層を検索する.
     */
    private static final String COUNT_BY_PARENTCORRESPONID_REPLYADDRESSUSERID
        = "countByParentCorresponIdReplyAddressUserId";

    /**
     * 空のインスタンスを生成する.
     */
    public CorresponHierarchyDaoImpl() {
        super(NAMESPACE);
    }

    /**
     * 親コレポン文書ID、返信元宛先ユーザーIDでコレポン文書階層を検索し
     * ヒット数を取得する.
     * @param parentCorresponId 親コレポン文書ID
     * @param replyAddressUserId 返信元宛先ユーザーID
     * @return ヒット数
     */
    public int countByParentCorresponIdReplyAddressUserId(
        Long parentCorresponId, Long replyAddressUserId) {
        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("parentCorresponId", parentCorresponId);
        condition.put("replyAddressUserId", replyAddressUserId);

        int count = 0;
        count = Integer.parseInt(getSqlMapClientTemplate().queryForObject(
            getSqlId(COUNT_BY_PARENTCORRESPONID_REPLYADDRESSUSERID), condition).toString());
        return count;
    }
}
