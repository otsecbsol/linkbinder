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

import java.util.List;

/**
 * Excelワークブック生成に関する情報を保持する.
 * @author opentone
 */
public class WorkbookGeneratorContext {

    // finalフィールドとして定義しているのでpublicでOK。CheckStyleの警告を抑止する
    //CHECKSTYLE:OFF
    /**
     * シート名.
     */
    public final String sheetName;
    /**
     * 出力データ.
     */
    public final List<?> data;
    /**
     * 出力プロパティ名.
     */
    public final List<String> outputFieldNames;
    /**
     * ヘッダ名.
     */
    public final List<String> headerNames;
    /**
     * 罫線を出力する場合はtrue.
     */
    public final boolean outputLine;
    //CHECKSTYLE:ON

    /**
     * Excelワークブック生成に必要な情報を指定して
     * インスタンス化する.
     * @param sheetName シート名
     * @param headerNames ヘッダ名
     * @param outputFieldNames 出力フィールド名
     * @param data 出力データ
     * @param outputLine 罫線を出力する場合はtrue
     */
    public WorkbookGeneratorContext(
        String sheetName,
        List<String> headerNames,
        List<String> outputFieldNames,
        List<?> data,
        boolean outputLine) {

        this.sheetName = sheetName;
        this.headerNames = headerNames;
        this.outputFieldNames = outputFieldNames;
        this.data = data;
        this.outputLine = outputLine;
    }
}
