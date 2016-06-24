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
package jp.co.opentone.bsol.linkbinder.view.correspon.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.linkbinder.dto.AddressCorresponGroup;
import jp.co.opentone.bsol.linkbinder.dto.AddressUser;
import jp.co.opentone.bsol.linkbinder.dto.Correspon;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.view.action.control.CorresponPageElementControl;

/**
 * コレポン文書の宛先-ユーザー1件を表す表示用クラス.
 * @author opentone
 */
public class AddressUserModel implements Serializable {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -4770973325077335924L;

    /** logger. */
    private static final Logger log = LoggerFactory.getLogger(AddressUserModel.class);

    /**
     * 宛先-活動単位.
     */
    private AddressCorresponGroup addressGroup;
    /**
     * 宛先-ユーザー.
     */
    private AddressUser addressUser;
    /**
     * 従業員番号からユーザーオブジェクトを取得できるデータソース.
     */
    private Map<String, User> dsUsers;
    /**
     * 画面の表示要素を制御するオブジェクト.
     */
    private CorresponPageElementControl elemControl;
    /**
     * このオブジェクトに関連付けられた担当者のリスト.
     */
    private List<PersonInChargeModel> pics;
    /**
     * 担当者を表すDataModel.
     */
    private transient DataModel<?> personInCharges;

    /**
     * この宛先-ユーザー及び任命したPerson in Chargeが返信したコレポン文書.
     */
    private List<Correspon> replyCorrespons;

    /**
     * 選択されたPerson in Chargeの従業員番号をカンマ区切りで連結した値.
     */
    private String personInChargeValues;

    /**
     * 宛先-ユーザーを指定してインスタンスを生成する.
     *
     * @param addressUser 宛先-ユーザー
     */
    AddressUserModel(
                AddressCorresponGroup addressGroup,
                AddressUser addressUser,
                Map<String, User> dsUsers,
                CorresponPageElementControl elemControl) {
        this.addressGroup = addressGroup;
        this.addressUser = addressUser;
        this.dsUsers = dsUsers;
        this.elemControl = elemControl;
        this.pics =
            PersonInChargeModel.createPersonInChargeModelList(
                                    addressUser.getPersonInCharges());

        initPersonInChargeValues();
    }

    private void initPersonInChargeValues() {
        personInChargeValues = null;
        if (pics == null) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (PersonInChargeModel model : pics) {
            sb.append(model.getPersonInCharge().getUser().getEmpNo())
              .append(',');
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        personInChargeValues = sb.toString();
    }

    /**
     * 宛先-ユーザーを設定する.
     * @param addressUser 宛先-ユーザー
     */
    public void setAddressUser(AddressUser addressUser) {
        this.addressUser = addressUser;
    }

    /**
     * 宛先-ユーザーを取得する.
     * @return 宛先-ユーザー
     */
    public AddressUser getAddressUser() {
        return addressUser;
    }

    /**
     * 担当者を表すDataModelを設定する.
     * @param personInCharges 担当者
     */
    public void setPersonInCharges(DataModel<?> personInCharges) {
        this.personInCharges = personInCharges;
    }

    /**
     * 担当者を表すDataModelを取得する.
     * @return 担当者
     * @throws ServiceAbortException
     */
    public DataModel<?> getPersonInCharges() throws ServiceAbortException {
        if (personInCharges == null) {
            personInCharges = new ListDataModel<PersonInChargeModel>();
        }
        personInCharges.setWrappedData(
            PersonInChargeModel.createPersonInChargeModelList(personInChargeValues, dsUsers));
        return personInCharges;
    }

    /**
     * {@link AddressUser}への委譲メソッド.
     *
     * @return {@link AddressUser}の同名メソッドを参照のこと.
     */
    public User getUser() {
        return addressUser.getUser();
    }

    /**
     * {@link AddressUser}への委譲メソッド.
     *
     * @return {@link AddressUser}の同名メソッドを参照のこと.
     */
    public boolean isAttention() {
        return addressUser.isAttention();
    }

    /**
     * {@link AddressUser}への委譲メソッド.
     *
     * @return {@link AddressUser}の同名メソッドを参照のこと.
     */
    public boolean isCc() {
        return addressUser.isCc();
    }

    /**
     * @param replyCorrespons the replyCorrespons to set
     */
    public void setReplyCorrespons(List<Correspon> replyCorrespons) {
        this.replyCorrespons = replyCorrespons;
    }

    /**
     * @return the replyCorrespons
     */
    public List<Correspon> getReplyCorrespons() {
        return replyCorrespons;
    }

    /**
     * 返信状況が空の場合はtrueを返す.
     * @return 返信状況が空の場合はtrue
     */
    public boolean isReplyCorresponEmpty() {
        return replyCorrespons == null || replyCorrespons.isEmpty();
    }

    /**
     * @param personInChargeValues the personInChargeValues to set
     */
    public void setPersonInChargeValues(String personInChargeValues) {
        this.personInChargeValues = personInChargeValues;
    }

    /**
     * @return the personInChargeValues
     */
    public String getPersonInChargeValues() {
        return personInChargeValues;
    }

    /**
     * ログインユーザーが、このオブジェクトのPerson in Chargeを編集可能な場合はtrueを返す.
     * @return 編集可能な場合はtrue
     */
    public boolean isEditPersonInCharge() {
        if (log.isDebugEnabled()) {
            log.debug("editPIC attention = " + addressUser.getUser().getLabel());
        }
        return elemControl.isEditPersonInCharge(addressGroup, addressUser);
    }

    /**
     * ログインユーザーが、このオブジェクトのPerson in Chargeを編集可能な場合はtrueを返す.
     * @return 編集可能な場合はtrue
     */
    public boolean isDeletePersonInCharge() {
        if (log.isDebugEnabled()) {
            log.debug("deletePIC attention = " + addressUser.getUser().getLabel());
        }
        return elemControl.isDeletePersonInCharge(addressGroup, addressUser);
    }

    /**
     * ログインユーザーが、このオブジェクトのPerson in Chargeを編集可能な場合はtrueを返す.
     * @return 編集可能な場合はtrue
     */
    public boolean isAssignPersonInCharge() {
        if (log.isDebugEnabled()) {
            log.debug("assign to attention = " + addressUser.getUser().getLabel());
        }
        return elemControl.isAssignPersonInCharge(addressGroup, addressUser);
    }

    /**
     * 宛先-ユーザーのリストをこのクラスのリストに変換して返す.
     *
     * @param addressGroup 宛先-活動単位
     * @param users 宛先-ユーザーのリスト
     * @param dsUsers 従業員番号とユーザーのマップ
     * @param elemControl ページ構成要素制御オブジェクト
     * @return 宛先-ユーザーのリスト
     */
    public static List<AddressUserModel> createAddressUserModelList(
                    AddressCorresponGroup addressGroup,
                    List<AddressUser> users,
                    Map<String, User> dsUsers,
                    CorresponPageElementControl elemControl) {
        List<AddressUserModel> result = new ArrayList<AddressUserModel>();
        if (users == null) {
            return result;
        }

        for (AddressUser u : users) {
            result.add(new AddressUserModel(addressGroup, u, dsUsers, elemControl));
        }
        return result;
    }

}
