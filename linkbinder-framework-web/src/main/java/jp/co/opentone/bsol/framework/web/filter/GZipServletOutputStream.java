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
import java.util.zip.GZIPOutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;

/**
 * GZIP形式の圧縮に対応した {@link ServletOutputStream}.
 * @author opentone
 */
public class GZipServletOutputStream extends ServletOutputStream {

    /** 元となる {@link ServletOutputStream}. */
    private ServletOutputStream sOut;
    /** 元となる {@link HttpServletResponse}. */
    private HttpServletResponse response;
    /** データをGZip形式で出力するStream. */
    private GZIPOutputStream gOut;

    public GZipServletOutputStream(HttpServletResponse response) throws IOException {
        this.response = response;
    }

    private void init() throws IOException {
        if (gOut != null) {
            return;
        }

        response.addHeader("Content-Encoding", "gzip");
        sOut = response.getOutputStream();
        gOut = new GZIPOutputStream(sOut);
    }

    /* (non-Javadoc)
     * @see java.io.OutputStream#write(int)
     */
    @Override
    public void write(int b) throws IOException {
        init();
        gOut.write(b);
    }

    /* (non-Javadoc)
     * @see java.io.OutputStream#write(byte[])
     */
    @Override
    public void write(byte[] b) throws IOException {
        init();
        gOut.write(b);
    }

    /* (non-Javadoc)
     * @see java.io.OutputStream#write(byte[], int, int)
     */
    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        init();
        gOut.write(b, off, len);
    }

    public void finish() throws IOException {
        if (gOut != null) {
            gOut.flush();
            gOut.finish();
        }
    }

    /* (non-Javadoc)
     * @see java.io.OutputStream#flush()
     */
    @Override
    public void flush() throws IOException {
        if (gOut != null) {
            gOut.flush();
        }
        if (sOut != null) {
            sOut.flush();
        }
    }

    /* (non-Javadoc)
     * @see java.io.OutputStream#close()
     */
    @Override
    public void close() throws IOException {
        //  何もしない
    }

    public void reset() {
        gOut = null;
    }

    @Override
    public boolean isReady() {
        // TODO 自動生成されたメソッド・スタブ
        return false;
    }

    @Override
    public void setWriteListener(WriteListener arg0) {
        // TODO 自動生成されたメソッド・スタブ

    }
}
