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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.co.acroquest.endosnipe.common.logger.ENdoSnipeLogger;
import jp.co.acroquest.endosnipe.data.dao.MeasurementInfoDao;
import jp.co.acroquest.endosnipe.data.db.DBManager;
import jp.co.acroquest.endosnipe.data.db.DatabaseType;
import jp.co.acroquest.endosnipe.web.dashboard.config.AgentSetting;
import jp.co.acroquest.endosnipe.web.dashboard.config.ConfigurationReader;
import jp.co.acroquest.endosnipe.web.dashboard.config.DataBaseConfig;
import jp.co.acroquest.endosnipe.web.dashboard.constants.EventConstants;
import jp.co.acroquest.endosnipe.web.dashboard.constants.LogMessageCodes;
import jp.co.acroquest.endosnipe.web.dashboard.exception.InitializeException;
import jp.co.acroquest.endosnipe.web.dashboard.manager.DatabaseManager;
import jp.co.acroquest.endosnipe.web.dashboard.service.processor.AgentInformationProcessor;
import jp.co.acroquest.endosnipe.web.dashboard.service.processor.AgentNotifyProcessor;
import jp.co.acroquest.endosnipe.web.dashboard.service.processor.AlarmNotifyStartProcessor;
import jp.co.acroquest.endosnipe.web.dashboard.service.processor.AlarmNotifyStopProcessor;
import jp.co.acroquest.endosnipe.web.dashboard.service.processor.AutoMeasurementEndProcessor;
import jp.co.acroquest.endosnipe.web.dashboard.service.processor.AutoMeasurementStartProcessor;
import jp.co.acroquest.endosnipe.web.dashboard.service.processor.AutoMeasurementStopProcessor;
import jp.co.acroquest.endosnipe.web.dashboard.service.processor.EventProcessor;
import jp.co.acroquest.endosnipe.web.dashboard.service.processor.GetReportListRequestProcessor;
import jp.co.acroquest.endosnipe.web.dashboard.service.processor.ResourceAlarmStartProcessor;
import jp.co.acroquest.endosnipe.web.dashboard.service.processor.ResourceAlarmStopProcessor;
import jp.co.acroquest.endosnipe.web.dashboard.service.processor.ResourceStateAllProcessor;
import jp.co.acroquest.endosnipe.web.dashboard.service.processor.TermAlarmNotifyProcessor;
import jp.co.acroquest.endosnipe.web.dashboard.service.processor.TermMeasurementDataProcessor;

/**
 * クライアントから通知要求を受信するためのサーブレットです。
 * @author fujii
 *
 */
public class DashBoardNotifyServlet extends HttpServlet
{
    /** シリアルID */
    private static final long            serialVersionUID = -6688090852275089760L;

    /** ロガー */
    private static final ENdoSnipeLogger LOGGER           =
                                                            ENdoSnipeLogger.getLogger(DashBoardNotifyServlet.class);

    /** イベント処理クラスのMap */
    private Map<Integer, EventProcessor> processorMap_    = new HashMap<Integer, EventProcessor>();

    /**
     * {@inheritDoc}
     */
    public void init()
        throws ServletException
    {
        ServletConfig servletConfig = getServletConfig();
        String filePath = servletConfig.getInitParameter("collector.property");

        this.processorMap_.put(Integer.valueOf(EventConstants.EVENT_START_AUTO_MEASUREMENT),
                               new AutoMeasurementStartProcessor());
        this.processorMap_.put(Integer.valueOf(EventConstants.EVENT_STOP_AUTO_MEASUREMENT),
                               new AutoMeasurementStopProcessor());
        this.processorMap_.put(Integer.valueOf(EventConstants.EVENT_END_AUTO_MEASUREMENT),
                               new AutoMeasurementEndProcessor());
        this.processorMap_.put(Integer.valueOf(EventConstants.EVENT_NOTIFY_TERM_MEASUREMENT_REQUEST),
                               new TermMeasurementDataProcessor());
        this.processorMap_.put(Integer.valueOf(EventConstants.EVENT_START_ALARM_NOTIFY),
                               new AlarmNotifyStartProcessor());
        this.processorMap_.put(Integer.valueOf(EventConstants.EVENT_STOP_ALARM_NOTIFY),
                               new AlarmNotifyStopProcessor());
        this.processorMap_.put(Integer.valueOf(EventConstants.EVENT_TERM_NOTIFY_ALARM_REQUEST),
                               new TermAlarmNotifyProcessor());
        this.processorMap_.put(Integer.valueOf(EventConstants.EVENT_START_RESOURCE_ALARM),
                               new ResourceAlarmStartProcessor());
        this.processorMap_.put(Integer.valueOf(EventConstants.EVENT_STOP_RESOURCE_ALARM),
                               new ResourceAlarmStopProcessor());
        this.processorMap_.put(Integer.valueOf(EventConstants.EVENT_RESOURCE_STATE_ALL_REQUEST),
                               new ResourceStateAllProcessor());
        this.processorMap_.put(Integer.valueOf(EventConstants.EVENT_AGENT_LIST_REQUEST),
                               new AgentInformationProcessor());
        this.processorMap_.put(Integer.valueOf(EventConstants.EVENT_GET_AGENT_INFO),
                               new AgentNotifyProcessor());

        // レポート一覧を取得するイベントの処理
        // TODO Report画面用のServletを別に用意したほうがよいか、検討すること
        String reportDir = servletConfig.getInitParameter("report.directory");
        GetReportListRequestProcessor getReportListReqEvProc = new GetReportListRequestProcessor();
        getReportListReqEvProc.setReportDir(reportDir);
        this.processorMap_.put(Integer.valueOf(EventConstants.EVENT_REPORT_LIST_REQUEST),
                               getReportListReqEvProc);

        DataBaseConfig dbConfig = null;
        dbConfig = loadConfig(filePath);
        if (dbConfig == null)
        {
            return;
        }
        // DBの初期化
        setDatabase(dbConfig);

        DatabaseManager manager = DatabaseManager.getInstance();
        manager.setDataBaseConfig(dbConfig);
        
        // コールバック関数を作成するために、初期化メソッドを呼び出す。
        MeasurementInfoDao.initialize();
    }

    /**
     * データベースの初期設定を行う。
     * @param config {@link DataBaseConfig}オブジェクト
     */
    private void setDatabase(DataBaseConfig config)
    {

        String host = config.getDatabaseHost();

        String port = config.getDatabasePort();

        String userName = config.getDatabaseUserName();

        String password = config.getDatabasePassword();

        String dbDir = config.getBaseDir();

        DatabaseType dbType = config.getDatabaseType();

        boolean useDefaultDb = true;
        if (dbType.getId().equals("postgres"))
        {
            useDefaultDb = false;
        }

        DBManager.updateSettings(useDefaultDb, dbDir, host, port, userName, password);
    }

    /**
     * {@inheritDoc}
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response)
    {
        doRequest(request, response);
    }

    /**
     * {@inheritDoc}
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response)
    {
        doRequest(request, response);
    }

    /**
     * クライアントから通知要求を受信するためのサーブレットです。
     * @param request {@link HttpServletRequest}オブジェクト
     * @param response {@link HttpServletResponse}オブジェクト
     */
    public void doRequest(HttpServletRequest request, HttpServletResponse response)
    {
        String eventId = request.getParameter(EventConstants.EVENT_ID);
        if (eventId == null)
        {
            LOGGER.log(LogMessageCodes.UNKNOWN_EVENT_ID);
            return;
        }
        try
        {
            Integer eventIdInt = Integer.valueOf(eventId);
            EventProcessor processor = this.processorMap_.get(eventIdInt);
            if (processor == null)
            {
                LOGGER.log(LogMessageCodes.UNKNOWN_EVENT_ID, eventId);
                return;
            }
            //char setの設定を行う。
            ServletContext servletContext = getServletConfig().getServletContext();
            String charset = servletContext.getInitParameter(EventConstants.CHAR_SET_CODE);
            String context = EventConstants.HTML_SETTING_CODE + charset;
            response.setContentType(context);
            processor.process(request, response);
        }
        catch (NumberFormatException ex)
        {
            LOGGER.log(LogMessageCodes.UNKNOWN_EVENT_ID, eventId);
        }

    }

    /**
     * 設定ファイルを読み込みます。
     * @param filePath ファイルパス
     * @return {@link DataBaseConfig}オブジェクト
     */
    private DataBaseConfig loadConfig(String filePath)
    {
        DataBaseConfig config = null;
        try
        {
            config = ConfigurationReader.load(filePath);
        }
        catch (InitializeException ex)
        {
            LOGGER.log(LogMessageCodes.CANNOT_FIND_PROPERTY, filePath);
            return null;
        }
        catch (IOException ex)
        {
            LOGGER.log(LogMessageCodes.CANNOT_FIND_PROPERTY, filePath);
            return null;
        }
            if ("client".equals(config.getConnectionMode()))
        {
            List<AgentSetting> agentList = config.getAgentSettingList();
            if (agentList == null || agentList.size() == 0)
            {
                LOGGER.log(LogMessageCodes.CANNOT_FIND_HOST, filePath);
                return null;
            }
        }
            
        return config;
    }
}
