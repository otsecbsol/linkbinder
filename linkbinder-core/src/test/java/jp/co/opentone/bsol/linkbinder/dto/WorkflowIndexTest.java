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
package jp.co.opentone.bsol.linkbinder.dto;

import static org.junit.Assert.*;

import org.junit.Test;

import jp.co.opentone.bsol.framework.test.AbstractTestCase;
import jp.co.opentone.bsol.linkbinder.dto.code.AllowApproverToBrowse;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowProcessStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowType;

public class WorkflowIndexTest extends AbstractTestCase {

    /**
     * 表示設定のテスト. パターン1で検証前.
     */
    @Test
    public void testWorkflowIndex1_1() {
        // Preparer
        CorresponType ct = new CorresponType();
        WorkflowPattern wp = new WorkflowPattern();
        wp.setWorkflowCd("001");
        ct.setWorkflowPattern(wp);
        ct.setAllowApproverToBrowse(AllowApproverToBrowse.INVISIBLE);
        WorkflowIndex preparer =
                new WorkflowIndex(createWorkflow(null, null), WorkflowStatus.REQUEST_FOR_CHECK,
                                 ct, false, "USER1");

        assertTrue(preparer.isView());
        assertFalse(preparer.isUpdate());
        assertFalse(preparer.isCheckApprove());


        assertFalse(preparer.isVerification());
        // Preparer
        WorkflowIndex preparer1_0 =
                new WorkflowIndex(createWorkflow(null, null), WorkflowStatus.REQUEST_FOR_CHECK,
                                  ct, false, "USER0");

        assertTrue(preparer1_0.isView());
        assertFalse(preparer1_0.isUpdate());
        assertFalse(preparer1_0.isCheckApprove());


        assertFalse(preparer1_0.isVerification());
        // Preparer
        WorkflowIndex preparer1_1 =
                new WorkflowIndex(createWorkflow(null, null), WorkflowStatus.REQUEST_FOR_CHECK,
                                  ct, true, "USER1");

        assertTrue(preparer1_1.isView());
        assertFalse(preparer1_1.isUpdate());
        assertFalse(preparer1_1.isCheckApprove());


        assertFalse(preparer1_1.isVerification());
        // Preparer
        WorkflowIndex preparer1_2 =
                new WorkflowIndex(createWorkflow(null, null), WorkflowStatus.REQUEST_FOR_CHECK,
                                    ct, true, "USER0");

        assertTrue(preparer1_2.isView());
        assertFalse(preparer1_2.isUpdate());
        assertFalse(preparer1_2.isCheckApprove());


        assertFalse(preparer1_2.isVerification());

        // Checker1
        WorkflowIndex checker1 =
                new WorkflowIndex(createWorkflow(WorkflowType.CHECKER,
                                                 WorkflowProcessStatus.REQUEST_FOR_CHECK),
                                  WorkflowStatus.REQUEST_FOR_CHECK, ct, false, "USER1");

        assertTrue(checker1.isView());
        assertTrue(checker1.isUpdate());
        assertTrue(checker1.isCheckApprove());

        assertFalse(checker1.isVerification());

        // Checker1
        WorkflowIndex checker1_0 =
                new WorkflowIndex(createWorkflow(WorkflowType.CHECKER,
                                                 WorkflowProcessStatus.REQUEST_FOR_CHECK),
                                  WorkflowStatus.REQUEST_FOR_CHECK, ct, false, "USER0");

        assertTrue(checker1_0.isView());
        assertTrue(checker1_0.isUpdate());
        assertTrue(checker1_0.isCheckApprove());

        assertTrue(checker1_0.isVerification());

        // Checker1
        WorkflowIndex checker1_1 =
                new WorkflowIndex(createWorkflow(WorkflowType.CHECKER,
                                                 WorkflowProcessStatus.REQUEST_FOR_CHECK),
                                  WorkflowStatus.REQUEST_FOR_CHECK, ct, true, "USER1");

        assertTrue(checker1_1.isView());
        assertTrue(checker1_1.isUpdate());
        assertTrue(checker1_1.isCheckApprove());

        assertTrue(checker1_1.isVerification());

        // Checker1
        WorkflowIndex checker1_2 =
                new WorkflowIndex(createWorkflow(WorkflowType.CHECKER,
                                                 WorkflowProcessStatus.REQUEST_FOR_CHECK),
                                  WorkflowStatus.REQUEST_FOR_CHECK, ct, true, "USER0");

        assertTrue(checker1_2.isView());
        assertTrue(checker1_2.isUpdate());
        assertTrue(checker1_2.isCheckApprove());

        assertTrue(checker1_2.isVerification());


        // Checker2
        WorkflowIndex checker2 =
                new WorkflowIndex(createWorkflow(WorkflowType.CHECKER, WorkflowProcessStatus.NONE),
                                  WorkflowStatus.REQUEST_FOR_CHECK, ct, false, "USER1");

        assertFalse(checker2.isView());
        assertFalse(checker2.isUpdate());
        assertFalse(checker2.isCheckApprove());

        assertFalse(checker2.isVerification());

        // Checker2
        WorkflowIndex checker2_0 =
                new WorkflowIndex(createWorkflow(WorkflowType.CHECKER, WorkflowProcessStatus.NONE),
                                  WorkflowStatus.REQUEST_FOR_CHECK, ct, false, "USER1");

        assertFalse(checker2_0.isView());
        assertFalse(checker2_0.isUpdate());
        assertFalse(checker2_0.isCheckApprove());

        assertFalse(checker2_0.isVerification());

        // Checker2
        WorkflowIndex checker2_1 =
                new WorkflowIndex(createWorkflow(WorkflowType.CHECKER, WorkflowProcessStatus.NONE),
                                  WorkflowStatus.REQUEST_FOR_CHECK, ct, true, "USER1");

        assertFalse(checker2_1.isView());
        assertFalse(checker2_1.isUpdate());
        assertFalse(checker2_1.isCheckApprove());

        assertFalse(checker2_1.isVerification());

        // Checker2
        WorkflowIndex checker2_2 =
                new WorkflowIndex(createWorkflow(WorkflowType.CHECKER, WorkflowProcessStatus.NONE),
                                  WorkflowStatus.REQUEST_FOR_CHECK, ct, true, "USER0");

        assertFalse(checker2_2.isView());
        assertFalse(checker2_2.isUpdate());
        assertFalse(checker2_2.isCheckApprove());

        assertFalse(checker2_2.isVerification());

        // Approver
        WorkflowIndex approver =
                new WorkflowIndex(
                                  createWorkflow(WorkflowType.APPROVER, WorkflowProcessStatus.NONE),
                                  WorkflowStatus.REQUEST_FOR_CHECK, ct, false, "USER1");

        assertFalse(approver.isView());
        assertFalse(approver.isUpdate());
        assertFalse(approver.isCheckApprove());

        assertFalse(approver.isVerification());
        // Approver
        WorkflowIndex approver1_0 =
                new WorkflowIndex(
                                  createWorkflow(WorkflowType.APPROVER, WorkflowProcessStatus.NONE),
                                  WorkflowStatus.REQUEST_FOR_CHECK, ct, false, "USER0");

        assertFalse(approver1_0.isView());
        assertFalse(approver1_0.isUpdate());
        assertFalse(approver1_0.isCheckApprove());

        assertFalse(approver1_0.isVerification());


        // Approver
        WorkflowIndex approver1_1 =
                new WorkflowIndex(
                                  createWorkflow(WorkflowType.APPROVER, WorkflowProcessStatus.NONE),
                                  WorkflowStatus.REQUEST_FOR_CHECK, ct, true, "USER1");

        assertFalse(approver1_1.isView());
        assertFalse(approver1_1.isUpdate());
        assertFalse(approver1_1.isCheckApprove());

        assertFalse(approver1_1.isVerification());

        // Approver
        WorkflowIndex approver1_2 =
                new WorkflowIndex(
                                  createWorkflow(WorkflowType.APPROVER, WorkflowProcessStatus.NONE),
                                  WorkflowStatus.REQUEST_FOR_CHECK, ct, true, "USER0");

        assertFalse(approver1_2.isView());
        assertFalse(approver1_2.isUpdate());
        assertFalse(approver1_2.isCheckApprove());

        assertFalse(approver1_2.isVerification());

    }


    /**
     * 表示設定のテスト. パターン1で検証中.
     */
    @Test
    public void testWorkflowIndex1_2() {
        CorresponType ct = new CorresponType();
        WorkflowPattern wp = new WorkflowPattern();
        wp.setWorkflowCd("001");
        ct.setWorkflowPattern(wp);
        ct.setAllowApproverToBrowse(AllowApproverToBrowse.INVISIBLE);

        // Preparer
        WorkflowIndex preparer =
                new WorkflowIndex(createWorkflow(null, null), WorkflowStatus.UNDER_CONSIDERATION,
                                  ct, false, "USER1");

        assertTrue(preparer.isView());
        assertFalse(preparer.isUpdate());
        assertFalse(preparer.isCheckApprove());


        assertFalse(preparer.isVerification());
        // Preparer
        WorkflowIndex preparer1_0 =
                new WorkflowIndex(createWorkflow(null, null), WorkflowStatus.UNDER_CONSIDERATION,
                                  ct, false, "USER0");

        assertTrue(preparer1_0.isView());
        assertFalse(preparer1_0.isUpdate());
        assertFalse(preparer1_0.isCheckApprove());


        assertFalse(preparer1_0.isVerification());

        // Preparer
        WorkflowIndex preparer1_1 =
                new WorkflowIndex(createWorkflow(null, null), WorkflowStatus.UNDER_CONSIDERATION,
                                  ct, true, "USER1");

        assertTrue(preparer1_1.isView());
        assertFalse(preparer1_1.isUpdate());
        assertFalse(preparer1_1.isCheckApprove());


        assertFalse(preparer1_1.isVerification());
        // Preparer
        WorkflowIndex preparer1_2 =
                new WorkflowIndex(createWorkflow(null, null), WorkflowStatus.UNDER_CONSIDERATION,
                                  ct, true, "USER0");

        assertTrue(preparer1_2.isView());
        assertFalse(preparer1_2.isUpdate());
        assertFalse(preparer1_2.isCheckApprove());


        assertFalse(preparer1_2.isVerification());


        // Checker1
        WorkflowIndex checker1 =
                new WorkflowIndex(createWorkflow(WorkflowType.CHECKER,
                                                 WorkflowProcessStatus.CHECKED),
                                  WorkflowStatus.UNDER_CONSIDERATION,
                                  ct, false, "USER1");

        assertTrue(checker1.isView());
        assertFalse(checker1.isUpdate());
        assertFalse(checker1.isCheckApprove());


        assertFalse(checker1.isVerification());

        // Checker1
        WorkflowIndex checker1_0 =
                new WorkflowIndex(createWorkflow(WorkflowType.CHECKER,
                                                 WorkflowProcessStatus.CHECKED),
                                  WorkflowStatus.UNDER_CONSIDERATION,
                                  ct, false, "USER0");

        assertTrue(checker1_0.isView());
        assertFalse(checker1_0.isUpdate());
        assertFalse(checker1_0.isCheckApprove());


        assertFalse(checker1_0.isVerification());

        // Checker1
        WorkflowIndex checker1_1 =
                new WorkflowIndex(createWorkflow(WorkflowType.CHECKER,
                                                 WorkflowProcessStatus.CHECKED),
                                  WorkflowStatus.UNDER_CONSIDERATION,
                                  ct, true, "USER1");

        assertTrue(checker1_1.isView());
        assertFalse(checker1_1.isUpdate());
        assertFalse(checker1_1.isCheckApprove());


        assertFalse(checker1_1.isVerification());

        // Checker1
        WorkflowIndex checker1_2 =
                new WorkflowIndex(createWorkflow(WorkflowType.CHECKER,
                                                 WorkflowProcessStatus.CHECKED),
                                  WorkflowStatus.UNDER_CONSIDERATION,
                                  ct, true, "USER0");

        assertTrue(checker1_2.isView());
        assertFalse(checker1_2.isUpdate());
        assertFalse(checker1_2.isCheckApprove());


        assertFalse(checker1.isVerification());


        // Checker2
        WorkflowIndex checker2 =
                new WorkflowIndex(createWorkflow(WorkflowType.CHECKER,
                                                 WorkflowProcessStatus.REQUEST_FOR_CHECK),
                                  WorkflowStatus.UNDER_CONSIDERATION,
                                  ct, false, "USER1");

        assertTrue(checker2.isView());
        assertTrue(checker2.isUpdate());
        assertTrue(checker2.isCheckApprove());


        assertFalse(checker2.isVerification());

        // Checker2
        WorkflowIndex checker2_0 =
                new WorkflowIndex(createWorkflow(WorkflowType.CHECKER,
                                                 WorkflowProcessStatus.REQUEST_FOR_CHECK),
                                  WorkflowStatus.UNDER_CONSIDERATION,
                                  ct, false, "USER0");

        assertTrue(checker2_0.isView());
        assertTrue(checker2_0.isUpdate());
        assertTrue(checker2_0.isCheckApprove());


        assertTrue(checker2_0.isVerification());

        // Checker2
        WorkflowIndex checker2_1 =
                new WorkflowIndex(createWorkflow(WorkflowType.CHECKER,
                                                 WorkflowProcessStatus.REQUEST_FOR_CHECK),
                                  WorkflowStatus.UNDER_CONSIDERATION,
                                  ct, true, "USER1");

        assertTrue(checker2_1.isView());
        assertTrue(checker2_1.isUpdate());
        assertTrue(checker2_1.isCheckApprove());


        assertTrue(checker2_1.isVerification());
        // Checker2
        WorkflowIndex checker2_2 =
                new WorkflowIndex(createWorkflow(WorkflowType.CHECKER,
                                                 WorkflowProcessStatus.REQUEST_FOR_CHECK),
                                  WorkflowStatus.UNDER_CONSIDERATION,
                                  ct, true, "USER0");

        assertTrue(checker2_2.isView());
        assertTrue(checker2_2.isUpdate());
        assertTrue(checker2_2.isCheckApprove());


        assertTrue(checker2_2.isVerification());



        // Approver
        WorkflowIndex approver =
                new WorkflowIndex(
                                  createWorkflow(WorkflowType.APPROVER, WorkflowProcessStatus.NONE),
                                  WorkflowStatus.UNDER_CONSIDERATION,
                                  ct, false, "USER1");

        assertFalse(approver.isView());
        assertFalse(approver.isUpdate());
        assertFalse(approver.isCheckApprove());

        assertFalse(approver.isVerification());

        // Approver
        WorkflowIndex approver1_0 =
                new WorkflowIndex(
                                  createWorkflow(WorkflowType.APPROVER, WorkflowProcessStatus.NONE),
                                  WorkflowStatus.UNDER_CONSIDERATION,
                                  ct, false, "USER0");

        assertFalse(approver1_0.isView());
        assertFalse(approver1_0.isUpdate());
        assertFalse(approver1_0.isCheckApprove());

        assertFalse(approver1_0.isVerification());

        // Approver
        WorkflowIndex approver1_1 =
                new WorkflowIndex(
                                  createWorkflow(WorkflowType.APPROVER, WorkflowProcessStatus.NONE),
                                  WorkflowStatus.UNDER_CONSIDERATION,
                                  ct, true, "USER1");

        assertFalse(approver1_1.isView());
        assertFalse(approver1_1.isUpdate());
        assertFalse(approver1_1.isCheckApprove());

        assertFalse(approver1_1.isVerification());
        // Approver
        WorkflowIndex approver1_2 =
                new WorkflowIndex(
                                  createWorkflow(WorkflowType.APPROVER, WorkflowProcessStatus.NONE),
                                  WorkflowStatus.UNDER_CONSIDERATION,
                                  ct, true, "USER0");

        assertFalse(approver1_2.isView());
        assertFalse(approver1_2.isUpdate());
        assertFalse(approver1_2.isCheckApprove());

        assertFalse(approver1_2.isVerification());

    }


    /**
     * 表示設定のテスト. パターン1で承認前.
     */
    @Test
    public void testWorkflowIndex1_3() {
        CorresponType ct = new CorresponType();
        WorkflowPattern wp = new WorkflowPattern();
        wp.setWorkflowCd("001");
        ct.setWorkflowPattern(wp);
        ct.setAllowApproverToBrowse(AllowApproverToBrowse.INVISIBLE);

        // Preparer
        WorkflowIndex preparer =
                new WorkflowIndex(createWorkflow(null, null), WorkflowStatus.UNDER_CONSIDERATION,
                                  ct, false, "USER1");

        assertTrue(preparer.isView());
        assertFalse(preparer.isUpdate());
        assertFalse(preparer.isCheckApprove());

        assertFalse(preparer.isVerification());

        // Preparer
        WorkflowIndex preparer1_0 =
                new WorkflowIndex(createWorkflow(null, null), WorkflowStatus.UNDER_CONSIDERATION,
                                  ct, false, "USER0");

        assertTrue(preparer1_0.isView());
        assertFalse(preparer1_0.isUpdate());
        assertFalse(preparer1_0.isCheckApprove());

        assertFalse(preparer1_0.isVerification());

        // Preparer
        WorkflowIndex preparer1_1 =
                new WorkflowIndex(createWorkflow(null, null), WorkflowStatus.UNDER_CONSIDERATION,
                                  ct, true, "USER1");

        assertTrue(preparer1_1.isView());
        assertFalse(preparer1_1.isUpdate());
        assertFalse(preparer1_1.isCheckApprove());

        assertFalse(preparer1_1.isVerification());

        // Preparer
        WorkflowIndex preparer1_2 =
                new WorkflowIndex(createWorkflow(null, null), WorkflowStatus.UNDER_CONSIDERATION,
                                  ct, true, "USER0");

        assertTrue(preparer1_2.isView());
        assertFalse(preparer1_2.isUpdate());
        assertFalse(preparer1_2.isCheckApprove());

        assertFalse(preparer1_2.isVerification());

        // Checker1
        WorkflowIndex checker1 =
                new WorkflowIndex(createWorkflow(WorkflowType.CHECKER,
                                                 WorkflowProcessStatus.CHECKED),
                                  WorkflowStatus.UNDER_CONSIDERATION,
                                  ct, false, "USER1");

        assertTrue(checker1.isView());
        assertFalse(checker1.isUpdate());
        assertFalse(checker1.isCheckApprove());

        assertFalse(checker1.isVerification());
        // Checker1
        WorkflowIndex checker1_0 =
                new WorkflowIndex(createWorkflow(WorkflowType.CHECKER,
                                                 WorkflowProcessStatus.CHECKED),
                                  WorkflowStatus.UNDER_CONSIDERATION,
                                  ct, false, "USER0");

        assertTrue(checker1_0.isView());
        assertFalse(checker1_0.isUpdate());
        assertFalse(checker1_0.isCheckApprove());

        assertFalse(checker1_0.isVerification());

        // Checker1
        WorkflowIndex checker1_1 =
                new WorkflowIndex(createWorkflow(WorkflowType.CHECKER,
                                                 WorkflowProcessStatus.CHECKED),
                                  WorkflowStatus.UNDER_CONSIDERATION,
                                  ct, true, "USER1");

        assertTrue(checker1_1.isView());
        assertFalse(checker1_1.isUpdate());
        assertFalse(checker1_1.isCheckApprove());

        assertFalse(checker1_1.isVerification());
        // Checker1
        WorkflowIndex checker1_2 =
                new WorkflowIndex(createWorkflow(WorkflowType.CHECKER,
                                                 WorkflowProcessStatus.CHECKED),
                                  WorkflowStatus.UNDER_CONSIDERATION,
                                  ct, true, "USER0");

        assertTrue(checker1_2.isView());
        assertFalse(checker1_2.isUpdate());
        assertFalse(checker1_2.isCheckApprove());

        assertFalse(checker1_2.isVerification());

        // Checker2
        WorkflowIndex checker2 =
                new WorkflowIndex(createWorkflow(WorkflowType.CHECKER,
                                                 WorkflowProcessStatus.CHECKED),
                                  WorkflowStatus.UNDER_CONSIDERATION,
                                  ct, false, "USER1");

        assertTrue(checker2.isView());
        assertFalse(checker2.isUpdate());
        assertFalse(checker2.isCheckApprove());

        assertFalse(checker2.isVerification());

        // Checker2
        WorkflowIndex checker2_0 =
                new WorkflowIndex(createWorkflow(WorkflowType.CHECKER,
                                                 WorkflowProcessStatus.CHECKED),
                                  WorkflowStatus.UNDER_CONSIDERATION,
                                  ct, false, "USER0");

        assertTrue(checker2_0.isView());
        assertFalse(checker2_0.isUpdate());
        assertFalse(checker2_0.isCheckApprove());

        assertFalse(checker2_0.isVerification());

        // Checker2
        WorkflowIndex checker2_1 =
                new WorkflowIndex(createWorkflow(WorkflowType.CHECKER,
                                                 WorkflowProcessStatus.CHECKED),
                                  WorkflowStatus.UNDER_CONSIDERATION,
                                  ct, true, "USER1");

        assertTrue(checker2_1.isView());
        assertFalse(checker2_1.isUpdate());
        assertFalse(checker2_1.isCheckApprove());

        assertFalse(checker2_1.isVerification());

        // Checker2
        WorkflowIndex checker2_2 =
                new WorkflowIndex(createWorkflow(WorkflowType.CHECKER,
                                                 WorkflowProcessStatus.CHECKED),
                                  WorkflowStatus.UNDER_CONSIDERATION,
                                  ct, true, "USER0");

        assertTrue(checker2_2.isView());
        assertFalse(checker2_2.isUpdate());
        assertFalse(checker2_2.isCheckApprove());

        assertFalse(checker2_2.isVerification());

        // Approver
        WorkflowIndex approver =
                new WorkflowIndex(createWorkflow(WorkflowType.APPROVER,
                                                 WorkflowProcessStatus.REQUEST_FOR_APPROVAL),
                                  WorkflowStatus.UNDER_CONSIDERATION,
                                  ct, false, "USER1");

        assertTrue(approver.isView());
        assertTrue(approver.isUpdate());
        assertTrue(approver.isCheckApprove());

        assertFalse(approver.isVerification());
        // Approver
        WorkflowIndex approver1_0 =
                new WorkflowIndex(createWorkflow(WorkflowType.APPROVER,
                                                 WorkflowProcessStatus.REQUEST_FOR_APPROVAL),
                                  WorkflowStatus.UNDER_CONSIDERATION,
                                  ct, false, "USER0");

        assertTrue(approver1_0.isView());
        assertTrue(approver1_0.isUpdate());
        assertTrue(approver1_0.isCheckApprove());

        assertTrue(approver1_0.isVerification());
        // Approver
        WorkflowIndex approver1_1 =
                new WorkflowIndex(createWorkflow(WorkflowType.APPROVER,
                                                 WorkflowProcessStatus.REQUEST_FOR_APPROVAL),
                                  WorkflowStatus.UNDER_CONSIDERATION,
                                  ct, true, "USER1");

        assertTrue(approver1_1.isView());
        assertTrue(approver1_1.isUpdate());
        assertTrue(approver1_1.isCheckApprove());

        assertTrue(approver1_1.isVerification());
        // Approver
        WorkflowIndex approver1_2 =
                new WorkflowIndex(createWorkflow(WorkflowType.APPROVER,
                                                 WorkflowProcessStatus.REQUEST_FOR_APPROVAL),
                                  WorkflowStatus.UNDER_CONSIDERATION,
                                  ct, true, "USER0");

        assertTrue(approver1_2.isView());
        assertTrue(approver1_2.isUpdate());
        assertTrue(approver1_2.isCheckApprove());

        assertTrue(approver1_2.isVerification());

    }

    /**
     * 表示設定のテスト. パターン1で承認後.
     */
    @Test
    public void testWorkflowIndex1_4() {
        CorresponType ct = new CorresponType();
        WorkflowPattern wp = new WorkflowPattern();
        wp.setWorkflowCd("001");
        ct.setWorkflowPattern(wp);
        ct.setAllowApproverToBrowse(AllowApproverToBrowse.INVISIBLE);

        // Preparer
        WorkflowIndex preparer =
                new WorkflowIndex(createWorkflow(null, null), WorkflowStatus.ISSUED,
                                  ct, false, "USER1");

        assertTrue(preparer.isView());
        assertFalse(preparer.isUpdate());
        assertFalse(preparer.isCheckApprove());

        assertFalse(preparer.isVerification());

        // Preparer
        WorkflowIndex preparer1_0 =
                new WorkflowIndex(createWorkflow(null, null), WorkflowStatus.ISSUED,
                                  ct, false, "USER0");

        assertTrue(preparer1_0.isView());
        assertFalse(preparer1_0.isUpdate());
        assertFalse(preparer1_0.isCheckApprove());

        assertFalse(preparer1_0.isVerification());

        // Preparer
        WorkflowIndex preparer1_1 =
                new WorkflowIndex(createWorkflow(null, null), WorkflowStatus.ISSUED,
                                  ct, true, "USER1");

        assertTrue(preparer1_1.isView());
        assertFalse(preparer1_1.isUpdate());
        assertFalse(preparer1_1.isCheckApprove());

        assertFalse(preparer1_1.isVerification());
        // Preparer
        WorkflowIndex preparer1_2 =
                new WorkflowIndex(createWorkflow(null, null), WorkflowStatus.ISSUED,
                                  ct, true, "USER0");

        assertTrue(preparer1_2.isView());
        assertFalse(preparer1_2.isUpdate());
        assertFalse(preparer1_2.isCheckApprove());

        assertFalse(preparer1_2.isVerification());


        // Checker1
        WorkflowIndex checker1 =
                new WorkflowIndex(createWorkflow(WorkflowType.CHECKER,
                                                 WorkflowProcessStatus.CHECKED),
                                  WorkflowStatus.ISSUED, ct, false, "USER1");

        assertTrue(checker1.isView());
        assertFalse(checker1.isUpdate());
        assertFalse(checker1.isCheckApprove());

        assertFalse(checker1.isVerification());

        // Checker1
        WorkflowIndex checker1_0 =
                new WorkflowIndex(createWorkflow(WorkflowType.CHECKER,
                                                 WorkflowProcessStatus.CHECKED),
                                  WorkflowStatus.ISSUED, ct, false, "USER0");

        assertTrue(checker1_0.isView());
        assertFalse(checker1_0.isUpdate());
        assertFalse(checker1_0.isCheckApprove());

        assertFalse(checker1_0.isVerification());

        // Checker1
        WorkflowIndex checker1_1 =
                new WorkflowIndex(createWorkflow(WorkflowType.CHECKER,
                                                 WorkflowProcessStatus.CHECKED),
                                  WorkflowStatus.ISSUED, ct, true, "USER1");

        assertTrue(checker1_1.isView());
        assertFalse(checker1_1.isUpdate());
        assertFalse(checker1_1.isCheckApprove());

        assertFalse(checker1_1.isVerification());

        // Checker1
        WorkflowIndex checker1_2 =
                new WorkflowIndex(createWorkflow(WorkflowType.CHECKER,
                                                 WorkflowProcessStatus.CHECKED),
                                  WorkflowStatus.ISSUED, ct, true, "USER0");

        assertTrue(checker1_2.isView());
        assertFalse(checker1_2.isUpdate());
        assertFalse(checker1_2.isCheckApprove());

        assertFalse(checker1_2.isVerification());


        // Checker2
        WorkflowIndex checker2 =
                new WorkflowIndex(createWorkflow(WorkflowType.CHECKER,
                                                 WorkflowProcessStatus.CHECKED),
                                  WorkflowStatus.ISSUED, ct, false, "USER1");

        assertTrue(checker2.isView());
        assertFalse(checker2.isUpdate());
        assertFalse(checker2.isCheckApprove());

        assertFalse(checker2.isVerification());

        // Checker2
        WorkflowIndex checker2_0 =
                new WorkflowIndex(createWorkflow(WorkflowType.CHECKER,
                                                 WorkflowProcessStatus.CHECKED),
                                  WorkflowStatus.ISSUED, ct, false, "USER0");

        assertTrue(checker2_0.isView());
        assertFalse(checker2_0.isUpdate());
        assertFalse(checker2_0.isCheckApprove());

        assertFalse(checker2_0.isVerification());

        // Checker2
        WorkflowIndex checker2_1 =
                new WorkflowIndex(createWorkflow(WorkflowType.CHECKER,
                                                 WorkflowProcessStatus.CHECKED),
                                  WorkflowStatus.ISSUED, ct, true, "USER1");

        assertTrue(checker2_1.isView());
        assertFalse(checker2_1.isUpdate());
        assertFalse(checker2_1.isCheckApprove());

        assertFalse(checker2_1.isVerification());
        // Checker2
        WorkflowIndex checker2_2 =
                new WorkflowIndex(createWorkflow(WorkflowType.CHECKER,
                                                 WorkflowProcessStatus.CHECKED),
                                  WorkflowStatus.ISSUED, ct, true, "USER0");

        assertTrue(checker2_2.isView());
        assertFalse(checker2_2.isUpdate());
        assertFalse(checker2_2.isCheckApprove());

        assertFalse(checker2_2.isVerification());

        // Approver
        WorkflowIndex approver =
                new WorkflowIndex(createWorkflow(WorkflowType.APPROVER,
                                                 WorkflowProcessStatus.APPROVED),
                                  WorkflowStatus.ISSUED, ct, false, "USER1");

        assertTrue(approver.isView());
        assertFalse(approver.isUpdate());
        assertFalse(approver.isCheckApprove());

        assertFalse(approver.isVerification());
        // Approver
        WorkflowIndex approver1_0 =
                new WorkflowIndex(createWorkflow(WorkflowType.APPROVER,
                                                 WorkflowProcessStatus.APPROVED),
                                  WorkflowStatus.ISSUED, ct, false, "USER0");

        assertTrue(approver1_0.isView());
        assertFalse(approver1_0.isUpdate());
        assertFalse(approver1_0.isCheckApprove());

        assertFalse(approver1_0.isVerification());

        // Approver
        WorkflowIndex approver1_1 =
                new WorkflowIndex(createWorkflow(WorkflowType.APPROVER,
                                                 WorkflowProcessStatus.APPROVED),
                                  WorkflowStatus.ISSUED, ct, true, "USER1");

        assertTrue(approver1_1.isView());
        assertFalse(approver1_1.isUpdate());
        assertFalse(approver1_1.isCheckApprove());

        assertFalse(approver1_1.isVerification());

        // Approver
        WorkflowIndex approver1_2 =
                new WorkflowIndex(createWorkflow(WorkflowType.APPROVER,
                                                 WorkflowProcessStatus.APPROVED),
                                  WorkflowStatus.ISSUED, ct, true, "USER0");

        assertTrue(approver1_2.isView());
        assertFalse(approver1_2.isUpdate());
        assertFalse(approver1_2.isCheckApprove());

        assertFalse(approver1_2.isVerification());

    }

    /**
     * 表示設定のテスト. パターン2+パターン4で検証前.
     */
    @Test
    public void testWorkflowIndex2_1() {
        CorresponType ctv = new CorresponType();
        WorkflowPattern wp = new WorkflowPattern();
        wp.setWorkflowCd("002");
        ctv.setWorkflowPattern(wp);
        ctv.setAllowApproverToBrowse(AllowApproverToBrowse.VISIBLE);

        // Preparer
        WorkflowIndex preparer =
                new WorkflowIndex(createWorkflow(null, null), WorkflowStatus.REQUEST_FOR_CHECK,
                                  ctv, false, "USER1");

        assertTrue(preparer.isView());
        assertFalse(preparer.isUpdate());
        assertFalse(preparer.isCheckApprove());

        assertFalse(preparer.isVerification());
        // Preparer
        WorkflowIndex preparer1_0 =
                new WorkflowIndex(createWorkflow(null, null), WorkflowStatus.REQUEST_FOR_CHECK,
                                  ctv, false, "USER0");

        assertTrue(preparer1_0.isView());
        assertFalse(preparer1_0.isUpdate());
        assertFalse(preparer1_0.isCheckApprove());

        assertFalse(preparer1_0.isVerification());

        // Preparer
        WorkflowIndex preparer1_1 =
                new WorkflowIndex(createWorkflow(null, null), WorkflowStatus.REQUEST_FOR_CHECK,
                                  ctv, true, "USER1");

        assertTrue(preparer1_1.isView());
        assertFalse(preparer1_1.isUpdate());
        assertFalse(preparer1_1.isCheckApprove());

        assertFalse(preparer1_1.isVerification());

        // Preparer
        WorkflowIndex preparer1_2 =
                new WorkflowIndex(createWorkflow(null, null), WorkflowStatus.REQUEST_FOR_CHECK,
                                  ctv, true, "USER0");

        assertTrue(preparer1_2.isView());
        assertFalse(preparer1_2.isUpdate());
        assertFalse(preparer1_2.isCheckApprove());

        assertFalse(preparer1_2.isVerification());


        // Checker1
        WorkflowIndex checker1 =
                new WorkflowIndex(createWorkflow(WorkflowType.CHECKER,
                                                 WorkflowProcessStatus.REQUEST_FOR_CHECK),
                                  WorkflowStatus.REQUEST_FOR_CHECK, ctv, false, "USER1");

        assertTrue(checker1.isView());
        assertTrue(checker1.isUpdate());
        assertTrue(checker1.isCheckApprove());

        assertFalse(checker1.isVerification());

        // Checker1
        WorkflowIndex checker1_0 =
                new WorkflowIndex(createWorkflow(WorkflowType.CHECKER,
                                                 WorkflowProcessStatus.REQUEST_FOR_CHECK),
                                  WorkflowStatus.REQUEST_FOR_CHECK, ctv, false, "USER0");

        assertTrue(checker1_0.isView());
        assertTrue(checker1_0.isUpdate());
        assertTrue(checker1_0.isCheckApprove());

        assertTrue(checker1_0.isVerification());
        // Checker1
        WorkflowIndex checker1_1 =
                new WorkflowIndex(createWorkflow(WorkflowType.CHECKER,
                                                 WorkflowProcessStatus.REQUEST_FOR_CHECK),
                                  WorkflowStatus.REQUEST_FOR_CHECK, ctv, true, "USER1");

        assertTrue(checker1_1.isView());
        assertTrue(checker1_1.isUpdate());
        assertTrue(checker1_1.isCheckApprove());

        assertTrue(checker1_1.isVerification());
        // Checker1
        WorkflowIndex checker1_2 =
                new WorkflowIndex(createWorkflow(WorkflowType.CHECKER,
                                                 WorkflowProcessStatus.REQUEST_FOR_CHECK),
                                  WorkflowStatus.REQUEST_FOR_CHECK, ctv, true, "USER0");

        assertTrue(checker1_2.isView());
        assertTrue(checker1_2.isUpdate());
        assertTrue(checker1_2.isCheckApprove());

        assertTrue(checker1_2.isVerification());


        // Checker2
        WorkflowIndex checker2 =
                new WorkflowIndex(createWorkflow(WorkflowType.CHECKER,
                                                 WorkflowProcessStatus.REQUEST_FOR_CHECK),
                                  WorkflowStatus.REQUEST_FOR_CHECK, ctv, false, "USER1");

        assertTrue(checker2.isView());
        assertTrue(checker2.isUpdate());
        assertTrue(checker2.isCheckApprove());

        assertFalse(checker2.isVerification());
        // Checker2
        WorkflowIndex checker2_0 =
                new WorkflowIndex(createWorkflow(WorkflowType.CHECKER,
                                                 WorkflowProcessStatus.REQUEST_FOR_CHECK),
                                  WorkflowStatus.REQUEST_FOR_CHECK, ctv, false, "USER0");

        assertTrue(checker2_0.isView());
        assertTrue(checker2_0.isUpdate());
        assertTrue(checker2_0.isCheckApprove());

        assertTrue(checker2_0.isVerification());
        // Checker2
        WorkflowIndex checker2_1 =
                new WorkflowIndex(createWorkflow(WorkflowType.CHECKER,
                                                 WorkflowProcessStatus.REQUEST_FOR_CHECK),
                                  WorkflowStatus.REQUEST_FOR_CHECK, ctv, true, "USER1");

        assertTrue(checker2_1.isView());
        assertTrue(checker2_1.isUpdate());
        assertTrue(checker2_1.isCheckApprove());

        assertTrue(checker2_1.isVerification());
        // Checker2
        WorkflowIndex checker2_2 =
                new WorkflowIndex(createWorkflow(WorkflowType.CHECKER,
                                                 WorkflowProcessStatus.REQUEST_FOR_CHECK),
                                  WorkflowStatus.REQUEST_FOR_CHECK, ctv, true, "USER0");

        assertTrue(checker2_2.isView());
        assertTrue(checker2_2.isUpdate());
        assertTrue(checker2_2.isCheckApprove());

        assertTrue(checker2_2.isVerification());


        // Approver
        WorkflowIndex approver =
                new WorkflowIndex(
                                  createWorkflow(WorkflowType.APPROVER, WorkflowProcessStatus.NONE),
                                  WorkflowStatus.REQUEST_FOR_CHECK, ctv, false, "USER1");

        assertTrue(approver.isView());
        assertFalse(approver.isUpdate());
        assertFalse(approver.isCheckApprove());

        assertFalse(approver.isVerification());

        // Approver
        WorkflowIndex approver1_0 =
                new WorkflowIndex(
                                  createWorkflow(WorkflowType.APPROVER, WorkflowProcessStatus.NONE),
                                  WorkflowStatus.REQUEST_FOR_CHECK, ctv, false, "USER0");

        assertTrue(approver1_0.isView());
        assertFalse(approver1_0.isUpdate());
        assertFalse(approver1_0.isCheckApprove());

        assertFalse(approver1_0.isVerification());

        // Approver
        WorkflowIndex approver1_1 =
                new WorkflowIndex(
                                  createWorkflow(WorkflowType.APPROVER, WorkflowProcessStatus.NONE),
                                  WorkflowStatus.REQUEST_FOR_CHECK, ctv, true, "USER1");

        assertTrue(approver1_1.isView());
        assertFalse(approver1_1.isUpdate());
        assertFalse(approver1_1.isCheckApprove());

        assertFalse(approver1_1.isVerification());

        // Approver
        WorkflowIndex approver1_2 =
                new WorkflowIndex(
                                  createWorkflow(WorkflowType.APPROVER, WorkflowProcessStatus.NONE),
                                  WorkflowStatus.REQUEST_FOR_CHECK, ctv, true, "USER0");

        assertTrue(approver1_2.isView());
        assertFalse(approver1_2.isUpdate());
        assertFalse(approver1_2.isCheckApprove());

        assertFalse(approver1_2.isVerification());

    }

    /**
     * 表示設定のテスト. パターン2+パターン4で検証中.
     */
    @Test
    public void testWorkflowIndex2_2() {
        CorresponType ctv = new CorresponType();
        WorkflowPattern wp = new WorkflowPattern();
        wp.setWorkflowCd("002");
        ctv.setWorkflowPattern(wp);
        ctv.setAllowApproverToBrowse(AllowApproverToBrowse.VISIBLE);

        // Preparer
        WorkflowIndex preparer =
                new WorkflowIndex(createWorkflow(null, null), WorkflowStatus.UNDER_CONSIDERATION,
                                  ctv, false, "USER1");

        assertTrue(preparer.isView());
        assertFalse(preparer.isUpdate());
        assertFalse(preparer.isCheckApprove());

        assertFalse(preparer.isVerification());
        // Preparer
        WorkflowIndex preparer1_0 =
                new WorkflowIndex(createWorkflow(null, null), WorkflowStatus.UNDER_CONSIDERATION,
                                  ctv, false, "USER0");

        assertTrue(preparer1_0.isView());
        assertFalse(preparer1_0.isUpdate());
        assertFalse(preparer1_0.isCheckApprove());

        assertFalse(preparer1_0.isVerification());

        // Preparer
        WorkflowIndex preparer1_1 =
                new WorkflowIndex(createWorkflow(null, null), WorkflowStatus.UNDER_CONSIDERATION,
                                  ctv, true, "USER1");

        assertTrue(preparer1_1.isView());
        assertFalse(preparer1_1.isUpdate());
        assertFalse(preparer1_1.isCheckApprove());

        assertFalse(preparer1_1.isVerification());
        // Preparer
        WorkflowIndex preparer1_2 =
                new WorkflowIndex(createWorkflow(null, null), WorkflowStatus.UNDER_CONSIDERATION,
                                  ctv, true, "USER0");

        assertTrue(preparer1_2.isView());
        assertFalse(preparer1_2.isUpdate());
        assertFalse(preparer1_2.isCheckApprove());

        assertFalse(preparer1_2.isVerification());

        // Checker1
        WorkflowIndex checker1 =
                new WorkflowIndex(createWorkflow(WorkflowType.CHECKER,
                                                 WorkflowProcessStatus.CHECKED),
                                  WorkflowStatus.UNDER_CONSIDERATION, ctv, false, "USER1");

        assertTrue(checker1.isView());
        assertFalse(checker1.isUpdate());
        assertFalse(checker1.isCheckApprove());

        assertFalse(checker1.isVerification());
        // Checker1
        WorkflowIndex checker1_0 =
                new WorkflowIndex(createWorkflow(WorkflowType.CHECKER,
                                                 WorkflowProcessStatus.CHECKED),
                                  WorkflowStatus.UNDER_CONSIDERATION, ctv, false, "USER1");

        assertTrue(checker1_0.isView());
        assertFalse(checker1_0.isUpdate());
        assertFalse(checker1_0.isCheckApprove());

        assertFalse(checker1_0.isVerification());

        // Checker1
        WorkflowIndex checker1_1 =
                new WorkflowIndex(createWorkflow(WorkflowType.CHECKER,
                                                 WorkflowProcessStatus.CHECKED),
                                  WorkflowStatus.UNDER_CONSIDERATION, ctv, true, "USER1");

        assertTrue(checker1_1.isView());
        assertFalse(checker1_1.isUpdate());
        assertFalse(checker1_1.isCheckApprove());

        assertFalse(checker1_1.isVerification());
        // Checker1
        WorkflowIndex checker1_2 =
                new WorkflowIndex(createWorkflow(WorkflowType.CHECKER,
                                                 WorkflowProcessStatus.CHECKED),
                                  WorkflowStatus.UNDER_CONSIDERATION, ctv, true, "USER0");

        assertTrue(checker1_2.isView());
        assertFalse(checker1_2.isUpdate());
        assertFalse(checker1_2.isCheckApprove());

        assertFalse(checker1_2.isVerification());

        // Checker2
        WorkflowIndex checker2 =
                new WorkflowIndex(createWorkflow(WorkflowType.CHECKER,
                                                 WorkflowProcessStatus.REQUEST_FOR_CHECK),
                                  WorkflowStatus.UNDER_CONSIDERATION, ctv, false, "USER1");

        assertTrue(checker2.isView());
        assertTrue(checker2.isUpdate());
        assertTrue(checker2.isCheckApprove());

        assertFalse(checker2.isVerification());

        // Checker2
        WorkflowIndex checker2_0 =
                new WorkflowIndex(createWorkflow(WorkflowType.CHECKER,
                                                 WorkflowProcessStatus.REQUEST_FOR_CHECK),
                                  WorkflowStatus.UNDER_CONSIDERATION, ctv, false, "USER0");

        assertTrue(checker2_0.isView());
        assertTrue(checker2_0.isUpdate());
        assertTrue(checker2_0.isCheckApprove());

        assertTrue(checker2_0.isVerification());
        // Checker2
        WorkflowIndex checker2_1 =
                new WorkflowIndex(createWorkflow(WorkflowType.CHECKER,
                                                 WorkflowProcessStatus.REQUEST_FOR_CHECK),
                                  WorkflowStatus.UNDER_CONSIDERATION, ctv, true, "USER1");

        assertTrue(checker2_1.isView());
        assertTrue(checker2_1.isUpdate());
        assertTrue(checker2_1.isCheckApprove());

        assertTrue(checker2_1.isVerification());
        // Checker2
        WorkflowIndex checker2_2 =
                new WorkflowIndex(createWorkflow(WorkflowType.CHECKER,
                                                 WorkflowProcessStatus.REQUEST_FOR_CHECK),
                                  WorkflowStatus.UNDER_CONSIDERATION, ctv, true, "USER0");

        assertTrue(checker2_2.isView());
        assertTrue(checker2_2.isUpdate());
        assertTrue(checker2_2.isCheckApprove());

        assertTrue(checker2_2.isVerification());

        // Approver
        WorkflowIndex approver =
                new WorkflowIndex(
                                  createWorkflow(WorkflowType.APPROVER, WorkflowProcessStatus.NONE),
                                  WorkflowStatus.UNDER_CONSIDERATION, ctv, false, "USER1");

        assertTrue(approver.isView());
        assertFalse(approver.isUpdate());
        assertFalse(approver.isCheckApprove());

        assertFalse(approver.isVerification());
        // Approver
        WorkflowIndex approver1_0 =
                new WorkflowIndex(
                                  createWorkflow(WorkflowType.APPROVER, WorkflowProcessStatus.NONE),
                                  WorkflowStatus.UNDER_CONSIDERATION, ctv, false, "USER0");

        assertTrue(approver1_0.isView());
        assertFalse(approver1_0.isUpdate());
        assertFalse(approver1_0.isCheckApprove());

        assertFalse(approver1_0.isVerification());

        // Approver
        WorkflowIndex approver1_1 =
                new WorkflowIndex(
                                  createWorkflow(WorkflowType.APPROVER, WorkflowProcessStatus.NONE),
                                  WorkflowStatus.UNDER_CONSIDERATION, ctv, true, "USER1");

        assertTrue(approver1_1.isView());
        assertFalse(approver1_1.isUpdate());
        assertFalse(approver1_1.isCheckApprove());

        assertFalse(approver1_1.isVerification());

        // Approver
        WorkflowIndex approver1_2 =
                new WorkflowIndex(
                                  createWorkflow(WorkflowType.APPROVER, WorkflowProcessStatus.NONE),
                                  WorkflowStatus.UNDER_CONSIDERATION, ctv, true, "USER0");

        assertTrue(approver1_2.isView());
        assertFalse(approver1_2.isUpdate());
        assertFalse(approver1_2.isCheckApprove());

        assertFalse(approver1_2.isVerification());

    }

    /**
     * 表示設定のテスト. パターン2+パターン4で承認前.
     */
    @Test
    public void testWorkflowIndex2_3() {
        CorresponType ctv = new CorresponType();
        WorkflowPattern wp = new WorkflowPattern();
        wp.setWorkflowCd("002");
        ctv.setWorkflowPattern(wp);
        ctv.setAllowApproverToBrowse(AllowApproverToBrowse.VISIBLE);

        // Preparer
        WorkflowIndex preparer =
                new WorkflowIndex(createWorkflow(null, null), WorkflowStatus.UNDER_CONSIDERATION,
                                  ctv, false, "USER1");

        assertTrue(preparer.isView());
        assertFalse(preparer.isUpdate());
        assertFalse(preparer.isCheckApprove());

        assertFalse(preparer.isVerification());

        // Preparer
        WorkflowIndex preparer1_0 =
                new WorkflowIndex(createWorkflow(null, null), WorkflowStatus.UNDER_CONSIDERATION,
                                  ctv, false, "USER0");

        assertTrue(preparer1_0.isView());
        assertFalse(preparer1_0.isUpdate());
        assertFalse(preparer1_0.isCheckApprove());

        assertFalse(preparer1_0.isVerification());
        // Preparer
        WorkflowIndex preparer1_1 =
                new WorkflowIndex(createWorkflow(null, null), WorkflowStatus.UNDER_CONSIDERATION,
                                  ctv, true, "USER1");

        assertTrue(preparer1_1.isView());
        assertFalse(preparer1_1.isUpdate());
        assertFalse(preparer1_1.isCheckApprove());

        assertFalse(preparer1_1.isVerification());
        // Preparer
        WorkflowIndex preparer1_2 =
                new WorkflowIndex(createWorkflow(null, null), WorkflowStatus.UNDER_CONSIDERATION,
                                  ctv, true, "USER0");

        assertTrue(preparer1_2.isView());
        assertFalse(preparer1_2.isUpdate());
        assertFalse(preparer1_2.isCheckApprove());

        assertFalse(preparer1_2.isVerification());


        // Checker1
        WorkflowIndex checker1 =
                new WorkflowIndex(createWorkflow(WorkflowType.CHECKER,
                                                 WorkflowProcessStatus.CHECKED),
                                  WorkflowStatus.UNDER_CONSIDERATION, ctv, false, "USER1");

        assertTrue(checker1.isView());
        assertFalse(checker1.isUpdate());
        assertFalse(checker1.isCheckApprove());

        assertFalse(checker1.isVerification());

        // Checker1
        WorkflowIndex checker1_0 =
                new WorkflowIndex(createWorkflow(WorkflowType.CHECKER,
                                                 WorkflowProcessStatus.CHECKED),
                                  WorkflowStatus.UNDER_CONSIDERATION, ctv, false, "USER0");

        assertTrue(checker1_0.isView());
        assertFalse(checker1_0.isUpdate());
        assertFalse(checker1_0.isCheckApprove());

        assertFalse(checker1_0.isVerification());

        // Checker1
        WorkflowIndex checker1_1 =
                new WorkflowIndex(createWorkflow(WorkflowType.CHECKER,
                                                 WorkflowProcessStatus.CHECKED),
                                  WorkflowStatus.UNDER_CONSIDERATION, ctv, true, "USER1");

        assertTrue(checker1_1.isView());
        assertFalse(checker1_1.isUpdate());
        assertFalse(checker1_1.isCheckApprove());

        assertFalse(checker1_1.isVerification());

        // Checker1
        WorkflowIndex checker1_2 =
                new WorkflowIndex(createWorkflow(WorkflowType.CHECKER,
                                                 WorkflowProcessStatus.CHECKED),
                                  WorkflowStatus.UNDER_CONSIDERATION, ctv, true, "USER0");

        assertTrue(checker1_2.isView());
        assertFalse(checker1_2.isUpdate());
        assertFalse(checker1_2.isCheckApprove());

        assertFalse(checker1_2.isVerification());

        // Checker2
        WorkflowIndex checker2 =
                new WorkflowIndex(createWorkflow(WorkflowType.CHECKER,
                                                 WorkflowProcessStatus.CHECKED),
                                  WorkflowStatus.UNDER_CONSIDERATION, ctv, false, "USER1");

        assertTrue(checker2.isView());
        assertFalse(checker2.isUpdate());
        assertFalse(checker2.isCheckApprove());

        assertFalse(checker2.isVerification());

        // Checker2
        WorkflowIndex checker2_0 =
                new WorkflowIndex(createWorkflow(WorkflowType.CHECKER,
                                                 WorkflowProcessStatus.CHECKED),
                                  WorkflowStatus.UNDER_CONSIDERATION, ctv, false, "USER0");

        assertTrue(checker2_0.isView());
        assertFalse(checker2_0.isUpdate());
        assertFalse(checker2_0.isCheckApprove());

        assertFalse(checker2_0.isVerification());
        // Checker2
        WorkflowIndex checker2_1 =
                new WorkflowIndex(createWorkflow(WorkflowType.CHECKER,
                                                 WorkflowProcessStatus.CHECKED),
                                  WorkflowStatus.UNDER_CONSIDERATION, ctv, true, "USER1");

        assertTrue(checker2_1.isView());
        assertFalse(checker2_1.isUpdate());
        assertFalse(checker2_1.isCheckApprove());

        assertFalse(checker2_1.isVerification());
        // Checker2
        WorkflowIndex checker2_2 =
                new WorkflowIndex(createWorkflow(WorkflowType.CHECKER,
                                                 WorkflowProcessStatus.CHECKED),
                                  WorkflowStatus.UNDER_CONSIDERATION, ctv, true, "USER0");

        assertTrue(checker2_2.isView());
        assertFalse(checker2_2.isUpdate());
        assertFalse(checker2_2.isCheckApprove());

        assertFalse(checker2_2.isVerification());


        // Approver
        WorkflowIndex approver =
                new WorkflowIndex(createWorkflow(WorkflowType.APPROVER,
                                                 WorkflowProcessStatus.REQUEST_FOR_APPROVAL),
                                  WorkflowStatus.UNDER_CONSIDERATION, ctv, false, "USER1");

        assertTrue(approver.isView());
        assertTrue(approver.isUpdate());
        assertTrue(approver.isCheckApprove());

        assertFalse(approver.isVerification());

        // Approver
        WorkflowIndex approver1_0 =
                new WorkflowIndex(createWorkflow(WorkflowType.APPROVER,
                                                 WorkflowProcessStatus.REQUEST_FOR_APPROVAL),
                                  WorkflowStatus.UNDER_CONSIDERATION, ctv, false, "USER0");

        assertTrue(approver1_0.isView());
        assertTrue(approver1_0.isUpdate());
        assertTrue(approver1_0.isCheckApprove());

        assertTrue(approver1_0.isVerification());
        // Approver
        WorkflowIndex approver1_1 =
                new WorkflowIndex(createWorkflow(WorkflowType.APPROVER,
                                                 WorkflowProcessStatus.REQUEST_FOR_APPROVAL),
                                  WorkflowStatus.UNDER_CONSIDERATION, ctv, true, "USER1");

        assertTrue(approver1_1.isView());
        assertTrue(approver1_1.isUpdate());
        assertTrue(approver1_1.isCheckApprove());

        assertTrue(approver1_1.isVerification());

        // Approver
        WorkflowIndex approver1_2 =
                new WorkflowIndex(createWorkflow(WorkflowType.APPROVER,
                                                 WorkflowProcessStatus.REQUEST_FOR_APPROVAL),
                                  WorkflowStatus.UNDER_CONSIDERATION, ctv, true, "USER0");

        assertTrue(approver1_2.isView());
        assertTrue(approver1_2.isUpdate());
        assertTrue(approver1_2.isCheckApprove());

        assertTrue(approver1_2.isVerification());

    }

    /**
     * 表示設定のテスト. パターン2+パターン4で承認後.
     */
    @Test
    public void testWorkflowIndex2_4() {
        CorresponType ctv = new CorresponType();
        WorkflowPattern wp = new WorkflowPattern();
        wp.setWorkflowCd("002");
        ctv.setWorkflowPattern(wp);
        ctv.setAllowApproverToBrowse(AllowApproverToBrowse.VISIBLE);

        // Preparer
        WorkflowIndex preparer =
                new WorkflowIndex(createWorkflow(null, null), WorkflowStatus.ISSUED,
                                  ctv, false, "USER1");

        assertTrue(preparer.isView());
        assertFalse(preparer.isUpdate());
        assertFalse(preparer.isCheckApprove());

        assertFalse(preparer.isVerification());

        // Preparer
        WorkflowIndex preparer1_0 =
                new WorkflowIndex(createWorkflow(null, null), WorkflowStatus.ISSUED,
                                  ctv, false, "USER0");

        assertTrue(preparer1_0.isView());
        assertFalse(preparer1_0.isUpdate());
        assertFalse(preparer1_0.isCheckApprove());

        assertFalse(preparer1_0.isVerification());
        // Preparer
        WorkflowIndex preparer1_1 =
                new WorkflowIndex(createWorkflow(null, null), WorkflowStatus.ISSUED,
                                  ctv, true, "USER1");

        assertTrue(preparer1_1.isView());
        assertFalse(preparer1_1.isUpdate());
        assertFalse(preparer1_1.isCheckApprove());

        assertFalse(preparer1_1.isVerification());
        // Preparer
        WorkflowIndex preparer1_2 =
                new WorkflowIndex(createWorkflow(null, null), WorkflowStatus.ISSUED,
                                  ctv, true, "USER0");

        assertTrue(preparer1_2.isView());
        assertFalse(preparer1_2.isUpdate());
        assertFalse(preparer1_2.isCheckApprove());

        assertFalse(preparer1_2.isVerification());

        // Checker1
        WorkflowIndex checker1 =
                new WorkflowIndex(createWorkflow(WorkflowType.CHECKER,
                                                 WorkflowProcessStatus.CHECKED),
                                  WorkflowStatus.ISSUED, ctv, false, "USER1");

        assertTrue(checker1.isView());
        assertFalse(checker1.isUpdate());
        assertFalse(checker1.isCheckApprove());

        assertFalse(checker1.isVerification());

        // Checker1
        WorkflowIndex checker1_0 =
                new WorkflowIndex(createWorkflow(WorkflowType.CHECKER,
                                                 WorkflowProcessStatus.CHECKED),
                                  WorkflowStatus.ISSUED, ctv, false, "USER0");

        assertTrue(checker1_0.isView());
        assertFalse(checker1_0.isUpdate());
        assertFalse(checker1_0.isCheckApprove());

        assertFalse(checker1_0.isVerification());
        // Checker1
        WorkflowIndex checker1_1 =
                new WorkflowIndex(createWorkflow(WorkflowType.CHECKER,
                                                 WorkflowProcessStatus.CHECKED),
                                  WorkflowStatus.ISSUED, ctv, true, "USER1");

        assertTrue(checker1_1.isView());
        assertFalse(checker1_1.isUpdate());
        assertFalse(checker1_1.isCheckApprove());

        assertFalse(checker1_1.isVerification());
        // Checker1
        WorkflowIndex checker1_2 =
                new WorkflowIndex(createWorkflow(WorkflowType.CHECKER,
                                                 WorkflowProcessStatus.CHECKED),
                                  WorkflowStatus.ISSUED, ctv, true, "USER0");

        assertTrue(checker1_2.isView());
        assertFalse(checker1_2.isUpdate());
        assertFalse(checker1_2.isCheckApprove());

        assertFalse(checker1_2.isVerification());

        // Checker2
        WorkflowIndex checker2 =
                new WorkflowIndex(createWorkflow(WorkflowType.CHECKER,
                                                 WorkflowProcessStatus.CHECKED),
                                  WorkflowStatus.ISSUED, ctv, false, "USER1");

        assertTrue(checker2.isView());
        assertFalse(checker2.isUpdate());
        assertFalse(checker2.isCheckApprove());

        assertFalse(checker2.isVerification());

        // Checker2
        WorkflowIndex checker2_0 =
                new WorkflowIndex(createWorkflow(WorkflowType.CHECKER,
                                                 WorkflowProcessStatus.CHECKED),
                                  WorkflowStatus.ISSUED, ctv, false, "USER0");

        assertTrue(checker2_0.isView());
        assertFalse(checker2_0.isUpdate());
        assertFalse(checker2_0.isCheckApprove());

        assertFalse(checker2_0.isVerification());

        // Checker2
        WorkflowIndex checker2_1 =
                new WorkflowIndex(createWorkflow(WorkflowType.CHECKER,
                                                 WorkflowProcessStatus.CHECKED),
                                  WorkflowStatus.ISSUED, ctv, true, "USER1");

        assertTrue(checker2_1.isView());
        assertFalse(checker2_1.isUpdate());
        assertFalse(checker2_1.isCheckApprove());

        assertFalse(checker2_1.isVerification());
        // Checker2
        WorkflowIndex checker2_2 =
                new WorkflowIndex(createWorkflow(WorkflowType.CHECKER,
                                                 WorkflowProcessStatus.CHECKED),
                                  WorkflowStatus.ISSUED, ctv, true, "USER0");

        assertTrue(checker2_2.isView());
        assertFalse(checker2_2.isUpdate());
        assertFalse(checker2_2.isCheckApprove());

        assertFalse(checker2_2.isVerification());

        // Approver
        WorkflowIndex approver =
                new WorkflowIndex(createWorkflow(WorkflowType.APPROVER,
                                                 WorkflowProcessStatus.APPROVED),
                                  WorkflowStatus.ISSUED, ctv, false, "USER1");

        assertTrue(approver.isView());
        assertFalse(approver.isUpdate());
        assertFalse(approver.isCheckApprove());

        assertFalse(approver.isVerification());
        // Approver
        WorkflowIndex approver1_0 =
                new WorkflowIndex(createWorkflow(WorkflowType.APPROVER,
                                                 WorkflowProcessStatus.APPROVED),
                                  WorkflowStatus.ISSUED, ctv, false, "USER0");

        assertTrue(approver1_0.isView());
        assertFalse(approver1_0.isUpdate());
        assertFalse(approver1_0.isCheckApprove());

        assertFalse(approver1_0.isVerification());
        // Approver
        WorkflowIndex approver1_1 =
                new WorkflowIndex(createWorkflow(WorkflowType.APPROVER,
                                                 WorkflowProcessStatus.APPROVED),
                                  WorkflowStatus.ISSUED, ctv, true, "USER1");

        assertTrue(approver1_1.isView());
        assertFalse(approver1_1.isUpdate());
        assertFalse(approver1_1.isCheckApprove());

        assertFalse(approver1_1.isVerification());
        // Approver
        WorkflowIndex approver1_2 =
                new WorkflowIndex(createWorkflow(WorkflowType.APPROVER,
                                                 WorkflowProcessStatus.APPROVED),
                                  WorkflowStatus.ISSUED, ctv, true, "USER0");

        assertTrue(approver1_2.isView());
        assertFalse(approver1_2.isUpdate());
        assertFalse(approver1_2.isCheckApprove());

        assertFalse(approver1_2.isVerification());

    }

    /**
     * 表示設定のテスト. パターン3で検証前.
     */
    @Test
    public void testWorkflowIndex3_1() {
        CorresponType ct = new CorresponType();
        WorkflowPattern wp = new WorkflowPattern();
        wp.setWorkflowCd("003");
        ct.setWorkflowPattern(wp);
        ct.setAllowApproverToBrowse(AllowApproverToBrowse.INVISIBLE);

        // Preparer
        WorkflowIndex preparer =
                new WorkflowIndex(createWorkflow(null, null), WorkflowStatus.REQUEST_FOR_CHECK,
                                  ct, false, "USER1");

        assertTrue(preparer.isView());
        assertFalse(preparer.isUpdate());
        assertFalse(preparer.isCheckApprove());

        assertFalse(preparer.isVerification());
        // Preparer
        WorkflowIndex preparer1_0 =
                new WorkflowIndex(createWorkflow(null, null), WorkflowStatus.REQUEST_FOR_CHECK,
                                  ct, false, "USER0");

        assertTrue(preparer1_0.isView());
        assertFalse(preparer1_0.isUpdate());
        assertFalse(preparer1_0.isCheckApprove());

        assertFalse(preparer1_0.isVerification());

        // Preparer
        WorkflowIndex preparer1_1 =
                new WorkflowIndex(createWorkflow(null, null), WorkflowStatus.REQUEST_FOR_CHECK,
                                  ct, true, "USER1");

        assertTrue(preparer1_1.isView());
        assertFalse(preparer1_1.isUpdate());
        assertFalse(preparer1_1.isCheckApprove());

        assertFalse(preparer1_1.isVerification());
        // Preparer
        WorkflowIndex preparer1_2 =
                new WorkflowIndex(createWorkflow(null, null), WorkflowStatus.REQUEST_FOR_CHECK,
                                  ct, true, "USER0");

        assertTrue(preparer1_2.isView());
        assertFalse(preparer1_2.isUpdate());
        assertFalse(preparer1_2.isCheckApprove());

        assertFalse(preparer1_2.isVerification());

        // Checker1
        WorkflowIndex checker1 =
                new WorkflowIndex(createWorkflow(WorkflowType.CHECKER,
                                                 WorkflowProcessStatus.REQUEST_FOR_CHECK),
                                  WorkflowStatus.REQUEST_FOR_CHECK, ct, false, "USER1");

        assertTrue(checker1.isView());
        assertTrue(checker1.isUpdate());
        assertTrue(checker1.isCheckApprove());

        assertFalse(checker1.isVerification());

        // Checker1
        WorkflowIndex checker1_0 =
                new WorkflowIndex(createWorkflow(WorkflowType.CHECKER,
                                                 WorkflowProcessStatus.REQUEST_FOR_CHECK),
                                  WorkflowStatus.REQUEST_FOR_CHECK, ct, false, "USER0");

        assertTrue(checker1_0.isView());
        assertTrue(checker1_0.isUpdate());
        assertTrue(checker1_0.isCheckApprove());

        assertTrue(checker1_0.isVerification());
        // Checker1
        WorkflowIndex checker1_1 =
                new WorkflowIndex(createWorkflow(WorkflowType.CHECKER,
                                                 WorkflowProcessStatus.REQUEST_FOR_CHECK),
                                  WorkflowStatus.REQUEST_FOR_CHECK, ct, true, "USER1");

        assertTrue(checker1_1.isView());
        assertTrue(checker1_1.isUpdate());
        assertTrue(checker1_1.isCheckApprove());

        assertTrue(checker1_1.isVerification());
        // Checker1
        WorkflowIndex checker1_2 =
                new WorkflowIndex(createWorkflow(WorkflowType.CHECKER,
                                                 WorkflowProcessStatus.REQUEST_FOR_CHECK),
                                  WorkflowStatus.REQUEST_FOR_CHECK, ct, true, "USER0");

        assertTrue(checker1_2.isView());
        assertTrue(checker1_2.isUpdate());
        assertTrue(checker1_2.isCheckApprove());

        assertTrue(checker1_2.isVerification());

        // Checker2
        WorkflowIndex checker2 =
                new WorkflowIndex(createWorkflow(WorkflowType.CHECKER,
                                                 WorkflowProcessStatus.REQUEST_FOR_CHECK),
                                  WorkflowStatus.REQUEST_FOR_CHECK, ct, false, "USER1");

        assertTrue(checker2.isView());
        assertTrue(checker2.isUpdate());
        assertTrue(checker2.isCheckApprove());

        assertFalse(checker2.isVerification());

        // Checker2
        WorkflowIndex checker2_0 =
                new WorkflowIndex(createWorkflow(WorkflowType.CHECKER,
                                                 WorkflowProcessStatus.REQUEST_FOR_CHECK),
                                  WorkflowStatus.REQUEST_FOR_CHECK, ct, false, "USER0");

        assertTrue(checker2_0.isView());
        assertTrue(checker2_0.isUpdate());
        assertTrue(checker2_0.isCheckApprove());

        assertTrue(checker2_0.isVerification());

        // Checker2
        WorkflowIndex checker2_1 =
                new WorkflowIndex(createWorkflow(WorkflowType.CHECKER,
                                                 WorkflowProcessStatus.REQUEST_FOR_CHECK),
                                  WorkflowStatus.REQUEST_FOR_CHECK, ct, true, "USER1");

        assertTrue(checker2_1.isView());
        assertTrue(checker2_1.isUpdate());
        assertTrue(checker2_1.isCheckApprove());

        assertTrue(checker2_1.isVerification());
        // Checker2
        WorkflowIndex checker2_2 =
                new WorkflowIndex(createWorkflow(WorkflowType.CHECKER,
                                                 WorkflowProcessStatus.REQUEST_FOR_CHECK),
                                  WorkflowStatus.REQUEST_FOR_CHECK, ct, true, "USER0");

        assertTrue(checker2_2.isView());
        assertTrue(checker2_2.isUpdate());
        assertTrue(checker2_2.isCheckApprove());

        assertTrue(checker2_2.isVerification());
        // Approver
        WorkflowIndex approver =
                new WorkflowIndex(createWorkflow(WorkflowType.APPROVER,
                                                 WorkflowProcessStatus.REQUEST_FOR_APPROVAL),
                                  WorkflowStatus.REQUEST_FOR_CHECK, ct, false, "USER1");

        assertTrue(approver.isView());
        assertTrue(approver.isUpdate());
        assertTrue(approver.isCheckApprove());

        assertFalse(approver.isVerification());

        // Approver
        WorkflowIndex approver1_0 =
                new WorkflowIndex(createWorkflow(WorkflowType.APPROVER,
                                                 WorkflowProcessStatus.REQUEST_FOR_APPROVAL),
                                  WorkflowStatus.REQUEST_FOR_CHECK, ct, false, "USER0");

        assertTrue(approver1_0.isView());
        assertTrue(approver1_0.isUpdate());
        assertTrue(approver1_0.isCheckApprove());

        assertTrue(approver1_0.isVerification());
        // Approver
        WorkflowIndex approver1_1 =
                new WorkflowIndex(createWorkflow(WorkflowType.APPROVER,
                                                 WorkflowProcessStatus.REQUEST_FOR_APPROVAL),
                                  WorkflowStatus.REQUEST_FOR_CHECK, ct, true, "USER1");

        assertTrue(approver1_1.isView());
        assertTrue(approver1_1.isUpdate());
        assertTrue(approver1_1.isCheckApprove());

        assertTrue(approver1_1.isVerification());

        // Approver
        WorkflowIndex approver1_2 =
                new WorkflowIndex(createWorkflow(WorkflowType.APPROVER,
                                                 WorkflowProcessStatus.REQUEST_FOR_APPROVAL),
                                  WorkflowStatus.REQUEST_FOR_CHECK, ct, true, "USER0");

        assertTrue(approver1_2.isView());
        assertTrue(approver1_2.isUpdate());
        assertTrue(approver1_2.isCheckApprove());

        assertTrue(approver1_2.isVerification());

    }

    /**
     * 表示設定のテスト. パターン3で検証中.
     */
    @Test
    public void testWorkflowIndex3_2() {
        CorresponType ct = new CorresponType();
        WorkflowPattern wp = new WorkflowPattern();
        wp.setWorkflowCd("003");
        ct.setWorkflowPattern(wp);
        ct.setAllowApproverToBrowse(AllowApproverToBrowse.INVISIBLE);

        // Preparer
        WorkflowIndex preparer =
                new WorkflowIndex(createWorkflow(null, null), WorkflowStatus.UNDER_CONSIDERATION,
                                  ct, false, "USER1");

        assertTrue(preparer.isView());
        assertFalse(preparer.isUpdate());
        assertFalse(preparer.isCheckApprove());

        assertFalse(preparer.isVerification());

        // Preparer
        WorkflowIndex preparer1_0 =
                new WorkflowIndex(createWorkflow(null, null), WorkflowStatus.UNDER_CONSIDERATION,
                                  ct, false, "USER0");

        assertTrue(preparer1_0.isView());
        assertFalse(preparer1_0.isUpdate());
        assertFalse(preparer1_0.isCheckApprove());

        assertFalse(preparer1_0.isVerification());
        // Preparer
        WorkflowIndex preparer1_1 =
                new WorkflowIndex(createWorkflow(null, null), WorkflowStatus.UNDER_CONSIDERATION,
                                  ct, true, "USER1");

        assertTrue(preparer1_1.isView());
        assertFalse(preparer1_1.isUpdate());
        assertFalse(preparer1_1.isCheckApprove());

        assertFalse(preparer1_1.isVerification());
        // Preparer
        WorkflowIndex preparer1_2 =
                new WorkflowIndex(createWorkflow(null, null), WorkflowStatus.UNDER_CONSIDERATION,
                                  ct, true, "USER0");

        assertTrue(preparer1_2.isView());
        assertFalse(preparer1_2.isUpdate());
        assertFalse(preparer1_2.isCheckApprove());

        assertFalse(preparer1_2.isVerification());

        // Checker1
        WorkflowIndex checker1 =
                new WorkflowIndex(createWorkflow(WorkflowType.CHECKER,
                                                 WorkflowProcessStatus.CHECKED),
                                  WorkflowStatus.UNDER_CONSIDERATION,
                                  ct, false, "USER1");

        assertTrue(checker1.isView());
        assertFalse(checker1.isUpdate());
        assertFalse(checker1.isCheckApprove());

        assertFalse(checker1.isVerification());

        // Checker1
        WorkflowIndex checker1_0 =
                new WorkflowIndex(createWorkflow(WorkflowType.CHECKER,
                                                 WorkflowProcessStatus.CHECKED),
                                  WorkflowStatus.UNDER_CONSIDERATION,
                                  ct, false, "USER0");

        assertTrue(checker1_0.isView());
        assertFalse(checker1_0.isUpdate());
        assertFalse(checker1_0.isCheckApprove());

        assertFalse(checker1_0.isVerification());

        // Checker1
        WorkflowIndex checker1_1 =
                new WorkflowIndex(createWorkflow(WorkflowType.CHECKER,
                                                 WorkflowProcessStatus.CHECKED),
                                  WorkflowStatus.UNDER_CONSIDERATION,
                                  ct, true, "USER1");

        assertTrue(checker1_1.isView());
        assertFalse(checker1_1.isUpdate());
        assertFalse(checker1_1.isCheckApprove());

        assertFalse(checker1_1.isVerification());

        // Checker1
        WorkflowIndex checker1_2 =
                new WorkflowIndex(createWorkflow(WorkflowType.CHECKER,
                                                 WorkflowProcessStatus.CHECKED),
                                  WorkflowStatus.UNDER_CONSIDERATION,
                                  ct, true, "USER0");

        assertTrue(checker1_2.isView());
        assertFalse(checker1_2.isUpdate());
        assertFalse(checker1_2.isCheckApprove());

        assertFalse(checker1_2.isVerification());



        // Checker2
        WorkflowIndex checker2 =
                new WorkflowIndex(createWorkflow(WorkflowType.CHECKER,
                                                 WorkflowProcessStatus.REQUEST_FOR_CHECK),
                                  WorkflowStatus.UNDER_CONSIDERATION,
                                  ct, false, "USER1");

        assertTrue(checker2.isView());
        assertTrue(checker2.isUpdate());
        assertTrue(checker2.isCheckApprove());

        assertFalse(checker2.isVerification());

        // Checker2
        WorkflowIndex checker2_0 =
                new WorkflowIndex(createWorkflow(WorkflowType.CHECKER,
                                                 WorkflowProcessStatus.REQUEST_FOR_CHECK),
                                  WorkflowStatus.UNDER_CONSIDERATION,
                                  ct, false, "USER0");

        assertTrue(checker2_0.isView());
        assertTrue(checker2_0.isUpdate());
        assertTrue(checker2_0.isCheckApprove());

        assertTrue(checker2_0.isVerification());
        // Checker2
        WorkflowIndex checker2_1 =
                new WorkflowIndex(createWorkflow(WorkflowType.CHECKER,
                                                 WorkflowProcessStatus.REQUEST_FOR_CHECK),
                                  WorkflowStatus.UNDER_CONSIDERATION,
                                  ct, true, "USER1");

        assertTrue(checker2_1.isView());
        assertTrue(checker2_1.isUpdate());
        assertTrue(checker2_1.isCheckApprove());

        assertTrue(checker2_1.isVerification());
        // Checker2
        WorkflowIndex checker2_2 =
                new WorkflowIndex(createWorkflow(WorkflowType.CHECKER,
                                                 WorkflowProcessStatus.REQUEST_FOR_CHECK),
                                  WorkflowStatus.UNDER_CONSIDERATION,
                                  ct, true, "USER0");

        assertTrue(checker2_2.isView());
        assertTrue(checker2_2.isUpdate());
        assertTrue(checker2_2.isCheckApprove());

        assertTrue(checker2_2.isVerification());

        // Approver
        WorkflowIndex approver =
                new WorkflowIndex(createWorkflow(WorkflowType.APPROVER,
                                                 WorkflowProcessStatus.REQUEST_FOR_APPROVAL),
                                  WorkflowStatus.UNDER_CONSIDERATION,
                                  ct, false, "USER1");

        assertTrue(approver.isView());
        assertTrue(approver.isUpdate());
        assertTrue(approver.isCheckApprove());

        assertFalse(approver.isVerification());


        // Approver
        WorkflowIndex approver1_0 =
                new WorkflowIndex(createWorkflow(WorkflowType.APPROVER,
                                                 WorkflowProcessStatus.REQUEST_FOR_APPROVAL),
                                  WorkflowStatus.UNDER_CONSIDERATION,
                                  ct, false, "USER0");

        assertTrue(approver1_0.isView());
        assertTrue(approver1_0.isUpdate());
        assertTrue(approver1_0.isCheckApprove());

        assertTrue(approver1_0.isVerification());
        // Approver
        WorkflowIndex approver1_1 =
                new WorkflowIndex(createWorkflow(WorkflowType.APPROVER,
                                                 WorkflowProcessStatus.REQUEST_FOR_APPROVAL),
                                  WorkflowStatus.UNDER_CONSIDERATION,
                                  ct, true, "USER1");

        assertTrue(approver1_1.isView());
        assertTrue(approver1_1.isUpdate());
        assertTrue(approver1_1.isCheckApprove());

        assertTrue(approver1_1.isVerification());

        // Approver
        WorkflowIndex approver1_2 =
                new WorkflowIndex(createWorkflow(WorkflowType.APPROVER,
                                                 WorkflowProcessStatus.REQUEST_FOR_APPROVAL),
                                  WorkflowStatus.UNDER_CONSIDERATION,
                                  ct, true, "USER0");

        assertTrue(approver1_2.isView());
        assertTrue(approver1_2.isUpdate());
        assertTrue(approver1_2.isCheckApprove());

        assertTrue(approver1_2.isVerification());

    }

    /**
     * 表示設定のテスト. パターン3でCheckerを残して承認後.
     */
    @Test
    public void testWorkflowIndex3_3() {
        CorresponType ct = new CorresponType();
        WorkflowPattern wp = new WorkflowPattern();
        wp.setWorkflowCd("003");
        ct.setWorkflowPattern(wp);
        ct.setAllowApproverToBrowse(AllowApproverToBrowse.INVISIBLE);

        // Preparer
        WorkflowIndex preparer =
                new WorkflowIndex(createWorkflow(null, null), WorkflowStatus.ISSUED,
                                  ct, false, "USER1");

        assertTrue(preparer.isView());
        assertFalse(preparer.isUpdate());
        assertFalse(preparer.isCheckApprove());

        assertFalse(preparer.isVerification());

        // Preparer
        WorkflowIndex preparer1_0 =
                new WorkflowIndex(createWorkflow(null, null), WorkflowStatus.ISSUED,
                                  ct, false, "USER0");

        assertTrue(preparer1_0.isView());
        assertFalse(preparer1_0.isUpdate());
        assertFalse(preparer1_0.isCheckApprove());

        assertFalse(preparer1_0.isVerification());
        // Preparer
        WorkflowIndex preparer1_1 =
                new WorkflowIndex(createWorkflow(null, null), WorkflowStatus.ISSUED,
                                  ct, true, "USER1");

        assertTrue(preparer1_1.isView());
        assertFalse(preparer1_1.isUpdate());
        assertFalse(preparer1_1.isCheckApprove());

        assertFalse(preparer1_1.isVerification());
        // Preparer
        WorkflowIndex preparer1_2 =
                new WorkflowIndex(createWorkflow(null, null), WorkflowStatus.ISSUED,
                                  ct, true, "USER0");

        assertTrue(preparer1_2.isView());
        assertFalse(preparer1_2.isUpdate());
        assertFalse(preparer1_2.isCheckApprove());

        assertFalse(preparer1_2.isVerification());

        // Checker1
        WorkflowIndex checker1 =
                new WorkflowIndex(createWorkflow(WorkflowType.CHECKER,
                                                 WorkflowProcessStatus.CHECKED),
                                  WorkflowStatus.ISSUED, ct, false, "USER1");

        assertTrue(checker1.isView());
        assertFalse(checker1.isUpdate());
        assertFalse(checker1.isCheckApprove());

        assertFalse(checker1.isVerification());
        // Checker1
        WorkflowIndex checker1_0 =
                new WorkflowIndex(createWorkflow(WorkflowType.CHECKER,
                                                 WorkflowProcessStatus.CHECKED),
                                  WorkflowStatus.ISSUED, ct, false, "USER0");

        assertTrue(checker1_0.isView());
        assertFalse(checker1_0.isUpdate());
        assertFalse(checker1_0.isCheckApprove());

        assertFalse(checker1_0.isVerification());

        // Checker1
        WorkflowIndex checker1_1 =
                new WorkflowIndex(createWorkflow(WorkflowType.CHECKER,
                                                 WorkflowProcessStatus.CHECKED),
                                  WorkflowStatus.ISSUED, ct, true, "USER1");

        assertTrue(checker1_1.isView());
        assertFalse(checker1_1.isUpdate());
        assertFalse(checker1_1.isCheckApprove());

        assertFalse(checker1_1.isVerification());
        // Checker1
        WorkflowIndex checker1_2 =
                new WorkflowIndex(createWorkflow(WorkflowType.CHECKER,
                                                 WorkflowProcessStatus.CHECKED),
                                  WorkflowStatus.ISSUED, ct, true, "USER0");

        assertTrue(checker1_2.isView());
        assertFalse(checker1_2.isUpdate());
        assertFalse(checker1_2.isCheckApprove());

        assertFalse(checker1_2.isVerification());

        // Checker2
        WorkflowIndex checker2 =
                new WorkflowIndex(createWorkflow(WorkflowType.CHECKER,
                                                 WorkflowProcessStatus.REQUEST_FOR_CHECK),
                                  WorkflowStatus.ISSUED, ct, false, "USER1");

        assertTrue(checker2.isView());
        assertFalse(checker2.isUpdate());
        assertFalse(checker2.isCheckApprove());

        assertFalse(checker2.isVerification());

        // Checker2
        WorkflowIndex checker2_0 =
                new WorkflowIndex(createWorkflow(WorkflowType.CHECKER,
                                                 WorkflowProcessStatus.REQUEST_FOR_CHECK),
                                  WorkflowStatus.ISSUED, ct, false, "USER0");

        assertTrue(checker2_0.isView());
        assertFalse(checker2_0.isUpdate());
        assertFalse(checker2_0.isCheckApprove());

        assertFalse(checker2_0.isVerification());
        // Checker2
        WorkflowIndex checker2_1 =
                new WorkflowIndex(createWorkflow(WorkflowType.CHECKER,
                                                 WorkflowProcessStatus.REQUEST_FOR_CHECK),
                                  WorkflowStatus.ISSUED, ct, true, "USER1");

        assertTrue(checker2_1.isView());
        assertFalse(checker2_1.isUpdate());
        assertFalse(checker2_1.isCheckApprove());

        assertFalse(checker2_1.isVerification());
        // Checker2
        WorkflowIndex checker2_2 =
                new WorkflowIndex(createWorkflow(WorkflowType.CHECKER,
                                                 WorkflowProcessStatus.REQUEST_FOR_CHECK),
                                  WorkflowStatus.ISSUED, ct, true, "USER0");

        assertTrue(checker2_2.isView());
        assertFalse(checker2_2.isUpdate());
        assertFalse(checker2_2.isCheckApprove());

        assertFalse(checker2_2.isVerification());

        // Approver
        WorkflowIndex approver =
                new WorkflowIndex(createWorkflow(WorkflowType.APPROVER,
                                                 WorkflowProcessStatus.APPROVED),
                                  WorkflowStatus.ISSUED, ct, false, "USER1");

        assertTrue(approver.isView());
        assertFalse(approver.isUpdate());
        assertFalse(approver.isCheckApprove());

        assertFalse(approver.isVerification());

        // Approver
        WorkflowIndex approver1_0 =
                new WorkflowIndex(createWorkflow(WorkflowType.APPROVER,
                                                 WorkflowProcessStatus.APPROVED),
                                  WorkflowStatus.ISSUED, ct, false, "USER0");

        assertTrue(approver1_0.isView());
        assertFalse(approver1_0.isUpdate());
        assertFalse(approver1_0.isCheckApprove());

        assertFalse(approver1_0.isVerification());
        // Approver
        WorkflowIndex approver1_1 =
                new WorkflowIndex(createWorkflow(WorkflowType.APPROVER,
                                                 WorkflowProcessStatus.APPROVED),
                                  WorkflowStatus.ISSUED, ct, true, "USER1");

        assertTrue(approver1_1.isView());
        assertFalse(approver1_1.isUpdate());
        assertFalse(approver1_1.isCheckApprove());

        assertFalse(approver1_1.isVerification());

        // Approver
        WorkflowIndex approver1_2 =
                new WorkflowIndex(createWorkflow(WorkflowType.APPROVER,
                                                 WorkflowProcessStatus.APPROVED),
                                  WorkflowStatus.ISSUED, ct, true, "USER0");

        assertTrue(approver1_2.isView());
        assertFalse(approver1_2.isUpdate());
        assertFalse(approver1_2.isCheckApprove());

        assertFalse(approver1_2.isVerification());

    }

    /**
     * 承認作業状態がNoneの時は全ての権限が無いことを確認
     * 但し次のケースでは、閲覧権限はON
     *  ・Approver閲覧許可フラグON
     *  ・承認状態がDenied
     *  ・承認作業状態がNone
     */
    @Test
    public void testWorkflowIndexWorkflowProcessStatusNone() {

        CorresponType ct = new CorresponType();
        WorkflowPattern wp = new WorkflowPattern();
        wp.setWorkflowCd("001");
        ct.setWorkflowPattern(wp);
        ct.setAllowApproverToBrowse(AllowApproverToBrowse.INVISIBLE);

        WorkflowIndex w =
                new WorkflowIndex(createWorkflow(WorkflowType.CHECKER,
                                                 WorkflowProcessStatus.NONE),
                                  WorkflowStatus.DENIED, ct, false, "USER0");

        assertFalse(w.isView());
        assertFalse(w.isUpdate());
        assertFalse(w.isCheckApprove());

        assertFalse(w.isVerification());

        w = new WorkflowIndex(createWorkflow(WorkflowType.APPROVER,
                                             WorkflowProcessStatus.NONE),
                              WorkflowStatus.DENIED, ct, false, "USER0");

        assertFalse(w.isView());
        assertFalse(w.isUpdate());
        assertFalse(w.isCheckApprove());

        assertFalse(w.isVerification());


        //  Approverが閲覧できる設定に変更して検証
        ct.setAllowApproverToBrowse(AllowApproverToBrowse.VISIBLE);

        //  Checkerは変わらず
        w = new WorkflowIndex(createWorkflow(WorkflowType.CHECKER,
                                             WorkflowProcessStatus.NONE),
                              WorkflowStatus.DENIED, ct, false, "USER0");
        assertFalse(w.isView());
        assertFalse(w.isUpdate());
        assertFalse(w.isCheckApprove());

        assertFalse(w.isVerification());

        //  Approverは閲覧権限だけ付与されている
        w = new WorkflowIndex(createWorkflow(WorkflowType.APPROVER,
                                             WorkflowProcessStatus.NONE),
                              WorkflowStatus.DENIED, ct, false, "USER0");

        assertTrue(w.isView());
        assertFalse(w.isUpdate());
        assertFalse(w.isCheckApprove());

        assertFalse(w.isVerification());

    }

    private Workflow createWorkflow(WorkflowType type, WorkflowProcessStatus status) {
        Workflow workflow = new Workflow();
        workflow.setWorkflowType(type);
        workflow.setWorkflowProcessStatus(status);

        User user = new User();
        user.setEmpNo("USER0");
        workflow.setUser(user);
        return workflow;
    }
}
