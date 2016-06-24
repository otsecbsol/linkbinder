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

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.co.opentone.bsol.framework.core.dao.KeyDuplicateException;
import jp.co.opentone.bsol.framework.core.dao.RecordNotFoundException;
import jp.co.opentone.bsol.framework.core.dao.StaleRecordException;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.core.util.ArgumentValidator;
import jp.co.opentone.bsol.linkbinder.dao.CorresponReadStatusDao;
import jp.co.opentone.bsol.linkbinder.dto.CorresponReadStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.ReadStatus;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.service.AbstractService;
import jp.co.opentone.bsol.linkbinder.service.correspon.CorresponReadStatusService;

/**
 * このサービスでは既読・未読に関する処理を提供する.
 * @author opentone
 */
@Service
@Transactional(rollbackFor = { ServiceAbortException.class })
public class CorresponReadStatusServiceImpl extends AbstractService implements
        CorresponReadStatusService {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -2795456058133877087L;
    /**
     * 空のインスタンスを生成する.
     */
    public CorresponReadStatusServiceImpl() {
    }

    /*
     * (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.service.correspon.CorresponReadStatusService#readStatusCorrespon
     * (jp.co.opentone.bsol.linkbinder.dto.Correspon)
     */
    public Integer updateReadStatusByCorresponId(Long id , ReadStatus readStatus)
                                                      throws ServiceAbortException {
        ArgumentValidator.validateNotNull(id);
        ArgumentValidator.validateNotNull(readStatus);

        CorresponReadStatus corresponReadStatus = new  CorresponReadStatus();

        corresponReadStatus.setCorresponId(id);
        corresponReadStatus.setEmpNo(getCurrentUser().getEmpNo());
        corresponReadStatus.setCreatedBy(getCurrentUser());
        corresponReadStatus.setReadStatus(readStatus);
        corresponReadStatus.setUpdatedBy(getCurrentUser());

        return updateByCorresponId(corresponReadStatus);
    }


    /*
     * (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.service.correspon.CorresponReadStatusService#readStatusUnit
     * (jp.co.opentone.bsol.linkbinder.dto.Correspon)
     */
    public Long updateReadStatusById(Long id, ReadStatus  readStatus) throws ServiceAbortException {
        ArgumentValidator.validateNotNull(id);
        ArgumentValidator.validateNotNull(readStatus);

        CorresponReadStatus corresponReadStatus = null;
        if (id > 0) {
            corresponReadStatus = findReadStatus(id);
        }
        if (corresponReadStatus != null) {
            if (corresponReadStatus.getReadStatus() != readStatus) {
                // 更新件数がない際は、更新対象のIDを0で返す
                if (update(getUpdateReadStatus(corresponReadStatus, readStatus)) == 0) {
                    // 指定のコレポン情報が存在しない際はエラー
                    throw new ServiceAbortException(ApplicationMessageCode.NO_DATA_FOUND);
                }
            }
            return corresponReadStatus.getId();
        } else {
            return create(getCreateReadStatus(id, readStatus));
        }

    }

    /**
     * 既読・未読登録情報を取得する.
     * @param id コレポンID
     * @param readStatus 既読・未読状態
     * @return 登録既読・未読情報
     */
    private CorresponReadStatus getCreateReadStatus(Long id, ReadStatus readStatus) {
        CorresponReadStatus corresponReadStatus = new CorresponReadStatus();

        corresponReadStatus.setCorresponId(id);

        corresponReadStatus.setEmpNo(getCurrentUser().getEmpNo());
        corresponReadStatus.setCreatedBy(getCurrentUser());
        corresponReadStatus.setReadStatus(readStatus);
        corresponReadStatus.setUpdatedBy(getCurrentUser());

        return corresponReadStatus;
    }

    /**
     * 既読・未読更新情報を取得する.
     * @param oldReadStatus 更新対象既読・未読情報
     * @param readStatus 既読・未読状態
     * @return 更新既読・未読情報
     */
    private CorresponReadStatus getUpdateReadStatus(CorresponReadStatus oldReadStatus
                                                                    , ReadStatus readStatus) {
        CorresponReadStatus corresponReadStatus = new CorresponReadStatus();

        corresponReadStatus.setId(oldReadStatus.getId());
        corresponReadStatus.setReadStatus(readStatus);
        corresponReadStatus.setUpdatedBy(getCurrentUser());
        corresponReadStatus.setVersionNo(oldReadStatus.getVersionNo());

        return corresponReadStatus;
    }

    /**
     * 既読／未読ステータスを取得する.
     * @param id コレポンID
     * @return 既読／未読ステータス
     */
    private CorresponReadStatus findReadStatus(Long id) {
        try {
            CorresponReadStatusDao dao = getDao(CorresponReadStatusDao.class);
            return dao.findByEmpNo(id, getCurrentUser().getEmpNo());
        } catch (RecordNotFoundException e) {
            return null;
        }
    }


    /**
     * コレポン文書の既読・未読を作成する.
     * @param readStatus
     *            既読・未読状態
     * @return 登録対象既読・未読ID
     * @throws ServiceAbortException
     */
    private Long create(CorresponReadStatus readStatus) throws ServiceAbortException {
        CorresponReadStatusDao dao = getDao(CorresponReadStatusDao.class);
        try {
            return dao.create(readStatus);
        } catch (KeyDuplicateException e) {
            throw new ServiceAbortException(e);
        }
    }

    /**
     * コレポン文書の既読・未読を更新する.
     * @param readStatus
     *            既読・未読状態
     * @return 更新件数
     * @throws ServiceAbortException
     */
    private Integer update(CorresponReadStatus readStatus) throws ServiceAbortException {
        CorresponReadStatusDao dao = getDao(CorresponReadStatusDao.class);
        try {
            return dao.update(readStatus);
        } catch (KeyDuplicateException e) {
            throw new ServiceAbortException(e);
        } catch (StaleRecordException e) {
            // 排他エラーは設定しない（排他エラーをチェックしない）
            throw new ServiceAbortException(e);
        }
    }

    /**
     * コレポン文書の既読・未読を更新する.
     * @param readStatus
     *            既読・未読状態
     * @return 更新件数
     * @throws ServiceAbortException
     */
    private Integer updateByCorresponId(CorresponReadStatus readStatus)
                                                throws ServiceAbortException {
        CorresponReadStatusDao dao = getDao(CorresponReadStatusDao.class);
        try {
            return dao.updateByCorresponId(readStatus);
        } catch (KeyDuplicateException e) {
            throw new ServiceAbortException(e);
        }
    }
}
