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
package jp.co.opentone.bsol.linkbinder;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.DriverManager;
import java.util.Properties;

import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;

import jp.co.opentone.bsol.framework.test.DataTypeFactorySelector;

public class DataImporter {

    private static DataTypeFactorySelector factorySelector = new DataTypeFactorySelector();

    private static Properties loadProperties(String resourceName) throws IOException {

        Properties p = new Properties();
        p.load(new FileInputStream(resourceName));

        return p;
    }

    /**
     * @param args
     *            args
     * @throws Exception
     *             Exception
     */
    public static void main(String[] args) throws Exception {

        String prop = "src/test/resources/jdbc.properties";
        if (args.length > 0) {
            prop = args[0];
        }
        Properties p = loadProperties(prop);

        String input = "import.xls";
        if (args.length > 1) {
            input = args[1];
        }

        IDatabaseConnection con = createConnection(p);
        DatabaseConfig config = con.getConfig();
            config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY,
                               factorySelector.getDataTypeFactory(con));
        TableDataOperator op = new TableDataOperator();
        // op.deleteInsert(con, input);
        op.insertUpdate(con, input);
    }

    private static IDatabaseConnection createConnection(Properties p) throws Exception {
        String url = p.getProperty("jdbc.url");
        String user = p.getProperty("jdbc.username");
        String password = p.getProperty("jdbc.password");

        Class.forName(p.getProperty("jdbc.driverClassName"));
        return new DatabaseConnection(DriverManager.getConnection(url, user, password), user);
    }
}
