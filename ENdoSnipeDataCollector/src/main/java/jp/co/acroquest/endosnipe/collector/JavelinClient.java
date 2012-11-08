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

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import jp.co.acroquest.endosnipe.collector.data.JavelinConnectionData;
import jp.co.acroquest.endosnipe.collector.listener.AllNotifyListener;
import jp.co.acroquest.endosnipe.collector.listener.CommonResponseListener;
import jp.co.acroquest.endosnipe.collector.listener.JvnFileNotifyListener;
import jp.co.acroquest.endosnipe.collector.listener.SystemResourceListener;
import jp.co.acroquest.endosnipe.collector.listener.TelegramNotifyListener;
import jp.co.acroquest.endosnipe.collector.transfer.JavelinTransferServerThread;
import jp.co.acroquest.endosnipe.common.logger.CommonLogMessageCodes;
import jp.co.acroquest.endosnipe.common.logger.ENdoSnipeLogger;
import jp.co.acroquest.endosnipe.common.util.NetworkUtil;
import jp.co.acroquest.endosnipe.communicator.AbstractCommunicator;
import jp.co.acroquest.endosnipe.communicator.CommunicationClient;
import jp.co.acroquest.endosnipe.communicator.CommunicationFactory;
import jp.co.acroquest.endosnipe.communicator.CommunicatorListener;
import jp.co.acroquest.endosnipe.communicator.TelegramListener;
import jp.co.acroquest.endosnipe.communicator.TelegramReceiver;
import jp.co.acroquest.endosnipe.communicator.TelegramSender;
import jp.co.acroquest.endosnipe.communicator.entity.ConnectNotifyData;
import jp.co.acroquest.endosnipe.communicator.entity.Header;
import jp.co.acroquest.endosnipe.communicator.entity.Telegram;
import jp.co.acroquest.endosnipe.communicator.entity.TelegramConstants;
import jp.co.acroquest.endosnipe.data.service.HostInfoManager;

/**
 * Javelin からデータを受信するためのクライアントです。<br />
 * 
 * @author y-komori
 */
public class JavelinClient implements CommunicatorListener, LogMessageCodes
{
    private static final ENdoSnipeLogger      LOGGER          =
                                                                ENdoSnipeLogger.getLogger(JavelinClient.class,
                                                                                          ENdoSnipeDataCollectorPluginProvider.INSTANCE);

    private String                            databaseName_;

    /** Javelin が動作しているホスト名または IP アドレス */
    private String                            javelinHost_;

    /** Javelin への接続ポート番号 */
    private int                               javelinPort_;

    /** BottleneckEye 待ち受けポート番号 */
    private int                               acceptPort_;

    private CommunicationClient               client_;

    private List<TelegramNotifyListener>      telegramNotifyListenerList_;

    private String                            clientId_;

    private final JavelinTransferServerThread transferThread_ = new JavelinTransferServerThread();

    /** データを蓄積するためのキュー */
    private JavelinDataQueue                  queue_;

    /**
     * コンストラクタ。
     */
    public JavelinClient()
    {
        this.telegramNotifyListenerList_ = Collections.emptyList();
    }

    /**
     * クライアントの接続設定を行います。<br />
     *
     * @param databaseName データベース名
     * @param javelinHost 接続先 Javelin のホスト名または IP アドレス
     * @param javelinPort 接続先 Javelin のポート番号
     * @param acceptPort BottleneckEye からの接続待ち受けポート番号
     */
    public void init(final String databaseName, final String javelinHost, final int javelinPort,
            final int acceptPort)
    {
        this.databaseName_ = databaseName;
        this.javelinHost_ = javelinHost;
        this.javelinPort_ = javelinPort;
        this.acceptPort_ = acceptPort;
        this.clientId_ = JavelinClient.createClientIdFromHost(javelinHost, javelinPort);
    }

    /**
     * クライアントに通知するためのリスナを登録します。
     * 登録と同時に、リスナに対して電文送信オブジェクトをセットします。
     *
     * @param notifyListenerList クライアントに通知するためのリスナ
     */
    public void setTelegramNotifyListener(final List<TelegramNotifyListener> notifyListenerList)
    {
        if (notifyListenerList != null)
        {
            this.telegramNotifyListenerList_ = notifyListenerList;
            setTelegramSenders();
        }
        else
        {
            this.telegramNotifyListenerList_ = Collections.emptyList();
        }
    }

    /**
     * クライアントを開始します。<br />
     *
     * @param queue データを蓄積するためのキュー
     * @param connectNotify 接続完了後に送信する接続通知情報
     */
    public synchronized void connect(final JavelinDataQueue queue,
            final ConnectNotifyData connectNotify)
    {
        connect(queue, BehaviorMode.CONNECT_MODE, connectNotify);
    }

    /**
     * クライアントを開始します。<br />
     *
     * @param queue データを蓄積するためのキュー
     * @param behaviorMode サービスモード
     * @param connectNotify 接続完了後に送信する接続通知情報
     */
    public synchronized void connect(final JavelinDataQueue queue, final BehaviorMode behaviorMode,
            final ConnectNotifyData connectNotify)
    {
        this.queue_ = queue;

        if (isConnected() == true)
        {
            // 既に接続中の場合
            LOGGER.log(JAVELIN_ALREADY_CONNECTED, this.javelinHost_, this.javelinPort_);
            return;
        }

        String hostName = null;
        if (NetworkUtil.isIpAddress(this.javelinHost_) == false)
        {
            hostName = this.javelinHost_;
        }

        initializeClient(queue, behaviorMode, hostName, connectNotify);
    }

    /**
     * サーバから切断します。<br />
     */
    public synchronized void disconnect()
    {
        if (this.client_ != null)
        {
            // 切断を表すデータをキューに追加する
            Date currentDate = new Date();
            long currentTime = currentDate.getTime();
            JavelinConnectionData disconnectionData =
                                                      new JavelinConnectionData(
                                                                                JavelinConnectionData.TYPE_DISCONNECTION);
            disconnectionData.measurementTime = currentTime;
            disconnectionData.setDatabaseName(JavelinClient.this.databaseName_);
            if (this.queue_ != null)
            {
                this.queue_.offer(disconnectionData);
            }
            transferThread_.stop();
        }

        if (this.client_ != null)
        {
            this.client_.disconnect();
            this.client_.shutdown();
        }
    }

    /**
     * Javelin へ接続中かどうかを返します。<br />
     * 
     * @return 接続中の場合は <code>true</code>
     */
    public synchronized boolean isConnected()
    {
        if (this.client_ != null)
        {
            return this.client_.isConnected();
        }
        return false;
    }

    /**
     * データベース名を返します。<br />
     *
     * @return データベース名
     */
    public String getDatabaseName()
    {
        return this.databaseName_;
    }

    /**
     * クライアントを識別する ID を返します。<br />
     * 
     * @return クライアントID
     */
    public String getClientId()
    {
        return this.clientId_;
    }

    /**
     * ホスト名とポート番号からクライアント ID を生成します。<br />
     * 
     * @param host ホスト名
     * @param port ポート番号
     * @return クライアント ID
     */
    public static String createClientIdFromHost(final String host, final int port)
    {
        String ipAddress = host;
        try
        {
            InetAddress address = InetAddress.getByName(host);
            ipAddress = address.getHostAddress();
        }
        catch (UnknownHostException ex)
        {
            LOGGER.warn(CommonLogMessageCodes.UNEXPECTED_ERROR, ex);
        }

        return createClientId(ipAddress, port);
    }

    /**
     * IPアドレスとポート番号からクライアント ID を生成します。<br />
     * 
     * @param ipAddr IPアドレス
     * @param port ポート番号
     * @return クライアント ID
     */
    public static String createClientId(final String ipAddr, final int port)
    {
        return ipAddr + ":" + port;
    }

    /**
     * {@inheritDoc}
     */
    public void clientConnected(final String hostName, final String ipAddress, final int port)
    {
        LOGGER.log(JAVELIN_CONNECTED, this.javelinHost_, this.javelinPort_);
    }

    /**
     * {@inheritDoc}
     */
    public void clientDisconnected(final boolean forceDisconnected)
    {
        LOGGER.log(JAVELIN_DISCONNECTED, this.javelinHost_, this.javelinPort_);
    }

    private void setTelegramSenders()
    {
        for (TelegramNotifyListener notifyListener : this.telegramNotifyListenerList_)
        {
            notifyListener.setTelegramSender(getTelegramSender());
        }
    }

    /**
     * 電文送信オブジェクトを取得します。
     * @return 電文送信オブジェクト
     */
    public TelegramSender getTelegramSender()
    {
        return getCommunicator();
    }

    /**
     * 電文受信オブジェクトを取得します。
     * @return 電文受信オブジェクト
     */
    private TelegramReceiver getTelegramReceiver()
    {
        return getCommunicator();
    }

    private void addResponseTelegramListener(final byte telegramKind)
    {
        CommonResponseListener listener = new CommonResponseListener(this.queue_, telegramKind);
        getTelegramReceiver().addTelegramListener(listener);
    }

    /**
     * BottleneckEye に、 DataCollector が変換したデータを通知します。
     *
     * @param telegram 電文
     */
    public void sendTelegramToClient(final Telegram telegram)
    {
        // プラグインモードの場合は通知し、そうでない場合はクライアントに電文送信する。
        if (this.acceptPort_ == -1)
        {
            for (TelegramNotifyListener notifyListener : this.telegramNotifyListenerList_)
            {
                if (notifyListener.isRawTelegramNeeded() == false)
                {
                    notifyListener.receiveTelegram(telegram);
                }
            }
        }
        else
        {
            this.transferThread_.sendTelegram(telegram);
        }
    }

    /**
     * クライアント動作時の初期化処理。
     * 
     * @param queue データを蓄積するためのキュー
     * @param alarmRepository アラーム
     * @param behaviorMode サービスモード
     * @param hostName ホスト名
     * @param connectNotify 接続完了後に送信する接続通知情報
     */
    private synchronized void initializeClient(final JavelinDataQueue queue,
            final BehaviorMode behaviorMode, final String hostName,
            final ConnectNotifyData connectNotify)
    {
        this.client_ =
                       CommunicationFactory.getCommunicationClient("DataCollector-ClientThread-"
                               + this.clientId_);
        this.client_.init(this.javelinHost_, this.javelinPort_);

        initializeCommon(queue, behaviorMode, hostName);

        // サーバへ接続する(接続に成功するまでリトライを続ける)
        this.client_.connect(connectNotify);
    }

    /**
     * サーバ/クライアント動作の共通の初期化処理。
     * 
     * @param queue データを蓄積するためのキュー
     * @param alarmRepository アラーム
     * @param behaviorMode サービスモード
     * @param hostName ホスト名
     */
    private synchronized void initializeCommon(final JavelinDataQueue queue,
            final BehaviorMode behaviorMode, final String hostName)
    {
        setTelegramSenders();

        TelegramReceiver receiver = getTelegramReceiver();

        AllNotifyListener allNotifyListener = new AllNotifyListener();
        allNotifyListener.setTelegramNotifyListener(this.telegramNotifyListenerList_);
        receiver.addTelegramListener(allNotifyListener);

        final JvnFileNotifyListener JVN_FILE_NOTIFY_LISTENER =
                                                               createJvnFileNotifyListener(queue,
                                                                                           hostName);
        final SystemResourceListener SYSTEM_RESOURCE_LISTENER =
                                                                createSystemResourceListener(queue,
                                                                                             hostName);

        if (queue != null)
        {
            receiver.addTelegramListener(JVN_FILE_NOTIFY_LISTENER);
            receiver.addTelegramListener(SYSTEM_RESOURCE_LISTENER);
            addResponseTelegramListener(TelegramConstants.BYTE_TELEGRAM_KIND_GET_DUMP);
            addResponseTelegramListener(TelegramConstants.BYTE_TELEGRAM_KIND_UPDATE_PROPERTY);
        }

        // クライアント・サーバの状態変化を通知するためのリスナを登録
        CommunicatorListener listener =
                                        createCommunicatorListener(queue, JVN_FILE_NOTIFY_LISTENER,
                                                                   SYSTEM_RESOURCE_LISTENER);
        getCommunicator().addCommunicatorListener(listener);
        getCommunicator().addCommunicatorListener(this);
    }

    /**
     * サービスモード時の初期化処理。
     * @param alarmRepository アラーム
     */
    private synchronized void initializeForServiceMode()
    {

        // BottleneckEye->DataCollector->Javelin
        this.transferThread_.addTelegramListener(new TelegramListener() {
            public Telegram receiveTelegram(final Telegram telegram)
            {
                getTelegramSender().sendTelegram(telegram);
                return null;
            }
        });

        // Javelin->DataCollector->BottleneckEye
        this.transferThread_.start(this.acceptPort_);
        getTelegramReceiver().addTelegramListener(new TelegramListener() {
            public Telegram receiveTelegram(final Telegram telegram)
            {
                Header header = telegram.getObjHeader();
                if (header.getByteTelegramKind() == TelegramConstants.BYTE_TELEGRAM_KIND_RESOURCENOTIFY
                        && header.getByteRequestKind() == TelegramConstants.BYTE_REQUEST_KIND_RESPONSE)
                {
                    return null;
                }

                transferThread_.sendTelegram(telegram);
                return null;
            }
        });
    }

    /**
     * クライアント・サーバの状態変化を受け取るリスナを生成します。
     * 
     * @param queue データを蓄積するためのキュー
     * @param jvnFileNotifyListener Javelinログを受信するためのリスナ
     * @param systemResourceListener システムリソース通知を受信するためのリスナ
     * @return 生成したリスナ
     */
    private CommunicatorListener createCommunicatorListener(final JavelinDataQueue queue,
            final JvnFileNotifyListener jvnFileNotifyListener,
            final SystemResourceListener systemResourceListener)
    {
        // 接続に成功するとSocketChannelからIPアドレスを取得できるため、
        // そのときにJvnFileNotifyListenerにIPアドレスを登録する
        CommunicatorListener listener = new CommunicatorListener() {
            public void clientConnected(final String hostName, final String ipAddress,
                    final int port)
            {
                if (queue != null)
                {
                    jvnFileNotifyListener.setIpAddress(ipAddress);
                    systemResourceListener.setIpAddress(ipAddress);
                    HostInfoManager.registerHostInfo(JavelinClient.this.databaseName_, hostName,
                                                     ipAddress, JavelinClient.this.javelinPort_,
                                                     null);
                }

                for (TelegramNotifyListener telegramNotifyListener : JavelinClient.this.telegramNotifyListenerList_)
                {
                    telegramNotifyListener.clientConnected(hostName, ipAddress, port);
                }

                // 接続を表すデータをキューに追加する
                Date currentDate = new Date();
                long currentTime = currentDate.getTime();
                JavelinConnectionData connectionData =
                                                       new JavelinConnectionData(
                                                                                 JavelinConnectionData.TYPE_CONNECTION);
                connectionData.measurementTime = currentTime;
                connectionData.setDatabaseName(JavelinClient.this.databaseName_);
                if (queue != null)
                {
                    queue.offer(connectionData);
                }
            }

            public void clientDisconnected(final boolean forceDisconnected)
            {
                if (forceDisconnected)
                {
                    // 切断を表すデータをキューに追加する（強制切断された場合）
                    Date currentDate = new Date();
                    long currentTime = currentDate.getTime();
                    JavelinConnectionData disconnectionData =
                                                              new JavelinConnectionData(
                                                                                        JavelinConnectionData.TYPE_DISCONNECTION);
                    disconnectionData.measurementTime = currentTime;
                    disconnectionData.setDatabaseName(JavelinClient.this.databaseName_);
                    if (queue != null)
                    {
                        queue.offer(disconnectionData);
                    }
                }

                for (TelegramNotifyListener telegramNotifyListener : JavelinClient.this.telegramNotifyListenerList_)
                {
                    telegramNotifyListener.clientDisconnected(forceDisconnected);
                }
            }
        };
        return listener;
    }

    /**
     * JvnFileNotifyListenerを作成します。
     * 
     * @param queue データを蓄積するためのキュー
     * @param hostName 接続先のホスト名
     * @return 作成したJvnFileNotifyListener
     */
    private JvnFileNotifyListener createJvnFileNotifyListener(final JavelinDataQueue queue,
            final String hostName)
    {
        JvnFileNotifyListener notifyListener = null;
        if (queue != null)
        {
            notifyListener = new JvnFileNotifyListener(queue);
            notifyListener.setDatabaseName(this.databaseName_);
            notifyListener.setHostName(hostName);
            notifyListener.setPort(this.javelinPort_);
        }
        return notifyListener;
    }

    /**
     * SystemResourceListenerを作成します。
     * 
     * @param queue データを蓄積するためのキュー
     * @param hostName 接続先のホスト名
     * @return 作成したSystemResourceListener
     */
    private SystemResourceListener createSystemResourceListener(final JavelinDataQueue queue,
            final String hostName)
    {
        SystemResourceListener notifyListener = null;
        if (queue != null)
        {
            notifyListener = new SystemResourceListener(queue);
            notifyListener.setDatabaseName(this.databaseName_);
            notifyListener.setHostName(hostName);
            notifyListener.setPort(this.javelinPort_);
        }
        return notifyListener;
    }

    /**
     * Javelin接続モードから現在有効なコミュニケータを返します。
     * @return 現在有効なコミュニケータ
     */
    private synchronized AbstractCommunicator getCommunicator()
    {
        AbstractCommunicator communicator;
        communicator = this.client_;
        return communicator;
    }
}
