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
package jp.co.opentone.bsol.linkbinder.dao;

import org.springframework.dao.DataIntegrityViolationException;

import jp.co.opentone.bsol.framework.core.dao.Entity;
import jp.co.opentone.bsol.framework.core.dao.GenericDao;
import jp.co.opentone.bsol.framework.core.dao.KeyDuplicateException;
import jp.co.opentone.bsol.framework.core.dao.RecordNotFoundException;
import jp.co.opentone.bsol.framework.core.dao.StaleRecordException;
import jp.co.opentone.bsol.framework.core.dao.VersioningEntity;

/**
 * Daoの親クラス.
 *
 * @author opentone
 *
 */
public abstract class AbstractDao<T extends Entity> extends BaseDao implements GenericDao<T> {

    /**
     * SerialVersionID.
     */
    private static final long serialVersionUID = 8264014886725665143L;

    /**
     * SQLID: 新規作成.
     */
    public static final String SQL_ID_CREATE = "create";
    /**
     * SQLID: 更新.
     */
    public static final String SQL_ID_UPDATE = "update";
    /**
     * SQLID: 削除.
     */
    public static final String SQL_ID_DELETE = "delete";
    /**
     * SQLID: 1件取得.
     */
    public static final String SQL_ID_FIND_BY_ID = "findById";
    /**
     * SQLID: 1件取得(FOR UPDATE).
     */
    public static final String SQL_ID_FIND_BY_ID_FOR_UPDATE = "findByIdForUpdate";

    /**
     * SQL定義ファイルの名前空間を指定してインスタンス化する.
     * @param namespace
     *            SQL定義ファイルのnamespace
     */
    public AbstractDao(String namespace) {
        super(namespace);
    }

    /*
     * (非 Javadoc)
     * @see
     * jp.co.opentone.bsol.framework.core.enericDao#create(jp.c.framework.core.daok.core.dao.Entity)
     */
    public Long create(T entity) throws KeyDuplicateException {
        try {
            return (Long) getSqlMapClientTemplate().insert(getSqlId(SQL_ID_CREATE), entity);
        } catch (DataIntegrityViolationException e) {
            KeyDuplicateException ex = new KeyDuplicateException("primary key duplicate.", entity);
            ex.initCause(e);

            throw ex;
        }
    }

    /*
     * (非 Javadoc)
     * @see
     *.framework.core.daomework.core.dao.GenericDao#u.framework.core.daoc.framework.core.dao.Entity)
     */
    public Integer update(T entity) throws KeyDuplicateException, StaleRecordException {
        return update(entity, SQL_ID_UPDATE);
    }

    private Integer update(T dto, String sqlId) throws KeyDuplicateException, StaleRecordException {

        checkVersionNo(dto);
        try {
            return getSqlMapClientTemplate().update(getSqlId(sqlId), dto);
        } catch (DataIntegrityViolationException e) {
            KeyDuplicateException ex =
                    new KeyDuplicateException("primary key or foreign key duplicate.", dto);
            ex.initCause(e);
            throw ex;
        }
    }

    private void checkVersionNo(T dto) throws StaleRecordException {
        if (!(dto instanceof VersioningEntity)) {
            return;
        }
        try {
            T current = findByIdForUpdate(dto.getId());
            if (!((VersioningEntity) current).getVersionNo()
                                             .equals(((VersioningEntity) dto).getVersionNo())) {
                throw new StaleRecordException("record is already old. version_no : "
                        + ((VersioningEntity) current).getVersionNo());
            }
        } catch (RecordNotFoundException e) {
            throw new StaleRecordException("record already deleted.");
        }
    }

    /*
     * (non-Javadoc)
     * @see GenericDao#delete(Entity)
     */
    public Integer delete(T entity) throws KeyDuplicateException, StaleRecordException {
        return update(entity, SQL_ID_DELETE);
    }

    /*
     * (non-Javadoc)
     * @see GenericDao#findById(java.lang.Long)
     */
    @SuppressWarnings("unchecked")
    public T findById(Long id) throws RecordNotFoundException {
        T record = (T) getSqlMapClientTemplate().queryForObject(getSqlId(SQL_ID_FIND_BY_ID), id);

        if (record == null) {
            throw new RecordNotFoundException(id.toString());
        }
        return record;
    }

    /*
     * (non-Javadoc)
     * @see GenericDao#findByIdForUpdate(java.lang.Long)
     */
    @SuppressWarnings("unchecked")
    public T findByIdForUpdate(Long id) throws RecordNotFoundException {
        T record =
                (T) getSqlMapClientTemplate()
                                             .queryForObject(
                                                             getSqlId(SQL_ID_FIND_BY_ID_FOR_UPDATE),
                                                             id);

        if (record == null) {
            throw new RecordNotFoundException(id.toString());
        }
        return record;
    }
}
