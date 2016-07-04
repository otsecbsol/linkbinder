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
import jp.co.opentone.bsol.linkbinder.dao.EmailNoticeDao;
import jp.co.opentone.bsol.linkbinder.dto.code.EmailNoticeStatus;
import jp.co.opentone.bsol.linkbinder.dto.notice.EmailNotice;

/**
 * EmailNotice を操作するDao.
 *
 * @author opentone
 *
 */
@Repository
public class EmailNoticeDaoImpl extends AbstractDao<EmailNotice> implements EmailNoticeDao {

    /**
     *
     */
    private static final long serialVersionUID = -2261817244953101214L;

    /**
     * このクラスが利用するsqlMap.xmlのnamespace.
     */
    private static final String NAMESPACE = "emailNotice";

    /**
     * SQLID: 指定文書のメール通知情報検索.
     */
    private static final String SQL_FIND_BY_CORRESPON_ID = "findByCorresponId";

    /**
     * 空のインスタンスを生成する.
     *
     * @param namespace
     */
    public EmailNoticeDaoImpl() {
        super(NAMESPACE);
    }

    /* (非 Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.dao.EmailNoticeDao#findByCorresponId(java.lang.Long, jp.co.opentone.bsol.linkbinder.dto.code.EmailNoticeStatus[])
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<EmailNotice> findByCorresponId(Long corresponId, EmailNoticeStatus... statuses) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("corresponId", corresponId);
        map.put("statuses", statuses);

        return (List<EmailNotice>) getSqlMapClientTemplate()
                    .queryForList(getSqlId(SQL_FIND_BY_CORRESPON_ID), map);
    }
}
