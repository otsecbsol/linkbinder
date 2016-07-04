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
package jp.co.opentone.bsol.linkbinder.service.notice.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.co.opentone.bsol.framework.core.config.SystemConfig;
import jp.co.opentone.bsol.framework.core.dao.KeyDuplicateException;
import jp.co.opentone.bsol.framework.core.dao.RecordNotFoundException;
import jp.co.opentone.bsol.framework.core.dao.StaleRecordException;
import jp.co.opentone.bsol.framework.core.exception.ApplicationFatalRuntimeException;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.core.util.ArgumentValidator;
import jp.co.opentone.bsol.framework.core.util.ConvertUtil;
import jp.co.opentone.bsol.framework.core.util.DateUtil;
import jp.co.opentone.bsol.linkbinder.dao.AddressUserDao;
import jp.co.opentone.bsol.linkbinder.dao.EmailNoticeDao;
import jp.co.opentone.bsol.linkbinder.dao.EmailNoticeRecvSettingDao;
import jp.co.opentone.bsol.linkbinder.dao.UserDao;
import jp.co.opentone.bsol.linkbinder.dao.UserProfileDao;
import jp.co.opentone.bsol.linkbinder.dao.WorkflowDao;
import jp.co.opentone.bsol.linkbinder.dto.AddressCorresponGroup;
import jp.co.opentone.bsol.linkbinder.dto.AddressUser;
import jp.co.opentone.bsol.linkbinder.dto.Correspon;
import jp.co.opentone.bsol.linkbinder.dto.EmailNoticeRecvSettingResult;
import jp.co.opentone.bsol.linkbinder.dto.PersonInCharge;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.UserProfile;
import jp.co.opentone.bsol.linkbinder.dto.Workflow;
import jp.co.opentone.bsol.linkbinder.dto.code.AddressUserType;
import jp.co.opentone.bsol.linkbinder.dto.code.EmailBodyReplaceWord;
import jp.co.opentone.bsol.linkbinder.dto.code.EmailNoticeAddressType;
import jp.co.opentone.bsol.linkbinder.dto.code.EmailNoticeCategory;
import jp.co.opentone.bsol.linkbinder.dto.code.EmailNoticeEventCd;
import jp.co.opentone.bsol.linkbinder.dto.code.EmailNoticeReceivable;
import jp.co.opentone.bsol.linkbinder.dto.code.EmailNoticeStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.EmailNoticeType;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowType;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchAddressUserCondition;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchUserCondition;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchWorkflowCondition;
import jp.co.opentone.bsol.linkbinder.dto.notice.EmailNotice;
import jp.co.opentone.bsol.linkbinder.dto.notice.EmailNoticeRecvSetting;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.service.AbstractService;
import jp.co.opentone.bsol.linkbinder.service.notice.EmailNoticeService;

/**
 * E-mail通知関連のサービスを提供する.
 *
 * @author opentone
 */
@Service
@Transactional(rollbackFor = { ServiceAbortException.class })
public class EmailNoticeServiceImpl extends AbstractService implements EmailNoticeService {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 5844500316686026479L;

    /** 単純通知一通知あたり宛先上限取得キー. */
    private static final String SIMPLE_NOTICE_TO_USER_UPPER_LIMIT_KEY =
        "notice.emailNotice.simple.upperlimit.to";

    /**
     * メールヘッダー・Fromの取得キー.
     */
    private static final String EMAIL_NOTICE_MH_FROM_KEY = "notice.emailNotice.mhFrom";

    /**
     * メールボディテンプレートファイルのパス取得キー.
     */
    private static final String EMAIL_NOTICE_MAIL_BODY_TEMPLATE_PATH_KEY
        = "notice.emailNotice.template.file";

    /**
     * メールボディテンプレートのencoding取得キー.
     */
    private static final String EMAIL_NOTICE_MAIL_BODY_ENCODING_KEY
        = "notice.emailNotice.template.file.encoding";

    /*
     * (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.service.notice.EmailNoticeService#sendWorkflowNotice
     * (jp.co.opentone.bsol.linkbinder.dto.Correspon,
     * jp.co.opentone.bsol.linkbinder.dto.code.EmailNoticeEventCd)
     */
    @Override
    public void sendWorkflowNotice(Correspon correspon, EmailNoticeEventCd emailNoticeEventCd)
            throws ServiceAbortException {
        ArgumentValidator.validateNotNull(correspon);
        ArgumentValidator.validateNotNull(emailNoticeEventCd);

        EmailNotice emailNotice = new EmailNotice();

        // 共通項目の設定
        setCommonRegisterParam(correspon,
                emailNoticeEventCd,
                emailNotice,
                EmailNoticeCategory.WORKFLOW);
        emailNotice.setEmailNoticeAddressType(EmailNoticeAddressType.TO);

        // AddressList for Checker
        List<Workflow> checkerAddressList = new ArrayList<Workflow>();

        // AddressList for Approver
        List<Workflow> approverAddressList = new ArrayList<Workflow>();

        // AddressList for Preparer
        List<EmailNoticeRecvSetting> preparerAddressList = new ArrayList<EmailNoticeRecvSetting>();

        // メール通知を「受信する」に設定しているユーザを取得する
        // ユーザーの取得先、条件はメール通知イベント毎に異なる
        if (emailNoticeEventCd == EmailNoticeEventCd.REQUESTED_FOR_APPROVAL
                || emailNoticeEventCd == EmailNoticeEventCd.CHECKED) {

            // メール通知を「受信する」に設定している Checker を取得する
            checkerAddressList = makeCheckedAddressList(correspon, WorkflowType.CHECKER);

            // メール通知を「受信する」に設定している Approver を取得する
            approverAddressList = makeCheckedAddressList(correspon, WorkflowType.APPROVER);
        } else if (emailNoticeEventCd == EmailNoticeEventCd.APPROVED
                || emailNoticeEventCd == EmailNoticeEventCd.DENIED) {

            // メール通知を「受信する」に設定している preparer を取得する
            preparerAddressList = makeApprovedAddressList(correspon);
        }

        if (preparerAddressList.size() > 0) {
            // リストが1件以上ある場合だけ処理する
            StringBuilder mhTo = new StringBuilder();
            for (int i = 0; i < preparerAddressList.size(); i++) {
                EmailNoticeRecvSetting emailNoticeRecvSetting = preparerAddressList.get(i);

                if (mhTo.length() > 0) {
                    mhTo.append(",");
                }
                mhTo.append(emailNoticeRecvSetting.getEmpNo());
            }
            emailNotice.setMhTo(mhTo.toString());

            EmailNoticeType emailNoticeType = EmailNoticeType.APPROVAL_NOTICE;
            if (emailNoticeEventCd == EmailNoticeEventCd.DENIED) {
                emailNoticeType = EmailNoticeType.DENY_NOTICE;
            }
            String mhSubject = makeSubject(correspon, emailNoticeType);
            emailNotice.setMhSubject(mhSubject);

            String mailBodyTemplate = makeMailBody(correspon);
            emailNotice.setMailBody(mailBodyTemplate);

            registerEmailNotice(emailNotice);
        }

        if (checkerAddressList.size() > 0) {
            // リストが1件以上ある場合だけ処理する

            StringBuilder mhTo = new StringBuilder();
            for (int i = 0; i < checkerAddressList.size(); i++) {
                Workflow workflow = checkerAddressList.get(i);

                if (mhTo.length() > 0) {
                    mhTo.append(",");
                }
                mhTo.append(workflow.getUser().getEmpNo());
            }
            emailNotice.setMhTo(mhTo.toString());

            String mhSubject = makeSubject(correspon,
                    EmailNoticeType.REQUEST_FOR_CHECK_NOTICE);
            emailNotice.setMhSubject(mhSubject);

            String mailBodyTemplate = makeMailBody(correspon);
            emailNotice.setMailBody(mailBodyTemplate);

            registerEmailNotice(emailNotice);
        }

        if (approverAddressList.size() > 0) {
            // リストが1件以上ある場合だけ処理する
            StringBuilder mhTo = new StringBuilder();
            for (int i = 0; i < approverAddressList.size(); i++) {
                Workflow workflow = approverAddressList.get(i);

                if (mhTo.length() > 0) {
                    mhTo.append(",");
                }
                mhTo.append(workflow.getUser().getEmpNo());
            }
            emailNotice.setMhTo(mhTo.toString());

            String mhSubject = makeSubject(correspon,
                    EmailNoticeType.REQUEST_FOR_APPROVAL_NOTICE);
            emailNotice.setMhSubject(mhSubject);

            String mailBodyTemplate = makeMailBody(correspon);
            emailNotice.setMailBody(mailBodyTemplate);

            registerEmailNotice(emailNotice);
        }
    }

    private String readDefaultMailTemplate(String encoding) {
        try {
            return IOUtils.toString(
                    getClass().getResourceAsStream("/template/mailBody.txt"),
                    encoding);
        } catch (IOException e) {
            throw new ApplicationFatalRuntimeException(e);
        }
    }

    /**
     * @param correspon
     * @return mailBody
     * @throws ServiceAbortException
     */
    private String makeMailBody(Correspon correspon) throws ServiceAbortException {

        // mailBodyテンプレートを取得する
        File path = new File(SystemConfig.getValue(EMAIL_NOTICE_MAIL_BODY_TEMPLATE_PATH_KEY));
        String encoding = SystemConfig.getValue(EMAIL_NOTICE_MAIL_BODY_ENCODING_KEY);
        String mailBodyTemplate;
        try {
            mailBodyTemplate = FileUtils.readFileToString(path, encoding);
        } catch (IOException e) {
            mailBodyTemplate = readDefaultMailTemplate(encoding);
        }

        // ID
        mailBodyTemplate = mailBodyReplace(EmailBodyReplaceWord.CORRESPON_ID.getLabel(), correspon
                .getId()
                .toString(), mailBodyTemplate);

        // No
        mailBodyTemplate = mailBodyReplace(EmailBodyReplaceWord.CORRESPON_NO.getLabel(),
                correspon.getCorresponNo(),
                mailBodyTemplate);

        // From(Group)
        mailBodyTemplate = mailBodyReplace(EmailBodyReplaceWord.FROM_GROUP.getLabel(),
                correspon.getFromCorresponGroup().getName(),
                mailBodyTemplate);

        // From
        mailBodyTemplate = mailBodyReplace(EmailBodyReplaceWord.FROM.getLabel(),
                super.getCurrentUser().getLabel(),
                mailBodyTemplate);

        // To(Group)
        List<AddressCorresponGroup> toAddressCorresponGroups = correspon
        .getToAddressCorresponGroups();
        StringBuilder mbTo = new StringBuilder();
        for (AddressCorresponGroup addressCorresponGroup : toAddressCorresponGroups) {
            if (mbTo.length() > 0) {
                mbTo.append(",");
            }
            mbTo.append(addressCorresponGroup.getCorresponGroup().getName());
        }
        mailBodyTemplate = mailBodyReplace(EmailBodyReplaceWord.TO_GROUP.getLabel(),
                mbTo.toString(),
                mailBodyTemplate);

        // Cc(Group)
        List<AddressCorresponGroup> ccAddressCorresponGroups = correspon
        .getCcAddressCorresponGroups();
        StringBuilder mbCcShort = new StringBuilder();
        if (!ccAddressCorresponGroups.isEmpty()) {
            mbCcShort.append(ccAddressCorresponGroups.get(0).getCorresponGroup().getName());
            if (ccAddressCorresponGroups.size() > 1) {
                mbCcShort.append("...");
            }
        }
        mailBodyTemplate = mailBodyReplace(EmailBodyReplaceWord.CC_GROUP.getLabel(),
                mbCcShort.toString(),
                mailBodyTemplate);

        // Correspondence Type
        StringBuilder corresponType = new StringBuilder();
        corresponType.append(correspon.getCorresponType().getLabel());
        mailBodyTemplate = mailBodyReplace(EmailBodyReplaceWord.CORRESPON_TYPE.getLabel(),
                corresponType.toString(),
                mailBodyTemplate);

        // Subject
        mailBodyTemplate = mailBodyReplace(EmailBodyReplaceWord.SUBJECT.getLabel(),
                correspon.getSubject(),
                mailBodyTemplate);

        // 承認状態
        mailBodyTemplate = mailBodyReplace(EmailBodyReplaceWord.WORKFLOW_STATUS.getLabel(),
                correspon.getWorkflowStatus().getLabel(),
                mailBodyTemplate);

        // created on
        mailBodyTemplate = mailBodyReplace(EmailBodyReplaceWord.CREATED_ON.getLabel(),
                DateUtil.convertDateToStringForView(correspon.getCreatedAt()),
                mailBodyTemplate);

        // 返信期限
        mailBodyTemplate = mailBodyReplace(EmailBodyReplaceWord.DEADLINE_FOR_REPLY.getLabel(),
                DateUtil.convertDateToStringForView(correspon.getDeadlineForReply()),
                mailBodyTemplate);

        // 作成者従業員番号
        mailBodyTemplate = mailBodyReplace(EmailBodyReplaceWord.CREATED_BY.getLabel(),
                correspon.getCreatedBy().getLabel(),
                mailBodyTemplate);

        // 発行者
        if (correspon.getIssuedBy() != null) {
            mailBodyTemplate = mailBodyReplace(EmailBodyReplaceWord.ISSUED_BY.getLabel(), correspon
                    .getIssuedBy()
                    .getLabel(), mailBodyTemplate);
        } else {
            mailBodyTemplate = mailBodyReplace(EmailBodyReplaceWord.ISSUED_BY.getLabel(),
                    "",
                    mailBodyTemplate);
        }

        // 発行日
        if (correspon.getIssuedAt() != null) {
            mailBodyTemplate = mailBodyReplace(EmailBodyReplaceWord.ISSUED_ON.getLabel(),
                    DateUtil.convertDateToStringForView(correspon.getIssuedAt()),
                    mailBodyTemplate);
        } else {
            mailBodyTemplate = mailBodyReplace(EmailBodyReplaceWord.ISSUED_ON.getLabel(),
                    "",
                    mailBodyTemplate);
        }

        // 返信要否
        mailBodyTemplate = mailBodyReplace(EmailBodyReplaceWord.REPLY_REQUIRED.getLabel(),
                correspon.getReplyRequired().getLabel(),
                mailBodyTemplate);

        // URL
        String corresponUrl = super.getBaseURL() + "/correspon/correspon.jsf?id="
                + correspon.getId() + "&projectId=" + correspon.getProjectId();
        mailBodyTemplate = mailBodyReplace(EmailBodyReplaceWord.CORRESPON_URL.getLabel(),
                corresponUrl,
                mailBodyTemplate);

        return mailBodyTemplate;
    }

    /**
     * @param replacekeyWord
     * @param replaceValue
     * @param mailBodyTemplate
     * @return result
     */
    private String mailBodyReplace(String replaceKeyword, String replaceValue,
            String mailBodyTemplate) {
        String result;
        if (StringUtils.isNotEmpty(replaceValue)) {
            result = ConvertUtil.replaceAllWithEscapedBackReference(
                            mailBodyTemplate,
                            replaceKeyword,
                            replaceValue);
        } else {
            result = ConvertUtil.replaceAllWithEscapedBackReference(
                            mailBodyTemplate,
                            replaceKeyword,
                            "");
        }
        return result;
    }

    @Override
    public void sendIssuedNotice(Correspon correspon, EmailNoticeEventCd emailNoticeEventCd,
            String[] additionalToUserIdArray)
            throws ServiceAbortException {
        ArgumentValidator.validateNotNull(correspon);
        ArgumentValidator.validateNotNull(emailNoticeEventCd);

        EmailNotice emailNotice = new EmailNotice();

        // 共通項目の設定
        setCommonRegisterParam(correspon,
                emailNoticeEventCd,
                emailNotice,
                EmailNoticeCategory.DISTRIBUTION);

        // AddressList for Attention
        List<AddressUser> attentionAddressList = makeIssuedAddressList(correspon,
                AddressUserType.ATTENTION);

        // AddressList for Cc
        List<AddressUser> ccAddressList = makeIssuedAddressList(correspon,
                AddressUserType.NORMAL_USER);

        // CC のユーザ ID が宛先に含まれている場合、CC のユーザ ID は除外する
        List<AddressUser> summarizedCcAddressList = new ArrayList<AddressUser>();
        for (AddressUser ccAddressUser : ccAddressList) {
            boolean isDublicateAddress = false;
            for (AddressUser attentionAddressUser : attentionAddressList) {
                if (attentionAddressUser
                        .getUser()
                        .getEmpNo()
                        .equals(ccAddressUser.getUser().getEmpNo())) {
                    isDublicateAddress = true;
                    break;
                }
            }
            if (!isDublicateAddress) {
                summarizedCcAddressList.add(ccAddressUser);
            }
        }
        ccAddressList.clear();
        ccAddressList.addAll(summarizedCcAddressList);

        registerEmailNotice(correspon,
                additionalToUserIdArray,
                emailNotice,
                attentionAddressList,
                EmailNoticeAddressType.TO,
                EmailNoticeType.ISSUE_NOTICE_ATTENTION);
        registerEmailNotice(correspon,
                additionalToUserIdArray,
                emailNotice,
                ccAddressList,
                EmailNoticeAddressType.CC,
                EmailNoticeType.ISSUE_NOTICE_CC);
    }

    /**
     * @param correspon
     * @param emailNoticeEventCd
     * @param emailNotice
     * @param emailNoticeCategory
     */
    private void setCommonRegisterParam(Correspon correspon, EmailNoticeEventCd emailNoticeEventCd,
            EmailNotice emailNotice, EmailNoticeCategory emailNoticeCategory) {
        emailNotice.setProjectId(correspon.getProjectId());
        emailNotice.setEmailNoticeCategory(emailNoticeCategory);
        emailNotice.setEmailNoticeEventCd(emailNoticeEventCd);
        emailNotice.setEmailNoticeStatus(EmailNoticeStatus.NEW);
        emailNotice.setMhFrom(SystemConfig.getValue(EMAIL_NOTICE_MH_FROM_KEY));
        emailNotice.setMhErrorsTo(super.getCurrentUser().getUserId());
        emailNotice.setCorresponId(correspon.getId());

        emailNotice.setCreatedBy(getCurrentUser());
        emailNotice.setUpdatedBy(getCurrentUser());
    }

    /**
     * @param correspon
     * @param additionalToUserIdArray
     * @param emailNotice
     * @param addressList
     * @param emailNoticeAddressType
     * @param emailNoticeType
     * @throws ServiceAbortException
     */
    private void registerEmailNotice(Correspon correspon, String[] additionalToUserIdArray,
            EmailNotice emailNotice, List<AddressUser> addressList,
            EmailNoticeAddressType emailNoticeAddressType, EmailNoticeType emailNoticeType)
            throws ServiceAbortException {

        if (addressList.size() > 0) {
            // リストが1件以上ある場合だけ処理する

            emailNotice.setEmailNoticeAddressType(emailNoticeAddressType);
            if (additionalToUserIdArray != null && additionalToUserIdArray.length > 0) {
                // 送信先アドレスの中から、今回新規追加する分だけに絞る
                List<AddressUser> additonalAttentionAddressList = new ArrayList<AddressUser>();
                for (int i = 0; i < addressList.size(); i++) {
                    boolean isAdditional = false;
                    AddressUser projectUser = addressList.get(i);
                    String attentionEmpNo = projectUser.getUser().getEmpNo();

                    for (int j = 0; j < additionalToUserIdArray.length; j++) {
                        String additionalEmpNo = additionalToUserIdArray[j];
                        if (attentionEmpNo.equals(additionalEmpNo)) {
                            isAdditional = true;
                            break;
                        }
                    }
                    if (isAdditional) {
                        additonalAttentionAddressList.add(projectUser);
                    }
                }
                addressList.clear();
                addressList.addAll(additonalAttentionAddressList);
            }

            // 宛先、CCについて、既定値を超える場合は分割して登録する
            StringBuilder mhTo = new StringBuilder();
            int maxMhToLimit = Integer.parseInt(SystemConfig
                    .getValue(SIMPLE_NOTICE_TO_USER_UPPER_LIMIT_KEY));

            for (int i = 0; i < addressList.size(); i++) {
                AddressUser addressUser = addressList.get(i);
                mhTo.append(addressUser.getUser().getEmpNo());

                if ((i + 1) % maxMhToLimit == 0 || (i + 1) == addressList.size()) {
                    emailNotice.setMhTo(mhTo.toString());

                    String mhSubject = makeSubject(correspon, emailNoticeType);
                    emailNotice.setMhSubject(mhSubject);

                    String mailBodyTemplate = makeMailBody(correspon);
                    emailNotice.setMailBody(mailBodyTemplate);

                    registerEmailNotice(emailNotice);
                    mhTo = new StringBuilder();
                }
                if (mhTo.length() > 0) {
                    mhTo.append(",");
                }
            }
        }
    }

    /**
     * @param correspon
     * @param workflowType
     * @return List<Workflow>
     */
    private List<Workflow> makeCheckedAddressList(Correspon correspon, WorkflowType workflowType) {
        SearchWorkflowCondition condition = new SearchWorkflowCondition();
        condition.setCorrespon(correspon);
        condition.setWorkflowType(workflowType);

        WorkflowDao dao = getDao(WorkflowDao.class);
        List<Workflow> addressList = findSendApplyUser(condition, dao);
        return addressList;
    }

    /**
     * @param correspon
     * @return checkerAddressList
     */
    private List<EmailNoticeRecvSetting> makeApprovedAddressList(Correspon correspon) {
        EmailNoticeRecvSetting condition = new EmailNoticeRecvSetting();
        condition.setProjectId(correspon.getProjectId());
        condition.setEmpNo(correspon.getCreatedBy().getEmpNo());

        EmailNoticeRecvSettingDao dao = getDao(EmailNoticeRecvSettingDao.class);
        List<EmailNoticeRecvSetting> addressList = findSendApplyUser(condition, dao);
        return addressList;
    }

    /**
     * @param correspon
     * @param addressUserType
     * @return List<AddressUser>
     */
    private List<AddressUser> makeIssuedAddressList(Correspon correspon,
            AddressUserType addressUserType) {
        SearchAddressUserCondition condition = new SearchAddressUserCondition();
        condition.setAddressCorresponGroup(new AddressCorresponGroup());
        condition.setEmailNoticeRecvSetting(new EmailNoticeRecvSetting());
        condition.setAddressUser(new AddressUser());

        condition.getAddressCorresponGroup().setCorresponId(correspon.getId());
        condition.getEmailNoticeRecvSetting().setProjectId(correspon.getProjectId());
        condition.getAddressUser().setAddressUserType(addressUserType);

        AddressUserDao dao = getDao(AddressUserDao.class);
        List<AddressUser> addressList = findSendApplyUser(condition, dao);

        // 格納されているユーザ ID をユニーク集約する
        Map<String, AddressUser> addressMap = new HashMap<String, AddressUser>();
        for (AddressUser addressUser : addressList) {
            addressMap.put(addressUser.getUser().getEmpNo(), addressUser);
        }
        addressList.clear();
        addressList.addAll(addressMap.values());

        return addressList;
    }

    /**
     * @param correspon
     * @param additionalToUserId
     * @return List<User>
     */
    private List<User> makePICAddressList(Correspon correspon,
            String additionalToUserId) {
        SearchUserCondition condition = new SearchUserCondition();

        condition.setProjectId(correspon.getProjectId());
        condition.setEmpNo(additionalToUserId);

        UserDao dao = getDao(UserDao.class);
        List<User> addressList = findSendApplyUser(condition, dao);
        return addressList;
    }

    /**
     * @param correspon
     * @return List<AddressUser>
     */
    private List<AddressUser> makeAttentionAddressListForPIC(Correspon correspon) {
        SearchAddressUserCondition condition = new SearchAddressUserCondition();
        condition.setAddressCorresponGroup(new AddressCorresponGroup());
        condition.setAddressUser(new AddressUser());
        condition.setEmailNoticeRecvSetting(new EmailNoticeRecvSetting());

        condition.getAddressCorresponGroup().setCorresponId(correspon.getId());
        condition.getAddressUser().setAddressUserType((AddressUserType.ATTENTION));
        condition.getEmailNoticeRecvSetting().setProjectId(correspon.getProjectId());

        AddressUserDao dao = getDao(AddressUserDao.class);
        List<AddressUser> addressList = findSendApplyUserForPersonInCharge(condition, dao);
        return addressList;
    }

    /**
     * @param correspon
     * @return List<AddressUser>
     */
    private List<AddressUser> makeCcAddressListForPIC(Correspon correspon) {
        SearchAddressUserCondition condition = new SearchAddressUserCondition();
        condition.setAddressCorresponGroup(new AddressCorresponGroup());
        condition.setAddressUser(new AddressUser());
        condition.setEmailNoticeRecvSetting(new EmailNoticeRecvSetting());

        condition.getAddressCorresponGroup().setCorresponId(correspon.getId());
        condition.getAddressUser().setAddressUserType((AddressUserType.NORMAL_USER));
        condition.getEmailNoticeRecvSetting().setProjectId(correspon.getProjectId());

        AddressUserDao dao = getDao(AddressUserDao.class);
        List<AddressUser> addressList = findSendApplyUserForPersonInCharge(condition, dao);
        return addressList;
    }

    /**
     * @param correspon
     * @param emailNoticeType
     * @return mhSubject
     */
    private String makeSubject(Correspon correspon, EmailNoticeType emailNoticeType) {
        String mhSubject = String.format("%s:%s: %s",
                                         correspon.getProjectId(),
                                         emailNoticeType.getLabel(),
                                         correspon.getSubject());
        return mhSubject;
    }

    private List<Workflow> findSendApplyUser(SearchWorkflowCondition condition, WorkflowDao dao) {
        return dao.findSendApplyUser(condition);
    }

    private List<User> findSendApplyUser(SearchUserCondition condition, UserDao dao) {
        return dao.findSendApplyUser(condition);
    }

    private List<EmailNoticeRecvSetting> findSendApplyUser(EmailNoticeRecvSetting condition,
            EmailNoticeRecvSettingDao dao) {
        return dao.findSendApplyUser(condition);
    }

    private List<AddressUser> findSendApplyUser(SearchAddressUserCondition condition,
            AddressUserDao dao) {
        return dao.findSendApplyUser(condition);
    }

    private List<AddressUser> findSendApplyUserForPersonInCharge(
            SearchAddressUserCondition condition,
            AddressUserDao dao) {
        return dao.findSendApplyUserForPersonInCharge(condition);
    }

    private void registerEmailNotice(EmailNotice emailNotice) throws ServiceAbortException {
        try {
            EmailNoticeDao dao = getDao(EmailNoticeDao.class);
            dao.create(emailNotice);
        } catch (KeyDuplicateException kde) {
            throw new ServiceAbortException(kde);
        }
    }

    /*
     * (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.service.notice.EmailNoticeService#
     * sendIssuedNoticeToAddtionalAddressUser(jp.co.opentone.bsol.linkbinder.dto.Correspon,
     * jp.co.opentone.bsol.linkbinder.dto.code.EmailNoticeEventCd, java.util.List)
     */
    public void sendIssuedNoticeToAddtionalAddressUser(Correspon correspon,
            EmailNoticeEventCd emailNoticeEventCd,
            List<AddressCorresponGroup> oldAddressCorresponGroupList) throws ServiceAbortException {
        ArgumentValidator.validateNotNull(correspon);
        ArgumentValidator.validateNotNull(oldAddressCorresponGroupList);
        ArgumentValidator.validateNotNull(emailNoticeEventCd);

        // 前回分の宛先ユーザIDをまとめる
        String[] oldToUserIdArray = collectUniqueUserIdOfAddressCorresponGroupList(
                oldAddressCorresponGroupList);
        // 今回分の宛先ユーザIDをまとめる
        String[] currentUserIdArray = collectUniqueUserIdOfAddressCorresponGroupList(correspon
                .getAddressCorresponGroups());
        // 今回新規に発生した宛先ユーザIDを取得する
        String[] additionalToUserIdArray = subtractOldToUserIdFromCurrent(oldToUserIdArray,
                currentUserIdArray);
        // 新規宛先が0件の場合は通知不要
        if (additionalToUserIdArray.length == 0) {
            return;
        }

        // 単純通知(発行)
        sendIssuedNotice(correspon, emailNoticeEventCd, additionalToUserIdArray);
    }

    /*
     * (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.service.notice.EmailNoticeService#
     * sendPICAssignedEmailNoticeToAdditionalPIC(jp.co.opentone.bsol.linkbinder.dto.Correspon,
     * jp.co.opentone.bsol.linkbinder.dto.code.EmailNoticeEventCd, java.util.List,
     * java.util.List)
     */
    public void sendPICAssignedEmailNoticeToAdditionalPIC(Correspon correspon,
            EmailNoticeEventCd emailNoticeEventCd, List<PersonInCharge> oldPics,
            List<PersonInCharge> newPics) throws ServiceAbortException {
        ArgumentValidator.validateNotNull(correspon);
        ArgumentValidator.validateNotNull(oldPics);
        ArgumentValidator.validateNotNull(newPics);
        ArgumentValidator.validateNotNull(emailNoticeEventCd);

        // 前回分のPICのユーザIDをまとめる
        String[] oldToUserIdArray = collectUniqueUserIdOfPersonInChargeList(oldPics);
        // 今回分のPICのユーザIDをまとめる
        String[] currentUserIdArray = collectUniqueUserIdOfPersonInChargeList(newPics);
        // 今回新規に発生したPICのユーザIDを取得する
        String[] additionalToUserIdArray = subtractOldToUserIdFromCurrent(oldToUserIdArray,
                currentUserIdArray);
        // 新規宛先がない場合は通知不要
        if (additionalToUserIdArray.length == 0) {
            return;
        }
        // 単純通知(担当者決定)
        sendPICSimpleNotice(correspon,
                additionalToUserIdArray[0],
                emailNoticeEventCd);
    }

    /**
     * @param correspon
     * @param additionalToUserId
     * @param emailNoticeEventCd
     * @throws ServiceAbortException
     */
    private void sendPICSimpleNotice(Correspon correspon, String additionalToUserId,
            EmailNoticeEventCd emailNoticeEventCd) throws ServiceAbortException {

        EmailNotice emailNotice = new EmailNotice();

        // 共通項目の設定開始
        emailNotice.setProjectId(correspon.getProjectId());
        emailNotice.setEmailNoticeCategory(EmailNoticeCategory.WORKFLOW);
        emailNotice.setEmailNoticeEventCd(emailNoticeEventCd);
        emailNotice.setEmailNoticeStatus(EmailNoticeStatus.NEW);
        emailNotice.setMhFrom(SystemConfig.getValue(EMAIL_NOTICE_MH_FROM_KEY));
        emailNotice.setMhErrorsTo(super.getCurrentUser().getUserId());
        emailNotice.setCorresponId(correspon.getId());

        emailNotice.setCreatedBy(getCurrentUser());
        emailNotice.setUpdatedBy(getCurrentUser());

        // AddressList for PIC
        List<User> personInChargeAddressList = makePICAddressList(correspon, additionalToUserId);

        // AddressList for Attention
        List<AddressUser> attentionAddressList = makeAttentionAddressListForPIC(correspon);

        // AddressList for Cc
        List<AddressUser> ccAddressList = makeCcAddressListForPIC(correspon);

        // 格納されているユーザ ID をユニーク集約する
        Map<String, AddressUser> sendAddressMap = new HashMap<String, AddressUser>();
        List<AddressUser> sendAddressList = new ArrayList<AddressUser>();
        for (User user : personInChargeAddressList) {
            AddressUser addressUserD = new AddressUser();
            addressUserD.setUser(new User());
            addressUserD.getUser().setEmpNo(user.getEmpNo());
            sendAddressMap.put(user.getEmpNo(), addressUserD);
        }
        for (AddressUser addressUser : attentionAddressList) {
            sendAddressMap.put(addressUser.getUser().getEmpNo(), addressUser);
        }
        for (AddressUser addressUser : ccAddressList) {
            sendAddressMap.put(addressUser.getUser().getEmpNo(), addressUser);
        }
        sendAddressList.addAll(sendAddressMap.values());

        registerEmailNotice(correspon,
                null,
                emailNotice,
                sendAddressList,
                EmailNoticeAddressType.TO,
                EmailNoticeType.ASSIGNMENT_NOTICE);
    }

    /**
     * 新旧のユーザID配列を比較し、新規設定となるユーザIDの配列を返す.
     *
     * @param oldToUserIdArray 旧ユーザID配列
     * @param currentUserIdArray 新ユーザID配列
     * @return 新ユーザID配列で新規設定となったユーザID
     */
    private String[] subtractOldToUserIdFromCurrent(String[] oldToUserIdArray,
            String[] currentUserIdArray) {
        List<String> ret = new ArrayList<String>();
        List<String> list = Arrays.asList(oldToUserIdArray);
        for (String currentUserId : currentUserIdArray) {
            if (!list.contains(currentUserId)) {
                ret.add(currentUserId);
            }
        }
        return ret.toArray(ArrayUtils.EMPTY_STRING_ARRAY);
    }

    /**
     * Address Correspon Groupに格納されているユーザIDをユニーク集約して返す.
     *
     * @param addressCorresponGroups ユーザID集約対象のAddress Correspon Group
     * @return ユニーク集約化されたユーザID
     */
    private String[] collectUniqueUserIdOfAddressCorresponGroupList(
            List<AddressCorresponGroup> addressCorresponGroups) {
        if (addressCorresponGroups == null) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        }
        Set<String> uniqueIdSet = new HashSet<String>();
        for (AddressCorresponGroup group : addressCorresponGroups) {
            if (group.getUsers() == null) {
                continue;
            }
            for (AddressUser user : group.getUsers()) {
                uniqueIdSet.add(user.getUser().getEmpNo());
            }
        }
        String[] ret = uniqueIdSet.toArray(ArrayUtils.EMPTY_STRING_ARRAY);
        Arrays.sort(ret);
        return ret;
    }

    /**
     * PICのリストに格納されているユーザIDをユニーク集約して返す.
     *
     * @param pics ユーザID集約対象のPersonInChargeのリスト
     * @return ユニーク集約化されたユーザID
     */
    private String[] collectUniqueUserIdOfPersonInChargeList(List<PersonInCharge> pics) {
        Set<String> uniqueIdSet = new HashSet<String>();
        for (PersonInCharge pic : pics) {
            uniqueIdSet.add(pic.getUser().getEmpNo());
        }
        String[] ret = uniqueIdSet.toArray(ArrayUtils.EMPTY_STRING_ARRAY);
        Arrays.sort(ret);
        return ret;
    }

    /*
     * (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.service.notice.EmailNoticeService#findEmailNoticeRecvSetting
     * (java.lang.String)
     */
    public EmailNoticeRecvSettingResult findEmailNoticeRecvSetting(String empNo)
            throws ServiceAbortException {
        ArgumentValidator.validateNotNull(empNo);

        EmailNoticeRecvSettingResult result = new EmailNoticeRecvSettingResult();

        // ユーザ取得
        User user = findUser(empNo);

        UserProfileDao userProfileDao = getDao(UserProfileDao.class);
        result.setUserProfile(userProfileDao.findByEmpNo(user.getEmpNo()));
        EmailNoticeRecvSettingDao emailNoticeRecvDao = getDao(EmailNoticeRecvSettingDao.class);
        result.setEmailNoticeRecvSettingList(
                emailNoticeRecvDao.findByEmpNo(user.getEmpNo()));

        return result;
    }

    private User findUser(String userId) throws ServiceAbortException {
        try {
            UserDao dao = getDao(UserDao.class);
            return dao.findByEmpNo(userId);
        } catch (RecordNotFoundException e) {
            throw new ServiceAbortException(ApplicationMessageCode.NO_DATA_FOUND);
        }
    }

    /*
     * (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.service.notice.EmailNoticeService#save(java.util.List,
     * jp.co.opentone.bsol.linkbinder.dto.UserProfile)
     */
    public void save(List<EmailNoticeRecvSetting> recvSetting, UserProfile userProfile)
            throws ServiceAbortException {

        // ID非存在の判定用
        Long unExistId = new Long("0");
        // userProfileレコードがない場合の処理
        if (userProfile == null  ) {
            userProfile =  new UserProfile();
            userProfile.setUser(new User());
        }


        //  排他制御のため、常にバージョンNoも更新
        saveUserProfile(userProfile);

        EmailNoticeRecvSettingDao dao = getDao(EmailNoticeRecvSettingDao.class);

        for (int i = 0; i < recvSetting.size(); i++) {
            // IDが存在しない場合Insert
            if (null == recvSetting.get(i).getId()
                    || recvSetting.get(i).getId().compareTo(unExistId) == 0) {

                EmailNoticeRecvSetting entity = new EmailNoticeRecvSetting();

                entity.setProjectId(recvSetting.get(i).getProjectId());
                entity.setEmpNo(userProfile.getUser().getEmpNo());
                entity.setCreatedBy(getCurrentUser());
                entity.setUpdatedBy(getCurrentUser());
                // 通知設定
                if (recvSetting.get(i).isReceiveWorkflowChk()) {
                    entity.setReceiveWorkflow(EmailNoticeReceivable.YES);
                } else {
                    entity.setReceiveWorkflow(EmailNoticeReceivable.NO);
                }
                if (recvSetting.get(i).isRecvDistributionAttentionChk()) {
                    entity.setRecvDistributionAttention(EmailNoticeReceivable.YES);
                } else {
                    entity.setRecvDistributionAttention(EmailNoticeReceivable.NO);
                }
                if (recvSetting.get(i).isRecvDistributionCcChk()) {
                    entity.setRecvDistributionCc(EmailNoticeReceivable.YES);
                } else {
                    entity.setRecvDistributionCc(EmailNoticeReceivable.NO);
                }

                try {
                    dao.create(entity);
                } catch (KeyDuplicateException e) {
                    throw new ServiceAbortException(e);
                }

            } else {
                // レコードが既に存在する場合update
                EmailNoticeRecvSetting entity = new EmailNoticeRecvSetting();

                entity.setId(recvSetting.get(i).getId());
                entity.setProjectId(recvSetting.get(i).getProjectId());
                entity.setEmpNo(userProfile.getUser().getEmpNo());
                entity.setUpdatedBy(getCurrentUser());
                // 通知設定
                if (recvSetting.get(i).isReceiveWorkflowChk()) {
                    entity.setReceiveWorkflow(EmailNoticeReceivable.YES);
                } else {
                    entity.setReceiveWorkflow(EmailNoticeReceivable.NO);
                }
                if (recvSetting.get(i).isRecvDistributionAttentionChk()) {
                    entity.setRecvDistributionAttention(EmailNoticeReceivable.YES);
                } else {
                    entity.setRecvDistributionAttention(EmailNoticeReceivable.NO);
                }
                if (recvSetting.get(i).isRecvDistributionCcChk()) {
                    entity.setRecvDistributionCc(EmailNoticeReceivable.YES);
                } else {
                    entity.setRecvDistributionCc(EmailNoticeReceivable.NO);
                }

                try {
                    dao.update(entity);
                } catch (KeyDuplicateException e) {
                    throw new ServiceAbortException(e);
                } catch (StaleRecordException e) {
                    throw new ServiceAbortException(e);
                }
            }
        }
    }

    private void saveUserProfile(UserProfile userProfile) throws ServiceAbortException {
        UserProfile p = findUserProfile(userProfile.getUser().getEmpNo());
        if (p != null) {
            //  更新前のバージョンNoを再設定して更新
            p.setVersionNo(userProfile.getVersionNo());
            updateUserProfile(p, userProfile.getDefaultProjectId());
        }
    }

    private UserProfile findUserProfile(String userId) {
        UserProfileDao dao = getDao(UserProfileDao.class);
        return dao.findByEmpNo(userId);
    }

    private void updateUserProfile(UserProfile old, String projectId) throws ServiceAbortException {
        UserProfile profile = new UserProfile();
        profile.setId(old.getId());
        profile.setVersionNo(old.getVersionNo());
        profile.setDefaultProjectId(projectId);
        profile.setUpdatedBy(getCurrentUser());

        UserProfileDao dao = getDao(UserProfileDao.class);
        try {
            dao.update(profile);
        } catch (KeyDuplicateException e) {
            throw new ServiceAbortException(e);
        } catch (StaleRecordException e) {
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_USER_SETTINGS_ALREADY_UPDATED);
        }
    }

}
