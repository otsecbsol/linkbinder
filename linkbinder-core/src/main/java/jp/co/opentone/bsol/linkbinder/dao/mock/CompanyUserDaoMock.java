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
package jp.co.opentone.bsol.linkbinder.dao.mock;

import org.springframework.stereotype.Repository;

import jp.co.opentone.bsol.linkbinder.dao.AbstractDao;
import jp.co.opentone.bsol.linkbinder.dto.CompanyUser;

/**
 *
 * @author opentone
 *
 */
@Repository
public class CompanyUserDaoMock extends AbstractDao<CompanyUser> {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 5368923504296169447L;

    public CompanyUserDaoMock() {
        super("mock");
    }

    @Override
    public Long create(CompanyUser companyUser) {
        System.out.println("登録成功："+ companyUser.getId());
        return companyUser.getId();
    }

    public Integer deleteByCompanyId(Long companyId) {
        return 1;
    }
}
