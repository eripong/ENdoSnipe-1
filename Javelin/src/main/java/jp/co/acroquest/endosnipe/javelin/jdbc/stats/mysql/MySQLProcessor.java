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
package jp.co.acroquest.endosnipe.javelin.jdbc.stats.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import jp.co.acroquest.endosnipe.common.db.AbstractExecutePlanChecker;
import jp.co.acroquest.endosnipe.common.db.MySQLExecutePlanChecker;
import jp.co.acroquest.endosnipe.javelin.jdbc.common.SqlUtil;
import jp.co.acroquest.endosnipe.javelin.jdbc.stats.AbstractProcessor;

/**
 * MySQL用
 * @author eriguchi
 */
public class MySQLProcessor extends AbstractProcessor
{
    /** 実行計画結果の列数。 */
    private static final int COLUMN_MAX = 10;

    /** 取得した実行計画の先頭につけるヘッダ。 */
    private static final String EXEC_PLAN_HEADER =
            "MySQL Explain Plan:" + "\nid,select_type,table,type" + ",possible_keys,key,key_len,"
                    + "ref,rows,Extra\n";

    /** JDBC接続URLがこの文字列で始まるとき、実行計画を取得する(MySQL) */
    public static final String EXPLAIN_TARGET = "jdbc:mysql";

    /**
     * {@inheritDoc}
     */
    public boolean isTarget(final String jdbcUrl)
    {
        return jdbcUrl.startsWith(EXPLAIN_TARGET);
    }

    /**
     * MySQLで実行計画を取得する。
     * 
     * @param stmt ステートメント
     * @param originalSql SQL文
     * @param args 引数。
     * @return 実行計画
     * @throws SQLException Statementクローズ時にエラーが発生したとき
     */
    public String getOneExecPlan(final Statement stmt, final String originalSql, List<?> args)
        throws SQLException
    {
        String sql = SqlUtil.removeHeadComment(originalSql);
        int length = "SELECT".length();
        if (sql.length() < length || sql.substring(0, length).equalsIgnoreCase("SELECT") == false)
        {
            return "";
        }

        StringBuilder sqlBuffer = new StringBuilder();
        sqlBuffer.append("EXPLAIN ");
        sqlBuffer.append(originalSql);

        String planSql = sqlBuffer.toString();
        PreparedStatement planPreparedStatement = null;

        ResultSet result = null;
        StringBuffer execPlanText = new StringBuffer();
        try
        {
            Connection connection = stmt.getConnection();
            planPreparedStatement = connection.prepareStatement(planSql);

            if (args == null)
            {
                args = SqlUtil.getJdbcJavelinBindValByRef(stmt);
            }
            if (args != null)
            {
                for (int index = 0; index < args.size(); index++)
                {
                    planPreparedStatement.setObject(index + 1, args.get(index));
                }
            }

            result = planPreparedStatement.executeQuery();

            execPlanText.append(EXEC_PLAN_HEADER);

            while (result.next())
            {
                for (int index = 1; index <= COLUMN_MAX; index++)
                {
                    if (index > 1)
                    {
                        execPlanText.append(",");
                    }
                    execPlanText.append(result.getObject(index));
                }
                execPlanText.append("\n");
            }
        }
        finally
        {
            try
            {
                if (result != null)
                {
                    result.close();
                }
            }
            finally
            {
                if (planPreparedStatement != null)
                {
                    planPreparedStatement.close();
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
        return new MySQLExecutePlanChecker();
    }
}
