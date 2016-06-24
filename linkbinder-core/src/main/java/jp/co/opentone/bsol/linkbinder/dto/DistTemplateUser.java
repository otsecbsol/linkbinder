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

import org.springframework.stereotype.Component;

import jp.co.opentone.bsol.framework.core.dao.VersioningEntity;

/**
 * DistributionテンプレートユーザーDto.
 *
 * @author opentone
 *
 */
@Component
 public class DistTemplateUser extends DistTemplateBase
     implements DistTemplateUserCreate, DistTemplateUserDelete, VersioningEntity {
    /**
     * serialVersionUID.
     */
    private static final long serialVersionUID = -6703980946917539355L;

    /**
     * ID.
     */
    private Long id;

    /**
     * Distributionテンプレート活動単位ID.
     */
    private Long distTemplateGroupId;

    /**
     * 表示順.
     */
    private Long orderNo;

    /**
     * User.
     */
    private User user = new User();

    /**
     * コンストラクタ.
     * (default constractor).
     */
    public DistTemplateUser() {
    }

    /**
     * コンストラクタ.
     * (Advanced Search mock version).
     * @param empNo 従業員番号
     * @param nameE 従業員氏名(英語)
     */
    public DistTemplateUser(String empNo, String nameE) {
        this.setUser(new User(empNo, nameE));
    }

    /**
     * idの値を格納する.
     *
     * @param id
     *            ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * idの値を取得する.
     *
     * @return id
     *            ID
     */
    public Long getId() {
        return id;
    }

    /**
     * distTemplateGroupIdの値を格納する.
     *
     * @param distTemplateGroupId
     *            Distributionテンプレート活動単位ID
     */
    public void setDistTemplateGroupId(Long distTemplateGroupId) {
        this.distTemplateGroupId = distTemplateGroupId;
    }

    /**
     * distTemplateGroupIdの値を取得する.
     *
     * @return distTemplateGroupId
     *            Distributionテンプレート活動単位ID
     */
    public Long getDistTemplateGroupId() {
        return distTemplateGroupId;
    }

    /**
     * orderNoの値を格納する.
     *
     * @param orderNo
     *            表示順
     */
    public void setOrderNo(Long orderNo) {
        this.orderNo = orderNo;
    }

    /**
     * orderNoの値を取得する.
     *
     * @return orderNo
     *            表示順
     */
    public Long getOrderNo() {
        return orderNo;
    }

    /**
     * empNoの値を格納する.
     *
     * @param empNo
     *            宛先従業員番号
     */
    public void setEmpNo(String empNo) {
        this.user.setEmpNo(empNo);
    }

    /**
     * empNoの値を取得する.
     *
     * @return empNo
     *            宛先従業員番号
     */
    public String getEmpNo() {
        return user.getEmpNo();
    }

    /**
     * userの値を格納する.
     *
     * @param user
     *            宛先従業員
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * userの値を取得する.
     *
     * @return user
     *            宛先従業員
     */
    public User getUser() {
        return user;
    }

    /* (非 Javadoc)
     * @see java.lang.Object#clone()
     */
    @Override
    public DistTemplateUser clone() {
        DistTemplateUser clone = (DistTemplateUser) super.clone();
        clone.setId(cloneField(id));
        clone.setDistTemplateGroupId(cloneField(distTemplateGroupId));
        clone.setOrderNo(cloneField(orderNo));
        clone.setUser(user);
        return clone;
    }

    /**
     * DistTemplateUserCreateインスタンスを作成します.
     * @return vインスタンス
     */
    public static DistTemplateUserCreate newDistTemplateUserCreate() {
        return new DistTemplateUser();
    }

    /**
     * DistTemplateUserDeleteインスタンスを作成します.
     * @return DistTemplateUserDeleteインスタンス
     */
    public static DistTemplateUserDelete newDistTemplateUserDelete() {
        return new DistTemplateUser();
    }
}
