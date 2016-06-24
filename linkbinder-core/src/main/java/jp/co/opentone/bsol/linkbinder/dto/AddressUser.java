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

import java.util.Date;
import java.util.List;

import jp.co.opentone.bsol.framework.core.dao.Entity;
import jp.co.opentone.bsol.framework.core.util.CloneUtil;
import jp.co.opentone.bsol.linkbinder.dto.code.AddressUserType;

/**
 * テーブル [v_address_user] の1レコードを表すDto.
 *
 * @author opentone
 *
 */
public class AddressUser extends AbstractDto implements Entity {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -8030787243555417843L;

    /**
     * Id.
     * <p>
     * [v_address_user.id]
     * </p>
     */
    private Long id;

    /**
     * Correspon.
     * <p>
     * [v_address_user.addressCorresponGroupId]
     * </p>
     */
    private Long addressCorresponGroupId;

    /**
     * User.
     */
    private User user;

    /**
     * Address User type.
     * <p>
     * [v_address_user.address_user_type]
     * </p>
     */
    private AddressUserType addressUserType;

    /**
     * Created by.
     * <p>
     * </p>
     */
    private User createdBy;

    /**
     * Created at.
     * <p>
     * [v_address_user.created_at]
     * </p>
     */
    private Date createdAt;

    /**
     * Updated by.
     * <p>
     * </p>
     */
    private User updatedBy;

    /**
     * Updated at.
     * <p>
     * [v_address_user.updated_at]
     * </p>
     */
    private Date updatedAt;

    /**
     * Delete no.
     * <p>
     * [v_address_user.delete_no]
     * </p>
     */
    private Long deleteNo;

    /**
     * この宛先-ユーザーが任命したPerson in Charge.
     */
    private List<PersonInCharge> personInCharges;

    /**
     * 空のインスタンスを生成する.
     */
    public AddressUser() {
    }

    /**
     * この宛先-ユーザーがAttentionを表している場合はtrueを返す.
     * @return Attentionの場合true
     */
    public boolean isAttention() {
        return addressUserType == AddressUserType.ATTENTION;
    }

    /**
     * この宛先がCcを表している場合はtrueを返す.
     * @return Ccの場合true
     */
    public boolean isCc() {
        return addressUserType == AddressUserType.NORMAL_USER;
    }

    /**
     * id の値を返す.
     * <p>
     * [v_address_user.id]
     * </p>
     * @return id
     */
    public Long getId() {
        return id;
    }

    /**
     * id の値を設定する.
     * <p>
     * [v_address_user.id]
     * </p>
     * @param id
     *            id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * addressCorresponGroupId の値を返す.
     * <p>
     * [v_address_user.address_correspon_group_id]
     * </p>
     * @return addressCorresponGroupId
     */
    public Long getAddressCorresponGroupId() {
        return addressCorresponGroupId;
    }

    /**
     * addressCorresponGroupId の値を設定する.
     * <p>
     * [v_address_user.address_correspon_group_id]
     * </p>
     * @param addressCorresponGroupId
     *            addressCorresponGroupId
     */
    public void setAddressCorresponGroupId(Long addressCorresponGroupId) {
        this.addressCorresponGroupId = addressCorresponGroupId;
    }

    /**
     * addressUserType の値を返す.
     * <p>
     * [v_address_user.address_user_type]
     * </p>
     * @return addressUserType
     */
    public AddressUserType getAddressUserType() {
        return addressUserType;
    }

    /**
     * addressUserType の値を設定する.
     * <p>
     * [v_address_user.address_user_type]
     * </p>
     * @param addressUserType
     *            addressUserType
     */
    public void setAddressUserType(AddressUserType addressUserType) {
        this.addressUserType = addressUserType;
    }

    /**
     * 作成者を返す.
     * <p>
     * </p>
     * @return createdBy
     */
    public User getCreatedBy() {
        return createdBy;
    }

    /**
     * 作成者を設定する.
     * <p>
     * </p>
     * @param createdBy
     *            作成者
     */
    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * createdAt の値を返す.
     * <p>
     * [v_address_user.created_at]
     * </p>
     * @return createdAt
     */
    public Date getCreatedAt() {
        return CloneUtil.cloneDate(createdAt);
    }

    /**
     * createdAt の値を設定する.
     * <p>
     * [v_address_user.created_at]
     * </p>
     * @param createdAt
     *            createdAt
     */
    public void setCreatedAt(Date createdAt) {
        this.createdAt = CloneUtil.cloneDate(createdAt);
    }

    /**
     * 更新者を返す.
     * <p>
     * </p>
     * @return updatedBy
     */
    public User getUpdatedBy() {
        return updatedBy;
    }

    /**
     * 更新者を設定する.
     * <p>
     * </p>
     * @param updatedBy
     *            更新者
     */
    public void setUpdatedBy(User updatedBy) {
        this.updatedBy = updatedBy;
    }

    /**
     * updatedAt の値を返す.
     * <p>
     * [v_address_user.updated_at]
     * </p>
     * @return updatedAt
     */
    public Date getUpdatedAt() {
        return CloneUtil.cloneDate(updatedAt);
    }

    /**
     * updatedAt の値を設定する.
     * <p>
     * [v_address_user.updated_at]
     * </p>
     * @param updatedAt
     *            updatedAt
     */
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = CloneUtil.cloneDate(updatedAt);
    }

    /**
     * deleteNo の値を返す.
     * <p>
     * [v_address_user.delete_no]
     * </p>
     * @return deleteNo
     */
    public Long getDeleteNo() {
        return deleteNo;
    }

    /**
     * deleteNo の値を設定する.
     * <p>
     * [v_address_user.delete_no]
     * </p>
     * @param deleteNo
     *            deleteNo
     */
    public void setDeleteNo(Long deleteNo) {
        this.deleteNo = deleteNo;
    }

    /**
     * @param user
     *            the user to set
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * @return the user
     */
    public User getUser() {
        return user;
    }

    /**
     * @param personInCharges the personInCharges to set
     */
    public void setPersonInCharges(List<PersonInCharge> personInCharges) {
        this.personInCharges = personInCharges;
    }

    /**
     * @return the personInCharges
     */
    public List<PersonInCharge> getPersonInCharges() {
        return personInCharges;
    }
}
