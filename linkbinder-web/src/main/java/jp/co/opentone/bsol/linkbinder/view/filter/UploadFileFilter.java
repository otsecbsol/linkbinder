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
package jp.co.opentone.bsol.linkbinder.view.filter;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import jp.co.opentone.bsol.framework.core.config.SystemConfig;
import jp.co.opentone.bsol.linkbinder.Constants;
import jp.co.opentone.bsol.linkbinder.attachment.UploadedFile;
import jp.co.opentone.bsol.linkbinder.view.util.help.TemporaryFileHelper;
import net.arnx.jsonic.JSON;

/**
 * マルチパートリクエストを処理するフィルタ.
 * @author opentone
 */
public class UploadFileFilter implements Filter {
    /** デフォルトリクエスト最大サイズ 1Mbyte. */
    private static final int DEFAULT_MAX_SIZE = 1024 * 1024;
    /** 1MERあたりの最大ファイルサイズ. */
    private int maxSize = DEFAULT_MAX_SIZE;
    /** 1ファイルあたりの最大ファイルサイズ. */
    private int maxFileSize = DEFAULT_MAX_SIZE;
    /** ファイル名最大長. */
    private int maxFilenameLength = 0;
    /** ファイル名最小長. */
    private int minFilenameLength = 1;

    // 小さければメモリ上に、大きければファイルとして アイテムを保持するバイト単位の閾値
    /** デフォルト値. */
    private static final int DEFAULT_THRESHOLD_SIZE = 1024;
    /** 設定値. */
    private int thresholdSize = DEFAULT_THRESHOLD_SIZE;

    /** 一時格納ディレクトリ. */
    private File temporaryDirectory;

    /* (非 Javadoc)
     * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
     */
    @Override
    public void init(FilterConfig config) throws ServletException {
        String s = config.getInitParameter("max-size");
        maxSize = toInt(s, maxSize);

        s = config.getInitParameter("threshold-size");
        thresholdSize = toInt(s, thresholdSize);

        // 最大ファイルサイズの設定値単位はMBなので
        // DEFAULT_MAX_SIZEを乗じます.
        s = SystemConfig.getValue(Constants.KEY_FILE_MAX_SIZE);
        maxFileSize = toInt(s, maxFileSize) * DEFAULT_MAX_SIZE;

        s = SystemConfig.getValue(Constants.KEY_FILE_DIR_PATH);
        if (!StringUtils.isEmpty(s)) {
            File f = new File(s);
            if (!f.exists() || !f.isDirectory()) {
                throw new ServletException(
                        "temporary directory is missing ;" + f.getAbsolutePath());
            }
            temporaryDirectory = f;
        } else {
            throw new ServletException("temporary directory parameter is missing");
        }

        s = SystemConfig.getValue(Constants.KEY_FILENAME_MAX_LENGTH);
        maxFilenameLength = toInt(s, maxFilenameLength);
    }

    /**
     * 設定値をintに変換します.
     * @param value 設定値
     * @param defaultValue デフォルト値. 設定がない場合はこの値が返される.
     * @return 変換したint値
     * @throws ServletException 設定値が数値として正しくない場合
     */
    private int toInt(String value, int defaultValue) throws ServletException {
        int result = defaultValue;
        if (StringUtils.isNotEmpty(value)) {
            result = NumberUtils.toInt(value, Integer.MIN_VALUE);
            if (Integer.MIN_VALUE == result) {
                // 数値として正しくない設定がされている
                throw new ServletException(
                    String.format("Value[%s] is invalid as number.", value));
            }
        }
        return result;
    }

    /* (非 Javadoc)
     * @see javax.servlet.Filter#doFilter(
     *                 javax.servlet.ServletRequest,
     *                 javax.servlet.ServletResponse,
     *                 javax.servlet.FilterChain)
     */
    @Override
    public void doFilter(ServletRequest req, ServletResponse res,
            FilterChain chain) throws IOException, ServletException {

        // キャストする前に一応チェック
        if (!(req instanceof HttpServletRequest)) {
            chain.doFilter(req, res);
            return;
        }

        HttpServletRequest httpReq = (HttpServletRequest) req;
        // マルチパートではない場合、スルーします。
        if (!ServletFileUpload.isMultipartContent(httpReq)) {
            chain.doFilter(req, res);
            return;
        }

        DiskFileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload sfu = new ServletFileUpload(factory);

        factory.setSizeThreshold(thresholdSize);
        sfu.setSizeMax(maxSize); //
        sfu.setHeaderEncoding(req.getCharacterEncoding());

        try {
            @SuppressWarnings("unchecked")
            Iterator<FileItem> ite = sfu.parseRequest(httpReq).iterator();
            List<String> keys = new ArrayList<String>();
            List<String> names = new ArrayList<String>();
            List<String> fieldNames = new ArrayList<String>();
            List<Long> fileSize = new ArrayList<Long>();

            while (ite.hasNext()) {
                String name = null;
                FileItem item = ite.next();

                // フォームデータは無視します。
                if (!(item.isFormField())) {
                    name = item.getName();
                    name = name.substring(name.lastIndexOf('\\') + 1);
                    if (StringUtils.isEmpty(name)) {
                        continue;
                    }
                    File f = null;
                    // CHECKSTYLE:OFF
                    // 存在しないファイルを作成するまでループします.
                    while ((f = new File(createTempFilePath())).exists()){
                    }
                    // CHECKSTYLE:ON
                    if (!validateByteLength(
                            name,
                            maxFilenameLength,
                            minFilenameLength)) {
                        // ファイル名上限オーバー
                        names.add(name);
                        keys.add(UploadedFile.KEY_FILENAME_OVER);
                        fieldNames.add(item.getFieldName());
                        fileSize.add(item.getSize());
                    } else  if (item.getSize() == 0) {
                        // ファイルサイズ0
                        names.add(name);
                        keys.add(UploadedFile.KEY_SIZE_ZERO);
                        fieldNames.add(item.getFieldName());
                        fileSize.add(item.getSize());
                    } else if (maxFileSize > 0 && item.getSize() > maxFileSize) {
                        // ファイルサイズオーバー
                        // 最大ファイルサイズに0が指定されている場合はValidationを無視
                        names.add(name);
                        keys.add(UploadedFile.KEY_SIZE_OVER);
                        fieldNames.add(item.getFieldName());
                        fileSize.add(item.getSize());
                    } else {
                        item.write(f);
                        names.add(name);
                        keys.add(f.getName());
                        fieldNames.add(item.getFieldName());
                        fileSize.add(item.getSize());
                    }
                    f.deleteOnExit();
                }
            }

            // 正常
            UploadFileFilterResult result = new UploadFileFilterResult();
            result.setResult(UploadFileFilterResult.RESULT_OK);
            result.setNames(names.toArray(new String[names.size()]));
            result.setKeys(keys.toArray(new String[keys.size()]));
            result.setFieldNames(fieldNames.toArray(new String[fieldNames.size()]));
            result.setFileSize(fileSize.toArray(new Long[fileSize.size()]));
            writeResponse(req, res, result);
        } catch (Exception e) {
            e.printStackTrace();
            // エラー
            UploadFileFilterResult result = new UploadFileFilterResult();
            result.setResult(UploadFileFilterResult.RESULT_NG);
            writeResponse(req, res, result);
        }
    }

    /**
     * 文字列のバイト数チェックを行う.
     * @param value 入力値
     * @param maximum 最大文字列バイト数
     * @param minimum 最小文字列バイト数
     * @return 指定範囲に収まる場合true
     */
    private boolean validateByteLength(String value, int maximum, int minimum) {
        int length = getByteLength(value);
        if (minimum != 0) {
            if (length < minimum) {
                return false;
            }
        }
        if (maximum != 0) {
            if (length > maximum) {
                return false;
            }
        }
        return true;
    }

    private int getByteLength(Object value) {
        String v;
        if (value instanceof String) {
            v = (String) value;
        } else {
            v = value.toString();
        }

        int length = 0;
        for (int i = 0; i < v.length(); i++) {
            // ASCII範囲内であれば1バイト、その他は2バイト
            // CHECKSTYLE:OFF
            length += (v.charAt(i) <= 0x7f) ? 1 : 2;
            // CHECKSTYLE:ON
        }
        return length;
    }

    /**
     * 一時ファイルのパスを作成します.
     * @return 一時ファイルのパス
     */
    private String createTempFilePath() {
        TemporaryFileHelper helper
            = new TemporaryFileHelper(temporaryDirectory.getAbsolutePath());
        return helper.createTempFilePath();
    }

    @Override
    public void destroy() {
    }

    private void writeResponse(ServletRequest req,
                               ServletResponse res,
                               UploadFileFilterResult result) throws IOException {
        res.setContentType("text/plain;charset=" + req.getCharacterEncoding());
        OutputStream out = res.getOutputStream();
        out.write(JSON.encode(result).getBytes(req.getCharacterEncoding()));
    }
}
