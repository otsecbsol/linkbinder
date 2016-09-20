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

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import jp.co.opentone.bsol.framework.core.dao.KeyDuplicateException;
import jp.co.opentone.bsol.framework.core.dao.RecordNotFoundException;
import jp.co.opentone.bsol.framework.core.dao.StaleRecordException;
import jp.co.opentone.bsol.linkbinder.dao.AbstractLegacyDao;
import jp.co.opentone.bsol.linkbinder.dao.ProjectDao;
import jp.co.opentone.bsol.linkbinder.dto.Project;
import jp.co.opentone.bsol.linkbinder.dto.ProjectSummary;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchProjectCondition;

/**
 * project を操作するDao.
 *
 * @author opentone
 *
 */
@Repository
public class ProjectDaoImpl extends AbstractLegacyDao<Project> implements ProjectDao {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -4582562323460192204L;

    /**
     * このクラスが利用するsqlMap.xmlのnamespace.
     */
    private static final String NAMESPACE = "project";

    /**
     * SQLID: 従業員番号を指定してプロジェクトを取得する.
     */
    private static final String SQL_FIND_BY_EMP_NO = "findByEmpNo";

    /**
     * SQLID: 検索条件を指定してプロジェクトを取得する.
     */
    private static final String SQL_FIND = "find";

    /**
     * SQLID: 検索条件を指定して学習用プロジェクトを取得する.
     *
     */
    private static final String SQL_FIND_LEARNING = "findLearningPj";

    /**
     * SQLID: 従業員番号を指定してプロジェクトサマリを取得する.
     */
    private static final String SQL_FIND_PROJECT_SUMMARY = "findProjectSummary";

    /**
     * SQLID: 検索条件に該当する件数を取得する.
     */
    private static final String SQL_COUNT = "count";

    /**
     * SQLID: 検索条件を指定してプロジェクト(SYS_PJ)を取得する.
     */
    private static final String SQL_FIND_ALL = "findAllSysPJ";

    /**
     * SQLID: 登録されているプロジェクト数(SYS_PJ)を取得する.
     */
    private static final String SQL_FIND_POROJECT_COUNT = "findProjectCount";

    /**
     * SQLID: 従業員番号とシステムコードで利用可能システムの該当件数を取得する。(利用可能チェック).
     */
    private static final String SQL_COUNT_AVAILABLE_APP_SYSTEM_CODE = "countAvailableAppSystemCode";

    private static final String SQL_SELECT_PROJECT = "findBySysPJId";
    private static final String SQL_CREATED_PROJECT = "createProject";
    private static final String SQL_UPDATE_PROJECT = "updateProject";
    private static final String SQL_UPDATE_PROJECT_AUTH = "updateProjectAuth";
    private static final String SQL_DELETE_PROJECT = "deleteProject";
    private static final String SQL_DELETE_PROJECT_AUTH = "deleteProjectAuth";


    /**
     * 前方一致検索を行うフィールド名.
     */
    private static final List<String> FIELDS;

    static {
        FIELDS = new ArrayList<String>();
        FIELDS.add("projectId");
        FIELDS.add("nameE");
    }

    /**
     * 空のインスタンスを生成する.
     */
    public ProjectDaoImpl() {
        super(NAMESPACE);
    }

    /*
     * (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.dao.ProjectDao#findByEmpNo(java.lang.String)
     */
    @SuppressWarnings("unchecked")
    public List<Project> findByEmpNo(String empNo) {
        return (List<Project>) getSqlMapClientTemplate().queryForList(getSqlId(SQL_FIND_BY_EMP_NO),
                                                                      empNo);
    }

    /*
     * (non-Javadoc)
     * @seejp.co.opentone.bsol.linkbinder.dao.ProjectDao#find(jp.co.opentone.bsol.linkbinder.dto.
     * SearchProjectCondition)
     */
    @SuppressWarnings("unchecked")
    public List<Project> find(SearchProjectCondition condition) {
        // 前方一致検索を行う
        SearchProjectCondition likeCondition = getLikeSearchCondition(condition, FIELDS);
        int skipResults = condition.getSkipNum();
        int maxResults = condition.getPageRowNum();
        return (List<Project>) getSqlMapClientTemplate()
                                            .queryForList(getSqlId(SQL_FIND),
                                                          likeCondition,
                                                          skipResults,
                                                          maxResults);
    }

    /*
 * (non-Javadoc)
 * @seejp.co.opentone.bsol.linkbinder.dao.ProjectDao#find(jp.co.opentone.bsol.linkbinder.dto.
 * SearchProjectCondition)
 */
    @SuppressWarnings("unchecked")
    public List<Project> findLearningPj(SearchProjectCondition condition) {
        // 前方一致検索を行う
        SearchProjectCondition likeCondition = getLikeSearchCondition(condition, FIELDS);
        int skipResults = condition.getSkipNum();
        int maxResults = condition.getPageRowNum();
        return (List<Project>) getSqlMapClientTemplate()
                .queryForList(getSqlId(SQL_FIND_LEARNING),
                        likeCondition,
                        skipResults,
                        maxResults);
    }

    public List<Project> findForCsvDownload(SearchProjectCondition condition) {
        // 前方一致検索を行う
        return (List<Project>) getSqlMapClientTemplate()
                                            .queryForList(getSqlId(SQL_FIND_ALL), condition);
    }

    /*
     * (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.dao.ProjectDao#findProjectSummary(
     * jp.co.opentone.bsol.linkbinder.dto.condition.SearchProjectCondition)
     */
    @SuppressWarnings("unchecked")
    public List<ProjectSummary> findProjectSummary(SearchProjectCondition condition) {
        return (List<ProjectSummary>) getSqlMapClientTemplate()
                                .queryForList(getSqlId(SQL_FIND_PROJECT_SUMMARY), condition);
    }

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.dao.ProjectDao#count(
     *     jp.co.opentone.bsol.linkbinder.dto.condition.SearchProjectCondition)
     */
    public int count(SearchProjectCondition condition) {
        // 前方一致検索を行う
        SearchProjectCondition likeCondition = getLikeSearchCondition(condition, FIELDS);
        return Integer.parseInt(getSqlMapClientTemplate().queryForObject(getSqlId(SQL_COUNT),
            likeCondition).toString());
    }

    /*
     * (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.dao.ProjectDao#findById(java.lang.String)
     */
    public Project findById(String projectId) throws RecordNotFoundException {
        Project record = (Project) getSqlMapClientTemplate()
            .queryForObject(getSqlId(SQL_ID_FIND_BY_ID), projectId);
        if (record == null) {
            throw new RecordNotFoundException(projectId);
        }
        return record;
    }



    /*
     * (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.dao.ProjectDao#findBySysPJId(java.lang.String)
     */
    public String findBySysPJId(String projectId) throws RecordNotFoundException {
        String record = (String) getSqlMapClientTemplate()
            .queryForObject(getSqlId(SQL_SELECT_PROJECT), projectId);
        if (record == null) {
            throw new RecordNotFoundException(projectId);
        }
        return record;
    }
    public void createProject(Project project) throws KeyDuplicateException {
        getSqlMapClientTemplate()
                .insert(getSqlId(SQL_CREATED_PROJECT), project);
    }

    public void updateProject(Project project)
            throws RecordNotFoundException, KeyDuplicateException, StaleRecordException {
        int result = 0;
        if (0 < getSqlMapClientTemplate().update(getSqlId(SQL_UPDATE_PROJECT),
                project)){
            result = getSqlMapClientTemplate().update(getSqlId(SQL_UPDATE_PROJECT_AUTH),
                    project);
        } else {
            throw new KeyDuplicateException();
        }
        if (result == 0) {
            throw new KeyDuplicateException();
        }

    }

    public void deleteProject(Project project)
            throws RecordNotFoundException, KeyDuplicateException, StaleRecordException {
        int result = 0;
        if (0 < getSqlMapClientTemplate().delete(getSqlId(SQL_DELETE_PROJECT),
                project)) {
            result = getSqlMapClientTemplate().delete(
                        getSqlId(SQL_DELETE_PROJECT_AUTH), project);
        }

        if (result == 0) {
            throw new StaleRecordException();
        }
    }

    @Override
    public int count() {
        return Integer.parseInt(getSqlMapClientTemplate()
                .queryForObject(getSqlId(SQL_FIND_POROJECT_COUNT)).toString());
    }
}
