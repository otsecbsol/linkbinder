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
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCustomFieldCondition;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.service.admin.impl.CustomFieldServiceImpl;
import jp.co.opentone.bsol.linkbinder.view.AbstractPage;
import mockit.Mock;
import mockit.MockUp;

/**
 * {@link CustomFieldConfirmationPage}のテストケース
 * @author opentone
 */
public class CustomFieldConfirmationPageTest extends AbstractTestCase {

    /**
     * テスト対象クラス
     */
    @Resource
    private CustomFieldConfirmationPage page;

    /**
     * テストの前準備.
     */
    @BeforeClass
    public static void testSetUp() {
        FacesContextMock.initialize();
        new MockAbstractPage();
        new MockCustomFieldService();
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
        MockCustomFieldService.RET_GENERAL_EXCEL = null;
        MockCustomFieldService.CRT_DELETE = null;
        MockCustomFieldService.RET_ASSIGN_TO = null;
        MockCustomFieldService.CRT_ASSIGN_TO = null;
        MockCustomFieldService.RET_FIND = null;
        MockCustomFieldService.RET_SAVE = null;
        MockCustomFieldService.CRT_SAVE = null;
        MockCustomFieldService.CRT_VALIDATE = null;
        MockCustomFieldService.RET_SEARCH_PAGING_LIST = null;
        MockCustomFieldService.RET_VALIDATE = false;

        MockAbstractPage.RET_PROJID = null;
        MockAbstractPage.IS_SYSTEM_ADMIN = false;
        MockAbstractPage.IS_PROJECT_ADMIN = false;
        page.setInitialDisplaySuccess(false);
        page.setInitialValuesSuccess(false);

    }

    /**
     * 初期化アクションの検証. Valuesが入力されている
     * @throws Exception
     */
    @Test
    public void testInitializeValues() throws Exception {
        MockAbstractPage.IS_SYSTEM_ADMIN = true;

        CustomField cf = new CustomField();
        cf.setLabel("Test1");
        cf.setOrderNo(1L);

        List<CustomFieldValue> cfvList = new ArrayList<CustomFieldValue>();
        CustomFieldValue cfv = new CustomFieldValue();
        cfv.setValue("Value1");
        cfvList.add(cfv);

        cfv = new CustomFieldValue();
        cfv.setValue("Value2");
        cfvList.add(cfv);

        cfv = new CustomFieldValue();
        cfv.setValue("Value3");
        cfvList.add(cfv);

        cf.setCustomFieldValues(cfvList);

        page.setCustomField(cf);

        // テスト実行
        page.initialize();

        assertTrue(page.isInitialValuesSuccess());
        assertTrue(page.isInitialDisplaySuccess());
        assertNull(page.getProjectId());

    }

    /**
     * 初期化アクションの検証. Valuesが入力されていない
     * @throws Exception
     */
    @Test
    public void testInitializeNoValues() throws Exception {
        MockAbstractPage.IS_SYSTEM_ADMIN = true;
        MockAbstractPage.RET_PROJID = "PJ1";

        CustomField cf = new CustomField();
        cf.setLabel("Test1");
        cf.setOrderNo(1L);

        page.setCustomField(cf);

        // テスト実行
        page.initialize();

        assertFalse(page.isInitialValuesSuccess());
        assertTrue(page.isInitialDisplaySuccess());
        assertEquals("PJ1", page.getProjectId());
    }

    /**
     * 初期化アクションの検証. 権限がない
     * @throws Exception
     */
    @Test
    public void testInitializeInvalidPermission() throws Exception {
        // 権限がない
        MockAbstractPage.IS_SYSTEM_ADMIN = false;
        MockAbstractPage.IS_PROJECT_ADMIN = false;

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
     * 保存アクションの検証.
     * @throws Exception
     */
    @Test
    public void testSave() throws Exception {
        CustomField cf = new CustomField();
        cf.setLabel("Test1");
        cf.setOrderNo(1L);

        List<CustomFieldValue> cfvList = new ArrayList<CustomFieldValue>();
        CustomFieldValue cfv = new CustomFieldValue();
        cfv.setValue("Value1");
        cfvList.add(cfv);

        cfv = new CustomFieldValue();
        cfv.setValue("Value2");
        cfvList.add(cfv);

        cfv = new CustomFieldValue();
        cfv.setValue("Value3");
        cfvList.add(cfv);

        cf.setCustomFieldValues(cfvList);
        page.setCustomField(cf);

        MockCustomFieldService.RET_SAVE = 10L;

        // 期待されるメッセージをセット
        FacesContextMock.EXPECTED_MESSAGE =
                new FacesMessage(
                    FacesMessage.SEVERITY_INFO, FacesMessage.SEVERITY_INFO.toString(),
                    createExpectedMessageString(Messages.getMessageAsString(CUSTOM_FIELD_SAVED),
                        "Save"));

        // テスト実行
        page.save();

        assertEquals(String.valueOf(10L), String.valueOf(page.getId()));
        assertEquals(cf.toString(), MockCustomFieldService.CRT_SAVE.toString());

    }

    /**
     * Backリンク押下時の検証.
     * @throws Exception
     */
    @Test
    public void testBack() throws Exception {
        String back = page.back();
        assertEquals("customFieldEdit", back);
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
