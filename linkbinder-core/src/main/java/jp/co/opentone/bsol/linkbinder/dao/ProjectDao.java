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

import java.util.List;

import jp.co.opentone.bsol.framework.core.dao.KeyDuplicateException;
import jp.co.opentone.bsol.framework.core.dao.LegacyGenericDao;
import jp.co.opentone.bsol.framework.core.dao.RecordNotFoundException;
import jp.co.opentone.bsol.framework.core.dao.StaleRecordException;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.linkbinder.dto.Project;
import jp.co.opentone.bsol.linkbinder.dto.ProjectSummary;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchProjectCondition;

/**
 * project を操作するDao.
 *
 * @author opentone
 *
 */
public interface ProjectDao extends LegacyGenericDao<Project> {

    /**
     * プロジェクト一覧の並び順.
     *
     * @author opentone
     *
     */
    public enum OrderBy {
        /** 並び順: プロジェクトID. */
        PROJECT_ID("project_id");

        /** 並び順に対応したORDER BY句に指定する文字列.  */
        private String value;

        private OrderBy(String value) {
            this.value = value;
        }

        /**
         * ORDER BY句に指定する文字列を返す.
         * @return ORDER BY句に指定する文字列.
         */
        public String getValue() {
            return value;
        }
    }

    /**
     * 従業員番号で、ユーザーがアクセス可能なプロジェクトを検索する.
     * @param empNo
     *            従業員番号
     * @return アクセス可能なプロジェクト
     */
    List<Project> findByEmpNo(String empNo);

    /**
     * 指定された条件に該当する通常プロジェクトを検索する.
     * @param condition
     *            検索条件
     * @return 検索結果
     */
    List<Project> find(SearchProjectCondition condition);

    /**
     * 検索条件に一致するプロジェクトを検索する.
     * @param condition 検索条件
     * @return 検索結果
     */
    // FIXME: メソッド名を適切な物へ変更する。
    // CSVDownloadでのみ利用する。
    List<Project> findAll(SearchProjectCondition condition);

    /**
     * 指定された条件に該当する件数を取得する.
     * @param condition 検索条件
     * @return 件数
     */
    int count(SearchProjectCondition condition);

    /**
     * 従業員番号で、ユーザーがアクセス可能なプロジェクトサマリを検索する.
     * @param condition 検索条件
     * @return 検索結果
     */
    List<ProjectSummary> findProjectSummary(SearchProjectCondition condition);

    /**
     * 指定されたIDに一致するプロジェクトを検索する.
     * @param id 検索するプロジェクトID
     * @return 検索結果。
     * @throws RecordNotFoundException レコードが存在しなかった場合
     */
    Project findById(String id) throws RecordNotFoundException;
    /**
     * 指定されたIDに一致するシステムプロジェクトを検索する.
     * @param id 検索するプロジェクトID
     * @return 検索結果。
     * @throws RecordNotFoundException レコードが存在しなかった場合
     */
    String findBySysPJId(String id) throws RecordNotFoundException;

    /**
     * レコードを追加する
     * @param condition
     * @return
     */
    void createProject(Project project) throws KeyDuplicateException;
    /**
     * 指定されたレコードを更新する
     * @param condition
     * @return
     * @throws StaleRecordException, StaleRecordException
     */
    void updateProject(Project project) throws KeyDuplicateException, StaleRecordException, RecordNotFoundException;
    /**
     * 指定されたレコードを削除する
     * @param condition
     * @return
     * @throws StaleRecordException, StaleRecordException
     * @throws ServiceAbortException
     */
    void deleteProject(Project project) throws KeyDuplicateException, StaleRecordException, RecordNotFoundException, ServiceAbortException;

    /**
     * 登録されたレコード数を取得する.
     * @return
     */
    int count();

}
