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
import jp.co.opentone.bsol.linkbinder.dao.AbstractDao;
import jp.co.opentone.bsol.linkbinder.dao.CustomFieldValueDao;
import jp.co.opentone.bsol.linkbinder.dto.CustomFieldValue;

/**
 * custom_field_value を操作するDao.
 *
 * @author opentone
 *
 */
@Repository
public class CustomFieldValueDaoImpl extends AbstractDao<CustomFieldValue> implements
    CustomFieldValueDao {
    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -8737897486918546334L;
    /**
     * このクラスが利用するsqlMap.xmlのnamespace.
     */
    private static final String NAMESPACE = "customFieldValue";

    /**
     * SQLID: カスタムフィールドIDを指定してカスタムフィールド設定値を取得.
     */
    private static final String SQL_FIND_BY_CUSTOM_FIELD_ID = "findByCustomFieldId";

    /**
     * SQLID: カスタムフィールドIDを指定してカスタムフィールド設定値を削除する.
     */
    private static final String SQL_DELETE_BY_COSTOM_FIELD_ID = "deleteByCustomFieldId";

    /**
     * 空のインスタンスを生成する.
     */
    public CustomFieldValueDaoImpl() {
        super(NAMESPACE);
    }

    /*
     * (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.dao.CustomFieldValueDao#findByCustomFieldId(java.lang
     * .Long)
     */
    @SuppressWarnings("unchecked")
    public List<CustomFieldValue> findByCustomFieldId(Long customFieldId) {
        return (List<CustomFieldValue>) getSqlMapClientTemplate()
            .queryForList(getSqlId(SQL_FIND_BY_CUSTOM_FIELD_ID), customFieldId);
    }

    /*
     * (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.dao.CustomFieldValueDao#deleteByCustomFieldId(
     *  jp.co.opentone.bsol.linkbinder.dto.CustomFieldValue)
     */
    public Integer deleteByCustomFieldId(CustomFieldValue customFieldValue)
        throws KeyDuplicateException {
        return getSqlMapClientTemplate().update(getSqlId(SQL_DELETE_BY_COSTOM_FIELD_ID),
            customFieldValue);
    }
}
