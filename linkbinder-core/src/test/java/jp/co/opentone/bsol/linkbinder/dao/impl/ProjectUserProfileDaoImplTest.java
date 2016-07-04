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
package jp.co.opentone.bsol.linkbinder.dao.impl;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;

import jp.co.opentone.bsol.framework.core.dao.KeyDuplicateException;
import jp.co.opentone.bsol.framework.core.dao.RecordNotFoundException;
import jp.co.opentone.bsol.framework.test.AbstractDaoTestCase;
import jp.co.opentone.bsol.linkbinder.dao.ProjectUserProfileDao;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroup;
import jp.co.opentone.bsol.linkbinder.dto.ProjectUserProfile;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchProjectUserProfileCondition;

/**
 * ProjectUserProfileDaoImplのテスト.
 * @author opentone
 */
public class ProjectUserProfileDaoImplTest extends AbstractDaoTestCase {

    /**
     * テスト対象.
     */
    @Resource
    private ProjectUserProfileDao dao;

    /**
     * {@link jp.co.opentone.bsol.linkbinder.dao.impl.ProjectUserProfileDaoImpl
     *         #findByEmpNo(java.lang.String)} のためのテスト・メソッド.
     */
    @Test
    public void testFindById() throws Exception {
        ProjectUserProfile profile = dao.findById(1L);

        assertNotNull(profile);
        assertDataSetEquals(newDataSet("ProjectUserProfileDaoImplTest_testFindById_expected.xls"),
                            profile);
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.dao.impl.ProjectUserProfileDaoImpl
     *         #findByEmpNo(java.lang.String)} のためのテスト・メソッド.
     * 削除されたレコード（エラー）.
     */
    @Test(expected=RecordNotFoundException.class)
    public void testFindByIdDelete() throws Exception {
        dao.findById(3L);
        fail("例外が発生していない");
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.dao.impl.ProjectUserProfileDaoImpl
     *         #findByEmpNo(java.lang.String)} のためのテスト・メソッド.
     * 該当なし（エラー）.
     */
    @Test(expected=RecordNotFoundException.class)
    public void testFindByIdNoRecord() throws Exception {
        dao.findById(-1L);
        fail("例外が発生していない");
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.dao.impl.ProjectUserProfileDaoImpl
     *         #testFind(jp.co.opentone.bsol.linkbinder.dto.condition.SearchProjectUserProfileCondition)}
     * のためのテスト・メソッド.
     */
    @Test
    public void testFind() throws Exception {
        SearchProjectUserProfileCondition condition = new SearchProjectUserProfileCondition();
        condition.setEmpNo("ZZA01");
        condition.setProjectId("PJ1");
        condition.setRole("LE");
        ProjectUserProfile profile = dao.find(condition);

        assertNotNull(profile);
        assertDataSetEquals(newDataSet("ProjectUserProfileDaoImplTest_testFind_expected.xls"),
                            profile);
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.dao.impl.ProjectUserProfileDaoImpl
     *         #testFind(jp.co.opentone.bsol.linkbinder.dto.condition.SearchProjectUserProfileCondition)}
     * のためのテスト・メソッド.
     * 削除されたレコード（エラーなし）.
     */
    @Test
    public void testFindDelete() throws Exception {
        SearchProjectUserProfileCondition condition = new SearchProjectUserProfileCondition();
        condition.setEmpNo("ZZA03");
        condition.setProjectId("PJ1");
        condition.setProjectId("LE");
        ProjectUserProfile profile = dao.find(condition);

        assertNull(profile);
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.dao.impl.ProjectUserProfileDaoImpl
     *         #testFind(jp.co.opentone.bsol.linkbinder.dto.condition.SearchProjectUserProfileCondition)}
     * のためのテスト・メソッド.
     * 該当なし（エラーなし）.
     */
    @Test
    public void testFindNoRecord() throws Exception {
        SearchProjectUserProfileCondition condition = new SearchProjectUserProfileCondition();
        condition.setEmpNo("XXXXX");
        condition.setProjectId("PJ1");
        condition.setProjectId("LE");
        ProjectUserProfile profile = dao.find(condition);

        assertNull(profile);
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.dao.impl.ProjectUserProfileDaoImpl
     *         findList(jp.co.opentone.bsol.linkbinder.dto.condition.SearchProjectUserProfileCondition)}
     * のためのテスト・メソッド.
     */
    @Test
    public void testFindList() throws Exception {
        SearchProjectUserProfileCondition condition = new SearchProjectUserProfileCondition();
        condition.setDefaultCorresponGroupId(2L);
        List<ProjectUserProfile> profile = dao.findList(condition);

        assertNotNull(profile);
        assertDataSetEquals(newDataSet("ProjectUserProfileDaoImplTest_testFindList_expected.xls"),
                            profile);
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.dao.impl.ProjectUserProfileDaoImpl
     *         findList(jp.co.opentone.bsol.linkbinder.dto.condition.SearchProjectUserProfileCondition)}
     * のためのテスト・メソッド.
     * 削除されたレコード（エラーなし）.
     */
    @Test
    public void testFindListDelete() throws Exception {
        SearchProjectUserProfileCondition condition = new SearchProjectUserProfileCondition();
        condition.setDefaultCorresponGroupId(1L);
        List<ProjectUserProfile> profile = dao.findList(condition);

        assertNotNull(profile);
        assertEquals(0, profile.size());
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.dao.impl.ProjectUserProfileDaoImpl
     *         findList(jp.co.opentone.bsol.linkbinder.dto.condition.SearchProjectUserProfileCondition)}
     * のためのテスト・メソッド.
     * 該当なし（エラーなし）.
     */
    @Test
    public void testFindListNoRecord() throws Exception {
        SearchProjectUserProfileCondition condition = new SearchProjectUserProfileCondition();
        condition.setDefaultCorresponGroupId(-1L);
        List<ProjectUserProfile> profile = dao.findList(condition);

        assertNotNull(profile);
        assertEquals(0, profile.size());
    }
    /**
     * {@link jp.co.opentone.bsol.linkbinder.dao.AbstractDao
     *         #create.framework.core.daomework.core.dao.Entity)} のためのテスト・メソッド.
     */
    @Test
    public void testCreate() throws Exception {

        User user = new User();
        user.setEmpNo("ZZA04");

        ProjectUserProfile p = new ProjectUserProfile();
        p.setId(null);
        p.setUser(user);
        p.setProjectId("PJ1");
        p.setRole("Role");
        CorresponGroup group = new CorresponGroup();
        group.setId(1L);
        p.setDefaultCorresponGroup(group);
        p.setCreatedBy(user);
        p.setUpdatedBy(user);

        Long id = null;
        while (true) {
            try {
                id = dao.create(p);
                break;
            } catch (KeyDuplicateException e) {
                continue;
            } catch (Exception e) {
                fail("例外が発生" + e.getMessage());
                break;
            }
        }

        assertNotNull(id);
        ProjectUserProfile actual = dao.findById(id);
        assertNotNull(actual);
        assertEquals(id, actual.getId());
        assertEquals(p.getUser().getEmpNo(), actual.getUser().getEmpNo());
        assertEquals(p.getProjectId(), actual.getProjectId());
        assertEquals(p.getRole(), actual.getRole());
        assertEquals(p.getDefaultCorresponGroup().getId(), actual.getDefaultCorresponGroup().getId());
        assertEquals(p.getCreatedBy().getEmpNo(), p.getCreatedBy().getEmpNo());
        assertNotNull(actual.getCreatedAt());
        assertEquals(p.getUpdatedBy().getEmpNo(), p.getUpdatedBy().getEmpNo());
        assertNotNull(actual.getUpdatedAt());
        assertEquals(0L, actual.getDeleteNo().longValue());
    }


    /**
     * {@link jp.co.opentone.bsol.linkbinder.dao.AbstractDao
     *         #c.framework.core.daoc.framework.core.dao.Entity)} のためのテスト・メソッド.
     * KEYが重複.
     */
    @Test(expected=KeyDuplicateException.class)
    public void testCreateDuplicate() throws Exception {

        User user = new User();
        user.setEmpNo("ZZA02"); // すでにあるユーザー

        ProjectUserProfile p = new ProjectUserProfile();
        p.setId(null);
        p.setUser(user);
        p.setProjectId("PJ1");
        p.setRole("Role");
        CorresponGroup group = new CorresponGroup();
        group.setId(1L);
        p.setDefaultCorresponGroup(group);
        p.setCreatedBy(user);
        p.setUpdatedBy(user);

        dao.create(p);
        fail("例外が発生していない");
    }

    @Test
    public void testUpdate() throws Exception {

        User user = new User();
        user.setEmpNo("ZZA01");

        ProjectUserProfile p = new ProjectUserProfile();
        p.setId(1L);
        p.setUser(user);
        p.setProjectId("PJ1");
        p.setRole("Role");
        CorresponGroup group = new CorresponGroup();
        group.setId(1L);
        p.setDefaultCorresponGroup(group);
        p.setCreatedBy(user);
        p.setUpdatedBy(user);

        Integer count = dao.update(p);

        assertNotNull(count);
        assertEquals(1, count.intValue());

        ProjectUserProfile actual = dao.findById(p.getId());
        assertNotNull(actual);
        assertEquals(p.getId(), actual.getId());
        assertEquals(p.getUser().getEmpNo(), actual.getUser().getEmpNo());
        assertEquals(p.getProjectId(), actual.getProjectId());
        assertEquals(p.getRole(), actual.getRole());
        assertEquals(p.getDefaultCorresponGroup().getId(), actual.getDefaultCorresponGroup().getId());
        assertEquals(p.getCreatedBy().getEmpNo(), p.getCreatedBy().getEmpNo());
        assertNotNull(actual.getCreatedAt());
        assertEquals(p.getUpdatedBy().getEmpNo(), p.getUpdatedBy().getEmpNo());
        assertNotNull(actual.getUpdatedAt());
        assertEquals(0L, actual.getDeleteNo().longValue());
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.dao.AbstractDao
     * .framework.core.daoe(Entity)} のためのテスト・メソッド.
     * NULL設定.
     */
    @Test
    public void testUpdateNullColumn() throws Exception {

        User user = new User();
        user.setEmpNo("ZZA01");

        ProjectUserProfile p = new ProjectUserProfile();
        p.setId(1L);
        p.setNullColumn("default_correspon_group_id");
        p.setUpdatedBy(user);

        Integer count = dao.update(p);

        assertNotNull(count);
        assertEquals(1, count.intValue());

        ProjectUserProfile actual = dao.findById(p.getId());
        assertNotNull(actual);
        assertEquals(p.getId(), actual.getId());
        assertNull(actual.getDefaultCorresponGroup());
        assertEquals(p.getUpdatedBy().getEmpNo(), p.getUpdatedBy().getEmpNo());
        assertNotNull(actual.getUpdatedAt());
        assertEquals(0L, actual.getDeleteNo().longValue());
    }


    /**
     * {@link jp.co.opentone.bsol.linkbinder.dao.AbstractDao
  .framework.core.daoupdate(Entity)} のためのテスト・メソッド.
     * KEYが重複.
     */
    @Test(expected=KeyDuplicateException.class)
    public void testUpdateDuplicate() throws Exception {

        User user = new User();
        user.setEmpNo("ZZA02"); // すでにあるユーザー

        ProjectUserProfile p = new ProjectUserProfile();
        p.setId(1L);
        p.setUser(user);
        p.setProjectId("PJ1");
        p.setRole("Role");
        CorresponGroup group = new CorresponGroup();
        group.setId(1L);
        p.setDefaultCorresponGroup(group);
        p.setCreatedBy(user);
        p.setUpdatedBy(user);

        dao.update(p);
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.dao.AbstractDa.framework.core.dao    #update(Entity)} のためのテスト・メソッド.
     * 該当なし（エラーなし）.
     */
    @Test
    public void testUpdateNoRecord() throws Exception {

        User user = new User();
        user.setEmpNo("ZZA02");

        ProjectUserProfile p = new ProjectUserProfile();
        p.setId(-1L);
        p.setUser(user);
        p.setProjectId("PJ1");
        p.setRole("Role");
        CorresponGroup group = new CorresponGroup();
        group.setId(1L);
        p.setDefaultCorresponGroup(group);
        p.setCreatedBy(user);
        p.setUpdatedBy(user);

        Integer count = dao.update(p);

        assertNotNull(count);
        assertEquals(0, count.intValue());
    }
}
