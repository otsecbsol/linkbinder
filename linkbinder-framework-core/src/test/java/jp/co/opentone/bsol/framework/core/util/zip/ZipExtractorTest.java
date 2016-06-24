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
package jp.co.opentone.bsol.framework.core.util.zip;

import static org.junit.Assert.*;

import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import org.apache.tools.zip.ZipEntry;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author opentone
 */
public class ZipExtractorTest {

    /**
     * プロパティファイル名.
     */
    private static final String TEST_PROPERTY_FILE_NAME = "/linkbinder-framework-core-test.properties";

    /**
     * データディレクトリを表すプロパティキー.
     */
    private static final String KEY_TEST_DATA_DIR = "ZipExtractorTest.dir";

    /**
     * Zipエンコーディング.
     */
    private static final String ZIP_ENCODING = "Windows-31J";

    /**
     * テストデータのルートディレクトリパス.
     */
    private static String testDataDirPath;

    /**
     * @throws java.lang.Exception 例外.
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        InputStream is = null;
        try {
            is = ZipExtractorTest.class.getResourceAsStream(TEST_PROPERTY_FILE_NAME);
            Properties prop = new Properties();
            prop.load(is);
            testDataDirPath = prop.getProperty(KEY_TEST_DATA_DIR);
        } finally {
            if (null != is) {
                is.close();
            }
        }
    }

    /**
     * @throws java.lang.Exception 例外.
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    /**
     * @throws java.lang.Exception 例外.
     */
    @Before
    public void setUp() throws Exception {
    }

    /**
     * @throws java.lang.Exception 例外.
     */
    @After
    public void tearDown() throws Exception {
    }

    /**
     * {@link ZipExtractor#ZipExtractor(java.lang.String)}.
     * <p>正常系</p>
     */
    @Test
    public void testZipExtractor() {
        String zipPath = getFullPath("test001.zip");
        new ZipExtractor(zipPath, ZIP_ENCODING);
    }

    /**
     * {@link ZipExtractor#ZipExtractor(java.lang.String)}.
     * <p>異常系(引数がnull)</p>
     */
    @Test(expected = NullPointerException.class)
    public void testZipExtractorError01() {
        new ZipExtractor(null, ZIP_ENCODING);
    }

    /**
     * {@link ZipExtractor#ZipExtractor(java.lang.String)}.
     * <p>異常系(引数が空文字列)</p>
     */
    @Test(expected = IllegalArgumentException.class)
    public void testZipExtractorError02() {
        new ZipExtractor("", ZIP_ENCODING);
    }

    /**
     * {@link ZipExtractor#ZipExtractor(java.lang.String)}.
     * <p>異常系(引数に存在しないパス)</p>
     */
    @Test(expected = IllegalArgumentException.class)
    public void testZipExtractorError03() {
        new ZipExtractor("C:/abcdefghijk.zip", ZIP_ENCODING);
    }

    /**
     * {@link ZipExtractor#ZipExtractor(java.lang.String)}.
     * <p>異常系(Zip形式ではないファイルを指定)</p>
     */
    @Test(expected = ZipUtilRuntimeException.class)
    public void testZipExtractorError04() {
        String zipPath = getFullPath("test007.txt");
        new ZipExtractor(zipPath, ZIP_ENCODING);
    }

    /**
     * {@link ZipExtractor#getZipEntriesByPath(java.lang.String)} のためのテスト・メソッド.
     * <p>正規表現指定無し</p>
     */
    @Test
    public void testGetZipEntriesByPath01() {
        String zipPath = getFullPath("test001.zip");
        ZipExtractor zip = new ZipExtractor(zipPath, ZIP_ENCODING);
        List<ZipEntry> list = zip.getZipEntriesByPath();
        assertEquals(1, list.size());
        assertEquals("Zip抽出テスト.txt", list.get(0).getName());
    }

    /**
     * {@link ZipExtractor#getRootDirectory()} のためのテスト・メソッド.
     */
    @Test
    public void testGetRootDirectory() {
        // まったくフォルダ無し
        String zipPath = getFullPath("test003.zip");
        ZipExtractor zip = new ZipExtractor(zipPath, ZIP_ENCODING);
        String rootDir = zip.getRootDirectory();
        assertEquals("", rootDir);

        // 直下にファイル、ディレクトリの混在
        zipPath = getFullPath("test004.zip");
        zip = new ZipExtractor(zipPath, ZIP_ENCODING);
        rootDir = zip.getRootDirectory();
        assertEquals("", rootDir);

        // 直下にファイルはないが、複数のディレクトリの混在
        zipPath = getFullPath("test005.zip");
        zip = new ZipExtractor(zipPath, ZIP_ENCODING);
        rootDir = zip.getRootDirectory();
        assertEquals("", rootDir);

    }

    private String getFullPath(String fileName) {
        return String.format("%s/%s", testDataDirPath, fileName);
    }
}

