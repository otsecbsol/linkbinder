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

import static jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode.*;
import static org.junit.Assert.*;

import java.util.List;

import javax.annotation.Resource;
import javax.faces.application.FacesMessage;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import jp.co.opentone.bsol.framework.core.exception.InvalidOperationRuntimeException;
import jp.co.opentone.bsol.framework.core.message.Messages;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.extension.jsf.FacesContextMock;
import jp.co.opentone.bsol.framework.test.AbstractTestCase;
import jp.co.opentone.bsol.linkbinder.dto.SearchSiteResult;
import jp.co.opentone.bsol.linkbinder.dto.Site;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchSiteCondition;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.service.admin.impl.SiteServiceImpl;
import jp.co.opentone.bsol.linkbinder.view.AbstractPage;
import mockit.Mock;
import mockit.MockUp;

/**
 * {@link SiteEditPage}のテストケース.
 * @author opentone
 */
public class SiteEditPageTest extends AbstractTestCase {

    /**
     * テスト対象クラス
     */
    @Resource
    private SiteEditPage page;

    /**
     * ユーザー情報
     */
    private User user;

    /**
     * テストの前準備.
     */
    @BeforeClass
    public static void testSetUp() {
        FacesContextMock.initialize();
        new MockAbstractPage();
        new MockSiteService();
    }

    /**
     * 後始末.
     */
    @AfterClass
    public static void testTeardown() {
        new MockAbstractPage().tearDown();
        new MockSiteService().tearDown();
        FacesContextMock.tearDown();
    }

    @Before
    public void setUp() {
        user = new User();
        user.setEmpNo("ZZA01");
        user.setNameE("Taro Yamada");
    }

    @After
    public void tearDown() {
        MockAbstractPage.RET_PROJID = null;
        MockAbstractPage.IS_SYSTEM_ADMIN = false;
        MockAbstractPage.IS_PROJECT_ADMIN = false;
        MockAbstractPage.IS_ANY_GROUP_ADMIN = false;
        MockSiteService.RET_SEARCH = null;
        MockSiteService.CRT_DELETE = null;
        page.setCode(null);
        page.setName(null);
    }

    /**
     * 初期化アクションを検証する. 登録、SystemAdminの場合
     * @throws Exception
     */
    @Test
    public void testInsertInitializeSystemAdmin() throws Exception {
        // テストに必要なデータを作成
        MockAbstractPage.IS_SYSTEM_ADMIN = true;
        MockAbstractPage.RET_PROJID = "PJ1";

        page.initialize();

        assertEquals("拠点新規登録", page.getTitle());
        assertTrue(page.getInitialDisplaySuccess());
    }

    /**
     * 初期化アクションを検証する. 登録、ProjectAdminの場合
     * @throws Exception
     */
    @Test
    public void testInsertInitializeProjectAdmin() throws Exception {
        // テストに必要なデータを作成
        MockAbstractPage.IS_PROJECT_ADMIN = true;
        MockAbstractPage.RET_PROJID = "PJ1";

        page.initialize();

        assertEquals("拠点新規登録", page.getTitle());
        assertTrue(page.getInitialDisplaySuccess());
    }

    /**
     * 初期化アクションを検証する. 登録、権限がない場合
     * @throws Exception
     */
    @Test
    public void testInvalidPermissionInitialize() throws Exception {
        // テストに必要なデータを作成
        // 権限がない
        MockAbstractPage.IS_SYSTEM_ADMIN = false;
        MockAbstractPage.IS_PROJECT_ADMIN = false;
        MockAbstractPage.RET_PROJID = "PJ1";

        // 期待されるメッセージをセット
        FacesContextMock.EXPECTED_CLIENT_ID = null;
        FacesContextMock.EXPECTED_MESSAGE =
                new FacesMessage(
                    FacesMessage.SEVERITY_ERROR, FacesMessage.SEVERITY_ERROR.toString(),
                    createExpectedMessageString(
                        Messages.getMessageAsString(CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT),
                        "Initialize"));
        try {
            page.initialize();
            fail("例外が発生していない");
        } catch (InvalidOperationRuntimeException e) {
            ServiceAbortException actual = (ServiceAbortException)e.getCause();
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT, actual.getMessageCode());
        }
    }

    /**
     * 初期化アクションを検証する. プロジェクトが選択されていない場合
     * @throws Exception
     */
    @Test
    public void testProjectNotSelectedInitialize() throws Exception {
        // テストに必要なデータを作成
        // 権限はあるがプロジェクトが選択されていない
        MockAbstractPage.IS_SYSTEM_ADMIN = false;
        MockAbstractPage.IS_PROJECT_ADMIN = true;
        MockAbstractPage.RET_PROJID = null;

        // 期待されるメッセージをセット
        FacesContextMock.EXPECTED_CLIENT_ID = null;
        FacesContextMock.EXPECTED_MESSAGE =
                new FacesMessage(
                    FacesMessage.SEVERITY_ERROR, FacesMessage.SEVERITY_ERROR.toString(),
                    createExpectedMessageString(
                        Messages.getMessageAsString(CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT),
                        "Initialize"));
        try {
            page.initialize();
            fail("例外が発生していない");
        } catch (InvalidOperationRuntimeException e) {
            ServiceAbortException actual = (ServiceAbortException)e.getCause();
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT, actual.getMessageCode());
        }
    }

    /**
     * 初期化アクションを検証する. 更新、SystemAdminの場合
     * @throws Exception
     */
    @Test
    public void testUpdateInitializeSystemAdmin() throws Exception {
        // テストに必要なデータを作成
        MockAbstractPage.IS_SYSTEM_ADMIN = true;
        MockAbstractPage.RET_PROJID = "PJ1";
        page.setId(1L);

        Site site = new Site();
        site.setId(1L);
        site.setSiteCd("YOC");
        site.setName("Yocohama");
        site.setProjectId("PJ1");
        site.setProjectNameE("Test Project1");
        site.setCreatedBy(user);
        site.setUpdatedBy(user);
        site.setVersionNo(1L);
        site.setDeleteNo(0L);

        MockSiteService.RET_FIND = site;

        page.initialize();

        assertEquals("拠点更新", page.getTitle());
        assertTrue(page.getInitialDisplaySuccess());
        assertEquals(site.getSiteCd(), page.getCode());
        assertEquals(site.getName(), page.getName());
    }

    /**
     * 初期化アクションを検証する. 更新、ProjectAdminの場合
     * @throws Exception
     */
    @Test
    public void testUpdateInitializeProjectAdmin() throws Exception {
        // テストに必要なデータを作成
        MockAbstractPage.IS_PROJECT_ADMIN = true;
        MockAbstractPage.RET_PROJID = "PJ1";
        page.setId(1L);

        Site site = new Site();
        site.setId(1L);
        site.setSiteCd("YOC");
        site.setName("Yocohama");
        site.setProjectId("PJ1");
        site.setProjectNameE("Test Project1");
        site.setCreatedBy(user);
        site.setUpdatedBy(user);
        site.setVersionNo(1L);
        site.setDeleteNo(0L);

        MockSiteService.RET_FIND = site;

        page.initialize();

        assertEquals("拠点更新", page.getTitle());
        assertTrue(page.getInitialDisplaySuccess());
        assertEquals(site.getSiteCd(), page.getCode());
        assertEquals(site.getName(), page.getName());
    }

    /**
     * 入力値を検証するアクションを検証. 登録の場合
     * @throws Exception
     */
    @Test
    public void testInsertValidate() throws Exception {
        // テストに必要なデータを作成
        page.setCode("YOC");
        page.setName("Yocohama");

        MockAbstractPage.RET_PROJID = "PJ1";

        String next = page.next();

        assertEquals("siteConfirmation?projectId=PJ1", next);
        assertTrue(page.getSite() != null);
        assertEquals("YOC", page.getSite().getSiteCd());
        assertEquals("Yocohama", page.getSite().getName());
    }

    /**
     * 入力値を検証するアクションを検証. 更新の場合
     * @throws Exception
     */
    @Test
    public void testUpdateValidate() throws Exception {
        // テストに必要なデータを作成
        Site site = new Site();
        site.setId(1L);
        site.setSiteCd("YOC");
        site.setName("Yocohama");
        site.setProjectId("PJ1");
        site.setProjectNameE("Test Project1");
        site.setCreatedBy(user);
        site.setUpdatedBy(user);
        site.setVersionNo(1L);
        site.setDeleteNo(0L);

        page.setSite(site);
        page.setCode("SHJ");
        page.setName("Shinjuku");

        MockAbstractPage.RET_PROJID = "PJ1";

        String next = page.next();

        assertEquals("siteConfirmation?projectId=PJ1", next);
        assertTrue(page.getSite() != null);
        assertEquals("SHJ", page.getSite().getSiteCd());
        assertEquals("Shinjuku", page.getSite().getName());
        assertEquals(site.getId(), page.getSite().getId());
        assertEquals(site.getProjectId(), page.getSite().getProjectId());
        assertEquals(site.getProjectNameE(), page.getSite().getProjectNameE());
        assertEquals(site.getCreatedBy(), page.getSite().getCreatedBy());
        assertEquals(site.getUpdatedBy(), page.getSite().getUpdatedBy());
        assertEquals(site.getVersionNo(), page.getSite().getVersionNo());
        assertEquals(site.getDeleteNo(), page.getSite().getDeleteNo());

    }

    public static class MockAbstractPage extends MockUp<AbstractPage> {
        static String RET_PROJID;
        static boolean IS_SYSTEM_ADMIN;
        static boolean IS_PROJECT_ADMIN;
        static boolean IS_ANY_GROUP_ADMIN;

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
    }

    public static class MockSiteService extends MockUp<SiteServiceImpl> {
        static SearchSiteResult RET_SEARCH;
        static byte[] RET_EXCEL;
        static Site CRT_DELETE;
        static Site RET_FIND;
        static Site CRT_VALIDATE;

        @Mock
        public SearchSiteResult search(SearchSiteCondition condition) throws ServiceAbortException {
            return RET_SEARCH;
        }

        @Mock
        public byte[] generateExcel(List<Site> Sites) throws ServiceAbortException {
            return RET_EXCEL;
        }

        @Mock
        public void delete(Site site) throws ServiceAbortException {
            CRT_DELETE = site;
        }

        @Mock
        public Site find(Long id) throws ServiceAbortException {
            return RET_FIND;
        }

        @Mock
        public void validate(Site site) throws ServiceAbortException {
            CRT_VALIDATE = site;
        }
    }
}
