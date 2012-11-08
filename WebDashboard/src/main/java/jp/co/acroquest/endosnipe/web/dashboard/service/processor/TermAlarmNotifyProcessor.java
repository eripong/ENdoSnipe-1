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
package jp.co.acroquest.endosnipe.web.dashboard.service.processor;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.co.acroquest.endosnipe.common.logger.ENdoSnipeLogger;
import jp.co.acroquest.endosnipe.data.dao.JavelinLogDao;
import jp.co.acroquest.endosnipe.data.entity.JavelinLog;
import jp.co.acroquest.endosnipe.perfdoctor.WarningUnit;
import jp.co.acroquest.endosnipe.web.dashboard.constants.EventConstants;
import jp.co.acroquest.endosnipe.web.dashboard.constants.LogMessageCodes;
import jp.co.acroquest.endosnipe.web.dashboard.entity.AlarmNotifyEntity;
import jp.co.acroquest.endosnipe.web.dashboard.entity.comparator.AlarmEntityComparator;
import jp.co.acroquest.endosnipe.web.dashboard.manager.DatabaseManager;
import jp.co.acroquest.endosnipe.web.dashboard.service.JvnFileEntryJudge;
import jp.co.acroquest.endosnipe.web.dashboard.util.DaoUtil;
import jp.co.acroquest.endosnipe.web.dashboard.util.EventUtil;
import jp.co.acroquest.endosnipe.web.dashboard.util.RequestUtil;
import jp.co.acroquest.endosnipe.web.dashboard.util.ResponseUtil;

/**
 * 指定した期間のJvnファイルを取得し、計測情報として変換するクラスです。
 * @author fujii
 *
 */
public class TermAlarmNotifyProcessor implements EventProcessor
{
    /** ロガー */
    private static final ENdoSnipeLogger LOGGER =
            ENdoSnipeLogger.getLogger(TermAlarmNotifyProcessor.class);

    /**
     * 指定した期間Jvnファイルを取得し、計測情報として変換する処理をします。
     * @param request {@link HttpServletRequest}オブジェクト
     * @param response {@link HttpServletResponse}オブジェクト
     */
    public void process(HttpServletRequest request, HttpServletResponse response)
    {
        String clientId = request.getParameter(EventConstants.CLIENT_ID);
        String agentIds = request.getParameter(EventConstants.AGENT_IDS);
        String alarmLevelStr = request.getParameter(EventConstants.ALARM_LEVEL);
        String spanStr = request.getParameter(EventConstants.SPAN);
        String alarmCntStr = request.getParameter(EventConstants.ALARM_COUNT);

        if (clientId == null)
        {
            LOGGER.log(LogMessageCodes.NO_CLIENT_ID);
            return;
        }
        List<Integer> agentIdList = RequestUtil.getAgentIdList(agentIds);
        if (agentIdList == null || agentIdList.size() == 0)
        {
            LOGGER.log(LogMessageCodes.UNKNOWN_AGENT_ID, agentIds);
            return;
        }
        int alarmLevel = EventUtil.getAlarmLevel(alarmLevelStr);
        int alarmCount = EventUtil.getAlarmCount(alarmCntStr);

        Date[] spanArray = RequestUtil.getSpanList(spanStr);
        if (spanArray == null || spanArray.length == 0)
        {
            LOGGER.log(LogMessageCodes.UNKNOWN_SPAN, spanStr);
            return;
        }

        if (spanStr == null)
        {
            LOGGER.log(LogMessageCodes.UNKNOWN_SPAN);
            return;
        }

        // 検索
        Timestamp startTime = new Timestamp(spanArray[0].getTime());
        Timestamp endTime = new Timestamp(spanArray[1].getTime());
        DatabaseManager dbMmanager = DatabaseManager.getInstance();

        JvnFileEntryJudge judge = new JvnFileEntryJudge();
        List<AlarmNotifyEntity> entityList = new ArrayList<AlarmNotifyEntity>();
        List<WarningUnit> warningUnitList = new ArrayList<WarningUnit>();

        for (Integer agentId : agentIdList)
        {
            try
            {
                String dbName = dbMmanager.getDataBaseName(agentId.intValue());
                if (dbName == null)
                {
                    LOGGER.log(LogMessageCodes.FAIL_READ_DB_NAME);
                    continue;
                }
                List<JavelinLog> jvnLogList =
                                              JavelinLogDao.selectByTermWithLog(dbName, startTime,
                                                                                endTime);

                warningUnitList.addAll(judge.judge(jvnLogList, true, true));
                createAlarmEntity(warningUnitList, alarmLevel, agentId.intValue(), entityList);
            }
            catch (SQLException ex)
            {
                LOGGER.log(LogMessageCodes.SQL_EXCEPTION);
                return;
            }
        }
        Collections.sort(entityList, new AlarmEntityComparator());

        if (entityList.size() >= alarmCount)
        {
            entityList = entityList.subList(0, alarmCount);
        }
        if (entityList != null && entityList.size() > 0)
        {
            ResponseUtil.sendMessageOfJSONCode(response, entityList, clientId);
        }
    }

    /**
     * PerformanceDoctorの結果より、{@link AlarmNotifyEntity}のリストを作成します。
     * @param warningUnitList パフォーマンスドクターの結果
     * @param alarmLevel アラームのレベル
     * @param agentId エージェントID
     * @param entityList {@link AlarmNotifyEntity}のリスト
     */
    private void createAlarmEntity(List<WarningUnit> warningUnitList, int alarmLevel, int agentId,
            List<AlarmNotifyEntity> entityList)
    {
        for (WarningUnit unit : warningUnitList)
        {
            String level = unit.getLevel();
            if (EventUtil.compareLevel(level, alarmLevel) == false)
            {
                continue;
            }

            int eventId = EventConstants.EVENT_TERM_NOTIFY_ALARM_RESPONSE;

            AlarmNotifyEntity alarmNotifyEntity =
                                                  DaoUtil.createAlarmEntity(agentId, unit, level,
                                                                            eventId);
            entityList.add(alarmNotifyEntity);
        }
    }

}
