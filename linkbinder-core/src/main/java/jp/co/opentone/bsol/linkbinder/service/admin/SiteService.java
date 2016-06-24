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
package jp.co.opentone.bsol.linkbinder.service.admin;

import java.util.List;

import jp.co.opentone.bsol.framework.core.service.IService;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.linkbinder.dto.SearchSiteResult;
import jp.co.opentone.bsol.linkbinder.dto.Site;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchSiteCondition;

/**
 * このサービスでは部門情報に関する処理を提供する.
 * @author opentone
 */
public interface SiteService extends IService {

    /**
     * 指定された条件に該当する拠点情報を検索し、指定ページのレコードだけを返す.
     * @param condition
     *            検索条件
     * @return 拠点情報リスト
     * @throws ServiceAbortException
     *             検索エラー
     */
    SearchSiteResult search(SearchSiteCondition condition) throws ServiceAbortException;

    /**
     * 指定された拠点情報一覧をExcel形式に変換して返す.
     * @param sites
     *            拠点情報一覧
     * @return Excelデータ
     * @throws ServiceAbortException
     *             変換エラー
     */
    byte[] generateExcel(List<Site> sites) throws ServiceAbortException;

    /**
     * 指定された拠点を削除する.
     * @param site
     *            拠点情報
     * @throws ServiceAbortException
     *             削除失敗
     */
    void delete(Site site) throws ServiceAbortException;

    /**
     * 指定された拠点情報を返す.
     * @param id
     *            ID
     * @return 拠点情報
     * @throws ServiceAbortException
     *             検索エラー
     */
    Site find(Long id) throws ServiceAbortException;

    /**
     * 入力値を検証する.
     * @param site
     *            拠点情報
     * @throws ServiceAbortException
     *             検証NG
     */
    void validate(Site site) throws ServiceAbortException;

    /**
     * 指定された拠点を保存する.
     * @param site
     *            拠点情報
     * @return 保存された拠点情報のID
     * @throws ServiceAbortException
     *             保存処理失敗
     */
    Long save(Site site) throws ServiceAbortException;
}
