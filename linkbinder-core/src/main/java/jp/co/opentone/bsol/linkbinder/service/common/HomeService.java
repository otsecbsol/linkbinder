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
import jp.co.opentone.bsol.linkbinder.dto.ProjectDetailsSummary;
import jp.co.opentone.bsol.linkbinder.dto.ProjectSummary;

/**
 * ホーム画面に関するサービスを提供する.
 * @author opentone
 */
public interface HomeService extends IService {

    /**
     * ログインユーザーが利用可能なプロジェクトの一覧を返す.
     * @return プロジェクトサマリ
     * @throws ServiceAbortException 検索エラー
     */
    List<ProjectSummary> findProjects() throws ServiceAbortException;


    /**
     * 指定されたプロジェクトのサマリ情報を返す.
     * @param projectId プロジェクトID
     * @return プロジェクト詳細サマリ
     * @throws ServiceAbortException 検索エラー
     */
    ProjectDetailsSummary findProjectDetails(String projectId) throws ServiceAbortException;

    /**
     * 指定されたプロジェクトのサマリ情報を返す.
     * @param projectId プロジェクトID
     * @param usePersonInCharge PersonInCharge使用可否
     * @return プロジェクト詳細サマリ
     * @throws ServiceAbortException 検索エラー
     */
    ProjectDetailsSummary findProjectDetails(String projectId, boolean usePersonInCharge)
            throws ServiceAbortException;

    /**
     * 学習用コンテンツエリアのタイトルを取得して返す.
     * @return 学習用コンテンツエリアタイトル
     */
    String getLearningContentsTitle();
}
