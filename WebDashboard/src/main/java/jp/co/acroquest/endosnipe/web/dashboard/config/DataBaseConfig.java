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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.co.acroquest.endosnipe.data.db.DatabaseType;

/**
 * ENdoSnipe DataBase の設定/定数を保持するクラスです。<br />
 * 
 * @author fujii
 */
public class DataBaseConfig
{
    /** Javelinログを分割するかどうか */
    private boolean                               isLogSplit_                   = DEF_IS_LOG_SPLIT;

    /** Javelinログを分割保存する場合の１レコードあたりの最大サイズ */
    private int                                   logSplitSize_                 =
                                                                                  DEF_LOG_SPLIT_SIZE;

    /** Javelinログを分割する場合の閾値 */
    private int                                   logSplitThreshold_            =
                                                                                  DEF_LOG_SPLIT_THRESHOLD;

    /** Javelinログ最大蓄積件数のキー */
    public static final String                    JAVELIN_LOG_MAX_KEY           =
                                                                                  "javelin.log.max.record";

    /** Javelinログ最大蓄積件数のデフォルト値 */
    public static final int                       DEF_JAVELIN_LOG_MAX           = 260000;

    /** Javelinログ最大蓄積件数の最小値 */
    public static final int                       MIN_JAVELIN_LOG_MAX           = 1;

    /** 計測データ最大蓄積件数のキー */
    public static final String                    MEASUREMENT_LOG_MAX_KEY       =
                                                                                  "measurement.log.max.record";

    /** 計測データ最大蓄積件数のデフォルト値 */
    public static final int                       DEF_MEASUREMENT_LOG_MAX       = 870000;

    /** 計測データ最大蓄積件数の最小値 */
    public static final int                       MIN_MEASUREMENT_LOG_MAX       = 1;

    /** データベースの種類 */
    private DatabaseType                          databaseType_                 = DEF_DATABASE_TYPE;

    /** データベースディレクトリ */
    private String                                baseDir_                      = DEF_DATABASE_DIR;

    /** データベースのホストアドレス */
    private String                                databaseHost_                 = DEF_DATABASE_HOST;

    /** データベースのポート番号 */
    private String                                databasePort_                 = DEF_DATABASE_PORT;
    
    /** データベース名。 */
    private String                                databaseName_                 = DEF_DATABASE_NAME;

    /** データベースログインユーザ名 */
    private String                                databaseUserName_             = DEF_DATABASE_USER;

    /** データベースログインパスワード */
    private String                                databasePassword_             =
                                                                                  DEF_DATABASE_PASSWORD;

    /** リソースモニタリングの設定ファイル名 */
    private String                                resourceMonitoringConf_       =
                                                                                  DEF_RESOURCE_MONITORING_CONF;

    /** 接続モード */
    private String                                connectionMode_               = DEF_CONNECTION_MODE;
    
    /** サーバモードの設定 */
    private AgentSetting                          serverModeAgentSetting_       =
                                                                                  new AgentSetting();

    private final List<AgentSetting>              agentSttingList_              =
                                                                                  new ArrayList<AgentSetting>();

    /** データベースの種類のデフォルト値 */
    private static final DatabaseType             DEF_DATABASE_TYPE             = DatabaseType.H2;

    /** データベース保存先ベースディレクトリのデフォルト値 */
    private static final String                   DEF_DATABASE_DIR              = "../data";

    /** データベースのホストアドレスのデフォルト値 */
    private static final String                   DEF_DATABASE_HOST             = "localhost";

    /** データベースのポート番号のデフォルト値 */
    private static final String                   DEF_DATABASE_PORT             = "5432";
    
    /** データベース名。 */
    private static final String                   DEF_DATABASE_NAME             = null;

    /** データベースログインユーザ名のデフォルト値 */
    private static final String                   DEF_DATABASE_USER             = "";

    /** データベースログインパスワードのデフォルト値 */
    private static final String                   DEF_DATABASE_PASSWORD         = "";

    /** Javelinログ分割保存を行うかどうかのデフォルト値 */
    private static final boolean                  DEF_IS_LOG_SPLIT              = false;

    /** Javelinログを分割保存する場合の1レコード辺りの最大サイズのデフォルト値 */
    private static final int                      DEF_LOG_SPLIT_SIZE            = 300;

    /** Javelinログを分割保存する場合の閾値のデフォルト値 */
    private static final int                      DEF_LOG_SPLIT_THRESHOLD       = 1024;

    /** サーバのリソース間隔（共通） */
    private long                                  resourceInterval_             =
                                                                                  DEF_RESOURCE_INTERVAL;

    /** リソース取得間隔のデフォルト値(ミリ秒) */
    private static final int                      DEF_RESOURCE_INTERVAL         = 5000;

    /** リソースモニタリングの設定ファイル名のデフォルト値 */
    private static final String                   DEF_RESOURCE_MONITORING_CONF  =
                                                                                  "../conf/resource_monitoring.conf";

    /** 接続モードのデフォルト値 */
    private static final String                   DEF_CONNECTION_MODE           = "client";

    /** datacollector(server mode)のホスト名のデフォルト値 */
    private static final String                   DEF_SERVER_MODE_ACCEPT_HOST   = "localhost";

    /** datacollector(server mode)のポート名のデフォルト値 */
    private static final String                   DEF_SERVER_MODE_ACCEPT_PORT   = "19000";

    //--------------------
    // SMTP settings(default)
    //--------------------
    /** メール通知を送信するかどうかのデフォルト値。 */
    private static final boolean                  DEF_SEND_MAIL                 = true;

    /** メールサーバのデフォルト値。 */
    private static final String                   DEF_SMTP_SERVER               =
                                                                                  "mail.example.com";

    /** メールのエンコーディング設定デフォルト値。 */
    private static final String                   DEF_SMTP_ENCODING             = "iso-2022-jp";

    /** 送信元メールアドレス設定デフォルト値。 */
    private static final String                   DEF_SMTP_FROM                 =
                                                                                  "endosnipe@example.com";

    /** 送信先メールアドレス設定デフォルト値。 */
    private static final String                   DEF_SMTP_TO                   =
                                                                                  "endosnipe@example.com";

    /** メールSubjectのデフォルト値。 */
    private static final String                   DEF_SMTP_SUBJECT              =
                                                                                  "[javelin] ${eventName} is ocurred.";

    /** メールテンプレート(jvnアラーム用)のデフォルト値。 */
    private static final String                   DEF_SMTP_TEMPLATE_JVN         =
                                                                                  "mai_template_jvn.txt";

    /** メールテンプレート(計測値アラーム用)のデフォルト値。 */
    private static final String                   DEF_SMTP_TEMPLATE_MEASUREMENT =
                                                                                  "mai_template_measurement.txt";

    //--------------------
    // SMTP settings
    //--------------------
    /** メール通知を送信するかどうか */
    private boolean                               sendMail_                     = DEF_SEND_MAIL;

    /** メールサーバ */
    private String                                smtpServer_                   = DEF_SMTP_SERVER;

    /** メールのエンコーディング */
    private String                                smtpEncoding_                 = DEF_SMTP_ENCODING;

    /** 送信元メールアドレス */
    private String                                smtpFrom_                     = DEF_SMTP_FROM;

    /** 送信先メールアドレス */
    private String                                smtpTo_                       = DEF_SMTP_TO;

    /** メールSubject */
    private String                                smtpSubject_                  = DEF_SMTP_SUBJECT;

    /** メールテンプレート(jvnアラーム用) */
    private String                                smtpTemplateJvn_              =
                                                                                  DEF_SMTP_TEMPLATE_JVN;

    /** メールテンプレート(計測値アラーム用) */
    private String                                smtpTemplateMeasurement_      =
                                                                                  DEF_SMTP_TEMPLATE_MEASUREMENT;

    /** メールテンプレート（キー：設定項目名、値：テンプレートファイル名） */
    private final Map<String, MailTemplateEntity> smtpTemplateMap_              =
                                                                                  new HashMap<String, MailTemplateEntity>();

    //--------------------
    // SNMP settings(default)
    //--------------------
    /** SNMPTrapを送信するかどうかのデフォルト値true(=送信する) */
    private static final boolean                  DEF_SEND_TRAP                 = true;

    /** マネージャリストのデフォルト値localhost */
    private static final String                   DEF_MANAGERS                  = "localhost";

    /** SNMP Trapポート番号のデフォルト値162 */
    private static final int                      DEF_TRAP_PORT                 = 162;

    /** Trapコミュニティ名のデフォルト値public */
    private static final String                   DEF_TRAP_COMMUNITY            = "public";

    /** SNMP Version: v1 */
    public static final String                    VERSION_V1                    = "v1";

    /** SNMP Version: v2c */
    public static final String                    VERSION_V2C                   = "v2c";

    /** SNMP Version: v3 */
    public static final String                    VERSION_V3                    = "v3";

    /** SNMP Versionのデフォルト値v2c */
    private static final String                   DEF_VERSION                   = VERSION_V2C;

    //--------------------
    // SNMP settings
    //--------------------
    /** SNMPTrapを送信するかどうか。true:送信する、false:送信しない */
    private boolean                               sendTrap_                     = DEF_SEND_TRAP;

    /** マネージャリスト */
    private String                                managers_                     = DEF_MANAGERS;

    /** SNMP Trapポート番号 */
    private int                                   trapPort_                     = DEF_TRAP_PORT;

    /** SNMP Version */
    private String                                version_                      = DEF_VERSION;

    /** Trapコミュニティ名 */
    private String                                trapCommunity_                =
                                                                                  DEF_TRAP_COMMUNITY;

    /**
     * データベースの種類を返します。<br />
     *
     * @return データベースの種類
     */
    public DatabaseType getDatabaseType()
    {
        return this.databaseType_;
    }

    /**
     * データベースの種類を設定します。<br />
     *
     * @param type データベースの種類
     */
    public void setDatabaseType(final DatabaseType type)
    {
        this.databaseType_ = type;
    }

    /**
     * データベースディレクトリを返します。<br />
     * 
     * @return データベースディレクトリ
     */
    public String getBaseDir()
    {
        return baseDir_;
    }

    /**
     * データベースディレクトリを設定します。<br />
     * 
     * @param baseDir データベースディレクトリ
     */
    public void setBaseDir(final String baseDir)
    {
        baseDir_ = baseDir;
    }

    /**
     * データベースのホストアドレスを返します。<br />
     *
     * @return ホストアドレスまたはホスト名
     */
    public String getDatabaseHost()
    {
        return this.databaseHost_;
    }

    /**
     * データベースのホストアドレスを設定します。<br />
     *
     * @param host ホストアドレス（ホスト名でも可）
     */
    public void setDatabaseHost(final String host)
    {
        this.databaseHost_ = host;
    }

    /**
     * データベースのポート番号を返します。<br />
     *
     * @return ポート番号
     */
    public String getDatabasePort()
    {
        return this.databasePort_;
    }

    /**
     * データベースのポート番号を設定します。<br />
     *
     * @param port ポート番号
     */
    public void setDatabasePort(final String port)
    {
        this.databasePort_ = port;
    }

    /**
     * データベース名を取得します。
     * 
	 * @return データベース名。
	 */
	public String getDatabaseName()
	{
		return databaseName_;
	}

	/**
	 * データベース名を設定します。
	 * 
	 * @param databaseName データベース名。
	 */
	public void setDatabaseName(String databaseName)
	{
		this.databaseName_ = databaseName;
	}

	/**
     * データベースログインユーザ名を返します。<br />
     *
     * @return ユーザ名
     */
    public String getDatabaseUserName()
    {
        return this.databaseUserName_;
    }

    /**
     * データベースログインユーザ名を設定します。<br />
     *
     * @param user ユーザ名
     */
    public void setDatabaseUserName(final String user)
    {
        this.databaseUserName_ = user;
    }

    /**
     * データベースログインパスワードを返します。<br />
     *
     * @return パスワード
     */
    public String getDatabasePassword()
    {
        return this.databasePassword_;
    }

    /**
     * データベースログインパスワードを設定します。<br />
     *
     * @param password パスワード
     */
    public void setDatabasePassword(final String password)
    {
        this.databasePassword_ = password;
    }

    /**
     * リソース取得間隔を設定します。<br />
     *
     * @param interval リソース取得間隔
     */
    public void setResourceInterval(final long interval)
    {
        this.resourceInterval_ = interval;
    }

    /**
     * リソース取得間隔を返します。<br />
     *
     * @return Interval リソース取得間隔
     */
    public long getResourceInterval()
    {
        return this.resourceInterval_;
    }

    /**
     * {@link AgentSetting} を追加します。<br />
     * 
     * @param agentSetting {@link AgentSetting} オブジェクト
     */
    public void addAgentSetting(final AgentSetting agentSetting)
    {
        agentSttingList_.add(agentSetting);
    }

    /**
     * {@link AgentSetting} のリストを返します。<br />
     * 
     * @return {@link AgentSetting} のリスト
     */
    public List<AgentSetting> getAgentSettingList()
    {
        return Collections.unmodifiableList(agentSttingList_);
    }

    /**
     * Javelinログを分割するかどうか。<br />
     * 
     * @return Javelinログを分割する場合、<code>true</code>
     */
    public boolean isLogSplit()
    {
        return this.isLogSplit_;
    }

    /**
     * Javelinログを分割するかどうかを設定します。<br />
     * 
     * @param isLogSplit Javelinログを分割する場合、<code>true</code>
     */
    public void setLogSplit(final boolean isLogSplit)
    {
        this.isLogSplit_ = isLogSplit;
    }

    /**
     * Javelinログを分割保存する場合の1レコード当たりの最大サイズを返します。<br />
     * 
     * @return Javelinログを分割保存する場合の1レコード当たりの最大サイズ
     */
    public int getLogSplitSize()
    {
        return this.logSplitSize_;
    }

    /**
     * Javelinログを分割保存する場合の1レコード当たりの最大サイズを設定します。<br />
     * 
     * @param logSplitSize Javelinログを分割保存する場合の1レコード当たりの最大サイズ
     */
    public void setLogSplitSize(final int logSplitSize)
    {
        this.logSplitSize_ = logSplitSize;
    }

    /**
     * Javelinログを分割保存する場合の閾値を返します。<br />
     * 
     * @return Javelinログを分割保存する場合の閾値
     */
    public int getLogSplitThreshold()
    {
        return this.logSplitThreshold_;
    }

    /**
     * Javelinログを分割保存する場合の閾値を設定します。<br />
     * 
     * @param logSplitThreshold Javelinログを分割保存する場合の閾値
     */
    public void setLogSplitThreshold(final int logSplitThreshold)
    {
        this.logSplitThreshold_ = logSplitThreshold;
    }

    /**
     * @param resourceMonitoringConf セットする resourceMonitoringConf
     */
    public void setResourceMonitoringConf(final String resourceMonitoringConf)
    {
        resourceMonitoringConf_ = resourceMonitoringConf;
    }

    /**
     * @return resourceMonitoringConf
     */
    public String getResourceMonitoringConf()
    {
        return resourceMonitoringConf_;
    }

    /**
     * メール通知を送信するかどうかを取得する。
     * 
     * @return メール通知を送信するかどうかの設定
     */
    public boolean isSendMail()
    {
        return sendMail_;
    }

    /**
     *　メール通知を送信するかどうかを設定する。
     * 
     * @param sendMail メール通知を送信するかどうかの設定
     */
    public void setSendMail(final boolean sendMail)
    {
        sendMail_ = sendMail;
    }

    /**
     * メールのエンコーディングを取得する。
     * 
     * @return メールのエンコーディングの設定
     */
    public String getSmtpEncoding()
    {
        return smtpEncoding_;
    }

    /**
     *　メールのエンコーディングを設定する。
     * 
     * @param smtpEncoding メールのエンコーディングの設定
     */
    public void setSmtpEncoding(final String smtpEncoding)
    {
        smtpEncoding_ = smtpEncoding;
    }

    /**
     * メールサーバを取得する。
     * 
     * @return メールサーバの設定
     */
    public String getSmtpServer()
    {
        return smtpServer_;
    }

    /**
     *　メールサーバを設定する。
     * 
     * @param smtpServer メールサーバの設定
     */
    public void setSmtpServer(final String smtpServer)
    {
        smtpServer_ = smtpServer;
    }

    /**
     * 送信元メールアドレスを取得する。
     * 
     * @return 送信元メールアドレスの設定
     */
    public String getSmtpFrom()
    {
        return smtpFrom_;
    }

    /**
     *　送信元メールアドレスを設定する。
     * 
     * @param smtpFrom 送信元メールアドレスの設定
     */
    public void setSmtpFrom(final String smtpFrom)
    {
        smtpFrom_ = smtpFrom;
    }

    /**
     * 送信先メールアドレスを取得する。
     * 
     * @return 送信先メールアドレスの設定
     */
    public String getSmtpTo()
    {
        return smtpTo_;
    }

    /**
     *　送信先メールアドレスを設定する。
     * 
     * @param smtpTo 送信先メールアドレスの設定
     */
    public void setSmtpTo(final String smtpTo)
    {
        smtpTo_ = smtpTo;
    }

    /**
     * メールテンプレート(jvnアラーム用)を取得する。
     * 
     * @return メールテンプレート(jvnアラーム用)の設定
     */
    public String getSmtpTemplateJvn()
    {
        return smtpTemplateJvn_;
    }

    /**
     *　メールテンプレート(jvnアラーム用)を設定する。
     * 
     * @param smtpTemplateJvn メールテンプレート(jvnアラーム用)の設定
     */
    public void setSmtpTemplateJvn(final String smtpTemplateJvn)
    {
        smtpTemplateJvn_ = smtpTemplateJvn;
    }

    /**
     * メールテンプレート(計測値アラーム用)を取得する。
     * 
     * @return メールテンプレート(計測値アラーム用)の設定
     */
    public String getSmtpTemplateMeasurement()
    {
        return smtpTemplateMeasurement_;
    }

    /**
     *　メールテンプレート(計測値アラーム用)を設定する。
     * 
     * @param smtpTemplateMeasurement メールテンプレート(計測値アラーム用)の設定
     */
    public void setSmtpTemplateMeasurement(final String smtpTemplateMeasurement)
    {
        smtpTemplateMeasurement_ = smtpTemplateMeasurement;
    }

    /**
     * メールテンプレートファイル名を取得する。
     *
     * @param name テンプレート名
     * @return テンプレートファイル名
     */
    public MailTemplateEntity getSmtpTemplate(final String name)
    {
        if (name == null)
        {
            return null;
        }
        return this.smtpTemplateMap_.get(name);
    }

    /**
     * メールテンプレートファイル名を設定する。
     *
     * @param name テンプレート名
     * @param template テンプレートファイル名
     */
    public void setSmtpTemplate(final String name, final MailTemplateEntity template)
    {
        this.smtpTemplateMap_.put(name, template);
    }

    /**
     * メールSubjectを取得する。
     * 
     * @return メールSubjectの設定
     */
    public String getSmtpSubject()
    {
        return smtpSubject_;
    }

    /**
     *　メールSubjectを設定する。
     * 
     * @param smtpSubject メールSubjectの設定
     */
    public void setSmtpSubject(final String smtpSubject)
    {
        smtpSubject_ = smtpSubject;
    }

    /**
     * SNMPTrapを送信するかどうかを取得する。
     * 
     * @return SNMPTrapを送信するかどうか
     */
    public boolean isSendTrap()
    {
        return sendTrap_;
    }

    /**
     *　SNMPTrapを送信するかどうかを設定する。
     * 
     * @param sendTrap SNMPTrapを送信するかどうか
     */
    public void setSendTrap(final boolean sendTrap)
    {
        sendTrap_ = sendTrap;
    }

    /**
     * マネージャリストを取得する。
     * 
     * @return マネージャリスト
     */
    public String getManagers()
    {
        return managers_;
    }

    /**
     *　マネージャリストを設定する。
     * 
     * @param managers マネージャリスト
     */
    public void setManagers(final String managers)
    {
        managers_ = managers;
    }

    /**
     * SNMP Trapポート番号を取得する。
     * 
     * @return SNMP Trapポート番号
     */
    public int getTrapPort()
    {
        return trapPort_;
    }

    /**
     *　SNMP Trapポート番号を設定する。
     * 
     * @param trapPort SNMP Trapポート番号
     */
    public void setTrapPort(final int trapPort)
    {
        trapPort_ = trapPort;
    }

    /**
     * Trapコミュニティ名を取得する。
     * 
     * @return Trapコミュニティ名
     */
    public String getTrapCommunity()
    {
        return trapCommunity_;
    }

    /**
     *　Trapコミュニティ名を設定する。
     * 
     * @param trapCommunity Trapコミュニティ名
     */
    public void setTrapCommunity(final String trapCommunity)
    {
        trapCommunity_ = trapCommunity;
    }

    /**
     * SNMP Versionを取得する。
     * 
     * @return SNMP Version
     */
    public String getVersion()
    {
        return version_;
    }

    /**
     *　SNMP Versionを設定する。
     * 
     * @param version SNMP Version
     */
    public void setVersion(final String version)
    {
        version_ = version;
    }

    /**
     * @return connectionMode
     */
    public String getConnectionMode()
    {
        return connectionMode_;
    }

    /**
     * @param connectionMode セットする connectionMode
     */
    public void setConnectionMode(String connectionMode)
    {
        connectionMode_ = connectionMode;
    }

    /**
     * @return serverModeAgentSetting
     */
    public AgentSetting getServerModeAgentSetting()
    {
        return serverModeAgentSetting_;
    }

    /**
     * @param serverModeAgentSetting セットする serverModeAgentSetting
     */
    public void setServerModeAgentSetting(AgentSetting serverModeAgentSetting)
    {
        serverModeAgentSetting_ = serverModeAgentSetting;
    }

}
