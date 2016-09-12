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
import jp.co.opentone.bsol.linkbinder.dao.CorresponLearningTagDao;
import jp.co.opentone.bsol.linkbinder.dao.LearningTagDao;
import jp.co.opentone.bsol.linkbinder.dto.Correspon;
import jp.co.opentone.bsol.linkbinder.dto.CorresponLearningTag;
import jp.co.opentone.bsol.linkbinder.dto.LearningTag;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.service.AbstractService;
import jp.co.opentone.bsol.linkbinder.service.correspon.LearningTagService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * このサービスでは学習用タグに関する処理を提供する.
 * @author opentone
 */
@Service
@Transactional(rollbackFor = { ServiceAbortException.class })
public class LearningTagServiceImpl extends AbstractService implements LearningTagService {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 4004774537778286277L;
    /**
     * logger.
     */
    private static final Logger log = LoggerFactory.getLogger(LearningTagServiceImpl.class);

    @Override
    @Transactional(readOnly = true)
    public List<LearningTag> findAll() throws ServiceAbortException {
        LearningTagDao dao = getDao(LearningTagDao.class);
        return dao.findByProjectId(getCurrentProjectId());
    }

    @Override
    public void clearAllLearningTags(Correspon correspon) throws ServiceAbortException {
        LearningTagDao dao = getDao(LearningTagDao.class);
        CorresponLearningTagDao ctDao = getDao(CorresponLearningTagDao.class);
        try {
            for (CorresponLearningTag ct : ctDao.findByCorresponId(correspon.getId())) {
                // 関連を削除
                ctDao.delete(ct);

                // 結果、未使用のタグとなった場合はタグも削除
                LearningTag t = new LearningTag();
                t.setId(ct.getTagId());
                dao.deleteIfUnused(t);
            }
        } catch (StaleRecordException e) {
            throw new ServiceAbortException(
                    ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CORRESPON_ALREADY_UPDATED);
        } catch (KeyDuplicateException e) {
            throw new ServiceAbortException(e);
        }
    }

    @Override
    public void saveLearningTags(Correspon correspon) throws ServiceAbortException {
        List<LearningTag> tags = correspon.getLearningTag();
        Set<LearningTag> deleteCandidateTags = new HashSet<>();

        LearningTagDao dao = getDao(LearningTagDao.class);
        CorresponLearningTagDao ctDao = getDao(CorresponLearningTagDao.class);
        try {
            List<CorresponLearningTag> exists = ctDao.findByCorresponId(correspon.getId());
            // 保存対象に含まれていなければ削除する
            for (CorresponLearningTag ct : exists) {
                LearningTag found = (LearningTag) CollectionUtils.find(tags, o -> ((LearningTag) o).getId().equals(ct.getTagId()));
                if (found == null) {
                    ctDao.delete(ct);

                    LearningTag t = new LearningTag();
                    t.setId(ct.getTagId());
                    deleteCandidateTags.add(t);
                }
            }

            // 新しいタグを登録
            for (LearningTag t : correspon.getLearningTag()) {
                if (t.getId() < 0) {
                    t.setCreatedBy(getCurrentUser());
                    t.setUpdatedBy(getCurrentUser());
                    Long id = dao.create(t);
                    t.setId(id);
                }
                // 関連付けを登録
                CorresponLearningTag found =
                        (CorresponLearningTag) CollectionUtils.find(exists, o -> ((CorresponLearningTag) o).getTagId().equals(t.getId()));
                if (found == null) {
                    CorresponLearningTag ct = new CorresponLearningTag();
                    ct.setCorresponId(correspon.getId());
                    ct.setTagId(t.getId());
                    ct.setCreatedBy(getCurrentUser());
                    ct.setUpdatedBy(getCurrentUser());

                    ctDao.create(ct);
                }
            }

            // 結果、未使用のタグとなった場合はタグも削除
            deleteCandidateTags.forEach(dao::deleteIfUnused);
        } catch (StaleRecordException e) {
            throw new ServiceAbortException(
                    ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CORRESPON_ALREADY_UPDATED);
        } catch (KeyDuplicateException e) {
            throw new ServiceAbortException(e);
        }
    }
}
