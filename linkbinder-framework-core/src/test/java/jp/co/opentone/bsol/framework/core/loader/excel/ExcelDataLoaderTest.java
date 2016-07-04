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
package jp.co.opentone.bsol.framework.core.loader.excel;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import jp.co.opentone.bsol.framework.core.R;
import jp.co.opentone.bsol.framework.core.S;
import jp.co.opentone.bsol.framework.core.loader.excel.ExcelDataLoadException.Reason;


/**
 * {@link ExcelDataLoader}のテストケース.
 * @author opentone
 */
public class ExcelDataLoaderTest {

    /** テスト対象. */
    private ExcelDataLoader loader;

    @Before
    public void setup() {
        loader = new ExcelDataLoader();
    }

    @After
    public void teardown() {

    }

    /**
     * {@link ExcelDataLoader#load(ExcelDataLoaderConfig)}のテスト.
     * <p>
     * 正しい設定情報を渡してExcelブックの内容が読み込まれていることを検証する.
     * @throws Exception
     */
    @Test
    public void testLoad() throws Exception {
        ExcelDataLoaderConfig config = new ExcelDataLoaderConfig();
        config.setFileName("src/test/resources/ExcelDataLoaderTest.xls");
        config.setOptionFileName("src/test/resources/excelMappingConfig.xml");
        config.setResultClass(S.class);

        S sheet = loader.load(config);
        assertNotNull(sheet);
        assertFalse(sheet.getRecords().isEmpty());
        System.out.println(sheet.getRecords().size());
        for (R r : sheet.getRecords()) {
            System.out.println(r);
        }
    }

    /**
     * {@link ExcelDataLoader#load(ExcelDataLoaderConfig)}のテスト.
     * <p>
     * 存在しないExcelファイル名を指定すると例外が発生することを検証する.
     * @throws Exception
     */
    @Test
    public void testLoadFileNotFound() throws Exception {
        ExcelDataLoaderConfig config = new ExcelDataLoaderConfig();
        config.setFileName("path/to/not/exist/file");
        config.setOptionFileName("src/test/resources/excelMappingConfig.xml");
        config.setResultClass(S.class);

        try {
            loader.load(config);
            fail("例外が発生していない");
        } catch (ExcelDataLoadException actual) {
            actual.printStackTrace();
            assertEquals(Reason.FILE_NOT_FOUND, actual.getReason());
        }
    }

    /**
     * {@link ExcelDataLoader#load(ExcelDataLoaderConfig)}のテスト.
     * <p>
     * 存在しない定義ファイル名を指定すると例外が発生することを検証する.
     * @throws Exception
     */
    @Test
    public void testLoadConfigFileNotFound() throws Exception {
        ExcelDataLoaderConfig config = new ExcelDataLoaderConfig();
        config.setFileName("src/test/resources/ExcelDataLoaderTest.xls");
        config.setOptionFileName("path/to/not/exist/file");
        config.setResultClass(S.class);

        try {
            loader.load(config);
            fail("例外が発生していない");
        } catch (ExcelDataLoadException actual) {
            actual.printStackTrace();
            assertEquals(Reason.FILE_NOT_FOUND, actual.getReason());
        }
    }

    /**
     * {@link ExcelDataLoader#load(ExcelDataLoaderConfig)}のテスト.
     * <p>
     * Excelブック形式以外のファイル名を指定すると例外が発生することを検証する.
     * @throws Exception
     */
    @Test
    public void testLoadIllegalFileFormat() throws Exception {
        ExcelDataLoaderConfig config = new ExcelDataLoaderConfig();
        config.setFileName("src/test/resources/jdbc-ut.properties"); // Excelじゃないファイル
        config.setOptionFileName("src/test/resources/excelMappingConfig.xml");
        config.setResultClass(S.class);

        try {
            loader.load(config);
            fail("例外が発生していない");
        } catch (ExcelDataLoadException actual) {
            actual.printStackTrace();
            assertEquals(Reason.FAIL_TO_READ, actual.getReason());
        }
    }

    /**
     * {@link ExcelDataLoader#load(ExcelDataLoaderConfig)}のテスト.
     * <p>
     * Office2007形式のExcelブックを指定すると例外が発生することを確認する.
     * @throws Exception
     */
    @Test
    public void testLoadOffice2007() throws Exception {
        ExcelDataLoaderConfig config = new ExcelDataLoaderConfig();
        config.setFileName("src/test/resources/ExcelDataLoaderTest-2007.xlsx");
        config.setOptionFileName("src/test/resources/excelMappingConfig.xml");
        config.setResultClass(S.class);

        try {
            loader.load(config);
            fail("例外が発生していない");
        } catch (ExcelDataLoadException actual) {
            actual.printStackTrace();
            assertEquals(Reason.FAIL_TO_READ, actual.getReason());
        }
    }
}
