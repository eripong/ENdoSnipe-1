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
package jp.co.acroquest.endosnipe.web.dashboard.listener.javelin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.co.acroquest.endosnipe.communicator.CommunicationClient;
import jp.co.acroquest.endosnipe.communicator.CommunicationFactory;
import jp.co.acroquest.endosnipe.communicator.TelegramListener;
import jp.co.acroquest.endosnipe.communicator.accessor.ConnectNotifyAccessor;
import jp.co.acroquest.endosnipe.communicator.entity.ConnectNotifyData;
import jp.co.acroquest.endosnipe.communicator.entity.Header;
import jp.co.acroquest.endosnipe.communicator.entity.Telegram;
import jp.co.acroquest.endosnipe.communicator.entity.TelegramConstants;
import jp.co.acroquest.endosnipe.web.dashboard.config.DataBaseConfig;
import jp.co.acroquest.endosnipe.web.dashboard.listener.collector.CollectorListener;
import jp.co.acroquest.endosnipe.web.dashboard.listener.collector.ResourceAlarmListener;
import jp.co.acroquest.endosnipe.web.dashboard.listener.collector.ResourceStateListener;
import jp.co.acroquest.endosnipe.web.dashboard.manager.ConnectionClient;
import jp.co.acroquest.endosnipe.web.dashboard.manager.DatabaseManager;
import jp.co.acroquest.endosnipe.web.dashboard.manager.MessageSender;
import jp.co.acroquest.endosnipe.web.dashboard.servlet.DashBoardServlet;

/**
 * DataCollectorからJavelinの増減通知を受け、agentの作成を行うリスナです。
 * @author kajiwara
 *
 */
public class JavelinNotifyListener implements TelegramListener
{

    /** メッセージ送信用オブジェクトです。 */
    private MessageSender               messageSender_;

    /** DB名(1つの場合のみ対応) TODO 複数対応 */
    private static String dataBaseName_;

    /** 接続しているDB名のリスト */
    private static Map<Integer, String> databaseNameMap_ = new HashMap<Integer, String>();
    
    /** エージェントID */
    private static int currentAgentID__;

    /**
     * コンストラクタです。
     * @param messageSender {@link MessageSender}オブジェクト
     * @param agentId エージェントID
     */
    public JavelinNotifyListener(MessageSender messageSender)
    {
        this.messageSender_ = messageSender;
    }

    /**
     * Javelin増減電文を受けagentの増減を行います。
     * @param telegram Javelin増減通知電文
     * @return 応答電文(nullを返す。)
     */
    public Telegram receiveTelegram(Telegram telegram)
    {
        Header header = telegram.getObjHeader();
        if ((header.getByteTelegramKind() == TelegramConstants.BYTE_TELEGRAM_KIND_ADD_DATABASE_NAME || header.getByteTelegramKind() == TelegramConstants.BYTE_TELEGRAM_KIND_DEL_DATABASE_NAME)
                && header.getByteRequestKind() == TelegramConstants.BYTE_REQUEST_KIND_NOTIFY)
        {
            Set<String> databaseNameList = ConnectNotifyAccessor.getDataBaseNameList(telegram);
            modifyCollectorClientThread(databaseNameList, header.getByteTelegramKind());

            //            sendNotifyEntry(databaseNameList, header.getByteTelegramKind());
            //
        }
        return null;
    }

    /**
     * DataCollector-ClientThreadを作成・削除を行う
     * @param databaseNameList DataCollectorから取得したDB名リスト
     * @param telegramKind Javelin増減通知電文
     */
    private void modifyCollectorClientThread(Set<String> databaseNameList, byte telegramKind)
    {
        DatabaseManager manager = DatabaseManager.getInstance();
        DataBaseConfig dbConfig = manager.getDataBaseConfig();

        if (telegramKind == TelegramConstants.BYTE_TELEGRAM_KIND_ADD_DATABASE_NAME)
        {
            ConnectionClient connectionClient = ConnectionClient.getInstance();
            List<CommunicationClient> clientList = connectionClient.getClientList();
            for (String databaseName : databaseNameList)
            {
                if (dataBaseName_ != null)
                {
                    continue;
                }
                // DataCollectorに接続する。
                String javelinHost = dbConfig.getServerModeAgentSetting().acceptHost;
                int javelinPort = dbConfig.getServerModeAgentSetting().acceptPort;

                int agentId = generateAgentId();
                String clientId = DashBoardServlet.createClientId(javelinHost, javelinPort);

                CommunicationClient client =
                                             CommunicationFactory.getCommunicationClient("DataCollector-ClientThread-"
                                                     + clientId);
                client.init(javelinHost, javelinPort);
                client.addTelegramListener(new CollectorListener(messageSender_, agentId,
                                                                 databaseName));
                client.addTelegramListener(new ResourceAlarmListener(messageSender_, agentId));
                client.addTelegramListener(new ResourceStateListener(messageSender_, agentId));

                ConnectNotifyData connectNotify = new ConnectNotifyData();
                connectNotify.setKind(ConnectNotifyData.KIND_CONTROLLER);
                connectNotify.setPurpose(ConnectNotifyData.PURPOSE_GET_RESOURCE);
                connectNotify.setDbName(databaseName);

                client.connect(connectNotify);
                clientList.add(client);

                dataBaseName_ = databaseName;
            }
        }
    }

    /**
     * agentIDを取得する。
     * @return agentID
     */
    private synchronized int generateAgentId()
    {
        return currentAgentID__++;
    }

    /**
     * DB名リストを取得します。
     * @return databaseNameList_ DB名リスト
     */
    public static Map<Integer, String> getDatabaseNameMap()
    {
        return databaseNameMap_;
    }

    public static String getDatabaseName(int agentId)
    {
        return dataBaseName_;
    }
}
