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
import jp.co.opentone.bsol.framework.core.dao.RecordNotFoundException;
import jp.co.opentone.bsol.linkbinder.dto.ProjectCustomField;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCustomFieldCondition;

/**
 * project_custom_field を操作するDao.
 *
 * @author opentone
 *
 */
public interface ProjectCustomFieldDao extends GenericDao<ProjectCustomField> {

    /**
     * 条件を指定してプロジェクトカスタムフィールド件数を取得する(エラーチェック用).
     * @param condition 検索条件
     * @return プロジェクトカスタムフィールド件数
     */
    int countCheck(SearchCustomFieldCondition condition);

    /**
     * 条件を指定してidを取得する.
     * @param condition 検索条件
     * @return id id
     * @throws RecordNotFoundException 例外
     */
    Long findIdByProjectIdNo(SearchCustomFieldCondition condition)
        throws RecordNotFoundException;

    /**
     * 条件を指定してproject_custom_field_idを取得する.
     * @param condition 検索条件
     * @return id id
     * @throws RecordNotFoundException 例外
     */
    Long findProjectCustomFieldIdByProjectIdNo(SearchCustomFieldCondition condition)
        throws RecordNotFoundException;

}
