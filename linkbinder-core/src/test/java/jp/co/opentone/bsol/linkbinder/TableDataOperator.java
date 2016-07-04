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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.excel.XlsDataSet;
import org.dbunit.operation.DatabaseOperation;

/**
 * データベーステーブルのデータを操作するクラス.
 * <ul>
 * <li>Excelファイルのデータをデータベーステーブルにインポート</li>
 * <li>データベーステーブルのデータをExcelファイルにエクスポート</li>
 * </ul>
 * @author opentone
 */
public class TableDataOperator {

    /**
     * 出力ファイル名.
     */
    public static final String DEFAULT_OUTPUT_FILE_NAME = "src/test/resources/AllTable.xls";

    /**
     * 対象のテーブル名一覧.
     */
    private static final String[] TABLE_NAMES =
            { "issue_purpose", "workflow_pattern", "correspon_type", "project_correspon_type",
             "company", "project_company", "company_user", "site", "discipline", "correspon_group",
             "correspon_group_user", "custom_field", "project_custom_field", "custom_field_value",
             "correspon", "address_user", "address_correspon_group", "correspon_hierarchy",
             "correspon_read_status", "attachment", "person_in_charge", "correspon_custom_field",
             "workflow", "project_user_profile", "user_profile", };

    /**
     * 全てのテーブルのデータをExcelファイルにエクスポートする.
     * <ul>
     * <li>対象テーブル：すべて
     * <li>
     * <li>出力ファイル：DEFAULT_OUTPUT_FILE_NAME</li>
     * </ul>
     * @param con
     *            con
     * @throws Exception
     *             例外
     */
    public void export(IDatabaseConnection con) throws Exception {
        export(con, TABLE_NAMES, DEFAULT_OUTPUT_FILE_NAME);
    }

    /**
     * 指定されたテーブルのデータをExcelファイルにエクスポートする.
     * <ul>
     * <li>対象テーブル：tableNames
     * <li>
     * <li>出力ファイル：fileName</li>
     * </ul>
     * @param con
     *            データベース接続オブジェクト
     * @param tableNames
     *            対象のテーブル名
     * @param fileName
     *            出力ファイル名
     * @throws Exception
     *             例外
     */
    public void export(IDatabaseConnection con, String[] tableNames, String fileName)
        throws Exception {

        IDataSet ds = con.createDataSet(tableNames);
        OutputStream out = new FileOutputStream(new File(fileName));
        try {
            XlsDataSet.write(ds, out);
        } finally {
            out.close();
        }
    }

    /**
     * 指定されたExcelファイルのデータをデータベースのテーブルにINSERTする. 同一キーのレコードがある場合は上書きせず、処理が失敗する.
     * @param con
     *            データベース接続オブジェクト
     * @param fileName
     *            データファイル名
     * @throws Exception
     *             例外
     */
    public void insert(IDatabaseConnection con, String fileName) throws Exception {

        InputStream in = new FileInputStream(new File(fileName));
        try {
            XlsDataSet ds = new XlsDataSet(in);
            DatabaseOperation.INSERT.execute(con, ds);
        } finally {
            in.close();
        }
    }

    /**
     * 指定されたExcelファイルのデータをデータベースのテーブルにINSERTする. 同一キーのレコードがある場合はUPDATEする.
     * @param con
     *            データベース接続オブジェクト
     * @param fileName
     *            データファイル名
     * @throws Exception
     *             例外
     */
    public void insertUpdate(IDatabaseConnection con, String fileName) throws Exception {

        InputStream in = new FileInputStream(new File(fileName));
        try {
            XlsDataSet ds = new XlsDataSet(in);
            DatabaseOperation.REFRESH.execute(con, ds);
        } finally {
            in.close();
        }
    }

    /**
     * 指定されたExcelファイルのデータをデータベースのテーブルにINSERTする. テーブルに存在するレコードはすべて削除される.
     * @param con
     *            データベース接続オブジェクト
     * @param fileName
     *            データファイル名
     * @throws Exception
     *             例外
     */
    public void deleteInsert(IDatabaseConnection con, String fileName) throws Exception {

        InputStream in = new FileInputStream(new File(fileName));
        try {
            XlsDataSet ds = new XlsDataSet(in);
            DatabaseOperation.CLEAN_INSERT.execute(con, ds);
        } finally {
            in.close();
        }
    }
}
