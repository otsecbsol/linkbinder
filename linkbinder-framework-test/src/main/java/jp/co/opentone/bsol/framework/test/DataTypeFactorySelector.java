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

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.datatype.DefaultDataTypeFactory;
import org.dbunit.dataset.datatype.IDataTypeFactory;
import org.dbunit.ext.db2.Db2DataTypeFactory;
import org.dbunit.ext.h2.H2DataTypeFactory;
import org.dbunit.ext.hsqldb.HsqldbDataTypeFactory;
import org.dbunit.ext.mssql.MsSqlDataTypeFactory;
import org.dbunit.ext.mysql.MySqlDataTypeFactory;

/**
 * DBUnitの接続定義に合わせ適切な{@link IDataTypeFactory}を選択する.
 * @author opentone
 */
public class DataTypeFactorySelector {

    /**
     * データベース製品毎の{@link IDataTypeFactory}格納コンテナ.
     */
    private static final Map<String, Class<? extends IDataTypeFactory>> FACTORIES;
    static {
        FACTORIES = new HashMap<String, Class<? extends IDataTypeFactory>>();
        FACTORIES.put("sqlserver", MsSqlDataTypeFactory.class);
        FACTORIES.put("oracle", ExtendedOracle10DataTypeFactory.class);
//        FACTORIES.put("oracle", Oracle10DataTypeFactory.class);
        FACTORIES.put("db2", Db2DataTypeFactory.class);
        FACTORIES.put("mysql", MySqlDataTypeFactory.class);
        FACTORIES.put("hsqldb", HsqldbDataTypeFactory.class);
        FACTORIES.put("h2", H2DataTypeFactory.class);
    }

    /**
     * 空のインスタンスを生成する.
     */
    public DataTypeFactorySelector() {
    }

    /**
     * 現在接続中データベースに合わせた{@link IDataTypeFactory}を返す.
     * @param conn
     *            データベースへの接続
     * @return DataTypeFactory
     * @throws SQLException
     *             接続情報へのアクセスに失敗
     */
    public IDataTypeFactory getDataTypeFactory(IDatabaseConnection conn) throws SQLException {
        String vendor = getVendor(conn);
        if (vendor == null) {
            return createDefaultDataTypeFactory();
        }
        Class<? extends IDataTypeFactory> factoryClass = FACTORIES.get(vendor);
        if (factoryClass == null) {
            return createDefaultDataTypeFactory();
        }

        try {
            return factoryClass.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private String getVendor(IDatabaseConnection conn) throws SQLException {
        String url = conn.getConnection().getMetaData().getURL();
        // jdbc:vendorName:option://url
        String[] token = url.split(":");
        if (token.length > 2) {
            return token[1];
        }
        return null;
    }

    private IDataTypeFactory createDefaultDataTypeFactory() {
        return new DefaultDataTypeFactory();
    }
}
