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
package jp.co.acroquest.endosnipe.data.preference;

import java.io.File;

import jp.co.acroquest.endosnipe.data.ENdoSnipeDataAccessorPlugin;
import jp.co.acroquest.endosnipe.data.db.DBManager;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * プリファレンスに保存されたDB情報の操作を行なうUtilクラスです。
 * @author fujii
 *
 */
public class DBPreferenceUtil
{
    /** データベース種別保持用のキー */
    public static final String DBNAME_STORE         = "database.dbname";

    /** ベースディレクトリ保持用のキー */
    public static final String BASEDIR_STORE        = "database.dir";

    /** ホスト保持用のキー */
    public static final String HOST_STORE           = "database.host";

    /** ポート保持用のキー */
    public static final String PORT_STORE           = "database.port";

    /** ユーザ名保持用のキー */
    public static final String USERNAME_STORE       = "database.user";

    /** パスワード保持用のキー */
    public static final String PASSWORD_STORE       = "database.password";

    /** H2データベースの名称 */
    public static final String H2_NAME              = "H2";

    /** PostgreSQLデータベースの名称 */
    public static final String POSTGRE_SQL_NAME     = "PostgreSQL";

    /** DB 種別のデフォルト値 */
    public static final String PREF_DB_KIND_DEFAULT = "H2";

    /** DB のフォルダのパスのデフォルト値 */
    public static final String PREF_DB_DIR_DEFAULT  =
                                                        ".metadata/.plugins/jp.co.acroquest.endosnipe.data/db";

    /** 接続先DBのホストのデフォルト値 */
    public static final String PREF_DB_HOST_DEFAULT = "localhost";

    /** 接続先DBのポート番号のデフォルト値 */
    public static final String PREF_DB_PORT_DEFAULT = "5432";

    /** 接続先DBのユーザ名のデフォルト値 */
    public static final String PREF_DB_USER_DEFAULT = "endosnipe";

    /** 接続先DBのパスワードのデフォルト値 */
    public static final String PREF_DB_PASS_DEFAULT = "endosnipe";

    /**
     * インスタンス化を防止するためのデフォルトコンストラクタ
     */
    private DBPreferenceUtil()
    {
    }

    /**
     * 空であるかどうかを返します。
     * @param args 引数
     * @return 空である場合、<code>true</code>
     */
    public static boolean isNotEmpty(String args)
    {
        return args != null && (!args.equals(""));
    }

    /**
     * DBの初期化を行ないます。
     */
    public static synchronized void initDb()
    {
        IPreferenceStore store = ENdoSnipeDataAccessorPlugin.getDefault().getPreferenceStore();
        String dbName = store.getString(DBNAME_STORE);
        String dbDir = store.getString(BASEDIR_STORE);
        String host = store.getString(HOST_STORE);
        String port = store.getString(PORT_STORE);
        String userName = store.getString(USERNAME_STORE);
        String password = store.getString(PASSWORD_STORE);

        boolean useDefaultDb = true;
        if (isNotEmpty(dbName) && dbName.equals(POSTGRE_SQL_NAME))
        {
            useDefaultDb = false;
        }
        DBManager.updateSettings(useDefaultDb, dbDir, host, port, userName, password);
    }

    /**
     * 設定を保存します。<br />
     * 
     * @param dbName データベース名
     * @param dbDir データベース基準ディレクトリ
     * @param host ホスト
     * @param port ポート
     * @param userName ユーザ名
     * @param password パスワード
     */
    public static synchronized void storeSettings(String dbName, String dbDir, String host,
        String port, String userName, String password)
    {
        IPreferenceStore store = ENdoSnipeDataAccessorPlugin.getDefault().getPreferenceStore();

        store.setValue(DBNAME_STORE, dbName);

        boolean useDefaultDb = true;
        if (isNotEmpty(dbName) && dbName.equals(POSTGRE_SQL_NAME))
        {
            useDefaultDb = false;
        }

        if (useDefaultDb == true)
        {
            store.setValue(BASEDIR_STORE, dbDir);
        }
        else
        {
            store.setValue(HOST_STORE, host);
            store.setValue(PORT_STORE, port);
            store.setValue(USERNAME_STORE, userName);
            store.setValue(PASSWORD_STORE, password);
        }
    }

    /**
     * 設定値をデフォルトに戻します
     */
    public static void initPreference()
    {
        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        IPath workspacePath = workspace.getRoot().getLocation();
        File file = workspacePath.toFile();
        file = new File(file.getAbsolutePath(), PREF_DB_DIR_DEFAULT);
        IPreferenceStore store = ENdoSnipeDataAccessorPlugin.getDefault().getPreferenceStore();
        store.setValue(BASEDIR_STORE, file.toString());
        store.setValue(DBNAME_STORE, PREF_DB_KIND_DEFAULT);
        store.setValue(HOST_STORE, PREF_DB_HOST_DEFAULT);
        store.setValue(PORT_STORE, PREF_DB_PORT_DEFAULT);
        store.setValue(USERNAME_STORE, PREF_DB_USER_DEFAULT);
        store.setValue(PASSWORD_STORE, PREF_DB_PASS_DEFAULT);
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
        store.setDefault(BASEDIR_STORE, file.toString());
        store.setDefault(DBNAME_STORE, PREF_DB_KIND_DEFAULT);
        store.setDefault(HOST_STORE, PREF_DB_HOST_DEFAULT);
        store.setDefault(PORT_STORE, PREF_DB_PORT_DEFAULT);
        store.setDefault(USERNAME_STORE, PREF_DB_USER_DEFAULT);
        store.setDefault(PASSWORD_STORE, PREF_DB_PASS_DEFAULT);
    }
}
