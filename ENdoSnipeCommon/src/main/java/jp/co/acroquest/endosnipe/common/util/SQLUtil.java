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
package jp.co.acroquest.endosnipe.common.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * SQL に関するユーティリティクラスです。<br />
 * 
 * @author y-komori
 */
public class SQLUtil
{
    private SQLUtil()
    {

    }

    /**
     * {@link ResultSet} をクローズします。<br />
     * 引数が <code>null</code> の場合は何も行いません。
     * 
     * @param rs {@link ResultSet} オブジェクト
     */
    public static void closeResultSet(final ResultSet rs)
    {
        if (rs != null)
        {
            try
            {
                rs.close();
            }
            catch (SQLException ex)
            // CHECKSTYLE:OFF
            {
                // Do nothing.
            }
            // CHECKSTYLE:ON
        }
    }

    /**
     * {@link Statement} をクローズします。<br />
     * 引数が <code>null</code> の場合は何も行いません。
     * 
     * @param statement {@link Statement} オブジェクト
     */
    public static void closeStatement(final Statement statement)
    {
        if (statement != null)
        {
            try
            {
                statement.close();
            }
            catch (SQLException ex)
            // CHECKSTYLE:OFF
            {
                // Do nothing.
            }
            // CHECKSTYLE:ON
        }
    }

    /**
     * {@link Connection} をクローズします。<br />
     * 引数が <code>null</code> の場合は何も行いません。
     * 
     * @param connection {@link Connection} オブジェクト
     */
    public static void closeConnection(final Connection connection)
    {
        if (connection != null)
        {
            try
            {
                connection.close();
            }
            catch (SQLException ex)
            // CHECKSTYLE:OFF
            {
                // Do nothing.
            }
            // CHECKSTYLE:ON
        }
    }
}
