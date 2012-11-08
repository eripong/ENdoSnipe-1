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

import java.sql.SQLException;

import javax.sql.DataSource;

import jp.co.acroquest.endosnipe.common.logger.ENdoSnipeLogger;
import jp.co.acroquest.endosnipe.data.ENdoSnipeDataAccessorPluginProvider;
import jp.co.acroquest.endosnipe.data.LogMessageCodes;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.ObjectPool;

/**
 * H2用のデータソースを作成するクラスです。<br />
 * 
 * @author fujii
 *
 */
public class H2DataSourceCreator extends AbstractDataSourceCreator implements LogMessageCodes
{
    private static final ENdoSnipeLogger LOGGER =
            ENdoSnipeLogger.getLogger(H2DataSourceCreator.class,
                                      ENdoSnipeDataAccessorPluginProvider.INSTANCE);

    private static final String USER_NAME = "sa";

    private static final String PASSWORD = "";

    /**
     * {@inheritDoc}
     */
    public DataSource createPoolingDataSource(final String dbname, final boolean connectOnlyExists)
        throws SQLException
    {
        try
        {
            Class.forName("org.h2.Driver");
            String uri = createDatabaseURI(dbname, connectOnlyExists);
            ConnectionFactory connectionFactory =
                    new DriverManagerConnectionFactory(uri, USER_NAME, PASSWORD);

            // データベース名に対応した StackObjectPool を取得する。
            // もし存在しなければ、生成する。
            ConnectionManager manager = ConnectionManager.getInstance();
            ObjectPool connectionPool = manager.getConnectionPool(uri);
            if (connectionPool == null)
            {
                connectionPool = manager.createNewConnectionPool(uri);
            }

            new PoolableConnectionFactory(connectionFactory, connectionPool, null, null, false,
                                          true);
            return new PoolingDataSource(connectionPool);
        }
        catch (Exception ex)
        {
            LOGGER.log(EXCEPTION_OCCURED, ex, ex.getMessage());
            throw new SQLException(ex.getMessage());
        }
    }

    /**
     * データベース接続用 URI を生成します。<br />
     *
     * @param dbname データベース名
     * @param connectOnlyExists データベースが存在するときのみ接続する場合は <code>true</code> 、
     *                          存在しないときにデータベースを生成する場合は <code>false</code>
     * @return URL
     */
    protected String createDatabaseURI(final String dbname, final boolean connectOnlyExists)
    {
        //return String.format("jdbc:h2:tcp://%s:%d/%s/%<s", hostName_, port_, dbname);
        String uri = String.format("jdbc:h2:%s/%s/%s", this.baseDir_, dbname, dbname);
        if (connectOnlyExists)
        {
            uri = uri + ";IFEXISTS=TRUE";
        }
        return uri;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isTarget()
    {
        return DBManager.isDefaultDb();
    }

    /**
     * {@inheritDoc}
     */
    public String getSequenceSql(String sequenceName)
    {
        return "call next value for " + sequenceName;
    }

    /**
     * {@inheritDoc}
     */
    public boolean existsDatabase(String dbName)
    {
        // 実装なし
        return true;
    }
}
