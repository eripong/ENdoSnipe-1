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
package jp.co.acroquest.endosnipe.web.dashboard.servlet;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.co.acroquest.endosnipe.common.logger.ENdoSnipeLogger;
import jp.co.acroquest.endosnipe.communicator.CommunicationClient;
import jp.co.acroquest.endosnipe.communicator.CommunicationFactory;
import jp.co.acroquest.endosnipe.communicator.entity.ConnectNotifyData;
import jp.co.acroquest.endosnipe.web.dashboard.config.AgentSetting;
import jp.co.acroquest.endosnipe.web.dashboard.config.DataBaseConfig;
import jp.co.acroquest.endosnipe.web.dashboard.constants.EventConstants;
import jp.co.acroquest.endosnipe.web.dashboard.constants.LogMessageCodes;
import jp.co.acroquest.endosnipe.web.dashboard.dto.CometEventWrapper;
import jp.co.acroquest.endosnipe.web.dashboard.listener.collector.AlarmNotifyListener;
import jp.co.acroquest.endosnipe.web.dashboard.listener.collector.CollectorListener;
import jp.co.acroquest.endosnipe.web.dashboard.listener.collector.ResourceAlarmListener;
import jp.co.acroquest.endosnipe.web.dashboard.listener.collector.ResourceStateListener;
import jp.co.acroquest.endosnipe.web.dashboard.listener.javelin.JavelinNotifyListener;
import jp.co.acroquest.endosnipe.web.dashboard.manager.ConnectionClient;
import jp.co.acroquest.endosnipe.web.dashboard.manager.DatabaseManager;
import jp.co.acroquest.endosnipe.web.dashboard.manager.MessageSender;

import org.apache.catalina.comet.CometEvent;
import org.apache.catalina.comet.CometProcessor;

/**
 * WebDashボードの通信を行うサーブレットです。
 * @author s_fujii
 *
 */
public class DashBoardServlet extends HttpServlet implements CometProcessor
{

    private static final int             SLEEP_TIME         = 5000;

    private static final int             CONNECTION_TIMEOUT = 60 * 1000 * 30;

    /** シリアルID */
    private static final long            serialVersionUID   = 3003980920335995413L;

    /** ロガー */
    private static final ENdoSnipeLogger LOGGER             =
                                                              ENdoSnipeLogger.getLogger(DashBoardServlet.class);

    /** メッセージ送信用オブジェクト */
    public transient MessageSender       messageSender_     = null;

    /**
     * 初期処理を行います。
     * @throws ServletException サーブレット上で例外が発生した場合
     */
    public void init()
        throws ServletException
    {
        //　通信用オブジェクトの作成

        messageSender_ = new MessageSender();
        Thread messageSenderThread =
                                     new Thread(messageSender_, "MessageSender["
                                             + getServletContext().getContextPath() + "]");
        messageSenderThread.setDaemon(true);
        messageSenderThread.start();

        DataBaseConfig dbConfig = null;
        // DBの設定が行われるのを待ち続ける。
        while (true)
        {
            DatabaseManager manager = DatabaseManager.getInstance();
            dbConfig = manager.getDataBaseConfig();
            if (dbConfig != null)
            {
                break;
            }
            try
            {
                Thread.sleep(SLEEP_TIME);
            }
            catch (InterruptedException ex)
            {
                LOGGER.log(LogMessageCodes.FAIL_READ_DB_SETTING, SLEEP_TIME);
            }
        }
        // client modeの場合、設定ファイルのエージェントごとに、threadを作成する
        if ("client".equals(dbConfig.getConnectionMode()))
        {

            List<AgentSetting> agentSettings = dbConfig.getAgentSettingList();

            ConnectionClient connectionClient = ConnectionClient.getInstance();
            List<CommunicationClient> clientList = connectionClient.getClientList();
            for (int cnt = 0; cnt < agentSettings.size(); cnt++)
            {
                AgentSetting setting = agentSettings.get(cnt);
                // DataCollectorに接続する。
                // TODO 複数エージェント対応・エラーチェック
                String javelinHost = setting.acceptHost;
                int javelinPort = setting.acceptPort;
                int agentId = cnt + 1;
                String clientId = createClientId(javelinHost, javelinPort);

                CommunicationClient client =
                                             CommunicationFactory.getCommunicationClient("DataCollector-ClientThread-"
                                                     + clientId);

                client.init(javelinHost, javelinPort);
                client.addTelegramListener(new CollectorListener(messageSender_, agentId,
                                                                 setting.databaseName));
                client.addTelegramListener(new AlarmNotifyListener(messageSender_, agentId));
                client.addTelegramListener(new ResourceAlarmListener(messageSender_, agentId));
                client.addTelegramListener(new ResourceStateListener(messageSender_, agentId));

                ConnectNotifyData connectNotify = new ConnectNotifyData();
                connectNotify.setKind(ConnectNotifyData.KIND_CONTROLLER);
                connectNotify.setPurpose(ConnectNotifyData.PURPOSE_GET_RESOURCE);
                connectNotify.setDbName(setting.databaseName);

                client.connect(connectNotify);
                clientList.add(client);
            }
        }
        else if ("server".equals(dbConfig.getConnectionMode()))
        {
            ConnectionClient connectionClient = ConnectionClient.getInstance();
            List<CommunicationClient> clientList = connectionClient.getClientList();

            CommunicationClient client =
                                         CommunicationFactory.getCommunicationClient("DataCollector-JavelinNotify-Thread");
            client.init(dbConfig.getServerModeAgentSetting().acceptHost,
                        dbConfig.getServerModeAgentSetting().acceptPort);
            ConnectNotifyData connectNotify = new ConnectNotifyData();
            connectNotify.setKind(ConnectNotifyData.KIND_CONTROLLER);
            connectNotify.setPurpose(ConnectNotifyData.PURPOSE_GET_DATABASE);
            connectNotify.setDbName("noDatabase");
            client.addTelegramListener(new JavelinNotifyListener(messageSender_));

            client.connect(connectNotify);
            clientList.add(client);
        }
    }

    /**
     * 終了処理を行います。
     */
    public void destroy()
    {
        this.messageSender_.destroy();
        messageSender_.stop();
        messageSender_ = null;
    }

    /**
     * Process the given Comet event.
     *
     * @param event The Comet event that will be processed
     * @throws IOException 入出力例外が発生した場合
     * @throws ServletException サーブレット上で例外が発生した場合
     */
    public void event(CometEvent event)
        throws IOException,
            ServletException
    {
        try
        {
            HttpServletRequest request = event.getHttpServletRequest();
            HttpServletResponse response = event.getHttpServletResponse();

            if (event.getEventType() == CometEvent.EventType.BEGIN)
            {
                event.setTimeout(CONNECTION_TIMEOUT);

                if (LOGGER.isDebugEnabled())
                {
                    LOGGER.log(LogMessageCodes.SESSION_INFORMATION, "Begin",
                               request.getSession(true).getId());
                }
                ServletContext servletContext = getServletConfig().getServletContext();
                String charset = servletContext.getInitParameter(EventConstants.CHAR_SET_CODE);
                String context = EventConstants.JAVASCRIPT_SETTING_CODE + charset;
                response.setContentType(context);

                String clientId = request.getParameter(EventConstants.CLIENT_ID);
                if (clientId == null)
                {
                    LOGGER.log(LogMessageCodes.NO_CLIENT_ID);
                    return;
                }
                String eventId = request.getParameter(EventConstants.EVENT_ID);
                if (eventId == null)
                {
                    LOGGER.log(LogMessageCodes.UNKNOWN_EVENT_ID);
                    return;
                }
                if (String.valueOf(EventConstants.EVENT_STORE_RESPONSE).equals(eventId))
                {
                    this.messageSender_.addCometEvent(clientId, event);
                    this.messageSender_.notifyMessage();
                }
                else if (String.valueOf(EventConstants.EVENT_DELETE_RESPONSE).equals(eventId))
                {
                    Map<String, CometEventWrapper> eventMap = this.messageSender_.getEventMap();
                    synchronized (eventMap)
                    {
                        // IE対応 画面遷移時にサーバに残っている設定情報を削除する。
                        CometEventWrapper setCometEvent =
                                                          this.messageSender_.getCometEvent(clientId);
                        this.messageSender_.removeCometEvent(clientId, false);
                        if (setCometEvent != null)
                        {
                            setCometEvent.close();
                        }
                        event.close();
                    }
                }
            }
            else if (event.getEventType() == CometEvent.EventType.ERROR)
            {
                if (LOGGER.isDebugEnabled())
                {
                    LOGGER.log(LogMessageCodes.SESSION_INFORMATION, "Error",
                               request.getSession(true).getId());
                }
                String clientId = request.getParameter(EventConstants.CLIENT_ID);
                boolean deleteSettings = false;

                // タイムアウト時以外の異常終了時にはサーバの設定を削除する。
                if (event.getEventSubType() != CometEvent.EventSubType.TIMEOUT)
                {
                    deleteSettings = true;
                }

                if (clientId != null)
                {
                    this.messageSender_.removeCometEvent(clientId, deleteSettings);
                }
                else
                {
                    this.messageSender_.removeCometEvent(response, deleteSettings);
                }
                event.close();
            }
            else if (event.getEventType() == CometEvent.EventType.END)
            {
                if (LOGGER.isDebugEnabled())
                {
                    LOGGER.log(LogMessageCodes.SESSION_INFORMATION, "End",
                               request.getSession(true).getId());
                }
                Map<String, CometEventWrapper> eventMap = this.messageSender_.getEventMap();
                synchronized (eventMap)
                {
                    String clientId = request.getParameter(EventConstants.CLIENT_ID);
                    if (clientId != null)
                    {
                        this.messageSender_.removeCometEvent(clientId, false);
                    }
                    event.close();
                }
            }
        }
        catch (IllegalStateException ex)
        {
            LOGGER.log(LogMessageCodes.COMET_ERROR, ex);
        }
    }

    /**
     * ホスト名とポート番号からクライアント ID を生成します。<br />
     *
     * @param host ホスト名
     * @param port ポート番号
     * @return クライアント ID
     */
    public static String createClientId(final String host, final int port)
    {
        return host + ":" + port;
    }

}
