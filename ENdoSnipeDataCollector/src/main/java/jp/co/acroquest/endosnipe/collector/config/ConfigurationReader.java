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
package jp.co.acroquest.endosnipe.collector.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.co.acroquest.endosnipe.collector.ENdoSnipeDataCollectorPluginProvider;
import jp.co.acroquest.endosnipe.collector.LogMessageCodes;
import jp.co.acroquest.endosnipe.collector.exception.InitializeException;
import jp.co.acroquest.endosnipe.common.logger.ENdoSnipeLogger;
import jp.co.acroquest.endosnipe.common.util.StreamUtil;
import jp.co.acroquest.endosnipe.data.db.DatabaseType;

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
 *   <dd>レポートを出力するディレクトリ</dd>
 *   <dt>report.output.dir</dt>
 *   <dd>エージェントのデータベースディレクトリ</dd>
 *   <dt>resource.get.interval</dt>
 *   <dd>エージェントのリソース取得間隔
 *   (数値以外の文字列が指定された場合、5000 として解釈します)</dd>
 *   <dt>javelin.log.storage.period</dt>
 *   <dd>エージェントのJavelinログの最大蓄積期間</dd>
 *   <dt>measurement.log.storage.period</dt>
 *   <dd>エージェントの計測データの最大蓄積期間</dd>
 *   <dt>javelin.javelin.log.split</dt>
 *   <dd>エージェントがJavelinログを分割保存するかどうか</dd>
 *   <dt>javelin.log.split.size</dt>
 *   <dd>エージェントの1レコード辺りの最大サイズ
 *   (javelin.javelin.log.splitがtrueの時のみ有効)<br />
 *   (数値以外の文字列が指定された場合、300(KBytes) として解釈します)<br />
 *   <dd>エージェントがJavelinログを分割保存する場合の閾値
 *   (javelin.javelin.log.splitがtrueの時のみ有効)<br />
 *   (数値以外の文字列が指定された場合、1024(KBytes) として解釈します)<br />
 *   </dd>
 *   <dt>collector.smtp.sendMail</dt>
 *   <dd>メール通知を送信するかどうか</dd>
 *   <dt>collector.smtp.server</dt>
 *   <dd>メールサーバ</dd>
 *   <dt>collector.smtp.encoding</dt>
 *   <dd>メールのエンコーディング</dd>
 *   <dt>collector.smtp.from</dt>
 *   <dd>送信元メールアドレス</dd>
 *   <dt>collector.smtp.to</dt>
 *   <dd>送信先メールアドレス</dd>
 *   <dt>collector.smtp.subject</dt>
 *   <dd>メールSubject</dd>
 *   <dt>collector.smtp.template.jvn</dt>
 *   <dd>メールテンプレート(jvnアラーム用)</dd>
 *   <dt>collector.smtp.template.measurement</dt>
 *   <dd>メールテンプレート(計測値アラーム用)</dd>
 *   <dt>collector.snmp.sendTrap</dt>
 *   <dd>SNMPTrapを送信するかどうか</dd>
 *   <dt>collector.snmp.managers</dt>
 *   <dd>マネージャリスト</dd>
 *   <dt>collector.snmp.trapPort</dt>
 *   <dd>SNMP Trapポート番号</dd>
 *   <dt>collector.snmp.version</dt>
 *   <dd>SNMP Version</dd>
 *   <dt>collector.snmp.trapCommunity</dt>
 *   <dd>Trapコミュニティ名</dd>
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
 * @author y-komori
 */
public class ConfigurationReader
{
    private static final ENdoSnipeLogger logger_                               =
                                                                                 ENdoSnipeLogger.getLogger(ConfigurationReader.class,
                                                                                                           ENdoSnipeDataCollectorPluginProvider.INSTANCE);

    /** DataCollectorConfig */
    private static DataCollectorConfig   config__                              = null;

    /** 改行文字。 */
    private static final String          LS                                    =
                                                                                 System.getProperty("line.separator");

    /** ホスト名を表す接頭辞 */
    private static final String          SERVER_HOST                           = "server.host.";

    /** ポート番号を表す接頭辞 */
    private static final String          SERVER_PORT                           = "server.port.";

    /** ホスト名を表す接頭辞 */
    private static final String          AGENT_HOST                            = "javelin.host.";

    /** ポート番号を表す接頭辞 */
    private static final String          AGENT_PORT                            = "javelin.port.";

    /** ポート番号を表す接頭辞 */
    private static final String          ACCEPT_PORT                           =
                                                                                 "datacollector.acceptport.";

    /** データベース名を表す接頭辞 */
    private static final String          DATABASE_NAME                         = "database.name";

    /** データベースの種類を表す接頭辞 */
    private static final String          DATABASE_TYPE                         = "database.type";

    /** データベースディレクトリを表す接頭辞 */
    private static final String          DATABASE_DIR                          = "database.dir";

    /** データベースのホストアドレスを表す接頭辞 */
    private static final String          DATABASE_HOST                         = "database.host";

    /** データベースのポート番号を表す接頭辞 */
    private static final String          DATABASE_PORT                         = "database.port";

    /** データベースのログインユーザ名を表す接頭辞 */
    private static final String          DATABASE_USERNAME                     =
                                                                                 "database.username";

    /** データベースのログインパスワードを表す接頭辞 */
    private static final String          DATABASE_PASSWORD                     =
                                                                                 "database.password";

    /** リソース取得間隔を表す接頭辞 */
    private static final String          RESOURCE_INTERVAL                     =
                                                                                 "resource.get.interval";

    /** リソースモニタリングの設定ファイル名を表す接頭辞 */
    private static final String          RESOURCE_MONITORING                   =
                                                                                 "resource.config.filename";

    /** レポート出力先ディレクトリ */
    private static final String          REPORT_OUTPUT_DIR                     =
                                                                                 "report.output.dir";

    /** 接続モード */
    private static final String          CONNECTION_MODE                       = "connection.mode";

    /** 待ち受けホスト */
    private static final String          SERVER_ACCEPT_HOST                    = "accept.host";

    /** 待ち受けポート */
    private static final String          SERVER_ACCEPT_PORT                    = "accept.port";

    /** Javelinログの最大蓄積期間 (共通設定) */
    private static final String          COMMON_JVN_LOG_STORAGE_PERIOD         =
                                                                                 "javelin.log.storage.period";

    /** 計測データの最大蓄積期間を表す接頭辞 (共通設定) */
    private static final String          COMMON_MEASUREMENT_LOG_STORAGE_PERIOD =
                                                                                 "measurement.log.storage.period";

    /** Javelinログの最大蓄積期間を表す接頭辞 */
    private static final String          JVN_LOG_STRAGE_PERIOD                 =
                                                                                 "javelin.log.storage.period.";

    /** 計測データの最大蓄積期間を表す接頭辞 */
    private static final String          MEASUREMENT_LOG_STRAGE_PERIOD         =
                                                                                 "measurement.log.storage.period.";

    /** Javelinログを分割保存するかどうかを表す接頭辞 */
    private static final String          LOG_SPLIT                             =
                                                                                 "javelin.log.split";

    /** Javelinログを分割保存する場合の1レコード辺りの最大サイズを表す接頭辞 */
    private static final String          LOG_SPLIT_SIZE                        =
                                                                                 "javelin.log.split.size";

    /** Javelinログを分割保存する場合の閾値を表す接頭辞 */
    private static final String          LOG_SPLIT_THRESHOLD                   =
                                                                                 "javelin.log.split.threshold";

    /** データベース名で使用できる文字を、正規表現で表したもの */
    private static final String          DATABASE_NAME_USABLE_PATTERN          =
                                                                                 "[A-Za-z0-9#$%@=\\+\\-_~\\.]*";

    /** パラメータ定義ファイルのパス */
    private static String                configFilePath_;

    /** jvnログのローテート期間の最大値（月） */
    private static final int             LOG_ROTATE_MAX_PERIOD_MONTH           = 24;

    /** jvnログのローテート期間の最大値（日） */
    private static final int             LOG_ROTATE_MAX_PERIOD_DAY             = 365;

    /** jvnログのローテート期間の最大値（時間） */
    private static final int             LOG_ROTATE_MAX_PERIOD_HOUR            =
                                                                                 LOG_ROTATE_MAX_PERIOD_DAY * 24;

    //--------------------
    // SMTP settings
    //--------------------
    /** SMTPメール通知機能関連設定項目の接頭辞。 */
    private static final String          SMTP_PREFIX                           = "collector.smtp.";

    /** メール通知を送信するかどうかを指定する設定項目名。 */
    private static final String          SEND_MAIL                             = SMTP_PREFIX
                                                                                       + "sendMail";

    /** メールサーバを指定する設定項目名。 */
    private static final String          SMTP_SERVER                           = SMTP_PREFIX
                                                                                       + "server";

    /** メールのエンコーディングを指定する設定項目名。 */
    private static final String          SMTP_ENCODING                         = SMTP_PREFIX
                                                                                       + "encoding";

    /** 送信元メールアドレスを指定する設定項目名。 */
    private static final String          SMTP_FROM                             = SMTP_PREFIX
                                                                                       + "from";

    /** 送信先メールアドレスを指定する設定項目名。 */
    private static final String          SMTP_TO                               = SMTP_PREFIX + "to";

    /** メールのSubjectを指定する設定項目名。 */
    private static final String          SMTP_SUBJECT                          = SMTP_PREFIX
                                                                                       + "subject";

    /** メールテンプレートを指定する設定項目の接頭辞。 */
    public static final String           SMTP_TEMPLATE_PREFIX                  =
                                                                                 SMTP_PREFIX
                                                                                         + "template.";

    /** メールの件名テンプレートを指定する設定項目の接尾辞。 */
    private static final String          SMTP_TEMPLATE_SUBJECT_SUFFIX          = ".subject";

    /** メールの本文テンプレートを指定する設定項目の接尾辞。 */
    private static final String          SMTP_TEMPLATE_BODY_SUFFIX             = ".body";

    /** メールテンプレート(jvnアラーム用)を指定する設定項目名。 */
    private static final String          SMTP_TEMPLATE_JVN                     =
                                                                                 SMTP_TEMPLATE_PREFIX
                                                                                         + "jvn";

    /** メールテンプレート(計測値アラーム用)を指定する設定項目名。 */
    private static final String          SMTP_TEMPLATE_MEASUREMENT             =
                                                                                 SMTP_TEMPLATE_PREFIX
                                                                                         + "measurement";

    public static final String           SMTP_TEMPLATE_COLLECT_COMPLETED       =
                                                                                 SMTP_TEMPLATE_PREFIX
                                                                                         + "collectCompleted";

    //--------------------
    // SNMP settings
    //--------------------
    /** SNMP系パラメータの接頭辞 */
    public static final String           SNMP_PREFIX                           = "collector.snmp.";

    /** SNMPTrapを送信するかどうかの設定項目名 */
    public static final String           SEND_TRAP                             = SNMP_PREFIX
                                                                                       + "sendTrap";

    /** マネージャリストの設定項目名 */
    public static final String           MANAGERS                              = SNMP_PREFIX
                                                                                       + "managers";

    /** SNMP Trapポート番号の設定項目名 */
    public static final String           TRAP_PORT                             = SNMP_PREFIX
                                                                                       + "trapPort";

    /** SNMP Versionの設定項目名 */
    public static final String           VERSION                               = SNMP_PREFIX
                                                                                       + "version";

    /** Trapコミュニティ名の設定項目名 */
    public static final String           TRAP_COMMUNITY                        =
                                                                                 SNMP_PREFIX
                                                                                         + "trapCommunity";

    /** 使用言語の設定項目 */
    public static final String           LANGUAGE                              = "language";

    public static final String           BATCH_SIZE                            =
                                                                                 "insert.batch.size";

    public static final String           ITEMID_CACHE_SIZE                     =
                                                                                 "itemid.cache.size";

    /**
     * ストリームから設定ファイルを読み込み、{@link DataCollectorConfig} を構築します。<br />
     * 
     * @param inputStream 入力ストリーム
     * @return {@link DataCollectorConfig} オブジェクト
     * @throws IOException 入出力エラーが発生した場合
     * @throws InitializeException 初期化例外が発生した場合
     */
    public static DataCollectorConfig load(final InputStream inputStream)
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

        DataCollectorConfig config = new DataCollectorConfig();

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
     * 指定されたパスから設定ファイルを読み込み、{@link DataCollectorConfig} を構築します。<br />
     * 
     * @param path パス
     * @return {@link DataCollectorConfig} オブジェクト
     * @throws IOException 入出力エラーが発生した場合
     * @throws InitializeException 初期化例外が発生した場合
     */
    public static DataCollectorConfig load(final String path)
        throws IOException,
            InitializeException
    {
        if (config__ != null)
        {
            return config__;
        }

        File file = new File(path);
        configFilePath_ = file.getCanonicalPath();
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
                logger_.log(LogMessageCodes.FAIL_READ_PARAMETER_USE_DEFAULT, configFilePath_, key,
                            AgentSetting.DEF_PORT);
            }
        }
        else if (key.startsWith(ACCEPT_PORT))
        {
            try
            {
                setting.acceptPort = Integer.parseInt(value);
            }
            catch (NumberFormatException ex)
            {
                logger_.log(LogMessageCodes.FAIL_READ_PARAMETER_USE_DEFAULT, configFilePath_, key,
                            AgentSetting.DEF_ACCEPT_PORT);
            }
        }
        else if (key.startsWith(DATABASE_NAME))
        {
            // database.name.n = xxx
            if (isValidDBName(value))
            {
                setting.databaseName = value;
            }
            else
            {
                logger_.log(LogMessageCodes.FAIL_TO_READ_PARAMETER, configFilePath_, key);
                throw new InitializeException("Invalid Unit.");
            }
        }
        else if (key.startsWith(JVN_LOG_STRAGE_PERIOD))
        {
            try
            {
                setting.jvnLogStragePeriod = getStragePeriod(value);
            }
            catch (InitializeException ex)
            {
                String defaultValue =
                                      AgentSetting.DEF_PERIOD
                                              + AgentSetting.DEF_PERIOD_UNIT.toString();
                logger_.log(LogMessageCodes.FAIL_READ_PARAMETER_USE_DEFAULT, configFilePath_, key,
                            defaultValue);
            }
        }
        else if (key.startsWith(MEASUREMENT_LOG_STRAGE_PERIOD))
        {
            try
            {
                setting.measureStragePeriod = getStragePeriod(value);
            }
            catch (InitializeException ex)
            {
                String defaultValue =
                                      AgentSetting.DEF_PERIOD
                                              + AgentSetting.DEF_PERIOD_UNIT.toString();
                logger_.log(LogMessageCodes.FAIL_READ_PARAMETER_USE_DEFAULT, configFilePath_, key,
                            defaultValue);
            }
        }
    }

    /**
     * 蓄積期間に異常値がないかチェックを行います。<br />
     * 値が正常であれば、入力された文字列をそのまま返します。<br />
     * 
     * @param value 入力値
     * @return 入力値
     * @throws InitializeException パラメータの初期化に失敗した場合
     */
    private static String getStragePeriod(final String value)
        throws InitializeException
    {
        if (value == null || value.length() < 2)
        {
            throw new InitializeException("Invalid Unit.");
        }

        if (AgentSetting.NONE.equals(value))
        {
            return value;
        }
        String num = value.substring(0, value.length() - 1);
        String unit = value.substring(value.length() - 1);
        int intNum;
        try
        {
            intNum = Integer.parseInt(num);
        }
        catch (NumberFormatException ex)
        {
            throw new InitializeException(ex);
        }

        if ("h".equals(unit))
        {
            if (intNum < 1 || LOG_ROTATE_MAX_PERIOD_HOUR < intNum)
            {
                throw new InitializeException("Invalid Unit.");
            }
        }
        else if ("d".equals(unit))
        {
            if (intNum < 1 || LOG_ROTATE_MAX_PERIOD_DAY < intNum)
            {
                throw new InitializeException("Invalid Unit.");
            }
        }
        else if ("m".equals(unit))
        {
            if (intNum < 1 || LOG_ROTATE_MAX_PERIOD_MONTH < intNum)
            {
                throw new InitializeException("Invalid Unit.");
            }
        }
        else
        {
            throw new InitializeException("Invalid Unit.");
        }
        return value;
    }

    /**
     * 共通パラメータを設定します。<br />
     * 
     * @param config {@link DataCollectorConfig}オブジェクト
     * @param key キー
     * @param value 値
     * @throws InitializeException パラメータの初期化に失敗した場合
     */
    private static void setCommonValue(final DataCollectorConfig config, final String key,
            final String value)
        throws InitializeException
    {
        if (DATABASE_TYPE.equals(key))
        {
            DatabaseType databaseType = DatabaseType.fromId(value);
            if (databaseType == null)
            {
                databaseType = DatabaseType.H2;
                logger_.log(LogMessageCodes.FAIL_READ_PARAMETER_USE_DEFAULT, configFilePath_, key,
                            databaseType.toString());
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
        else if (key.equals(DATABASE_NAME))
        {
            // database.name = xxx
            if (isValidDBName(value))
            {
                config.setDatabaseName(value);
            }
            else
            {
                logger_.log(LogMessageCodes.FAIL_TO_READ_PARAMETER, configFilePath_, key);
                throw new InitializeException("Invalid Unit.");
            }
        }
        else if (DATABASE_USERNAME.equals(key))
        {
            config.setDatabaseUserName(value);
        }
        else if (DATABASE_PASSWORD.equals(key))
        {
            config.setDatabasePassword(value);
        }
        else if (REPORT_OUTPUT_DIR.equals(key))
        {
            config.setReportOutputPath(value);
        }
        else if (RESOURCE_INTERVAL.equals(key))
        {
            try
            {
                config.setResourceInterval(Integer.parseInt(value));
            }
            catch (NumberFormatException ex)
            {
                logger_.log(LogMessageCodes.FAIL_READ_PARAMETER_USE_DEFAULT, configFilePath_, key,
                            DataCollectorConfig.DEF_RESOURCE_INTERVAL);
            }
        }
        else if (RESOURCE_MONITORING.equals(key))
        {
            config.setResourceMonitoringConf(value);
        }
        else if (CONNECTION_MODE.equals(key))
        {
            config.setConnectionMode(value);
        }
        else if (SERVER_ACCEPT_HOST.equals(key))
        {
            config.setAcceptHost(value);
        }
        else if (SERVER_ACCEPT_PORT.equals(key))
        {
            try
            {
                config.setAcceptPort(Integer.parseInt(value));
            }
            catch (NumberFormatException ex)
            {
                logger_.log(LogMessageCodes.FAIL_TO_READ_PARAMETER, configFilePath_, key);
                throw new InitializeException(ex);
            }
        }
        else if (COMMON_JVN_LOG_STORAGE_PERIOD.equals(key))
        {
            config.setJvnLogStoragePeriod(value);
        }
        else if (COMMON_MEASUREMENT_LOG_STORAGE_PERIOD.equals(key))
        {
            config.setMeasurementLogStoragePeriod(value);
        }
        else if (LOG_SPLIT.equals(key))
        {
            config.setLogSplit(Boolean.parseBoolean(value));
        }
        else if (LOG_SPLIT_SIZE.equals(key))
        {
            try
            {
                config.setLogSplitSize(Integer.parseInt(value));
            }
            catch (NumberFormatException ex)
            {
                logger_.log(LogMessageCodes.FAIL_READ_PARAMETER_USE_DEFAULT, configFilePath_, key,
                            DataCollectorConfig.DEF_LOG_SPLIT_SIZE);
            }
        }
        else if (LOG_SPLIT_THRESHOLD.equals(key))
        {
            try
            {
                config.setLogSplitThreshold(Integer.parseInt(value));
            }
            catch (NumberFormatException ex)
            {
                logger_.log(LogMessageCodes.FAIL_READ_PARAMETER_USE_DEFAULT, configFilePath_, key,
                            DataCollectorConfig.DEF_LOG_SPLIT_THRESHOLD);
            }
        }
        // SMTP settings
        else if (SEND_MAIL.equals(key))
        {
            if (value.equalsIgnoreCase("true"))
            {
                config.setSendMail(true);
            }
            else if (value.equalsIgnoreCase("false"))
            {
                config.setSendMail(false);
            }
            else
            {
                logger_.log(LogMessageCodes.FAIL_READ_PARAMETER_USE_DEFAULT, configFilePath_, key,
                            DataCollectorConfig.DEF_SEND_MAIL);
            }
        }
        else if (SMTP_SERVER.equals(key))
        {
            config.setSmtpServer(value);
        }
        else if (SMTP_FROM.equals(key))
        {
            config.setSmtpFrom(value);
        }
        else if (SMTP_TO.equals(key))
        {
            config.setSmtpTo(value);
        }
        else if (SMTP_TEMPLATE_JVN.equals(key))
        {
            config.setSmtpTemplateJvn(value);
        }
        else if (SMTP_TEMPLATE_MEASUREMENT.equals(key))
        {
            config.setSmtpTemplateMeasurement(value);
        }
        else if (SMTP_ENCODING.equals(key))
        {
            config.setSmtpEncoding(value);
        }
        else if (SMTP_SUBJECT.equals(key))
        {
            config.setSmtpSubject(value);
        }
        else if (key.startsWith(SMTP_TEMPLATE_PREFIX))
        {
            // メールテンプレート設定
            if (key.endsWith(SMTP_TEMPLATE_SUBJECT_SUFFIX))
            {
                // 件名
                String name =
                              key.substring(0, key.length() - SMTP_TEMPLATE_SUBJECT_SUFFIX.length());
                MailTemplateEntity entity = getMailTemplateEntity(config, name);
                entity.subject = value;
            }
            else if (key.endsWith(SMTP_TEMPLATE_BODY_SUFFIX))
            {
                // 本文
                String name = key.substring(0, key.length() - SMTP_TEMPLATE_BODY_SUFFIX.length());
                MailTemplateEntity entity = getMailTemplateEntity(config, name);
                try
                {
                    entity.body = readTemplate(value);
                }
                catch (IOException ex)
                {
                    logger_.log(LogMessageCodes.FAIL_READ_MAIL_TEMPLATE, value);
                }
            }
        }
        // SNMP settings
        else if (SEND_TRAP.equals(key))
        {
            if (value.equalsIgnoreCase("true"))
            {
                config.setSendTrap(true);
            }
            else if (value.equalsIgnoreCase("false"))
            {
                config.setSendTrap(true);
            }
            else
            {
                logger_.log(LogMessageCodes.FAIL_READ_PARAMETER_USE_DEFAULT, configFilePath_, key,
                            DataCollectorConfig.DEF_SEND_TRAP);
            }
        }
        else if (MANAGERS.equals(key))
        {
            config.setManagers(value);
        }
        else if (TRAP_PORT.equals(key))
        {
            try
            {
                config.setTrapPort(Integer.parseInt(value));
            }
            catch (NumberFormatException ex)
            {
                logger_.log(LogMessageCodes.FAIL_READ_PARAMETER_USE_DEFAULT, configFilePath_, key,
                            DataCollectorConfig.DEF_TRAP_PORT);
            }
        }
        else if (TRAP_COMMUNITY.equals(key))
        {
            config.setTrapCommunity(value);
        }
        else if (VERSION.equals(key))
        {
            config.setVersion(value);
        }
        else if (LANGUAGE.equals(key))
        {
            config.setLanguage(value);
        }
        else if (BATCH_SIZE.equals(key))
        {
            try
            {
                config.setBatchSize(Integer.parseInt(value));
            }
            catch (NumberFormatException ex)
            {
                logger_.log(LogMessageCodes.FAIL_READ_PARAMETER_USE_DEFAULT, configFilePath_, key,
                            DataCollectorConfig.DEF_BATCH_SIZE);
            }

        }
        else if (ITEMID_CACHE_SIZE.equals(key))
        {
            try
            {
                config.setItemIdCacheSize(Integer.parseInt(value));
            }
            catch (NumberFormatException ex)
            {
                logger_.log(LogMessageCodes.FAIL_READ_PARAMETER_USE_DEFAULT, configFilePath_, key,
                            DataCollectorConfig.DEF_CACHE_SIZE);
            }

        }

    }

    /**
     * 指定されたメールテンプレート項目の設定オブジェクトを取得します。<br />
     * オブジェクトが存在しない場合は生成します。
     *
     * @param config {@link DataCollectorConfig} オブジェクト
     * @param name メールテンプレート項目名（ SUFFIX を除いた部分）
     * @return 設定オブジェクト
     */
    private static MailTemplateEntity getMailTemplateEntity(final DataCollectorConfig config,
            final String name)
    {
        MailTemplateEntity entity = config.getSmtpTemplate(name);
        if (entity == null)
        {
            entity = new MailTemplateEntity();
            config.setSmtpTemplate(name, entity);
        }
        return entity;
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
        return configFilePath_;
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

    /**
     * ファイルを全て読み込み、単一のStringオブジェクトとして返す。 各改行はStringオブジェクト中に挿入される。
     * 
     * @param filePath 読み込むテンプレートのパス。
     * @return 読み込んだ文字列。
     * @throws IOException ファイルの読み込みに失敗した場合。
     */
    private static String readTemplate(final String filePath)
        throws IOException
    {
        StringBuilder template = new StringBuilder();
        FileReader fileReader = new FileReader(filePath);
        BufferedReader br = new BufferedReader(fileReader);

        try
        {
            while (true)
            {
                String line = br.readLine();
                if (line == null)
                {
                    break;
                }

                template.append(line);
                template.append(LS);
            }
        }
        finally
        {
            br.close();
        }

        return template.toString();
    }
}
