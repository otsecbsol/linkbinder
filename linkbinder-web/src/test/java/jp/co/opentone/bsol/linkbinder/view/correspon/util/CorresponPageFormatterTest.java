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
package jp.co.opentone.bsol.linkbinder.view.correspon.util;

import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import jp.co.opentone.bsol.framework.test.AbstractTestCase;
import jp.co.opentone.bsol.linkbinder.dto.Correspon;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.Workflow;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowType;
import jp.co.opentone.bsol.linkbinder.util.view.correspon.CorresponPageFormatter;


/**
 * {@link CorresponPageFormatter}のテストケース.
 * @author opentone
 */
public class CorresponPageFormatterTest extends AbstractTestCase {

    /**
     * テストに使用するコレポン文書.
     */
    private Correspon correspon;
    private CorresponPageFormatter formatter;

    @Before
    public void setup() {
        correspon = new Correspon();
        formatter = new CorresponPageFormatter(correspon);
    }

    /**
     * {@link CorresponPageFormatter#getCorresponNo()}の検証.
     * コレポン文書番号が未割り当ての場合
     */
    @Test
    public void testGetCorresponNo() {
        correspon.setCorresponNo(null);
        assertEquals("(発行後に採番されます)", formatter.getCorresponNo());
    }

    /**
     * {@link CorresponPageFormatter#getCorresponNo()}の検証.
     * コレポン文書番号が割り当て済の場合
     */
    @Test
    public void testGetCorresponNoDefault() {
        String corresponNo = "YOC:IT-12345";
        correspon.setCorresponNo(corresponNo);
        assertEquals(corresponNo, formatter.getCorresponNo());
    }
    /**
     * {@link CorresponPageFormatter#getReplySubject()}の検証.
     */
    @Test
    public void testGetReplySubject() {
        String subject = "これはてすとです。";
        String expected = "Re: " + subject;
        correspon.setSubject(subject);

        assertEquals(expected, formatter.getReplySubject());
    }

    /**
     * {@link CorresponPageFormatter#getReplySubject()}の検証.
     * 返信文書ヘッダと、blockquoteタグにより引用を表現する文字列が返されるか確認する.
     */
    @Test
    public void testGetReplyBody() {
        String body =
            "<p>これは1行目です</p>\n<p>&nbsp;</p>\n<ol>\n<li>aaa</li>\n<li>bbb</li>\n<p>end</p>\n";
        correspon.setBody(body);

        // 発行日、承認者を返信ヘッダーに表示するので値を準備
        Date issuedAt = new GregorianCalendar(2009, 7, 5, 9, 9, 12).getTime(); // 5-Aug-2009
        User checker = new User(); // 返信文書ヘッダには関係ないが、Approverが正しく取得できるかを確認するために必要
        checker.setEmpNo("ZZA00");
        checker.setNameE("Checker");

        User approver = new User();
        approver.setEmpNo("ZZA99");
        approver.setNameE("Taro Yamada");

        List<Workflow> workflows = new ArrayList<Workflow>();
        Workflow w;
        w = new Workflow();
        w.setWorkflowType(WorkflowType.CHECKER);
        w.setUser(checker);
        workflows.add(w);

        w = new Workflow();
        w.setWorkflowType(WorkflowType.APPROVER);
        w.setUser(approver);
        workflows.add(w);

        correspon.setIssuedAt(issuedAt);
        correspon.setWorkflows(workflows);

        String actual = formatter.getReplyBody();

        assertTrue(approver.getEmpNo(), actual.contains(approver.getEmpNo()));
        assertTrue(approver.getNameE(), actual.contains(approver.getNameE()));

        String strIssuedAt =
            new SimpleDateFormat("yyyy-MM-dd HH:mm").format(issuedAt);
        assertTrue(strIssuedAt, actual.contains(strIssuedAt));

        assertTrue("<blockquote>", actual.contains("<blockquote"));
        assertTrue("</blockquote>", actual.contains("</blockquote>"));
    }

    /**
     * {@link CorresponPageFormatter#getForwardSubject()}の検証.
     */
    @Test
    public void testGetForwardSubject() {
        String subject = "これはてすとです。";
        String expected = "Fw: " + subject;
        correspon.setSubject(subject);

        assertEquals(expected, formatter.getForwardSubject());
    }

    /**
     * {@link CorresponPageFormatter#getForwardSubject()}の検証.
     * 返信文書ヘッダと、blockquoteタグにより引用を表現する文字列が返されるか確認する.
     */
    @Test
    public void testGetForwardBody() {
        String body =
            "<p>これは1行目です</p>\n<p>&nbsp;</p>\n<ol>\n<li>aaa</li>\n<li>bbb</li>\n<p>end</p>\n";
        correspon.setBody(body);

        // 発行日、承認者を返信ヘッダーに表示するので値を準備
        Date issuedAt = new GregorianCalendar(2009, 7, 5, 9, 9, 12).getTime(); // 5-Aug-2009
        User checker = new User(); // 返信文書ヘッダには関係ないが、Approverが正しく取得できるかを確認するために必要
        checker.setEmpNo("ZZA00");
        checker.setNameE("Checker");

        User approver = new User();
        approver.setEmpNo("ZZA99");
        approver.setNameE("Taro Yamada");

        List<Workflow> workflows = new ArrayList<Workflow>();
        Workflow w;
        w = new Workflow();
        w.setWorkflowType(WorkflowType.CHECKER);
        w.setUser(checker);
        workflows.add(w);

        w = new Workflow();
        w.setWorkflowType(WorkflowType.APPROVER);
        w.setUser(approver);
        workflows.add(w);

        correspon.setIssuedAt(issuedAt);
        correspon.setWorkflows(workflows);

        String actual = formatter.getForwardBody();

        assertTrue(approver.getEmpNo(), actual.contains(approver.getEmpNo()));
        assertTrue(approver.getNameE(), actual.contains(approver.getNameE()));

        String strIssuedAt =
            new SimpleDateFormat("yyyy-MM-dd HH:mm").format(issuedAt);
        assertTrue(strIssuedAt, actual.contains(strIssuedAt));

        assertTrue("<blockquote>", actual.contains("<blockquote"));
        assertTrue("</blockquote>", actual.contains("</blockquote>"));
    }
}
