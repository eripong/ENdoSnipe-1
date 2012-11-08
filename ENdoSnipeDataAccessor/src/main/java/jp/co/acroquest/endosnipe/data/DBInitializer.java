/*******************************************************************************
 * ENdoSnipe 5.0 - (https://github.com/endosnipe)
 * 
 * The MIT License (MIT)
 * 
 * Copyright (c) 2012 Acroquest Technology Co.,Ltd.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package jp.co.acroquest.endosnipe.data;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Date;

import jp.co.acroquest.endosnipe.common.logger.ENdoSnipeLogger;
import jp.co.acroquest.endosnipe.common.util.SQLUtil;
import jp.co.acroquest.endosnipe.data.db.DBManager;
import jp.co.acroquest.endosnipe.data.db.H2DBUtil;
import jp.co.acroquest.endosnipe.data.db.SQLExecutor;
import jp.co.acroquest.endosnipe.util.ResourceDataDaoUtil;

/**
 * ENdoSnipe 用データベースを初期化するためのクラスです。<br />
 * 
 * @author y-komori
 */
public class DBInitializer
{
    private static final String          DDL_PATH                  = "/ddl/ENdoSnipe.ddl";

    private static final String          POSTGRES_DDL_PATH         = "/ddl/ENdoSnipe_PostgreSQL.ddl";

    private static final String          DDL_SEQUENCE_PATH         = "/ddl/ENdoSnipeSequence.ddl";

    private static final String          TSV_MEASUREMENT_INFO_PATH = "/measurementInfo.tsv";

    private static final String          H2_FUNC_PATH                  = "/func/h2_func.sql";

    private static final ENdoSnipeLogger LOGGER                    =
                                                                       ENdoSnipeLogger.getLogger(
                                                                                                 DBInitializer.class,
                                                                                                 ENdoSnipeDataAccessorPluginProvider.INSTANCE);

    /** PostgreSQLデータベースのドライバクラス名称 */
    private static final String           POSTGRES_DRIVER = "org.postgresql.Driver";

    /** PostgreSQL接続用URIのプレフィクス */
    private static final String           POSTGRES_URI_PREFIX = "jdbc:postgresql://";

    private DBInitializer()
    {
        // Do nothing.
    }

    /**
     * 接続されたデータベースが初期化済みかどうかを調べます。<br />
     * 
     * @param con コネクション
     * @return 初期化済みの場合は <code>true</code>、そうでない場合は <code>false</code>。
     */
    public static boolean isInitialized(final Connection con)
    {
        if (con == null)
        {
            return false;
        }

        boolean initialized = false;
        String sql = null;

        if (DBManager.isDefaultDb())
        {
            sql = "SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_TYPE='TABLE'";
        }
        else
        {
            sql = "SELECT last_value FROM SEQ_LOG_ID";
        }
        Statement stmt = null;
        ResultSet rs = null;
        try
        {
            stmt = con.createStatement();
            rs = stmt.executeQuery(sql);
            if (rs.next() == true)
            {
                initialized = true;
            }
        }
        catch (SQLException ex)
        {
            // 初期化されていない場合に例外が発生するため、
            // INFO以上での出力とする。
            if (LOGGER.isInfoEnabled())
            {
                LOGGER.log(LogMessageCodes.DB_ACCESS_ERROR, ex, ex.getMessage());
            }
        }
        finally
        {
            SQLUtil.closeResultSet(rs);
            SQLUtil.closeStatement(stmt);
        }
        return initialized;
    }

    /**
     * 接続されたデータベースの初期化を行います。<br />
     * データベースが初期化済みの場合は何も行いません。<br />
     * 
     * @param con コネクション
     * @throws SQLException SQL発行に失敗した場合
     * @throws IOException 入出力エラーが発生した場合
     */
    public static void initialize(final Connection con)
        throws SQLException,
            IOException
    {
        if (isInitialized(con) == true)
        {
            return;
        }

        H2DBUtil.executeDDL(con, DDL_SEQUENCE_PATH);
        if (DBManager.isDefaultDb())
        {
            H2DBUtil.executeDDL(con, DDL_PATH);
        }
        else
        {
            H2DBUtil.executeDDL(con, POSTGRES_DDL_PATH);

            // 現在のデータを挿入するテーブルインデックス
            int startIndex = ResourceDataDaoUtil.getTableIndexToInsert(null);

            // 今年の西暦
            Calendar currentYearCalendar = Calendar.getInstance();
            int currentYear = currentYearCalendar.get(Calendar.YEAR);

            // パーティショニング用テーブルを作成する（MEASUREMENT_VALUE、JAVELIN_LOGテーブル）
            for (int index = 1; index <= ResourceDataDaoUtil.PARTITION_TABLE_COUNT; index++)
            {
                int year;
                if (index < startIndex)
                {
                    // 現在のデータを挿入するテーブルよりも前のテーブルを作成する場合、
                    // CHECK制約に入れる時刻の範囲は翌年の値を入れる
                    year = currentYear + 1;
                }
                else
                {
                    // 現在のデータを挿入するテーブル以降のテーブルを作成する場合、
                    // CHECK制約に入れる時刻の範囲は今年の値を入れる
                    year = currentYear;
                }

                createMeasurementValueTable(con, index, year);
                createJavelinLogTable(con, index, year);
            }
        }
        // MEASUREMENT_INFOテーブルを削除したので、初期データ投入もしない
        //initMeasurementInfoFromTsv(con, TSV_MEASUREMENT_INFO_PATH);

    }

    /**
     * 接続の度に必要な初期化を行います。
     * 
     * @param con コネクション
     * @throws SQLException SQL発行に失敗した場合
     * @throws IOException 入出力エラーが発生した場合
     */
    public static void reinitialize(Connection con)
        throws SQLException,
            IOException
    {
        initialize(con);
        if (DBManager.isDefaultDb())
        {
            H2DBUtil.executeDDL(con, H2_FUNC_PATH);
        }
        // MEASUREMENT_INFOテーブルを削除したので、初期データ投入もしない
        //initMeasurementInfoFromTsv(con, TSV_MEASUREMENT_INFO_PATH);
    }

// MEASUREMENT_INFOテーブルを削除したので、初期データ投入もしない
//    /**
//     * クラスパス上に存在する TSV ファイルを読み込み、 MEASUREMENT_INFO テーブルにデータを挿入します。<br />
//     *
//     * @param con コネクション
//     * @param path 読み込む TSV ファイルのパス
//     * @throws IOException 入出力エラーが発生した場合
//     * @throws SQLException SQLエラーが発生した場合
//     */
//    public static void initMeasurementInfoFromTsv(final Connection con, final String path)
//        throws IOException,
//            SQLException
//    {
//        final int COLUMN_COUNT = 4;
//
//        if (path == null)
//        {
//            throw new IllegalArgumentException("schemaPath can't be null");
//        }
//
//        try
//        {
//            MeasurementInfoDao.deleteAllWithoutJMX(con);
//        }
//        catch (SQLException sqle)
//        {
//            // 初期化されていない場合に例外が発生するため、
//            // INFO以上での出力とする。
//            if (LOGGER.isInfoEnabled())
//            {
//                LOGGER.log(LogMessageCodes.DB_ACCESS_ERROR, sqle, sqle.getMessage());
//            }
//        }
//        
//        BufferedReader reader = null;
//        try
//        {
//            InputStream is = H2DBUtil.class.getResourceAsStream(path);
//            reader = new BufferedReader(new InputStreamReader(is));
//
//
//            String line;
//            while ((line = reader.readLine()) != null)
//            {
//                String[] items = line.split("\t", COLUMN_COUNT);
//                if (items.length == COLUMN_COUNT)
//                {
//                    // CHECKSTYLE:OFF
//                    long measurementType = Long.parseLong(items[0]);
//                    String itemName = items[1];
//                    String displayName = items[2];
//                    String description = items[3];
//                    // CHECKSTYLE:ON
//                    MeasurementInfo measurementInfo =
//                            new MeasurementInfo(measurementType, itemName, displayName, description);
//                    MeasurementInfoDao.insert(con, measurementInfo);
//                }
//            }
//        }
//        finally
//        {
//            reader.close();
//        }
//    }

    /**
     * CHECK 制約名を生成します。
     *
     * @param tableName インデックスも含めたテーブル名
     * @param column カラム名
     * @return CHECK 制約名
     */
    public static String createCheckConstraintName(final String tableName, final String column)
    {
        return tableName + "_" + column + "_check";
    }

    /**
     * パーティショニングされた個々のテーブルに対して設定する CHECK 制約の文を生成します。
     *
     * @param column 制限をかけるカラム名
     * @param tableIndex テーブルインデックス
     * @param year 年
     * @return CHECK 制約の文
     */
    public static String createCheckConstraintText(final String column, final int tableIndex,
            final int year)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, 0, 1, 0, 0, 0);
        calendar.add(Calendar.DATE, (tableIndex - 1) * ResourceDataDaoUtil.DAY_OF_WEEK);
        Date startDate = calendar.getTime();
        if (tableIndex == ResourceDataDaoUtil.PARTITION_TABLE_COUNT)
        {
            // 最後のテーブルでは、テーブルに格納する終了日付はその年の12/31（なので終了日は翌年の1/1）
            calendar.set(year + 1, 0, 1, 0, 0, 0);
        }
        else
        {
            calendar.add(Calendar.DATE, ResourceDataDaoUtil.DAY_OF_WEEK);
        }
        Date endDate = calendar.getTime();
        String checkConstraint =
                String.format("CHECK ('%2$tY/%2$tm/%2$td' <= %1$s"
                        + " AND %1$s < '%3$tY/%3$tm/%3$td')", column, startDate, endDate);
        return checkConstraint;
    }

    /**
     * MEASUREMENT_VALUE_xx テーブルを作成します。
     *
     * @param con データベースコネクション
     * @param index 作成するテーブルのインデックス
     * @param year 西暦
     * @throws SQLException テーブル作成時に例外が発生した場合
     */
    private static void createMeasurementValueTable(final Connection con, int index, int year)
        throws SQLException
    {
        // MEASUREMENT_VALUE_xx テーブルを作成する
        String tableName =
                String.format("%s_%02d", TableNames.MEASUREMENT_VALUE, index);
        String checkConstraintName =
                createCheckConstraintName(tableName,
                                          "MEASUREMENT_TIME");
        String checkConstraint =
                createCheckConstraintText("MEASUREMENT_TIME", index, year);
        String createMeasurementValueSql =
                String.format("CREATE UNLOGGED TABLE %s (CONSTRAINT %s %s) INHERITS (%s)",
                              tableName,
                              checkConstraintName,
                              checkConstraint,
                              TableNames.MEASUREMENT_VALUE);
        SQLExecutor.executeSQL(con, createMeasurementValueSql, null);

        // MEASUREMENT_VALUE_xx テーブルにインデックスをつける
        String createIndexSql =
            String.format("CREATE INDEX IDX_%1$s_MEASUREMENT_TIME ON %1$s (MEASUREMENT_TIME)",
                          tableName);
        SQLExecutor.executeSQL(con, createIndexSql, null);
    }

    /**
     * JAVELIN_LOG_xx テーブルを作成します。
     *
     * @param con データベースコネクション
     * @param index 作成するテーブルのインデックス
     * @param year 西暦
     * @throws SQLException テーブル作成時に例外が発生した場合
     */
    private static void createJavelinLogTable(final Connection con, int index, int year)
        throws SQLException
    {
        // JAVELIN_LOG_xx テーブルを作成する
        String tableName =
                String.format("%s_%02d", TableNames.JAVELIN_LOG, index);
        String checkConstraintName =
                createCheckConstraintName(tableName, "END_TIME");
        String checkConstraint =
                createCheckConstraintText("END_TIME", index, year);
        String createJavelinLogSql =
                String.format("CREATE TABLE %s (CONSTRAINT %s %s) INHERITS (%s)",
                              tableName, checkConstraintName,
                              checkConstraint, TableNames.JAVELIN_LOG);
        SQLExecutor.executeSQL(con, createJavelinLogSql, null);

        // JAVELIN_LOG_xx テーブルにインデックスをつける
        String createStartTimeIndexSql =
            String.format("CREATE INDEX IDX_%1$s_START_TIME ON %1$s (START_TIME)",
                          tableName);
        String createEndTimeIndexSql =
            String.format("CREATE INDEX IDX_%1$s_END_TIME ON %1$s (END_TIME)",
                          tableName);
        SQLExecutor.executeSQL(con, createStartTimeIndexSql, null);
        SQLExecutor.executeSQL(con, createEndTimeIndexSql, null);
    }

    /**
     * 指定された名前のデータベースを作成する。
     * 
     * @param dbName データベース名
     * @return データベースの作成に成功したら <code>true</code>、そうでない場合は<code>false</code>
     */
    public static boolean createDatabase(String dbName)
    {
        if (DBManager.isDefaultDb() == true)
        {
            // H2の場合は何もしない
            return false;
        }
        return createPostgresDatabase(dbName);
    }

    /**
     * 指定された名前のデータベースをPostgreSQLで作成する。
     * 
     * @param dbName データベース名
     * @return データベースの作成に成功したら <code>true</code>、そうでない場合は<code>false</code>
     */
    private static boolean createPostgresDatabase(String dbName)
    {
        try
        {
            Class.forName(POSTGRES_DRIVER);
        }
        catch (ClassNotFoundException ex)
        {
            return false;
        }

        String uri = POSTGRES_URI_PREFIX + DBManager.getHostName() + ":" 
                                         + DBManager.getPort() + "/";
        Connection conn = null;
        Statement state = null;

        // データベースを作成し、その結果を返す
        try
        {
            conn = DriverManager.getConnection( uri,
                                                DBManager.getUserName(),
                                                DBManager.getPassword() );

            state = conn.createStatement();

            // データベース名を""で括る
            dbName = "\"" + dbName + "\"";

            return state.execute("CREATE DATABASE " + dbName + ";");
        }
        catch (SQLException sqlex)
        {
            return false;
        }
        finally
        {
            SQLUtil.closeStatement(state);
            SQLUtil.closeConnection(conn);
        }
    }

}
