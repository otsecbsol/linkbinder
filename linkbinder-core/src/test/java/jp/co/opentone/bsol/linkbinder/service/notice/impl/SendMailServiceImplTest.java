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

import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import jp.co.opentone.bsol.framework.test.AbstractTestCase;
import jp.co.opentone.bsol.linkbinder.dto.notice.EmailNotice;

/**
 * @author opentone
 */
public class SendMailServiceImplTest extends AbstractTestCase {

    @Autowired
    private SendMailServiceImpl service;

    @Test
    public void testSendMail() {
        EmailNotice notice = new EmailNotice();
        notice.setId(1L);
        notice.setMhSubject("メール送信テスト");
        notice.setMailBody("これはテストです");
        notice.setMhFrom("00000");
        notice.setMhTo("00000,00001");

        assertTrue(service.sendMail(notice));
    }
}
