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
package jp.co.opentone.bsol.linkbinder.service.correspon.impl;

import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.linkbinder.dao.CorresponLearningLabelDao;
import jp.co.opentone.bsol.linkbinder.dao.LearningLabelDao;
import jp.co.opentone.bsol.linkbinder.dto.Correspon;
import jp.co.opentone.bsol.linkbinder.dto.CorresponLearningLabel;
import jp.co.opentone.bsol.linkbinder.dto.LearningLabel;
import jp.co.opentone.bsol.linkbinder.service.AbstractService;
import jp.co.opentone.bsol.linkbinder.service.correspon.LearningLabelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * このサービスでは学習用ラベルに関する処理を提供する.
 * @author opentone
 */
@Service
@Transactional(rollbackFor = { ServiceAbortException.class })
public class LearningLabelServiceImpl extends AbstractService implements LearningLabelService {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 4004774537778286277L;
    /**
     * logger.
     */
    private static final Logger log = LoggerFactory.getLogger(LearningLabelServiceImpl.class);
    /**
     * 空のインスタンスを生成する.
     */
    public LearningLabelServiceImpl() {
    }

    @Override
    @Transactional(readOnly = true)
    public List<LearningLabel> findAll() throws ServiceAbortException {
        LearningLabelDao dao = getDao(LearningLabelDao.class);
        return dao.findByProjectId(getCurrentProjectId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LearningLabel> findExsistLabel() {
        LearningLabelDao dao = getDao(LearningLabelDao.class);
        return dao.findExsistLabel();
    }

    @Override
    public Long insertLearningLabel(LearningLabel label) {
        LearningLabelDao dao = getDao(LearningLabelDao.class);
        return dao.insertLearningLabel(label);
    }

    @Override
    public Long insertCorresponLearningLabel(LearningLabel label,Correspon correspon) {
        CorresponLearningLabelDao dao = getDao(CorresponLearningLabelDao.class);
        CorresponLearningLabel corresponLearningLabel = new CorresponLearningLabel();

        corresponLearningLabel.setCorresponIdId(correspon.getId());
        corresponLearningLabel.setLabelId(label.getId());
        corresponLearningLabel.setCreatedBy(correspon.getCreatedBy());
        corresponLearningLabel.setUpdatedBy(correspon.getUpdatedBy());

        return dao.insertLearningLabel(corresponLearningLabel);
    }

    @Override
    public List<CorresponLearningLabel> findByCorresponId(Long corresponId) {
        CorresponLearningLabelDao dao = getDao(CorresponLearningLabelDao.class);
        return dao.findByCorresponId(corresponId);
    }
}
