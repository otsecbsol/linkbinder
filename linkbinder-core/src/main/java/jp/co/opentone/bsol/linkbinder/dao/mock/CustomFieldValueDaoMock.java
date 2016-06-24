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
package jp.co.opentone.bsol.linkbinder.dao.mock;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import jp.co.opentone.bsol.framework.core.dao.KeyDuplicateException;
import jp.co.opentone.bsol.linkbinder.dao.AbstractDao;
import jp.co.opentone.bsol.linkbinder.dao.CustomFieldValueDao;
import jp.co.opentone.bsol.linkbinder.dto.CustomFieldValue;

@Repository
public class CustomFieldValueDaoMock extends AbstractDao<CustomFieldValue> implements
        CustomFieldValueDao {

    public CustomFieldValueDaoMock() {
        super("mock");
    }

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * カスタムフィールドIDで、カスタムフィールド設定値を検索する。
     * @param Long
     *            customFieldId
     * @return カスタムフィールド設定値
     */
    public List<CustomFieldValue> findByCustomFieldId(Long customFieldId) {
        List<CustomFieldValue> list = new ArrayList<CustomFieldValue>();
        CustomFieldValue c = new CustomFieldValue();
        c.setId(new Long(1));
        c.setCustomFieldId(new Long(1));
        c.setValue("CustomFieldValue");
        c.setOrderNo(new Long(1));

        list.add(c);

        c = new CustomFieldValue();
        c.setId(new Long(2));
        c.setCustomFieldId(new Long(2));
        c.setValue("CustomFieldValue2");
        c.setOrderNo(new Long(2));

        list.add(c);

        c = new CustomFieldValue();
        c.setId(new Long(3));
        c.setCustomFieldId(new Long(3));
        c.setValue("CustomFieldValue3");
        c.setOrderNo(new Long(3));

        list.add(c);

        c = new CustomFieldValue();
        c.setId(new Long(4));
        c.setCustomFieldId(new Long(4));
        c.setValue("CustomFieldValue4");
        c.setOrderNo(new Long(4));

        list.add(c);

        c = new CustomFieldValue();
        c.setId(new Long(5));
        c.setCustomFieldId(new Long(5));
        c.setValue("CustomFieldValue5");
        c.setOrderNo(new Long(5));

        list.add(c);

        return list;
    }

    public Integer deleteByCustomFieldId(CustomFieldValue customFieldValue)
        throws KeyDuplicateException {
        return 1;
    }
}
