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
import jp.co.opentone.bsol.framework.core.dao.RecordNotFoundException;
import jp.co.opentone.bsol.linkbinder.dto.CorresponType;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponTypeCondition;

/**
 * correspon_type を操作するDao.
 *
 * @author opentone
 *
 */
public interface CorresponTypeDao extends GenericDao<CorresponType> {

    /**
     * 検索条件に該当するコレポン文書種別を検索し返す.
     * @param conditon 検索条件
     * @return コレポン文書種別
     */
    List<CorresponType> find(SearchCorresponTypeCondition conditon);

    /**
     * 検索条件に該当するコレポン文書種別の件数を取得する.
     * @param conditon 検索条件
     * @return 件数
     */
    int count(SearchCorresponTypeCondition conditon);

    /**
     * 指定したプロジェクトに登録されていないコレポン文書種別を検索し返す.
     * @param projectId プロジェクトID
     * @return コレポン文書種別
     */
    List<CorresponType> findNotExist(String projectId);

    /**
     * 検索条件に該当するコレポン文書種別を検索し返す.
     * @param projectCorresponTypeId プロジェクト - コレポン文書ID
     * @return コレポン文書種別
     * @throws RecordNotFoundException 該当するコレポン文書種別が存在しない
     */
    CorresponType findByProjectCorresponTypeId(Long projectCorresponTypeId)
            throws RecordNotFoundException;

    /**
     * 検索条件に該当したコレポン文書種別の件数を取得する.（チェック用）.
     * @param condition 検索条件
     * @return 件数
     */
    int countCheck(SearchCorresponTypeCondition condition);

    /**
     * IDとプロジェクトIDを指定してコレポン文書種別を取得する.
     * @param id ID
     * @param projectId プロジェクトID
     * @return コレポン文書種別
     * @throws RecordNotFoundException 取得失敗
     */
    CorresponType findByIdProjectId(Long id, String projectId) throws RecordNotFoundException;

}
