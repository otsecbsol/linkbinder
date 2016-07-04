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


import jp.co.opentone.bsol.framework.core.dao.GenericDao;
import jp.co.opentone.bsol.linkbinder.dto.ProjectCorresponType;

/**
 * project_correspon_type を操作するDao.
 *
 * @author opentone
 *
 */
public interface ProjectCorresponTypeDao extends GenericDao<ProjectCorresponType> {

    /**
     * コレポン文書種別IDとプロジェクトIDを指定してプロジェクトコレポン文書種別の件数を取得する.
     * @param corresponTypeId コレポン文書種別ID
     * @param projectId プロジェクトID
     * @return コレポン文書種別件数
     */
    int countByCorresponTypeIdProjectId(Long corresponTypeId, String projectId);
}
