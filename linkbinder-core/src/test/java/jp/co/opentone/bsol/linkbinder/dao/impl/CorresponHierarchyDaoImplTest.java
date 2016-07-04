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

import javax.annotation.Resource;

import org.junit.Test;

import jp.co.opentone.bsol.framework.core.dao.KeyDuplicateException;
import jp.co.opentone.bsol.framework.test.AbstractDaoTestCase;
import jp.co.opentone.bsol.linkbinder.dto.CorresponHierarchy;
import jp.co.opentone.bsol.linkbinder.dto.User;

public class CorresponHierarchyDaoImplTest extends AbstractDaoTestCase {

    @Resource
    private CorresponHierarchyDaoImpl dao;

    /**
     * {@link CorresponHierarchyDaoImpl#create(jp.co.opentone.bsol.linkbinder.dto.CorresponHierarchy)}
     * のテストケース. コレポン文書新規登録の検証.
     * @throws Exception
     */
    @Test
    public void testCreate() throws Exception {
        // ログインユーザーの準備
        User login = new User();
        login.setEmpNo("ZZA01");

        // 登録内容の準備
        CorresponHierarchy c = new CorresponHierarchy();
        c.setParentCorresponId(18L);
        c.setChildCorresponId(19L);
        c.setReplyAddressUserId(20L);
        c.setCreatedBy(login);
        c.setUpdatedBy(login);

        // 登録
        Long id = new Long(0L);

        // id（自動採番）を最大値にするためのループ
        while (true) {
            try {
                id = dao.create(c);
                break;
            } catch (KeyDuplicateException e) {
                continue;
            }
        }

        assertNotNull(id);

        CorresponHierarchy actual = dao.findById(id);

        assertEquals(id, actual.getId());
        assertEquals(c.getParentCorresponId(), actual.getParentCorresponId());
        assertEquals(c.getChildCorresponId(), actual.getChildCorresponId());
        assertEquals(c.getReplyAddressUserId(), actual.getReplyAddressUserId());
        assertEquals(c.getCreatedBy().getEmpNo(), actual.getCreatedBy().getEmpNo());
        assertEquals(c.getUpdatedBy().getEmpNo(), actual.getUpdatedBy().getEmpNo());
        assertEquals(Long.valueOf(0L), actual.getDeleteNo());
        assertNotNull(actual.getCreatedAt());
        assertNotNull(actual.getUpdatedAt());
    }

    /**
     * {@link CorresponHierarchyDaoImpl#countByParentCorresponIdReplyAddressUserId(jp.co.opentone.bsol.linkbinder.dto.CorresponHierarchy)}
     * のテストケース.
     * @throws Exception
     */
    @Test
    public void testCountByParentCorresponIdReplyAddressUserId() throws Exception {
        int count = dao.countByParentCorresponIdReplyAddressUserId(12L,14L);
        assertEquals(1, count);
    }

}
