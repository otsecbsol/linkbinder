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

import java.util.Arrays;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.co.opentone.bsol.framework.core.dao.KeyDuplicateException;
import jp.co.opentone.bsol.framework.core.dao.RecordNotFoundException;
import jp.co.opentone.bsol.framework.core.dao.StaleRecordException;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.core.util.DateUtil;
import jp.co.opentone.bsol.framework.core.util.LogUtil;
import jp.co.opentone.bsol.linkbinder.dao.EmailNoticeDao;
import jp.co.opentone.bsol.linkbinder.dao.UserDao;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.code.EmailNoticeStatus;
import jp.co.opentone.bsol.linkbinder.dto.notice.EmailNotice;
import jp.co.opentone.bsol.linkbinder.service.AbstractService;
import jp.co.opentone.bsol.linkbinder.service.notice.SendMailService;

/**
 * メール送信サービス.
 * @author opentone
 */
@Service
@Transactional(rollbackFor = { ServiceAbortException.class })
public class SendMailServiceImpl extends AbstractService implements SendMailService {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 5844500316686026479L;

    /** logger. */
    private static Logger LOG = LogUtil.getLogger();

    /** メール送信オブジェクト. */
    @Autowired
    private JavaMailSender mailSender;

    /* (非 Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.service.notice
     *      .SendMailService#sendMailForCorrespon(java.lang.Long)
     */
    @Override
    public void sendMailForCorrespon(Long corresponId) throws ServiceAbortException {
        EmailNoticeDao dao = getDao(EmailNoticeDao.class);
        dao.findByCorresponId(corresponId, EmailNoticeStatus.NEW)
            .forEach(m -> {
                if (sendMail(m)) {
                    updateResult(m, EmailNoticeStatus.NOTIFIED);
                } else {
                    updateResult(m, EmailNoticeStatus.FAILED);
                }
            });
    }

    public boolean sendMail(EmailNotice emailNotice) {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        try {
            helper.setSubject(emailNotice.getMhSubject());
            helper.setText(emailNotice.getMailBody());

            String[] addresses = convertEmpNoToMailAddress(emailNotice.getMhTo());
            if (addresses.length == 0) {
                LOG.warn("toが設定されていない、または該当ユーザーのメールアドレスが設定されていないため"
                        + "メールを送信できません。email_notice.id = {}", emailNotice.getId());
                return false;
            }
            helper.setBcc(addresses);

//            String [] from = convertEmpNoToMailAddress(emailNotice.getMhFrom());
//            if (from.length == 0) {
//                LOG.warn("fromが設定されていない、または該当ユーザーのメールアドレスが設定されていないため"
//                        + "メールを送信できません。email_notice.id = {}", emailNotice.getId());
//                return false;
//            }
            helper.setFrom(emailNotice.getMhFrom());

            mailSender.send(message);

            return true;
        } catch (MessagingException e) {
            LOG.warn("メール送信に失敗しました", e);
            return false;
        }
    }

    private void updateResult(EmailNotice emailNotice, EmailNoticeStatus status) {
        emailNotice.setEmailNoticeStatus(status);
        if (EmailNoticeStatus.NOTIFIED == status) {
            emailNotice.setNotifiedAt(DateUtil.getNow());
        }

        EmailNoticeDao dao = getDao(EmailNoticeDao.class);
        try {
            dao.update(emailNotice);
        } catch (KeyDuplicateException | StaleRecordException e) {
            LOG.warn("メールステータスが更新できません", e);
        }
    }

    private String[] convertEmpNoToMailAddress(String empNoCsvList) {
        return Arrays.stream(StringUtils.split(empNoCsvList, ','))
            .map(this::findMailAddressByEmpNo)
            .filter(StringUtils::isNotEmpty)
            .toArray(String[]::new);
    }

    private String findMailAddressByEmpNo(String empNo) {
        UserDao dao = getDao(UserDao.class);
        try {
            User user = dao.findByEmpNo(empNo);
            return user.getEmailAddress();
        } catch (RecordNotFoundException e) {
            return null;
        }
    }
}
