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

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.faces.application.FacesMessage;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import jp.co.opentone.bsol.framework.core.exception.InvalidOperationRuntimeException;
import jp.co.opentone.bsol.framework.core.message.Messages;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.extension.jsf.FacesContextMock;
import jp.co.opentone.bsol.framework.test.AbstractTestCase;
import jp.co.opentone.bsol.linkbinder.dto.CustomField;
import jp.co.opentone.bsol.linkbinder.dto.CustomFieldValue;
import jp.co.opentone.bsol.linkbinder.dto.SearchCustomFieldResult;
import jp.co.opentone.bsol.linkbinder.dto.code.UseWhole;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCustomFieldCondition;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.service.admin.impl.CustomFieldServiceImpl;
import jp.co.opentone.bsol.linkbinder.view.AbstractPage;
import mockit.Mock;
import mockit.MockUp;

/**
 * {@link CustomFieldEditPage}のテストケース
 * @author opentone
 */
public class CustomFieldEditPageTest extends AbstractTestCase {

    /**
     * テスト対象クラス
     */
    @Resource
    private CustomFieldEditPage page;

    /**
     * テストの前準備.
     */
    @BeforeClass
    public static void testSetUp() {
        new MockAbstractPage();
        new MockCustomFieldService();
        FacesContextMock.initialize();
    }

    /**
     * 後始末.
     */
    @AfterClass
    public static void testTeardown() {
        new MockAbstractPage().tearDown();
        new MockCustomFieldService().tearDown();

        FacesContextMock.tearDown();
    }

    @After
    public void tearDown() {
        MockAbstractPage.RET_PROJID = null;
        MockAbstractPage.IS_SYSTEM_ADMIN = false;
        MockAbstractPage.IS_PROJECT_ADMIN = false;

        MockCustomFieldService.RET_GENERAL_EXCEL = null;
        MockCustomFieldService.CRT_DELETE = null;
        MockCustomFieldService.RET_ASSIGN_TO = null;
        MockCustomFieldService.CRT_ASSIGN_TO = null;
        MockCustomFieldService.RET_FIND = null;
        MockCustomFieldService.RET_SAVE = null;
        MockCustomFieldService.CRT_SAVE = null;
        MockCustomFieldService.CRT_VALIDATE = null;
        MockCustomFieldService.RET_SEARCH_NOT_ASSIGNED = null;
        MockCustomFieldService.RET_SEARCH_PAGING_LIST = null;
        MockCustomFieldService.RET_VALIDATE = false;
    }

    /**
     * 初期化アクションを検証する. 登録
     * @throws Exception
     */
    @Test
    public void testInitializeInsert() throws Exception {
        MockAbstractPage.IS_SYSTEM_ADMIN = true;

        // テスト実行
        page.initialize();
        assertEquals("その他項目新規作成", page.getTitle());
        assertTrue(page.getInitialDisplaySuccess());
        assertNull(page.getProjectId());

    }

    /**
     * 初期化アクションを検証する. 更新
     * @throws Exceptino
     */
    @Test
    public void testInitializeUpdate() throws Exception {
        MockAbstractPage.IS_PROJECT_ADMIN = true;

        // 更新
        page.setId(1L);

        CustomField cf = new CustomField();
        cf.setId(1L);
        cf.setNo(1L);
        cf.setProjectCustomFieldId(1L);
        cf.setLabel("CustomField1");
        cf.setOrderNo(1L);
        cf.setUseWhole(UseWhole.ALL);

        MockCustomFieldService.RET_FIND = cf;

        List<CustomFieldValue> cfvList = new ArrayList<CustomFieldValue>();
        CustomFieldValue cfv = new CustomFieldValue();
        cfv.setCustomFieldId(1L);
        cfv.setValue("Vaule1");
        cfv.setOrderNo(1L);
        cfvList.add(cfv);

        cfv = new CustomFieldValue();
        cfv.setCustomFieldId(1L);
        cfv.setValue("Vaule2");
        cfv.setOrderNo(2L);
        cfvList.add(cfv);

        MockCustomFieldService.RET_FIND_CUSTOM_FIELD_VALUE = cfvList;

        // テスト実行
        page.initialize();

        assertEquals("その他項目更新", page.getTitle());
        assertEquals(cf.toString(), page.getCustomField().toString());
        assertEquals(cfvList.toString(), page.getCustomFieldValues().toString());
        assertTrue(page.getInitialDisplaySuccess());
    }

    /**
     * 初期化アクションを検証する. 権限がない
     * @throws Exception
     */
    @Test
    public void testInitializeInvalidPermission() throws Exception {
        // 権限がない
        MockAbstractPage.IS_SYSTEM_ADMIN = false;
        MockAbstractPage.IS_PROJECT_ADMIN = false;

        // 期待されるメッセージをセット
        FacesContextMock.EXPECTED_MESSAGE =
                new FacesMessage(FacesMessage.SEVERITY_ERROR, FacesMessage.SEVERITY_ERROR
                    .toString(), createExpectedMessageString(Messages
                    .getMessageAsString(CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT),
                    "Initialize"));
        try {
            // テスト実行
            page.initialize();
            fail("例外が発生していない");
        } catch (InvalidOperationRuntimeException e) {
            ServiceAbortException actual = (ServiceAbortException)e.getCause();
            assertEquals(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT, actual.getMessageCode());
        }
    }

    /**
     * 次画面遷移を検証する. 登録
     * @throws Exception
     */
    @Test
    public void testNextInsert() throws Exception {
        MockCustomFieldService.RET_VALIDATE = true;

        // 期待されるメッセージをセット
        FacesContextMock.EXPECTED_MESSAGE =
                new FacesMessage(
                    FacesMessage.SEVERITY_INFO, FacesMessage.SEVERITY_INFO.toString(),
                    createExpectedMessageString(Messages.getMessageAsString(CONTENT_WILL_BE_SAVED),
                        "Initialize"));

        page.setLabel("Test1");
        page.setOrderNo("1");

        String valueList = "Value1\tValue2\tValue3";
        page.setValueList(valueList);

        // テスト実行
        String next = page.next();

        assertEquals("customFieldConfirmation", next);
        assertEquals("Test1", page.getCustomField().getLabel());
        assertEquals(String.valueOf(1L), String.valueOf(page.getCustomField().getOrderNo()));
        assertNull(page.getCustomField().getProjectId());
        assertTrue(3 == page.getCustomField().getCustomFieldValues().size());
        assertEquals("Value1", page.getCustomField().getCustomFieldValues().get(0).getValue());
        assertEquals("Value2", page.getCustomField().getCustomFieldValues().get(1).getValue());
        assertEquals("Value3", page.getCustomField().getCustomFieldValues().get(2).getValue());

    }

    /**
     * 次画面遷移を検証する. 更新
     * @throws Exception
     */
    @Test
    public void testNextUpdate() throws Exception {
        MockCustomFieldService.RET_VALIDATE = true;

        // 期待されるメッセージをセット
        FacesContextMock.EXPECTED_MESSAGE =
                new FacesMessage(
                    FacesMessage.SEVERITY_INFO, FacesMessage.SEVERITY_INFO.toString(),
                    createExpectedMessageString(Messages.getMessageAsString(CONTENT_WILL_BE_SAVED),
                        "Initialize"));

        CustomField cf = new CustomField();
        cf.setId(1L);
        page.setCustomField(cf);

        page.setLabel("Test1");
        page.setOrderNo("1");

        String valueList = "Value1\tValue2\tValue3";
        page.setValueList(valueList);

        // テスト実行
        String next = page.next();

        assertEquals("customFieldConfirmation", next);
        assertEquals(cf.getId(), page.getCustomField().getId());
        assertEquals("Test1", page.getCustomField().getLabel());
        assertEquals(String.valueOf(1L), String.valueOf(page.getCustomField().getOrderNo()));
        assertNull(page.getCustomField().getProjectId());
        assertTrue(3 == page.getCustomField().getCustomFieldValues().size());
        assertEquals("Value1", page.getCustomField().getCustomFieldValues().get(0).getValue());
        assertEquals("Value2", page.getCustomField().getCustomFieldValues().get(1).getValue());
        assertEquals("Value3", page.getCustomField().getCustomFieldValues().get(2).getValue());

    }

    public static class MockCustomFieldService extends MockUp<CustomFieldServiceImpl> {
        static byte[] RET_GENERAL_EXCEL;
        static CustomField CRT_DELETE;
        static Long RET_ASSIGN_TO;
        static CustomField CRT_ASSIGN_TO;
        static CustomField RET_FIND;
        static Long RET_SAVE;
        static CustomField CRT_SAVE;;
        static CustomField CRT_VALIDATE;
        static List<CustomField> RET_SEARCH_NOT_ASSIGNED;
        static SearchCustomFieldResult RET_SEARCH_PAGING_LIST;
        static boolean RET_VALIDATE;
        static List<CustomFieldValue> RET_FIND_CUSTOM_FIELD_VALUE;

        @Mock
        public List<CustomFieldValue> findCustomFieldValue(Long customFieldId) {
            return RET_FIND_CUSTOM_FIELD_VALUE;
        }

        @Mock
        public byte[] generateExcel(List<CustomField> customFields) {
            return RET_GENERAL_EXCEL;
        }

        @Mock
        public void delete(CustomField customField) throws ServiceAbortException {
            CRT_DELETE = customField;
        }

        @Mock
        public Long assignTo(CustomField customField) throws ServiceAbortException {
            CRT_ASSIGN_TO = customField;
            return RET_ASSIGN_TO;
        }

        @Mock
        public CustomField find(Long id) throws ServiceAbortException {
            return RET_FIND;
        }

        @Mock
        public boolean validate(CustomField customField) throws ServiceAbortException {
            CRT_VALIDATE = customField;
            return RET_VALIDATE;
        }

        @Mock
        public Long save(CustomField customField) throws ServiceAbortException {
            CRT_SAVE = customField;
            return RET_SAVE;
        }

        @Mock
        public List<CustomField> searchNotAssigned() throws ServiceAbortException {
            return RET_SEARCH_NOT_ASSIGNED;
        }

        @Mock
        public SearchCustomFieldResult searchPagingList(SearchCustomFieldCondition condition)
            throws ServiceAbortException {
            return RET_SEARCH_PAGING_LIST;
        }

    }

    public static class MockAbstractPage extends MockUp<AbstractPage> {
        static String RET_PROJID;
        static boolean IS_SYSTEM_ADMIN;
        static boolean IS_PROJECT_ADMIN;

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
    }
}
