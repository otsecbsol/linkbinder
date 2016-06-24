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
package jp.co.opentone.bsol.linkbinder.service.common.impl;

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

import jp.co.opentone.bsol.framework.core.dao.KeyDuplicateException;
import jp.co.opentone.bsol.framework.core.dao.RecordNotFoundException;
import jp.co.opentone.bsol.framework.core.dao.StaleRecordException;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.test.AbstractTestCase;
import jp.co.opentone.bsol.linkbinder.dao.impl.FavoriteFilterDaoImpl;
import jp.co.opentone.bsol.linkbinder.dto.FavoriteFilter;
import jp.co.opentone.bsol.linkbinder.dto.Project;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchFavoriteFilterCondition;
import jp.co.opentone.bsol.linkbinder.service.MockAbstractService;
import mockit.Mock;
import mockit.MockUp;

/**
 * FavoriteFilterService のテストクラス.
 * @author opentone
 */
public class FavoriteFilterServiceImplTest extends AbstractTestCase {

    /**
     * テスト対象.
     */
    @Resource
    private FavoriteFilterServiceImpl target;

    @BeforeClass
    public static void setupOnce() {
        new MockAbstractService();
    }

    @AfterClass
    public static void teardownOnce() {
        new MockAbstractService().tearDown();
    }
    @Before
    public void setup() {
        User user = new User();
        user.setEmpNo("TST01");
        MockAbstractService.CURRENT_USER = user;
        MockAbstractService.CURRENT_PROJECT_ID = "PJ1";
        MockAbstractService.IS_SYSTEM_ADMIN = true;
    }

    @After
    public void tearDown() {
    }

    /**
     * 例外を投げるモックをセットアップ.
     * @param type 1: 1番目の例外を投げるモック、2： 2番目の例外を投げるモック
     */
    private void setExceptionMock(int type) {
        switch(type) {
        case 1:
            //TODO JMockitバージョンアップ対応
//            Mockit.redefineMethods(AbstractDao.class, MockAbstractDaoException1.class);
            break;
        case 2:
            //TODO JMockitバージョンアップ対応
//            Mockit.redefineMethods(AbstractDao.class, MockAbstractDaoException2.class);
            break;
        default:
        }
    }

    /**
     * search のテスト.
     * dao のメソッドを正しく呼んでいる事を確認。
     * @throws Exception 例外
     */
    @Test
    public void testSearchNormal01() throws Exception {
        // prepare
        SearchFavoriteFilterCondition param
            = new SearchFavoriteFilterCondition(new Project(), new User());

        new MockUp<FavoriteFilterDaoImpl>() {
            @Mock List<FavoriteFilter> find(SearchFavoriteFilterCondition actual) {
                assertEquals("empNo not equals.", param.getEmpNo(), actual.getEmpNo());
                assertEquals("projectId not equals.", param.getProjectId(), actual.getProjectId());

                return new ArrayList<>();
            }
        };

        // execute
        this.target.search(param);
    }
    /**
     * search のテスト.
     * 引数のNULL検証を実施している事。
     * @throws Exception 例外
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSearchAbnormal01() throws Exception {
        // prepare
        // execute
        this.target.search(null);
        // verify
        fail("exception must be occured.");
    }

    /**
     * find のテスト.
     * dao のメソッドを正しく呼んでいる事を確認。
     * @throws Exception 例外
     */
    @Test
    public void testFindNormal01() throws Exception {
        // prepare
        Long id = 10L;
        new MockUp<FavoriteFilterDaoImpl>() {
            @Mock FavoriteFilter findById(Long id) {
                assertThat(id, is(10L));
                return newFavoriteFilter();
            }
        };

        // execute
        this.target.find(id);
    }
    /**
     * find のテスト.
     * Dao で発生した例外(RecordNotFoundException) を ServiceAbortException でラップしている事を確認。
     * @throws Exception 例外
     */
    @Test(expected = ServiceAbortException.class)
    public void testFindAbnormal01() throws Exception {
        // prepare
        // 例外モックをセットアップ
        Long id = 10L;
        new MockUp<FavoriteFilterDaoImpl>() {
            @Mock FavoriteFilter findById(Long id) throws RecordNotFoundException {
                throw new RecordNotFoundException();
            }
        };

        // execute
        this.target.find(id);
        // verify
        fail("exception must be occured.");
    }

    /**
     * create のテスト.
     * dao のメソッドを正しく呼んでいる事を確認。
     * @throws Exception 例外
     */
    @Test
    public void testSaveNewNormal01() throws Exception {
        // prepare
        FavoriteFilter p = new FavoriteFilter();
        new MockUp<FavoriteFilterDaoImpl>() {
            @Mock Long create(FavoriteFilter actual) {
                assertEquals("id not equals.", p.getId(), actual.getId());
                assertEquals("projectId not equals.", p.getProjectId(), actual.getProjectId());
                assertEquals("favorite name", p.getFavoriteName(), actual.getFavoriteName());
                assertEquals("search conditions",
                        p.getSearchConditions(),
                        actual.getSearchConditions());
                assertEquals("createUser.", MockAbstractService.CURRENT_USER.getEmpNo(), actual
                        .getCreatedBy()
                        .getEmpNo());
                assertEquals("updateUser.", MockAbstractService.CURRENT_USER.getEmpNo(), actual
                        .getUpdatedBy()
                        .getEmpNo());

                return 1L;
            }
        };

        // execute
        this.target.save(p);
    }
    /**
     * create のテスト.
     * Dao で発生した例外 (KeyDuplicateException) を ServiceAbortException でラップしている事を確認。
     * @throws Exception 例外
     */
    @Test
    public void testSaveNewAbnormal01() throws Exception {
        // prepare
        // 例外モックをセットアップ
        FavoriteFilter param = new FavoriteFilter();
        new MockUp<FavoriteFilterDaoImpl>() {
            @Mock Long create(FavoriteFilter actual) throws KeyDuplicateException {
                throw new KeyDuplicateException();
            }
        };

        try {
            // execute
            this.target.save(param);
        } catch (ServiceAbortException e) {
            return;
        }
        fail("exception must be occured.");
    }

    /**
     * update のテスト.
     * dao のメソッドを正しく呼んでいる事を確認。
     * @throws Exception 例外
     */
    @Test
    public void testSaveUpdateNormal01() throws Exception {
        // prepare
        FavoriteFilter p = new FavoriteFilter();
        p.setId(1L);
        new MockUp<FavoriteFilterDaoImpl>() {
            @Mock int update(FavoriteFilter actual) {
                assertEquals("id not equals.", p.getId(), actual.getId());
                assertEquals("projectId not equals.", p.getProjectId(), actual.getProjectId());
                assertEquals("favorite name", p.getFavoriteName(), actual.getFavoriteName());
                assertEquals("search conditions",
                        p.getSearchConditions(),
                        actual.getSearchConditions());
                assertEquals("currentUser.", MockAbstractService.CURRENT_USER.getEmpNo(), actual
                        .getUpdatedBy()
                        .getEmpNo());
                return 1;
            }
        };

        // execute
        this.target.save(p);
    }
    /**
     * update のテスト.
     * Dao で発生した例外(KeyDuplicateException)を ServiceAbortException でラップしている事を確認。
     * @throws Exception 例外
     */
    @Test
    public void testSaveUpdateAbnormal01() throws Exception {
        // prepare
        // 例外モックをセットアップ
        FavoriteFilter param = new FavoriteFilter();
        param.setId(1L);
        new MockUp<FavoriteFilterDaoImpl>() {
            @Mock int update(FavoriteFilter actual) throws KeyDuplicateException {
                throw new KeyDuplicateException();
            }
        };

        try {
            // execute
            this.target.save(param);
        } catch (ServiceAbortException e) {
            return;
        }
        fail("exception must be occured.");
    }
    /**
     * update のテスト.
     * Dao で発生した例外(StaleRecordException)を ServiceAbortException でラップしている事を確認。
     * @throws Exception 例外
     */
    @Test
    public void testSaveUpdateAbnormal02() throws Exception {
        // prepare
        // 例外モックをセットアップ
        FavoriteFilter param = new FavoriteFilter();
        param.setId(1L);
        new MockUp<FavoriteFilterDaoImpl>() {
            @Mock int update(FavoriteFilter actual)
                    throws KeyDuplicateException, StaleRecordException {
                throw new StaleRecordException();
            }
        };
        try {
            // execute
            this.target.save(param);
        } catch (ServiceAbortException e) {
            return;
        }
        // verify
        fail("exception must be occured.");
    }

    /**
     * delete のテスト.
     * dao のメソッドを正しく呼んでいる事を確認。
     * @throws Exception 例外
     */
    @Test
    public void testDeleteNormal01() throws Exception {
        // prepare
        Long id = 99L;
        FavoriteFilter dto = new FavoriteFilter();
        dto.setId(id);
        new MockUp<FavoriteFilterDaoImpl>() {
            @Mock int delete(FavoriteFilter actual)
                    throws KeyDuplicateException, StaleRecordException {
                assertEquals("parameter is not equals.", id, actual.getId());
                assertEquals("currentUser.", MockAbstractService.CURRENT_USER.getEmpNo(), actual
                        .getUpdatedBy()
                        .getEmpNo());
                return 1;
            }
        };

        // execute
        this.target.delete(dto);
    }
    /**
     * delete のテスト.
     * Dao で発生した例外(KeyDupulicateException)を ServiceAbortException でラップしている事を確認。
     * @throws Exception 例外
     */
    @Test
    public void testDeleteAbnormal02() throws Exception {
        // prepare
        // 例外モックをセットアップ
        FavoriteFilter dto = new FavoriteFilter();
        dto.setId(99L);
        new MockUp<FavoriteFilterDaoImpl>() {
            @Mock int delete(FavoriteFilter actual)
                    throws KeyDuplicateException, StaleRecordException {
                throw new KeyDuplicateException();
            }
        };

        try {
            // execute
            this.target.delete(dto);
        } catch (ServiceAbortException e) {
            return;
        }
        // verify
        fail("exception must be occured.");
    }
    /**
     * delete のテスト.
     * Dao で発生した例外(StaleRecordException)を ServiceAbortException でラップしている事を確認。
     * @throws Exception 例外
     */
    @Test
    public void testDeleteAbnormal03() throws Exception {
        // prepare
        // 例外モックをセットアップ
        FavoriteFilter dto = new FavoriteFilter();
        dto.setId(99L);
        new MockUp<FavoriteFilterDaoImpl>() {
            @Mock int delete(FavoriteFilter actual)
                    throws KeyDuplicateException, StaleRecordException {
                throw new StaleRecordException();
            }
        };

        try {
            // execute
            this.target.delete(dto);
        }catch (ServiceAbortException e) {
            return;
        }
        // verify
        fail("exception must be occured.");
    }


    private FavoriteFilter newFavoriteFilter() {
        FavoriteFilter result = new FavoriteFilter();

        User user = MockAbstractService.CURRENT_USER;
        result.setUser(user);
        result.setCreatedBy(user);
        result.setProjectId(MockAbstractService.CURRENT_PROJECT_ID);

        return result;
    }
}
