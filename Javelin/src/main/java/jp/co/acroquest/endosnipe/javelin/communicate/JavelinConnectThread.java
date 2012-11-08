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
package jp.co.acroquest.endosnipe.javelin.communicate;

import java.util.List;

import jp.co.acroquest.endosnipe.common.config.JavelinConfig;
import jp.co.acroquest.endosnipe.common.logger.SystemLogger;
import jp.co.acroquest.endosnipe.communicator.TelegramListener;
import jp.co.acroquest.endosnipe.communicator.entity.ConnectNotifyData;
import jp.co.acroquest.endosnipe.communicator.entity.Telegram;
import jp.co.acroquest.endosnipe.communicator.entity.TelegramConstants;
import jp.co.acroquest.endosnipe.communicator.impl.CommunicationClientImpl;
import jp.co.acroquest.endosnipe.communicator.impl.CommunicatorMessages;
import jp.co.acroquest.endosnipe.javelin.CallTreeNode;
import jp.co.acroquest.endosnipe.javelin.StatsJavelinRecorder;
import jp.co.acroquest.endosnipe.javelin.bean.Invocation;
import jp.co.acroquest.endosnipe.javelin.util.ArrayList;

/**
 * JavelinからDataCollectorへの通信用スレッド
 * @author matsuoka
 *
 */
public class JavelinConnectThread extends CommunicationClientImpl implements AlarmListener,
        TelegramConstants
{
    /** 設定 */
    static JavelinConfig config__ = new JavelinConfig();

    /** JavelinConnectThreadのインスタンス(シングルトン) */
    static JavelinConnectThread instance__ = new JavelinConnectThread();
    private JavelinConnectThread()
    {
        super("JavelinConnectThread", false, false);
        this.isJavelin_ = true;
        StatsJavelinRecorder.addListener(this);
        this.registerTelegramListeners(config__.getTelegramListeners().split(","));
    }

    /**
     * 初期化メソッド
     */
    public void init()
    {
        // Do Nothing.
    }

    /**
     * 接続スレッドを開始します。
     */
    public void connect()
    {
        String host = config__.getConnectHost();
        int port = config__.getConnectPort();
        this.init(host, port);
        
        ConnectNotifyData connectNotify = new ConnectNotifyData();
        connectNotify.setKind(ConnectNotifyData.KIND_JAVELIN);
        connectNotify.setDbName(config__.getDatabaseName());

        super.connect(connectNotify);
    }
        
    /**
     * JavelinAcceptThreadのインスタンスを取得する。
     * @return JavelinAcceptThread
     */
    public static JavelinConnectThread getInstance()
    {
        return instance__;
    }
 
    /**
     * {@inheritDoc}
     */
    public void sendExceedThresholdAlarm(final CallTreeNode node)
    {
        List<Invocation> invocationList = new ArrayList<Invocation>();
        List<Long> accumulateTimeList = new ArrayList<Long>();
        addInvocationList(invocationList, accumulateTimeList, node);

        Telegram objTelegram = JavelinTelegramCreator.create(invocationList,
                accumulateTimeList, BYTE_TELEGRAM_KIND_ALERT,
                BYTE_REQUEST_KIND_NOTIFY);

        // アラームを送信する。
        sendTelegram(objTelegram);
    }

    /**
     * CallTreeに保存された全てのInvocationと実行時間を再帰的にListに保存します。<br />
     * 
     * @param invocationList Invocationを保存するリスト。
     * @param accumulateTimeList 実行時間を保存するリスト。
     * @param node CallTreeNode
     */
    private void addInvocationList(List<Invocation> invocationList, List<Long> accumulateTimeList,
            final CallTreeNode node)
    {
        invocationList.add(node.getInvocation());
        accumulateTimeList.add(node.getAccumulatedTime());
        List<CallTreeNode> children = node.getChildren();
        for (int index = 0; index < children.size(); index++)
        {
            CallTreeNode child = children.get(index);
            addInvocationList(invocationList, accumulateTimeList, child);
        }

    }

    /**
     * このAlarmListenerがルートノードのみを処理するかどうかを返す。 ※このクラスでは、常にfalseを返す。
     * 
     * @see jp.co.acroquest.endosnipe.javelin.communicate.AlarmListener#isSendingRootOnly()
     * @return false(ルートノード以外も処理する)
     */
    public boolean isSendingRootOnly()
    {
        return false;
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
                    addTelegramListener((TelegramListener)listener);

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
}
