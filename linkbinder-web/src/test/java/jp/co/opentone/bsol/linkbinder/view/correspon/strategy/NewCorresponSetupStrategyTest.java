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
package jp.co.opentone.bsol.linkbinder.view.correspon.strategy;

import static org.junit.Assert.*;

import org.junit.Test;

import jp.co.opentone.bsol.linkbinder.dto.Correspon;
import jp.co.opentone.bsol.linkbinder.dto.code.CorresponStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowStatus;
import jp.co.opentone.bsol.linkbinder.view.correspon.util.CorresponEditMode;


/**
 * @author opentone
 */
public class NewCorresponSetupStrategyTest extends AbstractCorresponSetupStrategyTestCase {

    public NewCorresponSetupStrategyTest() {
        super(CorresponEditMode.NEW);
    }

    /**
     * {@link NewCorresponSetupStrategy#setup()}の検証.
     * @throws Exception
     */
    @Test
    public void testSetup() throws Exception {
        strategy.setup();

        Correspon actual = page.getCorrespon();
        assertNotNull(actual);
        assertEquals(currentProject.getProjectId(), actual.getProjectId());

        assertEquals(null, actual.getId());
        assertEquals(null, actual.getCorresponNo());
        assertEquals(null, actual.getParentCorresponId());
        assertEquals(null, actual.getParentCorresponNo());
        assertEquals(null, actual.getPreviousRevCorresponId());
        assertEquals(null, actual.getPreviousRevCorresponNo());
        assertEquals(null, actual.getFromCorresponGroup());
        assertEquals(CorresponStatus.OPEN, actual.getCorresponStatus());
        assertEquals(null, actual.getCorresponType());
        assertEquals(null, actual.getAddressCorresponGroups());
        assertEquals(null, actual.getSubject());
        assertEquals(null, actual.getBody());
        assertEquals(null, actual.getReplyRequired());
        assertEquals(null, actual.getDeadlineForReply());
        //  attachments
        assertEquals(0, actual.getAttachments().size());
        //  custom fields
        assertEquals(null, actual.getCustomField1Value());
        assertEquals(null, actual.getCustomField2Value());
        assertEquals(null, actual.getCustomField3Value());
        assertEquals(null, actual.getCustomField4Value());
        assertEquals(null, actual.getCustomField5Value());
        assertEquals(null, actual.getCustomField6Value());
        assertEquals(null, actual.getCustomField7Value());
        assertEquals(null, actual.getCustomField8Value());
        assertEquals(null, actual.getCustomField9Value());
        assertEquals(null, actual.getCustomField10Value());

        assertEquals(WorkflowStatus.DRAFT, actual.getWorkflowStatus());

    }
}
