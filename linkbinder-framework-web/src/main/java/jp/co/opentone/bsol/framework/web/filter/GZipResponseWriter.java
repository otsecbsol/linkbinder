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
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import sun.nio.cs.StreamEncoder;

/**
 * レスポンスストリームをGZIP形式で圧縮して書き出す.
 * @author opentone
 */
public class GZipResponseWriter extends Writer {

    /** データ出力用オブジェクト. */
    private StreamEncoder se;

    public GZipResponseWriter(OutputStream out, String charsetName)
        throws UnsupportedEncodingException {
        super(out);
        se = StreamEncoder.forOutputStreamWriter(out, this, charsetName);
    }

    public String getEncoding() {
        return se.getEncoding();
    }

    public void flushBuffer() throws IOException {
        se.flushBuffer();
    }

    /* (non-Javadoc)
     * @see java.io.Writer#write(char[], int, int)
     */
    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        se.write(cbuf, off, len);
        flushBuffer();
    }

    /* (non-Javadoc)
     * @see java.io.Writer#write(int)
     */
    @Override
    public void write(int c) throws IOException {
        se.write(c);
        flushBuffer();
    }

    /* (non-Javadoc)
     * @see java.io.Writer#write(java.lang.String, int, int)
     */
    @Override
    public void write(String str, int off, int len) throws IOException {
        se.write(str, off, len);
        flushBuffer();
    }

    /* (non-Javadoc)
     * @see java.io.Writer#flush()
     */
    @Override
    public void flush() throws IOException {
        se.flush();
    }

    /* (non-Javadoc)
     * @see java.io.Writer#close()
     */
    @Override
    public void close() throws IOException {
        se.close();
    }
}
