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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.sql.DataSource;

import jp.co.acroquest.endosnipe.common.logger.ENdoSnipeLogger;
import jp.co.acroquest.endosnipe.common.util.SQLUtil;
import jp.co.acroquest.endosnipe.data.DBInitializer;
import jp.co.acroquest.endosnipe.data.ENdoSnipeDataAccessorPluginProvider;
import jp.co.acroquest.endosnipe.data.LogMessageCodes;

import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.StackObjectPool;
import org.seasar.framework.util.StringUtil;

/**
 * データベースコネクションを管理するためのクラスです。<br />
 * 
 * @author y-komori
 */
public class ConnectionManager implements LogMessageCodes
{
    /** ロガー */
    private static final ENdoSnipeLogger   LOGGER =
                                                      ENdoSnipeLogger.getLogger(
                                                                                ConnectionManager.class,
                                                                                ENdoSnipeDataAccessorPluginProvider.INSTANCE);

    /** ConnectionManagerインスタンス保持用変数 */
    private static ConnectionManager       instance__;

    /** 管理下に入っているDataSourceのリスト */
    private final List<DataSourceEntry>    dataSourceList_;

    /** データベース名をキーにしたコネクションプールオブジェクトのマップ */
    private final Map<String, ObjectPool>  connectionPoolMap_;

    /** 初期化済みのデータソース */
    private final Set<String>  initializedDatabaseSet_;
    
    /** データソースのリスト */
    private static List<DataSourceCreator> dataSouceCreatorList__;

    static
    {
        dataSouceCreatorList__ = new ArrayList<DataSourceCreator>();
        dataSouceCreatorList__.add(new H2DataSourceCreator());
        dataSouceCreatorList__.add(new PostgresDataSourceCreator());
    }

    /**
     * インスタンス化を防止するためのコンストラクタ
     */
    private ConnectionManager()
    {
        this.dataSourceList_ = new ArrayList<DataSourceEntry>();
        this.connectionPoolMap_ = new ConcurrentHashMap<String, ObjectPool>();
        this.initializedDatabaseSet_ = new CopyOnWriteArraySet<String>();
    }

    /**
     * {@link ConnectionManager} のインスタンスを返します。<br />
     * 
     * @return インスタンス
     */
    public static ConnectionManager getInstance()
    {
        if (instance__ == null)
        {
            instance__ = new ConnectionManager();
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run()
                {
                    instance__.closeAll();
                }
            });
        }
        return instance__;
    }

    /**
     * データベースコネクションを取得します。<br />
     * 
     * @param dbname データベース名
     * @param connectOnlyExists データベースが存在するときのみ接続する場合は <code>true</code> 、
     *                          存在しないときにデータベースを生成する場合は <code>false</code>
     * @return {@link Connection} オブジェクト
     * @throws SQLException コネクションが取得できなかった場合
     */
    public synchronized Connection getConnection(final String dbname,
            final boolean connectOnlyExists)
        throws SQLException
    {
        return getConnection(dbname, connectOnlyExists, true);
    }
    
    /**
     * データベースコネクションを取得します。<br />
     * 
     * @param dbname データベース名
     * @param connectOnlyExists データベースが存在するときのみ接続する場合は <code>true</code> 、
     *                          存在しないときにデータベースを生成する場合は <code>false</code>
     * @param initialize        データベースを初期化するかどうか。
     * @return {@link Connection} オブジェクト
     * @throws SQLException コネクションが取得できなかった場合
     */
    public synchronized Connection getConnection(final String dbname,
        final boolean connectOnlyExists, final boolean initialize)
        throws SQLException
    {
        if (dbname == null)
        {
            throw new IllegalArgumentException("dbname can't be null");
        }

        DataSource ds = getDataSource(dbname);
        if (ds != null)
        {
            Connection connection = getConnection(ds, dbname);
            if(this.initializedDatabaseSet_.contains(dbname) == false && initialize)
            {
                initialize(dbname, initialize, connection);
            }
            return connection;
        }

        ds = createPoolingDataSource(dbname, connectOnlyExists);

        Connection conn = getConnection(ds, dbname);

        // コネクション取得に成功すれば、 DataSource を登録する
        registDataSource(dbname, ds);
        
        initialize(dbname, initialize, conn);

        return conn;
    }

    private void initialize(final String dbname, final boolean initialize, Connection conn)
        throws SQLException
    {
        try
        {
            // 必要に応じてデータベースの初期化を行う
            boolean isInitialized = DBInitializer.isInitialized(conn);
            if (isInitialized)
            {
                this.initializedDatabaseSet_.add(dbname);
            }
            else if (initialize == true && isInitialized == false)
            {
                DBInitializer.initialize(conn);
                LOGGER.log(DB_INITIALIZED, dbname);
                this.initializedDatabaseSet_.add(dbname);
            }
            
            DBInitializer.reinitialize(conn);
        }
        catch (Exception ex)
        {
            LOGGER.log(EXCEPTION_OCCURED, ex, ex.getMessage());
            SQLException sqlex = new SQLException();
            sqlex.initCause(ex);
            throw sqlex;
        }
    }

    /**
     * データベースのベースディレクトリを設定します。<br />
     *
     * データベースが変更された場合は、コネクションプールをクリアします。<br />
     *
     * @param baseDir ベースディレクトリ。 <code>null</code> を指定した場合は ~（ホームディレクトリ）
     */
    public synchronized void setBaseDir(final String baseDir)
    {
        boolean changeBaseDir = !StringUtil.equals(baseDir, DBManager.getDbDir());

        if (baseDir != null)
        {
            DBManager.setDbDir(baseDir);
        }
        else
        {
            DBManager.setDbDir("~");
        }
        for (DataSourceCreator creator : dataSouceCreatorList__)
        {
            creator.setBaseDir(DBManager.getDbDir());
        }

        if (changeBaseDir)
        {
            closeAll();
        }
    }

    /**
     * データベースが存在するかどうかを調べます。<br />
     *
     * @param dbname データベース名
     * @return データベースが存在する場合は <code>true</code> 、存在しない場合は <code>false</code>
     */
    public boolean existsDatabase(final String dbname)
    {
        // コネクションが取れるかどうかで、DBの存在を判断する
        Connection con = null;
        boolean exist = false;
        try
        {
            con = getConnection(dbname, true);
            exist = true;
        }
        catch (SQLException ex)
        {
            exist = false;
        }
        finally
        {
            SQLUtil.closeConnection(con);
        }
        return exist;
    }

    /**
     * アイドル中のコネクションをすべてクローズします。<br />
     * 使用中のコネクションはクローズされませんので注意してください。
     */
    public void closeAll()
    {
        try
        {
            for (ObjectPool connectionPool : this.connectionPoolMap_.values())
            {
                connectionPool.clear();
            }
            int numActive = getNumActive();
            if (numActive > 0)
            {
                LOGGER.log(ACTIVE_CONNECTIONS_REMAINED, numActive);
            }
            this.connectionPoolMap_.clear();
            this.dataSourceList_.clear();
        }
        catch (Exception ex)
        {
            LOGGER.log(EXCEPTION_OCCURED, ex, ex.getMessage());
        }
    }

    /**
     * コネクションを取得します。<br />
     *
     * @param dataSource データソース
     * @param dbName {@link ConnectionWrapper} に設定するデータベース名
     * @return {@link Connection} オブジェクト
     * @throws SQLException コネクションが取得できなかった場合
     */
    protected Connection getConnection(final DataSource dataSource, final String dbName)
        throws SQLException
    {
        Connection conn = dataSource.getConnection();
        ConnectionWrapper wrapper = new ConnectionWrapper(conn, dbName);
        LOGGER.log(DB_CONNECTED, dbName);
        return wrapper;
    }

    /**
     * 指定されたデータベースのデータソースを取得します。<br />
     *
     * @param dbname データベース名
     * @return データソース
     */
    protected DataSource getDataSource(final String dbname)
    {
        for (DataSourceEntry entry : this.dataSourceList_)
        {
            if (entry.getDbname().equals(dbname))
            {
                return entry.getDataSource();
            }
        }
        return null;
    }

    /**
     * データソースをリストに登録します。<br />
     *
     * @param dbname データベース名
     * @param dataSource データソース
     */
    protected void registDataSource(final String dbname, final DataSource dataSource)
    {
        if (getDataSource(dbname) == null)
        {
            DataSourceEntry entry = new DataSourceEntry(dbname, dataSource);
            this.dataSourceList_.add(entry);
        }
    }

    /**
     * {@link DataSource} を作成します。<br />
     *
     * @param dbname データベース名
     * @param connectOnlyExists データベースが存在するときのみ接続する場合は <code>true</code> 、
     *                          存在しないときにデータベースを生成する場合は <code>false</code>
     * @return {@link DataSource}
     * @throws SQLException データソース作成時に例外が発生した場合
     */
    protected DataSource createPoolingDataSource(final String dbname,
        final boolean connectOnlyExists)
        throws SQLException
    {
        DataSourceCreator creator = getDataSourceCreator();
        return creator.createPoolingDataSource(dbname, connectOnlyExists);
    }

    /**
     * データベース名と {@link DataSource} を紐づけて管理するためのエントリクラスです。<br />
     * 
     * @author y-komori
     */
    private static class DataSourceEntry
    {
        private final String     dbname_;

        private final DataSource dataSource_;

        /**
         * {@link DataSourceEntry} を構築します。<br />
         * 
         * @param dbname データベース名

         * @param dataSource {@link DataSource} オブジェクト
         */
        public DataSourceEntry(final String dbname, final DataSource dataSource)
        {
            this.dbname_ = dbname;
            this.dataSource_ = dataSource;
        }

        /**
         * データベース名を返します。<br />
         * 
         * @return データベース名

         */
        public String getDbname()
        {
            return this.dbname_;
        }

        /**
         * データソースを返します。<br />
         * 
         * @return データソース
         */
        public DataSource getDataSource()
        {
            return this.dataSource_;
        }
    }

    /**
     * プールの中から使用しているオブジェクトの数を返します。<br />
     *
     * @return 使用しているオブジェクトの数
     */
    private int getNumActive()
    {
        int numActive = 0;
        for (ObjectPool objectPool : this.connectionPoolMap_.values())
        {
            numActive += objectPool.getNumActive();
        }
        return numActive;
    }

    /**
     * コネクションプールから取得します。
     * @param key キー
     * @return コネクションプール
     */
    public ObjectPool getConnectionPool(final String key)
    {
        return this.connectionPoolMap_.get(key);
    }

    /**
     * コネクションプールから新規作成します。
     * @param key キー
     * @return コネクションプール
     */
    public ObjectPool createNewConnectionPool(String key)
    {
        ObjectPool connectionPool = this.connectionPoolMap_.get(key);
        if (connectionPool == null)
        {
            connectionPool = new StackObjectPool();
            this.connectionPoolMap_.put(key, connectionPool);
        }
        return connectionPool;
    }

    /**
     * データソース作成オブジェクトを取得します。<br />
     * 
     * @return データソース作成オブジェクト
     */
    public synchronized DataSourceCreator getDataSourceCreator()
    {
        for (DataSourceCreator creator : dataSouceCreatorList__)
        {
            if (creator.isTarget())
            {
                return creator;
            }
        }
        return null;
    }

    /**
     * シーケンス番号を取得するSQLを取得します。
     * @param sequenceName シーケンス番号
     * @return シーケンス番号取得SQL
     */
    public String getSequenceSql(String sequenceName)
    {
        DataSourceCreator creator = getDataSourceCreator();
        return creator.getSequenceSql(sequenceName);
    }
}
