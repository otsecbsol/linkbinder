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

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.annotation.Resource;

import org.dbunit.dataset.ITable;
import org.junit.Test;

import jp.co.opentone.bsol.framework.core.dao.KeyDuplicateException;
import jp.co.opentone.bsol.framework.core.dao.StaleRecordException;
import jp.co.opentone.bsol.framework.core.extension.ibatis.DBValue;
import jp.co.opentone.bsol.framework.core.extension.ibatis.exception.InvalidNullUpdatedRuntimeException;
import jp.co.opentone.bsol.framework.test.AbstractDaoTestCase;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.Workflow;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowProcessStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowType;

/**
 * {@link WorkflowDaoImpl}のテストケース.
 * @author opentone
 */
public class WorkflowDaoImplTest extends AbstractDaoTestCase {

    /**
     * テスト対象.
     */
    @Resource
    private WorkflowDaoImpl dao;

    /**
     * 該当データがある場合の検証.
     * @throws Exception
     */
    @Test
    public void testFindByCorresponId() throws Exception {
        List<Workflow> aus = dao.findByCorresponId(1L);

        assertNotNull(aus);
        assertDataSetEquals(newDataSet("WorkflowDaoImplTest_testFindByCorresponId_expected.xls"),
                            aus);

    }

    /**
     * 該当データが無い場合の検証.
     * @throws Exception
     */
    @Test
    public void testFindByCorresponIdEmpty() throws Exception {
        assertEquals(0, dao.findByCorresponId(-1L).size());
    }

    /**
     * 1件レコード取得の検証.
     * @throws Exception
     */
    @Test
    public void testFindById() throws Exception {
        Workflow w = dao.findById(1L);

        assertNotNull(w);
        assertDataSetEquals(newDataSet("WorkflowDaoImplTest_testFindById_expected.xls"), w);
    }

    /**
     * 登録の検証.
     * @throws Exception
     */
    @Test
    public void testCreate() throws Exception {
        // ログインユーザーの準備
        User login = new User();
        login.setEmpNo("ZZA01");

        // 登録内容の準備
        Workflow w = new Workflow();
        w.setCorresponId(1L);
        w.setWorkflowNo(2L);

        User u = new User();
        u.setEmpNo("ZZA02");
        w.setUser(u);

        w.setWorkflowType(WorkflowType.CHECKER);
        w.setWorkflowProcessStatus(WorkflowProcessStatus.NONE);

        w.setCreatedBy(login);
        w.setUpdatedBy(login);

        w.setCommentOn(DBValue.STRING_NULL);
        w.setFinishedAt(DBValue.DATE_NULL);

        // 登録
        Long id = null;
        while (true) {
            try {
                id = dao.create(w);
                break;
            } catch (KeyDuplicateException e) {
                continue;
            }
        }
        assertNotNull(id);

        Workflow actual = dao.findById(id);

        assertEquals(id, actual.getId());
        assertEquals(w.getCorresponId(), actual.getCorresponId());
        assertEquals(w.getWorkflowNo(), actual.getWorkflowNo());
        assertEquals(w.getUser().getEmpNo(), actual.getUser().getEmpNo());
        assertEquals(w.getWorkflowType(), actual.getWorkflowType());
        assertEquals(w.getWorkflowProcessStatus(), actual.getWorkflowProcessStatus());
        assertEquals(w.getCreatedBy().getEmpNo(), actual.getCreatedBy().getEmpNo());
        assertEquals(w.getUpdatedBy().getEmpNo(), actual.getUpdatedBy().getEmpNo());
        assertEquals(Long.valueOf(1L), actual.getVersionNo());
        assertEquals(Long.valueOf(0L), actual.getDeleteNo());
        assertNotNull(actual.getCreatedAt());
        assertNotNull(actual.getUpdatedAt());
    }

    /**
     * 登録の検証(コメント付).
     * @throws Exception
     */
    @Test
    public void testCreate1() throws Exception {
        // ログインユーザーの準備
        User login = new User();
        login.setEmpNo("ZZA01");

        // 登録内容の準備
        Workflow w = new Workflow();
        w.setCorresponId(1L);
        w.setWorkflowNo(2L);

        User u = new User();
        u.setEmpNo("ZZA02");
        w.setUser(u);

        w.setWorkflowType(WorkflowType.CHECKER);
        w.setWorkflowProcessStatus(WorkflowProcessStatus.NONE);
        w.setCommentOn("test Comment");

        w.setCreatedBy(login);
        w.setUpdatedBy(login);

        w.setFinishedAt(DBValue.DATE_NULL);

        // 登録
        Long id = null;
        while (true) {
            try {
                id = dao.create(w);
                break;
            } catch (KeyDuplicateException e) {
                continue;
            }
        }
        assertNotNull(id);

        Workflow actual = dao.findById(id);

        assertEquals(id, actual.getId());
        assertEquals(w.getCorresponId(), actual.getCorresponId());
        assertEquals(w.getWorkflowNo(), actual.getWorkflowNo());
        assertEquals(w.getUser().getEmpNo(), actual.getUser().getEmpNo());
        assertEquals(w.getWorkflowType(), actual.getWorkflowType());
        assertEquals(w.getWorkflowProcessStatus(), actual.getWorkflowProcessStatus());
        assertEquals(w.getCommentOn(), actual.getCommentOn());
        assertEquals(w.getCreatedBy().getEmpNo(), actual.getCreatedBy().getEmpNo());
        assertEquals(w.getUpdatedBy().getEmpNo(), actual.getUpdatedBy().getEmpNo());
        assertEquals(Long.valueOf(1L), actual.getVersionNo());
        assertEquals(Long.valueOf(0L), actual.getDeleteNo());
        assertNotNull(actual.getCreatedAt());
        assertNotNull(actual.getUpdatedAt());
    }

    /**
     * コレポン文書IDでの削除処理の検証.
     * @throws Exception
     */
    @Test
    public void testDeleteByCorresponId() throws Exception {
        Long corresponId = 1L;
        User user = new User();
        user.setEmpNo("ZZA01");
        user.setNameE("Taro Yamada");

        //削除前のデータを取得
        List<Workflow> before = dao.findByCorresponId(corresponId);

        Integer actual = dao.deleteByCorresponId(corresponId,user);

        String sql = "select * from workflow where correspon_id = 1 and delete_no != 0 and delete_no != 1 order by workflow_no";
        ITable after = getConnection().createQueryTable("actual", sql);

        assertNotNull(actual);
        assertEquals(Integer.valueOf(3), actual);
        assertTrue(dao.findByCorresponId(corresponId).isEmpty());
        assertFalse(dao.findByCorresponId(2L).isEmpty());

        assertEquals(before.get(0).getUser().getEmpNo(), after.getValue(0, "emp_no"));
        assertEquals(before.get(0).getWorkflowType().getValue().toString(), after.getValue(0, "workflow_type").toString());
        assertEquals(before.get(0).getWorkflowNo().toString(), after.getValue(0, "workflow_no").toString());
        assertEquals(before.get(0).getWorkflowProcessStatus().getValue().toString(), after.getValue(0, "workflow_process_status").toString());
        assertEquals(before.get(0).getCommentOn(), after.getValue(0, "comment_on"));
        assertEquals(before.get(0).getFinishedBy().getEmpNo(), after.getValue(0, "finished_by"));
        assertEquals(before.get(0).getFinishedAt(), after.getValue(0, "finished_at"));
        assertEquals(before.get(0).getCreatedBy().getEmpNo(), after.getValue(0, "created_by").toString());
        assertEquals(before.get(0).getCreatedAt(), after.getValue(0, "created_at"));
        assertEquals(user.getEmpNo(), after.getValue(0, "updated_by").toString());
        assertNotNull(after.getValue(0, "updated_at"));
        assertTrue(String.valueOf((before.get(0).getVersionNo() + 1)).equals(after.getValue(0, "version_no").toString()));
        assertTrue(!after.getValue(0, "delete_no").toString().equals(String.valueOf(0)));

        assertEquals(before.get(1).getUser().getEmpNo(), after.getValue(1, "emp_no"));
        assertEquals(before.get(1).getWorkflowType().getValue().toString(), after.getValue(1, "workflow_type").toString());
        assertEquals(before.get(1).getWorkflowNo().toString(), after.getValue(1, "workflow_no").toString());
        assertEquals(before.get(1).getWorkflowProcessStatus().getValue().toString(), after.getValue(1, "workflow_process_status").toString());
        assertEquals(before.get(1).getCommentOn(), after.getValue(1, "comment_on"));
        assertEquals(before.get(1).getFinishedBy(), after.getValue(1, "finished_by"));
        assertEquals(before.get(1).getFinishedAt(), after.getValue(1, "finished_at"));
        assertEquals(before.get(1).getCreatedBy().getEmpNo(), after.getValue(1, "created_by").toString());
        assertEquals(before.get(1).getCreatedAt(), after.getValue(1, "created_at"));
        assertEquals(user.getEmpNo(), after.getValue(1, "updated_by").toString());
        assertNotNull(after.getValue(1, "updated_at"));
        assertTrue(String.valueOf((before.get(1).getVersionNo() + 1)).equals(after.getValue(1, "version_no").toString()));
        assertTrue(!after.getValue(1, "delete_no").toString().equals(String.valueOf(0)));

        assertEquals(before.get(2).getUser().getEmpNo(), after.getValue(2, "emp_no"));
        assertEquals(before.get(2).getWorkflowType().getValue().toString(), after.getValue(2, "workflow_type").toString());
        assertEquals(before.get(2).getWorkflowNo().toString(), after.getValue(2, "workflow_no").toString());
        assertEquals(before.get(2).getWorkflowProcessStatus().getValue().toString(), after.getValue(2, "workflow_process_status").toString());
        assertEquals(before.get(2).getCommentOn(), after.getValue(2, "comment_on"));
        assertEquals(before.get(2).getFinishedBy(), after.getValue(2, "finished_by"));
        assertEquals(before.get(2).getFinishedAt(), after.getValue(2, "finished_at"));
        assertEquals(before.get(2).getCreatedBy().getEmpNo(), after.getValue(2, "created_by").toString());
        assertEquals(before.get(2).getCreatedAt(), after.getValue(2, "created_at"));
        assertEquals(user.getEmpNo(), after.getValue(2, "updated_by").toString());
        assertNotNull(after.getValue(2, "updated_at"));
        assertTrue(String.valueOf((before.get(2).getVersionNo() + 1)).equals(after.getValue(2, "version_no").toString()));
        assertTrue(!after.getValue(2, "delete_no").toString().equals(String.valueOf(0)));
    }

    /**
     * コレポン文書IDでの削除処理の検証.
     * @throws Exception
     */
    @Test
    public void testDeleteByCorresponIdWorkflowNo() throws Exception {
        Long corresponId = 1L;
        User user = new User();
        user.setEmpNo("ZZA01");
        user.setNameE("Taro Yamada");

        //削除前のデータを取得
        List<Workflow> before = dao.findByCorresponId(corresponId);

        Integer actual = dao.deleteByCorresponIdWorkflowNo(corresponId, 2L, user);

        String sql = "select * from workflow where correspon_id = 1 and delete_no != 0 and delete_no != 1 order by workflow_no";
        ITable after = getConnection().createQueryTable("actual", sql);

        assertNotNull(actual);
        assertEquals(Integer.valueOf(1), actual);

        //削除されなかったデータを取得
        List<Workflow> notDelete = dao.findByCorresponId(corresponId);

        assertEquals(before.get(0).getUser().getEmpNo(), notDelete.get(0).getUser().getEmpNo());
        assertEquals(before.get(0).getWorkflowType().getValue().toString(), notDelete.get(0).getWorkflowType().getValue().toString());
        assertEquals(before.get(0).getWorkflowNo().toString(), notDelete.get(0).getWorkflowNo().toString());
        assertEquals(before.get(0).getWorkflowProcessStatus().getValue().toString(), notDelete.get(0).getWorkflowProcessStatus().getValue().toString());
        assertEquals(before.get(0).getCommentOn(), notDelete.get(0).getCommentOn());
        assertEquals(before.get(0).getFinishedBy().getEmpNo(), notDelete.get(0).getFinishedBy().getEmpNo());
        assertEquals(before.get(0).getFinishedAt(), notDelete.get(0).getFinishedAt());
        assertEquals(before.get(0).getCreatedBy().getEmpNo(), notDelete.get(0).getCreatedBy().getEmpNo());
        assertEquals(before.get(0).getCreatedAt(), notDelete.get(0).getCreatedAt());
        assertEquals(user.getEmpNo(), notDelete.get(0).getUpdatedBy().getEmpNo());
        assertNotNull(notDelete.get(0).getUpdatedAt());
        assertTrue((before.get(0).getVersionNo()).equals(notDelete.get(0).getVersionNo()));
        assertTrue(notDelete.get(0).getDeleteNo().toString().equals(String.valueOf(0)));

        assertEquals(before.get(1).getUser().getEmpNo(), notDelete.get(1).getUser().getEmpNo());
        assertEquals(before.get(1).getWorkflowType().getValue().toString(), notDelete.get(1).getWorkflowType().getValue().toString());
        assertEquals(before.get(1).getWorkflowNo().toString(), notDelete.get(1).getWorkflowNo().toString());
        assertEquals(before.get(1).getWorkflowProcessStatus().getValue().toString(), notDelete.get(1).getWorkflowProcessStatus().getValue().toString());
        assertEquals(before.get(1).getCommentOn(), notDelete.get(1).getCommentOn());
        assertNull(notDelete.get(1).getFinishedBy());
        assertNull(notDelete.get(1).getFinishedAt());
        assertEquals(before.get(1).getCreatedBy().getEmpNo(), notDelete.get(1).getCreatedBy().getEmpNo());
        assertEquals(before.get(1).getCreatedAt(), notDelete.get(1).getCreatedAt());
        assertEquals(user.getEmpNo(), notDelete.get(1).getUpdatedBy().getEmpNo());
        assertNotNull(notDelete.get(1).getUpdatedAt());
        assertTrue((before.get(1).getVersionNo()).equals(notDelete.get(1).getVersionNo()));
        assertTrue(notDelete.get(1).getDeleteNo().toString().equals(String.valueOf(0)));

        // 削除されたデータ
        assertEquals(before.get(2).getUser().getEmpNo(), after.getValue(0, "emp_no"));
        assertEquals(before.get(2).getWorkflowType().getValue().toString(), after.getValue(0, "workflow_type").toString());
        assertEquals(before.get(2).getWorkflowNo().toString(), after.getValue(0, "workflow_no").toString());
        assertEquals(before.get(2).getWorkflowProcessStatus().getValue().toString(), after.getValue(0, "workflow_process_status").toString());
        assertEquals(before.get(2).getCommentOn(), after.getValue(0, "comment_on"));
        assertEquals(before.get(2).getFinishedBy(), after.getValue(0, "finished_by"));
        assertEquals(before.get(2).getFinishedAt(), after.getValue(0, "finished_at"));
        assertEquals(before.get(2).getCreatedBy().getEmpNo(), after.getValue(0, "created_by").toString());
        assertEquals(before.get(2).getCreatedAt(), after.getValue(0, "created_at"));
        assertEquals(user.getEmpNo(), after.getValue(0, "updated_by").toString());
        assertNotNull(after.getValue(0, "updated_at"));
        assertTrue(String.valueOf((before.get(2).getVersionNo() + 1)).equals(after.getValue(0, "version_no").toString()));
        assertTrue(!after.getValue(0, "delete_no").toString().equals(String.valueOf(0)));

    }

    /**
     * 更新の検証. 更新者、更新日、バージョンナンバー、承認状態以外は更新しない
     * @throws Exception
     */
    @Test
    public void testUpdate1() throws Exception {
        // ログインユーザーの準備
        User login = new User();
        login.setEmpNo("ZZA01");

        Long workflowId = 2L;
        // 更新内容の準備
        Workflow w = new Workflow();
        w.setCorresponId(1L);
        w.setWorkflowNo(2L);

        User u = new User();
        u.setEmpNo("ZZA02");
        w.setUser(u);

        w.setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_CHECK);
        w.setId(workflowId);
        Workflow beforeActual = dao.findById(workflowId);

        w.setUpdatedBy(login);
        w.setVersionNo(beforeActual.getVersionNo());

        // 更新
        int updateRecord = dao.update(w);
        assertNotNull(updateRecord);
        assertTrue(updateRecord == 1);

        Workflow afterActual = dao.findById(workflowId);

        assertEquals(workflowId, afterActual.getId());
        assertEquals(beforeActual.getCorresponId(), afterActual.getCorresponId());
        assertEquals(w.getUser().getEmpNo(), afterActual.getUser().getEmpNo());
        assertEquals(beforeActual.getWorkflowNo(), afterActual.getWorkflowNo());
        assertEquals(w.getWorkflowProcessStatus(), afterActual.getWorkflowProcessStatus());
        assertEquals(beforeActual.getCreatedBy().getEmpNo(), afterActual.getCreatedBy().getEmpNo());
        assertEquals(beforeActual.getCreatedAt(), afterActual.getCreatedAt());
        assertEquals(w.getUpdatedBy().getEmpNo(), afterActual.getUpdatedBy().getEmpNo());
        assertEquals(Long.valueOf(beforeActual.getVersionNo() + 1), afterActual.getVersionNo());
        assertEquals(beforeActual.getDeleteNo(), afterActual.getDeleteNo());
        assertNotNull(afterActual.getUpdatedAt());

    }

    /**
     * 更新の検証null可のカラムにnullをセット.
     * @throws Exception
     */
    @Test
    public void testUpdate2() throws Exception {
        // ログインユーザーの準備
        User login = new User();
        login.setEmpNo("ZZA01");

        // 更新内容の準備
        Workflow w = new Workflow();

        User u = new User();
        u.setEmpNo("ZZA02");
        w.setUser(u);

        w.setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_CHECK);
        Long workflowId = 3L;
        w.setId(workflowId);
        Workflow beforeActual = dao.findById(workflowId);

        w.setUpdatedBy(login);
        w.setVersionNo(beforeActual.getVersionNo());

        w.setCommentOn(DBValue.STRING_NULL);
        w.setFinishedAt(DBValue.DATE_NULL);
        u = new User();
        u.setEmpNo(DBValue.STRING_NULL);

        w.setFinishedBy(u);

        // 更新
        int updateRecord = dao.update(w);
        assertNotNull(updateRecord);
        assertTrue(updateRecord == 1);

        Workflow afterActual = dao.findById(workflowId);

        assertEquals(workflowId, afterActual.getId());
        assertEquals(beforeActual.getCorresponId(), afterActual.getCorresponId());
        assertEquals(w.getUser().getEmpNo(), afterActual.getUser().getEmpNo());
        assertEquals(beforeActual.getWorkflowNo(), afterActual.getWorkflowNo());
        assertEquals(w.getWorkflowProcessStatus(), afterActual.getWorkflowProcessStatus());
        assertEquals(beforeActual.getCreatedBy().getEmpNo(), afterActual.getCreatedBy().getEmpNo());
        assertEquals(beforeActual.getCreatedAt(), afterActual.getCreatedAt());
        assertEquals(w.getUpdatedBy().getEmpNo(), afterActual.getUpdatedBy().getEmpNo());
        assertEquals(Long.valueOf(w.getVersionNo() + 1), afterActual.getVersionNo());
        assertEquals(beforeActual.getDeleteNo(), afterActual.getDeleteNo());
        assertNotNull(afterActual.getUpdatedAt());
        assertNull(afterActual.getCommentOn());
        assertNull(afterActual.getFinishedAt());
        assertNull(afterActual.getFinishedBy());

    }

    /**
     * 更新の検証 更新者、更新日、バージョンナンバー以外は更新しない
     * @throws Exception
     */
    @Test
    public void testUpdate3() throws Exception {
        Long workflowId = 3L;
        Workflow beforeActual = dao.findById(workflowId);
        // ログインユーザーの準備
        User login = new User();
        login.setEmpNo("ZZA01");

        // 更新内容の準備
        Workflow w = new Workflow();
        w.setVersionNo(beforeActual.getVersionNo());
        w.setId(workflowId);

        w.setUpdatedBy(login);

        // 更新
        int updateRecord = dao.update(w);
        assertNotNull(updateRecord);
        assertTrue(updateRecord == 1);

        Workflow afterActual = dao.findById(workflowId);

        assertEquals(workflowId, afterActual.getId());
        assertEquals(beforeActual.getCorresponId(), afterActual.getCorresponId());
        assertEquals(beforeActual.getUser().getEmpNo(), afterActual.getUser().getEmpNo());
        assertEquals(beforeActual.getWorkflowNo(), afterActual.getWorkflowNo());
        assertEquals(beforeActual.getWorkflowProcessStatus(),
                     afterActual.getWorkflowProcessStatus());
        assertEquals(beforeActual.getCommentOn(), afterActual.getCommentOn());
        assertEquals(beforeActual.getFinishedBy().getEmpNo(), afterActual.getFinishedBy()
                                                                         .getEmpNo());
        assertEquals(beforeActual.getFinishedAt(), afterActual.getFinishedAt());
        assertEquals(beforeActual.getCreatedBy().getEmpNo(), afterActual.getCreatedBy().getEmpNo());
        assertEquals(beforeActual.getCreatedAt(), afterActual.getCreatedAt());
        assertEquals(w.getUpdatedBy().getEmpNo(), afterActual.getUpdatedBy().getEmpNo());
        assertEquals(Long.valueOf(w.getVersionNo() + 1), afterActual.getVersionNo());
        assertEquals(beforeActual.getDeleteNo(), afterActual.getDeleteNo());
        assertNotNull(afterActual.getUpdatedAt());

    }

    /**
     * 更新の検証 null不可のカラムにnullをセットした場合 {@link InvalidNullUpdatedRuntimeException}
     * が発生することを検証する.
     * @throws Exception
     */
    @Test(expected = InvalidNullUpdatedRuntimeException.class)
    public void testUpdate4() throws Exception {
        Long workflowId = 3L;
        // ログインユーザーの準備
        User login = new User();
        login.setEmpNo("ZZA01");

        // 更新内容の準備
        Workflow w = new Workflow();
        w.setVersionNo(0L);
        w.setId(workflowId);
        w.setCreatedAt(DBValue.DATE_NULL);

        w.setUpdatedBy(login);

        dao.update(w);
        fail("例外が発生していない");

    }

    /**
     * 排他チェックにかかった場合、{@link StaleRecordException}が発生することを検証する.
     * @throws Exception
     */
    @Test(expected = StaleRecordException.class)
    public void testUpdateVersionNo() throws Exception {
        Long workflowId = 3L;
        // ログインユーザーの準備
        User login = new User();
        login.setEmpNo("ZZA01");

        // 更新内容の準備
        Workflow w = new Workflow();
        w.setVersionNo(-1L);
        w.setId(workflowId);

        w.setUpdatedBy(login);

        dao.update(w);
        fail("例外が発生していない");
    }

    /**
     * 更新の検証. Deniedの編集.
     * @throws Exception
     */
    @Test
    public void testUpdateByCorresponId() throws Exception {
        // 更新内容の準備
        Workflow w = new Workflow();
        w.setCorresponId(1L);

        User finish = new User();

        w.setWorkflowProcessStatus(WorkflowProcessStatus.NONE);
        w.setFinishedBy(finish);
        w.setFinishedAt(DBValue.DATE_NULL);

        User user = new User();
        user.setEmpNo("ZZA05");
        user.setNameE("Keiichi Ogiwara");

        w.setUpdatedBy(user);
        w.setUpdatedAt(new GregorianCalendar(2009, 3, 2, 2, 2, 2).getTime()); // 適用されない

        w.setVersionNo(0L);

        // 更新
        int actual = dao.updateByCorresponId(w);
        assertNotNull(actual);
        assertTrue(actual == 3);

        // 更新後の内容
        List<Workflow> result = dao.findByCorresponId(w.getCorresponId());

        assertDataSetEquals(newDataSet("WorkflowDaoImplTest_testUpdateByCorresponId_expected.xls"),
                            result);

        // 更新日はExcelのデータから更新されており、勝手に指定できない。
        Date testDate = new GregorianCalendar(2009, 3, 2, 10, 40, 30).getTime();
        assertNotSame(testDate, result.get(0).getUpdatedAt());
        assertNotSame(w.getUpdatedAt(), result.get(0).getUpdatedAt());

    }

    /**
     * 承認作業状態更新の検証.
     * コレポン文書番号、検証者種別を条件に複数のレコードを更新する
     * @throws Exception
     */
    @Test
    public void testUpdateWorkflowProcessStatusesByCorresponIdAndWorkflowType() throws Exception {
        // 更新内容の準備
        User user = new User();
        user.setEmpNo("ZZA05");
        user.setNameE("Keiichi Ogiwara");

        WorkflowProcessStatus currentStatus = WorkflowProcessStatus.NONE;
        Workflow w = new Workflow();
        //  更新値
        w.setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_CHECK);
        w.setUpdatedBy(user);
        w.setUpdatedAt(new GregorianCalendar(2009, 3, 2, 2, 2, 2).getTime()); // 適用されない
        //  更新条件
        w.setCorresponId(4L);
        w.setWorkflowType(WorkflowType.CHECKER);

        // 更新
        int actual = dao.updateWorkflowProcessStatusesByCorresponIdAndWorkflowType(w, currentStatus);
        assertNotNull(actual);
        // テストデータ
        //   ・Checker  4人 うち承認作業状態がNoneの人は2人
        //   ・Approver 1人
        assertTrue(String.format("count=%d", actual), actual == 2);

        // 更新後の内容
        List<Workflow> result = dao.findByCorresponId(w.getCorresponId());

        assertDataSetEquals(
            newDataSet("WorkflowDaoImplTest_testUpdateWorkflowProcessStatusesByCorresponIdAndWorkflowType_expected.xls"),
            result);

        // 更新日はExcelのデータから更新されており、勝手に指定できない。
        Date testDate = new GregorianCalendar(2009, 3, 2, 10, 40, 30).getTime();
        assertNotSame(testDate, result.get(0).getUpdatedAt());
        assertNotSame(w.getUpdatedAt(), result.get(0).getUpdatedAt());

    }

    /**
     * 承認作業状態更新の検証.
     * IDを条件に複数のレコードを更新する
     * @throws Exception
     */
    @Test
    public void testUpdateWorkflowProcessStatusById() throws Exception {
        // 更新内容の準備
        Long corresponId = 4L;
        User user = new User();
        user.setEmpNo("ZZA05");
        user.setNameE("Keiichi Ogiwara");

        WorkflowProcessStatus currentStatus = WorkflowProcessStatus.NONE;
        Workflow w = new Workflow();
        //  更新値
        w.setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_CHECK);
        w.setUpdatedBy(user);
        w.setUpdatedAt(new GregorianCalendar(2009, 3, 2, 2, 2, 2).getTime()); // 適用されない
        //  更新条件
        w.setId(13L);

        // 更新
        int actual = dao.updateWorkflowProcessStatusById(w, currentStatus);
        assertNotNull(actual);
        assertTrue(String.format("count=%d", actual), actual == 1);

        //  更新条件のIDを変更して更新
        //  ただしこのレコードは承認作業状態がNoneではないので更新数に0が返されるはず
        w.setId(12L);
        // 更新
        actual = dao.updateWorkflowProcessStatusById(w, currentStatus);
        assertNotNull(actual);
        assertTrue(String.format("count=%d", actual), actual == 0);

        // 更新後の内容
        List<Workflow> result = dao.findByCorresponId(corresponId);

        assertDataSetEquals(
            newDataSet("WorkflowDaoImplTest_testUpdateWorkflowProcessStatusById_expected.xls"),
            result);

        // 更新日はExcelのデータから更新されており、勝手に指定できない。
        Date testDate = new GregorianCalendar(2009, 3, 2, 10, 40, 30).getTime();
        assertNotSame(testDate, result.get(0).getUpdatedAt());
        assertNotSame(w.getUpdatedAt(), result.get(0).getUpdatedAt());

    }
}
