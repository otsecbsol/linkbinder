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
package jp.co.opentone.bsol.framework.web.view.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.mail.internet.MimeUtility;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponseWrapper;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.co.opentone.bsol.framework.core.config.SystemConfig;
import jp.co.opentone.bsol.framework.core.dto.Code;
import jp.co.opentone.bsol.framework.core.dto.StringCode;
import jp.co.opentone.bsol.framework.core.exception.ApplicationFatalRuntimeException;
import jp.co.opentone.bsol.framework.core.message.Message;
import jp.co.opentone.bsol.framework.core.util.ArgumentValidator;

/**
 * View関連の共通処理を集めたクラス.
 * @author opentone
 */
public class ViewHelper implements Serializable {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 8334223734895951826L;
    /** logger. */
    private static final Logger log = LoggerFactory.getLogger(ViewHelper.class);

    /**
     * ファイルダウンロード時のレスポンスヘッダに設定するファイル名のエンコード指定.
     */
    public static final String KEY_DOWNLOAD_HEADER_FILENAME_ENCODING =
            "download.header.filename.encoding";

    /** サーバ名. */
    public static final String KEY_SERVER_NAME = "server.name";

    /**
     * 絶対パス用フォーマット.
     */
    public static final String BASE_PATH = "%s://%s%s%s";

    /**
     * ファイルダウンロード用のOutputStream.
     * <p>
     * @see #getDownloadOutputStream(String)
     * @see #completeDownload()
     */
    private transient DownloadOutputStream out;
    /**
     * デフォルトコンストラクタ.
     */
    public ViewHelper() {
    }

    /**
     * ExternalContextを返す.
     * @return ExternalContext
     */
    public ExternalContext getExternalContext() {
        return FacesContext.getCurrentInstance().getExternalContext();
    }

    /**
     * requestスコープに格納された値を返す.
     * @param <T>
     *            戻り値の型
     * @param key
     *            取得対象のキー
     * @return 対応する値. 無い場合はnull
     */
    @SuppressWarnings("unchecked")
    public <T> T getRequestValue(String key) {
        return (T) getExternalContext().getRequestMap().get(key);
    }

    /**
     * requestスコープに値を設定する.
     * @param key
     *            キー
     * @param value
     *            値
     */
    public void setRequestValue(String key, Object value) {
        getExternalContext().getRequestMap().put(key, value);
    }

    /**
     * requestスコープに格納された値を削除する.
     * @param key
     *            キー
     */
    public void removeRequestValue(String key) {
        getExternalContext().getRequestMap().remove(key);
    }

    /**
     * sessionスコープに格納された値を返す.
     * @param <T>
     *            戻り値の型
     * @param key
     *            取得対象のキー
     * @return 対応する値. 無い場合はnull
     */
    @SuppressWarnings("unchecked")
    public <T> T getSessionValue(String key) {
        return (T) getExternalContext().getSessionMap().get(key);
    }

    /**
     * sessionスコープに値を設定する.
     * @param key
     *            キー
     * @param value
     *            値
     */
    public void setSessionValue(String key, Object value) {
        getExternalContext().getSessionMap().put(key, value);
    }

    /**
     * HttpSession値を取得する.
     * @return sessionオブジェクト.
     */
    public HttpSession getHttpSession() {
        return (HttpSession) getExternalContext().getSession(true);
    }

    /**
     * sessionスコープに格納された値を削除する.
     * @param key
     *            キー
     */
    public void removeSessionValue(String key) {
        getExternalContext().getSessionMap().remove(key);
    }

    /**
     * applicationスコープに格納された値を返す.
     * @param <T>
     *            戻り値の型
     * @param key
     *            取得対象のキー
     * @return 対応する値. 無い場合はnull
     */
    @SuppressWarnings("unchecked")
    public <T> T getApplicationValue(String key) {
        return (T) getExternalContext().getApplicationMap().get(key);
    }

    /**
     * applicationスコープに値を設定する.
     * @param key
     *            キー
     * @param value
     *            値
     */
    public void setApplicationValue(String key, Object value) {
        getExternalContext().getApplicationMap().put(key, value);
    }

    /**
     * applicationスコープに格納された値を削除する.
     * @param key
     *            キー
     */
    public void removeApplicationValue(String key) {
        getExternalContext().getApplicationMap().remove(key);
    }

    /**
     * Cookieに格納された値を返す.
     * @param key
     *            取得対象のキー
     * @return 対応する値. 無い場合はnull
     */
    public String getCookieValue(String key) {
        String value = null;
        Cookie[] cookies = ((HttpServletRequest) getExternalContext().getRequest()).getCookies();
        if (cookies != null) {
            for (int i = 0; i < cookies.length; i++) {
                Cookie cookie = cookies[i];
                if (cookie.getName() != null && cookie.getName().equals(key)) {
                    value = cookie.getValue();
                }
            }
        }
        return value;
    }

    /**
     * request/session/applicationのいずれかのスコープに格納された値を返す. 優先順位は以下の通り.
     * <ol>
     * <li>request</li>
     * <li>session</li>
     * <li>application</li>
     * </ol>
     * @param <T>
     *            戻り値の型
     * @param key
     *            取得対象のキー
     * @return 対応する値. 無い場合はnull
     */
    @SuppressWarnings("unchecked")
    public <T> T getValue(String key) {
        // NOTE
        // T型にキャストするのは冗長なようだが、
        // 省略するとJDKのコンパイラではコンパイルエラーになる
        // Eclipseのコンパイラでは通ってしまうので、削除しないこと
        T value = (T) getRequestValue(key);
        if (value == null) {
            value = (T) getSessionValue(key);
            if (value == null) {
                value = (T) getApplicationValue(key);
            }
        }
        return value;
    }

    /**
     * メッセージを追加する.
     * @param message
     *            メッセージ
     */
    public void addMessage(Message message) {
        addMessage(null, message);
    }

    /**
     * メッセージを追加する.
     * @param clientId
     *            コンポーネントのクライアントID
     * @param message
     *            メッセージ
     */
    public void addMessage(String clientId, Message message) {
        FacesContext context = FacesContext.getCurrentInstance();

        context.addMessage(clientId, createFacesMessage(message));
    }

    /**
     * メッセージの内容から、メッセージの重要度を決定する.
     * @param message
     *            メッセージ
     * @return メッセージの重要度
     */
    protected Severity getSeverity(Message message) {
        String messageCode = message.getMessageCode();
        char mType = messageCode.charAt(0);
        switch (mType) {
        case 'I':
            return FacesMessage.SEVERITY_INFO;
        case 'E':
            return FacesMessage.SEVERITY_ERROR;
        case 'W':
            return FacesMessage.SEVERITY_WARN;
        case 'F':
            return FacesMessage.SEVERITY_FATAL;
        default:
            throw new IllegalArgumentException(String.format("Severity for %s is undefined.",
                                                             messageCode));
        }
    }

    /**
     * MessageからFacesMessageを作成する.
     * @param message
     *            メッセージコード
     * @return FacesMessage
     */
    public FacesMessage createFacesMessage(Message message) {
        Severity severity = getSeverity(message);
        String summary = message.getSummary() != null ? message.getSummary() : severity.toString();

        FacesMessage fMessage = new FacesMessage(severity, summary, message.getMessage());

        return fMessage;
    }

    /**
     * dtoのリストを、JSFのSelectItemに変換して返す.
     * dtoListがnullなら空のリストを返す
     * @param dtoList
     *            SelectItemに設定するデータ
     * @param valuePropertyName
     *            SelectItemの値として設定する、dtoListに格納されたオブジェクトのプロパティ名
     * @param labelPropertyName
     *            SelectItemの表示ラベルとして設定する、dtoListに格納されたオブジェクトのプロパティ名
     * @return 変換後の値
     */
    public List<SelectItem> createSelectItem(List<?> dtoList, String valuePropertyName,
                                             String labelPropertyName) {
        ArgumentValidator.validateNotEmpty(valuePropertyName, "valuePropertyName");
        ArgumentValidator.validateNotEmpty(labelPropertyName, "labelPropertyName");

        List<SelectItem> result = new ArrayList<SelectItem>();
        try {
            if (dtoList == null) {
                return result;
            }
            for (Object item : dtoList) {
                Object val = PropertyUtils.getProperty(item, valuePropertyName);
                String label = BeanUtils.getProperty(item, labelPropertyName);
                if (isValidSelectItem(val, label)) {
                    result.add(new SelectItem(val, label));
                }
            }
            return result;

        } catch (IllegalAccessException e) {
            throw new ApplicationFatalRuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new ApplicationFatalRuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new ApplicationFatalRuntimeException(e);
        }
    }

    private boolean isValidSelectItem(Object val, String label) {
        if (val == null || label == null) {
            log.warn("Value or label is null. Maybe it's a bug. value={}, label={}", val, label);
            return false;
        }
        return true;
    }

    /**
     * Codeのリストを、JSFのSelectItemに変換して返す.
     * Codeのリストがnullなら空のリストを返す
     * @param codeList
     *            SelectItemに設定するデータ
     * @return 変換後の値
     */
    public List<SelectItem> createSelectItem(List<Code> codeList) {
        List<SelectItem> result = new ArrayList<SelectItem>();
        if (codeList == null) {
            return result;
        }
        for (Code ev : codeList) {
            Object val = ev.getValue();
            String label = ev.getLabel();
            if (isValidSelectItem(val, label)) {
                result.add(new SelectItem(val, label));
            }
        }
        return result;
    }

    /**
     * StringCodeの配列を、JSFのSelectItemに変換して返す.
     * StringCodeの配列がnullなら空のリストを返す
     * @param codes
     *            SelectItemに設定するデータ
     * @return 変換後の値
     */
    public List<SelectItem> createSelectItem(StringCode[] codes) {
        if (codes == null) {
            return new ArrayList<SelectItem>();
        }
        return createSelectStringItem(Arrays.asList(codes));
    }

    /**
     * StringCodeのリストを、JSFのSelectItemに変換して返す.
     * StringCodeのリストがnullなら空のリストを返す
     * @param codeList
     *            SelectItemに設定するデータ
     * @return 変換後の値
     */
    public List<SelectItem> createSelectStringItem(List<StringCode> codeList) {
        List<SelectItem> result = new ArrayList<SelectItem>();
        if (codeList == null) {
            return result;
        }
        for (StringCode ev : codeList) {
            Object val = ev.getValue();
            String label = ev.getLabel();
            if (isValidSelectItem(val, label)) {
                result.add(new SelectItem(val, label));
            }
        }
        return result;
    }

    /**
     * Codeの配列を、JSFのSelectItemに変換して返す.
     * Codeの配列がnullなら空のリストを返す
     * @param codes
     *            SelectItemに設定するデータ
     * @return 変換後の値
     */
    public List<SelectItem> createSelectItem(Code[] codes) {
        if (codes == null) {
            return new ArrayList<SelectItem>();
        }
        return createSelectItem(Arrays.asList(codes));
    }

    /**
     * StringCodeの配列を、JSFのSelectItemに変換して返す.
     * StringCodeの配列がnullなら空のリストを返す
     * @param codes
     *            SelectItemに設定するデータ
     * @return 変換後の値
     */
    public List<SelectItem> createSelectStringItem(StringCode[] codes) {
        if (codes == null) {
            return new ArrayList<SelectItem>();
        }
        return createSelectStringItem(Arrays.asList(codes));
    }

    /**
     * ファイルをダウンロードするための出力ストリームを生成して返す.
     * <p>
     * {@link #completeDownload()}を呼び出してダウンロードの終了処理を行うのは
     * 呼出元の責務である.
     *
     * @see #completeDownload()
     * @param fileName ファイル名
     * @param downloadByRealName ファイル名をそのまま加工しない場合はtrue
     * @return ファイルダウンロード用の出力ストリーム
     * @throws IOException ストリームの生成に失敗
     */
    public OutputStream getDownloadOutputStream(String fileName, boolean downloadByRealName)
            throws IOException {
        HttpServletRequest request = (HttpServletRequest) getExternalContext().getRequest();
        HttpServletResponse response = (HttpServletResponse) getExternalContext().getResponse();

        out = null;
        out = new DownloadOutputStream(request, response, fileName, downloadByRealName);

        return out;
    }

    /**
     * ファイルをダウンロードするための出力ストリームを生成して返す.
     * <p>
     * {@link #completeDownload()}を呼び出してダウンロードの終了処理を行うのは
     * 呼出元の責務である.
     *
     * @see #completeDownload()
     * @param fileName ファイル名
     * @return ファイルダウンロード用の出力ストリーム
     * @throws IOException ストリームの生成に失敗
     */
    public OutputStream getDownloadOutputStream(String fileName) throws IOException {
        return getDownloadOutputStream(fileName, false);
    }

    /**
     * ファイルをクライアントにダウンロードさせる.
     * @param fileName
     *            ファイル名
     * @param content
     *            ファイルの内容
     * @throws IOException 出力に失敗
     */
    public void download(String fileName, byte[] content) throws IOException {
        download(fileName, content, false);
    }

    /**
     * ファイルをクライアントにダウンロードさせる.
     * @param fileName
     *            ファイル名
     * @param content
     *            ファイルの内容
     * @param downloadByRealFileName ダウンロードファイル名を加工しない場合はtrue
     * @throws IOException 出力に失敗
     */
    public void download(String fileName,
                         byte[] content,
                         boolean downloadByRealFileName)
            throws IOException {

        HttpServletRequest request = (HttpServletRequest) getExternalContext().getRequest();
        HttpServletResponse response = (HttpServletResponse) getExternalContext().getResponse();

        setDownloadResponseHeader(request,
                                response,
                                fileName,
                                downloadByRealFileName,
                                content.length);

        doDownload(response, new ByteArrayInputStream(content));

        FacesContext.getCurrentInstance().responseComplete();
    }

    public void download(String fileName, InputStream in, int length) throws IOException {
        HttpServletRequest request = (HttpServletRequest) getExternalContext().getRequest();
        HttpServletResponse response = (HttpServletResponse) getExternalContext().getResponse();

        setDownloadResponseHeader(request, response, fileName, false, length);
        doDownload(response, in);

        FacesContext.getCurrentInstance().responseComplete();
    }

    protected void setDownloadResponseHeader(HttpServletRequest request,
                                             HttpServletResponse response,
                                             String fileName,
                                             boolean downloadByRealName,
                                             int length) {
        setContentType(response, fileName);
        setHeaderFileName(request, response, fileName, downloadByRealName);
        response.addHeader("Accept-Ranges", "none"); // For Acrobat Reader 7.0

        if (length > 0) {
            response.setContentLength(length);
        }
    }

     /**
     * 現在のリクエストがactionIdにより起動されている場合はtrueを返す.
     * @param actionId
     *            アクション名. submitボタンのid
     * @return 指定されたアクションにより起動されている場合はtrue
     */
    public boolean isActionInvoked(String actionId) {
        HttpServletRequest req =
            (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext()
                    .getRequest();
        if (isPartialAjax()) {
            return StringUtils
                    .equals(actionId, req.getParameter("javax.faces.source"));
        } else {
            return StringUtils.isNotEmpty(req.getParameter(actionId));
        }
    }

     /**
     * 現在のリクエストが指定されたactionIdsのいずれかにより起動されている場合はtrueを返す.
     * @param actionIds
     *            アクション名. submitボタンのid
     * @return 指定されたアクションのいずれかにより起動されている場合はtrue
     */
    public boolean isAnyActionsInvoked(String... actionIds) {
        for (String id : actionIds) {
            if (isActionInvoked(id)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 現在のリクエストがJSF2のAjaxによるPartial Requestの場合はtrueを返す.
     * @return Partial Requestの場合はtrue
     */
    public boolean isPartialAjax() {
        String value =
            ((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext()
                    .getRequest()).getParameter("javax.faces.partial.ajax");

        return Boolean.parseBoolean(value);
    }

    private void setContentType(HttpServletResponse response, String fileName) {
        String extension = getExtension(fileName);
        if (".xls".equals(extension)) {
            response.setContentType("application/vnd.ms-excel");
        } else if (".word".equals(extension)) {
            response.setContentType("application/msword");
        } else if (".pdf".equals(extension)) {
            response.setContentType("application/pdf");
        } else if (".csv".equals(extension)) {
            response.setContentType("text/csv");
        } else {
            response.setContentType("application/octet-stream");
        }
    }

    /**
     * ダウンロード対象ファイル名を、User-Agentに応じたエンコードを行いレスポンスヘッダに設定する.
     * <ul>
     * <li>IEで正常にファイル名が表示・ダウンロードできることを第一とする</li>
     * <li>User-Agentの偽装は考慮しない</li>
     * <li>ファイル名の長さが256バイトを越える場合、ダウンロードしたファイルを開けない場合があるがここでは考慮しない</li>
     * </ul>
     * @param request リクエスト情報
     * @param response レスポンス情報
     * @param fileName ダウンロードするファイル名
     */
    private void setHeaderFileName(HttpServletRequest request,
                                   HttpServletResponse response,
                                   String fileName,
                                   boolean downloadByRealFileName) {
        String headerFileName;
        String userAgent = request.getHeader("User-Agent");
        try {
            if (userAgent.indexOf("MSIE") != -1) {
                //  IEで認識可能な文字エンコーディングに変換したファイル名を設定
                String enc = SystemConfig.getValue(KEY_DOWNLOAD_HEADER_FILENAME_ENCODING);
                headerFileName = new String(fileName.getBytes(enc), "iso-8859-1");
                //  以下コードでも文字化けは起こらないが
                //  URLエンコードした名前 + 保存先パス名が256byteを越える可能性が大きいので
                //  採用しない
//                headerFileName = URLEncoder.encode(fileName, "UTF-8");
            } else {
                //  その他のブラウザであればMIMEエンコードしたファイル名を設定
                //  see RFC2231
                headerFileName = MimeUtility.encodeWord(fileName, "ISO-2022-JP", "B");
            }
            //  SpreadsheetMLをExcel 2007で開くと拡張子偽装の警告が出るので
            //  *.xmlに変更
            if (!downloadByRealFileName && headerFileName.endsWith(".xls")) {
                headerFileName = headerFileName.replaceAll("\\.xls$", ".xml");
            }
            response.addHeader("Content-Disposition",
                               "attachment; filename=\"" + headerFileName + "\"");
        } catch (UnsupportedEncodingException ignore) {
            log.warn(
                String.format("failed to set filename to response header. %s", fileName),
                ignore);
        }
    }

    protected void doDownload(HttpServletResponse response, InputStream in) throws IOException {
        ServletOutputStream o = getServletOutputStream(response);
        try {
            final int bufLength = 4096;
            byte[] buf = new byte[bufLength];
            int i = 0;
            while ((i = in.read(buf, 0, buf.length)) != -1) {
                o.write(buf, 0, i);
            }

            o.flush();
            o.close();
        } catch (IOException e) {
            if (isDownloadCanceled(e)) {
                log.warn("Download canceled.");
            } else {
                throw e;
            }
        }
    }

    protected ServletOutputStream getServletOutputStream(HttpServletResponse response)
            throws IOException {
        ServletOutputStream o;
        if (response instanceof ServletResponseWrapper) {
            log.warn("Response is instance of ServletResponseWrapper");
            o = response.getOutputStream();
            /*
            HttpServletResponse rawResponse =
                (HttpServletResponse) ((ServletResponseWrapper) response).getResponse();
            o = rawResponse.getOutputStream();
            */
        } else {
            log.warn("Response is instance of ServletResponse");
            o = response.getOutputStream();
        }

        return o;
    }

    /**
     * ダウンロードを終了する.
     * {@link #getDownloadOutputStream(String)}を使用して呼出元で
     * 独自にダウンロード処理を行う場合は、最後にこのメソッドを呼び出さなければならない.
     *
     * @see #getDownloadOutputStream(String)
     */
    public void completeDownload() {
        if (out.isOpen()) {
            FacesContext.getCurrentInstance().responseComplete();
            out = null;
        }
    }

    /**
     * ダウンロード時に発生したIOExceptionを判定し、ユーザーによるキャンセルの可能性がある場合はtrueを返す.
     * @param e ダウンロード時に発生した例外
     * @return ユーザーによるキャンセルが予想される場合はtrue
     */
    public boolean isDownloadCanceled(IOException e) {
        return e instanceof SocketException
           || (e.getCause() != null && e.getCause() instanceof SocketException);
    }

    /**
     * HTMLデータをブラウザに表示させる.
     * @param content
     *            ファイルの内容
     * @param charset レスポンスヘッダに設定する文字エンコーディング
     * @throws IOException 出力に失敗
     */
    public void requestResponse(byte[] content, String charset) throws IOException {
        HttpServletResponse response = (HttpServletResponse) getExternalContext().getResponse();
        setHtmlResponseHeader(response, charset);

        doDownload(response, new ByteArrayInputStream(content));
//        doWrite(response, content);

        FacesContext.getCurrentInstance().responseComplete();
    }

    protected void setHtmlResponseHeader(HttpServletResponse response, String charset) {
        response.setContentType(String.format("text/html; charset=%s", charset));
    }

    protected void doWrite(HttpServletResponse response, byte[] content) throws IOException {
        ServletOutputStream o = response.getOutputStream();
        o.write(content);
        o.flush();
        o.close();
    }

    private String getExtension(String fileName) {
        int i = fileName.lastIndexOf('.');
        if (i == -1) {
            return null;
        }
        String extension = fileName.substring(i);
        return extension;
    }

    /**
     * URLベースパスを返す.
     * @return URLベースパス
     */
    public String getBasePath() {
        HttpServletRequest request =
                (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext()
                    .getRequest();
        return String.format(BASE_PATH,
            request.getScheme(),
            SystemConfig.getValue(KEY_SERVER_NAME),
            getPort(request),
            request.getContextPath());
    }

    /**
     * URLベースパスを返す.
     * @return URLベースパス
     */
    public String getContextPath() {
        HttpServletRequest request =
                (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext()
                    .getRequest();
        return request.getContextPath();
    }

    private String getPort(HttpServletRequest request) {
        final int portHttp = 80;
        final int portHttps = 443;
        switch (request.getServerPort()) {
        case portHttp:
        case portHttps:
            return "";
        default:
            return String.format(":%s", request.getServerPort());
        }
    }


    /**
     * ファイルダウンロードに特化した{@link OutputStream}.
     * <p>
     * 最初に書込系のメソッドが呼ばれるまでストリームをOpenしないのが特徴.
     * </p>
     */
    class DownloadOutputStream extends OutputStream {

        /** request. */
        private HttpServletRequest request;
        /** response. */
        private HttpServletResponse response;
        /** ダウンロードするファイル名. */
        private String fileName;

        /** ファイル名をそのまま加工しない場合はtrue. */
        private boolean downloadByRealName;

        /** 元となる{@link OutputStream}. */
        private OutputStream out;

        /**
         * ダウンロードストリームを生成するための情報を指定してインスタンス化する.
         * @param request HttpServletRequest
         * @param response HttpServletResponse
         * @param fileName ファイル名
         */
        public DownloadOutputStream(
                HttpServletRequest request, HttpServletResponse response, String fileName) {
            this.request = request;
            this.response = response;
            this.fileName = fileName;
        }

        /**
         * ダウンロードストリームを生成するための情報を指定してインスタンス化する.
         * @param request HttpServletRequest
         * @param response HttpServletResponse
         * @param fileName ファイル名
         * @param downloadByRealName ファイル名をそのまま加工しない場合はtrue
         */
        public DownloadOutputStream(
                HttpServletRequest request,
                HttpServletResponse response,
                String fileName,
                boolean downloadByRealName) {
            this.request = request;
            this.response = response;
            this.fileName = fileName;
            this.downloadByRealName = downloadByRealName;
        }

        public boolean isOpen() {
            return out != null;
        }

        private OutputStream getOutputStream() throws IOException {
            if (!isOpen()) {
                setDownloadResponseHeader(request, response, fileName, downloadByRealName, -1);
                out = getServletOutputStream(response);
            }
            return out;
        }

        /* (non-Javadoc)
         * @see java.io.OutputStream#write(int)
         */
        @Override
        public void write(int b) throws IOException {
            getOutputStream().write(b);
        }

        /* (non-Javadoc)
         * @see java.io.OutputStream#write(byte[])
         */
        @Override
        public void write(byte[] b) throws IOException {
            getOutputStream().write(b);
        }

        /* (non-Javadoc)
         * @see java.io.OutputStream#write(byte[], int, int)
         */
        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            getOutputStream().write(b, off, len);
        }

        /* (non-Javadoc)
         * @see java.io.OutputStream#flush()
         */
        @Override
        public void flush() throws IOException {
            if (isOpen()) {
                getOutputStream().flush();
            }
        }

        /* (non-Javadoc)
         * @see java.io.OutputStream#close()
         */
        @Override
        public void close() throws IOException {
            if (isOpen()) {
                getOutputStream().close();
            }
        }
    }
}

