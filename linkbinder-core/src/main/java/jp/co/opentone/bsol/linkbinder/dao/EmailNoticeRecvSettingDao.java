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
import jp.co.opentone.bsol.linkbinder.dto.notice.EmailNoticeRecvSetting;

/**
 * メール通知受信設定 を操作するDao.
 *
 * @author opentone
 *
 */
public interface EmailNoticeRecvSettingDao extends GenericDao<EmailNoticeRecvSetting> {

    /**
     * 指定されたメール通知受信設定検索条件に設定された全てのメール通知受信設定を返す.
     *
     * @param condition メール通知受信設定検索条件
     * @return メール通知受信設定
     */
    List<EmailNoticeRecvSetting> findSendApplyUser(EmailNoticeRecvSetting condition);

    /**
     * 指定された社員番号に設定されたメール通知受信設定とプロジェクト情報を返す.
     *
     * @param empNo 社員番号
     * @return メール通知受信設定
     */
    List<EmailNoticeRecvSetting> findByEmpNo(String empNo);

}
