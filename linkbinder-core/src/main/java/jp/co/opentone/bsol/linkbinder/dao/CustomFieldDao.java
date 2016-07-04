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
import jp.co.opentone.bsol.linkbinder.dto.CustomField;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCustomFieldCondition;

/**
 * custom_field を操作するDao.
 *
 * @author opentone
 *
 */
public interface CustomFieldDao extends GenericDao<CustomField> {

    /**
     * 検索条件に該当するカスタムフィールドを検索する.
     * @param condition 検索条件
     * @return カスタムフィールド
     */
    List<CustomField> findByProjectId(SearchCustomFieldCondition condition);

    /**
     * 検索条件に該当するカスタムフィールドを検索する.
     * @param condition 検索条件
     * @return カスタムフィールド
     * @throws RecordNotFoundException 検索失敗
     */
    CustomField findByIdProjectId(SearchCustomFieldCondition condition)
        throws RecordNotFoundException;

    /**
     * 検索条件に該当するカスタムフィールド件数を取得する.
     * @param condition 検索条件
     * @return カスタムフィールド件数
     */
    int count(SearchCustomFieldCondition condition);

    /**
     * 検索条件に該当するカスタムフィールドを検索する.
     * @param condition 検索条件
     * @return カスタムフィールド
     */
    List<CustomField> find(SearchCustomFieldCondition condition);

    /**
     * プロジェクトに属さないカスタムフィールドを取得する.
     * @param projectId プロジェクトID
     * @return プロジェクトに属さないカスタムフィールド
     */
    List<CustomField> findNotAssignTo(String projectId);

    /**
     * 条件を指定してカスタムフィールド件数を取得する(エラーチェック用).
     * @param condition 検索条件
     * @return カスタムフィールド件数
     */
    int countCheck(SearchCustomFieldCondition condition);

}
