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
import jp.co.opentone.bsol.linkbinder.dao.LearningLabelDao;
import jp.co.opentone.bsol.linkbinder.dto.LearningLabel;
import jp.co.opentone.bsol.linkbinder.dto.LearningLabelCorrespon;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * learning_label を操作するDao.
 *
 * @author opentone
 *
 */
@Repository
public class LearningLabelDaoImpl extends AbstractDao<LearningLabel> implements LearningLabelDao {
    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -8737897486918546334L;
    /**
     * このクラスが利用するsqlMap.xmlのnamespace.
     */
    private static final String NAMESPACE = "learningLabel";

    /**
     * SQLID: 有効なラベルが設定された文書を検索するID.
     */
    private static final String SQL_FIND_LEARNING_LABEL_CORRESPON = "findLearningLabelCorrespon";
    /**
     * SQLID: 指定プロジェクトの学習用ラベルを検索するID.
     */
    private static final String SQL_FIND_BY_PROJECT_ID = "findByProjectId";

    /**
     * 前方一致検索を行うフィールド名.
     */
    private static final List<String> FIELDS;
    static {
        FIELDS = new ArrayList<String>();
        FIELDS.add("name");
    }

    /**
     * 空のインスタンスを生成する.
     */
    public LearningLabelDaoImpl() {
        super(NAMESPACE);
    }

    @Override
    public List<LearningLabelCorrespon> findLearningLabelCorrespon() {
        return (List<LearningLabelCorrespon>) getSqlMapClientTemplate()
                    .queryForList(getSqlId(SQL_FIND_LEARNING_LABEL_CORRESPON));
    }

    @Override
    public List<LearningLabel> findByProjectId(String projectId) {
        return (List<LearningLabel>) getSqlMapClientTemplate()
                .queryForList(getSqlId(SQL_FIND_BY_PROJECT_ID),projectId);
    }
}
