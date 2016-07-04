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

import jp.co.opentone.bsol.framework.core.dao.GenericDao;
import jp.co.opentone.bsol.framework.core.dao.KeyDuplicateException;
import jp.co.opentone.bsol.framework.core.dao.StaleRecordException;
import jp.co.opentone.bsol.linkbinder.dto.CompanyUser;

/**
 * company_user を操作するDao.
 *
 * @author opentone
 *
 */
public interface CompanyUserDao extends GenericDao<CompanyUser> {

    /**
     * 会社情報IDで会社ユーザーを削除する.
     * @param companyUser
     *            会社ユーザー
     * @return 削除した件数
     * @throws KeyDuplicateException エラー発生時
     * @throws StaleRecordException 排他制御に引っかかった場合
     */
    Integer deleteByCompanyId(CompanyUser companyUser) throws KeyDuplicateException,
        StaleRecordException;
}
