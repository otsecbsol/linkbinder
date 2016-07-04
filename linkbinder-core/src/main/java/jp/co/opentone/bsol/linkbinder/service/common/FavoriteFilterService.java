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
package jp.co.opentone.bsol.linkbinder.service.common;

import java.util.List;

import jp.co.opentone.bsol.framework.core.service.IService;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.linkbinder.dto.FavoriteFilter;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchFavoriteFilterCondition;

/**
 * FavoriteFilterに関するサービスを提供する.
 * @author opentone
 */
public interface FavoriteFilterService extends IService {

    /**
     * FavoriteFilter を取得する.
     * @param condition 取得条件
     * @return FavoriteFilter のリスト
     * @throws ServiceAbortException 検索に失敗
     */
    List<FavoriteFilter> search(SearchFavoriteFilterCondition condition)
        throws ServiceAbortException;

    /**
     * FavoriteFilter を取得する.
     * @param id FavoriteFilter のキー
     * @return FavoriteFilter
     * @throws ServiceAbortException 検索に失敗
     */
    FavoriteFilter find(Long id) throws ServiceAbortException;

    /**
     * FavoriteFilter を保存する.
     * @param favoriteFilter 保存したいFavoriteFilter
     * @throws ServiceAbortException 作成に失敗
     */
    void save(FavoriteFilter favoriteFilter) throws ServiceAbortException;

    /**
     * FavoriteFilter を削除する.
     * @param favoriteFilter 削除したいFavoriteFilter
     * @throws ServiceAbortException 削除に失敗
     */
    void delete(FavoriteFilter favoriteFilter) throws ServiceAbortException;

}
