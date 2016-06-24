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

import static org.junit.Assert.*;

import java.util.List;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.extension.jsf.FacesContextMock;
import jp.co.opentone.bsol.framework.test.AbstractTestCase;
import jp.co.opentone.bsol.framework.web.view.flash.Flash;
import jp.co.opentone.bsol.linkbinder.dto.Correspon;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroup;
import jp.co.opentone.bsol.linkbinder.dto.CorresponType;
import jp.co.opentone.bsol.linkbinder.dto.CustomField;
import jp.co.opentone.bsol.linkbinder.dto.CustomFieldValue;
import jp.co.opentone.bsol.linkbinder.dto.ProjectUser;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCustomFieldCondition;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchUserCondition;
import jp.co.opentone.bsol.linkbinder.service.admin.impl.CorresponGroupServiceImpl;
import jp.co.opentone.bsol.linkbinder.service.admin.impl.CorresponTypeServiceImpl;
import jp.co.opentone.bsol.linkbinder.service.admin.impl.CustomFieldServiceImpl;
import jp.co.opentone.bsol.linkbinder.service.admin.impl.UserServiceImpl;
import jp.co.opentone.bsol.linkbinder.service.correspon.impl.CorresponSaveServiceImpl;
import jp.co.opentone.bsol.linkbinder.service.correspon.impl.CorresponServiceImpl;
import jp.co.opentone.bsol.linkbinder.service.correspon.impl.CorresponValidateServiceImpl;
import jp.co.opentone.bsol.linkbinder.view.AbstractPage;
import mockit.Mock;
import mockit.MockUp;

/**
 * {@link CorresponPage}のテストケース.
 *
 * @author opentone
 */
public class CorresponConfirmationPageTest extends AbstractTestCase {

    /**
     * テスト対象.
     */
    @Resource
    private CorresponConfirmationPage page;

    /**
     * テストの前準備.
     */
    @BeforeClass
    public static void testSetUp() {
        FacesContextMock.initialize();
        new MockAbstractPage();
        new MockCustomFieldService();
        new MockCorresponGroupService();
        new MockCorresponTypeService();
        new MockUserService();
        new MockCorresponService();
        new MockCorresponSaveService();
        new MockCorresponValidateService();
    }

    /**
     * 後始末.
     */
    @AfterClass
    public static void testTeardown() {
        new MockAbstractPage().tearDown();
        new MockCustomFieldService().tearDown();
        new MockCorresponGroupService().tearDown();
        new MockCorresponTypeService().tearDown();
        new MockUserService().tearDown();
        new MockCorresponService().tearDown();
        new MockCorresponSaveService().tearDown();
        new MockCorresponValidateService().tearDown();
        FacesContextMock.tearDown();
    }

    @Before
    public void setUp() {
        FacesContextMock.EXPECTED_MESSAGE = null;

        MockCorresponTypeService.FIND_BY_ID = null;
        MockCorresponGroupService.FIND_BY_ID = null;
        MockCorresponService.RET_FIND = null;
        MockCustomFieldService.RET_FIND = null;
        MockCustomFieldService.RET_FIND_VALUE = null;
        MockUserService.RET_FIND = null;
        MockCorresponService.EX_FIND_BY_ID = null;
    }

    @After
    public void tearDown() {
        MockCorresponService.RET_FIND = null;
        MockCorresponTypeService.FIND_BY_ID = null;
        MockCorresponGroupService.FIND_BY_ID = null;
        MockCustomFieldService.RET_FIND = null;
        MockCustomFieldService.RET_FIND_VALUE = null;
        MockUserService.RET_FIND = null;
        MockCorresponService.EX_FIND_BY_ID = null;
    }

    /**
     * 初期化アクションのテスト.
     * @throws Exception
     */
    @Test
    public void testInitialize01() throws Exception {

        Correspon c = new Correspon();
        CorresponType corresponType = new CorresponType();
        corresponType.setId(1L);
        corresponType.setProjectCorresponTypeId(2L);
        corresponType.setName("CorresponTypeName");
        c.setCorresponType(corresponType);

        MockCorresponTypeService.FIND_BY_ID = corresponType;
        CorresponGroup corresponGroup = new CorresponGroup();
        corresponGroup.setId(3L);
        corresponGroup.setName("CorresponGroup");
        c.setFromCorresponGroup(corresponGroup);
        MockCorresponGroupService.FIND_BY_ID = corresponGroup;

        c.setBody("<p>1234567890</p>");

        page.setCorrespon(c);

        page.initialize();

        assertEquals(c.getCorresponType().getProjectCorresponTypeId(),
                     page.getCorrespon().getCorresponType().getProjectCorresponTypeId());
        assertEquals(c.getCorresponType().getName(), page.getCorrespon().getCorresponType().getName());

        assertEquals(c.getFromCorresponGroup().getId(),
                     page.getCorrespon().getFromCorresponGroup().getId());
        assertEquals(c.getFromCorresponGroup().getName(),
                     page.getCorrespon().getFromCorresponGroup().getName());

        assertEquals("<p>1234<wbr/>5678<wbr/>90</p>", page.getDisplayBody());
    }

    /**
     * 初期化アクションのテスト(更新).
     * @throws Exception
     */
    @Test
    public void testInitialize02() throws Exception {
        Correspon c = new Correspon();
        c.setId(100L);
        CorresponType corresponType = new CorresponType();
        corresponType.setId(1L);
        corresponType.setProjectCorresponTypeId(10L);
        corresponType.setName("CorresponTypeName");
        c.setCorresponType(corresponType);

        MockCorresponTypeService.FIND_BY_ID = corresponType;

        CorresponGroup corresponGroup = new CorresponGroup();
        corresponGroup.setId(2L);
        corresponGroup.setName("CorresponGroup");
        c.setFromCorresponGroup(corresponGroup);

        MockCorresponGroupService.FIND_BY_ID = corresponGroup;

        c.setBody("<p>1234567890</p>");

        page.setCorrespon(c);

        page.initialize();

        assertEquals(c.getCorresponType().getProjectCorresponTypeId(),
                     page.getCorrespon().getCorresponType().getProjectCorresponTypeId());
        assertEquals(c.getCorresponType().getName(), page.getCorrespon().getCorresponType().getName());

        assertEquals(c.getFromCorresponGroup().getId(),
                     page.getCorrespon().getFromCorresponGroup().getId());
        assertEquals(c.getFromCorresponGroup().getName(),
                     page.getCorrespon().getFromCorresponGroup().getName());
        assertEquals("<p>1234<wbr/>5678<wbr/>90</p>", page.getDisplayBody());
    }

    /**
     * ステータス表示有無のテスト.
     * @throws Exception
     */
    @Test
    public void testIsDisplayCorresponStatus() {
        page.setCorrespon(null);

        // コレポン文書が無い場合はfalse
        assertFalse(page.isDisplayCorresponStatus());

        // コレポン文書があればtrue
        Correspon c = new Correspon();
        c.setId(100L);
        page.setCorrespon(c);
        assertTrue(page.isDisplayCorresponStatus());

        // 返信文書でもtrue
        c.setParentCorresponId(99L);
        assertTrue(page.isDisplayCorresponStatus());
    }

    @Test
    public void testSave() {
        Flash flash = new Flash();
        flash.setValue("backPage", null);
        MockAbstractPage.RET_CURRENT_PROJECT_ID = "0-1111-2";

        String url = null;

        // ダミーのコレポン文書を作成
        Correspon c = new Correspon();
        c.setId(100L);
        page.setCorrespon(c);

        page.setBackPage(null);
        url = page.save();
        assertEquals("correspon?id=100&projectId=0-1111-2&readMode=UPDATE_READ_STATUS&fromEdit=1", url);
        assertNull(flash.getValue("backPage"));

        page.setBackPage("corresponSearch");
        url = page.save();
        assertEquals("correspon?id=100&projectId=0-1111-2&readMode=UPDATE_READ_STATUS&fromEdit=1", url);
        assertEquals("corresponSearch", flash.getValue("backPage"));
    }

    @Test
    public void testBack() {
        Flash flash = new Flash();
        flash.setValue("backPage", null);

        page.setBackPage(null);
        page.back();
        assertNull(flash.getValue("backPage"));

        page.setBackPage("corresponSearch");
        page.back();
        assertEquals("corresponSearch", flash.getValue("backPage"));
    }

    /**
     * Saveアクションのテスト.
     * @throws Exception
     */
    //FIXME テストが起動できない為、コメントアウト
//    @Test
//    public void testSave01() throws Exception {
//        page.setCorrespon(new Correspon());
//        page.save();
//
//        assertEquals(Long.valueOf(1L), page.getCorrespon().getId());
//    }

    public static class MockAbstractPage extends MockUp<AbstractPage> {
        static String RET_CURRENT_PROJECT_ID;
        @Mock
        public String getCurrentProjectId() {
            return RET_CURRENT_PROJECT_ID;
        }
    }

    public static class MockCustomFieldService extends MockUp<CustomFieldServiceImpl> {
        static List<CustomField> RET_FIND;
        static List<CustomFieldValue> RET_FIND_VALUE;

        @Mock
        public List<CustomField> search(SearchCustomFieldCondition cust) throws ServiceAbortException {
            return RET_FIND;
        }
        @Mock
        public List<CustomFieldValue> findCustomFieldValue(Long id) throws ServiceAbortException {
            return RET_FIND_VALUE;
        }
    }

    public static class MockCorresponGroupService extends MockUp<CorresponGroupServiceImpl> {

        static CorresponGroup FIND_BY_ID;

        @Mock
        public CorresponGroup find(Long id) throws ServiceAbortException {
            return FIND_BY_ID;
        }

    }

    public static class MockCorresponTypeService extends MockUp<CorresponTypeServiceImpl>{
        static CorresponType FIND_BY_ID;

        @Mock
        public CorresponType find(Long id) throws ServiceAbortException {
            return FIND_BY_ID;
        }

        @Mock
        public  CorresponType findByProjectCorresponTypeId(Long projectCorresponTypeId) throws ServiceAbortException {
            return FIND_BY_ID;
        }
    }

    public static class MockUserService extends MockUp<UserServiceImpl> {
        static List<ProjectUser> RET_FIND;

        @Mock
        public List<ProjectUser> search(SearchUserCondition conditionUser) throws ServiceAbortException {
            return RET_FIND;
        }
    }

    public static class MockCorresponService extends MockUp<CorresponServiceImpl> {
        static Correspon RET_FIND;
        static ServiceAbortException EX_FIND_BY_ID;
        @Mock
        public Correspon find(Long id) throws ServiceAbortException {
            if(RET_FIND == null){
                throw EX_FIND_BY_ID;
            }
            return RET_FIND;
        }
    }

    public static class MockCorresponSaveService extends MockUp<CorresponSaveServiceImpl> {
        static ServiceAbortException EX_FIND_BY_ID;
        @Mock
        public Long save(Correspon correspon) throws ServiceAbortException {
            if(correspon == null){
                throw EX_FIND_BY_ID;
            }
            return correspon.getId();
        }
    }

    public static class MockCorresponValidateService extends MockUp<CorresponValidateServiceImpl> {
        @Mock
        public boolean validate(Correspon correspon) throws ServiceAbortException {
            return true;
        }
    }
}
