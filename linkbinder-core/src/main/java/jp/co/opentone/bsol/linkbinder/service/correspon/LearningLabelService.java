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
package jp.co.opentone.bsol.linkbinder.service.correspon;

import jp.co.opentone.bsol.framework.core.service.IService;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.linkbinder.dto.Correspon;
import jp.co.opentone.bsol.linkbinder.dto.CorresponLearningLabel;
import jp.co.opentone.bsol.linkbinder.dto.LearningLabel;

import java.util.List;

/**
 * このサービスでは学習用ラベルに関する処理を提供する.
 * @author opentone
 */
public interface LearningLabelService extends IService {

    /**
     * 現在のプロジェクトに登録済みのすべての学習用ラベルを返す.
     * @return 検索結果
     * @throws ServiceAbortException 検索に失敗
     */
    List<LearningLabel> findAll() throws ServiceAbortException;

    /**
     * 利用されている学習用ラベルを返す.
     * @return 検索結果
     */
    List<LearningLabel> findExsistLabel();

    /**
     * 指定された学習用ラベル１件を登録する.
     * @param label 学習用ラベル
     * @return 学習用ラベルId
     */
    Long insertLearningLabel(LearningLabel label);

    /**
     * ラベルと文書の関連を登録する。
     * @param label 学習用ラベル
     * @param correspon 文書
     * @return 紐付けたID
     */
    Long insertCorresponLearningLabel(LearningLabel label,Correspon correspon);

    /**
     *
     * @param corresponId
     * @return
     */
    List<CorresponLearningLabel> findByCorresponId(Long corresponId);
}
