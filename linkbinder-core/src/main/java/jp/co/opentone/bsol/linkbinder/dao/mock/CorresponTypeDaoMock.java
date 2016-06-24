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

import jp.co.opentone.bsol.framework.core.dao.RecordNotFoundException;
import jp.co.opentone.bsol.linkbinder.dao.AbstractDao;
import jp.co.opentone.bsol.linkbinder.dao.CorresponTypeDao;
import jp.co.opentone.bsol.linkbinder.dto.CorresponType;
import jp.co.opentone.bsol.linkbinder.dto.WorkflowPattern;
import jp.co.opentone.bsol.linkbinder.dto.code.AllowApproverToBrowse;
import jp.co.opentone.bsol.linkbinder.dto.code.ForceToUseWorkflow;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponTypeCondition;

@Repository
public class CorresponTypeDaoMock extends AbstractDao<CorresponType> implements CorresponTypeDao {

    public CorresponTypeDaoMock() {
        super("mock");
    }

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public List<CorresponType> find(SearchCorresponTypeCondition conditon) {

        List<CorresponType> list = new ArrayList<CorresponType>();

        CorresponType coTy = new CorresponType();
        coTy.setId(Long.parseLong("1"));
        coTy.setName("Sample");
        coTy.setProjectId("PJ1");
        coTy.setProjectCorresponTypeId(new Long(1));
        coTy.setAllowApproverToBrowse(AllowApproverToBrowse.VISIBLE);
        coTy.setForceToUseWorkflow(ForceToUseWorkflow.OPTIONAL);
        WorkflowPattern pattern = new WorkflowPattern();
        pattern.setId(1L);
        pattern.setName("Pattern1");
        coTy.setWorkflowPattern(pattern);
        list.add(coTy);

        coTy = new CorresponType();
        coTy.setId(Long.parseLong("2"));
        coTy.setName("Sample2");
        coTy.setProjectId("PJ1");
        coTy.setProjectCorresponTypeId(new Long(2));
        coTy.setAllowApproverToBrowse(AllowApproverToBrowse.INVISIBLE);
        coTy.setForceToUseWorkflow(ForceToUseWorkflow.OPTIONAL);
        pattern = new WorkflowPattern();
        pattern.setId(2L);
        pattern.setName("Pattern2");
        coTy.setWorkflowPattern(pattern);
        list.add(coTy);

        coTy = new CorresponType();
        coTy.setId(Long.parseLong("3"));
        coTy.setName("Sample3");
        coTy.setProjectId("PJ1");
        coTy.setProjectCorresponTypeId(new Long(3));
        coTy.setAllowApproverToBrowse(AllowApproverToBrowse.INVISIBLE);
        coTy.setForceToUseWorkflow(ForceToUseWorkflow.REQUIRED);
        pattern = new WorkflowPattern();
        pattern.setId(3L);
        pattern.setName("Pattern3");
        coTy.setWorkflowPattern(pattern);
        list.add(coTy);

        coTy = new CorresponType();
        coTy.setId(Long.parseLong("4"));
        coTy.setName("Sample4");
        coTy.setProjectId("PJ1");
        coTy.setProjectCorresponTypeId(new Long(4));
        pattern = new WorkflowPattern();
        pattern.setId(3L);
        pattern.setName("Pattern3");
        coTy.setWorkflowPattern(pattern);
        list.add(coTy);

        coTy = new CorresponType();
        coTy.setId(Long.parseLong("5"));
        coTy.setName("Sample5");
        coTy.setProjectId("PJ1");
        coTy.setProjectCorresponTypeId(new Long(5));
        coTy.setAllowApproverToBrowse(AllowApproverToBrowse.VISIBLE);
        coTy.setForceToUseWorkflow(ForceToUseWorkflow.OPTIONAL);
        pattern = new WorkflowPattern();
        pattern.setId(1L);
        pattern.setName("Pattern1");
        coTy.setWorkflowPattern(pattern);
        list.add(coTy);

        return list;
    }

    @Override
    public CorresponType findById(Long id) {

        CorresponType coTy = new CorresponType();
        coTy.setId(Long.parseLong("1"));
        coTy.setName("Sample");
        coTy.setProjectId("PJ1");
        coTy.setProjectCorresponTypeId(new Long(1));

        return coTy;
    }

    public int count(SearchCorresponTypeCondition conditon) {
        return 5;
    }

    public List<CorresponType> findNotExist(String projectId) {
        return null;
    }
    public CorresponType findByProjectCorresponTypeId(Long projectCorresponTypeId) {
        return null;
    }

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.dao.CorresponTypeDao#findCheck(jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponTypeCondition)
     */
    public int countCheck(SearchCorresponTypeCondition condition) {
        return 0;
    }

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.dao.CorresponTypeDao#findByIdProjectId(java.lang.Long, java.lang.String)
     */
    public CorresponType findByIdProjectId(Long id, String projectId)
        throws RecordNotFoundException {
        return null;
    }
}
