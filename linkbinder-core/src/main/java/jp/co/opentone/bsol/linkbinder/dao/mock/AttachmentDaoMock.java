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

import jp.co.opentone.bsol.linkbinder.dao.AbstractDao;
import jp.co.opentone.bsol.linkbinder.dao.AttachmentDao;
import jp.co.opentone.bsol.linkbinder.dto.Attachment;

@Repository
public class AttachmentDaoMock extends AbstractDao<Attachment> implements AttachmentDao {

    public AttachmentDaoMock() {
        super("mock");
    }

    /**
     *
     */
    private static final long serialVersionUID = 2281517404428789272L;

    public Attachment findById(Long id) {
        Attachment a = new Attachment();
        a.setId(id);
        a.setFileId("05740CB");
        a.setFileName("textfile.txt");
        a.setCorresponId(1L);
        a.setDeleteNo(0L);
        return a;
    }

    public List<Attachment> findByCorresponId(Long corresponId) {
        List<Attachment> a = new ArrayList<Attachment>();

        return a;
    }
}
