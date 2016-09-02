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

import jp.co.opentone.bsol.linkbinder.dao.AbstractDao;
import jp.co.opentone.bsol.linkbinder.dao.CorresponLearningLabelDao;
import jp.co.opentone.bsol.linkbinder.dto.CorresponLearningLabel;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * correspon_learning_label を操作するDao.
 *
 * @author opentone
 *
 */
@Repository
public class CorresponLearningLabelDaoImpl extends AbstractDao<CorresponLearningLabel> implements CorresponLearningLabelDao {
    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -8737897486918546334L;
    /**
     * このクラスが利用するsqlMap.xmlのnamespace.
     */
    private static final String NAMESPACE = "corresponLearningLabel";

    private static final String SQL_INSERT_CORRESPON_LEARNING_LABEL = "corresponLearningLabel.insertCorresponLearningLabel";

    private static final String SQL_FIND_BY_CORRESPON_ID = "corresponLearningLabel.findByCorresponId";

    /**
     * 前方一致検索を行うフィールド名.
     */
    private static final List<String> FIELDS;
    static {
        FIELDS = new ArrayList<String>();
        FIELDS.add("label_id");
    }

    /**
     * 空のインスタンスを生成する.
     */
    public CorresponLearningLabelDaoImpl() {
        super(NAMESPACE);
    }

    @Override
    public Long insertLearningLabel(CorresponLearningLabel label) {
        return (Long)getSqlMapClientTemplate().insert(SQL_INSERT_CORRESPON_LEARNING_LABEL,label);
    }

    @Override
    public List<CorresponLearningLabel> findByCorresponId(Long corresponId) {
        return (List<CorresponLearningLabel>)getSqlMapClientTemplate().queryForList(SQL_FIND_BY_CORRESPON_ID,corresponId);
    }


}
