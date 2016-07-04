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
package jp.co.opentone.bsol.linkbinder.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import jp.co.opentone.bsol.linkbinder.dao.AbstractDao;
import jp.co.opentone.bsol.linkbinder.dao.EmailNoticeRecvSettingDao;
import jp.co.opentone.bsol.linkbinder.dto.notice.EmailNoticeRecvSetting;

/**
 * EmailNoticeRecvSetting を操作するDao.
 *
 * @author opentone
 *
 */
@Repository
public class EmailNoticeRecvSettingDaoImpl extends AbstractDao<EmailNoticeRecvSetting> implements
        EmailNoticeRecvSettingDao {

    /**
     * SQLID: メール受信設定検索条件を指定してメール受信設定を取得.
     */
    private static final String SQL_FIND_SEND_APPLY_USER = "findSendApplyUser";

    /**
     * SQLID: 社員番号を指定してメール受信設定を取得.
     */
    private static final String SQL_FIND_BY_EMP_NO = "findByEmpNo";

    /**
     *
     */
    private static final long serialVersionUID = -2261817244953101214L;

    /**
     * このクラスが利用するsqlMap.xmlのnamespace.
     */
    private static final String NAMESPACE = "emailNoticeRecvSetting";

    /**
     * 空のインスタンスを生成する.
     *
     * @param namespace
     */
    public EmailNoticeRecvSettingDaoImpl() {
        super(NAMESPACE);
    }

    /*
     * (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.dao.EmailNoticeRecvSettingDao#findSendApplyUser(
     *  jp.co.opentone.bsol.linkbinder.dto.notice.EmailNoticeRecvSetting)
     *
     */
    @SuppressWarnings("unchecked")
    public List<EmailNoticeRecvSetting> findSendApplyUser(EmailNoticeRecvSetting condition) {
        return getSqlMapClientTemplate()
                .queryForList(getSqlId(SQL_FIND_SEND_APPLY_USER), condition);
    }

    /*
     * (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.dao.EmailNoticeRecvSettingDao#findByEmpNo(java.lang.
     * String)
     */
    @SuppressWarnings("unchecked")
    public List<EmailNoticeRecvSetting> findByEmpNo(String empNo) {
        return getSqlMapClientTemplate()
                .queryForList(getSqlId(SQL_FIND_BY_EMP_NO), empNo);
    }
}
