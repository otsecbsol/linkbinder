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
package jp.co.opentone.bsol.linkbinder.view.correspon.strategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.linkbinder.dto.AddressCorresponGroup;
import jp.co.opentone.bsol.linkbinder.dto.AddressUser;
import jp.co.opentone.bsol.linkbinder.dto.Correspon;
import jp.co.opentone.bsol.linkbinder.dto.PersonInCharge;
import jp.co.opentone.bsol.linkbinder.dto.UpdateMode;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.Workflow;
import jp.co.opentone.bsol.linkbinder.dto.code.AddressType;
import jp.co.opentone.bsol.linkbinder.dto.code.AddressUserType;
import jp.co.opentone.bsol.linkbinder.dto.code.CorresponStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.ReplyRequired;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowStatus;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.util.view.correspon.CorresponPageFormatter;
import jp.co.opentone.bsol.linkbinder.view.correspon.CorresponEditPage;
import jp.co.opentone.bsol.linkbinder.view.correspon.util.CorresponEditMode;

/**
 * コレポン文書入力画面に初期表示するコレポン文書オブジェクトを生成する.
 * 編集モード @{link {@link CorresponEditMode#REPLY}に対応する.
 * @author opentone
 */
public class ReplyCorresponSetupStrategy extends CorresponSetupStrategy {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -7392260577497403507L;

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.view.correspon.util.setup.CorresponSetup#setup()
     */
    @Override
    public void setup() throws ServiceAbortException {
        Correspon reply = new Correspon();
        setupBase(reply);

        // 返信元文書を取得し、返信文書に内容を引き継ぐ
        Correspon request = getReferencedCorrespon(page.getId());
        validateReply(request);
        // 検索条件として使用したので値をクリア
        page.setId(null);
        transferReply(request, reply);
        reply.setCorresponStatus(page.getDefaultStatus());
        page.setCorrespon(reply);
    }

    private void validateReply(Correspon correspon) throws ServiceAbortException {
        // Attention、Person in Charge、Ccのいずれかにログインユーザーが含まれていないといけない
        // 管理者はチェックしない
        if (page.isSystemAdmin() || page.isProjectAdmin(page.getCurrentProjectId())
                || page.isAnyGroupAdmin(page.getCurrentProjectId())) {
            return;
        } else if (isNotReplyPermission(correspon)) {
            throw new ServiceAbortException(ApplicationMessageCode.
                CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT);
        }
    }

    /**
     * ログインユーザーがAttention、Person in Charge、Ccのいずれかに設定されているか判定する.
     * @param correspon コレポン文書
     * @return 設定されていないtrue / 設定されているfalse
     */
    private boolean isNotReplyPermission(Correspon correspon) {
        // 誰でも返信ができるように変更
        return false;
    }

    protected void setupBase(Correspon reply) {
        reply.setProjectId(page.getCurrentProjectId());
        reply.setProjectNameE(page.getCurrentProject().getNameE());
        reply.setWorkflowStatus(WorkflowStatus.DRAFT);
        reply.setReplyRequired(ReplyRequired.NO);
    }

    /**
     * 依頼文書の値を返信文書に引き継ぐ.
     * @param request 依頼文書
     * @param reply 返信文書
     */
    private void transferReply(Correspon request, Correspon reply) throws ServiceAbortException {
        transferReplyTo(request, reply);
        transferAddress(request, reply);
        transferReplyCustomField(request, reply);
        transferValues(request, reply);
    }

    /**
     * 依頼文書のコレポン文書ID、コレポン文書番号を返信文書に設定する.
     * @param request 依頼文書
     * @param reply 返信文書
     */
    protected void transferReplyTo(Correspon request, Correspon reply) {
        reply.setParentCorresponId(request.getId());
        reply.setParentCorresponNo(request.getCorresponNo());
    }

    /**
     * 依頼文書の各種値を返信文書に設定する.
     * @param request 依頼文書
     * @param reply 返信文書
     */
    private void transferValues(Correspon request, Correspon reply) {
        reply.setCorresponType(request.getCorresponType());

        CorresponPageFormatter f = new CorresponPageFormatter(request);
        reply.setSubject(f.getReplySubject());
        reply.setBody(f.getReplyBody());
    }

    /**
     * 返信文書の宛先を設定する.
     * @param request 依頼文書
     * @param reply 返信文書
     * @throws ServiceAbortException 設定に失敗
     */
    private void transferAddress(Correspon request, Correspon reply) throws ServiceAbortException {
        List<AddressCorresponGroup> addresses = new ArrayList<AddressCorresponGroup>();
        addresses.add(getTo(request, reply));
        addresses.addAll(getCc(request, reply));

        //  編集モードを新規に設定したうえでコレポン文書に設定
        UpdateMode.setUpdateMode(addresses, UpdateMode.NEW);
        reply.setAddressCorresponGroups(addresses);
        clearRepliedInformationFromAddresses(reply);
    }

    /**
     * 返信文書のToに設定する、依頼文書の送信元活動単位および作成者、検討者、承認者を返す.
     * @param request 依頼文書
     * @param reply 返信文書
     * @return 返信文書のTo
     * @throws ServiceAbortException 設定に失敗
     */
    protected AddressCorresponGroup getTo(Correspon request, Correspon reply)
            throws ServiceAbortException {
        AddressCorresponGroup ag = new AddressCorresponGroup();
        ag.setCorresponGroup(request.getFromCorresponGroup());
        ag.setAddressType(AddressType.TO);

        Set<String> empNoSet = new HashSet<String>();

        //  Preparer
        List<AddressUser> users = new ArrayList<AddressUser>();
        AddressUser au = new AddressUser();
        au.setUser(request.getCreatedBy());
        au.setAddressUserType(AddressUserType.ATTENTION);
        users.add(au);
        empNoSet.add(au.getUser().getEmpNo());

        //  Checker/Approver
        if (request.getWorkflows() != null) {
            for (Workflow w : request.getWorkflows()) {
                if (empNoSet.contains(w.getUser().getEmpNo())) {
                    continue;
                }
                au = new AddressUser();
                au.setUser(w.getUser());
                au.setAddressUserType(AddressUserType.ATTENTION);
                users.add(au);
                empNoSet.add(au.getUser().getEmpNo());
            }
        }
        ag.setUsers(users);
        return ag;
    }

    /**
     * 返信文書のCcに設定する、依頼文書のTo、Ccを返す.
     * @param request 依頼文書
     * @param reply 返信文書
     * @return 返信文書のCc
     * @throws ServiceAbortException 設定に失敗
     */
    private List<AddressCorresponGroup> getCc(Correspon request, Correspon reply)
            throws ServiceAbortException {
        // 重複する活動単位をまとめるためのMap
        Map<Long, AddressCorresponGroup> groupMap = new HashMap<Long, AddressCorresponGroup>();

        for (AddressCorresponGroup ag : request.getAddressCorresponGroups()) {
            //  送信元活動単位に返信者しか含まれていない場合は宛先に加えない
            if (isExcludeAddressCorresponGroup(request, reply, ag)) {
                continue;
            }
            // 重複する活動単位をまとめる
            AddressCorresponGroup group = groupMap.get(ag.getCorresponGroup().getId());
            if (group == null) {
                group = new AddressCorresponGroup();
                group.setCorresponGroup(ag.getCorresponGroup());
                group.setCorresponId(ag.getCorresponId());
                group.setAddressType(AddressType.CC);
            }
            groupMap.put(
                ag.getCorresponGroup().getId(), flattenAddressUserPersonInCharge(group, ag));
        }
        return new ArrayList<AddressCorresponGroup>(groupMap.values());
    }

    private boolean isExcludeAddressCorresponGroup(
            Correspon request, Correspon reply, AddressCorresponGroup ag) {
        //  現在返信しようとしているユーザーしか宛先に設定されていなければCcには含まれない
        User login = page.getCurrentUser();
        int count = 0;
        for (AddressUser au : ag.getUsers()) {
            count += login.getEmpNo().equals(au.getUser().getEmpNo())
                        ? 0 : 1;

            count += au.getPersonInCharges() != null
                        ? au.getPersonInCharges().size()
                        : 0;
        }
        return count == 0;  //  返信者以外が一人もいなければ宛先には含まない
    }

    /**
     * 宛先に含まれる宛先-ユーザーとその担当者を平坦化し、
     * 全て宛先-活動単位に関連付く宛先-ユーザーとして再設定する.
     * @param result 設定先の宛先-活動単位
     * @param ag 設定元の宛先-活動単位
     * @return 設定後の宛先
     */
    private AddressCorresponGroup flattenAddressUserPersonInCharge(
        AddressCorresponGroup result, AddressCorresponGroup ag) {
        //  作成者自身は宛先に含めない
        String excludeEmpNo = page.getCurrentUser().getEmpNo();

        Set<String> empNoSet = createEmpNoSet(result);
        List<AddressUser> flatten = new ArrayList<AddressUser>();
        if (ag.getUsers() != null) {
            for (AddressUser au : ag.getUsers()) {
                if (!excludeEmpNo.equals(au.getUser().getEmpNo())
                     && !empNoSet.contains(au.getUser().getEmpNo())) {
                    au.setAddressUserType(AddressUserType.NORMAL_USER);
                    flatten.add(au);
                    empNoSet.add(au.getUser().getEmpNo());
                }

                if (au.getPersonInCharges() != null) {
                    for (PersonInCharge pic : au.getPersonInCharges()) {
                        if (excludeEmpNo.equals(pic.getUser().getEmpNo())
                            || empNoSet.contains(pic.getUser().getEmpNo())) {
                            continue;
                        }
                        AddressUser u = new AddressUser();
                        u.setAddressUserType(AddressUserType.NORMAL_USER);
                        u.setUser(pic.getUser());
                        flatten.add(u);
                        empNoSet.add(u.getUser().getEmpNo());
                    }
                }
            }
        }
        if (result.getUsers() != null && !result.getUsers().isEmpty()) {
            result.getUsers().addAll(flatten);
        } else {
            result.setUsers(flatten);
        }

        return result;
    }

    private Set<String> createEmpNoSet(AddressCorresponGroup ag) {
        Set<String> result = new HashSet<String>();
        if (ag.getUsers() != null) {
            for (AddressUser au : ag.getUsers()) {
                if (au.getUser() != null) {
                    result.add(au.getUser().getEmpNo());
                }
            }
        }

        return result;
    }

    /**
     * 依頼文書のカスタムフィールド値を返信文書に設定する.
     * @param request 依頼文書
     * @param reply 返信文書
     */
    //カスタムフィールドの項目数が多いため行数オーバーの警告が出るが、仕方が無いので警告を抑制する
    //CHECKSTYLE:OFF
    protected void transferReplyCustomField(
            Correspon request, Correspon reply) {
    //CHECKSTYLE:ON
        reply.setCustomField1Id(request.getCustomField1Id());
        reply.setCustomField1Label(request.getCustomField1Label());
        reply.setCustomField1Value(request.getCustomField1Value());
        reply.setCustomField2Id(request.getCustomField2Id());
        reply.setCustomField2Label(request.getCustomField2Label());
        reply.setCustomField2Value(request.getCustomField2Value());
        reply.setCustomField3Id(request.getCustomField3Id());
        reply.setCustomField3Label(request.getCustomField3Label());
        reply.setCustomField3Value(request.getCustomField3Value());
        reply.setCustomField4Id(request.getCustomField4Id());
        reply.setCustomField4Label(request.getCustomField4Label());
        reply.setCustomField4Value(request.getCustomField4Value());
        reply.setCustomField5Id(request.getCustomField5Id());
        reply.setCustomField5Label(request.getCustomField5Label());
        reply.setCustomField5Value(request.getCustomField5Value());
        reply.setCustomField6Id(request.getCustomField6Id());
        reply.setCustomField6Label(request.getCustomField6Label());
        reply.setCustomField6Value(request.getCustomField6Value());
        reply.setCustomField7Id(request.getCustomField7Id());
        reply.setCustomField7Label(request.getCustomField7Label());
        reply.setCustomField7Value(request.getCustomField7Value());
        reply.setCustomField8Id(request.getCustomField8Id());
        reply.setCustomField8Label(request.getCustomField8Label());
        reply.setCustomField8Value(request.getCustomField8Value());
        reply.setCustomField9Id(request.getCustomField9Id());
        reply.setCustomField9Label(request.getCustomField9Label());
        reply.setCustomField9Value(request.getCustomField9Value());
        reply.setCustomField10Id(request.getCustomField10Id());
        reply.setCustomField10Label(request.getCustomField10Label());
        reply.setCustomField10Value(request.getCustomField10Value());
    }

    protected Correspon getReferencedCorrespon(Long id) throws ServiceAbortException {
        Correspon org = findReplyOriginalCorrespon(id);
        org.setCorresponStatus(page.getDefaultStatus());
        validateReplyOriginalCorrespon(org);
        return org;
    }

    private Correspon findReplyOriginalCorrespon(Long id) throws ServiceAbortException {
        try {
            return page.getCorresponService().find(id);
        } catch (ServiceAbortException e) {
            if (ApplicationMessageCode.NO_DATA_FOUND.equals(e.getMessageCode())) {
                throw new ServiceAbortException(
                    ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ORIGINAL_CORRESPON_NOT_EXIST);
            } else {
                throw e;
            }
        }
    }

    protected void validateReplyOriginalCorrespon(Correspon org) throws ServiceAbortException {
        // 返信元コレポン文書のプロジェクトが現在選択中のプロジェクト以外はエラー
        if (!StringUtils.equals(page.getCurrentProjectId(), org.getProjectId())) {
            throw new ServiceAbortException(
                "Project ID is invalid.",
                ApplicationMessageCode.INVALID_ORIGINAL_CORRESPON,
                org.getCorresponNo());
        }
        // 返信元コレポン文書の承認状態が
        // [5：Issued]以外に関してはエラー
        if (org.getWorkflowStatus() != WorkflowStatus.ISSUED) {
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ORIGINAL_WORKFLOW_STATUS_INVALID);
        }

        // コレポン文書状態が[0:Open]以外はエラー
        if (org.getCorresponStatus() != CorresponStatus.OPEN
            && org.getCorresponStatus() != CorresponStatus.CLOSED) {
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CORRESPON_NOT_OPENED);
        }
    }

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.view.correspon.util.setup.CorresponSetup#getPageTitle()
     */
    @Override
    protected String getPageTitle() {
        return CorresponEditPage.NEW;
    }
}
