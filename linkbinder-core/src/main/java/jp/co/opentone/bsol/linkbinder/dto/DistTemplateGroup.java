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

import org.springframework.stereotype.Component;

import jp.co.opentone.bsol.framework.core.dao.VersioningEntity;
import jp.co.opentone.bsol.linkbinder.dto.code.DistributionType;

/**
 * Distributionテンプレート活動単位Dto.
 *
 * @author opentone
 *
 */
@Component
public class DistTemplateGroup extends DistTemplateBase
    implements DistTemplateGroupCreate,
                DistTemplateGroupUpdate,
                DistTemplateGroupDelete,
                VersioningEntity {

    /**
     * serialVersionUID.
     */
    private static final long serialVersionUID = 204896019131194172L;

    /**
     * 編集モード.
     */
    private UpdateMode mode = UpdateMode.NONE;

    /**
     * ID.
     */
    private Long id;

    /**
     * DistributionテンプレートヘッダーID.
     */
    private Long distTemplateHeaderId;

    /**
     * 宛先種別.
     */
    private DistributionType distributionType;

    /**
     * 表示順.
     */
    private Long orderNo;

    /**
     * 活動単位ID.
     */
    private Long groupId;

    /**
     * 活動単位名.
     */
    private String groupName;

    /**
     * ユーザー.
     */
    private List<DistTemplateUser> users;

    /**
     * Correspon Group.
     */
    private CorresponGroup corresponGroup;

    /**
     * コンストラクタ.
     * (Advanced Search mock version).
     */
    public DistTemplateGroup() {
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
     * distTemplateHeaderIdの値を格納する.
     *
     * @param distTemplateHeaderId
     *            DistributionテンプレートヘッダーID
     */
    public void setDistTemplateHeaderId(Long distTemplateHeaderId) {
        this.distTemplateHeaderId = distTemplateHeaderId;
    }

    /**
     * distTemplateHeaderIdの値を取得する.
     *
     * @return distTemplateHeaderId
     *            DistributionテンプレートヘッダーID
     */
    public Long getDistTemplateHeaderId() {
        return distTemplateHeaderId;
    }

    /**
     * distributionTypeの値を格納する.
     *
     * @param distributionType
     *            宛先種別
     */
    public void setDistributionType(DistributionType distributionType) {
        this.distributionType = distributionType;
    }

    /**
     * distributionTypeの値を取得する.
     *
     * @return distributionType
     *            宛先種別
     */
    public DistributionType getDistributionType() {
        return distributionType;
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
     * groupIdの値を格納する.
     *
     * @param groupId
     *            活動単位ID
     */
    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    /**
     * groupIdの値を取得する.
     *
     * @return groupId
     *            活動単位ID
     */
    public Long getGroupId() {
        return groupId;
    }

    /**
     * groupNameの値を格納する.
     *
     * @param groupName
     *            活動単位名
     */
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    /**
     * groupNameの値を取得する.
     *
     * @return groupName
     *            活動単位名
     */
    public String getGroupName() {
        return groupName;
    }

    /**
     * ユーザーの値を格納する.
     *
     * @param users
     *            ユーザー
     */
    public void setUsers(List<DistTemplateUser> users) {
        this.users = users;
    }

    /**
     * ユーザーの値を取得する.
     *
     * @return users
     *            ユーザー
     */
    public List<DistTemplateUser> getUsers() {
        return users;
    }

    /**
     * corresponGroupの値を格納する.
     *
     * @param corresponGroup
     *            活動単位名
     */
    public void setCorresponGroup(CorresponGroup corresponGroup) {
        this.corresponGroup = corresponGroup;
    }

    /**
     * corresponGroupの値を取得する.
     *
     * @return corresponGroup
     *            活動単位
     */
    public CorresponGroup getCorresponGroup() {
        return corresponGroup;
    }

    /**
     * DistTemplateGroupCreateインスタンスを作成します.
     * @return DistTemplateGroupCreateインスタンス
     */
    public static DistTemplateGroupCreate newDistTemplateGroupCreate() {
        DistTemplateGroup group = new DistTemplateGroup();
        group.setMode(UpdateMode.NEW);
        return group;
    }

    /**
     * DistTemplateGroupUpdateインスタンスを作成します.
     * @return DistTemplateGroupUpdateインスタンス
     */
    public static DistTemplateGroupUpdate newDistTemplateGroupUpdate() {
        DistTemplateGroup group = new DistTemplateGroup();
        group.setMode(UpdateMode.UPDATE);
        return group;
    }

    /**
     * DistTemplateHeaderDeleteインスタンスを作成します.
     * @return DistTemplateHeaderDeleteインスタンス
     */
    public static DistTemplateGroupDelete newDistTemplateGroupDelete() {
        DistTemplateGroup group = new DistTemplateGroup();
        group.setMode(UpdateMode.DELETE);
        return group;
    }

    /*
     * (非 Javadoc)
     * @see jp.co.opentone.bsol.mer.dto.MerDtoBase#clone()
     */
    @Override
    public DistTemplateGroup clone() {
        DistTemplateGroup clone = (DistTemplateGroup) super.clone();
        clone.setMode(mode);
        clone.setId(cloneField(id));
        clone.setDistTemplateHeaderId(distTemplateHeaderId);
        clone.setDistributionType(distributionType);
        clone.setOrderNo(cloneField(orderNo));
        clone.setGroupId(cloneField(groupId));
        clone.setGroupName(groupName);
        clone.setUsers(cloneList(users));
        clone.setCorresponGroup(corresponGroup);
        return clone;
    }
}
