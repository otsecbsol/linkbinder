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
import jp.co.opentone.bsol.linkbinder.dto.CustomField;
import jp.co.opentone.bsol.linkbinder.dto.CustomFieldValue;
import jp.co.opentone.bsol.linkbinder.dto.SearchCustomFieldResult;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCustomFieldCondition;

/**
 * このサービスではカスタムフィールドに関する処理を提供する.
 * @author opentone
 */
public interface CustomFieldService extends IService {

    /**
     * 指定された条件に該当するカスタムフィールドを検索し返す.
     * @param condition
     *            検索条件 condition
     * @return カスタムフィールド
     */
    List<CustomField> search(SearchCustomFieldCondition condition);

    /**
     * 指定された条件に該当するカスタムフィールド値を検索し返す.
     * @param customFieldId
     *            ID
     * @return カスタムフィールド値
     */
    List<CustomFieldValue> findCustomFieldValue(Long customFieldId);

    /**
     * 指定されたカスタムフィールド一覧をExcel形式に変換して返す.
     * @param customFields
     *            カスタムフィールド
     * @return Excel形式に変換されたカスタムフィールド
     * @throws ServiceAbortException
     *             変換失敗
     */
    byte[] generateExcel(List<CustomField> customFields) throws ServiceAbortException;

    /**
     * 指定されたカスタムフィールドを削除する.
     * @param customField
     *            カスタムフィールド
     * @throws ServiceAbortException
     *             削除失敗
     */
    void delete(CustomField customField) throws ServiceAbortException;

    /**
     * 指定されたカスタムフィールドをプロジェクトで利用可能にする.
     * @param customField
     *            カスタムフィールド
     * @return ID
     * @throws ServiceAbortException
     *             追加失敗
     */
    Long assignTo(CustomField customField) throws ServiceAbortException;

    /**
     * 指定されたカスタムフィールドを返す.
     * @param id
     *            ID
     * @return カスタムフィールド
     * @throws ServiceAbortException
     *             取得失敗
     */
    CustomField find(Long id) throws ServiceAbortException;

    /**
     * 入力値を検証する.
     * @param customField
     *            カスタムフィールド
     * @return 問題ないtrue / 問題ありfalse
     * @throws ServiceAbortException
     *             検証エラー
     */
    boolean validate(CustomField customField) throws ServiceAbortException;

    /**
     * 指定されたカスタムフィールドを保存する.
     * @param customField
     *            カスタムフィールド
     * @return 保存したカスタムフィールドのID
     * @throws ServiceAbortException
     *             保存失敗
     */
    Long save(CustomField customField) throws ServiceAbortException;

    /**
     * プロジェクトに登録されていないカスタムフィールドを取得する.
     * @return カスタムフィールドリスト
     * @throws ServiceAbortException
     *             取得失敗
     */
    List<CustomField> searchNotAssigned() throws ServiceAbortException;

    /**
     * 指定された条件に該当するカスタムフィールドを検索し返す.
     * @param condition
     *            検索条件
     * @return 画面表示一覧用カスタムフィールド
     * @throws ServiceAbortException
     *             検索処理失敗
     */
    SearchCustomFieldResult searchPagingList(SearchCustomFieldCondition condition)
        throws ServiceAbortException;

}
