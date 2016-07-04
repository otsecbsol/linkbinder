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

import org.springframework.stereotype.Repository;

import jp.co.opentone.bsol.framework.core.dao.KeyDuplicateException;
import jp.co.opentone.bsol.framework.core.dao.StaleRecordException;
import jp.co.opentone.bsol.linkbinder.dao.AbstractDao;
import jp.co.opentone.bsol.linkbinder.dao.CompanyUserDao;
import jp.co.opentone.bsol.linkbinder.dto.CompanyUser;

/**
 * company_user を操作するDao.
 *
 * @author opentone
 *
 */
@Repository
public class CompanyUserDaoImpl extends AbstractDao<CompanyUser> implements CompanyUserDao {
    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -8737897486918546334L;
    /**
     * このクラスが利用するsqlMap.xmlのnamespace.
     */
    private static final String NAMESPACE = "companyUser";

    /**
     * SQLID: 会社IDを指定して会社ユーザーを削除する.
     */
    private static final String SQL_ID_DELETE_BY_COMPANY_ID = "deleteByCompanyId";

    /**
     * 空のインスタンスを生成する.
     */
    public CompanyUserDaoImpl() {
        super(NAMESPACE);
    }

    /*
     * (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.dao.CompanyUserDao#deleteByCompanyId(java.lang.Long)
     */
    public Integer deleteByCompanyId(CompanyUser companyUser) throws KeyDuplicateException,
        StaleRecordException {
        return getSqlMapClientTemplate().update(getSqlId(SQL_ID_DELETE_BY_COMPANY_ID), companyUser);
    }

}
