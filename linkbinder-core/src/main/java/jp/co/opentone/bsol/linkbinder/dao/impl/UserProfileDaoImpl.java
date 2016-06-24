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

import jp.co.opentone.bsol.linkbinder.dao.AbstractDao;
import jp.co.opentone.bsol.linkbinder.dao.UserProfileDao;
import jp.co.opentone.bsol.linkbinder.dto.UserProfile;

/**
 * user_profile を操作するDao.
 *
 * @author opentone
 *
 */
@Repository
public class UserProfileDaoImpl extends AbstractDao<UserProfile> implements UserProfileDao {
    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -8737897486918546334L;
    /**
     * このクラスが利用するsqlMap.xmlのnamespace.
     */
    private static final String NAMESPACE = "userProfile";

    /**
     * SQLID: 従業員番号を指定してユーザーを取得する.
     */
    private static final String SQL_FIND_BY_EMP_NO = "findByEmpNo";

    /**
     * 空のインスタンスを生成する.
     */
    public UserProfileDaoImpl() {
        super(NAMESPACE);
    }

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.dao.UserProfileDao#findByEmpNo(java.lang.String)
     */
    public UserProfile findByEmpNo(String empNo) {
        return (UserProfile) getSqlMapClientTemplate()
                .queryForObject(getSqlId(SQL_FIND_BY_EMP_NO), empNo);
    }
}
