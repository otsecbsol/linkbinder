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
import jp.co.opentone.bsol.linkbinder.dao.AttachmentDao;
import jp.co.opentone.bsol.linkbinder.dto.Attachment;

/**
 * attachment を操作するDao.
 * @author opentone
 *
 */
@Repository
public class AttachmentDaoImpl extends AbstractDao<Attachment> implements AttachmentDao {
    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -8737897486918546334L;
    /**
     * このクラスが利用するsqlMap.xmlのnamespace.
     */
    private static final String NAMESPACE = "attachment";

    /**
     * SQLID: コレポン文書を指定して添付ファイルを取得.
     */
    private static final String SQL_FIND_BY_CORRESPON_ID = "findByCorresponId";

    /**
     * 空のインスタンスを生成する.
     */
    public AttachmentDaoImpl() {
        super(NAMESPACE);
    }

    /*
     * (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.dao.AttachmentDao#findByCorresponId(java.lang.Long)
     */
    @SuppressWarnings("unchecked")
    public List<Attachment> findByCorresponId(Long corresponId) {
        return getSqlMapClientTemplate().queryForList(getSqlId(SQL_FIND_BY_CORRESPON_ID),
                                                      corresponId);
    }

}
