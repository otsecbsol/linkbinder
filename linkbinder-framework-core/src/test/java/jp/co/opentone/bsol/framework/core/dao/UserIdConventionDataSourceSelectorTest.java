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
package jp.co.opentone.bsol.framework.core.dao;

import static org.junit.Assert.*;

import java.net.BindException;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.h2.tools.Server;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import jp.co.opentone.bsol.framework.core.ProcessContext;
import jp.co.opentone.bsol.framework.core.config.SystemConfig;
import jp.co.opentone.bsol.framework.core.exception.ApplicationFatalRuntimeException;

/**
 * {@link UserIdConventionDataSourceSelector}のテストケース.
 * @author opentone
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:daoContextTest.xml", "classpath:commonTestContext.xml"})
public class UserIdConventionDataSourceSelectorTest extends AbstractJUnit4SpringContextTests {

    /**
     * テスト対象.
     */
    @Resource
    private UserIdConventionDataSourceSelector selector;

    /**
     * テスト用データベースサーバー.
     */
    private static Server DB_SERVER;

    /**
     * テスト開始前の準備. データベースサーバーを起動する.
     * @throws Exception
     */
    @BeforeClass
    public static void classSetUp() throws Exception {
        try {
            DB_SERVER =
                    Server.createTcpServer(new String[]{ "-baseDir", "./data", "-tcpPort", "9092" });
            DB_SERVER.start();
        } catch (Exception e) {
            if (e.getCause() instanceof BindException) {
                // 既に起動済かもしれないので無視
                e.getCause().printStackTrace();
            } else {
                throw e;
            }
        }
    }

    /**
     * テスト終了後の処理. データベースサーバーを停止する.
     * @throws Exception
     */
    @AfterClass
    public static void classTeardown() throws Exception {
        DB_SERVER.shutdown();
    }

    @After
    public void tearDown() throws Exception {
        ProcessContext.getCurrentContext().setValue("user", null);
    }

    /**
     * {@link UserIdConventionDataSourceSelector#select()}の検証.
     * @throws Exception
     */
    @Test
    public void testSelectProduction() throws Exception {
        setUserId("00001");
        assertEquals(getDataSource("productionDataSource"), selector.select());
    }

    /**
     * {@link UserIdConventionDataSourceSelector#select()}の検証.
     * @throws Exception
     */
    @Test
    public void testSelectDemo() throws Exception {
        setUserId("ZZB001");
        assertEquals(getDataSource("demoDataSource"), selector.select());
    }

    /**
     * {@link UserIdConventionDataSourceSelector#select()}の検証.
     * @throws Exception
     */
    @Test
    public void testSelectTest() throws Exception {
        setUserId("ZZA001");
        assertEquals(getDataSource("testDataSource"), selector.select());
    }

    /**
     * {@link UserIdConventionDataSourceSelector#select()}の検証.
     * ユーザーID未設定で例外が発生するケース.
     * @throws Exception
     */
    @Test(expected = ApplicationFatalRuntimeException.class)
    public void setSelectExceptionEmptyUserId() throws Exception {
        setUserId(null);

        selector.select();
    }

    /**
     * {@link UserIdConventionDataSourceSelector#select()}の検証.
     * ユーザー未設定で例外が発生するケース.
     * @throws Exception
     */
    @Test(expected = ApplicationFatalRuntimeException.class)
    public void setSelectExceptionEmptyUser() throws Exception {
        selector.select();
    }

    private void setUserId(String userId) {
        ProcessContext c = ProcessContext.getCurrentContext();

        c.setValue(SystemConfig.KEY_USER_ID, userId);
    }

    private DataSource getDataSource(String name) {
        return (DataSource) applicationContext.getBean(name);
    }
}
