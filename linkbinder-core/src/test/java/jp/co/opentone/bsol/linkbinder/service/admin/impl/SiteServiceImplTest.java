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

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import jp.co.opentone.bsol.framework.core.dao.RecordNotFoundException;
import jp.co.opentone.bsol.framework.core.dao.StaleRecordException;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.test.AbstractTestCase;
import jp.co.opentone.bsol.linkbinder.dao.impl.CorresponGroupDaoImpl;
import jp.co.opentone.bsol.linkbinder.dao.impl.SiteDaoImpl;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroup;
import jp.co.opentone.bsol.linkbinder.dto.Project;
import jp.co.opentone.bsol.linkbinder.dto.SearchSiteResult;
import jp.co.opentone.bsol.linkbinder.dto.Site;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponGroupCondition;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchSiteCondition;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.service.MockAbstractService;
import jp.co.opentone.bsol.linkbinder.service.admin.SiteService;
import mockit.Mock;
import mockit.MockUp;

/**
 * {@link SiteServiceImpl}のテストケース
 * @author opentone
 */
public class SiteServiceImplTest extends AbstractTestCase {

    /**
     * テスト対象.
     */
    @Resource
    private SiteService service;

    private User user = new User();

    @BeforeClass
    public static void testSetUp() {
        new MockAbstractService();
    }

    @AfterClass
    public static void testTeardown() {
        new MockAbstractService().tearDown();;
    }

    /**
     * テスト前準備.
     */
    @Before
    public void setUp() {
        user.setEmpNo("ZZA01");
        user.setNameE("Taro Yamada");
        MockAbstractService.CURRENT_USER = user;

    }

    /**
     * 後始末.
     */
    @After
    public void tearDown() {
        tearDownMockAbstractService();
    }

    private void tearDownMockAbstractService() {
        MockAbstractService.CURRENT_PROJECT_ID = null;
        MockAbstractService.CURRENT_PROJECT = null;
        MockAbstractService.CURRENT_USER = null;
        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        MockAbstractService.IS_GROUP_ADMIN = null;
        MockAbstractService.IS_ANY_GROUP_ADMIN = false;
        MockAbstractService.ACCESSIBLE_PROJECTS = null;
        MockAbstractService.VALIDATE_PROJECT_ID_EXCEPTION = null;
    }

    /**
     * 拠点情報取得処理を検証する. SystemAdminの場合
     * @throws Exception
     */
    @Test
    public void testSearchSystemAdmin() throws Exception {
        // テストに必要なデータを作成
        SearchSiteCondition condition = new SearchSiteCondition();
        condition.setSystemAdmin(true);
        condition.setProjectAdmin(false);
        condition.setGroupAdmin(false);
        condition.setProjectId("PJ1");

        List<Site> sites = createSiteList();

        // Mock準備
        MockAbstractService.IS_SYSTEM_ADMIN = true;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        new MockUp<SiteDaoImpl>() {
            @Mock List<Site> find(SearchSiteCondition condition) {
                return sites;
            }

            @Mock int count(SearchSiteCondition condition) {
                return sites.size();
            }
        };

        SearchSiteResult result = service.search(condition);

        assertEquals(sites.get(0).getId(), result.getSiteList().get(0).getId());
        assertEquals(sites.get(0).getProjectId(), result.getSiteList().get(0).getProjectId());
        assertEquals(sites.get(0).getProjectNameE(), result.getSiteList().get(0).getProjectNameE());
        assertEquals(sites.get(0).getSiteCd(), result.getSiteList().get(0).getSiteCd());
        assertEquals(sites.get(0).getName(), result.getSiteList().get(0).getName());
        assertEquals(sites.get(0).getCreatedBy(), result.getSiteList().get(0).getCreatedBy());
        assertEquals(sites.get(0).getUpdatedBy(), result.getSiteList().get(0).getUpdatedBy());
        assertEquals(sites.get(0).getVersionNo(), result.getSiteList().get(0).getVersionNo());
        assertEquals(sites.get(0).getDeleteNo(), result.getSiteList().get(0).getDeleteNo());

        assertEquals(sites.get(1).getId(), result.getSiteList().get(1).getId());
        assertEquals(sites.get(1).getProjectId(), result.getSiteList().get(1).getProjectId());
        assertEquals(sites.get(1).getProjectNameE(), result.getSiteList().get(1).getProjectNameE());
        assertEquals(sites.get(1).getSiteCd(), result.getSiteList().get(1).getSiteCd());
        assertEquals(sites.get(1).getName(), result.getSiteList().get(1).getName());
        assertEquals(sites.get(1).getCreatedBy(), result.getSiteList().get(1).getCreatedBy());
        assertEquals(sites.get(1).getUpdatedBy(), result.getSiteList().get(1).getUpdatedBy());
        assertEquals(sites.get(1).getVersionNo(), result.getSiteList().get(1).getVersionNo());
        assertEquals(sites.get(1).getDeleteNo(), result.getSiteList().get(1).getDeleteNo());

        assertEquals(sites.get(2).getId(), result.getSiteList().get(2).getId());
        assertEquals(sites.get(2).getProjectId(), result.getSiteList().get(2).getProjectId());
        assertEquals(sites.get(2).getProjectNameE(), result.getSiteList().get(2).getProjectNameE());
        assertEquals(sites.get(2).getSiteCd(), result.getSiteList().get(2).getSiteCd());
        assertEquals(sites.get(2).getName(), result.getSiteList().get(2).getName());
        assertEquals(sites.get(2).getCreatedBy(), result.getSiteList().get(2).getCreatedBy());
        assertEquals(sites.get(2).getUpdatedBy(), result.getSiteList().get(2).getUpdatedBy());
        assertEquals(sites.get(2).getVersionNo(), result.getSiteList().get(2).getVersionNo());
        assertEquals(sites.get(2).getDeleteNo(), result.getSiteList().get(2).getDeleteNo());

    }

    /**
     * 拠点情報取得処理を検証する. ProjectAdminの場合
     * @throws Exception
     */
    @Test
    public void testSearchProjectAdmin() throws Exception {
        // テストに必要なデータを作成
        SearchSiteCondition condition = new SearchSiteCondition();
        condition.setSystemAdmin(false);
        condition.setProjectAdmin(true);
        condition.setGroupAdmin(false);
        condition.setProjectId("PJ1");

        List<Site> sites = createSiteList();

        // Mock準備
        MockAbstractService.IS_PROJECT_ADMIN = true;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        new MockUp<SiteDaoImpl>() {
            @Mock List<Site> find(SearchSiteCondition condition) {
                return sites;
            }

            @Mock int count(SearchSiteCondition condition) {
                return sites.size();
            }
        };

        SearchSiteResult result = service.search(condition);

        assertEquals(sites.get(0).getId(), result.getSiteList().get(0).getId());
        assertEquals(sites.get(0).getProjectId(), result.getSiteList().get(0).getProjectId());
        assertEquals(sites.get(0).getProjectNameE(), result.getSiteList().get(0).getProjectNameE());
        assertEquals(sites.get(0).getSiteCd(), result.getSiteList().get(0).getSiteCd());
        assertEquals(sites.get(0).getName(), result.getSiteList().get(0).getName());
        assertEquals(sites.get(0).getCreatedBy(), result.getSiteList().get(0).getCreatedBy());
        assertEquals(sites.get(0).getUpdatedBy(), result.getSiteList().get(0).getUpdatedBy());
        assertEquals(sites.get(0).getVersionNo(), result.getSiteList().get(0).getVersionNo());
        assertEquals(sites.get(0).getDeleteNo(), result.getSiteList().get(0).getDeleteNo());

        assertEquals(sites.get(1).getId(), result.getSiteList().get(1).getId());
        assertEquals(sites.get(1).getProjectId(), result.getSiteList().get(1).getProjectId());
        assertEquals(sites.get(1).getProjectNameE(), result.getSiteList().get(1).getProjectNameE());
        assertEquals(sites.get(1).getSiteCd(), result.getSiteList().get(1).getSiteCd());
        assertEquals(sites.get(1).getName(), result.getSiteList().get(1).getName());
        assertEquals(sites.get(1).getCreatedBy(), result.getSiteList().get(1).getCreatedBy());
        assertEquals(sites.get(1).getUpdatedBy(), result.getSiteList().get(1).getUpdatedBy());
        assertEquals(sites.get(1).getVersionNo(), result.getSiteList().get(1).getVersionNo());
        assertEquals(sites.get(1).getDeleteNo(), result.getSiteList().get(1).getDeleteNo());

        assertEquals(sites.get(2).getId(), result.getSiteList().get(2).getId());
        assertEquals(sites.get(2).getProjectId(), result.getSiteList().get(2).getProjectId());
        assertEquals(sites.get(2).getProjectNameE(), result.getSiteList().get(2).getProjectNameE());
        assertEquals(sites.get(2).getSiteCd(), result.getSiteList().get(2).getSiteCd());
        assertEquals(sites.get(2).getName(), result.getSiteList().get(2).getName());
        assertEquals(sites.get(2).getCreatedBy(), result.getSiteList().get(2).getCreatedBy());
        assertEquals(sites.get(2).getUpdatedBy(), result.getSiteList().get(2).getUpdatedBy());
        assertEquals(sites.get(2).getVersionNo(), result.getSiteList().get(2).getVersionNo());
        assertEquals(sites.get(2).getDeleteNo(), result.getSiteList().get(2).getDeleteNo());

    }

    /**
     * 拠点情報取得処理を検証する. GroupAdminの場合
     * @throws Exception
     */
    @Test
    public void testSearchGroupAdmin() throws Exception {
        // テストに必要なデータを作成
        SearchSiteCondition condition = new SearchSiteCondition();
        condition.setSystemAdmin(false);
        condition.setProjectAdmin(false);
        condition.setGroupAdmin(true);
        condition.setProjectId("PJ1");

        List<Site> sites = createSiteList();

        // Mock準備
        MockAbstractService.IS_ANY_GROUP_ADMIN = true;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        new MockUp<SiteDaoImpl>() {
            @Mock List<Site> find(SearchSiteCondition condition) {
                return sites;
            }
            @Mock int count(SearchSiteCondition condition) {
                return sites.size();
            }
        };

        SearchSiteResult result = service.search(condition);

        assertEquals(sites.get(0).getId(), result.getSiteList().get(0).getId());
        assertEquals(sites.get(0).getProjectId(), result.getSiteList().get(0).getProjectId());
        assertEquals(sites.get(0).getProjectNameE(), result.getSiteList().get(0).getProjectNameE());
        assertEquals(sites.get(0).getSiteCd(), result.getSiteList().get(0).getSiteCd());
        assertEquals(sites.get(0).getName(), result.getSiteList().get(0).getName());
        assertEquals(sites.get(0).getCreatedBy(), result.getSiteList().get(0).getCreatedBy());
        assertEquals(sites.get(0).getUpdatedBy(), result.getSiteList().get(0).getUpdatedBy());
        assertEquals(sites.get(0).getVersionNo(), result.getSiteList().get(0).getVersionNo());
        assertEquals(sites.get(0).getDeleteNo(), result.getSiteList().get(0).getDeleteNo());

        assertEquals(sites.get(1).getId(), result.getSiteList().get(1).getId());
        assertEquals(sites.get(1).getProjectId(), result.getSiteList().get(1).getProjectId());
        assertEquals(sites.get(1).getProjectNameE(), result.getSiteList().get(1).getProjectNameE());
        assertEquals(sites.get(1).getSiteCd(), result.getSiteList().get(1).getSiteCd());
        assertEquals(sites.get(1).getName(), result.getSiteList().get(1).getName());
        assertEquals(sites.get(1).getCreatedBy(), result.getSiteList().get(1).getCreatedBy());
        assertEquals(sites.get(1).getUpdatedBy(), result.getSiteList().get(1).getUpdatedBy());
        assertEquals(sites.get(1).getVersionNo(), result.getSiteList().get(1).getVersionNo());
        assertEquals(sites.get(1).getDeleteNo(), result.getSiteList().get(1).getDeleteNo());

        assertEquals(sites.get(2).getId(), result.getSiteList().get(2).getId());
        assertEquals(sites.get(2).getProjectId(), result.getSiteList().get(2).getProjectId());
        assertEquals(sites.get(2).getProjectNameE(), result.getSiteList().get(2).getProjectNameE());
        assertEquals(sites.get(2).getSiteCd(), result.getSiteList().get(2).getSiteCd());
        assertEquals(sites.get(2).getName(), result.getSiteList().get(2).getName());
        assertEquals(sites.get(2).getCreatedBy(), result.getSiteList().get(2).getCreatedBy());
        assertEquals(sites.get(2).getUpdatedBy(), result.getSiteList().get(2).getUpdatedBy());
        assertEquals(sites.get(2).getVersionNo(), result.getSiteList().get(2).getVersionNo());
        assertEquals(sites.get(2).getDeleteNo(), result.getSiteList().get(2).getDeleteNo());

    }

    /**
     * 拠点情報取得処理を検証する. 引数がnullの場合
     * @throws Exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSearchArgumentNull() throws Exception {
        // テストに必要なデータを作成
        service.search(null);
    }

    /**
     * 拠点情報取得処理を検証する. 権限がない場合
     * @throws Exception
     */
    @Test
    public void testSearchInvalidPermission() throws Exception {
        // テストに必要なデータを作成
        SearchSiteCondition condition = new SearchSiteCondition();
        condition.setSystemAdmin(false);
        condition.setProjectAdmin(false);
        condition.setGroupAdmin(true);
        condition.setProjectId("PJ1");

        // 権限がない
        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        MockAbstractService.IS_ANY_GROUP_ADMIN = false;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";

        try {
            service.search(condition);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT,
                actual.getMessageCode());
        }
    }

    /**
     * 拠点情報取得処理を検証する. 該当データ0件の場合
     * @throws Exception
     */
    @Test
    public void testSearchNoData() throws Exception {
        // テストに必要なデータを作成
        SearchSiteCondition condition = new SearchSiteCondition();
        condition.setSystemAdmin(false);
        condition.setProjectAdmin(false);
        condition.setGroupAdmin(true);
        condition.setProjectId("PJ1");

        // Mock準備
        MockAbstractService.IS_SYSTEM_ADMIN = true;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        new MockUp<SiteDaoImpl>() {
            @Mock List<Site> find(SearchSiteCondition condition) {
                return new ArrayList<>();
            }
            @Mock int count(SearchSiteCondition condition) {
                return 0;
            }
        };

        try {
            service.search(condition);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.NO_DATA_FOUND, actual.getMessageCode());
        }
    }

    /**
     * 拠点情報取得処理を検証する. 指定のページを選択した際にそのページに表示するデータが存在しない場合
     * @throws Exception
     */
    @Test
    public void testSearchNoPageData() throws Exception {
        // テストに必要なデータを作成
        SearchSiteCondition condition = new SearchSiteCondition();
        condition.setSystemAdmin(false);
        condition.setProjectAdmin(false);
        condition.setGroupAdmin(true);
        condition.setProjectId("PJ1");

        // Mock準備
        MockAbstractService.IS_SYSTEM_ADMIN = true;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        new MockUp<SiteDaoImpl>() {
            @Mock List<Site> find(SearchSiteCondition condition) {
                return new ArrayList<>();
            }
            @Mock int count(SearchSiteCondition condition) {
                return 3;
            }
        };

        try {
            service.search(condition);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.NO_PAGE_FOUND, actual.getMessageCode());
        }
    }

    /**
     * 指定された会社情報一覧をExcel形式に変換して返す処理を検証
     */
    @Test
    public void testGenerateExcel() throws Exception {
        // 結果レコード
        List<Object> expected = new ArrayList<Object>();
        /* ヘッダ */
        List<Object> line = new ArrayList<Object>();
        line.add("ID");
        line.add("Code");
        line.add("Name");
        expected.add(line);

        /* 1行目 */
        line = new ArrayList<Object>();
        line.add(new Integer(1));
        line.add("YOC");
        line.add("Yocohama");
        expected.add(line);

        /* 2行目 */
        line = new ArrayList<Object>();
        line.add(new Integer(2));
        line.add("SJK");
        line.add("Shinjuku");
        expected.add(line);

        /* 3行目 */
        line = new ArrayList<Object>();
        line.add(new Integer(3));
        line.add("JGI");
        line.add("Jogi");
        expected.add(line);

        // マスタ管理の為、リストを作成する際にnullを渡す。
        byte[] actual = service.generateExcel(createSiteList());

        // 作成したExcelを確認
        assertExcel(1, "SiteIndex", 3, expected, actual);
    }

    /**
     * 指定された会社情報一覧をExcel形式に変換して返す処理を検証 引数がnull
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGenerateExcelArgumentNull() throws Exception {
        service.generateExcel(null);
    }

    /**
     * 入力値をチェックする処理を検証. 登録
     * @throws Exception
     */
    @Test
    public void testValidateInsert() throws Exception {
        // テストに必要なデータを作成
        MockAbstractService.IS_SYSTEM_ADMIN = true;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";

        List<Project> lp = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        lp.add(p);

        // Mock準備
        MockAbstractService.ACCESSIBLE_PROJECTS = lp;
        new MockUp<SiteDaoImpl>() {
            @Mock List<Site> find(SearchSiteCondition condition) {
                return new ArrayList<>();
            }
            @Mock int countCheck(SearchSiteCondition condition) {
                return 0;
            }
        };

        Site site = new Site();
        site.setSiteCd("YOC");
        site.setName("Yocohama");
        service.validate(site);
    }

    /**
     * 入力値をチェックする処理を検証. 更新
     * @throws Exception
     */
    @Test
    public void testValidateUpdate() throws Exception {
        // テストに必要なデータを作成
        List<Project> lp = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        lp.add(p);

        Site site = createSite();

        // Mock準備
        MockAbstractService.IS_SYSTEM_ADMIN = true;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        MockAbstractService.ACCESSIBLE_PROJECTS = lp;
        new MockUp<SiteDaoImpl>() {
            @Mock List<Site> find(SearchSiteCondition condition) {
                return new ArrayList<>();
            }
            @Mock int count(SearchSiteCondition condition) {
                return 0;
            }
            @Mock Site findById(Long id) {
                return site;
            }
        };

        service.validate(site);
    }

    /**
     * 入力値をチェックする処理を検証. 引数がnull
     * @throws Exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testValidateArgumentNull() throws Exception {
        service.validate(null);
        fail("例外が発生していない");
    }

    /**
     * 入力値をチェックする処理を検証. 権限がない
     * @throws Exception
     */
    @Test
    public void testValidateInvalidPermission() throws Exception {
        // テストに必要なデータを作成
        List<Project> lp = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        lp.add(p);

        Site site = createSite();

        // 権限がない
        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        MockAbstractService.ACCESSIBLE_PROJECTS = lp;

        try {
            service.validate(site);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT,
                actual.getMessageCode());
        }
    }

    /**
     * 入力値をチェックする処理を検証. 更新、該当データが0件
     * @throws Exception
     */
    @Test
    public void testValidateNoData() throws Exception {
        // テストに必要なデータを作成
        List<Project> lp = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        lp.add(p);

        Site site = createSite();

        // Mock準備
        MockAbstractService.IS_PROJECT_ADMIN = true;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        MockAbstractService.ACCESSIBLE_PROJECTS = lp;
        new MockUp<SiteDaoImpl>() {
            @Mock List<Site> find(SearchSiteCondition condition) {
                return new ArrayList<>();
            }
            @Mock Site findById(Long id) throws RecordNotFoundException {
                // 該当データ無し
                throw new RecordNotFoundException();
            }
        };

        try {
            service.validate(site);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.NO_DATA_FOUND, actual.getMessageCode());
        }
    }

    /**
     * 入力値をチェックする処理を検証. 同じ拠点情報が既に存在する
     * @throws Exception
     */
    @Test
    public void testValidateExistSiteCode() throws Exception {
        // テストに必要なデータを作成
        List<Project> lp = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        lp.add(p);

        Site findSite = createSite();

        // Mock準備
        MockAbstractService.IS_PROJECT_ADMIN = true;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        MockAbstractService.ACCESSIBLE_PROJECTS = lp;
        new MockUp<SiteDaoImpl>() {
            @Mock List<Site> find(SearchSiteCondition condition) {
                return new ArrayList<>();
            }
            @Mock int countCheck(SearchSiteCondition condition) {
                return 1;
            }
            @Mock Site findById(Long id) throws RecordNotFoundException {
                return findSite;
            }
        };

        try {
            service.validate(findSite);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_SITE_CODE_ALREADY_EXISTS,
                actual.getMessageCode());
            assertEquals(1, actual.getMessageVars().length);
            assertEquals("YOC", actual.getMessageVars()[0]);
        }
    }

    /**
     * IDを指定して拠点情報を取得する処理を検証.
     * @throws Exception
     */
    @Test
    public void testFind() throws Exception {
        // テストに必要なデータを作成
        Site site = createSite();

        List<Project> lp = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        lp.add(p);

        // Mock準備
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        MockAbstractService.ACCESSIBLE_PROJECTS = lp;
        new MockUp<SiteDaoImpl>() {
            @Mock Site findById(Long id) throws RecordNotFoundException {
                return site;
            }
        };

        Site actual = service.find(1L);

        assertEquals(site.getId(), actual.getId());
        assertEquals(site.getSiteCd(), actual.getSiteCd());
        assertEquals(site.getName(), actual.getName());
        assertEquals(site.getProjectId(), actual.getProjectId());
        assertEquals(site.getProjectNameE(), actual.getProjectNameE());
        assertEquals(site.getCreatedBy().toString(), actual.getCreatedBy().toString());
        assertEquals(site.getUpdatedBy().toString(), actual.getUpdatedBy().toString());
        assertEquals(site.getVersionNo(), actual.getVersionNo());
        assertEquals(site.getDeleteNo(), actual.getDeleteNo());

    }

    /**
     * IDを指定して拠点情報を取得する処理を検証. 該当データ0件
     * @throws Exception
     */
    @Test
    public void testFindNoData() throws Exception {
        // テストに必要なデータを作成
        List<Project> lp = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        lp.add(p);

        // Mock準備
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        MockAbstractService.ACCESSIBLE_PROJECTS = lp;
        new MockUp<SiteDaoImpl>() {
            @Mock Site findById(Long id) throws RecordNotFoundException {
                // 該当データ無し
                throw new RecordNotFoundException();
            }
        };

        try {
            service.find(1L);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.NO_DATA_FOUND, actual.getMessageCode());
        }
    }

    /**
     * IDを指定して拠点情報を取得する処理を検証. 拠点のプロジェクトが現在選択中のプロジェクトではない
     * @throws Exception
     */
    @Test
    public void testFindNotExistProject() throws Exception {
        // テストに必要なデータを作成
        Site site = createSite();

        List<Project> lp = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ2");
        lp.add(p);

        // Mock準備
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        MockAbstractService.ACCESSIBLE_PROJECTS = lp;
        new MockUp<SiteDaoImpl>() {
            @Mock Site findById(Long id) throws RecordNotFoundException {
                return site;
            }
        };

        try {
            service.find(1L);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_PROJECT_DIFF, actual
                .getMessageCode());
        }
    }

    /**
     * IDを指定して拠点情報を取得する処理を検証. 引数がnull
     * @throws Exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testFindArgumentNull() throws Exception {
        // テストに必要なデータを作成
        service.find(null);
        fail("例外が発生していない");
    }

    /**
     * 拠点情報保存処理を検証する. 引数がnull
     * @throws Exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSaveArgumentNull() throws Exception {
        service.save(null);
        fail("例外が発生していない");
    }

    /**
     * 拠点情報保存処理を検証する. 登録
     * @throws Exception
     */
    @Test
    public void testSaveInsert() throws Exception {
        // テストに必要なデータを作成
        Site site = new Site();
        site.setSiteCd("SHJ");
        site.setName("Shinjuku");

        // Mock準備
        MockAbstractService.IS_PROJECT_ADMIN = true;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        new MockUp<SiteDaoImpl>() {
            @Mock List<Site> find(SearchSiteCondition condition) {
                return new ArrayList<>();
            }
            @Mock int count(SearchSiteCondition condition) {
                return 0;
            }
            @Mock Long create(Site s) {
                assertThat(s.getSiteCd(), is(site.getSiteCd()));
                assertThat(s.getName(), is(site.getName()));

                return 1L;
            }
        };

        Long id = service.save(site);
        assertThat(id, is(1L));
    }

    /**
     * 拠点情報保存処理を検証する. 権限がない
     * @throws Exception
     */
    @Test
    public void testSaveInvalidPermission() throws Exception {
        // テストに必要なデータを作成
        Site site = new Site();
        site.setSiteCd("SHJ");
        site.setName("Shinjuku");

        // 権限がない
        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        try {
            service.save(site);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT,
                actual.getMessageCode());
        }
    }

    /**
     * 拠点情報保存処理を検証する. 登録、既に拠点コードが存在している
     * @throws Exception
     */
    @Test
    public void testSaveInsertExistSiteCode() throws Exception {
        // テストに必要なデータを作成
        Site site = new Site();
        site.setSiteCd("SHJ");
        site.setName("Shinjuku");

        // Mock準備
        MockAbstractService.IS_PROJECT_ADMIN = true;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        new MockUp<SiteDaoImpl>() {
            @Mock int countCheck(SearchSiteCondition condition) {
                // 既に拠点コードが存在している
                return 1;
            }
        };

        try {
            service.save(site);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_SITE_CODE_ALREADY_EXISTS,
                actual.getMessageCode());
            assertEquals(1, actual.getMessageVars().length);
            assertEquals("SHJ", actual.getMessageVars()[0]);
        }
    }

    /**
     * 拠点情報保存処理を検証する. 更新
     * @throws Exception
     */
    @Test
    public void testSaveUpdate() throws Exception {
        // テストに必要なデータを作成
        Site site = createSite();
        User updateUser = new User();
        updateUser.setEmpNo("ZZA02");
        updateUser.setNameE("Tetsuo Aoki");
        site.setUpdatedBy(updateUser);

        List<Project> lp = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        lp.add(p);

        // Mock準備
        MockAbstractService.IS_PROJECT_ADMIN = true;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        MockAbstractService.ACCESSIBLE_PROJECTS = lp;
        new MockUp<SiteDaoImpl>() {
            @Mock Site findById(Long id) throws RecordNotFoundException {
                return site;
            }
            @Mock List<Site> find(SearchSiteCondition condition) {
                return new ArrayList<>();
            }
            @Mock int countCheck(SearchSiteCondition condition) {
                return 0;
            }
            @Mock int count(SearchSiteCondition condition) {
                return 0;
            }
            @Mock int update(Site s) {
                assertThat(s.getSiteCd(), is(site.getSiteCd()));
                assertThat(s.getName(), is(site.getName()));

                return 1;
            }
        };


        Long id = service.save(site);

        assertThat(id, is(site.getId()));
    }

    /**
     * 拠点情報保存処理を検証する. 更新、該当データ0件
     * @throws Exception
     */
    @Test
    public void testSaveUpdateNoData() throws Exception {
        // テストに必要なデータを作成
        Site site = createSite();
        User updateUser = new User();
        updateUser.setEmpNo("ZZA02");
        updateUser.setNameE("Tetsuo Aoki");
        site.setUpdatedBy(updateUser);

        List<Project> lp = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        lp.add(p);

        // Mock準備
        MockAbstractService.IS_PROJECT_ADMIN = true;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        MockAbstractService.ACCESSIBLE_PROJECTS = lp;
        new MockUp<SiteDaoImpl>() {
            @Mock Site findById(Long id) throws RecordNotFoundException {
                throw new RecordNotFoundException();
            }
            @Mock List<Site> find(SearchSiteCondition condition) {
                return new ArrayList<>();
            }
        };

        try {
            service.save(site);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.NO_DATA_FOUND, actual.getMessageCode());
        }
    }

    /**
     * 拠点情報保存処理を検証する. 更新、既に拠点コードが存在する
     * @throws Exception
     */
    @Test
    public void testSaveUpdateExistSiteCode() throws Exception {
        // テストに必要なデータを作成
        Site site = createSite();
        User updateUser = new User();
        updateUser.setEmpNo("ZZA02");
        updateUser.setNameE("Tetsuo Aoki");
        site.setUpdatedBy(updateUser);

        List<Project> lp = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        lp.add(p);

        // Mock準備
        MockAbstractService.IS_PROJECT_ADMIN = true;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        MockAbstractService.ACCESSIBLE_PROJECTS = lp;
        new MockUp<SiteDaoImpl>() {
            @Mock Site findById(Long id) throws RecordNotFoundException {
                return site;
            }
            @Mock int countCheck(SearchSiteCondition condition) {
                // 既に拠点コードが存在している
                return 1;
            }
            @Mock List<Site> find(SearchSiteCondition condition) {
                return new ArrayList<>();
            }
        };

        try {
            service.save(site);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_SITE_CODE_ALREADY_EXISTS,
                actual.getMessageCode());
            assertEquals(1, actual.getMessageVars().length);
            assertEquals("YOC", actual.getMessageVars()[0]);
        }
    }

    /**
     * 拠点情報保存処理を検証する. 更新、拠点情報のプロジェクトが現在選択中のプロジェクトではない
     * @throws Exception
     */
    @Test
    public void testSaveUpdateProjectDiff() throws Exception {
        // テストに必要なデータを作成
        Site site = createSite();
        User updateUser = new User();
        updateUser.setEmpNo("ZZA02");
        updateUser.setNameE("Tetsuo Aoki");
        site.setUpdatedBy(updateUser);


        List<Project> lp = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ2");
        lp.add(p);

        // Mock準備
        MockAbstractService.IS_PROJECT_ADMIN = true;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        MockAbstractService.ACCESSIBLE_PROJECTS = lp;
        new MockUp<SiteDaoImpl>() {
            @Mock Site findById(Long id) throws RecordNotFoundException {
                return site;
            }
            @Mock int countCheck(SearchSiteCondition condition) {
                return 0;
            }
            @Mock List<Site> find(SearchSiteCondition condition) {
                return new ArrayList<>();
            }
        };

        try {
            service.save(site);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_PROJECT_DIFF, actual
                .getMessageCode());
        }
    }

    /**
     * 拠点情報保存処理を検証する. 更新、拠点情報のプロジェクトが現在選択中のプロジェクトではない
     * @throws Exception
     */
    @Test
    public void testSaveUpdateStaleRecordException() throws Exception {
        // テストに必要なデータを作成
        Site site = createSite();
        User updateUser = new User();
        updateUser.setEmpNo("ZZA02");
        updateUser.setNameE("Tetsuo Aoki");
        site.setUpdatedBy(updateUser);

        List<Project> lp = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        lp.add(p);

        // Mock準備
        MockAbstractService.IS_PROJECT_ADMIN = true;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        MockAbstractService.ACCESSIBLE_PROJECTS = lp;
        new MockUp<SiteDaoImpl>() {
            @Mock Site findById(Long id) throws RecordNotFoundException {
                return site;
            }
            @Mock int countCheck(SearchSiteCondition condition) {
                return 0;
            }
            @Mock List<Site> find(SearchSiteCondition condition) {
                return new ArrayList<>();
            }
            @Mock int update(Site s) throws StaleRecordException {
                throw new StaleRecordException();
            }
        };

        try {
            service.save(site);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_SITE_ALREADY_UPDATED, actual
                .getMessageCode());
        }
    }

    /**
     * 活動単位削除処理を検証する.
     * @throws Exception
     */
    @Test
    public void testDelete() throws Exception {
        // テストに必要なデータを作成する
        Site site = new Site();
        site.setId(1L);
        site.setProjectId("PJ1");
        site.setProjectNameE("Test Project1");
        site.setSiteCd("YOC");
        site.setName("Yocohama");
        site.setCreatedBy(user);
        site.setUpdatedBy(user);
        site.setVersionNo(1L);
        site.setDeleteNo(0L);

        List<Project> projectList = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        projectList.add(p);

        // Mock準備
        MockAbstractService.IS_SYSTEM_ADMIN = true;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        MockAbstractService.ACCESSIBLE_PROJECTS = projectList;
        new MockUp<SiteDaoImpl>() {
            @Mock Site findById(Long id) throws RecordNotFoundException {
                return site;
            }
            @Mock int countCheck(SearchSiteCondition condition) {
                return 0;
            }
            @Mock List<Site> find(SearchSiteCondition condition) {
                return new ArrayList<>();
            }
            @Mock int delete(Site s) throws StaleRecordException {
                assertThat(s.getId(), is(site.getId()));
                return 1;
            }
        };
        new MockUp<CorresponGroupDaoImpl>() {
            @Mock List<CorresponGroup> find(SearchCorresponGroupCondition condition) {
                return new ArrayList<>();
            }
        };

        service.delete(site);
    }

    /**
     * 活動単位削除処理を検証する. 引数がnull
     * @throws Exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testDeleteArgumentNull() throws Exception {
        service.delete(null);
    }

    /**
     * 活動単位削除処理を検証する. 権限がない
     * @throws Exception
     */
    @Test
    public void testDeleteInvalidPermission() throws Exception {
        // テストに必要なデータを作成する
        Site site = new Site();
        site.setId(1L);
        site.setProjectId("PJ1");
        site.setProjectNameE("Test Project1");
        site.setSiteCd("YOC");
        site.setName("Yocohama");
        site.setCreatedBy(user);
        site.setUpdatedBy(user);
        site.setVersionNo(1L);
        site.setDeleteNo(0L);

        // 権限がない
        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN = false;

        try {
            service.delete(site);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT,
                actual.getMessageCode());
        }

    }

    /**
     * 活動単位削除処理を検証する. 該当データ0件
     * @throws Exception
     */
    @Test
    public void testDeleteNoData() throws Exception {
        // テストに必要なデータを作成する
        Site site = new Site();
        site.setId(1L);
        site.setProjectId("PJ1");
        site.setProjectNameE("Test Project1");
        site.setSiteCd("YOC");
        site.setName("Yocohama");
        site.setCreatedBy(user);
        site.setUpdatedBy(user);
        site.setVersionNo(1L);
        site.setDeleteNo(0L);

        List<Project> projectList = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        projectList.add(p);

        // Mock準備
        MockAbstractService.IS_SYSTEM_ADMIN = true;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        MockAbstractService.ACCESSIBLE_PROJECTS = projectList;
        new MockUp<SiteDaoImpl>() {
            @Mock Site findById(Long id) throws RecordNotFoundException {
                throw new RecordNotFoundException();
            }
        };

        try {
            service.delete(site);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.NO_DATA_FOUND, actual.getMessageCode());
        }

    }

    /**
     * 活動単位削除処理を検証する. 活動する部門が関連付けられている
     * @throws Exception
     */
    @Test
    public void testDeleteIsRelationDiscipline() throws Exception {
        // テストに必要なデータを作成する
        Site site = new Site();
        site.setId(1L);
        site.setProjectId("PJ1");
        site.setProjectNameE("Test Project1");
        site.setSiteCd("YOC");
        site.setName("Yocohama");
        site.setCreatedBy(user);
        site.setUpdatedBy(user);
        site.setVersionNo(1L);
        site.setDeleteNo(0L);

        List<CorresponGroup> corresponGroupList = new ArrayList<CorresponGroup>();
        corresponGroupList.add(new CorresponGroup());

        List<Project> projectList = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        projectList.add(p);

        // Mock準備
        MockAbstractService.IS_SYSTEM_ADMIN = true;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        MockAbstractService.ACCESSIBLE_PROJECTS = projectList;
        new MockUp<SiteDaoImpl>() {
            @Mock Site findById(Long id) throws RecordNotFoundException {
                return site;
            }
        };
        new MockUp<CorresponGroupDaoImpl>() {
            @Mock List<CorresponGroup> find(SearchCorresponGroupCondition condition) {
                return corresponGroupList;
            }
        };

        try {
            service.delete(site);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_SITE_ALREADY_RELATED_WITH_DISCIPLINE,
                actual.getMessageCode());
        }

    }

    /**
     * 活動単位削除処理を検証する. 拠点情報のプロジェクトが現在選択中のプロジェクトではない
     * @throws Exception
     */
    @Test
    public void testDeleteProjectDiff() throws Exception {
        // テストに必要なデータを作成する
        Site site = new Site();
        site.setId(1L);
        // プロジェクトが違う
        site.setProjectId("PJ2");
        site.setProjectNameE("Test Project1");
        site.setSiteCd("YOC");
        site.setName("Yocohama");
        site.setCreatedBy(user);
        site.setUpdatedBy(user);
        site.setVersionNo(1L);
        site.setDeleteNo(0L);

        List<Project> projectList = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        projectList.add(p);

        // Mock準備
        MockAbstractService.IS_SYSTEM_ADMIN = true;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        MockAbstractService.ACCESSIBLE_PROJECTS = projectList;
        new MockUp<SiteDaoImpl>() {
            @Mock Site findById(Long id) throws RecordNotFoundException {
                return site;
            }
        };
        new MockUp<CorresponGroupDaoImpl>() {
            @Mock List<CorresponGroup> find(SearchCorresponGroupCondition condition) {
                return new ArrayList<>();
            }
        };


        try {
            service.delete(site);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_PROJECT_DIFF, actual
                .getMessageCode());
        }

    }

    /**
     * 活動単位削除処理を検証する. StaleRecordExceptionが発生
     * @throws Exception
     */
    @Test
    public void testDeleteStaleRecordException() throws Exception {
        // テストに必要なデータを作成する
        Site site = new Site();
        site.setId(1L);
        site.setProjectId("PJ1");
        site.setProjectNameE("Test Project1");
        site.setSiteCd("YOC");
        site.setName("Yocohama");
        site.setCreatedBy(user);
        site.setUpdatedBy(user);
        site.setVersionNo(1L);
        site.setDeleteNo(0L);

        List<Project> projectList = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        projectList.add(p);

        // Mock準備
        MockAbstractService.IS_SYSTEM_ADMIN = true;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        MockAbstractService.ACCESSIBLE_PROJECTS = projectList;
        new MockUp<SiteDaoImpl>() {
            @Mock Site findById(Long id) throws RecordNotFoundException {
                return site;
            }
            @Mock int delete(Site s) throws StaleRecordException {
                throw new StaleRecordException();
            }
        };
        new MockUp<CorresponGroupDaoImpl>() {
            @Mock List<CorresponGroup> find(SearchCorresponGroupCondition condition) {
                return new ArrayList<>();
            }
        };

        try {
            service.delete(site);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_SITE_ALREADY_UPDATED, actual
                .getMessageCode());
        }

    }
    private List<Site> createSiteList() {
        List<Site> lstSite = new ArrayList<Site>();

        Site site = new Site();
        site.setId(1L);
        site.setProjectId("PJ1");
        site.setProjectNameE("Test Project1");
        site.setSiteCd("YOC");
        site.setName("Yocohama");
        site.setCreatedBy(user);
        site.setUpdatedBy(user);
        site.setVersionNo(1L);
        site.setDeleteNo(0L);

        lstSite.add(site);

        site = new Site();
        site.setId(2L);
        site.setProjectId("PJ1");
        site.setProjectNameE("Test Project1");
        site.setSiteCd("SJK");
        site.setName("Shinjuku");
        site.setCreatedBy(user);
        site.setUpdatedBy(user);
        site.setVersionNo(1L);
        site.setDeleteNo(0L);

        lstSite.add(site);

        site = new Site();
        site.setId(3L);
        site.setProjectId("PJ1");
        site.setProjectNameE("Test Project1");
        site.setSiteCd("JGI");
        site.setName("Jogi");
        site.setCreatedBy(user);
        site.setUpdatedBy(user);
        site.setVersionNo(1L);
        site.setDeleteNo(0L);

        lstSite.add(site);
        return lstSite;
    }

    /**
     * 拠点オブジェクトを作成する.
     * @return 拠点
     */
    private Site createSite() {
        Site site = new Site();
        site.setId(1L);
        site.setProjectId("PJ1");
        site.setProjectNameE("Test Project1");
        site.setSiteCd("YOC");
        site.setName("Yocohama");
        site.setCreatedBy(user);
        site.setUpdatedBy(user);
        site.setVersionNo(1L);
        site.setDeleteNo(0L);
        return site;
    }
}
