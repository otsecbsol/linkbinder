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

import jp.co.opentone.bsol.linkbinder.dto.code.DistributionType;

/**
 * Distributionテンプレート活動単位作成時のDtoInterface.
 *
 * @author opentone
 *
 */
public interface DistTemplateGroupCreate extends DistTemplateCommon {

    /**
     * 登録するdistTemplateHeaderIdの値を格納する.
     * @param distTemplateHeaderId DistributionテンプレートヘッダーID
     */
    void setDistTemplateHeaderId(Long distTemplateHeaderId);

    /**
     * 登録するdistTemplateHeaderIdの値を取得する.
     * @return DistributionテンプレートヘッダーID
     */
    Long getDistTemplateHeaderId();

    /**
     * 登録するdistributionTypeの値を格納する.
     * @param distributionType 宛先種別
     */
    void setDistributionType(DistributionType distributionType);

    /**
     * 登録するdistributionTypeの値を取得する.
     * @return 宛先種別
     */
    DistributionType getDistributionType();

    /**
     * 登録するorderNoの値を格納する.
     * @param orderNo 表示順
     */
    void setOrderNo(Long orderNo);

    /**
     * 登録するorderNoの値を取得する.
     * @return 表示順
     */
    Long getOrderNo();

    /**
     * 登録するgroupIdの値を格納する.
     * @param groupId 活動単位ID
     */
    void setGroupId(Long groupId);

    /**
     * 登録するgroupIdの値を取得する.
     * @return 活動単位ID
     */
    Long getGroupId();


    /**
     * ユーザーの値を格納する.
     * @param users ユーザー
     */
    void setUsers(List<DistTemplateUser> users);

    /**
     * ユーザーの値を取得する.
     * @return users ユーザー
     */
    List<DistTemplateUser> getUsers();
}
