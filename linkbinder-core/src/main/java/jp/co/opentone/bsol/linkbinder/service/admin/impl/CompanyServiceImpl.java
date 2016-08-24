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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.co.opentone.bsol.framework.core.config.SystemConfig;
import jp.co.opentone.bsol.framework.core.dao.KeyDuplicateException;
import jp.co.opentone.bsol.framework.core.dao.RecordNotFoundException;
import jp.co.opentone.bsol.framework.core.dao.StaleRecordException;
import jp.co.opentone.bsol.framework.core.generator.GeneratorFailedException;
import jp.co.opentone.bsol.framework.core.generator.excel.WorkbookGenerator;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.core.util.ArgumentValidator;
import jp.co.opentone.bsol.linkbinder.dao.CompanyDao;
import jp.co.opentone.bsol.linkbinder.dao.CompanyUserDao;
import jp.co.opentone.bsol.linkbinder.dao.ProjectCompanyDao;
import jp.co.opentone.bsol.linkbinder.dao.UserDao;
import jp.co.opentone.bsol.linkbinder.dto.Company;
import jp.co.opentone.bsol.linkbinder.dto.CompanyUser;
import jp.co.opentone.bsol.linkbinder.dto.ProjectCompany;
import jp.co.opentone.bsol.linkbinder.dto.ProjectUser;
import jp.co.opentone.bsol.linkbinder.dto.SearchCompanyResult;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCompanyCondition;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchUserCondition;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.service.AbstractService;
import jp.co.opentone.bsol.linkbinder.service.admin.CompanyService;

/**
 * このサービスでは会社情報に関する処理を提供する.
 * @author opentone
 */
@Service
@Transactional(rollbackFor = { ServiceAbortException.class })
public class CompanyServiceImpl extends AbstractService implements CompanyService {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 2347882896607861272L;

    /**
     * 会社情報件数.
     */
    private int dataCount;

    /**
     * プロパティに設定されたEXCELシート名のKEY.
     */
    private static final String SHEET_KEY = "excel.sheetname.companyindex";

    /**
     * EXCELシート名がプロパティに設定されていない場合のデフォルト名.
     */
    private static final String SHEET_DEFAULT = "CompanyIndex";

    /**
     * Excel出力する際のヘッダ名(マスタ管理).
     */
    private static final List<String> MASTER_HEADER;
    static {
        MASTER_HEADER = new ArrayList<String>();
        MASTER_HEADER.add("ID");
        MASTER_HEADER.add("会社コード");
        MASTER_HEADER.add("会社名");
    }

    /**
     * Excel出力する際の出力項目（マスタ管理）.
     */
    private static final List<String> MASTER_FIELDS;
    static {
        MASTER_FIELDS = new ArrayList<String>();
        MASTER_FIELDS.add("id");
        MASTER_FIELDS.add("companyCd");
        MASTER_FIELDS.add("name");
    }

    /**
     * Excel出力する際のヘッダ名（プロジェクトマスタ管理）.
     */
    private static final List<String> PROJECT_HEADER;
    static {
        PROJECT_HEADER = new ArrayList<String>();
        PROJECT_HEADER.add("ID");
        PROJECT_HEADER.add("会社コード");
        PROJECT_HEADER.add("会社名");
        PROJECT_HEADER.add("役割");
    }

    /**
     * Excel出力する際の出力項目（マスタ管理）.
     */
    private static final List<String> PROJECT_FIELDS;
    static {
        PROJECT_FIELDS = new ArrayList<String>();
        PROJECT_FIELDS.add("projectCompanyId");
        PROJECT_FIELDS.add("companyCd");
        PROJECT_FIELDS.add("name");
        PROJECT_FIELDS.add("role");
    }

    /*
     * (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.service.admin.CompanyService#search(jp.co.opentone.bsol.linkbinder
     * .dto.SearchCompanyCondition)
     */
    @Transactional(readOnly = true)
    public SearchCompanyResult search(SearchCompanyCondition condition)
            throws ServiceAbortException {
        ArgumentValidator.validateNotNull(condition);
        // 会社情報取得
        List<Company> companyList = searchCompany(condition);
        SearchCompanyResult result = createResult(companyList);
        return result;
    }

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.service.admin.CompanyService#searchProjectCompany(java.lang.String)
     */
    public List<Company> searchRelatedToProject(String projectId) {
        ArgumentValidator.validateNotEmpty(projectId);

        SearchCompanyCondition condition = new SearchCompanyCondition();
        condition.setProjectId(projectId);

        return findCompany(condition);
    }

    /*
     * (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.service.admin.CompanyService#searchNotProjectCompany()
     */
    @Transactional(readOnly = true)
    public List<Company> searchNotAssigned() {
        return findNotAssignTo(getCurrentProjectId());
    }

    /*
     * (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.service.admin.CompanyService#assignTo(jp.co.opentone.bsol.linkbinder
     * .dto.Company)
     */
    public Long assignTo(Company company) throws ServiceAbortException {
        ArgumentValidator.validateNotNull(company);
        // 会社情報を登録するチェックを行う
        validateAssignToProject(company);
        // 会社情報をプロジェクトに登録する
        return insertProjectCompany(company);
    }

    /*
     * (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.service.admin.CompanyService#delete(jp.co.opentone.bsol.linkbinder
     * .dto.Company)
     */
    public void delete(Company company) throws ServiceAbortException {
        if (StringUtils.isEmpty(getCurrentProjectId())) {
            validateDeleteMaster(company);
            Company deleteCompany = createDeleteCompany(company);
            deleteCompany(deleteCompany);
        } else {
            validateDeleteProject(company);
            ProjectCompany deleteProjectCompany = createDeleteProjectCompany(company);
            deleteProjectCompany(deleteProjectCompany);
            Company exclusiveCompany = createExclusiveCheckCompany(company);
            // 排他制御
            updateCompany(exclusiveCompany);
        }
    }

    /*
     * (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.service.admin.CompanyService#find(java.lang.Long)
     */
    @Transactional(readOnly = true)
    public Company find(Long id) throws ServiceAbortException {
        ArgumentValidator.validateNotNull(id);
        Company company = null;
        if (StringUtils.isEmpty(getCurrentProjectId())) {
            company = findById(id);
        } else {
            // 会社のプロジェクトが現在選択中のプロジェクト以外はエラー
            company = findProjectCompanyById(id);
            validateProjectId(company.getProjectId());
        }
        return company;

    }

    /*
     * (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.service.admin.CompanyService#findMembers(java.lang.Long)
     */
    @Transactional(readOnly = true)
    public List<CompanyUser> findMembers(Long id) throws ServiceAbortException {
        ArgumentValidator.validateNotNull(id);
        validatePermission();
        return findCompanyMembers(id);
    }

    /*
     * (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.service.admin.CompanyService#generateExcel(java.util
     * .List)
     */
    @Transactional(readOnly = true)
    public byte[] generateExcel(List<Company> companies) throws ServiceAbortException {
        try {
            String sheetName = SystemConfig.getValue(SHEET_KEY);
            if (StringUtils.isEmpty(sheetName)) {
                sheetName = SHEET_DEFAULT;
            }
            WorkbookGenerator generator =
                    new WorkbookGenerator(
                        sheetName, companies, getOutputField(companies), getHeaderNames(companies),
                        true);
            return generator.generate();
        } catch (GeneratorFailedException e) {
            throw new ServiceAbortException(e.getMessage());
        }

    }

    /*
     * (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.service.admin.CompanyService#save(jp.co.opentone.bsol.linkbinder.dto
     * .Company)
     */
    public Long save(Company company) throws ServiceAbortException {
        ArgumentValidator.validateNotNull(company);
        // Validateチェック
        validate(company);
        // マスタ管理
        if (StringUtils.isEmpty(getCurrentProjectId())) {
            if (!company.isNew()) {
                // 更新
                return updateCompany(createUpdateCompany(company));
            }
            // 会社情報を登録する
            return insertCompany(createInsertCompany(company));
        } else {
            // プロジェクトマスタ管理
            Long id = updateProjectCompany(company);
            // 排他制御
            updateCompany(createExclusiveCheckCompany(company));

            return id;
        }
    }

    /*
     * (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.service.admin.CompanyService#saveMembers(jp.co.opentone.bsol.linkbinder
     * .dto.Company, java.util.List)
     */
    public void saveMembers(Company company, List<User> users) throws ServiceAbortException {
        ArgumentValidator.validateNotNull(company);
        ArgumentValidator.validateNotNull(users);
        validatePermission();

        // ユーザーマスタに存在しない際はエラー
        validateUserExists(users);
        // 選択されたプロジェクトユーザーのユーザーが同一プロジェクトに属していない際はエラー
        validateProjectUserExists(company.getId(), users);
        // 指定した会社ユーザーの情報を全て削除する
        deleteAllCompanyUsers(company.getProjectCompanyId());
        // 指定した会社ユーザーを登録する
        insertCompanyUser(company, users);
        // 排他制御
        Company exclusiveCompany = createExclusiveCheckCompany(company);
        updateCompany(exclusiveCompany);
    }

    /*
     * (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.service.admin.CompanyService#validate(jp.co.opentone.bsol.linkbinder
     * .dto.Company)
     */
    @Transactional(readOnly = true)
    public boolean validate(Company company) throws ServiceAbortException {
        // マスタ管理
        if (StringUtils.isEmpty(getCurrentProjectId())) {
            validateMaster(company);
        } else {
            validateProjectMaster(company);
        }
        return true;
    }

    /**
     * 検索条件にあった会社情報を取得する.
     * @param condition 検索情報
     * @return 会社リスト
     */
    private List<Company> findCompany(SearchCompanyCondition condition) {
        CompanyDao dao = getDao(CompanyDao.class);
        return dao.find(condition);
    }

    /**
     * 会社情報検索処理.
     * @param condition 検索条件
     * @throws ServiceAbortException
     */
    private List<Company> searchCompany(SearchCompanyCondition condition)
            throws ServiceAbortException {
        // SystemAdminHome
        if (StringUtils.isEmpty(getCurrentProjectId())) {
            validateNotSystemAdmin();
        } else {
            if (!isSystemAdmin(getCurrentUser())) {
                // ログインユーザーが、現在選択しているプロジェクトに属していない場合はエラー
                validateProjectUserDiff();
            }
        }

        // 該当データ0件の場合はエラー
        validateExistCompany(condition);
        List<Company> company = findCompany(condition);
        // 指定のページを選択した際、そのページに表示するデータが存在しない場合はエラー
        validatePageSpecificationExistCompany(company);
        return company;
    }

    /**
     * ログインユーザーが現在選択しているプロジェクトに属していない場合はエラー.
     * @throws ServiceAbortException プロジェクトに属していない
     */
    private void validateProjectUserDiff() throws ServiceAbortException {
        if (findProjectUser(getCurrentProjectId(), getCurrentUser().getEmpNo()) == null) {
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_PROJECT_DIFF);
        }
    }

    /**
     * 権限共通チェック[2] SystemAdmin,ProjectAdminかを判定する.
     * @return boolean SystemAdmin又はProjectAdminの場合はtrue /
     *         SystemAdmin又はProjectAdmin以外はfalse
     */
    private boolean isValidPermission(String projectId) {
        return isSystemAdmin(getCurrentUser()) || isProjectAdmin(getCurrentUser(), projectId);
    }

    /**
     * 権限がない場合はエラー.
     * @throws ServiceAbortException 権限がない
     */
    private void validatePermission() throws ServiceAbortException {
        if (!isValidPermission(getCurrentProjectId())) {
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT);
        }
    }

    /**
     * 会社件数を取得する.
     * @param condition 検索条件
     * @return int 会社件数
     */
    private int countCompany(SearchCompanyCondition condition) {
        CompanyDao dao = getDao(CompanyDao.class);
        return dao.countCompany(condition);
    }

    /**
     * 会社情報が存在するか判定する.
     * @param condition 検索条件
     * @return 会社情報が0件の場合はfalse / 会社情報が0件ではない場合はtrue
     */
    private boolean isExistCompany(SearchCompanyCondition condition) {
        dataCount = countCompany(condition);
        return dataCount > 0;
    }

    /**
     * 会社情報が存在しない場合はエラー.
     * @param condition 検索条件
     * @throws ServiceAbortException 会社情報が存在しない
     */
    private void validateExistCompany(SearchCompanyCondition condition)
            throws ServiceAbortException {
        if (!isExistCompany(condition)) {
            throw new ServiceAbortException(ApplicationMessageCode.NO_DATA_FOUND);
        }

    }

    /**
     * ページを指定した際に表示するデータが存在するか判定する.
     * @param company 会社情報
     * @return 会社情報が存在しない場合はfalse / 存在する場合はtrue
     */
    private boolean isPageSpecificationExistCompany(List<Company> company) {
        return company.size() > 0;
    }

    /**
     * ページを指定した際に表示するデータが存在しない場合はエラー.
     * @param companies 会社情報リスト
     * @throws ServiceAbortException データが存在しない
     */
    private void validatePageSpecificationExistCompany(List<Company> companies)
            throws ServiceAbortException {
        // 指定のページを選択した際、そのページに表示するデータが存在しない場合はエラー
        if (!isPageSpecificationExistCompany(companies)) {
            throw new ServiceAbortException(ApplicationMessageCode.NO_PAGE_FOUND);
        }
    }

    /**
     * 会社情報一覧画面に渡すオブジェクトを生成する.
     * @param companyList 一覧データ
     * @return 一覧表示用オブジェクト
     */
    private SearchCompanyResult createResult(List<Company> companyList) {
        SearchCompanyResult result = new SearchCompanyResult();
        result.setCompanyList(companyList);
        result.setCount(dataCount);
        return result;
    }

    /**
     * 会社情報一覧画面の出力パラメータを取得する.
     * @return 出力パラメータ
     */
    private List<String> getOutputField(List<Company> companies) {
        // プロジェクトマスタ管理
        if (StringUtils.isNotEmpty(getCurrentProjectId())) {
            return PROJECT_FIELDS;
        }
        return MASTER_FIELDS;
    }

    /**
     * 会社情報一覧画面のヘッダ名を取得する.
     * @return ヘッダ名
     */
    private List<String> getHeaderNames(List<Company> companies) {
        if (StringUtils.isNotEmpty(getCurrentProjectId())) {
            return PROJECT_HEADER;
        }
        return MASTER_HEADER;
    }

    /**
     * Projectに追加する会社情報を取得する.
     * @param ProjectId プロジェクトID
     * @return 会社情報一覧
     */
    private List<Company> findNotAssignTo(String projectId) {
        if (StringUtils.isNotEmpty(projectId)) {
            CompanyDao dao = getDao(CompanyDao.class);
            return dao.findNotAssignTo(projectId);
        }
        return new ArrayList<Company>(); // NULLにはしない
    }

    /**
     * 会社情報をプロジェクトに登録する.
     * @param company 会社情報
     * @return 会社情報ID
     * @throws ServiceAbortException
     */
    private Long insertProjectCompany(Company company) throws ServiceAbortException {
        ProjectCompany projectCompany = new ProjectCompany();
        projectCompany.setProjectId(getCurrentProjectId());
        projectCompany.setCompanyId(company.getId());
        User loginUser = getCurrentUser();
        projectCompany.setCreatedBy(loginUser);
        projectCompany.setUpdatedBy(loginUser);
        try {
            ProjectCompanyDao dao = getDao(ProjectCompanyDao.class);
            return dao.create(projectCompany);
        } catch (KeyDuplicateException kde) {
            throw new ServiceAbortException(kde);
        }
    }

    /**
     * 会社情報をプロジェクトに登録するチェックを行う.
     * @param company 会社情報
     * @throws ServiceAbortException
     */
    private void validateAssignToProject(Company company) throws ServiceAbortException {
        // 権限共通チェック【2】参照
        validatePermission();
        // 指定の会社情報が存在しない場合はエラー
        findById(company.getId());
        // 指定のプロジェクトに同じ会社情報が既に紐付いている際はエラー
        validateAlreadyCompanyProject(company);
    }

    /**
     * 会社情報を登録する.
     * @param company 会社情報
     * @return 登録した会社ID
     * @throws ServiceAbortException
     */
    private Long insertCompany(Company company) throws ServiceAbortException {
        CompanyDao dao = getDao(CompanyDao.class);
        try {
            return dao.create(company);
        } catch (KeyDuplicateException kde) {
            throw new ServiceAbortException(kde);
        }
    }

    /**
     * 登録用の会社情報オブジェクトを作成する. 登録する会社情報に必要な情報をセットする.
     * @param company 会社情報
     * @return 会社情報
     */
    private Company createInsertCompany(Company company) {
        User loginUser = getCurrentUser();
        Company insertCompany = new Company();
        insertCompany.setCompanyCd(company.getCompanyCd());
        insertCompany.setName(company.getName());
        insertCompany.setCreatedBy(loginUser);
        insertCompany.setUpdatedBy(loginUser);
        return insertCompany;
    }

    /**
     * マスタ管理のValidateチェックを行う.
     * @param company 会社情報
     * @throws ServiceAbortException チェックに引っかかった場合
     */
    private void validateMaster(Company company) throws ServiceAbortException {
        // マスタ管理新規登録の場合
        if (company.isNew()) {
            // SystemADmin以外はエラー
            validateNotSystemAdmin();
        } else {
            // 更新処理の場合
            // 該当データが0件の場合はエラー
            findById(company.getId());
        }
        // 有効な会社情報の同じ会社コード（company_cd）が既に登録されている際はエラー
        validateExistCompanyCode(company);
    }

    /**
     * プロジェクトマスタ管理のValidateチェックを行う.
     * @param company 会社情報
     * @throws ServiceAbortException チェックに引っかかった場合
     */
    private void validateProjectMaster(Company company) throws ServiceAbortException {
        // SystemAdmin,ProjectAdmin以外はエラー
        validatePermission();
        // 該当データが0件の場合はエラー
        findProjectCompanyById(company.getId());
        // 会社のプロジェクトが現在選択中のプロジェクトか判定
        validateProjectId(company.getProjectId());
    }

    /**
     * 会社情報を更新する.(company).
     * @param company 会社情報
     * @return 会社情報ID
     * @throws ServiceAbortException
     */
    private Long updateCompany(Company company) throws ServiceAbortException {
        CompanyDao dao = getDao(CompanyDao.class);
        try {
            dao.update(company);
        } catch (KeyDuplicateException kdle) {
            throw new ServiceAbortException(kdle);
        } catch (StaleRecordException sre) {
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_COMPANY_ALREADY_UPDATED);
        }
        return company.getId();
    }

    /**
     * 会社情報を更新する.(project_company).
     * @param projectCompany プロジェクト会社情報
     * @return 更新した会社ID
     * @throws ServiceAbortException 更新失敗
     */
    private Long updateProjectCompany(Company company) throws ServiceAbortException {
        ProjectCompanyDao dao = getDao(ProjectCompanyDao.class);
        try {
            ProjectCompany updateProjectCompany = createUpdateProjectCompany(company);
            dao.update(updateProjectCompany);
        } catch (KeyDuplicateException kdle) {
            throw new ServiceAbortException(kdle);
        } catch (StaleRecordException sre) {
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_COMPANY_ALREADY_UPDATED);
        }
        return company.getId();
    }

    /**
     * 更新用の会社情報オブジェクトを作成する.
     * @param company 会社情報
     * @return 更新用会社情報
     */
    private Company createUpdateCompany(Company company) {
        Company updateCompany = new Company();
        updateCompany.setId(company.getId());
        updateCompany.setCompanyCd(company.getCompanyCd());
        updateCompany.setName(company.getName());
        updateCompany.setUpdatedBy(getCurrentUser());
        updateCompany.setVersionNo(company.getVersionNo());
        return updateCompany;
    }

    /**
     * 更新用の会社情報オブジェクトを作成する.
     * @param company 会社情報
     * @return 更新用会社情報(ProjectCompany)
     * @throws ServiceAbortException
     */
    private ProjectCompany createUpdateProjectCompany(Company company)
            throws ServiceAbortException {
        ProjectCompany updateProjectCompany = new ProjectCompany();
        updateProjectCompany.setId(company.getProjectCompanyId());
        updateProjectCompany.setRole(company.getRole());
        updateProjectCompany.setUpdatedBy(getCurrentUser());
        return updateProjectCompany;
    }

    /**
     * IDを指定して会社情報を取得する.
     * @param id ID
     * @return 取得した会社情報
     * @throws ServiceAbortException 会社情報が取得できない
     */
    private Company findById(Long id) throws ServiceAbortException {
        ArgumentValidator.validateNotNull(id);
        try {
            CompanyDao dao = getDao(CompanyDao.class);
            return dao.findById(id);
        } catch (RecordNotFoundException rnfe) {
            throw new ServiceAbortException(ApplicationMessageCode.NO_DATA_FOUND);
        }
    }

    /**
     * IDを指定して会社情報を取得する.
     * @param company 会社情報
     * @return 取得した会社情報
     * @throws ServiceAbortException 会社情報が取得できない
     */
    private Company findProjectCompanyById(Long id) throws ServiceAbortException {
        ArgumentValidator.validateNotNull(id);
        try {
            CompanyDao dao = getDao(CompanyDao.class);
            return dao.findProjectCompanyById(id, getCurrentProjectId());
        } catch (RecordNotFoundException rnfe) {
            throw new ServiceAbortException(ApplicationMessageCode.NO_DATA_FOUND);
        }
    }

    /**
     * 会社に所属しているユーザー情報を取得する.
     * @param id 会社情報ID
     * @return 会社に所属しているユーザー情報
     * @throws ServiceAbortException
     */
    private List<CompanyUser> findCompanyMembers(Long id) {
        CompanyDao dao = getDao(CompanyDao.class);
        List<CompanyUser> listCompanyUser = dao.findMembers(id);
        return listCompanyUser;
    }

    /**
     * 選択されたプロジェクトユーザーのユーザーが同一プロジェクトに属しているかチェックする.
     * @param id ID
     * @param users ユーザーリスト
     * @throws ServiceAbortException プロジェクトに属していない
     */
    private void validateProjectUserExists(Long id, List<User> users) throws ServiceAbortException {
        SearchUserCondition condition = new SearchUserCondition();
        condition.setProjectId(getCurrentProjectId());

        UserDao dao = getDao(UserDao.class);
        List<ProjectUser> projectUsers = dao.findProjectUser(condition);

        for (final User u : users) {
            //  プロジェクトに所属するユーザーリストに登録対象ユーザーが存在しなければエラー
            Object ret = CollectionUtils.find(projectUsers, new Predicate() {
                public boolean evaluate(Object object) {
                    ProjectUser pu = (ProjectUser) object;
                    return u.getEmpNo().equals(pu.getUser().getEmpNo());
                }
            });

            if (ret == null) {
                throw new ServiceAbortException(
                    "invalid user",
                    ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_USER_ALREADY_DELETED,
                    u.getLabel());
            }
        }
    }

    /**
     * 指定した会社ユーザーを全て削除する.
     * @param id ID
     * @return 削除した件数
     * @throws ServiceAbortException
     */
    private Integer deleteAllCompanyUsers(Long id) throws ServiceAbortException {
        try {
            CompanyUserDao dao = getDao(CompanyUserDao.class);
            CompanyUser companyUser = new CompanyUser();
            companyUser.setProjectCompanyId(id);
            companyUser.setUpdatedBy(getCurrentUser());
            return dao.deleteByCompanyId(companyUser);
        } catch (KeyDuplicateException kdle) {
            throw new ServiceAbortException(kdle);
        } catch (StaleRecordException sre) {
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_COMPANY_ALREADY_UPDATED);
        }
    }

    /**
     * 会社ユーザーを登録する.
     * @param companyUser 会社ユーザー
     * @throws ServiceAbortException
     */
    private void insertCompanyUser(Company company, List<User> listUser)
            throws ServiceAbortException {
        try {
            CompanyUserDao dao = getDao(CompanyUserDao.class);
            CompanyUser companyUser = null;
            for (User user : listUser) {
                companyUser = createCompanyUser(company, user);
                dao.create(companyUser);
            }
        } catch (KeyDuplicateException kde) {
            throw new ServiceAbortException(kde);
        }
    }

    /**
     * 登録する会社情報ユーザーオブジェクトを作成する.
     * @param company 会社情報
     * @param user ユーザー
     * @return 会社情報ユーザー
     */
    private CompanyUser createCompanyUser(Company company, User user) {
        CompanyUser updateCompanyUser = new CompanyUser();
        updateCompanyUser.setProjectCompanyId(company.getProjectCompanyId());
        updateCompanyUser.setProjectId(getCurrentProjectId());
        updateCompanyUser.setUser(user);
        updateCompanyUser.setCreatedBy(getCurrentUser());
        updateCompanyUser.setUpdatedBy(getCurrentUser());
        return updateCompanyUser;
    }

    /**
     * マスタ管理、会社情報削除処理のチェックを行う.
     * @param company 会社情報
     * @throws ServiceAbortException チェックに引っかかった場合
     */
    private void validateDeleteMaster(Company company) throws ServiceAbortException {
        // SystemAdmin以外はエラー
        validateNotSystemAdmin();
        // 削除対象の会社が、プロジェクトに関連付けられている場合は削除不可とする。
        validateRelationProject(company);
        // 該当データ0件の場合エラー ※会社マスタ情報
        findById(company.getId());
    }

    /**
     * プロジェクトマスタ管理、会社情報削除処理のチェックを行う.
     * @param company 会社情報
     * @throws ServiceAbortException 削除失敗
     */
    private void validateDeleteProject(Company company) throws ServiceAbortException {
        // 権限共通チェック[2]参照
        validatePermission();
        // 会社情報のプロジェクトが現在選択中のプロジェクト以外はエラー
        validateProjectId(company.getProjectId());
        // 削除対象の会社に、活動するユーザーが関連付けられている場合は削除不可とする。
        validateRelationUser(company);
        // 該当データ0件の場合エラー
        findProjectCompanyById(company.getId());
    }

    /**
     * マスタ管理、会社情報削除処理のチェックで使用する検索条件を作成する.
     * @param company 会社情報
     * @return 検索条件
     */
    private SearchCompanyCondition createCheckDeleteCondition(Company company) {
        SearchCompanyCondition condition = new SearchCompanyCondition();
        condition.setCompanyId(company.getId());
        return condition;
    }

    /**
     * 指定した会社情報を削除する.
     * @param id ID
     * @return 削除した会社のID
     * @throws ServiceAbortException 削除処理失敗
     */
    private Integer deleteCompany(Company company) throws ServiceAbortException {
        try {
            CompanyDao dao = getDao(CompanyDao.class);
            return dao.delete(company);
        } catch (KeyDuplicateException kde) {
            throw new ServiceAbortException(kde);
        } catch (StaleRecordException sre) {
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_COMPANY_ALREADY_UPDATED);
        }
    }

    /**
     * 指定したプロジェクト会社情報を削除する.
     * @param id ID
     * @return 削除した会社のID
     * @throws ServiceAbortException 削除処理失敗
     */
    private Integer deleteProjectCompany(ProjectCompany projectCompany)
            throws ServiceAbortException {
        try {
            ProjectCompanyDao dao = getDao(ProjectCompanyDao.class);
            return dao.delete(projectCompany);
        } catch (KeyDuplicateException kde) {
            throw new ServiceAbortException(kde);
        } catch (StaleRecordException sre) {
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_COMPANY_ALREADY_UPDATED);
        }
    }

    /**
     * 削除する会社情報を作成する.
     * @param company 会社情報
     * @return 会社情報
     */
    private Company createDeleteCompany(Company company) {
        Company deleteCompany = new Company();
        deleteCompany.setId(company.getId());
        deleteCompany.setUpdatedBy(getCurrentUser());
        deleteCompany.setVersionNo(company.getVersionNo());
        return deleteCompany;
    }

    /**
     * 削除するプロジェクト会社情報を作成する.
     * @param company 会社情報
     * @return プロジェクト会社情報
     */
    private ProjectCompany createDeleteProjectCompany(Company company) {
        ProjectCompany pc = new ProjectCompany();
        pc.setId(company.getProjectCompanyId());
        pc.setUpdatedBy(getCurrentUser());
        return pc;
    }

    /**
     * 排他チェック用の会社情報を作成する.
     * @param company 会社情報
     * @return 排他チェック用会社情報
     */
    private Company createExclusiveCheckCompany(Company company) {
        Company exclusiveCompany = new Company();
        exclusiveCompany.setId(company.getId());
        exclusiveCompany.setUpdatedBy(getCurrentUser());
        exclusiveCompany.setVersionNo(company.getVersionNo());
        return exclusiveCompany;
    }

    /**
     * 指定のプロジェクトに同じ会社情報が既に紐付いている場合はエラー.
     * @param company 会社情報
     * @throws ServiceAbortException 同じ会社が存在する
     */
    private void validateAlreadyCompanyProject(Company company) throws ServiceAbortException {
        Company c = null;
        // 指定のプロジェクトに同じ会社情報が既に紐付いている際はエラー
        try {
            c = findProjectCompanyById(company.getId());
        } catch (ServiceAbortException sae) {
            // 存在しないのが正の為処理はなし
            return;
        }
        if (c != null) {
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_COMPANY_ALREADY_ASSIGNED_TO_PROJECT);
        }
    }

    /**
     * SystemAdmin以外はエラー.
     * @throws ServiceAbortException SystemAdminではない
     */
    private void validateNotSystemAdmin() throws ServiceAbortException {
        // SystemAdmin以外はエラー
        if (!isSystemAdmin(getCurrentUser())) {
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT);
        }
    }

    /**
     * 会社コードが既に存在する場合はエラー.
     * @param company 会社情報
     * @throws ServiceAbortException 既に存在する
     */
    private void validateExistCompanyCode(Company company) throws ServiceAbortException {
        SearchCompanyCondition condition = new SearchCompanyCondition();
        condition.setCompanyCd(company.getCompanyCd());
        // 更新
        if (!company.isNew()) {
            condition.setId(company.getId());
        }
        // 有効な会社情報の同じ会社コード（company_cd）が既に登録されている際はエラー
        if (countCheck(condition) > 0) {
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_COMPANY_CODE_ALREADY_EXISTS,
                    (Object) company.getCompanyCd());
        }
    }

    /**
     * 条件を指定して会社情報件数を取得する（エラーチェック用）.
     * @param condition 検索条件
     * @return 会社情報件数
     */
    private int countCheck(SearchCompanyCondition condition) {
        CompanyDao dao = getDao(CompanyDao.class);
        return dao.countCheck(condition);
    }

    /**
     * 削除対象の会社が、プロジェクトに関連付けられている場合はエラー.
     * @param company 会社情報
     * @throws ServiceAbortException 削除対象の会社が、プロジェクトに関連付けられている
     */
    private void validateRelationProject(Company company) throws ServiceAbortException {
        // 削除対象の会社が、プロジェクトに関連付けられている場合は削除不可とする。
        if (countCheckProjectCompany(createCheckDeleteCondition(company)) > 0) {
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_COMPANY_ALREADY_ASSIGNED_TO_PROJECT);
        }
    }

    /**
     * 会社に活動するユーザーが関連付けられている場合はエラー.
     * @param company 会社情報
     * @throws ServiceAbortException 会社に活動するユーザーが関連付けられている
     */
    private void validateRelationUser(Company company) throws ServiceAbortException {
        // 削除対象の会社に、活動するユーザーが関連付けられている場合は削除不可とする。
        List<CompanyUser> listCu = findCompanyMembers(company.getProjectCompanyId());
        if (listCu.size() != 0) {
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_COMPANY_ALREADY_RELATED_WITH_USER);
        }
    }

    /**
     * 指定条件でプロジェクト会社件数を取得する.
     * @param condition 検索条件
     * @return プロジェクト会社件数
     */
    private int countCheckProjectCompany(SearchCompanyCondition condition) {
        ProjectCompanyDao dao = getDao(ProjectCompanyDao.class);
        return dao.countCheck(condition);
    }

    /**
     * 指定条件でプロジェクトユーザー件数を取得する.
     * @param condition 検索条件
     * @return プロジェクトユーザー件数
     */
    private int countCheckProjectUser(SearchUserCondition condition) {
        UserDao dao = getDao(UserDao.class);
        return dao.countCheck(condition);
    }

    /**
     * 指定したユーザーがプロジェクトユーザーマスタに存在しない際はエラー.
     * @param users ユーザーリスト
     * @throws ServiceAbortException プロジェクトユーザーマスタに存在しない
     */
    private void validateUserExists(List<User> users) throws ServiceAbortException {
        SearchUserCondition condition = new SearchUserCondition();
        for (User user : users) {
            condition.setEmpNo(user.getEmpNo());
            if (countCheckProjectUser(condition) == 0) {
                throw new ServiceAbortException(ApplicationMessageCode.NO_DATA_FOUND);
            }
        }
    }
}
