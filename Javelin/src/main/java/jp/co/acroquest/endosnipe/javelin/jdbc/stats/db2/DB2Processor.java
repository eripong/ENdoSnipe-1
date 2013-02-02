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
package jp.co.acroquest.endosnipe.javelin.jdbc.stats.db2;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import jp.co.acroquest.endosnipe.common.db.AbstractExecutePlanChecker;
import jp.co.acroquest.endosnipe.common.db.DB2ExecutePlanChecker;
import jp.co.acroquest.endosnipe.common.logger.SystemLogger;
import jp.co.acroquest.endosnipe.common.util.SQLUtil;
import jp.co.acroquest.endosnipe.javelin.jdbc.stats.AbstractProcessor;

/**
 * DB2固有の処理を行う。
 * @author ochiai
 */
public class DB2Processor extends AbstractProcessor
{

    /** JDBC接続URLがこの文字列で始まるとき、実行計画を取得する(DB2) */
    public static final String EXPLAIN_TARGET_DB2 = "jdbc:db2";
    
    /** Javelinログ中の改行文字を表す */
    public static final String NEW_LINE = "\n";
    
    /** Javelinログ中の区切り文字（カンマ）を表す */
    public static final String COMMA = ",";

    /**
     * {@inheritDoc}
     */
    public boolean isTarget(final String jdbcUrl)
    {
        return jdbcUrl.startsWith(EXPLAIN_TARGET_DB2);
    }

    /**
     * DB2でStatementの実行計画を取得する。
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
        StringBuilder execPlanText = new StringBuilder("Access Plan failed.");

        //DB2のEXPLAIN表に記入し、
        //EXPLAIN_OPERATORテーブルのOPERATOR_TYPEを得る
        String clearExplainTables = "DELETE FROM EXPLAIN_INSTANCE";
        String explainModeExplain = "SET CURRENT EXPLAIN MODE EXPLAIN";
        String explainModeNo = "SET CURRENT EXPLAIN MODE NO";
        
        //EXPLAIN表からフルスキャン、インデックススキャンの情報を取得
        String operatorType =
            "SELECT ope.OPERATOR_ID, ope.OPERATOR_TYPE, stm.OBJECT_NAME, ope.TOTAL_COST from EXPLAIN_OPERATOR ope LEFT JOIN EXPLAIN_STREAM stm ON ope.EXPLAIN_REQUESTER = stm.EXPLAIN_REQUESTER and ope.EXPLAIN_TIME = stm.EXPLAIN_TIME and ope.SOURCE_NAME = stm.SOURCE_NAME and ope.SOURCE_SCHEMA = stm.SOURCE_SCHEMA and ope.SOURCE_VERSION = stm.SOURCE_VERSION and ope.EXPLAIN_LEVEL = stm.EXPLAIN_LEVEL and ope.STMTNO = stm.STMTNO and ope.SECTNO = stm.SECTNO and ope.OPERATOR_ID = stm.TARGET_ID ORDER BY ope.OPERATOR_ID";
        
        //EXPLAIN表から Optimized Statement を取得
        String selectOptimizedStatement =
            "SELECT STATEMENT_TEXT FROM EXPLAIN_STATEMENT WHERE EXPLAIN_LEVEL='P'";
        
        String header = "Data from EXPLAIN tables";
        String optimizedStatement = "Optimized Statement:";
        String optimizedStatementSeparator = "------------------";
        String accessPlan = "Access Plan:";
        String accessPlanSeparator = "-----------";
        String meaningOfItems = "OPERATOR_ID,OPERATOR_TYPE,OBJECT_NAME,TOTAL_COST";
        
        // 実行計画を生成（EXPLAIN表に展開）
        ResultSet resultSet = null;
        Statement planStmt = null;
        try
        {
            planStmt = stmt.getConnection().createStatement();
            
            // EXPLAIN表に展開
            planStmt.execute(clearExplainTables);
            planStmt.execute(explainModeExplain);
            planStmt.execute(originalSql);
            planStmt.execute(explainModeNo);
            
            // EXPLAIN表からOptimized Statementを取得
            resultSet = planStmt.executeQuery(selectOptimizedStatement);
            
            if (resultSet != null)
            {
                execPlanText = new StringBuilder("");
                
                execPlanText.append(header);
                execPlanText.append(NEW_LINE);
                execPlanText.append(NEW_LINE);
                execPlanText.append(optimizedStatement);
                execPlanText.append(NEW_LINE);
                execPlanText.append(optimizedStatementSeparator);
                execPlanText.append(NEW_LINE);

                resultSet.next();
                
                execPlanText.append(resultSet.getString(1));
                execPlanText.append(NEW_LINE);
                execPlanText.append(NEW_LINE);
            }
             
            // EXPLAIN表からAccess Planを取得
            resultSet = planStmt.executeQuery(operatorType);

            if (resultSet != null)
            {
                // 検索された行数分ループ
                execPlanText.append(accessPlan);
                execPlanText.append(NEW_LINE);
                execPlanText.append(accessPlanSeparator);
                execPlanText.append(NEW_LINE);
                execPlanText.append(meaningOfItems);
                execPlanText.append(NEW_LINE);
                execPlanText.append(NEW_LINE);

                while (resultSet.next())
                {
                    // OPERATOR_TYPEを取得
                    // レコード取得処理を高速化するため、ResultSetへのアクセスは
                    // カラム名ではなくインデックスを用いて行う
                    StringBuilder planTableOutputBuilder = new StringBuilder();
                    planTableOutputBuilder.append(resultSet.getString(1));
                    planTableOutputBuilder.append(COMMA);
                    planTableOutputBuilder.append(resultSet.getString(2));
                    planTableOutputBuilder.append(COMMA);
                    planTableOutputBuilder.append(resultSet.getString(3));
                    planTableOutputBuilder.append(COMMA);
                    planTableOutputBuilder.append(resultSet.getString(4));
                    // 結合
                    execPlanText.append(planTableOutputBuilder.toString());
                    execPlanText.append(NEW_LINE);
                }
            }
        }
        catch (Exception ex)
        {
            SystemLogger.getInstance().warn(ex);
        }
        finally
        {
            // リソース解放
            SQLUtil.closeResultSet(resultSet);
            SQLUtil.closeStatement(planStmt);
        }

        return execPlanText.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractExecutePlanChecker<?> getExecutePlanChecker()
    {
        return new DB2ExecutePlanChecker();
    }
}
