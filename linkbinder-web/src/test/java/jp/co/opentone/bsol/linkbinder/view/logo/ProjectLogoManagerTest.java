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
package jp.co.opentone.bsol.linkbinder.view.logo;

import static org.junit.Assert.*;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.httpclient.util.DateUtil;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import jp.co.opentone.bsol.framework.core.config.SystemConfig;
import mockit.Mock;
import mockit.MockUp;

/**
 * {@link ProjectLogoManager}のテストケース.
 * @author opentone
 */
public class ProjectLogoManagerTest {
    /**
     * テスト対象.
     */
    private ProjectLogoManager projectLogoManager = new ProjectLogoManager();

    public static final File testLogo = new File("src/test/resources/test-default.png");
    /**
     * テスト前準備.
     */
    @Before
    public void setUp() throws Exception {
        new MockSystemConfig();

        MockSystemConfig.CONFIG_PROJECT_LOGO_DIR = "/tmp";
        MockSystemConfig.CONFIG_PROJECT_LOGO_DEFAULT = "default.png";
        MockSystemConfig.CONFIG_PROJECT_LOGO_EXTENSION = ".png";

        createLogoFiles();
    }


    public static void createLogoFiles() throws Exception {
        //  テストで使用するロゴファイルを準備
        FileUtils.copyFile(testLogo, new File("/tmp/test-default.png"));
        FileUtils.copyFile(testLogo, new File("/tmp/test-project.png"));
    }

    /**
     * 後始末.
     */
    @After
    public void tearDown() {
        new MockSystemConfig().tearDown();
    }

    /**
     * 引数null＆デフォルト画像が存在しない.
     */
    @Test
    public void testGet1(){
        ProjectLogo pl = projectLogoManager.get(null);
        assertNull(pl);
    }

    /**
     * 引数""＆デフォルト画像が存在しない.
     */
    @Test
    public void testGet2(){
        ProjectLogo pl = projectLogoManager.get("");
        assertNull(pl);
    }

    /**
     * 引数null＆デフォルト画像が存在する.
     */
    @Test
    public void testGet3(){
        MockSystemConfig.CONFIG_PROJECT_LOGO_DEFAULT = "test-default.png";

        String testFile = MockSystemConfig.CONFIG_PROJECT_LOGO_DIR + "/" + MockSystemConfig.CONFIG_PROJECT_LOGO_DEFAULT;
        File f = new File(testFile);
        if (!f.exists()) {
            fail("テスト用のファイル[" + testFile + "] を用意してください");
            return;
        }

        ProjectLogo expected = getExpectedData(testFile);
        ProjectLogo actual = projectLogoManager.get(null);

        assertNotNull(actual);
        assertArrayEquals(expected.getImage(), actual.getImage());
        assertEquals(expected.getLastModified(), actual.getLastModified());
    }

    /**
     * 引数""＆デフォルト画像が存在する.
     */
    @Test
    public void testGet4(){
        MockSystemConfig.CONFIG_PROJECT_LOGO_DEFAULT = "test-default.png";

        String testFile = MockSystemConfig.CONFIG_PROJECT_LOGO_DIR + "/" + MockSystemConfig.CONFIG_PROJECT_LOGO_DEFAULT;
        File f = new File(testFile);
        if (!f.exists()) {
            fail("テスト用のファイル[" + testFile + "] を用意してください");
            return;
        }

        ProjectLogo expected = getExpectedData(testFile);
        ProjectLogo actual = projectLogoManager.get("");

        assertNotNull(actual);
        assertArrayEquals(expected.getImage(), actual.getImage());
        assertEquals(expected.getLastModified(), actual.getLastModified());
    }

    /**
     * 引数あり＆プロジェクト画像が存在しない＆デフォルト画像が存在しない.
     */
    @Test
    public void testGet5(){
        String projectId = "not_exist_project";
        ProjectLogo actual = projectLogoManager.get(projectId);
        assertNull(actual);
    }

    /**
     * 引数あり＆プロジェクト画像が存在する＆デフォルト画像が存在しない.
     */
    @Test
    public void testGet6(){
        String projectId = "test-project";
        String testFile = MockSystemConfig.CONFIG_PROJECT_LOGO_DIR + "/" + projectId + ".png";
        File f = new File(testFile);
        if (!f.exists()) {
            fail("テスト用のファイル[" + testFile + "] を用意してください");
            return;
        }

        ProjectLogo expected = getExpectedData(testFile);
        ProjectLogo actual = projectLogoManager.get(projectId);

        assertNotNull(actual);
        assertArrayEquals(expected.getImage(), actual.getImage());
        assertEquals(expected.getLastModified(), actual.getLastModified());
    }


    /**
     * 引数あり＆プロジェクト画像が存在しない＆デフォルト画像が存在する
     */
    @Test
    public void testGet7(){
        String projectId = "not_exist_project";

        MockSystemConfig.CONFIG_PROJECT_LOGO_DEFAULT = "test-default.png";
        String testFile = MockSystemConfig.CONFIG_PROJECT_LOGO_DIR + "/" + MockSystemConfig.CONFIG_PROJECT_LOGO_DEFAULT;
        File f = new File(testFile);
        if (!f.exists()) {
            fail("テスト用のファイル[" + testFile + "] を用意してください");
            return;
        }

        ProjectLogo expected = getExpectedData(testFile);
        ProjectLogo actual = projectLogoManager.get(projectId);

        assertNotNull(actual);
        assertArrayEquals(expected.getImage(), actual.getImage());
        assertEquals(expected.getLastModified(), actual.getLastModified());
    }

    /**
     * 引数あり＆プロジェクト画像が存在する＆デフォルト画像が存在する
     */
    @Test
    public void testGet8(){
        String projectId = "test-project";

        String testFile = MockSystemConfig.CONFIG_PROJECT_LOGO_DIR + "/" + projectId + ".png";
        File f = new File(testFile);
        if (!f.exists()) {
            fail("テスト用のファイル[" + testFile + "] を用意してください");
            return;
        }

        MockSystemConfig.CONFIG_PROJECT_LOGO_DEFAULT = "test-default.png";
        String testFile2 = MockSystemConfig.CONFIG_PROJECT_LOGO_DIR + "/" + MockSystemConfig.CONFIG_PROJECT_LOGO_DEFAULT;
        File f2  = new File(testFile2);
        if (!f2.exists()) {
            fail("テスト用のファイル[" + testFile2 + "] を用意してください");
            return;
        }

        ProjectLogo expected = getExpectedData(testFile);
        ProjectLogo actual = projectLogoManager.get(projectId);

        assertNotNull(actual);
        assertArrayEquals(expected.getImage(), actual.getImage());
        assertEquals(expected.getLastModified(), actual.getLastModified());
    }

    /**
     * projectId:存在しないプロジェクト、ifModifiedSince:null
     */
    @Test
    public void testIsModified1(){
        String projectId = "not_exist_project";

        MockSystemConfig.CONFIG_PROJECT_LOGO_DEFAULT = "test-default.png";
        String testFile = MockSystemConfig.CONFIG_PROJECT_LOGO_DIR + "/" + MockSystemConfig.CONFIG_PROJECT_LOGO_DEFAULT;
        File f = new File(testFile);
        if (!f.exists()) {
            fail("テスト用のファイル[" + testFile + "] を用意してください");
            return;
        }

        assertTrue(projectLogoManager.isModified(projectId, null));
    }

    /**
     * projectId:存在しないプロジェクト、ifModifiedSince:""
     */
    @Test
    public void testIsModified2(){
        String projectId = "not_exist_project";

        MockSystemConfig.CONFIG_PROJECT_LOGO_DEFAULT = "test-default.png";
        String testFile = MockSystemConfig.CONFIG_PROJECT_LOGO_DIR + "/" + MockSystemConfig.CONFIG_PROJECT_LOGO_DEFAULT;
        File f = new File(testFile);
        if (!f.exists()) {
            fail("テスト用のファイル[" + testFile + "] を用意してください");
            return;
        }

        assertTrue(projectLogoManager.isModified(projectId, ""));
    }

    /**
     * projectId:存在しないプロジェクト、ifModifiedSince:ロゴと同じ
     */
    @Test
    public void testIsModified3(){
        String projectId = "not_exist_project";

        MockSystemConfig.CONFIG_PROJECT_LOGO_DEFAULT = "test-default.png";
        String testFile = MockSystemConfig.CONFIG_PROJECT_LOGO_DIR + "/" + MockSystemConfig.CONFIG_PROJECT_LOGO_DEFAULT;
        File f = new File(testFile);
        if (!f.exists()) {
            fail("テスト用のファイル[" + testFile + "] を用意してください");
            return;
        }
        String ifModifiedSince = DateUtil.formatDate(new Date(f.lastModified()));
        assertFalse(projectLogoManager.isModified(projectId, ifModifiedSince));
    }

    /**
     * projectId:存在しないプロジェクト、ifModifiedSince:ロゴ + 1秒
     */
    @Test
    public void testIsModified4(){
        String projectId = "not_exist_project";

        MockSystemConfig.CONFIG_PROJECT_LOGO_DEFAULT = "test-default.png";
        String testFile = MockSystemConfig.CONFIG_PROJECT_LOGO_DIR + "/" + MockSystemConfig.CONFIG_PROJECT_LOGO_DEFAULT;
        File f = new File(testFile);
        if (!f.exists()) {
            fail("テスト用のファイル[" + testFile + "] を用意してください");
            return;
        }
        String ifModifiedSince = DateUtil.formatDate(new Date(f.lastModified() + 1000));
        assertTrue(projectLogoManager.isModified(projectId, ifModifiedSince));
    }


    /**
     * projectId:存在しないプロジェクト、ifModifiedSince:ロゴ - 1秒
     */
    @Test
    public void testIsModified5(){
        String projectId = "not_exist_project";

        MockSystemConfig.CONFIG_PROJECT_LOGO_DEFAULT = "test-default.png";
        String testFile = MockSystemConfig.CONFIG_PROJECT_LOGO_DIR + "/" + MockSystemConfig.CONFIG_PROJECT_LOGO_DEFAULT;
        File f = new File(testFile);
        if (!f.exists()) {
            fail("テスト用のファイル[" + testFile + "] を用意してください");
            return;
        }
        String ifModifiedSince = DateUtil.formatDate(new Date(f.lastModified() - 1000));
        assertTrue(projectLogoManager.isModified(projectId, ifModifiedSince));
    }

    /**
     * projectId:存在するプロジェクト、ifModifiedSince:ロゴと同じ
     */
    @Test
    public void testIsModified6(){
        String projectId = "test-project";

        String testFile = MockSystemConfig.CONFIG_PROJECT_LOGO_DIR + "/" + projectId + ".png";
        File f = new File(testFile);
        if (!f.exists()) {
            fail("テスト用のファイル[" + testFile + "] を用意してください");
            return;
        }

        MockSystemConfig.CONFIG_PROJECT_LOGO_DEFAULT = "test-default.png";
        String testFile2 = MockSystemConfig.CONFIG_PROJECT_LOGO_DIR + "/" + MockSystemConfig.CONFIG_PROJECT_LOGO_DEFAULT;
        File f2 = new File(testFile2);
        if (!f2.exists()) {
            fail("テスト用のファイル[" + testFile2 + "] を用意してください");
            return;
        }

        String ifModifiedSince = DateUtil.formatDate(new Date(f.lastModified()));
        assertFalse(projectLogoManager.isModified(projectId, ifModifiedSince));
    }

    /**
     * projectId:存在するプロジェクト、ifModifiedSince:ロゴ + 1秒
     */
    @Test
    public void testIsModified7(){
        String projectId = "test-project";

        String testFile = MockSystemConfig.CONFIG_PROJECT_LOGO_DIR + "/" + projectId + ".png";
        File f = new File(testFile);
        if (!f.exists()) {
            fail("テスト用のファイル[" + testFile + "] を用意してください");
            return;
        }

        MockSystemConfig.CONFIG_PROJECT_LOGO_DEFAULT = "test-default.png";
        String testFile2 = MockSystemConfig.CONFIG_PROJECT_LOGO_DIR + "/" + MockSystemConfig.CONFIG_PROJECT_LOGO_DEFAULT;
        File f2 = new File(testFile2);
        if (!f2.exists()) {
            fail("テスト用のファイル[" + testFile2 + "] を用意してください");
            return;
        }

        String ifModifiedSince = DateUtil.formatDate(new Date(f.lastModified() + 1000));
        assertTrue(projectLogoManager.isModified(projectId, ifModifiedSince));
    }

    /**
     * projectId:存在するプロジェクト、ifModifiedSince:ロゴ - 1秒
     */
    @Test
    public void testIsModified8(){
        String projectId = "test-project";

        String testFile = MockSystemConfig.CONFIG_PROJECT_LOGO_DIR + "/" + projectId + ".png";
        File f = new File(testFile);
        if (!f.exists()) {
            fail("テスト用のファイル[" + testFile + "] を用意してください");
            return;
        }

        MockSystemConfig.CONFIG_PROJECT_LOGO_DEFAULT = "test-default.png";
        String testFile2 = MockSystemConfig.CONFIG_PROJECT_LOGO_DIR + "/" + MockSystemConfig.CONFIG_PROJECT_LOGO_DEFAULT;
        File f2 = new File(testFile2);
        if (!f2.exists()) {
            fail("テスト用のファイル[" + testFile2 + "] を用意してください");
            return;
        }

        String ifModifiedSince = DateUtil.formatDate(new Date(f.lastModified() - 1000));
        assertTrue(projectLogoManager.isModified(projectId, ifModifiedSince));
    }

    /**
     * デフォルトロゴが存在しない、ifModifiedSince:null
     */
    @Test
    public void testIsModified9(){
        String projectId = "not_exist_project";
        assertTrue(projectLogoManager.isModified(projectId, null));
    }

    /**
     * デフォルトロゴが存在しない、ifModifiedSince:""
     */
    @Test
    public void testIsModified10(){
        String projectId = "not_exist_project";
        assertTrue(projectLogoManager.isModified(projectId, ""));
    }

    /**
     * デフォルトロゴが存在しない、ifModifiedSince:現在日時
     */
    @Test
    public void testIsModified11(){
        String projectId = "not_exist_project";
        String ifModifiedSince = DateUtil.formatDate(new Date(System.currentTimeMillis()));
        assertTrue(projectLogoManager.isModified(projectId, ifModifiedSince));
    }

    /**
     * projectId:存在するプロジェクト、ifModifiedSince:不正な文字列
     */
    @Test
    public void testIsModified15(){
        String projectId = "test-project";

        String testFile = MockSystemConfig.CONFIG_PROJECT_LOGO_DIR + "/" + projectId + ".png";
        File f = new File(testFile);
        if (!f.exists()) {
            fail("テスト用のファイル[" + testFile + "] を用意してください");
            return;
        }

        MockSystemConfig.CONFIG_PROJECT_LOGO_DEFAULT = "test-default.png";
        String testFile2 = MockSystemConfig.CONFIG_PROJECT_LOGO_DIR + "/" + MockSystemConfig.CONFIG_PROJECT_LOGO_DEFAULT;
        File f2 = new File(testFile2);
        if (!f2.exists()) {
            fail("テスト用のファイル[" + testFile2 + "] を用意してください");
            return;
        }

        SimpleDateFormat rfc1123DateFormat =  new SimpleDateFormat("E, dd MMM yyyy hh:mm:ss.000 zzz");
        rfc1123DateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        String ifModifiedSince = rfc1123DateFormat.format(new Date(f.lastModified()));

        assertTrue(projectLogoManager.isModified(projectId, ifModifiedSince));
    }


    private ProjectLogo getExpectedData(String testFile){
        BufferedInputStream bis = null;
        File f = new File(testFile);
        byte[] data = new byte[(int) f.length()];
        try{
            bis = new BufferedInputStream(new FileInputStream(testFile));
            bis.read(data);
            bis.close();
        }catch(Exception e){
            fail("例外が発生しました");
        }
        ProjectLogo expected = new ProjectLogo();
        expected.setImage(data);
        expected.setLastModified(f.lastModified());

        return expected;
    }

    public static class MockSystemConfig extends MockUp<SystemConfig> {
        public static String CONFIG_PROJECT_LOGO_DIR;
        public static String CONFIG_PROJECT_LOGO_DEFAULT;
        public static String CONFIG_PROJECT_LOGO_EXTENSION;

        private static final String PROJECT_LOGO_DIR = "project.logo.dir";
        private static final String PROJECT_LOGO_DEFAULT = "project.logo.default";
        private static final String PROJECT_LOGO_EXTENSION = "project.logo.extension";

        @Mock
        public String getValue(String label){
            if (PROJECT_LOGO_DIR.equals(label)) {
                return CONFIG_PROJECT_LOGO_DIR;
            } else if (PROJECT_LOGO_DEFAULT.equals(label)) {
                return CONFIG_PROJECT_LOGO_DEFAULT;
            } else if (PROJECT_LOGO_EXTENSION.equals(label)) {
                return CONFIG_PROJECT_LOGO_EXTENSION;
            }

            return null;
        }
    }
}
