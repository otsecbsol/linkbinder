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
package jp.co.opentone.bsol.linkbinder.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import jp.co.opentone.bsol.framework.core.dao.KeyDuplicateException;
import jp.co.opentone.bsol.linkbinder.dao.AbstractDao;
import jp.co.opentone.bsol.linkbinder.dao.CorresponGroupUserDao;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroupUser;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroupUserMapping;
import jp.co.opentone.bsol.linkbinder.dto.User;

/**
 * correspon_group_user を操作するDao.
 *
 * @author opentone
 *
 */
@Repository
public class CorresponGroupUserDaoImpl extends AbstractDao<CorresponGroupUser> implements
    CorresponGroupUserDao {
    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -8737897486918546334L;
    /**
     * このクラスが利用するsqlMap.xmlのnamespace.
     */
    private static final String NAMESPACE = "corresponGroupUser";

    /**
     * SQLID: 検索条件を指定して活動単位に所属するユーザーを検索する.
     */
    private static final String SQL_FIND_BY_CORRESPON_GROUP_ID = "findByCorresponGroupId";
    /**
     * SQLID: 検索条件を指定して活動単位に所属するユーザーを検索する.
     */
    private static final String SQL_FIND_BY_PROJECT_ID = "findByProjectId";
    /**
     * SQLID: 検索条件を指定して活動単位に所属するユーザーを検索する.
     * v_project_userを主とし、紐付くv_correspon_group_userを検索する.
     */
    private static final String SQL_FIND_PROJECT_USER_WITH_GROUP_BY_PROJECT_ID
        = "findProjectUserWithGroupByProjectId";
    /**
     * SQLID: 検索条件を指定して活動単位に所属するユーザーを1件検索する.
     */
    private static final String SQL_FIND_BY_EMP_NO = "findByEmpNo";
    /**
     * SQLID: 活動単位とそれに所属するユーザーのマッピング情報.
     */
    private static final String SQL_FIND_CORRESPON_GROUP_USER_MAPPING =
            "findCorresponGroupUserMapping";
    /**
     * SQLID: 活動単位とそれに所属するユーザーのマッピング情報.
     * グループID＝0を取得する際に、当該グループのグループIDも返す.
     * コレポン文書の宛先選択ダイアログ用.
     */
    private static final String SQL_FIND_CORRESPON_GROUP_ID_USER_MAPPING =
            "findCorresponGroupIdUserMapping";

    /**
     * SQLID: 活動単位IDを指定して活動単位ユーザーを削除する.
     */
    private static final String SQL_DELETE_BY_CORRESPON_GROUP_ID = "deleteByCorresponGroupId";

    /**
     * 空のインスタンスを生成する.
     */
    public CorresponGroupUserDaoImpl() {
        super(NAMESPACE);
    }

    /*
     * (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.dao.CorresponGroupUserDao#findByCorresponGroupId(java
     * .lang.Long)
     */
    @SuppressWarnings("unchecked")
    public List<CorresponGroupUser> findByCorresponGroupId(Long corresponGroupId) {
        return (List<CorresponGroupUser>) getSqlMapClientTemplate()
            .queryForList(getSqlId(SQL_FIND_BY_CORRESPON_GROUP_ID), corresponGroupId);
    }

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.dao.CorresponGroupUserDao#findByProjectId(java.lang.String)
     */
    @SuppressWarnings("unchecked")
    public List<CorresponGroupUser> findByProjectId(String projectId) {
        return (List<CorresponGroupUser>) getSqlMapClientTemplate()
            .queryForList(getSqlId(SQL_FIND_BY_PROJECT_ID), projectId);
    }

    /*
     * (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.dao.CorresponGroupUserDao#findByEmpNo(java.lang.Long,
     * java.lang.String)
     */
    public CorresponGroupUser findByEmpNo(Long corresponGroupId, String empNo) {
        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("corresponGroupId", corresponGroupId);
        condition.put("empNo", empNo);

        return (CorresponGroupUser) getSqlMapClientTemplate()
            .queryForObject(getSqlId(SQL_FIND_BY_EMP_NO), condition);
    }

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.dao.CorresponGroupUserDao#findCorresponGroupUserMapping
     * (java.lang.String)
     */
    @SuppressWarnings("unchecked")
    public List<CorresponGroupUserMapping> findCorresponGroupUserMapping(String projectId) {
        Map<String, String> condition = new HashMap<String, String>();
        condition.put("projectId1", projectId);
        condition.put("projectId2", projectId);
        return (List<CorresponGroupUserMapping>) getSqlMapClientTemplate()
            .queryForList(getSqlId(SQL_FIND_CORRESPON_GROUP_USER_MAPPING), condition);
    }

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.dao.CorresponGroupUserDao#findCorresponGroupIdUserMapping
     * (java.lang.String)
     */
    @SuppressWarnings("unchecked")
    public List<CorresponGroupUserMapping> findCorresponGroupIdUserMapping(String projectId) {
        Map<String, String> condition = new HashMap<String, String>();
        condition.put("projectId1", projectId);
        condition.put("projectId2", projectId);
        return (List<CorresponGroupUserMapping>) getSqlMapClientTemplate()
            .queryForList(getSqlId(SQL_FIND_CORRESPON_GROUP_ID_USER_MAPPING), condition);
    }

    /*
     * (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.dao.CorresponGroupUserDao#deleteByCorresponGroupId(java
     * .lang.Long)
     */
    public Integer deleteByCorresponGroupId(Long corresponGroupId, User updateUser)
        throws KeyDuplicateException {
        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("corresponGroupId", corresponGroupId);
        condition.put("empNo", updateUser.getEmpNo());

        return getSqlMapClientTemplate().update(getSqlId(SQL_DELETE_BY_CORRESPON_GROUP_ID),
            condition);
    }

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.dao.CorresponGroupUserDao#
     * findProjectUserWithGroupByProjectId(java.lang.String)
     */
    @SuppressWarnings("unchecked")
    public List<CorresponGroupUser> findProjectUserWithGroupByProjectId(
            String projectId) {
        return (List<CorresponGroupUser>) getSqlMapClientTemplate()
            .queryForList(getSqlId(SQL_FIND_PROJECT_USER_WITH_GROUP_BY_PROJECT_ID),
                    projectId);
    }
}