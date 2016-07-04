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
import jp.co.opentone.bsol.linkbinder.dto.Discipline;
import jp.co.opentone.bsol.linkbinder.dto.SearchDisciplineResult;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchDisciplineCondition;

/**
 * このサービスでは部門情報に関する処理を提供する.
 * @author opentone
 */
public interface DisciplineService extends IService {

    /**
     * 指定された条件に該当する部門情報を検索し、指定ページのレコードだけを返す.
     * @param condition
     *            検索条件
     * @return 部門情報リスト
     * @throws ServiceAbortException
     *             検索エラー
     */
    SearchDisciplineResult search(SearchDisciplineCondition condition) throws ServiceAbortException;

    /**
     * 指定された部門情報一覧をExcel形式に変換して返す.
     * @param disciplines
     *            部門情報一覧
     * @return Excelデータ
     * @throws ServiceAbortException
     *             変換エラー
     */
    byte[] generateExcel(List<Discipline> disciplines) throws ServiceAbortException;

    /**
     * 指定された部門を削除する.
     * @param discipline
     *            部門情報
     * @throws ServiceAbortException
     *             削除失敗
     */
    void delete(Discipline discipline) throws ServiceAbortException;

    /**
     * 指定された部門情報を返す.
     * @param id
     *            ID
     * @return 部門情報
     * @throws ServiceAbortException
     *             検索エラー
     */
    Discipline find(Long id) throws ServiceAbortException;

    /**
     * 入力値を検証する.
     * @param discipline
     *            部門情報
     * @return true:検証OK
     * @throws ServiceAbortException
     *             検証NG
     */
    boolean validate(Discipline discipline) throws ServiceAbortException;

    /**
     * 指定された部門を保存する.
     * @param discipline
     *            部門情報
     * @return 保存された部門情報のID
     * @throws ServiceAbortException
     *             保存失敗
     */
    Long save(Discipline discipline) throws ServiceAbortException;
}
