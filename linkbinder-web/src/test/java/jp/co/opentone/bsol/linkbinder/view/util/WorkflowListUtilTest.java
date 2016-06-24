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
package jp.co.opentone.bsol.linkbinder.view.util;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.Workflow;

/**
 * {@link CreateDisplayWorkflowList}のテストケース.
 * @author opentone
 */
public class WorkflowListUtilTest {

    /**
     * {@link WorkflowListUtil#renumberWorkflowNo}の検証.
     * @throws Exception
     */
    @Test
    public void testRenumberWorkflowNo() throws Exception {
        List<Workflow> workflows = new ArrayList<Workflow>();
        Workflow workflow = new Workflow();

        // 比較用
        List<Workflow> workflowsKeep = new ArrayList<Workflow>();
        Workflow workflowKeep = new Workflow();

        User user = new User();
        User userKeep = new User();

        user.setEmpNo("Test1");
        user.setNameE("Test Ichiro");
        userKeep.setEmpNo("Test1");
        userKeep.setNameE("Test Ichiro");

        workflow.setWorkflowNo(10L);
        workflow.setUser(user);
        workflowKeep.setWorkflowNo(10L);
        workflowKeep.setUser(userKeep);
        workflows.add(workflow);
        workflowsKeep.add(workflowKeep);

        workflow = new Workflow();
        user = new User();
        workflowKeep = new Workflow();
        userKeep = new User();

        user.setEmpNo("Test2");
        user.setNameE("Test Jiro");
        userKeep.setEmpNo("Test2");
        userKeep.setNameE("Test Jiro");

        workflow.setWorkflowNo(7L);
        workflow.setUser(user);
        workflowKeep.setWorkflowNo(7L);
        workflowKeep.setUser(userKeep);

        workflows.add(workflow);
        workflowsKeep.add(workflowKeep);

        workflow = new Workflow();
        user = new User();
        workflowKeep = new Workflow();
        userKeep = new User();

        user.setEmpNo("Test3");
        user.setNameE("Test Saburo");
        userKeep.setEmpNo("Test3");
        userKeep.setNameE("Test Saburo");

        workflow.setWorkflowNo(90L);
        workflow.setUser(user);
        workflowKeep.setWorkflowNo(90L);
        workflowKeep.setUser(userKeep);

        workflows.add(workflow);
        workflowsKeep.add(workflowKeep);

        WorkflowListUtil.renumberWorkflowNo(workflows);

        assertEquals(3, workflows.size());

        assertEquals(String.valueOf(1L), workflows.get(0).getWorkflowNo().toString());
        assertEquals(workflowsKeep.get(0).getUser().getEmpNo(), workflows.get(0).getUser().getEmpNo());
        assertEquals(workflowsKeep.get(0).getUser().getNameE(), workflows.get(0).getUser().getNameE());

        assertEquals(String.valueOf(2L), workflows.get(1).getWorkflowNo().toString());
        assertEquals(workflowsKeep.get(1).getUser().getEmpNo(), workflows.get(1).getUser().getEmpNo());
        assertEquals(workflowsKeep.get(1).getUser().getNameE(), workflows.get(1).getUser().getNameE());

        assertEquals(String.valueOf(3L), workflows.get(2).getWorkflowNo().toString());
        assertEquals(workflowsKeep.get(2).getUser().getEmpNo(), workflows.get(2).getUser().getEmpNo());
        assertEquals(workflowsKeep.get(2).getUser().getNameE(), workflows.get(2).getUser().getNameE());

    }



//    /**
//     * {@link CloneUtil#cloneDate(Date)}の検証.
//     */
//    @Test
//    public void testCreateDisplayWorkflowList() {
//        List<Workflow> workflows = new ArrayList<Workflow>();
//
//        User user = new User();
//        user.setEmpNo("E0001");
//
//        Correspon correspon = new Correspon();
//        correspon.setCreatedBy(user);
//
//        Workflow workflow = new Workflow();
//        user = new User();
//        user.setEmpNo("E0002");
//        workflow.setUser(user);
//        workflow.setWorkflowNo(1L);
//        workflow.setWorkflowType(WorkflowType.CHECKER);
//        workflows.add(workflow);
//
//        workflow = new Workflow();
//        user = new User();
//        user.setEmpNo("E0003");
//        workflow.setUser(user);
//        workflow.setWorkflowNo(2L);
//        workflow.setWorkflowType(WorkflowType.APPROVER);
//        workflows.add(workflow);
//
//        correspon.setWorkflows(workflows);
//
//        List<Workflow> r_workflow = WorkflowListUtil.createDisplayWorkflowList(correspon);
//
//        assertEquals(correspon.getWorkflows().size() + 1, r_workflow.size());
//
//        for (int i = 0; i < r_workflow.size(); i++) {
//            if (i == 0) {
//                assertEquals("E0001", r_workflow.get(i).getUser().getEmpNo());
//                assertEquals(null, r_workflow.get(i).getWorkflowType());
//                assertEquals("1", r_workflow.get(i).getWorkflowNo().toString());
//            } else {
//                assertEquals(correspon.getWorkflows().get(i - 1).getUser().getEmpNo(), r_workflow.get(i).getUser().getEmpNo());
//                assertEquals(correspon.getWorkflows().get(i - 1).getWorkflowType(), r_workflow.get(i).getWorkflowType());
//                assertEquals(correspon.getWorkflows().get(i - 1).getWorkflowNo().toString(), r_workflow.get(i).getWorkflowNo().toString());
//            }
//
//        }
//    }
}
