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

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import jp.co.opentone.bsol.framework.core.dao.KeyDuplicateException;
import jp.co.opentone.bsol.framework.core.dao.RecordNotFoundException;
import jp.co.opentone.bsol.linkbinder.dao.AbstractDao;
import jp.co.opentone.bsol.linkbinder.dao.CorresponReadStatusDao;
import jp.co.opentone.bsol.linkbinder.dto.CorresponReadStatus;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponReadStatusCondition;

/**
 * correspon_read_status を操作するDao.
 *
 * @author opentone
 */
@Repository
public class CorresponReadStatusDaoImpl extends AbstractDao<CorresponReadStatus> implements
        CorresponReadStatusDao {
    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -8737897486918546334L;
    /**
     * このクラスが利用するsqlMap.xmlのnamespace.
     */
    private static final String NAMESPACE = "corresponReadStatus";
    /**
     * SQLID: を取得.
     */
    private static final String SQL_UPDATE_BY_CORRESPON_ID = "updateByCorresponId";
    /**
     * SQLID: を取得.
     */
    private static final String SQL_FIND_BY_EMP_NO = "findByEmpNo";
    /**
     * SQL EXCEPTION: を取得.
     */
    private static final String SQL_DATA_INTEGRIY_EXCEPTION_MESSAGE
                                  = "primary key or foreign key duplicate.";

    /**
     * 空のインスタンスを生成する.
     */
    public CorresponReadStatusDaoImpl() {
        super(NAMESPACE);
    }

    /**
     * コレポン文書の既読・未読ステータスをコレポンIDで更新する.
     * @param dto
     *            既読・未読状態
     * @return 更新件数
     * @throws KeyDuplicateException 更新エラー
     */
    public Integer updateByCorresponId(CorresponReadStatus dto) throws KeyDuplicateException {
        try {
            return (Integer) getSqlMapClientTemplate().update(getSqlId(SQL_UPDATE_BY_CORRESPON_ID),
                                                                                     dto);
        } catch (DataIntegrityViolationException e) {
            KeyDuplicateException ex =
                    new KeyDuplicateException(SQL_DATA_INTEGRIY_EXCEPTION_MESSAGE, dto);
            ex.initCause(e);
            throw ex;
        }
    }

    /**
     * コレポン文書の既読・未読ステータスを検索する.
     * @param id コレポン文書ID
     * @param empNo 従業員番号
     * @return 既読・未読ステータス情報
     * @throws RecordNotFoundException 検索件数0件
     */
    public CorresponReadStatus findByEmpNo(Long id, String empNo) throws RecordNotFoundException {
        SearchCorresponReadStatusCondition condition = new SearchCorresponReadStatusCondition();
        condition.setCorresponId(id);
        condition.setEmpNo(empNo);
        CorresponReadStatus record =
                (CorresponReadStatus) getSqlMapClientTemplate()
                                   .queryForObject(
                                                   getSqlId(SQL_FIND_BY_EMP_NO),
                                                   condition);
        if (record == null) {
            throw new RecordNotFoundException(empNo);
        }
        return record;
    }
}
