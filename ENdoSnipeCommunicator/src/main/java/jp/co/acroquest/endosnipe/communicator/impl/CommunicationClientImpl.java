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
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import jp.co.acroquest.endosnipe.common.jmx.JMXManager;
import jp.co.acroquest.endosnipe.common.logger.ENdoSnipeLogger;
import jp.co.acroquest.endosnipe.common.util.NetworkUtil;
import jp.co.acroquest.endosnipe.communicator.CommunicationClient;
import jp.co.acroquest.endosnipe.communicator.CommunicatorListener;
import jp.co.acroquest.endosnipe.communicator.ENdoSnipeCommunicatorPluginProvider;
import jp.co.acroquest.endosnipe.communicator.TelegramListener;
import jp.co.acroquest.endosnipe.communicator.TelegramUtil;
import jp.co.acroquest.endosnipe.communicator.accessor.ConnectNotifyAccessor;
import jp.co.acroquest.endosnipe.communicator.entity.ConnectNotifyData;
import jp.co.acroquest.endosnipe.communicator.entity.Telegram;

/**
 * {@link CommunicationClient} の実装クラスです。<br />
 * 
 * @author y-komori
 */
public class CommunicationClientImpl implements CommunicationClient, Runnable
{
    /** ロガークラス */
    private static final ENdoSnipeLogger LOGGER =
            ENdoSnipeLogger.getLogger(CommunicationClientImpl.class,
                                      ENdoSnipeCommunicatorPluginProvider.INSTANCE);

    /** をミリ秒単位で表した1秒 */
    private static final int SECOND_ON_MILLIS = 1000;

    /** 再接続を行う間隔（ミリ秒） */
    private static final int RETRY_INTERVAL = 10000;

    /** 電文を転送するターゲットオブジェクトのリスト */
    private final List<CommunicatorListener> listenerList_;

    /** 通信ソケット */
    private SocketChannel socketChannel_ = null;

    /** 出力ストリーム */
    private PrintStream objPrintStream_ = null;

    private TelegramReader telegramReader_;

    private final ExecutorService writeExecutor_ = createThreadPoolExecutor();

    /** ホスト（ホスト名または IP アドレス） */
    private String host_;

    /** ポート番号 */
    private int portNumber_;

    /** IPアドレス */
    private String ipAddress_;

    /** 接続状態 */
    private volatile boolean isConnect_ = false;

    /** start状態 */
    private volatile boolean started_ = true;

    /** 読み込みスレッド */
    private Thread readerThread_;

    /** 読み込みスレッドを監視するスレッド */
    private Thread readerMonitorThread_;

    /** スレッド名 */
    private final String threadName_;
    
    /** ログ出力有無. */
    private boolean isOutputLog_ = true;
    
    /** 受信電文を処理するためのリスナ */
    private List<TelegramListener> telegramListeners_;

    private boolean discard_;
    
    /** 接続完了後に送信する接続通知 */
    private ConnectNotifyData connectNotify_;

    /** Javelinかどうか */
    protected boolean isJavelin_ = false;

    /**
     * {@link CommunicationClientImpl} を構築します。<br />
     * @param threadName スレッド名
     */
    public CommunicationClientImpl(final String threadName)
    {
        this.threadName_ = threadName;

        // listenerList_ は synchronized を行う必要があるが、
        // 拡張 for 文（ iterator ）を使用する場合は Collections.synchronizedList でラップしても無駄なため、
        // 自前で synchronized 処理を行う
        this.listenerList_ = new ArrayList<CommunicatorListener>();
    }
    
    /**
     * {@link CommunicationClientImpl} を構築します。<br />
     * @param threadName スレッド名
     * @param isOutputLog ログ出力有無
     */
    public CommunicationClientImpl(final String threadName, final boolean discard, 
            final boolean isOutputLog)
    {
        this(threadName);
        this.discard_ = discard;
        this.isOutputLog_ = isOutputLog;
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void connect(ConnectNotifyData connectNotify)
    {
        this.connectNotify_ = connectNotify;

        String threadName = this.threadName_ + "-ReaderMonitor";
        this.readerMonitorThread_ = new Thread(this, threadName);
        this.readerMonitorThread_.setDaemon(true);
        this.readerMonitorThread_.start();
        this.started_ = true;
    }

    /**
     * サーバから切断します。<br />
     */
    public void disconnect()
    {
        disconnect(false);
    }

    /**
     * サーバから切断します。<br />
     *
     * @param forceDisconnected 強制切断された場合は <code>true</code>
     */
    public void disconnect(final boolean forceDisconnected)
    {
        this.started_ = false;
        this.writeExecutor_.execute(new Runnable() {
            public void run()
            {
                doClose(forceDisconnected);
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void shutdown()
    {
        if (this.telegramReader_ != null)
        {
            this.telegramReader_.shutdown();
        }
        this.writeExecutor_.shutdown();
    }

    /**
     * {@inheritDoc}
     */
    public void run()
    {
        // Javelinとして動作している場合、jmx.propertiesの読み込み完了を待つ
        if (isJavelin_)
            JMXManager.getInstance().waitInitialize();

        // 開始ボタンが押されている間（停止ボタンが押されるまで）再接続を繰り返す
        while (this.started_)
        {
            try
            {
                if (doConnect())
                {
                    startRead();

                    boolean forceDisconnected = false;

                    // 停止ボタンが押されるか、強制切断するまでループする
                    while (this.started_)
                    {
                        if (this.readerThread_ == null)
                        {
                            // 強制切断された
                            forceDisconnected = true;
                            break;
                        }
                        if (this.readerThread_.isAlive() == false)
                        {
                            // 強制切断された
                            forceDisconnected = true;
                            break;
                        }
                        waitMilliseconds(SECOND_ON_MILLIS);
                    }

                    stopRead();
                    doClose(forceDisconnected);
                }
                else
                {
                    // 接続に失敗したら、一定時間待ってから再接続を行う
                    waitMilliseconds(RETRY_INTERVAL);
                }
            }
            catch (InterruptedException ex)
            {
                LOGGER.log("WECC0202", ex);
            }
        }
        stopRead();
        doClose(false);
    }

    private void waitMilliseconds(final long milliseconds)
        throws InterruptedException
    {
        long remainedMilliseconds = milliseconds;
        while (remainedMilliseconds > 0 && this.started_)
        {
            long sleepTime = Math.min(remainedMilliseconds, SECOND_ON_MILLIS);
            Thread.sleep(sleepTime);
            remainedMilliseconds -= sleepTime;
        }
    }

    /**
     * サーバからの電文受信スレッドを開始します。<br />
     */
    public void startRead()
    {
        if (this.readerThread_ != null && this.readerThread_.isAlive())
        {
            return;
        }

        this.readerThread_ = new Thread(this.telegramReader_, this.threadName_ + "-Reader");
        this.readerThread_.setDaemon(true);
        if (this.telegramReader_ != null)
        {
            for (TelegramListener listener : this.telegramListeners_)
            {
                this.telegramReader_.addTelegramListener(listener);
            }
            this.telegramReader_.setRunning(true);
        }
        this.readerThread_.start();
    }

    /**
     * サーバからの電文受信スレッドを終了します。<br />
     */
    private void stopRead()
    {
        if (this.telegramReader_ != null)
        {
            this.telegramReader_.shutdown();
        }

        if (this.readerThread_ != null && this.readerThread_.isAlive())
        {
            // 割り込みを発生させ、終了するまで待つ。
            // join() を行わないと、スレッドが完全に終了しない間に再接続処理を実行しようとし、
            // スレッドが生きているため再接続を行わないようになってしまう。
            this.readerThread_.interrupt();
            try
            {
                this.readerThread_.join();
            }
            catch (InterruptedException ex)
            {
                LOGGER.log("WECC0204", ex);
            }
        }
        this.readerThread_ = null;
    }

    /**
     * サーバに電文を送信します。<br />
     * 
     * @param telegram 電文オブジェクト
     */
    public void sendTelegram(final Telegram telegram)
    {
        this.writeExecutor_.execute(new Runnable() {
            public void run()
            {
                List<byte[]> byteList = TelegramUtil.createTelegram(telegram);
                for (byte[] byteOutputArr : byteList)
                {
                    try
                    {
                        PrintStream objPrintStream = CommunicationClientImpl.this.objPrintStream_;
                        if (objPrintStream != null)
                        {
                            objPrintStream.write(byteOutputArr);
                            objPrintStream.flush();

                            // 強制終了が行われたとき、再接続を行う
                            if (objPrintStream.checkError())
                            {
                                outputLog("WECC0201");
                                // ReaderThread を終了させれば、 ReaderMonitorThread が再接続処理に入る
                                stopRead();
                            }
                        }
                    }
                    catch (IOException objIOException)
                    {
                        outputLog("WECC0202", objIOException);
                        CommunicationClientImpl.this.disconnect();
                    }
                }                    
            }
        });
    }

    /**
     * ソケットチャネルを取得します。<br />
     * 
     * @return ソケットチャネル
     */
    public SocketChannel getChannel()
    {
        return this.socketChannel_;
    }

    /**
     * 接続状態を取得します。<br />
     * 
     * @return 接続されているなら <code>true</code> 、そうでないなら <code>false</code>
     */
    public boolean isConnected()
    {
        return this.isConnect_;
    }

    /**
     * クローズ処理を行います。<br />
     *
     * @param forceDisconnected 強制切断された場合は <code>true</code>
     */
    private void doClose(final boolean forceDisconnected)
    {
        if (this.telegramReader_ != null)
        {
            this.telegramReader_.setRunning(false);
        }

        if (this.readerThread_ != null)
        {
            // 割り込みを発生させ、終了するまで待つ。
            // join() を行わないと、スレッドが完全に終了しない間に再接続処理を実行しようとし、
            // スレッドが生きているため再接続を行わないようになってしまう。
            this.readerThread_.interrupt();
            try
            {
                this.readerThread_.join();
            }
            catch (InterruptedException ex)
            {
                outputLog("WECC0204", ex);
            }
        }

        if (this.isConnect_ == false)
        {
            return;
        }

        // 使用した通信対象をクリアする
        if (this.objPrintStream_ != null)
        {
            this.objPrintStream_.close();
            this.objPrintStream_ = null;
        }

        try
        {
            if (this.socketChannel_ != null)
            {
                this.socketChannel_.close();
                this.socketChannel_ = null;
            }

            outputLog("IECC0205", this.threadName_);
            this.isConnect_ = false;
            if (this.telegramReader_ != null)
            {
                notifyClientDisconnected(forceDisconnected);
            }
        }
        catch (IOException objIOException)
        {
            // エラーを出す
            outputLog("WECC0202", objIOException);
        }
    }

    /**
     * 接続処理を行います。<br />
     *
     * @return 接続に成功した場合は <code>true</code>
     */
    private boolean doConnect()
    {
        if (this.isConnect_ == true)
        {
            return false;
        }

        try
        {
            // サーバに接続する
            SocketAddress remote = new InetSocketAddress(this.host_, this.portNumber_);
            this.socketChannel_ = SocketChannel.open(remote);
            this.ipAddress_ = getIpAddress();
            // 接続中のメッセージ
            outputLog("IECC0206", remote, this.threadName_);

            this.isConnect_ = true;
        }
        catch (Exception ex)
        {
            // エラーメッセージを出す
            logConnectException(this.host_, this.portNumber_);
            outputLog("WECC0202", ex);
            return false;
        }

        // リスナに通知する
        String hostName = null;
        if (NetworkUtil.isIpAddress(this.host_) == false)
        {
            hostName = this.host_;
        }
        try
        {
            if (this.socketChannel_ == null)
            {
                return false;
            }
            this.objPrintStream_ =
                    new PrintStream(this.socketChannel_.socket().getOutputStream(), true);
            
            this.telegramReader_ = new TelegramReader(this,
                                                      this.threadName_ + "-Sender",
                                                      this.socketChannel_.socket(), 
                                                      this.discard_,
                                                      this.isOutputLog_);
        }
        catch (IOException objIOException)
        {
            outputLog("WECC0202", objIOException);

            // 切断する。
            doClose(false);
            return false;
        }
        
        // 接続通知を送信
        ConnectNotifyData connectNotify = new ConnectNotifyData();
        if (this.connectNotify_ != null)
        {
            // 種別をコピー
            connectNotify.setKind(this.connectNotify_.getKind());
            connectNotify.setPurpose(this.connectNotify_.getPurpose());
            
            // DB名称を生成
            String dbName = this.connectNotify_.getDbName();
            InetAddress localAddress = this.socketChannel_.socket().getLocalAddress();
            String ipAddr = localAddress.getHostAddress();
            String localhostName = localAddress.getHostName();
            String realDbName = createDbName(dbName, localhostName, ipAddr);
            connectNotify.setDbName(realDbName);
            sendTelegram(ConnectNotifyAccessor.createTelegram(connectNotify));
        }

        notifyClientConnected(hostName, this.ipAddress_, this.portNumber_);
        
        return true;
    }

    /**
     * 開始状態を返します。<br />
     * 
     * @return 開始状態
     */
    public boolean isStart()
    {
        return this.started_;
    }

    /**
     * {@link TelegramListener} オブジェクトを追加します。<br />
     * 
     * @param listener 転送先オブジェクト
     */
    public void addTelegramListener(final TelegramListener listener)
    {
        
        if (this.telegramListeners_ == null)
        {
            this.telegramListeners_ = new ArrayList<TelegramListener>();
        }
        this.telegramListeners_.add(listener);
    }

    /**
     * {@inheritDoc}
     */
    public void addCommunicatorListener(final CommunicatorListener listener)
    {
        synchronized (this.listenerList_)
        {
            this.listenerList_.add(listener);
        }
    }

    /**
     * 接続先の IP アドレスを返します。<br />
     *
     * @return 接続先の IP アドレス
     */
    public String getIpAddress()
    {
        Socket socket = this.socketChannel_.socket();
        String inetAddr = socket.getInetAddress().toString();
        int delimiterPos = inetAddr.indexOf('/');
        String ipAddress = inetAddr.substring(delimiterPos + 1);
        return ipAddress;
    }

    /**
     * {@inheritDoc}
     */
    public void init(final String host, final int port)
    {
        this.host_ = host;
        this.portNumber_ = port;
    }

    /**
     * 電文をデバッグ出力します。<br />
     * 
     * @param message メッセージ
     * @param response 受信電文
     * @param length 電文長
     */
    public void logTelegram(final String message, final Telegram response, final int length)
    {
        /*
        String telegramStr = TelegramUtil.toPrintStr(response, length);
        LOGGER.warn(message + this.socketChannel_.socket().getInetAddress().getHostAddress() + ":"
                + this.socketChannel_.socket().getPort() + SystemLogger.NEW_LINE + telegramStr);
         */
    }

    private void logConnectException(final String ip, final int port)
    {
        outputLog("IECC0203", ip, port, this.threadName_);
    }

    private ThreadPoolExecutor createThreadPoolExecutor()
    {
        ThreadFactory factory = new ThreadFactory() {
            public Thread newThread(final Runnable r)
            {
                String name = CommunicationClientImpl.this.threadName_ + "-Writer";
                Thread thread = new Thread(r, name);
                thread.setDaemon(true);
                return thread;
            }
        };
        return new ThreadPoolExecutor(1, 1, 0, TimeUnit.NANOSECONDS,
                                      new LinkedBlockingQueue<Runnable>(), factory,
                                      new ThreadPoolExecutor.DiscardPolicy());
    }

    /**
     * 切断されたことを各リスナへ通知します。<br />
     *
     * @param forceDisconnected 強制切断された場合は <code>true</code>
     */
    private void notifyClientDisconnected(boolean forceDisconnected)
    {
        synchronized (this.listenerList_)
        {
            for (CommunicatorListener listener : this.listenerList_)
            {
                listener.clientDisconnected(forceDisconnected);
            }
        }
    }

    /**
     * 接続されたことを各リスナへ通知します。<br />
     *
     * @param hostName ホスト名（ <code>null</code> の可能性あり）
     * @param ipAddr IP アドレス
     * @param port ポート番号
     */
    private void notifyClientConnected(final String hostName, final String ipAddr, final int port)
    {
        synchronized (this.listenerList_)
        {
            for (CommunicatorListener listener : this.listenerList_)
            {
                listener.clientConnected(hostName, ipAddr, port);
            }
        }
    }
   
    /**
     * ログを出力します。<br />
     * 
     * @param messageCode メッセージコード
     * @param args 引数
     */
    private void outputLog(final String messageCode, final Object... args)
    {
        if (isOutputLog_)
        {
            LOGGER.log(messageCode, args);
        }
    }

    
    /**
     * DB名称を生成する。
     * "%H"文字列をホスト名に、
     * "%I"文字列をIPアドレスに置換した結果を返す。
     * 
     * @param dbName 接続情報に格納されていたDB名
     * @param hostName ホスト名
     * @param ipAddr IPアドレス
     * @return 返還後のDB名称
     */
    private static String createDbName(String dbName, String hostName, String ipAddr)
    {
        if (dbName == null)
        {
            return "unknown";
        }
        
        if (hostName == null)
        {
            hostName = "";
        }

        if (ipAddr == null)
        {
            ipAddr = "";
        }
        
        String realDbName = dbName;
        realDbName = realDbName.replaceAll("%H", hostName);
        realDbName = realDbName.replaceAll("%I", ipAddr);

        return realDbName;
    }

}
