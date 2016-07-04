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

import jp.co.opentone.bsol.framework.core.dao.KeyDuplicateException;
import jp.co.opentone.bsol.framework.core.dao.StaleRecordException;
import jp.co.opentone.bsol.linkbinder.dao.AbstractDao;
import jp.co.opentone.bsol.linkbinder.dao.AddressCorresponGroupDao;
import jp.co.opentone.bsol.linkbinder.dto.AddressCorresponGroup;
import jp.co.opentone.bsol.linkbinder.dto.AddressUser;
import jp.co.opentone.bsol.linkbinder.dto.PersonInCharge;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowStatus;

/**
 * address_correspon_group を操作するDao.
 * @author opentone
 *
 */
@Repository
public class AddressCorresponGroupDaoImpl extends AbstractDao<AddressCorresponGroup> implements
        AddressCorresponGroupDao {
    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -8737897486918546334L;
    /**
     * このクラスが利用するsqlMap.xmlのnamespace.
     */
    private static final String NAMESPACE = "addressCorresponGroup";

    /**
     * SQLID: コレポン文書を指定して宛先-ユーザー、担当者を取得.
     */
    private static final String SQL_FIND_BY_CORRESPON_ID = "findByCorresponId";

    /**
     * SQLID: コレポン文書を指定して削除.
     */
    private static final String SQL_DELETE_BY_CORRESPON_ID = "deleteByCorresponId";

    /**
     * 空のインスタンスを生成する.
     */
    public AddressCorresponGroupDaoImpl() {
        super(NAMESPACE);
    }

    /*
     * (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.dao.AddressCorresponGroupDao#findByCorresponId(java.
     * lang.Long)
     */
    @SuppressWarnings("unchecked")
    public List<AddressCorresponGroup> findByCorresponId(Long corresponId) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("corresponId", corresponId);
        param.put("workflowStatus", WorkflowStatus.ISSUED);

        List<AddressCorresponGroup> result =
            getSqlMapClientTemplate().queryForList(getSqlId(SQL_FIND_BY_CORRESPON_ID),
                                                      param);

        // 外部結合で宛先-ユーザー、担当者を取得しており
        // リスト中に空のオブジェクトが含まれる可能性があるため、
        // ここでそれらを取り除いて呼出元に返す
        for (AddressCorresponGroup g : result) {
            removeAddressUserIfEmpty(g.getUsers());
        }
        return result;
    }

    /**
     * 空の宛先-ユーザーが含まれていれば、usersから削除する.
     * @param users 宛先-ユーザーのリスト
     */
    private void removeAddressUserIfEmpty(List<AddressUser> users) {
        if (users == null) {
            return;
        }
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i) == null || users.get(i).getId() == null) {
                users.remove(i);
                continue;
            }
            removePersonInChargeIfEmpty(users.get(i).getPersonInCharges());
        }
    }

    /**
     * 空の担当者が含まれていれば、picsから削除する.
     * @param pics 担当者のリスト
     */
    private void removePersonInChargeIfEmpty(List<PersonInCharge> pics) {
        if (pics == null) {
            return;
        }
        for (int i = 0; i < pics.size(); i++) {
            if (pics.get(i) == null || pics.get(i).getId() == null) {
                pics.remove(i);
                continue;
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.dao.AddressCorresponGroupDao#deleteByCorresponId(java
     * .lang.Long)
     */
    public Integer deleteByCorresponId(AddressCorresponGroup addressCorresponGroup)
    throws KeyDuplicateException, StaleRecordException {
        return getSqlMapClientTemplate().update(
                       getSqlId(SQL_DELETE_BY_CORRESPON_ID), addressCorresponGroup);
    }
}
