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

import jp.co.opentone.bsol.framework.core.dao.GenericDao;
import jp.co.opentone.bsol.framework.core.dao.KeyDuplicateException;
import jp.co.opentone.bsol.framework.core.dao.RecordNotFoundException;
import jp.co.opentone.bsol.linkbinder.dto.CorresponReadStatus;

/**
 * correspon_read_status を操作するDao.
 *
 * @author opentone
 *
 */
public interface CorresponReadStatusDao extends GenericDao<CorresponReadStatus> {

    /**
     * コレポン文書の既読・未読ステータスをコレポンIDで更新する.
     * @param dto
     *            既読・未読状態
     * @return 更新件数
     * @throws KeyDuplicateException 更新エラー
     */
    Integer updateByCorresponId(CorresponReadStatus dto) throws KeyDuplicateException;
    /**
     * コレポン文書の既読・未読ステータスを検索する.
     * @param id コレポン文書ID
     * @param empNo 従業員番号
     * @return 既読・未読ステータス情報
     * @throws RecordNotFoundException 検索件数0件
     */
    CorresponReadStatus findByEmpNo(Long id, String empNo) throws RecordNotFoundException;
}
