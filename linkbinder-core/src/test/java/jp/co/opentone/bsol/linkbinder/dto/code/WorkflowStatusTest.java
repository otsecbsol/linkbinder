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
package jp.co.opentone.bsol.linkbinder.dto.code;

import static org.junit.Assert.*;

import org.junit.Test;

public class WorkflowStatusTest {

    @Test
    public void testValues() {

        assertEquals(0, WorkflowStatus.DRAFT.getValue().intValue());
        assertEquals("作成中", WorkflowStatus.DRAFT.getLabel());
        assertEquals(1, WorkflowStatus.REQUEST_FOR_CHECK.getValue().intValue());
        assertEquals("検証依頼", WorkflowStatus.REQUEST_FOR_CHECK.getLabel());
        assertEquals(2, WorkflowStatus.UNDER_CONSIDERATION.getValue().intValue());
        assertEquals("更新中", WorkflowStatus.UNDER_CONSIDERATION.getLabel());
        assertEquals(3, WorkflowStatus.REQUEST_FOR_APPROVAL.getValue().intValue());
        assertEquals("承認依頼", WorkflowStatus.REQUEST_FOR_APPROVAL.getLabel());
        assertEquals(4, WorkflowStatus.DENIED.getValue().intValue());
        assertEquals("否認", WorkflowStatus.DENIED.getLabel());
        assertEquals(5, WorkflowStatus.ISSUED.getValue().intValue());
        assertEquals("発行", WorkflowStatus.ISSUED.getLabel());
    }
}
