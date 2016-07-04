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
package jp.co.opentone.bsol.linkbinder.dao;

import java.util.List;

import jp.co.opentone.bsol.framework.core.dao.GenericDao;
import jp.co.opentone.bsol.framework.core.dao.KeyDuplicateException;
import jp.co.opentone.bsol.framework.core.dao.StaleRecordException;
import jp.co.opentone.bsol.linkbinder.dto.AddressUser;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchAddressUserCondition;

/**
 * address_user を操作するDao.
 *
 * @author opentone
 *
 */
public interface AddressUserDao extends GenericDao<AddressUser> {

    /**
     * 指定された宛先-活動単位IDに設定された全ての宛先ユーザーを返す.
     * @param addressCorresponGroupId
     *            宛先-活動単位ID
     * @return 宛先ユーザー
     */
    List<AddressUser> findByAddressCorresponGroupId(Long addressCorresponGroupId);

    /**
     * 宛先-活動単位IDで、宛先-ユーザーを削除する.
     * @param addressUser
     *            宛先-ユーザー
     * @return Integer
     *            削除した件数
     * @throws KeyDuplicateException
     *            キー重複
     * @throws StaleRecordException
     *            排他
     */
    Integer deleteByAddressCorresponGroupId(AddressUser addressUser)
    throws KeyDuplicateException, StaleRecordException;

    /**
     * 指定された宛先ユーザーの検索条件に設定された全ての宛先ユーザーを返す.
     * @param condition
     *            宛先ユーザー検索条件
     * @return 宛先ユーザー
     */
    List<AddressUser> findSendApplyUser(SearchAddressUserCondition condition);

    /**
     * 指定された宛先ユーザーの検索条件に設定された全ての宛先ユーザーを返す.
     * @param condition
     *            宛先ユーザー検索条件
     * @return 宛先ユーザー
     */
    List<AddressUser> findSendApplyUserForPersonInCharge(SearchAddressUserCondition condition);
}
