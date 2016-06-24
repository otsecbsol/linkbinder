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

import javax.annotation.Resource;

import org.junit.Test;

import jp.co.opentone.bsol.framework.core.dao.RecordNotFoundException;
import jp.co.opentone.bsol.framework.test.AbstractDaoTestCase;
import jp.co.opentone.bsol.linkbinder.dto.Project;
import jp.co.opentone.bsol.linkbinder.dto.ProjectSummary;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchProjectCondition;

/**
 * ProjectDaoImplのテスト.
 * @author opentone
 */
public class ProjectDaoImplTest  extends AbstractDaoTestCase {

    /**
     * テスト対象.
     */
    @Resource
    private ProjectDaoImpl dao;

    /**
     * {@link jp.co.opentone.bsol.linkbinder.dao.impl.ProjectDaoImpl#findByEmpNo(java.lang.String)} のためのテスト・メソッド.
     */
    @Test
    public void testFindByEmpNo() throws Exception {
        List<Project> actual = dao.findByEmpNo("ZZA02");

        assertNotNull(actual);
        assertDataSetEquals(newDataSet("ProjectDaoImplTest_testFindByEmpNo_expected.xls"),
                            actual);
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.dao.impl.ProjectDaoImpl
     *     #find(jp.co.opentone.bsol.linkbinder.dto.condition.SearchProjectCondition)} のためのテスト・メソッド.
     */
    @Test
    public void testFind() throws Exception {
        SearchProjectCondition condition = new SearchProjectCondition();
        condition.setProjectId("0-"); // 前方一致検索
        condition.setNameE("J-K"); // 前方一致検索

        List<Project> actual = dao.find(condition);

        assertNotNull(actual);
        assertDataSetEquals(newDataSet("ProjectDaoImplTest_testFind_expected.xls"),
                            actual);
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.dao.impl.ProjectDaoImpl
     *     #find(jp.co.opentone.bsol.linkbinder.dto.condition.SearchProjectCondition)} のためのテスト・メソッド.
     */
    @Test
    public void testFindNoParameter() throws Exception {

        List<Project> actual = dao.find(new SearchProjectCondition());

        assertNotNull(actual);
        assertDataSetEquals(newDataSet("ProjectDaoImplTest_testFindNoParameter_expected.xls"),
                            actual);
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.dao.impl.ProjectDaoImpl
     *     #find(jp.co.opentone.bsol.linkbinder.dto.condition.SearchProjectCondition)} のためのテスト・メソッド.
     * ページング処理を行う.
     */
    @Test
    public void testFindPaging() throws Exception {
        SearchProjectCondition condition = new SearchProjectCondition();
        condition.setPageNo(3);
        condition.setPageRowNum(1);

        List<Project> actual = dao.find(condition);

        assertNotNull(actual);
        assertDataSetEquals(newDataSet("ProjectDaoImplTest_testFindPaging_expected.xls"),
                            actual);
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.dao.impl.ProjectDaoImpl
     *     #find(jp.co.opentone.bsol.linkbinder.dto.condition.SearchProjectCondition)} のためのテスト・メソッド.
     */
    @Test
    public void testFindNoData() throws Exception {
        SearchProjectCondition condition = new SearchProjectCondition();
        condition.setProjectId("0-"); // 前方一致検索
        condition.setNameE("Test"); // 前方一致検索

        List<Project> actual = dao.find(condition);

        assertNotNull(actual);
        assertEquals(0, actual.size());
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.dao.impl.ProjectDaoImpl#findProjectSummary(jp.co.opentone.bsol.linkbinder.dto.condition.SearchProjectCondition)} のためのテスト・メソッド.
     */
    @Test
    public void testFindProjectSummary1() throws Exception {
        SearchProjectCondition condition = new SearchProjectCondition();
        condition.setEmpNo("ZZA02");
        condition.setSystemAdmin(false);

        List<ProjectSummary> actual = dao.findProjectSummary(condition);

        assertNotNull(actual);
        assertDataSetEquals(newDataSet("ProjectDaoImplTest_testFindProjectSummary1_expected.xls"),
                            actual);
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.dao.impl.ProjectDaoImpl#findProjectSummary(jp.co.opentone.bsol.linkbinder.dto.condition.SearchProjectCondition)} のためのテスト・メソッド.
     */
    @Test
    public void testFindProjectSummary2() throws Exception {
        SearchProjectCondition condition = new SearchProjectCondition();
        condition.setEmpNo("ZZA03");
        condition.setSystemAdmin(false);

        List<ProjectSummary> actual = dao.findProjectSummary(condition);

        assertNotNull(actual);
        assertDataSetEquals(newDataSet("ProjectDaoImplTest_testFindProjectSummary2_expected.xls"),
                            actual);
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.dao.impl.ProjectDaoImpl#findProjectSummary(jp.co.opentone.bsol.linkbinder.dto.condition.SearchProjectCondition)} のためのテスト・メソッド.
     */
    @Test
    public void testFindProjectSummary3() throws Exception {
        SearchProjectCondition condition = new SearchProjectCondition();
        condition.setEmpNo("ZZA03");
        condition.setSystemAdmin(false);

        List<ProjectSummary> actual = dao.findProjectSummary(condition);

        assertNotNull(actual);
        for (ProjectSummary projectSummary : actual) {
            assertNotNull(projectSummary.getProject());
        }
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.dao.impl.ProjectDaoImpl#findProjectSummary(jp.co.opentone.bsol.linkbinder.dto.condition.SearchProjectCondition)} のためのテスト・メソッド.
     * （SystemAdmin）
     */
    @Test
    public void testFindProjectSummarySystemAdmin() throws Exception {
        SearchProjectCondition condition = new SearchProjectCondition();
        condition.setEmpNo("ZZA03");
        condition.setSystemAdmin(true);

        List<ProjectSummary> actual = dao.findProjectSummary(condition);

        assertNotNull(actual);
        System.out.println(actual);
        assertDataSetEquals(newDataSet("ProjectDaoImplTest_testFindProjectSummarySystemAdmin_expected.xls"),
                            actual);
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.dao.impl.ProjectDaoImpl#findProjectSummary(jp.co.opentone.bsol.linkbinder.dto.condition.SearchProjectCondition)} のためのテスト・メソッド.
     * （ProjectAdmin）
     */
    @Test
    public void testFindProjectSummaryProjectAdmin() throws Exception {
        SearchProjectCondition condition = new SearchProjectCondition();
        condition.setEmpNo("ZZA03");
        condition.setProjectAdmin(true);

        List<ProjectSummary> actual = dao.findProjectSummary(condition);

        assertNotNull(actual);
        assertDataSetEquals(newDataSet("ProjectDaoImplTest_testFindProjectSummaryProjectAdmin_expected.xls"),
                            actual);
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.dao.impl.ProjectDaoImpl
     *     #count(jp.co.opentone.bsol.linkbinder.dto.condition.SearchProjectCondition)} のためのテスト・メソッド.
     */
    @Test
    public void testCount() throws Exception {
        SearchProjectCondition condition = new SearchProjectCondition();
        condition.setProjectId("0-"); // 前方一致検索
        condition.setNameE("J-K"); // 前方一致検索

        int actual = dao.count(condition);

        assertEquals(1, actual);
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.dao.impl.ProjectDaoImpl
     *     #count(jp.co.opentone.bsol.linkbinder.dto.condition.SearchProjectCondition)} のためのテスト・メソッド.
     */
    @Test
    public void testCountNoParameter() throws Exception {
        int actual = dao.count(new SearchProjectCondition());

        assertEquals(23, actual);
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.dao.impl.ProjectDaoImpl
     *     #count(jp.co.opentone.bsol.linkbinder.dto.condition.SearchProjectCondition)} のためのテスト・メソッド.
     */
    @Test
    public void testCountNoData() throws Exception {
        SearchProjectCondition condition = new SearchProjectCondition();
        condition.setProjectId("0-"); // 前方一致検索
        condition.setNameE("Test"); // 前方一致検索

        int actual = dao.count(condition);

        assertEquals(0, actual);
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.dao.impl.ProjectDaoImpl
     *     #findById(String projectId)} のためのテスト・メソッド.
     */
    @Test
    public void testFindById()  throws Exception {
        String projectId = "PJ1";
        Project project = dao.findById(projectId);
        assertEquals(projectId, project.getProjectId());
        assertEquals("Test Project1", project.getNameE());
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.dao.impl.ProjectDaoImpl
     *     #findById(String projectId)} のためのテスト・メソッド.
     * 該当データなしの場合
     */
    @Test
    public void testFindByIdNoData(){
        String projectId = "PJXX";
        try{
            dao.findById(projectId);
            fail();
        } catch (RecordNotFoundException rnfe){
            // ok
        } catch (Throwable t){
            fail();
        }
    }

    @Test
    public void testCreateProject() throws Exception {

    }
}
