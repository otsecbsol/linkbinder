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
import jp.co.opentone.bsol.linkbinder.dao.CorresponLearningTagDao;
import jp.co.opentone.bsol.linkbinder.dto.CorresponLearningTag;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * correspon_learning_tag を操作するDao.
 *
 * @author opentone
 *
 */
@Repository
public class CorresponLearningTagDaoImpl extends AbstractDao<CorresponLearningTag> implements CorresponLearningTagDao {
    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -8737897486918546334L;
    /**
     * このクラスが利用するsqlMap.xmlのnamespace.
     */
    private static final String NAMESPACE = "corresponLearningTag";

    private static final String SQL_FIND_BY_CORRESPON_ID = "corresponLearningTag.findByCorresponId";

    /**
     * 前方一致検索を行うフィールド名.
     */
    private static final List<String> FIELDS;
    static {
        FIELDS = new ArrayList<String>();
        FIELDS.add("tag_id");
    }

    /**
     * 空のインスタンスを生成する.
     */
    public CorresponLearningTagDaoImpl() {
        super(NAMESPACE);
    }

    @Override
    public List<CorresponLearningTag> findByCorresponId(Long corresponId) {
        return (List<CorresponLearningTag>)getSqlMapClientTemplate().queryForList(SQL_FIND_BY_CORRESPON_ID,corresponId);
    }


}
