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

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import jp.co.opentone.bsol.framework.core.dao.RecordNotFoundException;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.test.AbstractTestCase;
import jp.co.opentone.bsol.linkbinder.dao.impl.CorresponDaoImpl;
import jp.co.opentone.bsol.linkbinder.dao.impl.CorresponGroupDaoImpl;
import jp.co.opentone.bsol.linkbinder.dao.impl.ParentCorresponNoSeqDaoImpl;
import jp.co.opentone.bsol.linkbinder.dao.impl.ReplyCorresponNoSeqDaoImpl;
import jp.co.opentone.bsol.linkbinder.dto.Company;
import jp.co.opentone.bsol.linkbinder.dto.Correspon;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroup;
import jp.co.opentone.bsol.linkbinder.dto.Discipline;
import jp.co.opentone.bsol.linkbinder.dto.ParentCorresponNoSeq;
import jp.co.opentone.bsol.linkbinder.dto.ProjectUser;
import jp.co.opentone.bsol.linkbinder.dto.ReplyCorresponNoSeq;
import jp.co.opentone.bsol.linkbinder.dto.Site;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.condition.ParentCorresponNoSeqCondition;
import jp.co.opentone.bsol.linkbinder.service.MockAbstractService;
import mockit.Mock;
import mockit.MockUp;

/**
 * {@link CorresponSequenceServiceImpl}のテストケース.
 * @author opentone
 */
public class CorresponSequenceServiceImplTest extends AbstractTestCase {

    /**
     * テスト対象.
     */
    @Resource
    private CorresponSequenceServiceImpl service;

    @BeforeClass
    public static void testSetUp() {
        new MockAbstractService();
    }

    @AfterClass
    public static void testTeardown() {
        new MockAbstractService().tearDown();
    }

    /**
     * コレポン文書番号を採番する. 親文書番号 -- 採番テーブルにレコードなし.
     * @throws ServiceAbortException
     */
    @Test
    public void testGetCorresponNoParent_1() throws ServiceAbortException {
        Site site = new Site();
        site.setId(1L);
        site.setSiteCd("SITE");
        Company company = new Company();
        company.setId(2L);
        company.setCompanyCd("COMPANY");
        Discipline discipline = new Discipline();
        discipline.setId(3L);
        discipline.setDisciplineCd("DISCIPLINE");

        ProjectUser projectUser = new ProjectUser();
        projectUser.setProjectCompany(company);

        CorresponGroup corresponGroup = new CorresponGroup();
        corresponGroup.setId(1L);
        corresponGroup.setSite(site);
        corresponGroup.setDiscipline(discipline);
        List<CorresponGroup> list = new ArrayList<CorresponGroup>();
        list.add(corresponGroup);

        User user = new User();
        user.setEmpNo("80001");

        Correspon correspon = new Correspon();
        correspon.setParentCorresponId(null);
        correspon.setProjectId("PJ1");
        correspon.setCreatedBy(user);
        correspon.setFromCorresponGroup(new CorresponGroup());

        // Mock準備
        MockAbstractService.PROJECT_USER = projectUser;
        new MockUp<ParentCorresponNoSeqDaoImpl>() {
            @Mock ParentCorresponNoSeq findForUpdate(ParentCorresponNoSeqCondition condition) {
                return null;
            }
            @Mock Long create(ParentCorresponNoSeq actual) {

                return 1L;
            }
        };
        new MockUp<CorresponGroupDaoImpl>() {
            @Mock CorresponGroup findById(Long id) throws RecordNotFoundException {
                return list.get(0);
            }
        };

        String actual = service.getCorresponNo(correspon);
        assertEquals("SITE:DISCIPLINE-00001", actual);
    }

    /**
     * コレポン文書番号を採番する. 親文書番号 -- 採番テーブルにレコードあり.
     * @throws ServiceAbortException
     */
    @Test
    public void testGetCorresponNoParent_2() throws ServiceAbortException {
        Site site = new Site();
        site.setId(1L);
        site.setSiteCd("SITE");
        Company company = new Company();
        company.setId(2L);
        company.setCompanyCd("COMPANY");
        Discipline discipline = new Discipline();
        discipline.setId(3L);
        discipline.setDisciplineCd("DISCIPLINE");

        ProjectUser projectUser = new ProjectUser();
        projectUser.setProjectCompany(company);

        ParentCorresponNoSeq parentCorresponNoSeq = new ParentCorresponNoSeq();
        parentCorresponNoSeq.setNo(199L);

        CorresponGroup corresponGroup = new CorresponGroup();
        corresponGroup.setId(1L);
        corresponGroup.setSite(site);
        corresponGroup.setDiscipline(discipline);
        List<CorresponGroup> list = new ArrayList<CorresponGroup>();
        list.add(corresponGroup);

        User user = new User();
        user.setEmpNo("80001");

        Correspon correspon = new Correspon();
        correspon.setParentCorresponId(null);
        correspon.setProjectId("PJ1");
        correspon.setCreatedBy(user);
        correspon.setFromCorresponGroup(new CorresponGroup());

        // Mock準備
        MockAbstractService.PROJECT_USER = projectUser;
        new MockUp<ParentCorresponNoSeqDaoImpl>() {
            @Mock ParentCorresponNoSeq findForUpdate(ParentCorresponNoSeqCondition condition) {
                return parentCorresponNoSeq;
            }
            @Mock int update(ParentCorresponNoSeq actual) {
                return 1;
            }
        };
        new MockUp<CorresponGroupDaoImpl>() {
            @Mock CorresponGroup findById(Long id) throws RecordNotFoundException {
                return list.get(0);
            }
        };

        String actual = service.getCorresponNo(correspon);

        assertEquals("SITE:DISCIPLINE-00200", actual);
    }

    /**
     * コレポン文書番号を採番する. 返信文書番号 -- 採番テーブルにレコードなし.
     * @throws ServiceAbortException
     */
    @Test
    public void testGetCorresponNoReply_1() throws ServiceAbortException {
        User user = new User();
        user.setEmpNo("80001");

        Correspon correspon = new Correspon();
        correspon.setParentCorresponId(99L);

        Correspon corresponTop = new Correspon();
        corresponTop.setId(90L);
        corresponTop.setCorresponNo("SITE:COMPANY:DISCIPLINE-00900");
        corresponTop.setCreatedBy(user);

        // Mock準備
        new MockUp<ReplyCorresponNoSeqDaoImpl>() {
            @Mock ReplyCorresponNoSeq findForUpdate(Long id) {
                return null;
            }
            @Mock Long create(ReplyCorresponNoSeq actual) {
                return 1L;
            }
        };
        new MockUp<CorresponDaoImpl>() {
            @Mock Correspon findTopParent(Long id) throws RecordNotFoundException {
                return corresponTop;
            }
        };

        String actual = service.getCorresponNo(correspon);

        assertEquals("SITE:COMPANY:DISCIPLINE-00900-001", actual);
    }

    /**
     * コレポン文書番号を採番する. 返信文書番号 -- 採番テーブルにレコードあり.
     * @throws ServiceAbortException
     */
    @Test
    public void testGetCorresponNoReply_2() throws ServiceAbortException {
        User user = new User();
        user.setEmpNo("80001");

        Correspon correspon = new Correspon();
        correspon.setParentCorresponId(99L);

        Correspon corresponTop = new Correspon();
        corresponTop.setId(90L);
        corresponTop.setCorresponNo("SITE:COMPANY:DISCIPLINE-00900");
        corresponTop.setCreatedBy(user);

        ReplyCorresponNoSeq replyCorresponNoSeq = new ReplyCorresponNoSeq();
        replyCorresponNoSeq.setNo(99L);

        // Mock準備
        new MockUp<ReplyCorresponNoSeqDaoImpl>() {
            @Mock ReplyCorresponNoSeq findForUpdate(Long id) {
                return replyCorresponNoSeq;
            }
            @Mock int update(ReplyCorresponNoSeq actual) {
                return 1;
            }
        };
        new MockUp<CorresponDaoImpl>() {
            @Mock Correspon findTopParent(Long id) throws RecordNotFoundException {
                return corresponTop;
            }
        };

        String actual = service.getCorresponNo(correspon);

        assertEquals("SITE:COMPANY:DISCIPLINE-00900-100", actual);
    }

    /**
     * コレポン文書番号を採番する. 引数がNULL.
     * @throws ServiceAbortException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetCorresponNoReplyNull() throws ServiceAbortException {
        service.getCorresponNo(null);
        fail("例外が発生していない");
    }
}
