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

import org.dbunit.dataset.ITable;
import org.junit.Test;

import jp.co.opentone.bsol.framework.core.dao.KeyDuplicateException;
import jp.co.opentone.bsol.framework.core.dao.RecordNotFoundException;
import jp.co.opentone.bsol.framework.core.dao.StaleRecordException;
import jp.co.opentone.bsol.framework.test.AbstractDaoTestCase;
import jp.co.opentone.bsol.linkbinder.dto.CorresponType;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.WorkflowPattern;
import jp.co.opentone.bsol.linkbinder.dto.code.AllowApproverToBrowse;
import jp.co.opentone.bsol.linkbinder.dto.code.ForceToUseWorkflow;
import jp.co.opentone.bsol.linkbinder.dto.code.UseWhole;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponTypeCondition;

/**
 * {@link CorresponTypeDaoImpl}を検証する.
 * @author opentone
 */
public class CorresponTypeDaoImplTest extends AbstractDaoTestCase {

    /**
     * テスト対象.
     */
    @Resource
    private CorresponTypeDaoImpl dao;

    /**
     * {@link CorresponTypeDaoImpl#find(jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponTypeCondition)}
     * のテストケース. プロジェクト指定.
     * @throws Exception
     */
    @Test
    public void testFindProject() throws Exception {
        SearchCorresponTypeCondition condition = new SearchCorresponTypeCondition();
        condition.setProjectId("PJ1");

        // 1ページ10行の2ページ目 = 11～20
        condition.setPageNo(2);
        condition.setPageRowNum(10);

        List<CorresponType> result = dao.find(condition);

        // 取得したオブジェクトと、Excelファイルに定義済の期待値を比較する
        assertNotNull(result);
        assertDataSetEquals(newDataSet("CorresponTypeDaoImplTest_testFindProject_expected.xls"),
            result);
    }

    /**
     * {@link CorresponTypeDaoImpl#find(jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponTypeCondition)}
     * のテストケース. プロジェクト指定で検索条件あり.
     * @throws Exception
     */
    @Test
    public void testFindProjectSearch() throws Exception {
        SearchCorresponTypeCondition condition = new SearchCorresponTypeCondition();
        condition.setProjectId("PJ1");
        condition.setCorresponType("Answer");// 前方一致検索
        condition.setName("A");// 前方一致検索

        // 1ページ10行の1ページ目 = 1～10
        condition.setPageNo(1);
        condition.setPageRowNum(10);

        List<CorresponType> result = dao.find(condition);

        // 取得したオブジェクトと、Excelファイルに定義済の期待値を比較する
        assertNotNull(result);
        assertDataSetEquals(newDataSet("CorresponTypeDaoImplTest_testFindProjectSearch_expected.xls"),
            result);
    }

    /**
     * {@link CorresponTypeDaoImpl#find(jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponTypeCondition)}
     * のテストケース. UseWhole.ALL指定.
     * @throws Exception
     */
    @Test
    public void testFindWhole() throws Exception {
        SearchCorresponTypeCondition condition = new SearchCorresponTypeCondition();
        condition.setUseWhole(UseWhole.ALL);

        // 1ページ5行の3ページ目 = 11～15
        condition.setPageNo(3);
        condition.setPageRowNum(5);

        List<CorresponType> result = dao.find(condition);

        // 取得したオブジェクトと、Excelファイルに定義済の期待値を比較する
        assertNotNull(result);
        assertDataSetEquals(newDataSet("CorresponTypeDaoImplTest_testFindWhole_expected.xls"),
            result);
    }

    /**
     * {@link CorresponTypeDaoImpl#find(jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponTypeCondition)}
     * のテストケース. UseWhole.ALL指定で検索条件あり.
     * @throws Exception
     */
    @Test
    public void testFindWholeSearch() throws Exception {
        SearchCorresponTypeCondition condition = new SearchCorresponTypeCondition();
        condition.setUseWhole(UseWhole.ALL);
        condition.setCorresponType("Confirm");// 前方一致検索
        condition.setName("B");// 前方一致検索

        // 1ページ5行の1ページ目 = 1～5
        condition.setPageNo(1);
        condition.setPageRowNum(5);

        List<CorresponType> result = dao.find(condition);

        // 取得したオブジェクトと、Excelファイルに定義済の期待値を比較する
        assertNotNull(result);
        assertDataSetEquals(newDataSet("CorresponTypeDaoImplTest_testFindWholeSearch_expected.xls"),
            result);
    }

    /**
     * {@link CorresponTypeDaoImpl#find(jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponTypeCondition)}
     * のテストケース. 条件に該当するレコードがなくてもエラーにはならない.
     * @throws Exception
     */
    @Test
    public void testFindNoRecord() throws Exception {
        SearchCorresponTypeCondition condition = new SearchCorresponTypeCondition();
        condition.setUseWhole(UseWhole.ALL);
        condition.setCorresponType("AAAA"); // 該当なし

        // 1ページ5行の1ページ目 = 1～5
        condition.setPageNo(1);
        condition.setPageRowNum(5);

        List<CorresponType> result = dao.find(condition);

        assertEquals(0, result.size());
    }

    /**
     * {@link CorresponTypeDaoImpl#find(jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponTypeCondition)}
     * のテストケース. 指定ページに該当するレコードがなくてもエラーにはならない.
     * @throws Exception
     */
    @Test
    public void testFindNoPage() throws Exception {
        SearchCorresponTypeCondition condition = new SearchCorresponTypeCondition();
        condition.setUseWhole(UseWhole.ALL);
        condition.setCorresponType("Confirm");// 前方一致検索
        condition.setName("B");// 前方一致検索

        // 1ページ5行の3ページ目 = 11～15
        condition.setPageNo(3);
        condition.setPageRowNum(5);

        List<CorresponType> result = dao.find(condition);

        assertEquals(0, result.size());
    }

    /**
     * {@link CorresponTypeDaoImpl#find(jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponTypeCondition)}
     * のテストケース. 条件に該当するレコードがなくてもエラーにはならない. ユーザーが「_」を入力した場合は、文字列として扱う。
     * @throws Exception
     */
    @Test
    public void testFindEscape1() throws Exception {
        SearchCorresponTypeCondition condition = new SearchCorresponTypeCondition();
        condition.setUseWhole(UseWhole.ALL);
        condition.setCorresponType("Confirm_");

        // 1ページ5行の1ページ目 = 1～5
        condition.setPageNo(1);
        condition.setPageRowNum(5);

        List<CorresponType> result = dao.find(condition);

        assertEquals(0, result.size());
    }

    /**
     * {@link CorresponTypeDaoImpl#find(jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponTypeCondition)}
     * のテストケース. 条件に該当するレコードがなくてもエラーにはならない. ユーザーが「%」を入力した場合は、文字列として扱う。
     * @throws Exception
     */
    @Test
    public void testFindEscape2() throws Exception {
        SearchCorresponTypeCondition condition = new SearchCorresponTypeCondition();
        condition.setUseWhole(UseWhole.ALL);
        condition.setName("B%");

        // 1ページ5行の1ページ目 = 1～5
        condition.setPageNo(1);
        condition.setPageRowNum(5);

        List<CorresponType> result = dao.find(condition);

        assertEquals(0, result.size());
    }

    /**
     * {@link CorresponTypeDaoImpl#count(jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponTypeCondition)}
     * のテストケース. プロジェクト指定.
     * @throws Exception
     */
    @Test
    public void testCountProject() throws Exception {
        SearchCorresponTypeCondition condition = new SearchCorresponTypeCondition();
        condition.setProjectId("PJ1");
        // ページング指定に影響されない
        condition.setPageNo(2);
        condition.setPageRowNum(10);

        int result = dao.count(condition);

        // PJ1:25件 + 削除:5件 = 20
        assertEquals(20, result);
    }

    /**
     * {@link CorresponTypeDaoImpl#count(jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponTypeCondition)}
     * のテストケース. UseWhole.ALL指定.
     * @throws Exception
     */
    @Test
    public void testCountWhole() throws Exception {
        SearchCorresponTypeCondition condition = new SearchCorresponTypeCondition();
        condition.setUseWhole(UseWhole.ALL);
        // ページング指定に影響されない
        condition.setPageNo(2);
        condition.setPageRowNum(10);

        int result = dao.count(condition);

        // 全45件 - 削除:6件 - UseWhole.EACH:5件 * 2 = 29
        assertEquals(26, result);
    }

    /**
     * {@link CorresponTypeDaoImpl#findNotExist(String)}のテストケース.
     * @throws Exception
     */
    @Test
    public void testFindNotExist() throws Exception {
        String projectId = "PJ1";

        List<CorresponType> list = dao.findNotExist(projectId);

        // 取得したオブジェクトと、Excelファイルに定義済の期待値を比較する
        assertNotNull(list);
        assertDataSetEquals(newDataSet("CorresponTypeDaoImplTest_testFindNotExist_expected.xls"),
            list);
    }

    /**
     * {@link CorresponTypeDaoImpl#findByProjectCorresponTypeId(Long)}のテストケース.
     * @throws Exception
     */
    @Test
    public void testFindByProjectCorresponTypeId() throws Exception {
        Long projectCorresponTypeId = 24L;
        CorresponType actual = dao.findByProjectCorresponTypeId(projectCorresponTypeId);

        assertNotNull(actual);
        assertDataSetEquals(newDataSet("CorresponTypeDaoImplTest_testFindByProjectCorresponTypeId_expected.xls"),
            actual);
    }

    /**
     * {@link CorresponTypeDaoImpl#findByProjectCorresponTypeId(Long)}のテストケース.
     * レコードが削除されている.
     * @throws Exception
     */
    @Test(expected = RecordNotFoundException.class)
    public void testFindByProjectCorresponTypeIdDelete() throws Exception {
        Long projectCorresponTypeId = 15L;
        dao.findByProjectCorresponTypeId(projectCorresponTypeId);
    }

    /**
     * {@link CorresponTypeDaoImpl#findByProjectCorresponTypeId(Long)}のテストケース.
     * レコードが存在しない.
     * @throws Exception
     */
    @Test(expected = RecordNotFoundException.class)
    public void testFindByProjectCorresponTypeIdNoRecord() throws Exception {
        Long projectCorresponTypeId = -1L;
        dao.findByProjectCorresponTypeId(projectCorresponTypeId);
    }

    /**
     * {@link CorresponTypeDaoImpl#countCheck}のテストケース.
     * 文書種別を条件に検索
     * @throws Exception
     */
    @Test
    public void testCountCheckCorresponType() throws Exception {
        SearchCorresponTypeCondition condition = new SearchCorresponTypeCondition();
        condition.setCorresponType("Test");
        int actual = dao.countCheck(condition);

        assertTrue(1 == actual);
    }

    /**
     * {@link CorresponTypeDaoImpl#countCheck}のテストケース.
     * IDを条件に検索
     * @throws Exception
     */
    @Test
    public void testCountCheckId() throws Exception {
        SearchCorresponTypeCondition condition = new SearchCorresponTypeCondition();
        condition.setId(1L);
        int actual = dao.countCheck(condition);
        assertTrue(28 == actual);
    }

    /**
     * @link CorresponTypeDaoImpl#countCheck}のテストケース.
     * プロジェクトIDを条件に検索
     * @throws Exception
     */
    @Test
    public void testCountCheckProjectId() throws Exception {
        SearchCorresponTypeCondition condition = new SearchCorresponTypeCondition();
        condition.setProjectId("PJ2");
        int actual = dao.countCheck(condition);

        assertTrue(14 == actual);
    }

    /**
     * {@link CorresponTypeDaoImpl#countCheck}のテストケース.
     * IDと文書種別を条件に検索
     * @throws Exception
     */
    @Test
    public void testCountCheckCorresponTypeId() throws Exception {
        SearchCorresponTypeCondition condition = new SearchCorresponTypeCondition();
        condition.setCorresponType("Query");
        condition.setId(1L);
        int actual = dao.countCheck(condition);

        assertTrue(0 == actual);
    }

    /**
     * {@link CorresponTypeDaoImpl#create}のテストケース.
     * 指定したコレポン文書種別を登録する.
     * @throws Exception
     */
    @Test
    public void testCreate() throws Exception {
        CorresponType corresponType = new CorresponType();
        corresponType.setCorresponType("TEST");
        corresponType.setName("Test");

        WorkflowPattern wp = new WorkflowPattern();
        wp.setId(1L);
        wp.setName("pattern1");
        wp.setWorkflowCd("001");

        corresponType.setWorkflowPattern(wp);
        corresponType.setAllowApproverToBrowse(AllowApproverToBrowse.VISIBLE);
        corresponType.setForceToUseWorkflow(ForceToUseWorkflow.REQUIRED);
        corresponType.setUseWhole(UseWhole.ALL);

        User loginUser = new User();
        loginUser.setEmpNo("ZZA01");
        loginUser.setNameE("Yamada Taro");
        corresponType.setCreatedBy(loginUser);
        corresponType.setUpdatedBy(loginUser);

        Long id = null;
        while (true) {
            try {
                id = dao.create(corresponType);
                break;
            } catch (KeyDuplicateException e) {
                continue;
            }
        }

        CorresponType actual = dao.findById(id);

        assertEquals(corresponType.getCorresponType(), actual.getCorresponType());
        assertEquals(corresponType.getName(), actual.getName());
        assertEquals(corresponType.getWorkflowPattern().getId(), actual.getWorkflowPattern()
            .getId());
        assertEquals(corresponType.getAllowApproverToBrowse(), actual.getAllowApproverToBrowse());
        assertEquals(corresponType.getForceToUseWorkflow(), actual.getForceToUseWorkflow());
        assertEquals(corresponType.getUseWhole(), actual.getUseWhole());
        assertEquals(corresponType.getCreatedBy().toString(), actual.getCreatedBy().toString());
        assertNotNull(actual.getCreatedAt());
        assertEquals(corresponType.getUpdatedBy().toString(), actual.getUpdatedBy().toString());
        assertNotNull(actual.getUpdatedAt());
        assertTrue(1 == actual.getVersionNo());
        assertTrue(0 == actual.getDeleteNo());

    }

    /**
     * {@link CorresponTypeDaoImpl#update}のテストケース.
     * 指定したコレポン文書種別を更新する.
     * @throws Exception
     */
    @Test
    public void testUpdate() throws Exception {
        // 更新前のデータを取得
        CorresponType before = dao.findById(1L);

        CorresponType corresponType = new CorresponType();
        corresponType.setId(before.getId());
        corresponType.setCorresponType("TEST");
        corresponType.setName("Test");

        WorkflowPattern wp = new WorkflowPattern();
        wp.setId(2L);
        wp.setName("Pattern2");
        wp.setWorkflowCd("002");

        corresponType.setWorkflowPattern(wp);
        corresponType.setAllowApproverToBrowse(AllowApproverToBrowse.INVISIBLE);
        corresponType.setForceToUseWorkflow(ForceToUseWorkflow.OPTIONAL);
        corresponType.setUseWhole(UseWhole.ALL);

        User loginUser = new User();
        loginUser.setEmpNo("ZZA05");
        loginUser.setNameE("Ogiwara Keiichi");

        corresponType.setUpdatedBy(loginUser);
        corresponType.setVersionNo(before.getVersionNo());

        int record = dao.update(corresponType);

        // 更新後のデータを取得
        CorresponType actual = dao.findById(before.getId());

        assertTrue(1 == record);

        assertEquals(before.getId(), actual.getId());
        assertEquals(corresponType.getCorresponType(), actual.getCorresponType());
        assertEquals(corresponType.getName(), actual.getName());
        assertEquals(corresponType.getWorkflowPattern().toString(), actual.getWorkflowPattern()
            .toString());
        assertEquals(corresponType.getAllowApproverToBrowse(), actual.getAllowApproverToBrowse());
        assertEquals(corresponType.getForceToUseWorkflow(), actual.getForceToUseWorkflow());
        assertEquals(before.getUseWhole(), actual.getUseWhole());
        assertEquals(before.getCreatedBy().toString(), actual.getCreatedBy().toString());
        assertEquals(before.getCreatedAt(), actual.getCreatedAt());
        assertEquals(loginUser.toString(), actual.getUpdatedBy().toString());
        assertFalse(before.getUpdatedAt().equals(actual.getUpdatedAt()));
        assertNotNull(actual.getUpdatedAt());
        assertTrue(String.valueOf(before.getVersionNo() + 1).equals(String.valueOf(actual
            .getVersionNo())));
        assertTrue(before.getDeleteNo().equals(actual.getDeleteNo()));
    }

    /**
     * {@link CorresponTypeDaoImpl#update}のテストケース.
     * 指定したコレポン文書種別を更新する. バージョンナンバーが違う
     * @throws Exception
     */
    @Test(expected = StaleRecordException.class)
    public void testUpdateStaleRecordException() throws Exception {
        // 更新前のデータを取得
        CorresponType before = dao.findById(1L);

        CorresponType corresponType = new CorresponType();
        corresponType.setId(before.getId());
        corresponType.setCorresponType("TEST");
        corresponType.setName("Test");

        WorkflowPattern wp = new WorkflowPattern();
        wp.setId(2L);
        wp.setName("Pattern2");
        wp.setWorkflowCd("002");

        corresponType.setWorkflowPattern(wp);
        corresponType.setAllowApproverToBrowse(AllowApproverToBrowse.INVISIBLE);
        corresponType.setForceToUseWorkflow(ForceToUseWorkflow.OPTIONAL);
        corresponType.setUseWhole(UseWhole.ALL);

        User loginUser = new User();
        loginUser.setEmpNo("ZZA05");
        loginUser.setNameE("Keiichi Ogiwara");

        corresponType.setUpdatedBy(loginUser);
        corresponType.setVersionNo(99L);

        dao.update(corresponType);
    }

    /**
     * {@link CorresponTypeDaoImpl#findByIdProjectId}のテストケース
     * @throws Exception
     */
    @Test
    public void testFindByIdProjectId() throws Exception {
        CorresponType actual = dao.findByIdProjectId(1L, "PJ1");

        assertEquals(String.valueOf(1L), actual.getId().toString());
        assertEquals(String.valueOf(1L), actual.getProjectCorresponTypeId().toString());
        assertEquals("PJ1", actual.getProjectId());
        assertEquals("Test Project1", actual.getProjectNameE());
        assertEquals("A_Query", actual.getName());
        assertEquals(String.valueOf(1L), actual.getWorkflowPattern().getId().toString());
        assertEquals("001", actual.getWorkflowPattern().getWorkflowCd());
        assertEquals("Pattern1", actual.getWorkflowPattern().getName());
        assertEquals(AllowApproverToBrowse.INVISIBLE.toString(), actual.getAllowApproverToBrowse().toString());
        assertEquals(ForceToUseWorkflow.OPTIONAL.toString(), actual.getForceToUseWorkflow().toString());
        assertEquals(UseWhole.ALL.toString(), actual.getUseWhole().toString());
        assertEquals("00001", actual.getCreatedBy().getEmpNo());
        assertEquals("00001", actual.getUpdatedBy().getEmpNo());
        assertEquals(String.valueOf(0), actual.getVersionNo().toString());
        assertEquals(String.valueOf(0), actual.getDeleteNo().toString());
    }

    /**
     * {@link CorresponTypeDaoImpl#delete}のテストケース
     * @throws Exception
     */
    @Test
    public void testDelete() throws Exception {
        Long id = 1L;
        // 削除前のデータを取得
        CorresponType before = dao.findById(id);

        CorresponType corresponType = new CorresponType();
        corresponType.setId(id);

        User loginUser = new User();
        loginUser.setEmpNo("ZZA05");
        loginUser.setNameE("Keiichi Ogiwara");

        corresponType.setUpdatedBy(loginUser);
        corresponType.setVersionNo(before.getVersionNo());

        dao.delete(corresponType);

        // 削除したデータを取得
        String sql = "select * from correspon_type where id =" + id;
        ITable actual = getConnection().createQueryTable("actual", sql);

        assertEquals(before.getId().toString(), actual.getValue(0, "id").toString());
        assertEquals(before.getCorresponType().toString(), actual.getValue(0, "correspon_type").toString());
        assertEquals(before.getName(), actual.getValue(0, "name"));
        assertEquals(before.getWorkflowPattern().getId().toString(), actual.getValue(0, "workflow_pattern_id").toString());
        assertEquals(before.getAllowApproverToBrowse().getValue().toString(), actual.getValue(0, "allow_approver_to_browse").toString());
        assertEquals(before.getForceToUseWorkflow().getValue().toString(), actual.getValue(0, "force_to_use_workflow").toString());
        assertEquals(before.getUseWhole().getValue().toString(), actual.getValue(0, "use_whole").toString());
        assertEquals(before.getCreatedBy().getEmpNo(), actual.getValue(0, "created_by"));
        assertEquals(before.getCreatedAt(), actual.getValue(0, "created_at"));
        assertEquals(loginUser.getEmpNo(), actual.getValue(0, "updated_by"));
        assertFalse(before.getUpdatedAt().equals(actual.getValue(0, "updated_at")));
        assertEquals(String.valueOf(before.getVersionNo() + 1), actual.getValue(0, "version_no").toString());
        assertFalse(String.valueOf(before.getDeleteNo()).equals(actual.getValue(0, "delete_no").toString()));

    }

    /**
     * {@link CorresponTypeDaoImpl#delete}のテストケース
     * StaleRecordExceptionが発生
     * @throws Exception
     */
    @Test(expected = StaleRecordException.class)
    public void testDeleteStaleRecordException() throws Exception {
        Long id = 1L;
        // 削除前のデータを取得
        CorresponType before = dao.findById(id);

        CorresponType corresponType = new CorresponType();
        corresponType.setId(id);

        User loginUser = new User();
        loginUser.setEmpNo("ZZA05");
        loginUser.setNameE("Keiichi Ogiwara");

        corresponType.setUpdatedBy(loginUser);
        // StaleRecordExceptionを発生させる
        corresponType.setVersionNo(before.getVersionNo() + 1);

        dao.delete(corresponType);
    }
}
