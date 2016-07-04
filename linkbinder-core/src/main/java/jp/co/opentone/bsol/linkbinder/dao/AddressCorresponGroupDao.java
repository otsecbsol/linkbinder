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
import jp.co.opentone.bsol.linkbinder.dto.AddressCorresponGroup;

/**
 * address_correspon_group を操作するDao.
 *
 * @author opentone
 *
 */
public interface AddressCorresponGroupDao extends GenericDao<AddressCorresponGroup> {

    /**
     * 指定されたコレポン文書に設定された全ての宛先活動単位を返す.
     * @param corresponId
     *            コレポン文書ID
     * @return 宛先活動単位
     */
    List<AddressCorresponGroup> findByCorresponId(Long corresponId);

    /**
     * コレポン文書IDで、宛先-活動単位を削除する.
     * @param addressCorresponGroup
     *            宛先-活動単位
     * @return Integer
     *            削除した件数
     * @throws KeyDuplicateException
     *            キー重複
     * @throws StaleRecordException
     *            排他
     */
    Integer deleteByCorresponId(AddressCorresponGroup addressCorresponGroup)
    throws KeyDuplicateException, StaleRecordException;

}
