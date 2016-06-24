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
package jp.co.opentone.bsol.framework.core.dao;

import java.io.Serializable;

/**
 * Genericsを使って汎用的なDaoを作成するためのインターフェイス.
 * @author opentone
 */
public interface GenericDao<T extends Entity> extends Dao, Serializable {

    /**
     * プライマリキーを条件に1件のレコードを取得して返す.
     * @param id
     *            プライマリキー
     * @return 1件のレコードを表すentity
     * @throws RecordNotFoundException
     *             該当するレコードが無い場合
     */
    T findById(Long id) throws RecordNotFoundException;

    /**
     * プライマリキーを条件に1件のレコードを取得して返す. 取得したレコードはロックされる.
     * @param id
     *            プライマリキー
     * @return 1件のレコードを表すentity
     * @throws RecordNotFoundException
     *             該当するレコードが無い場合
     */
    T findByIdForUpdate(Long id) throws RecordNotFoundException;

    /**
     * 新しいレコードを登録する.
     * @param entity
     *            登録対象のentity
     * @return 生成したレコードを一意に識別する値
     * @throws KeyDuplicateException
     *             KeyDuplicateException
     */
    Long create(T entity) throws KeyDuplicateException;

    /**
     * 既存のレコードを、プライマリキーを条件に1件更新する.
     * @param entity
     *            更新対象のentity
     * @return 影響した件数
     * @throws KeyDuplicateException
     *             キー重複、外部キー制約エラー
     * @throws StaleRecordException
     *             対象レコードが他のセッションにより更新済の場合
     */
    Integer update(T entity) throws KeyDuplicateException, StaleRecordException;

    /**
     * 既存のレコードを、プライマリキーを条件に1件削除する.
     * @param entity
     *            削除対象のentity
     * @return 影響した件数
     * @throws KeyDuplicateException
     *             キー重複、外部キー制約エラー
     * @throws StaleRecordException
     *             対象レコードが他のセッションにより更新済の場合
     */
    Integer delete(T entity) throws KeyDuplicateException, StaleRecordException;
}
