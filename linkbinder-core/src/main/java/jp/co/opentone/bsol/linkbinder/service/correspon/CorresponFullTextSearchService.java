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
import jp.co.opentone.bsol.linkbinder.dto.Attachment;
import jp.co.opentone.bsol.linkbinder.dto.Correspon;
import jp.co.opentone.bsol.linkbinder.dto.FullTextSearchCorresponsResult;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchFullTextSearchCorresponCondition;

import java.util.List;

/**
 * このサービスではコレポン文書全文検索に関する処理を提供する.
 *
 * @author opentone
 */
public interface CorresponFullTextSearchService extends IService {

    /**
     * 入力されたKeyWordを含むコレポン文書を取得する.
     *
     * <p>
     * 取得に成功した際は、取得した結果情報を返す.
     * </p>
     *
     * @param condition
     *            入力されたキーワード
     * @return CorresponFullTextSearchResult
     *            全文検索結果情報
     * @throws ServiceAbortException
     *            対象のコレポン文書の取得に失敗した場合
     */
    List<FullTextSearchCorresponsResult> search(SearchFullTextSearchCorresponCondition condition)
        throws ServiceAbortException;

    /**
     * 入力されたKeyWordを含むコレポン文書を取得する.
     *
     * <p>
     * 取得に成功した際は、取得した結果情報を返す.
     * </p>
     *
     * @param condition
     *            入力されたキーワード
     * @return CorresponFullTextSearchResult
     *            全文検索結果情報
     * @throws ServiceAbortException
     *            対象のコレポン文書の取得に失敗した場合
     */
    List<FullTextSearchCorresponsResult> searchNoLimit(
            SearchFullTextSearchCorresponCondition condition)
        throws ServiceAbortException;
    /**
     * 入力されたKeyWordを含むコレポン文書IDを取得する.
     *
     * <p>
     * 取得に成功した際は、取得した結果情報を返す.
     * </p>
     *
     * @param condition
     *            入力されたキーワード
     * @return id
     *            全文検索結果のコレポンID
     * @throws ServiceAbortException
     *            対象のコレポン文書の取得に失敗した場合
     */
    List<Long> searchId(SearchFullTextSearchCorresponCondition condition)
        throws ServiceAbortException;

    /**
     * 指定された文書をインデックスに追加する.
     * @param correspon 文書
     * @param attachments 添付ファイル
     * @throws ServiceAbortException 追加に失敗した場合
     */
    void addToIndex(Correspon correspon, List<Attachment> attachments)
            throws ServiceAbortException;

    /**
     * 指定された文書をインデックスから削除する.
     * @param correspon 文書
     * @param attachments 添付ファイル
     * @throws ServiceAbortException 削除に失敗した場合
     */
    void deleteFromIndex(Correspon correspon, List<Attachment> attachments)
            throws ServiceAbortException;

    /**
     * インデックスを作成する.
     * @param projectId プロジェクトID
     * @throws ServiceAbortException 作成に失敗した場合
     */
    void createIndex(String projectId) throws ServiceAbortException;
}
