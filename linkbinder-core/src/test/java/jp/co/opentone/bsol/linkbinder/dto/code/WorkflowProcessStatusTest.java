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

public class WorkflowProcessStatusTest {
    @Test
    public void testValues() {

        assertEquals(0, WorkflowProcessStatus.NONE.getValue().intValue());
        assertEquals("作業前", WorkflowProcessStatus.NONE.getLabel());
        assertEquals(1, WorkflowProcessStatus.REQUEST_FOR_CHECK.getValue().intValue());
        assertEquals("検証依頼中", WorkflowProcessStatus.REQUEST_FOR_CHECK.getLabel());
        assertEquals(2, WorkflowProcessStatus.CHECKED.getValue().intValue());
        assertEquals("検証済", WorkflowProcessStatus.CHECKED.getLabel());
        assertEquals(3, WorkflowProcessStatus.REQUEST_FOR_APPROVAL.getValue().intValue());
        assertEquals("承認依頼中", WorkflowProcessStatus.REQUEST_FOR_APPROVAL.getLabel());
        assertEquals(4, WorkflowProcessStatus.UNDER_CONSIDERATION.getValue().intValue());
        assertEquals("更新中", WorkflowProcessStatus.UNDER_CONSIDERATION.getLabel());
        assertEquals(5, WorkflowProcessStatus.DENIED.getValue().intValue());
        assertEquals("否認", WorkflowProcessStatus.DENIED.getLabel());
        assertEquals(6, WorkflowProcessStatus.APPROVED.getValue().intValue());
        assertEquals("承認済", WorkflowProcessStatus.APPROVED.getLabel());
    }

}
