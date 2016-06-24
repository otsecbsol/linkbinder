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

import jp.co.opentone.bsol.framework.core.exception.ApplicationFatalRuntimeException;
import jp.co.opentone.bsol.framework.core.loader.excel.strategy.ExcelDataLoadStrategy;
import jp.co.opentone.bsol.framework.core.loader.excel.strategy.XLSBeansExcelDataLoadStrategy;

/**
 * Microsoft Excelブックのデータを読み込むクラス.
 * <p>
 * Excel2003形式のデータに対応している.
 * </p>
 * @author opentone
 */
public class ExcelDataLoader {

    /**
     * 実際の読み込み処理を行うクラス.
     * 外部から指定が無ければこのクラスのインスタンスを生成・起動する.
     */
    public static final Class<? extends ExcelDataLoadStrategy> DEFAULT_STRATEGY =
                XLSBeansExcelDataLoadStrategy.class;

    /**
     * 指定された定義情報に従いExcelデータを読み込んで返す.
     * @param <T> 戻り値の型
     * @param config 読み込み定義情報
     * @return 読み込み結果
     * @throws ExcelDataLoadException 読み込みに失敗
     */
    @SuppressWarnings("unchecked")
    public <T> T load(ExcelDataLoaderConfig config) throws ExcelDataLoadException {
        return (T) getStrategy(config).load();
    }

    /**
     * 定義情報から、実際の読み込み処理を行うインスタンスを生成する.
     * 特に指定が無ければデフォルトのインスタンスを生成する.
     * @param config 読み込み定義情報
     * @return 読み込みを行うインスタンス
     */
    public ExcelDataLoadStrategy getStrategy(ExcelDataLoaderConfig config) {
        Class<? extends ExcelDataLoadStrategy> clazz =
            config.getLoadStrategy() != null
                ? config.getLoadStrategy()
                : DEFAULT_STRATEGY;

        try {
            ExcelDataLoadStrategy result = clazz.newInstance();
            result.setConfig(config);
            return result;
        } catch (Exception e) {
            throw new ApplicationFatalRuntimeException(e);
        }
    }
}
