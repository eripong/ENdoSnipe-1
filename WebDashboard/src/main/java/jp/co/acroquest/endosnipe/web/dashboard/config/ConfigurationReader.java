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
package jp.co.acroquest.endosnipe.web.dashboard.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.co.acroquest.endosnipe.common.logger.ENdoSnipeLogger;
import jp.co.acroquest.endosnipe.common.util.StreamUtil;
import jp.co.acroquest.endosnipe.data.db.DatabaseType;
import jp.co.acroquest.endosnipe.web.dashboard.constants.LogMessageCodes;
import jp.co.acroquest.endosnipe.web.dashboard.exception.InitializeException;

/**
 * ENdoSnipe DataCollector の設定ファイルを読み込むためのクラスです。<br />
 * 
 * <p>
 * <b>設定ファイルの書式</b><br />
 * 設定ファイルは Java 標準のプロパティファイル形式です。<br />
 * 各キーは、「<i>キー名</i>.<i>エージェントID</i>」の形式で、
 * 複数のエージェントへの接続パラメータを記述できます。<br />
 * ここで、エージェントIDはピリオドを含まない任意の文字列とします。<br />
 * </p>
 * 
 * <p>
 * <b>キー一覧</b><br />
 * <dl>
 *   <dt>agent.host</dt>
 *   <dd>エージェントの接続先ホスト名</dd>
 *   <dt>agent.port</dt>
 *   <dd>エージェントの接続先ポート番号
 *   (数値以外の文字列が指定された場合、18000 として解釈します)</dd>
 *   <dt>database.name</dt>
 *   <dd>エージェントのデータベース名</dd>
 *   <dt>database.dir</dt>
 *   <dd>エージェントのデータベースディレクトリ</dd>
 * </dl>
 * </p>
 * 
 * <p>
 * <b>記述例</b><br />
 * <code>
 * agent.host.1=localhost<br />
 * agent.port.1=18000<br />
 * <br />
 * agent.host.2=192.168.1.1<br />
 * agent.port.2=18001<br />
 * </code>
 * </p>
 * 
 * @author fujii
 */
public class ConfigurationReader
{
    private static final ENdoSnipeLogger logger_                      =
                                                                        ENdoSnipeLogger.getLogger(ConfigurationReader.class);

    /** DataCollectorConfig */
    private static DataBaseConfig        config__                     = null;

    /** 接続モードを表す接頭辞 */
    private static final String          CONNECTION_MODE              = "connection.mode";

    /** ホスト名(serverモード)を表す接頭辞 */
    private static final String          SERVER_MODE_ACCEPT_HOST      = "accept.host";

    /** ポート番号(serverモード)を表す接頭辞 */
    private static final String          SERVER_MODE_ACCEPT_PORT      = "accept.port";

    /** ホスト名を表す接頭辞 */
    private static final String          AGENT_HOST                   = "javelin.host.";

    /** ポート番号を表す接頭辞 */
    private static final String          AGENT_PORT                   = "javelin.port.";

    /** ポート番号を表す接頭辞 */
    private static final String          CLIENT_MODE_ACCEPT_HOST      = "datacollector.accepthost.";

    /** ポート番号を表す接頭辞 */
    private static final String          CLIENT_MODE_ACCEPT_PORT      = "datacollector.acceptport.";

    /** データベース名(serverモード) */
    private static final String          DATABASE_NAME                = "database.name";

    /** データベース名を表す接頭辞 */
    private static final String          DATABASE_NAME_PREFIX         = "database.name.";

    /** データベースの種類を表す接頭辞 */
    private static final String          DATABASE_TYPE                = "database.type";

    /** データベースディレクトリを表す接頭辞 */
    private static final String          DATABASE_DIR                 = "database.dir";

    /** データベースのホストアドレスを表す接頭辞 */
    private static final String          DATABASE_HOST                = "database.host";

    /** データベースのポート番号を表す接頭辞 */
    private static final String          DATABASE_PORT                = "database.port";

    /** データベースのログインユーザ名を表す接頭辞 */
    private static final String          DATABASE_USERNAME            = "database.username";

    /** データベースのログインパスワードを表す接頭辞 */
    private static final String          DATABASE_PASSWORD            = "database.password";

    /** データベース名で使用できる文字を、正規表現で表したもの */
    private static final String          DATABASE_NAME_USABLE_PATTERN =
                                                                        "[A-Za-z0-9#$%@=\\+\\-_~\\.]*";

    /** パラメータ定義ファイルのパス */
    private static String                configFilePath__;

    /**
     * ストリームから設定ファイルを読み込み、{@link DataBaseConfig} を構築します。<br />
     * 
     * @param inputStream 入力ストリーム
     * @return {@link DataBaseConfig} オブジェクト
     * @throws IOException 入出力エラーが発生した場合
     * @throws InitializeException 初期化例外が発生した場合
     */
    public static DataBaseConfig load(final InputStream inputStream)
        throws IOException,
            InitializeException
    {

        Properties props = new Properties();
        try
        {
            props.load(inputStream);
            StreamUtil.closeStream(inputStream);
        }
        catch (IOException ex)
        {
            StreamUtil.closeStream(inputStream);
            throw ex;
        }

        Map<Integer, AgentSetting> settings = new TreeMap<Integer, AgentSetting>();

        DataBaseConfig config = new DataBaseConfig();

        Enumeration<Object> keys = props.keys();
        while (keys.hasMoreElements())
        {
            String key = (String)keys.nextElement();
            setCommonValue(config, key, props.getProperty(key));
            int agentId = getAgentId(key);
            if (agentId > 0)
            {
                AgentSetting setting = settings.get(agentId);
                if (setting == null)
                {
                    setting = new AgentSetting();
                    settings.put(agentId, setting);
                    setting.agentId = agentId;
                }

                setValue(setting, key, props.getProperty(key));
            }
        }

        int agentSequece = 1;
        // 
        for (AgentSetting setting : settings.values())
        {
            if (setting.hostName != null && setting.hostName != "")
            {
                if (agentSequece == setting.agentId)
                {
                    config.addAgentSetting(setting);
                    agentSequece++;
                }
                else
                {
                    break;
                }
            }
        }
        return config;
    }

    /**
     * 指定されたパスから設定ファイルを読み込み、{@link DataBaseConfig} を構築します。<br />
     * 
     * @param path パス
     * @return {@link DataBaseConfig} オブジェクト
     * @throws IOException 入出力エラーが発生した場合
     * @throws InitializeException 初期化例外が発生した場合
     */
    public static DataBaseConfig load(final String path)
        throws IOException,
            InitializeException
    {
        if (config__ != null)
        {
            return config__;
        }

        File file = new File(path);
        configFilePath__ = file.getCanonicalPath();
        FileInputStream is = new FileInputStream(file);
        try
        {
            config__ = load(is);
        }
        finally
        {
            StreamUtil.closeStream(is);
        }
        return config__;
    }

    /**
     * 設定ファイルに記述された値を設定します。
     *
     * @param setting 各エージェントの設定
     * @param key キー
     * @param value 値
     * @throws InitializeException パラメータ初期化例外が発生した場合
     */
    private static void setValue(final AgentSetting setting, final String key, final String value)
        throws InitializeException
    {
        if (key.startsWith(AGENT_HOST))
        {
            setting.hostName = value;
        }
        else if (key.startsWith(AGENT_PORT))
        {
            try
            {
                setting.port = Integer.parseInt(value);
            }
            catch (NumberFormatException ex)
            {
                logger_.log(LogMessageCodes.FAIL_TO_READ_PARAMETER, configFilePath__, key);
                throw new InitializeException(ex);
            }
        }
        else if (key.startsWith(CLIENT_MODE_ACCEPT_HOST))
        {
            setting.acceptHost = value;
        }
        else if (key.startsWith(CLIENT_MODE_ACCEPT_PORT))
        {
            try
            {
                setting.acceptPort = Integer.parseInt(value);
            }
            catch (NumberFormatException ex)
            {
                logger_.log(LogMessageCodes.FAIL_TO_READ_PARAMETER, configFilePath__, key);
                throw new InitializeException(ex);
            }
        }
        else if (key.startsWith(DATABASE_NAME_PREFIX))
        {
            if (isValidDBName(value))
            {
                setting.databaseName = value;
            }
            else
            {
                logger_.log(LogMessageCodes.FAIL_TO_READ_PARAMETER, configFilePath__, key);
                throw new InitializeException("Invalid Unit.");
            }
        }
    }

    /**
     * 共通パラメータを設定します。<br />
     * 
     * @param config {@link DataBaseConfig}オブジェクト
     * @param key キー
     * @param value 値
     * @throws InitializeException パラメータの初期化に失敗した場合
     */
    private static void setCommonValue(final DataBaseConfig config, final String key,
            final String value)
        throws InitializeException
    {
        if (DATABASE_TYPE.equals(key))
        {
            DatabaseType databaseType = DatabaseType.fromId(value);
            if (databaseType == null)
            {
                throw new InitializeException("Invalid Database Type.");
            }
            config.setDatabaseType(databaseType);
        }
        else if (DATABASE_DIR.equals(key))
        {
            config.setBaseDir(value);
        }
        else if (DATABASE_HOST.equals(key))
        {
            config.setDatabaseHost(value);
        }
        else if (DATABASE_PORT.equals(key))
        {
            config.setDatabasePort(value);
        }
        else if (DATABASE_NAME.equals(key))
        {
            config.setDatabaseName(value);
        }
        else if (DATABASE_USERNAME.equals(key))
        {
            config.setDatabaseUserName(value);
        }
        else if (DATABASE_PASSWORD.equals(key))
        {
            config.setDatabasePassword(value);
        }
        else if (CONNECTION_MODE.equals(key))
        {
            config.setConnectionMode(value);
        }
        else if (SERVER_MODE_ACCEPT_HOST.equals(key))
        {
            config.getServerModeAgentSetting().acceptHost = value;
        }
        else if (SERVER_MODE_ACCEPT_PORT.equals(key))
        {
            config.getServerModeAgentSetting().acceptPort = Integer.parseInt(value);
        }
    }

    /**
     * 各エージェントのIDを取得します。<br />
     * 
     * @param key キー
     * @return　エージェントのID
     * @throws InitializeException パラメータの初期化に失敗した場合
     */
    private static int getAgentId(final String key)
        throws InitializeException
    {
        int pos = key.lastIndexOf(".");
        if (pos < 0)
        {
            return -1;
        }
        String hostNumStr = key.substring(pos + 1);
        int hostNum = -1;
        try
        {
            hostNum = Integer.parseInt(hostNumStr);
        }
        catch (NumberFormatException ex)
        {
            // Do Nothing.
        }
        return hostNum;
    }

    /**
     * 設定ファイルの絶対パスを返します。<br />
     * 
     * @return 設定ファイルの絶対パス
     */
    public static String getAbsoluteFilePath()
    {
        return configFilePath__;
    }

    /**
     * 引数で指定されたデータベース名が有効であるかどうか判定します。<br />
     * 有効文字一覧<br />
     * <li>半角アルファベット</li>
     * <li>半角数字</li>
     * <li>「#」、「$」、「%」、「@」、「=」、「+」、「-」（ハイフン）、「_」（アンダースコア）、「~」</li>
     * 
     * @param databaseName データベース名
     * @return 有効である場合、<code>true</code>
     */
    private static boolean isValidDBName(final String databaseName)
    {
        Pattern pattern = Pattern.compile(DATABASE_NAME_USABLE_PATTERN);
        Matcher matcher = pattern.matcher(databaseName);
        return matcher.matches();
    }
}
