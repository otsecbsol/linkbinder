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
package jp.co.opentone.bsol.linkbinder.service.notice;

import jp.co.opentone.bsol.framework.core.service.IService;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;

/**
 * メール送信サービス.
 * @author opentone
 */
public interface SendMailService extends IService {

    /**
     * 指定された文書のメールを送信する.
     * 事前にメールキュー(email_noticeテーブル)にレコードが作成されていることが前提.
     * @param corresponId 文書ID
     * @throws ServiceAbortException 処理に失敗
     */
    void sendMailForCorrespon(Long corresponId) throws ServiceAbortException;
}
