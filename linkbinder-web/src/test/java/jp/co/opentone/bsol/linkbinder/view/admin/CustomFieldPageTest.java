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

import static jp.co.opentone.bsol.framework.core.message.MessageCode.*;
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
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.code.UseWhole;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCustomFieldCondition;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.service.admin.impl.CustomFieldServiceImpl;
import jp.co.opentone.bsol.linkbinder.view.AbstractPage;
import mockit.Mock;
import mockit.MockUp;

/**
 * {@link CustomFieldPage}のテストケース.}
 * @author opentone
 */
public class CustomFieldPageTest extends AbstractTestCase {

    /**
     * テスト対象クラス
     */
    @Resource
    private CustomFieldPage page;

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

    }

    /**
     * 初期化アクションを検証する.
     * @throws Exception
     */
    @Test
    public void testInitialize() throws Exception {

        page.setId(1L);

        MockAbstractPage.IS_SYSTEM_ADMIN = true;

        CustomField cf = new CustomField();
        cf.setId(1L);
        cf.setNo(1L);
        cf.setProjectCustomFieldId(1L);
        cf.setLabel("Test1");
        cf.setOrderNo(1L);
        cf.setUseWhole(UseWhole.ALL);

        User loginUser = new User();
        loginUser.setEmpNo("ZZA01");
        loginUser.setNameE("Taro Yamada");

        cf.setCreatedBy(loginUser);
        cf.setUpdatedBy(loginUser);

        MockCustomFieldService.RET_FIND = cf;

        List<CustomFieldValue> cfvList = new ArrayList<CustomFieldValue>();
        CustomFieldValue cfv = new CustomFieldValue();
        cfv.setValue("Value1");
        cfv.setCustomFieldId(1L);
        cfvList.add(cfv);

        cfv = new CustomFieldValue();
        cfv.setValue("Value2");
        cfv.setCustomFieldId(2L);
        cfvList.add(cfv);

        cfv = new CustomFieldValue();
        cfv.setValue("Value3");
        cfv.setCustomFieldId(3L);
        cfvList.add(cfv);

        MockCustomFieldService.RET_FIND_CUSTOM_FIELD_VALUE = cfvList;

        // テスト実行
        page.initialize();

        assertEquals(cf.getId(), page.getCustomField().getId());
        assertEquals(cf.getNo(), page.getCustomField().getNo());
        assertEquals(cf.getProjectCustomFieldId(), page.getCustomField().getProjectCustomFieldId());
        assertEquals(cf.getLabel(), page.getCustomField().getLabel());
        assertEquals(cf.getOrderNo(), page.getCustomField().getOrderNo());
        assertEquals(cf.getUseWhole(), page.getCustomField().getUseWhole());
        assertEquals(cf.getCreatedBy(), page.getCustomField().getCreatedBy());
        assertEquals(cf.getUpdatedBy(), page.getCustomField().getUpdatedBy());
        assertEquals(cfvList.toString(), page.getCustomField().getCustomFieldValues().toString());
    }

    /**
     * 初期化アクションを検証する. カスタムフィールドIDがnull
     * @throws Exception
     */
    @Test
    public void testInitializeIdNull() throws Exception {
        // カスタムフィールドIDがnull
        page.setId(null);

        MockAbstractPage.IS_SYSTEM_ADMIN = true;
        MockCustomFieldService.RET_FIND = new CustomField();
        MockCustomFieldService.RET_FIND_CUSTOM_FIELD_VALUE = new ArrayList<CustomFieldValue>();

        // 期待されるメッセージをセット
        FacesContextMock.EXPECTED_MESSAGE =
                new FacesMessage(FacesMessage.SEVERITY_ERROR, FacesMessage.SEVERITY_ERROR
                    .toString(), createExpectedMessageString(Messages
                    .getMessageAsString(E_INVALID_PARAMETER), "Initialize"));

        try {
            // テスト実行
            page.initialize();
            fail("例外が発生していない");
        } catch (InvalidOperationRuntimeException e) {
            ServiceAbortException actual = (ServiceAbortException)e.getCause();
            assertEquals(ApplicationMessageCode.E_INVALID_PARAMETER, actual.getMessageCode());
        }
    }

    /**
     * 初期化アクションを検証する. 権限がない
     * @throws Exception
     */
    @Test
    public void testInitializeInvalidPermission() throws Exception {
        page.setId(1L);

        // 権限がない
        MockAbstractPage.IS_SYSTEM_ADMIN = false;
        MockAbstractPage.IS_PROJECT_ADMIN = false;
        MockCustomFieldService.RET_FIND = new CustomField();
        MockCustomFieldService.RET_FIND_CUSTOM_FIELD_VALUE = new ArrayList<CustomFieldValue>();

        // 期待されるメッセージをセット
        FacesContextMock.EXPECTED_CLIENT_ID = null;
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
