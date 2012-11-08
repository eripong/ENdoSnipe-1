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
package jp.co.acroquest.endosnipe.data.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import jp.co.acroquest.endosnipe.common.util.SQLUtil;
import jp.co.acroquest.endosnipe.data.DBInitializer;
import jp.co.acroquest.endosnipe.data.db.ConnectionManager;

import org.apache.commons.dbcp.DelegatingPreparedStatement;

/**
 * Dao のための基底クラスです。<br />
 * 
 * @author y-komori
 */
public abstract class AbstractDao
{
    /**
     * 基底クラスオブジェクトを生成します。<br />
     */
    protected AbstractDao()
    {
        // Do nothing.
    }

    /**
     * コネクションを取得します。<br />
     *
     * データベースが存在しない場合は、作成します。<br />
     * 
     * @param database データベース名
     * @return {@link Connection} オブジェクト
     * @throws SQLException コネクションが取得できない場合
     */
    protected static Connection getConnection(final String database)
        throws SQLException
    {
        return getConnection(database, false);
    }

    /**
     * コネクションを取得します。<br />
     * 
     * @param databaseName データベース名
     * @param connectOnlyExists データベースが存在するときのみ接続する場合は <code>true</code> 、
     *                          存在しないときにデータベースを生成する場合は <code>false</code>
     * @return {@link Connection} オブジェクト
     * @throws SQLException コネクションが取得できない場合
     */
    protected static Connection getConnection(final String databaseName,
            final boolean connectOnlyExists)
        throws SQLException
    {
        return ConnectionManager.getInstance().getConnection(databaseName, connectOnlyExists);
    }

    /**
     * デリゲートされた {@link PreparedStatement} オブジェクトを取得します。<br />
     * 
     * commons-dbcp の {@link DelegatingPreparedStatement} 経由で blob を設定すると、
     * {@link AbstractMethodError} が発生するための処置。
     * 
     * @param pstmt {@link PreparedStatement}
     * @return {@link PreparedStatement} オブジェクト
     */
    protected static PreparedStatement getDelegatingStatement(final PreparedStatement pstmt)
    {
        if (pstmt instanceof DelegatingPreparedStatement)
        {
            return (PreparedStatement)((DelegatingPreparedStatement)pstmt).getDelegate();
        }
        else
        {
            return pstmt;
        }
    }

    /**
     * 指定されたテーブルのすべてのレコードを削除します。<br />
     *
     * @param database データベース名
     * @param table テーブル名
     * @throws SQLException SQL 実行時に例外が発生した場合
     */
    protected static void deleteAll(final String database, final String table)
        throws SQLException
    {
        Connection conn = null;
        try
        {
            conn = getConnection(database, true);
            deleteAll(conn, table);
        }
        finally
        {
            SQLUtil.closeConnection(conn);
        }
    }

    /**
     * 指定されたテーブルのすべてのレコードを削除します。<br />
     *
     * @param conn コネクション
     * @param table テーブル名
     * @throws SQLException SQL 実行時に例外が発生した場合
     */
    protected static void deleteAll(final Connection conn, final String table)
        throws SQLException
    {
        Statement stmt = null;
        try
        {
            stmt = conn.createStatement();
            stmt.execute("delete from " + table);
        }
        finally
        {
            SQLUtil.closeStatement(stmt);
        }
    }

    /**
     * 指定したテーブルを truncate します。
     *
     * @param database データベース名
     * @param tableName テーブル名
     * @throws SQLException SQL 実行時に例外が発生した場合
     */
    protected static void truncate(final String database, final String tableName)
        throws SQLException
    {
        Connection conn = null;
        Statement stmt = null;
        try
        {
            conn = getConnection(database);
            stmt = conn.createStatement();

            // データを削除する
            String sql = "truncate table " + tableName;
            stmt.execute(sql);
        }
        finally
        {
            SQLUtil.closeStatement(stmt);
            SQLUtil.closeConnection(conn);
        }
    }

    /**
     * 指定したテーブルの CHECK 属性を更新します。
     *
     * @param database データベース名
     * @param tableName テーブル名（インデックスも含めたテーブル名）
     * @param tableIndex テーブルインデックス
     * @param column 制約をつけるカラム名
     * @param year 年
     * @throws SQLException SQL 実行時に例外が発生した場合
     */
    protected static void alterCheckConstraint(final String database, final String tableName,
            final int tableIndex, final String column, final int year)
        throws SQLException
    {
        Connection conn = null;
        Statement stmt = null;

        String checkConstraintName = DBInitializer.createCheckConstraintName(tableName, column);

        try
        {
            conn = getConnection(database);
            stmt = conn.createStatement();

            // CHECK属性を削除する
            String sqlToDropCheck =
                    "ALTER TABLE " + tableName + " DROP CONSTRAINT " + checkConstraintName
                            + " RESTRICT";
            stmt.execute(sqlToDropCheck);

            // CHECK属性を更新する
            String checkConstraint =
                    DBInitializer.createCheckConstraintText(column, tableIndex, year);
            String sqlToCreateCheck =
                    String.format("ALTER TABLE %s ADD CONSTRAINT %s %s", tableName,
                                  checkConstraintName, checkConstraint);
            stmt.execute(sqlToCreateCheck);
        }
        finally
        {
            SQLUtil.closeStatement(stmt);
            SQLUtil.closeConnection(conn);
        }
    }

    /**
     * レコードの数を返します。<br />
     * 
     * @param database データベース名
     * @param table テーブル名
     * @param notNullKey NULL でないキー
     * @return レコードの数
     * @throws SQLException SQL 実行時に例外が発生した場合
     */
    protected static int count(final String database, final String table, final String notNullKey)
        throws SQLException
    {
        int count = 0;
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try
        {
            conn = getConnection(database, true);
            String sql = "select count(" + notNullKey + ") from " + table;
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            if (rs.next() == true)
            {
                count = rs.getInt(1);
            }
        }
        finally
        {
            SQLUtil.closeResultSet(rs);
            SQLUtil.closeStatement(stmt);
            SQLUtil.closeConnection(conn);
        }
        return count;
    }

    /**
     * 指定されたシーケンス名から値を生成します。<br />
     *
     * @param database データベース名
     * @param sequenceName シーケンス名
     * @return 値の生成に成功した場合は生成された値、失敗した場合は <code>-1</code>
     * @throws SQLException SQL 実行時に例外が発生した場合
     */
    protected static int createValueFromSequenceId(final String database, final String sequenceName)
        throws SQLException
    {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        int newMeasurementItemId = -1;
        try
        {
            conn = getConnection(database);
            stmt = conn.createStatement();
            String sql = ConnectionManager.getInstance().getSequenceSql(sequenceName);
            rs = stmt.executeQuery(sql);
            if (rs.next() == true)
            {
                newMeasurementItemId = rs.getInt(1);
            }
        }
        finally
        {
            SQLUtil.closeResultSet(rs);
            SQLUtil.closeStatement(stmt);
            SQLUtil.closeConnection(conn);
        }
        return newMeasurementItemId;
    }
}
