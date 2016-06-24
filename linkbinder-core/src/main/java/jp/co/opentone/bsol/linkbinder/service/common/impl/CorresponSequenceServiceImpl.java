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
package jp.co.opentone.bsol.linkbinder.service.common.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.co.opentone.bsol.framework.core.dao.KeyDuplicateException;
import jp.co.opentone.bsol.framework.core.dao.RecordNotFoundException;
import jp.co.opentone.bsol.framework.core.dao.StaleRecordException;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.core.util.ArgumentValidator;
import jp.co.opentone.bsol.linkbinder.dao.CorresponDao;
import jp.co.opentone.bsol.linkbinder.dao.CorresponGroupDao;
import jp.co.opentone.bsol.linkbinder.dao.ParentCorresponNoSeqDao;
import jp.co.opentone.bsol.linkbinder.dao.ReplyCorresponNoSeqDao;
import jp.co.opentone.bsol.linkbinder.dto.Correspon;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroup;
import jp.co.opentone.bsol.linkbinder.dto.Discipline;
import jp.co.opentone.bsol.linkbinder.dto.ParentCorresponNoSeq;
import jp.co.opentone.bsol.linkbinder.dto.ReplyCorresponNoSeq;
import jp.co.opentone.bsol.linkbinder.dto.Site;
import jp.co.opentone.bsol.linkbinder.dto.condition.ParentCorresponNoSeqCondition;
import jp.co.opentone.bsol.linkbinder.service.AbstractService;
import jp.co.opentone.bsol.linkbinder.service.common.CorresponSequenceService;

/**
 * このサービスではコレポン文書の発番に関する処理を提供する.
 * @author opentone
 */
@Service
@Transactional(rollbackFor = { ServiceAbortException.class })
public class CorresponSequenceServiceImpl extends AbstractService implements
        CorresponSequenceService {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 856848663952684359L;

    /*
     * (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.service.correspon.CorresponSequenceService#getCorresponNo
     * (jp.co.opentone.bsol.linkbinder.dto.Correspon)
     */
    public String getCorresponNo(Correspon correspon) throws ServiceAbortException {
        ArgumentValidator.validateNotNull(correspon);

        String corresponNo = null;
        // 親文書か返信文書か判定
        if (correspon.getParentCorresponId() == null) {
            // 親文書番号採番
            corresponNo = createParentCorresponNo(correspon);
        } else {
            // 返信文書番号採番
            corresponNo = createReplyCorresponNo(correspon);
        }
        return corresponNo;
    }

    /**
     * 親文書番号を採番する.
     * @param correspon
     *            コレポン文書
     * @return
     * @throws ServiceAbortException
     */
    private String createParentCorresponNo(Correspon correspon) throws ServiceAbortException {
        CorresponGroup corresponGroup =
                getCorresponGroup(correspon.getFromCorresponGroup().getId());
        Site site = corresponGroup.getSite();
        Discipline discipline = corresponGroup.getDiscipline();

        // 親文書番号採番情報を検索
        ParentCorresponNoSeq parentCorresponNoSeq =
                getParentCorresponNoSeq(site.getId(), discipline.getId());

        Long no = null;
        if (parentCorresponNoSeq == null) {
            // 情報を登録
            no = createParentCorresponNoSeq(site.getId(), discipline.getId());

        } else {
            // 情報を更新
            no = updateParentCorresponNoSeq(parentCorresponNoSeq);
        }

        // 親文書番号生成
        return site.getSiteCd() + ":" + discipline.getDisciplineCd()
                + "-" + String.format("%05d", no);
    }

    /**
     * コレポン文書グループを取得する.
     * @param parentCorresponId
     *            親コレポン文書ID
     * @return 返信文書番号採番情報
     * @throws ServiceAbortException
     */
    private CorresponGroup getCorresponGroup(Long id) throws ServiceAbortException {
        CorresponGroup corresponGroup = null;
        try {
            CorresponGroupDao dao = getDao(CorresponGroupDao.class);
            corresponGroup = dao.findById(id);
        } catch (RecordNotFoundException e) {
            throw new ServiceAbortException(e);
        }
        return corresponGroup;
    }

    /**
     * 親文書番号採番情報を取得する.
     * @param siteId
     *            拠点ID
     * @param disciplineId
     *            部門ID
     * @return 親文書番号採番情報
     */
    private ParentCorresponNoSeq getParentCorresponNoSeq(Long siteId, Long disciplineId) {
        ParentCorresponNoSeqCondition condition = new ParentCorresponNoSeqCondition();
        condition.setSiteId(siteId);
        condition.setDisciplineId(disciplineId);

        ParentCorresponNoSeqDao dao = getDao(ParentCorresponNoSeqDao.class);
        return dao.findForUpdate(condition);
    }

    /**
     * 親文書番号採番情報を登録する.
     * @param siteId
     *            拠点ID
     * @param disciplineId
     *            部門ID
     * @throws ServiceAbortException
     */
    private Long createParentCorresponNoSeq(Long siteId, Long disciplineId)
        throws ServiceAbortException {
        Long no = 1L;

        ParentCorresponNoSeq parentCorresponNoSeq = new ParentCorresponNoSeq();
        parentCorresponNoSeq.setSiteId(siteId);
        parentCorresponNoSeq.setDisciplineId(disciplineId);
        parentCorresponNoSeq.setNo(no);
        parentCorresponNoSeq.setCreatedBy(getCurrentUser());
        parentCorresponNoSeq.setUpdatedBy(getCurrentUser());

        try {
            ParentCorresponNoSeqDao dao = getDao(ParentCorresponNoSeqDao.class);
            dao.create(parentCorresponNoSeq);
        } catch (KeyDuplicateException e) {
            throw new ServiceAbortException(e);
        }
        return no;
    }

    /**
     * 親文書番号採番情報を更新する.
     * @param id
     *            ID
     * @throws ServiceAbortException
     */
    private Long updateParentCorresponNoSeq(ParentCorresponNoSeq parentCorresponNoSeq)
        throws ServiceAbortException {
        Long no = parentCorresponNoSeq.getNo() + 1;

        ParentCorresponNoSeq updateDto = new ParentCorresponNoSeq();
        updateDto.setId(parentCorresponNoSeq.getId());
        updateDto.setNo(no);
        updateDto.setUpdatedBy(getCurrentUser());
        updateDto.setVersionNo(parentCorresponNoSeq.getVersionNo());

        try {
            ParentCorresponNoSeqDao dao = getDao(ParentCorresponNoSeqDao.class);
            dao.update(updateDto);
        } catch (KeyDuplicateException e) {
            throw new ServiceAbortException(e);
        } catch (StaleRecordException e) {
            throw new ServiceAbortException(e);
        }
        return no;
    }

    /**
     * 返信文書番号を採番する.
     * @param correspon
     *            コレポン文書
     * @return
     * @throws ServiceAbortException
     */
    private String createReplyCorresponNo(Correspon correspon) throws ServiceAbortException {
        // 最上位のコレポン文書を検索
        Correspon parentCorrespon = getTopParentCorrespon(correspon.getParentCorresponId());
        // 返信文書番号採番情報を検索
        ReplyCorresponNoSeq replyCorresponNoSeq = getReplyCorresponNoSeq(parentCorrespon.getId());

        Long no = null;
        if (replyCorresponNoSeq == null) {
            // 情報を登録
            no = createReplyCorresponNoSeq(parentCorrespon.getId());
        } else {
            // 情報を更新
            no = updateReplytCorresponNoSeq(replyCorresponNoSeq);
        }

        // 返信文書番号生成
        return parentCorrespon.getCorresponNo() + "-" + String.format("%03d", no);
    }

    /**
     * 最上位のコレポン文書を取得する.
     * @param parentCorresponId
     *            親コレポン文書ID
     * @return 返信文書番号採番情報
     * @throws ServiceAbortException
     */
    private Correspon getTopParentCorrespon(Long parentCorresponId) throws ServiceAbortException {
        Correspon correspon = null;
        try {
            CorresponDao dao = getDao(CorresponDao.class);
            correspon = dao.findTopParent(parentCorresponId);
        } catch (RecordNotFoundException e) {
            throw new ServiceAbortException(e);
        }
        return correspon;
    }

    /**
     * 返信文書番号採番情報を取得する.
     * @param parentCorresponId
     *            親コレポン文書ID
     * @return 返信文書番号採番情報
     */
    private ReplyCorresponNoSeq getReplyCorresponNoSeq(Long parentCorresponId) {
        ReplyCorresponNoSeqDao dao = getDao(ReplyCorresponNoSeqDao.class);
        return dao.findForUpdate(parentCorresponId);
    }

    /**
     * 返信文書番号採番情報を登録する.
     * @param parentCorresponId
     *            親コレポン文書ID
     * @throws ServiceAbortException
     */
    private Long createReplyCorresponNoSeq(Long parentCorresponId) throws ServiceAbortException {
        Long no = 1L;

        ReplyCorresponNoSeq replyCorresponNoSeq = new ReplyCorresponNoSeq();
        replyCorresponNoSeq.setParentCorresponId(parentCorresponId);
        replyCorresponNoSeq.setNo(no);
        replyCorresponNoSeq.setCreatedBy(getCurrentUser());
        replyCorresponNoSeq.setUpdatedBy(getCurrentUser());
        try {
            ReplyCorresponNoSeqDao dao = getDao(ReplyCorresponNoSeqDao.class);
            dao.create(replyCorresponNoSeq);
        } catch (KeyDuplicateException e) {
            throw new ServiceAbortException(e);
        }
        return no;
    }

    /**
     * 返信文書番号採番情報を更新する.
     * @param id
     *            ID
     * @throws ServiceAbortException
     */
    private Long updateReplytCorresponNoSeq(ReplyCorresponNoSeq replyCorresponNoSeq)
        throws ServiceAbortException {
        Long no = replyCorresponNoSeq.getNo() + 1;

        ReplyCorresponNoSeq updateDto = new ReplyCorresponNoSeq();
        updateDto.setId(replyCorresponNoSeq.getId());
        updateDto.setNo(no);
        updateDto.setUpdatedBy(getCurrentUser());
        updateDto.setVersionNo(replyCorresponNoSeq.getVersionNo());

        try {
            ReplyCorresponNoSeqDao dao = getDao(ReplyCorresponNoSeqDao.class);
            dao.update(updateDto);
        } catch (KeyDuplicateException e) {
            throw new ServiceAbortException(e);
        } catch (StaleRecordException e) {
            throw new ServiceAbortException(e);
        }
        return no;
    }
}
