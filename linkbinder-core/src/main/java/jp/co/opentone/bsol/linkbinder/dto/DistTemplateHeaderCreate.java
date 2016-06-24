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
package jp.co.opentone.bsol.linkbinder.dto;

import java.util.List;

/**
 * Distributionテンプレートヘッダー作成時のDtoInterface.
 *
 * @author opentone
 *
 */
public interface DistTemplateHeaderCreate extends DistTemplateCommon {

    /**
     * 登録するprojectIdの値を設定する.
     * @param projectId projectId
     */
    void setProjectId(String projectId);

    /**
     * 登録するprojectIdの値を取得する.
     * @return projectId
     */
    String getProjectId();

    /**
     * 登録するempNoの値を設定する.
     * @param empNo 従業員番号
     */
    void setEmpNo(String empNo);

    /**
     * 登録するempNoの値を取得する.
     * @return 従業員番号
     */
    String getEmpNo();

    /**
     * 登録するtemplateCdの値を設定する.
     * @param templateCd テンプレートコード
     */
    void setTemplateCd(String templateCd);

    /**
     * 登録するtemplateCdの値を取得する.
     * @return テンプレートコード
     */
    String getTemplateCd();

    /**
     * 登録するnameの値を設定する.
     * @param name 名称
     */
    void setName(String name);

    /**
     * 登録するnameの値を取得する.
     * @return 名称
     */
    String getName();

    /**
     * 登録するグループの値を設定する.
     * @param distrTemplateGroups グループ
     */
    void setDistTemplateGroups(List<DistTemplateGroup> distrTemplateGroups);

    /**
     * 登録するグループの値を取得する.
     * @return グループ
     */
    List<DistTemplateGroup> getDistTemplateGroups();

}
