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
import jp.co.opentone.bsol.linkbinder.dao.impl.CustomFieldDaoImpl;
import jp.co.opentone.bsol.linkbinder.dao.impl.CustomFieldValueDaoImpl;
import jp.co.opentone.bsol.linkbinder.dao.impl.ProjectCustomFieldDaoImpl;
import jp.co.opentone.bsol.linkbinder.dto.CustomField;
import jp.co.opentone.bsol.linkbinder.dto.CustomFieldValue;
import jp.co.opentone.bsol.linkbinder.dto.Project;
import jp.co.opentone.bsol.linkbinder.dto.ProjectCustomField;
import jp.co.opentone.bsol.linkbinder.dto.SearchCustomFieldResult;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.code.UseWhole;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCustomFieldCondition;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.service.MockAbstractService;
import jp.co.opentone.bsol.linkbinder.service.admin.CustomFieldService;
import mockit.Mock;
import mockit.MockUp;

/**
 * {@link CustomFieldServiceImpl}のテストケース
 * @author opentone
 */
public class CustomFieldServiceImplTest extends AbstractTestCase {

    /**
     * テスト対象.
     */
    @Resource
    private CustomFieldService service;

    private User user = new User();

    @BeforeClass
    public static void testSetUp() {
        new MockAbstractService();
    }

    @AfterClass
    public static void testTeardown() {
        new MockAbstractService().tearDown();
    }

    /**
     * テスト前準備.
     */
    @Before
    public void setUp() {
        user.setEmpNo("ZZA01");
        MockAbstractService.CURRENT_USER = user;
        MockAbstractService.CURRENT_PROJECT_ID = null;
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
     * カスタムフィールド設定値を取得する処理のテスト
     * @throws ServiceAbortException
     */
    @Test
    public void testFindCustomFieldValue() throws ServiceAbortException {
        List<CustomFieldValue> cfvList = new ArrayList<CustomFieldValue>();
        CustomFieldValue cfv = new CustomFieldValue();
        cfv.setId(1L);
        cfv.setCustomFieldId(1L);
        cfv.setValue("Value1");
        cfv.setOrderNo(1L);
        cfv.setCreatedBy(user);
        cfv.setUpdatedBy(user);
        cfv.setDeleteNo(0L);

        cfvList.add(cfv);

        cfv = new CustomFieldValue();
        cfv.setId(2L);
        cfv.setCustomFieldId(1L);
        cfv.setValue("Value2");
        cfv.setOrderNo(1L);
        cfv.setCreatedBy(user);
        cfv.setUpdatedBy(user);
        cfv.setDeleteNo(0L);
        cfvList.add(cfv);

        cfv = new CustomFieldValue();
        cfv.setId(3L);
        cfv.setCustomFieldId(1L);
        cfv.setValue("Value3");
        cfv.setOrderNo(1L);
        cfv.setCreatedBy(user);
        cfv.setUpdatedBy(user);
        cfv.setDeleteNo(0L);
        cfvList.add(cfv);

        // Mock準備
        new MockUp<CustomFieldValueDaoImpl>() {
            @Mock List<CustomFieldValue> findByCustomFieldId(Long customFieldId) {
                return cfvList;
            }
        };

        List<CustomFieldValue> actual = service.findCustomFieldValue(1L);

        assertEquals(String.valueOf(1L), actual.get(0).getId().toString());
        assertEquals(String.valueOf(1L), actual.get(0).getCustomFieldId().toString());
        assertEquals("Value1", actual.get(0).getValue());
        assertEquals(String.valueOf(1L), actual.get(0).getOrderNo().toString());
        assertEquals(user.toString(), actual.get(0).getCreatedBy().toString());
        assertEquals(user.toString(), actual.get(0).getUpdatedBy().toString());
        assertEquals(String.valueOf(0L), actual.get(0).getDeleteNo().toString());

        assertEquals(String.valueOf(2L), actual.get(1).getId().toString());
        assertEquals(String.valueOf(1L), actual.get(1).getCustomFieldId().toString());
        assertEquals("Value2", actual.get(1).getValue());
        assertEquals(String.valueOf(1L), actual.get(1).getOrderNo().toString());
        assertEquals(user.toString(), actual.get(1).getCreatedBy().toString());
        assertEquals(user.toString(), actual.get(1).getUpdatedBy().toString());
        assertEquals(String.valueOf(0L), actual.get(1).getDeleteNo().toString());

        assertEquals(String.valueOf(3L), actual.get(2).getId().toString());
        assertEquals(String.valueOf(1L), actual.get(2).getCustomFieldId().toString());
        assertEquals("Value3", actual.get(2).getValue());
        assertEquals(String.valueOf(1L), actual.get(2).getOrderNo().toString());
        assertEquals(user.toString(), actual.get(2).getCreatedBy().toString());
        assertEquals(user.toString(), actual.get(2).getUpdatedBy().toString());
        assertEquals(String.valueOf(0L), actual.get(2).getDeleteNo().toString());

    }

    /**
     * カスタムフィールド設定値を取得する処理のテスト
     * 引数がnull
     * @throws ServiceAbortException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testFindCustomFieldValueArgumentNull() throws ServiceAbortException {
        service.findCustomFieldValue(null);
    }

    /**
     * 条件を指定してカスタムフィールドを取得する処理のテスト
     * @throws Exception
     */
    @Test
    public void testSearch() throws Exception {
        List<CustomField> customFieldList = new ArrayList<CustomField>();
        List<CustomField> expectedList = new ArrayList<CustomField>();

        CustomField cf = new CustomField();
        cf.setId(1L);
        cf.setNo(1L);
        cf.setProjectCustomFieldId(1L);
        cf.setProjectId("PJ1");
        cf.setProjectNameE("Test Project1");
        cf.setLabel("TEST1");
        cf.setOrderNo(1L);
        cf.setUseWhole(UseWhole.ALL);
        cf.setCreatedBy(user);
        cf.setUpdatedBy(user);
        cf.setVersionNo(1L);
        cf.setDeleteNo(0L);

        customFieldList.add(cf);
        expectedList.add(cf);

        cf = new CustomField();
        cf.setId(2L);
        cf.setNo(2L);
        cf.setProjectCustomFieldId(2L);
        cf.setProjectId("PJ1");
        cf.setProjectNameE("Test Project1");
        cf.setLabel("TEST2");
        cf.setOrderNo(2L);
        cf.setUseWhole(UseWhole.ALL);
        cf.setCreatedBy(user);
        cf.setUpdatedBy(user);
        cf.setVersionNo(1L);
        cf.setDeleteNo(0L);

        customFieldList.add(cf);
        expectedList.add(cf);

        cf = new CustomField();
        cf.setId(3L);
        cf.setNo(3L);
        cf.setProjectCustomFieldId(3L);
        cf.setProjectId("PJ1");
        cf.setProjectNameE("Test Project1");
        cf.setLabel("TEST3");
        cf.setOrderNo(2L);
        cf.setUseWhole(UseWhole.ALL);
        cf.setCreatedBy(user);
        cf.setUpdatedBy(user);
        cf.setVersionNo(1L);
        cf.setDeleteNo(0L);

        customFieldList.add(cf);
        expectedList.add(cf);

        // Mock準備
        new MockUp<CustomFieldDaoImpl>() {
            @Mock List<CustomField> findByProjectId(SearchCustomFieldCondition condition) {
                return customFieldList;
            }
        };

        SearchCustomFieldCondition condition = new SearchCustomFieldCondition();
        condition.setProjectId("PJ1");
        List<CustomField> actual = service.search(condition);

        assertEquals(expectedList.toString(), actual.toString());
    }

    /**
     * 条件を指定してカスタムフィールドを取得する処理のテスト
     * 引数がnull
     * @throws Exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSearchArgumentNull() throws Exception {
        service.search(null);
    }

    /**
     * カスタムフィールドを検索する処理を検証.
     * AdminHome
     * @throws Exception
     */
    @Test
    public void testSearchPagingListAdmin() throws Exception {
        MockAbstractService.IS_SYSTEM_ADMIN = true;
        List<CustomField> customFields = createList();

        // Mock準備
        new MockUp<CustomFieldDaoImpl>() {
            @Mock List<CustomField> find(SearchCustomFieldCondition condition) {
                return customFields;
            }
            @Mock int count(SearchCustomFieldCondition condition) {
                return 3;
            }
        };

        SearchCustomFieldResult result = service.searchPagingList(new SearchCustomFieldCondition());

        assertEquals(customFields.toString(), result.getCustomFieldList().toString());
        assertEquals(3, result.getCount());
    }

    /**
     * カスタムフィールドを検索する処理を検証.
     * AdminHome、権限がない
     * @throws Exception
     */
    @Test
    public void testSearchPagingListAdminInvalidPermission() throws Exception {
        // 権限がない
        MockAbstractService.IS_SYSTEM_ADMIN = false;

        try {
            service.searchPagingList(new SearchCustomFieldCondition());
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT,
                actual.getMessageCode());
        }
    }

    /**
     * カスタムフィールドを検索する処理を検証.
     * ProjectAdminHome
     * @throws Exception
     */
    @Test
    public void testSearchPagingListProject() throws Exception {
        List<CustomField> customFields = createList();

        // Mock準備
        MockAbstractService.IS_SYSTEM_ADMIN = true;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        new MockUp<CustomFieldDaoImpl>() {
            @Mock List<CustomField> find(SearchCustomFieldCondition condition) {
                return customFields;
            }
            @Mock int count(SearchCustomFieldCondition condition) {
                return 3;
            }
        };

        SearchCustomFieldResult result = service.searchPagingList(new SearchCustomFieldCondition());

        assertEquals(customFields.toString(), result.getCustomFieldList().toString());
        assertEquals(3, result.getCount());
    }

    /**
     * カスタムフィールドを検索する処理を検証.
     * 引数がない
     * @throws Exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSearchPagingListAugumentNull() throws Exception {
        service.searchPagingList(null);
    }

    /**
     * カスタムフィールドを検索する処理を検証.
     * ProjectAdminHome、権限がない
     * @throws Exception
     */
    @Test
    public void testSearchPagingListProjectInvalidPermission() throws Exception {
        // 権限がない場合
        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN = false;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        try {
            service.searchPagingList(new SearchCustomFieldCondition());
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT,
                actual.getMessageCode());
        }
    }

    /**
     * カスタムフィールドを検索する処理を検証.
     * 該当データ0件の場合
     * @throws Exception
     */
    @Test
    public void testSearchPagingListNoData() throws Exception {
        // Mock準備
        MockAbstractService.IS_SYSTEM_ADMIN = true;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        new MockUp<CustomFieldDaoImpl>() {
            @Mock List<CustomField> find(SearchCustomFieldCondition condition) {
                return new ArrayList<>();
            }
            @Mock int count(SearchCustomFieldCondition condition) {
                // 該当データ0件
                return 0;
            }
        };

        try {
            service.searchPagingList(new SearchCustomFieldCondition());
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.NO_DATA_FOUND, actual.getMessageCode());
        }
    }

    /**
     * カスタムフィールドを検索する処理を検証.
     * 指定のページを選択した際にそのページに表示するデータが存在しない
     * @throws Exception
     */
    @Test
    public void testSearchPagingListProjectNoPageData() throws Exception {
        // Mock準備
        MockAbstractService.IS_SYSTEM_ADMIN = true;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        new MockUp<CustomFieldDaoImpl>() {
            @Mock List<CustomField> find(SearchCustomFieldCondition condition) {
                // 指定のページを選択した際にデータがない
                return new ArrayList<>();
            }
            @Mock int count(SearchCustomFieldCondition condition) {
                return 3;
            }
        };

        try {
            service.searchPagingList(new SearchCustomFieldCondition());
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.NO_PAGE_FOUND, actual.getMessageCode());
        }
    }

    /**
     * 現在のプロジェクトに属していないカスタムフィールドを取得する処理を検証する.
     * @throws Exception
     */
    @Test
    public void testSearchNotAssigned() throws Exception {
        // Mock準備
        new MockUp<CustomFieldDaoImpl>() {
            @Mock List<CustomField> findNotAssignTo(String projectId) {
                return createNotProjectList();
            }
        };

        List<CustomField> actual = service.searchNotAssigned();
        assertEquals(createNotProjectList().toString(), actual.toString());
    }

    /**
     * 指定されたカスタムフィールド一覧をExcel形式に変換して返すテスト.
     */
    @Test
    public void testGenerateExcel() throws Exception {
        // 結果レコード
        List<Object> expected = new ArrayList<Object>();
        /* ヘッダ */
        List<Object> line = new ArrayList<Object>();
        line.add("ID");
        line.add("Label");
        line.add("Order No.");
        expected.add(line);

        /* 1行目 */
        line = new ArrayList<Object>();
        line.add(new Integer(1));
        line.add("TEST1");
        line.add(new Integer(1));
        expected.add(line);

        /* 2行目 */
        line = new ArrayList<Object>();
        line.add(new Integer(2));
        line.add("TEST2");
        line.add(new Integer(2));
        expected.add(line);

        /* 3行目 */
        line = new ArrayList<Object>();
        line.add(new Integer(3));
        line.add("TEST3");
        line.add(new Integer(3));
        expected.add(line);

        // マスタ管理の為、リストを作成する際にnullを渡す。
        byte[] actual = service.generateExcel(createList());

        // 作成したExcelを確認
        assertExcel(1, "CustomFieldIndex", 3, expected, actual);
    }

    /**
     * 指定されたカスタムフィールド一覧をExcel形式に変換して返すテスト.
     * 特定のプロジェクトでの操作を想定.
     */
    @Test
    public void testGenerateExcelPerProject() throws Exception {
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";

        // 結果レコード
        List<Object> expected = new ArrayList<Object>();
        /* ヘッダ */
        List<Object> line = new ArrayList<Object>();
        line.add("ID");
        line.add("Label");
        line.add("Order No.");
        expected.add(line);

        /* 1行目 */
        line = new ArrayList<Object>();
        line.add(new Integer(11));
        line.add("TEST1");
        line.add(new Integer(1));
        expected.add(line);

        /* 2行目 */
        line = new ArrayList<Object>();
        line.add(new Integer(22));
        line.add("TEST2");
        line.add(new Integer(2));
        expected.add(line);

        /* 3行目 */
        line = new ArrayList<Object>();
        line.add(new Integer(33));
        line.add("TEST3");
        line.add(new Integer(3));
        expected.add(line);

        // マスタ管理の為、リストを作成する際にnullを渡す。
        byte[] actual = service.generateExcel(createList());

        // 作成したExcelを確認
        assertExcel(1, "CustomFieldIndex", 3, expected, actual);
    }

    /**
     * 指定されたカスタムフィールド一覧をExcel形式に変換して返すテスト.
     * 引数がnull
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGenerateExcelArgumentNull() throws Exception {
        service.generateExcel(null);
    }

    /**
     * カスタムフィールドをプロジェクトに紐付ける処理のテスト.
     * @throws Exception
     */
    @Test
    public void testAssignTo() throws Exception {
        CustomField customField = new CustomField();
        customField.setId(1L);
        customField.setLabel("TEST1");
        customField.setOrderNo(1L);
        customField.setUseWhole(UseWhole.ALL);

        User loginUser = new User();
        loginUser.setEmpNo("ZZA01");
        loginUser.setNameE("Taro Yamada");

        customField.setCreatedBy(loginUser);
        customField.setUpdatedBy(loginUser);
        customField.setVersionNo(1L);
        customField.setDeleteNo(0L);


        User assignUser = new User();
        assignUser.setEmpNo("ZZA02");
        assignUser.setNameE("Tetsuo Aoki");

        // Mock準備
        MockAbstractService.IS_SYSTEM_ADMIN = true;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        MockAbstractService.CURRENT_USER = assignUser;
        new MockUp<CustomFieldDaoImpl>() {
            @Mock CustomField findById(Long id) throws RecordNotFoundException {
                return customField;
            }
            @Mock CustomField findByIdProjectId(SearchCustomFieldCondition condition)
                    throws RecordNotFoundException {
                throw new RecordNotFoundException();
            }
            @Mock int countCheck(SearchCustomFieldCondition condition) {
                return 0;
            }
        };
        new MockUp<ProjectCustomFieldDaoImpl>() {
            @Mock Long create(ProjectCustomField actual) {
                assertNull(actual.getId());
                assertEquals("PJ1", actual.getProjectId());
                assertEquals(customField.getId(), actual.getCustomFieldId());
                assertEquals(customField.getLabel(), actual.getLabel());
                assertEquals(customField.getOrderNo(), actual.getOrderNo());
                assertEquals(assignUser.toString(), actual.getCreatedBy().toString());
                assertEquals(assignUser.toString(), actual.getUpdatedBy().toString());
                assertNull(actual.getCreatedAt());
                assertNull(actual.getUpdatedAt());
                assertNull(actual.getDeleteNo());

                return 1L;
            }
        };

        // テスト実行
        service.assignTo(customField);
    }

    /**
     * カスタムフィールドをプロジェクトに紐付ける処理のテスト
     * 権限がない
     * @throws Exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAssignToArgumentNull() throws Exception {
        service.assignTo(null);
    }

    /**
     * カスタムフィールドをプロジェクトに紐付ける処理のテスト
     * 権限がない
     * @throws Exception
     */
    @Test
    public void testAssignToInvalidPermission() throws Exception {
        // 権限がない
        MockAbstractService.IS_SYSTEM_ADMIN = false;
        MockAbstractService.IS_PROJECT_ADMIN = false;

        try {
            service.assignTo(new CustomField());
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT,
                actual.getMessageCode());
        }
    }

    /**
     * カスタムフィールドをプロジェクトに紐付ける処理のテスト
     * 指定のカスタムフィールド情報が存在しない
     * @throws Exception
     */
    @Test
    public void testAssignToNoData() throws Exception {
        MockAbstractService.IS_SYSTEM_ADMIN = true;
        // Mock準備
        new MockUp<CustomFieldDaoImpl>() {
            // カスタムフィールド情報が存在しない
            @Mock CustomField findById(Long id) throws RecordNotFoundException {
                throw new RecordNotFoundException();
            }
        };

        try {
            service.assignTo(new CustomField());
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.NO_DATA_FOUND, actual.getMessageCode());
        }
    }

    /**
     * カスタムフィールドをプロジェクトに紐付ける処理のテスト
     * 指定のプロジェクトに同じカスタムフィールド情報が既に紐付いてる
     * @throws Exception
     */
    @Test
    public void testAssignToExistProjectCustomField() throws Exception {
        MockAbstractService.IS_PROJECT_ADMIN = true;

        // Mock準備
        new MockUp<CustomFieldDaoImpl>() {
            // カスタムフィールド情報が存在しない
            @Mock CustomField findById(Long id) throws RecordNotFoundException {
                return new CustomField();
            }
            @Mock CustomField findByIdProjectId(SearchCustomFieldCondition condition)
                    throws RecordNotFoundException {
                return new CustomField();
            }
        };

        try {
            service.assignTo(new CustomField());
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CUSTOM_FIELD_ALREADY_ASSIGNED_TO_PROJECT,
                actual.getMessageCode());
        }

    }

    /**
     * カスタムフィールドをプロジェクトに紐付ける処理のテスト.
     * 指定ののプロジェクトに同じラベルのカスタムフィールドが存在する
     * @throws Exception
     */
    @Test
    public void testAssignToExistLabelProjectCustomField() throws Exception {
        CustomField customField = new CustomField();
        customField.setId(1L);
        customField.setLabel("TEST1");
        customField.setOrderNo(1L);
        customField.setUseWhole(UseWhole.ALL);

        User loginUser = new User();
        loginUser.setEmpNo("ZZA01");
        loginUser.setNameE("Taro Yamada");

        customField.setCreatedBy(loginUser);
        customField.setUpdatedBy(loginUser);
        customField.setVersionNo(1L);
        customField.setDeleteNo(0L);

        User assignUser = new User();
        assignUser.setEmpNo("ZZA02");
        assignUser.setNameE("Tetsuo Aoki");

        // Mock準備
        MockAbstractService.IS_SYSTEM_ADMIN = true;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        MockAbstractService.CURRENT_USER = assignUser;
        new MockUp<CustomFieldDaoImpl>() {
            // カスタムフィールド情報が存在しない
            @Mock CustomField findById(Long id) throws RecordNotFoundException {
                return new CustomField();
            }
            @Mock CustomField findByIdProjectId(SearchCustomFieldCondition condition)
                    throws RecordNotFoundException {
                throw new RecordNotFoundException();
            }
            @Mock int countCheck(SearchCustomFieldCondition condition) {
                return 1;
            }
        };

        try {
            service.assignTo(customField);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CUSTOM_FIELD_LABEL_ALREADY_EXISTS, actual.getMessageCode());
        }
    }

    /**
     * IDを指定してカスタムフィールドを取得するテスト
     * AdminHome
     * @throws Exception
     */
    @Test
    public void testFindAdmin() throws Exception {
        MockAbstractService.IS_SYSTEM_ADMIN = true;

        CustomField customField = new CustomField();
        customField.setId(1L);
        customField.setLabel("TEST1");
        customField.setOrderNo(1L);
        customField.setUseWhole(UseWhole.ALL);

        User loginUser = new User();
        loginUser.setEmpNo("ZZA01");
        loginUser.setNameE("Taro Yamada");

        customField.setCreatedBy(loginUser);
        customField.setUpdatedBy(loginUser);
        customField.setVersionNo(1L);
        customField.setDeleteNo(0L);

        // Mock準備
        new MockUp<CustomFieldDaoImpl>() {
            // カスタムフィールド情報が存在しない
            @Mock CustomField findById(Long id) throws RecordNotFoundException {
                return customField;
            }
        };

        // テスト実行
        CustomField actual = service.find(1L);

        assertEquals(customField.toString(), actual.toString());
    }

    /**
     * IDを指定してカスタムフィールドを取得するテスト
     * ProjectAdminHome
     * @throws Exception
     */
    @Test
    public void testFindProject() throws Exception {
        CustomField customField = new CustomField();
        customField.setId(1L);
        customField.setLabel("TEST1");
        customField.setOrderNo(1L);
        customField.setProjectId("PJ1");
        customField.setProjectNameE("Test Project1");
        customField.setUseWhole(UseWhole.ALL);

        User loginUser = new User();
        loginUser.setEmpNo("ZZA01");
        loginUser.setNameE("Taro Yamada");

        customField.setCreatedBy(loginUser);
        customField.setUpdatedBy(loginUser);
        customField.setVersionNo(1L);
        customField.setDeleteNo(0L);

        List<Project> projectList = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        projectList.add(p);

        // Mock準備
        MockAbstractService.IS_PROJECT_ADMIN = true;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        MockAbstractService.ACCESSIBLE_PROJECTS = projectList;
        new MockUp<CustomFieldDaoImpl>() {
            @Mock CustomField findByIdProjectId(SearchCustomFieldCondition condition)
                    throws RecordNotFoundException {
                return customField;
            }
        };

        CustomField actual = service.find(1L);

        assertEquals(customField.toString(), actual.toString());
    }

    /**
     * IDを指定してカスタムフィールドを取得するテスト.
     * AdminHome、データが取得できない
     * @throws Exception
     */
    @Test
    public void testFindAdminNoData() throws Exception {
        // Mock準備
        MockAbstractService.IS_SYSTEM_ADMIN = true;
        new MockUp<CustomFieldDaoImpl>() {
            @Mock CustomField findById(Long id)
                    throws RecordNotFoundException {
                throw new RecordNotFoundException();
            }
        };

        try {
            // テスト実行
            service.find(1L);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.NO_DATA_FOUND, actual.getMessageCode());
        }
    }

    /**
     * IDを指定してカスタムフィールドを取得するテスト.
     * ProjectAdminHome、データが取得できない
     * @throws Exception
     */
    @Test
    public void testFindProjectNoData() throws Exception {
        // Mock準備
        MockAbstractService.IS_PROJECT_ADMIN = true;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        new MockUp<CustomFieldDaoImpl>() {
            @Mock CustomField findByIdProjectId(SearchCustomFieldCondition condition)
                    throws RecordNotFoundException {
                throw new RecordNotFoundException();
            }
        };

        try {
            // テスト実行
            service.find(1L);
            fail("例外が発生していない");
        } catch (ServiceAbortException actual) {
            assertEquals(ApplicationMessageCode.NO_DATA_FOUND, actual.getMessageCode());
        }
    }

    /**
     * IDを指定してカスタムフィールドを取得するテスト
     * 引数がnull
     * @throws Exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testFindArgumentNull() throws Exception {
        service.find(null);
    }

    /**
     * IDを指定してカスタムフィールドを取得するテスト
     * カスタムフィールドのプロジェクトが現在選択中のプロジェクト以外
     * @throws Exception
     */
    @Test
    public void testFindDiffProject() throws Exception {
        CustomField customField = new CustomField();
        customField.setId(1L);
        customField.setLabel("TEST1");
        customField.setOrderNo(1L);
        customField.setProjectId("PJ2");
        customField.setProjectNameE("Test Project1");
        customField.setUseWhole(UseWhole.ALL);

        User loginUser = new User();
        loginUser.setEmpNo("ZZA01");
        loginUser.setNameE("Taro Yamada");

        customField.setCreatedBy(loginUser);
        customField.setUpdatedBy(loginUser);
        customField.setVersionNo(1L);
        customField.setDeleteNo(0L);

        List<Project> projectList = new ArrayList<Project>();
        Project p = new Project();
        p.setProjectId("PJ1");
        projectList.add(p);

        // Mock準備
        MockAbstractService.IS_PROJECT_ADMIN = true;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        MockAbstractService.ACCESSIBLE_PROJECTS = projectList;
        new MockUp<CustomFieldDaoImpl>() {
            @Mock CustomField findByIdProjectId(SearchCustomFieldCondition condition)
                    throws RecordNotFoundException {
                return customField;
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

    private List<CustomField> createList() {
        User user = new User();
        user.setEmpNo("ZZA04");
        user.setNameE("Tomoko Okada");

        List<CustomField> customFields = new ArrayList<CustomField>();
        CustomField cf = new CustomField();
        cf.setId(1L);
        cf.setNo(1L);
        cf.setProjectCustomFieldId(11L);
        cf.setProjectId("PJ1");
        cf.setProjectNameE("Test Project1");
        cf.setLabel("TEST1");
        cf.setOrderNo(1L);
        cf.setUseWhole(UseWhole.ALL);

        cf.setCreatedBy(user);
        cf.setUpdatedBy(user);
        cf.setVersionNo(1L);
        cf.setDeleteNo(0L);

        customFields.add(cf);

        cf = new CustomField();

        cf.setId(2L);
        cf.setNo(2L);
        cf.setProjectCustomFieldId(22L);
        cf.setProjectId("PJ1");
        cf.setProjectNameE("Test Project1");
        cf.setLabel("TEST2");
        cf.setOrderNo(2L);
        cf.setUseWhole(UseWhole.ALL);

        cf.setCreatedBy(user);
        cf.setUpdatedBy(user);
        cf.setVersionNo(1L);
        cf.setDeleteNo(0L);

        customFields.add(cf);

        cf = new CustomField();

        cf.setId(3L);
        cf.setNo(3L);
        cf.setProjectCustomFieldId(33L);
        cf.setProjectId("PJ1");
        cf.setProjectNameE("Test Project1");
        cf.setLabel("TEST3");
        cf.setOrderNo(3L);
        cf.setUseWhole(UseWhole.ALL);

        cf.setCreatedBy(user);
        cf.setUpdatedBy(user);
        cf.setVersionNo(1L);
        cf.setDeleteNo(0L);

        customFields.add(cf);

        return customFields;
    }

    private List<CustomField> createNotProjectList() {
        User user = new User();
        user.setEmpNo("ZZA04");
        user.setNameE("Tomoko Okada");

        List<CustomField> customFields = new ArrayList<CustomField>();
        CustomField cf = new CustomField();
        cf.setId(11L);
        cf.setNo(11L);
        cf.setProjectCustomFieldId(11L);
        cf.setLabel("TEST11");
        cf.setOrderNo(11L);
        cf.setUseWhole(UseWhole.ALL);

        cf.setCreatedBy(user);
        cf.setUpdatedBy(user);
        cf.setVersionNo(1L);
        cf.setDeleteNo(0L);

        customFields.add(cf);

        cf = new CustomField();

        cf.setId(12L);
        cf.setNo(12L);
        cf.setProjectCustomFieldId(12L);
        cf.setLabel("TEST12");
        cf.setOrderNo(12L);
        cf.setUseWhole(UseWhole.ALL);

        cf.setCreatedBy(user);
        cf.setUpdatedBy(user);
        cf.setVersionNo(1L);
        cf.setDeleteNo(0L);

        customFields.add(cf);

        cf = new CustomField();

        cf.setId(13L);
        cf.setNo(13L);
        cf.setProjectCustomFieldId(13L);
        cf.setLabel("TEST3");
        cf.setOrderNo(13L);
        cf.setUseWhole(UseWhole.ALL);

        cf.setCreatedBy(user);
        cf.setUpdatedBy(user);
        cf.setVersionNo(1L);
        cf.setDeleteNo(0L);

        customFields.add(cf);

        return customFields;
    }

}
