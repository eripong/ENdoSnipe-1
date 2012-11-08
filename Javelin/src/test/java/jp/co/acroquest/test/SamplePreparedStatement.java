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
package jp.co.acroquest.test;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLWarning;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;

/**
 * テスト用PreparedStatement
 * 
 * @author k-ishida
 * 
 */
public class SamplePreparedStatement implements PreparedStatement
{
    /**
     * (non-Javadoc)
     * 
     * @see java.sql.PreparedStatement#addBatch()
     */
    public void addBatch()
    {
        // Do Nothing.
    }

    /**
     * (non-Javadoc)
     * 
     * @see java.sql.PreparedStatement#clearParameters()
     */
    public void clearParameters()
    {
        // Do Nothing.
    }

    /**
     * (non-Javadoc)
     * 
     * @see java.sql.PreparedStatement#execute()
     * @return false
     */
    public boolean execute()
    {
        // Do Nothing.
        return false;
    }

    /**
     * (non-Javadoc)
     * 
     * @see java.sql.PreparedStatement#executeQuery()
     * @return null
     */
    public ResultSet executeQuery()
    {
        // Do Nothing.
        return null;
    }

    /**
     * (non-Javadoc)
     * 
     * @see java.sql.PreparedStatement#executeUpdate()
     * @return 0
     */
    public int executeUpdate()
    {
        // Do Nothing.
        return 0;
    }

    /**
     * (non-Javadoc)
     * 
     * @see java.sql.PreparedStatement#getMetaData()
     * @return null
     */
    public ResultSetMetaData getMetaData()
    {
        // Do Nothing.
        return null;
    }

    /**
     * (non-Javadoc)
     * 
     * @see java.sql.PreparedStatement#getParameterMetaData()
     * @return null
     */
    public ParameterMetaData getParameterMetaData()
    {
        // Do Nothing.
        return null;
    }

    /**
     * (non-Javadoc)
     * 
     * @see java.sql.PreparedStatement#setArray(int, java.sql.Array)
     * @param arg0
     *            利用しない
     * @param arg1
     *            利用しない
     */
    public void setArray(final int arg0, final Array arg1)
    {
        // Do Nothing.
    }

    /**
     * (non-Javadoc)
     * 
     * @see java.sql.PreparedStatement#setAsciiStream(int, java.io.InputStream,
     *      int)
     * @param arg0
     *            利用しない
     * @param arg1
     *            利用しない
     * @param arg2
     *            利用しない
     */
    public void setAsciiStream(final int arg0, final InputStream arg1, final int arg2)
    {
        // Do Nothing.
    }

    /**
     * (non-Javadoc)
     * 
     * @see java.sql.PreparedStatement#setBigDecimal(int, java.math.BigDecimal)
     * @param arg0
     *            利用しない
     * @param arg1
     *            利用しない
     */
    public void setBigDecimal(final int arg0, final BigDecimal arg1)
    {
        // Do Nothing.
    }

    /**
     * (non-Javadoc)
     * 
     * @see java.sql.PreparedStatement#setBinaryStream(int, java.io.InputStream,
     *      int)
     * @param arg0
     *            利用しない
     * @param arg1
     *            利用しない
     * @param arg2
     *            利用しない
     */
    public void setBinaryStream(final int arg0, final InputStream arg1, final int arg2)
    {
        // Do Nothing.
    }

    /**
     * (non-Javadoc)
     * 
     * @see java.sql.PreparedStatement#setBlob(int, java.sql.Blob)
     * @param arg0
     *            利用しない
     * @param arg1
     *            利用しない
     */
    public void setBlob(final int arg0, final Blob arg1)
    {
        // Do Nothing.
    }

    /**
     * (non-Javadoc)
     * 
     * @see java.sql.PreparedStatement#setBoolean(int, boolean)
     * @param arg0
     *            利用しない
     * @param arg1
     *            利用しない
     */
    public void setBoolean(final int arg0, final boolean arg1)
    {
        // Do Nothing.
    }

    /**
     * (non-Javadoc)
     * 
     * @see java.sql.PreparedStatement#setByte(int, byte)
     * @param arg0
     *            利用しない
     * @param arg1
     *            利用しない
     */
    public void setByte(final int arg0, final byte arg1)
    {
        // Do Nothing.
    }

    /**
     * (non-Javadoc)
     * 
     * @see java.sql.PreparedStatement#setBytes(int, byte[])
     * @param arg0
     *            利用しない
     * @param arg1
     *            利用しない
     */
    public void setBytes(final int arg0, final byte[] arg1)
    {
        // Do Nothing.

    }

    /**
     * (non-Javadoc)
     * 
     * @see java.sql.PreparedStatement#setCharacterStream(int, java.io.Reader,
     *      int)
     * @param arg0
     *            利用しない
     * @param arg1
     *            利用しない
     * @param arg2
     *            利用しない
     */
    public void setCharacterStream(final int arg0, final Reader arg1, final int arg2)
    {
        // Do Nothing.
    }

    /**
     * (non-Javadoc)
     * 
     * @see java.sql.PreparedStatement#setClob(int, java.sql.Clob)
     * @param arg0
     *            利用しない
     * @param arg1
     *            利用しない
     */
    public void setClob(final int arg0, final Clob arg1)
    {
        // Do Nothing.
    }

    /**
     * (non-Javadoc)
     * 
     * @see java.sql.PreparedStatement#setDate(int, java.sql.Date,
     *      java.util.Calendar)
     * @param arg0
     *            利用しない
     * @param arg1
     *            利用しない
     * @param arg2
     *            利用しない
     */
    public void setDate(final int arg0, final Date arg1, final Calendar arg2)
    {
        // Do Nothing.
    }

    /**
     * (non-Javadoc)
     * 
     * @see java.sql.PreparedStatement#setDate(int, java.sql.Date)
     * @param arg0
     *            利用しない
     * @param arg1
     *            利用しない
     */
    public void setDate(final int arg0, final Date arg1)
    {
        // Do Nothing.
    }

    /**
     * (non-Javadoc)
     * 
     * @see java.sql.PreparedStatement#setDouble(int, double)
     * @param arg0
     *            利用しない
     * @param arg1
     *            利用しない
     */
    public void setDouble(final int arg0, final double arg1)
    {
        // Do Nothing.
    }

    /**
     * (non-Javadoc)
     * 
     * @see java.sql.PreparedStatement#setFloat(int, float)
     * @param arg0
     *            利用しない
     * @param arg1
     *            利用しない
     */
    public void setFloat(final int arg0, final float arg1)
    {
        // Do Nothing.
    }

    /**
     * (non-Javadoc)
     * 
     * @see java.sql.PreparedStatement#setInt(int, int)
     * @param arg0
     *            利用しない
     * @param arg1
     *            利用しない
     */
    public void setInt(final int arg0, final int arg1)
    {
        // Do Nothing.

    }

    /**
     * (non-Javadoc)
     * 
     * @see java.sql.PreparedStatement#setLong(int, long)
     * @param arg0
     *            利用しない
     * @param arg1
     *            利用しない
     */
    public void setLong(final int arg0, final long arg1)
    {
        // Do Nothing.

    }

    /**
     * (non-Javadoc)
     * 
     * @see java.sql.PreparedStatement#setNull(int, int, java.lang.String)
     * @param arg0
     *            利用しない
     * @param arg1
     *            利用しない
     * @param arg2
     *            利用しない
     */
    public void setNull(final int arg0, final int arg1, final String arg2)
    {
        // Do Nothing.

    }

    /**
     * (non-Javadoc)
     * 
     * @see java.sql.PreparedStatement#setNull(int, int)
     * @param arg0
     *            利用しない
     * @param arg1
     *            利用しない
     */
    public void setNull(final int arg0, final int arg1)
    {
        // Do Nothing.

    }

    /**
     * (non-Javadoc)
     * 
     * @see java.sql.PreparedStatement#setObject(int, java.lang.Object, int,
     *      int)
     * @param arg0
     *            利用しない
     * @param arg1
     *            利用しない
     * @param arg2
     *            利用しない
     * @param arg3
     *            利用しない
     */
    public void setObject(final int arg0, final Object arg1, final int arg2, final int arg3)
    {
        // Do Nothing.

    }

    /**
     * (non-Javadoc)
     * 
     * @see java.sql.PreparedStatement#setObject(int, java.lang.Object, int)
     * @param arg0
     *            利用しない
     * @param arg1
     *            利用しない
     * @param arg2
     *            利用しない
     */
    public void setObject(final int arg0, final Object arg1, final int arg2)
    {
        // Do Nothing.

    }

    /**
     * (non-Javadoc)
     * 
     * @see java.sql.PreparedStatement#setObject(int, java.lang.Object)
     * @param arg0
     *            利用しない
     * @param arg1
     *            利用しない
     */
    public void setObject(final int arg0, final Object arg1)
    {
        // Do Nothing.

    }

    /**
     * (non-Javadoc)
     * 
     * @see java.sql.PreparedStatement#setRef(int, java.sql.Ref)
     * @param arg0
     *            利用しない
     * @param arg1
     *            利用しない
     */
    public void setRef(final int arg0, final Ref arg1)
    {
        // Do Nothing.

    }

    /**
     * (non-Javadoc)
     * 
     * @see java.sql.PreparedStatement#setShort(int, short)
     * @param arg0
     *            利用しない
     * @param arg1
     *            利用しない
     */
    public void setShort(final int arg0, final short arg1)
    {
        // Do Nothing.

    }

    /**
     * (non-Javadoc)
     * 
     * @see java.sql.PreparedStatement#setString(int, java.lang.String)
     * @param arg0
     *            利用しない
     * @param arg1
     *            利用しない
     */
    public void setString(final int arg0, final String arg1)
    {
        // Do Nothing.

    }

    /**
     * (non-Javadoc)
     * 
     * @see java.sql.PreparedStatement#setTime(int, java.sql.Time,
     *      java.util.Calendar)
     * @param arg0
     *            利用しない
     * @param arg1
     *            利用しない
     * @param arg2
     *            利用しない
     */
    public void setTime(final int arg0, final Time arg1, final Calendar arg2)
    {
        // Do Nothing.

    }

    /**
     * (non-Javadoc)
     * 
     * @see java.sql.PreparedStatement#setTime(int, java.sql.Time)
     * @param arg0
     *            利用しない
     * @param arg1
     *            利用しない
     */
    public void setTime(final int arg0, final Time arg1)
    {
        // Do Nothing.

    }

    /**
     * (non-Javadoc)
     * 
     * @see java.sql.PreparedStatement#setTimestamp(int, java.sql.Timestamp,
     *      java.util.Calendar)
     * @param arg0
     *            利用しない
     * @param arg1
     *            利用しない
     * @param arg2
     *            利用しない
     */
    public void setTimestamp(final int arg0, final Timestamp arg1, final Calendar arg2)
    {
        // Do Nothing.

    }

    /**
     * (non-Javadoc)
     * 
     * @see java.sql.PreparedStatement#setTimestamp(int, java.sql.Timestamp)
     * @param arg0
     *            利用しない
     * @param arg1
     *            利用しない
     */
    public void setTimestamp(final int arg0, final Timestamp arg1)
    {
        // Do Nothing.

    }

    /**
     * (non-Javadoc)
     * 
     * @see java.sql.PreparedStatement#setUnicodeStream(int,
     *      java.io.InputStream, int)
     * @param arg0
     *            利用しない
     * @param arg1
     *            利用しない
     * @param arg2
     *            利用しない
     */
    @SuppressWarnings("deprecation")
    public void setUnicodeStream(final int arg0, final InputStream arg1, final int arg2)
    {
        // Do Nothing.
    }

    /**
     * (non-Javadoc)
     * 
     * @see java.sql.PreparedStatement#setURL(int, java.net.URL)
     * @param arg0
     *            利用しない
     * @param arg1
     *            利用しない
     */
    public void setURL(final int arg0, final URL arg1)
    {
        // Do Nothing.

    }

    /**
     * (non-Javadoc)
     * 
     * @see java.sql.Statement#addBatch(java.lang.String)
     * @param arg0
     *            利用しない
     */
    public void addBatch(final String arg0)
    {
        // Do Nothing.

    }

    /**
     * (non-Javadoc)
     * 
     * @see java.sql.Statement#cancel()
     */
    public void cancel()
    {
        // Do Nothing.

    }

    /**
     * (non-Javadoc)
     * 
     * @see java.sql.Statement#clearBatch()
     */
    public void clearBatch()
    {
        // Do Nothing.
    }

    /**
     * (non-Javadoc)
     * 
     * @see java.sql.Statement#clearWarnings()
     */
    public void clearWarnings()
    {
        // Do Nothing.
    }

    /**
     * (non-Javadoc)
     * 
     * @see java.sql.Statement#close()
     */
    public void close()
    {
        // Do Nothing.
    }

    /**
     * (non-Javadoc)
     * 
     * @see java.sql.Statement#execute(java.lang.String, int)
     * @param arg0
     *            利用しない
     * @param arg1
     *            利用しない
     * @return false
     */
    public boolean execute(final String arg0, final int arg1)
    {
        // Do Nothing.
        return false;
    }

    /**
     * (non-Javadoc)
     * 
     * @see java.sql.Statement#execute(java.lang.String, int[])
     * @param arg0
     *            利用しない
     * @param arg1
     *            利用しない
     * @return false
     */
    public boolean execute(final String arg0, final int[] arg1)
    {
        // Do Nothing.
        return false;
    }

    /**
     * (non-Javadoc)
     * 
     * @see java.sql.Statement#execute(java.lang.String, java.lang.String[])
     * @param arg0
     *            利用しない
     * @param arg1
     *            利用しない
     * @return false
     */
    public boolean execute(final String arg0, final String[] arg1)
    {
        // Do Nothing.
        return false;
    }

    /**
     * (non-Javadoc)
     * 
     * @see java.sql.Statement#execute(java.lang.String)
     * @param arg0
     *            利用しない
     * @return false
     */
    public boolean execute(final String arg0)
    {
        // Do Nothing.
        return false;
    }

    /**
     * (non-Javadoc)
     * 
     * @see java.sql.Statement#executeBatch()
     * @return 0
     */
    public int[] executeBatch()
    {
        // Do Nothing.
        return null;
    }

    /**
     * (non-Javadoc)
     * 
     * @see java.sql.Statement#executeQuery(java.lang.String)
     * @param arg0
     *            利用しない
     * @return null
     */
    public ResultSet executeQuery(final String arg0)
    {
        // Do Nothing.
        return null;
    }

    /**
     * (non-Javadoc)
     * 
     * @see java.sql.Statement#executeUpdate(java.lang.String, int)
     * @param arg0
     *            利用しない
     * @param arg1
     *            利用しない
     * @return 0
     */
    public int executeUpdate(final String arg0, final int arg1)
    {
        // Do Nothing.
        return 0;
    }

    /**
     * (non-Javadoc)
     * 
     * @see java.sql.Statement#executeUpdate(java.lang.String, int[])
     * @param arg0
     *            利用しない
     * @param arg1
     *            利用しない
     * @return 0
     */
    public int executeUpdate(final String arg0, final int[] arg1)
    {
        // Do Nothing.
        return 0;
    }

    /**
     * (non-Javadoc)
     * 
     * @see java.sql.Statement#executeUpdate(java.lang.String,
     *      java.lang.String[])
     * @param arg0
     *            利用しない
     * @param arg1
     *            利用しない
     * @return 0
     */
    public int executeUpdate(final String arg0, final String[] arg1)
    {
        // Do Nothing.
        return 0;
    }

    /**
     * (non-Javadoc)
     * 
     * @see java.sql.Statement#executeUpdate(java.lang.String)
     * @param arg0
     *            利用しない
     * @return 0
     */
    public int executeUpdate(final String arg0)
    {
        // Do Nothing.
        return 0;
    }

    /**
     * (non-Javadoc)
     * 
     * @see java.sql.Statement#getConnection()
     * @return null
     */
    public Connection getConnection()
    {
        // Do Nothing.
        return null;
    }

    /**
     * (non-Javadoc)
     * 
     * @see java.sql.Statement#getFetchDirection()
     * @return 0
     */
    public int getFetchDirection()
    {
        // Do Nothing.
        return 0;
    }

    /**
     * (non-Javadoc)
     * 
     * @see java.sql.Statement#getFetchSize()
     * @return 0
     */
    public int getFetchSize()
    {
        // Do Nothing.
        return 0;
    }

    /**
     * (non-Javadoc)
     * 
     * @see java.sql.Statement#getGeneratedKeys()
     * @return null
     */
    public ResultSet getGeneratedKeys()
    {
        // Do Nothing.
        return null;
    }

    /**
     * (non-Javadoc)
     * 
     * @see java.sql.Statement#getMaxFieldSize()
     * @return 0
     */
    public int getMaxFieldSize()
    {
        // Do Nothing.
        return 0;
    }

    /**
     * (non-Javadoc)
     * 
     * @see java.sql.Statement#getMaxRows()
     * @return 0
     */
    public int getMaxRows()
    {
        // Do Nothing.
        return 0;
    }

    /**
     * (non-Javadoc)
     * 
     * @see java.sql.Statement#getMoreResults()
     * @return false
     */
    public boolean getMoreResults()
    {
        // Do Nothing.
        return false;
    }

    /**
     * (non-Javadoc)
     * 
     * @see java.sql.Statement#getMoreResults(int)
     * @param arg0
     *            利用しない
     * @return false
     */
    public boolean getMoreResults(final int arg0)
    {
        // Do Nothing.
        return false;
    }

    /**
     * (non-Javadoc)
     * 
     * @see java.sql.Statement#getQueryTimeout()
     * @return 0
     */
    public int getQueryTimeout()
    {
        // Do Nothing.
        return 0;
    }

    /**
     * (non-Javadoc)
     * 
     * @see java.sql.Statement#getResultSet()
     * @return null
     */
    public ResultSet getResultSet()
    {
        // Do Nothing.
        return null;
    }

    /**
     * (non-Javadoc)
     * 
     * @see java.sql.Statement#getResultSetConcurrency()
     * @return 0
     */
    public int getResultSetConcurrency()
    {
        // Do Nothing.
        return 0;
    }

    /**
     * (non-Javadoc)
     * 
     * @see java.sql.Statement#getResultSetHoldability()
     * @return 0
     */
    public int getResultSetHoldability()
    {
        // Do Nothing.
        return 0;
    }

    /**
     * (non-Javadoc)
     * 
     * @see java.sql.Statement#getResultSetType()
     * @return 0
     */
    public int getResultSetType()
    {
        // Do Nothing.
        return 0;
    }

    /**
     * (non-Javadoc)
     * 
     * @see java.sql.Statement#getUpdateCount()
     * @return 0
     */
    public int getUpdateCount()
    {
        // Do Nothing.
        return 0;
    }

    /**
     * (non-Javadoc)
     * 
     * @see java.sql.Statement#getWarnings()
     * @return null
     */
    public SQLWarning getWarnings()
    {
        // Do Nothing.
        return null;
    }

    /**
     * (non-Javadoc)
     * 
     * @see java.sql.Statement#setCursorName(java.lang.String)
     * @param arg0
     *            利用しない
     */
    public void setCursorName(final String arg0)
    {
        // Do Nothing.
    }

    /**
     * (non-Javadoc)
     * 
     * @see java.sql.Statement#setEscapeProcessing(boolean)
     * @param arg0
     *            利用しない
     */
    public void setEscapeProcessing(final boolean arg0)
    {
        // Do Nothing.
    }

    /**
     * (non-Javadoc)
     * 
     * @see java.sql.Statement#setFetchDirection(int)
     * @param arg0
     *            利用しない
     */
    public void setFetchDirection(final int arg0)
    {
        // Do Nothing.
    }

    /**
     * (non-Javadoc)
     * 
     * @see java.sql.Statement#setFetchSize(int)
     * @param arg0
     *            利用しない
     */
    public void setFetchSize(final int arg0)
    {
        // Do Nothing.
    }

    /**
     * (non-Javadoc)
     * 
     * @see java.sql.Statement#setMaxFieldSize(int)
     * @param arg0
     *            利用しない
     */
    public void setMaxFieldSize(final int arg0)
    {
        // Do Nothing.
    }

    /**
     * (non-Javadoc)
     * 
     * @see java.sql.Statement#setMaxRows(int)
     * @param arg0
     *            利用しない
     */
    public void setMaxRows(final int arg0)
    {
        // Do Nothing.
    }

    /**
     * (non-Javadoc)
     * 
     * @see java.sql.Statement#setQueryTimeout(int)
     * @param arg0
     *            利用しない
     */
    public void setQueryTimeout(final int arg0)
    {
        // Do Nothing.
    }
}
