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

public class WorkflowTypeTest {

    @Test
    public void testValues() {

        assertEquals(1, WorkflowType.CHECKER.getValue().intValue());
        assertEquals("検証者", WorkflowType.CHECKER.getLabel());
        assertEquals(2, WorkflowType.APPROVER.getValue().intValue());
        assertEquals("承認者", WorkflowType.APPROVER.getLabel());
    }
}
