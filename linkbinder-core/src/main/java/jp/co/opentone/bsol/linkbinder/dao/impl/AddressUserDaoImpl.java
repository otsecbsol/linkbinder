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
import jp.co.opentone.bsol.linkbinder.dao.AddressUserDao;
import jp.co.opentone.bsol.linkbinder.dto.AddressUser;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchAddressUserCondition;

/**
 * address_user を操作するDao.
 * @author opentone
 *
 */
@Repository
public class AddressUserDaoImpl extends AbstractDao<AddressUser> implements AddressUserDao {
    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -8737897486918546334L;
    /**
     * このクラスが利用するsqlMap.xmlのnamespace.
     */
    private static final String NAMESPACE = "addressUser";

    /**
     * SQLID: 宛先-活動単位IDを指定して宛先-ユーザーを取得.
     */
    private static final String SQL_FIND_BY_ADDRESS_CORRESPON_GROUP_ID =
            "findByAddressCorresponGroupId";

    /**
     * SQLID: 宛先-活動単位IDを指定して削除.
     */
    private static final String SQL_DELETE_BY_ADDRESS_CORRESPON_GROUP_ID =
            "deleteByAddressCorresponGroupId";

    /**
     * SQLID: コレポン文書情報を指定してメール受信を許可している担当者を取得.
     */
    private static final String SQL_FIND_SEND_APPLY_USER = "findSendApplyUser";

    /**
     * SQLID: コレポン文書情報を指定してメール受信を許可している担当者を取得(Person In Charge決定時).
     */
    private static final String SQL_FIND_SEND_APPLY_USER_FOR_PERSON_IN_CHARGE
        = "findSendApplyUserForPersonInCharge";

    /**
     * 空のインスタンスを生成する.
     */
    public AddressUserDaoImpl() {
        super(NAMESPACE);
    }

    /*
     * (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.dao.AddressUserDao#findByAddressCorresponGroupId(java
     * .lang.Long)
     */
    @SuppressWarnings("unchecked")
    public List<AddressUser> findByAddressCorresponGroupId(Long addressCorresponGroupId) {
        return getSqlMapClientTemplate()
            .queryForList(getSqlId(
                SQL_FIND_BY_ADDRESS_CORRESPON_GROUP_ID), addressCorresponGroupId);
    }

    /*
     * (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.dao.AddressUserDao#deleteByAddressCorresponGroupId(java
     * .lang.Long)
     */
    public Integer deleteByAddressCorresponGroupId(AddressUser addressUser)
        throws KeyDuplicateException, StaleRecordException {
        return getSqlMapClientTemplate().update(getSqlId(SQL_DELETE_BY_ADDRESS_CORRESPON_GROUP_ID),
            addressUser);
    }

    /*
     * (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.dao.AddressUserDao#findSendApplyUser(
     * jp.co.opentone.bsol.linkbinder.dto.notice.SearchAddressUserCondition)
     */
    @SuppressWarnings("unchecked")
    public List<AddressUser> findSendApplyUser(SearchAddressUserCondition condition) {
        return getSqlMapClientTemplate()
            .queryForList(getSqlId(
                    SQL_FIND_SEND_APPLY_USER), condition);
    }

    /*
     * (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.dao.AddressUserDao#findSendApplyUserForPersonInCharge(
     * jp.co.opentone.bsol.linkbinder.dto.notice.SearchAddressUserCondition)
     */
    @SuppressWarnings("unchecked")
    public List<AddressUser> findSendApplyUserForPersonInCharge(
            SearchAddressUserCondition condition) {
        return getSqlMapClientTemplate()
                .queryForList(getSqlId(SQL_FIND_SEND_APPLY_USER_FOR_PERSON_IN_CHARGE), condition);
    }
}
