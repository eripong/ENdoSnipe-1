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
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.co.acroquest.endosnipe.common.Constants;
import jp.co.acroquest.endosnipe.common.jmx.JMXManager;
import jp.co.acroquest.endosnipe.common.logger.ENdoSnipeLogger;
import jp.co.acroquest.endosnipe.common.util.ResourceDataUtil;
import jp.co.acroquest.endosnipe.communicator.entity.MeasurementConstants;
import jp.co.acroquest.endosnipe.data.dao.MeasurementValueDao;
import jp.co.acroquest.endosnipe.data.dto.MeasurementValueDto;
import jp.co.acroquest.endosnipe.web.dashboard.config.MeasurementSetting;
import jp.co.acroquest.endosnipe.web.dashboard.constants.EventConstants;
import jp.co.acroquest.endosnipe.web.dashboard.constants.LogMessageCodes;
import jp.co.acroquest.endosnipe.web.dashboard.entity.MeasurementItemKey;
import jp.co.acroquest.endosnipe.web.dashboard.entity.TermMeasurementData;
import jp.co.acroquest.endosnipe.web.dashboard.entity.TermMeasurementDetailData;
import jp.co.acroquest.endosnipe.web.dashboard.entity.TermMeasurementEntity;
import jp.co.acroquest.endosnipe.web.dashboard.manager.DatabaseManager;
import jp.co.acroquest.endosnipe.web.dashboard.manager.EventManager;
import jp.co.acroquest.endosnipe.web.dashboard.util.EventUtil;
import jp.co.acroquest.endosnipe.web.dashboard.util.RequestUtil;
import jp.co.acroquest.endosnipe.web.dashboard.util.ResponseUtil;

/**
 * 指定した期間の計測情報を取得する処理クラスです。
 * @author fujii
 *
 */
public class TermMeasurementDataProcessor implements EventProcessor
{
    /** ロガー */
    private static final ENdoSnipeLogger      LOGGER          =
                                                                ENdoSnipeLogger.getLogger(TermMeasurementDataProcessor.class);

    /** 同時刻のデータを処理するためのサマリ時間 */
    private static final int                  SUMMARY_TIME    = 1000;

    /** SQLレスポンスを取り除く関数群 */
    public static final Set<String>           EXCLUSION_SQL_TYPES;

    /** SQLレスポンスのみ取り出す関数群 */
    public static final Set<String>           ONLY_SQL_TYPES;

    /** SQLレスポンスのみ取り出す関数群 */
    public static final Map<String, String> MEASUREMENT_TYPE_RELATION;

    /** 測定値の表示名 */
    public static final Map<String, String>  MEASUREMENT_TYPE_DISPLAY_NAME;

    /** 測定値の項目ID */
    public static final Map<String, String>  MEASUREMENT_TYPE_ITEM_NAME;

    /** SQLレスポンスかどうかを判断する文字列 */
    public static final String                SQL_TYPE_STRING = "jdbc";

    /** グラフの系列に値がない場合に設定する空文字 */
    private static final String               EMPTY_VALUE     = "";

    /** SQLを除く項目名かどうかを判定する文字列。 */
    private static final String ITEMNAME_FRAGMENT_EXCL_SQL = "/nosql/";

    /** SQLのみにする項目名かどうかを判定する文字列。 */
    private static final String ITEMNAME_FRAGMENT_ONLY_SQL = "/sql/";

    /** 汎用JMX項目かどうかを判定する文字列。 */
    private static final CharSequence ITEMNAME_FRAGMENT_JMX = "/jmx/";

    static
    {
        //SQLを排除する関数
        EXCLUSION_SQL_TYPES = new HashSet<String>();
        EXCLUSION_SQL_TYPES.add(Constants.ITEMNAME_PROCESS_RESPONSE_TOTAL_COUNT_EXCL_SQL);
        EXCLUSION_SQL_TYPES.add(Constants.ITEMNAME_PROCESS_RESPONSE_TIME_AVERAGE_EXCL_SQL);

        //SQLのみを取得する関数
        ONLY_SQL_TYPES = new HashSet<String>();
        ONLY_SQL_TYPES.add(Constants.ITEMNAME_PROCESS_RESPONSE_TOTAL_COUNT_ONLY_SQL);
        ONLY_SQL_TYPES.add(Constants.ITEMNAME_PROCESS_RESPONSE_TIME_AVERAGE_ONLY_SQL);

        //別の値を参照する必要がある測定項目を格納
        MEASUREMENT_TYPE_RELATION = new HashMap<String, String>();
        MEASUREMENT_TYPE_RELATION.put(Constants.ITEMNAME_PROCESS_RESPONSE_TOTAL_COUNT_EXCL_SQL,
                                      Constants.ITEMNAME_PROCESS_RESPONSE_TOTAL_COUNT);
        MEASUREMENT_TYPE_RELATION.put(Constants.ITEMNAME_PROCESS_RESPONSE_TOTAL_COUNT_ONLY_SQL,
                                      Constants.ITEMNAME_PROCESS_RESPONSE_TOTAL_COUNT);
        MEASUREMENT_TYPE_RELATION.put(Constants.ITEMNAME_PROCESS_RESPONSE_TIME_AVERAGE_EXCL_SQL,
                                      Constants.ITEMNAME_PROCESS_RESPONSE_TIME_AVERAGE);
        MEASUREMENT_TYPE_RELATION.put(Constants.ITEMNAME_PROCESS_RESPONSE_TIME_AVERAGE_ONLY_SQL,
                                      Constants.ITEMNAME_PROCESS_RESPONSE_TIME_AVERAGE);
        MEASUREMENT_TYPE_RELATION.put(Constants.ITEMNAME_SYSTEM_MEMORY_PHYSICAL_USED,
                                      Constants.ITEMNAME_SYSTEM_MEMORY_PHYSICAL_MAX);
        //測定項目名称
        MEASUREMENT_TYPE_DISPLAY_NAME = new HashMap<String, String>();
        
        // * MeasurementConstants.TYPE_PROC_RES_TOTAL_COUNT_EXCLUSION_SQL
        MEASUREMENT_TYPE_DISPLAY_NAME.put(Constants.ITEMNAME_PROCESS_RESPONSE_TOTAL_COUNT_EXCL_SQL,
                                          "レスポンス回数(sqlを除く)");
        
        // * MeasurementConstants.TYPE_PROC_RES_TOTAL_COUNT_ONLY_SQL
        MEASUREMENT_TYPE_DISPLAY_NAME.put(Constants.ITEMNAME_PROCESS_RESPONSE_TOTAL_COUNT_ONLY_SQL,
                                          "レスポンス回数(sqlのみ)");
        // * MeasurementConstants.TYPE_PROC_RES_TIME_AVERAGE_EXCLUSION_SQL
        MEASUREMENT_TYPE_DISPLAY_NAME.put(Constants.ITEMNAME_PROCESS_RESPONSE_TIME_AVERAGE_EXCL_SQL,
                                          "レスポンス時間(sqlを除く)");
        // * MeasurementConstants.TYPE_PROC_RES_TIME_AVERAGE_ONLY_SQL
        MEASUREMENT_TYPE_DISPLAY_NAME.put(Constants.ITEMNAME_PROCESS_RESPONSE_TIME_AVERAGE_ONLY_SQL,
                                          "レスポンス時間(sqlのみ)");
        // * MeasurementConstants.TYPE_SYS_PHYSICALMEM_USED
        MEASUREMENT_TYPE_DISPLAY_NAME.put(Constants.ITEMNAME_SYSTEM_MEMORY_PHYSICAL_USED,
                                          "システム全体の使用メモリ");

        MEASUREMENT_TYPE_ITEM_NAME = new HashMap<String, String>();
        MEASUREMENT_TYPE_ITEM_NAME.put(Constants.ITEMNAME_PROCESS_RESPONSE_TOTAL_COUNT_EXCL_SQL,
                                       "process.response.total.count.Exclusion.sql");
        MEASUREMENT_TYPE_ITEM_NAME.put(Constants.ITEMNAME_PROCESS_RESPONSE_TOTAL_COUNT_ONLY_SQL,
                                       "process.response.total.count.Only.sql");
        MEASUREMENT_TYPE_ITEM_NAME.put(Constants.ITEMNAME_PROCESS_RESPONSE_TIME_AVERAGE_EXCL_SQL,
                                       "process.response.time.average.Exclusion.sql");
        MEASUREMENT_TYPE_ITEM_NAME.put(Constants.ITEMNAME_PROCESS_RESPONSE_TIME_AVERAGE_ONLY_SQL,
                                       "process.response.time.average.Only.sql");
        MEASUREMENT_TYPE_ITEM_NAME.put(Constants.ITEMNAME_SYSTEM_MEMORY_PHYSICAL_USED,
                                       "system.memory.physical.used");
    }

    /**
     * 指定した期間の計測情報を要求を処理します。
     * @param request {@link HttpServletRequest}オブジェクト
     * @param response {@link HttpServletResponse}オブジェクト
     */
    public void process(HttpServletRequest request, HttpServletResponse response)
    {
        String graphIdStr = request.getParameter(EventConstants.GRAPH_ID);
        String spanStr = request.getParameter(EventConstants.SPAN);
        String clientId = request.getParameter(EventConstants.CLIENT_ID);
        String agentIds = request.getParameter(EventConstants.AGENT_IDS);
        String measurementTypes = request.getParameter(EventConstants.MEASUREMENT_TYPES);
        String itemName = request.getParameter(EventConstants.ITEM_NAME);

        if (graphIdStr == null)
        {
            LOGGER.log(LogMessageCodes.UNKNOWN_GRAPH_ID);
            return;
        }
        if (spanStr == null)
        {
            LOGGER.log(LogMessageCodes.UNKNOWN_SPAN);
            return;
        }
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

        Integer graphId = null;
        try
        {
            graphId = Integer.valueOf(graphIdStr);
        }
        catch (NumberFormatException ex)
        {
            LOGGER.log(LogMessageCodes.UNKNOWN_GRAPH_ID, graphId);
            return;
        }

        Date[] spanArray = RequestUtil.getSpanList(spanStr);
        if (spanArray == null || spanArray.length == 0)
        {
            LOGGER.log(LogMessageCodes.UNKNOWN_SPAN, spanStr);
            return;
        }

        //測定すべき項目を格納したMapを作成する。
        Map<Integer, Map<Integer, String>> graphMap = null;

        if (measurementTypes == null || "".equals(measurementTypes))
        {
            graphMap = getRegistGraphMap(clientId, graphId);
        }
        else
        {
            graphMap = createGraphMap(graphId, measurementTypes, itemName);
        }
        if (graphMap == null)
        {
            return;
        }

        TermMeasurementEntity entity = new TermMeasurementEntity();
        entity.event_id = EventConstants.EVENT_NOTIFY_TERM_MEASUREMENT_RESPONSE;
        entity.graph_id = graphId;
        entity.measurement_data = new ArrayList<TermMeasurementData>();

        Map<Long, Integer> timestampMap = new LinkedHashMap<Long, Integer>();

        // 検索
        Timestamp startTime = new Timestamp(spanArray[0].getTime());
        Timestamp endTime = new Timestamp(spanArray[1].getTime());

        DatabaseManager dbMmanager = DatabaseManager.getInstance();

        for (Entry<Integer, Map<Integer, String>> agentEntry : graphMap.entrySet())
        {
            // 1つのエージェントIDのみに対応。
            Integer agentId = agentIdList.get(0);
            String dbName = dbMmanager.getDataBaseName(agentId.intValue());
            if (dbName == null)
            {
                LOGGER.log(LogMessageCodes.FAIL_READ_DB_NAME);
                return;
            }
            TermMeasurementData measurementData = new TermMeasurementData();
            measurementData.agent_id = agentId;
            measurementData.measurement_items = new ArrayList<TermMeasurementDetailData>();

            Set<Integer> measurementTypeSet = agentEntry.getValue().keySet();
            List<MeasurementValueDto> allValueList = new ArrayList<MeasurementValueDto>();
            List<Long> timestampList = new ArrayList<Long>();
            for (Integer measurementType : measurementTypeSet)
            {
                List<MeasurementValueDto> measurementValueList =
                                                                 getMeasurementValueList(
                                                                                         dbName,
                                                                                         startTime,
                                                                                         endTime,
                                                                                         measurementType,
                                                                                         itemName);
                allValueList.addAll(measurementValueList);
                addTimeStamp(measurementValueList, timestampList);
            }

            Collections.sort(timestampList);
            setTimeStamp(timestampMap, timestampList);

            setDetailData(measurementData, allValueList, timestampMap);
            entity.measurement_data.add(measurementData);
        }

        List<Date> timeStampList = getTimeStampList(timestampMap);
        entity.timestamps = timeStampList;

        ResponseUtil.sendMessageOfJSONCode(response, entity, clientId);
    }

    /**
     * dbから測定データを取得し、Listとして返す
     *
     * @param dbName db名
     * @param startTime 取得するデータの開始時間
     * @param endTime 取得するデータの終了時間
     * @param measurementType 測定項目
     * @param measurementItemName 項目名
     * @return 測定結果を格納したList
     */
    private List<MeasurementValueDto> getMeasurementValueList(String dbName, Timestamp startTime,
            Timestamp endTime, Integer measurementType, String measurementItemName)
    {
        try
        {
            //別途データから値を作成するmeasurementItemNameを取得する。
            String tmpMesurementItemName = MEASUREMENT_TYPE_RELATION.get(measurementItemName);
            String trueMeasurementItemName = measurementItemName;
            if (tmpMesurementItemName != null)
            {
                measurementItemName= tmpMesurementItemName;
            }

            List<MeasurementValueDto> measurementValueList = null;
            if (measurementItemName.contains(ITEMNAME_FRAGMENT_JMX))
            {
                measurementValueList =
                                       MeasurementValueDao.selectByTermAndMeasurementItemName(
                                                                                          dbName,
                                                                                          startTime,
                                                                                          endTime,
                                                                                          measurementItemName);
            }
            else
            {
                measurementValueList =
                                       MeasurementValueDao.selectByTermAndJMXItemName(dbName,
                                                                                      startTime,
                                                                                      endTime,
                                                                                      measurementItemName);
            }

            //measurementTypeを元に戻す。
            if (tmpMesurementItemName != null)
            {
                List<MeasurementValueDto> sysPhysicMemFreeDtoList = null;
                if (trueMeasurementItemName.contains(Constants.ITEMNAME_SYSTEM_MEMORY_PHYSICAL_USED))
                {
                    //物理メモリ空き容量取得
                    sysPhysicMemFreeDtoList =
                                              MeasurementValueDao.selectByTermAndMeasurementItemName(
                                                                                                 dbName,
                                                                                                 startTime,
                                                                                                 endTime,
                                                                                                 Constants.ITEMNAME_SYSTEM_MEMORY_PHYSICAL_FREE);
                }
                //空きメモリ使用量から物理メモリ使用量を計算する。
                measurementValueList =
                                       EventUtil.confirmMeasurementValueList(trueMeasurementItemName,
                                                                             measurementValueList,
                                                                             sysPhysicMemFreeDtoList);
            }
            return measurementValueList;
        }
        catch (SQLException ex)
        {
            LOGGER.log(LogMessageCodes.SQL_EXCEPTION);
            return new ArrayList<MeasurementValueDto>();
        }
    }

    /**
     * マップをリストに変換
     *
     * @param timeStampMap
     * @return
     */
    private List<Date> getTimeStampList(Map<Long, Integer> timeStampMap)
    {
        List<Date> timeStampList = new ArrayList<Date>();
        Set<Long> tmpTime = timeStampMap.keySet();
        for (Long time : tmpTime)
        {
            timeStampList.add(new Date(time.longValue() * SUMMARY_TIME));
        }
        return timeStampList;
    }

    /**
     * 期間データを取得する処理を行う。
     *
     * @param clientId クライアントID
     * @param graphId グラフID
     * @return 期間データを格納したMap
     */
    private Map<Integer, Map<Integer, String>> getRegistGraphMap(String clientId, Integer graphId)
    {
        // 期間データ取得時には自動計測通知を終了する。
        EventManager manager = EventManager.getInstance();
        MeasurementSetting setting = manager.getMeasurementSettings(clientId);
        if (setting == null)
        {
            LOGGER.log(LogMessageCodes.NO_MEASUREMENT_SETTING);
            return null;
        }
        setting.setAutoNotify(graphId, false);
        Map<Integer, Map<Integer, String>> graphMap = setting.getAgentMap(graphId);
        if (graphMap == null)
        {
            LOGGER.log(LogMessageCodes.NO_MEASUREMENT_SETTING);
            return null;
        }
        return graphMap;
    }

    /**
     * グラフIDと測定対象IDから、データを取得すべき測定対象IDを格納したMapを作成する。
     *
     * @param graphId グラフのID
     * @param measurementTypes 測定対象ID
     * @return　測定対象IDを格納したMap
     */
    private Map<Integer, Map<Integer, String>> createGraphMap(Integer graphId,
            String measurementTypes, String itemName)
    {
        List<Integer> measurementList = RequestUtil.getMeasurementTypeList(measurementTypes);
        Map<Integer, Map<Integer, String>> graphMap = new HashMap<Integer, Map<Integer, String>>();
        Map<Integer, String> graphSet = new TreeMap<Integer, String>();
        for (Integer measurementType : measurementList)
        {
            graphSet.put(measurementType, itemName);
        }
        graphMap.put(graphId, graphSet);
        return graphMap;
    }

    /**
     * 各系列の最新時刻を設定します。
     * @param measurementValueList
     * @param timeStampMap 各系列の時刻を保存したマップ
     * @param itemIdSet 計測項目詳細IDのセット
     */
    private void addTimeStamp(List<MeasurementValueDto> measurementValueList,
            List<Long> timeStampList)
    {
        // 複数計測項目がある場合には、最も新しい時刻を設定する。
        for (int cnt = 0; cnt < measurementValueList.size(); cnt++)
        {
            MeasurementValueDto measurementValue = measurementValueList.get(cnt);
            timeStampList.add(measurementValue.measurementTime.getTime());
        }
    }

    /**
     * 各系列の最新時刻を設定します。
     * @param measurementValueList
     * @param timeStampMap 各系列の時刻を保存したマップ
     * @param itemIdSet 計測項目詳細IDのセット
     */
    private void setTimeStamp(Map<Long, Integer> timeStampMap, List<Long> timestampList)
    {
        // 複数計測項目がある場合には、最も新しい時刻を設定する。
        for (Long timestamp : timestampList)
        {
            Long compareTime = Long.valueOf(timestamp / SUMMARY_TIME);
            boolean contains = timeStampMap.containsKey(compareTime);
            if (contains == false)
            {
                Integer timestampId = timeStampMap.size();
                timeStampMap.put(compareTime, timestampId);
            }
        }
    }

    /**
     * 各系列の計測情報を設定する。
     * @param measurementData {@link TermMeasurementData}オブジェクト
     * @param measurementValueList {@link MeasurementValueDto}のリストオブジェクト
     * @param timestampMap 時刻のマップ
     */
    private void setDetailData(TermMeasurementData measurementData,
            List<MeasurementValueDto> measurementValueList, Map<Long, Integer> timestampMap)
    {
        Map<String, TermMeasurementDetailData> measurementDetailMap =
            new HashMap<String, TermMeasurementDetailData>();
        
        for (MeasurementValueDto measurementValue : measurementValueList)
        {
            int measurementId = measurementValue.measurementItemId;
            String measurementItemName = measurementValue.measurementItemName;
            Date measuementTime = measurementValue.measurementTime;
            if (measurementItemName == null)
            {
                measurementItemName = "";
            }

            // SQLを取り除くかどうかを判断し取捨選択する。
            if (EXCLUSION_SQL_TYPES.contains(measurementItemName))
            {
                if (measurementItemName.indexOf(SQL_TYPE_STRING) >= 0)
                {
                    continue;
                }
            }

            if (ONLY_SQL_TYPES.contains(measurementItemName))
            {
                if (measurementItemName.indexOf(SQL_TYPE_STRING) == -1)
                {
                    continue;
                }
            }

            // 値を取得
            TermMeasurementDetailData detailData = measurementDetailMap.get(measurementItemName);
            if (detailData == null)
            {

                detailData = new TermMeasurementDetailData();
                detailData.measurement_id = measurementId;
                detailData.measurement_values = new ArrayList<String>();
                detailData.item_name = measurementItemName;

                measurementData.measurement_items.add(detailData);
                measurementDetailMap.put(measurementItemName, detailData);
            }
            String addValue = null;

            if (Constants.ITEMNAME_SYSTEM_CPU_TOTAL_USAGE.equals(measurementValue.measurementItemName)
                || Constants.ITEMNAME_SYSTEM_CPU_SYSTEM_USAGE.equals(measurementValue.measurementItemName)
                || Constants.ITEMNAME_PROCESS_CPU_TOTAL_USAGE.equals(measurementValue.measurementItemName)
                || Constants.ITEMNAME_PROCESS_CPU_SYSTEM_USAGE.equals(measurementValue.measurementItemName))
            {
                addValue =
                           String.valueOf(Double.valueOf(measurementValue.value).doubleValue()
                                   / ResourceDataUtil.PERCENTAGE_DATA_MAGNIFICATION);
            }
            // jmxにおける処理。
            else if (measurementItemName.contains(ITEMNAME_FRAGMENT_JMX)) 
            {
                // HitRatioを検出する。
                boolean isRatio = false;
                for (String ratioName : JMXManager.JMX_RATIO_ITEMNAME_ARRAY) {
                    if (measurementItemName == null) {
                        break;
                    }
                    if (!measurementItemName.contains(ratioName)) {
                        continue;
                    }
                    isRatio = true;
                    break;
                }
                
                if (isRatio) {
                    addValue =
                        String.valueOf(Double.valueOf(measurementValue.value).doubleValue()
                                / ResourceDataUtil.PERCENTAGE_DATA_MAGNIFICATION);
                } else {
                    addValue = String.valueOf(measurementValue.value);
                }   
            }
            else
            {
                addValue = String.valueOf(measurementValue.value);
            }
            int addPosition = timestampMap.get(measuementTime.getTime() / SUMMARY_TIME);
            int startPosition = detailData.measurement_values.size();
            for (int position = startPosition; position < addPosition; position++)
            {
                detailData.measurement_values.add(EMPTY_VALUE);
            }
            detailData.measurement_values.add(addValue);
        }

        // リストのサイズを合わせる。
        int maxValueSize = 0;
        List<TermMeasurementDetailData> detailDataList = measurementData.measurement_items;
        for (TermMeasurementDetailData detailData : detailDataList)
        {
            int valueSize = detailData.measurement_values.size();
            if (maxValueSize < valueSize)
            {
                maxValueSize = valueSize;
            }
        }

        // 空文字を設定し、すべてのリストのサイズを合わせる。
        for (TermMeasurementDetailData detailData : detailDataList)
        {
            int valueSize = detailData.measurement_values.size();
            for (int cnt = valueSize; cnt < maxValueSize; cnt++)
            {
                detailData.measurement_values.add(EMPTY_VALUE);
            }
        }

    }
}
