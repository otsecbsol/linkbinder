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
package jp.co.opentone.bsol.linkbinder.service.admin.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.co.opentone.bsol.framework.core.config.SystemConfig;
import jp.co.opentone.bsol.framework.core.dao.KeyDuplicateException;
import jp.co.opentone.bsol.framework.core.dao.RecordNotFoundException;
import jp.co.opentone.bsol.framework.core.dao.StaleRecordException;
import jp.co.opentone.bsol.framework.core.generator.GeneratorFailedException;
import jp.co.opentone.bsol.framework.core.generator.csv.CsvGenerator;
import jp.co.opentone.bsol.framework.core.generator.excel.WorkbookGenerator;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.core.util.ArgumentValidator;
import jp.co.opentone.bsol.linkbinder.dao.ProjectDao;
import jp.co.opentone.bsol.linkbinder.dto.Project;
import jp.co.opentone.bsol.linkbinder.dto.SearchProjectResult;
import jp.co.opentone.bsol.linkbinder.dto.code.MasterDataImportResultStatus;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchProjectCondition;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.service.AbstractService;
import jp.co.opentone.bsol.linkbinder.service.admin.ProjectService;


/**
 * このサービスではプロジェクト情報に関する処理を提供する.
 * @author opentone
 */
@Service
@Transactional(rollbackFor = { ServiceAbortException.class })
public class ProjectServiceImpl extends AbstractService implements ProjectService {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -6130842521015648019L;

    /**
     * プロパティに設定されたEXCELシート名のKEY.
     */
    private static final String SHEET_KEY = "excel.sheetname.projectindex";

    /**
     * EXCELシート名がプロパティに設定されていない場合のデフォルト名.
     */
    private static final String SHEET_DEFAULT = "ProjectIndex";

    /**
     * CSV出力の際のエンコード
     */
    private static final String ENCODING = "csv.encoding";

    /**
     * Excel出力する際のヘッダ名.
     */
    private static final List<String> HEADER;
    static {
        HEADER = new ArrayList<String>();
        HEADER.add("Project ID");
        HEADER.add("Project Name");
    }

    /**
     * Excel出力する際の出力項目.
     */
    private static final List<String> FIELDS;
    static {
        FIELDS = new ArrayList<String>();
        FIELDS.add("projectId");
        FIELDS.add("nameE");
    }

   /**
    * ProjectDTOとのマッピング.
    */
   private static final List<String> MAP_DTO_PJ;
   static {
       MAP_DTO_PJ = new ArrayList<String>();
       MAP_DTO_PJ.add("projectId");
       MAP_DTO_PJ.add("clientNameE");
       MAP_DTO_PJ.add("clientNameJ");
       MAP_DTO_PJ.add("nameE");
       MAP_DTO_PJ.add("nameJ");
       MAP_DTO_PJ.add("useApprovedFlg");
   }
    /**
     * プロジェクト情報をCSV出力する際のヘッダ名.
     */
    private static final List<String> CSV_HEADER_PJ;
    static {
        CSV_HEADER_PJ = new ArrayList<String>();
        CSV_HEADER_PJ.add("プロジェクトID");
        CSV_HEADER_PJ.add("クライアント英語名称");
        CSV_HEADER_PJ.add("クライアント日本語名称");
        CSV_HEADER_PJ.add("プロジェクト英語名称");
        CSV_HEADER_PJ.add("プロジェクト日本語名称");
        CSV_HEADER_PJ.add("利用可否");
    }

    /*
     * (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.service.admin.ProjectService
     * #search(jp.co.opentone.bsol.linkbinder.dto.condition.SearchProjectCondition)
     */
    @Override
    @Transactional(readOnly = true)
    public List<Project> search(SearchProjectCondition condition) throws ServiceAbortException {
        ArgumentValidator.validateNotNull(condition);

        return find(condition);
    }
    /*
     * (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.service.admin.ProjectService#searchPagingList(jp.co.opentone.bsol.linkbinder
     * .dto.SearchProjectCondition)
     */
    @Override
    @Transactional(readOnly = true)
    public SearchProjectResult searchPagingList(SearchProjectCondition condition)
            throws ServiceAbortException {
        ArgumentValidator.validateNotNull(condition);
        // 権限チェック
        checkPermission();
        // 並び順の設定
        condition.setOrderBy(ProjectDao.OrderBy.PROJECT_ID.getValue());
        // 該当データの存在チェック
        int count = getDataCount(condition);
        // 指定の条件に該当するコレポン文書種別一覧情報を取得
        List<Project> projectList = find(condition);
        // ページングデータのチェック
        checkPagingData(projectList);

        return createResult(projectList, count);
    }

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.service.admin.ProjectService#generateExcel(java.util.List)
     */
    @Override
    @Transactional(readOnly = true)
    public byte[] generateExcel(List<Project> projects) throws ServiceAbortException {
        ArgumentValidator.validateNotNull(projects);
        try {
            String sheetName = SystemConfig.getValue(SHEET_KEY);
            if (StringUtils.isEmpty(sheetName)) {
                sheetName = SHEET_DEFAULT;
            }
            WorkbookGenerator generator =
                    new WorkbookGenerator(sheetName, projects, FIELDS, HEADER, true);
            return generator.generate();
        } catch (GeneratorFailedException e) {
            throw new ServiceAbortException(e.getMessage());
        }
    }

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.service.admin.ProjectService#generateExcel(java.util.List)
     */
    @Override
    @Transactional(readOnly = true)
    public byte[] generateCSV(List<Project> projects) throws ServiceAbortException {
        ArgumentValidator.validateNotNull(projects);
        try {
            CsvGenerator generator =
                    new CsvGenerator(projects, MAP_DTO_PJ, CSV_HEADER_PJ, SystemConfig.getValue(ENCODING));
            return generator.generate();
        } catch (GeneratorFailedException e) {
            throw new ServiceAbortException(e.getMessage());
        }
    }

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.service.admin.ProjectService#find(java.lang.Long)
     */
    @Override
    public Project find(String id) throws ServiceAbortException {
        if (StringUtils.isEmpty(id)) {
            return null;
        }
        ProjectDao dao = getDao(ProjectDao.class);
        try {
            return dao.findById(id);
        } catch (RecordNotFoundException e) {
            return null;
        }
    }

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.service.admin.ProjectService#find(java.lang.Long)
     */
    public String findSysPj(String id) throws ServiceAbortException {
        if (StringUtils.isEmpty(id)) {
            return null;
        }
        ProjectDao dao = getDao(ProjectDao.class);
        try {
            return dao.findBySysPJId(id);
        } catch (RecordNotFoundException e) {
            return null;
        }
    }

    /**
     * 権限チェックを行う.
     * @throws ServiceAbortException 権限チェックエラー
     */
    private void checkPermission() throws ServiceAbortException {
        if (!isSystemAdmin(getCurrentUser())) {
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT);
        }
    }

    /**
     * 検索条件に該当するレコード数を取得する.
     * 0件の場合、エラー.
     * @param condition 検索条件
     * @return レコード数
     * @throws ServiceAbortException 該当なし
     */
    private int getDataCount(SearchProjectCondition condition) throws ServiceAbortException {
        ProjectDao dao = getDao(ProjectDao.class);
        int count = dao.count(condition);
        // 該当データ0件の場合
        if (count == 0) {
            throw new ServiceAbortException(ApplicationMessageCode.NO_DATA_FOUND);
        }
        return count;
    }

    /**
     * 検索条件に該当するプロジェクト情報を取得する.
     * @param condition 検索条件
     * @return プロジェクト情報
     */
    private List<Project> find(SearchProjectCondition condition) {
        ProjectDao dao = getDao(ProjectDao.class);
        return dao.find(condition);
    }

    /**
     * ページングで指定されたページにデータが存在するかチェックする.
     * @param projectList
     *            一覧データ
     * @throws ServiceAbortException
     *             指定されたページにデータが存在しない
     */
    private void checkPagingData(List<Project> projectList)
        throws ServiceAbortException {
        // 該当データ0件の場合
        if (projectList.size() == 0) {
            throw new ServiceAbortException(ApplicationMessageCode.NO_PAGE_FOUND);
        }
    }

    /**
     * 一覧情報を作成する.
     * @param projectList プロジェクト情報
     * @param count レコード数
     * @return 一覧情報
     */
    private SearchProjectResult createResult(List<Project> projectList, int count) {
        SearchProjectResult result = new SearchProjectResult();
        result.setCount(count);
        result.setProjectList(projectList);

        return result;
    }

    /**
     * 新規登録か更新か判定する.
     * @param pjId
     *            拠点情報
     * @return 登録ならtrue / 更新ならfalse
     * @throws ServiceAbortException
     */
    private boolean isNew(String pjId) throws ServiceAbortException {
        return  findSysPj(pjId) == null ;
    }


    @Override
    public List<Project> findAll(SearchProjectCondition condition) throws ServiceAbortException {
        ProjectDao dao = getDao(ProjectDao.class);
        return dao.findAll(condition);
    }

    /* (非 Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.service.admin.ProjectService#save(java.util.List)
     */
    @Override
    public List<Project> save(List<Project> projectList) throws ServiceAbortException {
        List<Project> result = new ArrayList<>();

        for (Project p : projectList) {
            saveProject(p);
            result.add(p);
        }

        return result;
    }
    /* (非 Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.service.admin.ProjectService#delete(java.util.List)
     */
    @Override
    public List<Project> delete(List<Project> projectList) throws ServiceAbortException {
        List<Project> result = new ArrayList<>();

        for (Project p : projectList) {
            deleteProject(p);
        }

        return result;
    }

    private void saveProject(Project project) throws ServiceAbortException {
        ProjectDao dao = getDao(ProjectDao.class);
        if (isNew(project.getProjectId())) {
            //新規登録
             try {
                dao.createProject(project);
                project.setImportResultStatus(MasterDataImportResultStatus.CREATED);
            } catch (KeyDuplicateException e) {
                throw new ServiceAbortException("プロジェクトが重複しています。",
                        ApplicationMessageCode.ERROR_PROJECT_DUPLICATED,
                        project.getProjectId());
            }
        } else {
            //更新
            try {
                dao.updateProject(project);
                project.setImportResultStatus(MasterDataImportResultStatus.UPDATED);
            } catch (RecordNotFoundException e) {
                throw new ServiceAbortException("プロジェクトが存在しません",
                        ApplicationMessageCode.ERROR_PROJECT_NOT_FOUND,
                        project.getProjectId());
            } catch (KeyDuplicateException | StaleRecordException  e) {
                throw new ServiceAbortException("プロジェクトの更新に失敗しました",
                        ApplicationMessageCode.ERROR_PROJECT_FAILED_TO_UPDATE,
                        project.getProjectId());
            }
        }
    }

    private void deleteProject(Project project) throws ServiceAbortException{
        if (!isNew(project.getProjectId())) {
            try {
                ProjectDao dao = getDao(ProjectDao.class);
                dao.deleteProject(project);
                project.setImportResultStatus(MasterDataImportResultStatus.DELETED);
            } catch (RecordNotFoundException e) {
                throw new ServiceAbortException("プロジェクトが存在しません",
                        ApplicationMessageCode.ERROR_PROJECT_NOT_FOUND,
                        project.getProjectId());
            } catch (KeyDuplicateException | StaleRecordException e) {
                throw new ServiceAbortException("プロジェクトの削除に失敗しました",
                        ApplicationMessageCode.ERROR_PROJECT_FAILED_TO_DELETE,
                        project.getProjectId());
            }
        } else {
            throw new ServiceAbortException("プロジェクトが存在しません",
                    ApplicationMessageCode.ERROR_PROJECT_NOT_FOUND,
                    project.getProjectId());
        }
    }
}
