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
import jp.co.opentone.bsol.linkbinder.dto.LearningTag;

import java.util.List;

/**
 * learning_tagを操作するDao.
 *
 * @author opentone
 *
 */
public interface LearningTagDao extends GenericDao<LearningTag> {

    /**
     * 指定されたプロジェクトのすべての学習タグを返す.
     * @return 検索結果
     */
    List<LearningTag> findByProjectId(String projectId);

    List<LearningTag> findByCorresponId(Long corresponId);

    /**
     * 指定された学習用タグが使われていなければ削除する.
     * @param tag タグ
     */
    void deleteIfUnused(LearningTag tag);
}
