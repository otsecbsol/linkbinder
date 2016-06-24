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
package jp.co.opentone.bsol.linkbinder.view.common;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.faces.application.FacesMessage;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import jp.co.opentone.bsol.framework.core.config.SystemConfig;
import jp.co.opentone.bsol.framework.core.exception.InvalidOperationRuntimeException;
import jp.co.opentone.bsol.framework.core.message.MessageCode;
import jp.co.opentone.bsol.framework.core.message.Messages;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.extension.jsf.FacesContextMock;
import jp.co.opentone.bsol.framework.test.AbstractTestCase;
import jp.co.opentone.bsol.linkbinder.Constants;
import jp.co.opentone.bsol.linkbinder.dto.Company;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroup;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroupSummary;
import jp.co.opentone.bsol.linkbinder.dto.CorresponType;
import jp.co.opentone.bsol.linkbinder.dto.CorresponUserSummary;
import jp.co.opentone.bsol.linkbinder.dto.FavoriteFilter;
import jp.co.opentone.bsol.linkbinder.dto.ProjectDetailsSummary;
import jp.co.opentone.bsol.linkbinder.dto.ProjectUser;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.code.CorresponStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.ReadStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowProcessStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowStatus;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponCondition;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchFavoriteFilterCondition;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.service.common.impl.FavoriteFilterServiceImpl;
import jp.co.opentone.bsol.linkbinder.service.common.impl.HomeServiceImpl;
import jp.co.opentone.bsol.linkbinder.view.AbstractPage;
import mockit.Mock;
import mockit.MockUp;

/**
 * プロジェクトホーム画面のテストクラス.
 * @author opentone
 */
public class ProjectHomePageTest extends AbstractTestCase {

    /**
     * テスト対象.
     */
    @Resource
    private ProjectHomePage page;

    /**
     * テストの前準備.
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        FacesContextMock.initialize();
        new MockAbstractPage();
        new MockHomeService();
        new MockFavoriteFilterService();
    }

    /**
     * テストの後始末.
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        new MockAbstractPage().tearDown();
        new MockHomeService().tearDown();
        new MockFavoriteFilterService().tearDown();

        FacesContextMock.tearDown();
    }

    /**
     * テスト実行後.
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
        MockHomeService.RET_FIND_PROJECT_DETAILS = null;
        MockAbstractPage.RET_CURRENT_PROJECT_ID = null;
        MockAbstractPage.RET_SYSTEM_ADMIN = false;
        MockAbstractPage.RET_PROJECT_ADMIN = false;
        MockAbstractPage.RET_USER = null;
        MockAbstractPage.SET_CONDITION = null;
        MockAbstractPage.IS_USE_PERSON_IN_CHARGE = false;

        page.setSelectedCorresponStatus(null);
        page.setSelectedWorkflowProcessStatus(null);
        page.setSelectedCorresponStatus(null);
        page.setSelectedAttention(false);
        page.setSelectedPIC(false);
        page.setSelectedTo(false);
        page.setSelectedCc(false);
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.view.common.ProjectHomePage#initialize()} のためのテスト・メソッド.
     */
    @Test
    public void testInitialize() {
        ProjectDetailsSummary summary = new ProjectDetailsSummary();
        summary.setCorresponGroupSummary(createCorresponGroupSummaryList());
        summary.setUserRelatedCorresponGroups(createUserCorresponGroups());
        summary.setCorresponUserSummary(createCorresponUserSummary());
        summary.setCompanies(createCompanyList());
        summary.setCorresponGroups(createCorresponGroupList());
        summary.setProjectAdmins(createProjectAdminList());
        summary.setGroupAdmins(createGroupAdminList());
        summary.setNormalUsers(createNormalUserList());
        MockHomeService.RET_FIND_PROJECT_DETAILS = summary;

        MockAbstractPage.RET_CURRENT_PROJECT_ID = "PJ1";
        MockAbstractPage.IS_USE_PERSON_IN_CHARGE = true;

        page.initialize();

        assertEquals(summary, page.getProjectDetailsSummary());
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.view.common.ProjectHomePage#initialize()} のためのテスト・メソッド.
     * favoriteFilterService の処理が実行されていることを確認する。
     */
    @Test
    public void testInitializeForFavoriteFilter() {
        // prepare
        FacesContextMock.initialize();
        MockAbstractPage.RET_CURRENT_PROJECT_ID = "PJ1";
        MockAbstractPage.IS_USE_PERSON_IN_CHARGE = true;
        MockFavoriteFilterService.init();

        // execute
        page.initialize();
        DataModel<FavoriteFilter> actual = page.getFavoriteFilterDataModel();

        // verify
        assertNotNull("datamodel is null.", page.getFavoriteFilterDataModel());
        ListDataModel<FavoriteFilter> expected
            = new ListDataModel<FavoriteFilter>(MockFavoriteFilterService.DUMMY_LIST);
        assertEquals("dataModel rowCount.", expected.getRowCount(), actual.getRowCount());
        assertEquals("row data.", expected.getRowData().getId(), actual.getRowData().getId());
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.view.common.ProjectHomePage#initialize()} のためのテスト・メソッド.
     */
    @Test
    public void testInitializeNull() {
        // 期待されるメッセージをセット
        FacesContextMock.EXPECTED_MESSAGE =
            new FacesMessage(FacesMessage.SEVERITY_ERROR,
                             FacesMessage.SEVERITY_ERROR.toString(),
                             createExpectedMessageString(
                                 Messages.getMessageAsString(MessageCode.E_INVALID_PARAMETER),
                                 null));
        try {
            page.initialize();
            fail("例外が発生していない");
        } catch (InvalidOperationRuntimeException e) {
            ServiceAbortException actual = (ServiceAbortException)e.getCause();
            assertEquals(ApplicationMessageCode.E_INVALID_PARAMETER, actual.getMessageCode());
        }
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.view.common.ProjectHomePage#goCorresponIndexTracking()} のためのテスト・メソッド.
     */
    @Test
    public void testGoCorresponIndexTracking() {
        String projectId = "PJ1";
        User user = new User();
        user.setEmpNo("00001");
        boolean systemAdmin = true;
        boolean projectAdmin = false;

        MockAbstractPage.RET_CURRENT_PROJECT_ID = projectId;
        MockAbstractPage.RET_USER = user;
        MockAbstractPage.RET_SYSTEM_ADMIN = systemAdmin;
        MockAbstractPage.RET_PROJECT_ADMIN = projectAdmin;

        ProjectDetailsSummary summary = new ProjectDetailsSummary();
        summary.setUserRelatedCorresponGroups(createUserCorresponGroups());
        page.setProjectDetailsSummary(summary);

        List<CorresponGroupSummary> groupSummaryList = createCorresponGroupSummaryList();
        DataModel dataModel = new ListDataModel(groupSummaryList);
        dataModel.setRowIndex(1); // 2行目
        page.setGroupSummaryDataModel(dataModel);

        page.setSelectedTo(false);
        page.setSelectedCc(false);

        String expected = "correspon/corresponIndex?projectId=PJ1";

        // 暗黙の条件として、発行状態であること
        WorkflowStatus[] expWorkflowStatuses = {WorkflowStatus.ISSUED};
        CorresponType[] expCorresponTypes = {groupSummaryList.get(1).getCorresponType()}; // 2行目

        String actual = page.goCorresponIndexTracking();

        assertEquals(expected, actual);

        SearchCorresponCondition actCondition = MockAbstractPage.SET_CONDITION;
        assertEquals(projectId, actCondition.getProjectId());
        assertEquals(user.getEmpNo(), actCondition.getUserId());
        assertEquals(systemAdmin, actCondition.isSystemAdmin());
        assertEquals(projectAdmin, actCondition.isProjectAdmin());

        assertArrayEquals(expWorkflowStatuses, actCondition.getWorkflowStatuses());
        assertArrayEquals(expCorresponTypes, actCondition.getCorresponTypes());
//        assertArrayEquals(summary.getUserRelatedCorresponGroups(), actCondition.getCorresponGroups());
        assertArrayEquals(summary.getUserRelatedCorresponGroups(), actCondition.getToGroups());
        assertFalse(actCondition.isGroupTo());
        assertFalse(actCondition.isGroupCc());
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.view.common.ProjectHomePage#goCorresponIndexTracking()} のためのテスト・メソッド.
     * 宛先選択：TO.
     */
    @Test
    public void testGoCorresponIndexTrackingTo() {
        String projectId = "PJ1";
        User user = new User();
        user.setEmpNo("00001");
        boolean systemAdmin = true;
        boolean projectAdmin = false;

        MockAbstractPage.RET_CURRENT_PROJECT_ID = projectId;
        MockAbstractPage.RET_USER = user;
        MockAbstractPage.RET_SYSTEM_ADMIN = systemAdmin;
        MockAbstractPage.RET_PROJECT_ADMIN = projectAdmin;

        ProjectDetailsSummary summary = new ProjectDetailsSummary();
        summary.setUserRelatedCorresponGroups(createUserCorresponGroups());
        page.setProjectDetailsSummary(summary);

        List<CorresponGroupSummary> groupSummaryList = createCorresponGroupSummaryList();
        DataModel dataModel = new ListDataModel(groupSummaryList);
        dataModel.setRowIndex(1); // 2行目
        page.setGroupSummaryDataModel(dataModel);

        page.setSelectedTo(true);
        page.setSelectedCc(false);

        String expected = "correspon/corresponIndex?projectId=PJ1";

        // 暗黙の条件として、発行状態であること
        WorkflowStatus[] expWorkflowStatuses = {WorkflowStatus.ISSUED};
        CorresponType[] expCorresponTypes = {groupSummaryList.get(1).getCorresponType()}; // 2行目

        String actual = page.goCorresponIndexTracking();

        assertEquals(expected, actual);

        SearchCorresponCondition actCondition = MockAbstractPage.SET_CONDITION;
        assertEquals(projectId, actCondition.getProjectId());
        assertEquals(user.getEmpNo(), actCondition.getUserId());
        assertEquals(systemAdmin, actCondition.isSystemAdmin());
        assertEquals(projectAdmin, actCondition.isProjectAdmin());

        assertArrayEquals(expWorkflowStatuses, actCondition.getWorkflowStatuses());
        assertArrayEquals(expCorresponTypes, actCondition.getCorresponTypes());
//        assertArrayEquals(summary.getUserRelatedCorresponGroups(), actCondition.getCorresponGroups());
        assertArrayEquals(summary.getUserRelatedCorresponGroups(), actCondition.getToGroups());
        assertTrue(actCondition.isGroupTo());
        assertFalse(actCondition.isGroupCc());
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.view.common.ProjectHomePage#goCorresponIndexTracking()} のためのテスト・メソッド.
     * 宛先選択：CC.
     */
    @Test
    public void testGoCorresponIndexTrackingCc() {
        String projectId = "PJ1";
        User user = new User();
        user.setEmpNo("00001");
        boolean systemAdmin = true;
        boolean projectAdmin = false;

        MockAbstractPage.RET_CURRENT_PROJECT_ID = projectId;
        MockAbstractPage.RET_USER = user;
        MockAbstractPage.RET_SYSTEM_ADMIN = systemAdmin;
        MockAbstractPage.RET_PROJECT_ADMIN = projectAdmin;

        ProjectDetailsSummary summary = new ProjectDetailsSummary();
        summary.setUserRelatedCorresponGroups(createUserCorresponGroups());
        page.setProjectDetailsSummary(summary);

        List<CorresponGroupSummary> groupSummaryList = createCorresponGroupSummaryList();
        DataModel dataModel = new ListDataModel(groupSummaryList);
        dataModel.setRowIndex(1); // 2行目
        page.setGroupSummaryDataModel(dataModel);

        page.setSelectedTo(false);
        page.setSelectedCc(true);

        String expected = "correspon/corresponIndex?projectId=PJ1";

        // 暗黙の条件として、発行状態であること
        WorkflowStatus[] expWorkflowStatuses = {WorkflowStatus.ISSUED};
        CorresponType[] expCorresponTypes = {groupSummaryList.get(1).getCorresponType()}; // 2行目

        String actual = page.goCorresponIndexTracking();

        assertEquals(expected, actual);

        SearchCorresponCondition actCondition = MockAbstractPage.SET_CONDITION;
        assertEquals(projectId, actCondition.getProjectId());
        assertEquals(user.getEmpNo(), actCondition.getUserId());
        assertEquals(systemAdmin, actCondition.isSystemAdmin());
        assertEquals(projectAdmin, actCondition.isProjectAdmin());

        assertArrayEquals(expWorkflowStatuses, actCondition.getWorkflowStatuses());
        assertArrayEquals(expCorresponTypes, actCondition.getCorresponTypes());
//        assertArrayEquals(summary.getUserRelatedCorresponGroups(), actCondition.getCorresponGroups());
        assertArrayEquals(summary.getUserRelatedCorresponGroups(), actCondition.getToGroups());
        assertFalse(actCondition.isGroupTo());
        assertTrue(actCondition.isGroupCc());
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.view.common.ProjectHomePage#goCorresponIndexTrackingAll()} のためのテスト・メソッド.
     */
    @Test
    public void testGoCorresponIndexTrackingAll() {
        String projectId = "PJ1";
        User user = new User();
        user.setEmpNo("00001");
        boolean systemAdmin = false;
        boolean projectAdmin = true;

        MockAbstractPage.RET_CURRENT_PROJECT_ID = projectId;
        MockAbstractPage.RET_USER = user;
        MockAbstractPage.RET_SYSTEM_ADMIN = systemAdmin;
        MockAbstractPage.RET_PROJECT_ADMIN = projectAdmin;

        String expected = "correspon/corresponIndex?projectId=PJ1";

        // 暗黙の条件として、発行状態であること
        WorkflowStatus[] expWorkflowStatuses = {WorkflowStatus.ISSUED};

        String actual = page.goCorresponIndexTrackingAll();

        assertEquals(expected, actual);

        SearchCorresponCondition actCondition = MockAbstractPage.SET_CONDITION;
        assertEquals(projectId, actCondition.getProjectId());
        assertEquals(user.getEmpNo(), actCondition.getUserId());
        assertEquals(systemAdmin, actCondition.isSystemAdmin());
        assertEquals(projectAdmin, actCondition.isProjectAdmin());

        assertArrayEquals(expWorkflowStatuses, actCondition.getWorkflowStatuses());
        assertEquals(0, actCondition.getCorresponTypes().length);
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.view.common.ProjectHomePage#goCorresponIndexWorkflowStatus()} のためのテスト・メソッド.
     * 文書状態選択なし.
     */
    @Test
    public void testGoCorresponIndexWorkflowStatus() {
        String projectId = "PJ1";
        User user = new User();
        user.setEmpNo("00001");
        boolean systemAdmin = true;
        boolean projectAdmin = false;

        MockAbstractPage.RET_CURRENT_PROJECT_ID = projectId;
        MockAbstractPage.RET_USER = user;
        MockAbstractPage.RET_SYSTEM_ADMIN = systemAdmin;
        MockAbstractPage.RET_PROJECT_ADMIN = projectAdmin;

        page.setSelectedWorkflowStatus(WorkflowStatus.DRAFT.name());

        String expected = "correspon/corresponIndex?projectId=PJ1";

        WorkflowStatus[] expWorkflowStatuses = {WorkflowStatus.DRAFT};
        // 自分が作成した文書であること.
        User[] expUsers = {user};

        String actual = page.goCorresponIndexWorkflowStatus();

        assertEquals(expected, actual);

        SearchCorresponCondition actCondition = MockAbstractPage.SET_CONDITION;
        assertEquals(projectId, actCondition.getProjectId());
        assertEquals(user.getEmpNo(), actCondition.getUserId());
        assertEquals(systemAdmin, actCondition.isSystemAdmin());
        assertEquals(projectAdmin, actCondition.isProjectAdmin());

        assertArrayEquals(expWorkflowStatuses, actCondition.getWorkflowStatuses());
//        assertArrayEquals(expUsers, actCondition.getAddressUsers());
        assertArrayEquals(expUsers, actCondition.getFromUsers());
        assertTrue(actCondition.isUserPreparer());
//        assertTrue(actCondition.isAddressFrom());
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.view.common.ProjectHomePage#goCorresponIndexWorkflowStatus()} のためのテスト・メソッド.
     * 文書状態選択あり.
     */
    @Test
    public void testGoCorresponIndexWorkflowStatusOpen() {
        String projectId = "PJ1";
        User user = new User();
        user.setEmpNo("00001");
        boolean systemAdmin = false;
        boolean projectAdmin = false;

        MockAbstractPage.RET_CURRENT_PROJECT_ID = projectId;
        MockAbstractPage.RET_USER = user;
        MockAbstractPage.RET_SYSTEM_ADMIN = systemAdmin;
        MockAbstractPage.RET_PROJECT_ADMIN = projectAdmin;

        page.setSelectedWorkflowStatus(page.getIssued());
        page.setSelectedCorresponStatus(page.getOpen());

        String expected = "correspon/corresponIndex?projectId=PJ1";

        WorkflowStatus[] expWorkflowStatuses = {WorkflowStatus.ISSUED};
        CorresponStatus[] expCorresponStatuses = {CorresponStatus.OPEN};
        // 自分が作成した文書であること.
        User[] expUsers = {user};

        String actual = page.goCorresponIndexWorkflowStatus();

        assertEquals(expected, actual);

        SearchCorresponCondition actCondition = MockAbstractPage.SET_CONDITION;
        assertEquals(projectId, actCondition.getProjectId());
        assertEquals(user.getEmpNo(), actCondition.getUserId());
        assertEquals(systemAdmin, actCondition.isSystemAdmin());
        assertEquals(projectAdmin, actCondition.isProjectAdmin());

        assertArrayEquals(expWorkflowStatuses, actCondition.getWorkflowStatuses());
        assertArrayEquals(expCorresponStatuses, actCondition.getCorresponStatuses());
//        assertArrayEquals(expUsers, actCondition.getAddressUsers());
        assertArrayEquals(expUsers, actCondition.getFromUsers());
        assertTrue(actCondition.isUserPreparer());
//        assertTrue(actCondition.isAddressFrom());
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.view.common.ProjectHomePage#goCorresponIndexWorkflowProcessStatus()} のためのテスト・メソッド.
     * Request for Checkが選択された場合.
     */
    @Test
    public void testGoCorresponIndexWorkflowProcessStatusRequestForCheck() {
        String projectId = "PJ1";
        User user = new User();
        user.setEmpNo("00001");
        boolean systemAdmin = true;
        boolean projectAdmin = true;

        MockAbstractPage.RET_CURRENT_PROJECT_ID = projectId;
        MockAbstractPage.RET_USER = user;
        MockAbstractPage.RET_SYSTEM_ADMIN = systemAdmin;
        MockAbstractPage.RET_PROJECT_ADMIN = projectAdmin;

        page.setSelectedWorkflowProcessStatus(page.getRequestForCheck());

        String expected = "correspon/corresponIndex?projectId=PJ1";

        WorkflowProcessStatus[] expWorkflowProcessStatuses = {
            WorkflowProcessStatus.REQUEST_FOR_CHECK, WorkflowProcessStatus.UNDER_CONSIDERATION};
        //  Pageクラス内で暗黙的に、検証・承認依頼中のコレポン文書という条件を付与している
        WorkflowStatus[] expWorkflowStatus = WorkflowStatus.getRequesting();
        User[] expUsers = {user};

        String actual = page.goCorresponIndexWorkflowProcessStatus();

        assertEquals(expected, actual);

        SearchCorresponCondition actCondition = MockAbstractPage.SET_CONDITION;
        assertEquals(projectId, actCondition.getProjectId());
        assertEquals(user.getEmpNo(), actCondition.getUserId());
        assertEquals(systemAdmin, actCondition.isSystemAdmin());
        assertEquals(projectAdmin, actCondition.isProjectAdmin());

        assertArrayEquals(expWorkflowProcessStatuses, actCondition.getWorkflowProcessStatuses());
        assertArrayEquals(expWorkflowStatus, actCondition.getWorkflowStatuses());
//        assertArrayEquals(expUsers, actCondition.getWorkflowUsers());
        assertArrayEquals(expUsers, actCondition.getFromUsers());
    }

    /**
     * {@link ProjectHomePage#goCorresponIndexReadStatus()}のテストケース.
     * 正常系
     * Inboxリンク(未読)
     * Person in Chargeが有効
     */
    @Test
    public void testGoCorresponIndexReadStatusNew01() {
        String projectId = "PJ1";
        User user = new User();
        user.setEmpNo("00001");
        boolean systemAdmin = true;
        boolean projectAdmin = false;

        MockAbstractPage.RET_CURRENT_PROJECT_ID = projectId;
        MockAbstractPage.RET_USER = user;
        MockAbstractPage.RET_SYSTEM_ADMIN = systemAdmin;
        MockAbstractPage.RET_PROJECT_ADMIN = projectAdmin;
        MockAbstractPage.IS_USE_PERSON_IN_CHARGE = true;

        page.setSelectedReadStatus(ReadStatus.NEW.name());

        String expected = "correspon/corresponIndex?projectId=PJ1";

        ReadStatus[] expReadStatuses = {ReadStatus.NEW};

        String actual = page.goCorresponIndexReadStatus();

        assertEquals(expected, actual);

        SearchCorresponCondition actCondition = MockAbstractPage.SET_CONDITION;
        assertEquals(projectId, actCondition.getProjectId());
        assertEquals(user.getEmpNo(), actCondition.getUserId());
        assertEquals(systemAdmin, actCondition.isSystemAdmin());
        assertEquals(projectAdmin, actCondition.isProjectAdmin());

        assertArrayEquals(expReadStatuses, actCondition.getReadStatuses());
//        assertTrue(actCondition.isAddressCc());
//        assertTrue(actCondition.isAddressAttention());
//        assertTrue(actCondition.isAddressPersonInCharge());
        assertTrue(actCondition.isUserCc());
        assertTrue(actCondition.isUserAttention());
        assertTrue(actCondition.isUserPic());
    }

    /**
     * {@link ProjectHomePage#goCorresponIndexReadStatus()}のテストケース.
     * 正常系
     * Inboxリンク(未読)
     * Person in Chargeが無効
     */
    @Test
    public void testGoCorresponIndexReadStatusNew02() {
        String projectId = "PJ1";
        User user = new User();
        user.setEmpNo("00001");
        boolean systemAdmin = true;
        boolean projectAdmin = false;

        MockAbstractPage.RET_CURRENT_PROJECT_ID = projectId;
        MockAbstractPage.RET_USER = user;
        MockAbstractPage.RET_SYSTEM_ADMIN = systemAdmin;
        MockAbstractPage.RET_PROJECT_ADMIN = projectAdmin;
        MockAbstractPage.IS_USE_PERSON_IN_CHARGE = false;

        page.setSelectedReadStatus(ReadStatus.NEW.name());

        String expected = "correspon/corresponIndex?projectId=PJ1";

        ReadStatus[] expReadStatuses = {ReadStatus.NEW};

        String actual = page.goCorresponIndexReadStatus();

        assertEquals(expected, actual);

        SearchCorresponCondition actCondition = MockAbstractPage.SET_CONDITION;
        assertEquals(projectId, actCondition.getProjectId());
        assertEquals(user.getEmpNo(), actCondition.getUserId());
        assertEquals(systemAdmin, actCondition.isSystemAdmin());
        assertEquals(projectAdmin, actCondition.isProjectAdmin());

        assertArrayEquals(expReadStatuses, actCondition.getReadStatuses());
//        assertTrue(actCondition.isAddressCc());
//        assertTrue(actCondition.isAddressAttention());
//        assertFalse(actCondition.isAddressPersonInCharge());
        assertTrue(actCondition.isUserCc());
        assertTrue(actCondition.isUserAttention());
        assertFalse(actCondition.isUserPic());
    }

    /**
     * {@link ProjectHomePage#goCorresponIndexReadStatus()}のテストケース.
     * 正常系
     * Inboxリンク(既読)
     * Person in Chargeが有効
     */
    @Test
    public void testGoCorresponIndexReadStatusRead01() {
        String projectId = "PJ1";
        User user = new User();
        user.setEmpNo("00001");
        boolean systemAdmin = true;
        boolean projectAdmin = false;

        MockAbstractPage.RET_CURRENT_PROJECT_ID = projectId;
        MockAbstractPage.RET_USER = user;
        MockAbstractPage.RET_SYSTEM_ADMIN = systemAdmin;
        MockAbstractPage.RET_PROJECT_ADMIN = projectAdmin;
        MockAbstractPage.IS_USE_PERSON_IN_CHARGE = true;

        page.setSelectedReadStatus(ReadStatus.READ.name());

        String expected = "correspon/corresponIndex?projectId=PJ1";

        ReadStatus[] expReadStatuses = {ReadStatus.READ};

        String actual = page.goCorresponIndexReadStatus();

        assertEquals(expected, actual);

        SearchCorresponCondition actCondition = MockAbstractPage.SET_CONDITION;
        assertEquals(projectId, actCondition.getProjectId());
        assertEquals(user.getEmpNo(), actCondition.getUserId());
        assertEquals(systemAdmin, actCondition.isSystemAdmin());
        assertEquals(projectAdmin, actCondition.isProjectAdmin());

        assertArrayEquals(expReadStatuses, actCondition.getReadStatuses());
//        assertTrue(actCondition.isAddressCc());
//        assertTrue(actCondition.isAddressAttention());
//        assertTrue(actCondition.isAddressPersonInCharge());
        assertTrue(actCondition.isUserCc());
        assertTrue(actCondition.isUserAttention());
        assertTrue(actCondition.isUserPic());
    }

    /**
     * {@link ProjectHomePage#goCorresponIndexReadStatus()}のテストケース.
     * 正常系
     * Inboxリンク(既読)
     * Person in Chargeが無効
     */
    @Test
    public void testGoCorresponIndexReadStatusRead02() {
        String projectId = "PJ1";
        User user = new User();
        user.setEmpNo("00001");
        boolean systemAdmin = true;
        boolean projectAdmin = false;

        MockAbstractPage.RET_CURRENT_PROJECT_ID = projectId;
        MockAbstractPage.RET_USER = user;
        MockAbstractPage.RET_SYSTEM_ADMIN = systemAdmin;
        MockAbstractPage.RET_PROJECT_ADMIN = projectAdmin;
        MockAbstractPage.IS_USE_PERSON_IN_CHARGE = false;

        page.setSelectedReadStatus(ReadStatus.READ.name());

        String expected = "correspon/corresponIndex?projectId=PJ1";

        ReadStatus[] expReadStatuses = {ReadStatus.READ};

        String actual = page.goCorresponIndexReadStatus();

        assertEquals(expected, actual);

        SearchCorresponCondition actCondition = MockAbstractPage.SET_CONDITION;
        assertEquals(projectId, actCondition.getProjectId());
        assertEquals(user.getEmpNo(), actCondition.getUserId());
        assertEquals(systemAdmin, actCondition.isSystemAdmin());
        assertEquals(projectAdmin, actCondition.isProjectAdmin());

        assertArrayEquals(expReadStatuses, actCondition.getReadStatuses());
//        assertTrue(actCondition.isAddressCc());
//        assertTrue(actCondition.isAddressAttention());
//        assertFalse(actCondition.isAddressPersonInCharge());
        assertTrue(actCondition.isUserCc());
        assertTrue(actCondition.isUserAttention());
        assertFalse(actCondition.isUserPic());
    }

    /**
     * {@link ProjectHomePage#goCorresponIndexReadStatus()}のテストケース.
     * 正常系
     * Inboxリンク(未読・既読)
     * Person in Chargeが有効
     */
    @Test
    public void testGoCorresponIndexReadStatusNewRead01() {
        String projectId = "PJ1";
        User user = new User();
        user.setEmpNo("00001");
        boolean systemAdmin = true;
        boolean projectAdmin = false;

        MockAbstractPage.RET_CURRENT_PROJECT_ID = projectId;
        MockAbstractPage.RET_USER = user;
        MockAbstractPage.RET_SYSTEM_ADMIN = systemAdmin;
        MockAbstractPage.RET_PROJECT_ADMIN = projectAdmin;
        MockAbstractPage.IS_USE_PERSON_IN_CHARGE = true;

        String expected = "correspon/corresponIndex?projectId=PJ1";

        String actual = page.goCorresponIndexReadStatus();

        assertEquals(expected, actual);

        SearchCorresponCondition actCondition = MockAbstractPage.SET_CONDITION;
        assertEquals(projectId, actCondition.getProjectId());
        assertEquals(user.getEmpNo(), actCondition.getUserId());
        assertEquals(systemAdmin, actCondition.isSystemAdmin());
        assertEquals(projectAdmin, actCondition.isProjectAdmin());
//        assertTrue(actCondition.isAddressCc());
//        assertTrue(actCondition.isAddressAttention());
//        assertTrue(actCondition.isAddressPersonInCharge());
        assertTrue(actCondition.isUserCc());
        assertTrue(actCondition.isUserAttention());
        assertTrue(actCondition.isUserPic());
    }

    /**
     * {@link ProjectHomePage#goCorresponIndexReadStatus()}のテストケース.
     * 正常系
     * Inboxリンク(未読・既読)
     * Person in Chargeが無効
     */
    @Test
    public void testGoCorresponIndexReadStatusNewRead02() {
        String projectId = "PJ1";
        User user = new User();
        user.setEmpNo("00001");
        boolean systemAdmin = true;
        boolean projectAdmin = false;

        MockAbstractPage.RET_CURRENT_PROJECT_ID = projectId;
        MockAbstractPage.RET_USER = user;
        MockAbstractPage.RET_SYSTEM_ADMIN = systemAdmin;
        MockAbstractPage.RET_PROJECT_ADMIN = projectAdmin;
        MockAbstractPage.IS_USE_PERSON_IN_CHARGE = false;

        String expected = "correspon/corresponIndex?projectId=PJ1";

        String actual = page.goCorresponIndexReadStatus();

        assertEquals(expected, actual);

        SearchCorresponCondition actCondition = MockAbstractPage.SET_CONDITION;
        assertEquals(projectId, actCondition.getProjectId());
        assertEquals(user.getEmpNo(), actCondition.getUserId());
        assertEquals(systemAdmin, actCondition.isSystemAdmin());
        assertEquals(projectAdmin, actCondition.isProjectAdmin());
//        assertTrue(actCondition.isAddressCc());
//        assertTrue(actCondition.isAddressAttention());
//        assertFalse(actCondition.isAddressPersonInCharge());
        assertTrue(actCondition.isUserCc());
        assertTrue(actCondition.isUserAttention());
        assertFalse(actCondition.isUserPic());
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.view.common.ProjectHomePage#goCorresponIndexWorkflowProcessStatus()} のためのテスト・メソッド.
     * Request for Approvalが選択された場合.
     */
    @Test
    public void testGoCorresponIndexWorkflowProcessStatusRequestForApproval() {
        String projectId = "PJ1";
        User user = new User();
        user.setEmpNo("00001");
        boolean systemAdmin = true;
        boolean projectAdmin = true;

        MockAbstractPage.RET_CURRENT_PROJECT_ID = projectId;
        MockAbstractPage.RET_USER = user;
        MockAbstractPage.RET_SYSTEM_ADMIN = systemAdmin;
        MockAbstractPage.RET_PROJECT_ADMIN = projectAdmin;

        page.setSelectedWorkflowProcessStatus(page.getRequestForApproval());

        String expected = "correspon/corresponIndex?projectId=PJ1";

        WorkflowProcessStatus[] expWorkflowProcessStatuses = {
            WorkflowProcessStatus.REQUEST_FOR_APPROVAL, WorkflowProcessStatus.UNDER_CONSIDERATION};
        //  Pageクラス内で暗黙的に、検証・承認依頼中のコレポン文書という条件を付与している
        WorkflowStatus[] expWorkflowStatus = WorkflowStatus.getRequesting();
        User[] expUsers = {user};

        String actual = page.goCorresponIndexWorkflowProcessStatus();

        assertEquals(expected, actual);

        SearchCorresponCondition actCondition = MockAbstractPage.SET_CONDITION;
        assertEquals(projectId, actCondition.getProjectId());
        assertEquals(user.getEmpNo(), actCondition.getUserId());
        assertEquals(systemAdmin, actCondition.isSystemAdmin());
        assertEquals(projectAdmin, actCondition.isProjectAdmin());

        assertArrayEquals(expWorkflowProcessStatuses, actCondition.getWorkflowProcessStatuses());
        assertArrayEquals(expWorkflowStatus, actCondition.getWorkflowStatuses());
//        assertArrayEquals(expUsers, actCondition.getWorkflowUsers());
        assertArrayEquals(expUsers, actCondition.getFromUsers());
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.view.common.ProjectHomePage#goCorresponIndexAction()} のためのテスト・メソッド.
     */
    @Test
    public void testGoCorresponIndexAction() {
        String projectId = "PJ1";
        User user = new User();
        user.setEmpNo("00001");
        boolean systemAdmin = true;
        boolean projectAdmin = true;

        MockAbstractPage.RET_CURRENT_PROJECT_ID = projectId;
        MockAbstractPage.RET_USER = user;
        MockAbstractPage.RET_SYSTEM_ADMIN = systemAdmin;
        MockAbstractPage.RET_PROJECT_ADMIN = projectAdmin;

        page.setSelectedAttention(true);
        page.setSelectedPIC(true);

        String expected = "correspon/corresponIndex?projectId=PJ1";

        User[] expUsers = {user};

        String actual = page.goCorresponIndexAction();

        assertEquals(expected, actual);

        SearchCorresponCondition actCondition = MockAbstractPage.SET_CONDITION;
        assertEquals(projectId, actCondition.getProjectId());
        assertEquals(user.getEmpNo(), actCondition.getUserId());
        assertEquals(systemAdmin, actCondition.isSystemAdmin());
        assertEquals(projectAdmin, actCondition.isProjectAdmin());

        assertArrayEquals(new WorkflowStatus[] { WorkflowStatus.ISSUED },
                          actCondition.getWorkflowStatuses());

//        assertArrayEquals(expUsers, actCondition.getAddressUsers());
        assertArrayEquals(expUsers, actCondition.getToUsers());
//        assertTrue(actCondition.isAddressUnreplied());
//        assertTrue(actCondition.isAddressAttention());
//        assertTrue(actCondition.isAddressPersonInCharge());
        assertTrue(actCondition.isUserUnreplied());
        assertTrue(actCondition.isUserAttention());
        assertTrue(actCondition.isUserPic());
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.view.common.ProjectHomePage#goCorresponIndexCc()} のためのテスト・メソッド.
     */
    @Test
    public void testGoCorresponIndexCc() {
        String projectId = "PJ1";
        User user = new User();
        user.setEmpNo("00001");
        boolean systemAdmin = true;
        boolean projectAdmin = true;

        MockAbstractPage.RET_CURRENT_PROJECT_ID = projectId;
        MockAbstractPage.RET_USER = user;
        MockAbstractPage.RET_SYSTEM_ADMIN = systemAdmin;
        MockAbstractPage.RET_PROJECT_ADMIN = projectAdmin;

        page.setSelectedAttention(true);
        page.setSelectedPIC(true);

        String expected = "correspon/corresponIndex?projectId=PJ1";

        User[] expUsers = {user};

        String actual = page.goCorresponIndexCc();

        assertEquals(expected, actual);

        SearchCorresponCondition actCondition = MockAbstractPage.SET_CONDITION;
        assertEquals(projectId, actCondition.getProjectId());
        assertEquals(user.getEmpNo(), actCondition.getUserId());
        assertEquals(systemAdmin, actCondition.isSystemAdmin());
        assertEquals(projectAdmin, actCondition.isProjectAdmin());

        assertArrayEquals(new WorkflowStatus[] { WorkflowStatus.ISSUED },
                          actCondition.getWorkflowStatuses());

//        assertArrayEquals(expUsers, actCondition.getAddressUsers());
        assertArrayEquals(expUsers, actCondition.getToUsers());
//        assertTrue(actCondition.isAddressCc());
        assertTrue(actCondition.isUserCc());
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.view.common.ProjectHomePage#goCorresponIndexRelatedUserAll()} のためのテスト・メソッド.
     */
    @Test
    public void testGoCorresponIndexRelatedUserAll() {
        String projectId = "PJ1";
        User user = new User();
        user.setEmpNo("00001");
        boolean systemAdmin = true;
        boolean projectAdmin = true;

        MockAbstractPage.RET_CURRENT_PROJECT_ID = projectId;
        MockAbstractPage.RET_USER = user;
        MockAbstractPage.RET_SYSTEM_ADMIN = systemAdmin;
        MockAbstractPage.RET_PROJECT_ADMIN = projectAdmin;

        String expected = "correspon/corresponIndex?projectId=PJ1";

        String actual = page.goCorresponIndexRelatedUserAll();

        assertEquals(expected, actual);

        SearchCorresponCondition actCondition = MockAbstractPage.SET_CONDITION;
        assertEquals(projectId, actCondition.getProjectId());
        assertEquals(user.getEmpNo(), actCondition.getUserId());
        assertEquals(systemAdmin, actCondition.isSystemAdmin());
        assertEquals(projectAdmin, actCondition.isProjectAdmin());

        assertEquals(0, actCondition.getWorkflowStatuses().length);
        assertEquals(0, actCondition.getCorresponTypes().length);
        assertEquals(0, actCondition.getCorresponStatuses().length);
//        assertEquals(0, actCondition.getAddressUsers().length);
        assertEquals(0, actCondition.getFromUsers().length);
        assertEquals(0, actCondition.getToUsers().length);
        assertEquals(0, actCondition.getWorkflowProcessStatuses().length);
//        assertEquals(0, actCondition.getWorkflowUsers().length);
        assertEquals(0, actCondition.getFromUsers().length);
        assertEquals(0, actCondition.getReadStatuses().length);
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.view.common.ProjectHomePage#goCorresponIndexWorkflowProcessStatus()} のためのテスト・メソッド.
     * Under Reviewが選択された場合.
     */
    @Test
    public void testGoCorresponIndexWorkflowProcessStatusUnderReview() {
        String projectId = "PJ1";
        User user = new User();
        user.setEmpNo("00001");
        boolean systemAdmin = false;
        boolean projectAdmin = false;

        MockAbstractPage.RET_CURRENT_PROJECT_ID = projectId;
        MockAbstractPage.RET_USER = user;
        MockAbstractPage.RET_SYSTEM_ADMIN = systemAdmin;
        MockAbstractPage.RET_PROJECT_ADMIN = projectAdmin;

        page.setSelectedWorkflowProcessStatus(page.getUnderReview());

        String expected = "correspon/corresponIndex?projectId=PJ1";

        //  Pageクラス内で暗黙的に、検証・承認依頼中のコレポン文書という条件を付与している
        WorkflowStatus[] expWorkflowStatus = WorkflowStatus.getRequesting();

        User[] expUsers = {};

        String actual = page.goCorresponIndexWorkflowProcessStatus();

        assertEquals(expected, actual);

        SearchCorresponCondition actCondition = MockAbstractPage.SET_CONDITION;
        assertEquals(projectId, actCondition.getProjectId());
        assertEquals(user.getEmpNo(), actCondition.getUserId());
        assertEquals(systemAdmin, actCondition.isSystemAdmin());
        assertEquals(projectAdmin, actCondition.isProjectAdmin());
//        assertEquals(user, actCondition.getAddressUsers()[0]);
//        assertEquals(user, actCondition.getToUsers()[0]);
        assertTrue(actCondition.isUserPreparer());
//        assertTrue(actCondition.isAddressFrom());

        assertArrayEquals(expWorkflowStatus, actCondition.getWorkflowStatuses());
//        assertArrayEquals(expUsers, actCondition.getWorkflowUsers());
//        assertArrayEquals(expUsers, actCondition.getFromUsers());
    }

    private List<CorresponGroupSummary> createCorresponGroupSummaryList() {
        List<CorresponGroupSummary> list = new ArrayList<CorresponGroupSummary>();

        CorresponGroupSummary summary = new CorresponGroupSummary();
        CorresponType corresponType = new CorresponType();
        corresponType.setCorresponType("Type A");
        summary.setCorresponType(corresponType);
        summary.setToCount(1L);
        summary.setCcCount(2L);
        list.add(summary);

        summary = new CorresponGroupSummary();
        corresponType = new CorresponType();
        corresponType.setCorresponType("Type B");
        summary.setCorresponType(corresponType);
        summary.setToCount(11L);
        summary.setCcCount(22L);
        list.add(summary);

        summary = new CorresponGroupSummary();
        corresponType = new CorresponType();
        corresponType.setCorresponType("Type C");
        summary.setCorresponType(corresponType);
        summary.setToCount(111L);
        summary.setCcCount(222L);
        list.add(summary);

        return list;
    }

    private CorresponGroup[] createUserCorresponGroups() {
        CorresponGroup group1 = new CorresponGroup();
        group1.setId(1L);
        CorresponGroup group2 = new CorresponGroup();
        group2.setId(2L);
        CorresponGroup group3 = new CorresponGroup();
        group3.setId(3L);

        CorresponGroup[] groups = {group1, group2, group3};

        return groups;
    }

    private CorresponUserSummary createCorresponUserSummary() {
        CorresponUserSummary summary = new CorresponUserSummary();
        summary.setDraftCount(1L);
        summary.setRequestForCheckCount(2L);
        summary.setRequestForApproveCount(3L);
        summary.setDeniedCount(4L);
        summary.setOpenCount(5L);
        summary.setClosedCount(6L);
        summary.setCanceledCount(7L);
        summary.setAttentionCount(8L);
        summary.setPersonInChargeCount(9L);
        summary.setCcCount(10L);

        return summary;
    }

    private List<Company> createCompanyList() {
        List<Company> list = new ArrayList<Company>();

        Company company = new Company();
        company.setId(11L);
        company.setProjectCompanyId(1L);
        company.setName("Company Name A");
        list.add(company);

        company = new Company();
        company.setId(22L);
        company.setProjectCompanyId(2L);
        company.setName("Company Name B");
        list.add(company);

        company = new Company();
        company.setId(33L);
        company.setProjectCompanyId(3L);
        company.setName("Company Name C");
        list.add(company);

        return list;
    }

    private List<CorresponGroup> createCorresponGroupList() {
        List<CorresponGroup> list = new ArrayList<CorresponGroup>();

        CorresponGroup group = new CorresponGroup();
        group.setId(1L);
        group.setName("Group Name A");
        list.add(group);

        group = new CorresponGroup();
        group.setId(2L);
        group.setName("Group Name B");
        list.add(group);

        group = new CorresponGroup();
        group.setId(3L);
        group.setName("Group Name C");
        list.add(group);

        return list;
    }

    private List<ProjectUser> createProjectAdminList() {
        String projectAdminFlg = "X";
        String projectId = "PJ1";
        List<ProjectUser> list = new ArrayList<ProjectUser>();

        ProjectUser pUser = new ProjectUser();
        pUser.setProjectAdminFlg(projectAdminFlg);
        pUser.setProjectId(projectId);
        User user = new User();
        user.setEmpNo("00001");
        pUser.setUser(user);
        list.add(pUser);

        pUser = new ProjectUser();
        pUser.setProjectAdminFlg(projectAdminFlg);
        pUser.setProjectId(projectId);
        user = new User();
        user.setEmpNo("00002");
        pUser.setUser(user);
        list.add(pUser);

        return list;
    }

    private List<ProjectUser> createGroupAdminList() {
        String normalUserLevel = SystemConfig.getValue(Constants.KEY_SECURITY_LEVEL_NORMAL_USER);
        String projectId = "PJ1";
        List<ProjectUser> list = new ArrayList<ProjectUser>();

        ProjectUser pUser = new ProjectUser();
        pUser.setSecurityLevel(normalUserLevel);
        pUser.setProjectId(projectId);
        User user = new User();
        user.setEmpNo("00003");
        pUser.setUser(user);
        list.add(pUser);

        pUser = new ProjectUser();
        pUser.setSecurityLevel(normalUserLevel);
        pUser.setProjectId(projectId);
        user = new User();
        user.setEmpNo("00004");
        pUser.setUser(user);
        list.add(pUser);

        return list;

    }

    private List<ProjectUser> createNormalUserList() {
        String normalUserLevel = SystemConfig.getValue(Constants.KEY_SECURITY_LEVEL_NORMAL_USER);
        String projectId = "PJ1";
        List<ProjectUser> list = new ArrayList<ProjectUser>();

        ProjectUser pUser = new ProjectUser();
        pUser.setSecurityLevel(normalUserLevel);
        pUser.setProjectId(projectId);
        User user = new User();
        user.setEmpNo("00004");
        pUser.setUser(user);
        list.add(pUser);

        pUser = new ProjectUser();
        pUser.setSecurityLevel(normalUserLevel);
        pUser.setProjectId(projectId);
        user = new User();
        user.setEmpNo("00005");
        pUser.setUser(user);
        list.add(pUser);

        return list;
    }

    public static class MockHomeService extends MockUp<HomeServiceImpl> {
        static ProjectDetailsSummary RET_FIND_PROJECT_DETAILS;

        @Mock
        public ProjectDetailsSummary findProjectDetails(String projectId, boolean usePersonInCharge)
                throws ServiceAbortException {
            return RET_FIND_PROJECT_DETAILS;
        }
    }

    public static class MockAbstractPage extends MockUp<AbstractPage> {
        static String RET_CURRENT_PROJECT_ID;
        static User RET_USER;
        static boolean RET_SYSTEM_ADMIN;
        static boolean RET_PROJECT_ADMIN;
        static SearchCorresponCondition SET_CONDITION;
        static boolean IS_USE_PERSON_IN_CHARGE;

        @Mock
        public String getCurrentProjectId() {
            return RET_CURRENT_PROJECT_ID;
        }

        @Mock
        public User getCurrentUser() {
            return RET_USER;
        }

        @Mock
        public boolean isSystemAdmin() {
            return RET_SYSTEM_ADMIN;
        }

        @Mock
        public boolean isProjectAdmin(String checkProjectId) {
            return RET_PROJECT_ADMIN;
        }

        @Mock
        public void setCurrentSearchCorresponCondition(SearchCorresponCondition condition) {
            SET_CONDITION = condition;
        }

        @Mock
        public boolean isUsePersonInCharge() {
            return IS_USE_PERSON_IN_CHARGE;
        }
    }

    @SuppressWarnings("serial")
    public static class MockFavoriteFilterService extends MockUp<FavoriteFilterServiceImpl> {
        static FavoriteFilter dummyDto1 = new FavoriteFilter();
        static FavoriteFilter dummyDto2 = new FavoriteFilter();
        static FavoriteFilter dummyDto3 = new FavoriteFilter();
        static FavoriteFilter dummyDto4 = new FavoriteFilter();
        static FavoriteFilter dummyDto5 = new FavoriteFilter();

        static void init() {
            dummyDto1.setId(1L);
            dummyDto2.setId(2L);
            dummyDto3.setId(3L);
            dummyDto4.setId(4L);
            dummyDto5.setId(5L);
            DUMMY_LIST = null;
        }

        static List<FavoriteFilter> DUMMY_LIST;

        @Mock
        @SuppressWarnings("serial")
        public List<FavoriteFilter> search(SearchFavoriteFilterCondition condition) {
            DUMMY_LIST = new ArrayList<FavoriteFilter>(new ArrayList<FavoriteFilter>(5) {
                { add(MockFavoriteFilterService.dummyDto1);
                add(MockFavoriteFilterService.dummyDto2);
                add(MockFavoriteFilterService.dummyDto3);
                add(MockFavoriteFilterService.dummyDto4);
                add(MockFavoriteFilterService.dummyDto5); } });

            return DUMMY_LIST;
        }
    }
}
