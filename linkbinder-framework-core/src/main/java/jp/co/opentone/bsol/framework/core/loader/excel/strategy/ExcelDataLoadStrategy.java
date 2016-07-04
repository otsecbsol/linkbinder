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
package jp.co.opentone.bsol.framework.core.loader.excel.strategy;

import jp.co.opentone.bsol.framework.core.loader.excel.ExcelDataLoadException;
import jp.co.opentone.bsol.framework.core.loader.excel.ExcelDataLoaderConfig;

/**
 * Excelブックの読み込みを行うクラスを表すインターフェイス.
 * @author opentone
 */
public interface ExcelDataLoadStrategy {

    /**
     * 読み込みに関する定義情報を設定する.
     * @param config 定義情報
     */
    void setConfig(ExcelDataLoaderConfig config);

    /**
     * Excelブックを読み込み、T型のオブジェクトとして返す.
     * @param <T> 戻り値の型
     * @return 読み込み結果
     * @throws ExcelDataLoadException 読み込みに失敗
     */
    <T> T load() throws ExcelDataLoadException;
}
