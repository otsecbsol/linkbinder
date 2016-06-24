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
package jp.co.opentone.bsol.linkbinder.view.admin;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.faces.application.FacesMessage;
import javax.faces.model.SelectItem;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import jp.co.opentone.bsol.framework.core.config.SystemConfig;
import jp.co.opentone.bsol.framework.core.exception.InvalidOperationRuntimeException;
import jp.co.opentone.bsol.framework.core.message.Messages;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.extension.jsf.FacesContextMock;
import jp.co.opentone.bsol.framework.test.AbstractTestCase;
import jp.co.opentone.bsol.framework.web.view.action.ServiceActionHandler;
import jp.co.opentone.bsol.framework.web.view.util.ViewHelper;
import jp.co.opentone.bsol.linkbinder.Constants;
import jp.co.opentone.bsol.linkbinder.dto.Company;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroup;
import jp.co.opentone.bsol.linkbinder.dto.ProjectUser;
import jp.co.opentone.bsol.linkbinder.dto.SearchUserResult;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.UserIndex;
import jp.co.opentone.bsol.linkbinder.dto.condition.AbstractCondition;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponGroupCondition;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchUserCondition;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.service.admin.impl.CompanyServiceImpl;
import jp.co.opentone.bsol.linkbinder.service.admin.impl.CorresponGroupServiceImpl;
import jp.co.opentone.bsol.linkbinder.service.admin.impl.UserServiceImpl;
import jp.co.opentone.bsol.linkbinder.view.AbstractPage;
import mockit.Mock;
import mockit.MockUp;

/**
 * ユーザー一覧画面のテストクラス.
 * @author t-oda
 *
 * <p>
 * $Date: 2011-06-20 19:57:39 +0900 (月, 20  6 2011) $
 * $Rev: 4186 $
 * $Author: aoyagi $
 */
public class UserIndexPageTest extends AbstractTestCase {

    /**
     * テスト対象クラス
     */
    @Resource
    private UserIndexPage page;

    /**
     * 1ページあたりの表示件数を取得するためのKEY.
     */
    private static final String KEY_PAGE_ROW = "userindex.pagerow";

    /**
     * ページリンクの件数を取得するためのKEY.
     */
    private static final String KEY_PAGE_INDEX = "userindex.pageindex";

    /**
     * 検索結果を1画面に表示する件数のデフォルト値.
     */
    private static final int DEFAULT_PAGE_ROW_NUMBER = 10;
    /**
     * 検索結果をページ遷移するためのリンクを表示する数のデフォルト値.
     */
    private static final int DEFAULT_PAGE_INDEX_NUMBER = 10;

    /**
     * SystemAdminの値（表示用）.
     */
    private static final String VIEW_SYSTEM_ADMIN = "システム管理者";

    /**
     * ProjectAdminの値（表示用）.
     */
    private static final String VIEW_PROJECT_ADMIN = "プロジェクト管理者";

    /**
     * GroupAdminの値（表示用）.
     */
    private static final String VIEW_GROUP_ADMIN = "グループ管理者";

    /**
     * NormalUserの値（表示用）.
     */
    private static final String VIEW_NORMAL_USER = "一般ユーザー";

    /**
     * テストの前準備.
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        FacesContextMock.initialize();
        new MockAbstractPage();
        new MockSystemConfig();
        new MockUserService();
        new MockCompanyService();
        new MockCorresponGroupService();
        new MockViewHelper();

        MockSystemConfig.VALUES = new HashMap<String, String>();
        MockSystemConfig.VALUES.put(Constants.KEY_SECURITY_FLG_SYSTEM_ADMIN, "X");
        MockSystemConfig.VALUES.put(Constants.KEY_SECURITY_FLG_PROJECT_ADMIN, "X");
        MockSystemConfig.VALUES.put(Constants.KEY_SECURITY_LEVEL_GROUP_ADMIN, "30");
        MockSystemConfig.VALUES.put(Constants.KEY_SECURITY_LEVEL_NORMAL_USER, "40");
    }

    /**
     * 後始末.
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        new MockAbstractPage().tearDown();
        new MockSystemConfig().tearDown();
        new MockUserService().tearDown();
        new MockCompanyService().tearDown();
        new MockCorresponGroupService().tearDown();
        new MockViewHelper().tearDown();
        FacesContextMock.tearDown();
    }

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        MockSystemConfig.VALUES = new HashMap<String, String>();
        MockSystemConfig.VALUES.put(Constants.KEY_SECURITY_FLG_SYSTEM_ADMIN, "X");
        MockSystemConfig.VALUES.put(Constants.KEY_SECURITY_FLG_PROJECT_ADMIN, "X");
        MockSystemConfig.VALUES.put(Constants.KEY_SECURITY_LEVEL_GROUP_ADMIN, "30");
        MockSystemConfig.VALUES.put(Constants.KEY_SECURITY_LEVEL_NORMAL_USER, "40");
        MockSystemConfig.VALUES.put(ServiceActionHandler.KEY_INVALID_OPERATION_ERRORS, "E016,E904");

        page.setPageRowNum(UserIndexPage.DEFAULT_PAGE_ROW_NUMBER);
        page.setPageIndex(UserIndexPage.DEFAULT_PAGE_INDEX_NUMBER);
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
        MockAbstractPage.IS_SYSTEM_ADMIN = false;
        MockAbstractPage.IS_PROJECT_ADMIN = false;
        MockAbstractPage.IS_ANY_GROUP_ADMIN = false;
        MockAbstractPage.RET_PROJID = null;
        MockAbstractPage.SET_NEXT_SEARCH_CONDITION = null;
        MockAbstractPage.RET_PREVIOUS_SEARCH_CONDITION = null;
        MockAbstractPage.RET_FILE_NAME = null;
        MockViewHelper.RET_DATA = null;
        MockViewHelper.RET_FILENAME = null;
        MockCompanyService.RET_SEARCH_RELATED_TO_PROJECT = null;
        MockCorresponGroupService.RET_SEARCH = null;
        MockUserService.RET_SEARCH_PAGING_LIST = null;
        MockUserService.RET_GENERATE_EXCEL = null;
        MockSystemConfig.VALUES = null;
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.view.admin.UserIndexPage#initialize()} のためのテスト・メソッド.
     * ページ行・数の設定なし.SystemAdmin.
     */
    @Test
    public void testInitialize() {
        String projectId = "PJ1";
        MockAbstractPage.RET_PROJID = projectId;

        MockSystemConfig.VALUES.put(KEY_PAGE_ROW, null);
        MockSystemConfig.VALUES.put(KEY_PAGE_INDEX, null);

        String empNo = "00001";
        String nameE = "User Name";
        Long companyId = 1L;
        Long corresponGroupId = 2L;
        String securityLevel = VIEW_SYSTEM_ADMIN;

        page.setUserId(empNo);
        page.setName(nameE);
        page.setCompanyId(companyId);
        page.setCorresponGroupId(corresponGroupId);
        page.setSecurityLevel(securityLevel);

        List<Company> expCompanyList = createCompanyList();
        MockCompanyService.RET_SEARCH_RELATED_TO_PROJECT = expCompanyList;

        List<CorresponGroup> expGroupList = createCorresponGroupList();
        MockCorresponGroupService.RET_SEARCH = expGroupList;

        List<UserIndex> expIndexList = createUserIndexList();
        SearchUserResult result = new SearchUserResult();
        result.setCount(expIndexList.size());
        result.setUserIndexes(expIndexList);
        MockUserService.RET_SEARCH_PAGING_LIST = result;

        page.initialize();

        assertEquals(DEFAULT_PAGE_ROW_NUMBER, page.getPageRowNum());
        assertEquals(DEFAULT_PAGE_INDEX_NUMBER, page.getPageIndex());

        assertEquals(1, page.getPageNo());

        assertEquals(1, page.getCondition().getPageNo());
        assertEquals(DEFAULT_PAGE_ROW_NUMBER, page.getCondition().getPageRowNum());
        assertEquals(projectId, page.getCondition().getProjectId());

        assertEquals(empNo, page.getCondition().getEmpNo());
        assertEquals(nameE, page.getCondition().getNameE());
        assertEquals(companyId, page.getCondition().getCompanyId());
        assertEquals(corresponGroupId, page.getCondition().getCorresponGroupId());
        assertNull(page.getCondition().getProjectAdmin());
        assertNull(page.getCondition().getGroupAdmin());
        assertNull(page.getCondition().getSecurityLevel());

        assertEquals(expCompanyList.size(), page.getCompanySelectItems().size());
        for (int i = 0; i < expCompanyList.size(); i++) {
            Company expected = expCompanyList.get(i);
            SelectItem actual = page.getCompanySelectItems().get(i);
            assertEquals(expected.getProjectCompanyId(), actual.getValue());
            assertEquals(expected.getCodeAndName(), actual.getLabel());
        }

        assertEquals(expGroupList.size(), page.getGroupSelectItems().size());
        for (int i = 0; i < expGroupList.size(); i++) {
            CorresponGroup expected = expGroupList.get(i);
            SelectItem actual = page.getGroupSelectItems().get(i);
            assertEquals(expected.getId(), actual.getValue());
            assertEquals(expected.getName(), actual.getLabel());
        }

        assertEquals(3, page.getSecurityLevelSelectItems().size());
        assertEquals(VIEW_PROJECT_ADMIN,
                     page.getSecurityLevelSelectItems().get(0).getValue());
        assertEquals(VIEW_GROUP_ADMIN,
                     page.getSecurityLevelSelectItems().get(1).getValue());
        assertEquals(VIEW_NORMAL_USER,
                     page.getSecurityLevelSelectItems().get(2).getValue());
        assertEquals(VIEW_PROJECT_ADMIN,
                     page.getSecurityLevelSelectItems().get(0).getLabel());
        assertEquals(VIEW_GROUP_ADMIN,
                     page.getSecurityLevelSelectItems().get(1).getLabel());
        assertEquals(VIEW_NORMAL_USER,
                     page.getSecurityLevelSelectItems().get(2).getLabel());

        assertEquals(result.getCount(), page.getDataCount());
        assertEquals(result.getUserIndexes(), page.getUserIndexList());
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.view.admin.UserIndexPage#initialize()} のためのテスト・メソッド.
     * ページ行・数の設定あり.SystemAdmin.
     */
    @Test
    public void testInitializeNoPageSetting() {
        int nPageRow = 15;
        int nPageIndex = 5;
        MockSystemConfig.VALUES.put(KEY_PAGE_ROW, String.valueOf(nPageRow));
        MockSystemConfig.VALUES.put(KEY_PAGE_INDEX, String.valueOf(nPageIndex));

        String projectId = "PJ1";
        MockAbstractPage.RET_PROJID = projectId;

        String empNo = "00001";
        String nameE = "User Name";
        Long companyId = 1L;
        Long corresponGroupId = 2L;
        String securityLevel = VIEW_SYSTEM_ADMIN;

        page.setUserId(empNo);
        page.setName(nameE);
        page.setCompanyId(companyId);
        page.setCorresponGroupId(corresponGroupId);
        page.setSecurityLevel(securityLevel);

        List<Company> expCompanyList = createCompanyList();
        MockCompanyService.RET_SEARCH_RELATED_TO_PROJECT = expCompanyList;

        List<CorresponGroup> expGroupList = createCorresponGroupList();
        MockCorresponGroupService.RET_SEARCH = expGroupList;

        List<UserIndex> expIndexList = createUserIndexList();
        SearchUserResult result = new SearchUserResult();
        result.setCount(expIndexList.size());
        result.setUserIndexes(expIndexList);
        MockUserService.RET_SEARCH_PAGING_LIST = result;

        page.initialize();

        assertEquals(nPageRow, page.getPageRowNum());
        assertEquals(nPageIndex, page.getPageIndex());

        assertEquals(1, page.getPageNo());

        assertEquals(1, page.getCondition().getPageNo());
        assertEquals(nPageRow, page.getCondition().getPageRowNum());
        assertEquals(projectId, page.getCondition().getProjectId());

        assertEquals(empNo, page.getCondition().getEmpNo());
        assertEquals(nameE, page.getCondition().getNameE());
        assertEquals(companyId, page.getCondition().getCompanyId());
        assertEquals(corresponGroupId, page.getCondition().getCorresponGroupId());
        assertNull(page.getCondition().getGroupAdmin());
        assertNull(page.getCondition().getSecurityLevel());

        assertEquals(expCompanyList.size(), page.getCompanySelectItems().size());
        for (int i = 0; i < expCompanyList.size(); i++) {
            Company expected = expCompanyList.get(i);
            SelectItem actual = page.getCompanySelectItems().get(i);
            assertEquals(expected.getProjectCompanyId(), actual.getValue());
            assertEquals(expected.getCodeAndName(), actual.getLabel());
        }

        assertEquals(expGroupList.size(), page.getGroupSelectItems().size());
        for (int i = 0; i < expGroupList.size(); i++) {
            CorresponGroup expected = expGroupList.get(i);
            SelectItem actual = page.getGroupSelectItems().get(i);
            assertEquals(expected.getId(), actual.getValue());
            assertEquals(expected.getName(), actual.getLabel());
        }

        assertEquals(3, page.getSecurityLevelSelectItems().size());
        assertEquals(VIEW_PROJECT_ADMIN,
                     page.getSecurityLevelSelectItems().get(0).getValue());
        assertEquals(VIEW_GROUP_ADMIN,
                     page.getSecurityLevelSelectItems().get(1).getValue());
        assertEquals(VIEW_NORMAL_USER,
                     page.getSecurityLevelSelectItems().get(2).getValue());
        assertEquals(VIEW_PROJECT_ADMIN,
                     page.getSecurityLevelSelectItems().get(0).getLabel());
        assertEquals(VIEW_GROUP_ADMIN,
                     page.getSecurityLevelSelectItems().get(1).getLabel());
        assertEquals(VIEW_NORMAL_USER,
                     page.getSecurityLevelSelectItems().get(2).getLabel());

        assertEquals(result.getCount(), page.getDataCount());
        assertEquals(result.getUserIndexes(), page.getUserIndexList());
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.view.admin.UserIndexPage#initialize()} のためのテスト・メソッド.
     * ProjectAdmin.
     */
    @Test
    public void testInitializeProjectAdmin() {
        int nPageRow = 15;
        int nPageIndex = 5;
        MockSystemConfig.VALUES.put(KEY_PAGE_ROW, String.valueOf(nPageRow));
        MockSystemConfig.VALUES.put(KEY_PAGE_INDEX, String.valueOf(nPageIndex));

        String projectId = "PJ1";
        MockAbstractPage.RET_PROJID = projectId;

        String empNo = "00001";
        String nameE = "User Name";
        Long companyId = 1L;
        Long corresponGroupId = 2L;
        String securityLevel = VIEW_PROJECT_ADMIN;

        page.setUserId(empNo);
        page.setName(nameE);
        page.setCompanyId(companyId);
        page.setCorresponGroupId(corresponGroupId);
        page.setSecurityLevel(securityLevel);

        List<Company> expCompanyList = createCompanyList();
        MockCompanyService.RET_SEARCH_RELATED_TO_PROJECT = expCompanyList;

        List<CorresponGroup> expGroupList = createCorresponGroupList();
        MockCorresponGroupService.RET_SEARCH = expGroupList;

        List<UserIndex> expIndexList = createUserIndexList();
        SearchUserResult result = new SearchUserResult();
        result.setCount(expIndexList.size());
        result.setUserIndexes(expIndexList);
        MockUserService.RET_SEARCH_PAGING_LIST = result;

        page.initialize();

        assertEquals(nPageRow, page.getPageRowNum());
        assertEquals(nPageIndex, page.getPageIndex());

        assertEquals(1, page.getPageNo());

        assertEquals(1, page.getCondition().getPageNo());
        assertEquals(nPageRow, page.getCondition().getPageRowNum());
        assertEquals(projectId, page.getCondition().getProjectId());

        assertEquals(empNo, page.getCondition().getEmpNo());
        assertEquals(nameE, page.getCondition().getNameE());
        assertEquals(companyId, page.getCondition().getCompanyId());
        assertEquals(corresponGroupId, page.getCondition().getCorresponGroupId());
        assertNull(page.getCondition().getSysAdminFlg());
        assertEquals("X", page.getCondition().getProjectAdmin());
        assertNull(page.getCondition().getGroupAdmin());
        assertNull(page.getCondition().getSecurityLevel());

        assertEquals(expCompanyList.size(), page.getCompanySelectItems().size());
        for (int i = 0; i < expCompanyList.size(); i++) {
            Company expected = expCompanyList.get(i);
            SelectItem actual = page.getCompanySelectItems().get(i);
            assertEquals(expected.getProjectCompanyId(), actual.getValue());
            assertEquals(expected.getCodeAndName(), actual.getLabel());
        }

        assertEquals(expGroupList.size(), page.getGroupSelectItems().size());
        for (int i = 0; i < expGroupList.size(); i++) {
            CorresponGroup expected = expGroupList.get(i);
            SelectItem actual = page.getGroupSelectItems().get(i);
            assertEquals(expected.getId(), actual.getValue());
            assertEquals(expected.getName(), actual.getLabel());
        }

        assertEquals(3, page.getSecurityLevelSelectItems().size());
        assertEquals(VIEW_PROJECT_ADMIN,
                     page.getSecurityLevelSelectItems().get(0).getValue());
        assertEquals(VIEW_GROUP_ADMIN,
                     page.getSecurityLevelSelectItems().get(1).getValue());
        assertEquals(VIEW_NORMAL_USER,
                     page.getSecurityLevelSelectItems().get(2).getValue());
        assertEquals(VIEW_PROJECT_ADMIN,
                     page.getSecurityLevelSelectItems().get(0).getLabel());
        assertEquals(VIEW_GROUP_ADMIN,
                     page.getSecurityLevelSelectItems().get(1).getLabel());
        assertEquals(VIEW_NORMAL_USER,
                     page.getSecurityLevelSelectItems().get(2).getLabel());

        assertEquals(result.getCount(), page.getDataCount());
        assertEquals(result.getUserIndexes(), page.getUserIndexList());
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.view.admin.UserIndexPage#initialize()} のためのテスト・メソッド.
     * GroupAdmin.
     */
    @Test
    public void testInitializeGroupAdmin() {
        int nPageRow = 15;
        int nPageIndex = 5;
        MockSystemConfig.VALUES.put(KEY_PAGE_ROW, String.valueOf(nPageRow));
        MockSystemConfig.VALUES.put(KEY_PAGE_INDEX, String.valueOf(nPageIndex));

        String projectId = "PJ1";
        MockAbstractPage.RET_PROJID = projectId;

        String empNo = "00001";
        String nameE = "User Name";
        Long companyId = 1L;
        Long corresponGroupId = 2L;
        String securityLevel = VIEW_GROUP_ADMIN;

        page.setUserId(empNo);
        page.setName(nameE);
        page.setCompanyId(companyId);
        page.setCorresponGroupId(corresponGroupId);
        page.setSecurityLevel(securityLevel);

        List<Company> expCompanyList = createCompanyList();
        MockCompanyService.RET_SEARCH_RELATED_TO_PROJECT = expCompanyList;

        List<CorresponGroup> expGroupList = createCorresponGroupList();
        MockCorresponGroupService.RET_SEARCH = expGroupList;

        List<UserIndex> expIndexList = createUserIndexList();
        SearchUserResult result = new SearchUserResult();
        result.setCount(expIndexList.size());
        result.setUserIndexes(expIndexList);
        MockUserService.RET_SEARCH_PAGING_LIST = result;

        page.initialize();

        assertEquals(nPageRow, page.getPageRowNum());
        assertEquals(nPageIndex, page.getPageIndex());

        assertEquals(1, page.getPageNo());

        assertEquals(1, page.getCondition().getPageNo());
        assertEquals(nPageRow, page.getCondition().getPageRowNum());
        assertEquals(projectId, page.getCondition().getProjectId());

        assertEquals(empNo, page.getCondition().getEmpNo());
        assertEquals(nameE, page.getCondition().getNameE());
        assertEquals(companyId, page.getCondition().getCompanyId());
        assertEquals(corresponGroupId, page.getCondition().getCorresponGroupId());
        assertNull(page.getCondition().getSysAdminFlg());
        assertNull(page.getCondition().getProjectAdmin());
        assertEquals("30", page.getCondition().getGroupAdmin());
        assertNull(page.getCondition().getSecurityLevel());

        assertEquals(expCompanyList.size(), page.getCompanySelectItems().size());
        for (int i = 0; i < expCompanyList.size(); i++) {
            Company expected = expCompanyList.get(i);
            SelectItem actual = page.getCompanySelectItems().get(i);
            assertEquals(expected.getProjectCompanyId(), actual.getValue());
            assertEquals(expected.getCodeAndName(), actual.getLabel());
        }

        assertEquals(expGroupList.size(), page.getGroupSelectItems().size());
        for (int i = 0; i < expGroupList.size(); i++) {
            CorresponGroup expected = expGroupList.get(i);
            SelectItem actual = page.getGroupSelectItems().get(i);
            assertEquals(expected.getId(), actual.getValue());
            assertEquals(expected.getName(), actual.getLabel());
        }

        assertEquals(3, page.getSecurityLevelSelectItems().size());
        assertEquals(VIEW_PROJECT_ADMIN,
                     page.getSecurityLevelSelectItems().get(0).getValue());
        assertEquals(VIEW_GROUP_ADMIN,
                     page.getSecurityLevelSelectItems().get(1).getValue());
        assertEquals(VIEW_NORMAL_USER,
                     page.getSecurityLevelSelectItems().get(2).getValue());
        assertEquals(VIEW_PROJECT_ADMIN,
                     page.getSecurityLevelSelectItems().get(0).getLabel());
        assertEquals(VIEW_GROUP_ADMIN,
                     page.getSecurityLevelSelectItems().get(1).getLabel());
        assertEquals(VIEW_NORMAL_USER,
                     page.getSecurityLevelSelectItems().get(2).getLabel());

        assertEquals(result.getCount(), page.getDataCount());
        assertEquals(result.getUserIndexes(), page.getUserIndexList());
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.view.admin.UserIndexPage#initialize()} のためのテスト・メソッド.
     * NormalUser.
     */
    @Test
    public void testInitializeNormalUser() {
        int nPageRow = 15;
        int nPageIndex = 5;
        MockSystemConfig.VALUES.put(KEY_PAGE_ROW, String.valueOf(nPageRow));
        MockSystemConfig.VALUES.put(KEY_PAGE_INDEX, String.valueOf(nPageIndex));

        String projectId = "PJ1";
        MockAbstractPage.RET_PROJID = projectId;

        String empNo = "00001";
        String nameE = "User Name";
        Long companyId = 1L;
        Long corresponGroupId = 2L;
        String securityLevel = VIEW_NORMAL_USER;

        page.setUserId(empNo);
        page.setName(nameE);
        page.setCompanyId(companyId);
        page.setCorresponGroupId(corresponGroupId);
        page.setSecurityLevel(securityLevel);

        List<Company> expCompanyList = createCompanyList();
        MockCompanyService.RET_SEARCH_RELATED_TO_PROJECT = expCompanyList;

        List<CorresponGroup> expGroupList = createCorresponGroupList();
        MockCorresponGroupService.RET_SEARCH = expGroupList;

        List<UserIndex> expIndexList = createUserIndexList();
        SearchUserResult result = new SearchUserResult();
        result.setCount(expIndexList.size());
        result.setUserIndexes(expIndexList);
        MockUserService.RET_SEARCH_PAGING_LIST = result;

        page.initialize();

        assertEquals(nPageRow, page.getPageRowNum());
        assertEquals(nPageIndex, page.getPageIndex());

        assertEquals(1, page.getPageNo());

        assertEquals(1, page.getCondition().getPageNo());
        assertEquals(nPageRow, page.getCondition().getPageRowNum());
        assertEquals(projectId, page.getCondition().getProjectId());

        assertEquals(empNo, page.getCondition().getEmpNo());
        assertEquals(nameE, page.getCondition().getNameE());
        assertEquals(companyId, page.getCondition().getCompanyId());
        assertEquals(corresponGroupId, page.getCondition().getCorresponGroupId());
        assertEquals("X", page.getCondition().getSysAdminFlg());
        assertEquals("X", page.getCondition().getProjectAdmin());
        assertEquals("30", page.getCondition().getGroupAdmin());
        assertEquals("40", page.getCondition().getSecurityLevel());

        assertEquals(expCompanyList.size(), page.getCompanySelectItems().size());
        for (int i = 0; i < expCompanyList.size(); i++) {
            Company expected = expCompanyList.get(i);
            SelectItem actual = page.getCompanySelectItems().get(i);
            assertEquals(expected.getProjectCompanyId(), actual.getValue());
            assertEquals(expected.getCodeAndName(), actual.getLabel());
        }

        assertEquals(expGroupList.size(), page.getGroupSelectItems().size());
        for (int i = 0; i < expGroupList.size(); i++) {
            CorresponGroup expected = expGroupList.get(i);
            SelectItem actual = page.getGroupSelectItems().get(i);
            assertEquals(expected.getId(), actual.getValue());
            assertEquals(expected.getName(), actual.getLabel());
        }

        assertEquals(3, page.getSecurityLevelSelectItems().size());
        assertEquals(VIEW_PROJECT_ADMIN,
                     page.getSecurityLevelSelectItems().get(0).getValue());
        assertEquals(VIEW_GROUP_ADMIN,
                     page.getSecurityLevelSelectItems().get(1).getValue());
        assertEquals(VIEW_NORMAL_USER,
                     page.getSecurityLevelSelectItems().get(2).getValue());
        assertEquals(VIEW_PROJECT_ADMIN,
                     page.getSecurityLevelSelectItems().get(0).getLabel());
        assertEquals(VIEW_GROUP_ADMIN,
                     page.getSecurityLevelSelectItems().get(1).getLabel());
        assertEquals(VIEW_NORMAL_USER,
                     page.getSecurityLevelSelectItems().get(2).getLabel());

        assertEquals(result.getCount(), page.getDataCount());
        assertEquals(result.getUserIndexes(), page.getUserIndexList());
    }


    /**
     * {@link jp.co.opentone.bsol.linkbinder.view.admin.UserIndexPage#initialize()} のためのテスト・メソッド.
     * Flashに検索条件あり.ページ行・数の設定あり.SystemAdmin.
     */
    @Test
    public void testInitializeFlash() {
        int nPageRow = 15;
        int nPageIndex = 5;
        MockSystemConfig.VALUES.put(KEY_PAGE_ROW, String.valueOf(nPageRow));
        MockSystemConfig.VALUES.put(KEY_PAGE_INDEX, String.valueOf(nPageIndex));

        String projectId = "PJ1";
        MockAbstractPage.RET_PROJID = projectId;

        String empNo = "00001";
        String nameE = "User Name";
        Long companyId = 1L;
        Long corresponGroupId = 2L;
        String securityLevel = VIEW_SYSTEM_ADMIN;

        SearchUserCondition condition = new SearchUserCondition();
        condition.setEmpNo(empNo);
        condition.setNameE(nameE);
        condition.setCompanyId(companyId);
        condition.setCorresponGroupId(corresponGroupId);
        condition.setSysAdminFlg("X");
        MockAbstractPage.RET_PREVIOUS_SEARCH_CONDITION = condition;

        List<Company> expCompanyList = createCompanyList();
        MockCompanyService.RET_SEARCH_RELATED_TO_PROJECT = expCompanyList;

        List<CorresponGroup> expGroupList = createCorresponGroupList();
        MockCorresponGroupService.RET_SEARCH = expGroupList;

        List<UserIndex> expIndexList = createUserIndexList();
        SearchUserResult result = new SearchUserResult();
        result.setCount(expIndexList.size());
        result.setUserIndexes(expIndexList);
        MockUserService.RET_SEARCH_PAGING_LIST = result;

        page.initialize();

        assertEquals(nPageRow, page.getPageRowNum());
        assertEquals(nPageIndex, page.getPageIndex());

        assertEquals(1, page.getPageNo());

        assertEquals(1, page.getCondition().getPageNo());
        assertEquals(nPageRow, page.getCondition().getPageRowNum());
        assertEquals(projectId, page.getCondition().getProjectId());

        assertEquals(empNo, page.getCondition().getEmpNo());
        assertEquals(nameE, page.getCondition().getNameE());
        assertEquals(companyId, page.getCondition().getCompanyId());
        assertEquals(corresponGroupId, page.getCondition().getCorresponGroupId());
        assertEquals("X", page.getCondition().getSysAdminFlg());
        assertNull(page.getCondition().getProjectAdmin());
        assertNull(page.getCondition().getGroupAdmin());

        assertEquals(empNo, page.getUserId());
        assertEquals(nameE, page.getName());
        assertEquals(companyId, page.getCompanyId());
        assertEquals(corresponGroupId, page.getCorresponGroupId());

        assertEquals(expCompanyList.size(), page.getCompanySelectItems().size());
        for (int i = 0; i < expCompanyList.size(); i++) {
            Company expected = expCompanyList.get(i);
            SelectItem actual = page.getCompanySelectItems().get(i);
            assertEquals(expected.getProjectCompanyId(), actual.getValue());
            assertEquals(expected.getCodeAndName(), actual.getLabel());
        }

        assertEquals(expGroupList.size(), page.getGroupSelectItems().size());
        for (int i = 0; i < expGroupList.size(); i++) {
            CorresponGroup expected = expGroupList.get(i);
            SelectItem actual = page.getGroupSelectItems().get(i);
            assertEquals(expected.getId(), actual.getValue());
            assertEquals(expected.getName(), actual.getLabel());
        }

        assertEquals(3, page.getSecurityLevelSelectItems().size());
        assertEquals(VIEW_PROJECT_ADMIN,
                     page.getSecurityLevelSelectItems().get(0).getValue());
        assertEquals(VIEW_GROUP_ADMIN,
                     page.getSecurityLevelSelectItems().get(1).getValue());
        assertEquals(VIEW_NORMAL_USER,
                     page.getSecurityLevelSelectItems().get(2).getValue());
        assertEquals(VIEW_PROJECT_ADMIN,
                     page.getSecurityLevelSelectItems().get(0).getLabel());
        assertEquals(VIEW_GROUP_ADMIN,
                     page.getSecurityLevelSelectItems().get(1).getLabel());
        assertEquals(VIEW_NORMAL_USER,
                     page.getSecurityLevelSelectItems().get(2).getLabel());

        assertEquals(result.getCount(), page.getDataCount());
        assertEquals(result.getUserIndexes(), page.getUserIndexList());
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.view.admin.UserIndexPage#initialize()} のためのテスト・メソッド.
     * Flashに検索条件あり.ProjectAdmin.
     */
    @Test
    public void testInitializeFlashProjectAdmin() {
        int nPageRow = 15;
        int nPageIndex = 5;
        MockSystemConfig.VALUES.put(KEY_PAGE_ROW, String.valueOf(nPageRow));
        MockSystemConfig.VALUES.put(KEY_PAGE_INDEX, String.valueOf(nPageIndex));

        String projectId = "PJ1";
        MockAbstractPage.RET_PROJID = projectId;

        String empNo = "00001";
        String nameE = "User Name";
        Long companyId = 1L;
        Long corresponGroupId = 2L;
        String securityLevel = VIEW_PROJECT_ADMIN;

        SearchUserCondition condition = new SearchUserCondition();
        condition.setEmpNo(empNo);
        condition.setNameE(nameE);
        condition.setCompanyId(companyId);
        condition.setCorresponGroupId(corresponGroupId);
        condition.setProjectAdmin("X");
        MockAbstractPage.RET_PREVIOUS_SEARCH_CONDITION = condition;

        List<Company> expCompanyList = createCompanyList();
        MockCompanyService.RET_SEARCH_RELATED_TO_PROJECT = expCompanyList;

        List<CorresponGroup> expGroupList = createCorresponGroupList();
        MockCorresponGroupService.RET_SEARCH = expGroupList;

        List<UserIndex> expIndexList = createUserIndexList();
        SearchUserResult result = new SearchUserResult();
        result.setCount(expIndexList.size());
        result.setUserIndexes(expIndexList);
        MockUserService.RET_SEARCH_PAGING_LIST = result;

        page.initialize();

        assertEquals(nPageRow, page.getPageRowNum());
        assertEquals(nPageIndex, page.getPageIndex());

        assertEquals(1, page.getPageNo());

        assertEquals(1, page.getCondition().getPageNo());
        assertEquals(nPageRow, page.getCondition().getPageRowNum());
        assertEquals(projectId, page.getCondition().getProjectId());

        assertEquals(empNo, page.getCondition().getEmpNo());
        assertEquals(nameE, page.getCondition().getNameE());
        assertEquals(companyId, page.getCondition().getCompanyId());
        assertEquals(corresponGroupId, page.getCondition().getCorresponGroupId());
        assertNull(page.getCondition().getSysAdminFlg());
        assertEquals("X", page.getCondition().getProjectAdmin());
        assertNull(page.getCondition().getGroupAdmin());
        assertNull(page.getCondition().getSecurityLevel());

        assertEquals(empNo, page.getUserId());
        assertEquals(nameE, page.getName());
        assertEquals(companyId, page.getCompanyId());
        assertEquals(corresponGroupId, page.getCorresponGroupId());
        assertEquals(securityLevel, page.getSecurityLevel());

        assertEquals(expCompanyList.size(), page.getCompanySelectItems().size());
        for (int i = 0; i < expCompanyList.size(); i++) {
            Company expected = expCompanyList.get(i);
            SelectItem actual = page.getCompanySelectItems().get(i);
            assertEquals(expected.getProjectCompanyId(), actual.getValue());
            assertEquals(expected.getCodeAndName(), actual.getLabel());
        }

        assertEquals(expGroupList.size(), page.getGroupSelectItems().size());
        for (int i = 0; i < expGroupList.size(); i++) {
            CorresponGroup expected = expGroupList.get(i);
            SelectItem actual = page.getGroupSelectItems().get(i);
            assertEquals(expected.getId(), actual.getValue());
            assertEquals(expected.getName(), actual.getLabel());
        }

        assertEquals(3, page.getSecurityLevelSelectItems().size());
        assertEquals(VIEW_PROJECT_ADMIN,
                     page.getSecurityLevelSelectItems().get(0).getValue());
        assertEquals(VIEW_GROUP_ADMIN,
                     page.getSecurityLevelSelectItems().get(1).getValue());
        assertEquals(VIEW_NORMAL_USER,
                     page.getSecurityLevelSelectItems().get(2).getValue());
        assertEquals(VIEW_PROJECT_ADMIN,
                     page.getSecurityLevelSelectItems().get(0).getLabel());
        assertEquals(VIEW_GROUP_ADMIN,
                     page.getSecurityLevelSelectItems().get(1).getLabel());
        assertEquals(VIEW_NORMAL_USER,
                     page.getSecurityLevelSelectItems().get(2).getLabel());

        assertEquals(result.getCount(), page.getDataCount());
        assertEquals(result.getUserIndexes(), page.getUserIndexList());
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.view.admin.UserIndexPage#initialize()} のためのテスト・メソッド.
     * Flashに検索条件あり.GroupAdmin.
     */
    @Test
    public void testInitializeFlashGroupAdmin() {
        int nPageRow = 15;
        int nPageIndex = 5;
        MockSystemConfig.VALUES.put(KEY_PAGE_ROW, String.valueOf(nPageRow));
        MockSystemConfig.VALUES.put(KEY_PAGE_INDEX, String.valueOf(nPageIndex));

        String projectId = "PJ1";
        MockAbstractPage.RET_PROJID = projectId;

        String empNo = "00001";
        String nameE = "User Name";
        Long companyId = 1L;
        Long corresponGroupId = 2L;
        String value = "30";

        SearchUserCondition condition = new SearchUserCondition();
        condition.setEmpNo(empNo);
        condition.setNameE(nameE);
        condition.setCompanyId(companyId);
        condition.setCorresponGroupId(corresponGroupId);
        condition.setGroupAdmin(value);
        MockAbstractPage.RET_PREVIOUS_SEARCH_CONDITION = condition;

        List<Company> expCompanyList = createCompanyList();
        MockCompanyService.RET_SEARCH_RELATED_TO_PROJECT = expCompanyList;

        List<CorresponGroup> expGroupList = createCorresponGroupList();
        MockCorresponGroupService.RET_SEARCH = expGroupList;

        List<UserIndex> expIndexList = createUserIndexList();
        SearchUserResult result = new SearchUserResult();
        result.setCount(expIndexList.size());
        result.setUserIndexes(expIndexList);
        MockUserService.RET_SEARCH_PAGING_LIST = result;

        page.initialize();

        assertEquals(nPageRow, page.getPageRowNum());
        assertEquals(nPageIndex, page.getPageIndex());

        assertEquals(1, page.getPageNo());

        assertEquals(1, page.getCondition().getPageNo());
        assertEquals(nPageRow, page.getCondition().getPageRowNum());
        assertEquals(projectId, page.getCondition().getProjectId());

        assertEquals(empNo, page.getCondition().getEmpNo());
        assertEquals(nameE, page.getCondition().getNameE());
        assertEquals(companyId, page.getCondition().getCompanyId());
        assertEquals(corresponGroupId, page.getCondition().getCorresponGroupId());
        assertNull(page.getCondition().getSysAdminFlg());
        assertNull(page.getCondition().getProjectAdmin());
        assertEquals(value, page.getCondition().getGroupAdmin());
        assertNull(page.getCondition().getSecurityLevel());

        assertEquals(empNo, page.getUserId());
        assertEquals(nameE, page.getName());
        assertEquals(companyId, page.getCompanyId());
        assertEquals(corresponGroupId, page.getCorresponGroupId());
        assertEquals(VIEW_GROUP_ADMIN, page.getSecurityLevel());

        assertEquals(expCompanyList.size(), page.getCompanySelectItems().size());
        for (int i = 0; i < expCompanyList.size(); i++) {
            Company expected = expCompanyList.get(i);
            SelectItem actual = page.getCompanySelectItems().get(i);
            assertEquals(expected.getProjectCompanyId(), actual.getValue());
            assertEquals(expected.getCodeAndName(), actual.getLabel());
        }

        assertEquals(expGroupList.size(), page.getGroupSelectItems().size());
        for (int i = 0; i < expGroupList.size(); i++) {
            CorresponGroup expected = expGroupList.get(i);
            SelectItem actual = page.getGroupSelectItems().get(i);
            assertEquals(expected.getId(), actual.getValue());
            assertEquals(expected.getName(), actual.getLabel());
        }

        assertEquals(3, page.getSecurityLevelSelectItems().size());
        assertEquals(VIEW_PROJECT_ADMIN,
                     page.getSecurityLevelSelectItems().get(0).getValue());
        assertEquals(VIEW_GROUP_ADMIN,
                     page.getSecurityLevelSelectItems().get(1).getValue());
        assertEquals(VIEW_NORMAL_USER,
                     page.getSecurityLevelSelectItems().get(2).getValue());
        assertEquals(VIEW_PROJECT_ADMIN,
                     page.getSecurityLevelSelectItems().get(0).getLabel());
        assertEquals(VIEW_GROUP_ADMIN,
                     page.getSecurityLevelSelectItems().get(1).getLabel());
        assertEquals(VIEW_NORMAL_USER,
                     page.getSecurityLevelSelectItems().get(2).getLabel());

        assertEquals(result.getCount(), page.getDataCount());
        assertEquals(result.getUserIndexes(), page.getUserIndexList());
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.view.admin.UserIndexPage#initialize()} のためのテスト・メソッド.
     * Flashに検索条件あり.NormalUser.
     */
    @Test
    public void testInitializeFlashNormalUser() {
        int nPageRow = 15;
        int nPageIndex = 5;
        MockSystemConfig.VALUES.put(KEY_PAGE_ROW, String.valueOf(nPageRow));
        MockSystemConfig.VALUES.put(KEY_PAGE_INDEX, String.valueOf(nPageIndex));

        String projectId = "PJ1";
        MockAbstractPage.RET_PROJID = projectId;

        String empNo = "00001";
        String nameE = "User Name";
        Long companyId = 1L;
        Long corresponGroupId = 2L;
        String value = "40";

        SearchUserCondition condition = new SearchUserCondition();
        condition.setEmpNo(empNo);
        condition.setNameE(nameE);
        condition.setCompanyId(companyId);
        condition.setCorresponGroupId(corresponGroupId);
        condition.setSecurityLevel(value);
        condition.setSysAdminFlg("X");
        condition.setProjectAdmin("X");
        condition.setGroupAdmin("30");
        MockAbstractPage.RET_PREVIOUS_SEARCH_CONDITION = condition;

        List<Company> expCompanyList = createCompanyList();
        MockCompanyService.RET_SEARCH_RELATED_TO_PROJECT = expCompanyList;

        List<CorresponGroup> expGroupList = createCorresponGroupList();
        MockCorresponGroupService.RET_SEARCH = expGroupList;

        List<UserIndex> expIndexList = createUserIndexList();
        SearchUserResult result = new SearchUserResult();
        result.setCount(expIndexList.size());
        result.setUserIndexes(expIndexList);
        MockUserService.RET_SEARCH_PAGING_LIST = result;

        page.initialize();

        assertEquals(nPageRow, page.getPageRowNum());
        assertEquals(nPageIndex, page.getPageIndex());

        assertEquals(1, page.getPageNo());

        assertEquals(1, page.getCondition().getPageNo());
        assertEquals(nPageRow, page.getCondition().getPageRowNum());
        assertEquals(projectId, page.getCondition().getProjectId());

        assertEquals(empNo, page.getCondition().getEmpNo());
        assertEquals(nameE, page.getCondition().getNameE());
        assertEquals(companyId, page.getCondition().getCompanyId());
        assertEquals(corresponGroupId, page.getCondition().getCorresponGroupId());
        assertEquals("X", page.getCondition().getSysAdminFlg());
        assertEquals("X", page.getCondition().getProjectAdmin());
        assertEquals("30", page.getCondition().getGroupAdmin());
        assertEquals(value, page.getCondition().getSecurityLevel());

        assertEquals(empNo, page.getUserId());
        assertEquals(nameE, page.getName());
        assertEquals(companyId, page.getCompanyId());
        assertEquals(corresponGroupId, page.getCorresponGroupId());
        assertEquals(VIEW_NORMAL_USER, page.getSecurityLevel());

        assertEquals(expCompanyList.size(), page.getCompanySelectItems().size());
        for (int i = 0; i < expCompanyList.size(); i++) {
            Company expected = expCompanyList.get(i);
            SelectItem actual = page.getCompanySelectItems().get(i);
            assertEquals(expected.getProjectCompanyId(), actual.getValue());
            assertEquals(expected.getCodeAndName(), actual.getLabel());
        }

        assertEquals(expGroupList.size(), page.getGroupSelectItems().size());
        for (int i = 0; i < expGroupList.size(); i++) {
            CorresponGroup expected = expGroupList.get(i);
            SelectItem actual = page.getGroupSelectItems().get(i);
            assertEquals(expected.getId(), actual.getValue());
            assertEquals(expected.getName(), actual.getLabel());
        }

        assertEquals(3, page.getSecurityLevelSelectItems().size());
        assertEquals(VIEW_PROJECT_ADMIN,
                     page.getSecurityLevelSelectItems().get(0).getValue());
        assertEquals(VIEW_GROUP_ADMIN,
                     page.getSecurityLevelSelectItems().get(1).getValue());
        assertEquals(VIEW_NORMAL_USER,
                     page.getSecurityLevelSelectItems().get(2).getValue());
        assertEquals(VIEW_PROJECT_ADMIN,
                     page.getSecurityLevelSelectItems().get(0).getLabel());
        assertEquals(VIEW_GROUP_ADMIN,
                     page.getSecurityLevelSelectItems().get(1).getLabel());
        assertEquals(VIEW_NORMAL_USER,
                     page.getSecurityLevelSelectItems().get(2).getLabel());

        assertEquals(result.getCount(), page.getDataCount());
        assertEquals(result.getUserIndexes(), page.getUserIndexList());
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.view.admin.UserIndexPage#initialize()} のためのテスト・メソッド.
     * デフォルトプロジェクトIDなし.
     */
    @Test
    public void testInitializeProjectNotSelected() {
        // 期待されるメッセージをセット
        FacesContextMock.EXPECTED_MESSAGE =
            new FacesMessage(
                FacesMessage.SEVERITY_ERROR,
                FacesMessage.SEVERITY_ERROR.toString(),
                createExpectedMessageString(
                    Messages.getMessageAsString(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT),
                    "Initialize"));

        try {
            page.initialize();
            fail("例外が発生していない");
        } catch (InvalidOperationRuntimeException e) {
            ServiceAbortException actual = (ServiceAbortException)e.getCause();
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT,
                actual.getMessageCode());
        }
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.view.admin.UserIndexPage#search()} のためのテスト・メソッド.
     */
    @Test
    public void testSearch() {
        String projectId = "PJ1";
        MockAbstractPage.RET_PROJID = projectId;

        String empNo = "00001";
        String nameE = "User Name";
        Long companyId = 1L;
        Long corresponGroupId = 2L;
        String securityLevel = VIEW_SYSTEM_ADMIN;

        page.setUserId(empNo);
        page.setName(nameE);
        page.setCompanyId(companyId);
        page.setCorresponGroupId(corresponGroupId);
        page.setSecurityLevel(securityLevel);

        List<Company> expCompanyList = createCompanyList();
        MockCompanyService.RET_SEARCH_RELATED_TO_PROJECT = expCompanyList;

        List<CorresponGroup> expGroupList = createCorresponGroupList();
        MockCorresponGroupService.RET_SEARCH = expGroupList;

        List<UserIndex> expIndexList = createUserIndexList();
        SearchUserResult result = new SearchUserResult();
        result.setCount(expIndexList.size());
        result.setUserIndexes(expIndexList);
        MockUserService.RET_SEARCH_PAGING_LIST = result;

        String actual = page.search();

        assertNull(actual);

        assertEquals(1, page.getPageNo());

        assertEquals(1, page.getCondition().getPageNo());
        assertEquals(projectId, page.getCondition().getProjectId());

        assertEquals(empNo, page.getCondition().getEmpNo());
        assertEquals(nameE, page.getCondition().getNameE());
        assertEquals(companyId, page.getCondition().getCompanyId());
        assertEquals(corresponGroupId, page.getCondition().getCorresponGroupId());
        assertNull(page.getCondition().getProjectAdmin());

        assertEquals(expCompanyList.size(), page.getCompanySelectItems().size());
        for (int i = 0; i < expCompanyList.size(); i++) {
            Company expected = expCompanyList.get(i);
            SelectItem actItems = page.getCompanySelectItems().get(i);
            assertEquals(expected.getProjectCompanyId(), actItems.getValue());
            assertEquals(expected.getCodeAndName(), actItems.getLabel());
        }

        assertEquals(expGroupList.size(), page.getGroupSelectItems().size());
        for (int i = 0; i < expGroupList.size(); i++) {
            CorresponGroup expected = expGroupList.get(i);
            SelectItem actItems = page.getGroupSelectItems().get(i);
            assertEquals(expected.getId(), actItems.getValue());
            assertEquals(expected.getName(), actItems.getLabel());
        }

        assertEquals(result.getCount(), page.getDataCount());
        assertEquals(result.getUserIndexes(), page.getUserIndexList());
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.view.admin.UserIndexPage#downloadExcel()} のためのテスト・メソッド.
     */
    @Test
    public void testDownloadExcel() {
        String fileName = "File_Name";
        MockAbstractPage.RET_FILE_NAME = fileName;

        SearchUserCondition condition = new SearchUserCondition();
        condition.setEmpNo("000");
        condition.setNameE("Test");
        condition.setCompanyId(1L);
        condition.setCorresponGroupId(11L);
        page.setCondition(condition);

        List<UserIndex> expIndexList = createUserIndexList();
        SearchUserResult result = new SearchUserResult();
        result.setCount(expIndexList.size());
        result.setUserIndexes(expIndexList);
        MockUserService.RET_SEARCH_PAGING_LIST = result;

        String expected = "EXPECTED_EXCEL_DATA";
        MockUserService.RET_GENERATE_EXCEL = expected.getBytes();

        String actual = page.downloadExcel();

        assertNull(actual);

        assertEquals(fileName + ".xls", MockViewHelper.RET_FILENAME);
        assertEquals(expected, new String(MockViewHelper.RET_DATA));
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.view.admin.UserIndexPage#movePrevious()} のためのテスト・メソッド.
     */
    @Test
    public void testMovePrevious() {
        String projectId = "PJ1";
        MockAbstractPage.RET_PROJID = projectId;

        String empNo = "00001";
        String nameE = "User Name";
        Long companyId = 1L;
        Long corresponGroupId = 2L;
        String securityLevel = "10";

        page.setUserId(empNo);
        page.setName(nameE);
        page.setCompanyId(companyId);
        page.setCorresponGroupId(corresponGroupId);
        page.setSecurityLevel(securityLevel);

        List<Company> expCompanyList = createCompanyList();
        MockCompanyService.RET_SEARCH_RELATED_TO_PROJECT = expCompanyList;

        List<CorresponGroup> expGroupList = createCorresponGroupList();
        MockCorresponGroupService.RET_SEARCH = expGroupList;

        List<UserIndex> expIndexList = createUserIndexList();
        SearchUserResult result = new SearchUserResult();
        result.setCount(expIndexList.size());
        result.setUserIndexes(expIndexList);
        MockUserService.RET_SEARCH_PAGING_LIST = result;

        // 3ページ目から1つ戻る
        page.setPageNo(3);
        String actual = page.movePrevious();

        assertNull(actual);

        assertEquals(2, page.getPageNo());
        assertEquals(2, page.getCondition().getPageNo());

        assertEquals(result.getCount(), page.getDataCount());
        assertEquals(result.getUserIndexes(), page.getUserIndexList());

        // 再設定
        List<UserIndex> newIndexList = createNewUserIndexList();
        SearchUserResult newResult = new SearchUserResult();
        newResult.setCount(newIndexList.size());
        newResult.setUserIndexes(newIndexList);
        MockUserService.RET_SEARCH_PAGING_LIST = newResult;

        // さらに1つ戻る
        String actual2 =  page.movePrevious();

        assertNull(actual2);

        assertEquals(1, page.getPageNo());
        assertEquals(1, page.getCondition().getPageNo());
        assertEquals(newResult.getCount(), page.getDataCount());
        assertEquals(newResult.getUserIndexes(), page.getUserIndexList());
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.view.admin.UserIndexPage#moveNext()} のためのテスト・メソッド.
     */
    @Test
    public void testMoveNext() {
        String projectId = "PJ1";
        MockAbstractPage.RET_PROJID = projectId;

        String empNo = "00001";
        String nameE = "User Name";
        Long companyId = 1L;
        Long corresponGroupId = 2L;
        String securityLevel = "10";

        page.setUserId(empNo);
        page.setName(nameE);
        page.setCompanyId(companyId);
        page.setCorresponGroupId(corresponGroupId);
        page.setSecurityLevel(securityLevel);

        List<Company> expCompanyList = createCompanyList();
        MockCompanyService.RET_SEARCH_RELATED_TO_PROJECT = expCompanyList;

        List<CorresponGroup> expGroupList = createCorresponGroupList();
        MockCorresponGroupService.RET_SEARCH = expGroupList;

        List<UserIndex> expIndexList = createUserIndexList();
        SearchUserResult result = new SearchUserResult();
        result.setCount(expIndexList.size());
        result.setUserIndexes(expIndexList);
        MockUserService.RET_SEARCH_PAGING_LIST = result;

        // 1ページ目から1つ進む
        page.setPageNo(1);
        String actual = page.moveNext();

        assertNull(actual);

        assertEquals(2, page.getPageNo());
        assertEquals(2, page.getCondition().getPageNo());

        assertEquals(result.getCount(), page.getDataCount());
        assertEquals(result.getUserIndexes(), page.getUserIndexList());

        // 再設定
        List<UserIndex> newIndexList = createNewUserIndexList();
        SearchUserResult newResult = new SearchUserResult();
        newResult.setCount(newIndexList.size());
        newResult.setUserIndexes(newIndexList);
        MockUserService.RET_SEARCH_PAGING_LIST = newResult;

        // さらに1つ進む
        String actual2 =  page.moveNext();

        assertNull(actual2);

        assertEquals(3, page.getPageNo());
        assertEquals(3, page.getCondition().getPageNo());
        assertEquals(newResult.getCount(), page.getDataCount());
        assertEquals(newResult.getUserIndexes(), page.getUserIndexList());
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.view.admin.UserIndexPage#changePage()} のためのテスト・メソッド.
     */
    @Test
    public void testChangePage() {
        String projectId = "PJ1";
        MockAbstractPage.RET_PROJID = projectId;

        String empNo = "00001";
        String nameE = "User Name";
        Long companyId = 1L;
        Long corresponGroupId = 2L;
        String securityLevel = "10";

        page.setUserId(empNo);
        page.setName(nameE);
        page.setCompanyId(companyId);
        page.setCorresponGroupId(corresponGroupId);
        page.setSecurityLevel(securityLevel);

        List<Company> expCompanyList = createCompanyList();
        MockCompanyService.RET_SEARCH_RELATED_TO_PROJECT = expCompanyList;

        List<CorresponGroup> expGroupList = createCorresponGroupList();
        MockCorresponGroupService.RET_SEARCH = expGroupList;

        List<UserIndex> expIndexList = createUserIndexList();
        SearchUserResult result = new SearchUserResult();
        result.setCount(expIndexList.size());
        result.setUserIndexes(expIndexList);
        MockUserService.RET_SEARCH_PAGING_LIST = result;

        // 4ページ目を表示
        page.setPageNo(4);
        String actual = page.changePage();

        assertNull(actual);

        assertEquals(4, page.getPageNo());
        assertEquals(4, page.getCondition().getPageNo());

        assertEquals(result.getCount(), page.getDataCount());
        assertEquals(result.getUserIndexes(), page.getUserIndexList());

        // 再設定
        List<UserIndex> newIndexList = createNewUserIndexList();
        SearchUserResult newResult = new SearchUserResult();
        newResult.setCount(newIndexList.size());
        newResult.setUserIndexes(newIndexList);
        MockUserService.RET_SEARCH_PAGING_LIST = newResult;

        // 2ページ目を表示
        page.setPageNo(2);
        String actual2 = page.changePage();

        assertNull(actual2);

        assertEquals(2, page.getPageNo());
        assertEquals(2, page.getCondition().getPageNo());
        assertEquals(newResult.getCount(), page.getDataCount());
        assertEquals(newResult.getUserIndexes(), page.getUserIndexList());
    }

    private List<Company> createCompanyList() {
        List<Company> list = new ArrayList<Company>();

        Company company = new Company();
        company.setId(11L);
        company.setProjectCompanyId(1L);
        company.setCompanyCd("CD1");
        company.setName("Company 1");
        list.add(company);

        company = new Company();
        company.setId(22L);
        company.setProjectCompanyId(2L);
        company.setCompanyCd("CD2");
        company.setName("Company 2");
        list.add(company);

        company = new Company();
        company.setId(33L);
        company.setProjectCompanyId(3L);
        company.setCompanyCd("CD3");
        company.setName("Company 3");
        list.add(company);

        return list;
    }

    private List<CorresponGroup> createCorresponGroupList() {
        List<CorresponGroup> list = new ArrayList<CorresponGroup>();

        CorresponGroup group = new CorresponGroup();
        group.setId(11L);
        group.setName("Group 1");
        list.add(group);

        group = new CorresponGroup();
        group.setId(12L);
        group.setName("Group 2");
        list.add(group);

        group = new CorresponGroup();
        group.setId(13L);
        group.setName("Group 3");
        list.add(group);

        group = new CorresponGroup();
        group.setId(14L);
        group.setName("Group 4");
        list.add(group);

        return list;
    }

    private List<UserIndex> createUserIndexList() {
        List<UserIndex> list = new ArrayList<UserIndex>();

        UserIndex index = new UserIndex();
        CorresponGroup group = new CorresponGroup();
        group.setId(11L);
        group.setName("Group 1");
        index.setCorresponGroup(group);
        ProjectUser pUser = new ProjectUser();
        User user = new User();
        user.setEmpNo("00001");
        user.setNameE("User Name 1");
        pUser.setUser(user);
        index.setProjectUser(pUser);
        index.setSystemAdmin(true);
        index.setProjectAdmin(false);
        index.setGroupAdmin(false);
        index.setPermitUpdate(false);
        list.add(index);

        index = new UserIndex();
        group = new CorresponGroup();
        group.setId(12L);
        group.setName("Group 2");
        index.setCorresponGroup(group);
        pUser = new ProjectUser();
        user = new User();
        user.setEmpNo("00002");
        user.setNameE("User Name 2");
        pUser.setUser(user);
        index.setProjectUser(pUser);
        index.setSystemAdmin(false);
        index.setProjectAdmin(true);
        index.setGroupAdmin(false);
        index.setPermitUpdate(true);
        list.add(index);

        index = new UserIndex();
        group = new CorresponGroup();
        group.setId(13L);
        group.setName("Group 3");
        index.setCorresponGroup(group);
        pUser = new ProjectUser();
        user = new User();
        user.setEmpNo("00003");
        user.setNameE("User Name 3");
        pUser.setUser(user);
        index.setProjectUser(pUser);
        index.setSystemAdmin(false);
        index.setProjectAdmin(false);
        index.setGroupAdmin(true);
        index.setPermitUpdate(true);
        list.add(index);

        return list;
    }

    private List<UserIndex> createNewUserIndexList() {
        List<UserIndex> list = new ArrayList<UserIndex>();

        UserIndex index = new UserIndex();
        CorresponGroup group = new CorresponGroup();
        group.setId(14L);
        group.setName("Group 4");
        index.setCorresponGroup(group);
        ProjectUser pUser = new ProjectUser();
        User user = new User();
        user.setEmpNo("00004");
        user.setNameE("User Name 4");
        pUser.setUser(user);
        index.setProjectUser(pUser);
        index.setSystemAdmin(true);
        index.setProjectAdmin(false);
        index.setGroupAdmin(false);
        index.setPermitUpdate(false);
        list.add(index);

        index = new UserIndex();
        group = new CorresponGroup();
        group.setId(15L);
        group.setName("Group 5");
        index.setCorresponGroup(group);
        pUser = new ProjectUser();
        user = new User();
        user.setEmpNo("00005");
        user.setNameE("User Name 5");
        pUser.setUser(user);
        index.setProjectUser(pUser);
        index.setSystemAdmin(false);
        index.setProjectAdmin(true);
        index.setGroupAdmin(false);
        index.setPermitUpdate(true);
        list.add(index);

        index = new UserIndex();
        group = new CorresponGroup();
        group.setId(16L);
        group.setName("Group 6");
        index.setCorresponGroup(group);
        pUser = new ProjectUser();
        user = new User();
        user.setEmpNo("00006");
        user.setNameE("User Name 6");
        pUser.setUser(user);
        index.setProjectUser(pUser);
        index.setSystemAdmin(false);
        index.setProjectAdmin(false);
        index.setGroupAdmin(true);
        index.setPermitUpdate(true);
        list.add(index);

        return list;
    }

    public static class MockSystemConfig extends MockUp<SystemConfig> {
        static Map<String, String> VALUES = new HashMap<String, String>();

        @Mock
        public static String getValue(String key) {
            return VALUES.get(key);
        }
    }

    public static class MockAbstractPage extends MockUp<AbstractPage> {
        static String RET_PROJID;
        static boolean IS_SYSTEM_ADMIN;
        static boolean IS_PROJECT_ADMIN;
        static boolean IS_ANY_GROUP_ADMIN;
        static AbstractCondition SET_NEXT_SEARCH_CONDITION;
        static AbstractCondition RET_PREVIOUS_SEARCH_CONDITION;
        static String RET_FILE_NAME;

        @Mock
        public String getCurrentProjectId() {
            return RET_PROJID;
        }

        @Mock
        public boolean isSystemAdmin() {
            return IS_SYSTEM_ADMIN;
        }

        @Mock
        public boolean isProjectAdmin(String projectId) {
            return IS_PROJECT_ADMIN;
        }

        @Mock
        public boolean isAnyGroupAdmin(String projectId) {
            return IS_ANY_GROUP_ADMIN;
        }

        @Mock
        public void setNextSearchCondition(AbstractCondition condition) {
            SET_NEXT_SEARCH_CONDITION = condition;
        }
        @Mock
        public AbstractCondition getPreviousSearchCondition(Class<? extends AbstractCondition> clazz) {
            return RET_PREVIOUS_SEARCH_CONDITION;
        }

        @Mock
        public String createFileName() {
            return RET_FILE_NAME;
        }
    }

    public static class MockUserService extends MockUp<UserServiceImpl> {
        static SearchUserResult RET_SEARCH_PAGING_LIST;
        static byte[] RET_GENERATE_EXCEL;

        @Mock
        public SearchUserResult searchPagingList(SearchUserCondition condition)
                throws ServiceAbortException {
            return RET_SEARCH_PAGING_LIST;
        }

        @Mock
        public byte[] generateExcel(List<UserIndex> userIndexs) throws ServiceAbortException {
            return RET_GENERATE_EXCEL;
        }
    }

    public static class MockCompanyService extends MockUp<CompanyServiceImpl> {
        static List<Company> RET_SEARCH_RELATED_TO_PROJECT;

        @Mock
        public List<Company> searchRelatedToProject(String projectId) {
            return RET_SEARCH_RELATED_TO_PROJECT;
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

    public static class MockViewHelper extends MockUp<ViewHelper> {
        static byte[] RET_DATA;
        static String RET_FILENAME;

        @Mock
        public void download(String fileName, byte[] content) {
            RET_DATA = content;
            RET_FILENAME = fileName;
        }
    }
}
