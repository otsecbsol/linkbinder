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
package jp.co.opentone.bsol.framework.web.filter;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * GZIP形式の圧縮に対応した {@link HttpServletResponse} のラッパークラス.
 * @author opentone
 */
public class GZipResponseWrapper extends HttpServletResponseWrapper {

    /** 元の {@link HttpServletResponse}. */
    private HttpServletResponse response;
    /** 元の {@link ServletOutputStream}. */
    private ServletOutputStream out;
    /** 元の {@link PrintWriter}. */
    private PrintWriter writer;

    /**
     * 元の {@link ServletOutputStream} をラップしてGZIPで圧縮したデータを出力する
     * {@link ServletOutputStream}.
     */
    private GZipServletOutputStream gOut;

    /**
     * @param response レスポンス
     */
    public GZipResponseWrapper(HttpServletResponse response) {
        super(response);
        this.response = response;
    }

    /**
     * 新しい {@link ServletOutputStream} を生成する.
     * @return 新しい {@link ServletOutputStream}
     * @throws IOException 生成に失敗
     */
    private ServletOutputStream newOutputStream() throws IOException {
        if (gOut == null) {
            gOut = new GZipServletOutputStream(response);
        }
        return gOut;
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletResponseWrapper#setContentLength(int)
     */
    @Override
    public void setContentLength(int len) {
        //  なにもしない
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletResponseWrapper#getOutputStream()
     */
    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        if (writer != null) {
            throw new IllegalStateException();
        }
        if (out == null) {
            out = newOutputStream();
        }
        return out;
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletResponseWrapper#getWriter()
     */
    @Override
    public PrintWriter getWriter() throws IOException {
        if (out != null) {
            throw new IllegalStateException();
        }
        if (writer == null) {
            writer = new PrintWriter(
                    new GZipResponseWriter(newOutputStream(),
                                        response.getCharacterEncoding()));
        }
        return writer;
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletResponseWrapper#flushBuffer()
     */
    @Override
    public void flushBuffer() throws IOException {
        if (gOut == null) {
            return;
        }
        gOut.flush();
        super.flushBuffer();
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletResponseWrapper#resetBuffer()
     */
    @Override
    public void resetBuffer() {
        super.resetBuffer();
        if (gOut != null) {
            gOut.reset();
        }
    }

    public void finish() throws IOException {
        if (gOut == null) {
            return;
        }
        gOut.finish();
    }
}
