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
package jp.co.opentone.bsol.linkbinder.dto.condition;

import static org.junit.Assert.*;

import org.junit.Test;

import jp.co.opentone.bsol.linkbinder.dto.code.AddressType;
import jp.co.opentone.bsol.linkbinder.dto.code.AddressUserType;
import jp.co.opentone.bsol.linkbinder.dto.code.CorresponStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.RSSCategory;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowProcessStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowType;

public class SearchRSSCorresponConditionTest {

    @Test
    public void testGetter() {
        SearchRSSCorresponCondition s = new SearchRSSCorresponCondition();

        assertEquals(AddressType.TO, s.getTo());
        assertEquals(1, s.getTo().getValue().intValue());
        assertEquals(AddressType.CC, s.getCc());
        assertEquals(2, s.getCc().getValue().intValue());

        assertEquals(AddressUserType.ATTENTION, s.getAttention());
        assertEquals(2, s.getAttention().getValue().intValue());
        assertEquals(AddressUserType.NORMAL_USER, s.getNormalUser());
        assertEquals(1, s.getNormalUser().getValue().intValue());

        assertEquals(WorkflowStatus.ISSUED, s.getIssued());
        assertEquals(5, s.getIssued().getValue().intValue());
        assertEquals(WorkflowStatus.REQUEST_FOR_CHECK, s.getRequestForCheck());
        assertEquals(1, s.getRequestForCheck().getValue().intValue());
        assertEquals(WorkflowStatus.REQUEST_FOR_APPROVAL, s.getRequestForApproval());
        assertEquals(3, s.getRequestForApproval().getValue().intValue());
        assertEquals(WorkflowStatus.UNDER_CONSIDERATION, s.getUnderConsideration());
        assertEquals(2, s.getUnderConsideration().getValue().intValue());
        assertEquals(WorkflowStatus.DENIED, s.getDenied());
        assertEquals(4, s.getDenied().getValue().intValue());

        assertEquals(CorresponStatus.OPEN, s.getOpen());
        assertEquals(0, s.getOpen().getValue().intValue());
        assertEquals(CorresponStatus.CLOSED, s.getClosed());
        assertEquals(1, s.getClosed().getValue().intValue());

        assertEquals(WorkflowType.CHECKER, s.getChecker());
        assertEquals(1, s.getChecker().getValue().intValue());
        assertEquals(WorkflowType.APPROVER, s.getApprover());
        assertEquals(2, s.getApprover().getValue().intValue());

        assertEquals(WorkflowProcessStatus.REQUEST_FOR_CHECK, s.getRequestForCheckProcess());
        assertEquals(1, s.getRequestForCheckProcess().getValue().intValue());
        assertEquals(WorkflowProcessStatus.REQUEST_FOR_APPROVAL, s.getRequestForApprovalProcess());
        assertEquals(3, s.getRequestForApprovalProcess().getValue().intValue());

        assertEquals(RSSCategory.ISSUE_NOTICE_ATTENTION, s.getCategory1());
        assertEquals(1, s.getCategory1().getValue().intValue());
        assertEquals(RSSCategory.ISSUE_NOTICE_CC, s.getCategory2());
        assertEquals(2, s.getCategory2().getValue().intValue());
        assertEquals(RSSCategory.REQUEST_FOR_CHECK, s.getCategory3());
        assertEquals(3, s.getCategory3().getValue().intValue());
        assertEquals(RSSCategory.REQUEST_FOR_APPROVAL, s.getCategory4());
        assertEquals(4, s.getCategory4().getValue().intValue());
        assertEquals(RSSCategory.PERSON_IN_CHARGE, s.getCategory5());
        assertEquals(5, s.getCategory5().getValue().intValue());
        assertEquals(RSSCategory.DENIED, s.getCategory6());
        assertEquals(6, s.getCategory6().getValue().intValue());

    }
}
