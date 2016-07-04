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
package jp.co.opentone.bsol.linkbinder.dao;

import java.util.List;

import jp.co.opentone.bsol.framework.core.dao.GenericDao;
import jp.co.opentone.bsol.linkbinder.dto.code.EmailNoticeStatus;
import jp.co.opentone.bsol.linkbinder.dto.notice.EmailNotice;

/**
 * EmailNotice を操作するDao.
 *
 * @author opentone
 *
 */
public interface EmailNoticeDao extends GenericDao<EmailNotice> {

    /**
     * 指定された文書IDのメール情報を検索する.
     * @param corresponId 文書ID
     * @param statuses メールステータス
     * @return メール情報
     */
    List<EmailNotice> findByCorresponId(Long corresponId, EmailNoticeStatus... statuses);
}
