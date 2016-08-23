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

import jp.co.opentone.bsol.framework.core.dao.KeyDuplicateException;
import jp.co.opentone.bsol.framework.core.dao.RecordNotFoundException;
import jp.co.opentone.bsol.linkbinder.dao.AbstractLegacyDao;
import jp.co.opentone.bsol.linkbinder.dao.ProjectDao;
import jp.co.opentone.bsol.linkbinder.dto.Project;
import jp.co.opentone.bsol.linkbinder.dto.ProjectSummary;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchAvailableSystemCondition;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchProjectCondition;

/**
 * ProjectDaoのMockクラス.
 *
 * @author opentone
 *
 */
@Repository
public class ProjectDaoMock extends AbstractLegacyDao<Project> implements ProjectDao {

    public ProjectDaoMock() {
        super("mock");
    }

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 1158533541650152561L;

    public List<Project> findByEmpNo(String empNo) {
        List<Project> projects = new ArrayList<Project>();
        Project p;

        p = new Project();
        p.setProjectId("PJ1");
        p.setNameE("Project 1(Mock)");
        projects.add(p);

        p = new Project();
        p.setProjectId("PJ2");
        p.setNameE("Project 2(Mock)");
        projects.add(p);

        p = new Project();
        p.setProjectId("PJ3");
        p.setNameE("Project 3(Mock)");
        projects.add(p);

        return projects;
    }

    public List<Project> find(SearchProjectCondition condition) {
        List<Project> projects = new ArrayList<Project>();
        Project p;

        p = new Project();
        p.setProjectId("PJ1");
        p.setNameE("Project 1(Mock)");
        projects.add(p);

        p = new Project();
        p.setProjectId("PJ2");
        p.setNameE("Project 2(Mock)");
        projects.add(p);

        p = new Project();
        p.setProjectId("PJ3");
        p.setNameE("Project 3(Mock)");
        projects.add(p);

        return projects;
    }

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.dao.ProjectDao#findProjectSummary(java.lang.String)
     */
    public List<ProjectSummary> findProjectSummary(SearchProjectCondition condition) {
        return new ArrayList<ProjectSummary>();
    }

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.dao.ProjectDao#count(jp.co.opentone.bsol.linkbinder.dto.condition.SearchProjectCondition)
     */
    public int count(SearchProjectCondition condition) {
        return 10;
    }

    /*
     * (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.dao.ProjectDao#findById(java.lang.String)
     */
    public Project findById(String id) {
        Project p;

        p = new Project();
        p.setProjectId("PJ1");
        p.setNameE("Project 1(Mock)");
        return p;
    }

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.dao.ProjectDao#countAvailableAppSystemCode(SearchAuthenticatedAplSystemCodeCondition)
     */
    public int countAvailableAppSystemCode(SearchAvailableSystemCondition condition) {
        return 0;
    }



    public void createProject(Project project) throws KeyDuplicateException {
        // TODO 自動生成されたメソッド・スタブ
    }

    public void updateProject(Project project) throws KeyDuplicateException {
        // TODO 自動生成されたメソッド・スタブ
//        return 0;
    }

    public void deleteProject(Project project) throws KeyDuplicateException {
        // TODO 自動生成されたメソッド・スタブ
//        return 0;
    }

    public String findBySysPJId(String id) throws RecordNotFoundException {
        // TODO 自動生成されたメソッド・スタブ
        return null;
    }

    @Override
    public List<Project> findAll() {
        // TODO 自動生成されたメソッド・スタブ
        return null;
    }

    @Override
    public  List<Project> findAll(SearchProjectCondition condition) {
        return null;
    }

    @Override
    public int count() {
        // TODO 自動生成されたメソッド・スタブ
        return 0;
    }
}
