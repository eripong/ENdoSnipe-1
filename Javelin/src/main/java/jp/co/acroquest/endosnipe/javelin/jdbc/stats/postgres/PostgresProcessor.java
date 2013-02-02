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
package jp.co.acroquest.endosnipe.javelin.jdbc.stats.postgres;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import jp.co.acroquest.endosnipe.common.db.AbstractExecutePlanChecker;
import jp.co.acroquest.endosnipe.common.db.PostgreSQLExecutePlanChecker;
import jp.co.acroquest.endosnipe.common.logger.SystemLogger;
import jp.co.acroquest.endosnipe.javelin.jdbc.common.SqlUtil;
import jp.co.acroquest.endosnipe.javelin.jdbc.instrument.PreparedStatementPair;
import jp.co.acroquest.endosnipe.javelin.jdbc.stats.AbstractProcessor;
import jp.co.acroquest.endosnipe.javelin.jdbc.stats.JdbcJavelinRecorder;
import jp.co.acroquest.endosnipe.javelin.jdbc.stats.JdbcJavelinStatement;

/**
 * Postgres固有の処理を行う。
 * @author eriguchi
 */
public class PostgresProcessor extends AbstractProcessor
{

    /** JDBC接続URLがこの文字列で始まるとき、実行計画を取得する(PostgreSQL) */
    public static final String EXPLAIN_TARGET_POSTGRE = "jdbc:postgresql";

    /**
     * {@inheritDoc}
     */
    public boolean isTarget(final String jdbcUrl)
    {
        return jdbcUrl.startsWith(EXPLAIN_TARGET_POSTGRE);
    }

    /**
     * PostgreSQLでPreparedStatementの実行計画を取得する。
     *
     * @param stmt ステートメント
     * @param sql SQL文
     * @param args 引数。
     * @return 実行計画
     */
    @Override
    public String getExecPlanPrepared(final Statement stmt, final String sql, final List<?> args)
    {
        // 実行計画を格納するバッファ
        StringBuffer execPlanText = new StringBuffer();

        try
        {
            if(stmt instanceof JdbcJavelinStatement)
            {
                // 実行計画取得用に準備されたPreparedStatementを実行する
                JdbcJavelinStatement jdbcJavelinStatement = (JdbcJavelinStatement)stmt;
                PreparedStatementPair[] pstmtList = jdbcJavelinStatement.getStmtForPlan();
                if (pstmtList != null)
                {
                    for (PreparedStatementPair pair : pstmtList)
                    {
                        if (pair.isDml() == false)
                        {
                            continue;
                        }
                        PreparedStatement pstmt = pair.getPreparedStatement();
                        ResultSet resultSet = pstmt.executeQuery();
                        if (resultSet != null)
                        {
                            // 検索された行数分ループ
                            while (resultSet.next())
                            {
                                // 実行計画を取得
                                String planTableOutput = resultSet.getString(1);
                                // 結合
                                execPlanText.append(planTableOutput);
                                execPlanText.append("\n");
                            }
                            resultSet.close();
                        }
                        execPlanText.append("\n");
                    }
                }
            }
        }
        catch (Exception ex)
        {
            SystemLogger.getInstance().warn(ex);
        }

        if (execPlanText.length() == 0)
        {
            execPlanText.append("EXPLAIN PLAN failed.");
        }

        return execPlanText.toString();
    }

    /**
     * PostgreSQLでStatementの実行計画を取得する。
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
        ResultSet resultSet = null;

        // 実行計画取得に失敗した場合にargsにセットする文字列
        StringBuffer execPlanText = new StringBuffer("EXPLAIN PLAN failed.");

        Statement planStmt = null;
        try
        {
            planStmt = stmt.getConnection().createStatement();

            if (JdbcJavelinRecorder.getConfig().isVerbosePlanForPostgres())
            {
                resultSet = planStmt.executeQuery("EXPLAIN VERBOSE " + originalSql);
            }
            else
            {
                resultSet = planStmt.executeQuery("EXPLAIN " + originalSql);
            }

            if (resultSet != null)
            {
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
            }
        }
        catch (Exception ex)
        {
            // 自動生成された catch ブロック
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
    public void postPrepareStatement(final String sql, final PreparedStatement pstmt)
        throws SQLException
    {
        Connection connection = pstmt.getConnection();
        // PostgresSQL かつ 実行計画取得設定がON なら
        // 実行計画取得用PreparedStatementを作成する

        // セミコロンで区切られた複数のStatementを分割して、
        // それぞれPreparedStatementを作成する。
        List<String> sqlList = SqlUtil.splitSqlStatement(sql);
        int sqlListSize = sqlList.size();
        PreparedStatementPair[] pstmtList = new PreparedStatementPair[sqlListSize];
        for (int index = 0; index < sqlListSize; index++)
        {
            String splitedSql = sqlList.get(index);
            boolean isDml = SqlUtil.checkDml(splitedSql);
            pstmtList[index] =
                    new PreparedStatementPair(connection, "EXPLAIN VERBOSE " + splitedSql,
                                              isDml);
        }

        // 作成したPreparedStatementをフィールドに登録する
        try
        {
            JdbcJavelinStatement jdbcJavelinStatement = (JdbcJavelinStatement)pstmt;
            jdbcJavelinStatement.setStmtForPlan(pstmtList);
        }
        catch (Exception ex)
        {
            SystemLogger.getInstance().warn(ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractExecutePlanChecker<?> getExecutePlanChecker()
    {
        return new PostgreSQLExecutePlanChecker();
    }
}
