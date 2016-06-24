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
package jp.co.opentone.bsol.linkbinder.service;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;

import jp.co.opentone.bsol.framework.test.AbstractTestCase;
import jp.co.opentone.bsol.linkbinder.dto.Correspon;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.Workflow;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowType;

public class UserRoleHelperTest extends AbstractTestCase {

    /**
     * テスト対象.
     */
    @Resource
    private UserRoleHelper helper;

    /** preparer */
    private static final User PREPARER = new User();
    static {
        PREPARER.setEmpNo("USER000");
    }

    /** checker1 */
    private static final User CHECKER_1 = new User();
    static {
        CHECKER_1.setEmpNo("USER001");
    }

    /** checker2 */
    private static final User CHECKER_2 = new User();
    static {
        CHECKER_2.setEmpNo("USER002");
    }

    /** checker3 */
    private static final User CHECKER_3 = new User();
    static {
        CHECKER_3.setEmpNo("USER003");
    }

    /** approver */
    private static final User APPROVER = new User();
    static {
        APPROVER.setEmpNo("USER004");
    }

    /**
     * Preparer判定のテスト
     */
    @Test
    public void testIsWorkflowPreparer() {
        Correspon correspon = new Correspon();
        correspon.setCreatedBy(PREPARER);

        assertTrue(helper.isWorkflowPreparer(correspon, PREPARER));
        assertFalse(helper.isWorkflowPreparer(correspon, CHECKER_1));
    }

    /**
     * Checker判定のテスト.
     */
    @Test
    public void testIsWorkflowChecker() {
        Correspon correspon = new Correspon();
        correspon.setWorkflows(createWorkflowList());

        assertTrue(helper.isWorkflowChecker(correspon, CHECKER_1));
        assertTrue(helper.isWorkflowChecker(correspon, CHECKER_2));
        assertTrue(helper.isWorkflowChecker(correspon, CHECKER_3));
        assertFalse(helper.isWorkflowChecker(correspon, APPROVER));

        // 存在しないユーザ
        User user = new User();
        user.setEmpNo("ZZZZZ");
        assertFalse(helper.isWorkflowChecker(correspon, user));
    }

    /**
     * Approver判定のテスト.
     */
    @Test
    public void testIsWorkflowApprover() {
        Correspon correspon = new Correspon();
        correspon.setWorkflows(createWorkflowList());

        assertFalse(helper.isWorkflowApprover(correspon, CHECKER_1));
        assertFalse(helper.isWorkflowApprover(correspon, CHECKER_2));
        assertFalse(helper.isWorkflowApprover(correspon, CHECKER_3));
        assertTrue(helper.isWorkflowApprover(correspon, APPROVER));

        // 存在しないユーザ
        User user = new User();
        user.setEmpNo("ZZZZZ");
        assertFalse(helper.isWorkflowApprover(correspon, user));

    }

    /**
     * テスト用ワークフローリストの作成
     */
    private List<Workflow> createWorkflowList() {
        List<Workflow> list = new ArrayList<Workflow>();

        Workflow wf = new Workflow();
        wf.setId(1L);
        wf.setUser(CHECKER_1);
        wf.setWorkflowType(WorkflowType.CHECKER);
        list.add(wf);

        wf = new Workflow();
        wf.setId(2L);
        wf.setUser(CHECKER_2);
        wf.setWorkflowType(WorkflowType.CHECKER);
        list.add(wf);

        wf = new Workflow();
        wf.setId(3L);
        wf.setUser(CHECKER_3);
        wf.setWorkflowType(WorkflowType.CHECKER);
        list.add(wf);

        wf = new Workflow();
        wf.setId(4L);
        wf.setUser(APPROVER);
        wf.setWorkflowType(WorkflowType.APPROVER);
        list.add(wf);

        return list;
    }
    // TODO 新しく追加したメソッドのテストを追加する
}
