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

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Savepoint;
import java.sql.Statement;
import java.util.Map;

import jp.co.acroquest.endosnipe.common.logger.ENdoSnipeLogger;
import jp.co.acroquest.endosnipe.common.util.AssertionUtil;
import jp.co.acroquest.endosnipe.data.ENdoSnipeDataAccessorPluginProvider;
import jp.co.acroquest.endosnipe.data.LogMessageCodes;

/**
 * {@link Connection} をラップするクラスです。<br />
 * 
 * @author y-komori
 */
public class ConnectionWrapper implements Connection, LogMessageCodes
{
    private static final ENdoSnipeLogger LOGGER =
            ENdoSnipeLogger.getLogger(ConnectionWrapper.class,
                                      ENdoSnipeDataAccessorPluginProvider.INSTANCE);

    private final Connection wrappedConnection_;

    private final String dbName_;

    /**
     * {@link ConnectionWrapper} を構築します。<br />
     * 
     * @param wrappedConnection ラップ対象コネクション
     * @param dbName データーベース名
     */
    public ConnectionWrapper(final Connection wrappedConnection, final String dbName)
    {
        AssertionUtil.assertNotNull("wrappedConnection", wrappedConnection);
        AssertionUtil.assertNotNull("dbName", dbName);
        wrappedConnection_ = wrappedConnection;
        dbName_ = dbName;
    }

    /**
     * {@inheritDoc}
     */
    public void clearWarnings()
        throws SQLException
    {
        wrappedConnection_.clearWarnings();
    }

    /**
     * {@inheritDoc}
     */
    public void close()
        throws SQLException
    {
        wrappedConnection_.close();
        LOGGER.log(DB_DICONNECTED, dbName_);
    }

    /**
     * {@inheritDoc}
     */
    public void commit()
        throws SQLException
    {
        wrappedConnection_.commit();
    }

    /**
     * {@inheritDoc}
     */
    public Statement createStatement()
        throws SQLException
    {
        return wrappedConnection_.createStatement();
    }

    /**
     * {@inheritDoc}
     */
    public Statement createStatement(final int resultSetType, final int resultSetConcurrency)
        throws SQLException
    {
        return wrappedConnection_.createStatement(resultSetType, resultSetConcurrency);
    }

    /**
     * {@inheritDoc}
     */
    public Statement createStatement(final int resultSetType, final int resultSetConcurrency,
            final int resultSetHoldability)
        throws SQLException
    {
        return wrappedConnection_.createStatement(resultSetType, resultSetConcurrency,
                                                  resultSetHoldability);
    }

    /**
     * {@inheritDoc}
     */
    public boolean getAutoCommit()
        throws SQLException
    {
        return wrappedConnection_.getAutoCommit();
    }

    /**
     * {@inheritDoc}
     */
    public String getCatalog()
        throws SQLException
    {
        return wrappedConnection_.getCatalog();
    }

    /**
     * {@inheritDoc}
     */
    public int getHoldability()
        throws SQLException
    {
        return wrappedConnection_.getHoldability();
    }

    /**
     * {@inheritDoc}
     */
    public DatabaseMetaData getMetaData()
        throws SQLException
    {
        return wrappedConnection_.getMetaData();
    }

    /**
     * {@inheritDoc}
     */
    public int getTransactionIsolation()
        throws SQLException
    {
        return wrappedConnection_.getTransactionIsolation();
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, Class<?>> getTypeMap()
        throws SQLException
    {
        return wrappedConnection_.getTypeMap();
    }

    /**
     * {@inheritDoc}
     */
    public SQLWarning getWarnings()
        throws SQLException
    {
        return wrappedConnection_.getWarnings();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isClosed()
        throws SQLException
    {
        return wrappedConnection_.isClosed();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isReadOnly()
        throws SQLException
    {
        return wrappedConnection_.isReadOnly();
    }

    /**
     * {@inheritDoc}
     */
    public String nativeSQL(final String sql)
        throws SQLException
    {
        return wrappedConnection_.nativeSQL(sql);
    }

    /**
     * {@inheritDoc}
     */
    public CallableStatement prepareCall(final String sql)
        throws SQLException
    {
        return wrappedConnection_.prepareCall(sql);
    }

    /**
     * {@inheritDoc}
     */
    public CallableStatement prepareCall(final String sql, final int resultSetType,
            final int resultSetConcurrency)
        throws SQLException
    {
        return wrappedConnection_.prepareCall(sql, resultSetType, resultSetConcurrency);
    }

    /**
     * {@inheritDoc}
     */
    public CallableStatement prepareCall(final String sql, final int resultSetType,
            final int resultSetConcurrency, final int resultSetHoldability)
        throws SQLException
    {
        return wrappedConnection_.prepareCall(sql, resultSetType, resultSetConcurrency,
                                              resultSetHoldability);
    }

    /**
     * {@inheritDoc}
     */
    public PreparedStatement prepareStatement(final String sql)
        throws SQLException
    {
        return wrappedConnection_.prepareStatement(sql);
    }

    /**
     * {@inheritDoc}
     */
    public PreparedStatement prepareStatement(final String sql, final int autoGeneratedKeys)
        throws SQLException
    {
        return wrappedConnection_.prepareStatement(sql, autoGeneratedKeys);
    }

    /**
     * {@inheritDoc}
     */
    public PreparedStatement prepareStatement(final String sql, final int[] columnIndexes)
        throws SQLException
    {
        return wrappedConnection_.prepareStatement(sql, columnIndexes);
    }

    /**
     * {@inheritDoc}
     */
    public PreparedStatement prepareStatement(final String sql, final String[] columnNames)
        throws SQLException
    {
        return wrappedConnection_.prepareStatement(sql, columnNames);
    }

    /**
     * {@inheritDoc}
     */
    public PreparedStatement prepareStatement(final String sql, final int resultSetType,
            final int resultSetConcurrency)
        throws SQLException
    {
        return wrappedConnection_.prepareStatement(sql, resultSetType, resultSetConcurrency);
    }

    /**
     * {@inheritDoc}
     */
    public PreparedStatement prepareStatement(final String sql, final int resultSetType,
            final int resultSetConcurrency, final int resultSetHoldability)
        throws SQLException
    {
        return wrappedConnection_.prepareStatement(sql, resultSetType, resultSetConcurrency,
                                                   resultSetHoldability);
    }

    /**
     * {@inheritDoc}
     */
    public void releaseSavepoint(final Savepoint savepoint)
        throws SQLException
    {
        wrappedConnection_.releaseSavepoint(savepoint);
    }

    /**
     * {@inheritDoc}
     */
    public void rollback()
        throws SQLException
    {
        wrappedConnection_.rollback();
    }

    /**
     * {@inheritDoc}
     */
    public void rollback(final Savepoint savepoint)
        throws SQLException
    {
        wrappedConnection_.rollback(savepoint);
    }

    /**
     * {@inheritDoc}
     */
    public void setAutoCommit(final boolean autoCommit)
        throws SQLException
    {
        wrappedConnection_.setAutoCommit(autoCommit);
    }

    /**
     * {@inheritDoc}
     */
    public void setCatalog(final String catalog)
        throws SQLException
    {
        wrappedConnection_.setCatalog(catalog);
    }

    /**
     * {@inheritDoc}
     */
    public void setHoldability(final int holdability)
        throws SQLException
    {
        wrappedConnection_.setHoldability(holdability);
    }

    /**
     * {@inheritDoc}
     */
    public void setReadOnly(final boolean readOnly)
        throws SQLException
    {
        wrappedConnection_.setReadOnly(readOnly);
    }

    /**
     * {@inheritDoc}
     */
    public Savepoint setSavepoint()
        throws SQLException
    {
        return wrappedConnection_.setSavepoint();
    }

    /**
     * {@inheritDoc}
     */
    public Savepoint setSavepoint(final String name)
        throws SQLException
    {
        return wrappedConnection_.setSavepoint(name);
    }

    /**
     * {@inheritDoc}
     */
    public void setTransactionIsolation(final int level)
        throws SQLException
    {
        wrappedConnection_.setTransactionIsolation(level);
    }

    /**
     * {@inheritDoc}
     */
    public void setTypeMap(final Map<String, Class<?>> map)
        throws SQLException
    {
        wrappedConnection_.setTypeMap(map);
    }
}
