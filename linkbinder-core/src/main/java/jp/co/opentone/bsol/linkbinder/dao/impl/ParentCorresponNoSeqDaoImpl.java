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

import org.springframework.stereotype.Repository;

import jp.co.opentone.bsol.linkbinder.dao.AbstractDao;
import jp.co.opentone.bsol.linkbinder.dao.ParentCorresponNoSeqDao;
import jp.co.opentone.bsol.linkbinder.dto.ParentCorresponNoSeq;
import jp.co.opentone.bsol.linkbinder.dto.condition.ParentCorresponNoSeqCondition;

/**
 * 親コレポン文書採番を操作するDao.
 *
 * @author opentone
 *
 */
@Repository
public class ParentCorresponNoSeqDaoImpl extends AbstractDao<ParentCorresponNoSeq> implements
    ParentCorresponNoSeqDao {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -7530643060721356018L;

    /**
     * このクラスが利用するsqlMap.xmlのnamespace.
     */
    private static final String NAMESPACE = "parentCorresponNoSeq";

    /**
     * SQLID: 更新用1件取得.
     */
    private static final String SQL_FIND_FOR_UPDATE = "findForUpdate";

    /**
     * コンストラクタ.
     */
    public ParentCorresponNoSeqDaoImpl() {
        super(NAMESPACE);
    }

    /*
     * (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.dao.ParentCorresponNoSeqDao#findForUpdate(
     *      jp.co.opentone.bsol.linkbinder.dto.ParentCorresponNoSeqCondition)
     */
    public ParentCorresponNoSeq findForUpdate(ParentCorresponNoSeqCondition condition) {
        return (ParentCorresponNoSeq) getSqlMapClientTemplate()
            .queryForObject(getSqlId(SQL_FIND_FOR_UPDATE), condition);
    }
}
