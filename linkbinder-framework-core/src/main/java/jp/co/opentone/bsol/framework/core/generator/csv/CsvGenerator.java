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
package jp.co.opentone.bsol.framework.core.generator.csv;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.co.opentone.bsol.framework.core.dto.Code;
import jp.co.opentone.bsol.framework.core.generator.GeneratorFailedException;
import jp.co.opentone.bsol.framework.core.util.PropertyGetUtil;

/**
 * CSVデータを生成するクラス.
 * @author opentone
 */
public class CsvGenerator implements Serializable {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 4631200511578738435L;

    /** logger. */
    private static final Logger LOG = LoggerFactory.getLogger(CsvGenerator.class);

    /**
     * デフォルトで使用する日付の書式.
     */
    public static final String DEFAULT_DATE_FORMAT_PATTERN = "yyyy-MM-dd";

    /**
     * デフォルトで使用する日時の書式.
     */
    public static final String DEFAULT_DATETIME_FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss";

    /**
     * 区切り文字.
     */
    public static final char DELIM = ',';

    /**
     * データを囲む文字.
     */
    public static final char QUOTE = '"';

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
     * 出力エンコーディング.
     */
    private String encoding;

    /**
     * Date型カラムの日付／日時の設定.
     */
    private Map<String, String> formatPatterns;

    /**
     * 空のインスタンスを生成する.
     */
    public CsvGenerator() {
    }

    /**
     * CSVデータ生成に必要な情報を指定してインスタンス化する.
     * @param data
     *            出力データ
     * @param outputFieldNames
     *            出力プロパティ名. 出力順に格納すること.
     * @param headerNames
     *            ヘッダ名. 出力順に格納すること
     * @param encoding 出力エンコーディング
     */
    public CsvGenerator(List<?> data, List<String> outputFieldNames, List<String> headerNames,
                        String encoding) {

        this.setData(data);
        this.setOutputFieldNames(outputFieldNames);
        this.setHeaderNames(headerNames);
        this.setEncoding(encoding);
    }

    /**
     * CSVデータ生成に必要な情報を指定してインスタンス化する.
     * @param data
     *            出力データ
     * @param outputFieldNames
     *            出力プロパティ名. 出力順に格納すること.
     * @param headerNames
     *            ヘッダ名. 出力順に格納すること
     * @param encoding 出力エンコーディング
     * @param formatPatterns 日付の書式文字列
     */
    public CsvGenerator(List<?> data, List<String> outputFieldNames, List<String> headerNames,
                        String encoding, Map<String, String> formatPatterns) {

        this.setData(data);
        this.setOutputFieldNames(outputFieldNames);
        this.setHeaderNames(headerNames);
        this.setEncoding(encoding);
        this.setFormatPatterns(formatPatterns);
    }

    private void checkGenerate() {
        if (data == null) {
            throw new GeneratorFailedException("data is null.");
        }
    }

    private static boolean isSYLK(List<String> data) {
        //  CSVデータの先頭文字が[ID]で始まる場合、ExcelがSYLK形式のファイルと
        //  誤認識してしまうことへの対応
        //    http://support.microsoft.com/kb/323626/ja
        //    http://support.microsoft.com/kb/215591/
        //
        //  ×  ID,Name
        //  ×  ID001,Name
        //  ○    id,Name
        return !data.isEmpty() && data.get(0).startsWith("ID");
    }

    private static void convertSYLKAsCsv(List<String> data) {
        //  CSVデータの先頭文字が[ID]の場合、ExcelがSYLK形式のファイルと
        //  誤認識してしまうことへの対応
        //    http://support.microsoft.com/kb/323626/ja
        //    http://support.microsoft.com/kb/215591/
        data.set(0, " " + data.get(0));
    }

    /**
     * CSVデータを生成する.
     * <p>
     * 生成したデータのファイル出力は行わず、戻り値としてデータを返す.
     * </p>
     * @return CSVデータ
     */
    public byte[] generate() {
        checkGenerate();
        try {
            StringBuilder buffer = new StringBuilder();
            StringBuilder line = new StringBuilder();
            if (headerNames != null) {
                if (isSYLK(headerNames)) {
                    convertSYLKAsCsv(headerNames);
                }
                for (int i = 0; i < headerNames.size(); i++) {
                    line.append(QUOTE).append(formatValue(headerNames.get(i))).append(QUOTE).append(DELIM);
                }
                addLine(buffer, line.toString());
            }

            for (int j = 0; j < data.size(); j++) {
                line.delete(0, line.length());
                for (int k = 0; k < outputFieldNames.size(); k++) {
                    Object object = data.get(j);
                    String key = this.outputFieldNames.get(k);
                    Object value = getNestedValue(key, object);
                    line.append(QUOTE).append(format(value, key)).append(QUOTE).append(DELIM);
                }
                addLine(buffer, line.toString());
                if (LOG.isDebugEnabled()) {
                    LOG.debug("row[{}] created.", j);
                }
            }
            return buffer.toString().getBytes(encoding);
        } catch (UnsupportedEncodingException e) {
            throw new GeneratorFailedException(e);
        }
    }

    /**
     * CSVデータを生成する.
     * <p>
     * 生成したデータのファイル出力は行わず、戻り値としてデータを返す.
     * </p>
     * @param records 出力対象
     * @return CSVデータ
     */
    public byte[] generateRecords(List<?> records) {
        try {
            StringBuilder buffer = new StringBuilder();
            StringBuilder line = new StringBuilder();

            // データ行
            for (int i = 0; i < records.size(); i++) {
                line.delete(0, line.length());
                for (int j = 0; j < outputFieldNames.size(); j++) {
                    Object object = records.get(i);
                    String key = outputFieldNames.get(j);
                    Object value = getNestedValue(key, object);
                    line.append(QUOTE)
                        .append(format(value, key))
                        .append(QUOTE).append(DELIM);
                }
                addLine(buffer, line.toString());
                if (LOG.isDebugEnabled()) {
                    LOG.debug("row[{}] created.", i);
                }
            }
            return buffer.toString().getBytes(encoding);
        } catch (UnsupportedEncodingException e) {
            throw new GeneratorFailedException(e);
        }
    }

    /**
     * CSVデータのヘッダーを生成する.
     * <p>
     * 生成したデータのファイル出力は行わず、戻り値としてデータを返す.
     * </p>
     * @return CSVデータ
     */
    public byte[] generateHeader() {
        try {
            StringBuilder buffer = new StringBuilder();
            StringBuilder line = new StringBuilder();
            if (isSYLK(headerNames)) {
                convertSYLKAsCsv(headerNames);
            }
            for (int i = 0; i < headerNames.size(); i++) {
                line.append(headerNames.get(i)).append(DELIM);
            }
            addLine(buffer, line.toString());
            return buffer.toString().getBytes(encoding);

        } catch (UnsupportedEncodingException e) {
            throw new GeneratorFailedException(e);
        }
    }

    private static Object getNestedValue(String key, Object object) {
        try {
            return PropertyGetUtil.getValue(object, key);
        } catch (IllegalAccessException e) {
            throw new GeneratorFailedException(e);
        } catch (InvocationTargetException e) {
            throw new GeneratorFailedException(e);
        } catch (NoSuchMethodException e) {
            throw new GeneratorFailedException(e);
        }
    }

    /**
     * データを文字列に変換する. ・NULLは空文字へ ・「"」が含まれている場合は「""」へ ・Dateは指定されたフォーマットに変換
     * @param value
     *            データ
     * @return 文字列
     */
    private String format(Object value, String key) {
        if (value == null) {
            return "";
        } else if (value instanceof Date) {
            String format = null;
            if (formatPatterns != null) {
                format = formatPatterns.get(key);
            }

            if (format == null) {
                throw new GeneratorFailedException("Format pattern is not set. >> " + key);
            }

            return new SimpleDateFormat(format).format(value);
        } else if (value instanceof Code) {
            return ((Code) value).getLabel().replaceAll("\"", "\"\"");
        } else {
            return value.toString().replaceAll("\"", "\"\"");
        }
    }


    /**
     * ヘッダーを文字列に変換する. ・NULLは空文字へ ・「"」が含まれている場合は「""」へ ・Dateは指定されたフォーマットに変換
     * @param value
     *            データ
     * @return 文字列
     */
    private String formatValue(Object value) {
        if (value == null) {
            return "";
        } else if (value instanceof Date) {
            String format = null;
            return new SimpleDateFormat(format).format(value);
        } else if (value instanceof Code) {
            return ((Code) value).getLabel().replaceAll("\"", "\"\"");
        } else {
            return value.toString().replaceAll("\"", "\"\"");
        }
    }

    /**
     * バッファに行を追加する.
     * @param buffer
     *            バッファ
     * @param line
     *            行データ（最後に区切り文字がついたまま）
     */
    private static void addLine(StringBuilder buffer, String line) {
        if (line.length() > 0) {
            buffer.append(line.substring(0, line.length() - 1)); // 最後の区切り文字を削除
            buffer.append("\n");
        }
    }

    public void setData(List<?> data) {
        this.data = data;
    }

    public List<?> getData() {
        return data;
    }

    public void setHeaderNames(List<String> headerNames) {
        this.headerNames = headerNames;
    }

    public List<String> getHeaderNames() {
        return headerNames;
    }

    public void setOutputFieldNames(List<String> outputFieldNames) {
        this.outputFieldNames = outputFieldNames;
    }

    public List<String> getOutputFieldNames() {
        return outputFieldNames;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getEncoding() {
        return encoding;
    }

    public Map<String, String> getFormatPatterns() {
        return formatPatterns;
    }

    public void setFormatPatterns(Map<String, String> formatPatterns) {
        this.formatPatterns = formatPatterns;
    }
}
