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

import jp.co.opentone.bsol.framework.core.loader.excel.strategy.ExcelDataLoadStrategy;

/**
 * Excelブックのデータ読み込みに関する定義情報.
 * @author opentone
 */
public class ExcelDataLoaderConfig {

    /** 読み込み対象ファイルのフルパス. */
    private String fileName;

    /** 読み込み処理に関するオプション情報が格納された設定ファイル. */
    private String optionFileName;

    /** 読み込み結果を格納するクラス. */
    private Class<?> resultClass;
    /**
     *  実際に読み込み処理を行うオブジェクトの指定.
     *  通常は何も指定しません.
     */
    private Class<? extends ExcelDataLoadStrategy> loadStrategy;
    /**
     * 空のインスンタンスを生成する.
     */
    public ExcelDataLoaderConfig() {
    }

    /**
     * @param fileName the fileName to set
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    /**
     * @return the fileName
     */
    public String getFileName() {
        return fileName;
    }
    /**
     * @param optionFileName the optionFileName to set
     */
    public void setOptionFileName(String optionFileName) {
        this.optionFileName = optionFileName;
    }
    /**
     * @return the optionFileName
     */
    public String getOptionFileName() {
        return optionFileName;
    }

    /**
     * @param loadStrategy the loadStrategy to set
     */
    public void setLoadStrategy(Class<? extends ExcelDataLoadStrategy> loadStrategy) {
        this.loadStrategy = loadStrategy;
    }

    /**
     * @return the loadStrategy
     */
    public Class<? extends ExcelDataLoadStrategy> getLoadStrategy() {
        return loadStrategy;
    }

    /**
     * @param resultClass the resultClass to set
     */
    public void setResultClass(Class<?> resultClass) {
        this.resultClass = resultClass;
    }

    /**
     * @return the resultClass
     */
    public Class<?> getResultClass() {
        return resultClass;
    }
}
