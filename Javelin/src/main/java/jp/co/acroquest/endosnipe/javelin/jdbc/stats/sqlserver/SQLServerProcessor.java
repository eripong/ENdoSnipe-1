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
package jp.co.acroquest.endosnipe.javelin.jdbc.stats.sqlserver;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import jp.co.acroquest.endosnipe.common.db.AbstractExecutePlanChecker;
import jp.co.acroquest.endosnipe.common.db.SQLServerExecutePlanChecker;
import jp.co.acroquest.endosnipe.common.logger.SystemLogger;
import jp.co.acroquest.endosnipe.javelin.jdbc.common.JdbcJavelinMessages;
import jp.co.acroquest.endosnipe.javelin.jdbc.stats.AbstractProcessor;

/**
 * SQLServer固有の処理を行う。
 * @author eriguchi
 */
public class SQLServerProcessor extends AbstractProcessor
{
    /** JDBC接続URLがこの文字列で始まるとき、実行計画を取得する(SQL Server SQLJDBC Ver1.1以降) */
    public static final String EXPLAIN_TARGET_SQLSERVER = "jdbc:sqlserver";

    /** JDBC接続URLがこの文字列で始まるとき、実行計画を取得する(SQL Server SQLJDBC Ver1.0) */
    public static final String EXPLAIN_TARGET_SQLSERVER_1_0 = "jdbc:sqljdbc";

    /** SQL Server で実行計画を取るための設定コマンド */
    public static final String SQLSERVER_SHOWPLAN = "SHOWPLAN_XML";

    /** SQL Server で実行計画取得を開始するための設定コマンド */
    private static final String SHOWPLAN_ON = "SET " + SQLSERVER_SHOWPLAN + " ON;";

    /** SQL Server で実行計画取得を終了するための設定コマンド */
    private static final String SHOWPLAN_OFF = "SET " + SQLSERVER_SHOWPLAN + " OFF;";

    /**
     * {@inheritDoc}
     */
    public boolean isTarget(final String jdbcUrl)
    {
        boolean isSqlServer =
                jdbcUrl.startsWith(EXPLAIN_TARGET_SQLSERVER)
                        || jdbcUrl.startsWith(EXPLAIN_TARGET_SQLSERVER_1_0);
        return isSqlServer;
    }

    /**
     * SQL Serverで実行計画を取得する。
     * 
     * @param stmt ステートメント
     * @param originalSql SQL文
     * @param args 引数。
     * @return 実行計画
     * @throws SQLException ResultSetクローズ時にエラーが発生したとき
     */
    public String getOneExecPlan(final Statement stmt, final String originalSql, final List<?> args)
        throws SQLException
    {
        // 実行計画取得に失敗した場合にargsにセットする文字列
        StringBuffer execPlanText = new StringBuffer("EXPLAIN PLAN failed.");

        Statement planStmt = null;
        ResultSet resultSet = null;
        try
        {
            planStmt = stmt.getConnection().createStatement();

            // PreparedStatementなら、ドライバ内部のSQL文字列を用いてキャッシュから取得を試みる。
            // Statementなら、フラグ変更で実行計画を取得する。
            if (stmt instanceof PreparedStatement)
            {
                try
                {
                    // SQLServerPreparedStatement#preparedSQLに、SQL文の内部形式が格納されている
                    Field preparedSQL = stmt.getClass().getDeclaredField("preparedSQL");

                    // SQLServerPreparedStatement#preparedTypeDefinitionsに、変数の型が格納されている
                    Field preparedTypeDefinitions =
                            stmt.getClass().getDeclaredField("preparedTypeDefinitions");
                    preparedSQL.setAccessible(true);
                    preparedTypeDefinitions.setAccessible(true);
                    String typeDefinitionsString = (String)preparedTypeDefinitions.get(stmt);
                    StringBuffer internalSQL = new StringBuffer();
                    if (typeDefinitionsString != null && typeDefinitionsString.length() > 0)
                    {
                        internalSQL.append("(");
                        internalSQL.append(typeDefinitionsString);
                        internalSQL.append(")");
                    }
                    internalSQL.append((String)preparedSQL.get(stmt));
                    String internalSQLText = new String(internalSQL);

                    // キャッシュテーブルの中から、SQL Handle と Plan Handle で絞り込み、
                    // 内部形式のSQL文に一致するものを探す
                    String sql =
                            "SELECT query_plan " + "FROM sys.dm_exec_query_stats qs "
                                    + "CROSS APPLY sys.dm_exec_sql_text(qs.sql_handle) "
                                    + "CROSS APPLY sys.dm_exec_query_plan(qs.plan_handle) "
                                    + "WHERE text='" + internalSQLText.replaceAll("'", "''") + "'";
                    resultSet = planStmt.executeQuery(sql);
                    if (resultSet.next())
                    {
                        // SQL文に対応する実行計画が見つかったら、
                        // 最初のものを取り出す（同じものが複数返ってくる場合があるので）
                        execPlanText.setLength(0);
                        execPlanText.append(resultSet.getString("query_plan"));
                        execPlanText.append('\n');
                    }
                }
                catch (NoSuchFieldException e)
                {
                    String key =
                            "javelin.jdbc.stats.sqlserver.SQLServerProcessor.NoSuchFieldExceptionMessage";
                    String message = JdbcJavelinMessages.getMessage(key);
                    SystemLogger.getInstance().warn(message, e);
                }
            }
            else
            {
                planStmt.addBatch(SHOWPLAN_ON);
                planStmt.executeBatch();

                // 実行計画を取るSQL文の送信＆実行計画の取得
                resultSet = planStmt.executeQuery(originalSql);

                // 検索された行数分ループ
                execPlanText.setLength(0);
                while (resultSet.next())
                {
                    // 実行計画を取得
                    String planTableOutput = resultSet.getString(1);
                    // 結合
                    execPlanText.append(planTableOutput);
                    execPlanText.append('\n');
                }

                // 実行計画取得を解除する
                planStmt.addBatch(SHOWPLAN_OFF);
                planStmt.executeBatch();
            }
        }
        catch (SQLException sqle)
        {
            // DBアクセスエラーが発生した場合は標準エラー出力に出力しておく。
            SystemLogger.getInstance().warn(sqle);
        }
        catch (IllegalAccessException iae)
        {
            // 想定外の例外が発生した場合は標準エラー出力に出力しておく。
            SystemLogger.getInstance().warn(iae);
        }
        catch (RuntimeException ex)
        {
            // 想定外の例外が発生した場合は標準エラー出力に出力しておく。
            SystemLogger.getInstance().warn(ex);
        }
        finally
        {
            // リソース解放
            try
            {
                if (resultSet != null)
                {
                    resultSet.close();
                }
            }
            finally
            {
                if (planStmt != null)
                {
                    planStmt.close();
                }
            }
        }

        return execPlanText.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractExecutePlanChecker<?> getExecutePlanChecker()
    {
        return new SQLServerExecutePlanChecker();
    }
}
