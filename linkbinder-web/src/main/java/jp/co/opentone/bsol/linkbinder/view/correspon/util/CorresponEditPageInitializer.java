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

import jp.co.opentone.bsol.framework.core.config.SystemConfig;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.core.util.ArgumentValidator;
import jp.co.opentone.bsol.framework.core.util.DateUtil;
import jp.co.opentone.bsol.framework.core.util.PropertyGetUtil;
import jp.co.opentone.bsol.linkbinder.Constants;
import jp.co.opentone.bsol.linkbinder.dto.Correspon;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroup;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroupUser;
import jp.co.opentone.bsol.linkbinder.dto.CorresponType;
import jp.co.opentone.bsol.linkbinder.dto.CustomField;
import jp.co.opentone.bsol.linkbinder.dto.DistTemplateHeader;
import jp.co.opentone.bsol.linkbinder.dto.ProjectCustomSetting;
import jp.co.opentone.bsol.linkbinder.dto.ProjectUser;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.code.CorresponStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.CorresponTypeAdmittee;
import jp.co.opentone.bsol.linkbinder.dto.code.ForLearning;
import jp.co.opentone.bsol.linkbinder.dto.code.ReplyRequired;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowStatus;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponGroupCondition;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponTypeCondition;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCustomFieldCondition;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchUserCondition;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.service.admin.CorresponGroupService;
import jp.co.opentone.bsol.linkbinder.service.admin.CorresponTypeService;
import jp.co.opentone.bsol.linkbinder.service.admin.CustomFieldService;
import jp.co.opentone.bsol.linkbinder.service.admin.DistributionTemplateService;
import jp.co.opentone.bsol.linkbinder.service.admin.UserService;
import jp.co.opentone.bsol.linkbinder.service.correspon.CorresponService;
import jp.co.opentone.bsol.linkbinder.service.correspon.LearningLabelService;
import jp.co.opentone.bsol.linkbinder.service.correspon.LearningTagService;
import jp.co.opentone.bsol.linkbinder.view.correspon.CorresponEditPage;
import jp.co.opentone.bsol.linkbinder.view.correspon.strategy.CorresponSetupStrategy;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * コレポン編集画面の初期表示に必要な処理を行うクラス.
 * @author opentone
 */
@Component
@Scope("request")
public class CorresponEditPageInitializer implements Serializable {
    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 2048271684499685335L;

    /**
     * カスタムフィールドの最大数.
     */
    private static final int MAX_CUSTOM_FIELD =
        Integer.parseInt(SystemConfig.getValue(Constants.KEY_CUSTOM_FIELD_MAX_COUNT));

    /**
     * コレポン文書編集ページ.
     */
    private CorresponEditPage page;

    /**
     * コレポン文書サービス.
     */
    @Resource
    private CorresponService corresponService;
    /**
     * 活動単位サービス.
     */
    @Resource
    private CorresponGroupService corresponGroupService;
    /**
     * コレポン文書種別サービス.
     */
    @Resource
    private CorresponTypeService corresponTypeService;
    /**
     * カスタムフィールドサービス.
     */
    @Resource
    private CustomFieldService customFieldService;
    /**
     * ユーザーサービス.
     */
    @Resource
    private UserService userService;
    /**
     * Distribution templateサービス.
     */
    @Resource
    private DistributionTemplateService distTemplateService;
    /**
     * 学習用ラベルサービス
     */
    @Resource
    private LearningLabelService learningLabelService;
    /**
     * 学習用タグサービス
     */
    @Resource
    private LearningTagService learningTagService;

    /**
     * 空のインスタンスを生成する.
     */
    public CorresponEditPageInitializer() {
        page = null;
    }

    /**
     * コレポン文書編集モードを返す.
     * @return 現在の編集モード
     */
    private CorresponEditMode getCorresponEditMode() {
        CorresponEditMode mode = CorresponEditMode.NEW;
        if (StringUtils.isNotEmpty(page.getEditMode())) {
            mode = CorresponEditMode.valueOf(page.getEditMode());
        }
        return mode;
    }

    /**
     * コレポン文書編集ページを初期化する.
     * @param editPage コレポン文書編集ページ
     * @throws ServiceAbortException 初期化に失敗
     */
    public void initialize(CorresponEditPage editPage) throws ServiceAbortException {
        ArgumentValidator.validateNotNull(editPage);

        setPage(editPage);

        try {
            processSetupCorrespon();
        } catch (ServiceAbortException e) {
            page.setInitialDisplaySuccess(false);
            throw e;
        }
        processValidateProject();
        processSetupInputValues();
        processApplyCorresponValuesToPage();
        processDisplayState();
    }

    /**
     * ページに表示するコレポン文書の情報を設定する.
     * @throws ServiceAbortException コレポン文書情報設定に失敗
     */
    private void processSetupCorrespon() throws ServiceAbortException {
        CorresponSetupStrategy.getCorresponSetupStrategy(page, getCorresponEditMode()).setup();
    }

    /**
     * ページの表示状態を設定する.
     */
    private void processDisplayState() {
    }

    /**
     * 現在のプロジェクトに対して有効なコレポン文書であるか検証する.
     * @throws ServiceAbortException
     */
    private void processValidateProject() throws ServiceAbortException {
        // 指定のコレポン情報のプロジェクトが現在選択中のプロジェクト以外はエラー
        Correspon correspon = page.getCorrespon();
        if (!correspon.getProjectId().equals(page.getCurrentProjectId())) {
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_PROJECT_DIFF);
        }
    }

    /**
     * コレポン文書が保持する情報をページオブジェクトに適用する.
     * @throws ServiceAbortException
     */
    private void processApplyCorresponValuesToPage() throws ServiceAbortException {
        ValueProcessor e = new ValueProcessor(this);
        e.process();
    }

    /**
     * 値の選択候補など、入力に使用する値を準備する.
     * @throws ServiceAbortException
     */
    private void processSetupInputValues() throws ServiceAbortException {
        ValueListProcessor p = new ValueListProcessor(this);
        p.process();
    }

    /**
     * @param page the page to set
     */
    public void setPage(CorresponEditPage page) {
        this.page = page;
    }

    /**
     * @return the page
     */
    public CorresponEditPage getPage() {
        return page;
    }

    /**
     * コレポン文書の内容を画面表示オブジェクトに変換するための内部クラス.
     * @author opentone
     */
    static class ValueProcessor {
        /** ページ初期化オブジェクト. */
        private CorresponEditPageInitializer initializer;
        /** コレポン文書入力画面. */
        private CorresponEditPage page;
        /** コレポン文書. */
        private Correspon correspon;

        /**
         * 必須のオブジェクトを指定してインスタンス化する.
         */
        public ValueProcessor(CorresponEditPageInitializer initializer) {
            this.initializer = initializer;
            this.page = initializer.page;
            this.correspon = page.getCorrespon();
        }

        void process() throws ServiceAbortException {
            //  その他の情報のデフォルト値を適用
            applyDefaultValues();
            // カスタムフィールド値設定（画面表示1～10）
            applyCustomFields();

            // 画面制御を入力画面へ
            page.setEditMode(CorresponEditMode.NEW.name());
        }

        /**
         * ページに初期値を設定する.
         */
        private void applyDefaultValues() {
            applyFrom();
            applyContent();
            applyCorresponType();
            applyCorresponStatus();
            applyReplyRequired();
            applyLearningContents();
        }

        /**
         * 送信元活動単位を設定する.
         * <p>
         * コレポン文書に設定済の活動単位か、ユーザーのデフォルト活動単位.
         */
        private void applyFrom() {
            if (correspon.getFromCorresponGroup() != null) {
                page.setFrom(correspon.getFromCorresponGroup().getId());
            } else {
                ProjectUser pu = page.getCurrentProjectUser();
                if (pu != null && pu.getDefaultCorresponGroup() != null) {
                    page.setFrom(pu.getDefaultCorresponGroup().getId());
                } else {
                    // デフォルト活動単位が未設定なら選択肢の先頭を初期値にする
                    List<CorresponGroup> groups = page.getFromGroup();
                    if (groups != null && !groups.isEmpty()) {
                        page.setFrom(groups.get(0).getId());
                    }
                }
            }
        }

        /**
         * 件名、本文を設定する.
         */
        private void applyContent() {
            page.setSubject(correspon.getSubject());
            page.setBody(correspon.getBody());
        }

        /**
         * コレポン文書種別を設定する.
         */
        private void applyCorresponType() {
            if (correspon.getCorresponType() == null) {
                page.setType(0L);
            } else {
                //  corresponType.idではなく、
                //  プロジェクト毎マスタのIDであるcorresponType.projectCorresponTypeIdを設定する
                page.setType(correspon.getCorresponType().getProjectCorresponTypeId());
            }
        }

        /**
         * 文書状態を設定する.
         */
        private void applyCorresponStatus() {
            if (correspon.getCorresponStatus() != null) {
                page.setCorresponStatus(correspon.getCorresponStatus().getValue());
            }
        }

        /**
         * 学習用文書か否かを設定する.
         */
        private void applyLearningContents() {
            page.setForLearning(correspon.getForLearning() == ForLearning.LEARNING);

            // ラベル・タグ
            if (CollectionUtils.isNotEmpty(correspon.getLearningLabel())) {
                List<Long> result = correspon.getLearningLabel().stream()
                        .map(l -> l.getId())
                        .collect(Collectors.toList());
                page.setLearningLabels(result.toArray(new Long[0]));
            }
            if (CollectionUtils.isNotEmpty(correspon.getLearningTag())) {
                List<Long> result = correspon.getLearningTag().stream()
                        .map(l -> l.getId())
                        .collect(Collectors.toList());
                page.setLearningTags(result.toArray(new Long[0]));
            }
        }

        /**
         * 返信要否・返信期限を設定する.
         */
        private void applyReplyRequired() {
            if (correspon.getReplyRequired() != null) {
                page.setReplyRequired(correspon.getReplyRequired().getValue());
            }
            page.setDeadlineForReply(DateUtil.convertDateToString(correspon.getDeadlineForReply()));
        }

        /**
         * カスタムフィールド項目を設定する.
         */
        private void applyCustomFields() throws ServiceAbortException {
            // 値を設定
            initializer.corresponService.adjustCustomFields(correspon);
            applyCustomFieldValue();
        }

        private void applyCustomFieldValue() {
            final String property = "customField%dValue";
            for (int i = 1; i <= MAX_CUSTOM_FIELD; i++) {
                String p = String.format(property, i);
                PropertyGetUtil.setProperty(page, p, PropertyGetUtil.getProperty(correspon, p));
            }
            page.initCustomFieldValues();
        }
    }

    /**
     * コレポン文書入力画面に表示する入力値を準備するための内部クラス.
     * @author opentone
     */
    static class ValueListProcessor {

        /** ページ初期化オブジェクト. */
        private CorresponEditPageInitializer initializer;
        /** コレポン文書入力画面. */
        private CorresponEditPage page;
        /** コレポン文書. */
        private Correspon correspon;
        /**
         * 必須のオブジェクトを指定してインスタンス化する.
         */
        ValueListProcessor(CorresponEditPageInitializer initializer) {
            this.initializer = initializer;
            this.page = initializer.page;
            this.correspon = page.getCorrespon();
        }

        /**
         * 値のリストを準備する.
         *   送信元活動単位
         *   コレポン文書種別
         *   文書状態
         *   返信要否
         *   宛先(活動単位)
         *   宛先(ユーザー)
         *   カスタムフィールド
         * @throws ServiceAbortException
         */
        void process() throws ServiceAbortException {
            processFromCorresponGroup();
            processCorresponType();
            processCorresponStatus();
            processReplyRequired();
            processAddressGroup();
            processAddressUser();
            processCustomFieldValues();
            processDistributionTemplate();
            processLearning();
        }

        private void processFromCorresponGroup() throws ServiceAbortException {
            // 送信元活動単位を取得、選択リスト生成
            if (page.isSystemAdmin() || page.isProjectAdmin(page.getCurrentProjectId())) {
                page.setFromGroup(findProjectCorresponGroups());
            } else {
                String projectId = page.getCurrentProjectId();
                String empNo = page.getCurrentUser().getEmpNo();

                List<CorresponGroupUser> corresponGroupUsers =
                    initializer.userService.searchCorrseponGroup(projectId, empNo);

                // コレポン文書のFromグループを取り出して追加する。
                List<CorresponGroup> corresponGroups = findLoginUserGroups(corresponGroupUsers);
                CorresponGroup corresponGroup = page.getCorrespon().getFromCorresponGroup();
                if (corresponGroup != null) {
                    // 重複を防ぐだめグループリスト内になければ追加する。
                    boolean find = false;
                    for (CorresponGroup cg : corresponGroups) {
                        if (cg.getId().equals(corresponGroup.getId())) {
                            find = true;
                            break;
                        }
                    }
                    if (!find) {
                        corresponGroups.add(corresponGroup);
                    }
                }

                //コレポン文書のFROM活動単位
                page.setFromGroup(corresponGroups);
            }
            page.createSelectFrom();
        }

        private void processCorresponType() throws ServiceAbortException {
            // コレポン文書種別を取得、選択リスト生成
            SearchCorresponTypeCondition condition =
                new SearchCorresponTypeCondition();
            String projectId = page.getCurrentProjectId();
            condition.setProjectId(projectId);
            List<CorresponType> corresponTypeList =
                initializer.corresponTypeService.search(condition);
            if (corresponTypeList != null && !corresponTypeList.isEmpty()) {
                ProjectCustomSetting pcs =
                    page.getCurrentProject().getProjectCustomSetting();
                if (pcs != null && pcs.isUseCorresponAccessControl()
                    && !page.isSystemAdmin()) {
                    corresponTypeList = filterCorresponType(corresponTypeList);
                }
            }
            page.setCorresponType(corresponTypeList);
            page.createSelectCorresponType();
        }


        private void processLearning() throws ServiceAbortException {
            // 学習用ラベル・タグを取得、選択リスト生成
            page.setLearningLabelList(initializer.learningLabelService.findAll());
            page.setLearningTagList(initializer.learningTagService.findAll());
        }

        /**
         * ユーザーの種別に応じて CorresponType のリストをフィルターする.
         * @param corresponTypeList 元のリスト
         * @return フィルター済みのリスト
         */
        private List<CorresponType>
                filterCorresponType(List<CorresponType> corresponTypeList) {
            List<CorresponType> result =
                new ArrayList<CorresponType>(corresponTypeList.size());
            String projectId = page.getCurrentProjectId();
            // ユーザーが持っている権限
            int userPrivilege = 0;
            if (page.isProjectAdmin(projectId)) {
                userPrivilege |= CorresponTypeAdmittee.PROJECT_ADMIN.getValue();
            }
            // プロジェクト内のいずれかのグループの Admin なら GA
            if (page.isAnyGroupAdmin(projectId)) {
                userPrivilege |= CorresponTypeAdmittee.GROUP_ADMIN.getValue();
            }
            // PA でも GA でもない場合だけ NU とする
            if (userPrivilege == 0) {
                userPrivilege |= CorresponTypeAdmittee.NORMAL_USER.getValue();
            }
            CorresponType currentCt = correspon.getCorresponType();
            Long currentId = (currentCt == null)
                                ? null : currentCt.getProjectCorresponTypeId();
            for (CorresponType ct : corresponTypeList) {
                if (((userPrivilege & ct.getCorresponAccessControlFlags()) > 0)
                    || (CorresponEditMode.UPDATE
                    == initializer.getCorresponEditMode() && currentId != null
                        && currentId.equals(ct.getProjectCorresponTypeId()))) {
                    result.add(ct);
                }
            }
            return result;
        }

        private void processCorresponStatus() {
            // コレポン文書状態
            //  未発行のコレポン文書の場合、「Canceled」を選択することはできない
            if (correspon.getWorkflowStatus() != WorkflowStatus.ISSUED) {
                CorresponStatus[] values = new CorresponStatus[CorresponStatus.values().length - 1];
                int index = 0;
                for (CorresponStatus val : CorresponStatus.values()) {
                    if (val != CorresponStatus.CANCELED) {
                        values[index++] = val;
                    }
                }
                page.createSelectCorresponStatus(values);
            } else {
                page.createSelectCorresponStatus(CorresponStatus.values());
            }
        }

        private void processReplyRequired() {
            page.createSelectReplyRequired(ReplyRequired.values());
        }

        private void processAddressGroup() throws ServiceAbortException {
            // 活動単位を設定
            page.setCorresponGroups(findProjectCorresponGroups());
        }

        private void processAddressUser() throws ServiceAbortException {
            List<ProjectUser> projectUsers = getProjectUsers();
            page.setProjectUsers(createUserList(projectUsers));
        }

        private void processDistributionTemplate() throws ServiceAbortException {
            List<DistTemplateHeader> list = findDistributionTemplate();
            page.setDistributionTemplateList(list);
            page.createSelectDistributionTemplate();
        }

        private List<User> createUserList(List<ProjectUser> projectUsers) {
            List<User> users = new ArrayList<User>();
            if (projectUsers != null) {
                for (ProjectUser u : projectUsers) {
                    users.add(u.getUser());
                }
            }
            return users;
        }

        private void processCustomFieldValues() throws ServiceAbortException {
            final String pageProperty = "customFieldValue%d";

            SearchCustomFieldCondition condition = new SearchCustomFieldCondition();
            condition.setProjectId(correspon.getProjectId());
            List<CustomField> fields = initializer.customFieldService.search(condition);
            int max = fields.size() > MAX_CUSTOM_FIELD ? MAX_CUSTOM_FIELD : fields.size();
            int i = 1;
            for (; i <= max; i++) {
                Long id = fields.get(i - 1).getId();

                PropertyGetUtil.setProperty(page,
                    String.format(pageProperty, i),
                    initializer.customFieldService.findCustomFieldValue(id));
            }
            for (; i <= MAX_CUSTOM_FIELD; i++) {
                PropertyGetUtil.setProperty(page,
                    String.format(pageProperty, i),
                    null);
            }
            page.initCustomFieldValueCandidateList();
        }

        private List<ProjectUser> getProjectUsers() throws ServiceAbortException {
            SearchUserCondition condition = new SearchUserCondition();
            condition.setProjectId(page.getCurrentProjectId());
            return initializer.userService.search(condition);
        }

        private List<CorresponGroup> findProjectCorresponGroups() throws ServiceAbortException {
            SearchCorresponGroupCondition condition = new SearchCorresponGroupCondition();
            condition.setProjectId(page.getCurrentProjectId());
            return initializer.corresponGroupService.search(condition);
        }

        private List<DistTemplateHeader> findDistributionTemplate()
            throws ServiceAbortException {
            List<DistTemplateHeader> list = null;
            try {
                String projectId = page.getCurrentProjectId();
                list = initializer.distTemplateService.findDistTemplateList(projectId);
            } catch (ServiceAbortException e) {
                // Distribution templateが存在しない場合は利用しないだけでエラーとは扱わない
                if (!ApplicationMessageCode.NO_DATA_FOUND.equals(e.getMessage())) {
                    throw e;
                }
            }
            return list;
        }
        /**
         * ログインユーザーが所属する活動単位取得.
         * @param corresponGroupUsers
         * @return CorresponGroup List
         */
        private List<CorresponGroup> findLoginUserGroups(
            List<CorresponGroupUser> corresponGroupUsers) {
            List<CorresponGroup> groupLists = new ArrayList<CorresponGroup>();
            if (corresponGroupUsers == null) {
                return groupLists;
            }
            for (CorresponGroupUser correponGroupUser : corresponGroupUsers) {
                CorresponGroup group = new CorresponGroup();
                group.setId(correponGroupUser.getCorresponGroup().getId());
                group.setName(correponGroupUser.getCorresponGroup().getName());

                groupLists.add(group);
            }
            return groupLists;
        }
    }
}
