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
package jp.co.opentone.bsol.linkbinder.util;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import jp.co.opentone.bsol.framework.test.AbstractTestCase;


/**
 * {@link AttachmentUtil}のテストケース.
 * @author opentone
 */
public class AttachmentUtilTest extends AbstractTestCase {

    private String fileName;

    @Before
    public void setUp() {
        cleanup();
    }

    @After
    public void teardown() {
        cleanup();
    }

    private void cleanup() {
        if (StringUtils.isNotEmpty(fileName)) {
            File f = new File(fileName);
            if (f.exists()) {
                f.delete();
            }
        }
    }

    /**
     * {@link AttachmentUtil#createTempporaryFile(String, String, java.io.InputStream)}
     * の検証.
     * @throws Exception
     */
    @Test
    public void testCreateTemporaryFile() throws Exception {
        String randomId = "test";
        String baseName = "attachmentUtilTest.txt";
        String data = "this is test.";
        ByteArrayInputStream in = new ByteArrayInputStream(data.getBytes());

        fileName = AttachmentUtil.createTempporaryFile(randomId, baseName, in);

        assertNotNull(fileName);

        File actual = new File(fileName);
        assertTrue(actual.exists());

        String actualData = FileUtils.readFileToString(actual);
        assertEquals(data, actualData);
    }
}
