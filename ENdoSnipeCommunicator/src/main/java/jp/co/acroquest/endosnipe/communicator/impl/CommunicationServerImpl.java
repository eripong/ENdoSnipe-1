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
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import jp.co.acroquest.endosnipe.common.logger.ENdoSnipeLogger;
import jp.co.acroquest.endosnipe.communicator.CommunicationServer;
import jp.co.acroquest.endosnipe.communicator.CommunicatorListener;
import jp.co.acroquest.endosnipe.communicator.ENdoSnipeCommunicatorPluginProvider;
import jp.co.acroquest.endosnipe.communicator.TelegramListener;
import jp.co.acroquest.endosnipe.communicator.TelegramUtil;
import jp.co.acroquest.endosnipe.communicator.entity.Telegram;
import jp.co.acroquest.endosnipe.communicator.entity.TelegramConstants;
import jp.co.acroquest.endosnipe.communicator.impl.JavelinClientThread.JavelinClientThreadListener;

/**
 * 通信部分のサーバ側の実装。
 *
 * @author eriguchi
 */
public class CommunicationServerImpl implements Runnable, CommunicationServer, TelegramConstants
{
    /** ロガークラス */
    private static final ENdoSnipeLogger LOGGER =
            ENdoSnipeLogger.getLogger(CommunicationServerImpl.class,
                                      ENdoSnipeCommunicatorPluginProvider.INSTANCE);


    private static final int MAX_SOCKET = 30;

    /** ポート番号の最大値 */
    private static final int MAX_PORT = 65535;
    
    /** サーバソケット */
    ServerSocket objServerSocket_ = null;


    /** クライアントのリスト */
    protected List<JavelinClientThread> clientList_ = new ArrayList<JavelinClientThread>();

    /** スレッド処理中かどうかを表すフラグ */
    boolean isRunning_ = false;

    /** 通信中かを表すフラグ */
    private boolean isListening_ = false;

    /** Javelinと通信を行うポート */
    private int port_;

    /** Javelinと通信を行う初期ポート番号 */
    private int startPort_;
    
    /** 通信用スレッド名 */
    private String acceptThreadName_ = "JavelinAcceptThread";

    /** 通信用スレッド */
    private Thread acceptThread_;

    /** 通信に使用するポートを範囲指定するか、のフラグ */
    private boolean isRange_ = false;

    /** 通信に使用するポートを範囲指定する際の最大値 */
    private int rangeMax_;

    private long waitForThreadStart_;

    private int bindInterval_;
    
    private boolean discard_;
    
    private String[] listeners_;
    
    /** CommunicationServerの状態変化を通知するリスナのリスト */
    private final List<CommunicatorListener> listenerList_;

    /** Javelinかどうか */
    protected boolean isJavelin_ = false;

    /**
     * @return discard
     */
    public boolean isDiscard()
    {
        return this.discard_;
    }
    
    /**
     * @return listeners
     */
    public String[] getListeners()
    {
        return this.listeners_;
    }

    
    /**
     * サーバインスタンスを生成します。
     *
     * @param isRange 接続ポートに範囲指定を利用する場合は <code>true</code>
     * @param rangeMax 接続ポートに範囲指定を利用する場合の範囲の最大値
     * @param waitForThreadStart スレッド開始までの待ち時間（ミリ秒）
     * @param bindInterval ポートオープンの試行間隔（秒）
     * @param listeners 利用するTelegramListener名
     */
    public CommunicationServerImpl(boolean isRange, int rangeMax, long waitForThreadStart,
            int bindInterval, String[] listeners)
    {
        this.isRange_ = isRange;
        this.rangeMax_ = rangeMax;
        this.waitForThreadStart_ = waitForThreadStart;
        this.bindInterval_ = bindInterval;
        this.listeners_ = listeners;
        this.listenerList_ = new ArrayList<CommunicatorListener>();
    }

    
    /**
     * サーバインスタンスを生成します。
     *
     * @param isRange 接続ポートに範囲指定を利用する場合は <code>true</code>
     * @param rangeMax 接続ポートに範囲指定を利用する場合の範囲の最大値
     * @param waitForThreadStart スレッド開始までの待ち時間（ミリ秒）
     * @param bindInterval ポートオープンの試行間隔（秒）
     * @param listeners 利用するTelegramListener名
     * @param threadName 通信用スレッド名
     */
    public CommunicationServerImpl(boolean isRange, int rangeMax, long waitForThreadStart,
            int bindInterval, String[] listeners, String threadName)
    {
        this(isRange, rangeMax, waitForThreadStart, bindInterval, listeners);
        this.acceptThreadName_ = threadName;
    }

    /**
     * {@inheritDoc}
     */
    public int getActiveClient()
    {
        return this.clientList_.size();
    }

    /**
     * {@inheritDoc}
     */
    public void init()
    {
        // 何もしない。
    }

    /**
     * {@inheritDoc}
     */
    public void start(int port)
    {
        if (this.objServerSocket_ != null)
        {
            return;
        }

        this.startPort_ = port;
        this.port_ = this.startPort_;

        if (this.isRange_ == true)
        {
            String key = "";
            String message = "";
            if (isPortNumValid(this.port_, this.rangeMax_) == true)
            {
                key = "javelin.communicate.JavelinAcceptThread.initRange";
                message = CommunicatorMessages.getMessage(key, this.startPort_, this.rangeMax_);
                LOGGER.info(message);
            }
            else
            {
                key = "javelin.communicate.JavelinAcceptThread.rangeError";
                message = CommunicatorMessages.getMessage(key, this.startPort_);
                LOGGER.warn(message);
                this.isRange_ = false;
            }
        }

        // クライアント接続の受付を開始する。
        try
        {
            this.acceptThread_ = new Thread(this, acceptThreadName_);
            this.acceptThread_.setDaemon(true);
            this.acceptThread_.start();
        }
        catch (Exception objException)
        {
            LOGGER.warn(objException);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void stop()
    {
        this.isRunning_ = false;
        
        if (this.isListening_ == false)
        {
            try
            {
                //　通信用ポートBind待ち状態のために、割り込みを行う
                Thread acceptThread = this.acceptThread_;
                if(acceptThread != null)
                {
                    acceptThread.interrupt();
                }
            }
            catch (Exception ex)
            {
                LOGGER.warn(ex);
            }
        }
        
        if (this.isListening_)
        {
            // 待ち受けソケットを閉じることにより、accept()でSocketExceptionが
            // 発生し、待ち受けスレッドが停止する。
            if (this.objServerSocket_ != null)
            {
                try
                {
                    this.objServerSocket_.close();
                }
                catch (Exception ex)
                {
                    LOGGER.warn(ex);
                }
            }

            this.isListening_ = false;
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean isConnected()
    {
        return false;
    }
    
    /**
     * クライアントにTelegramを送信する。
     * 
     * @param telegram 送信する電文。
     */
    public void sendTelegram(Telegram telegram)
    {

        if (telegram == null)
        {
            return;
        }
        
        boolean isSweep = false;

        List<byte[]> byteList = TelegramUtil.createTelegram(telegram);
        for (byte[] bytes : byteList)
        {
            List<JavelinClientThread> clientList = this.clientList_;
            int size = clientList.size();
            for (int index = size - 1; index >= 0; index--)
            {
                JavelinClientThread client = null;
                synchronized (clientList)
                {
                    if (index < clientList.size())
                    {
                        client = clientList.get(index);
                    }
                    else
                    {
                        continue;
                    }
                }
                
                if (client.isClosed())
                {
                    isSweep = true;
                    continue;
                }
                
                client.sendAlarm(bytes);
                if (LOGGER.isDebugEnabled())
                {
                    client.logTelegram(telegram, bytes);
                }
            }
        }                    
        
        
        if (isSweep == true)
        {
            sweepClient();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void addTelegramListener(TelegramListener listener)
    {
        // Do nothing.
    }

    /**
     * 通信用スレッドを実行する。
     */
    public void run()
    {
        try
        {
            Thread.sleep(this.waitForThreadStart_);
        }
        catch (Exception ex)
        {
            // Do nothing.
        }

        ThreadGroup group = new ThreadGroup("JavelinThreadGroup");
        String key = "";
        String message = "";

        this.isRunning_ = true;

        while (this.isRunning_ == true && this.isListening_ == false)
        {
            try
            {
                this.objServerSocket_ = new ServerSocket(this.port_);

                key = "javelin.communicate.JavelinAcceptThread.start";
                message = CommunicatorMessages.getMessage(key, this.port_);
                LOGGER.info(message);
                this.isListening_ = true;
            }
            catch (IOException objIOException)
            {
                int interval = this.bindInterval_;
                key = "javelin.communicate.JavelinAcceptThread.restart";
                message = CommunicatorMessages.getMessage(key, this.port_, interval);
                LOGGER.warn(message);
                if (this.isRange_ == true)
                {
                    // ポート番号を１増やして再接続を行う。
                    // 接続範囲を超えた場合には、javelin.bind.intervalの間スリープした後、処理を再度実行する。 
                    this.port_++;
                    if (this.port_ > this.rangeMax_)
                    {
                        key = "javelin.communicate.JavelinAcceptThread.overRange";
                        message =
                                CommunicatorMessages.getMessage(key, this.rangeMax_,
                                                                this.startPort_);
                        LOGGER.info(message);
                        this.port_ = this.startPort_;
                    }
                }
                sleep();
            }
        }

        while (this.isRunning_)
        {
            try
            {
                try
                {
                    accept(group);
                }
                catch (RuntimeException re)
                {
                    key = "javelin.communicate.snmp.TrapListener.SendingTelegramErrorMessage";
                    message = CommunicatorMessages.getMessage(key);
                    LOGGER.warn(message, re);
                }
            }
            catch (Throwable th)
            {
                LOGGER.warn(th);
            }
        }

        synchronized (this.clientList_)
        {
            for (int index = this.clientList_.size() - 1; index >= 0; index--)
            {
                JavelinClientThread client = this.clientList_.get(index);
                client.stop();
            }
        }

        try
        {
            if (this.objServerSocket_ != null && this.isConnected())
            {
                this.objServerSocket_.close();
            }
        }
        catch (IOException ioe)
        {
            key = "javelin.communicate.commonMessage.serverSocketCloseError";
            message = CommunicatorMessages.getMessage(key);
            LOGGER.warn(message, ioe);
        }
    }

    private void accept(final ThreadGroup group) throws SocketException
    {
        Socket clientSocket = null;
        
        String key = "";
        String message = "";
        try
        {
            // モニター
            clientSocket = this.objServerSocket_.accept();
        }
        catch (SocketException se)
        {
            // stop()でソケットを閉じた場合にSocketExceptionが発生する。
            throw se;
        }
        catch (IOException ioe)
        {
            key = "javelin.communicate.commonMessage.serverSocketAcceptError";
            message = CommunicatorMessages.getMessage(key);
            LOGGER.warn(message, ioe);
            return;
        }

        int clientCount = sweepClient();
        if (clientCount > MAX_SOCKET)
        {
            LOGGER.info("接続数が最大数[" + MAX_SOCKET + "]を超えたため、接続を拒否します。");
            try
            {
                clientSocket.close();
            }
            catch (IOException ioe)
            {
                key = "javelin.communicate.commonMessage.clientSocketCloseError";
                message = CommunicatorMessages.getMessage(key);
                LOGGER.warn(message, ioe);
            }
            return;
        }

        InetAddress clientIP = clientSocket.getInetAddress();
        key = "javelin.communicate.commonMessage.clientConnect";
        message = CommunicatorMessages.getMessage(key, clientIP);
        LOGGER.info(message);
        
        // クライアントからの要求受付用に、処理スレッドを起動する。
        JavelinClientThread clientRunnable;
        try
        {
            clientRunnable = createJavelinClientThread(clientSocket);
            Thread objHandleThread =
                    new Thread(group, clientRunnable,
                               acceptThreadName_ + "-JavelinClientThread-" + clientCount);
            objHandleThread.setDaemon(true);
            objHandleThread.start();

            // 通知のためのクライアントリストに追加する。
            synchronized (this.clientList_)
            {
                this.clientList_.add(clientRunnable);
            }
        }
        catch (IOException ioe)
        {
            LOGGER.warn("クライアント通信スレッドの生成に失敗しました。", ioe);
        }
        
        // 接続完了をリスナに通知
        String hostName = clientIP.getHostName();
        String ip = clientIP.getHostAddress();
        int port = clientSocket.getPort();
        notifyClientConnected(hostName, ip, port);
    }

    /**
     * JavelinClientコネクションオブジェクトを生成します。
     *
     * @param clientSocket ソケット
     * @return JavelinClientコネクションオブジェクト
     * @throws IOException 入出力例外が発生した場合
     */
    protected JavelinClientThread createJavelinClientThread(final Socket clientSocket)
        throws IOException
    {
        JavelinClientThreadListener listener = new JavelinClientThreadListener() {
            public void disconnected(boolean forceDisconnected)
            {
                notifyClientDisconnected(forceDisconnected);
            }
        };
        return new JavelinClientThread(clientSocket, this.discard_, this.listeners_, listener);
    }

    /**
     * ポートが既に開かれている場合に待機する。
     */
    private void sleep()
    {
        int interval = this.bindInterval_;

        try
        {
            Thread.sleep(interval);
        }
        catch (InterruptedException iex)
        {
            LOGGER.warn(iex);
        }
    }

    private int sweepClient()
    {
        int size;
        synchronized (this.clientList_)
        {
            for (int index = this.clientList_.size() - 1; index >= 0; index--)
            {
                JavelinClientThread client = this.clientList_.get(index);
                if (client.isClosed())
                {
                    this.clientList_.remove(index);
                }
            }
            size = this.clientList_.size();
        }

        return size;
    }

    /**
     * 初期ポート、ポート最大値が正常な範囲の値になっているかを判定する。
     * 
     * @param port 初期ポート
     * @param portMax ポート最大値
     * @return true 初期ポート、ポート最大値が正常な範囲の値になっている場合、<code>true</code>
     */
    private static boolean isPortNumValid(final int port, final int portMax)
    {
        if (portMax < 0 || portMax > MAX_PORT)
        {
            return false;
        }
        if (port > portMax || port < 0)
        {
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public void addCommunicatorListener(CommunicatorListener listener)
    {
        synchronized (this.listenerList_)
        {
            this.listenerList_.add(listener);
        }
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

}
