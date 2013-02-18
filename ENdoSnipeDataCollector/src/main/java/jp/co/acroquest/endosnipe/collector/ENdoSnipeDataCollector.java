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
package jp.co.acroquest.endosnipe.collector;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import jp.co.acroquest.endosnipe.collector.config.AgentSetting;
import jp.co.acroquest.endosnipe.collector.config.DataCollectorConfig;
import jp.co.acroquest.endosnipe.collector.config.DisplayNameManager;
import jp.co.acroquest.endosnipe.collector.config.RotateConfig;
import jp.co.acroquest.endosnipe.collector.exception.InitializeException;
import jp.co.acroquest.endosnipe.collector.listener.TelegramNotifyListener;
import jp.co.acroquest.endosnipe.collector.request.CommunicationClientRepository;
import jp.co.acroquest.endosnipe.collector.rotate.LogRotator;
import jp.co.acroquest.endosnipe.common.config.JavelinConfig;
import jp.co.acroquest.endosnipe.common.logger.ENdoSnipeLogger;
import jp.co.acroquest.endosnipe.communicator.TelegramSender;
import jp.co.acroquest.endosnipe.communicator.accessor.ResourceNotifyAccessor;
import jp.co.acroquest.endosnipe.communicator.accessor.SystemResourceGetter;
import jp.co.acroquest.endosnipe.communicator.entity.Telegram;
import jp.co.acroquest.endosnipe.communicator.impl.DataCollectorClient;
import jp.co.acroquest.endosnipe.data.db.ConnectionManager;
import jp.co.acroquest.endosnipe.data.db.DBManager;
import jp.co.acroquest.endosnipe.data.db.DatabaseType;

/**
 * ENdoSnipe DataCollector のメインクラスです。<br />
 * 
 * @author y-komori
 */
public class ENdoSnipeDataCollector implements CommunicationClientRepository, LogMessageCodes
{
    private static final ENdoSnipeLogger                    LOGGER                      =
                                                                                          ENdoSnipeLogger.getLogger(ENdoSnipeDataCollector.class,
                                                                                                                    ENdoSnipeDataCollectorPluginProvider.INSTANCE);

    /** JavelinDataLogger のスレッド名称 */
    private static final String                             LOGGER_THREAD_NAME          =
                                                                                          "JavelinDataLoggerThread";

    /** ローテート用スレッドの名称 */
    private static final String                             ROTATE_THREAD_NAME          =
                                                                                          "JavelinDataCollectorRotateThread";

    private static final String                             RESOURCE_GETTER_THREAD_NAME =
                                                                                          "ResourceGetterThread";

    private DataCollectorConfig                             config_;

    /** ローテート用設定リスト */
    private final List<RotateConfig>                        rotateConfigList_           =
                                                                                          new ArrayList<RotateConfig>();

    /** デフォルトのローテート設定 */
    private RotateConfig                                    defaultRotateConfig_;

    /** ログローテートタスク */
    private LogRotator                                      logRotator_;

    /** ログローテート用スレッド */
    private Thread                                          rotateThread_;

    private volatile Thread                                 javelinDataLoggerThread_;

    private JavelinDataLogger                               javelinDataLogger_;

    /** DB名をキーにした、クライアントに通知するためのリスナのリスト */
    private final Map<String, List<TelegramNotifyListener>> telegramNotifyListenersMap_ =
                                                                                          new HashMap<String, List<TelegramNotifyListener>>();

    private final List<JavelinClient>                       clientList_                 =
                                                                                          new ArrayList<JavelinClient>();

    private volatile boolean                                isRunning_;

    /** リソースを取得するスレッド */
    private Timer                                           timer_;

    /** リソースを取得するタイマータスク */
    private SystemResourceGetter                            resourceGetterTask_;

    /** サービスモードかどうか */
    private BehaviorMode                                    behaviorMode_               =
                                                                                          BehaviorMode.SERVICE_MODE;

    /** Javelinからの接続を待ち受けるサーバインスタンス */
    private JavelinServer                                   server_;

    /**
     * {@link ENdoSnipeDataCollector} の設定を行います。<br />
     * 
     * @param config 設定オブジェクト
     */
    public void init(final DataCollectorConfig config)
    {
        this.config_ = config;
    }

    /**
     * クライアントに通知するためのリスナを登録します。
     * 
     * @param dbName DB名
     * @param notifyListener クライアントに通知するためのリスナ
     */
    public void addTelegramNotifyListener(final String dbName,
            final TelegramNotifyListener notifyListener)
    {
        List<TelegramNotifyListener> telegramNotifyListeners =
                                                               this.telegramNotifyListenersMap_.get(dbName);
        if (telegramNotifyListeners == null)
        {
            telegramNotifyListeners = new ArrayList<TelegramNotifyListener>();
            this.telegramNotifyListenersMap_.put(dbName, telegramNotifyListeners);
        }
        telegramNotifyListeners.add(notifyListener);
    }

    /**
     * プラグインモードで DataCollector を実行します。
     */
    public void startPluginMode()
    {
        start(BehaviorMode.PLUGIN_MODE);
    }

    /**
     * サービスモードで DataCollector を実行します。
     */
    public void startService()
    {
        start(BehaviorMode.SERVICE_MODE);
    }

    /**
     * {@link ENdoSnipeDataCollector} を開始します。<br />
     * ログローテートを行う場合は、{@link #addRotateConfig(RotateConfig)}を用いて
     * ローテート用の設定を追加した後に本メソッドを呼び出してください。
     * 
     * @param behaviorMode サービスモードの場合は <code>true</code> 、プラグインモードの場合は
     *            <code>false</code>
     */
    public synchronized void start(final BehaviorMode behaviorMode)
    {

        LOGGER.log(ENDOSNIPE_DATA_COLLECTOR_STARTING, behaviorMode);

        JavelinConfig javelinConfig = new JavelinConfig();
        javelinConfig.setItemNamePrefix("");
        javelinConfig.setClusterName("");

        this.behaviorMode_ = behaviorMode;
        if (config_ != null)
        {
            String baseDir = config_.getBaseDir();
            boolean useDefaultDatabase = true;
            if (config_.getDatabaseType() != DatabaseType.H2)
            {
                useDefaultDatabase = false;
            }
            String databaseHost = config_.getDatabaseHost();
            String databasePort = config_.getDatabasePort();
            String databaseName = config_.getDatabaseName();
            String databaseUserName = config_.getDatabaseUserName();
            String databasePassword = config_.getDatabasePassword();
            DBManager.updateSettings(useDefaultDatabase, baseDir, databaseHost, databasePort,
                                     databaseName, databaseUserName, databasePassword);
            ConnectionManager.getInstance().setBaseDir(baseDir);
            LOGGER.log(DATABASE_BASE_DIR, baseDir);
            if (useDefaultDatabase == false)
            {
                LOGGER.log(DATABASE_PARAMETER, databaseHost, databasePort, databaseUserName);
            }
        }

        // JavelinDataLogger の開始
        if (javelinDataLogger_ != null)
        {
            javelinDataLogger_.stop();
        }
        javelinDataLogger_ = new JavelinDataLogger(config_, this);
        javelinDataLogger_.init(this.rotateConfigList_);
        if (defaultRotateConfig_ != null)
        {
            javelinDataLogger_.setDefaultRotateConfig(defaultRotateConfig_);
        }
        javelinDataLoggerThread_ =
                                   new Thread(javelinDataLogger_, LOGGER_THREAD_NAME + "_"
                                           + Integer.toHexString(System.identityHashCode(this)));

        if (behaviorMode == BehaviorMode.SERVICE_MODE)
        {
            javelinDataLoggerThread_.setDaemon(true);

            // BottleneckEyeへ通知する表示名の変換マップを登録
            ResourceNotifyAccessor.setConvMap(DisplayNameManager.getManager().getConvMap());
        }

        javelinDataLoggerThread_.start();

        long resourceRequestInterval = config_.getResourceInterval();
        startTimer(resourceRequestInterval, behaviorMode);

        if (behaviorMode == BehaviorMode.PLUGIN_MODE)
        {
            startLogRotate(false);
        }
        isRunning_ = true;
        LOGGER.log(ENDOSNIPE_DATA_COLLECTOR_STARTED);
    }

    /**
     * DataCollectorが終了するまで、スレッドをブロックします。<br />
     */
    public void blockTillStop()
    {
        if (javelinDataLoggerThread_ != null)
        {
            try
            {
                javelinDataLoggerThread_.join();
            }
            catch (InterruptedException ex)
            {
                // Do nothing.
            }
        }
    }

    /**
     * システムリソース情報取得のタイマータスクを、interval(単位:ms)ごとに実行する。
     * 既にタイマータスクがあれば、キャンセルした後新たにタイマータスクを作成する。
     * 
     * @param interval システムリソース情報取得の間隔(単位:ms)
     * @param behaviorMode {@link BehaviorMode.SERVICE_MODE} の場合、スレッドをデーモン化する。
     */
    private void startTimer(final long interval, final BehaviorMode behaviorMode)
    {
        if (this.resourceGetterTask_ != null)
        {
            this.resourceGetterTask_.cancel();
        }

        if (this.timer_ != null)
        {
            this.timer_.purge();
        }

        boolean daemon = (behaviorMode == BehaviorMode.SERVICE_MODE);
        this.timer_ = new Timer(RESOURCE_GETTER_THREAD_NAME, daemon);
        this.resourceGetterTask_ = new SystemResourceGetter();
        this.resourceGetterTask_.setMinimumInterval(interval / 2);
        Date currentDate = new Date();
        long currentTime = currentDate.getTime();
        Date firstDate = new Date(currentTime - (currentTime % 1000) + interval);
        this.timer_.scheduleAtFixedRate(this.resourceGetterTask_, firstDate, interval);
    }

    private synchronized void stopTimer()
    {
        // システムリソース取得スレッドの停止
        if (this.resourceGetterTask_ != null)
        {
            this.resourceGetterTask_.cancel();
        }

        // システムリソース取得用タイマーの停止
        this.timer_.cancel();
    }

    /**
     * ログローテート用スレッドを開始する。
     * 
     * @param daemon <code>true</code> の場合、スレッドをデーモン化する。
     */
    private void startLogRotate(final boolean daemon)
    {
        if (DBManager.isDefaultDb() == false)
        {
            return;
        }

        // H2を使用しているときのみ、ローテート用スレッドを開始する
        this.logRotator_ = new LogRotator();
        this.logRotator_.setConfig(this.rotateConfigList_);

        this.rotateThread_ =
                             new Thread(this.logRotator_, ROTATE_THREAD_NAME + "_"
                                     + Integer.toHexString(System.identityHashCode(this)));
        if (daemon == true)
        {
            this.rotateThread_.setDaemon(true);
        }
        this.rotateThread_.start();
    }

    /**
     * {@link ENdoSnipeDataCollector} を終了します。<br />
     */
    public synchronized void stop()
    {
        if (isRunning() == false)
        {
            return;
        }
        LOGGER.log(ENDOSNIPE_DATA_COLLECTOR_STOPPING);

        // ログローテート機能の終了
        if (this.logRotator_ != null)
        {
            this.logRotator_.stop();
            this.rotateThread_.interrupt();
        }
        this.rotateConfigList_.clear();

        // リソース取得タイマの終了
        stopTimer();

        // JavelinClient の終了
        disconnectAll();

        // JavelinDataLogger の終了
        javelinDataLogger_.stop();

        LOGGER.log(ENDOSNIPE_DATA_COLLECTOR_STOPPED);
    }

    /**
     * クライアントからの接続を待ち受けるサーバを開始する。
     * @throws InitializeException 初期化に失敗
     */
    public void startServer()
        throws InitializeException
    {
        startServer(config_.getAcceptPort());

        AgentSetting agentSetting = new AgentSetting();
        agentSetting.jvnLogStragePeriod = config_.getJvnLogStoragePeriod();
        agentSetting.measureStragePeriod = config_.getMeasurementLogStoragePeriod();
        RotateConfig rotateConfig = null;
        rotateConfig = createRotateConfig(agentSetting);
        setDefaultRotateConfig(rotateConfig);
        startLogRotate(true);
    }

    /**
     * ポート番号を指定してクライアントからの接続を待ち受けるサーバを開始する。
     * @param port 待ち受けポート番号
     */
    public void startServer(final int port)
    {
        checkRunning();

        server_ = new JavelinServer();
        JavelinDataQueue queue = this.javelinDataLogger_.getQueue();
        server_.setTelegramNotifyListener(this.telegramNotifyListenersMap_);
        server_.start(port, queue, config_.getDatabaseName(), resourceGetterTask_, behaviorMode_);
    }

    /**
     * 設定に記述されたすべての Javelin エージェントに接続します。<br />
     * {@link #init(DataCollectorConfig) init()} メソッドで渡された
     * {@link DataCollectorConfig} オブジェクトに従って、接続を行います。<br />
     * 本メソッドは {@link #startPluginMode()} メソッドを呼んだあとに実行してください。<br />
     */
    public void connectAll()
        throws InitializeException
    {
        checkRunning();

        if (this.config_ == null)
        {
            return;
        }

        List<AgentSetting> agentList = this.config_.getAgentSettingList();
        for (AgentSetting agentSetting : agentList)
        {
            String databaseName = agentSetting.databaseName;
            if (databaseName == null || databaseName.length() == 0)
            {
                // データベース名が指定されていなければ、
                // endosnipedb_(javelin.host.nの値)_(javelin.port.nの値) にする
                databaseName = "endosnipedb_" + agentSetting.hostName + "_" + agentSetting.port;
                agentSetting.databaseName = databaseName;
            }
            RotateConfig rotateConfig = createRotateConfig(agentSetting);
            addRotateConfig(rotateConfig);
            String clientId =
                              connect(databaseName, agentSetting.hostName, agentSetting.port,
                                      agentSetting.acceptPort);
            if (clientId == null)
            {
                LOGGER.log(DATABASE_ALREADY_USED, databaseName, agentSetting.hostName,
                           agentSetting.port);
            }
        }
        startLogRotate(true);
    }

    /**
     * Javelin エージェントに接続します。<br />
     * 本メソッドは {@link #startPluginMode()} メソッドを呼んだあとに実行してください。<br />
     *
     * すでに同じデータベースを参照して接続しているものが存在した場合は、
     * 接続せずに <code>null</code> を返します。<br />
     *
     * @param databaseName データベース名
     * @param javelinHost 接続先 Javelin のホスト名または IP アドレス
     * @param javelinPort 接続先 Javelin のポート番号
     * @param acceptPort BottleneckEye からの接続待ち受けポート番号
     * @return クライアント ID
     */
    public synchronized String connect(final String databaseName, final String javelinHost,
            final int javelinPort, final int acceptPort)
    {
        DBManager.setDbName(databaseName);

        checkRunning();

        String clientId = JavelinClient.createClientIdFromHost(javelinHost, javelinPort);

        // すでに同じデータベースを参照して接続しているものが存在した場合は、接続しない
        DataBaseManager manager = DataBaseManager.getInstance();
        String hostInfo = manager.getHostInfo(databaseName);
        if (hostInfo != null)
        {
            return null;
        }

        manager.addDbInfo(databaseName, clientId);

        JavelinClient javelinClient = findJavelinClient(clientId);
        JavelinDataQueue queue = this.javelinDataLogger_.getQueue();
        if (javelinClient == null)
        {
            // 既にクライアントが存在しない場合は新たに生成する
            javelinClient = new JavelinClient();
            javelinClient.init(databaseName, javelinHost, javelinPort, acceptPort);
            javelinClient.setTelegramNotifyListener(this.telegramNotifyListenersMap_.get(databaseName));
            javelinClient.connect(queue, this.behaviorMode_, null);
            this.clientList_.add(javelinClient);
            this.resourceGetterTask_.addTelegramSenderList(javelinClient.getTelegramSender());
        }
        else
        {
            javelinClient.setTelegramNotifyListener(this.telegramNotifyListenersMap_.get(clientId));

            // 既にクライアントが存在し、未接続の場合は接続する
            if (javelinClient.isConnected() == false)
            {
                javelinClient.connect(queue, this.behaviorMode_, null);
            }
        }

        return clientId;
    }

    /**
     * 指定されたクライアントを Javelin から切断します。<br />
     * 
     * @param clientId クライアント ID
     */
    public synchronized void disconnect(final String clientId)
    {
        JavelinClient client = findJavelinClient(clientId);
        if (client != null)
        {
            client.disconnect();
            DataBaseManager.getInstance().removeDbInfo(client.getDatabaseName());
        }
    }

    /**
     * すべての Javelin から切断します。<br />
     */
    public synchronized void disconnectAll()
    {
        for (JavelinClient javelinClient : clientList_)
        {
            javelinClient.disconnect();
            DataBaseManager.getInstance().removeDbInfo(javelinClient.getDatabaseName());
        }
        if (server_ != null)
        {
            server_.stop();
        }
    }

    /**
     * {@link ENdoSnipeDataCollector} が実行中であるかどうかを返します。<br />
     * 
     * @return 実行中の場合は <code>true</code>
     */
    public boolean isRunning()
    {
        return isRunning_;
    }

    private void checkRunning()
    {
        if (isRunning_ == false)
        {
            throw new IllegalStateException("ENdoSnipe DataCollector is not running.");
        }
    }

    /**
     * 指定されたクライアント ID の {@link JavelinClient} インスタンスを返します。
     *
     * @param clientId クライアント ID
     * @return 存在する場合は {@link JavelinClient} のインスタンス、存在しない場合は <code>null</code>
     */
    private JavelinClient findJavelinClient(final String clientId)
    {
        for (JavelinClient javelinClient : clientList_)
        {
            if (clientId.equals(javelinClient.getClientId()) == true)
            {
                return javelinClient;
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void sendTelegramToClient(final String clientId, final Telegram telegram)
    {
        if (server_ != null)
        {
            server_.sendTelegramToControlClient(clientId, telegram);
        }
        else
        {
            for (JavelinClient javelinClient : clientList_)
            {
                if (clientId.equals(javelinClient.getClientId()) == true)
                {
                    javelinClient.sendTelegramToClient(telegram);
                    return;
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public TelegramSender getTelegramSender(final String clientId)
    {
        if (config_.getConnectionMode().equals(DataCollectorConfig.MODE_CLIENT))
        {
            JavelinClient client = findJavelinClient(clientId);
            if (client != null)
            {
                return client.getTelegramSender();
            }
        }
        else
        {
            DataCollectorClient client = server_.getClient(clientId);
            if (client != null)
            {
                return client;
            }
        }
        return null;
    }

    /**
     * ログローテート用設定を追加
     * 
     * @param config ログローテート設定
     */
    public void addRotateConfig(final RotateConfig config)
    {
        synchronized (this.rotateConfigList_)
        {
            this.rotateConfigList_.add(config);
        }
        synchronized (this)
        {
            if (this.javelinDataLogger_ != null)
            {
                this.javelinDataLogger_.addRotateConfig(config);
            }
        }
    }

    /**
     * デフォルトのログローテート用設定を追加
     * 
     * @param config ログローテート設定
     */
    public void setDefaultRotateConfig(final RotateConfig config)
    {
        synchronized (this.rotateConfigList_)
        {
            this.rotateConfigList_.add(config);
        }
        synchronized (this)
        {
            if (this.javelinDataLogger_ != null)
            {
                this.javelinDataLogger_.setDefaultRotateConfig(config);
            }
            else
            {
                defaultRotateConfig_ = config;
            }
        }
    }

    /**
     * ログローテート設定を生成します。<br />
     * 
     * @param agentSetting 各エージェントの設定
     */
    private RotateConfig createRotateConfig(final AgentSetting agentSetting)
        throws InitializeException
    {
        RotateConfig config = new RotateConfig();
        config.setDatabase(agentSetting.databaseName);
        config.setJavelinRotatePeriod(agentSetting.getJavelinRotatePeriod());
        config.setJavelinRotatePeriodUnit(agentSetting.getJavelinRotatePeriodUnit());
        config.setMeasureRotatePeriod(agentSetting.getMeasurementRotatePeriod());
        config.setMeasureRotatePeriodUnit(agentSetting.getMeasurementRotatePeriodUnit());

        return config;
    }
}
