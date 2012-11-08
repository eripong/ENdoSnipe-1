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
package jp.co.acroquest.endosnipe.data.util;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jp.co.acroquest.endosnipe.common.logger.ENdoSnipeLogger;
import jp.co.acroquest.endosnipe.common.util.SQLUtil;
import jp.co.acroquest.endosnipe.data.DBInitializer;
import jp.co.acroquest.endosnipe.data.ENdoSnipeDataAccessorPlugin;
import jp.co.acroquest.endosnipe.data.ENdoSnipeDataAccessorPluginProvider;
import jp.co.acroquest.endosnipe.data.LogMessageCodes;
import jp.co.acroquest.endosnipe.data.db.ConnectionManager;
import jp.co.acroquest.endosnipe.data.db.DBManager;
import jp.co.acroquest.endosnipe.data.preference.BaseDirectoryChangableListener;
import jp.co.acroquest.endosnipe.data.preference.DatabaseItem;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * DataAccessor の設定を扱うユーティリティクラス。<br />
 *
 * @author sakamoto
 */
public class DataAccessorConfigUtil implements LogMessageCodes
{
    /** ロガー */
    private static final ENdoSnipeLogger               LOGGER               =
                                                                                ENdoSnipeLogger.getLogger(
                                                                                                          DataAccessorConfigUtil.class,
                                                                                                          ENdoSnipeDataAccessorPluginProvider.INSTANCE);

    /** DB 種別を取得するためのキー */
    private static final String                        PREF_DB_KIND_KEY     =
                                                                                "database.dbname";

    /** DB のフォルダのパスを取得するためのキー */
    private static final String                        PREF_DB_DIR_KEY      = "database.dir";

    /** 接続先DBのホストを取得するためのキー */
    private static final String                        PREF_DB_HOST_KEY     = "database.host";

    /** 接続先DBのポート番号を取得するためのキー */
    private static final String                        PREF_DB_PORT_KEY     = "database.port";

    /** 接続先DBのユーザ名を取得するためのキー */
    private static final String                        PREF_DB_USER_KEY     = "database.user";

    /** 接続先DBのパスワードを取得するためのキー */
    private static final String                        PREF_DB_PASS_KEY     = "database.password";

    /** DB 種別のデフォルト値 */
    public static final String                         PREF_DB_KIND_DEFAULT = "H2";

    /** DB のフォルダのパスのデフォルト値 */
    public static final String                         PREF_DB_DIR_DEFAULT  =
                                                                                ".metadata/.plugins/jp.co.acroquest.endosnipe.data/db";

    /** 接続先DBのホストのデフォルト値 */
    public static final String                         PREF_DB_HOST_DEFAULT = "localhost";

    /** 接続先DBのポート番号のデフォルト値 */
    public static final String                         PREF_DB_PORT_DEFAULT = "5432";

    /** 接続先DBのユーザ名のデフォルト値 */
    public static final String                         PREF_DB_USER_DEFAULT = "endosnipe";

    /** 接続先DBのパスワードのデフォルト値 */
    public static final String                         PREF_DB_PASS_DEFAULT = "endosnipe";

    /** PostgreSQLデータベースのドライバクラス名称 */
    public static final String                         POSTGRES_DRIVER      =
                                                                                "org.postgresql.Driver";

    /** BaseDirectory
     *  */
    private static Set<BaseDirectoryChangableListener> changableListeners__ =
                                                                                new HashSet<BaseDirectoryChangableListener>();

    static
    {
        setDefaultPreference();
    }

    /**
     * コンストラクタを隠蔽します。<br />
     */
    private DataAccessorConfigUtil()
    {
        // Do nothing.
    }

    /**
     * DataAccessorPlugin で指定されたデータベースの基準ディレクトリを返します。<br />
     *
     * @return データベースの基準ディレクトリ
     */
    public static String getDatabaseDirectory()
    {
        IPreferenceStore store = ENdoSnipeDataAccessorPlugin.getDefault().getPreferenceStore();
        String dbdir = store.getString(PREF_DB_DIR_KEY);
        return dbdir;
    }

    /**
     * データベースの基準ディレクトリをプリファレンスストアにセットします。<br />
     *
     * @param directory データベースの基準ディレクトリ
     */
    public static void setDatabaseDirectory(final String directory)
    {
        IPreferenceStore store = ENdoSnipeDataAccessorPlugin.getDefault().getPreferenceStore();
        store.setValue(PREF_DB_DIR_KEY, directory);
    }

    /**
     * デフォルトのデータベースの基準ディレクトリを返します。<br />
     *
     * @return データベースの基準ディレクトリ
     */
    public static String getDefaultDatabaseDirectory()
    {
        IPreferenceStore store = ENdoSnipeDataAccessorPlugin.getDefault().getPreferenceStore();
        String dbdir = store.getDefaultString(PREF_DB_DIR_KEY);
        return dbdir;
    }

    /**
     * デフォルトの設定をプリファレンスストアにセットします
     */
    public static void setDefaultPreference()
    {
        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        IPath workspacePath = workspace.getRoot().getLocation();
        File file = workspacePath.toFile();
        file = new File(file.getAbsolutePath(), PREF_DB_DIR_DEFAULT);
        IPreferenceStore store = ENdoSnipeDataAccessorPlugin.getDefault().getPreferenceStore();
        store.setDefault(PREF_DB_DIR_KEY, file.toString());
        store.setDefault(PREF_DB_KIND_KEY, PREF_DB_KIND_DEFAULT);
        store.setDefault(PREF_DB_HOST_KEY, PREF_DB_HOST_DEFAULT);
        store.setDefault(PREF_DB_PORT_KEY, PREF_DB_PORT_DEFAULT);
        store.setDefault(PREF_DB_USER_KEY, PREF_DB_USER_DEFAULT);
        store.setDefault(PREF_DB_PASS_KEY, PREF_DB_PASS_DEFAULT);
    }

    /**
     * 基準ディレクトリ配下にあるデータベースの一覧を返します。<br />
     *
     * @return データベース一覧
     */
    public static List<DatabaseItem> getDatabaseList()
    {
        if (DBManager.isDefaultDb() == true)
        {
            String baseDirectory = DBManager.getDbDir();
            return getH2DatabaseList(baseDirectory);
        }
        return getPostgresDatabaseList();
    }

    /**
     * 指定されたディレクトリ直下にあるH2データベースの一覧を返します。<br />
     *
     * @param baseDirectory ディレクトリ
     * @return データベース一覧
     */
    public static List<DatabaseItem> getH2DatabaseList(final String baseDirectory)
    {
        List<DatabaseItem> databaseList = new ArrayList<DatabaseItem>();

        // baseDirectory直下にあるディレクトリが、DBの候補
        File baseFolder = new File(baseDirectory);
        File[] directoryArray = null;
        if (baseFolder.isDirectory())
        {
            directoryArray = baseFolder.listFiles();
        }
        if (directoryArray == null)
        {
            return databaseList;
        }

        // データベースが存在しない場合、データベースを作成しないモードにする
        ConnectionManager connectionManager = ConnectionManager.getInstance();
        String prevDbDir = DBManager.getDbDir();
        connectionManager.setBaseDir(baseDirectory);

        for (File folderItem : directoryArray)
        {
            if (folderItem.isDirectory())
            {
                String folderName = folderItem.getName();
                try
                {
                    DatabaseItem databaseItem = DatabaseItem.createDatabaseItem(folderName);
                    if (databaseItem != null)
                    {
                        // 正しいデータベース（＝ホスト情報が存在する）のときのみリストに追加する
                        databaseList.add(databaseItem);
                    }
                }
                catch (SQLException ex)
                {
                    LOGGER.log(DB_ACCESS_ERROR, ex, ex.getMessage());
                }
            }
        }

        // 元の設定・モードに戻す
        connectionManager.setBaseDir(prevDbDir);

        return databaseList;
    }

    /**
     * 指定された接続先に存在するPostgreSQLデータベースの一覧を返します。<br />
     * @return データベース一覧
     */
    public static List<DatabaseItem> getPostgresDatabaseList()
    {
        List<DatabaseItem> resultList = new ArrayList<DatabaseItem>();
        List<String> databaseNameList = getPostgresDatabaseNameList();

        //各データベースに対するDataSourceを取得
        for (String databaseName : databaseNameList)
        {
            Connection connection = null;
            try
            {
                connection =
                    ConnectionManager.getInstance().getConnection(databaseName, true, false);
                
                // 初期化されていない場合は無視する。
                if (DBInitializer.isInitialized(connection) == false)
                {
                    continue;
                }
            }
            catch (SQLException ex)
            {
                continue;
            }
            finally
            {
               if (connection != null)
               {
                   SQLUtil.closeConnection(connection);
               }
            }
            
            try
            {
                DatabaseItem target = DatabaseItem.createDatabaseItem(databaseName);
                if (target != null)
                {
                    resultList.add(target);
                }
            }
            catch (SQLException ex)
            {
                continue;
            }
        }
        return resultList;
    }

    /**
     * PostgreSQLのデータベース一覧を取得する。<br />
     * 
     * @return データベース一覧
     */
    private static List<String> getPostgresDatabaseNameList()
    {
        List<String> databaseNameList = new ArrayList<String>();
        try
        {
            Class.forName(POSTGRES_DRIVER);
        }
        catch (ClassNotFoundException ex)
        {
            return databaseNameList;
        }
        String baseUri = createDatabaseURI(DBManager.getHostName(), DBManager.getPort());
        Connection connection = null;
        Statement state = null;
        ResultSet rs = null;

        //pg_databaseテーブルは名称しか使用しないため、Dao等は使用せず、直でアクセスし、１レコード目を取得する
        try
        {
            connection =
                DriverManager.getConnection(baseUri, DBManager.getUserName(),
                                            DBManager.getPassword());
            state = connection.createStatement();
            rs = state.executeQuery("SELECT * FROM pg_database where datistemplate = false;");
            while (rs.next() == true)
            {
                String databaseName = rs.getString(1);
                databaseNameList.add(databaseName);
            }
        }
        catch (SQLException sqlex)
        {
            return databaseNameList;
        }
        finally
        {
            SQLUtil.closeResultSet(rs);
            SQLUtil.closeStatement(state);
            SQLUtil.closeConnection(connection);
        }
        return databaseNameList;
    }

    /**
     * データベースの基準ディレクトリの変更を許可するかどうかを通知するリスナを追加します。<br />
     *
     * @param listener リスナ
     */
    public static void addBaseDirectoryChangableListener(
        final BaseDirectoryChangableListener listener)
    {
        changableListeners__.add(listener);
    }

    /**
     * 基準ディレクトリの変更を許可するかどうかを返します。<br />
     *
     * @return 基準ディレクトリの変更を許可する場合は <code>true</code> 、
     *         変更を許可しない場合は <code>false</code>
     */
    public static boolean isBaseDirectoryChangeAllowed()
    {
        boolean ret = true;
        for (BaseDirectoryChangableListener listener : changableListeners__)
        {
            if (!listener.isChangeBaseDirectoryAllowed())
            {
                // 1 つでも変更を許可しないリスナがあれば、変更を許可しない
                ret = false;
                break;
            }
        }
        return ret;
    }

    /**
     * PostgreSQLデータベース用の接続文字列を作成
     * @param host ホスト
     * @param port ポート
     * @return 接続文字列
     */
    public static String createDatabaseURI(final String host, final String port)
    {
        String base = "jdbc:postgresql://";
        String uri = base + host + ":" + port + "/";
        return uri;
    }

}
