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

import jp.co.opentone.bsol.framework.core.dao.Dao;
import jp.co.opentone.bsol.linkbinder.dto.DistTemplateHeader;
import jp.co.opentone.bsol.linkbinder.dto.DistTemplateHeaderCreate;
import jp.co.opentone.bsol.linkbinder.dto.DistTemplateHeaderDelete;
import jp.co.opentone.bsol.linkbinder.dto.DistTemplateHeaderUpdate;

/**
 * Distributionテンプレートヘッダーを操作するDao.
 *
 * @author opentone
 *
 */
public interface DistTemplateHeaderDao extends Dao {

    /**
     * 指定されたプロジェクトIDを条件にDistributionテンプレートヘッダーを取得する.
     * @param projectId プロジェクトID
     * @return Distributionテンプレートヘッダー
     */
    List<DistTemplateHeader> findByProjectId(String projectId);

    /**
     *  DistributionテンプレートヘッダーIDを条件に Distributionテンプレートヘッダーを取得する.
     * @param id  DistributionテンプレートヘッダーID
     * @return  Distributionテンプレートヘッダー
     */
    DistTemplateHeader findById(Long id);

    /**
     * レコードを1件追加する.
     * @param distTemplateHeader 追加する情報
     * @return 新規追加したレコードID値
     */
    Long create(DistTemplateHeaderCreate distTemplateHeader);

    /**
     * レコードを1件論理削除する.
     * @param distTemplateHeader 削除情報
     * @return 論理削除件数
     */
    Integer delete(DistTemplateHeaderDelete distTemplateHeader);

    /**
     * レコードを1件更新する.
     * @param distTemplateHeader 更新情報
     * @return 更新件数
     */
    Integer update(DistTemplateHeaderUpdate distTemplateHeader);

    /**
     * プロジェクトIDを条件に、Distributionテンプレートヘッダー内の使用可能な通番(template_cd)を取得する.
     * @param projectId プロジェクトID
     * @return String Distributionテンプレートヘッダー内の使用可能な通番(template_cd)
     */
    String getTemplateCodeByProject(String projectId);

    /**
     * プロジェクトID、FWBS No.を条件にDistributionテンプレートヘッダーを取得する.
     * @param projectId プロジェクトID
     * @param option1 FWBS No.
     * @return Distributionテンプレートヘッダー
     */
    List<DistTemplateHeader> findTemplateList(String projectId, String option1);

    /**
     * Distributionテンプレートヘッダーをグループとユーザーも含めて取得する.
     * @param projectId プロジェクトID
     * @param option1 FWBS No.
     * @return Distributionテンプレートヘッダー
     */
    List<DistTemplateHeader> findTemplateSelect(String projectId, String option1);
}

