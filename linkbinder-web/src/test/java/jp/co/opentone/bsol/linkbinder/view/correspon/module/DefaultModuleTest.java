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
package jp.co.opentone.bsol.linkbinder.view.correspon.module;

import static jp.co.opentone.bsol.framework.core.message.MessageCode.*;
import static jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode.*;
import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.faces.application.FacesMessage;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import jp.co.opentone.bsol.framework.core.exception.InvalidOperationRuntimeException;
import jp.co.opentone.bsol.framework.core.message.MessageCode;
import jp.co.opentone.bsol.framework.core.message.Messages;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.core.util.ArgumentValidator;
import jp.co.opentone.bsol.framework.core.util.DateUtil;
import jp.co.opentone.bsol.framework.extension.jsf.FacesContextMock;
import jp.co.opentone.bsol.framework.test.AbstractTestCase;
import jp.co.opentone.bsol.framework.web.view.flash.Flash;
import jp.co.opentone.bsol.framework.web.view.util.ViewHelper;
import jp.co.opentone.bsol.linkbinder.dto.AddressCorresponGroup;
import jp.co.opentone.bsol.linkbinder.dto.Correspon;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroup;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroupUserMapping;
import jp.co.opentone.bsol.linkbinder.dto.CorresponReadStatus;
import jp.co.opentone.bsol.linkbinder.dto.CorresponResponseHistory;
import jp.co.opentone.bsol.linkbinder.dto.CorresponType;
import jp.co.opentone.bsol.linkbinder.dto.Project;
import jp.co.opentone.bsol.linkbinder.dto.ProjectUser;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.Workflow;
import jp.co.opentone.bsol.linkbinder.dto.WorkflowPattern;
import jp.co.opentone.bsol.linkbinder.dto.code.AddressType;
import jp.co.opentone.bsol.linkbinder.dto.code.CorresponStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.ReadStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowType;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponCondition;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponGroupCondition;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchFullTextSearchCorresponCondition;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchUserCondition;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.service.admin.impl.CorresponGroupServiceImpl;
import jp.co.opentone.bsol.linkbinder.service.admin.impl.UserServiceImpl;
import jp.co.opentone.bsol.linkbinder.service.correspon.impl.CorresponFullTextSearchServiceImpl;
import jp.co.opentone.bsol.linkbinder.service.correspon.impl.CorresponReadStatusServiceImpl;
import jp.co.opentone.bsol.linkbinder.service.correspon.impl.CorresponSearchServiceImpl;
import jp.co.opentone.bsol.linkbinder.service.correspon.impl.CorresponServiceImpl;
import jp.co.opentone.bsol.linkbinder.util.view.correspon.CorresponResponseHistoryModel;
import jp.co.opentone.bsol.linkbinder.view.AbstractPage;
import jp.co.opentone.bsol.linkbinder.view.correspon.CorresponPage;
import jp.co.opentone.bsol.linkbinder.view.correspon.util.CorresponEditMode;
import mockit.Mock;
import mockit.MockUp;

/**
 * {@link DefaultModule}のテストケース.
 * @author opentone
 */
public class DefaultModuleTest extends AbstractTestCase {

    /**
     * ページクラス.
     */
    @Resource(name = "corresponPage")
    private CorresponPage page;

    /**
     * テスト対象.
     */
    @Resource
    private DefaultModule defaultModule;

    /** checker1 */
    private static final User CHECKER_1 = new User();
    static {
        CHECKER_1.setEmpNo("USER001");
    }

    /** checker2 */
    private static final User CHECKER_2 = new User();
    static {
        CHECKER_2.setEmpNo("USER002");
    }

    /** checker3 */
    private static final User CHECKER_3 = new User();
    static {
        CHECKER_3.setEmpNo("USER003");
    }

    /** approver */
    private static final User APPROVER = new User();
    static {
        APPROVER.setEmpNo("USER004");
    }

    /**
     * ログインユーザー
     */
    private User loginUser;

    /**
     * テストの前準備.
     */
    @BeforeClass
    public static void testSetUp() {
        FacesContextMock.initialize();
        new MockCorresponService();
        new MockAbstractPage();
        new MockViewHelper();
        new MockCorresponReadStatusService();
        new MockCorresponGroupService();
        new MockUserService();
        new MockCorresponPage();
    }

    /**
     * 後始末.
     */
    @AfterClass
    public static void testTeardown() {
        new MockCorresponService().tearDown();
        new MockAbstractPage().tearDown();
        new MockViewHelper().tearDown();
        new MockCorresponReadStatusService().tearDown();
        new MockCorresponGroupService().tearDown();
        new MockUserService().tearDown();
        new MockCorresponPage().tearDown();
        FacesContextMock.tearDown();
    }

    @Before
    public void setUp() {
        FacesContextMock.EXPECTED_MESSAGE = null;
        FacesContextMock.IS_ADD_MESSAGE_CALLED = false;
        loginUser = new User();
        loginUser.setEmpNo("USER002");
        loginUser.setNameE("Taro Yamada");
        page.setCurrentUser(loginUser);
        page.setId(null);

        page.setTosDisplay(CorresponPage.STYLE_SHOW);
        page.setBodyDisplay(CorresponPage.STYLE_SHOW);
        page.setAttachmentsDisplay(CorresponPage.STYLE_SHOW);
        page.setCustomFieldsDisplay(CorresponPage.STYLE_SHOW);
        page.setResponseHistoryDisplay(CorresponPage.STYLE_HIDE);
        page.setCorresponResponseHistory(null);
    }

    @After
    public void tearDown() {
        MockCorresponService.RET_FIND = null;
        MockCorresponService.RET_REQUESTFORAPPROVAL = null;
        MockCorresponService.RET_HTML = null;
        MockCorresponService.SET_CORRESPON = null;
        MockCorresponService.CRT_SAVE_PARTIAL = null;
        MockCorresponService.EXP_FIND = null;
        MockViewHelper.EX_RESPONSE = null;
        MockViewHelper.SET_DATA = null;

        MockCorresponReadStatusService.SET_CREATE = new ArrayList<CorresponReadStatus>();
        MockCorresponReadStatusService.SET_UPDATE = new ArrayList<CorresponReadStatus>();
        MockCorresponReadStatusService.SET_READ_CREATE = new CorresponReadStatus();
        MockCorresponReadStatusService.EX_UPDATE = null;

        MockCorresponGroupService.RET_FIND_CORRESPON_GROUP_USER_MAPPINGS = null;
        MockCorresponGroupService.RET_SEARCH = null;
        MockUserService.RET_SEARCH = null;
    }

    /**
     * 初期化アクションのテスト.
     * @throws Exception
     */
    @Test
    public void testInitialize() throws Exception {
        setRequiredFields();

        MockAbstractPage.RET_SYSTEM_ADMIN = false;
        MockAbstractPage.RET_PROJECT_ADMIN = true;
        MockAbstractPage.RET_GROUP_ADMIN = false;

        User user = new User();
        user.setEmpNo("USER004");
        MockAbstractPage.RET_USER = user;
        MockCorresponReadStatusService.RET_CURRENT_USER = user;

        Project project = new Project();
        project.setProjectId("PJ1");
        MockAbstractPage.RET_PROJECT = project;

        // Serviceが返すダミーのコレポン文書を設定
        Correspon expCorrespon = createCorrespon(10L);
        List<Workflow> workflowList = createWorkflowList();

        expCorrespon.setWorkflows(workflowList);
        expCorrespon.setWorkflowStatus(WorkflowStatus.DENIED);
        expCorrespon.setBody("<p>1234567890</p>");

        MockCorresponService.RET_FIND = expCorrespon;
        MockCorresponGroupService.RET_FIND_CORRESPON_GROUP_USER_MAPPINGS =
                new ArrayList<CorresponGroupUserMapping>();
        MockCorresponGroupService.RET_SEARCH = new ArrayList<CorresponGroup>();
        MockUserService.RET_SEARCH = new ArrayList<ProjectUser>();

        // 必須のプロパティをセットして実行
        page.setId(expCorrespon.getId());
        defaultModule.initialize();

        assertEquals(expCorrespon.toString(), page.getCorrespon().toString());

        // prerender実行後の、Show Detailsの初期状態を確認
        page.prerender();
        assertEquals(CorresponPage.LABEL_HIDE_DETAILS, page.getTosDetail());
        assertEquals(CorresponPage.STYLE_SHOW, page.getTosDisplay());
        assertEquals(CorresponPage.LABEL_HIDE_DETAILS, page.getBodyDetail());
        assertEquals(CorresponPage.STYLE_SHOW, page.getBodyDisplay());
        assertEquals(CorresponPage.LABEL_HIDE_DETAILS, page.getAttachmentsDetail());
        assertEquals(CorresponPage.STYLE_SHOW, page.getAttachmentsDisplay());
        assertEquals(CorresponPage.LABEL_HIDE_DETAILS, page.getCustomFieldsDetail());
        assertEquals(CorresponPage.STYLE_SHOW, page.getCustomFieldsDisplay());
        assertEquals(CorresponPage.LABEL_HIDE_DETAILS, page.getResponseHistoryDetail());
        assertEquals(CorresponPage.STYLE_SHOW, page.getResponseHistoryDisplay());

        assertEquals("<p>1234<wbr/>5678<wbr/>90</p>", page.getDisplayBody());
   }


    /**
     * 起動元からコレポン文書のIDが指定されない状態で実行した場合の検証.
     * @throws Exception
     */
    @Test
    public void testInitializedInvalidParam() throws Exception {
        setRequiredFields();

        // 期待されるエラーメッセージを用意
        FacesContextMock.EXPECTED_CLIENT_ID = null;
        FacesContextMock.EXPECTED_MESSAGE =
                new FacesMessage(FacesMessage.SEVERITY_ERROR, FacesMessage.SEVERITY_ERROR
                    .toString(), "パラメータが正しくありません。リクエストは失敗しました。");

        // 必須のプロパティをセットせず実行

        try {
            defaultModule.initialize();
            fail("例外が発生していない");
        } catch (InvalidOperationRuntimeException actual) {
            assertEquals(actual.getCause().getMessage(), "ID is not specified.");
        }

        assertNull(page.getCorrespon());

        // prerender実行後の、Show Detailsの初期状態を確認
        page.prerender();
        assertEquals(CorresponPage.LABEL_HIDE_DETAILS, page.getTosDetail());
        assertEquals(CorresponPage.STYLE_SHOW, page.getTosDisplay());
        assertEquals(CorresponPage.LABEL_HIDE_DETAILS, page.getAttachmentsDetail());
        assertEquals(CorresponPage.STYLE_SHOW, page.getAttachmentsDisplay());
        assertEquals(CorresponPage.LABEL_HIDE_DETAILS, page.getCustomFieldsDetail());
        assertEquals(CorresponPage.STYLE_SHOW, page.getCustomFieldsDisplay());
    }

    /**
     * 起動元からコレポン文書のIDが指定されない状態で実行した場合の検証.
     * @throws Exception
     */
    @Test
    public void testInitializedInvalidWorkflowStatus() throws Exception {
        setRequiredFields();

        // 期待されるエラーメッセージを用意
        FacesContextMock.EXPECTED_CLIENT_ID = null;
        FacesContextMock.EXPECTED_MESSAGE =
                new FacesMessage(FacesMessage.SEVERITY_ERROR, FacesMessage.SEVERITY_ERROR
                    .toString(), "アクセスレベルが不十分なため、この操作を行うことができません。操作：[Initialize]");

        page.setId(1L);
        page.setActionName("Initialize");

        // Serviceが返すダミーのコレポン文書を設定
        Correspon expCorrespon = createCorrespon(10L);
        List<Workflow> workflowList = createWorkflowList();

        expCorrespon.setWorkflows(workflowList);
        expCorrespon.setWorkflowStatus(WorkflowStatus.DENIED);
        expCorrespon.setBody("<p>1234567890</p>");

        MockCorresponService.RET_FIND = expCorrespon;
        MockCorresponGroupService.RET_FIND_CORRESPON_GROUP_USER_MAPPINGS =
                new ArrayList<CorresponGroupUserMapping>();
        MockCorresponGroupService.RET_SEARCH = new ArrayList<CorresponGroup>();
        MockUserService.RET_SEARCH = new ArrayList<ProjectUser>();

        MockCorresponService.EXP_FIND = new ServiceAbortException(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_INVISIBLE_CORRESPON);

        defaultModule.initialize();

        assertNull(page.getCorrespon());

        // prerender実行後の、Show Detailsの初期状態を確認
        page.prerender();
        assertEquals(CorresponPage.LABEL_HIDE_DETAILS, page.getTosDetail());
        assertEquals(CorresponPage.STYLE_SHOW, page.getTosDisplay());
        assertEquals(CorresponPage.LABEL_HIDE_DETAILS, page.getAttachmentsDetail());
        assertEquals(CorresponPage.STYLE_SHOW, page.getAttachmentsDisplay());
        assertEquals(CorresponPage.LABEL_HIDE_DETAILS, page.getCustomFieldsDetail());
        assertEquals(CorresponPage.STYLE_SHOW, page.getCustomFieldsDisplay());
    }

    /**
     * 別コレポン文書取得アクションのテスト.
     * <p>
     * 持っている応答履歴リスト内に当該コレポン文書が存在する.
     * </p>
     * @throws Exception
     */
    @Test
    public void testChangeCorrespon1() throws Exception {
        setRequiredFields();

        User retUser = new User();
        retUser.setEmpNo("USER004");
        MockAbstractPage.RET_USER = retUser;
        MockCorresponReadStatusService.RET_CURRENT_USER = retUser;

        MockAbstractPage.RET_SYSTEM_ADMIN = false;
        MockAbstractPage.RET_PROJECT_ADMIN = true;
        MockAbstractPage.RET_GROUP_ADMIN = false;

        User user = new User();
        user.setEmpNo("USER004");
        MockAbstractPage.RET_USER = user;
        MockCorresponReadStatusService.RET_CURRENT_USER = user;

        Project project = new Project();
        project.setProjectId("PJ1");
        MockAbstractPage.RET_PROJECT = project;

        // Serviceが返すダミーのコレポン文書を設定
        Correspon expCorrespon = createCorrespon(10L);
        List<Workflow> workflowList = createWorkflowList();

        expCorrespon.setWorkflows(workflowList);
        expCorrespon.setWorkflowStatus(WorkflowStatus.DENIED);
        expCorrespon.setBody("<p>1234567890</p>");

        MockCorresponService.RET_FIND = expCorrespon;
        MockCorresponGroupService.RET_FIND_CORRESPON_GROUP_USER_MAPPINGS =
                new ArrayList<CorresponGroupUserMapping>();
        MockCorresponGroupService.RET_SEARCH = new ArrayList<CorresponGroup>();
        MockUserService.RET_SEARCH = new ArrayList<ProjectUser>();

        // changeCorrespon実行前の状態をセット
        List<CorresponResponseHistory> expect = createCorresponResponseHistoryList(10L, 1);
        page.setCorresponResponseHistory(expect);
        page.setTosDisplay(CorresponPage.STYLE_HIDE);
        page.setTosDetail(CorresponPage.LABEL_SHOW_DETAILS);
        page.setBodyDisplay(CorresponPage.STYLE_HIDE);
        page.setBodyDetail(CorresponPage.LABEL_SHOW_DETAILS);
        page.setAttachmentsDisplay(CorresponPage.STYLE_HIDE);
        page.setAttachmentsDetail(CorresponPage.LABEL_SHOW_DETAILS);
        page.setCustomFieldsDisplay(CorresponPage.STYLE_HIDE);
        page.setCustomFieldsDetail(CorresponPage.LABEL_SHOW_DETAILS);
        page.setResponseHistoryDisplay(CorresponPage.STYLE_SHOW);
        page.setResponseHistoryDetail(CorresponPage.LABEL_HIDE_DETAILS);
        page.setWorkflowDisplay(CorresponPage.STYLE_HIDE);
        page.setWorkflowDetail(CorresponPage.LABEL_SHOW_DETAILS);

        // 必須のプロパティをセットして実行
        page.setId(expCorrespon.getId());

        defaultModule.changeCorrespon();

        // ページ描画前処理
        page.prerender();

        assertEquals(CorresponPage.LABEL_HIDE_DETAILS, page.getTosDetail());
        assertEquals(CorresponPage.STYLE_SHOW, page.getTosDisplay());
        assertEquals(CorresponPage.LABEL_HIDE_DETAILS, page.getBodyDetail());
        assertEquals(CorresponPage.STYLE_SHOW, page.getBodyDisplay());
        assertEquals(CorresponPage.LABEL_HIDE_DETAILS, page.getAttachmentsDetail());
        assertEquals(CorresponPage.STYLE_SHOW, page.getAttachmentsDisplay());
        assertEquals(CorresponPage.LABEL_HIDE_DETAILS, page.getCustomFieldsDetail());
        assertEquals(CorresponPage.STYLE_SHOW, page.getCustomFieldsDisplay());
        assertEquals(CorresponPage.LABEL_HIDE_DETAILS, page.getResponseHistoryDetail());
        assertEquals(CorresponPage.STYLE_SHOW, page.getResponseHistoryDisplay());
        assertEquals(expCorrespon.toString(), page.getCorrespon().toString());
        assertEquals(expect, page.getCorresponResponseHistory());
    }

    /**
     * 別コレポン文書取得アクションのテスト.
     * <p>
     * 持っている応答履歴リスト内に当該コレポン文書が存在しない.
     * </p>
     * @throws Exception
     */
    @Test
    public void testChangeCorrespon2() throws Exception {
        setRequiredFields();

        MockAbstractPage.RET_SYSTEM_ADMIN = false;
        MockAbstractPage.RET_PROJECT_ADMIN = true;
        MockAbstractPage.RET_GROUP_ADMIN = false;

        User user = new User();
        user.setEmpNo("USER004");
        MockAbstractPage.RET_USER = user;
        MockCorresponReadStatusService.RET_CURRENT_USER = user;

        Project project = new Project();
        project.setProjectId("PJ1");
        MockAbstractPage.RET_PROJECT = project;

        // Serviceが返すダミーのコレポン文書を設定
        Correspon expCorrespon = createCorrespon(20L);
        List<Workflow> workflowList = createWorkflowList();

        expCorrespon.setWorkflows(workflowList);
        expCorrespon.setWorkflowStatus(WorkflowStatus.DENIED);
        expCorrespon.setBody("<p>1234567890</p>");

        MockCorresponService.RET_FIND = expCorrespon;
        MockCorresponGroupService.RET_FIND_CORRESPON_GROUP_USER_MAPPINGS =
                new ArrayList<CorresponGroupUserMapping>();
        MockCorresponGroupService.RET_SEARCH = new ArrayList<CorresponGroup>();
        MockUserService.RET_SEARCH = new ArrayList<ProjectUser>();

        // changeCorrespon実行前の状態をセット
        List<CorresponResponseHistory> expect = createCorresponResponseHistoryList(10L, 4);
        page.setCorresponResponseHistory(expect);
        page.setTosDisplay(CorresponPage.STYLE_HIDE);
        page.setTosDetail(CorresponPage.LABEL_SHOW_DETAILS);
        page.setBodyDisplay(CorresponPage.STYLE_HIDE);
        page.setBodyDetail(CorresponPage.LABEL_SHOW_DETAILS);
        page.setAttachmentsDisplay(CorresponPage.STYLE_HIDE);
        page.setAttachmentsDetail(CorresponPage.LABEL_SHOW_DETAILS);
        page.setCustomFieldsDisplay(CorresponPage.STYLE_HIDE);
        page.setCustomFieldsDetail(CorresponPage.LABEL_SHOW_DETAILS);
        page.setResponseHistoryDisplay(CorresponPage.STYLE_SHOW);
        page.setResponseHistoryDetail(CorresponPage.LABEL_HIDE_DETAILS);
        page.setWorkflowDisplay(CorresponPage.STYLE_HIDE);
        page.setWorkflowDetail(CorresponPage.LABEL_SHOW_DETAILS);

        // 必須のプロパティをセットして実行
        page.setId(expCorrespon.getId());

        defaultModule.changeCorrespon();

        // ページ描画前処理
        page.prerender();

        assertEquals(CorresponPage.LABEL_HIDE_DETAILS, page.getTosDetail());
        assertEquals(CorresponPage.STYLE_SHOW, page.getTosDisplay());
        assertEquals(CorresponPage.LABEL_HIDE_DETAILS, page.getBodyDetail());
        assertEquals(CorresponPage.STYLE_SHOW, page.getBodyDisplay());
        assertEquals(CorresponPage.LABEL_HIDE_DETAILS, page.getAttachmentsDetail());
        assertEquals(CorresponPage.STYLE_SHOW, page.getAttachmentsDisplay());
        assertEquals(CorresponPage.LABEL_HIDE_DETAILS, page.getCustomFieldsDetail());
        assertEquals(CorresponPage.STYLE_SHOW, page.getCustomFieldsDisplay());
        assertEquals(CorresponPage.LABEL_SHOW_DETAILS, page.getResponseHistoryDetail());
        assertEquals(CorresponPage.STYLE_HIDE, page.getResponseHistoryDisplay());
        assertEquals(expCorrespon.toString(), page.getCorrespon().toString());
        assertNull(page.getCorresponResponseHistory());
    }

    /**
     * HTML出力（印刷）アクションのテスト.
     * @throws Exception
     */
    @Test
    public void testPrint() throws Exception {
        setRequiredFields();

        Correspon correspon = createCorrespon(10L);
        page.setCorrespon(correspon);

        String expected = "OUTPUT-HTML-VALUE";
        MockCorresponService.RET_HTML = expected.getBytes();
        List<CorresponResponseHistory> expList = createCorresponResponseHistoryList(10L, 1);
        MockCorresponService.RET_CORRESPON_RESPONSE_HISTORY = expList;

        defaultModule.print();

        Correspon actual = MockCorresponService.SET_CORRESPON;

        // Preparerが追加されている
        assertNotSame(correspon, actual);
        // Preparer
        assertEquals(correspon.getCreatedBy().getEmpNo(), actual.getWorkflows().get(0).getUser()
            .getEmpNo());
        assertEquals(expected, new String(MockViewHelper.SET_DATA));
        assertEquals(expected, new String(MockViewHelper.SET_DATA));
        assertEquals(expList, page.getCorresponResponseHistory());
    }

    /**
     * HTML出力（印刷）アクションのテスト. IOExceptionが発生.
     * @throws Exception
     */
    @Test
    public void testPrintException() throws Exception {
        setRequiredFields();

        Correspon correspon = new Correspon();
        correspon.setId(1L);
        correspon.setCorresponNo("YOC:OT:IT-00001");

        page.setCorrespon(correspon);

        String expected = "OUTPUT-HTML-VALUE";
        MockCorresponService.RET_HTML = expected.getBytes();
        MockCorresponService.RET_CORRESPON_RESPONSE_HISTORY =
            createCorresponResponseHistoryList(10L, 1);

        MockViewHelper.EX_RESPONSE = new IOException();

        // 期待されるメッセージをセット
        FacesContextMock.EXPECTED_CLIENT_ID = null;
        FacesContextMock.EXPECTED_MESSAGE =
                new FacesMessage(FacesMessage.SEVERITY_ERROR, FacesMessage.SEVERITY_ERROR
                    .toString(), createExpectedMessageString(Messages
                    .getMessageAsString(MessageCode.E_DOWNLOAD_FAILED), null));

        defaultModule.print();
    }

    /**
     * 発行アクションのテスト.
     * @throws Exception
     */
    @Test
    public void testIssue() throws Exception {
        setRequiredFields();

        // テストに必要な値をセット
        Correspon correspon = new Correspon();
        page.setCorrespon(correspon);

        MockCorresponService.EX_VALIDATE = null;

        // 期待されるメッセージをセット
        FacesContextMock.EXPECTED_MESSAGE =
                new FacesMessage(
                    FacesMessage.SEVERITY_INFO, FacesMessage.SEVERITY_INFO.toString(),
                    createExpectedMessageString(Messages.getMessageAsString(CORRESPON_ISSUED),
                        "Issue"));

        // 戻り先切り替え
        page.setBackPage("corresponSearch");
        page.setActionName("Issue");
        assertEquals("corresponSearch?afterAction=true&sessionSort=1&sessionPageNo=1", defaultModule.issue());
        assertEquals("文書は発行されました。", MockCorresponService.RET_ISSUE);

        // 戻り先切り替え
        page.setBackPage(null);
        page.setActionName("Issue");
        assertEquals("corresponIndex?afterAction=true&sessionSort=1&sessionPageNo=1", defaultModule.issue());
        assertEquals("文書は発行されました。", MockCorresponService.RET_ISSUE);
    }

    /**
     * 発行アクションのテスト(例外発生).
     * @throws Exception
     */
    @Test
    public void testIssueInvalid() throws Exception {
        setRequiredFields();

        // テストに必要な値をセット
        Correspon correspon = new Correspon();
        page.setCorrespon(correspon);

        // Serviceで発生する検証例外をセット
        MockCorresponService.EX_VALIDATE =
                new ServiceAbortException(CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT);

        // 期待されるメッセージをセット
        FacesContextMock.EXPECTED_MESSAGE =
                new FacesMessage(FacesMessage.SEVERITY_ERROR, FacesMessage.SEVERITY_ERROR
                    .toString(), createExpectedMessageString(Messages
                    .getMessageAsString(CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT), "Issue"));

        try {
            page.setActionName("Issue");
            assertNull(defaultModule.issue());
            fail("例外が発生していない");
        } catch (InvalidOperationRuntimeException e) {
            ServiceAbortException actual = (ServiceAbortException) e.getCause();
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT,
                actual.getMessageCode());
        }
    }

    /**
     * 検証依頼アクションのテスト.
     * @throws Exception
     */
    @Test
    public void testRequestForApproval() throws Exception {
        setRequiredFields();

        // テストに必要な値をセット
        MockAbstractPage.RET_SYSTEM_ADMIN = false;
        MockAbstractPage.RET_PROJECT_ADMIN = false;
        MockAbstractPage.RET_GROUP_ADMIN = false;
        User user = new User();
        user.setEmpNo("USER004");

        MockAbstractPage.RET_USER = user;

        Project project = new Project();
        project.setProjectId("PJ1");
        MockAbstractPage.RET_PROJECT = project;

        // Serviceが返すダミーのコレポン文書を設定
        Correspon expCorrespon = createCorrespon(10L);
        List<Workflow> workflowList = createWorkflowList();

        expCorrespon.setWorkflows(workflowList);
        expCorrespon.setWorkflowStatus(WorkflowStatus.DENIED);

        MockCorresponService.RET_FIND = expCorrespon;

        MockCorresponGroupService.RET_FIND_CORRESPON_GROUP_USER_MAPPINGS =
                new ArrayList<CorresponGroupUserMapping>();
        MockCorresponGroupService.RET_SEARCH = new ArrayList<CorresponGroup>();
        MockUserService.RET_SEARCH = new ArrayList<ProjectUser>();

        page.setId(expCorrespon.getId());

        page.setCorrespon(new Correspon());

        FacesContextMock.EXPECTED_MESSAGE =
                new FacesMessage(
                    FacesMessage.SEVERITY_INFO, FacesMessage.SEVERITY_INFO.toString(),
                    "文書は検証依頼されました。");

        defaultModule.requestForApproval();

        assertEquals("文書は検証依頼されました。", MockCorresponService.RET_REQUESTFORAPPROVAL);

        assertEquals(expCorrespon.toString(), page.getCorrespon().toString());

        // prerender実行後の、Show Detailsの初期状態を確認
        page.prerender();
        assertEquals(CorresponPage.LABEL_HIDE_DETAILS, page.getTosDetail());
        assertEquals(CorresponPage.STYLE_SHOW, page.getTosDisplay());
        assertEquals(CorresponPage.LABEL_HIDE_DETAILS, page.getAttachmentsDetail());
        assertEquals(CorresponPage.STYLE_SHOW, page.getAttachmentsDisplay());
        assertEquals(CorresponPage.LABEL_HIDE_DETAILS, page.getCustomFieldsDetail());
        assertEquals(CorresponPage.STYLE_SHOW, page.getCustomFieldsDisplay());
    }

    /**
     * 既読アクションのテスト.
     * @throws Exception
     */
    @Test
    public void testRead() throws Exception {
        setRequiredFields();

        User user = new User();
        user.setEmpNo("USER004");
        MockAbstractPage.RET_USER = user;
        MockCorresponReadStatusService.RET_CURRENT_USER = user;

        // テストに必要な値をセット
        Correspon correspon = new Correspon();
        correspon.setId(100L) ;
        page.setCorrespon(correspon);

        MockCorresponService.EX_VALIDATE = null;

        // 期待されるメッセージをセット
        FacesContextMock.EXPECTED_MESSAGE =
                new FacesMessage(
                    FacesMessage.SEVERITY_INFO, FacesMessage.SEVERITY_INFO.toString(),
                    createExpectedMessageString(Messages.getMessageAsString(SAVE_SUCCESSFUL),
                        null));

        defaultModule.read();

        assertEquals(100L, MockCorresponReadStatusService.SET_READ_CREATE.getCorresponId().longValue());
        assertEquals("USER004", MockCorresponReadStatusService.SET_READ_CREATE.getEmpNo());
        assertEquals(ReadStatus.READ, MockCorresponReadStatusService.SET_READ_CREATE.getReadStatus());
        assertEquals("USER004", MockCorresponReadStatusService.SET_READ_CREATE.getCreatedBy().getEmpNo());
        assertEquals("USER004", MockCorresponReadStatusService.SET_READ_CREATE.getUpdatedBy().getEmpNo());
    }

    /**
     * 未読アクションのテスト.
     * @throws Exception
     */
    @Test
    public void testUnread() throws Exception {
        setRequiredFields();

        User user = new User();
        user.setEmpNo("USER004");
        MockAbstractPage.RET_USER = user;
        MockCorresponReadStatusService.RET_CURRENT_USER = user;

        // テストに必要な値をセット
        Correspon correspon = new Correspon();
        correspon.setId(100L) ;
        page.setCorrespon(correspon);

        MockCorresponService.EX_VALIDATE = null;

        // 期待されるメッセージをセット
        FacesContextMock.EXPECTED_MESSAGE =
                new FacesMessage(
                    FacesMessage.SEVERITY_INFO, FacesMessage.SEVERITY_INFO.toString(),
                    createExpectedMessageString(Messages.getMessageAsString(SAVE_SUCCESSFUL),
                        null));

        defaultModule.unread();

        assertEquals(100L, MockCorresponReadStatusService.SET_READ_CREATE.getCorresponId().longValue());
        assertEquals("USER004", MockCorresponReadStatusService.SET_READ_CREATE.getEmpNo());
        assertEquals(ReadStatus.NEW, MockCorresponReadStatusService.SET_READ_CREATE.getReadStatus());
        assertEquals("USER004", MockCorresponReadStatusService.SET_READ_CREATE.getCreatedBy().getEmpNo());
        assertEquals("USER004", MockCorresponReadStatusService.SET_READ_CREATE.getUpdatedBy().getEmpNo());
    }

    /**
     * 削除アクションのテスト.
     * @throws Exception
     */
    @Test
    public void testDelete() throws Exception {
        setRequiredFields();

        // テストに必要な値をセット
        Correspon correspon = new Correspon();
        page.setCorrespon(correspon);

        MockCorresponService.EX_VALIDATE = null;

        // 期待されるメッセージをセット
        FacesContextMock.EXPECTED_MESSAGE =
                new FacesMessage(
                    FacesMessage.SEVERITY_INFO, FacesMessage.SEVERITY_INFO.toString(),
                    createExpectedMessageString(Messages.getMessageAsString(CORRESPON_DELETED),
                        "Delete"));

        // 戻り先切り替え
        page.setBackPage("corresponSearch");
        page.setActionName("Delete");
        assertEquals("corresponSearch?afterAction=true&sessionSort=1&sessionPageNo=1", defaultModule.delete());
        assertEquals("文書は削除されました。", MockCorresponService.RET_DELETE);

        // デフォルトの戻り先へ
        page.setBackPage(null);
        page.setActionName("Delete");
        assertEquals("corresponIndex?afterAction=true&sessionSort=1&sessionPageNo=1", defaultModule.delete());
        assertEquals("文書は削除されました。", MockCorresponService.RET_DELETE);
    }

    /**
     * 削除アクションのテスト(例外発生).
     * @throws Exception
     */
    @Test
    public void testDeleteInvalid() throws Exception {
        setRequiredFields();

        // テストに必要な値をセット
        Correspon correspon = new Correspon();
        page.setCorrespon(correspon);

        // Serviceで発生する検証例外をセット
        MockCorresponService.EX_VALIDATE =
                new ServiceAbortException(CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT);

        // 期待されるメッセージをセット
        FacesContextMock.EXPECTED_MESSAGE =
                new FacesMessage(
                    FacesMessage.SEVERITY_ERROR, FacesMessage.SEVERITY_ERROR.toString(),
                    createExpectedMessageString(Messages
                        .getMessageAsString(CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT),
                        "Delete"));
        try {
            page.setActionName("Delete");
            assertNull(defaultModule.delete());
            fail("例外が発生していない");
        } catch (InvalidOperationRuntimeException actual) {
            ServiceAbortException e = (ServiceAbortException) actual.getCause();
            assertEquals(e.getMessageCode(),
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT);

        }
    }

    /**
     * 更新アクションのテスト.
     * @throws Exception
     */
    @Test
    public void testSave() throws Exception {
        setRequiredFields();

        MockAbstractPage.RET_SYSTEM_ADMIN = false;
        MockAbstractPage.RET_PROJECT_ADMIN = true;
        MockAbstractPage.RET_GROUP_ADMIN = false;
        User user = new User();
        user.setEmpNo("USER004");

        MockAbstractPage.RET_USER = user;

        MockCorresponReadStatusService.RET_CURRENT_USER = user;

        Project project = new Project();
        project.setProjectId("PJ1");
        MockAbstractPage.RET_PROJECT = project;

        // Serviceが返すダミーのコレポン文書を設定
        Correspon expCorrespon = createCorrespon(10L);
        List<Workflow> workflowList = createWorkflowList();

        expCorrespon.setWorkflows(workflowList);
        expCorrespon.setWorkflowStatus(WorkflowStatus.DENIED);

        expCorrespon.setBody("<p>1234567890</p>");

        MockCorresponService.RET_FIND = expCorrespon;
        // 必須のプロパティをセットして実行
        page.setId(expCorrespon.getId());
        page.setDeadlineForReply(DateUtil.convertDateToString(new GregorianCalendar(
            0, 0, 0, 0, 0, 0).getTime()));
        page.setCorrespon(expCorrespon);

        MockCorresponGroupService.RET_FIND_CORRESPON_GROUP_USER_MAPPINGS =
                new ArrayList<CorresponGroupUserMapping>();
        MockCorresponGroupService.RET_SEARCH = new ArrayList<CorresponGroup>();
        MockUserService.RET_SEARCH = new ArrayList<ProjectUser>();

        // 期待されるメッセージをセット
        FacesContextMock.EXPECTED_MESSAGE =
                new FacesMessage(
                    FacesMessage.SEVERITY_INFO, FacesMessage.SEVERITY_INFO.toString(),
                    createExpectedMessageString(Messages.getMessageAsString(SAVE_SUCCESSFUL),
                        "Save"));
        defaultModule.save();

        Correspon actual = MockCorresponService.CRT_SAVE_PARTIAL;

        assertEquals(expCorrespon.toString(), actual.toString());

        assertEquals(expCorrespon.toString(), page.getCorrespon().toString());

        // prerender実行後の、Show Detailsの初期状態を確認
        page.prerender();
        assertEquals(CorresponPage.LABEL_HIDE_DETAILS, page.getTosDetail());
        assertEquals(CorresponPage.STYLE_SHOW, page.getTosDisplay());
        assertEquals(CorresponPage.LABEL_HIDE_DETAILS, page.getAttachmentsDetail());
        assertEquals(CorresponPage.STYLE_SHOW, page.getAttachmentsDisplay());
        assertEquals(CorresponPage.LABEL_HIDE_DETAILS, page.getCustomFieldsDetail());
        assertEquals(CorresponPage.STYLE_SHOW, page.getCustomFieldsDisplay());

        assertEquals("<p>1234<wbr/>5678<wbr/>90</p>", page.getDisplayBody());
    }

    /**
     * 応答履歴の表示アクションのテスト.
     * @throws Exception
     */
    @Test
    public void testShowResponseHistory() throws Exception {
        setRequiredFields();

        List<CorresponResponseHistory> crh = createCorresponResponseHistoryList(10L, 1);
        MockCorresponService.RET_CORRESPON_RESPONSE_HISTORY = crh;

        // 応答履歴取得実行
        defaultModule.showResponseHistory();

        // 実行時にPageに結果がセットさｓれる
        List<CorresponResponseHistory> actual = page.getCorresponResponseHistory();
        assertEquals(10L, actual.get(0).getCorrespon().getId().longValue());
    }

    /**
     * 改訂アクションのテスト.
     * @throws Exception
     */
    @Test
    public void testRevise() throws Exception {
        setRequiredFields();

        // テストに必要な値をセット
        Correspon expected = new Correspon();
        expected.setId(10L);
        expected.setSubject("mock");

        page.setId(expected.getId());
        page.setCorrespon(new Correspon());

        String actual = defaultModule.revise();

        String editMode = CorresponEditMode.REVISE.toQueryString();
        assertEquals(String.format("corresponEdit?id=10&%s&newEdit=1", editMode), actual);
    }

    /**
     * 複写登録アクションのテスト.
     * @throws Exception
     */
    @Test
    public void testCopy() throws Exception {
        setRequiredFields();

        // テストに必要な値をセット
        Correspon expected = new Correspon();
        expected.setId(10L);
        expected.setSubject("mock");

        page.setId(expected.getId());
        page.setCorrespon(new Correspon());

        page.setAttachment1Checked(true);
        page.setAttachment2Checked(false);
        page.setAttachment3Checked(true);
        page.setAttachment4Checked(false);
        page.setAttachment5Checked(true);

        String testCopy = defaultModule.copy();

        String editMode = CorresponEditMode.COPY.toQueryString();
        assertEquals(String.format("corresponEdit?id=10&%s&newEdit=1" + "&attachment1Transfer=true"
            + "&attachment2Transfer=false" + "&attachment3Transfer=true"
            + "&attachment4Transfer=false" + "&attachment5Transfer=true", editMode), testCopy);
    }

    /**
     * 転送アクションのテスト.
     * @throws Exception
     */
    @Test
    public void testForward() throws Exception {
        setRequiredFields();

        // テストに必要な値をセット
        Correspon expected = new Correspon();
        expected.setId(10L);
        expected.setSubject("mock");

        page.setId(expected.getId());
        page.setCorrespon(new Correspon());

        page.setAttachment1Checked(true);
        page.setAttachment2Checked(false);
        page.setAttachment3Checked(true);
        page.setAttachment4Checked(false);
        page.setAttachment5Checked(true);

        String testForward = defaultModule.forward();

        String editMode = CorresponEditMode.FORWARD.toQueryString();
        assertEquals(String.format("corresponEdit?id=10&%s&newEdit=1" + "&attachment1Transfer=true"
            + "&attachment2Transfer=false" + "&attachment3Transfer=true"
            + "&attachment4Transfer=false" + "&attachment5Transfer=true", editMode), testForward);
    }

    /**
     * 返信アクションのテスト.
     * @throws Exception
     */
    @Test
    public void testReply() throws Exception {
        setRequiredFields();

        // 初めに削除しておく
        Flash flash = new Flash();
        flash.setValue("backPage", null);

        page.setBackPage(null);
        defaultModule.reply();
        assertNull(flash.getValue("backPage"));

        page.setBackPage("corresponSearch");
        defaultModule.reply();
        assertEquals("corresponSearch", flash.getValue("backPage"));
    }

    /**
     * コピー返信アクションのテスト.
     * @throws Exception
     */
    @Test
    public void testReplyWithPreviousCorrespon() throws Exception {
        setRequiredFields();

        // 初めに削除しておく
        Flash flash = new Flash();
        flash.setValue("backPage", null);

        page.setBackPage(null);
        defaultModule.replyWithPreviousCorrespon();
        assertNull(flash.getValue("backPage"));

        page.setBackPage("corresponSearch");
        defaultModule.replyWithPreviousCorrespon();
        assertEquals("corresponSearch", flash.getValue("backPage"));
    }

    /**
     * 更新アクションのテスト.
     * @throws Exception
     */
    @Test
    public void testUpdate() throws Exception {
        setRequiredFields();

        // テストに必要な値をセット
        Correspon expected = new Correspon();
        expected.setId(10L);
        expected.setSubject("mock");

        // 初めに削除しておく
        Flash flash = new Flash();
        flash.setValue("backPage", null);

        page.setBackPage(null);
        page.setId(expected.getId());
        page.setCorrespon(new Correspon());

        String tesUpdate = defaultModule.update();

        String editMode = CorresponEditMode.UPDATE.toQueryString();
        assertEquals(String.format("corresponEdit?id=10&%s", editMode), tesUpdate);
        assertNull(flash.getValue("backPage"));

        page.setBackPage("corresponSearch");
        defaultModule.update();
        assertEquals("corresponSearch", flash.getValue("backPage"));
    }

    /**
     * 一覧へ戻るアクションのテスト.
     * @throws Exception
     */
    @Test
    public void testBack() throws Exception {
        setRequiredFields();

        // 戻り先の指定ありならその戻り先へ
        page.setBackPage("corresponSearch");
        String testBack = defaultModule.back();
        assertEquals("corresponSearch?sessionSort=1&sessionPageNo=1", testBack);

        // 戻り先の指定なしならデフォルトの戻り先の
        page.setBackPage(null);
        testBack = defaultModule.back();
        assertEquals("corresponIndex?sessionSort=1&sessionPageNo=1", testBack);
    }

    /**
     * ZIP出力アクションのテスト.
     * @throws Exception
     */
    @Test
    public void testDownloadZip() throws Exception {
        setRequiredFields();

        // 変換後のデータを作成
        byte[] expected = "TEST-DOWNLOAD-ZIP".getBytes();
        MockCorresponService.RET_ZIP = expected;

        String expFileName = "20100414";
        MockAbstractPage.RET_FILE_NAME = expFileName;

        Correspon c = new Correspon();
        c.setId(1L) ;
        page.setCorrespon(c);

        defaultModule.downloadZip();

        assertEquals("download", MockViewHelper.RET_ACTION);
        assertArrayEquals(expected, MockViewHelper.RET_DATA);
        assertEquals(expFileName + ".zip", MockViewHelper.RET_FILENAME);
        assertEquals(c, MockCorresponService.SET_ZIP);
    }

    /**
     * ZIP出力アクションのテスト.
     * @throws Exception
     */
    @Test
    public void testDownloadZipIOException() throws Exception {
        setRequiredFields();

        // 変換後のデータを作成
        byte[] expected = "TEST-DOWNLOAD-ZIP".getBytes();
        MockCorresponService.RET_ZIP = expected;

        String expFileName = "20100414";
        MockAbstractPage.RET_FILE_NAME = expFileName;

        Correspon c = new Correspon();
        c.setId(1L) ;
        page.setCorrespon(c);

        MockViewHelper.EX_DOWNLOAD = new IOException();

        // 期待されるメッセージをセット
        FacesContextMock.EXPECTED_CLIENT_ID = null;
        FacesContextMock.EXPECTED_MESSAGE =
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                 FacesMessage.SEVERITY_ERROR.toString(),
                                 createExpectedMessageString(
                                     Messages.getMessageAsString(E_DOWNLOAD_FAILED),
                                     null));
        defaultModule.downloadZip();
    }

    /**
     * コレポン一覧上での前アクションのテスト
     * @throws Exception
     */
    @Test
    public void testMovePrevious() throws Exception {
        setRequiredFields();

        User retUser = new User();
        retUser.setEmpNo("USER004");
        MockAbstractPage.RET_USER = retUser;
        MockCorresponReadStatusService.RET_CURRENT_USER = retUser;

        MockAbstractPage.RET_SYSTEM_ADMIN = false;
        MockAbstractPage.RET_PROJECT_ADMIN = true;
        MockAbstractPage.RET_GROUP_ADMIN = false;

        User user = new User();
        user.setEmpNo("USER004");
        MockAbstractPage.RET_USER = user;
        MockCorresponReadStatusService.RET_CURRENT_USER = user;

        Project project = new Project();
        project.setProjectId("PJ1");
        MockAbstractPage.RET_PROJECT = project;

        // Serviceが返すダミーのコレポン文書を設定
        Correspon expCorrespon = createCorrespon(10L);
        List<Workflow> workflowList = createWorkflowList();

        expCorrespon.setWorkflows(workflowList);
        expCorrespon.setWorkflowStatus(WorkflowStatus.DENIED);
        expCorrespon.setBody("<p>1234567890</p>");

        MockCorresponService.RET_FIND = expCorrespon;
        MockCorresponGroupService.RET_FIND_CORRESPON_GROUP_USER_MAPPINGS =
                new ArrayList<CorresponGroupUserMapping>();
        MockCorresponGroupService.RET_SEARCH = new ArrayList<CorresponGroup>();
        MockUserService.RET_SEARCH = new ArrayList<ProjectUser>();

        //モックのセット
        MockCorresponSearchService.RET_SEARCH_ID = new ArrayList<Long>();
        MockCorresponFullTextSearchService.RET_SEARCH_ID = new ArrayList<Long>();

        // 必須のプロパティをセット
        page.setId(expCorrespon.getId());
        defaultModule.initialize();

        List<Long> corresponIdList = new ArrayList<Long>();
        corresponIdList.add(100L);
        corresponIdList.add(200L);

        page.setId(corresponIdList.get(1));
        page.setDisplayIndex(1);
        page.setCorresponIds(corresponIdList);

        assertNull(defaultModule.movePrevious());
        assertEquals(page.getId(), corresponIdList.get(0));
        assertEquals(0, page.getDisplayIndex());
    }

    /**
     * コレポン一覧上でのNextアクションのテスト
     * @throws Exception
     */
    @Test
    public void testMoveNext() throws Exception {
        setRequiredFields();

        User retUser = new User();
        retUser.setEmpNo("USER004");
        MockAbstractPage.RET_USER = retUser;
        MockCorresponReadStatusService.RET_CURRENT_USER = retUser;

        MockAbstractPage.RET_SYSTEM_ADMIN = false;
        MockAbstractPage.RET_PROJECT_ADMIN = true;
        MockAbstractPage.RET_GROUP_ADMIN = false;

        User user = new User();
        user.setEmpNo("USER004");
        MockAbstractPage.RET_USER = user;
        MockCorresponReadStatusService.RET_CURRENT_USER = user;

        Project project = new Project();
        project.setProjectId("PJ1");
        MockAbstractPage.RET_PROJECT = project;

        // Serviceが返すダミーのコレポン文書を設定
        Correspon expCorrespon = createCorrespon(10L);
        List<Workflow> workflowList = createWorkflowList();

        expCorrespon.setWorkflows(workflowList);
        expCorrespon.setWorkflowStatus(WorkflowStatus.DENIED);
        expCorrespon.setBody("<p>1234567890</p>");

        MockCorresponService.RET_FIND = expCorrespon;
        MockCorresponGroupService.RET_FIND_CORRESPON_GROUP_USER_MAPPINGS =
                new ArrayList<CorresponGroupUserMapping>();
        MockCorresponGroupService.RET_SEARCH = new ArrayList<CorresponGroup>();
        MockUserService.RET_SEARCH = new ArrayList<ProjectUser>();

        //モックのセット
        MockCorresponSearchService.RET_SEARCH_ID = new ArrayList<Long>();
        MockCorresponFullTextSearchService.RET_SEARCH_ID = new ArrayList<Long>();

        // 必須のプロパティをセット
        page.setId(expCorrespon.getId());
        defaultModule.initialize();

        List<Long> corresponIdList = new ArrayList<Long>();
        corresponIdList.add(100L);
        corresponIdList.add(200L);

        page.setId(corresponIdList.get(0));
        page.setDisplayIndex(0);
        page.setCorresponIds(corresponIdList);

        assertNull(defaultModule.moveNext());
        assertEquals(page.getId(), corresponIdList.get(1));
        assertEquals(1, page.getDisplayIndex());
    }

    private void setRequiredFields() {
        defaultModule.setCorresponPage(page);
        defaultModule.setServiceActionHandler(page.getHandler());
        defaultModule.setViewHelper(page.getViewHelper());
    }

    /**
     * テスト用コレポン文書の作成
     */
    private Correspon createCorrespon(Long id) {
        Correspon c = new Correspon();
        c.setId(id);
        c.setCorresponNo("YOC:OT:BUILDING-00001");
        CorresponGroup from = new CorresponGroup();
        from.setId(new Long(1L));
        from.setName("YOC:BUILDING");
        c.setFromCorresponGroup(from);
        c.setSubject("Mock");
        CorresponType ct = new CorresponType();
        ct.setId(new Long(1L));
        ct.setName("Request");
        WorkflowPattern wp = new WorkflowPattern();
        wp.setId(1L);
        wp.setWorkflowCd("001");
        ct.setWorkflowPattern(wp);
        c.setCorresponType(ct);
        c.setWorkflowStatus(WorkflowStatus.DRAFT);
        c.setCreatedAt(new GregorianCalendar(2009, 3, 1, 1, 1, 1).getTime());
        c.setUpdatedAt(new GregorianCalendar(2009, 3, 10, 10, 10, 10).getTime());
        User u = new User();
        u.setEmpNo("00001");
        u.setNameE("Test User");
        c.setCreatedBy(u);
        c.setUpdatedBy(u);
        c.setDeadlineForReply(new GregorianCalendar(2009, 4, 1).getTime());
        c.setCorresponStatus(CorresponStatus.OPEN);
        return c;
    }

    /**
     * テスト用ワークフローリストの作成
     */
    private List<Workflow> createWorkflowList() {
        List<Workflow> list = new ArrayList<Workflow>();

        Workflow wf = new Workflow();
        wf.setId(1L);
        wf.setUser(CHECKER_1);
        wf.setWorkflowType(WorkflowType.CHECKER);
        list.add(wf);

        wf = new Workflow();
        wf.setId(2L);
        wf.setUser(CHECKER_2);
        wf.setWorkflowType(WorkflowType.CHECKER);
        list.add(wf);

        wf = new Workflow();
        wf.setId(3L);
        wf.setUser(CHECKER_3);
        wf.setWorkflowType(WorkflowType.CHECKER);
        list.add(wf);

        wf = new Workflow();
        wf.setId(4L);
        wf.setUser(APPROVER);
        wf.setWorkflowType(WorkflowType.APPROVER);
        list.add(wf);
        return list;
    }

    /**
     * テスト用応答履歴リストの作成
     */
    private List<CorresponResponseHistory> createCorresponResponseHistoryList(Long start, int len) {
        List<CorresponResponseHistory> crh = new ArrayList<CorresponResponseHistory>();
        for (int i = 0 ; i < len ; i++ ) {
            crh.add(createCorresponResponseHistory(start++));
        }
        return crh;
    }

    /**
     * テスト用応答履歴の作成
     */
    private CorresponResponseHistory createCorresponResponseHistory(Long id) {
        CorresponResponseHistory history = new CorresponResponseHistory();
        Correspon correspon = new Correspon();
        correspon.setId(id);
        history.setCorrespon(correspon);
        return history;
    }

    private List<AddressCorresponGroup> createAddressCorresponGroups() {
        List<AddressCorresponGroup> groups = new ArrayList<AddressCorresponGroup>();
        groups.add(createAddressCorresponGroup(1L, AddressType.TO));
        groups.add(createAddressCorresponGroup(2L, AddressType.TO));
        groups.add(createAddressCorresponGroup(3L, AddressType.CC));
        return groups;
    }

    private AddressCorresponGroup createAddressCorresponGroup(Long id, AddressType type) {
        AddressCorresponGroup group = new AddressCorresponGroup();
        group.setId(id);
        group.setAddressType(type);
        return group;
    }

    private List<ProjectUser> createProjectUsers() {
        List<ProjectUser> users = new ArrayList<ProjectUser>();
        users.add(createProjectUser("USER001"));
        users.add(createProjectUser("USER002"));
        users.add(createProjectUser("USER003"));
        users.add(createProjectUser("USER004"));
        return users;
    }

    private ProjectUser createProjectUser(String empNo) {
        ProjectUser projectUser = new ProjectUser();
        projectUser.setProjectId("0-1111-2");
        User user = new User();
        user.setEmpNo(empNo);
        projectUser.setUser(user);
        return projectUser;
    }

    public static class MockAbstractPage extends MockUp<AbstractPage> {
        static User RET_USER;
        static Project RET_PROJECT;
        static boolean RET_SYSTEM_ADMIN;
        static boolean RET_PROJECT_ADMIN;
        static boolean RET_GROUP_ADMIN;
        static String RET_FILE_NAME;

        @Mock
        public User getCurrentUser() {
            return RET_USER;
        }

        @Mock
        public Project getCurrentProject() {
            System.out.println("getCurrentProject():" + RET_PROJECT);
            return RET_PROJECT;
        }

        @Mock
        public boolean isSystemAdmin() {
            return RET_SYSTEM_ADMIN;
        }

        @Mock
        public boolean isProjectAdmin(String projectId) {
            ArgumentValidator.validateNotEmpty(projectId);

            return RET_PROJECT_ADMIN;
        }

        @Mock
        public boolean isAnyGroupAdmin(Correspon c) {
            ArgumentValidator.validateNotNull(c);

            return RET_GROUP_ADMIN;
        }

        @Mock
        public String createFileName() {
            return RET_FILE_NAME;
        }
    }

    public static class MockCorresponPage extends MockUp<CorresponPage> {
        static List<ProjectUser> RET_USERS;

        @Mock
        public List<ProjectUser> getUsers() {
            return RET_USERS;
        }
    }

    public static class MockCorresponService extends MockUp<CorresponServiceImpl> {
        static Correspon RET_FIND;
        static String RET_REQUESTFORAPPROVAL;
        static String RET_UPDATE;
        static String RET_DELETE;
        static String RET_ISSUE;
        static byte[] RET_HTML;
        static Correspon SET_ZIP;
        static byte[] RET_ZIP;
        static Correspon SET_CORRESPON;
        static ServiceAbortException EX_VALIDATE;
        static Correspon CRT_SAVE_PARTIAL;
        static List<Correspon> RET_REPLY_CORRESPONS;
        static List<CorresponResponseHistory> RET_CORRESPON_RESPONSE_HISTORY;
        static ServiceAbortException EXP_FIND;

        @Mock
        public Correspon find(Long id) throws ServiceAbortException {
            if (EXP_FIND != null) {
                throw EXP_FIND;
            }
            return RET_FIND;
        }

        @Mock
        public void requestForApproval(Correspon correspon) throws ServiceAbortException {
            RET_REQUESTFORAPPROVAL = "文書は検証依頼されました。";
        }

        @Mock
        public byte[] generateHTML(Correspon correspon,
            List<CorresponResponseHistoryModel> corresponResponseHistory,
            boolean usePersonInCharge) throws ServiceAbortException {
            SET_CORRESPON = correspon;
            return RET_HTML;
        }

        @Mock
        public byte[] generateZip(Correspon correspon,
                boolean usePersonInCharge) throws ServiceAbortException {
            SET_ZIP = correspon;
            return RET_ZIP;
        }

        @Mock
        public void delete(Correspon correspon) throws ServiceAbortException {
            if (EX_VALIDATE != null) {
                throw EX_VALIDATE;
            }
            RET_DELETE = "文書は削除されました。";
        }

        @Mock
        public void issue(Correspon correspon) throws ServiceAbortException {
            if (EX_VALIDATE != null) {
                throw EX_VALIDATE;
            }
            RET_ISSUE = "文書は発行されました。";
        }

        @Mock
        public List<Correspon> findReplyCorrespons(Correspon correspon, Long id) {
            return RET_REPLY_CORRESPONS;
        }

        @Mock
        public void savePartial(Correspon correspon) throws ServiceAbortException {
            CRT_SAVE_PARTIAL = correspon;
        }

        @Mock
        public List<CorresponResponseHistory> findCorresponResponseHistory(Correspon correspon)
            throws ServiceAbortException {
            return RET_CORRESPON_RESPONSE_HISTORY;
        }
    }

    public static class MockViewHelper extends MockUp<ViewHelper> {
        static String RET_ACTION;
        static byte[] RET_DATA;
        static String RET_FILENAME;
        static byte[] SET_DATA;
        static IOException EX_RESPONSE;
        static Map<String, Object> map = new HashMap<String, Object>();
        static IOException EX_DOWNLOAD;

        @Mock
        public void download(String fileName, byte[] content) throws IOException {
            if (EX_DOWNLOAD != null) {
                throw EX_DOWNLOAD;
            }
            RET_ACTION = "download";
            RET_DATA = content;
            RET_FILENAME = fileName;
        }

        @Mock
        public void requestResponse(byte[] content, String charset) throws IOException {
            if (EX_RESPONSE != null) {
                throw EX_RESPONSE;
            }
            SET_DATA = content;
        }

        @Mock
        @SuppressWarnings("unchecked")
        public <T> T getSessionValue(String key) {
            return (T) map.get(key);
        }

        @Mock
        public void setSessionValue(String key, Object value) {
            map.put(key, value);
        }

        @Mock
        public void removeSessionValue(String key) {
            map.remove(key);
        }
    }

    public static class MockCorresponReadStatusService extends MockUp<CorresponReadStatusServiceImpl> {
        static Integer RET_FIND = 2;
        static Long RET_FIND_UNIT = 2L;
        static List<CorresponReadStatus> SET_CREATE = new ArrayList<CorresponReadStatus>();
        static List<CorresponReadStatus> SET_UPDATE = new ArrayList<CorresponReadStatus>();
        static CorresponReadStatus SET_READ_CREATE = new CorresponReadStatus();
        static User RET_CURRENT_USER;
        static ServiceAbortException EX_UPDATE;

        @Mock
        public Long updateReadStatusById(Long id, ReadStatus readStatus)
            throws ServiceAbortException {
            if (EX_UPDATE != null) {
                throw EX_UPDATE;
            }
            SET_READ_CREATE = new CorresponReadStatus();
            SET_READ_CREATE.setCorresponId(id);

            SET_READ_CREATE.setEmpNo(RET_CURRENT_USER.getEmpNo());
            SET_READ_CREATE.setReadStatus(readStatus);
            if (SET_READ_CREATE.getId() == null) {
                SET_READ_CREATE.setCreatedBy(RET_CURRENT_USER);
                SET_READ_CREATE.setUpdatedBy(RET_CURRENT_USER);
                SET_CREATE.add(SET_READ_CREATE);
            } else {
                SET_READ_CREATE.setUpdatedBy(RET_CURRENT_USER);
                SET_UPDATE.add(SET_READ_CREATE);
            }
            return 1L;
        }

        @Mock
        public Integer updateReadStatusByCorresponId(Long id, ReadStatus readStatus)
            throws ServiceAbortException {
            if (EX_UPDATE != null) {
                throw EX_UPDATE;
            }
            SET_READ_CREATE = new CorresponReadStatus();
            SET_READ_CREATE.setCorresponId(id);

            SET_READ_CREATE.setEmpNo(RET_CURRENT_USER.getEmpNo());
            SET_READ_CREATE.setReadStatus(readStatus);
            if (SET_READ_CREATE.getId() == null) {
                SET_READ_CREATE.setCreatedBy(RET_CURRENT_USER);
                SET_READ_CREATE.setUpdatedBy(RET_CURRENT_USER);
                SET_CREATE.add(SET_READ_CREATE);
            } else {
                SET_READ_CREATE.setUpdatedBy(RET_CURRENT_USER);
                SET_UPDATE.add(SET_READ_CREATE);
            }
            return 1;
        }
    }

    public static class MockCorresponGroupService extends MockUp<CorresponGroupServiceImpl> {
        static List<CorresponGroup> RET_SEARCH;
        static List<CorresponGroupUserMapping> RET_FIND_CORRESPON_GROUP_USER_MAPPINGS;

        @Mock
        public List<CorresponGroup> search(SearchCorresponGroupCondition condition)
            throws ServiceAbortException {
            return RET_SEARCH;
        }

        @Mock
        public List<CorresponGroupUserMapping> findCorresponGroupUserMappings() {
            return RET_FIND_CORRESPON_GROUP_USER_MAPPINGS;
        }
    }

    public static class MockUserService extends MockUp<UserServiceImpl> {
        static List<ProjectUser> RET_SEARCH;
        @Mock
        public List<ProjectUser> search(SearchUserCondition condition) throws ServiceAbortException {
            return RET_SEARCH;
        }
    }

    public static class MockCorresponSearchService extends MockUp<CorresponSearchServiceImpl> {
        static List<Long> RET_SEARCH_ID;

        @Mock
        List<Long> searchId(SearchCorresponCondition condition) throws ServiceAbortException {
            return RET_SEARCH_ID;
        }
    }

    public static class MockCorresponFullTextSearchService extends MockUp<CorresponFullTextSearchServiceImpl>{
        static List<Long> RET_SEARCH_ID;

        @Mock
        List<Long> searchId(SearchFullTextSearchCorresponCondition condition)
            throws ServiceAbortException{
            return RET_SEARCH_ID;
        }
    }
}
