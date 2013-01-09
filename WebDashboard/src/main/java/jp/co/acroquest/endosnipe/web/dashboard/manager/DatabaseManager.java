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
package jp.co.acroquest.endosnipe.web.dashboard.manager;

import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.co.acroquest.endosnipe.web.dashboard.config.AgentSetting;
import jp.co.acroquest.endosnipe.web.dashboard.config.DataBaseConfig;
import jp.co.acroquest.endosnipe.web.dashboard.listener.javelin.JavelinNotifyListener;

/**
 * DBを管理するクラス
 * @author fujii
 *
 */
public final class DatabaseManager
{
    /** イスタンス */
    private static DatabaseManager instance__ = new DatabaseManager();

    /** データベース名 */
    private String                 dbName_;

    /** データベースの設定オブジェクト */
    private DataBaseConfig         dbConfig_;

    /**
     * インスタンス化を阻止するプライベートコンストラクタ。
     */
    private DatabaseManager()
    {
        // Do Nothing.
    }

    /**
     * インスタンスを取得します。
     * @return {@link DatabaseManager}オブジェクト
     */
    public static DatabaseManager getInstance()
    {
        return instance__;
    }

    /**
     * データベース名を取得する。
     * @return データベース名
     */
    public String getDBName()
    {
        return this.dbName_;
    }

    /**
     * データベース名を設定する。
     * @param dbName データベース名
     */
    public void setDBName(String dbName)
    {
        this.dbName_ = dbName;
    }

    /**
     * DataBaseの設定オブジェクトを保持します。
     * @param dbConfig DataBaseの設定オブジェクト
     */
    public void setDataBaseConfig(DataBaseConfig dbConfig)
    {
        this.dbConfig_ = dbConfig;
    }

    /**
     * DataBaseの設定情報を取得します。
     * @return DataBaseの設定情報
     */
    public DataBaseConfig getDataBaseConfig()
    {
        return this.dbConfig_;
    }

    /**
     * 指定したエージェントIDに対応するデータベース名を取得します。
     * @param agentId エージェントID
     * @return 指定したエージェントIDに対応するデータベース名
     */
    public String getDataBaseName(int agentId)
    {
        if (this.dbConfig_ == null)
        {
            return null;
        }

        if ("server".equals(dbConfig_.getConnectionMode()))
        {
        	// serverモードの時はcollector.propertiesに定義した値を使用する        	
            return dbConfig_.getDatabaseName();
            //return JavelinNotifyListener.getDatabaseName(agentId);
        }
        else
        {

            List<AgentSetting> agentSettings = this.dbConfig_.getAgentSettingList();

            if (agentSettings.size() < agentId)
            {
                return null;
            }
            AgentSetting setting = agentSettings.get(agentId - 1);
            return setting.databaseName;
        }
    }

}
