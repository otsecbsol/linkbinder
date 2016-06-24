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
package jp.co.opentone.bsol.linkbinder.view.correspon;

import static jp.co.opentone.bsol.framework.core.message.MessageCode.*;
import static jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode.*;
import static org.junit.Assert.*;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.annotation.Resource;
import javax.faces.application.FacesMessage;
import javax.faces.model.SelectItem;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import jp.co.opentone.bsol.framework.core.dto.Code;
import jp.co.opentone.bsol.framework.core.message.MessageCode;
import jp.co.opentone.bsol.framework.core.message.Messages;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.extension.jsf.FacesContextMock;
import jp.co.opentone.bsol.framework.test.AbstractTestCase;
import jp.co.opentone.bsol.framework.web.view.util.ViewHelper;
import jp.co.opentone.bsol.linkbinder.dto.Correspon;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroup;
import jp.co.opentone.bsol.linkbinder.dto.CorresponIndex;
import jp.co.opentone.bsol.linkbinder.dto.CorresponIndexHeader;
import jp.co.opentone.bsol.linkbinder.dto.CorresponReadStatus;
import jp.co.opentone.bsol.linkbinder.dto.CorresponType;
import jp.co.opentone.bsol.linkbinder.dto.CustomField;
import jp.co.opentone.bsol.linkbinder.dto.FavoriteFilter;
import jp.co.opentone.bsol.linkbinder.dto.ProjectUser;
import jp.co.opentone.bsol.linkbinder.dto.SearchCorresponResult;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.code.CorresponStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.FullTextSearchMode;
import jp.co.opentone.bsol.linkbinder.dto.code.ReadStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowProcessStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowStatus;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponCondition;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponGroupCondition;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponTypeCondition;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCustomFieldCondition;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchUserCondition;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.service.UserPermissionHelper;
import jp.co.opentone.bsol.linkbinder.service.admin.impl.CorresponGroupServiceImpl;
import jp.co.opentone.bsol.linkbinder.service.admin.impl.CorresponTypeServiceImpl;
import jp.co.opentone.bsol.linkbinder.service.admin.impl.CustomFieldServiceImpl;
import jp.co.opentone.bsol.linkbinder.service.admin.impl.UserServiceImpl;
import jp.co.opentone.bsol.linkbinder.service.common.impl.FavoriteFilterServiceImpl;
import jp.co.opentone.bsol.linkbinder.service.correspon.impl.CorresponSearchServiceImpl;
import jp.co.opentone.bsol.linkbinder.view.AbstractPage;
import jp.co.opentone.bsol.linkbinder.view.correspon.CorresponIndexPage.SearchAction;
import mockit.Mock;
import mockit.MockUp;

/**
 * {@link CorresponIndexPage}のテストケース.
 * @author opentone
 */
public class CorresponIndexPageTest extends AbstractTestCase {

    /**
     * テスト対象.
     */
    @Resource
    private CorresponIndexPage page;

    /**
     * 検証用データ.
     */
    private List<Correspon> list;

    /**
     * テストの前準備.
     */
    @BeforeClass
    public static void testSetUp() {
        FacesContextMock.initialize();
        new MockCorresponSearchService();
        new MockCorresponTypeService();
        new MockUserService();
        new MockCorresponGroupService();
        new MockCustomFieldService();
        new MockAbstractPage();
        new MockViewHelper();
        new MockUserPermissionHelper();
        new MockFavoriteFilterService();
    }

    /**
     * 後始末.
     */
    @AfterClass
    public static void testTeardown() {
        new MockCorresponSearchService().tearDown();
        new MockCorresponTypeService().tearDown();
        new MockUserService().tearDown();
        new MockCorresponGroupService().tearDown();
        new MockCustomFieldService().tearDown();
        new MockAbstractPage().tearDown();
        new MockViewHelper().tearDown();
        new MockUserPermissionHelper().tearDown();
        new MockFavoriteFilterService().tearDown();
        FacesContextMock.tearDown();
    }

    @Before
    public void setUp() {
        list = getList();
        page.setAdvancedSearchDisplayed(false);
    }

    @After
    public void tearDown() {
        MockCorresponSearchService.RET_SEARCH = null;
        MockCorresponSearchService.RET_CSV = null;
        MockCorresponSearchService.RET_EXCEL = null;
        MockCorresponSearchService.RET_HTML = null;
        MockCorresponSearchService.RET_ZIP = null;
        MockCorresponSearchService.SET_READ_STATUS = null;
        MockCorresponSearchService.SET_STATUS = null;
        MockCorresponSearchService.SET_DELETE = null;
        MockCorresponSearchService.SET_ZIP = null;
        MockCorresponSearchService.ERROR_SEARCH = null;
        MockCorresponTypeService.RET_SEARCH = null;
        MockUserService.RET_SEARCH = null;
        MockCorresponGroupService.RET_SEARCH = null;
        MockCustomFieldService.RET_SEARCH = null;
        MockAbstractPage.RET_PROJID = null;
        MockAbstractPage.RET_CONDITION = null;
        MockAbstractPage.SET_CONDITION = null;
        MockAbstractPage.RET_FILE_NAME = null;
        MockAbstractPage.RET_USER = null;
        MockUserPermissionHelper.RET_PROJECT_ADMIN = false;
        MockViewHelper.EX_DOWNLOAD = null;
        MockViewHelper.RET_ACTION = null;
        MockViewHelper.RET_DATA = null;
        MockViewHelper.RET_FILENAME = null;
        MockViewHelper.RET_COOKIE_VALUE = null;

        FacesContextMock.EXPECTED_CLIENT_ID = null;
        FacesContextMock.EXPECTED_MESSAGE = null;

        MockFavoriteFilterService.RET_FAVORITE_FILTER = null;
        MockFavoriteFilterService.IS_EXCEPTION = false;
        MockFavoriteFilterService.IS_ROLLBACK = false;

        initializePageSearchCondition();

        page.setSessionSort(null);
        page.setSessionPageNo(null);
        page.setPageRowNum(10);
        page.setPageIndex(10);
    }

    /**
     * 初期化アクションのテスト.
     * @throws Exception
     */
    @Test
    public void testInitialize() throws Exception {
        // Serviceが返すダミーのコレポン文書を設定
        SearchCorresponResult result = new SearchCorresponResult();
        result.setCorresponList(list);
        result.setCount(5);
        MockCorresponSearchService.RET_SEARCH = result;
        MockAbstractPage.RET_PROJID = "MOCK";

        List<CorresponType> expTypeList = createTypeList();
        List<ProjectUser> expUserList = createUserList();
        List<CorresponGroup> expGroupList = createGroupList();
        List<CustomField> expCustomFieldList = createCustomFieldList();
        MockCorresponTypeService.RET_SEARCH = expTypeList;
        MockUserService.RET_SEARCH = expUserList;
        MockCorresponGroupService.RET_SEARCH = expGroupList;
        MockCustomFieldService.RET_SEARCH = expCustomFieldList;

        MockUserPermissionHelper.RET_PROJECT_ADMIN = true;

        page.initialize();

        assertEquals(list, page.getCorresponList());
        assertEquals(5, page.getDataCount());

        // 初期設定を確認
        assertFalse(page.getNext());
        assertFalse(page.getPrevious());
        assertEquals(10, page.getPageIndex());
        assertEquals(5, page.getDataCount());
        assertEquals(1, page.getPageNo());
        assertEquals(10, page.getPageRowNum());
        assertEquals("issued_at", page.getSort());
        // 検索条件
        assertTrue(page.isSimpleSearch());
        assertEquals(Long.valueOf(-1L), page.getType());
        assertEquals(Integer.valueOf(-1), page.getWorkflow());
        assertEquals(ReadStatus.NEW.getValue(), page.getReadStatus());
        assertEquals(expTypeList, page.getTypeList());
        assertArrayEquals(WorkflowStatus.values(), page.getWorkflowList());
        assertArrayEquals(ReadStatus.values(), page.getReadStatusList());
        assertArrayEquals(CorresponStatus.values(), page.getStatusList());
        assertEquals(expUserList, page.getUserList());
        assertArrayEquals(WorkflowProcessStatus.values(), page.getWorkflowProcessesList());
        assertEquals(expGroupList, page.getGroupList());
        assertEquals(expCustomFieldList, page.getCustomFieldList());
        // TODO delete
//        assertFalse(page.isAddressFrom());
//        assertFalse(page.isAddressCc());
//        assertFalse(page.isAddressAttention());
//        assertFalse(page.isAddressPIC());
//        assertFalse(page.isAddressUnreplied());
        assertFalse(page.isUserPreparer());
        assertFalse(page.isUserCc());
        assertFalse(page.isUserAttention());
        assertFalse(page.isUserPic());
        assertFalse(page.isUserUnreplied());
//        assertFalse(page.isGroupFrom());
        assertFalse(page.isGroupTo());
        assertFalse(page.isGroupCc());
        assertEquals("MOCK", page.getCondition().getProjectId());
        assertEquals(1, page.getCondition().getPageNo());
        assertEquals(10, page.getCondition().getPageRowNum());
        assertEquals("issued_at", page.getCondition().getSort());
        assertFalse(page.getCondition().isAscending());
        assertEquals("issued_at DESC NULLS LAST", page.getCondition().getOrderBy());
        assertEquals(page.getCondition(), MockAbstractPage.SET_CONDITION);
        // 表示カラム -- 奇数行がfalse << MockViewHelper.getCookieValue()
        assertFalse(page.getHeader().isNo());
        assertTrue(page.getHeader().isCorresponNo());
        assertFalse(page.getHeader().isPreviousRevision());
        assertTrue(page.getHeader().isFrom());
        assertFalse(page.getHeader().isTo());
        assertTrue(page.getHeader().isType());
        assertFalse(page.getHeader().isSubject());
        assertTrue(page.getHeader().isWorkflow());
        assertFalse(page.getHeader().isCreatedOn());
        assertTrue(page.getHeader().isIssuedOn());
        assertFalse(page.getHeader().isDeadline());
        assertTrue(page.getHeader().isUpdatedOn());
        assertFalse(page.getHeader().isCreatedBy());
        assertTrue(page.getHeader().isIssuedBy());
        assertFalse(page.getHeader().isUpdatedBy());
        assertTrue(page.getHeader().isReplyRequired());
        assertFalse(page.getHeader().isStatus());
    }

    /**
     * 正常系（favorite filterが存在する場合).
     * お気に入りに登録されている検索条件を取得することを検証
     * @throws ServiceAbortException 例外
     */
    @Test
    public void testInitializeFavoriteFilter01() throws ServiceAbortException {
        //TODO JMockitバージョンアップ対応
//        Mockit.redefineMethods(SearchAction.class, MockSearchAction.class);

        MockAbstractPage.RET_PROJID = "0-0000-0A";
        MockAbstractPage.RET_CONDITION = getSearchCorresponConditionForFavoriteFilter();
        FavoriteFilter ff = new FavoriteFilter();
        ff.setSearchConditionsToJson(new SearchCorresponCondition());
        MockFavoriteFilterService.RET_FAVORITE_FILTER = ff;
        page.setFavoriteFilterId(1L);

        page.initialize();

        assertFalseForFavoriteFilter(page.getCondition());
    }

    /**
     * 正常系（favorite filterが存在しない場合）.
     * 通常の検索条件履歴から取得することを検証
     */
    @Test
    public void testInitializeFavoriteFilter02() {
        //TODO JMockitバージョンアップ対応
//        Mockit.redefineMethods(SearchAction.class, MockSearchAction.class);

        MockAbstractPage.RET_PROJID = "0-0000-0A";
        MockAbstractPage.RET_CONDITION = getSearchCorresponConditionForFavoriteFilter();
        MockFavoriteFilterService.RET_FAVORITE_FILTER = null;
        page.setFavoriteFilterId(1L);

        page.initialize();

        assertTrueForFavoriteFilter(page.getCondition());
    }

    /**
     * 正常系（favorite filter取得時にException発生）.
     * 通常の検索条件履歴から取得することを検証
     */
    @Test
    public void testInitializeFavoriteFilter03() {
        //TODO JMockitバージョンアップ対応
//        Mockit.redefineMethods(SearchAction.class, MockSearchAction.class);

        MockAbstractPage.RET_PROJID = "0-0000-0A";
        MockAbstractPage.RET_CONDITION = getSearchCorresponConditionForFavoriteFilter();
        MockFavoriteFilterService.IS_EXCEPTION = true;
        page.setFavoriteFilterId(1L);

        page.initialize();

        assertTrueForFavoriteFilter(page.getCondition());
    }

    /**
     * 正常系（favorite filterの指定なし）.
     * 通常の検索条件履歴から取得することを検証
     */
    @Test
    public void testInitializeFavoriteFilter04() {
        //TODO JMockitバージョンアップ対応
//        Mockit.redefineMethods(SearchAction.class, MockSearchAction.class);

        MockAbstractPage.RET_PROJID = "0-0000-0A";
        MockAbstractPage.RET_CONDITION = getSearchCorresponConditionForFavoriteFilter();
        page.setFavoriteFilterId(null);

        page.initialize();

        assertTrueForFavoriteFilter(page.getCondition());
    }

    /**
     * 正常系.
     * 登録に成功し、favoriteFilterDisplayフラグがfalseであることの検証
     */
    @Test
    public void testAddFavoriteFilter01() {
        // 期待されるメッセージをセット
        FacesContextMock.EXPECTED_MESSAGE =
                new FacesMessage(
                    FacesMessage.SEVERITY_INFO,
                    FacesMessage.SEVERITY_INFO.toString(),
                    Messages.getMessageAsString(ApplicationMessageCode.FAVORITE_FILTER_SAVED));

        User u = new User();
        u.setEmpNo("TST01");
        MockAbstractPage.RET_USER = u;
        MockAbstractPage.RET_PROJID = "0-0000-0A";
        MockAbstractPage.RET_CONDITION = getSearchCorresponConditionForFavoriteFilter();
        page.setFavoriteFilterName("favorite filter name 01");
        page.setFavoriteFilterDisplay(true);

        page.addFavoriteFilter();

        assertFalse(page.isFavoriteFilterDisplay());
    }

    /**
     * 異常系.
     * 登録に失敗し、favoriteFilterDisplayフラグがtrueであることの検証
     */
    @Test
    public void testAddFavoriteFilter02() {
        // 期待されるメッセージをセット
        FacesContextMock.EXPECTED_MESSAGE =
                new FacesMessage(
                        FacesMessage.SEVERITY_ERROR,
                        FacesMessage.SEVERITY_ERROR.toString(),
                        Messages.getMessageAsString(MessageCode.E_KEY_DUPLICATE));

        MockFavoriteFilterService.IS_ROLLBACK = true;
        User u = new User();
        u.setEmpNo("TST01");
        MockAbstractPage.RET_USER = u;
        MockAbstractPage.RET_PROJID = "0-0000-0A";
        MockAbstractPage.RET_CONDITION = getSearchCorresponConditionForFavoriteFilter();
        page.setFavoriteFilterName("favorite filter name 01");
        page.setFavoriteFilterDisplay(true);

        page.addFavoriteFilter();

        assertTrue(page.isFavoriteFilterDisplay());
    }

    private void assertTrueForFavoriteFilter(SearchCorresponCondition actual) {
        SearchCorresponCondition sc = getSearchCorresponConditionForFavoriteFilter();

        assertTrue(sc.getProjectId().equals(actual.getProjectId()));
        assertTrue(sc.getSequenceNo().equals(actual.getSequenceNo()));
        assertTrue(sc.getCorresponNo().equals(actual.getCorresponNo()));
        assertTrue(sc.isIncludingRevision() == actual.isIncludingRevision());
        assertTrue(sc.getKeyword().equals(actual.getKeyword()));
        assertTrue(sc.getFullTextSearchMode().equals(actual.getFullTextSearchMode()));
        assertTrue(sc.getCorresponTypes().length == actual.getCorresponTypes().length);
        assertTrue(sc.getReadStatuses().length == actual.getReadStatuses().length);
        assertTrue(sc.getCorresponStatuses().length == actual.getCorresponStatuses().length);
        assertTrue(sc.getWorkflowStatuses().length == actual.getWorkflowStatuses().length);
        assertTrue(sc.getFromGroups().length == actual.getFromGroups().length);
        assertTrue(sc.getToGroups().length == actual.getToGroups().length);
        assertTrue(sc.isGroupTo() == actual.isGroupTo());
        assertTrue(sc.isGroupCc() == actual.isGroupCc());
        assertTrue(sc.isGroupUnreplied() == actual.isGroupUnreplied());
        assertTrue(sc.getFromUsers().length == actual.getFromUsers().length);
        assertTrue(sc.isUserPreparer() == actual.isUserPreparer());
        assertTrue(sc.isUserChecker() == actual.isUserChecker());
        assertTrue(sc.isUserApprover() == actual.isUserApprover());
        assertTrue(sc.getWorkflowProcessStatuses().length
                == actual.getWorkflowProcessStatuses().length);
        assertTrue(sc.getToUsers().length == actual.getToUsers().length);
        assertTrue(sc.isUserAttention() == actual.isUserAttention());
        assertTrue(sc.isUserCc() == actual.isUserCc());
        assertTrue(sc.isUserPic() == actual.isUserPic());
        assertTrue(sc.isUserUnreplied() == actual.isUserUnreplied());
        assertTrue(sc.getFromCreatedOn().equals(actual.getFromCreatedOn()));
        assertTrue(sc.getToCreatedOn().equals(actual.getToCreatedOn()));
        assertTrue(sc.getFromIssuedOn().equals(actual.getFromIssuedOn()));
        assertTrue(sc.getToIssuedOn().equals(actual.getToIssuedOn()));
        assertTrue(sc.getFromDeadlineForReply().equals(actual.getFromDeadlineForReply()));
        assertTrue(sc.getToDeadlineForReply().equals(actual.getToDeadlineForReply()));
        assertTrue(sc.getCustomFieldNo().equals(actual.getCustomFieldNo()));
        assertTrue(sc.getCustomFieldValue().equals(actual.getCustomFieldValue()));
        assertTrue(sc.getSort().equals(actual.getSort()));
    }

    private void assertFalseForFavoriteFilter(SearchCorresponCondition actual) {
        SearchCorresponCondition sc = getSearchCorresponConditionForFavoriteFilter();

        assertFalse(sc.getProjectId().equals(actual.getProjectId()));
        assertFalse(sc.getSequenceNo().equals(actual.getSequenceNo()));
        assertFalse(sc.getCorresponNo().equals(actual.getCorresponNo()));
        assertFalse(sc.isIncludingRevision() == actual.isIncludingRevision());
        assertFalse(sc.getKeyword().equals(actual.getKeyword()));
        assertFalse(sc.getFullTextSearchMode().equals(actual.getFullTextSearchMode()));
        assertFalse(sc.getCorresponTypes().length == actual.getCorresponTypes().length);
        assertFalse(sc.getReadStatuses().length == actual.getReadStatuses().length);
        assertFalse(sc.getCorresponStatuses().length == actual.getCorresponStatuses().length);
        assertFalse(sc.getWorkflowStatuses().length == actual.getWorkflowStatuses().length);
        assertFalse(sc.getFromGroups().length == actual.getFromGroups().length);
        assertFalse(sc.getToGroups().length == actual.getToGroups().length);
        assertFalse(sc.isGroupTo() == actual.isGroupTo());
        assertFalse(sc.isGroupCc() == actual.isGroupCc());
        assertFalse(sc.isGroupUnreplied() == actual.isGroupUnreplied());
        assertFalse(sc.getFromUsers().length == actual.getFromUsers().length);
        assertFalse(sc.isUserPreparer() == actual.isUserPreparer());
        assertFalse(sc.isUserChecker() == actual.isUserChecker());
        assertFalse(sc.isUserApprover() == actual.isUserApprover());
        assertFalse(sc.getWorkflowProcessStatuses().length
                == actual.getWorkflowProcessStatuses().length);
        assertFalse(sc.getToUsers().length == actual.getToUsers().length);
        assertFalse(sc.isUserAttention() == actual.isUserAttention());
        assertFalse(sc.isUserCc() == actual.isUserCc());
        assertFalse(sc.isUserPic() == actual.isUserPic());
        assertFalse(sc.isUserUnreplied() == actual.isUserUnreplied());
        assertFalse(sc.getFromCreatedOn().equals(actual.getFromCreatedOn()));
        assertFalse(sc.getToCreatedOn().equals(actual.getToCreatedOn()));
        assertFalse(sc.getFromIssuedOn().equals(actual.getFromIssuedOn()));
        assertFalse(sc.getToIssuedOn().equals(actual.getToIssuedOn()));
        assertFalse(sc.getFromDeadlineForReply().equals(actual.getFromDeadlineForReply()));
        assertFalse(sc.getToDeadlineForReply().equals(actual.getToDeadlineForReply()));
        assertFalse(sc.getCustomFieldNo().equals(actual.getCustomFieldNo()));
        assertFalse(sc.getCustomFieldValue().equals(actual.getCustomFieldValue()));
        assertFalse(sc.getSort().equals(actual.getSort()));
    }

    private SearchCorresponCondition getSearchCorresponConditionForFavoriteFilter() {
        SearchCorresponCondition rt = new SearchCorresponCondition();
        rt.setProjectId("0-0000-0B");
        rt.setSequenceNo(1L);
        rt.setCorresponNo("TEST-01234-012");
        rt.setIncludingRevision(true);
        rt.setKeyword("test");
        rt.setFullTextSearchMode(FullTextSearchMode.SUBJECT_AND_BODY);
        rt.setCorresponTypes(new CorresponType[]{new CorresponType()});
        rt.setReadStatuses(new ReadStatus[]{ReadStatus.NEW});
        rt.setCorresponStatuses(new CorresponStatus[]{CorresponStatus.OPEN});
        rt.setWorkflowStatuses(new WorkflowStatus[]{WorkflowStatus.DENIED});
        rt.setFromGroups(new CorresponGroup[]{new CorresponGroup()});
        rt.setToGroups(new CorresponGroup[]{new CorresponGroup()});
        rt.setGroupTo(true);
        rt.setGroupCc(true);
        rt.setGroupUnreplied(true);
        rt.setFromUsers(new User[]{new User()});
        rt.setUserPreparer(true);
        rt.setUserChecker(true);
        rt.setUserApprover(true);
        rt.setWorkflowProcessStatuses(new WorkflowProcessStatus[]{WorkflowProcessStatus.APPROVED});
        rt.setToUsers(new User[]{new User()});
        rt.setUserAttention(true);
        rt.setUserCc(true);
        rt.setUserPic(true);
        rt.setUserUnreplied(true);
        rt.setFromCreatedOn(new Date());
        rt.setToCreatedOn(new Date());
        rt.setFromIssuedOn(new Date());
        rt.setToIssuedOn(new Date());
        rt.setFromDeadlineForReply(new Date());
        rt.setToDeadlineForReply(new Date());
        rt.setCustomFieldNo(1L);
        rt.setCustomFieldValue("test");
        rt.setSort("ASC");
        return rt;
    }

    /**
     * 検索条件（CorresponType）が既に削除されている場合に表示する文字列の検証.
     */
    @Test
    public void testTypeConditionText() {
        page.setCondition(getSearchCorresponConditionForViewText());
        List<CorresponType> l = new ArrayList<CorresponType>();
        CorresponType ct1 = new CorresponType();
        ct1.setId(1L);
        ct1.setCorresponType("CorresponType1");
        CorresponType ct2 = new CorresponType();
        ct2.setId(2L);
        ct2.setCorresponType("CorresponType2");
        l.add(ct1);
        l.add(ct2);
        page.setTypeList(l);
        String txt = page.getTypeConditionText();
        assertEquals("CorresponType1,[Unknown]", txt);
    }

    /**
     * 検索条件（CustomField）が既に削除されている場合に表示する文字列の検証.
     */
    @Test
    public void testCustomFieldText() {
        page.setCondition(getSearchCorresponConditionForViewText());
        List<CustomField> l = new ArrayList<CustomField>();
        CustomField cf1 = new CustomField();
        cf1.setId(1L);
        cf1.setLabel("CustomField1");
        CustomField cf2 = new CustomField();
        cf2.setId(2L);
        cf2.setLabel("CustomField2");
        l.add(cf1);
        l.add(cf2);
        page.setCustomFieldList(l);
        String txt = page.getCustomFieldText();
        assertEquals("[Unknown]", txt);
    }

    /**
     * 検索条件（FromGroup/ToGroup）が既に削除されている場合に表示する文字列の検証.
     */
    @Test
    public void testGroupsConditionText() {
        page.setCondition(getSearchCorresponConditionForViewText());
        List<CorresponGroup> l = new ArrayList<CorresponGroup>();
        CorresponGroup g1 = new CorresponGroup();
        g1.setId(1L);
        g1.setName("AKIHABARA:IT");
        CorresponGroup g2 = new CorresponGroup();
        g2.setId(2L);
        g2.setName("SHINAGAWA:IT");
        l.add(g1);
        l.add(g2);
        page.setGroupList(l);

        String txt1 = page.getFromGroupsConditionText();
        assertEquals("AKIHABARA:IT,[Unknown]", txt1);

        String txt2 = page.getToGroupsConditionText();
        assertEquals("AKIHABARA:IT,[Unknown]", txt2);
    }

    /**
     * 検索条件（FromUser/ToUser）が既に削除されている場合に表示する文字列の検証.
     */
    @Test
    public void testUsersConditionText() {
        page.setCondition(getSearchCorresponConditionForViewText());
        List<ProjectUser> l = new ArrayList<ProjectUser>();
        ProjectUser pu1 = new ProjectUser();
        User u1 = new User();
        u1.setEmpNo("AKB01");
        u1.setNameE("AKIBA TARO");
        pu1.setUser(u1);
        ProjectUser pu2 = new ProjectUser();
        User u2 = new User();
        u2.setEmpNo("SNG01");
        u2.setNameE("SHINA SABURO");
        pu2.setUser(u2);
        l.add(pu1);
        l.add(pu2);
        page.setUserList(l);

        String txt1 = page.getFromUsersConditionText();
        assertEquals("AKIBA TARO/AKB01,[Unknown]/YKH01", txt1);

        String txt2 = page.getToUsersConditionText();
        assertEquals("AKIBA TARO/AKB01,[Unknown]/YKH01", txt2);
    }

    private SearchCorresponCondition getSearchCorresponConditionForViewText() {
        SearchCorresponCondition rt = new SearchCorresponCondition();
        CorresponType ct1 = new CorresponType();
        ct1.setId(1L);
        ct1.setCorresponType("CorresponType1");
        CorresponType ct2 = new CorresponType();
        ct2.setId(3L);
        ct2.setCorresponType("CorresponType3");
        rt.setCorresponTypes(new CorresponType[]{ct1, ct2});

        rt.setCustomFieldNo(3L);

        CorresponGroup g1 = new CorresponGroup();
        g1.setId(1L);
        g1.setName("AKIHABARA:IT");
        CorresponGroup g2 = new CorresponGroup();
        g2.setId(3L);
        g2.setName("YOKOHAMA:IT");
        rt.setFromGroups(new CorresponGroup[]{g1, g2});
        rt.setToGroups(new CorresponGroup[]{g1, g2});

        ProjectUser pu1 = new ProjectUser();
        User u1 = new User();
        u1.setEmpNo("AKB01");
        u1.setNameE("AKIBA TARO");
        pu1.setUser(u1);
        ProjectUser pu2 = new ProjectUser();
        User u2 = new User();
        u2.setEmpNo("YKH01");
        u2.setNameE("HAMA JIRO");
        pu2.setUser(u2);
        rt.setFromUsers(new User[]{u1, u2});
        rt.setToUsers(new User[]{u1, u2});

        return rt;
    }

    /**
     * 初期化アクションのテスト(Cookieにより1ページ表示件数変更済).
     * @throws Exception
     */
    @Test
    public void testInitializePageRowCookie() throws Exception {
        // Cookieに、指定されている表示件数を設定
        page.setPageRowNum(10);
        MockViewHelper.RET_COOKIE_VALUE = "50";

        // Serviceが返すダミーのコレポン文書を設定
        SearchCorresponResult result = new SearchCorresponResult();
        result.setCorresponList(list);
        result.setCount(5);
        MockCorresponSearchService.RET_SEARCH = result;
        MockAbstractPage.RET_PROJID = "MOCK";

        List<CorresponType> expTypeList = createTypeList();
        List<ProjectUser> expUserList = createUserList();
        List<CorresponGroup> expGroupList = createGroupList();
        List<CustomField> expCustomFieldList = createCustomFieldList();
        MockCorresponTypeService.RET_SEARCH = expTypeList;
        MockUserService.RET_SEARCH = expUserList;
        MockCorresponGroupService.RET_SEARCH = expGroupList;
        MockCustomFieldService.RET_SEARCH = expCustomFieldList;

        MockUserPermissionHelper.RET_PROJECT_ADMIN = true;

        page.initialize();

        assertEquals(list, page.getCorresponList());
        assertEquals(5, page.getDataCount());

        // 初期設定を確認
        assertFalse(page.getNext());
        assertFalse(page.getPrevious());
        assertEquals(10, page.getPageIndex());
        assertEquals(5, page.getDataCount());
        assertEquals(1, page.getPageNo());
        assertEquals(50, page.getPageRowNum());
        assertEquals("issued_at", page.getSort());
        // 検索条件
        assertTrue(page.isSimpleSearch());
        assertEquals(Long.valueOf(-1L), page.getType());
        assertEquals(Integer.valueOf(-1), page.getWorkflow());
        assertEquals(ReadStatus.NEW.getValue(), page.getReadStatus());
        assertEquals(expTypeList, page.getTypeList());
        assertArrayEquals(WorkflowStatus.values(), page.getWorkflowList());
        assertArrayEquals(ReadStatus.values(), page.getReadStatusList());
        assertArrayEquals(CorresponStatus.values(), page.getStatusList());
        assertEquals(expUserList, page.getUserList());
        assertArrayEquals(WorkflowProcessStatus.values(), page.getWorkflowProcessesList());
        assertEquals(expGroupList, page.getGroupList());
        assertEquals(expCustomFieldList, page.getCustomFieldList());
     // TODO delete
//        assertFalse(page.isAddressFrom());
//        assertFalse(page.isAddressCc());
//        assertFalse(page.isAddressAttention());
//        assertFalse(page.isAddressPIC());
//        assertFalse(page.isAddressUnreplied());
        assertFalse(page.isUserPreparer());
        assertFalse(page.isUserCc());
        assertFalse(page.isUserAttention());
        assertFalse(page.isUserPic());
        assertFalse(page.isUserUnreplied());
//        assertFalse(page.isGroupFrom());
        assertFalse(page.isGroupTo());
        assertFalse(page.isGroupCc());
        assertEquals("MOCK", page.getCondition().getProjectId());
        assertEquals(1, page.getCondition().getPageNo());
        assertEquals(50, page.getCondition().getPageRowNum());
        assertEquals("issued_at", page.getCondition().getSort());
        assertFalse(page.getCondition().isAscending());
        assertEquals("issued_at DESC NULLS LAST", page.getCondition().getOrderBy());
        assertEquals(page.getCondition(), MockAbstractPage.SET_CONDITION);
        // 表示カラム -- 奇数行がfalse << MockViewHelper.getCookieValue()
        assertFalse(page.getHeader().isNo());
        assertTrue(page.getHeader().isCorresponNo());
        assertFalse(page.getHeader().isPreviousRevision());
        assertTrue(page.getHeader().isFrom());
        assertFalse(page.getHeader().isTo());
        assertTrue(page.getHeader().isType());
        assertFalse(page.getHeader().isSubject());
        assertTrue(page.getHeader().isWorkflow());
        assertFalse(page.getHeader().isCreatedOn());
        assertTrue(page.getHeader().isIssuedOn());
        assertFalse(page.getHeader().isDeadline());
        assertTrue(page.getHeader().isUpdatedOn());
        assertFalse(page.getHeader().isCreatedBy());
        assertTrue(page.getHeader().isIssuedBy());
        assertFalse(page.getHeader().isUpdatedBy());
        assertTrue(page.getHeader().isReplyRequired());
        assertFalse(page.getHeader().isStatus());
    }

    /**
     * 初期化アクションのテスト.
     * セッションに検索条件あり.
     * @throws Exception
     */
    @Test
    public void testInitializeCondition() throws Exception {
        MockAbstractPage.RET_PROJID = "MOCK";
        // Serviceが返すダミーのコレポン文書を設定
        SearchCorresponResult result = new SearchCorresponResult();
        result.setCorresponList(list);
        result.setCount(5);
        MockCorresponSearchService.RET_SEARCH = result;

        List<CorresponType> expTypeList = createTypeList();
        List<ProjectUser> expUserList = createUserList();
        List<CorresponGroup> expGroupList = createGroupList();
        List<CustomField> expCustomFieldList = createCustomFieldList();
        MockCorresponTypeService.RET_SEARCH = expTypeList;
        MockUserService.RET_SEARCH = expUserList;
        MockCorresponGroupService.RET_SEARCH = expGroupList;
        MockCustomFieldService.RET_SEARCH = expCustomFieldList;

        SearchCorresponCondition condition = new SearchCorresponCondition();
        condition.setProjectId("MOCK");
        condition.setSimpleSearch(false);
        condition.setFromId(1L);
        condition.setToId(2L);
        condition.setCorresponNo("Test Correspon");
        CorresponType type = new CorresponType();
        type.setProjectCorresponTypeId(1L);
        type.setCorresponType("TypeA");
        CorresponType[] types = {type};
        Long[] expTypes = {type.getProjectCorresponTypeId()};
        condition.setCorresponTypes(types);
        WorkflowStatus[] workflowStatuses = {WorkflowStatus.DENIED};
        Integer[] expWorkflowStatus = {WorkflowStatus.DENIED.getValue()};
        condition.setWorkflowStatuses(workflowStatuses);
        ReadStatus[] readStatuses = {ReadStatus.NEW};
        Integer[] expReadStatuses = {ReadStatus.NEW.getValue()};
        condition.setReadStatuses(readStatuses);
        CorresponStatus[] corresponStatuses = {CorresponStatus.CLOSED};
        Integer[] expCorresponStatus = {CorresponStatus.CLOSED.getValue()};
        condition.setCorresponStatuses(corresponStatuses);
        User user = new User();
        user.setEmpNo("00001");
        user.setNameE("A User");
        User[] users = {user};
        String[] expUser = {user.getEmpNo()};
//        condition.setAddressUsers(users);
//        condition.setAddressFrom(true);
//        condition.setAddressCc(true);
//        condition.setAddressAttention(true);
//        condition.setAddressPersonInCharge(true);
//        condition.setAddressUnreplied(true);
        condition.setFromUsers(users);
        condition.setUserPreparer(true);
        condition.setToUsers(users);
        condition.setUserCc(true);
        condition.setUserAttention(true);
        condition.setUserPic(true);
        condition.setUserUnreplied(true);
//        condition.setWorkflowUsers(users);
        WorkflowProcessStatus[] workflowProcessStatuses = {WorkflowProcessStatus.DENIED};
        Integer[] expWorkflowProcessStatus = {WorkflowProcessStatus.DENIED.getValue()};
        condition.setWorkflowProcessStatuses(workflowProcessStatuses);
        CorresponGroup group = new CorresponGroup();
        group.setId(1L);
        group.setName("A Group");
        CorresponGroup[] groups = {group};
        Long[] expGroup = {group.getId()};
//        condition.setCorresponGroups(groups);
//        condition.setGroupFrom(true);
        condition.setFromGroups(groups);
        condition.setToGroups(groups);
        condition.setGroupTo(true);
        condition.setGroupCc(true);
        Date date1 = new GregorianCalendar(2009, 3, 1, 10, 20, 30).getTime();
        condition.setFromCreatedOn(date1);
        Date date2 = new GregorianCalendar(2009, 3, 2, 10, 20, 30).getTime();
        condition.setToCreatedOn(date2);
        Date date3 = new GregorianCalendar(2009, 3, 3, 10, 20, 30).getTime();
        condition.setFromIssuedOn(date3);
        Date date4 = new GregorianCalendar(2009, 3, 4, 10, 20, 30).getTime();
        condition.setToIssuedOn(date4);
        Date date5 = new GregorianCalendar(2009, 3, 5, 10, 20, 30).getTime();
        condition.setFromDeadlineForReply(date5);
        Date date6 = new GregorianCalendar(2009, 3, 6, 10, 20, 30).getTime();
        condition.setToDeadlineForReply(date6);
        condition.setCustomFieldNo(1L);
        condition.setCustomFieldValue("Field Value");

        // ここで設定した検索条件は無視され、
        // デフォルト("id", true, 1)が設定されて検索される
        condition.setSort("correspon_type");
        condition.setAscending(false);
        condition.setPageNo(2);

        MockAbstractPage.RET_CONDITION = condition;

        page.initialize();

        assertEquals(list, page.getCorresponList());
        assertEquals(5, page.getDataCount());

        // 初期設定を確認
        assertFalse(page.getNext());
        assertFalse(page.getPrevious());
        assertEquals(10, page.getPageIndex());
        assertEquals(5, page.getDataCount());
        assertEquals(1, page.getPageNo());
        assertEquals(10, page.getPageRowNum());
        assertEquals("issued_at", page.getSort());
        assertFalse(page.isAscending());
        // 検索条件
        // 初期表示時は常にSimple Search欄を表示するため、SimpleSearch = trueとなる
        // 但し検索処理はAdvanced Searchで行われる
        assertTrue(page.isSimpleSearch());
        // Advanced Searchで設定した条件が
        // Simple Searchにも反映されている
        assertEquals(expTypes[0], page.getType());
        assertEquals(expWorkflowStatus[0], page.getWorkflow());
        assertEquals(expReadStatuses[0], page.getReadStatus());

        assertEquals(expTypeList, page.getTypeList());
        assertArrayEquals(WorkflowStatus.values(), page.getWorkflowList());
        assertArrayEquals(ReadStatus.values(), page.getReadStatusList());
        assertArrayEquals(CorresponStatus.values(), page.getStatusList());
        assertEquals(expUserList, page.getUserList());
        assertArrayEquals(WorkflowProcessStatus.values(), page.getWorkflowProcessesList());
        assertEquals(expGroupList, page.getGroupList());
        assertEquals(expCustomFieldList, page.getCustomFieldList());
        assertEquals("1", page.getFromNo());
        assertEquals("2", page.getToNo());
        assertEquals("Test Correspon", page.getCorresponNo());
        assertArrayEquals(expTypes, page.getTypes());
        assertArrayEquals(expWorkflowStatus, page.getWorkflowStatuses());
        assertArrayEquals(expReadStatuses, page.getReadStatuses());
        assertArrayEquals(expCorresponStatus, page.getStatuses());
//        assertArrayEquals(expUser, page.getAddressUsers());
     // TODO delete
//        assertTrue(page.isAddressFrom());
//        assertTrue(page.isAddressCc());
//        assertTrue(page.isAddressAttention());
//        assertTrue(page.isAddressPIC());
//        assertTrue(page.isAddressUnreplied());
        assertArrayEquals(expUser, page.getFromUsers());
        assertArrayEquals(expUser, page.getToUsers());
        assertTrue(page.isUserPreparer());
        assertTrue(page.isUserCc());
        assertTrue(page.isUserAttention());
        assertTrue(page.isUserPic());
        assertTrue(page.isUserUnreplied());
//        assertArrayEquals(expUser, page.getWorkflowUsers());
        assertArrayEquals(expWorkflowProcessStatus, page.getWorkflowProcesses());
//        assertArrayEquals(expGroup, page.getGroups());
//        assertTrue(page.isGroupFrom());
        assertArrayEquals(expGroup, page.getFromGroups());
        assertArrayEquals(expGroup, page.getToGroups());
        assertTrue(page.isGroupTo());
        assertTrue(page.isGroupCc());
        assertEquals("01-Apr-2009", page.getFromCreated());
        assertEquals("02-Apr-2009", page.getToCreated());
        assertEquals("03-Apr-2009", page.getFromIssued());
        assertEquals("04-Apr-2009", page.getToIssued());
        assertEquals("05-Apr-2009", page.getFromReply());
        assertEquals("06-Apr-2009", page.getToReply());
        assertEquals(Long.valueOf(1L), page.getCustomFieldNo());
        assertEquals("Field Value", page.getCustomFieldValue());
        assertEquals("MOCK", page.getCondition().getProjectId());
        assertEquals(1, page.getCondition().getPageNo());
        assertEquals(10, page.getCondition().getPageRowNum());
        assertEquals("issued_at", page.getCondition().getSort());
        assertFalse(page.getCondition().isAscending());
        assertEquals("issued_at DESC NULLS LAST", page.getCondition().getOrderBy());
        assertEquals(page.getCondition(), MockAbstractPage.SET_CONDITION);
        // 表示カラム -- 奇数行がfalse << MockViewHelper.getCookieValue()
        assertFalse(page.getHeader().isNo());
        assertTrue(page.getHeader().isCorresponNo());
        assertFalse(page.getHeader().isPreviousRevision());
        assertTrue(page.getHeader().isFrom());
        assertFalse(page.getHeader().isTo());
        assertTrue(page.getHeader().isType());
        assertFalse(page.getHeader().isSubject());
        assertTrue(page.getHeader().isWorkflow());
        assertFalse(page.getHeader().isCreatedOn());
        assertTrue(page.getHeader().isIssuedOn());
        assertFalse(page.getHeader().isDeadline());
        assertTrue(page.getHeader().isUpdatedOn());
        assertFalse(page.getHeader().isCreatedBy());
        assertTrue(page.getHeader().isIssuedBy());
        assertFalse(page.getHeader().isUpdatedBy());
        assertTrue(page.getHeader().isReplyRequired());
        assertFalse(page.getHeader().isStatus());
    }

    /**
     * 初期化アクションのテスト.
     * セッションに検索条件あり(Sort条件保持).
     * @throws Exception
     */
    @Test
    public void testInitializeConditionSort() throws Exception {
        // Serviceが返すダミーのコレポン文書を設定
        SearchCorresponResult result = new SearchCorresponResult();
        result.setCorresponList(list);
        result.setCount(5);
        MockCorresponSearchService.RET_SEARCH = result;

        List<CorresponType> expTypeList = createTypeList();
        List<ProjectUser> expUserList = createUserList();
        List<CorresponGroup> expGroupList = createGroupList();
        List<CustomField> expCustomFieldList = createCustomFieldList();
        MockCorresponTypeService.RET_SEARCH = expTypeList;
        MockUserService.RET_SEARCH = expUserList;
        MockCorresponGroupService.RET_SEARCH = expGroupList;
        MockCustomFieldService.RET_SEARCH = expCustomFieldList;

        SearchCorresponCondition condition = new SearchCorresponCondition();
        condition.setProjectId("MOCK");
        condition.setSimpleSearch(false);
        condition.setFromId(1L);
        condition.setToId(2L);
        condition.setCorresponNo("Test Correspon");
        CorresponType type = new CorresponType();
        type.setProjectCorresponTypeId(1L);
        type.setCorresponType("TypeA");
        CorresponType[] types = {type};
        Long[] expTypes = {type.getProjectCorresponTypeId()};
        condition.setCorresponTypes(types);
        WorkflowStatus[] workflowStatuses = {WorkflowStatus.DENIED};
        Integer[] expWorkflowStatus = {WorkflowStatus.DENIED.getValue()};
        condition.setWorkflowStatuses(workflowStatuses);
        ReadStatus[] readStatuses = {ReadStatus.NEW};
        Integer[] expReadStatuses = {ReadStatus.NEW.getValue()};
        condition.setReadStatuses(readStatuses);
        CorresponStatus[] corresponStatuses = {CorresponStatus.CLOSED};
        Integer[] expCorresponStatus = {CorresponStatus.CLOSED.getValue()};
        condition.setCorresponStatuses(corresponStatuses);
        User user = new User();
        user.setEmpNo("00001");
        user.setNameE("A User");
        User[] users = {user};
        String[] expUser = {user.getEmpNo()};
//        condition.setAddressUsers(users);
//        condition.setAddressFrom(true);
//        condition.setAddressCc(true);
//        condition.setAddressAttention(true);
//        condition.setAddressPersonInCharge(true);
//        condition.setAddressUnreplied(true);
        condition.setFromUsers(users);
        condition.setUserPreparer(true);
        condition.setToUsers(users);
        condition.setUserCc(true);
        condition.setUserAttention(true);
        condition.setUserPic(true);
        condition.setUserUnreplied(true);
//        condition.setWorkflowUsers(users);
        WorkflowProcessStatus[] workflowProcessStatuses = {WorkflowProcessStatus.DENIED};
        Integer[] expWorkflowProcessStatus = {WorkflowProcessStatus.DENIED.getValue()};
        condition.setWorkflowProcessStatuses(workflowProcessStatuses);
        CorresponGroup group = new CorresponGroup();
        group.setId(1L);
        group.setName("A Group");
        CorresponGroup[] groups = {group};
        Long[] expGroup = {group.getId()};
//        condition.setCorresponGroups(groups);
//        condition.setGroupFrom(true);
        condition.setFromGroups(groups);
        condition.setToGroups(groups);
        condition.setGroupTo(true);
        condition.setGroupCc(true);
        Date date1 = new GregorianCalendar(2009, 3, 1, 10, 20, 30).getTime();
        condition.setFromCreatedOn(date1);
        Date date2 = new GregorianCalendar(2009, 3, 2, 10, 20, 30).getTime();
        condition.setToCreatedOn(date2);
        Date date3 = new GregorianCalendar(2009, 3, 3, 10, 20, 30).getTime();
        condition.setFromIssuedOn(date3);
        Date date4 = new GregorianCalendar(2009, 3, 4, 10, 20, 30).getTime();
        condition.setToIssuedOn(date4);
        Date date5 = new GregorianCalendar(2009, 3, 5, 10, 20, 30).getTime();
        condition.setFromDeadlineForReply(date5);
        Date date6 = new GregorianCalendar(2009, 3, 6, 10, 20, 30).getTime();
        condition.setToDeadlineForReply(date6);
        condition.setCustomFieldNo(1L);
        condition.setCustomFieldValue("Field Value");

        condition.setSort("correspon_type");
        condition.setAscending(false);
        condition.setPageNo(2);

        MockAbstractPage.RET_CONDITION = condition;

        page.setSessionSort("1");
        page.initialize();

        assertEquals(list, page.getCorresponList());
        assertEquals(5, page.getDataCount());

        // 初期設定を確認
        assertFalse(page.getNext());
        assertFalse(page.getPrevious());
        assertEquals(10, page.getPageIndex());
        assertEquals(5, page.getDataCount());
        assertEquals(1, page.getPageNo());
        assertEquals(10, page.getPageRowNum());
        assertEquals("correspon_type", page.getSort());
        assertFalse(page.isAscending());
        // 検索条件
        // 初期表示時は常にSimple Search欄を表示するため、SimpleSearch = trueとなる
        // 但し検索処理はAdvanced Searchで行われる
        assertTrue(page.isSimpleSearch());
        // Advanced Searchで設定した条件が
        // Simple Searchにも反映されている
        assertEquals(expTypes[0], page.getType());
        assertEquals(expWorkflowStatus[0], page.getWorkflow());
        assertEquals(expReadStatuses[0], page.getReadStatus());

        assertEquals(expTypeList, page.getTypeList());
        assertArrayEquals(WorkflowStatus.values(), page.getWorkflowList());
        assertArrayEquals(ReadStatus.values(), page.getReadStatusList());
        assertArrayEquals(CorresponStatus.values(), page.getStatusList());
        assertEquals(expUserList, page.getUserList());
        assertArrayEquals(WorkflowProcessStatus.values(), page.getWorkflowProcessesList());
        assertEquals(expGroupList, page.getGroupList());
        assertEquals(expCustomFieldList, page.getCustomFieldList());
        assertEquals("1", page.getFromNo());
        assertEquals("2", page.getToNo());
        assertEquals("Test Correspon", page.getCorresponNo());
        assertArrayEquals(expTypes, page.getTypes());
        assertArrayEquals(expWorkflowStatus, page.getWorkflowStatuses());
        assertArrayEquals(expReadStatuses, page.getReadStatuses());
        assertArrayEquals(expCorresponStatus, page.getStatuses());
//        assertArrayEquals(expUser, page.getAddressUsers());
     // TODO delete
//        assertTrue(page.isAddressFrom());
//        assertTrue(page.isAddressCc());
//        assertTrue(page.isAddressAttention());
//        assertTrue(page.isAddressPIC());
//        assertTrue(page.isAddressUnreplied());
        assertArrayEquals(expUser, page.getFromUsers());
        assertArrayEquals(expUser, page.getToUsers());
        assertTrue(page.isUserPreparer());
        assertTrue(page.isUserCc());
        assertTrue(page.isUserAttention());
        assertTrue(page.isUserPic());
        assertTrue(page.isUserUnreplied());
//        assertArrayEquals(expUser, page.getWorkflowUsers());
        assertArrayEquals(expWorkflowProcessStatus, page.getWorkflowProcesses());
//        assertArrayEquals(expGroup, page.getGroups());
//        assertTrue(page.isGroupFrom());
        assertArrayEquals(expGroup, page.getFromGroups());
        assertArrayEquals(expGroup, page.getToGroups());
        assertTrue(page.isGroupTo());
        assertTrue(page.isGroupCc());
        assertEquals("01-Apr-2009", page.getFromCreated());
        assertEquals("02-Apr-2009", page.getToCreated());
        assertEquals("03-Apr-2009", page.getFromIssued());
        assertEquals("04-Apr-2009", page.getToIssued());
        assertEquals("05-Apr-2009", page.getFromReply());
        assertEquals("06-Apr-2009", page.getToReply());
        assertEquals(Long.valueOf(1L), page.getCustomFieldNo());
        assertEquals("Field Value", page.getCustomFieldValue());
        assertEquals("MOCK", page.getCondition().getProjectId());
        assertEquals(1, page.getCondition().getPageNo());
        assertEquals(10, page.getCondition().getPageRowNum());
        assertEquals("correspon_type", page.getCondition().getSort());
        assertFalse(page.getCondition().isAscending());
        assertEquals("correspon_type DESC NULLS LAST", page.getCondition().getOrderBy());
        assertEquals(page.getCondition(), MockAbstractPage.SET_CONDITION);
        // 表示カラム -- 奇数行がfalse << MockViewHelper.getCookieValue()
        assertFalse(page.getHeader().isNo());
        assertTrue(page.getHeader().isCorresponNo());
        assertFalse(page.getHeader().isPreviousRevision());
        assertTrue(page.getHeader().isFrom());
        assertFalse(page.getHeader().isTo());
        assertTrue(page.getHeader().isType());
        assertFalse(page.getHeader().isSubject());
        assertTrue(page.getHeader().isWorkflow());
        assertFalse(page.getHeader().isCreatedOn());
        assertTrue(page.getHeader().isIssuedOn());
        assertFalse(page.getHeader().isDeadline());
        assertTrue(page.getHeader().isUpdatedOn());
        assertFalse(page.getHeader().isCreatedBy());
        assertTrue(page.getHeader().isIssuedBy());
        assertFalse(page.getHeader().isUpdatedBy());
        assertTrue(page.getHeader().isReplyRequired());
        assertFalse(page.getHeader().isStatus());
    }


    /**
     * 初期化アクションのテスト.
     * セッションに検索条件あり(PageNo条件保持).
     * @throws Exception
     */
    @Test
    public void testInitializeConditionPageNo() throws Exception {
        // Serviceが返すダミーのコレポン文書を設定
        SearchCorresponResult result = new SearchCorresponResult();
        result.setCorresponList(list);
        result.setCount(5);
        MockCorresponSearchService.RET_SEARCH = result;

        List<CorresponType> expTypeList = createTypeList();
        List<ProjectUser> expUserList = createUserList();
        List<CorresponGroup> expGroupList = createGroupList();
        List<CustomField> expCustomFieldList = createCustomFieldList();
        MockCorresponTypeService.RET_SEARCH = expTypeList;
        MockUserService.RET_SEARCH = expUserList;
        MockCorresponGroupService.RET_SEARCH = expGroupList;
        MockCustomFieldService.RET_SEARCH = expCustomFieldList;

        SearchCorresponCondition condition = new SearchCorresponCondition();
        condition.setProjectId("MOCK");
        condition.setSimpleSearch(false);
        condition.setFromId(1L);
        condition.setToId(2L);
        condition.setCorresponNo("Test Correspon");
        CorresponType type = new CorresponType();
        type.setProjectCorresponTypeId(1L);
        type.setCorresponType("TypeA");
        CorresponType[] types = {type};
        Long[] expTypes = {type.getProjectCorresponTypeId()};
        condition.setCorresponTypes(types);
        WorkflowStatus[] workflowStatuses = {WorkflowStatus.DENIED};
        Integer[] expWorkflowStatus = {WorkflowStatus.DENIED.getValue()};
        condition.setWorkflowStatuses(workflowStatuses);
        ReadStatus[] readStatuses = {ReadStatus.NEW};
        Integer[] expReadStatuses = {ReadStatus.NEW.getValue()};
        condition.setReadStatuses(readStatuses);
        CorresponStatus[] corresponStatuses = {CorresponStatus.CLOSED};
        Integer[] expCorresponStatus = {CorresponStatus.CLOSED.getValue()};
        condition.setCorresponStatuses(corresponStatuses);
        User user = new User();
        user.setEmpNo("00001");
        user.setNameE("A User");
        User[] users = {user};
        String[] expUser = {user.getEmpNo()};
//        condition.setAddressUsers(users);
//        condition.setAddressFrom(true);
//        condition.setAddressCc(true);
//        condition.setAddressAttention(true);
//        condition.setAddressPersonInCharge(true);
//        condition.setAddressUnreplied(true);
        condition.setFromUsers(users);
        condition.setUserPreparer(true);
        condition.setToUsers(users);
        condition.setUserCc(true);
        condition.setUserAttention(true);
        condition.setUserPic(true);
        condition.setUserUnreplied(true);
//        condition.setWorkflowUsers(users);
        WorkflowProcessStatus[] workflowProcessStatuses = {WorkflowProcessStatus.DENIED};
        Integer[] expWorkflowProcessStatus = {WorkflowProcessStatus.DENIED.getValue()};
        condition.setWorkflowProcessStatuses(workflowProcessStatuses);
        CorresponGroup group = new CorresponGroup();
        group.setId(1L);
        group.setName("A Group");
        CorresponGroup[] groups = {group};
        Long[] expGroup = {group.getId()};
//        condition.setCorresponGroups(groups);
//        condition.setGroupFrom(true);
        condition.setFromGroups(groups);
        condition.setToGroups(groups);
        condition.setGroupTo(true);
        condition.setGroupCc(true);
        Date date1 = new GregorianCalendar(2009, 3, 1, 10, 20, 30).getTime();
        condition.setFromCreatedOn(date1);
        Date date2 = new GregorianCalendar(2009, 3, 2, 10, 20, 30).getTime();
        condition.setToCreatedOn(date2);
        Date date3 = new GregorianCalendar(2009, 3, 3, 10, 20, 30).getTime();
        condition.setFromIssuedOn(date3);
        Date date4 = new GregorianCalendar(2009, 3, 4, 10, 20, 30).getTime();
        condition.setToIssuedOn(date4);
        Date date5 = new GregorianCalendar(2009, 3, 5, 10, 20, 30).getTime();
        condition.setFromDeadlineForReply(date5);
        Date date6 = new GregorianCalendar(2009, 3, 6, 10, 20, 30).getTime();
        condition.setToDeadlineForReply(date6);
        condition.setCustomFieldNo(1L);
        condition.setCustomFieldValue("Field Value");

        condition.setSort("correspon_type");
        condition.setAscending(false);
        condition.setPageNo(2);

        MockAbstractPage.RET_CONDITION = condition;

        page.setSessionPageNo("1");
        page.initialize();

        assertEquals(list, page.getCorresponList());
        assertEquals(5, page.getDataCount());

        // 初期設定を確認
        assertFalse(page.getNext());
        assertTrue(page.getPrevious());
        assertEquals(10, page.getPageIndex());
        assertEquals(5, page.getDataCount());
        assertEquals(2, page.getPageNo());
        assertEquals(10, page.getPageRowNum());
        assertEquals("issued_at", page.getSort());
        assertFalse(page.isAscending());
        // 検索条件
        // 初期表示時は常にSimple Search欄を表示するため、SimpleSearch = trueとなる
        // 但し検索処理はAdvanced Searchで行われる
        assertTrue(page.isSimpleSearch());
        // Advanced Searchで設定した条件が
        // Simple Searchにも反映されている
        assertEquals(expTypes[0], page.getType());
        assertEquals(expWorkflowStatus[0], page.getWorkflow());
        assertEquals(expReadStatuses[0], page.getReadStatus());

        assertEquals(expTypeList, page.getTypeList());
        assertArrayEquals(WorkflowStatus.values(), page.getWorkflowList());
        assertArrayEquals(ReadStatus.values(), page.getReadStatusList());
        assertArrayEquals(CorresponStatus.values(), page.getStatusList());
        assertEquals(expUserList, page.getUserList());
        assertArrayEquals(WorkflowProcessStatus.values(), page.getWorkflowProcessesList());
        assertEquals(expGroupList, page.getGroupList());
        assertEquals(expCustomFieldList, page.getCustomFieldList());
        assertEquals("1", page.getFromNo());
        assertEquals("2", page.getToNo());
        assertEquals("Test Correspon", page.getCorresponNo());
        assertArrayEquals(expTypes, page.getTypes());
        assertArrayEquals(expWorkflowStatus, page.getWorkflowStatuses());
        assertArrayEquals(expReadStatuses, page.getReadStatuses());
        assertArrayEquals(expCorresponStatus, page.getStatuses());
//        assertArrayEquals(expUser, page.getAddressUsers());
     // TODO delete
//        assertTrue(page.isAddressFrom());
//        assertTrue(page.isAddressCc());
//        assertTrue(page.isAddressAttention());
//        assertTrue(page.isAddressPIC());
//        assertTrue(page.isAddressUnreplied());
        assertArrayEquals(expUser, page.getFromUsers());
        assertArrayEquals(expUser, page.getToUsers());
        assertTrue(page.isUserPreparer());
        assertTrue(page.isUserCc());
        assertTrue(page.isUserAttention());
        assertTrue(page.isUserPic());
        assertTrue(page.isUserUnreplied());
//        assertArrayEquals(expUser, page.getWorkflowUsers());
        assertArrayEquals(expWorkflowProcessStatus, page.getWorkflowProcesses());
//        assertArrayEquals(expGroup, page.getGroups());
//        assertTrue(page.isGroupFrom());
        assertArrayEquals(expGroup, page.getFromGroups());
        assertArrayEquals(expGroup, page.getToGroups());
        assertTrue(page.isGroupTo());
        assertTrue(page.isGroupCc());
        assertEquals("01-Apr-2009", page.getFromCreated());
        assertEquals("02-Apr-2009", page.getToCreated());
        assertEquals("03-Apr-2009", page.getFromIssued());
        assertEquals("04-Apr-2009", page.getToIssued());
        assertEquals("05-Apr-2009", page.getFromReply());
        assertEquals("06-Apr-2009", page.getToReply());
        assertEquals(Long.valueOf(1L), page.getCustomFieldNo());
        assertEquals("Field Value", page.getCustomFieldValue());
        assertEquals("MOCK", page.getCondition().getProjectId());
        assertEquals(2, page.getCondition().getPageNo());
        assertEquals(10, page.getCondition().getPageRowNum());
        assertEquals("issued_at", page.getCondition().getSort());
        assertFalse(page.getCondition().isAscending());
        assertEquals("issued_at DESC NULLS LAST", page.getCondition().getOrderBy());
        assertEquals(page.getCondition(), MockAbstractPage.SET_CONDITION);
        // 表示カラム -- 奇数行がfalse << MockViewHelper.getCookieValue()
        assertFalse(page.getHeader().isNo());
        assertTrue(page.getHeader().isCorresponNo());
        assertFalse(page.getHeader().isPreviousRevision());
        assertTrue(page.getHeader().isFrom());
        assertFalse(page.getHeader().isTo());
        assertTrue(page.getHeader().isType());
        assertFalse(page.getHeader().isSubject());
        assertTrue(page.getHeader().isWorkflow());
        assertFalse(page.getHeader().isCreatedOn());
        assertTrue(page.getHeader().isIssuedOn());
        assertFalse(page.getHeader().isDeadline());
        assertTrue(page.getHeader().isUpdatedOn());
        assertFalse(page.getHeader().isCreatedBy());
        assertTrue(page.getHeader().isIssuedBy());
        assertFalse(page.getHeader().isUpdatedBy());
        assertTrue(page.getHeader().isReplyRequired());
        assertFalse(page.getHeader().isStatus());
    }

    /**
     * 初期化アクションのテスト.
     * セッションに検索条件あり(Sort,PageNo条件保持).
     * @throws Exception
     */
    @Test
    public void testInitializeConditionSortPageNo() throws Exception {
        // Serviceが返すダミーのコレポン文書を設定
        SearchCorresponResult result = new SearchCorresponResult();
        result.setCorresponList(list);
        result.setCount(5);
        MockCorresponSearchService.RET_SEARCH = result;

        List<CorresponType> expTypeList = createTypeList();
        List<ProjectUser> expUserList = createUserList();
        List<CorresponGroup> expGroupList = createGroupList();
        List<CustomField> expCustomFieldList = createCustomFieldList();
        MockCorresponTypeService.RET_SEARCH = expTypeList;
        MockUserService.RET_SEARCH = expUserList;
        MockCorresponGroupService.RET_SEARCH = expGroupList;
        MockCustomFieldService.RET_SEARCH = expCustomFieldList;

        SearchCorresponCondition condition = new SearchCorresponCondition();
        condition.setProjectId("MOCK");
        condition.setSimpleSearch(false);
        condition.setFromId(1L);
        condition.setToId(2L);
        condition.setCorresponNo("Test Correspon");
        CorresponType type = new CorresponType();
        type.setProjectCorresponTypeId(1L);
        type.setCorresponType("TypeA");
        CorresponType[] types = {type};
        Long[] expTypes = {type.getProjectCorresponTypeId()};
        condition.setCorresponTypes(types);
        WorkflowStatus[] workflowStatuses = {WorkflowStatus.DENIED};
        Integer[] expWorkflowStatus = {WorkflowStatus.DENIED.getValue()};
        condition.setWorkflowStatuses(workflowStatuses);
        ReadStatus[] readStatuses = {ReadStatus.NEW};
        Integer[] expReadStatuses = {ReadStatus.NEW.getValue()};
        condition.setReadStatuses(readStatuses);
        CorresponStatus[] corresponStatuses = {CorresponStatus.CLOSED};
        Integer[] expCorresponStatus = {CorresponStatus.CLOSED.getValue()};
        condition.setCorresponStatuses(corresponStatuses);
        User user = new User();
        user.setEmpNo("00001");
        user.setNameE("A User");
        User[] users = {user};
        String[] expUser = {user.getEmpNo()};
//        condition.setAddressUsers(users);
//        condition.setAddressFrom(true);
//        condition.setAddressCc(true);
//        condition.setAddressAttention(true);
//        condition.setAddressPersonInCharge(true);
//        condition.setAddressUnreplied(true);
        condition.setFromUsers(users);
        condition.setUserPreparer(true);
        condition.setToUsers(users);
        condition.setUserCc(true);
        condition.setUserAttention(true);
        condition.setUserPic(true);
        condition.setUserUnreplied(true);
//        condition.setWorkflowUsers(users);
        WorkflowProcessStatus[] workflowProcessStatuses = {WorkflowProcessStatus.DENIED};
        Integer[] expWorkflowProcessStatus = {WorkflowProcessStatus.DENIED.getValue()};
        condition.setWorkflowProcessStatuses(workflowProcessStatuses);
        CorresponGroup group = new CorresponGroup();
        group.setId(1L);
        group.setName("A Group");
        CorresponGroup[] groups = {group};
        Long[] expGroup = {group.getId()};
//        condition.setCorresponGroups(groups);
//        condition.setGroupFrom(true);
        condition.setFromGroups(groups);
        condition.setToGroups(groups);
        condition.setGroupTo(true);
        condition.setGroupCc(true);
        Date date1 = new GregorianCalendar(2009, 3, 1, 10, 20, 30).getTime();
        condition.setFromCreatedOn(date1);
        Date date2 = new GregorianCalendar(2009, 3, 2, 10, 20, 30).getTime();
        condition.setToCreatedOn(date2);
        Date date3 = new GregorianCalendar(2009, 3, 3, 10, 20, 30).getTime();
        condition.setFromIssuedOn(date3);
        Date date4 = new GregorianCalendar(2009, 3, 4, 10, 20, 30).getTime();
        condition.setToIssuedOn(date4);
        Date date5 = new GregorianCalendar(2009, 3, 5, 10, 20, 30).getTime();
        condition.setFromDeadlineForReply(date5);
        Date date6 = new GregorianCalendar(2009, 3, 6, 10, 20, 30).getTime();
        condition.setToDeadlineForReply(date6);
        condition.setCustomFieldNo(1L);
        condition.setCustomFieldValue("Field Value");

        condition.setSort("correspon_type");
        condition.setAscending(false);
        condition.setPageNo(2);

        MockAbstractPage.RET_CONDITION = condition;

        page.setSessionSort("1");
        page.setSessionPageNo("1");
        page.initialize();

        assertEquals(list, page.getCorresponList());
        assertEquals(5, page.getDataCount());

        // 初期設定を確認
        assertFalse(page.getNext());
        assertTrue(page.getPrevious());
        assertEquals(10, page.getPageIndex());
        assertEquals(5, page.getDataCount());
        assertEquals(2, page.getPageNo());
        assertEquals(10, page.getPageRowNum());
        assertEquals("correspon_type", page.getSort());
        assertFalse(page.isAscending());
        // 検索条件
        // 初期表示時は常にSimple Search欄を表示するため、SimpleSearch = trueとなる
        // 但し検索処理はAdvanced Searchで行われる
        assertTrue(page.isSimpleSearch());
        // Advanced Searchで設定した条件が
        // Simple Searchにも反映されている
        assertEquals(expTypes[0], page.getType());
        assertEquals(expWorkflowStatus[0], page.getWorkflow());
        assertEquals(expReadStatuses[0], page.getReadStatus());

        assertEquals(expTypeList, page.getTypeList());
        assertArrayEquals(WorkflowStatus.values(), page.getWorkflowList());
        assertArrayEquals(ReadStatus.values(), page.getReadStatusList());
        assertArrayEquals(CorresponStatus.values(), page.getStatusList());
        assertEquals(expUserList, page.getUserList());
        assertArrayEquals(WorkflowProcessStatus.values(), page.getWorkflowProcessesList());
        assertEquals(expGroupList, page.getGroupList());
        assertEquals(expCustomFieldList, page.getCustomFieldList());
        assertEquals("1", page.getFromNo());
        assertEquals("2", page.getToNo());
        assertEquals("Test Correspon", page.getCorresponNo());
        assertArrayEquals(expTypes, page.getTypes());
        assertArrayEquals(expWorkflowStatus, page.getWorkflowStatuses());
        assertArrayEquals(expReadStatuses, page.getReadStatuses());
        assertArrayEquals(expCorresponStatus, page.getStatuses());
//        assertArrayEquals(expUser, page.getAddressUsers());
     // TODO delete
//        assertTrue(page.isAddressFrom());
//        assertTrue(page.isAddressCc());
//        assertTrue(page.isAddressAttention());
//        assertTrue(page.isAddressPIC());
//        assertTrue(page.isAddressUnreplied());
        assertArrayEquals(expUser, page.getFromUsers());
        assertArrayEquals(expUser, page.getToUsers());
        assertTrue(page.isUserPreparer());
        assertTrue(page.isUserCc());
        assertTrue(page.isUserAttention());
        assertTrue(page.isUserPic());
        assertTrue(page.isUserUnreplied());
//        assertArrayEquals(expUser, page.getWorkflowUsers());
        assertArrayEquals(expWorkflowProcessStatus, page.getWorkflowProcesses());
//        assertArrayEquals(expGroup, page.getGroups());
//        assertTrue(page.isGroupFrom());
        assertArrayEquals(expGroup, page.getFromGroups());
        assertArrayEquals(expGroup, page.getToGroups());
        assertTrue(page.isGroupTo());
        assertTrue(page.isGroupCc());
        assertEquals("01-Apr-2009", page.getFromCreated());
        assertEquals("02-Apr-2009", page.getToCreated());
        assertEquals("03-Apr-2009", page.getFromIssued());
        assertEquals("04-Apr-2009", page.getToIssued());
        assertEquals("05-Apr-2009", page.getFromReply());
        assertEquals("06-Apr-2009", page.getToReply());
        assertEquals(Long.valueOf(1L), page.getCustomFieldNo());
        assertEquals("Field Value", page.getCustomFieldValue());
        assertEquals("MOCK", page.getCondition().getProjectId());
        assertEquals(2, page.getCondition().getPageNo());
        assertEquals(10, page.getCondition().getPageRowNum());
        assertEquals("correspon_type", page.getCondition().getSort());
        assertFalse(page.getCondition().isAscending());
        assertEquals(page.getCondition(), MockAbstractPage.SET_CONDITION);
        // 表示カラム -- 奇数行がfalse << MockViewHelper.getCookieValue()
        assertFalse(page.getHeader().isNo());
        assertTrue(page.getHeader().isCorresponNo());
        assertFalse(page.getHeader().isPreviousRevision());
        assertTrue(page.getHeader().isFrom());
        assertFalse(page.getHeader().isTo());
        assertTrue(page.getHeader().isType());
        assertFalse(page.getHeader().isSubject());
        assertTrue(page.getHeader().isWorkflow());
        assertFalse(page.getHeader().isCreatedOn());
        assertTrue(page.getHeader().isIssuedOn());
        assertFalse(page.getHeader().isDeadline());
        assertTrue(page.getHeader().isUpdatedOn());
        assertFalse(page.getHeader().isCreatedBy());
        assertTrue(page.getHeader().isIssuedBy());
        assertFalse(page.getHeader().isUpdatedBy());
        assertTrue(page.getHeader().isReplyRequired());
        assertFalse(page.getHeader().isStatus());
    }

    /**
     * 検索アクションのテスト（シンプルな検索）.
     * @throws Exception
     */
    @Test
    public void testSearchSimple() throws Exception {
        // Serviceが返すダミーのコレポン文書を設定
        SearchCorresponResult result = new SearchCorresponResult();
        result.setCorresponList(list);
        result.setCount(5);
        MockCorresponSearchService.RET_SEARCH = result;

        SearchCorresponCondition condition = new SearchCorresponCondition();
        condition.setProjectId("MOCK");
        page.setCondition(condition);
        page.setSort("id");
        page.setAscending(true);

        page.setTypeList(createTypeList());
        page.setWorkflowList(WorkflowStatus.values());
        page.setReadStatusList(ReadStatus.values());
        page.setStatusList(CorresponStatus.values());
        page.setUserList(createUserList());
        page.setWorkflowProcessesList(WorkflowProcessStatus.values());
        page.setGroupList(createGroupList());
        page.setCustomFieldList(createCustomFieldList());

        // 検索条件
        page.setSimpleSearch(true);
        page.setType(2L);
        page.setWorkflow(WorkflowStatus.DRAFT.getValue());
        page.setReadStatus(-1);

        page.search();

        assertEquals(list, page.getCorresponList());
        assertEquals(5, page.getDataCount());

        // 初期設定を確認
        assertFalse(page.getNext());
        assertFalse(page.getPrevious());
        assertEquals(10, page.getPageIndex());
        assertEquals(5, page.getDataCount());
        assertEquals(1, page.getPageNo());
        assertEquals(10, page.getPageRowNum());
        assertEquals("id", page.getSort());
        // 検索条件
        assertTrue(page.isSimpleSearch());
        assertEquals("TypeB",
                     page.getCondition().getCorresponTypes()[0].getCorresponType());
        assertEquals(WorkflowStatus.DRAFT,
                     page.getCondition().getWorkflowStatuses()[0]);
        assertEquals(10, page.getCondition().getPageRowNum());
        // Read Statusは未設定なので
        // 検索条件オブジェクトにも何も設定されていないはず
        assertEquals(0,
                     page.getCondition().getReadStatuses().length);
        assertEquals(1, page.getCondition().getPageNo());
        assertEquals("id", page.getCondition().getSort());
        assertTrue(page.getCondition().isAscending());
        assertEquals(page.getCondition(), MockAbstractPage.SET_CONDITION);
    }

    /**
     * 検索アクションのテスト（シンプルな検索、Cookieに表示件数設定済）.
     * @throws Exception
     */
    @Test
    public void testSearchSimplePageRowCookie() throws Exception {
        // Cookieに、指定されている表示件数を設定
        page.setPageRowNum(10);
        MockViewHelper.RET_COOKIE_VALUE = "50";

        // Serviceが返すダミーのコレポン文書を設定
        SearchCorresponResult result = new SearchCorresponResult();
        result.setCorresponList(list);
        result.setCount(5);
        MockCorresponSearchService.RET_SEARCH = result;

        SearchCorresponCondition condition = new SearchCorresponCondition();
        condition.setProjectId("MOCK");
        page.setCondition(condition);
        page.setSort("id");
        page.setAscending(true);

        page.setTypeList(createTypeList());
        page.setWorkflowList(WorkflowStatus.values());
        page.setReadStatusList(ReadStatus.values());
        page.setStatusList(CorresponStatus.values());
        page.setUserList(createUserList());
        page.setWorkflowProcessesList(WorkflowProcessStatus.values());
        page.setGroupList(createGroupList());
        page.setCustomFieldList(createCustomFieldList());

        // 検索条件
        page.setSimpleSearch(true);
        page.setType(2L);
        page.setWorkflow(WorkflowStatus.DRAFT.getValue());
        page.setReadStatus(-1);

        page.search();

        assertEquals(list, page.getCorresponList());
        assertEquals(5, page.getDataCount());

        // 初期設定を確認
        assertFalse(page.getNext());
        assertFalse(page.getPrevious());
        assertEquals(10, page.getPageIndex());
        assertEquals(5, page.getDataCount());
        assertEquals(1, page.getPageNo());
        assertEquals(50, page.getPageRowNum());
        assertEquals("id", page.getSort());
        // 検索条件
        assertTrue(page.isSimpleSearch());
        assertEquals("TypeB",
                     page.getCondition().getCorresponTypes()[0].getCorresponType());
        assertEquals(WorkflowStatus.DRAFT,
                     page.getCondition().getWorkflowStatuses()[0]);
        assertEquals(50, page.getCondition().getPageRowNum());
        // Read Statusは未設定なので
        // 検索条件オブジェクトにも何も設定されていないはず
        assertEquals(0,
                     page.getCondition().getReadStatuses().length);
        assertEquals(1, page.getCondition().getPageNo());
        assertEquals("id", page.getCondition().getSort());
        assertTrue(page.getCondition().isAscending());
        assertEquals(page.getCondition(), MockAbstractPage.SET_CONDITION);
    }

    /**
     * 検索アクションのテスト（高度な検索）.
     * @throws Exception
     */
    @Test
    public void testSearchAdvanced() throws Exception {
        // Serviceが返すダミーのコレポン文書を設定
        SearchCorresponResult result = new SearchCorresponResult();
        result.setCorresponList(list);
        result.setCount(5);
        MockCorresponSearchService.RET_SEARCH = result;

        SearchCorresponCondition condition = new SearchCorresponCondition();
        condition.setProjectId("MOCK");
        page.setCondition(condition);
        page.setSort("id");
        page.setAscending(true);

        page.setTypeList(createTypeList());
        page.setWorkflowList(WorkflowStatus.values());
        page.setReadStatusList(ReadStatus.values());
        page.setStatusList(CorresponStatus.values());
        page.setUserList(createUserList());
        page.setWorkflowProcessesList(WorkflowProcessStatus.values());
        page.setGroupList(createGroupList());
        page.setCustomFieldList(createCustomFieldList());

        // 検索条件
        page.setSimpleSearch(false);
        page.setFromNo("1");
        page.setToNo("2");
        page.setCorresponNo("Test Correspon");
        Long[] types = {1L};
        page.setTypes(types);
        Integer[] workflowStatuses = {WorkflowStatus.DENIED.getValue()};
        page.setWorkflowStatuses(workflowStatuses);
        Integer[] readStatuses = {ReadStatus.NEW.getValue()};
        page.setReadStatuses(readStatuses);
        Integer[] corresponStatuses = {CorresponStatus.CLOSED.getValue()};
        page.setStatuses(corresponStatuses);
        String[] users = {"00001"};
//        page.setAddressUsers(users);
     // TODO delete
//        page.setAddressFrom(true);
//        page.setAddressCc(true);
//        page.setAddressAttention(true);
//        page.setAddressPIC(true);
//        page.setAddressUnreplied(true);
        page.setFromUsers(users);
        page.setUserPreparer(true);
        page.setToUsers(users);
        page.setUserCc(true);
        page.setUserAttention(true);
        page.setUserPic(true);
        page.setUserUnreplied(true);
//        page.setWorkflowUsers(users);
        Integer[] processStatuses = {WorkflowProcessStatus.DENIED.getValue()};
        page.setWorkflowProcesses(processStatuses);
        Long[] groups = {1L};
//        page.setGroups(groups);
//        page.setGroupFrom(true);
        page.setFromGroups(groups);
        page.setToGroups(groups);
        page.setGroupTo(true);
        page.setGroupCc(true);
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        Date expDate1 = new GregorianCalendar(2009, 3, 1).getTime();
        Date date1 = new GregorianCalendar(2009, 3, 1, 10, 20, 30).getTime();
        page.setFromCreated(f.format(date1));
        Date expDate2 = new GregorianCalendar(2009, 3, 2).getTime();
        Date date2 = new GregorianCalendar(2009, 3, 2, 10, 20, 30).getTime();
        page.setToCreated(f.format(date2));
        Date expDate3 = new GregorianCalendar(2009, 3, 3).getTime();
        Date date3 = new GregorianCalendar(2009, 3, 3, 10, 20, 30).getTime();
        page.setFromIssued(f.format(date3));
        Date expDate4 = new GregorianCalendar(2009, 3, 4).getTime();
        Date date4 = new GregorianCalendar(2009, 3, 4, 10, 20, 30).getTime();
        page.setToIssued(f.format(date4));
        Date expDate5 = new GregorianCalendar(2009, 3, 5).getTime();
        Date date5 = new GregorianCalendar(2009, 3, 5, 10, 20, 30).getTime();
        page.setFromReply(f.format(date5));
        Date expDate6 = new GregorianCalendar(2009, 3, 6).getTime();
        Date date6 = new GregorianCalendar(2009, 3, 6, 10, 20, 30).getTime();
        page.setToReply(f.format(date6));
        page.setCustomFieldNo(1L);
        page.setCustomFieldValue("Field Value");

        page.search();

        assertEquals(list, page.getCorresponList());
        assertEquals(5, page.getDataCount());

        // 初期設定を確認
        assertFalse(page.getNext());
        assertFalse(page.getPrevious());
        assertEquals(10, page.getPageIndex());
        assertEquals(5, page.getDataCount());
        assertEquals(1, page.getPageNo());
        assertEquals(10, page.getPageRowNum());
        assertEquals("id", page.getSort());
        // 検索条件
        assertFalse(page.isSimpleSearch());
        assertEquals(Long.valueOf(1L), page.getCondition().getFromId());
        assertEquals(Long.valueOf(2L), page.getCondition().getToId());
        assertEquals("Test Correspon", page.getCondition().getCorresponNo());
        assertEquals("TypeA",
                     page.getCondition().getCorresponTypes()[0].getCorresponType());
        assertEquals(WorkflowStatus.DENIED,
                     page.getCondition().getWorkflowStatuses()[0]);
        assertEquals(ReadStatus.NEW,
                     page.getCondition().getReadStatuses()[0]);
        assertEquals(CorresponStatus.CLOSED,
                     page.getCondition().getCorresponStatuses()[0]);
//        assertEquals("A User",
//                     page.getCondition().getAddressUsers()[0].getNameE());
//        assertTrue(page.getCondition().isAddressFrom());
//        assertTrue(page.getCondition().isAddressCc());
//        assertTrue(page.getCondition().isAddressAttention());
//        assertTrue(page.getCondition().isAddressPersonInCharge());
//        assertTrue(page.getCondition().isAddressUnreplied());
        assertEquals("A User",
                page.getCondition().getFromUsers()[0].getNameE());
        assertEquals("A User",
                page.getCondition().getToUsers()[0].getNameE());
        assertTrue(page.getCondition().isUserPreparer());
        assertTrue(page.getCondition().isUserCc());
        assertTrue(page.getCondition().isUserAttention());
        assertTrue(page.getCondition().isUserPic());
        assertTrue(page.getCondition().isUserUnreplied());
//        assertEquals("A User",
//                     page.getCondition().getWorkflowUsers()[0].getNameE());
        assertEquals(WorkflowProcessStatus.DENIED,
                     page.getCondition().getWorkflowProcessStatuses()[0]);
//        assertEquals("A Group",
//                     page.getCondition().getCorresponGroups()[0].getName());
//        assertTrue(page.getCondition().isGroupFrom());
        assertEquals("A Group",
                     page.getCondition().getFromGroups()[0].getName());
        assertEquals("A Group",
                     page.getCondition().getToGroups()[0].getName());
        assertTrue(page.getCondition().isGroupTo());
        assertTrue(page.getCondition().isGroupCc());
        assertEquals(expDate1, page.getCondition().getFromCreatedOn());
        assertEquals(expDate2, page.getCondition().getToCreatedOn());
        assertEquals(expDate3, page.getCondition().getFromIssuedOn());
        assertEquals(expDate4, page.getCondition().getToIssuedOn());
        assertEquals(expDate5, page.getCondition().getFromDeadlineForReply());
        assertEquals(expDate6, page.getCondition().getToDeadlineForReply());
        assertEquals(Long.valueOf(1L), page.getCondition().getCustomFieldNo());
        assertEquals("Field Value", page.getCondition().getCustomFieldValue());
        assertEquals(1, page.getCondition().getPageNo());
        assertEquals("id", page.getCondition().getSort());
        assertTrue(page.getCondition().isAscending());
        assertEquals(page.getCondition(), MockAbstractPage.SET_CONDITION);
        assertEquals(10, page.getCondition().getPageRowNum());
    }

    /**
     * 検索アクションのテスト（高度な検索、Cookieに表示件数設定済）.
     * @throws Exception
     */
    @Test
    public void testSearchAdvancedPageRowCookie() throws Exception {
        // Cookieに、指定されている表示件数を設定
        page.setPageRowNum(20);
        MockViewHelper.RET_COOKIE_VALUE = "30";

        // Serviceが返すダミーのコレポン文書を設定
        SearchCorresponResult result = new SearchCorresponResult();
        result.setCorresponList(list);
        result.setCount(5);
        MockCorresponSearchService.RET_SEARCH = result;

        SearchCorresponCondition condition = new SearchCorresponCondition();
        condition.setProjectId("MOCK");
        page.setCondition(condition);
        page.setSort("id");
        page.setAscending(true);

        page.setTypeList(createTypeList());
        page.setWorkflowList(WorkflowStatus.values());
        page.setReadStatusList(ReadStatus.values());
        page.setStatusList(CorresponStatus.values());
        page.setUserList(createUserList());
        page.setWorkflowProcessesList(WorkflowProcessStatus.values());
        page.setGroupList(createGroupList());
        page.setCustomFieldList(createCustomFieldList());

        // 検索条件
        page.setSimpleSearch(false);
        page.setFromNo("1");
        page.setToNo("2");
        page.setCorresponNo("Test Correspon");
        Long[] types = {1L};
        page.setTypes(types);
        Integer[] workflowStatuses = {WorkflowStatus.DENIED.getValue()};
        page.setWorkflowStatuses(workflowStatuses);
        Integer[] readStatuses = {ReadStatus.NEW.getValue()};
        page.setReadStatuses(readStatuses);
        Integer[] corresponStatuses = {CorresponStatus.CLOSED.getValue()};
        page.setStatuses(corresponStatuses);
        String[] users = {"00001"};
//        page.setAddressUsers(users);
//        page.setAddressFrom(true);
//        page.setAddressCc(true);
//        page.setAddressAttention(true);
//        page.setAddressPIC(true);
//        page.setAddressUnreplied(true);
        page.setFromUsers(users);
        page.setUserPreparer(true);
        page.setToUsers(users);
        page.setUserCc(true);
        page.setUserAttention(true);
        page.setUserPic(true);
        page.setUserUnreplied(true);
//        page.setWorkflowUsers(users);
        Integer[] processStatuses = {WorkflowProcessStatus.DENIED.getValue()};
        page.setWorkflowProcesses(processStatuses);
        Long[] groups = {1L};
//        page.setGroups(groups);
//        page.setGroupFrom(true);
        page.setFromGroups(groups);
        page.setToGroups(groups);
        page.setGroupTo(true);
        page.setGroupCc(true);
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        Date expDate1 = new GregorianCalendar(2009, 3, 1).getTime();
        Date date1 = new GregorianCalendar(2009, 3, 1, 10, 20, 30).getTime();
        page.setFromCreated(f.format(date1));
        Date expDate2 = new GregorianCalendar(2009, 3, 2).getTime();
        Date date2 = new GregorianCalendar(2009, 3, 2, 10, 20, 30).getTime();
        page.setToCreated(f.format(date2));
        Date expDate3 = new GregorianCalendar(2009, 3, 3).getTime();
        Date date3 = new GregorianCalendar(2009, 3, 3, 10, 20, 30).getTime();
        page.setFromIssued(f.format(date3));
        Date expDate4 = new GregorianCalendar(2009, 3, 4).getTime();
        Date date4 = new GregorianCalendar(2009, 3, 4, 10, 20, 30).getTime();
        page.setToIssued(f.format(date4));
        Date expDate5 = new GregorianCalendar(2009, 3, 5).getTime();
        Date date5 = new GregorianCalendar(2009, 3, 5, 10, 20, 30).getTime();
        page.setFromReply(f.format(date5));
        Date expDate6 = new GregorianCalendar(2009, 3, 6).getTime();
        Date date6 = new GregorianCalendar(2009, 3, 6, 10, 20, 30).getTime();
        page.setToReply(f.format(date6));
        page.setCustomFieldNo(1L);
        page.setCustomFieldValue("Field Value");

        page.search();

        assertEquals(list, page.getCorresponList());
        assertEquals(5, page.getDataCount());

        // 初期設定を確認
        assertFalse(page.getNext());
        assertFalse(page.getPrevious());
        assertEquals(10, page.getPageIndex());
        assertEquals(5, page.getDataCount());
        assertEquals(1, page.getPageNo());
        assertEquals(30, page.getPageRowNum());
        assertEquals("id", page.getSort());
        // 検索条件
        assertFalse(page.isSimpleSearch());
        assertEquals(Long.valueOf(1L), page.getCondition().getFromId());
        assertEquals(Long.valueOf(2L), page.getCondition().getToId());
        assertEquals("Test Correspon", page.getCondition().getCorresponNo());
        assertEquals("TypeA",
                     page.getCondition().getCorresponTypes()[0].getCorresponType());
        assertEquals(WorkflowStatus.DENIED,
                     page.getCondition().getWorkflowStatuses()[0]);
        assertEquals(ReadStatus.NEW,
                     page.getCondition().getReadStatuses()[0]);
        assertEquals(CorresponStatus.CLOSED,
                     page.getCondition().getCorresponStatuses()[0]);
//        assertEquals("A User",
//                     page.getCondition().getAddressUsers()[0].getNameE());
//        assertTrue(page.getCondition().isAddressFrom());
//        assertTrue(page.getCondition().isAddressCc());
//        assertTrue(page.getCondition().isAddressAttention());
//        assertTrue(page.getCondition().isAddressPersonInCharge());
//        assertTrue(page.getCondition().isAddressUnreplied());
        assertEquals("A User",
                page.getCondition().getFromUsers()[0].getNameE());
        assertEquals("A User",
                page.getCondition().getToUsers()[0].getNameE());
        assertTrue(page.getCondition().isUserPreparer());
        assertTrue(page.getCondition().isUserCc());
        assertTrue(page.getCondition().isUserAttention());
        assertTrue(page.getCondition().isUserPic());
        assertTrue(page.getCondition().isUserUnreplied());
//        assertEquals("A User",
//                     page.getCondition().getWorkflowUsers()[0].getNameE());
        assertEquals(WorkflowProcessStatus.DENIED,
                     page.getCondition().getWorkflowProcessStatuses()[0]);
//        assertEquals("A Group",
//                     page.getCondition().getCorresponGroups()[0].getName());
//        assertTrue(page.getCondition().isGroupFrom());
        assertEquals("A Group", page.getCondition().getFromGroups()[0].getName());
        assertEquals("A Group", page.getCondition().getToGroups()[0].getName());
        assertTrue(page.getCondition().isGroupTo());
        assertTrue(page.getCondition().isGroupCc());
        assertEquals(expDate1, page.getCondition().getFromCreatedOn());
        assertEquals(expDate2, page.getCondition().getToCreatedOn());
        assertEquals(expDate3, page.getCondition().getFromIssuedOn());
        assertEquals(expDate4, page.getCondition().getToIssuedOn());
        assertEquals(expDate5, page.getCondition().getFromDeadlineForReply());
        assertEquals(expDate6, page.getCondition().getToDeadlineForReply());
        assertEquals(Long.valueOf(1L), page.getCondition().getCustomFieldNo());
        assertEquals("Field Value", page.getCondition().getCustomFieldValue());
        assertEquals(1, page.getCondition().getPageNo());
        assertEquals("id", page.getCondition().getSort());
        assertTrue(page.getCondition().isAscending());
        assertEquals(page.getCondition(), MockAbstractPage.SET_CONDITION);
        assertEquals(30, page.getCondition().getPageRowNum());
    }

    /**
     * 検索アクションのテスト（高度な検索の後のシンプルな検索でTypeを変更）.
     * @throws Exception
     */
    @Test
    public void testSearchSimpleTypeAfterAdvancedSearch() throws Exception {
        // Serviceが返すダミーのコレポン文書を設定
        SearchCorresponResult result = new SearchCorresponResult();
        result.setCorresponList(list);
        result.setCount(5);
        MockCorresponSearchService.RET_SEARCH = result;

        SearchCorresponCondition condition = new SearchCorresponCondition();
        condition.setProjectId("MOCK");
        page.setCondition(condition);
        page.setSort("id");
        page.setAscending(true);

        page.setTypeList(createTypeList());
        page.setWorkflowList(WorkflowStatus.values());
        page.setReadStatusList(ReadStatus.values());
        page.setStatusList(CorresponStatus.values());
        page.setUserList(createUserList());
        page.setWorkflowProcessesList(WorkflowProcessStatus.values());
        page.setGroupList(createGroupList());
        page.setCustomFieldList(createCustomFieldList());

        // 前回検索時の検索条件を設定(高度な検索を実行)
        page.setSimpleSearch(false);
        page.setFromNo("1");
        page.setToNo("2");
        page.setCorresponNo("Test Correspon");
        Long[] types = {2L};
        page.setTypes(types);
        Integer[] workflowStatuses = {WorkflowStatus.DENIED.getValue()};
        page.setWorkflowStatuses(workflowStatuses);
        Integer[] readStatuses = {ReadStatus.NEW.getValue()};
        page.setReadStatuses(readStatuses);
        Integer[] corresponStatuses = {CorresponStatus.CLOSED.getValue()};
        page.setStatuses(corresponStatuses);
        String[] users = {"00001"};
//        page.setAddressUsers(users);
//        page.setAddressFrom(true);
//        page.setAddressCc(true);
//        page.setAddressAttention(true);
//        page.setAddressPIC(true);
//        page.setAddressUnreplied(true);
        page.setFromUsers(users);
        page.setUserPreparer(true);
        page.setToUsers(users);
        page.setUserCc(true);
        page.setUserAttention(true);
        page.setUserPic(true);
        page.setUserUnreplied(true);
//        page.setWorkflowUsers(users);
        Integer[] processStatuses = {WorkflowProcessStatus.DENIED.getValue()};
        page.setWorkflowProcesses(processStatuses);
        Long[] groups = {1L};
//        page.setGroups(groups);
//        page.setGroupFrom(true);
        page.setFromGroups(groups);
        page.setToGroups(groups);
        page.setGroupTo(true);
        page.setGroupCc(true);
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        Date expDate1 = new GregorianCalendar(2009, 3, 1).getTime();
        Date date1 = new GregorianCalendar(2009, 3, 1, 10, 20, 30).getTime();
        page.setFromCreated(f.format(date1));
        Date expDate2 = new GregorianCalendar(2009, 3, 2).getTime();
        Date date2 = new GregorianCalendar(2009, 3, 2, 10, 20, 30).getTime();
        page.setToCreated(f.format(date2));
        Date expDate3 = new GregorianCalendar(2009, 3, 3).getTime();
        Date date3 = new GregorianCalendar(2009, 3, 3, 10, 20, 30).getTime();
        page.setFromIssued(f.format(date3));
        Date expDate4 = new GregorianCalendar(2009, 3, 4).getTime();
        Date date4 = new GregorianCalendar(2009, 3, 4, 10, 20, 30).getTime();
        page.setToIssued(f.format(date4));
        Date expDate5 = new GregorianCalendar(2009, 3, 5).getTime();
        Date date5 = new GregorianCalendar(2009, 3, 5, 10, 20, 30).getTime();
        page.setFromReply(f.format(date5));
        Date expDate6 = new GregorianCalendar(2009, 3, 6).getTime();
        Date date6 = new GregorianCalendar(2009, 3, 6, 10, 20, 30).getTime();
        page.setToReply(f.format(date6));
        page.setCustomFieldNo(1L);
        page.setCustomFieldValue("Field Value");
        page.search();

        // 検索条件設定
        initializePageSearchCondition();
        page.setSimpleSearch(true);
        page.setType(1L);
        page.setWorkflow(WorkflowStatus.DRAFT.getValue());
        page.setReadStatus(-1);
        page.setSimpleSearchSelectedItem(1);

        page.search();

        assertEquals(list, page.getCorresponList());
        assertEquals(5, page.getDataCount());

        // 初期設定を確認
        assertFalse(page.getNext());
        assertFalse(page.getPrevious());
        assertEquals(10, page.getPageIndex());
        assertEquals(5, page.getDataCount());
        assertEquals(1, page.getPageNo());
        assertEquals(10, page.getPageRowNum());
        assertEquals("id", page.getSort());
        // 検索条件
        assertTrue(page.isSimpleSearch());
        assertEquals(Long.valueOf(1L), page.getCondition().getFromId());
        assertEquals(Long.valueOf(2L), page.getCondition().getToId());
        assertEquals("Test Correspon", page.getCondition().getCorresponNo());
        assertEquals("TypeA",
                     page.getCondition().getCorresponTypes()[0].getCorresponType());
        assertEquals(WorkflowStatus.DENIED,
                     page.getCondition().getWorkflowStatuses()[0]);
        assertEquals(ReadStatus.NEW,
                     page.getCondition().getReadStatuses()[0]);
        assertEquals(CorresponStatus.CLOSED,
                     page.getCondition().getCorresponStatuses()[0]);
//        assertEquals("A User",
//                     page.getCondition().getAddressUsers()[0].getNameE());
//        assertTrue(page.getCondition().isAddressFrom());
//        assertTrue(page.getCondition().isAddressCc());
//        assertTrue(page.getCondition().isAddressAttention());
//        assertTrue(page.getCondition().isAddressPersonInCharge());
//        assertTrue(page.getCondition().isAddressUnreplied());
        assertEquals("A User",
                page.getCondition().getFromUsers()[0].getNameE());
        assertEquals("A User",
                page.getCondition().getToUsers()[0].getNameE());
        assertTrue(page.getCondition().isUserPreparer());
        assertTrue(page.getCondition().isUserCc());
        assertTrue(page.getCondition().isUserAttention());
        assertTrue(page.getCondition().isUserPic());
        assertTrue(page.getCondition().isUserUnreplied());
//        assertEquals("A User",
//                     page.getCondition().getWorkflowUsers()[0].getNameE());
        assertEquals(WorkflowProcessStatus.DENIED,
                     page.getCondition().getWorkflowProcessStatuses()[0]);
//        assertEquals("A Group",
//                     page.getCondition().getCorresponGroups()[0].getName());
//        assertTrue(page.getCondition().isGroupFrom());
        assertEquals("A Group", page.getCondition().getFromGroups()[0].getName());
        assertEquals("A Group", page.getCondition().getToGroups()[0].getName());
        assertTrue(page.getCondition().isGroupTo());
        assertTrue(page.getCondition().isGroupCc());
        assertEquals(expDate1, page.getCondition().getFromCreatedOn());
        assertEquals(expDate2, page.getCondition().getToCreatedOn());
        assertEquals(expDate3, page.getCondition().getFromIssuedOn());
        assertEquals(expDate4, page.getCondition().getToIssuedOn());
        assertEquals(expDate5, page.getCondition().getFromDeadlineForReply());
        assertEquals(expDate6, page.getCondition().getToDeadlineForReply());
        assertEquals(Long.valueOf(1L), page.getCondition().getCustomFieldNo());
        assertEquals("Field Value", page.getCondition().getCustomFieldValue());
        assertEquals(1, page.getCondition().getPageNo());
        assertEquals("id", page.getCondition().getSort());
        assertTrue(page.getCondition().isAscending());
        assertEquals(page.getCondition(), MockAbstractPage.SET_CONDITION);
    }

    /**
     * 検索アクションのテスト（高度な検索の後のシンプルな検索でWorkflowStatusを変更）.
     * @throws Exception
     */
    @Test
    public void testSearchSimpleWorkflowStatusAfterAdvancedSearch() throws Exception {
        // Serviceが返すダミーのコレポン文書を設定
        SearchCorresponResult result = new SearchCorresponResult();
        result.setCorresponList(list);
        result.setCount(5);
        MockCorresponSearchService.RET_SEARCH = result;

        SearchCorresponCondition condition = new SearchCorresponCondition();
        condition.setProjectId("MOCK");
        page.setCondition(condition);
        page.setSort("id");
        page.setAscending(true);

        page.setTypeList(createTypeList());
        page.setWorkflowList(WorkflowStatus.values());
        page.setReadStatusList(ReadStatus.values());
        page.setStatusList(CorresponStatus.values());
        page.setUserList(createUserList());
        page.setWorkflowProcessesList(WorkflowProcessStatus.values());
        page.setGroupList(createGroupList());
        page.setCustomFieldList(createCustomFieldList());

        // 前回検索時の検索条件を設定(高度な検索を実行)
        page.setSimpleSearch(false);
        page.setFromNo("1");
        page.setToNo("2");
        page.setCorresponNo("Test Correspon");
        Long[] types = {1L};
        page.setTypes(types);
        Integer[] workflowStatuses = {WorkflowStatus.DRAFT.getValue()};
        page.setWorkflowStatuses(workflowStatuses);
        Integer[] readStatuses = {ReadStatus.NEW.getValue()};
        page.setReadStatuses(readStatuses);
        Integer[] corresponStatuses = {CorresponStatus.CLOSED.getValue()};
        page.setStatuses(corresponStatuses);
        String[] users = {"00001"};
//        page.setAddressUsers(users);
//        page.setAddressFrom(true);
//        page.setAddressCc(true);
//        page.setAddressAttention(true);
//        page.setAddressPIC(true);
//        page.setAddressUnreplied(true);
        page.setFromUsers(users);
        page.setUserPreparer(true);
        page.setToUsers(users);
        page.setUserCc(true);
        page.setUserAttention(true);
        page.setUserPic(true);
        page.setUserUnreplied(true);
//        page.setWorkflowUsers(users);
        Integer[] processStatuses = {WorkflowProcessStatus.DENIED.getValue()};
        page.setWorkflowProcesses(processStatuses);
        Long[] groups = {1L};
//        page.setGroups(groups);
//        page.setGroupFrom(true);
        page.setFromGroups(groups);
        page.setToGroups(groups);
        page.setGroupTo(true);
        page.setGroupCc(true);
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        Date expDate1 = new GregorianCalendar(2009, 3, 1).getTime();
        Date date1 = new GregorianCalendar(2009, 3, 1, 10, 20, 30).getTime();
        page.setFromCreated(f.format(date1));
        Date expDate2 = new GregorianCalendar(2009, 3, 2).getTime();
        Date date2 = new GregorianCalendar(2009, 3, 2, 10, 20, 30).getTime();
        page.setToCreated(f.format(date2));
        Date expDate3 = new GregorianCalendar(2009, 3, 3).getTime();
        Date date3 = new GregorianCalendar(2009, 3, 3, 10, 20, 30).getTime();
        page.setFromIssued(f.format(date3));
        Date expDate4 = new GregorianCalendar(2009, 3, 4).getTime();
        Date date4 = new GregorianCalendar(2009, 3, 4, 10, 20, 30).getTime();
        page.setToIssued(f.format(date4));
        Date expDate5 = new GregorianCalendar(2009, 3, 5).getTime();
        Date date5 = new GregorianCalendar(2009, 3, 5, 10, 20, 30).getTime();
        page.setFromReply(f.format(date5));
        Date expDate6 = new GregorianCalendar(2009, 3, 6).getTime();
        Date date6 = new GregorianCalendar(2009, 3, 6, 10, 20, 30).getTime();
        page.setToReply(f.format(date6));
        page.setCustomFieldNo(1L);
        page.setCustomFieldValue("Field Value");
        page.search();

        // 検索条件設定
        initializePageSearchCondition();
        page.setSimpleSearch(true);
        page.setType(2L);
        page.setWorkflow(WorkflowStatus.DENIED.getValue());
        page.setReadStatus(-1);
        page.setSimpleSearchSelectedItem(2);

        page.search();

        assertEquals(list, page.getCorresponList());
        assertEquals(5, page.getDataCount());

        // 初期設定を確認
        assertFalse(page.getNext());
        assertFalse(page.getPrevious());
        assertEquals(10, page.getPageIndex());
        assertEquals(5, page.getDataCount());
        assertEquals(1, page.getPageNo());
        assertEquals(10, page.getPageRowNum());
        assertEquals("id", page.getSort());
        // 検索条件
        assertTrue(page.isSimpleSearch());
        assertEquals(Long.valueOf(1L), page.getCondition().getFromId());
        assertEquals(Long.valueOf(2L), page.getCondition().getToId());
        assertEquals("Test Correspon", page.getCondition().getCorresponNo());
        assertEquals("TypeA",
                     page.getCondition().getCorresponTypes()[0].getCorresponType());
        assertEquals(WorkflowStatus.DENIED,
                     page.getCondition().getWorkflowStatuses()[0]);
        assertEquals(ReadStatus.NEW,
                     page.getCondition().getReadStatuses()[0]);
        assertEquals(CorresponStatus.CLOSED,
                     page.getCondition().getCorresponStatuses()[0]);
//        assertEquals("A User",
//                     page.getCondition().getAddressUsers()[0].getNameE());
//        assertTrue(page.getCondition().isAddressFrom());
//        assertTrue(page.getCondition().isAddressCc());
//        assertTrue(page.getCondition().isAddressAttention());
//        assertTrue(page.getCondition().isAddressPersonInCharge());
//        assertTrue(page.getCondition().isAddressUnreplied());
        assertEquals("A User",
                page.getCondition().getFromUsers()[0].getNameE());
        assertEquals("A User",
                page.getCondition().getToUsers()[0].getNameE());
        assertTrue(page.getCondition().isUserPreparer());
        assertTrue(page.getCondition().isUserCc());
        assertTrue(page.getCondition().isUserAttention());
        assertTrue(page.getCondition().isUserPic());
        assertTrue(page.getCondition().isUserUnreplied());
//        assertEquals("A User",
//                     page.getCondition().getWorkflowUsers()[0].getNameE());
        assertEquals(WorkflowProcessStatus.DENIED,
                     page.getCondition().getWorkflowProcessStatuses()[0]);
//        assertEquals("A Group",
//                     page.getCondition().getCorresponGroups()[0].getName());
//        assertTrue(page.getCondition().isGroupFrom());
        assertEquals("A Group", page.getCondition().getFromGroups()[0].getName());
        assertEquals("A Group", page.getCondition().getToGroups()[0].getName());
        assertTrue(page.getCondition().isGroupTo());
        assertTrue(page.getCondition().isGroupCc());
        assertEquals(expDate1, page.getCondition().getFromCreatedOn());
        assertEquals(expDate2, page.getCondition().getToCreatedOn());
        assertEquals(expDate3, page.getCondition().getFromIssuedOn());
        assertEquals(expDate4, page.getCondition().getToIssuedOn());
        assertEquals(expDate5, page.getCondition().getFromDeadlineForReply());
        assertEquals(expDate6, page.getCondition().getToDeadlineForReply());
        assertEquals(Long.valueOf(1L), page.getCondition().getCustomFieldNo());
        assertEquals("Field Value", page.getCondition().getCustomFieldValue());
        assertEquals(1, page.getCondition().getPageNo());
        assertEquals("id", page.getCondition().getSort());
        assertTrue(page.getCondition().isAscending());
        assertEquals(page.getCondition(), MockAbstractPage.SET_CONDITION);
    }

    /**
     * 検索アクションのテスト（高度な検索の後のシンプルな検索でReadStatusを変更）.
     * @throws Exception
     */
    @Test
    public void testSearchSimpleReadStatusAfterAdvancedSearch() throws Exception {
        // Serviceが返すダミーのコレポン文書を設定
        SearchCorresponResult result = new SearchCorresponResult();
        result.setCorresponList(list);
        result.setCount(5);
        MockCorresponSearchService.RET_SEARCH = result;

        SearchCorresponCondition condition = new SearchCorresponCondition();
        condition.setProjectId("MOCK");
        page.setCondition(condition);
        page.setSort("id");
        page.setAscending(true);

        page.setTypeList(createTypeList());
        page.setWorkflowList(WorkflowStatus.values());
        page.setReadStatusList(ReadStatus.values());
        page.setStatusList(CorresponStatus.values());
        page.setUserList(createUserList());
        page.setWorkflowProcessesList(WorkflowProcessStatus.values());
        page.setGroupList(createGroupList());
        page.setCustomFieldList(createCustomFieldList());

        // 前回検索時の検索条件を設定(高度な検索を実行)
        page.setSimpleSearch(false);
        page.setFromNo("1");
        page.setToNo("2");
        page.setCorresponNo("Test Correspon");
        Long[] types = {1L};
        page.setTypes(types);
        Integer[] workflowStatuses = {WorkflowStatus.DENIED.getValue()};
        page.setWorkflowStatuses(workflowStatuses);
        Integer[] readStatuses = {ReadStatus.READ.getValue()};
        page.setReadStatuses(readStatuses);
        Integer[] corresponStatuses = {CorresponStatus.CLOSED.getValue()};
        page.setStatuses(corresponStatuses);
        String[] users = {"00001"};
//        page.setAddressUsers(users);
//        page.setAddressFrom(true);
//        page.setAddressCc(true);
//        page.setAddressAttention(true);
//        page.setAddressPIC(true);
//        page.setAddressUnreplied(true);
        page.setFromUsers(users);
        page.setUserPreparer(true);
        page.setToUsers(users);
        page.setUserCc(true);
        page.setUserAttention(true);
        page.setUserPic(true);
        page.setUserUnreplied(true);
//        page.setWorkflowUsers(users);
        Integer[] processStatuses = {WorkflowProcessStatus.DENIED.getValue()};
        page.setWorkflowProcesses(processStatuses);
        Long[] groups = {1L};
//        page.setGroups(groups);
//        page.setGroupFrom(true);
        page.setFromGroups(groups);
        page.setToGroups(groups);
        page.setGroupTo(true);
        page.setGroupCc(true);
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        Date expDate1 = new GregorianCalendar(2009, 3, 1).getTime();
        Date date1 = new GregorianCalendar(2009, 3, 1, 10, 20, 30).getTime();
        page.setFromCreated(f.format(date1));
        Date expDate2 = new GregorianCalendar(2009, 3, 2).getTime();
        Date date2 = new GregorianCalendar(2009, 3, 2, 10, 20, 30).getTime();
        page.setToCreated(f.format(date2));
        Date expDate3 = new GregorianCalendar(2009, 3, 3).getTime();
        Date date3 = new GregorianCalendar(2009, 3, 3, 10, 20, 30).getTime();
        page.setFromIssued(f.format(date3));
        Date expDate4 = new GregorianCalendar(2009, 3, 4).getTime();
        Date date4 = new GregorianCalendar(2009, 3, 4, 10, 20, 30).getTime();
        page.setToIssued(f.format(date4));
        Date expDate5 = new GregorianCalendar(2009, 3, 5).getTime();
        Date date5 = new GregorianCalendar(2009, 3, 5, 10, 20, 30).getTime();
        page.setFromReply(f.format(date5));
        Date expDate6 = new GregorianCalendar(2009, 3, 6).getTime();
        Date date6 = new GregorianCalendar(2009, 3, 6, 10, 20, 30).getTime();
        page.setToReply(f.format(date6));
        page.setCustomFieldNo(1L);
        page.setCustomFieldValue("Field Value");
        page.search();

        // 検索条件設定
        initializePageSearchCondition();
        page.setSimpleSearch(true);
        page.setType(2L);
        page.setWorkflow(WorkflowStatus.DRAFT.getValue());
        page.setReadStatus(ReadStatus.NEW.getValue());
        page.setSimpleSearchSelectedItem(3);

        page.search();

        assertEquals(list, page.getCorresponList());
        assertEquals(5, page.getDataCount());

        // 初期設定を確認
        assertFalse(page.getNext());
        assertFalse(page.getPrevious());
        assertEquals(10, page.getPageIndex());
        assertEquals(5, page.getDataCount());
        assertEquals(1, page.getPageNo());
        assertEquals(10, page.getPageRowNum());
        assertEquals("id", page.getSort());
        // 検索条件
        assertTrue(page.isSimpleSearch());
        assertEquals(Long.valueOf(1L), page.getCondition().getFromId());
        assertEquals(Long.valueOf(2L), page.getCondition().getToId());
        assertEquals("Test Correspon", page.getCondition().getCorresponNo());
        assertEquals("TypeA",
                     page.getCondition().getCorresponTypes()[0].getCorresponType());
        assertEquals(WorkflowStatus.DENIED,
                     page.getCondition().getWorkflowStatuses()[0]);
        assertEquals(ReadStatus.NEW,
                     page.getCondition().getReadStatuses()[0]);
        assertEquals(CorresponStatus.CLOSED,
                     page.getCondition().getCorresponStatuses()[0]);
//        assertEquals("A User",
//                     page.getCondition().getAddressUsers()[0].getNameE());
//        assertTrue(page.getCondition().isAddressFrom());
//        assertTrue(page.getCondition().isAddressCc());
//        assertTrue(page.getCondition().isAddressAttention());
//        assertTrue(page.getCondition().isAddressPersonInCharge());
//        assertTrue(page.getCondition().isAddressUnreplied());
        assertEquals("A User",
                page.getCondition().getFromUsers()[0].getNameE());
        assertEquals("A User",
                page.getCondition().getToUsers()[0].getNameE());
        assertTrue(page.getCondition().isUserPreparer());
        assertTrue(page.getCondition().isUserCc());
        assertTrue(page.getCondition().isUserAttention());
        assertTrue(page.getCondition().isUserPic());
        assertTrue(page.getCondition().isUserUnreplied());
//        assertEquals("A User",
//                     page.getCondition().getWorkflowUsers()[0].getNameE());
        assertEquals(WorkflowProcessStatus.DENIED,
                     page.getCondition().getWorkflowProcessStatuses()[0]);
//        assertEquals("A Group",
//                     page.getCondition().getCorresponGroups()[0].getName());
//        assertTrue(page.getCondition().isGroupFrom());
        assertEquals("A Group", page.getCondition().getFromGroups()[0].getName());
        assertEquals("A Group", page.getCondition().getToGroups()[0].getName());
        assertTrue(page.getCondition().isGroupTo());
        assertTrue(page.getCondition().isGroupCc());
        assertEquals(expDate1, page.getCondition().getFromCreatedOn());
        assertEquals(expDate2, page.getCondition().getToCreatedOn());
        assertEquals(expDate3, page.getCondition().getFromIssuedOn());
        assertEquals(expDate4, page.getCondition().getToIssuedOn());
        assertEquals(expDate5, page.getCondition().getFromDeadlineForReply());
        assertEquals(expDate6, page.getCondition().getToDeadlineForReply());
        assertEquals(Long.valueOf(1L), page.getCondition().getCustomFieldNo());
        assertEquals("Field Value", page.getCondition().getCustomFieldValue());
        assertEquals(1, page.getCondition().getPageNo());
        assertEquals("id", page.getCondition().getSort());
        assertTrue(page.getCondition().isAscending());
        assertEquals(page.getCondition(), MockAbstractPage.SET_CONDITION);
    }

    /**
     * ページに設定されている検索条件を初期化する.
     */
    private void initializePageSearchCondition() {
        page.setSimpleSearch(false);
        page.setFromNo(null);
        page.setToNo(null);
        page.setCorresponNo(null);
        page.setTypes(null);
        page.setWorkflowStatuses(null);
        page.setReadStatuses(null);
        page.setStatuses(null);
//        page.setAddressUsers(null);
//        page.setAddressFrom(false);
//        page.setAddressCc(false);
//        page.setAddressAttention(false);
//        page.setAddressPIC(false);
//        page.setAddressUnreplied(false);
        page.setFromUsers(null);
        page.setUserPreparer(false);
        page.setToUsers(null);
        page.setUserCc(false);
        page.setUserAttention(false);
        page.setUserPic(false);
        page.setUserUnreplied(false);
//        page.setWorkflowUsers(null);
        page.setWorkflowProcesses(null);
//        page.setGroups(null);
//        page.setGroupFrom(false);
        page.setFromGroups(null);
        page.setToGroups(null);
        page.setGroupTo(false);
        page.setGroupCc(false);
        page.setFromCreated(null);
        page.setToCreated(null);
        page.setFromIssued(null);
        page.setToIssued(null);
        page.setFromReply(null);
        page.setToReply(null);
        page.setCustomFieldNo(null);
        page.setCustomFieldValue(null);
    }

    /**
     * CSV出力アクションのテスト.
     * @throws Exception
     */
    @Test
    public void testDownloadCsv() throws Exception {
        // 変換後のデータを作成
        byte[] expected = "TEST-OUT-CSV".getBytes();
        MockCorresponSearchService.RET_CSV = expected;

        String expFileName = "TEST001";
        MockAbstractPage.RET_FILE_NAME = expFileName;

        SearchCorresponResult result = new SearchCorresponResult();
        result.setCorresponList(list);
        result.setCount(5);
        MockCorresponSearchService.RET_SEARCH = result;

        page.setCondition(new SearchCorresponCondition());

        page.downloadCsv();

        assertEquals("download", MockViewHelper.RET_ACTION);
        assertArrayEquals(expected, MockViewHelper.RET_DATA);
        assertEquals(expFileName + ".csv", MockViewHelper.RET_FILENAME);

        // 2回目の作成
        byte[] newExpected = "NEW-TEST-OUT-CSV".getBytes();
        MockCorresponSearchService.RET_CSV = newExpected;

        String expFileName2 = "TEST002";
        MockAbstractPage.RET_FILE_NAME = expFileName2;

        page.downloadCsv();

        assertEquals("download", MockViewHelper.RET_ACTION);
        assertArrayEquals(newExpected, MockViewHelper.RET_DATA);
        assertEquals(expFileName2 + ".csv", MockViewHelper.RET_FILENAME);
    }

    /**
     * EXCEL出力アクションのテスト.
     * IOExceptionが発生.
     * @throws Exception
     */
    @Test
    public void testDownloadCsvIOException() throws Exception {
        // 変換後のデータを作成
        byte[] expected = "TEST-OUT-CSV".getBytes();
        MockCorresponSearchService.RET_CSV = expected;

        String expFileName = "TEST001";
        MockAbstractPage.RET_FILE_NAME = expFileName;

        SearchCorresponResult result = new SearchCorresponResult();
        result.setCorresponList(list);
        result.setCount(5);
        MockCorresponSearchService.RET_SEARCH = result;

        MockViewHelper.EX_DOWNLOAD = new IOException();

        // 期待されるメッセージをセット
        FacesContextMock.EXPECTED_CLIENT_ID = null;
        FacesContextMock.EXPECTED_MESSAGE =
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                 FacesMessage.SEVERITY_ERROR.toString(),
                                 "ダウンロードに失敗しました。");

        page.setCondition(new SearchCorresponCondition());
        page.downloadExcel();
    }

    /**
     * EXCEL出力アクションのテスト.
     * @throws Exception
     */
    @Test
    public void testDownloadExcel() throws Exception {
        // 変換後のデータを作成
        byte[] expected = "TEST-OUT-EXCEL".getBytes();
        MockCorresponSearchService.RET_EXCEL = expected;

        String expFileName = "TEST001";
        MockAbstractPage.RET_FILE_NAME = expFileName;

        SearchCorresponResult result = new SearchCorresponResult();
        result.setCorresponList(list);
        result.setCount(5);
        MockCorresponSearchService.RET_SEARCH = result;

        page.setCondition(new SearchCorresponCondition());

        page.downloadExcel();

        assertEquals("download", MockViewHelper.RET_ACTION);
        assertArrayEquals(expected, MockViewHelper.RET_DATA);
        assertEquals(expFileName + ".xls", MockViewHelper.RET_FILENAME);

        // 2回目の作成
        byte[] newExpected = "NEW-TEST-OUT-EXCEL".getBytes();
        MockCorresponSearchService.RET_EXCEL = newExpected;

        String expFileName2 = "TEST002";
        MockAbstractPage.RET_FILE_NAME = expFileName2;

        page.downloadExcel();

        assertEquals("download", MockViewHelper.RET_ACTION);
        assertArrayEquals(newExpected, MockViewHelper.RET_DATA);
        assertEquals(expFileName2 + ".xls", MockViewHelper.RET_FILENAME);
    }

    /**
     * EXCEL出力アクションのテスト.
     * IOExceptionが発生.
     * @throws Exception
     */
    @Test
    public void testDownloadExcelIOException() throws Exception {
        // 変換後のデータを作成
        byte[] expected = "TEST-OUT-EXCEL".getBytes();
        MockCorresponSearchService.RET_EXCEL = expected;

        String expFileName = "TEST001";
        MockAbstractPage.RET_FILE_NAME = expFileName;

        SearchCorresponResult result = new SearchCorresponResult();
        result.setCorresponList(list);
        result.setCount(5);
        MockCorresponSearchService.RET_SEARCH = result;

        MockViewHelper.EX_DOWNLOAD = new IOException();

        // 期待されるメッセージをセット
        FacesContextMock.EXPECTED_CLIENT_ID = null;
        FacesContextMock.EXPECTED_MESSAGE =
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                 FacesMessage.SEVERITY_ERROR.toString(),
                                 createExpectedMessageString(
                                     Messages.getMessageAsString(E_DOWNLOAD_FAILED),
                                     null));

        page.setCondition(new SearchCorresponCondition());
        page.downloadExcel();
    }

    /**
     * HTML出力アクションのテスト.
     * @throws Exception
     */
    @Test
    public void testPrintHTML() throws Exception {
        // 変換後のデータを作成
        byte[] expected = "TEST-OUT-HTML".getBytes();
        MockCorresponSearchService.RET_HTML = expected;

        SearchCorresponResult result = new SearchCorresponResult();
        result.setCorresponList(list);
        result.setCount(5);
        MockCorresponSearchService.RET_SEARCH = result;

        page.printHTML();

        assertEquals("requestResponse", MockViewHelper.RET_ACTION);
        assertArrayEquals(expected, MockViewHelper.RET_DATA);

        // 2回目の作成
        byte[] newExpected = "NEW-TEST-OUT-HTML".getBytes();
        MockCorresponSearchService.RET_HTML = newExpected;

        page.printHTML();

        assertEquals("requestResponse", MockViewHelper.RET_ACTION);
        assertArrayEquals(newExpected, MockViewHelper.RET_DATA);
    }

    /**
     * ZIP出力アクションのテスト.
     * @throws Exception
     */
    @Test
    public void testDownloadZip() throws Exception {
        // 変換後のデータを作成
        byte[] expected = "TEST-DOWNLOAD-ZIP".getBytes();
        MockCorresponSearchService.RET_ZIP = expected;

        String expFileName = "TEST001";
        MockAbstractPage.RET_FILE_NAME = expFileName;

        boolean check = false;
        List<CorresponIndex> indexList = new ArrayList<CorresponIndex>();
        List<Correspon> expList = new ArrayList<Correspon>();
        for (Correspon correspon : list) {
            check = !check; // 奇数行にチェック
            CorresponIndex index = new CorresponIndex();
            index.setCorrespon(correspon);
            index.setChecked(check);
            indexList.add(index);

            if (check) {
                expList.add(correspon);
            }
        }
        page.setIndexList(indexList);

        page.downloadZip();

        assertEquals("download", MockViewHelper.RET_ACTION);
        assertArrayEquals(expected, MockViewHelper.RET_DATA);
        assertEquals(expFileName + ".zip", MockViewHelper.RET_FILENAME);

        List<Correspon> actList = MockCorresponSearchService.SET_ZIP;
        assertEquals(expList.size(), actList.size());
        for (int i = 0; i < actList.size(); i++) {
            Correspon actCorrespon = actList.get(i);
            Correspon expCorrespon = expList.get(i);
            assertEquals(expCorrespon.getId(), actCorrespon.getId());
        }
    }

    /**
     * ZIP出力アクションのテスト.
     * IOExceptionが発生.
     * @throws Exception
     */
    @Test
    public void testDownloadZipIOException() throws Exception {
        // 変換後のデータを作成
        byte[] expected = "TEST-DOWNLOAD-ZIP".getBytes();
        MockCorresponSearchService.RET_ZIP = expected;

        String expFileName = "TEST001";
        MockAbstractPage.RET_FILE_NAME = expFileName;

        boolean check = false;
        List<CorresponIndex> indexList = new ArrayList<CorresponIndex>();
        List<Correspon> expList = new ArrayList<Correspon>();
        for (Correspon correspon : list) {
            check = !check; // 奇数行にチェック
            CorresponIndex index = new CorresponIndex();
            index.setCorrespon(correspon);
            index.setChecked(check);
            indexList.add(index);

            if (check) {
                expList.add(correspon);
            }
        }
        page.setIndexList(indexList);

        MockViewHelper.EX_DOWNLOAD = new IOException();

        // 期待されるメッセージをセット
        FacesContextMock.EXPECTED_CLIENT_ID = null;
        FacesContextMock.EXPECTED_MESSAGE =
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                 FacesMessage.SEVERITY_ERROR.toString(),
                                 createExpectedMessageString(
                                     Messages.getMessageAsString(E_DOWNLOAD_FAILED),
                                     null));
        page.downloadZip();
    }

    /**
     * 既読アクションのテスト.
     * @throws Exception
     */
    @Test
    public void testRead() throws Exception {
        SearchCorresponResult result = new SearchCorresponResult();
        result.setCorresponList(list);
        result.setCount(15);
        MockCorresponSearchService.RET_SEARCH = result;

        // 期待されるメッセージをセット
        FacesContextMock.EXPECTED_CLIENT_ID = null;
        FacesContextMock.EXPECTED_MESSAGE =
                new FacesMessage(FacesMessage.SEVERITY_INFO,
                                 FacesMessage.SEVERITY_INFO.toString(),
                                 createExpectedMessageString(
                                     Messages.getMessageAsString(SAVE_SUCCESSFUL),
                                     null));

        boolean check = false;
        List<CorresponIndex> indexList = new ArrayList<CorresponIndex>();
        List<Correspon> expected = new ArrayList<Correspon>();
        for (Correspon correspon : list) {
            check = !check; // 奇数行にチェック
            CorresponIndex index = new CorresponIndex();
            index.setCorrespon(correspon);
            index.setChecked(check);
            indexList.add(index);

            if (check) {
                expected.add(correspon);
            }
        }
        page.setIndexList(indexList);

        page.setCondition(new SearchCorresponCondition());
        page.setPageNo(2);
        page.setSort("name");

        page.read();

        List<Correspon> actual = MockCorresponSearchService.SET_READ_STATUS;
        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < actual.size(); i++) {
            Correspon actCorrespon = actual.get(i);
            Correspon expCorrespon = expected.get(i);
            assertEquals(expCorrespon.getId(), actCorrespon.getId());
            assertEquals(ReadStatus.READ, actCorrespon.getCorresponReadStatus().getReadStatus());
        }

        // 同一ページでリロードされている
        assertFalse(page.getNext());
        assertTrue(page.getPrevious());
        assertEquals(10, page.getPageIndex());
        assertEquals(15, page.getDataCount());
        assertEquals(2, page.getPageNo());
        assertEquals(10, page.getPageRowNum());
        assertEquals("name", page.getSort());
        assertEquals(list, page.getCorresponList());
    }

    /**
     * 未読アクションのテスト.
     * @throws Exception
     */
    @Test
    public void testUnread() throws Exception {
        SearchCorresponResult result = new SearchCorresponResult();
        result.setCorresponList(list);
        result.setCount(15);
        MockCorresponSearchService.RET_SEARCH = result;

        // 期待されるメッセージをセット
        FacesContextMock.EXPECTED_CLIENT_ID = null;
        FacesContextMock.EXPECTED_MESSAGE =
                new FacesMessage(FacesMessage.SEVERITY_INFO,
                                 FacesMessage.SEVERITY_INFO.toString(),
                                 createExpectedMessageString(
                                     Messages.getMessageAsString(SAVE_SUCCESSFUL),
                                     null));

        boolean check = true;
        List<CorresponIndex> indexList = new ArrayList<CorresponIndex>();
        List<Correspon> expected = new ArrayList<Correspon>();
        for (Correspon correspon : list) {
            check = !check; // 偶数行にチェック
            CorresponIndex index = new CorresponIndex();
            index.setCorrespon(correspon);
            index.setChecked(check);
            indexList.add(index);

            if (check) {
                expected.add(correspon);
            }
        }
        page.setIndexList(indexList);

        page.setCondition(new SearchCorresponCondition());
        page.setPageNo(2);
        page.setSort("name");

        page.unread();

        List<Correspon> actual = MockCorresponSearchService.SET_READ_STATUS;
        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < actual.size(); i++) {
            Correspon actCorrespon = actual.get(i);
            Correspon expCorrespon = expected.get(i);
            assertEquals(expCorrespon.getId(), actCorrespon.getId());
            assertEquals(ReadStatus.NEW, actCorrespon.getCorresponReadStatus().getReadStatus());
        }

        // 同一ページでリロードされている
        assertFalse(page.getNext());
        assertTrue(page.getPrevious());
        assertEquals(10, page.getPageIndex());
        assertEquals(15, page.getDataCount());
        assertEquals(2, page.getPageNo());
        assertEquals(10, page.getPageRowNum());
        assertEquals("name", page.getSort());
        assertEquals(list, page.getCorresponList());
    }

    /**
     * 文書Openアクションのテスト.
     * @throws Exception
     */
    @Test
    public void testOpen() throws Exception {
        SearchCorresponResult result = new SearchCorresponResult();
        result.setCorresponList(list);
        result.setCount(15);
        MockCorresponSearchService.RET_SEARCH = result;

        // 期待されるメッセージをセット
        FacesContextMock.EXPECTED_CLIENT_ID = null;
        FacesContextMock.EXPECTED_MESSAGE =
                new FacesMessage(FacesMessage.SEVERITY_INFO,
                                 FacesMessage.SEVERITY_INFO.toString(),
                                 createExpectedMessageString(
                                     Messages.getMessageAsString(SAVE_SUCCESSFUL),
                                     null));

        boolean check = false;
        List<CorresponIndex> indexList = new ArrayList<CorresponIndex>();
        List<Correspon> expected = new ArrayList<Correspon>();
        for (Correspon correspon : list) {
            check = !check; // 奇数行にチェック
            CorresponIndex index = new CorresponIndex();
            index.setCorrespon(correspon);
            index.setChecked(check);
            indexList.add(index);

            if (check) {
                expected.add(correspon);
            }
        }
        page.setIndexList(indexList);

        page.setCondition(new SearchCorresponCondition());
        page.setPageNo(2);
        page.setSort("name");

        page.open();

        List<Correspon> actual = MockCorresponSearchService.SET_STATUS;
        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < actual.size(); i++) {
            Correspon actCorrespon = actual.get(i);
            Correspon expCorrespon = expected.get(i);
            assertEquals(expCorrespon.getId(), actCorrespon.getId());
            assertEquals(CorresponStatus.OPEN, actCorrespon.getCorresponStatus());
        }

        // 同一ページでリロードされている
        assertFalse(page.getNext());
        assertTrue(page.getPrevious());
        assertEquals(10, page.getPageIndex());
        assertEquals(15, page.getDataCount());
        assertEquals(2, page.getPageNo());
        assertEquals(10, page.getPageRowNum());
        assertEquals("name", page.getSort());
        assertEquals(list, page.getCorresponList());
    }

    /**
     * 文書Closeアクションのテスト.
     * @throws Exception
     */
    @Test
    public void testClose() throws Exception {
        SearchCorresponResult result = new SearchCorresponResult();
        result.setCorresponList(list);
        result.setCount(15);
        MockCorresponSearchService.RET_SEARCH = result;

        // 期待されるメッセージをセット
        FacesContextMock.EXPECTED_CLIENT_ID = null;
        FacesContextMock.EXPECTED_MESSAGE =
                new FacesMessage(FacesMessage.SEVERITY_INFO,
                                 FacesMessage.SEVERITY_INFO.toString(),
                                 createExpectedMessageString(
                                     Messages.getMessageAsString(SAVE_SUCCESSFUL),
                                     null));

        boolean check = true;
        List<CorresponIndex> indexList = new ArrayList<CorresponIndex>();
        List<Correspon> expected = new ArrayList<Correspon>();
        for (Correspon correspon : list) {
            check = !check; // 偶数行にチェック
            CorresponIndex index = new CorresponIndex();
            index.setCorrespon(correspon);
            index.setChecked(check);
            indexList.add(index);

            if (check) {
                expected.add(correspon);
            }
        }
        page.setIndexList(indexList);

        page.setCondition(new SearchCorresponCondition());
        page.setPageNo(2);
        page.setSort("name");

        page.close();

        List<Correspon> actual = MockCorresponSearchService.SET_STATUS;
        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < actual.size(); i++) {
            Correspon actCorrespon = actual.get(i);
            Correspon expCorrespon = expected.get(i);
            assertEquals(expCorrespon.getId(), actCorrespon.getId());
            assertEquals(CorresponStatus.CLOSED, actCorrespon.getCorresponStatus());
        }

        // 同一ページでリロードされている
        assertFalse(page.getNext());
        assertTrue(page.getPrevious());
        assertEquals(10, page.getPageIndex());
        assertEquals(15, page.getDataCount());
        assertEquals(2, page.getPageNo());
        assertEquals(10, page.getPageRowNum());
        assertEquals("name", page.getSort());
        assertEquals(list, page.getCorresponList());
    }

    /**
     * 削除アクションのテスト.
     * @throws Exception
     */
    @Test
    public void testDelete() throws Exception {
        SearchCorresponResult result = new SearchCorresponResult();
        result.setCorresponList(list);
        result.setCount(15);
        MockCorresponSearchService.RET_SEARCH = result;

        // 期待されるメッセージをセット
        FacesContextMock.EXPECTED_CLIENT_ID = null;
        FacesContextMock.EXPECTED_MESSAGE =
                new FacesMessage(FacesMessage.SEVERITY_INFO,
                                 FacesMessage.SEVERITY_INFO.toString(),
                                 createExpectedMessageString(
                                     Messages.getMessageAsString(CORRESPONS_DELETED),
                                     null));

        boolean check = true;
        List<CorresponIndex> indexList = new ArrayList<CorresponIndex>();
        List<Correspon> expected = new ArrayList<Correspon>();
        for (Correspon correspon : list) {
            check = !check; // 偶数行にチェック
            CorresponIndex index = new CorresponIndex();
            index.setCorrespon(correspon);
            index.setChecked(check);
            indexList.add(index);

            if (check) {
                expected.add(correspon);
            }
        }
        page.setIndexList(indexList);

        page.setCondition(new SearchCorresponCondition());
        page.setPageNo(2);
        page.setSort("name");
        page.setDataCount(16);

        page.delete();

        List<Correspon> actual = MockCorresponSearchService.SET_DELETE;
        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < actual.size(); i++) {
            Correspon actCorrespon = actual.get(i);
            Correspon expCorrespon = expected.get(i);
            assertEquals(expCorrespon.getId(), actCorrespon.getId());
        }

        // 同一ページでリロードされている
        assertFalse(page.getNext());
        assertTrue(page.getPrevious());
        assertEquals(15, page.getDataCount());
        assertEquals(2, page.getPageNo());
        assertEquals("name", page.getSort());
        assertEquals(list, page.getCorresponList());
    }

    /**
     * 削除アクションのテスト.
     * 削除した結果、レコードが0件.
     * @throws Exception
     */
    @Test
    public void testDeleteNoDataFound() throws Exception {
        MockCorresponSearchService.ERROR_SEARCH
            = new ServiceAbortException(ApplicationMessageCode.NO_DATA_FOUND);

        // 期待されるメッセージをセット
        FacesContextMock.EXPECTED_CLIENT_ID = null;
        FacesContextMock.EXPECTED_MESSAGE =
                new FacesMessage(FacesMessage.SEVERITY_INFO,
                                 FacesMessage.SEVERITY_INFO.toString(),
                                 createExpectedMessageString(
                                     Messages.getMessageAsString(CORRESPONS_DELETED),
                                     null));

        boolean check = true;
        List<CorresponIndex> indexList = new ArrayList<CorresponIndex>();
        List<Correspon> expected = new ArrayList<Correspon>();
        for (Correspon correspon : list) {
            check = !check; // 偶数行にチェック
            CorresponIndex index = new CorresponIndex();
            index.setCorrespon(correspon);
            index.setChecked(check);
            indexList.add(index);

            if (check) {
                expected.add(correspon);
            }
        }
        page.setIndexList(indexList);

        page.setCondition(new SearchCorresponCondition());
        page.setPageNo(2);
        page.setSort("name");
        page.setDataCount(16);

        page.delete();

        List<Correspon> actual = MockCorresponSearchService.SET_DELETE;
        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < actual.size(); i++) {
            Correspon actCorrespon = actual.get(i);
            Correspon expCorrespon = expected.get(i);
            assertEquals(expCorrespon.getId(), actCorrespon.getId());
        }

        assertEquals(0, page.getDataCount());
    }

    /**
     * 削除アクションのテスト.
     * 削除した結果、該当ページのレコードが0件.
     * @throws Exception
     */
    @Test
    public void testDeleteNoPageFound() throws Exception {
        SearchCorresponResult result = new SearchCorresponResult();
        result.setCorresponList(list);
        result.setCount(15);
        MockCorresponSearchService.RET_SEARCH = result;

        // 期待されるメッセージをセット
        FacesContextMock.EXPECTED_CLIENT_ID = null;
        FacesContextMock.EXPECTED_MESSAGE =
                new FacesMessage(FacesMessage.SEVERITY_INFO,
                                 FacesMessage.SEVERITY_INFO.toString(),
                                 createExpectedMessageString(
                                     Messages.getMessageAsString(CORRESPONS_DELETED),
                                     null));

        boolean check = true;
        List<CorresponIndex> indexList = new ArrayList<CorresponIndex>();
        List<Correspon> expected = new ArrayList<Correspon>();
        for (Correspon correspon : list) {
            check = !check; // 偶数行にチェック
            CorresponIndex index = new CorresponIndex();
            index.setCorrespon(correspon);
            index.setChecked(check);
            indexList.add(index);

            if (check) {
                expected.add(correspon);
            }
        }
        page.setIndexList(indexList);

        page.setCondition(new SearchCorresponCondition());
        page.setPageNo(MockCorresponSearchService.ERROR_PAGE_NO);
        page.setSort("name");
        page.setDataCount(16);

        page.delete();

        List<Correspon> actual = MockCorresponSearchService.SET_DELETE;
        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < actual.size(); i++) {
            Correspon actCorrespon = actual.get(i);
            Correspon expCorrespon = expected.get(i);
            assertEquals(expCorrespon.getId(), actCorrespon.getId());
        }

        // １つ前のページでリロードされている
        assertFalse(page.getNext());
        assertTrue(page.getPrevious());
        assertEquals(15, page.getDataCount());
        assertEquals(4, page.getPageNo());
        assertEquals("name", page.getSort());
        assertEquals(list, page.getCorresponList());
    }

    /**
     * 前ページ（<<）アクションのテスト.
     * @throws Exception
     */
    @Test
    public void testMovePrevious() throws Exception {
        // ダミーのコレポン文書を設定
        SearchCorresponResult result = new SearchCorresponResult();
        result.setCorresponList(list);
        result.setCount(30);
        MockCorresponSearchService.RET_SEARCH = result;

        /* 全30件を10行ずつ表示 -- 全3ページ */
        page.setPageRowNum(10);
        SearchCorresponCondition condition = new SearchCorresponCondition();
        condition.setPageRowNum(10);
        page.setCondition(condition);

        // 3ページ目から1つ戻る
        page.setPageNo(3);
        page.movePrevious();

        assertEquals(2, page.getPageNo());
        assertEquals(list, page.getCorresponList());
        assertEquals(30, page.getDataCount());
        // 検索条件
        assertEquals(2, page.getCondition().getPageNo());

        // 再設定
        List<Correspon> newList = getNewList();
        result = new SearchCorresponResult();
        result.setCorresponList(newList);
        result.setCount(32); // 増加
        MockCorresponSearchService.RET_SEARCH = result;

        // さらに1つ戻る
        page.movePrevious();

        assertEquals(1, page.getPageNo());
        assertEquals(newList, page.getCorresponList());
        assertEquals(32, page.getDataCount()); // 増加
        // 検索条件
        assertEquals(1, page.getCondition().getPageNo());
    }

    /**
     * 次ページ（>>）アクションのテスト.
     * @throws Exception
     */
    @Test
    public void testMoveNext() throws Exception {
        // ダミーのコレポン文書を設定
        SearchCorresponResult result = new SearchCorresponResult();
        result.setCorresponList(list);
        result.setCount(30);
        MockCorresponSearchService.RET_SEARCH = result;

        /* 全30件を10行ずつ表示 -- 全3ページ */
        page.setPageRowNum(10);
        SearchCorresponCondition condition = new SearchCorresponCondition();
        condition.setPageRowNum(10);
        page.setCondition(condition);

        // 1ページ目から1つ進む
        page.setPageNo(1);
        page.moveNext();

        assertEquals(2, page.getPageNo());
        assertEquals(list, page.getCorresponList());
        assertEquals(30, page.getDataCount());
        // 検索条件
        assertEquals(2, page.getCondition().getPageNo());

        // 再設定
        List<Correspon> newList = getNewList();
        result = new SearchCorresponResult();
        result.setCorresponList(newList);
        result.setCount(32); // 増加
        MockCorresponSearchService.RET_SEARCH = result;

        // さらに1つ進む
        page.moveNext();

        assertEquals(3, page.getPageNo());
        assertEquals(newList, page.getCorresponList());
        assertEquals(32, page.getDataCount()); // 増加
        // 検索条件
        assertEquals(3, page.getCondition().getPageNo());
    }

    /**
     * ページ遷移アクションのテスト.
     * @throws Exception
     */
    @Test
    public void testChangePage() throws Exception {
        // ダミーのコレポン文書を設定
        SearchCorresponResult result = new SearchCorresponResult();
        result.setCorresponList(list);
        result.setCount(50);
        MockCorresponSearchService.RET_SEARCH = result;

        /* 全50件を10行ずつ表示 -- 全5ページ */
        page.setPageRowNum(10);
        page.setPageIndex(10);
        SearchCorresponCondition condition = new SearchCorresponCondition();
        condition.setPageRowNum(10);
        page.setCondition(condition);

        // 4ページ目へ
        page.setPageNo(4);
        page.changePage();

        assertEquals(4, page.getPageNo());
        assertEquals(list, page.getCorresponList());
        assertEquals(50, page.getDataCount());
        // 検索条件
        assertEquals(4, page.getCondition().getPageNo());

        // 再設定
        List<Correspon> newList = getNewList();
        result = new SearchCorresponResult();
        result.setCorresponList(newList);
        result.setCount(54); // 増加
        MockCorresponSearchService.RET_SEARCH = result;

        // 2ページ目へ
        page.setPageNo(2);
        page.changePage();

        assertEquals(2, page.getPageNo());
        assertEquals(newList, page.getCorresponList());
        assertEquals(54, page.getDataCount()); // 増加
        // 検索条件
        assertEquals(2, page.getCondition().getPageNo());
    }

    /**
     * ソートアクションのテスト.
     * ソート列がクリックされた時に検索を行うことを確認.
     * @throws Exception
     */
    @Test
    public void testSortIndex() throws Exception {
        // ダミーのコレポン文書を設定
        SearchCorresponResult result = new SearchCorresponResult();
        result.setCorresponList(list);
        result.setCount(50);
        MockCorresponSearchService.RET_SEARCH = result;

        /* 全50件を10行ずつ表示 -- 全5ページ */
        page.setPageRowNum(10);
        page.setPageIndex(10);
        page.setPageNo(2);
        SearchCorresponCondition condition = new SearchCorresponCondition();
        page.setCondition(condition);

        // ソート
        page.setSort("id");
        page.setAscending(false);
        page.sortIndex();

        assertEquals(2, page.getPageNo());
        assertEquals(list, page.getCorresponList());
        assertEquals(50, page.getDataCount());
        assertEquals("id", page.getSort());
        assertFalse(page.isAscending());
        // 検索条件
        assertEquals("id", page.getCondition().getSort());
        assertFalse(page.getCondition().isAscending());

        // 再設定
        List<Correspon> newList = getNewList();
        result = new SearchCorresponResult();
        result.setCorresponList(newList);
        result.setCount(54); // 増加
        MockCorresponSearchService.RET_SEARCH = result;

        // ソート
        page.setSort("correspon_no");
        page.setAscending(true);
        page.sortIndex();

        assertEquals(2, page.getPageNo());
        assertEquals(newList, page.getCorresponList());
        assertEquals(54, page.getDataCount()); // 増加
        assertEquals("correspon_no", page.getSort());
        assertTrue(page.isAscending());
        // 検索条件
        assertEquals("correspon_no", page.getCondition().getSort());
        assertTrue(page.getCondition().isAscending());
    }

    /**
     * 画面表示件数のテスト.
     * @throws Exception
     */
    @Test
    public void testPageDisplayNo() throws Exception {

        /* 全23件を10件ずつ表示の時 */
        page.setPageRowNum(10);
        page.setDataCount(23);

        page.setPageNo(1);
        assertEquals("1-10", page.getPageDisplayNo());
        page.setPageNo(2);
        assertEquals("11-20", page.getPageDisplayNo());
        page.setPageNo(3);
        assertEquals("21-23", page.getPageDisplayNo());

        /* 全22件を7件ずつ表示の時 */
        page.setPageRowNum(7);
        page.setDataCount(22);

        page.setPageNo(1);
        assertEquals("1-7", page.getPageDisplayNo());
        page.setPageNo(2);
        assertEquals("8-14", page.getPageDisplayNo());
        page.setPageNo(3);
        assertEquals("15-21", page.getPageDisplayNo());
        page.setPageNo(4);
        assertEquals("22-22", page.getPageDisplayNo());

        /* 全16件を20件ずつ表示の時 */
        page.setPageRowNum(20);
        page.setDataCount(16);

        page.setPageNo(1);
        assertEquals("1-16", page.getPageDisplayNo());
    }

    /**
     * ページリンク表示のテスト.
     * @throws Exception
     */
    @Test
    public void testPagingNo() throws Exception {

        String[] result = null;

        /* 全136件を10件ごと、10ページ分表示の時 */
        page.setPageRowNum(10);
        page.setDataCount(136);
        page.setPageIndex(10);

        // 5ページまでは表示が変わらない
        String[] expected = { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" };
        for (int i = 0; i < 5; i++) {
            page.setPageNo(i + 1);
            result = page.getPagingNo();

            assertEquals(10, result.length);
            for (int j = 0; j < 10; j++) {
                assertEquals(expected[j], result[j]);
            }
        }

        // 6ページからは1ページずつ表示がずれる
        String[] expected6 = { "2", "3", "4", "5", "6", "7", "8", "9", "10", "11" };
        page.setPageNo(6);
        result = page.getPagingNo();

        assertEquals(10, result.length);
        for (int i = 0; i < 10; i++) {
            assertEquals(expected6[i], result[i]);
        }
        String[] expected7 = { "3", "4", "5", "6", "7", "8", "9", "10", "11", "12" };
        page.setPageNo(7);
        result = page.getPagingNo();

        assertEquals(10, result.length);
        for (int i = 0; i < 10; i++) {
            assertEquals(expected7[i], result[i]);
        }
        String[] expected8 = { "4", "5", "6", "7", "8", "9", "10", "11", "12", "13" };
        page.setPageNo(8);
        result = page.getPagingNo();

        assertEquals(10, result.length);
        for (int i = 0; i < 10; i++) {
            assertEquals(expected8[i], result[i]);
        }

        // 9ページからは表示が変わらない
        String[] expectedEnd = { "5", "6", "7", "8", "9", "10", "11", "12", "13", "14" };
        for (int i = 8; i < 14; i++) {
            page.setPageNo(i + 1);
            result = page.getPagingNo();

            assertEquals(10, result.length);
            for (int j = 0; j < 10; j++) {
                assertEquals(expectedEnd[j], result[j]);
            }
        }

        /* 全56件を7件ごと、5ページ分表示の時 */
        page.setPageRowNum(7);
        page.setDataCount(56);
        page.setPageIndex(5);

        // 2ページまでは表示が変わらない
        String[] expectedHalf = { "1", "2", "3", "4", "5" };
        for (int i = 0; i < 3; i++) {
            page.setPageNo(i + 1);
            result = page.getPagingNo();

            assertEquals(5, result.length);
            for (int j = 0; j < 5; j++) {
                assertEquals(expectedHalf[j], result[j]);
            }
        }

        // 3ページからは1ページずつ表示がずれる
        String[] expectedHalf4 = { "2", "3", "4", "5", "6" };
        page.setPageNo(4);
        result = page.getPagingNo();

        assertEquals(5, result.length);
        for (int i = 0; i < 5; i++) {
            assertEquals(expectedHalf4[i], result[i]);
        }
        String[] expectedHalf5 = { "3", "4", "5", "6", "7" };
        page.setPageNo(5);
        result = page.getPagingNo();

        assertEquals(5, result.length);
        for (int i = 0; i < 5; i++) {
            assertEquals(expectedHalf5[i], result[i]);
        }
        // 5ページからは表示が変わらない
        String[] expectedHalfEnd = { "4", "5", "6", "7", "8" };
        for (int i = 5; i < 8; i++) {
            page.setPageNo(i + 1);
            result = page.getPagingNo();

            assertEquals(5, result.length);
            for (int j = 0; j < 5; j++) {
                assertEquals(expectedHalfEnd[j], result[j]);
            }
        }

        /* 全317件を15件ごと、20ページ分表示の時 */
        page.setPageRowNum(15);
        page.setDataCount(317);
        page.setPageIndex(20);

        // 10ページまでは表示が変わらない
        String[] expectedDouble =
                { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15",
                 "16", "17", "18", "19", "20" };
        for (int i = 0; i < 10; i++) {
            page.setPageNo(i + 1);
            result = page.getPagingNo();

            assertEquals(20, result.length);
            for (int j = 0; j < 20; j++) {
                assertEquals(expectedDouble[j], result[j]);
            }
        }

        // 11ページで1ページ表示がずれる
        String[] expectedDouble11 =
                { "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16",
                 "17", "18", "19", "20", "21" };
        page.setPageNo(11);
        result = page.getPagingNo();

        assertEquals(20, result.length);
        for (int i = 0; i < 20; i++) {
            assertEquals(expectedDouble11[i], result[i]);
        }

        // 12ページからは表示が変わらない
        String[] expectedDoubleEnd =
                { "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16",
                 "17", "18", "19", "20", "21", "22" };
        for (int i = 11; i < 22; i++) {
            page.setPageNo(i + 1);
            result = page.getPagingNo();

            assertEquals(20, result.length);
            for (int j = 0; j < 20; j++) {
                assertEquals(expectedDoubleEnd[j], result[j]);
            }
        }

        /* 全1件を1件ごと、1ページ分表示の時 */
        page.setPageRowNum(1);
        page.setDataCount(1);
        page.setPageIndex(1);
        String[] expectedMin = { "1" };

        page.setPageNo(1);
        result = page.getPagingNo();

        assertEquals(1, result.length);
        for (int i = 0; i < 1; i++) {
            assertEquals(expectedMin[i], result[i]);
        }
    }

    /**
     * 前ページ（<<）のリンク表示判定テスト.
     * @throws Exception
     */
    @Test
    public void testPrevious() throws Exception {
        // 1ページ目のみリンク表示しない
        page.setPageNo(1);
        assertFalse(page.getPrevious());

        for (int i = 2; i < 10; i++) {
            page.setPageNo(i);
            assertTrue(page.getPrevious());
        }
    }

    /**
     * 次ページ（>>）のリンク表示判定テスト.
     * @throws Exception
     */
    @Test
    public void testNext() throws Exception {
        /* 全136件を10件ごと、10ページ分表示の時 */
        page.setPageRowNum(10);
        page.setDataCount(136);
        page.setPageIndex(10);

        for (int i = 1; i < 14; i++) {
            page.setPageNo(i);
            assertTrue(page.getNext());
        }
        // 最終ページのみリンク表示しない
        page.setPageNo(14);
        assertFalse(page.getNext());

        /* 全56件を7件ごと、5ページ分表示の時 */
        page.setPageRowNum(7);
        page.setDataCount(56);
        page.setPageIndex(5);

        for (int i = 1; i < 8; i++) {
            page.setPageNo(i);
            assertTrue(page.getNext());
        }
        // 最終ページのみリンク表示しない
        page.setPageNo(8);
        assertFalse(page.getNext());

        /* 全317件を15件ごと、20ページ分表示の時 */
        page.setPageRowNum(15);
        page.setDataCount(317);
        page.setPageIndex(20);

        for (int i = 1; i < 22; i++) {
            page.setPageNo(i);
            assertTrue(page.getNext());
        }
        // 最終ページのみリンク表示しない
        page.setPageNo(22);
        assertFalse(page.getNext());
    }

    @Test
    public void testGetCustomFieldText() {
        //  テストデータの準備
        List<CustomField> customFieldList = createCustomFieldList();
        page.setCustomFieldList(customFieldList);

        SearchCorresponCondition c = new SearchCorresponCondition();
        page.setCondition(c);


        // 検索条件未設定の場合はnullが返される
        c.setCustomFieldNo(null);
        assertNull(page.getCustomFieldText());


        CustomField f;
        // 検索条件が設定されている場合はそのラベルが返される
        c.setCustomFieldNo(11L); // see #createCustomFieldList
        f = findByProjectCustomFieldId(customFieldList, c.getCustomFieldNo());
        assertEquals(f.getLabel(), page.getCustomFieldText());

        c.setCustomFieldNo(33L); // see #createCustomFieldList
        f = findByProjectCustomFieldId(customFieldList, c.getCustomFieldNo());
        assertEquals(f.getLabel(), page.getCustomFieldText());
    }

    /**
     * 表示件数変更アクションのテスト.
     * @throws Exception
     */
    @Test
    public void testChangePageRow() throws Exception {
        // Serviceが返すダミーのコレポン文書を設定
        SearchCorresponResult result = new SearchCorresponResult();
        result.setCorresponList(list);
        result.setCount(5);
        MockCorresponSearchService.RET_SEARCH = result;

        SearchCorresponCondition condition = new SearchCorresponCondition();
        condition.setProjectId("MOCK");
        page.setCondition(condition);
        page.setSort("id");
        page.setAscending(true);

        page.setTypeList(createTypeList());
        page.setWorkflowList(WorkflowStatus.values());
        page.setReadStatusList(ReadStatus.values());
        page.setStatusList(CorresponStatus.values());
        page.setUserList(createUserList());
        page.setWorkflowProcessesList(WorkflowProcessStatus.values());
        page.setGroupList(createGroupList());
        page.setCustomFieldList(createCustomFieldList());

        // 検索条件
        page.setSimpleSearch(false);
        page.setFromNo("1");
        page.setToNo("2");
        page.setCorresponNo("Test Correspon");
        Long[] types = {1L};
        page.setTypes(types);
        Integer[] workflowStatuses = {WorkflowStatus.DENIED.getValue()};
        page.setWorkflowStatuses(workflowStatuses);
        Integer[] readStatuses = {ReadStatus.NEW.getValue()};
        page.setReadStatuses(readStatuses);
        Integer[] corresponStatuses = {CorresponStatus.CLOSED.getValue()};
        page.setStatuses(corresponStatuses);
        String[] users = {"00001"};
//        page.setAddressUsers(users);
//        page.setAddressFrom(true);
//        page.setAddressCc(true);
//        page.setAddressAttention(true);
//        page.setAddressPIC(true);
//        page.setAddressUnreplied(true);
        page.setFromUsers(users);
        page.setUserPreparer(true);
        page.setToUsers(users);
        page.setUserCc(true);
        page.setUserAttention(true);
        page.setUserPic(true);
        page.setUserUnreplied(true);
//        page.setWorkflowUsers(users);
        Integer[] processStatuses = {WorkflowProcessStatus.DENIED.getValue()};
        page.setWorkflowProcesses(processStatuses);
        Long[] groups = {1L};
//        page.setGroups(groups);
//        page.setGroupFrom(true);
        page.setFromGroups(groups);
        page.setToGroups(groups);
        page.setGroupTo(true);
        page.setGroupCc(true);
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        Date expDate1 = new GregorianCalendar(2009, 3, 1).getTime();
        Date date1 = new GregorianCalendar(2009, 3, 1, 10, 20, 30).getTime();
        page.setFromCreated(f.format(date1));
        Date expDate2 = new GregorianCalendar(2009, 3, 2).getTime();
        Date date2 = new GregorianCalendar(2009, 3, 2, 10, 20, 30).getTime();
        page.setToCreated(f.format(date2));
        Date expDate3 = new GregorianCalendar(2009, 3, 3).getTime();
        Date date3 = new GregorianCalendar(2009, 3, 3, 10, 20, 30).getTime();
        page.setFromIssued(f.format(date3));
        Date expDate4 = new GregorianCalendar(2009, 3, 4).getTime();
        Date date4 = new GregorianCalendar(2009, 3, 4, 10, 20, 30).getTime();
        page.setToIssued(f.format(date4));
        Date expDate5 = new GregorianCalendar(2009, 3, 5).getTime();
        Date date5 = new GregorianCalendar(2009, 3, 5, 10, 20, 30).getTime();
        page.setFromReply(f.format(date5));
        Date expDate6 = new GregorianCalendar(2009, 3, 6).getTime();
        Date date6 = new GregorianCalendar(2009, 3, 6, 10, 20, 30).getTime();
        page.setToReply(f.format(date6));
        page.setCustomFieldNo(1L);
        page.setCustomFieldValue("Field Value");

        page.search();

        MockViewHelper.RET_COOKIE_VALUE = "30";
        page.changePageRowNum();

        assertEquals(list, page.getCorresponList());
        assertEquals(5, page.getDataCount());

        // 初期設定を確認
        assertFalse(page.getNext());
        assertFalse(page.getPrevious());
        assertEquals(10, page.getPageIndex());
        assertEquals(5, page.getDataCount());
        assertEquals(1, page.getPageNo());
        assertEquals(30, page.getPageRowNum());
        assertEquals("id", page.getSort());
        // 検索条件
        assertFalse(page.isSimpleSearch());
        assertEquals(Long.valueOf(1L), page.getCondition().getFromId());
        assertEquals(Long.valueOf(2L), page.getCondition().getToId());
        assertEquals("Test Correspon", page.getCondition().getCorresponNo());
        assertEquals("TypeA",
                     page.getCondition().getCorresponTypes()[0].getCorresponType());
        assertEquals(WorkflowStatus.DENIED,
                     page.getCondition().getWorkflowStatuses()[0]);
        assertEquals(ReadStatus.NEW,
                     page.getCondition().getReadStatuses()[0]);
        assertEquals(CorresponStatus.CLOSED,
                     page.getCondition().getCorresponStatuses()[0]);
//        assertEquals("A User",
//                     page.getCondition().getAddressUsers()[0].getNameE());
//        assertTrue(page.getCondition().isAddressFrom());
//        assertTrue(page.getCondition().isAddressCc());
//        assertTrue(page.getCondition().isAddressAttention());
//        assertTrue(page.getCondition().isAddressPersonInCharge());
//        assertTrue(page.getCondition().isAddressUnreplied());
        assertEquals("A User",
                page.getCondition().getFromUsers()[0].getNameE());
        assertEquals("A User",
                page.getCondition().getToUsers()[0].getNameE());
        assertTrue(page.getCondition().isUserPreparer());
        assertTrue(page.getCondition().isUserCc());
        assertTrue(page.getCondition().isUserAttention());
        assertTrue(page.getCondition().isUserPic());
        assertTrue(page.getCondition().isUserUnreplied());
//        assertEquals("A User",
//                     page.getCondition().getWorkflowUsers()[0].getNameE());
        assertEquals(WorkflowProcessStatus.DENIED,
                     page.getCondition().getWorkflowProcessStatuses()[0]);
//        assertEquals("A Group",
//                     page.getCondition().getCorresponGroups()[0].getName());
//        assertTrue(page.getCondition().isGroupFrom());
        assertEquals("A Group", page.getCondition().getFromGroups()[0].getName());
        assertEquals("A Group", page.getCondition().getToGroups()[0].getName());
        assertTrue(page.getCondition().isGroupTo());
        assertTrue(page.getCondition().isGroupCc());
        assertEquals(expDate1, page.getCondition().getFromCreatedOn());
        assertEquals(expDate2, page.getCondition().getToCreatedOn());
        assertEquals(expDate3, page.getCondition().getFromIssuedOn());
        assertEquals(expDate4, page.getCondition().getToIssuedOn());
        assertEquals(expDate5, page.getCondition().getFromDeadlineForReply());
        assertEquals(expDate6, page.getCondition().getToDeadlineForReply());
        assertEquals(Long.valueOf(1L), page.getCondition().getCustomFieldNo());
        assertEquals("Field Value", page.getCondition().getCustomFieldValue());
        assertEquals(1, page.getCondition().getPageNo());
        assertEquals("id", page.getCondition().getSort());
        assertTrue(page.getCondition().isAscending());
        assertEquals(page.getCondition(), MockAbstractPage.SET_CONDITION);
        assertEquals(30, page.getCondition().getPageRowNum());

        MockViewHelper.RET_COOKIE_VALUE = "abc";
        page.changePageRowNum();

        assertEquals(list, page.getCorresponList());
        assertEquals(5, page.getDataCount());

        // 初期設定を確認
        assertFalse(page.getNext());
        assertFalse(page.getPrevious());
        assertEquals(10, page.getPageIndex());
        assertEquals(5, page.getDataCount());
        assertEquals(1, page.getPageNo());
        assertEquals(30, page.getPageRowNum());
        assertEquals("id", page.getSort());
        // 検索条件
        assertFalse(page.isSimpleSearch());
        assertEquals(Long.valueOf(1L), page.getCondition().getFromId());
        assertEquals(Long.valueOf(2L), page.getCondition().getToId());
        assertEquals("Test Correspon", page.getCondition().getCorresponNo());
        assertEquals("TypeA",
                     page.getCondition().getCorresponTypes()[0].getCorresponType());
        assertEquals(WorkflowStatus.DENIED,
                     page.getCondition().getWorkflowStatuses()[0]);
        assertEquals(ReadStatus.NEW,
                     page.getCondition().getReadStatuses()[0]);
        assertEquals(CorresponStatus.CLOSED,
                     page.getCondition().getCorresponStatuses()[0]);
//        assertEquals("A User",
//                     page.getCondition().getAddressUsers()[0].getNameE());
//        assertTrue(page.getCondition().isAddressFrom());
//        assertTrue(page.getCondition().isAddressCc());
//        assertTrue(page.getCondition().isAddressAttention());
//        assertTrue(page.getCondition().isAddressPersonInCharge());
//        assertTrue(page.getCondition().isAddressUnreplied());
        assertEquals("A User",
                page.getCondition().getFromUsers()[0].getNameE());
        assertEquals("A User",
                page.getCondition().getToUsers()[0].getNameE());
        assertTrue(page.getCondition().isUserPreparer());
        assertTrue(page.getCondition().isUserCc());
        assertTrue(page.getCondition().isUserAttention());
        assertTrue(page.getCondition().isUserPic());
        assertTrue(page.getCondition().isUserUnreplied());
//        assertEquals("A User",
//                     page.getCondition().getWorkflowUsers()[0].getNameE());
        assertEquals(WorkflowProcessStatus.DENIED,
                     page.getCondition().getWorkflowProcessStatuses()[0]);
//        assertEquals("A Group",
//                     page.getCondition().getCorresponGroups()[0].getName());
//        assertTrue(page.getCondition().isGroupFrom());
        assertEquals("A Group", page.getCondition().getFromGroups()[0].getName());
        assertEquals("A Group", page.getCondition().getToGroups()[0].getName());
        assertTrue(page.getCondition().isGroupTo());
        assertTrue(page.getCondition().isGroupCc());
        assertEquals(expDate1, page.getCondition().getFromCreatedOn());
        assertEquals(expDate2, page.getCondition().getToCreatedOn());
        assertEquals(expDate3, page.getCondition().getFromIssuedOn());
        assertEquals(expDate4, page.getCondition().getToIssuedOn());
        assertEquals(expDate5, page.getCondition().getFromDeadlineForReply());
        assertEquals(expDate6, page.getCondition().getToDeadlineForReply());
        assertEquals(Long.valueOf(1L), page.getCondition().getCustomFieldNo());
        assertEquals("Field Value", page.getCondition().getCustomFieldValue());
        assertEquals(1, page.getCondition().getPageNo());
        assertEquals("id", page.getCondition().getSort());
        assertTrue(page.getCondition().isAscending());
        assertEquals(page.getCondition(), MockAbstractPage.SET_CONDITION);
        assertEquals(30, page.getCondition().getPageRowNum());
    }

    /**
     * 表示件数変更アクションのテスト(PAGE NOT FOUND).
     * @throws Exception
     */
    @Test
    public void testChangePageRowPageNotFound() throws Exception {
        // ダミーのコレポン文書を設定
        SearchCorresponResult result = new SearchCorresponResult();
        result.setCorresponList(list);
        result.setCount(50);
        MockCorresponSearchService.RET_SEARCH = result;

        /* 全50件を10行ずつ表示 -- 全5ページ */
        page.setPageRowNum(10);
        page.setPageIndex(10);
        SearchCorresponCondition condition = new SearchCorresponCondition();
        condition.setPageRowNum(10);
        condition.setPageNo(MockCorresponSearchService.ERROR_PAGE_NO);
        page.setCondition(condition);
        page.setPageIndex(MockCorresponSearchService.ERROR_PAGE_NO);

        MockViewHelper.RET_COOKIE_VALUE = "30";
        page.changePageRowNum();

        assertEquals(1, page.getCondition().getPageNo());
        assertEquals(1, page.getPageNo());
        assertEquals(list, page.getCorresponList());
        assertEquals(50, page.getDataCount());
        assertEquals(30, page.getPageRowNum());
    }

    /**
     * 表示件数変更アクションのテスト(DATA NOT FOUND).
     * @throws Exception
     */
    @Test
    public void testChangePageRowDataNotFound() throws Exception {
        // 期待するエラーをセット
        MockCorresponSearchService.ERROR_SEARCH
            = new ServiceAbortException(ApplicationMessageCode.NO_DATA_FOUND);
        FacesContextMock.EXPECTED_CLIENT_ID = null;
        FacesContextMock.EXPECTED_MESSAGE =
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                 FacesMessage.SEVERITY_ERROR.toString(),
                                 createExpectedMessageString(
                                     Messages.getMessageAsString(ApplicationMessageCode.NO_DATA_FOUND),
                                     null));

        page.setPageRowNum(10);
        page.setPageIndex(10);
        SearchCorresponCondition condition = new SearchCorresponCondition();
        condition.setPageRowNum(10);
        condition.setPageNo(MockCorresponSearchService.ERROR_PAGE_NO - 1);
        page.setCondition(condition);
        page.setPageIndex(MockCorresponSearchService.ERROR_PAGE_NO - 1);

        MockViewHelper.RET_COOKIE_VALUE = "30";
        page.changePageRowNum();
        assertEquals(30, page.getPageRowNum());
    }

    /**
     * Advanced Search表示のテスト.
     */
    @Test
    public void testShowAdvancedSearch() {
        page.setAdvancedSearchDisplayed(false);

        // Advanced Searchが表示状態になる
        SearchCorresponCondition condition = new SearchCorresponCondition();
        page.setCondition(condition);
        page.showAdvancedSearch();
        assertTrue(page.isAdvancedSearchDisplayed());
    }

    /**
     * コレポンタイプ取得のテスト.
     */
    @Test
    public void testGetTypeSelectList() {
        MockViewHelper.RET_CREATE_SELECT_LIST = createDummyList(new CorresponType());

        List<SelectItem> list = page.getTypeSelectList();
        assertEquals(0, list.size());

        page.setAdvancedSearchDisplayed(true);
        list = page.getTypeSelectList();
        assertEquals(1, list.size());
    }

    /**
     * ワークフローステータス取得のテスト.
     */
    @Test
    public void testGetWorkflowStatusSelectList() {
        MockViewHelper.RET_CREATE_SELECT_LIST = createDummyList(WorkflowStatus.ISSUED);

        List<SelectItem> list = page.getWorkflowStatusSelectList();
        assertEquals(0, list.size());

        page.setAdvancedSearchDisplayed(true);
        list = page.getWorkflowStatusSelectList();
        assertEquals(1, list.size());
    }

    /**
     * 既読・未読状態取得のテスト.
     */
    @Test
    public void testGetReadStatusSelectList() {
        MockViewHelper.RET_CREATE_SELECT_LIST = createDummyList(ReadStatus.READ);

        List<SelectItem> list = page.getReadStatusSelectList();
        assertEquals(0, list.size());

        page.setAdvancedSearchDisplayed(true);
        list = page.getReadStatusSelectList();
        assertEquals(1, list.size());
    }

    /**
     * コレポンステータス取得のテスト.
     */
    @Test
    public void testGetStatusSelectList() {
        MockViewHelper.RET_CREATE_SELECT_LIST = createDummyList(CorresponStatus.OPEN);

        List<SelectItem> list = page.getStatusSelectList();
        assertEquals(0, list.size());

        page.setAdvancedSearchDisplayed(true);
        list = page.getStatusSelectList();
        assertEquals(1, list.size());
    }

    /**
     * ユーザー取得のテスト.
     */
    @Test
    public void testGetUserSelectList() {
        List<ProjectUser> users = new ArrayList<ProjectUser>();
        ProjectUser pu = new ProjectUser();
        User user = new User();
        user.setEmpNo("U0001");
        user.setNameE("User1");
        pu.setUser(user);
        users.add(pu);
        page.setUserList(users);

        List<SelectItem> list = page.getUserSelectList();
        assertEquals(0, list.size());

        page.setAdvancedSearchDisplayed(true);
        list = page.getUserSelectList();
        assertEquals(1, list.size());
    }

    /**
     * ワークスフロー作業状態取得のテスト.
     */
    @Test
    public void testGetWorkflowProcessSelectList() {
        MockViewHelper.RET_CREATE_SELECT_LIST = createDummyList(WorkflowProcessStatus.APPROVED);

        List<SelectItem> list = page.getWorkflowProcessSelectList();
        assertEquals(0, list.size());

        page.setAdvancedSearchDisplayed(true);
        list = page.getWorkflowProcessSelectList();
        assertEquals(1, list.size());
    }

    /**
     * グループ取得のテスト.
     */
    @Test
    public void testGetGroupSelectList() {
        MockViewHelper.RET_CREATE_SELECT_LIST = createDummyList(new CorresponGroup());

        List<SelectItem> list = page.getGroupSelectList();
        assertEquals(0, list.size());

        page.setAdvancedSearchDisplayed(true);
        list = page.getGroupSelectList();
        assertEquals(1, list.size());
    }

    /**
     * カスタムフィールド取得のテスト.
     */
    @Test
    public void testGetCustomFieldSelectList() {
        MockViewHelper.RET_CREATE_SELECT_LIST = createDummyList(new CustomField());

        List<SelectItem> list = page.getCustomFieldSelectList();
        assertEquals(0, list.size());

        page.setAdvancedSearchDisplayed(true);
        list = page.getCustomFieldSelectList();
        assertEquals(1, list.size());
    }

    private List<SelectItem> createDummyList(Object dto) {
        List<SelectItem> result = new ArrayList<SelectItem>();
        result.add(new SelectItem(dto, "label"));
        return result;
    }

    private CustomField findByProjectCustomFieldId(List<CustomField> list, Long id) {
        for (CustomField f : list) {
            if (id.equals(f.getProjectCustomFieldId())) {
                return f;
            }
        }
        return null;
    }

    private List<Correspon> getList() {
        // ダミーの戻り値をセット
        List<Correspon> list = new ArrayList<Correspon>();
        Correspon c = new Correspon();
        c.setId(1L);
        c.setCorresponNo("YOC:OT:BUILDING-00001");
        CorresponGroup from = new CorresponGroup();
        from.setId(new Long(1L));
        from.setName("YOC:BUILDING");
        c.setFromCorresponGroup(from);
        c.setSubject("Mock");
        CorresponType ct = new CorresponType();
        ct.setId(new Long(1L));
        ct.setName("Request");
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
        c.setCorresponReadStatus(new CorresponReadStatus());
        list.add(c);

        c = new Correspon();
        c.setId(2L);
        c.setCorresponNo("YOC:OT:BUILDING-00001");
        from = new CorresponGroup();
        from.setId(new Long(1L));
        from.setName("YOC:BUILDING");
        c.setFromCorresponGroup(from);
        c.setSubject("Mock");
        ct = new CorresponType();
        ct.setId(new Long(1L));
        ct.setName("Request");
        c.setCorresponType(ct);
        c.setWorkflowStatus(WorkflowStatus.DRAFT);
        c.setCreatedAt(new GregorianCalendar(2009, 3, 1, 1, 1, 1).getTime());
        c.setUpdatedAt(new GregorianCalendar(2009, 3, 10, 10, 10, 10).getTime());
        u = new User();
        u.setEmpNo("00001");
        u.setNameE("Test User");
        c.setCreatedBy(u);
        c.setUpdatedBy(u);
        c.setDeadlineForReply(new GregorianCalendar(2009, 4, 1).getTime());
        c.setCorresponStatus(CorresponStatus.OPEN);
        c.setCorresponReadStatus(new CorresponReadStatus());
        list.add(c);

        c = new Correspon();
        c.setId(3L);
        c.setCorresponNo("YOC:OT:BUILDING-00001");
        from = new CorresponGroup();
        from.setId(new Long(1L));
        from.setName("YOC:BUILDING");
        c.setFromCorresponGroup(from);
        c.setSubject("Mock");
        ct = new CorresponType();
        ct.setId(new Long(1L));
        ct.setName("Request");
        c.setCorresponType(ct);
        c.setWorkflowStatus(WorkflowStatus.DRAFT);
        c.setCreatedAt(new GregorianCalendar(2009, 3, 1, 1, 1, 1).getTime());
        c.setUpdatedAt(new GregorianCalendar(2009, 3, 10, 10, 10, 10).getTime());
        u = new User();
        u.setEmpNo("00001");
        u.setNameE("Test User");
        c.setCreatedBy(u);
        c.setUpdatedBy(u);
        c.setDeadlineForReply(new GregorianCalendar(2009, 4, 1).getTime());
        c.setCorresponStatus(CorresponStatus.OPEN);
        c.setCorresponReadStatus(new CorresponReadStatus());
        list.add(c);

        c = new Correspon();
        c.setId(4L);
        c.setCorresponNo("YOC:OT:BUILDING-00001");
        from = new CorresponGroup();
        from.setId(new Long(1L));
        from.setName("YOC:BUILDING");
        c.setFromCorresponGroup(from);
        c.setSubject("Mock");
        ct = new CorresponType();
        ct.setId(new Long(1L));
        ct.setName("Request");
        c.setCorresponType(ct);
        c.setWorkflowStatus(WorkflowStatus.DRAFT);
        c.setCreatedAt(new GregorianCalendar(2009, 3, 1, 1, 1, 1).getTime());
        c.setUpdatedAt(new GregorianCalendar(2009, 3, 10, 10, 10, 10).getTime());
        u = new User();
        u.setEmpNo("00001");
        u.setNameE("Test User");
        c.setCreatedBy(u);
        c.setUpdatedBy(u);
        c.setDeadlineForReply(new GregorianCalendar(2009, 4, 1).getTime());
        c.setCorresponStatus(CorresponStatus.OPEN);
        c.setCorresponReadStatus(new CorresponReadStatus());
        list.add(c);

        c = new Correspon();
        c.setId(5L);
        c.setCorresponNo("YOC:OT:BUILDING-00001");
        from = new CorresponGroup();
        from.setId(new Long(1L));
        from.setName("YOC:BUILDING");
        c.setFromCorresponGroup(from);
        c.setSubject("Mock");
        ct = new CorresponType();
        ct.setId(new Long(1L));
        ct.setName("Request");
        c.setCorresponType(ct);
        c.setWorkflowStatus(WorkflowStatus.DRAFT);
        c.setCreatedAt(new GregorianCalendar(2009, 3, 1, 1, 1, 1).getTime());
        c.setUpdatedAt(new GregorianCalendar(2009, 3, 10, 10, 10, 10).getTime());
        u = new User();
        u.setEmpNo("00001");
        u.setNameE("Test User");
        c.setCreatedBy(u);
        c.setUpdatedBy(u);
        c.setDeadlineForReply(new GregorianCalendar(2009, 4, 1).getTime());
        c.setCorresponStatus(CorresponStatus.OPEN);
        c.setCorresponReadStatus(new CorresponReadStatus());
        list.add(c);

        return list;
    }

    private List<Correspon> getNewList() {
        // ダミーの戻り値をセット
        List<Correspon> list = new ArrayList<Correspon>();
        Correspon c = new Correspon();
        c.setId(6L);
        c.setCorresponNo("YOC:OT:BUILDING-00001");
        CorresponGroup from = new CorresponGroup();
        from.setId(new Long(1L));
        from.setName("YOC:BUILDING");
        c.setFromCorresponGroup(from);
        c.setSubject("Mock");
        CorresponType ct = new CorresponType();
        ct.setId(new Long(1L));
        ct.setName("Request");
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
        c.setCorresponReadStatus(new CorresponReadStatus());
        list.add(c);

        c = new Correspon();
        c.setId(7L);
        c.setCorresponNo("YOC:OT:BUILDING-00001");
        from = new CorresponGroup();
        from.setId(new Long(1L));
        from.setName("YOC:BUILDING");
        c.setFromCorresponGroup(from);
        c.setSubject("Mock");
        ct = new CorresponType();
        ct.setId(new Long(1L));
        ct.setName("Request");
        c.setCorresponType(ct);
        c.setWorkflowStatus(WorkflowStatus.DRAFT);
        c.setCreatedAt(new GregorianCalendar(2009, 3, 1, 1, 1, 1).getTime());
        c.setUpdatedAt(new GregorianCalendar(2009, 3, 10, 10, 10, 10).getTime());
        u = new User();
        u.setEmpNo("00001");
        u.setNameE("Test User");
        c.setCreatedBy(u);
        c.setUpdatedBy(u);
        c.setDeadlineForReply(new GregorianCalendar(2009, 4, 1).getTime());
        c.setCorresponStatus(CorresponStatus.OPEN);
        c.setCorresponReadStatus(new CorresponReadStatus());
        list.add(c);

        c = new Correspon();
        c.setId(8L);
        c.setCorresponNo("YOC:OT:BUILDING-00001");
        from = new CorresponGroup();
        from.setId(new Long(1L));
        from.setName("YOC:BUILDING");
        c.setFromCorresponGroup(from);
        c.setSubject("Mock");
        ct = new CorresponType();
        ct.setId(new Long(1L));
        ct.setName("Request");
        c.setCorresponType(ct);
        c.setWorkflowStatus(WorkflowStatus.DRAFT);
        c.setCreatedAt(new GregorianCalendar(2009, 3, 1, 1, 1, 1).getTime());
        c.setUpdatedAt(new GregorianCalendar(2009, 3, 10, 10, 10, 10).getTime());
        u = new User();
        u.setEmpNo("00001");
        u.setNameE("Test User");
        c.setCreatedBy(u);
        c.setUpdatedBy(u);
        c.setDeadlineForReply(new GregorianCalendar(2009, 4, 1).getTime());
        c.setCorresponStatus(CorresponStatus.OPEN);
        c.setCorresponReadStatus(new CorresponReadStatus());
        list.add(c);

        c = new Correspon();
        c.setId(9L);
        c.setCorresponNo("YOC:OT:BUILDING-00001");
        from = new CorresponGroup();
        from.setId(new Long(1L));
        from.setName("YOC:BUILDING");
        c.setFromCorresponGroup(from);
        c.setSubject("Mock");
        ct = new CorresponType();
        ct.setId(new Long(1L));
        ct.setName("Request");
        c.setCorresponType(ct);
        c.setWorkflowStatus(WorkflowStatus.DRAFT);
        c.setCreatedAt(new GregorianCalendar(2009, 3, 1, 1, 1, 1).getTime());
        c.setUpdatedAt(new GregorianCalendar(2009, 3, 10, 10, 10, 10).getTime());
        u = new User();
        u.setEmpNo("00001");
        u.setNameE("Test User");
        c.setCreatedBy(u);
        c.setUpdatedBy(u);
        c.setDeadlineForReply(new GregorianCalendar(2009, 4, 1).getTime());
        c.setCorresponStatus(CorresponStatus.OPEN);
        c.setCorresponReadStatus(new CorresponReadStatus());
        list.add(c);

        c = new Correspon();
        c.setId(10L);
        c.setCorresponNo("YOC:OT:BUILDING-00001");
        from = new CorresponGroup();
        from.setId(new Long(1L));
        from.setName("YOC:BUILDING");
        c.setFromCorresponGroup(from);
        c.setSubject("Mock");
        ct = new CorresponType();
        ct.setId(new Long(1L));
        ct.setName("Request");
        c.setCorresponType(ct);
        c.setWorkflowStatus(WorkflowStatus.DRAFT);
        c.setCreatedAt(new GregorianCalendar(2009, 3, 1, 1, 1, 1).getTime());
        c.setUpdatedAt(new GregorianCalendar(2009, 3, 10, 10, 10, 10).getTime());
        u = new User();
        u.setEmpNo("00001");
        u.setNameE("Test User");
        c.setCreatedBy(u);
        c.setUpdatedBy(u);
        c.setDeadlineForReply(new GregorianCalendar(2009, 4, 1).getTime());
        c.setCorresponStatus(CorresponStatus.OPEN);
        c.setCorresponReadStatus(new CorresponReadStatus());
        list.add(c);

        return list;
    }

    private List<CorresponType> createTypeList() {
        List<CorresponType> list = new ArrayList<CorresponType>();
        CorresponType type = new CorresponType();
        type.setProjectCorresponTypeId(1L);
        type.setCorresponType("TypeA");
        list.add(type);
        type = new CorresponType();
        type.setProjectCorresponTypeId(2L);
        type.setCorresponType("TypeB");
        list.add(type);
        type = new CorresponType();
        type.setProjectCorresponTypeId(3L);
        type.setCorresponType("TypeC");
        list.add(type);

        return list;
    }

    private List<ProjectUser> createUserList() {
        List<ProjectUser> list = new ArrayList<ProjectUser>();
        ProjectUser pUser = new ProjectUser();
        User user = new User();
        user.setEmpNo("00001");
        user.setNameE("A User");
        pUser.setUser(user);
        list.add(pUser);
        pUser = new ProjectUser();
        user = new User();
        user.setEmpNo("00002");
        user.setNameE("B User");
        pUser.setUser(user);
        list.add(pUser);
        pUser = new ProjectUser();
        user = new User();
        user.setEmpNo("00003");
        user.setNameE("C User");
        pUser.setUser(user);
        list.add(pUser);

        return list;
    }

    private List<CorresponGroup> createGroupList() {
        List<CorresponGroup> list = new ArrayList<CorresponGroup>();
        CorresponGroup group = new CorresponGroup();
        group.setId(1L);
        group.setName("A Group");
        list.add(group);
        group = new CorresponGroup();
        group.setId(2L);
        group.setName("B Group");
        list.add(group);
        group = new CorresponGroup();
        group.setId(3L);
        group.setName("C Group");
        list.add(group);

        return list;
    }

    private List<CustomField> createCustomFieldList() {
        List<CustomField> list = new ArrayList<CustomField>();
        CustomField field = new CustomField();
        field.setId(1L);
        field.setProjectCustomFieldId(11L);
        field.setLabel("A Field");
        list.add(field);
        field = new CustomField();
        field.setId(2L);
        field.setProjectCustomFieldId(22L);
        field.setLabel("B Field");
        list.add(field);
        field = new CustomField();
        field.setId(3L);
        field.setProjectCustomFieldId(33L);
        field.setLabel("C Field");
        list.add(field);

        return list;
    }



    public static class MockCorresponSearchService extends MockUp<CorresponSearchServiceImpl> {
        static final int ERROR_PAGE_NO = 5;
        static final ServiceAbortException ERROR_NO_PAGE_FOUND
            = new ServiceAbortException(ApplicationMessageCode.NO_PAGE_FOUND);
        static ServiceAbortException ERROR_SEARCH;
        static SearchCorresponResult RET_SEARCH;
        static byte[] RET_CSV;
        static byte[] RET_EXCEL;
        static byte[] RET_HTML;
        static byte[] RET_ZIP;
        static List<Correspon> SET_READ_STATUS;
        static List<Correspon> SET_STATUS;
        static List<Correspon> SET_DELETE;
        static List<Correspon> SET_ZIP;

        @Mock
        public SearchCorresponResult search(SearchCorresponCondition condition)
            throws ServiceAbortException {
            if (condition.getPageNo() == ERROR_PAGE_NO) {
                throw ERROR_NO_PAGE_FOUND;
            } else if (ERROR_SEARCH != null) {
                throw ERROR_SEARCH;
            }
            return RET_SEARCH;
        }

        @Mock
        public byte[] generateCsv(List<Correspon> correspons) throws ServiceAbortException {
            return RET_CSV;
        }

        @Mock
        public byte[] generateExcel(List<Correspon> correspons) throws ServiceAbortException {
            return RET_EXCEL;
        }

        @Mock
        public byte[] generateHTML(List<Correspon> correspons, CorresponIndexHeader header)
            throws ServiceAbortException {
            return RET_HTML;
        }

        @Mock
        public byte[] generateZip(List<Correspon> correspons,
                boolean usePersonInCharge) throws ServiceAbortException {
            SET_ZIP = correspons;
            return RET_ZIP;
        }

        @Mock
        public void updateCorresponsReadStatus(List<Correspon> correspons)
                throws ServiceAbortException {
            SET_READ_STATUS = correspons;
        }

        @Mock
        public void updateCorresponsStatus(List<Correspon> correspons)
                throws ServiceAbortException {
            SET_STATUS = correspons;
        }

        @Mock
        public void deleteCorrespons(List<Correspon> correspons) throws ServiceAbortException {
            SET_DELETE = correspons;
        }
    }

    public static class MockCorresponTypeService extends MockUp<CorresponTypeServiceImpl> {
        static List<CorresponType> RET_SEARCH;

        @Mock
        public List<CorresponType> search(SearchCorresponTypeCondition condition)
            throws ServiceAbortException {
            return RET_SEARCH;
        }
    }

    public static class MockUserService extends MockUp<UserServiceImpl> {
        static List<ProjectUser> RET_SEARCH;

        @Mock
        public List<ProjectUser> search(SearchUserCondition condition)
            throws ServiceAbortException {
            return RET_SEARCH;
        }
    }

    public static class MockCorresponGroupService extends MockUp<CorresponGroupServiceImpl> {
        static List<CorresponGroup> RET_SEARCH;

        @Mock
        public List<CorresponGroup> search(SearchCorresponGroupCondition condition)
            throws ServiceAbortException {
            return RET_SEARCH;
        }
    }

    public static class MockCustomFieldService extends MockUp<CustomFieldServiceImpl> {
        static List<CustomField> RET_SEARCH;

        @Mock
        public List<CustomField> search(SearchCustomFieldCondition condition)
            throws ServiceAbortException {
            return RET_SEARCH;
        }
    }

    public static class MockAbstractPage extends MockUp<AbstractPage> {
        static String RET_PROJID;
        static SearchCorresponCondition RET_CONDITION;
        static SearchCorresponCondition SET_CONDITION;
        static String RET_FILE_NAME;
        static User RET_USER;

        @Mock
        public String getCurrentProjectId() {
            return RET_PROJID;
        }

        @Mock
        public SearchCorresponCondition getCurrentSearchCorresponCondition() {
            return RET_CONDITION;
        }
        @Mock
        public void setCurrentSearchCorresponCondition(SearchCorresponCondition condition) {
            SET_CONDITION = condition;
        }

        @Mock
        public String createFileName() {
            return RET_FILE_NAME;
        }

        @Mock
        public User getCurrentUser() {
            return RET_USER;
        }
    }

    public static class MockViewHelper extends MockUp<ViewHelper> {
        static String RET_ACTION;
        static byte[] RET_DATA;
        static String RET_FILENAME;
        static IOException EX_DOWNLOAD;
        static String RET_COOKIE_VALUE;
        static List<SelectItem> RET_CREATE_SELECT_LIST;

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
        public void requestResponse(byte[] content, String charset) {
            RET_ACTION = "requestResponse";
            RET_DATA = content;
        }

        @Mock
        public String getCookieValue(String key) {
            try {
                if (Integer.parseInt(key) % 2 == 0) {
                    return "true";
                }
                return "false";
            } catch (NumberFormatException e){
                return RET_COOKIE_VALUE;
            }
        }

        @Mock
        public List<SelectItem> createSelectItem(List<?> dtoList, String valuePropertyName,
            String labelPropertyName) {
            System.out.println(RET_CREATE_SELECT_LIST);
            return RET_CREATE_SELECT_LIST;
        }

        @Mock
        public List<SelectItem> createSelectItem(List<Code> codeList) {
            System.out.println(RET_CREATE_SELECT_LIST);
            return RET_CREATE_SELECT_LIST;
        }

        @Mock
        public List<SelectItem> createSelectItem(Code[] codes) {
            System.out.println(RET_CREATE_SELECT_LIST);
            return RET_CREATE_SELECT_LIST;
        }
    }

    public static class MockUserPermissionHelper extends MockUp<UserPermissionHelper> {
        static boolean RET_PROJECT_ADMIN;

        @Mock
        public boolean isProjectAdmin(User user, String projectId) {
            return RET_PROJECT_ADMIN;
        }
    }

    public static class MockFavoriteFilterService extends MockUp<FavoriteFilterServiceImpl> {
        static FavoriteFilter RET_FAVORITE_FILTER;
        static boolean IS_EXCEPTION = false;
        static boolean IS_ROLLBACK = false;

        @Mock
        public FavoriteFilter find(Long id) throws ServiceAbortException {
            if (IS_EXCEPTION) {
                throw new ServiceAbortException(ApplicationMessageCode.NO_DATA_FOUND);
            }
            return RET_FAVORITE_FILTER;
        }

        @Mock
        public void save(FavoriteFilter favoriteFilter) throws ServiceAbortException {
            if (IS_ROLLBACK) {
                throw new ServiceAbortException(MessageCode.E_KEY_DUPLICATE);
            }
        }
    }

    public static class MockSearchAction extends MockUp<SearchAction> {
        @Mock
        public void execute() throws ServiceAbortException {
        }
    }
}
