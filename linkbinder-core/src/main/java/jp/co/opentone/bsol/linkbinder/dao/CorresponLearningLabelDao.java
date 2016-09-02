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
import jp.co.opentone.bsol.linkbinder.dto.CorresponLearningLabel;

import java.util.List;

/**
 * correspon_learning_labelを操作するDao.
 *
 * @author opentone
 *
 */
public interface CorresponLearningLabelDao extends GenericDao<CorresponLearningLabel> {

    /**
     * ラベルと文書の関連を登録する。
     */
    Long insertLearningLabel(CorresponLearningLabel label);

    /**
     * 文書に紐付いたラベルの一覧を返す.
     * @param corresponId
     * @return ラベル-文書関連情報.
     */
    List<CorresponLearningLabel> findByCorresponId(Long corresponId);
}
