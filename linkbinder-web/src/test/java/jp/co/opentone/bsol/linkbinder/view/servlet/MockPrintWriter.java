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
package jp.co.opentone.bsol.linkbinder.view.servlet;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

/**
 * @author opentone
 */
public class MockPrintWriter extends PrintWriter {
    private static int RET_TESTCASE;

    /**
     * @param out
     */
    public MockPrintWriter(Writer out) {
        super(out);
    }

    /**
     * @param out
     */
    public MockPrintWriter(OutputStream out) {
        super(out);
    }

    /**
     * @param fileName
     * @throws FileNotFoundException
     */
    public MockPrintWriter(String fileName) throws FileNotFoundException {
        super(fileName);
        MockPrintWriter.RET_TESTCASE = Integer.valueOf(fileName);
    }

    /**
     * @param file
     * @throws FileNotFoundException
     */
    public MockPrintWriter(File file) throws FileNotFoundException {
        super(file);
    }

    /**
     * @param out
     * @param autoFlush
     */
    public MockPrintWriter(Writer out, boolean autoFlush) {
        super(out, autoFlush);
    }

    /**
     * @param out
     * @param autoFlush
     */
    public MockPrintWriter(OutputStream out, boolean autoFlush) {
        super(out, autoFlush);
    }

    /**
     * @param fileName
     * @param csn
     * @throws FileNotFoundException
     * @throws UnsupportedEncodingException
     */
    public MockPrintWriter(String fileName, String csn) throws FileNotFoundException,
        UnsupportedEncodingException {
        super(fileName, csn);
    }

    /**
     * @param file
     * @param csn
     * @throws FileNotFoundException
     * @throws UnsupportedEncodingException
     */
    public MockPrintWriter(File file, String csn) throws FileNotFoundException,
        UnsupportedEncodingException {
        super(file, csn);
    }

    /**
     *
     */
    @Override
    public void println(String statement) {
        switch (RET_TESTCASE) {
        case 1:
            assertEquals("paramerter error : maked time", statement);
            break;
        case 2:
            assertEquals("paramerter error : hash code", statement);
            break;
        case 3:
            assertEquals("paramerter error : no value", statement);
            break;
        case 4:
            assertEquals("paramerter error : no value", statement);
            break;
        case 5:
            assertEquals("paramerter error : no value", statement);
            break;
        case 6:
            assertEquals("paramerter error : no value", statement);
            break;
        }
    }
    /**
     *
     */
    @Override
    public void println(int i) {
        switch (RET_TESTCASE) {
        case 7:
            assertEquals(i,0);
            break;
        case 8:
            assertEquals(i,0);
            break;
        }
    }

}
