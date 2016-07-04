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
package jp.co.opentone.bsol.linkbinder.service.correspon.impl;

import static org.junit.Assert.*;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import jp.co.opentone.bsol.framework.core.dao.RecordNotFoundException;
import jp.co.opentone.bsol.framework.test.AbstractTestCase;
import jp.co.opentone.bsol.linkbinder.dao.impl.CorresponReadStatusDaoImpl;
import jp.co.opentone.bsol.linkbinder.dto.Correspon;
import jp.co.opentone.bsol.linkbinder.dto.CorresponReadStatus;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.code.ReadStatus;
import jp.co.opentone.bsol.linkbinder.service.MockAbstractService;
import jp.co.opentone.bsol.linkbinder.service.correspon.CorresponReadStatusService;
import mockit.Mock;
import mockit.MockUp;

/**
 * {@link CorresponReadStatusServiceImpl}のテストケース.
 * @author opentone
 */
public class CorresponReadStatusServiceImplTest extends AbstractTestCase {

    /**
     * テスト対象.
     */
    @Resource
    private CorresponReadStatusService service;

    @BeforeClass
    public static void testSetUp() {
        new MockAbstractService();
    }

    @AfterClass
    public static void testTearDown() {
        new MockAbstractService().tearDown();;
    }

    /**
     * テスト前準備.
     */
    @Before
    public void setUp() {
    }

    /**
     * 後始末.
     */
    @After
    public void tearDown() {
        tearDownMockAbstractService();
    }

    private void tearDownMockAbstractService() {
    }

    /**
     * １コレポン文書既読・未読の状態作成
     */
    @Test
    public void testChangeReadStatusCreate1Check() throws Exception {
        User user = new User();
        user.setEmpNo("E0001");

        Correspon correspon = new Correspon();

        correspon.setId(136L);
        correspon.setUpdatedBy(user);
        CorresponReadStatus cReadStatus = new CorresponReadStatus();
        cReadStatus.setReadStatus(ReadStatus.READ);
        correspon.setCorresponReadStatus(cReadStatus);

        // Mock準備
        MockAbstractService.CURRENT_USER = user;
        new MockUp<CorresponReadStatusDaoImpl>() {
            @Mock CorresponReadStatus findByEmpNo(Long id, String empNo) throws RecordNotFoundException {
                return null;
            }
            @Mock Long create(CorresponReadStatus actual) {
                return 1L;
            }
        };

        assertEquals("1", String.valueOf(service.updateReadStatusById(correspon.getId(), ReadStatus.READ)));
    }

    /**
     * １コレポン文書既読・未読の状態更新(既読→未読)
     */
    @Test
    public void testChangeReadStatusUpdate1Check() throws Exception {
        User user = new User();
        user.setEmpNo("E0002");

        Correspon correspon = new Correspon();

        correspon.setId(136L);
        CorresponReadStatus cReadStatus = new CorresponReadStatus();
        cReadStatus.setId(41L);
        correspon.setCorresponReadStatus(cReadStatus);
        correspon.setUpdatedBy(user);

        CorresponReadStatus read = new CorresponReadStatus();
        read.setId(41L);
        read.setCorresponId(136L);
        read.setUpdatedBy(user);
        read.setReadStatus(ReadStatus.NEW);

        // Mock準備
        MockAbstractService.CURRENT_USER = user;
        new MockUp<CorresponReadStatusDaoImpl>() {
            @Mock CorresponReadStatus findByEmpNo(Long id, String empNo) throws RecordNotFoundException {
                return read;
            }
            @Mock int update(CorresponReadStatus actual) {
                return 1;
            }
        };

        assertEquals("41", String.valueOf(service.updateReadStatusById(correspon.getId(), ReadStatus.NEW)));
    }
}
