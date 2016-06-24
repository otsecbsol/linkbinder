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

import java.util.GregorianCalendar;

import javax.annotation.Resource;

import org.junit.Test;

import jp.co.opentone.bsol.framework.core.dao.KeyDuplicateException;
import jp.co.opentone.bsol.framework.core.dao.RecordNotFoundException;
import jp.co.opentone.bsol.framework.core.extension.ibatis.DBValue;
import jp.co.opentone.bsol.framework.test.AbstractDaoTestCase;
import jp.co.opentone.bsol.linkbinder.dao.UserProfileDao;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.UserProfile;


/**
 * {@link UserProfileDaoImpl}のテストケース.
 * @author opentone
 */
public class UserProfileDaoImplTest extends AbstractDaoTestCase {

    /**
     * テスト対象.
     */
    @Resource
    private UserProfileDao dao;


    /**
     * {@link UserProfileDao#create(jp.co.opentone.bsol.linkbinder.dto.UserProfile)}のテスト.
     * @throws Exception
     */
    @Test
    public void testCreate() throws Exception {

        User user = new User();
        user.setEmpNo("ZZA02");

        UserProfile p = new UserProfile();
        p.setId(null);
        p.setUser(user);
        p.setLastLoggedInAt(new GregorianCalendar(2009, 4, 28, 10, 12, 13).getTime());
        p.setCreatedBy(user);
        p.setUpdatedBy(user);
        p.setVersionNo(null);
        p.setFeedKey("1234567890");

        Long id = null;
        while (true) {
            try {
                id = dao.create(p);
                break;
            } catch (KeyDuplicateException e) {
                continue;
            }
        }

        assertNotNull(id);
        UserProfile actual = dao.findById(id);
        assertNotNull(actual);
        assertEquals(id, actual.getId());
        assertEquals(p.getUser().getEmpNo(), actual.getUser().getEmpNo());
        assertEquals(p.getLastLoggedInAt(), actual.getLastLoggedInAt());
        assertEquals(p.getDefaultProjectId(), actual.getDefaultProjectId());
        assertEquals(p.getCorresponInvisibleFields(), actual.getCorresponInvisibleFields());
        assertEquals(p.getCreatedBy().getEmpNo(), p.getCreatedBy().getEmpNo());
        assertNotNull(actual.getCreatedAt());
        assertEquals(p.getUpdatedBy().getEmpNo(), p.getUpdatedBy().getEmpNo());
        assertNotNull(actual.getUpdatedAt());
        assertEquals(1L, actual.getVersionNo().longValue());
        assertEquals(0L, actual.getDeleteNo().longValue());
        assertEquals(p.getFeedKey(), actual.getFeedKey());
    }

    /**
     * {@link UserProfileDao#update(jp.co.opentone.bsol.linkbinder.dto.UserProfile)}のテスト.
     * @throws Exception
     */
    @Test
    public void testUpdate() throws Exception {

        User user = new User();
        user.setEmpNo("ZZA01");

        UserProfile p = new UserProfile();
        p.setId(1L);
        p.setUser(user);
        p.setDefaultProjectId("PJ1");
        p.setCorresponInvisibleFields(DBValue.STRING_NULL);
        p.setLastLoggedInAt(new GregorianCalendar(2009, 4, 28, 10, 12, 13).getTime());
        p.setCreatedBy(user);
        p.setUpdatedBy(user);
        p.setVersionNo(0L);
        p.setFeedKey("zxcvsadf");

        Integer count = dao.update(p);

        assertNotNull(count);
        assertEquals(1, count.intValue());

        UserProfile actual = dao.findById(p.getId());
        assertNotNull(actual);
        assertEquals(p.getId(), actual.getId());
        assertEquals(p.getUser().getEmpNo(), actual.getUser().getEmpNo());
        assertEquals(p.getLastLoggedInAt(), actual.getLastLoggedInAt());
        assertEquals(p.getDefaultProjectId(), actual.getDefaultProjectId());
        assertEquals(null, actual.getCorresponInvisibleFields());
        assertEquals(p.getCreatedBy().getEmpNo(), p.getCreatedBy().getEmpNo());
        assertNotNull(actual.getCreatedAt());
        assertEquals(p.getUpdatedBy().getEmpNo(), p.getUpdatedBy().getEmpNo());
        assertNotNull(actual.getUpdatedAt());
        assertEquals(1L, actual.getVersionNo().longValue());
        assertEquals(0L, actual.getDeleteNo().longValue());
        assertEquals(p.getFeedKey(), actual.getFeedKey());
    }

    /**
     * {@link UserProfileDao#update(jp.co.opentone.bsol.linkbinder.dto.UserProfile)}のテスト.
     * @throws Exception
     */
    @Test
    public void testFindByEmpNo() throws Exception {
        UserProfile profile = dao.findByEmpNo("ZZA01");

        assertNotNull(profile);
        assertDataSetEquals(newDataSet("UserProfileDaoImplTest_testFindByEmpNo_expected.xls"),
                            profile);
    }

    /**
     * {@link UserProfileDao#update(jp.co.opentone.bsol.linkbinder.dto.UserProfile)}のテスト.
     * 削除されたデータ（エラーなし）.
     * @throws Exception
     */
    @Test
    public void testFindByEmpNoDelete() throws Exception {
        UserProfile profile = dao.findByEmpNo("ZZA03");

        assertNull(profile);
    }

    /**
     * {@link UserProfileDao#update(jp.co.opentone.bsol.linkbinder.dto.UserProfile)}のテスト.
     * 該当なし（エラーなし）.
     * @throws Exception
     */
    @Test
    public void testFindByEmpNoNoRecord() throws Exception {
        UserProfile profile = dao.findByEmpNo("XXXXX");

        assertNull(profile);
    }


    /**
     * {@link jp.co.opentone.bsol.linkbinder.dao.impl.ProjectUserProfileDaoImpl
     *         #findByEmpNo(java.lang.String)} のためのテスト・メソッド.
     */
    @Test
    public void testFindById() throws Exception {
        UserProfile profile = dao.findById(1L);

        assertNotNull(profile);
        assertDataSetEquals(newDataSet("UserProfileDaoImplTest_testFindById_expected.xls"),
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
}
