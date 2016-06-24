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

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.commons.io.FileUtils;

/**
 * 指定されたファイルの内容でコレポン文書の本文を更新する。 障害検証用データ投入のための一時クラス。
 * @author opentone
 */
public class CorresponBodyUpdater {

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {

        Connection con = getConnection();
        PreparedStatement stmt = null;
        boolean success = true;
        try {
            con.setAutoCommit(false);

            stmt = con.prepareStatement("UPDATE correspon SET body = ? WHERE id = ?");
            execute(stmt);

            success = true;
        } finally {
            if (success) {
                con.commit();
            } else {
                con.rollback();
            }

            if (stmt != null) stmt.close();
            con.close();
        }

    }

    static void execute(PreparedStatement stmt) throws Exception {
        String dir = "/path/to/修正対象コレポンのBody";

        stmt.setString(1, FileUtils.readFileToString(new File(dir, "ID6564LOG.txt")));
        stmt.setLong(2, 1093);
        stmt.executeUpdate();

        stmt.setString(1, FileUtils.readFileToString(new File(dir, "ID6565LOG.txt")));
        stmt.setLong(2, 1094);
        stmt.executeUpdate();

        stmt.setString(1, FileUtils.readFileToString(new File(dir, "ID6661LOG.txt")));
        stmt.setLong(2, 1095);
        stmt.executeUpdate();
    }

    static Connection getConnection() throws ClassNotFoundException, SQLException {
        String driverClassName = "oracle.jdbc.driver.OracleDriver";
        String url = "jdbc:oracle:thin:linkbinder/linkbinder@192.168.1.222:1521";
        String username = "linkbinder";
        String password = "linkbinder";

        Class.forName(driverClassName);
        return DriverManager.getConnection(url, username, password);
    }

}
