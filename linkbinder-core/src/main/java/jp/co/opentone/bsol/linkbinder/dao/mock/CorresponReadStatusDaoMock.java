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

import org.springframework.stereotype.Repository;

import jp.co.opentone.bsol.framework.core.dao.KeyDuplicateException;
import jp.co.opentone.bsol.framework.core.dao.RecordNotFoundException;
import jp.co.opentone.bsol.linkbinder.dao.AbstractDao;
import jp.co.opentone.bsol.linkbinder.dao.CorresponReadStatusDao;
import jp.co.opentone.bsol.linkbinder.dto.CorresponReadStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.ReadStatus;

@Repository
public class CorresponReadStatusDaoMock extends AbstractDao<CorresponReadStatus> implements CorresponReadStatusDao {

    /**
     * serialVersionUID.
     */
    private static final long serialVersionUID = -1160725424163794251L;

    public CorresponReadStatusDaoMock() {
        super("mock");
    }

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.dao.CorresponReadStatusDao#findByEmpNo(java.lang.Long, java.lang.String)
     */
    public CorresponReadStatus findByEmpNo(Long id, String empNo) throws RecordNotFoundException {

        CorresponReadStatus corresponReadStatus = new CorresponReadStatus();

        corresponReadStatus.setCorresponId(1L);
        corresponReadStatus.setId(1L);
        corresponReadStatus.setDeleteNo(0L);
        corresponReadStatus.setEmpNo("E0001");
        corresponReadStatus.setVersionNo(1L);

        corresponReadStatus.setReadStatus(ReadStatus.NEW);
        if (id == 1) {
            corresponReadStatus.setReadStatus(ReadStatus.READ);
        }

        return corresponReadStatus;
    }

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.dao.CorresponReadStatusDao#updateByCorresponId(jp.co.opentone.bsol.linkbinder.dto.CorresponReadStatus)
     */
    public Integer updateByCorresponId(CorresponReadStatus dto) throws KeyDuplicateException {
        return 1;
    }


}
