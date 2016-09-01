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
import jp.co.opentone.bsol.linkbinder.dto.Project;
import jp.co.opentone.bsol.linkbinder.dto.SearchProjectResult;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchProjectCondition;

/**
 * このサービスではプロジェクト情報に関する処理を提供する.
 * @author opentone
 */
public interface ProjectService extends IService {

    /**
     * 指定された条件に該当するプロジェクト情報を返す.
     * @param condition
     *            検索条件
     * @return 検索結果
     * @throws ServiceAbortException
     *             検索に失敗
     */
    List<Project> search(SearchProjectCondition condition) throws ServiceAbortException;

    /**
     * 指定された条件に該当するプロジェクト情報を返す.
     * @param condition
     *            検索条件
     * @return 検索結果
     * @throws ServiceAbortException
     *             検索に失敗
     */
    SearchProjectResult searchPagingList(SearchProjectCondition condition)
            throws ServiceAbortException;

    /**
     * 指定されたプロジェクト情報一覧をExcel形式に変換して返す.
     * @param projects
     *            プロジェクト情報
     * @return Excelデータ
     * @throws ServiceAbortException
     *             変換エラー
     */
    byte[] generateExcel(List<Project> projects) throws ServiceAbortException;

    /**
     * 指定されたIDに該当するプロジェクト情報を返す.
     * @param id 検索プロジェクトID
     * @return 検索結果。一致結果がない場合はnullを返却する。
     * @throws ServiceAbortException
     *             検索に失敗
     */
    Project find(String id) throws ServiceAbortException;

    /**
     * 検索条件に一致するプロジェクトを検索する.
     * @return 検索結果。一致結果がない場合はnullを返却する。
     * @throws ServiceAbortException
     *             検索に失敗
     */
    List<Project> findForCsvDownload(SearchProjectCondition condition) throws ServiceAbortException;


    /**
     * 指定されたプロジェクト情報一覧をCSV形式に変換して返す.
     * @param list projects
     *            プロジェクト情報
     * @param csvHeaderPj ヘッダー情報
     * @param filedName フィールド情報
     * @return
     */
    byte[] generateCSV(List<Project> list) throws ServiceAbortException;

    /**
     * 指定されたプロジェクトを保存する.
     *
     * 各要素の処理結果は戻り値の {@link Project#getImportResultStatus()}
     * で確認することができる.
     *
     * @param projectList 登録対象
     * @return 処理結果を格納したリスト
     * @throws ServiceAbortException 保存失敗
     */
    List<Project> save(List<Project> projectList) throws ServiceAbortException;

    /**
     * 指定されたプロジェクトを削除する.
     *
     * 各要素の処理結果は戻り値の {@link Project#getImportResultStatus()}
     * で確認することができる.
     *
     * @param projectList 削除対象
     * @return 処理結果を格納したリスト
     * @throws ServiceAbortException 保存失敗
     */
    List<Project> delete(List<Project> projectList) throws ServiceAbortException;
}
