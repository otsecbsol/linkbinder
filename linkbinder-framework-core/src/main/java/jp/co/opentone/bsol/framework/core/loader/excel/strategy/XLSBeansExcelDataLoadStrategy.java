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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;

import jp.co.opentone.bsol.framework.core.loader.excel.ExcelDataLoadException;
import jp.co.opentone.bsol.framework.core.loader.excel.ExcelDataLoadException.Reason;
import jp.co.opentone.bsol.framework.core.loader.excel.ExcelDataLoaderConfig;
import jp.co.opentone.bsol.framework.core.util.LogUtil;
import net.java.amateras.xlsbeans.XLSBeans;
import net.java.amateras.xlsbeans.XLSBeansException;

/**
 * {@link XLSBeans}を使用してExcelブックのデータを読み込むクラス.
 * <p>
 * http://amateras.sourceforge.jp/cgi-bin/fswiki/wiki.cgi?page=XLSBeans
 * </p>
 * @author opentone
 */
public class XLSBeansExcelDataLoadStrategy implements ExcelDataLoadStrategy {

    /** logger. */
    private static Logger log = LogUtil.getLogger();

    /** 読み込み定義情報. */
    private ExcelDataLoaderConfig config;

    /* (non-Javadoc)
     * @see ExcelDataLoadStrategy
     *      #setConfig(ExcelDataLoaderConfig)
     */
    @Override
    public void setConfig(ExcelDataLoaderConfig config) {
        this.config = config;
    }

    /* (non-Javadoc)
     * @see ExcelDataLoadStrategy#load()
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T load() throws ExcelDataLoadException {
        InputStream in = null;
        InputStream xml = null;
        try {
            in = openInputStream(config.getFileName());
            xml = openInputStream(config.getOptionFileName());
            return (T) new XLSBeans().load(in, xml, config.getResultClass());
        } catch (XLSBeansException e) {
            throw new ExcelDataLoadException(e, Reason.FAIL_TO_READ);
        } finally {
            closeQuietly(in);
            closeQuietly(xml);
        }
    }

    private static InputStream openInputStream(String fileName) throws ExcelDataLoadException {
        File file = new File(fileName);
        if (!file.exists()) {
            throw new ExcelDataLoadException(fileName, Reason.FILE_NOT_FOUND);
        }
        try {
            return new FileInputStream(file);
        } catch (IOException e) {
            throw new ExcelDataLoadException(e, Reason.FAIL_TO_READ);
        }
    }

    private static void closeQuietly(InputStream in) {
        if (in != null) {
            try {
                in.close();
            } catch (IOException ignore) {
                log.warn(ignore.getMessage(), ignore);
            }
        }
    }
}
