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
package jp.co.opentone.bsol.framework.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.dbunit.Assertion;
import org.dbunit.DataSourceBasedDBTestCase;
import org.dbunit.DatabaseUnitException;
import org.dbunit.IDatabaseTester;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITableMetaData;
import org.dbunit.dataset.excel.XlsDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import jp.co.opentone.bsol.framework.test.dataset.bean.BeanDataSet;
import jp.co.opentone.bsol.framework.test.dataset.bean.BeanTable;
import jp.co.opentone.bsol.framework.test.dataset.bean.EmptyDataSet;
import jp.co.opentone.bsol.framework.test.util.ExpectedMessageStringGenerator;
import junit.framework.AssertionFailedError;

/**
 * Daoのテストクラスの親クラス. データベース接続に関する各種共通処理を提供する.
 *
 * @author opentone
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:scope.xml", "classpath:applicationContextTest.xml",
"classpath:daoContextTest.xml" })
@Transactional
@Rollback
public abstract class AbstractDaoTestCase extends DataSourceBasedDBTestCase {

    public static final String RESOURCE_ROOT = "src/test/resources";

    /**
     * テストに使用するデータソース.
     */
    @Resource(name = "testDataSource")
    private DataSource dataSource;

    private DataTypeFactorySelector factorySelector = new DataTypeFactorySelector();

    @Before
    public void customSetup() throws Exception {
        super.getDatabaseTester().setSchema("linkbinder_test");

        // マスタデータ
        File resource = new File(getPjUserResourceName());
        if (resource.exists()) {
            IDataSet ds = new XlsDataSet(new FileInputStream(resource));
            IDatabaseConnection connection = getConnection();
            try {
                DatabaseOperation.REFRESH.execute(connection, ds);
            } finally {
                closeConnection(connection);
            }
        }

        super.setUp();

    }
    @After
    public void customTeardown() throws Exception {
    }

//    /**
//     * 各テストメソッド実行前の初期処理.
//     *
//     * @throws Exception
//     *             Exception
//     */
//    @Override
//    @Before
//    public void setUp() throws Exception {
//
//        // AmbiguousTableNameException対策
//        // 明示的に接続するスキーマを指定する
//        // if (useSchema) {
//        // //TODO 非推奨のメソッドになってしまったので代替策を考える
//        // super.getDatabaseTester().setSchema(DB_SCHEMA);
//        // }
//
//        super.setUp();
//    }
//
    /**
     * テストで使用するデータソースを返す.
     *
     * @return DataSource
     */
    @Override
    protected DataSource getDataSource() {
        return dataSource;
    }

    @Override
    protected void setUpDatabaseConfig(DatabaseConfig config) {
        super.setUpDatabaseConfig(config);
        try {

            IDatabaseTester databaseTester = getDatabaseTester();
            IDatabaseConnection connection = databaseTester.getConnection();
            config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY,
                factorySelector.getDataTypeFactory(connection));
        } catch (Exception ignore) {
            ignore.printStackTrace();
        }
    }

    /**
     * DataSetの期待値と、beanの内容が等しいか検証する.
     * <p>
     * 比較対象となるのは、expectedに定義されている項目のみ. それ以外の項目は無視される.
     * </p>
     *
     * @param expected
     *            期待値
     * @param actual
     *            実測値. Java Bean
     * @throws DatabaseUnitException
     *             検証処理に失敗
     */
    public void assertDataSetEquals(IDataSet expected, Object actual) throws DatabaseUnitException {
        List<Object> beans = new ArrayList<Object>();
        beans.add(actual);

        assertDataSetEquals(expected, beans);
    }

    /**
     * DataSetの期待値と、Listに格納されたBeanの内容が等しいか検証する.
     * <p>
     * 比較対象となるのは、expectedに定義されている項目のみ. それ以外の項目は無視される.
     * </p>
     *
     * @param expected
     *            期待値
     * @param actual
     *            実測値. DataSet内のTable1行に対応したJava Beanが格納されたリスト
     * @throws DatabaseUnitException
     *             検証処理に失敗
     */
    public void assertDataSetEquals(IDataSet expected, List<?> actual) throws DatabaseUnitException {
        String tableName = null;
        if (actual.size() > 0) {
            tableName = actual.get(0).getClass().getSimpleName();
        }

        // 簡単のため、BeanTableのメタデータ(列名等)は期待値のメタデータをそのまま流用する
        ITableMetaData metaData = expected.getTableMetaData(tableName);
        BeanDataSet actualDataSet = new BeanDataSet(tableName, actual, metaData);

        Assertion.assertEquals(expected, actualDataSet);
    }

    public void assertDataSetEquals(IDataSet expected, List<?>...actuals) throws DatabaseUnitException {
        BeanDataSet actualDataSet = new BeanDataSet();
        for (List<?> actual : actuals) {
            String tableName = null;
            if (actual.size() > 0 ) {
                tableName = actual.get(0).getClass().getSimpleName();
            }
            ITableMetaData metaData = expected.getTableMetaData(tableName);
            BeanTable table = new BeanTable(tableName, actual, metaData);
            actualDataSet.addTable(table);
        }
        Assertion.assertEquals(expected, actualDataSet);
    }

    /**
     * 指定されたExcelファイルの内容をDataSetに変換して返す.
     * <p>
     * ファイルはテストクラスと同一パッケージ内に格納されている前提.
     * </p>
     *
     * @param fileName
     *            パスを含まないファイル名
     * @return DataSet
     * @throws DataSetException
     *             DataSetへの変換に失敗
     * @throws IOException
     *             ファイル読み込みに失敗
     */
    public IDataSet newDataSet(String fileName) throws DataSetException, IOException {
        String packageName = getClass().getName().replaceAll(getClass().getSimpleName(), "");
        String dir = packageName.replace('.', '/');

        String file = String.format("%s/%s/%s", RESOURCE_ROOT, dir, fileName);
        return new XlsDataSet(new File(file));
    }

    /**
     * テストクラス名と同名のExcelファイルのデータをDataSetに変換して返す.
     * <p>
     * ファイルはテストクラスと同一パッケージ内に格納されている前提.
     * </p>
     */
    protected IDataSet getDataSet() throws Exception {
        File resource = new File(getResourceName());
        if (resource.exists()) {
            // テストクラス名の規約からロードするDataSetを決定する
            return new XlsDataSet(new FileInputStream(resource));
        }

        return new EmptyDataSet();
    }

    private String getResourceName() {
        String className = getClass().getName();
        String resourceName = className.replace('.', '/') + ".xls";
        return String.format("%s/%s", RESOURCE_ROOT, resourceName);
    }

    private String getPjUserResourceName() {
        String resourceName = "testdata-pj_user.xls";
        return String.format("%s/%s", RESOURCE_ROOT, resourceName);
    }

    public void insertTestDataForCurrentTest() throws Exception {
        insertTestData(getDataSet());
    }

    public void deleteTestDataForCurrentTest() throws Exception {
        insertTestData(getDataSet());
    }


    public void insertTestData(IDataSet ds) throws Exception {
        IDatabaseConnection connection = getConnection();
        try {
            DatabaseOperation.CLEAN_INSERT.execute(connection, ds);
        } finally {
            closeConnection(connection);
        }
    }

    public void deleteTestData(IDataSet ds) throws Exception {
        IDatabaseConnection connection = getConnection();
        try {
            DatabaseOperation.DELETE_ALL.execute(connection, ds);
        } finally {
            closeConnection(connection);
        }
    }

    public void closeConnection(IDatabaseConnection con) throws SQLException {
        if (con != null) {
            con.close();
        }
    }
    /**
     * 期待されるメッセージ文字列を生成して返す.
     * <p>
     * 生成したメッセージに次の文字が残っている場合は{@link AssertionFailedError}が発生する.
     * <ul>
     * <li>$action$</li>
     * <li>{n}</li>
     * </ul>
     * </p>
     * @param msg メッセージ
     * @param actionName アクション名
     * @param vars メッセージ内の変数を置換する値
     * @return 期待されるメッセージ文字列
     */
    protected String createExpectedMessageString(
        String msg, String actionName, Object... vars) {
        return ExpectedMessageStringGenerator.generate(msg, actionName, vars);
    }

//    public DatabaseConnection getConnection()
//                throws SQLException,
//                    CannotGetJdbcConnectionException,
//                    DatabaseUnitException {
//        DatabaseConnection con = new DatabaseConnection(DataSourceUtils.getConnection(dataSource));
//        DatabaseConfig config = con.getConfig();
//        config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY,
//                factorySelector.getDataTypeFactory(con));
//
//        return con;
//    }
}
