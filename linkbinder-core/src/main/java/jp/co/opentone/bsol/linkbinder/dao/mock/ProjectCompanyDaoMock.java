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

import jp.co.opentone.bsol.framework.core.dao.KeyDuplicateException;
import jp.co.opentone.bsol.framework.core.dao.StaleRecordException;
import jp.co.opentone.bsol.linkbinder.dao.AbstractDao;
import jp.co.opentone.bsol.linkbinder.dao.ProjectCompanyDao;
import jp.co.opentone.bsol.linkbinder.dto.ProjectCompany;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCompanyCondition;

@Repository
public class ProjectCompanyDaoMock extends AbstractDao<ProjectCompany> implements ProjectCompanyDao {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 6343199211433767666L;

    public ProjectCompanyDaoMock() {
        super("mock");
    }

    @Override
    public Long create(ProjectCompany projectCompany) {
        // デバッグ
        System.out.println("プロジェクトID：" + projectCompany.getProjectId());
        System.out.println("会社ID：" + projectCompany.getCompanyId());
        System.out.println("作成者：" + projectCompany.getCreatedBy().getEmpNo());
        System.out.println("更新者：" + projectCompany.getUpdatedBy().getEmpNo());
        System.out.println("登録処理完了");
        return 1L;
    }

    @Override
    public ProjectCompany findById(Long id) {
        ProjectCompany pc = new ProjectCompany();
        pc.setCompanyId(1L);
        pc.setProjectId("PJ1");

        User user = new User();
        user.setEmpNo("ZZA01");
        user.setNameE("Taro Yamada");
        pc.setCreatedBy(user);
        pc.setUpdatedBy(user);
        return pc;
    }

    public Integer deleteById(ProjectCompany projectCompany) throws KeyDuplicateException, StaleRecordException {
        return projectCompany.getId().intValue();
    }

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.dao.ProjectCompanyDao#countCheck(jp.co.opentone.bsol.linkbinder.dto.condition.SearchCompanyCondition)
     */
    public int countCheck(SearchCompanyCondition condition) {
        return 0;
    }

}
