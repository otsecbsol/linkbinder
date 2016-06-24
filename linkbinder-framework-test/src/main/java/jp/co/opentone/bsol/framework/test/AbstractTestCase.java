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
package jp.co.opentone.bsol.framework.test;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.Pointer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.subethamail.wiser.Wiser;
import org.w3c.dom.Document;

import jp.co.opentone.bsol.framework.test.util.ExpectedMessageStringGenerator;
import junit.framework.AssertionFailedError;

/**
 * AbstractTestCase.
 *
 * <p>
 * $Date: 2011-05-18 18:58:16 +0900 (水, 18  5 2011) $
 * $Rev: 3929 $
 * $Author: tsuyoshi.hashimoto $
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:scope.xml", "classpath:applicationContextTest.xml",
"classpath:daoContextTest.xml" })
@Transactional
@Rollback
public abstract class AbstractTestCase extends AbstractJUnit4SpringContextTests {

    protected static Wiser wiser;
    @BeforeClass
    public static void setupClass() throws IOException {
        Properties p = new Properties();
        try {
            p.load(AbstractTestCase.class.getResourceAsStream("/mail.properties"));
            wiser = new Wiser();
            wiser.setHostname(p.getProperty("mail.host"));
            wiser.setPort(Integer.valueOf(p.getProperty("mail.port")));

            wiser.start();
        } catch (IOException e) {
            throw e;
        }
    }

    @AfterClass
    public static void teardownClass() {
        if (wiser != null) {
            wiser.stop();
        }
    }

    /**
     * 期待されるメッセージ文字列を生成して返す.
     * <p>
     * 生成したメッセージに次の文字が残っている場合は{@link AssertionFailedError}が発生する.
     * <ul>
     * <li>$action$</li>
     * <li>{n}</li>
     * </ul>
     * </p>
     * @param msg メッセージ
     * @param actionName アクション名
     * @param vars メッセージ内の変数を置換する値
     * @return 期待されるメッセージ文字列
     */
    protected String createExpectedMessageString(
        String msg, String actionName, Object... vars) {
        return ExpectedMessageStringGenerator.generate(msg, actionName, vars);
    }

    /**
     * 作成されたExcelデータが期待値通りかを確認する.
     * <p>
     * 具体的には以下を確認する
     * <ul>
     * <li>Excelシートの枚数</li>
     * <li>Excelシートの名前</li>
     * <li>Excelシートの行数</li>
     * <li>それぞれのセルの値</li>
     * </ul>
     * </p>
     * @param sheetCount Excelシートの枚数
     * @param sheetName Excelシートの名前
     * @param sheetRow Excelシートのヘッダを含まない行数
     * @param expected 期待値
     * @param actual Excelデータ
     * @throws Exception 予期せぬ例外発生
     */
    @SuppressWarnings("unchecked")
    protected void assertExcel(int sheetCount, String sheetName,
        int sheetRow, List<Object> expected, byte[] actual) throws Exception{
        // JXPathContextの準備
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new ByteArrayInputStream(actual));
        JXPathContext context = JXPathContext.newContext( document );
        // 名前空間の設定
        context.registerNamespace("NS",
            "urn:schemas-microsoft-com:office:spreadsheet");

        // 【TEST】シート数
        assertEquals(sheetCount, ((Double)context.getValue(
            "count(/NS:Workbook/NS:Worksheet)")).intValue());
        // 【TEST】シート名
        assertEquals(sheetName,
            context.getValue("/NS:Workbook/NS:Worksheet/@ss:Name"));
        // 【TEST】行数
        assertEquals(sheetRow + 1,
            Integer.parseInt((String)context.getValue(
                "/NS:Workbook/NS:Worksheet/NS:Table/@ss:ExpandedRowCount")));

        // Rowの個数を取得
        int rows = ((Double)context.getValue(
            "count(/NS:Workbook/NS:Worksheet/NS:Table/NS:Row)")).intValue();
        assertEquals(sheetRow + 1, rows);

        // 一行ごとに処理
        for (int i = 1; i <= rows; i++){
            // XPath用の変数を作成
            context.getVariables().declareVariable("index", i);
            // Pointerを使って行の位置のJXPathContextを取得
            Pointer pointer = context.getPointer(
                "/NS:Workbook/NS:Worksheet/NS:Table/NS:Row[$index]");
            JXPathContext contextRow = context.getRelativeContext(pointer);
            contextRow.registerNamespace("NS",
                "urn:schemas-microsoft-com:office:spreadsheet");

            List<Object> expectedRow = (List<Object>) expected.get(i - 1);

            // Columnの個数を取得
            int columns = ((Double)contextRow.getValue("count(NS:Cell)")).intValue();
            // 一列ごとに処理
            for (int j = 1; j <= columns; j++) {
                // Pointerを使って列の位置のJXPathContextを取得
                contextRow.getVariables().declareVariable("index", j);
                Pointer pointerColumn = contextRow.getPointer("NS:Cell[$index]");
                JXPathContext contextColumn =
                    contextRow.getRelativeContext(pointerColumn);

                // 列を比較
                String actualColumn = (String)contextColumn.getValue("NS:Data");
                // actualColumnはStringのため、全てStringにする
                String expectedColumn;
                Object o = expectedRow.get(j - 1);
                if (o == null) {
                    expectedColumn = "";
                } else if (o instanceof Date) {
                    SimpleDateFormat dateFormat =
                        new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
                    expectedColumn = dateFormat.format(o);
                } else {
                    expectedColumn = o.toString();
                }
                // 【TEST】セルの値
                assertEquals(expectedColumn, actualColumn);
            }
        }
    }
}
