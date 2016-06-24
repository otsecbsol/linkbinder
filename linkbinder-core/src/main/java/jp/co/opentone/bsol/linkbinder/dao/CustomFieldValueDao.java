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
import jp.co.opentone.bsol.linkbinder.dto.CustomFieldValue;

/**
 * custom_field_value を操作するDao.
 *
 * @author opentone
 *
 */
public interface CustomFieldValueDao extends GenericDao<CustomFieldValue> {

    /**
     * カスタムフィールドIDで、カスタムフィールド設定値を検索する.
     * @param customFieldId
     *            カスタムフィールドID
     * @return カスタムフィールド設定値
     */
    List<CustomFieldValue> findByCustomFieldId(Long customFieldId);

    /**
     * カスタムフィールドIDで、カスタムフィールド設定値を削除する.
     * @param customFieldValue
     *            カスタムフィールド値
     * @return 削除件数
     * @throws KeyDuplicateException
     *             同じデータが存在
     */
    Integer deleteByCustomFieldId(CustomFieldValue customFieldValue) throws KeyDuplicateException;
}
