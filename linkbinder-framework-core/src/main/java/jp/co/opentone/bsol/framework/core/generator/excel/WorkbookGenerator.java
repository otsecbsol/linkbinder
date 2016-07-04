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
package jp.co.opentone.bsol.framework.core.generator.excel;

import java.io.Serializable;
import java.util.List;

import jp.co.opentone.bsol.framework.core.generator.excel.strategy.WorkbookGeneratorStrategy;
import jp.co.opentone.bsol.framework.core.generator.excel.strategy.XmlWorkbookGeneratorStrategy;

/**
 * ExcelワークブックにBeanリストのデータを出力するクラス. このクラスでサポートするのは、1ブック1シートのデータのみ.
 * 罫線を出力することは可能だが、線種や太さなどの指定はできない.
 * @author opentone
 */
public class WorkbookGenerator implements Serializable {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -286963670434760341L;

    /**
     * Excelワークブックを生成する実装.
     */
    private WorkbookGeneratorStrategy strategy = new XmlWorkbookGeneratorStrategy();

    /**
     * シート名.
     */
    private String sheetName;
    /**
     * 出力データ.
     */
    private List<?> data;
    /**
     * 出力プロパティ名.
     */
    private List<String> outputFieldNames;
    /**
     * ヘッダ名.
     */
    private List<String> headerNames;
    /**
     * 罫線を出力する場合はtrue.
     */
    private boolean outputLine;

    /**
     * 空のインスタンスを生成する.
     */
    public WorkbookGenerator() {
    }

    /**
     * Excel ワークブック生成に必要な情報を指定してインスタンス化する.
     * @param sheetName
     *            シート名
     * @param data
     *            出力データ
     * @param outputFieldNames
     *            出力プロパティ名. 出力順に格納すること.
     * @param headerNames
     *            ヘッダ名. 出力順に格納すること
     * @param outputLine
     *            罫線を出力する場合はtrue
     */
    public WorkbookGenerator(String sheetName, List<?> data, List<String> outputFieldNames,
                             List<String> headerNames, boolean outputLine) {

        this.sheetName = sheetName;
        this.data = data;
        this.outputFieldNames = outputFieldNames;
        this.headerNames = headerNames;
        this.outputLine = outputLine;
    }

    /**
     * Excelワークブックを生成するオブジェクトを返す.
     * @return ワークブック生成オブジェクト
     */
    public WorkbookGeneratorStrategy getStrategy() {
        return strategy;
    }

    /**
     * Excelワークブックを生成するオブジェクトを設定する.
     * @param strategy ワークブック生成オブジェクト
     */
    public void setStrategy(WorkbookGeneratorStrategy strategy) {
        this.strategy = strategy;
    }

    /**
     * MS Excelワークブックを生成する.
     * <p>
     * このメソッドでサポートするのは、1ブック1シートのデータのみ. 罫線を出力することは可能だが、線種や太さなどの指定はできない.
     * 生成したデータのファイル出力は行わず、戻り値としてデータを返す.
     * </p>
     * @return MS Excelワークブックデータ
     */
    public byte[] generate() {
        WorkbookGeneratorContext context =
                new WorkbookGeneratorContext(sheetName,
                                             headerNames,
                                             outputFieldNames,
                                             data,
                                             outputLine);
        return strategy.generate(context);
    }
}
