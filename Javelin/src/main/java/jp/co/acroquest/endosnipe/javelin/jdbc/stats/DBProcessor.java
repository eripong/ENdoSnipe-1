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

/**
 * DBごとに異なる処理を実施する。
 * 
 * @author eriguchi
 *
 */
public interface DBProcessor
{
    /**
     * 処理対象のDBへの接続かどうかを判定する。
     * 
     * @param jdbcUrl DB接続文字列。
     * @return 処理対象のDBへの接続かどうか。
     */
    boolean isTarget(String jdbcUrl);

    /**
     * PreparedStatementの実行計画を取得する。
     *
     * @param stmt ステートメント
     * @param originalSql SQL文
     * @param args TODO
     * @return 実行計画
     * 
     * @throws SQLException 実行計画取得時にエラーが発生した場合。
     */
    String getExecPlanPrepared(Statement stmt, String originalSql, List<?> args)
        throws SQLException;

    /**
     * Statementの実行計画を取得する。
     *
     * @param stmt ステートメント
     * @param originalSql SQL文
     * @param args TODO
     * @return 実行計画
     * 
     * @throws SQLException ResultSetクローズ時にエラーが発生したとき
     */
    String getOneExecPlan(Statement stmt, String originalSql, List<?> args)
        throws SQLException;

    /**
     * Statementの実行計画を取得する。
     *
     * @param stmt ステートメント
     * @param originalSqlElement SQL文
     * @param planStmt 実行計画取得用ステートメント
     * @return 実行計画
     * @throws SQLException ResultSetクローズ時にエラーが発生したとき
     */
    String execPlan(Statement stmt, String originalSqlElement, Statement planStmt)
        throws SQLException;

    /**
     * SQLトレース取得用SQLを発行する。
     * @param connection コネクション
     */
    void startSqlTrace(Connection connection);

    /**
     * Connection.prepareStatementメソッド呼び出し後に呼ばれるメソッド。
     *
     * @param sql PreparedStatement文字列
     * @param pstmt Connection.prepareStatement()の戻り値
     * 
     * @throws SQLException ResultSetクローズ時にエラーが発生したとき
     */
    void postPrepareStatement(String sql, PreparedStatement pstmt)
        throws SQLException;
    
    /**
     * DBごとの実行計画の調査するクラスのインスタンスを返します。<br>
     * @return 実行計画調査クラスのインスタンス
     */
    AbstractExecutePlanChecker<?> getExecutePlanChecker();
    
    /**
     * SQLでFull Scanを行っているかどうかを調査し、<br>
     * 行っているテーブル名のセットを作成して返す。
     * @param executePlan 実行計画の文字列
     * @return Full Scanを行っているテーブル名のセット
     */
    Set<String> checkFullScan(String executePlan);

    /**
     * 実行計画取得時にロックする必要があるかどうか。
     * 
     * @return 実行計画取得時にロックする必要があるかどうか
     */
    boolean needsLock();
}
