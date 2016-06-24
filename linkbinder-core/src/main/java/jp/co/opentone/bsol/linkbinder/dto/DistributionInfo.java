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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.core.util.JSONUtil;

/**
 * Distribution情報Dto.
 *
 * @author opentone
 *
 */
@Component
public class DistributionInfo extends DistTemplateBase {

    /**
     * serialVersionUID.
     */
    private static final long serialVersionUID = -2308769259630770498L;

    /**
     * 活動単位リストに表示する選択肢「全て」.
     */
    public static final CorresponGroup GROUP_ALL;
    static {
        GROUP_ALL = new CorresponGroup();
        GROUP_ALL.setId(0L);
        GROUP_ALL.setName("*");
    }

    /**
     * 活動単位情報.
     */
    private List<CorresponGroup> corresponGroups;

    /**
     * ユーザー情報.
     */
    private List<ProjectUser> corresponUsers;

    /**
     * 活動単位とユーザーのマッピング情報.
     */
    private List<CorresponGroupUserMapping> corresponGroupUserMappings;

    /**
     * 活動単位情報を取得します.
     * @return 活動単位情報.
     */
    public List<CorresponGroup> getCorresponGroups() {
        return corresponGroups;
    }

    /**
     * 活動単位情報を設定します.
     * @param corresponGroups 活動単位情報.
     */
    public void setCorresponGroups(List<CorresponGroup> corresponGroups) {
        this.corresponGroups = corresponGroups;
    }

    /**
     * ユーザー情報を取得します.
     * @return ユーザー情報.
     */
    public List<ProjectUser> getCorresponUsers() {
        return corresponUsers;
    }

    /**
     * ユーザー情報を設定します.
     * @param corresponUsers ユーザー情報.
     */
    public void setCorresponUsers(List<ProjectUser> corresponUsers) {
        this.corresponUsers = corresponUsers;
    }

    /**
     * 活動単位とユーザーのマッピング情報を取得します.
     * @return 活動単位とユーザーのマッピング情報.
     */
    public List<CorresponGroupUserMapping> getCorresponGroupUserMappings() {
        return corresponGroupUserMappings;
    }

    /**
     * 活動単位とユーザーのマッピング情報を設定します.
     * @param corresponGroupUserMappings 活動単位とユーザーのマッピング情報.
     */
    public void setCorresponGroupUserMappings(
            List<CorresponGroupUserMapping> corresponGroupUserMappings) {
        this.corresponGroupUserMappings = corresponGroupUserMappings;
    }


    /**
     * プロジェクト内の全ての活動単位のリストをJSON形式に変換して返す.
     * @return プロジェクト内の全ての活動単位リストのJSON形式
     * @throws ServiceAbortException {@link ServiceAbortException}
     */
    public String getGroupJSONString() throws ServiceAbortException {
        List<CorresponGroup> list = getCorresponGroups();
        if (list != null) {
            return JSONUtil.encode(convertGroupJSON(list));
        } else {
            return null;
        }
    }

    public void setGroupJSONString(String groupJSONString) {
        // 何もしない
    }

    /**
     * 活動単位リストをJSON変換用のDTOに変換する.
     * @param groups 活動単位リスト
     * @return JSON変換用活動単位
     */
    private List<CorresponGroupJSON> convertGroupJSON(List<CorresponGroup> groups) {
        List<CorresponGroupJSON> jsonGroups = new ArrayList<CorresponGroupJSON>();
        for (CorresponGroup group : groups) {
            jsonGroups.add(CorresponGroupJSON.newInstance(group));
        }
        return jsonGroups;
    }

    /**
     * プロジェクト内の全てのユーザーのリストをJSON形式に変換して返す.
     * @return プロジェクト内の全てのユーザーリストのJSON形式
     * @throws ServiceAbortException {@link ServiceAbortException}
     */
    public String getUserJSONString() throws ServiceAbortException {
        //CHECKSTYLE:OFF フィールドと同名の警告が出るがこの名前が最適
        Map<String, UserJSON> users = new HashMap<String, UserJSON>();
        //CHECKSTYLE:ON
        List<ProjectUser> list = getCorresponUsers();
        if (list != null) {
            for (ProjectUser u : list) {
                users.put(u.getUser().getEmpNo(), UserJSON.newInstance(u.getUser()));
            }
        }
        return JSONUtil.encode(users);
    }

    public void setUserJSONString(String userJSONString) {
        // 何もしない
    }

    /**
     * 活動単位とユーザーのマッピング情報をJSON形式に変換して返す.
     * @return 活動単位とユーザーのマッピング情報のJSON形式
     * @throws ServiceAbortException {@link ServiceAbortException}
     */
    public String getGroupUserMappingsJSONString() throws ServiceAbortException {
        Map<Long, List<String>> mappings = new HashMap<Long, List<String>>();
        List<CorresponGroupUserMapping> list = getCorresponGroupUserMappings();
        if (list == null) {
            return JSONUtil.encode(mappings);
        }
        for (CorresponGroupUserMapping gu : list) {
            Long id = gu.getCorresponGroupId();
            List<String> empNos = new ArrayList<String>();
            for (int i = 0; i < gu.getUsers().size(); i++) {
                User u = gu.getUsers().get(i);
                empNos.add(u.getEmpNo());
            }
//            for (User u : gu.getUsers()) {
//                empNos.add(u.getEmpNo());
//            }
            mappings.put(id, empNos);
        }
        return JSONUtil.encode(mappings);
    }

    public void setGroupUserMappingsJSONString(String groupUserMappingsJSONString) {
        // 何もしない
    }

    /**
     * 編集モードの定義をJSON形式に変換して返す.
     * @return 編集モード
     */
    public String getUpdateModeJSONString() {
        Map<String, Integer> modes = new HashMap<String, Integer>();
        for (UpdateMode mode : UpdateMode.values()) {
            modes.put(mode.toString(), mode.ordinal());
        }
        return JSONUtil.encode(modes);
    }

    public void setUpdateModeJSONString(String updateModeJSONString) {
        // 何もしない
    }

    /* (非 Javadoc)
     * @see java.lang.Object#clone()
     */
    @Override
    public DistributionInfo clone() {
        DistributionInfo clone = (DistributionInfo) super.clone();
        clone.setCorresponGroups(corresponGroups);
        clone.setCorresponUsers(corresponUsers);
        clone.setCorresponGroupUserMappings(corresponGroupUserMappings);
        return clone;
    }
}
