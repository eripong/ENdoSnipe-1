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
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import jp.co.acroquest.endosnipe.common.logger.SystemLogger;
import jp.co.acroquest.endosnipe.communicator.TelegramListener;
import jp.co.acroquest.endosnipe.communicator.TelegramUtil;
import jp.co.acroquest.endosnipe.communicator.entity.Telegram;

/**
 * Javelinのクライアントスレッドです<br />
 * 
 * @author eriguchi
 *
 */
public class JavelinClientThread implements Runnable
{
    private JavelinClientConnection clientConnection_;

    private boolean isRunning_;

    /** 電文処理クラスのリスト */
    private final List<TelegramListener> telegramListenerList_ = new ArrayList<TelegramListener>();
    
    /** JavelinClientThreadの状態変化を通知するリスナ */
    private JavelinClientThreadListener clientListener_;

    /**
     * JavelinClientThreadの状態変化を通知するリスナ。
     * 
     * <p>汎用性のない {@link JavelinClientThread} 専用のコールバックインターフェイス
     * であるため、内部インターフェイスとして定義する。</p>
     * 
     * @author matsuoka
     */
    interface JavelinClientThreadListener 
    {
        /**
         * 通信切断時にコールされる。
         *
         * @param forceDisconnected 強制切断された場合は <code>true</code>
         */
        void disconnected(boolean forceDisconnected);
    }

    /**
     * JavelinClientコネクションの開始と電文クラスの登録を行います。<br />
     * 
     * @param objSocket ソケット
     * @param discard アラーム送信間隔内に発生した同じアラームを破棄するかどうか
     * @param listeners 利用するTelegramListener名
     * @throws IOException 入出力例外が発生した場合
     */
    public JavelinClientThread(final Socket objSocket, final boolean discard,
            final String[] listeners)
        throws IOException
    {
        this.clientConnection_ = new JavelinClientConnection(objSocket, discard);

        // 電文処理クラスを登録する
        registerTelegramListeners(listeners);
    }
    
    /**
     * JavelinClientコネクションの開始と電文クラスの登録を行います。<br />
     * 
     * @param objSocket ソケット
     * @param discard アラーム送信間隔内に発生した同じアラームを破棄するかどうか
     * @param listeners 利用するTelegramListener名
     * @param clientLisener JavelinClientThreadの状態を通知するためのリスナ
     * @throws IOException 入出力例外が発生した場合
     */
    public JavelinClientThread(final Socket objSocket, final boolean discard,
            final String[] listeners, JavelinClientThreadListener clientLisener)
        throws IOException
    {
        this(objSocket, discard, listeners);
        this.clientListener_ = clientLisener;
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

            this.isRunning_ = true;
            while (this.isRunning_)
            {
                try
                {
                    // 要求を受信する。
                    byte[] byteInputArr = null;
                    byteInputArr = this.clientConnection_.recvRequest();

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

                    this.receiveTelegram(request);
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
                clientListener_.disconnected(forceDisconnected);
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
     * @param listener 電文処理に利用するTelegramListener
     */
    public void addListener(final TelegramListener listener)
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
        // 各TelegramListenerで処理を行う
        for (TelegramListener listener : this.telegramListenerList_)
        {
            try
            {
                Telegram response = listener.receiveTelegram(request);

                // 応答電文がある場合のみ、応答を返す
                if (response != null)
                {
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
            }
            catch (Throwable th)
            {
                SystemLogger.getInstance().warn(th);
            }
        }
    }

    /**
     * TelegramListenerのクラスをJavelin設定から読み込み、登録する。 クラスのロードは、以下の順でクラスローダでのロードを試みる。
     * <ol> <li>JavelinClientThreadをロードしたクラスローダ</li> <li>コンテキストクラスローダ</li>
     * </ol>
     * 
     * @param listeners 利用するTelegramListener名
     */
    private void registerTelegramListeners(final String[] listeners)
    {
        String key = "";
        String message = "";
        for (String listenerName : listeners)
        {
            try
            {
                if ("".equals(listenerName))
                {
                    continue;
                }

                Class<?> listenerClass = loadClass(listenerName);
                Object listener = listenerClass.newInstance();
                if (listener instanceof TelegramListener)
                {
                    addListener((TelegramListener)listener);

                    key = "javelin.communicate.JavelinClientThread.regist";
                    message = CommunicatorMessages.getMessage(key, listenerName);
                    SystemLogger.getInstance().info(message);
                }
                else
                {
                    key = "javelin.communicate.JavelinClientThread.notImplement";
                    message = CommunicatorMessages.getMessage(key, listenerName);
                    SystemLogger.getInstance().info(message);
                }
            }
            catch (Exception ex)
            {
                key = "javelin.communicate.JavelinClientThread.registError";
                message = CommunicatorMessages.getMessage(key, listenerName);
                SystemLogger.getInstance().warn(message, ex);
            }
        }
    }

    /**
     * クラスをロードする。 以下の順でクラスローダでのロードを試みる。 <ol> <li>JavelinClientThreadをロードしたクラスローダ</li>
     * <li>コンテキストクラスローダ</li> </ol>
     * 
     * @param className ロードするクラスの名前。
     * @return ロードしたクラス。
     * @throws ClassNotFoundException 全てのクラスローダでクラスが見つからない場合
     */
    private Class<?> loadClass(final String className)
        throws ClassNotFoundException
    {

        Class<?> clazz;
        try
        {
            clazz = Class.forName(className);
        }
        catch (ClassNotFoundException cnfe)
        {
            String key = "javelin.communicate.JavelinClientThread.loadError";
            String message = CommunicatorMessages.getMessage(key, className);
            SystemLogger.getInstance().info(message);
            clazz = Thread.currentThread().getContextClassLoader().loadClass(className);
        }

        return clazz;
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
}
