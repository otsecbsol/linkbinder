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
package jp.co.opentone.bsol.linkbinder.view.admin;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.ManagedBean;
import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.constraints.Length;
import org.springframework.context.annotation.Scope;

import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.core.util.JSONUtil;
import jp.co.opentone.bsol.framework.core.validation.constraints.Required;
import jp.co.opentone.bsol.framework.web.extension.jsf.annotation.Initialize;
import jp.co.opentone.bsol.framework.web.extension.jsf.annotation.Transfer;
import jp.co.opentone.bsol.linkbinder.action.AbstractAction;
import jp.co.opentone.bsol.linkbinder.dto.AddressCorresponGroup;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroup;
import jp.co.opentone.bsol.linkbinder.dto.DistTemplateGroup;
import jp.co.opentone.bsol.linkbinder.dto.DistTemplateHeader;
import jp.co.opentone.bsol.linkbinder.dto.DistTemplateUser;
import jp.co.opentone.bsol.linkbinder.dto.DistributionInfo;
import jp.co.opentone.bsol.linkbinder.dto.UpdateMode;
import jp.co.opentone.bsol.linkbinder.dto.code.DistributionType;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.service.admin.DistributionTemplateService;
import jp.co.opentone.bsol.linkbinder.view.AbstractPage;
import net.arnx.jsonic.JSON;

/**
 * Distribution Template編集Page.
 * @author opentone
 */
@ManagedBean
@Scope("view")
public class DistributionTemplateEditPage extends AbstractPage {

    /**
     * serialVersionUID.
     */
    private static final long serialVersionUID = -2095088984708121069L;

    /** パラメータ名：id. */
    public static final String PARAM_NAME_ID = "id";
    /** パラメータ名：copyId. */
    public static final String PARAM_NAME_COPY_ID = "copyId";
    /** パラメータ値：id指定無し. */
    public static final String VALUE_ID_EMPTY = "0";
    /** パラメータ名：projectId. */
    public static final String PARAM_NAME_PJ_ID = "projectId";
    /** パラメータ名：msgId. */
    public static final String PARAM_NAME_MSG_ID = "msgId";
    /** 新規作成時のID. */
    public static final Long NEW_CREATE_ID = Long.valueOf(0);
    /** 不正なID. */
    public static final Long IRREGULAR_ID = Long.MIN_VALUE;
    /** Flashオブジェクトキー名. */
    public static final String FLASH_KEY_MESSAGE = "FLASH_KEY_MESSAGE";
    /** Request属性名. */
    public static final String REQUEST_ATTR_MESSAGE = "REQUEST_ATTR_MESSAGE";
    /** コピーされたことを表すラベル. */
    public static final String LABEL_COPY = "(Copy)";
    /** 新規登録時タイトル. */
    public static final String TITLE_NEW = "宛先テンプレート新規登録";
    /** 編集登録時タイトル. */
    private static final String TITLE_UPDATE = "宛先テンプレート更新";
    /**
     * JSON形式の宛先をデコードするための型情報.
     */
    private static final Type MAPPING_TYPE;
    static {
        try {
            MAPPING_TYPE = DistributionTemplateEditPage.class
                                .getDeclaredField("m").getGenericType();
        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    /**
    * Distributionテンプレートの画面処理ステータス.
    */
   public enum DistributionTemplateState {
       /** 新規登録モード. */
       REGIST,
       /** 編集モード. */
       EDIT,
       /** コピーモード. */
       COPY
   }

    /**
     * 宛先テンプレートサービス.
     */
    @Resource
    private DistributionTemplateService service;

    /**
     * 初期画面表示判定値.
     */
    @Transfer
    private boolean initialDisplaySuccess = false;

    /**
     * JSON形式の宛先をデコードするための型情報を取得するためのダミーフィールド.
     * <p>
     * staticイニシャライザ内で名前を参照されるだけで、処理には一切使用されない.
     * </p>
     */
    @SuppressWarnings("unused")
    private List<DistTemplateGroup> m;

    /**
     * Toに設定された値のJSON形式の文字列.
     * この値は{@code List<AddressCorresponGroup>}に変換可能.
     *
     * @see AddressCorresponGroup
     */
    @Transfer
    private String toAddressValues;

    /**
     * Ccに設定された値のJSON形式の文字列.
     * この値は{@code List<AddressCorresponGroup>}に変換可能.
     *
     * @see AddressCorresponGroup
     */
    @Transfer
    private String ccAddressValues;

    /**
     * Toから削除された値のJSON形式の文字列.
     * この値は{@code List<AddressCorresponGroup>}に変換可能.
     *
     * @see AddressCorresponGroup
     */
    @Transfer
    private String toRemovedAddressValues;

    /**
     * Ccから削除された値のJSON形式の文字列.
     * この値は{@code List<AddressCorresponGroup>}に変換可能.
     *
     * @see AddressCorresponGroup
     */
    @Transfer
    private String ccRemovedAddressValues;

    /**
     * プロジェクト内の全ての活動単位のリストのJSON形式.
     */
    @Transfer
    private String groupJSONString;

    /**
     * プロジェクト内の全てのユーザーのリストのJSON形式.
     */
    @Transfer
    private String userJSONString;
    /**
     * 活動単位とユーザーのマッピング情報のJSON形式.
     */
    @Transfer
    private String groupUserMappingsJSONString;

    /**
     * 編集モードの定義情報のJSON形式.
     */
    @Transfer
    private String updateModeJSONString;

    /**
     * 宛先テンプレートID.
     * <p>
     * 起動元画面からのクエリパラメータが自動設定される.
     * </p>
     */
    @Transfer
    private Long id;

    /**
     * コピー元のDistiribution Header ID.
     */
    @Transfer
    private Long copyId;

    /**
     * 表示タイトル.
     */
    @Transfer
    private String title;

    /**
     * テンプレート名.
     */
    @Transfer
    @Required
    //CHECKSTYLE:OFF
    @Length(max = 100)
    //CHECKSTYLE:ON
    private String name;

    /**
     * 画面を初期化する.
     * <p>
     * 他画面から遷移した時に起動される. ページの初期化に必要な情報を準備し、ユーザーがアクセス可能な状態であるか検証する.
     * </p>
     */
    @Initialize
    public void initialize() {
        handler.handleAction(new InitializeAction(this));
    }

    public Type getMappingType() {
        return MAPPING_TYPE;
    }

    /**
     * 1件のDistributionテンプレート情報.
     */
    @Transfer
    private DistTemplateHeader distTemplateHeader;

    /**
     * Distributionテンプレートヘッダー情報を取得する.
     * @return Distributionテンプレートヘッダー情報
     */
    public DistTemplateHeader getDistTemplateHeader() {
        return distTemplateHeader;
    }

    /**
     * Distributionテンプレートヘッダー情報を設定する.
     * @param distTemplateHeader Distributionテンプレートヘッダー情報
     */
    public void setDistTemplateHeader(DistTemplateHeader distTemplateHeader) {
        this.distTemplateHeader = distTemplateHeader;
    }

    /**
     * @param toAddressValues the toAddressValues to set
     */
    public void setToAddressValues(String toAddressValues) {
        this.toAddressValues = toAddressValues;
    }

    /**
     * @return the toAddressValues
     */
    public String getToAddressValues() {
        return toAddressValues;
    }

    /**
     * @param ccAddressValues the ccAddressValues to set
     */
    public void setCcAddressValues(String ccAddressValues) {
        this.ccAddressValues = ccAddressValues;
    }

    /**
     * @return the ccAddressValues
     */
    public String getCcAddressValues() {
        return ccAddressValues;
    }

    /**
     * @param toRemovedAddressValues the toRemovedAddressValues to set
     */
    public void setToRemovedAddressValues(String toRemovedAddressValues) {
        this.toRemovedAddressValues = toRemovedAddressValues;
    }

    /**
     * @return the toRemovedAddressValues
     */
    public String getToRemovedAddressValues() {
        return toRemovedAddressValues;
    }

    /**
     * @param ccRemovedAddressValues the ccRemovedAddressValues to set
     */
    public void setCcRemovedAddressValues(String ccRemovedAddressValues) {
        this.ccRemovedAddressValues = ccRemovedAddressValues;
    }

    /**
     * @return the ccRemovedAddressValues
     */
    public String getCcRemovedAddressValues() {
        return ccRemovedAddressValues;
    }

    /**
     * @param groupJSONString groupJSONString
     */
    public void setGroupJSONString(String groupJSONString) {
        this.groupJSONString = groupJSONString;
    }

    /**
     * @return the ccRemovedAddressValues
     */
    public String getGroupJSONString() {
        return groupJSONString;
    }

    /**
     * @param groupUserMappingsJSONString groupUserMappingsJSONString
     */
    public void setGroupUserMappingsJSONString(String groupUserMappingsJSONString) {
        this.groupUserMappingsJSONString = groupUserMappingsJSONString;
    }

    /**
     * @return the ccRemovedAddressValues
     */
    public String getGroupUserMappingsJSONString() {
        return groupUserMappingsJSONString;
    }

    /**
     * @param updateModeJSONString updateModeJSONString
     */
    public void setUpdateModeJSONString(String updateModeJSONString) {
        this.updateModeJSONString = updateModeJSONString;
    }

    /**
     * @return the ccRemovedAddressValues
     */
    public String getUpdateModeJSONString() {
        return updateModeJSONString;
    }

    /**
     * コピー元のDistiribution Header IDを設定します.
     * @param copyId CopyId
     */
    public void setCopyId(Long copyId) {
        this.copyId = copyId;
    }

    /**
     * コピー元のDistiribution Header IDを取得します.
     * @return copyId
     */
    public Long getCopyId() {
        return copyId;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @param userJSONString userJSONString
     */
    public void setUserJSONString(String userJSONString) {
        this.userJSONString = userJSONString;
    }

    /**
     * @return the userJSONString
     */
    public String getUserJSONString() {
        return userJSONString;
    }

    /**
     * コピーリンクを表示するか否かを返す.
     * Ture：表示する
     * False：表示しない
     * @return boolean
     */
    public boolean getIsViewCopyLink() {
        boolean result = false;
        Long dthId = distTemplateHeader.getId();
        if (dthId != null && dthId > 0) {
            result = true;
        }
        return result;
    }

    /**
     * 現在の画面の処理モードを返却する.
     * @return 処理ステータス
     */
    public DistributionTemplateState getViewState() {
        DistributionTemplateState state;
        if (NEW_CREATE_ID.equals(id)
                && (copyId == null || copyId <= 0)) {
            // 新規登録
            state = DistributionTemplateState.REGIST;
            title = TITLE_NEW;
        } else if (copyId > 0) {
            // コピー
            state = DistributionTemplateState.COPY;
            title = TITLE_UPDATE;
        } else {
            // 更新
            state = DistributionTemplateState.EDIT;
            title = TITLE_UPDATE;
        }

        return state;
    }

    public String save() {
        handler.handleAction(new SaveAction(this));
        return null;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * 入力された宛先情報を宛先オブジェクトに復元して返す.
     * @param to Toの場合true
     * @return 復元後のオブジェクト
     */
    private List<DistTemplateGroup> decodeAddress(boolean to) {
        String value = to ? getToAddressValues() : getCcAddressValues();
        return decodeAddressCorresponGroup(value, to);
    }

    /**
     * 削除された宛先情報を宛先オブジェクトに復元して返す.
     * @param to Toの場合true
     * @return 復元後のオブジェクト
     */
    private List<DistTemplateGroup> decodeRemovedAddress(boolean to) {
        String value = to ? getToRemovedAddressValues() : getCcRemovedAddressValues();
        return decodeAddressCorresponGroup(value, to);
    }

    @SuppressWarnings("unchecked")
    private List<DistTemplateGroup> decodeAddressCorresponGroup(String value, boolean to) {
        if (StringUtils.isEmpty(value)) {
            return new ArrayList<DistTemplateGroup>();
        }

        List<DistTemplateGroup> addresses =
            (List<DistTemplateGroup>) JSON.decode(value, getMappingType());
        Long dgOrderNo = 0L;
        for (DistTemplateGroup dg : addresses) {
            dg.setDistributionType(to ? DistributionType.TO : DistributionType.CC);
            dg.setOrderNo(dgOrderNo++);
            List<DistTemplateUser> users = dg.getUsers();
            if (users == null) {
                continue;
            }
            Long duOrderNo = 0L;
            for (DistTemplateUser du : users) {
                du.setDistTemplateGroupId(dg.getId());
                du.setOrderNo(duOrderNo++);
            }
        }
        return addresses;
    }

    /**
     * 現在の画面処理モードを取得する.
     * @return 画面処理モード.
     */
    public DistributionTemplateState getState() {
        // 現在の画面処理モード
        DistributionTemplateState state = getViewState();
        return state;
    }
    /**
     * initialDisplaySuccessを設定する.
     * @param initialDisplaySuccess the initialDisplaySuccess to set
     */
    public void setInitialDisplaySuccess(boolean initialDisplaySuccess) {
        this.initialDisplaySuccess = initialDisplaySuccess;
    }

    /**
     * initialDisplaySuccessを取得する.
     * @return the initialDisplaySuccess
     */
    public boolean isInitialDisplaySuccess() {
        return initialDisplaySuccess;
    }
    /**
     * 画面初期化アクション.
     * @author opentone
     */
    static class InitializeAction extends AbstractAction {
        /**
         * serialVersionUID.
         */
        private static final long serialVersionUID = -1535014384956420021L;
        /** アクション発生元ページ. */
        private DistributionTemplateEditPage page;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page
         *            ページ
         */
        public InitializeAction(DistributionTemplateEditPage page) {
            super(page);
            this.page = page;
        }

        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            page.checkProjectSelected();
            checkPermission();

            // 現在の画面処理モード
            DistributionTemplateState state = page.getState();
            if (state == DistributionTemplateState.REGIST) {
                executeRegist();
            } else if (state == DistributionTemplateState.EDIT) {
                executeEdit();
            } else if (state == DistributionTemplateState.COPY) {
                executeCopy();
            }
            setProjectAddress();
            // 正気表示処理成功
            // Distributionテンプレートが保持する宛先情報を表示変換
            applyAddressValues();
            // プロジェクトに紐付くグループ、ユーザーを取得
            page.setInitialDisplaySuccess(true);
        }

        /**
         * 新規作成時の処理を行う.
         * @throws ServiceAbortException 処理エラー.
         */
        public void executeRegist() throws ServiceAbortException {
            DistTemplateHeader header = new DistTemplateHeader();
            header.setProjectId(page.getCurrentProjectId());
            // DistributionInfoオブジェクトの作成
            DistributionInfo info = page.service.findDistributionInfo(page.getCurrentProjectId());
            header.setDistributionInfo(info);
            page.setDistTemplateHeader(header);
        }

        /**
         * 編集時の処理を行う.
         * @throws ServiceAbortException 処理エラー.
         */
        public void executeEdit() throws ServiceAbortException {
            DistTemplateHeader header = page.service.find(page.getId());
            page.setDistTemplateHeader(header);
            page.setName(header.getName());
        }

        /**
         * コピー作成時の処理を行う.
         * @throws ServiceAbortException 処理エラー.
         */
        public void executeCopy() throws ServiceAbortException {
            page.setDistTemplateHeader(page.service.find(page.copyId));
            // コピー独自処理
            DistTemplateHeader distHeader = page.getDistTemplateHeader();
            // [ID]を初期化
            distHeader.setId(null);
            distHeader.setTemplateCd(null);
            // [Name]に「(Copy)」を連結する
            page.setName(new StringBuilder(distHeader.getName()).append(LABEL_COPY).toString());
        }

        /**
         * 権限チェックを行う.
         * @throws ServiceAbortException 権限エラー
         */
        private void checkPermission() throws ServiceAbortException {
            if (!page.isSystemAdmin()
                    && !page.isProjectAdmin(page.getCurrentProjectId())) {
                throw new ServiceAbortException(
                    ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT);
            }
        }
        /**
         * Distributionテンプレートが保持する宛先情報をページ表示形式に変換して設定する.
         */
        private void applyAddressValues()
            throws ServiceAbortException {
            DistTemplateHeader distTemplateHeader = page.getDistTemplateHeader();
            List<DistTemplateGroup> toGroups = distTemplateHeader.getToDistTemplateGroups();
            List<DistTemplateGroup> ccGroups = distTemplateHeader.getCcDistTemplateGroups();

            //  宛先情報設定
            page.setToAddressValues(JSONUtil.encode(toGroups));
            page.setCcAddressValues(JSONUtil.encode(ccGroups));
        }

        /**
         * Distributionテンプレートが保持する宛先情報をページ表示形式に変換して設定する.
         */
        private void setProjectAddress() throws ServiceAbortException {
            DistributionInfo distributionInfo
                = page.getDistTemplateHeader().getDistributionInfo();
            // 活動単位が1件も無い場合はエラーとする.
            List<CorresponGroup> groups = distributionInfo.getCorresponGroups();
            if (null == groups || 0 == groups.size()) {
                throw new ServiceAbortException(ApplicationMessageCode.MSG_NOT_IN_CORRESPON_GROUPS);
            }
            // プロジェクト内の全ての活動単位のリストをJSON形式に変換.
            page.setGroupJSONString(distributionInfo.getGroupJSONString());
            // プロジェクト内の全てのユーザーのリストをJSON形式に変換.
            page.setUserJSONString(distributionInfo.getUserJSONString());
            // 活動単位とユーザーのマッピング情報をJSON形式に変換.
            page.setGroupUserMappingsJSONString(
                distributionInfo.getGroupUserMappingsJSONString());
            // 編集モードの定義をJSON形式に変換.
            page.setUpdateModeJSONString(
                distributionInfo.getUpdateModeJSONString());
        }
    }

    /**
     * 保存アクション.
     * @author opentone
     */
    static class SaveAction extends AbstractAction {
        /**
         * serialVersionUID.
         */
        private static final long serialVersionUID = 495759833176416372L;
        /** アクション発生元ページ. */
        private DistributionTemplateEditPage page;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page
         *            ページ
         */
        public SaveAction(DistributionTemplateEditPage page) {
            super(page);
            this.page = page;
        }

        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            // 検証
            validate();
            // 保存
            DistributionTemplateState state = page.getState();
            if (DistributionTemplateState.REGIST == state
                    || DistributionTemplateState.COPY == state) {
                create();
            } else if (DistributionTemplateState.EDIT == state) {
                update();
            }
            page.setPageMessage(ApplicationMessageCode.DISTRIBUTION_TEMPLATE_SAVED);
            // ページデータを取得し直す
            refresh();
            page.setInitialDisplaySuccess(true);
        }

        /**
         * 新規登録処理を行う.
         * @throws ServiceAbortException 処理エラーが発生.
         */
        private void create() throws ServiceAbortException {
            DistTemplateHeader header = new DistTemplateHeader();
            header.setProjectId(page.getCurrentProjectId());
            header.setName(page.getName());
            header.setDistTemplateGroups(createDistTemplateGroup());
            // Mode:Copyの場合はUpdateModeを変更する.
            if (DistributionTemplateState.COPY == page.getState()) {
                List<DistTemplateGroup> list = header.getDistTemplateGroups();
                Set<DistTemplateGroup> deletedGroups = new HashSet<DistTemplateGroup>();
                if (null != list) {
                    for (DistTemplateGroup group : list) {
                        // Copy後ユーザーが削除したGroupは含めてはいけない
                        if (group.getMode() == UpdateMode.DELETE) {
                            deletedGroups.add(group);
                        } else {
                            group.setMode(UpdateMode.NEW);
                        }
                    }
                    for (DistTemplateGroup group : deletedGroups) {
                        list.remove(group);
                    }
                }
            }
            // 保存処理
            header = page.service.save(header, page.getCurrentUser().getEmpNo());
            page.setId(header.getId());
        }

        /**
         * 更新登録処理を行う.
         * @throws ServiceAbortException 処理エラーが発生.
         */
        private void update() throws ServiceAbortException {
            DistTemplateHeader header = page.getDistTemplateHeader().clone();
            header.setName(page.getName());
            header.setDistTemplateGroups(createDistTemplateGroup());
            // 保存処理
            header = page.service.save(header, page.getCurrentUser().getEmpNo());
        }

        /**
         * 画面上の表示データを更新する.
         * @throws ServiceAbortException 処理エラー
         */
        private void refresh() throws ServiceAbortException {
            DistTemplateHeader header = page.service.find(page.getId());
            page.setDistTemplateHeader(header);

            List<DistTemplateGroup> toGroups = header.getToDistTemplateGroups();
            List<DistTemplateGroup> ccGroups = header.getCcDistTemplateGroups();
            //  宛先情報設定
            page.setToAddressValues(JSONUtil.encode(toGroups));
            page.setCcAddressValues(JSONUtil.encode(ccGroups));

            // 削除されたグループ情報をクリア
            List<DistTemplateGroup> empty = new ArrayList<DistTemplateGroup>();
            page.setToRemovedAddressValues(JSONUtil.encode(empty));
            page.setCcRemovedAddressValues(JSONUtil.encode(empty));

            // copy元IDをクリア
            page.setCopyId(0L);
        }

        /**
         * DistTemplateGroupリストを作成する.
         * @return DistTemplateGroupリスト
         */
        @SuppressWarnings("unchecked")
        private List<DistTemplateGroup> createDistTemplateGroup() {
            List<DistTemplateGroup> groups = new ArrayList<DistTemplateGroup>();
            Object[] groupList = {
                page.decodeAddress(true),
                page.decodeAddress(false),
                page.decodeRemovedAddress(true),
                page.decodeRemovedAddress(false),
            };
            Object[] distributionType = {
                DistributionType.TO,
                DistributionType.CC,
                DistributionType.TO,
                DistributionType.CC,
            };
            for (int i = 0; i < groupList.length; i++) {
                if (null != groupList[i]) {
                    for (DistTemplateGroup group : (List<DistTemplateGroup>) groupList[i]) {
                        group.setDistributionType((DistributionType) distributionType[i]);
                        group.setGroupId(group.getCorresponGroup().getId());
                        groups.add(group);
                    }
                }
            }
            return groups;
        }

        /**
         * To, Ccリストの設定内容を検証する.
         * @throws ServiceAbortException エラーがある場合
         */
        private void validate() throws ServiceAbortException {
            validateEmpty();
            validateDuplicated();
        }

        /**
         * To, Ccリストが1件でも指定されているかを検証する.
         * @throws ServiceAbortException 何も指定されていない場合
         */
        private void validateEmpty() throws ServiceAbortException {
            List<DistTemplateGroup> listTo = page.decodeAddress(true);
            List<DistTemplateGroup> listCc = page.decodeAddress(false);
            if (0 == listTo.size() && 0 == listCc.size()) {
                throw new ServiceAbortException(
                    ApplicationMessageCode.MSG_GROUP_NOT_SPECIFIED);
            }
        }

        /**
         * グループに重複が無いを検証する.
         * @throws ServiceAbortException 何も指定されていない場合
         */
        private void validateDuplicated() throws ServiceAbortException {
            validateDuplicated(page.decodeAddress(true));
            validateDuplicated(page.decodeAddress(false));
        }

        /**
         * グループに重複が無いかどうかを検証する.
         * @throws ServiceAbortException 何も指定されていない場合
         */
        private void validateDuplicated(List<DistTemplateGroup> groupList)
            throws ServiceAbortException {
            if (null != groupList) {
                HashMap<String, String> groupIds = new HashMap<String, String>();
                for (DistTemplateGroup group : page.decodeAddress(true)) {
                    String corresponGroupId = group.getCorresponGroup().getId().toString();
                    if (groupIds.containsKey(corresponGroupId.toString())) {
                        throw new ServiceAbortException(
                            ApplicationMessageCode.MSG_GROUP_DUPLICATE,
                                new Object[] {group.getCorresponGroup().getName() });
                    }
                    groupIds.put(corresponGroupId, "on");
                }
            }
        }
    }
}
