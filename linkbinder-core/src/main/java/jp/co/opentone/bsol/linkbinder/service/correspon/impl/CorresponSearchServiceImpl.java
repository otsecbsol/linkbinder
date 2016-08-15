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

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.beanutils.PropertyUtils;
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
import jp.co.opentone.bsol.framework.core.generator.html.HTMLGenerator;
import jp.co.opentone.bsol.framework.core.message.MessageCode;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.core.util.ArgumentValidator;
import jp.co.opentone.bsol.framework.core.util.zip.ZipArchiver;
import jp.co.opentone.bsol.linkbinder.Constants;
import jp.co.opentone.bsol.linkbinder.dao.CorresponDao;
import jp.co.opentone.bsol.linkbinder.dao.WorkflowDao;
import jp.co.opentone.bsol.linkbinder.dto.Correspon;
import jp.co.opentone.bsol.linkbinder.dto.CorresponIndexHeader;
import jp.co.opentone.bsol.linkbinder.dto.CorresponResponseHistory;
import jp.co.opentone.bsol.linkbinder.dto.FullTextSearchCorresponsResult;
import jp.co.opentone.bsol.linkbinder.dto.SearchCorresponResult;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.Workflow;
import jp.co.opentone.bsol.linkbinder.dto.code.CorresponStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.FullTextSearchMode;
import jp.co.opentone.bsol.linkbinder.dto.code.ReadStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowProcessStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowStatus;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponCondition;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponCondition.Ids;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchFullTextSearchCorresponCondition;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.service.AbstractService;
import jp.co.opentone.bsol.linkbinder.service.correspon.CorresponFullTextSearchService;
import jp.co.opentone.bsol.linkbinder.service.correspon.CorresponReadStatusService;
import jp.co.opentone.bsol.linkbinder.service.correspon.CorresponSearchService;
import jp.co.opentone.bsol.linkbinder.service.correspon.CorresponService;
import jp.co.opentone.bsol.linkbinder.util.view.correspon.CorresponPageFormatter;
import jp.co.opentone.bsol.linkbinder.util.view.correspon.CorresponResponseHistoryModel;

/**
 * このサービスではコレポン文書一覧に関する処理を提供する.
 * @author opentone
 */
@Service
@Transactional(rollbackFor = { ServiceAbortException.class })
public class CorresponSearchServiceImpl extends AbstractService implements CorresponSearchService {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -2044852955447346197L;

    /**
     * プロパティに設定されたEXCELシート名のKEY.
     */
    private static final String SHEET_KEY = "excel.sheetname.corresponindex";

    /**
     * EXCELシート名がプロパティに設定されていない場合のデフォルト名.
     */
    private static final String SHEET_DEFAULT = "CorresponIndex";

    /**
     * HTMLテンプレート名（一覧表示）.
     */
    private static final String TEMPLATE_KEY_HTML = "template.correspon.index";

    /**
     * HTMLテンプレート名（ZIPダウンロード）.
     */
    private static final String TEMPLATE_KEY_ZIP = "template.correspon.zip";

    /**
     * スタイルシート名.
     */
    private static final String TEMPLATE_KEY_STYLESHEET = "template.stylesheet";
    /**
     * ファイル名フォーマット形式.
     */
    private static final String FILENAME_FORMAT = "%s_%s(%s).html";

    /**
     * ファイル名用IDフォーマット形式.
     */
    private static final String FILENAME_ID_FORMAT = "%010d";

    /**
     * ファイル名禁止文字をプロパティから取得するKEY.
     */
    private static final String FILENAME_KEY_REGEX = "file.name.regex";

    /**
     * ファイル名禁止文字がプロパティに設定されていない場合のデフォルト値.
     */
    private static final String FILENAME_DEFAULT_REGEX = "[\\\\/:*?\"<>|]";

    /**
     * ファイル名置換文字をプロパティから取得するするKEY.
     */
    private static final String FILENAME_KEY_REPLACEMENT = "file.name.replacement";

    /**
     * ファイル名禁止文字がプロパティに設定されていない場合のデフォルト値.
     */
    private static final String FILENAME_DEFAULT_REPLACEMENT = "-";

    /**
     * SELECT 文 IN 句の、最大要素数.
     */
    private static final int MAX_IN_ARGS = 1000;

    /**
     * Excel出力する際のヘッダ名.
     */
    private static final List<String> HEADER;
    static {
        HEADER = new ArrayList<String>();
        HEADER.add("No.");
        HEADER.add("Correspondence No.");
        HEADER.add("Previous Revision");
        HEADER.add("From");
        HEADER.add("To");
        HEADER.add("Type");
        HEADER.add("Subject");
        HEADER.add("Workfrow Status");
        HEADER.add("Created on");
        HEADER.add("Issued on");
        HEADER.add("Deadline for Reply");
        HEADER.add("Updated on");
        HEADER.add("Created by");
        HEADER.add("Issued by");
        HEADER.add("Updated by");
        HEADER.add("Reply Required");
        HEADER.add("Status");
    }

    /**
     * Excel出力する際の出力項目.
     */
    private static final List<String> FIELDS;
    static {
        FIELDS = new ArrayList<String>();
        FIELDS.add("id");
        FIELDS.add("corresponNo");
        FIELDS.add("previousRevCorresponNo");
        FIELDS.add("fromCorresponGroup.name");
        FIELDS.add("toGroupName");
        FIELDS.add("corresponType.corresponType");
        FIELDS.add("subject");
        FIELDS.add("workflowStatus.label");
        FIELDS.add("createdAt");
        FIELDS.add("issuedAt");
        FIELDS.add("deadlineForReply");
        FIELDS.add("updatedAt");
        FIELDS.add("createdBy.label");
        FIELDS.add("issuedBy.label");
        FIELDS.add("updatedBy.label");
        FIELDS.add("replyRequired.label");
        FIELDS.add("corresponStatus.label");
    }

    /**
     * Csv出力する際の日付フォーマット.
     */
    private static final Map<String, String> FORMATS;
    static {
        FORMATS = new HashMap<String, String>();
        FORMATS.put("createdAt", CsvGenerator.DEFAULT_DATE_FORMAT_PATTERN);
        FORMATS.put("issuedAt", CsvGenerator.DEFAULT_DATE_FORMAT_PATTERN);
        FORMATS.put("updatedAt", CsvGenerator.DEFAULT_DATE_FORMAT_PATTERN);
        FORMATS.put("deadlineForReply", CsvGenerator.DEFAULT_DATE_FORMAT_PATTERN);
    }

    /**
     * コレポン文書サービス.
     */
    @Resource
    private CorresponService corresponService;

    /**
     * コレポン文書既読・未読サービス.
     */
    @Resource
    private CorresponReadStatusService corresponReadStatusService;

    /**
     * 全文検索サービス.
     */
    @Resource
    private CorresponFullTextSearchService fullTextSearchService;

    /**
     * 空のインスタンスを生成する.
     */
    public CorresponSearchServiceImpl() {
    }

    /*
     * (non-Javadoc)
     * @seejp.co.opentone.bsol.linkbinder.service.correspon.CorresponSearchService#search(
     * CorresponSearchConditon)
     */
    @Transactional(readOnly = true)
    public SearchCorresponResult search(SearchCorresponCondition condition)
        throws ServiceAbortException {
        // 全文検索条件を含んでいるかどうか判定
        List<Correspon> corresponList = null;
        if (hasFullTextSearchCondition(condition)) {
            // 全文検索条件をセット
            setCorresponListFullTextSearchCondition(condition);
        } else if (null != condition.getIdList()) {
            condition.setIdList(null);
        }
        // 該当データの存在チェック
        int count = getDataCount(condition);
        // 指定された条件に該当するコレポン文書一覧情報を取得
        corresponList = findCorresponList(condition);
        // ページングデータのチェック
        checkPagingData(corresponList);

        return createResult(corresponList, count);
    }
    /**
     * 全文検索条件を含んでいるかどうかを判定する.
     * @param condition 検索条件
     * @return true: 全文検索条件を含んでいる、false: 含んでいない
     */
    private boolean hasFullTextSearchCondition(SearchCorresponCondition condition) {
        String key = condition.getKeyword();
        FullTextSearchMode mode = condition.getFullTextSearchMode();
        return (!StringUtils.isEmpty(key) && mode != null);
    }

    /*
     * (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.service.correspon.CorresponSearchService#generateCsv
     * (java.util.List)
     */
    public byte[] generateCsv(List<Correspon> correspons) throws ServiceAbortException {
        try {
            CsvGenerator generator =
                    new CsvGenerator(correspons, FIELDS, HEADER,
                                     SystemConfig.getValue(Constants.KEY_CSV_ENCODING),
                                     FORMATS);
            return generator.generate();
        } catch (GeneratorFailedException e) {
            throw new ServiceAbortException(e.getMessage(), e, MessageCode.E_GENERATION_FAILED,
                                            e.getMessage());
        }
    }

    /*
     * (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.service.correspon.CorresponSearchService#generateExcel
     * (java.util.List)
     */
    public byte[] generateExcel(List<Correspon> correspons) throws ServiceAbortException {
        try {
            String sheetName = SystemConfig.getValue(SHEET_KEY);
            if (sheetName == null) {
                sheetName = SHEET_DEFAULT;
            }
            WorkbookGenerator generator =
                    new WorkbookGenerator(sheetName, correspons, FIELDS,
                                          HEADER, true);
            return generator.generate();
        } catch (GeneratorFailedException e) {
            throw new ServiceAbortException(e.getMessage(), e, MessageCode.E_GENERATION_FAILED,
                                            e.getMessage());
        }
    }

    /*
     * (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.service.correspon.CorresponSearchService#generateHTML
     * (java.util.List, jp.co.opentone.bsol.linkbinder.dto.CorresponIndexHeader)
     */
    public byte[] generateHTML(List<Correspon> correspons, CorresponIndexHeader header)
        throws ServiceAbortException {
        try {
            HTMLGenerator generator =
                    new HTMLGenerator(SystemConfig.getValue(TEMPLATE_KEY_HTML), correspons);
            CorresponHTMLGeneratorUtil util = new CorresponSearchServiceGeneratorUtil(header);
            util.setBaseURL(getBaseURL());
            util.setContextURL(getContextURL());
            util.setStylesheetName(SystemConfig.getValue(TEMPLATE_KEY_STYLESHEET));
            generator.setUtil(util);

            return generator.generate();
        } catch (GeneratorFailedException e) {
            throw new ServiceAbortException(e.getMessage(), e, MessageCode.E_GENERATION_FAILED,
                                            e.getMessage());
        }
    }

    /*
     * (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.service.correspon.CorresponSearchService
     * #generateZip(java.util.List)
     */
    public byte[] generateZip(List<Correspon> correspons) throws ServiceAbortException {
        return generateZip(correspons, true);
    }

    /*
     * (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.service.correspon.CorresponSearchService
     * #generateZip(java.util.List, boolean)
     */
    public byte[] generateZip(List<Correspon> correspons, boolean usePersonInCharge)
        throws ServiceAbortException {
        ArgumentValidator.validateNotNull(correspons);
        // コレポン文書が未選択の場合はエラー
        checkRowSelected(correspons);
        try {
            ZipArchiver zip = new ZipArchiver();
            for (Correspon selected : correspons) {
                // 指定のコレポン文書を取得 -- 存在しない場合はエラー
                Correspon correspon = findCorresponDetail(selected.getId());

                // 応答履歴を取得し、表示用のビーンに詰める
                List<CorresponResponseHistory> histories =
                    corresponService.findCorresponResponseHistory(correspon);

                List<CorresponResponseHistoryModel> historyModels =
                    new ArrayList<CorresponResponseHistoryModel>();

                for (CorresponResponseHistory history : histories) {
                    CorresponResponseHistoryModel model = new CorresponResponseHistoryModel();
                    model.setCorresponResponseHistory(history);
                    historyModels.add(model);
                }

                HTMLGenerator generator =
                    new HTMLGenerator(SystemConfig.getValue(TEMPLATE_KEY_ZIP), correspon);
                CorresponHTMLGeneratorUtil util = new CorresponServiceGeneratorUtil(true);
                util.setCorresponResponseHistoryModel(historyModels);
                util.setBaseURL(getBaseURL());
                util.setContextURL(getContextURL());
                CorresponPageFormatter corresponPageFormatter = new CorresponPageFormatter();
                util.setIconName(
                    corresponPageFormatter.getProjectLogoUrl(correspon.getProjectId()));
                util.setStylesheetName(SystemConfig.getValue(TEMPLATE_KEY_STYLESHEET));
                util.setUsePersonInCharge(usePersonInCharge);
                generator.setUtil(util);

                zip.add(createCorresponHtmlName(correspon), generator.generate());
            }
            return zip.toByte();
        } catch (GeneratorFailedException e) {
            throw new ServiceAbortException(e.getMessage(), e, MessageCode.E_GENERATION_FAILED,
                                            e.getMessage());
        }
    }

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.service.correspon.CorresponSearchService#
     *                              updateCorresponsReadStatus(java.util.List)
     */
    public void updateCorresponsReadStatus(List<Correspon> correspons)
            throws ServiceAbortException {
        ArgumentValidator.validateNotNull(correspons);
        // コレポン文書が未選択の場合はエラー
        checkRowSelected(correspons);
        for (Correspon correspon : correspons) {
            // 指定のコレポン文書を取得（存在チェック）
            findCorrespon(correspon.getId());
            // コレポン文書を更新した為、既読→未読、未読→既読に変更
            corresponReadStatusService.updateReadStatusById(
                correspon.getId(), correspon.getCorresponReadStatus().getReadStatus());
        }
    }

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.service.correspon.CorresponSearchService#
     *                              updateCorresponsStatus(java.util.List)
     */
    public void updateCorresponsStatus(List<Correspon> correspons) throws ServiceAbortException {
        ArgumentValidator.validateNotNull(correspons);
        // コレポン文書が未選択の場合はエラー
        checkRowSelected(correspons);
        for (Correspon correspon : correspons) {
            // 権限チェック
            checkWorkflowStatus(correspon);
            // ステータスチェック
            checkCorresponStatus(correspon);
            // 更新
            updateCorrespon(createUpdatedCorrespon(correspon));
            // コレポン文書を更新した為、既読→未読に変更
            if (CorresponStatus.CANCELED == correspon.getCorresponStatus()) {
                corresponReadStatusService.updateReadStatusByCorresponId(
                            correspon.getId(), ReadStatus.NEW);
            }
        }
    }

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.service.correspon.CorresponSearchService#
     *                              deleteCorrespons(java.util.List)
     */
    public void deleteCorrespons(List<Correspon> correspons) throws ServiceAbortException {
        ArgumentValidator.validateNotNull(correspons);
        // コレポン文書が未選択の場合はエラー
        checkRowSelected(correspons);
        for (Correspon correspon : correspons) {
            // 権限チェック
            checkDeletePermission(correspon);
            // 更新
            deleteCorrespon(createDeletedCorrespon(correspon));
        }
    }

    /*
     * (non-Javadoc)
     * @seejp.co.opentone.bsol.linkbinder.service.correspon.CorresponSearchService#searchId(
     * CorresponSearchConditon)
     */
    @Transactional(readOnly = true)
    public List<Long> searchId(SearchCorresponCondition condition)
        throws ServiceAbortException {
        List<Long> result = findCorresponIdList(condition);
        // 該当データ0件の場合
        if (result.size() == 0) {
            throw new ServiceAbortException(ApplicationMessageCode.NO_DATA_FOUND);
        }
        return result;
    }

    /* (非 Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.service.correspon.CorresponSearchService#searchIdInPage(jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponCondition)
     */
    @Override
    @Transactional(readOnly = true)
    public List<Long> searchIdInPage(SearchCorresponCondition condition)
            throws ServiceAbortException {
        List<Long> result = findCorresponIdInPageList(condition);
        // 該当データ0件の場合
        if (result.size() == 0) {
            throw new ServiceAbortException(ApplicationMessageCode.NO_DATA_FOUND);
        }
        return result;
    }

    /**
     * コレポン文書一覧を取得する.
     * @param condition
     *            検索条件
     * @return コレポン文書のリスト
     */
    private List<Correspon> findCorresponList(SearchCorresponCondition condition) {
        CorresponDao dao = getDao(CorresponDao.class);
        return dao.find(condition);
    }

    /**
     * コレポン文書一覧を取得する.
     * @param condition
     *            検索条件
     * @return コレポン文書のリスト
     */
    private List<Long> findCorresponIdList(SearchCorresponCondition condition) {
        CorresponDao dao = getDao(CorresponDao.class);
        return dao.findId(condition);
    }

    /**
     * コレポン文書一覧を取得する.
     * @param condition
     *            検索条件
     * @return コレポン文書のリスト
     * @author opentone
     */
    private List<Long> findCorresponIdInPageList(SearchCorresponCondition condition) {
        CorresponDao dao = getDao(CorresponDao.class);
        return dao.findIdInPage(condition);
    }

    /**
     * 全文検索結果を含んだコレポン文書一覧を取得する.
     * 引数の contidion のプロパティを変更する。
     * @param condition 検索条件
     * @return コレポン文書のリスト
     * @throws ServiceAbortException 例外
     */
    private void setCorresponListFullTextSearchCondition(SearchCorresponCondition condition)
        throws ServiceAbortException {
        SearchFullTextSearchCorresponCondition fullTextSearchCondition
            = new SearchFullTextSearchCorresponCondition();
        try {
            fullTextSearchCondition.setKeyword(condition.getKeyword());
            fullTextSearchCondition.setFullTextSearchMode(condition.getFullTextSearchMode());
            List<FullTextSearchCorresponsResult> resultFullText
                = fullTextSearchService.searchNoLimit(fullTextSearchCondition);

            // 最大件数チェック
            validateHitNumber(resultFullText.size(), true);

            // 取得結果のIDを、通常検索条件にセットする
            List<Ids> idOuter = new ArrayList<Ids>();
            List<Long> idInner = new ArrayList<Long>();
            int i = 0;
            for (FullTextSearchCorresponsResult item : resultFullText) {
                i++;
                idInner.add(item.getId());
                // 全文検索結果が 規定件数 （SELECT 文 IN 句の最大要素数） を超える場合、条件を分ける
                if ((i % MAX_IN_ARGS) == 0) {
                    idOuter.add(new Ids(idInner));
                    idInner = new ArrayList<Long>();
                }
            }
            if (idInner.size() > 0) {
                idOuter.add(new Ids(idInner));
            }
            condition.setIdList(idOuter);
        } catch (ServiceAbortException e) {
            throw e;
        }
    }

    /**
     * 該当データの件数を取得する. 0件の場合、エラー.
     * @param condition
     *            検索条件
     * @return 件数
     * @throws ServiceAbortException
     *             件数が0件
     */
    private int getDataCount(SearchCorresponCondition condition) throws ServiceAbortException {
        CorresponDao dao = getDao(CorresponDao.class);
        int count = dao.count(condition);
        // 該当データ0件の場合
        if (count == 0) {
            throw new ServiceAbortException(ApplicationMessageCode.NO_DATA_FOUND);
        }
        return count;
    }

    /**
     * ページングで指定されたページにデータが存在するかチェックする.
     * @param corresponList
     *            一覧データ
     * @throws ServiceAbortException
     *             指定されたページにデータが存在しない
     */
    private void checkPagingData(List<Correspon> corresponList) throws ServiceAbortException {
        // 該当データ0件の場合
        if (corresponList.size() == 0) {
            throw new ServiceAbortException(ApplicationMessageCode.NO_PAGE_FOUND);
        }
    }

    /**
     * コレポン一覧画面に渡すオブジェクトを生成する.
     * @param corresponList
     *            一覧データ
     * @param count
     *            総件数
     * @return 一覧表示用オブジェクト
     */
    private SearchCorresponResult createResult(List<Correspon> corresponList, int count) {
        SearchCorresponResult result = new SearchCorresponResult();
        result.setCorresponList(corresponList);
        result.setCount(count);

        return result;
    }

    /**
     * コレポン文書が選択されているかチェックする.
     * @param correspons コレポン文書リスト
     * @throws ServiceAbortException コレポン文書が選択されていない
     */
    private void checkRowSelected(List<Correspon> correspons) throws ServiceAbortException {
        if (correspons.isEmpty()) {
            throw new ServiceAbortException(
                    ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CORRESPON_NOT_SELECTED);
        }
    }

    /**
     * 指定されたIDのコレポン文書を取得する.
     * @param id コレポン文書ID
     * @return コレポン文書
     * @throws ServiceAbortException 該当のコレポン文書が存在しない
     */
    private Correspon findCorrespon(Long id) throws ServiceAbortException {
        try {
            CorresponDao dao = getDao(CorresponDao.class);
            return dao.findById(id);
        } catch (RecordNotFoundException e) {
            throw new ServiceAbortException(ApplicationMessageCode.NO_DATA_FOUND);
        }
    }

    /**
     * 指定されたコレポン文書の承認状態が、ユーザーが更新可能な状態であるかチェックする.
     * <p>
     * 前提： ここでチェックするコレポン文書はユーザーが閲覧可能なもの.
     * </p>
     * @param correspon ユーザーが閲覧可能なコレポン文書
     * @throws ServiceAbortException 更新できない承認状態の場合
     */
    private void checkWorkflowStatus(Correspon correspon) throws ServiceAbortException {
        //  System Admin場合はチェックしない
        User u = getCurrentUser();
        if (isSystemAdmin(u)) {
            return;
        }
        //  Project Admin/Group Adminの場合は基本的に
        //  参照可能なコレポン文書は全て更新できるのでチェックしない
        //  但し他人の作成したコレポン文書の承認状態がDeniedの場合は更新できない
        if (isProjectAdmin(u, getCurrentProjectId())
            || isGroupAdmin(u, correspon.getFromCorresponGroup().getId())) {

            if (correspon.getWorkflowStatus() == WorkflowStatus.DENIED
                && !isPreparer(correspon.getCreatedBy().getEmpNo())) {
                throw new ServiceAbortException(
                    ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_WORKFLOW_STATUS_INVALID);
            }
            return;
        }

        //  承認フローの作業状態に関するチェックが必要なため
        //  ここで現在の承認フローをロード
        loadWorkflow(correspon);

        if (!isValidWorkflowStatusForLogin(correspon)) {
            throw new ServiceAbortException(
                        ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_WORKFLOW_STATUS_INVALID);
        }
    }

    private boolean isValidWorkflowStatusForLogin(Correspon c) {
        boolean valid = false;
        switch (c.getWorkflowStatus()) {
        case DRAFT:
            valid = isPreparer(c.getCreatedBy().getEmpNo());
            break;
        case ISSUED:
            valid = isIssueUpdatable(c);
            break;
        case REQUEST_FOR_CHECK:
        case REQUEST_FOR_APPROVAL:
        case UNDER_CONSIDERATION:
            valid = isVerifying(c);
            break;
        case DENIED:
            valid = isPreparer(c.getCreatedBy().getEmpNo());
            break;
        default:
            break;
        }
        return valid;
    }

    private boolean isIssueUpdatable(Correspon c) {
        return isPreparer(c.getCreatedBy().getEmpNo())
               || isChecker(c)
               || isApprover(c);
    }

    private boolean isVerifying(Correspon c) {
        boolean verifying = false;
        Workflow w = findMyWorkflow(c);

        if (w != null) {
            WorkflowProcessStatus s = w.getWorkflowProcessStatus();
            verifying = (s == WorkflowProcessStatus.REQUEST_FOR_CHECK
                         || s == WorkflowProcessStatus.REQUEST_FOR_APPROVAL
                         || s == WorkflowProcessStatus.UNDER_CONSIDERATION);
        }
        return verifying;
    }

    private void loadWorkflow(Correspon c) {
        WorkflowDao dao = getDao(WorkflowDao.class);
        c.setWorkflows(dao.findByCorresponId(c.getId()));
    }

    /**
     * コレポン文書に設定されている承認フロー中に ログインユーザーが設定されていればその情報を返す.
     * @param c
     *            コレポン文書
     * @return 承認フロー情報. ログインユーザーが承認フローに設定されていない場合はnull
     */
    private Workflow findMyWorkflow(Correspon c) {
        User me = getCurrentUser();
        for (Workflow wf : c.getWorkflows()) {
            if (me.getEmpNo().equals(wf.getUser().getEmpNo())) {
                return wf;
            }
        }
        return null;
    }

    /**
     * 文書状態更新のステータスチェックを行う.
     * @param correspon コレポン文書
     * @throws ServiceAbortException ステータスエラー
     */
    private void checkCorresponStatus(Correspon correspon) throws ServiceAbortException {
        Correspon dbData = findCorrespon(correspon.getId());
        if (CorresponStatus.CANCELED.equals(dbData.getCorresponStatus())) {
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CORREPON_CANCELED);
        }
    }

    /**
     * 文書状態更新用のオブジェクトを作成する.
     * @param old 更新対象のコレポン文書
     * @return 更新用のオブジェクト
     */
    private Correspon createUpdatedCorrespon(Correspon old) {
        Correspon correspon = new Correspon();
        correspon.setId(old.getId());
        correspon.setCorresponStatus(old.getCorresponStatus());
        correspon.setUpdatedBy(getCurrentUser());
        correspon.setVersionNo(old.getVersionNo());

        return correspon;
    }

    /**
     * コレポン文書を更新する.
     * @param correspon コレポン文書
     * @throws ServiceAbortException 更新に失敗
     */
    private void updateCorrespon(Correspon correspon) throws ServiceAbortException {
        try {
            CorresponDao dao = getDao(CorresponDao.class);
            dao.update(correspon);
        } catch (KeyDuplicateException e) {
            throw new ServiceAbortException(e);
        } catch (StaleRecordException e) {
            throw new ServiceAbortException(
                    ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CORRESPON_ALREADY_UPDATED);
        }
    }

    /**
     * コレポン文書を削除する権限をチェックする.
     * @param correspon コレポン文書
     * @throws ServiceAbortException 権限エラー
     */
    private void checkDeletePermission(Correspon correspon) throws ServiceAbortException {
        if (!WorkflowStatus.DRAFT.equals(correspon.getWorkflowStatus())) {
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CORRESPON_STATUS_INVALID);
        } else if (!isSystemAdmin(getCurrentUser())
                && !getCurrentUser().getEmpNo().equals(correspon.getCreatedBy().getEmpNo())) {
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT);
        }
    }

    /**
     * 文書状態削除用のオブジェクトを作成する.
     * @param old 削除対象のコレポン文書
     * @return 削除用のオブジェクト
     */
    private Correspon createDeletedCorrespon(Correspon old) {
        Correspon correspon = new Correspon();
        correspon.setId(old.getId());
        correspon.setUpdatedBy(getCurrentUser());
        correspon.setVersionNo(old.getVersionNo());

        return correspon;
    }

    /**
     * コレポン文書を削除する.
     * @param correspon コレポン文書
     * @throws ServiceAbortException 削除に失敗
     */
    private void deleteCorrespon(Correspon correspon) throws ServiceAbortException {
        try {
            CorresponDao dao = getDao(CorresponDao.class);
            dao.delete(correspon);
        } catch (KeyDuplicateException e) {
            throw new ServiceAbortException(e);
        } catch (StaleRecordException e) {
            throw new ServiceAbortException(
                    ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CORRESPON_ALREADY_UPDATED);
        }
    }

    /**
     * ZIPファイル内のHTMLファイルの名前を作成する.
     * <pre>
     * ファイル名：
     *     projectId_コレポン文書番号(コレポン文書ID).html
     *     ただし未発行の場合はprojectId_(コレポン文書ID).html
     *         ・コレポン文書IDは10桁前ゼロ埋め
     *         ・コレポン文書番号の「:」はWindowsのファイル名として使用できない文字なので、「-」に変換
     *         ・その他Windowsのファイル名として使用できない文字があれば「-」に変換
     * </pre>
     * @return HTMLファイル名
     */
    private String createCorresponHtmlName(Correspon correspon) {
        String corresponNo = correspon.getCorresponNo();
        if (corresponNo == null) { // NULLならば空文字に変換
            corresponNo = "";
        }
        String corresponId = String.format(FILENAME_ID_FORMAT, correspon.getId());

        String fileName = String.format(FILENAME_FORMAT,
                                        getCurrentProjectId(),
                                        corresponNo,
                                        corresponId);
        return convertFileName(fileName);
    }

    /**
     * Windowsのファイル名として使用できない文字を変換する.
     * @return ファイル名
     */
    private String convertFileName(String name) {
        String regex = SystemConfig.getValue(FILENAME_KEY_REGEX);
        if (StringUtils.isEmpty(regex)) {
            regex = FILENAME_DEFAULT_REGEX;
        }
        String replacement = SystemConfig.getValue(FILENAME_KEY_REPLACEMENT);
        if (replacement == null) { // 置換文字は空文字でも問題ない
            replacement = FILENAME_DEFAULT_REPLACEMENT;
        }
        return name.replaceAll(regex, replacement);
    }

    /**
     * コレポン文書の情報を関連する情報と一緒に取得する.
     * @throws ServiceAbortException 取得エラー
     */
    private Correspon findCorresponDetail(Long id) throws ServiceAbortException {
        try {
            Correspon clone = new Correspon();
            Correspon original = corresponService.find(id);
            PropertyUtils.copyProperties(clone, original);
            clone.setWorkflows(createDisplayWorkflowList(original));
            return clone;
        } catch (ServiceAbortException e) {
            throw new ServiceAbortException(ApplicationMessageCode.NO_DATA_FOUND);
        } catch (IllegalAccessException e) {
            throw new ServiceAbortException(e.getMessage());
        } catch (InvocationTargetException e) {
            throw new ServiceAbortException(e.getMessage());
        } catch (NoSuchMethodException e) {
            throw new ServiceAbortException(e.getMessage());
        }
    }

    /**
     * ハイパエストレイヤーより取得したコレポン文書の検索結果の件数チェックを行う.
     * @param count 検索結果件数
     * @param hasLimit 結果数の上限を設定するかどうかのフラグ
     * @throws ServiceAbortException 件数が多すぎる時に発生
     */
    private void validateHitNumber(int count, boolean hasLimit)
        throws ServiceAbortException {
        // 上限を設定しない場合、最大件数チェック不要
        if (!hasLimit) {
            return;
        }
        int max = Integer.parseInt(SystemConfig.getValue(Constants.ADVANCED_SEARCH_RESULT_MAX));
        // 検索結果件数が多すぎる際はエラー
        if (max < count) {
            throw new ServiceAbortException(ApplicationMessageCode.RETURNED_MORE_THAN_RECORDS, max);
        }
    }


    /**
     * 学習用プロジェクトのプロジェクトIDを返す.
     * @return 学習用プロジェクトID
     *
     */
    private String getLearningProjectId() {
        return SystemConfig.getValue(Constants.KEY_LEARNING_PJ);
    }
}
