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

import jp.co.opentone.bsol.framework.test.AbstractDaoTestCase;
import jp.co.opentone.bsol.linkbinder.dto.WorkflowPattern;

/**
 * {@link WorkflowPatternDaoImpl}を検証する.
 * @author opentone
 */
public class WorkflowPatternDaoImplTest extends AbstractDaoTestCase {

    /**
     * テスト対象
     */
    @Resource
    private WorkflowPatternDaoImpl dao;

    /**
     * {@link WorkflowPatternDaoImpl#findAll}のテストケース.
     * @throws Exception
     */
    @Test
    public void testFindAll() throws Exception {
        List<WorkflowPattern> actual = dao.findAll();

        // 取得したオブジェクトと、Excelファイルに定義済の期待値を比較する
        assertNotNull(actual);
        assertDataSetEquals(newDataSet("WorkflowPatternDaoImplTest_testFindAll_expected.xls"),
            actual);
    }
}
