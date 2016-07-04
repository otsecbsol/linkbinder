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

import jp.co.opentone.bsol.framework.core.dao.KeyDuplicateException;
import jp.co.opentone.bsol.framework.core.dao.StaleRecordException;
import jp.co.opentone.bsol.linkbinder.dao.AbstractDao;
import jp.co.opentone.bsol.linkbinder.dao.PersonInChargeDao;
import jp.co.opentone.bsol.linkbinder.dto.PersonInCharge;

/**
 * person_in_charge を操作するDao.
 *
 * @author opentone
 *
 */
@Repository
public class PersonInChargeDaoImpl extends AbstractDao<PersonInCharge>
implements PersonInChargeDao {
    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -8737897486918546334L;
    /**
     * このクラスが利用するsqlMap.xmlのnamespace.
     */
    private static final String NAMESPACE = "personInCharge";

    /**
     * SQLID: 宛先-ユーザーIDを指定して担当者を取得.
     */
    private static final String SQL_FIND_BY_ADDRESS_USER_ID = "findByAddressUserId";

    /**
     * SQLID: 宛先-ユーザーIDを指定して削除.
     */
    private static final String SQL_DELETE_BY_CORRESPON_ID = "deleteByAddressUserId";

    /**
     * 空のインスタンスを生成する.
     */
    public PersonInChargeDaoImpl() {
        super(NAMESPACE);
    }

    /*
     * (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.dao.PersonInChargeDao#findByAddressUserId(java
     * .lang.Long)
     */
    @SuppressWarnings("unchecked")
    public List<PersonInCharge> findByAddressUserId(Long addressUserId) {
        return getSqlMapClientTemplate()
            .queryForList(getSqlId(
                SQL_FIND_BY_ADDRESS_USER_ID), addressUserId);
    }

    /*
     * (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.dao.PersonInChargeDao#deleteByAddressUserId(
     * jp.co.opentone.bsol.linkbinder.dto.PersonInCharge)
     */
    public Integer deleteByAddressUserId(PersonInCharge personInCharge)
            throws KeyDuplicateException,
                   StaleRecordException {
        return getSqlMapClientTemplate().update(
                                             getSqlId(SQL_DELETE_BY_CORRESPON_ID), personInCharge);
    }

}
