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


/**
 * Distributionテンプレートヘッダー更新時のDtoInterface.
 *
 * @author opentone
 *
 */
public interface DistTemplateHeaderUpdate extends DistTemplateHeaderCreate {

    /**
     * 更新キーにするid値を設定する.
     * @param id id
     */
    void setId(Long id);

    /**
     * 更新キーにするid値を取得する.
     * @return id
     */
    Long getId();

    /**
     * 更新するversion_no値を設定する.
     * @param versionNo versionNo
     */
    void setVersionNo(Long versionNo);

    /**
     * 更新キーにするversion_no値を取得する.
     * @return version_no
     */
    Long getVersionNo();
}
