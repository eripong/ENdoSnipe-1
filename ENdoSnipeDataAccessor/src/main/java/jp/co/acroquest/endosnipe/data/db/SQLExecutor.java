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
package jp.co.acroquest.endosnipe.data.db;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * SQL 実行のためのユーティリティクラスです。<br />
 * 
 * @author y-komori
 */
public class SQLExecutor
{
    private SQLExecutor()
    {
    }

    /**
     * ストリームからSQLを読み込んで実行します。<br />
     * 
     * セミコロンで区切られた複数のSQLが記述されている場合、順番に実行します。<br />
     * 行中にある -- 以降はコメントとして読み飛ばします。
     * 
     * @param con データベースコネクション
     * @param stream SQL を読み込むための入力ストリーム
     * @return 総更新行数
     * @throws IOException 入出力エラーが発生した場合
     * @throws SQLException SQL実行に失敗した場合
     */
    public static int executeSQL(final Connection con, final InputStream stream)
        throws IOException,
            SQLException
    {
        return executeSQL(con, stream, null, null);
    }

    /**
     * ストリームからSQLを読み込んで実行します。<br />
     * 
     * セミコロンで区切られた複数のSQLが記述されている場合、順番に実行します。<br />
     * 行中にある -- 以降はコメントとして読み飛ばします。
     * 
     * @param con データベースコネクション
     * @param stream SQL を読み込むための入力ストリーム
     * @param replacer SQL を置換するための {@link SQLReplacer}
     * @return 総更新行数
     * @throws IOException 入出力エラーが発生した場合
     * @throws SQLException SQL実行に失敗した場合
     */
    public static int executeSQL(final Connection con, final InputStream stream,
            final SQLReplacer replacer)
        throws IOException,
            SQLException
    {
        return executeSQL(con, stream, null, replacer);
    }

    /**
     * エンコーディングを指定したストリームからSQLを読み込んで実行します。<br />
     * 
     * セミコロンで区切られた複数のSQLが記述されている場合、順番に実行します。<br />
     * 行中にある -- 以降はコメントとして読み飛ばします。
     * 
     * @param con  データベースコネクション
     * @param stream SQL を読み込むための入力ストリーム
     * @param encoding SQL のエンコーディング
     * @param replacer SQL を置換するための {@link SQLReplacer}
     * @return 総更新行数
     * @throws IOException 入出力エラーが発生した場合
     * @throws SQLException SQL実行に失敗した場合

     */
    public static int executeSQL(final Connection con, final InputStream stream,
            final String encoding, final SQLReplacer replacer)
        throws IOException,
            SQLException
    {
        if (con == null)
        {
            throw new IllegalArgumentException("connection can't be null");
        }
        if (stream == null)
        {
            throw new IllegalArgumentException("stream can't be null");
        }

        InputStreamReader isr;
        if (encoding != null)
        {
            isr = new InputStreamReader(stream, encoding);
        }
        else
        {
            isr = new InputStreamReader(stream);
        }
        int updateCount = 0;
        SQLReader reader = new SQLReader(isr);
        List<String> sqls = null;
        try
        {
            sqls = reader.readSql();
        }
        catch (IOException ex)
        {
            isr.close();
            throw ex;
        }

        for (String sql : sqls)
        {
            Statement stmt = null;
            try
            {
                if (replacer != null)
                {
                    sql = replacer.replace(sql);
                }
                stmt = con.createStatement();
                //System.out.println("SQL: " + sql);
                if (!stmt.execute(sql))
                {
                    updateCount += stmt.getUpdateCount();
                }
            }
            catch (SQLException ex)
            {
                if (stmt != null)
                {
                    stmt.close();
                }
                throw ex;
            }
            if (stmt != null)
            {
                stmt.close();
            }
        }
        return updateCount;
    }

    /**
     * SQLを実行します。
     *
     * @param con データベースコネクション
     * @param sql SQL文
     * @param replacer SQL を置換するための {@link SQLReplacer}
     * @return 総更新行数
     * @throws SQLException SQL実行に失敗した場合
     */
    public static int executeSQL(final Connection con, final String sql, final SQLReplacer replacer)
        throws SQLException
    {
        int updateCount = 0;
        String sqlConverted = sql;
        Statement stmt = null;
        try
        {
            if (replacer != null)
            {
                sqlConverted = replacer.replace(sql);
            }
            stmt = con.createStatement();
            if (!stmt.execute(sqlConverted))
            {
                updateCount = stmt.getUpdateCount();
            }
        }
        finally
        {
            if (stmt != null)
            {
                stmt.close();
            }
        }
        return updateCount;
    }
}
