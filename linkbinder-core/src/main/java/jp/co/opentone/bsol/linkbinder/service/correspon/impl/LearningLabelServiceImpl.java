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

import jp.co.opentone.bsol.framework.core.dao.KeyDuplicateException;
import jp.co.opentone.bsol.framework.core.dao.StaleRecordException;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.linkbinder.dao.CorresponLearningLabelDao;
import jp.co.opentone.bsol.linkbinder.dao.LearningLabelDao;
import jp.co.opentone.bsol.linkbinder.dto.Correspon;
import jp.co.opentone.bsol.linkbinder.dto.CorresponLearningLabel;
import jp.co.opentone.bsol.linkbinder.dto.LearningLabel;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.service.AbstractService;
import jp.co.opentone.bsol.linkbinder.service.correspon.LearningLabelService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    @Transactional(readOnly = true)
    public List<CorresponLearningLabel> findByCorresponId(Long corresponId) {
        CorresponLearningLabelDao dao = getDao(CorresponLearningLabelDao.class);
        return dao.findByCorresponId(corresponId);
    }

    @Override
    public void clearAllLearningLabels(Correspon correspon) throws ServiceAbortException {
        LearningLabelDao dao = getDao(LearningLabelDao.class);
        CorresponLearningLabelDao clDao = getDao(CorresponLearningLabelDao.class);
        try {
            for (CorresponLearningLabel cl : clDao.findByCorresponId(correspon.getId())) {
                // 関連を削除
                clDao.delete(cl);

                // 結果、未使用のラベルとなった場合はラベルも削除
                LearningLabel l = new LearningLabel();
                l.setId(cl.getLabelId());
                dao.deleteIfUnused(l);
            }
        } catch (KeyDuplicateException e) {
            throw new ServiceAbortException(e);
        } catch (StaleRecordException e) {
            throw new ServiceAbortException(
                    ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CORRESPON_ALREADY_UPDATED);
        }
    }

    @Override
    public void saveLearningLabels(Correspon correspon) throws ServiceAbortException {
        List<LearningLabel> labels = correspon.getLearningLabel();
        Set<LearningLabel> deleteCandidateLabels = new HashSet<>();

        LearningLabelDao dao = getDao(LearningLabelDao.class);
        CorresponLearningLabelDao clDao = getDao(CorresponLearningLabelDao.class);
        try {
            List<CorresponLearningLabel> exists = clDao.findByCorresponId(correspon.getId());
            // 保存対象に含まれていなければ削除する
            for (CorresponLearningLabel cl : exists) {
                LearningLabel found = (LearningLabel) CollectionUtils.find(labels, new Predicate() {
                    @Override
                    public boolean evaluate(Object o) {
                        return ((LearningLabel) o).getId().equals(cl.getLabelId());
                    }
                });
                if (found == null) {
                    clDao.delete(cl);

                    LearningLabel l = new LearningLabel();
                    l.setId(cl.getLabelId());
                    deleteCandidateLabels.add(l);
                }
            }

            // 新しいラベルを登録
            for (LearningLabel l : correspon.getLearningLabel()) {
                if (l.getId() < 0) {
                    l.setCreatedBy(getCurrentUser());
                    l.setUpdatedBy(getCurrentUser());
                    Long id = dao.create(l);
                    l.setId(id);
                }
                // 関連付けを登録
                CorresponLearningLabel found =
                        (CorresponLearningLabel) CollectionUtils.find(exists, new Predicate() {
                            @Override
                            public boolean evaluate(Object o) {
                                return ((CorresponLearningLabel) o).getLabelId().equals(l.getId());
                            }
                        });
                if (found == null) {
                    CorresponLearningLabel cl = new CorresponLearningLabel();
                    cl.setCorresponId(correspon.getId());
                    cl.setLabelId(l.getId());
                    cl.setCreatedBy(getCurrentUser());
                    cl.setUpdatedBy(getCurrentUser());

                    clDao.create(cl);
                }
            }

            for (LearningLabel l : deleteCandidateLabels) {
                // 結果、未使用のラベルとなった場合はラベルも削除
                dao.deleteIfUnused(l);
            }
        } catch (KeyDuplicateException e) {
            throw new ServiceAbortException(e);
        } catch (StaleRecordException e) {
            throw new ServiceAbortException(
                    ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CORRESPON_ALREADY_UPDATED);
        }
    }
}
