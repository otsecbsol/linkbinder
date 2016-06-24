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
 * Distributionテンプレートユーザー作成時のDtoInterface.
 *
 * @author opentone
 *
 */
public interface DistTemplateUserCreate extends DistTemplateCommon {

    /**
     * 登録するdistTemplateGroupIdの値を設定する.
     * @param distTemplateGroupId
     *            Distributionテンプレート活動単位ID
     */
    void setDistTemplateGroupId(Long distTemplateGroupId);

    /**
     * 登録するdistTemplateGroupIdの値を取得する.
     * @return Distributionテンプレート活動単位ID
     */
    Long getDistTemplateGroupId();

    /**
     * 登録するorderNoの値を設定する.
     * @param orderNo
     *            表示順
     */
    void setOrderNo(Long orderNo);

    /**
     * 登録するorderNoの値を取得する.
     * @return 表示順
     */
    Long getOrderNo();

    /**
     * 登録するempNoの値を設定する.
     * @param empNo
     *            宛先従業員番号
     */
    void setEmpNo(String empNo);

    /**
     * 登録するempNoの値を取得する.
     * @return 宛先従業員番号
     */
    String getEmpNo();
}
