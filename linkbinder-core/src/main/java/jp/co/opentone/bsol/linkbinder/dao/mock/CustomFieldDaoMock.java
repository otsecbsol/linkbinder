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

import jp.co.opentone.bsol.linkbinder.dao.AbstractDao;
import jp.co.opentone.bsol.linkbinder.dao.CustomFieldDao;
import jp.co.opentone.bsol.linkbinder.dto.CustomField;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCustomFieldCondition;

@Repository
public class CustomFieldDaoMock extends AbstractDao<CustomField> implements CustomFieldDao {

    public CustomFieldDaoMock() {
        super("mock");
    }

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * カスタムフィールドで、カスタムフィールドを検索する。
     * @param Long
     *            customFieldId
     * @return カスタムフィールド設定値
     */
    public List<CustomField> find(SearchCustomFieldCondition condition) {
        List<CustomField> list = new ArrayList<CustomField>();

        CustomField c = new CustomField();
        c.setId(new Long(1));
        c.setLabel("CustomField1");
        c.setOrderNo(new Long(1));
        c.setProjectId("PJ1");
        list.add(c);

        c = new CustomField();
        c.setId(new Long(2));
        c.setLabel("CustomField2");
        c.setOrderNo(new Long(2));
        c.setProjectId("PJ1");
        list.add(c);

        c = new CustomField();
        c.setId(new Long(3));
        c.setLabel("CustomField3");
        c.setOrderNo(new Long(3));
        c.setProjectId("PJ1");
        list.add(c);

        return list;
    }

    /**
     * カスタムフィールドで、カスタムフィールドを検索する。
     * @param Long
     *            customFieldId
     * @return カスタムフィールド設定値
     */
    @Override
    public CustomField findById(Long id) {

        CustomField c = new CustomField();
        c.setId(new Long(1));
        c.setLabel("CustomField1");
        c.setOrderNo(new Long(1));
        c.setProjectId("PJ1");

        return c;
    }

    /**
     * カスタムフィールドで、カスタムフィールドを検索する。
     * @param Long
     *            customFieldId
     * @return カスタムフィールド設定値
     */
    public List<CustomField> findByProjectId(SearchCustomFieldCondition condition) {

        List<CustomField> c = new ArrayList<CustomField>();
//        c.setId(new Long(1));
//        c.setLabel("CustomField1");
//        c.setOrderNo(new Long(1));
//        c.setProjectId("PJ1");

        return c;
    }
    /**
     * カスタムフィールドで、カスタムフィールドを検索する。
     * @param Long
     *            customFieldId
     * @return カスタムフィールド設定値
     */
    public CustomField findByIdProjectId(SearchCustomFieldCondition condition) {

        CustomField c = new CustomField();
        c.setId(new Long(1));
        c.setLabel("CustomField1");
        c.setOrderNo(new Long(1));
        c.setProjectId("PJ1");

        return c;
    }

    public int count(SearchCustomFieldCondition condition) {
        return 1;
    }

    public List<CustomField> findNotAssignTo(String projectId) {
        List<CustomField> list = new ArrayList<CustomField>();

        CustomField c = new CustomField();
        c.setId(new Long(11));
        c.setLabel("CustomField11");
        c.setOrderNo(new Long(11));
        c.setProjectId("PJ1");
        list.add(c);

        c = new CustomField();
        c.setId(new Long(12));
        c.setLabel("CustomField12");
        c.setOrderNo(new Long(12));
        c.setProjectId("PJ1");
        list.add(c);

        c = new CustomField();
        c.setId(new Long(13));
        c.setLabel("CustomField13");
        c.setOrderNo(new Long(13));
        c.setProjectId("PJ1");
        list.add(c);
        return list;
    }

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.dao.CustomFieldDao#countCheck(jp.co.opentone.bsol.linkbinder.dto.condition.SearchCustomFieldCondition)
     */
    public int countCheck(SearchCustomFieldCondition condition) {
        return 0;
    }

}
