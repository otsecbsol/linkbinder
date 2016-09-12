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
import jp.co.opentone.bsol.linkbinder.dto.LearningTag;

import java.util.List;

/**
 * このサービスでは学習用タグに関する処理を提供する.
 * @author opentone
 */
public interface LearningTagService extends IService {

    /**
     * 現在のプロジェクトに登録済みのすべての学習用タグを返す.
     * @return 検索結果
     * @throws ServiceAbortException 検索に失敗
     */
    List<LearningTag> findAll() throws ServiceAbortException;

    /**
     * 指定された文書に設定されたタグをすべて削除する.
     * @param correspon 文書
     * @throws ServiceAbortException 削除に失敗
     */
    void clearAllLearningTags(Correspon correspon) throws ServiceAbortException;

    /**
     * 学習用文書のタグを保存する.
     * @param correspon 学習用文書
     * @throws ServiceAbortException 保存に失敗
     */
    void saveLearningTags(Correspon correspon) throws ServiceAbortException;
}
