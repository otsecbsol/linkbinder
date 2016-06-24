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

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.co.opentone.bsol.framework.core.dao.KeyDuplicateException;
import jp.co.opentone.bsol.framework.core.dao.RecordNotFoundException;
import jp.co.opentone.bsol.framework.core.dao.StaleRecordException;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.core.util.ArgumentValidator;
import jp.co.opentone.bsol.linkbinder.dao.FavoriteFilterDao;
import jp.co.opentone.bsol.linkbinder.dto.FavoriteFilter;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchFavoriteFilterCondition;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.service.AbstractService;
import jp.co.opentone.bsol.linkbinder.service.common.FavoriteFilterService;

/**
 * FavoriteFilter のCRUDを提供する.
 * @author opentone
 */
@Service
@Transactional(rollbackFor = { ServiceAbortException.class })
public class FavoriteFilterServiceImpl extends AbstractService implements FavoriteFilterService {

    /**
     * logger.
     */
    private static Logger log = LoggerFactory.getLogger(FavoriteFilterServiceImpl.class);
    /**
     * 生成シリアルID.
     */
    private static final long serialVersionUID = -7433664602126389071L;

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.service.common.
     * FavoriteFilterService#search(jp.co.opentone.bsol.linkbinder.dto.condition.SearchFavoriteFilterCondition)
     */
    @Override
    @Transactional(readOnly = true)
    public List<FavoriteFilter> search(SearchFavoriteFilterCondition condition)
            throws ServiceAbortException {
        ArgumentValidator.validateNotNull(condition);

        FavoriteFilterDao dao = getDao(FavoriteFilterDao.class);
        return dao.find(condition);
    }

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.service.common.FavoriteFilterService#find(java.lang.Long)
     */
    @Override
    @Transactional(readOnly = true)
    public FavoriteFilter find(Long id) throws ServiceAbortException {
        ArgumentValidator.validateNotNull(id);

        FavoriteFilterDao dao = getDao(FavoriteFilterDao.class);
        FavoriteFilter result = null;
        try {
            result = dao.findById(id);

            // アクセス可能かどうかチェック
            validateAccess(result);

        } catch (RecordNotFoundException e) {
            if (log.isDebugEnabled()) {
                log.debug(e.getMessage());
            }
            throw new ServiceAbortException(ApplicationMessageCode.NO_DATA_FOUND);
        }
        return result;
    }

    /**
     * @param result
     * @throws ServiceAbortException
     */
    private void validateAccess(FavoriteFilter result) throws ServiceAbortException {
        validateProjectId(result.getProjectId());
        // 自分で作成したFavoriteFilterかどうかチェック
        User curUser = getCurrentUser();
        User filterUser = result.getUser();
        ArgumentValidator.validateNotNull(curUser);
        ArgumentValidator.validateNotNull(filterUser);
        if (curUser.getEmpNo().equals(filterUser.getEmpNo())) {
            return;
        }
        throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT);
    }

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.service.common.FavoriteFilterService
     *      #save(jp.co.opentone.bsol.linkbinder.dto.FavoriteFilter)
     */
    @Override
    public void save(FavoriteFilter favoriteFilter) throws ServiceAbortException {
        ArgumentValidator.validateNotNull(favoriteFilter);

        User curUser = getCurrentUser();
        favoriteFilter.setUser(curUser);
        favoriteFilter.setUpdatedBy(curUser);
        if (favoriteFilter.isNew()) {
            favoriteFilter.setCreatedBy(curUser);
            create(favoriteFilter);
            return;
        }
        update(favoriteFilter);
    }

    private void create(FavoriteFilter favoriteFilter) throws ServiceAbortException {

        FavoriteFilterDao dao = getDao(FavoriteFilterDao.class);
        try {
            dao.create(favoriteFilter);
        } catch (KeyDuplicateException e) {
            if (log.isDebugEnabled()) {
                log.debug(e.getMessage());
            }
            throw new ServiceAbortException(e);
        }
    }

    private void update(FavoriteFilter favoriteFilter) throws ServiceAbortException {

        FavoriteFilterDao dao = getDao(FavoriteFilterDao.class);
        try {
            dao.update(favoriteFilter);
        } catch (KeyDuplicateException e) {
            if (log.isDebugEnabled()) {
                log.debug(e.getMessage());
            }
            throw new ServiceAbortException(e);
        } catch (StaleRecordException e) {
            if (log.isDebugEnabled()) {
                log.debug(e.getMessage());
            }
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_FAVORITE_FILTER_ALREADY_UPDATED);
        }
    }

    /*
     * (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.service.common.FavoriteFilterService
     *      #delete(jp.co.opentone.bsol.linkbinder.dto.FavoriteFilter)
     */
    @Override
    public void delete(FavoriteFilter favoriteFilter) throws ServiceAbortException {
        ArgumentValidator.validateNotNull(favoriteFilter);
        ArgumentValidator.validateNotNull(favoriteFilter.getId());

        FavoriteFilterDao dao = getDao(FavoriteFilterDao.class);
        favoriteFilter.setUpdatedBy(getCurrentUser());
        try {
            dao.delete(favoriteFilter);
        } catch (KeyDuplicateException e) {
            if (log.isDebugEnabled()) {
                log.debug(e.getMessage());
            }
            throw new ServiceAbortException(e);
        } catch (StaleRecordException e) {
            if (log.isDebugEnabled()) {
                log.debug(e.getMessage());
            }
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_FAVORITE_FILTER_ALREADY_UPDATED);
        }
    }

}
