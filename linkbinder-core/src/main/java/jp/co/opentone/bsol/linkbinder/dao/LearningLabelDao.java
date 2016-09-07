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
import jp.co.opentone.bsol.linkbinder.dto.LearningLabel;
import jp.co.opentone.bsol.linkbinder.dto.LearningLabelCorrespon;

import java.util.List;

/**
 * learning_labelを操作するDao.
 *
 * @author opentone
 *
 */
public interface LearningLabelDao extends GenericDao<LearningLabel> {

    /**
     * 有効なラベルが設定された学習用文書を返す.
     * @return 検索結果
     */
    List<LearningLabelCorrespon> findLearningLabelCorrespon();

    /**
     * 指定されたプロジェクトの、全ての関連付けられた学習用ラベルを返す.
     * @return 検索結果
     */
    List<LearningLabel> findByProjectId(String projectId);

    /**
     * 指定されたプロジェクトの、利用されている全ての学習用ラベルを返す.
     * @return 検索結果
     */
    List<LearningLabel> findExsistLabel();


    /**
     * 指定された文書に設定された学習用ラベルを返す.
     * @return 検索結果
     */
    List<LearningLabel> findByCorresponId(Long corresponId);

    /**
     * 指定された学習用ラベルが使われていなければ削除する.
     * @param label ラベル
     */
    void deleteIfUnused(LearningLabel label);
}
