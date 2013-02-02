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
package jp.co.acroquest.endosnipe.javelin.jdbc.stats;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Set;

import jp.co.acroquest.endosnipe.common.db.AbstractExecutePlanChecker;
import jp.co.acroquest.endosnipe.javelin.jdbc.common.SqlUtil;

/**
 * DBProcessorの抽象クラス。
 * 開発者は、このクラスを継承してDBProcessorを作成する。
 * 
 * @author eriguchi
 */
public abstract class AbstractProcessor implements DBProcessor
{
    /**
     * PreparedStatementの実行計画を取得する。
     * getExecPlanを呼び出すのみ。
     *
     * @param stmt ステートメント
     * @param originalSql SQL文
     * @param args 引数。
     * @return 実行計画
     * 
     * @throws SQLException 実行計画取得時にエラーが発生した場合。
     */
    public String getExecPlanPrepared(final Statement stmt, final String originalSql,
            final List<?> args)
        throws SQLException
    {
        // 実行計画を取得（DBMSの種類によって分岐）
        List<String> sqlList = SqlUtil.splitSqlStatement(originalSql);
        StringBuilder execPlanText = new StringBuilder();
        for (String sql : sqlList)
        {
            if (SqlUtil.checkDml(sql))
            {
                execPlanText.append(getOneExecPlan(stmt, sql, args));
            }
        }

        return execPlanText.toString();
    }

    /**
     * {@inheritDoc}
     */
    public String execPlan(final Statement stmt, final String originalSqlElement,
            final Statement planStmt)
        throws SQLException
    {
        StringBuffer buffer = new StringBuffer();

        List<String> sqlList = SqlUtil.splitSqlStatement(originalSqlElement);
        for (String sql : sqlList)
        {
            if (SqlUtil.checkDml(sql))
            {
                buffer.append(getOneExecPlan(stmt, sql, null));
            }
        }

        return buffer.toString();
    }

    /**
     * {@inheritDoc}
     */
    public void startSqlTrace(final Connection connection)
    {
        // Do Nothing
        return;
    }

    /**
     * {@inheritDoc}
     */
    public void postPrepareStatement(final String sql, final PreparedStatement pstmt)
        throws SQLException
    {
        // Do Nothing
        return;
    }
    
    /**
     * {@inheritDoc}
     */
    public Set<String> checkFullScan(String executePlan)
    {
        AbstractExecutePlanChecker<?> executePlanChecker = this.getExecutePlanChecker();
        
        Set<String> fullScanTableNames = executePlanChecker
                .getFullScanTableNameSet(executePlan, null);
        
        return fullScanTableNames;
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean needsLock()
    {
        return false;
    }
    
    /**
     * {@inheritDoc}
     */
    public abstract AbstractExecutePlanChecker<?> getExecutePlanChecker();
}
