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
package jp.co.opentone.bsol.linkbinder.service.admin.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.linkbinder.dao.CorresponGroupDao;
import jp.co.opentone.bsol.linkbinder.dao.CorresponGroupUserDao;
import jp.co.opentone.bsol.linkbinder.dao.DistTemplateGroupDao;
import jp.co.opentone.bsol.linkbinder.dao.DistTemplateHeaderDao;
import jp.co.opentone.bsol.linkbinder.dao.DistTemplateUserDao;
import jp.co.opentone.bsol.linkbinder.dao.UserDao;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroup;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroupUserMapping;
import jp.co.opentone.bsol.linkbinder.dto.DistTemplateGroup;
import jp.co.opentone.bsol.linkbinder.dto.DistTemplateHeader;
import jp.co.opentone.bsol.linkbinder.dto.DistTemplateHeaderDelete;
import jp.co.opentone.bsol.linkbinder.dto.DistTemplateUser;
import jp.co.opentone.bsol.linkbinder.dto.DistTemplateUserDelete;
import jp.co.opentone.bsol.linkbinder.dto.DistributionInfo;
import jp.co.opentone.bsol.linkbinder.dto.ProjectUser;
import jp.co.opentone.bsol.linkbinder.dto.UpdateMode;
import jp.co.opentone.bsol.linkbinder.dto.code.DistributionType;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponGroupCondition;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchUserCondition;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.service.AbstractService;
import jp.co.opentone.bsol.linkbinder.service.admin.DistributionTemplateService;

/**
 * {@link DistributionTemplateService}.
 * @author opentone
 */
@Service
@Transactional(rollbackFor = { ServiceAbortException.class })
public class DistributionTemplateServiceImpl extends AbstractService
    implements DistributionTemplateService {

    /**
     * serialVersionUID.
     */
    private static final long serialVersionUID = 6917783199589237385L;

    /**
     * テストモードで使用する場合はtrue.
     */
    private boolean testMode;

    /**
     * テストモードを取得する.
     * @return テストモード
     */
    public boolean isTestMode() {
        return testMode;
    }

    /**
     * テストモードを設定する.
     * @param testMode テストモード.
     */
    public void setTestMode(boolean testMode) {
        this.testMode = testMode;
    }

    /* (non-Javadoc)
     * @see .DistributionTemplateService#find(Long id)
     */
    @Transactional(readOnly = true)
    public DistTemplateHeader find(Long id) throws ServiceAbortException {
        // 操作権限チェック
        // 不要. このメソッドはユーザーも使用するため
        //validatePermission();

        DistTemplateHeaderDao headerDao = getDao(DistTemplateHeaderDao.class);
        DistTemplateGroupDao groupDao = getDao(DistTemplateGroupDao.class);

        // Distributionテンプレートヘッダー情報を取得
        DistTemplateHeader dth = headerDao.findById(id);
        if (null == dth) {
            throw new ServiceAbortException(MSG_NOT_IN_DIST_TEMPLATE_HEADER);
        }
        // ログイン中のプロジェクト情報と照合
        validateProjectId(dth.getProjectId());
        // グループ・ユーザー情報を取得
        List<DistTemplateGroup> groupList = groupDao.findByDistTemplateHeaderId(id);
        dth.setDistTemplateGroups(groupList);
        // このプロジェクトで利用可能な活動単位一覧の取得
        DistributionInfo distInfo = findDistributionInfo(dth.getProjectId());
        dth.setDistributionInfo(distInfo);

        return dth;
    }

    /**
     * プロジェクトに設定されている活動単位情報を取得する.
     * @param projectId プロジェクトID.
     * @return DistributionInfo
     * @throws ServiceAbortException 処理エラー.
     */
    @Transactional(readOnly = true)
    public DistributionInfo findDistributionInfo(String projectId)
        throws ServiceAbortException {
        DistributionInfo distInfo = new DistributionInfo();
        // 活動単位一覧の取得
        List<CorresponGroup> groupList = getCorresponGroupList(projectId);
        distInfo.setCorresponGroups(groupList);
        // ユーザー情報の取得
        List<ProjectUser> userList = getProjectUsers(projectId);
        distInfo.setCorresponUsers(userList);
        // 活動単位/ユーザー情報の取得
        List<CorresponGroupUserMapping> mappingList = getCorresponGroupUserMappings(projectId);
        distInfo.setCorresponGroupUserMappings(mappingList);
        return distInfo;
    }

    /**
     * プロジェクトに登録されている活動単位一覧を取得する.
     * @param projectId プロジェクトID
     * @return 活動単位一覧
     */
    private List<CorresponGroup> getCorresponGroupList(String projectId)
        throws ServiceAbortException {
        SearchCorresponGroupCondition condition = new SearchCorresponGroupCondition();
        condition.setProjectId(projectId);
        CorresponGroupDao dao = getDao(CorresponGroupDao.class);
        List<CorresponGroup> groupList = dao.find(condition);
        return groupList;
    }

    /**
     * プロジェクトに登録されている活動単位/ユーザー情報を取得する.
     * @param projectId プロジェクトID
     * @return 活動単位/ユーザー情報一覧
     */
    private List<CorresponGroupUserMapping> getCorresponGroupUserMappings(String projectId)
        throws ServiceAbortException {
        CorresponGroupUserDao dao = getDao(CorresponGroupUserDao.class);
        List<CorresponGroupUserMapping> list = dao.findCorresponGroupUserMapping(projectId);
        return list;
    }

    /**
     * プロジェクトに登録されているユーザー一覧を取得する.
     * @param projectId プロジェクトID
     * @return ユーザー一覧
     */
    private List<ProjectUser> getProjectUsers(String projectId)
        throws ServiceAbortException {
        SearchUserCondition condition = new SearchUserCondition();
        condition.setProjectId(projectId);
        UserDao dao = getDao(UserDao.class);
        List<ProjectUser> userList = dao.findProjectUser(condition);
        return userList;
    }

    @Override
    public void validateProjectId(String projectId) throws ServiceAbortException {
        if (!testMode) {
            super.validateProjectId(projectId);
        }
    }

    /* (non-Javadoc)
     * @see .DistributionTemplateService#findDistributionTemplates(Long projectId)
     */
    @Transactional(readOnly = true)
    public List<DistTemplateHeader> findDistTemplateList(String projectId)
        throws ServiceAbortException {
        // 権限チェック
        // 不要. このメソッドはユーザーも使用するため
        //validatePermission();
        // ログイン中のプロジェクト情報と照合
        validateProjectId(projectId);

        DistTemplateHeaderDao distTemplateHeaderDao = getDao(DistTemplateHeaderDao.class);
        List<DistTemplateHeader> list = distTemplateHeaderDao.findByProjectId(projectId);
        if (null == list || 0 == list.size()) {
            throw new ServiceAbortException(NO_DATA_FOUND);
        }
        return list;
    }

    /* (non-Javadoc)
     * @see .DistributionTemplateService
     *          #save(DistTemplateHeader distTemplateHeader, String updatedBy)
     */
    public DistTemplateHeader save(DistTemplateHeader distTemplateHeader, String empNo)
        throws ServiceAbortException {
        // データの検証
        validateForSave(distTemplateHeader);
        validatePermission();

        // 保存処理の実行
        DistTemplateHeader resultHeader = distTemplateHeader.clone();
        resultHeader = setUpdateBy(resultHeader, empNo);
        if (null != distTemplateHeader) {
            // dist_template_headerレコード保存
            resultHeader = saveDistTemplateHeader(resultHeader);
            // dist_template_groupレコード保存
            resultHeader = setGroupOrderNo(resultHeader);
            resultHeader = saveDistTemplateGroup(resultHeader);
            // dist_template_userレコード保存
            if (null != resultHeader && null != resultHeader.getDistTemplateGroups()) {
                List<DistTemplateGroup> gList = new ArrayList<DistTemplateGroup>();
                for (DistTemplateGroup group : resultHeader.getDistTemplateGroups()) {
                    DistTemplateGroup g = saveDistTemplateUser(group);
                    gList.add(g);
                }
                resultHeader.setDistTemplateGroups(gList);
            }
        }
        if (null == resultHeader) {
            throw new ServiceAbortException(MSG_FAIL_UPDATE);
        }
        return resultHeader;
    }

    /**
     * 各レコードに更新従業員No.を設定する.
     * @param header 更新用レコード.
     * @param empNo 更新従業員No.
     * @return 更新後のレコード.
     */
    public DistTemplateHeader setUpdateBy(DistTemplateHeader header, String empNo) {
        if (null != header) {
            header.setUpdatedBy(empNo);
            if (null == header.getId() || 0 == header.getId().longValue()) {
                header.setCreatedBy(empNo);
            }
            if (null != header.getDistTemplateGroups()) {
                for (DistTemplateGroup group : header.getDistTemplateGroups()) {
                    setGroupUpdateBy(group, empNo);
                }
            }
        }
        return header;
    }

    /**
     * 各グループレコードに更新従業員No.を設定する.
     * @param group 更新用レコード.
     * @param empNo 更新従業員No.
     */
    public void setGroupUpdateBy(DistTemplateGroup group, String empNo) {
        if (null != group) {
            if (UpdateMode.NEW == group.getMode()) {
                group.setCreatedBy(empNo);
            }
            group.setUpdatedBy(empNo);
            if (null != group.getUsers()) {
                for (DistTemplateUser user : group.getUsers()) {
                    user.setCreatedBy(empNo);
                    user.setUpdatedBy(empNo);
                }
            }
        }
    }

    /**
     * 更新データとして問題がないかを検証する.
     * @param distTemplateHeader 更新データ
     */
    private void validateForSave(DistTemplateHeader header)
        throws ServiceAbortException {
        // グループの検証
        validateGroupForSave(header);
        // ユーザーの検証
        if (null != header.getDistTemplateGroups()) {
            for (DistTemplateGroup group : header.getDistTemplateGroups()) {
                validateUserForSave(group);
            }
        }
    }

    /**
     * グループ設定が更新データとして問題がないかを検証する.
     * <ul>
     *   <lh>検証内容
     *   <li>To, Ccの各Distributionに同一のグループが設定されていないか</li>
     * </ul>
     * @param distTemplateHeader 更新データ
     */
    private void validateGroupForSave(DistTemplateHeader header)
        throws ServiceAbortException {
        List<List<DistTemplateGroup>> groupLists = new ArrayList<List<DistTemplateGroup>>(2);
        groupLists.add(header.getToDistTemplateGroups());
        groupLists.add(header.getCcDistTemplateGroups());
        for (List<DistTemplateGroup> groupList : groupLists) {
            HashMap<String, String> groupIdMap = new HashMap<String, String>(groupList.size());
            for (DistTemplateGroup group : groupList) {
                if (groupIdMap.containsKey(group.getGroupId().toString())) {
                    // 重複レコード有り
                    throw new ServiceAbortException("group duplicated. ",
                                    MSG_GROUP_DUPLICATE,
                                    group.getCorresponGroup().getName());
                }
                groupIdMap.put(group.getGroupId().toString(), "true");
                // グループ内のユーザー情報の検証
                validateUserForSave(group);
            }
        }
    }

    /**
     * ユーザー設定が更新データとして問題がないかを検証する.
     * <ul>
     *   <lh>検証内容
     *   <li>各グループに同一のユーザーが設定されていないか</li>
     * </ul>
     * @param distTemplateHeader 更新データ
     */
    private void validateUserForSave(DistTemplateGroup group)
        throws ServiceAbortException {
        if (null != group && null != group.getUsers()) {
            HashMap<String, String> empNoMap
                = new HashMap<String, String>(group.getUsers().size());
            for (DistTemplateUser user : group.getUsers()) {
                if (empNoMap.containsKey(user.getEmpNo().toString())) {
                    // 重複レコード有り
                    throw new ServiceAbortException(MSG_USER_DUPLICATE);
                }
                empNoMap.put(user.getEmpNo().toString(), "true");
            }
        }
    }

    /**
     * dist_template_headerレコードを保存する.
     * @param distTemplateHeader dist_template_headerレコード
     * @return 保存情報を含むdist_template_headerレコード
     * @exception ServiceAbortException 処理失敗.
     */
    private DistTemplateHeader saveDistTemplateHeader(DistTemplateHeader header)
        throws ServiceAbortException {
        if (null == header.getId() || 0L == header.getId().longValue()) {
            // 新規登録
            header = insertHeader(header);
        } else {
            // 更新登録
            header = updateHeader(header);
        }
        return header;
    }

    /**
     * dist_template_headerレコードを1件追加する.
     * @param header dist_template_headerレコード
     * @return 更新情報を含むdist_template_headerレコード
     */
    private DistTemplateHeader insertHeader(DistTemplateHeader header) {
        DistTemplateHeaderDao dao = getDao(DistTemplateHeaderDao.class);
        // 時刻情報を設定
        header.setUpdatedAt(header.setCreatedAtNow());
        // テンプレートコードにはプロジェクト内の通番を設定する
        header.setTemplateCd(getTemplateCd(header.getProjectId()));
        // empNoは予約機能なので現状は[0000]固定
        header.setEmpNo(DIST_TEMP_HEADER_EMP_NO);

        // 更新処理
        Long id = dao.create(header);

        // 更新後のデータ設定
        header.setId(id);
        header.setVersionNo(1L);
        header.setDeleteNo(0L);

        return header;
    }

    /**
     * dist_template_headerレコードを1件更新する.
     * @param header dist_template_headerレコード
     * @return 更新に成功した場合は更新後のdist_template_headerレコード
     * @exception ServiceAbortException 処理失敗.
     */
    private DistTemplateHeader updateHeader(DistTemplateHeader header)
        throws ServiceAbortException {
        DistTemplateHeader resultHeader = null;
        DistTemplateHeaderDao dao = getDao(DistTemplateHeaderDao.class);
        // 時刻情報を設定
        header.setUpdatedAtNow();

        // 更新処理
        Integer count = dao.update(header);
        if (1 == count.intValue()) {
            resultHeader = header;
            resultHeader.setVersionNo(header.getVersionNo() + 1);
        } else {
            throw new ServiceAbortException(MSG_FAIL_UPDATE);
        }
        return resultHeader;
    }

    /**
     * dist_template_groupの各レコードに更新後のorderNoを設定する.
     * @param distTemplateHeader 親dist_template_headerレコード
     * @return 更新に成功した場合は更新後のdist_template_headerレコード
     * @throws ServiceAbortException 更新失敗
     */
    private DistTemplateHeader setGroupOrderNo(DistTemplateHeader header)
        throws ServiceAbortException {
        if (null != header) {
            // グループの設定
            if (null != header.getDistTemplateGroups()) {
                long toOrderNo = 0L;
                long ccOrderNo = 0L;
                for (DistTemplateGroup group : header.getDistTemplateGroups()) {
                    if (UpdateMode.DELETE != group.getMode()) {
                        long orderNo
                            = (DistributionType.TO == group.getDistributionType())
                                ? ++toOrderNo : ++ccOrderNo;
                        group.setOrderNo(orderNo);
                    }
                }
            }
        }
        return header;
    }

    /**
     * dist_template_groupを更新する.
     * @param distTemplateHeader 親dist_template_headerレコード
     * @return 更新に成功した場合は更新後のdist_template_headerレコード
     * @throws ServiceAbortException 更新失敗
     */
    private DistTemplateHeader saveDistTemplateGroup(DistTemplateHeader header)
        throws ServiceAbortException {
        if (null != header) {
            // グループの設定
            if (null != header.getDistTemplateGroups()) {
                List<DistTemplateGroup> gList = new ArrayList<DistTemplateGroup>();
                // Note:キー制約を回避するため、削除対象だけ先ずは処理
                for (DistTemplateGroup group : header.getDistTemplateGroups()) {
                    if (UpdateMode.DELETE == group.getMode()) {
                        deleteGroup(group);
                    }
                }
                for (int i = 0; i < header.getDistTemplateGroups().size(); i++) {
                    DistTemplateGroup group = header.getDistTemplateGroups().get(i);
                    // 更新後の情報
                    if (UpdateMode.NEW == group.getMode()) {
                        group.setDistTemplateHeaderId(header.getId());
                        gList.add(0, insertGroup(group));
                    } else if (UpdateMode.UPDATE == group.getMode()
                            || UpdateMode.NONE == group.getMode()) {
                        gList.add(0, updateGroup(group));
                    }
                }
                header.setDistTemplateGroups(gList);
            }
        }
        return header;
    }

    /**
     * dist_template_groupレコードを1件追加する.
     * @param header dist_template_groupレコード
     * @return 更新情報を含むdist_template_headerレコード
     */
    private DistTemplateGroup insertGroup(DistTemplateGroup group) {
        DistTemplateGroupDao dao = getDao(DistTemplateGroupDao.class);
        // 登録・更新ユーザー,時刻情報を設定
        group.setUpdatedAt(group.setCreatedAtNow());

        // 登録処理
        Long id = dao.create(group);

        // 更新後のデータ設定
        group.setId(id);
        group.setDeleteNo(0L);

        return group;
    }

    /**
     * dist_template_groupレコードを1件更新する.
     * @param header dist_template_groupレコード
     * @return 更新情報を含むdist_template_headerレコード
     * @throws ServiceAbortException 更新失敗
     */
    private DistTemplateGroup updateGroup(DistTemplateGroup group)
        throws ServiceAbortException {
        DistTemplateGroupDao dao = getDao(DistTemplateGroupDao.class);
        // 登録・更新ユーザー,時刻情報を設定
        group.setUpdatedAtNow();

        // 更新処理
        if (!dao.update(group)) {
            throw new ServiceAbortException(MSG_FAIL_UPDATE);
        }
        return group;
    }

    /**
     * dist_template_groupレコードを1件論理削除する.
     * @param header dist_template_groupレコード
     * @return 更新情報を含むdist_template_headerレコード
     * @throws ServiceAbortException 更新失敗
     */
    private void deleteGroup(DistTemplateGroup group)
        throws ServiceAbortException {
        DistTemplateGroupDao dao = getDao(DistTemplateGroupDao.class);
        // 登録・更新ユーザー,時刻情報を設定
        group.setUpdatedAtNow();

        // 論理削除処理
        Integer count = dao.delete(group);
        if (1 != count.intValue()) {
            throw new ServiceAbortException(MSG_FAIL_UPDATE);
        }
    }

    /**
     * dist_template_userを更新する.
     * @param distTemplateHeader 親dist_template_groupレコード
     * @return 更新に成功した場合は更新後のdist_template_groupレコード
     * @throws ServiceAbortException 更新失敗
     */
    private DistTemplateGroup saveDistTemplateUser(DistTemplateGroup group)
        throws ServiceAbortException {
        if (null != group) {
            DistTemplateUserDao dao = getDao(DistTemplateUserDao.class);
            // 既存レコードはすべて論理削除
            if (UpdateMode.NEW != group.getMode() && UpdateMode.DELETE != group.getMode()) {
                DistTemplateUserDelete delete = createDistTemplateUserDelete(group);
                // 既存レコード無しの場合もあるので更新件数でエラー判定は行わない
                dao.deleteByDistTemplateGroupId(delete);
            }
            // 新規登録処理
            if (null != group.getUsers()) {
                long orderNo = 0;
                for (DistTemplateUser user : group.getUsers()) {
                    user.setDistTemplateGroupId(group.getId());
                    user.setOrderNo(++orderNo);
                    user.setCreatedAtNow();
                    user.setUpdatedAt(user.getCreatedAt());
                    // 登録処理実行
                    Long id = dao.create(user);
                    // 登録後のデータ設定
                    user.setId(id);
                    user.setVersionNo(1L);
                    user.setDeleteNo(0L);
                }
            }
        }
        return group;
    }

    /**
     * ユーザー情報削除用オブジェクトを作成する.
     * @param group 削除対象のグループ情報
     * @return ユーザー情報削除用オブジェクト
     */
    private DistTemplateUserDelete createDistTemplateUserDelete(DistTemplateGroup group) {
        DistTemplateUserDelete obj = DistTemplateUser.newDistTemplateUserDelete();
        obj.setDistTemplateGroupId(group.getId());
        obj.setUpdatedBy(group.getUpdatedBy());
        obj.setUpdatedAtNow();
        return obj;
    }

    /**
     * 権限がない場合はエラー.
     * @throws ServiceAbortException 権限がない.
     */
    private void validatePermission() throws ServiceAbortException {
        if (!testMode && !isValidPermission()) {
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT);
        }
    }

    /**
     * SystemAdmin,ProjectAdminか判定する.
     * @return SystemAdmin,ProjectAdmin,GroupAdminのいづれかの場合true / 以外はfalse
     */
    private boolean isValidPermission() {
        boolean result = false;
        if (testMode) {
            result = true;
        } else {
            result = isSystemAdmin(getCurrentUser())
                        || isProjectAdmin(getCurrentUser(), getCurrentProjectId());
        }
        return result;
    }

    /**
     * テンプレートコードを発行する.
     * @param projectId プロジェクトID
     * @return String テンプレートコード
     */
    private String getTemplateCd(String projectId) {
        DistTemplateHeaderDao dao = getDao(DistTemplateHeaderDao.class);
        String templateCode = dao.getTemplateCodeByProject(projectId);
        return templateCode;
    }

    /* (非 Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.service.admin.DistributionTemplateService
     *          #delete(jp.co.opentone.bsol.linkbinder.dto.DistTemplateHeaderDelete, String empNo)
     */
    @Override
    public void delete(DistTemplateHeaderDelete distTemplateHeader, String empNo)
        throws ServiceAbortException {
        // データの検証
        validateForSave((DistTemplateHeader) distTemplateHeader);

        DistTemplateHeaderDao dao = getDao(DistTemplateHeaderDao.class);
        DistTemplateHeader cloneObj = ((DistTemplateHeader) distTemplateHeader).clone();
        cloneObj.setUpdatedBy(empNo);
        cloneObj.setUpdatedAtNow();

        Integer count = dao.delete(cloneObj);
        if (1 != count) {
            throw new ServiceAbortException(MSG_FAIL_DELETE);
        }
    }
}
