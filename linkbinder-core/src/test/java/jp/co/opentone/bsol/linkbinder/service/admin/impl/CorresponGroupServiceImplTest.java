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
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.test.AbstractTestCase;
import jp.co.opentone.bsol.linkbinder.dao.impl.CorresponGroupDaoImpl;
import jp.co.opentone.bsol.linkbinder.dao.impl.CorresponGroupUserDaoImpl;
import jp.co.opentone.bsol.linkbinder.dao.impl.DisciplineDaoImpl;
import jp.co.opentone.bsol.linkbinder.dao.impl.ProjectUserProfileDaoImpl;
import jp.co.opentone.bsol.linkbinder.dao.impl.SiteDaoImpl;
import jp.co.opentone.bsol.linkbinder.dao.impl.UserDaoImpl;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroup;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroupUser;
import jp.co.opentone.bsol.linkbinder.dto.Discipline;
import jp.co.opentone.bsol.linkbinder.dto.Project;
import jp.co.opentone.bsol.linkbinder.dto.ProjectUser;
import jp.co.opentone.bsol.linkbinder.dto.ProjectUserProfile;
import jp.co.opentone.bsol.linkbinder.dto.SearchCorresponGroupResult;
import jp.co.opentone.bsol.linkbinder.dto.Site;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponGroupCondition;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchProjectUserProfileCondition;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchUserCondition;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.service.MockAbstractService;
import jp.co.opentone.bsol.linkbinder.service.admin.CorresponGroupService;
import mockit.Mock;
import mockit.MockUp;

/**
 * {@link CorresponGroupServiceImpl}のテストケース.
 * @author opentone
 */
public class CorresponGroupServiceImplTest extends AbstractTestCase {

    /**
     * テスト対象.
     */
    @Resource
    private CorresponGroupService service;

    /**
     * ユーザー
     */
    private User user = new User();

    @BeforeClass
    public static void testSetUp() {
        new MockAbstractService();
    }

    @AfterClass
    public static void testTeardown() {
        // 差し換えたMockをクリアする
        new MockAbstractService().tearDown();
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
     * {@link CorresponGroupService#searchPagingList}のテストケース.
     * @throws Exception
     */
    @Test
    public void testSearchPagingList() throws Exception {
//    public <T extends CorresponGroupDao> void testSearchPagingList() throws Exception {
        // テストに必要なデータを作成
        SearchCorresponGroupCondition condition = new SearchCorresponGroupCondition();
        condition.setSiteId(1L);
        condition.setProjectId("PJ1");
        condition.setPageNo(1);
        condition.setPageRowNum(10);

        // Mock準備
        List<CorresponGroup> lcg = createCorresponGroupList();
        MockAbstractService.IS_PROJECT_ADMIN = true;
        new MockUp<CorresponGroupDaoImpl>() {
//        new MockUp<T>() {
            @Mock int countCorresponGroup(SearchCorresponGroupCondition condition) {
                return lcg.size();
            }
            @Mock List<CorresponGroup> find(SearchCorresponGroupCondition condition) {
                return lcg;
            }
        };

        SearchCorresponGroupResult result = service.searchPagingList(condition);

        assertEquals(lcg.toString(), result.getCorresponGroupList().toString());
        assertEquals(lcg.size(), result.getCount());
    }

    /**
     * {@link CorresponGroupService#searchPagingList}のテストケース. 権限がない
     * @throws Exception
     */
    @Test
    public void testSearchPagingListInvalidPermission() throws Exception {
        // テストに必要なデータを作成
        SearchCorresponGroupCondition condition = new SearchCorresponGroupCondition();
        condition.setSiteId(1L);
        condition.setProjectId("PJ1");
        condition.setPageNo(1);
        condition.setPageRowNum(10);

        // Mock準備
        List<CorresponGroup> lcg = createCorresponGroupList();
        List<Discipline> ld = createDisciplineList();
        new MockUp<CorresponGroupDaoImpl>() {
            @Mock List<CorresponGroup> find(SearchCorresponGroupCondition condition) {
                return lcg;
            }
        };
        new MockUp<DisciplineDaoImpl>() {
            @Mock List<Discipline> findNotExistCorresponGroup(String projectId, Long siteId) {
                return ld;
            }
        };
        // 権限がない
        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        MockAbstractService.IS_ANY_GROUP_ADMIN = false;

        try {
            service.searchPagingList(condition);
            fail("例外が発生しない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT,
                actual.getMessageCode());
        }
    }

    /**
     * {@link CorresponGroupService#searchPagingList}のテストケース. 該当データが存在しない
     * @throws Exception
     */
    @Test
    public void testSearchPagingListNoData() throws Exception {
        // テストに必要なデータを作成
        SearchCorresponGroupCondition condition = new SearchCorresponGroupCondition();
        condition.setSiteId(1L);
        condition.setProjectId("PJ1");
        condition.setPageNo(1);
        condition.setPageRowNum(10);

        // Mock準備
        List<CorresponGroup> lcg = createCorresponGroupList();
        List<Discipline> ld = createDisciplineList();
        new MockUp<CorresponGroupDaoImpl>() {
            @Mock List<CorresponGroup> find(SearchCorresponGroupCondition condition) {
                return lcg;
            }
            @Mock int countCorresponGroup(SearchCorresponGroupCondition condition) {
                return 0;
            }
        };
        new MockUp<DisciplineDaoImpl>() {
            @Mock List<Discipline> findNotExistCorresponGroup(String projectId, Long siteId) {
                return ld;
            }
        };
        MockAbstractService.IS_PROJECT_ADMIN = true;

        try {
            service.searchPagingList(condition);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.NO_DATA_FOUND, actual.getMessageCode());
        }
    }

    /**
     * {@link CorresponGroupService#searchPagingList}のテストケース. ページ指定をした際にデータがない
     * @throws Exception
     */
    @Test
    public void testSearchPagingListNoPageData() throws Exception {
        // テストに必要なデータを作成
        SearchCorresponGroupCondition condition = new SearchCorresponGroupCondition();
        condition.setSiteId(1L);
        condition.setProjectId("PJ1");
        condition.setPageNo(1);
        condition.setPageRowNum(10);

        // Mock準備
        List<Discipline> ld = createDisciplineList();
        new MockUp<CorresponGroupDaoImpl>() {
            @Mock List<CorresponGroup> find(SearchCorresponGroupCondition condition) {
                return new ArrayList<CorresponGroup>();
            }
            @Mock int countCorresponGroup(SearchCorresponGroupCondition condition) {
                return 1;
            }
        };
        new MockUp<DisciplineDaoImpl>() {
            @Mock List<Discipline> findNotExistCorresponGroup(String projectId, Long siteId) {
                return ld;
            }
        };
        MockAbstractService.IS_PROJECT_ADMIN = true;

        try {
            service.searchPagingList(condition);
            fail("例外が発生しない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.NO_PAGE_FOUND, actual.getMessageCode());
        }
    }

    /**
     * 指定された活動単位一覧をExcel形式に変換して返す処理を検証
     */
    @Test
    public void testGenerateExcel() throws Exception {
        // 結果レコード
        List<Object> expected = new ArrayList<Object>();
        /* ヘッダ */
        List<Object> line = new ArrayList<Object>();
        line.add("ID");
        line.add("Discipline");
        line.add("Group");
        expected.add(line);

        /* 1行目 */
        line = new ArrayList<Object>();
        line.add(new Integer(1));
        line.add("IT : It");
        line.add("YOC:IT");
        expected.add(line);

        /* 2行目 */
        line = new ArrayList<Object>();
        line.add(new Integer(2));
        line.add("PIPING : Piping");
        line.add("YOC:PIPING");
        expected.add(line);

        /* 3行目 */
        line = new ArrayList<Object>();
        line.add(new Integer(3));
        line.add("BUILDING : Building");
        line.add("YOC:BUILDING");
        expected.add(line);

        // マスタ管理の為、リストを作成する際にnullを渡す。
        byte[] actual = service.generateExcel(createCorresponGroupList());

        // 作成したExcelを確認
        assertExcel(1, "CorresponGroupIndex", 3, expected, actual);
    }

    /**
     * 指定された活動単位一覧をExcel形式に変換して返す処理を検証 引数がnull
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGenerateExcelArgumentNull() throws Exception {
        service.generateExcel(null);
    }

    /**
     * 活動単位を追加する処理を検証
     * @throws Exception
     */
    @Test
    public void testAdd() throws Exception {
        // テストに必要なデータを作成
        Site site = new Site();
        site.setId(5L);
        site.setSiteCd("YMA");
        site.setProjectId("PJ1");
        site.setName("Yamanote");
        site.setCreatedBy(user);
        site.setUpdatedBy(user);
        site.setVersionNo(1L);
        site.setDeleteNo(0L);

        Discipline dis = new Discipline();
        dis.setId(7L);
        dis.setProjectId("PJ1");
        dis.setDisciplineCd("PIP");
        dis.setName("Pip");
        dis.setCreatedBy(user);
        dis.setUpdatedBy(user);
        dis.setVersionNo(1L);
        dis.setDeleteNo(0L);

        // Mock準備
        MockAbstractService.IS_PROJECT_ADMIN = true;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        new MockUp<CorresponGroupDaoImpl>() {
            @Mock List<CorresponGroup> find(SearchCorresponGroupCondition condition) {
                return new ArrayList<CorresponGroup>();
            }
            @Mock Long create(CorresponGroup cg) {
                assertNull(cg.getId());
                assertEquals(site.getId(), cg.getSite().getId());
                assertEquals(dis.getId(), cg.getDiscipline().getId());
                assertEquals(site.getSiteCd() + ":" + dis.getDisciplineCd(), cg.getName());
                assertEquals(user.toString(), cg.getCreatedBy().toString());
                assertEquals(user.toString(), cg.getUpdatedBy().toString());
                assertNull(cg.getCreatedAt());
                assertNull(cg.getUpdatedAt());
                assertNull(cg.getVersionNo());
                assertNull(cg.getDeleteNo());
                return 9L;
            }
        };
        new MockUp<SiteDaoImpl>() {
            @Mock Site findById(Long id) throws RecordNotFoundException {
                return site;
            }
        };
        new MockUp<DisciplineDaoImpl>() {
            @Mock Discipline findById(Long id) throws RecordNotFoundException {
                return dis;
            }
        };


        // テスト実施
        Long id = service.add(site, dis);

        assertNotNull(id);
        assertEquals(String.valueOf(9L), String.valueOf(id));
    }

    /**
     * 活動単位を追加する処理を検証 第一引数がnull
     * @throws Exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddArgument1Null() throws Exception {
        service.add(null, new Discipline());
    }

    /**
     * 活動単位を追加する処理を検証 第二引数がnull
     * @throws Exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddArgument2Null() throws Exception {
        service.add(new Site(), null);
    }

    /**
     * 活動単位を追加する処理を検証. 権限がない場合
     * @throws Exception
     */
    @Test
    public void testAddInvalidPermission() throws Exception {
        // テストに必要なデータを作成
        Site site = new Site();
        site.setId(5L);
        site.setSiteCd("YMA");
        site.setProjectId("PJ1");
        site.setName("Yamanote");
        site.setCreatedBy(user);
        site.setUpdatedBy(user);
        site.setVersionNo(1L);
        site.setDeleteNo(0L);

        Discipline dis = new Discipline();
        dis.setId(7L);
        dis.setProjectId("PJ1");
        dis.setDisciplineCd("PIP");
        dis.setName("Pip");
        dis.setCreatedBy(user);
        dis.setUpdatedBy(user);
        dis.setVersionNo(1L);
        dis.setDeleteNo(0L);

        // Mock準備
        // 権限がない
        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        new MockUp<CorresponGroupDaoImpl>() {
            @Mock List<CorresponGroup> find(SearchCorresponGroupCondition condition) {
                return new ArrayList<CorresponGroup>();
            }
            @Mock Long create(CorresponGroup cg) {
                return 9L;
            }
        };
        new MockUp<SiteDaoImpl>() {
            @Mock Site findById(Long id) throws RecordNotFoundException {
                return site;
            }
        };
        new MockUp<DisciplineDaoImpl>() {
            @Mock Discipline findById(Long id) throws RecordNotFoundException {
                return dis;
            }
        };


        try {
            service.add(site, dis);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT,
                actual.getMessageCode());
        }
    }

    /**
     * 活動単位を追加する処理を検証 既に拠点ID、部門IDが登録されている場合
     * @throws Exception
     */
    @Test
    public void testAddExistSiteIdDisciplineId() throws Exception {
        // テストに必要なデータを作成
        Site site = new Site();
        site.setId(5L);
        site.setSiteCd("YMA");
        site.setProjectId("PJ1");
        site.setName("Yamanote");
        site.setCreatedBy(user);
        site.setUpdatedBy(user);
        site.setVersionNo(1L);
        site.setDeleteNo(0L);

        Discipline dis = new Discipline();
        dis.setId(7L);
        dis.setProjectId("PJ1");
        dis.setDisciplineCd("PIP");
        dis.setName("Pip");
        dis.setCreatedBy(user);
        dis.setUpdatedBy(user);
        dis.setVersionNo(1L);
        dis.setDeleteNo(0L);

        // テスト準備
        MockAbstractService.IS_PROJECT_ADMIN = true;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";

        List<CorresponGroup> lcg = new ArrayList<CorresponGroup>();
        CorresponGroup findCg = new CorresponGroup();
        lcg.add(findCg);
        new MockUp<CorresponGroupDaoImpl>() {
            @Mock List<CorresponGroup> find(SearchCorresponGroupCondition condition) {
                return lcg;
            }
            @Mock Long create(CorresponGroup cg) {
                return 9L;
            }
        };
        new MockUp<SiteDaoImpl>() {
            @Mock Site findById(Long id) throws RecordNotFoundException {
                return site;
            }
        };
        new MockUp<DisciplineDaoImpl>() {
            @Mock Discipline findById(Long id) throws RecordNotFoundException {
                return dis;
            }
        };

        try {
            service.add(site, dis);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_SITE_DISCIPLINE_ALREADY_EXISTS,
                actual.getMessageCode());
            assertEquals(1, actual.getMessageVars().length);
            assertEquals("YMA:PIP", actual.getMessageVars()[0]);
        }
    }

    /**
     * 活動単位を追加する処理を検証 指定した拠点情報がマスタに存在しない場合
     * @throws Exception
     */
    @Test
    public void testAddNotEmptySite() throws Exception {
        // テストに必要なデータを作成
        Site site = new Site();
        site.setId(5L);
        site.setSiteCd("YMA");
        site.setProjectId("PJ1");
        site.setName("Yamanote");
        site.setCreatedBy(user);
        site.setUpdatedBy(user);
        site.setVersionNo(1L);
        site.setDeleteNo(0L);

        Discipline dis = new Discipline();
        dis.setId(7L);
        dis.setProjectId("PJ1");
        dis.setDisciplineCd("PIP");
        dis.setName("Pip");
        dis.setCreatedBy(user);
        dis.setUpdatedBy(user);
        dis.setVersionNo(1L);
        dis.setDeleteNo(0L);

        // Mock準備
        MockAbstractService.IS_PROJECT_ADMIN = true;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        new MockUp<CorresponGroupDaoImpl>() {
            @Mock List<CorresponGroup> find(SearchCorresponGroupCondition condition) {
                return new ArrayList<>();
            }
            @Mock Long create(CorresponGroup cg) {
                return 9L;
            }
        };
        new MockUp<SiteDaoImpl>() {
            @Mock Site findById(Long id) throws RecordNotFoundException {
                // レコードが見つからない
                throw new RecordNotFoundException();
            }
        };
        new MockUp<DisciplineDaoImpl>() {
            @Mock Discipline findById(Long id) throws RecordNotFoundException {
                return dis;
            }
        };

        try {
            service.add(site, dis);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.NO_DATA_FOUND, actual.getMessageCode());
        }
    }

    /**
     * 活動単位を追加する処理を検証 選択した拠点が同一プロジェクトに属していない
     * @throws Exception
     */
    @Test
    public void testAddNotEmptySiteDiffProject() throws Exception {
        // テストに必要なデータを作成
        Site site = new Site();
        site.setId(5L);
        site.setSiteCd("YMA");
        site.setProjectId("PJ2");
        site.setName("Yamanote");
        site.setCreatedBy(user);
        site.setUpdatedBy(user);
        site.setVersionNo(1L);
        site.setDeleteNo(0L);

        Discipline dis = new Discipline();
        dis.setId(7L);
        dis.setProjectId("PJ1");
        dis.setDisciplineCd("PIP");
        dis.setName("Pip");
        dis.setCreatedBy(user);
        dis.setUpdatedBy(user);
        dis.setVersionNo(1L);
        dis.setDeleteNo(0L);

        // Mock準備
        MockAbstractService.IS_PROJECT_ADMIN = true;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        new MockUp<CorresponGroupDaoImpl>() {
            @Mock List<CorresponGroup> find(SearchCorresponGroupCondition condition) {
                return new ArrayList<>();
            }
            @Mock Long create(CorresponGroup cg) {
                return 9L;
            }
        };
        new MockUp<SiteDaoImpl>() {
            @Mock Site findById(Long id) throws RecordNotFoundException {
                return site;
            }
        };
        new MockUp<DisciplineDaoImpl>() {
            @Mock Discipline findById(Long id) throws RecordNotFoundException {
                return dis;
            }
        };

        try {
            service.add(site, dis);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_PROJECT_DIFF, actual
                .getMessageCode());
        }
    }

    /**
     * 活動単位を追加する処理を検証 選択した部門がマスタに存在しない
     * @throws Exception
     */
    @Test
    public void testAddNotEmptyDiscipline() throws Exception {
        // テストに必要なデータを作成
        Site site = new Site();
        site.setId(5L);
        site.setSiteCd("YMA");
        site.setProjectId("PJ1");
        site.setName("Yamanote");
        site.setCreatedBy(user);
        site.setUpdatedBy(user);
        site.setVersionNo(1L);
        site.setDeleteNo(0L);

        Discipline dis = new Discipline();
        dis.setId(7L);
        dis.setProjectId("PJ1");
        dis.setDisciplineCd("PIP");
        dis.setName("Pip");
        dis.setCreatedBy(user);
        dis.setUpdatedBy(user);
        dis.setVersionNo(1L);
        dis.setDeleteNo(0L);

        // Mock準備
        MockAbstractService.IS_PROJECT_ADMIN = true;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        new MockUp<CorresponGroupDaoImpl>() {
            @Mock List<CorresponGroup> find(SearchCorresponGroupCondition condition) {
                return new ArrayList<>();
            }
            @Mock Long create(CorresponGroup cg) {
                return 9L;
            }
        };
        new MockUp<SiteDaoImpl>() {
            @Mock Site findById(Long id) throws RecordNotFoundException {
                return site;
            }
        };
        new MockUp<DisciplineDaoImpl>() {
            @Mock Discipline findById(Long id) throws RecordNotFoundException {
                // レコードが見つからない
                throw new RecordNotFoundException();
            }
        };

        try {
            service.add(site, dis);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.NO_DATA_FOUND, actual.getMessageCode());
        }
    }

    /**
     * 活動単位を追加する処理を検証 選択した拠点が同一プロジェクトに属していない
     * @throws Exception
     */
    @Test
    public void testAddNotEmptyDisciplineDiffProject() throws Exception {
        // テストに必要なデータを作成
        MockAbstractService.IS_PROJECT_ADMIN = true;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";

        Site site = new Site();
        site.setId(5L);
        site.setSiteCd("YMA");
        site.setProjectId("PJ2");
        site.setName("Yamanote");
        site.setCreatedBy(user);
        site.setUpdatedBy(user);
        site.setVersionNo(1L);
        site.setDeleteNo(0L);

        Discipline dis = new Discipline();
        dis.setId(7L);
        dis.setProjectId("PJ2");
        dis.setDisciplineCd("PIP");
        dis.setName("Pip");
        dis.setCreatedBy(user);
        dis.setUpdatedBy(user);
        dis.setVersionNo(1L);
        dis.setDeleteNo(0L);

        // Mock準備
        new MockUp<CorresponGroupDaoImpl>() {
            @Mock List<CorresponGroup> find(SearchCorresponGroupCondition condition) {
                return new ArrayList<>();
            }
            @Mock Long create(CorresponGroup cg) {
                return 9L;
            }
        };
        new MockUp<SiteDaoImpl>() {
            @Mock Site findById(Long id) throws RecordNotFoundException {
                return site;
            }
        };
        new MockUp<DisciplineDaoImpl>() {
            @Mock Discipline findById(Long id) throws RecordNotFoundException {
                return dis;
            }
        };

        try {
            service.add(site, dis);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_PROJECT_DIFF, actual
                .getMessageCode());
        }
    }

    private CorresponGroup createCorresponGroupForSave() {
        CorresponGroup cg = new CorresponGroup();
        cg.setId(1L);
        cg.setProjectId("PJ1");

        Site site = new Site();
        site.setId(1L);
        site.setSiteCd("YOC");
        site.setName("Yocohama");
        site.setProjectId("PJ1");
        site.setCreatedBy(user);
        site.setUpdatedBy(user);
        site.setVersionNo(1L);
        site.setDeleteNo(0L);

        cg.setSite(site);

        Discipline dis = new Discipline();
        dis.setId(1L);
        dis.setDisciplineCd("PIPING");
        dis.setName("Piping");
        dis.setProjectId("PJ1");
        dis.setCreatedBy(user);
        dis.setUpdatedBy(user);
        dis.setVersionNo(1L);
        dis.setDeleteNo(0L);

        cg.setDiscipline(dis);
        cg.setName("YOC:PIPING");
        cg.setCreatedBy(user);
        cg.setUpdatedBy(user);
        cg.setVersionNo(1L);
        cg.setDeleteNo(0L);

        return cg;
    }

    private List<User> createUserListForSave() {
        List<User> users = new ArrayList<User>();

        User corresponGroupUser = new User();
        corresponGroupUser.setEmpNo("ZZA02");
        corresponGroupUser.setNameE("Tetsuo Aoki");
        corresponGroupUser.setSecurityLevel("40");

        users.add(corresponGroupUser);

        corresponGroupUser = new User();
        corresponGroupUser.setEmpNo("ZZA03");
        corresponGroupUser.setNameE("Atsushi Isida");
        corresponGroupUser.setSecurityLevel("40");

        users.add(corresponGroupUser);

        corresponGroupUser = new User();
        corresponGroupUser.setEmpNo("ZZA04");
        corresponGroupUser.setNameE("Tomoko Okada");
        corresponGroupUser.setSecurityLevel("30");

        users.add(corresponGroupUser);

        return users;
    }

    private List<ProjectUser> createProjectUserListForSave(List<User> users) {
        List<ProjectUser> lpu = new ArrayList<ProjectUser>();
        for (User u : users) {
            ProjectUser pu = new ProjectUser();
            pu.setProjectId("PJ1");
            pu.setUser(u);

            lpu.add(pu);
        }
        return lpu;
    }

    /**
     * 活動単位、メンバーを保存する処理を検証
     * @throws Exception
     */
    @Test
    public void testSave() throws Exception {
        // テストに必要なデータを作成
        CorresponGroup cg = createCorresponGroupForSave();
        List<User> users = createUserListForSave();
        List<ProjectUser> lpu = createProjectUserListForSave(users);

        List<Project> lp = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        lp.add(p);

        // Mock準備
        MockAbstractService.IS_SYSTEM_ADMIN = true;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        MockAbstractService.ACCESSIBLE_PROJECTS = lp;

        new MockUp<CorresponGroupDaoImpl>() {
            @Mock CorresponGroup findById(Long id) throws RecordNotFoundException {
                return new CorresponGroup();
            }
            @Mock List<CorresponGroup> find(SearchCorresponGroupCondition condition) {
                return new ArrayList<>();
            }
            @Mock Long create(CorresponGroup cg) {
                return 9L;
            }
            @Mock CorresponGroup findByIdForUpdate(Long id) throws RecordNotFoundException {
                return cg;
            }
        };
        new MockUp<SiteDaoImpl>() {
            @Mock Site findById(Long id) throws RecordNotFoundException {
                return cg.getSite();
            }
        };
        new MockUp<DisciplineDaoImpl>() {
            @Mock Discipline findById(Long id) throws RecordNotFoundException {
                return cg.getDiscipline();
            }
        };
        new MockUp<UserDaoImpl>() {
            @Mock User findByEmpNo(String empNo) throws RecordNotFoundException {
                return new User();
            }

            @Mock List<ProjectUser> findProjectUser(SearchUserCondition condition) {
                return lpu;
            }
        };
        new MockUp<ProjectUserProfileDaoImpl>() {
            @Mock List<ProjectUserProfile> findList(SearchProjectUserProfileCondition condition) {
                return createProjectUserProfileList();
            }
        };
        new MockUp<CorresponGroupUserDaoImpl>() {
            @Mock Integer deleteByCorresponGroupId(Long corresponGroupId, User updateUser) {
                return 3;
            }

            int index = 0;
            @Mock Long create(CorresponGroupUser cu) {
                // 期待通りの値が設定されているか検証
                assertEquals(cg.toString(),
                             cu.getCorresponGroup().toString());
                assertEquals(users.get(index).toString(),
                             cu.getUser().toString());
                assertEquals(user, cu.getCreatedBy());
                assertEquals(user, cu.getUpdatedBy());
                assertNull(cu.getDeleteNo());

                index++;
                return 9L;
            }
        };


        // 保存
        service.save(cg, users);
    }

    /**
     * 活動単位、メンバーを保存する処理を検証. 第一引数がnull
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSaveArgument1Null() throws Exception {
        service.save(null, new ArrayList<User>());
    }

    /**
     * 活動単位、メンバーを保存する処理を検証. 第二引数がnull
     * @throws Exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSaveArgument2Null() throws Exception {
        service.save(new CorresponGroup(), null);
    }

    /**
     * 活動単位、メンバーを保存する処理を検証. 権限がない
     * @throws Exception
     */
    @Test
    public void testSaveInvalidPermission() throws Exception {
        // テストに必要なデータを作成
        CorresponGroup cg = createCorresponGroupForSave();
        List<User> users = createUserListForSave();
        List<ProjectUser> lpu = createProjectUserListForSave(users);
        List<Project> lp = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        lp.add(p);


        // 権限がない
        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        MockAbstractService.IS_ANY_GROUP_ADMIN = false;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        MockAbstractService.ACCESSIBLE_PROJECTS = lp;
        new MockUp<CorresponGroupDaoImpl>() {
            @Mock CorresponGroup findById(Long id) throws RecordNotFoundException {
                return new CorresponGroup();
            }
            @Mock List<CorresponGroup> find(SearchCorresponGroupCondition condition) {
                return new ArrayList<>();
            }
            @Mock Long create(CorresponGroup cg) {
                return 9L;
            }
            @Mock CorresponGroup findByIdForUpdate(Long id) throws RecordNotFoundException {
                return cg;
            }
        };
        new MockUp<SiteDaoImpl>() {
            @Mock Site findById(Long id) throws RecordNotFoundException {
                return cg.getSite();
            }
        };
        new MockUp<DisciplineDaoImpl>() {
            @Mock Discipline findById(Long id) throws RecordNotFoundException {
                return cg.getDiscipline();
            }
        };
        new MockUp<UserDaoImpl>() {
            @Mock User findByEmpNo(String empNo) throws RecordNotFoundException {
                return new User();
            }

            @Mock List<ProjectUser> findProjectUser(SearchUserCondition condition) {
                return lpu;
            }
        };
        new MockUp<ProjectUserProfileDaoImpl>() {
            @Mock List<ProjectUserProfile> findList(SearchProjectUserProfileCondition condition) {
                return createProjectUserProfileList();
            }
        };
        new MockUp<CorresponGroupUserDaoImpl>() {
            @Mock Integer deleteByCorresponGroupId(Long corresponGroupId, User updateUser) {
                return 3;
            }
            @Mock Long create(CorresponGroupUser cu) {
                return 9L;
            }
        };


        try {
            service.save(cg, users);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT,
                actual.getMessageCode());
        }
    }

    /**
     * 活動単位、メンバーを保存する処理を検証. GroupAdminチェック GroupAdminの場合
     * @throws Exeption
     */
    @Test
    public void testSaveGroupAdmin() throws Exception {
        // テストに必要なデータを作成
        CorresponGroup cg = createCorresponGroupForSave();
        List<User> users = createUserListForSave();
        // 自身をGroup Adminとして追加
        user.setSecurityLevel("30");
        users.add(user);

        List<ProjectUser> lpu = createProjectUserListForSave(users);
        List<Project> lp = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        lp.add(p);

        List<CorresponGroupUser> lstCgu = new ArrayList<CorresponGroupUser>();
        CorresponGroupUser cgu = new CorresponGroupUser();

        cgu.setUser(user);
        cgu.setCorresponGroup(cg);
        cgu.setSecurityLevel("30");
        lstCgu.add(cgu);

        cgu = new CorresponGroupUser();
        cgu.setUser(users.get(1));
        cgu.setCorresponGroup(cg);
        cgu.setSecurityLevel("40");
        lstCgu.add(cgu);

        cgu = new CorresponGroupUser();
        cgu.setUser(users.get(2));
        cgu.setCorresponGroup(cg);
        cgu.setSecurityLevel("40");
        lstCgu.add(cgu);

        MockAbstractService.IS_ANY_GROUP_ADMIN = true;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        MockAbstractService.ACCESSIBLE_PROJECTS = lp;
        new MockUp<CorresponGroupDaoImpl>() {
            @Mock CorresponGroup findById(Long id) throws RecordNotFoundException {
                return new CorresponGroup();
            }
            @Mock List<CorresponGroup> find(SearchCorresponGroupCondition condition) {
                return new ArrayList<>();
            }
            @Mock Long create(CorresponGroup cg) {
                return 9L;
            }
            @Mock CorresponGroup findByIdForUpdate(Long id) throws RecordNotFoundException {
                return cg;
            }
        };
        new MockUp<SiteDaoImpl>() {
            @Mock Site findById(Long id) throws RecordNotFoundException {
                return cg.getSite();
            }
        };
        new MockUp<DisciplineDaoImpl>() {
            @Mock Discipline findById(Long id) throws RecordNotFoundException {
                return cg.getDiscipline();
            }
        };
        new MockUp<UserDaoImpl>() {
            @Mock User findByEmpNo(String empNo) throws RecordNotFoundException {
                return new User();
            }

            @Mock List<ProjectUser> findProjectUser(SearchUserCondition condition) {
                return lpu;
            }
        };
        new MockUp<ProjectUserProfileDaoImpl>() {
            @Mock List<ProjectUserProfile> findList(SearchProjectUserProfileCondition condition) {
                return createProjectUserProfileList();
            }
        };
        new MockUp<CorresponGroupUserDaoImpl>() {
            @Mock List<CorresponGroupUser> findByCorresponGroupId(Long corresponGroupId) {
                return lstCgu;
            }
;
            @Mock Integer deleteByCorresponGroupId(Long corresponGroupId, User updateUser) {
                return 3;
            }
            int index = 0;
            @Mock Long create(CorresponGroupUser cu) {
                // 期待通りの値が設定されているか検証
                assertEquals(cg.toString(),
                             cu.getCorresponGroup().toString());
                assertEquals(users.get(index).toString(),
                             cu.getUser().toString());
                assertEquals(user, cu.getCreatedBy());
                assertEquals(user, cu.getUpdatedBy());
                assertNull(cu.getDeleteNo());

                index++;
                return 9L;
            }
        };


        service.save(cg, users);
    }

    /**
     * 活動単位、メンバーを保存する処理を検証. GroupAdminチェック
     * 更新する活動単位は自分がGroupAdminとして所属していない
     * @throws Exeption
     */
    @Test
    public void testSaveNotExistGroupAdminMySelf() throws Exception {
        // テストに必要なデータを作成
        CorresponGroup cg = createCorresponGroupForSave();
        List<User> users = createUserListForSave();

        List<ProjectUser> lpu = createProjectUserListForSave(users);
        List<Project> lp = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        lp.add(p);

        List<CorresponGroupUser> lstCgu = new ArrayList<CorresponGroupUser>();
        CorresponGroupUser cgu = new CorresponGroupUser();

        cgu.setUser(user);
        cgu.setCorresponGroup(cg);
        cgu.setSecurityLevel("30");
        lstCgu.add(cgu);

        cgu = new CorresponGroupUser();
        cgu.setUser(users.get(1));
        cgu.setCorresponGroup(cg);
        cgu.setSecurityLevel("40");
        lstCgu.add(cgu);

        cgu = new CorresponGroupUser();
        cgu.setUser(users.get(2));
        cgu.setCorresponGroup(cg);
        cgu.setSecurityLevel("40");
        lstCgu.add(cgu);

        MockAbstractService.IS_ANY_GROUP_ADMIN = true;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        MockAbstractService.ACCESSIBLE_PROJECTS = lp;
        new MockUp<CorresponGroupDaoImpl>() {
            @Mock CorresponGroup findById(Long id) throws RecordNotFoundException {
                return new CorresponGroup();
            }
            @Mock List<CorresponGroup> find(SearchCorresponGroupCondition condition) {
                return new ArrayList<>();
            }
            @Mock Long create(CorresponGroup cg) {
                return 9L;
            }
            @Mock CorresponGroup findByIdForUpdate(Long id) throws RecordNotFoundException {
                return cg;
            }
        };
        new MockUp<SiteDaoImpl>() {
            @Mock Site findById(Long id) throws RecordNotFoundException {
                return cg.getSite();
            }
        };
        new MockUp<DisciplineDaoImpl>() {
            @Mock Discipline findById(Long id) throws RecordNotFoundException {
                return cg.getDiscipline();
            }
        };
        new MockUp<UserDaoImpl>() {
            @Mock User findByEmpNo(String empNo) throws RecordNotFoundException {
                return new User();
            }

            @Mock List<ProjectUser> findProjectUser(SearchUserCondition condition) {
                return lpu;
            }
        };
        new MockUp<ProjectUserProfileDaoImpl>() {
            @Mock List<ProjectUserProfile> findList(SearchProjectUserProfileCondition condition) {
                return createProjectUserProfileList();
            }
        };
        new MockUp<CorresponGroupUserDaoImpl>() {
            @Mock List<CorresponGroupUser> findByCorresponGroupId(Long corresponGroupId) {
                return lstCgu;
            }
;
            @Mock Integer deleteByCorresponGroupId(Long corresponGroupId, User updateUser) {
                return 3;
            }
            @Mock Long create(CorresponGroupUser cu) {
                return 9L;
            }
        };

        try {
            service.save(cg, users);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT, actual
                .getMessageCode());
        }
    }

    /**
     * 活動単位、メンバーを保存する処理を検証. GroupAdminチェック GroupAdminは自分を変更することはできない
     * @throws Exeption
     */
    @Test
    public void testSaveNotChangeGroupAdminMySelf() throws Exception {
        // テストに必要なデータを作成
        CorresponGroup cg = createCorresponGroupForSave();
        List<User> users = createUserListForSave();
        users.add(user);

        List<ProjectUser> lpu = createProjectUserListForSave(users);
        List<Project> lp = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        lp.add(p);

        List<CorresponGroupUser> lstCgu = new ArrayList<CorresponGroupUser>();
        CorresponGroupUser cgu = new CorresponGroupUser();

        cgu.setUser(user);
        cgu.setCorresponGroup(cg);
        cgu.setSecurityLevel("30");
        lstCgu.add(cgu);

        cgu = new CorresponGroupUser();
        cgu.setUser(users.get(1));
        cgu.setCorresponGroup(cg);
        cgu.setSecurityLevel("40");
        lstCgu.add(cgu);

        cgu = new CorresponGroupUser();
        cgu.setUser(users.get(2));
        cgu.setCorresponGroup(cg);
        cgu.setSecurityLevel("40");
        lstCgu.add(cgu);

        // Mock準備
        MockAbstractService.IS_ANY_GROUP_ADMIN = true;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        MockAbstractService.ACCESSIBLE_PROJECTS = lp;
        new MockUp<CorresponGroupDaoImpl>() {
            @Mock CorresponGroup findById(Long id) throws RecordNotFoundException {
                return new CorresponGroup();
            }
            @Mock List<CorresponGroup> find(SearchCorresponGroupCondition condition) {
                return new ArrayList<>();
            }
            @Mock Long create(CorresponGroup cg) {
                return 9L;
            }
            @Mock CorresponGroup findByIdForUpdate(Long id) throws RecordNotFoundException {
                return cg;
            }
        };
        new MockUp<SiteDaoImpl>() {
            @Mock Site findById(Long id) throws RecordNotFoundException {
                return cg.getSite();
            }
        };
        new MockUp<DisciplineDaoImpl>() {
            @Mock Discipline findById(Long id) throws RecordNotFoundException {
                return cg.getDiscipline();
            }
        };
        new MockUp<UserDaoImpl>() {
            @Mock User findByEmpNo(String empNo) throws RecordNotFoundException {
                return new User();
            }

            @Mock List<ProjectUser> findProjectUser(SearchUserCondition condition) {
                return lpu;
            }
        };
        new MockUp<ProjectUserProfileDaoImpl>() {
            @Mock List<ProjectUserProfile> findList(SearchProjectUserProfileCondition condition) {
                return createProjectUserProfileList();
            }
        };
        new MockUp<CorresponGroupUserDaoImpl>() {
            @Mock List<CorresponGroupUser> findByCorresponGroupId(Long corresponGroupId) {
                return lstCgu;
            }
;
            @Mock Integer deleteByCorresponGroupId(Long corresponGroupId, User updateUser) {
                return 3;
            }
            @Mock Long create(CorresponGroupUser cu) {
                return 9L;
            }
        };


        try {
            service.save(cg, users);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT,
                actual.getMessageCode());
        }
    }

    /**
     * 活動単位、メンバーを保存する処理を検証. 活動単位の該当データ0件
     * @throws Exception
     */
    @Test
    public void testSaveNoData() throws Exception {
        // テストに必要なデータを作成
        CorresponGroup cg = createCorresponGroupForSave();
        List<User> users = createUserListForSave();
        users.add(user);

        List<ProjectUser> lpu = createProjectUserListForSave(users);
        List<Project> lp = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        lp.add(p);

        List<CorresponGroupUser> lstCgu = new ArrayList<CorresponGroupUser>();
        CorresponGroupUser cgu = new CorresponGroupUser();

        cgu.setUser(user);
        cgu.setCorresponGroup(cg);
        cgu.setSecurityLevel("30");
        lstCgu.add(cgu);

        cgu = new CorresponGroupUser();
        cgu.setUser(users.get(1));
        cgu.setCorresponGroup(cg);
        cgu.setSecurityLevel("40");
        lstCgu.add(cgu);

        cgu = new CorresponGroupUser();
        cgu.setUser(users.get(2));
        cgu.setCorresponGroup(cg);
        cgu.setSecurityLevel("40");
        lstCgu.add(cgu);

        // Mock準備
        MockAbstractService.IS_SYSTEM_ADMIN = true;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        MockAbstractService.ACCESSIBLE_PROJECTS = lp;
        new MockUp<CorresponGroupDaoImpl>() {
            @Mock CorresponGroup findById(Long id) throws RecordNotFoundException {
                // 該当データなし
                throw new RecordNotFoundException();
            }
            @Mock List<CorresponGroup> find(SearchCorresponGroupCondition condition) {
                return new ArrayList<>();
            }
            @Mock Long create(CorresponGroup cg) {
                return 9L;
            }
            @Mock CorresponGroup findByIdForUpdate(Long id) throws RecordNotFoundException {
                return cg;
            }
        };
        new MockUp<SiteDaoImpl>() {
            @Mock Site findById(Long id) throws RecordNotFoundException {
                return cg.getSite();
            }
        };
        new MockUp<DisciplineDaoImpl>() {
            @Mock Discipline findById(Long id) throws RecordNotFoundException {
                return cg.getDiscipline();
            }
        };
        new MockUp<UserDaoImpl>() {
            @Mock User findByEmpNo(String empNo) throws RecordNotFoundException {
                return new User();
            }

            @Mock List<ProjectUser> findProjectUser(SearchUserCondition condition) {
                return lpu;
            }
        };
        new MockUp<ProjectUserProfileDaoImpl>() {
            @Mock List<ProjectUserProfile> findList(SearchProjectUserProfileCondition condition) {
                return createProjectUserProfileList();
            }
        };
        new MockUp<CorresponGroupUserDaoImpl>() {
            @Mock List<CorresponGroupUser> findByCorresponGroupId(Long corresponGroupId) {
                return lstCgu;
            }
;
            @Mock Integer deleteByCorresponGroupId(Long corresponGroupId, User updateUser) {
                return 3;
            }
            @Mock Long create(CorresponGroupUser cu) {
                return 9L;
            }
        };


        try {
            service.save(cg, users);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.NO_DATA_FOUND, actual.getMessageCode());
        }
    }

    /**
     * 活動単位、メンバーを保存する処理を検証. プロジェクト相違チェック
     * @throws Exception
     */
    @Test
    public void testSaveProjectDiff() throws Exception {
        // テストに必要なデータを作成
        CorresponGroup cg = createCorresponGroupForSave();
        // プロジェクト相違
        cg.setProjectId("zzzzzzzzzz");
        List<User> users = createUserListForSave();

        List<ProjectUser> lpu = createProjectUserListForSave(users);
        List<Project> lp = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        lp.add(p);

        List<CorresponGroupUser> lstCgu = new ArrayList<CorresponGroupUser>();
        CorresponGroupUser cgu = new CorresponGroupUser();

        cgu.setUser(user);
        cgu.setCorresponGroup(cg);
        cgu.setSecurityLevel("30");
        lstCgu.add(cgu);

        cgu = new CorresponGroupUser();
        cgu.setUser(users.get(1));
        cgu.setCorresponGroup(cg);
        cgu.setSecurityLevel("40");
        lstCgu.add(cgu);

        cgu = new CorresponGroupUser();
        cgu.setUser(users.get(2));
        cgu.setCorresponGroup(cg);
        cgu.setSecurityLevel("40");
        lstCgu.add(cgu);

        MockAbstractService.IS_SYSTEM_ADMIN = true;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        MockAbstractService.ACCESSIBLE_PROJECTS = lp;
        new MockUp<CorresponGroupDaoImpl>() {
            @Mock CorresponGroup findById(Long id) throws RecordNotFoundException {
                return cg;
            }
            @Mock List<CorresponGroup> find(SearchCorresponGroupCondition condition) {
                return new ArrayList<>();
            }
            @Mock Long create(CorresponGroup cg) {
                return 9L;
            }
            @Mock CorresponGroup findByIdForUpdate(Long id) throws RecordNotFoundException {
                return cg;
            }
        };
        new MockUp<SiteDaoImpl>() {
            @Mock Site findById(Long id) throws RecordNotFoundException {
                return cg.getSite();
            }
        };
        new MockUp<DisciplineDaoImpl>() {
            @Mock Discipline findById(Long id) throws RecordNotFoundException {
                return cg.getDiscipline();
            }
        };
        new MockUp<UserDaoImpl>() {
            @Mock User findByEmpNo(String empNo) throws RecordNotFoundException {
                return new User();
            }

            @Mock List<ProjectUser> findProjectUser(SearchUserCondition condition) {
                return lpu;
            }
        };
        new MockUp<ProjectUserProfileDaoImpl>() {
            @Mock List<ProjectUserProfile> findList(SearchProjectUserProfileCondition condition) {
                return createProjectUserProfileList();
            }
        };
        new MockUp<CorresponGroupUserDaoImpl>() {
            @Mock List<CorresponGroupUser> findByCorresponGroupId(Long corresponGroupId) {
                return lstCgu;
            }
;
            @Mock Integer deleteByCorresponGroupId(Long corresponGroupId, User updateUser) {
                return 3;
            }
            @Mock Long create(CorresponGroupUser cu) {
                return 9L;
            }
        };

        try {
            service.save(cg, users);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_PROJECT_DIFF, actual
                .getMessageCode());
        }
    }

    /**
     * 指定した活動単位を削除する処理を検証. 引数がnull
     * @throws Exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testDeleteArgumentNull() throws Exception {
        service.delete(null);
    }
    /**
     * 追加する部門情報を取得する.
     * @throws Exception
     */
    @Test
    public void testSearchNotAdd() throws Exception {
        // テストに必要なデータを作成
        List<Discipline> lstDis = createDisciplineList();

        // Mock準備
        new MockUp<DisciplineDaoImpl>() {
            @Mock List<Discipline> findNotExistCorresponGroup(String projectId, Long siteId) {
                return lstDis;
            }
        };

        List<Discipline> actual = service.searchNotAdd(9999L);
        assertEquals(lstDis.toString(), actual.toString());
    }

    /**
     * IDを指定して活動単位を取得する処理を検証する.
     * @throws Exception
     */
    @Test
    public void testFind() throws Exception {
        // テストに必要なデータを作成
        CorresponGroup group = new CorresponGroup();
        group.setId(1L);
        group.setProjectId("PJ1");
        group.setName("Test Project");

        // Mock準備
        new MockUp<CorresponGroupDaoImpl>() {
            @Mock CorresponGroup findById(Long id) {
                return group;
            }
        };


        CorresponGroup actual = service.find(1L);
        assertEquals(group.toString(), actual.toString());
    }

    /**
     * IDを指定して活動単位を取得する処理を検証する.
     * 引数がnullの場合
     * @throws Exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testFindArgumentNull() throws Exception {
        // テストに必要なデータを作成
        service.find(null);
    }

    /**
     * IDを指定して活動単位を取得する処理を検証する.
     * 取得できなかった場合
     * @throws Exception
     */
    @Test
    public void testFindNoData() throws Exception {
        // Mock準備
        new MockUp<CorresponGroupDaoImpl>() {
            @Mock CorresponGroup findById(Long id) throws RecordNotFoundException {
                throw new RecordNotFoundException();
            }
        };

        try {
            service.find(99L);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.NO_DATA_FOUND, actual.getMessageCode());
        }
    }

    /**
     * 活動単位リストを作成する.
     * @return 活動単位リスト
     */
    private List<CorresponGroup> createCorresponGroupList() {
        List<CorresponGroup> lg = new ArrayList<CorresponGroup>();
        CorresponGroup cg = new CorresponGroup();

        // 拠点情報
        Site site = new Site();
        site.setId(1L);
        site.setSiteCd("YOC");
        site.setName("Yokohama");

        // 部門情報
        Discipline dis = new Discipline();
        dis.setId(1L);
        dis.setDisciplineCd("IT");
        dis.setName("It");

        cg.setId(1L);
        cg.setProjectId("PJ1");
        cg.setProjectNameE("Test Project1");
        cg.setSite(site);
        cg.setDiscipline(dis);
        cg.setName("YOC:IT");

        lg.add(cg);

        cg = new CorresponGroup();
        cg.setId(2L);
        cg.setProjectId("PJ1");
        cg.setProjectNameE("Test Project1");
        cg.setSite(site);

        dis = new Discipline();
        dis.setId(2L);
        dis.setDisciplineCd("PIPING");
        dis.setName("Piping");

        cg.setDiscipline(dis);
        cg.setName("YOC:PIPING");

        lg.add(cg);

        cg = new CorresponGroup();
        cg.setId(3L);
        cg.setProjectId("PJ1");
        cg.setProjectNameE("Test Project1");
        cg.setSite(site);

        dis = new Discipline();
        dis.setId(3L);
        dis.setDisciplineCd("BUILDING");
        dis.setName("Building");

        cg.setDiscipline(dis);
        cg.setName("YOC:BUILDING");

        lg.add(cg);

        return lg;
    }

    /**
     * 部門情報リストを作成する.
     * @return 部門情報リスト
     */
    private List<Discipline> createDisciplineList() {
        List<Discipline> ld = new ArrayList<Discipline>();

        Discipline dis = new Discipline();
        dis.setId(4L);
        dis.setProjectId("PJ1");
        dis.setDisciplineCd("AAABUMON");
        dis.setName("AAA");

        ld.add(dis);

        dis = new Discipline();
        dis.setId(5L);
        dis.setProjectId("PJ1");
        dis.setDisciplineCd("BBBUMON");
        dis.setName("BBB");

        ld.add(dis);

        dis = new Discipline();
        dis.setId(6L);
        dis.setProjectId("PJ1");
        dis.setDisciplineCd("CCCUMON");
        dis.setName("CCC");

        ld.add(dis);

        return ld;
    }

    private List<ProjectUserProfile> createProjectUserProfileList() {
        List<ProjectUserProfile> list = new ArrayList<ProjectUserProfile>();

        ProjectUserProfile profile = new ProjectUserProfile();
        profile.setId(1L);
        User user = new User();
        user.setEmpNo("ZZA02");
        profile.setUser(user);
        list.add(profile);

        profile = new ProjectUserProfile();
        profile.setId(2L);
        user = new User();
        user.setEmpNo("ZZA03");
        profile.setUser(user);
        list.add(profile);

        profile = new ProjectUserProfile();
        profile.setId(3L);
        user = new User();
        user.setEmpNo("ZZA04");
        profile.setUser(user);
        list.add(profile);

        profile = new ProjectUserProfile();
        profile.setId(4L);
        user = new User();
        user.setEmpNo("ZZA05");
        profile.setUser(user);
        list.add(profile);

        return list;
    }
}
