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
package jp.co.acroquest.endosnipe.communicator.impl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import jp.co.acroquest.endosnipe.common.logger.SystemLogger;
import jp.co.acroquest.endosnipe.communicator.TelegramListener;
import jp.co.acroquest.endosnipe.communicator.TelegramSender;
import jp.co.acroquest.endosnipe.communicator.TelegramUtil;
import jp.co.acroquest.endosnipe.communicator.accessor.ConnectNotifyAccessor;
import jp.co.acroquest.endosnipe.communicator.entity.ConnectNotifyData;
import jp.co.acroquest.endosnipe.communicator.entity.Header;
import jp.co.acroquest.endosnipe.communicator.entity.Telegram;
import jp.co.acroquest.endosnipe.communicator.entity.TelegramConstants;

/**
 * DataCollectorがクライアントから接続されたときに起動されるクライアントスレッド。
 * <p>本スレッドを起動すると、接続情報通知電文の受信待ち状態となる。クライアント
 * からの最初の電文は接続情報通知でなければならず、それ以外の電文を受信しても
 * すべて破棄する。また、接続情報通知電文を受信した後は、
 * {@link DataCollectorClient#setEnabled(boolean)}がコールされて接続状態が
 * 有効となるまで、電文を受信せずに滞留させる。</p>
 * 
 * @author matsuoka
 *
 */
public class DataCollectorClient implements Runnable, TelegramSender
{
    private static final long WAIT_FOR_CONNECT_NOTIFY_SLEEP_TIME = 100;

    private JavelinClientConnection clientConnection_;

    private boolean isRunning_;

    private boolean isWaitForConnectNotify_ = false;

    private boolean isEnabled_ = false;

    private ConnectNotifyData notifyData_;

    private String ipAddr_;

    private String hostName_;

    private int port_;

    private String clientId_;

    /** DB名単位でユニークなDB番号 */
    private int dbNo_ = 0;
    
    /** DB名 */
    private String dbName_ = null;

    /** 電文処理クラスのリスト */
    private final List<TelegramListener> telegramListenerList_ = new ArrayList<TelegramListener>();

    /** JavelinClientThreadの状態変化を通知するリスナ */
    private DataCollectorClientListener clientListener_;

    /**
     * {@link DataCollectorClient}の状態変化を通知するリスナ。
     * 
     * <p>汎用性のない {@link DataCollectorClient} 専用のコールバックインターフェイス
     * であるため、内部インターフェイスとして定義する。</p>
     * 
     * @author matsuoka
     */
    interface DataCollectorClientListener
    {
        /**
         * 接続情報通知を受信した時にコールされる。
         * <p>実装メソッドでは、接続情報通知の詳細を確認して
         * {@link DataCollectorClient#setEnabled(boolean)}か
         * {@link DataCollectorClient#stop()}をコールしなければならない。</p>
         * 
         * @param client クライアントインスタンス
         */
        void receiveConnectNotify(DataCollectorClient client);

        /**
         * 通信切断時にコールされる。
         * @param client クライアントインスタンス
         * @param forceDisconnected 強制切断された場合は <code>true</code>
         */
        void disconnected(DataCollectorClient client, boolean forceDisconnected);
    }

    /**
     * JavelinClientコネクションの開始と電文クラスの登録を行います。<br />
     * 
     * @param objSocket ソケット
     * @param discard discard
     * @param clientId クライアントID
     * @throws IOException 入出力例外が発生した場合
     */
    public DataCollectorClient(final Socket objSocket, final boolean discard, String clientId)
        throws IOException
    {
        InetAddress addr = objSocket.getInetAddress();
        ipAddr_ = addr.getHostAddress();
        hostName_ = addr.getHostName();
        port_ = objSocket.getPort();
        clientId_ = clientId;

        this.clientConnection_ = new JavelinClientConnection(objSocket, discard);
    }

    /**
     * 
     */
    public void run()
    {
        try
        {
            // 送信スレッドを開始する。
            startSendThread();

            isRunning_ = true;
            while (isRunning_)
            {
                try
                {
                    while (isWaitForConnectNotify_ && clientConnection_.isConnected())
                    {
                        // 接続情報通知電文を受信した後は、setEnabled()が
                        // コールされるまでsleepする。
                        Thread.sleep(WAIT_FOR_CONNECT_NOTIFY_SLEEP_TIME);
                    }

                    // 要求を受信する。
                    byte[] byteInputArr = null;
                    byteInputArr = clientConnection_.recvRequest();

                    // byte列をTelegramに変換する。
                    Telegram request = TelegramUtil.recoveryTelegram(byteInputArr);

                    if (request == null)
                    {
                        continue;
                    }
                    if (SystemLogger.getInstance().isDebugEnabled())
                    {
                        logReceiveTelegram(request, byteInputArr);
                    }

                    receiveTelegram(request);
                }
                catch (SocketTimeoutException ste)
                {
                    SystemLogger.getInstance().debug(ste);
                }
            }
        }
        catch (Exception exception)
        {
            String key = "javelin.communicate.commonMessage.receiveTelegramError";
            SystemLogger.getInstance().warn(CommunicatorMessages.getMessage(key), exception);
        }
        finally
        {
            boolean forceDisconnected = false;
            if (this.isRunning_)
            {
                forceDisconnected = true;
            }

            this.isRunning_ = false;
            this.clientConnection_.close();

            if (this.clientListener_ != null)
            {
                clientListener_.disconnected(this, forceDisconnected);
            }
        }
    }

    private void startSendThread()
    {
        JavelinClientSendRunnable clientSendRunnable =
                new JavelinClientSendRunnable(this.clientConnection_);
        String threadName = Thread.currentThread().getName() + "-Send";
        Thread clientSendThread = new Thread(clientSendRunnable, threadName);
        clientSendThread.setDaemon(true);
        clientSendThread.start();
    }

    /**
     * 電文処理に利用するTelegramListenerを登録する
     * 
     * @param listenerList 電文処理に利用するTelegramListenerのリスト
     */
    public void setTelegramListener(final List<TelegramListener> listenerList)
    {
        for (TelegramListener listener : listenerList)
        {
            addTelegramListener(listener);
        }
    }

    /**
     * 電文処理に利用するTelegramListenerを登録する
     * 
     * @param listener 電文処理に利用するTelegramListener
     */
    public void addTelegramListener(final TelegramListener listener)
    {
        synchronized (this.telegramListenerList_)
        {
            this.telegramListenerList_.add(listener);
        }
    }

    /**
     * 電文を受信し、応答電文があるときのみ電文を送信します。<br />
     * 
     * @param request 取得電文
     */
    protected void receiveTelegram(final Telegram request)
    {
        if (!isEnabled_)
        {
            // コネクションが有効となる前の処理
            boolean result = processConnectNotify(request);
            if (result)
            {
                isWaitForConnectNotify_ = true;
                clientListener_.receiveConnectNotify(this);
            }
            return;
        }

        // 各TelegramListenerで処理を行う
        for (TelegramListener listener : this.telegramListenerList_)
        {
            try
            {
                Telegram response = listener.receiveTelegram(request);

                // 応答電文がある場合のみ、応答を返す
                if (response == null)
                {
                    continue;
                }
                List<byte[]> byteList = TelegramUtil.createTelegram(response);
                for (byte[] byteOutputArr : byteList)
                {
                    this.clientConnection_.sendAlarm(byteOutputArr);
                    if (SystemLogger.getInstance().isDebugEnabled())
                    {
                        logTelegram(response, byteOutputArr);
                    }
                }
            }
            catch (Throwable th)
            {
                SystemLogger.getInstance().warn(th);
            }
        }
    }

    /**
     * 接続情報通知を処理する。
     * <p>受信した電文が接続情報通知であれば、
     * {@link DataCollectorClientListener#receiveConnectNotify(ConnectNotifyData)}を
     * コールし、<code>true</code>を返す。それ以外の電文であれば
     * <code>false</code>を返す。</p>
     * 
     * @param request 受信した電文
     * @return 受信した電文を処理できたら<code>true</code>を返す。
     */
    private boolean processConnectNotify(Telegram request)
    {
        Header header = request.getObjHeader();
        byte reqKind = header.getByteRequestKind();
        byte telKind = header.getByteTelegramKind();
        if (reqKind == TelegramConstants.BYTE_REQUEST_KIND_NOTIFY
                && telKind == TelegramConstants.BYTE_TELEGRAM_KIND_CONNECT_NOTIFY)
        {
            notifyData_ = ConnectNotifyAccessor.getConnectNotifyData(request);
        }
        else
        {
            return false;
        }
        return true;
    }

    /**
     * スレッドを停止する。
     */
    public void stop()
    {
        this.isRunning_ = false;
    }

    /**
     * 通信がクローズしているかどうかを返します。<br />
     * 
     * @return 通信がクローズしている場合、<code>true</code>
     */
    public boolean isClosed()
    {
        return this.clientConnection_.isClosed();
    }

    /**
     * アラームを送信します。<br />
     * 
     * @param bytes 電文のバイト列
     */
    public void sendAlarm(final byte[] bytes)
    {
        this.clientConnection_.sendAlarm(bytes);
    }

    /**
     * 電文のログをデバッグレベルで表示します。<br />
     * 
     * @param telegram 電文
     * @param bytes バイト列
     */
    public void logTelegram(final Telegram telegram, final byte[] bytes)
    {
        String key = "javelin.communicate.commonMessage.sendTelegram";
        SystemLogger.getInstance().debug(CommunicatorMessages.getMessage(key, telegram, bytes));
    }

    /**
     * 受信電文のログをデバッグレベルで表示します。<br />
     * 
     * @param telegram 電文
     * @param bytes バイト列
     */
    public void logReceiveTelegram(final Telegram telegram, final byte[] bytes)
    {
        String key = "javelin.communicate.commonMessage.receiveTelegram";
        SystemLogger.getInstance().debug(CommunicatorMessages.getMessage(key, telegram, bytes));
    }

    /**
     * {@inheritDoc}
     */
    public boolean isConnected()
    {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public void sendTelegram(Telegram telegram)
    {
        if (telegram == null)
        {
            return;
        }

        List<byte[]> byteList = TelegramUtil.createTelegram(telegram);
        for (byte[] bytes : byteList)
        {
            sendAlarm(bytes);
        }                    
        
    }

    /**
     * 接続の状態をを返す。
     * @return 接続が有効であれば<code>true</code>を返す。
     */
    public boolean isEnabled()
    {
        return isEnabled_;
    }

    /**
     * 接続の状態を設定する。
     * @param enabled 接続の状態
     */
    public void setEnabled(boolean enabled)
    {
        isEnabled_ = enabled;
        isWaitForConnectNotify_ = false;
    }

    /**
     * {@link DataCollectorClient}の状態変化を通知するためのリスナを登録する。
     * @param listener 登録するリスナ
     */
    public void setClientListener(DataCollectorClientListener listener)
    {
        clientListener_ = listener;
    }

    /**
     * 接続通知情報を取得する。
     * @return 接続通知情報を返す。
     */
    public ConnectNotifyData getConnectNotifyData()
    {
        return notifyData_;
    }


    /**
     * DB番号を取得する。
     * @return dbNo DB番号
     */
    public int getDbNo()
    {
        return dbNo_;
    }

    /**
     * DB番号を設定する、
     * @param dbNo DB番号
     */
    public void setDbNo(int dbNo)
    {
        dbNo_ = dbNo;
    }

    /**
     * DB名を取得する。
     * @return DB名
     */
    public String getDbName()
    {
        return this.dbName_;
    }

    /**
     * IPアドレスを取得する。
     * @return ipAddr IPアドレス
     */
    public String getIpAddr()
    {
        return ipAddr_;
    }

    /**
     * ホスト名を取得する。
     * @return hostName ホスト名
     */
    public String getHostName()
    {
        return hostName_;
    }

    /**
     * ポート番号を取得する。
     * @return port ポート番号
     */
    public int getPort()
    {
        return port_;
    }

    /**
     * クライアントIDを取得する。
     * @return clientId クライアントID
     */
    public String getClientId()
    {
        return clientId_;
    }

    /**
     * DB名を設定する。
     * 
     * @param dbName DB名
     */
    public void setDbName(String dbName)
    {
        this.dbName_ = dbName;
    }
}
