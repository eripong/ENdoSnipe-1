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
package jp.co.acroquest.endosnipe.javelin.jdbc.stats.oracle;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import jp.co.acroquest.endosnipe.common.db.AbstractExecutePlanChecker;
import jp.co.acroquest.endosnipe.common.db.OracleExecutePlanChecker;
import jp.co.acroquest.endosnipe.common.logger.SystemLogger;
import jp.co.acroquest.endosnipe.javelin.jdbc.common.JdbcJavelinConfig;
import jp.co.acroquest.endosnipe.javelin.jdbc.stats.AbstractProcessor;
import jp.co.acroquest.endosnipe.javelin.jdbc.stats.JdbcJavelinRecorder;

/**
 * Oracle用
 * @author akiba
 *
 */
public class OracleProcessor extends AbstractProcessor
{
    /** JDBC接続URLがこの文字列で始まるとき、実行計画を取得する(Oracle Thin ドライバ) */
    public static final String EXPLAIN_TARGET_ORACLE = "jdbc:oracle";

    /** JDBC接続URLがこの文字列で始まるとき、実行計画を取得する(BEA WebLogic Type 4 JDBC Oracle ドライバ) */
    public static final String EXPLAIN_TARGET_BEA_ORACLE = "jdbc:bea:oracle:";
    
    /**
     * {@inheritDoc}
     */
    public boolean isTarget(final String jdbcUrl)
    {
        return jdbcUrl.startsWith(EXPLAIN_TARGET_ORACLE)
                || jdbcUrl.startsWith(EXPLAIN_TARGET_BEA_ORACLE);
    }

    /**
     * Oracleで実行計画を取得する。
     * 
     * @param stmt ステートメント
     * @param originalSql SQL文
     * @param args 引数。
     * @return 実行計画
     * @throws SQLException Statementクローズ時にエラーが発生したとき
     */
    public String getOneExecPlan(final Statement stmt, final String originalSql, final List<?> args)
        throws SQLException
    {
        JdbcJavelinConfig config = new JdbcJavelinConfig();

        // 実行計画取得に失敗した場合にargsにセットする文字列
        StringBuilder execPlanText = null;

        // 実行計画生成SQL文の生成    
        StringBuilder sql = new StringBuilder();
        sql.append("EXPLAIN PLAN FOR ");
        sql.append(originalSql);

        // 実行計画整形・取得SQLの生成。
        StringBuilder planTable = new StringBuilder();
        planTable.append("SELECT PLAN_TABLE_OUTPUT FROM TABLE"
                + "(DBMS_XPLAN.DISPLAY('PLAN_TABLE',NULL,'");
        planTable.append(config.getOutputOption());
        planTable.append("'))");

        // 実行計画を生成（PLANテーブルに展開）
        ResultSet resultSet = null;
        Statement planStmt = null;
        try
        {
            planStmt = stmt.getConnection().createStatement();

            planStmt.execute(sql.toString());

            // 実行計画を整形・取得
            resultSet = planStmt.executeQuery(planTable.toString());

            // 検索された行数分ループ
            execPlanText = new StringBuilder("");
            while (resultSet.next())
            {
                // PLAN_TABLE_OUTPUTを取得
                String planTableOutput = resultSet.getString(1);
                // 結合
                execPlanText.append(planTableOutput);
                execPlanText.append('\n');
            }
        }
        catch (SQLException ex)
        {
            execPlanText = new StringBuilder(JdbcJavelinRecorder.EXPLAIN_PLAN_FAILED);
            // DBアクセスエラー/想定外の例外が発生した場合はエラーログに出力しておく。
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
     * SQLトレース取得用SQLを発行する。
     * @param connection コネクション
     */
    @Override
    public void startSqlTrace(final Connection connection)
    {
        // SQLトレースのトレースIDを設定する。
        SimpleDateFormat dataFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String id = Thread.currentThread().getId() + "_" + dataFormat.format(new Date());

        Statement stmt = null;
        try
        {
            // SQLトレース取得用のSQLを実行する。
            stmt = connection.createStatement();
            stmt.execute(SET_TRACE_ID + id + "'");

            // 現時点までのSQLトレースを一旦すべて出力してから、再度開始する
            // ※OracleのSQLトレースは、最後の出力停止時点から現時点までを出力する(動きに見える)
            stmt.execute(START_SQL_TRACE);
        }
        catch (Exception ex)
        {
            SystemLogger.getInstance().warn(ex);
        }
        finally
        {
            try
            {
                if (stmt != null)
                {
                    stmt.close();
                }
            }
            catch (Exception ex)
            {
                SystemLogger.getInstance().warn(ex);
            }
        }
    }

    /**
     * SQLトレース終了用のSQLを発行する。
     * @param connection コネクション
     */
    public static void stopSqlTrace(final Connection connection)
    {
        Statement stmt = null;
        try
        {
            // SQLトレース終了用のSQLを実行する。
            stmt = connection.createStatement();
            stmt.execute(STOP_SQL_TRACE);
        }
        catch (Exception ex)
        {
            SystemLogger.getInstance().warn(ex);
        }
        finally
        {
            try
            {
                if (stmt != null)
                {
                    stmt.close();
                }
            }
            catch (Exception ex)
            {
                SystemLogger.getInstance().warn(ex);
            }
        }
    }

    /** SQLトレースのID */
    private static final String SET_TRACE_ID = "alter session set tracefile_identifier='";

    /** SQLトレース取得用SQL */
    private static final String START_SQL_TRACE = "alter session set sql_trace=true";

    /** SQLトレース終了用SQL */
    private static final String STOP_SQL_TRACE = "alter session set sql_trace=false";

    /**
     * {@inheritDoc}
     */
    public boolean needsLock()
    {
        return true;
    }
    
    /**
     * {@inheritDoc}
     */
    public AbstractExecutePlanChecker<?> getExecutePlanChecker()
    {
        return new OracleExecutePlanChecker();
    }
}
