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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import jp.co.opentone.bsol.framework.core.extension.ibatis.EnumValue;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.core.util.JSONUtil;
import jp.co.opentone.bsol.framework.web.extension.jsf.annotation.Transfer;
import jp.co.opentone.bsol.linkbinder.action.AbstractAction;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroup;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroupJSON;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroupUser;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroupUserJSON;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroupUserMapping;
import jp.co.opentone.bsol.linkbinder.dto.ProjectUser;
import jp.co.opentone.bsol.linkbinder.dto.UpdateMode;
import jp.co.opentone.bsol.linkbinder.dto.UserJSON;
import jp.co.opentone.bsol.linkbinder.dto.UserWithGroup;
import jp.co.opentone.bsol.linkbinder.dto.code.ReplyRequired;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponGroupCondition;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchUserCondition;
import jp.co.opentone.bsol.linkbinder.service.admin.CorresponGroupService;
import jp.co.opentone.bsol.linkbinder.service.admin.UserService;
import jp.co.opentone.bsol.linkbinder.view.AbstractPage;

/**
 * コレポン関連処理で共通的に利用されるデータの定義.
 * @author opentone
 */
@Component
@Scope("request")
public class CorresponDataSource extends AbstractPage {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 1540533786087811508L;

    /**
     * {@link EnumValue}型との変換を行う選択フィールドでの、未選択を表す値.
     *
     */
    public static final Integer VALUE_NOT_SELECTED = -1;

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
     * 返信要否の値のうち、返信期限を編集可能な値の定義.
     */
    public static final String[] EDITABLE_REPLY_REQUIRED_VALUES = {
        "\"" + ReplyRequired.YES.getValue() + "\""
    };

    /**
     * 活動単位サービス.
     */
    @Resource
    private CorresponGroupService corresponGroupService;
    /**
     * ユーザーサービス.
     */
    @Resource
    private UserService userService;

    /**
     * 現在のプロジェクト内に存在する全ての活動単位.
     */
    @Transfer
    private List<CorresponGroup> groups;

    /**
     * 現在のプロジェクトに所属する全てのユーザー.
     */
    @Transfer
    private List<ProjectUser> users;

    /**
     * 現在のプロジェクトに所属する全てのユーザー(活動単位).
     */
    @Transfer
    private List<CorresponGroupUser> groupUsers;

    /**
     * 活動単位とユーザーのマッピングリスト.
     */
    @Transfer
    private List<CorresponGroupUserMapping> corresponGroupUserMappings;

    /**
     * 空のインスタンスを生成する.
     */
    public CorresponDataSource() {
    }

    private List<CorresponGroup> findProjectCorresponGroups() throws ServiceAbortException {
        SearchCorresponGroupCondition condition = new SearchCorresponGroupCondition();
        condition.setProjectId(getCurrentProjectId());
        //CHECKSTYLE:OFF フィールドと同名の警告が出るがこの名前が最適
        List<CorresponGroup> groups = corresponGroupService.search(condition);
        //CHECKSTYLE:ON
        //  「全て」を追加
        groups.add(0, GROUP_ALL);

        return groups;
    }

    private List<ProjectUser> findProjectUsers() throws ServiceAbortException {
        SearchUserCondition condition = new SearchUserCondition();
        condition.setProjectId(getCurrentProjectId());
        return userService.search(condition);
    }

    private List<CorresponGroupUser> findProjectGroupUsers() throws ServiceAbortException {

        return userService.searchCorrseponGroupUsers(getCurrentProjectId());
    }


    private List<CorresponGroupUserMapping> findCorresponGroupUserMappings()
            throws ServiceAbortException {
        return corresponGroupService.findCorresponGroupIdUserMappings();
    }

    /**
     * プロジェクト内の全ての活動単位のリストをJSON形式に変換して返す.
     * @return プロジェクト内の全ての活動単位リストのJSON形式
     */
    public String getGroupJSONString() {
        List<CorresponGroup> list = getGroups();
        if (list != null) {
            return JSONUtil.encode(convertGroupJSON(list));
        } else {
            return null;
        }
    }

    /**
     * JSFの実装上必要なSetter.処理は何も行わない.
     * @param value 値
     */
    public void setGroupJSONString(String value) {
        //何もしない
    }

    /**
     * 活動単位とユーザーのマッピング情報をJSON形式に変換して返す.
     * @return 活動単位とユーザーのマッピング情報のJSON形式
     */
    public String getGroupUserMappingsJSONString() {
        Map<Long, List<String>> mappings = new HashMap<Long, List<String>>();
        List<CorresponGroupUserMapping> list = getCorresponGroupUserMappings();
        if (list == null) {
            return JSONUtil.encode(mappings);
        }
        for (CorresponGroupUserMapping gu : list) {
            Long id = gu.getCorresponGroupId();
            List<String> empNos = new ArrayList<String>();
            for (UserWithGroup u : gu.getUsers()) {
                Long realCorresponGroupId = u.getRealCorresponGroupId();
                if (realCorresponGroupId == null) {
                    realCorresponGroupId = -1L;
                }
                empNos.add(u.getEmpNo() + "-" + realCorresponGroupId);
            }
            mappings.put(id, empNos);
        }
        return JSONUtil.encode(mappings);
    }

    /**
     * JSFの実装上必要なSetter.処理は何も行わない.
     * @param value 値
     */
    public void setGroupUserMappingsJSONString(String value) {
        //  何もしない
    }

    /**
     * プロジェクト内の全てのユーザーのリストをJSON形式に変換して返す.
     * @return プロジェクト内の全てのユーザーリストのJSON形式
     */
    public String getUserJSONString() {
        //CHECKSTYLE:OFF フィールドと同名の警告が出るがこの名前が最適
        Map<String, UserJSON> users = new HashMap<String, UserJSON>();
        //CHECKSTYLE:ON
        List<ProjectUser> list = getUsers();
        if (list != null) {
            for (ProjectUser u : list) {
                users.put(u.getUser().getEmpNo(), UserJSON.newInstance(u.getUser()));
            }
        }
        return JSONUtil.encode(users);
    }

    /**
     * JSFの実装上必要なSetter.処理は何も行わない.
     * @param value 値
     */
    public void setUserJSONString(String value) {
        //  何もしない
    }

    /**
     * プロジェクト内の全てのユーザーのリストをJSON形式に変換して返す(活動単位情報を含む).
     * @return プロジェクト内の全てのユーザーリストのJSON形式
     */
    public String getGroupUserJSONString() {
        //CHECKSTYLE:OFF フィールドと同名の警告が出るがこの名前が最適
        Map<String, CorresponGroupUserJSON> groupUsers = new HashMap<String, CorresponGroupUserJSON>();
        //CHECKSTYLE:ON
        List<CorresponGroupUser> list = getGroupUsers();
        if (list != null) {
            for (CorresponGroupUser gu : list) {
                Long id = gu.getCorresponGroup().getId();
                if (id == null) {
                    id = -1L;
                    gu.getCorresponGroup().setId(id);
                }
                String key = gu.getUser().getEmpNo() + "-" + id;
                groupUsers.put(key, CorresponGroupUserJSON.newInstance(gu));
            }
        }
        return JSONUtil.encode(groupUsers);
    }

    /**
     * JSFの実装上必要なSetter.処理は何も行わない.
     * @param value 値
     */
    public void setGroupUserJSONString(String value) {
        //  何もしない
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

    /**
     * JSFの実装上必要なSetter.処理は何も行わない.
     * @param value 値
     */
    public void setUpdateModeJSONString(String value) {
        //  何もしない
    }

    /**
     * @param groups the groups to set
     */
    public void setGroups(List<CorresponGroup> groups) {
        this.groups = groups;
    }

    /**
     * @return the groups
     */
    public List<CorresponGroup> getGroups() {
        if (groups == null) {
            handler.handleAction(new LoadGroupsAction(this));
        }
        return groups;
    }

    /**
     * @param users the users to set
     */
    public void setUsers(List<ProjectUser> users) {
        this.users = users;
    }

    /**
     * @param users the users to set
     */
    public void setGroupUsers(List<CorresponGroupUser> groupUsers) {
        this.groupUsers = groupUsers;
    }


    /**
     * @return the users
     */
    public List<ProjectUser> getUsers() {
        if (users == null) {
            handler.handleAction(new LoadUsersAction(this));
        }
        return users;
    }

    /**
     * @return the users
     */
    public List<CorresponGroupUser> getGroupUsers() {
        if (groupUsers == null) {
            handler.handleAction(new LoadGroupUsersAction(this));
        }
        return groupUsers;
    }

    /**
     * @param corresponGroupUserMappings the corresponGroupUserMappings to set
     */
    public void setCorresponGroupUserMappings(
            List<CorresponGroupUserMapping> corresponGroupUserMappings) {
        this.corresponGroupUserMappings = corresponGroupUserMappings;
    }

    /**
     * @return the corresponGroupUserMappings
     */
    public List<CorresponGroupUserMapping> getCorresponGroupUserMappings() {
        if (corresponGroupUserMappings == null) {
            handler.handleAction(new LoadGroupUserMappingsAction(this));
        }
        return corresponGroupUserMappings;
    }

    /**
     * 活動単位リストをJSON変換用のDTOに変換する.
     * @param corresponGroups 活動単位リスト
     * @return JSON変換用活動単位
     */
    private List<CorresponGroupJSON> convertGroupJSON(List<CorresponGroup> corresponGroups) {
        List<CorresponGroupJSON> jsonGroups = new ArrayList<CorresponGroupJSON>();
        for (CorresponGroup group : corresponGroups) {
            jsonGroups.add(CorresponGroupJSON.newInstance(group));
        }
        return jsonGroups;
    }

    static class LoadGroupsAction extends AbstractAction {
        /**
         * serialVersionUID.
         */
        private static final long serialVersionUID = -7012936190769129372L;
        /** このアクションの処理結果を格納するオブジェクト. */
        private CorresponDataSource ds;
        /**
         * {@link CorresponDataSource}を指定してインスタンス化する.
         * @param ds {@link CorresponDataSource}のインスタンス
         */
        public LoadGroupsAction(CorresponDataSource ds) {
            super(ds);
            this.ds = ds;
        }
        /* (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            ds.setGroups(ds.findProjectCorresponGroups());
        }
    }

    static class LoadUsersAction extends AbstractAction {
        /**
         * serialVersionUID.
         */
        private static final long serialVersionUID = 3393814518405250655L;
        /** このアクションの処理結果を格納するオブジェクト. */
        private CorresponDataSource ds;
        /**
         * {@link CorresponDataSource}を指定してインスタンス化する.
         * @param ds {@link CorresponDataSource}のインスタンス
         */
        public LoadUsersAction(CorresponDataSource ds) {
            super(ds);
            this.ds = ds;
        }
        /* (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            ds.setUsers(ds.findProjectUsers());
        }
    }

    // 活動単位の情報を含んだユーザー情報を取得
    static class LoadGroupUsersAction extends AbstractAction {
        /**
         *  serialVersionUID.
         */
        private static final long serialVersionUID = -4809451045534178711L;

        /** このアクションの処理結果を格納するオブジェクト. */
        private CorresponDataSource ds;
        /**
         * {@link CorresponDataSource}を指定してインスタンス化する.
         * @param ds {@link CorresponDataSource}のインスタンス
         */
        public LoadGroupUsersAction(CorresponDataSource ds) {
            super(ds);
            this.ds = ds;
        }
        /* (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            ds.setGroupUsers(ds.findProjectGroupUsers());
        }
    }

    static class LoadGroupUserMappingsAction extends AbstractAction {
        /**
         * serialVersionUID.
         */
        private static final long serialVersionUID = -9109545280914831199L;
        /** このアクションの処理結果を格納するオブジェクト. */
        private CorresponDataSource ds;
        /**
         * {@link CorresponDataSource}を指定してインスタンス化する.
         * @param ds {@link CorresponDataSource}のインスタンス
         */
        public LoadGroupUserMappingsAction(CorresponDataSource ds) {
            super(ds);
            this.ds = ds;
        }
        /* (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            ds.setCorresponGroupUserMappings(ds.findCorresponGroupUserMappings());
        }
    }
}
