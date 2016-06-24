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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import jp.co.opentone.bsol.framework.core.util.ArgumentValidator;
import jp.co.opentone.bsol.linkbinder.dto.AddressCorresponGroup;
import jp.co.opentone.bsol.linkbinder.dto.AddressUser;
import jp.co.opentone.bsol.linkbinder.dto.Correspon;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroup;
import jp.co.opentone.bsol.linkbinder.dto.PersonInCharge;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.code.AddressType;
import jp.co.opentone.bsol.linkbinder.view.action.control.CorresponPageElementControl;

/**
 * コレポン文書の宛先-活動単位1件を表す表示用クラス.
 * @author opentone
 */
public class AddressModel implements Serializable {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -5309803130529949693L;

    /**
     * 宛先-活動単位.
     */
    private AddressCorresponGroup addressGroup;
    /**
     * このオブジェクトに関連付けられた宛先-ユーザーのリスト.
     */
    private List<AddressUserModel> addressUsers;
    /**
     * 宛先-ユーザーリストを表すDataModel.
     */
    private transient DataModel<?> users;

    /**
     * 従業員番号からユーザーオブジェクトを取得できるデータソース.
     */
//    private Map<String, User> dsUsers;

    /**
     * この宛先-グループが返信したコレポン文書.
     */
    private List<Correspon> replyCorrespons;

    /**
     * ページの各要素を制御するオブジェクト.
     */
    private CorresponPageElementControl elemControl;

    /**
     * 宛先-活動単位を指定してインスタンスを生成する.
     *
     * @param addressGroup 宛先-活動単位
     */
    AddressModel(
            AddressCorresponGroup addressGroup,
            Map<String, User> dsUsers,
            CorresponPageElementControl elemControl) {
        this.addressGroup = addressGroup;
//        this.dsUsers = dsUsers;
        this.elemControl = elemControl;
        this.addressUsers =
            AddressUserModel.createAddressUserModelList(
                                this.addressGroup,
                                this.addressGroup.getUsers(),
                                dsUsers,
                                this.elemControl);
//        this.addressUsers =
//                AddressUserModel.createAddressUserModelList(
//                                    this.addressGroup,
//                                    this.addressGroup.getUsers(),
//                                    this.dsUsers,
//                                    this.elemControl);
    }

    /**
     * この宛先-活動単位に関連付いた宛先-ユーザーを取得する.
     * 見つからない場合はnullを返す.
     * @param id 宛先-ユーザーID
     * @return 宛先-活動単位.見つからない場合はnull
     */
    public AddressUserModel getAddressUserModelById(Long id) {
        ArgumentValidator.validateNotNull(id);

        for (AddressUserModel model : addressUsers) {
            if (id.equals(model.getAddressUser().getId())) {
                return model;
            }
        }
        return null;
    }

    /**
     * 宛先-活動単位を設定する.
     * @param addressGroup 宛先-活動単位
     */
    public void setAddressGroup(AddressCorresponGroup addressGroup) {
        this.addressGroup = addressGroup;
    }

    /**
     * 宛先-活動単位を取得する.
     * @return 宛先-活動単位
     */
    public AddressCorresponGroup getAddressGroup() {
        return addressGroup;
    }

    /**
     * 宛先-ユーザーを表すDataModelを設定する.
     * @param users 宛先-ユーザー
     */
    public void setUsers(DataModel<?> users) {
        this.users = users;
    }

    /**
     * 宛先-ユーザーを表すDataModelを返す.
     * @return 宛先-ユーザー
     */
    public DataModel<?> getUsers() {
        if (users == null) {
            users = new ListDataModel<AddressUserModel>();
        }
        if (addressUsers != null) {
            users.setWrappedData(addressUsers);
        }
        return users;
    }

    /**
     * {@link AddressCorresponGroup}への委譲メソッド.
     *
     * @return {@link AddressCorresponGroup}の同名メソッドを参照のこと.
     */
    public CorresponGroup getCorresponGroup() {
        return addressGroup.getCorresponGroup();
    }

    /**
     * {@link AddressCorresponGroup}への委譲メソッド.
     *
     * @return {@link AddressCorresponGroup}の同名メソッドを参照のこと.
     */
    public Long getReplyCount() {
        return addressGroup.getReplyCount();
    }

    /**
     * {@link AddressCorresponGroup}への委譲メソッド.
     *
     * @return {@link AddressCorresponGroup}の同名メソッドを参照のこと.
     */
    public boolean isTo() {
        return addressGroup.isTo();
    }

    /**
     * {@link AddressCorresponGroup}への委譲メソッド.
     *
     * @return {@link AddressCorresponGroup}の同名メソッドを参照のこと.
     */
    public boolean isCc() {
        return addressGroup.isCc();
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
     * 宛先-活動単位のリストをこのクラスのリストに変換して返す.
     *
     * @param addressGroups 宛先-活動単位のリスト
     * @param to
     *     addressGroupsから{@link AddressType#TO}のものを抽出する場合はtrue.
     *     addressGroupsから{@link AddressType#CC}のものを抽出する場合はfalse
     * @param allUsers プロジェクトに所属する全てのユーザー
     * @param elemControl ページ構成要素制御オブジェクト
     * @return 宛先-活動単位のリスト
     */
    public static List<AddressModel> createAddressModelList(
            List<AddressCorresponGroup> addressGroups,
            boolean to,
//            List<ProjectUser> allUsers,
            CorresponPageElementControl elemControl) {
        List<AddressModel> result = new ArrayList<AddressModel>();
        if (addressGroups == null) {
            return result;
        }

//        Map<String, User> dsUser = convertProjectUserToDataSource(allUsers);
        Map<String, User> dsUser = convertUserToDataSource(addressGroups);
        for (AddressCorresponGroup g : addressGroups) {
            if (to && g.isTo()) {
                result.add(new AddressModel(g, dsUser, elemControl));
            } else if (!to && g.isCc()) {
                result.add(new AddressModel(g, dsUser, elemControl));
            }
        }
        return result;
    }

    /**
     * 宛先-活動単位のリストをこのクラスのリストに変換して返す.
     *
     * @param addressGroups 宛先-活動単位のリスト
     * @param to
     *     addressGroupsから{@link AddressType#TO}のものを抽出する場合はtrue.
     *     addressGroupsから{@link AddressType#CC}のものを抽出する場合はfalse
     * @param allUsers プロジェクトに所属する全てのユーザー
     * @param elemControl ページ構成要素制御オブジェクト
     * @return 宛先-活動単位のリスト
     */
    public static List<AddressModel> createAddressModelListWithDataSource(
                        List<AddressCorresponGroup> addressGroups,
                        boolean to,
//                        List<User> allUsers,
                        CorresponPageElementControl elemControl) {
        List<AddressModel> result = new ArrayList<AddressModel>();
        if (addressGroups == null) {
            return result;
        }

//        Map<String, User> dsUser = convertUserToDataSource(allUsers);
        Map<String, User> dsUser = convertUserToDataSource(addressGroups);
        for (AddressCorresponGroup g : addressGroups) {
            if (to && g.isTo()) {
                result.add(new AddressModel(g, dsUser, elemControl));
            } else if (!to && g.isCc()) {
                result.add(new AddressModel(g, dsUser, elemControl));
            }
        }
        return result;
    }

    private static Map<String, User> convertUserToDataSource(List<AddressCorresponGroup> addressGroups) {
        Map<String, User> users = new HashMap<String, User>();
        for (AddressCorresponGroup group : addressGroups) {
            for (AddressUser user : group.getUsers()) {
                if (user.getPersonInCharges() != null) {
                    for (PersonInCharge pic : user.getPersonInCharges()) {
                        users.put(pic.getUser().getEmpNo(), pic.getUser());
                    }
                }
            }
        }
        return users;
    }

//    private static Map<String, User> convertProjectUserToDataSource(List<ProjectUser> allUsers) {
//        Map<String, User> ds = new HashMap<String, User>();
//        for (ProjectUser u : allUsers) {
//            ds.put(u.getUser().getEmpNo(), u.getUser());
//        }
//        return ds;
//    }
//
//    private static Map<String, User> convertUserToDataSource(List<User> allUsers) {
//        Map<String, User> ds = new HashMap<String, User>();
//        for (User u : allUsers) {
//            ds.put(u.getEmpNo(), u);
//        }
//        return ds;
//    }
}
