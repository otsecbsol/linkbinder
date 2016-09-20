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

import jp.co.opentone.bsol.framework.core.config.SystemConfig;
import jp.co.opentone.bsol.framework.core.dao.KeyDuplicateException;
import jp.co.opentone.bsol.framework.core.dao.RecordNotFoundException;
import jp.co.opentone.bsol.framework.core.dao.StaleRecordException;
import jp.co.opentone.bsol.framework.core.generator.GeneratorFailedException;
import jp.co.opentone.bsol.framework.core.generator.excel.WorkbookGenerator;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.core.util.ArgumentValidator;
import jp.co.opentone.bsol.linkbinder.dao.CorresponGroupDao;
import jp.co.opentone.bsol.linkbinder.dao.SiteDao;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroup;
import jp.co.opentone.bsol.linkbinder.dto.SearchSiteResult;
import jp.co.opentone.bsol.linkbinder.dto.Site;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponGroupCondition;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchSiteCondition;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.service.AbstractService;
import jp.co.opentone.bsol.linkbinder.service.admin.SiteService;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * このサービスでは拠点情報に関する処理を提供する.
 * @author opentone
 */
@Service
@Transactional(rollbackFor = { ServiceAbortException.class })
public class SiteServiceImpl extends AbstractService implements SiteService {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 7970336194861719548L;

    /**
     * プロパティに設定されたEXCELシート名のKEY.
     */
    private static final String SHEET_KEY = "excel.sheetname.siteindex";

    /**
     * EXCELシート名がプロパティに設定されていない場合のデフォルト名.
     */
    private static final String SHEET_DEFAULT = "SiteIndex";

    /**
     * 拠点情報件数.
     */
    private int dataCount;

    /**
     * Excel出力する際のヘッダ名.
     */
    private static final List<String> HEADER;
    static {
        HEADER = new ArrayList<String>();
        HEADER.add("ID");
        HEADER.add("拠点コード");
        HEADER.add("拠点名");
    }

    /**
     * Excel出力する際の出力項目.
     */
    private static final List<String> FIELDS;
    static {
        FIELDS = new ArrayList<String>();
        FIELDS.add("id");
        FIELDS.add("siteCd");
        FIELDS.add("name");
    }

    /*
     * (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.service.admin.SiteService#delete(jp.co.opentone.bsol.linkbinder
     * .dto.Site)
     */
    public void delete(Site site) throws ServiceAbortException {
        ArgumentValidator.validateNotNull(site);

        // 権限共通チェック【2】
        validateNotPermissionPatternTwo();

        // 該当データ0件の場合はエラー
        findById(site.getId());

        // 活動する部門が関連付けられている場合はエラー
        validateRelationDiscipline(site);

        // 拠点情報のプロジェクトが現在選択中のプロジェクト以外はエラー
        validateProjectId(site.getProjectId());

        // 削除
        deleteSite(createDeletedSite(site));

    }

    /*
     * (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.service.admin.SiteService#find(java.lang.Long)
     */
    @Transactional(readOnly = true)
    public Site find(Long id) throws ServiceAbortException {
        ArgumentValidator.validateNotNull(id);
        Site site = findById(id);
        // 拠点の情報が現在選択中のプロジェクト以外はエラー
        validateProjectId(site.getProjectId());
        return site;
    }

    /*
     * (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.service.admin.SiteService#generateExcel(java.util
     * .List)
     */
    @Transactional(readOnly = true)
    public byte[] generateExcel(List<Site> sites) throws ServiceAbortException {
        ArgumentValidator.validateNotNull(sites);
        try {
            String sheetName = SystemConfig.getValue(SHEET_KEY);
            if (StringUtils.isEmpty(sheetName)) {
                sheetName = SHEET_DEFAULT;
            }
            WorkbookGenerator generator =
                    new WorkbookGenerator(
                        sheetName, sites, getOutputField(), getHeaderNames(), true);
            return generator.generate();
        } catch (GeneratorFailedException e) {
            throw new ServiceAbortException(e.getMessage());
        }
    }

    /*
     * (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.service.admin.SiteService#save(jp.co.opentone.bsol.linkbinder
     * .dto.Site)
     */
    public Long save(Site site) throws ServiceAbortException {
        ArgumentValidator.validateNotNull(site);
        // エラーチェック
        validate(site);

        Long id = null;

        // 新規登録
        if (site.isNew()) {
            Site insertSite = createInsertSite(site);
            id = insertSite(insertSite);
        } else {
            // 拠点情報のプロジェクトが現在選択中のプロジェクト以外はエラー
            validateProjectId(site.getProjectId());

            Site updateSite = createUpdateSite(site);
            // 更新
            id = updateSite(updateSite);
        }
        return id;
    }

    /*
     * (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.service.admin.SiteService#search(jp.co.opentone.bsol.linkbinder
     * .dto.SearchSiteCondition)
     */
    @Transactional(readOnly = true)
    public SearchSiteResult search(SearchSiteCondition condition) throws ServiceAbortException {
        ArgumentValidator.validateNotNull(condition);

        // 権限共通チェック【3】
        validateNotPermission();

        // 該当データ0件の場合
        validateNotExistSite(condition);

        List<Site> sites = findSite(condition);

        // 指定のページを選択した際に、そのページに表示するデータが存在しない場合エラー
        validateNoPageData(sites);

        SearchSiteResult result = createResult(sites);
        return result;
    }

    /*
     * (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.service.admin.SiteService#validate(jp.co.opentone.bsol.linkbinder
     * .dto.Site)
     */
    @Transactional(readOnly = true)
    public void validate(Site site) throws ServiceAbortException {
        ArgumentValidator.validateNotNull(site);
        // 権限共通チェック【2】
        validateNotPermissionPatternTwo();

        // 更新処理の場合
        if (!site.isNew()) {
            // 該当データ0件の場合はエラー
            findById(site.getId());
        }

        // 同一プロジェクトにある有効な拠点情報の同じ拠点コード（site_cd）が既に登録されている際はエラー
        validateExistSiteCode(site);

    }

    /**
     * 拠点情報の出力パラメータを取得する.
     * @return 出力パラメータ
     */
    private List<String> getOutputField() {
        return FIELDS;
    }

    /**
     * 拠点一覧画面のヘッダ名を取得する.
     * @return ヘッダ名
     */
    private List<String> getHeaderNames() {
        return HEADER;
    }

    /**
     * 権限チェックを行う.
     * @return 権限がある場合はtrue / ない場合はfalse
     */
    private boolean isValidPermission() {
        User loginUser = getCurrentUser();
        String loginProjectId = getCurrentProjectId();
        return isSystemAdmin(loginUser) || isProjectAdmin(loginUser, loginProjectId)
            || isAnyGroupAdmin(loginProjectId);
    }

    /**
     * 権限がない(権限共通チェック【3】)場合はエラー.
     * @throws ServiceAbortException
     *             権限がない
     */
    private void validateNotPermission() throws ServiceAbortException {
        if (!isValidPermission()) {
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT);
        }
    }

    /**
     * 権限チェックを行う. SystemAdmin,ProjectAdminか判定する.
     * @return 権限がある場合true / ない場合false
     */
    private boolean isValidPermissionPatternTwo() {
        User loginUer = getCurrentUser();
        return isSystemAdmin(loginUer) || isProjectAdmin(loginUer, getCurrentProjectId());
    }

    /**
     * 権限がない（権限共通チェック【2】）場合はエラー.
     * @throws ServiceAbortException
     *             権限がない
     */
    private void validateNotPermissionPatternTwo() throws ServiceAbortException {
        if (!isValidPermissionPatternTwo()) {
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT);
        }
    }

    /**
     * 指定した検索条件で、拠点情報を取得する.
     * @param condition
     *            検索条件
     * @return 拠点情報
     */
    private List<Site> findSite(SearchSiteCondition condition) {
        SiteDao dao = getDao(SiteDao.class);
        return dao.find(condition);
    }

    /**
     * 拠点情報の件数を取得する.
     * @param condition
     * @return 拠点情報件数
     */
    private int countSite(SearchSiteCondition condition) {
        SiteDao dao = getDao(SiteDao.class);
        return dao.count(condition);
    }

    /**
     * 該当する拠点情報が存在するか判定する.
     * @param condition
     *            検索条件
     * @return 存在するtrue / 存在しないfalse
     * @throws ServiceAbortException
     *             取得しっぱい
     */
    private boolean isExistSite(SearchSiteCondition condition) throws ServiceAbortException {
        dataCount = countSite(condition);
        return dataCount > 0;
    }

    /**
     * 拠点情報が存在しない場合はエラー.
     * @param condition
     *            検索条件
     * @throws ServiceAbortException
     *             拠点情報が存在しない
     */
    private void validateNotExistSite(SearchSiteCondition condition) throws ServiceAbortException {
        if (!isExistSite(condition)) {
            throw new ServiceAbortException(ApplicationMessageCode.NO_DATA_FOUND);
        }
    }

    /**
     * 指定のページを選択した際にそのページに表示するデータが存在するか判定する.
     * @param sites
     *            拠点情報
     * @return 存在するtrue / 存在しないfalse
     */
    private boolean isPageSearchNotExist(List<Site> sites) {
        return sites.isEmpty();
    }

    /**
     * 指定のページに表示するデータが存在しない場合はエラー.
     * @param sites
     *            拠点
     * @throws ServiceAbortException
     *             データが存在しない
     */
    private void validateNoPageData(List<Site> sites) throws ServiceAbortException {
        if (isPageSearchNotExist(sites)) {
            throw new ServiceAbortException(ApplicationMessageCode.NO_PAGE_FOUND);
        }
    }

    /**
     * SearchSiteResultオブジェクトを作成する.
     * @param sites
     *            拠点情報
     * @return SearchSiteResultオブジェクト
     */
    private SearchSiteResult createResult(List<Site> sites) {
        SearchSiteResult result = new SearchSiteResult();
        result.setSiteList(sites);
        result.setCount(dataCount);

        return result;
    }

    /**
     * IDを指定して拠点情報を取得する.
     * @param id
     *            ID
     * @return 拠点情報
     */
    private Site findById(Long id) throws ServiceAbortException {
        SiteDao dao = getDao(SiteDao.class);
        try {
            return dao.findById(id);
        } catch (RecordNotFoundException e) {
            throw new ServiceAbortException(ApplicationMessageCode.NO_DATA_FOUND);
        }
    }

    /**
     * 同一プロジェクトにある有効な拠点の同じ拠点コードが既に登録されているかチェックする.
     * @param site
     *            拠点情報
     * @return 異常なしtrue / 異常ありfalse
     * @throws ServiceAbortException
     *             判定処理中に予期せぬエラー
     */
    private boolean isExistSiteCode(Site site) throws ServiceAbortException {
        SearchSiteCondition condition = new SearchSiteCondition();
        condition.setProjectId(getCurrentProjectId());
        condition.setSiteCd(site.getSiteCd());

        // 更新の場合
        if (!site.isNew()) {
            condition.setId(site.getId());
        }
        return countCheck(condition) == 0;

    }

    /**
     * 条件を指定して拠点情報件数を取得する（エラーチェック用）.
     * @param condition 検索条件
     * @return 拠点情報件数
     */
    private int countCheck(SearchSiteCondition condition) {
        SiteDao dao = getDao(SiteDao.class);
        return dao.countCheck(condition);
    }

    /**
     * 拠点コードが既に存在する場合はエラー.
     * @param site
     *            拠点
     * @throws ServiceAbortException
     *             拠点コードが既に存在している
     */
    private void validateExistSiteCode(Site site) throws ServiceAbortException {
        if (!isExistSiteCode(site)) {
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_SITE_CODE_ALREADY_EXISTS,
                    (Object) site.getSiteCd());
        }
    }

    /**
     * 登録用の拠点オブジェクトを作成する.
     * @param site
     *            拠点情報
     * @return 登録用拠点情報
     */
    private Site createInsertSite(Site site) {
        Site insertSite = new Site();
        insertSite.setProjectId(getCurrentProjectId());
        insertSite.setSiteCd(site.getSiteCd());
        insertSite.setName(site.getName());
        insertSite.setCreatedBy(getCurrentUser());
        insertSite.setUpdatedBy(getCurrentUser());
        return insertSite;
    }

    /**
     * 拠点情報を登録する.
     * @param site
     *            拠点
     * @return 新規に登録した拠点のID
     * @throws ServiceAbortException
     *             登録失敗
     */
    private Long insertSite(Site site) throws ServiceAbortException {
        SiteDao dao = getDao(SiteDao.class);
        try {
            return dao.create(site);
        } catch (KeyDuplicateException kde) {
            throw new ServiceAbortException(kde);
        }
    }

    /**
     * 更新用の拠点オブジェクトを作成する.
     * @param site
     *            拠点
     * @return 更新用拠点情報
     */
    private Site createUpdateSite(Site site) {
        Site updateSite = new Site();
        updateSite.setId(site.getId());
        updateSite.setSiteCd(site.getSiteCd());
        updateSite.setName(site.getName());
        updateSite.setUpdatedBy(getCurrentUser());
        updateSite.setVersionNo(site.getVersionNo());
        return updateSite;
    }

    /**
     * 拠点を更新する.
     * @return 拠点情報ID
     * @throws ServiceAbortException
     *             更新失敗
     */
    private Long updateSite(Site site) throws ServiceAbortException {
        SiteDao dao = getDao(SiteDao.class);
        try {
            dao.update(site);
        } catch (KeyDuplicateException kde) {
            throw new ServiceAbortException(kde);
        } catch (StaleRecordException sre) {
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_SITE_ALREADY_UPDATED);
        }
        // 更新した拠点のIDを返す
        return site.getId();

    }

    /**
     * 活動する部門が関連付けられているか判定する.
     * @param site
     *            拠点情報
     * @return 活動する部門が関連付けられているtrue / 関連付けられてないfalse
     */
    private boolean isRelationDiscipline(Site site) {
        List<CorresponGroup> corresponGroupList = findCorresponGroupBySiteId(site);
        return !corresponGroupList.isEmpty();
    }

    /**
     * 活動する部門が関連付けられている場合はエラー.
     * @param site
     *            拠点
     * @throws ServiceAbortException
     *             関連付けられている
     */
    private void validateRelationDiscipline(Site site) throws ServiceAbortException {
        if (isRelationDiscipline(site)) {
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_SITE_ALREADY_RELATED_WITH_DISCIPLINE);
        }
    }

    /**
     * 拠点IDを指定して活動単位を取得する.
     * @param site
     *            拠点情報
     * @return 活動単位
     */
    private List<CorresponGroup> findCorresponGroupBySiteId(Site site) {
        SearchCorresponGroupCondition condition = new SearchCorresponGroupCondition();
        condition.setProjectId(getCurrentProjectId());
        condition.setSiteId(site.getId());

        CorresponGroupDao dao = getDao(CorresponGroupDao.class);
        return dao.find(condition);
    }

    /**
     * 拠点情報を削除する.
     * @return 該当件数
     * @throws ServiceAbortException
     *             削除失敗
     */
    private Integer deleteSite(Site site) throws ServiceAbortException {
        SiteDao dao = getDao(SiteDao.class);
        try {
            return dao.delete(site);
        } catch (KeyDuplicateException kde) {
            throw new ServiceAbortException(kde);
        } catch (StaleRecordException sre) {
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_SITE_ALREADY_UPDATED);
        }
    }


    /**
     * 拠点削除用のオブジェクトを作成する.
     * @param old
     *            削除対象の拠点
     * @return 削除用のオブジェクト
     */
    private Site createDeletedSite(Site old) {
        Site site = new Site();
        site.setId(old.getId());
        site.setUpdatedBy(getCurrentUser());
        site.setVersionNo(old.getVersionNo());
        return site;
    }
}
