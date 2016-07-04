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

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import jp.co.opentone.bsol.framework.core.dao.RecordNotFoundException;
import jp.co.opentone.bsol.linkbinder.dao.AbstractDao;
import jp.co.opentone.bsol.linkbinder.dao.CompanyDao;
import jp.co.opentone.bsol.linkbinder.dto.Company;
import jp.co.opentone.bsol.linkbinder.dto.CompanyUser;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCompanyCondition;

@Repository
public class CompanyDaoMock extends AbstractDao<Company> implements CompanyDao {

    /**
     * serialVersionUID.
     */
    private static final long serialVersionUID = -1160725424163794251L;

    public CompanyDaoMock() {
        super("mock");
    }

    public List<Company> find(SearchCompanyCondition condition) {
        List<Company> companyList = new ArrayList<Company>();
        Company com = new Company();

        // マスタ管理かプロジェクトマスタ管理か判定する
        if (condition.getProjectId() == null) {
            com.setCompanyCd("OT");
            com.setId(1L);
            com.setName("Open Tone Inc.");
            com.setRole("Owner");
            com.setVersionNo(1L);

            companyList.add(com);

            com = new Company();
            com.setCompanyCd("AAA");
            com.setId(2L);
            com.setName("Capsule Corporation");
            com.setRole("Owner");
            com.setVersionNo(1L);

            companyList.add(com);

            com = new Company();
            com.setCompanyCd("BBB");
            com.setId(3L);
            com.setName("Kaisya Corporation");
            com.setRole("Owner");
            com.setVersionNo(1L);

            companyList.add(com);

            long count = 4;
            for (int i = 0; i < 11; i++) {
                com = new Company();
                com.setCompanyCd("BBB");
                com.setId(count++);
                com.setName("Kaisya Corporation");
                com.setRole("Owner");
                com.setVersionNo(1L);

                companyList.add(com);

            }

        } else {
            com.setCompanyCd("ZZZ");
            com.setId(1L);
            com.setName("Open Tone Inc.");
            com.setVersionNo(1L);

            companyList.add(com);

            com = new Company();
            com.setCompanyCd("YYY");
            com.setId(2L);
            com.setName("Capsule Company");
            com.setVersionNo(1L);

            companyList.add(com);

            com = new Company();
            com.setCompanyCd("XXX");
            com.setId(3L);
            com.setName("Kaisya Company");
            com.setVersionNo(1L);

            companyList.add(com);

            com = new Company();
            com.setCompanyCd("VVV");
            com.setId(4L);
            com.setName("WorldWide Company");
            com.setVersionNo(1L);

            companyList.add(com);

        }

        return companyList;
    }

    public int countCompany(SearchCompanyCondition condition) {
        if (condition.getProjectId() == null) {
            return 14;
        } else {
            return 4;
        }
    }

    public List<Company> findNotAssignTo(String projectId) {
        Company com = new Company();
        List<Company> companyList = new ArrayList<Company>();

        com.setCompanyCd("ABC");
        com.setId(1L);
        com.setName("ABC Company");
        com.setVersionNo(1L);

        companyList.add(com);

        com = new Company();
        com.setCompanyCd("DEF");
        com.setId(2L);
        com.setName("DEF Company");
        com.setVersionNo(1L);

        companyList.add(com);

        com = new Company();
        com.setCompanyCd("GHI");
        com.setId(3L);
        com.setName("GHI Company");
        com.setVersionNo(1L);

        companyList.add(com);

        com = new Company();
        com.setCompanyCd("JKL");
        com.setId(4L);
        com.setName("JKL Company");
        com.setVersionNo(1L);

        companyList.add(com);
        return companyList;

    }

    @Override
    public Long create(Company company) {
        System.out.println("登録成功：" + company.getId());
        return company.getId();
    }

    public Company findProjectCompanyById(Long id, String projectId) throws RecordNotFoundException{
        Company com = new Company();

        com.setCompanyCd("OT");
        com.setId(1L);
        com.setName("Open Tone Inc.");
        com.setRole("Owner");
        com.setVersionNo(1L);

        return com;
    }

    public List<CompanyUser> findMembers(Long projectCompanyId){
        List<CompanyUser> listCu = new ArrayList<CompanyUser>();
        CompanyUser cu = new CompanyUser();
        cu.setId(1L);
        listCu.add(cu);

        cu = new CompanyUser();
        cu.setId(2L);
        listCu.add(cu);

        cu = new CompanyUser();
        cu.setId(3L);
        listCu.add(cu);
        return listCu;

    }

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.dao.CompanyDao#countCheck(jp.co.opentone.bsol.linkbinder.dto.condition.SearchCompanyCondition)
     */
    public int countCheck(SearchCompanyCondition condition) {
        return 0;
    }

}
