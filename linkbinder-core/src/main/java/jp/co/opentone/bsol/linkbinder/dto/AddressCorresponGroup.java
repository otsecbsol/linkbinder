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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jp.co.opentone.bsol.framework.core.dao.Entity;
import jp.co.opentone.bsol.framework.core.util.CloneUtil;
import jp.co.opentone.bsol.linkbinder.dto.UpdateMode.ModeHolder;
import jp.co.opentone.bsol.linkbinder.dto.code.AddressType;

/**
 * テーブル [v_address_correspon_group]、[v_address] の1レコードを表すDto.
 *
 * @author opentone
 *
 */
public class AddressCorresponGroup extends AbstractDto implements Entity, ModeHolder {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -155058695275418153L;

    /**
     * 編集モード.
     */
    private UpdateMode mode = UpdateMode.NONE;

    /**
     * Id.
     * <p>
     * [v_address_correspon_group.id]
     * </p>
     */
    private Long id;

    /**
     * Correspon.
     * <p>
     * [v_address_correspon_group.correspon_id]
     * </p>
     */
    private Long corresponId;

    /**
     * Correspon Group.
     */
    private CorresponGroup corresponGroup;

    /**
     * Address type.
     * <p>
     * [v_address_correspon_group.address_type]
     * </p>
     */
    private AddressType addressType;

    /**
     * Created by.
     * <p>
     * </p>
     */
    private User createdBy;

    /**
     * Created at.
     * <p>
     * [v_address_correspon_group.created_at]
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
     * [v_address_correspon_group.updated_at]
     * </p>
     */
    private Date updatedAt;

    /**
     * Delete no.
     * <p>
     * [v_address_correspon_group.delete_no]
     * </p>
     */
    private Long deleteNo;

    /**
     * 宛先に関連付けられたユーザー.
     */
    private List<AddressUser> users;

    /**
     * この宛先に設定された宛先-ユーザーが
     * 1件でも返信文書を作成している場合はtrue.
     * <p>
     * 画面の入力制御にのみ使用するプロパティであり、
     * Service層以降で使用してはいけない.
     * </p>
     */
    private boolean replyCorresponExists;
    /**
     * この宛先に設定された宛先-ユーザーに
     * 1人でもPerson in Chargeが設定されている場合はtrue.
     * <p>
     * 画面の入力制御にのみ使用するプロパティであり、
     * Service層以降で使用してはいけない.
     * </p>
     */
    private boolean personInChargeExists;

    /**
     * この宛先-グループが返信したコレポン文書の件数.
     */
    private Long replyCount;

    /**
     * 空のインスタンスを生成する.
     */
    public AddressCorresponGroup() {
    }

    /**
     * この宛先がToを表している場合はtrueを返す.
     * @return Toの場合true
     */
    public boolean isTo() {
        return addressType == AddressType.TO;
    }

    /**
     * この宛先がCcを表している場合はtrueを返す.
     * @return Ccの場合true
     */
    public boolean isCc() {
        return addressType == AddressType.CC;
    }

    /**
     * id の値を返す.
     * <p>
     * [v_address_correspon_group.id]
     * </p>
     * @return id
     */
    public Long getId() {
        return id;
    }

    /**
     * id の値を設定する.
     * <p>
     * [v_address_correspon_group.id]
     * </p>
     * @param id
     *            id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * corresponId の値を返す.
     * <p>
     * [v_address_correspon_group.correspon_id]
     * </p>
     * @return corresponId
     */
    public Long getCorresponId() {
        return corresponId;
    }

    /**
     * corresponId の値を設定する.
     * <p>
     * [v_address_correspon_group.correspon_id]
     * </p>
     * @param corresponId
     *            corresponId
     */
    public void setCorresponId(Long corresponId) {
        this.corresponId = corresponId;
    }

    /**
     * addressType の値を返す.
     * <p>
     * [v_address_correspon_group.address_type]
     * </p>
     * @return addressType
     */
    public AddressType getAddressType() {
        return addressType;
    }

    /**
     * addressType の値を設定する.
     * <p>
     * [v_address_correspon_group.address_type]
     * </p>
     * @param addressType
     *            addressType
     */
    public void setAddressType(AddressType addressType) {
        this.addressType = addressType;
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
     * [v_address_correspon_group.created_at]
     * </p>
     * @return createdAt
     */
    public Date getCreatedAt() {
        return CloneUtil.cloneDate(createdAt);
    }

    /**
     * createdAt の値を設定する.
     * <p>
     * [v_address_correspon_group.created_at]
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
     * [v_address_correspon_group.updated_at]
     * </p>
     * @return updatedAt
     */
    public Date getUpdatedAt() {
        return CloneUtil.cloneDate(updatedAt);
    }

    /**
     * updatedAt の値を設定する.
     * <p>
     * [v_address_correspon_group.updated_at]
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
     * [v_address_correspon_group.delete_no]
     * </p>
     * @return deleteNo
     */
    public Long getDeleteNo() {
        return deleteNo;
    }

    /**
     * deleteNo の値を設定する.
     * <p>
     * [v_address_correspon_group.delete_no]
     * </p>
     * @param deleteNo
     *            deleteNo
     */
    public void setDeleteNo(Long deleteNo) {
        this.deleteNo = deleteNo;
    }

    /**
     * @param corresponGroup
     *            the corresponGroup to set
     */
    public void setCorresponGroup(CorresponGroup corresponGroup) {
        this.corresponGroup = corresponGroup;
    }

    /**
     * @return the corresponGroup
     */
    public CorresponGroup getCorresponGroup() {
        return corresponGroup;
    }

    /**
     * @param users the users to set
     */
    public void setUsers(List<AddressUser> users) {
        this.users = users;
    }

    /**
     * @return the users
     */
    public List<AddressUser> getUsers() {
        return users;
    }

    /**
     * @param mode the mode to set
     */
    public void setMode(UpdateMode mode) {
        this.mode = mode;
    }

    /**
     * @return the mode
     */
    public UpdateMode getMode() {
        return mode;
    }

    /**
     * @param replyCorresponExists the replyCorresponExists to set
     */
    public void setReplyCorresponExists(boolean replyCorresponExists) {
        this.replyCorresponExists = replyCorresponExists;
    }

    /**
     * @return the replyCorresponExists
     */
    public boolean isReplyCorresponExists() {
        return replyCorresponExists;
    }

    /**
     * @param personInChargeExists the personInChargeExists to set
     */
    public void setPersonInChargeExists(boolean personInChargeExists) {
        this.personInChargeExists = personInChargeExists;
    }

    /**
     * @return the personInChargeExists
     */
    public boolean isPersonInChargeExists() {
        return personInChargeExists;
    }

    /**
     * @param replyCount the replyCount to set
     */
    public void setReplyCount(Long replyCount) {
        this.replyCount = replyCount;
    }

    /**
     * @return the replyCount
     */
    public Long getReplyCount() {
        return replyCount;
    }

    /**
     * 指定された従業員番号を持つユーザーが含まれている場合はtrueを返す.
     * データベースに問い合わせを行うわけではなく、このオブジェクトが保持するユーザーリストを元に
     * 判定を行うことに注意.
     * @param empNo 従業員番号
     * @return ユーザーが含まれる場合はtrue
     */
    public boolean containsUser(String empNo) {
        if (getUsers() == null) {
            return false;
        }
        boolean result = false;
        for (AddressUser au : getUsers()) {
            User u = au.getUser();
            if (u != null && empNo.equals(u.getEmpNo())) {
                result = true;
                break;
            }
        }
        return result;
    }

    /**
     * 指定されたユーザーを追加する.
     * @param au 宛先ユーザー
     */
    public void addUser(AddressUser au) {
        if (users == null) {
            users = new ArrayList<AddressUser>();
        }
        users.add(au);
    }

    public boolean isNew() {
        return (getId() == null || getId() == 0);
    }

}
